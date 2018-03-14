package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 出院统计报表</p>
 *
 * <p>Description: 出院统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-18
 * @version 1.0
 */
public class ADMOutPrintTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMOutPrintTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMOutPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMOutPrintTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMOutPrintTool() {
        setModuleName("adm\\ADMOutPrintModule.x");
        onInit();
    }

    /**
     * 查询入院病患信息
     * @param parm TParm
     */
    public TParm selectOutHosp(TParm parm){
        TParm result = this.query("selectOutHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
