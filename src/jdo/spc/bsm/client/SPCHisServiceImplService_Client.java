package jdo.spc.bsm.client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import com.dongyang.config.TConfig;

public final class SPCHisServiceImplService_Client {
	public SPCHisServiceImplService_Client() {
	}
	/**
	 * 出库信息同步给his
	 * @param xml
	 * @return
	 */
	public static String drugToHisAccount(String xml) {
		String  return_xml = "" ;
		try {
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setAddress(getWebServicesIp());
			System.out.println("sasasasa===="+xml);
			factory.setServiceClass(ServiceSoap.class);
			System.out.println("sasasasa==1=====");
			ServiceSoap service = (ServiceSoap) factory.create();
			System.out.println("sasasasa=====2==");
	       return_xml = service.drugToHisAccount(xml) ;	
	       System.out.println("sasasasa=====3==");
		} catch (Exception e) {
			System.out.println("err21212======="+e.toString());
		}
			
		return return_xml;
	}
	private static  TConfig getProp() {
       TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
       return config;
	}

	 public  static String getWebServicesIp(){
		 TConfig config = getProp() ;
		 String url = config.getString("", "bsmHis.path");
		 return url;
	 }

}
