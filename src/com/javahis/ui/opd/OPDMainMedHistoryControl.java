package com.javahis.ui.opd;

import jdo.opd.Medhistory;
import jdo.opd.MedhistoryList;
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
 * Title: 既往史Panel
 * </p>
 * 
 * <p>
 * Description:既往史Panel
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
 * @author ehui 20080924
 * @version 1.0
 */
public class OPDMainMedHistoryControl extends TControl {
	ODO odo;
	int selectRow=-1;
	String admType="O";
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEMEDHISTORY|addEventListener","TABLEMEDHISTORY->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLEMEDHISTORY->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		initMedHistory();
	}
	/**
	 * 初始化界面
	 */
	public void initMedHistory(){
		if(odo== null){
			return;
		}
		
		TParm medicineorder=odo.getMedhistoryList().getParm(MedhistoryList.PRIMARY);
		if(medicineorder.getCount()>0){
			//就诊日期,80;诊断类别,80;疾病诊断,80;备注,80;就诊科别,80;本院,60;就诊医生,100;门急住别,80;就诊号,100
			callFunction("UI|TABLEMEDHISTORY|setParmValue",medicineorder);
			onNew();
			return;
		}
		callFunction("UI|TABLEMEDHISTORY|setParmValue",new TParm());
		this.callFunction("UI|TABLEMEDHISTORY|removeRowAll");
		onNew();
		return;
	}
	/**
	 * TABLE点击事件
	 * @param row
	 */
	public void onTableClicked(int row){
		 String message = (String)callFunction("UI|delete|getActionMessage");
		 String s[] = StringTool.getHead(message, "|");
		 if(s!=null&&!callFunction("UI|getTag").equals(s[0])){
			 callFunction("UI|" + s[0] + "|clearTableSelection");
		 }
		 callFunction("UI|delete|setActionMessage", callFunction("UI|getTag")
		 + "|onDelete");
		selectRow=row;
	}

	/**
	 * TABLE值改变事件
	 * @param tableNode
	 */
	public void onTableChangeValue(Object tableNode){
		TTableNode	mytableNode=(TTableNode)tableNode;
		//该CELL的值
		String nodevalue=TCM_Transform.getString(mytableNode.getValue());
		//TABLE上第几行
		int c=mytableNode.getRow();
		//第几列
		int column=mytableNode.getColumn();
		
		Medhistory mh=odo.getMedhistoryList().getMedhistory(c);
		switch(column){
		//ICD_TYPE
		case 1:
			mh.modifyIcdType(nodevalue);
			break;
		//ICD_CODE
		case 2:
			mh.modifyIcdCode(nodevalue);
			onNew();
			break;
		case 3:
			mh.modifyDescription(nodevalue);
			break;
		case 4:
			mh.modifyDeptCode(nodevalue);
			break;
		case 5:
			mh.modifyDrCode(nodevalue);
			break;
		case 6:
			mh.modifyAdmType(nodevalue);
			break;
		}
		mh.modifyOptUser(Operator.getID());
		mh.modifyOptTerm(Operator.getIP());
	}
	/**
	 * TABLE新增一行数据
	 */
	public void onNew(){
		if(odo==null){
			return;
		}
		String admDate=StringTool.getString(StringTool.getTimestamp(SystemTool.getInstance().getDate()), "yyyyMMddHHmmss");
		Medhistory mh=odo.getMedhistoryList().newMedhistory();
		TParm parm=mh.getParm();
		parm.setData("ADM_DATE",admDate.substring(0,8));
		parm.setData("CASE_NO",odo.getCaseNo());
		parm.setData("DR_CODE",Operator.getID());
		parm.setData("ICD_TYPE","W");
		parm.setData("DEPT_CODE",Operator.getDept());
		parm.setData("ADM_TYPE",admType);
		mh.modifyAdmDate(admDate);
		mh.modifyDrCode(Operator.getID());
		mh.modifyCaseNo(odo.getCaseNo());
		mh.modifyOptUser(Operator.getID());
		mh.modifyOptTerm(Operator.getIP());
		mh.modifyIcdType("W");
		mh.modifyDeptCode(Operator.getDept());
		mh.modifyAdmType(admType);
		mh.setPat(odo.getPat());
		int rownow=TCM_Transform.getInt(callFunction("UI|TABLEMEDHISTORY|getRowCount"));
		//addRow(int row, TParm parm) 新增一行数据
		this.callFunction("UI|TABLEMEDHISTORY|addRow",rownow,parm);
	}
	/**
	 * TABLE删除一行数据
	 */
	public void onDelete(){
		if(selectRow==-1||odo==null)
			return;
		
		this.callFunction("UI|TABLEMEDHISTORY|removeRow", selectRow);
		odo.getMedhistoryList().removeData(selectRow);
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
		initMedHistory();
	}
	/**
	 * 清空界面
	 */
	public void onClear(){
		callFunction("UI|TABLEMEDHISTORY|setParmValue",new TParm());
		callFunction("UI|TABLEMEDHISTORY|removeRowAll");
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}
}
