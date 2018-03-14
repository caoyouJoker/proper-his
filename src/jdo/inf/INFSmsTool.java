package jdo.inf;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 感染信息 短信邮件工具类
 * </p>
 * 
 * <p>
 * Description: 感染信息 短信邮件工具类
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
 * @author wukai
 * @version 1.0
 */
public class INFSmsTool extends TJDOTool {
	/**
	 * 构造器
	 */
	public INFSmsTool() {
		setModuleName("inf\\INFSmsModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static INFSmsTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INFReportTool
	 */
	public static INFSmsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INFSmsTool();
		return instanceObject;
	}

	/**
	 * 插入字段（传染病预警）
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertInfWarnData(TParm parm, TConnection conn) {
		TParm result = this.update("insertInfWarnData", parm, conn);
		return result;
	}

	/**
	 * 查询全字段
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectAllData(TParm parm) {
		TParm result = this.update("selectAllData", parm);
		return result;
	}

	/**
	 * 获取发送记录（传染病预警）
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectInfWarnData(TParm parm) {
		TParm result = this.query("selectInfWarnData", parm);
		return result;
	}

	/**
	 * 获取干预措施选项
	 * 
	 * @param inventId
	 *            : 干预措施ID
	 * @return
	 */
	public TParm getInfInventOptions(String inventId) {

		String sql = "SELECT 'N' AS FLG, ID, CHN_DESC AS NAME FROM INF_INTERVENTION_OPTIONS WHERE INTERVENT_ID = '"
				+ inventId + "' ORDER BY ID ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		return result;

	}

	/**
	 * 更新字段 （传染病预警）
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateInfWarnData(TParm parm, TConnection conn) {
		TParm result = this.update("updateInfWarnData", parm, conn);
		return result;
	}

	/**
	 * 更新字段 （传染病预警）
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateInfWarnData(TParm parm) {
		TParm result = this.update("updateInfWarnData", parm);
		return result;
	}

	/**
	 * 获取开单医生信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getBillDr(String drNameOrCode) {
		String sql = "SELECT USER_ID, TEL1 FROM SYS_OPERATOR WHERE USER_ID = '"
				+ drNameOrCode + "' OR USER_NAME = '" + drNameOrCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 插入字段 （感染消息沟通）add lij 20170426
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertInfSms(TParm parm, TConnection conn) {
		TParm result = this.update("insertInfSmsData", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 获取感染科人员电话号码 （感染消息沟通）add lij 20170426
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm getSendUserCode(String caseNo) {
		String sql = "SELECT A.SEND_USER,A.MR_NO,B.TEL1 "
				+ " FROM INF_SMS A,SYS_OPERATOR B "
				+ " WHERE A.SEND_USER=B.USER_ID AND A.CASE_NO='" + caseNo
				+ "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 更新字段 （感染消息沟通）
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateInfSms(TParm parm) {
		TParm result = this.update("updateInfSmsData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入字段 （感染消息沟通）
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInfSms(TParm parm) {
		TParm result = this.update("insertInfSmsData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
