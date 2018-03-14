package com.javahis.ui.mro;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title:介入医师完成术式明细 </p>
 * 
 * <p> Description:介入医师完成术式明细 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company:bluecore </p>
 * 
 * @author wangbin 2014-7-24
 * @version 1.0
 */
public class MROIntOpeControl extends TControl {
	
	private TTable table; // 介入医师完成术式明细
	private String initTableHeader; // 页面表格原始标题
	private String initTableParmMap; // 页面表格原始ParmMap
	private String initTableColHorAlign; // 页面表格数据对齐方式

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // 介入医师完成术式明细
		initTableHeader = this.table.getHeader();
		initTableParmMap = this.table.getParmMap();
		this.initTableColHorAlign = "0,left;1,center;2,left;3,left;4,right;5,center;6,center;7,right";
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
	}
	
	/**
	 * 查询介入医师完成术式明细
	 */
	public void onQuery() {
		// 验证查询条件
		if (StringUtils.isEmpty(this.getValueString("OUT_DATE_START"))
				|| StringUtils.isEmpty(this.getValueString("OUT_DATE_END"))) {
			this.messageBox("请输入出院日期区间");
			return;
		}
		
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT MAX(T.OPE_COUNT) AS MAX_OPE_COUNT FROM (");
		sbSql.append(" SELECT COUNT(A.MR_NO) AS OPE_COUNT,A.CASE_NO ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D,SYS_OPERATIONICD E ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 <> 'B' ");
		sbSql.append(" AND B.OP_CODE=E.OPERATION_ICD ");
		sbSql.append(" AND (E.OPERATION_ICD <='88.50000' OR E.OPERATION_ICD >='88.58000') ");
		sbSql.append(" AND A.DS_DATE BETWEEN TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_START").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') AND TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_END").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') ");
		// 科室编码
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(this.getValueString("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// 病案号
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(this.getValueString("MR_NO"));
			sbSql.append("' ");
		}
		sbSql.append(" GROUP BY A.CASE_NO ");
		sbSql.append(" ORDER BY OPE_COUNT DESC) T ");
		
		// 取得指定查询范围内做介入手术的最大值，以最大值为基准构筑表的列
		TParm maxOpeCountParm = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (maxOpeCountParm.getErrCode() < 0) {
			err("ERR:" + maxOpeCountParm.getErrCode() + maxOpeCountParm.getErrText()
					+ maxOpeCountParm.getErrName());
			this.messageBox("查询数据出现错误");
			return;
		}
		
		if (maxOpeCountParm.getCount() <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("未查到数据");
			return;
		}
		
		sbSql = new StringBuilder();
		sbSql.append(" SELECT DISTINCT * FROM (");
		sbSql.append(" SELECT  A.CASE_NO,A.DEPT_CODE, A.MR_NO, A.IPD_NO, C.PAT_NAME, C.SEX_CODE, ");
		sbSql.append(" FLOOR(MONTHS_BETWEEN( SYSDATE, C.BIRTH_DATE) / 12) AS AGE, A.IN_DATE, A.DS_DATE, ");
		sbSql.append(" CASE WHEN TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) < 1 THEN 1 ELSE TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) END AS REAL_STAY_DAYS ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D,SYS_OPERATIONICD E ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 <> 'B' ");
		sbSql.append(" AND B.OP_CODE=E.OPERATION_ICD ");
		sbSql.append(" AND (E.OPERATION_ICD <='88.50000' OR E.OPERATION_ICD >='88.58000') ");
		sbSql.append(" AND A.DS_DATE BETWEEN TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_START").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') AND TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_END").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') ");
		// 科室编码
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(this.getValueString("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// 病案号
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(this.getValueString("MR_NO"));
			sbSql.append("' ");
		}
		sbSql.append(" ) T ");
		sbSql.append(" ORDER BY T.DEPT_CODE, T.MR_NO, T.IPD_NO ");
		
		// 取得指定查询范围内病患信息
		TParm patInfoParm = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (patInfoParm.getErrCode() < 0) {
			err("ERR:" + patInfoParm.getErrCode() + patInfoParm.getErrText()
					+ patInfoParm.getErrName());
			this.messageBox("查询数据出现错误");
			return;
		}
		
		int patCount = patInfoParm.getCount();
		if (patCount <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("未查到数据");
			return;
		}
		
		// 根据检索条件查询数据
		sbSql = new StringBuilder();
		sbSql.append(" SELECT * FROM ( ");
		sbSql.append(" SELECT A.DEPT_CODE, A.CASE_NO, A.MR_NO, A.IPD_NO, C.PAT_NAME, C.SEX_CODE, ");
		sbSql.append(" FLOOR(MONTHS_BETWEEN( SYSDATE, C.BIRTH_DATE) / 12) AS AGE, A.IN_DATE, A.DS_DATE, ");
		sbSql.append(" CASE WHEN TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) < 1 THEN 1 ELSE TRUNC(A.DS_DATE) - TRUNC(A.IN_DATE) END AS REAL_STAY_DAYS, ");
		sbSql.append(" B.OP_CODE, B.OP_DESC, B.OP_DATE, B.SEQ_NO, NVL(B.MAIN_FLG,'N') AS MAIN_FLG, B.MAIN_SUGEON ");
		sbSql.append(" FROM ADM_INP A, MRO_RECORD_OP B, SYS_PATINFO C, SYS_DEPT D,SYS_OPERATIONICD E ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO ");
		sbSql.append(" AND A.MR_NO = C.MR_NO ");
		sbSql.append(" AND A.DS_DEPT_CODE = D.DEPT_CODE ");
		sbSql.append(" AND D.DEPT_CAT1 <> 'B' ");
		sbSql.append(" AND B.OP_CODE = E.OPERATION_ICD ");
		// 根据国家规定88.50000-88.58000范围内的是外科手术，其余是介入手术
		sbSql.append(" AND (E.OPERATION_ICD <= '88.50000' OR E.OPERATION_ICD >= '88.58000')");
		sbSql.append(" AND A.DS_DATE BETWEEN TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_START").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') AND TO_DATE('");
		sbSql.append(this.getValueString("OUT_DATE_END").split(" ")[0]);
		sbSql.append("', 'YYYY/MM/DD') ");
		
		// 科室编码
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(this.getValueString("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// 病案号
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(this.getValueString("MR_NO"));
			sbSql.append("' ");
		}
		
		sbSql.append(") T");
		sbSql.append(" ORDER BY T.DEPT_CODE, T.MR_NO, T.IPD_NO, T.MAIN_FLG DESC, T.SEQ_NO");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox("查询数据出现错误");
			return;
		}
		
		int dataCount = result.getCount();
		if (dataCount <= 0) {
			this.table.setParmValue(new TParm());
			this.messageBox("未查到数据");
			return;
		}
		
		// 取得查询期间内做介入手术最多的病人的手术次数
		int maxOpeCount = maxOpeCountParm.getInt("MAX_OPE_COUNT", 0);
		// 现有表格的标题
		String tableHeader = this.initTableHeader + ";";
		// 现有表格的ParmMap
		String parmMap = this.initTableParmMap + ";";
		// 现有表格的数据对齐方式
		String tableColHorAlign = this.initTableColHorAlign + ";";
		
		for (int i = 1; i <= maxOpeCount; i++) {
			tableHeader = tableHeader + "术式" + i + ",200;";
			tableHeader = tableHeader + "编码" + ",80;";
			
			parmMap = parmMap + "OP_DESC" + i + ";OP_CODE" + i + ";";
		}
		
		tableHeader = tableHeader + "术者,80,OPERATOR_CODE";
		parmMap = parmMap + "MAIN_SUGEON";
		
		// 根据数据的个数计算数据的对齐方式
		for (int j = 1; j <= maxOpeCount*2 + 1; j++) {
			tableColHorAlign = tableColHorAlign + (7 + j) + ",left;";
		}
		
		// 重新设置表格标题
		this.table.setHeader(tableHeader);
		// 重新设定表格ParmMap
		this.table.setParmMap(parmMap);
		// 重新设定表格数据对齐方式
		this.table.setColumnHorizontalAlignmentData(tableColHorAlign);
		
		for (int i = 0; i < patCount; i++) {
			int count = 0;
			for (int j = 0; j < dataCount; j++) {
				if (StringUtils.equals(patInfoParm.getValue("CASE_NO", i), result.getValue("CASE_NO", j))) {
					count = count + 1;
					patInfoParm.setData("OP_DESC" + count, i, result.getValue("OP_DESC", j));
					patInfoParm.setData("OP_CODE" + count, i, result.getValue("OP_CODE", j));
					// 相同就诊号的数据，result中的第一条为主手术，因为查询条件中增加了是否主手术的排序
					if (count == 1) {
						// 手术时间
						patInfoParm.setData("OP_DATE", i, StringTool.getString(result.getTimestamp("OP_DATE", j), "yyyy/MM/dd"));
						// 术者
						patInfoParm.setData("MAIN_SUGEON", i, result.getValue("MAIN_SUGEON", j));
					}
				}
			}
			
			// 根据maxOpeCount的值填充剩余的数据
			for (int k = count + 1; k <= maxOpeCount; k++) {
				patInfoParm.setData("OP_DESC" + k, i, "");
				patInfoParm.setData("OP_CODE" + k, i, "");
				
			}
		}
		
		this.table.setParmValue(patInfoParm);
	}
	
	/**
	 * 根据病案号查询
	 */
	public void onQueryByMrNo() {
		// 取得病案号
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			// modify by huangtt 20160929 EMPI患者查重提示 start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			this.setValue("MR_NO", mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				this.setValue("MR_NO", pat.getMrNo());// 病案号
			}
			// modify by huangtt 20160929 EMPI患者查重提示 start
			this.onQuery();
		}
	}
	
	/**
	 * 导出报表
	 */
	public void onExport() {
		if (this.table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "介入医师完成术式明细");
		} else {
			this.messageBox("没有需要导出的数据");
			return;
		}
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.onInit();
		this.setValue("DEPT_CODE", "");
		this.setValue("MR_NO", "");
		this.table.setParmValue(new TParm());
	}
}
