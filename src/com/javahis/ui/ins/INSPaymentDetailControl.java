package com.javahis.ui.ins;




import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;


/**
 * Title: 医保支付项目查询明细表
 * Description:医保支付项目查询明细表
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2014
 * @version 1.0
 */
public class INSPaymentDetailControl extends TControl {
	private TParm parm;
	private TTable table;
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        parm = (TParm) getParameter();//查询信息
//       System.out.println("parm===========" + parm);
		//若无此信息返回
		if (null == parm) {
			return;
		}	   
		onQuery();
    }
    /**
     * 查询
     */
    public void onQuery() { 		
	       String sql_condition ="";
	       String sql_condition2 ="";
	       String sql_condition3 ="";
	       //医保类别选择
	       if(parm.getValue("YB_TYPE").equals("1"))
    	   sql_condition2 =" AND C.INS_CROWD_TYPE ='1'";//城职
           else if(parm.getValue("YB_TYPE").equals("2"))
    	   sql_condition2 =" AND C.INS_CROWD_TYPE ='2'";//城乡
	       //支付类别选择
	       if(parm.getValue("ZF_TYPE").equals("1"))
	       sql_condition =" AND B.IN_STATUS IN( '2','3','4')";//全部
	       else if(parm.getValue("ZF_TYPE").equals("2"))
	       sql_condition =" AND B.IN_STATUS IN( '2','3')";//未支付
	       else
	       sql_condition =" AND B.IN_STATUS = '4'";//已支付 
	       //科室选择
	       if(!parm.getValue("DEPT_CODE").equals("")) 	    	   
	       sql_condition3 =" AND A.DEPT_CODE ='"+ parm.getValue("DEPT_CODE")+ "'";
	       String sql ="";
	     if(parm.getValue("HZ_TYPE").equals("1"))//普通医保    
	    sql =" SELECT A.MR_NO,A.PAT_NAME,D.CHN_DESC,A.IDNO,C.CTZ_DESC,SUM(A.NHI_PAY) AS APPLY_AMT," +
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
	    " SUM (CASE WHEN C.INS_CROWD_TYPE = '1' THEN A.NHI_COMMENT ELSE  A.NHI_COMMENT+A.ARMYAI_AMT END) AS NHI_COMMENT,"+ 
        " SUM (CASE WHEN C.INS_CROWD_TYPE = '1' THEN A.ARMYAI_AMT ELSE 0 END ) AS ARMYAI_AMT,"+
        " '0.00' SINGLE_STANDARD_OWN_AMT,"+ 
    	" '0.00' SINGLE_SUPPLYING_AMT,"+
	    " SUM(CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS ILLNESS_SUBSIDY_AMT,"+
	    " SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+
	    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+" +
	    " A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT"+ 
	    " FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_CTZ C,SYS_DICTIONARY D"+
	    " WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
	    " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
	    " AND A.CASE_NO = B.CASE_NO"+
	    " AND A.ADM_SEQ = B.ADM_SEQ"+
	    " AND B.HIS_CTZ_CODE = C.CTZ_CODE"+
	    " AND ( B.INS_CROWD_TYPE IN('1','2') OR B.INS_CROWD_TYPE IS NULL)"+
        " AND C.NHI_CTZ_FLG = 'Y'"+
        " AND D.GROUP_ID = 'SYS_SEX'"+
        " AND A.SEX_CODE=D.ID"+
	    sql_condition+
	    "AND B.SDISEASE_CODE IS NULL"+
	    sql_condition2+
	    sql_condition3+
	    " GROUP BY A.MR_NO,A.PAT_NAME,D.CHN_DESC,A.IDNO,C.CTZ_DESC";
	    //单病种
	    else if(parm.getValue("HZ_TYPE").equals("2"))
	    sql =" SELECT A.MR_NO,A.PAT_NAME,D.CHN_DESC,A.IDNO,C.CTZ_DESC,SUM(A.NHI_PAY) AS APPLY_AMT," +
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
    	" SUM (CASE WHEN C.INS_CROWD_TYPE = '1' THEN A.NHI_COMMENT ELSE  A.NHI_COMMENT+A.ARMYAI_AMT END) AS NHI_COMMENT,"+ 
        " SUM (CASE WHEN C.INS_CROWD_TYPE = '1' THEN A.ARMYAI_AMT ELSE 0 END ) AS ARMYAI_AMT,"+
    	" SUM(CASE WHEN A.SINGLE_STANDARD_OWN_AMT IS NULL THEN 0 ELSE A.SINGLE_STANDARD_OWN_AMT END) AS SINGLE_STANDARD_OWN_AMT,"+ 
    	" SUM(CASE WHEN A.SINGLE_SUPPLYING_AMT IS NULL THEN 0 ELSE A.SINGLE_SUPPLYING_AMT END) AS SINGLE_SUPPLYING_AMT,"+
    	" SUM(CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS ILLNESS_SUBSIDY_AMT,"+
		" SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+
		" SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+" +
		" A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT"+ 
	 	" FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_CTZ C,SYS_DICTIONARY D"+
	 	" WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
	 	" AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
	 	" AND A.CASE_NO = B.CASE_NO"+
	 	" AND A.ADM_SEQ = B.ADM_SEQ"+
	 	" AND B.HIS_CTZ_CODE = C.CTZ_CODE"+
	    " AND C.NHI_CTZ_FLG = 'Y'"+
	    " AND D.GROUP_ID = 'SYS_SEX'"+
	    " AND A.SEX_CODE=D.ID"+
	 	sql_condition+
	 	"AND B.SDISEASE_CODE IS NOT NULL"+
	 	sql_condition2+
	 	sql_condition3+
	 	" GROUP BY A.MR_NO,A.PAT_NAME,D.CHN_DESC,A.IDNO,C.CTZ_DESC";
	    else if(parm.getValue("HZ_TYPE").equals("3")){//异地
	    	sql = " SELECT A.MR_NO,A.PAT_NAME,D.CHN_DESC,A.IDNO,C.CTZ_DESC,SUM(A.NHI_PAY) AS APPLY_AMT," +
	 	    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+ A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT-"+
	 	    " A.NHI_PAY-A.NHI_COMMENT- A.ARMYAI_AMT-CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END-"+
	 	    " CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS OWN_AMT, "+
	 	    " SUM(A.NHI_PAY) AS OTOT_AMT, SUM(A.NHI_COMMENT) AS NHI_COMMENT, SUM (A.ARMYAI_AMT) AS ARMYAI_AMT,"+
	 	    " SUM(CASE WHEN A.SINGLE_STANDARD_OWN_AMT IS NULL THEN 0 ELSE A.SINGLE_STANDARD_OWN_AMT END) AS SINGLE_STANDARD_OWN_AMT,"+
	 	    " SUM(CASE WHEN A.SINGLE_SUPPLYING_AMT IS NULL THEN 0 ELSE A.SINGLE_SUPPLYING_AMT END) AS SINGLE_SUPPLYING_AMT, "+
	 	    " SUM(CASE WHEN A.ILLNESS_SUBSIDY_AMT IS NULL THEN 0 ELSE A.ILLNESS_SUBSIDY_AMT END) AS ILLNESS_SUBSIDY_AMT,"+ 
	 	    " SUM(CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) AS ACCOUNT_PAY_AMT,"+ 
	 	    " SUM(A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+ A.BLOODALL_AMT+A.BLOOD_AMT+A.OTHER_AMT) AS TOT_AMT"+ 
	 	    " FROM INS_IBS A,INS_ADM_CONFIRM B,SYS_CTZ C,SYS_DICTIONARY D"+
	 	    " WHERE A.UPLOAD_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS')"+
	 	    " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"+
	 	    " AND A.CASE_NO = B.CASE_NO"+
	 	    " AND A.ADM_SEQ = B.ADM_SEQ"+
	 	    " AND B.HIS_CTZ_CODE = C.CTZ_CODE"+
	 	    " AND B.INS_CROWD_TYPE = '3'"+
	        " AND C.NHI_CTZ_FLG = 'Y'"+
	        " AND D.GROUP_ID = 'SYS_SEX'"+
	        " AND A.SEX_CODE=D.ID"+
	 	    sql_condition+
	 	    "AND B.SDISEASE_CODE IS NULL"+
	 	    sql_condition2+
	 	    sql_condition3+
	 	    " GROUP BY A.MR_NO,A.PAT_NAME,D.CHN_DESC,A.IDNO,C.CTZ_DESC";	
	    }
	     
	    else if(parm.getValue("HZ_TYPE").equals("4")){//异地新农合
	    	sql = "SELECT B.MR_NO,A.PAT_NAME,A.SEX_DESC AS CHN_DESC,A.IN_NO AS IDNO,C.CTZ_DESC,SUM(A.OWN_AMT) AS OWN_AMT,"
	    		+ " '0.00' SINGLE_STANDARD_OWN_AMT, '0.00' SINGLE_SUPPLYING_AMT,SUM(A.REAL_INS_AMT) AS OTOT_AMT,"
	    		+ " '0.00' NHI_COMMENT,'0.00' ARMYAI_AMT,'0.00' ILLNESS_SUBSIDY_AMT,'0.00' ACCOUNT_PAY_AMT,SUM(A.TOT_AMT) AS TOT_AMT"
	    		+ " FROM INS_XNH A,ADM_INP B,SYS_CTZ C"
	    		+ " WHERE A.CASE_NO = B.CASE_NO "
	    		+ " AND B.CTZ1_CODE = C.CTZ_CODE"
	    		+ " AND A.SETTLE_DATE BETWEEN TO_DATE('"+ parm.getValue("START_DATE")+ "','YYYYMMDDHH24MISS') "
	    		+ " AND TO_DATE('"+ parm.getValue("END_DATE")+ "','YYYYMMDDHH24MISS')"
	    		+ " AND B.DEPT_CODE ='"+ parm.getValue("DEPT_CODE")+ "'"
	    		+ " GROUP BY B.MR_NO,A.PAT_NAME,A.SEX_DESC,A.IN_NO,C.CTZ_DESC";
	    	System.out.println("sql = "+sql);
	    }
	     
	    TParm data = new TParm(TJDODBTool.getInstance().select(sql));
//	    System.out.println("data=========="+data);  
	    if (data.getErrCode() < 0) {
	 	    this.messageBox("E0116");//没有数据
		    return;
	      }	    
	     ((TTable) getComponent("TABLE")).setParmValue(data);	
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="";
    	String  a ="";
    	String  b ="";
    	String  c ="";
    	if(parm.getValue("ZF_TYPE").equals("1"))
    	    a ="全部";
    	else if (parm.getValue("ZF_TYPE").equals("2"))
    	    a ="未支付";
    	else
    	    a ="已支付";
    	if(parm.getValue("HZ_TYPE").equals("1"))
    	    b="普通医保";	
    	 else if (parm.getValue("HZ_TYPE").equals("2"))
    	    b="单病种";	
    	 else if (parm.getValue("HZ_TYPE").equals("3"))
     	    b="异地";
    	 else
    		b="异地新农合";
        if(parm.getValue("YB_TYPE").equals("1"))
    		c = "城职";
    	else if(parm.getValue("YB_TYPE").equals("2"))
    		c = "城乡";   			 
    		title = "住院"+a+b+c+"病患明细表";
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }   	  
}
