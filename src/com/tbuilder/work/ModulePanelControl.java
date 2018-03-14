package com.tbuilder.work;

import com.dongyang.config.TModuleConfigParse;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTreeNode;
import jdo.db.DBTool;

public class ModulePanelControl
  extends TControl
{
  public static final String DB_TREE = "TDB";
  private TTreeNode dbNodeRoot;
  private String filename;
  private String name;
  private String data;
  private TModuleConfigParse configParse;
  
  public void onInit()
  {
    super.onInit();
    TParm parm = (TParm)getParameter();
    
    this.name = parm.getValue("NAME");
    
    this.filename = parm.getValue("FILE_NAME");
    
    callMessage("UI|setName|" + this.name);
    
    this.data = new String(TIOM_AppServer.readFile(this.filename));
    
    callFunction("UI|UIDesign|setText", new Object[] { this.data });
    
    onInitDBTree();
  }
  
  public void onInitDBTree()
  {
    out("begin");
    this.dbNodeRoot = ((TTreeNode)callMessage("UI|TDB|getRoot"));
    if (this.dbNodeRoot == null) {
      return;
    }
    this.dbNodeRoot.setText("DataBase");
    this.dbNodeRoot.setType("DB");
    this.dbNodeRoot.removeAllChildren();
    downloadDBUserTree(this.dbNodeRoot);
    callMessage("UI|TDB|update");
    out("end");
  }
  
  public void downloadDBUserTree(TTreeNode root)
  {
    TParm parm = DBTool.getInstance().getUsers();
    if (parm.getErrCode() < 0)
    {
      err(parm.getErrCode() + " " + parm.getErrText());
      return;
    }
    int count = parm.getCount();
    for (int i = 0; i < count; i++)
    {
      String userName = parm.getValue("USERNAME", i);
      TTreeNode node = new TTreeNode(userName, "USER");
      node.setID(userName);
      root.add(node);
      downloadDBTableTree(node, userName);
    }
  }
  
  public void downloadDBTableTree(TTreeNode root, String owner)
  {
    TParm parm = DBTool.getInstance().getTables(owner);
    if (parm.getErrCode() < 0)
    {
      err(parm.getErrCode() + " " + parm.getErrText());
      return;
    }
    int count = parm.getCount();
    for (int i = 0; i < count; i++)
    {
      String tableName = parm.getValue("TABLE_NAME", i);
      TTreeNode node = new TTreeNode(tableName, "TABLE");
      node.setID(tableName);
      root.add(node);
    }
  }
  
  public void setConfigParse(TModuleConfigParse configParse)
  {
    this.configParse = configParse;
  }
  
  public TModuleConfigParse getConfigParse()
  {
    return this.configParse;
  }
  
  public void onSave()
  {
    String newData = (String)callFunction("UI|UIDesign|getText", new Object[0]);
    if (this.data.equals(newData)) {
      return;
    }
    TIOM_AppServer.writeFile(this.filename, newData.getBytes());
    this.data = newData;
  }
}
