package jdo.bms;

import java.sql.Timestamp;
import java.util.TimerTask;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;


/**
 * <p>
 * Title: ��Ѫʱ�䳬��4Сʱ֪ͨTask
 * </p>
 * 
 * <p>
 * Description: ��Ѫʱ�䳬��4Сʱ֪ͨTask
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhanglei 2017.6.7
 * @version 1.0
 */
public class BMSBloodOutTask extends TimerTask {
	

	/**
	 * Socket���͹���
	 */
	private SocketLink client;


	private static BMSBloodOutTask instance = new BMSBloodOutTask();
	
	private BMSBloodOutTask(){
	}
	
	public static BMSBloodOutTask getInstance() {
		return instance;
	}
	

	/**
	 * ���ݲɼ�
	 */
	
	public void run() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String state = StringTool.rollDate(sysDate, -1).toString().substring(0, 10).
                replaceAll("-", "") + "000000" ;
		String end = sysDate.toString().substring(0, 10).replaceAll("-", "")
				+ "235959" ;
		//System.out.println("--------��Ѫʱ�䳬��4Сʱ֪ͨ-------- " + sysDate);
		//System.out.println("��ʼʱ�� " + state + "����ʱ�� " + end);
		String sql= "SELECT B.MR_NO, S.PAT_NAME, S.SEX_CODE, B.BLDTRANS_USER, "
				   + " B.BLDTRANS_TIME, A.BED_NO_DESC,B.OUT_DATE "
				   + " FROM SYS_PATINFO S, SYS_BED A, BMS_BLOOD B "
				   + " WHERE     B.MR_NO = S.MR_NO"
				   + " AND B.CASE_NO = A.CASE_NO"
				   + " AND B.BLDTRANS_TIME IS NOT NULL"
				   + " AND B.BLDTRANS_END_TIME IS NULL"
				   + " AND OUT_DATE BETWEEN TO_DATE ('" + state + "', "
				   //+ " AND OUT_DATE BETWEEN TO_DATE ('20170410000000', "
				   + " 'YYYYMMDDHH24MISS')"
	               + " AND TO_DATE ('" + end + "',"
	               //+ " AND TO_DATE ('20170410235959',"
	               + " 'YYYYMMDDHH24MISS')";
		System.out.println("--------��Ѫʱ�䳬��4Сʱ֪ͨ-------- " + sql);
		
		TParm parm = new TParm( TJDODBTool.getInstance().select(sql) );
		System.out.println("--------��Ѫʱ�䳬��4Сʱ֪ͨparm-------- " + parm);
		if(parm == null || parm.getErrCode() < 0) {
			System.out.println("��Ѫ��ʱ����:��ѯʧ��");
			return;
		}
		if(parm.getCount("MR_NO") <= 0) {
			System.out.println("��Ѫ��ʱ����:��������");
			return;
		}
		for(int i = 0 ;i<parm.getCount("MR_NO");i++){
			//�ж��Ƿ�ͨ����ʿ����ִ��
			if(parm.getValue("BLDTRANS_TIME",i).length() <= 0){
				System.out.println("�ǲ�����Ѫ " + i);
				continue;
			}
			//�����ݿ�鵽���Ա����ת���ɺ���
			if(getDiffInMinAndSec(parm.getTimestamp("OUT_DATE",i),sysDate) >= 240){
				if(parm.getValue("SEX_CODE",i).toString().equals("1")){
					parm.setData("SEX_CODE", i, "��");
				}else if(parm.getValue("SEX_CODE",i).toString().equals("2")){
					parm.setData("SEX_CODE", i, "Ů");
				}
			
//				String sqlUser = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '" + Operator.getID() + "'";
//				System.out.println("sqlUser:"+sqlUser);
//				TParm parmUser = new TParm(TJDODBTool.getInstance().select(sqlUser));
//				String pwd = OperatorTool.getInstance().decrypt(
//		                (String) parmUser.getData("USER_PASSWORD",0));
				
				//��÷������˺�����
				client = SocketLink
						.running("", TConfig.getSystemValue("BMS.BloodOutOvertimeID"), TConfig.getSystemValue("BMS.BloodOutOvertimePWD"));
				System.out.println("�û��� " + TConfig.getSystemValue("BMS.BloodOutOvertimeID") + " ���� " + TConfig.getSystemValue("BMS.BloodOutOvertimePWD"));
				if (client.isClose()) {
					System.out.println("�����^_<");
					System.out.println(client.getErrText());
					return;
				}
				//��ý����˵��˺� 
				client.sendMessage(parm.getValue("BLDTRANS_USER",i),"���������ţ�" + parm.getValue("MR_NO",i) + " ������" + parm.getValue("PAT_NAME",i) + 
						" �Ա�" + parm.getValue("SEX_CODE",i) + " ���ţ�" + parm.getValue("BED_NO_DESC",i) + " ��Ѫ�ѳ���4Сʱ");
				System.out.println("�������ʺ�" + parm.getValue("BLDTRANS_USER",i));
				if (client == null)
					return;
				client.close();
				
//				System.out.println("���������ţ�" + parm.getValue("MR_NO",i) + " ������" + parm.getValue("PAT_NAME",i) + 
//						" �Ա�" + parm.getValue("SEX_CODE",i) + " ���ţ�" + parm.getValue("BED_NO_DESC",i) + "��Ѫ����4Сʱ");
			}
//			System.out.println("ѭ��������" + i + " ��ǰʱ�䣺" + sysDate + " Ѫ����ʱ�䣺" + parm.getValue("OUT_DATE",i) + 
//			BLDTRANS_USER		" ʱ�� " + getDiffInMinAndSec(parm.getTimestamp("OUT_DATE",i),sysDate) + " ��Ѫ���ȣ�" + parm.getValue("BLDTRANS_TIME",i).length());
		}
//		try {
//			
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	


	/**
	 * ���� ʱ���  ��ʽ XX��XX��
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static int getDiffInMinAndSec(Timestamp t1, Timestamp t2) {
		if((t1 == null || t2 == null) || (t2.getTime() < t1.getTime()) ) {
			return 1;
		}

		if(t2.getTime() == t1.getTime()) {
			return 1;
		}
		
	    double diffSec =	(t2.getTime()  - t1.getTime()) / 1000D;
	    
		return  ((int)(diffSec / 60D));
	}
//	/**
//	 * ��ȡ TConfig.x
//	 *
//	 * @return TConfig
//	 */
//	private TConfig getProp() {
//		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
//		return config;
//	}
}
