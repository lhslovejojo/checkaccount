package com.lef.checkaccount.grabdata;

import java.io.File;
import java.util.List;

import com.lef.checkaccount.vo.RetVo;

public interface GrabDataService {
    /*
     * 根据DayStr解析某一天的对账文件
     */
	public void execute(String dayStr);
	/**
	 * 解析文件
	 * @param file
	 */
	public void analysis(File file);
	/**
	 * 处理解析的数据
	 * @param list
	 * @return
	 */
	public RetVo handle(List<String[]> list);
}
