package com.javahis.ui.sys;

import com.dongyang.control.TControl;

import java.util.Vector;

import com.dongyang.ui.TTreeNode;

import jdo.sys.Operator;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:�������ֵ䵵
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 1.0 (c) 2017
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liuyalin
 */
public class SYSMedicalDepartmentChargesControl extends TControl {

	public SYSMedicalDepartmentChargesControl() {
	}

	/**
	 * ����
	 */
	private TTreeNode treeRoot;
	/**
	 * ��Ź�����𹤾�
	 */
	private SYSRuleTool ruleTool;
	/**
	 * �������ݷ���datastore���ڶ��������ݹ���
	 */
	private TDataStore treeDataStore = new TDataStore();
	/**
	 * ��ǰѡ�е���Ŀ����(SYS_FEE_HISTORY�У�ORDER_CODE/SYS_ORDERSETDETAI�У�ORDERSET_CODE)
	 */
	String orderCode = "";
	// --------------------
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	// ����order�õ�����
	public double getPrice(String code) {
		if (dataStore == null)
			return 0.0;
		String bufferString = dataStore.isFilter() ? dataStore.FILTER
				: dataStore.PRIMARY;
		TParm parm = dataStore.getBuffer(bufferString);
		Vector vKey = (Vector) parm.getData("ORDER_CODE");
		Vector vPrice = (Vector) parm.getData("OWN_PRICE");
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (code.equals(vKey.get(i)))
				return TypeTool.getDouble(vPrice.get(i));
		}
		return 0.0;
	}

	/**
	 * ����Ŀؼ�
	 */
	// ��
	TTree tree;
	// ��
	TTable TABLE;

	public void onInit() { // ��ʼ������
		super.onInit();
		tree = (TTree) callFunction("UI|TREE|getThis");
		// ��ʼ����
		onInitTree();
		// ��tree��Ӽ����¼�
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		// ��ʼ�����
		onInitNode();
		callFunction("UI|downTable|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
	}

	/**
	 * ��ʼ����
	 */
	public void onInitTree() {
		// �õ�����
		treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
		if (treeRoot == null)
			return;
		// �����ڵ����������ʾ
		treeRoot.setText("���������");
		// �����ڵ㸳tag
		treeRoot.setType("Root");
		// ���ø��ڵ��id
		treeRoot.setID("");
		// ������нڵ������
		treeRoot.removeAllChildren();
		// ���������ʼ������
		callMessage("UI|TREE|update");
	}

	/**
	 * ��ʼ�����Ľ��
	 */

	public void onInitNode() {
		// ��dataStore��ֵ
		treeDataStore
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
		// �����dataStore���õ�������С��0
		if (treeDataStore.retrieve() <= 0)
			return;
		// ��������,�Ǳ�������еĿ�������
		ruleTool = new SYSRuleTool("EXM_RULE");
		if (ruleTool.isLoad()) { // �����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
			TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
					"CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
			// ѭ����������ڵ�
			for (int i = 0; i < node.length; i++)
				treeRoot.addSeq(node[i]);
		}
		// �õ������ϵ�������
		TTree tree = (TTree) callMessage("UI|TREE|getThis");
		// ������
		tree.update();
		// ��������Ĭ��ѡ�нڵ�
		tree.setSelectNode(treeRoot);
	}

	/**
	 * ������
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) {
		// // ���
		// onClear();
		// �õ�������Ľڵ����
		TTreeNode node = tree.getSelectNode();
		if (node == null)
			return;
		// �õ�table����
		TTable table = (TTable) this.callFunction("UI|upTable|getThis");
		// �жϵ�����Ƿ������ĸ����
		if (node.getType().equals("Root")) {
			// ��������ĸ��ӵ�table�ϲ���ʾ����
			table.removeRowAll();
		} else { // �����Ĳ��Ǹ����
					// �õ���ǰѡ�еĽڵ��idֵ
			String id = node.getID();
			String sql = getSQL(id);
		}
	}

	private String getSQL(String orderCode) {
		String sql = " ";
		// ���������
		if (orderCode != null && orderCode.length() > 0)
			sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,B.PRICE3,A.ORDERSET_CODE,A.ORDER_DESC2,A.OWN_PRICE2,A.DOSAGE_QTY "
					+ "FROM (  SELECT A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,A.ORDERSET_CODE,B.ORDER_DESC ORDER_DESC2,"
					+ "B.OWN_PRICE OWN_PRICE2,A.DOSAGE_QTY "
					+ "FROM (SELECT A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,B.ORDER_CODE ORDERSET_CODE,B.DOSAGE_QTY "
					+ "FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
					+ "WHERE B.ORDERSET_CODE(+) = A.ORDER_CODE "
					+ "AND ORDERSET_FLG = 'Y' "
					+ "AND A.ACTIVE_FLG = 'Y' "
					+ "AND A.ORDER_CODE LIKE 'Y%') A,SYS_FEE B "
					+ "WHERE A.ORDERSET_CODE = B.ORDER_CODE "
					+ "GROUP BY A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,A.ORDERSET_CODE,B.ORDER_DESC,B.OWN_PRICE,A.DOSAGE_QTY) A,"
					+ "(SELECT A.ORDERSET_CODE, SUM (A.DOSAGE_QTY * B.OWN_PRICE) PRICE3 "
					+ "FROM SYS_ORDERSETDETAIL A, SYS_FEE B "
					+ "WHERE B.ORDER_CODE = A.ORDER_CODE "
					+ "GROUP BY ORDERSET_CODE) B "
					+ "WHERE B.ORDERSET_CODE(+) = A.ORDER_CODE "
					+ "AND B.ORDERSET_CODE LIKE '"
					+ orderCode
					+ "%'"
					+ "GROUP BY A.ORDER_CODE, A.ORDER_DESC,B.PRICE3,A.ORDERSET_CODE,A.ORDER_DESC2,A.OWN_PRICE2, A.DOSAGE_QTY "
					+ "ORDER BY ORDER_CODE ";
		System.out.println("-------------" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTTable("TABLE").setParmValue(parm);
		return null;
	}

//	/**
//	 * ��ӡ����
//	 */
//	public void onPrint() {
//		TTable table = getTTable("TABLE");
//		// ����
//		if (table.getRowCount() <= 0) {
//			this.messageBox("û�д�ӡ����");
//			return;
//		} else {
//			// ��ӡ����
//			TParm data = new TParm();
//			// ��ͷ����
//			data.setData("TITLE", "TEXT", Manager.getOrganization()
//					.getHospitalCHNFullName(Operator.getRegion())
//					+ "ҽ�������շ��ײͱ���");
//
//			data.setData("DATE", "TEXT",
//					"�Ʊ�ʱ��: "
//							+ SystemTool.getInstance().getDate().toString()
//									.substring(0, 10).replace('-', '/'));
//			data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
//
//			// �������
//			TParm parm = new TParm();
//			TParm tableParm = table.getShowParmValue();
//			for (int i = 0; i < table.getRowCount(); i++) {
//				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
//				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
//				parm.addData("PRICE3", tableParm.getValue("PRICE3", i));
//				parm.addData("ORDERSET_CODE",
//						tableParm.getValue("ORDERSET_CODE", i));
//				parm.addData("ORDER_DESC2",
//						tableParm.getValue("ORDER_DESC2", i));
//				parm.addData("OWN_PRICE2", tableParm.getValue("OWN_PRICE2", i));
//				parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
//			}
//			parm.setCount(parm.getCount("ORDER_CODE"));
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "PRICE3");
//			parm.addData("SYSTEM", "COLUMNS", "ORDERSET_CODE");
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC2");
//			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE2");
//			parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
//			data.setData("tablePrint", parm.getData());
//			// ���ô�ӡ����
//			// this.messageBox_(data);
//			// this.messageBox_(parm.getData());
//			this.openPrintWindow(
//					"%ROOT%\\config\\prt\\opb\\OPBMedicalDepartmentCharges.jhw",
//					data);
//			// this.openPrintDialog(
//			// "%ROOT%\\config\\prt\\opb\\ceshi.jhw", data);
//
//		}
//
//	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		TTable table = this.getTTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox_("���޵���Excel����");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "ҽ�������շ��ײͱ���");

	}

	/**
	 * ��ȡTTable
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		TABLE.removeRowAll();
	}

}
