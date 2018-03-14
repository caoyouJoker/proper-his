package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title:药库批次解锁 </p>
 *
 * <p>Description:药库批次解锁 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangy 2009.4.22
 * @version 1.0
 */

public class IndBatchLockControl extends TControl {

	public IndBatchLockControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始画面数据
		initPage();
	}

	/**
	 * 批次解锁
	 */
	public void onUnLock() {
		TTable table = getTable("TABLE");
		table.acceptText();
		if (table.getDataStore().rowCount() == 0) {
			this.messageBox("没有需要解锁的部门");
			return;
		}
		TDataStore dataStore = table.getDataStore();
		Timestamp date = StringTool.getTimestamp(new Date());
		for (int i = 0; i < table.getRowCount(); i++) {
			if ("Y".equals(table.getItemString(i, 0))) {
				dataStore.setItem(i, "BATCH_FLG", "N");
				dataStore.setItem(i, "OPT_USER", Operator.getID());
				dataStore.setItem(i, "OPT_DATE", date);
				dataStore.setItem(i, "OPT_TERM", Operator.getIP());
			} else {
				dataStore.setItem(i, "BATCH_FLG", "Y");
			}
		}
		if (!table.update()) {
			messageBox("解锁失败");
			return;
		}
		messageBox("解锁成功");
		initPage();
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		TTable table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(INDSQL.getINDBatchLockORG("Y"));
		dataStroe.retrieve();
		if (dataStroe.rowCount() == 0) {
			this.messageBox("没有需要解锁的部门");
			return;
		}
		table.setDataStore(dataStroe);
		table.setDSValue();
	}

	/**
	 * 全选(CheckBox)改变事件
	 */
	public void onSelectAll() {
		TTable table = getTable("TABLE");
		if (table.getDataStore().rowCount() == 0)
			return;
		String flg = getCheckBox("SELECT_ALL").getValue();
		if ("Y".equals(flg)) {
			for (int i = 0; i < table.getDataStore().rowCount(); i++) {
				table.getDataStore().setItem(i, "BATCH_FLG", "Y");
			}
		} else {
			for (int i = 0; i < table.getDataStore().rowCount(); i++) {
				table.getDataStore().setItem(i, "BATCH_FLG", "N");
			}
		}
		table.acceptText();
		table.setDSValue();
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
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
}
