package com.lef.checkaccount.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lef.checkaccount.task.MainHandlerTask;
import com.lef.checkaccount.utils.DateUtil;
import com.lef.checkaccount.vo.RetVo;

@Controller
public class GrabDataController {
	@Resource
	MainHandlerTask mainHandlerTask;

	@RequestMapping(value = "/grabData")
	public @ResponseBody RetVo grabData(@RequestParam(value = "dayStr") String dayStr,
			@RequestParam(value = "reLoad", required = false) Boolean reLoad) {
		if (DateUtil.isValidDate(dayStr, "yyyyMMdd")) {
			if (reLoad != null && reLoad) {
				return mainHandlerTask.execute(dayStr, reLoad);
			} else {
				return mainHandlerTask.execute(dayStr);
			}
		} else {
			return RetVo.getFailRet("日期参数格式有误");
		}
	}

	@RequestMapping(value = "/toGrabData")
	public String toGrabData() {
		return "grabData";
	}

}
