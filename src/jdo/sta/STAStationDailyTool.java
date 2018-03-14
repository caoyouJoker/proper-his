package jdo.sta;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 病区日志中间档</p>
 *
 * <p>Description: 病区日志中间档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STAStationDailyTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAStationDailyTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAStationDailyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAStationDailyTool();
        return instanceObject;
    }

    public STAStationDailyTool() {
        setModuleName("sta\\STAStationDailyModule.x");
        onInit();
    }

    /**
     * 获取要查询的各项目的数据
     * @param parm TParm
     * @param type String  输入要查询的SQL语句名称
     * @return TParm
     */
    public Map getNum(String STADATE, String type,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STADATE",STADATE);
        //=============pangben modify 20110617 start
        if(null!=regionCode&&regionCode.length()>0)
             parm.setData("REGION_CODE",regionCode);
         //=============pangben modify 20110617 stop
        Map map = new HashMap();
        TParm result = this.query(type, parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return map;
        }
        for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
            //以科室code和病区code同时作为 键值
            String key = result.getValue("DEPT_CODE", i)+"_"+result.getValue("STATION_CODE", i);
            map.put(key,result.getValue("O_NUM", i));
        }
        return map;
    }
    /**
     * 插入院区中间表
     * @param parm ActionParm
     * @return ActionParm
     */
    public TParm insertStation_Daily(TParm parms, TConnection conn) {
        TParm result = new TParm();
        if (parms == null) {
            result.setErr( -1,
                          "参数不能为NULL");
            return result;
        }
        //循环提取数据 进行保存
        for (int i = 0; i < parms.getCount("STA_DATE"); i++) {
            TParm parm = new TParm();
            parm.setData("STA_DATE", parms.getData("STA_DATE", i));
            parm.setData("DEPT_CODE", parms.getData("DEPT_CODE", i));
            parm.setData("STATION_CODE", parms.getData("STATION_CODE", i));
            parm.setData("ORIGINAL_NUM", parms.getData("ORIGINAL_NUM", i));
            parm.setData("ADM_NUM", parms.getData("ADM_NUM", i));
            parm.setData("FROM_OTHER_DEPT", parms.getData("FROM_OTHER_DEPT", i));
            parm.setData("RECOVER_NUM", parms.getData("RECOVER_NUM", i));
            parm.setData("EFFECT_NUM", parms.getData("EFFECT_NUM", i));
            parm.setData("INVALED_NUM", parms.getData("INVALED_NUM", i));
            parm.setData("DIED_NUM", parms.getData("DIED_NUM", i));
            parm.setData("OTHER_NUM", parms.getData("OTHER_NUM", i));
            parm.setData("TRANS_DEPT_NUM", parms.getData("TRANS_DEPT_NUM", i));
            parm.setData("END_BED_NUM", parms.getData("END_BED_NUM", i));
            parm.setData("REAL_OPEN_BEB_NUM",
                         parms.getData("REAL_OPEN_BEB_NUM", i));
            parm.setData("AVG_OPEB_BED_NUM",
                         parms.getData("AVG_OPEB_BED_NUM", i));
            parm.setData("REAL_OCUU_BED_NUM",
                         parms.getData("REAL_OCUU_BED_NUM", i));
            parm.setData("DS_TOTAL_ADM_DAY",
                         parms.getData("DS_TOTAL_ADM_DAY", i));
            parm.setData("DS_ADM_NUM",
                    parms.getData("DS_ADM_NUM", i));
            parm.setData("OUYCHK_OI_NUM", parms.getData("OUYCHK_OI_NUM", i));
            parm.setData("OUYCHK_RAPA_NUM", parms.getData("OUYCHK_RAPA_NUM", i));
            parm.setData("OUYCHK_INOUT", parms.getData("OUYCHK_INOUT", i));
            parm.setData("OUYCHK_OPBFAF", parms.getData("OUYCHK_OPBFAF", i));
            parm.setData("HEAL_LV_I_CASE", parms.getData("HEAL_LV_I_CASE", i));
            parm.setData("HEAL_LV_BAD", parms.getData("HEAL_LV_BAD", i));
            parm.setData("GET_TIMES", parms.getData("GET_TIMES", i));
            parm.setData("SUCCESS_TIMES", parms.getData("SUCCESS_TIMES", i));
            parm.setData("CARE_NUMS", parms.getData("CARE_NUMS", i));
            parm.setData("RECOVER_RATE", parms.getData("RECOVER_RATE", i));
            parm.setData("EFFECT_RATE", parms.getData("EFFECT_RATE", i));
            parm.setData("DIED_RATE", parms.getData("DIED_RATE", i));
            parm.setData("BED_RETUEN", parms.getData("BED_RETUEN", i));
            parm.setData("BED_WORK_DAY", parms.getData("BED_WORK_DAY", i));
            parm.setData("BED_USE_RATE", parms.getData("BED_USE_RATE", i));
            parm.setData("AVG_ADM_DAY", parms.getData("AVG_ADM_DAY", i));
            parm.setData("DIAG_RATE", parms.getData("DIAG_RATE", i));
            parm.setData("HEAL_LV_BAD_RATE",
                         parms.getData("HEAL_LV_BAD_RATE", i));
            parm.setData("SUCCESS_RATE", parms.getData("SUCCESS_RATE", i));
            parm.setData("CARE_RATE", parms.getData("CARE_RATE", i));
            parm.setData("OUYCHK_RAPA_RATE",
                         parms.getData("OUYCHK_RAPA_RATE", i));
            parm.setData("OUYCHK_OPBFAF_RATE",
                         parms.getData("OUYCHK_OPBFAF_RATE", i));
            parm.setData("OPT_USER", parms.getData("OPT_USER", i));
            parm.setData("OPT_TERM", parms.getData("OPT_TERM", i));
            //================pangben modify 20110520 start
            parm.setData("REGION_CODE", parms.getData("REGION_CODE", i));
            //删除原有数据
            result = this.delete_Station_Daily(parms.getValue("STA_DATE", i),parms.getValue("DEPT_CODE", i),parms.getValue("STATION_CODE", i),parms.getValue("REGION_CODE", i), conn);
            //================pangben modify 20110520 stop
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            result = this.update("Insert_Station_Daily", parm, conn);
            // 判断错误值
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * 插入 院区中间档 数据
     * @param parm TParm SQL语句参数
     * @param dept TParm 中间表部门列表
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertSTA_STATION_DATA(TParm parmObj, TConnection conn,String regionCode) {
        TParm parm = parmObj.getParm("SQL"); //SQL参数
        TParm dept = parmObj.getParm("DEPT"); //部门参数
        TParm result = new TParm();
        //各个字段的数据集
        //------------------------------//昨日原有人数 start
        //获取前一天日期
        String yesterday = getYesterdayString(parm.getValue("STADATE").toString());
        if (yesterday.equals("")) {
            result.setErr( -1,
                "jdo.sta.StaStationDailyTool.insertSTA_OPD_DAILY=>计算前一天日期错误！");
            return result;
        }
        TParm ORIGINAL_NUM_Parm = new TParm();
        ORIGINAL_NUM_Parm.setData("STADATE", yesterday);
        //=====pangben modify 20110617 添加区域参数
        Map ORIGINAL_NUM = this.getNum(yesterday, "ORIGINAL_NUM",regionCode); //昨日原有人数
        //-----------------------------//昨日原有人数 end
        //=====pangben modify 20110617 添加区域参数
        Map ADM_NUM = this.getNum(parm.getValue("STADATE"), "ADM_NUM",regionCode); //入院人数
//        System.out.println("-=-------ADM_NUM------"+ADM_NUM);
//        System.out.println("ADM_NUM:"+ADM_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map FROM_OTHER_DEPT = this.getNum(parm.getValue("STADATE"), "FROM_OTHER_DEPT",regionCode); //他科转入
//        System.out.println("FROM_OTHER_DEPT:"+FROM_OTHER_DEPT);
        //=====pangben modify 20110617 添加区域参数
        Map RECOVER_NUM = this.getNum(parm.getValue("STADATE"), "RECOVER_NUM",regionCode); //治愈人数
//        System.out.println("RECOVER_NUM:"+RECOVER_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map EFFECT_NUM = this.getNum(parm.getValue("STADATE"), "EFFECT_NUM",regionCode); //好转人数
//        System.out.println("EFFECT_NUM:"+EFFECT_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map INVALED_NUM = this.getNum(parm.getValue("STADATE"), "INVALED_NUM",regionCode); //未愈人数
//        System.out.println("INVALED_NUM:"+INVALED_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map DIED_NUM = this.getNum(parm.getValue("STADATE"), "DIED_NUM",regionCode); //死亡人数
//        System.out.println("DIED_NUM:"+DIED_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map OTHER_NUM = this.getNum(parm.getValue("STADATE"), "OTHER_NUM",regionCode); //其他人数
//        System.out.println("OTHER_NUM:"+OTHER_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map TRANS_DEPT_NUM = this.getNum(parm.getValue("STADATE"), "TRANS_DEPT_NUM",regionCode); //转往他科人数
//        System.out.println("TRANS_DEPT_NUM:"+TRANS_DEPT_NUM);
        //=====pangben modify 20110617 添加区域参数
        Map END_BED_NUM = this.getNum(parm.getValue("STADATE"), "END_BED_NUM",""); //期末实有病床数
        //=====pangben modify 20110617 添加区域参数
        Map REAL_OPEN_BEB_NUM = this.getNum(parm.getValue("STADATE"), "END_BED_NUM",""); //////////////实际开放总床日数  //暂时等于 END_BED_NUM
      //=====pangben modify 20110617 添加区域参数
        Map DS_TOTAL_ADM_DAY = this.getNum(parm.getValue("STADATE"), "DS_TOTAL_ADM_DAY",regionCode); //出院者住院日数
        
        Map DS_ADM_NUM = this.getNum(parm.getValue("STADATE"), "DS_ADM_NUM",regionCode); //出院人数
       //=====pangben modify 20110617 添加区域参数
        Map OUYCHK_OI_NUM = this.getNum(parm.getValue("STADATE"), "OUYCHK_OI_NUM",regionCode); //门诊诊断符合数
      //=====pangben modify 20110617 添加区域参数
        Map OUYCHK_RAPA_NUM = this.getNum(parm.getValue("STADATE"), "OUYCHK_RAPA_NUM",regionCode); //病理诊断符合数
        Map OUYCHK_INOUT = this.getNum(parm.getValue("STADATE"), "OUYCHK_INOUT",regionCode); //入院诊断符合数
        Map OUYCHK_OPBFAF = this.getNum(parm.getValue("STADATE"), "OUYCHK_OPBFAF",regionCode); //术前术后诊断符合数
        Map HEAL_LV_I_CASE = this.getNum(parm.getValue("STADATE"), "HEAL_LV_I_CASE",regionCode); //无菌切口手术数
        Map HEAL_LV_BAD = this.getNum(parm.getValue("STADATE"), "HEAL_LV_BAD",regionCode); //无菌切口化脓数
        Map GET_TIMES = this.getNum(parm.getValue("STADATE"), "GET_TIMES",regionCode); //危重病人抢救次数
        Map SUCCESS_TIMES = this.getNum(parm.getValue("STADATE"), "SUCCESS_TIMES",regionCode); //危重病人抢救成功
        Map CARE_NUMS = this.getNum(parm.getValue("STADATE"), "CARE_NUMS",regionCode); //危重病人抢救成功
        Map AVG_ADM_DAY = this.getNum(parm.getValue("STADATE"), "AVG_ADM_DAY",regionCode); //患者平均住院日
        Map RAPA_NUM = this.getNum(parm.getValue("STADATE"), "RAPA_NUM",regionCode); //病理诊断数
        Map OPBFAF = this.getNum(parm.getValue("STADATE"), "OPBFAF",regionCode); //术前术后诊断数

        int i_ORIGINAL_NUM; //昨日原有人数
        int i_ADM_NUM; //入院人数
        int i_FROM_OTHER_DEPT; //他科转入
        int i_RECOVER_NUM; //治愈人数
        int i_EFFECT_NUM; //好转人数
        int i_INVALED_NUM; //未愈人数
        int i_DIED_NUM; //死亡人数
        int i_OTHER_NUM; //其他人数
        int i_TRANS_DEPT_NUM; //转往他科人数
        int i_END_BED_NUM; //期末实有病床数
        int i_REAL_OPEN_BEB_NUM; //实际开放总床日数
        int i_AVG_OPEB_BED_NUM; //平均开放病床数
        int i_REAL_OCUU_BED_NUM; //实际占用总床数
        int i_DS_TOTAL_ADM_DAY; //出院者住院日数
        int i_DS_ADM_NUM;//出院人数
        int i_OUYCHK_OI_NUM; //门诊诊断符合数
        int i_OUYCHK_RAPA_NUM; //病理诊断符合数
        int i_OUYCHK_INOUT; //入院诊断符合数
        int i_OUYCHK_OPBFAF; //术前术后诊断符合数
        int i_HEAL_LV_I_CASE; //无菌切口手术数
        int i_HEAL_LV_BAD; //无菌切口化脓数
        int i_GET_TIMES; //危重病人抢救次数
        int i_SUCCESS_TIMES; //危重病人抢救成功
        int i_CARE_NUMS; //陪人数
        double i_RECOVER_RATE; //治愈率
        double i_EFFECT_RATE; //好转率
        double i_DIED_RATE; //病死率
        double i_BED_RETUEN; //病床周转
        int i_BED_WORK_DAY; //病床工作日
        double i_BED_USE_RATE; //病床使用率
        double i_AVG_ADM_DAY; //患者平均住院日
        double i_DIAG_RATE; //诊断符合率%
        double i_HEAL_LV_BAD_RATE; //无菌切口化脓率
        double i_SUCCESS_RATE; //危重病人抢救成功率
        double i_CARE_RATE; //陪人率
        double i_OUYCHK_RAPA_RATE; //病理诊断符合率%
        double i_OUYCHK_OPBFAF_RATE; //术前术后诊断符合率
        int i_RAPA_NUM; //病理诊断数
        int i_OPBFAF; //术前术后诊断数
        TParm data = new TParm(); //存储插入数据
        //循环搜索 将对应科室的数据存储
        for (int i = 0; i < dept.getCount("DEPT_CODE"); i++) {
            //中间表科室代码
            String dept_code = dept.getValue("DEPT_CODE", i);
            //以住院科室代码和病区代码作为主键值
            String Dc = dept.getValue("IPD_DEPT_CODE", i)+"_"+dept.getValue("STATION_CODE", i);
            //昨日原有病人数  要用中间表的科室代码 注意要加下滑杠
            i_ORIGINAL_NUM = ORIGINAL_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(ORIGINAL_NUM.get(Dc).toString());
            i_ADM_NUM = ADM_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(ADM_NUM.get(Dc).toString());
            i_FROM_OTHER_DEPT = FROM_OTHER_DEPT.get(Dc) == null ? 0 :
                Integer.valueOf(FROM_OTHER_DEPT.get(Dc).toString());
            //治愈人数
            i_RECOVER_NUM = RECOVER_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(RECOVER_NUM.get(Dc).toString());
            //好转人数
            i_EFFECT_NUM = EFFECT_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(EFFECT_NUM.get(Dc).toString());
             //未愈人数
            i_INVALED_NUM = INVALED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(INVALED_NUM.get(Dc).toString());
            //死亡人数
            i_DIED_NUM = DIED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(DIED_NUM.get(Dc).toString());
            //其他人数
            i_OTHER_NUM = OTHER_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(OTHER_NUM.get(Dc).toString());
            //转往他科人数
            i_TRANS_DEPT_NUM = TRANS_DEPT_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(TRANS_DEPT_NUM.get(Dc).toString());
            //期末实有病床数
            i_END_BED_NUM = END_BED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(END_BED_NUM.get(Dc).toString());
            //实际开放总床日数(等于END_BED_NUM)
            i_REAL_OPEN_BEB_NUM = END_BED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(END_BED_NUM.get(Dc).toString());
            //平均开放病床数(等于END_BED_NUM)
            i_AVG_OPEB_BED_NUM = END_BED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(END_BED_NUM.get(Dc).toString());
            //实际占用总床数  昨日原有人数+入院人数+他科转入-（RECOVER_NUM + EFFECT_NUM+ INVALED_NUM+ OTHER_NUM+ TRANS_DEPT_NUM）
            i_REAL_OCUU_BED_NUM = i_ORIGINAL_NUM + i_ADM_NUM +
                i_FROM_OTHER_DEPT -
                (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_OTHER_NUM +
                 i_TRANS_DEPT_NUM);
            //出院者住院日数
            i_DS_TOTAL_ADM_DAY = DS_TOTAL_ADM_DAY.get(Dc) == null ? 0 :
                Integer.valueOf(DS_TOTAL_ADM_DAY.get(Dc).toString());
            //出院人数
            i_DS_ADM_NUM=DS_ADM_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(DS_ADM_NUM.get(Dc).toString());
            //门诊诊断符合数
            i_OUYCHK_OI_NUM = OUYCHK_OI_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_OI_NUM.get(Dc).toString());
            //病理诊断符合数
            i_OUYCHK_RAPA_NUM = OUYCHK_RAPA_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_RAPA_NUM.get(Dc).toString());
            //入院诊断符合数
            i_OUYCHK_INOUT = OUYCHK_INOUT.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_INOUT.get(Dc).toString());
            //术前术后诊断符合数
            i_OUYCHK_OPBFAF = OUYCHK_OPBFAF.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_OPBFAF.get(Dc).toString());
            //无菌切口手术数
            i_HEAL_LV_I_CASE = HEAL_LV_I_CASE.get(Dc) == null ? 0 :
                Integer.valueOf(HEAL_LV_I_CASE.get(Dc).toString());
            //无菌切口化脓数
            i_HEAL_LV_BAD = HEAL_LV_BAD.get(Dc) == null ? 0 :
                Integer.valueOf(HEAL_LV_BAD.get(Dc).toString());
            //危重病人抢救次数
            i_GET_TIMES = GET_TIMES.get(Dc) == null ? 0 :
                Integer.valueOf(GET_TIMES.get(Dc).toString());
            //危重病人抢救成功
            i_SUCCESS_TIMES = SUCCESS_TIMES.get(Dc) == null ? 0 :
                Integer.valueOf(SUCCESS_TIMES.get(Dc).toString());
            //陪护人数
            i_CARE_NUMS = CARE_NUMS.get(Dc) == null ? 0 :
                Integer.valueOf(CARE_NUMS.get(Dc).toString()); 
            if ( (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_DIED_NUM +
                  i_OTHER_NUM) != 0) //治愈率
                i_RECOVER_RATE = (double) i_RECOVER_NUM /
                    (double) (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                              i_DIED_NUM + i_OTHER_NUM);
            else
                i_RECOVER_RATE = 0;
            if ( (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_DIED_NUM +
                  i_OTHER_NUM) != 0)//好转率
                i_EFFECT_RATE = (double) i_EFFECT_NUM /
                    (double) (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                              i_DIED_NUM + i_OTHER_NUM);
            else
                i_EFFECT_RATE = 0;
            if ( (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_DIED_NUM +
                  i_OTHER_NUM) != 0)//病死率
                i_DIED_RATE = (double) i_DIED_NUM /
                    (double) (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                              i_DIED_NUM + i_OTHER_NUM);
            else
                i_DIED_RATE = 0;
            if (i_AVG_OPEB_BED_NUM != 0)//病床周转  调用计算周转次的方法
                i_BED_RETUEN = STAWorkLogTool.getInstance().countBedReturn( (
                    i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                    i_DIED_NUM + i_OTHER_NUM), i_AVG_OPEB_BED_NUM);
            else
                i_BED_RETUEN = 0;
            i_BED_WORK_DAY = i_END_BED_NUM;//病床工作日  每床每天为1个工作日，每天导入的工作日应该等于期末实有病床数<17>--add by zhangk 2009-7-23
//            System.out.println("-=------i_END_BED_NUM----------------"+i_END_BED_NUM);
//            System.out.println("-=------i_REAL_OCUU_BED_NUM----------------"+i_REAL_OCUU_BED_NUM);
            if(i_END_BED_NUM!=0)
                i_BED_USE_RATE = (double)i_REAL_OCUU_BED_NUM/(double)i_END_BED_NUM*100; //病床使用率=实际占用病床数/开放病床数
            else
                i_BED_USE_RATE = 0;
            i_AVG_ADM_DAY = AVG_ADM_DAY.get(Dc) == null ? 0 :
                StringTool.round(Double.valueOf(AVG_ADM_DAY.get(Dc).toString()), 2); //患者平均住院日
            i_DIAG_RATE = 0; //诊断符合率%  还不清楚需要询问
            if (i_HEAL_LV_I_CASE != 0) //无菌切口化脓率  <27>无菌切口化脓数/<26>无菌切口手术数
                i_HEAL_LV_BAD_RATE = (double) i_HEAL_LV_BAD /
                    (double) i_HEAL_LV_I_CASE;
            else
                i_HEAL_LV_BAD_RATE = 0;
            if (i_GET_TIMES != 0) //危重病人抢救成功率  <29>危重病人抢救成功/<28>危重病人抢救数
                i_SUCCESS_RATE = (double) i_SUCCESS_TIMES /
                    (double) i_GET_TIMES;
            else
                i_SUCCESS_RATE = 0;
            if (i_ADM_NUM != 0) //陪人率
            	i_CARE_RATE = (double) i_CARE_NUMS /
                    (double) i_ADM_NUM;
            else
            	i_CARE_RATE = 0;//陪人率   还不清楚需要询问
            i_RAPA_NUM = RAPA_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(RAPA_NUM.get(Dc).toString());
            i_OPBFAF = OPBFAF.get(Dc) == null ? 0 :
                Integer.valueOf(OPBFAF.get(Dc).toString());
            if (i_RAPA_NUM != 0) //病理诊断符合率%
                i_OUYCHK_RAPA_RATE = (double) i_OUYCHK_RAPA_NUM /
                    (double) i_RAPA_NUM;
            else
                i_OUYCHK_RAPA_RATE = 0;
            if (i_OPBFAF != 0)
                i_OUYCHK_OPBFAF_RATE = (double) i_OUYCHK_OPBFAF /
                    (double) i_OPBFAF; //术前术后诊断符合率
            else
                i_OUYCHK_OPBFAF_RATE = 0;
            data.addData("STA_DATE", parm.getValue("STADATE").toString());
            data.addData("DEPT_CODE", dept.getValue("DEPT_CODE", i));
            data.addData("STATION_CODE", dept.getValue("STATION_CODE", i));//病区CODE
            data.addData("ORIGINAL_NUM", i_ORIGINAL_NUM); //昨日实有病人数
            data.addData("ADM_NUM", i_ADM_NUM);
            data.addData("FROM_OTHER_DEPT", i_FROM_OTHER_DEPT);
            data.addData("RECOVER_NUM", i_RECOVER_NUM);
            data.addData("EFFECT_NUM", i_EFFECT_NUM);
            data.addData("INVALED_NUM", i_INVALED_NUM);
            data.addData("DIED_NUM", i_DIED_NUM);
            data.addData("OTHER_NUM", i_OTHER_NUM);
            data.addData("TRANS_DEPT_NUM", i_TRANS_DEPT_NUM);
            data.addData("END_BED_NUM", i_END_BED_NUM);
            data.addData("REAL_OPEN_BEB_NUM", i_REAL_OPEN_BEB_NUM); //暂时等于 END_BED_NUM
            data.addData("AVG_OPEB_BED_NUM", i_AVG_OPEB_BED_NUM); //暂时等于 END_BED_NUM
            data.addData("REAL_OCUU_BED_NUM", i_REAL_OCUU_BED_NUM);
            data.addData("DS_TOTAL_ADM_DAY", i_DS_TOTAL_ADM_DAY);
            data.addData("DS_ADM_NUM", i_DS_ADM_NUM);
            data.addData("OUYCHK_OI_NUM", i_OUYCHK_OI_NUM);
            data.addData("OUYCHK_RAPA_NUM", i_OUYCHK_RAPA_NUM);
            data.addData("OUYCHK_INOUT", i_OUYCHK_INOUT);
            data.addData("OUYCHK_OPBFAF", i_OUYCHK_OPBFAF);
            data.addData("HEAL_LV_I_CASE", i_HEAL_LV_I_CASE);
            data.addData("HEAL_LV_BAD", i_HEAL_LV_BAD);
            data.addData("GET_TIMES", i_GET_TIMES);
            data.addData("SUCCESS_TIMES", i_SUCCESS_TIMES);
            data.addData("CARE_NUMS", i_CARE_NUMS); //护士，于日报表，自行录入
            data.addData("RECOVER_RATE", StringTool.round(i_RECOVER_RATE,2)); //RECOVER_NUM/RECOVER_NUM + EFFECT_NUM +INVALED_NUM + DIED_NUM + OTHER_NUM
            data.addData("EFFECT_RATE", StringTool.round(i_EFFECT_RATE,2));
            data.addData("DIED_RATE", StringTool.round(i_DIED_RATE,2));
            data.addData("BED_RETUEN",  StringTool.round(i_BED_RETUEN,2));
            data.addData("BED_WORK_DAY", i_BED_WORK_DAY);
            data.addData("BED_USE_RATE",  StringTool.round(i_BED_USE_RATE,2));
            data.addData("AVG_ADM_DAY", StringTool.round(i_AVG_ADM_DAY,2));
            data.addData("DIAG_RATE", StringTool.round(i_DIAG_RATE,2));
            data.addData("HEAL_LV_BAD_RATE", StringTool.round(i_HEAL_LV_BAD_RATE,2));
            data.addData("SUCCESS_RATE", StringTool.round(i_SUCCESS_RATE,2));
            data.addData("CARE_RATE", StringTool.round(i_CARE_RATE,2));
            data.addData("OUYCHK_RAPA_RATE", StringTool.round(i_OUYCHK_RAPA_RATE,2));
            data.addData("OUYCHK_OPBFAF_RATE", i_OUYCHK_OPBFAF_RATE);
            data.addData("OPT_USER", parm.getValue("OPT_USER"));
            data.addData("OPT_TERM", parm.getValue("OPT_TERM"));
            //===========pangben modify 20110520 start
            data.addData("REGION_CODE", parm.getValue("REGION_CODE"));
            //===========pangben modify 20110520 stop
        }
//    System.out.println("data====>"+data);
        //插入数据
        result = this.insertStation_Daily(data, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 获取前一天的日期字符串
     * @param date String  格式 YYYYMMDD
     * @return String
     */
    private String getYesterdayString(String date) {
        String yesterday = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date d;
        try {
            d = df.parse(date);
            Calendar ctest = Calendar.getInstance();
            ctest.setTime(d);
            ctest.add(Calendar.DATE, -1);
            d = ctest.getTime();
            yesterday = df.format(d);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return yesterday;
    }
    /**
     * 查询登录人员的主科室
     * @param userID String
     * @return String
     */
    private String getOPTDeptCode(String userID){
        String DeptCode="";
        return DeptCode;
    }
    /**
     * 删除病区中间档信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delete_Station_Daily(TParm parm,TConnection conn){
        TParm result = this.update("delete_Station_Daily",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 删除病区中间档信息
     * @param parm TParm
     * @param regionCode 区域
     * @param conn TConnection
     * @return TParm
     * ========pangben modify 20110520 添加区域参数
     */
    public TParm delete_Station_Daily(String STA_DATE,String Dept_code,String Station_code,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        parm.setData("DEPT_CODE",Dept_code);
        parm.setData("STATION_CODE",Station_code);
        //========pangben modify 20110520 start
        parm.setData("REGION_CODE",regionCode);
        //========pangben modify 20110520 stop
        TParm result = this.update("delete_Station_Daily",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 导入病区中间表信息
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parmObj, TConnection conn) {
        TParm parm = parmObj.getParm("SQL"); //SQL参数
//        TParm dept = parmObj.getParm("DEPT"); //部门参数
        TParm result = new TParm();
//        //先删除原有数据
//        for(int i=0;i<dept.getCount("DEPT_CODE");i++){
//            result = this.delete_Station_Daily(parm.getValue("STADATE"),dept.getValue("DEPT_CODE",i), conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                    result.getErrName());
//                return result;
//            }
//        }
        result = insertSTA_STATION_DATA(parmObj,conn,parm.getValue("REGION_CODE"));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 自动批次导入信息表
     * @param p TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm batchData(TParm p,TConnection conn) {
        //获取昨天的日期
        Timestamp time = StringTool.rollDate(SystemTool.getInstance().getDate(),-1);
        String yestodate = StringTool.getString(time,"yyyyMMdd");
        TParm parm = new TParm();//日期参数
        parm.setData("STADATE",yestodate);
        TParm dept = new TParm();//部门列表  获取中间对照部门中的门诊部门
        dept = STADeptListTool.getInstance().selectIPD_DEPT(p);
        TParm parmObj = new TParm();//总参数
        parmObj.setData("SQL",parm.getData());
        parmObj.setData("DEPT",dept.getData());
        TParm result = new TParm();
//        //先删除原有数据
//        TParm delParm = new TParm();
//        delParm.setData("STA_DATE",parm.getValue("STADATE"));
//        result = this.delete_Station_Daily(delParm,conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
        result = insertSTA_STATION_DATA(parmObj,conn,"");
        if (result.getErrCode() < 0) {
            err("STA住院批次ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询 STA_STATION_DAILY 病区中间档数据
     * @param parm TParm
     * @return TParm
     */
    public TParm select_Station_Daily(TParm parm){
        TParm result = this.query("select_Station_Daily",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 修改实有病人数
     * @param parm TParm
     * @return TParm
     */
    public TParm updateREAL_OCUU_BED_NUM(TParm parm,TConnection conn){
        TParm result = this.update("updateREAL_OCUU_BED_NUM",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
