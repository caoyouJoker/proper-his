package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 在院患者医疗费用报表控制类</p>
 *
 * <p>Description: 在院患者医疗费用报表控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.12
 * @version 1.0
 */
public class BILInHospPatDetailFeeControl
    extends TControl {
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("CASH_CODE", Operator.getID());
        setValue("REGION_CODE", Operator.getRegion());
        this.callFunction("UI|Table|removeRowAll");
    }

    /**
     * 打印
     */
    public void onPrint() {
        print();
    }

    /**
     * 调用报表打印预览界面
     */
    private void print() {
        TTable table = (TTable)this.getComponent("Table");
        int row = table.getRowCount();
        if (row < 1) {
            this.messageBox_("先查询数据!");
            return;
        }
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyy/MM/dd");
        TParm parm = new TParm();
        parm.setData("Title", "在院患者结转费用明细表");
        parm.setData("S_DATE", sDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", Operator.getName());
        parm.setData("OPT_DATE", sysDate);
        parm.setData("exeDeptTable", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\BILInHospPatDetailFee.jhw",
                             parm);

    }

    /**
     * 整理打印数据
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm selParm = new TParm();
        String deptWhere = "";
        if (getValue("DEPT_CODE").toString().length() != 0)
            deptWhere = " AND A.EXE_DEPT_CODE = '" + getValue("DEPT_CODE") +
                "'  ";
        String sql =
            "   SELECT A.EXE_DEPT_CODE, SUM (A.TOT_AMT)  AS TOT_AMT, A.REXP_CODE, B.DEPT_ABS_DESC" +
            "     FROM IBS_ORDD A, SYS_DEPT B " +
            "    WHERE A.BILL_DATE BETWEEN TO_DATE ('" + startTime + "000000" +
            "', 'yyyyMMddhh24miss') " +
            "                      AND TO_DATE ('" + endTime + "235959" +
            "', 'yyyyMMddhh24miss') " +
            deptWhere +
            "      AND A.EXE_DEPT_CODE = B.DEPT_CODE " +
            " GROUP BY A.EXE_DEPT_CODE, A.REXP_CODE, B.DEPT_ABS_DESC " +
            " ORDER BY A.EXE_DEPT_CODE ";
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("EXE_DEPT_CODE") < 1) {
            //查无数据
            this.messageBox_("E0008");
            this.initPage();
            return selParm;
        }
        BILRecpChargeForPrint endData = new BILRecpChargeForPrint();
        TParm endParm = endData.getValue(selParm);
        this.callFunction("UI|Table|setParmValue", endParm);
        return endParm;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "在院患者结转费用明细表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();

    }

}
