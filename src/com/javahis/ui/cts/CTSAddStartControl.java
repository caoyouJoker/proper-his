package com.javahis.ui.cts;

import jdo.cts.CTSTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * 
 * <p>
 * Title:洗衣单独入库
 * </p>
 * 
 * <p>
 * Description:洗衣单独入库
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * implements MessageListener
 * @author huangtt 20140311
 * @version 1.0
 * 
 */
public class CTSAddStartControl extends TControl {
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String clothNo = acceptData.getData("CLOTH_NO").toString();
            this.setValue("CLOTH_NO", clothNo);
            this.onQueryNo();
        }
	}
	
	public void onQueryNo(){
		TParm parm = new TParm();
		parm.setData("CLOTH_NO", getValue("CLOTH_NO"));
		TParm result = CTSTool.getInstance().selectCloth(parm);
		this.setValue("INV_CODE", result.getValue("INV_CODE", 0));
		this.setValue("OWNER", result.getValue("OWNER", 0));
		this.setValue("OWNER_CODE", result.getValue("OWNER_CODE", 0));
		this.setValue("STATION_CODE", result.getValue("STATION_CODE", 0));
		this.setValue("TURN_POINT", result.getValue("TURN_POINT", 0));
	}
	
	public void onQuery(){
		this.onQueryNo();
	}
	
	public void onClear(){
		this.clearValue("CLOTH_NO;INV_CODE;OWNER;OWNER_CODE;STATION_CODE;TURN_POINT");
	}
	
	public void onSave(){
		TParm parmDD = new TParm();
		parmDD.setData("RFID", getValue("CLOTH_NO"));
		parmDD.setData("STATE", "0");
		TParm washM = new TParm();
		String wash_no = CTSTool.getInstance().getWashNo();
		washM.setData("WASH_NO", wash_no);
		washM.setData("DEPT_CODE", "");
		washM.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		washM.setData("QTY", 1);
		washM.setData("START_DATE", TJDODBTool.getInstance().getDBTime());
		String patFlg="Y";
		if(this.getValueString("OWNER").equals("")){
			patFlg = "N";
		}
		washM.setData("PAT_FLG", patFlg);
		washM.setData("STATE", 2);
		washM.setData("WASH_CODE", Operator.getID());
		washM.setData("OPT_USER", Operator.getID());
		washM.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		washM.setData("OPT_TERM", Operator.getIP());
		washM.setData("NEW_FLG", "N");
		washM.setData("TURN_POINT", this.getValueString("TURN_POINT"));
		TParm washD = new TParm();
		washD.setData("WASH_NO", wash_no);
		washD.setData("SEQ_NO", 1);
		washD.setData("CLOTH_NO", getValueString("CLOTH_NO"));
		washD.setData("OWNER", getValueString("OWNER"));
		washD.setData("PAT_FLG", patFlg);
		washD.setData("OPT_USER", Operator.getID());
		washD.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		washD.setData("OPT_TERM", Operator.getIP());
		washD.setData("OUT_FLG", "N");
		washD.setData("NEW_FLG", "M");
		washD.setData("TURN_POINT", this.getValueString("TURN_POINT"));
		TParm all = new TParm();
		all.setData("washM", washM.getData());
		all.setData("washD", washD.getData());
		all.setData("parmDD", parmDD.getData());
		TParm result = TIOM_AppServer.executeAction("action.cts.CTSAction","insertCTSWashMD",all);
    	if(result.getErrCode()<0){
    		this.messageBox("保存失败！");
    		return;
    	}
    	this.messageBox("保存成功！");
		
	}

}
