package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
/**
 * <p>
 * Title: 药物(抗生素或所有药品)销售量排名
 * </p>
 * 
 * <p>
 * Description: 药物(抗生素或所有药品)销售量排名
 * </p>
 * 
 * <p>
 * Copyright: ProperSoft
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author yanjing
 * @version 1.0
 */
public class UDDSellOrderControl extends TControl {
	private TTable table;
	private TNumberTextField TOT;

	public UDDSellOrderControl() {
	}

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.setValue("ORDER_CODE", "");
		this.setValue("TOT", 0.00);
		initPage();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		//初始化查询区间
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
		// 设置弹出菜单
		TParm parmIn = new TParm();
		parmIn.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		setValue("REGION_CODE", Operator.getRegion());
		// setValue("REE", Operator.getRegion());
	}
	/**
	 * 得到上个月
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
//		System.out.println("9999999"+d);
		try {
			d = defaultFormatter.parse(dateStr);
//			System.out.println("9999999"+d);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}

	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}

	/**
	 * 得到TABLE对象
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 查询操作
	 */
	public void onQuery() {
		if ("".equals(this.getValue("START_DATE"))
				|| this.getValue("START_DATE") == null) {
			this.messageBox("开始时间不能为空！");
			return;
		} else if ("".equals(this.getValue("END_DATE"))
				|| this.getValue("END_DATE") == null) {
			this.messageBox("结束时间不能为空！");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy/MM/dd HH:mm:ss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy/MM/dd HH:mm:ss");
		TParm selAccountData = new TParm();
//		System.out.println("================"+this.getValueString("DS_FLG"));
		if(this.getValueString("DS_FLG").equals("1")){
			selAccountData.setData("DS_FLG", "Y");
		}else if(this.getValueString("DS_FLG").equals("2")){
			selAccountData.setData("DS_FLG", "N");
		}
			
		TParm result = new TParm();
		if (this.getValue("ORDER_CODE").equals("")) {
			selAccountData.setData("REGION_CODE", this.getValue("REGION_CODE"));
			selAccountData.setData("START_DATE", startTime);
			selAccountData.setData("END_DATE", endTime);
			if (this.getValue("TYPE_CODE").equals("1")) {
				if (this.getValue("ADM_TYPE").equals("1")) {
					result = UDDTool.getInstance().getSellOrderOPD(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("2")) {
					result = UDDTool.getInstance().getSellOrderODI(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("3")){
					result = UDDTool.getInstance().getSellOrderO(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("4")){
					result = UDDTool.getInstance().getSellOrderE(selAccountData);
				}else{//yanjing 20131128
					if("3".equals(this.getValueString("DS_FLG"))||"".equals(this.getValueString("DS_FLG"))){
						result = UDDTool.getInstance().getSellOrder6(selAccountData);
					}else{
						result = UDDTool.getInstance().getSellOrder(selAccountData);
					}
				}
					
			} else if (this.getValue("TYPE_CODE").equals("2")) {
				if (this.getValue("ADM_TYPE").equals("1")) {
					result = UDDTool.getInstance().getAllOrderOPD(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("2")) {
					result = UDDTool.getInstance().getAllOrderODI(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("3")) {
					result = UDDTool.getInstance().getAllOrderO(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("4")) {
					result = UDDTool.getInstance().getAllOrderE(selAccountData);
				}else{//yanjing 20131128
					if("3".equals(this.getValueString("DS_FLG"))||"".equals(this.getValueString("DS_FLG"))){
						result = UDDTool.getInstance().getAllOrder(selAccountData);
					}else{
						result = UDDTool.getInstance().getAllOrder3(selAccountData);
					}
					
				}
				
			} else {
				this.messageBox("请选择药品的种类！");
				return;
			} 
		} else {
			selAccountData.setData("REGION_CODE", this.getValue("REGION_CODE"));
			selAccountData.setData("START_DATE", startTime);
			selAccountData.setData("END_DATE", endTime);
			selAccountData.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
			selAccountData.setData("ORDER_DESC", this.getValue("ORDER_DESC"));
			if (this.getValue("TYPE_CODE").equals("1")) {
				if (this.getValue("ADM_TYPE").equals("1")) {
					result = UDDTool.getInstance().getSellOrderOPD1(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("2")) {
					result = UDDTool.getInstance().getSellOrderODI1(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("3")) {
					result = UDDTool.getInstance().getSellOrderO1(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("4")) {
					result = UDDTool.getInstance().getSellOrderE1(selAccountData);
				}else{//YANJING 20131128
					if("3".equals(this.getValueString("DS_FLG"))){
						result = UDDTool.getInstance().getSellOrder1(selAccountData);
					}else{
						result = UDDTool.getInstance().getSellOrder3(selAccountData);
					}
					
				}
				
			} else if (this.getValue("TYPE_CODE").equals("2")) {
				if (this.getValue("ADM_TYPE").equals("1")) {
					result = UDDTool.getInstance().getAllOrderOPD1(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("2")) {
					result = UDDTool.getInstance().getAllOrderODI1(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("3")) {
					result = UDDTool.getInstance().getAllOrderO1(selAccountData);
				}else if (this.getValue("ADM_TYPE").equals("4")) {
					result = UDDTool.getInstance().getAllOrderE1(selAccountData);
				}else{//YANJING 20131128
					if("3".equals(this.getValueString("DS_FLG"))){
					result = UDDTool.getInstance().getAllOrder1(selAccountData);
					}else{
						result = UDDTool.getInstance().getAllOrder6(selAccountData); 	
					}
				}
					
			} else {
				this.messageBox("请选择药品的种类！");
				return;
			}
		}
		table.removeRowAll();
		if (result.getCount() <= 0) {
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		//System.out.println("resutlsdfsdfsdf::::"+result);
		// 总金额
		double totalAmt = 0.0;
		int count = result.getCount();
//		int sumDispenseQty = 0;// 数量
		TOT = (TNumberTextField) this.getComponent("TOT");
		// 循环累加
		for (int i = 0; i < count; i++) {
			double temp = result.getDouble("SUM_AMT", i);
			totalAmt += temp;
//			sumDispenseQty += result.getInt("SUM_QTY", i);
//			result.setData("SUM_QTY", i, result.getInt("SUM_QTY", i));
		}
		TOT.setValue(totalAmt);
		result.addData("REGION_CHN_ABN", "总计:");
		result.addData("ORDER_CODE", "");
		result.addData("ORDER_DESC", "");
		result.addData("SPECIFICATION", "");
		result.addData("OWN_PRICE", "");
		result.addData("SUM_QTY", "");
		result.addData("SUM_AMT", totalAmt);
		result.setCount(count+1);
		// 加载table上的数据
		table.setParmValue(result);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有要汇出的数据");
			return;
		}
		if (this.getValue("TYPE_CODE").equals("1")) {
			ExportExcelUtil.getInstance().exportExcel(table, "抗生素销售排名记录表");
		} else {
			ExportExcelUtil.getInstance().exportExcel(table, "全品种销售排名记录表");
		}
	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		 String clear = "TYPE_CODE;ORDER_CODE;ORDER_DESC;TOT";
		 this.clearValue(clear);
		 this.setValue("TYPE_CODE", "3");
		TTable table = (TTable) this.getComponent("TABLE");
		table.removeRowAll();
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
	}

}
