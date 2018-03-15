package jdo.label;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.dongyang.config.TConfig;




/**
 * <p>
 * Title: ҩ���Tool
 * </p>
 *
 * <p>
 * Description: ҩ���Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public class Constant {
	
	public static  List<NameValuePair> parameters = new ArrayList<NameValuePair>();  
	
	// <-------- identity by shendr 20131121
	//	public static final String IPPORT = "http://172.20.10.63:8081/";
	public static final String IPPORT = getWebServicesIp();
	// -------->
	//public static final String IPPORT = "http://172.20.168.132:8081/";
	/**
	 * ��վ����URL
	 */
	public static final String APPREGION_URL = IPPORT+"IdIlinkerService.svc/APRegion";
	
	/**
	 * ��ǩ�趨URL
	 */
	public static final String APTAG_URL = IPPORT+"IdIlinkerService.svc/APTag";
	
	/**
	 * ���Ҫ�������ݵı�ǩ�����Ƿ��ӦURL
	 */
	public static final String CHECKREGIONLABEL_URL = IPPORT+"IDILinkerService.svc/CheckRegionLabel/";
	
	/**
	 * ���ͱ�ǩ��ʾ����URL
	 */
	public static final String LABELDATA_URL = IPPORT+"IDILinkerService.svc/LabelData/1";
	
	/**
	 * ͨ���¼��Ų�ѯ���ͽ��URL
	 */
	public static final String SENDRESULT_URL = IPPORT+"IDILinkerService.svc/SendResult/ObjectId";
	
	/**
	 * ��ѯ�����»�վ�ź�IPURL
	 */
	public static final String GETREGIONAP_URL = IPPORT+"IDILinkerService.svc/GetRegionAP/RegionId";
	
	/**
	 *��ѯ������ȫ����ǩ���һ��ͨѶ״̬URL 
	 */
	public static final String GETREGIONLABEL_URL = IPPORT+"IDILinkerService.svc/GetRegionLabel/RegionId";
	
	/**
	 * ��ѯ������ȫ����ǩ���һ��ͨѶ״̬URL
	 */
	public static final String GETLABEL_URL = IPPORT+"IDILinkerService.svc/GetLabel/LabelNo";
	
	// <-------- identity by shendr 20131121 ���ӱ�ǩ������IP��Ϊ��������ʽ
	/**
	 * ��ȡ�����ļ�
	 * @author shendr
	 */
	public static TConfig getProp() {
		TConfig config=null;
		try{
		 config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		}catch(Exception e){
			
		}
		return config;
	}
	
	/**
	 * ��ȡ�����ļ��еĵ��ӱ�ǩ������IP
	 * @return
	 */
	public static String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "WEB_SERVICES_LABELIP");
		return url;
	}
	// -------->

}