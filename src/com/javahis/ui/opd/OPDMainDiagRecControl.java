package com.javahis.ui.opd;

import jdo.opd.DiagRec;
import jdo.opd.DrugAllergyList;
import jdo.opd.ODO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
/**
 * 
 * <p>
 * Title: 诊断Panel
 * </p>
 * 
 * <p>
 * Description:诊断Panel
 * </p>
 * 
 * <p>
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 20080901
 * @version 1.0
 */
public class OPDMainDiagRecControl extends TControl {
	ODO odo;
	int selectRow=-1;
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEDIAGNOSIS|addEventListener","TABLEDIAGNOSIS->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLEDIAGNOSIS->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		initDiagRec();
	}
	/**
	 * 初始化界面
	 */
	public void initDiagRec(){
		if (odo == null) {
			return;
		}
		this.setValue("ICD_TYPE", "W");
		TParm temp=odo.getDiagRecList().getParm(DrugAllergyList.PRIMARY);
		if(temp.getCount()>0){
		//主,40;疾病诊断,80;名称,150;备注,150;开立医生,100;开立时间,100;报告卡序号,80
		callFunction("UI|TABLEDIAGNOSIS|setParmValue",temp
				,"MAINDIAG_FLG;ICD_CODE;ICD_CODE;DIAG_NOTE;DR_CODE;ORDER_DATE;FILE_NO");
		return;
		}
		onNew();
	}
	/**
	 * TABLE点击事件
	 * @param row
	 */
	public void onTableClicked(int row){
		selectRow=row;
	}

	/**
	 * TABLE值改变事件
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode){
		
		TTableNode	mytableNode=(TTableNode)tableNode;
		this.callFunction("UI|TABLEDIAGNOSIS|acceptText");
		//该CELL的值
		String nodevalue=TCM_Transform.getString(mytableNode.getValue());
		//TABLE上第几行
		int c=mytableNode.getRow();
		//第几列
		int column=mytableNode.getColumn();
		
		DiagRec dr=odo.getDiagRecList().getDiagRec(c);
		switch(column){
		//MAINDIAGFLG
		case 0:
			dr.modifyMainDiagFlg(nodevalue);
		//ICD_TYPE,ICD_CODE
		case 1:
			dr.modifyIcdCode(nodevalue);
			dr.modifyIcdType(TCM_Transform.getString(this.getValue("ICD_TYPE")));
			break;
		//ICD_CODE
		case 2:
			break;
		case 3:
			dr.modifyDiagNote(nodevalue);
			break;
		case 4:
			dr.modifyDrCode(nodevalue);
			break;
		case 6:
			dr.modifyDrCode(nodevalue);	
			break;
		}
		dr.modifyOptUser(Operator.getID());
		dr.modifyOptTerm(Operator.getIP());
	}
	/**
	 * TABLE新增一行数据
	 */
	public void onNew(){
		if(odo==null)
			return;
		String admDate=StringTool.getString(StringTool.getTimestamp(SystemTool.getInstance().getDate()), "yyMMddHHmmss");
		DiagRec dr=odo.getDiagRecList().newDiagRec();
		TParm parm=dr.getParm();
		dr.modifyDrCode(Operator.getID());
		dr.modifyOptUser(Operator.getID());
		dr.modifyOptTerm(Operator.getIP());
		dr.modifyOrderDate(StringTool.getTimestamp(SystemTool.getInstance().getDate()));
		parm.setData("ORDER_DATE",admDate);
		parm.setData("DR_CODE",Operator.getID());
		int rownow=TCM_Transform.getInt(callFunction("UI|TABLEDIAGNOSIS|getRowCount"));
		//addRow(int row, TParm parm) 新增一行数据
		this.callFunction("UI|TABLEDIAGNOSIS|addRow",rownow,parm);
	}
	/**
	 * TABLE删除一行数据
	 */
	public void onDelete(){
		if(selectRow==-1||odo==null)
			return;
		this.callFunction("UI|TABLEDIAGNOSIS|removeRow", selectRow);
		odo.getDiagRecList().removeData(selectRow);
	}
	/**
	 * 设置ODO对象
	 * @param odo
	 */
	public void setOdo(Object odo)
	{
		
		if(odo==null)
			return;
		this.odo=(ODO)odo;
		initDiagRec();
	}
	/**
	 * 取得odo对象
	 * @return ODO
	 */
	public ODO getOdo(){
		if(odo==null)
			return null;
		return odo;
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}
	
}
