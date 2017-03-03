package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.tran.ConCludeRequest;
import com.lef.checkaccount.utils.NumberUtil;
/**
 * 客户交易成交记录推送
 * @author lihongsong
 *
 */
@Component
public class ConCludeSend extends AbstractSend {

	public void send() {
		List<ConCludeRequest> list = findFromDb();
		if (!CollectionUtils.isEmpty(list)) {
			for (ConCludeRequest request : list) {
				txnServiceClient.conClude(request);
			}
		}
	}
	public List<ConCludeRequest> findFromDb() {
		String sql = "select * from dealinfo";
		return dbManager.getJdbcTemplate().query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConCludeRequest request = new ConCludeRequest();
				// request.setRequestTime(new Date());
				// request.setRequestId(requestId);
				request.setBusiNo(rs.getString("deal_id"));
				request.setExchangeId(rs.getString("exchange_id"));
				request.setExchangeMarketType(rs.getString("exchange_market_type"));
				request.setBusiType(rs.getString("biz_type"));

				request.setMemCode(rs.getString("open_mem_code"));
				request.setFundAccountClear(rs.getString("open_fund_account"));
				request.setOpenTradeAccount(rs.getString("open_trade_account"));
				request.setOppMemCode(rs.getString("opp_mem_code"));

				request.setOppFundAccount(rs.getString("opp_fund_account"));
				request.setOppTradeAccount(rs.getString("opp_trade_account"));
//				request.setMoneyType(moneyType);(rs.getString("bank_no"));
				request.setProductCode(rs.getString("product_code"));
				request.setProductCategoryId(rs.getString("product_category_id"));
				request.setTradeType(rs.getString("trade_type"));
				request.setBusiDate(rs.getString("init_date"));
				request.setBusiTime(request.getBusiDate()+rs.getString("deal_time"));
				request.setTradeDir(rs.getString("trade_dir"));
				request.setDealType(rs.getString("deal_type"));
				request.setOppDealType(rs.getString("opp_deal_type"));
//				request.setOrderWay(rs.getString("trade_type"));
//				request.setDepositWay(rs.getString("deposit_type"));
				request.setOrderPrice(NumberUtil.getLongFromStr(rs.getString("deal_price")));
				request.setHoldPrice(NumberUtil.getLongFromStr(rs.getString("hold_price")));
				request.setOrderQuantity(NumberUtil.getIntegerFromStr(rs.getString("deal_quantity")));
				request.setDealTotalPrice(NumberUtil.getLongFromStr(rs.getString("deal_total_price")));
				request.setDepositRate(NumberUtil.getDoubleFromStr(rs.getString("deposit_rate")));
				
				request.setDepositRatioType(rs.getString("deposit_ratio_type"));
				request.setDepositType(rs.getString("deposit_type"));
				request.setDepositBalance(NumberUtil.getLongFromStr(rs.getString("deposit_balance")));
				request.setOpenPoundage(NumberUtil.getLongFromStr(rs.getString("open_poundage")));
				request.setOppPoundage(NumberUtil.getLongFromStr(rs.getString("opp_poundage")));
				request.setDepotOrderNo(rs.getString("depot_order_no"));
				request.setOppDepotOrderNo(rs.getString("opp_depot_order_no"));
				request.setOrderId(rs.getString("order_id"));
				request.setOppOrderId(rs.getString("opp_order_id"));
				request.setSettlementDate(rs.getString("settlement_date"));
				return request;
			}
		});
	}
}
