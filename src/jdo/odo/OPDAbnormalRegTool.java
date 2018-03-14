package jdo.odo;

import com.dongyang.jdo.*;
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
public class OPDAbnormalRegTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPDAbnormalRegTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static OPDAbnormalRegTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPDAbnormalRegTool();
        return instanceObject;
    }

    public OPDAbnormalRegTool() {
        this.setModuleName("opd\\OPDAbnormalRegModule.x");
        this.onInit();
    }
    /**
     * 新增挂号信息(非常态门诊)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveReg(TParm parm,TConnection conn){
        TParm result = this.update("saveReg",parm,conn);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询非常态门诊挂号的病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectRegForOPD(TParm parm){
        TParm result = this.query("selectRegForOPD",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
