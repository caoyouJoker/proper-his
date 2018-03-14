package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:病历主索引Tool
 * </p>
 *
 * <p>
 * Description:病历主索引Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.06.14
 * @version 1.0
 */
public class SYSEmrIndexTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SYSEmrIndexTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return PositionTool
	 */
	public static SYSEmrIndexTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSEmrIndexTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSEmrIndexTool() {
		setModuleName("sys\\SYSEmrIndexModule.x");
		onInit();
	}

	/**
	 * 门诊、急诊挂号写入
	 *
	 * @param case_no
	 *            就诊号
	 * @param adm_type
	 *            门急住别
	 * @param region_code
	 *            区域
	 * @param mr_no
	 *            病案号
	 * @param adm_date
	 *            看诊日
	 * @param dept_code
	 *            科室
	 * @param dr_code
	 *            医生
	 * @param opt_user
	 *            操作者
	 * @param opt_date
	 *            操作时间
	 * @param opt_term
	 *            操作IP
	 * @param conn
	 *            DB连接
	 * @return
	 */
	public boolean onInsertOpdEmg(String case_no, String adm_type,
			String region_code, String mr_no, Timestamp adm_date,
			String dept_code, String dr_code, String opt_user,
			String opt_term, TConnection conn) {
		TParm result = new TParm();
		// 判断是否已存在该数据
		if (existsData(case_no)) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("ADM_TYPE", adm_type);
		parm.setData("REGION_CODE", region_code);
		parm.setData("MR_NO", mr_no);
		parm.setData("ADM_DATE", adm_date);
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("DR_CODE", dr_code);
		parm.setData("OPT_USER", opt_user);
		parm.setData("OPT_TERM", opt_term);
		result = this.update("insertOpdEmg", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		return true;
	}

	/**
	 * 住院登记写入
	 *
	 * @param case_no
	 *            就诊号
	 * @param adm_type
	 *            门急住别
	 * @param region_code
	 *            区域
	 * @param ipd_no
	 *            住院号
	 * @param mr_no
	 *            病案号
	 * @param adm_date
	 *            看诊日
	 * @param dept_code
	 *            科室
	 * @param dr_code
	 *            医生
	 * @param opt_user
	 *            操作者
	 * @param opt_date
	 *            操作时间
	 * @param opt_term
	 *            操作IP
	 * @param conn
	 *            DB连接
	 * @return
	 */
	public boolean onInsertIpd(String case_no, String adm_type,
			String region_code, String ipd_no, String mr_no,
			Timestamp adm_date, String dept_code, String dr_code,
			String opt_user, String opt_term, TConnection conn) {
		TParm result = new TParm();
		// 判断是否已存在该数据
		if (existsData(case_no)) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("ADM_TYPE", adm_type);
		parm.setData("REGION_CODE", region_code);
		parm.setData("IPD_NO", ipd_no);
		parm.setData("MR_NO", mr_no);
		parm.setData("ADM_DATE", adm_date);
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("DR_CODE", dr_code);
		parm.setData("OPT_USER", opt_user);
		parm.setData("OPT_TERM", opt_term);
		result = this.update("insertIpd", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		return true;
	}

	/**
	 * 出院写入
	 *
	 * @param case_no
	 *            就诊号
	 * @param ds_date
	 *            出院时间
	 * @param opt_user
	 *            操作者
	 * @param opt_date
	 *            操作时间
	 * @param opt_term
	 *            操作IP
	 * @param conn
	 *            DB连接
	 * @return
	 */
	public boolean onUpdateIpd(String case_no, Timestamp ds_date,
			String opt_user, String opt_term, TConnection conn) {
		TParm result = new TParm();
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("DR_DATE", ds_date);
		parm.setData("OPT_USER", opt_user);
		parm.setData("OPT_TERM", opt_term);
		result = this.update("updateIpd", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		return true;
	}

	/**
	 * 插入之前判断是否以存在该主键
	 *
	 * @param case_no
	 *            就诊号
	 * @return
	 */
	public boolean existsData(String case_no) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		return getResultInt(query("existdata", parm), "COUNT") > 0;
	}

}
