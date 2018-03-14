package action.bms;

import com.dongyang.action.TAction;
import jdo.bms.BMSTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ±¸ÑªÉêÇë
 * </p>
 *
 * <p>
 * Description: ±¸ÑªÉêÇë
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
public class BMSApplyAction
extends TAction {
	public BMSApplyAction() {
	}

	/**
	 * ÐÂÔö
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInsertBMSApply(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = BMSTool.getInstance().onInsertBMSApply(parm, conn);
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
	 * ¸üÐÂ
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdateBMSApply(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = BMSTool.getInstance().onUpdateBMSApply(parm, conn);
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
	 * É¾³ý
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onDeleteBMSApply(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = BMSTool.getInstance().onDeleteBMSApply(parm, conn);
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
	// --------------------------------add by wangqing start--------------------------------
	/**
	 * ÐÞ¸ÄÉóºË×´Ì¬
	 * @param parm
	 * @return
	 */
	public TParm onCheckBMSApply(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = BMSTool.getInstance().onCheckBMSApply(parm, conn);
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
	//-----------------------------add by wangqing end--------------------------------------



}
