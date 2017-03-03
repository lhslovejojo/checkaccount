package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.tran.TollRequest;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 交易所收费单接口
 * 
 * @author lihongsong
 *
 */
@Component
public class TollSend extends AbstractSend {

	public void send() {
		List<TollRequest> list = findFromDb();
		if (!CollectionUtils.isEmpty(list)) {
			for (TollRequest request : list) {
				txnServiceClient.toll(request);
			}
		}
	}

	public List<TollRequest> findFromDb() {
		String sql = "select * from dealinfo";
		return dbManager.getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TollRequest request = new TollRequest();
				// request.setRequestTime(new Date());
				// request.setRequestId(requestId);
				request.setBusiNo(rs.getString("deal_id"));
				request.setExchangeId(rs.getString("exchange_id"));
				request.setExchangeMarketType(rs.getString("exchange_market_type"));
				request.setBizType(rs.getString("biz_type"));

				request.setExchangeFeesType(rs.getString("exchange_fees_type"));
				request.setFeesBalance(NumberUtil.getLongFromStr(rs.getString("fees_balance")));
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
}
