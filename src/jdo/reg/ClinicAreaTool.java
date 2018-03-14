package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TDebug;
/**
 *
 * <p>Title:诊区维护工具类 </p>
 *
 * <p>Description:诊区维护工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class ClinicAreaTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ClinicAreaTool instanceObject;
    /**
     * 得到实例
     * @return ClinicAreaTool
     */
    public static ClinicAreaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ClinicAreaTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ClinicAreaTool() {
        setModuleName("reg\\REGClinicAreaModule.x");
        onInit();
    }

    /**
     * 查询诊区
     * @return TParm
     */
    public TParm queryTree() {
        TParm result = query("queryTree");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 查询诊区table信息
     * @param
     * @return TParm
     */
    public TParm selectdata() {
        TParm parm = new TParm();
        TParm result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public static void main(String args[]) {
        TDebug.initServer();
        //System.out.println(ClinicAreaTool.getInstance().queryTree());

    }
}
