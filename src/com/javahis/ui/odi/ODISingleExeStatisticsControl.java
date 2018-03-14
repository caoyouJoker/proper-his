package com.javahis.ui.odi;



import java.text.DecimalFormat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: 扫码率统计
 * Description:扫码率统计
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class ODISingleExeStatisticsControl extends TControl {
	private TTable table;
	DecimalFormat df1 = new DecimalFormat("##########0.00");
	DecimalFormat df = new DecimalFormat("##########0");
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return true;
    	}		
	    return false; 
	}
    /**
     * 查询
     */
    public void onQuery() {
    	//数据检核
    	if(checkdata())
		    return;
    	String sql ="";
    	String dept ="";//科室
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//科室选择
		if(!this.getValue("DEPT_CODE").equals("")){
			String deptcode = this.getValue("DEPT_CODE").toString();
			dept = " AND M.DEPT_CODE = '"+ deptcode+ "'";
		}
		//医嘱选择
		//检验项目
    	if(this.getValue("ORDER_TYPE").equals("1")){
    		sql=" SELECT TO_CHAR(M.ORDER_DATE,'YYYYMM') AS ORDER_DATE," +
    			 " DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
                 " SUM(CASE"+ 
    			 " WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    			 " THEN 1"+ 
    			 " ELSE 0"+
    			 " END ) AS EXEC_ACCOUNT_NUM,'检验项目' AS OREDER_TYPE,M.DEPT_CODE"+
    			 " FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT"+
    			 " WHERE M.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
    	         " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    			 " AND M.CAT1_TYPE='LIS'"+
    			 " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    			 " AND M.ORDER_CODE = M.ORDERSET_CODE"+
                 " AND M.OPTITEM_CODE  NOT IN ('I3','I4')"+
    			 " AND D.CASE_NO=M.CASE_NO"+
    			 " AND D.ORDER_NO=M.ORDER_NO"+
    			 " AND D.ORDER_SEQ=M.ORDER_SEQ"+
    			 " AND D.DC_DATE IS NULL" +
    			 " AND M.DC_NS_CHECK_DATE IS NULL"+
    			 dept+
    			 " AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    			 " GROUP BY TO_CHAR(M.ORDER_DATE,'YYYYMM'),DEPT.DEPT_CHN_DESC,'检验项目',M.DEPT_CODE"+
    			 " ORDER BY ORDER_DATE,M.DEPT_CODE,OREDER_TYPE"; 
    	}
    	//针剂大量点滴
    	else if(this.getValue("ORDER_TYPE").equals("2")){
    		sql=" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    			 " DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    			 " SUM(CASE" + 
    			 " WHEN D.NS_EXEC_DATE_REAL IS NOT NULL" +
    			 " THEN 1" + 
    			 " ELSE 0" +
    			 " END ) AS EXEC_ACCOUNT_NUM,'针剂大量点滴' AS OREDER_TYPE,M.DEPT_CODE" +
    			 " FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S" +
    			 " WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    			 " AND M.CAT1_TYPE='PHA'" +
    			 " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    			 " AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
                 " AND S.CLASSIFY_TYPE IN ('F','I')" +
                 " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
                 " AND (M.ROUTE_CODE !='IN.I.P' AND M.FREQ_CODE !='.')" +
    			 " AND D.CASE_NO=M.CASE_NO" +
    			 " AND D.ORDER_NO=M.ORDER_NO" +
    			 " AND D.ORDER_SEQ=M.ORDER_SEQ" +
    			 " AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
    			 " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
    			 " AND M.PHA_CHECK_DATE IS NOT NULL"+
    			 " AND M.DC_NS_CHECK_DATE IS NULL"+
    			 dept+
    			 " AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') " +
    			 " OR D.DC_DATE IS NULL)" +
    			 " AND DEPT.DEPT_CODE=M.DEPT_CODE" +
    			 " GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'针剂大量点滴',M.DEPT_CODE" +
    			 " ORDER BY ORDER_DATE,M.DEPT_CODE,OREDER_TYPE";
    	}
    	//口服外用
    	else if(this.getValue("ORDER_TYPE").equals("3")){
    		sql=" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    			 " DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    			 " SUM(CASE" +
    			 " WHEN D.NS_EXEC_DATE_REAL IS NOT NULL" +
    			 " THEN 1" + 
    			 " ELSE 0" +
    			 " END ) AS EXEC_ACCOUNT_NUM,'口服外用' AS OREDER_TYPE,M.DEPT_CODE" +
    			 " FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S" +
    			 " WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    			 " AND M.CAT1_TYPE='PHA'" +
    			 " AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    			 " AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
                 " AND S.CLASSIFY_TYPE NOT IN ('F','I')" +
                 " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
    			 " AND D.CASE_NO=M.CASE_NO" +
    			 " AND D.ORDER_NO=M.ORDER_NO" +
    			 " AND D.ORDER_SEQ=M.ORDER_SEQ" +
    			 " AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
    			 " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
    			 " AND M.PHA_CHECK_DATE IS NOT NULL"+
    			 " AND M.DC_NS_CHECK_DATE IS NULL"+
    			 dept+
    			 " AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') " +
    			 " OR D.DC_DATE IS NULL)" +
    			 " AND DEPT.DEPT_CODE=M.DEPT_CODE" +
    			 " GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'口服外用',M.DEPT_CODE" + 
    			 " ORDER BY ORDER_DATE,M.DEPT_CODE,OREDER_TYPE"; 		
    	}
    	else {
    		sql=" SELECT A.ORDER_DATE,A.DEPT_CHN_DESC ,SUM(A.ACCOUNT_NUM) AS ACCOUNT_NUM," +
    			" SUM(A.EXEC_ACCOUNT_NUM) AS EXEC_ACCOUNT_NUM,A.OREDER_TYPE,A.DEPT_CODE"+
    		" FROM ("+
    		" SELECT TO_CHAR(M.ORDER_DATE,'YYYYMM') AS ORDER_DATE," +
    		" DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    		" SUM(CASE"+ 
    		" WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    		" THEN 1"+ 
    		" ELSE 0"+
    		" END ) AS EXEC_ACCOUNT_NUM,'检验项目' AS OREDER_TYPE,M.DEPT_CODE"+
    		" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT"+
    		" WHERE M.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
	        " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    		" AND M.CAT1_TYPE='LIS'"+
    		" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    		" AND M.ORDER_CODE = M.ORDERSET_CODE"+
            " AND M.OPTITEM_CODE  NOT IN ('I3','I4')"+
    		" AND D.CASE_NO=M.CASE_NO"+
    		" AND D.ORDER_NO=M.ORDER_NO"+
    		" AND D.ORDER_SEQ=M.ORDER_SEQ"+
    		" AND D.DC_DATE IS NULL"+
    		" AND M.DC_NS_CHECK_DATE IS NULL"+
    		 dept+
    		" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    		" GROUP BY TO_CHAR(M.ORDER_DATE,'YYYYMM'),DEPT.DEPT_CHN_DESC,'检验项目',M.DEPT_CODE"+
    		" UNION"+
    		" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    		" DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    		" SUM(CASE"+ 
    		" WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    		" THEN 1"+ 
    		" ELSE 0"+
    		" END ) AS EXEC_ACCOUNT_NUM,'针剂大量点滴' AS OREDER_TYPE,M.DEPT_CODE"+
    		" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S"+
    		" WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    		" AND M.CAT1_TYPE='PHA'"+
    		" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    		" AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
            " AND S.CLASSIFY_TYPE IN ('F','I')" +
            " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
            " AND (M.ROUTE_CODE !='IN.I.P' AND M.FREQ_CODE !='.')" +
    		" AND D.CASE_NO=M.CASE_NO"+
    		" AND D.ORDER_NO=M.ORDER_NO"+
    		" AND D.ORDER_SEQ=M.ORDER_SEQ"+
    		" AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
			" AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
			" AND M.PHA_CHECK_DATE IS NOT NULL"+
			" AND M.DC_NS_CHECK_DATE IS NULL"+
    		 dept+
    		" AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') OR D.DC_DATE IS NULL)"+
    		" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    		" GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'针剂大量点滴',M.DEPT_CODE"+
    		" UNION"+
    		" SELECT SUBSTR(D.ORDER_DATE,0,6) AS ORDER_DATE," +
    		" DEPT.DEPT_CHN_DESC,COUNT(M.ORDER_CODE) AS ACCOUNT_NUM,"+
    		" SUM(CASE"+ 
    		" WHEN D.NS_EXEC_DATE_REAL IS NOT NULL"+
    		" THEN 1"+ 
    		" ELSE 0"+
    		" END ) AS EXEC_ACCOUNT_NUM,'口服外用' AS OREDER_TYPE,M.DEPT_CODE"+
    		" FROM ODI_DSPNM M ,ODI_DSPND D,SYS_DEPT DEPT,SYS_PHAROUTE S"+
    		" WHERE D.ORDER_DATE||D.ORDER_DATETIME BETWEEN '"+startDate+"' AND '"+endDate+"'"+
    		" AND M.CAT1_TYPE='PHA'"+
    		" AND M.DEPT_CODE NOT IN ('030901','030902','0411')"+
    		" AND M.ROUTE_CODE  = S.ROUTE_CODE(+)" +
            " AND S.CLASSIFY_TYPE NOT IN ('F','I')" +
            " AND M.DSPN_KIND NOT IN ('DS','RT','F')" +
    		" AND D.CASE_NO=M.CASE_NO"+
    		" AND D.ORDER_NO=M.ORDER_NO"+
    		" AND D.ORDER_SEQ=M.ORDER_SEQ"+
    		" AND M.DISPENSE_EFF_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+  
			" AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
			" AND M.PHA_CHECK_DATE IS NOT NULL"+
			" AND M.DC_NS_CHECK_DATE IS NULL"+
    		 dept+
    		" AND ((D.ORDER_DATE||D.ORDER_DATETIME)<=TO_CHAR(D.DC_DATE,'YYYYMMDDHH24MISS') OR D.DC_DATE IS NULL)"+
    		" AND DEPT.DEPT_CODE=M.DEPT_CODE"+
    		" GROUP BY SUBSTR(D.ORDER_DATE,0,6),DEPT.DEPT_CHN_DESC,'口服外用',M.DEPT_CODE) A"+
    		" GROUP BY A.ORDER_DATE,A.DEPT_CODE,A.DEPT_CHN_DESC ,A.OREDER_TYPE"+
    		" ORDER BY A.ORDER_DATE,A.DEPT_CODE,A.OREDER_TYPE";
    	}
//		 System.out.println("sql========="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE")).removeRowAll();
			return;
		}
		double accountsum =0;//应执行数合计
		double execaccountsum =0;//实际执行数合计		
		int count  = result.getCount();
		for (int i = 0; i < count; i++) {
			result.setData("SURPLUS_COUNT", i, df1.format((result.getDouble("EXEC_ACCOUNT_NUM",i)/
	    			result.getDouble("ACCOUNT_NUM",i))*100)+"%");			
			accountsum+=StringTool.round(result.getDouble("ACCOUNT_NUM",i),0);
			execaccountsum+=StringTool.round(result.getDouble("EXEC_ACCOUNT_NUM",i),0);	
		}
		result.addData("ORDER_DATE", "合计");
		result.addData("ACCOUNT_NUM",df.format(accountsum));
		result.addData("EXEC_ACCOUNT_NUM",df.format(execaccountsum));
		result.addData("SURPLUS_COUNT",df1.format((execaccountsum/accountsum)*100)+"%");
    	((TTable) getComponent("TABLE")).setParmValue(result);
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	 String title ="";
    	 if(this.getValue("ORDER_TYPE").equals(""))
    	   title ="科室护士单次执行全部统计表";
    	 else if (this.getValue("ORDER_TYPE").equals("1"))
    	   title ="科室护士单次执行检验项目统计表";
    	 else if (this.getValue("ORDER_TYPE").equals("2"))
    	   title ="科室护士单次执行针剂大量点滴统计表";
    	 else if (this.getValue("ORDER_TYPE").equals("3"))
    	   title ="科室护士单次执行口服外用统计表";   				 
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }
    	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	this.setValue("DEPT_CODE","");
    	this.setValue("ORDER_TYPE","");
 	   this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
    /**
     * 获得明细
     */
    public void onSelect() {
    	int Row = table.getSelectedRow();//行数
    	TParm data = table.getParmValue().getRow(Row);//获得数据    	
//    	 System.out.println("data===========" + data);
    	if(data.getValue("ORDER_DATE").equals("合计"))
    		return;
        String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+"000000"; //开始日期
   	     String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+"235959"; //结束日期
   	    TParm parm = new TParm();
   	    parm.setData("START_DATE", startdate);
   	    parm.setData("END_DATE", enddate);
   	    parm.setData("ORDER_TYPE", data.getValue("OREDER_TYPE"));//医嘱类型
   	    parm.setData("DEPT_CODE", data.getValue("DEPT_CODE"));//科室代码
   	    parm.setData("DEPT_DESC", data.getValue("DEPT_CHN_DESC"));//科室名称
    	this.openDialog("%ROOT%\\config\\odi\\ODISingleDetail.x",parm);

    }
}
