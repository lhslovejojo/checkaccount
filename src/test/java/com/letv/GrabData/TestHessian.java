package com.letv.GrabData;

import java.net.MalformedURLException;

import com.blockchain.service.QueryService;
import com.blockchain.service.page.PageResponse;
import com.blockchain.service.query.UserRequest;
import com.blockchain.service.query.UserResponse;
import com.caucho.hessian.client.HessianProxyFactory;

public class TestHessian {
  public static String url = "http://127.0.0.1:8080/remote/queryService";
  public static void  main(String[] args){
      HessianProxyFactory factory = new HessianProxyFactory();
      try {
    	  QueryService queryService = (QueryService) factory.create(QueryService.class, url);
    	  UserRequest request =new UserRequest();
    	  request.setMemCode("567008");
//    	  request.setFundAccountClear(fundAccountClear);("567008");
    	  PageResponse<UserResponse> users= queryService.queryUser(request);
          System.out.println(users);
      } catch (MalformedURLException e) {
          e.printStackTrace();
      }
  }
}
