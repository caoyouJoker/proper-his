package jdo.bms;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

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
public class BMSQueryPatTool extends TJDOTool {
    /**
     * 实例
     */
    public static BMSQueryPatTool instanceObject;

    /**
     * 得到实例
     *
     * @return BMSBloodTool
     */
    public static BMSQueryPatTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSQueryPatTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSQueryPatTool() {
        setModuleName("bms\\BMSQueryPatModule.x");
        onInit();
    }

    /**
     * 门急诊查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryOE(TParm parm) {
        TParm result = this.query("queryOE", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 住院查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryI(TParm parm) {
        TParm result = this.query("queryI", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
