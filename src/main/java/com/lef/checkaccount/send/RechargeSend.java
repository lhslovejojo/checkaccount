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

import com.blockchain.service.tran.RechargeRequest;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.TaskCode;
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
			throw new AnalysisException(TaskCode.find_data_fromdb_error_code, TaskCode.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			for (RechargeRequest request : list) {
				try {
					request.setRequestTime(new Date());
					request.setRequestId(codeUtil.getSysRequestId(TaskCode.code_recharge_type));// 入金请求流水号
					txnServiceClient.recharge(request);
				} catch (Exception e) {
					logger.error(e);
					throw new AnalysisException(TaskCode.send_data_tohessian_error_code,
							TaskCode.send_data_tohessian_error_msg, e);
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
				request.setBusiType("Recharge"); //自定义

				request.setMemCode(rs.getString("mem_code"));
				request.setFundAccountClear(rs.getString("fund_account"));
				request.setMoneyType(rs.getString("money_type"));
				request.setOrderAmt(NumberUtil.getLongFromStr(rs.getString("occur_amount")));
				// 下面是扩展字段
				request.setBankProCode(rs.getString("bank_pro_code"));
				request.setBankAccount(rs.getString("bank_account"));
				request.setBankNO(rs.getString("bank_no"));
				return request;
			}
		});
	}
}
