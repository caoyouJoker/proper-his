package com.javahis.ui.aci;

import java.sql.Timestamp;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import jdo.aci.ACIBadEventTool;
import jdo.sys.SystemTool;

/**
 * <p> Title: 不良事件复合统计 </p>
 * 
 * <p> Description: 不良事件复合统计 </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author WangLong 2012.12.24
 * @version 1.0
 */
public class ACIBadEventComplexStatisticsControl extends TControl {
    TTable table;// 表格
    TRadioButton year;// 年（单选按钮）
    TRadioButton month;// 月（单选按钮）
    TRadioButton day;// 日（单选按钮）
    TTextFormat date;// 时间
    TRadioButton deptAndSac;// 上报科室/SAC分级（单选按钮）
    TRadioButton deptAndType;// 上报科室/事件分类（单选按钮）
    TRadioButton sacAndType;// SAC分级/事件分类（单选按钮）
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
        year = (TRadioButton) this.getComponent("YEAR");
        month = (TRadioButton) this.getComponent("MONTH");
        day = (TRadioButton) this.getComponent("DAY");
        date = (TTextFormat) this.getComponent("DATE");
        deptAndSac = (TRadioButton) this.getComponent("DEPT_AND_SAC");
        deptAndType = (TRadioButton) this.getComponent("DEPT_AND_TYPE");
        sacAndType = (TRadioButton) this.getComponent("SAC_AND_TYPE");
        level = (TComboBox) this.getComponent("LEVEL");
        initUI();
    }

    /**
     * 初始化界面信息
     */
    public void initUI() {
        day.setSelected(true);
        onChooseDay();
        deptAndSac.setSelected(true);
        onChooseDeptAndSac();
        level.setSelectedIndex(0);
        onChooseLevel();
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("DATE_TYPE", dateType);
        Timestamp startDate = (Timestamp) this.getValue("DATE");
        if(dateType.equals("YEAR")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy"));
        } else if(dateType.equals("MONTH")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy/MM"));
        } else if(dateType.equals("DAY")) {
            parm.setData("DATE", StringTool.getString(startDate, "yyyy/MM/dd"));
        }
        parm.setData("LEVEL", typeLevel);
        //add by wukai on 20160908 start 添加 责任科室 和 上报部门查询条件
        if(!StringUtils.isEmpty(this.getText("DEPT_IN_CHARGE"))) {
        	parm.setData("DEPT_IN_CHARGE", this.getValueString("DEPT_IN_CHARGE"));
        }
        if(!StringUtils.isEmpty(this.getText("REPORT_SECTION"))) {
        	 parm.setData("REPORT_SECTION", this.getValueString("REPORT_SECTION"));
        }
        //System.out.println("parm :::::::::::  " + parm);
        //add by wukai on 20160908 end 添加 责任科室 和 上报部门查询条件
        TParm result = new TParm();
        if(queryType.equals("DEPT_AND_SAC")) {// 按“上报科室/SAC分级”
            result = ACIBadEventTool.getInstance().selectStatisticByDeptAndSac(parm);
        } else if(queryType.equals("DEPT_AND_TYPE")) {// 按“上报科室/事件分类”
            result = ACIBadEventTool.getInstance().selectStatisticByDeptAndType(parm);
        } else if(queryType.equals("SAC_AND_TYPE")) {// 按“SAC分级/事件分类”
            result = ACIBadEventTool.getInstance().selectStatisticBySacAndType(parm);
        }
        if(result.getErrCode() < 0) {
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
        Timestamp startDate = (Timestamp) this.getValue("DATE");
        String dateStr = "";
        if (dateType.equals("YEAR")) {
            dateStr = StringTool.getString(startDate, "yyyy");
        } else if (dateType.equals("MONTH")) {
            dateStr = StringTool.getString(startDate, "yyyy-MM");
        } else if (dateType.equals("DAY")) {
            dateStr = StringTool.getString(startDate, "yyyy-MM-dd");
        }
        if (table.getRowCount() > 0) ExportExcelUtil.getInstance().exportExcel(table, "不良事件复合统计");
    }

    /**
     * 清空
     */
    public void onClear() {
        callFunction("UI|TABLE|setParmValue", new TParm());
        this.clearValue("DEPT_IN_CHARGE;REPORT_SECTION");
        initUI();
    }

    /**
     * 选择“年”
     */
    public void onChooseYear() {
        dateType = "YEAR";
        date.setFormat("yyyy");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * 选择“月”
     */
    public void onChooseMonth() {
        dateType = "MONTH";
        date.setFormat("yyyy/MM");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * 选择“日”
     */
    public void onChooseDay() {
        dateType = "DAY";
        date.setFormat("yyyy/MM/dd");
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("DATE", now);
    }

    /**
     * 选择“上报科室/SAC分级”
     */
    public void onChooseDeptAndSac() {
        queryType = "DEPT_AND_SAC";
        level.setEnabled(false);
    }

    /**
     * 选择“上报科室/事件分类”
     */
    public void onChooseDeptAndType() {
        queryType = "DEPT_AND_TYPE";
        level.setEnabled(true);
    }

    /**
     * 选择“SAC分级/事件分类”
     */
    public void onChooseSacAndType() {
        queryType = "SAC_AND_TYPE";
        level.setEnabled(true);
    }
    
    /**
     * 选择事件分类级数
     */
    public void onChooseLevel() {
        typeLevel = this.getValueString("LEVEL");
    }
}
