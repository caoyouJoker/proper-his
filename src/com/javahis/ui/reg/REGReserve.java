package com.javahis.ui.reg;

import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 预约挂号人次统计报表</p>
 *
 * <p>Description: 预约挂号人次统计报表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.04
 * @version 1.0
 */
public class REGReserve    extends TControl {
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        initPage();
    }

    /**
     * 行单击事件
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0)
            return;
        setPageValue();
    }

    /**
     * 初始化界面
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("CLINICTYPE_CODE", "");
        setValue("DEPT_CODE", "");
        setValue("DR_CODE", "");
        setValue("MR_NO", "");
        setValue("PAT_NAME", "");
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
                                              "yyyy-MM-dd hh:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyy/MM/dd");
        TParm parm = new TParm();
        parm.setData("Title", "预约挂号人次统计报表");
        parm.setData("S_DATE", sDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", Operator.getName());
        parm.setData("OPT_DATE", sysDate);
        parm.setData("reservetable", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\REGReserve.jhw", parm);

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
        String clinicTypeCodeWhere = "";
        if (getValue("CLINICTYPE_CODE").toString().length() != 0)
            clinicTypeCodeWhere = " AND A.CLINICTYPE_CODE = '" +
                getValue("CLINICTYPE_CODE") +
                "'  ";
        String deptCodeWhere = "";
        if (getValue("DEPT_CODE").toString().length() != 0)
            deptCodeWhere = " AND A.REALDEPT_CODE = '" + getValue("DEPT_CODE") +
                "'  ";
        String drCodeWhere = "";
        if (getValue("DR_CODE").toString().length() != 0)
            drCodeWhere = " AND A.REALDR_CODE = '" + getValue("DR_CODE") +
                "'  ";
        String mrNoWhere = "";
        if (getValue("MR_NO").toString().trim().length() != 0)
            mrNoWhere = " AND A.MR_NO = '" + getValue("MR_NO") +
                "'  ";
        String sql =
            " SELECT A.ADM_DATE, A.MR_NO, A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE," +
            "        A.QUE_NO, A.CTZ1_CODE, B.AR_AMT, C.PAT_NAME, D.CLINICTYPE_DESC," +
            "        E.USER_NAME, F.DEPT_ABS_DESC,G.CTZ_DESC " +
            "   FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,REG_CLINICTYPE D,SYS_OPERATOR E,SYS_DEPT F,SYS_CTZ G " +
            "  WHERE A.REG_DATE BETWEEN TO_DATE ('" + startTime + "000000" +
            "', 'yyyyMMddHH24miss') " +
            "                       AND TO_DATE ('" + endTime + "235959" +
            "', 'yyyyMMddHH24miss') " +
            "    AND A.MR_NO = C.MR_NO " +
            "    AND A.CASE_NO = B.CASE_NO(+) " +
            "    AND A.REALDEPT_CODE = F.DEPT_CODE " +
            "    AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE " +
            clinicTypeCodeWhere +
            deptCodeWhere +
            drCodeWhere +
            mrNoWhere +
            "    AND A.REALDR_CODE = E.USER_ID " +
            "    AND A.CTZ1_CODE = G.CTZ_CODE ";
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("MR_NO") < 1) {
            this.messageBox("查无数据");
            this.initPage();
            return selParm;
        }
        this.callFunction("UI|Table|setParmValue", selParm);
        TParm endDate = new TParm();
        int count = selParm.getCount("MR_NO");
        //ADM_DATE;MR_NO;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
        for (int i = 0; i < count; i++) {
            Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);
            String admDateStr = StringTool.getString(admDate, "yyyyMMdd");
            String mrNo = selParm.getValue("MR_NO", i);
            String clinicDesc = selParm.getValue("CLINICTYPE_DESC", i);
            String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);
            String useName = selParm.getValue("USER_NAME", i);
            int queNo = selParm.getInt("QUE_NO", i);
            String ctzDesc = selParm.getValue("CTZ_DESC", i);
            double ar_amt = selParm.getDouble("AR_AMT", i);
            endDate.addData("ADM_DATE", admDateStr);
            endDate.addData("MR_NO", mrNo);
            endDate.addData("CLINICTYPE_DESC", clinicDesc);
            endDate.addData("DEPT_ABS_DESC", deptDesc);
            endDate.addData("USER_NAME", useName);
            endDate.addData("QUE_NO", queNo);
            endDate.addData("CTZ_DESC", ctzDesc);
            endDate.addData("AR_AMT", df.format(ar_amt));
        }
        endDate.setCount(count);
        endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE");
        endDate.addData("SYSTEM", "COLUMNS", "MR_NO");
        endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
        endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
        endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
        endDate.addData("SYSTEM", "COLUMNS", "QUE_NO");
        endDate.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
        endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
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
        ExportExcelUtil.getInstance().exportExcel(table, "预约挂号人次统计报表");
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
     * 点选grid数据给界面翻值
     */
    public void setPageValue() {
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("请点选行数据!");
        }
        TParm tableParm = table.getParmValue();
        String clinicTypeCode = tableParm.getValue("CLINICTYPE_CODE", selRow);
        String deptCode = tableParm.getValue("REALDEPT_CODE", selRow);
        String drCode = tableParm.getValue("REALDR_CODE", selRow);
        String mrNo = tableParm.getValue("MR_NO", selRow);
        String patName = tableParm.getValue("PAT_NAME", selRow);
        setValue("CLINICTYPE_CODE", clinicTypeCode);
        setValue("DEPT_CODE", deptCode);
        setValue("DR_CODE", drCode);
        setValue("MR_NO", mrNo);
        setValue("PAT_NAME", patName);
    }
}

