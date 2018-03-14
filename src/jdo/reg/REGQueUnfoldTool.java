package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class REGQueUnfoldTool extends TJDOTool{
	
	 /**
     * 实例
     */
    public static REGQueUnfoldTool instanceObject;
    /**
     * 得到实例
     * @return ClinicAreaTool
     */
    public static REGQueUnfoldTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGQueUnfoldTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public REGQueUnfoldTool() {
        setModuleName("reg\\REGQueUnfoldModule.x");
        onInit();
    }
    
    /**
     * 增加VIP就诊号 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm addRegQue(TParm parm, TConnection conn) {

        TParm result = update("addRegQue", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    } 

}
