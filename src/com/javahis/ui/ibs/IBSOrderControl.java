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
 * Title: 住院计价控制类
 * </p>
 * 
 * 
 * <p>
 * Description: 住院计价控制类
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
	 * 就诊号
	 */
	private String caseNo;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 床号
	 */
	private String bedNo;
	/**
	 * 系统别
	 */
	String sysType;
	/**
	 * 医嘱 add caoyong 20131111
	 */
	private String orderDesc;
	
	public String getOrderDesc() {
		return orderDesc;
	}

	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}

	/**
	 * 服务等级
	 */
	String serviceLevel;
	/**
	 * 集合医嘱组号
	 */
	private int groupNo;
	private TTable t;
	private IBSOrderD order;
//	private static final String actionName = "action.ibs.IBSAction";
	private String orderNo; // 医嘱序号
	private String clncpathCode;// 临床路径代码
	private String schdCode;// 时程代码
	TParm operationParm; // 手术室回传数据
	/**
	 * TABLE
	 */
	private static String TABLE = "MAINTABLE";
	private TextFormatCLPDuration SCHD_CODE;//add by caowl 20121126
	private TextFormatCLPDuration CLNCPATH_CODE;//add by caowl 20121126临床路径
	
	//add by huangtt start 20130922
	/**
	 * 医嘱名称
	 */
	private  String OrderCode;
	
	private double ownRate;    //自付比例
	
	/**
	 * 身份一 
	 */
	String ctz1Code;
	/**
	 * 身份二
	 */
	String ctz2Code;
	/**
	 * 身份三
	 */
	String ctz3Code;
	//add by huangtt end 20130922
	

	/**
	 * 处理当前TOOLBAR
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	/**
	 * 初始化
	 */
	public void onInit() {
//		 this.messageBox("权限"+this.getPopedem("INWLEAD"));
//		 SYSRolePopedomTool.getInstance().getPopedom(role, groupCode, code)
		super.onInit();
		 
		// 初始化界面参数
		this.initPage();
		// table专用的监听
		getTTable("MAINTABLE").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// 监听datastore列值改变
		order.addEventListener(order.ACTION_SET_ITEM, this, "onSetItemEvent");
		// 模糊查询 -------start---------
		OrderList orderDesc = new OrderList();
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.addItem("ORDER_LIST", orderDesc);
		callFunction("UI|MAINTABLE|addEventListener", "MAINTABLE" + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 临时TABLE值改变监听
		addEventListener(TABLE + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// 添加临床路径引入时程======pangben 2012-7-9
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
	 * 监听table单击事件
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
		// 状态条
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
	}
	
	/**
	 * 值改事件监听
	 * 
	 * @param obj
	 *            Object
	 * @throws java.text.ParseException 
	 */
	public boolean onChangeTableValue(Object obj) throws java.text.ParseException {
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
//		System.out.println("666===++++===columnName columnName is ::"+columnName);
		// 判断当前列是否有医嘱
		int selRow = node.getRow();
		TParm orderP = this.getTTable(TABLE).getDataStore().getRowParm(selRow);
//		System.out.println("====++++====orderP orderP orderP is :"+orderP);
		String orderSetGroupNo = orderP.getValue("ORDERSET_GROUP_NO");
		String orderSetCode = orderP.getValue("ORDERSET_CODE");
		String indvFlg = orderP.getValue("INDV_FLG");
		
		if (orderP.getValue("ORDER_CODE").length() == 0) {
			// 清空医嘱名称
			this.getTTable(TABLE).setDSValue(selRow);
		}
//		System.out.println("1111===++++===columnName columnName is ::"+columnName);
		if ("EXEC_DATE".equals(columnName)) {
//			this.messageBox("00000000000000000000000000000000000000000");
			String execDate = node.getValue().toString().substring(0, 10);
			//取当前时间
			String nowDate = SystemTool.getInstance().getDate().toString().substring(0, 10);
			//查询此病患的的入院时间
			String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
			TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
			String inDate = inParm.getValue("IN_DATE", 0).substring(0, 10);
			if(execDate.compareTo(inDate)<0){
				this.messageBox("执行时间不可小于入院时间");
				return true;
			}else if(execDate.compareTo(nowDate)>0){
				this.messageBox("执行时间不可大于当前时间");
				return true;
			}
			//====若为集合医嘱修改order对象中相应的细项的执行时间
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
//			System.out.println("输出单元格值  is：： "+node.getValue());
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
			this.messageBox("执行时间不可为空");
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
				  //要转换字符串 str_test
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
			
			//取当前时间
			String nowDate = SystemTool.getInstance().getDate().toString().substring(0, 10);
			//查询此病患的的入院时间
			String inSql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
			TParm inParm = new TParm(TJDODBTool.getInstance().select(inSql));
			String inDate = inParm.getValue("IN_DATE", 0).substring(0, 10);
			if(execDate.compareTo(inDate)<0){
				this.messageBox("执行时间不可小于入院时间");
				this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
				return ;
			}else if(execDate.compareTo(nowDate)>0){
				this.setValue("EXEC_DATE", SystemTool.getInstance().getDate());
				this.messageBox("执行时间不可大于当前时间");
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
				  //要转换字符串 str_test
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
	 * datastore列值改变事件
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
				//modify by wanglong 20120814 修改总价的计算方式（总价等于每个医嘱的单价保留小数点两位后再相加起来，而不是先相加再舍四进五）
				//payAmt = payAmt + t.getItemDouble(i, "TOT_AMT");
				payAmt = payAmt + StringTool.round(t.getItemDouble(i, "TOT_AMT"), 2);
			}
			setValue("OWN_AMT", payAmt);

		}
		
		
		if (columnName.equals("EXE_DEPT_CODE")) {
			int countTable = t.getRowCount();
			for (int i = 0; i < countTable; i++) {
				// 成本中心
				t.setItem(i, "COST_CENTER_CODE", t.getItemData(i,
						"EXE_DEPT_CODE"));
				t
						.setItem(i, "EXE_DEPT_CODE", t.getItemData(i,
								"EXE_DEPT_CODE"));
			}
		}
	}

	/**
	 * sysFee模糊查询
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
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * sysFee弹出界面
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// 弹出sysfee对话框的列
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
		// System.out.println("医嘱类型" + getValueString("CAT1_TYPE"));
		// if (!"".equals(Operator.getRegion()))
		// parm.setData("REGION_CODE", Operator.getRegion());
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("SYS_FEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}
	
	
	/**
	 * 是否是集合医嘱
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
	 * 回传医嘱共用
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
			this.messageBox("获得身份折扣出现问题");
			return;
		}
		order.setOwnRate(ownRate); //add by huangtt 20131114

		t.acceptText();
		String cat1Type = parm.getValue("CAT1_TYPE");
		// 状态条
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
		int selRow = selectRow;
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = getValue("EXE_DEPT_CODE").toString();//===========modify by  20120810 caowl
		}
		t.setItem(selRow, "DS_FLG", "N");
		// 判断是否是集合医嘱
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// 自费注记
			t.setItem(selRow, "OWN_FLG", "Y");
			// 收费注记
			t.setItem(selRow, "BILL_FLG", "Y");
			// 自付比例
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// 频次
			t.setItem(selRow, "FREQ_CODE", "STAT");
			// 医嘱名称
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// 开药单位
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// 配药单位
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// 医保单价
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// 院内费用代码
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// 医嘱类别
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// 医嘱名称
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// 医嘱细分类
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// 集合医嘱组号
			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));

			// 查询收据费用代码
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// 收据费用代码
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
			 //System.out.println("细项数据"+parmDetail);
			if (parmDetail.getErrCode() != 0) {
				this.messageBox("取得细相数据错误");
				return;
			}
			// 成本中心
			t.setItem(selRow, "COST_CENTER_CODE", execDept);
			// 集合医嘱组号
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
				// 自费注记
				order.setItem(row, "OWN_FLG", "Y");
				// 收费注记
				order.setItem(row, "BILL_FLG", "Y");
				// 自付比例
//				order.setItem(row, "OWN_RATE", 1);
				order.setItem(row, "OWN_RATE", ownRate);  //modify by huangtt 20130922
				// 医嘱名称
				order.setItem(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// 频次
				order.setItem(row, "FREQ_CODE", "STAT");
				// 开药单位
				order.setItem(row, "MEDI_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// 配药单位
				order.setItem(row, "DOSAGE_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// 自费单价
				if ("2".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE2"));
				} else if ("3".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE3"));
				} else
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE"));

				// 医保单价
				order.setItem(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE"));
				// 院内费用代码
				order.setItem(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// 集合医嘱组号
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				// //查询收据费用代码
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
				// 收据费用代码
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
				// 医嘱类别
				order.setItem(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// 医嘱细分类
				order.setItem(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// 医嘱名称
				order.setItem(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				// 成本中心
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
//				order.setItem(row, "SCHD_CODE",getSchdCode());//=====时程  yanjing 20140902

			}
			// 自费单价
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
			// 查询收据费用代码
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			if (null==hexpParm||null==hexpParm.getValue("IPD_CHARGE_CODE",0)||hexpParm.getValue("IPD_CHARGE_CODE",0).length()<=0) {
				this.messageBox("获得收费明细存在问题");
				return;
			}
			t.setItem(selRow, "SCHD_CODE", order.getSchdCode());
			// 自费注记
			t.setItem(selRow, "OWN_FLG", "Y");
			// double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
			// parm.getValue("ORDER_CODE"),
			// serviceLevel);
			// if (ownRate < 0) {
			// return result.newErrParm( -1, "自付比例错误");
			// }
			// 自付比例
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);//modify by huangtt 20130922
			// 收费注记
			t.setItem(selRow, "BILL_FLG", "Y");
			// 频次
			t.setItem(selRow, "FREQ_CODE", "STAT");
			// 用量
			t.setItem(selRow, "MEDI_QTY", parm.getDouble("MEDI_QTY"));//从默认为1修改--xiongwg201050429
			// 天数
			t.setItem(selRow, "TAKE_DAYS", 1);
			// 总量
			t.setItem(selRow, "DOSAGE_QTY", parm.getDouble("MEDI_QTY"));//从默认为1修改--xiongwg201050429
			// 医嘱名称
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// 开药单位
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// 配药单位
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// 自费单价
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// 自费
			t.setItem(selRow, "OWN_AMT", ownPriceSingle);
			// 总价
//			t.setItem(selRow, "TOT_AMT", ownPriceSingle);
			t.setItem(selRow, "TOT_AMT", parm.getDouble("MEDI_QTY")*ownPriceSingle*ownRate);  //modify by huangtt 20130922
			// 医保单价
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// 院内费用代码
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// 医嘱细分类
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// 医嘱名称
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// 成本中心
			t.setItem(selRow, "COST_CENTER_CODE", this
					.getValueString("EXE_DEPT_CODE"));
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			// 医嘱细分类类别
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// 收据费用代码
			t.setItem(selRow, "REXP_CODE", hexpParm.getValue("IPD_CHARGE_CODE",
					0));
			if(this.getValueString("EXEC_DATE").length()==0||
					this.getValue("EXEC_DATE")==null){
				t.setItem(selRow, "EXEC_DATE", SystemTool.getInstance().getDate());
			}else{
				t.setItem(selRow, "EXEC_DATE", this.getValue("EXEC_DATE"));
			}
			
			// System.out.println("新增执行单位"+order.getItemData(selRow,"EXE_DEPT_CODE"));
			order.setActive(selRow, true);
			onInsert(selRow);
		}
	}

	
    
	/**
	 * 新增医嘱
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		
		TParm parm = (TParm) obj;
//		 System.out.println("sysfee信息"+parm);
//		double ownPriceSingle = 0.00;
//		if ("2".equals(serviceLevel)) {
//			ownPriceSingle = parm.getDouble("OWN_PRICE2");
//		} else if ("3".equals(serviceLevel)) {
//			ownPriceSingle = parm.getDouble("OWN_PRICE3");
//		} else
//			ownPriceSingle = parm.getDouble("OWN_PRICE");

		// System.out.println("新开立医嘱"+parm);
		// t.acceptText();
		// String cat1Type = parm.getValue("CAT1_TYPE");
		// //状态条
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
		// //判断是否是集合医嘱
		// if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
		// //自费注记
		// t.setItem(selRow, "OWN_FLG", "Y");
		// //收费注记
		// t.setItem(selRow, "BILL_FLG", "Y");
		// //自付比例
		// t.setItem(selRow, "OWN_RATE", 1);
		// //频次
		// t.setItem(selRow, "FREQ_CODE", "STAT");
		// //医嘱名称
		// t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
		// //开药单位
		// t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		// //配药单位
		// t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		// //医保单价
		// t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
		// //院内费用代码
		// t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		// //医嘱类别
		// t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		// //医嘱名称
		// t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
		// //医嘱细分类
		// t.setItem(selRow, "ORDER_CAT1_CODE",
		// parm.getValue("ORDER_CAT1_CODE"));
		// //集合医嘱组号
		// t.setItem(selRow, "ORDERSET_GROUP_NO",
		// parm.getInt("ORDERSET_GROUP_NO"));
		//
		// //查询收据费用代码
		// TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
		// parm);
		// //收据费用代码
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
		// // System.out.println("细项数据"+parmDetail);
		// if (parmDetail.getErrCode() != 0) {
		// this.messageBox("取得细相数据错误");
		// return;
		// }
		// //成本中心
		// t.setItem(selRow, "COST_CENTER_CODE", execDept);
		// //集合医嘱组号
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
		// //自费注记
		// order.setItem(row, "OWN_FLG", "Y");
		// //收费注记
		// order.setItem(row, "BILL_FLG", "Y");
		// //自付比例
		// order.setItem(row, "OWN_RATE", 1);
		// //医嘱名称
		// order.setItem(row, "ORDER_DESC",
		// parmDetail.getValue("ORDER_CODE", i));
		// //频次
		// order.setItem(row, "FREQ_CODE", "STAT");
		// //开药单位
		// order.setItem(row, "MEDI_UNIT",
		// parmDetail.getValue("UNIT_CODE"));
		// //配药单位
		// order.setItem(row, "DOSAGE_UNIT",
		// parmDetail.getValue("UNIT_CODE"));
		// //自费单价
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
		// //医保单价
		// order.setItem(row, "NHI_PRICE",
		// parmDetail.getDouble("NHI_PRICE"));
		// //院内费用代码
		// order.setItem(row, "HEXP_CODE",
		// parmDetail.getValue("CHARGE_HOSP_CODE", i));
		// //集合医嘱组号
		// order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		// // //查询收据费用代码
		// // hexpParm =
		// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
		// //收据费用代码
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
		// //医嘱类别
		// order.setItem(row, "CAT1_TYPE",
		// parmDetail.getValue("CAT1_TYPE", i));
		// //医嘱细分类
		// order.setItem(row, "ORDER_CAT1_CODE",
		// parmDetail.getValue("ORDER_CAT1_CODE", i));
		// //医嘱名称
		// order.setItem(row, "ORDER_CHN_DESC",
		// parmDetail.getValue("ORDER_DESC", i));
		// //成本中心
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
		// //自费单价
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
		// //自费注记
		// t.setItem(selRow, "OWN_FLG", "Y");
		// // double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
		// // parm.getValue("ORDER_CODE"),
		// // serviceLevel);
		// // if (ownRate < 0) {
		// // return result.newErrParm( -1, "自付比例错误");
		// // }
		// //自付比例
		// t.setItem(selRow, "OWN_RATE", 1);
		// //收费注记
		// t.setItem(selRow, "BILL_FLG", "Y");
		// //频次
		// t.setItem(selRow, "FREQ_CODE", "STAT");
		// //用量
		// t.setItem(selRow, "MEDI_QTY", 1);
		// //天数
		// t.setItem(selRow, "TAKE_DAYS", 1);
		// //总量
		// t.setItem(selRow, "DOSAGE_QTY", 1);
		// //医嘱名称
		// t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
		// //开药单位
		// t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		// //配药单位
		// t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		// //自费单价
		// t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
		// //自费
		// t.setItem(selRow, "OWN_AMT", ownPriceSingle);
		// //总价
		// t.setItem(selRow, "TOT_AMT", ownPriceSingle);
		// //医保单价
		// t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
		// //院内费用代码
		// t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		// //医嘱细分类
		// t.setItem(selRow, "ORDER_CAT1_CODE",
		// parm.getValue("ORDER_CAT1_CODE"));
		// //医嘱名称
		// t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
		// //成本中心
		// t.setItem(selRow, "COST_CENTER_CODE",
		// this.getValueString("EXE_DEPT_CODE"));
		// t.setItem(selRow, "EXE_DEPT_CODE", execDept);
		// //医嘱细分类类别
		// t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		// //查询收据费用代码
		// TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
		// parm);
		// //收据费用代码
		// t.setItem(selRow, "REXP_CODE",
		// hexpParm.getValue("IPD_CHARGE_CODE", 0));
		// //
		// System.out.println("新增执行单位"+order.getItemData(selRow,"EXE_DEPT_CODE"));
		// order.setActive(selRow, true);
		// onInsert(selRow);
		// }
		newOrderTemp(parm, t.getSelectedRow());
	}

	/**
	 * 由病患管理的查询调用 再次赋值参数
	 */
	public void onInitReset() {
		// 初始化页面
		this.initPage();
	}

	/**
	 * 补充计价取得医嘱序号
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
	 * 初始化页面(自定义)
	 */
	public void initPage() {
		TParm parm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			parm = (TParm) obj;
//			 //判断是护士长调入
//			 if (parm.existGroup("INWEXE")) {
//			 }
//			 if(this.getPopedem("INWLEAD")){
//				 this.messageBox("00000000000");
//				 
//			 }
//			System.out.println("初始化 初始化 parm parm is：："+parm);
			t = (TTable) this.getComponent("MAINTABLE");
			if (null!=parm &&null!=parm.getData("IBS", "INWLEAD_FLG")) {
				if(parm.getData("IBS", "INWLEAD_FLG").toString().equals("true")){//护士长权限
					callFunction("UI|EXEC_DATE|setEnabled", true);
					t.setLockColumns("2,7,8,9,13,14,15,16,18,19");
				}else{//一般权限
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
			//=========pangben 2015-10-16 修改重新查询临床路径代码
			TParm schdCodeParm = new TParm(TJDODBTool.getInstance().select(schdCodeInit));
			schdCode = schdCodeParm.getValue("SCHD_CODE", 0);// 时程代码
			clncpathCode = schdCodeParm.getValue("CLNCPATH_CODE", 0);// 临床路径代码
//			schdCode = parm.getData("IBS", "SCHD_CODE").toString();// 时程代码
			//===modify by caowl 20121126 start
//			this.SCHD_CODE = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
//			SCHD_CODE.setSqlFlg("Y");
//			SCHD_CODE.setClncpathCode(schdCode);
//			SCHD_CODE.onQuery();
			//===modify by caowl 20121126 end 
			
			
		}
		// ===========pangben 2012-7-9 临床路径引用时程显示
		if (null != clncpathCode && clncpathCode.length() > 0) {
			callFunction("UI|clpOrderQuote|setEnabled", true);
		} else {
			callFunction("UI|clpOrderQuote|setEnabled", false);
		}
		if (parm.getData("IBS", "TYPE") != null) {
			callFunction("UI|close|setEnabled", false);
		}
		if (caseNo == null || caseNo.length() == 0) {
			this.messageBox("请输入就诊号");
			return;
		}
		ipdNo = parm.getData("IBS", "IPD_NO").toString();
		mrNo = parm.getData("IBS", "MR_NO").toString();
		bedNo = parm.getData("IBS", "BED_NO").toString();
		setValue("CAT1_TYPE", "");
		// 初始化当前table
		t = (TTable) this.getComponent("MAINTABLE");
		order = new IBSOrderD(caseNo);
		// 就诊序号
		order.setCaseNo(caseNo);
		// 账务序号
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		//this.messageBox("行行行行行行行行行行行行行行行想"+(maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		// 生效起日(临时医嘱)
		order.setBeginDate(SystemTool.getInstance().getDate());
		// 生效迄日(临时医嘱)
		order.setEndDate(SystemTool.getInstance().getDate());
		// 计价日期
		order.setBillDate(SystemTool.getInstance().getDate());
		// 执行日期
		order.setExecDate(SystemTool.getInstance().getDate());
		// 医嘱类型
		order.setCat1Type(getValue("CAT1_TYPE").toString());
		// 开单科室
		order.setDeptCode(parm.getValue("IBS", "DEPT_CODE"));//费用查询开单科室相关问题-xiongwg20150619
//		order.setDeptCode(Operator.getDept());
		// 开单病区
		order.setStationCode(parm.getValue("IBS", "STATION_CODE"));
		// 开单医师
		order.setDrCode(parm.getValue("IBS", "VS_DR_CODE"));
		// 执行科室
		order.setExeDeptCode(Operator.getCostCenter());
		// 执行病区
		order.setExeStationCode(parm.getValue("IBS", "STATION_CODE"));
		// 执行医师
		order.setExeDrCode(Operator.getID());
		// 成本中心
		order.setCostCenterCode(Operator.getCostCenter());
		// 操作人员
		order.setOptUser(Operator.getID());
		// 操作日期
		order.setOptDate(SystemTool.getInstance().getDate());
		// 操作终端
		order.setOptTerm(Operator.getIP());
		//===caowl 20121126 start
		//时程
//		order.setSchdCode(schdCode);
//		order.setSchdCode(SCHD_CODE.getComboValue());
		order.setSchdCode(schdCode);
		//临床路径
		order.setClncpathCode(clncpathCode);
//		order.setClncpathCode(schdCode);
		//===caowl 20121126 end
		order.onQuery();
		// 绑定datastore
		t.setDataStore(order);
		this.onInsert(0);
		t.setDSValue();
		// 计价日期
		setValue("BILL_DATE", SystemTool.getInstance().getDate());
		setValue("EXEC_DATE", SystemTool.getInstance().getDate());//===yanjing 20140805 添加执行时间字段
		// --------传参得到--------
		// System.out.println("补充计价开单病区"+parm.getData("IBS", "STATION_CODE"));
		// 开单病区
		setValue("STATION_CODE", parm.getData("IBS", "STATION_CODE"));
		setValue("SCHD_CODE", schdCode);
		setValue("SCHD_CODE1", schdCode);
		setValue("CLNCPATH_CODE", clncpathCode);
		// 开单科室
		setValue("DEPT_CODE", parm.getData("IBS", "DEPT_CODE"));//费用查询开单科室相关问题-xiongwg20150619
//		setValue("DEPT_CODE", Operator.getDept());
		// 开单医生
		setValue("DR_CODE", parm.getData("IBS", "VS_DR_CODE"));
		// 住院号
		setValue("IPD_NO", parm.getData("IBS", "IPD_NO"));
		// 默认和开单相同
		// 执行病区
		setValue("EXE_STATION_CODE", getValue("STATION_CODE"));
		// String deptCode = Operator.getDept();
		// TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
		// //执行科室
		// if (deptParm.getCount("DEPT_CODE") > 0)
		// setValue("EXE_DEPT_CODE",
		// DeptTool.getInstance().getCostCenter(this.
		// getValueString("DEPT_CODE"), this.getValueString("STATION_CODE")));
		// else
		// setValue("EXE_DEPT_CODE", "");
		setValue("EXE_DEPT_CODE", Operator.getCostCenter());
		// 执行医生
		setValue("EXE_DR_CODE", Operator.getID());
		// 计费金额
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
		// 出院已结算状态或账单已审核状态
		if ("4".equals(billStatus) || "3".equals(billStatus)) {
			this.messageBox("此病患已出院,不能再开新医嘱");
			callFunction("UI|save|setEnabled", false);
		}
        TTextFormat OPBOOK_SEQ = (TTextFormat) this.getComponent("OPBOOK_SEQ");//wanglong add 20141010 手术申请单号
        String opBookSeqSql =
                " SELECT OPBOOK_SEQ ID FROM OPE_OPBOOK WHERE CASE_NO='#' ORDER BY OPBOOK_SEQ ";
        opBookSeqSql = opBookSeqSql.replaceFirst("#", caseNo);
        OPBOOK_SEQ.setPopupMenuSQL(opBookSeqSql);
        OPBOOK_SEQ.onQuery();
        // 对应状态时间
        this.setValue("STATE_TIME", SystemTool.getInstance().getDate());
	}
	 /**
	 * 临床路径控件触发事件
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
	// * 关闭事件
	// * @return boolean
	// */
	// public boolean onClosing() {
	// switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
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
	// * 关闭
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
	 * 检核医嘱
	 * 
	 * @return TParm(校验信息)
	 */
	public TParm checkOrderSave() {
		TParm result = new TParm();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		// 新加的数据
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
					result.setErr(-1, "'"+orderDesc+"'"+"退费数量超过合计数量,不能保存");
					return result;
				}
			}
		}
		for (int i : newRow) {
			if (!order.isActive(i, buff))
				continue;
			// 增加处方标记位管控-------start-------- wangl modify
			String orderCode = order.getRowParm(i, buff).getValue("ORDER_CODE");
			String orderCodeSql = " SELECT ORDER_CODE,ORDER_DESC,SPECIFICATION,DR_ORDER_FLG "
					+ "   FROM SYS_FEE "
					+ "  WHERE ORDER_CODE = '"
					+ orderCode
					+ "'";
			// System.out.println("处方权限查询sql" + orderCodeSql);
			TParm orderCodeParm = new TParm(TJDODBTool.getInstance().select(
					orderCodeSql));
			boolean drOrderFlg = orderCodeParm.getBoolean("DR_ORDER_FLG", 0);
			if (drOrderFlg) {
				this.messageBox(orderCodeParm.getValue("ORDER_DESC", 0) + "||"
						+ orderCodeParm.getValue("SPECIFICATION", 0)
						+ "为医师处方药,不得开立");
			}
			// 增加处方标记位管控-------end-------- wangl modify

			// 增加退费数量管控
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
//				for (int i1 : newRow) {//xiongwg20150511,已计费的费用外医嘱未加退费管控问题
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
//					result.setErr(-1, "退费数量超过合计数量,不能保存");
//					return result;
//				}
//			}
			if (order.getRowParm(i, buff).getValue("ORDERSET_CODE") == null
					|| order.getRowParm(i, buff).getValue("ORDERSET_CODE")
							.length() == 0) {
				// 用量
				if (order.getRowParm(i, buff).getDouble("MEDI_QTY") == 0) {
					// this.messageBox_(ds.getRowParm(i,buff).getDouble("MEDI_QTY")+"==="+i);
					result.setErrCode(-1);
					result.setErrText(order.getRowParm(i, buff).getValue(
							"ORDER_DESC")
							+ "用量不能为:0");
					result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
							buff).getValue("ORDER_CODE"));
					return result;
				}
				//caowl 20130130 start
				 //频次
				 if (order.getRowParm(i, buff).getValue("FREQ_CODE").length()
				 ==
				 0) {
				 result.setErrCode( -2);
				 result.setErrText(order.getRowParm(i,
				 buff).getValue("ORDER_DESC") +
				 "医嘱频次不可以为空");
				 result.setData("ERR", "ORDER_CODE",
				 order.getRowParm(i,
				 buff).getValue("ORDER_CODE"));
				 return result;
				 }
				 //caowl 20130130 end
				// 天数
				if (order.getRowParm(i, buff).getInt("TAKE_DAYS") == 0) {
					result.setErrCode(-3);
					result.setErrText(order.getRowParm(i, buff).getValue(
							"ORDER_DESC")
							+ "医嘱天数不可以为0");
					result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
							buff).getValue("ORDER_CODE"));
					return result;
				}

			}
			// 执行科室
			if (order.getRowParm(i, buff).getValue("EXE_DEPT_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "执行科室不能为空");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// 开单科室
			if (order.getRowParm(i, buff).getValue("DEPT_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "开单科室不能为空");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// 医嘱代码
			if (order.getRowParm(i, buff).getValue("ORDER_CODE").length() == 0) {
				result.setErrCode(-4);
				result.setErrText(order.getRowParm(i, buff).getValue(
						"ORDER_DESC")
						+ "医嘱代码不能为空");
				result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
						.getValue("ORDER_CODE"));
				return result;
			}
			// System.out.println("总量"+order.getRowParm(i,
			// buff).getDouble("DOSAGE_QTY"));
			//caowl 20130130 start 总量不能为零
			 //总量
			 if (order.getRowParm(i, buff).getDouble("DOSAGE_QTY") == 0) {
			 result.setErrCode( -6);
			 result.setErrText(order.getRowParm(i,
			 buff).getValue("ORDER_DESC") + "总量不能为0");
			 result.setData("ERR", "ORDER_CODE",
			 order.getRowParm(i, buff).getValue("ORDER_CODE"));
			 return result;
			 }
			 //caowl 20130130 end
			// 检核库存
			// if("PHA".equals(ds.getRowParm(i,buff).getValue("CAT1_TYPE"))){
			// if(!INDTool.getInstance().inspectIndStock(ds.getRowParm(i,buff).getValue("EXEC_DEPT_CODE"),ds.getRowParm(i,buff).getValue("ORDER_CODE"),
			// ds.getRowParm(i,buff).getDouble("DOSAGE_QTY"))){
			// result.setErrCode(-5);
			// result.setErrText(ds.getRowParm(i,buff).getValue("ORDER_DESC")+"库存不足！");
			// result.setData("ERR","INDEX",index.get(ds.getRowParm(i,buff).getValue("RX_KIND")));
			// result.setData("ERR","ORDER_CODE",ds.getRowParm(i,buff).getValue("ORDER_CODE"));
			// return result;
			// }
			// }
		}
		return result;
	}

	/**
	 * 保存
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		// if (!checkFee(caseNo)) {
		// this.messageBox("余额不足!");
		// return false;
		// }
		t.acceptText();
		double newTotamt = TypeTool.getDouble(getValue("OWN_AMT"));
		 getinPage();
		//this.messageBox(""+newTotamt);
		// 检核
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
		// 医嘱序号
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
			
			// 医嘱顺序号
			max++;
			t.setItem(i, "ORDER_SEQ", "" + max);
			
			order.setFilter("ORDERSET_CODE ='' OR (ORDERSET_CODE !='' AND INDV_FLG='N')");
			order.filter();

		}
		////$------------start caoyong 20131111----------------
		if(flag){//t.getItemString(i, "ORDER_DESC")+
			 this.messageBox(this.getOrderDesc()+"医嘱不存在，请重新选择");
			  return false;
		}
		//$------------end caoyong 2013111----------------
		String buffer = order.isFilter() ? order.FILTER : order.PRIMARY;
		// 账单子序号
		int[] countArray = order.getNewRows(buffer);
		int seqNo = 1;
		for (int i : countArray) {
			order.setItem(countArray[i], "SEQ_NO", seqNo, buffer);
			seqNo++;
		}
		//==========modify by caowl 20120911 start
		// 账务序号
		TParm maxCaseNoSeq = selMaxCaseNoSeq(caseNo);
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			order.setCaseNoSeq(1);
		} else {
			order.setCaseNoSeq((maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
		}
		
		//this.messageBox("=====哈哈哈哈======="+(maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
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
			order.setItem(temp, "CASE_NO_SEQ", order.getCaseNoSeq());//add caoyong 重新赋值避免主键冲突
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
		// System.out.println("老总价"+oleTotalAmt);
		double oleCurAmt = selADMParm.getDouble("CUR_AMT", 0);
		// System.out.println("老剩余金额"+oleTotalAmt);
		double newTotalAmt = oleTotalAmt + newTotamt;
		// System.out.println("新金额产生"+newTotamt);
		double newCurAmt = oleCurAmt - newTotamt;
		// System.out.println("操作人员区域"+Operator.getRegion() );
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
        // wanglong add 20141010 增加手术状态，手术时间，更新手术申请表
        int StateSeq = -1;
        String opBookSeq = this.getValueString("OPBOOK_SEQ");
        String opState = this.getValueString("OPE_STATE");
        if (!opBookSeq.equals("")) {
            String opBookSql = "SELECT TYPE_CODE FROM OPE_OPBOOK WHERE OPBOOK_SEQ = '#'";
            opBookSql = opBookSql.replaceFirst("#", opBookSeq);
            TParm typeParm = new TParm(TJDODBTool.getInstance().select(opBookSql));
            if (typeParm.getErrCode() < 0) {
                this.messageBox("获得手术类型失败 " + typeParm.getErrText());
            } else if (typeParm.getCount() > 0) {
                if (opState.equals("")) {
                    this.messageBox("请选择手术状态");
                    return false;
                }
                if (this.getValueString("STATE_TIME").equals("")) {
                    this.messageBox("请填写手术时间");
                    return false;
                }
                if (typeParm.getValue("TYPE_CODE", 0).equals("1")) {// 1手术室手术
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
                } else {// 1介入手术
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
                this.messageBox("手术单号不存在");
            }
        }
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			
		if (result.getErrCode() < 0) {
			//caowl 20130320 start	   		    
			
			String errText = result.getErrText().substring(0,9);	
			if(errText.equals("ORA-00001")){
				this.messageBox("另外一位护士正对此病人进行计费操作，请关闭界面重新进入");
			}else{
				// 保存失败
				this.messageBox("E0001");
			}
			//caowl 20130320 end
		} else {
			// 保存成功
			this.messageBox("P0001");
            // 电视屏接口 wanglong add 20141010
            if (!this.getValueString("OPBOOK_SEQ").equals("") && !(StateSeq + "").equals(opState)) {
                TParm xmlParm = ADMXMLTool.getInstance().creatOPEStateXMLFile(caseNo, opBookSeq);
                if (xmlParm.getErrCode() < 0) {
                    this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
                }
            }
			result = ADMTool.getInstance().checkStopFee(caseNo);
			onInit();
		}
		return true;
	}
    /**
     * 验证SYS_FEE表中是否有添加的医嘱
     */
	public TParm getOrderCode(String ordCode){
		String sql="SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"+ordCode+"'";
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 return result;
	}
	/**
	 * 校验余额
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
		// System.out.println("校验余额"+selADMAllData);
		if ("Y".equals(selADMAllData.getValue("STOP_BILL_FLG", 0)))
			return false;
		return true;
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		int row = t.getSelectedRow();
		TParm tableParm = t.getDataStore().getRowParm(row);
		// System.out.println("tableParm显示数据" + tableParm);
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
	 * 删除集合医嘱细项
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
	 * 右击MENU弹出事件
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		TParm parm = table.getShowParmValue();
		int row = table.getSelectedRow();
		if (parm.getData("ORDER_DESC", row) != null
				&& order.getItemString(row, "ORDERSET_CODE").length() > 0) {
			table.setPopupMenuSyntax("显示集合医嘱细相,openRigthPopMenu");
			return;
		} else {
			table.setPopupMenuSyntax("");
			return;
		}
	}

	/**
	 * 打开集合医嘱细项查询
	 */
	public void openRigthPopMenu() {
		TTable table = (TTable) this.getComponent("MAINTABLE");
		int row = table.getSelectedRow();
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		String orderCode = order.getItemString(row, "ORDER_CODE");
		// this.messageBox_("集合医嘱groupNo" + groupNo);
		// this.messageBox_("集合医嘱orderCode" + orderCode);
		TParm parm = getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @param groupNo
	 *            int
	 * @param orderSetCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSetDetails(int groupNo, String orderSetCode) {
		// System.out.println("集合医嘱组号" + groupNo);
		// System.out.println("集合医嘱组代码" + orderSetCode);
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

		// System.out.println("医嘱信息" + parm);
		int count = parm.getCount();
		if (count < 0) {
			System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// temperr细项价格
		for (int i = 0; i < count; i++) {
			if (!orderSetCode.equals(parm.getValue("ORDER_CODE", i))
					&& parm.getBoolean("#ACTIVE#", i)
					&& orderSetCode.equals(parm.getValue("ORDERSET_CODE", i))) {
				// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// 查询单价
				TParm orderParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE,ORDER_DESC,SPECIFICATION,OPTITEM_CODE,INSPAY_TYPE "
								+ "FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// this.messageBox_(ownPriceParm);
				// 计算总价格
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
	 *医嘱类别改变
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
	 * 得到护士站数据
	 * 
	 * @param parm
	 *            TParm
	 */
	public void getINWData(TParm parm) {
		// this.messageBox_("进入护士执行");
		TTable table = (TTable) this.getComponent("MAINTABLE");
		table.removeAll();
		// this.messageBox_("接收护士站数据" + parm);
	}

	/**
	 * 查询最大账务序号
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
	 * 查询最大医嘱顺序号
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
	 * 查询最大集合医嘱组号
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
	 * 手术室补充计费
	 */
	public void onOperation() {
		operationParm = new TParm();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", parm,
				false);
		// System.out.println("手术补充计价" + operationParm);
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
	 * 手术套餐回传新增
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
			this.messageBox("获得身份折扣出现问题");
			return;
		}
		order.setOwnRate(ownRate); //add by huangtt 20131114	
		t.acceptText();
		// 状态条
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE")
				+ parm.getValue("ORDER_DESC") + parm.getValue("GOODS_DESC")
				+ parm.getValue("DESCRIPTION") + parm.getValue("SPECIFICATION"));
		int selRow = t.getRowCount() - 1;
		t.setSelectedRow(selRow);   //add by huangtt 20130922
		t.setItem(selRow, "DS_FLG", "N");
		// 判断是否是集合医嘱
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {

			
			// 自费注记
			t.setItem(selRow, "OWN_FLG", "Y");
			// 收费注记
			t.setItem(selRow, "BILL_FLG", "Y");
			// 自付比例
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate); //modify by huangtt 20130922
			// 医嘱名称
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// 医嘱类别
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// 医嘱名称
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			// 医嘱细分类
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// 开药单位
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// 配药单位
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// 医保单价
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// 院内费用代码
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			// 医嘱类别
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// 集合医嘱组号
			t.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));

			// 查询收据费用代码
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			// 收据费用代码
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
				this.messageBox("取得细相数据错误");
				return;
			}
			// 集合医嘱组号
			TParm groupNoParm = this.seleMaxOrdersetGroupNo(caseNo);
			groupNo = groupNoParm.getInt("ORDERSET_GROUP_NO", 0);

			t.setItem(selRow, "ORDERSET_GROUP_NO", groupNo);
			double allOwnAmt = 0.00;
			for (int i = 0; i < parmDetail.getCount(); i++) {
				int row = order.insertRow();				
				
				// 自费注记
				order.setItem(row, "OWN_FLG", "Y");
				// 医嘱名称
				order.setItem(row, "ORDER_DESC", parmDetail.getValue(
						"ORDER_CODE", i));
				// 医嘱类别
				order.setItem(row, "CAT1_TYPE", parmDetail.getValue(
						"CAT1_TYPE", i));
				// 医嘱细分类
				order.setItem(row, "ORDER_CAT1_CODE", parmDetail.getValue(
						"ORDER_CAT1_CODE", i));
				// 医嘱名称
				order.setItem(row, "ORDER_CHN_DESC", parmDetail.getValue(
						"ORDER_DESC", i));
				// 开药单位
				order.setItem(row, "MEDI_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				// 配药单位
				order.setItem(row, "DOSAGE_UNIT", parmDetail
						.getValue("UNIT_CODE"));
				//频次
				t.setItem(selRow, "FREQ_CODE", "STAT");//============modify by caowl 20120810
				// 自费单价
				if ("2".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE2"));
				} else if ("3".equals(serviceLevel)) {
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE3"));
				} else
					order.setItem(row, "OWN_PRICE", parmDetail
							.getDouble("OWN_PRICE"));

				// 医保单价
				order.setItem(row, "NHI_PRICE", parmDetail
						.getDouble("NHI_PRICE"));
				// 院内费用代码
				order.setItem(row, "HEXP_CODE", parmDetail.getValue(
						"CHARGE_HOSP_CODE", i));
				// 集合医嘱组号
				order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
				// //查询收据费用代码
				// hexpParm =
				// SYSChargeHospCodeTool.getInstance().selectalldata(parmDetail);
				// 收据费用代码
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
			// 自费单价
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
			// 查询收据费用代码
			TParm hexpParm = SYSChargeHospCodeTool.getInstance().selectalldata(
					parm);
			if (null==hexpParm||null==hexpParm.getValue("IPD_CHARGE_CODE",0)||hexpParm.getValue("IPD_CHARGE_CODE",0).length()<=0) {
				this.messageBox("获得收费明细存在问题");
				return;
			}
			// 自费注记
			t.setItem(selRow, "OWN_FLG", "Y");
			// 收费注记
			t.setItem(selRow, "BILL_FLG", "Y");
			// 自付比例
//			t.setItem(selRow, "OWN_RATE", 1);
			t.setItem(selRow, "OWN_RATE", ownRate);  //modify by huangtt 20130922
			// 医嘱名称
			t.setItem(selRow, "ORDER_DESC", parm.getValue("ORDER_CODE"));
			// 医嘱类别
			t.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
			// 医嘱名称
			t.setItem(selRow, "ORDER_CHN_DESC", parm.getValue("ORDER_DESC"));
			//============modify by caowl 20120810 start
			//频次
			t.setItem(selRow, "FREQ_CODE", "STAT");
			t.setItem(selRow, "EXE_DEPT_CODE", execDept);
			//============modify by caowl 20120810 end
			// 医嘱细分类
			t.setItem(selRow, "ORDER_CAT1_CODE", parm
					.getValue("ORDER_CAT1_CODE"));
			// 开药单位
			t.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
			// 配药单位
			t.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
			// 自费单价
			t.setItem(selRow, "OWN_PRICE", ownPriceSingle);
			// 医保单价
			t.setItem(selRow, "NHI_PRICE", parm.getDouble("NHI_PRICE"));
			// 院内费用代码
			t.setItem(selRow, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
			
			// 收据费用代码
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
	 * 费用查询
	 */
	public void onSelFee() {
		TParm parm = new TParm();
		parm.setData("IBS", "CASE_NO", caseNo);
		parm.setData("IBS", "MR_NO", mrNo);
		parm.setData("IBS", "TYPE", "IBS");
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
	}

	// 在table新增一条医嘱行
	public void onInsert(int selRow) {
		// 查看是否有未编辑行
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
	 * 是否有未编辑行
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
	 * 护士套餐
	 */
	public void onNbwPackage() {
		// 护士套餐
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
	 * 科室套餐
	 */
	public void onDeptPackage() {
		// 科室套餐
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
	 * 套餐赋值
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
				// System.out.println("套餐参数"+temp);
				insertNewOperationOrder(temp, temp.getDouble("MEDI_QTY"));
			}
		}
		return falg;
	}

	/**
	 * 重新计算总价
	 */
	public void setTotAmt() {
		int countTable = t.getRowCount();
		double payAmt = 0.0D;
		for (int i = 0; i < countTable; i++)
			//modify by wanglong 20120814 修改总价的计算方式（总价等于每个医嘱的单价保留小数点两位后再相加起来，而不是先相加再舍四进五）
			//payAmt += t.getItemDouble(i, "TOT_AMT");
		    payAmt += StringTool.round(t.getItemDouble(i, "TOT_AMT"), 2);
		setValue("OWN_AMT", Double.valueOf(payAmt));
	}

	/**
	 * 引入路径 ===============pangben 2012-7-9
	 */
	public void onAddCLNCPath() {
		TParm inParm = new TParm();
		// =======庞犇2012-06-04 获得当前时程
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
		// 得到当前时程
		String schdCode = "";
		if (result.getCount("SCHD_CODE") > 0) {
			schdCode = result.getValue("SCHD_CODE", 0);
		}
		inParm.setData("CLNCPATH_CODE", clncpathCode);
		inParm.setData("SCHD_CODE", schdCode);
		inParm.setData("CASE_NO", caseNo);
		inParm.setData("IND_FLG", "Y");
		inParm.setData("IND_CLP_FLG", true);//住院计价注记-xiongwg20150429
		result = (TParm) this.openDialog(
				"%ROOT%\\config\\clp\\CLPTemplateOrderQuote.x", inParm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		int rowCount = result.getCount("ORDER_CODE");
		for (int i = 0; i < rowCount; i++) {
//			TParm OrderParm = OdiUtil.getInstance().getSysFeeOrder(
//					result.getValue("ORDER_CODE", i));
			//增加用量的显示-xiongwg20150429
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
	 * 更换路径方法
	 * yanjing 20140919
	 */
    public void onChangeSchd(){
    	String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' AND CLNCPATH_CODE IS NOT NULL ";//查询该患者是否存在临床路径
    	TParm parm = new TParm (TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){//存在临床路径
    		String clncPathCode = parm.getValue("CLNCPATH_CODE", 0);
    		//调用更换时程的界面
    		TParm sendParm = new TParm();
            sendParm.setData("CASE_NO", caseNo); 
            sendParm.setData("CLNCPATH_CODE", clncPathCode);
            TParm result = (TParm) this.openDialog(
                    "%ROOT%\\config\\odi\\ODIintoDuration.x", sendParm);
            String schdCodeSql="SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"+ caseNo + "' ";
            TParm schdParm = new TParm (TJDODBTool.getInstance().select(schdCodeSql));
            this.setValue("SCHD_CODE", schdParm.getValue("SCHD_CODE", 0));
            //设置对象中的schd_code值
//            order.setSchdCode(schdParm.getValue("SCHD_CODE", 0));
            onSetSchdCode();
    	}else{
    		this.messageBox("不存在临床路径，不可更改时程。");
    		return;
    	}
    	
    }
    
    /**
     * 费用时程修改================xiongwg 2015-4-26
     */
	public void onClpOrderReSchdCode() {
		String sql = "SELECT CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "' AND CLNCPATH_CODE IS NOT NULL ";// 查询该患者是否存在临床路径
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {// 存在临床路径
			parm = new TParm();
			parm.setData("CLP", "CASE_NO", caseNo);
			parm.setData("CLP", "MR_NO", mrNo);
			parm.setData("CLP", "FLG", "Y");
			TParm result = (TParm) this.openDialog(
					"%ROOT%\\config\\clp\\CLPOrderReplaceSchdCode.x", parm);
		} else {
			this.messageBox("不存在临床路径，不可更改时程。");
			return;
		}

	}
    
	/**
	 * 拿到当前可编辑行
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
	 * 设置dateStore中schd_code的数据
	 */
	public void onSetSchdCode(){
		order.setSchdCode(this.getValueString("SCHD_CODE"));
		schdCode = order.getSchdCode();
//		System.out.println("--------时程输出 is：："+order.getSchdCode());
	}
	
    /**
     * 选择手术申请单号下拉框
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
            this.messageBox("获得手术状态失败 " + result.getErrText());
            return;
        }
        String opStateSql =
                "SELECT ID,CHN_DESC NAME FROM SYS_DICTIONARY WHERE GROUP_ID='#' ORDER BY ID";
        if (result.getValue("TYPE_CODE", 0).equals("1")) {// 手术室手术
            opStateSql = opStateSql.replaceFirst("#", "OPE_STATE1");
        } else {// TYPE_CODE=2 介入手术
            opStateSql = opStateSql.replaceFirst("#", "OPE_STATE2");
        }
        OPE_STATE.setPopupMenuSQL(opStateSql);
        OPE_STATE.onQuery();
    }
	
}
