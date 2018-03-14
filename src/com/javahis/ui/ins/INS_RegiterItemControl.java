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
	private TParm regionParm; // 医保区域代码

	public void onInit() {
		super.onInit();
		onComponentInit();// 界面组件初始化
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
		OUTSIDE_FLG.setSelectedIndex(0);
		// 得到前台传来的数据并显示在界面上
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
		// 设置弹出菜单
		NHI_CODE_Q.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
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
//		ICD10.setHeader("删除,40,boolean;代码,80;名称,120");
//		ICD10.setParmMap("CHOOSE;CODE;DESC");
//		ICD10.setItem("CODE;DESC");
//		ICD10.setColumnHorizontalAlignmentData("0,left;1,left;2,left");
		
		ICD10.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onCreateEditComponent");
		ICD10.addRow();
		OrderList orderList = new OrderList();
		ICD10.addItem("OrderList", orderList);
//      //诊断Grid值改变事件
        this.addEventListener("Daily_Table->" + TTableEvent.CHANGE_VALUE,
                              "onDiagTableValueCharge");
		OUTSIDE_HOSP_CODE_LIST
				.setHeader("删除,30,boolean;代码,60;名称,100,OUTSIDE_HOSP_CODE");
		OUTSIDE_HOSP_CODE_LIST.setItem("OUTSIDE_HOSP_CODE");
		OUTSIDE_HOSP_CODE_LIST.setParmMap("CHOOSE;CODE;CODE1");
		OUTSIDE_HOSP_CODE_LIST
				.setColumnHorizontalAlignmentData("1,left;2,left;3,left");
		// ////////////////////////////////////////////////////////////////////////
		APPARATUS_LIST.setHeader("删除,30,boolean;代码,60;名称,100,APPARATUS");
		APPARATUS_LIST.setItem("APPARATUS");
		APPARATUS_LIST.setParmMap("CHOOSE;CODE;CODE1");
		APPARATUS_LIST
				.setColumnHorizontalAlignmentData("1,left;2,left");
		// ///////////////////////////////////////////////////////////////////////
		DEPT_CODE_LIST.setHeader("删除,30,boolean;代码,60;名称,100,DEPT_CODE");
		DEPT_CODE_LIST.setItem("DEPT_CODE");
		DEPT_CODE_LIST.setParmMap("CHOOSE;CODE;CODE1");
		DEPT_CODE_LIST
				.setColumnHorizontalAlignmentData("1,left;2,left");
	}

	/**
	 * 需要执行的数据 =================pangben 2013-3-10
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
	 * 诊断CODE替换中文 模糊查询（内部类）
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
	 *诊断弹出界面 ICD10
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// 弹出ICD10对话框的列
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加ICD10弹出窗口
		textfield.setPopupMenuParameter("ICD10", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 给新text增加接受ICD10弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newAgentOrder");
	}

	/**
	 * 取得ICD10返回值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newAgentOrder(String tag, Object obj) {
		TTable table = (TTable) this.callFunction("UI|ICD10|getThis");
		// sysfee返回的数据包
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
	 * 诊断Grid 值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDiagTableValueCharge(Object obj) {
		TTable DiagGrid = (TTable) this.getComponent("ICD10");
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node.getColumn() == 1) {
			if (node.getRow() == (DiagGrid.getRowCount() - 1))
				DiagGrid.addRow();
		}
	}

	/**
	 * 医用材料增加按钮事件
	 */
	public void onAddMedicalMaterialsTable() {
		String userid = this.getValueString("MEDICAL_MATERIALS");// 获得材料信息user_id
		if (userid.length() > 0) {
			TTable table = (TTable) this.getComponent("MEDICAL_MATERIALS_LIST");
			int rowIndex = table.addRow();
			table.setValueAt("N", rowIndex, 0);
			table.setValueAt(userid, rowIndex, 1);
		}
	}

	/**
	 * 获取医用材料数据
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
			parm = "无";
		}
		return parm;
	}

	/**
	 * 试剂增加按钮事件
	 */
	public void onAddReagentTable() {
		String userid = this.getValueString("REAGENT");// 获得材料信息user_id
		if (userid.length() > 0) {
			TTable table = (TTable) this.getComponent("REAGENT_LIST");
			int rowIndex = table.addRow();
			table.setValueAt("N", rowIndex, 0);
			table.setValueAt(userid, rowIndex, 1);
		}
	}

	/**
	 * 获取试剂数据
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
			parm = "无";
		}
		return parm;
	}

	/**
	 * 外检医院增加按钮事件
	 */
	public void onAddOutsideHospCodeTable() {
		String userid = this.getValueString("OUTSIDE_HOSP_CODE");// 获得材料信息user_id
		if (userid.length() > 0) {
			addTTable(this.OUTSIDE_HOSP_CODE_LIST,this.OUTSIDE_HOSP_CODE);
		}
	}

	/**
	 * 获取外检医院数据
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
	 * 仪器设备增加按钮事件
	 */
	public void onAddApparatusTable() {
		String userid = this.getValueString("APPARATUS");// 获得材料信息user_id
		if (userid.length() > 0) {
			addTTable(this.APPARATUS_LIST,this.APPARATUS);
		}
	}

	/**
	 * 获取仪器设备数据
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
			parm = "无";
		}
		return parm;
	}

	/**
	 * 科室增加按钮事件
	 */
	public void onAddDeptCodeTable() {
		String userid = this.getValueString("DEPT_CODE");// 获得材料信息user_id
		if (userid.length() > 0) {
			addTTable(this.DEPT_CODE_LIST,this.DEPT_CODE);
		}
	}
	/**
	 * 写科室,院外,设备Ttable
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
	 * 获取科室数据
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
	 * 获取诊断数据
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
	 * 用医保码查询医嘱信息
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
			this.messageBox("没有查询到对应医嘱,请确认医保编码是否正确");
			return;
		}
		onSetUI(tabParm);
	}
	/**
	 * 查询备案信息
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
//			this.messageBox("没有查询到相应记录");
			return null;
		}
		return tabParm;
	}
	/**
	 * 设置界面数据
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
		parm.setData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.setData("NHI_CODE", NHI_CODE.getValue()); // 收费项目编码
		parm.setData("NHI_DESC", NHI_DESC.getValue()); // 收费项目名称
//		parm.setData("NHI_TYPE", this.getADMType()); // 应用范围
		parm.setData("NHI_TYPE", this.getValueString("cADM_TYPE")); // 应用范围
		parm.setData("FILE_NO", MINISTRY_HEALTHNO.getValue()); // 卫生物价部门颁布的文件依据（文号）
		parm.setData("ITEM_DESC", CONNOTATION_PROJECT.getValue()); // 项目内涵
		parm.setData("UNIT", UNIT.getValue()); // 收费单位
		parm.setData("PRICE", OWT_AMT.getValue()); // 收费标准
		TParm tmp = this.getICD10Data();
//		System.out.println("this.getICD10Data():"+tmp);
		parm.setData("ICD_DESC", tmp.getValue("ICD_DESC_LIST",0)); // 临床适应症
		parm.setData("ICD_CODE", tmp.getValue("ICD_CODE_LIST",0)); // 临床适应症ICD编码
		parm.setData("CLINICAL_DESC", CLINICAL_SIGNIFICANCE.getValue()); // 临床意义
		parm.setData("DEPT_CODE", this.getDeptCodeData()); // 临床使用科室
		parm.setData("DEVICE", this.getApparatusData()); // 使用的仪器设备
		parm.setData("REMARK", REMARK.getValue()); // 备注
		parm.setData("OUTEXM_FLG", this.getValueString("OUTSIDE_FLG")); // 外检标志
		parm.setData("OUTEXM_HOSP_NO", this.getOutsideHospCodeData()); // 外检医院编码
		parm.setData("SPECIAL_DESC", SPECIAL_CASE.getValue()); // 特殊情况说明
		parm.setData("DRUG", this.getReagentData()); // 试剂
		parm.setData("MATERIAL", this.getMedicalMaterialsData()); // 医用材料
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
//				saveData.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
				onRegisterItemUp(saveData);
			}
		} else {
			if (this.insert(saveData)) {
//				saveData.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
				onRegisterItemUp(saveData);
			}
		}

	}

	public void onSave() {
		TParm saveData = this.onGetSaveDate();
		if (isExist(saveData.getValue("NHI_CODE"),
			        saveData.getValue("NHI_TYPE"))) {
			if(this.update(saveData)){
				this.messageBox("诊疗项目备案信息保存成功");
			}else{
				this.messageBox("诊疗项目备案信息保存失败");
			}
		} else {
			if(this.insert(saveData)){
				this.messageBox("诊疗项目备案信息保存成功");
			}else{
				this.messageBox("诊疗项目备案信息保存失败");
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
	 * 诊疗项目备案信息上传
	 */
	public void onRegisterItemUp(TParm parm) {
		TParm tmpParm=onGetUpload(parm);
		System.out.println("onRegisterItemUp:"+tmpParm);
		TParm splitParm = INSTJTool.getInstance().DataDown_zjks_S(tmpParm);
		if (!INSTJTool.getInstance().getErrParm(splitParm)) {
			this.messageBox("诊疗项目备案信息上传失败\n"+splitParm.getErrText());
			return;
		} else {
			TParm newParm = new TParm(); // 累计数据
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
				this.messageBox("诊疗项目备案信息上传成功");
			}
		}
	}

	/**
	 * 根据@分割字符串
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
	 * 应用范围赋值
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
	 * 获得用于试剂,医用材料赋值数据
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
	 * 获得用于ICD10赋值数据
	 */
	public TParm getCodeTParm(String code,String desc) {
//		System.out.println("getCodeTParm_code="+code);
//		System.out.println("getCodeTParm_desc="+desc);
		TParm ret= new TParm();
		ret=this.getCodeTParm(code);
		TParm parm = this.onStrSplit(desc);
//		System.out.println("getCodeTParm_ret="+ret);
		if(ret.getCount()!=parm.getCount()){
			this.messageBox(code+"\n"+desc+"\n数量不对应");
			return null;
		}
		for (int i = 0; i < parm.getCount(); i++)
		   ret.addData("CODE1", parm.getData("STR",i));
//		System.out.println("getCodeTParm_ret="+ret);
		return ret;
	}
	/**
	 * 清空
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
	 * 空值检查
	 */
	public boolean onIsNull() {
		if(getValueString("CONNOTATION_PROJECT").equals("")){
			this.messageBox("项目内涵不能为空");
			return false;
		}
		if(getValueString("ITEM_CLASSIFICATION").equals("")){
			this.messageBox("请选择项目类别");
			return false;
		}
		return true;
	}
	private TParm onGetUpload(TParm tabParm) {
		TParm parm = new TParm();
		parm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0)); // 医院编码
		parm.addData("NHI_CODE", tabParm.getValue("NHI_CODE")); // 收费项目编码
		parm.addData("NHI_DESC", tabParm.getValue("NHI_DESC")); // 收费项目名称
		parm.addData("NHI_TYPE", tabParm.getValue("NHI_TYPE")); // 应用范围
		parm.addData("FILE_NO", tabParm.getValue("FILE_NO")); // 卫生物价部门颁布的文件依据（文号）
		parm.addData("ITEM_DESC", tabParm.getValue("ITEM_DESC")); // 项目内涵
		parm.addData("UNIT", tabParm.getValue("UNIT")); // 收费单位
		parm.addData("PRICE", tabParm.getValue("PRICE")); // 收费标准
		parm.addData("ICD_DESC", tabParm.getValue("ICD_DESC")); // 临床适应症
		parm.addData("ICD_CODE", tabParm.getValue("ICD_CODE")); // 临床适应症ICD编码
		parm.addData("CLINICAL_DESC", tabParm.getValue("CLINICAL_DESC")); // 临床意义
		parm.addData("DEPT_CODE", tabParm.getValue("DEPT_CODE")); // 临床使用科室
		parm.addData("DEVICE", tabParm.getValue("DEVICE")); // 使用的仪器设备
		parm.addData("REMARK", tabParm.getValue("REMARK")); // 备注
		parm.addData("OUTEXM_FLG", tabParm.getValue("OUTEXM_FLG")); // 外检标志
		parm.addData("OUTEXM_HOSP_NO", tabParm.getValue("OUTEXM_HOSP_NO")); // 外检医院编码
		parm.addData("SPECIAL_DESC", tabParm.getValue("SPECIAL_DESC")); // 特殊情况说明
		parm.addData("DRUG", tabParm.getValue("DRUG")); // 试剂
		parm.addData("MATERIAL", tabParm.getValue("MATERIAL")); // 医用材料
		parm.addData("PARM_COUNT", 19);
		return parm;
	}
	///////////////////////////////////////////////zhangs 2014-05-19 add
	/**
	 * 接受返回值方法
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

		// 清空查询控件
		onQueryNhiCode();
		NHI_CODE_Q.setValue("");
	}
///////////////////////////////////////////////////////zhangs 2014-05-19 end
}
