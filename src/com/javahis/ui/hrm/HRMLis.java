package com.javahis.ui.hrm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class HRMLis extends TControl {

    private TParm parm;

    public void onInit() {
        // 得到前一个页面的数据
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            this.setValue("StartDate", parm.getValue("start_Date"));
            this.setValue("EndDate", parm.getValue("end_Date"));
            TParm result =
                    onDisQuery(parm.getValue("CAT1_TYPE"), parm.getValue("start_Date"),
                               parm.getValue("end_Date"));
            this.getTTable("tblDis").setParmValue(result);
        }
    }

    /**
     * 查询详细信息
     */
    public TParm onDisQuery(String catType, String start_Date, String end_Date) {
        String sql =
                "SELECT ORDER_DESC, COUNT(CASE_NO) FROM  MED_APPLY " + " WHERE CAT1_TYPE = '"
                        + catType + "' AND STATUS <> 9 AND ORDER_DATE BETWEEN TO_DATE('" + start_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " AND TO_DATE('" + end_Date
                        + "','YYYY-MM-DD HH24:MI:SS') " + " GROUP BY ORDER_DESC";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 打印详细信息
     */
    public void onPrintDis() {
        int ATM = 0;
        TTable table_d = getTTable("tblDis");
        if (table_d.getRowCount() > 0) {
            // 打印数据OPT_USER;COUNT(CASE_NO)
            TParm date = new TParm();
            // 表头数据
            date.setData("Title", "TEXT",
                         Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
                                 + "检验检查详细人次统计报表");
            date.setData("createDate", "TEXT",
                         "制表日期: "
                                 + SystemTool.getInstance().getDate().toString().substring(0, 10)
                                         .replace('-', '/'));
            date.setData("start_Date", "TEXT", "开始时间: "
                    + parm.getValue("start_Date").toString().substring(0, 10).replace('-', '/'));
            date.setData("end_Date", "TEXT", "结束时间: "
                    + parm.getValue("end_Date").toString().substring(0, 10).replace('-', '/'));
            // 表格数据 B.CHN_DESC, COUNT (A.CASE_NO)
            TParm parm = new TParm();
            TParm tableParm = table_d.getParmValue();
            int count = tableParm.getCount();
            for (int i = 0; i < count; i++) {
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("COUNT(CASE_NO)", tableParm.getValue("COUNT(CASE_NO)", i));
                ATM += StringTool.getInt(tableParm.getValue("COUNT(CASE_NO)", i));
            }
            parm.setCount(parm.getCount("COUNT(CASE_NO)"));
            // System.out.println("parm====" + parm);
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "COUNT(CASE_NO)");
            date.setData("tbl1", parm.getData());
            // 表尾数据
            date.setData("createUser", "TEXT", "制表人: " + Operator.getName());
            date.setData("pass", "TEXT", "审核人: ");
            date.setData("ATM", "TEXT", "总计人数：" + ATM);
            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMCJBloodstatistics.jhw", date);
        } else {
            this.messageBox("E0010");// 弹出提示对话框（“没打印有数据”）
            return;
        }
    }

    /**
     * 汇出详细信息的Excel
     */
    public void onExportDis() {
        // 得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = getTTable("tblDis");
        ExportExcelUtil.getInstance().exportExcel(table, "检验检查人次详细信息统计报表");
    }

    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }
}
