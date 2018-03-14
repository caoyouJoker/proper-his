package jdo.cdss;

import java.util.ArrayList;
import java.util.List;



import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class CDSSClientOnServer {

	private static CDSSClientOnServer instance;


	public static CDSSClientOnServer getInstance() {
		if (instance == null) {
			instance = new CDSSClientOnServer();
		}
		return instance;
	}
	
	public HisPojo fireRules5(HisPojo hisPojo) {
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress("http://" + ip + "/services/cdss5?wsdl");
		factory.setServiceClass(ICDSSService5.class); 
		ICDSSService5 icdssService = (ICDSSService5) factory.create();
		System.out.println("Invoking fireRule...");
		hisPojo = icdssService.fireRule5(hisPojo);
		System.out.println("fireRule.result=" + hisPojo);
		return hisPojo;
	}

	public HisPojo fireRules1(HisPojo hisPojo) {
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress("http://" + ip + "/services/cdss1?wsdl");
		factory.setServiceClass(ICDSSService1.class); 
		ICDSSService1 icdssService = (ICDSSService1) factory.create();
		System.out.println("Invoking fireRule...");
		hisPojo = icdssService.fireRule1(hisPojo);
		System.out.println("fireRule.result=" + hisPojo);
		return hisPojo;
	}
	
	public HisPojo fireRules2(HisPojo hisPojo) {
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress("http://" + ip + "/services/cdss2?wsdl");
		factory.setServiceClass(ICDSSService2.class); 
		ICDSSService2 icdssService = (ICDSSService2) factory.create();
		System.out.println("Invoking fireRule...");
		hisPojo = icdssService.fireRule2(hisPojo);
		System.out.println("fireRule.result=" + hisPojo);
		return hisPojo;
	}
	
	public HisPojo fireRules3(HisPojo hisPojo) {
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress("http://" + ip + "/services/cdss3?wsdl");
		factory.setServiceClass(ICDSSService3.class); 
		ICDSSService3 icdssService = (ICDSSService3) factory.create();
		System.out.println("Invoking fireRule...");
		hisPojo = icdssService.fireRule3(hisPojo);
		System.out.println("fireRule.result=" + hisPojo);
		return hisPojo;
	}
	
	public ClpPojo fireRules4(ClpPojo clpPojo) {
		String ip = getWebServicesIp();
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress("http://" + ip + "/services/cdss4?wsdl");
		factory.setServiceClass(ICDSSService4.class); 
		ICDSSService4 icdssService = (ICDSSService4) factory.create();
		System.out.println("Invoking fireRule...");
		clpPojo = icdssService.fireRule4(clpPojo);
		System.out.println("fireRule.result4=" + clpPojo);
		return clpPojo;
		
	}
	
	
	public static void main(String[] args) {
		
		HisPojo hisPojo = new HisPojo();
		hisPojo.setAge(new Integer(1));
		OrderPojo orderPojo = new OrderPojo();
		orderPojo.setOrderCode("2I024001");
		List<OrderPojo> orderPojos = new ArrayList<OrderPojo>();
		orderPojos.add(orderPojo);
			orderPojo = new OrderPojo();
			orderPojo.setOrderCode("2N031012");
			orderPojos.add(orderPojo);
		hisPojo.setOrderPojos(orderPojos);
		
		hisPojo = CDSSClientOnServer.getInstance().fireRules1(hisPojo);
		
		List<AdvicePojo> advicePojos = hisPojo.getAdvicePojos();
		for (AdvicePojo advicePojo : advicePojos) {
			System.out.println(advicePojo.getAdviceText());
		}
	}
	
	
    private TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
    
    private String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "CDSS_SERVICES_IP");
		 return url;
	 }
}
