package com.javahis.ui.sta;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

public class STAItemQueryControl extends TControl {
	private static TTable table;
	private String title="";
	public void onInit() {
		super.onInit();
		table = (TTable) getComponent("TABLE"); 
		// 得到前台传来的数据并显示在界面上
		table.setHeader("收据代码,80,REXP_CODE;收据名称,80,CHN_DESC;项目名称,120;数量,80;调价后单价,80;调价后合计,120,double;旧标准单价,80;旧标准合计,120,double;对比,120,double");
		table.setParmMap("REXP_CODE;CHN_DESC;ORDER_DESC;DOSAGE_QTY;OWN_PRICE;AR_AMT;OWN_PRICE3;AR_AMT_2;DIFF");
		table.setItem("REXP_CODE;CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,right;4,right;5,right;6,right;7,right;8,right");
	
		TParm recptype = this.getInputParm();
		if (recptype != null) {
//			System.out.println("recptype:"+recptype);
			title=recptype.getValue("CHN_DESC");
			this.setTitle(title);
			if(recptype.getValue("ADM_TYPE",0).equals("O")){
			  this.onQuery(recptype.getValue("START_DATE",0),recptype.getValue("END_DATE",0),recptype.getValue("QUE",0),recptype.getValue("REXP_CODE"),recptype.getValue("CHECKBOX",0));
			}else if(recptype.getValue("ADM_TYPE",0).equals("I")){
			  this.onQuery(recptype.getValue("START_DATE",0),recptype.getValue("END_DATE",0),recptype.getValue("QUE",0),recptype.getValue("REXP_CODE"),recptype.getValue("DEPT",0),recptype.getValue("OPE",0),recptype.getValue("CHECKBOX",0));
			}
		}
	}

	/**
	 * 查询备案信息
	 * 
	 * @return TParm
	 */
	private void onQuery(String date_s,String date_e,String ctz,String recpCode,String flg) {
		String Sql ="";
		if(flg.equals("N")){
		Sql = 
			" SELECT Z.REXP_CODE,Z.CHN_DESC,Z.ORDER_DESC,SUM(Z.DOSAGE_QTY) DOSAGE_QTY,Z.OWN_PRICE,SUM(Z.AR_AMT) AR_AMT,Z.OWN_PRICE3,SUM(Z.AR_AMT_2) AR_AMT_2,SUM(Z.AR_AMT)-SUM(Z.AR_AMT_2) DIFF "+
			" FROM ( "+
			" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC, "+
			" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,SUM(B.DOSAGE_QTY) DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3,F.OUT_ID,F.OUT_DESC "+
			" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D,SYS_DIC_FOROUT F "+
			" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
			" AND B.CASE_NO=A.CASE_NO "+
			" AND A.ADM_TYPE IN ('O','E') "+
			" AND A.REGCAN_USER IS NULL "+
			" AND B.BILL_FLG='Y' "+
			" AND C.ID=B.REXP_CODE "+
			" AND C.GROUP_ID='SYS_CHARGE' "+
			" AND D.ORDER_CODE=B.ORDER_CODE "+
			" AND F.ID=B.REXP_CODE "+
			" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
			" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.OWN_PRICE,D.OWN_PRICE3,F.OUT_ID,F.OUT_DESC HAVING SUM(B.AR_AMT)<>0 "+
			" ) Z  "+
			" WHERE Z.REXP_CODE ='"+recpCode+"'  "+ctz+
			" GROUP BY Z.REXP_CODE,Z.CHN_DESC,Z.ORDER_DESC,Z.OWN_PRICE,Z.OWN_PRICE3 HAVING SUM(AR_AMT_2) <> SUM(AR_AMT) "+
			" ORDER BY Z.REXP_CODE ";
		}else if(flg.equals("Y")){
			Sql = 
				" SELECT Z.OUT_ID REXP_CODE,Z.OUT_DESC CHN_DESC,Z.ORDER_DESC,SUM(Z.DOSAGE_QTY) DOSAGE_QTY,Z.OWN_PRICE,SUM(Z.AR_AMT) AR_AMT,Z.OWN_PRICE3,SUM(Z.AR_AMT_2) AR_AMT_2,SUM(Z.AR_AMT)-SUM(Z.AR_AMT_2) DIFF "+
				" FROM ( "+
				" SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC, "+
				" SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,SUM(B.DOSAGE_QTY) DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3,F.OUT_ID,F.OUT_DESC "+
				" FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D,SYS_DIC_FOROUT F "+
				" WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
				" AND B.CASE_NO=A.CASE_NO "+
				" AND A.ADM_TYPE IN ('O','E') "+
				" AND A.REGCAN_USER IS NULL "+
				" AND B.BILL_FLG='Y' "+
				" AND C.ID=B.REXP_CODE "+
				" AND C.GROUP_ID='SYS_CHARGE' "+
				" AND D.ORDER_CODE=B.ORDER_CODE "+
				" AND F.ID=B.REXP_CODE "+
				" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
				" GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.OWN_PRICE,D.OWN_PRICE3,F.OUT_ID,F.OUT_DESC HAVING SUM(B.AR_AMT)<>0 "+
				" ) Z  "+
				" WHERE Z.OUT_ID ='"+recpCode+"'  "+ctz+
				" GROUP BY Z.OUT_ID,Z.OUT_DESC,Z.ORDER_DESC,Z.OWN_PRICE,Z.OWN_PRICE3 HAVING SUM(AR_AMT_2) <> SUM(AR_AMT) "+
				" ORDER BY Z.OUT_ID ";
		}
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("REXP_CODE") < 0) {
			this.messageBox("没有查询到相应记录");
			return;
		}
		
		table.setParmValue(getTotAmt(tabParm));
	}
	/**
	 * 查询备案信息
	 * 
	 * @return TParm
	 */
	private void onQuery(String date_s,String date_e,String ctz,String recpCode,String deptCode,String ope,String flg) {
		String  Sql = "";
		if(flg.equals("N")){
		Sql = 
			" SELECT Z.REXP_CODE,Z.CHN_DESC,Z.ORDER_DESC,SUM(Z.DOSAGE_QTY) DOSAGE_QTY,Z.OWN_PRICE,SUM(Z.TOT_AMT) AR_AMT,Z.OWN_PRICE3,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF "+
			" FROM ( "+
			" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC, "+
			"        SUM(B.DOSAGE_QTY) DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3,B.ORDER_CHN_DESC ORDER_DESC,F.OUT_ID,F.OUT_DESC "+
			" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
			" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
			" AND A.CANCEL_FLG='N' "+
			" AND A.CASE_NO=B.CASE_NO "+
			" AND C.ID=B.REXP_CODE "+
			" AND C.GROUP_ID='SYS_CHARGE' "+
			" AND D.ORDER_CODE=B.ORDER_CODE "+
			" AND E.CASE_NO=A.CASE_NO "+
			" AND E.OUT_DEPT NOT IN ('0411') "+
			deptCode+ope+(recpCode.equals("")?"AND B.ORDER_CODE IN ('M1400123','M1400124','M1400125','M1400126','M1400127','M1400128','M1400129','M1400130','F0900005','F0900006','F0900007') ":"")+
			" AND F.ID=B.REXP_CODE "+
			" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
			" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,B.OWN_PRICE,D.OWN_PRICE3,B.ORDER_CHN_DESC,F.OUT_ID,F.OUT_DESC "+
			"  ) Z ,SYS_CTZ Y WHERE ";
		if(!recpCode.equals("")){
			Sql=Sql+" Z.REXP_CODE='"+recpCode+"' AND ";
		}
		Sql=Sql+" Y.CTZ_CODE=Z.CTZ1_CODE " +ctz+
			" GROUP BY Z.REXP_CODE,Z.CHN_DESC,Z.ORDER_DESC,Z.OWN_PRICE,Z.OWN_PRICE3 HAVING SUM(Z.TOT_AMT_2) <> SUM(Z.TOT_AMT) "+
			" ORDER BY Z.REXP_CODE ";
		}else if(flg.equals("Y")){
			Sql = 
				" SELECT Z.OUT_ID REXP_CODE,Z.OUT_DESC CHN_DESC,Z.ORDER_DESC,SUM(Z.DOSAGE_QTY) DOSAGE_QTY,Z.OWN_PRICE,SUM(Z.TOT_AMT) AR_AMT,Z.OWN_PRICE3,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF "+
				" FROM ( "+
				" SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC, "+
				"        SUM(B.DOSAGE_QTY) DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3,B.ORDER_CHN_DESC ORDER_DESC,F.OUT_ID,F.OUT_DESC "+
				" FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F "+
				" WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
				" AND A.CANCEL_FLG='N' "+
				" AND A.CASE_NO=B.CASE_NO "+
				" AND C.ID=B.REXP_CODE "+
				" AND C.GROUP_ID='SYS_CHARGE' "+
				" AND D.ORDER_CODE=B.ORDER_CODE "+
				" AND E.CASE_NO=A.CASE_NO "+
				" AND E.OUT_DEPT NOT IN ('0411') "+
				deptCode+ope+(recpCode.equals("")?"AND B.ORDER_CODE IN ('M1400123','M1400124','M1400125','M1400126','M1400127','M1400128','M1400129','M1400130','F0900005','F0900006','F0900007') ":"")+
				" AND F.ID=B.REXP_CODE "+
				" AND F.GROUP_ID='CHARGE_CLASSIFY' "+
				" GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,B.OWN_PRICE,D.OWN_PRICE3,B.ORDER_CHN_DESC,F.OUT_ID,F.OUT_DESC "+
				"  ) Z ,SYS_CTZ Y WHERE ";
				if(!recpCode.equals("")){
					Sql=Sql+" Z.OUT_ID='"+recpCode+"' AND ";				
				}
			Sql=Sql+" Y.CTZ_CODE=Z.CTZ1_CODE " +ctz+
				" GROUP BY Z.OUT_ID,Z.OUT_DESC,Z.ORDER_DESC,Z.OWN_PRICE,Z.OWN_PRICE3 HAVING SUM(Z.TOT_AMT_2) <> SUM(Z.TOT_AMT) "+
				" ORDER BY Z.OUT_ID ";
		}
		
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("REXP_CODE") < 0) {
			this.messageBox("没有查询到相应记录");
			return;
		}
		table.setParmValue(getTotAmt(tabParm));
	}
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, title+"收入汇总表");
	}
	public void onClear(){
		table.setParmValue(new TParm());
	}
	public TParm getTotAmt(TParm parm){
		int count=parm.getCount("REXP_CODE");
		double ar_amt=0;
		double ar_amt_2=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(parm.getValue("AR_AMT", i));
			 ar_amt_2=ar_amt_2+Double.parseDouble(parm.getValue("AR_AMT_2", i));
		}
		parm.addData("REXP_CODE", "");
		parm.addData("CHN_DESC", "");
		parm.addData("ORDER_DESC", "合计");
		parm.addData("DOSAGE_QTY", "");
		parm.addData("OWN_PRICE", "");
		parm.addData("AR_AMT", ar_amt);
		parm.addData("OWN_PRICE3", "");
		parm.addData("AR_AMT_2", ar_amt_2);
		parm.addData("DIFF", ar_amt-ar_amt_2);
		return parm;
	}

}
