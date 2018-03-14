package action.spc;

import jdo.spc.INDTool;
import jdo.spc.IndVerifyinMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: �������Action
 * </p>
 *
 * <p>
 * Description: �������Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p> 
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.14
 * @version 1.0
 */

public class INDVerifyinAction
    extends TAction {

    /**
     * ��ѯ��������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = IndVerifyinMTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryMAcnt(TParm parm) {//wanglong add 20150202
        TParm result = new TParm();
        result = IndVerifyinMTool.getInstance().onQueryAcnt(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * �������յ�
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertVerify(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }
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
     * �������յ�
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertVerifyAcnt(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }
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
     * �������յ�
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateInd(TParm parm) {
       
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateVerifyInd(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }
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
     * �������յ�
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
       
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateVerify(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }
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
     * �������յ�
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateAcnt(TParm parm) {//wanglong add 20150202
       
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateVerifyAcnt(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }
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
     * �Զ��������쵥�ͳ��ⵥ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onSaveReqeustAndDispense(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onSaveReqeustAndDispense(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }
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
     * ɾ�����յ�(���ϸ��)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeleteVerifyinM(parm, conn);
        if (result == null || result.getErrCode() < 0) {
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
     * ɾ�����յ�(���ϸ��)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteMAcnt(TParm parm) {//wanglong add 20150202
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeleteVerifyinMAcnt(parm, conn);
        if (result == null || result.getErrCode() < 0) {
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
     * �������յ�
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateSpc(TParm parm) {
       
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateVerifySpc(parm, conn);
        if (result == null) {
            conn.close();
            return result;
        }  
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
