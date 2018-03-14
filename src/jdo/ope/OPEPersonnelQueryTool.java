package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 手术排程查询</p>
 *
 * <p>Description: 手术排程查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangk 2009-12-09
 * @version 4.0
 */
public class OPEPersonnelQueryTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPEPersonnelQueryTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static OPEPersonnelQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEPersonnelQueryTool();
        return instanceObject;
    }

    public OPEPersonnelQueryTool() {
        this.setModuleName("ope\\OPEPersonnelQueryModule.x");
        this.onInit();
    }
    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
