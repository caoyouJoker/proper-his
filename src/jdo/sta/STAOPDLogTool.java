package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 门急诊日志</p>
 *
 * <p>Description: 门急诊日志</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-7
 * @version 1.0
 */
public class STAOPDLogTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOPDLogTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOPDLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOPDLogTool();
        return instanceObject;
    }

    public STAOPDLogTool() {
        setModuleName("sta\\STAOPDLogModule.x");
        onInit();
    }
    /**
     * 查询指定日期的数据
     * @param Date String
     * @return TParm
     * ===============pangben modify 20110525 添加区域参数
     */
    public TParm selectData(String Date,String regionCode){
        TParm parm = new TParm();
        parm.setData("STA_DATE",Date);
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.query("selectData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 获取打印数据
     * @param STA_DATE String
     * @return TParm
     * ==============pangben modify 20110519 添加区域参数
     */
    public TParm getPrintData(String STA_DATE,String regionCode){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //==============pangben modify 20110519 start
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        //==============pangben modify 20110519 stop
        TParm result = this.query("selectPrintData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * update_STA_OPD_DAILY（门诊中间表）的数据
     * @param parm TParm
     * @return TParm
     */
    public TParm update_STA_OPD_DAILY(TParm parm){
        TParm result = this.update("update_STA_OPD_DAILY",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
