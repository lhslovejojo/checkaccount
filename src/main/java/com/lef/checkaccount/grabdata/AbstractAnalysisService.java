package com.lef.checkaccount.grabdata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.lef.checkaccount.utils.DbManager;

public abstract class AbstractAnalysisService {
	@Resource
	protected DbManager dbManager;
	@Value("${ftp.host}")
	String ftpHost;
	@Value("${ftp.port}")
	int ftpPort;
	@Value("${ftp.user}")
	String ftpUser;
	@Value("${ftp.pwd}")
	String ftpPwd;
	@Value("${ftp.toLocalDir}")
	protected String ftpToLocalDir;

	public void getFileFromFtp(String dayStr, String fileExpression) {
//		// FTP 下载
//		try {
//			fileExpression = dayStr + fileExpression;
//			FtpUtil.downFileByRegexp(ftpHost, ftpPort, ftpUser, ftpPwd, dayStr, fileExpression, ftpToLocalDir);
//			FtpUtil.downFileByRegexp(ftpHost, ftpPort, ftpUser, ftpPwd, null, fileExpression, ftpToLocalDir);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			throw new RuntimeException(e);
//		}
	}

	public Boolean isAnalysisFile(String fileName, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher match = pattern.matcher(fileName);
		return match.matches();

	}
	public void deleteDb(String tableName)
	{
			String sql="delete from "+tableName;
			dbManager.executeSql(sql);
	}
}
