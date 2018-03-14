package jdo.inv;


import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:物资科室 </p>
 *
 * <p>Description: 物资科室</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-5-21
 * @version 1.0
 */

public class INVOrgTool extends TJDOTool{
    /**
     * 实例
     */
    public static INVOrgTool instanceObject;
    /**
     * 得到实例
     * @return DeptTool
     */
    public static INVOrgTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVOrgTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INVOrgTool() {
        setModuleName("inv\\INVOrgModule.x");
        onInit();
    }
    /**
     * 物资科室
     * @return TParm
     */
    public TParm getDept(){
    TParm result = query("getDept");
    if (result.getErrCode() < 0)
        err("ERR:" + result.getErrCode() + result.getErrText() +
            result.getErrName());
    return result;

}






}
