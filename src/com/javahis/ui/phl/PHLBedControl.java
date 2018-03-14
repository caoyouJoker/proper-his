package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.phl.PhlBedTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: �����Ҵ�λ
 * </p>
 *
 * <p>
 * Description: �����Ҵ�λ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLBedControl
    extends TControl {

    private String action = "insert";

    private TTable table;


    public PHLBedControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        //====pangben modify 20110623 start
        if (null != getValueString("REGION_CODE_ALL") &&
            getValueString("REGION_CODE_ALL").length() > 0)
            parm.setData("REGION_CODE_ALL", getValueString("REGION_CODE_ALL"));
        //====pangben modify 20110623 stop
        parm.setData("BED_NO", getValueString("BED_NO"));
        parm.setData("BED_DESC", getValueString("BED_DESC"));
        parm.setData("BED_STATUS",
                     "Y".equals(getValueString("BED_STATUS")) ? "1" : "0");
        parm.setData("TYPE", getValueString("TYPE"));
        parm.setData("OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        if ("insert".equals(action)) {
            //=======pangben modify 20110622 start
            TParm parmQuery=new TParm();
            parmQuery.setData("BED_NO", getValueString("BED_NO"));
            parmQuery.setData("REGION_CODE", getValueString("REGION_CODE"));
            //=======pangben modify 20110622 stop
            result = PhlBedTool.getInstance().onQuery(parmQuery);
            if (result.getCount() > 0) {
                this.messageBox("�����Ѵ���");
                return;
            }
            result = PhlBedTool.getInstance().onInsert(parm);
        }
        else {
            result = PhlBedTool.getInstance().onUpdate(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����ʧ��");
            return;
        }
        this.messageBox("����ɹ�");
        getTable("TABLE").setSelectionMode(0);
        this.onQuery();
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        String region_code = getValueString("REGION_CODE");
        if (!"".equals(region_code)) {
            parm.setData("REGION_CODE", region_code);
        }
        //=====pangben modify 20110622 start
        String region_code_all = getValueString("REGION_CODE_ALL");
        if (!"".equals(region_code_all)) {
            parm.setData("REGION_CODE_ALL", region_code_all);
        }
        //=====pangben modify 20110622 stop
        String bed_no = getValueString("BED_NO");
        if (!"".equals(bed_no)) {
            parm.setData("BED_NO", bed_no);
        }
        TParm result = PhlBedTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) == 0) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
            parm.setData("REGION_CODE_ALL", getValueString("REGION_CODE_ALL"));//=======pangben modify 20110622
            parm.setData("BED_NO", getValueString("BED_NO"));
            TParm result = PhlBedTool.getInstance().onDelete(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("ɾ��ʧ��");
                return;
            }
            table.removeRow(row);
            table.setSelectionMode(0);
            this.messageBox("ɾ���ɹ�");
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
        action = "insert";
    }

    /**
     * ��շ���
     */
    public void onClear() {
        // ���VALUE
        String clear =
            "REGION_CODE;BED_NO;BED_DESC;BED_STATUS;TYPE";
        this.clearValue(clear);
        getTable("TABLE").setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "insert";
        this.setValue("REGION_CODE_ALL",Operator.getRegion());//========pangben modfiy 20110622
    }

    /**
     * ���(CLNDIAG_TABLE)�����¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            this.setValue("REGION_CODE", table.getItemString(row, "REGION_CODE"));
            this.setValue("REGION_CODE_ALL", table.getItemString(row, "REGION_CODE_ALL"));//pangben modify 20110622
            this.setValue("BED_NO", table.getItemString(row, "BED_NO"));
            this.setValue("BED_DESC", table.getItemString(row, "BED_DESC"));
            this.setValue("BED_STATUS", table.getItemString(row, "BED_STATUS"));
            this.setValue("TYPE", table.getItemString(row, "TYPE"));
            action = "update";
        }
    }


    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ��ʼ��
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        table = this.getTable("TABLE");
        //========pangben modify 20110622 start Ȩ�����
        this.setValue("REGION_CODE_ALL",Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE_ALL");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE_ALL")));
        //===========pangben modify 20110622 stop

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
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("REGION_CODE_ALL"))) {
            this.messageBox("������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("����������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("BED_NO"))) {
            this.messageBox("�������벻��Ϊ��");
            return false;
        }
        if ("".equals(getValueString("BED_DESC"))) {
            this.messageBox("�������Ʋ���Ϊ��");
            return false;
        }
        if ("".equals(getValueString("TYPE"))) {
            this.messageBox("�������Ͳ���Ϊ��");
            return false;
        }
        return true;
    }
}
