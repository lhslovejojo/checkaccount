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
 * 持仓明细文件 yyyymmdd_xxx(交易所代码)_memberPositionDetail.txt
 * 
 * @author lihongsong
 *
 */
@Service
public class MemberPositionDetailServiceImpl extends AbstractAnalysisService implements GrabDataService {
	private static Log logger = LogFactory.getLog(MemberPositionDetailServiceImpl.class);
	@Value("${grabdata.MemberPositionDetail.tableName}")
	private String tableName;
	@Value("${grabdata.MemberPositionDetail.fileExpression}")
	private String fileExpression;

	public void execute(String dayStr, String batchNo) {
		// TODO Auto-generated method stub
		super.getFileFromFtp(dayStr, fileExpression);
		File fileDirFile = new File(ftpToLocalDir+dayStr);
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
					+ " ( create_time, analysis_date,analysis_batch_no,init_date, exchange_id, hold_id, mem_code, trade_account, product_category_id, product_code, trade_dir, deposit_way, open_price, hold_price, deal_quantity, left_quantity, present_unit, trade_poundage, delay_fees, perform_balance, deposit_rate, square_profit_loss, settle_profit_loss, settle_price, deposit_ratio_type, deposit_type, today_hold_flag, busi_datetime)  values ");
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
					insertSql.append("'" + oneData[24] + "'");
					insertSql.append("),");
				}
			}
			sqlList.add(insertSql.subSequence(0, insertSql.length() - 1).toString());
		}
		return sqlList;

	}
}
