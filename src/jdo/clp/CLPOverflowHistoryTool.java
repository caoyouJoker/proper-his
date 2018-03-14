package jdo.clp;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description:科室病种溢出管理 </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110707
 * @version 1.0
 */
public class CLPOverflowHistoryTool extends TJDOTool{
    public CLPOverflowHistoryTool() {
        setModuleName("clp\\CLPOverflowHistoryModule.x");
        onInit();
    }
    /**
    * 实例
    */
   public static CLPOverflowHistoryTool instanceObject;

    /**
     * 得到实例
     * @return IBSTool
     */
    public static CLPOverflowHistoryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPOverflowHistoryTool();
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
