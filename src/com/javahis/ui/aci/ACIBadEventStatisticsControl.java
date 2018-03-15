package com.javahis.ui.aci;

import java.sql.Timestamp;
import jdo.aci.ACIBadEventTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.system.textFormat.TextFormatBadEventType;
/**
 * <p> Title: 不良事件单项统计 </p>
 * 
 * <p> Description: 不良事件单项统计 </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author WangLong 2012.12.24
 * @version 1.0
 */
public class ACIBadEventStatisticsControl
        extends TControl {

    TTable table;// 表格
    TRadioButton day;// 日（单选按钮）
    TRadioButton count;// 例数（单选按钮）
    TTextFormat startDate;// 开始时间（下拉框）
    TTextFormat endDate;// 结束时间（下拉框）
    TTextFormat deptCode;// 上报科室（下拉框）
    TTextFormat sacClass;// SAC分级（下拉框）
    TextFormatBadEventType eventType;// 事件分类（下拉框）
    TComboBox level;// 事件分类级数（下拉框）
    private String dateType = "";// 日期种类
    private String queryType = "";// 查询种类
    private String typeLevel = "";// 分类级数
    
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        day = (TRadioButton) this.getComponent("DAY");
        count = (TRadioButton) this.getComponent("NUM");
        startDate = (TTextFormat) this.getComponent("START_DATE");
        endDate = (TTextFormat) this.getComponent("END_DATE");
        deptCode = (TTextFormat) this.getComponent("REPORT_DEPT");
        sacClass = (TTextFormat) this.getComponent("SAC_CLASS");
        eventType = (TextFormatBadEventType) this.getComponent("EVENT_TYPE");
        level = (TComboBox) this.getComponent("LEVEL");
        initUI();
    }

    /**
     * 初始化界面信息
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        count.setSelected(true);
        onChooseCount();
        level.setSelectedIndex(0);
        onChooseLevel();
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("DATE_TYPE", dateType);
        //add by wukai on 20160905 添加责任科室 和 上报部门 条件
        if(!StringUtils.isEmpty(this.getText("DEPT_IN_CHARGE"))) {
        	parm.setData("DEPT_IN_CHARGE", this.getValueString("DEPT_IN_CHARGE"));
        }
        if(!StringUtils.isEmpty(this.getText("REPORT_SECTION"))) {
        	 parm.setData("REPORT_SECTION", this.getValueString("REPORT_SECTION"));
        }
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        if (dateType.equals("YEAR")) {
            parm.setData("START_DATE", StringTool.getString(startDate, "yyyy"));
            parm.setData("END_DATE", StringTool.getString(endDate, "yyyy"));
        } else if (dateType.equals("MONTH")) {
            parm.setData("START_DATE", StringTool.getString(startDate, "yyyy/MM"));
            parm.setData("END_DATE", StringTool.getString(endDate, "yyyy/MM"));
        } else if (dateType.equals("DAY")) {
            parm.setData("START_DATE", StringTool.getString(startDate, "yyyy/MM/dd"));
            parm.setData("END_DATE", StringTool.getString(endDate, "yyyy/MM/dd"));
        }
        parm.setData("LEVEL", typeLevel);
        TParm result = new TParm();
        if (queryType.equals("NUM")) {// 按例数
            result = ACIBadEventTool.getInstance().selectStatisticByCount(parm);
        } else if (queryType.equals("DEPT")) {// 按上报科室
            if (!this.getValueString("REPORT_DEPT").equals("")) {
                parm.setData("REPORT_DEPT", this.getValue("REPORT_DEPT"));// 上报科室
            }
            result = ACIBadEventTool.getInstance().selectStatisticByDept(parm);
        } else if (queryType.equals("SAC")) {// 按SAC分级
            if (!this.getValueString("SAC_CLASS").equals("")) {
                parm.setData("SAC_CLASS", this.getValue("SAC_CLASS"));// SAC分级
            }
            result = ACIBadEventTool.getInstance().selectStatisticBySac(parm);
        } else if (queryType.equals("TYPE")) {// 按事件分类
            if (!this.getValueString("EVENT_TYPE").equals("")) {
                parm.setData("EVENT_TYPE", this.getValue("EVENT_TYPE"));// 不良事件分类
            }
            result = ACIBadEventTool.getInstance().selectStatisticByType(parm);
        }
        if (result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
        }
        table.setDSValue();
        table.setHeader(result.getValue("TABLE_HEADER"));
        table.setParmMap(result.getValue("TABLE_PARMMAP"));
        table.setParmValue(result);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        String startDateStr = "";
        String endDateStr = "";
        if (dateType.equals("YEAR")) {
            startDateStr = StringTool.getString(startDate, "yyyy");
            endDateStr = StringTool.getString(endDate, "yyyy");
        } else if (dateType.equals("MONTH")) {
            startDateStr = StringTool.getString(startDate, "yyyy-MM");
            endDateStr = StringTool.getString(endDate, "yyyy-MM");
        } else if (dateType.equals("DAY")) {
            startDateStr = StringTool.getString(startDate, "yyyy-MM-dd");
            endDateStr = StringTool.getString(endDate, "yyyy-MM-dd");
        }
        if (table.getRowCount() > 0) ExportExcelUtil.getInstance().exportExcel(table,
        /* startDateStr + "~" + endDateStr + */"不良事件统计");
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("DEPT_CODE;SAC_CLASS;EVENT_TYPE;DEPT_IN_CHARGE;REPORT_SECTION");
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }

    /**
     * 选择“年”
     */
    public void onChooseYear() {
        dateType = "YEAR";
        startDate.setFormat("yyyy");
        endDate.setFormat("yyyy");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("START_DATE", yesterday);
        setValue("END_DATE", today);
    }

    /**
     * 选择“月”
     */
    public void onChooseMonth() {
        dateType = "MONTH";
        startDate.setFormat("yyyy/MM");
        endDate.setFormat("yyyy/MM");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("START_DATE", yesterday);
        setValue("END_DATE", today);
    }

    /**
     * 选择“日”
     */
    public void onChooseDay() {
        dateType = "DAY";
        startDate.setFormat("yyyy/MM/dd");
        endDate.setFormat("yyyy/MM/dd");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        Timestamp today = SystemTool.getInstance().getDate();
        setValue("START_DATE", yesterday);
        setValue("END_DATE", today);
    }

    /**
     * 选择“例数”
     */
    public void onChooseCount() {
        queryType = "NUM";
        deptCode.setEnabled(false);
        sacClass.setEnabled(false);
        eventType.setEnabled(false);
        level.setEnabled(false);
    }

    /**
     * 选择“上报科室”
     */
    public void onChooseDept() {
        queryType = "DEPT";
        deptCode.setEnabled(true);
        sacClass.setEnabled(false);
        eventType.setEnabled(false);
        level.setEnabled(false);
    }

    /**
     * 选择“SAC分级”
     */
    public void onChooseSac() {
        queryType = "SAC";
        deptCode.setEnabled(false);
        sacClass.setEnabled(true);
        eventType.setEnabled(false);
        level.setEnabled(false);
    }

    /**
     * 选择“事件分类”
     */
    public void onChooseType() {
        queryType = "TYPE";
        deptCode.setEnabled(false);
        sacClass.setEnabled(false);
        eventType.setEnabled(true);
        level.setEnabled(true);
    }
    
    /**
     * 选择事件分类级数
     */
    public void onChooseLevel() {
        typeLevel = this.getValueString("LEVEL");
        eventType.setTypeLevel(typeLevel);
        eventType.onQuery();
    }
}
