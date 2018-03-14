package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOpdDailyTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TSocket;
import jdo.sta.STADeptListTool;
import java.util.Map;
import jdo.sta.STAStationDailyTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextFormat;
import jdo.sys.Operator;

/**
 * <p>Title: 中间表导入控制类</p>
 *
 * <p>Description: 中间表导入控制类</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STADailyControl
    extends TControl {

    public void onInit() {
        //获取昨天的日期
        Timestamp time = StringTool.rollDate(SystemTool.getInstance().getDate(),
                                             -1);
        this.setValue("STADATE", time);
    }

    /**
     * 导入指定日期的门诊信息
     */
    public void insertSTA_OPD_DAILY() {
        String STADATE = this.getText("STADATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("请选择日期！");
            return;
        }
        //获取选定日期的前一天的日期
        Timestamp time = StringTool.rollDate( (Timestamp)this.getValue(
            "STADATE"), -1);
        String lastDay = StringTool.getString(time, "yyyyMMdd");
        TParm checkLastDay = new TParm();
        checkLastDay.setData("STA_DATE", lastDay);
        //======================pangben modify 20110520 start 添加区域
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            checkLastDay.setData("REGION_CODE", Operator.getRegion());
        //======================pangben modify 20110520 stop
        //获取选定日期的前一天的数据
        TParm check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(
            checkLastDay);
        if (check.getCount("STA_DATE") <= 0) { //如果前一天数据不存在，不允许导入，会影响数据准确度
            switch (this.messageBox("提示信息",
                                    StringTool.getString(time, "yyyy年MM月dd日") +
                                    "的数据不存在\n导入新数据会影响准确度\n是否导入？",
                                    this.YES_NO_OPTION)) {
            case 0: //生成
                break;
            case 1: //不生成
                return;
            }
        }
        //审核要导入的数据是否已经存在
        TParm checkDay = new TParm();
        checkDay.setData("STA_DATE", STADATE);
        //======================pangben modify 20110520 start 添加区域
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            checkDay.setData("REGION_CODE", Operator.getRegion());
        //======================pangben modify 20110520 stop
        check = STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(checkDay);
        if (check.getCount("STA_DATE") > 0) { //如果数据存在，询问是否导入
            switch (this.messageBox("提示信息",
                                    "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
                case 0: //生成
                    break;
                case 1: //不生成
                    return;
            }
        }
        TParm sql = new TParm();
        sql.setData("ADMDATE", STADATE);
        sql.setData("OPT_USER",Operator.getID());
        sql.setData("OPT_TERM",Operator.getIP());
        //============pangben modify 20110520 start
        sql.setData("REGION_CODE",Operator.getRegion());
         //============pangben modify 20110520 stop
        TParm dept = new TParm();
        //============pangben modify 20110520 添加区域参数
        dept = STADeptListTool.getInstance().selectOE_DEPT(Operator.getRegion());
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STADailyAction",
            "insertSTA_OPD_DAILY", parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return;
        }
        this.messageBox_("门诊中间档导入成功！");
    }

    /**
     * 导入指定日期的住院信息
     */
    public void insertStation_Daily() {
        String STADATE = this.getText("STADATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("请选择日期！");
            return;
        }
        //获取选定日期的前一天的日期
        Timestamp time = StringTool.rollDate( (Timestamp)this.getValue(
            "STADATE"), -1);
        String lastDay = StringTool.getString(time, "yyyyMMdd");
        TParm checkLastDay = new TParm();
        checkLastDay.setData("STA_DATE", lastDay);
        //==========pangben modify 20110523 start
        checkLastDay.setData("REGION_CODE", Operator.getRegion());
        //==========pangben modify 20110523 start
        //获取选定日期的前一天的数据
        TParm check = STAStationDailyTool.getInstance().select_Station_Daily(
            checkLastDay);
        if (check.getCount("STA_DATE") <= 0) { //如果前一天数据不存在，会影响数据准确度，询问是否导入
            switch (this.messageBox("提示信息",
                                    StringTool.getString(time, "yyyy年MM月dd日") +
                                    "的数据不存在\n导入新数据会影响准确度\n是否导入？",
                                    this.YES_NO_OPTION)) {
                case 0: //生成
                    break;
                case 1: //不生成
                    return;
            }
        }

        //审核要导入的数据是否已经存在
        TParm checkDay = new TParm();
        checkDay.setData("STA_DATE", STADATE);
        //==========pangben modify 20110523 start
        checkDay.setData("REGION_CODE", Operator.getRegion());
        //==========pangben modify 20110523 start

        check = STAStationDailyTool.getInstance().select_Station_Daily(checkDay);
        if (check.getCount("STA_DATE") > 0) { //如果数据存在，询问是否导入
            switch (this.messageBox("提示信息",
                                    "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
                case 0: //生成
                    break;
                case 1: //不生成
                    return;
            }
        }

        TParm sql = new TParm();
        sql.setData("STADATE", STADATE);
        sql.setData("OPT_USER",Operator.getID());
        sql.setData("OPT_TERM",Operator.getIP());
        //============pangben modify 20110520 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            sql.setData("REGION_CODE", Operator.getRegion());
         //============pangben modify 20110520 stop
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);//=====pangben modify 20110520 添加区域参数
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STADailyAction",
            "insertStation_Daily", parm);
        if (result.getErrCode() < 0) {
            System.out.println("" + result);
            return;
        }
        this.messageBox_("住院中间档导入成功！");
    }

    public static void main(String[] args) {
        com.javahis.util.JavaHisDebug.runFrame("sta\\STADaily.x");
    }
}
