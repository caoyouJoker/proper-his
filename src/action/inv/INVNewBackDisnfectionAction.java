package action.inv;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class INVNewBackDisnfectionAction extends TAction {

	
	/**
     * 新建回收清洗消毒单
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVNewBackDisnfectionTool.getInstance().insertBackDisnfection(parm, conn);
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
