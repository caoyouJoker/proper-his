package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 门诊资格确认书下载确立
 * </p>
 * 
 * <p>
 * Description: 门诊资格确认书下载确立
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 20111202
 * @version 1.0
 */
public class INSMZConfirmTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INSMZConfirmTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSADMConfirmTool
	 */
	public static INSMZConfirmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSMZConfirmTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INSMZConfirmTool() {
		setModuleName("ins\\INSMZConfirmModule.x");
		onInit();
	}

	/**
	 * 门诊医生站 特殊情况 查询是否可用特殊情况
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectSpcMemo(TParm parm) {
		TParm result = query("selectSpcMemo", parm);
		return result;
	}

	/**
	 * 门诊医生站 特殊情况 修改
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInsMZConfirmSpcMemo(TParm parm) {
		TParm result = update("updateInsMZConfirmSpcMemo", parm);
		return result;
	}

	/**
	 * 挂号使用医保操作需要添加表数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInsMZConfirm(TParm parm) {
		TParm result = update("insertInsMZConfirm", parm);
		return result;
	}

	/**
	 * 挂号使用医保操作修改表数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateInsMZConfirm(TParm parm) {
		TParm result = update("updateInsMZConfirm", parm);
		return result;
	}

	/**
	 * 操作失败删除
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteInsMZConfirm(TParm parm, TConnection conn) {
		TParm result = update("deleteInsMZConfirm", parm, conn);
		return result;
	}

	/**
	 * 执行添加修改操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSaveInsMZConfirm(TParm parm) {
		TParm result = this.queryMZConfirmOne(parm);
		if (result.getCount("CASE_NO") > 0) {
			// 修改InsMZConfirm 门诊资格确认书表数据
			result = this.updateInsMZConfirm(parm);
		} else {
			// 添加 InsMZConfirm 门诊资格确认书表数据
			result = this.insertInsMZConfirm(parm);
		}
		if (result.getErrCode() < 0) {
			return result;
		}
		result = this.queryMZConfirmOne(parm);
		return result;
	}
	/**
	 * 查询身份信息
	 * @param parm
	 * @return
	 */
	public TParm queryMZConfirm(TParm parm){
		TParm result = query("queryMZConfirm", parm);
		return result;
	}
	/**
	 * 查询身份信息
	 * @param parm
	 * @return
	 */
	public TParm queryMZConfirmOne(TParm parm){
		TParm result = query("queryMZConfirmOne", parm);
		return result;
	}
	/**
	 * 查询挂号就诊顺序号
	 * @param parm
	 * @return
	 */
	public TParm queryInsOpd(TParm parm)
	{
		String sql = " SELECT CONFIRM_NO FROM INS_OPD " +
			 "  WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "' " +
			 "	AND RECP_TYPE = 'REG'	";
	    TParm result = new TParm(TJDODBTool.getInstance().select(
			sql));		
		return result;
	}
	/**
	 * 查询医生医保备案编码
	 * @param parm
	 * @return
	 */
	public TParm queryDrQualifyCode(String userId)
	{
		String sql = " SELECT DR_QUALIFY_CODE FROM SYS_OPERATOR " +
				" WHERE USER_ID='"+ userId + "'";
	    TParm result = new TParm(TJDODBTool.getInstance().select(sql));		
		return result;
	}	
}
