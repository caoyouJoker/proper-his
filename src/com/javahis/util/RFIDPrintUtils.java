package com.javahis.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.device.Uitltool;

/**
 * 电子标签打印工具类
 * 
 * @author lixiang
 * 
 */
public class RFIDPrintUtils {
	/**
	 * 品名
	 */
	public static final String PARM_NAME = "P_NAME";
	/**
	 * 规格
	 */
	public static final String PARM_SPEC = "P_SPEC";
	/**
	 * 效期
	 */
	public static final String PARM_VALID_DATE = "P_VALID_D";

	/**
	 * 效期
	 */
	public static final String PARM_NUM = "P_NUM";

	/**
	 * 条码
	 */
	public static final String PARM_CODE = "P_CODE";

	/**
	 * RFID值(十进制形式)
	 */
	public static final String PARM_PRFID = "P_PRFID";
	/**
	 * 调试开关
	 */
	private static boolean isDebug = true;

	/**
	 * 打印电子标签的模版
	 */
	/*
	 * private static String RFID_PRINT_TEMPLATE = "{D0590,1035,0570|}\r\n" +
	 * "{C|}\r\n" + "{PC000;0160,0095,2,2,e,00,B=名称:|}\r\n" +
	 * "{PC001;0280,0095,2,2,e,00,B=#PNAME|}\r\n" +
	 * "{PC002;0160,0145,2,2,e,00,B=规格:|}\r\n" +
	 * "{PC003;0280,0145,2,2,e,00,B=#PSPEC|}\r\n" +
	 * "{XB00;0170,0210,B,1,05,05,13,13,05,0,0127,+0000000000,0,00=#PCODE|}\r\n"
	 * + "{PV00;0382,0374,0035,0035,B,00,B=#PCODE|}\r\n" +
	 * "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n" +
	 * "{PC004;0160,0195,2,2,e,00,B=效期:|}\r\n" +
	 * "{PC005;0280,0195,2,2,e,00,B=#PVDATE|}\r\n" +
	 * "{XS;I,0001,0000C6201|}\r\n";
	 */

	/*
	 * private static String RFID_PRINT_TEMPLATE = "{D0590,1035,0570|}\r\n" +
	 * "{C|}\r\n" + "{PC000;0120,0095,2,2,e,00,B=名称:|}\r\n" +
	 * "{PC001;0240,0095,2,2,e,00,B=#PNAME|}\r\n" +
	 * "{PC002;0120,0145,2,2,e,00,B=规格:|}\r\n" +
	 * "{PC003;0240,0145,2,2,e,00,B=#PSPEC|}\r\n" +
	 * "{XB00;0130,0210,A,3,05,0,0127,+0000000000,000,0,00=>#PCODE|}\r\n" +
	 * "{PV00;0197,0374,0035,0035,B,00,B=#PCODE|}\r\n" +
	 * "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n" +
	 * "{PC004;0120,0195,2,2,e,00,B=效期:|}\r\n" +
	 * "{PC005;0240,0195,2,2,e,00,B=#PVDATE|}\r\n" +
	 * "{XS;I,0001,0000C6201|}\r\n";
	 */

	private static String TOXIC_RFID_PRINT_TEMPLATE = ""
			+ "{D0421,0755,0401|}\r\n"
			+ "{AY;+00,0|}\r\n"
			+ "{C|}\r\n"
			+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
			+ "{PC000;0048,0056,15,15,e,00,B=名称：|}\r\n"
			+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
			+ "{PC003;0048,0096,15,15,e,00,B=规格：|}\r\n"
			+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
			+ "{PC005;0048,0136,15,15,e,00,B=容量：|}\r\n"
			+ "{PC006;0128,0136,15,15,e,00,B=#PNUM|}\r\n"
			+ "{XB00;0047,0180,A,3,05,0,0127,+0000000000,000,0,00=>5#PCODE|}\r\n"
			+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
			+ "{XS;I,0001,0000C6201|}\r\n";

	/**
	 * 正式打印模版
	 */
/*	private static String RFID_PRINT_TEMPLATE = "{U2;0120|}\r\n"
			+ "{D0421,0755,0401|}\r\n"
			+ "{AY;+00,0|}\r\n"
			+ "{C|}\r\n"
			+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
			+ "{PC000;0048,0056,15,15,e,00,B=名称：|}\r\n"
			+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
			+ "{PC003;0048,0096,15,15,e,00,B=规格：|}\r\n"
			+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
			+ "{PC005;0048,0136,15,15,e,00,B=效期：|}\r\n"
			+ "{PC006;0128,0136,15,15,e,00,B=#PVDATE|}\r\n"
			+ "{XB00;0047,0150,A,3,05,0,0127,+0000000000,000,0,00=>#PCODES|}\r\n"
			+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
			+ "{XS;I,0001,0000C6201|}\r\n"
			+ "{U1;0120|}\r\n";*/
	
	
	/*private static String RFID_PRINT_TEMPLATE = "{U2;0120|}\r\n"
		+ "{D0421,0755,0401|}\r\n"
		+ "{AY;+00,0|}\r\n"
		+ "{C|}\r\n"
		+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
		+ "{PC000;0048,0056,15,15,e,00,B=名称：|}\r\n"
		+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
		+ "{PC003;0048,0096,15,15,e,00,B=规格：|}\r\n"
		+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
		+ "{PC005;0048,0136,15,15,e,00,B=效期：|}\r\n"
		+ "{PC006;0128,0136,15,15,e,00,B=#PVDATE|}\r\n"
		+ "{XB00;0047,0150,A,3,05,0,0127,+0000000000,000,0,00=>#PCODE|}\r\n"
		+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
		+ "{XS;I,0001,0000C6201|}\r\n"
		+ "{U1;0120|}\r\n";*/
	/**
	 * 
	 */
//	private static String RFID_PRINT_TEMPLATE = "{D0421,0755,0401|}\r\n"
//		+ "{U2;0120|}\r\n"
//		+ "{AY;+00,0|}\r\n"
//		+ "{C|}\r\n"
//		+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
//		+ "{PC000;0048,0056,15,15,e,00,B=名称：|}\r\n"
//		+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
//		+ "{PC003;0048,0096,15,15,e,00,B=规格：|}\r\n"
//		+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
//		+ "{PC005;0048,0136,15,15,e,00,B=效期：|}\r\n"
//		+ "{PC006;0128,0136,15,15,e,00,B=#PVDATE|}\r\n"
//		+ "{XB00;0017,0090,A,3,05,0,0087,+0000000000,000,0,00=>5#PCODE|}\r\n"
//		+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
//		+ "{XS;I,0001,0002C3201|}\r\n"                           
//		+ "{U1;0120|}\r\n";
	
//	private static String RFID_PRINT_TEMPLATE = "{D0309,0510,0289|}\r\n"
//		//+"{U2;0120|}\r\n"
//		+"{AY;+00,0|}\r\n"
//		+"{C|}\r\n"
//		//+"{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
//		+"{PC000;0028,0056,15,15,e,00,B=名称：|}\r\n"
//		+"{XB00;0028,0140,A,3,05,0,0087,+0000000000,000,0,00=>#PCODES|}\r\n"
//		+"{PC001;0108,0250,1,1,e,00,B=#PCODE|}\r\n"
//		+"{PC002;0108,0056,15,15,e,00,B=#PNAME|}\r\n"
//		+"{PC003;0028,0086,15,15,e,00,B=规格：|}\r\n"
//		+"{PC004;0108,0086,15,15,e,00,B=#PSPEC|}\r\n"
//		+"{PC005;0028,0116,15,15,e,00,B=效期：|}\r\n"
//		+"{PC006;0108,0116,15,15,e,00,B=#PVDATE|}\r\n"
//		+"{XS;I,0001,0003C3201|}\r\n"; 
		//+"{U1;0120|}\r\n";
	
	
	private static String RFID_PRINT_TEMPLATE = "{D0421,0780,0401|}\r\n"
		//+"{U2;0120|}\r\n"
		+"{AY;+00,0|}\r\n"
		+"{C|}\r\n"
		+"{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
		+"{PC000;0028,0106,15,15,e,00,B=名称：|}\r\n"
		+"{XB00;0028,0190,A,3,05,0,0087,+0000000000,000,0,00=>#PCODES|}\r\n"
		+"{PC001;0108,0300,1,1,e,00,B=#PCODE|}\r\n"
		+"{PC002;0108,0106,15,15,e,00,B=#PNAME|}\r\n"
		+"{PC003;0028,0136,15,15,e,00,B=规格：|}\r\n"
		+"{PC004;0108,0136,15,15,e,00,B=#PSPEC|}\r\n"
		+"{PC005;0028,0166,15,15,e,00,B=效期：|}\r\n"
		+"{PC006;0108,0166,15,15,e,00,B=#PVDATE|}\r\n"
		+"{XS;I,0001,0003C3201|}\r\n"; 
		//+"{U1;0120|}\r\n";
	
	/*	private static String RFID_PRINT_TEMPLATE = "{D0421,0755,0401|}\r\n"
	+ "{U2;0120|}\r\n"
	+ "{AY;+00,0|}\r\n"
	+ "{C|}\r\n"
	+ "{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
	+ "{PC000;0048,0056,15,15,e,00,B=名称：|}\r\n"
	+ "{PC002;0128,0056,15,15,e,00,B=#PNAME|}\r\n"
	+ "{PC003;0048,0096,15,15,e,00,B=规格：|}\r\n"
	+ "{PC004;0128,0096,15,15,e,00,B=#PSPEC|}\r\n"
	+ "{PC005;0048,0136,15,15,e,00,B=效期：|}\r\n"
	+ "{PC006;0128,0136,15,15,e,00,B=#PVDATE|}\r\n"
	+ "{XB00;0047,0150,A,3,05,0,0127,+0000000000,000,0,00=>#PCODES|}\r\n"
	+ "{PC001;0132,0302,1,1,e,00,B=#PCODE|}\r\n"
	+ "{XS;I,0001,0002C6201|}\r\n"
	+ "{U1;0120|}\r\n";
	
	
	private static String RFID_PRINT_TEMPLATE = "{D0420,0700,0350|}\r\n"
	+"{U2;0100|}\r\n"
	+"{AY;+00,0|}\r\n"
	+"{C|}\r\n"
	//+"{XB01;0000,0000,r,T24,G2,B01=#PRFID|}\r\n"
	+"{PC000;0018,0026,15,15,e,00,B=名称：|}\r\n"
	//+"{XB00;0017,0090,A,3,05,0,0087,+0000000000,000,0,00=>5#PCODE|}\r\n"
	+"{PC001;0108,0202,1,1,e,00,B=#PCODE|}\r\n"
	+"{PC002;0098,0026,15,15,e,00,B=#PNAME|}\r\n"
	+"{PC003;0018,0056,15,15,e,00,B=规格：|}\r\n"
	+"{PC004;0098,0056,15,15,e,00,B=#PSPEC|}\r\n"
	+"{PC005;0018,0086,15,15,e,00,B=效期：|}\r\n"
	+"{PC006;0098,0086,15,15,e,00,B=#PVDATE|}\r\n"
	+"{XS;I,0001,0002D3001|}\r\n"
	+"{U1;0100|}\r\n";
	
	*
	*/
	
	
	private static String out = "{U1;0100|}\r\n";
	
		
	
	
	private static String in = "{U2;0100|}\r\n";
		
	

	/**
	 * 打印RFID标签
	 * 
	 * @param parm
	 */
	public static boolean send2LPT(TParm parm) {
		// 取参数;
		// 1.品名
		String pName = parm.getValue(PARM_NAME);
		// 2.规格
		String pSpec = parm.getValue(PARM_SPEC);
		// 3.效期
		String pValidDate = parm.getValue(PARM_VALID_DATE);
		// 4.条码
		String pCode = parm.getValue(PARM_CODE);
		// 5.rfid值
		String pRfid = parm.getValue(PARM_PRFID);

		// 替换模版
		// 1.
		// String rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PNAME", pName);
		String rfidData = StringTool.replaceAll(RFID_PRINT_TEMPLATE, "#PNAME",
				pName);
		// System.out.println("--"+rfidData);

		// 2
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PSPEC", pSpec);
		rfidData = StringTool.replaceAll(rfidData, "#PSPEC", pSpec);
		// 3效期
		rfidData = StringTool.replaceAll(rfidData, "#PVDATE", pValidDate);

		// 4.
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PCODE", pCode);
		String barcode = encode(pCode);
		rfidData = StringTool.replaceAll(rfidData, "#PCODES", barcode);
		rfidData = StringTool.replaceAll(rfidData, "#PCODE", pCode);

		// 5
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PRFID", pRfid);
		// 转换16进制数据
		// System.out.println("===10进制形式===="+Uitltool.encode(pRfid));

		// System.out.println("===10进制形式===="+Uitltool.decode((Uitltool.encode(pRfid))));
		rfidData = StringTool.replaceAll(rfidData, "#PRFID", Uitltool
				.encode(pRfid));
		if (isDebug) {
			System.out.println("rfidData:\r\n" + rfidData);
		}
		synchronized (rfidData) {
			if (isDebug) {
				System.out.println("---rfidData----" + rfidData);
			}
			// 数据送LPT1
			FileWriter fw = null;
			PrintWriter out = null;
			try {
				fw = new FileWriter("LPT3");
				// fw = new FileWriter("mydata.txt");

				out = new PrintWriter(fw);
				out.print(rfidData);
				return true;
			} catch (IOException e) {
				System.out.println(pName + "打印错误");
				e.printStackTrace();
				return false;
			} finally {
				out.close();
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 
	 * @param s1
	 * @return
	 */
	private static String encode(String s1) {
		// String s1="A13040500001";
		String s2 = s1.substring(0, 2);
		// System.out.println("--s2--"+s2);

		String s3 = s1.substring(2, s1.length());
		// System.out.println("--s3--"+s3);
		String strCode = "6" + s2 + ">5" + s3;
		if (isDebug) {
			System.out.println("----strCode-----" + strCode);
		}
		return strCode;
	}
	
	
	
	/**
	 * 打印RFID标签
	 * 
	 * @param parm
	 */
	public static boolean out() {

			// 数据送LPT1
			FileWriter fw = null;
			PrintWriter out = null;
			try {
				fw = new FileWriter("LPT3");
				// fw = new FileWriter("mydata.txt");

				out = new PrintWriter(fw);
				out.print(RFIDPrintUtils.out);
				return true;
			} catch (IOException e) {
				System.out.println( "============================退出打印错误");
				e.printStackTrace();
				return false;
			} finally {
				out.close();
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	public static boolean in() {

		// 数据送LPT1
		FileWriter fw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter("LPT1");
			// fw = new FileWriter("mydata.txt");

			out = new PrintWriter(fw);
			out.print(RFIDPrintUtils.in);
			return true;
		} catch (IOException e) {
			System.out.println( "============================退出打印错误");
			e.printStackTrace();
			return false;
		} finally {
			out.close();
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}

	/**
	 * 打印RFID标签
	 * 
	 * @param parm
	 */
	public static boolean send2LPTToxic(TParm parm) {
		// 取参数;
		// 1.品名
		String pName = parm.getValue(PARM_NAME);
		// 2.规格
		String pSpec = parm.getValue(PARM_SPEC);
		// 3.效期
		String pNum = parm.getValue(PARM_NUM);
		// 4.条码
		String pCode = parm.getValue(PARM_CODE);
		// 5.rfid值
		String pRfid = parm.getValue(PARM_PRFID);

		// 替换模版
		// 1.
		// String rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PNAME", pName);
		String rfidData = StringTool.replaceAll(TOXIC_RFID_PRINT_TEMPLATE,
				"#PNAME", pName);
		// System.out.println("--"+rfidData);

		// 2
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PSPEC", pSpec);
		rfidData = StringTool.replaceAll(rfidData, "#PSPEC", pSpec);
		// 3效期
		rfidData = StringTool.replaceAll(rfidData, "#PNUM", pNum);

		// 4.
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PCODE", pCode);
		rfidData = StringTool.replaceAll(rfidData, "#PCODE", pCode);

		// 5
		// rfidData = RFID_PRINT_TEMPLATE.replaceAll("#PRFID", pRfid);
		// 转换16进制数据
		// System.out.println("===10进制形式===="+Uitltool.encode(pRfid));

		// System.out.println("===10进制形式===="+Uitltool.decode((Uitltool.encode(pRfid))));
		rfidData = StringTool.replaceAll(rfidData, "#PRFID", Uitltool
				.encode(pRfid));
		if (isDebug) {
			System.out.println("rfidData:\r\n" + rfidData);
		}
		// 数据送LPT1
		FileWriter fw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter("LPT3");
			// fw = new FileWriter("mydata.txt");

			out = new PrintWriter(fw);
			out.print(rfidData);
			return true;
		} catch (IOException e) {
			System.out.println(pName + "打印错误");
			e.printStackTrace();
			return false;
		} finally {
			out.close();
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * TParm parm = new TParm();
		 * 
		 * parm.setData(RFIDPrintUtils.PARM_NAME, "品名测试1");
		 * 
		 * parm.setData(RFIDPrintUtils.PARM_SPEC, "规格测试15*15");
		 * 
		 * parm.setData(RFIDPrintUtils.PARM_VALID_DATE, "2013/04/13");
		 * 
		 * // 十进制 parm.setData(RFIDPrintUtils.PARM_CODE, "130104000001");
		 * 
		 * // 需要转化(看一下应用制作模版是否可以设置) // 十六进制
		 * parm.setData(RFIDPrintUtils.PARM_PRFID, "130104000001");
		 * 
		 * // 单个打印 RFIDPrintUtils.send2LPT(parm);
		 */

		// 多个打印
		// 6A1>53040500001 A13040500001
		String s1 = "A13040500001";
		String s2 = s1.substring(0, 2);
		System.out.println("--s2--" + s2);

		String s3 = s1.substring(2, s1.length());
		System.out.println("--s3--" + s3);
		String strCode = "6" + s2 + ">5" + s3;
		System.out.println("----strCode----" + strCode);

	}

}
