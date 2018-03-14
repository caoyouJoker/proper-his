package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.ui.TPanel;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SWFPanlControl extends TControl {
    private final String PANEL = "Panel";
    public void onInit(){
        super.onInit();
        this.initPage();
    }
    public void initPage(){
//        this.getTPanel().addItem();
    }
    /**
   * µÃµ½PANEL
   * @return TPanel
   */
  public TPanel getTPanel(){
      return (TPanel)this.getComponent(PANEL);
  }

}
