package com.javahis.device;

import jdo.sys.PatTool;

import com.dongyang.data.TParm;

/**
 * 建 行医疗卡驱动
 */
public class CardCBCRW {

	public static boolean loadFlg = true; // 有无Load Dll
	static {
		try {
			System.loadLibrary("CardCBCRW"); // 载入dll
			loadFlg = true;
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
	 * @param baud
	 * @param ic_type
	 * @param cardno
	 * @param address
	 * @param len
	 * @param data
	 * @return
	 */
	private native static int readCard(int commPort, int baud, int ic_type,
			int cardno, int address, int len, byte[] data);

	/**
	 * 写卡
	 * 
	 * @param comPort
	 * @param baud
	 * @param ic_type
	 * @param cardno
	 * @param pin
	 * @param address
	 * @param len
	 * @param data
	 * @return
	 */
	private native static int writeCard(int comPort, int baud, int ic_type,
			int cardno, byte[] pin, int address, int len, byte[] data);

	/**
	 * 读卡操作;
	 * 
	 * @param commPort
	 * @return
	 */
	public TParm readEKT(int commPort) {

		TParm result = new TParm();
		try {
			int start = 40; // 从第几位开始写 40
			// int total = 25; // 取几位(病历号(12码)+流水号(3码)+10位金额);
			// $$=======Modified 2012/02/22暂时去掉金额 START============$$//
			int total = 15;
			// $$=======Modified 2012/02/22暂时去掉金额 END============$$//

			// 后加金额；
			byte[] data = new byte[total];
			int flg = readCard(commPort, 9600, 1, 1, start, total, data);
			// System.out.println("===error==="+error);
			if (flg == 0) {
				result.setErr(-1, "读卡失败");
				return result;
			}
			// System.out.println("=====data========="+data);
			//System.out.println("data0" + data[2]);
			if (data[0] == -1) {
				// System.out.println("=====这是空卡======");
				result.setErr(-1, "此卡片是空卡");
				return result;
			}
			// 分析卡片返回数据;
			String strData = byte2Str(data);
			//System.out.println("===strData===="+strData);

			String strMrNo = strData.substring(0, PatTool.getInstance().getMrNoLength()); //=====  chenxi
			// System.out.println("===strMrNo===="+strMrNo);
			result.setData("MR_NO", strMrNo);
			String strSeq = strData.substring(12, 15);
			result.setData("SEQ", strSeq);
			//System.out.println("=====result========" + result);
			// result.addData("returnData", strData);

		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "读卡失败");

		}

		return result;
	}

	/**
	 * 写卡操作;
	 * 
	 * @param commPort
	 * @param cardNo
	 * @param seq
	 * @return
	 */
	public TParm writeEKT(int commPort, String cardNo, String seq) {
		TParm result = new TParm();
		String inputData = cardNo + seq;

		try {
			int Start = 40; // 从第几位开始写
			int Total = 15;
			byte[] Data = inputData.getBytes(); // 写入Data
			System.out.println("==Data==" + Data.length);

			String sPin = "FFFF";
			byte[] Pin = sPin.getBytes();

			if (Data.length != 15) {
				result.setErr(-1, "传入参数长度应为15");
				return result;
			}

			// 写资料
			int flg = writeCard(commPort, 9600, 1, 1, Pin, Start, Total, Data);
			if (flg < 1) {
				result.setErr(-1, "写卡失败");
				return result;
			}

		} catch (Exception e) {

			result.setErr(-1, "写卡失败");
		}
		return result;

	}

	// 将 byte转成符号
	private String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		// System.out.println("最後Data===>"+Total_Data);
		return Total_Data;
	}
	

	public static void main(String args[]) {

		// System.out.println("hex========"+Integer.toHexString(0xFFFF));
		CardCBCRW cardRW = new CardCBCRW();
		int i = CardCBCRW.init();
		// 写卡
		/**TParm param = cardRW.writeEKT(1, "111111111111", "001");
		if (param.getErrCode() == -1) {
			System.out.println("=========写卡操作出错.============");
			System.out.println(param.getErrParm().getErrText());
		}**/
		
		
		
		//读卡
		TParm param1=cardRW.readEKT(1);
		if(param1.getErrCode()==-1){
			System.out.println("=========读卡操作出错.============");
			System.out.println(param1.getErrParm().getErrText());
		}
		
		CardCBCRW.free();

	}

}
