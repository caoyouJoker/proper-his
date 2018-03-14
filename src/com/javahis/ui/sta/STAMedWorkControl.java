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
public class STAMedWorkControl extends TControl {

	private String sqloe_s = " SELECT COUNT(A.CASE_NO) AS NUM,A.EXEC_DEPT_CODE,C.DEGREE_CODE,B.ADM_TYPE "
			+ " FROM OPD_ORDER A,SYS_FEE C,REG_PATADM B "
			+ " WHERE  A.CASE_NO=B.CASE_NO "
			+ " AND B.REGCAN_USER IS NULL "
			+ " AND A.ORDER_CODE=C.ORDER_CODE ";
	private String sqloe_e = " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE') "
			+ " GROUP BY A.EXEC_DEPT_CODE,C.DEGREE_CODE,B.ADM_TYPE ";

	private String sqlh_s = " SELECT COUNT(A.CASE_NO)  AS NUM,A.EXEC_DEPT_CODE,C.DEGREE_CODE,'H' AS ADM_TYPE "
			+ " FROM HRM_ORDER A,SYS_FEE C "
			+ " WHERE A.ORDER_CODE=C.ORDER_CODE "
			+ " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE')";
	private String sqlh_e = " GROUP BY A.EXEC_DEPT_CODE,C.DEGREE_CODE ";

	private String sqli_s = " SELECT COUNT(A.CASE_NO)  AS NUM,A.EXEC_DEPT_CODE,C.DEGREE_CODE,'I' AS ADM_TYPE "
			+ " FROM ODI_DSPNM A,SYS_FEE C "
			+ " WHERE  A.ORDER_CODE=C.ORDER_CODE "
			+ " AND C.DEGREE_CODE IN (SELECT D.ID FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='DEGREE_CODE') ";
	private String sqli_e = " GROUP BY A.EXEC_DEPT_CODE,C.DEGREE_CODE ";
	
	private TTable table;
	
	private Map DifMap;
	
	private Map MedMap;
	
	private Map DeptMap;
	
	private Map MedClassMap;
	
	private Map DegreeMap;
	
	private String medclasssql="SELECT D.ID,D.CHN_DESC FROM SYS_DICTIONARY D WHERE  D.GROUP_ID='MED_CLASS'";
	
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
    	this.clearValue("DEPT_CODE;MED_CLASS;");
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
				+ "','YYYYMMDD')+1  AND A.BILL_FLG = 'Y'  ");
		if (!getValueString("DEPT_CODE").equals("")) {
			whereOEH.append(" AND A.EXEC_DEPT_CODE='" + getValueString("DEPT_CODE") + "'");
			whereI.append(" AND A.EXEC_DEPT_CODE='" + getValueString("DEPT_CODE") + "'");
		}
		sb.append(sqloe_s);
		sb.append(whereOEH);
		sb.append(sqloe_e);
		sb.append(" UNION  ");
		sb.append(sqlh_s);
		sb.append(whereOEH);
		sb.append(sqlh_e);
		sb.append(" UNION  ");
		sb.append(sqli_s);
		sb.append(whereI);
		sb.append(sqli_e);
//		System.out.println("========"+sb.toString());
		TParm parmValue = new TParm(TJDODBTool.getInstance().select(sb.toString()));
		if (parmValue.getCount() <= 0) {
			this.messageBox("没有数据");
			table.removeRowAll();
			return;
		}
		table.setParmValue(onResetData(parmValue,s_year,getValueString("MED_CLASS")));
	}
	/**
	 * 
	 * @param parm
	 * @return
	 */
    private TParm onResetData(TParm parm,String year,String medClassFilter){
    	TParm result=new TParm();
    	Map<String,Map> deptMap=new TreeMap<String,Map>();
    	for(int i=0;i<parm.getCount();i++){
    		TParm row=parm.getRow(i);
    		String key=row.getValue("EXEC_DEPT_CODE")+row.getValue("DEGREE_CODE");
    		String admtype=row.getValue("ADM_TYPE");
    		String num=row.getValue("NUM");
    		if(deptMap.get(key)==null){
    			Map rowMap=new HashMap<String,Object>();
    			rowMap.put("EXEC_DEPT_CODE", row.getValue("EXEC_DEPT_CODE"));
    			rowMap.put("DEGREE_CODE", row.getValue("DEGREE_CODE"));
    			rowMap.put("O", "0");
    			rowMap.put("E", "0");
    			rowMap.put("H", "0");
    			rowMap.put("I", "0");
    			rowMap.put(admtype, num);
    			deptMap.put(key, rowMap);
    		}else{
    			Map rowMap=deptMap.get(key);
    			rowMap.put(admtype, num);
    			deptMap.put(key, rowMap);
    		}
    	}
    	int colSumO=0;
    	int colSumE=0;
    	int colSumH=0;
    	int colSumI=0;
    	for (String key : deptMap.keySet()) {
			Map<String,String> map=deptMap.get(key);
			if(!medClassFilter.equals("")&&!medClassFilter.equals(MedMap.get(year+map.get("DEGREE_CODE"))))
				continue;
			result.addData("EXEC_DEPT_CODE", DeptMap.get(map.get("EXEC_DEPT_CODE")));
			result.addData("MED_CLASS", MedClassMap.get(MedMap.get(year+map.get("DEGREE_CODE"))));
			result.addData("DEGREE_CODE", DegreeMap.get(map.get("DEGREE_CODE")));
			result.addData("DIFFICULTY_DEGREE", DifMap.get(year+map.get("DEGREE_CODE")));
			result.addData("O", map.get("O"));
			result.addData("E", map.get("E"));
			result.addData("H", map.get("H"));
			result.addData("I", map.get("I"));
			result.addData("SUM", Integer.parseInt(map.get("O"))+ Integer.parseInt(map.get("E"))
					+ Integer.parseInt(map.get("H"))+ Integer.parseInt(map.get("I")));
			colSumO+=Integer.parseInt(map.get("O"));
			colSumE+=Integer.parseInt(map.get("E"));
			colSumH+=Integer.parseInt(map.get("H"));
			colSumI+=Integer.parseInt(map.get("I"));
		}
    	result.addData("EXEC_DEPT_CODE","");
    	result.addData("MED_CLASS", "");
		result.addData("DEGREE_CODE", "");
		result.addData("DIFFICULTY_DEGREE", "");
    	result.addData("O", colSumO);
		result.addData("E", colSumE);
		result.addData("H", colSumH);
		result.addData("I", colSumI);
		result.addData("SUM", colSumO+colSumE+colSumH+colSumI);
		result.setCount(result.getCount("EXEC_DEPT_CODE"));
    	return result;
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
		MedClassMap=new HashMap<String,String>();
		TParm medClass=new TParm(TJDODBTool.getInstance().select(medclasssql));
		for(int i=0;i<medClass.getCount();i++){
			TParm parm=medClass.getRow(i);
			MedClassMap.put(parm.getValue("ID"), parm.getValue("CHN_DESC"));
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
