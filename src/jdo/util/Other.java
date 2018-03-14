package jdo.util;

import java.sql.Timestamp;
import java.util.Map;

import jdo.sys.SYSEmrIndexTool;
import jdo.sys.SYSSupplierTool;
import jdo.sys.SYSTerminalTool;

import com.dongyang.db.TConnection;
import com.dongyang.jdo.TStrike;

/**
 *
 * <p>
 * Title: 其他共用
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
 * @author zhangy 2009.6.17
 * @version JavaHis 1.0
 */
public class Other extends TStrike {
	/**
	 * 门诊、急诊挂号写入病历主索引(SYS_EMR_INDEX)
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
	public boolean onInsertSYSEMRIndexOpdEmg(String case_no, String adm_type,
			String region_code, String mr_no, Timestamp adm_date,
			String dept_code, String dr_code, String opt_user, String opt_date,
			String opt_term, TConnection conn) {
		if (isClientlink()) {
			System.out.println("客服端无法取得连接");
			return false;
		}
		return SYSEmrIndexTool.getInstance().onInsertOpdEmg(case_no, adm_type,
				region_code, mr_no, adm_date, dept_code, dr_code, opt_user,
				 opt_term, conn);
	}

	/**
	 * 住院登记写入病历主索引(SYS_EMR_INDEX)
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
	public boolean onInsertSYSEMRIndexIpd(String case_no, String adm_type,
			String region_code, String ipd_no, String mr_no,
			Timestamp adm_date, String dept_code, String dr_code,
			String opt_user, String opt_date, String opt_term, TConnection conn) {
		if (isClientlink()) {
			System.out.println("客服端无法取得连接");
			return false;
		}
		return SYSEmrIndexTool.getInstance().onInsertIpd(case_no, adm_type,
				region_code, ipd_no, mr_no, adm_date, dept_code, dr_code,
				opt_user, opt_term, conn);
	}

	/**
	 * 住院出院写入病历主索引(SYS_EMR_INDEX)
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
	public boolean onUpdateSYSEMRIndexIpd(String case_no, Timestamp ds_date,
			String opt_user, String opt_date, String opt_term, TConnection conn) {
		if (isClientlink()) {
			System.out.println("客服端无法取得连接");
			return false;
		}
		return SYSEmrIndexTool.getInstance().onUpdateIpd(case_no, ds_date,
				opt_user, opt_term, conn);
	}

	/**
	 * 取得端末IP
	 *
	 * @param term_no
	 *            端末代码
	 * @return
	 */
	public String getSYSTerminal(String term_no) {
		if (isClientlink())
			return (String) callServerMethod(term_no);
		return SYSTerminalTool.getInstance().selectdata(term_no).getValue(
				"TERM_IP", 0);
	}

	/**
	 * 取得供货商状态信息
	 *
	 * @param sup_code
	 *            供货商代号
	 * @return Map TParm{PHA_FLG,MAT_FLG,DEV_FLG,OTHER_FLG,SUP_STOP_FLG,
	 *         SUP_STOP_DATE,SUP_END_DATE}
	 */
	public Map getSYSSupplierFlg(String sup_code) {
		if (isClientlink())
			return (Map) callServerMethod(sup_code);
		return SYSSupplierTool.getInstance().selectdata(sup_code).getData();
	}
}
