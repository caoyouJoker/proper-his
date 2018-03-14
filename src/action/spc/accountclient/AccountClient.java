package action.spc.accountclient;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class AccountClient {

	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "WEB_SERVICES_IP");
		 return url;
	 }

	 public static String onSaveIndAccount(IndAccounts accounts){
		 
		 String ip = getWebServicesIp();
	     String url = "http://"+ip+"/services/indAccountService" ;
	     JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(); 
	     factory.setAddress(url); 
	     
	     factory.setServiceClass(IndAccountService.class); 
	        //factory.getInInterceptors().add(new LoggingInInterceptor()); 
	     IndAccountService service = (IndAccountService) factory.create();
	     String result = service.onSaveIndAccount(accounts);
	     return result ;
	     
    }
}
