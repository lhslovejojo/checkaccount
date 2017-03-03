/**
 * 
 */
package com.lef.checkaccount.send;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.blockchain.service.customer.UserInfoSyncRequest;
import com.blockchain.service.customer.domain.UserCoreInfo;
import com.blockchain.service.customer.domain.UserInstInfo;

/**
 * 
 * @author lihongsong
 * 用户注册信息推送
 */
@Component
public class SyncUserInfoSend extends AbstractSend {

	public void send() {
		List<UserInfoSyncRequest> list = findFromDb();
		if (!CollectionUtils.isEmpty(list)) {
			for (UserInfoSyncRequest user : list) {
				accountServiceClient.syncUserInfo(user);
			}
		}
	}

	public List<UserInfoSyncRequest> findFromDb() {
		String sql = "select * from clientinfomod";
		return dbManager.getJdbcTemplate().query(sql, new RowMapper() {
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
}
