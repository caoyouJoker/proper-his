package com.javahis.device;

/**
 * 
 * @author lixiang
 * 
 */
public class NJSMCardDriver {


	static {
		System.loadLibrary("NJSMCardDriver"); // 载入dll
	}

	/**
	 * 初始化连接DLL
	 * 
	 * @return int 1 成功 0 失败
	 */
	public native static int init();

	/**
	 * 注销DLL
	 * 
	 * @return
	 */
	public native static int close();

	/**
	 * 连接读卡器，每次界面打开时，初始化一次就可以
	 * 
	 * @param ComPort
	 *            串口号，例如com1，ComPort为1。
	 * @param Baud
	 *            波特率。固定值：115200。
	 * @return >=0 成功。其值作为调用其他函数的操作句柄。 <0 失败。
	 */
	public native static int LinkReaderPro(int ComPort, int Baud);

	/**
	 * 读取卡类型及卡内号
	 * 
	 * 用来读取卡类型及卡内号
	 * 
	 * @param hReader
	 *            //操作句柄。为LinkReaderPro的返回值
	 * @param pCardInfo
	 *            //返回的卡类型及卡内号信息，总长度为137，16进制存储。
	 * @return 参照公共返回值说明
	 */
	public native static int FoundCardPro(int hReader, STCardInfo pCardInfo);

	/**
	 * 释放读卡器
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @return 参照公共返回值说明
	 */
	public native static int FreeReader(int hReader);

	/**
	 * 设置交易文件生成目录，每次消费时必须指定目录
	 * 
	 * 
	 * @param LogDir
	 *            LogDir：存入的目录名（完整路径，\\为分隔符）例：“C:\\temp\\”
	 * @return 参照公共返回值说明
	 */
	public native static int SetLogDir(String LogDir);

	/**
	 * 完成对M1卡读写的认证（每次认证只能读写取一个扇区）
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @param BlockNo
	 *            块号，具体参照南京市就诊卡卡结构附录
	 * @return 参照公共返回值说明
	 * 
	 * 
	 */
	public native static int M1_AuthPro(int hReader, int BlockNo);

	/**
	 * M1读块函数
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @param BlockNo
	 *            块号，具体参照南京市就诊卡卡结构附录。
	 * @param pData
	 *            返回的信息，16位字节。
	 * @return 参照公共返回值说明
	 */
	public native static int M1_ReadData(int hReader, int BlockNo, M1Data m1Data);

	/**
	 * 完成对M1卡某一块的写操作
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @param BlockNo
	 *            块号，具体参照南京市就诊卡卡结构附录
	 * @param pData
	 *            写入的信息，16位
	 * @return 参照公共返回值说明
	 */
	public native static int M1_WriteData(int hReader, int BlockNo, byte[] pData);

	/**
	 * Desfire认证
	 * 
	 * 功能：完成对Desfire卡读、写、扣款等操作的认证
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @param pDFAuth
	 *            认证信息
	 * @return
	 */
	public native static int DF_AuthPro(int hReader, STDFAuth pDFAuth);

	/**
	 * Desfire读公共信息函数 功能：完成对Desfire卡公共信息的读取操作。
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @param pDFInfo
	 *            公共信息返回（85字节）16进制返回
	 * @return 参照公共返回值说明
	 */
	public native static int DF_ReadCardInfo(int hReader, STDFInfo pDFInfo);

	/**
	 * 公共钱包扣款函数
	 * 
	 *功能：完成对Desfire卡公共钱包扣款的操作。（目前只支持正交易）
	 * 
	 * @param hReader
	 *            操作句柄。为LinkReaderPro的返回值
	 * @param Amount
	 *            消费金额（100代表1元人民币）
	 * @param pBalance
	 *            余额（100代表1元人民币）
	 * @return 参照公共返回值说明
	 */
	public native static int DF_CardDebit(int hReader, int Amount,
			Balance pBalance);
	
	/**
	 * 读卡设置翁鸣声;
	 * @param iNums
	 * @return
	 */
	public native static int TK_PCD_Beep(int iNums);
	
}
