package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;

/**
 *
 * <p>Title:给号组别工具类 </p>
 *
 * <p>Description:给号组别工具类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class PanelGroupTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static PanelGroupTool instanceObject;
    /**
     * 得到实例
     * @return PanelGroupTool
     */
    public static PanelGroupTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PanelGroupTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PanelGroupTool() {
        setModuleName("reg\\REGPanelGroupModule.x");
        onInit();
    }

    /**
     * 查询给号组别
     * @return TParm
     */
    public TParm queryTree() {
        TParm result = query("queryTree");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * 新增给号组别
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm) {
        TParm result = new TParm();
        String quegroupCode = parm.getValue("QUEGROUP_CODE");
        if (existsQueGroup(quegroupCode)) {
            result.setErr( -1, "给号组别" + " 已经存在!");
            return result;
        }

        result = update("insertdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新给号组别
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
     * 查询给号组别
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除给号组别
     * @param quegroupCode String
     * @return boolean
     */
    public TParm deletedata(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        TParm result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 判断是否存在给号组别
     * @param quegroupCode String 给号组别代码
     * @return boolean TRUE 存在 FALSE 不存在
     */
    public boolean existsQueGroup(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        return getResultInt(query("existsQueGroup", parm), "COUNT") > 0;
    }

    /**
     * 得到最大诊号
     * @param quegroupCode String
     * @return int
     */
    public int getMaxQue(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        return getResultInt(query("getMaxQue", parm), "MAX_QUE");
    }

    /**
     * 得到VIP注记
     * @param quegroupCode String
     * @return boolean
     */
    public boolean getVipFlg(String quegroupCode) {
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE", quegroupCode);
        return TCM_Transform.getBoolean(getResultString(query("getVipFlg", parm),
            "VIP_FLG"));
    }
    /**
     * 根据号别查询，最大诊号VIP注记
     * @param quegroupCode String
     * @return TParm
     */
    public TParm getInfobyClinicType(String quegroupCode) {
        if(quegroupCode==null||quegroupCode.length()==0)
            return null;
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE",quegroupCode);
        TParm result = query("getInfobyClinicType",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
