package com.javahis.ui.inv;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.inv.INVPackageReturnedCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;


/**
 * 
 * <p>
 * Title:���ư��˻�����
 * </p>
 * 
 * <p>
 * Description: ���ư��˻�����
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
public class INVPackageReturnedCheckControl extends TControl{

	private TTable tableRM;			//�˻�������
	
	private TTable tableRD;			//�˻���ϸ��
	
	private boolean isNew = false;	//�½����
	
	private String returnedNo ;		//�˻�����
	
	private TParm tParm = new TParm();
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
	
		tableRM = (TTable) getComponent("TABLEM");
		tableRD = (TTable) getComponent("TABLED");
		
		this.setTimes();
		
		//����������
        callFunction("UI|TABLED|addEventListener",
                     TTableEvent.CREATE_EDIT_COMPONENT, this,
                     "onCreateEditComponentPack");
		
	}
	 
	/**
     * �������ʵ�������(������)
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComponentPack(Component com, int row, int column) {
        //����INV_PACK�Ի������
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //��table�ϵ���text����INV_PACK��������
        TParm parm = new TParm();
        textfield.setPopupMenuParameter("PACK", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        //����text���ӽ���INV_PACK�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newPack");
    }
    /**
     * ������ϸ
     * @param tag String
     * @param obj Object
     */
    public void newPack(String tag, Object obj) {
        if (obj == null)
            return;
        tableRD.acceptText();
        
        TParm parm = (TParm) obj;
        String packCode = parm.getValue("PACK_CODE");
        
        TParm packParm = tableRD.getShowParmValue();
        
        if(null!=packParm){
        	int row = tableRD.getSelectedRow();
            for (int i = 0; i < packParm.getCount("PACK_CODE"); i++) {
                if (i == row) {
                    continue;
                }
                if (packCode.equals(packParm.getValue("PACK_CODE", i))) {
                    this.messageBox("������ͬ������!");
                    tableRD.setItem(row, "PACK_CODE", "");
                    tableRD.setItem(row, "PACK_DESC", "");
                    return;
                }
            }
        }
        setTableDValue(parm,tableRD.getSelectedRow());
        
        if (tableRD.getRowCount() == tableRD.getSelectedRow() + 1) {
        	tableRD.addRow();
        }
        tableRD.acceptText();
    }
	
    public void setTableDValue(TParm parm, int row) {
    	Timestamp date = SystemTool.getInstance().getDate();
    	tableRD.setRowParmValue(row, parm);
    	tableRD.setItem(row, "QTY", 0);
        tableRD.setItem(row, "OPT_USER", Operator.getID());
        tableRD.setItem(row, "OPT_DATE", date.toString().substring(0,19));
        tableRD.setItem(row, "OPT_TERM", Operator.getIP());
        tableRD.acceptText();
    }
    
    
    
	
	
	public void onNew(){
		if("".equals(this.getValueString("FROM_ORG_CODE"))){
			this.messageBox("��ѡ���˻����벿�ţ�");
            return;
		}
		if("".equals(this.getValueString("TO_ORG_CODE"))){
			this.messageBox("��ѡ���˻����ղ��ţ�");
            return;
		}
		if( "".equals(this.getValueString("START_DATE")) || "".equals(this.getValueString("END_DATE")) ){
			this.messageBox("��ѡ���������䣡");
            return;
		}
		if(isNew){
			this.messageBox("����ͬʱ�½������˻�����");
            return;
		}
		
		isNew = true;
		tParm = new TParm();
		returnedNo = this.getReturnedNo();
		
		tableRM.removeRowAll();
		tableRD.removeRowAll();

		this.createNewReturnedM(); //�����˻�������
		tableRD.addRow();
		
	}
	
	
	
	public void onSave(){
		Timestamp date = SystemTool.getInstance().getDate();
		//�½�״̬
		if(isNew){
			if(tableRD.getRowCount()<=1){
				messageBox("û����Ҫ��������ݣ�");
				return;
			} 
			
			TParm mTP = tableRM.getShowParmValue();
			mTP.setData("PACKAGERETURNED_NO", tableRM.getSelectedRow(), returnedNo);
			mTP.setData("FROM_ORG_CODE", tableRM.getSelectedRow(), getValueString("FROM_ORG_CODE"));
			mTP.setData("TO_ORG_CODE", tableRM.getSelectedRow(), getValueString("TO_ORG_CODE"));
			mTP.setData("CHECK_USER", tableRM.getSelectedRow(), Operator.getID());
			mTP.setData("OPT_USER", tableRM.getSelectedRow(), Operator.getID());
			mTP.setData("OPT_DATE", tableRM.getSelectedRow(), date.toString().substring(0,19));
			mTP.setData("OPT_TERM", tableRM.getSelectedRow(), Operator.getIP());
			TParm dTP = tableRD.getShowParmValue();
			
			int seq = 1;
			for(int i=0; i<dTP.getCount("PACK_CODE")-1; i++){
				if(dTP.getDouble("QTY", i) <= 0){
					messageBox( dTP.getValue("PACK_DESC", i) + "�˰���������С�ڵ���0��");
					return;
				}else{
					dTP.setData("SEQ", i, seq);
					dTP.setData("PACKAGERETURNED_NO", i, returnedNo);
					dTP.setData("OPT_USER", i,  Operator.getID());
					dTP.setData("OPT_DATE", i,  date.toString().substring(0,19));
				}
				seq = seq + 1;
			}
			
			tParm.setData("RETURNM", mTP.getData());
			tParm.setData("RETURND", dTP.getData());
			
			TParm result = TIOM_AppServer.executeAction("action.inv.INVPackageReturnedCheckAction",
		            "onInsert", tParm);
			if (result.getErrCode() < 0) { 
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				messageBox("����ʧ�ܣ�");
				return;
			}
			messageBox("����ɹ���");

			this.queryByNo();
			
			isNew = false;
			tParm = new TParm();
			returnedNo = "";
			
			return;
		}
		//���½�  ����δ���״̬
		if(!isNew){
			int row = tableRM.getSelectedRow();
			if(row<0){
				messageBox("��ѡ���˻�����");
				return;
			}
			TParm tp = tableRM.getParmValue().getRow(row);
			if(tp.getData("CONFIRM_FLG").equals("Y")){
				messageBox("��ȷ�ϵ��˻��������޸ģ�");
				return;
			}
			
//			int rowD = tableRD.getSelectedRow();
			int rowCount = tableRD.getRowCount();
			if(rowCount == 1){
				messageBox("�˻�����ϸ����Ϣ���޷����棡");
				return;
			}
			
			TParm dTP = tableRD.getShowParmValue();
			
			int seq = 1;
			for(int i=0; i<dTP.getCount("PACK_CODE")-1; i++){
				if(dTP.getDouble("QTY", i) <= 0){
					messageBox( dTP.getValue("PACK_DESC", i) + "�˰���������С�ڵ���0��");
					return;
				}else{
					dTP.setData("SEQ", i, seq);
					dTP.setData("PACKAGERETURNED_NO", i, tp.getData("PACKAGERETURNED_NO"));
					dTP.setData("OPT_USER", i, Operator.getID());
				}
				seq = seq + 1;
			}
			
			returnedNo = tp.getData("PACKAGERETURNED_NO").toString();
			
			tParm.setData("RETURNM", tp.getData());
			tParm.setData("RETURND", dTP.getData());
			
			TParm result = TIOM_AppServer.executeAction("action.inv.INVPackageReturnedCheckAction",
		            "onUpdate", tParm);
			if (result.getErrCode() < 0) { 
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				messageBox("����ʧ�ܣ�");
				tParm = new TParm();
				returnedNo = "";
				return;
			}
			messageBox("����ɹ���");

			this.queryByNo();
			
			tParm = new TParm();
			returnedNo = "";
			
			return;
		}
	}

	public void onDelete(){
		
		//�½�״̬
		if(isNew){
			int rowM = tableRM.getSelectedRow();
			int rowD = tableRD.getSelectedRow();
			
			int rowCount = tableRD.getRowCount();
			
			if(rowD == rowCount-1 ){
				messageBox("�հ����޷�ɾ����");
				return;
			}else if(rowCount>1 && rowD !=rowCount-1 && rowD>-1){
				tableRD.removeRow(rowD);
				return;
			}else if( (rowCount==1 && rowM == 0) || (rowD == -1 && rowM == 0)){
				if(this.messageBox("��ʾ��Ϣ", "�Ƿ�ɾ���˻������", this.YES_NO_OPTION)==0){
					tableRM.removeRowAll();
					tableRD.removeRowAll();
					isNew = false;
					tParm = new TParm();
					returnedNo = "";
					return;
				}else{
					return;
				}
			}
		}
		if(!isNew){
			int row = tableRM.getSelectedRow();
			if(row<0){
				messageBox("��ѡ���˻�����");
				return;
			}
			TParm tp = tableRM.getParmValue().getRow(row);
			if(tp.getData("CONFIRM_FLG").equals("Y")){
				messageBox("��ȷ�ϵ��˻�������ɾ����");
				return;
			}
			
			int rowM = tableRM.getSelectedRow();
			int rowD = tableRD.getSelectedRow();
			int rowCount = tableRD.getRowCount();
			
			if( rowM>-1 && rowD == -1 ){
				if(this.messageBox("��ʾ��Ϣ", "�Ƿ�ɾ���˻���ȫ����Ϣ��", this.YES_NO_OPTION)==0){
					
					TParm t = tableRM.getParmValue().getRow(rowM);
					TParm result = TIOM_AppServer.executeAction("action.inv.INVPackageReturnedCheckAction",
				            "onDelete", t);
					if (result.getErrCode() < 0) { 
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						messageBox("ɾ��ʧ�ܣ�");
						return;
					}
					
					tableRM.removeRowAll();
					tableRD.removeRowAll();

					return;
				}
			}else if(rowD == rowCount-1){
				messageBox("�հ����޷�ɾ����");
				return;
			}else{
				tableRD.removeRow(rowD);
				return;
			}
			
		}

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
		if(!"".equals(this.getValueString("FROM_ORG_CODE"))){
			tp.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
		}
		if(!"".equals(this.getValueString("TO_ORG_CODE"))){
			tp.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		}
		if(!"".equals(this.getValueString("RETURNEDNO"))){
			tp.setData("PACKAGERETURNED_NO", this.getValueString("RETURNEDNO"));
		}
		if(((TRadioButton)this.getComponent("CONFIRM")).isSelected()){
			tp.setData("CONFIRM_FLG", "Y");
		}else{
			tp.setData("CONFIRM_FLG", "N");
		}
		tableRM.removeRowAll();
		tableRD.removeRowAll();
		TParm mTP = INVPackageReturnedCheckTool.getInstance().queryPackageReturnedCheckM(tp);
		tableRM.setParmValue(mTP);
		
	}
	
	public void onTableMClick(){
		
		if(!isNew){
			int row = tableRM.getSelectedRow();
			TParm selParm = tableRM.getParmValue().getRow(row);
			
			TParm tp = new TParm();
			tp.setData("PACKAGERETURNED_NO", selParm.getData("PACKAGERETURNED_NO"));
			TParm dTP = INVPackageReturnedCheckTool.getInstance().queryPackageReturnedCheckD(tp);
			tableRD.removeRowAll();
			tableRD.setParmValue(dTP);
			
			tableRD.addRow();
			
			this.setValue("FROM_ORG_CODE", selParm.getData("FROM_ORG_CODE"));
			this.setValue("TO_ORG_CODE", selParm.getData("TO_ORG_CODE"));
		}
		
		
	}
	
	public void onPrint(){
		
		if(isNew){
			this.messageBox("�뱣����ٴ�ӡ") ;
			return ;
		}
		
		int selectedRow = tableRM.getSelectedRow();
		if(selectedRow<0){
			this.messageBox("��ѡ���˻���") ;
			return ;
		}
		TParm selParm = tableRM.getParmValue().getRow(selectedRow);	
		TParm tableParm = tableRD.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		DecimalFormat df1 = new DecimalFormat("0");
		int seq = 1;
		for(int i=0;i<tableParm.getCount()-1;i++){
			
			if(null == tableParm.getValue("PACK_CODE", i) || "".equals(tableParm.getValue("PACK_CODE", i))){
				break;
			}
			
			result.addData("SEQ", seq); //��ֵ 
			result.addData("PACK_CODE", tableParm.getValue("PACK_CODE", i)); 
			result.addData("PACK_DESC", tableParm.getValue("PACK_DESC", i)); 
//			TParm orgParm = new TParm(TJDODBTool.getInstance().select(this.getSQL(tableParm.getValue("BILL_UNIT", i))));
			result.addData("QTY", df1.format(tableParm.getDouble("QTY", i))); 
			result.addData("DESCRIPTION", "");
			seq = seq + 1;
		}
		
		//�鿴�������Ƿ���6������������-�����������ǣ�(count+2)%6���� ����6-����������-duzhw
		int allCount = tableParm.getCount()-1;
		int remainder = allCount%7;//����
		int addCount = 0;
		if(remainder!=0){
			addCount = 7 - remainder;
			for (int i = 0; i < addCount; i++){
				result.addData("SEQ", ""); 
				result.addData("PACK_CODE", ""); 
				result.addData("PACK_DESC", ""); 
				result.addData("QTY", ""); 
				result.addData("DESCRIPTION", ""); 
			}
		}
		
		result.setCount(tableParm.getCount() - 1 + addCount) ; 
		result.addData("SYSTEM", "COLUMNS", "SEQ");
		result.addData("SYSTEM", "COLUMNS", "PACK_CODE");
		result.addData("SYSTEM", "COLUMNS", "PACK_DESC");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);
		
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()); 
		printParm.setData("RETURNED_NO", "TEXT", selParm.getData("PACKAGERETURNED_NO")) ;
		printParm.setData("FROM_ORG_CODE", "TEXT", ((TTextFormat) getComponent("FROM_ORG_CODE")).getText());
		printParm.setData("TO_ORG_CODE", "TEXT", ((TTextFormat) getComponent("TO_ORG_CODE")).getText());
		printParm.setData("DATE", "TEXT", pDate);
		printParm.setData("CREATE_USER", "TEXT", Operator.getName());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackageReturn.jhw",printParm);
	}
	

	 public void onClear(){
		 
		 this.setTimes();
		 tableRM.removeRowAll();
		 tableRD.removeRowAll();
		 setValue("FROM_ORG_CODE", "");
		 setValue("TO_ORG_CODE", "");
		 setValue("RETURNEDNO", "");
		 
		 ( (TRadioButton)this.getComponent("UNCONFIRM")).setSelected(true);
		 
		 isNew = false;
		 tParm = new TParm();
		 returnedNo = "";
	 }

	/**
	 * 
	 * �������ư��˻�������
	 * 
	 * 
	 */

	private void createNewReturnedM(){
		Timestamp date = SystemTool.getInstance().getDate();
		//������
        int row = tableRM.addRow();
        tableRM.setItem(row, "CONFIRM_FLG", "N");
        tableRM.setItem(row, "PACKAGERETURNED_NO", "");
        tableRM.setItem(row, "FROM_ORG_CODE", getValueString("FROM_ORG_CODE"));
        tableRM.setItem(row, "TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        tableRM.setItem(row, "CHECK_USER", Operator.getID());
        tableRM.setItem(row, "CHECK_DATE", date.toString().substring(0,19));
        tableRM.setItem(row, "CONFIRM_USER", "");
        tableRM.setItem(row, "CONFIRM_DATE", "");
        
        tableRM.setSelectedRow(row);
		
	}



	
	/** 
	 * �����˻����� 
	 *  */
	private String getReturnedNo() {
		String returnedNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_SUP_PRETURNM", "No");
		return returnedNo;
	}
	
	private void setTimes(){
		//��ʼ��    �˻����ڲ�ѯ����
        Timestamp date = SystemTool.getInstance().getDate();
        String startDate = date.toString().substring(0, 10).replace('-', '/') + " 00:00:00";
        String endDate = date.toString().substring(0, 19).replace('-', '/');
        this.setValue("END_DATE",endDate);
        this.setValue("START_DATE",startDate);
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
