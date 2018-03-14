package jdo.cdss;

import net.sf.json.JSONObject;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class CDSSClientOnServer1 {
	private static CDSSClientOnServer1 instance;
	
	public static CDSSClientOnServer1 getInstance() {
		if (instance == null) {
			instance = new CDSSClientOnServer1();
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
	
	public HisPojo fireRules1(HisPojo hisPojo) throws Exception {
		String ip = getWebServicesIp();
//		JaxWsProxyFactoryBean factory = getJaxFactory();
//		factory.setAddress("http://" + ip + "/services/cdss1?wsdl");
//		factory.setServiceClass(ICDSSService1.class); 
//		ICDSSService1 icdssService = (ICDSSService1) factory.create();
//		System.out.println("Invoking fireRule...");
//		hisPojo = icdssService.fireRule1(hisPojo);
//		System.out.println("fireRule.result=" + hisPojo);
//		return hisPojo;
		
		JSONObject json = JSONObject.fromObject(hisPojo);//��java����ת��Ϊjson����  
        String jsonStr = json.toString();//��json����ת��Ϊ�ַ��� 
//		System.out.println("fireReles2- fireRules1-json=="+jsonStr);
		
		String url = "http://" + ip + "/rest/cdss.do?method=fireRules1";
		HttpCMRClientUtil clientUtil = new HttpCMRClientUtil();
		String jsonStrRe = clientUtil.post(url, jsonStr);
		
 
//		System.out.println("����==fireRules1==="+jsonStrRe);
         JSONObject obj =  JSONObject.fromObject(jsonStrRe);//��json�ַ���ת��Ϊjson����  
         hisPojo = (HisPojo)JSONObject.toBean(obj,HisPojo.class);//����json����ת��ΪPerson����  
         
         SysUtil sysUtil = new SysUtil(); 		
 		 hisPojo = sysUtil.parseJsonToHisPojo(hisPojo, obj);
	 		
		return hisPojo;  
		
		
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
