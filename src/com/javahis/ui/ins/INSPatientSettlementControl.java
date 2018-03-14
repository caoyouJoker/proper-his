package com.javahis.ui.ins;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 医保结算患者统计列表
 * </p>
 * 
 * <p>
 * Description:医保结算患者统计列表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author lim
 * @version 1.0
 */
public class INSPatientSettlementControl extends TControl {

	/**
	 * 初始化
	 */
	public void onInit() {
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		// 入院开始时间
		setValue("START_DATE", yesterday);
		// 入院结束时间
		setValue("END_DATE", SystemTool.getInstance().getDate());
	}

	/**
	 * 
	 * 查询
	 */
	public void onQuery() {
		String inDate = this.getValueString("START_DATE");// 出院开始日期.
		String dsDate = this.getValueString("END_DATE");// 出院结束日期.
		String stationCode = this.getValueString("STATION_CODE");// 护理站.

		if (!checkInput(inDate, dsDate)) {
			return;
		}
		String sql = "SELECT i.MR_NO,m.PAT_NAME,"
				+ "to_char(m.IN_DATE,'yyyy/mm/dd') IN_DATE,"
				+ "to_char(m.DS_DATE,'yyyy/mm/dd') DS_DATE,"
				+ "(m.PHA_AMT+m.EXM_AMT+m.TREAT_AMT+m.OP_AMT+m.BED_AMT+m.MATERIAL_AMT+m.OTHER_AMT+m.BLOODALL_AMT+m.BLOOD_AMT) TOTAL_AMT,"
				+ "(m.RESTART_STANDARD_AMT+m.STARTPAY_OWN_AMT+m.OWN_AMT+m.PERCOPAYMENT_RATE_AMT+m.ADD_AMT+INS_HIGHLIMIT_AMT+m.TRANBLOOD_OWN_AMT) OWN_AMT,"
				+ "(m.PHA_NHI_AMT+m.EXM_NHI_AMT+m.TREAT_NHI_AMT+m.OP_NHI_AMT+m.BED_NHI_AMT+m.MATERIAL_NHI_AMT+m.OTHER_NHI_AMT+m.BLOODALL_NHI_AMT+m.BLOOD_NHI_AMT) NHI_AMT,"
				+ "m.PHA_AMT," + "(SELECT sum(d.total_amt) "
				+ "FROM INS_IBS_ORDER  d, sys_fee f "
				+ "WHERE d.year_mon=m.year_mon  " 
				+ "AND  d.case_no=m.case_no "
				+ "AND  f.own_price>=100  "
				+ "AND  d.order_code=f.order_code "
				+ "AND  f.ORDER_CAT1_CODE ='PHA_W' "
				+ "AND  f.ACTIVE_FLG ='Y' ) SPHA ,"
				+ "'' as SPACE1,m.MATERIAL_AMT,"
				+ "( SELECT sum(d.total_amt) "
				+ " FROM INS_IBS_ORDER  d, sys_fee f "
				+ " WHERE  d.year_mon=m.year_mon  "
				+ " AND    d.case_no=m.case_no  "
				+ " AND    d.order_code=f.order_code"
				+ " AND    f.own_price>=500  "
				+ " AND    f.ORDER_CAT1_CODE ='MAT' "
				+ " AND    f.ACTIVE_FLG ='Y' ) SMATERIAL_AMT,'' as SPACE2 "
				+ "  FROM INS_IBS m,ADM_INP i "
				+ " WHERE  M.CASE_NO=I.CASE_NO  "
				+ "  AND M.IN_DATE BETWEEN TO_DATE('" + SystemTool.getInstance().getDateReplace(inDate,true) + "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ SystemTool.getInstance().getDateReplace(dsDate,false) + "','YYYYMMDDHH24MISS')";
				//+ " AND I.STATION_CODE = '" + stationCode + "'";
				
		if (stationCode.toString().length() > 0) {
			sql+=" AND I.STATION_CODE = '" + stationCode + "'";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("查无数据");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getDouble("PHA_AMT",i)!=0) {
				result.setData("SPACE1",i, StringTool.round((result.getDouble("SPHA",i)/result.getDouble("PHA_AMT",i)*100),2)+"%");
	
			}else{
				result.setData("SPACE1",i, "0.00%");
			}
			if (result.getDouble("TOTAL_AMT",i)!=0) {
				result.setData("SPACE2",i, StringTool.round((result.getDouble("SMATERIAL_AMT",i)/result.getDouble("TOTAL_AMT",i)*100),2)+"%");
			}else{
				result.setData("SPACE2",i, "0.00%");
			}
		}
		this.callFunction("UI|TTABLE|setParmValue", result);
	}

	private boolean checkInput(String inDate, String dsDate) {
		if ("".equals(inDate)) {
			this.messageBox("出院开始日期不能为空");
			return false;
		}
		if ("".equals(dsDate)) {
			this.messageBox("出院结束日期不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 清空
	 */
	public void onClear() {
		this.setValue("START_DATE", "");// 住院日期
		this.setValue("END_DATE", "");
		this.setValue("STATION_CODE", "");
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}

	/**
	 * 
	 * 汇出
	 */
	public void onExport() {
		TTable table = (TTable) this.getComponent("TTABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "医保结算患者统计列表");
	}
}
