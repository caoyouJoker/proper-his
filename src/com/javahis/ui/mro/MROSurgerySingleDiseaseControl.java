package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p> Title: 外科单病种查询 </p>
 * 
 * <p> Description: 外科单病种查询 </p>
 * 
 * <p> Copyright: ProperSoft </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 20141223
 * @version 1.0
 */
public class MROSurgerySingleDiseaseControl
        extends TControl {

    private TTable table;
    private BILComparator compare = new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;

    /**
     * 初始化
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        addSortListener(table);
        OpList opList = new OpList();
        DiagList diagList = new DiagList();
        table.addItem("OpList", opList);
        table.addItem("DiagList", diagList);
        this.onClear();
    }

    /**
     * 查询操作
     */
    public void onQuery() {
        if ("".equals(this.getValue("START_DATE")) || this.getValue("START_DATE") == null) {
            this.messageBox("开始时间不能为空");
            return;
        } else if ("".equals(this.getValue("END_DATE")) || this.getValue("END_DATE") == null) {
            this.messageBox("结束时间不能为空");
            return;
        }
        String startDate =
                StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")),
                                     "yyyyMMddHHmmss");
        String endDate =
                StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
        String sql =
                "WITH AA AS (SELECT CASE_NO, MAX(CHG_DATE) - MIN(CHG_DATE) DIFF_DATE "
                        + "         FROM (SELECT DISTINCT B.CASE_NO, B.SEQ_NO, B.DEPT_CODE, B.CHG_DATE, "
                        + "                      DENSE_RANK() OVER (ORDER BY B.CASE_NO, B.SEQ_NO) SEQ "
                        + "                 FROM ADM_INP A, ADM_CHG B, SYS_DEPT C, ADM_INPDIAG D, MRO_RECORD_OP E "
                        + "                WHERE A.CASE_NO = B.CASE_NO "
                        + "                  AND B.DEPT_CODE = C.DEPT_CODE "
                        + "                  AND C.ICU_TYPE IS NOT NULL "
                        + "                  AND A.DS_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "                                    AND TO_DATE( '@', 'YYYYMMDDHH24MISS') "
                        + "                  AND A.CASE_NO = D.CASE_NO "
                        + "                  AND D.IO_TYPE = 'O' "
                        + "                  AND A.CASE_NO = E.CASE_NO   !   &   ) D "
                        + "       GROUP BY D.CASE_NO, D.DEPT_CODE, D.SEQ - D.SEQ_NO), "
                        + "BB AS (SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, A.DEPT_CODE, A.STATION_CODE, "
                        + "              FLOOR(MONTHS_BETWEEN( A.DS_DATE, B.BIRTH_DATE) / 12) AS AGE, A.IN_DATE, A.DS_DATE, "
                        + "              (SELECT D.ICD_CODE "
                        + "                 FROM ADM_INPDIAG D "
                        + "                WHERE A.CASE_NO = D.CASE_NO "
                        + "                  AND D.IO_TYPE = 'O' "
                        + "                  AND D.MAINDIAG_FLG = 'Y' "
                        + "                  AND ROWNUM=1) DIAG_CODE1, "
                        + "              (SELECT WM_CONCAT(ICD_CHN_DESC) "
                        + "                 FROM (SELECT D.CASE_NO, D.ICD_CODE, E.ICD_CHN_DESC "
                        + "                         FROM ADM_INPDIAG D, SYS_DIAGNOSIS E "
                        + "                        WHERE D.IO_TYPE = 'O' "
                        + "                          AND D.MAINDIAG_FLG <> 'Y' "
                        + "                          AND D.ICD_CODE = E.ICD_CODE "
                        + "                     ORDER BY D.SEQ_NO) F "
                        + "               WHERE F.CASE_NO = A.CASE_NO) DIAG_CODE2, "
                        + "              C.OP_CODE, C.MAIN_SUGEON, C.AST_DR1, C.ANA_DR, "
                        + "              (SELECT MIN(D.CHG_DATE) "
                        + "                 FROM ADM_CHG D, SYS_DEPT E "
                        + "                WHERE D.DEPT_CODE = E.DEPT_CODE "
                        + "                  AND E.ICU_TYPE IS NOT NULL "
                        + "                  AND A.CASE_NO = D.CASE_NO) IN_ICU_DATE, "
                        + "              (SELECT MAX(D.CHG_DATE) "
                        + "                 FROM ADM_CHG D, SYS_DEPT E "
                        + "                WHERE D.DEPT_CODE = E.DEPT_CODE "
                        + "                  AND E.ICU_TYPE IS NOT NULL "
                        + "                  AND A.CASE_NO = D.CASE_NO) OUT_ICU_DATE "
                        + "         FROM ADM_INP A, SYS_PATINFO B, MRO_RECORD_OP C "
                        + "        WHERE A.MR_NO = B.MR_NO "
                        + "          AND A.CASE_NO = C.CASE_NO "
                        + "          AND C.MAIN_FLG = 'Y' "
                        + "          AND A.DS_DATE BETWEEN TO_DATE( '#', 'YYYYMMDDHH24MISS')  "
                        + "                            AND TO_DATE( '@', 'YYYYMMDDHH24MISS')) "
                        + "SELECT BB.CASE_NO, BB.MR_NO, BB.PAT_NAME, BB.SEX_CODE, BB.DEPT_CODE, BB.STATION_CODE, BB.AGE,"
                        + "       BB.DIAG_CODE1, BB.DIAG_CODE2, BB.OP_CODE, BB.MAIN_SUGEON, BB.AST_DR1, BB.ANA_DR,"
                        + "       BB.IN_ICU_DATE, BB.OUT_ICU_DATE, FLOOR(SUM(AA.DIFF_DATE) * 24) || '小时' STAY_HOURS,"
                        + "       TRUNC(SUM(AA.DIFF_DATE)) || '天' || TRUNC((SUM(AA.DIFF_DATE) - FLOOR(SUM(AA.DIFF_DATE))) * 24) || '小时' STAY_DAYS "
                        + "  FROM AA, BB "
                        + " WHERE AA.CASE_NO = BB.CASE_NO "
                        + "GROUP BY BB.CASE_NO, BB.MR_NO, BB.PAT_NAME, BB.SEX_CODE, BB.DEPT_CODE, BB.STATION_CODE, "
                        + "         BB.AGE, BB.DIAG_CODE1, BB.DIAG_CODE2, BB.OP_CODE, BB.MAIN_SUGEON, BB.AST_DR1, "
                        + "         BB.ANA_DR, BB.IN_ICU_DATE, BB.OUT_ICU_DATE, BB.IN_DATE, BB.DS_DATE "
                        + "ORDER BY BB.DS_DATE";
        sql = sql.replaceAll("#", startDate);
        sql = sql.replaceAll("@", endDate);
        ArrayList<String> diagList = new ArrayList<String>();
        if (this.getValueString("DIAG1").equals("Y")) {// 按诊断
            diagList.add("Q25.0");
        }
        if (this.getValueString("DIAG2").equals("Y")) {
            diagList.add("Q21.1");
        }
        if (this.getValueString("DIAG3").equals("Y")) {
            diagList.add("Q21.0");
        }
        if (this.getValueString("DIAG4").equals("Y")) {
            diagList.add("Q21.3");
            diagList.add("Q21.805");
        }
        switch (diagList.size()) {
            case 0:
                sql = sql.replaceFirst("!", "");
                break;
            case 1:
                sql =
                        sql.replaceFirst("!",
                                         " AND D.ICD_CODE LIKE '#%' ".replaceFirst("#",
                                                                                   diagList.get(0)));
                break;
            case 2:
                sql =
                        sql.replaceFirst("!",
                                         " AND (D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%') "
                                                 .replaceFirst("#", diagList.get(0))
                                                 .replaceFirst("#", diagList.get(1)));
                break;
            case 3:
                sql =
                        sql.replaceFirst("!",
                                         " AND (D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%') "
                                                 .replaceFirst("#", diagList.get(0))
                                                 .replaceFirst("#", diagList.get(1))
                                                 .replaceFirst("#", diagList.get(2)));
                break;
            case 4:
                sql =
                        sql.replaceFirst("!",
                                         " AND (D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%') "
                                                 .replaceFirst("#", diagList.get(0))
                                                 .replaceFirst("#", diagList.get(1))
                                                 .replaceFirst("#", diagList.get(2))
                                                 .replaceFirst("#", diagList.get(3)));
                break;
            case 5:
                sql =
                        sql.replaceFirst("!",
                                         " AND (D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%' OR D.ICD_CODE LIKE '#%') "
                                                 .replaceFirst("#", diagList.get(0))
                                                 .replaceFirst("#", diagList.get(1))
                                                 .replaceFirst("#", diagList.get(2))
                                                 .replaceFirst("#", diagList.get(3))
                                                 .replaceFirst("#", diagList.get(4)));
                break;
        }
        ArrayList<String> opList = new ArrayList<String>();
        if (this.getValueString("OP1").equals("Y")) {// 按术式
            opList.add("36.1");
        }
        if (this.getValueString("OP2").equals("Y")) {
            opList.add("35.2");
        }
        switch (opList.size()) {
            case 0:
                sql = sql.replaceFirst("&", "");
                break;
            case 1:
                sql =
                        sql.replaceFirst("&",
                                         " AND E.OP_CODE LIKE '#%' ".replaceFirst("#",
                                                                                  opList.get(0)));
                break;
            case 2:
                sql =
                        sql.replaceFirst("&", " AND (E.OP_CODE LIKE '#%' OR E.OP_CODE LIKE '#%') "
                                .replaceFirst("#", opList.get(0)).replaceFirst("#", opList.get(1)));
                break;
        }
//        System.out.println("----------------外科手术---------" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getErrCode()<0){
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("没有要查询的数据");
            table.removeRowAll();
            return;
        }
        this.setValue("COUNT", result.getCount()+"");
        table.setParmValue(result);
    }

    /**
     * 打印
     */
    public void onPrint() {
        TParm result = new TParm();
        // Timestamp now = SystemTool.getInstance().getDate();
        String startDate =
                StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")),
                                     "yyyy/MM/dd HH:mm:ss");
        String endDate =
                StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")),
                                     "yyyy/MM/dd HH:mm:ss");
        result.setData("START_END_DATE", "TEXT", startDate + "～" + endDate);
        result.setData("PRINT_USER", Operator.getName());
        TParm parmValue = table.getShowParmValue();
        parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
        parmValue.addData("SYSTEM", "COLUMNS", "STATION_CODE");
        parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
        parmValue.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parmValue.addData("SYSTEM", "COLUMNS", "SEX_CODE");
        parmValue.addData("SYSTEM", "COLUMNS", "AGE");
        parmValue.addData("SYSTEM", "COLUMNS", "DIAG_CODE1");
        parmValue.addData("SYSTEM", "COLUMNS", "DIAG_CODE2");
        parmValue.addData("SYSTEM", "COLUMNS", "OP_CODE");
        parmValue.addData("SYSTEM", "COLUMNS", "MAIN_SUGEON");
        parmValue.addData("SYSTEM", "COLUMNS", "AST_DR1");
        parmValue.addData("SYSTEM", "COLUMNS", "ANA_DR");
        parmValue.addData("SYSTEM", "COLUMNS", "IN_ICU_DATE");
        parmValue.addData("SYSTEM", "COLUMNS", "OUT_ICU_DATE");
        parmValue.addData("SYSTEM", "COLUMNS", "STAY_HOURS");
        parmValue.addData("SYSTEM", "COLUMNS", "STAY_DAYS");
        result.setData("TABLE", parmValue.getData());
        this.openPrintDialog("%ROOT%\\config\\prt\\MRO\\MROSurgerySingleDisease.jhw", result, false);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有要汇出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "外科病种查询");
    }

    /**
     * 清空操作
     */
    public void onClear() {
        this.clearValue("DIAG1;DIAG2;DIAG3;DIAG4;OP1;OP2;COUNT");
        Timestamp now =
                StringTool.getTimestamp(StringTool.getString(SystemTool.getInstance().getDate(),
                                                             "yyyyMMdd") + "235959",
                                        "yyyyMMddHHmmss");
        Timestamp lastweek =
                StringTool.getTimestamp(StringTool.getString(StringTool.rollDate(now, -7),
                                                             "yyyyMMdd") + "000000",
                                        "yyyyMMddHHmmss");// 上周
        this.setValue("START_DATE", lastweek);
        this.setValue("END_DATE", now);
        table.removeRowAll();
    }

    /**
     * 诊断CODE替换中文 模糊查询（内部类）
     */
    public class DiagList
            extends TLabel {

        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

        public String getTableShowValue(String s) {
            if (dataStore == null) return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i))) return "" + d.get(i);
            }
            return s;
        }
    }

    /**
     * 手术CODE替换中文 模糊查询（内部类）
     */
    public class OpList
            extends TLabel {

        TDataStore dataStore = new TDataStore();

        public OpList() {
            dataStore.setSQL("SELECT * FROM SYS_OPERATIONICD");
            dataStore.retrieve();
        }

        public String getTableShowValue(String s) {
            if (dataStore == null) return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("OPERATION_ICD");
            Vector d = (Vector) parm.getData("OPT_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i))) return "" + d.get(i);
            }
            return s;
        }
    }

    // ====================排序功能begin======================add by wanglong 20121217
    /**
     * 加入表格排序监听方法
     * 
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
     * 
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
        if (size > 0 && count > size) count = size;
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
     * 
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
     * 
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames,
                                   final TTable table) {
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
