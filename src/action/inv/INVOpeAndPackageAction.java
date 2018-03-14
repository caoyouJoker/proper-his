package action.inv;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

import jdo.inv.INVOpeAndPackageTool;
/**
 * <p>Title: ��������������Ӧ </p>
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
     * ����������Ӧ������
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
     * ����������Ӧ������
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
     * ɾ��������Ӧ������
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
     * ��������������
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
     * ��������������
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
     * ɾ��������Ӧ������
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
