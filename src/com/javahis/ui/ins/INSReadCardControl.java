package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 医保读卡控制类</p>
 *
 * <p>Description: 医保读卡控制类</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.09.20
 * @version 1.0
 */
public class INSReadCardControl
    extends TControl {
    public void onInit() {
        super.onInit();
        TParm parm = new TParm();
        parm.setData("CASE_NO", "201109200001");
        TParm result = TIOM_AppServer.executeAction("action.ins.INSAction",
            "onSaveREG", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return;
        }
    }

}
