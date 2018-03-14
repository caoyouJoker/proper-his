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
	 * 查询
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
				result.setData("TYPE_CODE",i,"外科手术");
			}else if ("2".equals(result.getValue("TYPE_CODE",i))){
				result.setData("TYPE_CODE",i,"介入手术");
			}
		}
		getTable("TABLE").setParmValue(result);
	}
	/**
	 *清空
	 */
	public void onClear() {
		this.clearValue("ROOM_NO;IP;OPBOOK_SEQ;TYPE_CODE");
		callFunction("UI|TABLE|removeRowAll");
	}
	
	/**
	 * 绑定
	 * @throws ParseException 
	 */
	public void onSave(){
		if (this.messageBox("提示", "是否保存?", 2) == 0) {
			if("".equals(this.getValueString("ROOM_NO"))){
				this.messageBox("术间不可为空");
				this.grabFocus("ROOM_NO");
				return;
			}
			if("".equals(this.getValueString("IP"))){
				this.messageBox("绑定IP不可为空");
				this.grabFocus("IP");
				return;
			}
			Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
			Matcher matcher = pattern.matcher(this.getValueString("IP"));
			if(!matcher.matches()){
				this.messageBox("绑定IP格式错误");
				this.grabFocus("IP");
				return;
			}
			TParm result = new TParm();
			TTable table = this.getTable("TABLE");
			int row = table.getSelectedRow();
			TParm tableParm = table.getParmValue();
			String roomNo = this.getValueString("ROOM_NO");
			String ip = this.getValueString("IP");
			//更新
			if(row >= 0){
				String oldIp = tableParm.getValue("IP", row);
				String sql = "SELECT IP,ROOM_NO FROM OPE_IPROOM WHERE IP != '"+oldIp+"'";
				TParm iPparm = new TParm(TJDODBTool.getInstance().select(sql));
				for(int i=0;i<iPparm.getCount();i++){
					if(ip.equals(iPparm.getValue("IP",i))){
						this.messageBox("IP不能重复，请重新输入！");
						return;
					}
					if(roomNo.equals(iPparm.getValue("ROOM_NO",i))){
						this.messageBox("术间不能重复，请重新输入！");
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
				//插入
			} else {
				String sql = "SELECT IP,ROOM_NO FROM OPE_IPROOM WHERE IP = '"+ip+"' ";
				TParm iPparm = new TParm(TJDODBTool.getInstance().select(sql));
				if(iPparm.getCount() > 0){
					this.messageBox("IP不能重复，请重新输入！");
					return;
				}
				sql = "SELECT IP,ROOM_NO FROM OPE_IPROOM WHERE ROOM_NO = '"+this.getValueString("ROOM_NO")+"' ";
				iPparm = new TParm(TJDODBTool.getInstance().select(sql));
				if(iPparm.getCount() > 0){
					this.messageBox("术间不能重复，请重新输入！");
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
	 * 解绑
	 */
	public void onRemove() {
		int row = getTable("TABLE").getSelectedRow();
		if(row < 0){
			this.messageBox("请选中一条记录");
			return;
		}
		if (this.messageBox("提示", "是否解绑?", 2) == 0) {
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
					this.messageBox("解绑失败！");
					onClear();
					return;
				} 
				this.messageBox("解绑成功！");
				onQuery();
			} else {
				this.messageBox("已解绑");
			}
		}
		
	}
	
	/**
	 * 删除
	 */
	public void onDelete() {
		int row = getTable("TABLE").getSelectedRow();
		if(row < 0){
			this.messageBox("请选中一条记录");
			return;
		}
		if (this.messageBox("提示", "是否删除?", 2) == 0) {
			TParm tableParm = getTable("TABLE").getParmValue();
			TParm p = new TParm();
			p.setData("ROOM_NO", tableParm.getValue("ROOM_NO", row));
			p.setData("IP", tableParm.getValue("IP", row));
			TParm result = TIOM_AppServer.executeAction("action.pda.PDAaction",
					"onDeletePDABindingIP", p);
			if (result.getErrCode() < 0) {
				this.messageBox("删除失败！");
				onClear();
				return;
			} 
			this.messageBox("删除成功！");
			onQuery();
		}
		
	}
	/**
	 *  表格单击事件 
	 * @param row
	 */
	public void onTable() {
		int row = getTable("TABLE").getSelectedRow();
		TParm tableParm = getTable("TABLE").getParmValue();
		setValue("ROOM_NO", tableParm.getData("ROOM_NO", row));
		setValue("IP", tableParm.getData("IP", row));
		setValue("OPBOOK_SEQ", tableParm.getData("OPBOOK_SEQ", row));
		String type = (String) tableParm.getData("TYPE_CODE", row);
		if("外科手术".equals(type)){
			setValue("TYPE_CODE", "1");
		}
		if("介入手术".equals(type)){
			setValue("TYPE_CODE", "2");
		}
	}
	
	/**
	 * 取得Table控件
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}
}
