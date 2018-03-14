package action.inv;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

import jdo.inv.INVOpeAndPackageTool;
/**
 * <p>Title: 手术与手术包对应 </p>
 *
 * <p>Description: </p>
 *  
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: </p>
 *  
 * @author fux
 * @version 4.0
 */
public class INVOpeAndPackageAction
    extends TAction {
    public INVOpeAndPackageAction() {
    }

    /**
     * 新增手术对应手术包
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVOpeAndPackageTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();  
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新手术对应手术包
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVOpeAndPackageTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();  
        return result;
    }
    
    /**
     * 删除手术对应手术包
     * @param parm TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVOpeAndPackageTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 新增手术包申请
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertAutoRequest(TParm parm) {
        TConnection conn = getConnection(); 
        TParm result = new TParm();     
        result = INVOpeAndPackageTool.getInstance().onInsertAutoRequest(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新手术包申请
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateAutoRequest(TParm parm) {  
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVOpeAndPackageTool.getInstance().onUpdateAutoRequest(parm, conn);
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
     * 删除手术对应手术包
     * @param parm TParm
     * @return TParm
     */  
    public TParm onDeleteAutoRequest(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVOpeAndPackageTool.getInstance().onDeleteAutoRequest(parm, conn);
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
