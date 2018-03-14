package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 住院医师统计报表</p>
 *
 * <p>Description: 住院医师统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-19
 * @version 1.0
 */
public class ADMDrCountPrintTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMDrCountPrintTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMDrCountPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMDrCountPrintTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMDrCountPrintTool() {
        setModuleName("adm\\ADMDrCountPrintModule.x");
        onInit();
    }

    /**
     * 查询入院病患信息
     * @param parm TParm
     */
    public TParm selectDrCount(TParm parm){
        TParm result = this.query("selectDrCount", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
