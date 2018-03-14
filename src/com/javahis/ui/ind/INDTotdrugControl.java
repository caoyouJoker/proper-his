package com.javahis.ui.ind;

import java.sql.*;
import java.text.*;
import java.util.Date;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.ui.*;
import com.dongyang.util.*;
import com.javahis.util.*;
import jdo.ind.*;
import jdo.sys.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class INDTotdrugControl
    extends TControl {
    public INDTotdrugControl() {

    }
    //占比
    String Percent;
    //表格
    private TTable table;
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        //起日
        Timestamp startDate = StringTool.getTimestamp(new Date());
        //迄日
        Timestamp endDate = StringTool.rollDate(startDate, 1);
        //开始日期
        this.setValue("START_DATE", startDate);
        //结束时间
        this.setValue("END_DATE", endDate);
        table = (TTable)this.getComponent("DrugTABLE");
        this.callFunction("UI|DrugTABLE|removeRowAll");
        selectcode();
    }

    public void selectcode() {

        if ("Y".equals(this.getValueString("RadioButton0"))) {
            this.callFunction("UI|Drug_class|setEnabled", true);
            this.callFunction("UI|PHA_CLASS|setEnabled", false);
            this.callFunction("UI|ROUTE_CODE|setEnabled", false);
            this.setValue("PHA_CLASS", "");
            this.setValue("ROUTE_CODE", "");

        }
        if ("Y".equals(this.getValueString("RadioButton1"))) {
            this.callFunction("UI|PHA_CLASS|setEnabled", true);
            this.callFunction("UI|Drug_class|setEnabled", false);
            this.callFunction("UI|ROUTE_CODE|setEnabled", false);
            this.setValue("Drug_class", "");
            this.setValue("ROUTE_CODE", "");

        }
        if ("Y".equals(this.getValueString("RadioButton2"))) {
            this.callFunction("UI|ROUTE_CODE|setEnabled", true);
            this.callFunction("UI|PHA_CLASS|setEnabled", false);
            this.callFunction("UI|Drug_class|setEnabled", false);
            this.setValue("Drug_class", "");
            this.setValue("PHA_CLASS", "");
        }
    }

    /**
     * 打印
     */
    public void onPrint() {
        if (this.table.getRowCount() <= 0) {
            this.messageBox("没有要打印的数据");
            return;
        }
        TParm prtParm = new TParm();
        //表头
        prtParm.setData("TITLE", "TEXT", "药品销售月统计报表");
        //起日
        prtParm.setData("START_DATE", "TEXT", "日期：" +
                        StringTool.getString(StringTool.getTimestamp(new Date()),
                                             "yyyy年MM月dd日") + "至");
        //截止
        prtParm.setData("END_DATE", "TEXT",
                        StringTool.getString(StringTool.rollDate(StringTool.
            getTimestamp(new Date()), 1),
                                             "yyyy年MM月dd日"));
        //占比
        prtParm.setData("PERCENT", "TEXT", "占比：" + Percent);
        //科室
        String dept = DeptTool.getInstance().getDescByCode(this.getValueString(
            "DEPT_CODE"));
        if (!dept.equals("") && dept.length() > 0) {
            prtParm.setData("DEPT_CODE", "TEXT", "科室:" + dept);
        }
        TParm prtTableParm = this.getSelectTParm();
        prtTableParm.setCount(prtTableParm.getCount("ORDER_DESC"));
        prtTableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT");
        prtTableParm.addData("SYSTEM", "COLUMNS", "PRICE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "QTY");
        prtTableParm.addData("SYSTEM", "COLUMNS", "OUT_AMT");
        prtParm.setData("DRUGTABLE", prtTableParm.getData());
        //表尾
        prtParm.setData("CREATEUSER", "TEXT", "制表人：" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\ind\\INDDrugTotprint.jhw",
                             prtParm);
    }

    public void onQuery() {
        if (!checkData()) {
            return;
        }
        TParm parm = this.getSelectTParm();
        table.setParmValue(parm);
    }

    public TParm getSelectTParm() {
        TParm tableparm = new TParm();
        //得到时间查询条件
        String startdate = this.getValueString("START_DATE");
        startdate = startdate.substring(0, 4) + startdate.substring(5, 7) +
            startdate.substring(8, 10);
        String enddate = this.getValueString("END_DATE");
        enddate = enddate.substring(0, 4) + enddate.substring(5, 7) +
            enddate.substring(8, 10);
        String drug_class = this.getValueString("Drug_class");
        String ROUTE = this.getValueString("ROUTE_CODE");
        String deptcode = this.getValueString("DEPT_CODE");
        String pha_class = this.getValueString("PHA_CLASS");
        TParm inparm = new TParm();
        inparm.setData("START_DATE", startdate);
        inparm.setData("END_DATE", enddate);
        //药品分类
        if (!drug_class.equals("") && drug_class.length() > 0) {
            inparm.setData("SYS_GRUG_CLASS", drug_class);
        }
        //用药方式
        if (!ROUTE.equals("") && ROUTE.length() > 0) {
            inparm.setData("ROUTE_CODE", ROUTE);
        }
        //科室
        if (!deptcode.equals("") && deptcode.length() > 0) {
            inparm.setData("ORG_CODE", deptcode);
        }
        //院区
        if (!Operator.getRegion().equals("") &&Operator.getRegion().length() > 0) {
            inparm.setData("REGION_CODE", Operator.getRegion());
        }
        String sql = " SELECT SUM(OUT_AMT) AS TOT FROM IND_DDSTOCK";
        TParm tot = new TParm(this.getDBTool().select(sql));
        double totnumber = tot.getDouble("TOT", 0);
        tableparm = IndDDStockTool.getInstance().onQueryBildrug(inparm);
//        System.out.println("tableparm==="+tableparm);
        TParm prtTableParm = new TParm();
        //处理查询出来的parm
        //统计信息begin
        double number = 0;
        DecimalFormat sf= new DecimalFormat("######0.00");
        DecimalFormat ff= new DecimalFormat("######0.0000");
        boolean pha_flg=true;
        for (int i = 0; i < tableparm.getCount(); i++) {
            TParm rowParm = tableparm.getRow(i);
            String pha=rowParm.getValue("SYS_PHA_CLASS");
            if(!pha_class.equals("")&& pha_class.length()>0)
            {
               pha_flg=SYSFeeTool.getInstance().getType(pha_class,pha);
            }
            if(pha_flg)
            {
                prtTableParm.addData("ORDER_DESC",
                                     rowParm.getValue("ORDER_DESC"));
                prtTableParm.addData("SPECIFICATION",
                                     rowParm.getValue("SPECIFICATION"));
                prtTableParm.addData("QTY",
                                     rowParm.getValue("QTY"));
                prtTableParm.addData("UNIT",
                                     rowParm.getValue("UNIT"));
                prtTableParm.addData("PRICE",
                                     ff.format(StringTool.round(rowParm.
                    getDouble("PRICE"), 4)));
                prtTableParm.addData("OUT_AMT",
                                     sf.format(StringTool.round(rowParm.
                    getDouble("OUT_AMT"), 2)));
                number += rowParm.getDouble("OUT_AMT");
            }
        }
         //统计信息end
        String percent =sf.format(StringTool.round(number / totnumber,4)*100);
        Percent = percent + "%" + "";
        this.setValue("PERCENT", Percent);
        prtTableParm.addData("ORDER_DESC", "合计：");
        prtTableParm.addData("SPECIFICATION","");
        prtTableParm.addData("QTY","");
        prtTableParm.addData("UNIT","");
        prtTableParm.addData("PRICE","");
        prtTableParm.addData("OUT_AMT",sf.format(StringTool.round(number, 2)));
        return prtTableParm;
    }

    public void onClear() {
        this.setValue("DEPT_CODE", "");
        this.setValue("Drug_class", "");
        this.setValue("PHA_CLASS", "");
        this.setValue("ROUTE_CODE", "");
    }

    /**
     * 导出Excel
     */
    public void onExport() {
        if (table.getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(table, "药物销售月统计细表");
        }
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkData() {
        String start = this.getValueString("START_DATE");
        if (start == null || start.length() <= 0) {
            this.messageBox("开始时间不能为空");
            return false;
        }
        String end = this.getValueString("END_DATE");
        if (end == null || end.length() <= 0) {
            this.messageBox("结束时间不能为空");
            return false;
        }

        return true;
    }

    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
