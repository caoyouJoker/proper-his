package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:挂号方式工具类 </p>
 *
 * <p>Description:挂号方式工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.12
 * @version 1.0
 */
public class RegMethodTool extends TJDOTool{

    /**
     * 实例
     */
    public static RegMethodTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static RegMethodTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new RegMethodTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public RegMethodTool()
    {
        setModuleName("reg\\REGRegMethodModule.x");
        onInit();
    }
    /**
     * 新增挂号方式
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String regMethod = parm.getValue("REGMETHOD_CODE");
        if(existsRegMethod(regMethod)){
            result.setErr(-1,"挂号方式 "+" 已经存在!");
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
     * 更新挂号方式
     * @param parm TParm
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
     * 根据挂号方式代码查询方式信息(右忽略)
     * @param regMethod String 挂号方式代码
     * @return TParm
     */
    public TParm selectdata(String regMethod){
        TParm parm = new TParm();
        regMethod += "%";
        parm.setData("REGMETHOD_CODE",regMethod);
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
     * 删除挂号方式
     * @param regMethod String
     * @return boolean
     */
    public TParm deletedata(String regMethod){
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE",regMethod);
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
     * 判断是否存在挂号方式
     * @param regMethod String 挂号方式代码
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsRegMethod(String regMethod){
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE",regMethod);
        return getResultInt(query("existsRegMethod",parm),"COUNT") > 0;
    }
    /**
     * 查询挂号方式combo是否可下拉注记
     * @param regmethodCode String 挂号方式代码
     * @return boolean true 可下拉 false 不可下拉
     */
    public boolean selComboFlg(String regmethodCode){
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE", regmethodCode);
        TParm result = query("selComboFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;
    }
    /**
     * 得到打票注记
     * @param regMethod String
     * @return boolean
     */
    public boolean selPrintFlg(String regMethod) {
        TParm parm = new TParm();
        parm.setData("REGMETHOD_CODE", regMethod);
        TParm result = query("selPrintFlg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        boolean printFlg = result.getBoolean("PRINT_FLG", 0);
        return printFlg;
    }

}
