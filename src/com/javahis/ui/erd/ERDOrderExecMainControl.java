package com.javahis.ui.erd;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;
import jdo.erd.ErdForBedAndRecordTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SysPhaBarTool;
import jdo.sys.SystemTool;


/**
 * <p> Title: �������ۻ�ʿվִ�� </p>
 * 
 * <p> Description: </p>
 * 
 * <p> Description: �������ۻ�ʿվִ�� </p>
 * 
 * <p> Copyright: JAVAHIS </p>
 * 
 * @author ZangJH 2009-9-10
 * 
 * @version 1.0
 */
public class ERDOrderExecMainControl
        extends TControl {

    /**
     * �����ϵ�UI����
     */
    private TTextFormat from_Date;
    private TTextField from_Time;
    private TTextFormat to_Date;
    private TTextField to_Time;
    // ҽ������
    private TRadioButton ord2All;
    private TRadioButton ord2PHA;
    private TRadioButton ord2PL;
    // ���״̬
    private TRadioButton checkAll;
    private TRadioButton checkYes;
    private TRadioButton checkNo;
    // ҩ������
    TCheckBox typeO;
    TCheckBox typeE;
    TCheckBox typeI;
    TCheckBox typeF;
    // ��table
    private TTable mainTable;
    // ȫ��ִ��
    private TCheckBox exeAll;
    // ȫ����ӡ
    private TCheckBox printAll;
    // ȫ����ӡ
    private TCheckBox reviseDate;
    private String caseNo = "";
    private String mrNo = "";
    private String patName = "";

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        // �õ��ⲿ�Ĳ���
        TParm outsideParm = (TParm) this.getParameter();
        if (outsideParm != null) {
            this.setCaseNo(outsideParm.getValue("CASE_NO"));
            this.setMrNo(outsideParm.getValue("MR_NO"));
            this.setPatName(outsideParm.getValue("PAT_NAME"));
        }
        // �õ�ʱ��ؼ�
        from_Date = (TTextFormat) this.getComponent("from_Date");
        from_Time = (TTextField) this.getComponent("from_Time");
        to_Date = (TTextFormat) this.getComponent("to_Date");
        to_Time = (TTextField) this.getComponent("to_Time");
        // �õ�table�ؼ�
        mainTable = (TTable) this.getComponent("MAINTABLE");
        // ��tableע��CHECK_BOX_CLICKED��������¼�
        this.callFunction("UI|MAINTABLE|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
                          "onTableCheckBoxClicked");
        mainTable.addEventListener(mainTable.getTag() + "->" + TTableEvent.CHANGE_VALUE, this,
                                   "onChangeDateTime");
        ord2All = (TRadioButton) this.getComponent("ord2All");// ȫ��
        ord2PHA = (TRadioButton) this.getComponent("ord2PHA");// ҩ��
        ord2PL = (TRadioButton) this.getComponent("ord2PL");// ����
        checkAll = (TRadioButton) this.getComponent("checkAll");// ȫ��
        checkYes = (TRadioButton) this.getComponent("checkYES");// ��ִ��
        checkNo = (TRadioButton) this.getComponent("checkNO");// δִ��
        typeO = (TCheckBox) this.getComponent("typeO");// �ڷ�
        typeE = (TCheckBox) this.getComponent("typeE");// ����
        typeI = (TCheckBox) this.getComponent("typeI");// ���
        typeF = (TCheckBox) this.getComponent("typeF");// ���
        exeAll = (TCheckBox) this.getComponent("exeALL");// ִ��ȫ��
        printAll = (TCheckBox) this.getComponent("printALL");// ��ӡȫ��
        reviseDate = (TCheckBox) this.getComponent("reviseDate");// ��¼��
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // �ý����00��00��ʼ����ʼʱ��
        from_Date.setValue(date);
        from_Time.setValue("00:00");
        to_Date.setValue(date);
        to_Time.setValue("23:59");
        this.setValue("EXESHEET_TYPE", "O");// ҽ��ִ�е�����
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    onQuery();// ִ�в�ѯ
                }
                catch (Exception e) {}
            }
        });
    }

    /**
     * ��ý����ϵ����в�ѯ����
     * 
     * @return TParm
     */
    public TParm getQueryData() {
        TParm result = new TParm();
        if (!TCM_Transform.isNull(this.getCaseNo())) {
            result.setData("CASE_NO", getCaseNo());
        }
        // ҽ������
        if (ord2PHA.isSelected()) { // ҩ��
            result.setData("CAT1_TYPEPHA", "Y");
        } else if (ord2PL.isSelected()) { // ����(������)
            result.setData("CAT1_TYPEPL", "Y");
        }
        String doseType = "";// �÷�
        if (typeO.isSelected()) { // �ڷ�
            doseType += "O";
        }
        if (typeE.isSelected()) { // ����
            doseType += "E";
        }
        if (typeI.isSelected()) { // ���
            doseType += "I";
        }
        if (typeF.isSelected()) { // ���
            doseType += "F";
        }
        if (!doseType.equals("")) {
            result.addData("DOSE_TYPE" + doseType, "Y");
        }
        if (checkYes.isSelected()) { // ��ִ��
            result.setData("EXEC_YES", "Y");
            String fromDate = StringTool.getString((Timestamp) from_Date.getValue(), "yyyyMMdd");
            String fromTime = (String) from_Time.getValue();
            String fromCheckDate = fromDate + fromTime.substring(0, 2) + fromTime.substring(3);
            String toDate = StringTool.getString((Timestamp) to_Date.getValue(), "yyyyMMdd");
            String toTime = (String) to_Time.getValue();
            String toCheckDate = toDate + toTime.substring(0, 2) + toTime.substring(3);
            result.setData("NS_EXEC_DATE", "Y");
            result.setData("fromCheckDate", fromCheckDate);
            result.setData("toCheckDate", toCheckDate);
            this.setValue("exeALL", "Y");
        } else if (checkNo.isSelected()) { // δִ��
            result.setData("EXEC_NO", "Y");
            this.setValue("exeALL", "N");
        } else {
            this.setValue("exeALL", "N");
        }
        return result;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm selParm = new TParm();
        selParm = getQueryData();
        TParm result = ErdForBedAndRecordTool.getInstance().selOrderExec(selParm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");// ִ��ʧ��
            return;
        }
        if (result.getCount() < 1) {
            mainTable.setParmValue(result);
            this.messageBox("E0116");// û������
            return;
        }
        if (checkAll.isSelected() || checkYes.isSelected()) {
            for (int i = 0; i < result.getCount(); i++) {
                if (!result.getValue("NS_EXEC_CODE", i).equals("")) {
                    result.setData("EXE_FLG", i, "Y");
                }
            }
        }
        mainTable.setParmValue(result);
        exeAll.setEnabled(true);
        printAll.setSelected(false);
        reviseDate.setSelected(false);
    }

    /**
     * ���涯��
     */
    public boolean onSave() {
        mainTable.acceptText();
        boolean isExistFlg = false;
        if (checkAll.isSelected()) {
            this.messageBox("ȫ��״̬��\n���ɱ��棡");
            return false;
        }
        // ����Ƿ���ѡ�е�����
        for (int i = 0; i < mainTable.getRowCount(); i++) {
            boolean selFlg =
                    TypeTool.getBoolean(mainTable
                            .getValueAt(i, mainTable.getColumnIndex("EXE_FLG")));
            if ((checkNo.isSelected() && selFlg) || (checkYes.isSelected() && !selFlg)) {
                isExistFlg = true;
                break;
            }
        }
        // ���û�д���ѡ�������
        if (!isExistFlg) {
            this.messageBox("ûѡ�б������ݣ�");
            return false;
        }
        if (!checkPW()) {// ����û�����
            return false;
        }
        // ���ñ���
        if (checkNo.isSelected()) {
            if (!onExec()) {
                this.messageBox("E0001");
                onQuery();
                return false;
            }
        } // �����˱�ѡ��˵������ʱ��--ȡ����ˣ�����Ҫ��֤�Ƿ���ִ�е�
        else {
            if (!onUndoExec()) {
                this.messageBox("E0001");
                onQuery();
                return false;
            }
        }
        this.messageBox("P0001");
        onQuery();
        return true;
    }

    /**
     * ����������֤
     * 
     * @return boolean
     */
    public boolean checkPW() {
        String erdExe = "erdExe";
        String value = (String) this.openDialog("%ROOT%\\config\\inw\\passWordCheck.x", erdExe);
        if (value == null) {
            return false;
        }
        return value.equals("OK");
    }

    /**
     * ִ��
     * 
     * @return boolean
     */
    public boolean onExec() {
        // �õ���������--չ���˵�caseNo
        TParm execData = new TParm();
        TParm parmValue = mainTable.getParmValue();
        int rowCount = mainTable.getRowCount();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        for (int i = 0; i < rowCount; i++) {
            String caseNo = parmValue.getValue("CASE_NO", i);
            String rxNo = parmValue.getValue("RX_NO", i);
            String seqNo = parmValue.getValue("SEQ_NO", i);
            if (TypeTool.getBoolean(mainTable.getValueAt(i, mainTable.getColumnIndex("EXE_FLG")))) {
                execData.addData("CASE_NO", caseNo);
                execData.addData("RX_NO", rxNo);
                execData.addData("SEQ_NO", seqNo);
                execData.addData("OPT_USER", Operator.getID());
                execData.addData("OPT_DATE", now);
                execData.addData("OPT_TERM", Operator.getIP());
                execData.addData("ORDER_DATE", parmValue.getTimestamp("ORDER_DATE", i));
                Timestamp execDate =
                        TCM_Transform.getTimestamp(mainTable.getValueAt(i, mainTable
                                .getColumnIndex("NS_EXEC_DATE_DAY")));
                if (execDate == null) {
                    this.messageBox("���ڸ�ʽ����");
                    return false;
                }
                Timestamp execTime =
                        TCM_Transform.getTimestamp(mainTable.getValueAt(i, mainTable
                                .getColumnIndex("NS_EXEC_DATE_TIME")));
                if (execTime == null) {
                    this.messageBox("ʱ���ʽ����");
                    return false;
                }
                Timestamp checkDateTime =
                        StringTool.getTimestamp(StringTool.getString(execDate, "yyyy/MM/dd")
                                + StringTool.getString(execTime, "HH:mm:ss"), "yyyy/MM/ddHH:mm:ss");
                execData.addData("NS_EXEC_DATE", checkDateTime);
                String execNote =
                        TCM_Transform.getString(mainTable.getValueAt(i, mainTable
                                .getColumnIndex("NS_NOTE"))); // ��ʿ��ע
                if (!TCM_Transform.isNull(execNote)) {
                    execData.addData("NS_NOTE", execNote);
                } else {
                    execData.addData("NS_NOTE", "");
                }
                String setMainFlg = parmValue.getValue("SETMAIN_FLG", i);
                String orderSetGroupNo = parmValue.getValue("ORDERSET_GROUP_NO", i);
                if ("Y".equals(setMainFlg)) { // ������ҽ��
                    execData =
                            setOrderDetail(execData, caseNo, rxNo, orderSetGroupNo, now, parmValue
                                    .getTimestamp("ORDER_DATE", i), checkDateTime);
                }
            }
        }
        execData.setCount(execData.getCount("CASE_NO"));
        TParm result =
                TIOM_AppServer.executeAction("action.erd.ERDOrderExecAction", "onSave", execData);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * ȡ��ִ��
     * 
     * @return boolean
     */
    public boolean onUndoExec() {
        // �õ���������--չ���˵�caseNo
        TParm execData = new TParm();
        TParm parmValue = mainTable.getParmValue();
        int rowCount = mainTable.getRowCount();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        for (int i = 0; i < rowCount; i++) {
            String caseNo = parmValue.getValue("CASE_NO", i);
            String rxNo = parmValue.getValue("RX_NO", i);
            String seqNo = parmValue.getValue("SEQ_NO", i);
            if (!TypeTool.getBoolean(mainTable.getValueAt(i, mainTable.getColumnIndex("EXE_FLG")))) {
                execData.addData("CASE_NO", caseNo);
                execData.addData("RX_NO", rxNo);
                execData.addData("SEQ_NO", seqNo);
                execData.addData("OPT_USER", Operator.getID());
                execData.addData("OPT_DATE", now);
                execData.addData("OPT_TERM", Operator.getIP());
                execData.addData("ORDER_DATE", parmValue.getTimestamp("ORDER_DATE", i));
                // ������ҽ��
                String setMainFlg = parmValue.getValue("SETMAIN_FLG", i);
                String orderSetGroupNo = parmValue.getValue("ORDERSET_GROUP_NO", i);
                if ("Y".equals(setMainFlg)) {
                    execData =
                            setOrderDetail(execData, caseNo, rxNo, orderSetGroupNo, now, parmValue.getTimestamp("ORDER_DATE", i), null);
                }
                execData.addData("NS_NOTE", ""); // ��ʿ��ע
            }
        }
        execData.setCount(execData.getCount("CASE_NO"));
        TParm result =
                TIOM_AppServer.executeAction("action.erd.ERDOrderExecAction", "onUndoSave",
                                             execData);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * �õ�����ҽ��ϸ���̨������
     * @param parm
     * @param caseNo
     * @param orderNo
     * @param orderSetGroupNo
     * @param now
     * @param orderDate
     * @param execDate
     * @return
     */
    private TParm setOrderDetail(TParm parm, String caseNo, String orderNo, String orderSetGroupNo,
                                 Timestamp now, Timestamp orderDate, Timestamp execDate) {
        String sql =
                "SELECT * FROM OPD_ORDER WHERE CASE_NO='" + caseNo + "' AND RX_NO='" + orderNo
                        + "' AND ORDERSET_GROUP_NO=" + TCM_Transform.getInt(orderSetGroupNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            String setMainFlg = result.getValue("SETMAIN_FLG", i);
            if (!"Y".equals(setMainFlg)) { // ��Ϊ�����Ѿ�������ѹ�룬���Կ��Բ��ظ�����
                parm.addData("CASE_NO", caseNo);
                parm.addData("RX_NO", orderNo);
                parm.addData("SEQ_NO", result.getValue("SEQ_NO", i));
                parm.addData("ORDER_DATE", orderDate);
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", now);
                parm.addData("OPT_TERM", Operator.getIP());
//                parm.addData("ORDER_DATE", orderDate);   //delete by huangtt 20150709 ɾ���ظ���ORDER_DATE��ֵ
                // �����˹��޸�
                parm.addData("NS_EXEC_DATE", execDate);
                parm.addData("NS_NOTE", "");
            }
        }
        return parm;
    }

    /**
     * table�ϵ�checkBox��ѡ�¼�
     * 
     * @param obj
     *            Object
     */
    public void onTableCheckBoxClicked(Object obj) {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        TTable table = (TTable) obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        int row = table.getSelectedRow();
        TParm parmValue = table.getParmValue();
        int rowCount = table.getRowCount();
        String columnName = table.getParmMap(col);
        if (columnName.equals("EXE_FLG")) {
            boolean exeFlg;
            exeFlg = TypeTool.getBoolean(table.getValueAt(row, col)); // ��õ��ʱ��ֵ
            if (exeFlg) { // ��ѡ
                selection(row, Operator.getName(), now);
                // �õ�ѡ�����ݵ�ҽ�����ͣ����������ǲ�ͬҽ�����͸��Լ������Ӻţ�����Ϊ�˱����ڡ�ȫ������״���»�����ظ����Ӻ������
                String rxKind = parmValue.getValue("RX_KIND", row);
                // -----------------------��������ҽ��start----------------------------
                // �ҵ���ͬ�����Ӻ�
                // String linkNo = (String) table.getValueAt(row, mainTable.getColumnIndex("LINK_NO"));
                String linkNo = parmValue.getValue("LINK_NO", row);
                if (TypeTool.getBoolean(linkNo)) {
                    for (int i = 0; i < rowCount; i++) {
                        // ���˵�ǰ������к������
                        if (i != row && linkNo.equals(parmValue.getValue("LINK_NO", i))
                                && rxKind.equals(parmValue.getValue("RX_KIND", i))) {
                            selection(i, Operator.getName(), now);
                        }
                    }
                }
            } else { // ȡ����ѡ
                unselection(row);
                // �õ�ѡ�����ݵ�ҽ�����ͣ����������ǲ�ͬҽ�����͸��Լ������Ӻţ�����Ϊ�˱����ڡ�ȫ������״���»�����ظ����Ӻ������
                String rxKind = parmValue.getValue("RX_KIND", row);
                // -----------------------��������ҽ��start----------------------------
                // �ҵ���ͬ�����Ӻ�
                // String linkNo = (String) table.getValueAt(row, mainTable.getColumnIndex("LINK_NO"));
                String linkNo = parmValue.getValue("LINK_NO", row);
                if (TypeTool.getBoolean(linkNo)) {
                    for (int i = 0; i < rowCount; i++) {
                        // ���˵�ǰ������к������
                        if (i != row && linkNo.equals(parmValue.getValue("LINK_NO", i))
                                && rxKind.equals(parmValue.getValue("RX_KIND", i))) {
                            unselection(i);
                        }
                    }
                }
            }
            // -----------------------end----------------------------------------
        }
    }

    /**
     * ѡ��
     * @param i
     * @param optName
     * @param execTime
     */
    private void selection(int i, String optName, Timestamp execTime) {
        mainTable.setValueAt(true, i, mainTable.getColumnIndex("EXE_FLG"));
        mainTable.setValueAt(optName, i, mainTable.getColumnIndex("NS_EXEC_CODE"));
        mainTable.setItem(i, "NS_EXEC_DATE_DAY", execTime);
        mainTable.setItem(i, "NS_EXEC_DATE_TIME", execTime);

    }

    /**
     * ȡ��ѡ��
     * @param i
     */
    private void unselection(int i) {
        mainTable.setValueAt(false, i, mainTable.getColumnIndex("EXE_FLG"));
        mainTable.setValueAt("", i, mainTable.getColumnIndex("NS_EXEC_CODE"));
        mainTable.setValueAt("", i, mainTable.getColumnIndex("NS_EXEC_DATE_DAY"));
        mainTable.setValueAt("", i, mainTable.getColumnIndex("NS_EXEC_DATE_TIME"));
    }

    /**
     * �޸�ִ�����ں�ʱ�䣨Ϊ������ҽ����
     * 
     * @param node
     */
    public boolean onChangeDateTime(TTableNode node) {
        int col = node.getColumn();
        String colName = mainTable.getParmMap(col);
        int row = node.getRow();// wanglong modify 20150128
        TParm parmValue = mainTable.getParmValue();
        TParm rowParm = parmValue.getRow(row);
        Map map = new HashMap();
        if ("ORDER_DATE".equals(colName)) {
            Timestamp temp = (Timestamp) node.getValue();
            if (temp == null) {
                this.messageBox("ʱ���ʽ����");
                node.setValue(node.getOldValue());
                return true;
            }
            if (!rowParm.getValue("LINK_NO").equals("")) {
                String linkStr =
                        rowParm.getValue("CASE_NO") + rowParm.getValue("RX_NO")
                                + rowParm.getValue("LINK_NO");
                map.put(linkStr, linkStr);
                int count = parmValue.getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = parmValue.getRow(i);
                    String linkStrTemp =
                            parm.getValue("CASE_NO") + parm.getValue("RX_NO")
                                    + parm.getValue("LINK_NO");
                    if (map.get(linkStrTemp) != null) {
                        mainTable.setItem(i, "ORDER_DATE", temp);
                    }
                }
            }
        }
        if ("NS_EXEC_DATE_DAY".equals(colName)) {
            Timestamp temp = (Timestamp) node.getValue();
            if (temp == null) {
                this.messageBox("���ڸ�ʽ����");
                node.setValue(node.getOldValue());
                return true;
            }
            if (!rowParm.getValue("LINK_NO").equals("")) {
                String linkStr =
                        rowParm.getValue("CASE_NO") + rowParm.getValue("RX_NO")
                                + rowParm.getValue("LINK_NO");
                map.put(linkStr, linkStr);
                int count = mainTable.getParmValue().getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = parmValue.getRow(i);
                    String linkStrTemp =
                            parm.getValue("CASE_NO") + parm.getValue("RX_NO")
                                    + parm.getValue("LINK_NO");
                    if (map.get(linkStrTemp) != null) {
                        mainTable.setItem(i, "NS_EXEC_DATE", temp);
                    }
                }
            }
        }
        if ("NS_EXEC_DATE_TIME".equals(colName)) {
            Timestamp temp = (Timestamp) node.getValue();
            if (temp == null) {
                this.messageBox("ʱ���ʽ����");
                node.setValue(node.getOldValue());
                return true;
            }
            if (!rowParm.getValue("LINK_NO").equals("")) {
                String linkStr =
                        rowParm.getValue("CASE_NO") + rowParm.getValue("RX_NO")
                                + rowParm.getValue("LINK_NO");
                map.put(linkStr, linkStr);
                int count = mainTable.getParmValue().getCount();
                for (int i = 0; i < count; i++) {
                    TParm parm = parmValue.getRow(i);
                    String linkStrTemp =
                            parm.getValue("CASE_NO") + parm.getValue("RX_NO")
                                    + parm.getValue("LINK_NO");
                    if (map.get(linkStrTemp) != null) {
                        mainTable.setItem(i, "NS_EXEC_DATE_TIME", temp);
                    }
                }
            }
        }
        return false;
    }

    /**
     * ҽ������ѡ���¼�
     * 
     * @param flg
     */
    public void selDOSE(Object flg) {
        // ���ѡ��
        typeO.setSelected(false);
        typeE.setSelected(false);
        typeI.setSelected(false);
        typeF.setSelected(false);
        ((TCheckBox) this.getComponent("DOSE_ALL")).setSelected(false);
        boolean temp = TypeTool.getBoolean(flg);
        // �༭״̬
        typeO.setEnabled(temp);
        typeE.setEnabled(temp);
        typeI.setEnabled(temp);
        typeF.setEnabled(temp);
        ((TCheckBox) this.getComponent("DOSE_ALL")).setEnabled(temp);
        // ���table
        mainTable.setParmValue(new TParm());
        exeAll.setSelected(false);
    }

    /**
     * �������ȫѡ�¼�
     */
    public void doseSelAll() {
        if (!((TCheckBox) getComponent("DOSE_ALL")).isSelected()) {
            typeO.setSelected(false);
            typeE.setSelected(false);
            typeI.setSelected(false);
            typeF.setSelected(false);
        } else {
            typeO.setSelected(true);
            typeE.setSelected(true);
            typeI.setSelected(true);
            typeF.setSelected(true);
        }
    }

    /**
     * ȫ��ִ��
     */
    public void onCheck() {
        boolean nowFlag = exeAll.isSelected();
        // ��ȫ��ִ�е�ʱ������һ��ʱ��
        Timestamp checkTime = TJDODBTool.getInstance().getDBTime();
        String optName = Operator.getName();
        // �õ�����
        int count = mainTable.getRowCount();
        for (int i = 0; i < count; i++) {
            // ѭ��ȡ���Թ�
            if (nowFlag) {
                selection(i, optName, checkTime);
            } else { // ȡ�����
                unselection(i);
            }
        }
    }

    /**
     * ȫ����ӡ
     */
    public void onAllPrint() {
        boolean flag = printAll.isSelected();
        // �õ�����
        int count = mainTable.getRowCount();
        for (int i = 0; i < count; i++) {
            mainTable.setItem(i, "PRINT_FLG", flag);
        }
    }

    /**
     * ��¼��
     */
    public void onReviseOrderDate() {
        boolean flag = reviseDate.isSelected();
        if (flag) {
            // �õ�����
            int count = mainTable.getRowCount();
            for (int i = 0; i < count; i++) {
                Timestamp execDateTemp =
                        TCM_Transform.getTimestamp(mainTable.getValueAt(i, mainTable
                                .getColumnIndex("NS_EXEC_DATE_DAY")));
                Timestamp execTimeTemp =
                        TCM_Transform.getTimestamp(mainTable.getValueAt(i, mainTable
                                .getColumnIndex("NS_EXEC_DATE_TIME")));
                if (execDateTemp != null && execTimeTemp != null) {
                    Timestamp execDate =
                            StringTool.getTimestamp(StringTool
                                                            .getString(execDateTemp, "yyyy/MM/dd")
                                                            + StringTool.getString(execTimeTemp,
                                                                                   "HH:mm:ss"),
                                                    "yyyy/MM/ddHH:mm:ss");
                    Timestamp newOrderDate = new Timestamp(execDate.getTime() - 30 * 1000);
                    mainTable.setItem(i, "ORDER_DATE", newOrderDate);
                }
            }
        }
    }
    
    /**
     * ��ӡ
     */
    public void onPrint() {
        if (this.getValueString("EXESHEET_TYPE").length() == 0) {
            this.messageBox("��ѡ��ҽ��ִ�е�����");
            return;
        }
        TParm selectData = getExeSheetParm();
        clearNullAndCode(selectData);
        if (selectData.getCount() <= 0) {
            this.messageBox("�޴�ӡ����");
            return;
        }
        TParm printParm = new TParm();
        // �ж�ִ�����ִ�ӡ��
        if ("O".equals(this.getValueString("EXESHEET_TYPE"))) { // �ڷ���
            printParm = arrangeData(selectData, "O");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " " + from_Time.getText()
                    + "��" + to_Date.getText() + " " + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow("%ROOT%\\config\\prt\\erd\\ERDExecOrderPrt_O.jhw", data);
        } else if ("I".equals(this.getValueString("EXESHEET_TYPE"))) { // ע��
            printParm = arrangeData(selectData, "I");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " " + from_Time.getText()
                    + "��" + to_Date.getText() + " " + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow("%ROOT%\\config\\prt\\erd\\ERDExecOrderPrt_I.jhw", data);
        } else if ("F".equals(this.getValueString("EXESHEET_TYPE"))) { // ��Һ
            printParm = arrangeData(selectData, "F");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            TParm data = new TParm();
            data.setData("NAME", "TEXT", this.getPatName());
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            ;
            // ͳ��ʱ��
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " " + from_Time.getText()
                    + "��" + to_Date.getText() + " " + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow("%ROOT%\\config\\prt\\erd\\ERDExecOrderPrt_F.jhw", data);
        } else { // ��ִͨ�е�
            printParm = arrangeData(selectData, "COM");
            if (printParm.getCount() <= 0) {
                messageBox("�޴�ӡ����");
                return;
            }
            printParm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
            printParm.addData("SYSTEM", "COLUMNS", "BED_NO");
            printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
            printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
            printParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
            TParm data = new TParm();
            // ����
            data.setData("STATION", "TEXT", selectData.getValue("REGION_DESC", 0));
            data.setData("RX_TYPE", "TEXT", "ȫ��");
            data.setData("PAT_INFO", "TEXT", "�����:"
                    + getCaseNo()
                    + "  ��λ:"
                    + StringUtil.getDesc("ERD_BED", "BED_DESC", "BED_NO = '"
                            + selectData.getValue("BED_NO", 0) + "'" + " AND   ERD_REGION_CODE = '"
                            + selectData.getValue("ERD_REGION_CODE", 0) + "'") + "  ��������:"
                    + getPatName());
            // ͳ��ʱ��
            data.setData("FROMTODATE", "TEXT", from_Date.getText() + " " + from_Time.getText()
                    + "��" + to_Date.getText() + " " + to_Time.getText());
            data.setData("TABLE", printParm.getData());
            this.openPrintWindow("%ROOT%\\config\\prt\\erd\\ERDExecSheetPrt.jhw", data);
        }
    }

    /**
     * �����ӡ
     */
    public void onBarCode() {
        String sql1 = "SELECT * FROM REG_PATADM WHERE CASE_NO = '" + this.getCaseNo() + "'";
        TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
        String mrNo = result1.getValue("MR_NO", 0);
        String sql2 = "SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + mrNo + "'";
        TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
        TParm parm = new TParm();
        // ����
        parm.setData("DEPT_CODE", result1.getValue("DEPT_CODE", 0));
        parm.setData("ADM_TYPE", "E");
        parm.setData("CASE_NO", this.getCaseNo());
        parm.setData("MR_NO", result1.getValue("MR_NO", 0));
        parm.setData("PAT_NAME", result2.getValue("PAT_NAME", 0));
        parm.setData("ADM_DATE", result1.getData("ADM_DATE", 0));
        parm.setData("CLINICAREA_CODE", result1.getValue("CLINICAREA_CODE", 0));
        parm.setData("CLINICROOM_NO", result1.getValue("CLINICROOM_NO", 0));
        parm.setData("POPEDEM", "1");
        openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
    }

    /**
     * ҽ����
     */
    public void onOrderPrt() {
        TParm parm = new TParm();
        parm.setData("INW", "CASE_NO", this.getCaseNo());
        this.openDialog("%ROOT%\\config\\erd\\ERDOrderSheetPrtAndPreView.x", parm);
    }

    /**
     * ���µ�
     */
    public void onTempPrt() {
        String sql =
                " SELECT B.CHN_DESC,A.BED_DESC FROM ERD_BED A,SYS_DICTIONARY B "
                        + " WHERE B.GROUP_ID='ERD_REGION' " + " AND B.ID=A.ERD_REGION_CODE "
                        + " AND A.CASE_NO='" + getCaseNo() + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        TParm sumParm = new TParm();
        sumParm.setData("SUM", "CASE_NO", getCaseNo());
        sumParm.setData("SUM", "MR_NO", getMrNo());
        sumParm.setData("SUM", "IPD_NO", "");
        sumParm.setData("SUM", "STATION_CODE", result.getValue("CHN_DESC", 0));
        sumParm.setData("SUM", "BED_NO", result.getValue("BED_DESC", 0));
        sumParm.setData("SUM", "ADM_TYPE", "E");
        this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm);
    }

    /**
     * �����¼
     */
    public void onNurseRec() {
        TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "EMG");
        parm.setData("ADM_TYPE", "E");
        parm.setData("CASE_NO", getCaseNo());
        parm.setData("PAT_NAME", getPatName());
        parm.setData("MR_NO", getMrNo());
        parm.setData("IPD_NO", "");
        parm.setData("ADM_DATE", getAdmDate());
        parm.setData("DEPT_CODE", Operator.getDept());
        parm.setData("RULETYPE", "2");
        parm.setData("EMR_DATA_LIST", new TParm());
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }

    private Object getAdmDate() {
        return new TParm(TJDODBTool.getInstance().select(
                                                         " SELECT ADM_DATE " + " FROM REG_PATADM "
                                                                 + " WHERE CASE_NO = '"
                                                                 + getCaseNo() + "'"))
                .getData("ADM_DATE", 0);
    }

    /**
     * �õ�ִ�е�SQL
     * 
     * @param caseNo
     *            String
     * @return String
     */
    private String getExeSheetSQL(String caseNo) {
        String doseType = "";
        if ("O".equals(getValueString("EXESHEET_TYPE"))) doseType =
                " AND F.CLASSIFY_TYPE = 'O'  AND CAT1_TYPE LIKE 'PHA%' ";
        else if ("I".equals(getValueString("EXESHEET_TYPE"))) doseType =
                " AND F.CLASSIFY_TYPE = 'I' AND CAT1_TYPE LIKE 'PHA%' ";
        else if ("F".equals(getValueString("EXESHEET_TYPE"))) doseType =
                " AND F.CLASSIFY_TYPE = 'F' AND CAT1_TYPE LIKE 'PHA%' ";
        else doseType =
                " AND ((F.CLASSIFY_TYPE = 'E' AND CAT1_TYPE LIKE 'PHA%') OR (CAT1_TYPE NOT LIKE  'PHA%') )";
        String SQL =
                " SELECT A.CASE_NO,A.MR_NO,B.BED_NO,ORDER_CODE,ORDER_DESC,"
                        + "        MEDI_QTY||C.UNIT_CHN_DESC MEDI_QTY,A.ROUTE_CODE,FREQ_CODE,DR_NOTE,DISPENSE_QTY||D.UNIT_CHN_DESC DISPENSE_QTY,"
                        + "        LINKMAIN_FLG,LINK_NO,NS_EXEC_DATE,B.ERD_REGION_CODE,E.CHN_DESC REGION_DESC"
                        + " FROM OPD_ORDER A,ERD_BED B,SYS_UNIT C,SYS_UNIT D,SYS_DICTIONARY E,SYS_PHAROUTE F "
                        + " WHERE  A.CASE_NO = '" + caseNo + "'"
                        + doseType
                        + " AND    A.CASE_NO = B.CASE_NO"
                        + " AND    MEDI_UNIT = C.UNIT_CODE"
                        + " AND    DISPENSE_UNIT = D.UNIT_CODE"
                        +
                        // " AND    NS_EXEC_DATE IS NOT NULL"+
                        " AND    E.GROUP_ID = 'ERD_REGION'" + " AND    B.ERD_REGION_CODE = E.ID"
                        + " AND    A.ROUTE_CODE = F.ROUTE_CODE(+)"
                        + " AND    ((A.ORDERSET_CODE IS NOT NULL AND A.ORDERSET_CODE = ORDER_CODE)"
                        + "       OR(A.ORDERSET_CODE IS NULL))";
        return SQL;
    }

    /**
     * �����ֵ
     * 
     * @param parm
     *            TParm
     */
    private void clearNullAndCode(TParm parm) {
        String names[] = parm.getNames();
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < parm.getCount(names[i]); j++) {
                if (parm.getData(names[i], j) == null
                        || parm.getValue(names[i], j).equalsIgnoreCase("null")) parm
                        .setData(names[i], j, "");
            }
        }
    }

    private TParm getExeSheetParm() {
        return new TParm(TJDODBTool.getInstance().select(getExeSheetSQL(getCaseNo())));
    }

    /**
     * ��������
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm, String flg) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            // �ж�����ҽ��
            if (!StringUtil.isNullString(order.getValue("LINK_NO"))) {
                // ���Ϊ����ҽ��ϸ�����账��
                if (!TypeTool.getBoolean(order.getData("LINKMAIN_FLG"))) continue;
                String finalOrder = getLinkOrder(order, parm);
                String medi = getLinkQty(order, parm);
                result.addData("ORDER_DESC", finalOrder);
                result.addData("MEDI_QTY", medi);
            } else { // ��ͨҽ��
                String drNote = order.getValue("DR_NOTE");
                String desc = order.getValue("ORDER_DESC");
                // �ж��Ƿ���ҽ����ע
                if (parm.getValue("ORDER_CODE").startsWith("Z")) {
                    desc = drNote;
                    drNote = "";
                }
                String finalDesc = "" + desc;
                // ��Ҫ����--ҽ��
                result.addData("ORDER_DESC", finalDesc);
                result.addData("MEDI_QTY", "" + order.getData("MEDI_QTY"));
            }
            // ����ҽ���������ò�ͬ��������
            if ("O".equals(flg)) {
                result.addData("ROUTE_CODE", order.getValue("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getValue("FREQ_CODE"));
                result.addData("DISPENSE_QTY", order.getData("DISPENSE_QTY"));
            } else if ("I".equals(flg)) { // ע��
                result.addData("ROUTE_CODE", order.getValue("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getValue("FREQ_CODE"));
            } else if ("F".equals(flg)) { // ��Һ
                result.addData("ROUTE_CODE", order.getValue("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getValue("FREQ_CODE"));
                result.addData("DR_NOTE", order.getValue("DR_NOTE"));
            } else if ("COM".equals(flg)) {
                result.addData("CASE_NO", order.getValue("CASE_NO"));
                result.addData("MR_NO", order.getValue("MR_NO"));
                result.addData("BED_NO", order.getValue("BED_NO"));
                result.addData("ROUTE_CODE", order.getValue("ROUTE_CODE"));
                result.addData("FREQ_CODE", order.getValue("FREQ_CODE"));
                result.addData("NS_EXEC_DATE", (order.getData("NS_EXEC_DATE") == null
                        || order.getValue("NS_EXEC_DATE").length() == 0 || order
                        .getValue("NS_EXEC_DATE").equalsIgnoreCase("null")) ? "" : order
                        .getData("NS_EXEC_DATE").toString()
                        .substring(0, order.getData("NS_EXEC_DATE").toString().length() - 2));
                result.addData("DR_NOTE", order.getValue("DR_NOTE"));
            }
        }
        result.setCount(result.getCount("ORDER_DESC"));
        return result;
    }

    /**
     * ��������ҽ��ORDER_DESC
     * 
     * @param order
     *            TParm
     * @param parm
     *            TParm
     * @return String
     */
    private String getLinkOrder(TParm order, TParm parm) {
        String resultDesc = "";
        String mainOrder = order.getValue("ORDER_DESC");
        String mainLinkNo = order.getValue("LINK_NO");
        resultDesc = mainOrder;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = parm.getValue("LINK_NO", i);
            if (mainLinkNo.equals(linkNo) && !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subOrder = parm.getValue("ORDER_DESC", i);
                resultDesc += "\r" + subOrder;
            } else continue;
        }
        return resultDesc;
    }

    /**
     * ��������ҽ��MEDI_QTY
     * @param order
     *            TParm
     * @param parm
     *            TParm
     * @return String
     */
    private String getLinkQty(TParm order, TParm parm) {
        String resultString = "";
        String mainMediQty = (String) order.getData("MEDI_QTY");
        String mainLinkNo = order.getValue("LINK_NO");
        resultString = mainMediQty;
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = parm.getValue("LINK_NO", i);
            if (mainLinkNo.equals(linkNo) && !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
                String subMediQty = (String) parm.getData("MEDI_QTY", i);
                resultString += "\r" + subMediQty;
            } else continue;
        }
        return resultString;
    }

    /**
     * �ر��¼�
     * 
     * @return boolean
     */
    public boolean onClosing() {
        // switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
        // case 0:
        // if (!onSave())
        // return false;
        // break;
        // case 1:
        // break;
        // case 2:
        // return false;
        // }
        return true;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    /**
     * ��ʾ�˵�
     */
    public void onShowWindowsFunction() {
        callFunction("UI|showTopMenu");
    }

    /**
     * ������Һ����
     */
    public void generatePhaBarcode() {
        TParm opdOrder = new TParm();
        TParm tablValue = mainTable.getParmValue();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int rowCount = mainTable.getRowCount();
        int count = 0;
        TParm linkparm = new TParm();
        Map mapBarCode = new HashMap();
        Map linkmap = new HashMap();
        String caseNo = "";
        String rxNo = "";
        String seqNo = "";
        String linkNo = "";
        String orderCode = "";
        String orderDesc = "";
        String doseType = "";
        String barCode = "";
        String linkStr = "";
        String routeCode = "";
        for (int i = 0; i < rowCount; i++) {
            caseNo = tablValue.getValue("CASE_NO", i);
            rxNo = tablValue.getValue("RX_NO", i);
            seqNo = tablValue.getValue("SEQ_NO", i);
            linkNo = tablValue.getValue("LINK_NO", i);
            orderCode = tablValue.getValue("ORDER_CODE", i);
            orderDesc = tablValue.getValue("ORDER_DESC_AND_SPECIFICATION", i);
            doseType = "";
            routeCode = tablValue.getValue("ROUTE_CODE", i);
            if (TypeTool.getBoolean(mainTable.getValueAt(i, mainTable.getColumnIndex("EXE_FLG")))) {// ִ
                if (tablValue.getValue("CAT1_TYPE", i).equals("PHA")) {
                    if (routeCode.equals("")) {
                        this.messageBox(orderDesc + "�÷�Ϊ�գ������������룡");
                        return;
                    }
//                    doseType = SysPhaBarTool.getInstance().getClassifyType(routeCode);
//                    if (!doseType.equals("I") && !doseType.equals("F")) {
//                        this.messageBox(orderDesc + "����������Σ������������룡");
//                        return;
//                    }
                    // �ж�����ҽ����һ��һ�룩
                    if (!linkNo.equals("")) {
                        linkStr = caseNo + rxNo + linkNo;
                        if (linkmap.get(linkStr) == null) {
                            // ȡ��
                            barCode = SysPhaBarTool.getInstance().getBarCode();
                            mapBarCode.put(linkStr, barCode);
                        }
                        linkmap.put(linkStr, linkStr);
                        String sql = // ��ѯϸ���SQL
                                "SELECT CASE_NO,RX_NO,SEQ_NO,BAR_CODE FROM OPD_ORDER "
                                        + "WHERE CASE_NO='" + caseNo + "' AND RX_NO='" + rxNo
                                        + "' AND LINK_NO='" + linkNo
                                        + "' ORDER BY CASE_NO,RX_NO,SEQ_NO";
                        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                        if (result.getCount() <= 0) continue;
                        for (int j = 0; j < result.getCount(); j++) {
                            if (!result.getValue("BAR_CODE", j).equals("")) continue;
                            opdOrder.addData("CASE_NO", result.getValue("CASE_NO", j));
                            opdOrder.addData("RX_NO", result.getValue("RX_NO", j));
                            opdOrder.addData("SEQ_NO", result.getValue("SEQ_NO", j));
                            opdOrder.addData("BAR_CODE", (String) mapBarCode.get(linkStr));
                            opdOrder.addData("OPT_USER", Operator.getID());
                            opdOrder.addData("OPT_DATE", now);
                            opdOrder.addData("OPT_TERM", Operator.getIP());
                            count++;
                        }
                    } else {
                        barCode = SysPhaBarTool.getInstance().getBarCode(); // ȡ��
                        String sql = // ��ѯϸ���SQL
                                "SELECT CASE_NO,RX_NO,SEQ_NO,BAR_CODE FROM OPD_ORDER "
                                        + "WHERE CASE_NO='" + caseNo + "' AND RX_NO='" + rxNo
                                        + "' AND SEQ_NO=" + seqNo
                                        + " ORDER BY CASE_NO,RX_NO,SEQ_NO";
                        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
                        if (result.getCount() <= 0) continue;
                        for (int j = 0; j < result.getCount(); j++) {
                            if (!result.getValue("BAR_CODE", j).equals("")) continue;
                            opdOrder.addData("CASE_NO", result.getValue("CASE_NO", j));
                            opdOrder.addData("RX_NO", result.getValue("RX_NO", j));
                            opdOrder.addData("SEQ_NO", result.getValue("SEQ_NO", j));
                            opdOrder.addData("BAR_CODE", barCode + j);
                            opdOrder.addData("OPT_USER", Operator.getID());
                            opdOrder.addData("OPT_DATE", now);
                            opdOrder.addData("OPT_TERM", Operator.getIP());
                            count++;
                        }
                    }
                }
            }
        }
        opdOrder.setCount(count);
        if (count > 0) {
            TParm result = ErdForBedAndRecordTool.getInstance().generateIFBarcode(opdOrder);
            if (result.getErrCode() < 0) {
                this.messageBox("��������ʧ�ܣ�");
                return;
            }
            this.messageBox("��������ɹ���");
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < opdOrder.getCount(); j++) {
                    if (tablValue.getValue("CASE_NO", i).equals(opdOrder.getValue("CASE_NO", j))
                            && tablValue.getValue("RX_NO", i).equals(opdOrder.getValue("RX_NO", j))
                            && tablValue.getValue("SEQ_NO", i)
                                    .equals(opdOrder.getValue("SEQ_NO", j))) {
                        tablValue.setData("BAR_CODE", i, opdOrder.getValue("BAR_CODE", j));
                    }
                }
            }
            mainTable.setParmValue(tablValue);
        } else {
            this.messageBox("û����Ҫ��������������������");
        }
    }

    /**
     * ƿǩ��ӡ
     */
    public void onPrintPaster() {
        TParm parmValue = mainTable.getParmValue();
//        String orderCode = "";
        String orderDesc = "";
        String cat1Type = "";
//        String doseType = "";
//        String routeCode = "";
        List<String> barCodeList = new ArrayList<String>();
        for (int i = 0; i < parmValue.getCount(); i++) {
            if (TCM_Transform.getBoolean(mainTable.getValueAt(i, mainTable
                    .getColumnIndex("PRINT_FLG")))) {

//                orderCode = parmValue.getValue("ORDER_CODE", i);
                orderDesc = parmValue.getValue("ORDER_DESC", i);
                cat1Type = parmValue.getValue("CAT1_TYPE", i);
//                routeCode = parmValue.getValue("ROUTE_CODE", i);
                if (!cat1Type.equals("PHA")) {
                    this.messageBox(orderDesc + " ����ҩƷ�����ܴ�ӡ��");
                    return;
                }
                if (parmValue.getValue("BAR_CODE", i).equals("")) {
                    this.messageBox(orderDesc + "δ�������룬���ܴ�ӡ��");
                    return;
                }
//                doseType = SysPhaBarTool.getInstance().getClassifyType(routeCode);
//                if (!doseType.equals("I") && !doseType.equals("F")) {
//                    this.messageBox(orderDesc + "����������Σ����ܴ�ӡ��");
//                    return;
//                }
                if (!barCodeList.contains(parmValue.getValue("BAR_CODE", i))) {
                    barCodeList.add(parmValue.getValue("BAR_CODE", i));
                }
            }
        }
        if (barCodeList.size() < 1) {
            this.messageBox("�޴�ӡ����");
            return;
        }
        Collections.sort(barCodeList);
        String sql =
                "SELECT A.ERD_LEVEL, A.CLINICAREA_CODE, A.DEPT_CODE, A.DR_CODE, A.REALDEPT_CODE, A.REALDR_CODE,"
                        + "       B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, C.BED_NO, C.BED_DESC, C.ERD_REGION_CODE "
                        + "  FROM REG_PATADM A, SYS_PATINFO B, ERD_BED C "
                        + " WHERE A.CASE_NO = '#' AND A.MR_NO = B.MR_NO "
                        + "   AND A.CASE_NO = C.CASE_NO";
        sql = sql.replaceFirst("#", caseNo);
        TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrText());
            return;
        }
        String deptDesc =
                StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='"
                        + patParm.getValue("REALDEPT_CODE", 0) + "'");
        String clinicareaDesc =
                StringUtil.getDesc("REG_CLINICAREA", "CLINIC_DESC", "CLINICAREA_CODE='"
                        + patParm.getValue("CLINICAREA_CODE", 0) + "'");
        String sexDesc =
                StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "ID='"
                        + patParm.getValue("SEX_CODE", 0) + "' AND GROUP_ID='SYS_SEX'");
        Timestamp now = SystemTool.getInstance().getDate();
        String age = OdoUtil.showAge(patParm.getTimestamp("BIRTH_DATE", 0), now);
        String erdRegionDesc =
                StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "ID='"
                        + patParm.getValue("ERD_REGION_CODE", 0) + "' AND GROUP_ID='ERD_REGION'");
        for (String barCode : barCodeList) {
            TParm onePage = new TParm();
            onePage.setData("DEPT_DESC", "TEXT", deptDesc);
            onePage.setData("CLINICAREA_DESC", "TEXT", clinicareaDesc);
            onePage.setData("MR_NO", "TEXT", mrNo);
            onePage.setData("PAT_NAME", "TEXT", patParm.getValue("PAT_NAME", 0));
            onePage.setData("SEX_DESC", "TEXT", sexDesc);
            onePage.setData("AGE", "TEXT", age);
            onePage.setData("BED_DESC", "TEXT", patParm.getValue("BED_DESC", 0));
            onePage.setData("ERD_REGION_DESC", "TEXT", erdRegionDesc);
            onePage.setData("BAR_CODE", "TEXT", barCode);
            TParm orderParm = new TParm();
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (barCode.equals(parmValue.getValue("BAR_CODE", i))
                        && parmValue.getBoolean("PRINT_FLG", i)) {
                    String orderDescSpecification = parmValue.getValue("ORDERDESC", i);
                    if (countStrLength(parmValue.getValue("ORDERDESC", i)) > 24) {
                        orderDescSpecification = subStr(parmValue.getValue("ORDERDESC", i), 24);
                    }
                    orderParm.addData("ORDER_DESC_SPECIFICATION", orderDescSpecification);
                    orderParm.addData("MEDI_QTY_UNIT", numDot(parmValue.getDouble("MEDI_QTY", i))
                            + StringUtil.getDesc("SYS_UNIT", "UNIT_CHN_DESC", "UNIT_CODE='"
                                    + parmValue.getValue("MEDI_UNIT", i) + "'"));
                    orderParm.addData("DOSAGE_QTY_UNIT",numDot(parmValue.getDouble("DOSAGE_QTY", i))
                            + StringUtil.getDesc("SYS_UNIT", "UNIT_CHN_DESC", "UNIT_CODE='"
                                    + parmValue.getValue("DOSAGE_UNIT", i) + "'"));
                    onePage.setData("FREQ_CODE", "TEXT", parmValue.getValue("FREQ_CODE", i));
                    onePage.setData("ROUTE_CODE", "TEXT", parmValue.getValue("ROUTE_CODE", i));
                    onePage.setData("ORDER_DATE", "TEXT", StringTool.getString(parmValue
                            .getTimestamp("ORDER_DATE", i), "MM/dd HH:mm:ss"));
                    onePage.setData("DR_DESC", "TEXT", StringUtil
                            .getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='"
                                    + parmValue.getValue("DR_CODE", i) + "'"));
                }
            }
            orderParm.setCount(orderParm.getCount("ORDER_DESC_SPECIFICATION"));
            if (orderParm.getCount() > 0 && orderParm.getCount() <= 5) {
                for (int i = 0; i < orderParm.getCount(); i++) {
                    onePage.setData("ORDER_DESC_SPECIFICATION_" + (i + 1), "TEXT", orderParm
                            .getValue("ORDER_DESC_SPECIFICATION", i));
                    onePage.setData("MEDI_QTY_UNIT_" + (i + 1), "TEXT", orderParm
                            .getValue("MEDI_QTY_UNIT", i));
                    onePage.setData("DOSAGE_QTY_UNIT_" + (i + 1), "TEXT", orderParm
                            .getValue("DOSAGE_QTY_UNIT", i));
                }
                onePage.setData("PAGE_INDEX", "TEXT", "1/1");
                this.openPrintWindow("%ROOT%\\config\\prt\\ERD\\ERDPrintBottle.jhw", onePage, true);
            }
            if (orderParm.getCount() > 5 && orderParm.getCount() <= 10) {
                for (int i = 0; i < 5; i++) {
                    onePage.setData("ORDER_DESC_SPECIFICATION_" + (i + 1), "TEXT", orderParm
                            .getValue("ORDER_DESC_SPECIFICATION", i));
                    onePage.setData("MEDI_QTY_UNIT_" + (i + 1), "TEXT", orderParm
                            .getValue("MEDI_QTY_UNIT", i));
                    onePage.setData("DOSAGE_QTY_UNIT_" + (i + 1), "TEXT", orderParm
                            .getValue("DOSAGE_QTY_UNIT", i));
                }
                onePage.setData("PAGE_INDEX", "1/2");
                this.openPrintWindow("%ROOT%\\config\\prt\\ERD\\ERDPrintBottle.jhw", onePage, true);
                for (int i = 5; i < orderParm.getCount(); i++) {
                    onePage.setData("ORDER_DESC_SPECIFICATION_" + (i - 4), "TEXT", orderParm
                            .getValue("ORDER_DESC_SPECIFICATION", i));
                    onePage.setData("MEDI_QTY_UNIT_" + (i - 4), "TEXT", orderParm
                            .getValue("MEDI_QTY_UNIT", i));
                    onePage.setData("DOSAGE_QTY_UNIT_" + (i - 4), "TEXT", orderParm
                            .getValue("DOSAGE_QTY_UNIT", i));
                }
                onePage.setData("PAGE_INDEX", "TEXT", "2/2");
                this.openPrintWindow("%ROOT%\\config\\prt\\ERD\\ERDPrintBottle.jhw", onePage, true);
            }
            if (orderParm.getCount() > 10 && orderParm.getCount() <= 15) {
                for (int i = 0; i < 5; i++) {
                    onePage.setData("ORDER_DESC_SPECIFICATION_" + (i + 1), "TEXT", orderParm
                            .getValue("ORDER_DESC_SPECIFICATION", i));
                    onePage.setData("MEDI_QTY_UNIT_" + (i + 1), "TEXT", orderParm
                            .getValue("MEDI_QTY_UNIT", i));
                    onePage.setData("DOSAGE_QTY_UNIT_" + (i + 1), "TEXT", orderParm
                            .getValue("DOSAGE_QTY_UNIT", i));
                }
                onePage.setData("PAGE_INDEX", "TEXT", "1/3");
                this.openPrintWindow("%ROOT%\\config\\prt\\ERD\\ERDPrintBottle.jhw", onePage, true);
                for (int i = 5; i < 10; i++) {
                    onePage.setData("ORDER_DESC_SPECIFICATION_" + (i - 4), "TEXT", orderParm
                            .getValue("ORDER_DESC_SPECIFICATION", i));
                    onePage.setData("MEDI_QTY_UNIT_" + (i - 4), "TEXT", orderParm
                            .getValue("MEDI_QTY_UNIT", i));
                    onePage.setData("DOSAGE_QTY_UNIT_" + (i - 4), "TEXT", orderParm
                            .getValue("DOSAGE_QTY_UNIT", i));
                }
                onePage.setData("PAGE_INDEX", "TEXT", "2/3");
                this.openPrintWindow("%ROOT%\\config\\prt\\ERD\\ERDPrintBottle.jhw", onePage, true);
                for (int i = 10; i < orderParm.getCount(); i++) {
                    onePage.setData("ORDER_DESC_SPECIFICATION_" + (i - 9), "TEXT", orderParm
                            .getValue("ORDER_DESC_SPECIFICATION", i));
                    onePage.setData("MEDI_QTY_UNIT_" + (i - 9), "TEXT", orderParm
                            .getValue("MEDI_QTY_UNIT", i));
                    onePage.setData("DOSAGE_QTY_UNIT_" + (i - 9), "TEXT", orderParm
                            .getValue("DOSAGE_QTY_UNIT", i));
                }
                onePage.setData("PAGE_INDEX", "TEXT", "3/3");
                this.openPrintWindow("%ROOT%\\config\\prt\\ERD\\ERDPrintBottle.jhw", onePage, true);
            }
        }
    }

    /**
     * ����Ʒ�
     */
    public void onCharge() {
        TParm parm = new TParm();
        parm.setData("MR_NO", mrNo);
        parm.setData("CASE_NO", caseNo);
        parm.setData("SYSTEM", "ONW");
        this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", parm);
    }

    /**
     * �����ӡ
     */
    public void onWrist() {// wanglong add 20150413
        if (StringUtil.isNullString(this.getMrNo())) {
            return;
        }
        Pat pat = Pat.onQueryByMrNo(this.getMrNo());
        TParm print = new TParm();
        print.setData("Barcode", "TEXT", pat.getMrNo());
        print.setData("PatName", "TEXT", pat.getName());
        print.setData("Sex", "TEXT", pat.getSexString());
        print.setData("BirthDay", "TEXT", StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
        this.openPrintDialog("%ROOT%\\config\\prt\\ERD\\ERDWrist", print);
    }

    /**
     * ���
     */
    public void onClear() {
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // �ý����00��00��ʼ����ʼʱ��
        from_Date.setValue(date);
        from_Time.setValue("00:00");
        to_Date.setValue(date);
        to_Time.setValue("23:59");
        this.setValue("EXESHEET_TYPE", "O");// ҽ��ִ�е�����
        this.selDOSE(false);
        checkAll.setSelected(false);
        this.clearValue("exeALL");
        reviseDate.setSelected(false);
        mainTable.removeRowAll();
    }
    
    /**
     * ����������ռ�ĳ���
     * 
     * @param str
     * @return
     */
    public static int countStrLength(String str) {
        try {
            str = new String(str.getBytes("GBK"), "ISO8859_1");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.length();
    }
    
    /**
     * �������ַ�����ȡ�Ӵ���������2�����ȡ���������ȡ�������ĺ����������ȡ�Ļ����������?������ֱ�Ӳ���ʾ
     * @param str �ַ���
     * @param subSLength �Ӵ�����
     * @return
     */
    public static String subStr(String str, int subSLength) {
        if (str == null) return "";
        else {
            int tempSubLength = subSLength;// ��ȡ�ֽ���
            String subStr = str.substring(0, str.length() < subSLength ? str.length() : subSLength);// ��ȡ���Ӵ�
            int subStrByetsL;
            try {
                subStrByetsL = subStr.getBytes("GBK").length;// ��ȡ�Ӵ����ֽڳ���
                while (subStrByetsL > tempSubLength) {
                    int subSLengthTemp = --subSLength;
                    subStr =
                            str.substring(0, subSLengthTemp > str.length() ? str.length()
                                    : subSLengthTemp);
                    subStrByetsL = subStr.getBytes("GBK").length;
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return subStr;
        }
    }
    
    private String numDot(double medQty) {
        if (medQty == 0)
            return "";
        if ((int) medQty == medQty)
            return "" + (int) medQty;
        else
            return "" + medQty;
    }
    
}
