/**
 * 
 */
package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.customer.CustomerResponse;
import com.blockchain.service.customer.UserInfoSyncRequest;
import com.blockchain.service.customer.domain.UserCoreInfo;
import com.blockchain.service.customer.domain.UserInstInfo;
import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;

/**
 * 
 * @author lihongsong 用户注册信息推送
 */
@Component
public class SyncUserInfoSend extends AbstractSend {
	private static Log logger = LogFactory.getLog(SyncUserInfoSend.class);

	public void send(String dayStr, String batchNo) {
		List<UserInfoSyncRequest> list = null;
		try {
			list = findFromDb( dayStr,  batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.find_data_fromdb_error_code, Constants.find_data_fromdb_error_msg, e);
		}
		if (!CollectionUtils.isEmpty(list)) {
			for (UserInfoSyncRequest user : list) {
				CustomerResponse customerResponse=null;
				String errorMsg=null;
				Date sendDate=new Date();
				try {
					 customerResponse=accountServiceClient.syncUserInfo(user);
				} catch (Exception e) {
					logger.error(e);
					errorMsg=Constants.send_data_tohessian_error_msg;
					throw new AnalysisException(Constants.send_data_tohessian_error_code,
							Constants.send_data_tohessian_error_msg, e);
				}
				finally
				{
					updateSendResult(dayStr,batchNo,user.getSerial_no(),customerResponse,errorMsg,sendDate);
				}
			}
		}
	}
	

	public List<UserInfoSyncRequest> findFromDb(String dayStr, String batchNo) {
		String sql = "select * from clientinfomod where analysis_date=? and analysis_batch_no=?";
		return dbManager.getJdbcTemplate().query(sql, new Object[]{dayStr,batchNo},new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserInfoSyncRequest user = new UserInfoSyncRequest();
				user.setExchangeId(rs.getString("exchange_id"));
				user.setSerial_no(rs.getString("serial_no"));
				user.setMemCode(rs.getString("mem_code"));
				user.setExchangeMemberStatus(rs.getString("exchange_member_status"));
				user.setFullName(rs.getString("full_name"));
				user.setShortName(rs.getString("short_name"));
				user.setEnFullName(rs.getString("en_full_name"));
				user.setEnShortName(rs.getString("en_short_name"));
				user.setGender(rs.getString("gender"));
				user.setNationality(rs.getString("nationality"));
				user.setIdKind(rs.getString("id_kind"));
				user.setIdNo(rs.getString("id_no"));
				user.setTel(rs.getString("tel"));
				user.setUpMemCode(rs.getString("up_mem_code"));
				user.setBrokerCode(rs.getString("broker_code"));
				UserCoreInfo coreInfo = new UserCoreInfo();
				coreInfo.setMemCodeClear(rs.getString("mem_code"));
				coreInfo.setFundAccountClear(rs.getString("fund_account"));
				coreInfo.setMemberMainType(rs.getString("member_main_type"));
				coreInfo.setMemberType(rs.getString("member_type"));
				coreInfo.setExchangeFundAccount(rs.getString("fund_account"));
				coreInfo.setTradeAccount(rs.getString("trade_account"));
				List<UserCoreInfo> list = new ArrayList<UserCoreInfo>();
				list.add(coreInfo);
				user.setCoreInfo(list);
				UserInstInfo instInfo = new UserInstInfo();
				instInfo.setLegalPerson(rs.getString("legal_person"));
				instInfo.setBusinessCert(rs.getString("business_cert"));
				instInfo.setOrgCode(rs.getString("org_code"));
				instInfo.setTaxCert(rs.getString("tax_cert"));
				instInfo.setTaxCertType(rs.getString("tax_cert_type"));
				instInfo.setRegAddr(rs.getString("reg_addr"));
				instInfo.setComAddr(rs.getString("com_addr"));
				instInfo.setContactName(rs.getString("contact_name"));
				instInfo.setContactTel(rs.getString("contact_tel"));
				instInfo.setContactFax(rs.getString("contact_fax"));
				instInfo.setContactEmail(rs.getString("contact_email"));
				List<UserInstInfo> infoList = new ArrayList<UserInstInfo>();
				infoList.add(instInfo);
				user.setInstInfo(infoList);
				return user;
			}
		});
	}
	private void updateSendResult(String dayStr, String batchNo,String serialNo,CustomerResponse customerResponse,String errorMsg,Date sendDate)
	{
		 String responseCode=null;
		 String responseDesc=null;
		if (customerResponse!=null)
		{
			responseCode=customerResponse.getResponseCode();
			responseDesc=customerResponse.getResponseDesc();
		}
		dbManager.getJdbcTemplate().update("update clientinfomod  set response_code=?,response_desc=?,send_time=?,send_error_msg=? where analysis_date=? and analysis_batch_no=? and serial_no=?", new Object[]{responseCode,responseDesc,sendDate,errorMsg,dayStr,batchNo,serialNo});
	}
}
