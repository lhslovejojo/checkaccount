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
import com.blockchain.service.tran.TranResponse;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.utils.CodeUtil;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 客户交易成交记录推送
 * 
 * @author lihongsong
 *
 */
@Component
public class ConCludeSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(ConCludeSend.class);

	@Resource
	CodeUtil codeUtil;

	public void send(String dayStr, String batchNo) {
		List<ConCludeRequest> list = null;
		try {
			list = findFromDb(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			for (ConCludeRequest request : list) {
				TranResponse response = null;
				String errorMsg = null;
				try {
					Long currentTime = System.currentTimeMillis();
					request.setRequestTime(new Date());
					request.setRequestId(codeUtil.getSysRequestId(Constants.code_conclude_type));// 交易成交请求流水号
					response = txnServiceClient.conClude(request);
					System.out.println("耗时:" + (System.currentTimeMillis() - currentTime));
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						logger.info("send second " + response);
						response = txnServiceClient.conClude(request);
					}
					if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
						throw new AnalysisException(Constants.send_data_tohessian_error_code,
								Constants.send_data_tohessian_error_msg);
					}
				} catch (Exception e) {
					logger.error(e);
					errorMsg = Constants.send_data_tohessian_error_msg;
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

	public List<ConCludeRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from dealinfo where analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { dayStr, batchNo }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConCludeRequest request = new ConCludeRequest();
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
				request.setMoneyType("CNY");
				request.setProductCode(rs.getString("product_code"));
				request.setProductCategoryId(rs.getString("product_category_id"));
				request.setTradeType(rs.getString("trade_type"));
				request.setBusiDate(rs.getString("init_date"));
				request.setBusiTime(request.getBusiDate() + rs.getString("deal_time"));
				request.setTradeDir(rs.getString("trade_dir"));
				request.setDealType(rs.getString("deal_type"));
				request.setOppDealType(rs.getString("opp_deal_type"));
				request.setOrderWay("Z");
				// request.setDepositWay(rs.getString("deposit_type"));
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

	private void updateSendResult(String dayStr, String batchNo, ConCludeRequest request, TranResponse response,
			String errorMsg) {
		String responseCode = null;
		String responseDesc = null;
		if (response != null) {
			responseCode = response.getResponseCode();
			responseDesc = response.getResponseDesc();
		}
		dbManager.getJdbcTemplate().update(
				"update dealinfo  set response_code=?,response_desc=?,send_time=?,send_request_id=?,send_error_msg=? where analysis_date=? and analysis_batch_no=? and deal_id=?",
				new Object[] { responseCode, responseDesc, request.getRequestTime(), request.getRequestId(), errorMsg,
						dayStr, batchNo, request.getBusiNo() });
	}
}
