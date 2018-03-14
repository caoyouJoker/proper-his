package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;

import java.awt.event.KeyEvent;

/**
 * <p>
 * Title: 临床路径项目
 * </p>
 * 
 * <p>
 * Description: 临床路径项目
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 2011-05-03
 * @version 1.0
 */
public class CLPOrdTypePopupControl extends TControl {
	private String oldText = "";
	private TTable table;
	private TParm dataParm;

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 查询SQL
	 */
	private String SQL = "";

	/**
	 * 前台传入数据
	 */
	private TParm tableData;

	/**
	 * 调用传入的参数
	 */
	public CLPOrdTypePopupControl() {
	}

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
		initParamenter();
		// 初始化数据
		onResetDW();
	}

	/**
	 * 更新本地
	 */
	public void onResetDW() {
		dataParm = new TParm(this.getDBTool().select(SQL));
		table.setParmValue(dataParm, table.getParmMap());
		
		// table.retrieve();
	}

	/**
	 * 初始化参数
	 */
	public void initParamenter() {
		Object obj = getParameter();
		setSQL(obj);
		if (obj == null) {
			return;
		}
		if (!(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj;
		String text = parm.getValue("TEXT");
		setEditText(text);
	}

	/**
	 * 字符串非空验证
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputString(String str) {
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}

	public void setSQL(Object obj) {
		String orderFlg = null;
		String regionCode = null;
		if (obj == null) {
			orderFlg = null;
		} else if (!(obj instanceof TParm)) {
			orderFlg = null;
		} else {
			TParm parm = (TParm) obj;
			orderFlg = parm.getValue("ORDER_FLG");
			regionCode = parm.getValue("REGION_CODE");
		}
		if (checkInputString(orderFlg)) {
			if ("Y".equals(orderFlg)) {
				// String sql = " SELECT A.TYPE_CODE AS ORDTYPE_CODE,"
				String sql = " SELECT A.TYPE_CODE  ,"
						+ " A.TYPE_CHN_DESC,"
						+ " A.TYPE_ENG_DESC,"
						+ " A.PY1,"
						+ " A.PY2,"
						//==============================modify by caowl 20120706 start================================
						//+ " B.ORDER_CODE, B.ORDER_DESC AS ORDER_CHN_DESC, B.PY1, B.PY2, B.SEQ, B.DESCRIPTION, B.TRADE_ENG_DESC AS ORDER_ENG_DESC, B.GOODS_DESC, B.GOODS_PYCODE, B.ALIAS_DESC, B.ALIAS_PYCODE, B.SPECIFICATION, B.NHI_FEE_DESC, B.HABITAT_TYPE, B.MAN_CODE, B.HYGIENE_TRADE_CODE, B.ORDER_CAT1_CODE, B.CHARGE_HOSP_CODE, B.OWN_PRICE, B.NHI_PRICE, B.GOV_PRICE, B.UNIT_CODE, B.LET_KEYIN_FLG, B.DISCOUNT_FLG, B.EXPENSIVE_FLG, B.OPD_FIT_FLG, B.EMG_FIT_FLG, B.IPD_FIT_FLG, B.HRM_FIT_FLG, B.DR_ORDER_FLG, B.INTV_ORDER_FLG, B.LCS_CLASS_CODE, B.TRANS_OUT_FLG, B.TRANS_HOSP_CODE, B.USEDEPT_CODE, B.EXEC_ORDER_FLG, B.EXEC_DEPT_CODE, B.INSPAY_TYPE, B.ADDPAY_RATE, B.ADDPAY_AMT, B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I, B.CTRL_FLG, B.CLPGROUP_CODE, B.ORDERSET_FLG, B.INDV_FLG, B.SUB_SYSTEM_CODE, B.RPTTYPE_CODE, B.DEV_CODE, B.OPTITEM_CODE, B.MR_CODE, B.DEGREE_CODE, B.CIS_FLG, B.OPT_USER, B.OPT_DATE, B.OPT_TERM, B.CAT1_TYPE"
						+ " B.ORDER_CODE, B.ORDER_DESC AS ORDER_CHN_DESC, B.SEQ, B.DESCRIPTION, B.TRADE_ENG_DESC AS ORDER_ENG_DESC, B.GOODS_DESC, B.GOODS_PYCODE, B.ALIAS_DESC, B.ALIAS_PYCODE, B.SPECIFICATION, B.NHI_FEE_DESC, B.HABITAT_TYPE, B.MAN_CODE, B.HYGIENE_TRADE_CODE, B.ORDER_CAT1_CODE, B.CHARGE_HOSP_CODE, B.OWN_PRICE, B.NHI_PRICE, B.GOV_PRICE, B.UNIT_CODE, B.LET_KEYIN_FLG, B.DISCOUNT_FLG, B.EXPENSIVE_FLG, B.OPD_FIT_FLG, B.EMG_FIT_FLG, B.IPD_FIT_FLG, B.HRM_FIT_FLG, B.DR_ORDER_FLG, B.INTV_ORDER_FLG, B.LCS_CLASS_CODE, B.TRANS_OUT_FLG, B.TRANS_HOSP_CODE, B.USEDEPT_CODE, B.EXEC_ORDER_FLG, B.EXEC_DEPT_CODE, B.INSPAY_TYPE, B.ADDPAY_RATE, B.ADDPAY_AMT, B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I, B.CTRL_FLG, B.CLPGROUP_CODE, B.ORDERSET_FLG, B.INDV_FLG, B.SUB_SYSTEM_CODE, B.RPTTYPE_CODE, B.DEV_CODE, B.OPTITEM_CODE, B.MR_CODE, B.DEGREE_CODE, B.CIS_FLG, B.OPT_USER, B.OPT_DATE, B.OPT_TERM, B.CAT1_TYPE"						
						//==============================modify by caowl 20120706 end================================
						
						+ " FROM CLP_ORDTYPE A,"
						+ " ( SELECT CO.ORDTYPE_CODE, SF.*"
						+ " FROM CLP_ORDERTYPE CO, SYS_FEE SF"
						+ " WHERE CO.ORDER_CODE = SF.ORDER_CODE"
						+ " AND CO.DEFAULT_FLG = 'Y'"
						+ " AND CO.ORDER_FLG = 'Y'"
						+ " ) B WHERE A.TYPE_CODE = B.ORDTYPE_CODE(+) AND (A.REGION_CODE='"
						+ regionCode
						+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE ='') "
						+ " ORDER BY A.SEQ";
				this.SQL = sql;

				System.out.println(sql);
			}
			if ("N".equals(orderFlg)) {
				// String sql = " SELECT A.TYPE_CODE AS ORDTYPE_CODE,"
				String sql = " SELECT A.TYPE_CODE  ,"
						+ " A.TYPE_CHN_DESC,"
						+ " A.TYPE_ENG_DESC,"
						+ " A.PY1,"
						+ " A.PY2,"
						//==============================modify by caowl 20120706 start================================
						//+ " B.CHKTYPE_CODE, B.CHKITEM_CODE AS ORDER_CODE, B.REGION_CODE, B.CHKITEM_CHN_DESC AS ORDER_CHN_DESC, B.CHKITEM_ENG_DESC AS ORDER_CHN_DESC, B.PY1, B.PY2, B.SEQ, B.CLNCPATH_CODE, B.CHKUSER_CODE, B.INDV_FLG, B.DESCRIPTION, B.OPT_USER, B.OPT_DATE, B.OPT_TERM"
						+ " B.CHKTYPE_CODE, B.CHKITEM_CODE AS ORDER_CODE, B.REGION_CODE, B.CHKITEM_CHN_DESC AS ORDER_CHN_DESC, B.CHKITEM_ENG_DESC AS ORDER_CHN_DESC,B.SEQ, B.CLNCPATH_CODE, B.CHKUSER_CODE, B.INDV_FLG, B.DESCRIPTION, B.OPT_USER, B.OPT_DATE, B.OPT_TERM"
						//==============================modify by caowl 20120706 end================================
						+ " FROM CLP_ORDTYPE A,"
						+ " ( SELECT CO.ORDTYPE_CODE, CC.*"
						+ " FROM CLP_ORDERTYPE CO, CLP_CHKITEM CC"
						+ " WHERE CO.ORDER_CODE = CC.CHKITEM_CODE"
						+ " AND CO.DEFAULT_FLG = 'Y'"
						+ " AND CO.ORDER_FLG = 'N'"
						+ " ) B WHERE A.TYPE_CODE = B.ORDTYPE_CODE(+) AND (A.REGION_CODE='"
						+ regionCode
						+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE ='') "
						+ " ORDER BY A.SEQ";
				this.SQL = sql;
			}
			if ("O".equals(orderFlg)) {
				// String sql = " SELECT A.TYPE_CODE AS ORDTYPE_CODE,"
				String sql = " SELECT A.TYPE_CODE ,"
						+ " A.TYPE_CHN_DESC,"
						+ " A.TYPE_ENG_DESC,"
						+ " A.PY1,"
						+ " A.PY2,"
						//==============================modify by caowl 20120706 start================================
						//+ " B.ORDER_CODE, B.REGION_CODE, B.ORDER_CHN_DESC, B.ORDER_ENG_DESC, B.PY1, B.PY2, B.UNIT, B.FREQ, B.AMOUNT, B.TYPE_CODE, B.DESCRIPTION, B.DEL_FLG, B.CHKTYPE_CODE, B.SEQ, B.OPT_USER, B.OPT_DATE, B.OPT_TERM"
						+ " B.ORDER_CODE, B.REGION_CODE, B.ORDER_CHN_DESC, B.ORDER_ENG_DESC, B.UNIT, B.FREQ, B.AMOUNT, B.TYPE_CODE, B.DESCRIPTION, B.DEL_FLG, B.CHKTYPE_CODE, B.SEQ, B.OPT_USER, B.OPT_DATE, B.OPT_TERM"
						//==============================modify by caowl 20120706 end================================
						+ " FROM CLP_ORDTYPE A,"
						+ " ( SELECT CO.ORDTYPE_CODE, CN.*"
						+ " FROM CLP_ORDERTYPE CO, CLP_NURSORDER CN"
						+ " WHERE CO.ORDER_CODE = CN.ORDER_CODE"
						+ " AND CO.DEFAULT_FLG = 'Y'"
						+ " AND CO.ORDER_FLG = 'O'"
						+ " ) B WHERE A.TYPE_CODE = B.ORDTYPE_CODE(+) AND (A.REGION_CODE='"
						+ regionCode
						+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE ='') "
						+ " ORDER BY A.SEQ";
				this.SQL = sql;
			}
		}
	}

	/**
	 * 重新加载
	 */
	public void onInitReset() {
		Object obj = getParameter();
		if (obj == null) {
			return;
		}
		if (!(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj;
		String text = parm.getValue("TEXT");
		String oldText = (String) callFunction("UI|EDIT|getText");

		if (oldText.equals(text)) {
			return;
		}
		setEditText(text);

	}

	/**
	 * 设置输入文字
	 * 
	 * @param s
	 *            String
	 */
	public void setEditText(String s) {
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
		s = s.toUpperCase();
		System.out.println("SSSS::::" + s);
		if (oldText.equals(s)) {
			return;
		}
		oldText = s;
		// ===========================modify by caowl 20120706 start===============================
		int count = dataParm.getCount("TYPE_CODE");
		String names[] = dataParm.getNames();
		TParm temp = new TParm();
		System.out.println(""+dataParm);
		for (int i = 0; i < count; i++) {
			TParm rowParm = dataParm.getRow(i);			
			if (this.filter(rowParm)) {
				for (String tempData : names) {
					temp.addData(tempData, rowParm.getData(tempData));

				}
			}
		}
		table.setParmValue(temp);
		int rowCount = temp.getCount("TYPE_CODE");
		if (rowCount > 0) {
			table.setSelectedRow(0);
		}
		// oldText = s;
		// table.filterObject(this, "filter");

		// ==============================modify by caowl 20120706 end==============================
		

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
	public boolean filter(TParm parm) {
		// boolean falg = parm.getValue("TYPE_CODE",row).toUpperCase().indexOf(
		// oldText) != -1
		// || parm.getValue("TYPE_CHN_DESC",row).toUpperCase()
		// .indexOf(oldText) != -1
		// || parm.getValue("PY1",row).toUpperCase().indexOf(oldText) != -1
		// || parm.getValue("TYPE_ENG_DESC",row).toUpperCase()
		// .indexOf(oldText) != -1;
		// return falg;

		// =====================modify by caowl 20120706 start==========================
		boolean falg = parm.getValue("TYPE_CODE").toUpperCase()
				.contains(oldText)
				|| parm.getValue("TYPE_CHN_DESC").toUpperCase()
						.contains(oldText)
				|| parm.getValue("PY1").toUpperCase().contains(oldText.toUpperCase())
				|| parm.getValue("TYPE_ENG_DESC").toUpperCase()
						.contains(oldText)
				|| parm.getValue("PY2").toUpperCase().contains(oldText.toUpperCase());

		return falg;

		// =======================modify by caowl 20120706 end===========================
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
		int count = table.getRowCount();
		if (count <= 0) {
			return;
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			int row = table.getSelectedRow() - 1;
			if (row < 0) {
				row = 0;
			}
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			break;
		case KeyEvent.VK_DOWN:
			row = table.getSelectedRow() + 1;
			if (row >= count) {
				row = count - 1;
			}
			table.getTable().grabFocus();
			table.setSelectedRow(row);
			break;
		case KeyEvent.VK_ENTER:
			callFunction("UI|setVisible", false);
			onSelected();
			break;
		}

	}

	/**
	 * 选中
	 */
	public void onSelected() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		System.out.println("选中的是" + row + "行");
		setReturnValue(table.getParmValue().getRow(row));

	}

	/**
	 * 重新下载全部
	 */
	public void onResetFile() {
		this.setValue("EDIT", "");
		TParm parm = new TParm(this.getDBTool().select(SQL));

		table.setParmValue(parm, table.getParmMap());
	}

	/**
	 * 行双击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onDoubleClicked(int row) {
		if (row < 0) {
			return;
		}
		callFunction("UI|setVisible", false);
		onSelected();
	}
}
