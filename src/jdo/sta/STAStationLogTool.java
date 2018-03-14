package jdo.sta;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 病区日志</p>
 *
 * <p>Description: 病区日志</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class STAStationLogTool extends TJDOTool {
    /**
     * 实例
     */
    public static STAStationLogTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAStationLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAStationLogTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public STAStationLogTool() {
        setModuleName("sta\\STAStationLogModule.x");
        onInit();
    }
    /**
     * 查询当日 病区日志信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectdata",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 获取所有科室
     * @return TParm
     */
    public TParm selectDept(TParm parm){
        TParm result = this.query("selectDept",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 获取所有病区
     * @return TParm
     * ==============pangben modify 20110525 添加参数
     */
    public TParm selectStation(String regionCode){
        //=========pangben modify 20110525 start
        TParm parm=new TParm();
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //=========pangben modify 20110525 stop
        TParm result = this.query("selectStation",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 获取中间表中的所有病区
     * @return TParm
     */
    public TParm selectSTAStation(String regionCode){
        //=========pangben modify 20110525 start
        TParm parm=new TParm();
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //=========pangben modify 20110525 stop
        TParm result = this.query("selectSTAStation",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入病区日志档
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_DAILY_01(TParm parm,TConnection conn2){
        TParm result = this.update("insertData",parm,conn2);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入病区日志档
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_DAILY_01(TParm parm){
        TParm result = this.update("insertData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改 病区日志中间档 STA_STATION_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_STATION_DAILY(TParm parm,TConnection conn){
        TParm result = this.update("updateSTA_STATION_DAILY",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改 门急诊中间档 STA_OPD_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OPD_DAILY(TParm parm,TConnection conn){
        TParm result = this.update("updateSTA_OPD_DAILY",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 同时修改 门急诊中间档 STA_OPD_DAILY 和 病区日志中间档 STA_STATION_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateDAILY(TParm parm,TConnection conn){
        TParm result = new TParm();
        if(parm.getData("OPD")==null||parm.getData("STATION")==null){
            result.setErr(-1,"缺少参数");
            return result;
        }
        result = this.updateSTA_OPD_DAILY(parm.getParm("OPD"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.updateSTA_STATION_DAILY(parm.getParm("STATION"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入病区日志档 STA_DAILY_01 同时修改 门急诊中间档 STA_OPD_DAILY 和 病区日志中间档 STA_STATION_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDate(TParm parm,TConnection conn){
        TParm result = new TParm();
        if(parm.getData("OPD")==null||parm.getData("STATION")==null||parm.getData("DAILY")==null){
            result.setErr(-1,"缺少参数");
            return result;
        }
        result = this.updateSTA_OPD_DAILY(parm.getParm("OPD"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.updateSTA_STATION_DAILY(parm.getParm("STATION"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.insertSTA_DAILY_01(parm.getParm("DAILY"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询入院人次
     * @return TParm
     */
    public TParm selectInNum(TParm parm){
        TParm result = this.query("selectInNum",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询出院人次
     * @return TParm
     */
    public TParm selectOutNum(TParm parm){
        TParm result = this.query("selectOutNum",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询 STA_DAILY_01 表数据，用于制作报表
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_DAILY_01(TParm parm) {
        TParm result = this.query("selectSTA_DAILY_01", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除 STA_DAILY_01 表数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteSTA_DAILY_01(TParm parm,TConnection conn2){
        TParm result = this.update("deleteSTA_DAILY_01",parm,conn2);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 生成STA_DAILY_01 表数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertNewData(TParm parm,TConnection conn1){
        TParm result = new TParm();
        if(parm.getData("Del")==null||parm.getData("Insert")==null){
            result.setErr(-1,"缺少参数！");
            return result;
        }
        TParm parmDel = parm.getParm("Del");
        TParm parmIn = parm.getParm("Insert");
        //先删除原有信息
        result = this.deleteSTA_DAILY_01(parmDel,conn1);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //插入新信息
        result = this.insertSTA_DAILY_01(parmIn,conn1);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改STA_DAILY_01表中的数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_DAILY_01(TParm parm,TConnection conn){
        TParm parm1 = parm.getParm("sta_daily_01");
        TParm parm2 = parm.getParm("station_daily");
        TParm result = this.update("updateSTA_DAILY_01",parm1,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = STAStationDailyTool.getInstance().updateREAL_OCUU_BED_NUM(parm2,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询STA_STATION_DAILY表相关数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_STATION_DAILY(TParm parm){
        TParm result = this.query("selectSTA_STATION_DAILY",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询STA_OPD_DAILY表相关数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OPD_DAILY(TParm parm){
        TParm result = this.query("selectSTA_OPD_DAILY",parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 获取转出科病人
     * @param deptCode String 部门code
     * @param date String 日期格式：yyyyMMdd
     * @param regionCode String 区域
     * @return TParm
     * ==============pangben  modify 20110518 添加区域参数
     */
    public TParm selectOUPR(String deptCode,String  stationCode,String date,String regionCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("DATE",date);
        if(null!=stationCode&&!stationCode.equals(""))
            parm.setData("STATION_CODE",stationCode);
        //========pangben modify 20110518 start
       if(null!=regionCode&&!regionCode.equals(""))
           parm.setData("REGION_CODE",regionCode);
       //========pangben modify 20110518 stop
        TParm result = this.query("selectOUPR",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 获取转入科病人
     * @param deptCode String 部门code
     * @param date String 日期格式：yyyyMMdd
     * @param regionCode String 区域
     * @return TParm
     * ==============pangben  modify 20110518 添加区域参数
     */
    public TParm selectINPR(String deptCode,String stationCode,String date,String regionCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("DATE",date);
        if(null!=stationCode&&!stationCode.equals(""))
            parm.setData("STATION_CODE",stationCode);
        //========pangben modify 20110518 start
        if(null!=regionCode&&!regionCode.equals(""))
            parm.setData("REGION_CODE",regionCode);
        //========pangben modify 20110518 stop
        TParm result = this.query("selectINPR",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据用户ID查询 所属部门
     * @param UserID String
     * @return TParm
     */
    public String getDeptByUserID(String UserID){
        TParm parm = new TParm();
        parm.setData("USER_ID",UserID);
        TParm result = this.query("selectDeptByUID",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return "";
        }
        return result.getValue("DEPT_CODE",0);
    }
}
