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
 * �����ĺ�һ����
 * @author lix
 *
 */
public class IccCardRWUtil {
	static Hashtable<Integer, String> h = new Hashtable<Integer, String>(); // ����ѶϢ
	boolean isDebug = true;
	/**
	 * ҽ�ƿ�����
	 * @param commPort
	 * @return
	 */
	public TParm readEKT(String commPort) {

		TParm result = new TParm();
		try {
			int start = 40; // �ӵڼ�λ��ʼд 40
			// int total=PatTool.getInstance().getMrNoLength() + 3;
			int total = 15;
			//
			byte[] data = new byte[total];
			int n = IccCardRW.iccCard.iIccOpenPort(0);
			if (isDebug) {
				System.out.println("====�豸���ӳɹ�������====" + n);
			}
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====�豸����ʧ�� ,����====" + result);
				}
				throw new Exception();
				//return result;
			}
			n = IccCardRW.iccCard.i4428MemoryCheckCardType();
			if (isDebug) {
				System.out.println("====������ ,����====" + n);
			}
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====У��ҽ�ƿ����� ,����====" + result);
				}
				throw new Exception();
				//return result;
			}
			n = IccCardRW.iccCard
					.i4428MemoryReadCard(start, total, data);
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====��ҽ�ƿ�,����====" + result);
				}
				throw new Exception();
				//return result;
			}
			if (data[0] == -1) {
				result.setErr(-1, "�˿�Ƭ�ǿտ�");
				if (isDebug) {
					System.out.println("====�˿�Ƭ�ǿտ�,����====" + result);
				}
				throw new Exception();
				//return result;
			}
			// ������Ƭ��������;
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
			//result.setErr(-1, "����ʧ��");
		}finally{
			
			IccCardRW.iccCard.vIccClosePort();		
		}
		//
		if(isDebug){
			System.out.println("====readEK����,����====" + result);
		}
		return result;
	}
	
	/**
	 * ҽ�ƿ�д��
	 * @param commPort
	 * @param cardNo   ����
	 * @param seq      �����
	 * @param price    ��������
	 * @return
	 */
	public TParm writeEKT(String commPort, String cardNo, String seq,String price) {
		TParm result = new TParm();
		String inputData=cardNo+seq;
		try {
			int start = 40; // �ӵڼ�λ��ʼд
			//int Total = PatTool.getInstance().getMrNoLength() +3;
			int total =15;
			byte[] data = inputData.getBytes(); // д��Data
			
			if (data.length != total) {
				result.setErr(-1, "�����������ӦΪ15");
				return result;
			}
			int n = IccCardRW.iccCard.iIccOpenPort(0);
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				if (isDebug) {
					System.out.println("====�豸����ʧ�� ,����====" + result);
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
			// д����
			n=IccCardRW.iccCard.i4428MemoryWriteCard(start, total, data);
			if (n != 0) {
				result.setErr(-1, this.getErrorMsg(n));
				throw new Exception();
			}

		} catch (Exception e) {
			e.printStackTrace();
			//result.setErr(-1, "д��ʧ��");
		}finally{
			if(isDebug){
				System.out.println("====�ر��豸====");
			}
			IccCardRW.iccCard.vIccClosePort();
		}
		//
		//
		if(isDebug){
			System.out.println("====writeEKT����,����====" + result);
		}
		return result;		
	}

	/**
	 * ���������֤
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
				System.out.println("===�������strData===="+strData);
				
			}
			//
			if(!StringUtils.isEmpty(strData)){
				String idcard[]=strData.split("\\|");
				System.out.println("length+++"+idcard.length);
				parm.setData("SID", idcard[0].trim());// SID
				parm.setData("PAT_NAME", idcard[1].trim());// ����
				parm.setData("SEX_CODE", idcard[2].trim().equals("��") ? "1" : "2");// �Ա�
				parm.setData("BRITHPLACE", idcard[3].trim());// ����
				parm.setData(
						"BIRTH_DATE",
						idcard[4].trim().substring(0, 4)
								+ "/"
								+ idcard[4].substring(4, 6)
								+ "/"
								+ idcard[4].trim()
										.substring(6, idcard[4].trim().length()));// ����
				parm.setData("IDNO", idcard[5].trim());// ���֤��
				parm.setData("RESID_ADDRESS",idcard[6].trim());// סַ
			}else{
				parm.setErr(-1, "δ������֤��Ϣ,�����²���");
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
			parm.setErr(-1, "δ������֤��Ϣ,�����²���");
			return parm;
		}*/	
		//
		// ͨ�����֤�Ų�ѯ������Ϣ		
		/*TParm infoParm = PatTool.getInstance().getInfoForIdNo(parm);
		if (infoParm.getCount() > 0) {
			parm = infoParm;
			// parm.setData("MESSAGE","�Ѵ��ڴ˾��ﲡ����Ϣ");
		} else {
			parm.setData("MESSAGE", "�����ڴ˾��ﲡ����Ϣ");
		}*/
		// delFolder(path);
		if(isDebug){
			System.out.println("===readIdCard===="+parm);
		}
		return parm;
	}
	
	/**
	 * ��ҽ����
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
			// ������Ƭ��������;
			//String strData = byte2Str(rapdu);
			if (isDebug) {
				for(int i=0;i<rapdu.length;i++){
					System.out.println("===readINSCard rapdu["+i+"]=" + rapdu[i]);
					//System.out.println("===readINSCard rapdu["+i+"]=" + Integer.toHexString(rapdu[i]));
				}
				String hrapDu=this.printHexString(rapdu);
				//
				String strData = byte2Str(rapdu);
				System.out.println("===�ַ��� dRapDu====" + strData);
				//
				String dRapDu=this.hexString2String(hrapDu);
				//
				System.out.println("===�ַ��� dRapDu====" + dRapDu);
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
	 * ��ȡ������ 
	 * @return  ������   INSCard �籣��| EKTCard ҽ�ƿ� |
	 */
	public  String getCardType(){
		String strCardType="INSCard";//�籣��
		//1.���ж�ҽ�ƿ�
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
		//2.�ж������֤��
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
		
		//3.�籣��
		return strCardType;
	}
	

	/**
	 * ��Ԫ���Գ���
	 * @param args
	 */
	public static void main(String args[]) {
		IccCardRWUtil cardDev = new IccCardRWUtil();
		//1.ҽ�ƿ���������
		//cardDev.readEKT("0");
		//2.ҽ�ƿ�д������
		//cardDev.writeEKT("0", "000012345678", "001", "0.00");
		//3.���������֤
		//cardDev.readIdCard();
		//4.�籣��
		//cardDev.readINSCard();  ����δ�ṩ�� ���ô���ˢ�ķ�ʽ
		//5.����̩�ĵ������ �Զ��жϿ�����
		System.out.println("===CardType==="+cardDev.getCardType());
		
		//System.out.println("16����"+Integer.toHexString(121));
		//BigInteger a=new BigInteger("6F",16);
		//System.out.println("10����"+a.toString());//131
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
	
    //��ָ��byte������16���Ƶ���ʽ��ӡ������̨   
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
       System.out.println("=16����  =="+ret); 
       return ret;   
      
    }  

    /** 
     * @Title:hexString2String 
     * @Description:16�����ַ���ת�ַ��� 
     * @param src 
     *            16�����ַ��� 
     * @return �ֽ����� 
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
		// System.out.println("����Data===>"+Total_Data);
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

	// ������ ���ձ�
	private String getErrorMsg(int error) {

		System.out.println("=====error111======="+Integer.toHexString(error));
		String msg = "";
		if (h.containsKey(new Integer(error)))
			msg = (String) h.get(new Integer(error));
		else
			msg = "�������";
		return msg;
	}
	
	/**
	 * ��ȡ�������֤·��
	 * @return
	 */
	public String getPhotoPath() {
		//C:\\IdCard
		String path = "C:\\IdCard";
		//
		/*path = getProp().getString("", "sid.path");
		if (path == null || path.trim().length() <= 0) {

		}*/
		//1.���粻�����򴴽�
		return path;
	}
	
	/**
	 * ��ȡ TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			//System.out.println("TConfig.x �ļ�û���ҵ���");
		}
		return config;
	}

	static void setErrorMessage() {
		h.put(new Integer(0XF000), "�򿪴���ʧ��"); // 0XF000 //�򿪴���ʧ��
		h.put(new Integer(0XF001), "���ճ�ʱ"); // 0XF001 //���ճ�ʱ
		h.put(new Integer(0XF002), "�û���ESC���˳�");// 0XF002 //�û���ESC���˳�
		h.put(new Integer(0XF003), "�����豸ʧ��");// 0XF003//�����豸ʧ��
		h.put(new Integer(0XF004), "��������ʧ��");// 0XF004//��������ʧ��
		h.put(new Integer(0XF010), "δ����hid�豸");// 0XF010//δ����hid�豸
		h.put(new Integer(0XF011), "û���ҵ�hid�豸");// 0XF011//û���ҵ�hid�豸
		h.put(new Integer(0XF012), "��ȡHID�豸��Ϣʧ��");// 0XF012 //��ȡHID�豸��Ϣʧ��
		h.put(new Integer(0XF013), "��ȡHID�豸ϸ����Ϣʧ��");// 0XF013//��ȡHID�豸ϸ����Ϣʧ��
		h.put(new Integer(0XF014), "Could not get HID preparsed data");// 0XF014//Could
																		// not
																		// get
																		// HID
																		// preparsed
																		// data!
		h.put(new Integer(0XF015), "����HidP_GetCaps ʧ��");// 0XF015
														// //����HidP_GetCaps ʧ��
		h.put(new Integer(0XF016), "�������ݵ���HidP_SetButtonsʧ��");// 0XF016
																// //�������ݵ���HidP_SetButtonsʧ��
		h.put(new Integer(0XF017), "����HidP_GetCaps ʧ��");// 0XF017
														// //�õ���Ʒ�������ַ���ʧ��GetProductString

		// ���ݴ���ʧ�ܷ���
		h.put(new Integer(0XA000), "δ֪����");// 0XA000//δ֪����
		h.put(new Integer(0xA001), "�ϲ�����������зǷ����ݣ��ϲ����ݱ�����0-F");// 0xA001//�ϲ�����������зǷ����ݣ��ϲ����ݱ�����0-F
		h.put(new Integer(0xA002), "�õ��м�����ʱû���ҵ���ͷ");// 0xA002//�õ��м�����ʱû���ҵ���ͷ
		h.put(new Integer(0xA003), "�õ��м�����ʱû���ҵ���β");// 0xA003//�õ��м�����ʱû���ҵ���β
		h.put(new Integer(0xA004), "����У�����");// 0xA004//����У�����
		h.put(new Integer(0XA005), "�˺�����ʱ��֧�ִ˹���");// 0XA005//�˺�����ʱ��֧�ִ˹���
		h.put(new Integer(0XA006), "�Ƿ�����");// 0XA006//�Ƿ�����

		// IC�����ش������
		h.put(new Integer(0X1001), "��֧�ֽӴ�ʽIC��"); // 0X1001//��֧�ֽӴ�ʽIC��
		h.put(new Integer(0X1002), "�Ӵ�ʽ�û�δ�嵽λ"); // 0X1002//�Ӵ�ʽ�û�δ�嵽λ
		h.put(new Integer(0X1003), "�Ӵ�ʽ�����ϵ�");// 0X1003//�Ӵ�ʽ�����ϵ�
		h.put(new Integer(0X1004), "�Ӵ�ʽ��δ�ϵ�");// 0X1004//�Ӵ�ʽ��δ�ϵ�
		h.put(new Integer(0x1005), "�Ӵ�ʽ���ϵ�ʧ��");// 0x1005//�Ӵ�ʽ���ϵ�ʧ��

		// psam
		h.put(new Integer(0X2001), "��֧��PSAM��");// 0X2001//��֧��PSAM��
		h.put(new Integer(0X2003), "PSAM���ϵ�");// 0X2003//PSAM���ϵ�
		h.put(new Integer(0X2004), "PSAM��δ�ϵ�");// 0X2004//PSAM��δ�ϵ�
		h.put(new Integer(0x2005), "PSAM���ϵ�ʧ��");// 0x2005//PSAM���ϵ�ʧ��
		h.put(new Integer(0X2006), "PSAM�����ݻ�Ӧ");// 0X2006//PSAM�����ݻ�Ӧ
		h.put(new Integer(0X2007), "PSAM ERR");// 0X2007//PSAM ERR

		// �ǽӴ�
		h.put(new Integer(0X3001), "��֧�ַǽӴ��û���");// 0X3001//��֧�ַǽӴ��û���
		h.put(new Integer(0X3004), "δ����ǽӴ�ʽ��");// 0X3004//δ����ǽӴ�ʽ��
		h.put(new Integer(0X3005), "�ǽӴ�ʽ������ʧ��");// 0X3005//�ǽӴ�ʽ������ʧ��
		h.put(new Integer(0X3006), "�ȴ��������Ӧ����ʱ,������ǽӴ�ʽ�û��������޻�Ӧ");// 0X3006//�ȴ��������Ӧ����ʱ,������ǽӴ�ʽ�û��������޻�Ӧ
		h.put(new Integer(0X3007), "�����ǽӴ�ʽ�û������ݳ��ִ���");// 0X3007//�����ǽӴ�ʽ�û������ݳ��ִ���
		h.put(new Integer(0X3008), "���ÿ�Halt״̬ʧ��");// 0X3008//���ÿ�Halt״̬ʧ��
		h.put(new Integer(0X3009), "�ж��ſ��ڸ�Ӧ��");// 0X3009//�ж��ſ��ڸ�Ӧ��
		h.put(new Integer(0X6001), "�Ӵ�ʽ�洢�������Ͳ���ȷ");// 0X6001//�Ӵ�ʽ�洢�������Ͳ���ȷ
		h.put(new Integer(0x5001), "��Ӧ����Ƭ�Ƕ���֤");// 0x5001//��Ӧ����Ƭ�Ƕ���֤
		h.put(new Integer(0x5004), "Ѱ��֤��ʧ��");// 0x5004//Ѱ��֤��ʧ��
		h.put(new Integer(0x5005), "ѡȡ֤��ʧ��");// 0x5005//ѡȡ֤��ʧ��

	}

}

interface IccCardRW extends Library {
	IccCardRW iccCard = (IccCardRW) Native.loadLibrary(
			(Platform.isWindows() ? ".\\iccCard" : "c"), IccCardRW.class);

	/**
	 * �����豸���ͣ��豸�˿ڼ��˿ڲ���
	 * 
	 * @param type
	 *            �豸���ͣ�����̬��֧��XXX, XXX, XXX
	 * @param port
	 *            �豸�˿ڣ����磺COM1, COM2, COM3, USB
	 * @return0���ɹ� ������ʧ�ܣ����庬����ο�����¼һ��
	 */
	int Desk_SetDeviceParam(String type, String port);

	/**
	 * �򿪶˿ڣ����Ӷ�����
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
	 * ��ȡ�������̼��汾��Ϣ
	 * 
	 * @param version
	 *            �������̼��汾�ţ����硱XXXX.XXXX��
	 * @return 0 ���ɹ�
	 */
	int Desk_GetVer(byte[] version);

	/**
	 * ��ȡ��̬��汾��Ϣ���˹��ܲ���Ҫ���Ӷ�����
	 * 
	 * @param libver
	 *            ������������̬��汾��Ϣ�����硱GWI IC20A Driver 1.0��
	 * @return 0���ɹ�
	 */
	int Desk_GetLibVer(byte[] libver);

	/**
	 * ��������λ�����ֶ��������ӣ��ͷ�IC�������ر���Ƶ���ߣ��������������״̬
	 * 
	 * @return 0���ɹ�
	 */
	int Desk_Reset();

	/**
	 * �����������û��ſ�������ʾ����������
	 * 
	 * @return
	 */
	int Desk_Beep();

	/**
	 * IC���ϵ�/�ǽӼ��Ƭ
	 * 
	 * @param cardno
	 *            ��������ţ����庬��ο�����¼���� atr ��IC���ϵ緵������ atrlen: :atr����
	 * 
	 * @return
	 */
	int Desk_IccPowerOn(int cardno, byte[] atr, IntByReference atrlen);

	/**
	 * IC��ͨ��
	 * 
	 * @param cardno
	 *            ������ţ����庬��ο�����¼����
	 * @param capdu_len
	 *            apdu��������ݳ���
	 * @param capdu
	 *            apdu��������
	 * @param rapdu
	 *            apdu��Ӧ���ݵĴ洢��
	 * @param rapdulen
	 *            apdu��Ӧ���ݵĳ���
	 * @return
	 */
	int Desk_IccApdu(int cardno, int capdu_len, byte[] capdu, byte[] rapdu,
			IntByReference rapdulen);

	/**
	 * IC���µ磬�ر���Ƶ����
	 * 
	 * @param cardno
	 *            ������ţ����庬��ο�����¼����
	 * @return
	 */
	int Desk_IccPowerOff(int cardno);

	/**
	 * ����M1���������ؿ�Ƭ���
	 * 
	 * @return
	 */
	int Desk_M1Active(byte[] snr);

	/**
	 * M1����֤��������
	 * 
	 * @param sectorid
	 *            �����ţ�1K����M1��������Ϊ0~15��4K����M1�����Ϊ0~63
	 * @param passtype
	 *            ��Կ���ͣ�=0ΪA��Կ��=1ΪB��Կ
	 * @param pwd
	 *            ���룬���� ��FFFFFFFFFFFF��
	 * @return 0 ���ɹ�
	 */
	int Desk_M1VerifyPwd(int sectorid, int passtype, byte[] pwd);

	/**
	 * ��ȡM1��������
	 * 
	 * @param blockid
	 *            ��ţ�0~63
	 * @param outbuf
	 *            ���ݴ洢��������17���ֽڴ�С
	 * @return 0 ���ɹ�
	 */
	int Desk_M1Read(int blockid, byte[] outbuf);

	/**
	 * ��BCD��ʽ��ȡM1��������
	 * 
	 * @param blockid
	 *            ��ţ�0~63
	 * @param outbuf
	 *            ���ݴ洢��������33���ֽڴ�С���������ݸ�ʽΪBCD���ַ���
	 * @return 0 ���ɹ�
	 */
	int Desk_M1ReadBCD(int blockid, byte[] outbuf);

	/**
	 * дM1��������
	 * 
	 * @param blockid
	 *            0~63
	 * @param databuf
	 *            �ԡ�\0���������ַ�����ԭ��д��M1�����������ġ�\0���ַ�������ַ������ȳ���16�ֽڣ�
	 *            ���Զ���ȡǰ��16���ֽ�д��M1��
	 * @return
	 */
	int Desk_M1Write(int blockid, byte[] databuf);

	/**
	 * ��BCD��ʽ��ȡM1��������
	 * 
	 * @param blockid
	 *            ��ţ�0~63
	 * @param databuf
	 *            BCD��ʽ�������ַ��������ȱ���Ϊ32
	 * @return 0 ���ɹ�
	 */
	int Desk_M1WriteBCD(int blockid, byte[] databuf);

	/**
	 * M1���µ�
	 * 
	 * @return 0 �C �ɹ�
	 */
	int Desk_M1_PowerOff();

	/**
	 * ������֤ID�����ؿ�ƬID
	 * 
	 * @param cardid
	 *            ������֤ID������ֽ�
	 * @return
	 */
	int Desk_ReadIDCardID(byte[] cardid);

	/**
	 * ���ôſ���ʾ��Ϣ
	 * 
	 * @param sw1
	 *            �Ƿ��лس���ʾ ��1���� 0û��
	 * @param sw2
	 *            �Ƿ��дŵ��ָ�����ʾ
	 * @param sw3
	 *            �Ƿ���ʾһ�ŵ���Ϣ
	 * @param sw4
	 *            �Ƿ���ʾ���ŵ���Ϣ
	 * @param sw5
	 *            �Ƿ���ʾ���ŵ���Ϣ
	 * @param sw6
	 *            �Ƿ���ʾ���ŵ�����
	 * @return 0���ɹ�
	 */
	int Desk_CkSet(int sw1, int sw2, int sw3, int sw4, int sw5, int sw6);

	/**
	 * �������������Ӷ���֤ģ��
	 * 
	 * @return
	 */
	int InitComm();

	/**
	 * ���������ڹر������ӵĶ˿ڣ�һ���ڵ���InitComm�ɹ�����ɶ����������á�
	 * 
	 * @return
	 */
	int CloseComm();

	/**
	 * ���������ڷ������֤����ѡ��
	 * 
	 * @return
	 */
	int Authenticate();

	/**
	 * ���������ڶ�ȡ���л�����Ϣ������������Ϣ��ͼ����Ϣ��������Ϣ�Ѿ��ֶν�����ÿһ�ֶ���Ϣ�Ѿ�����ʾΪ�ַ�����ͼ����Ϣ��������Ϊ�ļ�photo.
	 * bmp����sPhotoPathĿ¼��
	 * 
	 * @param sPhotoPath
	 *            ͼƬ����·����demo��Ĭ�ϱ�����dll���ڵ�ǰĿ¼��
	 * @param pMsg
	 *            ָ��������ı���Ϣ����Ҫ�ڵ���ʱ�����ڴ棬�ֽ�����С��186���������óɹ��󣬸��ֶε��ı���Ϣ�Ѿ�ת��Ϊ���ֽ���ʽ��
	 *            ����ʾΪ�ַ�����ʽ���ֶ����弰ƫ��ֵ������ʾ�����ֶ������Էָ�����|�����ָ���
	 * @param len
	 *            [out] ������ �������ַ����ȣ����Ը���ֵ��NULL����
	 * @return
	 */
	int ReadBaseMsg(String sPhotoPath, byte[] pMsg, IntByReference len);

	// ============4428 ҽ�ƿ�================

	/**
	 * iPortNo��0��hid�豸�����Ϊ�����豸��iPortNoΪ���ں�
	 * 
	 * @param iPortNo
	 * @return
	 */
	int iIccOpenPort(int iPortNo);

	/**
	 * �Ͽ��豸����
	 */
	void vIccClosePort();

	/**
	 * ��鿨���Ƿ���ȷ
	 * 
	 * @return
	 */
	int i4428MemoryCheckCardType();

	/**
	 * �˶Կ�����
	 * 
	 * @param iPinLen
	 *            �����������ֵΪ2
	 * @param upchPin
	 *            �����ַ���ָ��
	 * @return
	 */
	int i4428MemoryCheckCardPin(int iPinLen, String upchPin);

	/**
	 * ��ָ����ַ������
	 * 
	 * @param iAdd
	 *            ƫ�Ƶ�ַ����ֵ��Χ0��1023
	 * @param iDataLen
	 *            �ַ������ȣ���ֵ��Χ1��1024
	 * @param upchRetData
	 *            upchRetData ������������ŵ�ַָ��
	 * @return 0 ���ɹ�
	 */
	int i4428MemoryReadCard(int iAdd, int iDataLen, byte[] upchRetData);

	/**
	 * ��ָ����ַд����
	 * 
	 * @param iAdd
	 *            ƫ�Ƶ�ַ����ֵ��Χ0��1023
	 * @param iDataLen
	 *            �ַ������ȣ���ֵ��Χ1��1024
	 * @param upchWriteData
	 *            д������
	 * @return 0 ���ɹ�
	 */
	int i4428MemoryWriteCard(int iAdd, int iDataLen, byte[] upchWriteData);

	/**
	 * ����������������ֵ
	 * 
	 * @param piErrorCount
	 *            ����������ֵ���ָ��
	 * @return
	 */
	int i4428MemoryGetErrorCount(int[] piErrorCount);

}
