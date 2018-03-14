package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: 重新生成请领单</p>
 *
 * <p>Description: 重新生成请领单</p>
 *
 * <p>Copyright: (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author liyh
 * @version 1.0
 */
public class INDCreateRequestAutoControl
    extends TControl {
    public INDCreateRequestAutoControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始化查询区间
        Timestamp date = SystemTool.getInstance().getDate();
        date = StringTool.rollDate(date, -1);
        this.setValue("CLOSE_DATE", date.toString().substring(0, 7).replace('-', '/'));
    }

    /**
     * 重新生成请领单
     */
    public void onSave() {
      /*  String org_code = this.getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("请选择过账药房");
            return;
        }*/  
        String date = this.getValueString("DATE");
        //查询最近日结过账 日期
        Timestamp datetime = StringTool.getTimestamp(date, "yyyyMMdd");
        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        String regionCode = Operator.getRegion();
        TParm parm = new TParm();  
        parm.setData("OPT_USER",optUser);
        parm.setData("OPT_TERM",optTerm);
        parm.setData("REGION_CODE",regionCode);
        parm.setData("CLOSE_DATE", (Timestamp) this.getValue("CLOSE_DATE"));//wanglong add 20150202
        TParm result = TIOM_AppServer.executeAction(
				"action.ind.INDCreateRequestAutoAction", "createIndRequsestAuto", parm);
        if (result.getErrCode() < 0) {
        	this.messageBox("执行失败！");
        }
        else {
        	this.messageBox("执行成功！");
        }
   /*     TParm result = INDTool.getInstance().createIndRequsestAuto("", optUser, optTerm, regionCode);
        if (result.getErrCode() < 0) {
        	this.messageBox("执行失败！");
        }
        else {
        	this.messageBox("执行成功！");
        }*/


    }
    
    /**
     * 测试方式
     * @param args String[]
     */
    public static void main(String[] args) {
        INDCreateRequestAutoControl ind = new INDCreateRequestAutoControl();
    }
}
