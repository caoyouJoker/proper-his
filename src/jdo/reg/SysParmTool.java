package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:挂号参数工具类 </p>
 *
 * <p>Description:挂号参数工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class SysParmTool extends TJDOTool{
    /**
     * 实例
     */
    public static SysParmTool instanceObject;
    /**
     * 得到实例
     * @return SysParmTool
     */
    public static SysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SysParmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SysParmTool() {
        setModuleName("reg\\REGSysParmModule.x");

        onInit();
    }
    /**
     * 查询挂号参数
     * @param
     * @return TParm
     */
    public TParm selectdata() {
        TParm result = query("selectdata");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新挂号参数
     * @param  String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
