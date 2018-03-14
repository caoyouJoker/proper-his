package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;

public class PrintDialogControl extends TControl
{
  private TComboBox printlist;
  private TLabel zfPic;
  private TCheckBox zf;
  private TRadioButton ymAll;
  private TRadioButton ymNow;
  private TRadioButton ymCurr;
  private TTextField ymText;
  private TNumberTextField fs;

  public void onInit()
  {
    this.printlist = ((TComboBox)getComponent("PRINT_LIST"));
    this.zfPic = ((TLabel)getComponent("ZF_PIC"));
    this.zf = ((TCheckBox)getComponent("ZF"));
    this.ymAll = ((TRadioButton)getComponent("YM_ALL"));
    this.ymNow = ((TRadioButton)getComponent("YM_NOW"));
    this.ymCurr = ((TRadioButton)getComponent("YM_CURR"));
    this.ymText = ((TTextField)getComponent("YM_TEXT"));
    this.fs = ((TNumberTextField)getComponent("FS"));
    Object obj = getParameter();
    if ((obj != null) && ((obj instanceof TParm)))
    {
      TParm p = (TParm)obj;
      this.fs.setValue(Integer.valueOf(p.getInt("FS")));
    }
    initPrintList();
  }

  public void initPrintList()
  {
    List list = new ArrayList();
    PrintService[] services = PrinterJob.lookupPrintServices();
    for (int i = 0; i < services.length; i++) {
      list.add(services[i].getName().trim());
    }
    String[] s = (String[])(String[])list.toArray(new String[0]);
    this.printlist.setData(s);
    PrintService ps = PrinterJob.getPrinterJob().getPrintService();
    String defaultPrint = ps.getName();
    this.printlist.setSelectedID(defaultPrint);
  }

  public void onZF()
  {
    ((PrintDialogDrawControl)this.zfPic.getDrawControl()).setType(this.zf.isSelected());
    this.zfPic.repaint();
  }

  public PrintService getSelectPrint()
  {
    String name = this.printlist.getSelectedID();
    PrintService[] services = PrinterJob.lookupPrintServices();
    for (int i = 0; i < services.length; i++) {
      if (services[i].getName().trim().equals(name))
        return services[i];
    }
    return null;
  }

  public int getPageNo()
  {
    if (this.ymAll.isSelected())
      return 0;
    if (this.ymNow.isSelected())
      return 1;
    if (this.ymCurr.isSelected())
      return 2;
    return 0;
  }

  public int getFS()
  {
    try
    {
      return Integer.parseInt(this.fs.getText());
    } catch (Exception e) {
    }
    return 1;
  }

  public void onOK()
  {
    PrintService print = getSelectPrint();
    if (print == null)
    {
      messageBox_("ÇëÑ¡Ôñ´òÓ¡»ú!");
      return;
    }
    TParm parm = new TParm();
    parm.setData("PRINT", print);
    parm.setData("PAGE_NO", Integer.valueOf(getPageNo()));
    parm.setData("PAGE_TEXT", this.ymText.getText());
    parm.setData("COUNT", Integer.valueOf(getFS()));
    parm.setData("ZF", Boolean.valueOf(this.zf.isSelected()));
    setReturnValue(parm);
    closeWindow();
  }

  public void onCancel()
  {
    closeWindow();
  }
}