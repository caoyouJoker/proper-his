package com.javahis.ui.sys;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TSystem;

/**
 * 
 * <p>
 * Title: SYS_DIAGNOSIS 下拉选择框
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author EHUI 2009.1.16
 * @version 1.0
 */
public class SYSICDPopupControl extends TControl {
	private String oldText = "";
	private String icdType;
	private String icdStart;
	private String icdEnd;
	private TTable table;
	private String icdMin;
	private String icdMax;
	private String icdStartEx;
	private String icdEndEx;
	private String icdMinEx;
	private String icdMaxEx;
	private String icdExclude;
	private int page = 0;
	private int index = 0;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) callFunction("UI|TABLE|getThis");
		callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED,
				this, "onKeyReleased");
		callFunction("UI|EDIT|addEventListener", "EDIT->"
				+ TKeyListener.KEY_PRESSED, this, "onKeyPressed");
		table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
				"onDoubleClicked");
		// table.getDataStore().showDebug();
		initParamenter();
	}

	/**
	 * 初始化参数
	 */
	public void initParamenter() {
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		String text = parm.getValue("TEXT");
		setEditText(text);
		icdType = parm.getValue("ICD_TYPE");
		icdStart = parm.getValue("ICD_START");
		icdEnd = parm.getValue("ICD_END");
		this.icdMin = parm.getValue("ICD_MIN");
		this.icdMax = parm.getValue("ICD_MAX");
		this.icdExclude=parm.getValue("ICD_EXCLUDE");
		icdStartEx = parm.getValue("ICD_START_EX");
		icdEndEx = parm.getValue("ICD_END_EX");
		this.icdMinEx = parm.getValue("ICD_MIN_EX");
		this.icdMaxEx = parm.getValue("ICD_MAX_EX");
	}

	/**
	 * 重新加载
	 */
	public void onInitReset() {
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		String text = parm.getValue("TEXT");
		String oldText = (String) callFunction("UI|EDIT|getText");
		if (oldText.equals(text))
			return;
		setEditText(text);
	}

	/**
	 * 设置输入文字
	 * 
	 * @param s
	 *            String
	 */
	public void setEditText(String s) {
		page = 0;
		index = 0;
		setValue("L_PAGE", "" + (page + 1));

		callFunction("UI|EDIT|setText", s);
		int x = s.length();
		callFunction("UI|EDIT|select", x, x);
		onKeyReleased(s);
	}

	/**
	 * 按键事件
	 * 
	 * @param s
	 *            String
	 */
	public void onKeyReleased(String s) {
		page = 0;
		index = 0;
		setValue("L_PAGE", "" + (page + 1));

		s = s.toUpperCase();
		if (oldText.equals(s))
			return;
		oldText = s.toUpperCase();
		table.filterObject(this, "filter");
		int count = table.getRowCount();
		if (count > 0)
			table.setSelectedRow(0);
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filter(TParm parm, int row) {
		boolean result = false;
		// startsWith
		if (!"en".equals((String) TSystem.getObject("Language"))) {
			result = parm.getValue("ICD_CODE", row).toUpperCase().startsWith(
					oldText)
					|| parm.getValue("ICD_CHN_DESC", row).toUpperCase()
							.indexOf(oldText) != -1
					|| parm.getValue("PY1", row).toUpperCase().indexOf(oldText) != -1
					|| parm.getValue("PY2", row).toUpperCase().indexOf(oldText) != -1
					|| parm.getValue("DESCRIPTION", row).toUpperCase().indexOf(
							oldText) != -1;
		} else {
			result = parm.getValue("ICD_CODE", row).toUpperCase().startsWith(
					oldText)
					|| parm.getValue("ICD_ENG_DESC", row).toUpperCase()
							.indexOf(oldText) != -1
					|| parm.getValue("PY1", row).toUpperCase().indexOf(oldText) != -1
					|| parm.getValue("PY2", row).toUpperCase().indexOf(oldText) != -1
					|| parm.getValue("DESCRIPTION", row).toUpperCase().indexOf(
							oldText) != -1;
		}

		if (!StringUtil.isNullString(icdType)) {
			result = result
					&& icdType.equalsIgnoreCase(parm.getValue("ICD_TYPE", row));
		}
		if (!StringUtil.isNullString(icdStart)) {
			result = result
					&& parm.getValue("ICD_CODE", row).substring(0, 1)
							.compareTo(icdStart) >= 0;
		}
		if (!StringUtil.isNullString(icdEnd))
			result = result
					&& parm.getValue("ICD_CODE", row).substring(0, 1)
							.compareTo(icdEnd) <= 0;
		if (!StringUtil.isNullString(icdMin))
			result = result
					&& parm.getValue("ICD_CODE", row).compareTo(icdMin) >= 0
					&& parm.getValue("ICD_CODE", row).length() == icdMin
							.length();
		if (!StringUtil.isNullString(icdMax))
			result = result
					&& parm.getValue("ICD_CODE", row).compareTo(icdMax) <= 0
					&& parm.getValue("ICD_CODE", row).length() == icdMax
							.length();
		
		if(!StringUtil.isNullString(this.icdExclude)){
			if(icdExclude.equals("Y")){
				if (!StringUtil.isNullString(icdStartEx)&&!StringUtil.isNullString(icdEndEx)) {
					result = result
							&& !(parm.getValue("ICD_CODE", row).substring(0, 1)
									.compareTo(icdStartEx) >= 0&&parm.getValue("ICD_CODE", row).substring(0, 1)
									.compareTo(icdEndEx) <= 0);
				}
				if (!StringUtil.isNullString(icdMinEx)&&!StringUtil.isNullString(icdMaxEx)&&parm.getValue("ICD_CODE", row).indexOf("/")>-1)
					result = result
							&& !(parm.getValue("ICD_CODE", row).compareTo(icdMinEx) >= 0
							&& parm.getValue("ICD_CODE", row).compareTo(icdMaxEx) <= 0);
			}
			
		}
		if (result) {
			index++;
			if (index < (page) * 19 || index > (page + 1) * 19)
				return false;
		}
		return result;
	}

	/**
	 * 按键事件
	 * 
	 * @param e
	 *            KeyEvent
	 */
	public void onKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			callFunction("UI|setVisible", false);
			return;
		}
		int count = (Integer) callFunction("UI|TABLE|getRowCount");
		if (count <= 0)
			return;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			int row = (Integer) callFunction("UI|TABLE|getSelectedRow") - 1;
			if (row < 0)
				row = 0;
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			// callFunction("UI|TABLE|setSelectedRow",row);
			break;
		case KeyEvent.VK_DOWN:
			row = (Integer) callFunction("UI|TABLE|getSelectedRow") + 1;
			if (row >= count)
				row = count - 1;
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			// callFunction("UI|TABLE|setSelectedRow",row);
			break;
		case KeyEvent.VK_ENTER:
			callFunction("UI|setVisible", false);
			onSelected();
			break;
		}
	}

	/**
	 * 行双击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onDoubleClicked(int row) {
		if (row < 0)
			return;
		callFunction("UI|setVisible", false);
		onSelected();
	}

	/**
	 * 选中
	 */
	public void onSelected() {
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0)
			return;
		TDataStore dataStore = (TDataStore) callFunction("UI|TABLE|getDataStore");
		String idCode = dataStore.getItemString(row, "ICD_CODE");
		String sql = "SELECT ICD_TYPE, ICD_CODE, ICD_CHN_DESC, "
				+ " ICD_ENG_DESC, PY1, PY2, "
				+ " SEQ, DESCRIPTION, SYNDROME_FLG, "
				+ " MDC_CODE, CCMD_CODE, MAIN_DIAG_FLG, "
				+ " CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
				+ " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, "
				+ " START_AGE, LIMIT_DEPT_CODE, LIMIT_SEX_CODE,  "
				+ " END_AGE, AVERAGE_FEE,OPT_USER, "
				+ " OPT_DATE, OPT_TERM FROM SYS_DIAGNOSIS WHERE ICD_CODE='"
				+ idCode + "' ORDER BY ICD_CODE, SEQ ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0 || parm.getCount() <= 0)
			return;
		parm = parm.getRow(0);
		setReturnValue(parm);
	}

	/**
	 * 更新本地
	 */
	public void onResetDW() {
		// String sql = "SELECT ICD_TYPE, ICD_CODE, ICD_CHN_DESC, "
		// + " ICD_ENG_DESC, PY1, PY2, "
		// + " SEQ, DESCRIPTION, SYNDROME_FLG, "
		// + " MDC_CODE, CCMD_CODE, MAIN_DIAG_FLG, "
		// + " CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
		// + " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, "
		// + " START_AGE, LIMIT_DEPT_CODE, LIMIT_SEX_CODE,  "
		// + " END_AGE, AVERAGE_FEE, OPT_USER, "
		// + " OPT_DATE, OPT_TERM FROM SYS_DIAGNOSIS ORDER BY ICD_CODE, SEQ ";
		// table.getDataStore().setSQL(sql);
		TIOM_Database.removeLocalTable("SYS_DIAGNOSIS", false,
				" ORDER BY SEQ, ICD_CODE");
		table.retrieve();
	}

	/**
	 * 重新下载全部
	 */
	public void onResetFile() {
		// String sql = "SELECT ICD_TYPE, ICD_CODE, ICD_CHN_DESC, "
		// + " ICD_ENG_DESC, PY1, PY2, "
		// + " SEQ, DESCRIPTION, SYNDROME_FLG, "
		// + " MDC_CODE, CCMD_CODE, MAIN_DIAG_FLG, "
		// + " CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
		// + " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, "
		// + " START_AGE, LIMIT_DEPT_CODE, LIMIT_SEX_CODE,  "
		// + " END_AGE, AVERAGE_FEE, OPT_USER, "
		// + " OPT_DATE, OPT_TERM FROM SYS_DIAGNOSIS ORDER BY ICD_CODE, SEQ ";
		// table.getDataStore().setSQL(sql);
		TIOM_Database.removeLocalTable("SYS_DIAGNOSIS", true,
				" ORDER BY SEQ, ICD_CODE");
		table.retrieve();
	}

	/**
	 * 向上翻页
	 */
	public void onUp() {
		page--;
		index = 0;
		if (page < 0) {
			page = 0;
			return;
		}
		setValue("L_PAGE", "" + (page + 1));
		table.filterObject(this, "filter");
		int count = table.getRowCount();
		if (count > 0)
			table.setSelectedRow(0);
	}

	/**
	 * 向下翻页
	 */
	public void onDown() {
		page++;
		index = 0;
		setValue("L_PAGE", "" + (page + 1));
		table.filterObject(this, "filter");
		int count = table.getRowCount();
		if (count > 0)
			table.setSelectedRow(0);
	}

}
