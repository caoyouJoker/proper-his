package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.javahis.util.ExportExcelUtil;
import java.sql.Timestamp;
import java.util.Date;
import jdo.bms.BMSBldCodeTool;
import jdo.bms.BMSSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.util.Organization;

public class BMSBloodInControl extends TControl
{
  private String action = "insert";
  private TTable table;

  public void onInit()
  {
    initPage();
  }

  public void onSave()
  {
    if (!CheckData()) {
      return;
    }
    Timestamp date = StringTool.getTimestamp(new Date());
    TParm parm = new TParm();
    parm.setData("RH_FLG", 
      getRadioButton("RH_FLG_A").isSelected() ? "+" : "-");
    parm.setData("BLD_CODE", getValueString("BLD_CODE"));
    parm.setData("BLDRESU_CODE", getValueString("BLDRESU_CODE"));
    parm.setData("SUBCAT_CODE", getValueString("SUBCAT_CODE"));
    parm.setData("BLD_SUBCAT", getValueString("SUBCAT_CODE"));
    parm.setData("IN_DATE", date);
    parm.setData("BLD_TYPE", getValueString("BLD_TYPE"));
    parm.setData("SHIT_FLG", getValueString("SHIT_FLG"));
    parm.setData("END_DATE", getValue("VALID_DATE"));
    parm.setData("IN_PRICE", Double.valueOf(getValueDouble("IN_PRICE")));
    TParm inparm = new TParm(TJDODBTool.getInstance().select(
      BMSSQL.getBMSBldVol(getValueString("BLD_CODE"), 
      getValueString("SUBCAT_CODE"))));
    parm.setData("BLOOD_VOL", Double.valueOf(inparm.getDouble("BLD_VOL", 0)));
    parm.setData("ORG_BARCODE", getValueString("ORG_BARCODE"));
    parm.setData("STATE_CODE", "0");
    parm.setData("OPT_USER", Operator.getID());
    parm.setData("OPT_DATE", date);
    parm.setData("OPT_TERM", Operator.getIP());
    TParm result = new TParm();

    String myBloodNo = "";
    if ("insert".equals(this.action)) {
      String blood_no = SystemTool.getInstance().getNo("ALL", 
        "BMS", "BMS_BLOOD", "No");
      parm.setData("BLOOD_NO", blood_no);
      myBloodNo = blood_no;

      result = TIOM_AppServer.executeAction(
        "action.bms.BMSBloodAction", "onInsert", parm);
    }
    else if ("update".equals(this.action)) {
      parm.setData("BLOOD_NO", getValue("BLOOD_NO"));

      myBloodNo = getValueString("BLOOD_NO");

      result = TIOM_AppServer.executeAction(
        "action.bms.BMSBloodAction", "onUpdate", parm);
    }

    if ((result == null) || (result.getErrCode() < 0)) {
      messageBox("E0001");
      return;
    }
    messageBox("P0001");

    Timestamp date1 = StringTool.getTimestamp(new Date());

    setValue("END_DATE", date1.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
    setValue("START_DATE", date1.toString().substring(0, 10).replace('-', '/') + " 00:00:00");

    clearValue("ORG_BARCODE;BLOOD_NO");
    onQuery();

    TParm data = new TParm();
    TParm parmData = new TParm();

    parmData.addData("BLOOD_NO", myBloodNo);

    parmData.addData("BLD_CODE", 
      "血液:" + getComboBox("BLD_CODE").getSelectedName());

    parmData.addData("BLD_TYPE", 
      "血型:" + getValueString("BLD_TYPE") + 
      " 型");

    parmData.addData("BLDRESU_CODE", "来源:" + 
      getComboBox("BLDRESU_CODE").getSelectedName());

    parmData.addData("IN_DATE", "入库日期:" + StringTool.getString(date, "yyyy/MM/dd"));

    parmData.addData("END_DATE", 
      "失效日期:" + 
      getValueString("VALID_DATE")
      .substring(0, 10).replace('-', '/'));

    parmData.setCount(1);
    parmData.addData("SYSTEM", "COLUMNS", "BAR_INFO");
    data.setData("TABLE", parmData.getData());

    openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBarCode.jhw", data, true);
  }

  public void onClear()
  {
    String clearString = 
      "ORG_BARCODE;BLOOD_NO;SHIT_FLG;VALID_DAY;IN_PRICE";

    clearValue(clearString);
    Timestamp date = StringTool.getTimestamp(new Date());

    setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");

    setValue("START_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");

    getRadioButton("RH_FLG_A").setSelected(true);
    this.table.removeRowAll();
    this.table.setSelectionMode(0);
    this.action = "insert";
    getComboBox("BLD_CODE").setEnabled(true);
    getComboBox("SUBCAT_CODE").setEnabled(true);
    getComboBox("BLD_TYPE").setEnabled(true);
    grabFocus("ORG_BARCODE");
  }

  public void onPrint()
  {
    if (this.table.getRowCount() <= 0) {
      messageBox("没有打印数据");
      return;
    }
    TParm date = new TParm();
    date.setData("TITLE", "TEXT", Manager.getOrganization()
      .getHospitalCHNFullName(Operator.getRegion()) + 
      "血品入库明细单");

    TParm parm = new TParm();
    String blood_no = "";
    if (this.table.getSelectedRow() != -1) {
      blood_no = this.table.getItemString(this.table.getSelectedRow(), "BLOOD_NO");
      TParm bldInfoParm = new TParm(TJDODBTool.getInstance().select(
        BMSSQL.getBMSBldStockInfo(blood_no)));
      parm.addData("BLDCODE_DESC", bldInfoParm.getValue("BLDCODE_DESC", 0));
      parm.addData("SUBCAT_DESC", bldInfoParm.getValue("SUBCAT_DESC", 0));
      parm.addData("BLOOD_NO", bldInfoParm.getValue("BLOOD_NO", 0));
      parm.addData("ORG_BARCODE", bldInfoParm.getValue("ORG_BARCODE", 0));
      parm.addData("BLD_TYPE", bldInfoParm.getValue("BLD_TYPE", 0));
      parm.addData("RH_FLG", bldInfoParm.getValue("RH_FLG", 0));
      parm.addData("END_DATE", 
        bldInfoParm.getValue("END_DATE", 0).substring(0, 10)
        .replace('-', '/'));
      parm.addData("IN_DATE", 
        bldInfoParm.getValue("IN_DATE", 0).substring(0, 10)
        .replace('-', '/'));
      parm.addData("USER_NAME", bldInfoParm.getValue("USER_NAME", 0));
    }
    else {
      for (int i = 0; i < this.table.getRowCount(); i++) {
        blood_no = this.table.getItemString(i, "BLOOD_NO");
        TParm bldInfoParm = new TParm(TJDODBTool.getInstance().select(
          BMSSQL.getBMSBldStockInfo(blood_no)));
        parm.addData("BLDCODE_DESC", 
          bldInfoParm.getValue("BLDCODE_DESC", 0));
        parm.addData("SUBCAT_DESC", 
          bldInfoParm.getValue("SUBCAT_DESC", 0));
        parm.addData("BLOOD_NO", bldInfoParm.getValue("BLOOD_NO", 0));
        parm.addData("ORG_BARCODE", 
          bldInfoParm.getValue("ORG_BARCODE", 0));
        parm.addData("BLD_TYPE", bldInfoParm.getValue("BLD_TYPE", 0));
        parm.addData("RH_FLG", bldInfoParm.getValue("RH_FLG", 0));
        parm.addData("END_DATE", 
          bldInfoParm.getValue("END_DATE", 0).substring(0, 
          10).replace('-', '/'));
        parm.addData("IN_DATE", 
          bldInfoParm.getValue("IN_DATE", 0).substring(0, 10)
          .replace('-', '/'));
        parm.addData("USER_NAME", bldInfoParm.getValue("USER_NAME", 0));
      }
    }
    parm.setCount(parm.getCount("BLDCODE_DESC"));
    parm.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
    parm.addData("SYSTEM", "COLUMNS", "SUBCAT_DESC");
    parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
    parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
    parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
    parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
    parm.addData("SYSTEM", "COLUMNS", "END_DATE");
    parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
    parm.addData("SYSTEM", "COLUMNS", "USER_NAME");

    date.setData("TABLE", parm.getData());

    date.setData("OPT_USER", "TEXT", "制表人: " + Operator.getName());

    openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodIn.jhw", date);
  }

  public void onPrintNo()
  {
    if (this.table.getRowCount() <= 0) {
      messageBox("没有打印数据");
      return;
    }
    TParm data = new TParm();
    TParm parmData = new TParm();
    int row = this.table.getSelectedRow();

    parmData.addData("BLOOD_NO", getValueString("BLOOD_NO"));

    parmData.addData("BLD_CODE", 
      "血液:" + getComboBox("BLD_CODE").getSelectedName());

    parmData.addData("BLD_TYPE", 
      "血型:" + getComboBox("BLD_TYPE").getSelectedName() + 
      " 型");

    parmData.addData("BLDRESU_CODE", "来源:" + 
      getComboBox("BLDRESU_CODE").getSelectedName());

    parmData.addData("IN_DATE", "入库日期:" + 
      this.table.getItemString(row, "IN_DATE").substring(0, 10)
      .replace('-', '/'));

    parmData.addData("END_DATE", 
      "失效日期:" + 
      getValueString("VALID_DATE")
      .substring(0, 10).replace('-', '/'));

    parmData.setCount(1);
    parmData.addData("SYSTEM", "COLUMNS", "BAR_INFO");
    data.setData("TABLE", parmData.getData());

    openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBarCode.jhw", data);
  }

  public void onQuery()
  {
    TParm parm = new TParm();
    if (!"".equals(getValueString("START_DATE"))) {
      parm.setData("START_DATE", getValue("START_DATE"));
    }
    if (!"".equals(getValueString("END_DATE"))) {
      parm.setData("END_DATE", getValue("END_DATE"));
    }
    if (!"".equals(getValueString("BLDRESU_CODE"))) {
      parm.setData("BLDRESU_CODE", getValue("BLDRESU_CODE"));
    }
    if (!"".equals(getValueString("BLOOD_NO"))) {
      parm.setData("BLOOD_NO", getValue("BLOOD_NO"));
    }
    if (!"".equals(getValueString("BLD_CODE"))) {
      parm.setData("BLD_CODE", getValue("BLD_CODE"));
    }
    if (!"".equals(getValueString("SUBCAT_CODE"))) {
      parm.setData("SUBCAT_CODE", getValue("SUBCAT_CODE"));
    }
    if (!"".equals(getValueString("BLD_TYPE"))) {
      parm.setData("BLD_TYPE", getValue("BLD_TYPE"));
    }

    TParm result = TIOM_AppServer.executeAction(
      "action.bms.BMSBloodAction", "onQuery", parm);

    if (result.getCount("BLOOD_NO") == 0) {
      messageBox("没有查询数据");
      return;
    }
    this.table.setParmValue(result);
    grabFocus("ORG_BARCODE");
  }

  public void onDelete()
  {
    int row = this.table.getSelectedRow();
    if (row == -1) {
      messageBox("没有删除项");
      return;
    }
    
    TParm parm = new TParm();
    Timestamp date = StringTool.getTimestamp(new Date());
    parm.setData("BLOOD_NO", getValueString("BLOOD_NO"));
    parm.setData("BLD_CODE", getValueString("BLD_CODE"));
    parm.setData("BLD_TYPE", getValueString("BLD_TYPE"));
    parm.setData("BLD_SUBCAT", getValueString("SUBCAT_CODE"));
    TParm inparm = new TParm(TJDODBTool.getInstance().select(
      BMSSQL.getBMSBldVol(getValueString("BLD_CODE"), 
      getValueString("SUBCAT_CODE"))));
    parm.setData("BLOOD_VOL", Double.valueOf(inparm.getDouble("BLD_VOL", 0)));
    parm.setData("OPT_USER", Operator.getID());
    parm.setData("OPT_DATE", date);
    parm.setData("OPT_TERM", Operator.getIP());
    TParm result = new TParm();
    result = TIOM_AppServer.executeAction("action.bms.BMSBloodAction", 
      "onDelete", parm);
    if (result.getErrCode() < 0) {
      messageBox("删除失败");
      return;
    }
    this.table.removeRow(row);
    this.table.setSelectionMode(0);
    messageBox("删除成功");
    onClear();
    onQuery();
  }

  public void onValiidDay()
  {
    Timestamp date = StringTool.getTimestamp(new Date());
    if (((Timestamp)getValue("VALID_DATE")).compareTo(date) <= 0) {
      messageBox("效期不能早于当天");
      return;
    }
    int day = StringTool.getDateDiffer((Timestamp)getValue(
      "VALID_DATE"), date);
    setValue("VALID_DAY", Integer.valueOf(day + 1));
  }

  public void onChangeBld()
  {
    String bld_code = getComboBox("BLD_CODE").getSelectedID();
    ((TComboBMSBldsubcat)getComponent("SUBCAT_CODE")).setBldCode(
      bld_code);
    ((TComboBMSBldsubcat)getComponent("SUBCAT_CODE")).onQuery();
    TParm parm = new TParm();
    parm.setData("BLD_CODE", bld_code);
    TParm result = BMSBldCodeTool.getInstance().onQuery(parm);
    setValue("VALID_DAY", Double.valueOf(result.getDouble("VALUE_DAYS", 0)));
    Timestamp date = StringTool.getTimestamp(new Date());
    setValue("VALID_DATE", 
      StringTool.rollDate(date, result.getLong("VALUE_DAYS", 0)));
  }

  public void onTableClicked()
  {
    int row = this.table.getSelectedRow();
    if (row != -1)
    {
      String startDate = this.table.getItemData(row, "IN_DATE").toString().substring(0, 10).replace("-", "");
      setValue("START_DATE", StringTool.getTimestamp(startDate + "000000", "yyyyMMddHHmmss"));
      setValue("END_DATE", StringTool.getTimestamp(startDate + "235959", "yyyyMMddHHmmss"));
      setValue("BLDRESU_CODE", this.table.getItemData(row, "BLDRESU_CODE"));
      setValue("ORG_BARCODE", this.table.getItemData(row, "ORG_BARCODE"));
      setValue("BLOOD_NO", this.table.getItemData(row, "BLOOD_NO"));
      setValue("BLD_CODE", this.table.getItemData(row, "BLD_CODE"));
      setValue("SUBCAT_CODE", this.table.getItemData(row, "SUBCAT_CODE"));
      setValue("BLD_TYPE", this.table.getItemData(row, "BLD_TYPE"));
      if ("+".equals(this.table.getItemString(row, "RH_FLG"))) {
        getRadioButton("RH_FLG_A").setSelected(true);
      }
      else {
        getRadioButton("RH_FLG_B").setSelected(true);
      }
      setValue("IN_PRICE", this.table.getItemData(row, "IN_PRICE"));
      setValue("SHIT_FLG", this.table.getItemData(row, "SHIT_FLG"));
      setValue("VALID_DATE", this.table.getItemData(row, "END_DATE"));
      Timestamp date = StringTool.getTimestamp(new Date());
      int day = StringTool.getDateDiffer(this.table.getItemTimestamp(row, 
        "END_DATE"), date);
      setValue("VALID_DAY", Integer.valueOf(day + 1));
      this.action = "update";
    }
  }

  private void initPage()
  {
    Timestamp date = StringTool.getTimestamp(new Date());

    setValue("END_DATE", 
      date.toString().substring(0, 10).replace('-', '/') + 
      " 23:59:59");
    setValue("START_DATE", 
      StringTool.rollDate(date, -7L).toString().substring(0, 10)
      .replace('-', '/') + " 00:00:00");

    this.table = getTable("TABLE");
  }

  private boolean CheckData()
  {
    if (("update".equals(this.action)) && 
      ("".equals(getValueString("BLOOD_NO")))) {
      messageBox("院内条形码不能为空");
      return false;
    }

    if ("".equals(getValueString("BLDRESU_CODE"))) {
      messageBox("血品来源不能为空");
      return false;
    }
    if ("".equals(getValueString("BLD_CODE"))) {
      messageBox("血品代码不能为空");
      return false;
    }
    if ("".equals(getValueString("SUBCAT_CODE"))) {
      messageBox("血品规格不能为空");
      return false;
    }
    if ("".equals(getValueString("BLD_TYPE"))) {
      messageBox("血型不能为空");
      return false;
    }

    if (getValueDouble("IN_PRICE") < 0.0D) {
      messageBox("入库价格不能小于0");
      return false;
    }

    if ("".equals(getValueString("VALID_DATE"))) {
      messageBox("效期不能为空");
      return false;
    }
    return true;
  }

  public void onExport()
  {
    TParm parm = this.table.getParmValue();

    if (parm == null)
    {
      messageBox("没有需要导出的数据");
      return;
    }
    ExportExcelUtil.getInstance().exportExcel(this.table, "科室用血统计报表");
  }

  private TComboBox getComboBox(String tagName)
  {
    return (TComboBox)getComponent(tagName);
  }

  private TRadioButton getRadioButton(String tagName)
  {
    return (TRadioButton)getComponent(tagName);
  }

  private TTable getTable(String tagName)
  {
    return (TTable)getComponent(tagName);
  }
}