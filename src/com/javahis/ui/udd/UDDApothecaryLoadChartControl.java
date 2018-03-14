package com.javahis.ui.udd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.udd.UddApothecaryLoadChartTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 住院药房药师处方签工作量统计报表主档</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UDDApothecaryLoadChartControl
    extends TControl {

    public TTable table;

    public UDDApothecaryLoadChartControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table = this.getTable("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("REGION_CODE", Operator.getRegion());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("TYPE"))) {
            this.messageBox("请选择查询类型");
            return;
        }
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        if (!"".equals(this.getValueString("TYPE"))) {
            parm.setData("TYPE", this.getValueString("TYPE"));
        }
        String start_date = this.getValueString("START_DATE");
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10);
        String end_date = this.getValueString("END_DATE");
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
            end_date.substring(8, 10);
        parm.setData("START_DATE", start_date + "000000");
        parm.setData("END_DATE", end_date + "235959");

        TParm result = UddApothecaryLoadChartTool.getInstance().onQuery(parm);
        if (result == null || result.getCount("QDATE") <= 0) {
            this.messageBox("没有查询数据");
            onClear();
            return;
        }
        sumAMT(result);
        table.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "TOT_AMT;ORG_CODE;TYPE";
        this.clearValue(clearStr);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("REGION_CODE", Operator.getRegion());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("START_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        table.setParmValue(new TParm());
    }

    /**
     * 打印方法
     */
    public void onPrint() {

    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        String type = "";
        if ("CHECK".equals(this.getValueString("TYPE"))) {
            type = "审核";
        }
        else if ("DOSAGE".equals(this.getValueString("TYPE"))) {
            type = "配药";
        }
        else if ("DISPENSE".equals(this.getValueString("TYPE"))) {
            type = "发药";
        }
        else {
            type = "退药";
        }

        ExportExcelUtil.getInstance().exportExcel(table,
                                                  "住院药师处方工作量统计表(" + type + ")");
    }

    /**
     * 计算总金额
     * @param parm TParm
     */
    private void sumAMT(TParm parm) {
        double sum_amt = 0;
        for (int i = 0; i < parm.getCount("QDATE"); i++) {
            sum_amt += parm.getDouble("SUM_AMT", i);
        }
        this.setValue("TOT_AMT", StringTool.round(sum_amt, 2));
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
