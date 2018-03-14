package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;

/**
 *
 * <p>Title:周班转日班控制类 </p>
 *
 * <p>Description:周班转日班控制类 </p>
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
        //初始化REGION登陆默认登录区域
        //===pangben modify 20110410
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        this.setValue("ADM_DATE_START", SystemTool.getInstance().getDate());
        this.setValue("ADM_DATE_END", SystemTool.getInstance().getDate());

    }

    /**
     * 新增
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
        //提示信息“执行成功”
        this.messageBox("P0005");

    }

    /**
     * 保存
     */
    public void onSave() {
        onInsert();
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue(
            "ADM_DATE_START;ADM_DATE_END;REGION_CODE;ADM_TYPE;DEPT_CODE;DR_CODE");

    }
}
