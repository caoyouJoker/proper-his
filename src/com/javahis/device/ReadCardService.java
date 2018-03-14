package com.javahis.device;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import com.dongyang.data.TParm;

/**
 * 南京市民卡业务服务
 * 
 * @author lixiang
 * 
 */
public class ReadCardService {

	// 调试开关
	private static final boolean isDebug = true;
	// 错误信息标记
	private static final String ERR_FLG = "err";
	// 连接返回句柄
	private static int hReader = -1;
	//
	private static String STRING_CODING = "gb2312";

	/**
	 * 操作获取返回值及错误消息;
	 * 
	 * @param ret_value
	 *            返回的十进度
	 * @return 0成功 -1失败;
	 */
	public static int ReturnRet(int ret_value, TParm parm) {

		// System.out.println("=========ret_value返回的十进制值为=====" + ret_value);
		String ret = Integer.toHexString(ret_value);
		// System.out.println("========ret_value转成十六进制值为===========" + ret);
		if (ret.equalsIgnoreCase("0")) {
			parm.setData(ERR_FLG, "成功");
			return 0;
		} else if (ret.equalsIgnoreCase("1031")) {

		} else if (ret.equalsIgnoreCase("1041")) {

		} else if (ret.equalsIgnoreCase("1051")) {

		} else if (ret.equalsIgnoreCase("1061")) {

		} else if (ret.equalsIgnoreCase("1101")) {

		} else if (ret.equalsIgnoreCase("1111")) {

		} else if (ret.equalsIgnoreCase("1121")) {

		} else if (ret.equalsIgnoreCase("1122")) {
			parm.setData(ERR_FLG, "读取指定文件失败");
			return -1;
		} else if (ret.equalsIgnoreCase("1181")) {
			parm.setData(ERR_FLG, "消费失败");
			return -1;
		} else if (ret.equalsIgnoreCase("1182")) {
			parm.setData(ERR_FLG, "计算TAC失败");
			return -1;
		} else if (ret.equalsIgnoreCase("7001")) {
			parm.setData(ERR_FLG, "执行指令失败");
			return -1;
		} else if (ret.equalsIgnoreCase("7002")) {
			parm.setData(ERR_FLG, "无卡");
			return -1;
		} else if (ret.equalsIgnoreCase("7101")) {
			parm.setData(ERR_FLG, "Log文件创建失败");
			return -1;
		} else if (ret.equalsIgnoreCase("1091")) {
			parm.setData(ERR_FLG, "Desfire认证失败");
			return -1;
		} else if (ret.equalsIgnoreCase("1170")) {

		} else if (ret.equalsIgnoreCase("1171")) {

		} else if (ret.equalsIgnoreCase("1172")) {

		} else if (ret.equalsIgnoreCase("1173")) {

		} else if (ret.equalsIgnoreCase("1174")) {

		} else if (ret.equalsIgnoreCase("1175")) {

		} else if (ret.equalsIgnoreCase("1176")) {

		} else if (ret.equalsIgnoreCase("1177")) {

		} else if (ret.equalsIgnoreCase("1178")) {

		} else if (ret.equalsIgnoreCase("1179")) {
			parm.setData(ERR_FLG, "Desfire指令格式错误");
			return -1;
		} else if (ret.equalsIgnoreCase("1000")) {
		} else if (ret.equalsIgnoreCase("1001")) {
		} else if (ret.equalsIgnoreCase("1002")) {
		} else if (ret.equalsIgnoreCase("1003")) {
		} else if (ret.equalsIgnoreCase("1004")) {
		} else if (ret.equalsIgnoreCase("1005")) {
		} else if (ret.equalsIgnoreCase("1006")) {
		} else if (ret.equalsIgnoreCase("1007")) {
		} else if (ret.equalsIgnoreCase("1008")) {
		} else if (ret.equalsIgnoreCase("1009")) {
			parm.setData(ERR_FLG, "无卡");
		} else if (ret.equalsIgnoreCase("7E")) {
			parm.setData(ERR_FLG,
					"Length_Error Length of command string invalid");
			return -1;
		} else if (ret.equalsIgnoreCase("9E")) {
			parm.setData(ERR_FLG,
					"Parameter_Error Value of the parameter(s) invalid");
			return -1;
		} else if (ret.equalsIgnoreCase("AE")) {
			parm
					.setData(
							ERR_FLG,
							"Authentication_Error AUTHENTICATION_ERROR Current authentication status does not allow the requested command");
			return -1;
		} else if (ret.equalsIgnoreCase("0C")) {
			parm
					.setData(
							ERR_FLG,
							"NO_CHANGES No changes done to backup files, CommitTransaction/AbortTransaction not necessary");
			return -1;
		} else if (ret.equalsIgnoreCase("0E")) {
			parm
					.setData(ERR_FLG,
							"OUT_OF_EEPROM_ERROR Insufficient NV-Memory to complete command");
			return -1;
		} else if (ret.equalsIgnoreCase("1C")) {
			parm.setData(ERR_FLG,
					"ILLEGAL_COMMAND_CODE Command code not supported");
			return -1;
		} else if (ret.equalsIgnoreCase("1E")) {
			parm
					.setData(ERR_FLG,
							"INTEGRITY_ERROR CRC or MAC does not match data Padding bytes not valid");
			return -1;
		} else if (ret.equalsIgnoreCase("40")) {
			parm.setData(ERR_FLG, "NO_SUCH_KEY Invalid key number specified");
			return -1;
		} else if (ret.equalsIgnoreCase("9D")) {
			parm
					.setData(
							ERR_FLG,
							"PERMISSION_DENIED Current configuration / status does not allow the requested command");
			return -1;
		} else if (ret.equalsIgnoreCase("A0")) {
			parm.setData(ERR_FLG,
					"APPLICATION_NOT_FOUND Requested AID not present on PICC");
			return -1;
		} else if (ret.equalsIgnoreCase("A1")) {
			parm
					.setData(
							ERR_FLG,
							"APPL_INTEGRITY_ERROR Unrecoverable error within application, application will be disabled *");
			return -1;
		} else if (ret.equalsIgnoreCase("AF")) {
			parm
					.setData(ERR_FLG,
							"ADDITIONAL_FRAME Additional data frame is expected to be sent");
			return -1;
		} else if (ret.equalsIgnoreCase("BE")) {
			parm
					.setData(
							ERR_FLG,
							"BOUNDARY_ERROR Attempt to read/write data from/to beyond the file's/record's limits. Attempt to exceed the limits of a value file.");
			return -1;
		} else if (ret.equalsIgnoreCase("C1")) {
			parm
					.setData(
							ERR_FLG,
							"PICC_INTEGRITY_ERROR Unrecoverable error within PICC, PICC will be disabled *.");
			return -1;
		} else if (ret.equalsIgnoreCase("CD")) {
			parm
					.setData(ERR_FLG,
							"PICC_DISABLED_ERROR PICC was disabled by an unrecoverable error *");
			return -1;
		} else if (ret.equalsIgnoreCase("CE")) {
			parm
					.setData(
							ERR_FLG,
							"COUNT_ERROR Number of Applications limited to 28, no additional CreateApplication possible");
			return -1;
		} else if (ret.equalsIgnoreCase("DE")) {
			parm
					.setData(
							ERR_FLG,
							"DUPLICATE_ERROR Creation of file/application failed because file/application with same number already exists");
			return -1;
		} else if (ret.equalsIgnoreCase("EE")) {
			parm
					.setData(
							ERR_FLG,
							"EEPROM_ERROR Could not complete NV-write operation due to loss of power, internal backup/rollback mechanism activated *");
			return -1;
		} else if (ret.equalsIgnoreCase("F0")) {
			parm.setData(ERR_FLG,
					"FILE_NOT_FOUND Specified file number does not exist");
			return -1;
		} else if (ret.equalsIgnoreCase("F1")) {
			parm.setData(ERR_FLG,
					"FILE_NOT_FOUND Specified file number does not exist");
			return -1;
		}
		if (ret.startsWith("12")) {
			parm.setData(ERR_FLG, "注册读卡器失败");
			return -1;
		} else {
			parm.setData(ERR_FLG, "未知错误");
			return -1;
		}

	}

	/*
	 * 将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串 Convert byte[] to hex
	 * string.
	 * 
	 * @param src byte[] data
	 * 
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src, int byteLength) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || byteLength <= 0) {
			return null;
		}
		for (int i = 0; i < byteLength; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 连接读卡器
	 * 
	 * @return 0 成功 -1 失败
	 */
	public static int LinkReaderPro() {

		// 读卡，串口号根据配置文件读115200
		hReader = NJSMCardDriver.LinkReaderPro(1, 57600);
		if (isDebug) {
			System.out.println("连接读卡器返回值=============" + hReader);
		}
		// 判断返回值
		if (hReader < 0) {
			return -1;
		}
		// 设置交易流水号 ,接口文档没提到？？？？？？

		return 0;
	}

	/**
	 * 读卡号和卡类型（不分Desfire卡和M1卡） parm(cardType,cardNO,err)
	 * 
	 * @param parm
	 * @return
	 */
	public static int ReadCardNO(TParm parm) {
		// 判断返回值
		if (hReader < 0) {
			parm.setData("err", "请先连接读卡器");
			return -1;
		}
		STCardInfo stCardInfo = new STCardInfo();
		// 调用寻卡函数，这一步是必须调用的
		int ret = NJSMCardDriver.FoundCardPro(hReader, stCardInfo);
		// 判断返回值
		if (ReturnRet(ret, parm) == -1) {
			return -1;
		}
		//
		String strCardType = Byte.toString(stCardInfo.getCardType());
		int intCardType = Integer.valueOf(strCardType);
		String strHexCardType = Integer.toHexString(intCardType);
		if (isDebug) {
			System.out.println("strCardType卡类型===" + strHexCardType);// 卡类型，16进制转换
		}

		// System.out.println("uid leng====="+Integer.valueOf(new
		// String(stCardInfo.getUid()),16));
		// 1A为M1(C)卡
		// DF为Desfire(A,B)卡

		// 如果是C卡
		if (strHexCardType.equalsIgnoreCase("1A")) {
			// 必须先进行验证,每次验证只能操作一个扇区
			ret = NJSMCardDriver.M1_AuthPro(hReader, 2);
			if (ReturnRet(ret, parm) == -1) {
				// System.out.println("认证有错了");
				return -1;
			}
			// 读取数据
			M1Data m1Data = new M1Data();
			ret = NJSMCardDriver.M1_ReadData(hReader, 2, m1Data);
			// 判断返回值
			if (ReturnRet(ret, parm) == -1) {
				return -1;
			}
			// 由于M1的卡面号是BCD码存储的，所有需要将BCD码转换为10进制的编码
			// BCD码转10进制是按照每4位对应一个10进制数进行转换，例如：1001（BCD）=9（10进制）
			// 那么返回的16个字节 前8位为卡面号的字节。需要先将前8个字节转换为2进制，然后在按照每4位获得一个10进制数得出一个卡面号
			// 91 10 00 01 00 00 94 73
			byte buteb[] = m1Data.getpData();
			String kamianhao = ReadCardService.getM1CardNo(buteb);
			// 设置卡号
			parm.setData("cardNO", kamianhao);

			// Desfire卡
		} else if (strCardType.equalsIgnoreCase("DF")) {
			STDFAuth stDFAuth = new STDFAuth();
			stDFAuth.setDfaID(new byte[] { 0x01, 0x00, 0x00 });
			stDFAuth.keyNo = 0x01;
			stDFAuth.psamAPP = 0x11;
			stDFAuth.psamKeyIndex = 0x01;
			stDFAuth.psamKeyType = 0x01;
			ret = NJSMCardDriver.DF_AuthPro(hReader, stDFAuth);
			// 判断返回值
			if (ReturnRet(ret, parm) == -1) {
				return -1;
			}
			// 读取公共信息
			STDFInfo stDFInfo = new STDFInfo();
			ret = NJSMCardDriver.DF_ReadCardInfo(hReader, stDFInfo);
			// 判断返回值
			if (ReturnRet(ret, parm) == -1) {
				return -1;
			}

			// 卡面号
			String kamianhao = "";
			for (int i = 0; i < stDFInfo.getKamianhao().length; i++) {
				// 可能有问题？？？？？
				String strByte = Integer.valueOf(
						Byte.toString(stDFInfo.getKamianhao()[i]), 16)
						.toString();// 16转换为10进制
				if (isDebug) {
					System.out.println("Desfire卡字节为：" + strByte);
				}
				// 补足两位补0
				if (strByte.length() < 2) {
					strByte = addZeroForNum(strByte, 2);
				}
				kamianhao += strByte;

			}
			parm.setData("cardNO", kamianhao);

		} else {
			parm.setData("ERR_FLG", "未知卡类型");
			return -1;
		}
		// 翁呜声;
		NJSMCardDriver.TK_PCD_Beep(255);

		return 0;

	}

	/**
	 * 读卡信息（不分Desfire卡和M1卡）
	 * 
	 * @param parm
	 *            /// <param name="cardNO">返回的卡面号 长度16</param> /// <param
	 *            name="identifyNO">返回的身份证号</param> /// <param
	 *            name="patientName">返回的姓名</param> /// <param
	 *            name="healthNO">返回的健康档案号</param> /// <param
	 *            name="siType">返回的医保类型</param> /// <param
	 *            name="siNO">返回的医保号</param> /// <param
	 *            name="err">返回的错误信息</param>
	 * 
	 * @return -1 失败 0 成功
	 */
	public static int ReadCardInfo(TParm parm) {

		// 判断返回值
		if (hReader < 0) {
			parm.setData(ERR_FLG, "连接读卡器失败");
			return -1;
		}
		STCardInfo stCardInfo = new STCardInfo();

		// 调用寻卡函数，这一步是必须调用的
		int i = NJSMCardDriver.FoundCardPro(hReader, stCardInfo);
		// 判断返回值
		if (ReturnRet(i, parm) == -1) {
			return -1;
		}

		// 返回卡类型
		String strCardType = Byte.toString(stCardInfo.getCardType());
		int intCardType = Integer.valueOf(strCardType);
		String strHexCardType = Integer.toHexString(intCardType);
		if (isDebug) {
			System.out.println("strCardType卡类型===" + strHexCardType);
		}
		// 如果是C卡
		if (strHexCardType.equalsIgnoreCase("1A")) {
			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 2);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 读取数据
			M1Data m1Data = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 2, m1Data);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 由于M1的卡面号是BCD码存储的，所有需要将BCD码转换为10进制的编码
			// BCD码转10进制是按照每4位对应一个10进制数进行转换，例如：1001（BCD）=9（10进制）
			// 那么返回的16个字节 前8位为卡面号的字节。需要先将前8个字节转换为2进制，然后在按照每4位获得一个10进制数得出一个卡面号
			// 91 10 00 01 00 00 94 73
			byte cardData[] = m1Data.getpData();
			String kamianhao = ReadCardService.getM1CardNo(cardData);// 卡面号
			parm.setData("cardNO", kamianhao);

			// 将28-33块（其中31块不允许读写）的信息读取出来
			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 28);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 读取数据
			M1Data b28 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 28, b28);
			// 读取数据
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 29);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 读取数据
			M1Data b29 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 29, b29);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 30);
			if (ReturnRet(i, parm) == -1) {
				return -1;

			}

			M1Data b30 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 30, b30);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 32);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			M1Data b32 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 32, b32);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 33);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			M1Data b33 = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 33, b33);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 连接起来
			// 0-19 为患者信息
			byte[] bpatientName = new byte[] { b28.pData[0], b28.pData[1],
					b28.pData[2], b28.pData[3], b28.pData[4], b28.pData[5],
					b28.pData[6], b28.pData[7], b28.pData[8], b28.pData[9],
					b28.pData[10], b28.pData[11], b28.pData[12], b28.pData[13],
					b28.pData[14], b28.pData[15], b29.pData[0], b29.pData[1],
					b29.pData[2], b29.pData[3] };
			try {
				String strPatientName = new String(bpatientName, "gb2312");
				if (isDebug) {
					System.out.println("===strPatientName===" + strPatientName);
				}
				parm.setData("patientName", strPatientName);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 20-39为健康档案号
			byte[] bhealthNO = new byte[] { b29.pData[4], b29.pData[5],
					b29.pData[6], b29.pData[7], b29.pData[8], b29.pData[9],
					b29.pData[10], b29.pData[11], b29.pData[12], b29.pData[13],
					b29.pData[14], b29.pData[15], b30.pData[0], b30.pData[1],
					b30.pData[2], b30.pData[3], b30.pData[4], b30.pData[5],
					b30.pData[6], b30.pData[7] };
			try {
				String strHealthNO = new String(bhealthNO, "gb2312");
				if (isDebug) {
					System.out.println("===strHealthNO===" + strHealthNO);
				}
				parm.setData("healthNO", strHealthNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 40-41为医保类型
			byte[] bsiType = new byte[] { b30.pData[8], b30.pData[9] };
			try {
				String strSiType = new String(bsiType, "gb2312");
				if (isDebug) {
					System.out.println("===strSiType===" + strSiType);
				}
				parm.setData("siType", strSiType);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 42-61为医保号
			byte[] bsiNO = new byte[] { b30.pData[10], b30.pData[11],
					b30.pData[12], b30.pData[13], b30.pData[14], b30.pData[15],
					b32.pData[0], b32.pData[1], b32.pData[2], b32.pData[3],
					b32.pData[4], b32.pData[5], b32.pData[6], b32.pData[7],
					b32.pData[8], b32.pData[9], b32.pData[10], b32.pData[11],
					b32.pData[12], b32.pData[13] };
			try {
				String strSiNO = new String(bsiNO, "gb2312");
				if (isDebug) {
					System.out.println("===strSiNO===" + strSiNO);
				}
				parm.setData("siNO", strSiNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 40-41为身份证号
			byte[] bidentifityNO = new byte[] { b32.pData[14], b32.pData[15],
					b33.pData[0], b33.pData[1], b33.pData[2], b33.pData[3],
					b33.pData[4], b33.pData[5], b33.pData[6], b33.pData[7],
					b33.pData[8], b33.pData[9], b33.pData[10], b33.pData[11],
					b33.pData[12], b33.pData[13], b33.pData[14], b33.pData[15] };

			try {
				String strIdentifityNO = new String(bidentifityNO, "gb2312");
				if (isDebug) {
					System.out.println("===strIdentifityNO==="
							+ strIdentifityNO);
				}
				parm.setData("identifyNO", strIdentifityNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}

				return -1;
			}
			// A，B卡
		} else if (strHexCardType.equalsIgnoreCase("DF")) {
			// TODO 因没有卡， 暂没有测试；
			STDFAuth stDFAuth = new STDFAuth();
			stDFAuth.setDfaID(new byte[] { 0x01, 0x00, 0x00 });
			stDFAuth.keyNo = 0x01;
			stDFAuth.psamAPP = 0x11;
			stDFAuth.psamKeyIndex = 0x01;
			stDFAuth.psamKeyType = 0x01;
			i = NJSMCardDriver.DF_AuthPro(hReader, stDFAuth);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 读取公共信息
			STDFInfo stDFInfo = new STDFInfo();
			i = NJSMCardDriver.DF_ReadCardInfo(hReader, stDFInfo);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 卡面号
			String kamianhao = getDCardNo(stDFInfo.getKamianhao());
			parm.setData("cardNO", kamianhao);
			// 证件类型（01为身份证号）
			if (getHexString(stDFInfo.getZhengjianleixing()).equalsIgnoreCase(
					"01")) {
				String zhengjianbianhao = "";
				for (int j = 0; j < stDFInfo.getZhengjianbianhao().length; j++) {
					String strByte = getString(
							stDFInfo.getZhengjianbianhao()[j], 16);
					// 补足两位补0
					if (strByte.length() < 2) {
						strByte = addZeroForNum(strByte, 2);
					}
					zhengjianbianhao += strByte;// 转换为16进制
				}
				parm.setData("identifyNO", zhengjianbianhao);

			}

			// YY认证
			i = NJSMCardYYDriver.YYAuthPro(hReader);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			NJYY njyy = new NJYY();
			i = NJSMCardYYDriver.YYReadData(hReader, njyy);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			//
			try {
				String strPatientName = new String(njyy.getSickName(),
						STRING_CODING);
				parm.setData("patientName", strPatientName);

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			byte[] bHealthNO = new byte[] { njyy.getErecordNo()[0],
					njyy.getErecordNo()[1], njyy.getErecordNo()[2],
					njyy.getErecordNo()[3], njyy.getErecordNo()[4],
					njyy.getErecordNo()[5], njyy.getErecordNo()[6],
					njyy.getErecordNo()[7], njyy.getErecordNo()[8],
					njyy.getErecordNo()[9], njyy.getErecordNo()[10],
					njyy.getErecordNo()[11], njyy.getErecordNo()[12],
					njyy.getErecordNo()[13], njyy.getErecordNo()[14],
					njyy.getErecordNo()[15], njyy.getErecordNo()[16],
					njyy.getErecordNo()[17], njyy.getErecordNo()[18],
					njyy.getErecordNo()[19] };

			try {
				String strHealthNO = new String(bHealthNO, STRING_CODING);
				parm.setData("healthNO", strHealthNO);

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			byte[] bSiType = new byte[] { njyy.getFlag()[0], njyy.getFlag()[1] };
			try {
				String strSiType = new String(bSiType, STRING_CODING);
				parm.setData("siType", strSiType);

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 为医保号
			byte[] bSiNO = new byte[] { njyy.getErecordNo()[20],
					njyy.getErecordNo()[21], njyy.getErecordNo()[22],
					njyy.getErecordNo()[23], njyy.getErecordNo()[24],
					njyy.getErecordNo()[25], njyy.getErecordNo()[26],
					njyy.getErecordNo()[27], njyy.getErecordNo()[28],
					njyy.getErecordNo()[29], njyy.getFlag()[2],
					njyy.getHspNo()[0], njyy.getHspNo()[1], njyy.getHspNo()[2],
					njyy.getHspNo()[3], njyy.getHspNo()[4], njyy.getHspNo()[5],
					njyy.getHspNo()[6], njyy.getHspNo()[7], njyy.getHspNo()[8] };
			try {
				String strSiNO = new String(bSiNO, "gb2312");
				if (isDebug) {
					System.out.println("===strSiNO===" + strSiNO);
				}
				parm.setData("siNO", strSiNO);
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}

		} else {
			if (isDebug) {
				System.out.println("============未知卡类型=========");
			}
			parm.setData(ERR_FLG, "未知卡类型");
			return -1;
		}
		return 0;

	}

	/**
	 * 写卡信息（不分Desfire卡和M1卡）;
	 * 
	 * @param parm
	 *            <param name="cardNO">卡面号 长度16 用来进行比对是否正确</param> <param
	 *            name="identifyNO">身份证号</param> <param
	 *            name="patientName">姓名</param> <param
	 *            name="healthNO">健康档案号</param> <param
	 *            name="siType">医保类型</param> <param name="siNO">医保号</param>
	 *            <param name="err">错误信息</param>
	 * @return
	 */
	public static int WriteCardInfo(TParm parm) {
		String cardNO = parm.getValue("cardNO");
		String identifyNO = parm.getValue("identifyNO");
		String patientName = parm.getValue("patientName");
		String healthNO = parm.getValue("healthNO");
		String siType = parm.getValue("siType");
		String siNO = parm.getValue("siNO");

		// 判断返回值
		if (hReader < 0) {
			parm.setData(ERR_FLG, "连接读卡器失败");
			return -1;
		}
		// 初始化内存
		STCardInfo st = new STCardInfo();
		// 调用寻卡函数，这一步是必须调用的
		int i = NJSMCardDriver.FoundCardPro(hReader, st);
		// 判断返回值
		if (ReturnRet(i, parm) == -1) {
			return -1;
		}
		String strCardType = Byte.toString(st.getCardType());
		int intCardType = Integer.valueOf(strCardType);
		String strHexCardType = Integer.toHexString(intCardType);
		//
		if (isDebug) {
			System.out.println("市民卡类型===" + strHexCardType);
		}
		// M1卡写入
		// 必须先进行验证,每次验证只能操作一个扇区
		if (strHexCardType.equalsIgnoreCase("1A")) {
			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 2);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 读取数据
			// 读取数据
			M1Data m1Data = new M1Data();
			i = NJSMCardDriver.M1_ReadData(hReader, 2, m1Data);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 由于M1的卡面号是BCD码存储的，所有需要将BCD码转换为10进制的编码
			// BCD码转10进制是按照每4位对应一个10进制数进行转换，例如：1001（BCD）=9（10进制）
			// 那么返回的16个字节 前8位为卡面号的字节。需要先将前8个字节转换为2进制，然后在按照每4位获得一个10进制数得出一个卡面号
			// 91 10 00 01 00 00 94 73
			byte cardData[] = m1Data.getpData();
			String kamianhao = ReadCardService.getM1CardNo(cardData);// 卡面号
			if (!cardNO.equalsIgnoreCase(kamianhao)) {
				parm.setData(ERR_FLG, "卡号不一致，请检查");
				return -1;
			}
			// 0-19 为患者信息
			byte[] bpatientName = null;
			// 20-39为健康档案号
			byte[] bhealthNO = null;
			// 40-41为医保类型
			byte[] bsiType = null;
			// 42-61为医保号
			byte[] bsiNO = null;
			// 40-41为身份证号
			byte[] bidentifityNO = null;
			//
			try {
				int length = patientName.getBytes(STRING_CODING).length;
				if (length > 20) {
					parm.setData(ERR_FLG, "患者姓名太长");
					return -1;
				} else {
					bpatientName = new byte[20];
					byte[] temp = patientName.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bpatientName[m] = temp[m];
					}

				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			try {
				int length = healthNO.getBytes(STRING_CODING).length;
				if (length > 20) {
					parm.setData(ERR_FLG, "健康档案号太长");
					return -1;
				} else {
					bhealthNO = new byte[20];
					byte[] temp = healthNO.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bhealthNO[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			try {
				int length = siType.getBytes(STRING_CODING).length;
				if (length > 2) {
					parm.setData(ERR_FLG, "医保类型太长");
					return -1;
				} else {
					bsiType = new byte[20];
					byte[] temp = siType.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bsiType[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 42-61为医保号
			try {
				int length = siNO.getBytes(STRING_CODING).length;
				if (length > 20) {
					parm.setData(ERR_FLG, "医保号太长");
					return -1;
				} else {
					bsiNO = new byte[20];
					byte[] temp = siNO.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bsiNO[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}

			// 40-41为身份证号
			try {
				int length = identifyNO.getBytes(STRING_CODING).length;
				if (length > 18) {
					parm.setData(ERR_FLG, "身份证号太长");
					return -1;
				} else {
					bidentifityNO = new byte[20];
					byte[] temp = identifyNO.getBytes(STRING_CODING);
					for (int m = 0; m < length; m++) {
						bidentifityNO[m] = temp[m];
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			//
			M1Data data28 = new M1Data();
			byte block28[] = data28.getpData();
			block28 = new byte[] { bpatientName[0], bpatientName[1],
					bpatientName[2], bpatientName[3], bpatientName[4],
					bpatientName[5], bpatientName[6], bpatientName[7],
					bpatientName[8], bpatientName[9], bpatientName[10],
					bpatientName[11], bpatientName[12], bpatientName[13],
					bpatientName[14], bpatientName[15] };

			M1Data data29 = new M1Data();
			byte block29[] = data29.getpData();
			block29 = new byte[] { bpatientName[16], bpatientName[17],
					bpatientName[18], bpatientName[19], bhealthNO[0],
					bhealthNO[1], bhealthNO[2], bhealthNO[3], bhealthNO[4],
					bhealthNO[5], bhealthNO[6], bhealthNO[7], bhealthNO[8],
					bhealthNO[9], bhealthNO[10], bhealthNO[11], };

			M1Data data30 = new M1Data();
			byte block30[] = data30.getpData();
			block30 = new byte[] { bhealthNO[12], bhealthNO[13], bhealthNO[14],
					bhealthNO[15], bhealthNO[16], bhealthNO[17], bhealthNO[18],
					bhealthNO[19], bsiType[0], bsiType[1], bsiNO[0], bsiNO[1],
					bsiNO[2], bsiNO[3], bsiNO[4], bsiNO[5] };

			M1Data data32 = new M1Data();
			byte block32[] = data32.getpData();
			block32 = new byte[] { bsiNO[6], bsiNO[7], bsiNO[8], bsiNO[9],
					bsiNO[10], bsiNO[11], bsiNO[12], bsiNO[13], bsiNO[14],
					bsiNO[15], bsiNO[16], bsiNO[17], bsiNO[18], bsiNO[19],
					bidentifityNO[0], bidentifityNO[1] };
			M1Data data33 = new M1Data();
			byte block33[] = data33.getpData();
			block33 = new byte[] { bidentifityNO[2], bidentifityNO[3],
					bidentifityNO[4], bidentifityNO[5], bidentifityNO[6],
					bidentifityNO[7], bidentifityNO[8], bidentifityNO[9],
					bidentifityNO[10], bidentifityNO[11], bidentifityNO[12],
					bidentifityNO[13], bidentifityNO[14], bidentifityNO[15],
					bidentifityNO[16], bidentifityNO[17] };

			// 将28-33块（其中31块不允许读写）的信息读取出来
			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 28);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 写入数据
			i = NJSMCardDriver.M1_WriteData(hReader, 28, block28);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 29);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 写入数据
			i = NJSMCardDriver.M1_WriteData(hReader, 29, block29);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 30);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			i = NJSMCardDriver.M1_WriteData(hReader, 30, block30);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 32);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			i = NJSMCardDriver.M1_WriteData(hReader, 32, block32);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 必须先进行验证,每次验证只能操作一个扇区
			i = NJSMCardDriver.M1_AuthPro(hReader, 33);
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			i = NJSMCardDriver.M1_WriteData(hReader, 33, block33);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;

			}
			// 写卡操作结束

		} else if (strHexCardType.equalsIgnoreCase("DF")) {
			// TODO 暂无卡无法测试 Desfire卡写入
			// Desfire卡认证
			STDFAuth stDFAuth = new STDFAuth();
			stDFAuth.setDfaID(new byte[] { 0x01, 0x00, 0x00 });
			stDFAuth.keyNo = 0x01;
			stDFAuth.psamAPP = 0x11;
			stDFAuth.psamKeyIndex = 0x01;
			stDFAuth.psamKeyType = 0x01;
			i = NJSMCardDriver.DF_AuthPro(hReader, stDFAuth);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 读取公共信息
			STDFInfo stDFInfo = new STDFInfo();
			i = NJSMCardDriver.DF_ReadCardInfo(hReader, stDFInfo);

			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			// 卡面号
			String kamianhao = getDCardNo(stDFInfo.getKamianhao());
			if (cardNO != kamianhao) {
				parm.setData(ERR_FLG, "卡号不一致，请检查");
				return -1;
			}
			i = NJSMCardYYDriver.YYAuthPro(hReader);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}
			NJYY njyy = new NJYY();
			// 病患姓名
			try {
				byte[] bPatientName = patientName.getBytes(STRING_CODING);
				for (int j = 0; j < 20; j++) {
					if (j < bPatientName.length) {
						njyy.sickName[j] = bPatientName[j];
						// 不足20位补0;
					} else {
						njyy.sickName[j] = 0;
					}
				}

			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 医保类型
			byte[] bSiType;
			try {
				bSiType = siType.getBytes(STRING_CODING);
				for (int j = 0; j < 2; j++) {
					if (j < bSiType.length) {
						njyy.flag[j] = bSiType[j];
					} else {
						njyy.flag[j] = 0;
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}
			// 其中前20位为健康档案号 后10位 为医保号
			byte[] bIdentifyNO;
			try {
				bIdentifyNO = healthNO.getBytes(STRING_CODING);
				for (int j = 0; j < 20; j++) {
					if (j < bIdentifyNO.length) {
						njyy.erecordNo[j] = bIdentifyNO[j];
					} else {
						njyy.erecordNo[j] = 0;
					}
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
			}
			// 医保号
			byte[] bSiNO;
			try {
				bSiNO = siNO.getBytes(STRING_CODING);
				for (int j = 20; j < 30; j++) {
					if (j - 20 < bSiNO.length) {
						njyy.erecordNo[j] = bSiNO[j - 20];
					} else {
						njyy.erecordNo[j] = 0;
					}
				}
				if (bSiNO.length >= 11) {
					njyy.flag[2] = bSiNO[10];
				} else {
					njyy.flag[2] = 0;
				}
			} catch (UnsupportedEncodingException e) {
				if (isDebug) {
					e.printStackTrace();
				}
				return -1;
			}

			for (int j = 0; j < 9; j++) {
				if (j + 11 < bSiNO.length) {
					njyy.hspNo[j] = bSiNO[j + 11];
				} else {
					njyy.hspNo[j] = 0;
				}
			}
			// 写卡
			i = NJSMCardYYDriver.YYWriteData(hReader, njyy);
			// 判断返回值
			if (ReturnRet(i, parm) == -1) {
				return -1;
			}

			// 未知卡类型
		} else {
			parm.setData(ERR_FLG, "未知卡类型");
			return -1;
		}

		return 0;
	}

	/**
	 * 调用中心服务
	 * 
	 * @param actionName
	 *            Action名称
	 * @param actionMethod
	 *            调用方法名称
	 * @param parm
	 *            包含：parameter(传入传出参数)|err返回的错误信息
	 * @return -1 失败 0 成功 1 网络不通
	 */
	public static int InvokeISiteCenter(String actionName, String actionMethod,
			TParm parm) {
		int i;
		int li_result;
		int appCode = 0;
		// 读取配置文件;
		String path = new ReadCardService().getClass().getResource(
				"/com/javahis/device").getPath();
		IniReader reader = null;
		try {
			reader = new IniReader(path + "/eapagent.ini");
		} catch (IOException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			parm.setData(ERR_FLG, "读取配置文件失败.");
			return -1;
		}
		// 取中心服务器IP地址;
		String ipaddress = reader.getValue("HTTP_SERVER", "HOST");
		// 假如能通；
		if (ping(ipaddress)) {
			NJEapagentDriver.EapAgent_Init();
			NJEapagentDriver.EapAgent_Reset();
			NJEapagentDriver.EapAgent_SetAction(actionName);
			NJEapagentDriver.EapAgent_SetActionMethod(actionMethod);
			NJEapagentDriver.EapAgent_PutParameter("put", parm
					.getValue("parameter"));
			EapReturnParm ret_paramemter = new EapReturnParm();
			li_result = NJEapagentDriver.EapAgent_SendRequest();
			if (li_result != 0) {
				parm.setData(ERR_FLG, "请求服务端失败，请查看日志");
				return -1;
			} else {
				EapAppCode eapAppCode = new EapAppCode();
				NJEapagentDriver.EapAgent_GetAppCode(eapAppCode);
				appCode = eapAppCode.appCode;
				if (appCode != 0) {
					EapErrorMessage eapErrorMessage = new EapErrorMessage();
					i = NJEapagentDriver
							.EapAgent_GetBriefErrorMessage(eapErrorMessage);
					parm.setData(ERR_FLG, eapErrorMessage
							.getBriefErrorMessage());
					return -1;

				} else {
					i = NJEapagentDriver.EapAgent_GetParameter("out",
							ret_paramemter);
					parm.setData("parameter", ret_paramemter.getParaValue());
				}

			}

		} else {
			parm.setData(ERR_FLG, "网络不通，请检查网络");
			return 1;
		}

		return 0;
	}

	/**
	 * 执行ping命令;
	 * 
	 * @param host
	 *            主机地址
	 * @return
	 */
	private static boolean ping(String host) {
		int timeOut = 3000; // 超时应该在3钞以上
		boolean status = false;
		try {
			status = InetAddress.getByName(host).isReachable(timeOut);
		} catch (UnknownHostException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			return false;
		} catch (IOException e) {
			if (isDebug) {
				e.printStackTrace();
			}
			return false;
		}
		return status;
	}

	/**
	 * 获得M1卡卡号;
	 * 
	 * @param buteb
	 * @return
	 */
	private static String getM1CardNo(byte buteb[]) {
		String kamianhao = "";// 卡面号
		for (int i = 0; i < 8; i++) {
			// 二进制
			String strBin = Integer.toBinaryString(Integer.valueOf(Byte
					.toString(buteb[i])));
			if (isDebug) {
				System.out.println("字节为:" + strBin);
			}
			strBin = addZeroForNum(strBin, 8);
			if (isDebug) {
				System.out.println("字节补0后:" + strBin);
			}
			// 大于8位取后8位;
			if (strBin.length() > 8) {
				strBin = strBin.substring(strBin.length() - 8, strBin.length());
			}
			if (isDebug) {
				System.out.println("二进制为:" + strBin);
				System.out.println("二进制前4位:"
						+ String.valueOf(Integer.parseInt(strBin
								.substring(0, 4), 2)));
				System.out.println("二进制后4位====="
						+ String.valueOf(Integer.parseInt(strBin
								.substring(4, 8), 2)));
			}

			kamianhao += String.valueOf(Integer.parseInt(
					strBin.substring(0, 4), 2))
					+ String.valueOf(Integer
							.parseInt(strBin.substring(4, 8), 2));

		}
		if (isDebug) {
			System.out.println("==kamianhao==" + kamianhao);
		}
		return kamianhao;
	}

	/**
	 * 获得D卡的卡号
	 * 
	 * @param bKamianhao
	 * @return
	 */
	private static String getDCardNo(byte bKamianhao[]) {
		String kamianhao = "";
		for (int j = 0; j < bKamianhao.length; j++) {
			// 可能有问题？？？？？
			String strByte = Integer.valueOf(Byte.toString(bKamianhao[j]), 16)
					.toString();// 16转换为10进制
			if (isDebug) {
				System.out.println("Desfire卡字节为：" + strByte);
			}
			// 补足两位补0
			if (strByte.length() < 2) {
				strByte = addZeroForNum(strByte, 2);
			}
			kamianhao += strByte;

		}

		return kamianhao;

	}

	/**
	 * 将byte按进制，转成十进制字符串
	 * 
	 * @param b
	 * @param radix
	 * @return
	 */
	private static String getString(byte b, int radix) {

		return Integer.valueOf(Byte.toString(b), radix).toString();
	}

	/**
	 * 转成16进制字串
	 * 
	 * @param b
	 * @return
	 */
	private static String getHexString(byte b) {

		String strB = Byte.toString(b);
		int intB = Integer.valueOf(strB);
		String strHexByte = Integer.toHexString(intB);

		return strHexByte;
	}

	/*
	 * 
	 * 数字不足位数左补0
	 * 
	 * @param str
	 * 
	 * @param strLength
	 */
	private static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				// sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}

	public static void main(String args[]) {
		System.out.println("==============单元测试==============");
		// 1.初始化;
		/**
		 * int ret0 = NJSMCardDriver.init();
		 * System.out.println("====加载dll======" + ret0);
		 **/
		// 2.连接读卡器
		/**
		 * int ret1 = ReadCardService.LinkReaderPro();
		 * System.out.println("====连接情况======" + ret1);
		 **/
		// 3.读卡类型及号;
		// int ret2 = ReadCardService.ReadCardNO(new TParm());
		// System.out.println("====读卡类型及号=====" + ret2);

		// 4.读卡信息

		/**
		 * TParm p3 = new TParm(); int ret3 = ReadCardService.ReadCardInfo(p3);
		 * System.out.println("=====读卡信息ret3======" + ret3); if (ret3 == -1) {
		 * System.out.println("错误消息是=====" + p3.getData(ERR_FLG)); return; }
		 * System.out.println("卡号：" + p3.getValue("cardNO"));
		 * System.out.println("身份证号：" + p3.getValue("identifyNO"));
		 * System.out.println("姓名：" + p3.getValue("patientName"));
		 * System.out.println("健康档案号：" + p3.getValue("healthNO"));
		 * System.out.println("医保类型：" + p3.getValue("siType"));
		 * System.out.println("医保号：" + p3.getValue("siNO"));
		 **/

		// 5.写卡信息;
		// byte b = 0x11;
		// System.out.println("==test==" + Integer.valueOf(Byte.toString(b),
		// 16));

		// 6.释放读卡设备
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.注销TFReader.dll
		// int ret100 = NJSMCardDriver.close();

		// 8.获取配置文件
		// System.out.println(new
		// ReadCardService().getClass().getResource("/com/javahis/device").getPath());

		// 9.
		/**
		 * String path=new
		 * ReadCardService().getClass().getResource("/com/javahis/device"
		 * ).getPath(); try { IniReader reader = new
		 * IniReader(path+"/TFReaderConfig.ini"); String strPort =
		 * reader.getValue("COM", "port"); String strBaud =
		 * reader.getValue("COM", "Baud"); System.out.println("===strPort===" +
		 * strPort); System.out.println("===strBaud===" + strBaud);
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 **/
		// System.out.println("ping========="+ping("10.43.45.3"));

	}

	/**
	 * 读取ini配置文件类
	 * 
	 * @author lixiang
	 * 
	 */
	public static class IniReader {

		protected HashMap sections = new HashMap();
		private transient String currentSecion;
		private transient Properties current;

		public IniReader(String filename) throws IOException {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			read(reader);
			reader.close();
		}

		/**
		 * 
		 * @param reader
		 * @throws IOException
		 */
		protected void read(BufferedReader reader) throws IOException {
			String line;
			while ((line = reader.readLine()) != null) {
				parseLine(line);
			}
		}

		/**
		 * 
		 * @param line
		 */
		protected void parseLine(String line) {
			line = line.trim();
			if (line.matches("\\[.*\\]")) {
				// 如果是 JDK 1.4(不含1.4)以下版本，修改为
				// if (line.startsWith("[") && line.endsWith("]")) {
				if (current != null) {
					sections.put(currentSecion, current);
				}
				currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
				// JDK 低于 1.4 时
				// currentSection = line.substring(1， line.length() - 1);
				current = new Properties();
			} else if (line.matches(".*=.*")) {
				// JDK 低于 1.4 时
				// } else if (line.indexOf(’=’) >= 0) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name, value);
			}
		}

		/**
		 * 
		 * @param section
		 * @param name
		 * @return
		 */
		public String getValue(String section, String name) {
			Properties p = (Properties) sections.get(section);

			if (p == null) {
				return null;
			}

			String value = p.getProperty(name);
			return value;
		}

	}
}
