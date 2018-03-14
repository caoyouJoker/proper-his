package action.spc;

import jdo.spc.IndRequestMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class SpcRequestAction extends TAction {
	                       
    /**
     * ÐÂÔö
     *
     * @param parm                                    
     *            TParm
     * @return TParm
     */
    public TParm onInsertRequest(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndRequestMTool.getInstance().onInsertAll(parm, conn);    
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
