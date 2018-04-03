package action.phl;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.phl.PhlRegisterTool;
import jdo.phl.PhlExecuteTool;
import jdo.phl.PhlBedTool;

/**
 * <p>
 * Title: 静点室管理
 * </p>
 *
 * <p>
 * Description: 静点室管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLAction
    extends TAction {

    /**
     * 静点室报到
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlRegister(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlRegisterTool.getInstance().onPhlRegister(parm, conn);
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

    /**
     * 静点室离院清床
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlBedOut(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlBedTool.getInstance().onUpdateBed(parm, conn);
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

    /**
     * 静点室执行
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlExecute(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlExecuteTool.getInstance().onPhlExecute(parm, conn);
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
    /**
     * 静点室执行(门急诊护士站)
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onPhlExecuteForOnw(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhlExecuteTool.getInstance().onPhlExecuteForOnw(parm, conn);
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
    /**
     * 添加床位
     * add caoyong 2014/4/17
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm PHLBedControl(TParm parm) {
    	TConnection conn = getConnection();
    	TParm result = new TParm();
    	result = PhlBedTool.getInstance().PHLBed(parm, conn);
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
