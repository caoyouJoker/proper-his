package com.javahis.ui.opd;

import jdo.opd.DrugAllergy;
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
 * Title: 过敏史Panel
 * </p>
 * 
 * <p>
 * Description:过敏史Panel
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
public class OPDMainAllergyControl extends TControl {
	ODO odo;
	int selectRow=-1;
	public void onInit() {
		super.onInit();
		callFunction("UI|TABLEALLERGY|addEventListener","TABLEALLERGY->"+TTableEvent.CLICKED,this,"onTableClicked");
		this.addEventListener("TABLEALLERGY->" + TTableEvent.CHANGE_VALUE,
		"onTableChangeValue");
		initAllergy();
	}
	/**
	 * 初始化界面
	 */
	public void initAllergy(){
		if(odo== null||odo.getDrugAllergyList()==null||odo.getDrugAllergyList().size()<1){
			return;
			}
		TParm parm=odo.getDrugAllergyList().getParm(DrugAllergyList.PRIMARY);
		if(parm.getCount()>0){
			//过敏日期,80;类别,40;过敏源,130;过敏情况,130;就诊科别,80;本院,60;就诊医生,80;门急住别,80;就诊号,80
			callFunction("UI|TABLEALLERGY|setParmValue",odo.getDrugAllergyList().getParm(DrugAllergyList.PRIMARY));
			onNew();
//					,"ADM_DATE;DRUG_TYPE;DRUGORINGRD_CODE;ALLERGY_NOTE;DEPT_CODE;OURHOSP_FLG;DR_CODE;ADM_TYPE;CASE_NO");	
			return;
		}
		callFunction("UI|TABLEALLERGY|setParmValue",new TParm());
		callFunction("UI|TABLEALLERGY|removeRowAll");
		onNew();
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
		
		DrugAllergy da=odo.getDrugAllergyList().getDrugAllergy(c);
		switch(column){
		//ICD_TYPE
		case 1:
			da.modifyDrugType(nodevalue);
			break;
		//ICD_CODE
		case 2:
			da.modifyDrugoringrdCode(nodevalue);
			onNew();
			break;
		case 3:
			da.modifyAllergyNote(nodevalue);
			break;
		case 4:
			da.modifyDeptCode(nodevalue);
			break;
//		case 5:
//			da.modifyOurHospFlg(nodevalue);
//			break;
		case 5:
			da.modifyDrCode(nodevalue);	
			break;
		case 6:
			da.modifyAdmType(nodevalue);
			break;
		}
		da.modifyOptUser(Operator.getID());
		da.modifyOptTerm(Operator.getIP());
	}
	/**
	 * TABLE新增一行数据
	 */
	public void onNew(){
		if(odo==null)
			return;
		String admDate=StringTool.getString(StringTool.getTimestamp(SystemTool.getInstance().getDate()), "yyyyMMddHHmmss");
		DrugAllergy da=odo.getDrugAllergyList().newDrugAllergy();
		da.modifyAdmDate(admDate);
		da.modifyMrNo(odo.getPat().getMrNo());
		da.modifyCaseNo(odo.getCaseNo());
		da.modifyAdmType(odo.getAdmType());
		da.modifyDrCode(Operator.getID());
		da.modifyOptUser(Operator.getID());
		da.modifyOptTerm(Operator.getIP());
		TParm parm=da.getParm();
		parm.setData("DR_CODE",Operator.getID());
		parm.setData("CASE_NO",odo.getCaseNo());
		parm.setData("ADM_DATE",admDate.substring(0,8));
		int rownow=TCM_Transform.getInt(callFunction("UI|TABLEALLERGY|getRowCount"));
		//addRow(int row, TParm parm) 新增一行数据
		this.callFunction("UI|TABLEALLERGY|addRow",rownow,parm);
	}
	/**
	 * TABLE删除一行数据
	 */
	public void onDelete(){
		if(selectRow==-1||odo==null)
			return;
		this.callFunction("UI|TABLEALLERGY|removeRow", selectRow);
		odo.getDrugAllergyList().removeData(selectRow);
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
		if(this.odo.getDrugAllergyList()==null||this.odo.getDrugAllergyList().size()<1)
			return;
		initAllergy();
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
	/**
	 * 清空界面
	 */
	public void onClear(){
		callFunction("UI|TABLEALLERGY|setParmValue",new TParm());
		callFunction("UI|TABLEALLERGY|removeRowAll");
	}
	public Object callMessage(String message,Object parm){
		return 	super.callMessage(message, parm);
	}
}
