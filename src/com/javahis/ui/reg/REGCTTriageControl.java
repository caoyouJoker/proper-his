package com.javahis.ui.reg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JViewport;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.odi.ODIPatInfoControl;
import com.javahis.ui.sys.HorseRaceLamp;
import com.javahis.ui.sys.LEDEXECUI;
import com.javahis.ui.sys.LEDUI;
import com.javahis.ui.sys.LEDUI_NEW;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

import jdo.emr.EMRAMITool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;


/**
 * CT�Ҽ���
 * @author WangQing 20170316
 *
 */
public class REGCTTriageControl extends TControl {

	/**
	 * �����
	 */
	private HorseRaceLamp hrl;

	/**
	 * �����
	 */
	private LEDUI_NEW ledUi;

	public static String CT_ORDER_CODE = "Y0202";

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		// ���水ť������
		callFunction("UI|SAVE|setEnabled", true);
		//		this.openHRL();
		// ��LEDUI
		openLEDUI();	
		//		this.test(null);
	}

	/**
	 * �������
	 */
	public void openHRL() {
		Component com = (Component) this.getComponent();
		TParm parm = new TParm();
		parm.setData("STATION_CODE", "");
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while (com != null && !(com instanceof Frame))
			com = com.getParent();
		//				ledUi1 = new LEDEXECUI((Frame) com, this, parm, true);
		//		ledUi1.openWindow();
		hrl = new HorseRaceLamp((Frame) com);
		hrl.control = this;
		hrl.addMessage("��ʹ���Ĳ���֪ͨ������");
		hrl.setVisible(true);
	}

	/**
	 * �����˫���¼��ص�����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void doubleClickHRL(TParm parm) {
		// �򿪼��ﲡ������
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\odi\\ODIPatInfoUI.x", parm);
		if(result == null){
			return;
		}
		System.out.println("======result:::"+result);
		setValueForParm("CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CT_TRIAGE_NO",result);
		// ��������
		this.setValue("AGE", this.getAge(result.getTimestamp("BIRTH_DATE"), SystemTool.getInstance().getDate()));

		// �ж��Ƿ���ڼ�¼
		// �Ѵ��ڣ����ṩ��ѯ���ܣ������ڣ����ṩ���빦��
		String ctSql = "SELECT * FROM AMI_CT_RECORD WHERE CASE_NO = '" + result.getValue("CASE_NO") + "'";
		System.out.println("++++++ctSql:::"+ctSql);
		TParm ctParm = new TParm(TJDODBTool.getInstance().select(ctSql));
		if(ctParm.getCount() <= 0){// �����ڣ������µļ�¼,�������󱣴�
			// ���水ť����
			callFunction("UI|SAVE|setEnabled", true);

			String ctNoticeTime = "2017/03/15 08:08:08";// Ӧ�ô�������ϻ�ȡ
			String ctRespTime = "2017/03/15 08:08:08";// �������Ƶ�ʱ��
			String ctArriveTime = "2017/03/15 08:08:08";// �������Ƶ�ʱ��
			String patArriveTime = "2017/03/15 08:08:08";// ɨ�軼�������ʱ��

			this.setValue("CT_NOTICE_TIME", ctNoticeTime);

			this.setValue("CT_RESP_TIME", ctRespTime);

			this.setValue("CT_ARRIVE_TIME", ctArriveTime);

			this.setValue("PAT_ARRIVE_TIME", patArriveTime);

		}else{// �Ѵ��ڣ�ֱ�Ӹ�ҳ��
			// ���水ť������
			callFunction("UI|SAVE|setEnabled", true);

			this.setValue("CT_NOTICE_TIME", ctParm.getData("CT_NOTICE_TIME", 0));

			this.setValue("CT_RESP_TIME", ctParm.getData("CT_RESP_TIME", 0));

			this.setValue("CT_ARRIVE_TIME", ctParm.getData("CT_ARRIVE_TIME", 0));

			this.setValue("PAT_ARRIVE_TIME", ctParm.getData("PAT_ARRIVE_TIME", 0));

			this.setValue("CT_START_TIME", ctParm.getData("CT_START_TIME", 0));

			this.setValue("CT_REPORT_TIME", ctParm.getData("CT_REPORT_TIME", 0));		
		}
	}

	/**
	 * ����
	 */
	public void onSave(){
		TParm emrData = new TParm();
		emrData.setData("CASE_NO", this.getValueString("CASE_NO"));	// �����

		emrData.setData("CT_NOTICE_TIME", StringTool.getString(TypeTool.getTimestamp(getValue("CT_NOTICE_TIME")), "yyyy/MM/dd HH:mm:ss"));

		emrData.setData("CT_RESP_TIME", StringTool.getString(TypeTool.getTimestamp(getValue("CT_RESP_TIME")), "yyyy/MM/dd HH:mm:ss"));

		emrData.setData("CT_ARRIVE_TIME", StringTool.getString(TypeTool.getTimestamp(getValue("CT_ARRIVE_TIME")), "yyyy/MM/dd HH:mm:ss"));

		emrData.setData("PAT_ARRIVE_TIME", StringTool.getString(TypeTool.getTimestamp(getValue("PAT_ARRIVE_TIME")), "yyyy/MM/dd HH:mm:ss"));

		emrData.setData("CT_START_TIME", StringTool.getString(TypeTool.getTimestamp(getValue("CT_START_TIME")), "yyyy/MM/dd HH:mm:ss"));

		emrData.setData("CT_REPORT_TIME", StringTool.getString(TypeTool.getTimestamp(getValue("CT_REPORT_TIME")), "yyyy/MM/dd HH:mm:ss"));

		emrData.setData("OPT_USER", Operator.getID());
		emrData.setData("OPT_DATE", this.dateToString((Date) SystemTool.getInstance().getDate()));
		emrData.setData("OPT_TERM", Operator.getIP());

		String insertSql = "INSERT INTO AMI_CT_RECORD (CASE_NO, CT_NOTICE_TIME, CT_RESP_TIME, "
				+ "CT_ARRIVE_TIME, PAT_ARRIVE_TIME, CT_START_TIME, CT_REPORT_TIME, OPT_USER, OPT_DATE, OPT_TERM) "
				+ "VALUES('" + emrData.getData("CASE_NO") + "', " 
				+ "to_date ('"+ emrData.getData("CT_NOTICE_TIME") + "', 'YYYY/MM/DD HH24:MI:SS'), " 
				+ "to_date ('"+ emrData.getData("CT_RESP_TIME") + "', 'YYYY/MM/DD HH24:MI:SS'), "
				+ "to_date ('"+ emrData.getData("CT_ARRIVE_TIME") + "', 'YYYY/MM/DD HH24:MI:SS'), "
				+ "to_date ('"+ emrData.getData("PAT_ARRIVE_TIME") + "', 'YYYY/MM/DD HH24:MI:SS'), "
				+ "to_date ('"+ emrData.getData("CT_START_TIME") + "', 'YYYY/MM/DD HH24:MI:SS'), "
				+ "to_date ('"+ emrData.getData("CT_REPORT_TIME") + "', 'YYYY/MM/DD HH24:MI:SS'), '"
				+ emrData.getData("OPT_USER") + "', "
				+ "to_date ('"+ emrData.getData("OPT_DATE") + "', 'YYYY/MM/DD HH24:MI:SS'), '"
				+ emrData.getData("OPT_TERM") + "')";

		String updateSql = "UPDATE AMI_CT_RECORD SET "
				+ "CT_NOTICE_TIME=to_date('" + emrData.getData("CT_NOTICE_TIME") + "','yyyy/MM/dd HH24:MI:SS'),"
				+ "CT_RESP_TIME=to_date('" + emrData.getData("CT_RESP_TIME") + "','yyyy/MM/dd HH24:MI:SS'),"
				+ "CT_ARRIVE_TIME=to_date('" + emrData.getData("CT_ARRIVE_TIME") + "','yyyy/MM/dd HH24:MI:SS'),"
				+ "PAT_ARRIVE_TIME=to_date('" + emrData.getData("PAT_ARRIVE_TIME") + "','yyyy/MM/dd HH24:MI:SS'),"
				+ "CT_START_TIME=to_date('" + emrData.getData("CT_START_TIME") + "','yyyy/MM/dd HH24:MI:SS'),"
				+ "CT_REPORT_TIME=to_date('" + emrData.getData("CT_REPORT_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
				+ " WHERE CASE_NO='" + emrData.getData("CASE_NO") + "'";

		TParm result = null;

		TParm ctParm = EMRAMITool.getInstance().getAmiCTDataByCaseNo((String)emrData.getData("CASE_NO"));
		if (ctParm.getCount() > 0) {
			// ִ�и���
			result = new TParm(TJDODBTool.getInstance().update(updateSql));
		} else {
			// ִ�в���
			result = new TParm(TJDODBTool.getInstance().update(insertSql));
		}
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ�ܣ�");
			return;
		}
		this.messageBox("����ɹ���");
	}


	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CT_TRIAGE_NO;"
				+ "AGE;CT_LEVEL_CODE;CT_NOTICE_TIME;CT_RESP_TIME;CT_ARRIVE_TIME;"
				+ "PAT_ARRIVE_TIME;CT_START_TIME;CT_REPORT_TIME");
	}

	public void onQuery(){
		String caseNo = (String)getValue("CASE_NO");
		String mrNo = (String)getValue("MR_NO");
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
			setValue("MR_NO", pat.getMrNo());
		}

		// ���ɲ�ѯsql
		String sql = "";
		// CASE_NO ����ţ�MR_NO �����ţ�PAT_NAME ���������� SEX_CODE �Ա�BIRTH_DATE �������ڣ�CT_TRIAGE_NO CT���˺ţ�CT_LEVEL_CODE CT���˵ȼ�
		sql = "SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME, C.TRIAGE_NO as CT_TRIAGE_NO, C.LEVEL_CODE as CT_LEVEL_CODE,  "
				+ "B.SEX_CODE, B.BIRTH_DATE ,A.ENTER_ROUTE ,D.CT_NOTICE_TIME,D.CT_RESP_TIME,D.CT_ARRIVE_TIME, "
				+ "D.PAT_ARRIVE_TIME,D.CT_START_TIME,D.CT_REPORT_TIME "
				+ "FROM REG_PATADM A LEFT JOIN AMI_CT_RECORD D on A.CASE_NO=D.CASE_NO, SYS_PATINFO B , ERD_EVALUTION C "
				+ "WHERE A.MR_NO=B.MR_NO  "
				+ "AND A.CASE_NO=C.CASE_NO ";
		if((caseNo==null || "".equals(caseNo.trim())) &&					
				(mrNo==null || "".equals(mrNo.trim())))
			return ;

		if(caseNo!=null && !"".equals(caseNo.trim()))
			sql = sql + "AND A.CASE_NO='"+caseNo.trim()+"'";

		if(mrNo!=null && !"".equals(mrNo.trim()))
			sql = sql + "AND A.MR_NO='"+mrNo.trim()+"'";

		sql = sql + "ORDER BY A.CASE_NO";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			Timestamp bDay = result.getTimestamp("Data","BIRTH_DATE",0);
			this.setValue("CASE_NO", result.getData("Data","CASE_NO",0));
			this.setValue("MR_NO", result.getData("Data","MR_NO",0));
			this.setValue("PAT_NAME", result.getData("Data","PAT_NAME",0));
			this.setValue("SEX_CODE", result.getData("Data","SEX_CODE",0));
			this.setValue("CT_TRIAGE_NO", result.getData("Data","CT_TRIAGE_NO",0));
			this.setValue("CT_LEVEL_CODE", result.getData("Data","CT_LEVEL_CODE",0));
			this.setValue("AGE", this.getAge(bDay, SystemTool.getInstance().getDate()));
			this.setValue("CT_NOTICE_TIME", result.getTimestamp("Data","CT_NOTICE_TIME",0));	
			this.setValue("CT_RESP_TIME", result.getTimestamp("Data","CT_RESP_TIME",0));	
			this.setValue("CT_ARRIVE_TIME", result.getTimestamp("Data","CT_ARRIVE_TIME",0));	
			this.setValue("PAT_ARRIVE_TIME", result.getTimestamp("Data","PAT_ARRIVE_TIME",0));	
			this.setValue("CT_START_TIME", result.getTimestamp("Data","CT_START_TIME",0));	
			this.setValue("CT_REPORT_TIME", result.getTimestamp("Data","CT_REPORT_TIME",0));	
		}
	}

	/**
	 * ��ȡ�Ա���������
	 * @param sexCode
	 * @return
	 */
	public String getSexChnDesc(String sexCode){
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID = '" + sexCode +"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String sexChnDesc = result.getValue("CHN_DESC", 0);
		return sexChnDesc;
	}

	/**
	 * ��ȡ����
	 * @param birthDate
	 * @param sysDate
	 * @return
	 */
	public String getAge(Timestamp birthDate, Timestamp sysDate){
		return OdoUtil.showAge(birthDate, sysDate);
	}

	/**
	 * Date->String
	 * @param date
	 * @return
	 */
	public String dateToString(Date date){
		//		Date date = new Date();
		String dateStr = "";
		//format�ĸ�ʽ��������
		//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//			dateStr = sdf.format(date);
			//			System.out.println(dateStr);
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * Timestamp->String
	 * @param time
	 * @return
	 */
	public String timestampToString(Timestamp ts){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//����һ
			tsStr = sdf.format(ts);
			System.out.println(tsStr);
			//������
			//			tsStr = ts.toString();
			//			System.out.println(tsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tsStr;

	}

	/**
	 * String->Date
	 * @param dateString
	 * @return
	 */
	public Date stringToDate(String dateStr){
		//		String dateStr = "2010-05-04 12:34:23";
		Date date = new Date();
		//ע��format�ĸ�ʽҪ������String�ĸ�ʽ��ƥ��
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH/mm/ss");
		try {
			date = sdf.parse(dateStr);
			System.out.println(date.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return date;		
	}

	/**
	 * Timestamp->Date
	 * @param time
	 * @return
	 */
	public Date timestampToDate(Timestamp time){
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Date date = new Date();
		try {
			date = ts;
			System.out.println(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}


	/**
	 * �����˫���¼�
	 * @author wangqing
	 * @param parm
	 */
	public void test(TParm parm){
		// �򿪼��ﲡ������
		Object obj = this.openDialog("%ROOT%\\config\\odi\\ODIPatInfoUI.x", parm);
		if(obj != null && obj instanceof TParm){
			TParm result = (TParm) obj;
			System.out.println("======result:::"+result);
			setValueForParm("CASE_NO;MR_NO;PAT_NAME;SEX_CODE;CT_TRIAGE_NO;CT_LEVEL_CODE",result);
			// ��������
			this.setValue("AGE", this.getAge(result.getTimestamp("BIRTH_DATE"), SystemTool.getInstance().getDate()));

			// modified by wangqing 20170701
			// CT������ʱ���ֶ�����
			
			/*// �ж��Ƿ���ڼ�¼
			// �Ѵ��ڣ����ṩ��ѯ���ܣ������ڣ����ṩ���빦��
			String ctSql = "SELECT CASE_NO,CT_NOTICE_TIME,CT_RESP_TIME,CT_ARRIVE_TIME,PAT_ARRIVE_TIME,CT_START_TIME,CT_REPORT_TIME FROM AMI_CT_RECORD WHERE CASE_NO = '" + caseNo + "'";
			System.out.println("++++++ctSql:::"+ctSql);
			TParm ctParm = new TParm(TJDODBTool.getInstance().select(ctSql));
			if(ctParm.getCount() <= 0){// �����ڣ������µļ�¼,�������󱣴�
				// ���水ť����
				callFunction("UI|SAVE|setEnabled", true);

				ctNoticeTime = null;// ֪ͨCTʱ��(����ҽ��Ӧ��Ҫ��û��Ϊ��)

			}else{// �Ѵ��ڣ�ֱ�Ӹ�ҳ��
				// ���水ť����
				callFunction("UI|SAVE|setEnabled", true);
				ctNoticeTime = ctParm.getTimestamp("CT_NOTICE_TIME", 0); // ֪ͨCTʱ��(����ҽ��Ӧ��Ҫ��û��Ϊ��)
				ctRespTime = ctParm.getTimestamp("CT_RESP_TIME", 0)==null?ctRespTime:
					ctParm.getTimestamp("CT_RESP_TIME", 0);//CT�ظ�ʱ��(�������Ƶ�ʱ��)
				ctArriveTime = ctParm.getTimestamp("CT_ARRIVE_TIME", 0)==null?ctArriveTime:
					(ctParm.getTimestamp("CT_ARRIVE_TIME", 0));//CT����ʱ��(�������Ƶ�ʱ��)
				patArriveTime = ctParm.getTimestamp("PAT_ARRIVE_TIME", 0);// ɨ�軼�������ʱ��
				ctStartTime = ctParm.getTimestamp("CT_START_TIME", 0);// CT��ʼʱ��(����)
				ctReportTime = ctParm.getTimestamp("CT_REPORT_TIME", 0);// CT����ʱ��(����)
			}

			this.setValue("CT_NOTICE_TIME", ctNoticeTime);
			this.setValue("CT_RESP_TIME", ctRespTime);
			this.setValue("CT_ARRIVE_TIME", ctArriveTime);
			this.setValue("PAT_ARRIVE_TIME", patArriveTime);			
			this.setValue("CT_START_TIME", ctStartTime);
			this.setValue("CT_REPORT_TIME", ctReportTime);
			TParm saveCTParm = EMRAMITool.getInstance().updateAmiCTRespDataByCaseNo(caseNo,	
					StringTool.getString(TypeTool.getTimestamp(getValue("CT_RESP_TIME")), "yyyy/MM/dd HH:mm:ss"));
			if (saveCTParm.getErrCode() < 0) {
				System.out.println("CT�һظ�ʱ�䱣��ʧ�ܣ�");
				return;
			}
			this.grabFocus("MR_NO_CHK");*/
		}
	}

	/**
	 * ����LEDUI
	 */
	public void openLEDUI() {
		Component com = (Component) this.getComponent();
		TParm parm = new TParm();
		while (com != null && !(com instanceof Frame))
			com = com.getParent();
		ledUi = new LEDUI_NEW((Frame) com, this, parm);
		ledUi.openWindow();
	}


	public boolean onClosing(){
		//		this.messageBox("guanbi!!!");
		ledUi.close();
		return true;
	}

	public void onCheckMrNo(){
		String caseNo = (String)getValue("CASE_NO");
		String mrNo = (String)getValue("MR_NO");
		String mrNoChk = (String)getValue("MR_NO_CHK");
		Timestamp now = new Timestamp(new Date().getTime());
		if(mrNo.equals(mrNoChk)){
			this.setValue("PAT_ARRIVE_TIME", now);
			this.messageBox("ȷ�ϲ�������!!");
			TParm saveCTParm = EMRAMITool.getInstance().updateAmiCTPatArriveByCaseNo(caseNo,	
					StringTool.getString(now, "yyyy/MM/dd HH:mm:ss"));
			if (saveCTParm.getErrCode() < 0) {
				System.out.println("CT�Ҳ�������ʱ�䱣��ʧ�ܣ�");
				return;
			}
			callFunction("UI|SAVE|setEnabled", true);
		}else{
			this.messageBox("����������:"+mrNoChk+"��˴���!!");
		}

	}
}
