package com.letv.GrabData;

import java.net.MalformedURLException;

import com.blockchain.service.AccountService;
import com.blockchain.service.customer.CustomerResponse;
import com.blockchain.service.customer.UserInfoSyncRequest;
import com.caucho.hessian.client.HessianProxyFactory;

public class TestHessian {
  public static String url = "http://localhost:8080/leftranservice/remote/accountService";
  public static void  main(String[] args){
      HessianProxyFactory factory = new HessianProxyFactory();
      try {
    	  AccountService accountService = (AccountService) factory.create(AccountService.class, url);
    	  UserInfoSyncRequest request =new UserInfoSyncRequest();
    	  request.setMemCode("567008");
//    	  request.setFundAccountClear(fundAccountClear);("567008");
    	  CustomerResponse user= accountService.syncUserInfo(request);
          System.out.println(user);
      } catch (MalformedURLException e) {
          e.printStackTrace();
      }
  }
}
