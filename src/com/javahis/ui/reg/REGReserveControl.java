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
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.Vector;

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
public class REGReserveControl
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
        setValue("CLINICTYPE_CODE", "");
        setValue("DEPT_CODE", "");
        setValue("CTZ_CODE", "");
        this.callFunction("UI|Table|removeRowAll");
        setValue("S_CLINICTYPE", "Y");
        this.callFunction("UI|CTZ_CODE|setEnabled", false);
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
        TParm selParm = new TParm();
        DecimalFormat df = new DecimalFormat("##########0");
        String clinicTypeCodeWhere = "";
        if (getValue("CLINICTYPE_CODE").toString().length() != 0)
            clinicTypeCodeWhere = " AND A.CLINICTYPE_CODE = '" +
                getValue("CLINICTYPE_CODE") +
                "'  ";
        String deptCodeWhere = "";
        if (getValue("DEPT_CODE").toString().length() != 0)
            deptCodeWhere = " AND A.DEPT_CODE = '" + getValue("DEPT_CODE") +
                "'  ";
        String ctzWhere = "";
        if (getValue("CTZ_CODE").toString().trim().length() != 0)
            ctzWhere = " AND A.CTZ2_CODE = '" + getValue("CTZ_CODE") +
                "'  ";
        String sql =
            " SELECT C.DEPT_DESC,A.DEPT_CODE, A.CLINICTYPE_CODE,"+
            "        CASE WHEN A.REGCAN_USER IS NOT NULL THEN 'Y' END RETURN_FLG " +
            "   FROM REG_PATADM A, REG_RECEIPT B,SYS_DEPT C " +
            "  WHERE A.ADM_TYPE = 'O' " +
            "    AND A.DEPT_CODE = C.DEPT_CODE "+
            "    AND A.HOSP_AREA = B.HOSP_AREA " +
            "    AND A.ADM_TYPE = B.ADM_TYPE " +
            "    AND A.CASE_NO = B.CASE_NO " +
            "    AND A.ADM_DATE BETWEEN TO_DATE ('" + startTime + "000000" +
            "', 'yyyyMMddHH24miss') " +
            "                       AND TO_DATE ('" + endTime + "235959" +
            "', 'yyyyMMddHH24miss') " +
            clinicTypeCodeWhere +
            deptCodeWhere +
            ctzWhere +
            "    AND A.REGMETHOD_CODE <> 'A' ";
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("DEPT_DESC") < 1) {
            this.messageBox("查无数据");
            this.initPage();
            return selParm;
        }
        TParm tableParm = new TParm();
        int selParmCount = selParm.getCount("DEPT_DESC");
        for (int i = 0; i < selParmCount; i++) {
            String clinicTypeCode = selParm.getValue("CLINICTYPE_CODE", i);
            String deptDesc = selParm.getValue("DEPT_DESC", i);
            String returnFlg = selParm.getValue("RETURN_FLG", i);
            if ("02".equals(clinicTypeCode)||"04".equals(clinicTypeCode)||"05".equals(clinicTypeCode)) {
                tableParm.addData("DEPT_DESC", deptDesc);
                tableParm.addData("CLINICTYPE04", 1);
                tableParm.addData("CLINICTYPE03", 0);
                tableParm.addData("CLINICTYPE13", 0);
                tableParm.addData("RETURNREG", 0);
            }
            if ("03".equals(clinicTypeCode)||"06".equals(clinicTypeCode)) {
                tableParm.addData("DEPT_DESC", deptDesc);
                tableParm.addData("CLINICTYPE03", 1);
                tableParm.addData("CLINICTYPE04", 0);
                tableParm.addData("CLINICTYPE13", 0);
                tableParm.addData("RETURNREG", 0);
            }
            if ("11".equals(clinicTypeCode) || "13".equals(clinicTypeCode) ||
                "21".equals(clinicTypeCode) || "25".equals(clinicTypeCode)) {
                tableParm.addData("DEPT_DESC", deptDesc);
                tableParm.addData("CLINICTYPE13", 1);
                tableParm.addData("CLINICTYPE03", 0);
                tableParm.addData("CLINICTYPE04", 0);
                tableParm.addData("RETURNREG", 0);
            }
            if ("Y".equals(returnFlg)) {
                tableParm.addData("DEPT_DESC", deptDesc);
                tableParm.addData("CLINICTYPE04", 0);
                tableParm.addData("CLINICTYPE03", 0);
                tableParm.addData("CLINICTYPE13", 0);
                tableParm.addData("RETURNREG", 1);

            }
        }
//        System.out.println("tableParm"+tableParm);
        TParm endDate = new TParm();
        int count = tableParm.getCount("DEPT_DESC");
        //DEPT_DESC;CLINICTYPE04;CLINICTYPE03;CLINICTYPE13;RETURNREG
        TParm endParm = new TParm();
        for (int k = 0; k < count; k++) {
            String deptDesc = tableParm.getValue("DEPT_DESC", k);
            int clinicType04 = tableParm.getInt("CLINICTYPE04", k);
            int clinicType03 = tableParm.getInt("CLINICTYPE03", k);
            int clinicType13 = tableParm.getInt("CLINICTYPE13", k);
            int returnReg = tableParm.getInt("RETURNREG", k);
            int row = -1;
            Vector endVct = (Vector) endParm.getData("DEPT_DESC");
            if (endVct != null) {
                row = endVct.indexOf(deptDesc);
            }
            if (row == -1) {
                endParm.addData("DEPT_DESC", deptDesc);
                endParm.addData("CLINICTYPE04", df.format(clinicType04));
                endParm.addData("CLINICTYPE03", df.format(clinicType03));
                endParm.addData("CLINICTYPE13", df.format(clinicType13));
                endParm.addData("RETURNREG", df.format(returnReg));

            }
            else {
                endParm.setData("CLINICTYPE04", row,
                                clinicType04 +
                                endParm.getInt("CLINICTYPE04", row));
                endParm.setData("CLINICTYPE03", row,
                                clinicType03 +
                                endParm.getInt("CLINICTYPE03", row));
                endParm.setData("CLINICTYPE13", row,
                                clinicType13 +
                                endParm.getInt("CLINICTYPE13", row));
                endParm.setData("RETURNREG", row,
                                returnReg + endParm.getInt("RETURNREG", row));
            }
        }
        int endParmCount = endParm.getCount("DEPT_DESC");
        int allClinicType04 = 0;
        int allClinicType03 = 0;
        int allClinicType13 = 0;
        int allReturnReg = 0;
        for (int j = 0; j < endParmCount; j++) {
            int clinicType04 = endParm.getInt("CLINICTYPE04", j);
            int clinicType03 = endParm.getInt("CLINICTYPE03", j);
            int clinicType13 = endParm.getInt("CLINICTYPE13", j);
            int returnReg = endParm.getInt("RETURNREG", j);
            allClinicType04 = allClinicType04 + clinicType04;
            allClinicType03 = allClinicType03 + clinicType03;
            allClinicType13 = allClinicType13 + clinicType13;
            allReturnReg = allReturnReg + returnReg;
        }
        endParm.addData("DEPT_DESC", "总计:");
        endParm.addData("CLINICTYPE04", allClinicType04);
        endParm.addData("CLINICTYPE03", allClinicType03);
        endParm.addData("CLINICTYPE13", allClinicType13);
        endParm.addData("RETURNREG", allReturnReg);
        endParm.setCount(endParmCount + 1);
//        System.out.println("endParm"+endParm);
        this.callFunction("UI|Table|setParmValue", endParm);
        endParm.setCount(endParm.getCount("DEPT_DESC"));
        endParm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        endParm.addData("SYSTEM", "COLUMNS", "CLINICTYPE04");
        endParm.addData("SYSTEM", "COLUMNS", "CLINICTYPE03");
        endParm.addData("SYSTEM", "COLUMNS", "CLINICTYPE13");
        endParm.addData("SYSTEM", "COLUMNS", "RETURNREG");
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
     * ridioButton改变事件
     */
    public void selQueryWhere() {
        if ("Y".equals(getValue("S_CLINICTYPE"))) {
            this.callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
            this.callFunction("UI|CTZ_CODE|setEnabled", false);
            setValue("CTZ_CODE", "");
        }
        if ("Y".equals(getValue("S_CTZ"))) {
            this.callFunction("UI|CTZ_CODE|setEnabled", true);
            this.callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
            setValue("CLINICTYPE_CODE", "");
        }

    }
}

