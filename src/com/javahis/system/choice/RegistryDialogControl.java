package com.javahis.system.choice;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import java.io.PrintStream;

public class RegistryDialogControl extends TControl
{

    public RegistryDialogControl()
    {
    }

    public void onInit()
    {
        TParm parm = new TParm();
        TParm result = TIOM_AppServer.executeAction("action.sys.LoginAction", "getInfo", parm);
        setText("getInfo", result.getValue("CONAME"));
        setText("tLabel_2", result.getValue("COMPUTER"));
    }

    public void onCreate()
    {
        String coname = getText("NAME");
        if(coname == null || coname.length() == 0)
        {
            messageBox("\u8BF7\u8F93\u5165\u516C\u53F8\u540D\u79F0");
            return;
        } else
        {
            TParm parm = new TParm();
            parm.setData("CONAME", coname);
            TParm result = TIOM_AppServer.executeAction("action.sys.LoginAction", "getOutNo", parm);
            setValue("tTextField_0", result.getValue("KEY"));
            return;
        }
    }

    public void onOK()
    {
        String coname = getText("NAME");
        if(coname == null || coname.length() == 0)
        {
            messageBox("\u8BF7\u8F93\u5165\u516C\u53F8\u540D\u79F0");
            return;
        }
        String key = getText("tTextField_1");
        if(key == null || key.length() == 0)
        {
            messageBox("\u8BF7\u8F93\u5165\u6CE8\u518C\u53F7\u7801");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CONAME", coname);
        parm.setData("KEY", key);
        TParm result = TIOM_AppServer.executeAction("action.sys.LoginAction", "getRegistry", parm);
        String value = result.getValue("KEY");
        if(!"OK".equals(value))
        {
            messageBox(value);
            return;
        } else
        {
            messageBox("\u6CE8\u518C\u6210\u529F!");
            closeWindow();
            return;
        }
    }

    public void onCancel()
    {
        closeWindow();
    }
}