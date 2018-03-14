package com.javahis.device.card;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import jdo.sys.PatTool;

import com.bluecore.cardreader.CardInfoBO;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.javahis.ui.spc.util.StringUtils;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

/**
 * 
 * 建行四合一健盘
 * @author lix
 *
 */
public class IccCardRWUtil {
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // 错误讯息
	boolean isDebug = true;
	/**
	 * 医疗卡读卡
	 * @param commPort
	 * @return
	 */
	public TParm readEKT(String commPort) {

		TParm result = new TParm();
		try {
			int start = 40; // 从第几位开始写 40
			// int total=PatTool.getInstance().getMrNoLength() + 3;
			int total = 15;
			//
			byte[] data = new byte[total];
			int n = IccCardRW.iccCard.iIccOpenPort(0);
			if (isDebug) {
				System.out.println("====设备连接成功，返回====" + n);
			}
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====设备连接失败 ,返回====" + result);
				}
				throw new Exception();
				//return result;
			}
			n = IccCardRW.iccCard.i4428MemoryCheckCardType();
			if (isDebug) {
				System.out.println("====卡类型 ,返回====" + n);
			}
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====校验医疗卡类型 ,返回====" + result);
				}
				throw new Exception();
				//return result;
			}
			n = IccCardRW.iccCard
					.i4428MemoryReadCard(start, total, data);
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====读医疗卡,返回====" + result);
				}
				throw new Exception();
				//return result;
			}
			if (data[0] == -1) {
				result.setErr(-1, "此卡片是空卡");
				if (isDebug) {
					System.out.println("====此卡片是空卡,返回====" + result);
				}
				throw new Exception();
				//return result;
			}
			// 分析卡片返回数据;
			String strData = byte2Str(data);
			// PatTool.getInstance().getMrNoLength()
			String strMrNo = strData.substring(0, 12);
			if (isDebug) {
				System.out.println("===strMrNo====" + strMrNo);
			}
			result.setData("MR_NO", strMrNo);
			// PatTool.getInstance().getMrNoLength(),
			// PatTool.getInstance().getMrNoLength()+3
			String strSeq = strData.substring(12, 12 + 3);
			result.setData("SEQ", strSeq);
			if (isDebug) {
				System.out.println("===strSeq====" + strSeq);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			//result.setErr(-1, "读卡失败");
		}finally{
			
			IccCardRW.iccCard.vIccClosePort();		
		}
		//
		if(isDebug){
			System.out.println("====readEK方法,返回====" + result);
		}
		return result;
	}
	
	/**
	 * 医疗卡写卡
	 * @param commPort
	 * @param cardNo   卡号
	 * @param seq      卡序号
	 * @param price    现在无用
	 * @return
	 */
	public TParm writeEKT(String commPort, String cardNo, String seq,String price) {
		TParm result = new TParm();
		String inputData=cardNo+seq;
		try {
			int start = 40; // 从第几位开始写
			//int Total = PatTool.getInstance().getMrNoLength() +3;
			int total =15;
			byte[] data = inputData.getBytes(); // 写入Data
			
			if (data.length != total) {
				result.setErr(-1, "传入参数长度应为15");
				return result;
			}
			int n = IccCardRW.iccCard.iIccOpenPort(0);
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====设备连接失败 ,返回====" + result);
				}
				throw new Exception();
			}
			//2.
			n=IccCardRW.iccCard.i4428MemoryCheckCardType();
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}
			/*String sPin = "FFFF";
			byte[] pin = sPin.getBytes();*/
			
			Memory mem = new Memory(5);
			mem.clear();
			mem.setString(0, "FFFF");
			
			n=IccCardRW.iccCard.i4428MemoryCheckCardPin(2,mem.getString(0));
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}
			// 写资料
			n=IccCardRW.iccCard.i4428MemoryWriteCard(start, total, data);
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}

		} catch (Exception e) {
			e.printStackTrace();
			//result.setErr(-1, "写卡失败");
		}finally{
			if(isDebug){
				System.out.println("====关闭设备====");
			}
			IccCardRW.iccCard.vIccClosePort();
		}
		//
		//
		if(isDebug){
			System.out.println("====writeEKT方法,返回====" + result);
		}
		return result;		
	}

	/**
	 * 读二代身份证
	 * @return
	 */
	public TParm readIdCard() {
		String path = getPhotoPath();
		if(isDebug){
			System.out.println("====IC card photo path is==="+path);
		}
		TParm parm = new TParm();
		//CardInfoBO bo = null;
		int n=0;
		try {
			//1.
			n=IccCardRW.iccCard.InitComm();
			if(n!=0){
				if (n != 0) {
					parm.setErr(-1, this.getErrorMsg(n));
					throw new Exception();
				}
			}
			//2.
			n=IccCardRW.iccCard.Authenticate();
			System.out.println("==iccCard.Authenticate=="+n);
			if(n!=0){
				if (n != 0) {
					parm.setErr(-1, this.getErrorMsg(n));
					throw new Exception();
				}
			}
			//
			//byte[] sPhotoPath = path.getBytes(); 
			byte[] pMsg = new byte[256];
			//
			//ByteByReference b_ref=new ByteByReference();
			IntByReference n_ref = new IntByReference(); 
			n=IccCardRW.iccCard.ReadBaseMsg(path,pMsg,n_ref);
			if(n!=0){
				if (n != 0) {
					parm.setErr(-1, this.getErrorMsg(n));
					throw new Exception();
				}
			}
			//
			String strData = this.delAsc(pMsg); //byte2Str(pMsg);
			//
			if(isDebug){
				System.out.println("===输出中文strData===="+strData);
				
			}
			//
			if(!StringUtils.isEmpty(strData)){
				String idcard[]=strData.split("\\|");
				System.out.println("length+++"+idcard.length);
				parm.setData("SID", idcard[0].trim());// SID
				parm.setData("PAT_NAME", idcard[1].trim());// 姓名
				parm.setData("SEX_CODE", idcard[2].trim().equals("男") ? "1" : "2");// 性别
				parm.setData("BRITHPLACE", idcard[3].trim());// 民族
				parm.setData(
						"BIRTH_DATE",
						idcard[4].trim().substring(0, 4)
								+ "/"
								+ idcard[4].substring(4, 6)
								+ "/"
								+ idcard[4].trim()
										.substring(6, idcard[4].trim().length()));// 生日
				parm.setData("IDNO", idcard[5].trim());// 身份证号
				parm.setData("RESID_ADDRESS",idcard[6].trim());// 住址
			}else{
				parm.setErr(-1, "未获得身份证信息,请重新操作");
				throw new Exception();			
			}

		} catch (Exception e) {
			e.printStackTrace();
			//		
		}finally{
			//
			n=IccCardRW.iccCard.CloseComm();
		}
		//
/*		if (bo == null) {
			parm.setErr(-1, "未获得身份证信息,请重新操作");
			return parm;
		}*/	
		//
		// 通过身份证号查询病患信息		
		/*TParm infoParm = PatTool.getInstance().getInfoForIdNo(parm);
		if (infoParm.getCount() > 0) {
			parm = infoParm;
			// parm.setData("MESSAGE","已存在此就诊病患信息");
		} else {
			parm.setData("MESSAGE", "不存在此就诊病患信息");
		}*/
		// delFolder(path);
		if(isDebug){
			System.out.println("===readIdCard===="+parm);
		}
		return parm;
	}
	
	/**
	 * 读医保卡
	 * @return
	 */
	public TParm readINSCard() {
		TParm parm = new TParm();
/*		byte[] type = " ".getBytes();
		byte[] port = "USB".getBytes();*/
		try{
			//1.
			int n=IccCardRW.iccCard.Desk_SetDeviceParam("","USB");
			if (n != 0) {
				parm.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}

			//2.
			n=IccCardRW.iccCard.Desk_OpenDevice();
			if(n!=0){
				if (n != 0) {
					parm.setErr(-1, this.getErrorMsg(n));
					throw new Exception();
				}
			}
			//3.
			byte[] atr = new byte[32];
			//String atr=new String();
			IntByReference atrlen=new IntByReference();
			n=IccCardRW.iccCard.Desk_IccPowerOn(1,atr, atrlen);
			if (n != 0) {
				parm.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}
			//String strAtr = byte2Str(atr);
			//String strAtr = new String(atr, "GBK");
			if (isDebug) {				
				String strAtr=this.printHexString(atr);
				System.out.println("===readINSCard strAtr 16====" + strAtr);
				//3B6D000000905431168660130425001C4E
				//3B6D000000905431168660130425001C4E
			/*	for(int i=0;i<atr.length;i++){
					System.out.println("===readINSCard strAtr["+i+"]=" + atr[i]);
				}*/
				//System.out.println("===readINSCard strAtr====" + atr);
				//System.out.println("===readINSCard atrlen====" + atrlen.getValue());
			}
			//4.
			int capdu_len=5;	
		    //00B2010C3C //00 84 00 00 08 //00 A4
			//00B0050000 
			//00 A4 00 00 00 //6F0D8406BDA8C9E8B2BFA503880101	
			//TODO
			byte[] capdu=this.hexStringToBytes("00B2010C3C");
			//
			//capdu[0]=0x00;
			//
			byte[] rapdu = new byte[512];
			IntByReference rapdulen=new IntByReference();
			//
			n=IccCardRW.iccCard.Desk_IccApdu(1, capdu_len, capdu, rapdu,
					rapdulen);
			//
			if (n != 0) {
				parm.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}
			
			//System.out.println("===rapdu.length====" + rapdu.length);
			// 分析卡片返回数据;
			//String strData = byte2Str(rapdu);
			if (isDebug) {
				for(int i=0;i<rapdu.length;i++){
					System.out.println("===readINSCard rapdu["+i+"]=" + rapdu[i]);
					//System.out.println("===readINSCard rapdu["+i+"]=" + Integer.toHexString(rapdu[i]));
				}
				String hrapDu=this.printHexString(rapdu);
				//
				String strData = byte2Str(rapdu);
				System.out.println("===字符串 dRapDu====" + strData);
				//
				String dRapDu=this.hexString2String(hrapDu);
				//
				System.out.println("===字符串 dRapDu====" + dRapDu);
				//64F67DC8486DB6B29000
				//B517EFDCDAEF8B5D9000
				//System.out.println("===readINSCard data====" + strData);
				System.out.println("===rapdulen.length====" + rapdulen.getValue());
			}
			//5.
			n=IccCardRW.iccCard.Desk_IccPowerOff(1);
			if(n!=0){
				if (n != 0) {
					parm.setErr(-1, this.getErrorMsg(n));
					throw new Exception();
				}
			}		
		}catch(Exception e){
			e.printStackTrace();
			//
			
		}finally{
			IccCardRW.iccCard.Desk_CloseDevice();
		}
			
		return parm;
		
	}
	
	/**
	 * 获取卡类型 
	 * @return  卡类型   INSCard 社保卡| EKTCard 医疗卡 |
	 */
	public  String getCardType(){
		String strCardType="INSCard";//社保卡
		//1.先判断医疗卡
		int n = IccCardRW.iccCard.iIccOpenPort(0);
		if (n != 0) {
			IccCardRW.iccCard.vIccClosePort();	
			return "EKTCard error";
		}
		n = IccCardRW.iccCard.i4428MemoryCheckCardType();
		if(n==0){
			IccCardRW.iccCard.vIccClosePort();	
			return "EKTCard";
		}
		IccCardRW.iccCard.vIccClosePort();	
		//
		//2.判断是身份证卡
		//1.
		n=IccCardRW.iccCard.InitComm();
		if (n != 0) {
			IccCardRW.iccCard.CloseComm();
			return "IDCard error";
		}
		//2.
		n=IccCardRW.iccCard.Authenticate();
		if(n==0){
			IccCardRW.iccCard.CloseComm();	
			return "IDCard";
		}
		IccCardRW.iccCard.CloseComm();
		
		//3.社保卡
		return strCardType;
	}
	

	/**
	 * 单元测试程序
	 * @param args
	 */
	public static void main(String args[]) {
		IccCardRWUtil cardDev = new IccCardRWUtil();
		//1.医疗卡读卡操作
		//cardDev.readEKT("0");
		//2.医疗卡写卡操作
		//cardDev.writeEKT("0", "000012345678", "001", "0.00");
		//3.读二代身份证
		//cardDev.readIdCard();
		//4.社保卡
		//cardDev.readINSCard();  厂商未提供， 先用磁条刷的方式
		//5.依据泰心的情况， 自动判断卡类型
		System.out.println("===CardType==="+cardDev.getCardType());
		
		//System.out.println("16进制"+Integer.toHexString(121));
		//BigInteger a=new BigInteger("6F",16);
		//System.out.println("10进制"+a.toString());//131
	}
	
	
	public  byte[] hexStringToBytes(String hexString) {   
	    if (hexString == null || hexString.equals("")) {   
	        return null;   
	    }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
	    byte[] d = new byte[length];   
	    for (int i = 0; i < length; i++) {   
	        int pos = i * 2;   
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
	    }   
	    return d;   
	}   
	
	
	
	
	private byte charToByte(char c) {   
	    return (byte) "0123456789ABCDEF".indexOf(c);   
	}  
	
    //将指定byte数组以16进制的形式打印到控制台   
    public String  printHexString( byte[] b) {    
    	String ret = "";  
       for (int i = 0; i < b.length; i++) {    
         String hex = Integer.toHexString(b[i] & 0xFF);    
         if (hex.length() == 1) {    
           hex = '0' + hex;    
         }    
         //System.out.print(hex.toUpperCase() );    
         ret += hex.toUpperCase();  
       }    
       //
       System.out.println("=16进制  =="+ret); 
       return ret;   
      
    }  

    /** 
     * @Title:hexString2String 
     * @Description:16进制字符串转字符串 
     * @param src 
     *            16进制字符串 
     * @return 字节数组 
     * @throws 
     */  
    public String hexString2String(String src) {  
        String temp = "";  
        for (int i = 0; i < src.length() / 2; i++) {  
            temp = temp  
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),  
                            16).byteValue();  
        }  
        return temp;  
    } 
    
	private String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		// System.out.println("最後Data===>"+Total_Data);
		return Total_Data;
	}
	/**
	 * 
	 * @param bt
	 * @return
	 */
	private String delAsc(byte bt[]) {

        List<Byte> list = new ArrayList<Byte>();
		for ( byte tmp : bt ) {

			  int i=(int) tmp;
			  if( i!=0 && i!=32 ){
				  list.add(tmp);
			  }
		}

		byte[] newBt = new byte[list.size()];
        for( int i=0;i<list.size();i++ ){
        	newBt[i]=list.get(i);
        }

		return new String( newBt );
	}

	// 错误码 对照表
	private String getErrorMsg(int error) {

		System.out.println("=====error111======="+Integer.toHexString(error));
		String msg = "";
		if (h.containsKey(new Integer(error)))
			msg = (String) h.get(new Integer(error));
		else
			msg = "例外错误";
		return msg;
	}
	
	/**
	 * 获取二代身份证路径
	 * @return
	 */
	public String getPhotoPath() {
		//C:\\IdCard
		String path = "C:\\IdCard";
		//
		/*path = getProp().getString("", "sid.path");
		if (path == null || path.trim().length() <= 0) {

		}*/
		//1.假如不存在则创建
		return path;
	}
	
	/**
	 * 读取 TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			//System.out.println("TConfig.x 文件没有找到！");
		}
		return config;
	}

	static void setErrorMessage() {
		h.put(new Integer(0XF000), "打开串口失败"); // 0XF000 //打开串口失败
		h.put(new Integer(0XF001), "接收超时"); // 0XF001 //接收超时
		h.put(new Integer(0XF002), "用户按ESC键退出");// 0XF002 //用户按ESC键退出
		h.put(new Integer(0XF003), "连接设备失败");// 0XF003//连接设备失败
		h.put(new Integer(0XF004), "发送数据失败");// 0XF004//发送数据失败
		h.put(new Integer(0XF010), "未连接hid设备");// 0XF010//未连接hid设备
		h.put(new Integer(0XF011), "没有找到hid设备");// 0XF011//没有找到hid设备
		h.put(new Integer(0XF012), "获取HID设备信息失败");// 0XF012 //获取HID设备信息失败
		h.put(new Integer(0XF013), "获取HID设备细节信息失败");// 0XF013//获取HID设备细节信息失败
		h.put(new Integer(0XF014), "Could not get HID preparsed data");// 0XF014//Could
																		// not
																		// get
																		// HID
																		// preparsed
																		// data!
		h.put(new Integer(0XF015), "调用HidP_GetCaps 失败");// 0XF015
														// //调用HidP_GetCaps 失败
		h.put(new Integer(0XF016), "发送数据调用HidP_SetButtons失败");// 0XF016
																// //发送数据调用HidP_SetButtons失败
		h.put(new Integer(0XF017), "调用HidP_GetCaps 失败");// 0XF017
														// //得到产品描述符字符串失败GetProductString

		// 数据处理失败返回
		h.put(new Integer(0XA000), "未知错误");// 0XA000//未知错误
		h.put(new Integer(0xA001), "合并拆分数据中有非法数据，合并数据必须是0-F");// 0xA001//合并拆分数据中有非法数据，合并数据必须是0-F
		h.put(new Integer(0xA002), "得到中间数据时没有找到包头");// 0xA002//得到中间数据时没有找到包头
		h.put(new Integer(0xA003), "得到中间数据时没有找到包尾");// 0xA003//得到中间数据时没有找到包尾
		h.put(new Integer(0xA004), "返回校验错误");// 0xA004//返回校验错误
		h.put(new Integer(0XA005), "此函数暂时不支持此功能");// 0XA005//此函数暂时不支持此功能
		h.put(new Integer(0XA006), "非法数据");// 0XA006//非法数据

		// IC卡返回错误代码
		h.put(new Integer(0X1001), "不支持接触式IC卡"); // 0X1001//不支持接触式IC卡
		h.put(new Integer(0X1002), "接触式用户未插到位"); // 0X1002//接触式用户未插到位
		h.put(new Integer(0X1003), "接触式卡已上电");// 0X1003//接触式卡已上电
		h.put(new Integer(0X1004), "接触式卡未上电");// 0X1004//接触式卡未上电
		h.put(new Integer(0x1005), "接触式卡上电失败");// 0x1005//接触式卡上电失败

		// psam
		h.put(new Integer(0X2001), "不支持PSAM卡");// 0X2001//不支持PSAM卡
		h.put(new Integer(0X2003), "PSAM已上电");// 0X2003//PSAM已上电
		h.put(new Integer(0X2004), "PSAM卡未上电");// 0X2004//PSAM卡未上电
		h.put(new Integer(0x2005), "PSAM卡上电失败");// 0x2005//PSAM卡上电失败
		h.put(new Integer(0X2006), "PSAM无数据回应");// 0X2006//PSAM无数据回应
		h.put(new Integer(0X2007), "PSAM ERR");// 0X2007//PSAM ERR

		// 非接触
		h.put(new Integer(0X3001), "不支持非接触用户卡");// 0X3001//不支持非接触用户卡
		h.put(new Integer(0X3004), "未激活非接触式卡");// 0X3004//未激活非接触式卡
		h.put(new Integer(0X3005), "非接触式卡激活失败");// 0X3005//非接触式卡激活失败
		h.put(new Integer(0X3006), "等待卡进入感应区超时,或操作非接触式用户卡数据无回应");// 0X3006//等待卡进入感应区超时,或操作非接触式用户卡数据无回应
		h.put(new Integer(0X3007), "操作非接触式用户卡数据出现错误");// 0X3007//操作非接触式用户卡数据出现错误
		h.put(new Integer(0X3008), "设置卡Halt状态失败");// 0X3008//设置卡Halt状态失败
		h.put(new Integer(0X3009), "有多张卡在感应区");// 0X3009//有多张卡在感应区
		h.put(new Integer(0X6001), "接触式存储卡卡类型不正确");// 0X6001//接触式存储卡卡类型不正确
		h.put(new Integer(0x5001), "感应区卡片非二代证");// 0x5001//感应区卡片非二代证
		h.put(new Integer(0x5004), "寻找证卡失败");// 0x5004//寻找证卡失败
		h.put(new Integer(0x5005), "选取证卡失败");// 0x5005//选取证卡失败

	}

}

interface IccCardRW extends Library {
	IccCardRW iccCard = (IccCardRW) Native.loadLibrary(
			(Platform.isWindows() ? ".\\iccCard" : "c"), IccCardRW.class);

	/**
	 * 设置设备类型，设备端口及端口参数
	 * 
	 * @param type
	 *            设备类型，本动态库支持XXX, XXX, XXX
	 * @param port
	 *            设备端口，例如：COM1, COM2, COM3, USB
	 * @return0：成功 其他：失败，具体含义请参考《附录一》
	 */
	int Desk_SetDeviceParam(String type, String port);

	/**
	 * 打开端口，连接读卡器
	 * 
	 * @return
	 */
	int Desk_OpenDevice();
	
	/**
	 * 
	 * @return
	 */
	int Desk_CloseDevice();

	/**
	 * 获取读卡器固件版本信息
	 * 
	 * @param version
	 *            读卡器固件版本号，例如”XXXX.XXXX”
	 * @return 0 ：成功
	 */
	int Desk_GetVer(byte[] version);

	/**
	 * 获取动态库版本信息，此功能不需要连接读卡器
	 * 
	 * @param libver
	 *            读卡器驱动动态库版本信息，例如”GWI IC20A Driver 1.0”
	 * @return 0：成功
	 */
	int Desk_GetLibVer(byte[] libver);

	/**
	 * 读卡器复位，保持读卡器连接，释放IC卡座，关闭射频天线，清除读卡器错误状态
	 * 
	 * @return 0：成功
	 */
	int Desk_Reset();

	/**
	 * 蜂鸣，提醒用户放卡，或提示读卡器错误
	 * 
	 * @return
	 */
	int Desk_Beep();

	/**
	 * IC卡上电/非接激活卡片
	 * 
	 * @param cardno
	 *            ：卡座编号，具体含义参考《附录二》 atr ：IC卡上电返回数据 atrlen: :atr长度
	 * 
	 * @return
	 */
	int Desk_IccPowerOn(int cardno, byte[] atr, IntByReference atrlen);

	/**
	 * IC卡通信
	 * 
	 * @param cardno
	 *            卡座编号，具体含义参考《附录二》
	 * @param capdu_len
	 *            apdu命令的数据长度
	 * @param capdu
	 *            apdu命令数据
	 * @param rapdu
	 *            apdu响应数据的存储区
	 * @param rapdulen
	 *            apdu响应数据的长度
	 * @return
	 */
	int Desk_IccApdu(int cardno, int capdu_len, byte[] capdu, byte[] rapdu,
			IntByReference rapdulen);

	/**
	 * IC卡下电，关闭射频天线
	 * 
	 * @param cardno
	 *            卡座编号，具体含义参考《附录二》
	 * @return
	 */
	int Desk_IccPowerOff(int cardno);

	/**
	 * 激活M1卡，并返回卡片序号
	 * 
	 * @return
	 */
	int Desk_M1Active(byte[] snr);

	/**
	 * M1卡验证扇区密码
	 * 
	 * @param sectorid
	 *            扇区号，1K容量M1卡扇区号为0~15，4K容量M1卡块号为0~63
	 * @param passtype
	 *            密钥类型，=0为A密钥，=1为B密钥
	 * @param pwd
	 *            密码，例如 “FFFFFFFFFFFF”
	 * @return 0 ：成功
	 */
	int Desk_M1VerifyPwd(int sectorid, int passtype, byte[] pwd);

	/**
	 * 读取M1卡块数据
	 * 
	 * @param blockid
	 *            块号，0~63
	 * @param outbuf
	 *            数据存储区，至少17个字节大小
	 * @return 0 ：成功
	 */
	int Desk_M1Read(int blockid, byte[] outbuf);

	/**
	 * 以BCD格式读取M1卡块数据
	 * 
	 * @param blockid
	 *            块号，0~63
	 * @param outbuf
	 *            数据存储区，至少33个字节大小，返回数据格式为BCD码字符串
	 * @return 0 ：成功
	 */
	int Desk_M1ReadBCD(int blockid, byte[] outbuf);

	/**
	 * 写M1卡块数据
	 * 
	 * @param blockid
	 *            0~63
	 * @param databuf
	 *            以’\0’结束的字符串，原样写入M1卡，包括最后的’\0’字符，如果字符串长度超过16字节，
	 *            将自动截取前面16个字节写入M1卡
	 * @return
	 */
	int Desk_M1Write(int blockid, byte[] databuf);

	/**
	 * 以BCD格式读取M1卡块数据
	 * 
	 * @param blockid
	 *            块号，0~63
	 * @param databuf
	 *            BCD格式的数据字符串，长度必须为32
	 * @return 0 ：成功
	 */
	int Desk_M1WriteBCD(int blockid, byte[] databuf);

	/**
	 * M1卡下电
	 * 
	 * @return 0 – 成功
	 */
	int Desk_M1_PowerOff();

	/**
	 * 读二代证ID并返回卡片ID
	 * 
	 * @param cardid
	 *            ：二代证ID，五个字节
	 * @return
	 */
	int Desk_ReadIDCardID(byte[] cardid);

	/**
	 * 设置磁卡显示信息
	 * 
	 * @param sw1
	 *            是否有回车显示 ：1，有 0没有
	 * @param sw2
	 *            是否有磁道分隔符显示
	 * @param sw3
	 *            是否显示一磁道信息
	 * @param sw4
	 *            是否显示二磁道信息
	 * @param sw5
	 *            是否显示三磁道信息
	 * @param sw6
	 *            是否显示二磁道卡号
	 * @return 0：成功
	 */
	int Desk_CkSet(int sw1, int sw2, int sw3, int sw4, int sw5, int sw6);

	/**
	 * 本函数用于连接二代证模块
	 * 
	 * @return
	 */
	int InitComm();

	/**
	 * 本函数用于关闭已连接的端口，一般在调用InitComm成功并完成读卡任务后调用。
	 * 
	 * @return
	 */
	int CloseComm();

	/**
	 * 本函数用于发现身份证卡并选择卡
	 * 
	 * @return
	 */
	int Authenticate();

	/**
	 * 本函数用于读取卡中基本信息，包括文字信息与图像信息。文字信息已经分段解析，每一字段信息已经被表示为字符串。图象信息被解码后存为文件photo.
	 * bmp（在sPhotoPath目录下
	 * 
	 * @param sPhotoPath
	 *            图片保存路径，demo中默认保存在dll所在当前目录下
	 * @param pMsg
	 *            指向读到的文本信息。需要在调用时分配内存，字节数不小于186。函数调用成功后，各字段的文本信息已经转换为单字节形式，
	 *            并表示为字符串格式。字段意义及偏移值如下所示：各字段数据以分隔符“|”来分隔。
	 * @param len
	 *            [out] 整数， 返回总字符长度，可以给空值（NULL）。
	 * @return
	 */
	int ReadBaseMsg(String sPhotoPath, byte[] pMsg, IntByReference len);

	// ============4428 医疗卡================

	/**
	 * iPortNo：0是hid设备，如果为串口设备，iPortNo为串口号
	 * 
	 * @param iPortNo
	 * @return
	 */
	int iIccOpenPort(int iPortNo);

	/**
	 * 断开设备连接
	 */
	void vIccClosePort();

	/**
	 * 检查卡型是否正确
	 * 
	 * @return
	 */
	int i4428MemoryCheckCardType();

	/**
	 * 核对卡密码
	 * 
	 * @param iPinLen
	 *            密码个数，其值为2
	 * @param upchPin
	 *            密码字符串指针
	 * @return
	 */
	int i4428MemoryCheckCardPin(int iPinLen, String upchPin);

	/**
	 * 从指定地址读数据
	 * 
	 * @param iAdd
	 *            偏移地址，其值范围0～1023
	 * @param iDataLen
	 *            字符串长度，其值范围1～1024
	 * @param upchRetData
	 *            upchRetData 读出数据所存放地址指针
	 * @return 0 ：成功
	 */
	int i4428MemoryReadCard(int iAdd, int iDataLen, byte[] upchRetData);

	/**
	 * 向指定地址写数据
	 * 
	 * @param iAdd
	 *            偏移地址，其值范围0～1023
	 * @param iDataLen
	 *            字符串长度，其值范围1～1024
	 * @param upchWriteData
	 *            写入数据
	 * @return 0 ：成功
	 */
	int i4428MemoryWriteCard(int iAdd, int iDataLen, byte[] upchWriteData);

	/**
	 * 读出密码错误计数器值
	 * 
	 * @param piErrorCount
	 *            密码错误记数值存放指针
	 * @return
	 */
	int i4428MemoryGetErrorCount(int[] piErrorCount);

}
