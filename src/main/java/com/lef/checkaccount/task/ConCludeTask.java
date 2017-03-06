package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.TaskCode;
import com.lef.checkaccount.grabdata.impl.DealInfoServiceImpl;
import com.lef.checkaccount.send.ConCludeSend;

@Component
public class ConCludeTask {
	private static Log logger = LogFactory.getLog(ConCludeTask.class);
	@Resource
	DealInfoServiceImpl dealInfoServiceImpl;
	@Resource
	ConCludeSend conCludeSend;
   public void execute(String dayStr,String batchNo)
   {
	  //1.解析yyyymmdd_xxx(交易所代码)_clientInfoMod.txt				
	  //2.发送数据
	   try {
		   dealInfoServiceImpl.execute(dayStr,batchNo);
		   conCludeSend.send(dayStr,batchNo);
	   }catch (Exception e)
	   {
		   logger.error(e);
		   throw new AnalysisException(TaskCode.analysis_data_error_code,TaskCode.analysis_data_error_001,TaskCode.task_conclude_step,e);
	   }
   }
}
