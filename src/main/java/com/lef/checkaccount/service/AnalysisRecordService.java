package com.lef.checkaccount.service;

import com.lef.checkaccount.entity.AnalysisRecord;

public interface AnalysisRecordService {
  public AnalysisRecord findByDay(String dayStr);
  public AnalysisRecord saveAnalysisRecord(AnalysisRecord analysisRecord);
  public void insertAnalysisRecordHis(AnalysisRecord analysisRecord);
  public void deleteByDay(String dayStr);
}
