package com.javahis.ui.reg;

import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.util.Vector;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 医生收入统计报表</p>
 *
 * <p>Description: 医生收入统计报表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.10.09
 * @version 1.0
 */
public class REGDocFeeListControl
    extends TControl {
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        initPage();
    }

    /**
     * 行双击事件
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        if (row < 0)
            return;
        onDetial();
    }

    /**
     * 初始化界面
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("REGION_CODE", Operator.getRegion());
        setValue("DR_CODE", "");
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
            this.messageBox("先查询数据!");
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
        parm.setData("Title", "医生工作量统计报表");
        parm.setData("S_DATE", sDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", Operator.getName());
        parm.setData("OPT_DATE", sysDate);
        parm.setData("docWorkloadListTable", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGDocWorkloadList.jhw",
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
        String admTypeWhere = "";
        if (getValue("ADM_TYPE") != null) {
            if (getValue("ADM_TYPE").toString().length() != 0)
                admTypeWhere = " AND A.ADM_TYPE = '" + getValue("ADM_TYPE") +
                    "'  ";
        }
        String drCodeWhere = "";
        if (getValue("DR_CODE") != null) {
            if (getValue("DR_CODE").toString().length() != 0)
                drCodeWhere = " AND A.DR_CODE = '" + getValue("DR_CODE") +
                    "'  ";
        }
        String sql =
            " SELECT A.ADM_DATE, A.SESSION_CODE, A.CLINICTYPE_CODE, A.REALDEPT_CODE," +
            "        A.REALDR_CODE, C.SESSION_DESC, D.DEPT_ABS_DESC, E.USER_NAME," +
            "        F.CLINICTYPE_DESC, SUM (B.REG_FEE_REAL) REG_FEE," +
            "        SUM (B.CLINIC_FEE_REAL) CLINIC_FEE, SUM (B.AR_AMT) AR_AMT," +
            "        (COUNT (B.CASE_NO) - COUNT (B.RESET_RECEIPT_NO)) REG_COUNT," +
            "        COUNT (B.RESET_RECEIPT_NO) UN_REG_COUNT " +
            "   FROM REG_PATADM A, BIL_REG_RECP B,REG_SESSION C,SYS_DEPT D,SYS_OPERATOR E,REG_CLINICTYPE F " +
            "  WHERE A.CASE_NO = B.CASE_NO(+) " +
            "    AND A.SESSION_CODE = C.SESSION_CODE " +
            "    AND A.REALDEPT_CODE = D.DEPT_CODE " +
            "    AND A.REALDR_CODE = E.USER_ID " +
            "    AND A.CLINICTYPE_CODE = F.CLINICTYPE_CODE " +
            admTypeWhere +
            drCodeWhere +
            "    AND A.ADM_DATE BETWEEN TO_DATE('" + startTime +
            "000000" + "','yyyyMMddHH24miss') " +
            "            AND TO_DATE('" + endTime + "235959" +
            "','yyyyMMddHH24miss') " +
            "  GROUP BY A.ADM_DATE, A.SESSION_CODE,A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE,C.SESSION_DESC," +
            "           D.DEPT_ABS_DESC,E.USER_NAME,F.CLINICTYPE_DESC ";
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("REALDR_CODE") < 1) {
            this.messageBox("查无数据");
            this.initPage();
            return selParm;
        }
        this.callFunction("UI|Table|setParmValue", selParm);
        TParm endDate = new TParm();
        int count = selParm.getCount("REALDR_CODE");
        //ADM_DATE;SESSION_DESC;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;REG_FEE;CLINIC_FEE;AR_AMT;REG_COUNT;UN_REG_COUNT
        for (int i = 0; i < count; i++) {
            Timestamp orderDate = selParm.getTimestamp("ORDER_DATE", i);
            String orderDateStr = StringTool.getString(orderDate, "yyyy/MM/dd");
            String sessionDesc = selParm.getValue("SESSION_DESC", i);
            String clinicTypeDesc = selParm.getValue("CLINICTYPE_DESC", i);
            String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);
            String userName = selParm.getValue("USER_NAME", i);
            double regFee = selParm.getDouble("REG_FEE", i);
            double clinicFee = selParm.getDouble("CLINIC_FEE", i);
            double arAmt = selParm.getDouble("AR_AMT", i);
            int regCount = selParm.getInt("REG_COUNT", i);
            int unRegCount = selParm.getInt("UN_REG_COUNT", i);
            endDate.addData("ORDER_DATE", orderDateStr);
            endDate.addData("SESSION_DESC", sessionDesc);
            endDate.addData("CLINICTYPE_DESC", clinicTypeDesc);
            endDate.addData("DEPT_ABS_DESC", deptDesc);
            endDate.addData("USER_NAME", userName);
            endDate.addData("REG_FEE", df.format(regFee));
            endDate.addData("CLINIC_FEE", df.format(clinicFee));
            endDate.addData("AR_AMT", df.format(arAmt));
            endDate.addData("REG_COUNT", regCount);
            endDate.addData("UN_REG_COUNT", unRegCount);
        }
        endDate.setCount(count);
        endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE");
        endDate.addData("SYSTEM", "COLUMNS", "SESSION_DESC");
        endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
        endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
        endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
        endDate.addData("SYSTEM", "COLUMNS", "REG_FEE");
        endDate.addData("SYSTEM", "COLUMNS", "CLINIC_FEE");
        endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
        endDate.addData("SYSTEM", "COLUMNS", "REG_COUNT");
        endDate.addData("SYSTEM", "COLUMNS", "UN_REG_COUNT");
        return endDate;
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
        ExportExcelUtil.getInstance().exportExcel(table, "医生工作量统计报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();

    }

    /**
     * 明细
     */
    public void onDetial() {
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("请点选行数据!");
        }
        TParm tableParm = table.getParmValue();
        String drCode = tableParm.getValue("REALDR_CODE", selRow);
        Timestamp orderDateStr = tableParm.getTimestamp("ORDER_DATE", selRow);
        Vector vct = new Vector();
        vct.add(0, getValue("S_DATE"));
        vct.add(1, getValue("E_DATE"));
        vct.add(2, drCode);
        vct.add(3, orderDateStr);
        this.openDialog("%ROOT%\\config\\reg\\REGDocWorkloadDetial.x", vct);
    }
}


