package action.inv;

import com.dongyang.action.TAction;
import jdo.inv.INVTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class INVDispenseAction
    extends TAction {
    public INVDispenseAction() {
    }

    /**
     * 查询出库单
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryMOut(TParm parm) {
        TParm result = new TParm(); 
        result = INVTool.getInstance().onQueryDispenseMOut(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result; 
    }
 
    /**
     * 查询入库单
     * @param parm TParm 
     * @return TParm 
     */
    public TParm onQueryMIn(TParm parm) { 
        TParm result = new TParm();
        result = INVTool.getInstance().onQueryDispenseMIn(parm);
        System.out.println("查询入库单=="+result); 
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 新增（入）出库单
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();  
        TParm result = new TParm(); 
        result = INVTool.getInstance().onCreateDispenseOutorIn(parm, conn);
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
     * 删除入库单(整张删除)
     * @param parm TParm 
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm(); 
        result = INVTool.getInstance().onDeleteDispense(parm, conn);
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
     * 更新（入）出库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();  
        TParm result = new TParm(); 
        result = INVTool.getInstance().onUpdateDispenseOut(parm, conn);
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
     * 插入INV_DISPENSEDD 
     * @param parm TParm  
     * @return TParm 
     */
    public TParm onInsertDD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onCreateDispenseOutorIn(parm, conn);
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
     * 新增（入）出库单
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onInsertOutHigh(TParm parm) {
        TConnection conn = getConnection();  
        TParm result = new TParm();   
        result = INVTool.getInstance().onDipenseOutInHigh(parm, conn);
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

 
