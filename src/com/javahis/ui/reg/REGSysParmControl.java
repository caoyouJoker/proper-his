package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.reg.REGSysParmTool;
import jdo.reg.REGSysParmTool;
import jdo.sys.Operator;

/**
 *
 * <p>Title:挂号方式控制类 </p>
 *
 * <p>Description:挂号方式控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.22
 * @version 1.0
 */
public class REGSysParmControl
    extends TControl {
    public void onInit() {
        super.onInit();
        onQuery();
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = REGSysParmTool.getInstance().selectdata();
        if (parm.getErrCode() < 0) {
            messageBox(parm.getErrText());
            return;
        }
        this.setValueForParm(
            "CHECKIN_FLG;QUEREUSE_FLG;OTHHOSP_REG_FLG;TRIAGE_FLG;APPTCONTI_FLG;" +
            "DEFAULT_PAY_WAY;DEFAULT_VISIT_CODE;EFFECT_DAYS;Q_E_EFFECT_DAYS;" + 
            "Q_O_EFFECT_DAYS;MT_CLINIC_FEE_CODE",
            parm, 0);

        //this.callFunction("UI|Table|setParmValue", data);
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "CHECKIN_FLG;QUEREUSE_FLG;OTHHOSP_REG_FLG;TRIAGE_FLG;APPTCONTI_FLG;" +
            "DEFAULT_PAY_WAY;DEFAULT_VISIT_CODE;EFFECT_DAYS;Q_E_EFFECT_DAYS;"
            + "Q_O_EFFECT_DAYS;MT_CLINIC_FEE_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = REGSysParmTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        this.messageBox("P0001");
    }

    /**
     *新增
     */
    public void onInsert() {
        TParm parm = getParmForTag(
            "CHECKIN_FLG;QUEREUSE_FLG;OTHHOSP_REG_FLG;TRIAGE_FLG;APPTCONTI_FLG;" +
            "DEFAULT_PAY_WAY;DEFAULT_VISIT_CODE;EFFECT_DAYS;Q_E_EFFECT_DAYS;"
            + "Q_O_EFFECT_DAYS;MT_CLINIC_FEE_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = REGSysParmTool.getInstance().insert(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        this.messageBox("P0001");
    }

    /**
     * 保存
     */
    public void onSave() {
        TParm parm = REGSysParmTool.getInstance().selectdata();
        if (parm.getCount("CHECKIN_FLG") <= 0) {
            onInsert();
            return;
        }
        onUpdate();
    }

}
