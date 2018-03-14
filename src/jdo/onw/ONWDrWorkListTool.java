package jdo.onw;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 门急诊医师工作量统计</p>
 *
 * <p>Description: 门急诊医师工作量统计</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-4
 * @version 1.0
 */
public class ONWDrWorkListTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ONWDrWorkListTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static ONWDrWorkListTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWDrWorkListTool();
        return instanceObject;
    }
    public ONWDrWorkListTool() {
        setModuleName("onw\\ONWDrWorkListModule.x");
        onInit();
    }
    /**
     * 查询信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
