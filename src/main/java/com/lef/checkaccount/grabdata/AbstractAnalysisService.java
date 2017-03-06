package com.lef.checkaccount.grabdata;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.lef.checkaccount.utils.DbManager;

public abstract class AbstractAnalysisService {
	protected String charset = "GBK";
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

	@Value("${grabdata.maxLine}")
	protected int maxLine = 500;

	public void getFileFromFtp(String dayStr, String fileExpression) {
		// // FTP 下载
		// try {
		// fileExpression = dayStr + fileExpression;
		// FtpUtil.downFileByRegexp(ftpHost, ftpPort, ftpUser, ftpPwd, dayStr,
		// fileExpression, ftpToLocalDir);
		// FtpUtil.downFileByRegexp(ftpHost, ftpPort, ftpUser, ftpPwd, null,
		// fileExpression, ftpToLocalDir);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// throw new RuntimeException(e);
		// }
	}

	public Boolean isAnalysisFile(String fileName, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher match = pattern.matcher(fileName);
		return match.matches();

	}

	public void deleteDb(String tableName) {
		String sql = "delete from " + tableName;
		dbManager.executeSql(sql);
	}

	public void sortFileArrayByName(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File o1, File o2) {
				String o1Name = o1.getName();
				String o2Name = o2.getName();
				return (o1Name.compareTo(o2Name));
			}
		});
	}
}
