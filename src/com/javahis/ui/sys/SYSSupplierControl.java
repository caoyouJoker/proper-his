package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:供应厂商
 * </p>
 *
 * <p>
 * Description:供应厂商
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.08
 * @version 1.0
 */
public class SYSSupplierControl extends TControl {

	private String action = "save";
	// 主项表格
	private TTable table;

	public SYSSupplierControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());
		TDataStore dataStore = table.getDataStore();
		if ("save".equals(action)) {
			TTextField combo = getTextField("SUP_CODE");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				if (!CheckData())
					return;
				row = table.getSelectedRow();
			}
			// 厂商信息
			dataStore.setItem(row, "SUP_CODE", getValueString("SUP_CODE"));
			String desc = getValueString("SUP_CHN_DESC");
			dataStore.setItem(row, "SUP_CHN_DESC", desc);
			desc = getValueString("SUP_ENG_DESC");
			dataStore.setItem(row, "SUP_ENG_DESC", desc);
			desc = getValueString("SUP_ABS_DESC");
			dataStore.setItem(row, "SUP_ABS_DESC", desc);
			dataStore.setItem(row, "PY1", getValueString("PY1"));
			dataStore.setItem(row, "PY2", getValueString("PY2"));
			dataStore.setItem(row, "SEQ", getValueInt("SEQ"));
			desc = getValueString("DESCRIPTION");
			dataStore.setItem(row, "DESCRIPTION", desc);
			desc = getValueString("SELL_DEPT_CODE");
			dataStore.setItem(row, "SELL_DEPT_CODE", getValueString("SELL_DEPT_CODE"));
			// 厂商类别
			dataStore.setItem(row, "PHA_FLG", getValueString("PHA_FLG"));
			dataStore.setItem(row, "MAT_FLG", getValueString("MAT_FLG"));
			dataStore.setItem(row, "DEV_FLG", getValueString("DEV_FLG"));
			dataStore.setItem(row, "OTHER_FLG", getValueString("OTHER_FLG"));
			// 通讯信息
			dataStore.setItem(row, "SUP_BOSSNAME",
					getValueString("SUP_BOSSNAME"));
			dataStore.setItem(row, "SUP_IDNO", getValueString("SUP_IDNO"));
			dataStore.setItem(row, "SUP_TEL", getValueString("SUP_TEL"));
			dataStore.setItem(row, "SUP_FAX", getValueString("SUP_FAX"));
			dataStore.setItem(row, "NATIONAL_CODE",
					getValueString("NATIONAL_CODE"));
			dataStore.setItem(row, "POST_CODE", getValueString("POST_CODE"));
			dataStore.setItem(row, "ADDRESS", getValueString("ADDRESS"));
			dataStore.setItem(row, "E_MAIL", getValueString("E_MAIL"));
			dataStore.setItem(row, "WEBSITE", getValueString("WEBSITE"));
			dataStore.setItem(row, "BANK_CODE", getValueString("BANK_CODE"));
			dataStore.setItem(row, "BANK_IDNO", getValueString("BANK_IDNO"));
			dataStore.setItem(row, "BANK_NAME", getValueString("BANK_NAME"));
			// 业务员
			dataStore.setItem(row, "SUP_SALES1", getValueString("SUP_SALES1"));
			dataStore.setItem(row, "SUP_SALES2", getValueString("SUP_SALES2"));
			dataStore.setItem(row, "SUP_SALES3", getValueString("SUP_SALES3"));
			dataStore.setItem(row, "SUP_SALES1_TEL",
					getValueString("SUP_SALES1_TEL"));
			dataStore.setItem(row, "SUP_SALES2_TEL",
					getValueString("SUP_SALES2_TEL"));
			dataStore.setItem(row, "SUP_SALES3_TEL",
					getValueString("SUP_SALES3_TEL"));
			dataStore.setItem(row, "SUP_SALES1_EMAIL",
					getValueString("SUP_SALES1_EMAIL"));
			dataStore.setItem(row, "SUP_SALES2_EMAIL",
					getValueString("SUP_SALES2_EMAIL"));
			dataStore.setItem(row, "SUP_SALES3_EMAIL",
					getValueString("SUP_SALES3_EMAIL"));
			// 停用
			dataStore.setItem(row, "SUP_STOP_FLG",
					getValueString("SUP_STOP_FLG"));
			dataStore.setItem(row, "SUP_STOP_DATE", getValue("SUP_STOP_DATE"));
			dataStore.setItem(row, "SUP_END_DATE", getValue("SUP_END_DATE"));
			// OPT
			dataStore.setItem(row, "OPT_USER", Operator.getID());
			dataStore.setItem(row, "OPT_DATE", date);
			dataStore.setItem(row, "OPT_TERM", Operator.getIP());
		}
		if (dataStore.isModified()) {
			table.acceptText();
			if (!table.update()) {
				messageBox("E0001");
				table.removeRow(row);
				table.setDSValue();
				onClear();
				return;
			}
			table.setDSValue();
		}
		messageBox("P0001");
		table.setDSValue();
		onClear();
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;
		table.removeRow(row);
		table.setSelectionMode(0);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "delete";
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		// 初始化Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSSupplier());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		String code = getValueString("SUP_CODE");
		String desc = getValueString("SUP_CHN_DESC");
		String filterString = "";
		if (code.length() > 0 && desc.length() > 0)
			filterString += "SUP_CODE like '" + code
					+ "%' AND SUP_CHN_DESC like '" + desc + "%'";
		else if (code.length() > 0)
			filterString += "SUP_CODE like '" + code + "%'";
		else if (desc.length() > 0)
			filterString += "SUP_CHN_DESC like '" + desc + "%'";
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空画面内容
		String clearString = "SUP_CODE;SUP_CHN_DESC;SUP_ENG_DESC;SUP_ABS_DESC;PY1;"
				+ "PY2;SEQ;DESCRIPTION;PHA_FLG;MAT_FLG;"
				+ "DEV_FLG;OTHER_FLG;SUP_BOSSNAME;SUP_IDNO;SUP_TEL;"
				+ "SUP_FAX;NATIONAL_CODE;POST_CODE;ADDRESS;E_MAIL;"
				+ "WEBSITE;BANK_CODE;BANK_IDNO;BANK_NAME;SUP_SALES1;"
				+ "SUP_SALES1_TEL;SUP_SALES1_EMAIL;SUP_SALES2;SUP_SALES2_TEL;SUP_SALES2_EMAIL;"
				+ "SUP_SALES3;SUP_SALES3_TEL;SUP_SALES3_EMAIL;SUP_STOP_FLG;SUP_STOP_DATE;"
				+ "SUP_END_DATE;POST_P;POST_C;SELL_DEPT_CODE";
		clearValue(clearString);
		// 序号
		TDataStore dataStroe = table.getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		table.setSelectionMode(0);
		getTextField("SUP_CODE").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * TABLE单击事件
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "SUP_CODE;SUP_CHN_DESC;SUP_ENG_DESC;SUP_ABS_DESC;PY1;"
					+ "PY2;SEQ;DESCRIPTION;PHA_FLG;MAT_FLG;"
					+ "DEV_FLG;OTHER_FLG;SUP_BOSSNAME;SUP_IDNO;SUP_TEL;"
					+ "SUP_FAX;NATIONAL_CODE;POST_CODE;ADDRESS;E_MAIL;"
					+ "WEBSITE;BANK_CODE;BANK_IDNO;BANK_NAME;SUP_SALES1;"
					+ "SUP_SALES1_TEL;SUP_SALES1_EMAIL;SUP_SALES2;SUP_SALES2_TEL;SUP_SALES2_EMAIL;"
					+ "SUP_SALES3;SUP_SALES3_TEL;SUP_SALES3_EMAIL;SUP_STOP_FLG;SUP_STOP_DATE;"
					+ "SUP_END_DATE;SELL_DEPT_CODE";
			this.setValueForParm(likeNames, parm);
			getTextField("SUP_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
			onSupStopFlgAction();
			onPostClick();
		}
	}

	/**
	 * SupDesc回车事件
	 */
	public void onSupDescAction() {
		String py = TMessage.getPy(this.getValueString("SUP_CHN_DESC"));
		setValue("PY1", py);
		getTextField("SUP_ENG_DESC").grabFocus();
	}

	/**
	 * 通过城市带出邮政编码
	 */
	public void selectCode_1() {
		this.setValue("POST_CODE", this.getValue("POST_C"));
		this.onPost();
	}

	/**
	 * 通信邮编的得到省市
	 */
	public void onPost() {
		String post = getValueString("POST_CODE");
		if (post == null || "".equals(post)) {
			getTextField("ADDRESS").grabFocus();
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("POST_P", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("POST_C", parm.getData("POST_CODE", 0).toString());
		setValue("ADDRESS", parm.getData("STATE", 0).toString()
				+ parm.getData("CITY", 0));
		getTextField("ADDRESS").grabFocus();
	}

	/**
	 * 通信邮编的得到省市
	 */
	public void onPostClick() {
		String post = getValueString("POST_CODE");
		if (post == null || "".equals(post)) {
			return;
		}
		TParm parm = this.getPOST_CODE(post);
		setValue("POST_P", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("POST_C", parm.getData("POST_CODE", 0).toString());
		setValue("ADDRESS", parm.getData("STATE", 0).toString()
				+ parm.getData("CITY", 0));
	}

	/**
	 * 得到省市代码
	 *
	 * @param post
	 *            String
	 * @return TParm
	 */
	public TParm getPOST_CODE(String post) {
		TParm result = SYSPostTool.getInstance().getProvinceCity(post);
		return result;
	}

	/**
	 * 停止采购注记事件
	 */
	public void onSupStopFlgAction() {
		if ("Y".equals(getValueString("SUP_STOP_FLG"))) {
			getTextFormat("SUP_STOP_DATE").setEnabled(true);
			getTextFormat("SUP_END_DATE").setEnabled(true);
		} else {
			this.setValue("SUP_STOP_DATE", null);
			this.setValue("SUP_END_DATE", null);
			getTextFormat("SUP_STOP_DATE").setEnabled(false);
			getTextFormat("SUP_END_DATE").setEnabled(false);
		}
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		// 初始化Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSSupplier());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		// 最大号+1(SEQ)
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
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
	 * 得到TextFormat对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * 得到ComboBox对象
	 *
	 * @param tagName
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 检查数据
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("SUP_CODE"))) {
			this.messageBox("厂商代码不能为空");
			return false;
		}
		if ("".equals(getValueString("SUP_CHN_DESC"))) {
			this.messageBox("厂商名称不能为空");
			return false;
		}
		if (!"".equals(getValueString("E_MAIL"))) {
			if (!StringTool.isEmail(getValueString("E_MAIL"))) {
				this.messageBox("邮箱格式不正确");
				return false;
			}
		}
		if (!"".equals(getValueString("SUP_SALES1_EMAIL"))) {
			if (!StringTool.isEmail(getValueString("SUP_SALES1_EMAIL"))) {
				this.messageBox("业务员一的邮箱格式不正确");
				return false;
			}
		}
		if (!"".equals(getValueString("SUP_SALES2_EMAIL"))) {
			if (!StringTool.isEmail(getValueString("SUP_SALES2_EMAIL"))) {
				this.messageBox("业务员二的邮箱格式不正确");
				return false;
			}
		}
		if (!"".equals(getValueString("SUP_SALES3_EMAIL"))) {
			if (!StringTool.isEmail(getValueString("SUP_SALES3_EMAIL"))) {
				this.messageBox("业务员三的邮箱格式不正确");
				return false;
			}
		}
                /*if (!"".equals(getValueString("BANK_CODE"))) {
                 if ("".equals(getValueString("BANK_IDNO"))) {
                  this.messageBox("汇款帐号不能为空");
                  return false;
                 }
                 if ("".equals(getValueString("BANK_NAME"))) {
                  this.messageBox("汇款姓名不能为空");
                  return false;
                 }
                   }*/
		if (!"N".equals(getValueString("SUP_STOP_FLG"))) {
			if ("".equals(getValueString("SUP_STOP_DATE"))) {
				this.messageBox("停止采购开始日期不能为空");
				return false;
			}
			if ("".equals(getValueString("SUP_END_DATE"))) {
				this.messageBox("停止采购截止日期不能为空");
				return false;
			}
			Timestamp start = (Timestamp) getTextFormat("SUP_STOP_DATE")
					.getValue();
			Timestamp end = (Timestamp) getTextFormat("SUP_END_DATE")
					.getValue();
			if (start.compareTo(end) > 0) {
				this.messageBox("开始日期不能早于截止日期");
				return false;
			}
		}
		return true;
	}

	/**
	 * 得到最大的编号 +1
	 *
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName,
			String dbBuffer) {
		if (dataStore == null)
			return 0;
		// 保存数据量
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// 保存最大号
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// 保存最大值
			if (max < value) {
				max = value;
				continue;
			}
		}
		// 最大号加1
		max++;
		return max;
	}

}
