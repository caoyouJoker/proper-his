package action.ind.stockclient;



import java.net.URL;
import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;
/**
 * <p>
 * Title: webservice公用类
 * </p>
 *
 * <p>
 * Description: webservice公用类
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author fuwj 2013.04.18
 * @version 1.0
 */
public class StockClient {

	
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "WEB_SERVICES_IP");
		 return url;
	 }
	 
    
    public static List<action.ind.stockclient.IndStock> onSearchIndStock() {
    	String ip = getWebServicesIp();
    	String url = "http://"+ip+"/services/stockService" ; 
    	JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
    	factory.setAddress(url); 
        factory.setServiceClass(SpcStockService.class); 
        SpcStockService service = (SpcStockService) factory.create();
        java.util.List<action.ind.stockclient.IndStock> stockList = service.onSearchIndStock();
        return stockList ;
    }
    
	 
}
