package jdo.opd;

import jdo.reg.RegMethodTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 门诊医生工作站主档</p>
*
* <p>Description:门诊医生工作站主档</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800904
* @version 1.0
*/
public class OPDMainTool extends TJDOTool{

	/**
     * 实例
     */
    public static OPDMainTool instanceObject;
    /**
     * 得到实例
     * @return OPDMainTool
     */
    public static OPDMainTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new OPDMainTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public OPDMainTool()
    {
        setModuleName("opd\\OPDMainModule.x");
        onInit();
    }
    /**
     * 初始化诊断相关的树的显示数据
     *
     * @return TParm
     */
    public TParm initDiaTree(){
        TParm parm = new TParm();
        TParm result = query("initdiatree",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 初始化医嘱相关的树的显示数据
     *
     * @return TParm
     */
    public TParm initOrderTree(){
        TParm parm = new TParm();
        TParm result = query("initordertree",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
