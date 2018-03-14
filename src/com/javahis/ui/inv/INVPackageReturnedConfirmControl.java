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
 * Title:���ư��˻�ȷ��
 * </p>
 * 
 * <p>
 * Description: ���ư��˻�ȷ��
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

	private TTable tableRM;			//�˻�������
	
	private TTable tableRD;			//�˻���ϸ��
	
//	private boolean isNew = false;	//�½����
	
	private String returnedNo ;		//�˻�����
	
//	private TParm tParm = new TParm();
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
	
		tableRM = (TTable) getComponent("TABLEM");
		tableRD = (TTable) getComponent("TABLED");
		
		this.setTimes();
		
	}
	 
	private void setTimes(){
		//��ʼ��    �˻����ڲ�ѯ����
        Timestamp date = SystemTool.getInstance().getDate();
        String startDate = date.toString().substring(0, 10).replace('-', '/') + " 00:00:00";
        String endDate = date.toString().substring(0, 19).replace('-', '/');
        this.setValue("END_DATE",endDate);
        this.setValue("START_DATE",startDate);
	}

	
	
	public void onSave(){
		
		int row = tableRM.getSelectedRow();
		if(row<0){
			messageBox("��ѡ���˻�����");
			return;
		}
		TParm tp = tableRM.getParmValue().getRow(row);
		if(tp.getData("CONFIRM_FLG").equals("Y")){
			messageBox("��ȷ�ϵ��˻��������ٴ�ȷ�ϣ�");
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
			messageBox("����ʧ�ܣ�");
			return;
		}
		messageBox("����ɹ���");
		returnedNo = tp.getData("PACKAGERETURNED_NO").toString();
		this.queryByNo();
	}


	
	public void onQuery(){
		
		if( "".equals(this.getValueString("START_DATE")) || "".equals(this.getValueString("END_DATE")) ){
			this.messageBox("��ѡ���������䣡");
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
	 * �����˻�����Ų�ѯ�˻�����Ϣ 
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
