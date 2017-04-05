package com.lef.checkaccount.task;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.send.InitDataSend;
import com.lef.checkaccount.utils.DbManager;
import com.lef.checkaccount.vo.RetVo;

@Component
public class InitDataTask {
	private static Log logger = LogFactory.getLog(InitDataTask.class);
	@Resource
	InitDataSend initDataSend;

	@Resource
	DbManager dbManager;
	protected String charset = "GBK";
	protected int maxLine = 500;

	public void execute(String tableName, String filePath) {
		// 1.解析yyyymmdd_xxx(交易所代码)_yyy(银行产品代码)_bankCheck.txt 中入金记录
		// 2.发送数据
		logger.info("do initData begin tableName:" + tableName + " filePath:" + filePath);
		try {
			dbManager.executeSql("delete from " + tableName);
			analysis(tableName, filePath);
			logger.info("do initData end");
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.task_recharge_step, e);
		}
	}

	public void executeSendJbcc() {
		// 发送初始化用户数据
		logger.info("do initData account info begin");
		try {
			logger.info("do user info begin");
			initDataSend.sendUser();
			logger.info("do user info end");
			logger.info("do fundBal info begin");
			initDataSend.sendBal();
			logger.info("do fundBal info end");
			logger.info("do assetBal info begin");
			initDataSend.sendAssetBal();
			logger.info("do assetBal info end");
			logger.info("do initData account info end");
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.task_recharge_step, e);
		}

	}

	public RetVo handle(List<String> list, String tableName) {
		try {
			dbManager.executeSql(dataToSql(list, tableName));
		} catch (Exception e) {
			throw new AnalysisException(Constants.insert_data_error_code, Constants.insert_data_error_msg, e);
		}
		list.clear();
		return RetVo.getSuccessRet();
	}

	public void analysis(String tableName, String filePath) {
		// 解析银行对账文件
		BufferedReader bufferedReader = null;
		try {
			// 读取文件
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), this.charset));
			if (bufferedReader == null) {
				if (logger.isInfoEnabled()) {
					logger.info("银行对账文件为空");
				}
				return;
			}
			// 遍历解析行数据
			String rowData = null;
			List<String> dataList = new ArrayList<String>();
			bufferedReader.readLine(); // 跳过第一行
			while ((rowData = bufferedReader.readLine()) != null) {
				// 解析行数据
				if (StringUtils.isNotEmpty(rowData)) {
					dataList.add(rowData);
					if (dataList.size() == maxLine) {
						handle(dataList, tableName);
					}
				}
			}
			handle(dataList, tableName);
		} catch (Exception e) {
			logger.error("解析银行对账文件时出现异常", e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001, e);
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

	public List<String> dataToSql(List<String> dataList, String tableName) {
		List<String> sqlList = new ArrayList<String>();
		if (dataList != null && dataList.size() > 0) {
			StringBuffer insertSql = new StringBuffer("insert into " + tableName + "  values ");
			for (String oneData : dataList) {
				if (oneData != null) {
					insertSql.append("(");
					insertSql.append(oneData);
					insertSql.append("),");
				}
			}
			sqlList.add(insertSql.subSequence(0, insertSql.length() - 1).toString());
		}
		return sqlList;

	}
}
