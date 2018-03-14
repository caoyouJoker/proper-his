package com.javahis.ui.bil;

import com.dongyang.control.*;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:����Ʊ�� </p>
 *
 * <p>Description:����Ʊ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class BILAdjustmentRecipientsControl
    extends TControl {
    public void onInit() {
        super.onInit();
        TParm recptype = (TParm)this.getParameter();
        this.callFunction("UI|START_INVNO|setValue",
                          recptype.getValue("START_INVNO"));
        this.callFunction("UI|END_INVNO|setValue",
                          recptype.getValue("END_INVNO"));
        this.callFunction("UI|UPDATE_NO|setValue",
                          recptype.getValue("UPDATE_NO"));
        this.callFunction("UI|NOWNUMBER|setValue",
                          recptype.getValue("UPDATE_NO"));
        this.callFunction("UI|START_INVNO|setEnabled", false);
        this.callFunction("UI|END_INVNO|setEnabled", false);
        this.callFunction("UI|UPDATE_NO|setEnabled", false);
    }

    /**
     * ����
     */
    public void onSave() {
        if (!this.emptyTextCheck("NOWNUMBER"))
            return;
        if (!this.checkouts()) {
            return;
        }
        TParm recptype = (TParm)this.getParameter();
        //����Ʊ�ݵ�Ʊ��
        recptype.setData("NUMBER",
                         StringTool.bitDifferOfString(recptype.
            getValue("UPDATE_NO"), getValueString("NOWNUMBER"))+1);
        recptype.setData("NOWNUMBER", StringTool.addString(this.getValueString("NOWNUMBER")));
        //����ACTION��
        TParm result = TIOM_AppServer.executeAction(
            "action.bil.InvoicePersionAction", "Adjustment", recptype);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            this.messageBox("E0005"); //ִ��ʧ��
            return;
        }
        else {
            this.messageBox("P0005"); //ִ�гɹ�
        }
        this.callFunction("UI|onClose");
    }

    /**
     * ��˽����ϵ�������ȷ��
     * @return boolean
     */
    public boolean checkouts() {
        if (getValueString("NOWNUMBER").length() !=
            getValueString("UPDATE_NO").length()) {
            this.messageBox("E0016");
            return false;
        }
        if (getValueString("NOWNUMBER").compareTo(getValueString("END_INVNO")) >
            0) {
            this.messageBox("E0015");
            return false;
        }
        return true;
    }

    public void onAmt() {
        TParm recptype = (TParm)this.getParameter();
        this.callFunction("UI|number|setValue",
                          "NUMBER",
                          StringTool.bitDifferOfString(recptype.
            getValue("UPDATE_NO"), getValueString("NOWNUMBER"))+1);
    }
}
