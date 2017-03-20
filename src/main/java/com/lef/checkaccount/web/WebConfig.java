package com.lef.checkaccount.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.caucho.HessianServiceExporter;

import com.blockchain.service.AccountService;
import com.blockchain.service.TxnService;
import com.lef.checkaccount.remote.HessianRemoteInterface;

@Configuration
public class WebConfig {
	@Autowired
    private HessianRemoteInterface hessianRemoteImpl; 

	@Bean
	public HessianProxyFactoryBean hessianRemote() {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl("http://localhost:8080/helloService");
		factory.setServiceInterface(HessianRemoteInterface.class);
		return factory;
	}
	/**
	 * 账户接口调用hessian客户端
	 * @return
	 */
	@Bean
	public HessianProxyFactoryBean accountServiceClient() {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl("http://localhost:8080/remote/accountService");
		factory.setServiceInterface(AccountService.class);
		return factory;
	}
	/**
	 * 交易接口调用hessian客户端
	 * @return
	 */
	@Bean
	public HessianProxyFactoryBean txnServiceClient() {
		HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
		factory.setServiceUrl("http://localhost:8080/remote/txnService");
		factory.setServiceInterface(TxnService.class);
		return factory;
	}
	@Bean(name = "/helloService") 
	public HessianServiceExporter accountService() {
		HessianServiceExporter exporter = new HessianServiceExporter();
		exporter.setService(hessianRemoteImpl);
		exporter.setServiceInterface(HessianRemoteInterface.class);
		return exporter;
	 } 


}
