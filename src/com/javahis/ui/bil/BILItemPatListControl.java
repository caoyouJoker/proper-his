package com.javahis.ui.bil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import jdo.bil.BILComparator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 医疗费用统计病患列表 </p>
 * 
 * <p> Description: 医疗费用统计病患列表 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 20130613
 * @version 1.0
 */
public class BILItemPatListControl
        extends TControl {

    private static String TABLE = "TABLE";
    private TTable table;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent(TABLE);
        addSortListener(table);// add by wanglong 20130613
        TParm parm = (TParm) this.getParameter();
        if (parm.getData("ADM_TYPE") == null || parm.getData("START_DATE") == null
                || parm.getData("END_DATE") == null || parm.getData("DEPT_CODE") == null
                || parm.getData("ORDER_CODE") == null || parm.getData("AR_AMT") == null) {
            messageBox("E0024");// 初始化参数失败
            this.closeWindow();
        }
        String admType = parm.getValue("ADM_TYPE"); // 科室代码
        TParm result = new TParm();
        if (admType.equals("O")) {// 门诊
            result = onQueryOPDPatList(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("查询失败 " + result.getErrText());
                this.closeWindow();
            }
        } else if (admType.equals("H")) {// 健检
            result = onQueryHRMPatList(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("查询失败 " + result.getErrText());
                this.closeWindow();
            }
        } else if (admType.equals("I")) {// 住院
            result = onQueryODIPatList(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("查询失败 " + result.getErrText());
                this.closeWindow();
                // return;
            }
        } else {// 汇总
            TParm sumParm = new TParm();
            parm.setData("ADM_TYPE", "O");
            result = onQueryOPDPatList(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("查询失败 " + result.getErrText());
                this.closeWindow();
            }
            String columnName[] = result.getNames("Data");
            for (int i = 0; i < result.getCount(); i++) {
                for (int j = 0; j < columnName.length; j++) {
                    sumParm.addData(columnName[j], result.getData(columnName[j], i));
                }
                sumParm.addData("ADM_TYPE", "O");
            }
            parm.setData("ADM_TYPE", "H");
            result = onQueryHRMPatList(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("查询失败 " + result.getErrText());
                this.closeWindow();
            }
            columnName = result.getNames("Data");
            for (int i = 0; i < result.getCount(); i++) {
                for (int j = 0; j < columnName.length; j++) {
                    sumParm.addData(columnName[j], result.getData(columnName[j], i));
                }
                sumParm.addData("ADM_TYPE", "H");
            }
            parm.setData("ADM_TYPE", "I");
            result = onQueryODIPatList(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("查询失败 " + result.getErrText());
                this.closeWindow();
            }
            columnName = result.getNames("Data");
            for (int i = 0; i < result.getCount(); i++) {
                for (int j = 0; j < columnName.length; j++) {
                    sumParm.addData(columnName[j], result.getData(columnName[j], i));
                }
                sumParm.addData("ADM_TYPE", "I");
            }
            sumParm.setCount(sumParm.getCount("ADM_TYPE"));
            table.setHeader("门急住别,60,ADM_TYPE;就诊日期,100;病案号,100;姓名,140;项目,180;数量,80");
            table.setColumnHorizontalAlignmentData("0,left;3,left;4,left;5,right");
            table.setParmMap("ADM_TYPE;ADM_DATE;MR_NO;PAT_NAME;ORDER_DESC;QTY");
            result = sumParm;
        }
        table.setParmValue(result);
    }

    /**
     * 查询门诊医疗统计病患信息
     * 
     * @param parm
     * @return
     */
    public TParm onQueryOPDPatList(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        String orderCode = parm.getValue("ORDER_CODE"); // 医嘱码
        double ownPrice = parm.getDouble("OWN_PRICE"); // 单价add by wanglong 20130805
        double arAmt = parm.getDouble("AR_AMT"); // 实收金额
        String rexpCode = parm.getValue("REXP_CODE"); // 费用类别
        String sql =
                "WITH A AS (SELECT DISTINCT C.*, C.TOT_AMT / C.DISPENSE_QTY AR_AMT, E.REXP_CODE "
                        + "   FROM (SELECT AA.CASE_NO, B.ORDER_CODE, B.ORDER_DESC, AA.DISCOUNT_RATE,"
                        + "                SUM(AA.AR_AMT) TOT_AMT, SUM(AA.DISPENSE_QTY) DISPENSE_QTY "
                        + "           FROM (SELECT A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
                        + "                        SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                        SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DOSAGE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                        SUM(A.DOSAGE_QTY*A.OWN_PRICE) OWN_PRICE, "//add by wanglong 20130805
                        + "                        SUM(A.AR_AMT) AR_AMT "
                        + "                   FROM OPD_ORDER A "
                        + "                  WHERE A.ADM_TYPE = 'O' "
                        + "                    AND A.BILL_FLG = 'Y' "
                        + "                    AND A.ORDERSET_CODE IS NOT NULL "
                        + "                    ? "
                        + "                    AND A.ORDER_DATE BETWEEN TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                                         AND TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "               GROUP BY A.CASE_NO, A.RX_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE) AA, OPD_ORDER B "
                        + "          WHERE AA.CASE_NO = B.CASE_NO "
                        + "            AND AA.ORDERSET_CODE = B.ORDER_CODE "
                        + "            & "//add by wanglong 20130805
                        + "       GROUP BY AA.CASE_NO, B.ORDER_CODE, B.ORDER_DESC, AA.DISCOUNT_RATE "
                        + "         HAVING SUM(AA.DISPENSE_QTY) <> 0 "
                        + "          UNION "
                        + "         SELECT A.CASE_NO,A.ORDER_CODE,A.ORDER_DESC,"
                        + "                CASE WHEN A.DISCOUNT_RATE = 0 THEN 1 ELSE A.DISCOUNT_RATE END DISCOUNT_RATE,"
                        + "                SUM(A.AR_AMT) TOT_AMT,SUM(A.DOSAGE_QTY) DISPENSE_QTY "
                        + "           FROM OPD_ORDER A "
                        + "          WHERE A.ADM_TYPE = 'O' "
                        + "            AND A.BILL_FLG = 'Y' "
                        + "            AND A.ORDERSET_CODE IS NULL "
                        + "            ? "
                        + "            AND A.ORDER_DATE BETWEEN TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                                 AND TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "            & "//add by wanglong 20130805
                        + "       GROUP BY A.CASE_NO, A.ORDER_CODE, A.ORDER_DESC, A.DISCOUNT_RATE) C,OPD_ORDER E "
                        + "  WHERE C.DISPENSE_QTY <> 0          "
                        + "    AND C.CASE_NO = E.CASE_NO        "
                        + "    AND C.ORDER_CODE = E.ORDER_CODE) "
                        + "SELECT B.ADM_DATE,B.MR_NO,C.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC,A.AR_AMT,A.DISPENSE_QTY QTY "
                        + "  FROM A, REG_PATADM B, SYS_PATINFO C "
                        + " WHERE A.CASE_NO = B.CASE_NO          "
                        + "   AND B.MR_NO = C.MR_NO              "
                        + "   #            #            #        "
                        + "ORDER BY B.ADM_DATE, B.REG_DATE       ";
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("\\?", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
            sql = sql.replaceFirst("\\?", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("\\?", "");
            sql = sql.replaceFirst("\\?", "");
        }
        sql = sql.replaceFirst("@", startDate);
        sql = sql.replaceFirst("@", endDate);
        sql = sql.replaceFirst("@", startDate);
        sql = sql.replaceFirst("@", endDate);
        if (ownPrice != 0) {// add by wanglong 20130805
//            sql = sql.replaceFirst("&", " AND AA.OWN_PRICE = " + ownPrice);
            sql = sql.replaceFirst("&", " AND (AA.OWN_PRICE = " + ownPrice + " OR AA.OWN_PRICE = " + (-ownPrice) + ") ");
//            sql = sql.replaceFirst("&", " AND A.OWN_PRICE = " + ownPrice);
            sql = sql.replaceFirst("&", " AND (A.OWN_PRICE = " + ownPrice + " OR A.OWN_PRICE = " + (-ownPrice) + ") ");
        } else {
            sql = sql.replaceFirst("&", "");
            sql = sql.replaceFirst("&", "");
        }
        if (!StringUtil.isNullString(orderCode)) {
            sql = sql.replaceFirst("#", " AND A.ORDER_CODE = '" + orderCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (arAmt != 0) {
//            sql = sql.replaceFirst("#", " AND A.AR_AMT = " + arAmt);
            sql = sql.replaceFirst("#", " AND (A.AR_AMT = " + arAmt + " OR A.AR_AMT = " + (-arAmt) + ") ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (!StringUtil.isNullString(rexpCode)) {
            sql = sql.replaceFirst("#", " AND A.REXP_CODE = '" + rexpCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
//        System.out.println("------------------------查询门诊医疗统计病患信息sql------------------" + sql);
        return new TParm(TJDODBTool.getInstance().select(sql));
    }

    /**
     * 查询健检医疗统计病患信息
     * 
     * @param parm
     * @return
     */
    public TParm onQueryHRMPatList(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        String orderCode = parm.getValue("ORDER_CODE"); // 医嘱码
        double ownPrice = parm.getDouble("OWN_PRICE"); // 单价add by wanglong 20130805
        double ownAmt = parm.getDouble("OWN_AMT"); // 应收金额
        double arAmt = parm.getDouble("AR_AMT"); // 单价
        String rexpCode = parm.getValue("REXP_CODE"); // 费用类别
        String sql =
                "SELECT DISTINCT B.REPORT_DATE ADM_DATE,B.MR_NO,C.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC,A.AR_AMT,A.DISPENSE_QTY QTY "
                        + "  FROM (SELECT C.*, C.TOT_OWN_AMT / C.DISPENSE_QTY OWN_AMT, C.TOT_AR_AMT / C.DISPENSE_QTY AR_AMT "
                        + "          FROM (SELECT DISTINCT A.CASE_NO,B.ORDER_CODE,B.ORDER_DESC,A.DISCOUNT_RATE,A.TOT_OWN_AMT,A.DISPENSE_QTY,A.TOT_AR_AMT,B.REXP_CODE "
                        + "                  FROM (SELECT A.CASE_NO, A.ORDERSET_CODE, A.DISCOUNT_RATE, SUM(A.OWN_AMT) TOT_OWN_AMT, "
                        + "                                SUM(A.DISPENSE_QTY) DISPENSE_QTY, SUM(A.AR_AMT) TOT_AR_AMT "
                        + "                          FROM (SELECT A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
                        + "                                       SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE  THEN  A.DISCOUNT_RATE ELSE  0 END) DISCOUNT_RATE,"
                        + "                                       SUM(CASE WHEN A.ORDER_CODE =  A.ORDERSET_CODE THEN  A.DISPENSE_QTY ELSE  0  END) DISPENSE_QTY,"
                        + "                                       SUM(A.AR_AMT) AR_AMT,SUM(A.OWN_AMT) OWN_AMT "
                        + "                                  FROM HRM_ORDER A, HRM_PATADM B "
                        + "                                 WHERE A.CASE_NO = B.CASE_NO "
                        + "                                   AND B.REPORT_DATE IS NOT NULL"
                        + "                                   ? "
                        + "                                   AND A.ORDER_DATE BETWEEN TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                                                        AND TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                                GROUP BY A.CASE_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE) A "
                        + "                           WHERE 1=1 & "//add by wanglong 20130805
                        + "                        GROUP BY A.CASE_NO, A.ORDERSET_CODE, A.DISCOUNT_RATE "
                        + "                        HAVING SUM(A.DISPENSE_QTY) <> 0) A, HRM_ORDER B "
                        + "                 WHERE A.CASE_NO = B.CASE_NO "
                        + "                   AND A.ORDERSET_CODE = B.ORDER_CODE) C) A, HRM_PATADM B, SYS_PATINFO C "
                        + " WHERE A.CASE_NO = B.CASE_NO "
                        + "   AND B.MR_NO = C.MR_NO     "
                        + "   #     #     #     #       "
                        + "ORDER BY B.REPORT_DATE       ";
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("\\?", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("\\?", "");
        }
        sql = sql.replaceFirst("@", startDate);
        sql = sql.replaceFirst("@", endDate);
        
        
        if (ownPrice != 0) {// add by wanglong 20130805
            sql = sql.replaceFirst("&", " AND A.OWN_AMT/A.DISPENSE_QTY = " + ownPrice);
        } else {
            sql = sql.replaceFirst("&", "");
        }
        if (!StringUtil.isNullString(orderCode)) {
            sql = sql.replaceFirst("#", " AND A.ORDER_CODE = '" + orderCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (ownAmt != 0) {
            sql = sql.replaceFirst("#", " AND A.OWN_AMT = " + ownAmt);
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (arAmt != 0) {
            sql = sql.replaceFirst("#", " AND A.AR_AMT = " + arAmt);
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (!StringUtil.isNullString(rexpCode)) {
            sql = sql.replaceFirst("#", " AND A.REXP_CODE = '" + rexpCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
//        System.out.println("------------------------查询健检医疗统计病患信息sql------------------" + sql);
        return new TParm(TJDODBTool.getInstance().select(sql));
    }

    /**
     * 查询住院医疗统计病患信息
     * 
     * @param parm
     * @return
     */
    public TParm onQueryODIPatList(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        String orderCode = parm.getValue("ORDER_CODE"); // 医嘱码
        double ownPrice = parm.getDouble("OWN_PRICE"); // 单价add by wanglong 20130805
        double arAmt = parm.getDouble("AR_AMT"); // 实收金额
        String rexpCode = parm.getValue("REXP_CODE"); // 费用类别
        String sql =
                "WITH A AS (SELECT DISTINCT C.*, C.TOT_AMT / C.DISPENSE_QTY AR_AMT, E.REXP_CODE "
                        + "   FROM (SELECT D.CASE_NO, D.ORDER_CODE, D.ORDER_DESC, D.DISCOUNT_RATE,"
                        + "                SUM(D.TOT_AMT) TOT_AMT, SUM(D.DISPENSE_QTY) DISPENSE_QTY "
                        + "           FROM (SELECT AA.CASE_NO, B.ORDER_CODE, B.ORDER_CHN_DESC ORDER_DESC, "
                        + "                        AA.DISCOUNT_RATE, SUM(AA.TOT_AMT) TOT_AMT, SUM(AA.DISPENSE_QTY) DISPENSE_QTY "
                        + "                   FROM (SELECT A.CASE_NO,A.ORDER_NO,A.ORDERSET_GROUP_NO, A.CASE_NO_SEQ,A.ORDERSET_CODE,"
                        + "                                SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.OWN_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                                SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DOSAGE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                                SUM(A.DOSAGE_QTY*A.OWN_PRICE) OWN_PRICE, "//add by wanglong 20130805
                        + "                                SUM(A.TOT_AMT) TOT_AMT "
                        + "                           FROM IBS_ORDD A "
                        + "                          WHERE A.BILL_FLG = 'Y' "
                        + "                            AND A.ORDERSET_CODE IS NOT NULL "
                        + "                            ? "
                        + "                            AND A.BILL_DATE BETWEEN TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                                                AND TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                       GROUP BY A.CASE_NO, A.ORDER_NO, A.ORDERSET_GROUP_NO, A.CASE_NO_SEQ, A.ORDERSET_CODE) AA, IBS_ORDD B "
                        + "                  WHERE AA.CASE_NO = B.CASE_NO "
                        + "                    AND AA.ORDER_NO = B.ORDER_NO "
                        + "                    AND AA.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO "
                        + "                    AND AA.CASE_NO_SEQ = B.CASE_NO_SEQ "
                        + "                    AND AA.ORDERSET_CODE = B.ORDER_CODE "
                        + "                    & "//add by wanglong 20130805
                        + "               GROUP BY AA.CASE_NO, B.ORDER_CODE, B.ORDER_CHN_DESC, AA.DISCOUNT_RATE, AA.TOT_AMT "
                        + "                 HAVING SUM(AA.DISPENSE_QTY) <> 0) D "
                        + "       GROUP BY D.CASE_NO, D.ORDER_CODE, D.ORDER_DESC, D.DISCOUNT_RATE "
                        + "          UNION "
                        + "         SELECT A.CASE_NO,A.ORDER_CODE,A.ORDER_CHN_DESC ORDER_DESC,"
                        + "                CASE WHEN A.OWN_RATE = 0 THEN 1 ELSE A.OWN_RATE END DISCOUNT_RATE,"
                        + "                SUM(A.TOT_AMT) TOT_AMT,SUM(A.DOSAGE_QTY) DISPENSE_QTY "
                        + "           FROM IBS_ORDD A "
                        + "          WHERE A.BILL_FLG = 'Y' "
                        + "            AND A.ORDERSET_CODE IS NULL "
                        + "            ? "
                        + "            AND A.BILL_DATE BETWEEN TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "                                AND TO_DATE('@', 'YYYYMMDDHH24MISS') "
                        + "            & "//add by wanglong 20130805
                        + "       GROUP BY A.CASE_NO, A.ORDER_CODE, A.ORDER_CHN_DESC, A.OWN_RATE) C, IBS_ORDD E "
                        + "  WHERE C.DISPENSE_QTY <> 0     "
                        + "    AND C.CASE_NO = E.CASE_NO   "
                        + "    AND C.ORDER_CODE = E.ORDER_CODE) "
                        + "SELECT B.IN_DATE ADM_DATE, B.MR_NO, C.PAT_NAME, A.ORDER_CODE, A.ORDER_DESC,A.AR_AMT, A.DISPENSE_QTY QTY "
                        + "  FROM A, ADM_INP B, SYS_PATINFO C " 
                        + " WHERE A.CASE_NO = B.CASE_NO       "
                        + "   AND B.MR_NO = C.MR_NO           " 
                        + "   #           #           #       "
                        + "ORDER BY B.IN_DATE                 ";
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("\\?", " AND A.EXE_DEPT_CODE = '" + deptCode + "' ");
            sql = sql.replaceFirst("\\?", " AND A.EXE_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("\\?", "");
            sql = sql.replaceFirst("\\?", "");
        }
        sql = sql.replaceFirst("@", startDate);
        sql = sql.replaceFirst("@", endDate);
        sql = sql.replaceFirst("@", startDate);
        sql = sql.replaceFirst("@", endDate);
        if (ownPrice != 0) {// add by wanglong 20130805
//            sql = sql.replaceFirst("&", " AND AA.OWN_PRICE = " + ownPrice);
            sql = sql.replaceFirst("&", " AND (AA.OWN_PRICE = " + ownPrice + " OR AA.OWN_PRICE = " + (-ownPrice) + ") ");
//            sql = sql.replaceFirst("&", " AND A.OWN_PRICE = " + ownPrice);
            sql = sql.replaceFirst("&", " AND (A.OWN_PRICE = " + ownPrice + " OR A.OWN_PRICE = " + (-ownPrice) + ") ");
        } else {
            sql = sql.replaceFirst("&", "");
            sql = sql.replaceFirst("&", "");
        }
        if (!StringUtil.isNullString(orderCode)) {
            sql = sql.replaceFirst("#", " AND A.ORDER_CODE = '" + orderCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (arAmt != 0) {
//            sql = sql.replaceFirst("#", " AND A.AR_AMT = " + arAmt);
            sql = sql.replaceFirst("#", " AND (A.AR_AMT = " + arAmt + " OR A.AR_AMT = " + (-arAmt) + ") ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (!StringUtil.isNullString(rexpCode)) {
            sql = sql.replaceFirst("#", " AND A.REXP_CODE = '" + rexpCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
//        System.out.println("------------------------查询住院医疗统计病患信息sql------------------" + sql);
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    
    
    
    // ====================排序功能begin======================add by wanglong 20130613
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
}
