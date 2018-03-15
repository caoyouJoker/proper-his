package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;

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
 * Description: 临床路径时程明细报表
 * </p>
 * 
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben  2015-5-11
 * @version 1.0
 */
public class CLPMainSchdDetailWordControl  extends TControl{
	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 身份别
	private TComboBox ctz1Code;
	// 科室
	private TTextFormat deptCode;
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
	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径时程明细表");
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
         " CM.SCHD_AMT, RD.TOT_AMT,RD.SCHD_CODE,RG.REGION_CHN_ABN AS REGION_CHN_DESC,"+
         " P.DEPT_CODE, P.STATION_CODE, DN.DURATION_CHN_DESC AS SCHD_NAME,BF.CLNCPATH_CHN_DESC,P.MR_NO,O.PAT_NAME," +
         "TO_CHAR(P.IN_DATE,'YYYY/MM/DD') IN_DATE,TO_CHAR(P.DS_DATE,'YYYY/MM/DD') DS_DATE,S.CTZ_DESC FROM "+
         " CLP_THRPYSCHDM CM,ADM_INP P ,SYS_REGION RG,CLP_BSCINFO BF,SYS_DEPT D,SYS_STATION ST,SYS_PATINFO O,"+
         "CLP_MANAGEM M,SYS_CTZ S, CLP_DURATION DN,("+
         "SELECT SUM(TOT_AMT) TOT_AMT,A.SCHD_CODE,A.CASE_NO,P.CLNCPATH_CODE,M.SEQ  FROM IBS_ORDD A,ADM_INP P,SYS_CTZ S,CLP_THRPYSCHDM M "+
         "WHERE A.CASE_NO=P.CASE_NO AND P.CTZ1_CODE=S.CTZ_CODE AND P.CLNCPATH_CODE=M.CLNCPATH_CODE AND A.SCHD_CODE=M.SCHD_CODE AND P.DS_DATE IS NOT NULL "+selectCondition+admSelectCondition+regionSelect+stationSelect+" AND A.SCHD_CODE IS NOT NULL AND P.CLNCPATH_CODE IS NOT NULL " +
         "GROUP BY A.SCHD_CODE,A.CASE_NO,P.CLNCPATH_CODE,M.SEQ ) RD    "+
         "WHERE CM.SCHD_CODE=RD.SCHD_CODE  "+
         "AND RD.CLNCPATH_CODE=CM.CLNCPATH_CODE "+
         "AND P.CLNCPATH_CODE=CM.CLNCPATH_CODE   "+
         "AND P.REGION_CODE=RG.REGION_CODE "+
         "AND P.DEPT_CODE=D.DEPT_CODE(+) "+
         "AND P.CTZ1_CODE = S.CTZ_CODE "+
         "AND P.MR_NO=O.MR_NO "+
         "AND P.CLNCPATH_CODE = BF.CLNCPATH_CODE "+
         "AND P.STATION_CODE = ST.STATION_CODE(+) "+
         "AND P.CASE_NO=M.CASE_NO "+
         "AND P.CASE_NO=RD.CASE_NO "+
         "AND P.CLNCPATH_CODE = M.CLNCPATH_CODE "+
         "AND P.CLNCPATH_CODE =RD.CLNCPATH_CODE "+
         "AND M.DELETE_DTTM IS NULL "+
         "AND P.CLNCPATH_CODE IS NOT NULL AND P.DS_DATE IS NOT NULL "+
         "AND  CM.SCHD_CODE=DN.DURATION_CODE(+) AND P.CLNCPATH_CODE IS NOT NULL "+
         selectCondition+admSelectCondition+regionSelect+stationSelect+" ORDER BY P.DEPT_CODE, P.STATION_CODE,P.MR_NO, M.CLNCPATH_CODE,RD.SEQ ";
		TParm searchParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (searchParm.getErrCode()<0) {
			this.messageBox("查询出现错误");
			return null;
		}
		return searchParm;
	}
}
