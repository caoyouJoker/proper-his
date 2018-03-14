package com.javahis.ui.ibs;

import java.awt.Component;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.dongyang.control.TControl;
import com.javahis.util.OdiUtil;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TWindow;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMXMLTool;
import jdo.bil.BIL;
import jdo.ibs.IBSOrderD;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SYSRolePopedomTool;
import jdo.sys.SystemTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.util.StringUtil;
//import com.javahis.util.JavaHisDebug;

/**
 * 
 * <p>
 * Title: סԺ�Ƽۿ�����
 * </p>
 * 
 * 
 * <p>
 * Description: סԺ�Ƽۿ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSOrderControl extends TControl {
	/**
	 * �����
	 */
	private String caseNo;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * ����
	 */
	private String bedNo;
	/**
	 * ϵͳ��
	 */
	String sysType;
	/**
	 * ҽ�� add caoyong 20131111
	 */
	private String orderDesc;
	
	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	/**
	 * ����ȼ�
	 */
	String serviceLevel;
	/**
	 * ����ҽ�����
	 */
	private int groupNo;
	private TTable t;
	private IBSOrderD order;
//	private static final String actionName = "action.ibs.IBSAction";
	private String orderNo; // ҽ�����
	private String clncpathCode;// �ٴ�·������
	private String schdCode;// ʱ�̴���
	TParm operationParm; // �����һش�����
	/**
	 * TABLE
	 */
	private static String TABLE = "MAINTABLE";
	private TextFormatCLPDuration SCHD_CODE;//add by caowl 20121126
	private TextFormatCLPDuration CLNCPATH_CODE;//add by caowl 20121126�ٴ�·��
	
	//add by huangtt start 20130922
	/**
	 * ҽ������
	 */
	private  String OrderCode;
	
	private double ownRate;    //�Ը�����
	
	/**
	 * ���һ 
	 */
	String ctz1Code;
	/**
	 * ��ݶ�
	 */
	String ctz2Code;
	/**
	 * �����
	 */
	String ctz3Code;
	//add by huangtt end 20130922
	

	/**
	 * ����ǰTOOLBAR
	 */
	public void onShowWindowsFunction() {
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
//		 this.messageBox("Ȩ��"+this.getPopedem("INWLEAD"));
//		 SYSRolePopedomTool.getInstance().getPopedom(role, groupCode, code)
		super.onInit();
		 
		// ��ʼ���������
		this.initPage();
		// tableר�õļ���
		getTTable("MAINTABLE").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// ����datastore��ֵ�ı�
		order.addEventListener(order.ACTION_SET_ITEM, this, "onSetItemEvent");
		// ģ����ѯ -------start---------
		OrderList orderDesc = new OrderList();
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.addItem("ORDER_LIST", orderDesc);
		callFunction("UI|MAINTABLE|addEventListener", "MAINTABLE" + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// ��ʱTABLEֵ�ı����
		addEventListener(TABLE + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// ����ٴ�·������ʱ��======pangben 2012-7-9
//		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
//		.getComponent("SCHD_CODE");
		
		 TextFormatCLPDuration combo_schd1 = (TextFormatCLPDuration) this
			.getComponent("SCHD_CODE");
			combo_schd1.setClncpathCode(clncpathCode);
	        combo_schd1.onQuery();
	        
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
		.getComponent("SCHD_CODE1");
		combo_schd.setClncpathCode(clncpathCode);
        combo_schd.onQuery();
        table.addItem("SCHD_CODE", combo_schd);
	}

	/**
	 * ����table�����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
//		System.out.println("3right 0000 0000000");
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.acceptText();

		TParm parm = order.getRowParm(row);
		if (parm.getValue("ORDER_CODE").length() == 0)
			return;
		String orderCode = parm.getValue("ORDER_CODE");
		String sysFeeSql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,DESCRIPTION,SPECIFICATION "
				+ "   FROM SYS_FEE "
				+ "  WHERE ORDER_CODE = '"
				+ orderCode
				+ "' ";
		parm = new TParm(TJDODBTool.getInstance().select(sysFeeSql));
		parm = parm.getRow(0);
		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
	}
	
	/**
	 * ֵ���¼�����
	 * 
	 * @param obj
	 *            Object
	 * @throws java.text.ParseException 
	 */
	public boolean onChangeTableValue(Object obj) throws java.text.ParseException {
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
//		System.out.println("666===++++===columnName columnName is ::"+columnName);
		// �жϵ�ǰ���Ƿ���ҽ��
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE).getDataStore().getRowParm(selRow);
//		System.out.println("====++++====orderP orderP orderP is :"+orderP);
		String orderSetGroupNo = orderP.getValue("ORDERSET_GROUP_NO");
		String orderSetCode = orderP.getValue("ORDERSET_CODE");
		String indvFlg = orderP.getValue("INDV_FLG");
		
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// ���ҽ������
			this.getTTable(TABLE).setDSValue(selRow);
		}
//		System.out.println("1111===++++===columnName columnName is ::"+columnName);
		if ("EXEC_DATE".equals(columnName)) {
//			this.messageBox("00000000000000000000000000000000000000000");
			String execDate = node.getValue().toString().substring(0, 10);
			//ȡ��ǰʱ��
			String nowDate = SystemTool.getInstance().getDate().toString().substring(0, 10);
			//��ѯ�˲����ĵ���Ժʱ��
			String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
			TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
			String inDate = inParm.getValue("IN_DATE", 0).substring(0, 10);
			if(execDate.compareTo(inDate)<0){
				this.messageBox("ִ��ʱ�䲻��С����Ժʱ��");
				return true;
			}else if(execDate.compareTo(nowDate)>0){
				this.messageBox("ִ��ʱ�䲻�ɴ��ڵ�ǰʱ��");
				return true;
			}
			//====��Ϊ����ҽ���޸�order��������Ӧ��ϸ���ִ��ʱ��
			if(orderSetCode.length()>0&&indvFlg.equals("N")){
				order.setFilter("");
				order.filter();
		    int orderRow = order.rowCount();
			for(int i = 0;i<orderRow;i++){
				String orderSetGroupNo1 = order.getItemString(i, "ORDERSET_GROUP_NO");
				String orderSetCode1 = order.getItemString(i, "ORDERSET_CODE");
				if(orderSetGroupNo.equals(orderSetGroupNo1)&&
						orderSetCode.equals(orderSetCode1)	){
//					this.messageBox("9999");
					order.setItem(i, "EXEC_DATE", this.onExecDateChange1(execDate));
				}
				
			}
			
			order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();	
//			System.out.println("222===++++===columnName columnName is ::"+columnName);
		}
		}
//		System.out.println("===++++===columnName columnName is ::"+columnName);
		if ("SCHD_CODE".equals(columnName)) {
//			System.out.println("�����Ԫ��ֵ  is���� "+node.getValue());
//			TTable table = (TTable) this.getComponent("MAINTABLE");
//			TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
//			.getComponent("SCHD_CODE");
//			combo_schd.setClncpathCode(clncpathCode);
//	        combo_schd.onQuery();
//	        table.addItem("SCHD_CODE", combo_schd);
		}
		return false;
	}
	public void onExecDateChange() throws java.text.ParseException{
		
		if(this.getValue("EXEC_DATE")==null||
				this.getValue("EXEC_DATE").toString().length()==0){
			this.messageBox("ִ��ʱ�䲻��Ϊ��");
			this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
			return ;
			
		}else{
			String execDate = this.getValue("EXEC_DATE").toString().substring(0, 10);
//			System.out.println("====+++====execDate execDate is ::"+execDate);
//			System.out.println("------------this.getValueStringis ::"+this.getValue("EXEC_DATE"));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				order.setExecDate(SystemTool.getInstance().getDate());
//				order.setExecDate(Timestamp.valueOf(this.getValueString("EXEC_DATE")));
			}else{
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				  format.setLenient(false);
				  //Ҫת���ַ��� str_test
//				  String str_test ="2011-04-24"; 
				  try {
				   Timestamp ts = new Timestamp(format.parse(execDate).getTime());
				   order.setExecDate(ts);
//				   System.out.println(ts.toString());
				  } catch (ParseException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
				  }
//				order.setExecDate(SystemTool.getInstance().getDate());
//				order.setExecDate(Timestamp.valueOf(execDate));
			}
			
			//ȡ��ǰʱ��
			String nowDate = SystemTool.getInstance().getDate().toString().substring(0, 10);
			//��ѯ�˲����ĵ���Ժʱ��
			String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
			TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
			String inDate = inParm.getValue("IN_DATE", 0).substring(0, 10);
			if(execDate.compareTo(inDate)<0){
				this.messageBox("ִ��ʱ�䲻��С����Ժʱ��");
				this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
				return ;
			}else if(execDate.compareTo(nowDate)>0){
				this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
				this.messageBox("ִ��ʱ�䲻�ɴ��ڵ�ǰʱ��");
				return ;
			}
		}
		
	}
          public Timestamp onExecDateChange1(String execDate) throws java.text.ParseException{
		
//			String execDate = this.getValue("EXEC_DATE").toString().substring(0, 10);
//			System.out.println("====+++====execDate execDate is ::"+execDate);
//			System.out.println("------------this.getValueStringis ::"+this.getValue("EXEC_DATE"));
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				  format.setLenient(false);
				  //Ҫת���ַ��� str_test
//				  String str_test ="2011-04-24"; 
				  try {
				   Timestamp ts = new Timestamp(format.parse(execDate).getTime());
				   order.setExecDate(ts);
//				   System.out.println(ts.toString());
				  } catch (ParseException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
				  }
//				order.setExecDate(SystemTool.getInstance().getDate());
//				order.setExecDate(Timestamp.valueOf(execDate));
				  return new Timestamp(format.parse(execDate).getTime());
			}
	/**
	 * datastore��ֵ�ı��¼�
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            Object
	 */
	public void onSetItemEvent(String columnName, Object value) {
		if (columnName.equals("TOT_AMT")) {
			
			int countTable = t.getRowCount();
			double payAmt = 0.00;
			for (int i = 0; i < countTable; i++) {
				//modify by wanglong 20120814 �޸��ܼ۵ļ��㷽ʽ���ܼ۵���ÿ��ҽ���ĵ��۱���С������λ�����������������������������Ľ��壩
				//payAmt = payAmt + t.getItemDouble(i, "TOT_AMT");
				payAmt = payAmt + StringTool.round(t.getItemDouble(i, "TOT_AMT"), 2);
			}
			setValue("OWN_AMT", payAmt);

		}
		
		
		if (columnName.equals("EXE_DEPT_CODE")) {
			int countTable = t.getRowCount();
			for (int i = 0; i < countTable; i++) {
				// �ɱ�����
				t.setItem(i, "COST_CENTER_CODE", t.getItemData(i,
						"EXE_DEPT_CODE"));
				t
						.setItem(i, "EXE_DEPT_CODE", t.getItemData(i,
								"EXE_DEPT_CODE"));
			}
		}
	}

	/**
	 * sysFeeģ����ѯ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ORDER_CODE");
			Vector d = (Vector) parm.getData("ORDER_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * sysFee��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ����sysfee�Ի������
		if (column != 0)
			return;
		//caowl 20130204  start=============	
		int selRow = this.getTTable("MAINTABLE").getSelectedRow();
		TParm existParm = this.getTTable("MAINTABLE").getDataStore().getRowParm(
				selRow);
		
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		//caowl  20130204 end ===============
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", this.getValueString("CAT1_TYPE"));
		// System.out.println("ҽ������" + getValueString("CAT1_TYPE"));
		// if (!"".equals(Operator.getRegion()))
		// parm.setData("REGION_CODE", Operator.getRegion());
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("SYS_FEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}
	
	
	/**
	 * �Ƿ��Ǽ���ҽ��
	 * 
	 * @param row
	 *            int
	 * @param buff   caowl 20130204
	 *            String
	 * @return boolean
	 */
	public boolean isOrderSet(TParm orderParm) {
		boolean falg = false;		
		if (!orderParm.getValue("ORDERSET_CODE").equals("") && orderParm.getValue("ORDERSET_CODE")!= null ) {
			falg = true;
		}		
		return falg;
	}

	/**
	 * �ش�ҽ������
	 * 
	 * @param parm
	 * @param ownPriceSingle
	 *            ===============pangben 2012-7-9
	 */
	private void newOrderTemp(TParm parm, int selectRow) {
	
		double ownPriceSingle = 0.00;
		if ("2".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE2");
		} else if ("3".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE3");
		} else
			ownPriceSingle = parm.getDouble("OWN_PRICE");
		
		OrderCode = parm.getValue("ORDER_CODE");  //add by huangtt 20130922
		ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);  //add by huangtt 20130922
		if (ownRate==-1) {
			this.messageBox("�������ۿ۳�������");
			return;
		}
		order.setOwnRate(ownRate); //add by huangtt 20131114

		t.acceptText();
		String cat1Type = parm.getValue("CAT1_TYPE");
		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
		int selRow = selectRow;
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = getValue("EXE_DEPT_CODE").toString();//===========modify by  20120810 caowl
		}
		t.setItem(selRow, "DS_FLG", "N");
		// �ж��Ƿ��Ǽ���ҽ��
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ����ҽ�����
			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));

			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			String orderCode = parm.getValue("ORDER_CODE");
			t.setItem(selRow, "ORDERSET_CODE", orderCode);
			t.setItem(selRow, "INDV_FLG", "N");
			t.setItem(selRow, "MEDI_QTY", 1);
			t.setItem(selRow, "TAKE_DAYS", 1);
			t.setItem(selRow, "DOSAGE_QTY", 1);
			order.setActive(selRow, true);
			TParm parmDetail = SYSOrderSetDetailTool.getInstance()
					.selectByOrderSetCodeNew(parm.getValue("ORDER_CODE"));
			 //System.out.println("ϸ������"+parmDetail);
			if (parmDetail.getErrCode() != 0) {
				this.messageBox("ȡ��ϸ�����ݴ���");
				return;
			}
			// �ɱ�����
			t.setItem(selRow, "COST_CENTER_CODE", execDept);
			// ����ҽ�����
			TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
			groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);
			// if (groupNo == 0 || groupNoParm.getCount("ORDERSET_GROUP_NO") ==
			// 0)
			// groupNo = 1;
			// else
			// groupNo++;
			// this.messageBox_(groupNo);
			t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
			double allOwnAmt = 0.00;
			for (int i = 0; i < parmDetail.getCount(); i++) {
				int row = order.insertRow();
				// �Է�ע��
				order.setItem(row, "OWN_FLG", "Y");
				// �շ�ע��
				order.setItem(row, "BILL_FLG", "Y");
				// �Ը�����
//				order.setItem(row, "OWN_RATE", 1);
				order.setItem(row, "OWN_RATE", ownRate);  //modify by huangtt 20130922
				// ҽ������
				order.setItem(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// Ƶ��
				order.setItem(row, "FREQ_CODE", "STAT");
				// ��ҩ��λ
				order.setItem(row, "MEDI_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// ��ҩ��λ
				order.setItem(row, "DOSAGE_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// �Էѵ���
				if ("2".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE2"));
				} else if ("3".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE3"));
				} else
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE"));

				// ҽ������
				order.setItem(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE"));
				// Ժ�ڷ��ô���
				order.setItem(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ����ҽ�����
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				// //��ѯ�վݷ��ô���
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
				// �վݷ��ô���
			/*	order.setItem(row, "REXP_CODE", hexpParm.getValue(
						"IPD_CHARGE_CODE", 0));*/
				order.setItem(row, "REXP_CODE", parmDetail.getValue(
						"IPD_CHARGE_CODE", i));

				order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
						"OPTITEM_CODE", i));
				order.setItem(row, "EXE_DEPT_CODE", execDept);
				order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
						"INSPAY_TYPE", i));
				order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
						"RPTTYPE_CODE", i));
				order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
						"DEGREE_CODE", i));
				order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ҽ�����
				order.setItem(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// ҽ��ϸ����
				order.setItem(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// ҽ������
				order.setItem(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				// �ɱ�����
				order.setItem(row, "COST_CENTER_CODE", execDept);
				order.setItem(row, "INDV_FLG", "Y");
				order.setItem(row, "ORDERSET_CODE", orderCode);
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				double ownPrice = 0.00;
				if ("2".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE2", i);
				} else if ("3".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE3", i);
				} else
					ownPrice = parmDetail.getDouble("OWN_PRICE", i);
				order.setItem(row, "OWN_PRICE", ownPrice);
				// double qty = parmDetail.getDouble("DOSAGE_QTY", i);
				order.setItem(row, "MEDI_QTY", parmDetail
						.getDouble("TOTQTY", i));
				order.setItem(row, "DISPENSE_QTY", parmDetail.getDouble(
						"TOTQTY", i));
				order.setItem(row, "TAKE_DAYS", 1);
				order.setItem(row, "DOSAGE_QTY", parmDetail.getDouble("TOTQTY",
						i));
				order.setItem(row, "MEDI_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setActive(row, true);
				allOwnAmt = allOwnAmt + ownPrice
						* parmDetail.getDouble("TOTQTY", i);
				if(this.getValueString("EXEC_DATE").length()==0||
						this.getValue("EXEC_DATE")==null){
					order.setItem(row, "EXEC_DATE", SystemTool.getInstance().getDate());
				}else{
					order.setItem(row, "EXEC_DATE",  this.getValue("EXEC_DATE"));
				}
//				order.setItem(row, "SCHD_CODE",getSchdCode());//=====ʱ��  yanjing 20140902

			}
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", allOwnAmt);
//			t.setItem(selRow, "TOT_AMT", allOwnAmt);
			t.setItem(selRow, "TOT_AMT", allOwnAmt*ownRate);  //modify by huangtt 20130922
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}

			order
					.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();

			// order.insertRow();
			t.setDSValue();
			onInsert(selRow);
		} else {
			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			if (null==hexpParm||null==hexpParm.getValue("IPD_CHARGE_CODE",0)||hexpParm.getValue("IPD_CHARGE_CODE",0).length()<=0) {
				this.messageBox("����շ���ϸ��������");
				return;
			}
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
			// parm.getValue("ORDER_CODE"),
			// serviceLevel);
			// if (ownRate < 0) {
			// return result.newErrParm( -1, "�Ը���������");
			// }
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);//modify by huangtt 20130922
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			// ����
			t.setItem(selRow, "MEDI_QTY", parm.getDouble("MEDI_QTY"));//��Ĭ��Ϊ1�޸�--xiongwg201050429
			// ����
			t.setItem(selRow, "TAKE_DAYS", 1);
			// ����
			t.setItem(selRow, "DOSAGE_QTY", parm.getDouble("MEDI_QTY"));//��Ĭ��Ϊ1�޸�--xiongwg201050429
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// �Է�
			t.setItem(selRow, "OWN_AMT", ownPriceSingle);
			// �ܼ�
//			t.setItem(selRow, "TOT_AMT", ownPriceSingle);
			t.setItem(selRow, "TOT_AMT", parm.getDouble("MEDI_QTY")*ownPriceSingle*ownRate);  //modify by huangtt 20130922
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// �ɱ�����
			t.setItem(selRow, "COST_CENTER_CODE", this
					.getValueString("EXE_DEPT_CODE"));
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			// ҽ��ϸ�������
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			
			// System.out.println("����ִ�е�λ"+order.getItemData(selRow,"EXE_DEPT_CODE"));
			order.setActive(selRow, true);
			onInsert(selRow);
		}
	}

	
    
	/**
	 * ����ҽ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		
		TParm parm = (TParm) obj;
//		 System.out.println("sysfee��Ϣ"+parm);
//		double ownPriceSingle = 0.00;
//		if ("2".equals(serviceLevel)) {
//			ownPriceSingle = parm.getDouble("OWN_PRICE2");
//		} else if ("3".equals(serviceLevel)) {
//			ownPriceSingle = parm.getDouble("OWN_PRICE3");
//		} else
//			ownPriceSingle = parm.getDouble("OWN_PRICE");

		// System.out.println("�¿���ҽ��"+parm);
		// t.acceptText();
		// String cat1Type = parm.getValue("CAT1_TYPE");
		// //״̬��
		// callFunction("UI|setSysStatus",
		// parm.getValue("ORDER_CODE") + parm.getValue("ORDER_DESC") +
		// parm.getValue("GOODS_DESC") + parm.getValue("DESCRIPTION") +
		// parm.getValue("SPECIFICATION"));
		// int selRow = t.getSelectedRow();
		// String execDept = parm.getValue("EXEC_DEPT_CODE");
		// if (StringUtil.isNullString(execDept)) {
		// execDept = Operator.getCostCenter();
		// }
		// t.setItem(selRow, "DS_FLG", "N");
		// //�ж��Ƿ��Ǽ���ҽ��
		// if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
		// //�Է�ע��
		// t.setItem(selRow, "OWN_FLG", "Y");
		// //�շ�ע��
		// t.setItem(selRow, "BILL_FLG", "Y");
		// //�Ը�����
		// t.setItem(selRow, "OWN_RATE", 1);
		// //Ƶ��
		// t.setItem(selRow, "FREQ_CODE", "STAT");
		// //ҽ������
		// t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
		// //��ҩ��λ
		// t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		// //��ҩ��λ
		// t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		// //ҽ������
		// t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
		// //Ժ�ڷ��ô���
		// t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		// //ҽ�����
		// t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		// //ҽ������
		// t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
		// //ҽ��ϸ����
		// t.setItem(selRow, "ORDER_CAT1_CODE",
		// parm.getValue("ORDER_CAT1_CODE"));
		// //����ҽ�����
		// t.setItem(selRow, "ORDERSET_GROUP_NO",
		// parm.getInt("ORDERSET_GROUP_NO"));
		//
		// //��ѯ�վݷ��ô���
		// TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
		// parm);
		// //�վݷ��ô���
		// t.setItem(selRow, "REXP_CODE",
		// hexpParm.getValue("IPD_CHARGE_CODE", 0));
		// String orderCode = parm.getValue("ORDER_CODE");
		// t.setItem(selRow, "ORDERSET_CODE", orderCode);
		// t.setItem(selRow, "INDV_FLG", "N");
		// t.setItem(selRow, "MEDI_QTY", 1);
		// t.setItem(selRow, "TAKE_DAYS", 1);
		// t.setItem(selRow, "DOSAGE_QTY", 1);
		// order.setActive(selRow, true);
		// TParm parmDetail = SYSOrderSetDetailTool.getInstance()
		// .selectByOrderSetCode(parm.getValue("ORDER_CODE"));
		// // System.out.println("ϸ������"+parmDetail);
		// if (parmDetail.getErrCode() != 0) {
		// this.messageBox("ȡ��ϸ�����ݴ���");
		// return;
		// }
		// //�ɱ�����
		// t.setItem(selRow, "COST_CENTER_CODE", execDept);
		// //����ҽ�����
		// TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
		// groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);
		// // if (groupNo == 0 || groupNoParm.getCount("ORDERSET_GROUP_NO") ==
		// 0)
		// // groupNo = 1;
		// // else
		// // groupNo++;
		// // this.messageBox_(groupNo);
		// t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
		// double allOwnAmt = 0.00;
		// for (int i = 0; i < parmDetail.getCount(); i++) {
		// int row = order.insertRow();
		// //�Է�ע��
		// order.setItem(row, "OWN_FLG", "Y");
		// //�շ�ע��
		// order.setItem(row, "BILL_FLG", "Y");
		// //�Ը�����
		// order.setItem(row, "OWN_RATE", 1);
		// //ҽ������
		// order.setItem(row, "ORDER_DESC",
		// parmDetail.getValue("ORDER_CODE", i));
		// //Ƶ��
		// order.setItem(row, "FREQ_CODE", "STAT");
		// //��ҩ��λ
		// order.setItem(row, "MEDI_UNIT",
		// parmDetail.getValue("UNIT_CODE"));
		// //��ҩ��λ
		// order.setItem(row, "DOSAGE_UNIT",
		// parmDetail.getValue("UNIT_CODE"));
		// //�Էѵ���
		// if ("2".equals(serviceLevel)) {
		// order.setItem(row, "OWN_PRICE",
		// parmDetail.getDouble("OWN_PRICE2"));
		// } else if ("3".equals(serviceLevel)) {
		// order.setItem(row, "OWN_PRICE",
		// parmDetail.getDouble("OWN_PRICE3"));
		// } else
		// order.setItem(row, "OWN_PRICE",
		// parmDetail.getDouble("OWN_PRICE"));
		//
		// //ҽ������
		// order.setItem(row, "NHI_PRICE",
		// parmDetail.getDouble("NHI_PRICE"));
		// //Ժ�ڷ��ô���
		// order.setItem(row, "HEXP_CODE",
		// parmDetail.getValue("CHARGE_HOSP_CODE", i));
		// //����ҽ�����
		// order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		// // //��ѯ�վݷ��ô���
		// // hexpParm =
		// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
		// //�վݷ��ô���
		// order.setItem(row, "REXP_CODE",
		// hexpParm.getValue("IPD_CHARGE_CODE", 0));
		//
		// order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
		// "OPTITEM_CODE", i));
		// order.setItem(row, "EXE_DEPT_CODE", execDept);
		// order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
		// "INSPAY_TYPE", i));
		// order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
		// "RPTTYPE_CODE", i));
		// order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
		// "DEGREE_CODE", i));
		// order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
		// "CHARGE_HOSP_CODE", i));
		// //ҽ�����
		// order.setItem(row, "CAT1_TYPE",
		// parmDetail.getValue("CAT1_TYPE", i));
		// //ҽ��ϸ����
		// order.setItem(row, "ORDER_CAT1_CODE",
		// parmDetail.getValue("ORDER_CAT1_CODE", i));
		// //ҽ������
		// order.setItem(row, "ORDER_CHN_DESC",
		// parmDetail.getValue("ORDER_DESC", i));
		// //�ɱ�����
		// order.setItem(row, "COST_CENTER_CODE", execDept);
		// order.setItem(row, "INDV_FLG", "Y");
		// order.setItem(row, "ORDERSET_CODE", orderCode);
		// order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		// double ownPrice = 0.00;
		// if ("2".equals(serviceLevel)) {
		// ownPrice = parmDetail.getDouble("OWN_PRICE2", i);
		// } else if ("3".equals(serviceLevel)) {
		// ownPrice = parmDetail.getDouble("OWN_PRICE3", i);
		// } else
		// ownPrice = parmDetail.getDouble("OWN_PRICE", i);
		// order.setItem(row, "OWN_PRICE", ownPrice);
		// // double qty = parmDetail.getDouble("DOSAGE_QTY", i);
		// order.setItem(row, "MEDI_QTY", parmDetail.getDouble("TOTQTY", i));
		// order.setItem(row, "DISPENSE_QTY",
		// parmDetail.getDouble("TOTQTY", i));
		// order.setItem(row, "TAKE_DAYS", 1);
		// order.setItem(row, "DOSAGE_QTY",
		// parmDetail.getDouble("TOTQTY", i));
		// order.setItem(row, "MEDI_UNIT", parmDetail.getValue(
		// "UNIT_CODE", i));
		// order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue(
		// "UNIT_CODE", i));
		// order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
		// "UNIT_CODE", i));
		// order.setActive(row, true);
		// allOwnAmt = allOwnAmt +
		// ownPrice * parmDetail.getDouble("TOTQTY", i);
		//
		// }
		// //�Էѵ���
		// t.setItem(selRow, "OWN_PRICE", allOwnAmt);
		// t.setItem(selRow, "TOT_AMT", allOwnAmt);
		// order.setFilter(
		// "ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
		// order.filter();
		//
		// // order.insertRow();
		// t.setDSValue();
		// onInsert(selRow);
		// } else {
		//
		// //�Է�ע��
		// t.setItem(selRow, "OWN_FLG", "Y");
		// // double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
		// // parm.getValue("ORDER_CODE"),
		// // serviceLevel);
		// // if (ownRate < 0) {
		// // return result.newErrParm( -1, "�Ը���������");
		// // }
		// //�Ը�����
		// t.setItem(selRow, "OWN_RATE", 1);
		// //�շ�ע��
		// t.setItem(selRow, "BILL_FLG", "Y");
		// //Ƶ��
		// t.setItem(selRow, "FREQ_CODE", "STAT");
		// //����
		// t.setItem(selRow, "MEDI_QTY", 1);
		// //����
		// t.setItem(selRow, "TAKE_DAYS", 1);
		// //����
		// t.setItem(selRow, "DOSAGE_QTY", 1);
		// //ҽ������
		// t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
		// //��ҩ��λ
		// t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		// //��ҩ��λ
		// t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		// //�Էѵ���
		// t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
		// //�Է�
		// t.setItem(selRow, "OWN_AMT", ownPriceSingle);
		// //�ܼ�
		// t.setItem(selRow, "TOT_AMT", ownPriceSingle);
		// //ҽ������
		// t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
		// //Ժ�ڷ��ô���
		// t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		// //ҽ��ϸ����
		// t.setItem(selRow, "ORDER_CAT1_CODE",
		// parm.getValue("ORDER_CAT1_CODE"));
		// //ҽ������
		// t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
		// //�ɱ�����
		// t.setItem(selRow, "COST_CENTER_CODE",
		// this.getValueString("EXE_DEPT_CODE"));
		// t.setItem(selRow, "EXE_DEPT_CODE", execDept);
		// //ҽ��ϸ�������
		// t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		// //��ѯ�վݷ��ô���
		// TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
		// parm);
		// //�վݷ��ô���
		// t.setItem(selRow, "REXP_CODE",
		// hexpParm.getValue("IPD_CHARGE_CODE", 0));
		// //
		// System.out.println("����ִ�е�λ"+order.getItemData(selRow,"EXE_DEPT_CODE"));
		// order.setActive(selRow, true);
		// onInsert(selRow);
		// }
		newOrderTemp(parm, t.getSelectedRow());
	}

	/**
	 * �ɲ�������Ĳ�ѯ���� �ٴθ�ֵ����
	 */
	public void onInitReset() {
		// ��ʼ��ҳ��
		this.initPage();
	}

	/**
	 * ����Ƽ�ȡ��ҽ�����
	 * 
	 * @return String
	 */
	private String getNo() {
		if (orderNo == null) {
			orderNo = SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO",
					"ORDER_NO");
		}
		return orderNo;
	}

	/**
	 * ��ʼ��ҳ��(�Զ���)
	 */
	public void initPage() {
		TParm parm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			parm = (TParm) obj;
//			 //�ж��ǻ�ʿ������
//			 if (parm.existGroup("INWEXE")) {
//			 }
//			 if(this.getPopedem("INWLEAD")){
//				 this.messageBox("00000000000");
//				 
//			 }
//			System.out.println("��ʼ�� ��ʼ�� parm parm is����"+parm);
			t = (TTable) this.getComponent("MAINTABLE");
			if (null!=parm &&null!=parm.getData("IBS", "INWLEAD_FLG")) {
				if(parm.getData("IBS", "INWLEAD_FLG").toString().equals("true")){//��ʿ��Ȩ��
					callFunction("UI|EXEC_DATE|setEnabled", true);
					t.setLockColumns("2,7,8,9,13,14,15,16,18,19");
				}else{//һ��Ȩ��
					callFunction("UI|EXEC_DATE|setEnabled", false);
					t.setLockColumns("2,7,8,9,13,14,15,16,17,18,19");
				}
			}else{
				callFunction("UI|EXEC_DATE|setEnabled", false);
				t.setLockColumns("2,7,8,9,13,14,15,16,17,18,19");
			}
			
//			t = (TTable) this.getComponent("MAINTABLE");
//			String role = Operator.getRole();
//			 String groupCode = "IBS01";
//			 String code = "INWLEAD";
//			if( SYSRolePopedomTool.getInstance().getPopedom(role, groupCode, code)){
//				callFunction("UI|EXEC_DATE|setEnabled", true);
//				t.setLockColumns("2,7,8,9,13,14,15,16,18");
//			}else{
//				callFunction("UI|EXEC_DATE|setEnabled", false);
//				t.setLockColumns("2,7,8,9,13,14,15,16,17,18");
//			}
			caseNo = parm.getData("IBS", "CASE_NO").toString();
			// if (parm.getData("IBS", "TYPE") != null)
			// sysType = parm.getData("IBS", "TYPE").toString();
			// ==========pangben 2012-7-9 start
			
			
			String schdCodeInit = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"'";
			//=========pangben 2015-10-16 �޸����²�ѯ�ٴ�·������
			TParm schdCodeParm = new TParm(TJDODBTool.getInstance().select(schdCodeInit));
			schdCode = schdCodeParm.getValue("SCHD_CODE", 0);// ʱ�̴���
			clncpathCode = schdCodeParm.getValue("CLNCPATH_CODE", 0);// �ٴ�·������
//			schdCode = parm.getData("IBS", "SCHD_CODE").toString();// ʱ�̴���
			//===modify by caowl 20121126 start
//			this.SCHD_CODE = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
//			SCHD_CODE.setSqlFlg("Y");
//			SCHD_CODE.setClncpathCode(schdCode);
//			SCHD_CODE.onQuery();
			//===modify by caowl 20121126 end 
			
			
		}
		// ===========pangben 2012-7-9 �ٴ�·������ʱ����ʾ
		if (null != clncpathCode && clncpathCode.length() > 0) {
			callFunction("UI|clpOrderQuote|setEnabled", true);
		} else {
			callFunction("UI|clpOrderQuote|setEnabled", false);
		}
		if (parm.getData("IBS", "TYPE") != null) {
			callFunction("UI|close|setEnabled", false);
		}
		if (caseNo == null || caseNo.length() == 0) {
			this.messageBox("����������");
			return;
		}
		ipdNo = parm.getData("IBS", "IPD_NO").toString();
		mrNo = parm.getData("IBS", "MR_NO").toString();
		bedNo = parm.getData("IBS", "BED_NO").toString();
		setValue("CAT1_TYPE", "");
		// ��ʼ����ǰtable
		t = (TTable) this.getComponent("MAINTABLE");
		order = new IBSOrderD(caseNo);
		// �������
		order.setCaseNo(caseNo);
		// �������
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		//this.messageBox("��������������������������������"+(maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		// ��Ч����(��ʱҽ��)
		order.setBeginDate(SystemTool.getInstance().getDate());
		// ��Ч����(��ʱҽ��)
		order.setEndDate(SystemTool.getInstance().getDate());
		// �Ƽ�����
		order.setBillDate(SystemTool.getInstance().getDate());
		// ִ������
		order.setExecDate(SystemTool.getInstance().getDate());
		// ҽ������
		order.setCat1Type(getValue("CAT1_TYPE").toString());
		// ��������
		order.setDeptCode(parm.getValue("IBS", "DEPT_CODE"));//���ò�ѯ���������������-xiongwg20150619
//		order.setDeptCode(Operator.getDept());
		// ��������
		order.setStationCode(parm.getValue("IBS", "STATION_CODE"));
		// ����ҽʦ
		order.setDrCode(parm.getValue("IBS", "VS_DR_CODE"));
		// ִ�п���
		order.setExeDeptCode(Operator.getCostCenter());
		// ִ�в���
		order.setExeStationCode(parm.getValue("IBS", "STATION_CODE"));
		// ִ��ҽʦ
		order.setExeDrCode(Operator.getID());
		// �ɱ�����
		order.setCostCenterCode(Operator.getCostCenter());
		// ������Ա
		order.setOptUser(Operator.getID());
		// ��������
		order.setOptDate(SystemTool.getInstance().getDate());
		// �����ն�
		order.setOptTerm(Operator.getIP());
		//===caowl 20121126 start
		//ʱ��
//		order.setSchdCode(schdCode);
//		order.setSchdCode(SCHD_CODE.getComboValue());
		order.setSchdCode(schdCode);
		//�ٴ�·��
		order.setClncpathCode(clncpathCode);
//		order.setClncpathCode(schdCode);
		//===caowl 20121126 end
		order.onQuery();
		// ��datastore
		t.setDataStore(order);
		this.onInsert(0);
		t.setDSValue();
		// �Ƽ�����
		setValue("BILL_DATE", SystemTool.getInstance().getDate());
		setValue("EXEC_DATE", SystemTool.getInstance().getDate());//===yanjing 20140805 ���ִ��ʱ���ֶ�
		// --------���εõ�--------
		// System.out.println("����Ƽۿ�������"+parm.getData("IBS", "STATION_CODE"));
		// ��������
		setValue("STATION_CODE", parm.getData("IBS", "STATION_CODE"));
		setValue("SCHD_CODE", schdCode);
		setValue("SCHD_CODE1", schdCode);
		setValue("CLNCPATH_CODE", clncpathCode);
		// ��������
		setValue("DEPT_CODE", parm.getData("IBS", "DEPT_CODE"));//���ò�ѯ���������������-xiongwg20150619
//		setValue("DEPT_CODE", Operator.getDept());
		// ����ҽ��
		setValue("DR_CODE", parm.getData("IBS", "VS_DR_CODE"));
		// סԺ��
		setValue("IPD_NO", parm.getData("IBS", "IPD_NO"));
		// Ĭ�ϺͿ�����ͬ
		// ִ�в���
		setValue("EXE_STATION_CODE", getValue("STATION_CODE"));
		// String deptCode = Operator.getDept();
		// TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
		// //ִ�п���
		// if (deptParm.getCount("DEPT_CODE") > 0)
		// setValue("EXE_DEPT_CODE",
		// DeptTool.getInstance().getCostCenter(this.
		// getValueString("DEPT_CODE"), this.getValueString("STATION_CODE")));
		// else
		// setValue("EXE_DEPT_CODE", "");
		setValue("EXE_DEPT_CODE", Operator.getCostCenter());
		// ִ��ҽ��
		setValue("EXE_DR_CODE", Operator.getID());
		// �Ʒѽ��
		setValue("OWN_AMT", "");
		TParm admParm = new TParm();
		admParm.setData("CASE_NO", caseNo);
		TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);
		serviceLevel = selAdmParm.getValue("SERVICE_LEVEL", 0);
		//add by huangtt start  20130922		
		ctz1Code = selAdmParm.getValue("CTZ1_CODE",0);
		ctz2Code = selAdmParm.getValue("CTZ2_CODE",0);
		ctz3Code = selAdmParm.getValue("CTZ3_CODE",0);
		//add by huangtt end 20130922
		String billStatus = selAdmParm.getValue("BILL_STATUS", 0);
		// ��Ժ�ѽ���״̬���˵������״̬
		if ("4".equals(billStatus) || "3".equals(billStatus)) {
			this.messageBox("�˲����ѳ�Ժ,�����ٿ���ҽ��");
			callFunction("UI|save|setEnabled", false);
		}
        TTextFormat OPBOOK_SEQ = (TTextFormat) this.getComponent("OPBOOK_SEQ");//wanglong add 20141010 �������뵥��
        String opBookSeqSql =
                " SELECT OPBOOK_SEQ ID FROM OPE_OPBOOK WHERE CASE_NO='#' ORDER BY OPBOOK_SEQ ";
        opBookSeqSql = opBookSeqSql.replaceFirst("#", caseNo);
        OPBOOK_SEQ.setPopupMenuSQL(opBookSeqSql);
        OPBOOK_SEQ.onQuery();
        // ��Ӧ״̬ʱ��
        this.setValue("STATE_TIME", SystemTool.getInstance().getDate());
	}
	 /**
	 * �ٴ�·���ؼ������¼�
	 */
//	public void onClickClncpathCode() {
//		TTable table = (TTable) this.getComponent("MAINTABLE");
//		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
//				.getComponent("SCHD_CODE");
////		System.out.println("111clncpathCode clncpathCode is ::"+clncpathCode);
//		if (clncpathCode.length() > 0) {
//			System.out.println("111111111"+clncpathCode);
//			combo_schd.setClncpathCode(clncpathCode);
//		}
//		System.out.println("2222222222222");
//		combo_schd.onQuery();
//		table.addItem("SCHD_CODE", combo_schd);
//
//	}
	
	public void getinPage(){
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		t.setDataStore(order);
	}
   
	// /**
	// * �ر��¼�
	// * @return boolean
	// */
	// public boolean onClosing() {
	// switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
	// case 0:
	// if (!onSave())
	// return false;
	// break;
	// case 1:
	// break;
	// case 2:
	// return false;
	// }
	// return true;
	// }

	// /**
	// * �ر�
	// * @return Object
	// */
	// public Object onClosePanel() {
	// if (sysType.equals("BMS")) {
	// this.closeWindow();
	// return "OK";
	// }
	// return null;
	// }

	/**
	 * ���ҽ��
	 * 
	 * @return TParm(У����Ϣ)
	 */
	public TParm checkOrderSave() {
		TParm result = new TParm();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		// �¼ӵ�����
		int newRow[] = order.getNewRows(buff);

		TParm tempParm=new TParm();
		double dosageQty=0;
		boolean arrayFlg=false;
		for (int i : newRow) {
			if (!order.isActive(i, buff))
				continue;
			if (order.getRowParm(i, buff).getValue("ORDERSET_CODE").
					equals(order.getRowParm(i, buff).getValue("ORDER_CODE"))
					&&order.getRowParm(i, buff).getValue("ORDERSET_CODE").length()>0) {
				continue;
			}
			for (int j = 0; j < tempParm.getCount("ORDER_CODE"); j++) {
				
				if (tempParm.getValue("ORDER_CODE",j).
						equals(order.getRowParm(i, buff).getValue("ORDER_CODE"))) {
					arrayFlg=true;
				}
			}
			if(!arrayFlg){
				tempParm.addData("ORDER_CODE", order.getRowParm(i, buff).getValue("ORDER_CODE"));
				tempParm.addData("ORDER_CHN_DESC", order.getRowParm(i, buff).getValue("ORDER_CHN_DESC"));
				arrayFlg=false;
			}
		}
		for(int j=0;j<tempParm.getCount("ORDER_CODE");j++){
			for (int i : newRow) {
				if (!order.isActive(i, buff))
					continue;
				if (tempParm.getValue("ORDER_CODE",j).equals(order.getRowParm(i, buff).getValue("ORDER_CODE"))) {
					dosageQty+=order.getRowParm(i, buff).getDouble("DOSAGE_QTY");
				}
			}
			tempParm.setData("DOSAGE_QTY",j,dosageQty);
			dosageQty=0;
		}
//		System.out.println("tempParm::::"+tempParm);
		for (int k = 0; k < tempParm.getCount("ORDER_CODE"); k++) {
			if (tempParm.getDouble("DOSAGE_QTY", k) < 0) {
				String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
						+ "   FROM IBS_ORDD "
						+ "  WHERE ORDER_CODE = '"
						+ tempParm.getValue("ORDER_CODE", k)
						+ "' "
						+ "    AND CASE_NO = '"
						+ caseNo
						+ "' "
						+ "  GROUP BY ORDER_CODE ";
				TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(
						selQtySql));
				double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
				// System.out.println("selQtySql:::"+selQtySql);
				if (Math.abs(tempParm.getDouble("DOSAGE_QTY", k)) > dosageQtyTot) {
					String orderDesc = tempParm.getValue("ORDER_CHN_DESC", k);
					result.setErr(-1, "'"+orderDesc+"'"+"�˷����������ϼ�����,���ܱ���");
					return result;
				}
			}
		}
		for (int i : newRow) {
			if (!order.isActive(i, buff))
				continue;
			// ���Ӵ������λ�ܿ�-------start-------- wangl modify
			String orderCode = order.getRowParm(i, buff).getValue("ORDER_CODE");
			String orderCodeSql = " SELECT ORDER_CODE,ORDER_DESC,SPECIFICATION,DR_ORDER_FLG "
					+ "   FROM SYS_FEE "
					+ "  WHERE ORDER_CODE = '"
					+ orderCode
					+ "'";
			// System.out.println("����Ȩ�޲�ѯsql" + orderCodeSql);
			TParm orderCodeParm = new TParm(TJDODBTool.getInstance().select(
					orderCodeSql));
			boolean drOrderFlg = orderCodeParm.getBoolean("DR_ORDER_FLG", 0);
			if (drOrderFlg) {
				this.messageBox(orderCodeParm.getValue("ORDER_DESC", 0) + "||"
						+ orderCodeParm.getValue("SPECIFICATION", 0)
						+ "Ϊҽʦ����ҩ,���ÿ���");
			}
			// ���Ӵ������λ�ܿ�-------end-------- wangl modify

			// �����˷������ܿ�
//			dosageQty = order.getRowParm(i, buff)
//					.getDouble("DOSAGE_QTY");
//			if (dosageQty < 0) {
//				
//				
////                TParm filterParm = order.getBuffer(order.FILTER);//wanglong add 20150316
// //               dosageQty = 0;
////                for (int j = 0; j < filterParm.getCount(); j++) {
////                    if (orderCode.equals(filterParm.getValue("ORDER_CODE", j))) {
////                        dosageQty += filterParm.getDouble("DOSAGE_QTY", j);
////                    }
////                }
//				dosageQty = 0;
//				for (int i1 : newRow) {//xiongwg20150511,�ѼƷѵķ�����ҽ��δ���˷ѹܿ�����
//					String order_Code = "";
//					String orderSetCode = order.getRowParm(i1, buff).getValue("ORDERSET_CODE");
//					String cat1Type = order.getRowParm(i1, buff).getValue("CAT1_TYPE");
//					if(!cat1Type.equals("PHA")){
//						for(int j=0;j<feeDetailData.getCount("ORDER_CODE");j++){
//							order_Code = feeDetailData.getValue("ORDER_CODE", j);
//							if(orderCode.equals(orderSetCode)){
//								dosageQty+=order.getRowParm(i1, buff).getDouble("DOSAGE_QTY");
//							}	
//						}						
//					}else{
//						dosageQty+=order.getRowParm(i1, buff).getDouble("DOSAGE_QTY");
//					}
//				}              
//				String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
//						+ "   FROM IBS_ORDD "
//						+ "  WHERE ORDER_CODE = '"
//						+ orderCode
//						+ "' "
//						+ "    AND CASE_NO = '"
//						+ caseNo
//						+ "' " + "  GROUP BY ORDER_CODE ";
//				TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(
//						selQtySql));
//				double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
//				if (Math.abs(dosageQty) > dosageQtyTot) {
//					result.setErr(-1, "�˷����������ϼ�����,���ܱ���");
//					return result;
//				}
//			}
			if (order.getRowParm(i, buff).getValue("ORDERSET_CODE") == null
					|| order.getRowParm(i, buff).getValue("ORDERSET_CODE")
							.length() == 0) {
				// ����
				if (order.getRowParm(i, buff).getDouble("MEDI_QTY") == 0) {
					// this.messageBox_(ds.getRowParm(i,buff).getDouble("MEDI_QTY")+"==="+i);
					result.setErrCode(-1);
					result.setErrText(order.getRowParm(i, buff).getValue(
							"ORDER_DESC")
							+ "��������Ϊ:0");
					result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
							buff).getValue("ORDER_CODE"));
					return result;
				}
				//caowl 20130130 start
				 //Ƶ��
				 if (order.getRowParm(i, buff).getValue("FREQ_CODE").length()
				 ==
				 0) {
				 result.setErrCode( -2);
				 result.setErrText(order.getRowParm(i,
				 buff).getValue("ORDER_DESC") +
				 "ҽ��Ƶ�β�����Ϊ��");
				 result.setData("ERR", "ORDER_CODE",
				 order.getRowParm(i,
				 buff).getValue("ORDER_CODE"));
				 return result;
				 }
				 //caowl 20130130 end
				// ����
				if (order.getRowParm(i, buff).getInt("TAKE_DAYS") == 0) {
					result.setErrCode(-3);
					result.setErrText(order.getRowParm(i, buff).getValue(
							"ORDER_DESC")
							+ "ҽ������������Ϊ0");
					result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
							buff).getValue("ORDER_CODE"));
					return result;
				}

			}
			// ִ�п���
			if (order.getRowParm(i, buff).getValue("EXE_DEPT_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "ִ�п��Ҳ���Ϊ��");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// ��������
			if (order.getRowParm(i, buff).getValue("DEPT_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "�������Ҳ���Ϊ��");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// ҽ������
			if (order.getRowParm(i, buff).getValue("ORDER_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "ҽ�����벻��Ϊ��");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// System.out.println("����"+order.getRowParm(i,
			// buff).getDouble("DOSAGE_QTY"));
			//caowl 20130130 start ��������Ϊ��
			 //����
			 if (order.getRowParm(i, buff).getDouble("DOSAGE_QTY") == 0) {
			 result.setErrCode( -6);
			 result.setErrText(order.getRowParm(i,
			 buff).getValue("ORDER_DESC") + "��������Ϊ0");
			 result.setData("ERR", "ORDER_CODE",
			 order.getRowParm(i, buff).getValue("ORDER_CODE"));
			 return result;
			 }
			 //caowl 20130130 end
			// ��˿��
			// if("PHA".equals(ds.getRowParm(i,buff).getValue("CAT1_TYPE"))){
			// if(!INDTool.getInstance().inspectIndStock(ds.getRowParm(i,buff).getValue("EXEC_DEPT_CODE"),ds.getRowParm(i,buff).getValue("ORDER_CODE"),
			// ds.getRowParm(i,buff).getDouble("DOSAGE_QTY"))){
			// result.setErrCode(-5);
			// result.setErrText(ds.getRowParm(i,buff).getValue("ORDER_DESC")+"��治�㣡");
			// result.setData("ERR","INDEX",index.get(ds.getRowParm(i,buff).getValue("RX_KIND")));
			// result.setData("ERR","ORDER_CODE",ds.getRowParm(i,buff).getValue("ORDER_CODE"));
			// return result;
			// }
			// }
		}
		return result;
	}

	/**
	 * ����
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		// if (!checkFee(caseNo)) {
		// this.messageBox("����!");
		// return false;
		// }
		t.acceptText();
		double newTotamt = TypeTool.getDouble(getValue("OWN_AMT"));
		 getinPage();
		//this.messageBox(""+newTotamt);
		// ���
		TParm checkParm = checkOrderSave();
		if (checkParm.getErrCode() < 0) {
			this.messageBox(checkParm.getErrText());
			return false;
		}
		//==pangben 2016-9-13
		try {
			IBSTool.getInstance().onCheckClpDiff(caseNo);
		} catch (Exception e) {
			// TODO: handle exception
		}
		// ҽ�����
		order.setOrderNo(getNo());
		order.setFilter("");
		order.filter();
		int max = 1;
		int rowPCount = order.rowCount();
		String ordCode="";
		boolean flag=false;
		TParm sresult=new TParm();
		for (int i = 0; i < rowPCount; i++) {
	    //this.messageBox("xxxxxxx"+t.getItemString(i, "ORDER_CHN_DESC"));
			
			//$------------start caoyong 20131111----------------
			ordCode=t.getItemString(i, "ORDER_CODE");
			if(ordCode.length()>0){
			sresult=this.getOrderCode(ordCode);
           
			if(sresult.getValue("ORDER_CODE",0).length()<=0){
				 this.setOrderDesc(t.getItemString(i, "ORDER_CODE"));
				 flag=true;
				 break;
			}
			}
			//$------------end caoyong 20131111----------------
			
			// ҽ��˳���
			max++;
			t.setItem(i, "ORDER_SEQ", "" + max);
			
			order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();

		}
		////$------------start caoyong 20131111----------------
		if(flag){//t.getItemString(i, "ORDER_DESC")+
			 this.messageBox(this.getOrderDesc()+"ҽ�������ڣ�������ѡ��");
			  return false;
		}
		//$------------end caoyong 2013111----------------
		String buffer = order.isFilter() ? order.FILTER : order.PRIMARY;
		// �˵������
		int[] countArray = order.getNewRows(buffer);
		int seqNo = 1;
		for (int i : countArray) {
			order.setItem(countArray[i], "SEQ_NO", seqNo, buffer);
			seqNo++;
		}
		//==========modify by caowl 20120911 start
		// �������
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		
		//this.messageBox("=====��������======="+(maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		//=========modify by caowl 20120911 end
		order.setFilter("");
		int rowNewCount[] = order.getNewRows();
		for (int temp : rowNewCount) {
			TParm parmTemp = order.getRowParm(temp);
			if ("N".equals(parmTemp.getValue("INDV_FLG"))
					&& parmTemp.getValue("ORDERSET_CODE").length() != 0) {
				order.setItem(temp, "TOT_AMT", 0);
				order.setItem(temp, "OWN_PRICE", 0);
			}
			order.setItem(temp, "CASE_NO_SEQ", order.getCaseNoSeq());//add caoyong ���¸�ֵ����������ͻ
			//order.seti
		}
		
		String[] sql = order.getUpdateSQL();
		String[] insertOrdM = new String[2];
		//===zhangp 20130131 start
//		String selADMSql = " SELECT TOTAL_AMT,CUR_AMT " + "   FROM ADM_INP "
//				+ "  WHERE CASE_NO = '" + order.getCaseNo() + "'";
		String selADMSql = " SELECT TOTAL_AMT,CUR_AMT,DEPT_CODE " + "   FROM ADM_INP "
				+ "  WHERE CASE_NO = '" + order.getCaseNo() + "'";
		TParm selADMParm = new TParm(TJDODBTool.getInstance().select(selADMSql));
		String deptCode = selADMParm.getValue("DEPT_CODE", 0);
		if(deptCode.length()==0){
			deptCode = order.getDeptCode();
		}
		//===zhangp 20130131 end
		double oleTotalAmt = selADMParm.getDouble("TOTAL_AMT", 0);
		// System.out.println("���ܼ�"+oleTotalAmt);
		double oleCurAmt = selADMParm.getDouble("CUR_AMT", 0);
		// System.out.println("��ʣ����"+oleTotalAmt);
		double newTotalAmt = oleTotalAmt + newTotamt;
		// System.out.println("�½�����"+newTotamt);
		double newCurAmt = oleCurAmt - newTotamt;
		// System.out.println("������Ա����"+Operator.getRegion() );
		insertOrdM[0] = " INSERT INTO IBS_ORDM (CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,MR_NO,"
				+ "             DEPT_CODE,STATION_CODE,BED_NO,DATA_TYPE,BILL_NO,"
				+ "             OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE)"
				+ "      VALUES ('"
				+ order.getCaseNo()
				+ "','"
				+ order.getCaseNoSeq()
				+ "',TO_DATE('"
				+ StringTool.getString(order.getBillDate(),
						"yyyy/MM/dd HH:mm:ss")
				+ "','YYYY/MM/DD HH24:MI:SS'),'"
				+ ipdNo
				+ "','"
				+ mrNo
				+ "',"
				+ "             '"
				//====zhangp 20130131 start
//				+ order.getDeptCode()
				+ deptCode
				//====zhangp 20130131 end
				+ "','"
				+ order.getStationCode()
				+ "','"
				+ bedNo
				+ "','"
				+ "1"
				+ "','"
				+ ""
				+ "','"
				+ order.getOptUser()
				+ "',SYSDATE,'"
				+ order.getOptTerm()
				+ "','"
				+ Operator.getRegion() + "')";
		insertOrdM[1] = " UPDATE ADM_INP SET TOTAL_AMT = '" + newTotalAmt
				+ "',CUR_AMT = '" + newCurAmt + "' " + "  WHERE CASE_NO = '"
				+ order.getCaseNo() + "'";

		sql = StringTool.copyArray(sql, insertOrdM);
        // wanglong add 20141010 ��������״̬������ʱ�䣬�������������
        int StateSeq = -1;
        String opBookSeq = this.getValueString("OPBOOK_SEQ");
        String opState = this.getValueString("OPE_STATE");
        if (!opBookSeq.equals("")) {
            String opBookSql = "SELECT TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '#'";
            opBookSql = opBookSql.replaceFirst("#", opBookSeq);
            TParm typeParm = new TParm(TJDODBTool.getInstance().select(opBookSql));
            if (typeParm.getErrCode() < 0) {
                this.messageBox("�����������ʧ�� " + typeParm.getErrText());
            } else if (typeParm.getCount() > 0) {
                if (opState.equals("")) {
                    this.messageBox("��ѡ������״̬");
                    return false;
                }
                if (this.getValueString("STATE_TIME").equals("")) {
                    this.messageBox("����д����ʱ��");
                    return false;
                }
                if (typeParm.getValue("TYPE_CODE", 0).equals("1")) {// 1����������
                    if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_3)",
                                            "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 7;
                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_2)",
                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 6;
                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_1)",
                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 5;
                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_0)",
                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 2;
                    }
                } else {// 1��������
                    if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_3)",
                                            "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 7;
                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_2)",
                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 5;
                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_1)",
                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 4;
                    } else if (!StringUtil.getDesc("OPE_OPBOOK", "TO_CHAR(STATE_TIME_0)",
                                                   "OPBOOK_SEQ='" + opBookSeq + "'").equals("")) {
                        StateSeq = 2;
                    }
                }
                Timestamp stateTime = (Timestamp) this.getValue("STATE_TIME");
                String updateOpBook = "";
                if (opState.equals("2")) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_0=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                } else if ((opState.equals("5") && typeParm.getValue("TYPE_CODE", 0).equals("1"))
                        || (opState.equals("4") && typeParm.getValue("TYPE_CODE", 0).equals("2"))) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_1=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                } else if ((opState.equals("6") && typeParm.getValue("TYPE_CODE", 0).equals("1"))
                        || (opState.equals("5") && typeParm.getValue("TYPE_CODE", 0).equals("2"))) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_2=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                } else if (opState.equals("7")) {
                    updateOpBook =
                            "UPDATE OPE_OPBOOK SET STATE='&',STATE_TIME_3=TO_DATE('@','YYYY/MM/DD HH24:MI:SS') WHERE OPBOOK_SEQ='#'";
                }
                if (!updateOpBook.equals("")) {
                    updateOpBook = updateOpBook.replaceFirst("&", opState);
                    updateOpBook =
                            updateOpBook.replaceFirst("@", StringTool
                                    .getString(stateTime, "yyyy/MM/dd HH:mm:ss"));
                    updateOpBook = updateOpBook.replaceFirst("#", opBookSeq);
                    sql = StringTool.copyArray(sql, new String[]{updateOpBook });
                }
            } else {
                this.messageBox("�������Ų�����");
            }
        }
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			
		if (result.getErrCode() < 0) {
			//caowl 20130320 start	   		    
			
			String errText = result.getErrText().substring(0,9);	
			if(errText.equals("ORA-00001")){
				this.messageBox("����һλ��ʿ���Դ˲��˽��мƷѲ�������رս������½���");
			}else{
				// ����ʧ��
				this.messageBox("E0001");
			}
			//caowl 20130320 end
		} else {
			// ����ɹ�
			this.messageBox("P0001");
            // �������ӿ� wanglong add 20141010
            if (!this.getValueString("OPBOOK_SEQ").equals("") && !(StateSeq + "").equals(opState)) {
                TParm xmlParm = ADMXMLTool.getInstance().creatOPEStateXMLFile(caseNo, opBookSeq);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
                }
            }
			result = ADMTool.getInstance().checkStopFee(caseNo);
			onInit();
		}
		return true;
	}
    /**
     * ��֤SYS_FEE�����Ƿ�����ӵ�ҽ��
     */
	public TParm getOrderCode(String ordCode){
		String sql="SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"+ordCode+"'";
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 return result;
	}
	/**
	 * У�����
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean checkFee(String caseNo) {
		TParm inSelADMAllData = new TParm();
		inSelADMAllData.setData("CASE_NO", caseNo);
		TParm selADMAllData = ADMInpTool.getInstance().selectall(
				inSelADMAllData);
		// System.out.println("У�����"+selADMAllData);
		if ("Y".equals(selADMAllData.getValue("STOP_BILL_FLG", 0)))
			return false;
		return true;
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		int row = t.getSelectedRow();
		TParm tableParm = t.getDataStore().getRowParm(row);
		// System.out.println("tableParm��ʾ����" + tableParm);
		int ordersetGroupNo = tableParm.getInt("ORDERSET_GROUP_NO");
		String ordersetCode = tableParm.getValue("ORDERSET_CODE");
		String orderCode = tableParm.getValue("ORDER_CODE");
		if (orderCode.length() == 0)
			return;
		if (orderCode.equals(ordersetCode)) {
			deleteDetialCode(ordersetGroupNo, ordersetCode);
			return;
		}
		t.getDataStore().deleteRow(row);
		t.setDSValue();
		order.showDebug();
		setTotAmt();
		// int allCount = t.getRowCount();
		// if (allCount - 1 != row && row >= 0) {
		// t.removeRow(row);
		// }
	}

	/**
	 * ɾ������ҽ��ϸ��
	 * 
	 * @param ordersetGroupNo
	 *            String
	 * @param ordersetCode
	 *            String
	 */
	public void deleteDetialCode(int ordersetGroupNo, String ordersetCode) {
		// String filterStr = "INDV_FLG='N' OR INDV_FLG IS NULL ";
		String filterStr = "INDV_FLG='N' OR INDV_FLG ='' ";
		t.setFilter("");
		t.filter();
		String buffer = order.isFilter() ? order.FILTER : order.PRIMARY;
		int rowCount = order.isFilter() ? order.rowCountFilter() : order
				.rowCount();
		order.showDebug();
		for (int i = rowCount - 1; i >= 0; i--) {
			if (!order.isActive(i, buffer))
				continue;
			TParm temp = order.getRowParm(i, buffer);
			if (temp.getInt("ORDERSET_GROUP_NO") == ordersetGroupNo) {
				order.deleteRow(i, buffer);
			}
		}
		t.setFilter(filterStr);
		t.filter();
		t.setDSValue();
		order.showDebug();
		t.getDataStore().showDebug();
		setTotAmt();
	}

	/**
	 * �һ�MENU�����¼�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		TParm parm = table.getShowParmValue();
		int row = table.getSelectedRow();
		if (parm.getData("ORDER_DESC", row) != null
				&& order.getItemString(row, "ORDERSET_CODE").length() > 0) {
			table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
			return;
		} else {
			table.setPopupMenuSyntax("");
			return;
		}
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		int row = table.getSelectedRow();
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		String orderCode = order.getItemString(row, "ORDER_CODE");
		// this.messageBox_("����ҽ��groupNo" + groupNo);
		// this.messageBox_("����ҽ��orderCode" + orderCode);
		TParm parm = getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		// System.out.println("����ҽ�����" + groupNo);
		// System.out.println("����ҽ�������" + orderSetCode);
		TParm result = new TParm();
		if (groupNo < 0) {
			System.out
					.println("OpdOrder->getOrderSetDetails->groupNo is invalie");
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return result;
		}
		TParm parm = order.getBuffer(order.isModified() ? order.FILTER
				: order.PRIMARY);

		// System.out.println("ҽ����Ϣ" + parm);
		int count = parm.getCount();
		if (count < 0) {
			System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			if (!orderSetCode.equals(parm.getValue("ORDER_CODE", i))
					&& parm.getBoolean("#ACTIVE#", i)
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// ��ѯ����
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// �����ܼ۸�
				double ownPrice = orderParm.getDouble("OWN_PRICE", 0)
						* parm.getDouble("MEDI_QTY", i);
				result
						.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE",
								0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("ORDER_DESC", orderParm
						.getValue("ORDER_DESC", 0));
				result.addData("SPECIFICATION", orderParm.getValue(
						"SPECIFICATION", 0));
				result.addData("EXEC_DEPT_CODE", parm.getValue("EXE_DEPT_CODE",
						i));
				result.addData("OPTITEM_CODE", orderParm.getValue(
						"OPTITEM_CODE", 0));
				result.addData("INSPAY_TYPE", orderParm.getValue("INSPAY_TYPE",
						0));
			}
		}
		return result;
	}

	/**
	 *ҽ�����ı�
	 */
	public void onChangeCat1Type() {
		if (t != null)
			t.acceptText();
	}

	public static void main(String[] args) {
		com.javahis.util.JavaHisDebug.TBuilder();
		System.out.println(TDataStore.helpEvent());

	}

	/**
	 * �õ���ʿվ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void getINWData(TParm parm) {
		// this.messageBox_("���뻤ʿִ��");
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.removeAll();
		// this.messageBox_("���ջ�ʿվ����" + parm);
	}

	/**
	 * ��ѯ����������
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public synchronized TParm  selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ��ѯ���ҽ��˳���
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm selMaxOrderSeq(String caseNo) {
		String sql = " SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM IBS_ORDD "
				+ "  WHERE CASE_NO = '" + caseNo + "' "
				+ "    AND ORDER_NO = '" + orderNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ��󼯺�ҽ�����
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm seleMaxOrdersetGroupNo(String caseNo) {
		String sql = " SELECT NVL(MAX(ORDERSET_GROUP_NO),0) AS ORDERSET_GROUP_NO FROM IBS_ORDD"
				+ "  WHERE CASE_NO = '" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return result;
		}
		int groupNo = result.getInt("ORDERSET_GROUP_NO", 0);
		TParm filterParm = order.getBuffer(order.isFilter() ? order.FILTER
				: order.PRIMARY);
		for (int i = 0; i < filterParm.getCount(); i++) {
			if (filterParm.getInt("ORDERSET_GROUP_NO", i) > groupNo) {
				groupNo = filterParm.getInt("ORDERSET_GROUP_NO", i);
			}
		}
		result.setData("ORDERSET_GROUP_NO", 0, groupNo + 1);
		return result;
	}

	/**
	 * �����Ҳ���Ʒ�
	 */
	public void onOperation() {
		operationParm = new TParm();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", parm,
				false);
		// System.out.println("��������Ƽ�" + operationParm);
		TParm parm_obj = new TParm();
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '"
					+ operationParm.getValue("ORDER_CODE", i) + "' ";
			parm_obj = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm_obj == null || parm_obj.getCount() <= 0) {
				continue;
			}
			insertNewOperationOrder(parm_obj.getRow(0), operationParm
					.getDouble("DOSAGE_QTY", i));
		}
	}

	/**
	 * �����ײͻش�����
	 * 
	 * @param parm
	 *            TParm
	 * @param dosage_qty
	 *            double
	 */
	private void insertNewOperationOrder(TParm parm, double dosage_qty) {
//		 System.out.println("parm---"+parm);
		// System.out.println("dosage_qty---"+dosage_qty);
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = getValue("EXE_DEPT_CODE").toString();//modify by caowl 20120810	
		}
		double ownPriceSingle = 0.00;
		if ("2".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE2");
		} else if ("3".equals(serviceLevel)) {
			ownPriceSingle = parm.getDouble("OWN_PRICE3");
		} else
			ownPriceSingle = parm.getDouble("OWN_PRICE");
		
		OrderCode = parm.getValue("ORDER_CODE");  //add by huangtt 20130922
		ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,OrderCode,serviceLevel);  //add by huangtt 20130922
		if (ownRate==-1) {
			this.messageBox("�������ۿ۳�������");
			return;
		}
		order.setOwnRate(ownRate); //add by huangtt 20131114	
		t.acceptText();
		// ״̬��
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
		int selRow = t.getRowCount() - 1;
		t.setSelectedRow(selRow);   //add by huangtt 20130922
		t.setItem(selRow, "DS_FLG", "N");
		// �ж��Ƿ��Ǽ���ҽ��
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {

			
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate); //modify by huangtt 20130922
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ����ҽ�����
			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));

			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			String orderCode = parm.getValue("ORDER_CODE");
			t.setItem(selRow, "ORDERSET_CODE", orderCode);
			t.setItem(selRow, "INDV_FLG", "N");
			t.setItem(selRow, "MEDI_QTY", 1);
			t.setItem(selRow, "TAKE_DAYS", 1);
			t.setItem(selRow, "DOSAGE_QTY", 1);
			order.setActive(selRow, true);
			TParm parmDetail = SYSOrderSetDetailTool.getInstance()
					.selectByOrderSetCodeNew(parm.getValue("ORDER_CODE"));
			if (parmDetail.getErrCode() != 0) {
				this.messageBox("ȡ��ϸ�����ݴ���");
				return;
			}
			// ����ҽ�����
			TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
			groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);

			t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
			double allOwnAmt = 0.00;
			for (int i = 0; i < parmDetail.getCount(); i++) {
				int row = order.insertRow();				
				
				// �Է�ע��
				order.setItem(row, "OWN_FLG", "Y");
				// ҽ������
				order.setItem(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// ҽ�����
				order.setItem(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// ҽ��ϸ����
				order.setItem(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// ҽ������
				order.setItem(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				// ��ҩ��λ
				order.setItem(row, "MEDI_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// ��ҩ��λ
				order.setItem(row, "DOSAGE_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				//Ƶ��
				t.setItem(selRow, "FREQ_CODE", "STAT");//============modify by caowl 20120810
				// �Էѵ���
				if ("2".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE2"));
				} else if ("3".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE3"));
				} else
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE"));

				// ҽ������
				order.setItem(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE"));
				// Ժ�ڷ��ô���
				order.setItem(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// ����ҽ�����
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				// //��ѯ�վݷ��ô���
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
				// �վݷ��ô���
	/*			order.setItem(row, "REXP_CODE", hexpParm.getValue(
						"IPD_CHARGE_CODE", 0));*/
				order.setItem(row, "REXP_CODE", parmDetail.getValue(
						"IPD_CHARGE_CODE", i));

				order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
						"OPTITEM_CODE", i));
				order.setItem(row, "EXE_DEPT_CODE", execDept);
				order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
						"INSPAY_TYPE", i));
				order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
						"RPTTYPE_CODE", i));
				order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
						"DEGREE_CODE", i));
				order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				order.setItem(row, "INDV_FLG", "Y");
				order.setItem(row, "ORDERSET_CODE", orderCode);
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				double ownPrice = 0.00;
				if ("2".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE2", i);
				} else if ("3".equals(serviceLevel)) {
					ownPrice = parmDetail.getDouble("OWN_PRICE3", i);
				} else
					ownPrice = parmDetail.getDouble("OWN_PRICE", i);
				order.setItem(row, "OWN_PRICE", ownPrice);
				// double qty = parmDetail.getDouble("DOSAGE_QTY", i);
				order.setItem(row, "MEDI_QTY", 1);
				order.setItem(row, "DISPENSE_QTY", 1);
				order.setItem(row, "TAKE_DAYS", 1);
				order.setItem(row, "DOSAGE_QTY", 1);
				order.setItem(row, "MEDI_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
						"UNIT_CODE", i));
				order.setActive(row, true);
				allOwnAmt = allOwnAmt + ownPrice;
				order.setItem(row, "SCHD_CODE",  order.getSchdCode());
				if(this.getValueString("EXEC_DATE").length()==0||
						this.getValue("EXEC_DATE")==null){
					order.setItem(row, "EXEC_DATE", SystemTool.getInstance().getDate());
				}else{
					order.setItem(row, "EXEC_DATE",  this.getValue("EXEC_DATE"));
				}
			}
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", allOwnAmt);
//			t.setItem(selRow, "TOT_AMT", allOwnAmt);
			t.setItem(selRow, "TOT_AMT", allOwnAmt*ownRate);//modify by huangtt 20130922
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			order
					.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();

			t.setDSValue();
			order.setItem(selRow, "MEDI_QTY", dosage_qty);
			order.setItem(selRow, "TAKE_DAYS", 1);
			order.setItem(selRow, "DOSAGE_QTY", dosage_qty);
			onInsert(selRow);
		} else {
			// ��ѯ�վݷ��ô���
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			if (null==hexpParm||null==hexpParm.getValue("IPD_CHARGE_CODE",0)||hexpParm.getValue("IPD_CHARGE_CODE",0).length()<=0) {
				this.messageBox("����շ���ϸ��������");
				return;
			}
			// �Է�ע��
			t.setItem(selRow, "OWN_FLG", "Y");
			// �շ�ע��
			t.setItem(selRow, "BILL_FLG", "Y");
			// �Ը�����
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// ҽ������
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// ҽ�����
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// ҽ������
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			//============modify by caowl 20120810 start
			//Ƶ��
			t.setItem(selRow, "FREQ_CODE", "STAT");
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			//============modify by caowl 20120810 end
			// ҽ��ϸ����
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// ��ҩ��λ
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// �Էѵ���
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// ҽ������
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// Ժ�ڷ��ô���
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			
			// �վݷ��ô���
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			order.setActive(selRow, true);
			order.setItem(selRow, "MEDI_QTY", dosage_qty);
			order.setItem(selRow, "TAKE_DAYS", 1);
			order.setItem(selRow, "DOSAGE_QTY", dosage_qty);
			onInsert(selRow);
		}
	}

	/**
	 * ���ò�ѯ
	 */
	public void onSelFee() {
		TParm parm = new TParm();
		parm.setData("IBS", "CASE_NO", caseNo);
		parm.setData("IBS", "MR_NO", mrNo);
		parm.setData("IBS", "TYPE", "IBS");
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
	}

	// ��table����һ��ҽ����
	public void onInsert(int selRow) {
		// �鿴�Ƿ���δ�༭��
		if (isNewRow()) {
			return;
		}
		order.insertRow();
		t.setDSValue();
		t.getTable().grabFocus();
		t.setSelectedRow(selRow);
		t.setSelectedColumn(1);
	}

	/**
	 * �Ƿ���δ�༭��
	 * 
	 * @return boolean
	 */
	public boolean isNewRow() {
		boolean falg = false;
		int rowCount = order.rowCount();
		for (int i = 0; i < rowCount; i++) {
			if (!order.isActive(i)) {
				falg = true;
				break;
			}
		}
		return falg;
	}

	/**
	 * ��ʿ�ײ�
	 */
	public void onNbwPackage() {
		// ��ʿ�ײ�
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "IBS");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 4);
		parm.setData("RULE_TYPE", 4);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		TWindow window = (TWindow) this.openDialog(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);
	}

	/**
	 * �����ײ�
	 */
	public void onDeptPackage() {
		// �����ײ�
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "IBS");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 3);
		parm.setData("RULE_TYPE", 4);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		TWindow window = (TWindow) this.openDialog(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);
	}

	/**
	 * �ײ͸�ֵ
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetList(Object obj) {
		boolean falg = true;
		if (obj != null) {
			List orderList = (ArrayList) obj;
			Iterator iter = orderList.iterator();
			while (iter.hasNext()) {
				TParm temp = (TParm) iter.next();
				// System.out.println("�ײͲ���"+temp);
				insertNewOperationOrder(temp, temp.getDouble("MEDI_QTY"));
			}
		}
		return falg;
	}

	/**
	 * ���¼����ܼ�
	 */
	public void setTotAmt() {
		int countTable = t.getRowCount();
		double payAmt = 0.0D;
		for (int i = 0; i < countTable; i++)
			//modify by wanglong 20120814 �޸��ܼ۵ļ��㷽ʽ���ܼ۵���ÿ��ҽ���ĵ��۱���С������λ�����������������������������Ľ��壩
			//payAmt += t.getItemDouble(i, "TOT_AMT");
		    payAmt += StringTool.round(t.getItemDouble(i, "TOT_AMT"), 2);
		setValue("OWN_AMT", Double.valueOf(payAmt));
	}

	/**
	 * ����·�� ===============pangben 2012-7-9
	 */
	public void onAddCLNCPath() {
		TParm inParm = new TParm();
		// =======�Ӡ�2012-06-04 ��õ�ǰʱ��
		StringBuffer sqlbf = new StringBuffer();
//		sqlbf
//				.append("SELECT SCHD_CODE FROM CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE= '"
//						+ clncpathCode + "'");
//		sqlbf.append(" AND CASE_NO='" + caseNo + "' ");
//		sqlbf.append(" AND REGION_CODE='" + Operator.getRegion() + "' ");
//		sqlbf.append(" AND SYSDATE BETWEEN START_DATE AND END_DATE");
//		sqlbf.append(" ORDER BY SEQ ");
		 sqlbf.append("SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"
	                + caseNo + "'");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sqlbf.toString()));
		// �õ���ǰʱ��
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("CLNCPATH_CODE", clncpathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("IND_FLG", "Y");
		inParm.setData("IND_CLP_FLG", true);//סԺ�Ƽ�ע��-xiongwg20150429
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPTemplateOrderQuote.x", inParm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
//			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
//					result.getValue("ORDER_CODE", i));
			//������������ʾ-xiongwg20150429
			TParm OrderParm = OdiUtil.getInstance().getSysFeeAndclp(
                    result.getValue("ORDER_CODE", i),result.getValue("CLNCPATH_CODE", i),
                    result.getValue("SCHD_CODE", i),result.getValue("CHKTYPE_CODE", i),
                    result.getValue("ORDER_SEQ_NO", i),result.getValue("ORDER_TYPE", i));
			// OrderParm.setData("EXID_ROW", getExitRow());

			newOrderTemp(OrderParm,
					getExitRow());
			// this.popReturn(tag, OrderParm);
		}
	}
	/**
	 * ����·������
	 * yanjing 20140919
	 */
    public void onChangeSchd(){
    	String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' AND CLNCPATH_CODE IS NOT NULL ";//��ѯ�û����Ƿ�����ٴ�·��
    	TParm parm = new TParm (TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){//�����ٴ�·��
    		String clncPathCode = parm.getValue("CLNCPATH_CODE", 0);
    		//���ø���ʱ�̵Ľ���
    		TParm sendParm = new TParm();
            sendParm.setData("CASE_NO", caseNo); 
            sendParm.setData("CLNCPATH_CODE", clncPathCode);
            TParm result = (TParm) this.openDialog(
                    "%ROOT%\\config\\odi\\ODIintoDuration.x", sendParm);
            String schdCodeSql="SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"+ caseNo + "' ";
            TParm schdParm = new TParm (TJDODBTool.getInstance().select(schdCodeSql));
            this.setValue("SCHD_CODE", schdParm.getValue("SCHD_CODE", 0));
            //���ö����е�schd_codeֵ
//            order.setSchdCode(schdParm.getValue("SCHD_CODE", 0));
            onSetSchdCode();
    	}else{
    		this.messageBox("�������ٴ�·�������ɸ���ʱ�̡�");
    		return;
    	}
    	
    }
    
    /**
     * ����ʱ���޸�================xiongwg 2015-4-26
     */
	public void onClpOrderReSchdCode() {
		String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "' AND CLNCPATH_CODE IS NOT NULL ";// ��ѯ�û����Ƿ�����ٴ�·��
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {// �����ٴ�·��
			parm = new TParm();
			parm.setData("CLP", "CASE_NO", caseNo);
			parm.setData("CLP", "MR_NO", mrNo);
			parm.setData("CLP", "FLG", "Y");
			TParm result = (TParm) this.openDialog(
					"%ROOT%\\config\\clp\\CLPOrderReplaceSchdCode.x", parm);
		} else {
			this.messageBox("�������ٴ�·�������ɸ���ʱ�̡�");
			return;
		}

	}
    
	/**
	 * �õ���ǰ�ɱ༭��
	 * 
	 * @return int
	 */
	public int getExitRow() {
		TTable table = this.getTTable("MAINTABLE");
		int rowCount = table.getDataStore().rowCount();
		int rowOnly = -1;
		for (int i = 0; i < rowCount; i++) {
			if (!table.getDataStore().isActive(i)) {
				rowOnly = i;
				break;
			}
		}
		return rowOnly;
	}
	/**
	 * ����dateStore��schd_code������
	 */
	public void onSetSchdCode(){
		order.setSchdCode(this.getValueString("SCHD_CODE"));
		schdCode = order.getSchdCode();
//		System.out.println("--------ʱ����� is����"+order.getSchdCode());
	}
	
    /**
     * ѡ���������뵥��������
     */
    public void onChooseOpBookSeq() {// wanglong add 20141010
        String opBookSeq = this.getValueString("OPBOOK_SEQ");
        TTextFormat OPE_STATE = (TTextFormat) this.getComponent("OPE_STATE");
        if (opBookSeq.equals("")) {
            OPE_STATE.getTablePopupMenu().setParmValue(new TParm());
            OPE_STATE.setValue(null);
            return;
        }
        String opBookSql = "SELECT TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '#'";
        opBookSql = opBookSql.replaceFirst("#", opBookSeq);
        TParm result = new TParm(TJDODBTool.getInstance().select(opBookSql));
        if (result.getErrCode() < 0) {
            this.messageBox("�������״̬ʧ�� " + result.getErrText());
            return;
        }
        String opStateSql =
                "SELECT ID,CHN_DESC NAME FROM SYS_DICTIONARY WHERE GROUP_ID='#' ORDER BY ID";
        if (result.getValue("TYPE_CODE", 0).equals("1")) {// ����������
            opStateSql = opStateSql.replaceFirst("#", "OPE_STATE1");
        } else {// TYPE_CODE=2 ��������
            opStateSql = opStateSql.replaceFirst("#", "OPE_STATE2");
        }
        OPE_STATE.setPopupMenuSQL(opStateSql);
        OPE_STATE.onQuery();
    }
	
}
