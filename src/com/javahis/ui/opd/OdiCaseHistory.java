package com.javahis.ui.opd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;

public class OdiCaseHistory {
	
	private OpdCaseHistoryControl caseHistoryControl;
	
	public OdiCaseHistory(OpdCaseHistoryControl caseHistoryControl){
		this.caseHistoryControl = caseHistoryControl;
	}
	
	public void onTableClicked(String caseNo){
		checkboxi();
		String diagSql = DIAG_SQL.replace("#", caseNo);
		 System.out.println("diuagSql="+diagSql);
		TParm diagRec = new TParm(TJDODBTool.getInstance().select(diagSql));
		if (diagRec.getErrCode() < 0) {
			caseHistoryControl.messageBox_("查询诊断信息失败");
			return;
		}
		caseHistoryControl.diagTable.setParmValue(diagRec);
		
		String medSql = MED_SQL.replace("#", caseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		if (med.getErrCode() < 0) {
			caseHistoryControl.messageBox_("查询医嘱信息失败");
			return;
		}
		caseHistoryControl.medTable.setParmValue(med);
		
		TTabbedPane pane = (TTabbedPane) caseHistoryControl.getComponent("TTABBEDPANE");
		pane.setSelectedIndex(2);//西药页签
	}
	
	public void checkboxi(){
		caseHistoryControl.setValue("ZS", "N");
		caseHistoryControl.setValue("KS", "N");
		caseHistoryControl.setValue("TZ", "N");
		caseHistoryControl.setValue("JCJG", "N");
		caseHistoryControl.setValue("JY", "N");
		caseHistoryControl.setValue("ORDER", "Y");
		caseHistoryControl.setValue("ALLCHECK", "Y");
		caseHistoryControl.setValue("DIAG", "Y");
	}
	
	public void checkboxo(){
		caseHistoryControl.setValue("ZS", "Y");
		caseHistoryControl.setValue("KS", "Y");
		caseHistoryControl.setValue("TZ", "Y");
		caseHistoryControl.setValue("JCJG", "Y");
		caseHistoryControl.setValue("JY", "Y");
		caseHistoryControl.setValue("ORDER", "Y");
		caseHistoryControl.setValue("ALLCHECK", "Y");
		caseHistoryControl.setValue("DIAG", "Y");
	}
	
	public static final String SQL = 
		" SELECT ADM_DATE," +
		" REALDR_CODE DR_CODE," +
		" CASE_NO," +
		" ADM_TYPE," +
		" CASE" +
		" WHEN ADM_TYPE IN ('O', 'E') THEN '门'" +
		" WHEN ADM_TYPE = 'I' THEN '住'" +
		" END" +
		" OI" +
		" FROM REG_PATADM" +
		" WHERE MR_NO IN (#) AND SEE_DR_FLG != 'N'" +
		" UNION" +
		" SELECT IN_DATE," +
		" VS_DR_CODE," +
		" CASE_NO," +
		" 'I' ADM_TYPE," +
		" '住' IO" +
		" FROM ADM_INP" +
		" WHERE MR_NO IN (#)" +
		" ORDER BY OI, ADM_DATE DESC";
	
	private final String DIAG_SQL =
		" SELECT A.CASE_NO," +
		" A.ICD_TYPE," +
		" A.ICD_CODE," +
		" A.MAINDIAG_FLG MAIN_DIAG_FLG," +
		" 'I' ADM_TYPE," +
		" A.DESCRIPTION DIAG_NOTE," +
		" A.OPT_USER DR_CODE," +
		" A.OPT_DATE ORDER_DATE," +
		" 0 FILE_NO," +
		" A.OPT_USER," +
		" A.OPT_DATE," +
		" A.OPT_TERM," +
		" B.ICD_CHN_DESC," +
		" B.ICD_ENG_DESC" +
		" FROM ADM_INPDIAG A, SYS_DIAGNOSIS B" +
		" WHERE     A.CASE_NO = '#'" +
		" AND A.IO_TYPE = 'O'" +
		" AND A.ICD_CODE = B.ICD_CODE" +
		" ORDER BY A.MAINDIAG_FLG DESC, A.ICD_CODE";
	
	private final String MED_SQL =
		" SELECT 'Y' AS USE," +
		" B.OPD_FIT_FLG," +
		" B.EMG_FIT_FLG," +
		" B.ACTIVE_FLG," +
		" A.ORDER_CODE," +
		" A.LINKMAIN_FLG," +
		" A.LINK_NO," +
		" A.ORDER_DESC," +
		" A.MEDI_QTY," +
		" A.MEDI_UNIT," +
		" A.FREQ_CODE," +
		" A.ROUTE_CODE," +
		" A.TAKE_DAYS," +
		" A.DISPENSE_QTY," +
		" A.RELEASE_FLG," +
		" A.GIVEBOX_FLG," +
		" A.DISPENSE_UNIT," +
		" A.EXEC_DEPT_CODE," +
		" A.DR_NOTE," +
		" A.NS_NOTE," +
		" A.URGENT_FLG," +
		" A.INSPAY_TYPE," +
		" B.ORDER_CODE AS ORDER_CODE_FEE" +
		" FROM odi_order A, SYS_FEE B" +
		" WHERE     A.ORDER_CODE = B.ORDER_CODE(+)" +
		" AND A.CASE_NO = '#'" +
		" AND a.rx_kind = 'DS'";

}
                                                  