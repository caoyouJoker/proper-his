package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;
import jdo.inv.InvPackStockMTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Vector;

/**
 * <p>Title: ���������</p>
 *
 * <p>Description: ���������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.12
 * @version 1.0
 */
public class INVBackPackAndDisnfectionControl
    extends TControl {

    // ����
    private TTable tableM;
    // ϸ��
    private TTable tableD;
    // �洢�����¼
    private Map map;

    public INVBackPackAndDisnfectionControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableD = getTable("TABLED");
        // ��������¼�
        addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
        // ��������¼�
        addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // ��TABLEDEPT�е�CHECKBOX��������¼�
        callFunction("UI|TABLEM|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");

        map = new HashMap();

        TParm packParm = new TParm();
        String[] reqPackD = {
            "SELECT_FLG", "PACK_DESC", "PACK_SEQ_NO", "QTY", "STATUS",
            "DISINFECTION_DATE", "VALUE_DATE", "DISINFECTION_USER",
            "USE_COST", "ONCE_USE_COST", "PACK_CODE"};
        for (int i = 0; i < reqPackD.length; i++) {
            packParm.setData(reqPackD[i], new Vector());
        }
        tableD.setParmValue(packParm);

    }

    /**
     *
     */
    public void onOrgCodeAction() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        //parm.setData("STATUS", "1");
        // ���õ����˵�
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackStockMPopup.x"), parm);
        // ������ܷ���ֵ����
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
    }

    /**
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��Ӧ�Ҳ���");
            getTextField("PACK_CODE").setValue("");
            return;
        }

        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        this.setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        this.setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        this.setValue("PACK_SEQ_NO", parm.getValue("PACK_SEQ_NO"));
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��Ӧ�Ҳ���");
            return;
        }
        if ("".equals(this.getValueString("PACK_CODE"))) {
            this.messageBox("��ѡ������������");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        parm.setData("PACK_SEQ_NO", this.getValueInt("PACK_SEQ_NO"));

        TParm result = InvPackStockMTool.getInstance().onQueryStockM(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        for (int i = 0; i < result.getCount("PACK_CODE"); i++) {
            result.addData("SELECT_FLG", "N");
            result.setData("QTY", i, 1);
        }
        tableM.setParmValue(result);
        map = new HashMap();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        //���������Ч��
        if (!checkValueDate()) {
            return;
        }
        tableM.acceptText();
        TParm tableParm = tableM.getParmValue();
        int pack_seq_no = 0;
        String status = "1";
        for (int i = 0; i < tableParm.getCount("PACK_CODE"); i++) {
            pack_seq_no = tableParm.getInt("PACK_SEQ_NO", i);
            status = tableParm.getValue("STATUS", i);
            if (pack_seq_no != 0 && !"1".equals(status)) {
                this.messageBox("������״̬����ȷ���ɻ���");
                return;
            }
        }

        for (int i = tableParm.getCount("PACK_CODE") - 1; i >= 0; i--) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                tableParm.removeRow(i);
            }
        }
        if (tableParm.getCount("PACK_CODE") <= 0) {
            this.messageBox("û�б�������");
            return;
        }

        // ���һ�����ʿ��
        if (!checkData()) {
            return;
        }

        String org_code = this.getValueString("ORG_CODE");
        Timestamp datetime = SystemTool.getInstance().getDate();
        String disinfection = datetime.toString();
        disinfection = disinfection.substring(0, 4) + disinfection.substring(5, 7) +
            disinfection.substring(8, 10) + disinfection.substring(11, 13) +
            disinfection.substring(14, 16) + disinfection.substring(17, 19);
        // ��������
        TParm disinfectionParm = new TParm();
        for (int i = 0; i < tableParm.getCount("PACK_CODE"); i++) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            disinfectionParm.addData("ORG_CODE", org_code);
            disinfectionParm.addData("PACK_CODE",
                                     tableParm.getValue("PACK_CODE", i));
            disinfectionParm.addData("PACK_SEQ_NO",
                                     tableParm.getInt("PACK_SEQ_NO", i));
            disinfectionParm.addData("DISINFECTION_DATE", disinfection);
            disinfectionParm.addData("DISINFECTION", datetime);
            disinfectionParm.addData("QTY", tableParm.getDouble("QTY", i));
            disinfectionParm.addData("VALUE_DATE",
                                     tableM.getItemTimestamp(i, "VALUE_DATE"));
            disinfectionParm.addData("DISINFECTION_USER", Operator.getID());
            disinfectionParm.addData("OPT_USER", Operator.getID());
            disinfectionParm.addData("OPT_DATE", datetime);
            disinfectionParm.addData("OPT_TERM", Operator.getIP());
            if (this.getValueInt("PACK_SEQ_NO") == 0) {
                // ����Ź�������������
                disinfectionParm.addData("STOCK_QTY",
                                         tableParm.getDouble("QTY", i));
            }
            else {
                //��Ź�������������
                disinfectionParm.addData("STOCK_QTY", 0);
            }
            disinfectionParm.addData("STATUS", "0");
        }

        TParm parm = new TParm();
        parm.setData("INV_DISINFECTION", disinfectionParm.getData());
        parm.setData("MAP", map);
        //System.out.println("parm===" + parm);
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVBackPackAndDisnfectionAction", "onInsert", parm);
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        this.onPrint();
        this.onClear();
    }

    /**
     * ���������Ч��
     * @return boolean
     */
    private boolean checkValueDate() {
        for (int i = 0; i < tableM.getRowCount(); i++) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if ("".equals(tableM.getItemString(i, "VALUE_DATE"))) {
                this.messageBox("����д������Ч��");
                return false;
            }
        }
        return true;
    }

    /**
     * ���һ�����ʿ��
     * @return boolean
     */
    private boolean checkData() {
        for (int i = 0; i < tableM.getRowCount(); i++) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            TParm parm = tableM.getParmValue().getRow(i); 
            String sql = INVSQL.getINVPackStockDInfo(
                getValueString("ORG_CODE"), parm.getValue("PACK_CODE"),
                parm.getInt("PACK_SEQ_NO"), parm.getDouble("QTY"),parm.getInt("BATCH_NO"));
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            for (int j = 0; j < result.getCount("INV_CODE"); j++) {
                TParm stockMParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getINVStockQty(getValueString("ORG_CODE"),
                                          result.getValue("INV_CODE", j))));
                if (stockMParm.getDouble("STOCK_QTY", 0) <
                    result.getDouble("QTY", j)) {
                    this.messageBox("����" +
                                    stockMParm.getValue("INV_CHN_DESC", 0) +
                                    "��治��");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * ��շ���
     */
    public void onClear() {
        onOrgCodeAction();
        this.clearValue("ORG_CODE;PACK_CODE;PACK_SEQ_NO;PACK_DESC;SELECT_ALL");
        this.setValue("Disnfection", "Y");
        this.setValue("BACK", "Y");
        tableM.removeRowAll();
        tableD.removeRowAll();
        map = new HashMap();
    }

    /**
     * ������ⵥ
     */
    public void onExport() {
        Object obj = this.openDialog(
            "%ROOT%\\config\\inv\\INVSupDispenseChoose.x");
        if (obj == null)
            return;
        TParm parm = (TParm) obj;
        //System.out.println("parm===" + parm);
        TParm dispense_M = parm.getParm("DISPENSE_M");
        TParm dispense_D = parm.getParm("DISPENSE_D");
        this.setValue("ORG_CODE", dispense_M.getValue("FROM_ORG_CODE"));

        TParm parmD = new TParm();
        for (int i = 0; i < dispense_D.getCount("PACK_CODE"); i++) {
            parmD.addData("SELECT_FLG", "N");
            parmD.addData("PACK_DESC", dispense_D.getValue("PACK_DESC", i));
            parmD.addData("PACK_SEQ_NO", dispense_D.getInt("PACK_SEQ_NO", i));
            parmD.addData("QTY", dispense_D.getDouble("QTY", i));
            parmD.addData("STATUS", dispense_D.getValue("STATUS", i));
            parmD.addData("DISINFECTION_DATE", "");
            parmD.addData("VALUE_DATE", "");
            parmD.addData("DISINFECTION_USER", "");
            parmD.addData("USE_COST", dispense_D.getDouble("USE_COST", i));
            parmD.addData("ONCE_USE_COST", dispense_D.getDouble("ONCE_USE_COST", i));
            parmD.addData("PACK_CODE", dispense_D.getValue("PACK_CODE", i));
        }
        tableM.setParmValue(parmD);
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (tableM.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        TParm parm = tableM.getParmValue();
        ////////////////////////////////////
    }

    /**
     * ȫѡ����
     */
    public void onSelectAll() {
        tableM.acceptText();
        Timestamp datetime = SystemTool.getInstance().getDate();
        String user = Operator.getID();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < tableM.getRowCount(); i++) {
                tableM.setItem(i, "SELECT_FLG", true);
                tableM.setItem(i, "DISINFECTION_DATE", datetime);
                tableM.setItem(i, "DISINFECTION_USER", user);
                tableM.setItem(i, "VALUE_DATE", StringTool.rollDate(datetime, 1));
            }
        }
        else {
            for (int i = 0; i < tableM.getRowCount(); i++) {
                tableM.setItem(i, "SELECT_FLG", false);
                tableM.setItem(i, "DISINFECTION_DATE", "");
                tableM.setItem(i, "DISINFECTION_USER", "");
                tableM.setItem(i, "VALUE_DATE", "");
            }
        }
    }

    /**
     * �������¼�
     */
    public void onTableMClicked() {
        TParm parm = tableM.getParmValue().getRow(tableM.getSelectedRow());
        String sql = INVSQL.getINVPackStockDInfo(   
            getValueString("ORG_CODE"), parm.getValue("PACK_CODE"),
            parm.getInt("PACK_SEQ_NO"), parm.getDouble("QTY"),parm.getInt("BATCH_NO"));
        //System.out.println("sql=="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        tableD.setParmValue(result);
        // ��ʾ�����¼
        String org_code = this.getValueString("ORG_CODE");
        String pack_code = parm.getValue("PACK_CODE");
        int pack_seq_no = parm.getInt("PACK_SEQ_NO");
        String inv_code = "";
        String key = "";
        int value = 0;
        for (int i = 0; i < result.getCount("INV_CODE"); i++) {
            inv_code = result.getValue("INV_CODE", i);
            key = org_code + "|" + pack_code + "|" + pack_seq_no + "|" +
                inv_code;
            if (!map.containsKey(key)) {
                continue;
            }
            value = TypeTool.getInt(map.get(key));
            tableD.setItem(i, "RECOUNT_TIME", value);
            tableD.getParmValue().setData("RECOUNT_TIME", i, value);
        }
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        double pack_seq_no = tableM.getItemDouble(tableM.getSelectedRow(),
                                                  "PACK_SEQ_NO");
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 3) {
            double qty = TypeTool.getDouble(node.getValue());
            if (pack_seq_no > 0) {
                if (qty != 1) {
                    this.messageBox("������������ֻ�ܵ���Ϊ1");
                    return true;
                }
            }
            else {
                if (qty <= 0) {
                    this.messageBox("����������������С�ڻ����0");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ���ֵ�ı��¼�
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
        int column = node.getColumn();
        int row_m = tableM.getSelectedRow();
        int row_d = node.getRow();
        if (column == 6) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("�����������С��0");
                return true;
            }
            //�洢�����¼
            TParm tableMParm = tableM.getParmValue();
            TParm tableDParm = tableD.getParmValue();
            String org_code = this.getValueString("ORG_CODE");
            String pack_code = tableMParm.getValue("PACK_CODE", row_m);
            int pack_seq_no = tableMParm.getInt("PACK_SEQ_NO", row_m);
            String inv_code = tableDParm.getValue("INV_CODE", row_d);
            String key = org_code + "|" + pack_code + "|" + pack_seq_no + "|" +
                inv_code;
            map.put(key, node.getValue());
            return false;
        }
        return true;
    }

    /**
     * ���(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        tableM.acceptText();
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
     * �õ�CheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
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
}
