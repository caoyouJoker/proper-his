package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVPackageReturnedCheckTool;
import jdo.inv.INVReturnedCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;


/**
 * 
 * <p>
 * Title:诊疗包退货确认
 * </p>
 * 
 * <p>
 * Description: 诊疗包退货确认
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-11-22
 * @version 1.0
 */
public class INVPackageReturnedConfirmControl extends TControl{

	private TTable tableRM;			//退货单主表
	
	private TTable tableRD;			//退货单细表
	
//	private boolean isNew = false;	//新建标记
	
	private String returnedNo ;		//退货单号
	
//	private TParm tParm = new TParm();
	
	/**
	 * 初始化
	 */
	public void onInit() {
	
		tableRM = (TTable) getComponent("TABLEM");
		tableRD = (TTable) getComponent("TABLED");
		
		this.setTimes();
		
	}
	 
	private void setTimes(){
		//初始化    退货日期查询区间
        Timestamp date = SystemTool.getInstance().getDate();
        String startDate = date.toString().substring(0, 10).replace('-', '/') + " 00:00:00";
        String endDate = date.toString().substring(0, 19).replace('-', '/');
        this.setValue("END_DATE",endDate);
        this.setValue("START_DATE",startDate);
	}

	
	
	public void onSave(){
		
		int row = tableRM.getSelectedRow();
		if(row<0){
			messageBox("请选择退货单！");
			return;
		}
		TParm tp = tableRM.getParmValue().getRow(row);
		if(tp.getData("CONFIRM_FLG").equals("Y")){
			messageBox("已确认的退货单不能再次确认！");
			return;
		}
			
		TParm tpD = tableRD.getParmValue();
		Timestamp date = SystemTool.getInstance().getDate();
		for(int i=0;i<tpD.getCount("PACK_CODE");i++){
			tpD.setData("CONFIRM_USER", i, Operator.getID());
			tpD.setData("CONFIRM_DATE", i, date.toString().substring(0,19));
			tpD.setData("FROM_ORG_CODE", i, tp.getData("FROM_ORG_CODE"));
			tpD.setData("TO_ORG_CODE", i, tp.getData("TO_ORG_CODE"));
		}
		
		TParm result = TIOM_AppServer.executeAction("action.inv.INVPackageReturnedCheckAction",
		     "onConfirm", tpD);
		if (result.getErrCode() < 0) { 
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			messageBox("保存失败！");
			return;
		}
		messageBox("保存成功！");
		returnedNo = tp.getData("PACKAGERETURNED_NO").toString();
		this.queryByNo();
	}


	
	public void onQuery(){
		
		if( "".equals(this.getValueString("START_DATE")) || "".equals(this.getValueString("END_DATE")) ){
			this.messageBox("请选择日期区间！");
            return;
		}
		
		TParm tp = new TParm();
		if(!"".equals(this.getValueString("START_DATE"))){
			tp.setData("START_DATE", this.getValueString("START_DATE").toString().substring(0, 19));
		}
		if(!"".equals(this.getValueString("END_DATE"))){
			tp.setData("END_DATE", this.getValueString("END_DATE").toString().substring(0, 19));
		}
		if(!"".equals(this.getValueString("FROM_ORG_CODE_SEC"))){
			tp.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE_SEC"));
		}
		if(!"".equals(this.getValueString("TO_ORG_CODE"))){
			tp.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		}
		if(!"".equals(this.getValueString("RETURNEDNO"))){
			tp.setData("PACKAGERETURNED_NO", this.getValueString("RETURNEDNO"));
		}
		
		if(((TRadioButton)this.getComponent("UNCONFIRM")).isSelected()){
			tp.setData("CONFIRM_FLG", "N");
		}else{
			tp.setData("CONFIRM_FLG", "Y");
		}
		
		tableRM.removeRowAll();
		tableRD.removeRowAll();
		TParm mTP = INVPackageReturnedCheckTool.getInstance().queryPackageReturnedCheckM(tp);
		tableRM.setParmValue(mTP);
	
	}
	
	public void onTableMClick(){
		int row = tableRM.getSelectedRow();
		TParm selParm = tableRM.getParmValue().getRow(row);
		
		TParm tp = new TParm();
		tp.setData("PACKAGERETURNED_NO", selParm.getData("PACKAGERETURNED_NO"));
		TParm dTP = INVPackageReturnedCheckTool.getInstance().queryPackageReturnedCheckD(tp);
		tableRD.removeRowAll();
		tableRD.setParmValue(dTP);
	}
	
	
	 public void onClear(){
		 
		 this.setTimes();
		 tableRM.removeRowAll();
		 tableRD.removeRowAll();
		 setValue("FROM_ORG_CODE_SEC", "");
		 setValue("TO_ORG_CODE", "");
		 setValue("RETURNEDNO", "");

		 returnedNo = "";
		 
		 ( (TRadioButton)this.getComponent("UNCONFIRM")).setSelected(true);
		 
	 }
	 

	
	
	
	/** 
	 * 根据退货单编号查询退货单信息 
	 *  */
	private void queryByNo(){
		TParm tp = new TParm();
		tp.setData("PACKAGERETURNED_NO", returnedNo);
		
		TParm mTP = INVPackageReturnedCheckTool.getInstance().queryPackageReturnedCheckM(tp);
		TParm dTP = INVPackageReturnedCheckTool.getInstance().queryPackageReturnedCheckD(tp);
		
		tableRM.setParmValue(mTP);
		tableRD.setParmValue(dTP);
	}
	
}
