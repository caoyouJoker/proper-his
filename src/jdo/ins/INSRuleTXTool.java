package jdo.ins;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 门诊医保三目字典
 * </p>
 * 
 * <p>
 * Description:门诊医保三目字典
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2011-12-09
 * @version 2.0
 */
public class INSRuleTXTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INSRuleTXTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RuleTool
	 */
	public static INSRuleTXTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSRuleTXTool();
		return instanceObject;
	}

	

	/**
	 * 构造器
	 */
	public INSRuleTXTool() {
		setModuleName("ins\\INSRuleTXModule.x");

		onInit();
	}

	/**
	 * 添加数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSRule(TParm parm, TConnection connection) {
		TParm result = update("insertINSRule", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 修改数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateINSRule(TParm parm) {
		TParm result = update("updateINSRule", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 删除数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm deleteINSRule(TParm parm) {
		TParm result = update("deleteINSRule", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 查询方法
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectINSRule(TParm parm) {

		TParm result = query("selectINSRule", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	/**
	 * 查询已经匹配的数据
	 * @return
	 */
	public TParm selectMateSysFee(TParm parm){
		TParm result = query("selectMateSysFee"+parm.getValue("sqlName"), parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 完全匹配数据单选按钮选中执行查询数据
	 * @return
	 */
	public TParm selectSumSame(TParm parm){
		TParm result = query("selectSumSame"+parm.getValue("sqlName"), parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 修改SYS_FEE医保数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateSysFeeNhi(TParm parm,TConnection connection) {
		TParm result = update("updateSysFeeNhi", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	/**
	 * 修改SYS_FEE医保数据(修改后)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateSysFee(TParm parm,TConnection connection) {
		TParm result = update("updateSysFee", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	/**
	 * 修改SYS_FEE_HISTORY医保数据(修改后)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateSysFeeHistory(TParm parm,TConnection connection) {
		TParm result = update("updateSysFeehistory", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}
	//===============  chenxi modify 
	/**
	 * 保存SYS_FEE_HISTORY 中的数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveFeeHistory(TParm parm,TConnection connection) {
		TParm result = update("saveFeeHistory", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;    
		}  
		return result;

	}
	// ==========  chenxi modify 
	/**
	 * 修改sys_CHARGE_HOSP医保数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateChargeHosp(TParm parm,TConnection connection) {
		TParm result = update("updateChargeHosp", parm,connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	/**
	 * 未匹配数据查询操作
	 * @return
	 */
	public TParm selectNotMateSysFee(TParm parm){
		TParm result = query("selectNotMateSysFee"+parm.getValue("sqlName"), parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
