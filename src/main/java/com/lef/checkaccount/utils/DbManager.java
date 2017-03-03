package com.lef.checkaccount.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbManager {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void executeSql(List<String> sqlList) {
		if (sqlList == null || sqlList.size() < 1) {
			System.out.println("sqlList is empty");
			return;
		}
		for (String sql : sqlList) {
			jdbcTemplate.execute(sql);
		}
	}

	public void executeSql(String sql) {
		if (StringUtils.isEmpty(sql)) {
			System.out.println("sql is empty");
			return;
		}
		jdbcTemplate.execute(sql);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
