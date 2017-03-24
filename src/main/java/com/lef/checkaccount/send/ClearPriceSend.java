package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.tran.SettlePriceRequest;
import com.blockchain.service.tran.TranResponse;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.utils.CodeUtil;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 结算价推送
 * 
 * @author lihongsong
 *
 */
@Component
public class ClearPriceSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(ClearPriceSend.class);
	@Resource
	CodeUtil codeUtil;

	public void send(String dayStr, String batchNo) {
		List<SettlePriceRequest> list = null;
		try {
			list = findFromDb(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			for (SettlePriceRequest request : list) {
				TranResponse response = null;
				String errorMsg = null;
				try {
					response = txnServiceClient.syncSettlePrice(request);
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						logger.info("send second " + response);
						response = txnServiceClient.syncSettlePrice(request);
					}
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						throw new AnalysisException(Constants.send_data_tohessian_error_code,
								Constants.send_data_tohessian_error_msg);
					}
				} catch (Exception e) {
					errorMsg = Constants.send_data_tohessian_error_msg;
					logger.error(e);
					throw new AnalysisException(Constants.send_data_tohessian_error_code,
							Constants.send_data_tohessian_error_msg, e);
				} finally {
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						updateSendResult(dayStr, batchNo, request, response, errorMsg);
					}
				}
			}
		}
	}

	public List<SettlePriceRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from clearprice where  analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { dayStr, batchNo }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				SettlePriceRequest request = new SettlePriceRequest();
				request.setInitDate(rs.getString("init_date"));
				request.setExchangeId(rs.getString("exchange_id"));
				request.setExchangeMarketType(rs.getString("exchange_market_type"));
				request.setMoneyType(rs.getString("money_type"));
				request.setProductCategoryId(rs.getString("product_category_id"));
				request.setProductCode(rs.getString("product_code"));
				request.setSettlePrice(NumberUtil.getDoubleFromStr(rs.getString("settle_price")));
				return request;
			}
		});
	}

	private void updateSendResult(String dayStr, String batchNo, SettlePriceRequest request, TranResponse response,
			String errorMsg) {
		String responseCode = null;
		String responseDesc = null;
		if (response != null) {
			responseCode = response.getResponseCode();
			responseDesc = response.getResponseDesc();
		}
		dbManager.getJdbcTemplate().update(
				"update clearprice  set response_code=?,response_desc=?,send_error_msg=? where analysis_date=? and analysis_batch_no=? and init_date=? and product_code=?",
				new Object[] { responseCode, responseDesc, errorMsg, dayStr, batchNo, request.getInitDate(),
						request.getProductCode() });
	}
}
