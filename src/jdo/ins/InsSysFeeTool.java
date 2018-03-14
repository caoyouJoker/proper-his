package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class InsSysFeeTool extends TJDOTool {
    /**
     * 实例
     */
    public static InsSysFeeTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static InsSysFeeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InsSysFeeTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public InsSysFeeTool() {
        setModuleName("ins\\InsSysFeeModule.x");
        onInit();
    }
    /**
 * 查询SYS_FEE
 * @param parm TParm
 * @return TParm
 */
public TParm selSysFee(TParm parm) {
    //System.out.println("002 Tool 查询=====》");

    TParm result = new TParm();
    result = query("query", parm);
   // System.out.println("result=" + result);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
        return result;
    }
    return result;

}


}
