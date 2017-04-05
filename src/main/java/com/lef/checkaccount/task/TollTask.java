package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.grabdata.impl.MemberFeeServiceImpl;
import com.lef.checkaccount.send.TollSend;

@Component
public class TollTask {
	private static Log logger = LogFactory.getLog(TollTask.class);
	@Resource
	MemberFeeServiceImpl memberFeeServiceImpl;
	@Resource
	TollSend tollSend;

	public void execute(String dayStr,String batchNo) {
		// 1.解析yyyymmdd_xxx(交易所代码)_clientInfoMod.txt
		// 2.发送数据
		logger.info("do TollTask begin");
		try {
			memberFeeServiceImpl.execute(dayStr,batchNo);
			tollSend.send(dayStr,batchNo);
			logger.info("do TollTask end");
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.task_toll_step, e);
		}
	}
}
