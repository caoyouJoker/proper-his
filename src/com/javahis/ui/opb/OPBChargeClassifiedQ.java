package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:门诊收费查询
 * </p>
 * 
 * <p>
 * Description:门诊收费查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author yanmm
 * @version 1.0
 */
public class OPBChargeClassifiedQ extends TControl {

	// TWORD
	private TWord word;
	String P_DATE="";
	TParm rParm = new TParm();
	/**
	 * 初始化
	 */
	public void onInit() {
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("START_TIME", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		word = (TWord) this.getComponent("WORD");
		
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String startTime = this.getText("START_TIME");
		String endTime = this.getText("END_TIME");
		
		P_DATE = StringTool.getString((Timestamp)this.getValue("START_TIME"),"yyyy/MM/dd HH:mm:ss") +
		            " ～ "+StringTool.getString((Timestamp)this.getValue("END_TIME"),"yyyy/MM/dd HH:mm:ss");
		
//		String timeS = startTime.substring(0, 10).replaceAll("/", "");
//		String timeE = endTime.substring(0, 10).replaceAll("/", "");

		if (startTime.length() == 0) {
			messageBox("请选择查询时间!");
			return;
		}
		// 门诊-票据-医保:
		String sql1 = " SELECT  COUNT(B.CASE_NO) COUNT FROM BIL_INVRCP A,BIL_REG_RECP B,REG_PATADM C WHERE A.PRINT_DATE BETWEEN TO_DATE('"
				+ startTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND A.CANCEL_FLG = '0' AND A.ADM_TYPE ='O' AND A.RECP_TYPE='REG' AND"
				+ " A.RECEIPT_NO=B.RECEIPT_NO AND B.CASE_NO=C.CASE_NO AND C.INS_PAT_TYPE IS NOT NULL ";
		// 门诊-票据-自费:
		String sql2 = " SELECT  COUNT(B.CASE_NO) COUNT FROM BIL_INVRCP A,BIL_REG_RECP B,REG_PATADM C WHERE B.PRINT_DATE BETWEEN TO_DATE('"
				+ startTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND A.CANCEL_FLG = '0' AND A.ADM_TYPE ='O' AND A.RECP_TYPE='REG' AND "
				+ "A.RECEIPT_NO=B.RECEIPT_NO AND B.CASE_NO=C.CASE_NO AND C.INS_PAT_TYPE IS NULL ";
		// 门诊-挂号方式-人工:
		String sql3 = " SELECT COUNT (A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL"
				+ " AND B.CASH_CODE <> 'QeApp' AND A.ADM_TYPE ='O'  AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime + "','YYYY/MM/DD HH24:MI:SS')";
		// 门诊-挂号方式-Q医:
		String sql4 = " SELECT COUNT (A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL"
				+ " AND B.CASH_CODE = 'QeApp' AND A.ADM_TYPE ='O' AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime + "','YYYY/MM/DD HH24:MI:SS')";
		// 门诊-预约方式-预约:
		String sql5 = " SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND A.ADM_TYPE ='O' AND A.APPT_CODE ='Y' ";
		// 门诊-预约方式-非预约:
		String sql6 = " SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND A.ADM_TYPE ='O' AND A.APPT_CODE ='N' AND A.ARRIVE_FLG='Y' ";
		// 门诊-医保:
		String sql7 = "SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS')  AND A.ADM_TYPE ='O' AND A.INS_PAT_TYPE IS NOT NULL";
		// 门诊-非医保-普通门诊:
		String sql8 = "SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS') AND A.ADM_TYPE ='O' AND A.INS_PAT_TYPE IS NULL";
		// 门诊-非医保-先天病筛查:
		String sql9 = " SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS')  AND A.ADM_TYPE ='O' AND A.INS_PAT_TYPE IS NULL AND A.CTZ2_CODE ='61' ";
		// 急诊-医保:
		String sql10 = " SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS')  AND A.ADM_TYPE ='E' AND A.INS_PAT_TYPE IS NOT NULL  ";
		// 急诊-非医保:
		String sql11 = " SELECT COUNT(A.CASE_NO) COUNT FROM REG_PATADM A, BIL_REG_RECP B WHERE A.CASE_NO = B.CASE_NO"
				+ " AND B.RESET_RECEIPT_NO IS NULL AND B.AR_AMT >=0 AND A.REGCAN_USER IS NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('"
				+ endTime
				+ "','YYYY/MM/DD HH24:MI:SS')  AND A.ADM_TYPE ='E' AND A.INS_PAT_TYPE IS NULL ";

		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
		TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));
		TParm result5 = new TParm(TJDODBTool.getInstance().select(sql5));
		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
		TParm result7 = new TParm(TJDODBTool.getInstance().select(sql7));
		TParm result8 = new TParm(TJDODBTool.getInstance().select(sql8));
		TParm result9 = new TParm(TJDODBTool.getInstance().select(sql9));
		TParm result10 = new TParm(TJDODBTool.getInstance().select(sql10));
		TParm result11 = new TParm(TJDODBTool.getInstance().select(sql11));
	
		TParm parm = new TParm();
		parm.addData("SYSTEM", "COLUMNS", "TABLE1");
		parm.addData("SYSTEM", "COLUMNS", "TABLE2");
		parm.addData("SYSTEM", "COLUMNS", "TABLE3");
		parm.addData("SYSTEM", "COLUMNS", "TABLE4");
		parm.addData("SYSTEM", "COLUMNS", "TABLE5");
		parm.addData("SYSTEM", "COLUMNS", "TABLE6");
		parm.addData("SYSTEM", "COLUMNS", "TABLE7");
		parm.addData("SYSTEM", "COLUMNS", "TABLE8");
		parm.addData("SYSTEM", "COLUMNS", "TABLE9");
		parm.addData("SYSTEM", "COLUMNS", "TABLE10");
		parm.addData("SYSTEM", "COLUMNS", "TABLE11");
		parm.addData("TABLE1", result1.getValue("COUNT", 0));
		parm.addData("TABLE2", result2.getValue("COUNT", 0));
		parm.addData("TABLE3", result3.getValue("COUNT", 0));
		parm.addData("TABLE4", result4.getValue("COUNT", 0));
		parm.addData("TABLE5", result5.getValue("COUNT", 0));
		parm.addData("TABLE6", result6.getValue("COUNT", 0));
		parm.addData("TABLE7", result7.getValue("COUNT", 0));
		parm.addData("TABLE8", result8.getValue("COUNT", 0));
		parm.addData("TABLE9", result9.getValue("COUNT", 0));
		parm.addData("TABLE10", result10.getValue("COUNT", 0));
		parm.addData("TABLE11", result11.getValue("COUNT", 0));
		parm.setCount(1);
		rParm.setData("TABLE", parm.getData());
		Timestamp date = SystemTool.getInstance().getDate();
        rParm.setData("DATE","TEXT",P_DATE);
        rParm.setData("DATE_U","TEXT",date.toString().substring(0, 10).replace('-', '/'));
        rParm.setData("USER_U","TEXT",Operator.getName());
		// this.messageBox("parm:::" + parm.getData());
		// 获得错误信息消息
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
			return;
		}
	        word.setWordParameter(rParm);
			word.setPreview(true);
		word.setFileName("%ROOT%\\config\\prt\\opb\\OPBChargeClassifiedQ.jhw");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		onInit() ;
//		Timestamp date = SystemTool.getInstance().getDate();
//		setValue("START_TIME",
//				date.toString().substring(0, 19).replace('-', '/'));
		word.onNewFile();
		word.update();
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (word == null) {
			return;
		}
		if (StringUtil.isNullString(word.getFileName())) {
			return;
		}
	        this.openPrintWindow("%ROOT%\\config\\prt\\opb\\OPBChargeClassifiedQ.jhw",rParm);
	
	}

}
