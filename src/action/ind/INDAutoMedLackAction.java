package action.ind;

import jdo.ind.INDAutoMedLackTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * 自动备药出库补档 Action
 * 
 * @author wukai
 * 
 */
public class INDAutoMedLackAction extends TAction {

	/**
	 * 自动出库保存
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSaveIndDispenseAutoMed(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		result = INDAutoMedLackTool.getInstance().onDispenseOutForAutoMedLack(
				parm, conn);
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

}
