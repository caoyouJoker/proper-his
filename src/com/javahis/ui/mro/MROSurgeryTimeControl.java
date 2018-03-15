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
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title:�������ʱ���ͳ�� </p>
 * 
 * <p> Description:�������ʱ���ͳ�� </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 2014.07.24
 * @version 1.0
 */

public class MROSurgeryTimeControl extends TControl {
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
                "SELECT A.MR_NO, A.PAT_NAME, A.SEX, FLOOR(MONTHS_BETWEEN( SYSDATE, A.BIRTH_DATE) / 12) AS AGE, A.OUT_DEPT, "
                        + "       (SELECT REPLACE( WM_CONCAT(B.DEPT_CHN_DESC), ',', '��') "
                        + "          FROM (SELECT C.CASE_NO, D.DEPT_CHN_DESC "
                        + "                  FROM ADM_TRANS_LOG C, SYS_DEPT D "
                        + "                 WHERE C.IN_DEPT_CODE = D.DEPT_CODE "
                        + "                ORDER BY C.CASE_NO, C.IN_DATE, C.OUT_DATE) B "
                        + "         WHERE B.CASE_NO = A.CASE_NO "
                        + "        GROUP BY CASE_NO) AS TRANS_LOG, "
                        + "       A.IN_DATE, A.OUT_DATE, "
                        + "       CASE WHEN TRUNC(A.OUT_DATE) - TRUNC(A.IN_DATE) < 1 THEN 1 "
                        + "            ELSE TRUNC(A.OUT_DATE) - TRUNC(A.IN_DATE) "
                        + "        END AS REAL_STAY_DAYS, "
                        + "       TRUNC(B.OP_DATE) - TRUNC(A.IN_DATE) AS OP_IN_DAYS, "
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
                        + "        GROUP BY B.CASE_NO) AS ICU_DAYS, "
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
                        + "       TRUNC(A.OUT_DATE) - TRUNC(B.OP_DATE) AS OUT_OP_DAYS "
                        + "  FROM MRO_RECORD A, MRO_RECORD_OP B, SYS_DEPT C, ADM_INP D "
                        + " WHERE A.CASE_NO = B.CASE_NO "
                        + "   AND B.MAIN_FLG = 'Y' "//������
                        + "   AND A.OUT_DEPT = C.DEPT_CODE "
                        + "   AND C.DEPT_CAT1 = 'B' "//���
                        + "   AND A.CASE_NO = D.CASE_NO "
                        + "   AND A.OUT_DATE IS NOT NULL "
                        + "   AND A.OUT_DATE BETWEEN TO_DATE( '#', 'YYYY/MM/DD') AND TO_DATE( '#', 'YYYY/MM/DD') "
                        + "   @  &   ORDER BY A.OUT_DEPT, A.OUT_DATE";
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
        if (!deptCode.equals("")) {
            sql = sql.replaceFirst("@", " AND D.DEPT_CODE = '" + deptCode + "' ");
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
        table.setParmValue(result);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        ExportExcelUtil.getInstance().exportExcel(table, "�������ʱ���ͳ�Ʊ���");
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("DEPT_CODE;MR_NO");
        table.setDSValue();
        initPage();
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
