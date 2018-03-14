package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 供应类别</p>
 *
 * <p>Description: 供应类别</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class InvSupTypeTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static InvSupTypeTool instanceObject;

    /**
     * 构造器
     */
    public InvSupTypeTool() {
        setModuleName("inv\\INVSupTypeModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvSupTypeTool
     */
    public static InvSupTypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupTypeTool();
        return instanceObject;
    }


    /**
     * 查询
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm) {
        TParm result = this.update("update", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm) {
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
