package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 医保卡结算档
 * </p>
 * 
 * <p>
 * Description: 医保卡结算档
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdTJTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INSOpdTJTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSOpdApproveTool
	 */
	public static INSOpdTJTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSOpdTJTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INSOpdTJTool() {
		setModuleName("ins\\INSOpdTJModule.x");
		onInit();
	}

	/**
	 * 插入结算主档
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSOpd(TParm parm) {
		TParm result = update("insertINSOpd", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + ":" + result.getErrText());
		}
		return result;
	}

	/**
	 * 结算确认后修改结算表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateINSopdSettle(TParm parm) {
		TParm result = update("updateINSopdSettle", parm);
		return result;
	}

	/**
	 * INSAMT_FLG对账标志为3
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateINSOpd(TParm parm, TConnection conn) {
		TParm result = update("updateINSOpd", parm, conn);
		return result;

	}

	/**
	 * INSAMT_FLG对账标志为3
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateINSOpd(TParm parm) {
		TParm result = update("updateINSOpd", parm);
		return result;

	}

	/**
	 * 删除操作
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteINSOpd(TParm parm, TConnection conn) {
		TParm result = update("deleteINSOpd", parm, conn);
		return result;
	}

	/**
	 * 删除操作
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteINSOpd(TParm parm) {
		TParm result = update("deleteINSOpd", parm);
		return result;
	}

	/**
	 * 查询最大SEQ_NO
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectMAXSeqNo(TParm parm) {
		TParm result = query("selectMAXSeqNo", parm);
		return result;
	}

	/**
	 * 查询需要退费的门诊结算表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectResetFee(TParm parm) {
		TParm result = query("selectResetFee", parm);
		return result;
	}

	/**
	 * 添加一条退费信息
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertResetINSOpd(TParm parm, TConnection conn) {
		TParm result = update("insertResetINSOpd", parm, conn);
		return result;
	}

	/**
	 * 修改收据号
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateINSReceiptNo(TParm parm, TConnection conn) {
		TParm result = update("updateINSReceiptNo", parm, conn);
		return result;
	}

	/**
	 * 诊退费使用：是否执行医保退费操作通过票据号码
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectInsInvNo(TParm parm) {
		TParm result = query("selectInsInvNo", parm);
		return result;
	}

	/**
	 * INSAMT_FLG对账标志和票据号
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateINSOpdPrint(TParm parm, TConnection conn) {
		TParm result = update("updateINSOpdPrint", parm, conn);
		return result;
	}

	/**
	 * 门诊自动对账
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectAutoAccount(TParm parm) {
		TParm result = query("selectAutoAccount", parm);
		return result;
	}

	/**
	 * 门诊对账 查询病患就诊信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectMrNoAccount(TParm parm) {
		TParm result = query("selectMrNoAccount", parm);
		return result;
	}
	/**
	 * 查询特殊人员类别金额
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectSpecialPatAccount(TParm parm) {
		TParm result = query("selectSpecialPatAccount", parm);
		return result;
	}
	
	/**
	 * 门诊对账 查询对总账数据 结算使用
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOpdAccount(TParm parm) {
		TParm result = query("selectOpdAccount", parm);
		return result;
	}

	/**
	 * 门诊对账 查询对总账数据 结算保存使用： 正常\退费 两条数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectSaveAccount(TParm parm) {
		TParm result = query("selectSaveAccount", parm);
		return result;
	}

	/**
	 * 门诊收费医保打票数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryForPrint(TParm parm) {
		TParm result = new TParm();
		String sql = " SELECT REGION_CODE, CASE_NO, CONFIRM_NO, MR_NO, PHA_AMT, PHA_NHI_AMT, EXM_AMT,"
				+ "        EXM_NHI_AMT, TREAT_AMT, TREAT_NHI_AMT, OP_AMT, OP_NHI_AMT, BED_AMT,"
				+ "        BED_NHI_AMT, MATERIAL_AMT, MATERIAL_NHI_AMT, OTHER_AMT, OTHER_NHI_AMT,"
				+ "        BLOODALL_AMT, BLOODALL_NHI_AMT, BLOOD_AMT, BLOOD_NHI_AMT, TOT_AMT,"
				+ "        NHI_AMT, START_STANDARD_AMTS, OTOT_PAY_AMT, START_STANDARD_AMT,"
				+ "        ADD_AMT, OWN_AMT, INS_STANDARD_AMT, TRANBLOOD_OWN_AMT, APPLY_AMT,"
				+ "        OTOT_AMT, OINSTOT_AMT, INS_DATE, INSAMT_FLG, OPT_USER, OPT_DATE,"
				+ "        OPT_TERM, ACCOUNT_PAY_AMT, UNACCOUNT_PAY_AMT, ACCOUNT_LEFT_AMT,"
				+ "        REFUSE_AMT, REFUSE_ACCOUNT_PAY_AMT, REFUSE_DESC, CONFIRM_F_USER,"
				+ "        CONFIRM_F_DATE, CONFIRM_S_USER, CONFIRM_S_DATE, SLOW_DATE,"
				+ "        SLOW_PAY_DATE, REFUSE_CODE, REFUSE_OTOT_AMT, MZ_DIAG, DRUG_DAYS,"
				+ "        INS_STD_AMT, INS_CROWD_TYPE, INS_PAT_TYPE, RECP_TYPE, OWEN_PAY_SCALE,"
				+ "        REDUCE_PAY_SCALE, REAL_OWEN_PAY_SCALE, SALVA_PAY_SCALE,"
				+ "        BASEMED_BALANCE, INS_BALANCE, BCSSQF_STANDRD_AMT,"
				+ "        PERCOPAYMENT_RATE_AMT, INS_HIGHLIMIT_AMT, TOTAL_AGENT_AMT,"
				+ "        FLG_AGENT_AMT, ISSUE, ARMY_AI_AMT, SERVANT_AMT, INS_PAY_AMT,"
				+ "        UNREIM_AMT,REIM_TYPE,ILLNESS_SUBSIDY_AMT "
				+ "   FROM INS_OPD "
				+ "  WHERE  CONFIRM_NO = '"
				+ parm.getData("CONFIRM_NO")
				+ "' " + "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";	
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * 查询病患信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selPatDataForPrint(TParm parm) {
		TParm result = new TParm();
		String sql = " SELECT A.PAY_KIND, A.DISEASE_CODE, A.CTZ_CODE, A.INS_CROWD_TYPE,"
				+ "        A.INS_PAT_TYPE, A.PAT_AGE, B.NHI_NO, A.OWN_NO, A.SPECIAL_PAT,"
				+ "        A.INSCARD_PASSWORD "
				+ "   FROM INS_MZ_CONFIRM A, SYS_CTZ B "
				+ "  WHERE A.CASE_NO = '"
				+ parm.getData("CASE_NO")
				+ "' "
				+ "    AND A.CTZ_CODE = B.CTZ_CODE ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;

	}

	/**
	 * 门诊挂号费用信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selFeeForREGPrint(TParm parm) {
		TParm result = new TParm();
		String sql = " SELECT OTOT_AMT, UNACCOUNT_PAY_AMT, NHI_AMT - UNREIM_AMT AS SOTOT_AMT,"
				+ "        TOT_AMT - NHI_AMT + UNREIM_AMT AS SUNACCOUNT_PAY_AMT "
				+ "   FROM BIL_REG_RECP A, INS_OPD B "
				+ "  WHERE A.CASE_NO = '"
				+ parm.getData("CASE_NO")
				+ "'    AND A.PRINT_NO = '"
				+ parm.getData("PRINT_NO")
				+ "' "
				+ "    AND B.CASE_NO = A.CASE_NO ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;

	}
	/**
	 * 查询医保退费金额
	 * @param parm
	 * @return
	 */
	public TParm selectInsSumAmt(TParm parm){
		TParm result = query("selectInsSumAmt", parm);
		return result;
	}
	/**
	 * 票据补打修改票据号码
	 */
	public TParm updateInsOpdInvNo(TParm parm, TConnection conn){
		TParm result = update("updateInsOpdInvNo", parm);
		return result;
	}
}
