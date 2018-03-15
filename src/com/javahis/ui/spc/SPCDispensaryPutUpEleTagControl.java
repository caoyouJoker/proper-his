package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCDispensaryPutUpTool;
import jdo.spc.SPCMaterialLocTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:药房普药入库后上架显示当前库存control
 * </p>
 * 
 * <p>
 * Description: 药房普药入库后上架显示当前库存control
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author Yuanxm 20121015
 * @version 1.0
 */

public class SPCDispensaryPutUpEleTagControl  extends TControl {


	TTable table_N;// 未入库
	TTable table_Y;// 已入库

	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	TParm parmData = new TParm();
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		
		/**
		TFrame tFrame = (TFrame) getComponent("UI");
		final TTextField dispenseNo = this.getTextField("BOX_ESL_ID");
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				dispenseNo.requestFocus();
			}
		});*/
		Timestamp sysDate = SystemTool.getInstance().getDate();
		setValue("END_DATE",sysDate.toString().substring(0, 10).replace('-', '/'));
		setValue("START_DATE",StringTool.rollDate(sysDate, -2).toString().substring(0, 10).replace('-', '/') );
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
	}


	public void onQuery(){
		
		//周转箱
		String boxEslId = getValueString("BOX_ESL_ID");
		
		//出库单号
		String dispenseNo = getValueString("DISPENSE_NO");
		
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		
		
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO",dispenseNo);
		parm.setData("BOX_ESL_ID",boxEslId);
		
		String startDate = getValueString("START_DATE");
		if(startDate != null && !startDate.equals("")){
			startDate = startDate.substring(0,10);
			parm.setData("START_DATE",startDate);
		}
		String endDate = getValueString("END_DATE");
		if(endDate != null && !endDate.equals("")){
			endDate = endDate.substring(0,10);
			parm.setData("END_DATE",endDate);
		}
		
		//状态传3表示已经入库，只是没有上架
		parm.setData("UPDATE_FLG","3");
		/**
		if(N_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","N");
			parm.setData("UPDATE_FLG","1");
		}else if(Y_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","Y");
			parm.setData("UPDATE_FLG","3");
		}*/
		
		TParm result = SPCDispensaryPutUpTool.getInstance().onQuery(parm);
		this.setValue("BOX_ESL_ID", boxEslId);
		this.setValue("DISPENSE_NO", dispenseNo);

		if(result.getCount() <  0 ){
			 
			if(N_PANEL.isShowing()){
				table_N.setParmValue(new TParm());
			}
			if(Y_PANEL.isShowing()){
				table_Y.setParmValue(new TParm());
			}
			return ;
		}
		
		String orgChnDesc = (String)result.getData( "ORG_CHN_DESC",0);
		setValue("ORG_CHN_DESC", orgChnDesc);
		//this.getTextField("DISPENSE_NO").setEditable(false);
		//this.getTextField("ORG_CHN_DESC").setEditable(false);
		if(N_PANEL.isShowing()){
			table_N.setParmValue(result);
			//调用电子标签接口
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				String orderDesc = rowParm.getValue("ORDER_DESC");
				EleTagControl.getInstance().login();
				EleTagControl.getInstance().sendEleTag(rowParm.getValue("ELETAG_CODE"), orderDesc, rowParm.getValue("SPECIFICATION"), "", 50);
			}
		}else if(Y_PANEL.isShowing()){
			table_Y.setParmValue(result);
		}
		
			
	}

	/**
	 * 周转箱回车事件
	 * */
	public void onMonZHOUZX() {
		
		onQuery();
		//this.getTextField("BOX_ESL_ID").setEditable(false);
		this.getTextField("ELETAG_CODE").grabFocus();
		
	}
	
	/**
	 * 出库单号回车事件
	 */
	public void onDispenseNo(){
		onQuery();
		//this.getTextField("BOX_ESL_ID").setEditable(false);
		this.getTextField("ELETAG_CODE").grabFocus();
	}

	/**
	 * 货位电子标签的回车事件
	 * 
	 * 
	 */
	public void onClickByElectTags() {
		
		//货位电子标签
		String eletagCode =  getValueString("ELETAG_CODE");
		if (StringUtil.isNullString(eletagCode)){
	        return;
		}
		String boxEslId = getValueString("BOX_ESL_ID");
		String dispenseNo = getValueString("DISPENSE_NO");
		
		
		if(!StringUtil.isNullString(dispenseNo)){
			this.setValue("DISPENSE_NO", dispenseNo);
		}
		
		table_N.acceptText();
		
		//搜索IND_MATERIALLOC,得到ORDER_CODE
		TParm tabParm = table_N.getParmValue();
			
		String labelNo = "";
		String productName = "";
		String orgCode = "";
		String orderCode = "";
		String spec = "";
		int count = table_N.getParmValue().getCount();
		
		//判断是否找到
		boolean b = false; 
    	//TParm mRowParm = new TParm();
		for (int i = 0; i < count; i++) {
			TParm dParm = tabParm.getRow(i);
			
			if(eletagCode.equals(dParm.getValue("ELETAG_CODE"))){
				
				//根据出库单号查询IND_DISPENSEM表信息
				TParm inParmSec = new TParm() ;
				dispenseNo = dParm.getValue("DISPENSE_NO");
				inParmSec.setData("DISPENSE_NO",dispenseNo);
		    	//=TParm mParm = SPCInStoreReginTool.getInstance().onQueryDispenseM(inParmSec);
		    	//mRowParm = mParm.getRow(0);
				//初始化值 
				//setValueAll(mRowParm);
				
				dParm.setData("PUTAWAY_USER",Operator.getID());
				dParm.setData("IS_PUTAWAY","Y");
				SPCDispensaryPutUpTool.getInstance().updateDispensed(dParm);
				labelNo = eletagCode;
				productName = dParm.getValue("ORDER_DESC");
				orgCode = dParm.getValue("ORG_CODE");
				orderCode= dParm.getValue("ORDER_CODE");
				//SPCDispensaryPutUpTool.getInstance().updateDispensed(rowParm);
				//onSave(mRowParm, dParm);
				b = true;
				
				table_N.removeRow(i);
				 
			}
			 
		}
		
		if(table_N.getRowCount() <= 0 ){
			this.setValue("BOX_ESL_ID", "");
			this.setValue("DISPENSE_NO", "");
		}else{
			this.setValue("BOX_ESL_ID", boxEslId);
		}
		this.clearValue("ELETAG_CODE");
		
		//是的，调用电子标签接口显示库存与闪烁
		if(b){
			TParm inParm = new TParm();
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("ORG_CODE",orgCode);
			inParm.setData("ELETAG_CODE",eletagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			spec = outParm.getValue("SPECIFICATION",0);
			 
			EleTagControl.getInstance().login();
			EleTagControl.getInstance().sendEleTag(labelNo, productName, spec, outParm.getValue("QTY",0), 0);
		}
		
		//onQuery() ;
		return;
	}
	

	/**
	 * TPanel改变事件
	 * */
	public void onTPanlClick() {
		onQuery();
	}
	
	

	/**
	 * 清空操作
	 * */
	public void onClear() {
		String controlName = "DISPENSE_NO;BOX_ESL_ID;ORG_CHN_DESC;ELETAG_CODE";
		this.clearValue(controlName);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		this.getTextField("DISPENSE_NO").setEditable(true);
		this.getTextField("ORG_CHN_DESC").setEditable(true);
		table_N.removeRowAll();
		table_Y.removeRowAll();
		this.getTextField("BOX_ESL_ID").grabFocus();
	}
	
   
    
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	
	
		
}
