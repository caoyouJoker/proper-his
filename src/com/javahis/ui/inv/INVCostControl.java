package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;  
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.sql.Timestamp;


import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.TypeTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import jdo.ind.INDTool;   
import com.dongyang.ui.TTableNode;

/**
 * <p>
 * Title: ���۽�תControl
 * </p>
 *   
 * <p>
 * Description: ���۽�תControl
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
 * @author fux 2009.05.12
 * @version 1.0
 */
public class INVCostControl extends TControl {

	// ������
	private TTable table_m;

	// ϸ����
	private TTable table_d; 

	public INVCostControl() {
	} 

	String disNo = ""; 

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");

		// ��ʼ��������
		initPage();
	}   

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		parm.setData("START_DATE", formatString(this
				.getValueString("START_DATE")));
		parm.setData("END_DATE", formatString(this.getValueString("END_DATE")));
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		String flgString = "Y"; 
		String sqlM = "";   
		String sqlD = "";
		
		// ע�� inv_stockdd����cost_flg ��cost_date   
		
		if (this.getRadioButton("REQUEST_FLG_A").isSelected()) {
			flgString = "Y"; 

			// sqlM="select 'N' as SELECT_FLG, b.inv_code ,b.DESCRIPTION,b.INV_CHN_DESC,d.UNIT_CHN_DESC UNIT,c.qty from"
			// +
			// " (select count(a.COST_FLG) qty,a.inv_code  from inv_stockDD a where a.cost_flg='"+flgString+"'  and a.cost_date"
			// +  
			// "  BETWEEN TO_DATE('" +
			// this.getValueString("START_DATE").toString().substring(0, 19) +
			// "','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('" +
			// this.getValueString("END_DATE").toString().substring(0, 19) +
			// "','yyyy/mm/dd hh24:mi:ss')   group by inv_code) c"+
			// " left join inv_base b  on c.inv_code=B.INV_CODE" +
			// " left join SYS_UNIT d on b.STOCK_UNIT= d.UNIT_CODE" +
			// " ";

			
			// ѡ,���յ���,���ʴ���,���,��������   
			sqlM = "select 'N' as SELECT_FLG,a.VERIFYIN_NO disno, b.inv_code ,b.DESCRIPTION,b.INV_CHN_DESC,d.UNIT_CHN_DESC UNIT,a.in_qty qty,"
					+ " s.SUP_CHN_DESC sup,ss.SUP_CHN_DESC upsup,p.CONTRACT_PRICE price from"
					+ " INV_VERIFYIND a "
					+ " left join INV_VERIFYINM m on m.VERIFYIN_NO=a.VERIFYIN_NO "
					+ " left join inv_base b on b.inv_code=a.inv_code"
					+ " left join SYS_SUPPLIER s on s.SUP_CODE=b.SUP_CODE"
					+ " left join SYS_SUPPLIER ss on ss.SUP_CODE=b.UP_SUP_CODE"  
					+ " left join inv_agent p on b.inv_code=p.inv_code"
					+ " left join SYS_UNIT d on b.STOCK_UNIT= d.UNIT_CODE"
					+ " where  CON_ORG='0306'  and 	m.VERIFYIN_DATE  BETWEEN TO_DATE('"
					+ this.getValueString("START_DATE").toString().substring(0,
							19) 
					+ "','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('"
					+ this.getValueString("END_DATE").toString().substring(0,
							19)
					+ "','yyyy/mm/dd hh24:mi:ss')"  
					+ " order by  a.VERIFYIN_NO desc ";

			sqlD = "select 'N' as SELECT_FLG, a.inv_code,a.rfid,b.DESCRIPTION,b.INV_CHN_DESC,d.UNIT_CHN_DESC  UNIT ,spc.PAT_NAME,o.org_desc,fe.ORDER_DESC ,fe.OWN_PRICE,'1' as qty,fe.OWN_PRICE total ,spc.mr_no,spc.case_no from "
					+ " inv_stockdd  a  left join inv_base b on a.inv_code=b.inv_code"
					+ " left join SYS_UNIT d on b.STOCK_UNIT= d.UNIT_CODE"
					+ " left join SPC_INV_RECORD spc on spc.BAR_CODE=a.rfid"
					+ " left join inv_org o on o.org_code=spc.EXE_DEPT_CODE  "
					+ " left join sys_fee fe on fe.ORDER_CODE=b.ORDER_CODE  "
					+ " where a.cost_flg='"
					+ flgString
					+ "'"
					+ " and a.cost_date BETWEEN TO_DATE('"           
					+ this.getValueString("START_DATE").toString().substring(0, 
							19)
					+ "','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('"
					+ this.getValueString("END_DATE").toString().substring(0,
							19)
					+ "','yyyy/mm/dd hh24:mi:ss') "
					+ " order by a.inv_code";
			
			TParm parmM = new TParm(TJDODBTool.getInstance().select(sqlM));
			table_m.setParmValue(parmM);
			if(parmM.getCount()<0){
				this.messageBox("E0008");
			}
		} else {
			flgString = "N";

			sqlM = "select 'N' as SELECT_FLG, b.inv_code ,'' disno,b.DESCRIPTION,b.INV_CHN_DESC,d.UNIT_CHN_DESC UNIT,c.qty,s.SUP_CHN_DESC sup,ss.SUP_CHN_DESC upsup,p.CONTRACT_PRICE price from"
					+ " (select count(a.COST_FLG) qty,a.inv_code  from inv_stockDD a where a.cost_flg='"
					+ flgString
					+ "' and a.WAST_FLG='Y' and "
					+ "  a.OUT_DATE<TO_DATE('"
					+ this.getValueString("END_DATE").toString().substring(0,
							19)
					+ "','yyyy/mm/dd hh24:mi:ss')  group by inv_code) c"
					+ " left join inv_base b  on c.inv_code=B.INV_CODE "  
					+ " left join SYS_SUPPLIER s on s.SUP_CODE=b.SUP_CODE"
					+ " left join SYS_SUPPLIER ss on ss.SUP_CODE=b.UP_SUP_CODE"
					+ " left join inv_agent p on b.inv_code=p.inv_code"
					+ " left join SYS_UNIT d on b.STOCK_UNIT= d.UNIT_CODE"
					+ "  where 1=1 order by b.inv_code";

			sqlD = "select 'N' as SELECT_FLG, a.inv_code,a.rfid,b.DESCRIPTION,b.INV_CHN_DESC,d.UNIT_CHN_DESC  UNIT ,spc.PAT_NAME,o.org_desc,fe.ORDER_DESC ,fe.OWN_PRICE,'1' as qty,fe.OWN_PRICE total ,spc.mr_no,spc.case_no from "
					+ " inv_stockdd  a  left join inv_base b on a.inv_code=b.inv_code"
					+ " left join SYS_UNIT d on b.STOCK_UNIT= d.UNIT_CODE"
					+ " left join SPC_INV_RECORD spc on spc.BAR_CODE=a.rfid"
					+ " left join inv_org o on o.org_code=spc.EXE_DEPT_CODE  "
					+ " left join sys_fee fe on fe.ORDER_CODE=b.ORDER_CODE  "
					+ " where a.cost_flg='"
					+ flgString
					+ "' and a.WAST_FLG='Y' and"
					+ " a.OUT_DATE<TO_DATE('"
					+ this.getValueString("END_DATE").toString().substring(0,
							19)
					+ "','yyyy/mm/dd hh24:mi:ss') "
					+ " order by a.inv_code "; 
			
			TParm parmD = new TParm(TJDODBTool.getInstance().select(sqlD));
			table_d.setParmValue(parmD);
			if(parmD.getCount()<0){
				this.messageBox("E0008");  
			}  
		}   


		
		
		// Ĭ�ϼ����ܽ�� by liyh 20120910
		// setSumRetailMoneyOnQuery(parmM);
	}

	/**
	 * �������쵥
	 */
	public void onSave() {
		if (!CheckDataM()) {
			return;
		}
		disNo = SystemTool.getInstance().getNo("ALL", "INV", "DISPENSE_NO",
				"No");



		TParm parm = new TParm();
		// �������ݣ����뵥����
		getInsertTableMData(parm);

		getInsertTableDData(parm);                                            

		getInsertTableDDData(parm);

		getDispenseMData(parm);

		getDispenseDData(parm);

		TParm result = new TParm();
		// System.out.println("parm--3-" + parm);
		//���� inv_stockdd ���cost_flg ��cost_date
		result = TIOM_AppServer.executeAction("action.inv.INVCostAction",
				"onCreateCost", parm);

		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		onClear();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		((TMenuItem) getComponent("save")).setEnabled(true);
		// ( (TMenuItem) getComponent("printM")).setEnabled(true);
		// ( (TMenuItem) getComponent("printD")).setEnabled(false);
		// ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
		table_m.setVisible(true);
		table_m.removeRowAll();
		table_d.setVisible(false);
		table_d.removeRowAll();
		// ��ջ�������
		// String clearString =
		// "APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
		// +
		// "SELECT_ALL;URGENT_FLG;CHECK_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
		// + "PRICE_DIFFERENCE";
		// clearValue(clearString);
		getRadioButton("REQUEST_FLG_B").setSelected(true);
		getRadioButton("REQUEST_TYPE_A").setSelected(true);
	}

	/**
	 * �������뵥��
	 */
	public boolean request() {

		// String REQUEST_NO = (String) table_m.getItemData(0,"REQUEST_NO");
		// TParm num=new
		// TParm(TJDODBTool.getInstance().select(INDSQL.checkData(REQUEST_NO)));
		// int number = num.getCount();
		// this.messageBox("wocao--"+number);
		// if(number>1)
		// {
		// this.messageBox("���뵥̫�࣡");
		//        	
		// }

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < table_m.getRowCount(); i++) {
			set.add((String) table_m.getItemData(i, "REQUEST_NO"));
		}
		int number = set.size();
		if (number > 1) {
			this.messageBox("һ��ֻ�ܴ�ӡһ�����뵥�����ݣ�");
			return false;

		}
		return true;
	}

	/**
	 * ��ӡ���ܵ�
	 */

	/**
	 * ��ӡ��ϸ��
	 */
	public void onPrintD() {
		boolean flg = true;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û����ϸ��Ϣ");
			return;
		}

	}

	/**
	 * // * ������벿�� //
	 */
	// public void onChangeAppOrg() {
	// if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
	// // Ԥ������ⷿ
	// TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
	// INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
	// Operator.getRegion())));
	// getComboBox("TO_ORG_CODE").setSelectedID(sup_org_code.getValue(
	// "SUP_ORG_CODE", 0));
	// }
	// }

	/**
	 * ���ͳ��״̬
	 */
	public void onChangeRequestFlg() {
		if (this.getRadioButton("REQUEST_FLG_B").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				// ( (TMenuItem) getComponent("printM")).setEnabled(true);
				// ( (TMenuItem) getComponent("printD")).setEnabled(false);
				// ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
				table_m.setVisible(true);
				table_d.setVisible(false);
			} else {
				// ( (TMenuItem) getComponent("printM")).setEnabled(false);
				// ( (TMenuItem) getComponent("printD")).setEnabled(true);
				// ( (TMenuItem) getComponent("printRecipe")).setEnabled(true);
				table_m.setVisible(false);
				table_d.setVisible(true);
			}
		} else {
			((TMenuItem) getComponent("save")).setEnabled(false);
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				// ( (TMenuItem) getComponent("printM")).setEnabled(true);
				// ( (TMenuItem) getComponent("printD")).setEnabled(false);
				// ( (TMenuItem) getComponent("printRecipe")).setEnabled(false);
				table_m.setVisible(true);
				table_d.setVisible(false);
			} else {
				// ( (TMenuItem) getComponent("printM")).setEnabled(false);
				// ( (TMenuItem) getComponent("printD")).setEnabled(true);
				// ( (TMenuItem) getComponent("printRecipe")).setEnabled(true);
				table_m.setVisible(false);
				table_d.setVisible(true);
			}
		}
	}

	/**
	 * ȫѡ
	 */
	public void onSelectAll() {
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		} else {
			flg = "N";
		}
		for (int i = 0; i < table_m.getRowCount(); i++) {
			table_m.setItem(i, "SELECT_FLG", flg);
		}
		for (int i = 0; i < table_d.getRowCount(); i++) {
			table_d.setItem(i, "SELECT_FLG", flg);
		}
		setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
		setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
		setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
				- getSumRegMoney(), 4));
	}

	/**
	 * ���(TABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 */
	public void onTableMCheckBoxClicked(Object obj) {
		table_m.acceptText();
		// this.messageBox("2222222222");
		// ���ѡ�е���
		int column = table_m.getSelectedColumn();
		if (column == 0) {
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
		}
	}

	/**
	 * ���ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableMChangeValue(Object obj) {
		// ֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// �ж����ݸı�
		if (node.getValue().equals(node.getOldValue()))
			return true;
		int column = node.getColumn();
		int row = node.getRow();

		if (column == 0) {
			// this.messageBox("11111111");
			// setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			// setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			// setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
			// - getSumRegMoney(), 4));
			return false;
		}

		if (column == 4) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("������������С�ڻ����0");
				return true;
			}
			double amt1 = StringTool.round(qty
					* table_m.getItemDouble(row, "STOCK_PRICE"), 2);
			double amt2 = StringTool.round(qty
					* table_m.getItemDouble(row, "OWN_PRICE"), 2);
			table_m.setItem(row, "STOCK_AMT", amt1);
			table_m.setItem(row, "OWN_AMT", amt2);
			table_m.setItem(row, "DIFF_AMT", amt2 - amt1);
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
			return false;
		}
		return true;
	}

	/**
	 * ���(TABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 */
	public void onTableDCheckBoxClicked(Object obj) {
		table_d.acceptText();
		// ���ѡ�е���
		int column = table_d.getSelectedColumn();
		if (column == 0) {
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
		}
	}

	/**
	 * ������(TABLE_M)�����¼�
	 */
	public void onTableMClicked() {

	}

	/**
	 * ������(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {

	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		/**
		 * Ȩ�޿��� Ȩ��1:ֻ��ʾ������������ Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ��
		 */
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// ��ʼ��TABLE
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
		getTextFormat("ORG_CODE").setValue(Operator.getDept());
		
		//1��ֱ�ӻ�ȡ���ַ���ֻ����������������ʱ�����ʹ�ã�
		//ServletActionContext .getServletContext().getRealPath("/");
//2��ͨ��������ƻ�ȡ������·�����ַ���ʲôʱ�򶼿���ʹ�ã�
//		StringBuffer url = request.getRequestURL();  
//		String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString(); 

	
		     // HttpServletRequest request = ServletActionContext.getRequest();
		 // request.getRequestURL()��ȡ�����ǲ���������URL��request.getQueryString()��ȡ������URL�Ĳ������֣�Ҫ���ȡ������������URL������Ҫ����������ƴ������
		    //String url = request.getRequestURL()+"?"+request.getQueryString();
		  
		        
		   
         //this.getClass().getClassLoader().getResource("").getPath();
        //messageBox("url:"+url);       
		
//		          ��ȡ�������磺http://f0rb.iteye.com/ 
//			Java����  �ղش���
//			StringBuffer url = request.getRequestURL();  
//			String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();  
//
//
//			��ȡ�����𻷾������ĵ��������磺 http://www.iteye.com/admin/ 
//			Java����  �ղش���
//			StringBuffer url = request.getRequestURL();  
//			String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append(request.getServletContext().getContextPath()).append("/").toString(); 
		
		//String path = request.getSession().getServletContext().getRealPath("/");

	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���벿�Ų���Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * ���ݼ���
	 * 
	 * @return boolean
	 */
	private boolean CheckDataD() {
		if ("".equals(getValueString("TO_ORG_CODE"))) {
			this.messageBox("���ܲ��Ų���Ϊ��");
			return false;
		}
		if (table_d.getRowCount() == 0) {
			this.messageBox("û����������");
			return false;
		}
		boolean flg = true;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û����������");
			return false;
		}
		return true;
	}

	/**
	 * �������ݣ����뵥����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */

	/**
	 * �������ݣ����뵥ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */

	/**
	 * �������ݣ���������״̬
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * ��ʽ���ַ���(ʱ���ʽ)
	 * 
	 * @param arg
	 *            String
	 * @return String YYYYMMDDHHMMSS
	 */
	private String formatString(String arg) {
		arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10)
				+ arg.substring(11, 13) + arg.substring(14, 16)
				+ arg.substring(17, 19);
		return arg;
	}

	/**
	 * ���������ܽ��
	 * 
	 * @return
	 */
	private double getSumRetailMoney() {
		table_m.acceptText();
		table_d.acceptText();
		double sum = 0;
		if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_m.getItemDouble(i, "OWN_AMT");
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_d.getItemDouble(i, "OWN_AMT");
			}
		}
		return StringTool.round(sum, 4);
	}

	/**
	 * ����ɹ�/�����ܽ��
	 * 
	 * @return
	 * @author liyh
	 * @date 20120910
	 */
	private void setSumRetailMoneyOnQuery(TParm parmM) {
		// �����ܽ��
		double sum_retail = 0.0;
		// �ɹ��ܽ��
		double sum_verifyin = 0.0;
		int count = parmM.getCount();
		if (null != parmM && count > 0) {
			for (int i = 0; i < count; i++) {
				sum_retail += parmM.getDouble("OWN_AMT", i);
				sum_verifyin += parmM.getDouble("STOCK_AMT", i);
			}

		}
		setValue("SUM_RETAIL_PRICE", sum_retail);
		setValue("SUM_VERIFYIN_PRICE", sum_verifyin);
	}

	/**
	 * ����ɱ��ܽ��
	 * 
	 * @return
	 */
	private double getSumRegMoney() {
		table_m.acceptText();
		table_d.acceptText();
		double sum = 0;
		if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_m.getItemDouble(i, "STOCK_AMT");
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_d.getItemDouble(i, "STOCK_AMT");
			}
		}
		return StringTool.round(sum, 4);
	}

	/**
	 * �õ�TextFormat����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * ȡ��SYS_FEE��Ϣ��������״̬����
	 * 
	 * @param order_code
	 *            String
	 */
	private void setSysStatus(String order_code) {
		TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
		String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE") + " ҩƷ����:"
				+ order.getValue("ORDER_DESC") + " ��Ʒ��:"
				+ order.getValue("GOODS_DESC") + " ���:"
				+ order.getValue("SPECIFICATION");
		callFunction("UI|setSysStatus", status_desc);
	}

	// ------------------------------------------------------------------

	/**
	 * ȡ��������������(TABLE_M)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getInsertTableMData(TParm parm) {
		TParm parm_M = new TParm();
		// ��������
		String verifyin_no = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_VERIFYIN", "No");
		parm_M.setData("VERIFYIN_NO", verifyin_no);
		parm.setData("VERIFYIN_NO", verifyin_no);
		Timestamp date = SystemTool.getInstance().getDate();
		parm_M.setData("SUP_CODE", "19");
		parm_M.setData("VERIFYIN_DATE", date);
		parm_M.setData("VERIFYIN_USER", Operator.getID());
		parm_M.setData("VERIFYIN_DEPT", "011201");
		parm_M.setData("INVOICE_NO", "");
		parm_M.setData("INVOICE_DATE", date);
		parm_M.setData("INVOICE_AMT", "");
		parm_M.setData("INVOICE_AMT", "");
		parm_M.setData("STATIO_NO", "");
		parm_M.setData("CHECK_FLG", "Y");
		parm_M.setData("OPT_USER", Operator.getID());
		parm_M.setData("OPT_DATE", date);
		parm_M.setData("OPT_TERM", Operator.getIP());
		// Ϊɶֻ�н����������� ���Լ��۽�ת 
		parm_M.setData("CON_FLG", "N");
		parm_M.setData("CON_ORG", "0306");
		parm.setData("VER_M", parm_M.getData());
		return parm;
	}

	/**
	 * ȡ��������ϸ����(TABLE_D)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getInsertTableDData(TParm parm) {
		table_m.acceptText();
		// System.out.println(table_d.getParmValue());
		TParm parm_D = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		int count = 0;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if (!"Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			parm_D.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
			parm_D.addData("SEQ_NO", count);
			count++;
			parm_D.addData("INV_CODE", table_m.getParmValue().getValue(
					"INV_CODE", i));
			String sql = "select INV_KIND from INV_BASE where INV_CODE='"
					+ table_m.getParmValue().getValue("INV_CODE", i) + "'";
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
			String c = parm2.getData("INV_KIND", 0).toString();

			String string = "select STOCK_UNIT ,COST_PRICE  from inv_base where inv_code='"
					+ table_m.getParmValue().getValue("INV_CODE", i) + "'";
			TParm ma = new TParm(TJDODBTool.getInstance().select(string));

			parm_D.addData("INV_KIND", c);
			parm_D.addData("QTY", table_m.getParmValue().getData("QTY", i));
			parm_D.addData("GIFT_QTY", "0");
			parm_D.addData("BILL_UNIT", ma.getData("STOCK_UNIT", 0).toString());
			parm_D.addData("IN_QTY", table_m.getParmValue().getData("QTY", i));
			parm_D
					.addData("STOCK_UNIT", ma.getData("STOCK_UNIT", 0)
							.toString());
			parm_D
					.addData("UNIT_PRICE", ma.getData("COST_PRICE", 0)
							.toString());
			parm_D.addData("BATCH_NO", "1");
			parm_D.addData("VALID_DATE", tnull);
			parm_D.addData("PURORDER_NO", "");
			parm_D.addData("STESEQ_NO", "");
			parm_D.addData("REN_CODE", "");
			parm_D.addData("QUALITY_DEDUCT_AMT", "");
			parm_D.addData("OPT_USER", Operator.getID());
			parm_D.addData("OPT_DATE", date);
			parm_D.addData("OPT_TERM", Operator.getIP());
			parm_D.addData("SEQMAN_FLG", "Y");
		}
		parm.setData("VER_D", parm_D.getData());
		return parm;
	}

	/**
	 * ȡ��������Ź���ϸ������(TABLE_DD)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getInsertTableDDData(TParm parm) {
		TParm parm_DD = new TParm();
		TParm dD = new TParm();
		TParm parm_D = parm.getParm("VER_D");
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		String sql = "";
		String valid_date = "";
		for (int i = 0; i < parm_D.getCount("INV_CODE"); i++) {
			if ("Y".equals(parm_D.getValue("SEQMAN_FLG", i))) {
				// INVSEQ_NO ץȡ����+1
				TParm invSeqNoParm = new TParm(TJDODBTool.getInstance().select(

				INVSQL.getInvMaxInvSeqNo(parm_D.getValue("INV_CODE", i))));
				int invseq_no = 1;
				if (invSeqNoParm.getCount() > 0) {
					invseq_no = invSeqNoParm.getInt("INVSEQ_NO", 0) + 1;
				}
				// �������ź�Ч��ȡ��BATCH_SEQ
				valid_date = TypeTool.getString(parm_D
						.getValue("VALID_DATE", i));
				if (!"".equals(valid_date) && valid_date.length() > 18) {
					valid_date = parm_D.getValue("VALID_DATE", i).substring(0,
							4)
							+ parm_D.getValue("VALID_DATE", i).substring(5, 6)
							+ parm_D.getValue("VALID_DATE", i).substring(7, 8)
							+ parm_D.getValue("VALID_DATE", i).substring(9, 10)
							+ parm_D.getValue("VALID_DATE", i)
									.substring(11, 13)
							+ parm_D.getValue("VALID_DATE", i)
									.substring(14, 16);
				}
				sql = INVSQL.getInvBatchSeq(getValueString("ORG_CODE"), parm_D
						.getValue("INV_CODE", i), parm_D
						.getValue("BATCH_NO", i), valid_date);
				TParm stockDParm = new TParm(TJDODBTool.getInstance().select(
						sql));

				int batch_seq = 1;
				if (stockDParm.getCount("BATCH_SEQ") > 0) {
					batch_seq = stockDParm.getInt("BATCH_SEQ", 0);
				} else {
					// ץȡ���BATCH_SEQ+1
					TParm batchSeqParm = new TParm(TJDODBTool.getInstance()
							.select(
									INVSQL.getInvStockMaxBatchSeq("011201",
											parm_D.getValue("INV_CODE", i))));
					if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
						batch_seq = 1;
					} else {
						batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
					}
				}
				// String kind= parm_D.getValue("INV_KIND", i);
				String xString = "02";
				List<String> lt = new ArrayList<String>();
				for (int j2 = 0; j2 < table_d.getParmValue().getCount("RFID"); j2++) {
					if (table_d.getParmValue().getData("INV_CODE", j2).equals(
							parm_D.getValue("INV_CODE", i))) {
						lt.add(table_d.getParmValue().getData("RFID", j2)
								.toString());
					}        
				}

				for (int j = 0; j < parm_D.getDouble("QTY", i); j++) {
					parm_DD
							.addData("VERIFYIN_NO", parm
									.getValue("VERIFYIN_NO"));
					parm_DD.addData("SEQ_NO", parm_D.getInt("SEQ_NO", i));
					parm_DD.addData("DDSEQ_NO", j);
					parm_DD.addData("INV_CODE", parm_D.getValue("INV_CODE", i));
					parm_DD.addData("INVSEQ_NO", invseq_no);
					invseq_no++;
					parm_DD.addData("BATCH_SEQ", batch_seq);
					parm_DD.addData("BATCH_NO", parm_D.getValue("BATCH_NO", i));
					parm_DD.addData("VALID_DATE", tnull);
					parm_DD.addData("STOCK_UNIT", parm_D.getValue("STOCK_UNIT",
							i));
					parm_DD.addData("UNIT_PRICE", parm_D.getValue("UNIT_PRICE",
							i));
					parm_DD.addData("OPT_USER", Operator.getID());
					parm_DD.addData("OPT_DATE", date);
					parm_DD.addData("OPT_TERM", Operator.getIP());

					parm_DD.addData("RFID", lt.get(j));
					dD.addData("RFID", lt.get(j));
					dD.addData("COST_DATE", date);

				}
			}
		}
		parm.setData("DD", dD.getData());
		parm.setData("VER_DD", parm_DD.getData());
		// System.out.println("========"+ parm_DD.getData());
		return parm;
	}

	/**
	 * ȡ�ó��ⵥ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getDispenseMData(TParm parm) {

		TParm dispenseM = new TParm();
		// ���ⵥ��
		dispenseM.setData("DISPENSE_NO", disNo);
		// �������
		dispenseM.setData("REQUEST_TYPE", "REQ");
		// ���뵥��
		dispenseM.setData("REQUEST_NO", "");
		// ��������
		dispenseM.setData("REQUEST_DATE", SystemTool.getInstance().getDate());
		// �������벿��
		dispenseM.setData("FROM_ORG_CODE", "011201");
		// ���벿��
		dispenseM.setData("TO_ORG_CODE", "0306");  
		// ��������
		dispenseM.setData("DISPENSE_DATE", SystemTool.getInstance().getDate());
		// ������Ա
		dispenseM.setData("DISPENSE_USER", Operator.getID());
		// ����ע��
		dispenseM.setData("URGENT_FLG", "N");
		// ��ע
		dispenseM.setData("REMARK", "");
		// ȡ������
		dispenseM.setData("DISPOSAL_FLG", "N");
		// ����ȷ������
		dispenseM.setData("CHECK_DATE", SystemTool.getInstance().getDate());
		// ����ȷ����Ա
		dispenseM.setData("CHECK_USER", Operator.getID());
		// ����ԭ��
		dispenseM.setData("REN_CODE", "R01");
		// �����ע��
		dispenseM.setData("FINA_FLG", "0");
		// OPT
		dispenseM.setData("OPT_USER", Operator.getID());
		dispenseM.setData("OPT_DATE", SystemTool.getInstance().getDate());
		dispenseM.setData("OPT_TERM", Operator.getIP());
		// ������� 2������
		dispenseM.setData("IO_FLG", "2");
		parm.setData("DISPENSE_M", dispenseM.getData());
		return parm;
	}

	/**
	 * ȡ�ó��ⵥϸ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getDispenseDData(TParm parm) {
		TParm dispenseD = new TParm();

		int count = 0;
		TNull tnull = new TNull(Timestamp.class);
		for (int i = 0; i < table_m.getRowCount(); i++) {
			// ���ⵥ��
			dispenseD.addData("DISPENSE_NO", disNo);
			// ���ⵥ���
			dispenseD.addData("SEQ_NO", count);
			count++;
			// �������
			dispenseD.addData("BATCH_SEQ", "1"); 
			// ���ʴ���
			dispenseD.addData("INV_CODE", table_m.getParmValue().getValue(
					"INV_CODE", i));
			// �������
			dispenseD.addData("INVSEQ_NO", "1");
			// ��Ź���ע��       
			dispenseD.addData("SEQMAN_FLG", "N");
			// ����
			dispenseD.addData("QTY", table_m.getParmValue().getData("QTY", i));
			// ��λ
			dispenseD.addData("DISPENSE_UNIT", "");
			// �ɱ���
			dispenseD.addData("COST_PRICE", "");
			// �������
			dispenseD.addData("REQUEST_SEQ", "");
			// ����
			dispenseD.addData("BATCH_NO", "");
			// Ч�� VALID_DATE
			dispenseD.addData("VALID_DATE", "");
			// ȡ������
			dispenseD.addData("DISPOSAL_FLG", "N");
			// OPT
			dispenseD.addData("OPT_USER", Operator.getID());
			dispenseD.addData("OPT_DATE", SystemTool.getInstance().getDate());
			dispenseD.addData("OPT_TERM", Operator.getIP());
			// ������� 2������
			dispenseD.addData("IO_FLG", "2");
		}
		parm.setData("DISPENSE_D", dispenseD.getData());
		//System.out.println("55555555555" + dispenseD.getData());
		return parm;
	}

}
