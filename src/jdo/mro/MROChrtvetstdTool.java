package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 病案审核标准</p>
 *
 * <p>Description: 病案审核标准</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-4
 * @version 1.0
 */
public class MROChrtvetstdTool
    extends TJDOTool {
    /**
     * 实例
     *///项目标准分值：此分值按照病历质量考评标准中的排列顺序显示的如果在编码规则中顺序错乱分值将显示错误
    public static MROChrtvetstdTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROChrtvetstdTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROChrtvetstdTool();
        return instanceObject;
    }

    public MROChrtvetstdTool() {
        this.setModuleName("mro\\MROChrtvetstdModule.x");
        this.onInit();
    }
    /**
     * 查询数据
     * @param parm TParm 参数说明：TYPE_CODE,TYPE_DESC两个条件参数 可有可无
     * @return TParm
     */
    public TParm selectdata(String EXAMINE_CODE){
        TParm pm = new TParm();
        pm.setData("EXAMINE_CODE",EXAMINE_CODE+"%");
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
     * 查询数据
     * @param EXAMINE_CODE String
     * @param METHOD_CODE String
     * @return TParm
     */
    public TParm selectdata(String EXAMINE_CODE, String CHECK_RANGE,
                            String METHOD_CODE) {
        TParm pm = new TParm();
        if (!"".equals(EXAMINE_CODE)) {
            pm.setData("EXAMINE_CODE", EXAMINE_CODE + "%");
        }
        pm.setData("CHECK_RANGE", CHECK_RANGE);
        if (!"".equals(METHOD_CODE)) {
            pm.setData("METHOD_CODE", METHOD_CODE);
        }
        TParm result = query("selectdata", pm);
        // 判断错误值
        if (result.getErrCode() < 0) {
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
        parm.setData("EXAMINE_CODE",code);
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
