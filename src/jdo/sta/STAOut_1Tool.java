package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.text.DecimalFormat;

/**
 * <p>Title: 医院、卫生院病床使用及病患动态（卫统2表1）</p>
 *
 * <p>Description: 医院、卫生院病床使用及病患动态（卫统2表1）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-8
 * @version 1.0
 */
public class STAOut_1Tool
    extends TJDOTool {
    /**
     * 实例
     */
    public static STAOut_1Tool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STAOut_1Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOut_1Tool();
        return instanceObject;
    }

    public STAOut_1Tool() {
        setModuleName("sta\\STAOut_1Module.x");
        onInit();
    }

    /**
     * 查询 工作报表的月总合 作为 卫统2表1的数据源
     * @param parm TParm 必须参数  DATE_S:起始日期  DATE_E:截止日期
     * @return TParm
     *
     */
    public TParm selectSTA_DAILY_02_Sum(TParm parm) {
        String sql = STASQLTool.getInstance().getSTA_DAILY_02_Sum(parm.getValue(
            "DATE_S"), parm.getValue("DATE_E"),parm.getValue("REGION_CODE"));//====pangben modify 20110520 添加区域参数
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
     * 查询 工作报表的某一天总合 作为 卫统2表1的数据源
     * @param parm TParm
     * @return TParm
     * ================pangben modify 20110520 添加区域参数
     */
    public TParm getSTA_DAILY_02_DAY_SUM(String STA_DATE,String regionCode) {
        String sql = STASQLTool.getInstance().getSTA_DAILY_02_DAY_SUM(STA_DATE,regionCode);//==pangben modify 20110520 添加区域参数
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
     * 查询 门诊统计中间表的月总和 作为 卫统2表1的数据源
     * @param STADATE String  格式 yyyyMM
     * @return TParm
     */
    public TParm selectSTA_OPD_DAILY_Sum(TParm parm) {
        String sql = STASQLTool.getInstance().getSTA_OPD_DAILY_Sum(parm.
            getValue("DATE_S"), parm.getValue("DATE_E"),parm.getValue("REGION_CODE"));//=========pangben modify 20110520 添加区域参数
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
     * 查询STA_OUT_01表信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OUT_01(TParm parm) {
        TParm result = this.query("selectSTA_OUT_01", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入STA_OUT_01表信息  每月一笔
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_01(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_OUT_01", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改STA_OUT_01表信息 传入SQL数组
     * @param sql String[]
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_01bySQL(String[] sql, TConnection conn) {
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除STA_OUT_01表信息
     * @param STADATE String
     * @return TParm
     * ==========pangben modify 20110520 添加区域参数
     */
    public TParm deleteSTA_OUT_01(String STADATE, String regionCode,TConnection conn) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STADATE);
        //==========pangben modify 20110520 start
        parm.setData("REGION_CODE", regionCode);
        //==========pangben modify 20110520 stop
        TParm result = this.update("deleteSTA_OUT_01", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改STA_OUT_01中的数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_01(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getData("SQL1") == null || parm.getData("SQL2") == null) {
            result.setErr( -1, "缺少参数");
            return result;
        }
        String[] sql1 = (String[]) parm.getData("SQL1");
        String[] sql2 = (String[]) parm.getData("SQL2");
        result = updateSTA_OUT_01bySQL(sql1, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_01bySQL(sql2, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 生成
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String STA_DATE = parm.getValue("STA_DATE");
        //=============pangben modify 20110520
        String regionCode=parm.getValue("REGION_CODE");
        result = this.deleteSTA_OUT_01(STA_DATE,regionCode, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.insertSTA_OUT_01(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据日期段查询汇总数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getDataByDate(TParm parm) {
        DecimalFormat df = new DecimalFormat("0.00"); //设定Double类型格式
        //获取 门诊中间表 的月数据和
        TParm opd_Daily = STAOut_1Tool.getInstance().selectSTA_OPD_DAILY_Sum(
            parm);
        //获取 工作报表 的月数据和
        TParm daily02 = STAOut_1Tool.getInstance().selectSTA_DAILY_02_Sum(parm);
        //获取工作日报的月最后一天的数据  为了获取 实有病床数等只能按照天来统计的数据
        TParm daily02_day = STAOut_1Tool.getInstance().getSTA_DAILY_02_DAY_SUM(
            parm.getValue("DATE_E"),parm.getValue("REGION_CODE"));//===pangben modify 20110520 添加区域参数
        if (opd_Daily.getErrCode() < 0 || daily02.getErrCode() < 0 ||
            daily02_day.getErrCode() < 0) {
            return null;
        }
        TParm result = new TParm();
        result.addData("DATA_01", "1");
        result.addData("DATA_02", daily02.getInt("DATA_02", 0) +
                       daily02.getInt("DATA_03", 0) +
                       opd_Daily.getInt("OTHER_NUM", 0)
            ); //总计=门诊+急诊+其他
        result.addData("DATA_03",
                       daily02.getInt("DATA_02", 0) +
                       daily02.getInt("DATA_03", 0)); //门急诊计
        result.addData("DATA_04", daily02.getData("DATA_02", 0)); //门诊人次
        result.addData("DATA_05", daily02.getData("DATA_03", 0)); //急诊计
        result.addData("DATA_06", daily02.getData("DATA_04", 0)); //急诊死亡
        result.addData("DATA_07", daily02.getData("DATA_05", 0)); //观察人数
        result.addData("DATA_08", daily02.getData("DATA_06", 0)); //观察死亡
        result.addData("DATA_09", opd_Daily.getData("HRM_NUM", 0)); //健康检查人数
        result.addData("DATA_10", daily02.getData("DATA_08", 0)); //入院人数
        result.addData("DATA_11", daily02.getData("DATA_09", 0)); //出院人数总计
        result.addData("DATA_12", daily02.getData("DATA_10", 0)); //其中病人数，计
        result.addData("DATA_13", daily02.getData("DATA_11", 0)); //治愈
        result.addData("DATA_14", daily02.getData("DATA_12", 0)); //好转
        result.addData("DATA_15", daily02.getData("DATA_13", 0)); //未愈
        result.addData("DATA_16", daily02.getData("DATA_14", 0)); //死亡
        result.addData("DATA_17", ""); //手术人次  暂无
        result.addData("DATA_18", daily02_day.getData("DATA_17", 0)); //实有病床数
        result.addData("DATA_19", daily02.getData("DATA_18", 0)); //实际开放总床日数
        result.addData("DATA_20", daily02_day.getData("DATA_19", 0)); //平均开放床位数
        result.addData("DATA_21", daily02.getData("DATA_20", 0)); //实际占用总床日数
        result.addData("DATA_22", daily02.getData("DATA_21", 0)); //出院者占用总床日数
        result.addData("DATA_23",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_11",0)/daily02.getDouble("DATA_10",0)*100));//治愈率
        result.addData("DATA_24",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_12",0)/daily02.getDouble("DATA_10",0)*100));//好转率
        result.addData("DATA_25",
                       daily02.getInt("DATA_10", 0) == 0 ? "" :
                       df.
                       format(daily02.getDouble("DATA_14", 0) /
                              daily02.getDouble("DATA_10", 0) * 100)); //病死率
        result.addData("DATA_26",
                       daily02_day.getInt("DATA_17", 0) == 0 ? "" :
                       df.format((daily02.getDouble("DATA_11", 0) +
                                  daily02.getDouble("DATA_12", 0) +
                                  daily02.getDouble("DATA_13", 0) +
                                  daily02.getDouble("DATA_14", 0) +
                                  daily02.getDouble("DATA_15", 0) +
                                  daily02.getDouble("DATA_15_1", 0)) /
                                 daily02_day.getDouble("DATA_17", 0))); //床位周转次数
        result.addData("DATA_27", STATool.getInstance().getDaysOfMonth(parm.getValue("DATE_E").substring(0,6))); //病床工作日

        if (daily02.getInt("DATA_09", 0) != 0)
            result.addData("DATA_29",
                           daily02.getInt("DATA_21", 0) /
                           daily02.getInt("DATA_09", 0)); //出院者平均住院日
        else
            result.addData("DATA_29", 0);
        //每床与每日门、急诊诊次之比
        if (daily02.getDouble("DATA_02", 0) + daily02.getDouble("DATA_03", 0) !=
            0)
            result.addData("DATA_30",
                           StringTool.round(daily02.getDouble("DATA_03", 0) /
                                   (daily02.getDouble("DATA_02", 0) +
                                           daily02.getDouble("DATA_03", 0)), 2));
        else
            result.addData("DATA_30", "");
        //计算每百门急诊入院人数  与文档不同
        if (daily02.getInt("DATA_02", 0) + daily02.getInt("DATA_03", 0) != 0) {
            double chu = daily02.getDouble("DATA_02", 0) +
                daily02.getDouble("DATA_03", 0);
            double DATA_31 = daily02.getDouble("DATA_08", 0) / (chu / 100);
            result.addData("DATA_31",df.format(DATA_31));
        }
        else
            result.addData("DATA_31", 0);
        //门急诊诊次占总诊次
        double sumZc = opd_Daily.getDouble("OTHER_NUM", 0) +
            daily02.getDouble("DATA_02", 0) + daily02.getDouble("DATA_03", 0); //总诊次=门诊＋急诊＋其他
        if (sumZc != 0)
            result.addData("DATA_32",df.format((daily02.getDouble("DATA_02",0)+daily02.getDouble("DATA_03",0))/sumZc*100));

        else
            result.addData("DATA_32", "");
        //急诊病死率
        if (daily02.getDouble("DATA_03", 0) != 0)
            result.addData("DATA_33",
                           df.format(daily02.getDouble("DATA_04", 0) /
                                     daily02.getDouble("DATA_03", 0) * 100));
        else
            result.addData("DATA_33", 0);
        //观察室病死率
        if (daily02.getDouble("DATA_05", 0) != 0)
            result.addData("DATA_34",
                           df.format(daily02.getDouble("DATA_06", 0) /
                                     daily02.getDouble("DATA_05", 0) * 100));
        else
            result.addData("DATA_34", 0);
        //病床使用率
        if (daily02.getDouble("DATA_18", 0) != 0) {
            result.addData("DATA_28",
                           df.format(daily02.getDouble("DATA_20", 0) /
                           daily02.getDouble("DATA_18", 0) * 100)); //病床使用率
        }
        //必要参数
        result.addData("STA_DATE", "");
        return result;
    }
}
