package com.javahis.ui.pha;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.bil.BILSysParmTool;
import jdo.pha.PHAEAntibacterialsTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
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
 * 
 * <p>
 * Title: 院内门急诊抗菌药物明细统计
 * </p>
 * 
 * <p>
 * Description: 院内门急诊抗菌药物明细统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangm 2013.3.18
 * @version 1.0
 */
public class PHAEAntibacterialsControl extends TControl{
	private TTable table = new TTable();
	private String header = ""; // 存放表头
	private String parmMap = ""; // 存放表头Map
	private String align = "";//列数据对齐方式
	private String reportFlg = ""; // 报表类型标记

	private StringBuffer startTime = new StringBuffer(); // 开始日期
	private StringBuffer endTime = new StringBuffer(); // 截止日期
	
	private TTextField orderCode;//药品代码
	private TTextField orderDesc;//药品名称
	
	private TTextField mrNo;//病案号
	private TTextField name;//患者姓名
	
	private TComboBox regionCode; //区域
	private TNumberTextField total;//合计金额
	/**
	 * 界面初始化
	 */
	public void onInit() {
		super.onInit();
		this.resetDate(); // 初始化时间控件
		this.initTable(); // 初始化table
		this.initPage();//初始化界面
		// this.initPopeDem(); //初始化权限
	}

	/**
	 * 权限初始化
	 */
	// private void initPopeDem() {
	// // 组长权限
	// if (this.getPopedem("LEADER")) {
	// // callFunction("UI|DR_CODE|setEnabled",true);
	// }
	// // 全院权限
	// if (this.getPopedem("ALL")) {
	// // callFunction("UI|DEPT_CODE|setEnabled",true);
	// // callFunction("UI|DR_CODE|setEnabled",true);
	// }
	// }

	/**
	 * 界面初始化
	 */
	private void initPage(){
		orderCode = (TTextField)this.getComponent("txt_OrderCode");
		orderDesc = (TTextField)this.getComponent("txt_OrderDesc");
		mrNo = (TTextField)this.getComponent("txt_MR_NO");
		name = (TTextField)this.getComponent("txt_Name");
		regionCode = (TComboBox)this.getComponent("cbl_RegionCode");
		regionCode.setValue(Operator.getRegion());
		total = (TNumberTextField)this.getComponent("txt_Sum");
		this.callFunction("UI|cbl_RegionCode|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
	              getValueString("cbl_RegionCode")));
		// 注册激发SYSFeePopup弹出的事件
		orderCode.setPopupMenuParameter("TAG", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"));
        // 定义接受返回值方法
		orderCode.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
		this.setValue("ADM_TYPE", "E");
	}
	
	
	/**
	 * table初始化
	 */
	private void initTable() {
		table = (TTable) this.getComponent("tab_Statistics");
		Map map = this.getTableHeader();
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());
	}

	// table填充方法
	private void fillTable(TParm parm) {
		table = new TTable();
		table = (TTable) this.getComponent("tab_Statistics");
		table.removeRowAll();

		Map map = this.getTableHeader();
		table.setHeader(map.get("header").toString());
		table.setParmMap(map.get("parmMap").toString());
		table.setColumnHorizontalAlignmentData(map.get("align").toString());

		TParm result = new TParm();
		result = PHAEAntibacterialsTool.getInstance().selectReportData(parm);

		if (result.getCount() < 0) {
			this.messageBox("没有符合条件的数据！");
			return;
		}
		
//		int qty = 0;//数量
		BigDecimal amount = new BigDecimal(0);//总金额
		int count = result.getCount();
		
		for (int i = 0; i < count; i++) {
			BigDecimal temp = new BigDecimal(result.getDouble("OWN_AMT", i));
//            amount += temp;
            amount = amount.add(temp);
//            qty+=result.getInt("DOSAGE_QTY", i);
            
            BigDecimal changeFormat = new BigDecimal(result.getDouble("OWN_PRICE", i));
            DecimalFormat obj = new DecimalFormat("###########0.0000");
            result.setData("OWN_PRICE", i, obj.format(changeFormat));
        }
		total.setValue(amount);
		
		result.setData("REGION_CODE", count, "总计:");
		result.setData("ORDER_CODE", count, "");
		result.setData("ORDER_DESC", count, "");
		result.setData("BILL_DATE", count, "");
		result.setData("MR_NO", count, "");
		
		result.setData("PAT_NAME", count, "");
		result.setData("SPECIFICATION", count, "");
		result.setData("UNIT_CHN_DESC", count, "");
		result.setData("OWN_PRICE", count, "");
//		result.setData("DOSAGE_QTY", count, qty);
		result.setData("DOSAGE_QTY", count, "");
		result.setData("OWN_AMT", count, amount);
		
		table.setParmValue(result);
	}

	// 查询按钮触发事件
	public void onQuery() {
		if (!this.checkConditions()) {
			return;
		}
		TParm parm = new TParm();
		parm = this.encParameter(); // 获得查询条件
		fillTable(parm);
	}

	// 报表导出按钮触发事件
	public void onExport() {
		TTable expTable = (TTable) callFunction("UI|tab_Statistics|getThis");
		if (expTable.getRowCount() <= 0) {
			messageBox("无导出资料");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(expTable, "院内急诊抗菌药物明细统计");
	}

	// 清空按钮触发事件
	public void onClear() {
		this.resetDate();
		orderCode.setValue("");
		orderDesc.setValue("");
		mrNo.setValue("");
		name.setValue("");
		total.setValue("");
		table.removeRowAll();
	}

	// 重置时间控件
	private void resetDate() {
		this.setValue("txt_StartDate",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("txt_EndDate", rollDay);
		this.setValue("txt_StartTime", StringTool.getTimestamp("00:00:00",
				"HH:mm:ss"));
		this.setValue("txt_EndTime", StringTool.getTimestamp("23:59:59",
		"HH:mm:ss"));
	}

	// 获得表头
	private Map getTableHeader() {
		header = "区域,120,tbl_RegionCode;药品编码,100,ORDER_CODE;药品名称,180,ORDER_DESC;日期,90,BILL_DATE;病案号,100,MR_NO;姓名,80,PAT_NAME;规格,80,SPECIFICATION;数量,80,double,#########0.00,DOSAGE_QTY;单位,50,UNIT_CHN_DESC;零售价,80,OWN_PRICE;零售金额,80,double,#########0.0000,OWN_AMT";
		parmMap = "REGION_CODE;ORDER_CODE;ORDER_DESC;BILL_DATE;MR_NO;PAT_NAME;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT";
		align = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,left;9,right;10,right";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("header", header);
		map.put("parmMap", parmMap);
		map.put("align", align);
		return map;
	}

	private boolean checkConditions() {
		if (getValueString("txt_StartDate").equals("")
				|| getValueString("txt_EndDate").equals("")) {
			this.messageBox("请选择起讫日期！");
			return false;
		}
		return true;
	}

	// 封装查询参数
	private TParm encParameter() {
		TParm parm = new TParm(); // 参数列表

		startTime = new StringBuffer();
		endTime = new StringBuffer();

		String str = getValueString("txt_StartDate").toString();
		str = str.substring(0, 10).replaceAll("-", "/");

		startTime.append(str);
		startTime.append(" ");

		if (getValueString("txt_StartTime").equals("")) {
			startTime.append("00:00:00");
		} else {
			startTime.append(StringTool.getString(TCM_Transform
					.getTimestamp(getValue("txt_StartTime")), "HH:mm:ss"));
		}

		str = getValueString("txt_EndDate").toString();
		str = str.substring(0, 10).replaceAll("-", "/");

		endTime.append(str);
		endTime.append(" ");

		if (getValueString("txt_EndTime").equals("")) {
			endTime.append("23:59:59");
		} else {
			endTime.append(StringTool.getString(TCM_Transform
					.getTimestamp(getValue("txt_EndTime")), "HH:mm:ss"));
		}

		parm.setData("DATE_START", startTime.toString()); // 开始时间
		parm.setData("DATE_END", endTime.toString()); // 终止时间

		parm.setData("REGION_CODE", regionCode.getValue());//区域
	
		if(!orderCode.getValue().equals("")){
			parm.setData("ORDER_CODE", orderCode.getValue());//药品代码
		}
		if(!mrNo.getValue().equals("")){
			parm.setData("MR_NO", mrNo.getValue());//病案号
		}
		if (this.getValue("ADM_TYPE").toString().length()>0) {
			parm.setData("ADM_TYPE", this.getValue("ADM_TYPE"));//门急别
		}
		reportFlg = "selectEAnti"; // 报表类型标记

		parm.setData("REPORTFLG", reportFlg);
		return parm;
	}
	
	/**
     * 补齐MR_NO
     */
    public void onMrNo() {
        String strMrNo = mrNo.getValue();
		// modify by huangtt 20160930 EMPI患者查重提示 start
		strMrNo = PatTool.getInstance().checkMrno(strMrNo);
		Pat pat = Pat.onQueryByMrNo(strMrNo);
		if (pat == null) {
			mrNo.setValue("");
			name.setValue("");
			this.messageBox("无此病案号!");
			return;
		}
		if (!StringUtil.isNullString(strMrNo) && !strMrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + strMrNo + " 已合并至 " + "" + pat.getMrNo());
			strMrNo = pat.getMrNo();
		}
		name.setValue(pat.getName());
		// modify by huangtt 20160930 EMPI患者查重提示 start
        mrNo.setValue(strMrNo);
        
//        //得到病患名字
//        getPatName(strMrNo);
    }

    /**
     * 获得该病人的姓名
     * @param mrNo String
     */
    private void getPatName(String strMrNo){
    	name.setValue(PatTool.getInstance().getNameForMrno(strMrNo));
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
     * 接受返回值方法
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        System.out.println(parm);
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
        	orderCode.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
        	orderDesc.setValue(order_desc);
    }
}
