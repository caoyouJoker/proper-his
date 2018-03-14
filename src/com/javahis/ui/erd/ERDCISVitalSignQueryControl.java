package com.javahis.ui.erd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import jdo.bil.BILComparator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ������������¼��ѯ </p>
 * 
 * <p> Description: ������������¼��ѯ </p>
 * 
 * <p> Copyright: Copyright (c) 2015 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author WangLong 2015.05.09
 * @version 1.0
 */
public class ERDCISVitalSignQueryControl
        extends TControl {

    private TTable tablePat;// ����Table
    private TTable tableD;// ��¼Table
    TTextFormat BED_NO;// ����������
    
    // =================������==============
    private BILComparator compare = new BILComparator();
    private int sortColumn = -1;
    private boolean ascending = false;

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        tablePat = (TTable) this.getComponent("TBL_PAT");
        tableD = (TTable) this.getComponent("TBL_DETAIL");
        addSortListener(tableD);//������
        BED_NO = (TTextFormat) this.getComponent("BED_NO");
		// ������ݵ����¼�
		this.callFunction("UI|TBL_PAT|addEventListener", "TBL_PAT->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
        callFunction("UI|TBL_PAT|addEventListener", "TBL_PAT->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        callFunction("UI|MR_NO|grabFocus");
        initUI();
        TParm parm = this.getInputParm();
        if (parm != null) {
            String bedSql =
                    "SELECT BED_NO FROM ERD_BED WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
            TParm bedParm = new TParm(TJDODBTool.getInstance().select(bedSql));
            if (!bedParm.getValue("BED_NO", 0).equals("")) {
                this.setValue("BED_NO", bedParm.getValue("BED_NO", 0));
            }
            this.setValue("MR_NO", parm.getValue("MR_NO"));
            this.setValue("MONITOR_START_DATE", parm.getTimestamp("ADM_DATE"));
            this.setValue("ADM_START_DATE", parm.getTimestamp("ADM_DATE"));
            onQuery();
            if (tablePat.getRowCount() == 1) {
            	onTableClicked(0);
                onTableDoubleClicked(0);
            }
        }
    }

    /**
     * ��ʼ��������Ϣ
     */
    public void initUI() {
        this.setValue("FREQ_CODE", "1");
        Timestamp sysDate = SystemTool.getInstance().getDate();
        this.setValue("MONITOR_START_DATE", sysDate);
        this.setValue("MONITOR_END_DATE", sysDate);
        this.setValue("ADM_START_DATE", StringTool.rollDate(sysDate, -1));
        this.setValue("ADM_END_DATE", sysDate);
        String sql =
                "SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='ERD_REGION' ORDER BY SEQ,ID";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        this.setValue("ERD_REGION_CODE", parm.getValue("ID", 0));
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("ERD_REGION_CODE", this.getValueString("ERD_REGION_CODE"));
        parm.setData("BED_NO", this.getValueString("BED_NO"));
        String mrNo = this.getValueString("MR_NO").trim();
        if (!StringUtil.isNullString(mrNo)) {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            
            //modify by huangtt 20160927 EMPI���߲�����ʾ  start
            Pat pat = Pat.onQueryByMrNo(mrNo);
    		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    	          this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
    	          mrNo = pat.getMrNo();
    	    }
    		//modify by huangtt 20160927 EMPI���߲�����ʾ  end
            
            this.setValue("MR_NO", mrNo);
            parm.setData("MR_NO", mrNo);
        }
        Timestamp admStartDate = TCM_Transform.getTimestamp(this.getValue("ADM_START_DATE"));
        Timestamp admEndDate = TCM_Transform.getTimestamp(this.getValue("ADM_END_DATE"));
        parm.setData("ADM_START_DATE", admStartDate);
        parm.setData("ADM_END_DATE", admEndDate);
		String sql = "SELECT B.BED_DESC,B.BED_NO,A.CASE_NO,A.MR_NO,A.PAT_NAME,A.DR_CODE,A.IN_DATE,A.OUT_DATE FROM ERD_RECORD A, ERD_BED B WHERE A.BED_NO = B.BED_NO ";
		
        if (!StringUtil.isNullString(parm.getValue("ERD_REGION_CODE"))) {
            sql = sql + " AND B.ERD_REGION_CODE = '" + parm.getValue("ERD_REGION_CODE") + "'";
        }
        
        if (!StringUtil.isNullString(parm.getValue("BED_NO"))) {
            sql = sql + " AND B.BED_NO = '" + parm.getValue("BED_NO") + "'";
        }
        
        if (!StringUtil.isNullString(mrNo)) {
//        	sql = sql + " AND A.MR_NO ='" + mrNo + "'";
            sql = sql + " AND A.MR_NO IN (" + PatTool.getInstance().getMrRegMrNos(mrNo) + ")";
        }
        
		if (!TCM_Transform.isNull(parm.getData("ADM_START_DATE"))) {
			sql = sql
					+ " AND A.IN_DATE >= TO_DATE("
					+ StringTool.getString(parm.getTimestamp("ADM_START_DATE"),
							"yyyyMMddHHmmss") + ",'YYYYMMDDHH24MISS')";
		}
        
		if (!TCM_Transform.isNull(parm.getData("ADM_END_DATE"))) {
			sql = sql
					+ " AND A.IN_DATE <= TO_DATE("
					+ StringTool.getString(parm.getTimestamp("ADM_END_DATE"),
							"yyyyMMddHHmmss") + ",'YYYYMMDDHH24MISS')";
		}
		
		sql = sql + " ORDER BY A.IN_DATE DESC, B.BED_NO";
		
        TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrText());
            return;
        }
        
        if (patParm.getCount("CASE_NO") <= 0) {
        	this.messageBox("��������");
        	tablePat.setParmValue(new TParm());
        } else {
        	tablePat.setParmValue(patParm);
        }
    }

    /**
     * ���Excel
     */
    public void onExport() {
        if (tablePat.getRowCount() < 1) {
            this.messageBox("�޵�������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(tableD, "������������¼");
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("ERD_REGION_CODE;FREQ_CODE;BED_NO;MR_NO");
        callFunction("UI|TBL_PAT|setParmValue", new TParm());
        callFunction("UI|TBL_DETAIL|setParmValue", new TParm());
        initUI();
    }
    
    /**
     * ����Table�����¼�
     * 
     * @param row
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm parmRow = tablePat.getParmValue().getRow(row);
        this.setValue("MONITOR_START_DATE", parmRow.getTimestamp("IN_DATE"));
        if (StringUtils.isEmpty(parmRow.getValue("OUT_DATE"))) {
            this.setValue("MONITOR_END_DATE", SystemTool.getInstance().getDate());
        } else {
        	this.setValue("MONITOR_END_DATE", parmRow.getTimestamp("OUT_DATE"));
        }
    }

    /**
     * ����Table˫���¼�
     * 
     * @param row
     */
    public void onTableDoubleClicked(int row) {// ���Եõ�����
        if (row < 0) {
            return;
        }
        
        tableD.setParmValue(new TParm());
        
        TParm parmRow = tablePat.getParmValue().getRow(row);
        String bedNo = parmRow.getValue("BED_NO");
        String freqCode = this.getValueString("FREQ_CODE");
        Timestamp monitorStartDate =
                TCM_Transform.getTimestamp(this.getValue("MONITOR_START_DATE"));
        Timestamp monitorEndDate = TCM_Transform.getTimestamp(this.getValue("MONITOR_END_DATE"));
        String itemSql = "SELECT DISTINCT MONITOR_ITEM_EN FROM ERD_CISVITALSIGN WHERE 1=1 AND BED_NO = '"+ bedNo + "' # #";
        if (!TCM_Transform.isNull(monitorStartDate)) {
            itemSql =
                    itemSql.replaceFirst("#",
                                         " AND MONITOR_TIME >='"
                                                 + StringTool.getString(monitorStartDate,
                                                                        "yyyyMMddHHmm") + "'");
        } else {
            itemSql = itemSql.replaceFirst("#", "");
        }
        if (!TCM_Transform.isNull(monitorEndDate)) {
            itemSql =
                    itemSql.replaceFirst("#",
                                         " AND MONITOR_TIME <='"
                                                 + StringTool.getString(monitorEndDate,
                                                                        "yyyyMMddHHmm") + "'");
        } else {
            itemSql = itemSql.replaceFirst("#", "");
        }
        TParm itemParm = new TParm(TJDODBTool.getInstance().select(itemSql));
        if (itemParm.getErrCode() < 0) {
            this.messageBox(itemParm.getErrText());
            return;
        }
        if (itemParm.getCount() < 1) {
            this.messageBox("���޼������");
            return;
        }
        String header="���ʱ��/��Ŀ,140,timestamp,yyyy/MM/dd HH:mm";
        String parmMap = "MONITOR_TIME";
        String itemStr = "'#' AS #".replaceAll("#", itemParm.getValue("MONITOR_ITEM_EN", 0));
        header += ";" + itemParm.getValue("MONITOR_ITEM_EN", 0) + ",80";
        parmMap += ";" + itemParm.getValue("MONITOR_ITEM_EN", 0) + "";
        for (int i = 1; i < itemParm.getCount(); i++) {
            itemStr += ",'#' AS #".replaceAll("#", itemParm.getValue("MONITOR_ITEM_EN", i));
            header += ";" + itemParm.getValue("MONITOR_ITEM_EN", i) + ",80";
            parmMap += ";" + itemParm.getValue("MONITOR_ITEM_EN", i) + "";
        }
        String sql =
                "SELECT * FROM (SELECT TO_DATE( MONITOR_TIME, 'YYYYMMDDHH24MI') MONITOR_TIME, MONITOR_ITEM_EN, "
                        + "               MONITOR_VALUE "
                        + "          FROM ERD_CISVITALSIGN "
                        + "         WHERE BED_NO = '!' @ # "
                        + "           AND MOD(TO_NUMBER(SUBSTR(MONITOR_TIME,9,2))*60 + TO_NUMBER(SUBSTR(MONITOR_TIME,11,2)), $) "
                        + "             = (SELECT MOD(TO_NUMBER(SUBSTR(MIN( MONITOR_TIME),9,2))*60+TO_NUMBER(SUBSTR(MIN(MONITOR_TIME),11,2)), $) "
                        + "         FROM ERD_CISVITALSIGN  WHERE BED_NO = '!' @ #  ) "
                        + ") PIVOT (SUM(NVL(MONITOR_VALUE, 0)) FOR MONITOR_ITEM_EN IN (&)) "
                        + "ORDER BY MONITOR_TIME DESC";// ������ʵ����Ҫ
        if (StringUtil.isNullString(bedNo)) {
            return;
        } else {
            sql = sql.replaceAll("!", bedNo);
        }
        if (!TCM_Transform.isNull(monitorStartDate)) {
            sql =
                    sql.replaceAll("@",
                                     " AND MONITOR_TIME >='"
                                             + StringTool.getString(monitorStartDate,
                                                                    "yyyyMMddHHmm") + "'");
        } else {
            sql = sql.replaceAll("@", "");
        }
        if (!TCM_Transform.isNull(monitorEndDate)) {
            sql =
                    sql.replaceAll("#",
                                     " AND MONITOR_TIME <='"
                                             + StringTool.getString(monitorEndDate, "yyyyMMddHHmm")
                                             + "'");
        } else {
            sql = sql.replaceAll("#", "");
        }
        if (StringUtil.isNullString(freqCode)) {
            this.messageBox("��ѡ��Ƶ��");
            return;
        } else {
            sql = sql.replaceAll("\\$", freqCode);
        }
        sql = sql.replaceFirst("&", itemStr);
        TParm cisParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (cisParm.getErrCode() < 0) {
            this.messageBox(cisParm.getErrText());
            return;
        }
        if (cisParm.getCount() < 1) {
            this.messageBox("���޼������");
            this.onClear();
            return;
        }
        tableD.setHeader(header);
        tableD.setParmMap(parmMap);
        tableD.setParmValue(cisParm);
    }

    /**
     * ��������ѡ���¼�
     */
    public void onChooseRegion() {
        String bedNoSql =
                "SELECT BED_NO AS ID, BED_NO||' '||BED_DESC AS NAME FROM ERD_BED WHERE 1=1 # ORDER BY ID ASC NULLS LAST";
        if (!TCM_Transform.isNull(BED_NO.getValue())) {
            bedNoSql =
                    bedNoSql.replaceFirst("#",
                                          " AND ERD_REGION_CODE ='"
                                                  + this.getValueString("ERD_REGION_CODE") + "'");
        } else {
            bedNoSql = bedNoSql.replaceFirst("#", "");
        }
        BED_NO.setPopupMenuSQL(bedNoSql);// ��ͨ�û��޶�ֻ�ܲ�ѯ�Լ����ڿ��ҵļ�¼
        BED_NO.onQuery();
    }
    
    /**
     * Ƶ��������ѡ���¼�
     */
    public void onSelectFreq() {
        int row = tablePat.getSelectedRow();
        if (row < 0) {
            return;
        }
        onTableDoubleClicked(row);
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);
        this.setValue("MR_NO", mrNo);
        this.onQuery();
    }

    // ====================������begin======================
    /**
     * �����������������
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
     * ����ָ���������������е�index
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
    // ====================������end======================
}
