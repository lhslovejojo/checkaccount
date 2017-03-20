package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.grabdata.impl.ClearPriceServiceImpl;
import com.lef.checkaccount.send.ClearPriceSend;

@Component
public class SettlePriceTask {
	private static Log logger = LogFactory.getLog(SettlePriceTask.class);
	@Resource
	ClearPriceServiceImpl clearPriceServiceImpl;
	@Resource
	ClearPriceSend clearPriceSend;

	public void execute(String dayStr, String batchNo) {
		// 1.clearPrice.txt
		// 2.发送数据
		try {
			clearPriceServiceImpl.execute(dayStr, batchNo);
			clearPriceSend.send(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.task_settle_price_step, e);
		}
	}
}
