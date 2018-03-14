package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 临床路径时程报表
 * </p>
 * 
 * 
 * <p>
 * Description: 临床路径时程报表
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 
 * @version 1.0
 */
public class CLPMainSchdTotWordControl  extends TControl{
	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 身份别
	private TComboBox ctz1Code;
	// 科室
	private TTextFormat deptCode;
	// 年龄
	private TTextField age;
	// 转归别
	private TComboBox disCHCOde;
	// 手术标记
	private TRadioButton isOP;
	// 非手术标记
	private TRadioButton notOp;
	// 病区
	private TTextFormat stationCode;

	// 表格
	private TTable table;

	public void onInit() {
		super.onInit();
		initPage();
		initControl();
	}

	/**
	 * 初始化控件
	 */
	public void initControl() {
		start_date = (TTextFormat) this.getComponent("START_DATE");
		end_date = (TTextFormat) this.getComponent("END_DATE");
		table = (TTable) this.getComponent("CLPTABLE");
		// 科室
		deptCode = (TTextFormat) this.getComponent("DEPT_CODE");

		// 病区
		this.stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		table = (TTable) this.getComponent("CLPTABLE");
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.callFunction("UI|CLPTABLE|removeRowAll");
		this.setValue("REGION_CODE", Operator.getRegion());
		// 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (this.table.getRowCount() <= 0) {
			this.messageBox("没有要打印的数据");
			return;
		}
		TParm prtParm = new TParm();
		// 表头
		// prtParm.setData("TITLE","TEXT","临床路径管理总表");
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "所有院区")
				+ "临床路径时程总表");
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate)) {
			startDate = startDate.substring(0, (startDate.length() - 2));
		}
		if (this.checkNullAndEmpty(endDate)) {
			endDate = endDate.substring(0, (endDate.length() - 2));
		}
		prtParm.setData("START_DATE", "TEXT", startDate); // 制表时间start
		prtParm.setData("END_DATE", "TEXT", endDate); // 制表时间end
		TParm prtTableParm = new TParm();
		TParm parm=table.getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			prtTableParm.addData("REGION_CHN_ABN", parm.getValue("REGION_CHN_ABN",i));
			prtTableParm.addData("DEPT_CHN_DESC", parm.getValue("DEPT_CHN_DESC",i));
			prtTableParm.addData("STATION_DESC", parm.getValue("STATION_DESC",i));
			prtTableParm.addData("CLNCPATH_CHN_DESC", parm.getValue("CLNCPATH_CHN_DESC",i));
			prtTableParm.addData("TOTAL", parm.getInt("TOTAL",i));
			prtTableParm.addData("AVERAGECOST",StringTool.round(parm.getDouble("AVERAGECOST",i), 2) );
			prtTableParm.addData("REALCOST", StringTool.round(parm.getDouble("REALCOST",i),2));
			prtTableParm.addData("COSEDIFF", StringTool.round(parm.getDouble("COSEDIFF",i),2));
			prtTableParm.addData("STAYHOSP_DAYS", parm.getInt("STAYHOSP_DAYS",i));
			prtTableParm.addData("REAL_STAYHOSP_DAYS", parm.getInt("REAL_STAYHOSP_DAYS",i));
			prtTableParm.addData("STAYHOSP_DAYS_DIFF", parm.getInt("STAYHOSP_DAYS_DIFF",i));
		}
		prtTableParm.setCount(parm.getCount());
		// wangzhilei 20120723 添加
		prtTableParm.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");
		// wangzhilei 20120723 添加
		prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "TOTAL");
		prtTableParm.addData("SYSTEM", "COLUMNS", "AVERAGECOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "REALCOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "COSEDIFF");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM","COLUMNS","REAL_STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS_DIFF");
		prtParm.setData("CLPTABLE", prtTableParm.getData());
		// 表尾
		 prtParm.setData("NAME", "TEXT", Operator.getName());
		//prtParm.setData("NAME", "TEXT", "制作人：" + Operator.getName());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPNewMainManagePrt.jhw", prtParm);
	}

	public void onQuery() {
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (!this.checkNullAndEmpty(startDate) || !this.checkNullAndEmpty(endDate)) {
			this.messageBox("请输入统计时间");
			return;
		}
		TParm parm = this.getSelectTParm();
		if (parm==null) {
			table.removeRowAll();
			return;
		}
		if (parm.getCount()<=0) {
			this.messageBox("查无数据");
			table.removeRowAll();
			return;
		}
		for (int i = 0; i <parm.getCount(); i++) {
			parm.setData("COSEDIFF",i,StringTool.round(parm.getDouble("SCHD_AMT",i)-parm.getDouble("TOT_AMT",i),2));
		}
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		String regionSelect="";
		// REGION 过滤条件
		if (null!=Operator.getRegion()) {
			regionSelect = " AND P.REGION_CODE='" + Operator.getRegion()
			+ "' ";
		}
		if(((TRadioButton) this.getComponent("RDO_OWN")).isSelected()) {//pangben 2015-5-5身份添加条件
			regionSelect+=" AND S.MAIN_CTZ_FLG='Y' AND S.NHI_CTZ_FLG='N' ";
		}else if(((TRadioButton) this.getComponent("RDO_INS")).isSelected()) {//pangben 2015-5-5身份添加条件
			if (this.getValueString("SYS_CTZ").length()>0) {
				regionSelect+=" AND S.NHI_CTZ_FLG='Y' AND S.CTZ_CODE='"+this.getValueString("SYS_CTZ")+"' ";	
			}else{
				regionSelect+=" AND S.NHI_CTZ_FLG='Y' ";
			}
		}
		
		// 病区查询条件
		String stationSelect = "";
		if (null!=this.stationCode.getValue() && this.checkNullAndEmpty(this.stationCode.getValue().toString())) {
			stationSelect += " AND P.DS_STATION_CODE ='"
					+ this.stationCode.getValue().toString() + "' ";
		}
		// 得到时间查询条件
		String selectCondition = "";
		String admSelectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND P.DS_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}
		// 科室
		String deptCode = this.deptCode.getComboValue();
		if (this.checkNullAndEmpty(deptCode)) {
			admSelectCondition += " AND P.DS_DEPT_CODE='" + deptCode + "' ";
		}
		String clncPath = this.getValueString("CLNCPATH_CODE");
		if (this.checkNullAndEmpty(clncPath)) {
			admSelectCondition+=" AND P.CLNCPATH_CODE='"+clncPath+"' ";
		}
		String sql="SELECT  ST.STATION_DESC, M.CLNCPATH_CODE, D.DEPT_CHN_DESC,"+
         " SUM(CM.SCHD_AMT) SCHD_AMT, SUM(RD.TOT_AMT) TOT_AMT,RD.SCHD_CODE,RG.REGION_CHN_ABN AS REGION_CHN_DESC,"+
         " P.DEPT_CODE, P.STATION_CODE, DN.DURATION_CHN_DESC AS SCHD_NAME,BF.CLNCPATH_CHN_DESC FROM "+
         " CLP_THRPYSCHDM CM,ADM_INP P ,SYS_REGION RG,CLP_BSCINFO BF,SYS_DEPT D,SYS_STATION ST,"+
         "CLP_MANAGEM M,SYS_CTZ S, CLP_DURATION DN,("+
         "SELECT SUM(TOT_AMT) TOT_AMT,A.SCHD_CODE,A.CASE_NO,P.CLNCPATH_CODE  FROM IBS_ORDD A,ADM_INP P,SYS_CTZ S "+
         "WHERE A.CASE_NO=P.CASE_NO AND P.CTZ1_CODE=S.CTZ_CODE "+selectCondition+admSelectCondition+regionSelect+stationSelect+" AND A.SCHD_CODE IS NOT NULL AND P.CLNCPATH_CODE IS NOT NULL " +
         "GROUP BY A.SCHD_CODE,A.CASE_NO,P.CLNCPATH_CODE ) RD    "+
         "WHERE CM.SCHD_CODE=RD.SCHD_CODE  "+
         "AND RD.CLNCPATH_CODE=CM.CLNCPATH_CODE "+
         "AND P.CLNCPATH_CODE=CM.CLNCPATH_CODE   "+
         "AND P.REGION_CODE=RG.REGION_CODE "+
         "AND P.DEPT_CODE=D.DEPT_CODE(+) "+
         "AND P.CTZ1_CODE = S.CTZ_CODE "+
         "AND P.CLNCPATH_CODE = BF.CLNCPATH_CODE "+
         "AND P.STATION_CODE = ST.STATION_CODE(+) "+
         "AND P.CASE_NO=M.CASE_NO "+
         "AND P.CASE_NO=RD.CASE_NO "+
         "AND P.CLNCPATH_CODE = M.CLNCPATH_CODE "+
         "AND P.CLNCPATH_CODE =RD.CLNCPATH_CODE "+
         "AND M.DELETE_DTTM IS NULL "+
         "AND P.CLNCPATH_CODE IS NOT NULL "+
         "AND  CM.SCHD_CODE=DN.DURATION_CODE(+) AND P.CLNCPATH_CODE IS NOT NULL "+
         selectCondition+admSelectCondition+regionSelect+stationSelect+" GROUP BY ST.STATION_DESC, M.CLNCPATH_CODE, D.DEPT_CHN_DESC,"+
         "RD.SCHD_CODE,RG.REGION_CHN_ABN, P.DEPT_CODE, P.STATION_CODE, DN.DURATION_CHN_DESC,BF.CLNCPATH_CHN_DESC ORDER BY P.DEPT_CODE,P.STATION_CODE,M.CLNCPATH_CODE,RD.SCHD_CODE";
		TParm searchParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (searchParm.getErrCode()<0) {
			this.messageBox("查询出现错误");
			return null;
		}
		return searchParm;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		this.age.setValue("");
		this.ctz1Code.setValue("");
		this.deptCode.setValue("");
		this.disCHCOde.setValue("");
		this.stationCode.setValue("");
		table.removeRowAll();
		((TRadioButton) this.getComponent("RDO_SEL")).setSelected(true);
		onRdoSel();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径时程总表");
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * 整数验证方法
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validInt(String validData) {
		Pattern p = Pattern.compile("([0]{1})|([1-9]{1}[0-9]*)");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}
	/**
	 * 
	* @Title: onRdoSel
	* @Description: TODO(根据身份查询数据)
	* @author Dangzhang
	* @throws
	 */
	public void onRdoSel(){
		if(((TRadioButton) this.getComponent("RDO_SEL")).isSelected()) {
			callFunction("UI|SYS_CTZ|setEnabled", false);
		}else if(((TRadioButton) this.getComponent("RDO_OWN")).isSelected()) {//pangben 2015-5-5身份添加条件
			callFunction("UI|SYS_CTZ|setEnabled", false);
		}else if(((TRadioButton) this.getComponent("RDO_INS")).isSelected()) {//pangben 2015-5-5身份添加条件
			callFunction("UI|SYS_CTZ|setEnabled", true);
		}
		this.setValue("SYS_CTZ", "");
	}
}
