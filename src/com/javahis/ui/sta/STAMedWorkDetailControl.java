package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
 * 
 * @author Administrator
 *
 */
public class STAMedWorkDetailControl extends TControl {
	
	private String sqlo =" SELECT A.EXEC_DEPT_CODE,C.DEGREE_CODE,B.ADM_TYPE,A.BILL_DATE,E.PAT_NAME,B.MR_NO,B.CASE_NO,A.ORDER_DESC,A.DR_CODE,F.EXEC_DR_CODE,F.REPORT_DR "+
            " FROM OPD_ORDER A,SYS_FEE C,REG_PATADM B,SYS_PATINFO E,MED_APPLY F "+
            " WHERE  A.CASE_NO=B.CASE_NO "+
            " AND B.REGCAN_USER IS NULL "+
            " AND B.MR_NO=E.MR_NO "+
            " AND B.ADM_TYPE='O' "+
            " AND A.ORDER_CODE=C.ORDER_CODE  "+
            " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE') "+
            " AND A.CAT1_TYPE=F.CAT1_TYPE(+) "+
            " AND A.RX_NO=F.ORDER_NO(+) "+
            " AND A.SEQ_NO=F.SEQ_NO(+) "+
            " AND A.MED_APPLY_NO=F.APPLICATION_NO(+) ";
	
	
	private String sqle =" SELECT A.EXEC_DEPT_CODE,C.DEGREE_CODE,B.ADM_TYPE,A.BILL_DATE,E.PAT_NAME,B.MR_NO,B.CASE_NO,A.ORDER_DESC,A.DR_CODE,F.EXEC_DR_CODE,F.REPORT_DR "+
            " FROM OPD_ORDER A,SYS_FEE C,REG_PATADM B,SYS_PATINFO E,MED_APPLY F "+
            " WHERE  A.CASE_NO=B.CASE_NO "+
            " AND B.REGCAN_USER IS NULL "+
            " AND B.MR_NO=E.MR_NO "+
            " AND B.ADM_TYPE='E' "+
            " AND A.ORDER_CODE=C.ORDER_CODE  "+
            " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE') "+
            " AND A.CAT1_TYPE=F.CAT1_TYPE(+) "+
            " AND A.RX_NO=F.ORDER_NO(+) "+
            " AND A.SEQ_NO=F.SEQ_NO(+) "+
            " AND A.MED_APPLY_NO=F.APPLICATION_NO(+) ";
	

	private String sqlh =" SELECT A.EXEC_DEPT_CODE,C.DEGREE_CODE,'H' ADM_TYPE,A.BILL_DATE,E.PAT_NAME,B.MR_NO,B.CASE_NO,A.ORDER_DESC,A.DR_CODE,F.EXEC_DR_CODE,F.REPORT_DR "+
            " FROM HRM_ORDER A,SYS_FEE C,HRM_PATADM B,SYS_PATINFO E,MED_APPLY F  "+
            " WHERE  A.CASE_NO=B.CASE_NO  "+ 
            " AND B.MR_NO=E.MR_NO   "+
            " AND A.ORDER_CODE=C.ORDER_CODE   "+
            " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE')  "+
            " AND A.CASE_NO=F.CASE_NO(+)  "+
            " AND A.CASE_NO=F.ORDER_NO(+)  "+
            " AND A.SEQ_NO=F.SEQ_NO(+)  ";


	private String sqli =" SELECT A.EXEC_DEPT_CODE,C.DEGREE_CODE,'I' ADM_TYPE,A.CASHIER_DATE AS BILL_DATE,E.PAT_NAME,B.MR_NO,B.CASE_NO,A.ORDER_DESC,A.ORDER_DR_CODE AS DR_CODE,F.EXEC_DR_CODE,F.REPORT_DR "+ 
            " FROM ODI_DSPNM A,SYS_FEE C,ADM_INP B,SYS_PATINFO E,MED_APPLY F  "+
            " WHERE  A.CASE_NO=B.CASE_NO "+
            " AND B.MR_NO=E.MR_NO "+
            " AND A.ORDER_CODE=C.ORDER_CODE  "+
            " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE') "+
            " AND A.CASE_NO=F.CASE_NO(+) "+
            " AND A.ORDER_NO=F.ORDER_NO(+) "+
            " AND A.ORDER_SEQ=F.SEQ_NO(+)    ";
	
	private TTable table;
	
	private Map DifMap;
	
	private Map MedMap;
	
	private Map DeptMap;
	
	private Map drMap;
	
	private Map DegreeMap;
	
	private String drql="SELECT USER_ID,USER_NAME FROM SYS_OPERATOR ";
	
	private String degreesql="SELECT D.ID,D.CHN_DESC FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE'";
	
	private String deptsql="SELECT COST_CENTER_CODE,COST_CENTER_CHN_DESC FROM SYS_COST_CENTER";
	
	private String dicsql="SELECT MED_YEAR||PERFORMANCE_CLASS AS KEY,MED_CLASS,DIFFICULTY_DEGREE AS VALUE FROM STA_MED_PERFORMANCE";
	
	
	
	
	private DateFormat df=new SimpleDateFormat("yyyyMMdd");
	

	public void onInit() {
		super.onInit();
		initDicMap();
		table = getTTable("TABLE");
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("S_DATE", now);
		this.setValue("E_DATE", now);
        
	}
    public void  onClear(){
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	this.clearValue("DEPT_CODE;DEGREE_CODE;ADM_TYPE;TOTSUM;");
		this.setValue("S_DATE", now);
		this.setValue("E_DATE", now);
		table.removeRowAll();
    }
	/**
     * 
     */
	public void onQuery() {
		if (getValue("S_DATE") == null || getValue("E_DATE") == null) {
			this.messageBox("时间不能空");
			return;
		}
		Timestamp start = (Timestamp) getValue("S_DATE");
		Timestamp end = (Timestamp) getValue("E_DATE");
		String s_year=df.format(start).substring(0, 4);
		String e_year=df.format(end).substring(0, 4);
		if(!s_year.equals(e_year)){
			this.messageBox("查询时间不能跨年");
			return;
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer whereOEH = new StringBuffer();
		StringBuffer whereI = new StringBuffer();
		whereOEH.append(" AND A.BILL_DATE BETWEEN TO_DATE('" + df.format(start)
				+ "','YYYYMMDD') AND TO_DATE('" + df.format(end)
				+ "','YYYYMMDD')+1");
		whereI.append(" AND  A.CASHIER_DATE BETWEEN TO_DATE('" + df.format(start)
				+ "','YYYYMMDD') AND TO_DATE('" + df.format(end)
				+ "','YYYYMMDD')+1 AND  A.BILL_FLG='Y' ");
		if (!getValueString("DEPT_CODE").equals("")) {
			whereOEH.append(" AND A.EXEC_DEPT_CODE='" + getValueString("DEPT_CODE") + "'");
			whereI.append(" AND A.EXEC_DEPT_CODE='" + getValueString("DEPT_CODE") + "'");
		}
		if (!getValueString("DEGREE_CODE").equals("")) {
			whereOEH.append(" AND C.DEGREE_CODE='" + getValueString("DEGREE_CODE") + "'");
			whereI.append(" AND C.DEGREE_CODE='" + getValueString("DEGREE_CODE") + "'");
		}
		if(getValueString("ADM_TYPE").equals("O")){
			sb.append(sqlo);
			sb.append(whereOEH);
		}else if(getValueString("ADM_TYPE").equals("E")){
			sb.append(sqle);
			sb.append(whereOEH);
		}else if(getValueString("ADM_TYPE").equals("H")){
			sb.append(sqlh);
			sb.append(whereOEH);
		}else if(getValueString("ADM_TYPE").equals("I")){
			sb.append(sqli);
			sb.append(whereI);
		}else{
			sb.append("SELECT * FROM (");
			sb.append(sqlo);
			sb.append(whereOEH);
			sb.append(" UNION ALL ");
			sb.append(sqle);
			sb.append(whereOEH);
			sb.append(" UNION  ALL ");
			sb.append(sqlh);
			sb.append(whereOEH);
			sb.append(" UNION ALL ");
			sb.append(sqli);
			sb.append(whereI);
			sb.append(" ) AAA ORDER BY EXEC_DEPT_CODE,BILL_DATE");
		}
//		System.out.println("========================"+sb.toString());
		TParm parmValue = new TParm(TJDODBTool.getInstance().select(sb.toString()));
		if (parmValue.getCount() <= 0) {
			this.messageBox("没有数据");
			table.removeRowAll();
			return;
		}
		table.setParmValue(onResetData(parmValue,s_year));
		this.setValue("TOTSUM", parmValue.getCount()+"");
		
	}
	/**
	 * 
	 * @param parm
	 * @return
	 */
    private TParm onResetData(TParm parm,String year){
    	for (int i=0;i<parm.getCount();i++) {
    		TParm row=parm.getRow(i);
    		parm.setData("EXEC_DEPT_CODE",i, DeptMap.get(row.getValue("EXEC_DEPT_CODE")));
    		parm.setData("DEGREE_CODE",i, DegreeMap.get(row.getValue("DEGREE_CODE")));
    		parm.setData("DIFFICULTY_DEGREE",i,DifMap.get(year+row.getValue("DEGREE_CODE")));
    		parm.setData("DR_CODE",i, drMap.get(row.getValue("DR_CODE")));
		}
    	return parm;
    }
    /**
	 * 
	 * @return
	 */
	private void initDicMap(){
		DifMap=new HashMap<String,String>();
		MedMap=new HashMap<String,String>();
		TParm result=new TParm(TJDODBTool.getInstance().select(dicsql));
		for(int i=0;i<result.getCount();i++){
			TParm parm=result.getRow(i);
			DifMap.put(parm.getValue("KEY"), parm.getValue("VALUE"));
			MedMap.put(parm.getValue("KEY"), parm.getValue("MED_CLASS"));
		}
		DeptMap=new HashMap<String,String>();
		TParm dept=new TParm(TJDODBTool.getInstance().select(deptsql));
		for(int i=0;i<dept.getCount();i++){
			TParm parm=dept.getRow(i);
			DeptMap.put(parm.getValue("COST_CENTER_CODE"), parm.getValue("COST_CENTER_CHN_DESC"));
		}
		drMap=new HashMap<String,String>();
		TParm drParm=new TParm(TJDODBTool.getInstance().select(drql));
		for(int i=0;i<drParm.getCount();i++){
			TParm parm=drParm.getRow(i);
			drMap.put(parm.getValue("USER_ID"), parm.getValue("USER_NAME"));
		}
		DegreeMap=new HashMap<String,String>();
		TParm degree=new TParm(TJDODBTool.getInstance().select(degreesql));
		for(int i=0;i<degree.getCount();i++){
			TParm parm=degree.getRow(i);
			DegreeMap.put(parm.getValue("ID"), parm.getValue("CHN_DESC"));
		}
		
	}
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "医技工作量统计汇总表");
	}
	
	

}
