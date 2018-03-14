package com.javahis.ui.inf;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.event.ChangeEvent;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TComboBoxEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.event.TTextFormatEvent;

public class INFClinicalDrugMonitorControl extends TControl{
	private static Timestamp  startDate;
	private static Timestamp endDate;
	private String date;
	private String regionCode;
	private DecimalFormat df=new DecimalFormat("######0.00");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	   /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        Calendar c = Calendar.getInstance();    
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);
        String start = sdf.format(c.getTime());
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String end = sdf.format(c.getTime());
		 endDate = Timestamp.valueOf(end+ " 23:59:59");
		Timestamp startDate = Timestamp.valueOf(start+ " 00:00:00");
		this.setValue("START_DATE", startDate);
		this.setValue("END_DATE", endDate);
		this.setValue("YEAR", c.get(Calendar.YEAR)+"");
	int jd=	c.get(Calendar.MONTH)+1;
	if(jd<=3){
		callFunction("UI|QUARTER_NAME|SetValue", 1);
	}
	if(jd>=3&&jd<=6){
		callFunction("UI|QUARTER_NAME|SetValue", 2);
	}
	if(jd>=7&&jd<=9){
		callFunction("UI|QUARTER_NAME|SetValue", 3);
	}
	if(jd>=10&&jd<=12){
		callFunction("UI|QUARTER_NAME|SetValue", 4);
	}
		this.setValue("REGION_CODE",Operator.getRegion());
		callFunction("UI|QUARTER|SetSelected", false);
		callFunction("UI|QUARTER_NAME|SetEnabled", false);
		callFunction("UI|YEAR|SetEnabled", false);
		callFunction("UI|START_DATE|SetEnabled", true);
		callFunction("UI|END_DATE|SetEnabled", true);
    }
    /**
     * ��ѯ����
     */
    public void onquery(){
    	  TParm resultParm=new TParm();
		   if(((TCheckBox)this.getComponent("QUARTER")).isSelected()){
			   String jd=((TComboBox)this.getComponent("QUARTER_NAME")).getValue();
			   if(jd.equals("1")){
				   jd="��һ����";
			   }
			   if(jd.equals("2")){
				   jd="�ڶ�����";
			   }
			   if(jd.equals("3")){
				   jd="��������";
			   }
			   if(jd.equals("4")){
				   jd="���ļ���";
			   }
		   resultParm.addData("JD",jd);
		   }
		   else{
			   resultParm.addData("JD","");
		   }
    	String startDate= this.getValueString("START_DATE").substring(0, 19);
    	String endDate= this.getValueString("END_DATE").substring(0, 19);
    	 date=" BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd HH24:MI:ss') AND TO_DATE('"+endDate+"','yyyy-MM-dd HH24:MI:ss')";
    	 regionCode=this.getValueString("REGION_CODE");
    	 
    	 //��Ժ��������
    	 String sql="SELECT COUNT(*) OUT_NUM FROM ADM_INP WHERE DS_DATE "+date+ " AND DS_DATE IS NOT NULL AND REGION_CODE='"+regionCode+"'";
    	 TParm outNumParm = new TParm(TJDODBTool.getInstance().select(sql));
    	//this.messageBox("��Ժ��������"+parm.getValue("COUNT",0));
    	
    	//��Ժ����ʹ�ÿ���ҩ������
    	sql=" SELECT count(DISTINCT  A.CASE_NO) OUT_ANT_NUM FROM IBS_ORDD A, PHA_BASE E, ADM_INP J, SYS_REGION K"
    			+ " WHERE A.ORDER_CODE = E.ORDER_CODE AND A.CASE_NO = J.CASE_NO AND J.DS_DATE" +date
                + " AND A.CAT1_TYPE = 'PHA' AND E.ANTIBIOTIC_CODE IS NOT NULL "
                + " AND J.REGION_CODE=K.REGION_CODE AND J.REGION_CODE='"+regionCode+"'";
    	//System.out.println("��Ժ����ʹ�ÿ���ҩ������"+sql);
    	 TParm 	outAntNumParm = new TParm(TJDODBTool.getInstance().select(sql));
    	 //this.messageBox("��Ժ����ʹ�ÿ���ҩ������"+parm02.getValue("COUNT",0));
    	 
    	//��Ժ���߿���ҩ��ʹ����
    	 double outAntRate=0;
    	 if(outNumParm.getDouble("OUT_NUM",0)>0&&outAntNumParm.getDouble("OUT_ANT_NUM",0)>0){
    		 outAntRate=outAntNumParm.getDouble("OUT_ANT_NUM",0)/outNumParm.getDouble("OUT_NUM",0);
    	 }
    	 
    	//��Ժ���߿���ҩ��ʹ���ۼ�DDD
    	sql="SELECT SUM(DDD) SUM_DDD FROM( SELECT  A.CASE_NO, SUM(A.MEDI_QTY*E.DDD) DDD "
    			+ " FROM IBS_ORDD A,PHA_BASE E,ADM_INP J,SYS_REGION K"
    			+ " WHERE A.ORDER_CODE = E.ORDER_CODE  AND A.CASE_NO = J.CASE_NO "
    			+ " AND J.DS_DATE "+date
                + " AND A.CAT1_TYPE = 'PHA' AND E.ANTIBIOTIC_CODE IS NOT NULL "
                + " AND J.REGION_CODE=K.REGION_CODE "
                + " GROUP BY A.ORDER_CODE, A.CASE_NO)";
    	//System.out.println("��Ժ���߿���ҩ��ʹ���ۼ�DDD"+sql);
   	 TParm 	outSumDDDParm = new TParm(TJDODBTool.getInstance().select(sql));
    	//��Ժ����סԺ����
    	  sql="SELECT SUM(DAYS) SUM_DAYS FROM (SELECT TRUNC (DS_DATE - IN_DATE) AS DAYS  FROM ADM_INP WHERE DS_DATE "+date+ " AND DS_DATE IS NOT NULL AND REGION_CODE='"+regionCode+"')";
    	//System.out.println("��Ժ����סԺ����"+sql);
   	 TParm 	outSumDaysParm = new TParm(TJDODBTool.getInstance().select(sql));
     	 
    	//��Ժ����ƽ��סԺ����
   	double outAvgDays=0;
   	 if(outNumParm.getDouble("OUT_NUM",0)>0&&outSumDaysParm.getDouble("SUM_DAYS",0)>0){
     	 outAvgDays=outSumDaysParm.getDouble("SUM_DAYS",0)/outNumParm.getDouble("OUT_NUM",0);
   	 }
     	//סԺ���߿���ҩ��ʹ��ǿ�ȣ�DDD/100���죩	
   	double ddd=0;
   	if(outSumDDDParm.getDouble("SUM_DDD",0)>0&&outNumParm.getDouble("OUT_NUM",0)*outSumDaysParm.getDouble("SUM_DAYS",0)>0){
     	  ddd=outSumDDDParm.getDouble("SUM_DDD",0)/(outNumParm.getDouble("OUT_NUM",0)*outSumDaysParm.getDouble("SUM_DAYS",0));
   	}
     	 //ͬ�����￪�ߴ������˴�    �Һ�ʱ��
     	 sql="  SELECT COUNT (DISTINCT (B.RX_NO)) AS O_PRESCRIPTION_NUM FROM REG_PATADM A,OPD_ORDER B, PHA_BASE C"
     	 		+ " WHERE  A.CASE_NO = B.CASE_NO  AND A.REGCAN_USER IS NULL "
     	 		+ " AND B.ORDER_CODE = C.ORDER_CODE "
     	 		+ " AND B.BILL_FLG = 'Y' AND A.ADM_TYPE='O' "
     	 		+ " AND A.ADM_DATE"+date+" AND A.REGION_CODE='"+regionCode+"'";
     	 //System.out.println("ͬ�����￪�ߴ������˴�"+sql);
    	  TParm oPrescriptionNumParm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	  
    	  //������￪�߿���ҩ�ﴦ���˴�
    	  sql=" SELECT SUM(COUNT (DISTINCT (B.RX_NO)))  O_ANT_PRESCRIPTION_NUM "
    	  		+ "FROM REG_PATADM A, OPD_ORDER B,PHA_BASE C WHERE A.REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO"
    	  		+ " AND B.ORDER_CODE = C.ORDER_CODE  AND A.REGION_CODE = B.REGION_CODE AND B.BILL_FLG = 'Y' AND A.ADM_DATE "+date
    	  		+ "  AND A.ADM_TYPE='O' AND C.ANTIBIOTIC_CODE IS NOT NULL  AND  A.REGION_CODE ='"+regionCode+"' GROUP BY C.ANTIBIOTIC_CODE,B.ADM_TYPE";
    	 //System.out.println("������￪�߿���ҩ�ﴦ���˴�"+sql);
    	  TParm oAntPrescriptionNumParm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	  
    	  // ���ﻼ�߿���ҩ�ﴦ������(%)
    	  double oAntPrescriptionRate=0.00;
    	  if(oPrescriptionNumParm.getDouble("O_PRESCRIPTION_NUM",0)>0&&oAntPrescriptionNumParm.getDouble("O_ANT_PRESCRIPTION_NUM",0)>0){
    		 oAntPrescriptionRate=oAntPrescriptionNumParm.getDouble("O_ANT_PRESCRIPTION_NUM",0)/oPrescriptionNumParm.getDouble("O_PRESCRIPTION_NUM",0);
    	  }

		   	 //ͬ�ڼ��￪�ߴ������˴�    �Һ�ʱ��
	     	 sql="  SELECT COUNT (DISTINCT (B.RX_NO)) AS E_PRESCRIPTION_NUM FROM REG_PATADM A,OPD_ORDER B, PHA_BASE C"
	     	 		+ " WHERE  A.CASE_NO = B.CASE_NO  AND A.REGCAN_USER IS NULL "
	     	 		+ " AND B.ORDER_CODE = C.ORDER_CODE "
	     	 		+ " AND B.BILL_FLG = 'Y' AND A.ADM_TYPE='E' "
	     	 		+ " AND A.ADM_DATE "+date+" AND A.REGION_CODE='"+regionCode+"'";
	     	 //System.out.println("ͬ�ڼ��￪�ߴ������˴�"+sql);
	    	  TParm ePrescriptionNumParm = new TParm(TJDODBTool.getInstance().select(sql));

	    	  
	    	  //������￪�߿���ҩ�ﴦ���˴�
	    	  sql=" SELECT  SUM(COUNT (DISTINCT (B.RX_NO)))  E_ANT_PRESCRIPTION_NUM "
	    	  		+ "FROM REG_PATADM A, OPD_ORDER B,PHA_BASE C WHERE A.REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO"
	    	  		+ " AND B.ORDER_CODE = C.ORDER_CODE  AND A.REGION_CODE = B.REGION_CODE AND B.BILL_FLG = 'Y' AND A.ADM_DATE "+date
	    	  		+ "  AND A.ADM_TYPE='E' AND C.ANTIBIOTIC_CODE IS NOT NULL  AND  A.REGION_CODE ='"+regionCode+"' GROUP BY C.ANTIBIOTIC_CODE,B.ADM_TYPE";
	    	//System.out.println("������￪�߿���ҩ�ﴦ���˴�"+sql);
	    	  TParm eAntPrescriptionNumParm = new TParm(TJDODBTool.getInstance().select(sql));
	    	
	    	  
	    	  // ���ﻼ�߿���ҩ�ﴦ������(%)
	    	  double eAntPrescriptionRate=0;
	    	  if(ePrescriptionNumParm.getDouble("E_PRESCRIPTION_NUM",0)>0&&eAntPrescriptionNumParm.getDouble("E_ANT_PRESCRIPTION_NUM",0)>0){
	    	 eAntPrescriptionRate=eAntPrescriptionNumParm.getDouble("E_ANT_PRESCRIPTION_NUM",0)/ePrescriptionNumParm.getDouble("E_PRESCRIPTION_NUM",0);
	    	  }
	    	//��Ժ����������ʹ�ÿ���ҩ������
		    	sql="SELECT COUNT(*) ANT_TREAT_NUM FROM ( SELECT DISTINCT A.CASE_NO FROM IBS_ORDD A, PHA_BASE E,"
		    			+ " IBS_ORDM F, ADM_INP J,  SYS_REGION,SYS_REGION K,ODI_ORDER L WHERE "
		    			+ " A.CASE_NO=L.CASE_NO AND A.ORDER_NO=L.ORDER_NO "
		    			+ " AND A.ORDER_CODE = E.ORDER_CODE AND A.CASE_NO = F.CASE_NO "
		    			+ " AND A.CASE_NO_SEQ = F.CASE_NO_SEQ AND A.CASE_NO = J.CASE_NO "
		    			+ " AND J.DS_DATE "+date
		    			+ " AND A.CAT1_TYPE = 'PHA' AND E.ANTIBIOTIC_CODE IS NOT NULL "
		    			+ " GROUP BY A.ORDER_CODE, A.CASE_NO, F.MR_NO, A.OWN_PRICE,"
		    			+ " a.ORDER_CODE, K.REGION_CHN_ABN,J.DS_DATE,J.IN_DATE "
		    			+ " ORDER BY A.ORDER_CODE, A.CASE_NO)";
		    	//System.out.println("��Ժ����������ʹ�ÿ���ҩ������"+sql);
		    	 TParm  antTreatNumParm= new TParm(TJDODBTool.getInstance().select(sql));
		    	  
		    	  
		    	  //��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������
		    	 	sql="SELECT COUNT (*) ANT_MIC_TREAT_NUM  FROM ("
		    	 			+ "SELECT A.MR_NO, A.CASE_NO FROM ADM_INP A, ODI_ORDER B,SYS_FEE C,SYS_REGION D,"
		    	 			+ " SYS_STATION G, (  SELECT A.CASE_NO FROM ADM_INP A,ODI_ORDER B,SYS_FEE C,"
		    	 			+ "SYS_REGION Z WHERE A.CASE_NO = B.CASE_NO AND A.REGION_CODE = Z.REGION_CODE"
		    	 			+ " AND B.ORDER_CODE = C.ORDER_CODE AND B.ANTIBIOTIC_WAY = '02'"
		    	 			+ " AND B.ANTIBIOTIC_CODE IS NOT NULL AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"+regionCode+"'"
		    	 			+ " AND A.DS_DATE "+date
                            +" AND A.DS_DATE IS NOT NULL GROUP BY A.CASE_NO) S WHERE "
                            + " A.CASE_NO = S.CASE_NO AND A.CASE_NO = B.CASE_NO "
                            + " AND B.ORDER_CODE = C.ORDER_CODE AND A.REGION_CODE = D.REGION_CODE"
                            + " AND A.STATION_CODE = G.STATION_CODE AND C.ORD_SUPERVISION = '01' "
                            + "GROUP BY A.MR_NO,A.DS_DATE, A.REGION_CODE,D.REGION_CHN_ABN,"
                            + " A.DEPT_CODE, A.VS_DR_CODE, A.CASE_NO,A.IN_DATE, A.IPD_NO,"
                            + " G.STATION_DESC, C.ORDER_CODE, C.ORDER_DESC ORDER BY A.REGION_CODE,"
                            + " A.DEPT_CODE,A.VS_DR_CODE,A.CASE_NO)";
		    	 	//System.out.println("��Ժ���߿���ҩ������ʹ��ǰ΢�����ͼ�������"+sql);
			    	 TParm antMicTreatNumParm = new TParm(TJDODBTool.getInstance().select(sql));
			    	  
		    		//סԺ���߿���ҩ������ʹ��ǰ΢�����ͼ���(%)
			    	 double antTreatMicRate=0;
			    	 if(antMicTreatNumParm.getDouble("ANT_MIC_TREAT_NUM",0)>0&&antTreatNumParm.getDouble("ANT_TREAT_NUM",0)>0){
			    	  antTreatMicRate=antMicTreatNumParm.getDouble("ANT_MIC_TREAT_NUM",0)/antTreatNumParm.getDouble("ANT_TREAT_NUM",0);
			    	 }
		    	 	
			    	  //��Ժ����������ʹ�����Ƽ�����ҩ������
			    	  sql=" SELECT COUNT( DISTINCT A.CASE_NO) LIMIT_ANT_NUM "
			    	  		+ " FROM ADM_INP A,ODI_ORDER B, SYS_FEE C,SYS_DEPT D,SYS_OPERATOR E,SYS_REGION Z"
			    	  		+ " WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE "
			    	  		+ " AND A.REGION_CODE = Z.REGION_CODE AND A.VS_DR_CODE = E.USER_ID "
			    	  		+ " AND B.ORDER_CODE = C.ORDER_CODE AND B.ANTIBIOTIC_WAY = '02'"
			    	  		+ " AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"+regionCode+"'"
			    	  		+ " AND A.DS_DATE "+date
			    	  		+ " AND B.ANTIBIOTIC_CODE = '02'AND A.DS_DATE IS NOT NULL ";
			    	//System.out.println("��Ժ����������ʹ�����Ƽ�����ҩ������"+sql);
			    	 TParm limitAntNumParm= new TParm(TJDODBTool.getInstance().select(sql));
			    	  
			    	
			    	  //��Ժ����������ʹ�����Ƽ�����ҩ��ǰ΢�����ͼ�����
			    	  sql="  SELECT  COUNT(DISTINCT A.CASE_NO) LIMIT_ANT_MIC_NUM FROM ADM_INP A, ODI_ORDER B, SYS_FEE C,"
			    	  		+ " (  SELECT A.CASE_NO FROM ADM_INP A, ODI_ORDER B,"
			    	  		+ " SYS_REGION Z WHERE A.CASE_NO = B.CASE_NO "
			    	  		+ "  AND B.ANTIBIOTIC_WAY = '02' "
			    	  		+ " AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"+regionCode+"' "
			    	  		+ " AND A.DS_DATE "+date
                            +" AND B.ANTIBIOTIC_CODE = '02'AND A.DS_DATE IS NOT NULL GROUP BY A.CASE_NO) S"
                            + " WHERE     A.CASE_NO = S.CASE_NO AND A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE"
                            + " AND C.ORD_SUPERVISION = '01' ";
			    	  //System.out.println("��Ժ����������ʹ�����Ƽ�����ҩ��ǰ΢�����ͼ� ����"+sql);
			    	   TParm limitAntMicNumParm= new TParm(TJDODBTool.getInstance().select(sql));
				    	
				    	//סԺ�������Ƽ�����ҩ������ʹ��ǰ΢�����ͼ���(%)
			    	   double limitMicRate=0;
			    	   if(limitAntMicNumParm.getDouble("LIMIT_ANT_MIC_NUM",0)>0&&limitAntNumParm.getDouble("LIMIT_ANT_NUM",0)>0){
				    	limitMicRate=limitAntMicNumParm.getDouble("LIMIT_ANT_MIC_NUM",0)/limitAntNumParm.getDouble("LIMIT_ANT_NUM",0);
			    	   }
				    	//��Ժ����������ʹ�����⼶����ҩ������
			    	  sql="  SELECT COUNT(DISTINCT A.CASE_NO) SPE_ANT_NUM "
			    	  		+ " FROM ADM_INP A,ODI_ORDER B,"
			    	  		+ " SYS_REGION Z WHERE A.CASE_NO = B.CASE_NO  AND A.REGION_CODE = Z.REGION_CODE"
			    	  		+ " AND B.ANTIBIOTIC_WAY = '02' AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"+regionCode+"' "
			    	  		+ " AND A.DS_DATE" +date+" AND B.ANTIBIOTIC_CODE = '03' AND A.DS_DATE IS NOT NULL ";
			    	//System.out.println("��Ժ����������ʹ�����⼶����ҩ������"+sql);
			    	   TParm speAntNumParm= new TParm(TJDODBTool.getInstance().select(sql));
			    	
			    	
			    	  //��Ժ����������ʹ�����⼶����ҩ��ǰ΢�����ͼ�����
			    	  sql=" SELECT COUNT(DISTINCT A.CASE_NO) SPE_MIC_NUM FROM ADM_INP A, ODI_ORDER B,SYS_FEE C,"
			    	  		+ " (SELECT A.CASE_NO FROM ADM_INP A, ODI_ORDER B,SYS_REGION Z "
			    	  		+ " WHERE A.CASE_NO = B.CASE_NO  AND A.REGION_CODE = Z.REGION_CODE "
			    	  		+ " AND B.ANTIBIOTIC_WAY = '02' AND A.CANCEL_FLG <> 'Y' "
			    	  		+ " AND Z.REGION_CODE = '"+regionCode+"' AND A.DS_DATE "+date
                            + " AND B.ANTIBIOTIC_CODE = '03' AND A.DS_DATE IS NOT NULL GROUP BY A.CASE_NO) S"
                            + " WHERE A.CASE_NO = S.CASE_NO AND A.CASE_NO = B.CASE_NO"
                            + " AND B.ORDER_CODE = C.ORDER_CODE AND C.ORD_SUPERVISION = '01' ";
			    	  //System.out.println("��Ժ����������ʹ�����⼶����ҩ��ǰ΢�����ͼ�����"+sql);
			    	 TParm speMicNumParm= new TParm(TJDODBTool.getInstance().select(sql));
			    	  
			    	  //סԺ�������⼶����ҩ������ʹ��ǰ΢�����ͼ���(%)
			    	 double speMicRate=0;
			    	 if(speAntNumParm.getDouble("SPE_ANT_NUM",0)>0&&speMicNumParm.getDouble("SPE_MIC_NUM",0)>0){
			    	 speMicRate=speMicNumParm.getDouble("SPE_MIC_NUM",0)/speAntNumParm.getDouble("SPE_ANT_NUM",0);
			    	 }		 
			    	  
			    	  
			    	  //���п���������
						/*String caseNo=" SELECT CASE_NO FROM MRO_RECORD A WHERE A.OUT_DATE "+date;
						 sql="SELECT COUNT(CASE_NO) FROM MRO_RECORD   WHERE OUT_DATE "+date
							 		+ "  AND (CASE_NO IN ("+caseNo+"))  AND OP_CODE IS NOT NULL   AND HEAL_LV IN ('11','12','13','14')  ";*/
			    	  sql="SELECT COUNT(A.OPBOOK_SEQ) OPE_NUM FROM OPE_OPBOOK A, SYS_OPERATIONICD B WHERE A.OP_DATE "+date
                             +" AND (A.OP_CODE1=B.OPERATION_ICD  OR A.OP_CODE2=B.OPERATION_ICD)"
                             + " AND B.PHA_PREVENCODE='001'	 ";
						 //System.out.println("I���п���������"+sql);
						 TParm opeNumParm=new TParm(TJDODBTool.getInstance().select(sql));
						    
						 //I���п���������ҩ��Ԥ��ʹ������
						/* caseNo="SELECT CASE_NO FROM MRO_RECORD A WHERE A.OUT_DATE "+date+" AND A.OP_CODE IS NOT NULL   AND A.HEAL_LV IN ('11','12','13','14')  AND A.CHARGE_16 > 0 ";
						    sql="SELECT COUNT(CASE_NO) FROM MRO_RECORD  "
						    		+ " WHERE OUT_DATE "+date+" AND (CASE_NO IN ("+caseNo+"))  AND OP_CODE IS NOT NULL  "
						    		+ " AND HEAL_LV IN ('11','12','13','14')  AND CHARGE_16 > 0";*/
						 sql="SELECT COUNT(DISTINCT A.OPBOOK_SEQ) OPE_ANT_NUM FROM OPE_OPBOOK A, SYS_OPERATIONICD B,ODI_ORDER C WHERE A.OP_DATE "+date
	                             +" AND (A.OP_CODE1=B.OPERATION_ICD  OR A.OP_CODE2=B.OPERATION_ICD)"
	                             + " AND A.CASE_NO=C.CASE_NO AND C.ANTIBIOTIC_WAY = '01'"
	                             + " AND B.PHA_PREVENCODE='001'	 ";
						    //System.out.println("I���п���������ҩ��Ԥ��ʹ������"+sql);
							 TParm opeAntNumParm=new TParm(TJDODBTool.getInstance().select(sql));

					 //I���п���������ҩ��Ԥ��ʹ����(%):
					double opeAntRate=0;
					 if(opeAntNumParm.getDouble("OPE_ANT_NUM",0)>0&&opeNumParm.getDouble("OPE_NUM",0)>0){
					opeAntRate=opeAntNumParm.getDouble("OPE_ANT_NUM",0)/opeNumParm.getDouble("OPE_NUM",0);	    
							 }
		 //I���п���������Ԥ������ҩ��ʹ��ʱ������ǰ0.5-2Сʱ���ϸ�����
	/*	 caseNo="SELECT CASE_NO FROM MRO_RECORD A WHERE A.OUT_DATE "+date+" AND A.OP_CODE IS NOT NULL   AND A.HEAL_LV IN ('11','12','13','14')  AND A.CHARGE_16 > 0 ";
		 sql="SELECT COUNT(CASE_NO) FROM MRO_RECORD  "
			+ " WHERE OUT_DATE "+date+" AND (CASE_NO IN ("+caseNo+"))  AND OP_CODE IS NOT NULL  "
			+ " AND HEAL_LV IN ('11','12','13','14')  AND CHARGE_16 > 0";*/
			  sql="SELECT COUNT(DISTINCT A.OPBOOK_SEQ) OPE_OPP_NUM FROM OPE_OPBOOK A, SYS_OPERATIONICD B,ODI_ORDER C WHERE A.OP_DATE "+date
		                             +" AND (A.OP_CODE1=B.OPERATION_ICD  OR A.OP_CODE2=B.OPERATION_ICD)"
		                             + " AND A.CASE_NO=C.CASE_NO AND C.ANTIBIOTIC_WAY = '01'"
		                             + " AND B.PHA_PREVENCODE='001'	AND C.DC_DATE IS NOT NULL"
		                             + " AND A.OP_DATE-C.DC_DATE >0.5 AND A.OP_DATE-DC_DATE<2";			    
		   //System.out.println("I���п���������Ԥ������ҩ��ʹ��ʱ������ǰ0.5-2Сʱ���ϸ�����"+sql);
		   TParm opeOppNumParm=new TParm(TJDODBTool.getInstance().select(sql));
			
		   
		   //I���п���������Ԥ������ҩ��ʹ��ʱ������ǰ0.5-2Сʱ��T�ϸ��ʣ�%��
		   double opeOppRate=0;
		   if(opeOppNumParm.getDouble("OPE_OPP_NUM",0)>0&&opeAntNumParm.getDouble("OPE_ANT_NUM",0)>0){
		   opeOppRate=opeOppNumParm.getDouble("OPE_OPP_NUM",0)/opeAntNumParm.getDouble("OPE_ANT_NUM",0);
		   }
		 //I���п���������Ԥ������ҩ��ʹ��ʱ�̣�24Сʱ�ڣ��ϸ�����	 
			  sql="SELECT COUNT(DISTINCT A.OPBOOK_NO) OPE_APD_NUM FROM OPE_OPDETAIL A, SYS_OPERATIONICD B,ODI_ORDER C WHERE A.OP_DATE "+date
                      +" AND (A.OP_CODE1=B.OPERATION_ICD  OR A.OP_CODE2=B.OPERATION_ICD)"
                      + " AND A.CASE_NO=C.CASE_NO AND C.ANTIBIOTIC_WAY = '01'"
                      + " AND B.PHA_PREVENCODE='001' AND C.ORDER_DATE-A.OP_END_DATE <24"
                      + " AND C.RX_KIND='UD' ";	
		   //System.out.println("I���п���������Ԥ������ҩ��ʹ��ʱ�̣�24Сʱ�ڣ��ϸ�����	"+sql);
		   TParm opeApdNumParm=new TParm(TJDODBTool.getInstance().select(sql));
		   
		   // I���п���������Ԥ������ҩ��ʹ��ʱ�̣�24Сʱ�ڣ��ϸ���(%)
		   double opeApdRate=0;
		   if(opeApdNumParm.getDouble("OPE_APD_NUM",0)>0&&opeAntNumParm.getDouble("OPE_ANT_NUM",0)>0){
		   opeApdRate=opeApdNumParm.getDouble("OPE_APD_NUM",0)/opeAntNumParm.getDouble("OPE_ANT_NUM",0);
		   }
		   
		   
		   //ԭ���ϲ�ӦԤ��ʹ�ÿ���ҩ���I���п�������������
		 

		 //  ԭ���ϲ�ӦԤ��ʹ�ÿ���ҩ���I���п�������ʹ�ÿ���ҩ������

		   //ԭ���ϲ�ӦԤ��ʹ�ÿ���ҩ���I���п���������ҩ��Ԥ��ʹ���ʣ�%��
		   
		   
		   
		   
		   //���������������
		   sql="SELECT COUNT (DISTINCT OPBOOK_NO) OPE_STEP_NUM  FROM OPE_OPDETAIL WHERE OP_DATE "+date
                                  +" AND TYPE_CODE='1'";
		   //System.out.println("���������������"+sql);
		   TParm opeStepNumParm=new TParm(TJDODBTool.getInstance().select(sql));
		   
		   //�����������Ԥ������ҩ��ʹ������
		   sql="SELECT COUNT (DISTINCT A.OPBOOK_NO) OPE_ANT_STEP_NUM FROM OPE_OPDETAIL A, ODI_ORDER C  WHERE A.OP_DATE "+date
                 +" AND A.CASE_NO = C.CASE_NO AND C.ANTIBIOTIC_WAY = '01'";
		   //System.out.println("�����������Ԥ������ҩ��ʹ������"+sql);
		   TParm opeAntStepNumParm=new TParm(TJDODBTool.getInstance().select(sql));
		   
		   //���������������ҩ��Ԥ��ʹ����(%)
		   double opeStepRate=0;
		   if(opeAntStepNumParm.getDouble("OPE_ANT_STEP_NUM",0)>0&&opeStepNumParm.getDouble("OPE_STEP_NUM",0)>0){
		   opeStepRate=opeAntStepNumParm.getDouble("OPE_ANT_STEP_NUM",0)/opeStepNumParm.getDouble("OPE_STEP_NUM",0);
		   }
		   resultParm.addData("OUT_NUM",outNumParm.getValue("OUT_NUM",0));
		   resultParm.addData("OUT_ANT_NUM",outAntNumParm.getValue("OUT_ANT_NUM",0));
		   resultParm.addData("OUT_ANT_RATE",outAntRate==0?"0%":df.format(outAntRate*100)+"%");
		   resultParm.addData("SUM_DDD",outSumDDDParm.getValue("SUM_DDD",0));
		   resultParm.addData("SUM_DAYS",outSumDaysParm.getValue("SUM_DAYS",0));
		   resultParm.addData("OUT_AVG_DAYS",outAvgDays==0?"0":df.format(outAvgDays));
		   resultParm.addData("AVG_DDD",ddd==0?"0":df.format(ddd));
		   resultParm.addData("O_PRESCRIPTION_NUM",oPrescriptionNumParm.getValue("O_PRESCRIPTION_NUM",0));
		   resultParm.addData("O_ANT_PARESCRIPTION_NUM",oAntPrescriptionNumParm.getValue("O_ANT_PRESCRIPTION_NUM",0));
		   resultParm.addData("O_ANT_PRESCRIPTION_RATE",oAntPrescriptionRate==0?"0%":df.format(oAntPrescriptionRate*100)+"%");
		   resultParm.addData("E_PRESCRIPTION_NUM",ePrescriptionNumParm.getValue("E_PRESCRIPTION_NUM",0));
		   resultParm.addData("E_ANT_PRESCRIPTION_NUM",eAntPrescriptionNumParm.getValue("E_ANT_PRESCRIPTION_NUM",0));
		   resultParm.addData("E_ANT_PRESCRIPTION_RATE",eAntPrescriptionRate==0?"0%":df.format(eAntPrescriptionRate*100)+"%");
		   resultParm.addData("ANT_TREAT_NUM",antTreatNumParm.getValue("ANT_TREAT_NUM",0));
		   resultParm.addData("ANT_MIC_TREAT_NUM",antMicTreatNumParm.getValue("ANT_MIC_TREAT_NUM",0));
		   resultParm.addData("ANT_TREAT_MIC_RATE",antTreatMicRate==0?"0%":df.format(antTreatMicRate*100)+"%");
		   resultParm.addData("LIMIT_ANT_NUM",limitAntNumParm.getValue("LIMIT_ANT_NUM",0));
		   resultParm.addData("LIMIT_ANT_MIC_NUM",limitAntMicNumParm.getValue("LIMIT_ANT_MIC_NUM",0));
		   resultParm.addData("LIMIT_MIC_RATE",limitMicRate==0?"0%":df.format(limitMicRate*100)+"%");
		   resultParm.addData("SPE_ANT_NUM",speAntNumParm.getValue("SPE_ANT_NUM",0));
		   resultParm.addData("SPE_MIC_NUM",speMicNumParm.getValue("SPE_MIC_NUM",0));
		   resultParm.addData("SPE_MIC_RATE",speMicRate==0?"0%":df.format(speMicRate*100)+"%");
		   resultParm.addData("OPE_NUM",opeNumParm.getValue("OPE_NUM",0));
		   resultParm.addData("OPE_ANT_NUM",opeAntNumParm.getValue("OPE_ANT_NUM",0));
		   resultParm.addData("OPE_ANT_RATE",opeAntRate==0?"0%":df.format(opeAntRate*100)+"%");
		   resultParm.addData("OPE_OPP_NUM",opeOppNumParm.getValue("OPE_OPP_NUM",0));
		   resultParm.addData("OPE_OPP_RATE",opeOppRate==0?"0%":df.format(opeOppRate*100)+"%");
		   resultParm.addData("OPE_APD_NUM",opeApdNumParm.getValue("OPE_APD_NUM",0));
		   resultParm.addData("OPE_APD_RATE",opeApdRate==0?"0%":df.format(opeApdRate*100)+"%");
		   resultParm.addData("NO_ANT_OPE",0);
		   resultParm.addData("NO_ANT_OPE_Y",0);
		   resultParm.addData("NO_ANT_OPE_RATE","0%");
		   resultParm.addData("OPE_STEP_NUM",opeStepNumParm.getValue("OPE_STEP_NUM",0));
		   resultParm.addData("OPE_ANT_STEP_NUM",opeAntStepNumParm.getValue("OPE_ANT_STEP_NUM",0));
		   resultParm.addData("OPE_STEP_RATE",opeStepRate==0?"0%":df.format(opeStepRate*100)+"%");
		   resultParm.setCount(1);
		  ((TTable) this.getComponent("TABLE")).setParmValue(resultParm);
    }
    
    	public void  onClear(){
	 onInit();
	 ((TTable) this.getComponent("TABLE")).removeRowAll();
 }
    	
    	/**
    	 * ��ѯ��ʽ�ı�
    	 */
    	public void onChangeSelectType() {
    		if("Y".equals(this.getValue("QUARTER"))){
    			callFunction("UI|START_DATE|SetEnabled", false);
    			callFunction("UI|END_DATE|SetEnabled", false);
    			callFunction("UI|QUARTER_NAME|SetEnabled", true);
    			callFunction("UI|YEAR|SetEnabled", true);
    		}else{
    			callFunction("UI|START_DATE|SetEnabled", true);
    			callFunction("UI|END_DATE|SetEnabled", true);
    			callFunction("UI|QUARTER_NAME|SetEnabled", false);
    			callFunction("UI|YEAR|SetEnabled", false);
    		}
    		onDateChange();
    	}

        
        /** 
         * ���ȸı��¼�
         * 
         * @return 
         */  
      public void onDateChange(){
    	 String year= this.getValueString("YEAR").substring(0,4);
    	  if("1".equals(this.getValue("QUARTER_NAME"))){
    		  startDate=Timestamp.valueOf(year+"-01-01 00:00:00");
    		  endDate=Timestamp.valueOf(year+"-03-31 23:59:59");
    	  }
    	  if("2".equals(this.getValue("QUARTER_NAME"))){
    		  startDate=Timestamp.valueOf(year+"-04-01 00:00:00");
    		  endDate=Timestamp.valueOf(year+"-06-30 23:59:59");
    	  }
    	  if("3".equals(this.getValue("QUARTER_NAME"))){
    		  startDate=Timestamp.valueOf(year+"-07-01 00:00:00");
    		  endDate=Timestamp.valueOf(year+"-09-30 23:59:59");
    	  }
    	  if("4".equals(this.getValue("QUARTER_NAME"))){
    		  startDate=Timestamp.valueOf(year+"-10-01 00:00:00");
    		  endDate=Timestamp.valueOf(year+"-12-31 23:59:59");
    	  }
    	  this.setValue("START_DATE", startDate);
    	  this.setValue("END_DATE", endDate);
      }
    	
    	/**
    	 * ����Excel
    	 */
    	public void onExecl() {
    		TTable table = (TTable) this.getComponent("TABLE");
    		if(table.getParmValue().getCount() <= 0) {
    			this.messageBox_("���޵���Excel����");
    			return;
    		}
    		INFClinicaDurgMonitorUtil.getInstance().exportExcel(table, "����ҩ���ٴ�Ӧ�ü�������ܱ�");
    		
    	}
}
