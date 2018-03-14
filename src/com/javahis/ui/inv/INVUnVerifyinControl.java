package com.javahis.ui.inv;

import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import jdo.inv.InvPurorderMTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 引用订单Control
 * </p>
 * 
 * <p>
 * Description: 引用订单Control
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */
public class INVUnVerifyinControl extends TControl {

	// 返回数据集合
	private TParm resultParm;

	private TParm parm;

	public INVUnVerifyinControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 取得传入参数
		Object obj = getParameter();
		if (obj != null) {
			parm = (TParm) obj;
		}

		// 初始画面数据
		initPage();
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		// 初始化订购单ComboBox
		TParm parmNo = new TParm(TJDODBTool.getInstance().select(
				INVSQL.getInvPurOrderNo(parm.getValue("ORG_CODE"))));

		parmNo = this.changeTParm(parmNo);

		getComboBox("PURORDER_NO").setParmValue(parmNo);

		TParm parmIn = new TParm();
		parmIn.setData("SUP_CODE", "");
		// 设置弹出菜单
		getTextField("INV_CODE").setPopupMenuParameter(
				"UD",
				getConfigParm()
						.newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
				parmIn);
		// 定义接受返回值方法
		getTextField("INV_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE,
				this, "popReturn");

		resultParm = new TParm();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		String clearStr = "PURORDER_NO;INV_CODE;INV_DESC;SELECT_ALL;SUP_CODE";
		this.clearValue(clearStr);
		getTable("TABLE").removeRowAll();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm inParm = new TParm();
		if ("".equals(getValueString("SUP_CODE"))
				&& "".equals(getComboBox("PURORDER_NO").getSelectedID())) {
			this.messageBox("请选择供应厂商和订购单");
			return;
		}
		if (!"".equals(getValueString("SUP_CODE"))
				&& "".equals(getComboBox("PURORDER_NO").getSelectedID())) {
			this.messageBox("请选择订购单");
			return;
		}
		// 供货厂商
		if (!"".equals(getValueString("SUP_CODE"))) {
			inParm.setData("SUP_CODE", getValueString("SUP_CODE"));
		}
		// 订购单号
		if (!"".equals(getComboBox("PURORDER_NO").getSelectedID())) {
			inParm.setData("PURORDER_NO", getComboBox("PURORDER_NO")
					.getSelectedID().substring(
							0,
							getComboBox("PURORDER_NO").getSelectedID()
									.toString().indexOf(" ")));
		}
		// 药品代码
		if (!"".equals(getValueString("ORDER_CODE"))) {
			inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		}
		TParm result = new TParm();
		// fux modify 20140728 INVPurorderMModule.x queryUnDonePurOrder修改为默认选择
		result = InvPurorderMTool.getInstance().onQueryUnDone(inParm);
		if (result.getCount() == 0) {
			this.messageBox("查无资料");
			return;
		}
		this.getTable("TABLE").setParmValue(result);
		getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
		resultParm = result;
	}

	/**
	 * 全选复选框选中事件
	 */
	public void onCheckSelectAll() {
		TTable table = getTable("TABLE");
		table.acceptText();
		if (table.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
		}
	}

	/**
	 * 供应厂商改变事件
	 */
	public void onSupCodeChange() {
		if (!"".equals(this.getValueString("SUP_CODE"))) {
			TParm parmNo = new TParm(TJDODBTool.getInstance().select(
					INVSQL.getInvPurOrderNo(parm.getValue("ORG_CODE"),
							getValueString("SUP_CODE"))));

			parmNo = this.changeTParm(parmNo);

			getComboBox("PURORDER_NO").setParmValue(parmNo);
			getComboBox("PURORDER_NO").setSelectedIndex(-1);
		}
	}

	/**
	 * 订购单改变事件
	 */
	public void onPurOrderChange() {
		if ("".equals(getValueString("SUP_CODE"))) {
			TParm parm = new TParm();
			parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
			TParm result = InvPurorderMTool.getInstance().onQuery(parm);
			getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
		}
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String inv_code = parm.getValue("INV_CODE");
		if (!StringUtil.isNullString(inv_code))
			getTextField("INV_CODE").setValue(inv_code);
		String inv_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(inv_desc))
			getTextField("INV_DESC").setValue(inv_desc);
		// try {
		// if (1!=1) {
		// return ;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("e:"+e);
		// }
	}

	/**
	 * 传回方法
	 */
	public void onReturn() {
		// 是否强制全部传回
		TTable table = getTable("TABLE");
		table.acceptText();
		if (table.getRowCount() < 0) {
			this.messageBox("没有传回数据");
			return;
		}
		TParm result = resultParm;
		for (int i = table.getRowCount() - 1; i >= 0; i--) {
			if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
				result.removeRow(i);
				result.setCount(result.getCount() - 1);
			}
		}
		if (result == null || result.getCount("PURORDER_NO") <= 0) {
			this.messageBox("没有传回数据");
			return;
		}
		// fux modify 20140728
		// for (int i = table.getRowCount() - 1; i >= 0; i--) {
		// if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
		// this.messageBox("请全部勾选传回");
		// return;
		// }
		// }
		String str = getComboBox("PURORDER_NO").getSelectedID().toString();
		String[] tempStr = str.split(" ");
		result.setData("CON_ORG", tempStr[3]);
		System.out.println("tempStr[3]" + tempStr[3]);

		setReturnValue(result);
		// System.out.println("查询数据: " + result);  
		this.closeWindow();
	}

	private TParm changeTParm(TParm tp) {

		for (int i = 0; i < tp.getCount("PURORDER_NO"); i++) {
			tp.setData("PURORDER_NO", i, tp.getValue("PURORDER_NO", i) + " "
					+ tp.getValue("ORG_DESC", i) + " "
					+ tp.getValue("PURORDER_DATE", i).subSequence(0, 10) + " "
					+ tp.getValue("CON_ORG", i));
			// tp.setData("PURORDER_NO", i, tp.getValue("PURORDER_NO", i) + " "
			// + tp.getValue("ORG_DESC", i) + " " + tp.getValue("PURORDER_DATE",
			// i).toString().substring(0, 10).replace("-", "/") );
		}

		return tp;
	}

	/**
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
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
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

}
