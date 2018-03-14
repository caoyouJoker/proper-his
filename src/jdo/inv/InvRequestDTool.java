package jdo.inv;

import com.dongyang.jdo.TJDOTool;
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
public class InvRequestDTool extends TJDOTool {
    /**
     * 实例
     */
    public static InvRequestDTool instanceObject;

    /**
     * 构造器
     */
    public InvRequestDTool() {
        setModuleName("inv\\INVRequestDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvRequestDTool
     */
    public static InvRequestDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvRequestDTool();
        return instanceObject;
    }

    /** 
     * 查询申请单主档
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("queryRequestD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 新增订购细项
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createRequestD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 删除申请细项(全部)
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteRequestDAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询需要出库的申请单细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryRequestDOut(TParm parm) {
        TParm result = this.query("queryRequestDOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 申请单细项状态(更新申请单细项出入库累积量和完成状态)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateActualQtyAndType(TParm parm, TConnection conn){
        TParm result = this.update("updateActualQtyAndType", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
