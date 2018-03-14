package com.javahis.device;

/**
 * 数据中心交互dll封装;
 * @author lixiang
 *
 */
public class NJEapagentDriver {
	
	static {
		System.loadLibrary("NJEapagentDriver"); // 载入dll
	}
	
	/**
	 * 初始化连接DLL
	 * 
	 * @return int 1 成功 0 失败
	 */
	public native static int init();
	
	/**
	 * 注销DLL
	 * 
	 * @return
	 */
	public native static int close();
	
	/**
	 * 调用中心服务
	 * @return  每次调用中心端服务第一步必须调用初始化函数
	 */
	public native static int EapAgent_Init();
	
	/**
	 * 调用中心初始化函数
	 * @return  每次调用中心端服务第二步必须调用复位函数
	 */
	public native static int EapAgent_Reset();
	
	/**
	 * 调用中心设置Action函数
	 * @param action
	 * @return  指定需要调用的action
	 */
	public native static int EapAgent_SetAction(String action);
	
	/**
	 * 调用中心设置Action方法函数
	 * @param actionMethod
	 * @return  指定调用的action方法名。
	 */
	public native static int EapAgent_SetActionMethod(String actionMethod);
	
	/**
	 * 调用中心设置参数值函数
	 * @param paraName
	 * @param paraValue
	 * @return  paraName 为参数名，paraValue为方法值。参数名需要客户端与中心服务端约定，参见接口文档。参数值为字符串或规范Xml
	 */
	public native static int EapAgent_PutParameter(String paraName, String paraValue);
	
	/**
	 * 调用中心发送请求函数
	 * @return  之前步骤完成后，调用该函数发送请求。返回值不为0时说明“请求服务失败”
	 */
	public native static int EapAgent_SendRequest();
	
	/**
	 * 调用中心获取返回状态函数
	 * @param appCode   返回应用
	 * @return 获取返回状态函数，如果返回0则说明调用成功，-1为调用失败;
	 */
	public native static int EapAgent_GetAppCode(EapAppCode appCode);
	
	/**
	 * 调用中心获取返回值函数
	 * @param paraName
	 * @param paraValue
	 * @return paraName 返回值参数名，paraValue返回值。返回值参数需要客户端与中心服务端约定，参见接口文档
	 */
	public native static int EapAgent_GetParameter(String paraName,EapReturnParm paraValue);
	
	/**
	 * 调用中心获取返回错误信息函数
	 * @param briefErrorMessage
	 * @return  GetAppCode返回-1时，可调用该方法获取错误提示信息。
	 */
	public native static int EapAgent_GetBriefErrorMessage(EapErrorMessage briefErrorMessage);
	
	private static void getString(String aa){
		aa="eeeeee";
	}
	
	public static void main(String args[]){
	/*	EapReturnParm c=new EapReturnParm();
		EapAgent_GetParameter("aa",c);
		System.out.println("return para"+c.getParaValue());**/
		/*String bb="abcde";
		getString(bb);
		System.out.println("====bb===="+bb);*/
		
		/**String paraName="王丽";
		EapAgent_PutParameter(paraName,"bbb");
		System.out.println("paraName"+paraName);**/
		
		
	}
	

}


