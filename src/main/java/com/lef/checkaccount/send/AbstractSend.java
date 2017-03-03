package com.lef.checkaccount.send;

import javax.annotation.Resource;

import com.blockchain.service.AccountService;
import com.blockchain.service.TxnService;
import com.lef.checkaccount.utils.DbManager;

public abstract class AbstractSend {
	@Resource
	DbManager dbManager;
	@Resource
	AccountService accountServiceClient;
	@Resource
	TxnService txnServiceClient;
}
