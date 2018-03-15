package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TParm;
import jdo.spc.INDSQL;
import jdo.spc.IndSysParmTool;

import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.javahis.manager.IndRequestObserver;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.awt.Component;
import jdo.spc.IndStockMTool;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import java.util.Vector;
import jdo.spc.IndStockDTool;
import com.dongyang.util.TypeTool;
import jdo.util.Manager;
import com.dongyang.ui.TTableNode;
import jdo.spc.INDTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: ��ļ�����������ҵControl</p>
 *
 * <p>Description: ��ļ�����������ҵControl</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2009.05.23
 * @version 1.0
 */
public class INDRequestWasThoControl
    extends TControl {

    // �������
    private String action;
    // �������
    private TTable table_m;

    // ϸ�����
    private TTable table_d;

    // �������ѡ����
    private int row_m;

    // ϸ�����ѡ����
    private int row_d;

    // ҳ���Ϸ������б�
    private String[] pageItem = {
        "REQUEST_DATE", "REQUEST_NO", "APP_ORG_CODE",
        "REQTYPE_CODE", "REASON_CHN_DESC", "DESCRIPTION",
        "URGENT_FLG", "UNIT_TYPE"};

    // ϸ�����
    private int seq;

    // ���뵥��
    private String request_no;

    // ��������
    private String request_type;

    // ȫ������Ȩ��
    private boolean dept_flg = true;

    public INDRequestWasThoControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ע�ἤ��SYS_FEE�������¼�
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        // ���������¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��ʼ����������
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        // ������ѯ
        TParm result = TIOM_AppServer.executeAction(
            "action.spc.INDRequestAction", "onQueryM", onQueryParm());
        // ����״̬��������
        result = onFilterQueryByStatus(result);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("�޲�ѯ���");
        }
        else {
            table_m.setParmValue(result);
        }
    }

    /**
     * ���淽��
     */
    public void onSave() {
        // ���������
        TParm parm = new TParm();
        // ���ؽ����
        TParm result = new TParm();
        if (!"updateD".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // ���������Ϣ
            parm = getRequestMParm(parm);
            if ("insertM".equals(action)) {
                // ���뵥��
                String request_no = SystemTool.getInstance().getNo("ALL",
                    "IND", "IND_REQUEST", "No");
                parm.setData("REQUEST_NO", request_no);
                // ִ����������
                result = TIOM_AppServer.executeAction(
                    "action.spc.INDRequestAction", "onInsertM", parm);
            }
            else {
                parm.setData("REQUEST_NO", request_no);
                // ִ�����ݸ���
                result = TIOM_AppServer.executeAction(
                    "action.spc.INDRequestAction", "onUpdateM", parm);
            }
            // ������ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            onClear();
            setValue("REQUEST_NO", parm.getValue("REQUEST_NO"));
            this.messageBox("P0001");
            onQuery();
        }
        else {
            if (!CheckDataD()) {
                return;
            }
            // ��Ӧ���ſ�漰ҩƷ�������ж�
            if (!getOrgStockCheck()) {
                return;
            }
            Timestamp date = SystemTool.getInstance().getDate();
            table_d.acceptText();
            TDataStore dataStore = table_d.getDataStore();
            // ���ȫ���Ķ����к�
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // ���ȫ����������
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
            // ���̶�����������
            Vector vct = new Vector();
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "REQUEST_NO",
                                  getValueString("REQUEST_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
                vct.add(dataStore.getItemString(newrows[i], "ORDER_CODE"));
            }
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            TParm inParm = new TParm();
            inParm.setData("ORG_CODE", "");
            inParm.setData("ORDER_CODE_LIST", null);
            inParm.setData("OPT_USER", Operator.getID());
            inParm.setData("OPT_DATE", date);
            inParm.setData("OPT_TERM", Operator.getIP());
            inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            inParm.setData("REGION_CODE", Operator.getRegion());//========pangben modify 20110621
            // ִ����������
            result = TIOM_AppServer.executeAction(
                "action.spc.INDRequestAction", "onUpdateD", inParm);
            // ������ж�
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_d.setDSValue();
            // ���ô�ӡ����
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                this.onPrint();
            }
            return;
        }
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            this.messageBox("��������ɲ���ɾ��");
        }
        else {
            if (row_m > -1) {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥", 2) == 0) {
                    TParm parm = new TParm();
                    // ϸ����Ϣ
                    if (table_d.getRowCount() > 0) {
                        table_d.removeRowAll();
                        String deleteSql[] = table_d.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // ������Ϣ
                    parm.setData("REQUEST_NO", request_no);
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDRequestAction", "onDeleteM", parm);
                    // ɾ���ж�
                    if (result.getErrCode() < 0) {
                        this.messageBox("ɾ��ʧ��");
                        return;
                    }
                    table_m.removeRow(row_m);
                    this.messageBox("ɾ���ɹ�");
                    onClear();
                }
            }
            else {
                if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥ϸ��", 2) == 0) {
                    if ("".equals(table_d.getDataStore().getItemString(row_d,
                        "REQUEST_NO"))) {
                        table_d.removeRow(row_d);
                        return;
                    }
                    table_d.removeRow(row_d);
                    // ϸ����ж�
                    if (!table_d.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_d.setDSValue();
                    //onQuery();
                }
            }
        }
    }

    /**
     * ���뵥״̬����¼�
     */
    public void onChangeRequestStatus() {
        String clearString =
            "APP_ORG_CODE;REQUEST_NO;" +
            "REQTYPE_CODE;REASON_CHN_DESC;DESCRIPTION;" +
            "URGENT_FLG;SUM_RETAIL_PRICE";
        this.clearValue(clearString);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
    }


    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "REQUEST_DATE;APP_ORG_CODE;REQUEST_NO;" +
            "REQTYPE_CODE;REASON_CHN_DESC;DESCRIPTION;" +
            "URGENT_FLG;SUM_RETAIL_PRICE";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REQUEST_DATE", date);
        //getComboBox("REQTYPE_CODE").setSelectedIndex(0);
        // ����״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        getTextField("REQUEST_NO").setEnabled(true);
        getTextFormat("APP_ORG_CODE").setEnabled(true);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        action = "insertM";
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            this.messageBox("���������뵥");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // ��ӡ����
            TParm date = new TParm();
            // ��ͷ����
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         this.getComboBox("REQTYPE_CODE").getSelectedName() +
                         "��");
            date.setData("ORG_CODE_OUT", "TEXT", "���벿��: " +
                         this.getTextFormat("APP_ORG_CODE").getText());
            date.setData("REQ_NO", "TEXT",
                         "���뵥��: " + this.getValueString("REQUEST_NO"));
            date.setData("REQ_TYPE", "TEXT",
                         "�������: " +
                         this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("DATE", "TEXT",
                         "�Ʊ�����: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));
            date.setData("DESC", "TEXT",
                         "��ע: " + this.getValueString("DESCRIPTION"));

            // ��������
            TParm parm = new TParm();
            String order_code = "";
            String unit_type = "0";
            String order_desc = "";
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code, unit_type)));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return;
                }
                if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
                    order_desc = inparm.getValue("ORDER_DESC", 0);
                }
                else {
                    order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
                        inparm.getValue("GOODS_DESC", 0) + ")";
                }
                parm.addData("ORDER_DESC", order_desc);
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("UNIT_PRICE",
                             table_d.getItemDouble(i, "RETAIL_PRICE"));
                parm.addData("QTY",
                             table_d.getItemDouble(i, "QTY"));
                parm.addData("AMT", StringTool.round(table_d.getItemDouble(i,
                    "RETAIL_PRICE") * table_d.getItemDouble(i, "QTY"), 2));
            }
            if (parm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("û�д�ӡ����");
                return;
            }

            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());

            // ��β����
            date.setData("TOT", "TEXT", "�ϼ�: " +
                         StringTool.round(Double.parseDouble(this.
                getValueString("SUM_RETAIL_PRICE")),
                                          2));
            date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
            // ���ô�ӡ����
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\RequestWasTho.jhw",
                                 date);
        }
        else {
            this.messageBox("û�д�ӡ����");
            return;
        }
    }


    /**
     * �������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // �������ѡ���������Ϸ�
            getTableSelectValue(table_m, row_m, pageItem);
            // �趨ҳ��״̬
            getTextField("REQUEST_NO").setEnabled(false);
            getTextFormat("APP_ORG_CODE").setEnabled(false);
            getComboBox("REQTYPE_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "updateM";
            // �������
            request_type = getValueString("REQTYPE_CODE");
            // ���뵥��
            request_no = getValueString("REQUEST_NO");
            // ��ϸ��Ϣ
            getTableDInfo(request_no);
            // ����һ��ϸ��
            onAddRow();
            table_d.setSelectionMode(0);
        }
    }

    /**
     * ��ϸ����(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            action = "updateD";
            table_m.setSelectionMode(0);
            row_m = -1;
            // ȡ��SYS_FEE��Ϣ��������״̬����
            /*
             String order_code = table_d.getDataStore().getItemString(table_d.
                getSelectedRow(), "ORDER_CODE");
                         if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
                         }
                         else {
                callFunction("UI|setSysStatus", "");
                         }
             */
        }
    }

    /**
     * ��TABLE�����༭�ؼ�ʱ����
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\spc\\SPCSysFeePopup.x"), parm);
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table_d.acceptText();
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_code))
            table_d.getDataStore().setItem(table_d.getSelectedRow(),
                                           "ORDER_CODE", order_code);
        // �����ⲿ���Ƿ���ڸ�ҩƷ
        TParm qtyParm = new TParm();
        qtyParm.setData("ORG_CODE", this.getValueString("APP_ORG_CODE"));
        qtyParm.setData("ORDER_CODE", order_code);
        TParm getQTY = IndStockMTool.getInstance().onQueryAll(qtyParm);
        if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
            this.messageBox("���벿�Ų�����ҩƷ:" + order_desc + "(" + order_code + ")");
            table_d.removeRow(table_d.getSelectedRow());
            onAddRow();
            return;
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getPHAInfoByOrder(order_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            this.messageBox("ҩƷ��Ϣ����");
            return;
        }

        // ��ѯҩƷ�����κ�Ч��
        String sql = INDSQL.getOrderBatchNoValidAll(this.getValueString(
            "APP_ORG_CODE"), order_code, "0");
        TParm addParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (addParm == null || addParm.getErrCode() < 0) {
            this.messageBox("ҩƷ��Ϣ����");
            return;
        }
        if (addParm.getCount("ORDER_CODE") <= 0) {
            this.messageBox("û��ҩƷ��Ϣ");
            return;
        }
        // ֻ��һ�����ź�Ч�ڵ�����
        if (addParm.getCount("ORDER_CODE") == 1) {
            // ����
            table_d.setItem(table_d.getSelectedRow(), "BATCH_NO", addParm
                            .getValue("BATCH_NO", 0));
            // ��Ч��
            table_d.setItem(table_d.getSelectedRow(), "VALID_DATE",
                            addParm.getTimestamp("VALID_DATE", 0));
            
            //luhai 2012-2-13 add batch_seq verifyinPrice begin
            // batchSeq
            table_d.setItem(table_d.getSelectedRow(), "BATCH_SEQ", addParm
                            .getValue("BATCH_SEQ", 0));
            //verifyinPrice 
            table_d.setItem(table_d.getSelectedRow(), "VERIFYIN_PRICE", addParm
            		.getValue("VERIFYIN_PRICE", 0));
          //luhai 2012-2-13 add batch_seq verifyinPrice end
        }
        // ���ڶ����ź�Ч�ڵ�����
        else if (addParm.getCount("ORDER_CODE") > 1) {
            // ��������Ч�ڲ�ѯ����
            qtyParm.setData("UNIT_TYPE", "0");
            Object resultParm = openDialog("%ROOT%\\config\\spc\\INDForVaild.x",
                                           qtyParm);
            addParm = (TParm) resultParm;
            // ����
            table_d.setItem(table_d.getSelectedRow(), "BATCH_NO", addParm
                            .getValue("BATCH_NO"));
            // ��Ч��
            table_d.setItem(table_d.getSelectedRow(), "VALID_DATE",
                            addParm.getTimestamp("VALID_DATE"));
            //luhai 2012-01-16 add batch_seq 
            table_d.setItem(table_d.getSelectedRow(), "BATCH_SEQ",
            		addParm.getValue("BATCH_SEQ"));
            //luhai 2012-01-16 add verifyInPrice 
            table_d.setItem(table_d.getSelectedRow(), "VERIFYIN_PRICE",
            		addParm.getValue("VERIFYIN_PRICE"));
        }
        double stock_price = 0;
        double retail_price = 0;
        // ��浥λ
        table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE", result
                        .getValue("STOCK_UNIT", 0));
        stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
                                       * result.getDouble("DOSAGE_QTY", 0),
                                       2);
        retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
                                        * result.getDouble("DOSAGE_QTY", 0),
                                        2);
        // �ɱ���
        table_d.getDataStore().setItem(table_d.getSelectedRow(), "STOCK_PRICE",
                                       stock_price);
        // ���ۼ�
        table_d.setItem(table_d.getSelectedRow(), "RETAIL_PRICE", retail_price);
        // �趨ѡ���е���Ч��
        table_d.getDataStore().setActive(table_d.getSelectedRow(), true);
        onAddRow();
    }

    /**
     * ����һ��ϸ��
     */
    private void onAddRow() {
        // ��δ�༭��ʱ����
        if (!this.isNewRow())
            return;
        int row = table_d.addRow();
        TParm parm = new TParm();
        parm.setData("ACTIVE", false);
        table_d.getDataStore().setActive(row, false);
    }

    /**
     * �Ƿ���δ�༭��
     *
     * @return boolean
     */
    private boolean isNewRow() {
        Boolean falg = false;
        TParm parmBuff = table_d.getDataStore().getBuffer(
            table_d.getDataStore().PRIMARY);
        int lastRow = parmBuff.getCount("#ACTIVE#");
        Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
        if (obj != null) {
            falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
        }
        else {
            falg = true;
        }
        return falg;
    }

    /**
     * ����ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table������
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("ת����������С�ڻ����0");
                return true;
            }
            table.getDataStore().setItem(row, "QTY", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            return false;
        }
        return true;
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        /**
         * Ȩ�޿���
         * Ȩ��1:һ�����ֻ��ʾ������������
         * Ȩ��9:���Ȩ����ʾȫԺҩ�ⲿ��
         */
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
            getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion()));
            if (parm.getCount("NAME") > 0) {
                getTextFormat("APP_ORG_CODE").setValue(parm.getValue("ID", 0));
            }
            dept_flg = false;
        }
        else {
            // ��ʼ��ȫԺҩ�ⲿ��
            getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.
            		getSPCIndOrgComobo(
                    "IN ('A','B','C')", "", Operator.getRegion()));
        }

        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // ��ʼ������ʱ��
        setValue("REQUEST_DATE", date);
        // ��ʼ�����뵥����
        getComboBox("REQTYPE_CODE").setSelectedIndex(0);
        // ��ʼ������״̬
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // ��ʼ��TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 0;
        action = "insertM";
        setTypesOfDrug();
    }
    
    /**
     * ����ҩƷ����
     */
    public void setTypesOfDrug(){
    	TParm parm = getSysParm();
    	if(parm.getCount() > 0 ){
    		parm = parm.getRow(0);
    	}
    	String isSeparateReq = parm.getValue("IS_SEPARATE_REQ");
    	if(isSeparateReq != null && !isSeparateReq.equals("")){
    		if(isSeparateReq.equals("Y")){
    			getRadioButton("G_DRUGS").setSelected(true);
    		}else{
    			getRadioButton("ALL").setSelected(true);
    		}
    	}else {
    		getRadioButton("ALL").setSelected(true);
		}
    }

    /**
     * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ�������
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        // ��ϸ��Ϣ
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getRequestDByNo(req_no));
        tds.retrieve();
        if (tds.rowCount() == 0) {
            this.messageBox("û��������ϸ");
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }

        // �۲���
        IndRequestObserver obser = new IndRequestObserver();
        tds.addObserver(obser);
        table_d.setDataStore(tds);
        table_d.setDSValue();
        this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
    }


    /**
     * ��ѯ��������
     *
     * @return
     */
    private TParm onQueryParm() {
        TParm parm = new TParm();
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        else {
            parm.setData("TYPE_WAS_THO", "");
        }
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        
        //ҩƷ����--��ҩ:1,�龫��2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","2");
        }else if(getRadioButton("ALL").isSelected()){
        	parm.setData("DRUG_CATEGORY","3");
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        if (parm == null) {
            return parm;
        }
        return parm;
    }

    /**
     * ���ݶ���״̬���˲�ѯ���
     *
     * @param parm
     * @return
     */
    private TParm onFilterQueryByStatus(TParm parm) {
        String update_flg = "0";
        boolean flg = false;
        TDataStore ds = new TDataStore();
        for (int i = parm.getCount("REQUEST_NO") - 1; i >= 0; i--) {
            ds.setSQL(INDSQL.getRequestDByNo(parm.getValue("REQUEST_NO", i)));
            ds.retrieve();
            if (ds.rowCount() == 0) {
                flg = false;
            }
            else {
                flg = true;
                for (int j = 0; j < ds.rowCount(); j++) {
                    update_flg = ds.getItemString(j, "UPDATE_FLG");
                    if ("0".equals(update_flg) || "1".equals(update_flg)) {
                        // δ���
                        flg = false;
                        break;
                    }
                }
            }
            // ����״̬
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                // δ���
                if (flg) {
                    parm.removeRow(i);
                }
            }
            else {
                // ���
                if (!flg) {
                    parm.removeRow(i);
                }
            }
        }

        // û��ȫ������Ȩ��
        if (!dept_flg) {
            TParm optOrg = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
            for (int i = parm.getCount("APP_ORG_CODE") - 1; i >= 0; i--) {
                boolean check_flg = true;
                for (int j = 0; j < optOrg.getCount("ID"); j++) {
                    if (parm.getValue("APP_ORG_CODE", i).equals(optOrg.getValue(
                        "ID", j))) {
                        check_flg = false;
                        break;
                    }
                    else {
                        continue;
                    }
                }
                if (check_flg) {
                    parm.removeRow(i);
                }
            }
        }
        return parm;
    }

    /**
     * ����������������PARM
     *
     * @param parm
     * @return
     */
    private TParm getRequestMParm(TParm parm) {
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", "");
        parm.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parm.setData("REQUEST_USER", Operator.getID());
        parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
        parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
        String unit_type = "0";
        parm.setData("UNIT_TYPE", unit_type);
        parm.setData("URGENT_FLG", getValueString("URGENT_FLG"));
        parm.setData("APPLY_TYPE", "1");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        if(getRadioButton("G_DRUGS").isSelected()){//���龫
        	parm.setData("DRUG_CATEGORY", "1");
        }else if(getRadioButton("N_DRUGS").isSelected()){//�龫
        	parm.setData("DRUG_CATEGORY", "2");
        }else if(getRadioButton("ALL").isSelected()){
        	parm.setData("DRUG_CATEGORY","3");   //ȫ��������Ϊ����
        }
        return parm;
        
    }


    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("���벿�Ų���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("���������Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckDataD() {
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            if (table_d.getItemDouble(i, "QTY") <= 0) {
                this.messageBox("ת����������С�ڻ����0");
                return false;
            }
        }
        return true;
    }

    /**
     * ����ѡ���������Ϸ�
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }

    /**
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // ����������
        int count = dataStore.rowCount();
        // ��������
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // �������ֵ
            if (s < value) {
                s = value;
                continue;
            }
        }
        // ���ż�1
        s++;
        return s;
    }

    /**
     * ���������ܽ��
     *
     * @return
     */
    private double getSumRetailPrice() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            double amount1 = table_d.getItemDouble(i, "QTY");
            sum += table_d.getItemDouble(i, "RETAIL_PRICE") * amount1;
        }
        return StringTool.round(sum, 2);
    }

    /**
     * ���ⲿ�ſ�漰ҩƷ�������ж�
     */
    private boolean getOrgStockCheck() {
        /** ��Ӧ����ҩƷ¼��ܿ� */
        String org_code = getValueString("APP_ORG_CODE");
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        int batch_seq = 0;
        double stock_qty = 0;
        double qty = 0;

        for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            order_code = table_d.getDataStore().getItemString(i,
                "ORDER_CODE");
            qty = table_d.getDataStore().getItemDouble(i, "QTY");
            TParm resultParm = new TParm();
            if ("0".equals(getValueString("UNIT_TYPE"))) {
                String sql = INDSQL.getPHAInfoByOrder(order_code);
                resultParm = new TParm(TJDODBTool.getInstance().select(sql));
                if (resultParm.getErrCode() < 0) {
                    this.messageBox("ҩƷ��Ϣ����");
                    return false;
                }
                qty = StringTool.round(qty *
                                       resultParm.getDouble("DOSAGE_QTY", 0), 2);
            }
            batch_no = table_d.getItemString(i, "BATCH_NO");
            valid_date = TypeTool.getString(table_d.getItemData(i, "VALID_DATE")).
                substring(0, 10);
            TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
            		getIndStockBatchSeqAll(org_code, order_code, batch_no, valid_date)));
            if (inparm.getErrCode() < 0) {
                this.messageBox("ҩƷ��Ϣ����");
                return false;
            }
            //System.out.println("----"+inparm);
            batch_seq = inparm.getInt("BATCH_SEQ", 0);

            inparm.setData("ORG_CODE", org_code);
            inparm.setData("ORDER_CODE", order_code);
            inparm.setData("BATCH_SEQ", batch_seq);
           
            TParm resultQTY = IndStockDTool.getInstance().
                onQueryStockQTYByBatchAll(inparm);
            stock_qty = resultQTY.getDouble("QTY", 0);
            if (stock_qty < 0) {
                stock_qty = 0;
            }
            if (qty > stock_qty) {
                TParm order = INDTool.getInstance().getSysFeeOrder(
                    order_code);
                //                if (this.messageBox("��ʾ",
                //                                    "ҩƷ:" + order.getValue("ORDER_DESC") + "(" +
                //                                    order_code + ") �����������,��ǰ�����Ϊ" +
                //                                    stock_qty + ",�Ƿ����", 2) == 0) {
                //                    return true;
                //                }
                this.messageBox("ҩƷ:" + order.getValue("ORDER_DESC") + "(" +
                                order_code + ") �����������,��ǰ�����Ϊ" + stock_qty);
                return false;
            }
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

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * ȡ��SYS_FEE��Ϣ��������״̬����
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE")
            + " ҩƷ����:" + order.getValue("ORDER_DESC")
            + " ��Ʒ��:" + order.getValue("GOODS_DESC")
            + " ���:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }
    
    /**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }
}