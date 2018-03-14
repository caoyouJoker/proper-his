package jdo.cdss;

import net.sf.json.JSONObject;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class CDSSClientOnServer4 {
	private static CDSSClientOnServer4 instance;
	
	public static CDSSClientOnServer4 getInstance() {
		if (instance == null) {
			instance = new CDSSClientOnServer4();
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
	
	public ClpPojo fireRules4(ClpPojo clpPojo) throws Exception {
		String ip = getWebServicesIp();
//		JaxWsProxyFactoryBean factory = getJaxFactory();
//		factory.setAddress("http://" + ip + "/services/cdss4?wsdl");
//		factory.setServiceClass(ICDSSService4.class); 
//		ICDSSService4 icdssService = (ICDSSService4) factory.create();
//		System.out.println("Invoking fireRule...");
//		clpPojo = icdssService.fireRule4(clpPojo);
//		System.out.println("fireRule.result4=" + clpPojo);
//		return clpPojo;
		
		JSONObject json = JSONObject.fromObject(clpPojo);//��java����ת��Ϊjson����  
        String jsonStr = json.toString();//��json����ת��Ϊ�ַ��� 
//		System.out.println("- fireRules4-json=="+jsonStr);
		
		String url = "http://" + ip + "/rest/cdss.do?method=fireRules4";
		HttpCMRClientUtil clientUtil = new HttpCMRClientUtil();
		String jsonStrRe = clientUtil.post(url, jsonStr);
		
 
//		System.out.println("����==fireRules4==="+jsonStrRe);
         JSONObject obj =  JSONObject.fromObject(jsonStrRe);//��json�ַ���ת��Ϊjson����  
         clpPojo = (ClpPojo)JSONObject.toBean(obj,ClpPojo.class);//����json����ת��ΪPerson����  
         
         SysUtil sysUtil = new SysUtil(); 		
         clpPojo = sysUtil.parseJsonToClpPojo(clpPojo, obj);
		
 		return clpPojo;
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
