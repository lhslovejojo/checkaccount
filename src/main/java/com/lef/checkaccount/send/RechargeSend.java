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

import com.blockchain.service.tran.ConCludeRequest;
import com.blockchain.service.tran.RechargeRequest;
import com.blockchain.service.tran.TranResponse;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.utils.CodeUtil;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 客户入金推送
 * 
 * @author lihongsong
 *
 */
@Component
public class RechargeSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(RechargeSend.class);
	@Resource
	CodeUtil codeUtil;

	public void send(String dayStr, String batchNo) {
		List<RechargeRequest> list = null;
		try {
			list = findFromDb(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			logger.info("list size:"+list.size());
			for (RechargeRequest request : list) {
				TranResponse response = null;
				String errorMsg = null;
				try {
					request.setRequestTime(new Date());
					request.setRequestId(codeUtil.getSysRequestId(Constants.code_recharge_type));// 入金请求流水号
					response = txnServiceClient.recharge(request);
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						logger.info("send second " + response);
						response = txnServiceClient.recharge(request);
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

	public List<RechargeRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from bankcheck where inout_type='1'  and analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { dayStr, batchNo }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RechargeRequest request = new RechargeRequest();
				request.setSerialNo(rs.getString("serial_no"));
				request.setTradeSerialNo(rs.getString("trade_serial_no"));
				request.setSystemCode(rs.getString("exchange_id"));
				request.setBusiDate(rs.getString("init_date"));
				request.setBusiType("Recharge"); // 自定义

				request.setMemCode(rs.getString("mem_code"));
				request.setFundAccountClear(rs.getString("fund_account"));
				request.setMoneyType(rs.getString("money_type"));
				request.setOrderAmt(NumberUtil.getLongFromStr(rs.getString("occur_amount")));
				// 下面是扩展字段
				request.setBankProCode(rs.getString("bank_pro_code"));
				request.setBankAccount(rs.getString("bank_account"));
				request.setBankNo(rs.getString("bank_no"));
				return request;
			}
		});
	}

	private void updateSendResult(String dayStr, String batchNo, RechargeRequest request, TranResponse response,
			String errorMsg) {
		String responseCode = null;
		String responseDesc = null;
		if (response != null) {
			responseCode = response.getResponseCode();
			responseDesc = response.getResponseDesc();
		}
		dbManager.getJdbcTemplate().update(
				"update bankcheck  set response_code=?,response_desc=?,send_time=?,send_request_id=?,send_error_msg=? where analysis_date=? and analysis_batch_no=? and serial_no=?",
				new Object[] { responseCode, responseDesc, request.getRequestTime(), request.getRequestId(), errorMsg,
						dayStr, batchNo, request.getSerialNo() });
	}
}
