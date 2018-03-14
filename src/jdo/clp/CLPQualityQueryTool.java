package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class CLPQualityQueryTool extends TJDOTool{
    public CLPQualityQueryTool() {
        setModuleName("clp\\CLPQualityQueryModule.x");
        onInit();
    }
    /**
    * 实例
    */
   public static CLPQualityQueryTool instanceObject;

    /**
     * 
     * 得到实例
     * @return IBSTool
     */
    public static CLPQualityQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPQualityQueryTool();
        return instanceObject;
    }
    /**
     * 显示数据
     * @param parm TParm
     */
    public TParm selectData(String sqlName,TParm parm){
        TParm result = query(sqlName, parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
