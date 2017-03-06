package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.TaskCode;
import com.lef.checkaccount.grabdata.impl.BankCheckServiceImpl;
import com.lef.checkaccount.send.RechargeSend;

@Component
public class RechargeTask {
	private static Log logger = LogFactory.getLog(RechargeTask.class);
	@Resource
	BankCheckServiceImpl bankCheckServiceImpl;
	@Resource
	RechargeSend rechargeSend;
   public void execute(String dayStr,String batchNo)
   {
	  //1.解析yyyymmdd_xxx(交易所代码)_clientInfoMod.txt				
	  //2.发送数据
	   try {
	   bankCheckServiceImpl.execute(dayStr,batchNo);
	   rechargeSend.send(dayStr,batchNo);
	   }catch (Exception e)
	   {
		   logger.error(e);
		   throw new AnalysisException(TaskCode.analysis_data_error_code,TaskCode.analysis_data_error_001,TaskCode.task_recharge_step,e);
	   }
   }
}
