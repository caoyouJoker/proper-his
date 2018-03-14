package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;



/**
 * <p>Title: 每月统计</p>
 *
 * <p>Description: 每月统计</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yyn 20170912
 * @version 1.0
 */

public class BILOrderMonthControl extends TControl{
	private TTable table;
	
	/**
	 * 初始化方法
	 */
	public void onInit(){
		super.onInit();
		table = (TTable)this.getComponent("TABLE");
		initPage();//初始化页面信息
	}
	
	/**
     * 初始化页面信息
     */
    public void initPage() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate = today.toString();
        endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);
    }
    
  
    /**
     * 查询
     */
    public void onQuery(){
    	String admtype;//门急别 
    	String ordercode;//项目类别  	
    	String startDate;
    	String endDate;
    	
    	if (getValue("START_DATE") == null ||getValueString("START_DATE").equals("")){
    		messageBox("请选择开始时间!");
                return;
    	}
    	
        if (getValue("END_DATE") == null ||getValueString("END_DATE").equals("")){
        	messageBox("请选择结束时间!");
            return;
        }
        
        startDate = getValueString("START_DATE").substring(0, 19);
		endDate = getValueString("END_DATE").substring(0, 19);
		startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
		startDate.substring(8, 10) + startDate.substring(11, 13) +
		startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
		endDate.substring(8, 10) + endDate.substring(11, 13) +
		endDate.substring(14, 16) + endDate.substring(17, 19);        
        admtype = getValueString("ADM_TYPE");
        ordercode = getValueString("ORDER_CODE");
        
        //System.out.println(admtype);
        //System.out.println(ordercode);
        
        if(admtype.equals("I")){//住院
        	String sql = "SELECT C.MR_NO,A.CASE_NO,D.PAT_NAME,A.BILL_DATE,F.DEPT_ABS_DESC AS DEPT_DESC,"
        		+ " G.STATION_DESC,E.COST_CENTER_ABS_DESC AS COST_DESC,H.STATION_DESC AS STATION_EXE_DESC,"
        		+ " A.ORDER_CODE,A.ORDER_CHN_DESC AS ORDER_DESC,A.OWN_PRICE,A.DOSAGE_QTY AS QTY,A.TOT_AMT AS AMT "
        		+ " FROM JAVAHIS.IBS_ORDD A,JAVAHIS.ADM_INP C,JAVAHIS.SYS_PATINFO D,JAVAHIS.SYS_COST_CENTER E,"
        		+ " JAVAHIS.SYS_DEPT F,JAVAHIS.SYS_STATION G,JAVAHIS.SYS_STATION H WHERE "
        		+ " A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') "
        		+ " AND TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS') ";
        	if(ordercode.equals("0")){//核素
        		sql +=
        		"AND A.ORDER_CODE in('Y0101004','Y0101002')";
        	}
        	else if(ordercode.equals("1")){//肺功能
        		sql +=
            	"AND A.ORDER_CODE in('Y1201001','Y1201002','Y1401001')";
        	}
        	else if(ordercode.equals("2")){//普华床位费
        		sql +=
                "AND A.ORDER_CODE in('C0000046','C0000047','C0000027','C0000043','C0000025','C0000024')";
        	}
        	else if(ordercode.equals("3")){//全部开单执行
        		sql +=
                    "AND (A.EXE_DEPT_CODE in ('030901','030902','035022','035023') OR A.EXE_STATION_CODE in ('V01','V02'))";
        	}
        	else{
        		sql +=
                    "AND (A.ORDER_CODE='')";
        	}
        	sql +=
        		" AND A.ORDER_CAT1_CODE<>'PLN' AND C.CASE_NO=A.CASE_NO AND D.MR_NO=C.MR_NO "
        		+ " AND E.COST_CENTER_CODE=A.EXE_DEPT_CODE AND F.DEPT_CODE=A.DEPT_CODE AND "
        		+ " A.STATION_CODE=G.STATION_CODE AND A.EXE_STATION_CODE=H.STATION_CODE ORDER BY A.BILL_DATE";
        	
        	//System.out.println(sql);
        	
    		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		if(parm.getCount() < 0){
    			this.messageBox("没有要查询的数据");
    			table.removeRowAll();
    			return;
    		}
    		table.setParmValue(parm);

        }
        
        if(admtype.equals("O")){//门急诊
        	String sql = "SELECT A.MR_NO,A.CASE_NO,B.PAT_NAME,A.BILL_DATE,C.DEPT_CHN_DESC AS DEPT_DESC,D.COST_CENTER_CHN_DESC AS COST_DESC,"
        		+ " A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,A.DISPENSE_QTY AS QTY,A.AR_AMT AS AMT"
        		+ " FROM JAVAHIS.OPD_ORDER A,JAVAHIS.SYS_PATINFO B,JAVAHIS.SYS_DEPT C,JAVAHIS.SYS_COST_CENTER D WHERE ";
        	if(ordercode.equals("0")){//核素
        		sql +=
        			"A.ORDER_CODE in('Y0101004','Y0101002')";
        	}
        	else if(ordercode.equals("1")){//肺功能
        		sql +=
            	"A.ORDER_CODE in('Y1201001','Y1201002','Y1401001')";
        	}
        	else{
        		sql +=
        		"(A.ORDER_CODE='')";
        	}
        	sql +=
        		" AND A.MR_NO=B.MR_NO AND A.DEPT_CODE=C.DEPT_CODE AND A.EXEC_DEPT_CODE=D.COST_CENTER_CODE"
        	+ " AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"
        	+ " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ORDER BY A.BILL_DATE";
        	
        	//System.out.println(sql);
        	
        	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		if(parm.getCount() < 0){
    			this.messageBox("没有要查询的数据");
    			table.removeRowAll();
    			return;
    		}    		
    		for (int i = 0; i < parm.getCount(); i++) {
    			parm.addData("STATION_DESC", "-");
    			parm.addData("STATION_EXE_DESC", "-");
    		}        	
    		table.setParmValue(parm);
        }
        
        if(admtype.equals("H")){//健检
        	String sql = "SELECT A.MR_NO,A.CASE_NO,B.PAT_NAME,A.BILL_DATE,C.DEPT_CHN_DESC AS DEPT_DESC ,"
        		+ "		D.COST_CENTER_CHN_DESC AS COST_DESC ,A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,"
        		+ "	A.DISPENSE_QTY AS QTY ,A.AR_AMT AS AMT"
        		+ "	FROM JAVAHIS.HRM_ORDER A,JAVAHIS.SYS_PATINFO B,JAVAHIS.SYS_DEPT C,JAVAHIS.SYS_COST_CENTER D WHERE ";
        	if(ordercode.equals("0")){//核素
        		sql +=
        			"A.ORDER_CODE in('Y0101004','Y0101002')";
        	}
        	else if(ordercode.equals("1")){//肺功能
        		sql +=
            	"A.ORDER_CODE in('Y1201001','Y1201002','Y1401001')";
        	}
        	else{
        		sql +=
        		"(A.ORDER_CODE='')";
        	}
        	sql +=
        		"AND A.MR_NO=B.MR_NO AND A.DEPT_CODE=C.DEPT_CODE AND A.EXEC_DEPT_CODE=D.COST_CENTER_CODE"
        	+ " AND RECEIPT_NO IS NOT NULL "
        	+ " AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"
        	+ " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ORDER BY A.BILL_DATE";
        	
        	//System.out.println(sql);
        	
        	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    		if(parm.getCount() < 0){
    			this.messageBox("没有要查询的数据");
    			table.removeRowAll();
    			return;
    		}    		
    		for (int i = 0; i < parm.getCount(); i++) {
    			parm.addData("STATION_DESC", "-");
    			parm.addData("STATION_EXE_DESC", "-");
    		}    		
    		table.setParmValue(parm);   		
        }               
     }
    
    /**
     * 清空
     */
    public void onClear() {
    	table.removeRowAll();
    	initPage();
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport() {
		String title = "质控中心财务每月统计";
		if (table.getRowCount() > 0)   		
		ExportExcelUtil.getInstance().exportExcel(table,title);
	}

}
