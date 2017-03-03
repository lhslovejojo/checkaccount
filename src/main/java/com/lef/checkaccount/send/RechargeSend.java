package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.TxnService;
import com.blockchain.service.tran.RechargeRequest;
import com.lef.checkaccount.utils.DbManager;
import com.lef.checkaccount.utils.NumberUtil;
/**
 * 客户入金推送
 * @author lihongsong
 *
 */
@Component
public class RechargeSend extends AbstractSend {

	public void send() {
		List<RechargeRequest> list = findFromDb();
		if (!CollectionUtils.isEmpty(list)) {
			for (RechargeRequest request : list) {
				txnServiceClient.recharge(request);
			}
		}
	}

	public List<RechargeRequest> findFromDb() {
		String sql = "select * from bankcheck where inout_type='1'";
		return dbManager.getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RechargeRequest request = new RechargeRequest();
				// request.setRequestTime(new Date());
				// request.setRequestId(requestId);
				request.setSerialNo(rs.getString("serial_no"));
				request.setTradeSerialNo(rs.getString("trade_serial_no"));
				request.setSystemCode(rs.getString("exchange_id"));
				request.setBusiDate(rs.getString("init_date"));
				request.setBusiType(rs.getString("mem_code")); // 待确定

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
