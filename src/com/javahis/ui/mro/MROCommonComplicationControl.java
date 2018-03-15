package com.javahis.ui.mro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title:常见并发症统计表 </p>
 * 
 * <p> Description:常见并发症统计表 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 2014.07.24
 * @version 1.0
 */

public class MROCommonComplicationControl extends TControl {
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
        OrderList orderList = new OrderList();
        table.addItem("OrderList", orderList);
        initPage();
    }

    /**
     * 初始化界面
     */
    private void initPage() {
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("START_DATE", StringTool.rollDate(now, -7));
        setValue("END_DATE", now);
        setValue("DEPT_CODE", Operator.getDept());
    }

    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            this.setValue("MR_NO", mrNo);// 病案号
         // modify by huangtt 20160929 EMPI患者查重提示 start
            Pat pat = Pat.onQueryByMrNo(mrNo);
            if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
    			this.setValue("MR_NO", pat.getMrNo());// 病案号
    		}	
         // modify by huangtt 20160929 EMPI患者查重提示 end
            
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE");
        String endDate = this.getText("END_DATE");
        if (startDate.equals("") || endDate.equals("")) {
            this.messageBox("请选择时间段");
            return;
        }
        String deptCode = this.getValueString("DEPT_CODE");
        String mrNo = this.getValueString("MR_NO").trim();
        String sql =
                "SELECT A.CASE_NO, A.OUT_DEPT, "
                        + "       (SELECT REPLACE( WM_CONCAT(B.DEPT_CHN_DESC), ',', '→') "
                        + "          FROM (SELECT C.CASE_NO, D.DEPT_CHN_DESC "
                        + "                  FROM ADM_TRANS_LOG C, SYS_DEPT D "
                        + "                 WHERE C.IN_DEPT_CODE = D.DEPT_CODE "
                        + "                ORDER BY C.CASE_NO, C.IN_DATE, C.OUT_DATE) B "
                        + "         WHERE B.CASE_NO = A.CASE_NO "
                        + "        GROUP BY CASE_NO) AS TRANS_LOG, "
                        + "       A.MR_NO, A.PAT_NAME, A.SEX, FLOOR(MONTHS_BETWEEN( SYSDATE, A.BIRTH_DATE) / 12) AS AGE, "
                        + "       A.IN_DATE, A.OUT_DATE, "
                        + "       CASE WHEN TRUNC(A.OUT_DATE) - TRUNC(A.IN_DATE) < 1 THEN 1 "
                        + "            ELSE TRUNC(A.OUT_DATE) - TRUNC(A.IN_DATE) END AS REAL_STAY_DAYS, "
                        + "       (SELECT SUM(B.DAYS) "
                        + "          FROM (SELECT C.CASE_NO, "
                        + "                       CASE WHEN C.OUT_DATE IS NULL "
                        + "                             AND TRUNC(SYSDATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS') ) < 1 THEN 1 "
                        + "                            WHEN C.OUT_DATE IS NULL "
                        + "                            THEN TRUNC(SYSDATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS')) "
                        + "                            WHEN C.OUT_DATE IS NOT NULL "
                        + "                             AND TRUNC(C.OUT_DATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS')) < 1 THEN 1 "
                        + "                            ELSE TRUNC(C.OUT_DATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS')) "
                        + "                       END AS DAYS "
                        + "                  FROM ADM_TRANS_LOG C "
                        + "                 WHERE C.IN_DEPT_CODE IN ('0303')) B "// ICU，科室写死了
                        + "         WHERE B.CASE_NO = A.CASE_NO "
                        + "        GROUP BY B.CASE_NO) "
                        + "           AS ICU_DAYS, "
                        + "       (SELECT SUM(B.DAYS) "
                        + "          FROM (SELECT C.CASE_NO, "
                        + "                       CASE WHEN C.OUT_DATE IS NULL "
                        + "                             AND TRUNC(SYSDATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS') ) < 1 THEN 1 "
                        + "                            WHEN C.OUT_DATE IS NULL "
                        + "                            THEN TRUNC(SYSDATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS')) "
                        + "                            WHEN C.OUT_DATE IS NOT NULL "
                        + "                             AND TRUNC(C.OUT_DATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS')) < 1 THEN 1 "
                        + "                            ELSE TRUNC(C.OUT_DATE) - TRUNC(TO_DATE( C.IN_DATE, 'YYYY/MM/DD HH24:MI:SS')) "
                        + "                       END AS DAYS "
                        + "                  FROM ADM_TRANS_LOG C "
                        + "                 WHERE C.IN_DEPT_CODE IN ('0304')) B "// CCU，科室写死了
                        + "         WHERE B.CASE_NO = A.CASE_NO "
                        + "        GROUP BY B.CASE_NO) AS CCU_DAYS, "
                        + "       A.OUT_DIAG_CODE1 AS DIAG_CODE, A.OUT_TYPE "
                        + "  FROM MRO_RECORD A "
                        + " WHERE A.OUT_DATE IS NOT NULL "
                        + "   AND A.OUT_DATE BETWEEN TO_DATE( '#', 'YYYY/MM/DD') AND TO_DATE( '#', 'YYYY/MM/DD')  @  &  "
                        + "ORDER BY A.OUT_DEPT, A.OUT_DATE";
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
        if (!deptCode.equals("")) {
            sql = sql.replaceFirst("@", " AND A.OUT_DEPT = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!mrNo.equals("")) {
            sql = sql.replaceFirst("&", "  AND A.MR_NO = '" + mrNo + "' ");
        } else {
            sql = sql.replaceFirst("&", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("查询失败 " + result.getErrText());
            return;
        }
        int maxOPNum = 0;// 记录最大的手术数量
        int maxICDNum = 0;// 记录最大的诊断数量
        for (int i = 0; i < result.getCount(); i++) {
            String opSql =
                    "SELECT * FROM MRO_RECORD_OP WHERE CASE_NO='#'".replaceFirst("#", result
                            .getValue("CASE_NO", i));
            TParm opParm = new TParm(TJDODBTool.getInstance().select(opSql));
            if (opParm.getErrCode() < 0) {
                this.messageBox("查询失败 " + opParm.getErrText());
                return;
            }
            if (maxOPNum < opParm.getCount()) {
                maxOPNum = opParm.getCount();
            }
            int j = 0;
            for (; j < opParm.getCount(); j++) {
                if (opParm.getValue("MAIN_FLG", j).equals("Y")) {
                    result.setData("OP_DATE", i, opParm.getData("OP_DATE", j));// 手术时间
                    result.setData("MAIN_SUGEON", i, opParm.getData("MAIN_SUGEON", j));// 术者
                    result.setData("AST_DR1", i, opParm.getData("AST_DR1", j));// 一助
                    result.setData("ANA_DR", i, opParm.getData("ANA_DR", j));// 主麻醉师
                    result.setData("PFT_DR", i, "");// 主灌注师
                }
                result.setData("OP_DESC" + (j + 1), i, opParm.getData("OP_DESC", j));
                result.setData("OP_CODE" + (j + 1), i, opParm.getData("OP_CODE", j));
            }
            for (; j < 10; j++) {
                result.setData("OP_DESC" + (j + 1), i, "");
                result.setData("OP_CODE" + (j + 1), i, "");
            }
            String diagSql =
                    "SELECT * FROM MRO_RECORD_DIAG WHERE CASE_NO='#' AND IO_TYPE = 'W'"
                            .replaceFirst("#", result.getValue("CASE_NO", i));// 查并发症诊断
            TParm diagParm = new TParm(TJDODBTool.getInstance().select(diagSql));
            if (diagParm.getErrCode() < 0) {
                this.messageBox("查询失败 " + diagParm.getErrText());
                return;
            }
            if (maxICDNum < diagParm.getCount()) {
                maxICDNum = diagParm.getCount();
            }
            int k = 0;
            for (; k < diagParm.getCount(); k++) {
                result.setData("ICD_DESC" + (k + 1), i, diagParm.getData("ICD_DESC", k));// 并发症
                result.setData("ICD_CODE" + (k + 1), i, diagParm.getData("ICD_CODE", k));// 编码
                result.setData("ICD_STATUS" + (k + 1), i, diagParm.getData("ICD_STATUS", k));// 预后（转归）
            }
            for (; k < 10; k++) {
                result.setData("ICD_DESC" + (k + 1), i, "");
                result.setData("ICD_CODE" + (k + 1), i, "");
                result.setData("ICD_STATUS" + (k + 1), i, "");
            }
        }
        for (int j = 10; j > maxOPNum; j--) {
            result.removeData("OP_DESC" + j);
            result.removeData("OP_CODE" + j);
        }
        for (int j = 10; j > maxICDNum; j--) {
            result.removeData("ICD_DESC" + j);
            result.removeData("ICD_CODE" + j);
            result.removeData("ICD_STATUS" + j);
        }
        String header =
                "出院科室,90,DEPT_CODE;转科情况,250;病案号,100;姓名,80;性别,40;年龄,40;入院日期,90,timestamp,yyyy/MM/dd;出院日期,90,timestamp,yyyy/MM/dd;住院天数,60;住ICU天数,75;住CCU天数,75;主诊断,150,OrderList;手术时间,90,timestamp,yyyy/MM/dd;术者,80,OPT_USER;一助,80,OPT_USER;主麻醉师,80,OPT_USER;主灌注师,80;";
        String horizontalAlignment =
                "0,left;1,left;3,left;4,left;5,right;8,right;9,right;10,right;11,left;13,left;14,left;15,left;16,left";
        String parmMap =
                "OUT_DEPT;TRANS_LOG;MR_NO;PAT_NAME;SEX;AGE;IN_DATE;OUT_DATE;REAL_STAY_DAYS;ICU_DAYS;CCU_DAYS;DIAG_CODE;OP_DATE;MAIN_SUGEON;AST_DR1;ANA_DR;PFT_DR;";
        for (int j = 1, k = 1; j <= maxOPNum; j++, k = k + 2) {
            header += "术式" + j + ",150;编码,75;";
            parmMap += "OP_DESC" + j + ";OP_CODE" + j + ";";
            horizontalAlignment += (16 + k) + ",left;" + (16 + k + 1) + ",left;";
        }
        for (int j = 1, k = 1; j <= maxICDNum; j++, k = k + 3) {
            header += "并发症" + j + ",80;编码,80;预后,80,STATUS;";
            parmMap += "ICD_CODE" + j + ";ICD_DESC" + j + ";ICD_STATUS" + j + ";";
            horizontalAlignment +=
                    (16 + 2 * maxOPNum + k) + ",left;" + (16 + 2 * maxOPNum + k + 1) + ",left;"
                            + (16 + 2 * maxOPNum + k + 2) + ",left;";
        }
        header += "出院状态,90,OUT_TYPE";
        parmMap += "OUT_TYPE";
        horizontalAlignment += (16 + 2 * maxOPNum + 3 * maxOPNum + 1) + ",left";
        table.setHeader(header);
        table.setColumnHorizontalAlignmentData(horizontalAlignment);
        table.setParmMap(parmMap);
        table.setParmValue(result);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        ExportExcelUtil.getInstance().exportExcel(table, "常见并发症统计报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("DEPT_CODE;MR_NO");
        table.setDSValue();
        initPage();
    }

    /**
     * 诊断CODE替换中文 模糊查询（内部类）
     */
    public class OrderList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }

    // ====================排序功能begin======================
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
