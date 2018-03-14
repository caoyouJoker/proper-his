package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import jdo.sys.SystemTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:取消预约 </p>
 *
 * <p>Description:取消预约 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY 2009.04.13
 * @version 1.0
 */
public class ADMCanResvControl
    extends TControl {
    TParm acceptData = new TParm(); //接参
    public void onInit() {
        super.onInit();
        acceptData = (TParm)this.getParameter();
        this.setValueForParm("MR_NO;PAT_NAME", acceptData);
    }

    /**
     * 确定
     */
    public void onOK() {
        if(this.getValue("CAN_REASON_CODE").toString().length()<=0){
            this.messageBox_("请选择取消原因!");
            this.grabFocus("CAN_REASON_CODE");
            return;
        }
        acceptData.setData("CAN_REASON_CODE",this.getValue("CAN_REASON_CODE"));
        TParm result = TIOM_AppServer.executeAction("action.adm.ADMResvAction",
                "cancelResv", acceptData);
        if (result.getErrCode() < 0)
            this.messageBox("E0005");
        else {
            this.messageBox("P0005");
            TParm parm = new TParm();
            parm.setData("CAN", "true");
            this.setReturnValue(parm); //返回true 标识已经执行取消
            this.closeWindow();
        }
    }
}
