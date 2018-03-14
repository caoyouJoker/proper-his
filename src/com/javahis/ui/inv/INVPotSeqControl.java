package com.javahis.ui.inv;

import jdo.inv.INVPotSeqTool;
import jdo.inv.InvSupTypeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

public class INVPotSeqControl extends TControl {
	
	private TTable table;
	private TTextField potSEQ;
	private TComboBox hlFlg;
	private boolean action = true;
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		this.table = this.getTable("TABLE");
		this.potSEQ = this.getTextField("POTSEQ");
		this.hlFlg = this.getComboBox("HL_FLG");
	}
	/**
	 * ����
	 */
	public void onSave(){
		String pot_seq = this.getValueString("POTSEQ");
		String hl_flg = this.getValueString("HL_FLG");
		if(pot_seq.equals("")){
			this.messageBox("����д����!");
			return;
		}
		if(hl_flg.equals("")){
			this.messageBox("����д�ߵ��±��!");
			return;
		}
		String checkSql = "SELECT * FROM INV_POTSEQ";
		TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
		for(int i=0;i<checkParm.getCount("POTSEQ");i++){
			if(checkParm.getValue("POTSEQ", i).equals(pot_seq)){
				action = false;
				break;
			}
		}
		TParm parm = new TParm();
		parm.setData("POTSEQ", pot_seq);
		parm.setData("HL_FLG", hl_flg);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = new TParm();
		if(action){
			result = INVPotSeqTool.getInstance().onInsert(parm);
		}else{
			result = INVPotSeqTool.getInstance().onUpdate(parm);
			action = true;
		}
		if (result.getErrCode() < 0) { 
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			messageBox("����ʧ�ܣ�");
			return;
		}
		this.messageBox("����ɹ�");
		this.onClear();
		this.onQuery();
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		String pot_seq = this.getValueString("POTSEQ");
		String hl_flg = this.getValueString("HL_FLG");
		TParm parm = new TParm();
		if(!pot_seq.equals("")){
			parm.setData("POTSEQ", pot_seq);
		}
		if(!hl_flg.equals("")){
			parm.setData("HL_FLG", hl_flg);
		}
		TParm result = INVPotSeqTool.getInstance().onQuery(parm);
		if(result == null){
			this.messageBox("δ��ѯ������");
			return;
		}
		this.table.setParmValue(result);
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		String clearStr = "POTSEQ;HL_FLG";
	    this.clearValue(clearStr);
	    table.removeRowAll();
	}
	
	/**
	 * ɾ��
	 */
	public void onDelete(){
		if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ɾ����");
            return;
        }
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        TParm result = INVPotSeqTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        table.removeRow(table.getSelectedRow());
        this.messageBox("ɾ���ɹ�");
	}
	
	public void onTableClick(){
		if (table.getSelectedRow() < 0) {
            return;
        }
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		potSEQ.setValue(parm.getValue("POTSEQ"));
		hlFlg.setValue(parm.getValue("HL_FLG"));
	}
	
	public TTable getTable(String tag){
		return (TTable) this.getComponent(tag);
	}
	
	public TTextField getTextField(String tag){
		return (TTextField) this.getComponent(tag);
	}
	
	public TComboBox getComboBox(String tag){
		return (TComboBox) this.getComponent(tag);
	}
}
