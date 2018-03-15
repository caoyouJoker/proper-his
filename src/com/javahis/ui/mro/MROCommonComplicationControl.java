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
 * <p> Title:��������֢ͳ�Ʊ� </p>
 * 
 * <p> Description:��������֢ͳ�Ʊ� </p>
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
     * ��ʼ��
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        addSortListener(table);
        OrderList orderList = new OrderList();
        table.addItem("OrderList", orderList);
        initPage();
    }

    /**
     * ��ʼ������
     */
    private void initPage() {
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("START_DATE", StringTool.rollDate(now, -7));
        setValue("END_DATE", now);
        setValue("DEPT_CODE", Operator.getDept());
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            this.setValue("MR_NO", mrNo);// ������
         // modify by huangtt 20160929 EMPI���߲�����ʾ start
            Pat pat = Pat.onQueryByMrNo(mrNo);
            if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
    			this.setValue("MR_NO", pat.getMrNo());// ������
    		}	
         // modify by huangtt 20160929 EMPI���߲�����ʾ end
            
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String startDate = this.getText("START_DATE");
        String endDate = this.getText("END_DATE");
        if (startDate.equals("") || endDate.equals("")) {
            this.messageBox("��ѡ��ʱ���");
            return;
        }
        String deptCode = this.getValueString("DEPT_CODE");
        String mrNo = this.getValueString("MR_NO").trim();
        String sql =
                "SELECT A.CASE_NO, A.OUT_DEPT, "
                        + "       (SELECT REPLACE( WM_CONCAT(B.DEPT_CHN_DESC), ',', '��') "
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
                        + "                 WHERE C.IN_DEPT_CODE IN ('0303')) B "// ICU������д����
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
                        + "                 WHERE C.IN_DEPT_CODE IN ('0304')) B "// CCU������д����
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
            this.messageBox("��ѯʧ�� " + result.getErrText());
            return;
        }
        int maxOPNum = 0;// ��¼������������
        int maxICDNum = 0;// ��¼�����������
        for (int i = 0; i < result.getCount(); i++) {
            String opSql =
                    "SELECT * FROM MRO_RECORD_OP WHERE CASE_NO='#'".replaceFirst("#", result
                            .getValue("CASE_NO", i));
            TParm opParm = new TParm(TJDODBTool.getInstance().select(opSql));
            if (opParm.getErrCode() < 0) {
                this.messageBox("��ѯʧ�� " + opParm.getErrText());
                return;
            }
            if (maxOPNum < opParm.getCount()) {
                maxOPNum = opParm.getCount();
            }
            int j = 0;
            for (; j < opParm.getCount(); j++) {
                if (opParm.getValue("MAIN_FLG", j).equals("Y")) {
                    result.setData("OP_DATE", i, opParm.getData("OP_DATE", j));// ����ʱ��
                    result.setData("MAIN_SUGEON", i, opParm.getData("MAIN_SUGEON", j));// ����
                    result.setData("AST_DR1", i, opParm.getData("AST_DR1", j));// һ��
                    result.setData("ANA_DR", i, opParm.getData("ANA_DR", j));// ������ʦ
                    result.setData("PFT_DR", i, "");// ����עʦ
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
                            .replaceFirst("#", result.getValue("CASE_NO", i));// �鲢��֢���
            TParm diagParm = new TParm(TJDODBTool.getInstance().select(diagSql));
            if (diagParm.getErrCode() < 0) {
                this.messageBox("��ѯʧ�� " + diagParm.getErrText());
                return;
            }
            if (maxICDNum < diagParm.getCount()) {
                maxICDNum = diagParm.getCount();
            }
            int k = 0;
            for (; k < diagParm.getCount(); k++) {
                result.setData("ICD_DESC" + (k + 1), i, diagParm.getData("ICD_DESC", k));// ����֢
                result.setData("ICD_CODE" + (k + 1), i, diagParm.getData("ICD_CODE", k));// ����
                result.setData("ICD_STATUS" + (k + 1), i, diagParm.getData("ICD_STATUS", k));// Ԥ��ת�飩
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
                "��Ժ����,90,DEPT_CODE;ת�����,250;������,100;����,80;�Ա�,40;����,40;��Ժ����,90,timestamp,yyyy/MM/dd;��Ժ����,90,timestamp,yyyy/MM/dd;סԺ����,60;סICU����,75;סCCU����,75;�����,150,OrderList;����ʱ��,90,timestamp,yyyy/MM/dd;����,80,OPT_USER;һ��,80,OPT_USER;������ʦ,80,OPT_USER;����עʦ,80;";
        String horizontalAlignment =
                "0,left;1,left;3,left;4,left;5,right;8,right;9,right;10,right;11,left;13,left;14,left;15,left;16,left";
        String parmMap =
                "OUT_DEPT;TRANS_LOG;MR_NO;PAT_NAME;SEX;AGE;IN_DATE;OUT_DATE;REAL_STAY_DAYS;ICU_DAYS;CCU_DAYS;DIAG_CODE;OP_DATE;MAIN_SUGEON;AST_DR1;ANA_DR;PFT_DR;";
        for (int j = 1, k = 1; j <= maxOPNum; j++, k = k + 2) {
            header += "��ʽ" + j + ",150;����,75;";
            parmMap += "OP_DESC" + j + ";OP_CODE" + j + ";";
            horizontalAlignment += (16 + k) + ",left;" + (16 + k + 1) + ",left;";
        }
        for (int j = 1, k = 1; j <= maxICDNum; j++, k = k + 3) {
            header += "����֢" + j + ",80;����,80;Ԥ��,80,STATUS;";
            parmMap += "ICD_CODE" + j + ";ICD_DESC" + j + ";ICD_STATUS" + j + ";";
            horizontalAlignment +=
                    (16 + 2 * maxOPNum + k) + ",left;" + (16 + 2 * maxOPNum + k + 1) + ",left;"
                            + (16 + 2 * maxOPNum + k + 2) + ",left;";
        }
        header += "��Ժ״̬,90,OUT_TYPE";
        parmMap += "OUT_TYPE";
        horizontalAlignment += (16 + 2 * maxOPNum + 3 * maxOPNum + 1) + ",left";
        table.setHeader(header);
        table.setColumnHorizontalAlignmentData(horizontalAlignment);
        table.setParmMap(parmMap);
        table.setParmValue(result);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        ExportExcelUtil.getInstance().exportExcel(table, "��������֢ͳ�Ʊ���");
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("DEPT_CODE;MR_NO");
        table.setDSValue();
        initPage();
    }

    /**
     * ���CODE�滻���� ģ����ѯ���ڲ��ࣩ
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

    // ====================������begin======================
    /**
     * �����������������
     * 
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// ȡ�ñ��е�����
                String columnName[] = tableData.getNames("Data");// �������
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // ������������;
                int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * �����������ݣ���TParmתΪVector
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
     * ����ָ���������������е�index
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
     * �����������ݣ���Vectorת��Parm
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
    // ====================������end======================
}
