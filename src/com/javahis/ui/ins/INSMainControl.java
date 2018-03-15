package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
/**
 *
 * <p>Title: 医保主程序控制类</p>
 *
 * <p>Description: 医保主程序控制类</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2011.09.20
 * @version 1.0
 */
public class INSMainControl
    extends TControl {
    public void onInit() {
        super.onInit();
        TParm parm = new TParm();
        parm.setData("CASE_NO","201109200001");
        TParm result = TIOM_AppServer.executeAction("action.ins.INSAction",
            "onSaveREG", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return;
        }
    }

}
