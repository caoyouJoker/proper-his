package action.sys;

import com.dongyang.db.TConnection;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.sys.SYSComorderReplaceTool;

/**
 * <p>
 * Title: 医嘱模板替换动作类
 * </p>
 * 
 * <p>
 * Description: 医嘱模板替换动作类
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
 * @author wangl 2011.05.30
 * @version 1.0
 */
public class SYSComorderReplaceAction extends TAction {
	public TParm onReplace(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = SYSComorderReplaceTool.getInstance().onReplace(parm,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 添加修改方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			if (parm.getValue("FLG", i).equals("Y")) {// 添加
				result = SYSComorderReplaceTool.getInstance()
						.insertComorderReplace(parm.getRow(i), connection);
			} else {// 修改
				result = SYSComorderReplaceTool.getInstance()
						.updateComorderReplace(parm.getRow(i), connection);
			}
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 删除
	 * @param parm
	 * @return
	 */
	public TParm onDel(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result = SYSComorderReplaceTool.getInstance()
					.deleteComorderReplace(parm.getRow(i), connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
