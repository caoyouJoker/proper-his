package com.javahis.system;

import com.dongyang.control.TControl;
import com.dongyang.manager.TIOM_Database;

public class TMainControl extends TControl{
  /**
   * ≥ı ºªØ
   */
  public void onInit() {
    super.onInit();
    callMessage("UI|addEventListener|destroy|destroy", this);
    callMessage("UI|addEventListener|start|start", this);
    callMessage("UI|addEventListener|stop|stop", this);
    new Thread(){
        public void run()
        {
            TIOM_Database.getLocalTable("SYS_FEE");
            TIOM_Database.getLocalTable("SYS_DIAGNOSIS", false,
                                        " ORDER BY SEQ, ICD_CODE");
        }
    }.start();
  }
  public void start() {
  }

  public void stop() {
  }

  public void destroy() {
  }
}
