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

import com.blockchain.service.tran.TollRequest;
import com.blockchain.service.tran.TranResponse;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.utils.CodeUtil;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 交易所收费单接口
 * 
 * @author lihongsong
 *
 */
@Component
public class TollSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(TollSend.class);
	@Resource
	CodeUtil codeUtil;

	public void send(String dayStr, String batchNo) {
		List<TollRequest> list = null;
		try {
			list = findFromDb(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			logger.info("list size:"+list.size());
			for (TollRequest request : list) {
				TranResponse response = null;
				String errorMsg = null;
				try {
					request.setRequestTime(new Date());
					request.setRequestId(codeUtil.getSysRequestId(Constants.code_toll_type));// 交易所收费单请求流水号
					response = txnServiceClient.toll(request);
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						logger.info("send second " + response);
						response = txnServiceClient.toll(request);
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

	public List<TollRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from memberfee where analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { dayStr, batchNo }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TollRequest request = new TollRequest();
				request.setBusiNo(rs.getString("serial_no"));
				request.setExchangeId(rs.getString("exchange_id"));
				request.setExchangeMarketType(rs.getString("exchange_market_type"));
				request.setBizType(rs.getString("biz_type"));

				request.setExchangeFeesType(rs.getString("exchange_fees_type"));
				request.setFeesBalance(NumberUtil.getBigDecimalFromStr(rs.getString("fees_balance")));
				request.setPayerMemCode(rs.getString("payer_mem_code"));
				request.setPayerFundAccount(rs.getString("payer_fund_account"));

				request.setPayeeMemCode(rs.getString("payee_mem_code"));
				request.setPayeeFundAccount(rs.getString("payee_fund_account"));
				request.setRelatedBillType(rs.getString("related_bill_type"));
				request.setRelatedBillNo(rs.getString("related_bill_no"));
				request.setBusiDate(rs.getString("init_date"));
				request.setBusiTime(rs.getString("busi_datetime"));
				request.setRequestDesc(rs.getString("remark"));
				return request;
			}
		});
	}

	private void updateSendResult(String dayStr, String batchNo, TollRequest request, TranResponse response,
			String errorMsg) {
		String responseCode = null;
		String responseDesc = null;
		if (response != null) {
			responseCode = response.getResponseCode();
			responseDesc = response.getResponseDesc();
		}
		dbManager.getJdbcTemplate().update(
				"update memberfee  set response_code=?,response_desc=?,send_time=?,send_request_id=?,send_error_msg=? where analysis_date=? and analysis_batch_no=? and serial_no=?",
				new Object[] { responseCode, responseDesc, request.getRequestTime(), request.getRequestId(), errorMsg,
						dayStr, batchNo, request.getBusiNo() });
	}
}
