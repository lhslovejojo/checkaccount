package com.lef.checkaccount.grabdata.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.TaskCode;
import com.lef.checkaccount.grabdata.AbstractAnalysisService;
import com.lef.checkaccount.grabdata.GrabDataService;
import com.lef.checkaccount.utils.DbManager;
import com.lef.checkaccount.vo.RetVo;

@Service
public class BankCheckServiceImpl extends AbstractAnalysisService implements GrabDataService {
	private String charset = "GBK";
	private static Log logger = LogFactory.getLog(BankCheckServiceImpl.class);
	private String tableName="bankcheck";
	private String fileExpression=".*bankCheck.*\\.txt";
	@Resource
	DbManager dbManager;

	public void execute(String dayStr) {
		// TODO Auto-generated method stub
		super.deleteDb(tableName);
		super.getFileFromFtp(dayStr,fileExpression );
		File fileDirFile = new File(ftpToLocalDir);
		for (File file : fileDirFile.listFiles()) {
			if (isAnalysisFile(file.getName(),dayStr+fileExpression))
			{
				analysis(file);
			}
		}
		
	}

	public RetVo handle(List<String[]> list) {
		dbManager.executeSql(dataToSql(list));
		list.clear();
		return RetVo.getSuccessRet();
	}
	private List<Object> bindBean(List<String[]> list)
	{
		if (!CollectionUtils.isEmpty(list))
		{
		for (String[] strs: list)
		{
			
		}
		}
		return null;
	}
	private RetVo sendRemote()
	{
		return RetVo.getSuccessRet();
	}

	public void analysis(File file) {
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
			int maxLength = 500;
			List<String[]> dataList = new ArrayList<String[]>();
			while ((rowData = bufferedReader.readLine()) != null) {
				// 解析行数据
				if (StringUtils.isNotEmpty(rowData)) {
					String[] oneData = rowData.split("\\|",-1);
					dataList.add(oneData);
					if (dataList.size() == maxLength) {
						handle(dataList);
					}
				}
			}
			handle(dataList);
		} catch (Exception e) {
			logger.error("解析银行对账文件时出现异常", e);
			throw new AnalysisException(TaskCode.analysis_data_error_code,TaskCode.analysis_data_error_001);
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
	public List<String> dataToSql(List<String[]> dataList) {
		List<String> sqlList = new ArrayList<String>();
		if (dataList != null && dataList.size() > 0) {
			StringBuffer insertSql = new StringBuffer(
					"insert into "
							+ tableName
							+ " (create_time,serial_no, trade_serial_no, init_date, exchange_id, mem_code, fund_account, inout_way, inout_type, inout_source, occur_amount, money_type, bank_no, bank_pro_code, bank_account, deal_status, remark) values ");
			for (String[] oneData : dataList) {
				if (oneData != null) {
					insertSql.append("(");
					insertSql.append("NOW(),");
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
					if (oneData.length>15)
					{
					insertSql.append("'" + oneData[15] + "'");
					}
					else
					{
						insertSql.append("''");
					}
					insertSql.append("),");
				}
			}
			sqlList.add(insertSql.subSequence(0, insertSql.length() - 1)
					.toString());
		}
		return sqlList;

	}

}
