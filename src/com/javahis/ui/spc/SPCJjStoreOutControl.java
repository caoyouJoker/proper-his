package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title:紧急抢救麻精药出智能柜
 * </p>
 * 
 * <p>
 * Description:紧急抢救麻精药出智能柜
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */
public class SPCJjStoreOutControl extends TControl {

	private TTable TABLE_M;
	
	private TTable TABLE_N;

	private String GUARD_IP = ""; // 智能柜门禁IP

	private String CABINET_ID = ""; // 智能柜ID

	private String ip = ""; // 智能柜IP

	String dispense_no = ""; // 虚拟出库单号

	TPanel PANEL_N;
	TPanel PANEL_Y;

	/**
	 * 构造器
	 */
	public SPCJjStoreOutControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		TABLE_M = getTable("TABLE_M");
		TABLE_N = getTable("TABLE_N");
		PANEL_N = (TPanel) getComponent("PANEL_N");
		PANEL_Y = (TPanel) getComponent("PANEL_Y");
		this.initPage();
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {

		dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_DISPENSE", "No");
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if ("".equals(ip)) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CABINET_IP", ip);
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);
		this.setValue("CABINET_ID", result.getData("CABINET_ID", 0));
		this.setValue("CABINET_DESC", result.getData("CABINET_DESC", 0));
		this.setValue("ORG_CHN_DESC", result.getData("ORG_CHN_DESC", 0));
		GUARD_IP = (String) result.getData("GUARD_IP", 0);
		CABINET_ID = (String) result.getData("CABINET_ID", 0);
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 扫描条码
	 */
	public void onClick() {
		// modify by wangbin 2015/01/26 内部需求 #793 病区管理 > 紧急抢救出智能柜 相关调整 START
		TParm tparm = new TParm();
		String barCode = this.getValueString("BAR_CODE").trim();
		TParm result = SPCContainerTool.getInstance().queryBarCodeSize();
		int length = result.getInt("TOXIC_LENGTH", 0);
//		if (barCode.length() == length) {
			if (PANEL_N.isShowing()) {
				tparm.setData("TOXIC_ID", barCode);
				tparm.setData("CABINET_ID", CABINET_ID);
				result = SPCContainerTool.getInstance().queryJjMj(tparm);
				if (result.getErrCode() < 0) {
					this.messageBox("获取药品失败");
					this.setValue("BAR_CODE", "");
					return;
				}
				
				if (result.getCount() <= 0) {
					this.messageBox("查无该麻精条码号");
					this.setValue("BAR_CODE", "");
					return;
				}
				
				for (int i = 0; i < TABLE_M.getRowCount(); i++) {
					String toxic = TABLE_M.getItemString(i, "TOXIC_ID");
					if (barCode.equals(toxic)) {
						this.messageBox("此麻精药已经扫码完毕");
						this.setValue("BAR_CODE", "");
						return;
					}
				}
				TABLE_M.addRow(result.getRow(0));
				this.setValue("BAR_CODE", "");
			} else if (PANEL_Y.isShowing()) {
				tparm.setData("TOXIC_ID", barCode);
				tparm.setData("CABINET_ID", CABINET_ID);
				result = SPCContainerTool.getInstance().queryToxicD(tparm);
				if (result.getErrCode() < 0 || result.getCount() <= 0) {
					this.messageBox("无急救药品");
					this.setValue("BAR_CODE", "");
					return;
				}
				TABLE_N.setParmValue(result);
			}
			/*} else {
			if (PANEL_N.isShowing()) {
				tparm.setData("CONTAINER_ID", barCode);
				result = SPCContainerTool.getInstance().queryJjContainer(tparm);
				if (result.getErrCode() < 0 || result.getCount() <= 0) {
					this.messageBox("获取药品失败");
					return;
				}
				for (int i = 0; i < result.getCount(); i++) {
					String toxicId = String.valueOf(result.getData("TOXIC_ID",
							i));
					boolean flg = false;
					for (int j = 0; j < TABLE_M.getRowCount(); j++) {
						String toxic = TABLE_M.getItemString(j, "TOXIC_ID");
						if (toxicId.equals(toxic)) {
							flg = true;
							continue;
						}
					}
					if (!flg)
						TABLE_N.addRow(result.getRow(i));
				}
			} else if (PANEL_Y.isShowing()) {
				tparm.setData("CONTAINER_ID", barCode);
				result = SPCContainerTool.getInstance().queryToxicD(tparm);
				if (result.getErrCode() < 0 || result.getCount() <= 0) {
					this.messageBox("无急救药品");
					this.setValue("BAR_CODE", "");
					return;				
				}
				TABLE_N.setParmValue(result);
			}

		}*/
		// modify by wangbin 2015/01/26 内部需求 #793 病区管理 > 紧急抢救出智能柜 相关调整 START
	}

	/**
	 * 保存药品出库
	 */
	public void onSave() {
		TParm parm = new TParm();
		parm.setData("CABINET_IP", ip);
		TParm result = SPCInStoreTool.getInstance().queryCabinet(parm);
		String orgCode = (String) result.getData("ORG_CODE", 0);
		List list = TABLE_M.getValue();
		Set set = new HashSet();
		for (int j = 0; j < TABLE_M.getRowCount(); j++) {
			String containerId = (String) TABLE_M
					.getItemData(j, "CONTAINER_ID");
			set.add(containerId);
		}
		parm.setData("ORG_CODE", orgCode);
		parm.setData("list", list);
		parm.setData("set", set);
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("CABINET_ID", CABINET_ID);
		result = TIOM_AppServer.executeAction("action.spc.SPCJjStoreOutAction",
				"JjStockOut", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("取药失败");
			return;
		}
		this.messageBox("药品取出成功");
		TABLE_M.removeRowAll();
	}

	/**
	 * 清空
	 */
	public void onClear() {
		TABLE_M.removeRowAll();
	}

	/**
	 * 调用智能柜查询页面
	 */
	public void onOpen() {
		TParm parm = new TParm();
		parm.setData("CABINET_ID", CABINET_ID);
		Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x",
				parm);
	}

}
