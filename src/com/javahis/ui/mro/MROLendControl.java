package com.javahis.ui.mro;

import jdo.mro.MROLendTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 * <p>Title: ���������ֵ�</p>
 *
 * <p>Description: ���������ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-6
 * @version 1.0
 */
public class MROLendControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
    }
    /**
     * ���Ӷ�Table�ļ���
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // ѡ����
        if (row < 0)
            return;
        setValueForParm(
            "LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION",
            data, row);
        selectRow = row;
        // ���ɱ༭
        ((TTextField) getComponent("LEND_CODE")).setEnabled(false);
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }
    /**
     * ����
     */
    public void onInsert() {
        if(this.getText("LEND_CODE").trim().length()<=0||this.getText("LEND_DESC").trim().length()<=0){
            this.messageBox("��������ԭ������˵������Ϊ�գ�");
            return;
        }
        TParm parm = this.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROLendTool.getInstance().insertdata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // ��ʾ��������
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "LEND_CODE;LEND_DESC;PY1;PY2;DESCRIPTION;LEND_DAY;VALID_DAY;OPT_USER;OPT_DATE;OPT_TERM");

        data.setRowData(row, parm);
        this.messageBox("��ӳɹ���");
    }
    /**
     * ����
     */
    public void onUpdate() {
        if(this.getText("LEND_CODE").trim().length()<=0||this.getText("LEND_DESC").trim().length()<=0){
            this.messageBox("��������ԭ������˵������Ϊ�գ�");
            return;
        }
        TParm parm = this.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = MROLendTool.getInstance().updatedata(parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // ѡ����
        int row = ( (TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return;
        // ˢ�£�����ĩ��ĳ�е�ֵ
        data.setRowData(row, parm);
        ( (TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("�޸ĳɹ���");
    }
    /**
     * ����
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }
    /**
     * ��ѯ
     */
    public void onQuery() {
        data = MROLendTool.getInstance().selectdata(getText("LEND_CODE").trim(),getText("LEND_DESC").trim());
        // �жϴ���ֵ
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
        ((TTextField)this.getComponent("LEND_CODE")).setEnabled(true);
        this.clearValue("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
    }
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // ����ɾ����ť״̬
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTextField) getComponent("LEND_CODE")).setEnabled(true);
        onQuery();
    }
    /**
     * ɾ��
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            if (selectRow == -1)
                return;
            String code = getValue("LEND_CODE").toString();
            TParm result = MROLendTool.getInstance().deletedata(code);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
//            TTable table = ( (TTable) getComponent("Table"));
//            int row = table.getSelectedRow();
//            if (row < 0)
//                return;
            this.messageBox("ɾ���ɹ���");
            onClear();
        }
        else {
            return;
        }
    }
    /**
     * ���ݺ������ƴ������ĸ
     *
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("LEND_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("LEND_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // �������
        ( (TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
