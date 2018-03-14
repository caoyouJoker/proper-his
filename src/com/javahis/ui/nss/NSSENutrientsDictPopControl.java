package com.javahis.ui.nss;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

/**
 * add by lich 
 */
public class NSSENutrientsDictPopControl extends TControl{
	private String oldText = "";
	private TTable table;
	private String nutritionCode;
	/**
	 * ��ʼ��
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
	 * ���¼���
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
	 * ��ʼ������
	 */
	public void initParamenter() {
//		messageBox("��");
		Object obj = getParameter();
		if (obj == null)
			return;
		if (!(obj instanceof TParm))
			return;
		TParm parm = (TParm) obj;
		nutritionCode = parm.getValue("NUTRITION_CODE");
		
		// ��ʼ��TABLE
//		messageBox("��");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				getNutritionOrder(nutritionCode, "")));
		table.setParmValue(result);
		String text = parm.getValue("TEXT");
		setEditText(text);
	}
	
	/**
	 * ������������
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
	 * �����¼�
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
	 * ���˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public void filter() {

		String sql = getNutritionOrder(nutritionCode, getValueString("EDIT").toUpperCase());
//		System.out.println("SQLLL = = = " + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("result = = = " + result);
		table.setParmValue(result);
	}
	
	/**
	 * ģ����ѯ�������������ݲ�ѯӪ���ɷ�
	 * @param nutritionCode
	 * @param order_code
	 * @return
	 * 
	 */
	public String getNutritionOrder(String nutritionCode, String order_code){
		String sql = " SELECT NUTRITION_CODE ,NUTRITION_CHN_DESC ,NUTRITION_ENG_DESC ," +
				" PY1, PY2, NRV ,UNIT_CODE FROM NSS_NUTRITION " ;
		if (!"".equals(order_code)) {
			sql += " WHERE NUTRITION_CODE LIKE '%" + order_code + "%' "
					+ " OR NUTRITION_CHN_DESC LIKE '%" + order_code + "%' "
					+ " OR NUTRITION_ENG_DESC LIKE '%" + order_code + "%' "
					+ " OR PY1 LIKE '%" + order_code + "%'";
		}
		
		sql = sql + " ORDER BY NUTRITION_CODE ";
		
		return sql;
	}
	
	/**
	 * �����¼�
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
	 * ѡ��
	 */
	public void onSelected() {
		int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
		if (row < 0)
			return;
		TParm parm = table.getParmValue().getRow(row);
		setReturnValue(parm);
		this.closeWindow();
	}
	
	/**
	 * ��˫���¼�
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
	

}
