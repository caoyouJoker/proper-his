package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.action.TAction;

import jdo.ins.INSNoticeTool;
import jdo.ins.INSTool;

/**
 * 
 * <p>
 * Title: 医保患者申报查询
 * </p>
 * 
 * <p>
 * Description:医保患者申报查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2012.02.06
 * @version 1.0
 */
public class INSSuffererReportAction extends TAction {
	/**
	 * 保存
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		if (parm == null)
			return result.newErrParm(-1, "参数为空");
		TConnection connection = null;
		try {
			connection = getConnection();
			result = INSNoticeTool.getInstance().onSave(parm, connection);
			if (result == null || result.getErrCode() < 0) {
				return result;
			}
			connection.commit();
		} catch (Exception ex) {

		} finally {
			if (connection != null) {
				connection.close();
			}

		}
		return result;

	}

}
