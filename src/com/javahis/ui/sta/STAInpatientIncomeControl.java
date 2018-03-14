
package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;
import java.text.NumberFormat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
//import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
//import com.javahis.util.StringUtil;

/**
* <p>Title: 住院收入汇总表</p>
*
* <p>Description:住院收入汇总表 </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company: </p>
*
* @author zhangs 20140920
* @version 1.0
*/


public class STAInpatientIncomeControl extends TControl {
//	private static final String TParm = null;
	private static TTable table;
	private static TComboBox comboBox;
	private static TCheckBox checkBox;
//	private static TTextFormat START_DATE;
//	private static TTextFormat END_DATE;
//	private  String where="";
//	private  String and="";
	String whereSql="";
//	String andSql="";
	String dsDeptSql="";
	String opeSql="";
	java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
	
	public void onInit(){
		 table = (TTable) getComponent("TABLE");  
		 checkBox=(TCheckBox)getComponent("TCheckBox");
		 comboBox=(TComboBox)getComponent("tTableName");
		 comboBox.setStringData("[[id,text],[A,住院调价前后收入对比表],[B,住院人均费用查询],[C,住院收入构成表],[D,技术附加费]]");
		 comboBox.setSelectedIndex(0);
		 Timestamp date = StringTool.getTimestamp(new Date());
		 this.setValue("START_DATE",
				 StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/')
							+ " 00:00:00");
		 this.setValue("END_DATE", 
				 StringTool.rollDate(date, -1).toString().substring(0, 10).replace('-', '/')
					+ " 23:59:59");
//		table.setHeader("收据代码,100,REXP_CODE;收据名称,100,CHN_DESC;人次数,100;调价后,150,double;旧标准,150,double;对比,150,double;调价后构成比率,150");
//		table.setParmMap("REXP_CODE;CHN_DESC;COUNT;AR_AMT;AR_AMT_2;DIFF;ARATIO");
//		table.setItem("REXP_CODE;CHN_DESC");
//		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right");
	 
	}
	
	public void onQuery(){
		
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		//组合SQL
		//医保非医保
		if(((TRadioButton)getComponent("CTZ_ALL")).isSelected()){
			whereSql="";
//			andSql="";
//			where="";
//			and="";
		}else if(((TRadioButton)getComponent("CTZ_NHI")).isSelected()){
			whereSql=" AND Y.NHI_CTZ_FLG='Y' ";
//			andSql=" AND A.CTZ1_CODE IN ('11','12','13','21','22','23') ";
//			where=" WHERE ";
//			and=" AND ";
		}else if(((TRadioButton)getComponent("CTZ_OWN")).isSelected()){
			whereSql=" AND Y.NHI_CTZ_FLG='N' ";
//			andSql=" AND A.CTZ1_CODE NOT IN ('11','12','13','21','22','23') ";
//			where=" WHERE ";
//			and=" AND ";
		}
        //出院科室
		String dsDept=getValueString("DS_DEPT");
		
		if(dsDept.equals("")){
			dsDeptSql="";
		}else{
			dsDeptSql=" AND E.OUT_DEPT='"+dsDept+"' ";
		}
		//手术非手术
		String ope=getValueString("OPE");

		if(ope.equals("")){
			opeSql="";
		}else if(ope.equals("N")){
			opeSql=" AND E.OP_CODE IS NULL ";
		}else if(ope.equals("Y")){
			opeSql=" AND E.OP_CODE IS NOT NULL ";
		}
//		String sql=
//			" SELECT Z.REXP_CODE,Z.CHN_DESC,'' COUNT,SUM(Z.TOT_AMT) AR_AMT,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF,'' ARATIO "+
//			" FROM ( "+
//			" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC "+
//			" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E "+
//			" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
//			" AND A.CANCEL_FLG='N' "+
//			" AND A.CASE_NO=B.CASE_NO "+
//			" AND C.ID=B.REXP_CODE "+
//			" AND C.GROUP_ID='SYS_CHARGE' "+
//			" AND D.ORDER_CODE=B.ORDER_CODE "+
//			" AND E.CASE_NO=A.CASE_NO "+ 
//			" AND E.OUT_DEPT NOT IN ('0411') "+
//			dsDeptSql+
//			opeSql+
//			//--AND E.OUT_DEPT=''
//			//--AND E.OP_CODE is null
//			" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC "+
//			" ) Z  ";
//			//--WHERE  Z.CTZ1_CODE NOT IN ('11','12','13','21','22','23')
//		sql=sql+where+whereSql;
//		sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC  "+
//			" ORDER BY Z.REXP_CODE ";
//		
//		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
//		int count=tabParm.getCount("REXP_CODE");
//		if(count<0){
//			this.messageBox("没有要查询的数据！");
//			onClear();
//			return;
//		}
//		double ar_amt=0;
//		double ar_amt_2=0;
//		for(int i=0;i<count;i++){
//			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
//			 ar_amt_2=ar_amt_2+Double.parseDouble(tabParm.getValue("AR_AMT_2", i));
//		}
//		//取得住院人数
//		String sql2=
//			" SELECT COUNT(A.CASE_NO) COUNT "+
//			" FROM ADM_INP A,MRO_RECORD E "+
//			" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
//			" AND A.CANCEL_FLG='N' "+
//			" AND E.CASE_NO=A.CASE_NO "+
//			" AND E.OUT_DEPT NOT IN ('0411') "
//		    +andSql+dsDeptSql+opeSql;
//		//--AND A.CTZ1_CODE IN ('11','12','13','21','22','23')
//		//--AND E.OUT_DEPT=''
//		//--AND E.OP_CODE is null
//        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
//		
//		if(parm2.getCount("COUNT")<0){
//			this.messageBox("没有挂号数据！");
//		}
//
//		// 计算构成比率
//		NumberFormat format=NumberFormat.getPercentInstance();
//		format.setMinimumFractionDigits(4);
//		for(int i=0;i<count;i++){
//			 tabParm.setData("ARATIO", i, format.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/ar_amt));
//		}
//
//		
//		tabParm.addData("REXP_CODE", "");
//		tabParm.addData("CHN_DESC", "合计");
//		tabParm.addData("COUNT", parm2.getValue("COUNT", 0));
//		tabParm.addData("AR_AMT", ar_amt);
//		tabParm.addData("AR_AMT_2", ar_amt_2);
//		tabParm.addData("DIFF", ar_amt-ar_amt_2);
//		
//		table.setParmValue(tabParm);
//		//REXP_CODE,CHN_DESC,COUNT,AR_AMT,AR_AMT_2,DIFF
		String value=getValueString("tTableName");
		if(value.equals("A")){
			onQueryA( date_s, date_e);
		}else if(value.equals("B")){
			onQueryB( date_s,  date_e) ;
		}else if(value.equals("C")){
			onQueryC( date_s,  date_e);
		}else if(value.equals("D")){
			onQueryD( date_s,  date_e);
		}
	}
	private void onQueryA(String date_s, String date_e) {
		table.setHeader("费用明细,100,CHN_DESC;现价格,150,double;旧价格,150,double;对比,150,double;增幅,150");
		table.setParmMap("CHN_DESC;AR_AMT;AR_AMT_2;DIFF;ARATIO");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right");
		String sql="";
		if (this.checkBox.getValue().equals("Y")) {
			 sql=
					" SELECT Z.OUT_ID REXP_CODE,Z.OUT_DESC CHN_DESC,'' COUNT,SUM(Z.TOT_AMT) AR_AMT,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF,'' ARATIO "+
					" FROM ( "+
					" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
					" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
					" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
					" AND A.CANCEL_FLG='N' "+
					" AND A.CASE_NO=B.CASE_NO "+
					" AND C.ID=B.REXP_CODE "+
					" AND C.GROUP_ID='SYS_CHARGE' "+
					" AND D.ORDER_CODE=B.ORDER_CODE "+
					" AND E.CASE_NO=A.CASE_NO "+ 
					" AND E.OUT_DEPT NOT IN ('0411') "+
					dsDeptSql+
					opeSql+
					" AND F.ID=B.REXP_CODE "+
					" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
					" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
					" ) Z ,SYS_CTZ Y "+
					" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
				sql=sql+whereSql;
				sql=sql+" GROUP BY Z.OUT_ID,Z.OUT_DESC  "+
					" ORDER BY Z.OUT_ID ";
		}else{
		 sql=
			" SELECT Z.REXP_CODE,Z.CHN_DESC,'' COUNT,SUM(Z.TOT_AMT) AR_AMT,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF,'' ARATIO "+
			" FROM ( "+
			" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
			" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
			" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND A.CANCEL_FLG='N' "+
			" AND A.CASE_NO=B.CASE_NO "+
			" AND C.ID=B.REXP_CODE "+
			" AND C.GROUP_ID='SYS_CHARGE' "+
			" AND D.ORDER_CODE=B.ORDER_CODE "+
			" AND E.CASE_NO=A.CASE_NO "+ 
			" AND E.OUT_DEPT NOT IN ('0411') "+
			dsDeptSql+
			opeSql+
			" AND F.ID=B.REXP_CODE "+
			" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
			" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
			" ) Z ,SYS_CTZ Y "+
			" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
		sql=sql+whereSql;
		sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC  "+
			" ORDER BY Z.REXP_CODE ";
		}
//		System.out.println("onQueryA:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
		double ar_amt=0;
		double ar_amt_2=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
			 ar_amt_2=ar_amt_2+Double.parseDouble(tabParm.getValue("AR_AMT_2", i));
		}
		
		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "合计");
		tabParm.addData("AR_AMT", df.format(ar_amt));
		tabParm.addData("AR_AMT_2", df.format(ar_amt_2));
		tabParm.addData("DIFF", df.format(ar_amt-ar_amt_2));
		
		// 计算增幅= (现价格-旧价格)/旧价格
		NumberFormat format=NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(4);
		for(int i=0;i<count+1;i++){
			 tabParm.setData("ARATIO", i, format.format((Double.parseDouble(tabParm.getValue("AR_AMT", i))-
					                                     Double.parseDouble(tabParm.getValue("AR_AMT_2", i)))/
					                                     Double.parseDouble(tabParm.getValue("AR_AMT_2", i))));
		}
		
		table.setParmValue(tabParm);
	}
	private void onQueryB(String date_s, String date_e) {
//		table.setHeader("统计项目,100,CHN_DESC;人次,150,double;本期人均费用,150,double;对比期人均费用,150,double;对比,150,double;增幅,150");
//		table.setParmMap("CHN_DESC;COUNT;PERCAPITA;CORRTP;DIFF;ARATIO");
		table.setHeader("统计项目,100,CHN_DESC;本期人均费用,150;对比期人均费用,150;对比,150;增幅,150");
		table.setParmMap("CHN_DESC;PERCAPITA;CORRTP;DIFF;ARATIO");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right");
		String sql="";
		if (this.checkBox.getValue().equals("Y")) {
			sql=
            " SELECT OUT_ID REXP_CODE,OUT_DESC CHN_DESC,COUNT(CASE_NO) COUNT,ROUND(SUM(AR_AMT)/COUNT(CASE_NO),2) PERCAPITA,'' CORRTP,'' DIFF,'' ARATIO,SUM(AR_AMT) AR_AMT "+
			" FROM ( "+
				" SELECT OUT_ID,OUT_DESC,CASE_NO,SUM(TOT_AMT) AR_AMT "+
				" FROM ( "+
				" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
				" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
				" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
				" AND A.CANCEL_FLG='N' "+
				" AND A.CASE_NO=B.CASE_NO "+
				" AND C.ID=B.REXP_CODE "+
				" AND C.GROUP_ID='SYS_CHARGE' "+
				" AND D.ORDER_CODE=B.ORDER_CODE "+
				" AND E.CASE_NO=A.CASE_NO "+ 
				" AND E.OUT_DEPT NOT IN ('0411') "+
				dsDeptSql+
				opeSql+
				" AND F.ID=B.REXP_CODE "+
				" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
				" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
				" ) Z ,SYS_CTZ Y "+
				" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
			sql=sql+whereSql;
				sql=sql+" GROUP BY  OUT_ID,OUT_DESC,CASE_NO "+
				" ORDER BY CASE_NO,OUT_ID ) N "+
		    " GROUP BY OUT_ID,OUT_DESC "+
		    " ORDER BY OUT_ID ";
		} else {
			sql=
	            " SELECT REXP_CODE,CHN_DESC,COUNT(CASE_NO) COUNT,ROUND(SUM(AR_AMT)/COUNT(CASE_NO),2) PERCAPITA,'' CORRTP,'' DIFF,'' ARATIO,SUM(AR_AMT) AR_AMT "+
				" FROM ( "+
					" SELECT REXP_CODE,CHN_DESC,CASE_NO,SUM(TOT_AMT) AR_AMT "+
					" FROM ( "+
					" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
					" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
					" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
					" AND A.CANCEL_FLG='N' "+
					" AND A.CASE_NO=B.CASE_NO "+
					" AND C.ID=B.REXP_CODE "+
					" AND C.GROUP_ID='SYS_CHARGE' "+
					" AND D.ORDER_CODE=B.ORDER_CODE "+
					" AND E.CASE_NO=A.CASE_NO "+ 
					" AND E.OUT_DEPT NOT IN ('0411') "+
					dsDeptSql+
					opeSql+
					" AND F.ID=B.REXP_CODE "+
					" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
					" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
					" ) Z ,SYS_CTZ Y "+
					" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
				sql=sql+whereSql;
					sql=sql+" GROUP BY  REXP_CODE,CHN_DESC,CASE_NO "+
					" ORDER BY CASE_NO,REXP_CODE ) N "+
			    " GROUP BY REXP_CODE,CHN_DESC "+
			    " ORDER BY REXP_CODE ";
		}
//		System.out.println("onQueryB:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
		double ar_amt=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
		}

		//取得住院人数
		String sql2=
			" SELECT COUNT(A.CASE_NO) COUNT "+
			" FROM ADM_INP A,MRO_RECORD E,SYS_CTZ Y "+
			" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND A.CANCEL_FLG='N' "+
			" AND E.CASE_NO=A.CASE_NO "+
			" AND E.OUT_DEPT NOT IN ('0411') "+
			" AND Y.CTZ_CODE=A.CTZ1_CODE "
		    +whereSql+dsDeptSql+opeSql;

        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		
		if(parm2.getCount("COUNT")<0){
			this.messageBox("没有挂号数据！");
		}
		
		double totalPeople=Double.parseDouble(parm2.getValue("COUNT", 0));
		
//		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "总费用");
//		tabParm.addData("COUNT", totalPeople);
		tabParm.addData("PERCAPITA", df.format(ar_amt/totalPeople));
		tabParm.addData("CORRTP", "");
		tabParm.addData("DIFF", "");
		tabParm.addData("ARATIO", "");
		
		tabParm.addData("CHN_DESC", "注:");
//		tabParm.addData("COUNT", totalPeople);
		tabParm.addData("PERCAPITA", "总收入");
		tabParm.addData("CORRTP", df.format(ar_amt));
		tabParm.addData("DIFF", "人次");
		tabParm.addData("ARATIO", df.format(totalPeople));
		// 计算本期人均费用=各统计项目费用/住院人数(药品费=药费/开药人数)
		for(int i=0;i<count;i++){
//			if(tabParm.getValue("REXP_CODE", i).equals("010.01")||
//					tabParm.getValue("REXP_CODE", i).equals("010.02")||
//					tabParm.getValue("REXP_CODE", i).equals("011")||
//					tabParm.getValue("REXP_CODE", i).equals("01")){
//				continue;
//			}
//			tabParm.setData("COUNT", i,totalPeople); 
			tabParm.setData("PERCAPITA",i, df.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/
					totalPeople));
		}
		table.setParmValue(tabParm);
	}
	private void onQueryC(String date_s, String date_e) {
		table.setHeader("费用类别,100,CHN_DESC;本期费用,150,double;对比期费用,150,double;本期构成比,150;对比期构成比,150;构成比差值,150,double");
		table.setParmMap("CHN_DESC;AR_AMT;AR_AMT_2;CRATIO;CRATIO2;CRATIO_DIFF");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right");
		String sql="";
		if (this.checkBox.getValue().equals("Y")) {
			 sql=
					" SELECT Z.OUT_ID REXP_CODE,Z.OUT_DESC CHN_DESC,SUM(Z.TOT_AMT) AR_AMT,'' AR_AMT_2,'' CRATIO,'' CRATIO2,'' CRATIO_DIFF "+
					" FROM ( "+
					" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
					" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
					" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
					" AND A.CANCEL_FLG='N' "+
					" AND A.CASE_NO=B.CASE_NO "+
					" AND C.ID=B.REXP_CODE "+
					" AND C.GROUP_ID='SYS_CHARGE' "+
					" AND D.ORDER_CODE=B.ORDER_CODE "+
					" AND E.CASE_NO=A.CASE_NO "+ 
					" AND E.OUT_DEPT NOT IN ('0411') "+
					dsDeptSql+
					opeSql+
					" AND F.ID=B.REXP_CODE "+
					" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
					" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
					" ) Z ,SYS_CTZ Y "+
					" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
				sql=sql+whereSql;
				sql=sql+" GROUP BY Z.OUT_ID,Z.OUT_DESC  "+
					" ORDER BY Z.OUT_ID ";
		}else{
		 sql=
			" SELECT Z.REXP_CODE,Z.CHN_DESC,'' COUNT,SUM(Z.TOT_AMT) AR_AMT,'' AR_AMT_2,'' CRATIO,'' CRATIO2,'' CRATIO_DIFF "+
			" FROM ( "+
			" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
			" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
			" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND A.CANCEL_FLG='N' "+
			" AND A.CASE_NO=B.CASE_NO "+
			" AND C.ID=B.REXP_CODE "+
			" AND C.GROUP_ID='SYS_CHARGE' "+
			" AND D.ORDER_CODE=B.ORDER_CODE "+
			" AND E.CASE_NO=A.CASE_NO "+ 
			" AND E.OUT_DEPT NOT IN ('0411') "+
			dsDeptSql+
			opeSql+
			" AND F.ID=B.REXP_CODE "+
			" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
			" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+
			" ) Z ,SYS_CTZ Y "+
			" WHERE Y.CTZ_CODE=Z.CTZ1_CODE ";
		sql=sql+whereSql;
		sql=sql+" GROUP BY Z.REXP_CODE,Z.CHN_DESC  "+
			" ORDER BY Z.REXP_CODE ";
		}
//		System.out.println("onQueryA:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
		double ar_amt=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
		}
		
		NumberFormat format=NumberFormat.getPercentInstance();
		format.setMinimumFractionDigits(4);
		for(int i=0;i<count;i++){
			 tabParm.setData("CRATIO", i, format.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/ar_amt));
		}
		tabParm.addData("REXP_CODE", "");
		tabParm.addData("CHN_DESC", "合计");
		tabParm.addData("AR_AMT", df.format(ar_amt));
		
		table.setParmValue(tabParm);
	}
	private void onQueryD(String date_s, String date_e) {
		table.setHeader("科室,100,DEPT_DESC;身份,150;金额,150,double");
		table.setParmMap("DEPT_DESC;NHI_CTZ;TOT_AMT");
		table.setItem("DEPT_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,left;2,right");
		String sql=
			" SELECT D.DEPT_CHN_DESC DEPT_DESC,CASE WHEN Y.NHI_CTZ_FLG='Y' THEN '医保' ELSE '非医保' END NHI_CTZ,SUM(O.TOT_AMT) TOT_AMT "+
			" FROM JAVAHIS.SYS_PATINFO P,JAVAHIS.SYS_CTZ Y,JAVAHIS.SYS_DEPT D,JAVAHIS.IBS_ORDD O,JAVAHIS.MRO_RECORD E,JAVAHIS.ADM_INP A "+
			" WHERE  A.MR_NO=P.MR_NO "+
			whereSql+
			" AND Y.CTZ_CODE=A.CTZ1_CODE "+
			" AND D.DEPT_CODE(+)=A.DS_DEPT_CODE "+
			" AND A.CASE_NO=O.CASE_NO "+
			opeSql+
			" AND A.CASE_NO=E.CASE_NO "+
			" AND A.DS_DEPT_CODE <>'0411' "+
			" AND O.ORDER_CODE IN ('M1400123','M1400124','M1400125','M1400126','M1400127','M1400128','M1400129','M1400130','F0900005','F0900006','F0900007') "+
			dsDeptSql+
			" AND A.DS_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') AND TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') "+
			" GROUP BY D.DEPT_CHN_DESC,Y.NHI_CTZ_FLG "+
			" ORDER BY D.DEPT_CHN_DESC,Y.NHI_CTZ_FLG ";
		
//				System.out.println("onQueryD:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("DEPT_DESC");
		if(count<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}
		double ar_amt=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("TOT_AMT", i));
		}
		
//		NumberFormat format=NumberFormat.getPercentInstance();
//		format.setMinimumFractionDigits(4);
//		for(int i=0;i<count;i++){
//			 tabParm.setData("CRATIO", i, format.format(Double.parseDouble(tabParm.getValue("AR_AMT", i))/ar_amt));
//		}
		tabParm.addData("DEPT_DESC", "合计");
		tabParm.addData("NHI_CTZ", "");
		tabParm.addData("TOT_AMT", df.format(ar_amt));
		
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
		this.setValue("DS_DEPT", "");
		this.setValue("OPE", "");
	}
	/**
	 * 单项收入查询
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
		rowParm.addData("QUE",whereSql);
//		if(getValueString("tTableName").equals("D")){
//			rowParm.addData("ADM_TYPE","II");
//		}else{
			rowParm.addData("ADM_TYPE","I");			
//		}
		rowParm.addData("DEPT",this.dsDeptSql);
		rowParm.addData("OPE",this.opeSql);
		rowParm.addData("CHECKBOX",this.checkBox.getValue());
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\sta\\STAItemQuery.x", rowParm);
	}
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "住院收入汇总表");
	}
}
