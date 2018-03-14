package jdo.udd;

import com.dongyang.jdo.TJDOTool;
import jdo.ind.IndAgentTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UddApothecaryLoadChartTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static UddApothecaryLoadChartTool instanceObject;

    /**
     * 得到实例
     *
     * @return UddApothecaryLoadChartTool
     */
    public static UddApothecaryLoadChartTool getInstance() {
        if (instanceObject == null)
            instanceObject = new UddApothecaryLoadChartTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public UddApothecaryLoadChartTool() {
        setModuleName("udd\\UDDApothecaryLoadChartModule.x");
        onInit();
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if ("CHECK".equals(parm.getValue("TYPE"))) {
            result = this.query("queryCHECK", parm);
        }
        else if ("DOSAGE".equals(parm.getValue("TYPE"))) {
            result = this.query("queryDOSAGE", parm);
        }
        else if ("DISPENSE".equals(parm.getValue("TYPE"))) {
            result = this.query("queryDISPENSE", parm);
        }
        else {
            result = this.query("queryRTN", parm);
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
