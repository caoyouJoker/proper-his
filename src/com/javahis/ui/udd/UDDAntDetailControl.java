/**
 * @className UDDAntDetailControl.java 
 * @author yanjing
 * @Date 2013-3-28 
 * @version V 1.0 
 */
package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDNewTool;

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
 * Title: 使用抗菌药物的出院患者明细
 * </p>
 * 
 * <p>
 * Description: 使用抗菌药物的出院患者明细
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
public class UDDAntDetailControl extends TControl{
	private TTable table;
    private TTextField MR_NO;
    private TTextField NAME;
    private TNumberTextField TOT; //总计
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		this.setValue("DEPT_CODE","");
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("TOT", "0.00");
//		this.setValue("REGION_CODE", "H01");
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		//初始化查询起时,迄时
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
	    // 设置常用药物弹出菜单
	    TParm parmIn = new TParm();
	    parmIn.setData("CAT1_TYPE","PHA");
	    getTextField("ORDER_CODE").setPopupMenuParameter(
	            "UD",
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
	//定义接受返回值方法
	    getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
		try {
			d = defaultFormatter.parse(dateStr);
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
	  * 补齐MR_NO
	  */
	 public void onMrNo() {
		 MR_NO = (TTextField)this.getComponent("MR_NO");
	     String mrNo = MR_NO.getValue();
		// modify by huangtt 20160930 EMPI患者查重提示 start
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			clearValue("MR_NO;NAME;");
			this.messageBox("无此病案号!");
			return;
		}
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
		// modify by huangtt 20160930 EMPI患者查重提示 end
	     
	     MR_NO.setValue(mrNo);
	     //得到病患名字
	     getPatName(mrNo);
	 }

	 /**
	  * 获得该病人的姓名
	  * @param mrNo String
	  */
	 private void getPatName(String mrNo){
		 NAME = (TTextField)this.getComponent("NAME");
	     NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));
	 }

	 /**
	  * 查询操作
	  */
	 public void onQuery() {
		 if("".equals(this.getValue("START_DATE"))||this.getValue("START_DATE")==null){
				this.messageBox("开始时间不能为空！");
				return;
			}else if("".equals(this.getValue("END_DATE"))||this.getValue("END_DATE")==null){
				this.messageBox("结束时间不能为空！");
				return;		
			}
		 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	     "START_DATE")), "yyyy/MM/dd HH:mm:ss");
	      String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	     "END_DATE")), "yyyy/MM/dd HH:mm:ss");
	      TParm result = new TParm();
	      TParm selAccountData = new TParm(); 
	      if (this.getValueString("DS_FLG").equals("N")) {
	    	  selAccountData.setData("DS_FLG","N");
		}
	      if (this.getValue("MR_NO").equals("")) {
	    	  if (this.getValue("ORDER_CODE").equals("")) {
		    	  if (this.getValue("DEPT_CODE").equals("")) {
		    		  selAccountData.setData("START_DATE", startTime);
		              selAccountData.setData("END_DATE",endTime);
		              result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
				}else {
					selAccountData.setData("START_DATE", startTime);
				      selAccountData.setData("END_DATE",endTime);
				      selAccountData.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
				      result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
				}
		      }else if (this.getValue("DEPT_CODE").equals("")) {
		    	  selAccountData.setData("START_DATE", startTime);
			      selAccountData.setData("END_DATE",endTime);
			      selAccountData.setData("ORDER_CODE",this.getValue("ORDER_CODE"));
			      selAccountData.setData("ORDER_DESC",this.getValue("ORDER_DESC"));
			      result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
			}else{
					selAccountData.setData("START_DATE", startTime);
				      selAccountData.setData("END_DATE",endTime);
				      selAccountData.setData("ORDER_CODE",this.getValue("ORDER_CODE"));
				      selAccountData.setData("ORDER_DESC",this.getValue("ORDER_DESC"));
				      selAccountData.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
				      result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
				}
			
		}else{
			selAccountData.setData("MR_NO", this.getValue("MR_NO"));
	      if (this.getValue("ORDER_CODE").equals("")) {
	    	  if (this.getValue("DEPT_CODE").equals("")) {
	    		  selAccountData.setData("START_DATE", startTime);
	              selAccountData.setData("END_DATE",endTime);
	              result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
			}else {
				  selAccountData.setData("START_DATE", startTime);
			      selAccountData.setData("END_DATE",endTime);
			      selAccountData.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
			      result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
			}
	      }else if (this.getValue("DEPT_CODE").equals("")) {
	    	  selAccountData.setData("START_DATE", startTime);
		      selAccountData.setData("END_DATE",endTime);
		      selAccountData.setData("ORDER_CODE",this.getValue("ORDER_CODE"));
		      selAccountData.setData("ORDER_DESC",this.getValue("ORDER_DESC"));
		      result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
		}else{
				  selAccountData.setData("START_DATE", startTime);
			      selAccountData.setData("END_DATE",endTime);
			      selAccountData.setData("ORDER_CODE",this.getValue("ORDER_CODE"));
			      selAccountData.setData("ORDER_DESC",this.getValue("ORDER_DESC"));
			      selAccountData.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
			      result = UDDNewTool.getInstance().getPatientDetail(selAccountData);
			}
		}
	      if(result.getCount()<=0){
	         this.messageBox("没有要查询的数据");
	          table.removeRowAll();	
	          return;
	}
	    //总金额
	      double totalAmt = 0.0;
	      int count = result.getCount();
	      TOT = (TNumberTextField)this.getComponent("TOT");
	      //循环累加
	      for (int i = 0; i < count; i++) {
	          double temp = result.getDouble("SUM_AMT", i);
	          totalAmt += temp;
//	          sumDispenseQty+=result.getInt("SUM_QTY", i);
	          result.setData("SUM_QTY",i,result.getInt("SUM_QTY", i));
	      }
	      TOT.setValue(totalAmt);
	      result.setData("REGION_CHN_DESC", count, "总计:");
	      result.setData("MR_NO", count, "");
	      result.setData("NAME", count, "");
	      result.setData("SEX_CODE", count, "");
	      result.setData("GRADE", count, "");
	      result.setData("CASE_NO", count, "");
	      result.setData("DEPT_DESC", count, "");
	      result.setData("DS_DATE", count, "");
	      result.setData("DAYS", count, "");
	      result.setData("ORDER_CODE", count, "");
	      result.setData("ORDER_DESC", count, "");
	      result.setData("SPECIFICATION", count, "");
	      result.setData("UNIT_DESC", count, "");
	      result.setData("OWN_PRICE", count,"");
	      result.setData("SUM_QTY", count,"");
	      result.setData("SUM_AMT", count, totalAmt);
	      //加载table上的数据
	      this.callFunction("UI|TABLE|setParmValue", result);
	}
	 /**
	  * 汇出excel
	  */
	 public void onExport() {
	     if (table.getRowCount() <= 0) {
	         this.messageBox("没有要汇出的数据");
	         return;
	     }
	     ExportExcelUtil.getInstance().exportExcel(table, "使用抗菌药物出院患者明细");
	 }
	 /**
	  * 清空操作
	  */
	 public void onClear() {
		        String clear = "DEPT_CODE;ORDER_CODE;ORDER_DESC;TOT;MR_NO;NAME";
			    this.clearValue(clear);
			    TTable table = (TTable)this.getComponent("Table");
			    table.removeRowAll();
			    Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(),"yyyyMMdd")));
				Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
				String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
				this.setValue("START_DATE", date);
				this.setValue("END_DATE", end_day);
		
	}
	}
