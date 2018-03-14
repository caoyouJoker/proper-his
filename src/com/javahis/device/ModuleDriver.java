package com.javahis.device;

import com.silionmodule.Gen2;
import com.silionmodule.ParamNames;
import com.silionmodule.Reader;
import com.silionmodule.ReaderException;
import com.silionmodule.TagReadData;
import com.silionmodule.Gen2.MemBankE;
import com.silionmodule.ReaderType.AntTypeE;

/**
 * MT100 USB RFID 驱动
 * 
 * @author lix@bluecore.com.cn
 * 
 */
public class ModuleDriver {

	public static boolean isDebug = true;

	/**
	 * 最多12位
	 * 
	 * @param portNo
	 *            端口号
	 * @param antTypeE
	 *            天线类型
	 * @param no
	 *            EPC区存储号码
	 * @throws Exception
	 */
	public static void write(String portNo, AntTypeE antTypeE, String no)
			throws Exception {
		if (no == null) {
			throw new Exception("EPC编号不能为空!");
		} else if (no.length() > 12) {
			throw new Exception("EPC编号长度大于12位!");
		}

		Reader rd = null;
		// 1.
		try {
			rd = Reader.Create(portNo, antTypeE);
			// 大部分只支持按块写入
			rd.paramSet(ParamNames.Reader_Gen2_WriteMode,
					Gen2.WriteModeE.BLOCK_ONLY);

		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());

		}
		// 2.
		// 写EPC标签
		// 编码成16进制数据
		String towrite = Uitltool.encode(no);// 1111FFBBFFCC003620404044
		if (isDebug) {
			System.out.println("====towrite===" + towrite);
			System.out.println("=======hexstr_short16======="
					+ Uitltool.hexstr_short16(towrite));
		}

		try {
			rd.WriteTagMemWords(null, MemBankE.EPC, 2, Uitltool.hexstr_short16(towrite));

		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());
		}

		System.out.println("========写成功===========");

		rd.DisConnect();

	}

	/**
	 * 读EPC区数据
	 * 
	 * @param portNo   顺序号
	 * @param antTypeE 读卡器类型
	 * @param block
	 * @param isHex 是否是16进显示
	 * @return
	 * @throws Exception
	 */
	public static String read(String portNo, AntTypeE antTypeE, int block,
			boolean isHex) throws Exception {
		Reader rd = null;
		// 1.
		try {
			rd = Reader.Create(portNo, antTypeE);
		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());
		}

		short[] data;
		String rtnData = null;
		String dataHex = null;
		try {
			data = rd.ReadTagMemWords(null, MemBankE.EPC, 2, block);
			dataHex = Uitltool.shorts_HexString(data);
			if (isDebug) {
				System.out.println("-------dataHex-------" + dataHex);
			}

		} catch (ReaderException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			throw new Exception(e.GetMessage());
		}

		if (isHex) {
			rtnData = dataHex;
		} else {
			rtnData = Uitltool.decode(dataHex);
		}
		rd.DisConnect();

		return rtnData;

	}

	/**
	 * 盘点操作(USB 接口暂不实现)
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static TagReadData[] readByMil(int milliseconds) {

		return null;
	}

	/**
	 * 单元测试
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		/*
		 * String towrite = encode("A20121105001");// 1111FFBBFFCC003620404044
		 * System.out.println("====towrite==="+towrite); try {
		 * System.out.println
		 * ("=======hexstr_short16======="+hexstr_short16(towrite)); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// 1.读测试
		try {
			String data = ModuleDriver.read("COM5", AntTypeE.ONE_ANT, 6, false);
			System.out.println(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}

		// 2.写操作
		/*
		 * try { write("COM5", AntTypeE.ONE_ANT, "B21211050003"); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); e.getMessage(); }
		 */

		// 以下为实验代码，暂不使用
		// Reader rd = null;
		// 1.创建读写器对象
		/*
		 * try { rd=Reader.Create("COM5", AntTypeE.ONE_ANT);
		 * 
		 * } catch (ReaderException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		// .Uitltool.hexstr_short16(towrite)
		// 字符串->转成hexstr;
		// hexstr->字符串;

		// 写操作写EPC标签
		/*
		 * String towrite="1111cCCCC";//1111FFBBFFCC003620404044 try {
		 * rd.WriteTagMemWords(null, MemBankE.EPC, 2, hexstr_short16(towrite));
		 * } catch (ReaderException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (Exception e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

		// String s=Integer.toHexString(20121101001);
		// System.out.println("===s===="+s);
		// 十进制
		// int i=Integer.valueOf(s, 16);
		// System.out.println("==========="+i);

		// String s = Long.toHexString(201211010001l);
		/*
		 * Long i = Long.valueOf("E2001062580300361870FFEF", 16);
		 * System.out.println("===========" + i);
		 */

		/*
		 * String decodeStr = decode("E2001062580300361870FFEF"); //
		 * 323031323131303230303032 System.out.println("-------decodeStr-------"
		 * + decodeStr);
		 */

		// 最多12位
		/*
		 * String enstr = encode("201211020002");
		 * System.out.println("-------enstr-------" + enstr);
		 * 
		 * // 最多12位 String decodeStr1 = decode("323031323131303230303032");
		 */

		/*
		 * try { short[] ss=hexstr_short16("323031323131303230303032"); for(int
		 * i=0;i<ss.length;i++){ System.out.println("======ss======="+ss[i]); }
		 * 
		 * String ss2=shorts_HexString(ss);
		 * System.out.println("-------ss2-------" + ss2); String decodeStr1 =
		 * decode(ss2); System.out.println("-------decodeStr1-------" +
		 * decodeStr1);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// System.out.println("-------decodeStr-------" + decodeStr1);

		/*
		 * String s1=toStringHex("BBBB"); System.out.println("===ssss========" +
		 * s1);
		 */

		// StringUtil.
		/*
		 * try { byte[] b=ModuleDriver.hexstr_Byte("1e240");
		 * 
		 * //十六->十进制 //Integer.toString(i) //String s1=new String(b);
		 * //System.out.println("S1========"+s1);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// System.out.println("hex========"+Integer.toHexString(E2001062580300361870FFEF));
		// String.valueOf();

		// 读操作;
		/*
		 * short[] data; try { data = rd.ReadTagMemWords(null, MemBankE.EPC, 2,
		 * 3); System.out.println("read data:"+shorts_HexString(data)); } catch
		 * (ReaderException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// rd.DisConnect();

	}

}
