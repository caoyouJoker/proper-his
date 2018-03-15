package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;






import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.JXLReadExcelUtil;

/**
 * 
 * <p>
 * Title:ҽ��ͳ�Ƴ�Ժ������Ϣ����EXCLE
 * </p>
 * 
 * <p>
 * Description:ҽ��ͳ�Ƴ�Ժ������Ϣ����EXCLE
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangb 2012-5-6
 * @version 2.0
 */
public class MROAdmSearchControl extends TControl {
	private TTable table; // ����������Ϣ�б�

	private JFileChooser jfc = new JFileChooser();

	private Map disDicMap;
	
	private Map disCodeToDescMap;

	private String[] disFields = { "CASE_NO", "CITY", "STATE",
			"PRARENT_DISEASE_CODE", "DISEASE_CODE", "SPECIAL_CASE" };

	private String disdicsql = " SELECT  ID AS DISEASE_CODE,CHN_DESC AS DISEASE_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='DISEASES' ";

	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initDisDicMap();
		table = (TTable) this.getComponent(TABLE); // ����������Ϣ�б�
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// �������
		addListener(getTTable(TABLE));
	}
	@SuppressWarnings("unchecked")
	public void onTest() {
		int row = getTTable(TABLE).getSelectedRow();
		if (row < 0) {
			this.messageBox("û��ѡ����");
			return;
		}
		String caseNo = getTTable(TABLE).getParmValue().getRow(row).getValue("CASE_NO");
		try {
			TParm parm=new TParm();
			parm.addData("CASE_NO", caseNo);
			parm.setCount(parm.getCount("CASE_NO"));
			TParm result = TIOM_AppServer.executeAction("action.sta.STAWSAction", "onTest", parm);
			List<String> list=((Map<String,List<String>>)result.getData("DATA")).get(caseNo);
			setValue("DIS_CODE", list==null?"":list.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.messageBox("�������");
	}
	
	@SuppressWarnings("unchecked")
	public void onTestALL() {
		TParm parm= getTTable(TABLE).getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("����û������");
			return;
		}
		TParm result = TIOM_AppServer.executeAction("action.sta.STAWSAction", "onTest", parm);
		Map<String,List<String>> map=(Map<String,List<String>>)result.getData("DATA");
		for (String caseNo : map.keySet()) {
			try {
				List<String> list=map.get(caseNo);
				String DisCode = list.size()==1?list.get(0):"";
				String update="UPDATE MRO_RECORD SET DISEASES_CODE='"+DisCode+"' WHERE CASE_NO='"+caseNo+"'";
				result=new TParm(TJDODBTool.getInstance().update(update));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.messageBox("�������");
	}

	public void onBack() {
		int rowIndex = getTTable(TABLE).getSelectedRow();
		TParm parm = new TParm();
		TParm patInfo =getTTable(TABLE).getParmValue();
		parm.setData("MR_NO", patInfo.getValue("MR_NO", rowIndex));
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		parm.setData("USER_TYPE", "2-4");
		parm.setData("OPEN_USER", Operator.getID());
		this.openDialog("%ROOT%\\config\\mro\\MRORecord.x", parm);
	}

	// CHARGE_01:901.001һ��ҽ�Ʒ���� CHARGE_08:902.004�ٴ������Ŀ��
	// CHARGE_05:902.001������Ϸ�CHARGE_07: 902.003Ӱ��ѧ��Ϸ�
	// CHARGE_06:902.002ʵ������Ϸ� CHARGE_09:903.001.001 �ٴ��������Ʒ�
	// CHARGE_10:903.001.002���ٴ��������Ʒ�
	// CHARGE_12:903.002.002�������Ʒ�-������ CHARGE_20:908.001 Ѫ��
	// CHARGE_21:908.002�׵�������Ʒ�� CHARGE_22:908.003�򵰰�����Ʒ��
	// CHARGE23:908.004��Ѫ��������Ʒ�� CHARGE24:908.005ϸ����������Ʒ��
	// CHARGE28:910.001������CHARGE03:901.003�����
	// CHARGE26:909.002������һ����ҽ�ò��Ϸ�CHARGE25:909.001�����һ����ҽ�ò��Ϸ�CHARGE27:909.003
	// ������һ����ҽ�ò��Ϸ�
	// CHARGE17:906.001.002�ǿ���ҩ�����CHARGE16:906.001.001��ҩ��-����ҩ�����
	// CHARGE18:907.001�г�ҩ��
	// CHARGE19:907.002�в�ҩ�� CHARGE11:903.002.001�������Ʒ�-����� CHARGE13:903.002.003
	// �������Ʒ�-����
	private String sql = "SELECT A.MR_NO, A.CASE_NO, A.PAT_NAME,"
			+ "CASE WHEN A.SEX = '1' THEN '��' WHEN A.SEX = '2' THEN 'Ů' ELSE 'δ֪' END AS SEX,"
			+ "A.IDNO,D.CTZ_DESC,F.DEPT_CHN_DESC AS IN_DEPT_DESC,G.DEPT_CHN_DESC AS OUT_DEPT_DESC,"
			+ "TO_CHAR(A.IN_DATE,'YYYY-MM-DD') AS IN_DATE,TO_CHAR(A.OUT_DATE,'YYYY-MM-DD') AS OUT_DATE,CASE WHEN ROUND(TO_NUMBER(A.OUT_DATE-A.IN_DATE))<=0 THEN 1 ELSE ROUND(TO_NUMBER(A.OUT_DATE-A.IN_DATE)) END AS REAL_STAY_DAYS,"
			+ "TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS BIRTH_DATE,A.AGE,H.POST_DESCRIPTION, A.H_ADDRESS, A.CONT_TEL,"
			+ "I.USER_NAME AS DR_DESC, A.GET_TIMES, A.SUCCESS_TIMES,A.RBC, A.PLATE, A.PLASMA,"
			+ "A.WHOLE_BLOOD, A.OTH_BLOOD,CASE WHEN A.CODE1_STATUS='1' THEN '����' WHEN A.CODE1_STATUS='2' THEN '��ת' WHEN A.CODE1_STATUS='3' THEN 'δ��' "
			+ "WHEN A.CODE1_STATUS='4' THEN '����' WHEN A.CODE1_STATUS='5' THEN '����' ELSE '����' END  AS OUT_STATUS,"
			+ "A.INTE_DIAG_CODE ,T.ICD_CHN_DESC AS INTE_DIAG_DESC ,C.MAINDIAG,J.ICD_CHN_DESC AS MAIN_DESC, "
			+ "'' IN_DIAG_CODE1, '' IN_DIAG_DESC1 ,'' IN_DIAG_CODE2 ,'' IN_DIAG_DESC2,"
			+ "'' IN_DIAG_CODE3, '' IN_DIAG_DESC3 ,'' OE_DIAG_CODE1 ,'' OE_DIAG_DESC1,"
			+ "'' OE_DIAG_CODE2, '' OE_DIAG_DESC2 ,'' OE_DIAG_CODE3 ,'' OE_DIAG_DESC3,"
			+ "'' OUT_DIAG_CODE1,'' OUT_DIAG_DESC1,'' OUT_DIAG_CODE2,'' OUT_DIAG_DESC2,"
			+ "'' OUT_DIAG_CODE3,'' OUT_DIAG_DESC3,'' OUT_DIAG_CODE4,'' OUT_DIAG_DESC4,"
			+ "'' OP_CODE1 ,'' OP_DESC1,'' OP_DATE1,'' OP_USER_NAME1,'' OP_ANA_DESC1,"
			+ "'' OP_CODE2 ,'' OP_DESC2,'' OP_DATE2,'' OP_USER_NAME2,'' OP_ANA_DESC2,"
			+ "'' OP_CODE3 ,'' OP_DESC3,'' OP_DATE3,'' OP_USER_NAME3,'' OP_ANA_DESC3,"
			+ "'' OP_CODE4 ,'' OP_DESC4,'' OP_DATE4,'' OP_USER_NAME4,'' OP_ANA_DESC4,"
			+ "'' OP_CODE5 ,'' OP_DESC5,'' OP_DATE5,'' OP_USER_NAME5,'' OP_ANA_DESC5,"
			+ "'' OP_CODE6 ,'' OP_DESC6,'' OP_DATE6,'' OP_USER_NAME6,'' OP_ANA_DESC6,"
			+ "A.CHARGE_01 AS MEDICAL_AMT,A.CHARGE_02 AS TREAT_AMT,A.CHARGE_08 AS CLINICAL_DIAG_AMT,A.CHARGE_05 AS PATHOLOG_DIAG_AMT,"
			+ "A.CHARGE_07 AS IMAGE_DIAG_AMT, A.CHARGE_06 AS LABORATORY_DIAG_AMT,A.CHARGE_09 AS PAYSICAL_TY_AMT, "
			+ "A.CHARGE_10 AS NPAYSICAL_TY_AMT,A.CHARGE_12 AS SURGERY_AMT ,A.CHARGE_20 AS BLOOD_AMT,"
			+ "A.CHARGE_21 AS ALBUMINS_AMT, A.CHARGE_22 AS GLOBULIN_AMT,A.CHARGE_23 AS CLOT_FACTOR_AMT, A.CHARGE_24 AS CYTOK_AMT,"
			+ "A.CHARGE_28 AS OTHER_AMT,A.CHARGE_03 AS CARE_AMT,A.CHARGE_26 AS TREATMENT_AMT,A.CHARGE_25 AS CHECK_DISPOSABLE_AMT,"
			+ "A.CHARGE_27 AS DISPOSABLE_SURG_AMT, A.CHARGE_17 AS NON_ANTIM_AMT, A.CHARGE_16 AS ANTIBACTERIAL_DRUG_AMT,"
			+ "A.CHARGE_18 AS PROPRIETARY_CHIN_AMT, A.CHARGE_19 AS PHA_HERBAL_AMT, A.CHARGE_11 AS ANESTHESIA_AMT, "
			+ "A.CHARGE_13 AS SURGERY_OTHER_AMT,"
			+ "A.SUM_TOT AS SUM_AMT,"
			+ "(A.CHARGE_18+A.CHARGE_19+A.CHARGE_16+A.CHARGE_17)AS DRUG_AMT,(A.CHARGE_26+A.CHARGE_25+A.CHARGE_27) AS MATER_AMT"
			+ ",C.CLNCPATH_CODE,A.CHARGE_04,B.CTZ1_CODE,C.CTZ2_CODE"// wanglong add 20140717
			// add by wangb 2016/4/5
			// ������Ժ;���������������ͣ���������/������������������ʹ��ʱ�䡢31����סԺ�ƻ����໤�ҽ���ʱ�䡢�˳�ʱ�䡢���ȼ���������
			// START
			+ ",A.ADM_SOURCE,A.OPE_TYPE_CODE,NVL(A.VENTI_TIME,0) AS VENTI_TIME,A.AGN_PLAN_FLG,A.ICU_ROOM1,A.ICU_IN_DATE1,A.ICU_OUT_DATE1,A.ICU_ROOM2,A.ICU_IN_DATE2,A.ICU_OUT_DATE2"
			+ ",A.ICU_ROOM3,A.ICU_IN_DATE3,A.ICU_OUT_DATE3,A.ICU_ROOM4,A.ICU_IN_DATE4,A.ICU_OUT_DATE4,A.ICU_ROOM5,A.ICU_IN_DATE5,A.ICU_OUT_DATE5"
			+ ",(A.ICU_OUT_DATE1-A.ICU_IN_DATE1)*24 AS ICU_DATE_RANGE1,(A.ICU_OUT_DATE2-A.ICU_IN_DATE2)*24 AS ICU_DATE_RANGE2,(A.ICU_OUT_DATE3-A.ICU_IN_DATE3)*24 AS ICU_DATE_RANGE3"
			+ ",(A.ICU_OUT_DATE4-A.ICU_IN_DATE4)*24 AS ICU_DATE_RANGE4,(A.ICU_OUT_DATE5-A.ICU_IN_DATE5)*24 AS ICU_DATE_RANGE5"
			+ ",NVL(A.SPENURS_DAYS,0) AS SPENURS_DAYS,NVL(A.FIRNURS_DAYS,0) AS FIRNURS_DAYS,NVL(A.SECNURS_DAYS,0) AS SECNURS_DAYS,NVL(A.THRNURS_DAYS,0) AS THRNURS_DAYS,A.DISEASES_CODE"
			// add by wangb 2016/4/5
			// ������Ժ;���������������ͣ���������/������������������ʹ��ʱ�䡢31����סԺ�ƻ����໤�ҽ���ʱ�䡢�˳�ʱ�䡢���ȼ���������
			// END
			+ " FROM MRO_RECORD A,SYS_PATINFO B,ADM_INP C,SYS_CTZ D,SYS_DEPT F,SYS_DEPT G,SYS_POSTCODE H,SYS_OPERATOR I"
			+ ",SYS_DIAGNOSIS J,SYS_DIAGNOSIS T"
			+ " WHERE A.CASE_NO=C.CASE_NO "
			+ " AND A.MR_NO=B.MR_NO AND C.CTZ1_CODE=D.CTZ_CODE(+)"
			+ " AND A.IN_DEPT=F.DEPT_CODE(+) AND A.H_POSTNO=H.POST_CODE(+)"
			+ " AND A.OUT_DEPT=G.DEPT_CODE(+)"
			+ " AND A.VS_DR_CODE=I.USER_ID(+)"
			+ " AND C.MAINDIAG=J.ICD_CODE(+)"
			+ " AND A.INTE_DIAG_CODE=T.ICD_CODE(+)";

	// ������,100; �����,100; ��������,70;�Ա�,30;���֤����,130;���,70;��Ժ���� ,
	// 100;��Ժ����,100;��Ժʱ��,100; ��Ժʱ��,100;סԺ����,100;��������,100;
	// ����,100;����/������,100;����סַ,100;�绰����,100;����ҽ��,100;���ȴ���,40;
	// �ɹ�����,40;��Ѫ��,80;ѪС��,80; Ѫ��,80;ȫѪ,80;����ѪƷ����,100;��Ժ���,100;����ϴ���,100;
	// ���������,100; ��Ժ��ϴ���1,100;��Ժ�������1,100;��Ժ��ϴ���2,100;��Ժ�������2 ,100;��Ժ��ϴ���3,100;
	// ��Ժ�������3,100;�ż�����ϴ���1,100;�ż����������1 ,100;�ż�����ϴ���2,100;�ż����������2,100;
	// �ż�����ϴ���3,100;�ż����������3 ,100;��Ժ��ϴ���1,100;
	// ��Ժ�������1,100; ��Ժ��ϴ���2,100;��Ժ�������2,100;��Ժ��ϴ���3,100;��Ժ�������3 ,100;
	// ��Ժ��ϴ���4,100;��Ժ�������4,100;
	// ��������1,100;��������1,100;����ʱ��1,100;������Ա1,100;����ʽ1,100;
	// ��������2,100;��������2,100;����ʱ��2,100;������Ա2,100;����ʽ2,100; ��������3,100;��������3,100;
	// ����ʱ��3,100;������Ա3,100;����ʽ3,100;
	// ��������4,100;��������4,100;����ʱ��4,100;������Ա4,100;����ʽ4,100;
	// �ܷ���,100;ҩƷ��,100;���Ϸ�,100
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("�������Ժ����");
			return;
		}
		StringBuffer bf = new StringBuffer();
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			bf.append(" AND A.REGION_CODE ='")
					.append(this.getValue("REGION_CODE").toString())
					.append("'");
		}
		bf.append(" AND A.OUT_DATE BETWEEN TO_DATE('")
				.append(SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_START"), true))
				.append("','YYYYMMDDHH24MISS') AND TO_DATE('")
				.append(SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_END").substring(0, 10),
						false)).append("','YYYYMMDDHH24MISS')");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql + bf));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			return;
		}

		// �����ֵ���Ϣ
		TParm sysDeptResult = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_DEPT WHERE ICU_TYPE IS NOT NULL"));
		Map<String, String> deptMap = new HashMap<String, String>();
		for (int m = 0; m < sysDeptResult.getCount(); m++) {
			deptMap.put(sysDeptResult.getValue("DEPT_CODE", m),
					sysDeptResult.getValue("DEPT_CHN_DESC", m));
		}

		String icuInfo = "";
		String icuRoom = "";
		String icuInDate = "";
		String icuOutDate = "";
		// CCU�໤�ۼ�ʱ��
		double ccuTotalTime = 0;
		// ICU�໤�ۼ�ʱ��
		double icuTotalTime = 0;
		DecimalFormat df = new DecimalFormat("#.00");

		for (int i = 0; i < result.getCount(); i++) {
			StringBuffer tempSql = new StringBuffer();
			tempSql.append(
					"SELECT A.CASE_NO,A.OP_CODE,A.OP_DESC,TO_CHAR(A.OP_DATE,'YYYY-MM-DD') AS OP_DATE,B.CHN_DESC,C.USER_NAME,A.MAIN_FLG FROM MRO_RECORD_OP A,"
							+ "SYS_DICTIONARY B,SYS_OPERATOR C WHERE A.MAIN_SUGEON=C.USER_ID AND TRIM(A.ANA_WAY)=TRIM(B.ID(+)) "
							+ " AND B.GROUP_ID='OPE_ANAMETHOD'"
							+ " AND A.CASE_NO='")
					.append(result.getValue("CASE_NO", i)).append("'");
			TParm tempParm = getResultParm(tempSql.toString());
			if (tempParm.getErrCode() < 0) {
				this.messageBox("��ѯʧ��");
				return;
			} else {
				int index = 1;
				for (int j = 0; j < tempParm.getCount(); j++) {
					if (null != tempParm.getValue("MAIN_FLG", j)
							&& tempParm.getValue("MAIN_FLG", j).equals("Y")) {// ������
						result.setData("OP_CODE1", i,
								tempParm.getValue("OP_CODE", j));
						result.setData("OP_DESC1", i,
								tempParm.getValue("OP_DESC", j));
						result.setData("OP_USER_NAME1", i,
								tempParm.getValue("USER_NAME", j));
						result.setData("OP_DATE1", i,
								tempParm.getValue("OP_DATE", j));
						result.setData("OP_ANA_DESC1", i,
								tempParm.getValue("CHN_DESC", j));
						continue;
					}
					index++;
					result.setData("OP_CODE" + index, i,
							tempParm.getValue("OP_CODE", j));
					result.setData("OP_DESC" + index, i,
							tempParm.getValue("OP_DESC", j));
					result.setData("OP_USER_NAME" + index, i,
							tempParm.getValue("USER_NAME", j));
					result.setData("OP_DATE" + index, i,
							tempParm.getValue("OP_DATE", j));
					result.setData("OP_ANA_DESC" + index, i,
							tempParm.getValue("CHN_DESC", j));
				}
			}
			// ��Ժ��ϳ�Ժ�������
			tempSql = new StringBuffer();
			tempSql.append(
					"SELECT IO_TYPE,ICD_CODE,ICD_DESC,MAIN_FLG FROM MRO_RECORD_DIAG WHERE CASE_NO ='")
					.append(result.getValue("CASE_NO", i))
					.append("' ORDER BY IO_TYPE,MAIN_FLG");
			tempParm = getResultParm(tempSql.toString());
			if (tempParm.getErrCode() < 0) {
				this.messageBox("��ѯʧ��");
				return;
			} else {
				int indexM = 1;// ��Ժ
				int indexO = 1;// ��Ժ
				int indexI = 1;// �ż���
				for (int j = 0; j < tempParm.getCount(); j++) {
					// �����
					if (null != tempParm.getValue("MAIN_FLG", j)
							&& tempParm.getValue("MAIN_FLG", j).equals("Y")) {
						// IO_TYPE =M ��Ժ O ��Ժ I �ż���
						if (tempParm.getValue("IO_TYPE", j).equals("M")) {
							result.setData("IN_DIAG_CODE1", i,
									tempParm.getValue("ICD_CODE", j));
							result.setData("IN_DIAG_DESC1", i,
									tempParm.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("O")) {
							result.setData("OUT_DIAG_CODE1", i,
									tempParm.getValue("ICD_CODE", j));
							result.setData("OUT_DIAG_DESC1", i,
									tempParm.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("I")) {
							result.setData("OE_DIAG_CODE1", i,
									tempParm.getValue("ICD_CODE", j));
							result.setData("OE_DIAG_DESC1", i,
									tempParm.getValue("ICD_DESC", j));
						}
					} else {
						// �����
						if (tempParm.getValue("IO_TYPE", j).equals("M")) {
							indexM++;
							result.setData("IN_DIAG_CODE" + indexM, i,
									tempParm.getValue("ICD_CODE", j));
							result.setData("IN_DIAG_DESC" + indexM, i,
									tempParm.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("O")) {
							indexO++;
							result.setData("OUT_DIAG_CODE" + indexO, i,
									tempParm.getValue("ICD_CODE", j));
							result.setData("OUT_DIAG_DESC" + indexO, i,
									tempParm.getValue("ICD_DESC", j));
						} else if (tempParm.getValue("IO_TYPE", j).equals("I")) {
							indexI++;
							result.setData("OE_DIAG_CODE" + indexI, i,
									tempParm.getValue("ICD_CODE", j));
							result.setData("OE_DIAG_DESC" + indexI, i,
									tempParm.getValue("ICD_DESC", j));
						}
					}
				}
			}

			// add by wangb 2016/4/5
			// ������Ժ;���������������ͣ���������/������������������ʹ��ʱ�䡢31����סԺ�ƻ����໤�ҽ���ʱ�䡢�˳�ʱ�䡢���ȼ���������
			// START
			if (result.getValue("OPE_TYPE_CODE", i).equals("1")) {
				result.addData("OPE_TYPE_DESC", "��������");
			} else if (result.getValue("OPE_TYPE_CODE", i).equals("2")) {
				result.addData("OPE_TYPE_DESC", "��������");
			} else {
				result.addData("OPE_TYPE_DESC", "");
			}

			if (result.getValue("AGN_PLAN_FLG", i).equals("Y")) {
				result.addData("AGN_PLAN_DESC", "��");
			} else {
				result.addData("AGN_PLAN_DESC", "��");
			}

			// �����໤�Ҽ໤��Ϣ
			icuInfo = "";
			ccuTotalTime = 0;
			icuTotalTime = 0;
			for (int k = 1; k <= 5; k++) {
				icuRoom = result.getValue("ICU_ROOM" + k, i);
				icuInDate = result.getValue("ICU_IN_DATE" + k, i);
				icuOutDate = result.getValue("ICU_OUT_DATE" + k, i);
				if (StringUtils.isNotEmpty(icuRoom)
						&& StringUtils.isNotEmpty(icuInDate)
						&& StringUtils.isNotEmpty(icuOutDate)) {
					icuInfo = icuInfo + deptMap.get(icuRoom) + ":"
							+ icuInDate.substring(0, 19) + "��"
							+ icuOutDate.substring(0, 19);
					icuInfo = icuInfo + ";";

					// ����໤�ۼ�ʱ��
					if ("CCU".equals(deptMap.get(icuRoom))) {
						ccuTotalTime = ccuTotalTime
								+ result.getDouble("ICU_DATE_RANGE" + k, i);
					} else if ("ICU".equals(deptMap.get(icuRoom))) {
						icuTotalTime = icuTotalTime
								+ result.getDouble("ICU_DATE_RANGE" + k, i);
					}
				}
			}
			if (icuInfo.length() > 0) {
				icuInfo = icuInfo.substring(0, icuInfo.length() - 1);
			}
			result.addData("ICU_INFO", icuInfo);

			result.addData("CCU_TOTAL_TIME",
					ccuTotalTime > 0 ? df.format(ccuTotalTime) : 0);
			result.addData("ICU_TOTAL_TIME",
					icuTotalTime > 0 ? df.format(icuTotalTime) : 0);
			// add by wangb 2016/4/5
			// ������Ժ;���������������ͣ���������/������������������ʹ��ʱ�䡢31����סԺ�ƻ����໤�ҽ���ʱ�䡢�˳�ʱ�䡢���ȼ���������
			// END
            System.out.println("================"+result.getValue("DISEASES_CODE", i));
			result.setData("DISEASES_CODE", i,result.getValue("DISEASES_CODE", i).equals("")?"":disCodeToDescMap.get(result.getValue("DISEASES_CODE", i)));
		}
		((TTextField) this.getComponent("COUNT")).setValue(result.getCount()
				+ "");// add by wanglong 20121221
		System.out.println("sql:"+sql);
		table.setParmValue(result);
	}

	/**
	 * ִ��SQL
	 * 
	 * @param tempSql
	 * @return
	 */
	private TParm getResultParm(String tempSql) {
		TParm tempParm = new TParm(TJDODBTool.getInstance().select(tempSql));
		if (tempParm.getErrCode() < 0) {
			return tempParm;
		}
		return tempParm;
	}

	/**
	 * ���Excel
	 */
	public void onExport() {// modify by wanglong 20121226
		TParm parmValue = table.getParmValue();
		TParm parm = table.getShowParmValue();
		TParm temp = cloneParm(parm);
		for (int i = 0; i < temp.getCount("MR_NO"); i++) {
			temp.setData("CLNCPATH_CODE", i,
					parmValue.getData("CLNCPATH_CODE", i));// �ٴ�·�� wanglong add
															// 20140717
			// ====================תint��
			temp.setData("GET_TIMES", i, temp.getInt("GET_TIMES", i));// ���ȴ���
			temp.setData("SUCCESS_TIMES", i, temp.getInt("SUCCESS_TIMES", i));// �ɹ�����
			temp.setData("RBC", i, temp.getInt("RBC", i));// ��Ѫ��
			temp.setData("PLATE", i, temp.getInt("PLATE", i));// ѪС��
			temp.setData("PLASMA", i, temp.getInt("PLASMA", i));// Ѫ��
			temp.setData("WHOLE_BLOOD", i, temp.getInt("WHOLE_BLOOD", i));// ȫѪ
			temp.setData("OTH_BLOOD", i, temp.getInt("OTH_BLOOD", i));// ����ѪƷ����
			if ((temp.getData("AGE", i) == null)
					|| temp.getData("AGE", i).equals("")
					|| temp.getData("AGE", i).toString().matches(".*��.*"))
				continue;
			temp.setData(
					"AGE",
					i,
					Integer.parseInt(temp.getData("AGE", i).toString()
							.replaceAll("[^0-9]", "")));// �������ֶ�ȥ�����ꡱ�֣�����Ϊint���͡�
		}

		table.setParmValue(temp);
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table,
					StringTool.getString(
							(Timestamp) this.getValue("OUT_DATE_START"),
							"yyyy.MM") + "���߻�������");
		table.setParmValue(parmValue);
	}

	/**
	 * ��� add by wanglong 20121221
	 */
	public void onClear() {
		this.clearValue("COUNT");
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		table.setParmValue(new TParm());
	}

	/**
	 * ��¡TParm
	 * 
	 * @param srcParm
	 * @return
	 */
	private TParm cloneParm(TParm srcParm) {// add by wanglong 20121221
		TParm copyParm = new TParm();
		String[] names = srcParm.getNames();
		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < srcParm.getCount(); j++) {
				copyParm.addData(names[i], srcParm.getData(names[i], j));
			}
		}
		copyParm.setCount(copyParm.getCount(names[0]));
		return copyParm;
	}

	/**
	 * 
	 * @return
	 */
	private void initDisDicMap() {
		disDicMap = new HashMap<String, String>();
		disCodeToDescMap= new HashMap<String, String>();
		TParm result = new TParm(TJDODBTool.getInstance().select(disdicsql));
		for (int i = 0; i < result.getCount(); i++) {
			TParm parm = result.getRow(i);
			disDicMap.put(parm.getValue("DISEASE_DESC"),parm.getValue("DISEASE_CODE"));
			disCodeToDescMap.put(parm.getValue("DISEASE_CODE"),parm.getValue("DISEASE_DESC"));
		}
		System.out.println("=====disCodeToDescMap======="+disCodeToDescMap);
	}

	/**
     * 
     */
	public void onDisImportExcel() {
		String filePath = this.openFilePath();
		JXLReadExcelUtil excelReader = new JXLReadExcelUtil(filePath);
		try {
			Map<Integer, Map<String, String>> map = excelReader
					.readDefaultExcelContent(disFields);
			String[] sql = new String[map.size()];
			int i = 0;
			for (Integer key : map.keySet()) {
				Map<String, String> mapV = map.get(key);
				StringBuffer sb = new StringBuffer("UPDATE MRO_RECORD ");
				if (disDicMap.get(mapV.get("DISEASE_CODE")) == null) {
					this.messageBox("EXCEL�в���(" + mapV.get("DISEASE_CODE")
							+ ")��HIS��û�ж���");
					return;
				}
				String EXCEPTIONAL = mapV.get("SPECIAL_CASE");
				sb.append(" SET DISEASES_CODE='"
						+ disDicMap.get(mapV.get("DISEASE_CODE"))
						+ "'");
				sb.append(" WHERE CASE_NO='" + mapV.get("CASE_NO") + "' ");
				sql[i] = sb.toString();
				i++;
			}
			TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
			if (parm.getErrCode() < 0) {
				this.messageBox("�������ݴ���");
				return;
			}
			this.messageBox("��������" + i + "��");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String openFilePath() {
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".xls");
			}

			public String getDescription() {
				return "Excel File";
			}
		});
		jfc.showOpenDialog(null);
		String resultOpen = jfc.getSelectedFile().getPath();
		return resultOpen;
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";

				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

}
