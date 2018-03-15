package com.javahis.ui.mro;

import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
 * 
 * <p> Title:各外科手术用血统计导出EXCLE </p>
 * 
 * <p> Description:各外科手术用血统计导出EXCLE </p>
 * 
 * <p> Copyright: Copyright (c) 2011 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author sunqy 20140903
 * @version 2.0
 */
public class MROOpeBloodControl extends TControl{
	
	private TTable table;//表格
	
	/**
	 * 初始化
	 */
	public void onInit(){
		table = (TTable)getComponent("Table");
		timerInit();
	}
	/**
	 * 初始化时间控件
	 */
	private void timerInit() {
		Date now = SystemTool.getInstance().getDate();
		this.setValue("DATE_S", now);
		this.setValue("DATE_E", now);
	}
	/**
	 * 查询
	 */
	public void onQuery(){
		String sql = "SELECT M.OUT_DEPT,COUNT(M.OP_CODE) QTY,SUM(M.RBC) RBC, ROUND(AVG(M.RBC),2) AVG_RBC FROM ";
		String groupSql = "(SELECT A.OUT_DEPT, E.OPT_CHN_DESC OP_CODE, A.RBC " +
				"FROM MRO_RECORD A, SYS_DEPT C, SYS_OPERATIONICD E WHERE  A.OUT_DEPT = C.DEPT_CODE " +
				"AND A.OP_CODE = E.OPERATION_ICD(+)";//SYS_OPERATIONICD 术式字典表
		if(this.getValueString("DEPT_TYPE").length()>0){//科室类别输入
			groupSql += " AND C.DEPT_CAT1 = '" +this.getValueString("DEPT_TYPE")+ "'";
		}
		if(this.getValueString("DEPT_CODE").length()>0){//科室输入
			groupSql += " AND A.OUT_DEPT = '" +this.getValueString("DEPT_CODE")+ "'";
		}
		if(this.getValueString("DATE_S").length()>0){//开始时间输入
			groupSql += " AND A.OUT_DATE > TO_DATE('" +this.getValueString("DATE_S").replaceAll("-", "/").substring(0, 10)+ "','YYYY/MM/DD')";
		}else{
			this.messageBox("开始时间不能为空");
			this.grabFocus("DATE_S");
			return;
		}
		if(this.getValueString("DATE_E").length()>0){//结束时间输入
			groupSql += " AND A.OUT_DATE < TO_DATE('" +this.getValueString("DATE_E").replaceAll("-", "/").substring(0, 10)+ "','YYYY/MM/DD')";
		}else{
			this.messageBox("结束时间不能为空");
			this.grabFocus("DATE_E");
			return;
		}
		groupSql += ") M";
		sql = sql + groupSql;
		sql += " GROUP BY M.OUT_DEPT, M.OP_CODE";
//		System.out.println("sql====:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			messageBox(result.getErrText());
			return;
		}
		if(result.getCount() <= 0){
			this.messageBox("没有查询到数据");
			return;
		}
		table.setParmValue(result);
		
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		callFunction("UI|DEPT_TYPE|setEnabled",true);//科室类别可用
		callFunction("UI|DEPT_CODE|setEnabled",true);//科室可用
		callFunction("UI|DATE_S|setEnabled",true);//开始时间可用
		callFunction("UI|DATE_E|setEnabled",true);//结束时间可用
		this.clearValue("DEPT_TYPE;DEPT_CODE;DATE_S;DATE_E");
		table.removeRowAll();
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0){
			this.messageBox("没有需要导出的数据");
		}else{
			ExportExcelUtil.getInstance().exportExcel(table, "各外科手术用血统计");
		}
	}
}
