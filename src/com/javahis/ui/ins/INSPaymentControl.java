package com.javahis.ui.ins;



import java.text.DecimalFormat;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.SystemTool;

/**
 * Title: 医保支付项目查询
 * Description:医保支付项目查询
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class INSPaymentControl extends TControl {
	private TTable table;
	private TComboBox combobox;
	DecimalFormat df1 = new DecimalFormat("##########0");
	DecimalFormat df2 = new DecimalFormat("##########0.00");
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        combobox = (TComboBox) this.getComponent("YB_TYPE");
        onClear();
    }
    /**
     * 初始化数据
     */
    public void getData() { 	
    	//开始日期
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
    	//结束日期
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	//初始化
    	this.callFunction("UI|TABLE|setParmValue", new TParm());
        this.setValue("QUERY_TYPE","1");
    	if(this.getValue("QUERY_TYPE").equals("1")){
    	callFunction("UI|YB_TYPE|setEnabled", true); //医保身份不可编辑      	
    	combobox.setStringData("[[id,name],[,],[1,城职门诊],[2,城职门特],[3,城乡门特]]");
        callFunction("UI|HZ_TYPE|setEnabled", false); //患者类别不可编辑 
        callFunction("UI|ZF_TYPE|setEnabled", false); //支付类别不可编辑 
    	clearValue("HZ_TYPE;ZF_TYPE;YB_TYPE");
        this.table.setHeader("科室,140;人次,100;自费金额,140,double,#########0.00;" +
        		 "基金支付金额,140,double,#########0.00;补助金额,140,double,#########0.00;" +
        		 "城乡大病金额,140,double,#########0.00;" +
        		 "个人账户金额,140,double,#########0.00;总费用金额,140,double,#########0.00") ;
        this.table.setLockColumns("0,1,2,3,4,5,6,7");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                                                    "4,right;5,right;6,right;7,right");
		this.table.setParmMap("DEPT_DESC;APPLY_COUNT;OWN_AMT;OTOT_AMT;" +
				              "ARMY_AI_AMT;ILLNESS_SUBSIDY_AMT;ACCOUNT_PAY_AMT;TOT_AMT");
    	}
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
    	if(this.getValue("QUERY_TYPE").equals("")){
    		this.messageBox("查询类别不能为空");
    		return true;
    	}
    	if(this.getValue("QUERY_TYPE").equals("2")){
    		 if(this.getValue("HZ_TYPE").equals("")){
     	    	this.messageBox("患者类别不能为空");
     	    	return true;
     	    	}
    	    if(this.getValue("ZF_TYPE").equals("")){
    	    	this.messageBox("支付类别不能为空");
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
    	 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "START_DATE")), "yyyyMMdd")+"000000"; //开始日期
    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "END_DATE")), "yyyyMMdd")+"235959"; //结束日期
    	 TParm parm = new TParm();
    	 parm.setData("START_DATE", startdate);
    	 parm.setData("END_DATE", enddate);
	
	//门诊查询
	if(this.getValue("QUERY_TYPE").equals("1")){
		String sql ="";
		 //医保类别选择
	       if(this.getValue("YB_TYPE").equals("1"))
 	          sql =" AND A.INS_CROWD_TYPE ='1'" +
                   " AND A.INS_PAT_TYPE = '1'";//城职门诊
          else if(this.getValue("YB_TYPE").equals("2"))
 	          sql =" AND A.INS_CROWD_TYPE ='1'" +
                   " AND A.INS_PAT_TYPE = '2'";//城职门特
 	      else if(this.getValue("YB_TYPE").equals("3"))
 	    	  sql =" AND A.INS_CROWD_TYPE ='2'" +
                   " AND A.INS_PAT_TYPE = '2'";//城乡门特
	       
		String sql3 = " SELECT C.DEPT_CHN_DESC AS DEPT_DESC,COUNT(DISTINCT A.CASE_NO) AS APPLY_COUNT," +
		" SUM(A.OTOT_AMT+A.TOTAL_AGENT_AMT)  AS APPLY_AMT,"+
		" SUM(A.TOT_AMT-A.OTOT_AMT-A.TOTAL_AGENT_AMT-A.ARMY_AI_AMT-" +
		" A.ILLNESS_SUBSIDY_AMT-A.ACCOUNT_PAY_AMT) AS OWN_AMT,"+
		" SUM(A.OTOT_AMT+A.TOTAL_AGENT_AMT) AS OTOT_AMT," +
		" SUM(A.ARMY_AI_AMT) AS ARMY_AI_AMT," +
		" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,"+
		" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,SUM(A.TOT_AMT) AS TOT_AMT" +
		" FROM INS_OPD A,REG_PATADM B,SYS_DEPT C" +
		" WHERE  A.CASE_NO = B.CASE_NO" +
		" AND A.INS_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')" +
		" AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')" +
		sql+
		" AND B.DEPT_CODE = C.DEPT_CODE" +
		" GROUP BY  C.DEPT_CHN_DESC";
		TParm data3 = new TParm(TJDODBTool.getInstance().select(sql3));
//		System.out.println("data3=========="+data3);  
		if (data3.getErrCode() < 0) {
			this.messageBox("E0116");//没有数据
			return;
		}
		int count  = data3.getCount();
		double applycount =0;//申请人次合计
	    double ownamt =0;//自费金额合计
	    double ototamt =0;//基金支付金额合计
	    double armyaiamt =0;//补助金额合计
	    double illnesssubsidyamt=0;//城乡大病金额合计
	    double accountpayamt=0;//个人账户金额
	    double totamt =0;//总费用金额
		for (int i = 0; i < count; i++) {
			applycount+=StringTool.round(data3.getDouble("APPLY_COUNT", i),0);
			ownamt+=StringTool.round(data3.getDouble("OWN_AMT", i),2);
		    ototamt+=StringTool.round(data3.getDouble("OTOT_AMT", i),2);
		    armyaiamt+=StringTool.round(data3.getDouble("ARMY_AI_AMT", i),2);
		    illnesssubsidyamt+=StringTool.round(data3.getDouble("ILLNESS_SUBSIDY_AMT", i),2);
		    accountpayamt+=StringTool.round(data3.getDouble("ACCOUNT_PAY_AMT", i),2);
		    totamt+=StringTool.round(data3.getDouble("TOT_AMT", i),2);
		}
		data3.addData("DEPT_DESC", "合计") ;
		data3.addData("APPLY_COUNT",df1.format(applycount));
	    data3.addData("OWN_AMT",StringTool.round(ownamt,2));
	    data3.addData("OTOT_AMT",StringTool.round(ototamt,2));
	    data3.addData("ARMY_AI_AMT",StringTool.round(armyaiamt,2));
	    data3.addData("ILLNESS_SUBSIDY_AMT",StringTool.round(illnesssubsidyamt,2));
	    data3.addData("ACCOUNT_PAY_AMT",StringTool.round(accountpayamt,2));
	    data3.addData("TOT_AMT",StringTool.round(totamt,2));
		((TTable) getComponent("TABLE")).setParmValue(data3);		
	}
	   //住院查询
	    if(this.getValue("QUERY_TYPE").equals("2")){
	    	TParm data4 = new TParm();
	       String sql_condition ="";
	       String sql_condition2 ="";
	       //医保类别选择
	       if(this.getValue("YB_TYPE").equals("1"))
    	   sql_condition2 =" AND D.INS_CROWD_TYPE ='1'";//城职
           else if(this.getValue("YB_TYPE").equals("2"))
    	   sql_condition2 =" AND D.INS_CROWD_TYPE ='2'";//城乡
	       //支付类别选择
	       if(this.getValue("ZF_TYPE").equals("1"))
	       sql_condition =" AND B.IN_STATUS IN( '2','3','4')";//全部
	       else if(this.getValue("ZF_TYPE").equals("2"))
	       sql_condition =" AND B.IN_STATUS IN( '2','3')";//未支付
	       else
	       sql_condition =" AND B.IN_STATUS = '4'";//已支付 
	       //患者类别选择
	       if(this.getValue("HZ_TYPE").equals("1")){//普通医保
	   	    String sql4 =" SELECT A.DEPT_CODE,C.DEPT_CHN_DESC,COUNT(A.CASE_NO) AS APPLY_COUNT ,SUM(A.NHI_PAY) AS APPLY_AMT," +
		    " SUM(CASE WHEN A.RESTART_STANDARD_AMT IS NULL THEN 0 ELSE A.RESTART_STANDARD_AMT END + "+
		    " CASE WHEN A.STARTPAY_OWN_AMT IS NULL THEN 0 ELSE A.STARTPAY_OWN_AMT END + "+
		    " CASE WHEN A.OWN_AMT IS NULL THEN 0 ELSE A.OWN_AMT END  + "+
		    " CASE WHEN A.PERCOPAYMENT_RATE_AMT IS NULL THEN 0 ELSE A.PERCOPAYMENT_RATE_AMT END  + "+
		    " CASE WHEN A.ADD_AMT IS NULL THEN 0 ELSE A.ADD_AMT END + "+
		    " CASE WHEN A.INS_HIGHLIMIT_AMT IS NULL THEN 0 ELSE A.INS_HIGHLIMIT_AMT END - "+
		    " CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END  - "+ 
		    " CASE WHEN A.ARMYAI_AMT IS NULL THEN 0 ELSE A.ARMYAI_AMT END - "+
		    " CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END ) AS OWN_AMT,"+
		    " SUM(A.NHI_PAY) AS OTOT_AMT," +
		    " SUM (CASE WHEN D.INS_CROWD_TYPE = '1' THEN A.NHI_COMMENT ELSE  A.NHI_COMMENT+A.ARMYAI_AMT END) AS NHI_COMMENT,"+ 
            " SUM (CASE WHEN D.INS_CROWD_TYPE = '1' THEN A.ARMYAI_AMT ELSE 0 END ) AS ARMYAI_AMT,"+
		    " '0.00' SINGLE_STANDARD_OWN_AMT,"+ 
	    	" '0.00' SINGLE_SUPPLYING_AMT,"+
		    " SUM(CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS ILLNESS_SUBSIDY_AMT,"+
		    " SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+
		    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+" +
		    " A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT"+ 
		    " FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_DEPT C,SYS_CTZ D"+
		    " WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
		    " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
		    " AND A.CASE_NO = B.CASE_NO"+
		    " AND A.ADM_SEQ = B.ADM_SEQ" +
		    " AND B.HIS_CTZ_CODE = D.CTZ_CODE"+	    
		    " AND A.DEPT_CODE = C.DEPT_CODE"+
		    " AND ( B.INS_CROWD_TYPE IN('1','2') OR B.INS_CROWD_TYPE IS NULL)"+
		    sql_condition+
		    " AND B.SDISEASE_CODE IS NULL" +
		    sql_condition2+
		    " GROUP BY A.DEPT_CODE, C.DEPT_CHN_DESC";
		     data4 = new TParm(TJDODBTool.getInstance().select(sql4));
//		    System.out.println("data4=========="+data4);     
	       }
	       //单病种
	       else if(this.getValue("HZ_TYPE").equals("2")){
	    	String sql4 =" SELECT A.DEPT_CODE,C.DEPT_CHN_DESC,COUNT(A.CASE_NO) AS APPLY_COUNT ,SUM(A.NHI_PAY) AS APPLY_AMT," +
	    	" SUM( CASE WHEN TO_NUMBER(TO_CHAR(B.IN_DATE,'yyyymmdd'))< 20170101 THEN  ( " +
	    	" CASE WHEN A.STARTPAY_OWN_AMT IS NULL THEN 0 ELSE A.STARTPAY_OWN_AMT END +"+
	    	" CASE WHEN A.BED_SINGLE_AMT IS NULL THEN 0 ELSE A.BED_SINGLE_AMT END  + " +
	    	" CASE WHEN A.MATERIAL_SINGLE_AMT IS NULL THEN 0 ELSE A.MATERIAL_SINGLE_AMT END+"+
	    	" CASE WHEN A.PERCOPAYMENT_RATE_AMT IS NULL THEN 0 ELSE A.PERCOPAYMENT_RATE_AMT END + " +
	    	" CASE WHEN A.INS_HIGHLIMIT_AMT IS NULL THEN 0 ELSE A.INS_HIGHLIMIT_AMT END -"+
	    	" CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END  - " +
	    	" CASE WHEN A.ARMYAI_AMT IS NULL THEN 0 ELSE A.ARMYAI_AMT END -"+ 
	    	" CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END ) ELSE"+ 
	    	" ( CASE WHEN A.STARTPAY_OWN_AMT IS NULL THEN 0 ELSE A.STARTPAY_OWN_AMT END +"+ 
	    	" CASE WHEN A.BED_SINGLE_AMT IS NULL THEN 0 ELSE A.BED_SINGLE_AMT END  + " +
	    	" CASE WHEN A.MATERIAL_SINGLE_AMT IS NULL THEN 0 ELSE A.MATERIAL_SINGLE_AMT END+"+ 
	    	" CASE WHEN A.PERCOPAYMENT_RATE_AMT IS NULL THEN 0 ELSE A.PERCOPAYMENT_RATE_AMT END + " +
	    	" CASE WHEN A.INS_HIGHLIMIT_AMT IS NULL THEN 0 ELSE A.INS_HIGHLIMIT_AMT END -"+ 
	    	" CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END  - " +
	    	" CASE WHEN A.ARMYAI_AMT IS NULL THEN 0 ELSE A.ARMYAI_AMT END - "+ 
	    	" CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END + "+ 
	    	" CASE WHEN A.RESTART_STANDARD_AMT IS NULL THEN 0 ELSE A.RESTART_STANDARD_AMT END ) END ) AS OWN_AMT,"+ 
	    	" SUM(A.NHI_PAY) AS OTOT_AMT," +
	    	" SUM (CASE WHEN D.INS_CROWD_TYPE = '1' THEN A.NHI_COMMENT ELSE  A.NHI_COMMENT+A.ARMYAI_AMT END) AS NHI_COMMENT,"+ 
            " SUM (CASE WHEN D.INS_CROWD_TYPE = '1' THEN A.ARMYAI_AMT ELSE 0 END ) AS ARMYAI_AMT,"+
	    	" SUM(CASE WHEN A.SINGLE_STANDARD_OWN_AMT IS NULL THEN 0 ELSE A.SINGLE_STANDARD_OWN_AMT END) AS SINGLE_STANDARD_OWN_AMT,"+ 
	    	" SUM(CASE WHEN A.SINGLE_SUPPLYING_AMT IS NULL THEN 0 ELSE A.SINGLE_SUPPLYING_AMT END) AS SINGLE_SUPPLYING_AMT,"+
	    	" SUM(CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS ILLNESS_SUBSIDY_AMT,"+
			" SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+			
			" SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+" +
			" A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT"+ 
			" FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_DEPT C,SYS_CTZ D"+
			" WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
			" AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
			" AND A.CASE_NO = B.CASE_NO"+
			" AND A.ADM_SEQ = B.ADM_SEQ"+
			" AND B.HIS_CTZ_CODE = D.CTZ_CODE"+	    
			" AND A.DEPT_CODE = C.DEPT_CODE"+
			sql_condition+
			" AND B.SDISEASE_CODE IS NOT NULL"+
			sql_condition2+
			" GROUP BY A.DEPT_CODE, C.DEPT_CHN_DESC";
			 data4 = new TParm(TJDODBTool.getInstance().select(sql4));
//			    System.out.println("data4=========="+data4);  	   
	       }
	       else if(this.getValue("HZ_TYPE").equals("3")){//异地
	    	 String sql4 = " SELECT A.DEPT_CODE,C.DEPT_CHN_DESC,COUNT(A.CASE_NO) AS APPLY_COUNT ,SUM(A.NHI_PAY) AS APPLY_AMT,"+ 
			    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+ A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT-"+
			    " A.NHI_PAY-A.NHI_COMMENT- A.ARMYAI_AMT-CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END-"+
			    " CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS OWN_AMT, "+
			    " SUM(A.NHI_PAY) AS OTOT_AMT, SUM(A.NHI_COMMENT) AS NHI_COMMENT, SUM (A.ARMYAI_AMT) AS ARMYAI_AMT,"+
			    " SUM(CASE WHEN A.SINGLE_STANDARD_OWN_AMT IS NULL THEN 0 ELSE A.SINGLE_STANDARD_OWN_AMT END) AS SINGLE_STANDARD_OWN_AMT,"+
			    " SUM(CASE WHEN A.SINGLE_SUPPLYING_AMT IS NULL THEN 0 ELSE A.SINGLE_SUPPLYING_AMT END) AS SINGLE_SUPPLYING_AMT, "+
			    " SUM(CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS ILLNESS_SUBSIDY_AMT,"+ 
			    " SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+ 
			    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+ A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT"+ 
			    " FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_DEPT C,SYS_CTZ D"+
			    " WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
			    " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
			    " AND A.CASE_NO = B.CASE_NO"+
			    " AND A.ADM_SEQ = B.ADM_SEQ "+
			    " AND B.HIS_CTZ_CODE = D.CTZ_CODE"+		    
			    " AND A.DEPT_CODE = C.DEPT_CODE "+
			    " AND B.INS_CROWD_TYPE = '3'"+
			    sql_condition+
			    " AND B.SDISEASE_CODE IS NULL"+
			    sql_condition2+
			    " GROUP BY A.DEPT_CODE, C.DEPT_CHN_DESC"; 
	    	 data4 = new TParm(TJDODBTool.getInstance().select(sql4));
	       }	       
	       else if(this.getValue("HZ_TYPE").equals("4")){//异地新农合
	    	   String sql4 = "SELECT B.DEPT_CODE,C.DEPT_CHN_DESC,COUNT(A.CASE_NO) AS APPLY_COUNT,SUM(A.OWN_AMT) AS OWN_AMT,"
	    		   			+ " '0.00' SINGLE_STANDARD_OWN_AMT, '0.00' SINGLE_SUPPLYING_AMT,SUM(A.REAL_INS_AMT) AS OTOT_AMT,"
	    		   			+ " '0.00' NHI_COMMENT,'0.00' ARMYAI_AMT,'0.00' ILLNESS_SUBSIDY_AMT,'0.00' ACCOUNT_PAY_AMT,SUM(A.TOT_AMT) AS TOT_AMT"
	    		   			+ " FROM INS_XNH A,ADM_INP B,SYS_DEPT C "
	    		   			+ " WHERE A.CASE_NO = B.CASE_NO AND B.DEPT_CODE = C.DEPT_CODE "
	    		   			+ " AND A.SETTLE_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS') "
	    		   			+ " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"
	    		   			+ " GROUP BY B.DEPT_CODE,C.DEPT_CHN_DESC";
	    	   data4 = new TParm(TJDODBTool.getInstance().select(sql4));
	       }
	       	       
	       if (data4.getErrCode() < 0) {
		 	    this.messageBox("E0116");//没有数据
			    return;
		      }     
		    int count  = data4.getCount();
		    double applycount =0;//申请人次合计
		    double ownamt =0;//实际自负金额合计
		    double singlestandardownamt =0;//医院超病种起付标准自负金额
			double singlesupplyingamt =0;//基本医疗保险补足金额
		    double ototamt =0;//基金支付金额合计
		    double nhicomment =0;//大额救助金额合计
		    double armyaiamt =0;//补助金额合计
		    double illnesssubsidyamt=0;//城乡大病金额合计 
		    double accountpayamt=0;//个人账户金额
		    double totamt =0;//总费用金额		    
		    for (int i = 0; i < count; i++) {
			    applycount+=StringTool.round(data4.getDouble("APPLY_COUNT", i),0);
			    ownamt+=StringTool.round(data4.getDouble("OWN_AMT", i),2);
			    singlestandardownamt+=StringTool.round(data4.getDouble("SINGLE_STANDARD_OWN_AMT", i),2);
				singlesupplyingamt+=StringTool.round(data4.getDouble("SINGLE_SUPPLYING_AMT", i),2);
			    ototamt+=StringTool.round(data4.getDouble("OTOT_AMT", i),2);
			    nhicomment+=StringTool.round(data4.getDouble("NHI_COMMENT", i),2);
			    armyaiamt+=StringTool.round(data4.getDouble("ARMYAI_AMT", i),2);
			    illnesssubsidyamt+=StringTool.round(data4.getDouble("ILLNESS_SUBSIDY_AMT", i),2); 
			    accountpayamt+=StringTool.round(data4.getDouble("ACCOUNT_PAY_AMT", i),2);
			    totamt+=StringTool.round(data4.getDouble("TOT_AMT", i),2);
		    }	    
		    data4.addData("DEPT_CHN_DESC", "合计") ;
		    data4.addData("APPLY_COUNT",df1.format(applycount));
		    data4.addData("OWN_AMT",StringTool.round(ownamt,2));
		    data4.addData("SINGLE_STANDARD_OWN_AMT",StringTool.round(singlestandardownamt,2));
		    data4.addData("SINGLE_SUPPLYING_AMT",StringTool.round(singlesupplyingamt,2));
		    data4.addData("OTOT_AMT",StringTool.round(ototamt,2));
		    data4.addData("NHI_COMMENT",StringTool.round(nhicomment,2));
		    data4.addData("ARMYAI_AMT",StringTool.round(armyaiamt,2));
		    data4.addData("ILLNESS_SUBSIDY_AMT",StringTool.round(illnesssubsidyamt,2));
		    data4.addData("ACCOUNT_PAY_AMT",StringTool.round(accountpayamt,2));
		    data4.addData("TOT_AMT",StringTool.round(totamt,2));
		     ((TTable) getComponent("TABLE")).setParmValue(data4);	      
	       
   }
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="";
    	String  a ="";
    	String  b ="";
    	String  c ="";
    	String  d ="";
    	if(this.getValue("QUERY_TYPE").equals("1")){
    		if(this.getValue("YB_TYPE").equals("1"))
        		d = "城职门诊";
           else if(this.getValue("YB_TYPE").equals("2"))
        		d = "城职门特"; 
           else if(this.getValue("YB_TYPE").equals("3"))
        	    d = "城乡门特"; 
    		title = "门诊"+d+"统计查询表";
    	}
    	if(this.getValue("QUERY_TYPE").equals("2")){
    		if(this.getValue("ZF_TYPE").equals("1"))
    		a ="全部";
    		else if (this.getValue("ZF_TYPE").equals("2"))
    		a ="未支付";
    		else
    		a ="已支付";
    	    if(this.getValue("HZ_TYPE").equals("1"))
    	    b="普通医保";	
    	    else if (this.getValue("HZ_TYPE").equals("2"))
    	    b="单病种";
    	    else if (this.getValue("HZ_TYPE").equals("3"))
        	b="异地";
    	    else
    	    b="异地新农合";		
    		if(this.getValue("YB_TYPE").equals("1"))
    		c = "城职";
    		else if(this.getValue("YB_TYPE").equals("2"))
    		c = "城乡";   			 
    		title = "住院"+a+b+c+"查询表";
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
     * 获得病患明细
     */
    public void onSelect() {
    	if(this.getValue("QUERY_TYPE").equals("2")){
    	int Row = table.getSelectedRow();//行数
    	TParm data = table.getParmValue().getRow(Row);//获得数据
        String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+"000000"; //开始日期
   	     String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+"235959"; //结束日期
   	    TParm parm = new TParm();
   	    parm.setData("START_DATE", startdate);
   	    parm.setData("END_DATE", enddate);
   	    parm.setData("ZF_TYPE", this.getValue("ZF_TYPE"));
   	    parm.setData("HZ_TYPE", this.getValue("HZ_TYPE"));
   	    parm.setData("YB_TYPE", this.getValue("YB_TYPE"));
   	    parm.setData("DEPT_CODE", data.getValue("DEPT_CODE"));
    	this.openDialog("%ROOT%\\config\\ins\\INSPaymentDetail.x",parm);
    	 }
    }
    /**
     * 查询类别选择事件
     */
    public void querytype(){
     if (this.getValue("QUERY_TYPE").equals("1")){//门诊
        callFunction("UI|YB_TYPE|setEnabled", true); //医保身份不可编辑
    	combobox.setStringData("[[id,name],[,],[1,城职门诊],[2,城职门特],[3,城乡门特]]");
        callFunction("UI|HZ_TYPE|setEnabled", false); //患者类别不可编辑
        callFunction("UI|ZF_TYPE|setEnabled", false); //支付类别不可编辑 
    	clearValue("HZ_TYPE;ZF_TYPE;YB_TYPE");
        this.table.setHeader("科室,140;人次,100;自费金额,140,double,#########0.00;" +
              		 "基金支付金额,140,double,#########0.00;补助金额,140,double,#########0.00;" +
              		 "城乡大病金额,140,double,#########0.00;" +
              		 "个人账户金额,140,double,#########0.00;总费用金额,140,double,#########0.00") ;
        this.table.setLockColumns("0,1,2,3,4,5,6,7");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                                                    "4,right;5,right;6,right;7,right");
      	this.table.setParmMap("DEPT_DESC;APPLY_COUNT;OWN_AMT;OTOT_AMT;" +
      				          "ARMY_AI_AMT;ILLNESS_SUBSIDY_AMT;ACCOUNT_PAY_AMT;TOT_AMT");  
     }else if(this.getValue("QUERY_TYPE").equals("2")){//住院
       	    callFunction("UI|HZ_TYPE|setEnabled", true); //患者类别可编辑 
       	    callFunction("UI|YB_TYPE|setEnabled", true); //医保身份可编辑
       		combobox.setStringData("[[id,name],[,],[1,城职],[2,城乡]]");
       	    callFunction("UI|ZF_TYPE|setEnabled", true); //支付类别可编辑
           this.setValue("HZ_TYPE","1");//普通医保
           this.setValue("YB_TYPE","");//默认值
           this.setValue("ZF_TYPE","1");//全部
           this.table.setHeader("科室,140;人次,100;" +
        		 "实际自负金额,140,double,#########0.00;" +
        		 "超病种起付标准自负金额,200,double,#########0.00;" +
  	    	     "基本医疗保险补足金额,200,double,#########0.00;" +
        		 "基金支付金额,140,double,#########0.00;" +
  	             "大额救助金额,140,double,#########0.00;" +
  	             "补助金额,140,double,#########0.00;" +
  	             "城乡大病金额,140,double,#########0.00;" +
  	             "个人账户金额,140,double,#########0.00;" +
  	             "总费用金额,140,double,#########0.00");
           this.table.setLockColumns("0,1,2,3,4,5,6,7,8,9,10");
           this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                             "4,right;5,right;6,right;7,right;8,right;9,right;10,right"); 
      	    this.table.setParmMap("DEPT_CHN_DESC;APPLY_COUNT;OWN_AMT;SINGLE_STANDARD_OWN_AMT;" +
      	    		              "SINGLE_SUPPLYING_AMT;OTOT_AMT;NHI_COMMENT;" +
			                      "ARMYAI_AMT;ILLNESS_SUBSIDY_AMT;ACCOUNT_PAY_AMT;TOT_AMT");         	
       } 
    	this.callFunction("UI|TABLE|setParmValue", new TParm());    
    }
}
