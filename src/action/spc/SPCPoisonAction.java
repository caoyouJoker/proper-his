package action.spc;

import java.util.List;

import jdo.spc.SPCPoisonTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ·ûÂë´ò°üAction
 * </p>
 *
 * <p>
 * Description: ·ûÂë´ò°üAction
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fuwj 2012.11.21
 * @version 1.0
 */
public class SPCPoisonAction extends TAction {
	
	/**
	 * ÈÝÆ÷·ûÂë
	 * @param parm
	 * @return
	 */
	public TParm insertContainer(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		List idList = (List) parm.getData("idList");
		String orderCode = (String) parm.getData("ORDER_CODE");
		result = SPCPoisonTool.getInstance().updateContainerNum(parm,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		result = SPCPoisonTool.getInstance().insertToxicm(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		for (int i = 0; i < idList.size(); i++) {
			parm.setData("TOXIC_ID", (String) idList.get(i));
			result = SPCPoisonTool.getInstance().insertToxicd(parm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.close();
				return result;
			}
			result = SPCPoisonTool.getInstance().insertContainer(parm,
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	
	/**
	 * ÈÝÆ÷·ûÂë
	 * @param parm
	 * @return
	 */
	public TParm insertContainerVerifyin(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		List idList = (List) parm.getData("idList");
		String orderCode = (String) parm.getData("ORDER_CODE");
		result = SPCPoisonTool.getInstance().updateContainerNumVerifyin(parm,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		result = SPCPoisonTool.getInstance().insertToxicm(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		for (int i = 0; i < idList.size(); i++) {
			parm.setData("TOXIC_ID", (String) idList.get(i));
			result = SPCPoisonTool.getInstance().insertToxicd(parm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.close();
				return result;
			}
			result = SPCPoisonTool.getInstance().insertContainer(parm,
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * Âé¾«Ò©¸ü»»ÈÝÆ÷
	 * @param parm
	 * @return
	 */
	public TParm updateContainer(TParm parm) {
		TConnection connection = getConnection();

		TParm result = SPCPoisonTool.getInstance().updateContainerD(parm,
				connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		result = SPCPoisonTool.getInstance().updateToxicm(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			connection.close();
			return result;
		}
		result = SPCPoisonTool.getInstance().updateToxicd(parm, connection);
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

}
