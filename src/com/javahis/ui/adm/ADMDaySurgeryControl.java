package com.javahis.ui.adm;


import javax.swing.SwingUtilities;

import jdo.adm.ADMDaySurgeryTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
/**
 * <p>Title: 日间手术变更</p>
 *
 * <p>Description: 出院通知</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhanglei  2017-3-31
 * @version 5.0
 */
public class ADMDaySurgeryControl 
	extends TControl {
	/**
	 * 记录就诊序号
	 */
	String CASE_NO = "";
	/**
	 * 记录病案号
	 */
	String MR_NO = "";
	/**
	 * 日间手术
	 */
	String DAY_OPE_FLG = "";
	/**
	 * 日间手术变更向后传的参数
	 */
	String DayOpeFlg = "";
	
	/**
	 * 界面初始化
	 */
    public void onInit(){
        super.onInit();
        onPageInit();
    }
    /**
     * 页面初始化
     */
    private void onPageInit(){
    	
        TParm parm = (TParm) getParameter();//查询信息
        if(parm == null){
            this.messageBox_("参数不可为空");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        closeW();//关闭窗口
                    }
                    catch (Exception e) {
                    }
                }
            });
            return;
        }
        //获得必要的参数
        CASE_NO = parm.getValue("CASE_NO");
        MR_NO = parm.getValue("MR_NO");
        DAY_OPE_FLG = parm.getValue("DAY_OPE_FLG");
        //this.messageBox("CASE_NO:" + CASE_NO + "--MR_NO:" + MR_NO + "--DAY_OPE_FLG:" + DAY_OPE_FLG);
        //非空判定
        if(CASE_NO.length() < 0){
        	this.messageBox("缺少就诊号");
        	return;
        }
        if(MR_NO.length() < 0){
        	this.messageBox("缺少病案号");
        	return;
        }
        if(DAY_OPE_FLG.length() < 0){
        	this.messageBox("缺少日间手术标识");
        	return;
        }
        
        if(DAY_OPE_FLG.equals("Y")){
            this.setValue("DS_Y",true);
            System.out.println("1111111"+this.getValueBoolean("DS_Y"));
        }else{
        	this.setValue("DS_N",true);
        	System.out.println("22222222"+this.getValueBoolean("DS_N"));
        	
        }
//        if(obj instanceof TParm){
//        	allParm = (TParm)obj;
//        }
    }
    
    /**
     * 关闭窗口
     */
    private void closeW(){
        this.closeWindow();
    }
    
    /**
     * 日间手术状态保存
     */
    public void onSave(){
        //this.messageBox("CASE_NO:" + CASE_NO + "--MR_NO:" + MR_NO + "--DAY_OPE_FLG:" + DAY_OPE_FLG);
        TParm SJ = new TParm();
        
        if(this.getValueBoolean("DS_Y")){
        	DayOpeFlg = "Y";
        	//this.messageBox("YYYYY");
        }else{
        	DayOpeFlg = "N";
        	//this.messageBox("NNNNN");
        }
        SJ.setData("DayOpeFlg",DayOpeFlg);
        SJ.setData("MR_NO",MR_NO);
        SJ.setData("CASE_NO",CASE_NO);
        
		TParm result = TIOM_AppServer.executeAction(
				"action.adm.ADMDaySurgeryAction", "onSave", SJ);
		if (result.getErrCode() != 0) {
			this.messageBox("E0001");
			return;
		} else {
			this.messageBox("P0001");
		}
		  
    }
    
    
}
