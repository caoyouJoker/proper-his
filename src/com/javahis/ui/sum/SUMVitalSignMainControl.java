package com.javahis.ui.sum;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import jdo.adm.ADMChgTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.erd.ERDForSUMTool;
import jdo.odi.ODICISVitalSignTool;
import jdo.sum.SUMVitalSignTool;
import jdo.sum.SUMXmlTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.base.TFrameBase;
import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.ui.emr.AnimationWindowUtils;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.DateUtil;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;
import com.sun.awt.AWTUtilities;

/**
 * <p>
 * Title: �������µ�
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: javahis
 * </p>
 * 
 * @author WangQing 20170306
 * 
 * @version 1.0
 */
public class SUMVitalSignMainControl extends TControl {

	TTable masterTable;// ���¼�¼table������
	TTable detailTable; // ������ϸtable��ϸ��
	int masterRow = -1;// ��tableѡ���к�
	int detailRow = -1;// ϸtableѡ���к�
	TParm patInfo = new TParm();// ������Ϣ
	String admType = "I"; // �ż�ס��
	String caseNo = ""; // �����
	TParm tprDtl = new TParm(); // ���±�����
	TParm inParm = new TParm(); // ���
	boolean isMroFlg = false;// ������ҳ����
	TParm p = new TParm();// ����

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// 20170531 liuyalin add
		Object object = this.getParameter();
		if (object instanceof TParm) {
			p = (TParm) object;
			// this.messageBox(p.getData("SUM", "CASE_NO")+"");
		}
		// 20170531 liuyalin add end
		masterTable = (TTable) this.getComponent("masterTable");// ��ʼ�����
		detailTable = (TTable) this.getComponent("detailTable");
		this.callFunction("UI|masterTable|addEventListener", "masterTable->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");// table����¼�
		// this.callFunction(e, parameters);

		inParm = this.getInputParm();
		if (inParm != null) {
			admType = inParm.getValue("SUM", "ADM_TYPE");
			caseNo = inParm.getValue("SUM", "CASE_NO");
			onQuery();
			if (inParm.getValue("SUM", "FLG").equals("MRO")) {
				isMroFlg = true;
				hideFrame();// ���ؽ���
			}
		}
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// ��¼ʱ��
		this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
	}

	/**
	 * �����MRO�������������
	 */
	public void hideFrame() {
		if (!(getComponent() instanceof TFrameBase))
			return;
		TFrame frame = (TFrame) getComponent();
		frame.setOpenShow(false);
		onPrint();
		frame.onClosed();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// 1. �����ѯ����
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		// 2.1 ��ѯ��������
		if ("E".equals(admType)) {
			patInfo = ERDForSUMTool.getInstance().selERDPatInfo(parm); // ������Ϣ
			patInfo.setData("ADM_DAYS", 0, this.getInHospDays(
					patInfo.getTimestamp("IN_DATE", 0),
					patInfo.getTimestamp("OUT_DATE", 0)));
		} else if ("I".equals(admType)) {
			patInfo = ADMTool.getInstance().getADM_INFO(parm);
			patInfo.setData("ADM_DAYS", 0,
					ADMTool.getInstance().getAdmDays(caseNo));
		}
		// 2.2 ��ѯ��������
		TParm result = SUMVitalSignTool.getInstance().selectExmDateUser(parm);// ���¼�¼
		// 3.����ҳ��
		// ��ʼ�����¼�¼table
		for (int row = 0; row < result.getCount(); row++) {
			result.setData("EXAMINE_DATE", row, StringTool.getString(StringTool
					.getDate(result.getValue("EXAMINE_DATE", row), "yyyyMMdd"),
					"yyyy/MM/dd"));
			if ((row + 1) % 7 == 0) {// ÿ��������һ������ɫ
				masterTable.setRowColor(row, new Color(255, 255, 132));
			}
		}
		masterTable.getTable().repaint();
		masterTable.setParmValue(result);
		if (masterTable.getRowCount() > 0) {
			masterTable.setSelectedRow(masterTable.getRowCount() - 1);// Ĭ��ѡ�����һ��
			onMasterTableClicked(masterTable.getSelectedRow()); // �ֶ�ִ�е���¼�
		}
	}

	/**
	 * ���סԺ����
	 * 
	 * @param inDate
	 * @param outDate
	 * @return
	 */
	private int getInHospDays(Timestamp inDate, Timestamp outDate) {
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		Timestamp endDate = null;
		if (outDate == null)
			endDate = now;
		else
			endDate = outDate;
		int days = StringTool.getDateDiffer(endDate, inDate);
		if (days == 0)
			return 1;
		return days;
	}

	/**
	 * ��ת��
	 * 
	 * @param tprDtl
	 *            TParm
	 * @return TParm
	 */
	public TParm rowToColumn(TParm tprDtl) {
		TParm result = new TParm();
		for (int i = 0; i < tprDtl.getCount(); i++) {
			result.addData("" + i, clearZero(tprDtl.getData("TEMPERATURE", i)));// ����
			result.addData("" + i, clearZero(tprDtl.getData("PLUSE", i)));// ����

			// modified by WangQing 20170228
			// ���ʹ�ú�����������ʾ������������ʾ��������
			if (tprDtl.getData("SPECIALRESPIRENOTE", i) != null
					&& tprDtl.getValue("SPECIALRESPIRENOTE", i).equals("R")) {// ���ʹ�ú�����
				result.addData("" + i, "R");// ʹ�ú�����
			} else {
				result.addData("" + i, clearZero(tprDtl.getData("RESPIRE", i)));// ����
			}
			/****************** shibl 20120330 modify ***************************/
			// result.addData("" + i,
			// clearZero(tprDtl.getData("SYSTOLICPRESSURE", i))); // ����ѹ
			//
			// 2017.05.17 chenhj
			if (tprDtl.getData("SYSTOLICPRESSURE", i) == null) {
				result.addData("" + i, "");
			} else {
				result.addData("" + i, tprDtl.getValue("SYSTOLICPRESSURE", i));
			}
			if (tprDtl.getData("DIASTOLICPRESSURE", i) == null) {
				result.addData("" + i, "");
			} else {
				result.addData("" + i, tprDtl.getValue("DIASTOLICPRESSURE", i));
			}

			// result.addData("" + i,
			// clearZero(tprDtl.getData("DIASTOLICPRESSURE", i))); // ����ѹ

			// modify by yangjj 20161219 ��Ŀ����4512//modify by machao 20170217
			// ��Ŀ����4512
			if (tprDtl.getInt("HEART_RATE", i) == -9999) {
				result.addData("" + i, "");
			} else {
				result.addData("" + i, tprDtl.getData("HEART_RATE", i)); // ����
			}

		}
		return result;
	}

	/**
	 * �½�
	 */
	public void onNew() {
		// �õ�������/���ݿ⵱ǰʱ��
		// String today =
		// StringTool.getString(TJDODBTool.getInstance().getDBTime(),
		// "yyyyMMdd");

		String today = (String) openDialog("%ROOT%\\config\\sum\\SUMTemperatureDateChoose.x");
		if (today.length() == 0) {
			messageBox("δѡ�����ʱ��");
			return;
		}
		// �ж��Ƿ����е�������
		for (int i = 0; i < masterTable.getRowCount(); i++) {
			if (today.equals(StringTool.getString(
					StringTool.getDate(
							masterTable.getItemString(i, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd"))) {
				this.messageBox("�Ѵ��ڽ�������\n�������½�");
				return;
			}
		}
		// ����һ������
		TParm MData = new TParm();
		MData.setData("EXAMINE_DATE",
				today.substring(0, 4) + "/" + today.substring(4, 6) + "/"
						+ today.substring(6));
		MData.setData("USER_ID", Operator.getName());
		// Ĭ��ѡ��������
		int newRow = masterTable.addRow(MData);
		masterTable.setSelectedRow(newRow);
		onMasterTableClicked(newRow); // �ֶ�ִ�е����¼�
		// �½���ʱ��д�뵱ǰʱ��
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// סԺ����
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// ��¼ʱ��
		this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
		this.setValue("STOOL", "0");// ���

		getOpeDays();
	}

	/**
	 * ���¼�¼table�����¼�
	 */
	public void onMasterTableClicked(int row) {
		masterRow = row; // ��tableѡ���к�
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXAMINE_DATE", StringTool.getString(StringTool.getDate(
				masterTable.getItemString(row, "EXAMINE_DATE"), "yyyy/MM/dd"),
				"yyyyMMdd"));
		// =========================��������
		TParm master = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		this.clearComponent();// ������
		// ��ؼ���ֵ
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// סԺ����
		this.setValue("OPE_DAYS", master.getValue("OPE_DAYS", 0));// �����ڼ���
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);
		this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
		// =========================ϸ������

		String sql = "SELECT   B.*, CASE WHEN A.HEART_RATE IS NULL THEN -9999 ELSE A.HEART_RATE END AS HEART_RATE, "
				+ "A.ADM_TYPE,A.CASE_NO,A.EXAMINE_DATE,A.EXAMINESESSION,A.PHYSIATRICS,A.RECTIME,A.SPCCONDCODE,A.TMPTRKINDCODE,A.TEMPERATURE, "
				+
				// modified by WangQing 20170228
				// ���� A.SPECIALRESPIRENOTE�ֶΣ�����ʹ�ú�����
				"A.PLUSE,A.RESPIRE, A.SPECIALRESPIRENOTE, TO_CHAR(A.SYSTOLICPRESSURE) AS SYSTOLICPRESSURE,TO_CHAR(A.DIASTOLICPRESSURE) AS DIASTOLICPRESSURE ,"
				+ "A.NOTPRREASONCODE,A.PTMOVECATECODE,A.PTMOVECATEDESC,A.USER_ID,A.OPT_USER, "
				+ "A.OPT_DATE,A.OPT_TERM "
				+ "FROM   SUM_VTSNTPRDTL A, SUM_VITALSIGN B "
				+ "WHERE  A.ADM_TYPE = '"
				+ admType
				+ "' "
				+ "AND A.CASE_NO = '"
				+ caseNo
				+ "' "
				+ "AND A.EXAMINE_DATE = '"
				+ StringTool.getString(StringTool.getDate(
						masterTable.getItemString(row, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd")
				+ "' "
				+ "AND A.ADM_TYPE = B.ADM_TYPE "
				+ "AND A.CASE_NO = B.CASE_NO "
				+ "AND A.EXAMINE_DATE = B.EXAMINE_DATE "
				+ "AND B.DISPOSAL_FLG IS NULL " + "ORDER BY EXAMINESESSION ";
		// System.out.println("ma:::"+sql);
		tprDtl = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println(tprDtl);
		// ���û�и�������ݲ���հ���
		if (tprDtl.getCount("CASE_NO") <= 0) {
			// this.messageBox("======û�е�������======");
			detailTable.removeRowAll();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			// return;
		} else {
			detailTable.setParmValue(rowToColumn(tprDtl));
		}

		// =========================ϸ����������
		String stool = master.getValue("STOOL", 0);
		if (StringUtils.isEmpty(stool)) {
			stool = "0";
		}
		this.setValue("STOOL", stool);// ���

		// modified by WangQing 20170238
		this.setValue("PB_TYPE", master.getValue("SPECIALSTOOLNOTE", 0));// �����ű�
		// this.setValue("AUTO_STOOL", master.getValue("AUTO_STOOL", 0));// �����ű�

		this.setValue("ENEMA", master.getValue("ENEMA", 0));// �೦
		this.setValue("DRAINAGE", master.getValue("DRAINAGE", 0));// ����
		this.setValue("INTAKEFLUIDQTY", master.getValue("INTAKEFLUIDQTY", 0));// ����
		this.setValue("OUTPUTURINEQTY", master.getValue("OUTPUTURINEQTY", 0));// ����
		this.setValue("WEIGHT", master.getValue("WEIGHT", 0));// ����
		this.setValue("HEIGHT", master.getValue("HEIGHT", 0));// ���
		this.setValue("USER_DEFINE_1", master.getValue("USER_DEFINE_1", 0));// �Զ���һ
		this.setValue("USER_DEFINE_1_VALUE",
				master.getValue("USER_DEFINE_1_VALUE", 0));
		this.setValue("USER_DEFINE_2", master.getValue("USER_DEFINE_2", 0));// �Զ����
		this.setValue("USER_DEFINE_2_VALUE",
				master.getValue("USER_DEFINE_2_VALUE", 0));
		this.setValue("USER_DEFINE_3", master.getValue("USER_DEFINE_3", 0));// �Զ�����
		this.setValue("USER_DEFINE_3_VALUE",
				master.getValue("USER_DEFINE_3_VALUE", 0));
	}

	/**
	 * ������ϸtable�����¼�(ͨ������ע�ᷨ)
	 */
	public void onDTableFocusChange() {
		// PS:���ڴ��ھ���ת�������⣬����ѡ�е���Ϊ����PARM����
		// ��ʼ��ϸtable��ѡ�е��У�����table���кţ�
		detailRow = detailTable.getSelectedColumn();
		((TComboBox) this.getComponent("EXAMINESESSION"))
				.setSelectedIndex(detailRow + 1);
		this.setValue("RECTIME", tprDtl.getValue("RECTIME", detailRow));// ��¼ʱ��
		if (tprDtl.getValue("RECTIME", detailRow).equals("")) {
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			this.setValue("RECTIME", now);// ��¼ʱ��
		}
		this.setValue("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", detailRow));// ���±仯�������
		this.setValue("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", detailRow));// ������
		this.setValue("TMPTRKINDCODE",
				tprDtl.getValue("TMPTRKINDCODE", detailRow));// ��������
		if (tprDtl.getValue("TMPTRKINDCODE", detailRow).equals("")) {
			this.setValue("TMPTRKINDCODE", "4");// �������ࣺҸ��
		}
		this.setValue("NOTPRREASONCODE",
				tprDtl.getValue("NOTPRREASONCODE", detailRow));// δ��ԭ��
		this.setValue("PTMOVECATECODE",
				tprDtl.getValue("PTMOVECATECODE", detailRow));// ���˶�̬
		this.setValue("PTMOVECATEDESC",
				tprDtl.getValue("PTMOVECATEDESC", detailRow));// ���˶�̬��ע
	}

	/**
	 * === add by wukai on 20170210 �Զ����� �����ڼ��� ���˶�̬����¼�
	 */
	public void onPtmovecateSelected() {
		this.setValue("PTMOVECATEDESC", "");
		String code = this.getValueString("PTMOVECATECODE");
		// add by wangb 2017/05/25 ��Ժ�ڡ�ת���ڣ�ʱ���Զ�����
		if ("01".equals(code)) {
			this.setPatientTrends(code);
		} else if ("02".equals(code)) { // ����
			getOpeDays();
		} else if ("03".equals(code)) {
			this.setPatientTrends(code);
		}
	}

	/**
	 * ��ȡ��������
	 */
	private void getOpeDays() {
		// liuyalin modify 20170531
		// 1.��ȡmasterTable�еļ�����ڣ����ݼ�����ڼ�����������
		int row = masterTable.getSelectedRow();
		// this.messageBox_(row);
		if (row < 0) {
			return;
		}
		String vitalDate = masterTable.getItemString(row, "EXAMINE_DATE")
				.replaceAll("/", "");//this.messageBox(vitalDate+"");
		// this.messageBox_(vitalDate);
		if (StringUtils.isEmpty(vitalDate)) {
			return;
		}
		// 2.ѡ���˶�̬����02������ ��ȡʱ��
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		parm.setData("STATE", "5");
		String code = this.getValueString("PTMOVECATECODE");
		String sql1 = "SELECT DISTINCT EXAMINE_DATE "
				+ "FROM SUM_VTSNTPRDTL A " + "WHERE PTMOVECATECODE = '02' "
				+ "" + "AND CASE_NO ='" + p.getData("SUM", "CASE_NO") + "' ORDER BY EXAMINE_DATE DESC";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		//System.out.println("22222222"+sql1);
		String vitalDate1="";
		String vitalDate2 ="";
		vitalDate2 = parm1.getValue("EXAMINE_DATE",0);
		if ("02".equals(code)) { // ����
			parm.setData("DYNA_DATE", vitalDate2);
//			this.messageBox_(vitalDate2);
		} else {
			for (int i = 0; i < parm1.getCount("EXAMINE_DATE"); i++) {
				vitalDate1 = parm1.getValue("EXAMINE_DATE",i);
				parm.setData("DYNA_DATE",i, vitalDate1);
			}
//			this.messageBox_(parm.getValue("DYNA_DATE"));
		}//this.messageBox(parm+"");
		// 3.����������
//		Timestamp t1 = Timestamp.valueOf(vitalDate);
//		Timestamp t2 = null;
		String t1 = vitalDate ;
		String t2 = "" ;
		//this.messageBox((Integer.parseInt("20170908") - Integer.parseInt("20170907"))+"");
		StringBuilder dayStr = new StringBuilder();
		for (int i = 0; i < parm.getCount("DYNA_DATE"); i++) {
			t2 = parm.getValue("DYNA_DATE", i);
			//int day = Integer.parseInt(t1) - Integer.parseInt(t2);
			int day = daysBetween(t2,t1);
			if (day > 0 && day <= 14) {
				dayStr.append(day+"/");
			}
		}
		String str = dayStr.toString();
		if (!StringUtils.isEmpty(str.toString())) {
			this.setValue("OPE_DAYS", str.substring(0, str.lastIndexOf("/")));
		}
		// // 3.����������
		// Timestamp t1 = Timestamp.valueOf(vitalDate);
		// Timestamp t2 = null;
		// StringBuilder dayStr = new StringBuilder();
		// t2 = Timestamp.valueOf(parm.getValue("DYNA_DATE").substring(0, 10)
		// + " 00:00:00");
		// int day = StringTool.getDateDiffer(t1, t2);
		// // �ж�����ʱ���Ƿ���14����
		// if (day >= 0 && day <= 14) {
		// // �ж��Ƿ�Ϊ��̨����
		// if (StringUtils.isNotEmpty((String) this.getValue("OPE_DAYS"))) {
		// if ((day + "").equals(this.getValue("OPE_DAYS"))) {
		// this.setValue("OPE_DAYS", this.getValue("OPE_DAYS"));
		// } else {
		// dayStr.append(day + "/" + this.getValue("OPE_DAYS"));
		// String str = dayStr.toString();
		// if (!StringUtils.isEmpty(str.toString())) {
		// this.setValue("OPE_DAYS", str);
		// }
		// }
		// } else {
		// this.setValue("OPE_DAYS", day + "");
		// }
		// }
		// // 2.��ȡ����ʱ��
		// TParm parm = new TParm();
		// parm.setData("CASE_NO", caseNo);
		// parm.setData("ADM_TYPE", admType);
		// parm.setData("STATE", "5");
		// parm = OPEOpBookTool.getInstance().selectOpBookForSum(parm);
		// // this.messageBox(parm+"");
		// if (parm.getCount("OP_DATE") <= 0 || parm.getErrCode() < 0) {
		// return;
		// }
		// // 3.����������
		// Timestamp t1 = Timestamp.valueOf(vitalDate);
		// Timestamp t2 = null;
		// StringBuilder dayStr = new StringBuilder();
		// for (int i = 0; i < parm.getCount("OP_DATE"); i++) {
		// t2 = Timestamp.valueOf(parm.getValue("OP_DATE", i).substring(0, 10)
		// + " 00:00:00");
		// int day = StringTool.getDateDiffer(t1, t2);
		// if (day > 0 && day <= 14) {
		// dayStr.append(day + "/");
		// }
		// }
		// String str = dayStr.toString();
		// if (!StringUtils.isEmpty(str.toString())) {
		// this.setValue("OPE_DAYS", str.substring(0, str.lastIndexOf("/")));
		// }
	}

	public static final int daysBetween(String earlyStr, String lateStr) { 
	    Date early = new Date();
	    Date late =  new Date();
	    DateFormat df = DateFormat.getDateInstance(); 
	    
       try {    	   
    	   early = df.parse(new StringBuilder(earlyStr).insert(4, "-").insert(7, "-").toString());   
    	   late = df.parse(new StringBuilder(lateStr).insert(4, "-").insert(7, "-").toString());   
		} catch (ParseException e) {   
		        e.printStackTrace();   
		} 
	    
        java.util.Calendar calst = java.util.Calendar.getInstance();   
        java.util.Calendar caled = java.util.Calendar.getInstance();   
        calst.setTime(early);   
        caled.setTime(late);   
        //����ʱ��Ϊ0ʱ   
        calst.set(java.util.Calendar.HOUR_OF_DAY, 0);   
        calst.set(java.util.Calendar.MINUTE, 0);   
        calst.set(java.util.Calendar.SECOND, 0);   
        caled.set(java.util.Calendar.HOUR_OF_DAY, 0);   
        caled.set(java.util.Calendar.MINUTE, 0);   
        caled.set(java.util.Calendar.SECOND, 0);   
        //�õ�����������������   
        int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst   
                .getTime().getTime() / 1000)) / 3600 / 24;   
         
        return days;   
   }
	
	// liuyalin modify 20170531 end

	/**
	 * ����
	 */
	public boolean onSave() {
		masterTable.acceptText();
		detailTable.acceptText();
		
		// if (masterRow < 0 || masterTable.getSelectedRow() < 0) {
		// this.messageBox("��ѡ��һ����¼��");
		// return false;
		// }
		// this.messageBox("masterRow:::"+masterRow);
		//У������������
		String currentWeight = getValueString("WEIGHT");
		boolean flgg = true;
		if(!isNumeric(currentWeight)){
			flgg = false;
//			this.messageBox("��������������");
//			((TTextField) getComponent("WEIGHT")).grabFocus();
//			return false;
		}
		// ��ȡҪ���������
		TParm saveParm = getValueFromUI();
		if (saveParm.getErrCode() < 0) {
			this.messageBox(saveParm.getErrText());
			return false;
		}
		
		// ��ߡ����غ����ر仯-start
		String sql = " SELECT HEIGHT,WEIGHT FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "' ";
		TParm HWeightParmadm = new TParm(TJDODBTool.getInstance().select(sql));
		// ��һ�ε�����

		// �����õ�������ػ�дADM_INP -start
		TParm HWeightParm = new TParm();
		try {
			if (!this.getValueString("HEIGHT").equals("")
					&& this.getValueDouble("HEIGHT") > 0) {
				HWeightParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));
			} else {
				HWeightParm.setData("HEIGHT",
						HWeightParmadm.getDouble("HEIGHT", 0));
			}
		} catch (NumberFormatException e) {
			HWeightParm
					.setData("HEIGHT", HWeightParmadm.getDouble("WEIGHT", 0));
		}

		try {
			if (!this.getValueString("WEIGHT").equals("")
					&& this.getValueDouble("WEIGHT") > 0) {
				HWeightParm.setData("WEIGHT", this.getValueString("WEIGHT"));
			} else {
				HWeightParm.setData("WEIGHT",
						HWeightParmadm.getDouble("WEIGHT", 0));
			}
		} catch (NumberFormatException e) {
			HWeightParm
					.setData("WEIGHT", HWeightParmadm.getDouble("WEIGHT", 0));
		}
		// �����õ�������ػ�дADM_INP -end
		HWeightParm.setData("CASE_NO", caseNo);
		// ��ߡ����غ����ر仯-end

		// ����-start
		// add by wangqing 20180302 -start ����ż������µ����ܱ�������� 
		/*String sqlAge = "SELECT A.BIRTH_DATE "
				+ "FROM SYS_PATINFO A, ADM_INP B "
				+ " WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = '" + caseNo + "'";*/
		String sqlAge = "";
		if(admType != null || admType.trim().length()>0){
			if(admType.equals("O") || admType.equals("E")){
				sqlAge = "SELECT A.BIRTH_DATE "
						+ "FROM SYS_PATINFO A, REG_PATADM B "
						+ " WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = '" + caseNo + "'";
			}else if(admType.equals("I")){
				sqlAge = "SELECT A.BIRTH_DATE "
						+ "FROM SYS_PATINFO A, ADM_INP B "
						+ " WHERE A.MR_NO = B.MR_NO AND B.CASE_NO = '" + caseNo + "'";
			}else{
				this.messageBox("�ż�ס����Ϊ"+admType+"���в�֧�ִ����͵����µ��������");
				return false;
			}
		}else{
			this.messageBox("�ż�ס���Ͳ���Ϊ��");
			return false;
		}
		System.out.println("sqlAge="+sqlAge);
		// add by wangqing 20180302 -end

		Timestamp birth = new TParm(TJDODBTool.getInstance().select(sqlAge))
				.getTimestamp("BIRTH_DATE", 0);
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ��������
		String age = "0";
		if (birth != null)
			age = OdiUtil.showAge(birth, sysDate);
		else
			age = "";
		String currentAge = age.split("��")[0];// String ��split()
		// ��������ָ���ַ��ָ��ַ��������طָ�������
		// ����-end
		
		// �ж������Ƿ�������
		//boolean weightNumFlg = false;

		saveParm.setData("HW", HWeightParm.getData());
		// ���ݱ������ڡ�����ź��ż�ס�������ж��Ƿ����и����ݲ���/����
		String saveDate = saveParm.getParm("MASET").getValue("EXAMINE_DATE");
		// �õ����table������
		TParm checkDate = new TParm();
		checkDate.setData("CASE_NO", caseNo);
		checkDate.setData("ADM_TYPE", admType);
		checkDate.setData("EXAMINE_DATE", saveDate);
		TParm existParm = SUMVitalSignTool.getInstance()
				.checkIsExist(checkDate);

		// ==================================================�������������ݣ������ǲ�����߸�������=============================================
		// 1.û�и������ݣ�����
		// 2.����

		if (existParm.getCount() == 0) {// ����
			String sqlWeight = "SELECT EXAMINE_DATE,WEIGHT,DISPOSAL_FLG "
					+ "FROM SUM_VITALSIGN " + "WHERE CASE_NO = '" + caseNo + "' "
					+ "AND (DISPOSAL_FLG <> 'Y' or DISPOSAL_FLG is null) "
					+ "ORDER BY EXAMINE_DATE DESC";//��������
			
			TParm weightParm = new TParm(TJDODBTool.getInstance().select(sqlWeight));
			//System.out.println("aaaaaaaa"+sqlWeight);
			String lastWeight = "";
			//��ȡ���ռ�����ڵ���������   ��ֵ���������ֵ���
			for (int i = 0; i < weightParm.getCount(); i++) {
				if (StringUtils.isEmpty((weightParm.getValue("WEIGHT", i))) 
						|| !isNumeric((weightParm.getValue("WEIGHT", i)))) {
					continue;
				}
				lastWeight = weightParm.getValue("WEIGHT", i);
				break;
			}
			
			//this.messageBox(lastWeight);
			boolean flg = true;
			if (StringUtils.isEmpty(lastWeight)) {
				flg = false;
				lastWeight = "0";
			}
			Double weightSub = 0.0;
			boolean flgNum = false;
			if(flgg && !(currentWeight == null || currentWeight.length() <= 0)){
				flgNum = true;
				weightSub = Double.parseDouble(lastWeight)
						- this.getValueDouble("WEIGHT");
			}
			
//			if (isNumeric(lastWeight)
//					&& isNumeric(this.getValueString("WEIGHT") + "")) {
//				// ������
//				weightNumFlg = true;
//				weightSub = Double.parseDouble(lastWeight)
//						- this.getValueDouble("WEIGHT");
//			} else {
//				// ��������
//				weightNumFlg = false;
//			}
			
			
			// Double �� parseDouble() ���� �����ַ���ת��ΪDouble
			if (flgg&& (Double.parseDouble(currentAge) <= 14)
					&& flg//��һ����Ϊ��  �������淽��
					&& flgNum//��д����Ϊ��  �������淽��
					&& (weightSub >= 1.0 || weightSub <= -1.0)
					&& !(0 == this.messageBox("",
							"�û���δ��14��,���Ҹô��������ϴ���ȳ���1kg���Ƿ񱣴棿",
							this.YES_NO_OPTION))) {
				return false;
			} else {
				saveParm.setData("I", true);
				// ����actionִ������
				TParm result = TIOM_AppServer.executeAction(
						"action.sum.SUMVitalSignAction", "onSave", saveParm);
				// ���ñ���
				if (result.getErrCode() < 0) {
					this.messageBox_(result);
					this.messageBox("����ʧ�ܣ�����");
					return false;
				}
				this.messageBox("����ɹ�������");
				// �������ӿ� wanglong add 20150527
				TParm xmlParm = ADMXMLTool.getInstance()
						.creatPatXMLFile(caseNo);
				if (xmlParm.getErrCode() < 0) {
					this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
				}
				onClear();
				return true;
			}

		} else {// ����
			String sqlWei = "SELECT EXAMINE_DATE,WEIGHT,DISPOSAL_FLG "
					+ "FROM SUM_VITALSIGN "
					+ "WHERE CASE_NO = '"
					+ caseNo
					+ "' "
					+ "AND EXAMINE_DATE = '"
					+ StringTool.getString(StringTool.getDate(masterTable
							.getItemString(masterRow, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd") + "' ";
			TParm p = new TParm(TJDODBTool.getInstance().select(sqlWei));
			Double weight = 0.0;

			//�ϴ�¼������ʱΪ��
			boolean flgEmpty = true;
			boolean flgNum = false;
			if(StringUtil.isNullString(p.getValue("WEIGHT",0)) || !isNumeric(p.getValue("WEIGHT",0))){
				flgEmpty = false;
			}else{			
				//this.messageBox("1");
				if(flgg && !(currentWeight == null || currentWeight.length() <= 0)){
					flgNum = true;
					weight = Double.parseDouble(p.getValue("WEIGHT",0)) - this.getValueDouble("WEIGHT");
				}
			}
			//֤���Ѿ�������
			boolean disFlg = true;
			Double weightDis = 0.0;
			if(!StringUtils.isEmpty(p.getData("DISPOSAL_FLG", 0) + "")){
				disFlg = false;

				String sqlWeight = "SELECT EXAMINE_DATE,WEIGHT,DISPOSAL_FLG "
						+ "FROM SUM_VITALSIGN " + "WHERE CASE_NO = '" + caseNo + "' "
						+ "AND (DISPOSAL_FLG <> 'Y' or DISPOSAL_FLG is null) "
						//+ "AND TRIM (TRANSLATE (NVL (WEIGHT, 'x'), '0123456789', ' ')) IS NULL "
						+ "AND REGEXP_LIKE (WEIGHT, '^-?[[:digit:],.]*$')"
						//+ "AND TRIM (WEIGHT) not like '% %'"
						+ "ORDER BY EXAMINE_DATE DESC";//��������
				System.out.println("wwwww"+sqlWeight);
				TParm pDis = new TParm(TJDODBTool.getInstance().select(sqlWeight));
				
//				this.messageBox(flgg+"");
//				this.messageBox(!StringUtil.isNullString(pDis.getValue("WEIGHT",0))+"");
//				this.messageBox(isNumeric(pDis.getValue("WEIGHT",0))+"");
//				this.messageBox(weightDis+"");
				
				if(flgg && !(currentWeight == null || currentWeight.length() <= 0) && !StringUtil.isNullString(pDis.getValue("WEIGHT",0)) && (isNumeric(pDis.getValue("WEIGHT",0)))){
					
					weightDis = Double.parseDouble(pDis.getValue("WEIGHT",0)) - this.getValueDouble("WEIGHT");
				}
			}
			
				saveParm.setData("I", false);
				// ������0˵���Ѿ��д��ڵĸ���������--���ϡ�û����--���¶���
				if (existParm.getData("DISPOSAL_FLG", 0) != null
						&& existParm.getData("DISPOSAL_FLG", 0).equals("Y")
						&& !(0 == this.messageBox("", "�������Ѿ����Ϲ���\n�Ƿ���ȷ�����棿",
								this.YES_NO_OPTION))) {
					return false;
				} else {
					//û�����ϵ�
					if(disFlg){
						if (flgg&& (Double.parseDouble(currentAge) <= 14)
								&& flgEmpty//�ϴ�Ϊ�յĻ�  �������淽��
								&& flgNum//����д�Ļ��ǿ�  �������淽��
								&& (weight >= 1.0 || weight <= -1.0)
								&& !(0 == this.messageBox("",
										"�û���δ��14��,���Ҹô��������ϴ���ȳ���1kg���Ƿ񱣴棿",
										this.YES_NO_OPTION))) {
							return false;
						}
						// ֱ�Ӹ���--DISPOSAL_FLG==null����N
						TParm result = TIOM_AppServer
								.executeAction("action.sum.SUMVitalSignAction",
										"onSave", saveParm);
						// ���ñ���
						if (result.getErrCode() < 0) {
							this.messageBox_(result);
							this.messageBox("����ʧ�ܣ�");
							return false;
						}
						this.messageBox("���³ɹ���");
						// �������ӿ� wanglong add 20150527
						TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(
								caseNo);
						if (xmlParm.getErrCode() < 0) {
							this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
						}
						String nisActivety = TConfig.getSystemValue("NisActivety");
						if (nisActivety.equals("Y")) {
							TParm inParm = new TParm();
							inParm.setData("ADM_TYPE", admType);
							inParm.setData("CASE_NO", caseNo);
							inParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
							inParm.setData("EXAMINE_DATE", saveDate);
							TParm hl7Xml = SUMXmlTool.getInstance().onAssembleData(
									inParm);
							if (hl7Xml.getErrCode() < 0) {
								this.messageBox("NIS�ӿڷ���ʧ��: " + hl7Xml.getErrText());
							}
						}
						onClear();
						return true;
					}else{//���ϵ�
						
//						this.messageBox((Double.parseDouble(currentAge) <= 14)+"");
//						this.messageBox(flgNum+"");
//						this.messageBox(weightDis+"");
						if ((Double.parseDouble(currentAge) <= 14)
								//&& flgEmpty//�ϴ�Ϊ�յĻ�  �������淽��
								&& flgNum//����д�Ļ��ǿ�  �������淽��
								&& (weightDis >= 1.0 || weightDis <= -1.0)
								&& !(0 == this.messageBox("",
										"�û���δ��14��,���Ҹô��������ϴ���ȳ���1kg���Ƿ񱣴棿",
										this.YES_NO_OPTION))) {
							return false;
						}
						// ֱ�Ӹ���--DISPOSAL_FLG==null����N
						TParm result = TIOM_AppServer
								.executeAction("action.sum.SUMVitalSignAction",
										"onSave", saveParm);
						// ���ñ���
						if (result.getErrCode() < 0) {
							this.messageBox_(result);
							this.messageBox("����ʧ�ܣ�");
							return false;
						}
						this.messageBox("���³ɹ���");
						// �������ӿ� wanglong add 20150527
						TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(
								caseNo);
						if (xmlParm.getErrCode() < 0) {
							this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
						}
						String nisActivety = TConfig.getSystemValue("NisActivety");
						if (nisActivety.equals("Y")) {
							TParm inParm = new TParm();
							inParm.setData("ADM_TYPE", admType);
							inParm.setData("CASE_NO", caseNo);
							inParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
							inParm.setData("EXAMINE_DATE", saveDate);
							TParm hl7Xml = SUMXmlTool.getInstance().onAssembleData(
									inParm);
							if (hl7Xml.getErrCode() < 0) {
								this.messageBox("NIS�ӿڷ���ʧ��: " + hl7Xml.getErrText());
							}
						}
						onClear();
						return true;
					}
				}
		}

	}

	/**
	 * �ж��Ƿ�Ϊ����
	 */
	public static boolean isNumeric(String str) {
		String s = str;
		String regex = "^[+-]?\\d+(\\.\\d+)?$";
		if (s == null || s.length() <= 0 || s.matches(regex) == true) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ���棺�ӿؼ�������ֵ�����������Ա�����TDSʹ��
	 */
	public TParm getValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// �������
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// סԺ����
		masterParm.setData("OPE_DAYS", this.getValue("OPE_DAYS"));// ��������
		masterParm.setData("ECTTIMES", "");// ĿǰECT��������ʱû�ã�
		masterParm.setData("MCFLG", "");// �¾�����ʱû�ã�
		masterParm.setData("HOURSOFSLEEP", "");// ˯��ʱ��-Сʱ����ʱû�ã�
		masterParm.setData("MINUTESOFSLEEP", "");// ˯��ʱ��-�֣���ʱû�ã�
		masterParm.setData("INTAKEDIETQTY", "");// ����-��ʳ������ʱû�ã�
		masterParm.setData("OUTPUTDRAINQTY", "");// �ų�-����������ʱû�ã�
		masterParm.setData("OUTPUTOTHERQTY", "");// �ų�-��������ʱû�ã�
		masterParm.setData("BATH", "");// ϴ�裨��ʱû�ã�
		masterParm.setData("GUESTKIND", "");// ��ͣ���ʱû�ã�
		masterParm.setData("STAYOUTSIDE", "");// ���ޣ���ʱû�ã�
		masterParm.setData("LEAVE", "");// �������ʱû�ã�
		masterParm.setData("LEAVEREASONCODE", "");// ���ԭ����루��ʱû�ã�
		masterParm.setData("NOTE", "");// ��ע����ʱû�ã�
		masterParm.setData("STATUS_CODE", "");// ������״̬����ʱû�ã�
		masterParm.setData("DISPOSAL_FLG", "");// ����ע��
		masterParm.setData("DISPOSAL_REASON", "");// ��������
		// modify by yangjj 20151111
		masterParm.setData("STOOL", this.getValueString("STOOL"));// ���
		// modified by WangQing 20170228
		masterParm.setData("SPECIALSTOOLNOTE", this.getValueString("PB_TYPE"));// �����ű����
		masterParm.setData("AUTO_STOOL", this.getValue("AUTO_STOOL"));// �����ű�
		masterParm.setData("ENEMA", this.getValue("ENEMA"));// �೦

		masterParm.setData("DRAINAGE", this.getValue("DRAINAGE"));// ����
		masterParm.setData("INTAKEFLUIDQTY",
				this.getValueDouble("INTAKEFLUIDQTY"));// ����-ע��
		masterParm.setData("OUTPUTURINEQTY",
				this.getValueDouble("OUTPUTURINEQTY"));// ����-С����
		masterParm.setData("WEIGHT", this.getValue("WEIGHT"));// ����
		masterParm.setData("HEIGHT", this.getValueString("HEIGHT"));// ���
		masterParm.setData("USER_DEFINE_1", this.getValue("USER_DEFINE_1"));// �Զ���һ
		masterParm.setData("USER_DEFINE_1_VALUE",
				this.getValue("USER_DEFINE_1_VALUE"));
		masterParm.setData("USER_DEFINE_2", this.getValue("USER_DEFINE_2"));// �Զ����
		masterParm.setData("USER_DEFINE_2_VALUE",
				this.getValue("USER_DEFINE_2_VALUE"));
		masterParm.setData("USER_DEFINE_3", this.getValue("USER_DEFINE_3"));// �Զ�����
		masterParm.setData("USER_DEFINE_3_VALUE",
				this.getValue("USER_DEFINE_3_VALUE"));
		masterParm.setData("USER_ID", Operator.getID());// ��¼��Ա
		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		String columnIndex = this.getValueString("EXAMINESESSION");
		for (int i = 0; i < 6; i++) {// ʱ����6��
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// ��¼ʱ��
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// ���±仯�������
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// ������
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// ��������
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// δ��ԭ��
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// ���˶�̬
				// liuyalin 20170531 modify
				// if (!StringUtil.isNullString(this
				// .getValueString("PTMOVECATECODE"))
				// && StringUtil.isNullString(this
				// .getValueString("PTMOVECATEDESC"))) {
				// TParm errParm = new TParm();
				// errParm.setErr(-1, "����д���˶�̬��ע");
				// return errParm;
				// }
				// liuyalin 20170531 modify end
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// ���˶�̬��ע
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// ��¼ʱ��
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// ���±仯�������
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// ������
				// wanglong modify 20140428
				oneParm.setData(
						"TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("") ? this
								.getValue("TMPTRKINDCODE") : tprDtl.getValue(
								"TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// δ��ԭ��
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// ���˶�̬
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// ���˶�̬��ע
			}
			// �õ�table�ϵ�������
			oneParm.setData("TEMPERATURE",
					TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// ����
			oneParm.setData("PLUSE",
					TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// ����
			// add by chenhj start
			oneParm.setData("SYSTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(3, i)));// ����ѹ
			oneParm.setData("DIASTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(4, i)));// ����ѹ
			// add by chenhj end
			// modified by WangQing 20170228 -start
			// ������������־
			if (TCM_Transform.getString(detailTable.getValueAt(2, i)).equals(
					"R")
					|| TCM_Transform.getString(detailTable.getValueAt(2, i))
							.equals("r")) {
				oneParm.setData("SPECIALRESPIRENOTE", "R");// ʹ�ú�����
				oneParm.setData("RESPIRE", 0.0);// ����
			} else {
				oneParm.setData("RESPIRE",
						TCM_Transform.getDouble(detailTable.getValueAt(2, i)));// ����
				oneParm.setData("SPECIALRESPIRENOTE", "");// ʹ�ú�����
			}
			// modified by WangQing 20170228 -end

			/****************** shibl 20120330 modify ***************************/
			double systolicPressure = TCM_Transform.getDouble(detailTable
					.getValueAt(3, i));
			double diastolicPressure = TCM_Transform.getDouble(detailTable
					.getValueAt(4, i));
			if (diastolicPressure > systolicPressure) {// wanglong add 20141022
				return TParm.newErrParm(-1, "����ѹ���ܴ�������ѹ");
			}
			oneParm.setData("SYSTOLICPRESSURE", systolicPressure);// ����ѹ
			oneParm.setData("DIASTOLICPRESSURE", diastolicPressure);// ����ѹ

			// this.messageBox((String)(detailTable.getValueAt(5, i)+""));
			// add by machao 20170227 ҳ�����ʲ���ʱ������Ϊ��ֵ
			if (!StringUtils
					.isEmpty((String) (detailTable.getValueAt(5, i) + ""))) {
				// this.messageBox("�ǿ�");
				oneParm.setData("HEART_RATE",
						TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// ����
			} else {
				// this.messageBox("���ʿ�");
				oneParm.setData("HEART_RATE", new TNull(Double.class));// ����
			}
			// add by chenhj 20170512 ҳ��Ѫѹ����ʱ������Ϊ��ֵ
			if (!StringUtils
					.isEmpty((String) (detailTable.getValueAt(3, i) + ""))) {// ����ѹ
				oneParm.setData("SYSTOLICPRESSURE",
						TCM_Transform.getDouble(detailTable.getValueAt(3, i)));
			} else {
				// this.messageBox("��");
				oneParm.setData("SYSTOLICPRESSURE", new TNull(Double.class));
			}
			if (!StringUtils
					.isEmpty((String) (detailTable.getValueAt(4, i) + ""))) {// ����ѹ

				oneParm.setData("DIASTOLICPRESSURE",
						TCM_Transform.getDouble(detailTable.getValueAt(4, i)));
			} else {
				// this.messageBox("��");
				oneParm.setData("DIASTOLICPRESSURE", new TNull(Double.class));
			}

			// oneParm.setData("HEART_RATE",
			// TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// ����
			// System.out.println("ma123:"+detailTable.getValueAt(5, i));
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * ���
	 */
	public void onClear() {
		// ��������ȫ�ֱ���
		masterRow = -1;
		detailRow = -1;
		this.clearComponent();
		detailTable.removeRowAll();
		onQuery();// ִ�в�ѯ
	}

	/**
	 * ������
	 */
	public void clearComponent() {
		// �����ϰ벿��
		this.clearValue("EXAMINESESSION;RECTIME;" // INHOSPITALDAYSסԺ���������;OPE_DAYS�������������
				+ "SPCCONDCODE;PHYSIATRICS;"// TMPTRKINDCODE�������಻���
				+ "NOTPRREASONCODE;PTMOVECATECODE;PTMOVECATEDESC");
		// �����°벿��
		this.clearValue("STOOL;INTAKEFLUIDQTY;OUTPUTURINEQTY;WEIGHT;HEIGHT;"
				+ "USER_DEFINE_1;USER_DEFINE_2;USER_DEFINE_3;USER_DEFINE_1_VALUE;USER_DEFINE_2_VALUE;USER_DEFINE_3_VALUE");
	}

	/**
	 * ����
	 */
	public void onDefeasance() {
		int selRow = masterTable.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ���������ݣ�");
			return;
		}
		// ����ԭ��
		String value = (String) this
				.openDialog("%ROOT%\\config\\sum\\SUMDefeasance.x");
		if (value == null)
			return;
		// �õ�ѡ���е�EXAMINE_DATE
		String examineDate = StringTool.getString(
				StringTool.getDate(
						masterTable.getItemString(selRow, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd");
		String defSel = "SELECT * FROM SUM_VITALSIGN WHERE ADM_TYPE = '"
				+ admType + "' AND CASE_NO = '" + caseNo
				+ "' AND EXAMINE_DATE = '" + examineDate + "'";
		TDS defData = new TDS();
		defData.setSQL(defSel);
		defData.retrieve();
		defData.setItem(0, "DISPOSAL_REASON", value);
		defData.setItem(0, "DISPOSAL_FLG", "Y");
		if (!defData.update()) {
			this.messageBox("����ʧ�ܣ�");
			return;
		}
		this.messageBox("���ϳɹ���");
		onClear();
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (masterTable.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ���ݣ�");
			return;
		}
		// ��ʱû��
		TParm prtForSheetParm = new TParm();
		// ��ô�ӡ������
		TParm parmDate = new TParm();
		// ��Ժ����ʱ��
		Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
		parmDate.setData("IN_DATE", inDate);
		TParm value = (TParm) this.openDialog(
				"%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
		if (value == null) {
			prtForSheetParm.setData("STOP", "ȡ����ӡ��");
			return;
		}
		// �õ�ѡ��ʱ��֮��ġ������+1===>��ӡ������
		int differCount = StringTool.getDateDiffer(
				value.getTimestamp("END_DATE"),
				value.getTimestamp("START_DATE")) + 1;
		if (differCount <= 0) {
			prtForSheetParm.setData("STOP", "��ѯ�������");
			return;
		}
		// ��ӡ�ı�����
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;

		Timestamp forDate = null;
		int pageNo = 1;
		for (int i = 0; i < pageCount; i++) {
			// ��װ�˿�ʼ���ںͽ�������
			TParm parm = new TParm();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (i == 0)
				startDate = value.getTimestamp("START_DATE");
			else
				startDate = StringTool.rollDate(forDate, 1);
			int dif = 6;
			// if(i % 7 == 0)
			// dif = 6;
			// else
			// dif = 7 - (i * 7 - differCount) - 1;
			endDate = StringTool.rollDate(startDate, dif);
			forDate = endDate;
			parm.setData("START_DATE", startDate);
			parm.setData("END_DATE", endDate);
			// ��ӡ������
			TParm printData = getValueForPrt(parm, dif + 1, i + 1);// �ؼ�����1
			if (printData == null)
				continue;
			printData.setData("PAGENO", "TEXT", pageNo++);
			if (printData.getData("STOP") != null) {
				this.messageBox(printData.getValue("STOP"));
				return;
			}
			// �������µ��ϴ�EMR
			Object returnObj = null;
			if ("E".equals(admType))
				returnObj = openPrintDialog(
						"%ROOT%\\config\\prt\\sum\\SUMVitalSign_PrtSheetE.jhw",
						printData);
			else if ("I".equals(admType))
				returnObj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\sum\\SUMVitalSign_PrtSheet.jhw",
						printData);
			if (returnObj != null) {
				String mr_no = patInfo.getValue("MR_NO", 0);
				EMRTool emrTool = new EMRTool(this.caseNo, mr_no, this);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String fileName = "���µ�" + format.format(startDate) + "-"
						+ format.format(endDate);
				// ======== modify by chenxi 20120702 false��ʾ���µ���ӡʱֻ����һ��
				emrTool.saveEMR(returnObj, fileName, "EMR100002",
						"EMR10000202", false);
			}
		}
	}

	/**
	 * �õ�UI�ϵĲ�������ӡ����
	 * 
	 * @param value
	 *            TParm ��װ�˿�ʼ���ںͽ�������
	 * @param differCount
	 *            int ����
	 * @param pageNo
	 *            int ҳ��
	 * @return
	 */
	private TParm getValueForPrt(TParm value, int differCount, int pageNo) {
		TParm prtForSheetParm = new TParm();
		// ��������������
		Vector tprSign = getVitalSignDate(value);// �ؼ�����2
		if (((TParm) tprSign.get(0)).getCount() <= 0)
			return null;
		// ��ӡ�����㷨��������ת��������
		prtForSheetParm = dataToCoordinate(tprSign, differCount);// �ؼ�����3
		String stationString = "";
		if ("E".equals(admType))
			stationString = patInfo.getValue("ERD_REGION_DESC", 0);
		else if ("I".equals(admType)) {
			String stationCode = patInfo.getValue("STATION_CODE", 0);
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ stationCode + "'"));
			stationString = parm.getValue("STATION_DESC", 0);
		}
		String mrNo = patInfo.getValue("MR_NO", 0);
		// ͨ��MR_NO�õ��Ա�
		Pat pat = Pat.onQueryByMrNo(mrNo);
		String sex = pat.getSexString();
		String ipdNo = patInfo.getValue("IPD_NO", 0);
		String bedNo = patInfo.getValue("BED_NO", 0);
		String bedString = "";
		if ("E".equals(admType))
			bedString = patInfo.getValue("BED_DESC", 0);
		else if ("I".equals(admType)) {
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT BED_NO_DESC FROM SYS_BED WHERE BED_NO='" + bedNo
							+ "'"));
			bedString = bedParm.getValue("BED_NO_DESC", 0);
		}
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ patInfo.getValue("IN_DEPT_CODE", 0) + "'"));
		String Name = pat.getName();
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				patInfo.getTimestamp("IN_DATE", 0));
		prtForSheetParm.setData("MR_NO", "TEXT", mrNo);
		prtForSheetParm.setData("IPD_NO", "TEXT", ipdNo);
		prtForSheetParm.setData("DEPT", "TEXT",
				deptParm.getValue("DEPT_CHN_DESC", 0));
		prtForSheetParm.setData("BED_NO", "TEXT", bedString);
		prtForSheetParm.setData("STATION", "TEXT", stationString);
		prtForSheetParm.setData("NAME", "TEXT", Name);
		prtForSheetParm.setData("SEX", "TEXT", sex);
		prtForSheetParm.setData("AGE", "TEXT", age);
		prtForSheetParm.setData("IN_DATE", "TEXT", StringTool.getString(
				patInfo.getTimestamp("IN_DATE", 0), "yyyy��MM��dd��"));
		return prtForSheetParm;
	}

	/**
	 * ��ӡ�����㷨��������ת��������
	 * 
	 * @param tprSign
	 *            Vector ��Ҫ����
	 * @param differCount
	 *            int Ҫ��ӡ��������endDate-startDate��
	 */
	public TParm dataToCoordinate(Vector tprSign, int differCount) {
		TParm mainPrtData = new TParm();
		// ��ϸ������
		TParm master = (TParm) tprSign.get(0); // ���������
		TParm detail = (TParm) tprSign.get(1); // (����)
		int countDays = detail.getCount("SYSTOLICPRESSURE") / 6;
		Vector finalSystol = new Vector();
		Vector finalDiastol = new Vector();

		for (int i = 0; i < countDays; i++) {
			String systol = "";
			String diastol = "";
			for (int j = 0; j < 6; j++) {
				if (!StringUtil.isNullString(detail.getValue(
						"SYSTOLICPRESSURE", i * 6 + j))
						&& detail.getDouble("SYSTOLICPRESSURE", i * 6 + j) != 0) {
					systol = detail.getValue("SYSTOLICPRESSURE", i * 6 + j);
				}
				if (!StringUtil.isNullString(detail.getValue(
						"DIASTOLICPRESSURE", i * 6 + j))
						&& detail.getDouble("DIASTOLICPRESSURE", i * 6 + j) != 0) {
					diastol = detail.getValue("DIASTOLICPRESSURE", i * 6 + j);
				}
			}
			finalSystol.add(systol);
			finalDiastol.add(diastol);
		}

		// �õ���������
		Vector examineDate = (Vector) master.getData("EXAMINE_DATE");
		// �õ��������������
		// modified by WangQing 20170301
		// ����specialStoolNote
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0, c7 = 0, c3S = 0, c3D = 0, c8 = 0, c9 = 0, c10 = 0, opeDaysVC = 0, cDrainage = 0, cEnema = 0, cAutoStool = 0, specialStoolNote = 0;
		int Sh1 = 0;
		// ���������ó�����
		int countWord = 0;
		// ���ѡ�����������>����/6˵�����������ݣ�������/6Ϊ���¡�����--���ص���Ч����/6
		int newDates = detail.getCount("TEMPERATURE") / 6;
		if (differCount > newDates)
			differCount = newDates;
		// ���ݣ�����/7���õ���Ҫ������ҳ��
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;
		TParm controlPage = new TParm();

		// ������ҳ
		for (int i = 1; i <= pageCount; i++) {
			ArrayList dotList_T = new ArrayList();// ����
			ArrayList dotList_PL = new ArrayList();// ����
			ArrayList dotList_R = new ArrayList();// ����
			ArrayList dotList_P = new ArrayList();// ������
			ArrayList dotList_H = new ArrayList();// ����
			ArrayList lineList_T = new ArrayList();
			ArrayList lineList_PL = new ArrayList();
			ArrayList lineList_R = new ArrayList();
			ArrayList lineList_P = new ArrayList();
			ArrayList lineList_H = new ArrayList();
			// ����ҳ��
			controlPage.addData("PAGE", "" + i);
			// Ƕ����ѭ��������----------------------start-------------------------
			// int date = differCount - (i * 7) % 7;
			int date;
			if (i * 7 <= differCount)
				date = 7;
			else
				date = 7 - (i * 7 - differCount);
			int xT = -1;
			int yT = -1;
			int xPL = -1;
			int yPL = -1;
			int xR = -1;
			int yR = -1;
			int xH = -1;
			int yH = -1;
			String notForward = "";
			String notForward2 = "";
			for (int j = 1; j <= date; j++) {
				// ���ڲ����ʱ��
				// ����ֵ
				Vector respireValue = new Vector();
				// double temForward = (Double) ( (Vector) temperatureV.get( (1
				// + (j - 1) * 6) - 1)).get(0);
				double temper1 = detail.getDouble("TEMPERATURE",
						(1 + (j - 1) * 6) - 1);
				String not1 = nullToEmptyStr(detail.getValue("NOTPRREASONCODE",
						(1 + (j - 1) * 6) - 1));
				if (temper1 != 0
						|| (temper1 == 0 && !StringUtil.isNullString(not1))) {
					notForward = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
					notForward2 = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
				}
				int temperHorizontal = 0;
				int temperVertical = 0;
				for (int exa = 1; exa <= 6; exa++) {
					// �õ�����-------------------start---------------------------
					double temper = detail.getDouble("TEMPERATURE", exa
							+ (j - 1) * 6 - 1);
					double temperBak = temper;
					String tempKindCode = nullToEmptyStr(detail.getValue(
							"TMPTRKINDCODE", exa + (j - 1) * 6 - 1));
					// ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// continue;
						// ���¶�<=35��ʱ��д�����²�����
						if (temper <= 35) {
							// ��ͱ߽�35��
							temper = 35;
							controlPage.addData(
									"NORAISE" + (exa + (j - 1) * 6), "���²���");
						}
						// �õ����µĺ������꣨�㣩
						temperHorizontal = countHorizontal(j, exa);
						temperVertical = (int) (getVertical(temper, "T") + 0.5); // ȡ��
						int dataTemper[] = new int[] {};
						if (temperBak >= 35) {// shibl modify �¶����಻��Ϊ��
							if (tempKindCode.equals("4")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7 };
							} else if (tempKindCode.equals("3")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6 };
							} else {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4 };
							}
						} else if (temperBak < 35) {
							if (tempKindCode.equals("4")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7, 1 };
							} else if (tempKindCode.equals("3")) {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6, 1 };
							} else {
								// �õ�һ���������
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4, 1 };
							}
						}
						// �������е�
						dotList_T.add(dataTemper);
					}
					// --------------------------end-----------------------------

					// �õ������ĵ�----------------start--------------------------
					double pluse = detail.getDouble("PLUSE", exa + (j - 1) * 6
							- 1);
					// ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
					int pluseHorizontal = 0;
					int pluseVertical = 0;
					if (pluse != 0) {
						// continue;
						// �õ������ĺ������꣨�㣩
						pluseHorizontal = countHorizontal(j, exa);
						pluseVertical = (int) (getVertical(pluse, "PL") + 0.5); // ȡ��
						int dataPluse[] = new int[] {};
						// �õ�һ���������
						dataPluse = new int[] { pluseHorizontal, pluseVertical,
								6, 6, 4 };
						for (int k = 0; k < dotList_T.size(); k++) {
							if (pluseHorizontal == ((int[]) dotList_T.get(k))[0]
									&& pluseVertical == ((int[]) dotList_T
											.get(k))[1]) {
								// �õ�һ���������
								dataPluse = new int[] { pluseHorizontal,
										pluseVertical, 6, 6, 6 };
								break;
							}
						}
						// �������е�
						dotList_PL.add(dataPluse);
					}
					// ---------------------------end----------------------------

					// �õ�����--------------------start--------------------------
					// modified by WangQing 20170301 -start
					if (detail.getValue("SPECIALRESPIRENOTE",
							exa + (j - 1) * 6 - 1).equals("R")) {
						respireValue.add(detail.getValue("SPECIALRESPIRENOTE",
								exa + (j - 1) * 6 - 1));
					} else {
						respireValue.add(detail.getValue("RESPIRE", exa
								+ (j - 1) * 6 - 1));
					}
					// modified by WangQing 20170301 -end

					/*
					 * Double respire = Double.parseDouble( ( (Vector)
					 * respireV.get( (exa + (j - 1) * 6) - 1)).get(0) + "");
					 * //��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻��� if (respire == 0)
					 * continue; //�õ������ĺ������꣨�㣩 int respireHorizontal =
					 * countHorizontal(j, exa); int respireVertical = (int)
					 * (getVertical(respire,"R") + 0.5); //ȡ�� //�õ�һ��������� int
					 * dataRespire[] = new int[] { respireHorizontal,
					 * respireVertical, 6, 6, 4}; //�������е�
					 * dotList_R.add(dataRespire);
					 */
					// ----------------------------end---------------------------

					// �õ����ʵĵ�----------------start--------------------------
					double heartRate = detail.getDouble("HEART_RATE",
							(exa + (j - 1) * 6) - 1);
					// ��ΪNULL��ʱ��Ϊ������������Զ�ת����0����ô��Ϊ0��ʱ�򲻻���
					int heartRateHorizontal = 0;
					int heartRateVertical = 0;
					if (heartRate != 0) {
						// continue;
						// �õ����ʵĺ������꣨�㣩
						heartRateHorizontal = countHorizontal(j, exa);
						heartRateVertical = (int) (getVertical(heartRate, "H") + 0.5); // ȡ��
						// �õ�һ���������
						int dataHeartRate[] = new int[] { heartRateHorizontal,
								heartRateVertical, 6, 6, 6 };
						// �������е�
						dotList_H.add(dataHeartRate);
					}
					// ---------------------------end----------------------------

					// �õ�������-----------------start--------------------------
					String tempPhsi = detail.getValue("PHYSIATRICS", exa
							+ (j - 1) * 6 - 1);
					if (!StringUtil.isNullString(tempPhsi)) {
						// �õ��������͵�
						double phsiatrics = TCM_Transform.getDouble(tempPhsi);
						if (phsiatrics <= 35) {
							// ��ͱ߽�35��
							phsiatrics = 35;
						}
						// �õ����µĺ������꣨�㣩
						int phsiHorizontal = countHorizontal(j, exa);
						int phsiVertical = (int) (getVertical(phsiatrics, "P") + 0.5); // ȡ��
						// �õ�һ���������
						int dataPhsi[] = new int[] { phsiHorizontal,
								phsiVertical, 6, 6, 6 };
						// �������е�
						dotList_P.add(dataPhsi);
						// �õ���������-----------start--------------------------
						int dataTempLine[] = new int[] { temperHorizontal + 3,
								temperVertical + 3, phsiHorizontal + 3,
								phsiVertical + 3, 1 };
						lineList_P.add(dataTempLine);
					}
					// ----------------------------end---------------------------

					// �õ�Ϊ����ԭ��
					String not = nullToEmptyStr(detail.getValue(
							"NOTPRREASONCODE", (exa + (j - 1) * 6) - 1));
					if (!StringUtil.isNullString(not)) {
						String sql1 = " SELECT CHN_DESC "
								+ " FROM SYS_DICTIONARY"
								+ " WHERE GROUP_ID='SUM_NOTMPREASON'"
								+ " AND   ID = '" + not + "'";
						TParm result1 = new TParm(TJDODBTool.getInstance()
								.select(sql1));
						controlPage.addData("REASON" + (exa + (j - 1) * 6),
								result1.getValue("CHN_DESC", 0));
					}

					// �õ����µ���----------------start--------------------------
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// if(countWord ==0 ||
						// StringTool.getDateDiffer(StringTool.getTimestamp(""+examineDate.get(countWord),"yyyyMMdd"),
						// StringTool.getTimestamp(""+examineDate.get(countWord
						// - 1),"yyyyMMdd")) <= 1 ||
						// exa != 1)
						if (xT != -1 && yT != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward)) {
							int dataTempLine[] = new int[] { xT + 3, yT + 3,
									temperHorizontal + 3, temperVertical + 3, 1 };
							lineList_T.add(dataTempLine);
						}
						xT = temperHorizontal;
						yT = temperVertical;
					}
					// temForward = temper;
					if (temper != 0
							|| (temper == 0 && !StringUtil.isNullString(not))) {
						notForward = not;
					}
					// --------------------------end----------------------------

					// �õ���������----------------start--------------------------
					if (pluse != 0) {
						if (xPL != -1 && yPL != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward2)) {
							int dataPluseLine[] = new int[] { xPL + 3, yPL + 3,
									pluseHorizontal + 3, pluseVertical + 3, 1 };
							lineList_PL.add(dataPluseLine);
						}
						xPL = pluseHorizontal;
						yPL = pluseVertical;
					}
					// temForward = temper;
					if (pluse != 0
							|| (pluse == 0 && !StringUtil.isNullString(not))) {
						notForward2 = not;
					}
					// --------------------------end----------------------------

					// �õ���������----------------start--------------------------
					/*
					 * if (xR != -1 && yR != -1 && "null".equals(not)) { int
					 * dataRespireLine[] = new int[] { xR + 3, yR + 3,
					 * respireHorizontal + 3, respireVertical + 3, 1};
					 * lineList_R.add(dataRespireLine); } xR =
					 * respireHorizontal; yR = respireVertical;
					 */
					// --------------------------end----------------------------

					// �õ����ʵ���----------------start--------------------------
					if (heartRate != 0) {
						if (xH != -1 && yH != -1
								&& StringUtil.isNullString(not)) {
							int dataHeartRateLine[] = new int[] { xH + 3,
									yH + 3, heartRateHorizontal + 3,
									heartRateVertical + 3, 1 };
							lineList_H.add(dataHeartRateLine);
						}
						xH = heartRateHorizontal;
						yH = heartRateVertical;
					}
					// --------------------------end----------------------------

					// ���˶�̬��Ϣ----------------start--------------------------
					String ptMoveCode = nullToEmptyStr(detail.getValue(
							"PTMOVECATECODE", exa + (j - 1) * 6 - 1));
					if (!StringUtil.isNullString(ptMoveCode)) {
						String ptMoveDesc = nullToEmptyStr(detail.getValue(
								"PTMOVECATEDESC", exa + (j - 1) * 6 - 1));
						controlPage.addData("MOVE" + (exa + (j - 1) * 6),
								ptMoveCode + "||" + ptMoveDesc);
					}
				}

				// �õ�����-------------------------start-------------------------
				String tenmpDate = examineDate.get(countWord++).toString();
				String fomatDate = "";
				if (countWord - 1 == 0) {
					fomatDate = tenmpDate.substring(0, 4) + "."
							+ tenmpDate.substring(4, 6) + "."
							+ tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				} else {
					String tenmpDateForward = examineDate.get(countWord - 2)
							.toString();
					if (!tenmpDateForward.substring(2, 4).equals(
							tenmpDate.substring(2, 4)))
						fomatDate = tenmpDate.substring(0, 4) + "."
								+ tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else if (!tenmpDateForward.substring(4, 6).equals(
							tenmpDate.substring(4, 6)))
						fomatDate = tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else
						fomatDate = tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				}
				controlPage.addData("OPEDAY" + j,
						master.getData("OPE_DAYS", opeDaysVC++));
				// ��Ժ����ʱ��
				Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
				// �õ��ó���������������-�������ӣ�-------------------------------
				int dates = getBornDateDiffer(tenmpDate,
						StringTool.getTimestampDate(inDate)) + 1;
				// dates = getInHospDaysE();
				controlPage.addData("INDATE" + j, dates == 0 ? "" : dates);
				// ��������OPEDAYn
				// -----------------------------------

				// �õ��������������----------------------start------------------------
				for (int k = 0; k < respireValue.size(); k++) {
					try {
						if (respireValue.get(k).toString().equals("R")) {
							controlPage.addData("L1_" + j + (k + 1), "R");
						} else {
							controlPage.addData(
									"L1_" + j + (k + 1),
									(int) Double.parseDouble(""
											+ clearZero(respireValue.get(k))));
						}
					} catch (Exception e) {
						controlPage.addData("L1_" + j + (k + 1),
								clearZero(respireValue.get(k)));
					}
				}

				// ==============================�ű� -start==============

				// if (StringUtil.isNullString(master.getValue("STOOL",
				// cAutoStool))
				// && StringUtil.isNullString(master.getValue("ENEMA",cEnema)))
				// {
				// controlPage.addData("L2" + j, "");
				// controlPage.addData("L12" + j, "");
				// controlPage.addData("L13" + j, "");
				// controlPage.addData("L14" + j, "");
				// } else {
				// // modify by wukai 20160606
				// // controlPage.addData("L2" + j, master.getData("STOOL",
				// // cAutoStool));
				// Object stool = master.getData("STOOL", cAutoStool);
				//
				// // Object specialStoolNote = master.getData("STOOL",
				// cAutoStool);
				//
				//
				//
				// Object ename = master.getData("ENEMA", cEnema);
				// if ((stool == null || "0".equals(stool)) && (ename != null))
				// {
				// controlPage.addData("L2" + j, "");
				// } else {
				// controlPage.addData("L2" + j, stool);
				// }
				// if (StringUtil.isNullString(master.getValue("ENEMA",
				// cEnema))) {
				// controlPage.addData("L12" + j, "");
				// controlPage.addData("L13" + j, "");
				// controlPage.addData("L14" + j, "");
				// } else {
				// controlPage.addData("L12" + j, master.getValue("ENEMA",
				// cEnema));
				// controlPage.addData("L13" + j, "/");
				// controlPage.addData("L14" + j, "E");
				// }
				// }
				// cAutoStool++;
				// cEnema++;
				//

				// modified by WangQing 20170301 -start
				// ����������ű㣬��ֻ��ʾ�����ű㣻������ʾ�����ű�+�೦
				if (!(StringUtil.isNullString(master.getValue(
						"SPECIALSTOOLNOTE", specialStoolNote)))) {// �����ű�
					String sql = "select DESCRIPTION from SYS_DICTIONARY where GROUP_ID='PB_TYPE' and ID='"
							+ master.getValue("SPECIALSTOOLNOTE",
									specialStoolNote) + "' ";
					TParm parm = new TParm();
					parm.setData(TJDODBTool.getInstance().select(sql));
					controlPage.addData("L2" + j,
							parm.getValue("DESCRIPTION", 0));// �����ű�
				} else {
					if ((!(StringUtil.isNullString(master.getValue("STOOL",
							cAutoStool))))
							|| (!(master.getValue("STOOL", cAutoStool)
									.equals("0")))) {// �����ű�
						controlPage.addData("L2" + j,
								master.getValue("STOOL", cAutoStool));// �����ű�
					}
					if ((!(StringUtil.isNullString(master.getValue("ENEMA",
							cEnema))))
							&& (!(master.getValue("ENEMA", cEnema).equals("0")))) {// �೦
						controlPage.addData("L12" + j,
								master.getValue("ENEMA", cEnema));// �೦����
						controlPage.addData("L13" + j, "/");
						controlPage.addData("L14" + j, "E");
					}
				}
				cAutoStool++;
				specialStoolNote++;
				cEnema++;
				// modified by WangQing 20170301 -end

				// ==============================�ű�
				// -end=================================

				if ((clearZero(finalSystol.get(c3S)) + "").length() == 0
						&& (clearZero(finalDiastol.get(c3D)) + "").length() == 0)
					controlPage.addData("L3", "");
				else
					controlPage.addData("L3" + j, finalSystol.get(c3S) + "/"
							+ finalDiastol.get(c3D));
				c3S++;
				c3D++;
				controlPage.addData("L4" + j,
						clearZero(master.getData("INTAKEFLUIDQTY", c4++)));
				controlPage.addData("L5" + j,
						clearZero(master.getData("OUTPUTURINEQTY", c5++)));
				controlPage.addData("L7" + j,
						clearZero(master.getData("WEIGHT", c6++)));
				controlPage.addData("L6" + j,
						clearZero(master.getData("HEIGHT", c7++)));
				controlPage.addData("L11" + j,
						clearZero(master.getData("DRAINAGE", cDrainage++)));
				for (int l = 0; l < master.getCount("USER_DEFINE_1") && j == 1; l++) {
					if (StringUtil.isNullString(master.getValue(
							"USER_DEFINE_1", l)))
						continue;
					controlPage.addData("L80",
							master.getData("USER_DEFINE_1", l));
					break;
				}
				Object obj8 = master.getData("USER_DEFINE_1_VALUE", c8++);
				if (obj8 == null || obj8.equals(""))
					controlPage.addData("L8" + j, "");
				else
					controlPage.addData("L8" + j, obj8);
				for (int l = 0; l < master.getCount("USER_DEFINE_2") && j == 1; l++) {
					if (StringUtil.isNullString(master.getValue(
							"USER_DEFINE_2", l)))
						continue;
					controlPage.addData("L90",
							master.getData("USER_DEFINE_2", l));
					break;
				}
				Object obj9 = master.getData("USER_DEFINE_2_VALUE", c9++);
				if (obj9 == null || obj9.equals(""))
					controlPage.addData("L9" + j, "");
				else
					controlPage.addData("L9" + j, obj9);
				for (int l = 0; l < master.getCount("USER_DEFINE_3") && j == 1; l++) {
					if (StringUtil.isNullString(master.getValue(
							"USER_DEFINE_3", l)))
						continue;
					controlPage.addData("L100",
							master.getData("USER_DEFINE_3", l));
					break;
				}
				Object obj10 = master.getData("USER_DEFINE_3_VALUE", c10++);
				;
				if (obj10 == null || obj10.equals(""))
					controlPage.addData("L10" + j, "");
				else
					controlPage.addData("L10" + j, obj10);
			}

			// ���µ�
			int pageDataForT[][] = new int[dotList_T.size()][5];
			for (int j = 0; j < dotList_T.size(); j++)
				pageDataForT[j] = (int[]) dotList_T.get(j);
			// ������
			int pageDataForPL[][] = new int[dotList_PL.size()][5];
			for (int j = 0; j < dotList_PL.size(); j++)
				pageDataForPL[j] = (int[]) dotList_PL.get(j);
			// ����
			int pageDataForR[][] = new int[dotList_R.size()][5];
			for (int j = 0; j < dotList_R.size(); j++)
				pageDataForR[j] = (int[]) dotList_R.get(j);
			// ����
			int pageDataForH[][] = new int[dotList_H.size()][5];
			for (int j = 0; j < dotList_H.size(); j++)
				pageDataForH[j] = (int[]) dotList_H.get(j);
			// �����µ�
			int pageDataForP[][] = new int[dotList_P.size()][5];
			for (int j = 0; j < dotList_P.size(); j++)
				pageDataForP[j] = (int[]) dotList_P.get(j);
			// ������
			int pageDataForTLine[][] = new int[lineList_T.size()][5];
			for (int j = 0; j < lineList_T.size(); j++) {
				pageDataForTLine[j] = (int[]) lineList_T.get(j);
			}
			// ������
			int pageDataForPLLine[][] = new int[lineList_PL.size()][5];
			for (int j = 0; j < lineList_PL.size(); j++)
				pageDataForPLLine[j] = (int[]) lineList_PL.get(j);
			// ������
			int pageDataForRLine[][] = new int[lineList_R.size()][5];
			for (int j = 0; j < lineList_R.size(); j++)
				pageDataForRLine[j] = (int[]) lineList_R.get(j);
			// ������
			int pageDataForPLine[][] = new int[lineList_P.size()][5];
			for (int j = 0; j < lineList_P.size(); j++)
				pageDataForPLine[j] = (int[]) lineList_P.get(j);
			// ������
			int pageDataForHLine[][] = new int[lineList_H.size()][5];
			for (int j = 0; j < lineList_H.size(); j++) {
				pageDataForHLine[j] = (int[]) lineList_H.get(j);
			}
			controlPage.addData("TEMPDOT", pageDataForT);
			controlPage.addData("PLUSEDOT", pageDataForPL);
			// controlPage.addData("RESPIREDOT", pageDataForR);
			controlPage.addData("PHSIDOT", pageDataForP);
			controlPage.addData("RESPIREDOT", pageDataForH);
			controlPage.addData("TEMPLINE", pageDataForTLine);
			controlPage.addData("PLUSELINE", pageDataForPLLine);
			// controlPage.addData("RESPIRELINE", pageDataForRLine);
			controlPage.addData("PHSILINE", pageDataForPLine);
			controlPage.addData("RESPIRELINE", pageDataForHLine);
			// ----------------------------end----------------------------------
		}

		// ����ҳ��
		controlPage.setCount(pageCount);
		controlPage.addData("SYSTEM", "COLUMNS", "PAGE");
		mainPrtData.setData("TABLE", controlPage.getData());
		return mainPrtData;
	}

	public Object clearZero(Object obj) {
		try {
			if (obj == null)
				return "";
			double mun = Double.parseDouble("" + obj);
			if (mun == 0)
				return "";
			else
				return obj;
		} catch (NumberFormatException e) {
			return obj;
		}
	}

	/**
	 * �õ�����������Ĳ�
	 * 
	 * @param nowDate
	 *            String
	 * @return int
	 */
	public int getBornDateDiffer(String date, Timestamp bornDate) {
		Timestamp nowDate = StringTool.getTimestamp(date, "yyyyMMdd");
		return StringTool.getDateDiffer(nowDate, bornDate);
	}

	/**
	 * �����������λ��
	 * 
	 * @param date
	 *            int
	 * @param examineSession
	 *            int
	 * @return int
	 */
	private int countHorizontal(int date, int examineSession) {
		// int adaptX = 7;
		// return 10 * ( (date - 1) * 6 + examineSession) - adaptX;
		int adaptX = 7;
		return 8 * ((date - 1) * 6 + examineSession) - adaptX;
	}

	/**
	 * �������µ�����������
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	private double getVertical(double value, String flag) {
		int adaptY = 8;
		// ���»���������
		if ("T".equals(flag) || "P".equals(flag))
			return (445 - countVertical(value, 42, 34, 40) * 10) - adaptY;
		// ����
		if ("PL".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		// ����
		if ("H".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		return -1;
	}

	/**
	 * �����������λ��--����
	 * 
	 * @param value
	 *            int ���ݿ��м�¼������
	 * @param topValue
	 *            int ���������ֵ--��
	 * @param butValue
	 *            int �������С��ֵ--��
	 * @param level
	 *            int �������С֮����ж��ٵȼ�-����
	 * @return int
	 */
	private double countVertical(double value, double topValue,
			double butValue, int level) {
		return (value - butValue) / ((topValue - butValue) / level) - 1;
	}

	/**
	 * �õ���Ҫ��ӡ��������
	 * 
	 * @param date
	 *            TParm
	 */
	public Vector getVitalSignDate(TParm date) {
		Vector tprSign = new Vector();
		date.setData("ADM_TYPE", admType);
		date.setData("CASE_NO", caseNo);
		// ��������
		TParm vitalSignMstParm = SUMVitalSignTool.getInstance().selectdataMst(
				date);
		// ����ϸ��
		TParm vitalSignDtlParm = SUMVitalSignTool.getInstance().selectdataDtl(
				date);
		// ������ǽ����0-������Ϣ 1-ϸ����Ϣ
		tprSign.add(vitalSignMstParm);
		tprSign.add(vitalSignDtlParm);
		return tprSign;
	}

	/**
	 * TParm�С�null����תΪ���ַ���
	 * 
	 * @param str
	 * @return
	 */
	public String nullToEmptyStr(String str) {
		if (str == null || str.equalsIgnoreCase("null")) {
			return "";
		}
		return str;
	}

	/**
	 * ����NIS������
	 */
	public void onNisVitalSign() {
		SystemTool.getInstance().OpenNisVitalSign(caseNo,
				patInfo.getValue("MR_NO", 0));
	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if (isMroFlg)
			return true;
		return true;
	}

	public static void main(String[] args) {
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sum\\SUMVitalSign.x");
	}

	/**
	 * ȫѡ��ѡ
	 */
	public void onCheckAll() {
		boolean flag = true;
		if (getCheckBox("CHECK_ALL").isSelected()) {
			flag = true;
		} else {
			flag = false;
		}
		getCheckBox("CHECK_2").setSelected(flag);
		getCheckBox("CHECK_6").setSelected(flag);
		getCheckBox("CHECK_10").setSelected(flag);
		getCheckBox("CHECK_14").setSelected(flag);
		getCheckBox("CHECK_18").setSelected(flag);
		getCheckBox("CHECK_22").setSelected(flag);
	}

	/**
	 * ͬ��CIS��ͼ����
	 */
	public void extractCisData() {
		TParm selParm = masterTable.getParmValue().getRow(masterRow);
		String selDate = selParm.getValue("EXAMINE_DATE");
		TParm parm = new TParm();
		parm.setData("START_POOLING_TIME", selDate + " 00:00");
		parm.setData("END_POOLING_TIME", selDate + " 23:59");
		parm.setData("CASE_NO", caseNo);

		// ��ѯ������ǰ��Ժ��Ϣ
		TParm admResult = ADMInpTool.getInstance().queryCaseNo(parm);

		if (admResult.getErrCode() < 0) {
			this.messageBox("��ѯ����סԺ��Ϣ����");
			err("ERR:" + admResult.getErrText());
			return;
		} else if (admResult.getCount("CASE_NO") < 1) {
			this.messageBox("���޲���סԺ��Ϣ");
			return;
		} else {
			// ȡ�ò�����ǰ����
			String stationCode = admResult.getValue("STATION_CODE", 0);
			String viewName = "";
			String databaseName = "";

			if (StringUtils.isNotEmpty(stationCode)) {
				// ���ݲ������ڲ�����CIS��Ӧ����ͼ��ȡ�����µ�����
				if ("I01,I02,I03,I04".contains(stationCode)) {
					databaseName = "javahisICU";
					viewName = "dbo.V_ICU_Vitalsigns";
				} else if ("C01,C02".contains(stationCode)) {
					databaseName = "javahisCCU";
					viewName = "dbo.V_CCU_Vitalsigns";
				} else {
					databaseName = "javahisWard";
					viewName = "dbo.V_Ward_Vitalsigns";
				}

				// ��ѯ����CIS����
				TParm result = ODICISVitalSignTool.getInstance()
						.queryODICISData(parm, viewName, databaseName);

				if (result.getErrCode() < 0) {
					this.messageBox("��ѯCIS�������ݴ���");
					err("ERR:" + admResult.getErrText());
					return;
				} else if (result.getCount("CASE_NO") < 1) {
					this.messageBox("����CIS��������");
					return;
				} else {
					this.setCisData(result);
				}
			}
		}
	}

	/**
	 * ��CISȡ�õ��������ݷŵ����µ������Ӧ�ؼ���
	 */
	private void setCisData(TParm parm) {
		TParm cisFilterResult = new TParm();
		int count = parm.getCount();
		// �����Ŀ�谴���������У������ɼ������ݱ����������Ż�����
		String[] monitorItemArray = { "ABPD", "ABPS", "BT2", "HR", "NBPD",
				"NBPS", "PULSE", "RR" };
		String[] monitorTimeArray = { "02", "06", "10", "14", "18", "22" };
		int monitorItemArrayLen = monitorItemArray.length;
		int monitorTimeArrayLen = monitorTimeArray.length;
		List<String> dataList = new ArrayList<String>();
		String key = "";
		// ����
		String inTake = "";
		// ����
		String outPut = "";

		// �������µ��Ĺ̶�ʱ��Σ��ֱ��ղ�ͬ�����Ŀȡ����ʱ��μ���������������ݶ���cisFilterResult
		for (int i = 0; i < monitorItemArrayLen; i++) {
			for (int j = 0; j < monitorTimeArrayLen; j++) {
				key = monitorItemArray[i] + "_" + monitorTimeArray[j];
				for (int k = 0; k < count; k++) {
					if ((parm.getValue("MONITOR_ITEM_EN", k) + "_" + parm
							.getValue("MONITOR_TIME", k).substring(11, 13))
							.equals(key)
							&& !dataList.contains(key)) {
						dataList.add(key);
						cisFilterResult.addData(monitorItemArray[i],
								parm.getValue("MONITOR_VALUE", k));
						break;
					} else if (k == count - 1) {
						cisFilterResult.addData(monitorItemArray[i], "");
					}
				}
			}
		}

		// ȡ������������
		for (int l = 0; l < count; l++) {
			if (parm.getValue("MONITOR_ITEM_EN", l).equals("INTAKE")
					&& StringUtils.isEmpty(inTake)) {
				inTake = parm.getValue("MONITOR_VALUE", l);
			}
			if (parm.getValue("MONITOR_ITEM_EN", l).equals("OUTPUT")
					&& StringUtils.isEmpty(outPut)) {
				outPut = parm.getValue("MONITOR_VALUE", l);
			}
			if (StringUtils.isNotEmpty(inTake)
					&& StringUtils.isNotEmpty(outPut)) {
				break;
			}
		}

		cisFilterResult.setCount(cisFilterResult.getCount("RR"));
		// ICU����ȡ�д�Ѫѹ���ȡ�޴�Ѫѹ
		for (int m = 0; m < cisFilterResult.getCount(); m++) {
			if (cisFilterResult.getValue("ABPS").replace("[", "")
					.replace("]", "").replace(" ", "").length() < 6) {
				cisFilterResult.addData("BPS",
						cisFilterResult.getValue("NBPS", m));
			}
			if (cisFilterResult.getValue("ABPD").replace("[", "")
					.replace("]", "").replace(" ", "").length() < 6) {
				cisFilterResult.addData("BPD",
						cisFilterResult.getValue("NBPD", m));
			}
		}

		// ͬ��ǰ���ԭ������
		TParm oldTableParm = detailTable.getParmValue();

		TParm newTableParm = new TParm();
		String[] showTableItemArray = { "BT2", "PULSE", "RR", "BPS", "BPD",
				"HR" };
		// ���ձ�񶨺õĴ���֮�µ�˳�򹹽���ʾ�������showTableParm
		for (int n = 0; n < 6; n++) {
			for (int p = 0; p < 6; p++) {
				newTableParm.addData("" + p,
						cisFilterResult.getValue(showTableItemArray[n], p));
			}
		}

		if (getCheckBox("CHECK_2").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "0");
		}
		if (getCheckBox("CHECK_6").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "1");
		}
		if (getCheckBox("CHECK_10").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "2");
		}
		if (getCheckBox("CHECK_14").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "3");
		}
		if (getCheckBox("CHECK_18").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "4");
		}
		if (getCheckBox("CHECK_22").isSelected()) {
			this.tableDataReplace(oldTableParm, newTableParm, "5");
		}

		// ʹ�����Ϻõı��������ʾ��ҳ��
		detailTable.setParmValue(oldTableParm);
		// ���������
		this.setValue("INTAKEFLUIDQTY", inTake);
		this.setValue("OUTPUTURINEQTY", outPut);

		this.messageBox("��ȡ�ɹ�");
	}

	/**
	 * ��������滻
	 */
	private void tableDataReplace(TParm oldTableParm, TParm newTableParm,
			String group) {
		String value = "";
		for (int i = 0; i < 6; i++) {
			value = newTableParm.getValue(group, i);
			if (StringUtils.isNotEmpty(value)) {
				oldTableParm.setData(group, i, value);
			}
		}
	}

	/**
	 * �õ�TCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * �����ַ���ť�����¼�
	 * 
	 * @author wangqing 2016.11.21
	 */
	public void onSpecialChars() {
		TParm parm = new TParm();
		parm.addListener("onReturnContent", this, "onReturnContent");
		// this.messageBox("======123123========");
		TWindow window = (TWindow) openWindow(
				"%ROOT%\\config\\emr\\EMRSpecialChars.x", parm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		AnimationWindowUtils.show(window);
		AWTUtilities.setWindowOpacity(window, 0.9f);
		window.setVisible(true);
	}

	/**
	 * �����ַ���
	 * 
	 * @param String
	 *            value
	 * @author Eric 2016.11.21
	 * 
	 */
	public void onReturnContent(String value) {
		// �õ�detailTable��ѡ����
		int selectColumn = this.detailTable.getSelectedColumn();
		// �õ�ѡ���еı༭��
		TTableCellEditor editor = detailTable.getCellEditor(selectColumn);
		// ��ȡ��ǰ�༭��cell
		TTextField c = (TTextField) editor.getDelegate().getComponent();
		// System.out.println(c.getSelectionStart()+"-"+c.getSelectionEnd());
		// ��ȡcell��ֵ
		String v = c.getValue();
		// ֵ�滻
		String v2 = v.substring(0, c.getSelectionStart()) + value
				+ v.substring(c.getSelectionEnd(), v.length());
		c.setValue(v2);
		// ʹ�������ַ�����ĩβ
		c.setCaretPosition(c.getText().length());
	}

	/**
	 * ���ò�����̬
	 * 
	 * @param code
	 *            ������̬����
	 */
	private void setPatientTrends(String code) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// ��ѯסԺ��Ϣ
		TParm admResult = ADMInpTool.getInstance().selectall(parm);
		// ��Ժ��
		if ("01".equals(code)) {
			parm.setData("START_DATE", admResult.getTimestamp("ADM_DATE", 0));
			parm.setData("END_DATE", SystemTool.getInstance().getDate());
		} else if ("03".equals(code)) {
			int row = masterTable.getSelectedRow();
			if (row < 0) {
				return;
			}
			String date = masterTable.getItemString(row, "EXAMINE_DATE")
					.replaceAll("/", "-").substring(0, 10);

			int col = detailTable.getSelectedColumn();

			if (col < 0) {
				return;
			}

			int hour = col * 4 + 2;
			// ת����
			parm.setData("START_DATE", StringTool.getTimestamp(date + " "
					+ (hour - 2) + ":00:00", "yyyy-MM-dd HH:mm:ss"));
			parm.setData(
					"END_DATE",
					StringTool.getTimestamp(date + " " + (hour + 1)
							+ ":59:59.0", "yyyy-MM-dd HH:mm:ss"));
		}

		// ��̬��¼��ѯ
		TParm admChgResult = ADMChgTool.getInstance().ADMQueryChgLog(parm);
		if (admChgResult.getErrCode() < 0) {
			this.messageBox("��ѯ��̬ת�Ƽ�¼�쳣:" + admChgResult.getErrText());
			return;
		}

		if ("01".equals(code)) {
			// ����ʱ���������к�ȡ��һ���봲������Ϊ��Ժʱ��
			for (int i = 0; i < admChgResult.getCount(); i++) {
				if ("INBD".equals(admChgResult.getValue("PSF_KIND", i))) {
					this.setValue("PTMOVECATEDESC", DateUtil
							.transferHMToChinese(admChgResult.getValue(
									"CHG_DATE", i)));
					return;
				}
			}
		} else if ("03".equals(code)) {
			// ת��ʱ��
			String transferDeptTime = "";
			int seq = 1;

			// ����ʱ���������к�ȡ���һ���봲����
			for (int i = 0; i < admChgResult.getCount(); i++) {
				if ("INBD".equals(admChgResult.getValue("PSF_KIND", i))) {
					transferDeptTime = admChgResult.getValue("CHG_DATE", i);
					seq = admChgResult.getInt("SEQ_NO", i);
				}
			}

			// �����ǰѡ���ʱ������봲������һ�����ϣ�ȡ���һ���봲������Ϊת��ʱ��
			if (StringUtils.isNotEmpty(transferDeptTime)) {
				// �봲ǰ����һ��ת�Ƽ�¼�������ƻ�ȡ����ƣ��򱾴��봲��Ϊת��
				parm = new TParm();
				parm.setData("CASE_NO", caseNo);
				parm.setData("SEQ_NO", seq - 1);
				parm.setData("START_DATE",
						admResult.getTimestamp("ADM_DATE", 0));
				parm.setData("END_DATE", SystemTool.getInstance().getDate());

				// ��̬��¼��ѯ
				admChgResult = ADMChgTool.getInstance().ADMQueryChgLog(parm);

				if (admChgResult.getCount() > 0
						&& ("INDP".equals(admChgResult.getValue("PSF_KIND", 0)) || "CANCELINDP"
								.equals(admChgResult.getValue("CANCELINDP", 0)))) {
					this.setValue("PTMOVECATEDESC",
							DateUtil.transferHMToChinese(transferDeptTime));
				}
			}
		}
	}
}
