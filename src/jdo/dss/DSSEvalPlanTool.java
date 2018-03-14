package jdo.dss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 绩效评估方案</p>
 *
 * <p>Description: 绩效评估方案</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DSSEvalPlanTool extends TJDOTool{

    /**
     * 实例
     */
    public static DSSEvalPlanTool instanceObject;

    /**
     * 得到实例
     * @return DSSSQLTool
     */
    public static DSSEvalPlanTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DSSEvalPlanTool();
        return instanceObject;
    }

    /**
     * 构造函数
     */
    public DSSEvalPlanTool() {
        setModuleName("dss\\DSSPlanModule.x");
        onInit();
    }

    /**
     * 绩效评估方案主表信息
     * @return TParm
     */
    public TParm queryDSSEvalPlanM() {
        TParm parm = new TParm();
        TParm result = query("queryDSSEvalPlanM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 绩效评估方案细表信息
     * @return TParm
     */
    public TParm queryDSSEvalPlanD() {
        TParm parm = new TParm();
        TParm result = query("queryDSSEvalPlanD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 通过代码绩效评估方案细表信息
     * @return TParm
     */
    public TParm queryDSSEvalPlanDByCode(String planCode) {
        TParm parm = new TParm();
        parm.setData("PLAN_CODE", planCode);
        TParm result = query("queryDSSEvalPlanDByCode", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 删除方案主表信息
     * @param planCode String
     * @return TParm
     */
    public TParm deletePlanM(String planCode){
        TParm parm = new TParm();
        parm.setData("PLAN_CODE", planCode);
        TParm result = update("deleteDSSEvalPlanM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 删除方案明细表信息
     * @param planCode String
     * @return TParm
     */
    public TParm deletePlanD(String planCode){
        TParm parm = new TParm();
        System.out.println("planCode = " + planCode);
        parm.setData("PLAN_CODE", planCode);
        TParm result = update("deleteDSSEvalPlanD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 取得主表最大顺序号
     * @return TParm
     */
    public int getDSSEvalMaxMSeq() {
        TParm parm = new TParm();
        TParm result = query("getSeqM", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return 0;
        }
        if(result.getCount("SEQ") <= 0)
            return 0;
        if(result.getValue("SEQ",0).length() == 0)
            return 0;
        return result.getInt("SEQ",0);
    }
    /**
     * 取得细表最大顺序号
     * @return TParm
     */
    public int getDSSEvalMaxDSeq() {
        TParm parm = new TParm();
        TParm result = query("getSeqD", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return 0;
        }
        if(result.getCount("SEQ") <= 0)
            return 0;
        if(result.getValue("SEQ",0).length() == 0)
            return 0;
        return result.getInt("SEQ",0);
    }

    /**
     * 得到KPI值
     * @param KPICode String
     * @return TParm
     */
    public TParm getKPI(String KPICode){
        TParm parm = new TParm();
        parm.setData("KPI_CODE",KPICode);
        TParm result = query("getKPI", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
}
