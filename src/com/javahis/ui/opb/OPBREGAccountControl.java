package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;

/**
 * <p>Title: 门诊挂号收费汇总</p>
 *
 * <p>Description: 门诊挂号收费汇总</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: javahis222</p>
 *
 * @author zhangp 20120322
 * @version 1.0
 */
public class OPBREGAccountControl
    extends TControl {
    TTable table;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化页面信息
     */
    public void initPage() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	table = (TTable)this.getComponent("TABLE");
    }

    /**
     * 查询
     */
    public void onQuery() {
    	if(getValueString("START_DATE").equals("")||getValueString("END_DATE").equals("")){
    		messageBox("请选择时间");
    		return;
    	}
		String start_date = this.getValueString("START_DATE").substring(0, 19);
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10) + start_date.substring(11, 13)
				+ start_date.substring(14, 16) + start_date.substring(17, 19);
		String end_date = this.getValueString("END_DATE").substring(0, 19);
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
    	String sql =
    		"SELECT ADM_TYPE,SUM (AR_AMT) AS AR_AMT " +
    		" FROM BIL_REG_RECP " +
    		" WHERE BILL_DATE BETWEEN TO_DATE('"+
    		start_date+"','yyyyMMddHH24miss') AND TO_DATE('"+
    		end_date+"','yyyyMMddHH24miss') GROUP BY ADM_TYPE ORDER BY ADM_TYPE";
    	TParm regAmtParm = new TParm(TJDODBTool.getInstance().select(sql));
    	sql = 
    		"SELECT ADM_TYPE,COUNT(PRINT_NO) AS TCOUNT" +
    		" FROM BIL_REG_RECP" +
    		" WHERE PRINT_DATE BETWEEN TO_DATE('"+
    		start_date+"','yyyyMMddHH24miss') AND TO_DATE('"+
    		end_date+"','yyyyMMddHH24miss') AND AR_AMT < 0 " +
    		" GROUP BY ADM_TYPE ORDER BY ADM_TYPE";
    	TParm regResetInvParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(regAmtParm.getCount()<0){
    		
    	}else{
        	for (int i = 0; i < regAmtParm.getCount(); i++) {
        		regAmtParm.setData("TCOUNT", i, 0);
        		if(regResetInvParm.getCount()>0){
        			for (int j = 0; j < regResetInvParm.getCount(); j++) {
        				if(regAmtParm.getData("ADM_TYPE", i).equals(regResetInvParm.getData("ADM_TYPE", j))){
        					regAmtParm.setData("TCOUNT", i, regResetInvParm.getData("TCOUNT", j));
        				}
        			}
        		}
        	}
		}
    	sql = 
    		"SELECT ADM_TYPE,SUM (AR_AMT) AR_AMT" +
    		" FROM BIL_OPB_RECP" +
    		" WHERE BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
    		" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss') " +
    		" GROUP BY ADM_TYPE " +
    		" ORDER BY ADM_TYPE";
    	TParm opbAmtParm = new TParm(TJDODBTool.getInstance().select(sql));
    	sql = 
    		"SELECT ADM_TYPE,COUNT(PRINT_NO) AS TCOUNT" +
    		" FROM BIL_OPB_RECP" +
    		" WHERE PRINT_DATE BETWEEN TO_DATE('"+
    		start_date+"','yyyyMMddHH24miss') AND TO_DATE('"+
    		end_date+"','yyyyMMddHH24miss') AND AR_AMT < 0 " +
    		" GROUP BY ADM_TYPE ORDER BY ADM_TYPE";
    	TParm opbResetInvParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(opbAmtParm.getCount()<0){
    		
    	}else{
        	for (int i = 0; i < opbAmtParm.getCount(); i++) {
        		opbAmtParm.setData("TCOUNT", i, 0);
        		if(opbResetInvParm.getCount()>0){
        			for (int j = 0; j < opbResetInvParm.getCount(); j++) {
        				if(opbAmtParm.getData("ADM_TYPE", i).equals(opbResetInvParm.getData("ADM_TYPE", j))){
        					opbAmtParm.setData("TCOUNT", i, opbResetInvParm.getData("TCOUNT", j));
        				}
        			}
        		}
        	}
		}
    	TParm result = new TParm();
    	if(regAmtParm.getCount()>0){
    		for (int i = 0; i < regAmtParm.getCount(); i++) {
    			result.addData("RECP_TYPE", "挂号");
        		result.addData("ADM_TYPE", regAmtParm.getData("ADM_TYPE", i));
        		result.addData("TCOUNT", regAmtParm.getData("TCOUNT", i));
        		result.addData("AR_AMT", regAmtParm.getData("AR_AMT", i));
    		}
    	}
    	if(opbAmtParm.getCount()>0){
    		for (int i = 0; i < opbAmtParm.getCount(); i++) {
    			result.addData("RECP_TYPE", "收费");
        		result.addData("ADM_TYPE", opbAmtParm.getData("ADM_TYPE", i));
        		result.addData("TCOUNT", opbAmtParm.getData("TCOUNT", i));
        		result.addData("AR_AMT", opbAmtParm.getData("AR_AMT", i));
    		}
    	}
//    	System.out.println("result==="+result);
        table.setParmValue(result);
    }

    /**
     * 取得票号
     * @param recpType
     * @param admType
     * @return
     */
    public TParm getPrintNo(String recpType,String admType){
    	if(getValueString("START_DATE").equals("")||getValueString("END_DATE").equals("")){
    		messageBox("请选择时间");
    		return null;
    	}
		String start_date = this.getValueString("START_DATE").substring(0, 19);
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10) + start_date.substring(11, 13)
				+ start_date.substring(14, 16) + start_date.substring(17, 19);
		String end_date = this.getValueString("END_DATE").substring(0, 19);
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
		String sql = 
    		"SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ FROM BIL_"+recpType+"_RECP WHERE " +
    		"BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
    		" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss') AND ADM_TYPE = '"+admType+"'" +
    		"ORDER BY PRINT_NO,BILL_DATE ";
		TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = 
			"SELECT START_INVNO,END_INVNO FROM BIL_INVOICE WHERE RECP_TYPE = '"+recpType+"'";
		TParm invNoParm = new TParm(TJDODBTool.getInstance().select(
        		sql));
    	sql = 
    		"SELECT COUNT(PRINT_NO) AS COUNT" +
    		" FROM BIL_"+recpType+"_RECP" +
    		" WHERE PRINT_DATE BETWEEN TO_DATE('"+
    		start_date+"','yyyyMMddHH24miss') AND TO_DATE('"+
    		end_date+"','yyyyMMddHH24miss') AND AR_AMT > 0 " +
    				"AND ADM_TYPE = '"+admType+"'" ;
    	TParm resetparm = new TParm(TJDODBTool.getInstance().select(sql));
		String recp_no = "";
		int printCount = 0;
		if(resetparm.getCount()>0){
			printCount = resetparm.getInt("COUNT", 0);
		}
        if(invNoParm.getCount()>0){
            for (int i = 0; i < invNoParm.getCount(); i++) {
            	TParm p = new TParm();
    			String startNo = invNoParm.getData("START_INVNO", i).toString();
    			String endNo = invNoParm.getData("END_INVNO", i).toString();
    			for (int j = 0; j < bilParm.getCount("PRINT_NO"); j++) {
    				String invNo = bilParm.getData("PRINT_NO", j).toString();
    				if(invNo.compareTo(startNo)>=0&&invNo.compareTo(endNo)<=0){
    					p.addData("INV_NO", invNo);
    				}
    			}
            	if(p.getCount("INV_NO")>1){
            		recp_no = recp_no + ","+p.getData("INV_NO", 0)+" ~ "+p.getData("INV_NO", p.getCount("INV_NO")-1);
//            		printCount += p.getCount("INV_NO");
            	}
            	if(p.getCount("INV_NO")>0&&p.getCount("INV_NO")<=1){
            		recp_no = ""+p.getData("INV_NO", 0);
//            		printCount += 1;
            	}
    		}
        }
        if(recp_no.contains(",")){
        	recp_no = recp_no.substring(1, recp_no.length());
        }
        sql = 
    		"SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ FROM BIL_"+recpType+"_RECP WHERE " +
    		"BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
    		" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss') AND ADM_TYPE = '"+admType+"'" +
    		" AND RESET_RECEIPT_NO IS NOT NULL ORDER BY BILL_DATE ";
        bilParm = new TParm(TJDODBTool.getInstance().select(sql));
        String restBil = "";
        if(bilParm.getCount()>0){
        	for (int i = 0; i < bilParm.getCount(); i++) {
        		restBil += ","+bilParm.getData("PRINT_NO", i);
			}
        	restBil = restBil.substring(1, restBil.length());
        }
        sql = 
        	"SELECT SUM (PAY_MEDICAL_CARD) PAY_MEDICAL_CARD, SUM (PAY_CASH) PAY_CASH," +
        	" SUM (PAY_CHECK) PAY_CHECK, SUM (AR_AMT) AR_AMT" +
        	" FROM BIL_"+recpType+"_RECP" +
        	" WHERE BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')" +
        	" AND ADM_TYPE = '"+admType+"'";
        bilParm = new TParm(TJDODBTool.getInstance().select(sql));
        double ektAmt = 0;
        double arAmt = 0;
        double checkAmt = 0;
        double cashAmt = 0;
        if(bilParm.getCount()>0){
        	ektAmt = bilParm.getDouble("PAY_MEDICAL_CARD", 0);
        	arAmt = bilParm.getDouble("AR_AMT", 0);
        	checkAmt = bilParm.getDouble("PAY_CHECK", 0);
        	cashAmt = bilParm.getDouble("PAY_CASH", 0);
        }
        TParm result = new TParm();
        result.setData("RECP_NO", recp_no);
        result.setData("RECP_COUNT", printCount);
        result.setData("RESET_BIL", restBil);
        result.setData("PAY_MEDICAL_CARD", ektAmt);
        result.setData("PAY_CASH", cashAmt);
        result.setData("PAY_CHECK", checkAmt);
        result.setData("AR_AMT", arAmt);
        return result;
    }
    /**
     * 查询门诊记录挂号
     * @return
     */
    public TParm getCharge(){
    	String start_date = this.getValueString("START_DATE").substring(0, 19);
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10) + start_date.substring(11, 13)
				+ start_date.substring(14, 16) + start_date.substring(17, 19);
		String end_date = this.getValueString("END_DATE").substring(0, 19);
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
    	String sql =
            "SELECT SUM(O.CHARGE01) CHARGE01,SUM(O.CHARGE02) CHARGE02,SUM(O.CHARGE03) CHARGE03,SUM(O.CHARGE04) CHARGE04," +
            "       SUM(O.CHARGE05) CHARGE05,SUM(O.CHARGE06) CHARGE06,SUM(O.CHARGE07) CHARGE07,SUM(O.CHARGE08) CHARGE08,SUM(O.CHARGE09) CHARGE09," +
            "       SUM(O.CHARGE10) CHARGE10,SUM(O.CHARGE11) CHARGE11,SUM(O.CHARGE12) CHARGE12,SUM(O.CHARGE13) CHARGE13,SUM(O.CHARGE14) CHARGE14," +
            "       SUM(O.CHARGE15) CHARGE15,SUM(O.CHARGE16) CHARGE16,SUM(O.CHARGE17) CHARGE17,SUM(O.CHARGE18) CHARGE18,SUM(O.CHARGE19) CHARGE19," +
            "       SUM(O.CHARGE20) CHARGE20,SUM(O.CHARGE21) CHARGE21,SUM(O.CHARGE22) CHARGE22,SUM(O.CHARGE23) CHARGE23,SUM(O.CHARGE24) CHARGE24," +
            "       SUM(O.CHARGE25) CHARGE25,SUM(O.CHARGE26) CHARGE26,SUM(O.CHARGE27) CHARGE27,SUM(O.CHARGE28) CHARGE28,SUM(O.CHARGE29) CHARGE29," +
            "       SUM(O.CHARGE30) CHARGE30,SUM(O.TOT_AMT) O_TOT_AMT,SUM(O.PAY_CASH) O_PAY_CASH,SUM(O.PAY_MEDICAL_CARD) O_PAY_MEDICAL_CARD,SUM(O.PAY_BANK_CARD) O_PAY_BANK_CARD," +
            "       SUM(O.PAY_CHECK) O_PAY_CHECK,SUM(O.PAY_DEBIT) O_PAY_DEBIT,SUM(O.PAY_BILPAY) O_PAY_BILPAY "+
            "  FROM BIL_OPB_RECP O " +
            " WHERE O.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')" ;
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	sql =
            "SELECT SUM(R.REG_FEE_REAL) R_REG_FEE,SUM(R.CLINIC_FEE_REAL) R_CLINIC_FEE,SUM(R.AR_AMT) R_AR_AMT," +
            "		SUM(R.PAY_CASH) R_PAY_CASH,SUM(R.PAY_BANK_CARD) R_PAY_BANK_CARD,SUM(R.PAY_MEDICAL_CARD) R_PAY_MEDICAL_CARD,SUM(R.PAY_CHECK) R_PAY_CHECK " +
            "  FROM BIL_REG_RECP R " +
            " WHERE R.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')";
    	TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
    	sql = 
    		"SELECT SUM(O.TOT_AMT) O_TOT_AMT,SUM(O.PAY_CASH) O_PAY_CASH,SUM(O.PAY_MEDICAL_CARD) O_PAY_MEDICAL_CARD,SUM(O.PAY_BANK_CARD) O_PAY_BANK_CARD," +
            "       SUM(O.PAY_CHECK) O_PAY_CHECK,SUM(O.PAY_DEBIT) O_PAY_DEBIT,SUM(O.PAY_BILPAY) O_PAY_BILPAY "+
            "  FROM BIL_OPB_RECP O " +
            " WHERE O.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss') " +
        	" AND TOT_AMT > 0" ;
    	TParm result3 = new TParm(TJDODBTool.getInstance().select(sql));
    	sql =
            "SELECT SUM(R.REG_FEE_REAL) R_REG_FEE,SUM(R.CLINIC_FEE_REAL) R_CLINIC_FEE,SUM(R.AR_AMT) R_AR_AMT," +
            "		SUM(R.PAY_CASH) R_PAY_CASH,SUM(R.PAY_BANK_CARD) R_PAY_BANK_CARD,SUM(R.PAY_MEDICAL_CARD) R_PAY_MEDICAL_CARD,SUM(R.PAY_CHECK) R_PAY_CHECK " +
            "  FROM BIL_REG_RECP R " +
            " WHERE R.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss') " +
        	" AND AR_AMT > 0";
    	TParm result4 = new TParm(TJDODBTool.getInstance().select(sql));
    	result.addData("R_REG_FEE", result2.getData("R_REG_FEE", 0));
    	result.addData("R_CLINIC_FEE", result2.getData("R_CLINIC_FEE", 0));
    	result.addData("R_AR_AMT", result2.getData("R_AR_AMT", 0));
    	result.addData("R_PAY_CASH", result2.getData("R_PAY_CASH", 0));
    	result.addData("R_PAY_BANK_CARD", result2.getData("R_PAY_BANK_CARD", 0));
    	result.addData("R_PAY_MEDICAL_CARD", result2.getData("R_PAY_MEDICAL_CARD", 0));
    	result.addData("R_PAY_CHECK", result2.getData("R_PAY_CHECK", 0));
    	result.addData("O_TOT_AMT_Y", result3.getData("O_TOT_AMT", 0));
    	result.addData("O_PAY_CASH_Y", result3.getData("O_PAY_CASH", 0));
    	result.addData("O_PAY_MEDICAL_CARD_Y", result3.getData("O_PAY_MEDICAL_CARD", 0));
    	result.addData("O_PAY_BANK_CARD_Y", result3.getData("O_PAY_BANK_CARD", 0));
    	result.addData("O_PAY_CHECK_Y", result3.getData("O_PAY_CHECK", 0));
    	result.addData("O_PAY_DEBIT_Y", result3.getData("O_PAY_DEBIT", 0));
    	result.addData("O_PAY_BILPAY_Y", result3.getData("O_PAY_BILPAY", 0));
    	result.addData("R_REG_FEE_Y", result4.getData("R_REG_FEE", 0));
    	result.addData("R_CLINIC_FEE_Y", result4.getData("R_CLINIC_FEE", 0));
    	result.addData("R_AR_AMT_Y", result4.getData("R_AR_AMT", 0));
    	result.addData("R_PAY_CASH_Y", result4.getData("R_PAY_CASH", 0));
    	result.addData("R_PAY_BANK_CARD_Y", result4.getData("R_PAY_BANK_CARD", 0));
    	result.addData("R_PAY_MEDICAL_CARD_Y", result4.getData("R_PAY_MEDICAL_CARD", 0));
    	result.addData("R_PAY_CHECK_Y", result4.getData("R_PAY_CHECK", 0));
    	return result;
    }
    
    public TParm getChargeParm(TParm chargeparm,TParm returnParm){
        double charge1 = chargeparm.getDouble("CHARGE01",0);//抗生素
        double charge2 = chargeparm.getDouble("CHARGE02",0);//非抗生素
        double charge1and2 = charge1+charge2;//西药费
        for (int i = 1; i < 31; i++) {
        	if(i<10){
        		returnParm.setData("CHARGE0"+i, StringTool.round(chargeparm.getDouble("CHARGE0"+i,0), 2));
        	}else{
        		returnParm.setData("CHARGE"+i, StringTool.round(chargeparm.getDouble("CHARGE"+i,0), 2));
        	}
		}
        returnParm.setData("CHARGE01AND02", StringTool.round(charge1and2,2));
    	return returnParm;
    }
    /**
     * 获取医保信息
     * @return
     */
    public TParm getInsMessage(TParm returnParm){
    	String start_date = this.getValueString("START_DATE").substring(0, 19);
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10) + start_date.substring(11, 13)
				+ start_date.substring(14, 16) + start_date.substring(17, 19);
		String end_date = this.getValueString("END_DATE").substring(0, 19);
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
        double payInsNhiS = 0;
        double payInsHelpS = 0;
        double unreimAmtY = 0;
        double unreimAmtT = 0;
        double unreimAmtS = 0;
        double payInsCardS = 0;
        double payInsS = 0;
        double payInsCardT = 0;
        double payInsT = 0;
        double payInsCardY = 0;
        double payInsY = 0;
        double payInsNhiT = 0;
        double payInsHelpT = 0;
		double payInsNhiY = 0;
		double payInsHelpY = 0;
		double payInsTotY = 0;
		double payInsTotT = 0;
		double payInsTotS = 0;
        String sql = 
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
        	" INS_OPD A,BIL_OPB_RECP B " +
        	"WHERE " +
        	" A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.CONFIRM_NO NOT LIKE '*%' " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND B.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')" +
        	" AND B.AR_AMT>0 " +
        	"GROUP BY  " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParm.getCount()>0){
        	for (int i = 0; i < insParm.getCount(); i++) {
    			if(insParm.getData("INS_CROWD_TYPE", i).equals("1")){
    				//城职INS_CROWD_TYPE = ‘1’
    				//个人账户=ACCOUNT_PAY_AMT
    				//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
    				payInsCardY += insParm.getDouble("ACCOUNT_PAY_AMT", i);
    				payInsNhiY += insParm.getDouble("OTOT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
    									insParm.getDouble("TOTAL_AGENT_AMT", i) + insParm.getDouble("FLG_AGENT_AMT", i) + 
    									insParm.getDouble("SERVANT_AMT", i);
    			}
    			if(insParm.getData("INS_CROWD_TYPE", i).equals("2")){
//    				城居INS_CROWD_TYPE = ‘2’
//    				救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//    				统筹=TOTAL_AGENT_AMT
    				payInsHelpY += insParm.getDouble("FLG_AGENT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
    							   insParm.getDouble("SERVANT_AMT", i) + insParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
    				payInsY += insParm.getDouble("TOTAL_AGENT_AMT", i);
    			}
    			unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
    		}
        }
        sql = 
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
        	" INS_OPD A,BIL_OPB_RECP B " +
        	"WHERE A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND A.CONFIRM_NO  LIKE '*%' " +
        	" AND B.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')" +
        	" AND B.AR_AMT<0 " +
        	"GROUP BY " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        TParm insParmT = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParmT.getCount()>0){
        	for (int i = 0; i < insParmT.getCount(); i++) {
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("1")){
            		//城职INS_CROWD_TYPE = ‘1’
            		//个人账户=ACCOUNT_PAY_AMT
            		//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
            		payInsCardT += insParmT.getDouble("ACCOUNT_PAY_AMT", i);
            		payInsNhiT += insParmT.getDouble("OTOT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("TOTAL_AGENT_AMT", i) + insParmT.getDouble("FLG_AGENT_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i);
            	}
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("2")){
//    				城居INS_CROWD_TYPE = ‘2’
//    				救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//    				统筹=TOTAL_AGENT_AMT
            		payInsHelpT += insParmT.getDouble("FLG_AGENT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i) + insParmT.getDouble("ILLNESS_SUBSIDY_AMT", i);
            		payInsT += insParmT.getDouble("TOTAL_AGENT_AMT", i);
            	}
            	unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
            }
        }
        sql = 
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
        	" AND B.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')" +
        	" AND B.AR_AMT>0 " +
        	"GROUP BY  " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        insParm = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParm.getCount()>0){
        	for (int i = 0; i < insParm.getCount(); i++) {
            	if(insParm.getData("INS_CROWD_TYPE", i).equals("1")){
            		//城职INS_CROWD_TYPE = ‘1’
            		//个人账户=ACCOUNT_PAY_AMT
            		//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
            		payInsCardY += insParm.getDouble("ACCOUNT_PAY_AMT", i);
            		payInsNhiY += insParm.getDouble("OTOT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
            		insParm.getDouble("TOTAL_AGENT_AMT", i) + insParm.getDouble("FLG_AGENT_AMT", i) + 
            		insParm.getDouble("SERVANT_AMT", i);
            	}
            	if(insParm.getData("INS_CROWD_TYPE", i).equals("2")){
//    				城居INS_CROWD_TYPE = ‘2’
//    				救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//    				统筹=TOTAL_AGENT_AMT
            		payInsHelpY += insParm.getDouble("FLG_AGENT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
            		insParm.getDouble("SERVANT_AMT", i) + insParm.getDouble("ILLNESS_SUBSIDY_AMT", i);
            		payInsY += insParm.getDouble("TOTAL_AGENT_AMT", i);
            	}
            	unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
            }
        }
        sql = 
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
        	" AND B.BILL_DATE BETWEEN TO_DATE ('"+start_date+"', 'yyyyMMddHH24miss')" +
        	" AND TO_DATE ('"+end_date+"', 'yyyyMMddHH24miss')" +
        	" AND B.AR_AMT<0 " +
        	"GROUP BY " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        insParmT = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParmT.getCount()>0){
        	for (int i = 0; i < insParmT.getCount(); i++) {
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("1")){
            		//城职INS_CROWD_TYPE = ‘1’
            		//个人账户=ACCOUNT_PAY_AMT
            		//社保基金支付=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
            		payInsCardT += insParmT.getDouble("ACCOUNT_PAY_AMT", i);
            		payInsNhiT += insParmT.getDouble("OTOT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("TOTAL_AGENT_AMT", i) + insParmT.getDouble("FLG_AGENT_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i);
            	}
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("2")){
//    				城居INS_CROWD_TYPE = ‘2’
//    				救助金额=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//    				统筹=TOTAL_AGENT_AMT
            		payInsHelpT += insParmT.getDouble("FLG_AGENT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i)+ insParmT.getDouble("ILLNESS_SUBSIDY_AMT", i);;
            		payInsT += insParmT.getDouble("TOTAL_AGENT_AMT", i);
            	}
            	unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
            }
        }
        unreimAmtS = unreimAmtY + unreimAmtT;
        payInsCardS = payInsCardY + payInsCardT ;
        payInsS = payInsY + payInsT;
        payInsHelpS = payInsHelpY + payInsHelpT;
        payInsNhiS = payInsNhiY + payInsNhiT;
        payInsTotS = payInsTotY + payInsTotT;
//		医保金额小计= 个人账户+社保基金支付+救助金额+统筹-基金未报销金额
        returnParm.setData("PAY_INS_HELP_T", StringTool.round(payInsHelpT,2));
		returnParm.setData("PAY_INS_T",  StringTool.round(payInsT,2));
		returnParm.setData("PAY_INS_CARD_T",  StringTool.round(payInsCardT,2));
		returnParm.setData("PAY_INS_NHI_T",  StringTool.round(payInsNhiT,2));
        returnParm.setData("PAY_INS_HELP_Y",  StringTool.round(payInsHelpY,2));
		returnParm.setData("PAY_INS_Y",  StringTool.round(payInsY,2));
        returnParm.setData("PAY_INS_CARD_Y", StringTool.round(payInsCardY,2));
		returnParm.setData("PAY_INS_NHI_Y", StringTool.round(payInsNhiY,2));
		returnParm.setData("PAY_INS_NHI_S", StringTool.round(payInsNhiS,2));
		returnParm.setData("PAY_INS_CARD_S",  StringTool.round(payInsCardS,2));
        returnParm.setData("PAY_INS_HELP_S",  StringTool.round(payInsHelpS,2));
        returnParm.setData("PAY_INS_S",  StringTool.round(payInsS,2));
		returnParm.setData("PAY_UNREIM_AMT_Y",  StringTool.round(unreimAmtY,2));
		returnParm.setData("PAY_UNREIM_AMT_T",  StringTool.round(unreimAmtT,2));
		returnParm.setData("PAY_UNREIM_AMT_S",  StringTool.round(unreimAmtS,2));
		returnParm.setData("PAY_INS_TOT_Y",  StringTool.round(payInsTotY,2));
		returnParm.setData("PAY_INS_TOT_T",  StringTool.round(payInsTotT,2));
		returnParm.setData("PAY_INS_TOT_S",  StringTool.round(payInsTotS,2));
		return returnParm;
    }
    /**
     * 打印日结报表
     */
    public void onPrint() {
    	TParm regOParm = getPrintNo("REG", "O");
    	TParm regEParm = getPrintNo("REG", "E");
    	TParm opbHParm = getPrintNo("OPB", "H");
    	TParm opbOParm = getPrintNo("OPB", "O");
    	TParm opbEParm = getPrintNo("OPB", "E");
    	TParm data = new TParm();
    	data.addData("REG_O_TITLE", "门诊挂号");
    	data.addData("REG_O_BIL", regOParm.getData("RECP_NO"));
    	data.addData("REG_O_COUNT",  regOParm.getData("RECP_COUNT"));
    	data.addData("REG_O_AR_AMT", regOParm.getData("AR_AMT"));
    	data.addData("REG_O_CASH", regOParm.getData("PAY_CASH"));
    	data.addData("REG_O_CHECK", regOParm.getData("PAY_CHECK"));
    	data.addData("REG_O_EKT", regOParm.getData("PAY_MEDICAL_CARD"));
    	data.addData("REG_O_TITLE", "门诊挂号作废票");
    	data.addData("REG_O_BIL", regOParm.getData("RESET_BIL"));
    	data.addData("REG_O_COUNT",  "");
    	data.addData("REG_O_AR_AMT", "");
    	data.addData("REG_O_CASH", "");
    	data.addData("REG_O_CHECK", "");
    	data.addData("REG_O_EKT", "");
    	
    	data.addData("REG_O_TITLE", "急诊挂号");
    	data.addData("REG_O_BIL", regEParm.getData("RECP_NO"));
    	data.addData("REG_O_COUNT", regEParm.getData("RECP_COUNT"));
    	data.addData("REG_O_AR_AMT", regEParm.getData("AR_AMT"));
    	data.addData("REG_O_CASH",  regEParm.getData("PAY_CASH"));
    	data.addData("REG_O_CHECK",  regEParm.getData("PAY_CHECK"));
    	data.addData("REG_O_EKT",  regEParm.getData("PAY_MEDICAL_CARD"));
    	data.addData("REG_O_TITLE", "急诊挂号作废票");
    	data.addData("REG_O_BIL", regEParm.getData("RESET_BIL"));
    	data.addData("REG_O_COUNT", "");
    	data.addData("REG_O_AR_AMT", "");
    	data.addData("REG_O_CASH",  "");
    	data.addData("REG_O_CHECK",  "");
    	data.addData("REG_O_EKT",  "");
    	
    	data.addData("REG_O_TITLE", "健康检查");
    	data.addData("REG_O_BIL",  opbHParm.getData("RECP_NO"));
    	data.addData("REG_O_COUNT",  opbHParm.getData("RECP_COUNT"));
    	data.addData("REG_O_AR_AMT",  opbHParm.getData("AR_AMT"));
    	data.addData("REG_O_CASH",  opbHParm.getData("PAY_CASH"));
    	data.addData("REG_O_CHECK", opbHParm.getData("PAY_CHECK"));
    	data.addData("REG_O_EKT", opbHParm.getData("PAY_MEDICAL_CARD"));
    	data.addData("REG_O_TITLE", "健康检查作废票");
    	data.addData("REG_O_BIL",  opbHParm.getData("RESET_BIL"));
    	data.addData("REG_O_COUNT",  "");
    	data.addData("REG_O_AR_AMT",  "");
    	data.addData("REG_O_CASH",  "");
    	data.addData("REG_O_CHECK", "");
    	data.addData("REG_O_EKT", "");
    	
    	data.addData("REG_O_TITLE", "门诊收费");
    	data.addData("REG_O_BIL",  opbOParm.getData("RECP_NO"));
    	data.addData("REG_O_COUNT", opbOParm.getData("RECP_COUNT"));
    	data.addData("REG_O_AR_AMT", opbOParm.getData("AR_AMT"));
    	data.addData("REG_O_CASH", opbOParm.getData("PAY_CASH"));
    	data.addData("REG_O_CHECK", opbOParm.getData("PAY_CHECK"));
    	data.addData("REG_O_EKT",  opbOParm.getData("PAY_MEDICAL_CARD"));
    	data.addData("REG_O_TITLE", "门诊收费作废票");
    	data.addData("REG_O_BIL",  opbOParm.getData("RESET_BIL"));
    	data.addData("REG_O_COUNT", "");
    	data.addData("REG_O_AR_AMT", "");
    	data.addData("REG_O_CASH", "");
    	data.addData("REG_O_CHECK", "");
    	data.addData("REG_O_EKT",  "");
    	
    	data.addData("REG_O_TITLE", "急诊收费");
    	data.addData("REG_O_BIL", opbEParm.getData("RECP_NO"));
    	data.addData("REG_O_COUNT", opbEParm.getData("RECP_COUNT"));
    	data.addData("REG_O_AR_AMT", opbEParm.getData("AR_AMT"));
    	data.addData("REG_O_CASH",  opbEParm.getData("PAY_CASH"));
    	data.addData("REG_O_CHECK",  opbEParm.getData("PAY_CHECK"));
    	data.addData("REG_O_EKT", opbEParm.getData("PAY_MEDICAL_CARD"));
    	data.addData("REG_O_TITLE", "急诊收费作废票");
    	data.addData("REG_O_BIL", "");
    	data.addData("REG_O_COUNT", "");
    	data.addData("REG_O_AR_AMT", "");
    	data.addData("REG_O_CASH",  "");
    	data.addData("REG_O_CHECK",  "");
    	data.addData("REG_O_EKT", "");
    	
    	data.setCount(10);
    	data.addData("SYSTEM", "COLUMNS", "REG_O_TITLE");
    	data.addData("SYSTEM", "COLUMNS", "REG_O_BIL");
    	data.addData("SYSTEM", "COLUMNS", "REG_O_COUNT");
    	data.addData("SYSTEM", "COLUMNS", "REG_O_AR_AMT");
    	data.addData("SYSTEM", "COLUMNS", "REG_O_CASH");
    	data.addData("SYSTEM", "COLUMNS", "REG_O_EKT");
    	data.addData("SYSTEM", "COLUMNS", "REG_O_CHECK");
    	TParm chargeparm = getCharge();
    	chargeparm = getChargeParm(chargeparm,chargeparm);
    	if(chargeparm.getCount()>0){
    		chargeparm.setData("TOTAL", StringTool.round(chargeparm.getDouble("O_TOT_AMT", 0)+chargeparm.getDouble("R_AR_AMT", 0),2));
    		chargeparm.setData("PAY_CASH", StringTool.round(chargeparm.getDouble("O_PAY_CASH", 0)+chargeparm.getDouble("R_PAY_CASH", 0),2));
    		chargeparm.setData("PAY_CHECK", StringTool.round(chargeparm.getDouble("O_PAY_CHECK", 0)+chargeparm.getDouble("R_PAY_CHECK", 0),2));
    		chargeparm.setData("PAY_BANK_CARD", StringTool.round(chargeparm.getDouble("O_PAY_BANK_CARD", 0)+chargeparm.getDouble("R_PAY_BANK_CARD", 0),2));
    		chargeparm.setData("PAY_MEDICAL_CARD",  StringTool.round(chargeparm.getDouble("O_PAY_MEDICAL_CARD", 0)+chargeparm.getDouble("R_PAY_MEDICAL_CARD", 0),2));
    		chargeparm.setData("R_REG_FEE",  StringTool.round(chargeparm.getDouble("R_REG_FEE", 0),2));
    		chargeparm.setData("R_CLINIC_FEE",  StringTool.round(chargeparm.getDouble("R_CLINIC_FEE", 0),2));
    		chargeparm.setData("TOTAL_Y", StringTool.round(chargeparm.getDouble("O_TOT_AMT_Y", 0)+chargeparm.getDouble("R_AR_AMT_Y", 0),2));
    		chargeparm.setData("PAY_CASH_Y", StringTool.round(chargeparm.getDouble("O_PAY_CASH_Y", 0)+chargeparm.getDouble("R_PAY_CASH_Y", 0),2));
    		chargeparm.setData("PAY_CHECK_Y", StringTool.round(chargeparm.getDouble("O_PAY_CHECK_Y", 0)+chargeparm.getDouble("R_PAY_CHECK_Y", 0),2));
    		chargeparm.setData("PAY_BANK_CARD_Y", StringTool.round(chargeparm.getDouble("O_PAY_BANK_CARD_Y", 0)+chargeparm.getDouble("R_PAY_BANK_CARD_Y", 0),2));
    		chargeparm.setData("PAY_MEDICAL_CARD_Y",  StringTool.round(chargeparm.getDouble("O_PAY_MEDICAL_CARD_Y", 0)+chargeparm.getDouble("R_PAY_MEDICAL_CARD_Y", 0),2));
    		chargeparm.setData("R_REG_FEE_Y",  StringTool.round(chargeparm.getDouble("R_REG_FEE_Y", 0),2));
    		chargeparm.setData("R_CLINIC_FEE_Y",  StringTool.round(chargeparm.getDouble("R_CLINIC_FEE_Y", 0),2));
    		chargeparm.setData("TOTAL_T", StringTool.round(chargeparm.getDouble("TOTAL")-chargeparm.getDouble("TOTAL_Y"),2));
    		chargeparm.setData("PAY_CASH_T", StringTool.round(chargeparm.getDouble("PAY_CASH")-chargeparm.getDouble("PAY_CASH_Y"),2));
    		chargeparm.setData("PAY_CHECK_T", StringTool.round(chargeparm.getDouble("PAY_CHECK")-chargeparm.getDouble("PAY_CHECK_Y"),2));
    		chargeparm.setData("PAY_BANK_CARD_T", StringTool.round(chargeparm.getDouble("PAY_BANK_CARD")-chargeparm.getDouble("PAY_BANK_CARD_Y"),2));
    		chargeparm.setData("PAY_MEDICAL_CARD_T",  StringTool.round(chargeparm.getDouble("PAY_MEDICAL_CARD_Y")-chargeparm.getDouble("PAY_MEDICAL_CARD_Y"),2));
    		chargeparm.setData("R_REG_FEE_T",  StringTool.round(chargeparm.getDouble("R_REG_FEE")-chargeparm.getDouble("R_REG_FEE_Y"),2));
    		chargeparm.setData("R_CLINIC_FEE_T",  StringTool.round(chargeparm.getDouble("R_CLINIC_FEE")-chargeparm.getDouble("R_CLINIC_FEE_Y"),2));
    	}
    	chargeparm.setData("REGOPBINV", data.getData());
    	chargeparm = getInsMessage(chargeparm);
    	chargeparm.setData("PRINT_USER", "打印人: "+Operator.getName());
    	String apDate = StringTool.getString(SystemTool.getInstance().getDate(),
        					"yyyyMMddHHmmss");
    	apDate = dealDate(apDate);
    	chargeparm.setData("PRINT_DATE", "打印时间: "+apDate);
    	chargeparm.setData("TITLE", "TEXT",
                Operator.getHospitalCHNShortName() 
                  + "门诊挂号收费汇总表");
        this.openPrintWindow(
        		"%ROOT%\\config\\prt\\opb\\OpbRegAccount.jhw", chargeparm);
    }

    /**
     * 清空
     */
    public void onClear() {
        onInit();
        this.callFunction("UI|TABLE|removeRowAll");

    }
    /**
     * 处理时间
     * @param date String
     * @return String
     */
    public String dealDate(String date) {
        String outDate = "";
        if (date == null || date.length() == 0)
            return "";
        if (date.length() >= 8)
            outDate += date.substring(0, 4) + " 年 " + date.substring(4, 6) +
                " 月 " +
                date.substring(6, 8) + " 日 ";
        if (date.length() >= 14)
            outDate += " " + date.substring(8, 10) + " 时 " +
                date.substring(10, 12) + " 分 " + date.substring(12, 14) + " 秒";
        return outDate;
    }
    

}
