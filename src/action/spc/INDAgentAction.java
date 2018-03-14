package action.spc;

import jdo.spc.IndAgentTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
			
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

public class INDAgentAction extends TAction {
	/**
	 * 查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = new TParm();
		result = IndAgentTool.getInstance().onQuery(parm);
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
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsert(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = IndAgentTool.getInstance().onInsert(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 更新
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdate(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = IndAgentTool.getInstance().onUpdate(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 删除
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onDelete(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = IndAgentTool.getInstance().onDelete(parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 查询购入单位
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryUnit(TParm parm) {
		TParm result = new TParm();
		result = IndAgentTool.getInstance().onQueryUnit(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
