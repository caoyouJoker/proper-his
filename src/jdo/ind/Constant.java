package jdo.ind;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;




/**
 * <p>
 * Title: 药筐绑定Tool
 * </p>
 *
 * <p>
 * Description: 药筐绑定Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company:BlueCore
 * </p>
 *
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public class Constant {
	
	public static  List<NameValuePair> parameters = new ArrayList<NameValuePair>();  
	
	public static final String IPPORT = "http://172.20.10.63:8080/";
	/**
	 * 服务状态请求URL
	 */
	public static final String SERVER_STATUS_URL = IPPORT+"IDILinkerService.svc/Status";
	
	/**
	 * 用户登录URL
	 */
	public static final String LOGIN_URL = IPPORT+"IDILinkerService.svc/User/";
	
	/**
	 * 用户查询URL
	 */
	public static final String FIND_USER_URL = IPPORT+"IDILinkerService.svc/GetUser/";
	
	/**
	 * 药房货位更新URL
	 */
	public static final String CARGO_URL = IPPORT+"IDILinkerService.svc/LabelData/1";
	
	/**
	 * 药筐更新URL
	 */
	public static final String DRUGBASKET_URL = IPPORT+"IDILinkerService.svc/LabelData/2";
	
	/**
	 * 药箱更新URL
	 */
	public static final String MEDICINECHEST_URL = IPPORT+"IDILinkerService.svc/LabelData/3";
	
	/**
	 * 药盒URL 
	 */
	public static final String PCS_URL = IPPORT+"IDILinkerService.svc/LabelData/4";
	
	/**
	 * 获取电子标签URL
	 */
	public static final String LABLE_URL = IPPORT+"IDILinkerService.svc/ResultESLData";
	
	

}
