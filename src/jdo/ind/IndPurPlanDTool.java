package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 采购计划明细Tool
 * </p>
 * 
 * <p>
 * Description: 采购计划明细Tool
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
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class IndPurPlanDTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static IndPurPlanDTool instanceObject;

	/**
	 * 构造器
	 */
	public IndPurPlanDTool() {
		setModuleName("ind\\INDPurPlanDModule.x");
		onInit();
	}

	/**
	 * 得到实例
	 * 
	 * @return IndPurPlanDTool
	 */
	public static IndPurPlanDTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IndPurPlanDTool();
		return instanceObject;
	}

	/**
	 * 查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		TParm result = this.query("queryPlanD", parm);
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
		TParm result = this.update("createNewPlanD", parm, conn);
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
		TParm result = this.update("updatePlanD", parm, conn);
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
		TParm result = this.update("deletePlanD", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询采购计划生成订购单明细
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryCreate(TParm parm) {
		TParm result = this.query("queryCreatePurPlanD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
