package com.javahis.ui.opd;

import java.awt.Component;

import jdo.odo.CommonDiag;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title: 门诊医生工作站常用诊断 </p>
 * 
 * <p> Description:门诊医生工作站常用诊断 </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * <p> Company:Javahis </p>
 * 
 * @author ehui 20090406
 * @version 1.0
 */

public class OPDComICDControl extends TControl {
	//传入值科别医师区分码
	private String deptOrDr;
	//静态科别区分码
	private final static String DEPT="1";
	//可别或医师的代码
	private String code;
	//诊断类别
	private String icdType;
	//常用诊断对象
	private CommonDiag diag;
	//主TABLE
	private TTable table;
	//取得最大显示序号SQL
	public final static String GET_MAX_SEQ=
		"SELECT MAX(SEQ) AS SEQ FROM OPD_COMDIAG";
	private boolean isEng;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		//取得传入参数
		getInitParameter();
		//初始化数据
		if("W".equalsIgnoreCase(icdType)){
			table.addEventListener("MED_TABLE->" + TTableEvent.CHANGE_VALUE,this, "onMedTableChangeValue");
		}else{
			table.addEventListener("MED_TABLE->" + TTableEvent.CHANGE_VALUE,this, "onChnTableChangeValue");
		}
		table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onDiagCreateEditComponent");
		onClear();
		isEng="en".equalsIgnoreCase(Operator.getLanguage());
	}
	/**
	 * 根据传入参数设置DEPTORDR的值的DEPTORDR_CODE的值
	 */
	public void getInitParameter(){
		String temp=TCM_Transform.getString(this.getParameter());
		deptOrDr=temp;
		icdType="W";
		TComboBox dr=(TComboBox)this.getComponent("OPERATOR");
		TComboBox dept=(TComboBox)this.getComponent("DEPT_CODE");
		TLabel label=(TLabel)this.getComponent("LABEL");
		if(DEPT.equals(deptOrDr)){
			code=Operator.getDept();
			label.setZhText("科室");
			label.setEnText("Dept.");
			dept.setVisible(true);
			dept.setEnabled(false);
			dept.setSelectedID(Operator.getDept());
			dr.setEnabled(false);
			dr.setVisible(false);
		}else{
			code=Operator.getID();
			label.setZhText("医师");
			label.setEnText("Dr.");
			dept.setEnabled(false);
			dept.setVisible(false);
			dr.setSelectedID(Operator.getID());
			dr.setEnabled(false);
			dr.setVisible(true);
		}
		if("W".equalsIgnoreCase(icdType))
			table=(TTable)this.getComponent("MED_TABLE");
	}
	/**
	 * 初始化TABLE数据
	 */
	public void onClear(){
		if(table==null)
		{
			table=(TTable)this.getComponent("MED_TABLE");
		}
		diag=new CommonDiag(deptOrDr,code);
		table.setDataStore(diag);
		int row=diag.insertRow();
		table.setDSValue();
	}
	/**
	 * 添加诊断弹出窗口
	 * 
	 * @param com Component
	 * @param row int
	 * @param column int
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {
		if (column != 0)
			return;
		if (!(com instanceof TTextField))
			return;
		TParm parm=new TParm();
		parm.setData("ICD_TYPE",icdType);
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"),parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popDiagReturn");
	}
	/**
	 * 新增诊断
	 * @param tag String
	 * @param obj Object
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag=(TTable)this.getComponent("MED_TABLE");
		int row=tableDiag.getSelectedRow();
		table.acceptText();
		if(!"W".equalsIgnoreCase(icdType))
			table.setValueAt(parm.getValue("SYNDROME_FLG"), row, 4);
		diag.setItem(row, "ICD_CODE", parm.getValue("ICD_CODE"));
		diag.setItem(row, "ICD_TYPE", icdType);
		diag.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		diag.setItem(row, "OPT_TERM", Operator.getIP());
		diag.setItem(row, "OPT_USER", Operator.getID());
		diag.setActive(row,true);
		int newRow=diag.insertRow();
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(newRow);
		table.setSelectedColumn(0);
	}
	/**
	 * 更改科室
	 */
	public void onChangeDept(){
		code=this.getValueString("DEPT_CODE");
		onClear();
	}
	/**
	 * 保存
	 */
	public void onSave(){
		String[] sql=diag.getUpdateSQL();
		if(sql== null||sql.length<1){
			this.messageBox_("保存失败");
			return;
		}
			
		
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()!=0){
			this.messageBox("E0001");
//			this.messageBox_(result.getErrText());
		}else{
			this.messageBox("P0001");
		}
		onClear();
	}
	/**
	 * 删除事件
	 */
	public void onDelete(){
		if(table==null){
			return;
		}
		int row=table.getSelectedRow();
		if(row<0){
			return;
		}
		if(StringUtil.isNullString(diag.getItemString(row, "ICD_CODE"))){
			return;
		}
		diag.deleteRow(row);
		if(diag.isModified()){
			onSave();	
		}
		table.setDSValue();
		
	}
}
