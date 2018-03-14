package action.inv;

import jdo.inv.INVNewRepackTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class INVNewRepackAction extends TAction{

	/**
     * 新建打包单
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
    	System.out.println("RepackAction");
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVNewRepackTool.getInstance().insertRepack(parm, conn);
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
