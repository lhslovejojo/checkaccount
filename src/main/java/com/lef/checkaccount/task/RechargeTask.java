package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.grabdata.impl.BankCheckRechargeServiceImpl;
import com.lef.checkaccount.send.RechargeSend;

@Component
public class RechargeTask {
	private static Log logger = LogFactory.getLog(RechargeTask.class);
	@Resource
	BankCheckRechargeServiceImpl bankCheckRechargeServiceImpl;
	@Resource
	RechargeSend rechargeSend;

	public void execute(String dayStr, String batchNo) {
		// 1.解析yyyymmdd_xxx(交易所代码)_yyy(银行产品代码)_bankCheck.txt 中入金记录
		// 2.发送数据
		logger.info("do RechargeTask begin");
		try {
			bankCheckRechargeServiceImpl.execute(dayStr, batchNo);
			rechargeSend.send(dayStr, batchNo);
			logger.info("do RechargeTask end");
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.task_recharge_step, e);
		}
	}
}
