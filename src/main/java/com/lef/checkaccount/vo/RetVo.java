package com.lef.checkaccount.vo;

public class RetVo {
	private String code;
	private String msg;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	public RetVo(String code,String msg)
	{
		this.code=code;
		this.msg=msg;
	}
	public static RetVo getSuccessRet()
	{
		return new RetVo("1","SUCCESS");
	}
	public static RetVo getFailRet()
	{
		return new RetVo("0","FAIL");
	}
	public static RetVo getFailRet(String msg)
	{
		return new RetVo("0",msg);
	}

}
