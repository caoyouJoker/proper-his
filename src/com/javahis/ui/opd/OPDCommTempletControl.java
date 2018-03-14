package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ����ģ��</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OPDCommTempletControl extends TControl{

    private String action = "save";
    // ������
    private TTable table;

    private String type = "2";

    public OPDCommTempletControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        type = (String)this.getParameter();
        // ��ʼ��Table
        table = getTable("TABLE");
        String sql = "SELECT DEPT_OR_DR, DEPTORDR_CODE, SUBCLASS_CODE, "
            + " SEQ, MAIN_FLG, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM OPD_COMTEMPLET "
            + " WHERE DEPT_OR_DR = '" + type + "' ";
        String where = "";
        // 1������ģ�壻2�� ҽ��ģ��
        if ("1".equals(type)) {
            where = " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(true);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(false);
            this.setValue("DEPT_CODE", Operator.getDept());
            this.setValue("LAB_TEXT", "���ң�");
        }
        else {
            where = " AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(false);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(true);
            this.setValue("DR_CODE", Operator.getID());
            this.setValue("LAB_TEXT", "ҽ����");
        }
        String order_by =
            " ORDER BY DEPTORDR_CODE, MAIN_FLG DESC, SUBCLASS_CODE, SEQ ";

        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql + where + order_by);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * ���淽��
     */
    public void onSave(){
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        String main_sql  = "";
        if ("1".equals(type)) {
            main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET " +
                "WHERE DEPT_OR_DR = '1' AND DEPTORDR_CODE = '" +
                getValueString("DEPT_CODE") + "' AND MAIN_FLG = 'Y'";
        }
        else {
            main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET " +
                "WHERE DEPT_OR_DR = '2' AND DEPTORDR_CODE = '" +
                getValueString("DR_CODE") + "' AND MAIN_FLG = 'Y' ";
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(main_sql));
        if (result != null && result.getCount() > 0 &&
            "Y".equals(this.getValueString("MAIN_FLG"))) {
            this.messageBox("�Ѵ�����ģ�壬������ѡ��");
            return;
        }

        if ("save".equals(action)) {
            TTextFormat combo = getTextFormat("SUBCLASS_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            if ("1".equals(type)) {
                table.getDataStore().setItem(row, "DEPT_OR_DR", "1");
                table.getDataStore().setItem(row, "DEPTORDR_CODE",
                                             getValueString("DEPT_CODE"));
            }
            else {
                table.getDataStore().setItem(row, "DEPT_OR_DR", "2");
                table.getDataStore().setItem(row, "DEPTORDR_CODE",
                                             getValueString("DR_CODE"));
            }
            table.setItem(row, "SUBCLASS_CODE",
                          getValueString("SUBCLASS_CODE"));
            String sql = "SELECT SEQ FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '" +
                getValueString("SUBCLASS_CODE") + "' AND OPD_FLG = 'Y' ";
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            table.setItem(row, "SEQ", parm.getInt("SEQ",0));
            table.setItem(row, "MAIN_FLG", getValueString("MAIN_FLG"));
            table.setItem(row, "OPT_USER", Operator.getID());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                table.removeRow(row);
                table.setDSValue();
                onClear();
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
    }

    /**
     * TABLE�����¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "SUBCLASS_CODE;MAIN_FLG";
            this.setValueForParm(likeNames, parm);
            getTextFormat("SUBCLASS_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ��ʼ��Table
        table = getTable("TABLE");
        String sql = "SELECT DEPT_OR_DR, DEPTORDR_CODE, SUBCLASS_CODE, "
            + " SEQ, MAIN_FLG, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM OPD_COMTEMPLET "
            + " WHERE DEPT_OR_DR = '" + type + "' ";
        String where = "";
        // 1������ģ�壻2�� ҽ��ģ��
        if ("1".equals(type)) {
            where = " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(true);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(false);
            this.setValue("DEPT_CODE", Operator.getDept());
            this.setValue("LAB_TEXT", "���ң�");
        }
        else {
            where = " AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(false);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(true);
            this.setValue("DR_CODE", Operator.getID());
            this.setValue("LAB_TEXT", "ҽ����");
        }
        String order_by =
            " ORDER BY DEPTORDR_CODE, MAIN_FLG DESC, SUBCLASS_CODE, SEQ ";
        // System.out.println(sql + where + order_by);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql + where + order_by);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        String code = getValueString("SUBCLASS_CODE");
        if (code.length() > 0) {
            String filterString = "SUBCLASS_CODE = '" + code + "'";
            table.setFilter(filterString);
            table.filter();
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        this.setValue("SUBCLASS_CODE", "");
        this.setValue("MAIN_FLG", "N");
        table.setSelectionMode(0);
        ( (TTextFormat)this.getComponent("SUBCLASS_CODE")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * �������
     */
    private boolean CheckData() {
        if ("".equals(getValueString("SUBCLASS_CODE"))) {
            this.messageBox("ģ���Ų���Ϊ��");
            return false;
        }
        return true;
    }


    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
