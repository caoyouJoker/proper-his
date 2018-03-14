package com.javahis.ui.mro;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.bms.BMSTool;
import jdo.emr.GetWordValue;
import jdo.mro.MROPrintTool;
import jdo.mro.MRORecordTool;
import jdo.mro.MROSqlTool;
import jdo.mro.MROTool;
import jdo.ope.OPETool;
import jdo.sta.STAIdcardValidator;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFormatEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ������ҳ
 * </p>
 * 
 * <p>
 * Description: ������ҳ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008 
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author ZhangK 2009-3-24
 * @version 4.0
 */
public class MRORecordControl extends TControl {

	private String REGION_CODE = Operator.getRegion(); // Ժ��
	private String MR_NO;// ����������
	private String CASE_NO;// �����������
	// String SYSTEM_CODE = "MRO";//��¼�ĸ�ϵͳ���ô˽��� ��ֵΪ��MRO��ʱ �ý��洦�ڱ༭״̬
	private Map map; // �վݷ����ֵ�
	private TTable DiagGrid;// ���Table
	private boolean opeSaveFlg = false;
	/**
	 * Ȩ��˵�� 1.סԺ�� 2.ҽʦ 3.���� 4.������(ȫ��Ȩ��) ��������Ȩ�� ҽʦȨ��ֻ�о���ҽʦ�����޸��Լ��Ĳ���
	 */
	private String UserType = "";
	private String OpenUser = "";// ��¼����ҽʦ����
	private String VS_DR = "";// ��¼�����ľ���ҽʦ
	private String ADMCHK_FLG = "";// סԺ���ύ
	
	private String DIAGCHK_FLG = "";// ҽʦ�ύ
	private String BILCHK_FLG = "";// �����ύ
	private String QTYCHK_FLG = "";// �ʿ��ύ
	private String OPT_USER = "";// ������Ա
	private String OPT_TERM = "";// ����IP
	private Timestamp dsDate;// ��Ժʱ��
	TWord word;
	
	
	
	// modified by WangQing 20170411 -start
	// ������ֶΣ�����ְҵ����ϵ����Ϣ��ʾ����
	String oldOccCode = "";
	String oldRelationCode = "";
	// modified by WangQing 20170411 -end
	
	
	
	/*
	 * �����о�����ʹ��sql���
	 */
	private String QUERY_EMR_SQL = "SELECT # FROM EMR_FILE_INDEX WHERE CASE_NO = '#'";
	
	//����Ҫ��ӽ�������¼����������
	private String[] cmpNames = {"TP23_RBC","TP23_PLATE","TP23_PLASMA","TP23_WHOLE_BLOOD","TP23_BANKED_BLOOD","TP23_OTH_BLOOD"};
	private boolean isFirst = true;
	
	public void onInit() {
		super.onInit();
		DiagGrid = (TTable) this.getComponent("TP21_Table2");
		OPT_USER = Operator.getID();
		OPT_TERM = Operator.getIP();
		// ģ�⴫����
		// TParm parm = new TParm();
		// parm.setData("MR_NO","000000000088");
		// parm.setData("CASE_NO","090217000005");
		// ��ȡ�������
		// Object obj = parm;
		Object obj = this.getParameter();
		if (obj != null) {
			TParm parmData = (TParm) obj;
			MR_NO = parmData.getValue("MR_NO");// ��ȡ������
			CASE_NO = parmData.getValue("CASE_NO");// ��ȡ�������
			UserType = parmData.getValue("USER_TYPE");// ����Ȩ��
			OpenUser = parmData.getValue("OPEN_USER");// ������Ա ����
			if (MR_NO.trim().length() > 0 && CASE_NO.trim().length() > 0) {
				patInHospInfo();// ��ѯ��ĳһ����סԺ��Ϣ
				// ����ҳǩѡ��״̬
				TTabbedPane tP = (TTabbedPane) this.getComponent("tTabbedPane_0");
				TTabbedPane tP2 = (TTabbedPane) this.getComponent("tTabbedPane_1");
				if (UserType.equals("1")) {
					tP.setSelectedIndex(0);// ѡ�е�һҳǩ
					this.setEnabledPage2(false);
					this.setEnabledPage3(false);
					this.setEnabledPage4(false);
					// this.callFunction("UI|tTabbedPane_0|setEnabled", false);
				} else if (UserType.equals("2")) {
					tP.setSelectedIndex(1);// ѡ�еڶ�ҳǩ
					// this.callFunction("UI|tTabbedPane_0|setEnabled", false);
					this.setEnabledPage1(false);
					this.setEnabledPage3(false);
					this.setEnabledPage4(false);
				} else if (UserType.equals("3")) {
					tP.setSelectedIndex(2);// ѡ�е���ҳǩ
					// this.callFunction("UI|tTabbedPane_0|setEnabled", false);
					this.setEnabledPage1(false);
					this.setEnabledPage2(false);
					this.setEnabledPage4(false);
				} else if (UserType.equals("4")) {
					tP.setSelectedIndex(0);// ѡ�е�һҳǩ
					// this.callFunction("UI|tTabbedPane_0|setEnabled", true);
				} else if (UserType.equals("2-4")) {
					tP.setSelectedIndex(1);
					tP2.setSelectedIndex(3);
					setEnagledPage2_2(true);
					callFunction("UI|save|setEnabled", true);
				}
				if(onCheckPatlog()){
					Color color=new Color(255,0,0);
					tP.setTabColor(0, color);
				}
                if (TCM_Transform.getTimestamp(this.getValue("TP2_OUT_DATE")) != null) {
                    onIntoICUData();// wanglong add 20150226
//                    onIntoBMSData();
                }
                
                // add by wangb 2017/06/27 ��ʼ��ʱ�Զ�������Ѫ���Ϻ͹�����¼
                onIntoBMSData();
                onIntoAllecData();
			} else {
				return;
			}
		}
		// tableר�õļ��� ICD10
		DiagGrid.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// ģ����ѯ(�����ڲ���)
		OrderList orderDesc = new OrderList();
		DiagGrid.addItem("OrderList", orderDesc);
		TTable OP_Table = (TTable) this.getComponent("TP23_OP_Table");
		// ����Table ����
		OP_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponentOP");
//		 //TP23_OP_Tableֵ�ı��¼�
		this.addEventListener("TP23_OP_Table->" + TTableEvent.CHANGE_VALUE,
		 		"onChangeValueOP");
		// �������ı��¼�
		OP_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onOpTableMainCharge");
		// ���Gridֵ�ı��¼�
		this.addEventListener("TP21_Table2->" + TTableEvent.CHANGE_VALUE,
				"onDiagTableValueCharge");
		// ������ϼ���
		TParm popParm = new TParm();
		popParm.setData("ICD_TYPE", "W");
		popParm.setData("ICD_MIN", "M80000/0");
		popParm.setData("ICD_MAX", "M99890/1");
		callFunction("UI|TP22_PATHOLOGY_DIAG|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm);
		// textfield���ܻش�ֵ
		callFunction("UI|TP22_PATHOLOGY_DIAG|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "pathology_Return");
		
		callFunction("UI|TP22_PATHOLOGY_DIAG2|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm);
		// textfield���ܻش�ֵ
		callFunction("UI|TP22_PATHOLOGY_DIAG2|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "pathology_Return2");
		
		callFunction("UI|TP22_PATHOLOGY_DIAG3|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm);
		// textfield���ܻش�ֵ
		callFunction("UI|TP22_PATHOLOGY_DIAG3|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "pathology_Return3");
		// �����ж���ϼ���
		TParm popParm2 = new TParm();
		popParm2.setData("ICD_TYPE", "W");
		popParm2.setData("ICD_START", "V");
		popParm2.setData("ICD_END", "Y");
		callFunction("UI|TP22_EX_RSN|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm2);
		// textfield���ܻش�ֵ
		callFunction("UI|TP22_EX_RSN|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ex_Return");
		callFunction("UI|TP22_EX_RSN2|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm2);
		// textfield���ܻش�ֵ
		callFunction("UI|TP22_EX_RSN2|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ex_Return2");
		callFunction("UI|TP22_EX_RSN3|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x", popParm2);
		// textfield���ܻش�ֵ
		callFunction("UI|TP22_EX_RSN3|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "ex_Return3");
		callFunction("UI|TP2_TRAN_HOSP|addEventListener",
				TTextFormatEvent.SELECTED, this, "onSelectTran");
		
		word = (TWord) this.getComponent("WORD");
		setSXFYByBMSData(cmpNames);
		setCmpFocusListener(cmpNames);
		
		// modified by WangQing 20170412
		onFinance(); // �������
	}


	/**
	 * ��ϵ������� ICD10
	 * 
	 * @param com
	 *            Component �ؼ�
	 * @param row
	 *            int ����
	 * @param column
	 *            int ����
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ����ICD10�Ի������
		if (column != 4 && column != 7)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", "W");
		parm.setData("ICD_EXCLUDE","Y");
		parm.setData("ICD_MIN_EX", "M80000/0");
		parm.setData("ICD_MAX_EX", "M99890/1");
		parm.setData("ICD_START_EX", "V");
		parm.setData("ICD_END_EX", "Y");
		// ��table�ϵ���text����ICD10��������
		textfield.setPopupMenuParameter("ICD10", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"),parm);
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		if (column == 4)
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"newAgentOrder");
		else if (column == 7)
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"newAdditionalOrder");
	}

	/**
	 * ȡ��ICD10����ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newAgentOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		Map map = new HashMap();
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		for (int i = 0; i < tableParm.getCount(); i++) {
			String admType = (String) table.getValueAt(i, 0);
			String code = (String) table.getValueAt(i, 4);
			if (i == table.getSelectedRow())
				continue;
			if (!code.equals("") && map.get(admType + code) == null)
				map.put(admType + code, admType + code);
		}
		String errStr = "";
		if (map.get((String) table.getValueAt(table.getSelectedRow(), 0)
				+ orderCode) != null) {
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("I"))
				errStr = "�ż������:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("M"))
				errStr = "��Ժ���:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("O"))
				errStr = "��Ժ���:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("Q"))
				errStr = "��Ⱦ���:";
			if (tableParm.getValue("TYPE", table.getSelectedRow()).equals("W"))
				errStr = "�������:";
			this.messageBox(errStr + parm.getValue("ICD_CHN_DESC") + "�ѿ���");
			return;
		}
		table.setItem(table.getSelectedRow(), "CODE", orderCode);
		table.setItem(table.getSelectedRow(), "NAME", parm
				.getValue("ICD_CHN_DESC"));
		table
				.setItem(table.getSelectedRow(), "KIND", parm
						.getValue("ICD_TYPE"));
	}

	/**
	 * ȡ��ICD10����ֵ(������)
	 * 
	 * @param tag
	 *            Stringng
	 * @param obj
	 *            Object
	 */
	public void newAdditionalOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		table.setItem(table.getSelectedRow(), "ADDITIONAL", orderCode);
		table.setItem(table.getSelectedRow(), "ADDITIONAL_DESC", parm
				.getValue("ICD_CHN_DESC"));
	}

	/**
	 * ������������ OpICD
	 * 
	 * @param com
	 *            Component �ؼ�
	 * @param row
	 *            int ����
	 * @param column
	 *            int ����
	 */
	public void onCreateEditComponentOP(Component com, int row, int column) {
		// ����ICD10�Ի������
		if (column != 3)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����ICD10��������
		textfield.setPopupMenuParameter("OPICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSOpICD.x"));
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOPOrder");
	}
	
	/**
	 * �ж���ѡ����ҽʦ�Ƿ�Ϊ��Ժҽʦ
	 * @param obj �����ֵ�ı�Ľڵ����
	 */
    public boolean onChangeValueOP(Object obj){
        TTable table = (TTable) this.getComponent("TP23_OP_Table");
        table.acceptText();
        TTableNode node= (TTableNode) obj;
        if ("OP_DATE".equals(table.getDataStoreColumnName(table.getSelectedColumn()))) {//add by wanglong 20140314
            Timestamp outDate = (Timestamp) this.getValue("TP2_OUT_DATE");
            Timestamp opDate = (Timestamp) node.getValue();
            Timestamp indate = StringTool.rollDate((Timestamp) getValue("TP2_IN_DATE"),-1);
			if (indate.getTime() > opDate.getTime()) {
				this.messageBox_("��Ժ����-1�첻�ܴ�����������");
				return false;
			}
            if (!(outDate == null || outDate.equals(new TNull(Timestamp.class)))) {
                if (opDate.getTime() > outDate.getTime()) {
                    this.messageBox_("�������ڲ��ܴ��ڳ�Ժ����");
                    return true;
                }
            }
        }
        if(!"MAIN_SUGEON".equals(table.getDataStoreColumnName(
                table.getSelectedColumn())))
            return false;
        String docId = node.getValue().toString();
        TParm result = MRORecordTool.getInstance().isOutDoc(docId);
        if(result.getCount() <= 0){
            table.setLockCell(table.getSelectedRow(), 10, true);
            return false;
        }
        if(result.getData("IS_OUT_FLG" , 0) != null && 
                "Y".equals(result.getData("IS_OUT_FLG" , 0).toString())){
            table.setLockCell(table.getSelectedRow(), 10, false);
            return false;
        }
        table.setValueAt("", table.getSelectedRow(), 10);
        table.setLockCell(table.getSelectedRow(), 10, true);
        return false;
    }
	
	/**
	 * ȡ������ICD����ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOPOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|TP23_OP_Table|getThis");
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("OPERATION_ICD");
		table.setItem(table.getSelectedRow(), "OP_CODE", orderCode);
		table.setItem(table.getSelectedRow(), "OP_DESC", parm
				.getValue("OPT_CHN_DESC"));
		table.setItem(table.getSelectedRow(), "OP_LEVEL", parm
				.getValue("OPE_LEVEL"));
	}

	/**
	 * ����Grid ����ϱ���޸��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onOpTableMainCharge(Object obj) {
		TTable OP_Table = (TTable) this.getComponent("TP23_OP_Table");
		OP_Table.acceptText();
		if (OP_Table.getSelectedColumn() == 0) {
			int row = OP_Table.getSelectedRow();
			for (int i = 0; i < OP_Table.getRowCount(); i++) {
				OP_Table.setItem(i, "MAIN_FLG", "N");
			}
			OP_Table.setItem(row, "MAIN_FLG", "Y");
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		// ���CASE_NO�����ڲ��ܱ���
		if (CASE_NO.length() <= 0) {
			return;
		}
		// �������ݼ��
		if (!checkSaveData()) {
			return;
		}
		// �ж����Grid�Ƿ���ϱ�׼
		String message = checkDiagGrid();
		if (message.length() > 0) {
			this.messageBox_(message);
			return;
		}
		// �ж���ҳ��Ժ����Ϻ��ٴ���ϳ�Ժ������Ƿ�һ�� duzhw modify 20131211 /ȥ��У��duzhw del 20140421
//		if(!checkOMainDiag()) {
//			this.messageBox("��Ժ����Ϻ��ٴ���ϳ�Ժ����ϲ�һ�£�");
//			return;
//		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("Page1", this.getFisrtPageInfo().getData());// ��һҳǩ����
		if (!onChoiceDSDate()) {// add by wanglong 20120921 סԺ�������Ժ���ڸ��Ķ�����
			return;
		}
		parm.setData("Page2", this.getSecendPageInfo().getData()); // �ڶ�ҳǩ����
		parm.setData("Page4", this.getFourPageInfo().getData());// ����ҳǩ����
//		parm.setData("PageOP", this.getOPSQL());// ��������Ϣ
		parm.setData("PageOP", this.insertMRO_Record_OP().getData());// ��������Ϣ
		// parm.setData("PageICD", this.getDiagGridData().getData());//
		// ��ȡ���Grid�����ݽ��б��� 20120623 shibl modify
		parm.setData("PageICD", this.getNewDiagGridData().getData());//��ȡ���Grid�����ݽ��б���
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MRORecordAction", "updateDate", parm);
		//add by guoy 20150806
		// modify by wangb 2017/03/14 Ӧ����ϵͳҪ���ѳ�Ժ������������ӿڷ�����Ϣ
//		TParm xmlParm = ADMXMLTool.getInstance().creatADMXMLFile(CASE_NO, "A03");
		if (result.getErrCode() < -1) {
			this.messageBox(result.getErrText());
			return;
		}
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
//		if (result.getErrCode() < 0) {
//			this.messageBox("����������¼ʧ��" + result.getErrCode() + result.getErrText() + result.getErrName());
//			return;
//		}
		if (this.getValueString("ADMCHK_FLG").equals("Y"))
			this.setEnabledPage1(false);
		else
			this.setEnabledPage1(true);
		if (this.getValueString("DIAGCHK_FLG").equals("Y"))
			this.setEnabledPage2(false);
		else
			this.setEnabledPage2(true);
		if (this.getValueString("BILCHK_FLG").equals("Y"))
			this.setEnabledPage3(false);
		else
			this.setEnabledPage3(true);
		if (this.getValueString("QTYCHK_FLG").equals("Y"))
			this.setEnabledPage4(false);
		else
			this.setEnabledPage4(true);
		this.messageBox("P0005");
		this.OPTableBind(MR_NO, CASE_NO);
		// shibl add 20120726 �������ҳǩû�б��л�
		onFinance();
		patInHospInfo();//duzhw
	}

	/**
	 * ���벡����Ϣ
	 */
	public void onInto() {
		// zhangyong20110405 ת�벡����Ϣ
		intoData(1); // �����ⲿ����ת�뵽������ҳ��
	}

	// /**
	// * ����ҽ����Ϣ
	// */
	// public void onIntoDr() {
	// // zhangyong20110405 ת��ҽ����Ϣ
	// intoData(2); // �����ⲿ����ת�뵽������ҳ��
	// }

	/**
	 * �������
	 */
	public void onFinance() {
		if (CASE_NO.trim().length() <= 0) {
			return;
		}
		// ��ѯ����ϵͳ�з��û��ܣ����浽��ҳ����
		TParm result = MRORecordTool.getInstance().updateMROIbsForIBS(CASE_NO);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm mro_info = MRORecordTool.getInstance().getInHospInfo(parm);
		this.clearTP3();
		this.setValueTP3(mro_info);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearTP1();
		this.clearTP2();
		this.clearTP3();
		this.clearTP4();
		// ���Grid
		TTable chgTable = (TTable) this.getComponent("TP21_Table1");
		chgTable.removeRowAll();// ��ն�̬��
		TTable dailyTable = (TTable) this.getComponent("TP21_Table2");
		dailyTable.removeRowAll();// �����ϱ�
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		opTable.removeRowAll();// ���������
		opTable.resetModify();
	}

	/**
	 * ��ѯ��ĳһ�λ���סԺ��Ϣ
	 */
	public void patInHospInfo() {
		if (map == null) {
			map = MRORecordTool.getInstance().getChargeName(); // ��ȡÿ���շѵ���������
			if (map == null) {
				messageBox_("��δ�趨�վݷ����ֵ�!");
				return;
			}
		}
		if (MR_NO == null || MR_NO.length() == 0) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		// ��ѯ����סԺ��Ϣ ADM_INP
		TParm adm_inp = ADMTool.getInstance().getADM_INFO(parm);
		VS_DR = adm_inp.getValue("VS_DR_CODE", 0);// ��ȡ�����ľ���ҽʦ �����ж�Ȩ��
		// ��ѯ����ĳһ�ε�סԺ��Ϣ
		TParm mro_data = MRORecordTool.getInstance().getMRO_RecordInfo(parm);
		if (mro_data.getErrCode() < 0) {
			this.messageBox_("û�в�ѯ���û���סԺ��Ϣ��");
			return;
		}
		TParm result = mro_data.getParm("RECORD");// ��ҳ��Ϣ
		//	result.setData("DAY_OPE_CODE", 0, adm_inp.getValue("DAY_OPE_CODE", 0)); //ȡadm_inp���е��ռ������ֶ�ֵ
		//	this.messageBox("�Ƿ���ֵ:"+result.getValue("DAY_OPE_FLG"));
		
		TParm admtran = mro_data.getParm("ADMTRAN");// ������̬��Ϣ
		this.clearTP1();// ���ԭ��ֵ
		this.clearTP2();
		this.clearTP3();
		this.clearTP4();
		/*---------------------��ֵ ---------------------------*/
		this.setValueTP1(result);// ��һҳǩ
		this.setValueTP2(result);// �ڶ�ҳǩ
		this.setValueTP3(result);// ����ҳǩ
		this.setValueTP4(result);// ����ҳǩ
		this.setValueTran(admtran);// ����ת��Grid������

	}

	/**
	 * ��һҳǩ��ֵ
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP1(TParm result) {
		/*-------------------��һҶǩ start------------------------------*/
		this.setValue("TP1_MR_NO", result.getValue("MR_NO", 0)); // ������
		this.setValue("TP1_IPD_NO", result.getValue("IPD_NO", 0)); // סԺ��
		this.setValue("PT1_PAT_NAME", result.getValue("PAT_NAME", 0)); // ����
		this.setValue("TP1_IDNO", result.getValue("IDNO", 0)); // ���֤
		this.setValue("TP1_SEX", result.getValue("SEX", 0)); // �Ա�
		this.setValue("TP1_BIRTH_DATE", result.getTimestamp("BIRTH_DATE", 0));// ����
		this.setValue("TP1_CASE_NO", result.getValue("CASE_NO", 0)); // סԺ���
		this.setValue("TP1_MARRIGE", result.getValue("MARRIGE", 0)); // ����
		// ��ʾ����
		if (result.getData("BIRTH_DATE", 0) != null) {
			this.setValue("TP1_AGE", com.javahis.util.StringUtil.showAge(result
					.getTimestamp("BIRTH_DATE", 0), result.getTimestamp(
					"IN_DATE", 0))); // Ӧ���� IN_DATE
		}
		this.setValue("HOMEPLACE_CODE", result.getValue("HOMEPLACE_CODE", 0));// �����ش���
		this.setValue("TP1_NATION", result.getValue("NATION", 0)); // ����
		this.setValue("TP1_FOLK", result.getValue("FOLK", 0)); // ����
		this.setValue("TP1_PAYTYPE", result.getValue("CTZ1_CODE", 0)); // ֧����ʽ
		// CTZ1_CODE
		this.setValue("TP1_INNUM", result.getValue("IN_COUNT", 0)); // סԺ����
		this.setValue("TP1_TEL", result.getValue("TEL", 0)); // ���ߵ绰
		// ʡ���б�ѡ����Ҫ���ʱ���ȷ����ʡ��ǰ3λ�����Ǻ�3λ
		this.setValue("TP1_H_ADDRESS", result.getValue("H_ADDRESS", 0)); // ����סַ
		this.setValue("TP1_H_POSTNO", result.getValue("H_POSTNO", 0)); // �����ʱ�
		//	this.setValue("TP1_OCCUPATION", result.getValue("OCCUPATION", 0)); // ְҵ
		this.setValue("TP1_OFFICE", result.getValue("OFFICE", 0)); // ��λ
		this.setValue("TP1_O_TEL", result.getValue("O_TEL", 0)); // ��λ�绰
		this.setValue("TP1_O_ADDRESS", result.getValue("O_ADDRESS", 0)); // ��λ��ַ
		this.setValue("TP1_O_POSTNO", result.getValue("O_POSTNO", 0)); // ��λ�ʱ�
		this.setValue("TP1_CONTACTER", result.getValue("CONTACTER", 0)); // ��ϵ������
		//this.setValue("TP1_RELATIONSHIP", result.getValue("RELATIONSHIP", 0)); // ��ϵ�˹�ϵ

		// modified by WangQing 20170331 -start 
		// �ϵ�ְҵ��Ϣ����ϵ�˹�ϵ��Ϣ��ʾ
		TTextFormat occText = (TTextFormat) this.getComponent("TP1_OCCUPATION");
		TComboBox relationCombo= (TComboBox) this.getComponent("TP1_RELATIONSHIP");
		relationCombo.setCanEdit(true);
		String sql1 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ result.getValue("OCCUPATION", 0) + "' ";
		String sql2 = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ result.getValue("RELATIONSHIP", 0) + "' ";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		occText.setText((String) result1.getData("CHN_DESC", 0));
		relationCombo.setText((String) result2.getData("CHN_DESC", 0));
		
		oldOccCode = result.getValue("OCCUPATION", 0);
		oldRelationCode = result.getValue("RELATIONSHIP", 0);
		// modified by WangQing 20170331 -end

		this.setValue("TP1_CONT_TEL", result.getValue("CONT_TEL", 0)); // ��ϵ�˵绰
		this.setValue("TP1_CONT_ADDRESS", result.getValue("CONT_ADDRESS", 0)); // ��ϵ�˵�ַ
		this.setValue("ADMCHK_FLG", result.getValue("ADMCHK_FLG", 0));// סԺ��¼��ʶ
		/*---------------------------------��ʼ----------------------------------------*/
		this.setValue("MRO_CTZ", result.getValue("MRO_CTZ", 0)); // ������ҳ���
		this.setValue("BIRTHPLACE", result.getValue("BIRTHPLACE", 0)); // ����
		this.setValue("TP1_ADDRESS", result.getValue("ADDRESS", 0)); // ͨ��סַ
		this.setValue("TP1_POST_NO", result.getValue("POST_NO", 0)); // ͨ���ʱ�
		this.setValue("TP1_NHI_NO", result.getValue("NHI_NO", 0)); // ҽ���� add
		
		this.onRESID_POST_CODE(); // �����ʱ����ʡ��
		this.onCompanyPost();// �����ʱ����ʡ��
		this.onPost();// �����ʱ����ʡ��
		
		
		
		
		
		
		
		
		// by
		// wanglong
		// 2012-11-27
		this.setValue("TP1_NHI_CARDNO", result.getValue("NHI_CARDNO", 0)); // ��������
		// add
		// by
		// wanglong
		// 2012-11-27
		/*---------------------------------����----------------------------------------*/
		ADMCHK_FLG = result.getValue("ADMCHK_FLG", 0);
		/*-------------------��һҶǩ end------------------------------*/
		// ���ÿؼ��Ƿ���Ա༭ ������� ��סԺ�����͡������ҡ����� ���õ�һҳǩ���ɱ༭
		if (!"1".equals(UserType) && !"4".equals(UserType)
				|| "Y".equals(ADMCHK_FLG)) {
			this.setEnabledPage1(false);
		}
		if ("1".equals(UserType) && "Y".equals(ADMCHK_FLG)) {
			callFunction("UI|save|setEnabled", false);
		}
	}

	/**
	 * �ڶ�ҳǩ��ֵ
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP2(TParm result) {
		/*-------------------�ڶ�Ҷǩ start----------------------------*/
		
		// modified by WangQing 20170412
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		if(table.getRowCount() <= 0 && isFirst){
			this.OPTableBind(MR_NO,CASE_NO);
			isFirst = false;
		}
		
		
		// ��һСҳǩ
		// this.setValue("TP2_TRANS_DEPT", result.getValue("TRANS_DEPT", 0)); //
		// ת�ƿ���
		Timestamp endTime = result.getValue("OUT_DATE", 0).equals("") ? SystemTool
				.getInstance().getDate()
				: result.getTimestamp("OUT_DATE", 0);
		int realStayDays = StringTool.getDateDiffer(StringTool.getTimestamp(
				StringTool.getString(endTime, "yyyyMMdd"), "yyyyMMdd"),
				StringTool.getTimestamp(StringTool.getString(result
						.getTimestamp("IN_DATE", 0), "yyyyMMdd"), "yyyyMMdd"));
		this.setValue("TP2_REAL_STAY_DAYS", realStayDays > 0 ? String
				.valueOf(realStayDays) : "1"); // ʵ��סԺ����
		this.setValue("TP2_OUT_DEPT", result.getValue("OUT_DEPT", 0)); // ��Ժ����
		this.setValue("TP2_IN_CONDITION", result.getValue("IN_CONDITION", 0)); // ��Ժ���
		this.setValue("TP2_OUT_DATE", result.getTimestamp("OUT_DATE", 0));// ��Ժ����
		this.setValue("TP2_CONFIRM_DATE", result
				.getTimestamp("CONFIRM_DATE", 0));// ȷ������
		/*
		 * ������������Ժ���غͳ�������  addBy ZhangZe
		 */
		
		this.setValue("TP2_NB_ADM_WEIGHT", result.getValue("NB_ADM_WEIGHT",0));
		this.setValue("TP2_NB_WEIGHT", result.getValue("NB_WEIGHT",0));
		
		
		/*--------------------------------20120112add  start------------------------------------------*/
		this.setValue("TP2_ADM_SOURCE", result.getValue("ADM_SOURCE", 0));// ������Դ
		this.setValue("TP2_IN_DATE", result.getTimestamp("IN_DATE", 0));// ��Ժ����
		this.setValue("TP2_IN_DEPT", result.getValue("IN_DEPT", 0));// ��Ժ����
		this.setValue("TP2_OUT_TYPE", result.getValue("OUT_TYPE", 0));// ��Ժ��ʽ
		this.outTypeSelect();
		this.setValue("TP2_TRAN_HOSP", result.getValue("TRAN_HOSP", 0));// ��תԺ��
		this.onSelectTran();
		// 20120813 shibl modify
		this.setValue("TRAN_HOSP_OTHER", result.getValue("TRAN_HOSP_OTHER", 0));// ��תԺ������������
		// 20121129 shibl add
		/*********************************************************************************/
		this.setValue("TP2_SPENURS_DAYS", result.getValue("SPENURS_DAYS", 0));// �ؼ���������
		this.setValue("TP2_FIRNURS_DAYS", result.getValue("FIRNURS_DAYS", 0));// һ����������
		this.setValue("TP2_SECNURS_DAYS", result.getValue("SECNURS_DAYS", 0));// ������������
		this.setValue("TP2_THRNURS_DAYS", result.getValue("THRNURS_DAYS", 0));// ������������
		this.setValue("TP2_VENTI_TIME", result.getValue("VENTI_TIME", 0));// ������ʹ��ʱ��(��ȷ��)
		
		// add by wangqing 20180116 start ����������ʹ�÷��ӡ�������ʹ����
		this.setValue("TP2_VENTI_TIME_MIN", result.getValue("VENTI_TIME_MIN", 0));//������ʹ�÷���
		this.setValue("TP2_VENTI_TIME_SEC", result.getValue("VENTI_TIME_SEC", 0));//������ʹ�÷���
		// add by wangqing 20180116 end
		
		/*********************************************************************************/
		String betime = result.getValue("BE_COMA_TIME", 0);// ������Ժǰʱ��
		String aftime = result.getValue("AF_COMA_TIME", 0);// ������Ժ��ʱ��
		if (betime.equals("")) {
			this.setValue("TP2_BE_IN_D", "");// ��
			this.setValue("TP2_BE_IN_H", "");// Сʱ
			this.setValue("TP2_BE_IN_M", "");// ����
		} else {
			this.setValue("TP2_BE_IN_D", TypeTool
					.getInt(betime.substring(0, 2)));// ��
			this.setValue("TP2_BE_IN_H", TypeTool
					.getInt(betime.substring(2, 4)));// Сʱ
			this.setValue("TP2_BE_IN_M", TypeTool
					.getInt(betime.substring(4, 6)));// ����
		}
		if (aftime.equals("")) {
			this.setValue("TP2_AF_IN_D", "");// ��
			this.setValue("TP2_AF_IN_H", "");// Сʱ
			this.setValue("TP2_AF_IN_M", "");// ����
		} else {
			this.setValue("TP2_AF_IN_D", TypeTool
					.getInt(aftime.substring(0, 2)));// ��
			this.setValue("TP2_AF_IN_H", TypeTool
					.getInt(aftime.substring(2, 4)));// Сʱ
			this.setValue("TP2_AF_IN_M", TypeTool
					.getInt(aftime.substring(4, 6)));// ����
		}
		this.setValue("TP2_VS_NURSE_CODE", result.getValue("VS_NURSE_CODE", 0));// ���λ�ʿ
		if (result.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			this.getCheckbox("TP2_AGN_PLAN_FLG").setSelected(true); // 31����סԺ�ƻ����
		}
		this.setValue("TP2_AGN_PLAN_INTENTION", result.getValue(
				"AGN_PLAN_INTENTION", 0));// 31����סԺ�ƻ�Ŀ��
		/*--------------------------------20120112add  end------------------------------------------*/
		// ����б� 4.0�е�����б��ǴӲ��������е��� ת���������ǹ̶������� Ϊ�յľͲ���ʾ
		TParm diagnosis = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT 'Y' AS EXEC, IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,ICD_KIND AS KIND,"	//duzhw update by 20131025  'Y' AS EXEC ��ʶ������ϱ����ݲ���
										+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,SEQ_NO,ICD_REMARK AS REMARK "
										// add by wukai on 2017/4/7 �����ϵ�ICD_CODE �� IO_TYPE,���ڸ������
										+ " , ICD_CODE AS OLD_ICD_CODE, IO_TYPE AS OLD_IO_TYPE "
										+ " FROM MRO_RECORD_DIAG "
										+ " WHERE CASE_NO='"
										+ this.CASE_NO
										+ "' ORDER BY IO_TYPE ASC,MAIN_FLG DESC,SEQ_NO")); // ��ϼ�¼
		// int diagRows = 0;
		// // �ż������
		// if (result.getValue("OE_DIAG_CODE", 0).length() > 0) {
		// diagnosis.addData("TYPE", "I"); // �ż������
		// diagnosis.addData("MAIN", "Y"); // �����
		// diagnosis.addData("NAME", result.getValue("OE_DIAG_CODE", 0));
		// diagnosis.addData("CODE", result.getValue("OE_DIAG_CODE", 0)); //
		// �ż������
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION", ""); // ��Ժ����
		// diagRows++;
		// }
		// // �ż������2
		// if (result.getValue("OE_DIAG_CODE2", 0).length() > 0) {
		// diagnosis.addData("TYPE", "I"); // �ż������
		// diagnosis.addData("MAIN", "N"); // �����
		// diagnosis.addData("NAME", result.getValue("OE_DIAG_CODE2", 0));
		// diagnosis.addData("CODE", result.getValue("OE_DIAG_CODE2", 0)); //
		// �ż������
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION", ""); // ��Ժ����
		// diagRows++;
		// }
		// // �ż������3
		// if (result.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// diagnosis.addData("TYPE", "I"); // �ż������
		// diagnosis.addData("MAIN", "N"); // �����
		// diagnosis.addData("NAME", result.getValue("OE_DIAG_CODE3", 0));
		// diagnosis.addData("CODE", result.getValue("OE_DIAG_CODE3", 0)); //
		// �ż������
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION", ""); // ��Ժ����
		// diagRows++;
		// }
		// // ��Ժ���
		// if (result.getValue("IN_DIAG_CODE", 0).length() > 0) {
		// diagnosis.addData("TYPE", "M"); // ��Ժ���
		// diagnosis.addData("MAIN", "Y"); // �����
		// diagnosis.addData("NAME", result.getValue("IN_DIAG_CODE", 0));
		// diagnosis.addData("CODE", result.getValue("IN_DIAG_CODE", 0)); //
		// ��Ժ���
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION", ""); // ��Ժ����
		// diagRows++;
		// }
		// // ��Ժ���2
		// if (result.getValue("IN_DIAG_CODE2", 0).length() > 0) {
		// diagnosis.addData("TYPE", "M"); // ��Ժ���
		// diagnosis.addData("MAIN", "N"); // �����
		// diagnosis.addData("NAME", result.getValue("IN_DIAG_CODE2", 0));
		// diagnosis.addData("CODE", result.getValue("IN_DIAG_CODE2", 0)); //
		// ��Ժ���
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION", ""); // ��Ժ����
		// diagRows++;
		// }
		// // ��Ժ���3
		// if (result.getValue("IN_DIAG_CODE3", 0).length() > 0) {
		// diagnosis.addData("TYPE", "M"); // ��Ժ���
		// diagnosis.addData("MAIN", "N");
		// diagnosis.addData("NAME", result.getValue("IN_DIAG_CODE3", 0));
		// diagnosis.addData("CODE", result.getValue("IN_DIAG_CODE3", 0)); //
		// ��Ժ���
		// diagnosis.addData("STATUS", "");
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION", ""); // ��Ժ����
		// diagRows++;
		// }
		// // ��Ժ�����
		// if (result.getValue("OUT_DIAG_CODE1", 0).length() > 0) {
		// diagnosis.addData("TYPE", "O"); // ��Ժ�����
		// diagnosis.addData("MAIN", "Y"); // �����
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE1", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE1", 0)); //
		// ��Ժ�����
		// diagnosis.addData("STATUS", result.getValue("CODE1_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE1_REMARK", 0)); //
		// ��ע
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE1", 0)); // ������
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE1", 0)); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION1", 0)); // ��Ժ����
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE2", 0).length() > 0) {// ��Ժ�ڶ����
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// �����
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE2", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE2", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE2_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE2_REMARK", 0));// ��ע
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE2", 0)); // ������
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE2", 0)); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION2", 0)); // ��Ժ����
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE3", 0).length() > 0) {// ��Ժ�������
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// �����
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE3", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE3", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE3_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE3_REMARK", 0));// ��ע
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE3", 0)); // ������
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE3", 0)); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION3", 0)); // ��Ժ����
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE4", 0).length() > 0) {// ��Ժ�������
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// �����
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE4", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE4", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE4_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE4_REMARK", 0));// ��ע
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE4", 0)); // ������
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE4", 0)); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION4", 0)); // ��Ժ����
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE5", 0).length() > 0) {// ��Ժ�������
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// �����
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE5", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE5", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE5_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE5_REMARK", 0));// ��ע
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE5", 0)); // ������
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE5", 0)); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION5", 0)); // ��Ժ����
		// diagRows++;
		// }
		// if (result.getValue("OUT_DIAG_CODE6", 0).length() > 0) {// ��Ժ�������
		// diagnosis.addData("TYPE", "O");
		// diagnosis.addData("MAIN", "N");// �����
		// diagnosis.addData("NAME", result.getValue("OUT_DIAG_CODE6", 0));
		// diagnosis.addData("CODE", result.getValue("OUT_DIAG_CODE6", 0));
		// diagnosis.addData("STATUS", result.getValue("CODE6_STATUS", 0));
		// diagnosis.addData("REMARK", result.getValue("CODE6_REMARK", 0));// ��ע
		// diagnosis.addData("ADDITIONAL",
		// result.getValue("ADDITIONAL_CODE6", 0)); // ������
		// diagnosis.addData("ADDITIONAL_DESC",
		// result.getValue("ADDITIONAL_CODE6", 0)); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("OUT_DIAG_CONDITION6", 0)); // ��Ժ����
		// diagRows++;
		// }
		// if (result.getValue("INTE_DIAG_CODE", 0).length() > 0) {// Ժ�ڸ�Ⱦ���
		// diagnosis.addData("TYPE", "Q"); // Ժ�ڸ�Ⱦ���
		// diagnosis.addData("MAIN", "N"); // �����
		// diagnosis.addData("NAME", result.getValue("INTE_DIAG_CODE", 0));
		// diagnosis.addData("CODE", result.getValue("INTE_DIAG_CODE", 0)); //
		// Ժ�ڸ�Ⱦ���
		// diagnosis.addData("STATUS", result.getValue("INTE_DIAG_STATUS", 0));
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("INTE_DIAG_CONDITION", 0)); // ��Ժ����
		// diagRows++;
		// }
		// /*------------------------------------20120112add
		// start----------------------------------------------*/
		// if (result.getValue("COMPLICATION_DIAG", 0).length() > 0) {// Ժ�ڲ������
		// diagnosis.addData("TYPE", "W"); // Ժ�ڲ������
		// diagnosis.addData("MAIN", "N"); // �����
		// diagnosis.addData("NAME", result.getValue("COMPLICATION_DIAG", 0));
		// diagnosis.addData("CODE", result.getValue("COMPLICATION_DIAG", 0));
		// // Ժ�ڲ������
		// diagnosis.addData("STATUS",
		// result.getValue("COMPLICATION_STATUS", 0));
		// diagnosis.addData("REMARK", ""); // ��ע
		// diagnosis.addData("ADDITIONAL", ""); // ������
		// diagnosis.addData("ADDITIONAL_DESC", ""); // �������滻����
		// diagnosis.addData("IN_PAT_CONDITION",
		// result.getValue("COMPLICATION_DIAG_CONDITION", 0)); // ��Ժ����
		// diagRows++;
		// }
		// // System.out.println("-------------------------" + diagnosis);
		// diagnosis.setCount(diagRows);
		DiagGrid.removeRowAll();
		DiagGrid.setParmValue(diagnosis);
		DiagGrid.addRow();// �½����� �����������
		this.setValue("DIAGCHK_FLG", result.getValue("DIAGCHK_FLG", 0));// ҽʦ�ύ��ʶ
		DIAGCHK_FLG = result.getValue("DIAGCHK_FLG", 0);
		// �ڶ�Сҳǩ
		this.setValue("TP22_PATHOLOGY_DIAG", result.getValue("PATHOLOGY_DIAG",
				0)); // �������1
		this.setValue("TP2_PATHOLOGY_NO", result.getValue("PATHOLOGY_NO", 0)); // �����1
		this.setValue("TP22_PATHOLOGY_DIAG2", result.getValue(
				"PATHOLOGY_DIAG2", 0)); // �������2
		this.setValue("TP2_PATHOLOGY_NO2", result.getValue("PATHOLOGY_NO2", 0)); // �����2
		this.setValue("TP22_PATHOLOGY_DIAG3", result.getValue(
				"PATHOLOGY_DIAG3", 0)); // �������2
		this.setValue("TP2_PATHOLOGY_NO3", result.getValue("PATHOLOGY_NO3", 0)); // �����2
		OrderList order = new OrderList();
		// �滻�����������
		this.setValue("PATHOLOGY_DIAG_DESC", order.getTableShowValue(result
				.getValue("PATHOLOGY_DIAG", 0)));
		this.setValue("PATHOLOGY_DIAG_DESC2", order.getTableShowValue(result
				.getValue("PATHOLOGY_DIAG2", 0)));
		this.setValue("PATHOLOGY_DIAG_DESC3", order.getTableShowValue(result
				.getValue("PATHOLOGY_DIAG3", 0)));

		this.setValue("MDIAG_BASIS", result.getValue("MDIAG_BASIS", 0)); // ����������
		this.setValue("DIF_DEGREE", result.getValue("DIF_DEGREE", 0)); // �ֻ��̶�

		this.setValue("TP22_EX_RSN", result.getValue("EX_RSN", 0)); // �ⲿ����1
		this.setValue("TP22_EX_RSN2", result.getValue("EX_RSN2", 0)); // �ⲿ����2
		this.setValue("TP22_EX_RSN3", result.getValue("EX_RSN3", 0)); // �ⲿ����3
		// �滻�����ж�����
		this.setValue("EX_RSN_DESC", order.getTableShowValue(result.getValue(
				"EX_RSN", 0)));
		this.setValue("EX_RSN_DESC2", order.getTableShowValue(result.getValue(
				"EX_RSN2", 0)));
		this.setValue("EX_RSN_DESC3", order.getTableShowValue(result.getValue(
				"EX_RSN3", 0)));

		this.setValue("TP22_ALLEGIC", result.getValue("ALLEGIC", 0)); // ҩ�����
		// �Ƿ���ҩ����� shibl 20120621 add
		if (result.getValue("ALLEGIC_FLG", 0).equals("1"))
			getRDBtn("ALLEGIC_1").setSelected(true);
		else if (result.getValue("ALLEGIC_FLG", 0).equals("2"))
			getRDBtn("ALLEGIC_2").setSelected(true);

		// HBsAg
		if (result.getValue("HBSAG", 0).equals("0"))
			getRDBtn("HBsAg_1").setSelected(true);
		else if (result.getValue("HBSAG", 0).equals("1"))
			getRDBtn("HBsAg_2").setSelected(true);
		else if (result.getValue("HBSAG", 0).equals("2"))
			getRDBtn("HBsAg_3").setSelected(true);
		// HCV-Ab
		if (result.getValue("HCV_AB", 0).equals("0"))
			getRDBtn("HCV-Ab_1").setSelected(true);
		else if (result.getValue("HCV_AB", 0).equals("1"))
			getRDBtn("HCV-Ab_2").setSelected(true);
		else if (result.getValue("HCV_AB", 0).equals("2"))
			getRDBtn("HCV-Ab_3").setSelected(true);
		// HIV-Ab
		if (result.getValue("HIV_AB", 0).equals("0"))
			getRDBtn("HIV-Ab_1").setSelected(true);
		else if (result.getValue("HIV_AB", 0).equals("1"))
			getRDBtn("HIV-Ab_2").setSelected(true);
		else if (result.getValue("HIV_AB", 0).equals("2"))
			getRDBtn("HIV-Ab_3").setSelected(true);
		// ������סԺ
		if (result.getValue("QUYCHK_OI", 0).equals("0"))
			getRDBtn("TP22_myc1").setSelected(true);
		else if (result.getValue("QUYCHK_OI", 0).equals("1"))
			getRDBtn("TP22_myc2").setSelected(true);
		else if (result.getValue("QUYCHK_OI", 0).equals("2"))
			getRDBtn("TP22_myc3").setSelected(true);
		else if (result.getValue("QUYCHK_OI", 0).equals("3"))
			getRDBtn("TP22_myc4").setSelected(true);
		// ��Ժ���Ժ
		if (result.getValue("QUYCHK_INOUT", 0).equals("0"))
			getRDBtn("TP22_ryc1").setSelected(true);
		else if (result.getValue("QUYCHK_INOUT", 0).equals("1"))
			getRDBtn("TP22_ryc2").setSelected(true);
		else if (result.getValue("QUYCHK_INOUT", 0).equals("2"))
			getRDBtn("TP22_ryc3").setSelected(true);
		else if (result.getValue("QUYCHK_INOUT", 0).equals("3"))
			getRDBtn("TP22_ryc4").setSelected(true);
		// ��ǰ������
		if (result.getValue("QUYCHK_OPBFAF", 0).equals("0"))
			getRDBtn("TP22_sys1").setSelected(true);
		else if (result.getValue("QUYCHK_OPBFAF", 0).equals("1"))
			getRDBtn("TP22_sys2").setSelected(true);
		else if (result.getValue("QUYCHK_OPBFAF", 0).equals("2"))
			getRDBtn("TP22_sys3").setSelected(true);
		else if (result.getValue("QUYCHK_OPBFAF", 0).equals("3"))
			getRDBtn("TP22_sys4").setSelected(true);
		// �ٴ��벡��
		if (result.getValue("QUYCHK_CLPA", 0).equals("0"))
			getRDBtn("TP22_lyb1").setSelected(true);
		else if (result.getValue("QUYCHK_CLPA", 0).equals("1"))
			getRDBtn("TP22_lyb2").setSelected(true);
		else if (result.getValue("QUYCHK_CLPA", 0).equals("2"))
			getRDBtn("TP22_lyb3").setSelected(true);
		else if (result.getValue("QUYCHK_CLPA", 0).equals("3"))
			getRDBtn("TP22_lyb4").setSelected(true);
		// �����벡��
		if (result.getValue("QUYCHK_RAPA", 0).equals("0"))
			getRDBtn("TP22_fyb1").setSelected(true);
		else if (result.getValue("QUYCHK_RAPA", 0).equals("1"))
			getRDBtn("TP22_fyb2").setSelected(true);
		else if (result.getValue("QUYCHK_RAPA", 0).equals("2"))
			getRDBtn("TP22_fyb3").setSelected(true);
		else if (result.getValue("QUYCHK_RAPA", 0).equals("3"))
			getRDBtn("TP22_fyb4").setSelected(true);
		// ����
		this.setValue("TP22_GET_TIMES", result.getValue("GET_TIMES", 0));
		this
				.setValue("TP22_SUCCESS_TIMES", result.getValue(
						"SUCCESS_TIMES", 0)); // ���ȳɹ�����
		this.setValue("TP22_DIRECTOR_DR_CODE", result.getValue(
				"DIRECTOR_DR_CODE", 0)); // ������
		this.setValue("TP22_PROF_DR_CODE", result.getValue("PROF_DR_CODE", 0)); // ��(����)��ҽʦ
		this.setValue("TP22_ATTEND_DR_CODE", result.getValue("ATTEND_DR_CODE",
				0)); // ����ҽʦ
		this.setValue("TP2_VS_DR_CODE", result.getValue("VS_DR_CODE", 0)); // סԺҽʦ
		this.setValue("TP22_INDUCATION_DR_CODE", result.getValue(
				"INDUCATION_DR_CODE", 0)); // ����ҽʦ
		this.setValue("TP22_INTERN_DR_CODE", result.getValue("INTERN_DR_CODE",
				0)); // ʵϰҽʦ
		this.setValue("TP22_GRADUATE_INTERN_CODE", result.getValue("GRADUATE_INTERN_CODE",
				0)); // �о���ҽʦ  add duzhw 20131204
		// ����Сҳǩ
		// ����
		
		// this.setValue("DAY_OPE_FLG", result.getValue("DAY_OPE_FLG"));//�ռ����� add by huangtt 20161222
		// this.messageBox(""+result);
		// this.messageBox("�Ƿ�ֵ:"+ result.getValue("DAY_OPE_FLG",0));
		
		if(result.getValue("DAY_OPE_FLG", 0).equals("Y")) {			// �ռ�������ѡ��ֵ   2017/3/29   BY  yanmm
	    		getCheckbox("DAY_OPE_FLG").setSelected(true);
	        }
		
//		this.OPTableBind(MR_NO, CASE_NO);
		if (!result.getValue("OPE_TYPE_CODE", 0).equals("0")) {
			String opeStr = result.getValue("OPE_TYPE_CODE", 0);
			String[] line = opeStr.split(",");
			if (line.length > 1) {
				for (int i = 0; i < line.length; i++) {
					if (line[i].equals("1"))
						this.getCheckbox("OPE_TYPE_CODE1").setSelected(true);
					if (line[i].equals("2"))
						this.getCheckbox("OPE_TYPE_CODE2").setSelected(true);
				}
			} else {
				if (opeStr.equals("1"))
					this.getCheckbox("OPE_TYPE_CODE1").setSelected(true);
				else if (opeStr.equals("2"))
					this.getCheckbox("OPE_TYPE_CODE2").setSelected(true);
			}
		} else {
			getCheckbox("OPE_TYPE_CODE1").setSelected(false);// ��������
			getCheckbox("OPE_TYPE_CODE2").setSelected(false);// ��������
		}
		// Ѫ��
		if (result.getValue("BLOOD_TYPE", 0).equals("1"))
			getRDBtn("TP23XX1").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("2"))
			getRDBtn("TP23XX2").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("3"))
			getRDBtn("TP23XX3").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("4"))
			getRDBtn("TP23XX4").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("5"))// ����
			getRDBtn("TP23XX5").setSelected(true);
		else if (result.getValue("BLOOD_TYPE", 0).equals("6"))// δ��
			getRDBtn("TP23XX6").setSelected(true);
		// ��Ѫ��ӳ
		if (result.getValue("TRANS_REACTION", 0).equals("1"))// ��
			getRDBtn("PT23SX1").setSelected(true);
		else if (result.getValue("TRANS_REACTION", 0).equals("2"))// ��
			getRDBtn("PT23SX2").setSelected(true);
		else if (result.getValue("TRANS_REACTION", 0).equals("0"))// δ��
			getRDBtn("PT23SX3").setSelected(true);
		// RH
		if (result.getValue("RH_TYPE", 0).equals("1"))
			getRDBtn("PT23RH1").setSelected(true);
		else if (result.getValue("RH_TYPE", 0).equals("2"))
			getRDBtn("PT23RH2").setSelected(true);
		else if (result.getValue("RH_TYPE", 0).equals("3"))
			getRDBtn("PT23RH3").setSelected(true); // ����
		else if (result.getValue("RH_TYPE", 0).equals("4"))
			getRDBtn("PT23RH4").setSelected(true); // δ��
		// ��ϸ��
		this.setValue("TP23_RBC", result.getValue("RBC", 0));
		this.setValue("TP23_PLATE", result.getValue("PLATE", 0)); // ��С��
		this.setValue("TP23_PLASMA", result.getValue("PLASMA", 0)); // Ѫ��
		this.setValue("TP23_WHOLE_BLOOD", result.getValue("WHOLE_BLOOD", 0)); // ȫѪ
		this.setValue("TP23_BANKED_BLOOD", result.getValue("BANKED_BLOOD", 0)); // ȫ�����
		this.setValue("TP23_OTH_BLOOD", result.getValue("OTH_BLOOD", 0)); // ����
		// ����Сҳǩ

		// ҽԺ��Ⱦ�ܴ���
		this.setValue("INFECT_COUNT", result.getValue("INFECT_COUNT", 0));
		// ʬ��
		this.setValue("BODY_CHECK", result.getValue("BODY_CHECK", 0));// wanglong modify 20141030
		// ��Ժ��һ��
		if (result.getValue("FIRST_CASE", 0).equals("1"))
			getRDBtn("PT24BY1").setSelected(true);
		else if (result.getValue("FIRST_CASE", 0).equals("2"))
			getRDBtn("PT24BY2").setSelected(true);
		// ���� ��������ꡢ�¡�������Ϊ�ջ���Ϊ�㣬��ô����Ҫ����
		if (result.getValue("ACCOMP_DATE", 0).length() <= 0)
			(getRDBtn("PT24SZ2")).setSelected(true);
		else
			(getRDBtn("PT24SZ1")).setSelected(true);
		// ������
		this.setValue("TP24_ACCOMPANY_YEAR", result.getValue("ACCOMPANY_YEAR",
				0));
		// ������
		this.setValue("TP24_ACCOMPANY_MONTH", result.getValue(
				"ACCOMPANY_MONTH", 0));
		// ������
		this.setValue("TP24_ACCOMPANY_WEEK", result.getValue("ACCOMPANY_WEEK",
				0));
		// ��������
		this
				.setValue("TP24_ACCOMP_DATE", result.getTimestamp(
						"ACCOMP_DATE", 0));
		// ʾ������
		if (result.getValue("SAMPLE_FLG", 0).equals("1"))
			getRDBtn("PT24SJBL1").setSelected(true);
		else if (result.getValue("SAMPLE_FLG", 0).equals("2"))
			getRDBtn("PT24SJBL2").setSelected(true);
		// �ٴ�·�����ٴ�·���������� �뿴2.0����
		/*
		 * ������ҳ�У����ֶ��ٴ����鲡���ֵ䡢 ��ѧ�����ֵ�ͬʱ�����ϲ�ѯ���ܣ�����ά���������ֶ� TEST_EMR,TEACH_EMR
		 * ZhenQin - 2011-05-09
		 */
		this.setValue("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE", 0));
		this.setValue("DISEASES_CODE", result.getValue("DISEASES_CODE", 0));
		this.setValue("TEACH_EMR", result.getValue("TEACH_EMR", 0));
		//&&==================modify by wukai 20160531  ��ADM_INP��ץȡ�ٴ���Ŀ��Ϣ start
		String sql = "SELECT PATLOGY_PRO_CODE,PATLOGY_DOC_CODE,PATLOGY_DEPT_CODE FROM ADM_INP "+
					 "WHERE MR_NO=" + result.getData("MR_NO", 0) + " AND CASE_NO=" + result.getData("CASE_NO", 0);
		//System.out.println("******sql : "+sql);
		Map map = TJDODBTool.getInstance().select(sql);
		System.out.println("******map : "+map.toString());
		if(map != null && map.size() > 0) {
			Map mm = (Map) map.get("Data");
			if(mm != null) {
				List l  = (List) mm.get("PATLOGY_PRO_CODE");
				if(l != null) {
					this.setValue("TEST_EMR", l.get(0));
				}
			}
		}
		//&&==================modify by wukai 20160531  ��ADM_INP��ץȡ�ٴ���Ŀ��Ϣ end
		if(result.getValue("TEST_EMR",0) != null && String.valueOf(result.getValue("TEST_EMR",0)).length() > 0) {
			this.setValue("TEST_EMR", result.getValue("TEST_EMR",0));
		}
		//����Сҳǩ  modify by zhangh 2013-10-11
		this.setValue("VENTI_TIME", result.getValue("VENTI_TIME", 0));
		
		// add by wangqing 20180116 start ����������ʹ�÷��ӡ�������ʹ����
		this.setValue("VENTI_TIME_MIN", result.getValue("VENTI_TIME_MIN", 0));
		this.setValue("VENTI_TIME_SEC", result.getValue("VENTI_TIME_SEC", 0));
		// add by wangqing 20180116 end
		
		TParm icuParm = new TParm();
		for (int i = 1; i <= 5; i++) {
			icuParm.setData("ICU_ROOM", i, result.getData("ICU_ROOM" + i, 0));
			icuParm.setData("IN_DATE", i, result.getData("ICU_IN_DATE" + i, 0));
			icuParm.setData("OUT_DATE", i, result.getData("ICU_OUT_DATE" + i, 0));
		}
		TTable icuTable = (TTable) this.getComponent("TP25_ICU_Table");
		icuTable.setParmValue(icuParm);
		
		/*-------------------�ڶ�Ҷǩ end----------------------------*/
		// ���ÿؼ��Ƿ���Ա༭ ������� ��ҽʦ���͡������ҡ����� ���õڶ�ҳǩ���ɱ༭
		if (!"2".equals(UserType) && !"4".equals(UserType)
				|| "Y".equals(DIAGCHK_FLG)) {
			this.setEnabledPage2(false);
		}
		if ("2".equals(UserType) && "Y".equals(DIAGCHK_FLG)) {
			callFunction("UI|save|setEnabled", false);
		}
	}

	/**
	 * ����ҳǩ��ֵ
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP3(TParm result) {
		/*-------------------����Ҷǩ start----------------------------*/
		// �����¼ ��Ҫ����תΪ����ʾ
		TParm cw = new TParm();
		double sum = 0; // �����ܺ�
		double num;
		String seq = "";
		String c_name; // ����
		String c_name1;
		// String chn_name="";//��������
		DecimalFormat df = new DecimalFormat("0.00");
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE_";
			c_name1 = "CHARGE";
			if (i < 10)// IС��10 ����
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			c_name1 += seq;
			if (result.getValue(c_name, 0).trim() == null
					|| result.getValue(c_name, 0).trim().equals(""))
				num = 0;
			else
				num = result.getDouble(c_name, 0);
			if (map.get(MrofeeCode.get(c_name1)) != null) {
				cw.setData("NO", i, i);
				cw.setData("Name", i, map.get(MrofeeCode.get(c_name1)) + "");
				cw.setData("Amount", i, df.format(num));
				sum += Double.valueOf(num);
			}
		}
		this.setValue("TP3_SUM", df.format(sum)); // �ܽ��
		((TTable) this.getComponent("TP3_Table1")).setParmValue(cw);
		this.setValue("BILCHK_FLG", result.getValue("BILCHK_FLG", 0));// �����ύ��ʶ
		BILCHK_FLG = result.getValue("BILCHK_FLG", 0);
		/*-------------------����Ҷǩ end----------------------------*/
	}

	/**
	 * ����ҳǩ��ֵ
	 * 
	 * @param result
	 *            TParm
	 */
	private void setValueTP4(TParm result) {
		/*-------------------����Ҷǩ start----------------------------*/
		// ��Ⱦ������
		if (result.getValue("INFECT_REPORT", 0).equals("1"))
			getRDBtn("TP4CR1").setSelected(true);
		else if (result.getValue("INFECT_REPORT", 0).equals("2"))
			getRDBtn("TP4CR2").setSelected(true);
		// �Ĳ����� ���±����� "��������" �ֶ�
		if (result.getValue("DIS_REPORT", 0).equals("1"))
			getRDBtn("TP4SB1").setSelected(true);
		else if (result.getValue("DIS_REPORT", 0).equals("2"))
			getRDBtn("TP4SB2").setSelected(true);
		// ��������
		if (result.getValue("QUALITY", 0).equals("1"))
			getRDBtn("TP4BA1").setSelected(true);
		else if (result.getValue("QUALITY", 0).equals("2"))
			getRDBtn("TP4BA2").setSelected(true);
		else if (result.getValue("QUALITY", 0).equals("3"))
			getRDBtn("TP4BA3").setSelected(true);
		// �ʿ�ҽʦ ��ʱ��֪��Ӧ��ʹ���ĸ��б��
		this.setValue("TP4_CTRL_DR", result.getValue("CTRL_DR", 0));
		// �ʿػ�ʿ ��ʱ��֪��Ӧ��ʹ���ĸ��б��
		this.setValue("TP4_CTRL_NURSE", result.getValue("CTRL_NURSE", 0));
		// ����
		this.setValue("TP4_CTRL_DATE", result.getTimestamp("CTRL_DATE", 0));
		// ����Ա ��ʱ��֪��Ӧ��ʹ���ĸ��б��
		this.setValue("TP4_ENCODER", result.getValue("ENCODER", 0));
		this.setValue("QTYCHK_FLG", result.getValue("QTYCHK_FLG", 0));// ������(�ʿ�)�ύ��ʶ
		QTYCHK_FLG = result.getValue("QTYCHK_FLG", 0);
		/*-------------------����Ҷǩ end----------------------------*/
		// ���ÿؼ��Ƿ���Ա༭ ������� ��ҽʦ���͡������ҡ����� ���õڶ�ҳǩ���ɱ༭
		if (!"4".equals(UserType) || QTYCHK_FLG.equals("Y")) {
			this.setEnabledPage4(false);
		}
	}

	/**
	 * �󶨲�����̬Grid
	 * 
	 * @param parm
	 *            TParm
	 */
	private void setValueTran(TParm parm) {
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("IN_DATE", i, dt.format(StringTool.getTimestamp(parm
					.getValue("IN_DATE", i), "yyyyMMdd")));
			if (!parm.getValue("OUT_DATE", i).equals(""))
				parm.setData("OUT_DATE", i, parm.getValue("OUT_DATE", i)
						.substring(0, 19).replaceAll("-", "/"));
		}
		parm.setCount(parm.getCount());
		TTable table = (TTable) this.getComponent("TP21_Table1");
		table.setParmValue(parm);
	}

	/**
	 * ��յ�һҳǩ
	 */
	private void clearTP1() {
		this
				.clearValue("TP1_MR_NO;TP1_IPD_NO;PT1_PAT_NAME;TP1_IDNO;TP1_SEX;TP1_BIRTH_DATE;TP1_CASE_NO;TP1_MARRIGE;TP1_AGE;TP1_NATION");
		this
				.clearValue("TP1_FOLK;TP1_PAYTYPE;TP1_INNUM;TP1_TEL;TP1_PROVICE;TP1_COUNTRY;TP1_H_ADDRESS;TP1_H_POSTNO;");
		// modified by WangQing 20170401 
		// delete TP1_RELATIONSHIP
		//				this
		//				.clearValue("TP1_OCCUPATION;TP1_OFFICE;TP1_O_TEL;TP1_O_ADDRESS;TP1_O_POSTNO;TP1_CONTACTER;TP1_RELATIONSHIP;TP1_CONT_TEL;TP1_CONT_ADDRESS");
		this
		.clearValue("TP1_OCCUPATION;TP1_OFFICE;TP1_O_TEL;TP1_O_ADDRESS;TP1_O_POSTNO;TP1_CONTACTER;TP1_CONT_TEL;TP1_CONT_ADDRESS");
		// add
		this
				.clearValue("TP1_ADDRESS;TP1_POST_NO;TP1_POST_R;TP1_POST_C;TP1_O_POST_R;TP1_O_POST_C;MRO_CTZ;BIRTHPLACE_DESC;BIRTHPLACE");
	}

	/**
	 * ��յڶ�ҳǩ
	 */
	private void clearTP2() {
		this
				.clearValue("TP21_Table1;TP21_Table2;TP21_IsSubmit;TP2_REAL_STAY_DAYS;TP2_OUT_DEPT;TP2_IN_CONDITION;TP2_OUT_DATE;TP2_CONFIRM_DATE");
		this
				.clearValue("TP22_PATHOLOGY_DIAG;TP22_EX_RSN;TP22_ALLEGIC;ALLEGIC_1;ALLEGIC_2;HBsAg_1;HBsAg_2;HBsAg_3;HCV-Ab_1;HCV-Ab_2;HCV-Ab_3;HIV-Ab_1;HIV-Ab_2;HIV-Ab_3");
		this
				.clearValue("TP22_myc1;TP22_myc2;TP22_myc3;TP22_myc4;TP22_sys1;TP22_sys2;TP22_sys3;TP22_sys4;TP22_fyb1;TP22_fyb2;TP22_fyb3;TP22_fyb4;TP22_ryc1;TP22_ryc2;TP22_ryc3;TP22_ryc4;TP22_lyb1;TP22_lyb2;TP22_lyb3;TP22_lyb4");
		this
				.clearValue("TP22_GET_TIMES;TP22_SUCCESS_TIMES;TP22_DIRECTOR_DR_CODE;TP2_VS_DR_CODE;TP22_INTERN_DR_CODE;TP22_PROF_DR_CODE;TP22_INDUCATION_DR_CODE;TP22_ATTEND_DR_CODE;TP22_GRADUATE_INTERN_CODE");
		this
				.clearValue("PT23_Table1;TP23XX1;TP23XX2;TP23XX3;TP23XX4;TP23XX5;TP23XX6;PT23SX1;PT23SX2;PT23RH1;PT23RH2;PT23RH3;TP23_RBC;TP23_PLATE;TP23_PLASMA;TP23_WHOLE_BLOOD;TP23_OTH_BLOOD");
		this
				.clearValue("BODY_CHECK;PT24BY1;PT24BY2;PT24SZ1;PT24SZ2;TP24_ACCOMP_DATE;PT24SJBL1;PT24SJBL2;");
		this.clearValue("PATHOLOGY_DIAG_DESC;EX_RSN_DESC");
		// add
		this
				.clearValue("TP2_ADM_SOURCE;TP2_IN_DATE;TP2_IN_DEPT;TP2_OUT_TYPE;TP2_TRAN_HOSP;TP2_BE_IN_D;TP2_BE_IN_H;TP2_BE_IN_M;TP2_AF_IN_D;TP2_AF_IN_H;TP2_AF_IN_M;TP2_VS_NURSE_CODE;TP2_AGN_PLAN_INTENTION;TP2_AGN_PLAN_FLG;TP2_PATHOLOGY_NO;TRAN_HOSP_OTHER");
		//zhangh 2013-10-11
		this.clearValue("TP25_ICU_Table;VENTI_TIME;DAY_OPE_FLG");
		
		// add by wangqing 20180116 start ����������ʹ�÷��ӡ�������ʹ����
		this.clearValue("VENTI_TIME_MIN;VENTI_TIME_SEC;TP2_VENTI_TIME_MIN;TP2_VENTI_TIME_SEC");
		
		// modoified by WangQing 20170412
		this.removeOP();
	}

	/**
	 * ��յ���ҳǩ
	 */
	private void clearTP3() {
		this.clearValue("TP3_SUM;TP3_IsSubmit;TP3_Table1");
	}

	/**
	 * ��յ���ҳǩ
	 */
	private void clearTP4() {
		this
				.clearValue("TP4CR1;TP4CR2;TP4SB1;TP4SB2;TP4_IsSubmit;TP4BA1;TP4BA2;TP4BA3;TP4_CTRL_DR;TP4_CTRL_NURSE;TP4_CTRL_DATE;TP4_ENCODER");
	}

	/**
	 * ��ȡ��һҳǩ������
	 * 
	 * @return TParm
	 */
	public TParm getFisrtPageInfo() {
		TParm parm = new TParm();
		parm.setData("PAT_NAME", this.getValue("PT1_PAT_NAME")); // ����
		parm.setData("IDNO", this.getValue("TP1_IDNO")); // ���֤��
		parm.setData("SEX", this.getValue("TP1_SEX")); // �Ա�
		if (this.getValue("TP1_BIRTH_DATE") == null)
			parm.setData("BIRTH_DATE", new TNull(Timestamp.class)); // ����
		else
			parm.setData("BIRTH_DATE", this.getValue("TP1_BIRTH_DATE")); // ����
		parm.setData("AGE", this.getValue("TP1_AGE")); // ����
		parm.setData("MARRIGE", this.getValue("TP1_MARRIGE")); // ����
		parm.setData("NATION", this.getValue("TP1_NATION")); // ����
		parm.setData("IN_COUNT", this.getValue("TP1_INNUM")); // סԺ����
		parm.setData("FOLK", this.getValue("TP1_FOLK")); // ����
		parm.setData("CTZ1_CODE", this.getValue("TP1_PAYTYPE")); // ���ʽ
		parm.setData("HOMEPLACE_CODE", this.getValueString("HOMEPLACE_CODE"));// �����ش���
		parm.setData("H_ADDRESS", this.getValue("TP1_H_ADDRESS")); // ������ַ
		parm.setData("H_POSTNO", this.getValue("TP1_H_POSTNO")); // �����ʱ�
		
		
		// modified by WangQing 20170411 -start
		parm.setData("OCCUPATION", this.getValue("TP1_OCCUPATION")); // ְҵ
//		this.messageBox("occCode: "+this.getValueString("TP1_OCCUPATION"));
		String sql001 = "SELECT count(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OCCUPATION' AND ID ='" 
				+ this.getValueString("TP1_OCCUPATION") + "' ";
		TParm result001 = new TParm(TJDODBTool.getInstance().select(sql001));
		int count001 = Integer.parseInt(result001.getValue("COUNT", 0));
		if(count001 == 0){
			parm.setData("OCCUPATION", oldOccCode);
		}else{
			parm.setData("OCCUPATION", this.getValue("TP1_OCCUPATION")); // ְҵ
		}
		// modified by WangQing 20170411 -end
		
		
		
	
		parm.setData("OFFICE", this.getValue("TP1_OFFICE")); // ��λ
		parm.setData("O_TEL", this.getValue("TP1_O_TEL")); // ��λ�绰
		parm.setData("O_ADDRESS", this.getValue("TP1_O_ADDRESS")); // ��λ��ַ
		parm.setData("O_POSTNO", this.getValue("TP1_O_POSTNO")); // ��λ�ʱ�
		parm.setData("CONTACTER", this.getValue("TP1_CONTACTER")); // ��ϵ������
		
		
		// modified by WangQing 20170411 -start
		parm.setData("RELATIONSHIP", this.getValue("TP1_RELATIONSHIP")); // ��ϵ�˹�ϵ
//		this.messageBox("===guanxi: "+this.getValue("TP1_RELATIONSHIP"));
		String sql002 = "SELECT COUNT(1) AS COUNT FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' AND ID ='" 
				+ this.getValueString("TP1_RELATIONSHIP") + "' ";
		TParm result002 = new TParm(TJDODBTool.getInstance().select(sql002));
		int count002 = Integer.parseInt(result002.getValue("COUNT", 0));
		if(count002 == 0){
			parm.setData("RELATIONSHIP", oldRelationCode); // ��ϵ�˹�ϵ
		}else{
			parm.setData("RELATIONSHIP", this.getValue("TP1_RELATIONSHIP")); // ��ϵ�˹�ϵ
		}
		// modified by WangQing 20170411 -end
		
		
		
		parm.setData("CONT_TEL", this.getValue("TP1_CONT_TEL")); // ��ϵ�˵绰
		parm.setData("CONT_ADDRESS", this.getValue("TP1_CONT_ADDRESS")); // ��ϵ�˵�ַ
		parm.setData("MR_NO", this.getValue("TP1_MR_NO"));
		parm.setData("CASE_NO", this.getValue("TP1_CASE_NO"));
		parm.setData("IPD_NO", this.getValue("TP1_IPD_NO"));
		/*-------------------------------------------------------------------*/
		parm.setData("MRO_CTZ", this.getValue("MRO_CTZ"));// ������ҳ���
		parm.setData("BIRTHPLACE", this.getValue("BIRTHPLACE"));// ����
		parm.setData("ADDRESS", this.getValue("TP1_ADDRESS"));// ͨ�ŵ�ַ
		parm.setData("POST_NO", this.getValue("TP1_POST_NO"));// ͨ���ʱ�
		parm.setData("TEL", this.getValue("TP1_TEL")); // �绰
		parm.setData("NHI_NO", this.getValue("TP1_NHI_NO")); // �������� add by
		// wanglong
		// 2012-11-27
		parm.setData("NHI_CARDNO", this.getValue("TP1_NHI_CARDNO")); // ҽ������ add
		// by
		// wanglong
		// 2012-11-27
		/*--------------------------------------------------------------------*/
		if (((TCheckBox) this.getComponent("ADMCHK_FLG")).isSelected())// סԺ�ύ��ʶ
			parm.setData("ADMCHK_FLG", "Y");
		else
			parm.setData("ADMCHK_FLG", "N");
		return parm;
	}

	/**
	 * ��ȡ�ڶ�ҳǩ����Ϣ
	 * 
	 * @return TParm
	 */
	public TParm getSecendPageInfo() {
		TParm result = new TParm();
		result.setData("TRANS_DEPT", this.getTranDept()); // ת�ƿ���
		/****************************** ADD START ************************************************/


		result.setData("ADM_SOURCE", this.getValue("TP2_ADM_SOURCE")); // סԺ������Դ
		if (this.getValue("TP2_IN_DATE") == null)
			result.setData("IN_DATE", new TNull(Timestamp.class));
		else
			result.setData("IN_DATE", this.getValue("TP2_IN_DATE")); // סԺ����
		// shibl 20121130 add
		result.setData("SPENURS_DAYS", this.getValue("TP2_SPENURS_DAYS")); // �ؼ���������
		result.setData("FIRNURS_DAYS", this.getValue("TP2_FIRNURS_DAYS")); // һ����������
		result.setData("SECNURS_DAYS", this.getValue("TP2_SECNURS_DAYS")); // ������������
		result.setData("THRNURS_DAYS", this.getValue("TP2_THRNURS_DAYS")); // ������������
//		result.setData("VENTI_TIME", this.getValue("TP2_VENTI_TIME")); // ������ʹ��ʱ��

//		result.setData("ICU_ROOM1", new TNull(String.class)); // ��֢�໤��1����
//		result.setData("ICU_IN_DATE1", new TNull(Timestamp.class));// ��֢�໤��1��ʱ��
//		result.setData("ICU_OUT_DATE1", new TNull(Timestamp.class));// ��֢�໤��1��ʱ��
//
//		result.setData("ICU_ROOM2", new TNull(String.class));// ��֢�໤��2����
//		result.setData("ICU_IN_DATE2", new TNull(Timestamp.class));// ��֢�໤��2��ʱ��
//		result.setData("ICU_OUT_DATE2", new TNull(Timestamp.class));// ��֢�໤��2��ʱ��
//
//		result.setData("ICU_ROOM3", new TNull(String.class));// ��֢�໤��3����
//		result.setData("ICU_IN_DATE3", new TNull(Timestamp.class));// ��֢�໤��3��ʱ��
//		result.setData("ICU_OUT_DATE3", new TNull(Timestamp.class));// ��֢�໤��3��ʱ��
//
//		result.setData("ICU_ROOM4", new TNull(String.class));// ��֢�໤��4����
//		result.setData("ICU_IN_DATE4", new TNull(Timestamp.class));// ��֢�໤��4��ʱ��
//		result.setData("ICU_OUT_DATE4", new TNull(Timestamp.class));// ��֢�໤��4��ʱ��
//
//		result.setData("ICU_ROOM5", new TNull(String.class));// ��֢�໤��5����
//		result.setData("ICU_IN_DATE5", new TNull(Timestamp.class));// ��֢�໤��5��ʱ��
//		result.setData("ICU_OUT_DATE5", new TNull(Timestamp.class));// ��֢�໤��5��ʱ��
//		// ȡ�ü໤����
//		TParm parm = MROTool.getInstance().getICUParm(this.CASE_NO);
//		int count = 1;
//		for (int i = 0; i < parm.getCount(); i++) {
//			result.setData("ICU_ROOM" + count, parm.getValue("DEPT_CODE", i));
//			result.setData("ICU_IN_DATE" + count, parm.getTimestamp("IN_DATE",
//					i) == null ? new TNull(Timestamp.class) : parm
//					.getTimestamp("IN_DATE", i));
//			result.setData("ICU_OUT_DATE" + count, parm.getTimestamp(
//					"OUT_DATE", i) == null ? new TNull(Timestamp.class) : parm
//					.getTimestamp("OUT_DATE", i));
//			count++;
//		}
		result.setData("NB_ADM_WEIGHT",this.getValue("TP2_NB_ADM_WEIGHT"));//��������Ժ����
		result.setData("NB_WEIGHT",this.getValue("TP2_NB_WEIGHT"));//��������
		
		result.setData("IN_DEPT", this.getValue("TP2_IN_DEPT")); // ��Ժ����
		result.setData("VS_NURSE_CODE", this.getValue("TP2_VS_NURSE_CODE")); // ���λ�ʿ
		result.setData("OUT_TYPE", this.getValue("TP2_OUT_TYPE")); // ��Ժ��ʽ
		result.setData("TRAN_HOSP", this.getValue("TP2_TRAN_HOSP")); // ��תԺ��
		// 20120813 shibl modify
		result.setData("TRAN_HOSP_OTHER", this.getValue("TRAN_HOSP_OTHER")); // ��תԺ����������
		String beday = getFormatString(this.getValueString("TP2_BE_IN_D"));
		String behour = getFormatString(this.getValueString("TP2_BE_IN_H"));
		String bemin = getFormatString(this.getValueString("TP2_BE_IN_M"));
		result.setData("BE_COMA_TIME", beday + behour + bemin); // ��Ժǰ����ʱ��
		String afday = getFormatString(this.getValueString("TP2_AF_IN_D"));
		String afhour = getFormatString(this.getValueString("TP2_AF_IN_H"));
		String afmin = getFormatString(this.getValueString("TP2_AF_IN_M"));
		result.setData("AF_COMA_TIME", afday + afhour + afmin); // ��Ժ�����ʱ��
		if (this.getCheckbox("TP2_AGN_PLAN_FLG").isSelected()) {
			result.setData("AGN_PLAN_FLG", "Y");
		} else {
			result.setData("AGN_PLAN_FLG", "N"); // 31����סԺ�ƻ����
		}
		result.setData("AGN_PLAN_INTENTION", this
				.getValue("TP2_AGN_PLAN_INTENTION")); // 31����סԺ�ƻ�
		/****************************** ADD END ************************************************/
		result.setData("OUT_DEPT", this.getValue("TP2_OUT_DEPT")); // ��Ժ����
		result.setData("IN_CONDITION", this.getValue("TP2_IN_CONDITION")); // ��Ժ���
		if (this.getValue("TP2_OUT_DATE") == null)
			result.setData("OUT_DATE", new TNull(Timestamp.class));
		else
			result.setData("OUT_DATE", this.getValue("TP2_OUT_DATE")); // ��Ժʱ��
		result.setData("ISMODIFY_DSDATE", this
				.IsModifydsDate((Timestamp) getValue("TP2_OUT_DATE")));

		if (this.getValue("TP2_CONFIRM_DATE") == null)
			result.setData("CONFIRM_DATE", new TNull(Timestamp.class));
		else
			result.setData("CONFIRM_DATE", this.getValue("TP2_CONFIRM_DATE")); // ȷ������
		result.setData("REAL_STAY_DAYS", this.getValue("TP2_REAL_STAY_DAYS"));// ʵ��סԺ����
		result.setData("PATHOLOGY_DIAG", this.getValue("TP22_PATHOLOGY_DIAG")); // �������1
		// ҽԺ��Ⱦ�ܴ���
		result.setData("INFECT_COUNT", this.getValue("INFECT_COUNT"));
		// 20120113 add shibl
		result.setData("PATHOLOGY_NO", this.getValue("TP2_PATHOLOGY_NO")); // �����1
		result
				.setData("PATHOLOGY_DIAG2", this
						.getValue("TP22_PATHOLOGY_DIAG2")); // �������2
		result.setData("PATHOLOGY_NO2", this.getValue("TP2_PATHOLOGY_NO2")); // �����2
		result
				.setData("PATHOLOGY_DIAG3", this
						.getValue("TP22_PATHOLOGY_DIAG3")); // �������3
		result.setData("PATHOLOGY_NO3", this.getValue("TP2_PATHOLOGY_NO3")); // �����3

		result.setData("DIF_DEGREE", this.getValue("DIF_DEGREE"));// �ֻ��̶�
		result.setData("MDIAG_BASIS", this.getValue("MDIAG_BASIS"));// ����������

		result.setData("EX_RSN", this.getValue("TP22_EX_RSN")); // �����ж��ⲿ����1
		result.setData("EX_RSN2", this.getValue("TP22_EX_RSN2")); // �����ж��ⲿ����2
		result.setData("EX_RSN3", this.getValue("TP22_EX_RSN3")); // �����ж��ⲿ����3

		result.setData("ALLEGIC", this.getValue("TP22_ALLEGIC") == null ? "":this.getValue("TP22_ALLEGIC")); // ҩ�����
		if (getRDBtn("ALLEGIC_1").isSelected())
			result.setData("ALLEGIC_FLG", "1");
		else if (getRDBtn("ALLEGIC_2").isSelected())
			result.setData("ALLEGIC_FLG", "2");
		else
			result.setData("ALLEGIC_FLG", "");

		result.setData("GET_TIMES", this.getValue("TP22_GET_TIMES")); // ���ȴ���
		result.setData("SUCCESS_TIMES", this.getValue("TP22_SUCCESS_TIMES")); // �ɹ�����
		result.setData("DIRECTOR_DR_CODE", this
				.getValue("TP22_DIRECTOR_DR_CODE")); // ������
		result.setData("PROF_DR_CODE", this.getValue("TP22_PROF_DR_CODE")); // ��(����)��ҽʦ
		result.setData("ATTEND_DR_CODE", this.getValue("TP22_ATTEND_DR_CODE")); // ����ҽʦ
		result.setData("VS_DR_CODE", this.getValue("TP2_VS_DR_CODE")); // סԺҽʦ
		result.setData("INDUCATION_DR_CODE", this
				.getValue("TP22_INDUCATION_DR_CODE")); // ����ҽʦ
		result.setData("GRADUATE_INTERN_CODE", this
				.getValue("TP22_GRADUATE_INTERN_CODE")); // �о���ʵϰҽʦ
		result.setData("INTERN_DR_CODE", this.getValue("TP22_INTERN_DR_CODE")); // ʵϰҽʦ
		String lineStr = "";
		if (getCheckbox("OPE_TYPE_CODE1").isSelected())
			lineStr = "1";
		if (getCheckbox("OPE_TYPE_CODE2").isSelected()) {
			if (lineStr.length() > 0) {
				lineStr += ",2";
			} else {
				lineStr = "2";
			}
		}
		
		result.setData("DAY_OPE_FLG", this.getValue("DAY_OPE_FLG")); // �ռ����� add by huangtt 20161222

		result.setData("OPE_TYPE_CODE", lineStr.equals("") ? "0" : lineStr);// ������������
		result.setData("RBC", this.getValue("TP23_RBC")); // ��ϸ��
		result.setData("PLATE", this.getValue("TP23_PLATE")); // ѪС��
		result.setData("PLASMA", this.getValue("TP23_PLASMA")); // Ѫ��
		result.setData("WHOLE_BLOOD", this.getValue("TP23_WHOLE_BLOOD")); // ȫ Ѫ
		result.setData("BANKED_BLOOD", this.getValue("TP23_BANKED_BLOOD")); // ȫ�����
		result.setData("OTH_BLOOD", this.getValue("TP23_OTH_BLOOD")); // �� ��
		result.setData("ACCOMPANY_YEAR", this.getValue("TP24_ACCOMPANY_YEAR")); // ��������
		result
				.setData("ACCOMPANY_MONTH", this
						.getValue("TP24_ACCOMPANY_MONTH")); // ��������
		result.setData("ACCOMPANY_WEEK", this.getValue("TP24_ACCOMPANY_WEEK")); // ��������
		setACCOMP_DATE();// ������������
		if (this.getValue("TP24_ACCOMP_DATE") == null)
			result.setData("ACCOMP_DATE", new TNull(Timestamp.class));
		else
			result.setData("ACCOMP_DATE", this.getValue("TP24_ACCOMP_DATE")); // ��Ժʱ��

		// result.setData("",this.getValue(""));//�ٴ�·�� ��ȱ
		// result.setData("",this.getValue(""));//�ٴ�·������ ��ȱ
		// ��ѡ��ȡֵ
		// HBsAg
		if (getRDBtn("HBsAg_1").isSelected())
			result.setData("HBSAG", "0");
		else if (getRDBtn("HBsAg_2").isSelected())
			result.setData("HBSAG", "1");
		else if (getRDBtn("HBsAg_3").isSelected())
			result.setData("HBSAG", "2");
		else
			result.setData("HBSAG", "");
		// HCV-Ab
		if (getRDBtn("HCV-Ab_1").isSelected())
			result.setData("HCV_AB", "0");
		else if (getRDBtn("HCV-Ab_2").isSelected())
			result.setData("HCV_AB", "1");
		else if (getRDBtn("HCV-Ab_3").isSelected())
			result.setData("HCV_AB", "2");
		else
			result.setData("HCV_AB", "");
		// HCV-Ab
		if (getRDBtn("HIV-Ab_1").isSelected())
			result.setData("HIV_AB", "0");
		else if (getRDBtn("HIV-Ab_2").isSelected())
			result.setData("HIV_AB", "1");
		else if (getRDBtn("HIV-Ab_3").isSelected())
			result.setData("HIV_AB", "2");
		else
			result.setData("HIV_AB", "");
		// ������סԺ
		if (getRDBtn("TP22_myc1").isSelected())
			result.setData("QUYCHK_OI", "0");
		else if (getRDBtn("TP22_myc2").isSelected())
			result.setData("QUYCHK_OI", "1");
		else if (getRDBtn("TP22_myc3").isSelected())
			result.setData("QUYCHK_OI", "2");
		else if (getRDBtn("TP22_myc4").isSelected())
			result.setData("QUYCHK_OI", "3");
		else
			result.setData("QUYCHK_OI", "");
		// ��Ժ���Ժ
		if (getRDBtn("TP22_ryc1").isSelected())
			result.setData("QUYCHK_INOUT", "0");
		else if (getRDBtn("TP22_ryc2").isSelected())
			result.setData("QUYCHK_INOUT", "1");
		else if (getRDBtn("TP22_ryc3").isSelected())
			result.setData("QUYCHK_INOUT", "2");
		else if (getRDBtn("TP22_ryc4").isSelected())
			result.setData("QUYCHK_INOUT", "3");
		else
			result.setData("QUYCHK_INOUT", "");
		// ��ǰ������
		if (getRDBtn("TP22_sys1").isSelected())
			result.setData("QUYCHK_OPBFAF", "0");
		else if (getRDBtn("TP22_sys2").isSelected())
			result.setData("QUYCHK_OPBFAF", "1");
		else if (getRDBtn("TP22_sys3").isSelected())
			result.setData("QUYCHK_OPBFAF", "2");
		else if (getRDBtn("TP22_sys4").isSelected())
			result.setData("QUYCHK_OPBFAF", "3");
		else
			result.setData("QUYCHK_OPBFAF", "");
		// �ٴ��벡��
		if (getRDBtn("TP22_lyb1").isSelected())
			result.setData("QUYCHK_CLPA", "0");
		else if (getRDBtn("TP22_lyb2").isSelected())
			result.setData("QUYCHK_CLPA", "1");
		else if (getRDBtn("TP22_lyb3").isSelected())
			result.setData("QUYCHK_CLPA", "2");
		else if (getRDBtn("TP22_lyb4").isSelected())
			result.setData("QUYCHK_CLPA", "3");
		else
			result.setData("QUYCHK_CLPA", "");
		// �����벡��
		if (getRDBtn("TP22_fyb1").isSelected())
			result.setData("QUYCHK_RAPA", "0");
		else if (getRDBtn("TP22_fyb2").isSelected())
			result.setData("QUYCHK_RAPA", "1");
		else if (getRDBtn("TP22_fyb3").isSelected())
			result.setData("QUYCHK_RAPA", "2");
		else if (getRDBtn("TP22_fyb4").isSelected())
			result.setData("QUYCHK_RAPA", "3");
		else
			result.setData("QUYCHK_RAPA", "");
		// Ѫ��
		if (getRDBtn("TP23XX1").isSelected())
			result.setData("BLOOD_TYPE", "1");
		else if (getRDBtn("TP23XX2").isSelected())
			result.setData("BLOOD_TYPE", "2");
		else if (getRDBtn("TP23XX3").isSelected())
			result.setData("BLOOD_TYPE", "3");
		else if (getRDBtn("TP23XX4").isSelected())
			result.setData("BLOOD_TYPE", "4");
		else if (getRDBtn("TP23XX5").isSelected())
			result.setData("BLOOD_TYPE", "5");// ����
		else if (getRDBtn("TP23XX6").isSelected())
			result.setData("BLOOD_TYPE", "6");// δ��
		// ��Ѫ��ӳ
		if (getRDBtn("PT23SX1").isSelected())
			result.setData("TRANS_REACTION", "1");
		else if (getRDBtn("PT23SX2").isSelected())
			result.setData("TRANS_REACTION", "2");
		else
			result.setData("TRANS_REACTION", "0");
		// RH
		if (getRDBtn("PT23RH1").isSelected())
			result.setData("RH_TYPE", "1");
		else if (getRDBtn("PT23RH2").isSelected())
			result.setData("RH_TYPE", "2");
		else if (getRDBtn("PT23RH3").isSelected())
			result.setData("RH_TYPE", "3");
		else if (getRDBtn("PT23RH4").isSelected())
			result.setData("RH_TYPE", "4");
		// ʬ��
		result.setData("BODY_CHECK", this.getValue("BODY_CHECK"));// wanglong modify 20141030
		// ��Ժ��һ��
		if (getRDBtn("PT24BY1").isSelected())
			result.setData("FIRST_CASE", "1");
		else if (getRDBtn("PT24BY2").isSelected())
			result.setData("FIRST_CASE", "2");
		else
			result.setData("FIRST_CASE", "");
		// ʾ�̲���
		if (getRDBtn("PT24SJBL1").isSelected())
			result.setData("SAMPLE_FLG", "1");
		else if (getRDBtn("PT24SJBL2").isSelected())
			result.setData("SAMPLE_FLG", "2");
		else
			result.setData("SAMPLE_FLG", "");
		// ��Ҫ����
		result.setData("MR_NO", this.getValue("TP1_MR_NO"));
		result.setData("CASE_NO", this.getValue("TP1_CASE_NO"));
		result.setData("IPD_NO", this.getValue("TP1_IPD_NO"));
		// ҽʦ�ύ��ʶ
		if (((TCheckBox) this.getComponent("DIAGCHK_FLG")).isSelected())
			result.setData("DIAGCHK_FLG", "Y");
		else
			result.setData("DIAGCHK_FLG", "N");
		boolean flg = false;
		// ��ȡ��������Ϣ
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		if (opTable.getRowCount() > 0) {
			int row = -1;
			for (int i = 0; i < opTable.getRowCount(); i++) {
				// �ж�������
				if ("Y".equals(opTable.getValueAt(i, 0))) {
					flg = true;
					row = i;
					break;
				}
			}
			if (flg) {
				result.setData("AGN_FLG", opTable.getValueAt(row, 1));// �ط����
				result.setData("OP_DATE", StringTool.getString(
						(Timestamp) opTable.getValueAt(row, 2),
						"yyyyMMddHHmmss"));// ��������
				result.setData("OP_CODE", opTable.getValueAt(row, 3));// ����CODE
				
				//add by yangjj 20150811
				Object opLevel = opTable.getValueAt(row, 5);
				if(opLevel == null){
					result.setData("OP_LEVEL", "");// �����ȼ�
				}else {
					result.setData("OP_LEVEL", opLevel);
				}
				
				
				//result.setData("OP_LEVEL", opTable.getValueAt(row, 5));// �����ȼ�
				result.setData("MAIN_SUGEON", opTable.getValueAt(row, 9));// ����ҽʦ
				result.setData("HEAL_LV",
						opTable.getValueAt(row, 15) == null ? "" : opTable
								.getValueAt(row, 15));// �ָ��ȼ� modify by wanglong 20130802 ��14��Ϊ15
			} else {
				result.setData("OP_CODE", "");
				result.setData("OP_DATE", "");
				result.setData("MAIN_SUGEON", "");
				result.setData("OP_LEVEL", "");
				result.setData("HEAL_LV", "");
				result.setData("AGN_FLG", "");
			}

		} else {
			result.setData("OP_CODE", "");
			result.setData("OP_DATE", "");
			result.setData("MAIN_SUGEON", "");
			result.setData("OP_LEVEL", "");
			result.setData("HEAL_LV", "");
			result.setData("AGN_FLG", "");
		}
		result.setData("OPT_USER", OPT_USER);
		result.setData("OPT_TERM", OPT_TERM);

		/*
		 * ������ҳ�У����ֶ��ٴ����鲡���ֵ䡢��ѧ�����ֵ�ͬʱ�� ���ϲ�ѯ���ܣ�����ά���������ֶ� Modify ZhenQin -
		 * 2011-05-09 and 2011-05-18
		 */

		result.setData("TEST_EMR", this.getValue("TEST_EMR"));
		result.setData("TEACH_EMR", this.getValueString("TEACH_EMR"));   //modify by wukai 20160602
		result.setData("CLNCPATH_CODE", this.getValue("CLNCPATH_CODE"));
		result.setData("DISEASES_CODE", this.getValue("DISEASES_CODE"));
		
		//modify by zhangh 2013-10-11 ���ӵڶ�ҳǩ����Сҳǩ  �໤����ס���
		TTable icuTable = (TTable) this.getComponent("TP25_ICU_Table");
		icuTable.acceptText();
		if(icuTable.getRowCount() > 0){
			int count = 1;
			for (int j = 0; j < icuTable.getRowCount(); j++) {
				result.setData("ICU_ROOM" + count, icuTable.getValueAt_(j, 0)
						== null ? "" : icuTable.getValueAt_(j, 0));
				result.setData("ICU_IN_DATE" + count, icuTable.getValueAt_(j, 1)
						== null ? new TNull(Timestamp.class) : icuTable.getValueAt_(j, 1));
				result.setData("ICU_OUT_DATE" + count, icuTable.getValueAt_(j, 2)
						== null ? new TNull(Timestamp.class) : icuTable.getValueAt_(j, 2));
				count++;
			}
		}
		result.setData("VENTI_TIME", this.getValue("VENTI_TIME"));
		
		// add by wangqing 20180116  start ����������ʹ�÷��ӡ�������ʹ����
		result.setData("VENTI_TIME_MIN", this.getValue("VENTI_TIME_MIN"));
		result.setData("VENTI_TIME_SEC", this.getValue("VENTI_TIME_SEC"));
		// add by wangqing 20180116  end
		
		return result;
	}

	/**
	 * �������ҳǩ������
	 * 
	 * @return TParm
	 */
	public TParm getFourPageInfo() {
		TParm parm = new TParm();
		// ��Ⱦ������
		if (getRDBtn("TP4CR1").isSelected())
			parm.setData("INFECT_REPORT", "1");
		else if (getRDBtn("TP4CR2").isSelected())
			parm.setData("INFECT_REPORT", "2");
		else
			parm.setData("INFECT_REPORT", "");
		// �Ĳ�����
		if (getRDBtn("TP4SB1").isSelected())
			parm.setData("DIS_REPORT", "1");
		else if (getRDBtn("TP4SB2").isSelected())
			parm.setData("DIS_REPORT", "2");
		else
			parm.setData("DIS_REPORT", "");
		// ��������
		if (getRDBtn("TP4BA1").isSelected())
			parm.setData("QUALITY", "1");
		else if (getRDBtn("TP4BA2").isSelected())
			parm.setData("QUALITY", "2");
		else if (getRDBtn("TP4BA3").isSelected())
			parm.setData("QUALITY", "3");
		else
			parm.setData("QUALITY", "");
		// �ʿ�ҽʦ
		parm.setData("CTRL_DR", this.getValue("TP4_CTRL_DR"));
		// �ʿػ�ʿ
		parm.setData("CTRL_NURSE", this.getValue("TP4_CTRL_NURSE"));
		// ����
		if (this.getValue("TP4_CTRL_DATE") == null)
			parm.setData("CTRL_DATE", new TNull(Timestamp.class));
		else
			parm.setData("CTRL_DATE", this.getValue("TP4_CTRL_DATE"));
		// ����Ա
		parm.setData("ENCODER", this.getValue("TP4_ENCODER"));
		parm.setData("MR_NO", this.getValue("TP1_MR_NO"));
		parm.setData("CASE_NO", this.getValue("TP1_CASE_NO"));
		parm.setData("IPD_NO", this.getValue("TP1_IPD_NO"));
		// �������ύ��ʶ
		if (((TCheckBox) this.getComponent("QTYCHK_FLG")).isSelected())
			parm.setData("QTYCHK_FLG", "Y");
		else
			parm.setData("QTYCHK_FLG", "N");

		// �����ύ��ʶ�����ڵ���ҳǩ���ݣ�û�е����ı��淽�����������ҳǩ���棩
		if (((TCheckBox) this.getComponent("BILCHK_FLG")).isSelected())
			parm.setData("BILCHK_FLG", "Y");
		else
			parm.setData("BILCHK_FLG", "N");
		parm.setData("OPT_USER", OPT_USER);
		parm.setData("OPT_TERM", OPT_TERM);
		return parm;
	}

	/**
	 * ��ȡ��ҳ�ĵ�ѡ��
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRDBtn(String tag) {
		TRadioButton rbt = (TRadioButton) this.getComponent(tag);
		return rbt;
	}

	/**
	 * ��ȡ����������Ϣ
	 * 
	 * @param MR_NO
	 *            String ������
	 * @param CASE_NO
	 *            String �������
	 */
	private void OPTableBind(String MR_NO, String CASE_NO) {
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
//		TDataStore OP_Info = table.getDataStore();
//		// ��ȡSQL���
		String sql = MROSqlTool.getInstance().getOPSelectSQL(MR_NO, CASE_NO);
//		OP_Info.setSQL(sql);
//		OP_Info.retrieve();
//		table.setDSValue();
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() <= 0){
			table.removeRowAll();
			return;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			//�����������ʱ��(Сʱ�ͷ���)
			if(parm.getData("OPE_TIME", i) != null && 
					!"".equals(parm.getData("OPE_TIME", i).toString())){
				String[] arr;
				String ope_hour="",ope_minute="";
				String opt_time = parm.getData("OPE_TIME", i).toString();
				arr = opt_time.split("|");
				int index;
				for (int j = 0; j < arr.length; j++) {
					if(arr[j].equals("|")){
						index = j;
						for (int j2 = 0; j2 < index; j2++) {
							ope_hour += arr[j2];
						}
						for (int j3 = index + 1; j3 < arr.length; j3++) {
							ope_minute += arr[j3];
						}
						break;
					}
				}
				parm.setData("OPE_TIME_HOUR", i, ope_hour);
				parm.setData("OPE_TIME_MINUTE", i, ope_minute);
			}
			//�жϱ������Ժҽʦ��ע�ֶ��Ƿ��д
			if(parm.getData("MAIN_SUGEON", i) != null && 
					parm.getData("MAIN_SUGEON", i).toString().length() > 0){
				String docId = parm.getData("MAIN_SUGEON", i).toString();
				TParm result = MRORecordTool.getInstance().isOutDoc(docId);
				if(result.getCount() <= 0){
					table.setLockCell(i, 10, true);
					continue;
				}
				if(result.getData("IS_OUT_FLG", 0) != null && 
						"Y".equals(result.getData("IS_OUT_FLG", 0).toString())){
			    	table.setLockCell(i, 10, false);
			    	continue;
			    }
			}
			table.setLockCell(i, 10, true);
		}
		table.setParmValue(parm);
	}

	/**
	 * ���������Ϣ
	 */
	public void addOP() {
		/*TTable table = (TTable) this.getComponent("TP23_OP_Table");
		table.acceptText();
		TDataStore OP_Info = table.getDataStore();
		int MAX_SEQ = 0;
		// ��ȡ������
		MAX_SEQ = getMaxSeq(OP_Info, "SEQ_NO");
		MAX_SEQ = getMaxSeq();
		// ��������
		int row = table.addRow();
		// ��datastore����Ӧ�������� ����������
		OP_Info.setItem(row, "SEQ_NO", MAX_SEQ);
		// ��ʼ�����ڸ�ʽ
		// table.setValueAt(SystemTool.getInstance().getDate(),row,0);
		table.setSelectedRow(row);*/
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		// ��������
		int row = table.addRow();
		table.setSelectedRow(row);
		table.setLockCell(row, 10, true);
//		this.messageBox(table.getParmValue().getData("SEQ_NO", row)+"");
	}

	/**
	 * �Ƴ�������Ϣ
	 */
	public void removeOP() {
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		// �ж��Ƿ���ѡ����
		if (table.getTable().getSelectedRow() < 0)
			return;
		// ɾ��ѡ����
		table.removeRow(table.getSelectedRow());
		table.clearSelection();
	}

	/**
	 * ��ȡ ������Ϣ�Ķ����ɵ�SQL���
	 * 
	 * @return String[]
	 */
	public String[] getOPSQL() {
		Timestamp date = StringTool.getTimestamp(new Date());
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		table.acceptText();
		TDataStore OP_Info = table.getDataStore();
		// ���ȫ���Ķ����к�
		int rows[] = OP_Info.getModifiedRows(OP_Info.PRIMARY);
		// ���̶�����������
		for (int i = 0; i < rows.length; i++) {
			// System.out.println("---------------"+rows[i]);
			OP_Info.setItem(rows[i], "CASE_NO", CASE_NO);
			OP_Info.setItem(rows[i], "MR_NO", MR_NO);
			OP_Info.setItem(rows[i], "IPD_NO", getValue("TP1_IPD_NO"));// סԺ��
			OP_Info.setItem(rows[i], "OPT_USER", Operator.getID());
			OP_Info.setItem(rows[i], "OPT_DATE", date);
			OP_Info.setItem(rows[i], "OPT_TERM", Operator.getIP());
		}
		// ��ȡ��Ҫִ�е�SQL
		String[] upSQL = OP_Info.getUpdateSQL();
		return upSQL;
	}
	
	/**
	 * ����mro_record_op����
	 * @return �����б�
	 */
	private TParm insertMRO_Record_OP(){
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		TParm parm = new TParm();
		if(table.getRowCount() <= 0){
			parm.addData("CASE_NO", CASE_NO);
			return parm;
		}
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("CASE_NO", CASE_NO);
			parm.addData("SEQ_NO", i + 1);
			parm.addData("IPD_NO", getValue("TP1_IPD_NO"));
			parm.addData("MR_NO", MR_NO);
			parm.addData("OP_CODE", table.getValueAt(i, 3) == null ? "" : table.getValueAt(i, 3));
			parm.addData("OP_DESC", table.getValueAt(i, 4) == null ? "" : table.getValueAt(i, 4));
			parm.addData("OP_REMARK", "");
			parm.addData("OP_DATE", table.getValueAt(i, 2) == null ? "" : table.getValueAt(i, 2).toString().length() > 0 ? table.getValueAt(i, 2).toString().substring(0, table.getValueAt(i, 2).toString().
					lastIndexOf(".")).replace("-", "").replace(" ", "").replace(":", "") : "");
			parm.addData("ANA_WAY", table.getValueAt(i, 14) == null ? "" : table.getValueAt(i, 14));
			parm.addData("ANA_DR", table.getValueAt(i, 16) == null ? "" : table.getValueAt(i, 16));
			parm.addData("MAIN_SUGEON", table.getValueAt(i, 9) == null ? "" : table.getValueAt(i, 9));
			parm.addData("MAIN_SUGEON_REMARK", table.getValueAt(i, 10) == null ? "" : table.getValueAt(i, 10));
			parm.addData("AST_DR1", table.getValueAt(i, 11) == null ? "" : table.getValueAt(i, 11));
			parm.addData("AST_DR2", table.getValueAt(i, 12) == null ? "" : table.getValueAt(i, 12));
			parm.addData("HEALTH_LEVEL", table.getValueAt(i, 15) == null ? "" : table.getValueAt(i, 15));
			parm.addData("OP_LEVEL", table.getValueAt(i, 5) == null ? "" : table.getValueAt(i, 5));
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("MAIN_FLG", table.getValueAt(i, 0) == null ? "" : table.getValueAt(i, 0));
			parm.addData("AGN_FLG", table.getValueAt(i, 1) == null ? "" : table.getValueAt(i, 1));
			parm.addData("OPE_SITE", table.getValueAt(i, 6) == null ? "" : table.getValueAt(i, 6));
			//������������ʱ��
			String ope_hour,ope_minute;
			if(table.getValueAt(i, 7) != null && !table.getValueAt(i, 7).toString().trim().equals("")){
				ope_hour = table.getValueAt(i, 7).toString();
			}else{
				ope_hour = "0";
			}
			if(table.getValueAt(i, 8) != null && !table.getValueAt(i, 8).toString().trim().equals("")){
				ope_minute = table.getValueAt(i, 8).toString();
			}else{
				ope_minute = "0";
			}
			parm.addData("OPE_TIME", ope_hour + "|" + ope_minute);
			parm.addData("ANA_LEVEL", table.getValueAt(i, 13) == null ? "" : table.getValueAt(i, 13));
			//zhangh 2013-5-13 �������յȼ�
			int opeHour = 0,nnisCode = 0,anaLevel = 0;
			String healthLevel = "";
			opeHour = Integer.parseInt(ope_hour);
			if(opeHour >= 3)
				nnisCode++;
			if(table.getValueAt(i, 15) != null && !table.getValueAt(i, 15).toString().trim().equals("")){
				healthLevel = table.getValueAt(i, 15).toString();
				int healthLevelFirst = Integer.parseInt(healthLevel.substring(0, 1));
				switch (healthLevelFirst) {
				case 3:
				case 4:
					nnisCode++;
					break;
				}
			}
			if(table.getValueAt(i, 13) != null && !table.getValueAt(i, 13).toString().trim().equals("")){
				anaLevel = Integer.parseInt(table.getValueAt(i, 13).toString());
				switch (anaLevel) {
				case 3:
				case 4:
				case 5:
				case 6:
					nnisCode++;
					break;
				}
			}
			parm.addData("NNIS_CODE", nnisCode);
		}
		return parm;
	}

	/**
	 * ģ����ѯ���ڲ��ࣩ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * ��ȡ���Grid���ݣ��ɵģ�
	 * 
	 * @return TParm
	 */
	private TParm getDiagGridData() {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm result = new TParm();
		TParm tableData = table.getParmValue();
		int outIndex = 2;// ��Ժ����� ��2��Ϊ��ʼֵ ��Ϊ��Ժ�������1
		int oeIndex = 2;// �ż�������� ��2��Ϊ��ʼֵ ��Ϊ�ż����������1
		int inIndex = 2;// ��Ժ����� ��2��Ϊ��ʼֵ ��Ϊ��Ժ�������1
		for (int i = 0; i < tableData.getCount(); i++) {
			// �ж��Ƿ����ż������
			if ("I".equals(tableData.getValue("TYPE", i))) {
				// �ż������ICD_CODE
				if ("Y".equals(tableData.getValue("MAIN", i))) {// �ж��Ƿ��������
					result.setData("OE_DIAG_CODE", tableData
							.getValue("CODE", i));
				} else {
					result.setData("OE_DIAG_CODE" + oeIndex, tableData
							.getValue("CODE", i));
					oeIndex++;
				}
			} else if ("M".equals(tableData.getValue("TYPE", i))) {// �ж��Ƿ�����Ժ���
				// ��Ժ���ICD_CODE
				if ("Y".equals(tableData.getValue("MAIN", i))) {// �ж��Ƿ��������
					result.setData("IN_DIAG_CODE", tableData
							.getValue("CODE", i));
				} else {
					result.setData("IN_DIAG_CODE" + inIndex, tableData
							.getValue("CODE", i));
					inIndex++;
				}
			} else if ("Q".equals(tableData.getValue("TYPE", i))) {// �ж��Ƿ���Ժ�ڸ�Ⱦ���
				// Ժ�ڸ�Ⱦ���ICD_CODE
				result.setData("INTE_DIAG_CODE", tableData.getValue("CODE", i));
				result.setData("INTE_DIAG_STATUS", tableData.getValue("STATUS",
						i));// ת��
				result.setData("INTE_DIAG_CONDITION", tableData.getValue(
						"IN_PAT_CONDITION", i));// ��Ժ����
			} else if ("W".equals(tableData.getValue("TYPE", i))) {// �ж��Ƿ���Ժ�ڲ������
				// Ժ�ڲ������ICD_CODE
				result.setData("COMPLICATION_DIAG", tableData.getValue("CODE",
						i));
				result.setData("COMPLICATION_STATUS", tableData.getValue(
						"STATUS", i));// ת��
				result.setData("COMPLICATION_DIAG_CONDITION", tableData
						.getValue("IN_PAT_CONDITION", i));// ��Ժ����
			} else if ("O".equals(tableData.getValue("TYPE", i))) {// �ж��Ƿ��ǳ�Ժ���
				if ("Y".equals(tableData.getValue("MAIN", i))) {// �ж��Ƿ��������
					result.setData("OUT_DIAG_CODE1", tableData.getValue("CODE",
							i));// ICD_10
					result.setData("CODE1_REMARK", tableData.getValue("REMARK",
							i));// ��ע
					result.setData("CODE1_STATUS", tableData.getValue("STATUS",
							i));// ת��
					result.setData("ADDITIONAL_CODE1", tableData.getValue(
							"ADDITIONAL", i));// ������
					result.setData("OUT_DIAG_CONDITION1", tableData.getValue(
							"IN_PAT_CONDITION", i));// ��Ժ����
				} else {
					result.setData("OUT_DIAG_CODE" + outIndex, tableData
							.getValue("CODE", i));// ICD_10
					result.setData("CODE" + outIndex + "_REMARK", tableData
							.getValue("REMARK", i));// ��ע
					result.setData("CODE" + outIndex + "_STATUS", tableData
							.getValue("STATUS", i));// ת��
					result.setData("ADDITIONAL_CODE" + outIndex, tableData
							.getValue("ADDITIONAL", i));// ������
					result.setData("OUT_DIAG_CONDITION" + outIndex, tableData
							.getValue("IN_PAT_CONDITION", i));// ��Ժ����
					outIndex++;
				}
			}
		}
		result.setData("CASE_NO", CASE_NO);// CASE_NO�������
		return result;
	}

	/**
	 * ��ȡ���Grid����
	 * 
	 * @return TParm
	 */
	private TParm getNewDiagGridData() {
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm result = new TParm();
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		TParm tableData = table.getParmValue();
		int count = 0;
		for (int i = 0; i < tableData.getCount(); i++) {
			if (!tableData.getValue("CODE", i).equals("")) {
				result.addData("CASE_NO", CASE_NO);// CASE_NO�������
				result.addData("MR_NO", this.MR_NO);// MR_NO�������
				result.addData("IPD_NO", this.getValue("TP1_IPD_NO"));// IPD_NO�������
				result.addData("IO_TYPE", tableData.getValue("TYPE", i));// ����
				result.addData("ICD_KIND", tableData.getValue("KIND", i));// ��ϱ���
				result.addData("MAIN_FLG", tableData.getValue("MAIN", i)
						.equals("Y") ? tableData.getValue("MAIN", i) : "N");// ��
				result.addData("ICD_CODE", tableData.getValue("CODE", i));// ��ϱ���
				//add by wukai on 20170406 �����޸ĺ��SEQ_NO
				result.addData("OLD_IO_TYPE", tableData.getValue("OLD_IO_TYPE", i));
				result.addData("OLD_ICD_CODE", tableData.getValue("OLD_ICD_CODE", i));
				//add by wukai on 20170406 �����޸ĺ��SEQ_NO
				result.addData("SEQ_NO", tableData.getValue("SEQ_NO", i));// ���
				result.addData("ICD_DESC", orderDesc
						.getTableShowValue(tableData.getValue("NAME", i)));// �������
				result.addData("ICD_REMARK", tableData.getValue("REMARK", i));// ��ע
				result.addData("ICD_STATUS", tableData.getValue("STATUS", i));// ת��
				result.addData("ADDITIONAL_CODE", tableData.getValue(
						"ADDITIONAL", i));// ������
				result.addData("ADDITIONAL_DESC", tableData.getValue(
						"ADDITIONAL_DESC", i));// �����������
				result.addData("IN_PAT_CONDITION", table.getValueAt(i, 6));// ��Ժ����
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_TERM", Operator.getIP());
				result.addData("EXEC", tableData.getValue("EXEC", i));// ��ʶ
				count++;
			}
		}
		return result;
	}

	/**
	 * ��ղ������
	 */
	public void onclearPatDiag() {
		this.clearValue("TP22_PATHOLOGY_DIAG;PATHOLOGY_DIAG_DESC");
	}

	/**
	 * ��ղ������
	 */
	public void onclearPatDiag2() {
		this.clearValue("TP22_PATHOLOGY_DIAG2;PATHOLOGY_DIAG_DESC2");
	}

	/**
	 * ��ղ������
	 */
	public void onclearPatDiag3() {
		this.clearValue("TP22_PATHOLOGY_DIAG3;PATHOLOGY_DIAG_DESC3");
	}

	/**
	 * ��ղ�������
	 */
	public void onClearEsx() {
		this.clearValue("TP22_EX_RSN;EX_RSN_DESC");
	}

	/**
	 * ��ղ�������
	 */
	public void onClearEsx2() {
		this.clearValue("TP22_EX_RSN2;EX_RSN_DESC2");
	}

	/**
	 * ��ղ�������
	 */
	public void onClearEsx3() {
		this.clearValue("TP22_EX_RSN3;EX_RSN_DESC3");
	}

	/**
	 * ��������Ϣ��д�Ƿ���Ϲ淶
	 * 
	 * @return String ���ء�����ʾ���ϱ�׼�� ������Ϣ��ʾ������ ����Ϣ��ʾ����
	 */
	private String checkDiagGrid() {
		String message = "";
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm data = table.getParmValue();
		int I_NUM = 0;// �ż��������
		int I_MainNum = 0;// �ż����������
		int M_NUM = 0;// ��Ժ�����
		int M_MainNum = 0;// ��Ժ�������
		int O_NUM = 0;// ��Ժ�����
		int Q_NUM = 0;// Ժ�ڸ�Ⱦ�����
		int W_NUM = 0;// Ժ�ڲ��������
		int O_MainNum = 0;// ��Ժ�������
		for (int i = 0; i < data.getCount(); i++) {
			if (data.getValue("CODE", i).trim().length() > 0
					&& data.getValue("TYPE", i).length() <= 0) {
				message = "��ѡ���������";
			}
			// �ж��Ƿ����ż������
			if ("I".equals(data.getValue("TYPE", i))) {
				I_NUM++;
				if ("Y".equals(data.getValue("MAIN", i))) {// �ж��Ƿ��������
					I_MainNum++;
				}
			} else if ("M".equals(data.getValue("TYPE", i))) {// �ж��Ƿ�����Ժ���
				M_NUM++;
				if ("Y".equals(data.getValue("MAIN", i))) {// �ж��Ƿ��������
					M_MainNum++;
				}
			} else if ("Q".equals(data.getValue("TYPE", i))) {// �ж��Ƿ���Ժ�ڸ�Ⱦ���
				Q_NUM++;
			} else if ("W".equals(data.getValue("TYPE", i))) {// �ж��Ƿ���Ժ�ڲ������
				W_NUM++;
			} else if ("O".equals(data.getValue("TYPE", i))) {// �ж��Ƿ��ǳ�Ժ���
				O_NUM++;
				//2013-04-09 zhangh ��� ��Ժ״��STATUS;��Ժ����IN_PAT_CONDITION �ǿ���֤
				if("Y".equals(this.getValueString("DIAGCHK_FLG"))){
					if(null == data.getValue("STATUS", i) || "".equals(data.getValue("STATUS", i))){
						message = "�뽫ȫ����Ժ��ϵĳ�Ժ״����д����";
						return message;
					}
					if(null == data.getValue("IN_PAT_CONDITION", i) || "".equals(data.getValue("IN_PAT_CONDITION", i))){
						message = "�뽫ȫ����Ժ��ϵ���Ժ������д����";
						return message;
					}
				}
				if ("Y".equals(data.getValue("MAIN", i))) {// �ж��Ƿ��������
					O_MainNum++;
				}
				// �жϳ�Ժ��ϵķ�Χ�Ƿ����� C00-C97,D00-D48 ֮��
				// ����ڴ˷�Χ����Ҫ��д������
				// String orderCode = data.getValue("CODE",i);
				// if (orderCode.substring(0, 3).compareTo("C00") >= 0 &&
				// orderCode.substring(0, 3).compareTo("D48") <= 0 &&
				// !orderCode.substring(0, 3).equals("D45")) {
				// String additional = data.getValue("ADDITIONAL",i);
				// if(additional.equals("")){
				// message = "����д������̬ѧ����M��(������)";
				// }
				// if (additional.compareTo("M80000/0") <= 0 ||
				// additional.compareTo("M99890/1") >= 0) {
				// message = "�����뷶ΧӦ����M80000/0-M99890/1";
				// }
				// }
			}
		}
		if (I_MainNum > 1) {
			message = "ֻ����1���ż��������Ϊ�ż��������";
			return message;
		} else if (I_MainNum == 0) {
			message = "��ѡ��1���ż��������Ϊ�ż��������";
			return message;
		}
		if (M_MainNum > 1) {
			message = "ֻ����1����Ժ�����Ϊ��Ժ�����";
			return message;
		} else if (M_MainNum == 0) {
			message = "��ѡ��1����Ժ�����Ϊ��Ժ�����";
			return message;
		}
		if (O_MainNum > 1) {
			message = "ֻ����1����Ժ�����Ϊ��Ժ�����";
			return message;
		} else if (O_MainNum == 0) {
			message = "��ѡ��1����Ժ�����Ϊ��Ժ�����";
			return message;
		}
		return message;
	}
	/**
	 * �ж���ҳ��Ժ����Ϻ��ٴ���Ժ������Ƿ�һ�� duzhw modify 20131211
	 * @return
	 */
	public boolean checkOMainDiag(){
		boolean flag = false;
		String icdCodeOMain1 = "";//�ٴ���ϳ�Ժ�����icd_code
		String icdCodeOMain2 = "";//��ҳ��ϳ�Ժ�����icd_code
		//�ж��ٴ�����Ƿ���ڳ�Ժ�����
		String caseNo = CASE_NO;
		String checkSql = "SELECT ICD_CODE FROM ADM_INPDIAG " +
				" WHERE CASE_NO = '"+caseNo+"' AND IO_TYPE = 'O' AND MAINDIAG_FLG = 'Y'";
		TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
		if(checkParm.getCount("ICD_CODE") < 0){
			flag = true;
			return flag;
		}
		icdCodeOMain1 = checkParm.getValue("ICD_CODE", 0);
		TTable table = (TTable) this.callFunction("UI|TP21_Table2|getThis");
		table.acceptText();
		TParm data = table.getParmValue();
		for (int i = 0; i < data.getCount(); i++) {
			if ("O".equals(data.getValue("TYPE", i))){//�жϳ�Ժ����
				if ("Y".equals(data.getValue("MAIN", i))){//�ж������
					icdCodeOMain2 = data.getValue("CODE", i);
					if(icdCodeOMain2.equals(icdCodeOMain1)){//��ҳ��ϳ�Ժ����� �� �ٴ���ϳ�Ժ����� ��ͬ
						flag = true;
					}
				}
			}
		}
		
		return flag;
	}

	/**
	 * ���Grid ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDiagTableValueCharge(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		int row = node.getRow();
		if (node.getColumn() == 0) {// ֻ��������ͽ����ж�
			// if ("I".equals(node.getValue().toString()) ||
			// "M".equals(node.getValue().toString())) { //������ż�����ϻ�����Ժ���
			// DiagGrid.setValueAt("Y", row, 1); //Ĭ������Ϊ�����
			// }
		} else if (node.getColumn() == 4) {
			if (row == DiagGrid.getRowCount() - 1)// �����ѡ���������һ�� ��ô�½���
				DiagGrid.addRow();
		}
	}

	/**
	 * ��ϱ� ����ɾ��
	 */
	public void onDiagDel() {
		int row = DiagGrid.getSelectedRow();
		int lastRow = DiagGrid.getRowCount() - 1;
		if (row >= 0) {// ��ѡ����
			//DiagGrid.removeRow(row);
			//duzhw �ߺ�ֱ̨��ɾ������
			TParm diagGridparm=this.DiagGrid.getParmValue();
			String caseNo = CASE_NO;
			String seqNo = diagGridparm.getValue("SEQ_NO",row);
			
			if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ���������ݣ�", "��Ϣ",
					JOptionPane.YES_NO_OPTION) == 0) {
				TParm selParm = new TParm(TJDODBTool.getInstance().update(delGridSql(caseNo,seqNo)));
			}
			
			if (row == lastRow)// �����ѡ���������һ�� ��ô�½���
				DiagGrid.addRow();
			patInHospInfo();
		}
	}
	/**
	 * ɾ�����sql
	 */
	public String delGridSql(String caseNo,String seqNo){
		String sql = " delete from MRO_RECORD_DIAG " +
				" where case_no = '"+caseNo+"' and seq_no = '"+seqNo+"'";
		return sql;
	}

	/**
	 * �����ʱ�õ�ʡ��
	 */
	public void onRESID_POST_CODE() {
		if (getValueString("TP1_H_POSTNO") == null
				|| "".equals(getValueString("TP1_H_POSTNO")))
			return;
		String post = getValueString("TP1_H_POSTNO");
		TParm parm = this.getPOST_CODE(post);
		if (parm.getData("POST_CODE", 0) == null
				|| "".equals(parm.getData("POST_CODE", 0)))
			return;
		setValue("TP1_PROVICE", parm.getData("POST_CODE", 0).toString()
				.substring(0, 2));
		setValue("TP1_COUNTRY", parm.getData("POST_CODE", 0).toString());
	}

	/**
	 * ͨ���ʱ�ĵõ�ʡ��
	 */
	public void onPost() {
		String post = getValueString("TP1_POST_NO");
		if (post == null || "".equals(post)) {
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("TP1_POST_R", parm.getData("POST_CODE", 0) == null ? "" : parm
				.getValue("POST_CODE", 0).substring(0, 2));
		setValue("TP1_POST_C", parm.getValue("POST_CODE", 0).toString());
	}

	/**
	 * ��λ�ʱ�ĵõ�ʡ��
	 */
	public void onCompanyPost() {
		String post = getValueString("TP1_O_POSTNO");
		if (post == null || "".equals(post)) {
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("TP1_O_POST_R", parm.getData("POST_CODE", 0) == null ? ""
				: parm.getValue("POST_CODE", 0).substring(0, 2));
		setValue("TP1_O_POST_C", parm.getValue("POST_CODE", 0).toString());
	}

	/**
	 * �õ�ʡ�д���
	 * 
	 * @param post
	 *            String
	 * @return TParm
	 */
	public TParm getPOST_CODE(String post) {
		TParm result = SYSPostTool.getInstance().getProvinceCity(post);
		return result;
	}

	/**
	 * ��ջ��� ��COMBO
	 */
	public void clearTP1_COUNTRY() {
		this.clearValue("TP1_COUNTRY");
	}

	/**
	 * ���ͨ�� ��COMBO
	 */
	public void clearTP1_POST_C() {
		this.clearValue("TP1_POST_C");
	}

	/**
	 * ��յ�λ ��COMBO
	 */
	public void clearTP1_O_POST_C() {
		this.clearValue("TP1_O_POST_C");
	}

	/**
	 * ͨ�����д�����������1
	 */
	public void selectCode_1() {
		this.setValue("TP1_POST_NO", this.getValue("TP1_POST_C"));
		this.onPost();
	}

	/**
	 * ͨ�����д�����������2
	 */
	public void selectCode_2() {
		this.setValue("TP1_H_POSTNO", this.getValue("TP1_COUNTRY"));
		this.onRESID_POST_CODE();
	}

	/**
	 * ͨ�����д�����������3
	 */
	public void selectCode_3() {
		this.setValue("TP1_O_POSTNO", this.getValue("TP1_O_POST_C"));
		this.onCompanyPost();
	}

	// zhangyong20110405
	/**
	 * ת�벡��������Ϣ
	 */
	private void intoData(int page) {
		// ��ȡ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		if (pat == null) { // �жϴ˻��� MR_NO �Ƿ����
			this.messageBox_("�޴˻�����Ϣ��");
			return;
		}

		// ��ȡ����סԺ��ص�������Ϣ
		TParm intoData = MRORecordTool.getInstance().intoData(CASE_NO);
		if (intoData.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		TParm admInp = intoData.getParm("ADMINP");// סԺ��Ϣ
		// System.out.println("inp------------------------------------------------"+admInp);
		TParm admTran = intoData.getParm("ADMTRAN");// ��̬��Ϣ
		// TParm admDiag = intoData.getParm("ADMDIAG");// �����Ϣ
		// System.out.println("-----------admDiag----" + admDiag);

		if (page == 1) {
			/******************** ��һҳǩ ����������Ϣ start *********************/
			if (("1".equals(UserType) && !ADMCHK_FLG.equals("Y"))
					|| ("2".equals(UserType) && !ADMCHK_FLG.equals("Y") && VS_DR
							.equals(OpenUser)) || "4".equals(UserType)) {
				this.clearTP1();
				this.setValue("TP1_MR_NO", pat.getMrNo()); // ������
				this.setValue("TP1_IPD_NO", pat.getIpdNo()); // סԺ��
				this.setValue("PT1_PAT_NAME", pat.getName()); // ����
				this.setValue("TP1_IDNO", pat.getIdNo()); // ���֤
				this.setValue("TP1_SEX", pat.getSexCode()); // �Ա�
				this.setValue("TP1_BIRTH_DATE", pat.getBirthday()); // ����
				this.setValue("TP1_CASE_NO", CASE_NO); // סԺ���
				this.setValue("TP1_MARRIGE", pat.getMarriageCode()); // ����
				// ��ʾ����
				String[] age = StringTool.CountAgeByTimestamp(
						pat.getBirthday(), admInp.getTimestamp("IN_DATE", 0));
				this.setValue("TP1_AGE", com.javahis.util.StringUtil.showAge(
						pat.getBirthday(), admInp.getTimestamp("IN_DATE", 0))); // Ӧ����
				// ��Ժ����-����
				this.setValue("TP1_NATION", pat.getNationCode()); // ����
				this.setValue("TP1_FOLK", pat.getSpeciesCode()); // ����
				this.setValue("HOMEPLACE_CODE", pat.gethomePlaceCode()); // �����ش���
				this.setValue("BIRTHPLACE", pat.getBirthPlace()); // �������
				// δ����,����������û���ҵ� CTZ1_CODE
				String ctz1Code = pat.getCtz1Code();
				// shibl add 20120112 start
				String mroCtz = "";
				TParm ctzParm = CTZTool.getInstance().getMroCtz(ctz1Code);
				if (ctzParm.getCount() > 0) {
					mroCtz = ctzParm.getValue("MRO_CTZ", 0);
				}
				this.setValue("TP1_PAYTYPE", ctz1Code); // ֧����ʽ CTZ1_CODE
				this.setValue("MRO_CTZ", mroCtz); // ������ҳ��� MRO_CTZ
				// add 20120112 end
				this.setValue("TP1_INNUM", admInp.getInt("IN_COUNT", 0)); // סԺ����
				this.setValue("TP1_TEL", pat.getTelHome()); // ���ߵ绰
				this.setValue("TP1_H_ADDRESS", pat.getResidAddress()); // ����סַ
				this.setValue("TP1_H_POSTNO", pat.getResidPostCode()); // �����ʱ�
				onRESID_POST_CODE(); // �����ʱ����ʡ��
				this.setValue("TP1_OCCUPATION", pat.getOccCode()); // ְҵ
				this.setValue("TP1_OFFICE", pat.getCompanyDesc()); // ��λ
				this.setValue("TP1_O_TEL", pat.getTelCompany()); // ��λ�绰
				// shibl add 20120112 start
				this.setValue("TP1_O_ADDRESS", pat.getCompanyAddress()); // ��λ��ַ
				this.setValue("TP1_O_POSTNO", pat.getCompanyPost()); // ��λ�ʱ�
				this.onCompanyPost(); // �����ʱ����ʡ��
				this.setValue("TP1_ADDRESS", pat.getAddress());// ͨ�ŵ�ַ
				this.setValue("TP1_POST_NO", pat.getPostCode());// ͨ���ʱ�
				this.onPost(); // �����ʱ����ʡ��
				// add 20120112 end
				this.setValue("TP1_CONTACTER", pat.getContactsName()); // ��ϵ������
				this.setValue("TP1_RELATIONSHIP", pat.getRelationCode()); // ��ϵ�˹�ϵ
				this.setValue("TP1_CONT_TEL", pat.getContactsTel()); // ��ϵ�˵绰
				this.setValue("TP1_CONT_ADDRESS", pat.getContactsAddress()); // ��ϵ�˵�ַ
				this.setValue("ADMCHK_FLG", "N"); // סԺ��¼��ʶ �ָ�Ϊδ���״̬
			}
		}
		/******************** ��һҳǩ ����������Ϣ end *********************/
		// } else if (page == 2) {
		// /******************** �ڶ�ҳǩ ����סԺ��Ϣ start *********************/
		// if (("2".equals(UserType) && !"Y".equals("DIAGCHK_FLG"))
		// && VS_DR.equals(OpenUser) || "4".equals(UserType)) {
		// this.clearTP2();
		// // ���Grid
		// TTable tranTable = (TTable) this.getComponent("TP21_Table1");
		// if (tranTable.getParmValue().getCount() > 0)
		// tranTable.removeRowAll(); // ��ն�̬��
		// TTable dailyTable = (TTable) this.getComponent("TP21_Table2");
		// if (dailyTable.getParmValue().getCount() > 0)
		// dailyTable.removeRowAll(); // �����ϱ�
		// TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		// // if (opTable.getParmValue().getCount() > 0)
		// opTable.removeRowAll(); // ��������� ����resetModify
		// // ����������ǰ��������¼û�б�ɾ��
		// // ��һСҳǩ
		// /*************************** ��һ��СҳǩSTART
		// *********************************************/
		// SimpleDateFormat dt=new SimpleDateFormat("yyyy/MM/dd");
		// for (int i = 0; i < admTran.getCount(); i++) {
		// admTran.setData("TRANS_DATE",i,dt.format(StringTool.getTimestamp(admTran.getValue("TRANS_DATE",
		// i),
		// "yyyyMMdd HHmmss")));
		// }
		// admTran.setCount(admTran.getCount());
		// // System.out.println("------------admTran--11----------" + admTran);
		// tranTable.setParmValue(admTran); // �󶨶�̬��Ϣ
		// // ���Grid��
		// TTable diagTable = (TTable) this.getComponent("TP21_Table2");
		// diagTable.setParmValue(admDiag);
		// diagTable.addRow();
		// // //ת�ƿ���
		// // this.setValue("TP2_TRANS_DEPT",
		// // admTran.getValue("DEPT_CODE",
		// // admTran.getCount() - 1));
		// Timestamp endTime = admInp.getValue("DS_DATE", 0).equals("") ?
		// SystemTool
		// .getInstance().getDate() : admInp.getTimestamp(
		// "DS_DATE", 0);
		// int realStayDays = StringTool.getDateDiffer(StringTool
		// .getTimestamp(
		// StringTool.getString(endTime, "yyyyMMdd"),
		// "yyyyMMdd"), StringTool.getTimestamp(StringTool
		// .getString(admInp.getTimestamp("IN_DATE", 0),
		// "yyyyMMdd"), "yyyyMMdd"));
		// this.setValue("TP2_REAL_STAY_DAYS",
		// realStayDays > 0 ? String.valueOf(realStayDays) : "1"); // ʵ��סԺ����
		// this.setValue("TP2_OUT_DEPT",
		// admInp.getValue("DS_DEPT_CODE", 0)); // ��Ժ����
		// this.setValue("TP2_IN_CONDITION",
		// admInp.getValue("PATIENT_CONDITION", 0)); // ��Ժ���
		// this.setValue("TP2_OUT_DATE", admInp.getTimestamp("DS_DATE", 0)); //
		// ��Ժ����
		//
		// this.setValue("TP2_CONFIRM_DATE", ""); // ȷ������ ��ʱ��֪��Ӧ��ȡ�ĸ�
		// /*----------------------add
		// start------------------------------------------------------------*/
		// this.setValue("TP2_IN_DATE", admInp.getTimestamp("IN_DATE", 0)); //
		// ��Ժ����
		// this.setValue("TP2_IN_DEPT", admInp.getValue("IN_DEPT_CODE", 0)); //
		// ��Ժ����
		// this.setValue("TP2_ADM_SOURCE",
		// admInp.getValue("ADM_SOURCE", 0)); // ��Ժ��Դ
		// this.setValue("TP2_VS_NURSE_CODE",
		// admInp.getValue("VS_NURSE_CODE", 0)); // ���λ�ʿ
		// /*----------------------add
		// end------------------------------------------------------------*/
		// this.setValue("DIAGCHK_FLG", "N"); // ҽʦ�ύ��ʶ ���´����Ĭ��ΪN
		// /**************** ��һ��Сҳǩend
		// **********************************************************************/
		// /************** ����СҳǩSTART
		// *********************************************************************/
		// //add 20120201
		// this.setValue("TP22_DIRECTOR_DR_CODE",
		// admInp.getValue("DIRECTOR_DR_CODE", 0));//������
		// this.setValue("TP22_ATTEND_DR_CODE",
		// admInp.getValue("ATTEND_DR_CODE", 0));//����ҽʦ
		// this.setValue("TP2_VS_DR_CODE", admInp.getValue("VS_DR_CODE",
		// 0));//����ҽʦ
		// this.setValue("TP22_ALLEGIC", Durgallec);//����ҩ��
		// // ����Grid��
		// onIntoOPData();
		// // ѪҺ��Ϣת��
		// onIntoBMSData();
		// /**************** ����СҳǩEND
		// ***********************************************************************/
		// }
		// /******************** �ڶ�ҳǩ ����סԺ��Ϣ end *********************/
		// }
		else {
			/******************** ����ҳǩ ����������Ϣ start *********************/
			onFinance(); // ���ò�����뷽��
			/******************** ����ҳǩ ����������Ϣ end *********************/
		}
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		TParm data = MROPrintTool.getInstance().getNewMroRecordprintData(
				CASE_NO, this.getValue("TP2_REAL_STAY_DAYS"));
		if (data.getErrCode() < 0) {
			this.messageBox("E0005");
		}
		EMRTool emrTool = new EMRTool(this.CASE_NO, this.MR_NO, this);
		Object obj = new Object();
		obj = openPrintDialog("%ROOT%\\config\\prt\\MRO\\MRO_NEWRECORD.jhw",
				data);
		emrTool.saveEMR(obj, "������ҳ", "EMR080001", "EMR08000101", false);
	}

	/**
	 * SYS_FEE�滻������
	 * 
	 * @param comboTag
	 *            String combobox�ؼ�Tag
	 * @param code
	 *            String
	 * @return String
	 */
	private String getDescByCode(String GROUP_ID, String code) {
		String sql = "SELECT CHN_DESC,ENG_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = '"
				+ GROUP_ID + "' AND ID = '" + code + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getValue("CHN_DESC", 0);
	}

	/**
	 * תԺIDѡ���¼� Ĭ��ѡ��999999ʱ�˹������ֶ�����
	 */
	public void onSelectTran() {
		String tranId = (String) getValue("TP2_TRAN_HOSP");
		if (tranId.equals("999999")) {
			callFunction("UI|TP2_TRAN_HOSP|setVisible", true);
			callFunction("UI|TRAN_HOSP_OTHER|setVisible", true);
		} else {
			callFunction("UI|TP2_TRAN_HOSP|setVisible", true);
			callFunction("UI|TRAN_HOSP_OTHER|setVisible", false);
			this.setValue("TRAN_HOSP_OTHER", "");
		}
	}

	/**
	 * �����صĿ���Combo�滻������
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	private String getDeptDesc(String code) {
		TComboBox cmb = (TComboBox) this.getComponent("printDept");
		cmb.setValue(code);
		return cmb.getSelectedName();
	}

	/**
	 * �����ص��û�ID�滻���û�����
	 * 
	 * @param userID
	 *            String
	 * @return String
	 */
	private String getUserName(String userID) {
		TComboBox cmb = (TComboBox) this.getComponent("printUser");
		cmb.setValue(userID);
		return cmb.getSelectedName();
	}

	/**
	 * ���õ�һҳǩ�Ŀؼ��Ƿ�ɱ༭
	 * 
	 * @param flg
	 *            boolean true:�ɱ༭ false�����ɱ༭
	 */
	private void setEnabledPage1(boolean flg) {
		callFunction("UI|PT1_PAT_NAME|setEnabled", flg);
		callFunction("UI|TP1_IDNO|setEnabled", flg);
		callFunction("UI|TP1_SEX|setEnabled", flg);
		callFunction("UI|TP1_BIRTH_DATE|setEnabled", flg);
		callFunction("UI|TP1_MARRIGE|setEnabled", flg);
		callFunction("UI|TP1_NATION|setEnabled", flg);
		callFunction("UI|TP1_FOLK|setEnabled", flg);
		callFunction("UI|TP1_PAYTYPE|setEnabled", flg);
		callFunction("UI|TP1_INNUM|setEnabled", flg);
		callFunction("UI|TP1_TEL|setEnabled", flg);
		callFunction("UI|HOMEPLACE_CODE|setEnabled", flg);
		callFunction("UI|TP1_H_ADDRESS|setEnabled", flg);
		callFunction("UI|TP1_H_POSTNO|setEnabled", flg);
		callFunction("UI|TP1_PROVICE|setEnabled", flg);
		callFunction("UI|TP1_COUNTRY|setEnabled", flg);
		callFunction("UI|TP1_OCCUPATION|setEnabled", flg);
		callFunction("UI|TP1_OFFICE|setEnabled", flg);
		callFunction("UI|TP1_O_TEL|setEnabled", flg);
		callFunction("UI|TP1_O_ADDRESS|setEnabled", flg);
		callFunction("UI|TP1_O_POSTNO|setEnabled", flg);
		callFunction("UI|TP1_CONTACTER|setEnabled", flg);
		callFunction("UI|TP1_RELATIONSHIP|setEnabled", flg);
		callFunction("UI|TP1_CONT_TEL|setEnabled", flg);
		callFunction("UI|TP1_CONT_ADDRESS|setEnabled", flg);
		// callFunction("UI|HOMEPLACE_DESC|setEnabled", flg);
		callFunction("UI|ADMCHK_FLG|setEnabled", flg);
		// shibl20120111modify
		callFunction("UI|BIRTHPLACE|setEnabled", flg);
		// callFunction("UI|BIRTHPLACE_DESC|setEnabled", flg);
		callFunction("UI|TP1_POST_NO|setEnabled", flg);
		callFunction("UI|TP1_ADDRESS|setEnabled", flg);
		callFunction("UI|TP1_POST_R|setEnabled", flg);
		callFunction("UI|TP1_POST_C|setEnabled", flg);
		callFunction("UI|TP1_O_POST_R|setEnabled", flg);
		callFunction("UI|TP1_O_POST_C|setEnabled", flg);
		callFunction("UI|MRO_CTZ|setEnabled", flg);
		callFunction("UI|TP1_NHI_NO|setEnabled", flg);// add by wanglong
		// 201211127
		callFunction("UI|TP1_NHI_CARDNO|setEnabled", flg);// add by wanglong
		// 201211127
		if ("4".equals(UserType)) {
			callFunction("UI|ADMCHK_FLG|setEnabled", true);
		}
	}

	/**
	 * ���õڶ�ҳǩ�Ŀؼ��Ƿ�ɱ༭
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnabledPage2(boolean flg) {
		setEnagledPage2_1(flg);
		setEnagledPage2_2(flg);
		setEnagledPage2_3(flg);
		setEnagledPage2_4(flg);
		setEnagledPage2_5(flg);
	}

	/**
	 * ���õڶ�ҳǩ�ĵ�һСҳǩ
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_1(boolean flg) {
		callFunction("UI|TP2_TRANS_DEPT|setEnabled", flg);
		callFunction("UI|TP21_Table1|setEnabled", flg);
		callFunction("UI|TP21_Table2|setEnabled", flg);
		callFunction("UI|TP2_REAL_STAY_DAYS|setEnabled", flg);
		callFunction("UI|TP2_OUT_DEPT|setEnabled", flg);
		callFunction("UI|TP2_IN_CONDITION|setEnabled", flg);
		callFunction("UI|TP2_OUT_DATE|setEnabled", flg);
		callFunction("UI|TP2_CONFIRM_DATE|setEnabled", flg);
		callFunction("UI|DIAGCHK_FLG|setEnabled", flg);
		callFunction("UI|btn_Del|setEnabled", flg);
		// add 20120113 shibl
		callFunction("UI|TP2_ADM_SOURCE|setEnabled", flg);
		callFunction("UI|TP2_IN_DATE|setEnabled", flg);
		callFunction("UI|TP2_IN_DEPT|setEnabled", flg);
		callFunction("UI|TP2_OUT_TYPE|setEnabled", flg);
		callFunction("UI|TP2_TRAN_HOSP|setEnabled", flg);
		callFunction("UI|TP2_BE_IN_D|setEnabled", flg);
		callFunction("UI|TP2_BE_IN_H|setEnabled", flg);
		callFunction("UI|TP2_BE_IN_M|setEnabled", flg);
		callFunction("UI|TP2_AF_IN_D|setEnabled", flg);
		callFunction("UI|TP2_AF_IN_H|setEnabled", flg);
		callFunction("UI|TP2_AF_IN_M|setEnabled", flg);
		callFunction("UI|TP2_VS_NURSE_CODE|setEnabled", flg);
		callFunction("UI|TP2_AGN_PLAN_INTENTION|setEnabled", flg);
		callFunction("UI|TP2_AGN_PLAN_FLG|setEnabled", flg);
		callFunction("UI|INFECT_COUNT|setEnabled", flg);

		callFunction("UI|TP2_SPENURS_DAYS|setEnabled", flg);
		callFunction("UI|TP2_FIRNURS_DAYS|setEnabled", flg);
		callFunction("UI|TP2_SECNURS_DAYS|setEnabled", flg);
		callFunction("UI|TP2_THRNURS_DAYS|setEnabled", flg);
			
		// add by wangqing 20180116  start ����������ʹ�÷��ӡ�������ʹ����
//		callFunction("UI|TP2_VENTI_TIME|setEnabled", flg);
//		callFunction("UI|TP2_VENTI_TIME_MIN|setEnabled", flg);
//		callFunction("UI|TP2_VENTI_TIME_SEC|setEnabled", flg);
		// add by wangqing 20180116  end
		
		
		
		if ("4".equals(UserType)) {
			callFunction("UI|DIAGCHK_FLG|setEnabled", true);
		}
	}

	/**
	 * ���õڶ�ҳǩ�ĵڶ�Сҳǩ
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_2(boolean flg) {
		callFunction("UI|TP22_PATHOLOGY_DIAG|setEnabled", flg);
		// add 20120113 shibl
		callFunction("UI|TP2_PATHOLOGY_NO|setEnabled", flg);

		callFunction("UI|TP22_PATHOLOGY_DIAG2|setEnabled", flg);
		callFunction("UI|TP2_PATHOLOGY_NO2|setEnabled", flg);
		callFunction("UI|TP22_PATHOLOGY_DIAG3|setEnabled", flg);
		callFunction("UI|TP2_PATHOLOGY_NO3|setEnabled", flg);

		callFunction("UI|TP22_EX_RSN|setEnabled", flg);

		callFunction("UI|TP22_EX_RSN2|setEnabled", flg);
		callFunction("UI|TP22_EX_RSN3|setEnabled", flg);
		callFunction("UI|DIF_DEGREE|setEnabled", flg);
		callFunction("UI|MDIAG_BASIS|setEnabled", flg);

		callFunction("UI|TP22_ALLEGIC|setEnabled", flg);
		callFunction("UI|ALLEGIC_1|setEnabled", flg);
		callFunction("UI|ALLEGIC_2|setEnabled", flg);
		callFunction("UI|HBsAg_1|setEnabled", flg);
		callFunction("UI|HBsAg_2|setEnabled", flg);
		callFunction("UI|HBsAg_3|setEnabled", flg);
		callFunction("UI|HCV-Ab_1|setEnabled", flg);
		callFunction("UI|HCV-Ab_2|setEnabled", flg);
		callFunction("UI|HCV-Ab_3|setEnabled", flg);
		callFunction("UI|HIV-Ab_1|setEnabled", flg);
		callFunction("UI|HIV-Ab_2|setEnabled", flg);
		callFunction("UI|HIV-Ab_3|setEnabled", flg);
		callFunction("UI|TP22_myc1|setEnabled", flg);
		callFunction("UI|TP22_myc2|setEnabled", flg);
		callFunction("UI|TP22_myc3|setEnabled", flg);
		callFunction("UI|TP22_myc4|setEnabled", flg);
		callFunction("UI|TP22_sys1|setEnabled", flg);
		callFunction("UI|TP22_sys2|setEnabled", flg);
		callFunction("UI|TP22_sys3|setEnabled", flg);
		callFunction("UI|TP22_sys4|setEnabled", flg);
		callFunction("UI|TP22_fyb1|setEnabled", flg);
		callFunction("UI|TP22_fyb2|setEnabled", flg);
		callFunction("UI|TP22_fyb3|setEnabled", flg);
		callFunction("UI|TP22_fyb4|setEnabled", flg);
		callFunction("UI|TP22_ryc1|setEnabled", flg);
		callFunction("UI|TP22_ryc2|setEnabled", flg);
		callFunction("UI|TP22_ryc3|setEnabled", flg);
		callFunction("UI|TP22_ryc4|setEnabled", flg);
		callFunction("UI|TP22_lyb1|setEnabled", flg);
		callFunction("UI|TP22_lyb2|setEnabled", flg);
		callFunction("UI|TP22_lyb3|setEnabled", flg);
		callFunction("UI|TP22_lyb4|setEnabled", flg);
		callFunction("UI|TP22_GET_TIMES|setEnabled", flg);
		callFunction("UI|TP22_SUCCESS_TIMES|setEnabled", flg);
		callFunction("UI|TP22_DIRECTOR_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_PROF_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_ATTEND_DR_CODE|setEnabled", flg);
		callFunction("UI|TP2_VS_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_INDUCATION_DR_CODE|setEnabled", flg);
		callFunction("UI|TP22_GRADUATE_INTERN_CODE|setEnabled", flg);
		callFunction("UI|TP22_INTERN_DR_CODE|setEnabled", flg);

		/*
		 * Modify ZhenQin 2011-05-18 ��� �ٴ�·��,���ַ���,�ٴ����Բ���,��ѧ����
		 */
		callFunction("UI|CLNCPATH_CODE|setEnabled", flg);
		callFunction("UI|DISEASES_CODE|setEnabled", flg);
		callFunction("UI|TEST_EMR|setEnabled", flg);
		callFunction("UI|TEACH_EMR|setEnabled", flg);

		callFunction("UI|TP4_CTRL_DR|setEnabled", flg);
		callFunction("UI|TP4_CTRL_NURSE|setEnabled", flg);
		callFunction("UI|TP4_CTRL_DATE|setEnabled", flg);
	}

	/**
	 * ���õڶ�ҳǩ�ĵ���Сҳǩ
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_3(boolean flg) {
		callFunction("UI|TP23_OP_Table|setEnabled", flg);
		callFunction("UI|TP23_btnAdd|setEnabled", flg);
	    callFunction("UI|TP23_btnPack|setEnabled", flg);//wanglong add 20150226
		callFunction("UI|TP23_btnIn|setEnabled", flg);
		callFunction("UI|TP23_btnDel|setEnabled", flg);
//		callFunction("UI|TP23XX1|setEnabled", flg);
//		callFunction("UI|TP23XX2|setEnabled", flg);
//		callFunction("UI|TP23XX3|setEnabled", flg);
//		callFunction("UI|TP23XX4|setEnabled", flg);
//		callFunction("UI|TP23XX5|setEnabled", flg);
//		callFunction("UI|TP23XX6|setEnabled", flg);
		callFunction("UI|PT23SX1|setEnabled", flg);
		callFunction("UI|PT23SX2|setEnabled", flg);
//		callFunction("UI|PT23RH1|setEnabled", flg);
//		callFunction("UI|PT23RH2|setEnabled", flg);
//		callFunction("UI|PT23RH3|setEnabled", flg);
//		callFunction("UI|PT23RH4|setEnabled", flg);
		callFunction("UI|TP23_RBC|setEnabled", flg);
		callFunction("UI|TP23_PLATE|setEnabled", flg);
		callFunction("UI|TP23_PLASMA|setEnabled", flg);
		callFunction("UI|TP23_WHOLE_BLOOD|setEnabled", flg);
		callFunction("UI|TP23_OTH_BLOOD|setEnabled", flg);
		callFunction("UI|TP23_InBlood|setEnabled", flg);

	//	callFunction("UI|DAY_OPE_FLG|setEnabled", false); 	//2017/3/29     �ؼ����ɱ༭  yanmm
		callFunction("UI|OPE_TYPE_CODE1|setEnabled", flg);
		callFunction("UI|OPE_TYPE_CODE2|setEnabled", flg);
		callFunction("UI|OPE_TYPE_CODE3|setEnabled", flg);
		callFunction("UI|TP23_BANKED_BLOOD|setEnabled", flg);
	}

	/**
	 * ���õڶ�ҳǩ�ĵ���Сҳǩ
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_4(boolean flg) {
	    callFunction("UI|BODY_CHECK|setEnabled", flg);// wanglong modify 20141030
		callFunction("UI|PT24BY1|setEnabled", flg);
		callFunction("UI|PT24BY2|setEnabled", flg);
		callFunction("UI|PT24SZ1|setEnabled", flg);
		callFunction("UI|PT24SZ2|setEnabled", flg);
		// callFunction("UI|TP24_ACCOMPANY_YEAR|setEnabled", flg);
		// callFunction("UI|TP24_ACCOMPANY_MONTH|setEnabled", flg);
		// callFunction("UI|TP24_ACCOMPANY_WEEK|setEnabled", flg);
		callFunction("UI|PT24SJBL1|setEnabled", flg);
		callFunction("UI|PT24SJBL2|setEnabled", flg);
	}
	
	/**
	 * ���õڶ�ҳǩ�ĵ���Сҳǩ
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnagledPage2_5(boolean flg) {
		callFunction("UI|TP25_ICU_Table|setEnabled", flg);
		callFunction("UI|TP25_btnAdd|setEnabled", flg);
		callFunction("UI|TP25_btnDel|setEnabled", flg);
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		// add by wangqing 20180116 start ����������ʹ�÷��ӡ�������ʹ����
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		callFunction("UI|VENTI_TIME|setEnabled", flg);
		// add by wangqing 20180116 end
	}

	/**
	 * ���õ���ҳǩ�Ŀؼ��Ƿ�ɱ༭
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnabledPage3(boolean flg) {
		callFunction("UI|TP3_SUM|setEnabled", flg);
		callFunction("UI|BILCHK_FLG|setEnabled", flg);
		callFunction("UI|TP3_Table1|setEnabled", flg);
		if ("4".equals(UserType)) {
			callFunction("UI|BILCHK_FLG|setEnabled", true);
		}
	}

	/**
	 * ���õ���ҳǩ�Ŀؼ��Ƿ�ɱ༭
	 * 
	 * @param flg
	 *            boolean
	 */
	private void setEnabledPage4(boolean flg) {
		callFunction("UI|TP4CR1|setEnabled", flg);
		callFunction("UI|TP4CR2|setEnabled", flg);
		callFunction("UI|TP4SB1|setEnabled", flg);
		callFunction("UI|TP4SB2|setEnabled", flg);
		callFunction("UI|TP4BA1|setEnabled", flg);
		callFunction("UI|TP4BA2|setEnabled", flg);
		callFunction("UI|TP4BA3|setEnabled", flg);
		// callFunction("UI|TP4_CTRL_DR|setEnabled", flg);
		// callFunction("UI|TP4_CTRL_NURSE|setEnabled", flg);
		// callFunction("UI|TP4_CTRL_DATE|setEnabled", flg);
		callFunction("UI|TP4_ENCODER|setEnabled", flg);
		callFunction("UI|QTYCHK_FLG|setEnabled", flg);
		if ("4".equals(UserType)) {
			callFunction("UI|QTYCHK_FLG|setEnabled", true);
		}
		
		//add by yangjj 20151224 ҽ����¼����ѡ��Ⱦ������
		if ("2".equals(UserType)){
			callFunction("UI|TP4CR1|setEnabled", true);
			callFunction("UI|TP4CR2|setEnabled", true);
		}
	}

	/**
	 * ��Ժ��Ƭ
	 */
	public void onOutHospital() {
		// ��ȡĳһ��������ҳ��Ϣ
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm mro = MRORecordTool.getInstance().getInHospInfo(parm);
		if (mro.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (mro.getValue("OUT_DATE", 0).length() <= 0) {
			this.messageBox_("�ò���סԺ��");
			return;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		// ��ѯ������Ϣ
		TParm op_date = MRORecordTool.getInstance().queryOP_Info(CASE_NO);
		// ���ICD�滻����
		OrderList orderDesc = new OrderList();
		// ��������
		TParm printData = new TParm();
		/*************** T1 ����������Ϣ���� *********************/
		printData.setData("STATION", "TEXT", getDeptDesc(mro.getValue(
				"IN_DEPT", 0)));// Ŀǰ��ʾ������Ժ����
		printData.setData("MR_NO", "TEXT", mro.getValue("MR_NO", 0));// ������
		printData.setData("IN_COUNT", "TEXT", mro.getValue("IN_COUNT", 0));// סԺ����
		printData.setData("ICD10", "TEXT", mro.getValue("IN_DIAG_CODE", 0));// ��Ժ���
		printData.setData("ICDTYPE", "TEXT", mro.getValue("", 0));// ��������� ����
		// ��֪��ȡ��
		printData.setData("CTZ", "TEXT", mro.getValue("CTZ1_CODE", 0));// ��ֱ�
		printData.setData("IPD_NO", "TEXT", mro.getValue("IPD_NO", 0));// סԺ��
		printData.setData("PAT_NAME", "TEXT", mro.getValue("PAT_NAME", 0));// ����
		printData.setData("SEX", "TEXT",
				mro.getValue("SEX", 0).equals("1") ? "��" : "Ů");// �Ա�
		printData.setData("MARRIGE", "TEXT", mro.getValue("MARRIGE", 0));// ����
		printData.setData("BIRTH_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));// ����
		String OCCUPATION = getDescByCode("SYS_OCCUPATION", mro.getValue(
				"OCCUPATION", 0));
		printData.setData("OCCUPATION", "TEXT", OCCUPATION);// ְҵ
		printData.setData("H_ADDRESS", "TEXT", mro.getValue("H_ADDRESS", 0));// ��סַ
		printData.setData("OFFICE", "TEXT", mro.getValue("OFFICE", 0));// ��λ
		printData.setData("IN_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("IN_DATE", 0), "yyyy��MM��dd��"));// ��Ժ����
		printData.setData("OUT_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("OUT_DATE", 0), "yyyy��MM��dd��"));// ��Ժ����
		printData.setData("REAL_STAY_DAYS", "TEXT", mro.getValue(
				"REAL_STAY_DAYS", 0));// ʵ��סԺ����
		printData.setData("TRAN_DEPT", "TEXT", getDeptDesc(mro.getValue(
				"TRAN_DEPT", 0)));// ת�ƿƱ�
		printData.setData("IN_DIAG_CODE", "TEXT", orderDesc
				.getTableShowValue(mro.getValue("IN_DIAG_CODE", 0)));// ��Ժ���
		printData.setData("IN_CONDITION", "TEXT", getDescByCode(
				"ADM_CONDITION", mro.getValue("IN_CONDITION", 0)));// ��Ժ���
		printData.setData("OUT_DIAG_CODE1", "TEXT", orderDesc
				.getTableShowValue(mro.getValue("OUT_DIAG_CODE1", 0)));// ��Ժ���
		printData.setData("CONFIRM_DATE", "TEXT", StringTool.getString(mro
				.getTimestamp("CONFIRM_DATE", 0), "yyyy��MM��dd��"));// ȷ������
		printData.setData("INTE_DIAG_CODE", "TEXT", orderDesc
				.getTableShowValue(mro.getValue("INTE_DIAG_CODE", 0)));// Ժ�ڸ�Ⱦ���
		printData.setData("GET_TIMES", "TEXT", mro.getValue("GET_TIMES", 0));// ���ȴ���
		printData.setData("SUCCESS_TIMES", "TEXT", mro.getValue(
				"SUCCESS_TIMES", 0));// �ɹ�����
		printData.setData("CODE1_STATUS", "TEXT", getDescByCode("ADM_RETURN", mro
				.getValue("CODE1_STATUS", 0)));// ��Ժת��
		printData.setData("PROF_DR_CODE", "TEXT", getUserName(mro.getValue(
				"PROF_DR_CODE", 0)));// ����ҽʦ
		printData.setData("ATTEND_DR_CODE", "TEXT", getUserName(mro.getValue(
				"ATTEND_DR_CODE", 0)));// ����ҽʦ
		printData.setData("VS_DR_CODE", "TEXT", getUserName(mro.getValue(
				"VS_DR_CODE", 0)));// סԺҽʦ
		/********************* ������Ϣ **************************/
		// ����ֻ��ʾǰ������Ϣ
		printData.setData("OPICD1", "TEXT", op_date.getValue("OP_CODE", 0));// ����ICD
		printData.setData("OPICD2", "TEXT", op_date.getValue("OP_CODE", 1));// ����ICD
		printData.setData("OPICD3", "TEXT", op_date.getValue("OP_CODE", 2));// ����ICD
		printData.setData("OP_DATE1", "TEXT", StringTool.getString(op_date
				.getTimestamp("OP_DATE", 0), "yyyy/MM/dd"));// ��������
		printData.setData("OP_DATE2", "TEXT", StringTool.getString(op_date
				.getTimestamp("OP_DATE", 1), "yyyy/MM/dd"));// ��������
		printData.setData("OP_DATE3", "TEXT", StringTool.getString(op_date
				.getTimestamp("OP_DATE", 2), "yyyy/MM/dd"));// ��������
		printData.setData("OP_DESC1", "TEXT", op_date.getValue("OP_DESC", 0));// ��������
		printData.setData("OP_DESC2", "TEXT", op_date.getValue("OP_DESC", 1));// ��������
		printData.setData("OP_DESC3", "TEXT", op_date.getValue("OP_DESC", 2));// ��������
		printData.setData("OP_DR1", "TEXT", getUserName(op_date.getValue(
				"MAIN_SUGEON", 0)));// ����ҽʦ
		printData.setData("OP_DR2", "TEXT", getUserName(op_date.getValue(
				"MAIN_SUGEON", 1)));// ����ҽʦ
		printData.setData("OP_DR3", "TEXT", getUserName(op_date.getValue(
				"MAIN_SUGEON", 2)));// ����ҽʦ
		printData.setData("OP_M1", "TEXT", op_date.getValue("ANA_WAY", 0));// ����ʽ
		printData.setData("OP_M2", "TEXT", op_date.getValue("ANA_WAY", 1));// ����ʽ
		printData.setData("OP_M3", "TEXT", op_date.getValue("ANA_WAY", 2));// ����ʽ
		printData.setData("HEALTH_LEVEL1", "TEXT", getDescByCode(
				"MRO_HEALTHLEVEL", op_date.getValue("HEALTH_LEVEL", 0)));// �ָ��ȼ�
		printData.setData("HEALTH_LEVEL2", "TEXT", getDescByCode(
				"MRO_HEALTHLEVEL", op_date.getValue("HEALTH_LEVEL", 1)));// �ָ��ȼ�
		printData.setData("HEALTH_LEVEL3", "TEXT", getDescByCode(
				"MRO_HEALTHLEVEL", op_date.getValue("HEALTH_LEVEL", 2)));// �ָ��ȼ�
        String bloodDesc = "";
        if (mro.getValue("BLOOD_TYPE", 0).equals("1")) {
            bloodDesc = "A";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("2")) {
            bloodDesc = "B";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("3")) {
            bloodDesc = "O";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("4")) {
            bloodDesc = "AB";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("5")) {
            bloodDesc = "����";
        } else if (mro.getValue("BLOOD_TYPE", 0).equals("6")) {
            bloodDesc = "δ��";
        }
		printData.setData("BLOOD_TYPE", "TEXT", bloodDesc);// Ѫ��
		printData.setData("RBC", "TEXT", mro.getValue("RBC", 0));// ��ϸ��
		printData.setData("PLATE", "TEXT", mro.getValue("PLATE", 0));// ѪС��
		printData.setData("PLASMA", "TEXT", mro.getValue("PLASMA", 0));// Ѫ��
		printData
				.setData("WHOLE_BLOOD", "TEXT", mro.getValue("WHOLE_BLOOD", 0));// ȫѪ
		printData.setData("OTH_BLOOD", "TEXT", mro.getValue("OTH_BLOOD", 0));// ����
		/********** ���ò��� *****************************/
		printData.setData("SUMTOT", "TEXT", df
				.format(mro.getDouble("CHARGE_01", 0)
						+ mro.getDouble("CHARGE_02", 0)
						+ mro.getDouble("CHARGE_03", 0)
						+ mro.getDouble("CHARGE_04", 0)
						+ mro.getDouble("CHARGE_05", 0)
						+ mro.getDouble("CHARGE_06", 0)
						+ mro.getDouble("CHARGE_07", 0)
						+ mro.getDouble("CHARGE_08", 0)
						+ mro.getDouble("CHARGE_09", 0)
						+ mro.getDouble("CHARGE_10", 0)
						+ mro.getDouble("CHARGE_11", 0)
						+ mro.getDouble("CHARGE_12", 0)
						+ mro.getDouble("CHARGE_13", 0)
						+ mro.getDouble("CHARGE_14", 0)
						+ mro.getDouble("CHARGE_15", 0)
						+ mro.getDouble("CHARGE_16", 0)
						+ mro.getDouble("CHARGE_17", 0)));
		printData.setData("CHARGE_01", "TEXT", df.format(mro.getDouble(
				"CHARGE_01", 0)));
		printData.setData("CHARGE_02", "TEXT", df.format(mro.getDouble(
				"CHARGE_02", 0)));
		printData.setData("CHARGE_03", "TEXT", df.format(mro.getDouble(
				"CHARGE_03", 0)));
		printData.setData("CHARGE_04", "TEXT", df.format(mro.getDouble(
				"CHARGE_04", 0)));
		printData.setData("CHARGE_05", "TEXT", df.format(mro.getDouble(
				"CHARGE_05", 0)));
		printData.setData("CHARGE_06", "TEXT", df.format(mro.getDouble(
				"CHARGE_06", 0)));
		printData.setData("CHARGE_07", "TEXT", df.format(mro.getDouble(
				"CHARGE_07", 0)));
		printData.setData("CHARGE_08", "TEXT", df.format(mro.getDouble(
				"CHARGE_08", 0)));
		printData.setData("CHARGE_09", "TEXT", df.format(mro.getDouble(
				"CHARGE_09", 0)));
		printData.setData("CHARGE_10", "TEXT", df.format(mro.getDouble(
				"CHARGE_10", 0)));
		printData.setData("CHARGE_11", "TEXT", df.format(mro.getDouble(
				"CHARGE_11", 0)));
		printData.setData("CHARGE_12", "TEXT", df.format(mro.getDouble(
				"CHARGE_12", 0)));
		printData.setData("CHARGE_13", "TEXT", df.format(mro.getDouble(
				"CHARGE_13", 0)));
		printData.setData("CHARGE_14", "TEXT", df.format(mro.getDouble(
				"CHARGE_14", 0)));
		printData.setData("CHARGE_15", "TEXT", df.format(mro.getDouble(
				"CHARGE_15", 0)));
		printData.setData("CHARGE_16", "TEXT", df.format(mro.getDouble(
				"CHARGE_16", 0)));
		printData.setData("CHARGE_17", "TEXT", df.format(mro.getDouble(
				"CHARGE_17", 0)));
		this.openPrintDialog("%ROOT%\\config\\prt\\MRO\\OutHospPrint.jhw",
				printData);
	}

	/**
	 * ��������¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void pathology_Return(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_PATHOLOGY_DIAG", parm.getValue("ICD_CODE"));
		this.setValue("PATHOLOGY_DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * �������2�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void pathology_Return2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_PATHOLOGY_DIAG2", parm.getValue("ICD_CODE"));
		this.setValue("PATHOLOGY_DIAG_DESC2", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * �������3�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void pathology_Return3(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_PATHOLOGY_DIAG3", parm.getValue("ICD_CODE"));
		this.setValue("PATHOLOGY_DIAG_DESC3", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * �����ж�����¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ex_Return(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_EX_RSN", parm.getValue("ICD_CODE"));
		this.setValue("EX_RSN_DESC", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * �����ж����2�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ex_Return2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_EX_RSN2", parm.getValue("ICD_CODE"));
		this.setValue("EX_RSN_DESC2", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * �����ж����3�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void ex_Return3(String tag, Object obj) {
		TParm parm = (TParm) obj;
		this.setValue("TP22_EX_RSN3", parm.getValue("ICD_CODE"));
		this.setValue("EX_RSN_DESC3", parm.getValue("ICD_CHN_DESC"));
	}

	/**
	 * ��˱������
	 * 
	 * @return boolean
	 */
	public boolean checkSaveData() {
		// �ж����ȳɹ��������ܴ������ȴ���
		if (this.getValue("TP22_GET_TIMES") != null
				&& this.getValue("TP22_SUCCESS_TIMES") != null) {
			int getTimes = Integer.valueOf(this
					.getValueString("TP22_GET_TIMES")); // ���ȴ���
			int succesTimes = Integer.valueOf(this
					.getValueString("TP22_SUCCESS_TIMES")); // ���ȳɹ�����
			if (succesTimes > getTimes) {
				this.messageBox_("���ȳɹ������ܴ������ȴ���");
				return false;
			}
		}
		// �жϲ�������Ƿ��� A00.00000��TZZ.ZZZZZ
		String PATHOLOGY_DIAG = this.getValueString("TP22_PATHOLOGY_DIAG");
		if (PATHOLOGY_DIAG.length() > 0) {
			// modify by wangb 2016/12/23 �����ٽ�ֵ���ں���Χ��Ӧ�ñ��ܿ�
			if (PATHOLOGY_DIAG.compareTo("M80000/0") < 0
					|| PATHOLOGY_DIAG.compareTo("M99890/1") > 0) {
				this.messageBox_("��������뷶Χ������M80000/0~M99890/1֮��");
				return false;
			}
		}
		// סԺ���ύ
		if ("Y".equalsIgnoreCase(this.getValueString("ADMCHK_FLG"))) {
			String mroCtz = this.getValueString("MRO_CTZ");// ������ҳ���
			if (mroCtz.equals("") || mroCtz.length() == 0) {
				this.messageBox("������ҳ����Ϊ��");
				return false;
			}
			String marrige = this.getValueString("TP1_MARRIGE");// ����״��
			if (marrige.equals("") || marrige.length() == 0) {
				this.messageBox("����״������Ϊ��");
				return false;
			}
			String count = this.getValueString("TP1_INNUM");// סԺ����
			if (count.equals("") || count.length() == 0) {
				this.messageBox("סԺ��������Ϊ��");
				return false;
			}
			String birthday = this.getValueString("TP1_BIRTH_DATE");// ��������
			if (birthday.equals("") || birthday.length() == 0) {
				this.messageBox("��������");
				return false;
			}
			String folk = this.getValueString("TP1_FOLK");// ����
			if (folk.equals("") || folk.length() == 0) {
				this.messageBox("���岻��Ϊ��");
				return false;
			}
			String nation = this.getValueString("TP1_NATION");// ����
			if (nation.equals("") || nation.length() == 0) {
				this.messageBox("��������Ϊ��");
				return false;
			}
			String sex = this.getValueString("TP1_SEX");// �Ա�
			if (sex.equals("") || sex.length() == 0) {
				this.messageBox("�Ա���Ϊ��");
				return false;
			}
			String address = this.getValueString("TP1_H_ADDRESS");// ������ַ
			if (address.equals("") || address.length() == 0) {
				this.messageBox("������ַ����Ϊ��");
				return false;
			}
			String idNo = this.getValueString("TP1_IDNO");// ���֤��
			STAIdcardValidator Idcheck = new STAIdcardValidator();
			if (!idNo.equals("")) {
				if (!Idcheck.isValidatedAllIdcard(idNo)) {
					this.messageBox("���֤�Ų��Ϸ�");
//					return false;
				}
			}
		}
		// �ж������ж�����Ƿ��Ƿ�����Ϸ�Χ
		TParm diag = this.getDiagGridData();
		String outCode = diag.getValue("OUT_DIAG_CODE1");
		String outStatus = diag.getValue("CODE1_STATUS");// �����ת��
		String pvtExt_Code = this.getValueString("TP22_EX_RSN");
		if (outCode.length() > 0) {
			if (outCode.substring(0, 1).equals("S")
					|| outCode.substring(0, 1).equals("T")) {
				if (pvtExt_Code.equals("")) {
					this.messageBox_("����д�����ж��ⲿ����");
					return false;
				}
				if (!(pvtExt_Code.substring(0, 1).compareTo("V") >= 0 && pvtExt_Code
						.substring(0, 1).compareTo("Y") <= 0)) { // �ж������ж�����Ƿ��Ƿ�����Ϸ�Χ
					this.messageBox_("�����ж��ⲿ���ط�Χ������V00.00000~ YZZ.ZZZZZ֮��");
					return false;
				}
			}
		}
		// ҽʦ�ύ
		if ("Y".equalsIgnoreCase(this.getValueString("DIAGCHK_FLG"))) {
			// ��Ժ��һ�� ��ѡ
			if ("N".equalsIgnoreCase(this.getValueString("PT24BY1"))
					&& "N".equalsIgnoreCase(this.getValueString("PT24BY2"))) {
				this.messageBox_("��ѡ���Ƿ�Ժ��һ��");
				return false;
			}
			// ���� ��ѡ
			if ("N".equalsIgnoreCase(this.getValueString("PT24SZ1"))
					&& "N".equalsIgnoreCase(this.getValueString("PT24SZ2"))) {
				this.messageBox_("��ѡ���Ƿ�����");
				return false;
			}
			// ʾ�̲��� ��ѡ
			if ("N".equalsIgnoreCase(this.getValueString("PT24SJBL1"))
					&& "N".equalsIgnoreCase(this.getValueString("PT24SJBL2"))) {
				this.messageBox_("��ѡ���Ƿ�ʾ�̲���");
				return false;
			}
			// Ѫ�� ��ѡ
			if ("N".equalsIgnoreCase(this.getValueString("TP23XX1"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX2"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX3"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX4"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX5"))
					&& "N".equalsIgnoreCase(this.getValueString("TP23XX6"))) {
				this.messageBox_("��ѡ��Ѫ��");
				return false;
			}
			// // �ж������ж�����Ƿ��Ƿ�����Ϸ�Χ
			// if (outStatus.length() <= 0) {
			// this.messageBox_("����д��Ժ����ϵ�ת�����");
			// return false;
			// }
			// shibl 20120425 begin --------------------------
			if (this.getValueString("TP2_OUT_TYPE").equals("")) {
				this.messageBox_("����д��Ժ��ʽ");
				return false;
			} else {
				if (this.getValueString("TP2_OUT_TYPE").equals("2")
						|| this.getValueString("TP2_OUT_TYPE").equals("3")) {
					if (this.getValueString("TP2_TRAN_HOSP").equals("")) {
						this.messageBox_("����д��תԺ��");
						return false;
					}
				}
				if (this.getValueString("TP2_OUT_TYPE").equals("5")) {
                    if (StringUtil.isNullString(this.getValueString("BODY_CHECK"))) {// wanglong
                                                                                     // modify
                                                                                     // 20141030
                        this.messageBox("������Ժʬ�첻��Ϊ��");
                        return false;
                    }
				}
			}
			if (this.getValueString("TP2_IN_CONDITION").equals("")) {
				this.messageBox_("����д��Ժ���");
				return false;
			}
			if (this.getValueBoolean("TP2_AGN_PLAN_FLG")) {
				if (this.getValueString("TP2_AGN_PLAN_INTENTION").equals("")) {
					this.messageBox_("����д��סԺ�ƻ�Ŀ��");
					return false;
				}
			} else {
				if (!this.getValueString("TP2_AGN_PLAN_INTENTION").equals("")) {
					this.messageBox_("�޼ƻ�������д��סԺ�ƻ�Ŀ��");
					return false;
				}
			}
			if (this.getValueBoolean("ALLEGIC_1")) {
				if (!this.getValueString("TP22_ALLEGIC").equals("")) {
					this.messageBox_("����չ���ҩ���¼");
					return false;
				}
			}
			if (this.getValueBoolean("ALLEGIC_2")) {
				if (this.getValueString("TP22_ALLEGIC").equals("")) {
					this.messageBox_("����д����ҩ���¼");
					return false;
				}
			}
			// shibl 20120425 end --------------------------
			// ­�����˻�����Ժ����ʱ��
			if (0 > this.getValueInt("TP2_BE_IN_D")
					|| this.getValueInt("TP2_BE_IN_D") > 31) {
				this.messageBox("��Ժǰ����ʱ����������Ϊ0~31");
				return false;
			}
			if (0 > this.getValueInt("TP2_BE_IN_H")
					|| this.getValueInt("TP2_BE_IN_H") > 24) {
				this.messageBox("��Ժǰ����ʱ��Сʱ����Ϊ0~24");
				return false;
			}
			if (0 > this.getValueInt("TP2_BE_IN_M")
					|| this.getValueInt("TP2_BE_IN_M") > 60) {
				this.messageBox("��Ժǰ����ʱ���������Ϊ0~60");
				return false;
			}
			if (0 > this.getValueInt("TP2_AF_IN_D")
					|| this.getValueInt("TP2_AF_IN_D") > 31) {
				this.messageBox("��Ժ�����ʱ����������Ϊ0~31");
				return false;
			}
			if (0 > this.getValueInt("TP2_AF_IN_H")
					|| this.getValueInt("TP2_AF_IN_H") > 24) {
				this.messageBox("��Ժ�����ʱ��Сʱ����Ϊ0~24");
				return false;
			}
			if (0 > this.getValueInt("TP2_AF_IN_M")
					|| this.getValueInt("TP2_AF_IN_M") > 60) {
				this.messageBox("��Ժ�����ʱ���������Ϊ0~60");
				return false;
			}
			if (!getValueString("TP2_CONFIRM_DATE").equals("")) {
				Timestamp indate = (Timestamp) getValue("TP2_IN_DATE");
				Timestamp confrimDate = (Timestamp) getValue("TP2_CONFIRM_DATE");
				if (indate.getTime() > confrimDate.getTime()) {
					this.messageBox("��Ժʱ�䲻�ܴ���ȷ��ʱ��");
					return false;
				}
			} else {
				this.messageBox("ȷ��ʱ�䲻��Ϊ��");
				return false;
			}
			if (!getValueString("TP2_OUT_DATE").equals("")) {
				Timestamp outdate = (Timestamp) getValue("TP2_OUT_DATE");
				Timestamp indate = (Timestamp) getValue("TP2_IN_DATE");
				if (indate.getTime() > outdate.getTime()) {
					this.messageBox("��Ժʱ�䲻�ܴ��ڳ�Ժʱ��");
					return false;
				}
				Timestamp confrimDate = (Timestamp) getValue("TP2_CONFIRM_DATE");
				if (outdate.getTime() < confrimDate.getTime()) {
					this.messageBox("��Ժʱ�䲻��С��ȷ��ʱ��");
					return false;
				}
			} else {
				this.messageBox("��Ժʱ�䲻��Ϊ��");
				return false;
			}
			if (this.getValueString("TP22_DIRECTOR_DR_CODE").equals("")) {
				this.messageBox("�����β���Ϊ��");
				return false;
			}
			if (this.getValueString("TP22_PROF_DR_CODE").equals("")) {
				this.messageBox("��(����)��ҽʦ����Ϊ��");
				return false;
			}
			if (this.getValueString("TP22_ATTEND_DR_CODE").equals("")) {
				this.messageBox("����ҽʦ����Ϊ��");
				return false;
			}
			if (this.getValue("TP2_VS_DR_CODE").equals("")) {
				this.messageBox("סԺҽʦ����Ϊ��");
				return false;
			}
			if (this.getValueString("TP2_VS_NURSE_CODE").equals("")) {
				this.messageBox("���λ�ʿ����Ϊ��");
				return false;
			}
			if (this.getValueString("TP4_CTRL_DR").equals("")) {
				this.messageBox("�ʿ�ҽʦ����Ϊ��");
				return false;
			}
			if (this.getValueString("TP4_CTRL_NURSE").equals("")) {
				this.messageBox("�ʿػ�ʿ����Ϊ��");
				return false;
			}
			if (this.getValueString("TP4_CTRL_DATE").equals("")) {
				this.messageBox("�ʿ����ڲ���Ϊ��");
				return false;
			} else {
				Timestamp indate = (Timestamp) getValue("TP2_IN_DATE");
				Timestamp ctlrDate = (Timestamp) getValue("TP4_CTRL_DATE");
				if (indate.getTime() > ctlrDate.getTime()) {
					this.messageBox("��Ժʱ�䲻�ܴ����ʿ�ʱ��");
					return false;
				}
			}
			
			if (!checkOpData()) {
				return false;
			}
		}
		// �����ύ
		if ("Y".equalsIgnoreCase(this.getValueString("BILCHK_FLG"))) {

		}
		// �������ύ
		if ("Y".equalsIgnoreCase(this.getValueString("QTYCHK_FLG"))) {
			if ("N".equalsIgnoreCase(this.getValueString("TP4BA1"))
					&& "N".equalsIgnoreCase(this.getValueString("TP4BA2"))
					&& "N".equalsIgnoreCase(this.getValueString("TP4BA3"))) {
				this.messageBox_("��ѡ�񲡰�����");
				return false;
			}
			if (this.getValue("TP4_ENCODER").equals("")) {
				this.messageBox("����Ա����Ϊ��");
				return false;
			}
		}
		
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		if(table.getRowCount() <= 0)
			return true;
		//�ж��Ƿ���Ҫ��д��Ժҽʦ��ע
		if(checkIsOutDocRemark()){
			this.messageBox("��ѡ����ҽʦ������Ժҽʦ������д��Ժҽʦ��ע��");
			return false;
		}
		
		return true;
	}

	/**
	 * �ж��Ƿ���Ҫ��д��Ժҽʦ��ע
	 * @return 
	 */
	public boolean checkIsOutDocRemark() {//modify by guoy 20150811
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		int rows = table.getRowCount();
		for(int i = 0;i < rows; i++){
			String docRemark = (String) table.getValueAt(i, 10);
			String docId = (String) table.getValueAt(i, 9);
			String sql = " SELECT IS_OUT_FLG FROM SYS_OPERATOR WHERE USER_ID = '" + docId + "' ";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			String outFlg = parm.getValue("IS_OUT_FLG",0);
			if("Y".equals(outFlg)){
				if("".equals(docRemark)){
					return true;
				}
			}
		}
		
		return false;
		/*
		TTable table = (TTable) this.getComponent("TP23_OP_Table");
		ArrayList<Integer> rows = new ArrayList<Integer>(table.getRowCount());//��ʼ������кż���
//		ArrayList<Integer> rows = new ArrayList<Integer>();//��ʼ������кż���
		String rowStr = "";
		int row = 0;
		for (int i = 0; i < table.getRowCount(); i++) {//Ϊ����кż����������  �����4��  �����0 1 2 3
			rows.add(i);
		}
		Map<String, String> map = table.getLockCellMap();
		Set set = map.entrySet();
        for (Iterator<Entry<String, String>> it = set.iterator(); it.hasNext();) {
            Map.Entry entry = it.next();
            String rowAndCol = (String) entry.getKey();
            rowStr = rowAndCol.substring(0, rowAndCol.indexOf(":"));
            if(rowStr != null && !"".equals(rowStr))
            	row = Integer.parseInt(rowStr);//��á���Ժҽʦ��ע���������к�
            for (int i = 0; i < rows.size(); i++) {//���кż�����ɾ������Ժҽʦ��ע����������
				if(row == rows.get(i)){
					rows.remove(i);
				}
			}
        }
        for (int i = 0; i < rows.size(); i++) {
    		if(table.getValueAt(rows.get(i), 10) == null || table.getValueAt(rows.get(i), 10).toString().length() <= 0){
	        	return true;
	        }
		}
		return false;
		*/
	}

	/**
	 * ���������Ϣ
	 * 
	 * @return boolean
	 */
	private boolean checkOpData() {
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		opTable.acceptText();
		boolean flg = false;
		for (int i = 0; i < opTable.getRowCount(); i++) {
			if (opTable.getValueAt(i, 0).toString().equals("Y")) {
				flg = true;
			}
		}
		while (opTable.getRowCount() > 0) {
			if (opTable.getItemString(opTable.getRowCount() - 1, 2).trim()
					.equals("")
					&& opTable.getItemString(opTable.getRowCount() - 1, 3)
							.trim().equals("")) {
				opTable.removeRow(opTable.getRowCount() - 1);// ɾ������ add by
				// wanglong
				// 20121109
				continue;
			} else {
				break;
			}
		}
		if (!flg && opTable.getRowCount() > 0) {
			this.messageBox_("��ѡ��һ������������Ϊ������");
			return flg;
		}
		for (int i = 0; i < opTable.getRowCount(); i++) {
			if (opTable.getValueAt(i, 2) == null) {
				this.messageBox_("����д����ICDʱ��");
				return false;
			} else {
				Timestamp opDate = opTable.getItemTimestamp(i, "OP_DATE");
				Timestamp outdate = (Timestamp) getValue("TP2_OUT_DATE");
				Timestamp indate = StringTool.rollDate((Timestamp) getValue("TP2_IN_DATE"),-1);
				if (indate.getTime() > opDate.getTime()) {
					this.messageBox_("��Ժ����-1�첻�ܴ�����������");
					return false;
				}
				if (opDate.getTime() > outdate.getTime()) {
					this.messageBox_("�������ڲ��ܴ��ڳ�Ժ����");
					return false;
				}
			}
			if (opTable.getValueAt(i, 3) != null && "".equals(opTable.getValueAt(i, 3).toString().trim())) {
				this.messageBox_("����д����ICD����");
				return false;
			}
			if (opTable.getValueAt(i, 9) != null && "".equals(opTable.getValueAt(i, 9).toString().trim())) {
				this.messageBox_("��ѡ������ҽʦ");
				return false;
			}
			if (opTable.getValueAt(i, 14) != null && "".equals(opTable.getValueAt(i, 14).toString().trim())) {
				this.messageBox_("����д����ʽ");
				return false;
			}
			if (opTable.getValueAt(i, 15) != null && "".equals(opTable.getValueAt(i, 15).toString().trim())) {
				this.messageBox_("����д���ϵȼ�");
				return false;
			}
			if (opTable.getValueAt(i, 7)!= null && !"".equals(opTable.getValueAt(i, 7).toString().trim())) {
				try {
					Integer.parseInt(opTable.getValueAt(i, 7).toString().trim());
				} catch (Exception e) {
					this.messageBox("����д��ȷ����������ʱ�䣨Сʱ��");
					return false;
				}
			}
			if (opTable.getValueAt(i, 8)!= null && !"".equals(opTable.getValueAt(i, 8).toString().trim())) {
				try {
					Integer.parseInt(opTable.getValueAt(i, 8).toString().trim());
				} catch (Exception e) {
					this.messageBox("����д��ȷ����������ʱ�䣨���ӣ�");
					return false;
				}
			}
		}
		if ((getCheckbox("OPE_TYPE_CODE1").isSelected() || getCheckbox(
				"OPE_TYPE_CODE2").isSelected())
				&& opTable.getRowCount() <= 0) {
			this.messageBox_("δ���������߲���ѡ��������������");
			return false;
		} else if (opTable.getRowCount() > 0
				&& !getCheckbox("OPE_TYPE_CODE1").isSelected()
				&& !getCheckbox("OPE_TYPE_CODE2").isSelected()) {
			this.messageBox_("�����������߱���ѡ��������������");
			return false;
		}
		return true;
	}

	/**
	 * ������������
	 */
	public void setACCOMP_DATE() {
		// ���������ֹ����
		if (this.getValue("TP2_OUT_DATE") != null) { // �жϳ�Ժ�����Ƿ�Ϊ�գ������ֹ�������Գ�Ժ����Ϊ��ʼ��ʼ����ģ���Ҫѯ��һ�£�
			int s_year = this.getValueInt("TP24_ACCOMPANY_YEAR"); // ��������
			int s_month = this.getValueInt("TP24_ACCOMPANY_MONTH"); // ����
			int s_week = this.getValueInt("TP24_ACCOMPANY_WEEK"); // ����
			Timestamp accomp_date = StringTool.getTimestamp(this.getValue(
					"TP2_OUT_DATE").toString(), "yyyy-MM-dd"); // ��ȡ��Ժ����
			if (s_week > 0) { // ��Ժ���ڼ�������
				accomp_date = StringTool.rollDate(accomp_date,
						(long) (7 * s_week));
			}
			if (s_month > 0) { // ������������������򽫳�Ժ���ڼ������� ����������ֹ����
				Calendar cal = Calendar.getInstance();
				cal.setTime(accomp_date);
				cal.add(cal.MONTH, s_month);
				accomp_date = new Timestamp(cal.getTimeInMillis()); // �����������
				// �����ֹ����
			}
			if (s_year > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(accomp_date);
				cal.add(cal.YEAR, s_year);
				accomp_date = new Timestamp(cal.getTimeInMillis()); // �����������
				// �����ֹ����
			}
			if (s_week > 0 || s_month > 0 || s_year > 0)
				this.setValue("TP24_ACCOMP_DATE", accomp_date);
			else
				this.setValue("TP24_ACCOMP_DATE", new TNull(Timestamp.class));
		} else
			// û�г�Ժ���ڣ��������ھ�Ϊ��
			this.setValue("TP24_ACCOMP_DATE", new TNull(Timestamp.class));
	}

	/**
	 * ת�������Ϣ
	 */
	public void onIntoAllecData() {
		// ����ҩ�������Ϣ
		String durgallec = "";
		TParm admDurg = MROTool.getInstance().getDrugAllErgy(MR_NO);
		if (admDurg.getCount() <= 0) {
			getRDBtn("ALLEGIC_1").setSelected(true);
			getRDBtn("ALLEGIC_2").setSelected(false);
			return;
		}
		List<String> admDurgList = new ArrayList<String>();
		for (int i = 0; i < admDurg.getCount(); i++) {
			if (!admDurgList.contains(admDurg.getValue("ALLERGY_NAME", i))) {
				admDurgList.add(admDurg.getValue("ALLERGY_NAME", i));
				if (!durgallec.equals(""))
					durgallec += ",";				
			    // add by wangqing  20180116 start
				// ҽ��վ��д���߹���ʷ��ֻ��Թ���������д'�Ǳ�Ժ��ҩ'��Ŀʱ���ڹ����������дҩƷ���ơ�
				// ������ҳ��ȡʱ�жϹ�������Ϊ���Ǳ�Ժ��ҩ'ʱ����ҳ��ҩ����������Զ������Ӧ�ġ���������������ݡ�
				if(admDurg.getValue("ALLERGY_NAME", i).equals("�Ǳ�Ժ��ҩ")){
					durgallec += admDurg.getValue("ALLERGY_NOTE", i);
				}else{
					durgallec += admDurg.getValue("ALLERGY_NAME", i);
				}
				// add by wangqing  20180116 end				
			}
		}
		this.setValue("TP22_ALLEGIC", durgallec);// ����ҩ��		
		
		// add by wangb 2017/06/27 �Զ�������������
		if (StringUtils.isEmpty(durgallec)) {
			getRDBtn("ALLEGIC_1").setSelected(true);
			getRDBtn("ALLEGIC_2").setSelected(false);
		} else {
			getRDBtn("ALLEGIC_1").setSelected(false);
			getRDBtn("ALLEGIC_2").setSelected(true);
		}
	}

	/**
	 * ת��ҽʦ��Ϣ
	 */
	public void onIntoDrData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		// ����סԺ������Ϣ adm_inp
		TParm inHospInfo = ADMTool.getInstance().getADM_INFO(parm);
		if (inHospInfo.getErrCode() < 0) {
			err("ERR:" + inHospInfo.getErrCode() + inHospInfo.getErrText()
					+ inHospInfo.getErrName());
			return;
		}
		this.setValue("TP22_DIRECTOR_DR_CODE", inHospInfo.getValue(
				"DIRECTOR_DR_CODE", 0));// ������
		this.setValue("TP22_ATTEND_DR_CODE", inHospInfo.getValue(
				"ATTEND_DR_CODE", 0));// ����ҽʦ
		this.setValue("TP2_VS_DR_CODE", inHospInfo.getValue("VS_DR_CODE", 0));// ����ҽʦ
		this.setValue("TP2_VS_NURSE_CODE", inHospInfo.getValue("VS_NURSE_CODE",
				0));// ���λ�ʿ
	}

	/**
	 * ת�������Ϣ
	 */
	public void onIntoDiagData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
			if (JOptionPane.showConfirmDialog(null, "���������ҳ���������ת���ٴ������Ϣ���Ƿ�ִ�У�", "��Ϣ",
				JOptionPane.YES_NO_OPTION) == 0) {
			TParm del = new TParm(this.getDBTool().update(getDelMroDiagSql(CASE_NO)));
			// ���Grid��
			TTable diagTable = (TTable) this.getComponent("TP21_Table2");
			if (diagTable.getParmValue().getCount() > 0)
				diagTable.setParmValue(null);
			//��ϼ�¼
			TParm admDiag = new TParm(this.getDBTool().select(
						"SELECT 'Z' AS EXEC,A.IO_TYPE AS TYPE,B.ICD_CHN_DESC AS NAME,A.ICD_CODE AS CODE,A.MAINDIAG_FLG AS MAIN,A.ICD_TYPE AS KIND," +
						" '' AS STATUS,'' AS IN_PAT_CONDITION,'' AS ADDITIONAL,'' AS ADDITIONAL_DESC,A.SEQ_NO,A.DESCRIPTION AS REMARK" +
						" FROM ADM_INPDIAG A,SYS_DIAGNOSIS B WHERE CASE_NO = '"+this.CASE_NO+"' AND A.ICD_CODE = B.ICD_CODE  AND A.IO_TYPE<>'Z'  ORDER BY IO_TYPE ASC, SEQ_NO "));
			if (admDiag.getCount() <= 0)
				return;
			diagTable.setParmValue(admDiag);
			diagTable.addRow();
		}
	}

	/**
	 * ת�����-ɾ����ҳ���sql-duzhw
	 */
	public String getDelMroDiagSql(String caseNo){
		String sql = "delete from MRO_RECORD_DIAG where case_no = '"+caseNo+"'";
		return sql;
	}

	/**
	 * ת��������Ϣ
	 */
	//2013-4-27�޸�
	public void onIntoOPData() {
		// ��ȡ������Ϣ
		TParm opeData = OPETool.getInstance().intoOPEDataForMRO(CASE_NO);
		if (opeData.getCount() <= 0) {
			return;
		}
		// ����Grid��
		TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
		opTable.removeRowAll();
		TParm parm = new TParm();
		for (int i = 0; i < opeData.getCount(); i++) {
			int opHour = (int)((StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_END_DATE", i), "ddHHmm"),"ddHHmm").getTime() - 
					StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_DATE", i),"ddHHmm"), "ddHHmm").getTime()) / 1000.0D / 60.0D / 60.0D);
			int opMinute = (int)((StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_END_DATE", i), "ddHHmm"),"ddHHmm").getTime() - 
					StringTool.getTimestamp(StringTool.getString(opeData.getTimestamp("OP_DATE", i),"ddHHmm"), "ddHHmm").getTime()) / 1000.0D / 60.0D) % 60;
			parm.setData("MAIN_FLG", i, "N");
			parm.setData("AGN_FLG", i, "Y");
			parm.setData("SEQ_NO", i, i + 1);
			parm.setData("OP_DATE", i, opeData.getData("OP_DATE", i));
			parm.setData("OP_CODE", i, opeData.getData("OP_CODE", i));
			parm.setData("OP_DESC", i, opeData.getData("OP_DESC", i));
			parm.setData("OP_LEVEL", i, opeData.getData("OP_LEVEL", i));
			parm.setData("OPE_TIME_HOUR", i, opHour);
			parm.setData("OPE_TIME_MINUTE", i, opMinute);
			parm.setData("MAIN_SUGEON", i, opeData.getData("MAIN_SUGEON", i));
			parm.setData("AST_DR1", i, opeData.getData("AST_DR1", i));
			parm.setData("AST_DR2", i, opeData.getData("AST_DR2", i));
			parm.setData("HEALTH_LEVEL", i, opeData.getData("HEALTH_LEVEL", i));
			parm.setData("ANA_WAY", i, opeData.getData("ANA_WAY", i));
			parm.setData("ANA_DR", i, opeData.getData("ANA_DR", i));
			parm.setData("NNIS_CODE", i, opeData.getData("NNIS_CODE", i));
		}
//		opTable.acceptText();
		opTable.setParmValue(parm);
	}

		
	

	/**
	 * ��Ѫ��Ϣת��
	 */
	public void onIntoBMSData() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", this.getValueString("TP1_IPD_NO"));
		TParm bms = BMSTool.getInstance().getApplyInfo(parm);
		if (bms.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.setValue("TP23_RBC", bms.getValue("RBC"));
		this.setValue("TP23_PLATE", bms.getValue("PLATE"));
		this.setValue("TP23_PLASMA", bms.getValue("PLASMA"));
		this.setValue("TP23_WHOLE_BLOOD", bms.getValue("WHOLE_BLOOD"));
		this.setValue("TP23_OTH_BLOOD", bms.getValue("OTH_BLOOD"));
		if ("1".equals(bms.getValue("TRANS_REACTION"))) {
			this.setValue("PT23SX1", true);
		} else if ("2".equals(bms.getValue("TRANS_REACTION"))) {
			this.setValue("PT23SX2", true);
		}
		
		// add by wangb 2018/1/8 ����LISѪ�ͼ������ͬ��������Ϣ��Ѫ�� START
		BMSTool.getInstance().updatePatBloodByLisData(MR_NO);
		// add by wangb 2018/1/8 ����LISѪ�ͼ������ͬ��������Ϣ��Ѫ�� END
		
		Pat pat = Pat.onQueryByMrNo(MR_NO);
		if ("A".equals(pat.getBloodType())) {
			this.setValue("TP23XX1", true);
		} else if ("B".equals(pat.getBloodType())) {
			this.setValue("TP23XX2", true);
		} else if ("O".equals(pat.getBloodType())) {
			this.setValue("TP23XX3", true);
		} else if ("AB".equals(pat.getBloodType())) {
			this.setValue("TP23XX4", true);
		} else if ("".equals(pat.getBloodType())) {
			this.setValue("TP23XX6", true);
		} else {
			this.setValue("TP23XX5", true);
		}
		// =======pangben modify 20110629 start RHѪ����ʾ
		if ("+".equals(pat.getBloodRHType())) {
			this.setValue("PT23RH2", true);
		} else if ("-".equals(pat.getBloodRHType())) {
			this.setValue("PT23RH1", true);
		} else if ("".equals(pat.getBloodRHType())) {
			this.setValue("PT23RH4", true);
		} else {
			this.setValue("PT23RH3", true);
		}
		// =======pangben modify 20110629 stop
		setSXFYByBMSData(cmpNames);
	}

	/**
	 * ���������� ���߽���
	 */
	public void onChild() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("MR_NO", MR_NO);
		parm.setData("IPD_NO", this.getValueString("TP1_IPD_NO"));
		this.openDialog("%ROOT%\\config\\adm\\ADMChildImmunity.x", parm);
	}

	/**
	 * ҳǩѡ�������ʱִ�л��ܷ��� ==========pangben modify 20110623
	 */
	public void onChangeStart() {
		TTabbedPane tP = (TTabbedPane) this.getComponent("tTabbedPane_0");
		switch (tP.getSelectedIndex()) {
		case 1:
			TTable table = (TTable) this.getComponent("TP23_OP_Table");
			if(table.getRowCount() <= 0 && isFirst){
				this.OPTableBind(MR_NO,CASE_NO);
				isFirst = false;
			}
			break;
		case 2:
			// onIntoDr(); // ����ҽ����Ϣ
			onFinance(); // �������
			break;
		}
	}

	/**
	 * �����о����� ===============pangben modify 20110710
	 */
	public void onCase() {
		// shibl 20120807 ��ʾ�û��Ƿ������о����� ��ֹ���
		if (JOptionPane.showConfirmDialog(null, "�Ƿ������о�����?", "��ʾ��Ϣ",
				JOptionPane.YES_NO_OPTION) != 0) {
			return;
		}
		boolean istrue = false;
		StringBuffer value = new StringBuffer();
		// 5.��˵��Ӳ�������ȱʧ
		String sql = QUERY_EMR_SQL.replaceFirst("#", " FILE_PATH , FILE_NAME ")
				.replaceFirst("#", CASE_NO);
		TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (queryParm.getCount() <= 0) {
			this.messageBox("û����Ҫ���ɵ�����");
			return;
		}
		for (int i = 0; i < queryParm.getCount(); i++) {
			TParm tagParm = new TParm();
			tagParm.setData("FILE_PATH", queryParm.getValue("FILE_PATH", i));
			tagParm.setData("FILE_NAME", queryParm.getValue("FILE_NAME", i));

			if (GetWordValue.getInstance().saveTestWord(tagParm)) {
				istrue = true;
			} else {
				value.append(queryParm.getValue("FILE_NAME", i)).append(",\n");
			}
		}
		if (istrue)
			this.messageBox("�о������������");
		else {
			this.messageBox("�о�����:\n"
					+ value.substring(0, value.lastIndexOf(",")) + "\n����ʧ��");
		}
	}

	/**
	 * ��Ժ��ʽ����
	 */
	public void outTypeSelect() {
		if (this.getValue("TP2_OUT_TYPE").equals("2")
				|| this.getValue("TP2_OUT_TYPE").equals("3")) {
			callFunction("UI|TP2_TRAN_HOSP|setEnabled", true);
		} else {
			callFunction("UI|TP2_TRAN_HOSP|setEnabled", false);
		}
		this.setValue("TP2_TRAN_HOSP", "");
		this.setValue("TRAN_HOSP_OTHER", "");
		this.onSelectTran();
	}

	/**
	 * �õ�TCheckBox�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TCheckBox getCheckbox(String tag) {
		return (TCheckBox) this.getComponent(tag);
		
	}
	
	/**
	 * ���0
	 * 
	 * @param str
	 * @return
	 */
	private String getFormatString(String str) {
		String datastr = "";
		if (str.length() > 0 && str.length() < 2) {
			for (int i = 0; i < (2 - str.length()); i++) {
				datastr = "0" + datastr;
			}
		}
		return datastr + str;
	}

	/**
	 * ��ת�ƿ����м��"|"
	 * 
	 * @return
	 */
	private String getTranDept() {
		String str = "";
		TParm result = new TParm();
		TParm inparm = new TParm();
		inparm.setData("CASE_NO", CASE_NO);
		TParm trandept = MROTool.getInstance().getTranDept(inparm);
		for (int i = 0; i < trandept.getCount(); i++) {
			if (str.trim().length() > 0) {
				str += "|";
			}
			str += trandept.getValue("TRAN_DEPT", i);
		}
		return str;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��Ժ���ڸ���ʱ����������סԺ����
	 */
	public boolean onChoiceDSDate() {// modify by wanglong 20121029
		Timestamp inDate = (Timestamp) this.getValue("TP2_IN_DATE");// ��Ժʱ��
		Timestamp dsDate = (Timestamp) this.getValue("TP2_OUT_DATE");// ��Ժʱ��
		if (inDate == null) {
			messageBox("��Ժʱ�����ò���ȷ");
			return false;
		}
		if (dsDate == null) {// �����Ժʱ�䣬סԺ�������㵽��ǰʱ��
			dsDate = new Timestamp(new Date().getTime());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// ��ʽ��������
		String strInDate = sdf.format(inDate);
		String strDsDate = sdf.format(dsDate);
		inDate = java.sql.Timestamp.valueOf(strInDate + " 00:00:00.000");
		dsDate = java.sql.Timestamp.valueOf(strDsDate + " 00:00:00.000");
		int stayDays = StringTool.getDateDiffer(dsDate, inDate);// ����סԺ����
		if (stayDays < 0) {
			this.messageBox("��Ժʱ�䲻��������Ժʱ��");
			return false;
		} else if (stayDays == 0) {
			stayDays = 1;
			this.setValue("TP2_REAL_STAY_DAYS", stayDays + "");// ʵ��סԺ����
			return true;
		} else {
			this.setValue("TP2_REAL_STAY_DAYS", stayDays + "");// ʵ��סԺ����
			return true;
		}
	}

	/**
	 * ��Ժ�����Ƿ�仯 shibl 20130108 add
	 * 
	 * @return
	 */
	private String IsModifydsDate(Timestamp dsDate) {
		String flg = "N";
		String sql = "SELECT OUT_DATE FROM MRO_RECORD WHERE CASE_NO='"
				+ this.CASE_NO + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Timestamp olddsDate = result.getTimestamp("OUT_DATE", 0);
	    if(!StringTool.getString(olddsDate, "yyyyMMddHHmmss").equals(StringTool.getString(dsDate, "yyyyMMddHHmmss"))){
			flg = "Y";
		}
		return flg;
	}
	
	/**
	 * ͨ����Ѫ��Ϣ������Ѫ��Ӧ��ֵ
	 * @param cmpNames �����������
	 * @param isInit �Ƿ�Ϊ��ʼ��
	 */
	private void setSXFYByBMSData(String[] cmpNames){
		//������ǣ���¼��Ѫ��Ϣֵ�Ƿ����0�����0��С��0δ������ʾ
		boolean flag = false;
		//ѭ���ж�ÿһ����Ѫ��Ϣ��ֵ�Ƿ����0 �������0����Ѫ��Ϣ�������Ϊtrue
		for (int i = 0; i < cmpNames.length; i++) {
			if(Integer.parseInt(this.getValue(cmpNames[i]).toString()) > 0) flag = true;
		}
		//�ж���Ѫ��Ϣ���������Ѫ��Ӧ��Ĭ��ѡ���ѡ��Χ
		if(flag){
			callFunction("UI|PT23SX1|setEnabled", true);
			callFunction("UI|PT23SX2|setEnabled", true);
			callFunction("UI|PT23SX3|setEnabled", false);
			//�����Ѫ��Ϣ����0������ҽ����Ѫ��Ӧ��ѡ��Ϊ���С�����������Ѫ��ӦΪ���ޡ�
			if(!getRDBtn("PT23SX1").isSelected())
				getRDBtn("PT23SX2").setSelected(true);
		}else{
			callFunction("UI|PT23SX1|setEnabled", false);
			callFunction("UI|PT23SX2|setEnabled", false);
			callFunction("UI|PT23SX3|setEnabled", true);
			getRDBtn("PT23SX3").setSelected(true);
		}
	}
	
	/**
	 * �������ӽ������
	 * @param cmpNames �����������
	 */
	private void setCmpFocusListener(final String[] cmpNames){
		//��������������Ϊ�ջ��������ݣ�ֱ�ӷ���
		if(null == cmpNames || cmpNames.length <= 0) return;
		//ѭ��Ϊ�����ӽ�������¼�
		for (int i = 0; i < cmpNames.length; i++) {
			((JComponent) this.getComponent(cmpNames[i])).addFocusListener(new FocusListener() {

				public void focusGained(FocusEvent e) {
					// TODO Auto-generated method stub
					setSXFYByBMSData(cmpNames);
				}

				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
				}
			});
		}
	}
	
	/**
	 * ����ICU��Ϣ
	 */
	public void onIntoICUData(){
		TTable icuTable = (TTable) this.getComponent("TP25_ICU_Table");
		TParm icuParm = MROTool.getInstance().getICUParm(CASE_NO);
		TParm tableParm = new TParm();
		for (int i = 0; i < 5; i++) {
			String deptCode = icuParm.getValue("DEPT_CODE", i);
//			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			tableParm.setData("ICU_ROOM", i, deptCode);
			tableParm.setData("IN_DATE", i, icuParm.getData("IN_DATE", i));
			tableParm.setData("OUT_DATE", i, icuParm.getData("OUT_DATE", i));
		}
		int row = icuParm.getCount();
		if(row < 0)
			row = 0;
		for (int i = row; i < 5; i++) {
			tableParm.setData("ICU_ROOM", i, "");
			tableParm.setData("IN_DATE", i, new TNull(Timestamp.class));
			tableParm.setData("OUT_DATE", i, new TNull(Timestamp.class));
		}
		icuTable.setParmValue(tableParm);
	}
	/**
	 * ���ò�����־
	 */
	public  void onPatlog(){
		TParm parm=new TParm();
		parm.setData("MR_NO", this.MR_NO);
		parm.setData("IN_DATE", this.getValue("TP2_IN_DATE"));
		openDialog("%ROOT%\\config\\sys\\SYSPatLogQuery.x",parm);
	}
	/**
	 * У���Ƿ���ڲ�����־
	 * @return
	 */
	public boolean onCheckPatlog(){
		Timestamp indate=(Timestamp)getValue("TP2_IN_DATE");
		if(indate==null){
			return false;
		}
		String sql="SELECT MODI_ITEM FROM SYS_PATLOG WHERE MR_NO='"+this.MR_NO+
		"' AND OPT_DATE>=TO_DATE('"+StringTool.getString(indate, "yyyyMMddHHmmss")+"','YYYYMMDDHH24MISS')";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()<0||parm.getCount()<=0){
			return false;
		}
		return true;
	}
	
    /**
     * �����ײ�
     */
    public void onOpPack() {//wanglong add 20150225
        TParm parm = new TParm();
        parm.setData("DEPT_OR_DR", 2);
        parm.setData("DEPT_CODE", Operator.getDept());
        parm.setData("USER_ID", Operator.getID());
        parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
        TWindow window = (TWindow) this.openWindow("%ROOT%\\config\\ope\\OPEPackICD.x", parm, true);
//        window.setX((ImageTool.getScreenWidth() - window.getWidth())/2);
//        window.setY((ImageTool.getScreenHeight() - window.getHeight())/2);
        window.setVisible(true);
        
    }
    
    /**
     * �ײ͸�ֵ
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onQuoteSheetList(Object obj) {//wanglong add 20150225
        boolean falg = true;
        if (obj != null) {
            List orderList = (ArrayList) obj;
            Iterator iter = orderList.iterator();
            TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
            TParm parmValue = opTable.getParmValue();
            if (parmValue == null) {
                parmValue = new TParm();
            }
            Timestamp now = SystemTool.getInstance().getDate();
            while (iter.hasNext()) {
                TParm temp = (TParm) iter.next();
                parmValue.addData("CASE_NO", "");
                parmValue.addData("MR_NO", "");
                parmValue.addData("IPD_NO", "");
                parmValue.addData("MAIN_FLG", "N");
                parmValue.addData("AGN_FLG", "N");
                parmValue.addData("OP_DATE", new TNull(Timestamp.class));
                parmValue.addData("OP_CODE", temp.getValue("OP_CODE"));
                parmValue.addData("OP_DESC", temp.getValue("OP_DESC"));
                parmValue.addData("OP_LEVEL", StringUtil.getDesc("SYS_OPERATIONICD",
                                                                 "OPE_LEVEL",
                                                                 "OPERATION_ICD='"
                                                                         + temp.getValue("OP_CODE")
                                                                         + "'"));
                parmValue.addData("OP_REMARK", "");
                parmValue.addData("OPE_SITE", "");
                parmValue.addData("OPE_TIME", "");
                parmValue.addData("OPE_TIME_HOUR", "");
                parmValue.addData("OPE_TIME_MINUTE", "");
                parmValue.addData("MAIN_SUGEON", "");
                parmValue.addData("MAIN_SUGEON_REMARK", "");
                parmValue.addData("AST_DR1", "");
                parmValue.addData("AST_DR2", "");
                parmValue.addData("HEALTH_LEVEL", "");
                parmValue.addData("ANA_WAY", "");
                parmValue.addData("ANA_DR", "");
                parmValue.addData("ANA_LEVEL", "");
                parmValue.addData("NNIS_CODE", "");
                parmValue.addData("SEQ_NO",
                                  parmValue.getInt("SEQ_NO", parmValue.getCount("SEQ_NO") - 1) + 1);
                parmValue.addData("OPT_USER", Operator.getID());
                parmValue.addData("OPT_DATE", now);
                parmValue.addData("OPT_TERM", Operator.getIP());
            }
            parmValue.setCount(parmValue.getCount("SEQ_NO"));
            opTable.setParmValue(parmValue);
        }
        return falg;
    }
    
    /**
     * ����table��ĳ�м�¼����һ��
     */
    public void onUpOPRecord() {//wanglong add 20150225
        TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
        if (opTable.getRowCount() <= 0) {
            return;
        }
        TParm parmValue = opTable.getParmValue();
        int row = opTable.getSelectedRow();
        if (row == 0) {
            return;
        }
        int preRow = row - 1;
        TParm rowParm = parmValue.getRow(row);
        parmValue.removeRow(row);
        parmValue.insertRow(preRow);
        String[] names = parmValue.getNames();
        for (int i = 0; i < names.length; i++) {
            parmValue.setData(names[i], preRow, rowParm.getData(names[i]));
        }
        parmValue.setCount(parmValue.getCount(names[0]));
        opTable.setParmValue(parmValue);
        opTable.setSelectedRow(preRow);
    }
    
    /**
     * ����table��ĳ�м�¼����һ��
     */
    public void onDownOPRecord() {//wanglong add 20150225
        TTable opTable = (TTable) this.getComponent("TP23_OP_Table");
        if (opTable.getRowCount() <= 0) {
            return;
        }
        TParm parmValue = opTable.getParmValue();
        int row = opTable.getSelectedRow();
        if (row == opTable.getRowCount() - 1) {
            return;
        }
        int nextRow = row + 1;
        TParm rowParm = parmValue.getRow(row);
        parmValue.removeRow(row);
        parmValue.insertRow(nextRow);
        String[] names = parmValue.getNames();
        for (int i = 0; i < names.length; i++) {
            parmValue.setData(names[i], nextRow, rowParm.getData(names[i]));
        }
        parmValue.setCount(parmValue.getCount(names[0]));
        opTable.setParmValue(parmValue);
        opTable.setSelectedRow(nextRow);
    }
	
    /**
     * add by wukai on 20170406
     * �����Ϣ����
     */
    public void onUpDiagRecord() {
    	 TTable table = (TTable) this.getComponent("TP21_Table2");
         if (table.getRowCount() <= 1) {
             return;
         }
         TParm parmValue = table.getParmValue();
         int row = table.getSelectedRow();
         if (row == 0 || row == table.getRowCount() - 1) {
             return;
         }
         int preRow = row - 1;
         TParm rowParm = parmValue.getRow(row);
         TParm preRowParm = parmValue.getRow(preRow);
         parmValue.removeRow(row);
         parmValue.insertRow(preRow);
         String[] names = parmValue.getNames();
         for (int i = 0; i < names.length; i++) {
        	 //�Ե����
        	 if("SEQ_NO".equals(names[i])) {
        		 parmValue.setData(names[i], preRow, preRowParm.getData(names[i]));
        		 parmValue.setData(names[i], row, rowParm.getData(names[i]));
        	 } else {
        		 parmValue.setData(names[i], preRow, rowParm.getData(names[i]));
        	 }
         }
         parmValue.setCount(parmValue.getCount(names[0]));
         table.setParmValue(parmValue);
         table.setSelectedRow(preRow);
    }
    
    /**
     * add by wukai on 20170406
     * �����Ϣ����
     */
    public void onDownDiagRecord() {
    	 TTable table = (TTable) this.getComponent("TP21_Table2");
         if (table.getRowCount() <= 1) {
             return;
         }
         TParm parmValue = table.getParmValue();
         int row = table.getSelectedRow();
         if (row == table.getRowCount() - 1 || row == table.getRowCount() - 2) {
             return;
         }
         int nextRow = row + 1;
         TParm rowParm = parmValue.getRow(row);
         TParm nextRowParm = parmValue.getRow(nextRow);
         parmValue.removeRow(row);
         parmValue.insertRow(nextRow);
         String[] names = parmValue.getNames();
         for (int i = 0; i < names.length; i++) {
        	 if("SEQ_NO".equals(names[i])) {
        		 parmValue.setData(names[i], nextRow, nextRowParm.getData(names[i]));
        		 parmValue.setData(names[i], row, rowParm.getData(names[i]));
        	 } else {
        		 parmValue.setData(names[i], nextRow, rowParm.getData(names[i]));
        	 }
         }
         parmValue.setCount(parmValue.getCount(names[0]));
         table.setParmValue(parmValue);
         table.setSelectedRow(nextRow);
    }
    
    
	// modified by WangQing 20170315
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
	
	
	
	
}