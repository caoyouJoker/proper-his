package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;

/**
 *
 * <p>Title:�ܰ�ת�հ������ </p>
 *
 * <p>Description:�ܰ�ת�հ������ </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.18
 * @version 1.0
 */
public class REGSchWeekForDayControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        //��ʼ��REGION��½Ĭ�ϵ�¼����
        //===pangben modify 20110410
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        this.setValue("ADM_DATE_START", SystemTool.getInstance().getDate());
        this.setValue("ADM_DATE_END", SystemTool.getInstance().getDate());

    }

    /**
     * ����
     */
    public void onInsert() {
        TParm parm = getParmForTag("REGION_CODE;ADM_TYPE;ADM_DATE_START:Timestamp;ADM_DATE_END:Timestamp;DEPT_CODE;DR_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
            "schWeekForDay", parm);

        if (result.getErrCode() != 0) {
            messageBox(result.getErrText());
            return;
        }
        //��ʾ��Ϣ��ִ�гɹ���
        this.messageBox("P0005");

    }

    /**
     * ����
     */
    public void onSave() {
        onInsert();
    }

    /**
     *���
     */
    public void onClear() {
        clearValue(
            "ADM_DATE_START;ADM_DATE_END;REGION_CODE;ADM_TYPE;DEPT_CODE;DR_CODE");

    }
}
