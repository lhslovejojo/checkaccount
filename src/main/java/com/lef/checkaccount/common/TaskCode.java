package com.lef.checkaccount.common;

import java.util.HashMap;
import java.util.Map;

public class TaskCode {
 public static final int task_sync_user_step=1;
 public static final int task_recharge_step=2;
 public static final int task_conclude_step=3;
 public static final int task_takenow_step=4;
 public static final int task_toll_step=5;
 public static final Map<Integer,String> task_step_dec_map=new HashMap<Integer,String>();
 public static final String analysis_data_error_code="001";
 public static final String analysis_data_error_001="文件解析失败";
 public static final String task_status_doing="doing";
 public static final String task_status_success="success";
 public static final String task_status_fail="fail";
 static 
 {
	 task_step_dec_map.put(task_sync_user_step, "同步交易所会员信息") ;
	 task_step_dec_map.put(task_recharge_step, "同步客户入金信息") ;
	 task_step_dec_map.put(task_conclude_step, "同步客户成交信息") ;
	 task_step_dec_map.put(task_takenow_step, "同步客户出金信息") ;
	 task_step_dec_map.put(task_toll_step, "同步收费单信息") ;
	 
 }
}
