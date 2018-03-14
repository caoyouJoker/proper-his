package jdo.cdss;

import java.net.URL;

import javax.xml.namespace.QName;

public class CDSSClient {

	private static CDSSClient instance;

//	private static final QName SERVICE_NAME = new QName(
//			"http://service.zhangp.com/", "CDSSImplService");
	
	private static final QName SERVICE_NAME5 = new QName(
			"http://service.zhangp.com/", "CDSS5ImplService");
	
	private static final QName SERVICE_NAME1 = new QName(
			"http://service.zhangp.com/", "CDSS1ImplService");
	private static final QName SERVICE_NAME2 = new QName(
			"http://service.zhangp.com/", "CDSS2ImplService");
	private static final QName SERVICE_NAME3 = new QName(
			"http://service.zhangp.com/", "CDSS3ImplService");
	private static final QName SERVICE_NAME4 = new QName(
			"http://service.zhangp.com/", "CDSS4ImplService");
	

	public static CDSSClient getInstance() {
		if (instance == null) {
			instance = new CDSSClient();
		}
		return instance;
	}
	
	public HisPojo fireRules5(HisPojo hisPojo) {
		URL wsdlURL = CDSS1ImplService.WSDL_LOCATION;

		CDSS5ImplService ss = new CDSS5ImplService(wsdlURL, SERVICE_NAME5);
		ICDSSService5 port = ss.getCDSS5ImplPort();

		System.out.println("Invoking fireRule...");
		hisPojo = port.fireRule5(hisPojo);
		System.out.println("fireRule.result5=" + hisPojo);

		return hisPojo;
	}

	public HisPojo fireRules1(HisPojo hisPojo) {
		URL wsdlURL = CDSS1ImplService.WSDL_LOCATION;

		CDSS1ImplService ss = new CDSS1ImplService(wsdlURL, SERVICE_NAME1);
		ICDSSService1 port = ss.getCDSS1ImplPort();

		System.out.println("Invoking fireRule...");
		hisPojo = port.fireRule1(hisPojo);
		System.out.println("fireRule.result1=" + hisPojo);

		return hisPojo;
	}
	
	public HisPojo fireRules2(HisPojo hisPojo) {
		URL wsdlURL = CDSS2ImplService.WSDL_LOCATION;
		
		CDSS2ImplService ss = new CDSS2ImplService(wsdlURL, SERVICE_NAME2);
		ICDSSService2 port = ss.getCDSS2ImplPort();
		
		System.out.println("Invoking fireRule...");
		hisPojo = port.fireRule2(hisPojo);
		System.out.println("fireRule.result2=" + hisPojo);
		
		return hisPojo;
	}
	
	public HisPojo fireRules3(HisPojo hisPojo) {
		URL wsdlURL = CDSS3ImplService.WSDL_LOCATION;
		
		CDSS3ImplService ss = new CDSS3ImplService(wsdlURL, SERVICE_NAME3);
		ICDSSService3 port = ss.getCDSS3ImplPort();
		
		System.out.println("Invoking fireRule...");
		hisPojo = port.fireRule3(hisPojo);
		System.out.println("fireRule.result3=" + hisPojo);
		
		return hisPojo;
	}
	
	public ClpPojo fireRules4(ClpPojo clpPojo){
		URL wsdlURL = CDSS4ImplService.WSDL_LOCATION;
		
		CDSS4ImplService ss = new CDSS4ImplService(wsdlURL, SERVICE_NAME4);
		ICDSSService4 port = ss.getCDSS4ImplPort();
		System.out.println("Invoking fireRule...");
		clpPojo = port.fireRule4(clpPojo);
		System.out.println("fireRule.result4=" + clpPojo);
		return clpPojo;
	}

}
