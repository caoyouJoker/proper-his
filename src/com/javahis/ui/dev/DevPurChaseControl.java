package com.javahis.ui.dev;

import com.dongyang.config.TConfigParm;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.JTableBase;
import com.dongyang.ui.base.TTableColumnModel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import java.awt.Component;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date; 

import jdo.dev.DEVStockCheckTool;
import jdo.dev.DevBaseDataStore;
import jdo.dev.DevCheckTool;
import jdo.dev.DevPurChaseDTool;
import jdo.dev.DevPurChaseMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.util.Organization;


/**  
 * <p>Title: 请购作业</p>  
 *
 * <p>Description: 请购作业</p>  
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: javahis</p>
 *
 * @author  fux
 * @version 1.0      
 */
public class DevPurChaseControl extends TControl
{
 /**
  * 动作类名称
  */
  private String actionName = "action.dev.DevAction";
  /**
   * 主
   */
  private static String TABLE1 = "TABLE1";
  /**
   * 细
   */
  private static String TABLE2 = "TABLE2";
  /**
   * 请购进度
   */
  private String rateofproCode = "";
  /**
   * 审核状态
   */
  private boolean checkFlg = false;
  /**
   * 初始化参数
   */
  public void onInitParameter()
  {
      /**
       * 1、一般权限（科室人员）:锁定左右科室人员,请购进度默认为申请锁定右锁定核准 GeneralPermissions
       * 2、审核（设备科长） AuditPermissions
       */
//      //一般权限
//      this.setPopedem("GeneralPermissions",true);
//      //合并请购单
//      this.setPopedem("ClerksPermissions",true);
      //审核 
//      this.setPopedem("AuditPermissions",true);
  }
  /**
   * 初始化
   */
  public void onInit()
  {
    /**
     * 初始化页面
     */	  
    onInitPage();
    /**
     * 初始化权限
     */
    onInitPopeDem();
    /**
     * 初始化事件
     */
    initEven();
  }
  /**
   * 初始化页面
   */	  
  public void onInitPage()
  {
	//科室、人员
    setValue("QDEPT_CODE", Operator.getDept());
    setValue("QOPERATOR", Operator.getID());
    setValue("REQUEST_DEPT", Operator.getDept());
    setValue("REQUEST_USER", Operator.getID());
    //订购进度
    setValue("RATEOFPRO_CODE", "A");
    setValue("QRATEOFPRO_CODE", "A");
    //起日
    Timestamp startDate = StringTool.getTimestamp(StringTool.getString(StringTool.getTimestamp(new Date()), "yyyy/MM/dd"), "yyyy/MM/dd");
    //迄日
    Timestamp endDate = StringTool.getTimestamp("9999/12/31", "yyyy/MM/dd");
    //查询请购日期起日
    setValue("QSTART_DATE", startDate);
    //查询请购日期迄日  
    setValue("QEND_DATE", endDate);
    //请购日期
    setValue("REQUEST_DATE", startDate);
    //预定交货起日 
    setValue("QUSERSTART_DATE", startDate);
    //预定交货迄日  
    setValue("QUSEEND_DATE", endDate);
    //预定交货日
    setValue("USE_DATE", startDate);
    //初始化TABLE1
    getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
    getTTable(TABLE1).setDSValue();
    //初始化TABLE2
    getTTable(TABLE2).setDataStore(getTableTDataStore("TABLE2"));
    getTTable(TABLE2).setDSValue();
    //插入一行
    insertRow(TABLE2);
    getTTable(TABLE2).setDSValue();
  }
  /** 
   * 事件初始化
   */
  public void initEven()
  {
	//主项TABLE1单击事件  
    callFunction("UI|" + TABLE1 + "|addEventListener", new Object[] { 
      TABLE1 + "->" + "table.clicked", this, "onTableClicked" });
    //细项TABLE2值改变监听
    addEventListener(TABLE2 + "->" + "table.changeValue", this, 
      "onChangeTableValue");     
    //细项TABLE2监听事件
    getTTable(TABLE2).addEventListener("table.createEditComponent", this, 
      "onCreateEditComoponent");
    //细项checkBox监听事件 
    getTTable(TABLE2).addEventListener("table.checkBoxClicked", this, "onCheckBoxValue");
  }
  /**
   * checkBox点击事件 
   * @param obj Object
   */
  public void onCheckBoxValue(Object obj)
  {
    TTable table = (TTable)obj;
    table.acceptText();
    int col = table.getSelectedColumn();
    String columnName = getTTable(TABLE2).getDataStoreColumnName(col);
    if ("#ACTIVE#".equals(columnName)) {
      double totAmt = getTotAmt(table.getDataStore());
      setValue("TOT_AMT", Double.valueOf(totAmt));
    }
  }
  /**
   * 值改变事件监听   
   * @param obj Object 
   */  
  public boolean onChangeTableValue(Object obj)
  {
	//拿到节点数据,存储当前改变的行号,列号,数据,列名等信息  
    TTableNode node = (TTableNode)obj;
    if (node == null) { 
      return true;
    } 
    //如果改变的节点数据和原来的数据相同就不改任何数据
    if (node.getValue().equals(node.getOldValue())) {
      return true;
    }
    //拿到table上的parmmap的列名
    String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
    if ("#ACTIVE#".equals(columnName)) {
      return false;
    }
    if ("UNIT_PRICE".equals(columnName)) {
      node.getTable().getDataStore().setItem(node.getRow(), "UNIT_PRICE", node.getValue());
      node.getTable().setDSValue(node.getRow());
    }
    if ("QTY".equals(columnName)) {
      node.getTable().getDataStore().setItem(node.getRow(), "QTY", node.getValue());
      node.getTable().setDSValue(node.getRow());
    }
    double totAmt = getTotAmt(node.getTable().getDataStore());
    setValue("TOT_AMT", Double.valueOf(totAmt));
    return false;
  }  
  /**
   * 点击事件 
   */
  public void onTableClicked(int row)
  {
    TParm parm = getTTable(TABLE1).getDataStore().getRowParm(row);
    callFunction("UI|setSysStatus", new Object[] { parm.getValue("DEV_CODE") + ":" + parm.getValue("DEV_CHN_DESC") + parm.getValue("SPECIFICATION") });
    //请购进度
    this.rateofproCode = parm.getValue("RATEOFPRO_CODE");
    if ((parm.getData("CHK_DATE") != null) && (parm.getValue("CHK_USER").length() != 0))
      this.checkFlg = true;
    else {
      this.checkFlg = false;
    }
    setValue("REQUEST_NO", parm.getData("REQUEST_NO"));
    setValue("REQUEST_DEPT", parm.getData("REQUEST_DEPT"));
    setValue("REQUEST_USER", parm.getData("REQUEST_USER"));
    setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
    setValue("REQUEST_DATE", parm.getData("REQUEST_DATE"));
    setValue("USE_DATE", parm.getData("USE_DATE"));
    setValue("TOT_AMT", parm.getData("TOT_AMT"));
    setValue("CHECK_FLG", Boolean.valueOf(this.checkFlg));
    setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
    setValue("PURTYPE_CODE", parm.getData("PURTYPE_CODE"));
    setValue("DEVUSE_CODE", parm.getData("DEVUSE_CODE"));
    setValue("DETAILED_USE", parm.getData("DETAILED_USE"));
    setValue("REQUEST_REASON", parm.getData("REQUEST_REASON"));
    setValue("BENEFIT_PROVE", parm.getData("BENEFIT_PROVE"));
    setValue("REMARK", parm.getData("REMARK"));
    //判断是否可以添加新的项目
    if ((this.rateofproCode.equals("C")) || (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E")) || (this.checkFlg)) {
      getTTextFormat("REQUEST_DEPT").setEnabled(false);
      getTTextFormat("REQUEST_USER").setEnabled(false);
      getTTextFormat("REQUEST_DATE").setEnabled(false);
      getTTextFormat("USE_DATE").setEnabled(false);
      getTNumberTextField("TOT_AMT").setEnabled(false);
      getTComboBox("FUNDSOU_CODE").setEnabled(false);
      getTComboBox("PURTYPE_CODE").setEnabled(false);
      getTComboBox("DEVUSE_CODE").setEnabled(false);
    } else {
      getTTextFormat("REQUEST_DEPT").setEnabled(true);
      getTTextFormat("REQUEST_USER").setEnabled(true);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
    }

    getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("REQUEST_NO")));
    getTTable(TABLE2).setDSValue();
    //初始化TABLE2
    if (!(this.checkFlg)) {
      //添加一行
      insertRow(TABLE2);
      getTTable(TABLE2).setLockColumns("");
    }
    else {
      getTTable(TABLE2).setLockColumns("all");
    }
    getTTable(TABLE2).setDSValue();
  }
  /**
   * 得到数字控件
   * @param tag String
   * @return TNumberTextField
   */
  public TNumberTextField getTNumberTextField(String tag)
  {
    return ((TNumberTextField)getComponent(tag));
  }
  /**
   * 拿到更变之前的列号
   * @param column int
   * @return int
   */
  public int getThisColumnIndex(int column)
  {
    return getTTable(TABLE2).getColumnModel().getColumnIndex(column);
  }
  /**
   * 返回实际列名
   * @param column String
   * @param column int
   * @return String
   */
  public String getFactColumnName(String tableTag, int column)
  {
    int col = getThisColumnIndex(column);
    return getTTable(tableTag).getDataStoreColumnName(col);
  }
  /**
   * 当TABLE创建编辑控件时临时
   * @param com Component
   * @param row int
   * @param column int
   */
  public void onCreateEditComoponent(Component com, int row, int column)
  { 
	//设备属性
    String devProCode = getTTable(TABLE2).getDataStore().getItemString(row, "DEVPRO_CODE");
    //状态条显示
    callFunction("UI|setSysStatus",""); 
    //拿到列名
    String columnName = getFactColumnName(TABLE2, column);
    if (!("DEV_CHN_DESC".equals(columnName)))
      return;
    if (!(com instanceof TTextField))
      return;
    TTextField textFilter = (TTextField)com;
    textFilter.onInit();
    TParm parm = new TParm(); 
    parm.setData("DEVPRO_CODE", devProCode);
    parm.setData("ACTIVE", "Y");
    //设置弹出菜单 
    textFilter.setPopupMenuParameter("DEVBASE", getConfigParm().newConfig("%ROOT%" +
    		"\\config\\sys\\DEVBASEPopupUI.x"), parm);
//    //定义接受返回值方法
//    textFilter.addEventListener("popupMenu.ReturnValue", this, "popReturn");
    //定义接受返回值方法         
    textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
  }   
  
  /**
   * 接受返回值方法
   * @param tag String
   * @param obj Object
   */
  public void popReturn(String tag, Object obj)  
  {
	//判断对象是否为空和是否为TParm类型
    if ((obj == null) && (!(obj instanceof TParm))) {
      return;
    } 
    //类型转换成TParm
    TParm action = (TParm)obj;
    callFunction("UI|setSysStatus", new Object[] { action.getValue("DEV_CODE") + ":" + action.getValue("DEV_CHN_DESC") + action.getValue("SPECIFICATION") });
    getTTable(TABLE2).acceptText();  
    int rowNum = getTTable(TABLE2).getSelectedRow(); 
    //判断是否可以添加新的项目
    if ((this.rateofproCode.equals("C")) || (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E"))) {
      messageBox("请购单请购进度状态不可以添加");
      getTTable(TABLE2).getDataStore().setItem(rowNum, "DEV_CHN_DESC", "");
      getTTable(TABLE2).setDSValue(rowNum);
      return;
    }
    if (this.checkFlg) {
      messageBox("请购单已经审核不可以添加");
      getTTable(TABLE2).getDataStore().setItem(rowNum, "DEV_CHN_DESC", "");
      getTTable(TABLE2).setDSValue(rowNum);
      return;
    }
    //判断是否有重复项
    if (isRepeatItem(action.getValue("DEV_CODE"), rowNum)) {
      messageBox("不可输入相同设备请在已有设备中修改数量！");
      getTTable(TABLE2).getDataStore().setItem(rowNum, "DEV_CHN_DESC", "");
      getTTable(TABLE2).setDSValue(rowNum);
      return;
    }
    String[] columnArr = getTTable(TABLE2).getDataStore().getColumns(); 
    for (String temp : columnArr) {
//    	tempREQUEST_NO
//    	tempSEQMAN_FLG
//    	tempDEVPRO_CODE
//    	tempDEV_CODE
//    	tempDEV_CHN_DESC
//    	tempDESCRIPTION
//    	tempQTY
//    	tempSTORGE_QTY
//    	tempSETDEV_CODE
//    	tempUNIT_CODE
//    	tempUNIT_PRICE
//    	tempTOT_VALUE
//    	tempUPDATE_FLG
//    	tempOPT_USER
//    	tempOPT_DATE
//    	tempOPT_TERM
      if (action.getValue(temp).length() == 0)
        continue;
      if ("OPT_DATE".equals(temp))
        continue;
      if ("OPT_USER".equals(temp))
        continue;
      if ("OPT_TERM".equals(temp))
        continue;  
//      数据Y
//      数据A
//      数据211AA0001
//      数据台式血压计主设备
//      数据121
//      数据80.0    
      //System.out.println("数据"+action.getData(temp));
      getTTable(TABLE2).getDataStore().setItem(rowNum, temp, action.getData(temp));
    } 
    getTTable(TABLE2).getDataStore().setActive(rowNum, true);
    insertRow(TABLE2);
    getTTable(TABLE2).setDSValue();
    getTTable(TABLE2).getTable().grabFocus();
    getTTable(TABLE2).setSelectedRow(rowNum);
    getTTable(TABLE2).setSelectedColumn(4);
  }
  
  
  /**
   * 是否有重复项 
   * @return boolean
   */
  public boolean isRepeatItem(String devOrder, int selCount)
  {
    boolean falg = false;
    TDataStore dataStore = getTTable(TABLE2).getDataStore();
    int rowCount = dataStore.rowCount();
    for (int i = 0; i < rowCount; ++i) {
      if (!(dataStore.isActive(i)))
        continue;
      if (i == selCount)
        continue;
      if (devOrder.equals(dataStore.getItemString(i, "DEV_CODE")))
        falg = true;
    }
    return falg;
  }
  /**
   * 保存
   * @return boolean
   */
  public boolean onSave()
  {
	//检核细表
    if (!(emptyTextCheck("REQUEST_DEPT,REQUEST_USER,RATEOFPRO_CODE,REQUEST_DATE,USE_DATE,TOT_AMT,FUNDSOU_CODE,PURTYPE_CODE,DEVUSE_CODE"))) {
      return false;
    }
    TParm checkParm = isCheckMItem(); 
    if (checkParm.getErrCode() < 0) {
      messageBox(checkParm.getErrText());
      return false; 
    }  
    //新增
    if (getValueString("REQUEST_NO").length() == 0)
    {
      //请购号 
      String requestNo = SystemTool.getInstance().getNo("ALL", "DEV", 
        "REQUEST_NO", "REQUEST_NO"); 
      TParm requestMParm = getRequestM(requestNo); 
      //requestMParm.setData("TYPE","INSERT"); 
      TParm resultM = DevPurChaseMTool.getInstance().insertDevPurChaseM(requestMParm,"INSERT");
//      String[] sqlRequestM = { creatRequestMSQL(requestMParm, "INSERT") };
//      for (String temp : sqlRequestM) {
//        System.out.println("temp:" + temp); 
//      }     
          
      TParm requestDParm = getRequestD(requestNo); 
      //requestDParm.setData("TYPE","INSERT"); 
      TParm resultD = DevPurChaseDTool.getInstance().insertDevPurChaseD(requestDParm,"INSERT");
//      String[] sqlRequestD = {creatRequestDSQL(requestDParm, "INSERT")};
//      for(String temp:sqlRequestD){ 
//          System.out.println("temp:"+temp);
//      }   
      if (resultM.getErrCode() < 0||resultD.getErrCode() < 0) {
        messageBox("保存失败！");
        return false; 
      }
      messageBox("保存成功！");
      setValue("REQUEST_NO", requestNo);
    }
    //更新
    else {
      if ((this.rateofproCode.equals("B")) || (this.rateofproCode.equals("C")) || 
        (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E"))) {
        messageBox("请购单请购进度状态不可以修改！");
        return false;
      } 
      getTTable(TABLE2).acceptText(); 
      String requestNo = this.getValueString("REQUEST_NO"); 
      TParm requestMParm = getRequestM(requestNo);  
      //requestMParm.setData("TYPE","UPDATE");  
      TParm resultM = DevPurChaseMTool.getInstance().insertDevPurChaseM(requestMParm,"UPDATE");
//      String[] sqlRequestM = { creatRequestMSQL(requestMParm, "UPDATE") };
//      for (String temp : sqlRequestM) {
//          System.out.println("temp:" + temp); 
//        }         
      TParm requestDParm = getRequestM(requestNo);  
      //requestDParm.setData("TYPE","UPDATE"); 
      TParm resultD = DevPurChaseDTool.getInstance().insertDevPurChaseD(requestDParm,"UPDATE");
//      String[] sqlRequestD = {creatRequestDSQL(requestDParm, "UPDATE")};
//      for (String temp : sqlRequestD) { 
//          System.out.println("temp:" + temp); 
//        }   
      if (resultM.getErrCode() < 0||resultD.getErrCode() < 0) {
        messageBox("保存失败！"); 
        return false;
      } 
      messageBox("保存成功！");
    }
    onClear();
    onQuery();
    return true;
  }
  /**
   * 拿到请购主档数据
   * @return TParm
   */
  public TParm getRequestM(String requestNo)
  { 
    TParm result = new TParm();  
    //请购单号
    result.setData("REQUEST_NO", requestNo);
    //请购日期REQUEST_DATE=2013-06-27 00:00:00.0
	String requestDate = this.getValueString("REQUEST_DATE").substring(0, 10).replace(":", "")
	.replace("-", "").replace(" ", ""); 
    result.setData("REQUEST_DATE", requestDate);
    //请购科室 
    result.setData("REQUEST_DEPT", getValue("REQUEST_DEPT"));
    //请购人员
    result.setData("REQUEST_USER", getValue("REQUEST_USER"));
    //订购进度
    result.setData("RATEOFPRO_CODE", getValue("RATEOFPRO_CODE"));
    //预定交货日
	String useDate = this.getValueString("USE_DATE").substring(0, 10).replace(":", "")
	.replace("-", "").replace(" ", "");
    result.setData("USE_DATE", useDate); 
    //总价 
    result.setData("TOT_AMT", getValue("TOT_AMT"));
    //资金来源
    result.setData("FUNDSOU_CODE", getValue("FUNDSOU_CODE"));
    //请购类别
    result.setData("PURTYPE_CODE", getValue("PURTYPE_CODE"));
    //设备用途
    result.setData("DEVUSE_CODE", getValue("DEVUSE_CODE"));
    //详细用途
    result.setData("DETAILED_USE", getValue("DETAILED_USE"));
    //请购原因
    result.setData("REQUEST_REASON", getValue("REQUEST_REASON"));
    //效益论证 
    result.setData("BENEFIT_PROVE", getValue("BENEFIT_PROVE"));
    //备注
    result.setData("REMARK", getValue("REMARK"));
    Timestamp timestamp = SystemTool.getInstance().getDate();
    String sysDate = StringTool.getString(timestamp,
    "yyyyMMdd");    
    if (getTCheckBox("CHECK_FLG").isSelected())
    {   
      result.setData("CHK_USER", Operator.getID());
      result.setData("CHK_DATE", sysDate); 
    } 
    else {  
      result.setData("CHK_USER", getValue("CHK_USER"));
      //没有CHK_DATE....
  	  String chkDate = this.getValueString("CHK_DATE").substring(0, 10).replace(":", "")
	  .replace("-", "").replace(" ", "");; 
      result.setData("CHK_DATE", chkDate); 
    }   
    //操作员  
    result.setData("OPT_USER", Operator.getName());  
    //操作时间
    result.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
    //操作IP  
    result.setData("OPT_TERM",  sysDate);  
    System.out.println("拿到请购主档数据result"+result); 
    return result; 
  }
  /**
   * 拿到请购细档数据   
   * @return TParm
   */ 
  public TParm getRequestD(String requestNo)
  { 
    TParm result = new TParm(); 
    TTable tableD = getTTable(TABLE2);
    //#ACTIVE#;DEVPRO_CODE;DEV_CHN_DESC;
    //SPECIFICATION;UNIT_PRICE;QTY;UNIT_CODE;XJ;DEV_CODE;REMARK
    for(int i=0;i<tableD.getRowCount()-1;i++){
    	System.out.println("i"+i);   
    //请购单号
    result.setData("REQUEST_NO", requestNo);
    //序号 
    result.setData("SEQ_NO", i+1);    
    //设备名称 
    result.setData("DEV_CHN_DESC", tableD.getItemData(i, "DEV_CHN_DESC"));
    //规格
    result.setData("SPECIFICATION", tableD.getItemData(i, "SPECIFICATION")); 
    //单位
    result.setData("UNIT_CODE", tableD.getItemData(i, "UNIT_CODE")); 
    //设备属性
    result.setData("DEVPRO_CODE", tableD.getItemData(i, "DEVPRO_CODE")); 
    //设备编码
    result.setData("DEV_CODE", tableD.getItemData(i, "DEV_CODE"));   
    //单价 
    result.setData("UNIT_PRICE", tableD.getItemData(i, "UNIT_PRICE"));  
    //数量
    result.setData("QTY", tableD.getItemData(i, "QTY")); 
    //总量 
    result.setData("SUM_QTY", "");  
    //备注
    result.setData("REMARK", tableD.getItemData(i, "REMARK"));
    //操作员
    result.setData("OPT_USER", Operator.getName());  
    //操作时间
    result.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
    //操作IP  
    result.setData("OPT_TERM", Operator.getIP());
    }
    System.out.println("拿到请购细档数据result"+result);  
    return result;
  }
   
  /**
   * 检核细项表
   * @return TParm
   */
  public TParm isCheckMItem()
  {
    TParm result = new TParm();
    TDataStore dateStore = getTTable(TABLE2).getDataStore();
    int rowCount = dateStore.rowCount();
    if (rowCount <= 0) {
      result.setErrCode(-1);
      result.setErrText("请填写设备明细资料！");
      return result;
    }
    for (int i = 0; i < rowCount; ++i) {
      if (!(dateStore.isActive(i))) 
        continue;
      if (dateStore.getItemDouble(i, "UNIT_PRICE") <= 0.0D) {
        result.setErrCode(-2);
        result.setErrText("设备名为:" + dateStore.getItemString(i, "DEV_CHN_DESC") + "的项目请填写参考价格！");
        return result;
      }
      if (dateStore.getItemInt(i, "QTY") <= 0) {
        result.setErrCode(-3);
        result.setErrText("设备名为:" + dateStore.getItemString(i, "DEV_CHN_DESC") + "的项目请填写数量！");
        return result;
      }
    }
    return result;
  }
  /**
   * 插入空行
   */
  public int insertRow(String tag)
  {
    int rowNum = -1;
    boolean falg = true;
    TTable table = getTTable(tag);
    TDataStore tabDataStore = table.getDataStore();
    int rowCount = tabDataStore.rowCount();
    for (int i = 0; i < rowCount; ++i) {
      if (!(tabDataStore.isActive(i))) {
        falg = false;
        break;
      }
    }
    if (falg) {
      rowNum = tabDataStore.insertRow();
      tabDataStore.setActive(rowNum, false);
    }
    return rowNum;
  }
  /**
   * 返回TABLE的数据
   * @param tag String
   * @param queryParm TParm
   * @return TDataStore
   */
  public TDataStore getTableTDataStore(String tag)
  {
    TDataStore dateStore = new TDataStore();
    if (tag.equals("TABLE1")) {
      String sql = "SELECT * FROM DEV_PURCHASEM";
      TParm queryParm = getTable1QueryParm();
      String[] columnName = queryParm.getNames();
      if (columnName.length > 0)
        sql = sql + " WHERE ";
      int count = 0;
      for (String temp : columnName) {
        if (temp.equals("QEND_DATE"))
          continue;
        if (temp.equals("YEND_DATE")) {
          continue;
        }
        if (temp.equals("QSTART_DATE")) {
          if (count > 0)
            sql = sql + " AND ";
          sql = sql + " REQUEST_DATE BETWEEN TO_DATE('" + queryParm.getValue("QSTART_DATE") + "','YYYYMMDD') AND TO_DATE('" + queryParm.getValue("QEND_DATE") + "','YYYYMMDD')";
          ++count;
        }
        else if (temp.equals("YSTART_DATE")) {
          if (count > 0)
            sql = sql + " AND ";
          sql = sql + " USE_DATE BETWEEN TO_DATE('" + queryParm.getValue("YSTART_DATE") + "','YYYYMMDD') AND TO_DATE('" + queryParm.getValue("YEND_DATE") + "','YYYYMMDD')";
          ++count;
        }
        else {
          if (count > 0)
            sql = sql + " AND ";
          sql = sql + temp + "='" + queryParm.getValue(temp) + "' ";
          ++count; }
      }
      System.out.println("sql:" + sql);
      dateStore.setSQL(sql);
      dateStore.retrieve();
    }
    if (tag.equals("TABLE2")) {
      String qrequestNo = getValueString("QREQUEST_NO");
      DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
      devBaseDataStore.setRequestNo(qrequestNo);
      devBaseDataStore.onQuery();

      double totAmt = getTotAmt(devBaseDataStore);
      setValue("TOT_AMT", Double.valueOf(totAmt));
      return devBaseDataStore;
    }
    return dateStore;
  }
  /**
   * 拿到明细表数据
   * @param requestNo String
   * @return DevBaseDataStore
   */
  public DevBaseDataStore getRequestDData(String requestNo)
  {
    String qrequestNo = requestNo;
    DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
    devBaseDataStore.setRequestNo(qrequestNo);
    devBaseDataStore.onQuery();

    double totAmt = getTotAmt(devBaseDataStore);
    setValue("TOT_AMT", Double.valueOf(totAmt));
    return devBaseDataStore;
  }
  /**
   * 计算总价格
   * @param devBaseDataStore TDataStore
   * @return double
   */
  public double getTotAmt(TDataStore devBaseDataStore)
  {
    int rowCount = devBaseDataStore.rowCount();
    double totAmt = 0.0D;
    for (int i = 0; i < rowCount; ++i) {
      if (!(devBaseDataStore.isActive(i)))
        continue;
      totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE") * devBaseDataStore.getItemDouble(i, "QTY");
    }
    return totAmt;
  }
  /**
   * 拿到TABLE1的查询条件
   * @return TParm
   */
  public TParm getTable1QueryParm()
  {
    TParm result = new TParm();
    String startDate = StringTool.getString((Timestamp)getValue("QSTART_DATE"), "yyyyMMdd");
    String endDate = StringTool.getString((Timestamp)getValue("QEND_DATE"), "yyyyMMdd");
    String deptCode = getValueString("QDEPT_CODE");
    String operator = getValueString("QOPERATOR");
    String reteoptro = getValueString("QRATEOFPRO_CODE");
    String requestNo = getValueString("QREQUEST_NO");
    String ydDateStart = StringTool.getString((Timestamp)getValue("QUSERSTART_DATE"), "yyyyMMdd");
    String ydDateEnd = StringTool.getString((Timestamp)getValue("QUSEEND_DATE"), "yyyyMMdd");
    String fundsouCode = getValueString("QFUNDSOU_CODE");
    String purtypeCode = getValueString("QPURTYPE_CODE");
    String devUseCode = getValueString("QDEVUSE_CODE");
    if (startDate.length() != 0)
      result.setData("QSTART_DATE", startDate);
    if (endDate.length() != 0)
      result.setData("QEND_DATE", endDate);
    if (ydDateStart.length() != 0)
      result.setData("YSTART_DATE", ydDateStart);
    if (ydDateEnd.length() != 0)
      result.setData("YEND_DATE", ydDateEnd);
    if (deptCode.length() != 0)
      result.setData("REQUEST_DEPT", deptCode);
    if (operator.length() != 0)
      result.setData("REQUEST_USER", operator);
    if (reteoptro.length() != 0)
      result.setData("RATEOFPRO_CODE", reteoptro);
    if (requestNo.length() != 0)
      result.setData("REQUEST_NO", requestNo);
    if (fundsouCode.length() != 0)
      result.setData("FUNDSOU_CODE", fundsouCode);
    if (purtypeCode.length() != 0)
      result.setData("PURTYPE_CODE", purtypeCode);
    if (devUseCode.length() != 0)
      result.setData("DEVUSE_CODE", devUseCode);
    return result;
  }
  /**
   * 清空   
   */
  public void onClear()
  { 
    //判断是否保存
    //清空
    clearValue("QREQUEST_NO;QFUNDSOU_CODE;QPURTYPE_CODE;QDEVUSE_CODE;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
    /**
     * 初始化页面
     */
    onInitPage();
    /**
     * 初始化权限
     */
    onInitPopeDem();
    this.checkFlg = false;
  }
  /**
   * 查询
   */
  public void onQuery()
  {
	 
    clearValue("REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
    //初始化TABLE1
    getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
    getTTable(TABLE1).setDSValue();
    //初始化TABLE2
    getTTable(TABLE2).setDataStore(getRequestDData(""));
    getTTable(TABLE2).setDSValue();
    //添加一行
    insertRow(TABLE2);
    getTTable(TABLE2).setDSValue();
  }
  /**
   * 关闭事件
   * @return boolean
   */
  public boolean onClosing()
  {
	//判断是否保存
    return true;
  }
  /**
   * 1、一般权限（科室人员）:锁定左右科室人员,请购进度默认为申请锁定右锁定核准 GeneralPermissions
   * 2、合并请购单（设备科员）锁定右科室人员锁定核准 ClerksPermissions
   * 3、审核（设备科长） AuditPermissions
   */
  public void onInitPopeDem()
  {
	//审核
    if (getPopedem("AuditPermissions")) {
      getTTextFormat("QDEPT_CODE").setEnabled(true);
      getTTextFormat("QOPERATOR").setEnabled(true);
      getTTextFormat("REQUEST_DEPT").setEnabled(true);
      getTTextFormat("REQUEST_USER").setEnabled(true);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
      getTCheckBox("CHECK_FLG").setEnabled(true);
      return;
    } 
    //一般权限
    if (getPopedem("GeneralPermissions")) {
      getTTextFormat("QDEPT_CODE").setEnabled(false);
      getTTextFormat("QOPERATOR").setEnabled(false);
      getTTextFormat("REQUEST_DEPT").setEnabled(false);
      getTTextFormat("REQUEST_USER").setEnabled(false);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
      getTCheckBox("CHECK_FLG").setEnabled(true);
    }

    if (getPopedem("ClerksPermissions")) {
      getTTextFormat("REQUEST_DEPT").setEnabled(false);
      getTTextFormat("REQUEST_USER").setEnabled(false);
      getTTextFormat("REQUEST_DATE").setEnabled(true);
      getTTextFormat("USE_DATE").setEnabled(true);
      getTNumberTextField("TOT_AMT").setEnabled(true);
      getTComboBox("FUNDSOU_CODE").setEnabled(true);
      getTComboBox("PURTYPE_CODE").setEnabled(true);
      getTComboBox("DEVUSE_CODE").setEnabled(true);
      getTCheckBox("CHECK_FLG").setEnabled(false);
    }
  }
  /**
   * 拿到TTextFormat
   * @return TTextFormat
   */
  public TTextFormat getTTextFormat(String tag)
  {
    return ((TTextFormat)getComponent(tag));
  }
  /**
   * 拿到getTComboBox
   * @return getTComboBox
   */
  public TComboBox getTComboBox(String tag)
  {
    return ((TComboBox)getComponent(tag));
  }
  /**
   * 拿到getTCheckBox
   * @return getTCheckBox
   */
  public TCheckBox getTCheckBox(String tag)
  {
    return ((TCheckBox)getComponent(tag));
  }
  /**
   * 拿到getTTable
   * @return getTTable
   */
  public TTable getTTable(String tag)
  {
    return ((TTable)getComponent(tag));
  }
  /**
   * 删除
   */
  public void onDelete()
  {
    int row = getTTable(TABLE1).getSelectedRow();
    if (row < 0) {
      messageBox("请选择要删除的数据！");
      return;
    }
    if ((this.rateofproCode.equals("B")) || (this.rateofproCode.equals("C")) || (this.rateofproCode.equals("D")) || (this.rateofproCode.equals("E"))) {
      messageBox("请购单请购进度状态不可以删除！");
      return;
    }
    if (this.checkFlg) {
      messageBox("请购单请已经审核不可以删除！");
      return;
    }
    if (messageBox("提示信息", "是否删除?", 0) != 0)
      return;
    getTTable(TABLE1).getDataStore().deleteRow(row);
    int rowCount = getTTable(TABLE2).getDataStore().rowCount();
    for (int i = rowCount - 1; i >= 0; --i) {
      if ((!(getTTable(TABLE2).getDataStore().isActive(i))) && (getTTable(TABLE2).getDataStore().getItemString(i, "REQUEST_NO").length() == 0)) {
        continue;
      }
      getTTable(TABLE2).getDataStore().deleteRow(i);
    }
    getTTable(TABLE1).setDSValue();
    getTTable(TABLE2).setDSValue();
    String[] arraySqlTable1 = getTTable(TABLE1).getUpdateSQL();
    String[] arraySqlTable2 = getTTable(TABLE2).getUpdateSQL();
    StringUtil.getInstance(); String[] arraySql = StringUtil.copyArray(arraySqlTable1, arraySqlTable2);
    TParm sqlParm = new TParm();
    sqlParm.setData("SQL", arraySql);
    TParm actionParm = TIOM_AppServer.executeAction(this.actionName, "saveDevRequest", sqlParm);
    if (actionParm.getErrCode() < 0) {
      messageBox("删除失败！");
      return;
    }
    messageBox("删除成功！");
    onClear();
  }
  /**
   * 打印
   */
  public void onPrint()
  {
    int row = getTTable(TABLE1).getSelectedRow();
    if (row < 0) {
      messageBox("请选择要打印的数据！");
      return;
    }  
    TParm parm = getRequestM(getValueString("REQUEST_NO"));
    parm.setData("TITLE_NAME", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
    parm.setData("FORMS_NAME", "TEXT", "设备请购单");
    TParm printDataParm = new TParm();
    TParm tableParm = getTTable(TABLE2).getDataStore().getBuffer("Primary!");
    System.out.println("tableParm" + tableParm);
    int rowCount = tableParm.getCount();
    for (int i = 0; i < rowCount; ++i) {
      if (!(tableParm.getBoolean("#ACTIVE#", i)))
        continue;
      printDataParm.addRowData(tableParm, i, "DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;QTY;REMARK");
    }
    printDataParm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
    printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
    printDataParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
    printDataParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
    printDataParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
    printDataParm.addData("SYSTEM", "COLUMNS", "QTY");
    printDataParm.addData("SYSTEM", "COLUMNS", "REMARK");
    printDataParm.setCount(printDataParm.getCount("DEV_CODE"));
    parm.setData("DEV_PURCHASED", printDataParm.getData());
    System.out.println("parm" + parm);
    openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevRequestForms.jhw", parm, false);
  } 
  
  //换行实例
  public void spilt() {   
	    String name = "本アプリは効果音を使用しております。うっかり電車の中などで使うと恥ずかしい場合がありますので、ボリュームやご利用環境には十分ご注意ください";   
	    int length = name.length();   
	    int part = (length + 2 ) / 3;   
	  
	    int from = 0;   
	    int to = 0;   
	    StringBuilder builder = new StringBuilder();   
	    for (int i = 0; i < 3; i++) {   
	        from = to;   
	        to = from + part;   
	        to = to > length ? length : to;   
	        builder.append(name.subSequence(from, to-1)).append("\n");   
	    }   
	       
	    // 这是处理过的结果   
	    String result = builder.toString();   
	       
	    System.out.println(result);   
	}  

  
  
  
  
  
  
  
}