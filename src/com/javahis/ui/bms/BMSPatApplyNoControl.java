package com.javahis.ui.bms;

import com.dongyang.ui.TRadioButton;
import jdo.bms.BMSApplyMTool;
import com.dongyang.ui.TTable;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.control.TControl;

/**
 * <p>
 * Title: ��Ѫ���뵥��ѯ
 * </p>
 *
 * <p>
 * Description: ��Ѫ���뵥��ѯ(��������¼����)
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSPatApplyNoControl
    extends TControl {
    public BMSPatApplyNoControl() {
    }

    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        initPage();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        // �ż�ס��
        String adm_type = "O";
        if (this.getRadioButton("ADM_TYPE_E").isSelected()) {
            adm_type = "E";
        }
        else if (this.getRadioButton("ADM_TYPE_I").isSelected()) {
            adm_type = "I";
        }
        parm.setData("ADM_TYPE", adm_type);
        // ������
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        // סԺ��
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", getValueString("IPD_NO"));
        }
        // ��Ѫ����
        if (!"".equals(this.getValueString("APPLY_NO"))) {
            parm.setData("APPLY_NO", getValueString("APPLY_NO"));
        }
        // ��Ѫ����
        parm.setData("START_DATE", getValue("START_DATE"));
        parm.setData("END_DATE", getValue("END_DATE"));
        TParm result = BMSApplyMTool.getInstance().onApplyNoQuery(parm);
        if (result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "MR_NO;IPD_NO;PAT_NAME;APPLY_NO";
        this.clearValue(clearStr);
        table.removeRowAll();
        this.getRadioButton("ADM_TYPE_O").setSelected(true);
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * ����˫���¼�
     */
    public void onTableDoubleClicked() {
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        this.setReturnValue(parm);
        this.closeWindow();
    }


    /**
     * ��ʼ��������
     */
    private void initPage() {
        table = getTable("TABLE");
        Timestamp date = StringTool.getTimestamp(new Date());
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
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
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}