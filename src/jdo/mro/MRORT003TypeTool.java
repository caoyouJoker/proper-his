package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 医师问题类型统计表</p>
 *
 * <p>Description: 医师问题类型统计表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT003TypeTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MRORT003TypeTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MRORT003TypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT003TypeTool();
        return instanceObject;
    }

    public MRORT003TypeTool() {
        this.setModuleName("mro\\MRORT003_TypeModule.x");
        this.onInit();
    }
    /**
     * 查询出院病患统计数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUT(TParm parm){
        TParm result = this.query("selectOUT",parm);
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
     * 查询在院病患统计数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectIN(TParm parm){
        TParm result = this.query("selectIN",parm);
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
     * 查询出院病患统计数据明细
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUTDetail(TParm parm){//add by wanglong 20130909
        TParm result = this.query("selectOUTDetail",parm);
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
     * 查询在院病患统计数据明细
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINDetail(TParm parm){//add by wanglong 20130909
        TParm result = this.query("selectINDetail",parm);
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
