package com.lef.checkaccount.task;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.lef.checkaccount.Exception.AnalysisException;
import com.lef.checkaccount.common.Constants;
import com.lef.checkaccount.entity.AnalysisRecord;
import com.lef.checkaccount.service.AnalysisRecordService;
import com.lef.checkaccount.utils.DateUtil;
import com.lef.checkaccount.utils.DbManager;
import com.lef.checkaccount.vo.RetVo;

@Component
@EnableScheduling
public class MainHandlerTask {
	private static Log logger = LogFactory.getLog(MainHandlerTask.class);
	@Resource
	SyncUserInfoTask syncUserInfoTask;
	@Resource
	RechargeTask rechargeTask;
	@Resource
	ConCludeTask conCludeTask;
	@Resource
	TakeNowTask takeNowTask;
	@Resource
	TollTask tollTask;
	@Resource
	PositionTask positionTask;
	@Resource
	SettlePriceTask settlePriceTask;
	@Resource
	DbManager dbManager;
	@Resource
	AnalysisRecordService analysisRecordService;
	
	public RetVo execute(String dayStr) {
		int step = 1;
		AnalysisRecord analysisRecord = analysisRecordService.findByDay(dayStr);
		if (analysisRecord != null && analysisRecord.getStatus().equals(Constants.task_status_doing)) {
			return RetVo.getDoingRet(dayStr + "日期的文件处理中，请稍后");
		}
		if (analysisRecord != null && analysisRecord.getStatus().equals(Constants.task_status_success)) {
			return RetVo.getSuccessRet();
		}
		if (analysisRecord != null) {
			step = (analysisRecord.getErrorStep() != null && analysisRecord.getErrorStep()>0)? analysisRecord.getErrorStep() : 1;
			analysisRecord.setErrorStep(null);
			analysisRecord.setErrorCode(null);
			analysisRecord.setErrorMsg(null);
		}
		if (analysisRecord == null) {
			analysisRecord = new AnalysisRecord();
		}
		analysisRecord.setAnalysisDay(dayStr);
		analysisRecord.setStatus(Constants.task_status_doing);
		String batchNo=DateUtil.DateToString(new Date(), "yyyyMMddHHmmss");
		logger.info("begin analysis batch-no:"+batchNo);
		analysisRecord.setAnalysisBatchNo(batchNo);
		analysisRecord = analysisRecordService.saveAnalysisRecord(analysisRecord);
		logger.info("step:"+step);
		try {
			switch (step) {
			case 1:
				syncUserInfoTask.execute(dayStr,batchNo); // 同步用户注册信息
			case 2:
				rechargeTask.execute(dayStr,batchNo); // 入金
			case 3:
				conCludeTask.execute(dayStr,batchNo); // 客户成交单
			case 4:
				takeNowTask.execute(dayStr,batchNo); // 出金
			case 5:
				tollTask.execute(dayStr,batchNo); // 交易所收费单
			case 6:
				positionTask.execute(dayStr,batchNo); // 持仓清算文件
			case 7:
				settlePriceTask.execute(dayStr,batchNo); // 同步结算价文件
			default:
				break;
			}
			analysisRecord.setStatus(Constants.task_status_success);
			analysisRecordService.saveAnalysisRecord(analysisRecord);
			logger.info("end analysis batch-no:"+batchNo);
			return RetVo.getSuccessRet();
		} catch (AnalysisException e) {
			 saveException(analysisRecord, e);
			 return RetVo.getFailRet(Constants.task_step_dec_map.get(e.getErrorTask())+"失败");
		} finally {
			analysisRecord = analysisRecordService.findByDay(dayStr);
			analysisRecordService.insertAnalysisRecordHis(analysisRecord);
		}
	}
	public RetVo execute(String dayStr,Boolean reLoad) {
		if (reLoad!=null && reLoad)
		{
			analysisRecordService.deleteByDay(dayStr);
		}
		return execute(dayStr);
	}
	public AnalysisRecord saveException(AnalysisRecord analysisRecord, AnalysisException e) {
		analysisRecord.setErrorCode(e.getRetCd());
		analysisRecord.setErrorStep(e.getErrorTask());
		analysisRecord.setErrorStepDesc(Constants.task_step_dec_map.get(e.getErrorTask()));
		analysisRecord.setErrorMsg(e.getMsgDes());
		analysisRecord.setStatus(Constants.task_status_fail);
		return analysisRecordService.saveAnalysisRecord(analysisRecord);
	}
}
