package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCTransportTrunkQueryTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:周转箱查询
 * </p>
 * 
 * <p>
 * Description: 周转箱查询
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

public class SPCTransportTrunkQueryControl extends TControl {

    // 主项表格
    private TTable TABLE_N,TABLE_Y,TABLE_C;
    
    private TPanel PANEL_Y,PANEL_N; //普药，麻精
    
    //申请单号
    private TTextField trunk_no;
    
    public SPCTransportTrunkQueryControl() {
    }

    /**初始化方法*/
    public void onInit() {
    	super.init();
//        TFrame tFrame  = (TFrame)getComponent("UI");
//		final TTextField mrField = (TTextField)getComponent("TRUNK_NO");
//		
//		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
//				            public void windowOpened(java.awt.event.WindowEvent evt) {
//				            	mrField.requestFocus();
//				            }
//				});
		TABLE_Y = (TTable)getComponent("TABLE_Y");
		PANEL_Y = (TPanel)getComponent("PANEL_Y");
		TABLE_N = (TTable) getComponent("TABLE_N");
		PANEL_N = (TPanel)getComponent("PANEL_N");
		TABLE_C = (TTable)getComponent("TABLE_C");
		
		TABLE_C.addEventListener(TTableEvent.CLICKED, this,"onTableClick");
		
		Timestamp sysDate = SystemTool.getInstance().getDate();
		setValue("END_DATE",sysDate.toString().substring(0, 10).replace('-', '/'));
		setValue("START_DATE",StringTool.rollDate(sysDate, -2).toString().substring(0, 10).replace('-', '/') );
      
    }
    
    /**周转箱回车事件*/
    public void onTrunkClicked() {
    	onQuery();
    }
    
    /**出库单号回车事件*/
    public void onDispenseNoClicked() {
    	onQuery();
    }
    

    /**查询*/
    public void onQuery() {
    	
    	TABLE_Y = (TTable)getComponent("TABLE_Y");
		PANEL_Y = (TPanel)getComponent("PANEL_Y");
		TABLE_N = (TTable) getComponent("TABLE_N");
		PANEL_N = (TPanel)getComponent("PANEL_N");
		
		TABLE_C = (TTable)getComponent("TABLE_C");
		
    	//周转箱
    	String boxEslId = getValueString("BOX_ESL_ID");
    	
    	//出库单号
    	String dispenseNo = getValueString("DISPENSE_NO");
    	
    	String orgCode = getValueString("ORG_CODE");
    	String startDate = getValueString("START_DATE");
    	String endDate = getValueString("END_DATE");
    	if(startDate != null && !startDate.equals("")){
			startDate = startDate.substring(0,10);
		}
		if(endDate != null && !endDate.equals("")){
			endDate = endDate.substring(0,10);
		}
    	
    	TParm parm = new TParm();
    	parm.setData("BOX_ESL_ID",boxEslId);
    	parm.setData("DISPENSE_NO",dispenseNo);
    	
    	parm.setData("ORG_CODE",orgCode);
    	parm.setData("START_DATE",startDate);
    	parm.setData("END_DATE",endDate);
    	
    	 //药品种类--普药:1,麻精：2
        if(getRadioButton("N_PUTAWAY").isSelected()){
        	parm.setData("IS_PUTAWAY","N");
        }else if(getRadioButton("Y_PUTAWAY").isSelected()){
        	parm.setData("IS_PUTAWAY","Y");
        }
    	
    	
    	if(PANEL_Y.isShowing()){
    		parm.setData("DRUG_CATEGORY","1");
    	}
    	if(PANEL_N.isShowing()){
    		parm.setData("DRUG_CATEGORY","2");
    	}
    
    	TParm result = SPCTransportTrunkQueryTool.getInstance().onQuery(parm);
    	//String orgCodeDesc = result.getValue("ORG_CHN_DESC", 0);
    	if(result.getCount() <  0 ){
			if(PANEL_Y.isShowing()){
				TABLE_Y.setParmValue(new TParm());
			}
			if(PANEL_N.isShowing()){
				TABLE_C.setParmValue(new TParm());
			}
			return ;
		}
    	//setValue("ORG_CHN_DESC", orgCodeDesc);
    	//this.getTextField("ORG_CHN_DESC").setEditable(false);
    	if(PANEL_Y.isShowing()){
    		TABLE_Y.setParmValue(result);
		}
		if(PANEL_N.isShowing()){
			TABLE_C.setParmValue(result);
		}
    	
    }
    
    public void onTableClick(){
    	
    	TABLE_C = (TTable)getComponent("TABLE_C");
    	TParm parm = TABLE_C.getParmValue() ;
    	int row = TABLE_C.getSelectedRow() ;
    	
    	TABLE_N = (TTable)getComponent("TABLE_N");
    	
    	String containerId  = parm.getValue("CONTAINER_ID", row);
    	String dispenseNo = parm.getValue("DISPENSE_NO",row);
    	
    	TParm inParm = new TParm();
    	inParm.setData("CONTAINER_ID",containerId);
    	inParm.setData("DISPENSE_NO",dispenseNo);
    	TParm result = SPCTransportTrunkQueryTool.getInstance().onQueryContainer(inParm);
    	if(result.getCount() <  0 ){
			this.messageBox("没有查询到数据");
			TABLE_N.removeRowAll();
			return ;
		}
    	TABLE_N.setParmValue(result);
    }
    

    
    /**清空方法*/
    public void onClear() {
        TABLE_N.removeRowAll();
        trunk_no.setText("");
    }

    private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
    
    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }
}
