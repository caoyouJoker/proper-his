package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:账务参数档 </p>
 *
 * <p>Description:账务参数档 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 20090724
 * @version 1.0
 */
public class BILSysParmTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILSysParmTool instanceObject;
    /**
     * 得到实例
     * @return BILSysParmTool
     */
    public static BILSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILSysParmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILSysParmTool() {
        setModuleName("bil\\BILSysParmModule.x");
        onInit();
    }

    /**
     * 得到门诊日结时间点
     * @param admType String
     * @return TParm
     */
    public TParm getDayCycle(String admType) {
        TParm result = new TParm();
        if (admType == null || admType.length() == 0)
            return result.newErrParm( -1, "参数错误");
        result.setData("ADM_TYPE", admType);
        result = query("getDayCycle", result);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }


}
