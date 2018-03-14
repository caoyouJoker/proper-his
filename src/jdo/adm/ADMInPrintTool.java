package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 入院统计报表</p>
 *
 * <p>Description: 入院统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-29
 * @version 4.0
 */
public class ADMInPrintTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMInPrintTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMInPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMInPrintTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMInPrintTool() {
        setModuleName("adm\\ADMInPrintModule.x");
        onInit();
    }

    /**
     * 查询入院病患信息
     * @param parm TParm
     */
    public TParm selectInHosp(TParm parm){
        TParm result = this.query("selectInHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
