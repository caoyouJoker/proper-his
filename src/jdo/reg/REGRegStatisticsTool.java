package jdo.reg;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 挂号信息统计</p>
 *
 * <p>Description: 挂号信息统计</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-3-18
 * @version 4.0
 */
public class REGRegStatisticsTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static REGRegStatisticsTool instanceObject;
    /**
     * 得到实例
     * @return PanelTypeTool
     */
    public static REGRegStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGRegStatisticsTool();
        return instanceObject;
    }
    public REGRegStatisticsTool() {
        setModuleName("reg\\REGRegStatisticsModule.x");
        onInit();
    }
    /**
     * 查询三级科室的挂号人数信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectNumForDept3(TParm parm){
        TParm result = this.query("selectNumForDept3",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * 查询二级科室的挂号人数信息
     * @return TParm
     */
    public TParm selectNumForDept2(TParm parm){
        TParm result = this.query("selectNumForDept2",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * 查询三级科室的挂号费用信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectNumForDept3_M(TParm parm){
        TParm result = this.query("selectNumForDept3_M",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * 查询二级科室的挂号费用信息
     * @return TParm
     */
    public TParm selectNumForDept2_M(TParm parm){
        TParm result = this.query("selectNumForDept2_M",parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }

    /**
     * 查询门急诊部门(三级部门)
     * @return TParm
     */
    //=============pangben modify 20110325 添加参数
    public TParm selectOEDpet(TParm parm){
        TParm result = this.query("selectOEDpet",parm);  //=============pangben modify 20110325 添加参数 parm
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
    /**
     * 查询二级部门
     * @return TParm
     */
    public TParm selectDept2(){
        TParm result = this.query("selectDept2");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        return result;
    }
}
