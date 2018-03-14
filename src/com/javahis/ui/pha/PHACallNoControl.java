package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

import jdo.device.CallNo;
import jdo.sys.Operator;

/**
 * <p>Title: 叫号对话框</p>
 *
 * <p>Description: 叫号对话框</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class PHACallNoControl extends TControl {

    //调用界面传参
    TParm parm;

    /**
     * 构造器
     */
    public PHACallNoControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        parm = (TParm)getParameter();

    }

    /**
     * 确定
     */
    public void onConfirm(){
        if(getValueString("SERIAL_NO").length() == 0){
        	this.messageBox("请输入领药号");
        	return;
        }
            
        if(parm.getValue("MR_NO").length() == 0)
            return;
        
        //查询对应参数;
        String sql="SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
    	sql+=" WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
    	sql+=" AND a.SEX_CODE=b.ID";
    	TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String patName=patParm.getValue("PAT_NAME", 0);
    	if(patName.equals("")){
    		this.messageBox("无此病患");
    		return;
    	}
    	String strSend=parm.getValue("MR_NO")+"|";
    	strSend+=patParm.getValue("PAT_NAME", 0)+"|";
    	strSend+=patParm.getValue("SEX", 0)+"|";
    	strSend+=patParm.getValue("BIRTH_DATE", 0)+"|";
    	strSend+=Operator.getIP();
    	System.out.println("========onConfirm sendString=======" + strSend);
    	TParm inParm = new TParm();
    	inParm.setData("msg", strSend);
    	TIOM_AppServer.executeAction(
				"action.device.CallNoAction", "doPHACallNo", inParm);
    	
        /**CallNo call = new CallNo();
        if(!call.init()) {
            messageBox("叫号服务器没准备好");
            return;
        }
        call.CallDrug(parm.getValue("COUNTER_NO"),
                      getValueString("SERIAL_NO"),
                      "",
                      "",
                      "",
                      Operator.getIP(),
                      "");**/
        onCancel();
    }

    /**
     * 取消
     */
    public void onCancel(){
        closeWindow();
    }
}
