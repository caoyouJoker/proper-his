package jdo.ekt;

import java.math.BigDecimal;
import java.util.List;

import jdo.bil.BILStrike;
import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.opd.OrderTool;
import jdo.reg.PanelTypeFeeTool;
import jdo.reg.PatAdmTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 预收费工具类
 * </p>
 * 
 * <p>
 * Description: 预收费工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp
 * @version 1.0
 */
public class EKTpreDebtTool extends TJDODBTool {

	/**
	 * 实例
	 */
	private static EKTpreDebtTool instanceObject;
	public static String PAY_TOHER3 = "LPK";
	public static String PAY_TOHER4 = "XJZKQ";

	/**
	 * 得到实例
	 * 
	 * @return EKTpreDebtTool
	 */
	public static EKTpreDebtTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTpreDebtTool();
		return instanceObject;
	}
	
	public TParm getMasterAndFee(ODO odo){
		
		double exedAmt = 0;
		double opdOrderAmt = 0;//医嘱总金额
		double clinicFee = 0;//诊查费
		double master = 0;//医疗卡余额
		double billedAmt = 0;
		
		OpdOrder opdOrder = odo.getOpdOrder();
		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");

		String caseNo = odo.getCaseNo();
		String mrNo = odo.getMrNo();
		
		TParm allParm = opdOrder.getBuffer(OpdOrder.FILTER);
		
		for (int i = 0; i < allParm.getCount(); i++) {
			
			if( allParm.getValue("MEM_PACKAGE_ID", i).length() == 0 ){
				
				if( !"CLINIC_FEE".equals(allParm.getValue("RX_NO", i)) ){
					opdOrderAmt += allParm.getDouble("AR_AMT", i);
				}else{
					clinicFee += allParm.getDouble("AR_AMT", i);
				}
				
				if(allParm.getBoolean("BILL_FLG", i)){
					billedAmt += allParm.getDouble("AR_AMT", i);
				}
				
				if(allParm.getBoolean("EXEC_FLG", i)){
					exedAmt += allParm.getDouble("AR_AMT", i);
				}
				
			}
			
		}
		
		if(clinicFee == 0){
			clinicFee = getClinicFee(caseNo);
		}
		
		opdOrderAmt += clinicFee;
		
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();
		
		TParm result = new TParm();
		master = getEkeMaster(mrNo);
		result.setData("EXE", exedAmt);
		result.setData("OPB", opdOrderAmt - exedAmt);
		result.setData("MASTER", master);
		result.setData("BILLAMT", billedAmt);
		return result;
	}
	

	/**
	 * 医生站预收费接口
	 * 
	 * @param odo
	 * @return
	 */
	public TParm checkMasterForOdo(ODO odo) {

		double opdOrderAmt = 0;//医嘱总金额
		double clinicFee = 0;//诊查费
		double master = 0;//医疗卡余额

		String caseNo = odo.getCaseNo();
		String mrNo = odo.getMrNo();
		OpdOrder opdOrder = odo.getOpdOrder();
		
		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		
		TParm allParm = opdOrder.getBuffer(OpdOrder.FILTER);
		
		for (int i = 0; i < allParm.getCount(); i++) {
			
			if( !"CLINIC_FEE".equals(allParm.getValue("RX_NO", i)) ){
				
				if( allParm.getValue("MEM_PACKAGE_ID", i).length() == 0 ){
					opdOrderAmt += allParm.getDouble("AR_AMT", i);
				}
				
			}else{
				clinicFee += allParm.getDouble("AR_AMT", i);
			}
			
		}
		
		if(clinicFee == 0){
			clinicFee = getClinicFee(caseNo);
		}
		
		opdOrderAmt += clinicFee;
		
		TParm result = new TParm();

		master = getEkeMaster(mrNo) + getCtzOverDraft(caseNo) + getGreenPath(caseNo);
		
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();

		if (master - opdOrderAmt >= 0) {
			return result;
		} else {
			result.setErrCode(-1);
			result.setErrText("医疗费:"+StringTool.round(opdOrderAmt, 2)+" 预交金余额:"+StringTool.round(master, 2) + " 预交金不足");
		}
		return result;
	}

	/**
	 * 静点接口
	 * @param caseNo
	 * @param mrNo
	 * @return
	 */
	public double getMasterForPHL(String caseNo, String mrNo) {
		

		double exedAmt = 0;//执行过的医嘱
		double master = 0;//医疗卡余额

		TParm parmOne = new TParm();
		parmOne.setData("CASE_NO", caseNo);
		TParm orderOldParm = OrderTool.getInstance().selDataForOPBEKT(parmOne);
		for (int i = 0; i < orderOldParm.getCount(); i++) {
			// EXEC_FLG
			if ((orderOldParm.getBoolean("EXEC_FLG", i) && !orderOldParm.getBoolean("BILL_FLG", i) && !orderOldParm.getValue("CAT1_TYPE",i).equals("PHA")) || 
					(orderOldParm.getValue("PHA_DOSAGE_CODE",i).length()>0 && orderOldParm.getValue("PHA_RETN_CODE",i).length()==0 && orderOldParm.getValue("CAT1_TYPE",i).equals("PHA"))) {
				if (orderOldParm.getValue("MEM_PACKAGE_ID", i).length() == 0){
					exedAmt += orderOldParm.getDouble("AR_AMT", i);
				}
			}
		}

		master = getEkeMaster(mrNo);

		return master - exedAmt;
	}
	
	/**
	 * 计费点接口
	 * 
	 * @param orderParm
	 * @return
	 */
	public TParm checkMasterForExe(TParm orderParm) {

		double exeAmt = 0;//要执行的医嘱
		double exedAmt = 0;//执行过的医嘱
		double sum = 0;//汇总
		double master = 0;//医疗卡余额

		String caseNo = orderParm.getValue("CASE_NO");
		String mrNo = orderParm.getValue("MR_NO");

		TParm parmOne = new TParm();
		parmOne.setData("CASE_NO", caseNo);
		TParm orderOldParm = OrderTool.getInstance().selDataForOPBEKT(parmOne);
		for (int i = 0; i < orderOldParm.getCount(); i++) {
			// EXEC_FLG
			if ((orderOldParm.getBoolean("EXEC_FLG", i) && !orderOldParm.getBoolean("BILL_FLG", i) && !orderOldParm.getValue("CAT1_TYPE",i).equals("PHA")) || 
					(orderOldParm.getValue("PHA_DOSAGE_CODE",i).length()>0 && orderOldParm.getValue("PHA_RETN_CODE",i).length()==0 && orderOldParm.getValue("CAT1_TYPE",i).equals("PHA"))) {
				if (orderOldParm.getValue("MEM_PACKAGE_ID", i).length() == 0){
					exedAmt += orderOldParm.getDouble("AR_AMT", i);
				}
			}
			for (int j = 0; j < orderParm.getCount("RX_NO"); j++) {
				// SEQ_NO
				if (orderOldParm.getValue("RX_NO", i).equals(
						orderParm.getValue("RX_NO", j))
						&& orderOldParm.getValue("SEQ_NO", i).equals(
								orderParm.getValue("SEQ_NO", j))) {
					if(!orderOldParm.getBoolean("BILL_FLG", i)){
						if (orderOldParm.getValue("MEM_PACKAGE_ID", i).length() == 0){
							exeAmt += orderOldParm.getDouble("AR_AMT", i);
						}
					}
				}
			}
		}

		sum = exedAmt + exeAmt;

		master = getEkeMaster(mrNo) + getCtzOverDraft(caseNo) + getGreenPath(caseNo);

		TParm result = new TParm();
		if (master - sum >= 0) {
			return result;
		} else {
			result.setErrCode(-1);
			result.setErrText("医疗费:"+StringTool.round(sum,2)+" 预交金余额:"+StringTool.round(master,2) + " 预交金不足");
		}
		return result;
	}

	/**
	 * 取余额
	 * 
	 * @param mrNo
	 * @return
	 */
	public double getEkeMaster(String mrNo) {
		String sql = " SELECT B.CURRENT_BALANCE,A.CARD_NO "
				+ " FROM EKT_ISSUELOG A, EKT_MASTER B " + " WHERE A.MR_NO = '"
				+ mrNo + "' "
				+ " AND A.WRITE_FLG = 'Y' AND A.CARD_NO = B.CARD_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getDouble("CURRENT_BALANCE", 0);
	}
	/**
	 * 取余额
	 * 
	 * @param mrNo
	 * @return
	 */
	public TParm getEktCardAndAmt(String mrNo) {
		String sql = " SELECT B.CURRENT_BALANCE,A.CARD_NO "
				+ " FROM EKT_ISSUELOG A, EKT_MASTER B " + " WHERE A.CARD_NO = B.CARD_NO AND A.MR_NO = '"
				+ mrNo + "' "
				+ " AND A.WRITE_FLG = 'Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	public double getCtzOverDraft(String caseNo){
		String sql =
			" SELECT CTZ1_CODE" +
			" FROM REG_PATADM" +
			" WHERE CASE_NO = '" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		sql =
			" SELECT CASE WHEN OVERDRAFT IS NULL THEN 0 ELSE OVERDRAFT END OVERDRAFT" +
			" FROM SYS_CTZ" +
			" WHERE CTZ_CODE = '" + result.getValue("CTZ1_CODE", 0) + "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getDouble("OVERDRAFT", 0);
	}
	
	/**
	 * 门诊收费保存医嘱接口
	 * @param orderParm
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
	public TParm checkMasterForCharge(TParm orderParm ,String mrNo ,String caseNo) {
		double opbAmt = 0;//门诊金额
		double master = 0;//余额
		
		TParm parmOne = new TParm();
		parmOne.setData("CASE_NO", caseNo);
		TParm orderOldParm = OrderTool.getInstance().selDataForOPBEKT(
				parmOne);
		
		TParm newParm = orderParm.getParm("New");
		TParm ModifiedParm = orderParm.getParm("Modified");
		
		for (int i = 0; i < newParm.getCount(); i++) {
			if(newParm.getValue("MEM_PACKAGE_ID", i).length() == 0 || newParm.getValue("MEM_PACKAGE_ID", i).equals("<TNULL>")){
				opbAmt += newParm.getDouble("AR_AMT", i);
			}
		}
		
		for (int i = 0; i < ModifiedParm.getCount(); i++) {
			if(ModifiedParm.getValue("MEM_PACKAGE_ID", i).length() == 0 || ModifiedParm.getValue("MEM_PACKAGE_ID", i).equals("<TNULL>")){
				opbAmt += ModifiedParm.getDouble("AR_AMT", i);
			}
			for (int j = 0; j < orderOldParm.getCount(); j++) {
				if(orderOldParm.getValue("RX_NO", j).equals(ModifiedParm.getValue("RX_NO", i)) 
						&& orderOldParm.getValue("SEQ_NO", j).equals(ModifiedParm.getValue("SEQ_NO", i))){
					if(orderOldParm.getValue("MEM_PACKAGE_ID", j).length() == 0){
						opbAmt -= 2*orderOldParm.getDouble("AR_AMT", j);
					}
				}
			}
		}
		
		for (int i = 0; i < orderOldParm.getCount(); i++) {
			if(orderOldParm.getValue("MEM_PACKAGE_ID", i).length() == 0){
				opbAmt += orderOldParm.getDouble("AR_AMT", i);
			}
			if(orderOldParm.getBoolean("BILL_FLG", i)){
				if(orderOldParm.getValue("MEM_PACKAGE_ID", i).length() == 0){
					opbAmt -= orderOldParm.getDouble("AR_AMT", i);
				}
			}
		}
		
		master = getEkeMaster(mrNo) + getCtzOverDraft(caseNo) + getGreenPath(caseNo);
		
		TParm result = new TParm();
		if (master - opbAmt >= 0) {
			return result;
		} else {
			result.setErrCode(-1);
			result.setErrText("医疗费:"+StringTool.round(opbAmt,2)+" 预交金余额:"+StringTool.round(master,2) + " 预交金不足");
		}
		return result;
	}
	
	/**
	 * 医生站插入诊查费
	 * @param caseNo
	 * @param regionCode
	 * @param optUser
	 * @param optTerm
	 * @return
	 */
	public TParm insertOpdClinicFee(String caseNo,String regionCode,String optUser,String optTerm){
		
		TParm oneParm = new TParm();
		oneParm.setData("CASE_NO", caseNo);
		TParm regParm = PatAdmTool.getInstance().selectdata(oneParm);
		regParm = regParm.getRow(0);
		//add by huangtt 20151119 已看诊的不用在生诊察费 start
		 boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			if (!"N".equals(regParm.getValue("SEE_DR_FLG"))) {
				return new TParm();
			}

		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: EKTpreDebtTool ，method ：insertOpdClinicFee");
				e.printStackTrace();
			}
		}
		//add by huangtt 20151119 已看诊的不用在生诊察费 end
		
//		SEE_DR_FLG
		String regSql =
			" SELECT COUNT (*) COUNT" +
			" FROM OPD_ORDER" +
			" WHERE CASE_NO = '" + caseNo + "' AND RX_NO = 'CLINIC_FEE' AND SEQ_NO = 1";
		TParm regCilParm = new TParm(TJDODBTool.getInstance().select(regSql));
		if(regCilParm.getInt("COUNT",0) == 0){
			TParm result = PanelTypeFeeTool.getInstance().getOrderCodeDetial(regParm.getValue("ADM_TYPE"),
	        		regParm.getValue("CLINICTYPE_CODE"), "CLINIC_FEE");
	        String orderCode = result.getValue("ORDER_CODE", 0);
	        TParm sysFeeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
	        sysFeeParm = sysFeeParm.getRow(0);
	        double clinicFee = BILStrike.getInstance().chargeCTZ(regParm.getValue("CTZ1_CODE"), regParm.getValue("CTZ2_CODE"), regParm.getValue("CTZ3_CODE"), orderCode,
	        		regParm.getValue("SERVICE_LEVEL"));
	        
	        String hexpCode = sysFeeParm.getValue("CHARGE_HOSP_CODE");
	        String sql = 
	        	"SELECT OPD_CHARGE_CODE FROM SYS_CHARGE_HOSP WHERE CHARGE_HOSP_CODE = '" + hexpCode + "'";
	        TParm rexpParm = new TParm(TJDODBTool.getInstance().select(sql));
	        String rexpCode = rexpParm.getValue("OPD_CHARGE_CODE", 0);
	        
	        sql = 
	        	" SELECT COST_CENTER_CODE" +
	        	" FROM SYS_DEPT" +
	        	" WHERE DEPT_CODE = '" + regParm.getValue("REALDEPT_CODE") + "'";
	        TParm costCenterParm = new TParm(TJDODBTool.getInstance().select(sql));
	        
	        String costCenterCode = costCenterParm.getValue("COST_CENTER_CODE", 0);
	        
	        double rate = 1;
	        
	        if(sysFeeParm.getDouble("OWN_PRICE") != 0){
	        	rate = StringTool.round((clinicFee / sysFeeParm.getDouble("OWN_PRICE")),2);
	        }
			
			sql =
				" INSERT INTO OPD_ORDER" +
				" (CASE_NO, RX_NO, SEQ_NO, PRESRT_NO, REGION_CODE, " +
				" MR_NO, ADM_TYPE, RX_TYPE, TEMPORARY_FLG, RELEASE_FLG, " +
				" LINKMAIN_FLG, ORDER_CODE, ORDER_DESC, ORDER_CAT1_CODE, MEDI_QTY, " +
				" MEDI_UNIT, TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, DISPENSE_QTY, " +
				" DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, " +
				" OWN_AMT, AR_AMT, DR_CODE, ORDER_DATE, DEPT_CODE, " +
				" EXEC_DEPT_CODE, SETMAIN_FLG, ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG, " +
				" FILE_NO, URGENT_FLG, INSPAY_TYPE, PHA_TYPE, DOSE_TYPE, " +
				" EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, PRESCRIPT_NO, ATC_FLG, BILL_FLG, " +
				" PRINT_FLG, REXP_CODE, HEXP_CODE, CTZ1_CODE, DCT_TAKE_QTY, " +
				" PACKAGE_TOT, DCTAGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, " +
				" MED_APPLY_NO, CAT1_TYPE, COUNTER_NO, EXEC_FLG, RECEIPT_FLG, " +
				" BILL_TYPE, FINAL_TYPE, COST_AMT, COST_CENTER_CODE, BATCH_SEQ1, " +
				" VERIFYIN_PRICE1, DISPENSE_QTY1, BATCH_SEQ2, VERIFYIN_PRICE2, DISPENSE_QTY2, " +
				" BATCH_SEQ3, VERIFYIN_PRICE3, DISPENSE_QTY3, EXEC_DR_CODE, EXEC_DATE,UPDATE_TIME)" +
				" VALUES" +
				" ('" + caseNo + "', 'CLINIC_FEE', 1, '', '" + regionCode + "', " +
				" '" + regParm.getValue("MR_NO") + "', '" + regParm.getValue("ADM_TYPE") + "', '9', 'N', 'N', " +
				" 'N', '" + orderCode + "', '" + sysFeeParm.getValue("ORDER_DESC") + "', '" + sysFeeParm.getValue("ORDER_CAT1_CODE") + "', 1, " +
				" '', 1, 1, '', 1, " +
				" '', '', " + sysFeeParm.getValue("OWN_PRICE") + ", " + sysFeeParm.getValue("NHI_PRICE") + ", " + rate + ", " +
				" " + sysFeeParm.getValue("OWN_PRICE") + ", " + clinicFee + ", '" + regParm.getValue("REALDR_CODE") + "', SYSDATE, '" + regParm.getValue("REALDEPT_CODE") + "', " +
				" '" + regParm.getValue("REALDEPT_CODE") + "', 'N', '', '', 'N', " +
				" '', 'N', 'C', 'W', 'O', " +
				" 'N', 'N', 0, 'N', 'N', " +
				" 'N', '" + rexpCode + "', '" + hexpCode + "', '" + regParm.getValue("CTZ1_CODE") + "', 0, " +
				" 0, 'N', '" + optUser + "', SYSDATE, '" + optTerm + "', " +
				" '', '" + sysFeeParm.getValue("CAT1_TYPE") + "', 0, 'Y', 'N', " +
				" 'C', 'F', 0, '" + costCenterCode + "', 0, " +
				" 0, 0, 0, 0, 0, " +
				" 0, 0, 0, '" + optUser + "', SYSDATE,'"+SystemTool.getInstance().getUpdateTime()+"')";
			
			TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
			return result1;
		}
		return new TParm();
	}
	
	/**
	 * 取得诊查费
	 * @param caseNo
	 * @return
	 */
	public double getClinicFee(String caseNo){
		TParm oneParm = new TParm();
		oneParm.setData("CASE_NO", caseNo);
		TParm regParm = PatAdmTool.getInstance().selectdata(oneParm);
		regParm = regParm.getRow(0);
		TParm result = PanelTypeFeeTool.getInstance().getOrderCodeDetial(regParm.getValue("ADM_TYPE"),
        		regParm.getValue("CLINICTYPE_CODE"), "CLINIC_FEE");
        String orderCode = result.getValue("ORDER_CODE", 0);
        double clinicFee = BILStrike.getInstance().chargeCTZ(regParm.getValue("CTZ1_CODE"), regParm.getValue("CTZ2_CODE"), regParm.getValue("CTZ3_CODE"), orderCode,
        		regParm.getValue("SERVICE_LEVEL"));
        return clinicFee;
	}
	
	/**
	 * 检查诊查费
	 * @param caseNo
	 * @return
	 */
	public TParm checkClinicFee(String caseNo){
		TParm oneParm = new TParm();
		oneParm.setData("CASE_NO", caseNo);
		TParm regParm = PatAdmTool.getInstance().selectdata(oneParm);
		regParm = regParm.getRow(0);
		if(regParm.getValue("SEE_DR_FLG").equals("N")){
			TParm result = PanelTypeFeeTool.getInstance().getOrderCodeDetial(regParm.getValue("ADM_TYPE"),
	        		regParm.getValue("CLINICTYPE_CODE"), "CLINIC_FEE");
	        String orderCode = result.getValue("ORDER_CODE", 0);
	        double clinicFee = BILStrike.getInstance().chargeCTZ(regParm.getValue("CTZ1_CODE"), regParm.getValue("CTZ2_CODE"), regParm.getValue("CTZ3_CODE"), orderCode,
	        		regParm.getValue("SERVICE_LEVEL"));
			double master = getEkeMaster(regParm.getValue("MR_NO")) + getCtzOverDraft(caseNo) + getGreenPath(caseNo);
			TParm result1 = new TParm();
			if(master - clinicFee >= 0){
				return result1;
			}else{
				result1.setErrCode(-1);
				result1.setErrText("诊查费:"+StringTool.round(clinicFee,2)+" 预交金余额:"+StringTool.round(master,2) + " 预交金不足");
				return result1;
			}
		}
		return new TParm();
	}
	
	/**
	 * 退挂
	 * @param caseNo
	 * @return
	 */
	public TParm unRegForPre(String caseNo){
		String sql = 
			"SELECT COUNT(CASE_NO) COUNT FROM OPD_ORDER WHERE CASE_NO = '" + caseNo + "' AND RX_NO <> 'CLINIC_FEE'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getInt("COUNT", 0) > 0){
			result.setErrCode(-2);
			result.setErrText("已开医嘱无法退挂");
			return result;
		}else{
			sql =
				"DELETE OPD_ORDER WHERE CASE_NO = '" + caseNo + "' AND RX_NO = 'CLINIC_FEE' AND SEQ_NO = 1";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if(result.getErrCode()<0){
				return result;
			}
		}
		return new TParm();
	}
	
	/**
	 * 医技获得opdOrder
	 * @param parm
	 * @return
	 */
	public TParm getMedOpdOrder(TParm parm){
		String applyNos = "";
		for (int i = 0; i < parm.getCount("APPLICATION_NO"); i++) {
			applyNos += "'"+parm.getValue("APPLICATION_NO", i)+"',";
		}
		applyNos = applyNos.substring(0, applyNos.length()-1);
		String sql = 
			" SELECT " +
			" CASE_NO, RX_NO, SEQ_NO " +
			" FROM OPD_ORDER" +
			" WHERE" +
			" MED_APPLY_NO IN (" + applyNos + ")";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	private double getOverDraft(String caseNo){
		String sql = 
			" SELECT CTZ1_CODE" +
			" FROM REG_PATADM" +
			" WHERE CASE_NO = '" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = 
			" SELECT OVERDRAFT" +
			" FROM SYS_CTZ" +
			" WHERE CTZ_CODE = '" + parm.getValue("CTZ1_CODE", 0) + "'";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("OVERDRAFT", 0);
	}
	
	public double getPayOther(String mrNo, String payType){
		String sql =
			" SELECT SUM(AMT - USED_AMT) AMT" +
			" FROM EKT_BIL_PAY" +
			" WHERE MR_NO = '" + mrNo + "' AND GATHER_TYPE = '" + payType + "' AND AMT > USED_AMT" +
		    " AND ACCNT_TYPE = '4'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("AMT", 0);
	}
	/**
	 * 获得此病患的所有礼品卡和现金折扣券金额，门诊减免操作时，打票和退票操作使用
	 * @param mrNo
	 * @return
	 * =====正流程打票操作使用
	 * ==========pangben 2014-7-16
	 */
	public TParm getPayOtherSum(String mrNo,String lpk,String xjzkq){
		String sql =
			" SELECT GATHER_TYPE,AMT - USED_AMT AS  AMT,CARD_NO,BIL_BUSINESS_NO,AMT AS SUM_AMT,USED_AMT " +
			" FROM EKT_BIL_PAY" +
			" WHERE MR_NO = '" + mrNo + "' AND GATHER_TYPE IN ('"+lpk+"','"+xjzkq+"')" +
		    " AND ACCNT_TYPE = '4' ORDER BY GATHER_TYPE,AMT,USED_AMT DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获得此病患的所有礼品卡和现金折扣券金额，门诊减免操作操作使用
	 * @param mrNo
	 * @return
	 * 逆流成使用退票操作使用
	 * ==========pangben 2014-7-16
	 */
	public TParm getPayOtherSumRe(String mrNo,String lpk,String xjzkq){
		String sql =
			" SELECT GATHER_TYPE,AMT - USED_AMT AS  AMT,CARD_NO,BIL_BUSINESS_NO,AMT AS SUM_AMT,USED_AMT " +
			" FROM EKT_BIL_PAY" +
			" WHERE MR_NO = '" + mrNo + "' AND GATHER_TYPE IN ('"+lpk+"','"+xjzkq+"') AND AMT > USED_AMT" +
		    " AND ACCNT_TYPE = '4' ORDER BY GATHER_TYPE,AMT,USED_AMT DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 更新礼品卡、现金折扣券使用金额
	 * @param parm
	 * @param connection
	 * @return
	 * =============pangben 2014-7-17
	 */
	public TParm updateEktBilPayUsedAmt(TParm parm,double usedAmt,
			TConnection connection){
		String sql =
			" UPDATE EKT_BIL_PAY SET USED_AMT=" +usedAmt+
			" ,OPT_DATE=SYSDATE,OPT_USER='"+parm.getValue("OPT_USER")+"',OPT_TERM='"+parm.getValue("OPT_TERM")+"' WHERE CARD_NO = '" + parm.getValue("CARD_NO") + "' AND BIL_BUSINESS_NO ='"+parm.getValue("BIL_BUSINESS_NO")+"'" ;
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,connection));
		return result;
	}
	/**
	 * 
	 * @param payOther3
	 * @param payOther4
	 * @param arAmt
	 * @param payOtherTop3
	 * @param payOtherTop4
	 * @return
	 */
	public TParm checkPayOther(double payOther3, double payOther4, double arAmt, double payOtherTop3, double payOtherTop4){
		TParm parm = new TParm();
		if(payOther3 + payOther4 > arAmt){
			parm.setErr(-1, "礼金卡金额与代金券金额之和大于应付金额");
		}
		if(payOther3 > payOtherTop3){
			parm.setErr(-2, "礼金卡金额超出礼金卡余额");
		}
		if(payOther4 > payOtherTop4){
			parm.setErr(-3, "代金券金额超出代金券余额");
		}
		return parm;
	}
	
	public double getGreenPath(String caseNo){
		String sql =
			" SELECT " +
			" CASE WHEN GREEN_BALANCE IS NULL THEN 0 ELSE GREEN_BALANCE END" +
			" AMT" +
			" FROM REG_PATADM" +
			" WHERE CASE_NO = '" + caseNo + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("AMT", 0);
	}
	
	/**
	 * 取得已执行未结账不包括套餐
	 * @param mrNo
	 * @return
	 */
	public double getExecAmt(String mrNo){
		String sql =
			" SELECT SUM (AR_AMT) AR_AMT" +
			" FROM OPD_ORDER" +
			" WHERE     MR_NO = '" + mrNo + "'" +
			" AND EXEC_FLG = 'Y'" +
			" AND BILL_FLG = 'N'" +
			" AND MEM_PACKAGE_ID IS NULL";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("AR_AMT", 0);
	}
	
	/**
	 * 补充计费接口
	 * @param caseNo
	 * @param mrNo
	 * @param list
	 * @param updateList
	 * @param deletList
	 * @return
	 */
	public String checkMasterForOpb(String caseNo, String mrNo, List<com.javahis.ui.testOpb.bean.OpdOrder> list) {
		
		BigDecimal opbAmt = new BigDecimal(0);//门诊金额
		double master = 0;//余额
		
		for (com.javahis.ui.testOpb.bean.OpdOrder opdOrder : list) {
			// if((opdOrder.memPackageId == null || opdOrder.memPackageId.length() == 0) && "N".equals(opdOrder.billFlg)){
				opbAmt = opbAmt.add(opdOrder.showAmt);
			// }
		}
		
		master = getEkeMaster(mrNo) + getCtzOverDraft(caseNo) + getGreenPath(caseNo);
		
		String result = "";
		if (master - opbAmt.doubleValue() >= 0) {
			return result;
		} else {
			result = "医疗费:"+StringTool.round(opbAmt.doubleValue(),2)+" 预交金余额:"+StringTool.round(master,2) + " 预交金不足";
		}
		return result;
	}
}
