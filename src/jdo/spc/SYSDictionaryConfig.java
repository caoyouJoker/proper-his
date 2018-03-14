package jdo.spc;

import java.util.List;

import jdo.sys.client.IDictionaryService;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
/**
 * <p>
 * Title: 物联网与HIS同步
 * </p>
 * 
 * <p>
 * Description: 物联网与HIS同步，读取配置信息
 * </p>
 * 
 * @author zhangyiwu 2013.7.10
 * @version 1.0
 */

public class SYSDictionaryConfig {
	
	public static String getregion(){
		TConfig config = getProp();
		String code =null;
		try{
			code=config.getString("","region_code");
		}catch(Exception e){
		}
		return code;
	}
	
	public static long getTime(){
		
		TConfig config = getProp();
		long time=0;
		try{
			time=Long.parseLong(config.getString("", "Time"));
		}catch(Exception e){
			
		}
		
		return time;
	}
	public static String getpassword(){
		TConfig config = getProp();
		String password=null;
		try{
		password =config.getString("", "Password");
		}catch(Exception e){
			
		}
		return password;
	}

	public static String getUserName(){
		TConfig config = getProp();
		String UserName=null;
		try{
			UserName=config.getString("", "UserName");
		}catch(Exception e){
			
		}
		return UserName;
	}

	public static TConfig getProp() {
		TConfig config=null;
		try{
		 config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		}catch(Exception e){
			
		}
		return config;
	}

	public static String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "WEB_SERVICES_SYS_DIC_IP");
		return url;
	}

	// 得到发生变化的表
	public static List<String> ModifyTable(String agr0, String agr1, String agr2) {
		List<String> result=null;
		try{
		String ip = getWebServicesIp();
		String url = "http://" + ip + "/services/dictionaryService";
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);

		factory.setServiceClass(IDictionaryService.class);
		
		IDictionaryService service = (IDictionaryService) factory.create();
		 result = service.getModifyTable(agr0, agr1, agr2);
		}catch(Exception e){
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "同步失败");
			logparm.setData("RESULT_DESC", "WEBSERVICES没有链接" );
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}

		return result;

	}

	// 得到具体数据
	public static List<String> ModifyInf(String arg0, String arg1, String arg2,
			String table) {
		List<String> result = null;
		try {
			String ip = getWebServicesIp();
			String url = "http://" + ip + "/services/dictionaryService";
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setAddress(url);

			factory.setServiceClass(IDictionaryService.class);
		
			IDictionaryService service = (IDictionaryService) factory.create();
			result = service.getModifyInf(arg0, arg1, arg2, table);
		} catch (Exception e) {
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "同步失败");
			logparm.setData("RESULT_DESC", "WEBSERVICES没有链接");
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);
		}
		return result;
	}

	// 得到数据后确认
	public static String ConfirmedInf(String agr0, String agr1, String arg2,
			String agr3) {
		String result = null;
		try {
			String ip = getWebServicesIp();
			String url = "http://" + ip + "/services/dictionaryService";
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setAddress(url);

			factory.setServiceClass(IDictionaryService.class);
			
			IDictionaryService service = (IDictionaryService) factory.create();
			result = service.confirmedInf(agr0, agr1, arg2, agr3);
		} catch (Exception e) {
			TParm logparm = new TParm();
			logparm.setData("BATCH_CODE", "同步失败");
			logparm.setData("RESULT_DESC", "WEBSERVICES没有链接");
			SYSDictionaryPatchTool.getInstance().OnSaveIndBatchLog(logparm);

		}
		return result;
	}

}
