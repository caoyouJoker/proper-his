package jdo.adm;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.OdiUtil;

/**
 * <p>Title: 转诊工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangbin 2015.8.24
 * @version 1.0
 */
public class ADMReferralTool extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static ADMReferralTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return ADMReferralTool
	 */
	public static ADMReferralTool getInstance() {
		if (instanceObject == null)
			instanceObject = new ADMReferralTool();
		return instanceObject;
	}
	
    /**
     * 构造器
     */
    public ADMReferralTool() {
        setModuleName("adm\\ADMReferralModule.x");
        onInit();
    }
	
	/**
	 * 查询外转院所信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm querySysTrnHosp(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * ");
		sbSql.append(" FROM SYS_TRN_HOSP ");
		sbSql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("HOSP_CODE"))) {
			sbSql.append(" AND HOSP_CODE = '");
			sbSql.append(parm.getValue("HOSP_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("HOSP_DESC"))) {
			sbSql.append(" AND HOSP_DESC = '");
			sbSql.append(parm.getValue("HOSP_DESC"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询病患基本信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryPatInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT B.*,A.IN_DATE,A.DS_DATE,A.DEPT_CODE,A.STATION_CODE,A.VS_DR_CODE,A.ATTEND_DR_CODE,A.CASE_NO,'I' AS ADM_TYPE ");
		sbSql.append(" FROM ADM_INP A, SYS_PATINFO B ");
		sbSql.append(" WHERE B.MR_NO = '");
		sbSql.append(parm.getValue("MR_NO"));
		sbSql.append("' AND A.MR_NO = B.MR_NO ");
		sbSql.append(" ORDER BY A.CASE_NO DESC ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 插入转诊转出申请表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertAdmReferralOut(TParm parm) {
		TParm result = this.update("insertAdmReferralOut", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询转诊转出申请表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryAdmReferralOut(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * ");
		sbSql.append(" FROM ADM_REFERRAL_OUT ");
		sbSql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("REF_APPLY_DATE_S"))
				&& StringUtils.isNotEmpty(parm.getValue("REF_APPLY_DATE_E"))) {
			sbSql.append(" AND REFERRAL_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("REF_APPLY_DATE_S"));
			sbSql.append("','YYYY/MM/DD') AND TO_DATE('");
			sbSql.append(parm.getValue("REF_APPLY_DATE_E"));
			sbSql.append("','YYYY/MM/DD') ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			sbSql.append(" AND MR_NO = '");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ACCEPT_HOSP_CODE"))) {
			sbSql.append(" AND ACCEPT_HOSP_CODE = '");
			sbSql.append(parm.getValue("ACCEPT_HOSP_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CANCEL_FLG"))) {
			sbSql.append(" AND CANCEL_FLG = '");
			sbSql.append(parm.getValue("CANCEL_FLG"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		int count = result.getCount();
    	String age = "";
    	for (int i = 0;i < count; i++) {
    		// 根据出生日期计算当前年龄
    		age = OdiUtil.getInstance().showAge(result.getTimestamp("BIRTH_DATE", i),
					SystemTool.getInstance().getDate());
    		result.addData("AGE", age);
    	}
		
		return result;
	}
	
	/**
	 * 查询转诊转入申请表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryAdmReferralIn(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * ");
		sbSql.append(" FROM ADM_REFERRAL_IN ");
		sbSql.append(" WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("REF_APPLY_DATE_S"))
				&& StringUtils.isNotEmpty(parm.getValue("REF_APPLY_DATE_E"))) {
			sbSql.append(" AND REFERRAL_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("REF_APPLY_DATE_S"));
			sbSql.append("','YYYY/MM/DD') AND TO_DATE('");
			sbSql.append(parm.getValue("REF_APPLY_DATE_E"));
			sbSql.append("','YYYY/MM/DD') ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("REFERRAL_NO"))) {
			sbSql.append(" AND REFERRAL_NO = '");
			sbSql.append(parm.getValue("REFERRAL_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("REF_APPLY_HOSP"))) {
			sbSql.append(" AND APPLY_HOSP_CODE = '");
			sbSql.append(parm.getValue("REF_APPLY_HOSP"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("IDNO"))) {
			sbSql.append(" AND IDNO = '");
			sbSql.append(parm.getValue("IDNO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("PAT_NAME"))) {
			sbSql.append(" AND PAT_NAME LIKE '%");
			sbSql.append(parm.getValue("PAT_NAME"));
			sbSql.append("%' ");
		}
		
		sbSql.append(" ORDER BY REFERRAL_DATE DESC ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		int count = result.getCount();
    	String age = "";
    	for (int i = 0;i < count; i++) {
    		// 根据出生日期计算当前年龄
    		age = OdiUtil.getInstance().showAge(result.getTimestamp("BIRTH_DATE", i),
					SystemTool.getInstance().getDate());
    		result.addData("AGE", age);
    	}
		
		return result;
	}
	
	/**
	 * 查询病历信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryEMRFileIndex(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * ");
		sbSql.append(" FROM EMR_FILE_INDEX ");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 提取转诊病历
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm extractEmrFile(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" UPDATE ADM_REFERRAL_IN ");
		sbSql.append(" SET EMR_FILE_EXTRACT_FLG = 'Y' ");
		sbSql.append(" WHERE REFERRAL_NO = '");
		sbSql.append(parm.getValue("REFERRAL_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
