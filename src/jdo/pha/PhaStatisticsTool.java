package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;


/**
 *
 * <p>Title: 门急诊药房报表工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2008.09.26
 * @version 1.0
 */

public class PhaStatisticsTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static PhaStatisticsTool instanceObject;

    /**
     * 得到实例
     * @return OrderTool
     */
    public static PhaStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaStatisticsTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaStatisticsTool() {
        //加载Module文件
        setModuleName("pha\\PhaStatisticsModule.x");

        onInit();
    }

    /**
     * 获得‘门急诊药师工作量统计报表’的主数据
     * @return TParm
     */
    public TParm getPhaStatisticsMainDate(TParm parm, String type) {

        TParm result = new TParm();
        //审核查询
        if (type.equals("审核")) {
            parm.setData("PHA_CHECK_DATE", "Y");
            result = query("queryApothecaryLoadCheck", parm);
        } //配药查询
        else if (type.equals("配药")) {
            parm.setData("PHA_DOSAGE_DATE", "Y");
            result = query("queryApothecaryLoadDispense", parm);
        } //发药查询
        else if (type.equals("发药")) {
            parm.setData("PHA_DISPENSE_DATE", "Y");
            result = query("queryApothecaryLoadSend", parm);
        } //退药查询
        else if (type.equals("退药")) {
            parm.setData("PHA_RETN_DATE", "Y");
            result = query("queryApothecaryLoadReturn", parm);
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
