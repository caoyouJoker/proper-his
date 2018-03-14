package action.inv;

import jdo.inv.INVNewSterilizationTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class INVNewSterilizationAction extends TAction{

	/**
     * 新建灭菌打包单
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVNewSterilizationTool.getInstance().insertSterilization(parm, conn);
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
