package jdo.cdss;

import net.sf.json.JSONObject;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;

public class CDSSClientOnServer7 {
private static CDSSClientOnServer7 instance;
	
	public static CDSSClientOnServer7 getInstance() {
		if (instance == null) {
			instance = new CDSSClientOnServer7();
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
	
	public HisPojo fireRules7(HisPojo hisPojo) throws Exception {
		String ip = getWebServicesIp();
//		System.out.println("-ip=="+ip);
		JSONObject json = JSONObject.fromObject(hisPojo);//��java����ת��Ϊjson����  
        String jsonStr = json.toString();//��json����ת��Ϊ�ַ��� 
//		System.out.println("- fireRules7-json=="+jsonStr);
		
		String url = "http://" + ip + "/rest/cdss.do?method=fireRules7";
		HttpCMRClientUtil clientUtil = new HttpCMRClientUtil();
		String jsonStrRe = clientUtil.post(url, jsonStr);
		
 
//		System.out.println("����==fireRules7==="+jsonStrRe);
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
