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
public class InvSupDispenseDDTool extends TJDOTool {
    /**
     * 实例
     */
    public static InvSupDispenseDDTool instanceObject;

    /**
     * 构造器
     */
    public InvSupDispenseDDTool() {
        setModuleName("inv\\INVSupDispenseDDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvSupDispenseDDTool
     */
    public static InvSupDispenseDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupDispenseDDTool();
        return instanceObject;
    }

    /**
     * 新增申请单主档
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
