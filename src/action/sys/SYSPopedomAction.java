package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSDictionaryTool;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class SYSPopedomAction extends TAction {
	public SYSPopedomAction() {
	}

	/**
	 * 更新程序和新增权限
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdatePopedom(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = SYSDictionaryTool.getInstance().onUpdatePopedom(parm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 删除程序
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onDeletePrg(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = SYSDictionaryTool.getInstance().onDeletePrg(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 删除权限
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onDeletePopedom(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = SYSDictionaryTool.getInstance().onDeletePopedom(parm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 修改SYSDictionary表默认注记 ============pangben 2011-12-30
	 */
	public TParm updateSysDictionaryFlg(TParm parm) {
		TConnection connection = getConnection();
		TParm flgParm = null;
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {
				flgParm = parm.getRow(i);
				flgParm.setData("FLG", "N");
				result = SYSDictionaryTool.getInstance().updateFlg(flgParm,
						connection);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
