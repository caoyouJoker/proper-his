package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
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
public class CLPOrderPopupControl extends TControl {
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
	public CLPOrderPopupControl() {
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
		String ordTypeCode = null;
		String region_code = null;
		if (obj == null) {
			orderFlg = null;
		} else if (!(obj instanceof TParm)) {
			orderFlg = null;
		} else {
			TParm parm = (TParm) obj;
			orderFlg = parm.getValue("ORDER_FLG");
			ordTypeCode = parm.getValue("ORDTYPE_CODE");
			region_code = parm.getValue("REGION_CODE");
		}
		if (checkInputString(orderFlg)) {
			if ("Y".equals(orderFlg)) {
				 String sql =
				 " SELECT A.ORDER_CODE, A.ORDER_DESC AS ORDER_CHN_DESC, A.PY1, A.PY2, A.SEQ, A.DESCRIPTION, "
				 +
				 "A.TRADE_ENG_DESC AS ORDER_ENG_DESC, A.GOODS_DESC, A.GOODS_PYCODE, A.ALIAS_DESC, A.ALIAS_PYCODE,"
				 +
				 " A.SPECIFICATION, A.NHI_FEE_DESC, A.HABITAT_TYPE, A.MAN_CODE, A.HYGIENE_TRADE_CODE,"
				 +
				 " A.ORDER_CAT1_CODE, A.CHARGE_HOSP_CODE, A.OWN_PRICE, A.NHI_PRICE, "
				 +
				 "A.GOV_PRICE, A.UNIT_CODE, A.LET_KEYIN_FLG, A.DISCOUNT_FLG, "
				 +
				 "A.EXPENSIVE_FLG, A.OPD_FIT_FLG, A.EMG_FIT_FLG, A.IPD_FIT_FLG, A.HRM_FIT_FLG, "
				 +
				 "A.DR_ORDER_FLG, A.INTV_ORDER_FLG, A.LCS_CLASS_CODE, A.TRANS_OUT_FLG, A.TRANS_HOSP_CODE, "
				 +
				 "A.USEDEPT_CODE, A.EXEC_ORDER_FLG, A.EXEC_DEPT_CODE, A.INSPAY_TYPE, A.ADDPAY_RATE, A.ADDPAY_AMT, "
				 +
				 "A.NHI_CODE_O, A.NHI_CODE_E, A.NHI_CODE_I, A.CTRL_FLG, A.CLPGROUP_CODE, A.ORDERSET_FLG, A.INDV_FLG, "
				 +
				 "A.SUB_SYSTEM_CODE, A.RPTTYPE_CODE, A.DEV_CODE, A.OPTITEM_CODE, A.MR_CODE, A.DEGREE_CODE, A.CIS_FLG, "
				 +
				 "A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.CAT1_TYPE,C.DOSAGE_QTY,C.MEDI_QTY,C.MEDI_UNIT,C.DOSAGE_UNIT AS DISPENSE_UNIT "
				 + " FROM SYS_FEE A,CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2";
				 sql = sql +
				 " ,PHA_TRANSUNIT C WHERE CO1.ORDTYPE_CODE = CO2.TYPE_CODE AND A.ORDER_CODE =CO1.ORDER_CODE  AND A.ORDER_CODE = C.ORDER_CODE(+) AND  CO1.ORDER_FLG = 'Y' AND A.ACTIVE_FLG='Y' AND (A.REGION_CODE='"+region_code+"' OR A.REGION_CODE  IS NULL   ) ";
				 if (checkInputString(ordTypeCode)) {
				 sql = sql + " AND CO1.ORDTYPE_CODE = '" + ordTypeCode + "'";
				 }
				sql += " ORDER BY SEQ";
				// 处理在变异分析使用时使用的情况
				// StringBuffer sqlbf = new StringBuffer();
				// sqlbf.append("SELECT * FROM (");
				// sqlbf.append("");
				this.SQL = sql;
			} else if ("N".equals(orderFlg)) {
				// 卢海修改
				String sql = " SELECT B.CLP_QTY,B.CLP_UNIT,B.CLP_RATE, A.CHKTYPE_CODE, A.CHKITEM_CODE AS ORDER_CODE, A.REGION_CODE, A.CHKITEM_CHN_DESC AS ORDER_CHN_DESC, A.CHKITEM_ENG_DESC AS ORDER_ENG_DESC, A.PY1, A.PY2, A.SEQ, A.CLNCPATH_CODE, A.CHKUSER_CODE, A.INDV_FLG, A.DESCRIPTION, A.OPT_USER, A.OPT_DATE, A.OPT_TERM"
						+ " FROM CLP_CHKITEM A,"
						+ " (SELECT CO1.ORDER_CODE,"
						+ " CO1.ORDER_FLG,"
						+ " CO1.ORDTYPE_CODE,"
						+ " CO1.CLP_UNIT,"
						+ " CO1.CLP_RATE,"
						+ " CO1.CLP_QTY,"
						+ " CO1.DEFAULT_FLG,"
						+ " CO2.TYPE_CHN_DESC,"
						+ " CO2.TYPE_ENG_DESC"
						+ " FROM CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2"
						+ " WHERE CO1.ORDTYPE_CODE = CO2.TYPE_CODE"
						+ " AND CO1.ORDER_FLG = 'N' ";
				if (checkInputString(ordTypeCode)) {
					sql = sql + " AND CO1.ORDTYPE_CODE = '" + ordTypeCode + "'";
				}
				sql = sql
						+ " ) B WHERE A.CHKITEM_CODE = B.ORDER_CODE AND (A.REGION_CODE='"
						+ region_code + "' OR A.REGION_CODE  IS NULL   )"
						+ " ORDER BY SEQ";
				this.SQL = sql;
			} else if ("O".equals(orderFlg)) {
				String sql = " SELECT A.ORDER_CODE, REGION_CODE, A.ORDER_CHN_DESC, A.ORDER_ENG_DESC, A.PY1, A.PY2, A.UNIT, A.FREQ, A.AMOUNT, A.TYPE_CODE, A.DESCRIPTION, A.DEL_FLG, A.CHKTYPE_CODE, A.SEQ, A.OPT_USER, A.OPT_DATE, A.OPT_TERM"
						+ " FROM CLP_NURSORDER A,"
						+ " (SELECT CO1.ORDER_CODE,"
						+ " CO1.ORDER_FLG,"
						+ " CO1.ORDTYPE_CODE,"
						+ " CO1.CLP_UNIT,"
						+ " CO1.CLP_RATE,"
						+ " CO1.DEFAULT_FLG,"
						+ " CO2.TYPE_CHN_DESC,"
						+ " CO2.TYPE_ENG_DESC"
						+ " FROM CLP_ORDERTYPE CO1, CLP_ORDTYPE CO2"
						+ " WHERE CO1.ORDTYPE_CODE = CO2.TYPE_CODE"
						+ " AND CO1.ORDER_FLG = 'O' ";
				if (checkInputString(ordTypeCode)) {
					sql = sql + " AND CO1.ORDTYPE_CODE = '" + ordTypeCode + "'";
				}
				sql = sql
						+ " ) B WHERE A.ORDER_CODE = B.ORDER_CODE AND (A.REGION_CODE='"
						+ region_code + "' OR A.REGION_CODE  IS NULL   )"
						+ " ORDER BY SEQ";
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
		if (oldText.equals(s)) {
			return;
		}
		oldText = s;
		int count = dataParm.getCount("ORDER_CODE");
		String names[] = dataParm.getNames();
		TParm temp = new TParm();
		for (int i = 0; i < count; i++) {
			TParm rowParm = dataParm.getRow(i);
			if (this.filter(rowParm)) {
				for (String tempData : names) {
					temp.addData(tempData, rowParm.getData(tempData));
				}
			}
		}
		table.setParmValue(temp);
		int rowCount = temp.getCount("ORDER_CODE");
		if (rowCount > 0) {
			table.setSelectedRow(0);
		}
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
		boolean falg = parm.getValue("ORDER_CODE").toUpperCase().contains(
				oldText)
				|| parm.getValue("ORDER_CHN_DESC").toUpperCase().contains(
						oldText)
				|| parm.getValue("ORDER_ENG_DESC").toUpperCase().contains(
						oldText)
				|| parm.getValue("PY1").toUpperCase().contains(oldText)
				|| parm.getValue("PY2").toUpperCase().contains(oldText);
		return falg;
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
