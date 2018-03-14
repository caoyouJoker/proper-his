package com.javahis.ui.dev;

import java.io.ByteArrayOutputStream;

import com.silionmodule.Reader;
import com.silionmodule.ReaderException;
import com.silionmodule.TagReadData;
import com.silionmodule.Gen2.MemBankE;
import com.silionmodule.ReaderType.AntTypeE;

/**
 * MT100 USB RFID 驱动
 * 
 * @author lix
 * 
 */
public class ModuleDriver {
	/**
	 * 最多12位
	 * @param portNo  端口号
	 * @param antTypeE   天线类型
	 * @param no         EPC区存储号码
	 * @throws Exception 
	 */
	public static void write(String portNo, AntTypeE antTypeE, String no) throws Exception {
		if(no==null){
			 throw new Exception("EPC编号不能为空!");
		}else if(no.length()>12){
			 throw new Exception("EPC编号长度大于12位!");
		}
		
		Reader rd = null;
		// 1.
		try {
			rd = Reader.Create(portNo, antTypeE);
		} catch (ReaderException e) {
			e.printStackTrace();
			throw new Exception("读卡器未准备好!");
			
		}
		// 2.
		// 写EPC标签   
		//编码成16进制数据
		String towrite = encode(no);// 1111FFBBFFCC003620404044
		try {
			rd.WriteTagMemWords(null, MemBankE.EPC, 2, hexstr_short16(towrite));

		} catch (ReaderException e) {
			e.printStackTrace();
			throw new Exception("写标签失败!");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("写标签失败!");
		}
		System.out.println("========写成功===========");

		rd.DisConnect();

	}

	/**
	 * 读EPC区数据
	 * @param portNo
	 * @param antTypeE
	 * @param block
	 * @param isHex
	 * @return
	 * @throws Exception
	 */
	public static String read(String portNo, AntTypeE antTypeE,int block,boolean isHex) throws Exception {
		Reader rd = null;
		// 1.
		try {
			rd = Reader.Create(portNo, antTypeE);
		} catch (ReaderException e) {
			e.printStackTrace();
			throw new Exception("读卡器未准备好!");
		}

		short[] data;
		String rtnData=null;
		String dataHex=null;
		try {
			data = rd.ReadTagMemWords(null, MemBankE.EPC, 2, block);
			System.out.println("read data:" + shorts_HexString(data));

			dataHex=shorts_HexString(data);

		    System.out.println("-------ss2-------" + dataHex);
			
			
		} catch (ReaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("读标签失败!");
		}

		if(isHex){
			rtnData=dataHex;
		}else{
			rtnData = decode(dataHex);
		}
		rd.DisConnect();

		return rtnData;

	}

	/**
	 * 盘点操作
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
		
		
		//1.读测试
		try {
			String data=ModuleDriver.read("COM5",AntTypeE.ONE_ANT,6,false);
			System.out.println(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}
		
		//2.写操作
/*		try {
			write("COM5", AntTypeE.ONE_ANT, "201211020001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getMessage();
		}*/
		
		
		
		
		
		//以下为实验代码，暂不使用
		//Reader rd = null;
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

		//String s = Long.toHexString(201211010001l);
		/*
		 * Long i = Long.valueOf("E2001062580300361870FFEF", 16);
		 * System.out.println("===========" + i);
		 */

		/*String decodeStr = decode("E2001062580300361870FFEF");
		// 323031323131303230303032
		System.out.println("-------decodeStr-------" + decodeStr);*/

		// 最多12位
/*		String enstr = encode("201211020002");
		System.out.println("-------enstr-------" + enstr);

		// 最多12位
		String decodeStr1 = decode("323031323131303230303032");*/
		
/*		try {
			short[] ss=hexstr_short16("323031323131303230303032");
			for(int i=0;i<ss.length;i++){
				System.out.println("======ss======="+ss[i]);
			}
			
			String ss2=shorts_HexString(ss);
			System.out.println("-------ss2-------" + ss2);
			String decodeStr1 = decode(ss2);
			System.out.println("-------decodeStr1-------" + decodeStr1);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//System.out.println("-------decodeStr-------" + decodeStr1);

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

	public static short[] hexstr_short16(String hexStr) throws Exception {
		byte baKeyword[] = new byte[hexStr.length() / 2];
		for (int i = 0; i < baKeyword.length; i++)
			try {
				baKeyword[i] = (byte) Integer.parseInt(hexStr.substring(i * 2,
						i * 2 + 2), 16);
			} catch (Exception e) {
				throw new Exception("Error Data Format");
			}

		return bytes_short16(baKeyword);
	}

	public static short[] bytes_short16(byte bytes[]) throws Exception {
		if (bytes.length % 1 != 0)
			throw new Exception("Error Data Format");
		short words[] = new short[bytes.length / 2];
		for (short i = 0; i < words.length; i++) {
			words[i] = (short) (bytes[2 * i] & 0xff);
			words[i] <<= 8;
			words[i] |= bytes[2 * i + 1] & 0xff;
		}

		return words;
	}

	public static final String shorts_HexString(short bArray[]) {
		String stemp = "";
		byte bytedata[] = new byte[bArray.length * 2];
		for (int i = 0; i < bArray.length; i++) {
			bytedata[i * 2] = (byte) (bArray[i] >> 8);
			bytedata[i * 2 + 1] = (byte) (bArray[i] & 0xff);
		}

		stemp = bytes_HexString(bytedata);
		return stemp;
	}

	public static final String bytes_HexString(byte bArray[]) {
		StringBuffer sb = new StringBuffer(bArray.length);
		for (int i = 0; i < bArray.length; i++) {
			String sTemp = Integer.toHexString(0xff & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}

		return sb.toString();
	}

	public static byte[] hexstr_Byte(String hexStr) throws Exception {
		byte baKeyword[] = new byte[hexStr.length() / 2];
		for (int i = 0; i < baKeyword.length; i++)
			try {
				baKeyword[i] = (byte) Integer.parseInt(hexStr.substring(i * 2,
						i * 2 + 2), 16);
			} catch (Exception e) {
				throw new Exception("Error Data Format");
			}

		return baKeyword;
	}

	/*
	 * public static String toStringHex(String s){ byte[] baKeyword=new
	 * byte[s.length()/2]; for(int i=0;i<baKeyword.length;i++){ try{
	 * baKeyword[i]=(byte)(0xff& Integer.parseInt(s.substring(i*2,i*2+2), 16));
	 * }catch(Exception e){ e.printStackTrace();
	 * 
	 * } }
	 * 
	 * try{ s=new String (baKeyword, "utf-8");//UTF-16le;Not "utf-8" }catch
	 * (Exception e1){ e1.printStackTrace(); } return s; }
	 */

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

}
