package com.javahis.ui.ind;

import com.dongyang.config.TConfigParm;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.JTableBase;
import java.awt.Component;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

public class INDOpmedControl extends TControl
{
  private TTable upTable;
  private TTable downTable;
  int selRow = -1;

  public void onInit()
  {
    initPage();
    onQuery();
  }

  public void initPage()
  {
    this.upTable = ((TTable)getComponent("upTable"));
    this.downTable = ((TTable)getComponent("downTable"));

    this.downTable.addEventListener("table.createEditComponent", this, 
      "onCreateSYSFEE");

    callFunction("UI|upTable|addEventListener", new Object[] { "upTable->table.clicked", 
      this, "onUpTableClicked" });
  }

  public void onCreateSYSFEE(Component com, int row, int column)
  {
    if (column != 1)
      return;
    if (!(com instanceof TTextField))
      return;
    TTextField textFilter = (TTextField)com;
    textFilter.onInit();

    TParm parm = new TParm();
    parm.setData("CAT1_TYPE", "PHA");
    textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
      "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);

    textFilter.addEventListener("popupMenu.ReturnValue", this, 
      "popReturn");
  }

  public void popReturn(String tag, Object obj)
  {
    if ((obj == null) && (!(obj instanceof TParm))) {
      return;
    }

    TParm result = (TParm)obj;
    String ordCode = result.getValue("ORDER_CODE");
    String ordDesc = result.getValue("ORDER_DESC");
    String unitcode = result.getValue("UNIT_CODE");
    String unitPrice = result.getValue("OWN_PRICE");
    String specification = result.getValue("SPECIFICATION");

    setDownTableAndTDS(ordCode, ordDesc, unitcode, unitPrice, specification);
  }

  public void setDownTableAndTDS(String code, String ordDesc, String unitcode, String unitPrice, String specification)
  {
    this.downTable.acceptText();
    int selrow = this.downTable.getSelectedRow();

    this.downTable.setItem(selrow, "N_DEL", "N");
    this.downTable.setItem(selrow, "ORDER_CODE", code);
    this.downTable.setItem(selrow, "ORDER_DESC", ordDesc);
    this.downTable.setItem(selrow, "UNIT_CODE", unitcode);
    this.downTable.setItem(selrow, "UNIT_PRICE", unitPrice);
    this.downTable.setItem(selrow, "QTY", Double.valueOf(1.0D));
    this.downTable.setItem(selrow, "SPECIFICATION", specification);
    this.downTable.setItem(selrow, "N_TOTFEE", Double.valueOf(Double.valueOf(unitPrice).doubleValue() * 1.0D));
    TParm parmRow = new TParm();
    parmRow.setData("N_DEL", "N");
    parmRow.setData("ORDER_CODE", null);
    parmRow.setData("ORDER_DESC", null);
    parmRow.setData("SPECIFICATION", null);
    parmRow.setData("QTY", null);
    parmRow.setData("UNIT_PRICE", null);
    parmRow.setData("N_TOTFEE", null);
    this.downTable.addRow(parmRow);
    this.downTable.getTable().grabFocus();
    this.downTable.setSelectedColumn(1);
  }

  public void getAmt()
  {
    this.downTable.acceptText();
    int row = this.downTable.getSelectedRow();
    double qty = this.downTable.getParmValue().getDouble("QTY", row);
    double price = this.downTable.getParmValue().getDouble("UNIT_PRICE", row);
    this.downTable.setItem(row, "N_TOTFEE", Double.valueOf(qty * price));
  }

  public void onDelete()
  {
    TParm result = new TParm();
    this.downTable.acceptText();
    this.upTable.acceptText();
    TParm parmD = this.downTable.getParmValue();
    for (int i = 0; i < parmD.getCount("ORDER_CODE") - 1; i++) {
      parmD.addData("OPMED_CODE", getValueString("OPMED_CODE"));
    }
    parmD.addData("OPMED_CODE", null);  

    int row = this.upTable.getSelectedRow();
    TParm parmM = this.upTable.getParmValue().getRow(row);

    List list = (List)parmD.getData("N_DEL");
    if (list.contains("Y"))
    {
      result = TIOM_AppServer.executeAction(
        "action.ind.INDOpmedAction", "onDeleteD", parmD);
    }
    else
    {
      TParm main = new TParm();
      main.setData("parmM", parmM.getData());
      main.setData("parmDAll", parmD.getData());

      result = TIOM_AppServer.executeAction(
        "action.ind.INDOpmedAction", "onDeleteMD", main);
    }

    if (result.getErrCode() < 0) {
      messageBox("删除失败");
    }
    else {
      messageBox("删除成功");
      onClear();
      onQuery();
    }
  }

  public void getOpmedCode()
  {
    String opmedCode = getValueString("OPMED_CODE");
    int count = opmedCode.length();
    for (int i = 0; i < 12 - count; i++) {
      opmedCode = "0" + opmedCode;
    }
    setValue("OPMED_CODE", opmedCode);
  }

  public void onQuery()
  {
    String opmedName = getValueString("OPMED_NAME");
    String opmedCode = getValueString("OPMED_CODE");
    String sql = "SELECT * FROM SYS_ORDER_OPMEDM WHERE 1=1 ";
    if (opmedCode.length() > 0) {
      sql = sql + " AND OPMED_CODE='" + opmedCode + "'";
    }
    if (opmedName.length() > 0) {
      sql = sql + " AND OPMED_NAME like '%" + opmedName + "%'";
    }
    sql = sql + " ORDER BY OPMED_CODE";
    TParm parmValue = new TParm(TJDODBTool.getInstance().select(sql));
    callFunction("UI|upTable|setParmValue", new Object[] { parmValue });
  }

  public void onSave()
  {
    TParm result = new TParm();
    TParm main = new TParm();
    TParm parmM = getupTableDS();
    TParm parmD = getdownTableDS();

    main.setData("parmM", parmM.getData());
    main.setData("parmD", parmD.getData());

    result = TIOM_AppServer.executeAction("action.ind.INDOpmedAction", "onInsert", main);
    if (result.getErrCode() < 0) {
      messageBox("E0001");
    }
    else {
      messageBox("P0001");
      onClear();
      onQuery();
    }

    callFunction("UI|OPMED_CODE|setEnabled", new Object[] { Boolean.valueOf(true) });
  }

  public TParm getupTableDS()
  {
    Timestamp now = TJDODBTool.getInstance().getDBTime();
    int row = this.upTable.getSelectedRow();
    TParm parmValueM = new TParm();
    parmValueM.setData("OPERATION_ICD", 
      getValueString("OPERATION_ICD"));
    parmValueM.setData("OPMED_NAME", getValueString("OPMED_NAME"));
    parmValueM.setData("OPMED_CODE", getValueString("OPMED_CODE"));
    parmValueM.setData("OPT_USER", Operator.getID());
    parmValueM.setData("OPT_DATE", now);
    parmValueM.setData("OPT_TERM", Operator.getIP());

    return parmValueM;
  }

  public TParm getdownTableDS()
  {
    Timestamp now = TJDODBTool.getInstance().getDBTime();
    int detailCount = this.downTable.getRowCount() - 1;

    String opmedCode = getValueString("OPMED_CODE");
    TParm parmValueD = new TParm();
    this.downTable.acceptText();
    TParm parm = this.downTable.getParmValue();

    for (int i = 0; i < detailCount; i++) {
      parmValueD.setData("OPMED_CODE", i, opmedCode);
      parmValueD.setData("OPT_USER", i, Operator.getID());
      parmValueD.setData("OPT_DATE", i, now);
      parmValueD.setData("OPT_TERM", i, Operator.getIP());

      parmValueD.setData("ORDER_CODE", i, parm.getData("ORDER_CODE", i));
      parmValueD.setData("ORDER_DESC", i, parm.getData("ORDER_DESC", i));
      parmValueD.setData("SEQ_NO", i, Integer.valueOf(i + 1));
      parmValueD.setData("UNIT_CODE", i, parm.getData("UNIT_CODE", i));
      parmValueD.setData("QTY", i, parm.getData("QTY", i));
      parmValueD.setData("SPECIFICATION", i, parm.getData("SPECIFICATION", i));
      parmValueD.setData("UNIT_PRICE", i, parm.getData("UNIT_PRICE", i));
      parmValueD.setData("N_TOTFEE", i, parm.getData("N_TOTFEE", i));
    }

    return parmValueD;
  }

  public void onNew()
  {
    cerateNewDate();

    String nowopmedCode = getValueString("OPMED_CODE");
    callFunction("UI|OPMED_CODE|setEnabled", new Object[] { Boolean.valueOf(false) });

    initDownTable(nowopmedCode);
  }

  public void initDownTable(String nowopmedCode)
  {
    this.downTable.acceptText();
    String sqlForDtl = "SELECT 'N' N_DEL,ORDER_CODE, ORDER_DESC, SPECIFICATION, UNIT_CODE, QTY, UNIT_PRICE, QTY*UNIT_PRICE AS N_TOTFEE FROM SYS_ORDER_OPMEDD WHERE OPMED_CODE ='" + 
      nowopmedCode + 
      "' ORDER BY TO_NUMBER(SEQ_NO)";

    TParm parmValueD = new TParm(TJDODBTool.getInstance().select(sqlForDtl));

    callFunction("UI|downTable|setParmValue", new Object[] { parmValueD });
    TParm parm = new TParm();
    parm.setData("N_DEL", null);
    parm.setData("ORDER_CODE", null);
    parm.setData("ORDER_DESC", null);
    parm.setData("SPECIFICATION", null);
    parm.setData("QTY", null);
    parm.setData("UNIT_PRICE", null);
    parm.setData("N_TOTFEE", null);
    this.downTable.addRow(parm);
  }

  public void onUpTableClicked(int row)
  {
    this.selRow = row;

    clearCtl();

    TParm parm = this.upTable.getParmValue();
    TParm tableDate = parm.getRow(this.selRow);

    setValueForDownCtl(tableDate);
    String opmedCode = getValueString("OPMED_CODE");

    initDownTable(opmedCode);
  }

  public void setValueForDownCtl(TParm date)
  {
    clearCtl();
    callFunction("UI|OPMED_CODE|setEnabled", new Object[] { Boolean.valueOf(false) });
    setValue("OPMED_CODE", date.getValue("OPMED_CODE"));
    setValue("OPMED_NAME", date.getValue("OPMED_NAME"));
    setValue("OPERATION_ICD", date.getValue("OPERATION_ICD"));
  }

  public void onClear()
  {
    clearCtl();
    callFunction("UI|OPMED_CODE|setEnabled", new Object[] { Boolean.valueOf(true) });
    this.upTable.removeRowAll();
    this.downTable.removeRowAll();
  }

  public void cerateNewDate()
  {
    String newCode = "";

    this.upTable.acceptText();

    String sql = "SELECT MAX (OPMED_CODE) AS OPMED_CODE FROM SYS_ORDER_OPMEDM";
    TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

    String maxCode = parm.getValue("OPMED_CODE", 0);

    if ("".equals(maxCode)) {
      maxCode = "0";
    }
    Integer newcode = Integer.valueOf(Integer.parseInt(maxCode) + 1);
    String code = newcode.toString();
    for (int i = 0; i < 12 - newcode.toString().length(); i++) {
      code = "0" + code;
    }
    newCode = newCode + code;

    setValue("OPMED_CODE", newCode);
    Timestamp date = TJDODBTool.getInstance().getDBTime();

    TParm parmDate = new TParm();
    parmDate.setData("OPMED_CODE", newCode);
    parmDate.setData("OPMED_NAME", "(新建名称)");
    parmDate.setData("OPERATION_ICD", null);
    parmDate.setData("SUM_AMT", null);
    parmDate.setData("OPT_USER", Operator.getName());
    parmDate.setData("OPT_DATE", date);
    parmDate.setData("OPT_TERM", Operator.getIP());
    this.upTable.addRow(parmDate);

    int row = this.upTable.getRowCount() - 1;
    this.upTable.setSelectedRow(row);
  }

  public void clearCtl()
  {
    String clearString = "OPMED_CODE;OPMED_NAME;OPERATION_ICD";
    clearValue(clearString);
  }

  public void onPrint()
  {
    Timestamp datetime = SystemTool.getInstance().getDate();
    TParm data = new TParm();
    DecimalFormat df = new DecimalFormat("#.00");
    TTextFormat operation_icd = (TTextFormat)getComponent("OPERATION_ICD");
    data.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
    data.setData("TITLE2", "TEXT", "手术药盒使用单");
    data.setData("OPMED_CODE", "TEXT", "药品盒号：" + 
      getValueString("OPMED_CODE"));
    data.setData("OPMED_NAME", "TEXT", "药品盒名称：" + 
      getValueString("OPMED_NAME"));
    data.setData("OPERATION_ICD", "TEXT", "适用手术：" + 
      operation_icd.getText());

    TParm parm = new TParm();
    TParm tableParm = getdownTableDS();
    TParm tableValue = new TParm();
    int count = tableParm.getCount("ORDER_DESC");

    for (int i = 0; i < count; i++) {
      tableValue.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i) + tableParm
        .getValue("SPECIFICATION", i));

      tableValue.addData("QTY", getQty(tableParm.getValue("QTY", i)));

      tableValue.addData("USER_QTY", "");
    }

    for (int i = 0; i < (count / 50 + 1) * 25; i++) {
      parm.addData("ORDER_DESC", "");
      parm.addData("QTY", "");
      parm.addData("USER_QTY", "");
      parm.addData("ORDER_DESC2", "");
      parm.addData("QTY2", "");
      parm.addData("USER_QTY2", "");
    }

    boolean left = true;
    int j = 0;
    for (int i = 0; i < tableValue.getCount("ORDER_DESC"); i++) {
      if (i / 25 % 2 == 0) {
        if (!left) {
          j = i / 2;
        }
        parm.setData("ORDER_DESC", j, tableValue.getValue("ORDER_DESC", i));
        parm.setData("QTY", j, tableValue.getValue("QTY", i));
        parm.setData("USER_QTY", j, tableValue.getValue("USER_QTY", i));
        left = true;
      } else {
        if (left) {
          j -= 25;
        }
        parm.setData("ORDER_DESC2", j, tableValue.getValue("ORDER_DESC", i));
        parm.setData("QTY2", j, tableValue.getValue("QTY", i));
        parm.setData("USER_QTY2", j, tableValue.getValue("USER_QTY", i));
        left = false;
      }
      j++;
    }

    parm.setCount(parm.getCount("ORDER_DESC"));
    parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
    parm.addData("SYSTEM", "COLUMNS", "QTY");
    parm.addData("SYSTEM", "COLUMNS", "USER_QTY");
    parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC2");
    parm.addData("SYSTEM", "COLUMNS", "QTY2");
    parm.addData("SYSTEM", "COLUMNS", "USER_QTY2");

    data.setData("DATE", "TEXT", "制表日期: " + 
      datetime.toString().substring(0, 10).replace('-', '/'));
    data.setData("USER", "TEXT", "制表人: " + Operator.getName());

    data.setData("TABLE", parm.getData());
    openPrintWindow("%ROOT%\\config\\prt\\IND\\INDOpmed.jhw", 
      data);
  }

  public String getQty(String qty)
  {
    String[] a = qty.split("\\.");
    if ((a.length > 1) && 
      (Integer.valueOf(a[1]).intValue() == 0)) {
      qty = a[0];
    }

    return qty;
  }

  public void onTableCheckBoxClicked(Object obj)
  {
    int column = this.downTable.getSelectedColumn();
    if (column == 0)
      this.downTable.acceptText();
  }
}