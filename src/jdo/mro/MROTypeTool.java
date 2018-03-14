package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 病案审核项目类型工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-4-29
 * @version 1.0
 */
public class MROTypeTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROTypeTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROTypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROTypeTool();
        return instanceObject;
    }
    public MROTypeTool(){
        this.setModuleName("mro\\MROTypeModule.x");
        this.onInit();
    }
    /**
     * 查询数据
     * @param parm TParm 参数说明：TYPE_CODE,TYPE_DESC两个条件参数 可有可无
     * @return TParm
     */
    public TParm selectType(TParm parm){
        TParm pm = new TParm();
        pm.setData("TYPE_CODE",parm.getValue("TYPE_CODE")+"%");
        pm.setData("TYPE_DESC",parm.getValue("TYPE_DESC")+"%");
        TParm result = query("selectdata",pm);
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
     * 插入数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = update("insertdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新数据
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
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(String code){
        TParm parm = new TParm();
        parm.setData("TYPE_CODE",code);
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
}
