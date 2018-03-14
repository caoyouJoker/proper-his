package com.javahis.device;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * 容器RFID驱动类 ZK_RFID401 JAVA 驱动
 * 
 * @author lix@bluecore.com.cn
 * 
 */
public class ZK_RFID401JAVA {
	public static boolean loadFlg = true; // 有无Load Dll
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // 错误讯息

	public ZK_RFID401JAVA() {

	}

	static {
		try {
			System.loadLibrary("ZK_RFID401JAVA"); // 载入dll
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
	 * This function is used to automatically detect the communication port
	 * unoccupied by other application and attached with a reader.
	 * 
	 * @param Port
	 * @param ComAdr
	 * @param Baud
	 * @param FrmHandle
	 * @return
	 */
	public native static long AutoOpenComPort(long Port, Byte ComAdr,
			byte Baud, Long FrmHandle);

	/**
	 * 
	 * @param Port
	 * @param ComAdr
	 * @param Baud
	 * @param FrmHandle
	 * @return
	 */
	public native static long OpenComPort(long Port, Byte ComAdr, byte Baud,
			Long FrmHandle);

	/**
	 * 
	 * @return
	 */
	public native static long CloseComPort();

	/**
	 * 
	 * @param FrmHandle
	 * @return
	 */
	public native static long CloseSpecComPort(long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param VersionInfo
	 * @param ReaderType
	 * @param TrType
	 * @param dmaxfre
	 * @param dminfre
	 * @param powerdBm
	 * @param ScanTime
	 * @param Ant
	 * @param BeepEn
	 * @param OutputRep
	 * @param FrmHandle
	 * @return
	 */
	public native static long GetReaderInformation(Byte ComAdr,
			byte[] VersionInfo, Byte ReaderType, Byte TrType, Byte dmaxfre,
			Byte dminfre, Byte powerdBm, Byte ScanTime, Byte Ant, Byte BeepEn,
			Byte OutputRep, long FrmHandle);
	
	
	/**
	 * 
	 * @param ComAdr
	 * @param ComAdrData
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetAddress(Byte ComAdr, Byte ComAdrData, long FrmHandle);
	/**
	 * 
	 * @param ComAdr
	 * @param ScanTime
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetInventoryScanTime(Byte ComAdr, byte ScanTime, long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param powerDbm
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetRfPower(Byte ComAdr, byte powerDbm, long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param dmaxfre
	 * @param dminfre
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetRegion (Byte ComAdr, byte dmaxfre, byte dminfre, long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param baud
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetBaudRate (Byte ComAdr, byte baud, long FrmHandle);
	
	
	/**
	 * 
	 * @param ComAdr
	 * @param AvtiveTime
	 * @param SilentTime
	 * @param Times
	 * @param FrmHandle
	 * @return
	 */
	public native static long BuzzerAndLEDControl (Byte ComAdr,byte AvtiveTime,byte SilentTime,byte Times,long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param Read_mode
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetWorkMode(Byte ComAdr, byte Read_mode, long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param Read_mode
	 * @param Accuracy
	 * @param RepCondition
	 * @param RepPauseTime
	 * @param ReadPauseTim
	 * @param TagProtocol
	 * @param MaskMem
	 * @param MaskAdr
	 * @param MaskLen
	 * @param MaskData
	 * @param TriggerTime
	 * @param AdrTID
	 * @param LenTID
	 * @param FrmHandle
	 * @return
	 */
	public native static long GetSystemParameter(Byte ComAdr,
			Byte Read_mode, Byte Accuracy, Byte RepCondition,
			Byte RepPauseTime, Byte ReadPauseTim, Byte TagProtocol,
			Byte MaskMem, byte[] MaskAdr, Byte MaskLen, byte[] MaskData,
			Byte TriggerTime, Byte AdrTID, Byte LenTID, long FrmHandle);
	
	/**
	 * 设置Beep响声(有用)
	 * @param ComAdr
	 * @param BeepEn
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetBeepNotification(Byte ComAdr, boolean BeepEn, int FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param ClockTime
	 * @param FrmHandle
	 * @return
	 */
	public native static long  SetReal_timeClock(Byte ComAdr, byte[] ClockTime, long FrmHandle);
	
	/**
	 * 
	 * @param ComAdr
	 * @param ClockTime
	 * @param FrmHandle
	 * @return
	 */
	public native static long GetTime(Byte ComAdr,byte[] ClockTime, long FrmHandle);
	
	
	/**
	 * 获取标签所有信息(有用)
	 * @param ComAdr
	 * @param Data
	 * @param dataLength
	 * @param FrmHandle
	 * @return
	 */
	public native static long GetTagBufferInfo(Byte ComAdr, byte[] Data,long dataLength, long FrmHandle);
	
	
	/**
	 * The function is used to clear tags information in memery.
	 * @param ComAdr
	 * @param FrmHandle
	 * @return
	 */
	public native static long ClearTagBuffer(Byte ComAdr, long FrmHandle);
	
	/**
	 * 设置触发时间
	 * @param ComAdr
	 * @param TriggerTime
	 * @param FrmHandle
	 * @return
	 */
	public native static long SetTriggerTime(Byte ComAdr, byte TriggerTime, long FrmHandle);
	
	/**
	 * 连接端口 (有用)
	 * @param Port
	 * @param IPaddr
	 * @param ComAdr
	 * @param FrmHandle
	 * @return
	 */
	public native static long OpenNetPort(int Port,String IPaddr, Byte ComAdr, Integer FrmHandle);
	
	/**
	 * 关闭端口 (有用)
	 * @param FrmHandle
	 * @return
	 */
	public native static long CloseNetPort (int FrmHandle);
	
	/**
	 * The function is used to detect tags in the inductive area and get their EPC values(有用)
	 * @param ComAdr
	 * @param MaskMem
	 * @param MaskAdr
	 * @param MaskLen
	 * @param MaskData
	 * @param MaskFlag
	 * @param AdrTID
	 * @param LenTID
	 * @param TIDFlag
	 * @param EPClenandEPC   Output
	 * @param Ant   Output
	 * @param Totallen  Output.
	 * @param CardNum    Output
	 * @param FrmHandle
	 * @return
	 */
	public native static long Inventory_G2(Byte ComAdr, byte MaskMem,
			byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte MaskFlag,
			byte AdrTID, byte LenTID, byte TIDFlag, byte[] EPClenandEPC,
			Byte Ant, Integer Totallen, Integer CardNum, int FrmHandle);
	
	/**
	 * The function is used to read part or all of a Tag’s Password, EPC, TID,
	 * or User memory. To the word as a unit, start to read data from the
	 * designated address. 
	 * 
	 * @param ComAdr
	 * @param EPC
	 * @param Enum
	 * @param Mem
	 * @param WordPtr
	 * @param Num
	 * @param Password
	 * @param MaskMem
	 * @param MaskAdr
	 * @param MaskLen
	 * @param MaskData
	 * @param Data      Output
	 * @param errorcode   Output
	 * @param FrmHandle
	 * @return
	 */
	public native static long ReadData_G2(Byte ComAdr, byte[] EPC, byte Enum,
			byte Mem, byte WordPtr, byte Num, byte[] Password, byte MaskMem,
			byte[] MaskAdr, byte MaskLen, byte[] MaskData, byte[] Data,
			Byte errorcode, int FrmHandle);
	
	/**
	 * The function is used to write several words in a Tag’s Reserved, EPC, TID, or User memory.
	 * 写操作
	 * @param ComAdr
	 * @param EPC
	 * @param Wnum
	 * @param Enum
	 * @param Mem
	 * @param WordPtr
	 * @param Writedata
	 * @param Password
	 * @param MaskMem
	 * @param MaskAdr
	 * @param MaskLen
	 * @param MaskData
	 * @param errorcode
	 * @param FrmHandle
	 * @return
	 */
	public native static long WriteData_G2(Byte ComAdr, byte[] EPC, byte Wnum,
			byte Enum, byte Mem, byte WordPtr, byte[] Writedata,
			byte[] Password, byte MaskMem, byte[] MaskAdr, byte MaskLen,
			byte[] MaskData, Byte errorcode, int FrmHandle);
	

	/**
	 * 错误消息;
	 */
	static void setErrorMessage() {
		h.put(new Integer(Integer.valueOf("01", 16)), "Return before Inventory finished");
		h.put(new Integer(Integer.valueOf("02", 16)), "The Inventory-scan-time overflow");
		h.put(new Integer(Integer.valueOf("03", 16)), "More Data");
		h.put(new Integer(Integer.valueOf("04", 16)), "Reader module MCU is Full");
		h.put(new Integer(Integer.valueOf("05", 16)), "Access password error");
		h.put(new Integer(Integer.valueOf("09", 16)), "Destroy password error");
		
		h.put(new Integer(Integer.valueOf("0a", 16)), "Destroy password error cann’t be Zero");
		h.put(new Integer(Integer.valueOf("0b", 16)), "Tag Not Support the command");
		h.put(new Integer(Integer.valueOf("0c", 16)), "Use the commmand,Access Password Cann’t be Zero");
		h.put(new Integer(Integer.valueOf("0d", 16)), "Tag is protected,cannot set it again");
		h.put(new Integer(Integer.valueOf("0e", 16)), "Tag is unprotected,no need to reset it");
		h.put(new Integer(Integer.valueOf("10", 16)), "There is some locked bytes,write fail");
		h.put(new Integer(Integer.valueOf("11", 16)), "can not lock it");
		
		h.put(new Integer(Integer.valueOf("12", 16)), "is locked,cannot lock it again");
		h.put(new Integer(Integer.valueOf("13", 16)), "Save Fail,Can Use Before Power");
		h.put(new Integer(Integer.valueOf("14", 16)), "Cannot adjust");
		h.put(new Integer(Integer.valueOf("15", 16)), "Return before Inventory finished");
		h.put(new Integer(Integer.valueOf("16", 16)), "Inventory-Scan-Time overflow");
		h.put(new Integer(Integer.valueOf("17", 16)), "More Data");
		h.put(new Integer(Integer.valueOf("18", 16)), "Reader module MCU is full");
		h.put(new Integer(Integer.valueOf("19", 16)), "Not Support Command Or AccessPassword Cannot be Zero");
		
		
		h.put(new Integer(Integer.valueOf("F9", 16)), "Command execute error");
		h.put(new Integer(Integer.valueOf("FA", 16)), "Get Tag,Poor Communication,Inoperable");
		h.put(new Integer(Integer.valueOf("FB", 16)), "No Tag Operable");
		h.put(new Integer(Integer.valueOf("FD", 16)), "Command length wrong");
		h.put(new Integer(Integer.valueOf("FE", 16)), "Illegal command");
		h.put(new Integer(Integer.valueOf("FF", 16)), "Parameter Error");
		
		h.put(new Integer(Integer.valueOf("30", 16)), "Communication error");
		h.put(new Integer(Integer.valueOf("31", 16)), "CRC checksummat error");
		h.put(new Integer(Integer.valueOf("32", 16)), "Return data length error");
		h.put(new Integer(Integer.valueOf("33", 16)), "Communication busy");
		h.put(new Integer(Integer.valueOf("34", 16)), "Busy,command is being executed");
		h.put(new Integer(Integer.valueOf("35", 16)), "ComPort Opened");
		h.put(new Integer(Integer.valueOf("36", 16)), "ComPort Closed");
		h.put(new Integer(Integer.valueOf("37", 16)), "Invalid Handle");
		h.put(new Integer(Integer.valueOf("38", 16)), "Invalid Port");
		h.put(new Integer(Integer.valueOf("EE", 16)), "Return command error");
		
		
	}
	/**
	 * 获取容器中的EPC值(多个)
	 * @param ip
	 * @param port
	 * @return
	 */
	public static List<String> getEpcList(String strip,int port){
		List<String> list=new ArrayList<String>();
		int Port=port;//27011;
		String IPaddr=strip; //"192.168.1.250";
		
		byte MaskMem=0;
		byte[] MaskAdr=new byte[2];
		byte MaskLen=0;
		byte[] MaskData=new byte[100];
        byte MaskFlag=0;
        byte Ant=0;
        byte AdrTID = 0;
        byte LenTID = 0;
        byte TIDFlag = 0;
        
        Integer CardNum= new Integer(0);
        Integer Totallen = new Integer(0);      
        byte[] EPC=new byte[5000];
        String fInventory_EPC_List;
        boolean fIsInventoryScan;
        int EPClen,m;
        String s, sEPC;
        String temps;
		
		int rtn0 = ZK_RFID401JAVA.init();
		System.out.println("===rtn0==" + rtn0);
/*
		byte[] comadr = { (byte) 0xFF };
		byte ComAdr = Byte.parseByte("00", 16);

		byte ComAdr1 = (byte) 0xff;*/
		//String s1="F";
		//Byte comadr=Byte.valueOf(s1, 16);
		Byte comadr=new Byte((byte)0xff);
		Integer FrmHandle=new Integer(-1);
		//连接
		long rtn1=ZK_RFID401JAVA.OpenNetPort(Port, IPaddr, comadr, FrmHandle);
		//0 是正确
		System.out.println("--------OpenNetPort rtn1---------"+rtn1);
		//
		System.out.println("--------OpenNetPort comadr---------"+Byte.valueOf(comadr));
		//
		System.out.println("=======OpenNetPort->FrmHandle======"+FrmHandle);
		//设置响声
/*		boolean BeepEn=true;
		long rtn2=ZK_RFID401JAVA.SetBeepNotification(comadr, BeepEn, FrmHandle);
		System.out.println("--------SetBeepNotification rtn2---------"+rtn2);
		
	
		//读标签
		long fCmdRet;
		fCmdRet=ZK_RFID401JAVA.Inventory_G2(comadr, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, AdrTID, LenTID, TIDFlag, EPC, Ant, Totallen, CardNum, FrmHandle.intValue());
	    while(true){
			if(fCmdRet==251){
				fCmdRet=ZK_RFID401JAVA.Inventory_G2(comadr, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, AdrTID, LenTID, TIDFlag, EPC, Ant, Totallen, CardNum, FrmHandle.intValue());
				if(fCmdRet!=1||fCmdRet!=2||fCmdRet!=3||fCmdRet!=4||fCmdRet!=0xFB){
					continue;
				}
			}
			break;
		}
		System.out.println("---------Inventory_G2------"+fCmdRet);
		System.out.println("=========CardNum     ======"+CardNum);
		//test
		if ( (fCmdRet == 1)| (fCmdRet == 2)| (fCmdRet == 3)| (fCmdRet == 4)|(fCmdRet == 0xFB) )//代表已查找结束，
		{
			byte[] daw = new byte[Totallen];
			//拷贝数组
			//Array.Copy(EPC, daw, Totallen);
			System.arraycopy(EPC, 0, daw, 0, Totallen);
			temps=Uitltool.bytes_HexString(daw);
			System.out.println("=========temps========="+temps);
			//将byte数据转成16进制string
			fInventory_EPC_List=temps;
			m=0;
            if (CardNum==0)
            {
                fIsInventoryScan = false;
                return null;
            }
            
         //String antstr= Integer.toString(Ant, 2);
         
         for (int CardIndex = 0;CardIndex<CardNum;CardIndex++)
         {
        	 System.out.println("========CardIndex========="+CardIndex);
        	 EPClen = daw[m];
        	 System.out.println("===EPClen==="+EPClen);
        	 System.out.println("===start post==="+((m * 2) + 2));
        	 System.out.println("===end post==="+(((m * 2) + 2)+(EPClen * 2)));
        	 //EPClen*2+2
        	 //开始位置
        	 
        	 //结束位置
             sEPC = temps.substring(m * 2 + 2, (((m * 2) + 2)+(EPClen * 2)));
             list.add(sEPC);
             System.out.println("====sEPC====="+sEPC);
             m = m + EPClen + 1;
             //最后的位置，需要算一下
             
             System.out.println("==m=="+m);
             System.out.println("==sEPC length=="+sEPC.length());
             System.out.println("==EPClen=="+EPClen*2 );
             
             
             if (sEPC.length()!= EPClen*2 )
             break;            
             //判断是否在库表中是否存在             
             
        	 
         }
		}*/
		
		//关连接
	    long rtn9=ZK_RFID401JAVA.CloseNetPort(FrmHandle.intValue());
		//System.out.println("--------CloseNetPort---------"+rtn9);
		ZK_RFID401JAVA.free();
		
		return list;
		
	}
	
	
	/**
	 * 测试类
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		int Port=27011;
		String IPaddr="192.168.1.250";
		
		byte MaskMem=0;
		byte[] MaskAdr=new byte[2];
		byte MaskLen=0;
		byte[] MaskData=new byte[100];
        byte MaskFlag=0;
        byte Ant=0;
        byte AdrTID = 0;
        byte LenTID = 0;
        byte TIDFlag = 0;
        
        Integer CardNum= new Integer(0);
        Integer Totallen = new Integer(0);      
        byte[] EPC=new byte[5000];
        String fInventory_EPC_List;
        boolean fIsInventoryScan;
        int EPClen,m;
        String s, sEPC;
        String temps;
		
		int rtn0 = ZK_RFID401JAVA.init();
		//System.out.println("===rtn0==" + rtn0);

		Byte comadr=new Byte((byte)0xff);
		Integer FrmHandle=new Integer(-2);
		//连接
		long rtn1=ZK_RFID401JAVA.OpenNetPort(Port, IPaddr, comadr, FrmHandle);
		//0 是正确
		//System.out.println("--------OpenNetPort rtn1---------"+rtn1);
		//
		//System.out.println("--------OpenNetPort comadr---------"+Byte.valueOf(comadr));
		//
		//System.out.println("=======OpenNetPort->FrmHandle======"+FrmHandle);
		//设置响声
		boolean BeepEn=true;
		long rtn2=ZK_RFID401JAVA.SetBeepNotification(comadr, BeepEn, FrmHandle);
		//System.out.println("--------SetBeepNotification rtn2---------"+rtn2);
		
	
		//读标签
		long fCmdRet;
		fCmdRet=ZK_RFID401JAVA.Inventory_G2(comadr, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, AdrTID, LenTID, TIDFlag, EPC, Ant, Totallen, CardNum, FrmHandle.intValue());
	    while(true){
			if(fCmdRet==251){
				fCmdRet=ZK_RFID401JAVA.Inventory_G2(comadr, MaskMem, MaskAdr, MaskLen, MaskData, MaskFlag, AdrTID, LenTID, TIDFlag, EPC, Ant, Totallen, CardNum, FrmHandle.intValue());
				if(fCmdRet!=1||fCmdRet!=2||fCmdRet!=3||fCmdRet!=4||fCmdRet!=0xFB){
					continue;
				}
			}
			break;
		}
		//System.out.println("---------Inventory_G2------"+fCmdRet);
		System.out.println("=========CardNum     ======"+CardNum);
		//test
		if ( (fCmdRet == 1)| (fCmdRet == 2)| (fCmdRet == 3)| (fCmdRet == 4)|(fCmdRet == 0xFB) )//代表已查找结束，
		{
			byte[] daw = new byte[Totallen];
			//拷贝数组
			//Array.Copy(EPC, daw, Totallen);
			System.arraycopy(EPC, 0, daw, 0, Totallen);
			temps=Uitltool.bytes_HexString(daw);
			//System.out.println("=========temps========="+temps);
			//将byte数据转成16进制string
			fInventory_EPC_List=temps;
			m=0;
            if (CardNum==0)
            {
                fIsInventoryScan = false;
                return;
            }
            
         //String antstr= Integer.toString(Ant, 2);
         
         for (int CardIndex = 0;CardIndex<CardNum;CardIndex++)
         {
        	 System.out.println("========CardIndex========="+CardIndex);
        	 EPClen = daw[m];
        	 System.out.println("===EPClen==="+EPClen);
        	 System.out.println("===start post==="+((m * 2) + 2));
        	 System.out.println("===end post==="+(((m * 2) + 2)+(EPClen * 2)));
        	 //EPClen*2+2
        	 //开始位置
        	 
        	 //结束位置
             sEPC = temps.substring(m * 2 + 2, (((m * 2) + 2)+(EPClen * 2)));
             System.out.println("====sEPC====="+sEPC);
             m = m + EPClen + 1;
             //最后的位置，需要算一下
             
             System.out.println("==m=="+m);
             System.out.println("==sEPC length=="+sEPC.length());
             System.out.println("==EPClen=="+EPClen*2 );
             
             
             if (sEPC.length()!= EPClen*2 )
             return;            
             //判断是否在库表中是否存在             
             
        	 
         }
		}
		
		//关连接
	    long rtn9=ZK_RFID401JAVA.CloseNetPort(FrmHandle.intValue());
		System.out.println("--------CloseNetPort---------"+rtn9);
		ZK_RFID401JAVA.free();
		
	    //Integer.v
		//System.out.println("ddddd======="+Integer.valueOf("251", 16));
		System.out.println("ddddd======="+Integer.valueOf("55", 16));	

	}

}
