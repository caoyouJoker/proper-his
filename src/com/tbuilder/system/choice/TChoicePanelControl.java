package com.tbuilder.system.choice;

import com.dongyang.config.TRegistry;
import com.dongyang.control.TControl;
import com.dongyang.ui.TComponent;
import com.dongyang.util.TSystem;
import java.io.PrintStream;
import jdo.sys.MessageTool;
import jdo.sys.OperatorTool;
import jdo.sys.SYSRegionTool;

public class TChoicePanelControl
  extends TControl
{
  public void onInit()
  {
    super.onInit();
    TSystem.setObject("MessageObject", new MessageTool());
    String defaultRegion = SYSRegionTool.getInstance().getRegionByIP(OperatorTool.getInstance().getUserIP());
    System.out.println("defaultRegion=" + defaultRegion);
    callFunction("UI|REGION|setValue", new Object[] { defaultRegion });
    TComponent com = (TComponent)callFunction("UI|getParentComponent", new Object[0]);
    com.callFunction("addEventListener", new Object[] { "start", this, "start" });
    String userID = TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\UserID");
    if (userID != null)
    {
      setValue("USER_ID", userID);
      callFunction("UI|DEPT|onQuery", new Object[0]);
      onDefDept();
      callFunction("UI|STATION|onQuery", new Object[0]);
      onDefStation();
    }
  }
  
  public void start()
  {
    if (getText("USER_ID").length() > 0) {
      grabFocus("PASSWORD");
    } else {
      grabFocus("USER_ID");
    }
  }
  
  public void onDefDept()
  {
    String defDept = OperatorTool.getInstance().getMainDept(getText("USER_ID"));
    callFunction("UI|DEPT|setValue", new Object[] { defDept });
  }
  
  public void onDefStation()
  {
    String defDept = OperatorTool.getInstance().getMainStation(getText("USER_ID"));
    callFunction("UI|STATION|setValue", new Object[] { defDept });
  }
  
  public void onLogin()
  {
    String userID = getText("USER_ID");
    if ((userID == null) || (userID.length() == 0))
    {
      messageBox_("请输入用户名称");
      callFunction("UI|USER_ID|grabFocus", new Object[0]);
      return;
    }
    String resgion = getText("REGION");
    if ((resgion == null) || (resgion.length() == 0))
    {
      messageBox_("请输入登录区域");
      callFunction("UI|REGION|grabFocus", new Object[0]);
      return;
    }
//    String result = OperatorTool.getInstance().login(getText("USER_ID"), getText("PASSWORD"), (String)getValue("REGION"), (String)getValue("DEPT"), (String)getValue("STATION"));
//    if (!"OK".equals(result))
//    {
//      messageBox(result);
//      callFunction("UI|PASSWORD|grabFocus", new Object[0]);
//      return;
//    }
    TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\Login\\UserID", getText("USER_ID"));
    callFunction("UI|P1|visible", new Object[] { Boolean.valueOf(false) });
    openWindow(getConfigString("TBuilderConfig"));
  }
}
