package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.JavaHisDebug;
import com.javahis.manager.sysfee.magicalObserverPackD;
import java.util.Vector;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import java.util.Map;
import java.util.HashMap;

import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class SYSFee_OrdSetOptionControl extends TControl {

	String dept;
	TTextFormat DEPT;
	TTextFormat TYPE;
	//double defaultQty = 0.0;
	//TDS packDDS = new TDS();
	TNumberTextField TOT_FEE;
	TTable table;
	TCheckBox all;// caowl 20130305 add
	// ���ڼ�¼�����TDS�������������table��--�Ƿ�ѡ��
//	Map recordRealyRow = new HashMap();
	// ���prefech��������
	//TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	public SYSFee_OrdSetOptionControl() {
	}

	public void onInit() {
		initParmFromOutside();
		this.callFunction("UI|TABLE|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableCheckBoxChangeValue");
		myInitControler();
		all = (TCheckBox) this.getComponent("ALL");// caowl 20130305 add
	}

	/**
	 * ��ʼ���������caseNo/stationCode
	 */
	public void initParmFromOutside() {
		// ��סԺ��ʿִ�н����õ�����TParm
		TParm outsideParm = (TParm) this.getParameter();
		if (outsideParm != null) {
			// �趨��ʼ������Ĳ���
			setDept(outsideParm.getData("PACK", "DEPT").toString());
		}
	}

	public void onSelect() {
		String type = this.getValueString("TYPE");
		showByOutside(type);

	}

	/**
	 * �ⲿ�ӿڵ���
	 * 
	 * @param orderByout
	 *            String
	 * @param fromFlg
	 *            String
	 */
	public void showByOutside(String orderCode) {

		table = (TTable) this.getComponent("TABLE");

		String exeSel = getSpl(orderCode);
		TParm parm=new TParm(TJDODBTool.getInstance().select(exeSel));
		TParm exeParm=new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if (i%2==0) {
				exeParm.addData("N_SEL", "N");
				exeParm.addData("N_ORDER_DESC", parm.getValue("ORDER_DESC",i));
				exeParm.addData("N_DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT",i));
				exeParm.addData("N_DOSAGE_QTY", parm.getDouble("DOSAGE_QTY",i));
				exeParm.addData("N_ORDER_CODE", parm.getValue("ORDER_CODE",i));
				exeParm.addData("BLANK", "");
			}else{
				exeParm.addData("S_SEL", "N");
				exeParm.addData("S_ORDER_DESC", parm.getValue("ORDER_DESC",i));
				exeParm.addData("S_DOSAGE_QTY", parm.getDouble("DOSAGE_QTY",i));
				exeParm.addData("S_DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT",i));
				exeParm.addData("S_ORDER_CODE", parm.getValue("ORDER_CODE",i));
			}
		}
		exeParm.setCount(exeParm.getCount("N_SEL"));
		table.setParmValue(exeParm);
		//packDDS.setSQL(exeSel);
		//packDDS.retrieve();
		// �õ�ȡ������������/2,��ʾ������
//		int row = packDDS.rowCount() / 2;
//
//		// �õ���table���Լ�����TDS
//		TDS tds = (TDS) table.getDataStore();
//		// ���ø�TDS����Ϊ5�У��յģ�
//		tds.getBuffer(TDS.PRIMARY).setCount(row);
//		// ��TDS������ҲΪ��
//		tds.setColumns(new String[] {});
		// �����۲���
		//magicalObserverPackD s = new magicalObserverPackD();
		// ���øù۲�����Ҫ�ı��TDS
		//s.setDS(packDDS);
		// ���ù۲�����Ҫ�۲��TDS���ٵģ�
		//tds.addObserver(s);
		//table.setDSValue();

	}

	/**
	 * table�ϵ�checkBoxע�����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxChangeValue(Object obj) {

		// ��õ����table����
		TTable table = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		table.acceptText();
		TParm tableParm=table.getParmValue();
		// ���ѡ�е���/��
		int row = table.getSelectedRow();
		if (row<0) {
			return;
		}
		int col = table.getSelectedColumn();
		if (col<0) {
			return;
		}
		// ���ѡ�е��ǵ�11�оͼ���ִ�ж���--ִ��
		String columnName = table.getParmMap(col);
		if (columnName.equals("N_SEL")) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			tableParm.setData("N_SEL",row, !exeFlg);
			// �޸ļ����ܼ�
			countTotFee();
		} else if (columnName.equals("S_SEL")) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			//table.setItem(row, "S_SEL", !exeFlg);
			tableParm.setData("S_SEL",row, !exeFlg);
			// �޸ļ����ܼ�
			countTotFee();
		}
		table.setParmValue(tableParm);
	}

	/**
	 * �õ�SQL���
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getSpl(String packcode) {

		String sql = " SELECT A.PACK_CODE,A.ORDER_CODE,A.ORDER_DESC||'(' ||B.SPECIFICATION || ')' ORDER_DESC," +
				"A.DOSAGE_QTY,A.DOSAGE_UNIT,A.HIDE_FLG,A.OPT_USER,A.OPT_DATE,A.OPT_TERM  FROM SYS_ORDER_PACKD  A ,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND" + " A.PACK_CODE='"
				+ packcode + "' ORDER BY A.ORDER_DESC";
		//System.out.println("sql::::"+sql);
		return sql;

	}

	/**
	 * ȷ������
	 * 
	 * @param args
	 *            String[]
	 */
	public void onOK() {
		// �ռ�Ҫ���ص����ݣ�code/������
		//TParm retDate = gainRtnDate();
		TParm resulta = new TParm();
		TParm tableParm=table.getParmValue();
		TParm exeParm=new TParm();
		//boolean flg=false;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("N_SEL",i).equals("Y")) {
				if (tableParm.getValue("N_ORDER_CODE",i).length()<=0) {
				}else{
					resulta=getSysFee(tableParm.getValue("N_ORDER_CODE",i));
					if(resulta.getCount()<=0){
						this.messageBox("����Ϊ"+tableParm.getValue("N_ORDER_CODE",i)+"��ҽ�������ڣ�");
						 return;
					}
					if("N".equals(resulta.getValue("ACTIVE_FLG",0))){
						this.messageBox(resulta.getValue("ORDER_DESC",0)+"��ͣ�����ܴ��أ�");
						return;
					}
					exeParm.addData("ORDER_CODE", tableParm.getValue("N_ORDER_CODE",i));
					exeParm.addData("DOSAGE_QTY", tableParm.getDouble("N_DOSAGE_QTY",i));
					exeParm.addData("DOSAGE_UNIT",tableParm.getValue("N_DOSAGE_UNIT",i));
				}
			}
			if(tableParm.getValue("S_SEL",i).equals("Y")){
				if (tableParm.getValue("S_ORDER_CODE",i).length()<=0) {
				}else{
					resulta=getSysFee(tableParm.getValue("S_ORDER_CODE",i));
					if(resulta.getCount()<=0){
						this.messageBox("����Ϊ"+tableParm.getValue("S_ORDER_CODE",i)+"��ҽ�������ڣ�");
						 return;
					}
					if("N".equals(resulta.getValue("ACTIVE_FLG",0))){
						this.messageBox(resulta.getValue("ORDER_DESC",0)+"��ͣ�����ܴ��أ�");
						return;
					}
					exeParm.addData("ORDER_CODE", tableParm.getValue("S_ORDER_CODE",i));
					exeParm.addData("DOSAGE_QTY", tableParm.getDouble("S_DOSAGE_QTY",i));
					exeParm.addData("DOSAGE_UNIT",tableParm.getValue("S_DOSAGE_UNIT",i));
				}
			}
		}
		exeParm.setCount(exeParm.getCount("ORDER_CODE"));
		//System.out.println("exeParm����������"+exeParm);
		// ���ظ����ý��������
		this.setReturnValue(exeParm);
		this.closeWindow();
	}

	/**
	 * �ռ�Ҫ���ص�����
	 */
//	public TParm gainRtnDate() {
//		TParm result = new TParm();
//		
//		// ѭ����ȡѡ�е���Ŀ
//		for (int i = 0; i < packDDS.rowCount(); i++) {
//			// ���к�Ϊ��������map��ѯ���ظ����Ƿ���ѡ��
//			if (TypeTool.getBoolean(recordRealyRow.get(i + ""))) {
//				String orderCode = packDDS.getItemString(i, "ORDER_CODE");
//				double qty = packDDS.getItemDouble(i, "DOSAGE_QTY");
//				result.addData("ORDER_CODE", orderCode);
//				result.addData("DOSAGE_QTY", qty);
//				result.addData("DOSAGE_UNIT", packDDS.getItemString(i, "DOSAGE_UNIT"));//====pangben 2013-08-05 ��ӵ�λ
//			}
//			continue;
//		}
//
//		return result;
//	}
	
	public TParm getSysFee(String orderCode){
		TParm result=new TParm();
		 String sql="SELECT ACTIVE_FLG ,ORDER_CODE,ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"'";
		 result = new TParm(TJDODBTool.getInstance().select(sql));
		 if(result.getCount()<0){
			 return result;
		 }
		    return result;
	}

	/**
	 * ��ص�5�У�����ֵ�ĸı�ˢ���ܷ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onChangeFee(TTableNode cell) {

		int row = cell.getRow();
		TParm tableParm=table.getParmValue();
		if (cell.getColumn() == 2) {
			tableParm.setData("N_DOSAGE_QTY",row,TypeTool.getDouble(cell.getValue()));
		}
		if (cell.getColumn() == 7) {
			tableParm.setData("S_DOSAGE_QTY",row,TypeTool.getDouble(cell.getValue()));
		}
		String columnName = table.getParmMap(cell.getColumn());
		if (columnName.equals("N_SEL")) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, cell.getColumn()));
			tableParm.setData("N_SEL",row, !exeFlg);
			// �޸ļ����ܼ�
			//countTotFee();
		} else if (columnName.equals("S_SEL")) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, cell.getColumn()));
			//table.setItem(row, "S_SEL", !exeFlg);
			tableParm.setData("S_SEL",row, !exeFlg);
			// �޸ļ����ܼ�
			//countTotFee();
		}
		table.setParmValue(tableParm);
//		TParm tableParm=table.getParmValue();
		// �ø���Ŀ����*�������+ԭ�����ܷ���=���ڵ��ܷ���
		countTotFee();
	}

	/**
	 * ����orderCode���޸ĵ�cell�Ĳ��������µ��ܼ�
	 * 
	 * @param orderCode
	 *            String
	 * @param cell
	 *            TTableNode
	 */
	public void countTotFee() {
		table.acceptText();
		double totFee=0.00;
		TParm tableParm=table.getParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("N_SEL",i).equals("Y")) {
				totFee+= TypeTool.getDouble(getSysFeeValue(tableParm.getValue("N_ORDER_CODE",i),
				"OWN_PRICE")*tableParm.getDouble("N_DOSAGE_QTY",i));
			}
			if (tableParm.getValue("S_SEL",i).equals("Y")) {
				totFee+= TypeTool.getDouble(getSysFeeValue(tableParm.getValue("S_ORDER_CODE",i),
				"OWN_PRICE")*tableParm.getDouble("S_DOSAGE_QTY",i));
			}
		}
		TOT_FEE.setValue(totFee);
	}

	/**
	 * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼� ����
	 */
	public void myInitControler() {

		DEPT = (TTextFormat) this.getComponent("DEPT");
		TYPE = (TTextFormat) this.getComponent("TYPE");
		// �õ�table�ؼ�
		table = (TTable) this.getComponent("TABLE");
		table.addEventListener(
				table.getTag() + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeFee");
		// PS:��סĳһ��--���Խ�����ĳһ��cell
		table.setLockCellColumn(2, true);
		table.setLockCellColumn(7, true);
		TOT_FEE = (TNumberTextField) this.getComponent("TOT_FEE");

		DEPT.setValue(getDept());
	}

	/**
	 * ȡ��
	 * 
	 * @param args
	 *            String[]
	 */
	public void onCANCLE() {
		switch (messageBox("��ʾ��Ϣ", "ȷ��ȡ��ѡ��", this.YES_NO_OPTION)) {
		case 0:
			this.closeWindow();
		case 1:
			break;
		}
		return;
	}

	// caowl 20130305 start
	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		table.acceptText();
		if (table == null) {
			return;
		}
		TParm tableParm =table.getParmValue();
		int row = table.getRowCount();
		double totFee = 0.0;// �ܼ�
		if (all.isSelected()) {

			for (int i = 0; i < row; i++) {
				tableParm.setData("N_SEL",i , true);
				tableParm.setData("S_SEL",i , true);
				//table.setItem(i, "S_SEL", true);
				double ownPrice = TypeTool.getDouble(getSysFeeValue(tableParm.getValue("N_ORDER_CODE",i), "OWN_PRICE"));
				// ѡ���ʱ���ʼ��
				double qty = tableParm.getDouble("N_DOSAGE_QTY",i);

				totFee += qty * ownPrice;
				ownPrice = TypeTool.getDouble(getSysFeeValue(tableParm.getValue("S_ORDER_CODE",i), "OWN_PRICE"));
				// ѡ���ʱ���ʼ��
				qty = tableParm.getDouble("S_DOSAGE_QTY",i);

				totFee += qty * ownPrice;
			}
//			for (int i = 0; i < 2 * row; i++) {
//				String ordCode = tableParm.getValue("ORDER_CODE",i);
//
//				double ownPrice = TypeTool.getDouble(getSysFeeValue(ordCode, "OWN_PRICE"));
//				// ѡ���ʱ���ʼ��
//				double qty = packDDS.getItemDouble(i, "DOSAGE_QTY");
//
//				totFee += qty * ownPrice;
//				recordRealyRow.put(i + "", true);
//			}

		} else {
			for (int i = 0; i < row; i++) {

				tableParm.setData("N_SEL",i , false);
				tableParm.setData("S_SEL",i , false);
			}
			totFee = 0.0;
		}
		TOT_FEE.setValue(totFee);
		table.setParmValue(tableParm);
	}

	// caowl 20130305 end

	/**
	 * 
	 * @param s
	 *            ���ݵ�ORDER_CODE
	 * @param colName
	 *            Ҫ�������
	 * @return String
	 */
	public double getSysFeeValue(String s, String colName) {
		String sql="SELECT "+colName+" FROM SYS_FEE WHERE ORDER_CODE='"+s+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()<=0) {
			return 0.00;
		}
		return result.getDouble(colName,0);
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDSETOPTION.x");
	}

}
