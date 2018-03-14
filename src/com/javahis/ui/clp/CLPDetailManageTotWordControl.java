package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import java.util.Vector;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.base.TComboBoxModel;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>
 * Title: 临床路径管理细表
 * </p>
 * 
 * <p>
 * Description: 临床路径管理细表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author luhai
 * @version 1.0
 */
public class CLPDetailManageTotWordControl extends TControl {
	public CLPDetailManageTotWordControl() {

	}

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
	// 临床路径
	private TTextFormat clncPathCode;
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
		// 身份别
		ctz1Code = (TComboBox) this.getComponent("CTZ1_CODE");
		// 科室
		deptCode = (TTextFormat) this.getComponent("DEPT_CODE");
		// 年龄
		age = (TTextField) this.getComponent("AGE");
		// 转归别
		disCHCOde = (TComboBox) this.getComponent("DISCH_CODE");
		// 手术标记
		isOP = (TRadioButton) this.getComponent("isOP");
		// 非手术标记
		notOp = (TRadioButton) this.getComponent("notOP");
		// 病区
		this.stationCode = (TTextFormat) this.getComponent("STATION_CODE");
		//临床路径
		this.clncPathCode = (TTextFormat) this.getComponent("CLNCPATH_CODE");
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1周
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.callFunction("UI|CLPTABLE|removeRowAll");
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
		// prtParm.setData("TITLE","TEXT","临床路径管理细表");
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "所有院区")
				+ "临床路径管理细表");
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
		TParm prtTableParm = this.getSelectTParm();
		prtTableParm.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");
		prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		prtTableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		// prtTableParm.addData("SYSTEM","COLUMNS","TOTAL");
		prtTableParm.addData("SYSTEM", "COLUMNS", "AVERAGECOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "REALCOST");
		prtTableParm.addData("SYSTEM", "COLUMNS", "COSEDIFF");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM", "COLUMNS", "REAL_STAYHOSP_DAYS");
		prtTableParm.addData("SYSTEM", "COLUMNS", "STAYHOSP_DAYS_DIFF");
		prtParm.setData("CLPTABLE", prtTableParm.getData());
		// 表尾
		prtParm.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPNewDetailManagePrt.jhw", prtParm);
	}

	public void onQuery() {
		TParm parm = this.getSelectTParm();
		
		if (parm == null) {
			this.messageBox("查无数据!");
			table.removeRowAll();			
			return;			
		}
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		TParm tableparm = new TParm();
		StringBuffer sqlbf = new StringBuffer();
		String regionSelect="";
		// REGION 过滤条件
		if (null!=Operator.getRegion()) {
			regionSelect = " AND AD.REGION_CODE='" + Operator.getRegion()
			+ "' ";
		}
		// 病区查询条件
		String stationSelect = "";
		if (null!=this.stationCode.getValue() && this.checkNullAndEmpty(this.stationCode.getValue().toString())) {
			stationSelect += " AND AD.DS_STATION_CODE ='"
					+ this.stationCode.getValue().toString() + "' ";
		}
		// 临床路径--xiongwg20150508
		String clncPathSelect = "";
		if (null!=this.clncPathCode.getValue() && this.checkNullAndEmpty(this.clncPathCode.getValue().toString())) {
			clncPathSelect += " AND M.CLNCPATH_CODE ='"
					+ this.clncPathCode.getValue().toString() + "' ";
		}
		// 得到时间查询条件
		String selectCondition = "";
		String admSelectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND AD.DS_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}
		// 身份别
		String ctz1Code = this.ctz1Code.getValue();
		if (this.checkNullAndEmpty(ctz1Code)) {
			selectCondition += " AND MR.CTZ1_CODE='" + ctz1Code + "'";
		}
		// 年龄
		String age = this.age.getValue();
		if (this.validInt(age)) {
			selectCondition += " AND (TO_NUMBER(TO_CHAR(SYSDATE,'YYYY')) -TO_NUMBER(TO_CHAR((MR.BIRTH_DATE),'YYYY'))) >= "
					+ age + " ";
		}
		// 科室
		String deptCode = this.deptCode.getComboValue();
		if (this.checkNullAndEmpty(deptCode)) {
			admSelectCondition += " AND AD.DS_DEPT_CODE='" + deptCode + "' ";
		}
		// 转归别
		String disCHCOde = this.disCHCOde.getValue();
		if (this.checkNullAndEmpty(disCHCOde)) {
			admSelectCondition += " AND AD.DISCH_CODE='" + disCHCOde + "' ";
		}
		// 手术表str
		String opTableStr = "";
		// 连接手术表条件
		String opWhereStr = "";
		// 手术判断
		if (this.isOP.isSelected()) {
			opTableStr = ",MRO_RECORD_OP OP ";
			opWhereStr = " AND OP.CASE_NO=MR.CASE_NO AND OP.SEQ_NO IS NOT NULL ";
		}

		//====modify by caowl 20120910 start
		// 得到临床路径信息
		String sql = "SELECT  R.REGION_CHN_ABN,ST.STATION_DESC,B.AVERAGECOST AS AVERAGECOST, "+
					       "  M.CLNCPATH_CODE,CASE WHEN MR.SUM_TOT IS NULL THEN 0  ELSE TO_NUMBER(MR.SUM_TOT) END AS REALCOST ,"+
					       "  CASE WHEN MR.SUM_TOT IS NULL THEN 0 ELSE TO_NUMBER(MR.SUM_TOT)-B.AVERAGECOST END AS COSEDIFF,D.DEPT_CHN_DESC,"+
					       "  B.CLNCPATH_CHN_DESC,B.STAYHOSP_DAYS,"+
					       "  CASE WHEN ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE)) <= 0 THEN 1 ELSE ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE)) END AS REAL_STAYHOSP_DAYS,"+
					       "  CASE WHEN ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE)) <= 0 THEN 1 ELSE ROUND (TO_NUMBER (MR.OUT_DATE - MR.IN_DATE))-B.STAYHOSP_DAYS END  AS STAYHOSP_DAYS_DIFF,"+
					       "  AD.DEPT_CODE,AD.STATION_CODE,AD.MR_NO,AD.IPD_NO,M.CASE_NO,TO_CHAR(MR.OUT_DATE,'YYYY-MM-DD') AS OUT_DATE"+
					 " FROM ADM_INP AD,MRO_RECORD MR,SYS_DEPT D,SYS_STATION ST,CLP_BSCINFO B,SYS_REGION R, CLP_MANAGEM M"+opTableStr+
					 " WHERE AD.CASE_NO = MR.CASE_NO"+
					   "  AND AD.CASE_NO = M.CASE_NO"+
					   "  AND AD.REGION_CODE = R.REGION_CODE(+)"+
					   "  AND M.CLNCPATH_CODE = B.CLNCPATH_CODE"+
					   "  AND AD.DEPT_CODE = D.DEPT_CODE"+
					   "  AND AD.STATION_CODE = ST.STATION_CODE"+
					   "  AND MR.OUT_DATE IS NOT NULL AND M.DELETE_DTTM IS NULL ";
		sql += regionSelect;
		sql += selectCondition;
		sql += stationSelect;
		sql += clncPathSelect;
		sql += admSelectCondition;
		sql += opWhereStr;		
		sql += " ORDER BY AD.DEPT_CODE,AD.STATION_CODE,M.CLNCPATH_CODE";
		// ===modify by caowl 20120910 end		
		tableparm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (tableparm.getCount() <= 0) {
			
			return null;
		}
	   
		//TParm prtTableParm = new TParm();
		// 处理查询出来的parm
		// 统计信息begin
//		int total = 0;
//		double standardFee = 0.00;
//		double realFee = 0.00;
//		double coseDiff = 0.00;
//		DecimalFormat formatObject = new DecimalFormat("###########0.00");
//		int stayHospDay = 0; // 标准住院天数
//		int diffHospDay = 0; // 天数差异
//		int realStayHospDays=0;
//		// 统计信息end
		TParm selParm =null;
		String sql1=null;
//		//TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
		for (int i = 0; i < tableparm.getCount(); i++) {
			//TParm rowParm = tableparm.getRow(i);
			//======modify by caowl 20120910 start 
			sql1 = "SELECT END_DTTM FROM CLP_MANAGEM_HISTORY WHERE CASE_NO = '"+
			tableparm.getData("CASE_NO",i).toString()+"' AND END_DTTM IS NOT NULL";
			selParm = new TParm(TJDODBTool.getInstance().select(sql1));			
			if(selParm.getCount()>0){
				tableparm.setData("EXIT_CODE",i,"溢出");				
			}else{
				tableparm.setData("EXIT_CODE",i,"");				
			}
			//=======modify by caowl 20120910 end
//			total += rowParm.getInt("TOTAL");
//			standardFee += rowParm.getDouble("AVERAGECOST");
//			// 实际费用
//			realFee += rowParm.getDouble("REALCOST");
//			// 差异
//			coseDiff += rowParm.getDouble("COSEDIFF");
//			realStayHospDays+=rowParm.getInt("REAL_STAYHOSP_DAYS");
//			stayHospDay += rowParm.getInt("STAYHOSP_DAYS");
//			// 实际住院天数
//			// 天数差异
//			diffHospDay += rowParm.getInt("STAYHOSP_DAYS_DIFF");
			
		}
//		// 处理总计
//		// wangzhilei 20120723 修改
//		tableparm.addData("REGION_CHN_ABN", "总计：");
//		// wangzhilei 20120723 修改
//		tableparm.addData("CLNCPATH_CHN_DESC", "");
//		
//		tableparm.addData("TOTAL", total);
//		tableparm.addData("AVERAGECOST", formatObject.format(standardFee));
//		// 实际费用
//		tableparm.addData("REALCOST", formatObject.format(realFee));
//		// 差异
//		tableparm.addData("COSEDIFF", formatObject.format(coseDiff));
//		tableparm.addData("STAYHOSP_DAYS", stayHospDay);
//		// 实际住院天数
//		tableparm.addData("REAL_STAYHOSP_DAYS", realStayHospDays);
//		// 天数差异
//		tableparm.addData("STAYHOSP_DAYS_DIFF", diffHospDay);
//		// 病区
//		tableparm.addData("STATION_DESC", "");
//		// 总计end
//		tableparm.setCount(tableparm.getCount()+1);
		return tableparm;
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
		this.clncPathCode.setValue("");
		table.removeRowAll();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径管理细表");
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

}
