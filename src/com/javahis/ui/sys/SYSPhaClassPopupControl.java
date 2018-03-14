package com.javahis.ui.sys;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

public class SYSPhaClassPopupControl extends TControl {

	public SYSPhaClassPopupControl() {

	}

	private String oldText = "";
	private TTable table;
	private String sql = "";

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
	 * 初始化参数
	 */
	public void initParamenter() {
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		String phaClass = parm.getValue("ALLERGY_TYPE");
		
		// 初始化TABLE
		// 药理大分类 
		if ("D".equals(phaClass)) {
			sql = "SELECT B.CATEGORY_CODE ORDER_CODE,B.CATEGORY_CHN_DESC ORDER_DESC"
					+ " FROM SYS_RULE A,SYS_CATEGORY B"
					+ " WHERE A.RULE_TYPE='PHA_RULE' AND A.CLASSIFY1 > 0 AND "
					+ " B.RULE_TYPE=A.RULE_TYPE AND LENGTH(B.CATEGORY_CODE)=A.CLASSIFY1";
		}
		// 药理次分类
		if ("E".equals(phaClass)) {
			sql = "SELECT B.CATEGORY_CODE ORDER_CODE,B.CATEGORY_CHN_DESC ORDER_DESC"
					+ " FROM SYS_RULE A,SYS_CATEGORY B"
					+ " WHERE A.RULE_TYPE='PHA_RULE' AND A.CLASSIFY2 > 0 AND "
					+ " B.RULE_TYPE=A.RULE_TYPE AND LENGTH(B.CATEGORY_CODE)=A.CLASSIFY1+A.CLASSIFY2";
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
		
		String text = parm.getValue("TEXT");
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
		if (oldText.equals(s))
			return;
		oldText = s;
		filter();
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
	public void filter() {
		String edit = getValueString("EDIT").toUpperCase();
		System.out.println(edit);
		String sql1 = "";
		if (!"".equals(edit)) {
			sql1 = " AND (B.CATEGORY_CODE LIKE'" + edit + "%' "
					+ "OR B.CATEGORY_CHN_DESC LIKE '" + edit + "%' "
					+ "OR B.PY1 LIKE '" + edit + "%' )";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql+sql1));
		table.setParmValue(result);
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
			callFunction("UI|TABLE|setSelectedRow", row);
			break;
		case KeyEvent.VK_DOWN:
			row = (Integer) callFunction("UI|TABLE|getSelectedRow") + 1;
			if (row >= count)
				row = count - 1;
			callFunction("UI|TABLE|setSelectedRow", row);
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
		TParm parm = table.getParmValue().getRow(row);
		setReturnValue(parm);
		this.closeWindow();
	}

}
