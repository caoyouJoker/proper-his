package com.javahis.ui.pda;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;

public class PDABindingIPControl extends TControl{
	/**
	 * ��ѯ
	 */
	public void onQuery () {
		TParm p = new TParm();
		p.setData("ROOM_NO", this.getValueString("ROOM_NO"));
		p.setData("IP", this.getValueString("IP"));
		p.setData("OPBOOK_SEQ", this.getValueString("OPBOOK_SEQ"));
		p.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
		TParm result = TIOM_AppServer.executeAction("action.pda.PDAaction",
                "onQueryPDABindingIP", p);
		for(int i=0;i<result.getCount();i++){
			if("1".equals(result.getValue("TYPE_CODE",i))){
				result.setData("TYPE_CODE",i,"�������");
			}else if ("2".equals(result.getValue("TYPE_CODE",i))){
				result.setData("TYPE_CODE",i,"��������");
			}
		}
		getTable("TABLE").setParmValue(result);
	}
	/**
	 *���
	 */
	public void onClear() {
		this.clearValue("ROOM_NO;IP;OPBOOK_SEQ;TYPE_CODE");
		callFunction("UI|TABLE|removeRowAll");
	}
	
	/**
	 * ��
	 * @throws ParseException 
	 */
	public void onSave(){
		if (this.messageBox("��ʾ", "�Ƿ񱣴�?", 2) == 0) {
			if("".equals(this.getValueString("ROOM_NO"))){
				this.messageBox("���䲻��Ϊ��");
				this.grabFocus("ROOM_NO");
				return;
			}
			if("".equals(this.getValueString("IP"))){
				this.messageBox("��IP����Ϊ��");
				this.grabFocus("IP");
				return;
			}
			Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
			Matcher matcher = pattern.matcher(this.getValueString("IP"));
			if(!matcher.matches()){
				this.messageBox("��IP��ʽ����");
				this.grabFocus("IP");
				return;
			}
			TParm result = new TParm();
			TTable table = this.getTable("TABLE");
			int row = table.getSelectedRow();
			TParm tableParm = table.getParmValue();
			String roomNo = this.getValueString("ROOM_NO");
			String ip = this.getValueString("IP");
			//����
			if(row >= 0){
				String oldIp = tableParm.getValue("IP", row);
				String sql = "SELECT IP,ROOM_NO FROM OPE_IPROOM WHERE IP != '"+oldIp+"'";
				TParm iPparm = new TParm(TJDODBTool.getInstance().select(sql));
				for(int i=0;i<iPparm.getCount();i++){
					if(ip.equals(iPparm.getValue("IP",i))){
						this.messageBox("IP�����ظ������������룡");
						return;
					}
					if(roomNo.equals(iPparm.getValue("ROOM_NO",i))){
						this.messageBox("���䲻���ظ������������룡");
						return;
					}
				}
				TParm p = new TParm();
				p.setData("OLD_IP", oldIp);
				p.setData("ROOM_NO", this.getValueString("ROOM_NO"));
				p.setData("IP", this.getValueString("IP"));
				p.setData("OPBOOK_SEQ", this.getValueString("OPBOOK_SEQ"));
				p.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
				p.setData("OPT_USER", Operator.getID());
				p.setData("OPT_TERM", Operator.getIP());
				result = TIOM_AppServer.executeAction("action.pda.PDAaction",
						"onUpdatePDABindingIP", p);
				//����
			} else {
				String sql = "SELECT IP,ROOM_NO FROM OPE_IPROOM WHERE IP = '"+ip+"' ";
				TParm iPparm = new TParm(TJDODBTool.getInstance().select(sql));
				if(iPparm.getCount() > 0){
					this.messageBox("IP�����ظ������������룡");
					return;
				}
				sql = "SELECT IP,ROOM_NO FROM OPE_IPROOM WHERE ROOM_NO = '"+this.getValueString("ROOM_NO")+"' ";
				iPparm = new TParm(TJDODBTool.getInstance().select(sql));
				if(iPparm.getCount() > 0){
					this.messageBox("���䲻���ظ������������룡");
					return;
				}
				TParm p = new TParm();
				p.setData("ROOM_NO", this.getValueString("ROOM_NO"));
				p.setData("IP", this.getValueString("IP"));
				p.setData("OPBOOK_SEQ", this.getValueString("OPBOOK_SEQ"));
				p.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
				p.setData("OPT_USER", Operator.getID());
				p.setData("OPT_TERM", Operator.getIP());
				result = TIOM_AppServer.executeAction("action.pda.PDAaction",
						"onInsertPDABindingIP", p);
			}
			
			if (result.getErrCode() < 0) {
				this.messageBox("E0001");
				onClear();
				return;
			} else {
				this.messageBox("P0001");
			}
			onQuery();
		}
	}
	
	/**
	 * ���
	 */
	public void onRemove() {
		int row = getTable("TABLE").getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ����¼");
			return;
		}
		if (this.messageBox("��ʾ", "�Ƿ���?", 2) == 0) {
			TParm tableParm = getTable("TABLE").getParmValue();
			if(!"".equals(tableParm.getValue("OPBOOK_SEQ",row))){
				TParm p = new TParm();
				p.setData("ROOM_NO", tableParm.getValue("ROOM_NO",row));
				p.setData("IP", tableParm.getValue("IP",row));
				p.setData("OPT_USER", Operator.getID());
				p.setData("OPT_TERM", Operator.getIP());
				TParm result = TIOM_AppServer.executeAction("action.pda.PDAaction",
						"onRemovePDABindingIP", p);
				if (result.getErrCode() < 0) {
					this.messageBox("���ʧ�ܣ�");
					onClear();
					return;
				} 
				this.messageBox("���ɹ���");
				onQuery();
			} else {
				this.messageBox("�ѽ��");
			}
		}
		
	}
	
	/**
	 * ɾ��
	 */
	public void onDelete() {
		int row = getTable("TABLE").getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ����¼");
			return;
		}
		if (this.messageBox("��ʾ", "�Ƿ�ɾ��?", 2) == 0) {
			TParm tableParm = getTable("TABLE").getParmValue();
			TParm p = new TParm();
			p.setData("ROOM_NO", tableParm.getValue("ROOM_NO", row));
			p.setData("IP", tableParm.getValue("IP", row));
			TParm result = TIOM_AppServer.executeAction("action.pda.PDAaction",
					"onDeletePDABindingIP", p);
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ�ܣ�");
				onClear();
				return;
			} 
			this.messageBox("ɾ���ɹ���");
			onQuery();
		}
		
	}
	/**
	 *  ��񵥻��¼� 
	 * @param row
	 */
	public void onTable() {
		int row = getTable("TABLE").getSelectedRow();
		TParm tableParm = getTable("TABLE").getParmValue();
		setValue("ROOM_NO", tableParm.getData("ROOM_NO", row));
		setValue("IP", tableParm.getData("IP", row));
		setValue("OPBOOK_SEQ", tableParm.getData("OPBOOK_SEQ", row));
		String type = (String) tableParm.getData("TYPE_CODE", row);
		if("�������".equals(type)){
			setValue("TYPE_CODE", "1");
		}
		if("��������".equals(type)){
			setValue("TYPE_CODE", "2");
		}
	}
	
	/**
	 * ȡ��Table�ؼ�
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}
}
