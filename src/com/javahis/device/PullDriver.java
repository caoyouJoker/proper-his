package com.javahis.device;

import java.util.Hashtable;

import com.dongyang.data.TParm;

/**
 * 
 * 门禁驱动接口类功能
 * 
 * @author lix
 * 
 */
public class PullDriver {
	public static boolean loadFlg = true; // 有无Load Dll
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // 错误讯息

	public PullDriver() {

	}

	static {
		try {
			System.loadLibrary("PullDriver"); // 载入dll
			loadFlg = true;
			// 加载错误消息;
			setErrorMessage();
		} catch (Throwable ex) {
			loadFlg = false;
		}
	}

	/**
	 * 初始化加载dll
	 * 
	 * @return int
	 */
	public native static int init();

	/**
	 * 注销dll
	 * 
	 * @return int
	 */
	public native static int free();

	/**
	 * 连接设备，连接成功后返回连接句柄
	 * 
	 * @param Parameters
	 * @return
	 */
	public native static int Connect(final String Parameters);

	/**
	 * 断开与设备的连接
	 * 
	 * @param handle
	 */
	public native static void Disconnect(int Handle);

	/**
	 * 设置控制器参数，例如设备号、门磁类型、锁驱动时间、读卡间隔等。
	 * 
	 * @param handle
	 * @param itemValues
	 * @return
	 */
	public native static int SetDeviceParam(int Handle, final String ItemValues);

	/**
	 * 读取控制器参数，例如设备号、门磁类型、锁驱动时间、读卡间隔等。
	 * 
	 * @param handle
	 * @param buffer
	 * @param bufferSize
	 * @param items
	 * @return
	 */
	public native static int GetDeviceParam(int Handle, String Buffer, int bufferSize,
			final String items);

	/**
	 * 控制控制器动作
	 * 
	 * @param Handle
	 * @param OperationID
	 * @param Param1
	 * @param Param2
	 * @param Param3
	 * @param Param4
	 * @param Options
	 * @return
	 */
	public native static int ControlDevice(int Handle, int OperationID, int Param1,
			int Param2, int Param3, int Param4, final String Options);

	/**
	 * 设置数据到设备,用于设置时间段、用户信息、假日设置、等数据，数据可以是一条记录，也可以是多条记录。
	 * 
	 * @param handle
	 * @param TableName
	 * @param Data
	 * @param Options
	 * @return
	 */
	public native static int SetDeviceData(int handle, final String TableName,
			final String Data, final String Options);

	/**
	 * 从设备读取数据,用于读取刷卡记录、时间段、用户信息、假日设置、等数据，数据可以是一条记录，也可以是多条记录。
	 * 
	 * @param handle
	 * @param Buffer
	 * @param BufferSize
	 * @param TableName
	 * @param FieldNames
	 * @param Filter
	 * @param Options
	 * @return  
	 */
	public native static int GetDeviceData(int handle, byte[] Buffer,
			int BufferSize, final String TableName, final String FieldNames,
			final String Filter, final String Options);

	/**
	 * 读取设备中的记录总数信息，返回指定数据的记录条数。
	 * 
	 * @param Handle
	 * @param TableName
	 * @param Filter
	 * @param Options
	 * @return
	 */
	public native static int GetDeviceDataCount(int Handle, final String TableName,
			final String Filter, final String Options);

	/**
	 * 删除设备中的数据，例如用户信息、时间段等数据。
	 * 
	 * @param handle
	 * @param TableName
	 * @param Data
	 * @param Options
	 * @return
	 */
	public native static int DeleteDeviceData(int handle, final String TableName,
			final String Data, final String Options);

	/**
	 * 实时获取设备事件记录。
	 * 
	 * @param handle
	 * @param Buffer
	 * @param BufferSize
	 * @return
	 */
	public native static int GetRTLog(int handle, byte[] Buffer, int BufferSize);

	/**
	 * 搜索局域网内的门禁控制器。
	 * 
	 * @param CommType
	 * @return
	 */
	public native static int SearchDevice(String CommType, String Address,
			byte[] Buffer);

	/**
	 * UDP广播方式修改控制器IP地址。
	 * 
	 * @param CommType
	 * @param Address
	 * @param Buffer
	 * @return
	 */
	public native static int ModifyIPAddress(String CommType, String Address,
			String Buffer);

	/**
	 * 获取错误ID， 返回失败时可通过个函数获取失败ID
	 * 
	 * @return
	 */
	public native static int PullLastError();

	/**
	 * 将文件从PC传送到设备。主要应用于将升级文件传到设备，升级文件名称“emfw.cfg”。
	 * 
	 * @param Handle
	 * @param FileName
	 * @param Buffer
	 * @param BufferSize
	 * @param Options
	 * @return
	 */
	public native static int SetDeviceFileData(int Handle, final String FileName,
			final String Buffer, int BufferSize, final String Options);

	/**
	 * 从设备获取文件到PC。可从设备获取用户信息文件、事件记录文件等。
	 * 
	 * @param Handle
	 * @param Buffer
	 * @param BufferSize
	 * @param FileName
	 * @param Options
	 * @return
	 */
	public native static int GetDeviceFileData(int Handle, String Buffer,
			int BufferSize, final String FileName, final String Options);

	/**
	 * 错误消息;
	 */
	static void setErrorMessage() {
		h.put(new Integer(-1), "命令发送失败");
		h.put(new Integer(-2), "命令没有回应");
		h.put(new Integer(-3), "需要的缓存不足");
		h.put(new Integer(-4), "解压失败");
		h.put(new Integer(-5), "读取数据长度不对");
		h.put(new Integer(-6), "解压的长度和期望的长度不一致");
		h.put(new Integer(-7), "命令重复");
		h.put(new Integer(-8), "连接尚未授权");
		h.put(new Integer(-9), "数据错误，CRC校验失败");
		h.put(new Integer(-10), "数据错误，PullSDK无法解析");
		h.put(new Integer(-11), "数据参数错误");
		h.put(new Integer(-12), "命令执行错误");
		h.put(new Integer(-13), "命令错误，没有此命令");
		h.put(new Integer(-14), "通迅密码错误");
		h.put(new Integer(-15), "写文件失败");
		h.put(new Integer(-16), "读文件失败");
		h.put(new Integer(-17), "文件不存在");
		h.put(new Integer(-99), "未知错误");
		h.put(new Integer(-100), "表结构不存在");
		h.put(new Integer(-101), "表结构中，条件字段不存在");
		h.put(new Integer(-102), "字段总数不一致");
		h.put(new Integer(-103), "字段排序不一致");
		h.put(new Integer(-104), "实时事件数据错误");
		h.put(new Integer(-105), "解析数据时，数据错误");
		h.put(new Integer(-106), "数据溢出，下发数据超出4M");
		h.put(new Integer(-107), "获取表结构失败");
		h.put(new Integer(-108), "无效OPTIONS选项");
		h.put(new Integer(-201), "LoadLibrary失败");
		h.put(new Integer(-202), "调用接口失败");
		h.put(new Integer(-203), "通讯初始化失败");
		h.put(new Integer(-301), "获取TCP/IP版本失败");
		h.put(new Integer(-302), "错误版本号");
		h.put(new Integer(-303), "获取协议类型失败");
		h.put(new Integer(-304), "无效SOCKET");
		h.put(new Integer(-305), "SOCKET错误");
		h.put(new Integer(-306), "HOST错误");
	}
	
	// 错误码 对照表
	private static String getErrorMsg(int error) {
		
		//System.out.println("=====error======="+error);
		String msg = "";
		if (h.containsKey(new Integer(error)))
			msg = (String) h.get(new Integer(error));
		else
			msg = "例外错误";
		return msg;
	}
	
	/**
	 * 单元测试
	 * @param args
	 */ 
	public static void main(String args[]) {
		TParm result = new TParm();
		//1.PullDriver初始化

		//2.加载dll
		int i=PullDriver.init();
		System.out.println("========load========="+i);				
		
		//3.连接服务
		String params = "protocol=TCP,ipaddress=192.168.1.5,port=4370,timeout=2000,passwd=";		
	//	String params = "protocol=TCP,ipaddress=192.168.1.5,port=4370,timeout=2000,passwd=";
	//	String params = "protocol=TCP,ipaddress=192.168.0.201,port=4370,timeout=2000,passwd=";
		int rtn1=PullDriver.Connect(params);					
		System.out.println("=====PullDriver rtn1===="+rtn1);							
		if(rtn1<=0){
			result.setErr(-1, PullDriver.getErrorMsg(rtn1));
			System.out.println("=====有错误====="+PullDriver.getErrorMsg(rtn1));
		}
		
		
		//5.开门操作     日志正常，但设备正常开  (成功)
    	int ret = 0;
		int operid = 1;
		int doorid = 1;
		int outputadr = 1;
		int doorstate = 6;
		ret=PullDriver.ControlDevice(rtn1, operid, doorid, outputadr, doorstate, 0, "");
		System.out.println("=======开门操作ret========"+ret);
		if(ret<0){
			result.setErr(-1, PullDriver.getErrorMsg(ret));		
			System.out.println("=====有错误====="+PullDriver.getErrorMsg(ret));
		}
		
	    //4.取日志  测试通过     (启动个监听程序时时取记录)
		//2012-10-23 15:34:08,33685762,33685506,0,255,0,200
		//2012-11-01 11:16:07,33685762,33685506,0,255,0,200
	  /*  byte[] data = new byte[256];
		int rtn2=PullDriver.GetRTLog(rtn1, data, 256);
		if(rtn1<0){
			result.setErr(-1, PullDriver.getErrorMsg(rtn1));
			System.out.println("=====有错误====="+rtn1);
		}
		String strData = byte2Str(data);
		System.out.println("=====strData======="+strData);
			 */
	
		
		//PullDriver.GetDeviceDataCount(Handle, "user", Filter, Options)
		
		//6.GetDeviceData  从设备读取数据,用于读取刷卡记录
		//解析数据时，数据错误
	/*	byte[] buffer = new byte[BUFFERSIZE];
		String devtablename = "user";
		String str = "*";
	    String devdatfilter = "";
		String options = "";

		int rtn6=PullDriver.GetDeviceData(rtn1, buffer, BUFFERSIZE, devtablename, str, devdatfilter, options);
		System.out.println("=====返回数据======"+rtn6);
		System.out.println("============"+buffer);
			if(rtn6<0){
				result.setErr(-1, PullDriver.getErrorMsg(rtn6));
				System.out.println("=====有错误====="+PullDriver.getErrorMsg(rtn6));
			}
		String strData = byte2Str(buffer);
		System.out.println("=====strData======="+strData);	int BUFFERSIZE = 10 * 1024 * 1024;*/
	
		
		
		//7.搜索局域网内的门禁控制器 (成功)
		//MAC=00:17:61:10:08:33,IP=192.168.1.3,NetMask=255.255.255.0,GATEIPAddress=192.168.1.255,SN=4602011070099,Device=inBIO460,Ver=AC Ver 5.0.8 Aug  8 2011
		//int ret7 = 0;
/*	    String udp = "UDP";
		String adr = "255.255.255.255";
		byte[] buffer = new byte[64 * 1024];
		int ret7=pullDrv.SearchDevice(udp, adr, buffer);
		
		//System.out.println("=====返回数据======"+ret7);
		if(ret7<0){
			result.setErr(-1, PullDriver.getErrorMsg(ret7));
			System.out.println("=====有错误====="+ret7);
		}
		String strData = byte2Str(buffer);
		System.out.println("=====strData======="+strData);*/
		
		
		//	
		//5.关才连接
		PullDriver.Disconnect(rtn1);
		
		//注销dll
		PullDriver.free();
		
		
	}
	// 将 byte转成符号
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		System.out.println("最後Data===>"+Total_Data);
		return Total_Data;
	}

}
