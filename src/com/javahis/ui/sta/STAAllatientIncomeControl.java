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
* <p>Title: 全院收入汇总表</p>
*
* <p>Description:全院收入汇总表 </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company: </p>
*
* @author zhangs 20140920
* @version 1.0
*/


public class STAAllatientIncomeControl extends TControl {
//	private static final String TParm = null;
	private static TTable table;
	private static TComboBox comboBox;
	private static TCheckBox checkBox;
//	private static TTextFormat START_DATE;
//	private static TTextFormat END_DATE;
//	private  String where="";
//	private  String and="";
	String whereSql="";
	String andSql="";
	java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
	
	public void onInit(){
		 table = (TTable) getComponent("TABLE"); 
		 checkBox=(TCheckBox)getComponent("TCheckBox");
		 comboBox=(TComboBox)getComponent("tTableName");
//		 comboBox.setStringData("[[id,text],[A,门诊调价前后收入对比表],[B,门诊人均费用查询],[C,门诊收入构成表]]");
//		 comboBox.setSelectedIndex(0);
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
			whereSql="";
			andSql="";
//			where="";
//			and="";
		}else if(((TRadioButton)getComponent("CTZ_NHI")).isSelected()){
//			whereSql=" AND (Z.CTZ1_CODE IN ('11','12','13','21','22','23','01','03','04') OR Z.CTZ1_CODE IS NULL) ";
			andSql=" AND Y.NHI_CTZ_FLG='Y' ";
//			where=" WHERE ";
//			and=" AND ";
		}else if(((TRadioButton)getComponent("CTZ_OWN")).isSelected()){
//			whereSql=" AND (Z.CTZ1_CODE NOT IN ('11','12','13','21','22','23','01','03','04') OR Z.CTZ1_CODE IS NULL) ";
			andSql=" AND Y.NHI_CTZ_FLG='N' ";
//			where=" WHERE ";
//			and=" AND ";
		}

			onQueryA( date_s, date_e);
	}
	
	private void onQueryA(String date_s, String date_e) {
		table.setHeader("费用明细,100,CHN_DESC;现价格,150,double;旧价格,150,double;对比,150,double;增幅,150");
		table.setParmMap("CHN_DESC;AR_AMT;AR_AMT_2;DIFF;ARATIO");
		table.setItem("CHN_DESC");
		table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right");
		String sql="";
//		System.out.println("onQueryA:"+this.checkBox.getValue().equals("Y"));
		if (this.checkBox.getValue().equals("Y")) {
			 sql=
				" SELECT N.OUT_ID REXP_CODE,N.OUT_DESC CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+
			    " FROM ( "+
	            "       SELECT  M.ID REXP_CODE,M.CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+ 
				"       FROM ( "+
				"             SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+ 
				"             SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+ 
				"             FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D,SYS_CTZ Y "+
				"             WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+
				"             AND B.CASE_NO=A.CASE_NO "+ 
				"             AND A.ADM_TYPE IN ('O','E') "+ 
				"             AND A.REGCAN_USER IS NULL "+ 
				"             AND B.BILL_FLG='Y' "+ 
				"             AND C.ID=B.REXP_CODE "+ 
				"             AND C.GROUP_ID='SYS_CHARGE' "+ 
				"             AND D.ORDER_CODE=B.ORDER_CODE "+
				"             AND Y.CTZ_CODE=A.CTZ1_CODE "+andSql+
				"             GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+ 
				"         ) Z ,STA_IBS_OPB_CHARGE M "+
                "         WHERE M.OPB_ID=Z.REXP_CODE(+) ";
//        sql=sql+whereSql;
   		sql=sql+"         GROUP BY  M.ID,M.CHN_DESC "+
				"         UNION ALL "+
			    "         SELECT M.ID REXP_CODE,M.CHN_DESC,SUM(Z.TOT_AMT) AR_AMT,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF,'' ARATIO "+
			    "         FROM ( "+
			    "               SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+ 
			    "               FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F,SYS_CTZ Y "+
			    "               WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
			    "               AND A.CANCEL_FLG='N' "+ 
			    "               AND A.CASE_NO=B.CASE_NO "+
			    "               AND C.ID=B.REXP_CODE "+ 
			    "               AND C.GROUP_ID='SYS_CHARGE' "+ 
			    "               AND D.ORDER_CODE=B.ORDER_CODE "+ 
			    "               AND E.CASE_NO=A.CASE_NO "+  
			    "               AND E.OUT_DEPT NOT IN ('0411') "+ 
			    "               AND F.ID=B.REXP_CODE "+ 
			    "               AND F.GROUP_ID='CHARGE_CLASSIFY' "+
			    "               AND Y.CTZ_CODE=A.CTZ1_CODE "+andSql+
			    "               GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+ 
			    "         ) Z  ,STA_IBS_OPB_CHARGE M "+
                "         WHERE M.IBS_ID=Z.REXP_CODE(+) ";
//        sql=sql+whereSql;
        sql=sql+"         GROUP BY M.ID,M.CHN_DESC "+
                "  ) M,SYS_DIC_FOROUT N "+ 
				"  WHERE N.GROUP_ID='STA_IBS_OPB_CHARGE' "+ 
				"  AND M.REXP_CODE=N.ID "+
				"  GROUP BY N.OUT_ID,N.OUT_DESC  "+
                "  ORDER BY N.OUT_ID ";
		} else {
			 sql=
				" SELECT REXP_CODE,CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(DIFF) DIFF,ARATIO "+
		        " FROM "+
		        " ( "+
				"	 SELECT  M.ID REXP_CODE,M.CHN_DESC,SUM(AR_AMT) AR_AMT,SUM(AR_AMT_2) AR_AMT_2,SUM(AR_AMT)-SUM(AR_AMT_2) DIFF,'' ARATIO "+ 
				"	 FROM ( "+
				"	 SELECT B.REXP_CODE REXP_CODE,C.CHN_DESC CHN_DESC,A.CASE_NO, "+ 
				"	 SUM(B.TOT_AMT2) AR_AMT_2,SUM(B.AR_AMT) AR_AMT,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 "+ 
				"	 FROM REG_PATADM A,OPD_ORDER B,SYS_DICTIONARY C,SYS_FEE D,SYS_CTZ Y "+ 
				"	 WHERE TO_CHAR(B.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
				"	 AND B.CASE_NO=A.CASE_NO "+
				"	 AND A.ADM_TYPE IN ('O','E') "+ 
				"	 AND A.REGCAN_USER IS NULL "+
				"	 AND B.BILL_FLG='Y' "+ 
				"	 AND C.ID=B.REXP_CODE "+ 
				"	 AND C.GROUP_ID='SYS_CHARGE' "+ 
				"	 AND D.ORDER_CODE=B.ORDER_CODE "+ 
				"    AND Y.CTZ_CODE=A.CTZ1_CODE "+andSql+
				"	 GROUP BY B.REXP_CODE,C.CHN_DESC,A.CTZ1_CODE,A.CASE_NO,B.ORDER_DESC,B.DOSAGE_QTY,B.OWN_PRICE,D.OWN_PRICE3 HAVING SUM(B.AR_AMT)<>0 "+ 
				"    ) Z ,STA_IBS_OPB_CHARGE M "+
		        "    WHERE M.OPB_ID=Z.REXP_CODE(+) ";
//		sql=sql+whereSql;
		sql=sql+"    GROUP BY  M.ID,M.CHN_DESC "+
				"	 UNION ALL "+
				"	 SELECT M.ID REXP_CODE,M.CHN_DESC,SUM(Z.TOT_AMT) AR_AMT,SUM(Z.TOT_AMT_2) AR_AMT_2,SUM(Z.TOT_AMT)-SUM(Z.TOT_AMT_2) DIFF,'' ARATIO "+ 
				"	 FROM ( "+ 
				"	 SELECT SUM(B.TOT_AMT2) TOT_AMT_2,SUM(B.TOT_AMT) TOT_AMT,A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+ 
				"	 FROM ADM_INP A,IBS_ORDD B,SYS_DICTIONARY C,SYS_FEE D,MRO_RECORD E,SYS_DIC_FOROUT F,SYS_CTZ Y "+
				"	 WHERE TO_CHAR(A.DS_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+date_s+"' AND '"+date_e+"' "+ 
				"	 AND A.CANCEL_FLG='N' "+ 
				"	 AND A.CASE_NO=B.CASE_NO "+ 
				"	 AND C.ID=B.REXP_CODE "+ 
				"	 AND C.GROUP_ID='SYS_CHARGE' "+ 
				"	 AND D.ORDER_CODE=B.ORDER_CODE "+ 
				"	 AND E.CASE_NO=A.CASE_NO "+  
				"	 AND E.OUT_DEPT NOT IN ('0411') "+ 
				"	 AND F.ID=B.REXP_CODE "+ 
				"	 AND F.GROUP_ID='CHARGE_CLASSIFY' "+
				"    AND Y.CTZ_CODE=A.CTZ1_CODE "+andSql+
				"	 GROUP BY A.CTZ1_CODE,B.REXP_CODE,C.CHN_DESC,A.CASE_NO,F.OUT_ID,F.OUT_DESC "+ 
				"	 ) Z  ,STA_IBS_OPB_CHARGE M "+
		        "    WHERE M.IBS_ID=Z.REXP_CODE(+) ";
//		sql=sql+whereSql;
		sql=sql+"    GROUP BY  M.ID,M.CHN_DESC "+
		        " ) "+
		        " GROUP BY REXP_CODE,CHN_DESC,ARATIO "+
				" ORDER BY  REXP_CODE ";
		}
//		System.out.println("onQueryA:"+sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count=tabParm.getCount("REXP_CODE");
		if(count<0){
			this.messageBox("没有要查询的数据！");
			onClear();
			return;
		}

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
			" AND Y.CTZ_CODE=A.CTZ1_CODE "+andSql;
//		System.out.println("onQueryA:"+sql1);
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
			
		if(parm1.getCount("AR_AMT")<0){
			this.messageBox("没有挂号数据！");
		}
//		tabParm.addData("REXP_CODE", "");
//		tabParm.addData("CHN_DESC", "挂号诊察费");
//		tabParm.addData("AR_AMT", parm1.getValue("AR_AMT", 0));
//		tabParm.addData("AR_AMT_2", parm1.getValue("AR_AMT_2", 0));
//		tabParm.addData("DIFF", df.format(Double.parseDouble(parm1.getValue("AR_AMT", 0))-
//				                Double.parseDouble(parm1.getValue("AR_AMT_2", 0))));
		if (this.checkBox.getValue().equals("Y")) {
			tabParm.setData("AR_AMT", 6, df.format(Double.parseDouble(parm1.getValue("AR_AMT", 0))+Double.parseDouble(tabParm.getValue("AR_AMT", 6))));
			tabParm.setData("AR_AMT_2", 6, df.format(Double.parseDouble(parm1.getValue("AR_AMT_2", 0))+Double.parseDouble(tabParm.getValue("AR_AMT_2", 6))));
			tabParm.setData("DIFF", 6, df.format(Double.parseDouble(parm1.getValue("AR_AMT", 0))-
	                                    Double.parseDouble(parm1.getValue("AR_AMT_2", 0))+Double.parseDouble(tabParm.getValue("DIFF", 6))));
		}else{
		tabParm.setData("AR_AMT", 16, parm1.getValue("AR_AMT", 0));
		tabParm.setData("AR_AMT_2", 16, parm1.getValue("AR_AMT_2", 0));
		tabParm.setData("DIFF", 16, df.format(Double.parseDouble(parm1.getValue("AR_AMT", 0))-
                                    Double.parseDouble(parm1.getValue("AR_AMT_2", 0))));
		}
		//合计
		double ar_amt=0;
		double ar_amt_2=0;
		for(int i=0;i<count;i++){
			 ar_amt=ar_amt+Double.parseDouble(tabParm.getValue("AR_AMT", i));
			 ar_amt_2=ar_amt_2+Double.parseDouble(tabParm.getValue("AR_AMT_2", i));
		}

//		ar_amt=ar_amt+Double.parseDouble(parm1.getValue("AR_AMT", 0)); 
//		ar_amt_2=ar_amt_2+Double.parseDouble(parm1.getValue("AR_AMT_2", 0));
		
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
	 * 单项收入查询
	 */
	public void onItemQuery() {

	}
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "全院收入汇总表");
	}
}
