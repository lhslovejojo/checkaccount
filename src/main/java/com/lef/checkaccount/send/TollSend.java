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
			for (TollRequest request : list) {
				try {
					request.setRequestTime(new Date());
					request.setRequestId(codeUtil.getSysRequestId(Constants.code_toll_type));// 交易所收费单请求流水号
					txnServiceClient.toll(request);
				} catch (Exception e) {
					logger.error(e);
					throw new AnalysisException(Constants.send_data_tohessian_error_code,
							Constants.send_data_tohessian_error_msg, e);
				}
			}
		}
	}

	public List<TollRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from dealinfo where analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { dayStr, batchNo }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TollRequest request = new TollRequest();
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
