package com.javahis.ui.ope;

import java.awt.Component;
import javax.swing.JOptionPane;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p> Title: �����ײ��趨</p>
 * 
 * <p> Description: �����ײ��趨</p>
 * 
 * <p> Copyright: Copyright (c) 2015</p>
 * 
 * <p> Company:BlueCore</p>
 * 
 * @author wanglong 20150225
 * @version 1.0
 */
public class OPEPackageControl
        extends TControl {

    // ����ϸTABLE
    private TTable mainTable, detailTable;
    // �ײ��������
    private TDataStore main;
    // �ײ�ϸ�����
    private TDataStore detail;
    // ɾ����ť�õı�������
    private String tableName;
    // �ײ�����������
    private int mainRow;
    final static String INIT_MAIN_SQL = "SELECT * FROM OPE_PACKM";
    final static String INIT_DETAIL_SQL =
            "SELECT * FROM OPE_PACKD WHERE PACK_CODE ='#' ORDER BY SEQ_NO";

    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ��ʼ������
        initData();
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        mainTable.addEventListener("MAIN_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                   "onMainValueChanged");
        detailTable = (TTable) this.getComponent("DETAIL_TABLE");
        detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onDetailCreateEditComponent");
        detailTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onDetailCheckClicked");
        detailTable.addEventListener("DETAIL_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                     "onDetailValueChanged");
    }

    /**
     * ��ʼ������
     */
    private void initData() {
        main = new TDataStore();
        detail = new TDataStore();
        main.setSQL(INIT_MAIN_SQL);
        main.retrieve();
        main.setSort("PACK_DESC");// add by wanglong 20130224
        main.sort();// add by wanglong 20130224
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detail.setSQL(INIT_DETAIL_SQL.replace("#", ""));
        detail.retrieve();
    }

    /**
     * ��TABLE����¼�,����ѡ�����ײʹ��룬��ʼ���ײ�ϸ��
     */
    public void onMainClick() {
        tableName = "MAIN_TABLE";
        int row = mainTable.getSelectedRow();
        String packCode = main.getItemString(row, "PACK_CODE");
        // main.setActive(row, false);
        detail.setSQL(INIT_DETAIL_SQL.replace("#", packCode));
        detail.retrieve();
        // detail.setFilter("PACK_CODE='" + packCode + "'");
        // detail.filter();
        String opCode = detail.getItemString(detail.rowCount() - 1, "OP_CODE");
        // String opDesc = detail.getItemString(detail.rowCount() - 1, "OP_DESC");
        if (!StringUtil.isNullString(opCode) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACK_CODE", packCode);
            if (detail.rowCount() < 1) {
                detail.setItem(row, "SEQ_NO", detail.getItemInt(row, "#ID#"));
            } else {
                detail.setItem(row, "SEQ_NO", detail.getItemInt(row - 1, "SEQ_NO") + 1);
            }
            detail.setItem(row, "OP_CODE", "");
            detail.setItem(row, "OP_DESC", "");
            detail.setItem(row, "DESCRIPTION", "");
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setActive(row, false);
        }
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
        mainRow = mainTable.getSelectedRow();
    }

    /**
     * ϸTABLE����¼�
     */
    public void onDetailClick() {
        tableName = "DETAIL_TABLE";
    }

    /**
     * ����ֵ�ı��¼�,�ײ������ײ������޸ģ��Զ����ɼ�ƴ���뱣�档
     * 
     * @param tNode
     */
    public boolean onMainValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mainTable.getParmMap(column);
        Object value = tNode.getValue();
//        Object oldValue = tNode.getOldValue();
        if (TCM_Transform.isNull(value)) {
            return true;
        }
        if ("PACK_DESC".equalsIgnoreCase(colName)) {
            String packDesc = tNode.getValue() + "";
            main.setItem(row, "PY1", SystemTool.getInstance().charToCode(packDesc));
            main.setActive(row, true);
            return false;
        } else {
            main.setItem(row, colName, tNode.getValue() + "");
            main.setActive(row, true);
        }
        return false;
    }

    /**
     * ϸ��ֵ�ı��¼�
     * 
     * @param tNode
     */
    public boolean onDetailValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = tNode.getTable().getParmMap(column);
        Object value = tNode.getValue();
        // Object oldValue = tNode.getOldValue();
        if (TCM_Transform.isNull(value)) {
            return true;
        }
        if ("OP_CODE".equalsIgnoreCase(colName)) {
            if (!TCM_Transform.isNull(value) && row == detailTable.getRowCount() - 1) {
                newRow(row);
            }
            detailTable.getTable().grabFocus();
            detailTable.setSelectedRow(row);
            // detailTable.setSelectedColumn(column);
            return false;
        }
        return false;
    }

    /**
     * ��ϵ������� ICD10
     * 
     * @param com
     * @param row
     * @param column
     */
    public void onDetailCreateEditComponent(Component com, int row, int column) {
        column = detailTable.getColumnModel().getColumnIndex(column);
        String columnName = detailTable.getParmMap(column);
        if (!"OP_CODE".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        // ��table�ϵ���text����ICD10��������
        textfield.setPopupMenuParameter("OP_ICD",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
        // ����text���ӽ���ICD10�������ڵĻش�ֵ
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popICDReturn");
    }

    /**
     * ȡ������ICD����ֵ
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void popICDReturn(String tag, Object obj) {
        detailTable.acceptText();
        TParm parm = (TParm) obj;
        String opCode = parm.getValue("OPERATION_ICD");
        String opDesc = parm.getValue("OPT_CHN_DESC");
        detailTable.setItem(detailTable.getSelectedRow(), "OP_CODE", opCode);
        detailTable.setItem(detailTable.getSelectedRow(), "OP_DESC", opDesc);
        detailTable.getTable().grabFocus();
        detailTable.setSelectedRow(detailTable.getSelectedRow());
        detailTable.setDSValue();
    }

    /**
     * ϸ��table�����¼�
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onDetailCheckClicked(Object obj) {
        int row = detailTable.getSelectedRow();
        // �õ���ǰѡ��������
        for (int i = 0; i < detailTable.getRowCount(); i++) {
            detailTable.setValueAt("N", i, 0);
        }
        detailTable.setValueAt("Y", row, 0);
        detailTable.acceptText();
        return true;
    }

    /**
     * ϸ��table����һ��
     * 
     * @param row
     */
    public void newRow(int row) {
        String packCode = detail.getItemString(row, "PACK_CODE");
        int newRow = detail.insertRow();
        detail.setItem(newRow, "PACK_CODE", packCode);
        detail.setItem(newRow, "SEQ_NO", detail.getItemInt(row, "SEQ_NO") + 1);
        detail.setItem(newRow, "OP_CODE", "");
        detail.setItem(newRow, "OP_DESC", "");
        detail.setItem(newRow, "DESCRIPTION", "");
        detail.setItem(newRow, "OPT_USER", Operator.getID());
        detail.setItem(newRow, "OPT_TERM", Operator.getIP());
        detail.setItem(newRow, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        detail.setActive(newRow, false);
        detail.setActive(row, true);
        detailTable.setDSValue();
    }

    /**
     * ����һ���ײ���������
     */
    public void onNew() {
        if (main.rowCount() > 0
                && StringUtil.isNullString(main.getItemString(main.rowCount() - 1, "PACK_DESC"))) {
            return;
        }
        String packCode = SystemTool.getInstance().getNo("ALL", "OPE", "OPEPACK_NO", "OPEPACK_NO");
        if (StringUtil.isNullString(packCode)) {
            this.messageBox_("ȡ���ײͱ���ʧ��");
            return;
        }
        int row = mainTable.addRow();
        mainTable.setSelectedRow(row);
        main.setItem(row, "PACK_CODE", packCode);
        main.setItem(row, "OPT_USER", Operator.getID());
        main.setItem(row, "OPT_TERM", Operator.getIP());
        main.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        main.setActive(row, false);
        mainTable.setDSValue();
    }

    /**
     * ɾ��һ������
     */
    public void onDelete() {
        String packCode = main.getItemString(mainRow, "PACK_CODE");
        // this.messageBox_(tableName);
        if ("MAIN_TABLE".equalsIgnoreCase(tableName)) {
            if (this.messageBox("��ʾ��Ϣ", "ȷ��ɾ����", JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            main.setActive(mainRow, true);
            main.deleteRow(mainRow);
            String[] sql = main.getUpdateSQL();
            detail.setFilter("PACK_CODE='" + packCode + "'");
            detail.filter();
            int count = detail.rowCount();
            for (int i = count - 1; i > -1; i--) {
                detail.deleteRow(i);
            }
            String[] detailSql = detail.getUpdateSQL();
            sql = StringTool.copyArray(sql, detailSql);
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrText());
                return;
            }
            detail.resetModify();
            main.resetModify();
            mainTable.setDSValue();
            detailTable.setDSValue();
        } else {
            int row = -1;
            row = detailTable.getSelectedRow();
            if (StringUtil.isNullString(detail.getItemString(row, "OP_CODE"))) {
                return;
            }
            detail.deleteRow(row);
            String[] sql = detail.getUpdateSQL();
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrText());
                return;
            }
            detail.resetModify();
            detailTable.setDSValue();
        }
    }

    /**
     * ����
     */
    public void onSave() {
        mainTable.acceptText();
        detailTable.acceptText();
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        sql = StringTool.copyArray(sql, detailSql);
        if (sql == null || sql.length < 1) {
            this.messageBox_("û���ҵ����������");
            return;
        }
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");// ����ʧ��
            return;
        }
        this.messageBox("P0001");// ����ɹ�
        onClear();
        return;
    }

    /**
     * ����¼�
     */
    public void onClear() {
        mainTable.removeRowAll();
        detailTable.removeRowAll();
        // ��ʼ���ײ�����
        main = new TDataStore();
        main.setSQL(INIT_MAIN_SQL);
        main.retrieve();
        main.setSort("PACK_DESC ASC");// add by wanglong 20130325
        main.sort();
        // ��ʼ���ײ�ϸ��
        detail = new TDataStore();
        detail.setSQL(INIT_DETAIL_SQL.replace("#", ""));
        detail.retrieve();
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
    }
}
