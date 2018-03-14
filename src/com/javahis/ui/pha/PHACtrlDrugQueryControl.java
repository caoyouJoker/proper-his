package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 特殊药品的统计查询</p>
 *
 * <p>Description: 特殊药品的统计查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2011.1.19
 * @version 1.0
 */
public class PHACtrlDrugQueryControl
    extends TControl {

    public TTable table;

    public PHACtrlDrugQueryControl() {
    }

    /*
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
        String sql = "";
        String where_dept_OE = "";
        String where_org_OE = "";
        String where_ctrl_OE = "";
        String where_dept_I = "";
        String where_org_I = "";
        String where_ctrl_I = "";
        String where_adm = "";
        String order_by =
            " ORDER BY ADM_TYPE, DEPT_CHN_DESC, ORG_CHN_DESC, ORDER_CODE ";
        String start_date = this.getValueString("START_DATE");
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + "000000";
        String end_date = this.getValueString("END_DATE");
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
            end_date.substring(8, 10) + "235959";

        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            where_dept_OE = " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") +
                "'";
            where_dept_I = " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") +
                "'";
        }

        if (!"".equals(this.getValueString("ORG_CODE"))) {
            where_org_OE = " AND A.EXEC_DEPT_CODE = '" +
                getValueString("ORG_CODE") +
                "'";
            where_org_I = " AND A.EXEC_DEPT_CODE = '" +
                getValueString("ORG_CODE") +
                "'";
        }

        if (!"".equals(this.getValueString("CTRL_CODE"))) {
            where_ctrl_OE = " AND D.CTRLDRUGCLASS_CODE = '" +
                getValueString("CTRL_CODE") + "'";
            where_ctrl_I = " AND D.CTRLDRUGCLASS_CODE = '" +
                getValueString("CTRL_CODE") + "'";
        }

        if (getCheckBox("TYPE_O").isSelected() &&
            getCheckBox("TYPE_E").isSelected()) {
            where_adm = "";
        }
        else if (getCheckBox("TYPE_O").isSelected()) {
            where_adm = " AND A.ADM_TYPE = 'O' ";
        }
        else if (getCheckBox("TYPE_E").isSelected()) {
            where_adm = " AND A.ADM_TYPE = 'E' ";
        }

        if (getCheckBox("TYPE_O").isSelected() ||
            getCheckBox("TYPE_E").isSelected()) {
            sql = "SELECT   CASE WHEN A.ADM_TYPE = 'O' THEN '门诊' ELSE '急诊' "
                + " END AS ADM_TYPE, B.DEPT_CHN_DESC, C.ORG_CHN_DESC, "
                + " D.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, "
                +
                " E.CTRLDRUGCLASS_CHN_DESC, SUM (A.DOSAGE_QTY) AS DOSAGE_QTY, "
                + " F.UNIT_CHN_DESC, SUM (A.AR_AMT) AS AR_AMT, "
                + " SUM (A.COST_AMT) AS COST_AMT "
                + " FROM OPD_ORDER A, SYS_DEPT B, IND_ORG C, PHA_BASE D, "
                + " SYS_CTRLDRUGCLASS E, SYS_UNIT F "
                + " WHERE A.DEPT_CODE = B.DEPT_CODE "
                + " AND A.EXEC_DEPT_CODE = C.ORG_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND D.CTRLDRUGCLASS_CODE = E.CTRLDRUGCLASS_CODE "
                + " AND A.DOSAGE_UNIT = F.UNIT_CODE "
                + where_dept_I + where_org_I + where_ctrl_I
                + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
                "', "
                + " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date + "', "
                + " 'YYYYMMDDHH24MISS' ) "
                + " GROUP BY A.ADM_TYPE, B.DEPT_CHN_DESC, C.ORG_CHN_DESC, "
                + " D.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, "
                + " E.CTRLDRUGCLASS_CHN_DESC, F.UNIT_CHN_DESC,A.DEPT_CODE, "
                + " A.EXEC_DEPT_CODE ";
        }

        if (getCheckBox("TYPE_I").isSelected()) {
            if (!"".equals(sql) && sql.length() > 0) {
                sql += " UNION ALL ";
            }
            sql += "SELECT   '住院' AS ADM_TYPE, B.DEPT_CHN_DESC, "
                +
                "C.ORG_CHN_DESC, D.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, "
                +
                "E.CTRLDRUGCLASS_CHN_DESC, SUM (A.DOSAGE_QTY) AS DOSAGE_QTY, "
                + "F.UNIT_CHN_DESC, SUM (A.DOSAGE_QTY * D.RETAIL_PRICE) "
                + "AS AR_AMT, SUM(A.DOSAGE_QTY * D.STOCK_PRICE) AS COST_AMT "
                + " FROM ODI_DSPNM A, SYS_DEPT B, IND_ORG C, PHA_BASE D, "
                + " SYS_CTRLDRUGCLASS E, SYS_UNIT F "
                + " WHERE A.DEPT_CODE = B.DEPT_CODE "
                + " AND A.EXEC_DEPT_CODE = C.ORG_CODE "
                + " AND A.ORDER_CODE = D.ORDER_CODE "
                + " AND D.CTRLDRUGCLASS_CODE = E.CTRLDRUGCLASS_CODE "
                + " AND A.DOSAGE_UNIT = F.UNIT_CODE "
                + where_dept_OE + where_org_OE + where_ctrl_OE + where_adm
                + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + start_date +
                "', "
                + " 'YYYYMMDDHH24MISS' ) AND TO_DATE ('" + end_date +
                "', 'YYYYMMDDHH24MISS') "
                + " GROUP BY B.DEPT_CHN_DESC, C.ORG_CHN_DESC, D.ORDER_CODE, "
                + " D.ORDER_DESC, D.SPECIFICATION, E.CTRLDRUGCLASS_CHN_DESC, "
                + " F.UNIT_CHN_DESC,A.DEPT_CODE, A.EXEC_DEPT_CODE ";
        }

        if (!"".equals(sql) && sql.length() > 0) {
            sql += order_by;
        }
        //System.out.println("sql----" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result == null || result.getCount("ADM_TYPE") <= 0) {
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
        String clearStr = "DEPT_CODE;ORG_CODE;CTRL_CODE;TOT_AMT";
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

        ExportExcelUtil.getInstance().exportExcel(table, "特殊药品统计查询");
    }

    /**
     * 计算总金额
     * @param parm TParm
     */
    private void sumAMT(TParm parm) {
        double sum_amt = 0;
        for (int i = 0; i < parm.getCount("ADM_TYPE"); i++) {
            sum_amt += parm.getDouble("AR_AMT", i);
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

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

}
