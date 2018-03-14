package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p> Title:  </p>
 * 
 * <p> Description: 门诊收入明细查询 </p>
 * 
 * <p> Copyright: Copyright (c) 2009 </p>
 * 
 * <p> Company: </p>
 * 
 * @author huangtt 20170608
 * @version 1.0
 */
public class OPBIncomeDetailNewControl extends TControl {
	private TTable table;
	private TTable tableDiff;
	private TTable tableFee;
	private static TCheckBox all;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	/**
	 * 初始化页面
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");	
		tableDiff = (TTable) getComponent("TABLE_DIFF");	
		tableFee = (TTable) getComponent("TABLE_FEE");	
		all = (TCheckBox) getComponent("selAll");
		// 初始化查询区间
		this.setValue("E_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		
		String sql1 = "SELECT  CHARGE01, CHARGE02, CHARGE03, CHARGE04,"
                + " CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09,"
                + " CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,"
                + " CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
                + " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24,"
                + " CHARGE25, CHARGE26, CHARGE27, CHARGE28, CHARGE29,"
                + " CHARGE30 "
                + " FROM BIL_RECPPARM WHERE ADM_TYPE='O'";
		String sql2 = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY  WHERE GROUP_ID = 'SYS_CHARGE'";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		TParm parmT = new TParm();
		parmT.addData("SEL", "N");
		parmT.addData("CODE", "'"+parm1.getValue("CHARGE01", 0)+"','"+parm1.getValue("CHARGE02", 0)+"'");
		parmT.addData("DESC", "西药费");
		for (int i = 3; i < 31; i++) {
			String v = "";
			if(i < 10){
				v = "CHARGE0"+i;
			}else{
				v = "CHARGE"+i;
			}
			
			if(parm1.getValue(v, 0).length() > 0){
				
				for (int j = 0; j < parm2.getCount(); j++) {
					if(parm1.getValue(v, 0).equals(parm2.getValue("ID", j))){
						parmT.addData("SEL", "N");
						parmT.addData("CODE", "'"+parm2.getValue("ID", j)+"'");
						parmT.addData("DESC", parm2.getValue("CHN_DESC", j));
						break;
					}
				}
			}
		}
		
		parmT.addData("SEL", "N");
		parmT.addData("CODE", "'001'");
		parmT.addData("DESC", "挂号费");
		
		tableFee.setParmValue(parmT);
		this.setValue("selAll", true);
		onAll();
		
//		REG_FEE_REAL 挂号费 001
//		CLINIC_FEE_REAL  诊察费 002

	
	}
	
	public void onAll(){
		tableFee.acceptText();
		TParm parm = tableFee.getParmValue();
		String flg = "N";
		if(all.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		for (int i = 0; i < parm.getCount("SEL"); i++) {
			parm.setData("SEL", i, flg);
	
		}
		tableFee.setParmValue(parm);
	}
	
	
	/**
	 * 查询
	 */
	public void onQuery() {
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		tableFee.acceptText();
		TParm parmFee = tableFee.getParmValue();
		String rexp="";
		String rexpReg="";
		String rexpClinic="";
		for (int i = 0; i < parmFee.getCount("CODE"); i++) {
			if(parmFee.getBoolean("SEL",i)){
				if("'001'".equals(parmFee.getValue("CODE", i))){
					rexpReg="001";
				}else if("'002'".equals(parmFee.getValue("CODE", i))){
					rexpClinic="002";
				}else{
					rexp =rexp + parmFee.getValue("CODE", i)+",";
				}
				
			}
		}
		if(rexp.length()> 0){
			rexp = rexp.substring(0, rexp.length()-1);
		}
		
		String sqlS = "SELECT BILL_DATE,DEPT,EXE_DEPT, CHN_DESC, SUM(AR_AMT) AR_AMT FROM ("
				+ " SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CHN_DESC DEPT,"
				+ " C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC, SUM(A.AR_AMT) AR_AMT"
				+ " FROM OPD_ORDER A, SYS_DEPT B, SYS_DEPT C, (SELECT ID, CHN_DESC"
				+ "  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE') D"
				+ "  WHERE  A.BILL_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
				+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS') AND A.DEPT_CODE = B.DEPT_CODE"
				+ " AND A.EXEC_DEPT_CODE = C.DEPT_CODE  AND A.REXP_CODE = D.ID "
				+ " AND A.REXP_CODE IN ( & )"
				+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC";

		if(rexpReg.length() > 0){
			sqlS +=  " UNION ALL"
					+ " SELECT A.BILL_DATE, B.DEPT_CHN_DESC DEPT, B.DEPT_CHN_DESC EXE_DEPT, '挂号费' CHN_DESC,"
					+ " SUM(A.REG_FEE_REAL) AR_AMT FROM SYS_DEPT B, REG_PATADM C,"
					+ " (SELECT TO_CHAR( BILL_DATE, 'YYYY-MM-DD') BILL_DATE, CASE_NO,"
					+ " SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL"
					+ " FROM BIL_REG_RECP WHERE BILL_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
					+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS') GROUP BY BILL_DATE, CASE_NO) A"
					+ " WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE"
					+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC";
		}
		
		if(rexpClinic.length() > 0){
			sqlS +=  " UNION ALL"
					+ " SELECT A.BILL_DATE , B.DEPT_CHN_DESC DEPT, B.DEPT_CHN_DESC EXE_DEPT, '诊察费' CHN_DESC,"
					+ "  SUM(A.CLINIC_FEE_REAL) AR_AMT FROM SYS_DEPT B, REG_PATADM C,"
					+ " (SELECT TO_CHAR( BILL_DATE, 'YYYY-MM-DD') BILL_DATE, CASE_NO,"
					+ " SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL"
					+ " FROM BIL_REG_RECP"
					+ " WHERE BILL_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
					+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS')"
					+ " GROUP BY BILL_DATE, CASE_NO) A"
					+ " WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE"
					+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC";
		}
		
		sqlS += ") GROUP BY BILL_DATE,DEPT,EXE_DEPT, CHN_DESC ORDER BY BILL_DATE";
		
		sqlS = sqlS.replaceAll("@", date_s);//wanglong modify 20150114
		sqlS = sqlS.replaceAll("#", date_e);		
		sqlS = sqlS.replaceAll("&", rexp);
		System.out.println("sqlS==="+sqlS);
		TParm resultS = new TParm(TJDODBTool.getInstance().select(sqlS));
		if(resultS.getCount() < 0){
			this.messageBox("没有要查询数据 ");
			table.removeRowAll();
			tableDiff.removeRowAll();
			return;
		}
		double sumAmt = 0;
		if(resultS.getCount() > 0){
			for (int i = 0; i < resultS.getCount(); i++) {
				sumAmt += resultS.getDouble("AR_AMT", i);
			}
			
			resultS.addData("BILL_DATE", "");
			resultS.addData("DEPT", "");
			resultS.addData("EXE_DEPT", "");
			resultS.addData("CHN_DESC", "总计");
			resultS.addData("AR_AMT", sumAmt);
			
		}
		
		table.setParmValue(resultS);
		
        String sqlA="SELECT BILL_DATE,DEPT,EXE_DEPT, CHN_DESC,RECEIPT_NO , SUM(AR_AMT) AR_AMT FROM ("
        		+ "SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CHN_DESC DEPT,"
        		+ " C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC, SUM(A.AR_AMT) AR_AMT,A.RECEIPT_NO"
        		+ " FROM OPD_ORDER A, SYS_DEPT B, SYS_DEPT C, (SELECT ID, CHN_DESC FROM SYS_DICTIONARY"
        		+ " WHERE GROUP_ID = 'SYS_CHARGE') D"
        		+ "  WHERE  A.BILL_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
        		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS') AND A.DEPT_CODE = B.DEPT_CODE"
        		+ " AND A.EXEC_DEPT_CODE = C.DEPT_CODE AND A.REXP_CODE = D.ID"
        		+ " AND A.REXP_CODE IN ( & )"
        		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC,A.RECEIPT_NO ";

        if(rexpReg.length() > 0){
        	sqlA += " UNION ALL"
            		+ " SELECT A.BILL_DATE, B.DEPT_CHN_DESC DEPT, B.DEPT_CHN_DESC EXE_DEPT, '挂号费' CHN_DESC,"
            		+ " SUM(A.REG_FEE_REAL) AR_AMT,A.RECEIPT_NO FROM SYS_DEPT B, REG_PATADM C,"
            		+ " (SELECT TO_CHAR( BILL_DATE, 'YYYY-MM-DD') BILL_DATE, CASE_NO,RECEIPT_NO,"
            		+ " SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL"
            		+ "  FROM BIL_REG_RECP WHERE BILL_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
            		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS')  GROUP BY BILL_DATE, CASE_NO,RECEIPT_NO) A"
            		+ " WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE"
            		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC,A.RECEIPT_NO";
        }
        if(rexpClinic.length() > 0){
        	sqlA += " UNION ALL"
            		+ " SELECT A.BILL_DATE , B.DEPT_CHN_DESC DEPT, B.DEPT_CHN_DESC EXE_DEPT, '诊察费' CHN_DESC,"
            		+ " SUM(A.CLINIC_FEE_REAL) AR_AMT,A.RECEIPT_NO FROM SYS_DEPT B, REG_PATADM C,"
            		+ " (SELECT TO_CHAR( BILL_DATE, 'YYYY-MM-DD') BILL_DATE, CASE_NO,RECEIPT_NO,"
            		+ " SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL"
            		+ "  FROM BIL_REG_RECP WHERE BILL_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
            		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS') GROUP BY BILL_DATE, CASE_NO,RECEIPT_NO) A"
            		+ " WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE"
            		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC,A.RECEIPT_NO";
        }
        
        sqlA += " ) GROUP BY BILL_DATE,DEPT,EXE_DEPT, CHN_DESC,RECEIPT_NO ORDER BY BILL_DATE,CHN_DESC,RECEIPT_NO";
        
        String sqlB = "SELECT BILL_DATE,DEPT,EXE_DEPT, CHN_DESC,RECEIPT_NO , SUM(AR_AMT) AR_AMT FROM ("
        		+ " SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CHN_DESC DEPT,"
        		+ " C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC, SUM(A.AR_AMT) AR_AMT,A.RECEIPT_NO"
        		+ " FROM OPD_ORDER A, SYS_DEPT B, SYS_DEPT C, (SELECT ID, CHN_DESC FROM SYS_DICTIONARY"
        		+ " WHERE GROUP_ID = 'SYS_CHARGE') D  WHERE A.RECEIPT_NO IN (SELECT A.RECEIPT_NO"
        		+ "  FROM BIL_OPB_RECP A WHERE A.ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
        		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS') AND A.ADM_TYPE <> 'H'"
        		+ " AND A.RESET_RECEIPT_NO IS NULL  AND A.AR_AMT > 0) AND A.DEPT_CODE = B.DEPT_CODE"
        		+ " AND A.EXEC_DEPT_CODE = C.DEPT_CODE AND A.REXP_CODE = D.ID"
        		+ " AND A.REXP_CODE IN ( & )"
        		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC,A.RECEIPT_NO"
        		+ " UNION ALL"
        		+ " SELECT TO_CHAR( A.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, B.DEPT_CHN_DESC DEPT,"
        		+ " C.DEPT_CHN_DESC EXE_DEPT, D.CHN_DESC, SUM(A.AR_AMT) AR_AMT,A.RECEIPT_NO"
        		+ " FROM OPD_ORDER_HISTORY_NEW A,  SYS_DEPT B, SYS_DEPT C, (SELECT ID, CHN_DESC"
        		+ " FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE') D"
        		+ " WHERE A.RECEIPT_NO IN (SELECT A.RECEIPT_NO FROM BIL_OPB_RECP A"
        		+ " WHERE A.ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
        		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS')"
        		+ " AND A.ADM_TYPE <> 'H' AND A.RESET_RECEIPT_NO IS NOT NULL"
        		+ " AND A.AR_AMT > 0)  AND A.DEPT_CODE = B.DEPT_CODE"
        		+ " AND A.EXEC_DEPT_CODE = C.DEPT_CODE AND A.REXP_CODE = D.ID"
        		+ " AND A.REXP_CODE IN ( & )"
        		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.DEPT_CHN_DESC, D.CHN_DESC,A.RECEIPT_NO"
        		+ " UNION ALL"
        		+ " SELECT TO_CHAR( B.BILL_DATE, 'YYYY-MM-DD') BILL_DATE, D.DEPT_CHN_DESC DEPT,"
        		+ " E.DEPT_CHN_DESC EXE_DEPT, F.CHN_DESC, SUM(-B.AR_AMT) AR_AMT,B.RECEIPT_NO"
        		+ " FROM BIL_OPB_RECP A, OPD_ORDER_HISTORY_NEW B, BIL_OPB_RECP C,"
        		+ " SYS_DEPT D, SYS_DEPT E, (SELECT ID, CHN_DESC FROM SYS_DICTIONARY"
        		+ " WHERE GROUP_ID = 'SYS_CHARGE') F  WHERE A.RESET_RECEIPT_NO IS NULL"
        		+ " AND A.AR_AMT < 0 AND A.RECEIPT_NO = C.RESET_RECEIPT_NO"
        		+ " AND A.ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS') "
        		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS')"
        		+ " AND C.RECEIPT_NO = B.RECEIPT_NO AND B.DEPT_CODE = D.DEPT_CODE"
        		+ " AND B.EXEC_DEPT_CODE = E.DEPT_CODE AND B.REXP_CODE = F.ID"
        		+ " AND B.REXP_CODE IN ( & )"
        		+ " GROUP BY B.BILL_DATE, D.DEPT_CHN_DESC, E.DEPT_CHN_DESC, F.CHN_DESC ,B.RECEIPT_NO" ;
        		
        if(rexpReg.length() > 0){
        	sqlB += " UNION ALL SELECT A.BILL_DATE, B.DEPT_CHN_DESC DEPT, B.DEPT_CHN_DESC EXE_DEPT, '挂号费' CHN_DESC,"
            		+ " SUM(A.REG_FEE_REAL) AR_AMT,A.RECEIPT_NO FROM SYS_DEPT B, REG_PATADM C,"
            		+ "  (SELECT TO_CHAR( BILL_DATE, 'YYYY-MM-DD') BILL_DATE, CASE_NO,RECEIPT_NO,"
            		+ " SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL"
            		+ " FROM BIL_REG_RECP WHERE ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
            		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS') GROUP BY BILL_DATE, CASE_NO,RECEIPT_NO) A"
            		+ "  WHERE A.CASE_NO = C.CASE_NO  AND C.REALDEPT_CODE = B.DEPT_CODE"
            		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC,A.RECEIPT_NO";
        }	
        
        if(rexpClinic.length() > 0){
        	sqlB += " UNION ALL"
            		+ " SELECT A.BILL_DATE , B.DEPT_CHN_DESC DEPT, B.DEPT_CHN_DESC EXE_DEPT, '诊察费' CHN_DESC,"
            		+ " SUM(A.CLINIC_FEE_REAL) AR_AMT,A.RECEIPT_NO FROM SYS_DEPT B,  REG_PATADM C,"
            		+ " (SELECT TO_CHAR( BILL_DATE, 'YYYY-MM-DD') BILL_DATE, CASE_NO,RECEIPT_NO,"
            		+ " SUM(REG_FEE_REAL) REG_FEE_REAL, SUM(CLINIC_FEE_REAL) CLINIC_FEE_REAL FROM BIL_REG_RECP"
            		+ " WHERE ACCOUNT_DATE BETWEEN TO_DATE( '@', 'YYYYMMDDHH24MISS')"
            		+ " AND TO_DATE( '#', 'YYYYMMDDHH24MISS')"
            		+ " GROUP BY BILL_DATE, CASE_NO,RECEIPT_NO) A"
            		+ " WHERE A.CASE_NO = C.CASE_NO AND C.REALDEPT_CODE = B.DEPT_CODE"
            		+ " GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, B.DEPT_CHN_DESC,A.RECEIPT_NO";
        }
        		
        		 
        sqlB += " ) GROUP BY BILL_DATE,DEPT,EXE_DEPT, CHN_DESC,RECEIPT_NO  ORDER BY CHN_DESC,RECEIPT_NO";
        
        
        
        String sql = "SELECT A.*,B.RECEIPT_NO AS RECEIPT_NO1,B.AR_AMT AS AR_AMT_BIL"
        		+ " FROM ( "+sqlA+" ) A, ( "+sqlB+" ) B"
        		+ " WHERE A.BILL_DATE=B.BILL_DATE AND A.DEPT=B.DEPT "
        		+ " AND A.EXE_DEPT=B.EXE_DEPT AND A.CHN_DESC=B.CHN_DESC "
        		+ " AND A.RECEIPT_NO=B.RECEIPT_NO AND A.AR_AMT <> B.AR_AMT"
        		+ " UNION ALL"
        		+ " SELECT BILL_DATE,DEPT,EXE_DEPT, CHN_DESC,  RECEIPT_NO, AR_AMT ,RECEIPT_NO_B RECEIPT_NO1 ,0 AR_AMT_BIL FROM "
        		+ " (SELECT A.*,B.RECEIPT_NO RECEIPT_NO_B FROM "
        		+ " ( "+sqlA+" ) A"
        		+ " LEFT JOIN"
        		+ " ( "+sqlB+" ) B"
        		+ " ON A.BILL_DATE=B.BILL_DATE AND A.DEPT=B.DEPT "
        		+ " AND A.EXE_DEPT=B.EXE_DEPT AND A.CHN_DESC=B.CHN_DESC "
        		+ " AND A.RECEIPT_NO=B.RECEIPT_NO) WHERE RECEIPT_NO_B IS NULL AND AR_AMT <> 0 "
        		+ " UNION ALL"
        		+ " SELECT BILL_DATE,DEPT,EXE_DEPT, CHN_DESC, RECEIPT_NO_B RECEIPT_NO, 0 AS AR_AMT,RECEIPT_NO RECEIPT_NO1 ,AR_AMT AR_AMT_BIL "
        		+ " FROM (SELECT A.*,B.RECEIPT_NO RECEIPT_NO_B FROM  "
        		+ " ( "+sqlB+" ) A"
        		+ " LEFT JOIN"
        		+ " ( "+sqlA+" ) B"
        		+ " ON A.BILL_DATE=B.BILL_DATE AND A.DEPT=B.DEPT "
        		+ " AND A.EXE_DEPT=B.EXE_DEPT AND A.CHN_DESC=B.CHN_DESC "
        		+ " AND A.RECEIPT_NO=B.RECEIPT_NO) WHERE RECEIPT_NO_B IS NULL AND AR_AMT <> 0"
        		+ " ORDER BY BILL_DATE,DEPT,EXE_DEPT, CHN_DESC";
        
        
        sql = sql.replaceAll("@", date_s);//wanglong modify 20150114
        sql = sql.replaceAll("#", date_e);
        sql = sql.replaceAll("&", rexp);
//        System.out.println(sqlA);
        System.out.println("sql------"+sql);
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			return;
		}
		
		double sumAmtY = 0;
		double sumAmtJ = 0;
		if(result.getCount() > 0){
			for (int i = 0; i < result.getCount(); i++) {
				sumAmtY += result.getDouble("AR_AMT", i);
				sumAmtJ += result.getDouble("AR_AMT_BIL", i);
			}
			
			result.addData("BILL_DATE", "");
			result.addData("DEPT", "");
			result.addData("EXE_DEPT", "");
			result.addData("CHN_DESC", "总计");
			result.addData("RECEIPT_NO", "");
			result.addData("AR_AMT", sumAmtY);
			result.addData("RECEIPT_NO1", "");
			result.addData("AR_AMT_BIL", sumAmtJ);
			
		}
		
		this.tableDiff.setParmValue(result);
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "门诊收入明细表");
		ExportExcelUtil.getInstance().exportExcel(tableDiff, "门诊收入差异明细表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
		tableDiff.removeRowAll();
	}
}
