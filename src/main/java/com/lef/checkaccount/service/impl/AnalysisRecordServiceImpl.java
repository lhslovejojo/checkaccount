package com.lef.checkaccount.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.lef.checkaccount.entity.AnalysisRecord;
import com.lef.checkaccount.service.AnalysisRecordService;
import com.lef.checkaccount.utils.DbManager;

@Service
public class AnalysisRecordServiceImpl implements AnalysisRecordService {
	@Resource
	DbManager dbManager;

	@Override
	public AnalysisRecord findByDay(String dayStr) {
		// TODO Auto-generated method stub
		String sql = "select * from analysis_record where analysis_day=?";
		List<AnalysisRecord> list = dbManager.getJdbcTemplate().query(sql, new String[] { dayStr }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AnalysisRecord record = new AnalysisRecord();
				record.setId(rs.getLong("id"));
				record.setAnalysisDay(rs.getString("analysis_day"));
				record.setErrorCode(rs.getString("error_code"));
				record.setErrorMsg(rs.getString("error_msg"));
				record.setErrorStep(rs.getInt("error_step"));
				record.setErrorStepDesc(rs.getString("error_step_desc"));
				record.setStatus(rs.getString("status"));
				return record;
			}
		});
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else
			return null;
	}

	public AnalysisRecord saveAnalysisRecord(AnalysisRecord analysisRecord) {
		if (findByDay(analysisRecord.getAnalysisDay()) != null) {
			String sql = "update  analysis_record set status=?,error_step=?,error_step_desc=?,error_msg=?,error_code=? where analysis_day=?";
			dbManager.getJdbcTemplate().update(sql,
					new Object[] { analysisRecord.getStatus(), analysisRecord.getErrorStep(),
							analysisRecord.getErrorStepDesc(), analysisRecord.getErrorMsg(),
							analysisRecord.getErrorCode(), analysisRecord.getAnalysisDay() });
		} else {
			String sql = "insert into analysis_record(analysis_day,status) values (?,?)";
			dbManager.getJdbcTemplate().update(sql,
					new Object[] { analysisRecord.getAnalysisDay(), analysisRecord.getStatus()});
		}
		return findByDay(analysisRecord.getAnalysisDay());
	}

	public void insertAnalysisRecordHis(AnalysisRecord analysisRecord) {
		String sql = "insert into analysis_record_his(analysis_day,status,error_step,error_step_desc,error_msg,error_code,create_time) values (?,?,?,?,?,?,NOW())";
		dbManager.getJdbcTemplate().update(sql,
				new Object[] { analysisRecord.getAnalysisDay(), analysisRecord.getStatus(),
						analysisRecord.getErrorStep(), analysisRecord.getErrorStepDesc(), analysisRecord.getErrorMsg(),
						analysisRecord.getErrorCode() });
	}

	@Override
	public void deleteByDay(String dayStr) {
		String sql = "delete  from analysis_record where analysis_day=?";
		dbManager.getJdbcTemplate().update(sql, new String[] { dayStr });

	}

}
