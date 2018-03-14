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
 * Title: ҽ��֧����Ŀ��ѯ
 * Description:ҽ��֧����Ŀ��ѯ
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
     * ��ʼ������
     */
    public void getData() { 	
    	//��ʼ����
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());
    	//��������
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	//��ʼ��
    	this.callFunction("UI|TABLE|setParmValue", new TParm());
        this.setValue("QUERY_TYPE","1");
    	if(this.getValue("QUERY_TYPE").equals("1")){
    	callFunction("UI|YB_TYPE|setEnabled", true); //ҽ����ݲ��ɱ༭      	
    	combobox.setStringData("[[id,name],[,],[1,��ְ����],[2,��ְ����],[3,��������]]");
        callFunction("UI|HZ_TYPE|setEnabled", false); //������𲻿ɱ༭ 
        callFunction("UI|ZF_TYPE|setEnabled", false); //֧����𲻿ɱ༭ 
    	clearValue("HZ_TYPE;ZF_TYPE;YB_TYPE");
        this.table.setHeader("����,140;�˴�,100;�Էѽ��,140,double,#########0.00;" +
        		 "����֧�����,140,double,#########0.00;�������,140,double,#########0.00;" +
        		 "����󲡽��,140,double,#########0.00;" +
        		 "�����˻����,140,double,#########0.00;�ܷ��ý��,140,double,#########0.00") ;
        this.table.setLockColumns("0,1,2,3,4,5,6,7");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                                                    "4,right;5,right;6,right;7,right");
		this.table.setParmMap("DEPT_DESC;APPLY_COUNT;OWN_AMT;OTOT_AMT;" +
				              "ARMY_AI_AMT;ILLNESS_SUBSIDY_AMT;ACCOUNT_PAY_AMT;TOT_AMT");
    	}
    }
    /**
	 * ���ݼ��
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("��ʼ���ڲ���Ϊ��");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("�������ڲ���Ϊ��");
    		return true;
    	}
    	if(this.getValue("QUERY_TYPE").equals("")){
    		this.messageBox("��ѯ�����Ϊ��");
    		return true;
    	}
    	if(this.getValue("QUERY_TYPE").equals("2")){
    		 if(this.getValue("HZ_TYPE").equals("")){
     	    	this.messageBox("���������Ϊ��");
     	    	return true;
     	    	}
    	    if(this.getValue("ZF_TYPE").equals("")){
    	    	this.messageBox("֧�������Ϊ��");
    	    	return true;
    	    	}
    	}
	    return false; 
	}
    /**
     * ��ѯ
     */
    public void onQuery() {
    	//���ݼ��
    	if(checkdata())
		    return;   		
    	 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "START_DATE")), "yyyyMMdd")+"000000"; //��ʼ����
    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "END_DATE")), "yyyyMMdd")+"235959"; //��������
    	 TParm parm = new TParm();
    	 parm.setData("START_DATE", startdate);
    	 parm.setData("END_DATE", enddate);
	
	//�����ѯ
	if(this.getValue("QUERY_TYPE").equals("1")){
		String sql ="";
		 //ҽ�����ѡ��
	       if(this.getValue("YB_TYPE").equals("1"))
 	          sql =" AND A.INS_CROWD_TYPE ='1'" +
                   " AND A.INS_PAT_TYPE = '1'";//��ְ����
          else if(this.getValue("YB_TYPE").equals("2"))
 	          sql =" AND A.INS_CROWD_TYPE ='1'" +
                   " AND A.INS_PAT_TYPE = '2'";//��ְ����
 	      else if(this.getValue("YB_TYPE").equals("3"))
 	    	  sql =" AND A.INS_CROWD_TYPE ='2'" +
                   " AND A.INS_PAT_TYPE = '2'";//��������
	       
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
			this.messageBox("E0116");//û������
			return;
		}
		int count  = data3.getCount();
		double applycount =0;//�����˴κϼ�
	    double ownamt =0;//�Էѽ��ϼ�
	    double ototamt =0;//����֧�����ϼ�
	    double armyaiamt =0;//�������ϼ�
	    double illnesssubsidyamt=0;//����󲡽��ϼ�
	    double accountpayamt=0;//�����˻����
	    double totamt =0;//�ܷ��ý��
		for (int i = 0; i < count; i++) {
			applycount+=StringTool.round(data3.getDouble("APPLY_COUNT", i),0);
			ownamt+=StringTool.round(data3.getDouble("OWN_AMT", i),2);
		    ototamt+=StringTool.round(data3.getDouble("OTOT_AMT", i),2);
		    armyaiamt+=StringTool.round(data3.getDouble("ARMY_AI_AMT", i),2);
		    illnesssubsidyamt+=StringTool.round(data3.getDouble("ILLNESS_SUBSIDY_AMT", i),2);
		    accountpayamt+=StringTool.round(data3.getDouble("ACCOUNT_PAY_AMT", i),2);
		    totamt+=StringTool.round(data3.getDouble("TOT_AMT", i),2);
		}
		data3.addData("DEPT_DESC", "�ϼ�") ;
		data3.addData("APPLY_COUNT",df1.format(applycount));
	    data3.addData("OWN_AMT",StringTool.round(ownamt,2));
	    data3.addData("OTOT_AMT",StringTool.round(ototamt,2));
	    data3.addData("ARMY_AI_AMT",StringTool.round(armyaiamt,2));
	    data3.addData("ILLNESS_SUBSIDY_AMT",StringTool.round(illnesssubsidyamt,2));
	    data3.addData("ACCOUNT_PAY_AMT",StringTool.round(accountpayamt,2));
	    data3.addData("TOT_AMT",StringTool.round(totamt,2));
		((TTable) getComponent("TABLE")).setParmValue(data3);		
	}
	   //סԺ��ѯ
	    if(this.getValue("QUERY_TYPE").equals("2")){
	    	TParm data4 = new TParm();
	       String sql_condition ="";
	       String sql_condition2 ="";
	       //ҽ�����ѡ��
	       if(this.getValue("YB_TYPE").equals("1"))
    	   sql_condition2 =" AND D.INS_CROWD_TYPE ='1'";//��ְ
           else if(this.getValue("YB_TYPE").equals("2"))
    	   sql_condition2 =" AND D.INS_CROWD_TYPE ='2'";//����
	       //֧�����ѡ��
	       if(this.getValue("ZF_TYPE").equals("1"))
	       sql_condition =" AND B.IN_STATUS IN( '2','3','4')";//ȫ��
	       else if(this.getValue("ZF_TYPE").equals("2"))
	       sql_condition =" AND B.IN_STATUS IN( '2','3')";//δ֧��
	       else
	       sql_condition =" AND B.IN_STATUS = '4'";//��֧�� 
	       //�������ѡ��
	       if(this.getValue("HZ_TYPE").equals("1")){//��ͨҽ��
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
	       //������
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
	       else if(this.getValue("HZ_TYPE").equals("3")){//���
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
	       else if(this.getValue("HZ_TYPE").equals("4")){//�����ũ��
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
		 	    this.messageBox("E0116");//û������
			    return;
		      }     
		    int count  = data4.getCount();
		    double applycount =0;//�����˴κϼ�
		    double ownamt =0;//ʵ���Ը����ϼ�
		    double singlestandardownamt =0;//ҽԺ�������𸶱�׼�Ը����
			double singlesupplyingamt =0;//����ҽ�Ʊ��ղ�����
		    double ototamt =0;//����֧�����ϼ�
		    double nhicomment =0;//���������ϼ�
		    double armyaiamt =0;//�������ϼ�
		    double illnesssubsidyamt=0;//����󲡽��ϼ� 
		    double accountpayamt=0;//�����˻����
		    double totamt =0;//�ܷ��ý��		    
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
		    data4.addData("DEPT_CHN_DESC", "�ϼ�") ;
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
     * ���
     */
    public void onExport() {
    	String title ="";
    	String  a ="";
    	String  b ="";
    	String  c ="";
    	String  d ="";
    	if(this.getValue("QUERY_TYPE").equals("1")){
    		if(this.getValue("YB_TYPE").equals("1"))
        		d = "��ְ����";
           else if(this.getValue("YB_TYPE").equals("2"))
        		d = "��ְ����"; 
           else if(this.getValue("YB_TYPE").equals("3"))
        	    d = "��������"; 
    		title = "����"+d+"ͳ�Ʋ�ѯ��";
    	}
    	if(this.getValue("QUERY_TYPE").equals("2")){
    		if(this.getValue("ZF_TYPE").equals("1"))
    		a ="ȫ��";
    		else if (this.getValue("ZF_TYPE").equals("2"))
    		a ="δ֧��";
    		else
    		a ="��֧��";
    	    if(this.getValue("HZ_TYPE").equals("1"))
    	    b="��ͨҽ��";	
    	    else if (this.getValue("HZ_TYPE").equals("2"))
    	    b="������";
    	    else if (this.getValue("HZ_TYPE").equals("3"))
        	b="���";
    	    else
    	    b="�����ũ��";		
    		if(this.getValue("YB_TYPE").equals("1"))
    		c = "��ְ";
    		else if(this.getValue("YB_TYPE").equals("2"))
    		c = "����";   			 
    		title = "סԺ"+a+b+c+"��ѯ��";
    	}
    	if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);			
    }
    	
    /**
     * ���
     */
    public void onClear() {
    	 getData();//��ʼ������     
    }
    /**
     * ��ò�����ϸ
     */
    public void onSelect() {
    	if(this.getValue("QUERY_TYPE").equals("2")){
    	int Row = table.getSelectedRow();//����
    	TParm data = table.getParmValue().getRow(Row);//�������
        String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "START_DATE")), "yyyyMMdd")+"000000"; //��ʼ����
   	     String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
        "END_DATE")), "yyyyMMdd")+"235959"; //��������
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
     * ��ѯ���ѡ���¼�
     */
    public void querytype(){
     if (this.getValue("QUERY_TYPE").equals("1")){//����
        callFunction("UI|YB_TYPE|setEnabled", true); //ҽ����ݲ��ɱ༭
    	combobox.setStringData("[[id,name],[,],[1,��ְ����],[2,��ְ����],[3,��������]]");
        callFunction("UI|HZ_TYPE|setEnabled", false); //������𲻿ɱ༭
        callFunction("UI|ZF_TYPE|setEnabled", false); //֧����𲻿ɱ༭ 
    	clearValue("HZ_TYPE;ZF_TYPE;YB_TYPE");
        this.table.setHeader("����,140;�˴�,100;�Էѽ��,140,double,#########0.00;" +
              		 "����֧�����,140,double,#########0.00;�������,140,double,#########0.00;" +
              		 "����󲡽��,140,double,#########0.00;" +
              		 "�����˻����,140,double,#########0.00;�ܷ��ý��,140,double,#########0.00") ;
        this.table.setLockColumns("0,1,2,3,4,5,6,7");
        this.table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;" +
                                                    "4,right;5,right;6,right;7,right");
      	this.table.setParmMap("DEPT_DESC;APPLY_COUNT;OWN_AMT;OTOT_AMT;" +
      				          "ARMY_AI_AMT;ILLNESS_SUBSIDY_AMT;ACCOUNT_PAY_AMT;TOT_AMT");  
     }else if(this.getValue("QUERY_TYPE").equals("2")){//סԺ
       	    callFunction("UI|HZ_TYPE|setEnabled", true); //�������ɱ༭ 
       	    callFunction("UI|YB_TYPE|setEnabled", true); //ҽ����ݿɱ༭
       		combobox.setStringData("[[id,name],[,],[1,��ְ],[2,����]]");
       	    callFunction("UI|ZF_TYPE|setEnabled", true); //֧�����ɱ༭
           this.setValue("HZ_TYPE","1");//��ͨҽ��
           this.setValue("YB_TYPE","");//Ĭ��ֵ
           this.setValue("ZF_TYPE","1");//ȫ��
           this.table.setHeader("����,140;�˴�,100;" +
        		 "ʵ���Ը����,140,double,#########0.00;" +
        		 "�������𸶱�׼�Ը����,200,double,#########0.00;" +
  	    	     "����ҽ�Ʊ��ղ�����,200,double,#########0.00;" +
        		 "����֧�����,140,double,#########0.00;" +
  	             "���������,140,double,#########0.00;" +
  	             "�������,140,double,#########0.00;" +
  	             "����󲡽��,140,double,#########0.00;" +
  	             "�����˻����,140,double,#########0.00;" +
  	             "�ܷ��ý��,140,double,#########0.00");
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
