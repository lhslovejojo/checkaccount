package com.lef.checkaccount.grabdata.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.grabdata.AbstractAnalysisService;
import com.lef.checkaccount.grabdata.GrabDataService;
import com.lef.checkaccount.vo.RetVo;

/**
 * 客户信息变更文件 yyyymmdd_xxx(交易所代码)_clientInfoMod.txt
 * 
 * @author lihongsong
 *
 */
@Service
public class ClientInfoModServiceImpl extends AbstractAnalysisService implements GrabDataService {

	private static Log logger = LogFactory.getLog(ClientInfoModServiceImpl.class);
	@Value("${grabdata.ClientInfoMod.tableName}")
	private String tableName;
	@Value("${grabdata.ClientInfoMod.fileExpression}")
	private String fileExpression;

	public void execute(String dayStr, String batchNo) {
		// TODO Auto-generated method stub
		super.getFileFromFtp(dayStr, fileExpression);
		File fileDirFile = new File(ftpToLocalDir);
		File[] files = fileDirFile.listFiles();
		if (files != null && files.length > 0) {
			sortFileArrayByName(files);
			for (File file : fileDirFile.listFiles()) {
				if (isAnalysisFile(file.getName(), dayStr + fileExpression)) {
					analysis(file, dayStr, batchNo);
					return;
				}
			}
		}
	}

	public RetVo handle(List<String[]> list, String dayStr, String batchNo) {
		try {
			dbManager.executeSql(dataToSql(list, dayStr, batchNo));
			list.clear();
			return RetVo.getSuccessRet();
		} catch (Exception e) {
			throw new AnalysisException(Constants.insert_data_error_code, Constants.insert_data_error_msg, e);
		}

	}

	public void analysis(File file, String dayStr, String batchNo) {
		// 解析银行对账文件
		BufferedReader bufferedReader = null;
		try {
			// 读取文件
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.charset));
			if (bufferedReader == null) {
				if (logger.isInfoEnabled()) {
					logger.info("银行对账文件为空");
				}
				return;
			}
			// 遍历解析行数据
			String rowData = null;
			List<String[]> dataList = new ArrayList<String[]>();
			while ((rowData = bufferedReader.readLine()) != null) {
				// 解析行数据
				if (StringUtils.isNotEmpty(rowData)) {
					String[] oneData = rowData.split("\\|", -1);
					dataList.add(oneData);
					if (dataList.size() == maxLine) {
						handle(dataList, dayStr, batchNo);
					}
				}
			}
			handle(dataList, dayStr, batchNo);
		} catch (Exception e) {
			logger.error("解析银行对账文件时出现异常", e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.task_sync_user_step);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error("解析银行对账文件关闭bufferedReader时出现异常", e);
				}
			}
		}
	}

	public List<String> dataToSql(List<String[]> dataList, String dayStr, String batchNo) {
		List<String> sqlList = new ArrayList<String>();
		if (dataList != null && dataList.size() > 0) {
			StringBuffer insertSql = new StringBuffer("insert into " + tableName
					+ " (create_time,analysis_date,analysis_batch_no, init_date, serial_no, exchange_id, mem_code, fund_account, trade_account, member_type, member_main_type, full_name, short_name, en_full_name, en_short_name, tel, exchange_member_status, up_mem_code, broker_code, legal_person, id_kind, id_no, gender, nationality, business_cert, org_code, tax_cert, tax_cert_type, reg_addr) values ");
			for (String[] oneData : dataList) {
				if (oneData != null) {
					insertSql.append("(");
					insertSql.append("NOW(),");
					insertSql.append("'" + dayStr + "',");
					insertSql.append("'" + batchNo + "',");
					insertSql.append("'" + oneData[0] + "',");
					insertSql.append("'" + oneData[1] + "',");
					insertSql.append("'" + oneData[2] + "',");
					insertSql.append("'" + oneData[3] + "',");
					insertSql.append("'" + oneData[4] + "',");
					insertSql.append("'" + oneData[5] + "',");
					insertSql.append("'" + oneData[6] + "',");
					insertSql.append("'" + oneData[7] + "',");
					insertSql.append("'" + oneData[8] + "',");
					insertSql.append("'" + oneData[9] + "',");
					insertSql.append("'" + oneData[10] + "',");
					insertSql.append("'" + oneData[11] + "',");
					insertSql.append("'" + oneData[12] + "',");
					insertSql.append("'" + oneData[13] + "',");
					insertSql.append("'" + oneData[14] + "',");
					insertSql.append("'" + oneData[15] + "',");
					insertSql.append("'" + oneData[16] + "',");
					insertSql.append("'" + oneData[17] + "',");
					insertSql.append("'" + oneData[18] + "',");
					insertSql.append("'" + oneData[19] + "',");
					insertSql.append("'" + oneData[20] + "',");
					insertSql.append("'" + oneData[21] + "',");
					insertSql.append("'" + oneData[22] + "',");
					insertSql.append("'" + oneData[23] + "',");
					insertSql.append("'" + oneData[24] + "',");
					if (oneData.length > 25) {
						insertSql.append("'" + oneData[25] + "'");
					} else {
						insertSql.append("''");
					}
					insertSql.append("),");
				}
			}
			sqlList.add(insertSql.subSequence(0, insertSql.length() - 1).toString());
		}
		return sqlList;

	}

}
