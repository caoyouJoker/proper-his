package com.javahis.device;

import java.io.ByteArrayOutputStream;

/**
 * 
 * @author lix@bluecore.com.cn
 *
 */
public class Uitltool {
	
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
