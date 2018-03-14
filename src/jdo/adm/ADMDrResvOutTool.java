package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 出院通知</p>
 *
 * <p>Description: 出院通知</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-25
 * @version 4.0
 */
public class ADMDrResvOutTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMDrResvOutTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMDrResvOutTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMDrResvOutTool();
        return instanceObject;
    }

    public ADMDrResvOutTool() {
        setModuleName("adm\\ADMDrResvOutModule.x");
        onInit();
    }
    /**
     * 查询出院通知书打印信息
     * @param CASE_NO String
     * @return TParm
     */
    public TParm selectPrintInfo(String CASE_NO){
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        TParm result = this.query("selectPrintInfo",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询病患诊断信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDiag(TParm parm){
        TParm result = this.query("selectDiag",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
