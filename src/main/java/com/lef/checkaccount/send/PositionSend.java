package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.tran.PositionRequest;
import com.blockchain.service.tran.TranResponse;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.utils.CodeUtil;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 日终持仓清算推送
 * 
 * @author lihongsong
 *
 */
@Component
public class PositionSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(PositionSend.class);
	@Resource
	CodeUtil codeUtil;

	public void send(String dayStr, String batchNo) {
		List<PositionRequest> list = null;
		try {
			list = findFromDb(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			logger.info("list size:" + list.size());
			for (PositionRequest request : list) {
				TranResponse response = null;
				String errorMsg = null;
				try {
					request.setRequestTime(new Date());
					request.setRequestId(codeUtil.getSysRequestId(Constants.code_position_type));// 持仓明细请求流水号
					response = txnServiceClient.position(request);
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						logger.info("send second " + response);
						response = txnServiceClient.position(request);
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

	public List<PositionRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from memberpositiondetail where analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { dayStr, batchNo }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				PositionRequest request = new PositionRequest();
				request.setBusiNo(rs.getString("product_code") + rs.getString("hold_id"));
				request.setExchangeId(rs.getString("exchange_id"));
				request.setMemCode(rs.getString("mem_code"));
				request.setOpenTradeAccount(rs.getString("trade_account"));
				request.setProductCode(rs.getString("product_code"));
				request.setProductCategoryId(rs.getString("product_category_id"));
				request.setBusiDate(rs.getString("init_date"));
				request.setBusiTime(rs.getString("busi_datetime"));
				request.setTradeDir(rs.getString("trade_dir"));
				request.setDepositWay(rs.getString("deposit_way"));
				request.setOrderPrice(NumberUtil.getBigDecimalFromStr(rs.getString("open_price")));
				request.setHoldPrice(NumberUtil.getBigDecimalFromStr(rs.getString("hold_price")));
				request.setOrderQuantity(NumberUtil.getBigDecimalFromStr(rs.getString("deal_quantity")));
				request.setDealTotalPrice(rs.getString("left_quantity"));
				request.setPresentUnit(rs.getString("present_unit"));
				request.setDepositRate(NumberUtil.getBigDecimalFromStr(rs.getString("deposit_rate")));
				request.setDepositRatioType(rs.getString("deposit_ratio_type"));
				request.setDepositType(rs.getString("deposit_type"));
				request.setDepositBalance(NumberUtil.getBigDecimalFromStr(rs.getString("perform_balance")));
				request.setOpenPoundage(NumberUtil.getBigDecimalFromStr(rs.getString("trade_poundage")));
				request.setSquareLoss(NumberUtil.getBigDecimalFromStr(rs.getString("square_profit_loss")));
				request.setSettleLoss(NumberUtil.getBigDecimalFromStr(rs.getString("settle_profit_loss")));
				request.setDepotOrderNo(rs.getString("hold_id"));
				request.setSettlePrice(NumberUtil.getBigDecimalFromStr(rs.getString("settle_price")));
				request.setDelayFees(NumberUtil.getBigDecimalFromStr(rs.getString("delay_fees")));
				return request;
			}
		});
	}

	private void updateSendResult(String dayStr, String batchNo, PositionRequest request, TranResponse response,
			String errorMsg) {
		String responseCode = null;
		String responseDesc = null;
		if (response != null) {
			responseCode = response.getResponseCode();
			responseDesc = response.getResponseDesc();
		}
		dbManager.getJdbcTemplate().update(
				"update memberpositiondetail  set response_code=?,response_desc=?,send_time=?,send_request_id=?,send_error_msg=? where analysis_date=? and analysis_batch_no=? and hold_id=?",
				new Object[] { responseCode, responseDesc, request.getRequestTime(), request.getRequestId(), errorMsg,
						dayStr, batchNo, request.getDepotOrderNo() });
	}
}
