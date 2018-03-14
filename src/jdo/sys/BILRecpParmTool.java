package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 * 费用档
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author fudw
 * @version 1.0
 */
public class BILRecpParmTool extends TJDOTool {
    /**
     * 实例
     */
    public static BILRecpParmTool instanceObject;
    /**
     * 得到实例
     * @return CTZTool
     */
    public static BILRecpParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILRecpParmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILRecpParmTool() {
        setModuleName("bil\\BILRecpParmModule.x");
        onInit();
    }

    /**
     * 查询全部收费类别
     * @return TParm charge_code
     */
    public TParm selectcharge(TParm parm) {
        TParm result = query("selectChargeCode",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
