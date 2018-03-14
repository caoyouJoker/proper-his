package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>Title: 科室药品发药成本汇总表</p>
 *
 * <p>Description: 科室药品发药成本汇总表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.12.28
 * @version 1.0
 */
public class INDDipenseCostQueryControl
    extends TControl {

    public TTable table_1;

    public INDDipenseCostQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table_1 = this.getTable("TABLE_1");
        // 初始化统计区间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // 结束时间
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" + TypeTool.getString(date).
            substring(5, 7) + "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
        // (上个月最后一天)
        setValue("END_DATE", StringTool.rollDate(dateTime, -1));
        // 起始时间(上个月第一天)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String start_date = getValueString("START_DATE");
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
        String end_date = getValueString("END_DATE");
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
            end_date.substring(8, 10) + end_date.substring(11, 13) +
            end_date.substring(14, 16) + end_date.substring(17, 19);

        String where = "";
        String where_1 = "";
        String where_2 = "";
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            where += " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") +
                "' ";
        }
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            where_1 += " AND A.EXEC_DEPT_CODE = '" + getValueString("ORG_CODE") +
                "' ";
            where_2 += " AND A.EXE_DEPT_CODE = '" + getValueString("ORG_CODE") +
                "' ";
        }

        String sql_1 = "SELECT   B.DEPT_CHN_DESC, C.USER_NAME, SUM (A.AR_AMT) "
            +
            " AS OWN_AMT, SUM (A.COST_AMT) AS COST_AMT, D.PHA_TYPE, A.DEPT_CODE, A.DR_CODE "
            + " FROM OPD_ORDER A, SYS_DEPT B, SYS_OPERATOR C, PHA_BASE D "
            + " WHERE A.DEPT_CODE = B.DEPT_CODE "
            + " AND A.DR_CODE = C.USER_ID "
            + " AND A.ORDER_CODE = D.ORDER_CODE "
            + " AND A.PHA_DOSAGE_DATE IS NOT NULL "
            + " AND A.PHA_RETN_CODE IS NULL "
            + " AND B.FINAL_FLG = 'Y' "
            + " AND B.CLASSIFY = '0' "
            + " AND A.REGION_CODE = '" + Operator.getRegion() + "' "
            + " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "', 'YYYYMMDDHH24MISS') " + where + where_1
            + " GROUP BY B.DEPT_CHN_DESC, C.USER_NAME, D.PHA_TYPE, "
            + " A.DEPT_CODE, A.DR_CODE ORDER BY A.DEPT_CODE, A.DR_CODE";
//        System.out.println("sql_1----" + sql_1);

        String sql_2 = "SELECT D.DEPT_CHN_DESC, E.USER_NAME, SUM (A.OWN_AMT) "
            +
            " AS OWN_AMT, SUM (A.COST_AMT) AS COST_AMT, C.PHA_TYPE, A.DEPT_CODE, A.DR_CODE "
            + " FROM IBS_ORDD A, IBS_ORDM B, PHA_BASE C, SYS_DEPT D, "
            + " SYS_OPERATOR E "
            + " WHERE A.CASE_NO = B.CASE_NO "
            + " AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND A.DEPT_CODE = D.DEPT_CODE "
            + " AND A.DR_CODE = E.USER_ID "
            + " AND B.DATA_TYPE = '2' "
            + " AND D.FINAL_FLG = 'Y' "
            + " AND D.CLASSIFY = '0' "
            + " AND B.REGION_CODE = '" + Operator.getRegion() + "' "
            + " AND A.BILL_DATE BETWEEN TO_DATE('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "', 'YYYYMMDDHH24MISS') " + where + where_2
            + " GROUP BY D.DEPT_CHN_DESC, E.USER_NAME, C.PHA_TYPE, "
            + " A.DEPT_CODE, A.DR_CODE ORDER BY A.DEPT_CODE, A.DR_CODE ";
//        System.out.println("sql_2----" + sql_2);
        TParm parm_1 = new TParm(TJDODBTool.getInstance().select(sql_1));
        TParm parm_2 = new TParm(TJDODBTool.getInstance().select(sql_2));

        TParm parm = new TParm();
        if (parm_1 == null || parm_1.getCount("DEPT_CHN_DESC") <= 0) {
            if (parm_2 == null || parm_2.getCount("DEPT_CHN_DESC") <= 0) {
                this.messageBox("没有查询数据");
                return;
            }
            parm = parm_2;
        }
        else if (parm_2 == null || parm_2.getCount("DEPT_CHN_DESC") <= 0) {
            if (parm_1 == null || parm_1.getCount("DEPT_CHN_DESC") <= 0) {
                this.messageBox("没有查询数据");
                return;
            }
            parm = parm_1;
        }
        else {
            String dr_code = "";
            String org_code = "";
            String pha_type = "";
            for (int i = 0; i < parm_2.getCount("DEPT_CHN_DESC"); i++) {
                boolean flg = false;
                dr_code = parm_2.getValue("DR_CODE", i);
                org_code = parm_2.getValue("DEPT_CODE", i);
                pha_type = parm_2.getValue("PHA_TYPE", i);
                for (int j = 0; j < parm_1.getCount("DEPT_CHN_DESC"); j++) {
                    if (org_code.equals(parm_1.getValue("DEPT_CODE", j)) &&
                        dr_code.equals(parm_1.getValue("DR_CODE", j)) &&
                        pha_type.equals(parm_1.getValue("PHA_TYPE", j))) {
                        parm_1.setData("OWN_AMT", j, StringTool.round(
                            parm_1.getDouble("OWN_AMT", j) +
                            parm_2.getDouble("OWN_AMT", i), 2));
                        parm_1.setData("COST_AMT", j, StringTool.round(
                            (parm_1.getDouble("COST_AMT", j) +
                             parm_2.getDouble("COST_AMT", i)), 2));
                        flg = true;
                        break;
                    }
                }
                if (!flg) {
                    parm_1.addData("DEPT_CHN_DESC",
                                   parm_2.getValue("DEPT_CHN_DESC", i));
                    parm_1.addData("USER_NAME",
                                   parm_2.getValue("USER_NAME", i));
                    parm_1.addData("OWN_AMT",
                                   StringTool.round(parm_2.getDouble("OWN_AMT",
                        i), 2));
                    parm_1.addData("COST_AMT", StringTool.round(parm_2.
                        getDouble("COST_AMT", i), 2));
                    parm_1.addData("PHA_TYPE", parm_2.getValue("PHA_TYPE", i));
                    parm_1.addData("DEPT_CODE", parm_2.getValue("DEPT_CODE", i));
                    parm_1.addData("DR_CODE", parm_2.getValue("DR_CODE", i));
                }
            }
            parm = parm_1;
            if (parm_1 == null || parm_1.getCount("DEPT_CHN_DESC") <= 0) {
                this.messageBox("没有查询数据");
                return;
            }
        }

        TParm data = new TParm();

        String dept_code = "";
        String dr_code = "";
        data.addData("DEPT_CHN_DESC", "");
        data.addData("USER_NAME", "");
        data.addData("OWN_AMT_W", 0);
        data.addData("COST_AMT_W", 0);
        data.addData("DIFF_AMT_W", 0);
        data.addData("OWN_AMT_C", 0);
        data.addData("COST_AMT_C", 0);
        data.addData("DIFF_AMT_C", 0);
        data.addData("OWN_AMT_G", 0);
        data.addData("COST_AMT_G", 0);
        data.addData("DIFF_AMT_G", 0);
        data.addData("DEPT_CODE", "");
        data.addData("DR_CODE", "");

        for (int i = 0; i < parm.getCount("DEPT_CHN_DESC"); i++) {
            boolean flg = false;
            dept_code = parm.getValue("DEPT_CODE", i);
            dr_code = parm.getValue("DR_CODE", i);
            if (i == 0) {
                data.setData("DEPT_CHN_DESC", i,
                             parm.getValue("DEPT_CHN_DESC", i));
                data.setData("USER_NAME", i, parm.getValue("USER_NAME", i));
                if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                    data.setData("OWN_AMT_W", i, parm.getDouble("OWN_AMT", i));
                    data.setData("COST_AMT_W", i, parm.getDouble("COST_AMT", i));
                    data.setData("DIFF_AMT_W", i, StringTool.round(parm.getDouble("OWN_AMT", i) -
                                 parm.getDouble("COST_AMT", i), 2));
                }
                else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                    data.setData("OWN_AMT_C", i, parm.getDouble("OWN_AMT", i));
                    data.setData("COST_AMT_C", i, parm.getDouble("COST_AMT", i));
                    data.setData("DIFF_AMT_C", i, StringTool.round(parm.getDouble("OWN_AMT", i) -
                                 parm.getDouble("COST_AMT", i), 2));
                }
                else {
                    data.setData("OWN_AMT_G", i, parm.getDouble("OWN_AMT", i));
                    data.setData("COST_AMT_G", i, parm.getDouble("COST_AMT", i));
                    data.setData("DIFF_AMT_G", i, StringTool.round(parm.getDouble("OWN_AMT", i) -
                                 parm.getDouble("COST_AMT", i), 2));
                }
                data.setData("DEPT_CODE", i, parm.getValue("DEPT_CODE", i));
                data.setData("DR_CODE", i, parm.getValue("DR_CODE", i));
            }
            else {
                for (int j = 0; j < data.getCount("DEPT_CHN_DESC"); j++) {
                    if (dept_code.equals(data.getValue("DEPT_CODE", j)) &&
                        dr_code.equals(data.getValue("DR_CODE", j))) {
                        if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                            data.setData("OWN_AMT_W", j,
                                         parm.getDouble("OWN_AMT", i));
                            data.setData("COST_AMT_W", j,
                                         parm.getDouble("COST_AMT", i));
                            data.setData("DIFF_AMT_W", j,
                                         StringTool.round(parm.getDouble("OWN_AMT", i) -
                                         parm.getDouble("COST_AMT", i),2));
                        }
                        else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                            data.setData("OWN_AMT_C", j,
                                         parm.getDouble("OWN_AMT", i));
                            data.setData("COST_AMT_C", j,
                                         parm.getDouble("COST_AMT", i));
                            data.setData("DIFF_AMT_C", j,
                                         StringTool.round(parm.getDouble("OWN_AMT", i) -
                                         parm.getDouble("COST_AMT", i),2));
                        }
                        else {
                            data.setData("OWN_AMT_G", j,
                                         parm.getDouble("OWN_AMT", i));
                            data.setData("COST_AMT_G", j,
                                         parm.getDouble("COST_AMT", i));
                            data.setData("DIFF_AMT_G", j,
                                         StringTool.round(parm.getDouble("OWN_AMT", i) -
                                         parm.getDouble("COST_AMT", i),2));
                        }
                        flg = true;
                        break;
                    }
                }
                if (!flg) {
                    data.addData("DEPT_CHN_DESC",
                                 parm.getValue("DEPT_CHN_DESC", i));
                    data.addData("USER_NAME", parm.getValue("USER_NAME", i));
                    data.addData("OWN_AMT_W", 0);
                    data.addData("COST_AMT_W", 0);
                    data.addData("DIFF_AMT_W", 0);
                    data.addData("OWN_AMT_C", 0);
                    data.addData("COST_AMT_C", 0);
                    data.addData("DIFF_AMT_C", 0);
                    data.addData("OWN_AMT_G", 0);
                    data.addData("COST_AMT_G", 0);
                    data.addData("DIFF_AMT_G", 0);
                    int row = data.getCount("DEPT_CHN_DESC") - 1;
                    if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                        data.setData("OWN_AMT_W", row,
                                     parm.getDouble("OWN_AMT", i));
                        data.setData("COST_AMT_W", row,
                                     parm.getDouble("COST_AMT", i));
                        data.setData("DIFF_AMT_W", row,
                                     StringTool.round(parm.getDouble("OWN_AMT", i) -
                                     parm.getDouble("COST_AMT", i),2));
                    }
                    else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                        data.setData("OWN_AMT_C", row,
                                     parm.getDouble("OWN_AMT", i));
                        data.setData("COST_AMT_C", row,
                                     parm.getDouble("COST_AMT", i));
                        data.setData("DIFF_AMT_C", row,
                                     StringTool.round(parm.getDouble("OWN_AMT", i) -
                                     parm.getDouble("COST_AMT", i),2));
                    }
                    else {
                        data.setData("OWN_AMT_G", row,
                                     parm.getDouble("OWN_AMT", i));
                        data.setData("COST_AMT_G", row,
                                     parm.getDouble("COST_AMT", i));
                        data.setData("DIFF_AMT_G", row,
                                     StringTool.round(parm.getDouble("OWN_AMT", i) -
                                     parm.getDouble("COST_AMT", i),2));
                    }
                    data.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
                    data.addData("DR_CODE", parm.getValue("DR_CODE", i));
                }
            }
        }

        Map map = new HashMap();
        for (int i = 0; i < data.getCount("DEPT_CODE"); i++) {
            if (map.isEmpty()) {
                map.put(data.getValue("DEPT_CODE", i),
                        data.getValue("DEPT_CHN_DESC", i));
            }
            else {
                if (map.containsKey(data.getValue("DEPT_CODE", i))) {
                    continue;
                }
                else {
                    map.put(data.getValue("DEPT_CODE", i),
                            data.getValue("DEPT_CHN_DESC", i));
                }
            }
        }

        //合计
        double SUM_OWN_AMT_W = 0;
        double SUM_COST_AMT_W = 0;
        double SUM_DIFF_AMT_W = 0;
        double SUM_OWN_AMT_C = 0;
        double SUM_COST_AMT_C = 0;
        double SUM_DIFF_AMT_C = 0;
        double SUM_OWN_AMT_G = 0;
        double SUM_COST_AMT_G = 0;
        double SUM_DIFF_AMT_G = 0;

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        TParm table_parm = new TParm();
        while (iterator.hasNext()) {
            //小计
            double OWN_AMT_W = 0;
            double COST_AMT_W = 0;
            double DIFF_AMT_W = 0;
            double OWN_AMT_C = 0;
            double COST_AMT_C = 0;
            double DIFF_AMT_C = 0;
            double OWN_AMT_G = 0;
            double COST_AMT_G = 0;
            double DIFF_AMT_G = 0;

            dept_code = TypeTool.getString(iterator.next());
            for (int i = 0; i < data.getCount("DEPT_CODE"); i++) {
                if (dept_code.equals(data.getValue("DEPT_CODE", i))) {
                    table_parm.addData("DEPT_CHN_DESC",
                                       data.getValue("DEPT_CHN_DESC", i));
                    table_parm.addData("USER_NAME",
                                       data.getValue("USER_NAME", i));
                    OWN_AMT_W += data.getDouble("OWN_AMT_W", i);
                    table_parm.addData("OWN_AMT_W",
                                       data.getDouble("OWN_AMT_W", i));
                    COST_AMT_W += data.getDouble("COST_AMT_W", i);
                    table_parm.addData("COST_AMT_W",
                                       data.getDouble("COST_AMT_W", i));
                    DIFF_AMT_W += data.getDouble("DIFF_AMT_W", i);
                    table_parm.addData("DIFF_AMT_W",
                                       data.getDouble("DIFF_AMT_W", i));
                    OWN_AMT_C += data.getDouble("OWN_AMT_C", i);
                    table_parm.addData("OWN_AMT_C",
                                       data.getDouble("OWN_AMT_C", i));
                    COST_AMT_C += data.getDouble("COST_AMT_C", i);
                    table_parm.addData("COST_AMT_C",
                                       data.getDouble("COST_AMT_C", i));
                    DIFF_AMT_C += data.getDouble("DIFF_AMT_C", i);
                    table_parm.addData("DIFF_AMT_C",
                                       data.getDouble("DIFF_AMT_C", i));
                    OWN_AMT_G += data.getDouble("OWN_AMT_G", i);
                    table_parm.addData("OWN_AMT_G",
                                       data.getDouble("OWN_AMT_G", i));
                    COST_AMT_G += data.getDouble("COST_AMT_G", i);
                    table_parm.addData("COST_AMT_G",
                                       data.getDouble("COST_AMT_G", i));
                    DIFF_AMT_G += data.getDouble("DIFF_AMT_G", i);
                    table_parm.addData("DIFF_AMT_G",
                                       data.getDouble("DIFF_AMT_G", i));
                }
            }
            //小计
            table_parm.addData("DEPT_CHN_DESC", map.get(dept_code));
            table_parm.addData("USER_NAME", "小计:");
            SUM_OWN_AMT_W += OWN_AMT_W;
            table_parm.addData("OWN_AMT_W", StringTool.round(OWN_AMT_W, 2));
            SUM_COST_AMT_W += COST_AMT_W;
            table_parm.addData("COST_AMT_W", StringTool.round(COST_AMT_W, 2));
            SUM_DIFF_AMT_W += DIFF_AMT_W;
            table_parm.addData("DIFF_AMT_W", StringTool.round(DIFF_AMT_W, 2));
            SUM_OWN_AMT_C += OWN_AMT_C;
            table_parm.addData("OWN_AMT_C", StringTool.round(OWN_AMT_C, 2));
            SUM_COST_AMT_C += COST_AMT_C;
            table_parm.addData("COST_AMT_C", StringTool.round(COST_AMT_C, 2));
            SUM_DIFF_AMT_C += DIFF_AMT_C;
            table_parm.addData("DIFF_AMT_C", StringTool.round(DIFF_AMT_C, 2));
            SUM_OWN_AMT_G += OWN_AMT_G;
            table_parm.addData("OWN_AMT_G", StringTool.round(OWN_AMT_G, 2));
            SUM_COST_AMT_G += COST_AMT_G;
            table_parm.addData("COST_AMT_G", StringTool.round(COST_AMT_G, 2));
            SUM_DIFF_AMT_G += DIFF_AMT_G;
            table_parm.addData("DIFF_AMT_G", StringTool.round(DIFF_AMT_G, 2));
        }

        //总计
        table_parm.addData("DEPT_CHN_DESC", "总计:");
        table_parm.addData("USER_NAME", "");
        table_parm.addData("OWN_AMT_W", StringTool.round(SUM_OWN_AMT_W, 2));
        table_parm.addData("COST_AMT_W", StringTool.round(SUM_COST_AMT_W, 2));
        table_parm.addData("DIFF_AMT_W", StringTool.round(SUM_DIFF_AMT_W, 2));
        table_parm.addData("OWN_AMT_C", StringTool.round(SUM_OWN_AMT_C, 2));
        table_parm.addData("COST_AMT_C", StringTool.round(SUM_COST_AMT_C, 2));
        table_parm.addData("DIFF_AMT_C", StringTool.round(SUM_DIFF_AMT_C, 2));
        table_parm.addData("OWN_AMT_G", StringTool.round(SUM_OWN_AMT_G, 2));
        table_parm.addData("COST_AMT_G", StringTool.round(SUM_COST_AMT_G, 2));
        table_parm.addData("DIFF_AMT_G", StringTool.round(SUM_DIFF_AMT_G, 2));

        //System.out.println("table_parm----" + table_parm);

        table_1.setParmValue(table_parm);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table_1.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table_1,
                                                  "科室药品发药成本汇总表");
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if (table_1.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }

        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "科室药品发药成本汇总表");
        date.setData("ORG_CODE", "TEXT",
                     "统计部门: " +
                     ("".equals(this.getText("ORG_CODE")) ? "全部" :
                      this.getText("ORG_CODE")));
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT",
                     "统计区间: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) +
                     " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19));
        date.setData("DATE", "TEXT",
                     "制表时间: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());

        TParm tableParm = table_1.getParmValue();
        tableParm.setCount(tableParm.getCount("DEPT_CHN_DESC"));
        tableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        tableParm.addData("SYSTEM", "COLUMNS", "USER_NAME");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_W");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_W");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_W");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_C");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_C");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_C");
        tableParm.addData("SYSTEM", "COLUMNS", "OWN_AMT_G");
        tableParm.addData("SYSTEM", "COLUMNS", "COST_AMT_G");
        tableParm.addData("SYSTEM", "COLUMNS", "DIFF_AMT_G");
        date.setData("TABLE", tableParm.getData());

        //System.out.println("date----" + date);
        // 调用打印方法
        this.openPrintWindow(
            "%ROOT%\\config\\prt\\IND\\INDDipenseCostQuery.jhw", date);

    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "ORG_CODE;DEPT_CODE";
        this.clearValue(clearStr);

        // 初始化统计区间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // 结束时间
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" + TypeTool.getString(date).
            substring(5, 7) + "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
        // (上个月最后一天)
        setValue("END_DATE", StringTool.rollDate(dateTime, -1));
        // 起始时间(上个月第一天)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");

        table_1.removeRowAll();
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
