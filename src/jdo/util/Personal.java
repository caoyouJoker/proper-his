package jdo.util;

import java.util.Map;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSOperatorTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TStrike;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>
 * Title: 人员共用
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Personal extends TStrike {
	/**
	 * 根据病案号查询姓名
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return String 姓名
	 */
	public String getNameForMrno(String mrNo) {
		if (isClientlink())
			return (String) callServerMethod(mrNo);
		return PatTool.getInstance().getNameForMrno(mrNo);
	}

	/**
	 * 根据病案号查询身份证号
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return String 身份证号
	 */
	public String getIdnoForMrno(String mrNo) {
		if (isClientlink())
			return (String) callServerMethod(mrNo);
		return PatTool.getInstance().getIdnoForMrno(mrNo);
	}

	/**
	 * 根据病案号查询基本信息
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return Map TParm{MR_NO,IPD_NO,DELETE_FLG,MERGE_FLG,MOTHER_MRNO,
	 *         PAT_NAME,PAT_NAME1,PY1,PY2,FOREIGNER_FLG,
	 *         IDNO,BIRTH_DATE,HOMEPLACE_CODE,CTZ1_CODE,CTZ2_CODE,
	 *         CTZ3_CODE,TEL_COMPANY,TEL_HOME,CELL_PHONE,COMPANY_DESC,
	 *         E_MAIL,BLOOD_TYPE,BLOOD_RH_TYPE,SEX_CODE,DEAD_DATE,
	 *         POST_CODE,ADDRESS,RESID_POST_CODE,RESID_ADDRESS,CONTACTS_NAME,
	 *         RELATION_CODE,CONTACTS_TEL,CONTACTS_ADDRESS,MARRIAGE_CODE,SPOUSE_IDNO,
	 *         FATHER_IDNO,MOTHER_IDNO,RELIGION_CODE,EDUCATION_CODE,OCC_CODE,
	 *         NATION_CODE,SPECIES_CODE,FIRST_ADM_DATE,RCNT_OPD_DATE,RCNT_OPD_DEPT,
	 *         RCNT_IPD_DATE,RCNT_IPD_DEPT,RCNT_EMG_DATE,RCNT_EMG_DEPT,RCNT_MISS_DATE,
	 *         RCNT_MISS_DEPT,KID_EXAM_RCNT_DATE,KID_INJ_RCNT_DATE,ADULT_EXAM_DATE,SMEAR_RCNT_DATE,
	 *         HEIGHT,WEIGHT,DESCRIPTION,BORNIN_FLG,NEWBORN_SEQ,
	 *         PREMATURE_FLG,HANDICAP_FLG,BLACK_FLG,NAME_INVISIBLE_FLG,LAW_PROTECT_FLG,
	 *         LMP_DATE,PREGNANT_DATE,BREASTFEED_STARTDATE,BREASTFEED_ENDDATE,PAT1_CODE,
	 *         PAT2_CODE,PAT3_CODE}
	 */
	public Map getInfoForMrno(String mrNo) {
		if (isClientlink())
			return (Map) callServerMethod(mrNo);
		return PatTool.getInstance().getInfoForMrno(mrNo).getData();
	}

	/**
	 * 病案号补零
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return String 完整病案号
	 */
	public String checkMrno(String mrNo) {
		if (isClientlink())
			return (String) callServerMethod(mrNo);
		return PatTool.getInstance().checkMrno(mrNo);
	}

	/**
	 * 住院号补零
	 *
	 * @param ipdNo
	 *            String 住院号
	 * @return String 完整住院号
	 */
	public String checkIpdno(String ipdNo) {
		if (isClientlink())
			return (String) callServerMethod(ipdNo);
		return PatTool.getInstance().checkIpdno(ipdNo);
	}

	/**
	 * 得到病案号长度
	 *
	 * @return int 病案号长度
	 */
	public int getMrNoLength() {
		return PatTool.getInstance().getMrNoLength();
	}

	/**
	 * 得到住院号长度
	 *
	 * @return int 住院号长度
	 */
	public int getIpdNoLength() {
		return PatTool.getInstance().getIpdNoLength();
	}

	/**
	 * 得到合并后病案号
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return String 合并后病案号
	 */
	public String getMergeMrno(String mrNo) {
		if (isClientlink())
			return (String) callServerMethod(mrNo);
		return PatTool.getInstance().getMergeMrno(mrNo);
	}

	/**
	 * 是否存在病患
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return boolean ture 存在 false 不存在
	 */
	public boolean existsPat(String mrNo) {
		if (isClientlink())
			return (Boolean) callServerMethod(mrNo);
		return PatTool.getInstance().existsPat(mrNo);
	}

	/**
	 * 加锁病患
	 *
	 * @param mrNo
	 *            String 病案号
	 * @param program
	 *            String 程序名
	 * @return boolean true 成功 false 失败
	 */
	public boolean lockPat(String mrNo, String program) {
		if (!isClientlink()) {
			System.out.println("不能后台调用lockPat(String mrNo,String program)方法");
			return false;
		}
		return lockPat(mrNo, program, Operator.getID(), Operator.getIP());
	}

	/**
	 * 加锁病患(后台调用)
	 *
	 * @param mrNo
	 *            String 病案号
	 * @param program
	 *            String 程序名
	 * @param userId
	 *            String 操作人员
	 * @param userIp
	 *            String 操作终端
	 * @return boolean
	 */
	public boolean lockPat(String mrNo, String program, String userId,
			String userIp) {
		if (isClientlink())
			return (Boolean) callServerMethod(mrNo, program, userId, userIp);
		return PatTool.getInstance().lockPat(mrNo, program, userId, userIp);
	}

	/**
	 * 是否加锁
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return boolean true 已加锁 false 未加锁
	 */
	public boolean isLockPat(String mrNo) {
		if (isClientlink())
			return (Boolean) callServerMethod(mrNo);
		return PatTool.getInstance().isLockPat(mrNo);
	}

	/**
	 * 得到加锁信息
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return Map TParm{PRG_ID,OPT_USER,OPT_TERM,OPT_DATE}
	 */
	public Map getLockPat(String mrNo) {
		if (isClientlink())
			return (Map) callServerMethod(mrNo);
		return PatTool.getInstance().getLockPat(mrNo).getData();
	}

	/**
	 * 解锁病患
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return boolean true 成功 false 失败
	 */
	public boolean unLockPat(String mrNo) {
		if (isClientlink())
			return (Boolean) callServerMethod(mrNo);
		return PatTool.getInstance().unLockPat(mrNo);
	}

	/**
	 * 得到加锁提示信息
	 *
	 * @param mrNo
	 *            String 病案号
	 * @return String
	 */
	public String getLockParmString(String mrNo) {
		if (isClientlink())
			return (String) callServerMethod(mrNo);
		return PatTool.getInstance().getLockParmString(mrNo);
	}

	/**
	 * 病患信息(For ADM)
	 *
	 * @param map
	 *            Map TParm{MR_NO;IPD_NO;PAT_NAME;TEL_HOME;ID_NO;BIRTH_DATE}
	 * @return Map TParm{MR_NO,IPD_NO,PAT_NAME,IDNO,TEL_HOME,BIRTH_DATE}
	 */
	public Map queryPat(Map map) {
		TParm parm = new TParm(map);
		if (isClientlink())
			return (Map) callServerMethod(map);
		return PatTool.getInstance().queryPat(parm).getData();
	}

	/**
	 * 用户基本信息
	 * @param user_id 用户ID
	 * @return Map TParm{USER_ID,USER_NAME,PY1,PY2,ID_CODE,
	 * SEX_CODE,USER_PASSWORD,POS_CODE,ROLE_ID,ACTIVE_DATE,
	 * END_DATE,PUB_FUNCTION,E_MAIL,LCS_NO,EFF_LCS_DATE,
	 * END_LCS_DATE,FULLTIME_FLG,CTRL_FLG,REGION_CODE,RCNT_LOGIN_DATE,
	 * RCNT_LOGOUT_DATE,RCNT_IP}
	 */
        public Map getSYSOperator(String user_id) {
            if (isClientlink())
                return (Map) callServerMethod(user_id);
            //=======pangben modify 20110531 start
            String regionCode = "";
            if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
                regionCode = Operator.getRegion();
            return SYSOperatorTool.getInstance().getOperator(user_id, regionCode).
                    getData();
            //=========pangben modify 20110531 stop
	}
        /**
         * 得到默认身份
         * @return String
         */
        public static String getDefCtz() {
            String sql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE DEF_CTZ_FLG='Y'";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0)
                return "";
            if (result.getCount("CTZ_CODE") == 0)
                return "";
            return result.getValue("CTZ_CODE", 0);
        }

}
