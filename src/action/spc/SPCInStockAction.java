package action.spc;

import jdo.spc.SPCInStoreTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class SPCInStockAction extends TAction {
	
	public TParm inStock(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCInStoreTool.getInstance().inStock(parm, conn);
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
