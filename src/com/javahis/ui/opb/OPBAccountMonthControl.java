package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 门诊收费数据
 * </p>
 * 
 * <p>
 * Description: 门诊收费数据
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author zhangp
 * @version 1.0
 */
public class OPBAccountMonthControl extends TControl {

	private static TTable table1;
	private static TTable table3;

	// private static String seq1 = "";
	// private static String seq3 = "";

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		table1 = (TTable) getComponent("TABLE1");
		table3 = (TTable) getComponent("TABLE3");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("REGION_CODE", Operator.getRegion());
		setValue("DEPT", Operator.getDept());
		 //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// boolean flag = false;
		TRadioButton tb0 = (TRadioButton) getComponent("YES");
		TRadioButton tb1 = (TRadioButton) getComponent("NO");
		TRadioButton tb2 = (TRadioButton) getComponent("ALL");
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String s = " ";
		if (tb0.isSelected()) {
			s = " AND A.ADM_TYPE='H' ";
			// flag = true;
		}
		if (tb1.isSelected()) {
			s = " AND A.ADM_TYPE != 'H' ";
			// flag = false;
		}
		if (tb2.isSelected()) {
			s = " ";
			// flag = false;
		}
		String deptTable="";
		String deptWhere="";
		if (!getValue("DEPT").equals("")) {
			deptTable = " , SYS_OPERATOR_DEPT B";
			deptWhere = " AND A.ACCOUNT_USER = B.USER_ID AND B.DEPT_CODE = '"
					+ getValue("DEPT") + "' AND B.MAIN_FLG = 'Y' ";
		}
		String sql1 = "SELECT 'Y' FLG,A.ACCOUNT_SEQ FROM BIL_ACCOUNT A "+deptTable+
		" WHERE ACCOUNT_TYPE = 'REG' AND ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ date_s
				+ "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ date_e
				+ "','YYYYMMDDHH24MISS')" + s +deptWhere+ " ORDER BY ACCOUNT_SEQ";
		String sql3 = "SELECT 'Y' FLG,A.ACCOUNT_SEQ FROM BIL_ACCOUNT A "+deptTable+" WHERE ACCOUNT_TYPE = 'OPB' AND ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ date_s
				+ "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ date_e
				+ "','YYYYMMDDHH24MISS')" + s +deptWhere+ " ORDER BY ACCOUNT_SEQ";
		TParm result = new TParm();
		table1.removeRowAll();
		// if(flag) {
		// result = new TParm(TJDODBTool.getInstance().select(sql3));
		// table3.setParmValue(result);
		// result = new TParm(TJDODBTool.getInstance().select(sql4));
		// table4.setParmValue(result);
		// }else {
		result = new TParm(TJDODBTool.getInstance().select(sql1));
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql3));
		if(result.getCount()<=0&&result1.getCount()<=0){
			this.messageBox("没有查询的数据");
		}
		table1.setParmValue(result);
		table3.setParmValue(result1);
		// }
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		TParm printParm = getPrintParm();
		if (printParm == null) {
			return;
		}
		String date = SystemTool.getInstance().getDate().toString();
		TTextFormat tf = (TTextFormat) getComponent("DEPT");
		printParm.setData("DEPT", "TEXT", "科室: " + tf.getText());
		printParm.setData("PRINTDATE", "TEXT", "打印日期: " + date.substring(0, 4)
				+ "/" + date.substring(5, 7) + "/" + date.substring(8, 10));
		this.openPrintWindow("%ROOT%\\config\\prt\\OPB\\OPBAccountMonth.jhw",
				printParm);

	}

	public void onExport() {
		TTable table = (TTable) getComponent("TABLE");
		TParm parm = getPrintParm();
		TParm exportParm = new TParm();
		// 行1
		exportParm.addData("C0", "挂号费");
		exportParm.addData("C1", parm.getValue("REG_FEE_REAL"));
		exportParm.addData("C2", parm.getValue("REG_FEE_REAL_INS"));
		exportParm.addData("C3", parm.getValue("REG_FEE_REAL_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行2
		exportParm.addData("C0", "诊察费");
		exportParm.addData("C1", parm.getValue("CLINIC_FEE_REAL"));
		exportParm.addData("C2", parm.getValue("CLINIC_FEE_REAL_INS"));
		exportParm.addData("C3", parm.getValue("CLINIC_FEE_REAL_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行3
		exportParm.addData("C0", "西药费");
		exportParm.addData("C1", parm.getValue("XY"));
		exportParm.addData("C2", parm.getValue("XY_INS"));
		exportParm.addData("C3", parm.getValue("XY_NOINS"));
		exportParm.addData("C4", "其中抗生素");
		exportParm.addData("C5", parm.getValue("KSS"));
		exportParm.addData("C6", parm.getValue("KSS_INS"));
		exportParm.addData("C7", parm.getValue("KSS_NOINS"));
		table.setParmValue(exportParm);
		// 行4
		exportParm.addData("C0", "中成药费");
		exportParm.addData("C1", parm.getValue("ZCY"));
		exportParm.addData("C2", parm.getValue("ZCY_INS"));
		exportParm.addData("C3", parm.getValue("ZCY_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行5
		exportParm.addData("C0", "检查费");
		exportParm.addData("C1", parm.getValue("JCF"));
		exportParm.addData("C2", parm.getValue("JCF_INS"));
		exportParm.addData("C3", parm.getValue("JCF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行6
		exportParm.addData("C0", "治疗费");
		exportParm.addData("C1", parm.getValue("ZLF"));
		exportParm.addData("C2", parm.getValue("ZLF_INS"));
		exportParm.addData("C3", parm.getValue("ZLF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行7
		exportParm.addData("C0", "放射费");
		exportParm.addData("C1", parm.getValue("FSF"));
		exportParm.addData("C2", parm.getValue("FSF_INS"));
		exportParm.addData("C3", parm.getValue("FSF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行8
		exportParm.addData("C0", "手术费");
		exportParm.addData("C1", parm.getValue("SSF"));
		exportParm.addData("C2", parm.getValue("SSF_INS"));
		exportParm.addData("C3", parm.getValue("SSF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行9
		exportParm.addData("C0", "化验费");
		exportParm.addData("C1", parm.getValue("HYF"));
		exportParm.addData("C2", parm.getValue("HYF_INS"));
		exportParm.addData("C3", parm.getValue("HYF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行10
		exportParm.addData("C0", "输血费");
		exportParm.addData("C1", parm.getValue("SXF"));
		exportParm.addData("C2", parm.getValue("SXF_INS"));
		exportParm.addData("C3", parm.getValue("SXF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行11
		exportParm.addData("C0", "输氧费");
		exportParm.addData("C1", parm.getValue("SYF"));
		exportParm.addData("C2", parm.getValue("SYF_INS"));
		exportParm.addData("C3", parm.getValue("SYF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行12
		exportParm.addData("C0", "体检费");
		exportParm.addData("C1", parm.getValue("TJF"));
		exportParm.addData("C2", parm.getValue("TJF_INS"));
		exportParm.addData("C3", parm.getValue("TJF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行13
		exportParm.addData("C0", "观察床费");
		exportParm.addData("C1", parm.getValue("GCCF"));
		exportParm.addData("C2", parm.getValue("GCCF_INS"));
		exportParm.addData("C3", parm.getValue("GCCF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行14
		exportParm.addData("C0", "自费部分");
		exportParm.addData("C1", parm.getValue("ZFBF"));
		exportParm.addData("C2", parm.getValue("ZFBF_INS"));
		exportParm.addData("C3", parm.getValue("ZFBF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行15
		exportParm.addData("C0", "CT");
		exportParm.addData("C1", parm.getValue("CT"));
		exportParm.addData("C2", parm.getValue("CT_INS"));
		exportParm.addData("C3", parm.getValue("CT_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行16
		exportParm.addData("C0", "MR");
		exportParm.addData("C1", parm.getValue("MR"));
		exportParm.addData("C2", parm.getValue("MR_INS"));
		exportParm.addData("C3", parm.getValue("MR_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		// 行17
		exportParm.addData("C0", "材料费");
		exportParm.addData("C1", parm.getValue("CLF"));
		exportParm.addData("C2", parm.getValue("CLF_INS"));
		exportParm.addData("C3", parm.getValue("CLF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		
		// 行18
		exportParm.addData("C0", "诊查费");
		exportParm.addData("C1", parm.getValue("ZCF"));
		exportParm.addData("C2", parm.getValue("ZCF_INS"));
		exportParm.addData("C3", parm.getValue("ZCF_NOINS"));
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		
		// 行19
		exportParm.addData("C0", "合计");
		exportParm.addData("C1", parm.getValue("TOT_AMT"));
		exportParm.addData("C2", "");
		exportParm.addData("C3", "");
		exportParm.addData("C4", "");
		exportParm.addData("C5", "");
		exportParm.addData("C6", "");
		exportParm.addData("C7", "");
		table.setParmValue(exportParm);
		TTextFormat tf = (TTextFormat) getComponent("DEPT");
		ExportExcelUtil.getInstance().exportExcel(table,
				tf.getText() + "门诊收费数据表");
	}

	
	
	
	
	private TParm getPrintParm() {
		//====start======add by kangy  20160930===
		TParm parm=new TParm();
		String s = "";
		TRadioButton tb0 = (TRadioButton) getComponent("YES");
		TRadioButton tb1 = (TRadioButton) getComponent("NO");
		TRadioButton tb2 = (TRadioButton) getComponent("ALL");
		table1.acceptText();
		table3.acceptText();
		TParm tableParm1 = table1.getParmValue();
		TParm tableParm3 = table3.getParmValue();
		if (tb0.isSelected()) {
			s = " AND A.ADM_TYPE='H' ";
			// flag = true;
			// seq2 = "1";
		}
		if (tb1.isSelected()) {
			s = " AND A.ADM_TYPE != 'H' ";
			// flag = false;
		}
		if (tb2.isSelected()) {
			s = " ";
			// flag = false;
		}
		String regSeq = "";
		String opbSeq = "";
		for (int i = 0; i < tableParm1.getCount(); i++) {
			if (null != tableParm1.getValue("FLG", i)
					&& tableParm1.getValue("FLG", i).equals("Y")) {
				regSeq += "'"+tableParm1.getValue("ACCOUNT_SEQ", i) + "',";
			}
		}
		for (int i = 0; i < tableParm3.getCount(); i++) {
			if (null != tableParm3.getValue("FLG", i)
					&& tableParm3.getValue("FLG", i).equals("Y")) {
				opbSeq += "'"+tableParm3.getValue("ACCOUNT_SEQ", i) + "',";
			}
		}
		TParm printParm = new TParm();
		if (regSeq.length() > 0 || opbSeq.length() > 0) {
			try {
				if (regSeq.length() > 0) {
					if (!tb0.isSelected()) {
						regSeq = regSeq.substring(0, regSeq.length() - 1);
					} else {
						regSeq = "";
					}
				}
				if (opbSeq.length() > 0) {
					opbSeq = opbSeq.substring(0, opbSeq.length() - 1);
				}
			} catch (StringIndexOutOfBoundsException e) {
				messageBox("无数据！");
				return null;
			}
			
	        String sqls = 
	        	"SELECT " +
	        	" A.INS_CROWD_TYPE," +
	        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
	        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
	        	" SUM(A.ARMY_AI_AMT)     AS ARMY_AI_AMT," +
	        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
	        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
	        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
	        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT," +
	        	" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT " +
	        	" FROM " +
	        	" INS_OPD A,BIL_REG_RECP B " +
	        	"WHERE " +
	        	" A.REGION_CODE = '"+Operator.getRegion()+"' " +
	        	" AND A.REGION_CODE  = B.REGION_CODE " +
	        	" AND A.CASE_NO = B.CASE_NO " +
	        	" AND A.CONFIRM_NO NOT LIKE '*%' " +
	        	" AND A.INV_NO = B.PRINT_NO " +
	        	" AND B.ACCOUNT_SEQ IN ("+regSeq+") " +
	        	" AND B.AR_AMT>0 " +
	        	"GROUP BY  " +
	        	" INS_CROWD_TYPE " +
	        	"ORDER BY " +
	        	" INS_CROWD_TYPE";
	        TParm insParm = new TParm(TJDODBTool.getInstance().select(sqls));
	        double payInsNhiS = 0;
	        double payInsHelpS = 0;
	        double unreimAmtY = 0;
	        double unreimAmtT = 0;
	        double unreimAmtS = 0;
	        double    payInsCardS = 0;
	        double   payInsS = 0;
	        double   payInsCardT = 0;
	        double  payInsT = 0;
	        double  payInsCardY = 0;
	        double  payInsY = 0;
	        double payInsNhiT = 0;
	        double payInsHelpT = 0;
			double payInsNhiY = 0;
			double payInsHelpY = 0;
			double payInsTotY = 0;
			double payInsTotT = 0;
			double payInsTotS = 0;
			if(insParm.getCount()>0){
				for (int i = 0; i < insParm.getCount(); i++) {
					if(insParm.getData("INS_CROWD_TYPE", i).equals("1")){
						//城职INS_CROWD_TYPE = ‘1’
						//个人账户=ACCOUNT_PAY_AMT
						//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
						payInsCardY = insParm.getDouble("ACCOUNT_PAY_AMT", i);
						payInsNhiY = insParm.getDouble("OTOT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
											insParm.getDouble("TOTAL_AGENT_AMT", i) + insParm.getDouble("FLG_AGENT_AMT", i) + 
											insParm.getDouble("SERVANT_AMT", i);
						parm.setData("PAY_INS_CARD_Y", "TEXT", StringTool.round(payInsCardY,2));
						parm.setData("PAY_INS_NHI_Y", "TEXT", StringTool.round(payInsNhiY,2));
					}
					if(insParm.getData("INS_CROWD_TYPE", i).equals("2")){
//						城居INS_CROWD_TYPE = ‘2’
//						救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//						统筹=TOTAL_AGENT_AMT
						payInsHelpY = insParm.getDouble("FLG_AGENT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
											insParm.getDouble("SERVANT_AMT", i) + insParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
						payInsY = insParm.getDouble("TOTAL_AGENT_AMT", i);
						parm.setData("PAY_INS_HELP_Y", "TEXT", StringTool.round(payInsHelpY,2));
						parm.setData("PAY_INS_Y", "TEXT", StringTool.round(payInsY,2));
					}
					unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
				}
			}
	        sqls = 
	        	"SELECT " +
	        	" A.INS_CROWD_TYPE," +
	        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
	        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
	        	" SUM(A.ARMY_AI_AMT) ARMY_AI_AMT," +
	        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
	        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
	        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
	        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT," +
	        	" SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT " +
	        	" FROM " +
	        	" INS_OPD A,BIL_REG_RECP B " +
	        	"WHERE A.REGION_CODE = '"+Operator.getRegion()+"' " +
	        	" AND A.REGION_CODE  = B.REGION_CODE " +
	        	" AND A.CASE_NO = B.CASE_NO " +
	        	" AND A.INV_NO = B.PRINT_NO " +
	        	" AND A.CONFIRM_NO  LIKE '*%' " +
	        	" AND B.ACCOUNT_SEQ IN ("+regSeq+") " +
	        	" AND B.AR_AMT<0 " +
	        	"GROUP BY " +
	        	" INS_CROWD_TYPE " +
	        	"ORDER BY " +
	        	" INS_CROWD_TYPE";
	        TParm insParmT = new TParm(TJDODBTool.getInstance().select(sqls));
	        if(insParmT.getCount()>0){
	        	for (int i = 0; i < insParmT.getCount(); i++) {
	            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("1")){
	            		//城职INS_CROWD_TYPE = ‘1’
	            		//个人账户=ACCOUNT_PAY_AMT
	            		//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
	            		payInsCardT = insParmT.getDouble("ACCOUNT_PAY_AMT", i);
	            		payInsNhiT = insParmT.getDouble("OTOT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
	            		insParmT.getDouble("TOTAL_AGENT_AMT", i) + insParmT.getDouble("FLG_AGENT_AMT", i) + 
	            		insParmT.getDouble("SERVANT_AMT", i);
	            		parm.setData("PAY_INS_CARD_T", "TEXT", Math.abs(StringTool.round(payInsCardT,2)));
	            		parm.setData("PAY_INS_NHI_T", "TEXT", Math.abs(StringTool.round(payInsNhiT,2)));
	            	}
	            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("2")){
//	    				城居INS_CROWD_TYPE = ‘2’
//	    				救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//	    				统筹=TOTAL_AGENT_AMT
	            		payInsHelpT = insParmT.getDouble("FLG_AGENT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
	            		insParmT.getDouble("SERVANT_AMT", i) + insParmT.getDouble("ILLNESS_SUBSIDY_AMT", i);
	            		payInsT = insParmT.getDouble("TOTAL_AGENT_AMT", i);
	            		parm.setData("PAY_INS_HELP_T", "TEXT", Math.abs(StringTool.round(payInsHelpT,2)));
	            		parm.setData("PAY_INS_T", "TEXT", Math.abs(StringTool.round(payInsT,2)));
	            	}
	            	unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
	            }
	        }
	        unreimAmtS = unreimAmtY + unreimAmtT;
	        payInsCardS = payInsCardY + payInsCardT ;
	        payInsS = payInsY + payInsT;
	        payInsHelpS = payInsHelpY + payInsHelpT;
	        payInsNhiS = payInsNhiY + payInsNhiT;
//			医保金额小计= 个人账户+社保基金支付+救助金额+统筹-基金未报销金额
	        payInsTotY = payInsCardY + payInsY + payInsHelpY + payInsNhiY;
	        payInsTotT = payInsCardT + payInsT + payInsHelpT + payInsNhiT;
	        payInsTotS = payInsTotY + payInsTotT;
			//======end====add by kangy 20160930==
			
			
			// messageBox("");
			String sql = " SELECT SUM (REG_FEE_REAL) REG_FEE_REAL, SUM (CLINIC_FEE_REAL) CLINIC_FEE_REAL "
					+
					// " FROM BIL_REG_RECP A " + deptTable +
					" FROM BIL_REG_RECP A "+
					// " WHERE (A.ACCOUNT_SEQ >= " + seq1 +
					// " AND A.ACCOUNT_SEQ <= " + seq2 + ")" +
					" WHERE A.ACCOUNT_SEQ IN (" + regSeq + ")" + 
					// deptWhere +
					" AND A.PAY_INS_CARD = 0";
			TParm regNoIns = new TParm(TJDODBTool.getInstance().select(sql));
			sql = " SELECT SUM (REG_FEE_REAL) REG_FEE_REAL, SUM (CLINIC_FEE_REAL) CLINIC_FEE_REAL "
					+
					// " FROM BIL_REG_RECP A " + deptTable +
					" FROM BIL_REG_RECP A " + 
					// " WHERE (A.ACCOUNT_SEQ >= " + seq1 +
					// " AND A.ACCOUNT_SEQ <= " + seq2 + ")"
					" WHERE A.ACCOUNT_SEQ IN (" + regSeq + ")" ;
			// + deptWhere
			TParm reg = new TParm(TJDODBTool.getInstance().select(sql));
			sql = " SELECT SUM(A.CHARGE01+A.CHARGE02) XY,SUM (A.CHARGE01) KSS, SUM (A.CHARGE02) FKSS, SUM (A.CHARGE03) ZCY,"
					+ " SUM (A.CHARGE04) ZCAY, SUM (A.CHARGE05) JCF, SUM (A.CHARGE06) ZLF,"
					+ " SUM (A.CHARGE07) FSF, SUM (A.CHARGE08) SSF, SUM (A.CHARGE09) SXF,"
					+ " SUM (A.CHARGE10) HYF, SUM (A.CHARGE11) TJF, SUM (A.CHARGE12) SQYL,"
					+ " SUM (A.CHARGE13) GCCF, SUM (A.CHARGE14) CT, SUM (A.CHARGE15) MR,"
					+ " SUM (A.CHARGE16) ZFBF, SUM (A.CHARGE17) CLF, SUM (A.CHARGE18) SYF,"
					+ " SUM (A.CHARGE19) ZCF, SUM (A.CHARGE20), SUM (A.CHARGE21), SUM (A.CHARGE22),"
					+ " SUM (A.CHARGE23), SUM (A.CHARGE24), SUM (A.CHARGE25), SUM (A.CHARGE26),"
					+ " SUM (A.CHARGE27), SUM (A.CHARGE28), SUM (A.CHARGE29), SUM (A.CHARGE30)"
					+
					// " FROM BIL_OPB_RECP A " + deptTable +
					" FROM BIL_OPB_RECP A " + 
					// " WHERE (A.ACCOUNT_SEQ >= " + seq3 +
					// " AND A.ACCOUNT_SEQ <= " + seq4 + ")"
					" WHERE A.ACCOUNT_SEQ IN (" + opbSeq + ")" ;
			// + deptOPBWhere
			TParm opb = new TParm(TJDODBTool.getInstance().select(sql));
			sql = " SELECT SUM(A.CHARGE01+A.CHARGE02) XY,SUM (A.CHARGE01) KSS, SUM (A.CHARGE02) FKSS, SUM (A.CHARGE03) ZCY,"
					+ " SUM (A.CHARGE04) ZCAY, SUM (A.CHARGE05) JCF, SUM (A.CHARGE06) ZLF,"
					+ " SUM (A.CHARGE07) FSF, SUM (A.CHARGE08) SSF, SUM (A.CHARGE09) SXF,"
					+ " SUM (A.CHARGE10) HYF, SUM (A.CHARGE11) TJF, SUM (A.CHARGE12) SQYL,"
					+ " SUM (A.CHARGE13) GCCF, SUM (A.CHARGE14) CT, SUM (A.CHARGE15) MR,"
					+ " SUM (A.CHARGE16) ZFBF, SUM (A.CHARGE17) CLF, SUM (A.CHARGE18) SYF,"
					+ " SUM (A.CHARGE19) ZCF, SUM (A.CHARGE20), SUM (A.CHARGE21), SUM (A.CHARGE22),"
					+ " SUM (A.CHARGE23), SUM (A.CHARGE24), SUM (A.CHARGE25), SUM (A.CHARGE26),"
					+ " SUM (A.CHARGE27), SUM (A.CHARGE28), SUM (A.CHARGE29), SUM (A.CHARGE30)"
					+
					// " FROM BIL_OPB_RECP A " + deptTable +
					" FROM BIL_OPB_RECP A " + 
					// " WHERE (A.ACCOUNT_SEQ >= " + seq3 +
					// " AND A.ACCOUNT_SEQ <= " + seq4 + ")" +
					" WHERE A.ACCOUNT_SEQ IN (" + opbSeq + ")" +
					// deptOPBWhere +
					" AND PAY_INS_CARD = 0";
			// System.out.println(sql+"dfdf");
			TParm opbNoIns = new TParm(TJDODBTool.getInstance().select(sql));
			double reg_fee_real = StringTool.round(reg.getDouble(
					"REG_FEE_REAL", 0), 2);
			double clinic_fee_real = StringTool.round(reg.getDouble(
					"CLINIC_FEE_REAL", 0), 2);
			double reg_fee_real_noins = StringTool.round(regNoIns.getDouble(
					"REG_FEE_REAL", 0), 2);
			double clinic_fee_real_noins = StringTool.round(regNoIns.getDouble(
					"CLINIC_FEE_REAL", 0), 2);
			double reg_fee_real_ins = reg_fee_real - reg_fee_real_noins;
			double clinic_fee_real_ins = clinic_fee_real
					- clinic_fee_real_noins;
			double xy = StringTool.round(opb.getDouble("XY", 0), 2);
			double kss = StringTool.round(opb.getDouble("KSS", 0), 2);
			double fkss = StringTool.round(opb.getDouble("FKSS", 0), 2);
			double zcy = StringTool.round(opb.getDouble("ZCY", 0), 2);
			double zcay = StringTool.round(opb.getDouble("ZCAY", 0), 2);
			double jcf = StringTool.round(opb.getDouble("JCF", 0), 2);
			double zlf = StringTool.round(opb.getDouble("ZLF", 0), 2);
			double fsf = StringTool.round(opb.getDouble("FSF", 0), 2);
			double ssf = StringTool.round(opb.getDouble("SSF", 0), 2);
			double sxf = StringTool.round(opb.getDouble("SXF", 0), 2);
			double hyf = StringTool.round(opb.getDouble("HYF", 0), 2);
			double tjf = StringTool.round(opb.getDouble("TJF", 0), 2);
			double sqyl = StringTool.round(opb.getDouble("SQYL", 0), 2);
			double gccf = StringTool.round(opb.getDouble("GCCF", 0), 2);
			double ct = StringTool.round(opb.getDouble("CT", 0), 2);
			double mr = StringTool.round(opb.getDouble("MR", 0), 2);
			double zfbf = StringTool.round(opb.getDouble("ZFBF", 0), 2);
			double clf = StringTool.round(opb.getDouble("CLF", 0), 2);
			double syf = StringTool.round(opb.getDouble("SYF", 0), 2);
			double zcf = StringTool.round(opb.getDouble("ZCF", 0), 2);
			double xy_noins = StringTool.round(opbNoIns.getDouble("XY", 0), 2);
			double kss_noins = StringTool
					.round(opbNoIns.getDouble("KSS", 0), 2);
			double fkss_noins = StringTool.round(opbNoIns.getDouble("FKSS", 0),
					2);
			double zcy_noins = StringTool
					.round(opbNoIns.getDouble("ZCY", 0), 2);
			double zcay_noins = StringTool.round(opbNoIns.getDouble("ZCAY", 0),
					2);
			double jcf_noins = StringTool
					.round(opbNoIns.getDouble("JCF", 0), 2);
			double zlf_noins = StringTool
					.round(opbNoIns.getDouble("ZLF", 0), 2);
			double fsf_noins = StringTool
					.round(opbNoIns.getDouble("FSF", 0), 2);
			double ssf_noins = StringTool
					.round(opbNoIns.getDouble("SSF", 0), 2);
			double sxf_noins = StringTool
					.round(opbNoIns.getDouble("SXF", 0), 2);
			double hyf_noins = StringTool
					.round(opbNoIns.getDouble("HYF", 0), 2);
			double tjf_noins = StringTool
					.round(opbNoIns.getDouble("TJF", 0), 2);
			double sqyl_noins = StringTool.round(opbNoIns.getDouble("SQYL", 0),
					2);
			double gccf_noins = StringTool.round(opbNoIns.getDouble("GCCF", 0),
					2);
			double ct_noins = StringTool.round(opbNoIns.getDouble("CT", 0), 2);
			double mr_noins = StringTool.round(opbNoIns.getDouble("MR", 0), 2);
			double zfbf_noins = StringTool.round(opbNoIns.getDouble("ZFBF", 0),
					2);
			double clf_noins = StringTool
					.round(opbNoIns.getDouble("CLF", 0), 2);
			double syf_noins = StringTool
					.round(opbNoIns.getDouble("SYF", 0), 2);
			double zcf_noins = StringTool
					.round(opbNoIns.getDouble("ZCF", 0), 2);
			double xy_ins = xy - xy_noins;
			double kss_ins = kss - kss_noins;
			double fkss_ins = fkss - fkss_noins;
			double zcy_ins = zcy - zcy_noins;
			double zcay_ins = zcay - zcay_noins;
			double jcf_ins = jcf - jcf_noins;
			double zlf_ins = zlf - zlf_noins;
			double fsf_ins = fsf - fsf_noins;
			double ssf_ins = ssf - ssf_noins;
			double sxf_ins = sxf - sxf_noins;
			double hyf_ins = hyf - hyf_noins;
			double tjf_ins = tjf - tjf_noins;
			double sqyl_ins = sqyl - sqyl_noins;
			double gccf_ins = gccf - gccf_noins;
			double ct_ins = ct - ct_noins;
			double mr_ins = mr - mr_noins;
			double zfbf_ins = zfbf - zfbf_noins;
			double clf_ins = clf - clf_noins;
			double syf_ins = syf - syf_noins;
			double zcf_ins = zcf - zcf_noins;
			double tot_amt = reg_fee_real + clinic_fee_real + xy + zcy + zcay
					+ jcf + zlf + fsf + ssf + sxf + hyf + tjf + sqyl + gccf
					+ ct + mr + zfbf + clf + syf + zcf;
			DecimalFormat df = new DecimalFormat("#########0.00");
			printParm
					.setData("REG_FEE_REAL", StringTool.round(reg_fee_real, 2));
			printParm.setData("CLINIC_FEE_REAL", StringTool.round(
					clinic_fee_real, 2));
			printParm.setData("REG_FEE_REAL_NOINS", StringTool.round(
					reg_fee_real_noins, 2));
			printParm.setData("CLINIC_FEE_REAL_NOINS", StringTool.round(
					clinic_fee_real-payInsTotS	, 2));//modify by kangy
			printParm.setData("REG_FEE_REAL_INS", StringTool.round(
					reg_fee_real_ins, 2));
			printParm.setData("CLINIC_FEE_REAL_INS", StringTool.round(
					payInsTotS, 2));//modify by kangy
			printParm.setData("XY", StringTool.round(xy, 2));
			printParm.setData("KSS", StringTool.round(kss, 2));
			printParm.setData("FKSS", StringTool.round(fkss, 2));
			printParm.setData("ZCY", StringTool.round(zcy, 2));
			printParm.setData("ZCAY", StringTool.round(zcay, 2));
			printParm.setData("JCF", StringTool.round(jcf, 2));
			printParm.setData("ZLF", StringTool.round(zlf, 2));
			printParm.setData("FSF", StringTool.round(fsf, 2));
			printParm.setData("SSF", StringTool.round(ssf, 2));
			printParm.setData("SXF", StringTool.round(sxf, 2));
			printParm.setData("HYF", StringTool.round(hyf, 2));
			printParm.setData("TJF", StringTool.round(tjf, 2));
			printParm.setData("SQYL", StringTool.round(sqyl, 2));
			printParm.setData("GCCF", StringTool.round(gccf, 2));
			printParm.setData("CT", StringTool.round(ct, 2));
			printParm.setData("MR", StringTool.round(mr, 2));
			printParm.setData("ZFBF", StringTool.round(zfbf, 2));
			printParm.setData("CLF", StringTool.round(clf, 2));
			printParm.setData("SYF", StringTool.round(syf, 2));
			printParm.setData("ZCF", StringTool.round(zcf, 2));
			printParm.setData("XY_NOINS", StringTool.round(xy_noins, 2));
			printParm.setData("KSS_NOINS", StringTool.round(kss_noins, 2));
			printParm.setData("FKSS_NOINS", StringTool.round(fkss_noins, 2));
			printParm.setData("ZCY_NOINS", StringTool.round(zcy_noins, 2));
			printParm.setData("ZCAY_NOINS", StringTool.round(zcay_noins, 2));
			printParm.setData("JCF_NOINS", StringTool.round(jcf_noins, 2));
			printParm.setData("ZLF_NOINS", StringTool.round(zlf_noins, 2));
			printParm.setData("FSF_NOINS", StringTool.round(fsf_noins, 2));
			printParm.setData("SSF_NOINS", StringTool.round(ssf_noins, 2));
			printParm.setData("SXF_NOINS", StringTool.round(sxf_noins, 2));
			printParm.setData("HYF_NOINS", StringTool.round(hyf_noins, 2));
			printParm.setData("TJF_NOINS", StringTool.round(tjf_noins, 2));
			printParm.setData("SQYL_NOINS", StringTool.round(sqyl_noins, 2));
			printParm.setData("GCCF_NOINS", StringTool.round(gccf_noins, 2));
			printParm.setData("CT_NOINS", StringTool.round(ct_noins, 2));
			printParm.setData("MR_NOINS", StringTool.round(mr_noins, 2));
			printParm.setData("ZFBF_NOINS", StringTool.round(zfbf_noins, 2));
			printParm.setData("CLF_NOINS", StringTool.round(clf_noins, 2));
			printParm.setData("SYF_NOINS", StringTool.round(syf_noins, 2));
			printParm.setData("ZCF_NOINS", StringTool.round(zcf_noins, 2));
			printParm.setData("XY_INS", StringTool.round(xy_ins, 2));
			printParm.setData("KSS_INS", StringTool.round(kss_ins, 2));
			printParm.setData("FKSS_INS", StringTool.round(fkss_ins, 2));
			printParm.setData("ZCY_INS", StringTool.round(zcy_ins, 2));
			printParm.setData("ZCAY_INS", StringTool.round(zcay_ins, 2));
			printParm.setData("JCF_INS", StringTool.round(jcf_ins, 2));
			printParm.setData("ZLF_INS", StringTool.round(zlf_ins, 2));
			printParm.setData("FSF_INS", StringTool.round(fsf_ins, 2));
			printParm.setData("SSF_INS", StringTool.round(ssf_ins, 2));
			printParm.setData("SXF_INS", StringTool.round(sxf_ins, 2));
			printParm.setData("HYF_INS", StringTool.round(hyf_ins, 2));
			printParm.setData("TJF_INS", StringTool.round(tjf_ins, 2));
			printParm.setData("SQYL_INS", StringTool.round(sqyl_ins, 2));
			printParm.setData("GCCF_INS", StringTool.round(gccf_ins, 2));
			printParm.setData("CT_INS", StringTool.round(ct_ins, 2));
			printParm.setData("MR_INS", StringTool.round(mr_ins, 2));
			printParm.setData("ZFBF_INS", StringTool.round(zfbf_ins, 2));
			printParm.setData("CLF_INS", StringTool.round(clf_ins, 2));
			printParm.setData("SYF_INS", StringTool.round(syf_ins, 2));
			printParm.setData("ZCF_INS", StringTool.round(zcf_ins, 2));
			printParm.setData("TOT_AMT", df.format(tot_amt));
			printParm.setData("REG_SEQ", regSeq);
			printParm.setData("OPB_SEQ", opbSeq);
		} else {
			messageBox("请选择需要打印的表号");
			return null;
		}
		return printParm;
	}

	/**
	 * 医保明细打印
	 */
	public void onDetailPrint() {
		TParm printData = new TParm();
		// 获得医保明细
		printData = getInsDetailPrint();
		// 表头
		printData.setData("TITLE", "TEXT", "门诊医保明细表");

		// 打印日期
		String printDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy-MM-dd HH:mm:ss");
		printData.setData("PRINTDATE", "TEXT", printDate);

		// 收费员
		printData.setData("USER", "TEXT", Operator.getName());
		if (printData == null)
			return;
		this.openPrintWindow("%ROOT%\\config\\prt\\opb\\INSDetailPrint.jhw",
				printData);
	}

	/**
	 *得到医保明细
	 * 
	 * @param tableParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getInsDetailPrint() {
		TParm returnParm = new TParm();
		String s = "";
		TRadioButton tb0 = (TRadioButton) getComponent("YES");
		TRadioButton tb1 = (TRadioButton) getComponent("NO");
		TRadioButton tb2 = (TRadioButton) getComponent("ALL");
		table1.acceptText();
		table3.acceptText();
		TParm tableParm1 = table1.getParmValue();
		TParm tableParm3 = table3.getParmValue();
		if (tb0.isSelected()) {
			messageBox("健检无医保明细！");
			return null;
		}
		if (tb1.isSelected()) {
			s = " AND A.ADM_TYPE != 'H' ";
		}
		if (tb2.isSelected()) {
			s = " ";
		}
		String regSeq = "";
		String opbSeq = "";
		for (int i = 0; i < tableParm1.getCount(); i++) {
			if (null != tableParm1.getValue("FLG", i)
					&& tableParm1.getValue("FLG", i).equals("Y")) {
				regSeq += "'"+tableParm1.getValue("ACCOUNT_SEQ", i) + "',";
			}
		}
		for (int i = 0; i < tableParm3.getCount(); i++) {
			if (null != tableParm3.getValue("FLG", i)
					&& tableParm3.getValue("FLG", i).equals("Y")) {
				opbSeq += "'"+tableParm3.getValue("ACCOUNT_SEQ", i) + "',";
			}
		}
		// TParm printParm = new TParm();
		if (regSeq.length() > 0 || opbSeq.length() > 0) {
			try {
				if (regSeq.length() > 0) {
					if (!tb0.isSelected()) {
						regSeq = regSeq.substring(0, regSeq.length() - 1);
					} else {
						regSeq = "";
					}
				}
				if (opbSeq.length() > 0) {
					opbSeq = opbSeq.substring(0, opbSeq.length() - 1);
				}
			} catch (StringIndexOutOfBoundsException e) {
				messageBox("无数据！");
				return null;
			}
			// System.out.println("regSeq======"+regSeq);
			// System.out.println("opbSeq======"+opbSeq);
			// 查询挂号医保明细--------------------------------------------------begin

			String sql = "SELECT "
					+ " A.INS_CROWD_TYPE,"
					+ " SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,"
					+ " SUM(A.OTOT_AMT)        AS OTOT_AMT,"
					+ " SUM(A.ARMY_AI_AMT)     AS ARMY_AI_AMT,"
					+ " SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT,"
					+ " SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT,"
					+ " SUM(A.SERVANT_AMT)     AS SERVANT_AMT,"
					+ " SUM(A.UNREIM_AMT)      AS UNREIM_AMT,"
					+ " SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,C.SPECIAL_PAT"
					+ " FROM " + " INS_OPD A,BIL_REG_RECP B,INS_MZ_CONFIRM C "
					+ " WHERE  A.REGION_CODE = '"
					+ Operator.getRegion() + "' "
					+ " AND A.REGION_CODE  = B.REGION_CODE "
					+ " AND A.CASE_NO = B.CASE_NO "
					+ " AND A.CONFIRM_NO NOT LIKE '*%' "
					+ " AND A.CASE_NO = C.CASE_NO"
					+ " AND A.CONFIRM_NO =C.CONFIRM_NO"
					+ " AND A.INV_NO = B.PRINT_NO " + " AND B.ACCOUNT_SEQ IN ("
					+ regSeq + ")  AND B.AR_AMT>0 "
					+ "GROUP BY  " + " A.INS_CROWD_TYPE,C.SPECIAL_PAT "
					+ "ORDER BY " + " A.INS_CROWD_TYPE,C.SPECIAL_PAT";
			// System.out.println("tttttttttttttt======"+sql);
			TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));

			double CZototamtS = 0;
			double CZototamtY = 0;
			double CZototamtT = 0;

			double CXototamtS = 0;
			double CXototamtY = 0;
			double CXototamtT = 0;

			double totalagentamtS = 0;
			double totalagentamtY = 0;
			double totalagentamtT = 0;

			double CZaccountpayamtS = 0;
			double CZaccountpayamtY = 0;
			double CZaccountpayamtT = 0;

			double CXaccountpayamtS = 0;
			double CXaccountpayamtY = 0;
			double CXaccountpayamtT = 0;

			double illnesssubsidyamtS = 0;
			double illnesssubsidyamtY = 0;
			double illnesssubsidyamtT = 0;

			double flgagentamtS = 0;
			double flgagentamtY = 0;
			double flgagentamtT = 0;

			double servantarmyaiamtS = 0;
			double servantarmyaiamtY = 0;
			double servantarmyaiamtT = 0;

			double soldierarmyaiamtS = 0;
			double soldierarmyaiamtY = 0;
			double soldierarmyaiamtT = 0;

			double civilarmyaiamtS = 0;
			double civilarmyaiamtY = 0;
			double civilarmyaiamtT = 0;

			double specialarmyaiamtS = 0;
			double specialarmyaiamtY = 0;
			double specialarmyaiamtT = 0;

			double insallamtS = 0;

			double unreimAmtS = 0;
			double unreimAmtY = 0;
			double unreimAmtT = 0;
			if (insParm.getCount() > 0) {
				for (int i = 0; i < insParm.getCount(); i++) {
					// 公务员补助
					if (insParm.getData("SPECIAL_PAT", i).equals("06"))
						servantarmyaiamtY += insParm
								.getDouble("ARMY_AI_AMT", i);
					// 军残补助
					else if (insParm.getData("SPECIAL_PAT", i).equals("04"))
						soldierarmyaiamtY += insParm
								.getDouble("ARMY_AI_AMT", i);
					// 民政补助
					else if (insParm.getData("SPECIAL_PAT", i).equals("07"))
						civilarmyaiamtY += insParm.getDouble("ARMY_AI_AMT", i);
					// 民政优抚
					else if (insParm.getData("SPECIAL_PAT", i).equals("08"))
						specialarmyaiamtY += insParm
								.getDouble("ARMY_AI_AMT", i);

					if (insParm.getData("INS_CROWD_TYPE", i).equals("1")) {
						// 城职专项基金
						CZototamtY += insParm.getDouble("OTOT_AMT", i);
						// 城职个人账户
						CZaccountpayamtY += insParm.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					if (insParm.getData("INS_CROWD_TYPE", i).equals("2")) {
						// 城乡专项基金
						CXototamtY += insParm.getDouble("OTOT_AMT", i);
						// 城乡个人账户
						CXaccountpayamtY += insParm.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					// 统筹支付
					totalagentamtY += insParm.getDouble("TOTAL_AGENT_AMT", i);
					// 大额救助
					flgagentamtY += insParm.getDouble("FLG_AGENT_AMT", i);
					// 城乡大病
					illnesssubsidyamtY += insParm.getDouble(
							"ILLNESS_SUBSIDY_AMT", i);
					// 基金未报销
					unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
				}
			}
			String sql1 = "SELECT "
					+ " A.INS_CROWD_TYPE,"
					+ " SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,"
					+ " SUM(A.OTOT_AMT)        AS OTOT_AMT,"
					+ " SUM(A.ARMY_AI_AMT) ARMY_AI_AMT,"
					+ " SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT,"
					+ " SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT,"
					+ " SUM(A.SERVANT_AMT)     AS SERVANT_AMT,"
					+ " SUM(A.UNREIM_AMT)      AS UNREIM_AMT,"
					+ " SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,C.SPECIAL_PAT "
					+ " FROM "
					+ " INS_OPD A,BIL_REG_RECP B,INS_MZ_CONFIRM C "
					+ " WHERE A.REGION_CODE = '"
					+ Operator.getRegion()
					+ "' "
					+ " AND A.REGION_CODE  = B.REGION_CODE "
					+ " AND A.CASE_NO = B.CASE_NO "
					+ " AND A.INV_NO = B.PRINT_NO "
					+ " AND A.CASE_NO =C.CASE_NO"
					+ " AND SUBSTR(A.CONFIRM_NO,2,LENGTH(A.CONFIRM_NO)) = C.CONFIRM_NO"
					+ " AND A.CONFIRM_NO  LIKE '*%' "
					+ " AND B.ACCOUNT_SEQ IN (" + regSeq + ") " 
					+ " AND B.AR_AMT<0 " + "GROUP BY "
					+ " A.INS_CROWD_TYPE ,C.SPECIAL_PAT " + "ORDER BY "
					+ " A.INS_CROWD_TYPE ,C.SPECIAL_PAT";
			// System.out.println("退==="+sql1);
			TParm insParmT = new TParm(TJDODBTool.getInstance().select(sql1));
			if (insParmT.getCount() > 0) {
				for (int i = 0; i < insParmT.getCount(); i++) {
					// 公务员补助
					if (insParmT.getData("SPECIAL_PAT", i).equals("06"))
						servantarmyaiamtT += insParmT.getDouble("ARMY_AI_AMT",
								i);
					// 军残补助
					else if (insParmT.getData("SPECIAL_PAT", i).equals("04"))
						soldierarmyaiamtT += insParmT.getDouble("ARMY_AI_AMT",
								i);
					// 民政补助
					else if (insParmT.getData("SPECIAL_PAT", i).equals("07"))
						civilarmyaiamtT += insParmT.getDouble("ARMY_AI_AMT", i);
					// 民政优抚
					else if (insParmT.getData("SPECIAL_PAT", i).equals("08"))
						specialarmyaiamtT += insParmT.getDouble("ARMY_AI_AMT",
								i);

					if (insParmT.getData("INS_CROWD_TYPE", i).equals("1")) {
						// 城职专项基金
						CZototamtT += insParmT.getDouble("OTOT_AMT", i);
						// 城职个人账户
						CZaccountpayamtT += insParmT.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					if (insParmT.getData("INS_CROWD_TYPE", i).equals("2")) {
						// 城乡专项基金
						CXototamtT += insParmT.getDouble("OTOT_AMT", i);
						// 城乡个人账户
						CXaccountpayamtT += insParmT.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					// 统筹支付
					totalagentamtT += insParmT.getDouble("TOTAL_AGENT_AMT", i);
					// 大额救助
					flgagentamtT += insParmT.getDouble("FLG_AGENT_AMT", i);
					// 城乡大病
					illnesssubsidyamtT += insParmT.getDouble(
							"ILLNESS_SUBSIDY_AMT", i);
					// 基金未报销
					unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
				}
			}

			CZototamtS = CZototamtY + CZototamtT;
			CXototamtS = CXototamtY + CXototamtT;
			totalagentamtS = totalagentamtY + totalagentamtT;
			CZaccountpayamtS = CZaccountpayamtY + CZaccountpayamtT;
			CXaccountpayamtS = CXaccountpayamtY + CXaccountpayamtT;
			illnesssubsidyamtS = illnesssubsidyamtY + illnesssubsidyamtT;
			flgagentamtS = flgagentamtY + flgagentamtT;
			servantarmyaiamtS = servantarmyaiamtY + servantarmyaiamtT;
			soldierarmyaiamtS = soldierarmyaiamtY + soldierarmyaiamtT;
			civilarmyaiamtS = civilarmyaiamtY + civilarmyaiamtT;
			specialarmyaiamtS = specialarmyaiamtY + specialarmyaiamtT;

			// 基金未报销
			unreimAmtS = unreimAmtY + unreimAmtT;
			// 医保合计
			insallamtS = CZototamtS + CXototamtS + totalagentamtS
					+ CZaccountpayamtS + CXaccountpayamtS + illnesssubsidyamtS
					+ flgagentamtS + servantarmyaiamtS + soldierarmyaiamtS
					+ civilarmyaiamtS + specialarmyaiamtS;
			// 查询挂号医保明细--------------------------------------------------end

			// 查询收费医保明细--------------------------------------------------begin
			String sql2 = "SELECT "
					+ " A.INS_CROWD_TYPE,"
					+ " SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,"
					+ " SUM(A.OTOT_AMT)        AS OTOT_AMT,"
					+ " SUM(A.ARMY_AI_AMT)     AS ARMY_AI_AMT,"
					+ " SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT,"
					+ " SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT,"
					+ " SUM(A.SERVANT_AMT)     AS SERVANT_AMT,"
					+ " SUM(A.UNREIM_AMT)      AS UNREIM_AMT,"
					+ " SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,C.SPECIAL_PAT"
					+ " FROM " + " INS_OPD A,BIL_OPB_RECP B,INS_MZ_CONFIRM C "
					+ " WHERE " + " A.REGION_CODE = '"
					+ Operator.getRegion() + "' "
					+ " AND A.REGION_CODE  = B.REGION_CODE "
					+ " AND A.CASE_NO = B.CASE_NO "
					+ " AND A.CONFIRM_NO NOT LIKE '*%' "
					+ " AND A.CASE_NO = C.CASE_NO"
					+ " AND A.CONFIRM_NO =C.CONFIRM_NO"
					+ " AND A.INV_NO = B.PRINT_NO " + " AND B.ACCOUNT_SEQ IN ("
					+ opbSeq + ") AND B.AR_AMT>0 "
					+ "GROUP BY  " + " A.INS_CROWD_TYPE,C.SPECIAL_PAT "
					+ "ORDER BY " + " A.INS_CROWD_TYPE,C.SPECIAL_PAT";
			// System.out.println("tttttttttttttt======"+sql);
			TParm insParmOPB = new TParm(TJDODBTool.getInstance().select(sql2));

			double CZototamtSOPB = 0;
			double CZototamtYOPB = 0;
			double CZototamtTOPB = 0;

			double CXototamtSOPB = 0;
			double CXototamtYOPB = 0;
			double CXototamtTOPB = 0;

			double totalagentamtSOPB = 0;
			double totalagentamtYOPB = 0;
			double totalagentamtTOPB = 0;

			double CZaccountpayamtSOPB = 0;
			double CZaccountpayamtYOPB = 0;
			double CZaccountpayamtTOPB = 0;

			double CXaccountpayamtSOPB = 0;
			double CXaccountpayamtYOPB = 0;
			double CXaccountpayamtTOPB = 0;

			double illnesssubsidyamtSOPB = 0;
			double illnesssubsidyamtYOPB = 0;
			double illnesssubsidyamtTOPB = 0;

			double flgagentamtSOPB = 0;
			double flgagentamtYOPB = 0;
			double flgagentamtTOPB = 0;

			double servantarmyaiamtSOPB = 0;
			double servantarmyaiamtYOPB = 0;
			double servantarmyaiamtTOPB = 0;

			double soldierarmyaiamtSOPB = 0;
			double soldierarmyaiamtYOPB = 0;
			double soldierarmyaiamtTOPB = 0;

			double civilarmyaiamtSOPB = 0;
			double civilarmyaiamtYOPB = 0;
			double civilarmyaiamtTOPB = 0;

			double specialarmyaiamtSOPB = 0;
			double specialarmyaiamtYOPB = 0;
			double specialarmyaiamtTOPB = 0;

			double insallamtSOPB = 0;

			double unreimAmtSOPB = 0;
			double unreimAmtYOPB = 0;
			double unreimAmtTOPB = 0;
			if (insParmOPB.getCount() > 0) {
				for (int i = 0; i < insParmOPB.getCount(); i++) {
					// 公务员补助
					if (insParmOPB.getData("SPECIAL_PAT", i).equals("06"))
						servantarmyaiamtYOPB += insParmOPB.getDouble(
								"ARMY_AI_AMT", i);
					// 军残补助
					else if (insParmOPB.getData("SPECIAL_PAT", i).equals("04"))
						soldierarmyaiamtYOPB += insParmOPB.getDouble(
								"ARMY_AI_AMT", i);
					// 民政补助
					else if (insParmOPB.getData("SPECIAL_PAT", i).equals("07"))
						civilarmyaiamtYOPB += insParmOPB.getDouble(
								"ARMY_AI_AMT", i);
					// 民政优抚
					else if (insParmOPB.getData("SPECIAL_PAT", i).equals("08"))
						specialarmyaiamtYOPB += insParmOPB.getDouble(
								"ARMY_AI_AMT", i);

					if (insParmOPB.getData("INS_CROWD_TYPE", i).equals("1")) {
						// 城职专项基金
						CZototamtYOPB += insParmOPB.getDouble("OTOT_AMT", i);
						// 城职个人账户
						CZaccountpayamtYOPB += insParmOPB.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					if (insParmOPB.getData("INS_CROWD_TYPE", i).equals("2")) {
						// 城乡专项基金
						CXototamtYOPB += insParmOPB.getDouble("OTOT_AMT", i);
						// 城乡个人账户
						CXaccountpayamtYOPB += insParmOPB.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					// 统筹支付
					totalagentamtYOPB += insParmOPB.getDouble(
							"TOTAL_AGENT_AMT", i);
					// 大额救助
					flgagentamtYOPB += insParmOPB.getDouble("FLG_AGENT_AMT", i);
					// 城乡大病
					illnesssubsidyamtYOPB += insParmOPB.getDouble(
							"ILLNESS_SUBSIDY_AMT", i);
					// 基金未报销
					unreimAmtYOPB += insParmOPB.getDouble("UNREIM_AMT", i);
				}
			}
			String sql3 = "SELECT "
					+ " A.INS_CROWD_TYPE,"
					+ " SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT,"
					+ " SUM(A.OTOT_AMT)        AS OTOT_AMT,"
					+ " SUM(A.ARMY_AI_AMT) ARMY_AI_AMT,"
					+ " SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT,"
					+ " SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT,"
					+ " SUM(A.SERVANT_AMT)     AS SERVANT_AMT,"
					+ " SUM(A.UNREIM_AMT)      AS UNREIM_AMT,"
					+ " SUM(A.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT,C.SPECIAL_PAT "
					+ " FROM "
					+ " INS_OPD A,BIL_OPB_RECP B,INS_MZ_CONFIRM C "
					+ " WHERE A.REGION_CODE = '"
					+ Operator.getRegion()
					+ "' "
					+ " AND A.REGION_CODE  = B.REGION_CODE "
					+ " AND A.CASE_NO = B.CASE_NO "
					+ " AND A.INV_NO = B.PRINT_NO "
					+ " AND A.CASE_NO =C.CASE_NO"
					+ " AND SUBSTR(A.CONFIRM_NO,2,LENGTH(A.CONFIRM_NO)) = C.CONFIRM_NO"
					+ " AND A.CONFIRM_NO  LIKE '*%' "
					+ " AND B.ACCOUNT_SEQ IN (" + opbSeq + ") " 
					+ " AND B.AR_AMT<0 " + "GROUP BY "
					+ " A.INS_CROWD_TYPE ,C.SPECIAL_PAT " + "ORDER BY "
					+ " A.INS_CROWD_TYPE ,C.SPECIAL_PAT";
			// System.out.println("退==="+sql1);
			TParm insParmTOPB = new TParm(TJDODBTool.getInstance().select(sql3));
			if (insParmTOPB.getCount() > 0) {
				for (int i = 0; i < insParmTOPB.getCount(); i++) {
					// 公务员补助
					if (insParmTOPB.getData("SPECIAL_PAT", i).equals("06"))
						servantarmyaiamtTOPB += insParmTOPB.getDouble(
								"ARMY_AI_AMT", i);
					// 军残补助
					else if (insParmTOPB.getData("SPECIAL_PAT", i).equals("04"))
						soldierarmyaiamtTOPB += insParmTOPB.getDouble(
								"ARMY_AI_AMT", i);
					// 民政补助
					else if (insParmTOPB.getData("SPECIAL_PAT", i).equals("07"))
						civilarmyaiamtTOPB += insParmTOPB.getDouble(
								"ARMY_AI_AMT", i);
					// 民政优抚
					else if (insParmTOPB.getData("SPECIAL_PAT", i).equals("08"))
						specialarmyaiamtTOPB += insParmTOPB.getDouble(
								"ARMY_AI_AMT", i);
					if (insParmTOPB.getData("INS_CROWD_TYPE", i).equals("1")) {
						// 城职专项基金
						CZototamtTOPB += insParmTOPB.getDouble("OTOT_AMT", i);
						// 城职个人账户
						CZaccountpayamtTOPB += insParmTOPB.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					if (insParmTOPB.getData("INS_CROWD_TYPE", i).equals("2")) {
						// 城乡专项基金
						CXototamtTOPB += insParmTOPB.getDouble("OTOT_AMT", i);
						// 城乡个人账户
						CXaccountpayamtTOPB += insParmTOPB.getDouble(
								"ACCOUNT_PAY_AMT", i);
					}
					// 统筹支付
					totalagentamtTOPB += insParmTOPB.getDouble(
							"TOTAL_AGENT_AMT", i);
					// 大额救助
					flgagentamtTOPB += insParmTOPB
							.getDouble("FLG_AGENT_AMT", i);
					// 城乡大病
					illnesssubsidyamtTOPB += insParmTOPB.getDouble(
							"ILLNESS_SUBSIDY_AMT", i);
					// 基金未报销
					unreimAmtTOPB += insParmTOPB.getDouble("UNREIM_AMT", i);
				}
			}

			CZototamtSOPB = CZototamtYOPB + CZototamtTOPB;
			CXototamtSOPB = CXototamtYOPB + CXototamtTOPB;
			totalagentamtSOPB = totalagentamtYOPB + totalagentamtTOPB;
			CZaccountpayamtSOPB = CZaccountpayamtYOPB + CZaccountpayamtTOPB;
			CXaccountpayamtSOPB = CXaccountpayamtYOPB + CXaccountpayamtTOPB;
			illnesssubsidyamtSOPB = illnesssubsidyamtYOPB
					+ illnesssubsidyamtTOPB;
			flgagentamtSOPB = flgagentamtYOPB + flgagentamtTOPB;
			servantarmyaiamtSOPB = servantarmyaiamtYOPB + servantarmyaiamtTOPB;
			soldierarmyaiamtSOPB = soldierarmyaiamtYOPB + soldierarmyaiamtTOPB;
			civilarmyaiamtSOPB = civilarmyaiamtYOPB + civilarmyaiamtTOPB;
			specialarmyaiamtSOPB = specialarmyaiamtYOPB + specialarmyaiamtTOPB;

			// 基金未报销
			unreimAmtSOPB = unreimAmtYOPB + unreimAmtTOPB;
			// 医保合计
			insallamtSOPB = CZototamtSOPB + CXototamtSOPB + totalagentamtSOPB
					+ CZaccountpayamtSOPB + CXaccountpayamtSOPB
					+ illnesssubsidyamtSOPB + flgagentamtSOPB
					+ servantarmyaiamtSOPB + soldierarmyaiamtSOPB
					+ civilarmyaiamtSOPB + specialarmyaiamtSOPB;
			// 查询收费医保明细--------------------------------------------------end
			returnParm.setData("CZ_OTOT_AMT", "TEXT", StringTool.round(
					CZototamtS + CZototamtSOPB, 2));
			returnParm.setData("TOTAL_AGENT_AMT", "TEXT", StringTool.round(
					totalagentamtS + totalagentamtSOPB, 2));
			returnParm.setData("CZ_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(
					CZaccountpayamtS + CZaccountpayamtSOPB, 2));
			returnParm.setData("ILLNESS_SUBSIDY_AMT", "TEXT", StringTool.round(
					illnesssubsidyamtS + illnesssubsidyamtSOPB, 2));
			returnParm.setData("FLG_AGENT_AMT", "TEXT", StringTool.round(
					flgagentamtS + flgagentamtSOPB, 2));
			returnParm.setData("SERVANT_ARMY_AI_AMT", "TEXT", StringTool.round(
					servantarmyaiamtS + servantarmyaiamtSOPB, 2));
			returnParm.setData("SOLDIER_ARMY_AI_AMT", "TEXT",StringTool.round(
					soldierarmyaiamtS + soldierarmyaiamtSOPB, 2));
			returnParm.setData("CIVIL_ARMY_AI_AMT", "TEXT", StringTool.round(
					civilarmyaiamtS + civilarmyaiamtSOPB, 2));
			returnParm.setData("SPECIAL_ARMY_AI_AMT", "TEXT", StringTool.round(
					specialarmyaiamtS + specialarmyaiamtSOPB, 2));
			returnParm.setData("CX_OTOT_AMT", "TEXT", StringTool.round(
					CXototamtS + CXototamtSOPB, 2));
			returnParm.setData("CX_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(
					CXaccountpayamtS + CXaccountpayamtSOPB, 2));
			returnParm.setData("UNREIM_AMT", "TEXT", StringTool.round(
					unreimAmtS + unreimAmtSOPB, 2));
			returnParm.setData("INS_ALL_AMT", "TEXT", StringTool.round(
					insallamtS + insallamtSOPB, 2));
			// 结算日期
			String stardate = "";
			String enddate = "";
			if (regSeq != null && opbSeq != null) {
				String sqlBilREG = " SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ "
						+ " FROM BIL_REG_RECP WHERE ACCOUNT_SEQ in (" + regSeq
						+ ") " + " ORDER BY BILL_DATE ";
				TParm bilParmREG = new TParm(TJDODBTool.getInstance().select(
						sqlBilREG));
				// System.out.println("bilParmREG======"+bilParmREG);
				String stardatereg = StringTool.getString(bilParmREG
						.getTimestamp("BILL_DATE", 0), "yyyyMMddHHmmss");
				String enddatereg = StringTool.getString(bilParmREG
						.getTimestamp("BILL_DATE", bilParmREG.getCount() - 1),
						"yyyyMMddHHmmss");

				String sqlBil = " SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ "
						+ " FROM BIL_OPB_RECP WHERE ACCOUNT_SEQ in (" + opbSeq
						+ ") " + " ORDER BY BILL_DATE ";
				TParm bilParm = new TParm(TJDODBTool.getInstance().select(
						sqlBil));
				// System.out.println("bilParm======"+bilParm);
				String stardateopb = StringTool.getString(bilParm.getTimestamp(
						"BILL_DATE", 0), "yyyyMMddHHmmss");
				String enddateopb = StringTool.getString(bilParm.getTimestamp(
						"BILL_DATE", bilParm.getCount() - 1), "yyyyMMddHHmmss");

				if (stardatereg.compareTo(stardateopb) < 0)
					stardate = bilParmREG.getData("BILL_DATE", 0).toString();
				else
					stardate = bilParm.getData("BILL_DATE", 0).toString();

				if (enddatereg.compareTo(enddateopb) > 0)
					enddate = bilParmREG.getData("BILL_DATE",
							bilParmREG.getCount() - 1).toString();
				else
					enddate = bilParm.getData("BILL_DATE",
							bilParm.getCount() - 1).toString();
			}
			// System.out.println("stardate======"+stardate);
			// System.out.println("enddate======"+enddate);
			stardate = stardate.substring(0, 19);
			enddate = enddate.substring(0, 19);
			returnParm.setData("ACCOUNTDATE", "TEXT", stardate + " 至 "
					+ enddate);
		} else {
			messageBox("请选择开始和结束表号");
			return null;
		}

		return returnParm;
	}

	public void onSelectReg() {
		TCheckBox box = (TCheckBox) this.getComponent("REG_CHECK");
		TParm parm = table1.getParmValue();
		String flg = "N";
		if (box.isSelected()) {
			flg = "Y";
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		table1.setParmValue(parm);
	}

	public void onSelectOpb() {
		TCheckBox box = (TCheckBox) this.getComponent("OPB_CHECK");
		TParm parm = table3.getParmValue();
		String flg = "N";
		if (box.isSelected()) {
			flg = "Y";
		}
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("FLG", i, flg);
		}
		table3.setParmValue(parm);
	}
}
