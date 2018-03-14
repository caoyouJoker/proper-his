package jdo.sum;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:体温种类工具类 </p>
 *
 * <p>Description:体温种类工具类 </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMTmptrKindTool extends TJDOTool{

    /**
     * 实例
     */
    public static SUMTmptrKindTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static SUMTmptrKindTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SUMTmptrKindTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SUMTmptrKindTool()
    {
        setModuleName("sum\\SUMTmptrKindModule.x");
        onInit();
    }

    /**
     * 新增体温种类
     * @param regMethod String
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String regMethod = parm.getValue("TMPTRKINDCODE");
        if(existsRegMethod(regMethod)){
            result.setErr(-1,"体温种类代码 "+" 已经存在!");
            return result ;
        }
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
     * 更新体温种类
     * @param regMethod String
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据体温种类代码查询方式信息(右忽略)
     * @param regMethod String 体温种类代码
     * @return TParm
     */
    public TParm selectdata(String regMethod){
        TParm parm = new TParm();
        regMethod += "%";
        parm.setData("TMPTRKINDCODE",regMethod);
        TParm result = query("selectdata",parm);
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
     * 删除体温种类
     * @param regMethod String
     * @return boolean
     */
    public TParm deletedata(String regMethod){
        TParm parm = new TParm();
        parm.setData("TMPTRKINDCODE",regMethod);
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
     * 判断是否存在体温种类
     * @param regMethod String 体温种类代码
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsRegMethod(String regMethod){
        TParm parm = new TParm();
        parm.setData("TMPTRKINDCODE",regMethod);
        return getResultInt(query("exists",parm),"COUNT") > 0;
    }


}
