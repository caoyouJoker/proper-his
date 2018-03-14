package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.sys.SYSBedTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;

/**
 * <p>Title:包床管理 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMSysBedAlloControl extends TControl{
     TParm acceptData = new TParm(); //接参

    public void onInit(){
        acceptData=(TParm)this.getParameter();
        this.setValueForParm("DEPT_CODE;STATION_CODE",acceptData);
        this.initRoom();
        TComboBox box = (TComboBox)this.getComponent("BED_NO");
        box.onQuery();
    }
    /**
     * 初始化病房
     */
    public void initRoom() {
        TParm parm = new TParm();
        parm.setData("BED_NO", acceptData.getData("BED_NO"));
        TParm result = SYSBedTool.getInstance().queryAll(parm);
        this.setValue("ROOM_CODE", result.getData("ROOM_CODE", 0));
    }

    /**
     * 包床保存
     */
    public void onBed(){
    	if("".equals(this.getValue("BED_NO"))){
    		this.messageBox("请选择包床床位!");
    		return;
    	}
    	String sql = "SELECT APPT_FLG FROM SYS_BED WHERE BED_NO = '" + this.getValue("BED_NO") + "'";
    	TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
    	if("Y".equals(sqlparm.getValue("APPT_FLG", 0))){
    		this.messageBox("此床位已被预约!");
    		return;
    	}
        TParm parm = new TParm();
        parm.setData("BED_NO",this.getValue("BED_NO"));
        parm.setData("CASE_NO",acceptData.getData("CASE_NO"));
        parm.setData("IPD_NO",acceptData.getData("IPD_NO"));
        parm.setData("MR_NO",acceptData.getData("MR_NO"));
        parm.setData("ALLO_FLG","Y");
        parm.setData("BED_OCCU_FLG","Y");
        parm.setData("BED_STATUS","3");//床位状态设置为 包床
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        TParm result = SYSBedTool.getInstance().alloSysbed(parm);
        if(result.getErrCode()<0){
             this.messageBox("E0005"); //失败
        }else{
             this.messageBox("P0005"); //成功
             TComboBox box = (TComboBox)this.getComponent("BED_NO");
             box.setSelectedIndex(0);
             box.onQuery();
        }
    }
    public void onCan(){
        this.closeWindow();
    }
}
