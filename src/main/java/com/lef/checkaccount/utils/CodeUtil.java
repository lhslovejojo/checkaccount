package com.lef.checkaccount.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CodeUtil {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Integer getNextNo(String type) {
			return jdbcTemplate.queryForObject("SELECT  nextval(?)", Integer.class, type);
	}
	public String getSysRequestId(String type)
	{
		StringBuffer requestId=new StringBuffer();
		int tempInt=getNextNo(type);
	    String tempIntStr= String.valueOf(tempInt);
	    int tempLength= tempIntStr.length();
	    if (tempLength<12)
	    {
	    for (int i=0;i<12-tempLength;i++)
	    {
	    	requestId.append("0");
	    }
	    }
	    requestId.append(tempIntStr);
	    return requestId.toString();
	}

}
