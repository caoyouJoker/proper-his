package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 对照科室信息</p>
 *
 * <p>Description: 对照科室信息</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-7-26
 * @version 1.0
 */
public class STADeptInfoTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STADeptInfoTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STADeptInfoTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new STADeptInfoTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public STADeptInfoTool() {
        setModuleName("sta\\STADeptInfoModule.x");
        onInit();
    }
    /**
     * 新增信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        result = update("insertdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = new TParm();
        result = update("updatedata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除信息
     * @param regMethod String
     * @return boolean
     */
    public TParm deletedata(String dept_code){
        TParm parm = new TParm();
        parm.setData("STA_DEPT_CODE",dept_code);
        TParm result = update("deletedata",parm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm){
        TParm result = new TParm();
        result = query("selectdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
