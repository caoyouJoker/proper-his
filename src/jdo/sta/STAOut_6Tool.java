package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 损伤中毒和中毒小计的外部原因分类年报表(卫统32表2)</p>
 *
 * <p>Description: 损伤中毒和中毒小计的外部原因分类年报表(卫统32表2)</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-14
 * @version 1.0
 */
public class STAOut_6Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOut_6Tool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOut_6Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOut_6Tool();
        return instanceObject;
    }
    public STAOut_6Tool() {
        setModuleName("sta\\STAOut_6Module.x");
        onInit();
    }
    /**
     * 根据条件生成需要的173病种统计SQL语句
     * @param Condition String
     * @return String
     * ==============pangben modify 20110523 添加区域参数
     */
    private String getSQL(String Condition,String StartDate,String EndDate,String DATA_TYPE,String regionCode){
        String sql = "";
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop

        if(Condition.trim().length()>0){
            sql = "SELECT COUNT(case_no) AS NUM,AGEGROUP,SUM(REAL_STAY_DAYS) AS DAYS FROM ( "+
                " SELECT CASE WHEN TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)<5 THEN '1' " +
                " WHEN TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)>=5 AND TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)<15 THEN '2' " +
                " WHEN TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)>=15 AND TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)<45 THEN '3' " +
                " WHEN TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)>=45 AND TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)<60 THEN '4' " +
                " WHEN TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0)>=60 THEN '5' ELSE 'other' END AS AGEGROUP, " +
                " case_no,TRUNC(MONTHS_BETWEEN(OUT_DATE,BIRTH_DATE)/12,0) as AGE,REAL_STAY_DAYS FROM MRO_RECORD " +
                " WHERE OUT_DATE BETWEEN TO_DATE('"+StartDate+"','YYYYMMDD') AND TO_DATE('"+EndDate+"235959','YYYYMMDDHH24MISS') "+region;
            if(DATA_TYPE.equals("1"))//男
                sql += " AND SEX='1' ";
            else if(DATA_TYPE.equals("2"))//女
                sql += " AND SEX='2' ";
            sql += " AND " + Condition +
                " ) " +
                " GROUP BY AGEGROUP";
        }
        return sql;
    }
    /**
     * 根据传入的SQL语句 查询MRO_RECORD视图
     * @param sql String
     * @return TParm
     * ==============pangben modify 20110523  添加区域参数
     */
    public TParm getRecordSum(String Condition,String StartDate,String EndDate,String DATA_TYPE,String regionCode){
        TParm result = new TParm();
        String sql = this.getSQL(Condition,StartDate,EndDate,DATA_TYPE,regionCode);
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
     * 获取中毒损伤病种的统计信息
     * @param parm TParm  必须参数：DATE_S:起始日期； DATE_E:截止日期
     * @param DATA_TYPE String 统计类型 0:合计   1:男    2:女
     * @return TParm
     */
    public TParm selectDiseaseSum(TParm parm,String DATA_TYPE){
        TParm result = new TParm();
        String StartDate = parm.getValue("DATE_S");//起始日期
        String EndDate = parm.getValue("DATE_E");//截止日期
        String regionCode= parm.getValue("REGION_CODE");//========pangben modify 20110523
        TParm Disease = STAExListTool.getInstance().selectData(new TParm());//获取损伤中毒病种列表
        //根据损伤中毒病种进行循环整理数据
        TParm RecordSum =null;
        for(int i=0;i<Disease.getCount("SEQ");i++){
            String CONDITION = Disease.getValue("CONDITION",i);
            RecordSum = getRecordSum(CONDITION,StartDate,EndDate,DATA_TYPE,regionCode);//获取一种病种的值
            //将一种病种的值转换为一行数据
            TParm row = this.getRowParm(RecordSum,"",Disease.getValue("SEQ",i),DATA_TYPE,Disease.getValue("ICD_DESC",i));
            result.setRowData(i,row,0,"STA_DATE;SEQ;ICD_DESC;DATA_TYPE;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;CONFIRM_FLG;CONFIRM_USER;CONFIRM_DATE;OPT_USER;OPT_TERM");
        }
        return result;
    }
    /**
     * 根据查询出的MroRecord数据提取每种病种的具体数据
     * @param RecordSum TParm
     * @return TParm
     */
    public TParm getRowParm(TParm RecordSum,String STA_DATE,String SEQ,String DATA_TYPE,String ICD_DESC){
        TParm result = new TParm();
        //初始化列
        result.addData("STA_DATE","");
        result.addData("SEQ","");
        result.addData("ICD_DESC","");
        result.addData("DATA_TYPE","");
        result.addData("DATA_01","0");
        result.addData("DATA_02","0");
        result.addData("DATA_03","0");
        result.addData("DATA_04","0");
        result.addData("DATA_05","0");
        result.addData("DATA_06","0");
        result.addData("DATA_07","0");
        int DsSum = 0;//总计出院人数
        int days = 0;//出院病人住院总天数
        for(int i=0;i<RecordSum.getCount();i++){
            if(RecordSum.getValue("AGEGROUP",i).equals("1")){//5岁以下
                result.setData("DATA_02",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("AGEGROUP",i).equals("2")){//5-14岁
                result.setData("DATA_03",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("AGEGROUP",i).equals("3")){//15-44岁
                result.setData("DATA_04",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("AGEGROUP",i).equals("4")){//45-60岁
                result.setData("DATA_05",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }else if(RecordSum.getValue("AGEGROUP",i).equals("5")){//60岁以上
                result.setData("DATA_06",0,RecordSum.getInt("NUM",i));
                DsSum += RecordSum.getInt("NUM",i);
                days += RecordSum.getInt("DAYS",i);
            }
        }
        result.setData("STA_DATE",0,STA_DATE);//日期
        result.setData("SEQ",0,SEQ);//序号
        result.setData("ICD_DESC",0,ICD_DESC);
        result.setData("DATA_TYPE",0,DATA_TYPE);//统计类型
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
     * 删除STA_OUT_06数据
     * @param STA_DATE String
     * @return TParm
     */
    public TParm deleteSTA_OUT_06(String STA_DATE,String DATA_TYPE,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("DATA_TYPE", DATA_TYPE);
        //=============pangben modify 20110523 start
        parm.setData("REGION_CODE", regionCode);
        //=============pangben modify 20110523 stop
        TParm result = update("deleteSTA_OUT_06", parm, conn);
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
    public TParm insertSTA_OUT_06(TParm parm,TConnection conn){
        TParm result = update("insertSTA_OUT_06",parm,conn);
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
    public TParm insertData(TParm parm,TConnection conn){
        TParm result = new TParm();
        String sta_date = parm.getValue("STA_DATE",0);
        String data_type = parm.getValue("DATA_TYPE",0);
        String regionCode=parm.getValue("REGION_CODE",0);
        //========pangben modify 20110523 添加区域参数
        result = this.deleteSTA_OUT_06(sta_date,data_type,regionCode,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm insert = null;
        for(int i=0;i<parm.getCount("STA_DATE");i++){
            insert = new TParm();
            insert.setData("STA_DATE",parm.getData("STA_DATE",i));
            insert.setData("SEQ",parm.getData("SEQ",i));
            insert.setData("DATA_TYPE",parm.getData("DATA_TYPE",i));
            insert.setData("DATA_01",parm.getData("DATA_01",i));
            insert.setData("DATA_02",parm.getData("DATA_02",i));
            insert.setData("DATA_03",parm.getData("DATA_03",i));
            insert.setData("DATA_04",parm.getData("DATA_04",i));
            insert.setData("DATA_05",parm.getData("DATA_05",i));
            insert.setData("DATA_06",parm.getData("DATA_06",i));
            insert.setData("DATA_07",parm.getData("DATA_07",i));
            insert.setData("CONFIRM_FLG",parm.getData("CONFIRM_FLG",i));
            insert.setData("CONFIRM_USER",parm.getData("CONFIRM_USER",i));
            insert.setData("CONFIRM_DATE",parm.getData("CONFIRM_DATE",i));
            insert.setData("OPT_USER",parm.getData("OPT_USER",i));
            insert.setData("OPT_TERM",parm.getData("OPT_TERM",i));
            //==========pangben modify 20110523 start
            insert.setData("REGION_CODE",parm.getData("REGION_CODE",i));
            //==========pangben modify 20110523 stop
            result = this.insertSTA_OUT_06(insert,conn);
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
     * =============pangben modify 20110523 添加区域参数
     */
    public TParm selectPrint(String STA_DATE,String DATA_TYPE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        parm.setData("DATA_TYPE",DATA_TYPE);
        //=============pangben modify 20110523 start
        parm.setData("REGION_CODE",regionCode);
        //=============pangben modify 20110523 stop
        TParm result = this.query("selectPrint", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

}
