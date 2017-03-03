package com.lef.checkaccount.remote;

import org.springframework.stereotype.Service;

import com.lef.checkaccount.vo.RetVo;
@Service("hessianRemoteImpl")
public class HessianRemoteImpl implements HessianRemoteInterface {

	public String sayHello() {
		// TODO Auto-generated method stub
		return "hello";
	}

	public RetVo foo() {
		// TODO Auto-generated method stub
		return RetVo.getSuccessRet();
	}

}
