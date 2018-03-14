package com.javahis.ui.ins;



import java.sql.Timestamp;
import java.text.DecimalFormat;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SystemTool;

/**
 * Title: 医保总额查询
 * Description:医保总额查询
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class INSGatherQueryControl extends TControl {
	private TTable table;
	private TComboBox tcombobox;
	DecimalFormat df1 = new DecimalFormat("##########0");
	DecimalFormat df2 = new DecimalFormat("##########0.00");
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        tcombobox = (TComboBox) this.getComponent("YB_TYPE");
        onClear();
    }
    /**
     * 初始化数据
     */
    public void getData() { 	
    	String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
    	String year = sysdate.substring(0,4);
    	this.setValue("YEAR",year);
    	//指标类别
    	this.setValue("YEAR_MONTH_TYPE","1");
    	callFunction("UI|YEAR_MONTH_TYPE|setEnabled", false); //指标类别不可编辑 
    	//开始日期
    	String startdate =year+"-04-01 00:00:00";
    	Timestamp date1 = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("START_DATE",date1);
    	//结束日期
    	DecimalFormat df = new DecimalFormat("##########0");
    	String nextyear =  df.format(Double.parseDouble(year)+1);
    	String enddate =nextyear+"-03-31 00:00:00";
    	Timestamp date2 = StringTool.getTimestamp(enddate, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("END_DATE",date2);
    	//初始化
    	this.callFunction("UI|TABLE|setParmValue", new TParm());
        this.setValue("QUERY_TYPE","1");
    	if(this.getValue("QUERY_TYPE").equals("1")){
    	callFunction("UI|YB_TYPE|setEnabled", false); //医保身份不可编辑            
        callFunction("UI|HZ_TYPE|setEnabled", false); //患者类别不可编辑 
    	callFunction("UI|DEPT_MONTH_TYPE|setEnabled", false); //汇总类别不可编辑 
    	this.setValue("HZ_TYPE","1");//普通医保
    	clearValue("YB_TYPE;DEPT_MONTH_TYPE");
    	this.table.setHeader("类别/科室,140;年指标人次,100;完成人次,100;人次完成比,100;" +
                 "年指标金额(万元),120;完成金额(元),100,double,#########0.00;金额完成比,100;" +
                 "次均统筹指标(元),120;次均统筹(元),100;个人负担率指标(%),120;个人负担率(%),100");
        this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                           "4,right;5,right;6,right;7,right;8,right;9,right;10,right");
        this.table.setParmMap("QUERY_TYPE;YEAR_PAT_COUNT;APPLY_COUNT;SURPLUS_COUNT;" +
                  "YEAR_QUOTA_AMT;APPLY_AMT;SURPLUS_AMT;" +
                  "AVERAGE_PAY_AMT;AVERAGE_APPLY_AMT;OWN_PAY_PERCENT;OWN_APPLY_PERCENT");
    	}
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
		if(this.getValue("YEAR").equals("")){
    		this.messageBox("年度不能为空");
    		return true;
    	}
		if(this.getValue("YEAR_MONTH_TYPE").equals("")){
    		this.messageBox("指标类别不能为空");
    		return true;
    	}
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return true;
    	}
    	if(this.getValue("QUERY_TYPE").equals("")){
    		this.messageBox("查询类别不能为空");
    		return true;
    	}
    	if(this.getValue("QUERY_TYPE").equals("2")||
    	   this.getValue("QUERY_TYPE").equals("3")){
    	    if(this.getValue("DEPT_MONTH_TYPE").equals("")){
    	    	this.messageBox("汇总类别不能为空");
    	    	return true;
    	    	}
    	    if(this.getValue("YB_TYPE").equals("")){
    	    	this.messageBox("医保身份不能为空");
    	    	return true;
    	    	} 
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
    	String sql_condition1 ="";//全院
    	if(this.getValue("QUERY_TYPE").equals("2")){//门诊   		
    		if(this.getValue("YB_TYPE").equals("1"))
       		 sql_condition1 =" AND B.INS_TYPE ='01'";//城职门诊
       		 else if(this.getValue("YB_TYPE").equals("2"))       			 
       		 sql_condition1 =" AND B.INS_TYPE ='02'";//城职门特
       		 else
       		 sql_condition1 =" AND B.INS_TYPE ='03'";//城乡门特	 
    	}
    	else if(this.getValue("QUERY_TYPE").equals("3")){//住院
    		 if(this.getValue("YB_TYPE").equals("1"))
    		 sql_condition1 =" AND B.INS_TYPE ='04'";//城职住院
    		 else
    		 sql_condition1 =" AND B.INS_TYPE ='05'";//城乡住院	 
    	}   		
    	 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "START_DATE")), "yyyyMMdd")+"000000"; //开始日期
    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "END_DATE")), "yyyyMMdd")+"235959"; //结束日期
    	 TParm parm = new TParm();
    	 parm.setData("START_DATE", startdate);
    	 parm.setData("END_DATE", enddate);
    	//医保总额指标查询
         String sql =" SELECT CASE WHEN B.INS_TYPE='01' THEN '城职门诊'  " +
    			     " WHEN B.INS_TYPE='02' THEN '城职门特' " +   			    
    			     " WHEN B.INS_TYPE='03' THEN '城乡门特' " +
    			     " WHEN B.INS_TYPE='04' THEN '城职住院' " +
    			     " WHEN B.INS_TYPE='05' THEN '城乡住院' END AS QUERY_TYPE,"+
                     " B.YEAR_PAT_COUNT,B.YEAR_QUOTA_AMT,"+
                     " B.MON_PAT_COUNT,B.MON_QUOTA_AMT,B.AVERAGE_PAY_AMT,B.OWN_PAY_PERCENT,B.INS_TYPE"+
                     " FROM INS_TOTAL_QUOTA B"+
                     " WHERE  B.YEAR ='"+getValue("YEAR")+"'" +
                     sql_condition1+
                     " ORDER BY B.INS_TYPE";
    	 TParm data = new TParm(TJDODBTool.getInstance().select(sql));
//    	 System.out.println("data=========="+data);  
		 if (data.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		//全院查询
		if(this.getValue("QUERY_TYPE").equals("1")){
		//门诊(城职门诊、城职门特、城乡门特)
		String sql1 = " SELECT COUNT(DISTINCT A.CASE_NO) AS APPLY_COUNT,"+
		" SUM(A.OTOT_AMT+A.TOTAL_AGENT_AMT) AS APPLY_AMT,A.INS_CROWD_TYPE,A.INS_PAT_TYPE"+
		" FROM INS_OPD A,REG_PATADM B"+
		" WHERE A.INS_DATE  BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+ 
		" AND  TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
		" AND A.CASE_NO = B.CASE_NO" +
		" GROUP BY A.INS_CROWD_TYPE,A.INS_PAT_TYPE"+
        " ORDER BY A.INS_CROWD_TYPE";
		TParm data1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		System.out.println("data1=========="+data1);  
		if (data1.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		//住院(城职、城乡)
		String sql2 =" SELECT SUBSTR (B.HIS_CTZ_CODE, 0, 1) AS INS_CROWD_TYPE,"+
		" COUNT(DISTINCT A.CASE_NO) AS APPLY_COUNT ,SUM(A.NHI_PAY) AS APPLY_AMT," +
		" SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+" +
		" A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT," +
		" SUM(A.RESTART_STANDARD_AMT+A.STARTPAY_OWN_AMT+A.OWN_AMT+" +
		" A.PERCOPAYMENT_RATE_AMT+A.ADD_AMT+A.INS_HIGHLIMIT_AMT) AS OWN_APPLY"+ 
		" FROM INS_IBS A,INS_ADM_CONFIRM B"+
		" WHERE A.UPLOAD_DATE  BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+ 
		" AND  TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
		" AND A.CASE_NO = B.CASE_NO"+
		" AND A.ADM_SEQ = B.ADM_SEQ"+
		" AND B.IN_STATUS IN( '2','3','4')"+
		" AND B.SDISEASE_CODE IS NULL"+
		" GROUP BY SUBSTR (B.HIS_CTZ_CODE, 0, 1)";
		TParm data2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		System.out.println("data2=========="+data2);  
		if (data2.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		 TParm result = new TParm();
		int count  = data.getCount();
		for (int i = 0; i < count; i++) {
			//门诊
			int countreg  = data1.getCount();
			for (int m = 0; m < countreg; m++) {
		  if(data.getData("INS_TYPE", i).equals("01")&&
			data1.getData("INS_CROWD_TYPE",m).equals("1")&&
			data1.getData("INS_PAT_TYPE",m).equals("1")){
			data.setData("APPLY_COUNT", i,data1.getData("APPLY_COUNT",m));
		    data.setData("APPLY_AMT", i,data1.getData("APPLY_AMT",m));
		    data.setData("AVERAGE_APPLY_AMT", i,0);
		    data.setData("OWN_APPLY_PERCENT", i,0);
		  }
		  if(data.getData("INS_TYPE", i).equals("02")&&
			data1.getData("INS_CROWD_TYPE",m).equals("1")&&
			data1.getData("INS_PAT_TYPE",m).equals("2")){
		    data.setData("APPLY_COUNT", i,data1.getData("APPLY_COUNT",m));
		    data.setData("APPLY_AMT", i,data1.getData("APPLY_AMT",m));
		    data.setData("AVERAGE_APPLY_AMT", i,0);
		    data.setData("OWN_APPLY_PERCENT", i,0);
		  }
		  if(data.getData("INS_TYPE", i).equals("03")&&
			data1.getData("INS_CROWD_TYPE",m).equals("2")&&
			data1.getData("INS_PAT_TYPE",m).equals("2")){
			data.setData("APPLY_COUNT", i,data1.getData("APPLY_COUNT",m));
		    data.setData("APPLY_AMT", i,data1.getData("APPLY_AMT",m));
		    data.setData("AVERAGE_APPLY_AMT", i,0);
		    data.setData("OWN_APPLY_PERCENT", i,0);
		  }		
		}
			//住院
			int countadm  = data2.getCount();
			for (int j = 0; j < countadm; j++) {
			    if(data.getData("INS_TYPE", i).equals("04")&&
			       data2.getData("INS_CROWD_TYPE", j).equals("1")){
			    	data.setData("APPLY_COUNT", i, data2.getData("APPLY_COUNT",j));
				    data.setData("APPLY_AMT", i, data2.getData("APPLY_AMT",j));
				    data.setData("AVERAGE_APPLY_AMT", i,
				    df2.format((data2.getDouble("APPLY_AMT",j)/data2.getDouble("APPLY_COUNT",j))));
				    data.setData("OWN_APPLY_PERCENT", i,
				    df1.format((data2.getDouble("OWN_APPLY",j)/data2.getDouble("TOT_AMT",j))*100));
			    }else if(data.getData("INS_TYPE", i).equals("05")&&
					     data2.getData("INS_CROWD_TYPE", j).equals("2")){
			    	data.setData("APPLY_COUNT", i, data2.getData("APPLY_COUNT",j));
				    data.setData("APPLY_AMT", i, data2.getData("APPLY_AMT",j));
				    data.setData("AVERAGE_APPLY_AMT", i,
				    df2.format((data2.getDouble("APPLY_AMT",j)/data2.getDouble("APPLY_COUNT",j))));
				    data.setData("OWN_APPLY_PERCENT", i,
				    df1.format((data2.getDouble("OWN_APPLY",j)/data2.getDouble("TOT_AMT",j))*100));
			    }			    	
			}
			
	}
	//计算人次、金额完成比
		for (int i = 0; i < count; i++) {
			data.setData("SURPLUS_COUNT", i, df2.format((data.getDouble("APPLY_COUNT",i)/
					data.getDouble("YEAR_PAT_COUNT",i))*100)+"%");
			data.setData("SURPLUS_AMT", i, df2.format((data.getDouble("APPLY_AMT",i)/
					(data.getDouble("YEAR_QUOTA_AMT",i)*10000))*100)+"%");			
		}
//		System.out.println("data=========="+data);  
		 ((TTable) getComponent("TABLE")).setParmValue(data);	
	 }
	//门诊查询
	if(this.getValue("QUERY_TYPE").equals("2")){
		 String sql_condition ="";
	       if(this.getValue("YB_TYPE").equals("1"))
  	   sql_condition =" AND A.INS_CROWD_TYPE ='1'" +
  	   		          " AND A.INS_PAT_TYPE ='1'";//城职门诊
         else if(this.getValue("YB_TYPE").equals("2"))
       sql_condition =" AND A.INS_CROWD_TYPE ='1'" +
		              " AND A.INS_PAT_TYPE ='2'";//城职门特
         else
       sql_condition =" AND A.INS_CROWD_TYPE ='2'" +
                      " AND A.INS_PAT_TYPE ='2'";//城乡门特	 
	       
		String sql3 = " SELECT C.DEPT_CHN_DESC AS DEPT_DESC,COUNT(DISTINCT A.CASE_NO) AS APPLY_COUNT," +
		" SUM(A.OTOT_AMT+A.TOTAL_AGENT_AMT)  AS APPLY_AMT,"+
		" SUM(A.TOT_AMT-A.OTOT_AMT-A.TOTAL_AGENT_AMT-A.ARMY_AI_AMT-" +
		" A.ILLNESS_SUBSIDY_AMT-A.ACCOUNT_PAY_AMT) AS OWN_AMT,"+
		" SUM(A.OTOT_AMT+A.TOTAL_AGENT_AMT) AS OTOT_AMT," +
		" SUM(A.ARMY_AI_AMT+A.ILLNESS_SUBSIDY_AMT) AS ARMY_AI_AMT,"+
		" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,SUM(A.TOT_AMT) AS TOT_AMT" +
		" FROM INS_OPD A,REG_PATADM B,SYS_DEPT C" +
		" WHERE  A.CASE_NO = B.CASE_NO" +
		" AND A.INS_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')" +
		" AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')" +
		" AND B.DEPT_CODE = C.DEPT_CODE" +
		sql_condition+
		" GROUP BY  C.DEPT_CHN_DESC";
		TParm data3 = new TParm(TJDODBTool.getInstance().select(sql3));
//		System.out.println("data3=========="+data3);  
		if (data3.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		int count  = data3.getCount();
		double applycount =0;//申请人次合计
		double applyamt =0;//申请金额合计
		for (int i = 0; i < count; i++) {
			if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
			data3.setData("YEAR_PAT_COUNT", i, data.getData("YEAR_PAT_COUNT",0));
			data3.setData("YEAR_QUOTA_AMT", i, data.getData("YEAR_QUOTA_AMT",0));
			} else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){
			data3.setData("MON_PAT_COUNT", i, data.getData("MON_PAT_COUNT",0));
			data3.setData("MON_QUOTA_AMT", i, data.getData("MON_QUOTA_AMT",0));			
			}
			data3.setData("AVERAGE_PAY_AMT", i, data.getData("AVERAGE_PAY_AMT",0));
			data3.setData("OWN_PAY_PERCENT", i, data.getData("OWN_PAY_PERCENT",0));	
			//默认值
			data3.setData("AVERAGE_APPLY_AMT", i,0);
			data3.setData("OWN_APPLY_PERCENT", i,0);

			applycount+=StringTool.round(data3.getDouble("APPLY_COUNT", i),0);
			applyamt+=StringTool.round(data3.getDouble("APPLY_AMT", i),2);
		} 
		//计算人次、金额完成比
		for (int i = 0; i < count; i++) {
			if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
			data3.setData("SURPLUS_COUNT", i, df2.format((data3.getDouble("APPLY_COUNT",i)/
					data3.getDouble("YEAR_PAT_COUNT",i))*100)+"%");
			data3.setData("SURPLUS_AMT", i, df2.format((data3.getDouble("APPLY_AMT",i)/
					(data3.getDouble("YEAR_QUOTA_AMT",i)*10000))*100)+"%");
			}
			else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){
			data3.setData("SURPLUS_COUNT", i, df2.format((data3.getDouble("APPLY_COUNT",i)/
					data3.getDouble("MON_PAT_COUNT",i))*100)+"%");
			data3.setData("SURPLUS_AMT", i, df2.format((data3.getDouble("APPLY_AMT",i)/
					data3.getDouble("MON_QUOTA_AMT",i))*100)+"%");	
			}
		}
		data3.addData("DEPT_DESC", "合计") ;
		data3.addData("APPLY_COUNT",df1.format(applycount));
		data3.addData("APPLY_AMT",StringTool.round(applyamt,2));
		if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
		data3.addData("YEAR_PAT_COUNT",data.getData("YEAR_PAT_COUNT",0));
		data3.addData("YEAR_QUOTA_AMT",data.getData("YEAR_QUOTA_AMT",0));
		data3.addData("SURPLUS_COUNT",df2.format((applycount/
				data.getDouble("YEAR_PAT_COUNT",0))*100)+"%");
		data3.addData("SURPLUS_AMT",df2.format((applyamt/
			  (data.getDouble("YEAR_QUOTA_AMT",0)*10000))*100)+"%");
		
		}
		 else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){
		data3.addData("MON_PAT_COUNT",data.getData("MON_PAT_COUNT",0));
		data3.addData("MON_QUOTA_AMT",data.getData("MON_QUOTA_AMT",0));
		data3.addData("SURPLUS_COUNT",df2.format((applycount/
				data.getDouble("MON_PAT_COUNT",0))*100)+"%");
		data3.addData("SURPLUS_AMT",df2.format((applyamt/
			  data.getDouble("MON_QUOTA_AMT",0))*100)+"%");
		}
		data3.addData("AVERAGE_PAY_AMT", data.getData("AVERAGE_PAY_AMT",0));
		data3.addData("OWN_PAY_PERCENT", data.getData("OWN_PAY_PERCENT",0));
		//默认值
		data3.addData("AVERAGE_APPLY_AMT",0);
		data3.addData("OWN_APPLY_PERCENT",0);  
		((TTable) getComponent("TABLE")).setParmValue(data3);
	}
	   //住院查询
	    if(this.getValue("QUERY_TYPE").equals("3")){
	       String sql_condition2 ="";
	       if(this.getValue("YB_TYPE").equals("1"))
    	   sql_condition2 =" AND SUBSTR (B.HIS_CTZ_CODE, 0, 1) ='1'";//城职
           else
    	   sql_condition2 =" AND SUBSTR (B.HIS_CTZ_CODE, 0, 1) ='2'";//城乡
	    String sql4 =" SELECT A.DEPT_DESC,COUNT(DISTINCT A.CASE_NO) AS APPLY_COUNT ,SUM(A.NHI_PAY) AS APPLY_AMT," +
	    " SUM(CASE WHEN A.RESTART_STANDARD_AMT IS NULL THEN 0 ELSE A.RESTART_STANDARD_AMT END + "+
	    " CASE WHEN A.STARTPAY_OWN_AMT IS NULL THEN 0 ELSE A.STARTPAY_OWN_AMT END + "+
	    " CASE WHEN A.OWN_AMT IS NULL THEN 0 ELSE A.OWN_AMT END  + "+
	    " CASE WHEN A.PERCOPAYMENT_RATE_AMT IS NULL THEN 0 ELSE A.PERCOPAYMENT_RATE_AMT END  + "+
	    " CASE WHEN A.ADD_AMT IS NULL THEN 0 ELSE A.ADD_AMT END + "+
	    " CASE WHEN A.INS_HIGHLIMIT_AMT IS NULL THEN 0 ELSE A.INS_HIGHLIMIT_AMT END - "+
	    " CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END  - "+ 
	    " CASE WHEN A.ARMYAI_AMT IS NULL THEN 0 ELSE A.ARMYAI_AMT END - "+
	    " CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END ) AS OWN_AMT,"+
	    " SUM(A.NHI_PAY) AS OTOT_AMT,SUM(A.NHI_COMMENT) AS NHI_COMMENT," +
	    " SUM(A.ARMYAI_AMT +A.ILLNESS_SUBSIDY_AMT) AS ARMYAI_AMT,"+
	    " SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+
	    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+" +
	    " A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT," +
	    " SUM(A.RESTART_STANDARD_AMT+A.STARTPAY_OWN_AMT+A.OWN_AMT+" +
	    " A.PERCOPAYMENT_RATE_AMT+A.ADD_AMT+A.INS_HIGHLIMIT_AMT) AS OWN_APPLY"+ 
	    " FROM INS_IBS A,INS_ADM_CONFIRM B"+
	    " WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
	    " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
	    " AND A.CASE_NO = B.CASE_NO"+
	    " AND A.ADM_SEQ = B.ADM_SEQ"+
	    " AND B.IN_STATUS IN( '2','3','4')"+
	    " AND B.SDISEASE_CODE IS NULL"+
	    sql_condition2+
	    " GROUP BY A.DEPT_DESC";
	    TParm data4 = new TParm(TJDODBTool.getInstance().select(sql4));
//	    System.out.println("data4=========="+data4);  
	    if (data4.getErrCode() < 0) {
	 	    this.messageBox("E0116");//没有数据
		    return;
	      }
	    int count  = data4.getCount();
	    double applycount =0;//申请人次合计
	    double applyamt =0;//申请金额合计
	    double totamt =0;//总额合计
	    double ownapply =0;//个人支付合计
	    for (int i = 0; i < count; i++) {
	    	if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
		    data4.setData("YEAR_PAT_COUNT", i, data.getData("YEAR_PAT_COUNT",0));
		    data4.setData("YEAR_QUOTA_AMT", i, data.getData("YEAR_QUOTA_AMT",0));
	    	} else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){	    	
		    data4.setData("MON_PAT_COUNT", i, data.getData("MON_PAT_COUNT",0));
		    data4.setData("MON_QUOTA_AMT", i, data.getData("MON_QUOTA_AMT",0));
	    	}
	    	data4.setData("AVERAGE_PAY_AMT", i, data.getData("AVERAGE_PAY_AMT",0));
			data4.setData("OWN_PAY_PERCENT", i, data.getData("OWN_PAY_PERCENT",0));	
	    	
		    applycount+=StringTool.round(data4.getDouble("APPLY_COUNT", i),0);
		    applyamt+=StringTool.round(data4.getDouble("APPLY_AMT", i),2);
		    
		    totamt+=StringTool.round(data4.getDouble("TOT_AMT", i),2);
		    ownapply+=StringTool.round(data4.getDouble("OWN_APPLY", i),2);
	     }
	  //计算人次、金额完成比,次均统筹支付,个人负担率
		for (int i = 0; i < count; i++) {
			if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
			data4.setData("SURPLUS_COUNT", i, df2.format((data4.getDouble("APPLY_COUNT",i)/
					data4.getDouble("YEAR_PAT_COUNT",i))*100)+"%");
			data4.setData("SURPLUS_AMT", i, df2.format((data4.getDouble("APPLY_AMT",i)/
					(data4.getDouble("YEAR_QUOTA_AMT",i)*10000))*100)+"%");
			}
			else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){
				data4.setData("SURPLUS_COUNT", i, df2.format((data4.getDouble("APPLY_COUNT",i)/
					data4.getDouble("MON_PAT_COUNT",i))*100)+"%");
			data4.setData("SURPLUS_AMT", i, df2.format((data4.getDouble("APPLY_AMT",i)/
					data4.getDouble("MON_QUOTA_AMT",i))*100)+"%");	
			}
			//次均统筹支付
			data4.setData("AVERAGE_APPLY_AMT", i,
			df2.format((data4.getDouble("APPLY_AMT",i)/data4.getDouble("APPLY_COUNT",i))));
			//个人负担率
			data4.setData("OWN_APPLY_PERCENT", i,
			df1.format((data4.getDouble("OWN_APPLY",i)/data4.getDouble("TOT_AMT",i))*100));
		} 		
	    data4.addData("DEPT_DESC", "合计") ;
	    data4.addData("APPLY_COUNT",df1.format(applycount));
	    data4.addData("APPLY_AMT",StringTool.round(applyamt,2));
	    if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
	    data4.addData("YEAR_PAT_COUNT",data.getData("YEAR_PAT_COUNT",0));
	    data4.addData("YEAR_QUOTA_AMT",data.getData("YEAR_QUOTA_AMT",0));
	    data4.addData("SURPLUS_COUNT",df2.format((applycount/
				data.getDouble("YEAR_PAT_COUNT",0))*100)+"%");
		data4.addData("SURPLUS_AMT",df2.format((applyamt/
			  (data.getDouble("YEAR_QUOTA_AMT",0)*10000))*100)+"%");
	    } else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){
	    data4.addData("MON_PAT_COUNT",data.getData("MON_PAT_COUNT",0));
	    data4.addData("MON_QUOTA_AMT",data.getData("MON_QUOTA_AMT",0));	
	    data4.addData("SURPLUS_COUNT",df2.format((applycount/
				data.getDouble("MON_PAT_COUNT",0))*100)+"%");
		data4.addData("SURPLUS_AMT",df2.format((applyamt/
			  data.getDouble("MON_QUOTA_AMT",0))*100)+"%");
	    }
	    data4.addData("AVERAGE_PAY_AMT", data.getData("AVERAGE_PAY_AMT",0));
		data4.addData("OWN_PAY_PERCENT", data.getData("OWN_PAY_PERCENT",0));
		//次均统筹支付
		data4.addData("AVERAGE_APPLY_AMT",df2.format((applyamt/applycount)));
		//个人负担率
		data4.addData("OWN_APPLY_PERCENT",df1.format((ownapply/totamt)*100));
	   ((TTable) getComponent("TABLE")).setParmValue(data4);	
   }
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="";
    	if(this.getValue("QUERY_TYPE").equals("1"))
    		title = "全院医保总额查询表";
    	if(this.getValue("QUERY_TYPE").equals("2"))
    		 if(this.getValue("YB_TYPE").equals("1"))
    			 title = "城职门诊按科室统计查询表";
    		 else if(this.getValue("YB_TYPE").equals("2"))
    			 title = "城职门特按科室统计查询表";
    		 else
    			 title = "城乡门特按科室统计查询表";
    	if(this.getValue("QUERY_TYPE").equals("3")){
    		 if(this.getValue("YB_TYPE").equals("1"))
    		     title = "城职住院按科室统计查询表";
    		 else
    		     title = "城乡住院按科室统计查询表";	 
    	}
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }
    	
    /**
     * 清空
     */
    public void onClear() {
    	 getData();//初始化数据     
    }
    /**
     * 初始化开始日期和结束日期
     */
    public void onYear() {
    	//年度
    	 String year =getValue("YEAR").toString();
    	//指标类别
     	this.setValue("YEAR_MONTH_TYPE","1"); 
    	//开始日期
    	String startdate =getValue("YEAR")+"-04-01 00:00:00";
    	Timestamp date1 = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("START_DATE",date1);
    	//结束日期
    	DecimalFormat df = new DecimalFormat("##########0");
    	String nextyear =  df.format(Double.parseDouble(year)+1);
    	String enddate =nextyear+"-03-31 00:00:00";
    	Timestamp date2 = StringTool.getTimestamp(enddate, "yyyy-MM-dd HH:mm:ss");
    	this.setValue("END_DATE",date2);	
    	
    }
    /**
     * 初始化开始日期和结束日期
     */
    public void onYearMonth() {
    	if(this.getValue("YEAR_MONTH_TYPE").equals("1")){
    	//年度
        String year =getValue("YEAR").toString();
       	//开始日期
       	String startdate =getValue("YEAR")+"-04-01 00:00:00";
       	Timestamp date1 = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
       	this.setValue("START_DATE",date1);
       	//结束日期
       	DecimalFormat df = new DecimalFormat("##########0");
       	String nextyear =  df.format(Double.parseDouble(year)+1);
       	String enddate =nextyear+"-03-31 00:00:00";
       	Timestamp date2 = StringTool.getTimestamp(enddate, "yyyy-MM-dd HH:mm:ss");
       	this.setValue("END_DATE",date2);
        this.table.setHeader("类别/科室,140;年指标人次,100;完成人次,100;人次完成比,100;" +
                "年指标金额(万元),120;完成金额(元),100,double,#########0.00;金额完成比,100;" +
                "次均统筹指标(元),120;次均统筹(元),100;个人负担率指标(%),120;个人负担率(%),100");
        this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                          "4,right;5,right;6,right;7,right;8,right;9,right;10,right");
        this.table.setParmMap("DEPT_DESC;YEAR_PAT_COUNT;APPLY_COUNT;SURPLUS_COUNT;" +
                 "YEAR_QUOTA_AMT;APPLY_AMT;SURPLUS_AMT;" +
                 "AVERAGE_PAY_AMT;AVERAGE_APPLY_AMT;OWN_PAY_PERCENT;OWN_APPLY_PERCENT");
    	} 
    	else if(this.getValue("YEAR_MONTH_TYPE").equals("2")){
        //开始日期
        String startdate =getValue("YEAR")+"-04-01 00:00:00";
        Timestamp date1 = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
        this.setValue("START_DATE",date1);
        //结束日期
        String enddate =getValue("YEAR")+"-04-30 00:00:00";
        Timestamp date2 = StringTool.getTimestamp(enddate, "yyyy-MM-dd HH:mm:ss");
        this.setValue("END_DATE",date2);
        this.table.setHeader("类别/科室,140;月指标人次,100;完成人次,100;人次完成比,100;" +
                "月指标金额,100;完成金额,100,double,#########0.00;金额完成比,100;" +
                "次均统筹指标(元),120;次均统筹(元),100;个人负担率指标(%),120;个人负担率(%),100");
        this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                          "4,right;5,right;6,right;7,right;8,right;9,right;10,right");
   	    this.table.setParmMap("DEPT_DESC;MON_PAT_COUNT;APPLY_COUNT;SURPLUS_COUNT;" +
		              "MON_QUOTA_AMT;APPLY_AMT;SURPLUS_AMT;" +
		              "AVERAGE_PAY_AMT;AVERAGE_APPLY_AMT;OWN_PAY_PERCENT;OWN_APPLY_PERCENT"); 	
    	}    	
    }
    /**
     * 查询类别选择事件
     */
    public void querytype(){
    	if(this.getValue("QUERY_TYPE").equals("1")){//全院
    		callFunction("UI|YB_TYPE|setEnabled", false); //医保身份不可编辑            
            callFunction("UI|HZ_TYPE|setEnabled", false); //患者类别不可编辑 
    		callFunction("UI|DEPT_MONTH_TYPE|setEnabled", false); //汇总类别不可编辑 
    		callFunction("UI|YEAR_MONTH_TYPE|setEnabled", false); //指标类别不可编辑 
    		this.setValue("YEAR_MONTH_TYPE","1");//按年度查询
    		this.setValue("HZ_TYPE","1");//普通医保
            clearValue("YB_TYPE;DEPT_MONTH_TYPE"); 
            this.table.setHeader("类别/科室,140;年指标人次,100;完成人次,100;人次完成比,100;" +
	                             "年指标金额(万元),120;完成金额(元),100,double,#########0.00;金额完成比,100;" +
	                             "次均统筹指标(元),120;次均统筹(元),100;个人负担率指标(%),120;个人负担率(%),100");
            this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
            this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                                           "4,right;5,right;6,right;7,right;8,right;9,right;10,right");
            this.table.setParmMap("QUERY_TYPE;YEAR_PAT_COUNT;APPLY_COUNT;SURPLUS_COUNT;" +
		                          "YEAR_QUOTA_AMT;APPLY_AMT;SURPLUS_AMT;" +
		                          "AVERAGE_PAY_AMT;AVERAGE_APPLY_AMT;OWN_PAY_PERCENT;OWN_APPLY_PERCENT");
        }else if(this.getValue("QUERY_TYPE").equals("2")){//门诊
        	callFunction("UI|DEPT_MONTH_TYPE|setEnabled", true); //汇总类别可编辑
        	callFunction("UI|HZ_TYPE|setEnabled", false); //患者类别不可编辑 
        	callFunction("UI|YB_TYPE|setEnabled", true); //医保身份可编辑       
        	callFunction("UI|YEAR_MONTH_TYPE|setEnabled", true); //指标类别可编辑 
        	this.setValue("DEPT_MONTH_TYPE","1");//按科室汇总
        	this.setValue("YEAR_MONTH_TYPE","1");//按年度查询
        	clearValue("HZ_TYPE");
        	this.tcombobox.setStringData("[[id,name],[,],[1,城职门诊],[2,城职门特],[3,城乡门特]]");//医保身份
        	this.setValue("YB_TYPE","1");//城职门诊
        	this.table.setHeader("类别/科室,140;年指标人次,100;完成人次,100;人次完成比,100;" +
                     "年指标金额(万元),120;完成金额(元),100,double,#########0.00;金额完成比,100;" +
                     "次均统筹指标(元),120;次均统筹(元),100;个人负担率指标(%),120;个人负担率(%),100");
            this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
            this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                               "4,right;5,right;6,right;7,right;8,right;9,right;10,right"); 
        	this.table.setParmMap("DEPT_DESC;YEAR_PAT_COUNT;APPLY_COUNT;SURPLUS_COUNT;" +
     		              "YEAR_QUOTA_AMT;APPLY_AMT;SURPLUS_AMT;" +
     		              "AVERAGE_PAY_AMT;AVERAGE_APPLY_AMT;OWN_PAY_PERCENT;OWN_APPLY_PERCENT");	    
        }else if(this.getValue("QUERY_TYPE").equals("3")){//住院
            callFunction("UI|DEPT_MONTH_TYPE|setEnabled", true); //汇总类别可编辑
       	    callFunction("UI|HZ_TYPE|setEnabled", false); //患者类别可编辑 
       	    callFunction("UI|YB_TYPE|setEnabled", true); //医保身份可编辑
       	    callFunction("UI|YEAR_MONTH_TYPE|setEnabled", true); //指标类别可编辑 
       	   this.setValue("DEPT_MONTH_TYPE","1");//按科室汇总
       	   this.setValue("YEAR_MONTH_TYPE","1");//按年度查询
           this.setValue("HZ_TYPE","1");//普通医保
           this.tcombobox.setStringData("[[id,name],[,],[1,城职],[2,城乡]]");//医保身份
           this.setValue("YB_TYPE","1");//城职
           this.table.setHeader("类别/科室,140;年指标人次,100;完成人次,100;人次完成比,100;" +
                   "年指标金额(万元),120;完成金额(元),100,double,#########0.00;金额完成比,100;" +
                   "次均统筹指标(元),120;次均统筹(元),100;个人负担率指标(%),120;个人负担率(%),100");
           this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
           this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                             "4,right;5,right;6,right;7,right;8,right;9,right;10,right"); 
      	    this.table.setParmMap("DEPT_DESC;YEAR_PAT_COUNT;APPLY_COUNT;SURPLUS_COUNT;" +
   		              "YEAR_QUOTA_AMT;APPLY_AMT;SURPLUS_AMT;" +
   		              "AVERAGE_PAY_AMT;AVERAGE_APPLY_AMT;OWN_PAY_PERCENT;OWN_APPLY_PERCENT");         	
       } 
    	this.callFunction("UI|TABLE|setParmValue", new TParm());
   	     //年度
        String year =getValue("YEAR").toString();
        //开始日期
        String startdate =getValue("YEAR")+"-04-01 00:00:00";
        Timestamp date1 = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
        this.setValue("START_DATE",date1);
        //结束日期
        DecimalFormat df = new DecimalFormat("##########0");
        String nextyear =  df.format(Double.parseDouble(year)+1);
        String enddate =nextyear+"-03-31 00:00:00";
        Timestamp date2 = StringTool.getTimestamp(enddate, "yyyy-MM-dd HH:mm:ss");
        this.setValue("END_DATE",date2);     
    }   
}
