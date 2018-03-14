package com.javahis.ui.adm;


import javax.swing.SwingUtilities;

import jdo.adm.ADMDaySurgeryTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
/**
 * <p>Title: �ռ��������</p>
 *
 * <p>Description: ��Ժ֪ͨ</p>
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
	 * ��¼�������
	 */
	String CASE_NO = "";
	/**
	 * ��¼������
	 */
	String MR_NO = "";
	/**
	 * �ռ�����
	 */
	String DAY_OPE_FLG = "";
	/**
	 * �ռ����������󴫵Ĳ���
	 */
	String DayOpeFlg = "";
	
	/**
	 * �����ʼ��
	 */
    public void onInit(){
        super.onInit();
        onPageInit();
    }
    /**
     * ҳ���ʼ��
     */
    private void onPageInit(){
    	
        TParm parm = (TParm) getParameter();//��ѯ��Ϣ
        if(parm == null){
            this.messageBox_("��������Ϊ��");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        closeW();//�رմ���
                    }
                    catch (Exception e) {
                    }
                }
            });
            return;
        }
        //��ñ�Ҫ�Ĳ���
        CASE_NO = parm.getValue("CASE_NO");
        MR_NO = parm.getValue("MR_NO");
        DAY_OPE_FLG = parm.getValue("DAY_OPE_FLG");
        //this.messageBox("CASE_NO:" + CASE_NO + "--MR_NO:" + MR_NO + "--DAY_OPE_FLG:" + DAY_OPE_FLG);
        //�ǿ��ж�
        if(CASE_NO.length() < 0){
        	this.messageBox("ȱ�پ����");
        	return;
        }
        if(MR_NO.length() < 0){
        	this.messageBox("ȱ�ٲ�����");
        	return;
        }
        if(DAY_OPE_FLG.length() < 0){
        	this.messageBox("ȱ���ռ�������ʶ");
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
     * �رմ���
     */
    private void closeW(){
        this.closeWindow();
    }
    
    /**
     * �ռ�����״̬����
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
