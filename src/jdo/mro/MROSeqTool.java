package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 病历排序维护工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 20012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author liuzhen 2012-8-2
 * @version 1.0
 */
public class MROSeqTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROSeqTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROSeqTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROSeqTool();
        return instanceObject;
    }
    public MROSeqTool(){
        this.setModuleName("mro\\MROSeqModule.x");
        this.onInit();
    }
    /**
     * 查询数据
     * @param parm TParm 参数说明：TYPE_CODE,TYPE_DESC两个条件参数 可有可无
     * @return TParm
     */
    public TParm selectType(TParm parm){
        TParm pm = new TParm();
        //pm.setData("TYPE_CODE",parm.getValue("TYPE_CODE")+"%");
        //pm.setData("TYPE_DESC",parm.getValue("TYPE_DESC")+"%");
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
    public TParm deletedata(int seq){
        TParm parm = new TParm();
        parm.setData("SEQ",seq);
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
