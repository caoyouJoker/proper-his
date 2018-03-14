package action.ope;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.ope.OPEBloodbagTool;
import jdo.ope.OPEOpDetailTool;

/**
 * <p>Title: ������¼</p>
 *
 * <p>Description: ������¼</p>
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
     * ���½���Ѫ����Ա��ʱ��
     * @param parm
     * @return
     */
    public TParm updateReceive(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
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
     * ���½���Ѫ����Ա��ʱ��
     * @param parm
     * @return
     */
    public TParm updateRecheck(TParm parm){
    	TParm result = new TParm();
    	if (parm == null) {
    		result.setErr( -1, "��������Ϊ�գ�");
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
