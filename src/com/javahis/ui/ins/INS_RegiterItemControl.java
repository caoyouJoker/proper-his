package com.javahis.ui.ins;

import java.awt.Component;
import java.util.Vector;

import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;

public class INS_RegiterItemControl extends TControl {
	private TTextField NHI_CODE_Q;
	private TRadioButton ADM_TYPE_O;
	private TRadioButton ADM_TYPE_E;
	private TRadioButton ADM_TYPE_I;
	private TTextField NHI_CODE;
	private TTextField NHI_DESC;
//	private TCheckBox cADM_TYPE_O;
//	private TCheckBox cADM_TYPE_E;
//	private TCheckBox cADM_TYPE_I;
	private TComboBox cADM_TYPE;
	private TTextField UNIT;
	private TTextField OWT_AMT;
	private TComboBox OUTSIDE_FLG;
	private TTextArea MINISTRY_HEALTHNO;
	private TTextArea CONNOTATION_PROJECT;
	private TTable ICD10;
	private TTextArea CLINICAL_SIGNIFICANCE;
	private TTextField MEDICAL_MATERIALS;
	private TTable MEDICAL_MATERIALS_LIST;
	private TTextField REAGENT;
	private TTable REAGENT_LIST;
	private TTextFormat OUTSIDE_HOSP_CODE;
	private TTable OUTSIDE_HOSP_CODE_LIST;
	private TTextFormat APPARATUS;
	private TTable APPARATUS_LIST;
	private TTextFormat DEPT_CODE;
	private TTable DEPT_CODE_LIST;
	private TTextArea REMARK;
	private TTextArea SPECIAL_CASE;
	private boolean updateFlg = false;
	private TParm regionParm; // ҽ���������

	public void onInit() {
		super.onInit();
		onComponentInit();// ���������ʼ��
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
		OUTSIDE_FLG.setSelectedIndex(0);
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm recptype = this.getInputParm();
		if (recptype == null) {
			updateFlg = false;
		} else {
			this.setValue("NHI_CODE", recptype.getValue("NHI_CODE"));
			this.setValue("ADM_TYPE", recptype.getValue("ADM_TYPE"));
			TParm data = this.onQuery(recptype.getValue("NHI_CODE"),recptype.getValue("ADM_TYPE"));
			updateFlg = true;
			this.onSetUI(data);
		}
	}

	public void onComponentInit() {
		NHI_CODE_Q = (TTextField) getComponent("NHI_CODE_Q");
		
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "");
		// ���õ����˵�
		NHI_CODE_Q.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		NHI_CODE_Q.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		ADM_TYPE_O = (TRadioButton) getComponent("ADM_TYPE_O");
		ADM_TYPE_E = (TRadioButton) getComponent("ADM_TYPE_E");
		ADM_TYPE_I = (TRadioButton) getComponent("ADM_TYPE_I");
		NHI_CODE = (TTextField) getComponent("NHI_CODE");
		NHI_DESC = (TTextField) getComponent("NHI_DESC");
//		cADM_TYPE_O = (TCheckBox) getComponent("cADM_TYPE_O");
//		cADM_TYPE_E = (TCheckBox) getComponent("cADM_TYPE_E");
//		cADM_TYPE_I = (TCheckBox) getComponent("cADM_TYPE_I");
		cADM_TYPE = (TComboBox) getComponent("cADM_TYPE");
		cADM_TYPE.setSelectedIndex(0);
		UNIT = (TTextField) getComponent("UNIT");
		OWT_AMT = (TTextField) getComponent("OWT_AMT");
		OUTSIDE_FLG = (TComboBox) getComponent("OUTSIDE_FLG");
		OUTSIDE_FLG.setSelectedIndex(0);
		MINISTRY_HEALTHNO = (TTextArea) getComponent("MINISTRY_HEALTHNO");
		CONNOTATION_PROJECT = (TTextArea) getComponent("CONNOTATION_PROJECT");
		ICD10 = (TTable) getComponent("ICD10");
		CLINICAL_SIGNIFICANCE = (TTextArea) getComponent("CLINICAL_SIGNIFICANCE");
		MEDICAL_MATERIALS = (TTextField) getComponent("MEDICAL_MATERIALS");
		MEDICAL_MATERIALS_LIST = (TTable) getComponent("MEDICAL_MATERIALS_LIST");
		REAGENT = (TTextField) getComponent("REAGENT");
		REAGENT_LIST = (TTable) getComponent("REAGENT_LIST");
		OUTSIDE_HOSP_CODE = (TTextFormat) getComponent("OUTSIDE_HOSP_CODE");
		OUTSIDE_HOSP_CODE_LIST = (TTable) getComponent("OUTSIDE_HOSP_CODE_LIST");
		APPARATUS = (TTextFormat) getComponent("APPARATUS");
		APPARATUS_LIST = (TTable) getComponent("APPARATUS_LIST");
		DEPT_CODE = (TTextFormat) getComponent("DEPT_CODE");
		DEPT_CODE_LIST = (TTable) getComponent("DEPT_CODE_LIST");
		REMARK = (TTextArea) getComponent("REMARK");
		SPECIAL_CASE = (TTextArea) getComponent("SPECIAL_CASE");
		onTTableInit();
	}

	public void onTTableInit() {
//		ICD10.setHeader("ɾ��,40,boolean;����,80;����,120");
//		ICD10.setParmMap("CHOOSE;CODE;DESC");
//		ICD10.setItem("CODE;DESC");
//		ICD10.setColumnHorizontalAlignmentData("0,left;1,left;2,left");
		
		ICD10.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onCreateEditComponent");
		ICD10.addRow();
		OrderList orderList = new OrderList();
		ICD10.addItem("OrderList", orderList);
//      //���Gridֵ�ı��¼�
        this.addEventListener("Daily_Table->" + TTableEvent.CHANGE_VALUE,
                              "onDiagTableValueCharge");
		OUTSIDE_HOSP_CODE_LIST
				.setHeader("ɾ��,30,boolean;����,60;����,100,OUTSIDE_HOSP_CODE");
		OUTSIDE_HOSP_CODE_LIST.setItem("OUTSIDE_HOSP_CODE");
		OUTSIDE_HOSP_CODE_LIST.setParmMap("CHOOSE;CODE;CODE1");
		OUTSIDE_HOSP_CODE_LIST
				.setColumnHorizontalAlignmentData("1,left;2,left;3,left");
		// ////////////////////////////////////////////////////////////////////////
		APPARATUS_LIST.setHeader("ɾ��,30,boolean;����,60;����,100,APPARATUS");
		APPARATUS_LIST.setItem("APPARATUS");
		APPARATUS_LIST.setParmMap("CHOOSE;CODE;CODE1");
		APPARATUS_LIST
				.setColumnHorizontalAlignmentData("1,left;2,left");
		// ///////////////////////////////////////////////////////////////////////
		DEPT_CODE_LIST.setHeader("ɾ��,30,boolean;����,60;����,100,DEPT_CODE");
		DEPT_CODE_LIST.setItem("DEPT_CODE");
		DEPT_CODE_LIST.setParmMap("CHOOSE;CODE;CODE1");
		DEPT_CODE_LIST
				.setColumnHorizontalAlignmentData("1,left;2,left");
	}

	/**
	 * ��Ҫִ�е����� =================pangben 2013-3-10
	 * 
	 * @return
	 */
	// private TParm onExeParm(TTable table) {
	// table.acceptText();
	// TParm parm = table.getParmValue();
	// TParm result = new TParm();
	// int countParm = parm.getCount();
	// int index = 0;
	// for (int i = 0; i < countParm; i++) {
	// if ("N".equalsIgnoreCase(parm.getValue("CHOOSE", i))) {
	// continue;
	// }
	// if (StringUtil.isNullString(parm.getValue("CODE", i))) {
	// continue;
	// }
	// result.setRowData(index, parm, i);
	// index++;
	// }
	// result.setCount(index);
	// return result;
	// }

	/**
	 * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
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
			Vector e = (Vector) parm.getData("ICD_ENG_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i))) {
					if ("en".equals(INS_RegiterItemControl.this.getLanguage())) {
						return "" + e.get(i);
					} else {
						return "" + d.get(i);
					}
				}
			}
			return s;
		}
	}

	/**
	 *��ϵ������� ICD10
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ����ICD10�Ի������
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����ICD10��������
		textfield.setPopupMenuParameter("ICD10", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newAgentOrder");
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
		TTable table = (TTable) this.callFunction("UI|ICD10|getThis");
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		table.setItem(table.getSelectedRow(), "CODE", orderCode);
		if ("en".equals(this.getLanguage()))
			table.setItem(table.getSelectedRow(), "CODE1", parm
					.getValue("ICD_ENG_DESC"));
		else
			table.setItem(table.getSelectedRow(), "CODE1", parm
					.getValue("ICD_CHN_DESC"));
		int rowNo=table.addRow();
		table.setSelectedRow(rowNo);
	}

	/**
	 * ���Grid ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDiagTableValueCharge(Object obj) {
		TTable DiagGrid = (TTable) this.getComponent("ICD10");
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node.getColumn() == 1) {
			if (node.getRow() == (DiagGrid.getRowCount() - 1))
				DiagGrid.addRow();
		}
	}

	/**
	 * ҽ�ò������Ӱ�ť�¼�
	 */
	public void onAddMedicalMaterialsTable() {
		String userid = this.getValueString("MEDICAL_MATERIALS");// ��ò�����Ϣuser_id
		if (userid.length() > 0) {
			TTable table = (TTable) this.getComponent("MEDICAL_MATERIALS_LIST");
			int rowIndex = table.addRow();
			table.setValueAt("N", rowIndex, 0);
			table.setValueAt(userid, rowIndex, 1);
		}
	}

	/**
	 * ��ȡҽ�ò�������
	 * 
	 * @return TParm
	 */
	private String getMedicalMaterialsData() {
		TTable Table = (TTable) this.getComponent("MEDICAL_MATERIALS_LIST");
		Table.acceptText();
		String parm = "";
		for (int i = 0; i < Table.getRowCount(); i++) {
			if ("Y".equals(Table.getValueAt(i, 0))) {
				continue;
			}
			if (Table.getValueAt(i, 1).toString().trim().length() > 0) {
				parm = parm + String.valueOf(Table.getValueAt(i, 1));
			}
			if (i < (Table.getRowCount() - 1)) {
				parm = parm + "@";
			}
		}
		if (parm.equals("")) {
			parm = "��";
		}
		return parm;
	}

	/**
	 * �Լ����Ӱ�ť�¼�
	 */
	public void onAddReagentTable() {
		String userid = this.getValueString("REAGENT");// ��ò�����Ϣuser_id
		if (userid.length() > 0) {
			TTable table = (TTable) this.getComponent("REAGENT_LIST");
			int rowIndex = table.addRow();
			table.setValueAt("N", rowIndex, 0);
			table.setValueAt(userid, rowIndex, 1);
		}
	}

	/**
	 * ��ȡ�Լ�����
	 * 
	 * @return TParm
	 */
	private String getReagentData() {
		TTable Table = (TTable) this.getComponent("REAGENT_LIST");
		Table.acceptText();
		String parm = "";
		for (int i = 0; i < Table.getRowCount(); i++) {
			if ("Y".equals(Table.getValueAt(i, 0))) {
				continue;
			}
			if (Table.getValueAt(i, 1).toString().trim().length() > 0) {
				parm = parm + String.valueOf(Table.getValueAt(i, 1));
			}
			if (i < (Table.getRowCount() - 1)) {
				parm = parm + "@";
			}
		}
		if (parm.equals("")) {
			parm = "��";
		}
		return parm;
	}

	/**
	 * ���ҽԺ���Ӱ�ť�¼�
	 */
	public void onAddOutsideHospCodeTable() {
		String userid = this.getValueString("OUTSIDE_HOSP_CODE");// ��ò�����Ϣuser_id
		if (userid.length() > 0) {
			addTTable(this.OUTSIDE_HOSP_CODE_LIST,this.OUTSIDE_HOSP_CODE);
		}
	}

	/**
	 * ��ȡ���ҽԺ����
	 * 
	 * @return TParm
	 */
	private String getOutsideHospCodeData() {
		TTable Table = (TTable) this.getComponent("OUTSIDE_HOSP_CODE_LIST");
		Table.acceptText();
		String parm = "";
		for (int i = 0; i < Table.getRowCount(); i++) {
			if ("Y".equals(Table.getValueAt(i, 0))&&
					"".equals(Table.getValueAt(i, 1))) {
				continue;
			}
			if (Table.getValueAt(i, 1).toString().trim().length() > 0) {
				parm = parm + String.valueOf(Table.getValueAt(i, 1));
			}
			if (i < (Table.getRowCount() - 1)) {
				parm = parm + "@";
			}
		}
		return parm;
	}

	/**
	 * �����豸���Ӱ�ť�¼�
	 */
	public void onAddApparatusTable() {
		String userid = this.getValueString("APPARATUS");// ��ò�����Ϣuser_id
		if (userid.length() > 0) {
			addTTable(this.APPARATUS_LIST,this.APPARATUS);
		}
	}

	/**
	 * ��ȡ�����豸����
	 * 
	 * @return TParm
	 */
	private String getApparatusData() {
		TTable Table = (TTable) this.getComponent("APPARATUS_LIST");
		Table.acceptText();
		String parm = "";
		for (int i = 0; i < Table.getRowCount(); i++) {
			if ("Y".equals(Table.getValueAt(i, 0))) {
				continue;
			}
			if (Table.getValueAt(i, 1).toString().trim().length() > 0) {
				parm = parm + String.valueOf(Table.getValueAt(i, 1));
			}
			if (i < (Table.getRowCount() - 1)) {
				parm = parm + "@";
			}
		}
		if (parm.equals("")) {
			parm = "��";
		}
		return parm;
	}

	/**
	 * �������Ӱ�ť�¼�
	 */
	public void onAddDeptCodeTable() {
		String userid = this.getValueString("DEPT_CODE");// ��ò�����Ϣuser_id
		if (userid.length() > 0) {
			addTTable(this.DEPT_CODE_LIST,this.DEPT_CODE);
		}
	}
	/**
	 * д����,Ժ��,�豸Ttable
	 * 
	 * @return TParm
	 */
public void addTTable(TTable table,TTextFormat tTextFormat){
	 TParm parmValue=table.getParmValue();
	 if(parmValue==null){
	     parmValue=new TParm();
	 }
      parmValue.addData("CHOOSE", "N" );
      parmValue.addData("CODE", tTextFormat.getValue()  );
      parmValue.addData("CODE1", tTextFormat.getValue());
      parmValue.setCount(parmValue.getCount("CODE"));
      table.setParmValue(parmValue);
}
	/**
	 * ��ȡ��������
	 * 
	 * @return TParm
	 */
	private String getDeptCodeData() {
		TTable Table = (TTable) this.getComponent("DEPT_CODE_LIST");
		Table.acceptText();
		String str = "";
		for (int i = 0; i < Table.getRowCount(); i++) {
			if ("Y".equals(Table.getValueAt(i, 0))) {
				continue;
			}
			if (Table.getValueAt(i, 1).toString().trim().length() > 0) {
				str = str + String.valueOf(Table.getValueAt(i, 1));
			}
			if (i < (Table.getRowCount() - 1)) {
				str = str + "@";
			}
		}
		return str;
	}

	/**
	 * ��ȡ�������
	 * 
	 * @return TParm
	 */
	private TParm getICD10Data() {
		TTable Table = (TTable) this.getComponent("ICD10");
		Table.acceptText();
		String code = "";
		String desc = "";
		for (int i = 0; i < Table.getRowCount(); i++) {
			if ("Y".equals(Table.getValueAt(i, 0))) {
				continue;
			}
			if (Table.getValueAt(i, 1).toString().trim().length() > 0) {
				code = code + String.valueOf(Table.getValueAt(i, 1));
				desc = desc + String.valueOf(Table.getValueAt(i, 2));
			}
			if (i < (Table.getRowCount() - 2)) {
				code = code + "@";
				desc = desc + "@";
			}
		}
		TParm returnTParm = new TParm();
		returnTParm.addData("ICD_CODE_LIST", code);
		returnTParm.addData("ICD_DESC_LIST", desc);
		return returnTParm;
	}

	/**
	 * ��ҽ�����ѯҽ����Ϣ
	 * 
	 * @return TParm
	 */
	public void onQueryNhiCode() {
		System.out.println("onQueryNhiCode()");
		String NHI_CODE_Q = this.getValueString("NHI_CODE_Q").trim();
		if (NHI_CODE_Q.equals("")) {
			return;
		}	
		String Sql = "";
//		if (ADM_TYPE_I.isSelected()) {
//			Sql = " SELECT A.SFXMBM AS NHI_CODE,A.XMMC AS NHI_DESC,C.UNIT_CHN_DESC AS UNIT,B.OWN_PRICE AS PRICE "
//					+ " FROM JAVAHIS.INS_RULE A,JAVAHIS.SYS_FEE B,JAVAHIS.SYS_UNIT C "
//					+ " WHERE A.SFXMBM='" + NHI_CODE_Q + "' "
//					+ " AND SYSDATE BETWEEN A.KSSJ AND A.JSSJ "
//					+ " AND B.NHI_CODE_I=A.SFXMBM "
//					+ " AND C.UNIT_CODE=B.UNIT_CODE ";
//		} else if (ADM_TYPE_E.isSelected()) {
//			Sql = " SELECT A.SFXMBM AS NHI_CODE,A.XMMC AS NHI_DESC,C.UNIT_CHN_DESC AS UNIT,B.OWN_PRICE AS PRICE "
//					+ " FROM JAVAHIS.INS_RULE A,JAVAHIS.SYS_FEE B,JAVAHIS.SYS_UNIT C "
//					+ " WHERE A.SFXMBM='" + NHI_CODE_Q + "' "
//					+ " AND SYSDATE BETWEEN A.KSSJ AND A.JSSJ "
//					+ " AND B.NHI_CODE_E=A.SFXMBM "
//					+ " AND C.UNIT_CODE=B.UNIT_CODE ";
//		} else if (ADM_TYPE_O.isSelected()) {
//			Sql = " SELECT A.SFXMBM AS NHI_CODE,A.XMMC AS NHI_DESC,C.UNIT_CHN_DESC AS UNIT,B.OWN_PRICE AS PRICE "
//					+ " FROM JAVAHIS.INS_RULE A,JAVAHIS.SYS_FEE B,JAVAHIS.SYS_UNIT C "
//					+ " WHERE A.SFXMBM='" + NHI_CODE_Q + "' "
//					+ " AND SYSDATE BETWEEN A.KSSJ AND A.JSSJ "
//					+ " AND B.NHI_CODE_O=A.SFXMBM "
//					+ " AND C.UNIT_CODE=B.UNIT_CODE ";
//		}
		if (ADM_TYPE_I.isSelected()) {
			Sql = " SELECT A.SFXMBM AS NHI_CODE,A.XMMC AS NHI_DESC,C.UNIT_CHN_DESC AS UNIT,B.OWN_PRICE AS PRICE "
					+ " FROM JAVAHIS.INS_RULE A,JAVAHIS.SYS_FEE B,JAVAHIS.SYS_UNIT C "
					+ " WHERE B.ORDER_CODE='" + NHI_CODE_Q + "' "
					+ " AND B.NHI_CODE_I=A.SFXMBM "
					+ " AND SYSDATE BETWEEN A.KSSJ AND A.JSSJ "
					+ " AND C.UNIT_CODE=B.UNIT_CODE ";
		} else if (ADM_TYPE_E.isSelected()) {
			Sql = " SELECT A.SFXMBM AS NHI_CODE,A.XMMC AS NHI_DESC,C.UNIT_CHN_DESC AS UNIT,B.OWN_PRICE AS PRICE "
					+ " FROM JAVAHIS.INS_RULE A,JAVAHIS.SYS_FEE B,JAVAHIS.SYS_UNIT C "
					+ " WHERE B.ORDER_CODE='" + NHI_CODE_Q + "' "
					+ " AND B.NHI_CODE_E=A.SFXMBM "
					+ " AND SYSDATE BETWEEN A.KSSJ AND A.JSSJ "
					+ " AND C.UNIT_CODE=B.UNIT_CODE ";
		} else if (ADM_TYPE_O.isSelected()) {
			Sql = " SELECT A.SFXMBM AS NHI_CODE,A.XMMC AS NHI_DESC,C.UNIT_CHN_DESC AS UNIT,B.OWN_PRICE AS PRICE "
					+ " FROM JAVAHIS.INS_RULE A,JAVAHIS.SYS_FEE B,JAVAHIS.SYS_UNIT C "
					+ " WHERE B.ORDER_CODE='" + NHI_CODE_Q + "' "
					+ " AND B.NHI_CODE_O=A.SFXMBM "
					+ " AND SYSDATE BETWEEN A.KSSJ AND A.JSSJ "
					+ " AND C.UNIT_CODE=B.UNIT_CODE ";
		}
		// System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
			this.messageBox("û�в�ѯ����Ӧҽ��,��ȷ��ҽ�������Ƿ���ȷ");
			return;
		}
		onSetUI(tabParm);
	}
	/**
	 * ��ѯ������Ϣ
	 * 
	 * @return TParm
	 */
	private TParm onQuery(String nhi_code,String nhi_type) {
		String Sql = "SELECT A.NHI_CODE,A.NHI_DESC,A.ADM_TYPE AS NHI_TYPE,A.MINISTRY_HEALTHNO AS FILE_NO,A.CONNOTATION_PROJECT AS ITEM_DESC, "
				+ " A.UNIT,A.OWT_AMT AS PRICE,A.ICD_DESC_LIST AS ICD_DESC,A.ICD_CODE_LIST AS ICD_CODE,A.CLINICAL_SIGNIFICANCE AS CLINICAL_DESC, "
				+ " A.DEPT_CODE,A.APPARATUS AS DEVICE,A.REMARK,A.OUTSIDE_FLG AS OUTEXM_FLG,A.OUTSIDE_HOSP_CODE AS OUTEXM_HOSP_NO, "
				+ " A.SPECIAL_CASE AS SPECIAL_DESC,A.REAGENT AS DRUG,A.MEDICAL_MATERIALS AS MATERIAL,A.CATEGORY, "
				+ " A.MODIFY_PROJECT_REASON,A.ISVERIFY,A.AUDIT_OPINION,A.UPDATE_FLG,A.ITEM_CLASSIFICATION "
				+ " FROM JAVAHIS.INS_REGISTERITEM A "
				+ " WHERE A.NHI_CODE='"
				+ nhi_code + "' " + " AND A.DEL_FLG='N' AND A.ADM_TYPE='"+nhi_type+"' ";
		// System.out.println("regSql==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("NHI_CODE") < 0) {
//			this.messageBox("û�в�ѯ����Ӧ��¼");
			return null;
		}
		return tabParm;
	}
	/**
	 * ���ý�������
	 * 
	 * @return void
	 */
	private void onSetUI(TParm tabParm) {
		if(tabParm==null){
			return;
		}
		this.setValue("NHI_CODE", tabParm.getValue("NHI_CODE",0));
		this.setValue("NHI_DESC", tabParm.getValue("NHI_DESC",0));
		this.setValue("UNIT", tabParm.getValue("UNIT",0));
		this.setValue("OWT_AMT", tabParm.getValue("PRICE",0));
		if (this.updateFlg) {
//			this.setADMtype(this.onStrSplit(tabParm.getValue("NHI_TYPE", 0)));
			this.setValue("cADM_TYPE", tabParm.getValue("NHI_TYPE", 0));
			this.setValue("MINISTRY_HEALTHNO", tabParm.getValue("FILE_NO",0));
			this.setValue("CONNOTATION_PROJECT", tabParm.getValue("ITEM_DESC",0));
			ICD10.setParmValue(this.getCodeTParm(tabParm.getValue("ICD_CODE",0),
					                             tabParm.getValue("ICD_DESC",0)));
			ICD10.addRow();
			this.setValue("CLINICAL_SIGNIFICANCE", tabParm.getValue("CLINICAL_DESC",0));
			DEPT_CODE_LIST.setParmValue(this.getCodeTParm(tabParm.getValue("DEPT_CODE",0),
                    tabParm.getValue("DEPT_CODE",0)));
			APPARATUS_LIST.setParmValue(this.getCodeTParm(tabParm.getValue("DEVICE",0),
                    tabParm.getValue("DEVICE",0)));
			this.setValue("REMARK", tabParm.getValue("REMARK",0));
			this.setValue("OUTSIDE_FLG", tabParm.getValue("OUTEXM_FLG",0));
			OUTSIDE_HOSP_CODE_LIST.setParmValue(this.getCodeTParm(tabParm.getValue("OUTEXM_HOSP_NO",0),
                    tabParm.getValue("OUTEXM_HOSP_NO",0)));
			this.setValue("SPECIAL_CASE", tabParm.getValue("SPECIAL_DESC",0));
			REAGENT_LIST.setParmValue(this.getCodeTParm(tabParm.getValue("DRUG",0)));
			MEDICAL_MATERIALS_LIST.setParmValue(this.getCodeTParm(tabParm.getValue("MATERIAL",0)));
		}
	}

	private TParm onGetSaveDate() {
		TParm parm = new TParm();
		parm.setData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.setData("NHI_CODE", NHI_CODE.getValue()); // �շ���Ŀ����
		parm.setData("NHI_DESC", NHI_DESC.getValue()); // �շ���Ŀ����
//		parm.setData("NHI_TYPE", this.getADMType()); // Ӧ�÷�Χ
		parm.setData("NHI_TYPE", this.getValueString("cADM_TYPE")); // Ӧ�÷�Χ
		parm.setData("FILE_NO", MINISTRY_HEALTHNO.getValue()); // ������۲��Ű䲼���ļ����ݣ��ĺţ�
		parm.setData("ITEM_DESC", CONNOTATION_PROJECT.getValue()); // ��Ŀ�ں�
		parm.setData("UNIT", UNIT.getValue()); // �շѵ�λ
		parm.setData("PRICE", OWT_AMT.getValue()); // �շѱ�׼
		TParm tmp = this.getICD10Data();
//		System.out.println("this.getICD10Data():"+tmp);
		parm.setData("ICD_DESC", tmp.getValue("ICD_DESC_LIST",0)); // �ٴ���Ӧ֢
		parm.setData("ICD_CODE", tmp.getValue("ICD_CODE_LIST",0)); // �ٴ���Ӧ֢ICD����
		parm.setData("CLINICAL_DESC", CLINICAL_SIGNIFICANCE.getValue()); // �ٴ�����
		parm.setData("DEPT_CODE", this.getDeptCodeData()); // �ٴ�ʹ�ÿ���
		parm.setData("DEVICE", this.getApparatusData()); // ʹ�õ������豸
		parm.setData("REMARK", REMARK.getValue()); // ��ע
		parm.setData("OUTEXM_FLG", this.getValueString("OUTSIDE_FLG")); // ����־
		parm.setData("OUTEXM_HOSP_NO", this.getOutsideHospCodeData()); // ���ҽԺ����
		parm.setData("SPECIAL_DESC", SPECIAL_CASE.getValue()); // �������˵��
		parm.setData("DRUG", this.getReagentData()); // �Լ�
		parm.setData("MATERIAL", this.getMedicalMaterialsData()); // ҽ�ò���
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

//	private String getADMType() {
//		String temp = "";
//		if (cADM_TYPE_O.isSelected()) {
//			temp = temp + "1";
//		}
//		if (cADM_TYPE_E.isSelected()) {
//			if (temp.length() > 0) {
//				temp = temp + "@";
//			}
//			temp = temp + "2";
//		}
//		if (cADM_TYPE_I.isSelected()) {
//			if (temp.length() > 0) {
//				temp = temp + "@";
//			}
//			temp = temp + "3";
//		}
//		return temp;
//	}
	private boolean isExist(String nhi_code,String nhi_type){
		TParm parm=this.onQuery(nhi_code,nhi_type);
		if (parm==null) {
			return false;
		}else{
			return true;
		}	
	}
	public void onSaveAddUp() {
		TParm saveData = this.onGetSaveDate();
		if (isExist(saveData.getValue("NHI_CODE"),
				    saveData.getValue("NHI_TYPE"))) {
			if (this.update(saveData)) {
//				saveData.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
				onRegisterItemUp(saveData);
			}
		} else {
			if (this.insert(saveData)) {
//				saveData.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
				onRegisterItemUp(saveData);
			}
		}

	}

	public void onSave() {
		TParm saveData = this.onGetSaveDate();
		if (isExist(saveData.getValue("NHI_CODE"),
			        saveData.getValue("NHI_TYPE"))) {
			if(this.update(saveData)){
				this.messageBox("������Ŀ������Ϣ����ɹ�");
			}else{
				this.messageBox("������Ŀ������Ϣ����ʧ��");
			}
		} else {
			if(this.insert(saveData)){
				this.messageBox("������Ŀ������Ϣ����ɹ�");
			}else{
				this.messageBox("������Ŀ������Ϣ����ʧ��");
			}
		}

	}

	private boolean insert(TParm saveData) {
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onInsertInsRegisterItem",
				saveData);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		return true;
	}

	private boolean update(TParm saveData) {
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INS_RegiterAction", "onInsRegisterItemUpdate",
				saveData);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		return true;
	}

	/**
	 * ������Ŀ������Ϣ�ϴ�
	 */
	public void onRegisterItemUp(TParm parm) {
		TParm tmpParm=onGetUpload(parm);
		System.out.println("onRegisterItemUp:"+tmpParm);
		TParm splitParm = INSTJTool.getInstance().DataDown_zjks_S(tmpParm);
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("������Ŀ������Ϣ�ϴ�ʧ��\n"+splitParm.getErrText());
			return;
		} else {
			TParm newParm = new TParm(); // �ۼ�����
			newParm.addData("NHI_CODE", tmpParm.getValue("NHI_CODE"));
			newParm.addData("ADM_TYPE", tmpParm.getValue("NHI_TYPE"));
			newParm.setData("OPT_USER", Operator.getID());
			newParm.setData("OPT_TERM", Operator.getIP());
			newParm.setData("ISVERIFY", "1");
			newParm.setData("UPDATE_FLG", "2");
			TParm result = TIOM_AppServer.executeAction(
					"action.ins.INS_RegiterAction","onCancelInsRegisterItem", newParm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0005");
				return;
			}else{
				this.messageBox("������Ŀ������Ϣ�ϴ��ɹ�");
			}
		}
	}

	/**
	 * ����@�ָ��ַ���
	 */
	public TParm onStrSplit(String str) {
		TParm parm = new TParm();
		String[] strarray = str.split("@");
		for (int i = 0; i < strarray.length; i++)
			parm.addData("STR", strarray[i]);
		parm.setCount(strarray.length);
		return parm;
	}

	/**
	 * Ӧ�÷�Χ��ֵ
	 */
//	public TParm setADMtype(TParm parm) {
//		for (int i = 0; i < parm.getCount(); i++) {
//			if (parm.getValue("STR",i).equals("1")) {
//				cADM_TYPE_O.setSelected(true);
//			} else if (parm.getValue("STR",i).equals("2")) {
//				cADM_TYPE_E.setSelected(true);
//			} else if (parm.getValue("STR",i).equals("3")) {
//				cADM_TYPE_I.setSelected(true);
//			}
//		}
//		return parm;
//	}
	/**
	 * ��������Լ�,ҽ�ò��ϸ�ֵ����
	 */
	public TParm getCodeTParm(String code) {
//		System.out.println("getCodeTParm_code="+code);
		TParm parm = this.onStrSplit(code);
//		System.out.println("getCodeTParm_ret="+parm);
		TParm ret= new TParm();
		for (int i = 0; i < parm.getCount(); i++)
			ret.addData("CHOOSE", "N");
		for (int i = 0; i < parm.getCount(); i++)
		    ret.addData("CODE", parm.getData("STR",i));
		ret.setCount(parm.getCount());
//		System.out.println("getCodeTParm_ret="+ret);
		return ret;
	}
	/**
	 * �������ICD10��ֵ����
	 */
	public TParm getCodeTParm(String code,String desc) {
//		System.out.println("getCodeTParm_code="+code);
//		System.out.println("getCodeTParm_desc="+desc);
		TParm ret= new TParm();
		ret=this.getCodeTParm(code);
		TParm parm = this.onStrSplit(desc);
//		System.out.println("getCodeTParm_ret="+ret);
		if(ret.getCount()!=parm.getCount()){
			this.messageBox(code+"\n"+desc+"\n��������Ӧ");
			return null;
		}
		for (int i = 0; i < parm.getCount(); i++)
		   ret.addData("CODE1", parm.getData("STR",i));
//		System.out.println("getCodeTParm_ret="+ret);
		return ret;
	}
	/**
	 * ���
	 */
	public void onClear(){
		NHI_CODE_Q.setValue("");
		NHI_CODE.setValue("");
		NHI_DESC.setValue("");
//		cADM_TYPE_O.setSelected(false);
//		cADM_TYPE_E.setSelected(false);
//		cADM_TYPE_I.setSelected(false);
		cADM_TYPE.setSelectedIndex(0);
		ADM_TYPE_I.setSelected(true);
		UNIT.setValue("");
		OWT_AMT.setValue("");
		OUTSIDE_FLG.setSelectedIndex(0);
		MINISTRY_HEALTHNO.setValue("");
		CONNOTATION_PROJECT.setValue("");
		this.ICD10.removeRowAll();
		CLINICAL_SIGNIFICANCE.setValue("");
		MEDICAL_MATERIALS_LIST.removeRowAll();
		REAGENT_LIST.removeRowAll();
		OUTSIDE_HOSP_CODE_LIST.removeRowAll();
		APPARATUS_LIST.removeRowAll();
		DEPT_CODE_LIST.removeRowAll();
		REMARK.setValue("");
		SPECIAL_CASE.setValue("");
	}
	/**
	 * ��ֵ���
	 */
	public boolean onIsNull() {
		if(getValueString("CONNOTATION_PROJECT").equals("")){
			this.messageBox("��Ŀ�ں�����Ϊ��");
			return false;
		}
		if(getValueString("ITEM_CLASSIFICATION").equals("")){
			this.messageBox("��ѡ����Ŀ���");
			return false;
		}
		return true;
	}
	private TParm onGetUpload(TParm tabParm) {
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // ҽԺ����
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE")); // �շ���Ŀ����
		parm.addData("NHI_DESC", tabParm.getValue("NHI_DESC")); // �շ���Ŀ����
		parm.addData("NHI_TYPE", tabParm.getValue("NHI_TYPE")); // Ӧ�÷�Χ
		parm.addData("FILE_NO", tabParm.getValue("FILE_NO")); // ������۲��Ű䲼���ļ����ݣ��ĺţ�
		parm.addData("ITEM_DESC", tabParm.getValue("ITEM_DESC")); // ��Ŀ�ں�
		parm.addData("UNIT", tabParm.getValue("UNIT")); // �շѵ�λ
		parm.addData("PRICE", tabParm.getValue("PRICE")); // �շѱ�׼
		parm.addData("ICD_DESC", tabParm.getValue("ICD_DESC")); // �ٴ���Ӧ֢
		parm.addData("ICD_CODE", tabParm.getValue("ICD_CODE")); // �ٴ���Ӧ֢ICD����
		parm.addData("CLINICAL_DESC", tabParm.getValue("CLINICAL_DESC")); // �ٴ�����
		parm.addData("DEPT_CODE", tabParm.getValue("DEPT_CODE")); // �ٴ�ʹ�ÿ���
		parm.addData("DEVICE", tabParm.getValue("DEVICE")); // ʹ�õ������豸
		parm.addData("REMARK", tabParm.getValue("REMARK")); // ��ע
		parm.addData("OUTEXM_FLG", tabParm.getValue("OUTEXM_FLG")); // ����־
		parm.addData("OUTEXM_HOSP_NO", tabParm.getValue("OUTEXM_HOSP_NO")); // ���ҽԺ����
		parm.addData("SPECIAL_DESC", tabParm.getValue("SPECIAL_DESC")); // �������˵��
		parm.addData("DRUG", tabParm.getValue("DRUG")); // �Լ�
		parm.addData("MATERIAL", tabParm.getValue("MATERIAL")); // ҽ�ò���
		parm.addData("PARM_COUNT", 19);
		return parm;
	}
	///////////////////////////////////////////////zhangs 2014-05-19 add
	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		System.out.println("popReturn:"+parm);
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			NHI_CODE_Q.setValue(order_code);

		// ��ղ�ѯ�ؼ�
		onQueryNhiCode();
		NHI_CODE_Q.setValue("");
	}
///////////////////////////////////////////////////////zhangs 2014-05-19 end
}
