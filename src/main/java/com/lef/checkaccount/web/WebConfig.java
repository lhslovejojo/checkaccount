package com.lef.checkaccount.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

import com.blockchain.service.AccountService;
import com.blockchain.service.TxnService;

@Configuration
public class WebConfig {
	@Value("${WebConfig.hessianUrl}")
	private String hessianUrl;

	/**
	 * 账户接口调用hessian客户端
	 * 
	 * @return
	 */
	@Bean
	public HessianProxyFactoryBean accountServiceClient() {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl(hessianUrl + "/remote/accountService");
		factory.setServiceInterface(AccountService.class);
		return factory;
	}

	/**
	 * 交易接口调用hessian客户端
	 * 
	 * @return
	 */
	@Bean
	public HessianProxyFactoryBean txnServiceClient() {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl(hessianUrl + "/remote/txnService");
		factory.setServiceInterface(TxnService.class);
		return factory;
	}

}
