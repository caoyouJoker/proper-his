package jdo.spc.spcPatInfoSyncClient;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

/**
 * This class was generated by Apache CXF 2.5.2 2013-03-22T10:05:09.562+08:00
 * Generated source version: 2.5.2
 * 
 */
public final class SpcPatInfoService_SpcPatInfoServiceImplPort_Client {

	// private static final QName SERVICE_NAME = new
	// QName("http://reqinf.spc.jdo/", "SpcPatInfoServiceImplService");

	public SpcPatInfoService_SpcPatInfoServiceImplPort_Client() {
	}

	public static void main(String args[]) throws java.lang.Exception {
		// URL wsdlURL = SpcPatInfoServiceImplService.WSDL_LOCATION;
		// if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
		// File wsdlFile = new File(args[0]);
		// try {
		// if (wsdlFile.exists()) {
		// wsdlURL = wsdlFile.toURI().toURL();
		// } else {
		// wsdlURL = new URL(args[0]);
		// }
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }
		// }

		// SpcPatInfoServiceImplService ss = new
		// SpcPatInfoServiceImplService(wsdlURL, SERVICE_NAME);
		// SpcPatInfoService port = ss.getSpcPatInfoServiceImplPort();
		//        
		// {
		// System.out.println("Invoking onSaveSpcPatInfo...");
		// jdo.spc.spcPatInfoSyncClient.SysPatinfo _onSaveSpcPatInfo_arg0 =
		// null;
		// java.lang.String _onSaveSpcPatInfo__return =
		// port.onSaveSpcPatInfo(_onSaveSpcPatInfo_arg0);
		// System.out.println("onSaveSpcPatInfo.result=" +
		// _onSaveSpcPatInfo__return);
		//
		//
		// }
		//
		// System.exit(0);
	}

	public static String onSaveSpcPatInfo(SysPatinfo _onSaveSpcPatInfo_arg0) {
//		 URL wsdlURL = SpcPatInfoServiceImplService.WSDL_LOCATION;
//		 SpcPatInfoServiceImplService ss = new
//		 SpcPatInfoServiceImplService(wsdlURL, SERVICE_NAME);
//		 SpcPatInfoService port = ss.getSpcPatInfoServiceImplPort();
//		 String _onSaveSpcPatInfo__return =
//		 port.onSaveSpcPatInfo(_onSaveSpcPatInfo_arg0);
//		 return _onSaveSpcPatInfo__return;
		String ip = getWebServicesIp();
		String url = "http://" + ip + "/services/SPCPatInfoSynchWs";
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(SpcPatInfoService.class);
		// factory.getInInterceptors().add(new LoggingInInterceptor());
		SpcPatInfoService service = (SpcPatInfoService) factory.create();
		String _onSaveSpcPatInfo__return = service.onSaveSpcPatInfo(_onSaveSpcPatInfo_arg0);
		return _onSaveSpcPatInfo__return;
	}
	
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "WEB_SERVICES_IP");
		 return url;
	 }

}
