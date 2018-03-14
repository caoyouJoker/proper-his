package jdo.cdss;

import net.sf.json.JSONObject;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class CDSSClientOnServer6 {
private static CDSSClientOnServer6 instance;
	
	public static CDSSClientOnServer6 getInstance() {
		if (instance == null) {
			instance = new CDSSClientOnServer6();
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
	
	public HisPojo fireRules6(HisPojo hisPojo) throws Exception {
		String ip = getWebServicesIp();
		
		JSONObject json = JSONObject.fromObject(hisPojo);//将java对象转换为json对象  
        String jsonStr = json.toString();//将json对象转换为字符串 
//		System.out.println("- fireRules3-json=="+jsonStr);
		
		String url = "http://" + ip + "/rest/cdss.do?method=fireRules6";
		HttpCMRClientUtil clientUtil = new HttpCMRClientUtil();
		String jsonStrRe = clientUtil.post(url, jsonStr);
		
 
//		System.out.println("出参==fireRules3==="+jsonStrRe);
         JSONObject obj =  JSONObject.fromObject(jsonStrRe);//将json字符串转换为json对象  
         hisPojo = (HisPojo)JSONObject.toBean(obj,HisPojo.class);//将建json对象转换为Person对象  
         
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
