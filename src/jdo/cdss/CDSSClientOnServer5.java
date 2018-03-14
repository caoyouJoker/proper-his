package jdo.cdss;

import net.sf.json.JSONObject;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class CDSSClientOnServer5 {
	private static CDSSClientOnServer5 instance;
	
	public static CDSSClientOnServer5 getInstance() {
		if (instance == null) {
			instance = new CDSSClientOnServer5();
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
	
	public HisPojo fireRules5(HisPojo hisPojo) throws Exception {
		String ip = getWebServicesIp();
//		JaxWsProxyFactoryBean factory = getJaxFactory();
//		factory.setAddress("http://" + ip + "/services/cdss5?wsdl");
//		factory.setServiceClass(ICDSSService5.class); 
//		ICDSSService5 icdssService = (ICDSSService5) factory.create();
//		System.out.println("Invoking fireRule...");
//		hisPojo = icdssService.fireRule5(hisPojo);
//		System.out.println("fireRule.result=" + hisPojo);
//		return hisPojo;
		
		JSONObject json = JSONObject.fromObject(hisPojo);//��java����ת��Ϊjson����  
        String jsonStr = json.toString();//��json����ת��Ϊ�ַ��� 
//		System.out.println("- fireRules5-json=="+jsonStr);
		
		String url = "http://" + ip + "/rest/cdss.do?method=fireRules5";
		HttpCMRClientUtil clientUtil = new HttpCMRClientUtil();
		String jsonStrRe = clientUtil.post(url, jsonStr);
		
 
//		System.out.println("����==fireRules5==="+jsonStrRe);
         JSONObject obj =  JSONObject.fromObject(jsonStrRe);//��json�ַ���ת��Ϊjson����  
         hisPojo = (HisPojo)JSONObject.toBean(obj,HisPojo.class);//����json����ת��ΪPerson����  
         
         SysUtil sysUtil = new SysUtil(); 		
 		 return hisPojo = sysUtil.parseJsonToHisPojo(hisPojo, obj);
		
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
