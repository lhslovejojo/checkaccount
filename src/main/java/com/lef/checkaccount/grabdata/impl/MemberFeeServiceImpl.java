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
 * 外部费用明细文件 yyyymmdd_xxx(交易所代码)_memberFee.txt
 * 
 * @author lihongsong
 *
 */
@Service
public class MemberFeeServiceImpl extends AbstractAnalysisService implements GrabDataService {

	private static Log logger = LogFactory.getLog(MemberFeeServiceImpl.class);
	@Value("${grabdata.MemberFee.tableName}")
	private String tableName;
	@Value("${grabdata.MemberFee.fileExpression}")
	private String fileExpression;

	public void execute(String dayStr, String batchNo) {
		// TODO Auto-generated method stub
		super.getFileFromFtp(dayStr, fileExpression);
		File fileDirFile = new File(ftpToLocalDir+dayStr);
		File[] files=fileDirFile.listFiles();
		if (files!=null && files.length>0)
		{
		sortFileArrayByName(files);
		for (File file : fileDirFile.listFiles()) {
			if (isAnalysisFile(file.getName(), dayStr + fileExpression)) {
				analysis(file, dayStr, batchNo);
				return ;
			}
		}
		}

	}

	public RetVo handle(List<String[]> list, String dayStr, String batchNo) {
		dbManager.executeSql(dataToSql(list, dayStr, batchNo));
		list.clear();
		return RetVo.getSuccessRet();
	}

	public void analysis(File file, String dayStr, String batchNo) {
		// 解析银行对账文件
		BufferedReader bufferedReader = null;
		try {
			// 读取文件
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.charset));
			if (bufferedReader == null) {
				logger.info("银行对账文件为空");
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
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001);
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
					+ " (create_time,analysis_date,analysis_batch_no, init_date, serial_no, exchange_id, exchange_market_type, biz_type, exchange_fees_type, fees_balance, payer_mem_code,payee_fund_account, payer_fund_account, payee_mem_code, related_bill_type, related_bill_no, remark, busi_datetime)  values ");
			for (String[] oneData : dataList) {
				if (oneData != null) {
					insertSql.append("(");
					insertSql.append("NOW(),");
					insertSql.append("'"+dayStr+"',");
					insertSql.append("'"+batchNo+"',");
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
					insertSql.append("'" + oneData[14] + "'");
					insertSql.append("),");
				}
			}
			sqlList.add(insertSql.subSequence(0, insertSql.length() - 1).toString());
		}
		return sqlList;

	}
}
