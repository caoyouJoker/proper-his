package jdo.reg.client;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class ServiceForTXYYSoapClient {
	
	public static ServiceForTXYYSoapClient instance;
	
	public static ServiceForTXYYSoapClient getInstance() {
		if (instance == null) {
			instance = new ServiceForTXYYSoapClient();
		}
		return instance;
	}
	
	private static JaxWsProxyFactoryBean jaxFactory;
	
	private static JaxWsProxyFactoryBean getJaxFactory(){
		if(jaxFactory == null){
			jaxFactory = new JaxWsProxyFactoryBean();
		}
		return jaxFactory;
	}
	
	public String getQyiPayData(String sDate,String eDate){
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = getJaxFactory();
//		factory.setAddress("http://" + ip + "/services/cdss1?wsdl"); 
		factory.setAddress("http://" + ip + "/WebService/ServiceForTXYY.asmx?WSDL");
		factory.setServiceClass(ServiceForTXYYSoap.class);
		ServiceForTXYYSoap service = (ServiceForTXYYSoap) factory.create();
		System.out.println("Invoking fireRule...");
		String json = service.getQyiPayData(sDate, eDate);
		System.out.println("Êä³ö½á¹ûjsons===="+json);
		return json;
		
	}
	
	 public TConfig getProp() {
	        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
	        return config;
		}
	
	 public String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "QE_SERVICES_IP");
		 return url;
	 }

}
