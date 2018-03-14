package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCRecoverTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: 麻精空瓶回收Control
 * </p>
 * 
 * <p>
 * Description: 麻精空瓶回收Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author fuwj 2013.05.12
 * @version 1.0
 */

public class SPCRecoverControl extends TControl {
														
	TPanel PANEL_N;
	TPanel PANEL_Y;
	TTable TABLE_M_N;								
	TTable TABLE_M_Y;
	String ip="";
	String orgCode = "";
															
	/**
	 * 构造器
	 */
	public SPCRecoverControl() {
		super();
	}
	
	/**
	 * 初始化参数
	 */
	public void onInit() {
		TABLE_M_N = getTable("TABLE_M_N");
		TABLE_M_Y = getTable("TABLE_M_Y");
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if ("".equals(ip)) {
			this.messageBox("获取智能柜信息失败");
			return;
		}
		TParm parm = new TParm();   
		parm.setData("CABINET_IP", ip);  
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);	
	    orgCode = (String) result.getData("ORG_CODE",0);
		onQuery();
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		parm.setData("IS_RECOVER","N");      							
		if (PANEL_N.isShowing()) {		
			 parm.setData("IS_RECOVER","N");      
		} else if (PANEL_Y.isShowing()) {
			 parm.setData("IS_RECOVER","Y");			
		}
		//String toxicId = this.getValueString("TOXIC_ID");
		parm.setData("ORG_CODE",orgCode);	
		TParm result = SPCRecoverTool.getInstance().getRecover(parm);
		if (result.getCount() <= 0) {
			this.messageBox("没有查询到数据");
			if (PANEL_N.isShowing()) {					
				TABLE_M_N.setParmValue(new TParm());
			}
			if (PANEL_Y.isShowing()) {
				TABLE_M_Y.setParmValue(new TParm());
			}
			return;
		}
		String flg = parm.getValue("IS_RECOVER");		
		if("N".equals(flg)) {		
			TABLE_M_N.setParmValue(result);
		}else if("Y".equals(flg)) {
			TABLE_M_Y.setParmValue(result);
		}
		/*if (PANEL_N.isShowing()) {
			TABLE_M_N.setParmValue(result);
		} else if (PANEL_Y.isShowing()) {   
			TABLE_M_Y.setParmValue(result);
		}*/
	}
	  
	/**
	 * 扫描麻精条码号
	 */
	public void onClick() {
		String str  = this.getValueString("TOXIC_ID");
		if (PANEL_N.isShowing()) {
			int rowNum = TABLE_M_N.getRowCount();
			if (rowNum == -1) {
				this.messageBox("麻精条码号错误");
				return;
			}						
			for(int i=0;i<rowNum;i++) {
				String toxicId = TABLE_M_N.getItemString(i, "TOXIC_ID1");			
				if(str.equals(toxicId)) {
					TParm parm = new TParm();
					parm.setData("TOXIC_ID",toxicId);
					String userId = Operator.getID();
					parm.setData("RECLAIM_USER",userId);
					TParm result = TIOM_AppServer.executeAction(
							"action.spc.SPCRecoverAction", "onRecover", parm);
					if (result.getErrCode() < 0) {
						this.messageBox("回收失败");
						return;
					}
					this.messageBox("回收成功");
					this.setValue("TOXIC_ID", "");
					onQuery();
					break;
				}
			}
		}
	}
	
/*	*//**
	 * 保存
	 *//*
	public void onSave() {
		if (PANEL_N.isShowing()) {
			int rowNum = TABLE_M_N.getSelectedRow();
			if (rowNum == -1) {
				this.messageBox("请选择要回收药品");
			}
			String toxicId = TABLE_M_N.getItemString(rowNum, "TOXIC_ID1");
			TParm parm = new TParm();
			parm.setData("TOXIC_ID",toxicId);
			String userId = Operator.getID();
			parm.setData("RECLAIM_USER",userId);
			TParm result = TIOM_AppServer.executeAction(
					"action.spc.SPCRecoverAction", "onRecover", parm);
			if (result.getErrCode() < 0) {
				this.messageBox("回收失败");
				return;
			}
			this.messageBox("回收成功");
			onQuery();
		}
	}*/
	
	/**
	 * 获取table对象
	 * 
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}
	
	/**
	 * 清空方法	
	 */
	public void onClear() {
		TABLE_M_N.removeRowAll();
		TABLE_M_Y.removeRowAll();
		this.setValue("TOXIC_ID", "");
	}
	
}
