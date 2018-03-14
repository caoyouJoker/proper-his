package com.javahis.device;

/**
 * NJSMCardYYDriver.dll(YYReader.dll)封装
 * @author lixiang
 *
 */
public class NJSMCardYYDriver {

	static {
		System.loadLibrary("NJSMCardYYDriver"); // 载入dll
	}
		
	/**
	 * 初始化连接DLL
	 * 
	 * @return int 1 成功 0 失败
	 */
	public native static int init();
	
	/**
	 * 注销DLL
	 * @return
	 */
	public native static int close();
	
	/**
	 * Desfire卡读写医疗认证
	 * @param hReader 操作句柄。为LinkReaderPro的返回值
	 * @return 参照公共返回值说明
	 */
	public native static int YYAuthPro(int hReader);
	
	/**
	 * Desfire卡读取医疗信息
	 * @param hReader  操作句柄。为LinkReaderPro的返回值
	 * @param yy    读出的医疗信息
	 * @return
	 */
	public native static int YYReadData(int hReader,NJYY yy);
	
	/**
	 * Desfire卡写入医疗信息
	 * @param hReader  操作句柄。为LinkReaderPro的返回值
	 * @param yy		写入的医疗信息
	 * @return			参照公共返回值说明。
	 */
	public native static int YYWriteData(int hReader,NJYY yy);
	
}
