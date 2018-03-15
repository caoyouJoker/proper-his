package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title:健康检查参数档 </p>
 *
 * <p>Description:健康检查参数档 </p>
 *
 * <p>Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wanglong 2014.02.14
 * @version 1.0
 */
public class HRMSysParmControl extends TControl {

    private TDataStore tds;//用于记录参数的DataStore
    private TRadioButton RIS_PRINT_N;//单选按钮
    private TRadioButton RIS_PRINT_Y;
    private TRadioButton RIS_PRINT_SOME;
    private TTable deptTable;//科室Grid
    private TTable orderTable;//医嘱Grid

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        RIS_PRINT_N = (TRadioButton) this.getComponent("RIS_PRINT_N");
        RIS_PRINT_Y = (TRadioButton) this.getComponent("RIS_PRINT_Y");
        RIS_PRINT_SOME = (TRadioButton) this.getComponent("RIS_PRINT_SOME");
        deptTable = (TTable) this.getComponent("DEPT_TABLE");
        orderTable = (TTable) this.getComponent("ORDER_TABLE");
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");// 设置弹出菜单
        callFunction("UI|ORDER_CODE|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popReturn"); // 定义接受返回值方法
        tds = new TDataStore();
        tds.setSQL("SELECT * FROM HRM_SYSPARM");
        tds.retrieve();
        if (tds.getItemString(0, "RIS_PRINT").equals("N")) {// 不打印
            RIS_PRINT_N.setSelected(true);
        } else if (tds.getItemString(0, "RIS_PRINT").equals("Y")) {// 全部打印
            RIS_PRINT_Y.setSelected(true);
        } else {// 仅某科室打印
            RIS_PRINT_SOME.setSelected(true);
        }
        TDataStore deptDS = new TDataStore();
        deptDS.setSQL("SELECT * FROM HRM_RISDEPT ORDER BY DEPT_CODE");
        deptDS.retrieve();
        for (int i = 0; i < deptDS.rowCount(); i++) {
            int row = deptTable.addRow();
            deptTable.setItem(row, "DEPT_CODE", deptDS.getItemString(i, "DEPT_CODE"));
        }
        String sql =
                "SELECT A.ORDER_CODE,B.ORDER_DESC FROM HRM_RISORDER A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE";
        TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (orderParm.getErrCode() != 0) {
            this.messageBox(orderParm.getErrText());
            return;
        }
        for (int i = 0; i < orderParm.getCount(); i++) {
            int row = orderTable.addRow();
            orderTable.setItem(row, "ORDER_CODE", orderParm.getValue("ORDER_CODE", i));
            orderTable.setItem(row, "ORDER_DESC", orderParm.getValue("ORDER_DESC", i));
        }
    }

    /**
     * 保存
     */
    public void onSave() {
        TParm deptTableParm = new TParm();
        for (int i = 0; i < deptTable.getRowCount(); i++) {
            if (deptTable.getItemString(i, "DEPT_CODE").trim().length() > 0) {
                deptTableParm.addData("DEPT_CODE", deptTable.getItemString(i, "DEPT_CODE"));
            }
        }
        deptTableParm.setCount(deptTableParm.getCount("DEPT_CODE"));
        TParm orderTableParm = new TParm();
        for (int i = 0; i < orderTable.getRowCount(); i++) {
            if (orderTable.getItemString(i, "ORDER_CODE").trim().length() > 0) {
                orderTableParm.addData("ORDER_CODE", orderTable.getItemString(i, "ORDER_CODE"));
            }
        }
        orderTableParm.setCount(orderTableParm.getCount("ORDER_CODE"));
        String[] sql = new String[]{};
        sql = StringTool.copyArray(sql, new String[]{"DELETE FROM HRM_RISDEPT" });
        sql = StringTool.copyArray(sql, new String[]{"DELETE FROM HRM_RISORDER" });
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String now = StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss");
        String deptTemplateSql =
                "INSERT INTO HRM_RISDEPT(DEPT_CODE,OPT_USER,OPT_DATE,OPT_TERM) VALUES(!,@,#,&)";
        for (int i = 0; i < deptTableParm.getCount(); i++) {
            String insertDeptSql =
                    deptTemplateSql
                            .replaceFirst("!", "'" + deptTableParm.getValue("DEPT_CODE", i) + "'")
                            .replaceFirst("@", "'" + Operator.getID() + "'")
                            .replaceFirst("#", "TO_DATE('" + now + "', 'YYYY/MM/DD HH24:MI:SS')")
                            .replaceFirst("&", "'" + Operator.getIP() + "'");
            sql = StringTool.copyArray(sql, new String[]{insertDeptSql });
        }
        String orderTemplateSql =
                "INSERT INTO HRM_RISORDER(ORDER_CODE,OPT_USER,OPT_DATE,OPT_TERM) VALUES(!,@,#,&)";
        for (int i = 0; i < orderTableParm.getCount(); i++) {
            String insertOrderSql =
                    orderTemplateSql
                            .replaceFirst("!", "'" + orderTableParm.getValue("ORDER_CODE", i) + "'")
                            .replaceFirst("@", "'" + Operator.getID() + "'")
                            .replaceFirst("#", "TO_DATE('" + now + "', 'YYYY/MM/DD HH24:MI:SS')")
                            .replaceFirst("&", "'" + Operator.getIP() + "'");
            sql = StringTool.copyArray(sql, new String[]{insertOrderSql });
        }
        if (RIS_PRINT_N.isSelected()) {// 不打印
            tds.setItem(0, "RIS_PRINT", "N");
        } else if (RIS_PRINT_Y.isSelected()) {// 全部打印
            tds.setItem(0, "RIS_PRINT", "Y");
        } else {// 仅某科室打印
            tds.setItem(0, "RIS_PRINT", "SOME");
        }
        tds.setItem(0, "OPT_USER", Operator.getID());
        tds.setItem(0, "OPT_DATE", SystemTool.getInstance().getDate());
        tds.setItem(0, "OPT_TERM", Operator.getIP());
        tds.setActive(0);
        sql = StringTool.copyArray(sql, tds.getUpdateSQL());
//        for (int i = 0; i < sql.length; i++) {
//            System.out.println("---------sql["+i+"]--------"+sql[i]);
//        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", inParm);
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
        } else {
            this.messageBox("P0001");
            tds.resetModify();
        }
    }

    /**
     * 接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(orderCode)) {
            ((TTextField) this.getComponent("ORDER_CODE")).setValue(orderCode);
        }
        String orderDesc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(orderDesc)) {
            ((TTextField) this.getComponent("ORDER_DESC")).setValue(orderDesc);
        }
    }
    
    /**
     * 添加科室
     */
    public void onAddDept() {
        String deptCode = this.getValueString("DEPT_CODE");
        if (!checkGrid(deptTable, deptCode, 0)) return;
        if (deptCode.length() > 0) {
            int row = deptTable.addRow();
            deptTable.setItem(row, "DEPT_CODE", deptCode);
        }
    }
    
    /**
     * 删除科室
     */
    public void onDelDept() {
        int row = deptTable.getSelectedRow();
        if (row > -1) {
            deptTable.removeRow(row);
        }
    }

    /**
     * 添加医嘱
     */
    public void onAddOrder() {
        String orderCode = this.getValueString("ORDER_CODE");
        String orderDesc = this.getValueString("ORDER_DESC");
        if (!checkGrid(orderTable, orderCode, 0)) return;
        if (orderCode.length() > 0) {
            int row = orderTable.addRow();
            orderTable.setItem(row, "ORDER_CODE", orderCode);
            orderTable.setItem(row, "ORDER_DESC", orderDesc);
        }
    }
    
    /**
     * 删除医嘱
     */
    public void onDelOrder() {
        int row = orderTable.getSelectedRow();
        if (row > -1) {
            orderTable.removeRow(row);
        }
    }

    /**
     * 检查Grid中是否有重复项目
     * @param table
     * @param obj
     * @param column
     * @return
     */
    private boolean checkGrid(TTable table,String obj,int column){
        for (int i = 0; i < table.getRowCount(); i++) {
            if (obj.equals(table.getValueAt(i, column).toString())) {
                this.messageBox_("不能重复添加");
                return false;
            }
        }
        return true;
    }

}
