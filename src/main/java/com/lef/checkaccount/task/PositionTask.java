package com.lef.checkaccount.task;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.grabdata.impl.MemberPositionDetailServiceImpl;
import com.lef.checkaccount.send.PositionSend;

/**
 * 持仓明细推送任务
 * 
 * @author lihongsong
 *
 */
@Component
public class PositionTask {
	private static Log logger = LogFactory.getLog(PositionTask.class);
	@Resource
	MemberPositionDetailServiceImpl memberPositionDetailServiceImpl;
	@Resource
	PositionSend positionSend;

	public void execute(String dayStr, String batchNo) {
		// 1.解析yyyymmdd_xxx(交易所代码)_clientInfoMod.txt
		// 2.发送数据
		try {
			memberPositionDetailServiceImpl.execute(dayStr, batchNo);
			positionSend.send(dayStr, batchNo);
		} catch (Exception e) {
			logger.error(e);
			throw new AnalysisException(Constants.analysis_data_error_code, Constants.analysis_data_error_001,
					Constants.position_toll_step, e);
		}
	}
}
