package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import jdo.sta.STADeptListTool;
import jdo.sta.STAIn_03Tool;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sta.STAWorkLogTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 工作报表
 * </p>
 * 
 * <p>
 * Description: 工作报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-21
 * @version 1.0
 */
public class STAWorkLogControl extends TControl {
    int MDays = 0; // 统计月份的实际天数
    String STA_DEPT_CODE = ""; // 记录中间部门表 code
    TParm result = null; // 报表数据
    String resultType = ""; // 记录数据类型 day：标识日报 month：表示月报
    boolean isSubmitD = false; // 记录提取的日报数据是否已经确认提交 true：确认提交
    boolean isSubmitM = false; // 记录提取的月报数据是否已经确认提交 true：确认提交
    private String LEADER = "";// 记录是否是组长权限 如果LEADER=2那么就是组长权限

    /**
     * 初始化
     */
    public void onInit() {
        Timestamp time = SystemTool.getInstance().getDate();
        this.setValue("STA_DATE1", StringTool.rollDate(time, -1));// 前一天
        this.setValue("STA_DATE2", STATool.getInstance().getLastMonth());// 设置初始时间（上个月）
        // 初始化权限
        if (this.getPopedem("LEADER")) {
            LEADER = "2";
        }
    }


    /**
     * 保存
     */
    public void onSave() {
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table = null;
        boolean submit = false;
        if (t.getSelectedIndex() == 0) { // 日报
            if (!checkSubmit(this.getText("STA_DATE1").replace("/", "")))// 查询各个住院部门是否提交“病区日志”报表，没有提交则返回
                return;
            // 如果不是组长权限 那么已经提交的数据不可再修改
            if (!LEADER.equals("2")) {
                TParm check = STAWorkLogTool.getInstance().checkNum(this.getText("STA_DATE1").replace("/", ""), Operator.getRegion());
                if (check.getErrCode() < 0) {
                    this.messageBox(check.getErrText());
                    return;
                }
                if (check.getCount("STA_DATE") > 0) { // 已存在该日期生成的数据
                    if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                        if (this.getValue("Submit1").equals("Y")) {
                            this.messageBox_("已经提交，不能修改！");
                            return;
                        }
                    }
                }
            }   
            table = (TTable) this.getComponent("TableDay");
            // 判断 日报 是否提交
            if (((TCheckBox) this.getComponent("Submit1")).isSelected())
                submit = true;
        } else if (t.getSelectedIndex() == 1) { // 月报
            // 如果不是组长权限 那么已经提交的数据不可再修改
            if (!LEADER.equals("2")) {
                TParm check = STAWorkLogTool.getInstance().checkNum(this.getText("STA_DATE1").replace("/", ""), Operator.getRegion());
                if (check.getErrCode() < 0) {
                    this.messageBox(check.getErrText());
                    return;
                }
                if (check.getCount("STA_DATE") > 0) { // 已存在该日期生成的数据
                    if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                        if (this.getValue("Submit2").equals("Y")) {
                            this.messageBox_("已经提交，不能修改！");
                            return;
                        }
                    }
                }
            }
            table = (TTable) this.getComponent("TableMouth");
            // 判断 月报 是否提交
            if (((TCheckBox) this.getComponent("Submit2")).isSelected())
                submit = true;
        }
        // 没有数据不进行操作
        if (table.getRowCount() <= 0) {
            return;
        }
        table.acceptText();
        TParm parmValue = table.getParmValue();
        // 获取数据库时间
        Timestamp DBTime = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmValue.getCount(); i++) {
            parmValue.setData("OPT_USER", i, Operator.getID()); // 设置修改人员信息
            parmValue.setData("OPT_TERM", i, Operator.getIP()); // 设置修改IP
            parmValue.setData("OPT_DATE", i, DBTime); // 设置修改时间
        }
        if (submit) { // 提交 需要把所有状态位 修改为 'Y'
            // 给固定数据配数据
            for (int i = 0; i < parmValue.getCount(); i++) {
                parmValue.setData("CONFIRM_FLG", i, "Y"); // 确认标记
                parmValue.setData("CONFIRM_USER", i, Operator.getID()); // 确认者ID
                parmValue.setData("CONFIRM_DATE", i, DBTime); // 确认时间
                parmValue.setData("REGION_CODE", i, Operator.getRegion()); // 区域
            }
            if (onUpdate(parmValue)) {
                this.messageBox_("提交成功！");
                if (t.getSelectedIndex() == 0) {
                    isSubmitD = true;
                } else if (t.getSelectedIndex() == 1) { // 月报
                    isSubmitM = true;
                }
            } else {
                this.messageBox_("提交失败！" + parmValue.getErrText());
            }
        } else { // 不提交
            // 给固定数据配数据
            for (int i = 0; i < parmValue.getCount(); i++) {
                parmValue.setData("CONFIRM_FLG", i, "N"); // 确认标记
                parmValue.setData("CONFIRM_USER", i, Operator.getID()); // 确认者ID
                parmValue.setData("CONFIRM_DATE", i, DBTime); // 确认时间
                parmValue.setData("REGION_CODE", i, Operator.getRegion()); // 区域
            }
            if (onUpdate(parmValue))
                this.messageBox_("修改成功！");
            else
                this.messageBox_("修改失败！" + parmValue.getErrText());
        }
    }

    /**
     * 修改方法
     * 
     * @param parm
     * @return
     */
    public boolean onUpdate(TParm parm) {
        int count = parm.getCount();
        if (count <= 0) {
            return true;
        }
        for (int i = 0; i < count - 1; i++) {
            TParm result = STAWorkLogTool.getInstance().updateSTA_DAILY_02(parm.getRow(i));
            if (result.getErrCode() < 0) return false;
        }
        return true;
    }
    
    /**
     * 生成报表数据
     */
    public void onGenerate() {
        isSubmitD = false;
        isSubmitM = false;
        this.clearValue("DEPT_CODE1;DEPT_CODE2;SubmitFlg;Submit1;Submit2");// 清空控件状态
        String STA_DATE = "";
        String deptCode="";
        TTable table = new TTable();
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        if (t.getSelectedIndex() == 0) { // 日报表
            if (this.getText("STA_DATE1").trim().length() <= 0) {
                this.messageBox_("请选择日期!");
                return;
            }
            deptCode = this.getValueString("DEPT_CODE1");
            STA_DATE = this.getText("STA_DATE1").replace("/", ""); // 获取日期
            table = (TTable) this.getComponent("TableDay"); // 获取Table
            // if(!checkSubmit(STA_DATE))//查询各个住院部门是否提交“病区日志”报表，没有提交则返回
            // return;
            // 判断是否已经生成该日期的数据
            TParm check = STAWorkLogTool.getInstance().checkNum(STA_DATE, Operator.getRegion());
            if (check.getErrCode() < 0) {
                this.messageBox("没有该日期的数据");
                return;
            }
            if (check.getCount("STA_DATE") > 0) { // 已存在该日期生成的数据
                if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                    isSubmitD = true; // 标识日报已经提交
                    this.setValue("Submit1", true);
                    this.messageBox_("数据已经提交，不能重新生成！");
                    TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // 绑定数据
                    return;
                } else {
                    switch (this.messageBox("提示信息", "数据已存在，是否重新生成？", TControl.YES_NO_OPTION)) {
                    // 生成
                    case 0:
                        break;
                    // 不生成
                    case 1:
                        TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // 绑定数据
                        return;
                    }
                }
            }
            DayReport(); // 生成日报数据
        } else if (t.getSelectedIndex() == 1) { // 月报表
            if (this.getText("STA_DATE2").trim().length() <= 0) {
                this.messageBox_("请选择日期!");
                return;
            }
            deptCode = this.getValueString("DEPT_CODE2");
            STA_DATE = this.getText("STA_DATE2").replace("/", "");
            table = (TTable) this.getComponent("TableMouth");
            // 判断是否已经生成该月份的数据
            TParm check = STAWorkLogTool.getInstance().checkNum(STA_DATE, Operator.getRegion());
            if (check.getErrCode() < 0) {
                this.messageBox("没有该日期的数据");
                return;
            }
            if (check.getCount("STA_DATE") > 0) { // 已存在该日期生成的数据
                if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                    isSubmitM = true; // 标识 月报已经提交
                    this.setValue("Submit2", true);
                    this.messageBox_("数据已经提交，不能重新生成！");
                    TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // 绑定数据
                    return;
                } else {
                    switch (this.messageBox("提示信息", "数据已存在，是否重新生成？", TControl.YES_NO_OPTION)) {
                    // 生成
                    case 0:
                        break;
                    // 不生成
                    case 1:
                        TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // 绑定数据
                        return;
                    }
                }
            }
            MonthReport(); // 生成月报数据
        }
        if (result == null) {
            this.messageBox_("生成失败！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        parm.setData("Daily", result.getData());
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm re = TIOM_AppServer.executeAction("action.sta.STAWorkLogAction", "insertData", parm);
        if (re.getErrCode() < 0) {
            this.messageBox_("生成失败！");
            return;
        }
        this.messageBox_("生成成功！");
        TableBind(table, STA_DATE, Operator.getRegion(),deptCode); // 绑定数据
    }
    

    /**
     * 日报表数据
     */
    private void DayReport() {
        TParm parm = new TParm();
        parm.setData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE1")).getText().replace("/", ""));
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
//        if (this.getValue("DEPT_CODE1") != null
//                && this.getValue("DEPT_CODE1").toString().trim().length() > 0) {
//            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE1"));// 部门CODE
//        }
        TParm re = STAWorkLogTool.getInstance().selectDataDay(parm);
        if (re.getErrCode() < 0) {
            result = null;
            return;
        }
        reportParm(re, "D");
        resultType = "day";
    }

    /**
     * 月报表数据
     */
    private void MonthReport() {
        TParm parm = new TParm();
        parm.setData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE2")).getText().replace("/", ""));
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm re = STAWorkLogTool.getInstance().selectDataMonth(parm);
        if (re.getErrCode() < 0) {
            result = null;
            return;
        }
        // System.out.println("月报:"+re);
        reportParm(re, "M"); // 生成月报表数据
        resultType = "month";
    }

    /**
     * 用于生成报表的数据
     * @param parm TParm
     * @param type String   “M”:月报表    “D”:日报表
     */
    private void reportParm(TParm parm, String type) {
        DecimalFormat df = new DecimalFormat("0.00"); // 设定Double类型格式
        if (type.equals("M"))
            MDays = this.getDaysOfMonth(((TTextFormat) this.getComponent("STA_DATE2")).getText());
        result = new TParm();
        for (int i = 0; i < parm.getCount("OUTP_NUM"); i++) {
            // 如果 OUTP_NUM和ERD_NUM有一个为空 表示 该部门不是门急诊部门 不需门急诊的统计项目
            if (parm.getData("OUTP_NUM", i) == null || parm.getData("ERD_NUM", i) == null) {
                result.addData("DATA_01", "");
            } else {
                // 数据库 ERD_NUM字段类型错误 注意要修改
                int DATA_01 = parm.getInt("OUTP_NUM", i) + Integer.valueOf(parm.getValue("ERD_NUM", i).trim());
                result.addData("DATA_01", DATA_01); // 诊疗人数总计<1>
            }
            result.addData("DATA_02", parm.getValue("OUTP_NUM", i)); // 门诊人次数<2>
            result.addData("DATA_03", parm.getValue("ERD_NUM", i)); // 急诊人次计 <3>
            result.addData("DATA_04", parm.getValue("ERD_DIED_NUM", i)); // 急诊死亡人次<4>
            result.addData("DATA_05", parm.getValue("OBS_NUM", i)); // 留观病人数<5>
            result.addData("DATA_06", parm.getValue("OBS_DIED_NUM", i)); // 留观死亡人数<6>
            result.addData("DATA_07", parm.getValue("DATA_07", i)); // 期初实有病人<7>
            result.addData("DATA_08", parm.getValue("DATA_08", i)); // 入院人数<8>
            result.addData("DATA_08_1", parm.getValue("DATA_08_1", i)); // 他科转入<8-1>
            result.addData("DATA_09", parm.getValue("DATA_09", i)); // 出院人数总计<9>
            result.addData("DATA_10", parm.getValue("DATA_10", i)); // 病人数计<10>
            result.addData("DATA_11", parm.getValue("DATA_11", i)); // 治愈<11>
            result.addData("DATA_12", parm.getValue("DATA_12", i)); // 好转<12>
            result.addData("DATA_13", parm.getValue("DATA_13", i)); // 未愈<13>
            result.addData("DATA_14", parm.getValue("DATA_14", i)); // 死亡<14>
            result.addData("DATA_15", parm.getValue("DATA_15", i)); // 其他<15>
            result.addData("DATA_15_1", parm.getValue("DATA_15_1", i)); // 转往他科人数<15_1>
            result.addData("DATA_16", parm.getValue("DATA_16", i)); // 实有病人数<16>
            result.addData("DATA_17", parm.getValue("DATA_17", i)); // 期末实有病床<17>
            result.addData("DATA_18", parm.getValue("DATA_18", i)); // 实际开放总床日<18>
            if (parm.getData("DATA_18", i) != null) { // 判断该数据是否为 null
                if (type.equals("M")) {// 月报        取平均数
                    result.addData("DATA_19", parm.getInt("DATA_18", i) / MDays); // 平均开放病床数<19>
                } else { // 日报         取当天实有病床数
                    result.addData("DATA_19", parm.getValue("DATA_17", i)); // 平均开放病床数<19>
                }
            } else
                result.addData("DATA_19", ""); // 平均开放病床数<19>
            result.addData("DATA_20", parm.getValue("DATA_16", i)); // 实际占用总床数<20>
            result.addData("DATA_21", parm.getValue("DATA_19", i)); // 出院者住院日数<21>
            result.addData("DATA_22", parm.getValue("OUYCHK_OI_NUM", i)); // 门诊诊断符合数<22>
            result.addData("DATA_23", parm.getValue("OUYCHK_RAPA_NUM", i)); // 病理诊断符合数<23>
            result.addData("DATA_24", parm.getValue("OUYCHK_INOUT", i)); // 入院诊断符合数<24>
            result.addData("DATA_25", parm.getValue("OUYCHK_OPBFAF", i)); // 术前术后符合数<25>
            result.addData("DATA_26", parm.getValue("HEAL_LV_I_CASE", i)); // 无菌切口手术数<26>
            result.addData("DATA_27", parm.getValue("HEAL_LV_BAD", i)); // 无菌切口化脓数<27>
            result.addData("DATA_28", parm.getValue("GET_TIMES", i)); // 危重病人抢救数<28>
            result.addData("DATA_29", parm.getValue("SUCCESS_TIMES", i)); // 危重病人抢救成功
            result.addData("DATA_30", parm.getValue("DATA_22", i)); // 陪人数<30>
            double O_num = parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i); // 用于计算治愈率
            if (O_num == 0) {
                result.addData("DATA_31", ""); // 治愈率<31>
                result.addData("DATA_32", ""); // 好转率<32>
                result.addData("DATA_33", ""); // 病死率<33>
            } else {
                result.addData("DATA_31", df.format(parm.getDouble("DATA_11", i) / O_num * 100)); // 治愈率<31>
                result.addData("DATA_32", df.format(parm.getDouble("DATA_12", i) / O_num * 100)); // 好转率<32>
                result.addData("DATA_33", df.format(parm.getDouble("DATA_14", i) / O_num * 100)); // 病死率<33>
            }
            // 病床周转(次)
            if (type.equals("M")) { // 月报 病床周转(次)=期内出院人数/ 同期平均开放病床数
                if (parm.getInt("DATA_18", i) > 0 && parm.getDouble("DATA_09", i) > 0) {
                    result.addData("DATA_34", parm.getDouble("DATA_09", i) / (parm.getDouble("DATA_18", i) / MDays));
                } else
                    result.addData("DATA_34", "");
            } else { // 日报
                if (parm.getDouble("BED_RETUEN", i) != 0)
                    result.addData("DATA_34", parm.getDouble("BED_RETUEN", i));
                else
                    result.addData("DATA_34", "");
            }
            result.addData("DATA_35", parm.getValue("BED_WORK_DAY", i)); // 病床工作日<35>
            // 病床使用率<36> 日报不统计
            if (type.equals("M")) { // 月报
                double DATA_36 = 0;
                DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(parm.getInt("DATA_16", i), MDays, parm.getInt("DATA_18", i) / MDays);
                result.addData("DATA_36", DATA_36);
            } else { // 日报 日报不统计
                result.addData("DATA_36", "");
            }
            // 患者平均住院日<37>
            if (parm.getData("DATA_09", i) == null) // 判断数据是否为Null
                result.addData("DATA_37", "");
            else if (parm.getInt("DATA_09", i) == 0)
                result.addData("DATA_37", "0");
            else
                result.addData("DATA_37", parm.getInt("DATA_21", i) / parm.getInt("DATA_09", i));
            result.addData("DATA_38", ""); // 诊断符合率<38>
            // 无菌切口化脓率<39>
            if (parm.getData("HEAL_LV_I_CASE", i) == null) // 判断数据是否为Null
                result.addData("DATA_39", "");
            else if (parm.getDouble("HEAL_LV_I_CASE", i) == 0)
                result.addData("DATA_39", "0");
            else
                result.addData("DATA_39", df.format(parm.getDouble("HEAL_LV_BAD", i)
                                                  / parm.getDouble("HEAL_LV_I_CASE", i) * 100));
            // 危重病人抢救成功%<40>
            if (parm.getData("GET_TIMES", i) == null)
                result.addData("DATA_40", "");
            else if (parm.getDouble("GET_TIMES", i) == 0)
                result.addData("DATA_40", "100");
            else
                result.addData("DATA_40", df.format(parm.getDouble("SUCCESS_TIMES", i)
                                                  / parm.getDouble("GET_TIMES", i) * 100));
            // 陪人率%<41> = 陪人数/实有病人数
            if (parm.getData("DATA_16", i) == null)
                result.addData("DATA_41", "");
            else if (parm.getDouble("DATA_16", i) == 0)
                result.addData("DATA_41", "0");
            else
                result.addData("DATA_41", df.format(parm.getDouble("DATA_22", i)
                                                  / parm.getDouble("DATA_16", i) * 100));
            // //病理诊断符合率%<41_1>
            if (parm.getData("DATA_10", i) == null
                    && parm.getData("DATA_15", i) == null)
                result.addData("DATA_41_1", "");
            else if ((parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15",
                    i)) == 0)
                result.addData("DATA_41_1", "100");
            else
                result.addData("DATA_41_1", df.format(parm.getDouble("OUYCHK_RAPA_NUM", i)
                                                     / (parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i)) * 100));
            // 术前术后诊断符合率%<41_2>
            if (parm.getData("DATA_10", i) == null && parm.getData("DATA_15", i) == null)
                result.addData("DATA_41_2", "");
            else if ((parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i)) == 0)
                result.addData("DATA_41_2", "0");
            else
                result.addData("DATA_41_2", df.format(parm.getDouble("OUYCHK_OPBFAF", i)
                                                      / (parm.getDouble("DATA_10", i) + parm.getDouble("DATA_15", i)) * 100));

            // 必须参数
            result.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i)); // 科室
            result.addData("STATION_CODE", parm.getValue("STATION_CODE", i)); // 科室
            result.addData("CONFIRM_FLG", "N"); // 确认注记 生成数据默认为 ‘N’
            result.addData("CONFIRM_USER", Operator.getID()); // 确认护士
            result.addData("OPT_USER", Operator.getID());
            result.addData("OPT_TERM", Operator.getIP());
            // 日期
            if (type.equals("M")) // 月报
            result.addData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE2")).getText().replace("/", ""));
            else
            // 日报
            result.addData("STA_DATE", ((TTextFormat) this.getComponent("STA_DATE1")).getText().replace("/", ""));
            // 病区日志是否提交标记
            if (type.equals("D")) // 日报
            result.addData("SUBMIT_FLG", parm.getValue("SUBMIT_FLG", i));
        }
    }

    
    /**
     * Table数据绑定
     */
    private void TableBind(TTable table, String STA_DATE, String regionCode,String deptcode) {
        DecimalFormat df = new DecimalFormat("0.00"); // 设定Double类型格式
        String sql = STASQLTool.getInstance().getSelectSTA_DAILY_02(STA_DATE, regionCode, deptcode);
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        // 合计 shibl 20210910 add
        int data01 = 0;// 诊疗人数总计合计<1>
        int data02 = 0;// 门诊人次数合计<2>
        int data03 = 0;// 急诊人次合计<3>
        int data04 = 0;// 急诊死亡人次合计<4>
        int data05 = 0;// 留观病人数合计<5>
        int data06 = 0;// 留观死亡人数合计<6>
        int data07 = 0;// 期初实有病人合计<7>
        int data08 = 0;// 入院人数合计<8>
        int data0801 = 0;// 他科转入合计<8-1>
        int data09 = 0;// 出院人数总计合计<9>
        int data10 = 0;// 病人数计合计<10>
        int data11 = 0;// 治愈合计<11>
        int data12 = 0;// 好转合计<12>
        int data13 = 0;// 未愈合计<13>
        int data14 = 0;// 死亡合计<14>
        int data15 = 0;// 其他合计<15>
        int data1501 = 0;// 转往他科人数合计<15_01>
        int data16 = 0;// 实有病人数合计<16>
        int data17 = 0;// 期末实有病人数<17>
        int data18 = 0;// 实际开放总床日数合计<18>
        int data19 = 0;// 平均开放床数合计<19>
        int data20 = 0;// 实际占床总数合计
        int data21 = 0;// 出院患者住院日数合计
        int data22 = 0;// 门诊诊断符数合计
        int data23 = 0;// 病历诊断符合数合计
        int data24 = 0;// 入院诊断符合数合计
        int data25 = 0;// 术前术后符合数合计
        int data26 = 0;// 无菌切口手术数合计
        int data27 = 0;// 无菌切口化脓数合计
        int data28 = 0;// 危重病人抢救数合计
        int data29 = 0;// 危重病人抢救成功合计
        int data30 = 0;// 陪 人数合计
        double data31 = 0;// 治愈率
        double data32 = 0;// 好转率
        double data33 = 0;// 病死率
        double data34 = 0;// 病床周转
        int data35 = 0;// 病床工作日
        double data36 = 0;// 病床使用率
        int data37 = 0;// 患者平均住院日<37>
        double data38 = 0;// 诊断符合率
        double data39 = 0;// 无菌切口化脓率<39>
        double data40 = 0;// 危重病人抢救成功%<40>
        double data41 = 0;// 陪人率%<41>
        double data4101 = 0;// 病理诊断符合率%<41_1>
        double data4102 = 0;// 术前术后诊断符合率%<41_2>
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getCount() <= 0) {
            this.messageBox("没有数据");
            return;
        }
        for (int i = 0; i < parm.getCount(); i++) {
            data01 += parm.getInt("DATA_01", i);
            data02 += parm.getInt("DATA_02", i);
            data03 += parm.getInt("DATA_03", i);
            data04 += parm.getInt("DATA_04", i);
            data05 += parm.getInt("DATA_05", i);
            data06 += parm.getInt("DATA_06", i);
            data07 += parm.getInt("DATA_07", i);
            data08 += parm.getInt("DATA_08", i);
            data0801 += parm.getInt("DATA_08_1", i);
            data09 += parm.getInt("DATA_09", i);
            data10 += parm.getInt("DATA_10", i);
            data11 += parm.getInt("DATA_11", i);
            data12 += parm.getInt("DATA_12", i);
            data13 += parm.getInt("DATA_13", i);
            data14 += parm.getInt("DATA_14", i);
            data15 += parm.getInt("DATA_15", i);
            data1501 += parm.getInt("DATA_15_1", i);
            data16 += parm.getInt("DATA_16", i);
            data17 += parm.getInt("DATA_17", i);
            data18 += parm.getInt("DATA_18", i);
            data19 += parm.getInt("DATA_19", i);
            data20 += parm.getInt("DATA_20", i);
            data21 += parm.getInt("DATA_21", i);
            data22 += parm.getInt("DATA_22", i);
            data23 += parm.getInt("DATA_23", i);
            data24 += parm.getInt("DATA_24", i);
            data25 += parm.getInt("DATA_25", i);
            data26 += parm.getInt("DATA_26", i);
            data27 += parm.getInt("DATA_27", i);
            data28 += parm.getInt("DATA_28", i);
            data29 += parm.getInt("DATA_29", i);
            data30 += parm.getInt("DATA_30", i);
        }
        parm.addData("SUBMIT_FLG", "N");
        parm.addData("DEPT_CODE", "合计:");
        parm.addData("DATA_01", data01);
        parm.addData("DATA_02", data02);
        parm.addData("DATA_03", data03);
        parm.addData("DATA_04", data04);
        parm.addData("DATA_05", data05);
        parm.addData("DATA_06", data06);
        parm.addData("DATA_07", data07);
        parm.addData("DATA_08", data08);
        parm.addData("DATA_08_1", data0801);
        parm.addData("DATA_09", data09);
        parm.addData("DATA_10", data10);
        parm.addData("DATA_11", data11);
        parm.addData("DATA_12", data12);
        parm.addData("DATA_13", data13);
        parm.addData("DATA_14", data14);
        parm.addData("DATA_15", data15);
        parm.addData("DATA_16", data16);
        parm.addData("DATA_17", data17);
        parm.addData("DATA_18", data18);
        parm.addData("DATA_15_1", data1501);
        parm.addData("DATA_19", data19);
        parm.addData("DATA_20", data20);
        parm.addData("DATA_21", data21);
        parm.addData("DATA_22", data22);
        parm.addData("DATA_23", data23);
        parm.addData("DATA_24", data24);
        parm.addData("DATA_25", data25);
        parm.addData("DATA_26", data26);
        parm.addData("DATA_27", data27);
        parm.addData("DATA_28", data28);
        parm.addData("DATA_29", data29);
        parm.addData("DATA_30", data30);
        parm.addData("DATA_30", data30);
        double O_num = data10 + data15; // 用于计算治愈率
        if (O_num == 0) {
            parm.addData("DATA_31", ""); // 治愈率<31>
            parm.addData("DATA_32", ""); // 好转率<32>
            parm.addData("DATA_33", ""); // 病死率<33>
        } else {
            parm.addData("DATA_31", df.format((double) data11 / O_num * 100)); // 治愈率<31>
            parm.addData("DATA_32", df.format((double) data12 / O_num * 100)); // 好转率<32>
            parm.addData("DATA_33", df.format((double) data14 / O_num * 100)); // 病死率<33>
        }
        // 病床周转(次)
        if (t.getSelectedIndex() == 1) { // 月报
            if (data18 > 0 && data09 > 0) {
                parm.addData("DATA_34", df.format((double) data09 / ((double) data18 / MDays)));// 病床周转(次) = 期内出院人数/ 同期平均开放病床数
            } else parm.addData("DATA_34", "");
        } else { // 日报
            parm.addData("DATA_34", "");// 算法待定
        }
        parm.addData("DATA_35", ""); // 病床工作日<35> 算法待定
        if (t.getSelectedIndex() == 1) { // 月报
            // add by wanglong 20131126
            MDays = this.getDaysOfMonth(StringTool.getString(StringTool.getTimestamp(STA_DATE, "yyyyMM"), "yyyy/MM"));
            // add end
            double DATA_36 = 0;
            DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(data16, MDays, data18 / MDays);  // 病床使用率<36>
            parm.addData("DATA_36", df.format(DATA_36));
        } else { // 日报
            parm.addData("DATA_36", "");// 日报不统计
        }
        if (data09 == 0)
            parm.addData("DATA_37", "0");
        else 
            parm.addData("DATA_37", df.format((double) data21 / (double) data09)); // 患者平均住院日<37>
        parm.addData("DATA_38", ""); // 诊断符合率<38>
        if (data26 == 0)
            parm.addData("DATA_39", "0");
        else
            parm.addData("DATA_39", df.format((double) data27 / (double) data26 * 100)); // 无菌切口化脓率<39>
        if (data28 == 0)
            parm.addData("DATA_40", "100");
        else
            parm.addData("DATA_40", df.format((double) data29 / (double) data28 * 100)); // 危重病人抢救成功%<40>
        if (data16 == 0)
            parm.addData("DATA_41", "100");
        else
            parm.addData("DATA_41", df.format((double) data30 / (double) data16 * 100));// 陪人率%<41> = 陪人数/实有病人数
        if (data10 == 0 && data15 == 0)
            parm.addData("DATA_41_1", "");
        else if (data10 + data15 == 0)
            parm.addData("DATA_41_1", "100");
        else
            parm.addData("DATA_41_1", df.format((double) data23 / (double) (data10 + data15) * 100)); // 病理诊断符合率%<41_1>
        if (data10 == 0 && data15 == 0)
            parm.addData("DATA_41_2", "");
        else if (data10 + data15 == 0)
            parm.addData("DATA_41_2", "0");
        else
            parm.addData("DATA_41_2", df.format((double) data25 / (double) (data10 + data15) * 100)); // 术前术后诊断符合率%<41_2>
        parm.setCount(parm.getCount("DATA_01"));
        table.setParmValue(parm);
        
        TParm check = STAWorkLogTool.getInstance().checkNum(STA_DATE, Operator.getRegion());
        if (check.getErrCode() < 0) {
            this.messageBox(check.getErrText());
            return;
        }
        if (check.getCount("STA_DATE") > 0) { // 已存在该日期生成的数据
            if (check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                if (t.getSelectedIndex() == 0) { // 日报
                    isSubmitD = true; // 标识日报已经提交
                    this.setValue("Submit1", true);
                } else { // 月报
                    isSubmitM = true; // 标识 月报已经提交
                    this.setValue("Submit2", true);
                }
            } else {
                if (t.getSelectedIndex() == 0) { // 日报
                    isSubmitD = false; // 标识日报已经提交
                    this.setValue("Submit1", false);
                } else { // 月报
                    isSubmitM = false; // 标识 月报已经提交
                    this.setValue("Submit2", false);
                }
            }
        }
    }
    
    
    /**
     * 查询
     */
    public void onQuery() {
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table;
        String STA_DATE = "";
        if (t.getSelectedIndex() == 0) { // 日报
            table = (TTable) this.getComponent("TableDay");
            STA_DATE = this.getText("STA_DATE1").replace("/", ""); // 获取日期
            String dept = this.getValueString("DEPT_CODE1");
            this.TableBind(table, STA_DATE, Operator.getRegion(), dept);
        } else if (t.getSelectedIndex() == 1) { // 月报
            table = (TTable) this.getComponent("TableMouth");
            STA_DATE = this.getText("STA_DATE2").replace("/", "");
            String dept = this.getValueString("DEPT_CODE2");
            this.TableBind(table, STA_DATE, Operator.getRegion(), dept);
        }
    }

 
    /**
     * 打印
     */
    public void onPrint() {
        String STADATE = "";
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        if (t.getSelectedIndex() == 0) { // 日报页签
            STADATE = this.getText("STA_DATE1").replace("/", "");
            if (STADATE.trim().length() <= 0) {
                this.messageBox_("请选择日期!");
                return;
            }
            // if(!checkSubmit(STADATE))//查询各个住院部门是否提交“病区日志”报表，没有提交则返回
            // return;

            // //判断是否已经生成该日期的数据
            // TParm check = STAWorkLogTool.getInstance().checkNum(STADATE);
            // if (check.getCount("STA_DATE") > 0) { //已存在该日期生成的数据
            // if (!check.getValue("CONFIRM_FLG", 0).equals("Y")) {
            // this.messageBox_("数据没有提交，不能生成报表！");
            // return;
            // }
            // }
        } else if (t.getSelectedIndex() == 1) { // 月报页签
            STADATE = this.getText("STA_DATE2").replace("/", "");
            if (STADATE.trim().length() <= 0) {
                this.messageBox_("请选择日期!");
                return;
            }
            // 判断是否已经生成该月份的数据
            TParm check = STAWorkLogTool.getInstance().checkNum(STADATE, Operator.getRegion());
            if (check.getCount("STA_DATE") > 0) { // 已存在该日期生成的数据
                if (!check.getValue("CONFIRM_FLG", 0).equals("Y")) {
                    this.messageBox_("数据没有提交，不能生成报表！");
                    return;
                }
            }
        }
        TParm printData = this.getPrintDate(STADATE, Operator.getRegion());
        // System.out.println("打印数据==>:"+printData);
        TParm parm = new TParm();
        parm.setData("Title", Operator.getHospitalCHNFullName());
        parm.setData("Date", STADATE);
        parm.setData("Table1", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_02.jhw", parm);
    }
    


    private TParm getPrintDate(String STADATE, String regionCode) {
        DecimalFormat df = new DecimalFormat("0.00"); // 设定Double类型格式
        // 获取所有1，2，3级科室 作为报表的左边部门列
        TParm DeptList = STAWorkLogTool.getInstance().selectDeptList();
        // 获取所有STA_DAILY_02表数据（以四级科室为基础的数据）
        TParm reData = STAWorkLogTool.getInstance().selectSTA_DAILY_02(STADATE, regionCode);
        String DATE_S = "";
        String DATE_E = "";
        if (STADATE.length() == 6) {
            DATE_S = STADATE + "01"; // 每月第一天
            // 获取此月份的最后一天
            DATE_E = StringTool.getString(STATool.getInstance().getLastDayOfMonth(STADATE), "yyyyMMdd");
        } else if (STADATE.length() > 6) {
            DATE_S = STADATE;
            DATE_E = STADATE;
        }
        Map deptIPD = STADeptListTool.getInstance().getIPDDeptMap(Operator.getRegion());
        // System.out.println("-=--------deptIPD-----------"+deptIPD);
        TParm printData = new TParm();// 打印数据
        for (int i = 0; i < DeptList.getCount(); i++) {
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i);// 部门等级
            String d_CODE = DeptList.getValue("DEPT_CODE", i);// 中间部门CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i);// 中间部门名称
            int subIndex = 0;// 记录根据科室级别要截取CODE的长度
            if (d_LEVEL.equals("1")) { // 如果是一级科室 code长度为1
                subIndex = 1;
            } else if (d_LEVEL.equals("2")) { // 如果是二级科室 code长度为3
                subIndex = 3;
                d_DESC = " " + d_DESC;// 加入前方空格
            } else if (d_LEVEL.equals("3")) { // 如果是三级科室 code长度为5
                subIndex = 5;
                d_DESC = "  " + d_DESC;// 加入前方空格
            }
            /*
             * 定义变量 用来累加子部门的数值 初始值为-1，如果用于累加的数据为null那么变量始终为-1，
             * 那个插入该变量所属字段的时候就插入null
             */
            int DATA_01 = -1;
            int DATA_02 = -1;
            int DATA_03 = -1;
            int DATA_04 = -1;
            int DATA_05 = -1;
            int DATA_06 = -1;
            int DATA_07 = -1;
            int DATA_08 = -1;
            int DATA_08_1 = -1;
            int DATA_09 = -1;
            int DATA_10 = -1;
            int DATA_11 = -1;
            int DATA_12 = -1;
            int DATA_13 = -1;
            int DATA_14 = -1;
            int DATA_15 = -1;
            int DATA_15_1 = -1;
            int DATA_16 = -1;
            int DATA_17 = -1;
            int DATA_18 = -1;
            int DATA_19 = -1;
            int DATA_20 = -1;
            int DATA_21 = -1;
            int DATA_22 = -1;
            int DATA_23 = -1;
            int DATA_24 = -1;
            int DATA_25 = -1;
            int DATA_26 = -1;
            int DATA_27 = -1;
            double DATA_28 = -1;
            int DATA_29 = -1;
            int DATA_30 = -1;
            double DATA_31 = -1;
            double DATA_32 = -1;
            double DATA_33 = -1;
            double DATA_34 = -1;
            int DATA_35 = -1;
            double DATA_36 = -1;
            int DATA_37 = -1;
            double DATA_38 = -1;
            double DATA_39 = -1;
            double DATA_40 = -1;
            double DATA_41 = -1;
            double DATA_41_1 = -1;
            double DATA_41_2 = -1;
            String dept4 = "";// 记录三级科室下的四级科室
            int deptCount = 0;// 记录每个部门下的子部门，概率方面的数据需要取平均值
            int deptInpCount = 0;// 记录每个部门下的住院病区个数
            // 循环遍历数据 取出符合条件的部门的数据进行累加
            for (int j = 0; j < reData.getCount(); j++) {
                // 如果部门id截取了指定长度后 等于外层循环中的部门CODE那么就是外层循环的子部门，就进行累加
                if (reData.getValue("DEPT_CODE", j).substring(0, subIndex).equals(d_CODE)) {
                    if (deptIPD.get(reData.getValue("DEPT_CODE", j)) != null) {
                        dept4 += deptIPD.get(reData.getValue("DEPT_CODE", j)).toString() + ",";
                    }
                    DATA_01 = checkNull_i(DATA_01, reData.getValue("DATA_01", j));
                    DATA_02 = checkNull_i(DATA_02, reData.getValue("DATA_02", j));
                    DATA_03 = checkNull_i(DATA_03, reData.getValue("DATA_03", j));
                    DATA_04 = checkNull_i(DATA_04, reData.getValue("DATA_04", j));
                    DATA_05 = checkNull_i(DATA_05, reData.getValue("DATA_05", j));
                    DATA_06 = checkNull_i(DATA_06, reData.getValue("DATA_06", j));
                    DATA_07 = checkNull_i(DATA_07, reData.getValue("DATA_07", j));
                    DATA_08 = checkNull_i(DATA_08, reData.getValue("DATA_08", j));
                    DATA_08_1 = checkNull_i(DATA_08_1, reData.getValue("DATA_08_1", j));
                    DATA_09 = checkNull_i(DATA_09, reData.getValue("DATA_09", j));
                    DATA_10 = checkNull_i(DATA_10, reData.getValue("DATA_10", j));
                    DATA_11 = checkNull_i(DATA_11, reData.getValue("DATA_11", j));
                    DATA_12 = checkNull_i(DATA_12, reData.getValue("DATA_12", j));
                    DATA_13 = checkNull_i(DATA_13, reData.getValue("DATA_13", j));
                    DATA_14 = checkNull_i(DATA_14, reData.getValue("DATA_14", j));
                    DATA_15 = checkNull_i(DATA_15, reData.getValue("DATA_15", j));
                    DATA_15_1 = checkNull_i(DATA_15_1, reData.getValue("DATA_15_1", j));
                    DATA_16 = checkNull_i(DATA_16, reData.getValue("DATA_16", j));
                    DATA_17 = checkNull_i(DATA_17, reData.getValue("DATA_17", j));
                    DATA_18 = checkNull_i(DATA_18, reData.getValue("DATA_18", j));
                    DATA_19 = checkNull_i(DATA_19, reData.getValue("DATA_19", j));
                    DATA_20 = checkNull_i(DATA_20, reData.getValue("DATA_20", j));
                    DATA_21 = checkNull_i(DATA_21, reData.getValue("DATA_21", j));
                    DATA_22 = checkNull_i(DATA_22, reData.getValue("DATA_22", j));
                    DATA_23 = checkNull_i(DATA_23, reData.getValue("DATA_23", j));
                    DATA_24 = checkNull_i(DATA_24, reData.getValue("DATA_24", j));
                    DATA_25 = checkNull_i(DATA_25, reData.getValue("DATA_25", j));
                    DATA_26 = checkNull_i(DATA_26, reData.getValue("DATA_26", j));
                    DATA_27 = checkNull_i(DATA_27, reData.getValue("DATA_27", j));
                    DATA_28 = checkNull_d(DATA_28, reData.getValue("DATA_28", j));
                    DATA_29 = checkNull_i(DATA_29, reData.getValue("DATA_29", j));
                    DATA_30 = checkNull_i(DATA_30, reData.getValue("DATA_30", j));
                    DATA_35 = checkNull_i(DATA_35, reData.getValue("DATA_35", j));
                    DATA_38 = checkNull_d(DATA_38, reData.getValue("DATA_38", j));
                    deptCount++;// 累计共汇总的多少个4级部门的数据
                    if (STAWorkLogTool.getInstance().checkIPDDept(reData.getValue("DEPT_CODE", j))) {
                        deptInpCount++;
                    }
                }
            }
            if (DATA_10 + DATA_15 > 0) {
                DATA_31 = (double) DATA_11 / ((double) DATA_10 + (double) DATA_15) * 100; // 治愈率
                DATA_32 = (double) DATA_12 / ((double) DATA_10 + (double) DATA_15) * 100; // 好转率
                DATA_33 = (double) DATA_13 / ((double) DATA_10 + (double) DATA_15) * 100; // 病死率
            }
            if (DATA_19 > 0) {
                if ((DATA_10 + DATA_15) > 0)
                    DATA_34 = (double) (DATA_10 + DATA_15) / (double) DATA_19; // 病床周转次 = 出院人数(1~5)/平均开放病床数
            }
            
            if ("month".equals(resultType)) {// 月报
                DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(DATA_20, MDays, DATA_19);// 病床使用率 月统计有效
            }
            if (DATA_09 > 0) DATA_37 = DATA_21 / DATA_09; // 患者平均住院天数
            if (DATA_26 > 0) DATA_39 = (double) DATA_27 / (double) DATA_26 * 100; // 无菌切口化脓率
            if (DATA_28 > 0) DATA_40 = (double) DATA_29 / (double) DATA_28 * 100; // 危重病人抢救成功%
            if (DATA_16 > 0) DATA_41 = (double) DATA_30 / (double) DATA_16 * 100; // 陪人率
            if ((DATA_10 + DATA_15) > 0) {
                DATA_41_1 = (double) DATA_23 / ((double) DATA_10 + (double) DATA_15) * 100; // 病理诊断符合率%
                DATA_41_2 = (double) DATA_25 / ((double) DATA_10 + (double) DATA_15) * 100;
            }
            // 如果本科室是三级科室 那么转科数量需要重新提取
            // 因为同一三级科室下的四级科室之间的转科不算入三级科室的数据中(交大二院需求)
            if (d_LEVEL.equals("3")) {
                if (dept4.length() > 0) {
                    String[] d = dept4.substring(0, dept4.length() - 1).split(",");
                    DATA_08_1 = STAIn_03Tool.getInstance().getDept_TranInNum(d, DATE_S, DATE_E);
                    DATA_15_1 = STAIn_03Tool.getInstance().getDept_TranOutNum(d, DATE_S, DATE_E);
                }
            }
            printData.addData("DEPT_DESC", d_DESC);
            printData.addData("DATA_01", DATA_01 == -1 ? "" : DATA_01);
            printData.addData("DATA_02", DATA_02 == -1 ? "" : DATA_02);
            printData.addData("DATA_03", DATA_03 == -1 ? "" : DATA_03);
            printData.addData("DATA_04", DATA_04 == -1 ? "" : DATA_04);
            printData.addData("DATA_05", DATA_05 == -1 ? "" : DATA_05);
            printData.addData("DATA_06", DATA_06 == -1 ? "" : DATA_06);
            printData.addData("DATA_07", DATA_07 == -1 ? "" : DATA_07);
            printData.addData("DATA_08", DATA_08 == -1 ? "" : DATA_08);
            printData.addData("DATA_08_1", DATA_08_1 == -1 ? "" : DATA_08_1);
            printData.addData("DATA_09", DATA_09 == -1 ? "" : DATA_09);
            printData.addData("DATA_10", DATA_10 == -1 ? "" : DATA_10);
            printData.addData("DATA_11", DATA_11 == -1 ? "" : DATA_11);
            printData.addData("DATA_12", DATA_12 == -1 ? "" : DATA_12);
            printData.addData("DATA_13", DATA_13 == -1 ? "" : DATA_13);
            printData.addData("DATA_14", DATA_14 == -1 ? "" : DATA_14);
            printData.addData("DATA_15", DATA_15 == -1 ? "" : DATA_15);
            printData.addData("DATA_15_1", DATA_15_1 == -1 ? "" : DATA_15_1);
            printData.addData("DATA_16", DATA_16 == -1 ? "" : DATA_16);
            printData.addData("DATA_17", DATA_17 == -1 ? "" : DATA_17);
            printData.addData("DATA_18", DATA_18 == -1 ? "" : DATA_18);
            printData.addData("DATA_19", DATA_19 == -1 ? "" : DATA_19);
            printData.addData("DATA_20", DATA_20 == -1 ? "" : DATA_20);
            printData.addData("DATA_21", DATA_21 == -1 ? "" : DATA_21);
            printData.addData("DATA_22", DATA_22 == -1 ? "" : DATA_22);
            printData.addData("DATA_23", DATA_23 == -1 ? "" : DATA_23);
            printData.addData("DATA_24", DATA_24 == -1 ? "" : DATA_24);
            printData.addData("DATA_25", DATA_25 == -1 ? "" : DATA_25);
            printData.addData("DATA_26", DATA_26 == -1 ? "" : DATA_26);
            printData.addData("DATA_27", DATA_27 == -1 ? "" : DATA_27);
            printData.addData("DATA_28", DATA_28 == -1 ? "" : DATA_28);
            printData.addData("DATA_29", DATA_29 == -1 ? "" : DATA_29);
            printData.addData("DATA_30", DATA_30 == -1 ? "" : DATA_30);
            printData.addData("DATA_31", DATA_31 == -1 ? "" : df.format(DATA_31));
            printData.addData("DATA_32", DATA_32 == -1 ? "" : df.format(DATA_32));
            printData.addData("DATA_33", DATA_33 == -1 ? "" : df.format(DATA_33));
            printData.addData("DATA_34", DATA_34 == -1 ? "" : df.format(DATA_34));
            printData.addData("DATA_35", DATA_35 == -1 ? "" : DATA_35);
            printData.addData("DATA_36", DATA_36 == -1 ? "" : df.format(DATA_36));
            printData.addData("DATA_37", DATA_37 == -1 ? "" : DATA_37);
            printData.addData("DATA_38", DATA_38 == -1 ? "" : df.format(DATA_38));
            printData.addData("DATA_39", DATA_39 == -1 ? "" : df.format(DATA_39));
            printData.addData("DATA_40", DATA_40 == -1 ? "" : df.format(DATA_40));
            printData.addData("DATA_41", DATA_41 == -1 ? "" : df.format(DATA_41));
            printData.addData("DATA_41_1", DATA_41_1 == -1 ? "" : df.format(DATA_41_1));
            printData.addData("DATA_41_2", DATA_41_2 == -1 ? "" : df.format(DATA_41_2));
        }
        // System.out.println(""+printData);
        printData.setCount(DeptList.getCount());
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "DATA_01");
        printData.addData("SYSTEM", "COLUMNS", "DATA_02");
        printData.addData("SYSTEM", "COLUMNS", "DATA_03");
        printData.addData("SYSTEM", "COLUMNS", "DATA_04");
        printData.addData("SYSTEM", "COLUMNS", "DATA_05");
        printData.addData("SYSTEM", "COLUMNS", "DATA_06");
        printData.addData("SYSTEM", "COLUMNS", "DATA_07");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_09");
        printData.addData("SYSTEM", "COLUMNS", "DATA_10");
        printData.addData("SYSTEM", "COLUMNS", "DATA_11");
        printData.addData("SYSTEM", "COLUMNS", "DATA_12");
        printData.addData("SYSTEM", "COLUMNS", "DATA_13");
        printData.addData("SYSTEM", "COLUMNS", "DATA_14");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_16");
        printData.addData("SYSTEM", "COLUMNS", "DATA_17");
        printData.addData("SYSTEM", "COLUMNS", "DATA_18");
        printData.addData("SYSTEM", "COLUMNS", "DATA_19");
        printData.addData("SYSTEM", "COLUMNS", "DATA_20");
        printData.addData("SYSTEM", "COLUMNS", "DATA_21");
        printData.addData("SYSTEM", "COLUMNS", "DATA_22");
        printData.addData("SYSTEM", "COLUMNS", "DATA_23");
        printData.addData("SYSTEM", "COLUMNS", "DATA_24");
        printData.addData("SYSTEM", "COLUMNS", "DATA_25");
        printData.addData("SYSTEM", "COLUMNS", "DATA_26");
        printData.addData("SYSTEM", "COLUMNS", "DATA_27");
        printData.addData("SYSTEM", "COLUMNS", "DATA_28");
        printData.addData("SYSTEM", "COLUMNS", "DATA_29");
        printData.addData("SYSTEM", "COLUMNS", "DATA_30");
        printData.addData("SYSTEM", "COLUMNS", "DATA_31");
        printData.addData("SYSTEM", "COLUMNS", "DATA_32");
        printData.addData("SYSTEM", "COLUMNS", "DATA_33");
        printData.addData("SYSTEM", "COLUMNS", "DATA_34");
        printData.addData("SYSTEM", "COLUMNS", "DATA_35");
        printData.addData("SYSTEM", "COLUMNS", "DATA_36");
        printData.addData("SYSTEM", "COLUMNS", "DATA_37");
        printData.addData("SYSTEM", "COLUMNS", "DATA_38");
        printData.addData("SYSTEM", "COLUMNS", "DATA_39");
        printData.addData("SYSTEM", "COLUMNS", "DATA_40");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41_1");
        printData.addData("SYSTEM", "COLUMNS", "DATA_41_2");
        // System.out.println("----------printData--------------------"+printData);
        return printData;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("DEPT_CODE1;DEPT_CODE2;SubmitFlg;Submit1;Submit2");
        Timestamp time = SystemTool.getInstance().getDate();
        this.setValue("STA_DATE1", StringTool.rollDate(time, -1)); // 前一天
        this.setValue("STA_DATE2", STATool.getInstance().getLastMonth());  // 设置初始时间（上个月）
        this.callFunction("UI|TableDay|setParmValue", new TParm());//add by wanglong 20131126
        this.callFunction("UI|TableMouth|setParmValue", new TParm());
    }

    /**
     * 判断数据是否为空 并返回对应的变量值（整数）
     * @param c_num int 变量
     * @param data Object 数据值
     * @return int
     */
    private int checkNull_i(int c_num, String data) {
        // 如果数据不为null
        if (data.trim().length() > 0) {
            // 如果累加变量已经不是初始值-1
            if (c_num != -1) {
                c_num += Integer.valueOf(data);
                return c_num;
            }
            // 如果变量是 -1 那么返回数据的值
            if (c_num == -1) {
                return Integer.valueOf(data);
            }
        }
        return c_num;
    }

    /**
     * 判断数据是否为空 并返回对应的变量值（带小数）
     * @param c_num double
     * @param data Object
     * @return double
     */
    private double checkNull_d(double c_num, String data) {
        // 如果数据不为null
        if (data.trim().length() > 0) {
            // 如果累加变量已经不是初始值-1
            if (c_num != -1) {
                return c_num + Double.valueOf(data);
            }
            // 如果变量是 -1 那么返回数据的值
            if (c_num == -1) {
                return Double.valueOf(data);
            }
        }
        return c_num;
    }

    /**
     * 查询各部门的 “病区日志”是否提交
     * @param STADATE String
     * @return boolean
     */
    public boolean checkSubmit(String STADATE) {
        // 检核数据状态
        int reFlg =
                STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY", STADATE, Operator.getRegion());
        if (reFlg != 2) {// 没有提交门急诊日志
            this.messageBox_("没有提交门急诊日志，不能执行操作");
            return false;
        }
        TParm re = STAWorkLogTool.getInstance().checkSubmit(STADATE, Operator.getRegion());
        if (re.getCount() > 0) {
            String message = "以下部门还没有提交“病区日志”不能执行操作";
            for (int i = 0; i < re.getCount(); i++) {
                message += "\n" + re.getValue("DEPT_DESC", i);
            }
            this.messageBox_(message);
            return false;
        }
        return true;
    }

    /**
     * 计算某一月有多少天
     * @param month String 格式 yyyy/MM
     * @return int
     */
    private int getDaysOfMonth(String month) {
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
        try {
            rightNow.setTime(simpleDate.parse(month)); // 要计算你想要的月份
        } catch (Exception e) {
            e.printStackTrace();
        }
        int days = rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    /**
     * 修改事件监听
     * @param obj Object
     */
    public void onChangeTableValue() {
        DecimalFormat df = new DecimalFormat("0.00"); // 设定Double类型格式
        TTabbedPane t = (TTabbedPane) this.getComponent("tTabbedPane_0");
        TTable table = null;
        if (t.getSelectedIndex() == 0)
            table = (TTable) this.getComponent("TableDay");
        else if (t.getSelectedIndex() == 1)
            table = (TTable) this.getComponent("TableMouth");
        table.acceptText();
        // 合计 shibl 20210910 add
        int data01 = 0;// 诊疗人数总计合计<1>
        int data02 = 0;// 门诊人次数合计<2>
        int data03 = 0;// 急诊人次合计<3>
        int data04 = 0;// 急诊死亡人次合计<4>
        int data05 = 0;// 留观病人数合计<5>
        int data06 = 0;// 留观死亡人数合计<6>
        int data07 = 0;// 期初实有病人合计<7>
        int data08 = 0;// 入院人数合计<8>
        int data0801 = 0;// 他科转入合计<8-1>
        int data09 = 0;// 出院人数总计合计<9>
        int data10 = 0;// 病人数计合计<10>
        int data11 = 0;// 治愈合计<11>
        int data12 = 0;// 好转合计<12>
        int data13 = 0;// 未愈合计<13>
        int data14 = 0;// 死亡合计<14>
        int data15 = 0;// 其他合计<15>
        int data1501 = 0;// 转往他科人数合计<15_01>
        int data16 = 0;// 实有病人数合计<16>
        int data17 = 0;// 期末实有病人数<17>
        int data18 = 0;// 实际开放总床日数合计<18>
        int data19 = 0;// 平均开放床数合计<19>
        int data20 = 0;// 实际占床总数合计
        int data21 = 0;// 出院患者住院日数合计
        int data22 = 0;// 门诊诊断符数合计
        int data23 = 0;// 病历诊断符合数合计
        int data24 = 0;// 入院诊断符合数合计
        int data25 = 0;// 术前术后符合数合计
        int data26 = 0;// 无菌切口手术数合计
        int data27 = 0;// 无菌切口化脓数合计
        int data28 = 0;// 危重病人抢救数合计
        int data29 = 0;// 危重病人抢救成功合计
        int data30 = 0;// 陪 人数合计
        double data31 = 0;// 治愈率
        double data32 = 0;// 好转率
        double data33 = 0;// 病死率
        double data34 = 0;// 病床周转
        int data35 = 0;// 病床工作日
        double data36 = 0;// 病床使用率
        int data37 = 0;// 患者平均住院日<37>
        double data38 = 0;// 诊断符合率
        double data39 = 0;// 无菌切口化脓率<39>
        double data40 = 0;// 危重病人抢救成功%<40>
        double data41 = 0;// 陪人率%<41>
        double data4101 = 0;// 病理诊断符合率%<41_1>
        double data4102 = 0;// 术前术后诊断符合率%<41_2>
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount("DATA_01")-1; i++) {
            data01 += parm.getInt("DATA_01", i);
            data02 += parm.getInt("DATA_02", i);
            data03 += parm.getInt("DATA_03", i);
            data04 += parm.getInt("DATA_04", i);
            data05 += parm.getInt("DATA_05", i);
            data06 += parm.getInt("DATA_06", i);
            data07 += parm.getInt("DATA_07", i);
            data08 += parm.getInt("DATA_08", i);
            data0801 += parm.getInt("DATA_08_1", i);
            data09 += parm.getInt("DATA_09", i);
            data10 += parm.getInt("DATA_10", i);
            data11 += parm.getInt("DATA_11", i);
            data12 += parm.getInt("DATA_12", i);
            data13 += parm.getInt("DATA_13", i);
            data14 += parm.getInt("DATA_14", i);
            data15 += parm.getInt("DATA_15", i);
            data1501 += parm.getInt("DATA_15_1", i);
            data16 += parm.getInt("DATA_16", i);
            data17 += parm.getInt("DATA_17", i);
            data18 += parm.getInt("DATA_18", i);
            data19 += parm.getInt("DATA_19", i);
            data20 += parm.getInt("DATA_20", i);
            data21 += parm.getInt("DATA_21", i);
            data22 += parm.getInt("DATA_22", i);
            data23 += parm.getInt("DATA_23", i);
            data24 += parm.getInt("DATA_24", i);
            data25 += parm.getInt("DATA_25", i);
            data26 += parm.getInt("DATA_26", i);
            data27 += parm.getInt("DATA_27", i);
            data28 += parm.getInt("DATA_28", i);
            data29 += parm.getInt("DATA_29", i);
            data30 += parm.getInt("DATA_30", i);
        }
        parm.setData("DATA_01",parm.getCount()-1, data01);
        parm.setData("DATA_02",parm.getCount()-1, data02);
        parm.setData("DATA_03",parm.getCount()-1, data03);
        parm.setData("DATA_04",parm.getCount()-1, data04);
        parm.setData("DATA_05",parm.getCount()-1, data05);
        parm.setData("DATA_06",parm.getCount()-1, data06);
        parm.setData("DATA_07",parm.getCount()-1, data07);
        parm.setData("DATA_08",parm.getCount()-1, data08);
        parm.setData("DATA_08_1",parm.getCount()-1, data0801);
        parm.setData("DATA_09",parm.getCount()-1, data09);
        parm.setData("DATA_10",parm.getCount()-1, data10);
        parm.setData("DATA_11",parm.getCount()-1, data11);
        parm.setData("DATA_12",parm.getCount()-1, data12);
        parm.setData("DATA_13",parm.getCount()-1, data13);
        parm.setData("DATA_14",parm.getCount()-1, data14);
        parm.setData("DATA_15",parm.getCount()-1, data15);
        parm.setData("DATA_16",parm.getCount()-1, data16);
        parm.setData("DATA_17",parm.getCount()-1, data17);
        parm.setData("DATA_18",parm.getCount()-1, data18);
        parm.setData("DATA_15_1",parm.getCount()-1, data1501);
        parm.setData("DATA_19",parm.getCount()-1, data19);
        parm.setData("DATA_20",parm.getCount()-1, data20);
        parm.setData("DATA_21",parm.getCount()-1, data21);
        parm.setData("DATA_22",parm.getCount()-1, data22);
        parm.setData("DATA_23",parm.getCount()-1, data23);
        parm.setData("DATA_24",parm.getCount()-1, data24);
        parm.setData("DATA_25",parm.getCount()-1, data25);
        parm.setData("DATA_26",parm.getCount()-1, data26);
        parm.setData("DATA_27",parm.getCount()-1, data27);
        parm.setData("DATA_28",parm.getCount()-1, data28);
        parm.setData("DATA_29",parm.getCount()-1, data29);
        parm.setData("DATA_30",parm.getCount()-1, data30);
        parm.setData("DATA_30",parm.getCount()-1, data30);
        double O_num = data10 + data15; // 用于计算治愈率
        if (O_num == 0) {
            parm.setData("DATA_31",parm.getCount()-1, ""); // 治愈率<31>
            parm.setData("DATA_32",parm.getCount()-1, ""); // 好转率<32>
            parm.setData("DATA_33",parm.getCount()-1, ""); // 病死率<33>
        } else {
            parm.setData("DATA_31",parm.getCount()-1, df.format((double) data11 / O_num * 100)); // 治愈率<31>
            parm.setData("DATA_32",parm.getCount()-1, df.format((double) data12 / O_num * 100)); // 好转率<32>
            parm.setData("DATA_33",parm.getCount()-1, df.format((double) data14 / O_num * 100)); // 病死率<33>
        }
        // 病床周转(次)
        if (t.getSelectedIndex() == 1) { // 月报 病床周转(次)=期内出院人数/ 同期平均开放病床数
            if (data18 > 0 && data09 > 0) {
                parm.setData("DATA_34",parm.getCount()-1, df.format((double) data09 / ((double) data18 / MDays)));
            } else
                parm.setData("DATA_34",parm.getCount()-1, "");
        } else { // 日报
            parm.setData("DATA_34",parm.getCount()-1, "");// 算法待定
        }
        parm.setData("DATA_35",parm.getCount()-1, ""); // 病床工作日<35> 算法待定
        if (t.getSelectedIndex() == 1) { // 月报
            double DATA_36 = 0;
            DATA_36 = STAWorkLogTool.getInstance().countBED_USE_RATE(data16, MDays, data18 / MDays); // 病床使用率<36>
            parm.setData("DATA_36",parm.getCount()-1, df.format(DATA_36));
        } else { // 日报
            parm.setData("DATA_36",parm.getCount()-1, "");// 日报不统计
        }
        if (data09 == 0)
            parm.setData("DATA_37",parm.getCount()-1, "0");
        else
            parm.setData("DATA_37",parm.getCount()-1, df.format((double) data21 / (double) data09)); // 患者平均住院日<37>
        parm.setData("DATA_38",parm.getCount()-1, ""); // 诊断符合率<38>
        if (data26 == 0)
            parm.setData("DATA_39",parm.getCount()-1, "0");
        else
            parm.setData("DATA_39",parm.getCount()-1, df.format((double) data27 / (double) data26 * 100)); // 无菌切口化脓率<39>
        if (data28 == 0)
            parm.setData("DATA_40",parm.getCount()-1, "100");
        else
            parm.setData("DATA_40",parm.getCount()-1, df.format((double) data29 / (double) data28 * 100));// 危重病人抢救成功%<40>
        if (data16 == 0)
            parm.setData("DATA_41",parm.getCount()-1, "100");
        else
            parm.setData("DATA_41",parm.getCount()-1, df.format((double) data30 / (double) data16 * 100)); // 陪人率%<41> = 陪人数/实有病人数
        if (data10 == 0 && data15 == 0)
            parm.setData("DATA_41_1",parm.getCount()-1, "");
        else if (data10 + data15 == 0)
            parm.setData("DATA_41_1",parm.getCount()-1, "100");
        else
            parm.setData("DATA_41_1",parm.getCount()-1, df.format((double) data23 / (double) (data10 + data15) * 100)); // 病理诊断符合率%<41_1>
        if (data10 == 0 && data15 == 0)
            parm.setData("DATA_41_2",parm.getCount()-1, "");
        else if (data10 + data15 == 0)
            parm.setData("DATA_41_2",parm.getCount()-1, "0");
        else
            parm.setData("DATA_41_2",parm.getCount()-1, df.format((double) data25 / (double) (data10 + data15) * 100)); // 术前术后诊断符合率%<41_2>
        table.setParmValue(parm);
    }
}
