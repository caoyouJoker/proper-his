package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 门急医师办理入院统计报表</p>
 *
 * <p>Description: 门急医师办理入院统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-19
 * @version 1.0
 */
public class ADMOpdDrPrintTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMOpdDrPrintTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMOpdDrPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMOpdDrPrintTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMOpdDrPrintTool() {
        setModuleName("adm\\ADMOpdDrPrintModule.x");
        onInit();
    }

    /**
     * 查询入院病患信息
     * @param parm TParm
     */
    public TParm selectOpdDrCount(TParm parm){
        TParm result = this.query("selectOpdDrCount", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
