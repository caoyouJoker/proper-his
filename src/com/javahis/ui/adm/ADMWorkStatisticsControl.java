package com.javahis.ui.adm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 收费员工作量统计
 * </p>
 * 
 * <p>
 * Description:收费员工作量统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012 BlueCore
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 2012-3-20
 * @version 4.0.1
 */
public class ADMWorkStatisticsControl extends TControl{
	private TTable table;
	private TTable tableDetail;//细项
	public ADMWorkStatisticsControl() {

	}

	/*
	 * 初始化
	 */
	public void onInit() {
		initPage();
	}
	private void initPage() {
		table = (TTable) this.getComponent("TABLE");
		tableDetail = (TTable) this.getComponent("TABLE_DETAIL");
		
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// 开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// 结束时间
		this.setValue("USER_ID", Operator.getID());
		table.removeRowAll();
		tableDetail.removeRowAll();

	}
	 /**
     * 清空
     */
    public void onClear(){
    	initPage();
    }
    /**
     * 汇出Excel
     */
    public void onExport() {

        // 得到UI对应控件对象的方法（UI|XXTag|getThis）
        //TTable table = (TTable) callFunction("UI|Table|getThis");
    	TParm parm=tableDetail.getParmValue();
    	if (parm.getCount()<=0) {
    		this.messageBox("没有需要导出的数据");
			return;
		}
        if (tableDetail.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(tableDetail, "收费员工作量统计");
    }
    /**
     * 查询
     */
    public void onQuery(){
    	
    	if (this.getValueString("START_DATE").length() == 0) {
			messageBox("开始时间不正确!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("结束时间不正确!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		
		getQueryParm(startTime, endTime);
    }
    /**
     * 查询第一个页签数据
     * @param startTime
     * @param endTime
     */
    public void getQueryParm(String startTime,String endTime){
    	StringBuffer sql=new StringBuffer();
    	// 预交金数据查询
		sql.append("SELECT A.ADM_CLERK,COUNT(A.CASE_NO) AS COUNT ,B.USER_NAME AS PAT_NAME FROM ADM_INP A,SYS_OPERATOR B "
				+ "WHERE A.ADM_CLERK=B.USER_ID(+) AND IN_DATE BETWEEN TO_DATE('" + startTime
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
				+ "','YYYYMMDDHH24MISS') ");
		// 收费人员
		if (this.getValue("USER_ID").toString().length() > 0) {
			sql.append(" AND ADM_CLERK ='").append(this.getValue("USER_ID"))
					.append("'");
		}
		sql.append(" GROUP BY A.ADM_CLERK,B.USER_NAME ");
    	TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
    	if (returnParm.getErrCode()<0) {
    		this.messageBox("E0005");
			return;
		}
    	if (returnParm.getCount()<=0) {
			this.messageBox("没有需要查询的数据");
			table.removeRowAll();
			tableDetail.removeRowAll();
			return;
		}
    	table.setParmValue(returnParm);
    	tableDetail.removeRowAll();
    }
    /**
     * 单击事件
     */
    public void onTableClick(){
    	TParm parm=table.getParmValue();
    	int row=table.getSelectedRow();
    	if (row<0) {
    		this.messageBox("请选择数据");
			return;
		}
    	String admClerk =parm.getValue("ADM_CLERK",row);//操作人员
    	//开始时间
    	String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
    	//结束时间
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
    	String sql="SELECT C.PAT_NAME,C.SEX_CODE,B.IN_DATE,"
		+ " B.MR_NO,B.IPD_NO"
		+ " FROM ADM_INP B,SYS_PATINFO C"
		+ " WHERE B.MR_NO=C.MR_NO(+)"+
		" AND B.IN_DATE BETWEEN TO_DATE('" + startTime
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
				+ "','YYYYMMDDHH24MISS') "
		+ " AND B.ADM_CLERK='"+admClerk
		+ "'";
    	TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				sql.toString()));
    	if (returnParm.getErrCode()<0) {
			return;
		}
    	tableDetail.setParmValue(returnParm);
    }
    /**
     * 打印
     */
    public void onPrint(){
    	TParm parm=table.getParmValue();

    	if (parm==null || parm.getCount()<=0) {
    		this.messageBox("没有需要打印的数据");
			return;
		}
    	String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyy/MM/dd HH:mm:ss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyy/MM/dd HH:mm:ss");
		
		TParm data2 = new TParm();
        for (int i = 0; i < parm.getCount(); i++) {
        	data2.addData("ADM_CLERK", parm.getData("ADM_CLERK", i)) ;
        	data2.addData("PAT_NAME", parm.getData("PAT_NAME", i)) ;
        	data2.addData("COUNT", parm.getData("COUNT", i)) ;
		}		
        data2.setCount(data2.getCount("ADM_CLERK")) ;
        data2.addData("SYSTEM", "COLUMNS", "ADM_CLERK");
        data2.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        data2.addData("SYSTEM", "COLUMNS", "COUNT");  

        TParm data1 = new TParm();// 打印的数据
        data1.setData("DATATABLE", data2.getData()) ;
        data1.setData("TIME","TEXT",startTime+"~"+endTime) ;
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMWorkStatistics.jhw",data1);
    }
}
