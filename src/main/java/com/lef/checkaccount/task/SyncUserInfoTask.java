package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.TaskCode;
import com.lef.checkaccount.grabdata.impl.ClientInfoModServiceImpl;
import com.lef.checkaccount.send.SyncUserInfoSend;

@Component
public class SyncUserInfoTask {
	private static Log logger = LogFactory.getLog(SyncUserInfoTask.class);
	@Resource
	ClientInfoModServiceImpl clientInfoModServiceImpl;
	@Resource
	SyncUserInfoSend syncUserInfoSend;
   public void execute(String dayStr,String batchNo)
   {
	  //1.解析yyyymmdd_xxx(交易所代码)_clientInfoMod.txt				
	  //2.发送数据
	   try {
	   clientInfoModServiceImpl.execute(dayStr,batchNo);
	   syncUserInfoSend.send(dayStr,batchNo);
	   }catch (Exception e)
	   {
		   logger.error(e);
		   throw new AnalysisException(TaskCode.analysis_data_error_code,TaskCode.analysis_data_error_001,TaskCode.task_sync_user_step,e);
	   }
   }
}
