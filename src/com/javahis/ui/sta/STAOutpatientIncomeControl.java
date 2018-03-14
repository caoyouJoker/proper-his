package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;
import java.text.NumberFormat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
//import com.util.StringUtil;

/**
* <p>Title: ����������ܱ�</p>
*
* <p>Description:����������ܱ� </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company: </p>
*
* @author zhangs 20140920
* @version 1.0
*/


public class STAOutpatientIncomeControl extends TControl {
//	private static final String TParm = null;
	private static TTable table;
	private static TComboBox comboBox;
	private static TCheckBox checkBox;
	private static TComboBox ctzCode;
//	private static TTextFormat START_DATE;
//	private static TTextFormat END_DATE;
//	private  String where="";
//	private  String and="";
//	String whereSql="";
	String andSql="";
	String mroCTZ="";
	java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
	
	public void onInit(){
		 table = (TTable) getComponent("TABLE"); 
		 checkBox=(TCheckBox)getComponent("TCheckBox");
		 comboBox=(TComboBox)getComponent("tTableName");
		 comboBox.setStringData("[[id,text],[A,�������ǰ������Աȱ�],[B,�����˾����ò�ѯ],[C,�������빹�ɱ�]]");
		 comboBox.setSelectedIndex(0);
		 ctzCode=(TComboBox)getComponent("CTZ_CODE");
		 Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/')  
							+ " 00:00:00");
		 this.setValue("END_DATE", 
				 StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/')
					+ " 23:59:59");	 
	}
	
	public void onQuery(){
		
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
		if(((TRadioButton)getComponent("CTZ_ALL")).isSelected()){
//			whereSql="";
			andSql="";
//			where="";
//			and="";
			mroCTZ="";
		}else if(((TRadioButton)getComponent("CTZ_NHI")).isSelected()){
//			whereSql=" Z.CTZ1_CODE IN ('11','12','13','21','22','23') ";
//			andSql=" AND A.CTZ1_CODE IN ('11','12','13','21','22','23') ";
//			where=" WHERE ";
//			and=" AND ";
			andSql=" AND Y.NHI_CTZ_FLG='Y' ";
			int ctz = getValueInt("CTZ_CODE");
			if (ctz == 1) {
				mroCTZ = " AND Y.MRO_CTZ= '1'";// ��ְ
			}else if (ctz == 2) {
				mroCTZ = " AND Y.MRO_CTZ= '2'";// �Ǿ�
			}else{
				mroCTZ="";//ȫ��
			}

		}else if(((TRadioButton)getComponent("CTZ_OWN")).isSelected()){
//			whereSql=" Z.CTZ1_CODE NOT IN ('11','12','13','21','22','23') ";
//			andSql=" AND A.CTZ1_CODE NOT IN ('11','12','13','21','22','23') ";
//			where=" WHERE ";
//			and=" AND ";
			andSql=" AND Y.NHI_CTZ_FLG='N' ";
			mroCTZ="";
		}

//		String sql=
//			" SELECT  Z.REXP_CODE,Z.CHN_DESC,'' COUNT,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+
//			" FROM ( "+
//			" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+
//			" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+
//			" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
//			" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
//			" AND B.CASE_NO=A.CASE_NO "+
//			" AND A.ADM_TYPE IN ('O','E') "+
//			" AND A.REGCAN_USER IS NULL "+
//			" AND B.BILL_FLG='Y' "+
//			" AND C.ID=B.REXP_CODE "+
//			" AND C.GROUP_ID='SYS_CHARGE' "+
//			" AND D.ORDER_CODE=B.ORDER_CODE "+
//			" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+
//			"  ) Z ";
//		sql=sql+where+whereSql;
//		sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC  "+
//			" ORDER BY Z.REXP_CODE ";
//
//		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
//		int count=tabParm.getCount("REXP_CODE");
//		if(count<0){
//			this.messageBox("û��Ҫ��ѯ�����ݣ�");
//			onClear();
//			return;
//		}
//		double ar_amt=0;
//		double ar_amt_2=0;
//		for(int i=0;i<count;i++){
//			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
//			 ar_amt_2=ar_amt_2+Double.parseDouble(tabParm.getValue("AR_AMT_2", i));
//		}
//
//		
//		//ȡ�õ�ǰ�Һŷ�����
//		String sql1=
//			" SELECT SUM(B.AR_AMT) AR_AMT "+ 
//			" FROM REG_PATADM A,BIL_REG_RECP B "+ 
//			" WHERE TO_CHAR(A.ADM_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('O','E') "+ 
//			" AND A.REGCAN_USER IS NULL "+ 
//		    " AND B.CASE_NO=A.CASE_NO "+andSql;
//		
//        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		
//		if(parm1.getCount("AR_AMT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
//		//ȡ��ԭ�Һŷ�����
//		String sql2=
//			" SELECT COUNT(A.CLINICTYPE_CODE) COUNT,SUM( "+
//			" CASE WHEN A.CLINICTYPE_CODE='01' THEN 13 "+
//			" WHEN A.CLINICTYPE_CODE='02' THEN 8 "+
//			" WHEN A.CLINICTYPE_CODE='03' THEN 4 "+
//			" WHEN A.CLINICTYPE_CODE='04' THEN 6 "+
//			" WHEN A.CLINICTYPE_CODE='05' THEN 13 END "+
//			" ) AR_AMT_2 "+
//			" FROM REG_PATADM A "+
//			" WHERE TO_CHAR(A.ADM_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('E','O') "+
//			" AND A.REGCAN_USER IS NULL "+andSql;
//		
//        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		
//		if(parm2.getCount("COUNT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
//
//		//ȡ�ÿ�ҩ��������
//		String sql3=
//			" SELECT COUNT(Z.CASE_NO) COUNT "+
//			" FROM ( "+
//			" SELECT A.CTZ1_CODE,A.CASE_NO  "+
//			" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
//			" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND B.CASE_NO=A.CASE_NO "+
//			" AND A.ADM_TYPE IN ('O','E') "+
//			" AND A.REGCAN_USER IS NULL "+
//			" AND B.BILL_FLG='Y' "+
//			" AND B.REXP_CODE IN ('010.01','010.02','011')  "+
//			" AND C.ID=B.REXP_CODE "+
//			" AND C.GROUP_ID='SYS_CHARGE' "+
//			" AND D.ORDER_CODE=B.ORDER_CODE "+
//			" GROUP BY A.CTZ1_CODE,A.CASE_NO "+
//			" ) Z  "+where+whereSql;
//		 TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
//			
//			if(parm3.getCount("COUNT")<0){
//				this.messageBox("û�йҺ����ݣ�");
//			}
//		tabParm.addData("REXP_CODE", "");
//		tabParm.addData("CHN_DESC", "�Һŷ�");
//		tabParm.addData("COUNT", "");
//		tabParm.addData("AR_AMT", parm1.getValue("AR_AMT", 0));
//		tabParm.addData("AR_AMT_2", parm2.getValue("AR_AMT_2", 0));
//		tabParm.addData("DIFF", Double.parseDouble(parm1.getValue("AR_AMT", 0))-
//				                Double.parseDouble(parm2.getValue("AR_AMT_2", 0)));
//		
//		ar_amt=ar_amt+Double.parseDouble(parm1.getValue("AR_AMT", 0)); 
//		ar_amt_2=ar_amt_2+Double.parseDouble(parm2.getValue("AR_AMT_2", 0));
//		// ���㹹�ɱ���
//		NumberFormat format=NumberFormat.getPercentInstance();
//		format.setMinimumFractionDigits(4);
//		for(int i=0;i<count+1;i++){
//			 tabParm.setData("ARATIO", i, format.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/ar_amt));
//		}
//
//		
//		tabParm.addData("REXP_CODE", "");
//		tabParm.addData("CHN_DESC", "�ϼ�");
//		tabParm.addData("COUNT", parm2.getValue("COUNT", 0));
//		tabParm.addData("AR_AMT", ar_amt);
//		tabParm.addData("AR_AMT_2", ar_amt_2);
//		tabParm.addData("DIFF", ar_amt-ar_amt_2);
//		
//		tabParm.setData("COUNT", 0, parm3.getValue("COUNT", 0));
//		table.setParmValue(tabParm);
//		//REXP_CODE,CHN_DESC,COUNT,AR_AMT,AR_AMT_2,DIFF
		String value=getValueString("tTableName");
		if(value.equals("A")){
			onQueryA( date_s, date_e);
		}else if(value.equals("B")){
			onQueryB( date_s,  date_e) ;
		}else if(value.equals("C")){
			onQueryC( date_s,  date_e);
		}
	}
	
	private void onQueryA(String date_s, String date_e) {
		table.setHeader("������ϸ,100,CHN_DESC;�ּ۸�,150,double;�ɼ۸�,150,double;�Ա�,150,double;����,150");
		table.setParmMap("CHN_DESC;AR_AMT;AR_AMT_2;DIFF;ARATIO");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right");
		String sql="";
//		System.out.println("onQueryA:"+this.checkBox.getValue().equals("Y"));
		if (this.checkBox.getValue().equals("Y")) {
			 sql=" SELECT N.OUT_ID REXP_CODE,N.OUT_DESC CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+
			     " FROM ( "+
					" SELECT  Z.REXP_CODE,Z.CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+
					" FROM ( "+
					" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+
					" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+
					" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
					" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
					" AND B.CASE_NO=A.CASE_NO "+
					" AND A.ADM_TYPE IN ('O','E') "+
					" AND A.REGCAN_USER IS NULL "+
					" AND B.BILL_FLG='Y' "+
					" AND C.ID=B.REXP_CODE "+
					" AND C.GROUP_ID='SYS_CHARGE' "+
					" AND D.ORDER_CODE=B.ORDER_CODE "+
					" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+
					" ) Z,SYS_CTZ Y  "+
					" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
				sql=sql+andSql+mroCTZ;
				sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC "+
				 " ORDER BY Z.REXP_CODE ) M,SYS_DIC_FOROUT N "+
				 " WHERE N.GROUP_ID='CHARGE_CLASSIFY' "+
				 " AND M.REXP_CODE=N.ID "+
				 " GROUP BY N.OUT_ID,N.OUT_DESC ";
		} else {
			 sql=
				" SELECT  Z.REXP_CODE,Z.CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+
				" FROM ( "+
				" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+
				" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+
				" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
				" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
				" AND B.CASE_NO=A.CASE_NO "+
				" AND A.ADM_TYPE IN ('O','E') "+
				" AND A.REGCAN_USER IS NULL "+
				" AND B.BILL_FLG='Y' "+
				" AND C.ID=B.REXP_CODE "+
				" AND C.GROUP_ID='SYS_CHARGE' "+
				" AND D.ORDER_CODE=B.ORDER_CODE "+
				" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+
				" ) Z,SYS_CTZ Y  "+
				" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
			sql=sql+andSql+mroCTZ;
			sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC  "+
				" ORDER BY Z.REXP_CODE ";
		}
//		System.out.println("onQueryA:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			onClear();
			return;
		}
		double ar_amt=0;
		double ar_amt_2=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
			 ar_amt_2=ar_amt_2+Double.parseDouble(tabParm.getValue("AR_AMT_2", i));
		}

		
		//ȡ�õ�ǰ�Һŷ�����
//		String sql1=
//			" SELECT SUM(B.AR_AMT) AR_AMT "+ 
//			" FROM REG_PATADM A,BIL_REG_RECP B "+ 
//			" WHERE TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('O','E') "+ 
//			" AND A.ARRIVE_FLG='Y' "+
//			" AND A.REGCAN_USER IS NULL "+ 
//		    " AND B.CASE_NO=A.CASE_NO "+andSql;
//		
//        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		
//		if(parm1.getCount("AR_AMT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
		//ȡ��ԭ�Һŷ�����
//		String sql2=
//			" SELECT COUNT(A.CLINICTYPE_CODE) COUNT,SUM( "+
//			" CASE WHEN A.CLINICTYPE_CODE='01' THEN 13 "+
//			" WHEN A.CLINICTYPE_CODE='02' THEN 8 "+
//			" WHEN A.CLINICTYPE_CODE='03' THEN 4 "+
//			" WHEN A.CLINICTYPE_CODE='04' THEN 6 "+
//			" WHEN A.CLINICTYPE_CODE='05' THEN 13 END "+
//			" ) AR_AMT_2 "+
//			" FROM REG_PATADM A "+
//			" WHERE TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('E','O') "+
//			" AND A.ARRIVE_FLG='Y' "+
//			" AND A.REGCAN_USER IS NULL "+andSql;
//		
//        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		
//		if(parm2.getCount("COUNT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
		String sql1=
			" SELECT "+
			" COUNT(B.CASE_NO) COUNT, "+ 
			" SUM (B.AR_AMT) AS AR_AMT, "+ 
			" SUM  "+
			" (  "+
			"     CASE "+ 
			"         WHEN A.CLINICTYPE_CODE= '01' "+ 
			"         THEN 13 "+
			"         WHEN A.CLINICTYPE_CODE= '02' "+ 
			"         THEN 8  "+
			"         WHEN A.CLINICTYPE_CODE= '03' "+
			"         THEN 4  "+
			"         WHEN A.CLINICTYPE_CODE= '04' "+ 
			"         THEN 6  "+
			"         WHEN A.CLINICTYPE_CODE= '05'  "+
			"         THEN 13 "+
			"     END  "+
			" )  "+
			" AR_AMT_2 "+ 
			" FROM  "+
			" REG_PATADM A , "+ 
			" BIL_REG_RECP B ,SYS_CTZ Y "+
			" WHERE  "+
			" TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND B.ADM_TYPE IN ('O','E')  "+
			" AND B.AR_AMT   >= 0  "+
			" AND B.CASE_NO   = A.CASE_NO "+ 
			" AND A.REGCAN_USER IS NULL  "+
			" AND A.ARRIVE_FLG= 'Y'  "+
			" AND Y.CTZ_CODE=A.CTZ1_CODE "+
			andSql+mroCTZ;
	    TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
			
		if(parm1.getCount("AR_AMT")<0){
			this.messageBox("û�йҺ����ݣ�");
		}
		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "�Һ�����");
		tabParm.addData("AR_AMT", parm1.getValue("AR_AMT", 0));
		tabParm.addData("AR_AMT_2", parm1.getValue("AR_AMT_2", 0));
		tabParm.addData("DIFF", df.format(Double.parseDouble(parm1.getValue("AR_AMT", 0))-
				                Double.parseDouble(parm1.getValue("AR_AMT_2", 0))));
//		tabParm.addData("ARATIO", (Double.parseDouble(parm1.getValue("AR_AMT", 0))-
//                                  Double.parseDouble(parm2.getValue("AR_AMT_2", 0)))/
//                                  Double.parseDouble(parm2.getValue("AR_AMT_2", 0)));
		
		ar_amt=ar_amt+Double.parseDouble(parm1.getValue("AR_AMT", 0)); 
		ar_amt_2=ar_amt_2+Double.parseDouble(parm1.getValue("AR_AMT_2", 0));
		
		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "�ϼ�");
		tabParm.addData("AR_AMT", df.format(ar_amt));
		tabParm.addData("AR_AMT_2", df.format(ar_amt_2));
		tabParm.addData("DIFF", df.format(ar_amt-ar_amt_2));
//		tabParm.addData("ARATIO", format.format((ar_amt-ar_amt_2)/ar_amt_2));
		
		// ��������= (�ּ۸�-�ɼ۸�)/�ɼ۸�
		NumberFormat format=NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(4);
		for(int i=0;i<count+2;i++){
			 tabParm.setData("ARATIO", i, format.format((Double.parseDouble(tabParm.getValue("AR_AMT", i))-
					                                     Double.parseDouble(tabParm.getValue("AR_AMT_2", i)))/
					                                     Double.parseDouble(tabParm.getValue("AR_AMT_2", i))));
		}
		table.setParmValue(tabParm);
	}

	private void onQueryB(String date_s ,String date_e) {
//		table.setHeader("ͳ����Ŀ,100,CHN_DESC;�����˾�����,150,double;�Ա����˾�����,150,double;�Ա�,150,double;����,150");
		table.setHeader("ͳ����Ŀ,100,CHN_DESC;�����˾�����,150;�Ա����˾�����,150;�Ա�,150;����,150");
		table.setParmMap("CHN_DESC;PERCAPITA;CORRTP;DIFF;ARATIO");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right");
		String sql="";
		if (this.checkBox.getValue().equals("Y")) {
			sql=                                                             
            " SELECT OUT_ID REXP_CODE,OUT_DESC CHN_DESC,COUNT(CASE_NO) COUNT,ROUND(SUM(AR_AMT)/COUNT(CASE_NO),2) PERCAPITA,'' CORRTP,'' DIFF,'' ARATIO,SUM(AR_AMT) AR_AMT "+
			" FROM ( "+
				" SELECT OUT_ID,OUT_DESC,CASE_NO,SUM(AR_AMT) AR_AMT "+
				" FROM ( "+
				"   SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,B.ORDER_DESC, "+
				"   SUM(B.AR_AMT) AR_AMT, SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.DOSAGE_QTY) DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3, "+
				"   A.CTZ1_CODE,A.CASE_NO,  E.OUT_ID,  E.OUT_DESC "+
				"   FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D,SYS_DIC_FOROUT E "+
				"   WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
				"   AND B.CASE_NO=A.CASE_NO "+
				"   AND A.ADM_TYPE IN ('O','E') "+ 
				"   AND A.REGCAN_USER IS NULL "+
				"   AND B.BILL_FLG='Y' "+
				"   AND C.ID=B.REXP_CODE "+
				"   AND C.GROUP_ID='SYS_CHARGE' "+ 
				"   AND D.ORDER_CODE=B.ORDER_CODE "+
				"   AND E.GROUP_ID='CHARGE_CLASSIFY' "+
				"   AND E.ID=B.REXP_CODE "+
				"   GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.OWN_PRICE,D.OWN_PRICE3, E.OUT_ID,  E.OUT_DESC HAVING SUM(B.AR_AMT)<>0 "+ 
				" ) Z,SYS_CTZ Y  "+
				" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
			sql=sql+andSql+mroCTZ;
				sql=sql+" GROUP BY  OUT_ID,OUT_DESC,CASE_NO "+
				" ORDER BY CASE_NO,OUT_ID ) N "+
		    " GROUP BY OUT_ID,OUT_DESC "+
		    " ORDER BY OUT_ID ";
		} else {
			sql=
	            " SELECT REXP_CODE,CHN_DESC,COUNT(CASE_NO) COUNT,ROUND(SUM(AR_AMT)/COUNT(CASE_NO),2) PERCAPITA,'' CORRTP,'' DIFF,'' ARATIO,SUM(AR_AMT) AR_AMT "+
				" FROM ( "+
					" SELECT REXP_CODE,CHN_DESC,CASE_NO,SUM(AR_AMT) AR_AMT "+
					" FROM ( "+
					"   SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,B.ORDER_DESC, "+
					"   SUM(B.AR_AMT) AR_AMT, SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.DOSAGE_QTY) DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3, "+
					"   A.CTZ1_CODE,A.CASE_NO,  E.OUT_ID,  E.OUT_DESC "+
					"   FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D,SYS_DIC_FOROUT E "+
					"   WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
					"   AND B.CASE_NO=A.CASE_NO "+
					"   AND A.ADM_TYPE IN ('O','E') "+ 
					"   AND A.REGCAN_USER IS NULL "+
					"   AND B.BILL_FLG='Y' "+
					"   AND C.ID=B.REXP_CODE "+
					"   AND C.GROUP_ID='SYS_CHARGE' "+ 
					"   AND D.ORDER_CODE=B.ORDER_CODE "+
					"   AND E.GROUP_ID='CHARGE_CLASSIFY' "+
					"   AND E.ID=B.REXP_CODE "+
					"   GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.OWN_PRICE,D.OWN_PRICE3, E.OUT_ID,  E.OUT_DESC HAVING SUM(B.AR_AMT)<>0 "+ 
					" ) Z,SYS_CTZ Y  "+
					" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
				sql=sql+andSql+mroCTZ;
					sql=sql+" GROUP BY  REXP_CODE,CHN_DESC,CASE_NO "+
					" ORDER BY CASE_NO,REXP_CODE ) N "+
			    " GROUP BY REXP_CODE,CHN_DESC "+
			    " ORDER BY REXP_CODE ";
		}
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			onClear();
			return;
		}
		double ar_amt=0;
		double pha_amt=0;
//		double pha_totalPeople=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
			 if(tabParm.getValue("REXP_CODE", i).equals("010.01")||
						tabParm.getValue("REXP_CODE", i).equals("010.02")||
						tabParm.getValue("REXP_CODE", i).equals("011")||
						tabParm.getValue("REXP_CODE", i).equals("01")){
				  pha_amt=pha_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
//				  pha_totalPeople=pha_totalPeople+Double.parseDouble(tabParm.getValue("COUNT", i));
			}
		}
		//ȡ�õ�ǰ�Һŷ�����
//		String sql1=
//			" SELECT SUM(B.AR_AMT) AR_AMT "+ 
//			" FROM REG_PATADM A,BIL_REG_RECP B "+ 
//			" WHERE TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('O','E') "+ 
//			" AND A.ARRIVE_FLG='Y' "+
//			" AND A.REGCAN_USER IS NULL "+ 
//		    " AND B.CASE_NO=A.CASE_NO "+andSql;
//		
//        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		
//		if(parm1.getCount("AR_AMT")<0){
//			this.messageBox("û�йҺ��������ݣ�");
//		}
		//ȡ��ԭ�Һŷ�����
//		String sql2=
//			" SELECT COUNT(A.CLINICTYPE_CODE) COUNT,SUM( "+
//			" CASE WHEN A.CLINICTYPE_CODE='01' THEN 13 "+
//			" WHEN A.CLINICTYPE_CODE='02' THEN 8 "+
//			" WHEN A.CLINICTYPE_CODE='03' THEN 4 "+
//			" WHEN A.CLINICTYPE_CODE='04' THEN 6 "+
//			" WHEN A.CLINICTYPE_CODE='05' THEN 13 END "+
//			" ) AR_AMT_2 "+
//			" FROM REG_PATADM A "+
//			" WHERE TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('E','O') "+
//			" AND A.ARRIVE_FLG='Y' "+
//			" AND A.REGCAN_USER IS NULL "+andSql;
//		
//        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		
//		if(parm2.getCount("COUNT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
		String sql1=
			" SELECT "+
			" COUNT(B.CASE_NO) COUNT, "+ 
			" SUM (B.AR_AMT) AS AR_AMT, "+ 
			" SUM  "+
			" (  "+
			"     CASE "+ 
			"         WHEN A.CLINICTYPE_CODE= '01' "+ 
			"         THEN 13 "+
			"         WHEN A.CLINICTYPE_CODE= '02' "+ 
			"         THEN 8  "+
			"         WHEN A.CLINICTYPE_CODE= '03' "+
			"         THEN 4  "+
			"         WHEN A.CLINICTYPE_CODE= '04' "+ 
			"         THEN 6  "+
			"         WHEN A.CLINICTYPE_CODE= '05'  "+
			"         THEN 13 "+
			"     END  "+
			" )  "+
			" AR_AMT_2 "+ 
			" FROM  "+
			" REG_PATADM A , "+ 
			" BIL_REG_RECP B,SYS_CTZ Y  "+
			" WHERE  "+
			" TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND B.ADM_TYPE IN ('O','E')  "+
			" AND B.AR_AMT   >= 0  "+
			" AND B.CASE_NO   = A.CASE_NO "+ 
			" AND A.REGCAN_USER IS NULL  "+
			" AND A.ARRIVE_FLG= 'Y'  "+
			" AND Y.CTZ_CODE=A.CTZ1_CODE "+
			andSql+mroCTZ;
	    TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
			
		if(parm1.getCount("AR_AMT")<0){
			this.messageBox("û�йҺ����ݣ�");
		}
//		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "�Һ�����");
		double totalPeople=Double.parseDouble(parm1.getValue("COUNT", 0));
//		tabParm.addData("COUNT", totalPeople);

		tabParm.addData("PERCAPITA", df.format(Double.parseDouble(parm1.getValue("AR_AMT", 0))/totalPeople));
		tabParm.addData("CORRTP","");
		tabParm.addData("DIFF", "");
		tabParm.addData("ARATIO", "");
		
		ar_amt=ar_amt+Double.parseDouble(parm1.getValue("AR_AMT", 0)); 
//		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "�ܷ���");
//		tabParm.addData("COUNT", totalPeople);
		tabParm.addData("PERCAPITA", df.format(ar_amt/totalPeople));
		tabParm.addData("CORRTP","");
		tabParm.addData("DIFF", "");
		tabParm.addData("ARATIO", "");
		// ���㱾���˾�����=��ͳ����Ŀ����/�Һ�����(ҩƷ��=ҩ��/��ҩ����)
		for(int i=0;i<count;i++){
			if(tabParm.getValue("REXP_CODE", i).equals("010.01")||
					tabParm.getValue("REXP_CODE", i).equals("010.02")||
					tabParm.getValue("REXP_CODE", i).equals("011")||
					tabParm.getValue("REXP_CODE", i).equals("01")){
				continue;
			}
//			tabParm.setData("COUNT", i,totalPeople); 
			tabParm.setData("PERCAPITA",i, df.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/
					totalPeople));
		}  
		//������
		tabParm.addData("CHN_DESC", "ע:");
		tabParm.addData("PERCAPITA", "������:");
		tabParm.addData("CORRTP",df.format(ar_amt));
		tabParm.addData("DIFF", "�˴�:");
		tabParm.addData("ARATIO", totalPeople);
		//ҩƷ����
		tabParm.addData("CHN_DESC", "");
		tabParm.addData("PERCAPITA", "ҩƷ����:");
		tabParm.addData("CORRTP",df.format(pha_amt));
		tabParm.addData("DIFF", "��ҩ�˴�:");
		//ȡ�ÿ�ҩ��������
		String sql3=
			" SELECT COUNT(Z.CASE_NO) COUNT "+
			" FROM ( "+
			" SELECT A.CTZ1_CODE,A.CASE_NO  "+
			" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
			" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
			" AND B.CASE_NO=A.CASE_NO "+
			" AND A.ADM_TYPE IN ('O','E') "+
			" AND A.REGCAN_USER IS NULL "+
			" AND B.BILL_FLG='Y' "+
			" AND B.REXP_CODE IN ('010.01','010.02','011')  "+
			" AND C.ID=B.REXP_CODE "+
			" AND C.GROUP_ID='SYS_CHARGE' "+
			" AND D.ORDER_CODE=B.ORDER_CODE "+
			" GROUP BY A.CTZ1_CODE,A.CASE_NO "+
			" ) Z,SYS_CTZ Y  "+
			" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
		sql3=sql3+andSql+mroCTZ;
//		System.out.println("ȡ�ÿ�ҩ��������:"+sql3);
		 TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
			
			if(parm3.getCount("COUNT")<0){
				this.messageBox("û�п�ҩ���ݣ�");
			}
		tabParm.addData("ARATIO", parm3.getValue("COUNT", 0));
		 
		table.setParmValue(tabParm);
	} 
	private void onQueryC(String date_s ,String date_e) {
		table.setHeader("�������,100,CHN_DESC;���ڷ���,150,double;�Ա��ڷ���,150,double;���ڹ��ɱ�,150;�Ա��ڹ��ɱ�,150;���ɱȲ�ֵ,150,double");
		table.setParmMap("CHN_DESC;AR_AMT;AR_AMT_2;CRATIO;CRATIO2;CRATIO_DIFF");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right");
		String sql="";
//		System.out.println("onQueryA:"+this.checkBox.getValue().equals("Y"));
		if (this.checkBox.getValue().equals("Y")) {
			 sql=" SELECT N.OUT_ID REXP_CODE,N.OUT_DESC CHN_DESC,SUM(AR_AMT) AR_AMT,'' AR_AMT_2,'' CRATIO,'' CRATIO2,'' CRATIO_DIFF "+
			     " FROM ( "+
					" SELECT  Z.REXP_CODE,Z.CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+
					" FROM ( "+
					" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+
					" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+
					" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
					" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
					" AND B.CASE_NO=A.CASE_NO "+
					" AND A.ADM_TYPE IN ('O','E') "+
					" AND A.REGCAN_USER IS NULL "+
					" AND B.BILL_FLG='Y' "+
					" AND C.ID=B.REXP_CODE "+
					" AND C.GROUP_ID='SYS_CHARGE' "+
					" AND D.ORDER_CODE=B.ORDER_CODE "+
					" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+
					" ) Z,SYS_CTZ Y  "+
					" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
				sql=sql+andSql+mroCTZ;
				sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC "+
				 " ORDER BY Z.REXP_CODE ) M,SYS_DIC_FOROUT N "+
				 " WHERE N.GROUP_ID='CHARGE_CLASSIFY' "+
				 " AND M.REXP_CODE=N.ID "+
				 " GROUP BY N.OUT_ID,N.OUT_DESC ";
		} else {
			 sql=
				" SELECT  Z.REXP_CODE,Z.CHN_DESC,SUM(AR_AMT) AR_AMT,'' AR_AMT_2,'' CRATIO,'' CRATIO2,'' CRATIO_DIFF "+
				" FROM ( "+
				" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+
				" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+
				" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D "+
				" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
				" AND B.CASE_NO=A.CASE_NO "+
				" AND A.ADM_TYPE IN ('O','E') "+
				" AND A.REGCAN_USER IS NULL "+
				" AND B.BILL_FLG='Y' "+
				" AND C.ID=B.REXP_CODE "+
				" AND C.GROUP_ID='SYS_CHARGE' "+
				" AND D.ORDER_CODE=B.ORDER_CODE "+
				" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+
				" ) Z,SYS_CTZ Y  "+
				" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
			sql=sql+andSql+mroCTZ;
			sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC  "+
				" ORDER BY Z.REXP_CODE ";
		}
//		System.out.println("onQueryA:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			onClear();
			return;
		}
		double ar_amt=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
		}

		
		//ȡ�õ�ǰ�Һŷ�����
//		String sql1=
//			" SELECT SUM(B.AR_AMT) AR_AMT "+ 
//			" FROM REG_PATADM A,BIL_REG_RECP B "+ 
//			" WHERE TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('O','E') "+ 
//			" AND A.ARRIVE_FLG='Y' "+
//			" AND A.REGCAN_USER IS NULL "+ 
//		    " AND B.CASE_NO=A.CASE_NO "+andSql;
//		
//        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		
//		if(parm1.getCount("AR_AMT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
		//ȡ��ԭ�Һŷ�����
//		String sql2=
//			" SELECT COUNT(A.CLINICTYPE_CODE) COUNT,SUM( "+
//			" CASE WHEN A.CLINICTYPE_CODE='01' THEN 13 "+
//			" WHEN A.CLINICTYPE_CODE='02' THEN 8 "+
//			" WHEN A.CLINICTYPE_CODE='03' THEN 4 "+
//			" WHEN A.CLINICTYPE_CODE='04' THEN 6 "+
//			" WHEN A.CLINICTYPE_CODE='05' THEN 13 END "+
//			" ) AR_AMT_2 "+
//			" FROM REG_PATADM A "+
//			" WHERE TO_CHAR(A.REG_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
//			" AND A.ADM_TYPE IN ('E','O') "+
//			" AND A.ARRIVE_FLG='Y' "+
//			" AND A.REGCAN_USER IS NULL "+andSql;
//		
//        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		
//		if(parm2.getCount("COUNT")<0){
//			this.messageBox("û�йҺ����ݣ�");
//		}
		String sql1=
			" SELECT "+
			" COUNT(B.CASE_NO) COUNT, "+ 
			" SUM (B.AR_AMT) AS AR_AMT, "+ 
			" SUM  "+
			" (  "+
			"     CASE "+ 
			"         WHEN A.CLINICTYPE_CODE= '01' "+ 
			"         THEN 13 "+
			"         WHEN A.CLINICTYPE_CODE= '02' "+ 
			"         THEN 8  "+
			"         WHEN A.CLINICTYPE_CODE= '03' "+
			"         THEN 4  "+
			"         WHEN A.CLINICTYPE_CODE= '04' "+ 
			"         THEN 6  "+
			"         WHEN A.CLINICTYPE_CODE= '05'  "+
			"         THEN 13 "+
			"     END  "+
			" )  "+
			" AR_AMT_2 "+ 
			" FROM  "+
			" REG_PATADM A , "+ 
			" BIL_REG_RECP B,SYS_CTZ Y  "+
			" WHERE  "+
			" TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND B.ADM_TYPE IN ('O','E')  "+
			" AND B.AR_AMT   >= 0  "+
			" AND B.CASE_NO   = A.CASE_NO "+ 
			" AND A.REGCAN_USER IS NULL  "+
			" AND A.ARRIVE_FLG= 'Y'  "+
			" AND Y.CTZ_CODE=A.CTZ1_CODE "+
			andSql+mroCTZ;
	    TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
			
		if(parm1.getCount("AR_AMT")<0){
			this.messageBox("û�йҺ����ݣ�");
		}
		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "�Һ�����");
		tabParm.addData("AR_AMT", parm1.getValue("AR_AMT", 0));
		
		ar_amt=ar_amt+Double.parseDouble(parm1.getValue("AR_AMT", 0)); 
		NumberFormat format=NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(4);
		// ���㱾�ڳɱ�=�ּ۸�/�ϼ�
		for(int i=0;i<count+1;i++){
			 tabParm.setData("CRATIO", i, format.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/ar_amt));
		}
		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "�ϼ�");
		tabParm.addData("AR_AMT", df.format(ar_amt));
//		tabParm.addData("ARATIO", format.format((ar_amt-ar_amt_2)/ar_amt_2));
		
		

		table.setParmValue(tabParm);
	}
	public void onClear(){
		table.setParmValue(new TParm());
		Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/')
							+ " 00:00:00");
		 this.setValue("END_DATE", date.toString()
					.substring(0, 10).replace('-', '/')
					+ " 23:59:59");
		
	}
	/**
	 * ���������ѯ
	 */
	public void onItemQuery() {
		TParm parm=new TParm();
		int row=table.getSelectedRow();
		TParm tableParm=table.getParmValue();
		TParm rowParm=tableParm.getRow(row);
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		rowParm.addData("START_DATE", date_s);
		rowParm.addData("END_DATE", date_e);
		rowParm.addData("QUE",andSql);
		rowParm.addData("MROCTZ", mroCTZ);
		rowParm.addData("ADM_TYPE","O");
		rowParm.addData("CHECKBOX",this.checkBox.getValue());
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\sta\\STAItemQuery.x", rowParm);
	}
	/**
	 * ����Excel
	 * */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "����������ܱ�");
	}
	/**
	 * ����ҽ������������༭״̬,ѡ��ȫ���ͷ�ҽ��ʱ���������޸�,�����
	 * */
	public void onTRBControl(){
		if(((TRadioButton)getComponent("CTZ_ALL")).isSelected()||
				((TRadioButton)getComponent("CTZ_OWN")).isSelected()){
			this.ctzCode.setEnabled(false);
			this.ctzCode.setValue("");
		}else if(((TRadioButton)getComponent("CTZ_NHI")).isSelected()){
			this.ctzCode.setEnabled(true);
		}
	}
}
