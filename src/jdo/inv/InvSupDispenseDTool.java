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
public class InvSupDispenseDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvSupDispenseDTool instanceObject;

    /**
     * 构造器
     */
    public InvSupDispenseDTool() {
        setModuleName("inv\\INVSupDispenseDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvSupDispenseDTool
     */
    public static InvSupDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupDispenseDTool();
        return instanceObject;
    }

    /**
     * 查询出库单细项(一般物资)
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryInv(TParm parm) {
        TParm result = this.query("queryInv", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询出库单细项(手术包)
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryPack(TParm parm) {
        TParm result = this.query("queryPack", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 新增出库单细项
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
