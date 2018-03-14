package action.spc;

import com.dongyang.action.TAction;
import jdo.spc.IndAgentTool;
import jdo.spc.IndStockMTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.spc.INDTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class INDStockMAction
    extends TAction {
    public INDStockMAction() {
    }

    /**
     * 更新
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateIndStockM(parm, conn);
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
     * 更新
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onMateriaLocUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateIndStockM(parm, conn);
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
     * 插入  chenxi 
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertIndStockM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        for(int i=0;i<parm.getCount();i++){
        	 result = IndStockMTool.getInstance().onInsertIndStockM(parm.getRow(i), conn);   
             if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
                 conn.rollback() ;
                 conn.close();
                 return result;
             }	
        }  
        conn.commit();
        conn.close();
        return result;
    }
}
