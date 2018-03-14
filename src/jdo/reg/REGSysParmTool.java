package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title:挂号参数工具类 </p>
 *
 * <p>Description:挂号参数工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class REGSysParmTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static REGSysParmTool instanceObject;
    /**
     * 得到实例
     * @return REGSysParmTool
     */
    public static REGSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGSysParmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public REGSysParmTool() {
        setModuleName("reg\\REGSysParmModule.x");

        onInit();
    }

    /**
     * 查询挂号参数
     * @return TParm
     */
    public TParm selectdata() {
        TParm result = query("selectdata");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新挂号参数
     * @param parm TParm
     * @return TParm
     */
    public TParm updatedata(TParm parm) {
        TParm result = update("updatedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增参数
     * @param parm TParm
     * @return TParm
     */
    public TParm insert(TParm parm) {
        TParm result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询默认支付方式
     * @return TParm
     */
    public TParm selPayWay() {
        TParm result = query("selPayWay");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }

    /**
     * 查询默认初复诊
     * @return TParm
     */
    public TParm selVisitCode() {
        TParm result = query("selVisitCode");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }

    /**
     * 查询挂号有效天数
     * @return int
     */
    public int selEffectDays() {
        TParm result = query("selEffectDays");
        int effectDays = result.getInt("EFFECT_DAYS", 0);
        return effectDays;
    }

    /**
     * 查询是否可以跨院区挂号
     * @return boolean
     */
    public boolean selOthHospRegFlg() {
        TParm result = query("selOthHospRegFlg");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        String othhospRegFlg = result.getValue("OTHHOSP_REG_FLG", 0);
        if ("Y".equals(othhospRegFlg))
            return true;
        else
            return false;
    }

    /**
     * 查询急诊检伤标记
     * @return boolean
     */
    public boolean selTriageFlg() {
        TParm result = query("selTriageFlg");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return false;
        }
        return true;
    }
}
