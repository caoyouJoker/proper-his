package com.javahis.device;

import java.math.BigDecimal;
import java.util.Hashtable;

import jdo.sys.PatTool;

import com.dongyang.data.TParm;

/**
 * <p>
 * Title: (泰达心血管)医疗卡接口
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-09-15
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @li.xiang19790130@gmial.com
 * @version 1.0
 */
public class CardRW {
	public static boolean loadFlg = true; // 有无Load Dll
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // 错误讯息

	public CardRW() {
	}

	static {
		try {
			System.loadLibrary("CardRWNew"); // 载入dll
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
	 * 读卡
	 * 
	 * @param commPort
	 *            String
	 * @param start
	 *            int
	 * @param total
	 *            int
	 * @param data
	 *            byte[]
	 * @return int
	 */
	private native static int readCard(String commPort, int start, int total,
			byte[] data);

	/**
	 * 写卡
	 * 
	 * @param commPort
	 *            String
	 * @param start
	 *            int
	 * @param total
	 *            int
	 * @param pin
	 *            byte[]
	 * @param data
	 *            byte[]
	 * @return int
	 */
	private native static int writeCard(String commPort, int start, int total,
			byte[] pin, byte[] data);

	/**
	 * 读医疗卡
	 * 
	 * @param CommPort
	 *            String
	 * @return TParm
	 */
	public TParm readEKT(String commPort) {
		TParm result = new TParm();
		try {
			int start = 40; // 从第几位开始写 40
			//int total = 25; // 取几位(病历号(12码)+流水号(3码)+10位金额);
			//$$=======Modified  2012/02/22暂时去掉金额 START============$$//
			int total=PatTool.getInstance().getMrNoLength() + 3;			
			//$$=======Modified  2012/02/22暂时去掉金额 END============$$//
			
			// 后加金额；
			byte[] data = new byte[total];
			int error = readCard(commPort, start, total, data);
		//	System.out.println("===error==="+error);
			if (error != 0) {
				result.setErr(-1, this.getErrorMsg(error));
				return result;
			}
			//System.out.println("=====data========="+data);
			//System.out.println("data0"+data[2]);
			if(data[0]==-1){
				//System.out.println("=====这是空卡======");
				result.setErr(-1, "此卡片是空卡");
				return result;
			}
			//分析卡片返回数据;	
			String strData = byte2Str(data);
			//System.out.println("===strData===="+strData);
		
			String strMrNo=strData.substring(0, PatTool.getInstance().getMrNoLength()); //======  chenxi
			//System.out.println("===strMrNo===="+strMrNo);	
			result.setData("MR_NO", strMrNo);
			String strSeq=strData.substring(PatTool.getInstance().getMrNoLength(), PatTool.getInstance().getMrNoLength()+3); //========== cehnxi
			result.setData("SEQ", strSeq);
			//System.out.println("===strSeq===="+strSeq);
			//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
			//金额部分，原泰心卡无此项，新系统加入并需兼容原医疗卡;
			/**if(data[15]==-1){
				result.setData("CURRENT_BALANCE", "0.00"); 
			
			//新发的卡有金额情况
			}else{
				String strBalance=strData.substring(15, 25);
				//int lastPost=strBalance.indexOf("-");
				//strBalance=strBalance.substring(0,lastPost);
				strBalance=covertPrice(strBalance);
				result.setData("CURRENT_BALANCE", strBalance); 
			}**/
			//$$===================Modified  2012/02/22暂时去掉金额 END============================$$//
			//System.out.println("=====result========"+result);
			//result.addData("returnData", strData);

		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "读卡失败");

		}

		return result;
	}


	/**
	 * 写医疗卡
	 * @param commPort  com口号
	 * @param cardNo    医疗卡号
	 * @param seq       序号
	 * @param price     金额(暂不使用)
	 * @return
	 */
	public TParm writeEKT(String commPort, String cardNo, String seq,String price) {
		TParm result = new TParm();
		//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
		//String inputData=cardNo+seq+doFormatedPrice(price);
		//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
		String inputData=cardNo+seq;
		try {
			int Start = 40; // 从第几位开始写
			//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
			//int Total = 25; // 总共写几位(病历号(12码)+流水号(3码))
			//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
			int Total = PatTool.getInstance().getMrNoLength() +3;
			byte[] Data = inputData.getBytes(); // 写入Data
			//System.out.println("==Data=="+Data.length);
			
			String sPin = "FFFF";
			byte[] Pin = sPin.getBytes();
			//System.out.println("===========pin===="+Pin.length);
			//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
			/**if (Data.length != 25) {
				result.setErr(-1, "传入参数长度应为25");
				return result;
			}**/
			//$$====================Modified  2012/02/22暂时去掉金额 START==========================$$//
			if (Data.length != Total) {
				result.setErr(-1, "传入参数长度应为15");
				return result;
			}
			
			// 写资料
			int error = writeCard(commPort, Start, Total, Pin, Data);
			if (error != 0) {
				result.setErr(-1, this.getErrorMsg(error));
				return result;
			}

		} catch (Exception e) {

			result.setErr(-1, "写卡失败");
		}
		return result;
	}
	/**
	 * @deprecated
	 * 写金额失败.
	 * @param commPort
	 * @param balance   100---------(总共10位)不足补-
	 * @return
	 */
	public TParm writeBalance(String commPort, String balance){
		TParm result = new TParm();
		try {
			int Start = 55; // 从第几位开始写
			int Total = 10; // 总共写几位(病历号(12码)+流水号(3码))
			balance=doFormatedPrice(balance);
					
			byte[] Data = balance.getBytes(); // 写入Data
			//System.out.println("==Data=="+Data.length);
			
			String sPin = "FFFF";
			byte[] Pin = sPin.getBytes();
			
			if (Data.length != 10) {
				result.setErr(-1, "传入参数长度应为10位.");
				return result;
			}
			// 写资料
			int error = writeCard(commPort, Start, Total, Pin, Data);
			if (error != 0) {
				result.setErr(-1, this.getErrorMsg(error));
				return result;
			}

		} catch (Exception e) {

			result.setErr(-1, "写金额失败");
		}
		return result;
		
	}
	/**
	 * @deprecated 
	 * 格式化成医疗卡存储格式10位，不足10位前补0
	 * @param price  0.75,100.35  0000000.75  0000100.35
	 * @return
	 */
	public String doFormatedPrice(String price){
		if(price==null||price.equals("")){
			return "0.00";
		}
		String strCardPrice="";
		//price四舍五入到2位
		BigDecimal  dPrice=new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
		String strPrice=dPrice.toString();
		//System.out.println("====strPrice====="+strPrice);	
		//不足10位前补0
		if(strPrice!=null&&strPrice.length()<10){
			int total=10-strPrice.length();
			for(int i=0;i<total;i++){
				strCardPrice+="0";
			}
			strCardPrice+=strPrice;
		}
		//System.out.println("====strCardPrice====="+strCardPrice);	
		return strCardPrice;
	}
	/**
	 * @deprecated 
	 * 卡数据转成金额;
	 * @param data  0000000.75|0000100.35
	 * @return
	 */
	public String covertPrice(String data){
		if(data==null||data.equals("")){
			return "0.00";
		}
		String strPrice="0.00";
		int pos=0;
		char c;
		for(int i=0;i<data.length();i++){
			//数字不等于0情况
			c=data.charAt(i);
			if(c!='0'){
				//System.out.println("c==="+c);
				//System.out.println("i==="+i);
				pos=i;
				break;
				//strPrice=strPrice.substring(i);
				//System.out.println("======strPrice1======"+strPrice);		
				//return strPrice;
			//==0情况
			}else{
				//下一个为.
				c=data.charAt(i+1);
				if(c=='.'){					
					pos=i;
					break;

				}
				
			}
			
		}
		strPrice=data.substring(pos);
		//System.out.println("======strPrice3======"+strPrice);		
		return strPrice;
		
	}
	

	// 错误码 对照表
	private String getErrorMsg(int error) {
		
		//System.out.println("=====error======="+error);
		String msg = "";
		if (h.containsKey(new Integer(error)))
			msg = (String) h.get(new Integer(error));
		else
			msg = "例外错误";
		return msg;
	}

	// 将 byte转成符号
	private  String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		// System.out.println("最後Data===>"+Total_Data);
		return Total_Data;
	}

	/**
	 * 错误消息;
	 */
	static void setErrorMessage() {
		h.put(new Integer(1), "开启COM埠失败"); // 开启COM埠失败!!
		h.put(new Integer(2), "关闭COM埠失败"); // 关闭COM埠失败!!
		h.put(new Integer(3), "错误的PIN码"); // 错误的PIN码!!
		h.put(new Integer(65535), "通讯错误"); // 0xffff
		h.put(new Integer(25088), "请置入卡片"); // 0x6200
		h.put(new Integer(25089), "请置入卡片或卡片未装置好"); // 0x6201
		h.put(new Integer(37119), "请置入卡片或卡片未装置好"); // 0x90FF
		h.put(new Integer(37118), "请置入卡片或卡片未装置好"); // 0x90FE
		h.put(new Integer(25217), "读取错误"); // 0x6281
		h.put(new Integer(25218), "文件结束"); // 0x6282
		h.put(new Integer(25344), "错误的PIN码"); // 0x6300
		h.put(new Integer(25537), "错误的PIN码"); // 0x63c1
		h.put(new Integer(25538), "错误的PIN码"); // 0x63c2
		h.put(new Integer(25544), "错误的PIN码");
		h.put(new Integer(25600), "复位不成功"); // 0x6400
		h.put(new Integer(25985), "写入失败"); // 0x6581
		h.put(new Integer(26368), "错误的命令长度"); // 0x6700
		h.put(new Integer(26880), "错误的状态"); // 0x6900
		h.put(new Integer(27009), "错误的文件类别"); // 0x6981
		h.put(new Integer(27010), "文件未选择"); // 0x6982
		h.put(new Integer(27011), "不可再试"); // 0x6983
		h.put(new Integer(27013), "文件已存在"); // 0x6985
		h.put(new Integer(27136), "错误的P1/P2"); // 0x6A00
		h.put(new Integer(27264), "错误的参数"); // 0x6A80
		h.put(new Integer(27265), "错误的P2"); // 0x6A81
		h.put(new Integer(27266), "文件没找到"); // 0x6A82
		h.put(new Integer(27268), "文件无足够记忆体空间"); // 0x6A84
		h.put(new Integer(27270), "错误的参数"); // 0x6A86
		h.put(new Integer(48384), "错误的偏移量"); // 0xBD00
		h.put(new Integer(27904), "无效的指令代码"); // 0x6D00
		h.put(new Integer(28160), "无效的CLA"); // 0x6E00
		h.put(new Integer(28656), "系统错误"); // 0x6FF0ss
	}

	public static void main(String args[]) {
		
		//System.out.println("hex========"+Integer.toHexString(0xFFFF));
		CardRW  cardRW=new CardRW();
		int i=CardRW.init();
		//写卡
		//TParm param=cardRW.writeEKT("COM1", "123456789987001100.12----");
		/*TParm param=cardRW.writeEKT("COM1", "123456789987","001","0.75");
		if(param.getErrCode()==-1){
			System.out.println("=========写卡操作出错.============");
			System.out.println(param.getErrParm().getErrText());
		}*/
		
		//读卡
		TParm param1=cardRW.readEKT("COM1");
		if(param1.getErrCode()==-1){
			System.out.println("=========读卡操作出错.============");
			System.out.println(param1.getErrParm().getErrText());
		}
		
		//只写金额
		//cardRW.writeBalance("COM1", "9999.35---");
		CardRW.free();
		//88  3A  B3  22  78  03  AE  26  78  03  AE  26  78  03  AE  
		//2CB4D88  3A  B3  22  78  03  AE  26  78  03  AE  26  78  03  AE 
		//2CB4D88  3A  B3  22  78  03  AE  26  78  03  AE  26  78  03  AE
		//byte[] Data={2CB4D88,3A,B3,22,78,03,AE,26,  78  03  AE  26  78  03  AE};
		
		//03201629   30  30  30  30  30  30  32  34  35  31  36  30  30  30  31
		//03237006   30  30  30  30  30  30  31  36  39  36  38  39  30  30  31
		//System.out.println(byte2Str(Data));
		
		//byte[] b={0x30,0x30,0x30,0x30,0x30,0x30,0x31,0x36,0x39,0x36,0x38,0x39,0x30,0x30,0x31};
		//System.out.println("byte2Str===="+byte2Str(b));//byte2Str====000000169689001 //169689
		//byte[] b={0x0,0x12,0x0,0x0,0x0};
		//System.out.println("byte2Str===="+byte2Str(b));
		//0.75,100.35  0000000.75  0000100.35
		//转成存入卡内的数据内容
		//cardRW.doFormatedPrice("0.75");
		//卡转钱
		//cardRW.covertPrice("0000000.75");
		
	}

}
