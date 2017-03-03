package com.lef.checkaccount.grabdata;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.lef.checkaccount.utils.ApplicationBeanUtil;
/**
 * 
 * @author lihongsong
 *
 */
@Component
public class GrabDataServiceFactory {
	private static Map<String,Object> map;
    public static GrabDataService getService(String key)
    {
    	//如果前面是多线程处理，注意这边的线程安全
    	if (map==null)
    	{
    	map=new HashMap<String,Object>();
    	map.put("bankCheck",ApplicationBeanUtil.getBean("bankCheckServiceImpl"));
    	map.put("clearPrice",ApplicationBeanUtil.getBean("clearPriceServiceImpl"));
    	map.put("clientInfoMod",ApplicationBeanUtil.getBean("clientInfoModServiceImpl"));
    	map.put("dealInfo",ApplicationBeanUtil.getBean("dealInfoServiceImpl"));
    	map.put("memberFee",ApplicationBeanUtil.getBean("memberFeeServiceImpl"));
    	map.put("memberPositionDetail",ApplicationBeanUtil.getBean("memberPositionDetailServiceImpl"));
    	}
    	return (GrabDataService)map.get(key);
    }
}
