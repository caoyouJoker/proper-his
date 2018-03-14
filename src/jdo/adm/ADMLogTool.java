package jdo.adm;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 住院日志报表</p>
 *
 * <p>Description: 住院日志报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-10-29
 * @version 4.0
 */
public class ADMLogTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMLogTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMLogTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMLogTool() {
        setModuleName("adm\\ADMLogModule.x");
        onInit();
    }

    /**
     * 查询入院病人信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInHosp(TParm parm) {
        TParm result = this.query("selectInHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询出院病人信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutHosp(TParm parm) {
        TParm result = this.query("selectOutHosp", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询死亡病人
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDead(TParm parm){
        TParm result = this.query("selectDead", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询转入病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectINPR(TParm parm){
        TParm result = this.query("selectINPR", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询转出病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOUPR(TParm parm){
        TParm result = this.query("selectOUPR", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 计算实有病床数
     * @param parm TParm
     * @return TParm
     */
    public TParm selectBedSum(TParm parm){
        String sql = "SELECT COUNT(BED_NO) AS NUM " +
            " FROM SYS_BED " +
            " WHERE STATION_CODE # " +
            " AND ACTIVE_FLG='Y' " +
            " AND OCCU_RATE_FLG='Y'";
        if(parm.getValue("STATION").length()<=0){
            sql = sql.replace("#","IN (SELECT STATION_CODE FROM SYS_STATION WHERE DEPT_CODE = '"+parm.getValue("DEPT")+"')");
        }else{
            sql = sql.replace("#","='"+parm.getValue("STATION")+"'");
        }
        //=========pangben modify 20110510 start 添加区域参数
        if (null != parm.getValue("REGION_CODE") &&
            parm.getValue("REGION_CODE").length() != 0)
            sql += " AND REGION_CODE='" + parm.getValue("REGION_CODE") + "'";
        //=========pangben modify 20110510 stop
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询指定日期的实有病人数
     * @param parm TParm  <DEPT>部门必须参数  <DATE>日期必须参数
     * @return TParm
     */
    public TParm selectHave(TParm parm){
        TParm result = this.query("selectHave", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
