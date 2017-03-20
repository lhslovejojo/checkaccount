package com.lef.checkaccount.common;

import java.util.HashMap;
import java.util.Map;

public class Constants {
 public static final int task_sync_user_step=1;
 public static final int task_recharge_step=2;
 public static final int task_conclude_step=3;
 public static final int task_takenow_step=4;
 public static final int task_toll_step=5;
 public static final int position_toll_step=6;
 public static final int task_settle_price_step=7;
 public static final Map<Integer,String> task_step_dec_map=new HashMap<Integer,String>();
 public static final String analysis_data_error_code="001";
 public static final String analysis_data_error_001="文件解析失败";
 public static final String insert_data_error_code="002";
 public static final String insert_data_error_msg="插入数据库失败";
 public static final String find_data_fromdb_error_code="003";
 public static final String find_data_fromdb_error_msg="从数据查询数据失败";
 public static final String send_data_tohessian_error_code="004";
 public static final String send_data_tohessian_error_msg="网络发送数据失败";
 public static final String task_status_doing="doing";
 public static final String task_status_success="success";
 public static final String task_status_fail="fail";
 public static final String code_recharge_type="recharge_request";  //获取入金requestId
 public static final String code_takenow_type="takenow_request";  //获取出金requestId
 public static final String code_conclude_type="conclude_request";  //获取成交requestId
 public static final String code_toll_type="toll_request";  //获取交易所收费requestId
 public static final String code_position_type="position_request";  //获取持仓明细requestId
 
 static 
 {
	 task_step_dec_map.put(task_sync_user_step, "同步交易所会员信息") ;
	 task_step_dec_map.put(task_recharge_step, "同步客户入金信息") ;
	 task_step_dec_map.put(task_conclude_step, "同步客户成交信息") ;
	 task_step_dec_map.put(task_takenow_step, "同步客户出金信息") ;
	 task_step_dec_map.put(task_toll_step, "同步收费单信息") ;
	 task_step_dec_map.put(position_toll_step, "同步持仓信息") ;
	 task_step_dec_map.put(task_settle_price_step, "同步结算价信息") ;
	 
 }
}
