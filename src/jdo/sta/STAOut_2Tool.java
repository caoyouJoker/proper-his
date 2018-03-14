package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 对外统计报表	-- 卫统5表1</p>
 *
 * <p>Description: 对外统计报表	-- 卫统5表1</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-12
 * @version 1.0
 */
public class STAOut_2Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOut_2Tool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOut_2Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOut_2Tool();
        return instanceObject;
    }
    public STAOut_2Tool() {
        setModuleName("sta\\STAOut_2Module.x");
        onInit();
    }
    /**
     * 根据条件生成需要的173病种统计SQL语句
     * @param Condition String
     * @return String
     */
    private String getSQL(String Condition,String StartDate,String EndDate,String regionCode){
        String sql = "";
        String region="";
        //===========pangben modify 20110520 start
      if (null != regionCode && regionCode.length() > 0)
          region=" AND REGION_CODE='"+regionCode+"' ";
      //===========pangben modify 20110520 stop

        if(Condition.trim().length()>0){
            sql = "SELECT COUNT(CASE_NO) AS NUM,SUM(REAL_STAY_DAYS) AS DAYS,CODE1_STATUS FROM MRO_RECORD "+
            " WHERE OUT_DATE BETWEEN TO_DATE('"+StartDate+"','YYYYMMDD') AND TO_DATE('"+EndDate+"235959','YYYYMMDDHH24MISS') "+
            " AND " +Condition+region+" GROUP BY CODE1_STATUS";
        }
        return sql;
    }
    /**
     * 根据传入的SQL语句 查询MRO_RECORD视图
     * @param sql String
     * @return TParm
     * ============pangben modify 20110523 添加区域参数
     */
    public TParm getRecordSum(String Condition,String StartDate,String EndDate,String regionCode){
        TParm result = new TParm();
        String sql = this.getSQL(Condition,StartDate,EndDate,regionCode);
        if(sql.trim().length()<=0){
            result.setErr(-1,"SQL语句不可为空");
            return result;
        }
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 获取173病种的统计信息
     * @param parm TParm  必须参数：DATE_S:起始日期； DATE_E:截止日期
     * @return TParm
     */
    public TParm selectDiseaseSum(TParm parm) {
        TParm result = new TParm();
        String StartDate = parm.getValue("DATE_S"); //起始日期
        String EndDate = parm.getValue("DATE_E"); //截止日期
        String regionCode= parm.getValue("REGION_CODE"); //========pangben modify 20110523
        TParm Disease = STA173ListTool.getInstance().selectData(new TParm()); //获取173病种列表
        //根据173病种进行循环整理数据
        TParm RecordSum = null;
        for (int i = 0; i < Disease.getCount("SEQ"); i++) {
            String CONDITION = Disease.getValue("CONDITION", i);
            RecordSum = getRecordSum(CONDITION, StartDate, EndDate,regionCode); //获取一种病种的值
            //将一种病种的值转换为一行数据
            TParm row = this.getRowParm(RecordSum, "",
                                        Disease.getValue("SEQ", i),
                                        Disease.getValue("ICD_DESC", i));
            result.setRowData(i, row, 0, "STA_DATE;SEQ;ICD_DESC;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;CONFIRM_FLG;CONFIRM_USER;CONFIRM_DATE;OPT_USER;OPT_TERM");
        }
        return result;
    }
    /**
     * 根据查询出的MroRecord数据提取每种病种的具体数据
     * @param RecordSum TParm
     * @param STA_DATE String
     * @param SEQ String
     * @param ICD_DESC String
     * @return TParm
     */
    public TParm getRowParm(TParm RecordSum,String STA_DATE,String SEQ,String ICD_DESC){
        TParm result = new TParm();
        //初始化列
        result.addData("STA_DATE","");
        result.addData("SEQ","");
        result.addData("ICD_DESC","");
        result.addData("DATA_01","0");
        result.addData("DATA_02","0");
        result.addData("DATA_03","0");
        result.addData("DATA_04","0");
        result.addData("DATA_05","0");
        result.addData("DATA_06","0");
        result.addData("DATA_07","0");
        int DsSum = 0;//总计院人数
        int days = 0;//出院病人住院总天数
        for(int i=0;i<RecordSum.getCount();i++){
            if(RecordSum.getValue("CODE1_STATUS",i).equals("1")){
                result.setData("DATA_02",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("CODE1_STATUS",i).equals("2")){
                result.setData("DATA_03",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("CODE1_STATUS",i).equals("3")){
                result.setData("DATA_04",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("CODE1_STATUS",i).equals("4")){
                result.setData("DATA_05",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("CODE1_STATUS",i).equals("5")){
                result.setData("DATA_06",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }
        }
        result.setData("STA_DATE",0,STA_DATE);//日期
        result.setData("SEQ",0,SEQ);//序号
        result.setData("ICD_DESC",0,ICD_DESC);
        result.setData("DATA_01",0,DsSum);
        result.setData("DATA_07",0,days);
        result.setData("CONFIRM_FLG",0,"");
        result.setData("CONFIRM_USER", 0, "");
        result.setData("CONFIRM_DATE", 0, "");
        result.setData("OPT_USER", 0, "");
        result.setData("OPT_TERM", 0, "");
        return result;
    }
    /**
     * 删除STA_OUT_02数据
     * @param STA_DATE String
     * @return TParm
     * ===========pangben modify 20110520 添加区域参数
     */
    public TParm deleteSTA_OUT_02(String STA_DATE,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        //===========pangben modify 20110520 start
        parm.setData("REGION_CODE",regionCode);
        //===========pangben modify 20110520 stop
        TParm result = update("deleteSTA_OUT_02",parm,conn);
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
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertSTA_OUT_02(TParm parm,TConnection conn){
        TParm result = update("insertSTA_OUT_02",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 导入数据   先删除原有数据 再插入新数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String sta_date = parm.getValue("STA_DATE", 0);
        //===========pangben modify 20110520 start
        String regionCode = parm.getValue("REGION_CODE", 0);
        //===========pangben modify 20110520 stop
        result = this.deleteSTA_OUT_02(sta_date, regionCode, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm insert = null;
        for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
            insert = new TParm();
            insert.setData("STA_DATE", parm.getData("STA_DATE", i));
            insert.setData("SEQ", parm.getData("SEQ", i));
            insert.setData("DATA_01", parm.getData("DATA_01", i));
            insert.setData("DATA_02", parm.getData("DATA_02", i));
            insert.setData("DATA_03", parm.getData("DATA_03", i));
            insert.setData("DATA_04", parm.getData("DATA_04", i));
            insert.setData("DATA_05", parm.getData("DATA_05", i));
            insert.setData("DATA_06", parm.getData("DATA_06", i));
            insert.setData("DATA_07", parm.getData("DATA_07", i));
            insert.setData("CONFIRM_FLG", parm.getData("CONFIRM_FLG", i));
            insert.setData("CONFIRM_USER", parm.getData("CONFIRM_USER", i));
            insert.setData("CONFIRM_DATE", parm.getData("CONFIRM_DATE", i));
            insert.setData("OPT_USER", parm.getData("OPT_USER", i));
            insert.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            insert.setData("REGION_CODE", parm.getData("REGION_CODE", i));//=========pangben modify 20110520
            result = this.insertSTA_OUT_02(insert, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }
    /**
     * 查询打印数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectPrint(String STA_DATE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        //===========pangben modify 20110520 start
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        //===========pangben modify 20110520 stop
        TParm result = this.query("selectPrint", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
