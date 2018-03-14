package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 供货商代理产品设定
 * </p>
 * 
 * <p>
 * Description: 供货商代理产品设定
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class IndAgentTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static IndAgentTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndAgentTool
	 */
	public static IndAgentTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndAgentTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public IndAgentTool() {
		setModuleName("spc\\INDAgentModule.x");
		onInit();
	}

	/**
	 * 查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("query", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 新增
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsert(TParm parm, TConnection conn) {
		TParm result = this.update("insert", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdate(TParm parm, TConnection conn) {
		TParm result = this.update("update", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onDelete(TParm parm, TConnection conn) {
		TParm result = this.update("delete", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询购入单位
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryUnit(TParm parm) {
		TParm result = this.query("queryUnit", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 自动维护购入单价,更新合约单价
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateContractPrice(TParm parm, TConnection conn) {
		TParm result = this.update("updateContractPrice", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
