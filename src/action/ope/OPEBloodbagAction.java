package action.ope;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.ope.OPEBloodbagTool;
import jdo.ope.OPEOpDetailTool;

/**
 * <p>Title: 手术记录</p>
 *
 * <p>Description: 手术记录</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-12-09
 * @version 4.0
 */
public class OPEBloodbagAction
    extends TAction {
    public OPEBloodbagAction() {
    }
    
    /**
     * 更新接收血袋人员和时间
     * @param parm
     * @return
     */
    public TParm updateReceive(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = OPEBloodbagTool.getInstance().updateReceive(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 更新接收血袋人员和时间
     * @param parm
     * @return
     */
    public TParm updateRecheck(TParm parm){
    	TParm result = new TParm();
    	if (parm == null) {
    		result.setErr( -1, "参数不能为空！");
    		return result;
    	}
    	TConnection connection = getConnection();
    	result = OPEBloodbagTool.getInstance().updateRecheck(parm,connection);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		connection.rollback();
    		connection.close();
    		return result;
    	}
    	connection.commit();
    	connection.close();
    	return result;
    }
}
