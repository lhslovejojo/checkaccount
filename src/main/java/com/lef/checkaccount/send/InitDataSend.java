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

import com.blockchain.service.customer.AccountAssetUpdateRequest;
import com.blockchain.service.customer.AccountBalUpdateRequest;
import com.blockchain.service.customer.CustomerResponse;
import com.blockchain.service.customer.UserInfoSyncRequest;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.utils.CodeUtil;
import com.lef.checkaccount.utils.NumberUtil;

/**
 * 数据初始化
 * 
 * @author lihongsong
 *
 */
@Component
public class InitDataSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(InitDataSend.class);
	@Resource
	CodeUtil codeUtil;

	public void sendUser() {
		List<UserInfoSyncRequest> list = null;
		int start = 0;
		int offset = 10000;
		try {
			boolean isGo = true;
			while (isGo) {
				list = findComUserFromDb(start, offset);
				if (CollectionUtils.isEmpty(list)) {
					isGo = false;
				} else {
					logger.info("list size:" + list.size());
					if (list.size() < offset) {
						isGo = false;
					}
					for (UserInfoSyncRequest user : list) {
						CustomerResponse response = null;
						try {
							response = accountServiceClient.syncUserInfo(user);
							if (response == null
									|| !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
								logger.info("send second " + response);
								response = accountServiceClient.syncUserInfo(user);
							}
							if (response == null
									|| !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
								throw new AnalysisException(Constants.send_data_tohessian_error_code,
										Constants.send_data_tohessian_error_msg);
							}
						} catch (Exception e) {
							logger.error(e);
							throw new AnalysisException(Constants.send_data_tohessian_error_code,
									Constants.send_data_tohessian_error_msg, e);
						}
					}
					start = start + offset;
				}
			}
			start = 0;
			isGo = true;
			while (isGo) {
				list = findPerUserFromDb(start, offset);
				if (CollectionUtils.isEmpty(list)) {
					isGo = false;
				} else {
					logger.info("list size:" + list.size());
					if (list.size() < offset) {
						isGo = false;
					}
					for (UserInfoSyncRequest user : list) {
						CustomerResponse response = null;
						try {
							response = accountServiceClient.syncUserInfo(user);
							if (response == null
									|| !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
								logger.info("send second " + response);
								response = accountServiceClient.syncUserInfo(user);
							}
							if (response == null
									|| !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
								throw new AnalysisException(Constants.send_data_tohessian_error_code,
										Constants.send_data_tohessian_error_msg);
							}
						} catch (Exception e) {
							logger.error(e);
							throw new AnalysisException(Constants.send_data_tohessian_error_code,
									Constants.send_data_tohessian_error_msg, e);
						}
					}
					start = start + offset;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
	}

	public void sendBal() {
		List<AccountBalUpdateRequest> list = null;
		int start = 0;
		int offset = 10000;
		try {
			boolean isGo = true;
			while (isGo) {
				list = findBalFromDb(start, offset);
				if (CollectionUtils.isEmpty(list)) {
					isGo = false;
				} else {
					logger.info("list size:" + list.size());
					if (list.size() < offset) {
						isGo = false;
					}
					CustomerResponse response = null;
					try {
						response = accountServiceClient.batchUpdateBal(list);
						if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
							logger.info("send second " + response);
							response = accountServiceClient.batchUpdateBal(list);
						}
						if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
							throw new AnalysisException(Constants.send_data_tohessian_error_code,
									Constants.send_data_tohessian_error_msg);
						}
					} catch (Exception e) {
						logger.error(e);
						throw new AnalysisException(Constants.send_data_tohessian_error_code,
								Constants.send_data_tohessian_error_msg, e);
					}
					start = start + offset;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
	}
	
	public void sendAssetBal() {
		List<AccountAssetUpdateRequest> list = null;
		int start = 0;
		int offset = 10000;
		try {
			boolean isGo = true;
			while (isGo) {
				list = findAssetBalFromDb(start, offset);
				if (CollectionUtils.isEmpty(list)) {
					isGo = false;
				} else {
					logger.info("list size:" + list.size());
					if (list.size() < offset) {
						isGo = false;
					}
					CustomerResponse response = null;
					try {
						response = accountServiceClient.batchUpdateAssetBal(list);
						if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
							logger.info("send second " + response);
							response = accountServiceClient.batchUpdateAssetBal(list);
						}
						if (response == null || !Constants.hessianBackSuccessCode.equals(response.getResponseCode())) {
							throw new AnalysisException(Constants.send_data_tohessian_error_code,
									Constants.send_data_tohessian_error_msg);
						}
					} catch (Exception e) {
						logger.error(e);
						throw new AnalysisException(Constants.send_data_tohessian_error_code,
								Constants.send_data_tohessian_error_msg, e);
					}
					start = start + offset;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
	}

	public List<UserInfoSyncRequest> findComUserFromDb(int start, int offset) {
		String sql = "select a.*,b.*,c.ACCOUNT_NO as trade_account_no,d.ACCOUNT_NO as fund_account_no from mem_user_com_info a left join mem_users b on a.MEM_CODE=b.MEM_CODE left join mem_account c on (a.MEM_CODE=c.MEM_CODE and c.ACCOUNT_TYPE='1' and c.IS_DELETED='0') left join  mem_account d on (a.MEM_CODE=d.MEM_CODE and d.ACCOUNT_TYPE='2' and d.IS_DELETED='0') limit ?,? ";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { start, offset }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserInfoSyncRequest user = new UserInfoSyncRequest();
				user.setExchangeId(rs.getString("exchange_id"));
				user.setSerialNo(rs.getString("mem_code") + System.currentTimeMillis());
				user.setBusiDate(rs.getString("gmt_create"));
				user.setMemCode(rs.getString("exchange_mem_code"));
				user.setExchangeMemberStatus(rs.getString("exchange_mem_status"));
				user.setFullName(rs.getString("mem_full_name"));
				user.setShortName(rs.getString("mem_short_name"));
				user.setEnFullName(rs.getString("mem_en_full_name"));
				user.setEnShortName(rs.getString("mem_en_short_name"));
				user.setGender(rs.getString("gender"));
				user.setNationality(rs.getString("country_code"));
				user.setIdKind(rs.getString("id_kind"));
				user.setIdNo(rs.getString("id_no"));
				user.setTel(rs.getString("mobile"));
				user.setMemCodeClear(rs.getString("mem_code"));
				user.setFundAccountClear(rs.getString("fund_account_no"));
				user.setMemberMainType(rs.getString("mem_main_type"));
				user.setMemberType(rs.getString("mem_type"));
				user.setExchangeFundAccount(rs.getString("exchange_mem_code"));
				user.setTradeAccount(rs.getString("trade_account_no"));
				user.setLegalPerson(rs.getString("legal_person"));
				user.setBusinessCert(rs.getString("business_cert"));
				user.setOrgCode(rs.getString("org_code"));
				user.setTaxCert(rs.getString("tax_cert"));
				user.setTaxCertType(rs.getString("tax_cert_type"));
				user.setRegAddr(rs.getString("reg_addr"));
				user.setComAddr(rs.getString("com_addr"));
				user.setContactName(rs.getString("contact_name"));
				user.setContactTel(rs.getString("contact_tel"));
				user.setContactFax(rs.getString("contact_fax"));
				user.setContactEmail(rs.getString("contact_email"));
				return user;
			}
		});
	}
	
	
	public List<UserInfoSyncRequest> findPerUserFromDb(int start, int offset) {
		String sql = "select a.*,b.*,c.ACCOUNT_NO as trade_account_no,d.ACCOUNT_NO as fund_account_no from mem_user_person_info a left join mem_users b on a.MEM_CODE=b.MEM_CODE left join mem_account c on (a.MEM_CODE=c.MEM_CODE and c.ACCOUNT_TYPE='1' and c.IS_DELETED='0') left join  mem_account d on (a.MEM_CODE=d.MEM_CODE and d.ACCOUNT_TYPE='2' and d.IS_DELETED='0') limit ?,? ";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { start, offset }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserInfoSyncRequest user = new UserInfoSyncRequest();
				user.setExchangeId(rs.getString("exchange_id"));
				user.setSerialNo(rs.getString("mem_code") + System.currentTimeMillis());
				user.setBusiDate(rs.getString("gmt_create"));
				user.setMemCode(rs.getString("exchange_mem_code"));
				user.setExchangeMemberStatus(rs.getString("exchange_mem_status"));
				user.setFullName(rs.getString("mem_full_name"));
				user.setShortName(rs.getString("mem_short_name"));
				user.setEnFullName(rs.getString("mem_en_full_name"));
				user.setEnShortName(rs.getString("mem_en_short_name"));
				user.setGender(rs.getString("gender"));
				user.setNationality(rs.getString("country_code"));
				user.setIdKind(rs.getString("id_kind"));
				user.setIdNo(rs.getString("id_no"));
				user.setTel(rs.getString("mobile"));
				user.setMemCodeClear(rs.getString("mem_code"));
				user.setFundAccountClear(rs.getString("fund_account_no"));
				user.setMemberMainType(rs.getString("mem_main_type"));
				user.setMemberType(rs.getString("mem_type"));
				user.setExchangeFundAccount(rs.getString("exchange_mem_code"));
				user.setTradeAccount(rs.getString("trade_account_no"));
				return user;
			}
		});
	}

	public List<AccountBalUpdateRequest> findBalFromDb(int start, int offset) {
		String sql = "select a.fund_account,a.account_balance,a.usable_balance,a.fetch_balance,a.freeze_balance,a.usable_balance , b.exchange_fund_account from fund_account_balance a left join fund_account b on a.fund_account=b.fund_account   limit ?,? ";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { start, offset }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountBalUpdateRequest request = new AccountBalUpdateRequest();
				request.setAbleBal(NumberUtil.getBigDecimalFromStr(rs.getString("usable_balance")));
				request.setAdvanceBal(NumberUtil.getBigDecimalFromStr(rs.getString("fetch_balance")));
				request.setTotalBal(NumberUtil.getBigDecimalFromStr(rs.getString("account_balance")));
				request.setFrozenBal(NumberUtil.getBigDecimalFromStr(rs.getString("freeze_balance")));
				request.setMemCode(rs.getString("exchange_fund_account"));
				request.setFundAccountClear(rs.getString("fund_account"));
				return request;
			}
		});
	}
	
	public List<AccountAssetUpdateRequest> findAssetBalFromDb(int start, int offset) {
		String sql = "select a.*,b.exchange_mem_code,c.product_code from asset_account_balance a left join mem_users b on a.mem_code=b.mem_code left join product_info c on a.product_in_id=c.product_in_id limit ?,? ";
		return dbManager.getJdbcTemplate().query(sql, new Object[] { start, offset }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AccountAssetUpdateRequest request = new AccountAssetUpdateRequest();
				request.setMemCode(rs.getString("exchange_mem_code"));
				request.setProductCode(rs.getString("product_code")!=null ? rs.getString("product_code"):rs.getString("product_in_id"));
				request.setHoldPrice(NumberUtil.getBigDecimalFromStr(rs.getString("hold_price")));
				request.setQuantity(NumberUtil.getBigDecimalFromStr(rs.getString("share_quantity")));
				return request;
			}
		});
	}

}
