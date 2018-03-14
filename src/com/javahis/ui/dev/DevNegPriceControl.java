package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TNumberTextField;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TDataStore;
import java.util.Date;
import com.dongyang.ui.TTextFormat;
import jdo.dev.DevBaseDataStore;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import com.dongyang.ui.TTabbedPane;
import javax.swing.SwingUtilities;
import jdo.dev.DevNegpriceDataStore;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:询价管理 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class DevNegPriceControl extends TControl {
    /**
      * 动作类名称
      */
     private String actionName = "action.dev.DevAction";
     /**
      * 主
      */
     private static String TABLE1="TABLE1";
     /**
      * 细
      */
     private static String TABLE2="TABLE2";
     /**
      * 细
      */
     private static String TABLE3 = "TABLE3";
     /**
      * 请购进度
      */
     private String rateofproCode="";
     /**
      * 审核状态
      */
     private boolean checkFlg=false;
     /**
      * 议价审核
      */
     private boolean checkFlgN=false;
     /**
      * 编辑状态
      */
     private String type="INSERT";
     /**
      * 页签
      */
     private static String TABLEPANE = "TABLEPANE";
     /**
      * 初始化参数
      */
     public void onInitParameter(){
         /**
          * 1、一般权限（科室人员）:锁定左右科室人员,请购进度默认为申请锁定右锁定核准 GeneralPermissions
          * 2、合并请购单（设备科员）锁定右科室人员锁定核准 ClerksPermissions
          * 3、审核（设备科长） AuditPermissions
          */
//        //一般权限
//        this.setPopedem("GeneralPermissions",true);
//        //合并请购单
//        this.setPopedem("ClerksPermissions",true);
         //审核
//         this.setPopedem("AuditPermissions",true);
     }
     /**
      * 初始化方法
      */
     public void onInit(){
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
     public void onInitPage(){
         //科室、人员
         this.setValue("QDEPT_CODE",Operator.getDept());
         this.setValue("QOPERATOR",Operator.getID());
         this.setValue("REQUEST_DEPT",Operator.getDept());
         this.setValue("REQUEST_USER",Operator.getID());
         //请购进度
         this.setValue("RATEOFPRO_CODE","A");
         this.setValue("QRATEOFPRO_CODE","A");
         //起日
         Timestamp startDate = StringTool.getTimestamp(StringTool.getString(StringTool.getTimestamp(new Date()),"yyyy/MM/dd"),"yyyy/MM/dd");
         //迄日
         Timestamp endDate = StringTool.getTimestamp("9999/12/31","yyyy/MM/dd");
         //查询请购日期起日
         this.setValue("QSTART_DATE",startDate);
         //查询请购日期迄日
         this.setValue("QEND_DATE",endDate);
         //请购日期
         this.setValue("REQUEST_DATE",startDate);
         //预定使用日起日
         this.setValue("QUSERSTART_DATE",startDate);
         //预定使用日迄日
         this.setValue("QUSEEND_DATE",endDate);
         //预定使用日
         this.setValue("USE_DATE",startDate);
         //初始化TABLE1
         this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
         this.getTTable(TABLE1).setDSValue();
         //初始化TABLE2
         this.getTTable(TABLE2).setDataStore(getTableTDataStore("TABLE2"));
         this.getTTable(TABLE2).setDSValue();
         //初始化TABLE3
         this.getTTable(TABLE3).setDataStore(getTableTDataStore("TABLE3"));
         this.getTTable(TABLE3).setDSValue();
         //保存设置灰
         callFunction("UI|save|setEnabled", false);
         //删除设置灰
         callFunction("UI|delete|setEnabled", false);
         //打印设置灰
         callFunction("UI|print|setEnabled", false);
     }
     /**
      * 事件初始化
      */
     public void initEven(){
         //主项TABLE1单击事件
         callFunction("UI|" + TABLE1 + "|addEventListener",
                     TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClickedTable1");
         //细项TABLE1单击事件
         callFunction("UI|" + TABLE3 + "|addEventListener",
                      TABLE3 + "->" + TTableEvent.CLICKED, this, "onTableClickedTable3");

     }
     /**
      * 明细点击事件
      * @param row int
      */
     public void onTableClickedTable3(int row){
         TParm parm = this.getTTable(TABLE3).getDataStore().getRowParm(row);
         if(parm.getData("CHK_DATE")!=null&&parm.getValue("CHK_USER").length()!=0){
             checkFlgN = true;
         }else{
             checkFlgN = false;
         }
         this.setValue("SUP_CODE",parm.getData("SUP_CODE"));
         onSupCodeChick();
         this.setValue("MAN_CODE",parm.getData("MAN_CODE"));
         this.setValue("MAN_NATION",parm.getData("MAN_NATION"));
         this.setValue("NEGTOT_AMT",parm.getData("TOT_AMT"));
         this.setValue("CURRENCY",parm.getData("CURRENCY"));
         this.setValue("RMBTOT_AMT",parm.getData("RMBTOT_AMT"));
         this.setValue("CHOOSE_FLG",parm.getBoolean("CHOOSE_FLG"));
         this.setValue("PAYMENT_TERMS",parm.getData("PAYMENT_TERMS"));
         this.setValue("REMARK",parm.getData("REMARK"));
         this.setValue("CHECKNeg_FLG",checkFlgN);
         type = "EDIT";
     }
     /**
      * 点击事件
      */
     public void onTableClickedTable1(int row){
         TParm parm = this.getTTable(TABLE1).getDataStore().getRowParm(row);
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
         //请购进度
         rateofproCode = parm.getValue("RATEOFPRO_CODE");
         if(parm.getData("CHK_DATE")!=null&&parm.getValue("CHK_USER").length()!=0){
             checkFlg = true;
         }else{
             checkFlg = false;
         }
         this.setValue("REQUEST_NO",parm.getData("REQUEST_NO"));
         this.setValue("REQUEST_DEPT", parm.getData("REQUEST_DEPT"));
         this.setValue("REQUEST_USER", parm.getData("REQUEST_USER"));
         this.setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
         this.setValue("REQUEST_DATE", parm.getData("REQUEST_DATE"));
         this.setValue("USE_DATE", parm.getData("USE_DATE"));
         this.setValue("TOT_AMT", parm.getData("TOT_AMT"));
         this.setValue("CHECK_FLG", checkFlg);
         this.setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
         this.setValue("PURTYPE_CODE", parm.getData("PURTYPE_CODE"));
         this.setValue("DEVUSE_CODE", parm.getData("DEVUSE_CODE"));
         //初始化TABLE2
         this.getTTable(TABLE2).setDataStore(getRequestDData(parm.getValue("REQUEST_NO")));
         this.getTTable(TABLE2).setDSValue();
         //初始化议价明细
         this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(this.getValueString("REQUEST_NO")));
         this.getTTable(TABLE3).setDSValue();
         type = "INSERT";
     }
     /**
      * 得到数字控件
      * @param tag String
      * @return TNumberTextField
      */
     public TNumberTextField getTNumberTextField(String tag){
         return (TNumberTextField)this.getComponent(tag);
     }
     /**
      * 拿到更变之前的列号
      * @param column int
      * @return int
      */
     public int getThisColumnIndex(int column){
         return this.getTTable(TABLE2).getColumnModel().getColumnIndex(column);
     }
     /**
      * 返回实际列名
      * @param column String
      * @param column int
      * @return String
      */
     public String getFactColumnName(String tableTag,int column){
         int col = this.getThisColumnIndex(column);
         return this.getTTable(tableTag).getDataStoreColumnName(col);
     }
     /**
      *
      */
     public void onChangeTable() {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 try {
                     onChangeTableD();
                 }
                 catch (Exception e) {
                 }

             }
         });
     }
     /**
      * 页签切换事件
      */
     public void onChangeTableD(){
         int row = this.getTTable(TABLE1).getSelectedRow();
         if(this.getTTabbedPane(TABLEPANE).getSelectedIndex()==1){
             if (row < 0) {
                 this.messageBox("请选中请购单！");
                 this.getTTabbedPane(TABLEPANE).setSelectedIndex(0);
                 return;
             }
             //if (!this.checkFlg) {
             if (false) {
                 this.messageBox("请购没有审核不可以定制议价明细！");
                 this.getTTabbedPane(TABLEPANE).setSelectedIndex(0);
                 return;
             }
             this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
             //初始化议价明细
             this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(this.getValueString("REQUEST_NO")));
             this.getTTable(TABLE3).setDSValue();
             //保存设置灰
             callFunction("UI|save|setEnabled", true);
             //删除设置灰
             callFunction("UI|delete|setEnabled", true);
             //打印设置灰
             callFunction("UI|print|setEnabled", true);
         }else{
             //保存设置灰
             callFunction("UI|save|setEnabled", false);
             //删除设置灰
             callFunction("UI|delete|setEnabled", false);
             //打印设置灰
             callFunction("UI|print|setEnabled", false);
         }
         type = "INSERT";
     }
     /**
      * 供应厂商下拉事件
      */
     public void onSupCodeChick(){
         String supCode = this.getValueString("SUP_CODE");
         TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1,SUP_SALES1_TEL,SUP_SALES1_EMAIL,ADDRESS FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         if(parm.getCount()<0)
             return;
         this.setValue("SUP_SALES1",parm.getData("SUP_SALES1",0));
         this.setValue("SUP_SALES1_TEL",parm.getData("SUP_SALES1_TEL",0));
         this.setValue("SUP_SALES1_EMAIL",parm.getData("SUP_SALES1_EMAIL",0));
         this.setValue("ADDRESS",parm.getData("ADDRESS",0));

     }
     /**
      * 返回数据库操作工具
      * @return TJDODBTool
      */
     public TJDODBTool getDBTool() {
         return TJDODBTool.getInstance();
     }

     /**
      * 得到TabbedPane
      * @param tag String
      * @return TTabbedPane
      */
     public TTabbedPane getTTabbedPane(String tag){
         return (TTabbedPane)this.getComponent(tag);
     }
     /**
      * 保存
      * @return boolean
      */
     public boolean onSave(){
         if("INSERT".equals(type)){
             if(!this.emptyTextCheck("SUP_CODE,MAN_CODE,MAN_NATION,CURRENCY"))
                 return false;
             if (this.getValueDouble("NEGTOT_AMT") <= 0) {
                 this.messageBox("请填写报价！");
                 return false;
             }
             if (this.getValueDouble("RMBTOT_AMT") <= 0) {
                 this.messageBox("请填写折合人民币！");
                 return false;
             }
             DevNegpriceDataStore devNegpriceData = (DevNegpriceDataStore)this.getTTable(TABLE3).getDataStore();
             if(devNegpriceData.rowCount()>0){
                  String sql[] = devNegpriceData.insertRow(getInsertParm());
                  TParm sqlParm = new TParm();
                  sqlParm.setData("SQL", sql);
                  TParm actionParm = TIOM_AppServer.executeAction(actionName,
                      "saveDevRequest", sqlParm);
                  if (actionParm.getErrCode() < 0) {
                      this.messageBox("保存失败！");
                      return false;
                  }
             }else{
                 String sql[] = devNegpriceData.insertRow(getInsertParm());
                 String arraySql[] = StringUtil.getInstance().copyArray(sql,
                     getUpdateRequestMSQL("B"));
                 TParm sqlParm = new TParm();
                 sqlParm.setData("SQL", arraySql);
                 TParm actionParm = TIOM_AppServer.executeAction(actionName,
                     "saveDevRequest", sqlParm);
                 if (actionParm.getErrCode() < 0) {
                     this.messageBox("保存失败！");
                     return false;
                 }
             }
         }else{
             //保存更新
            if ( (rateofproCode.equals("C")||rateofproCode.equals("D") || rateofproCode.equals("E"))) {
                this.messageBox("请购单请购进度状态不可以修改！");
                return false;
            }
             int row = this.getTTable(TABLE3).getSelectedRow();
             DevNegpriceDataStore devNegpriceData = (DevNegpriceDataStore)this.getTTable(TABLE3).getDataStore();
             if (!devNegpriceData.updateRow(row,getInsertParm())) {
                 this.messageBox("保存失败！");
                 return false;
             }
             type = "INSERT";
         }
         this.messageBox("保存成功！");
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         //初始化议价明细
         this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(this.getValueString("REQUEST_NO")));
         this.getTTable(TABLE3).setDSValue();
         return true;
     }
     /**
      * 更新请购进度
      * @return String[]
      */  
     public String [] getUpdateRequestMSQL(String type){
         return new String[]{"UPDATE DEV_PURCHASEM SET RATEOFPRO_CODE='"+type+"' WHERE REQUEST_NO='"+this.getValueString("REQUEST_NO")+"'"};
     }
     /**
      * 拿到新增数据
      * @return TParm
      */
     public TParm getInsertParm(){
         TParm result = new TParm();
         result.setData("REQUEST_NO",this.getValue("REQUEST_NO"));
         result.setData("SUP_CODE",this.getValue("SUP_CODE"));
         result.setData("MAN_CODE",this.getValue("MAN_CODE"));
         result.setData("MAN_NATION",this.getValue("MAN_NATION"));
         result.setData("TOT_AMT",this.getValueDouble("NEGTOT_AMT"));
         result.setData("CURRENCY",this.getValue("CURRENCY"));
         result.setData("RMBTOT_AMT",this.getValueDouble("RMBTOT_AMT"));
         result.setData("PAYMENT_TERMS",this.getValue("PAYMENT_TERMS"));
         result.setData("REMARK",this.getValue("REMARK"));
         result.setData("REMARK",this.getValue("REMARK"));
         result.setData("CHOOSE_FLG",this.getTCheckBox("CHOOSE_FLG").isSelected()?"Y":"N");
         if(this.getTCheckBox("CHECKNeg_FLG").isSelected()){
             result.setData("CHK_USER",Operator.getID());
             result.setData("CHK_DATE",StringTool.getTimestamp(new Date()));
         }else{
             result.setData("CHK_USER","");
             result.setData("CHK_DATE","");
         }
         result.setData("OPT_USER",Operator.getID());
         result.setData("OPT_DATE",StringTool.getTimestamp(new Date()));
         result.setData("OPT_TERM",Operator.getIP());
         return result;
     }
     /**
      * 返回TABLE的数据
      * @param tag String
      * @param queryParm TParm
      * @return TDataStore
      */
     public TDataStore getTableTDataStore(String tag){
         TDataStore dateStore = new TDataStore();
         if(tag.equals("TABLE1")){
             String sql="SELECT * FROM DEV_PURCHASEM"; 
             TParm queryParm = this.getTable1QueryParm();
             String columnName[] = queryParm.getNames();
             if(columnName.length>0)
                 sql+=" WHERE ";
             int count=0;
             for(String temp:columnName){
                 if(temp.equals("QEND_DATE"))
                     continue;
                 if(temp.equals("YEND_DATE"))
                     continue;
                 //请购日期
                 if(temp.equals("QSTART_DATE")){
                     if(count>0)
                         sql+=" AND ";
                     sql+=" REQUEST_DATE BETWEEN TO_DATE('"+queryParm.getValue("QSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("QEND_DATE")+"','YYYYMMDD')";
                     count++;
                     continue;
                 }
                 //预使用定日期
                 if(temp.equals("YSTART_DATE")){
                     if(count>0)
                         sql+=" AND ";
                     sql+=" USE_DATE BETWEEN TO_DATE('"+queryParm.getValue("YSTART_DATE")+"','YYYYMMDD') AND TO_DATE('"+queryParm.getValue("YEND_DATE")+"','YYYYMMDD')";
                     count++;
                      continue;
                 }
                 if(count>0)
                     sql+=" AND ";
                 sql+=temp+"='"+queryParm.getValue(temp)+"' ";
                 count++;
             } 
             System.out.println("sql::::::::"+sql);
             dateStore.setSQL(sql);
             dateStore.retrieve();
         } 
         if(tag.equals("TABLE2")){
             String qrequestNo = this.getValueString("QREQUEST_NO");
             DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
             devBaseDataStore.setRequestNo(qrequestNo);
             devBaseDataStore.onQuery();
             //参考价格总价格
             double totAmt =getTotAmt(devBaseDataStore);
             this.setValue("TOT_AMT",totAmt);
             return devBaseDataStore;
         }
         if(tag.equals("TABLE3")){
             String qrequestNo = this.getValueString("REQUEST_NO");
             DevNegpriceDataStore devNegPriceDataStore = new DevNegpriceDataStore();
             devNegPriceDataStore.setRequestNo(qrequestNo);
             devNegPriceDataStore.onQuery();
             return devNegPriceDataStore;
         }

         return dateStore;
     }
     /**
      * 拿到明细表数据
      * @param requestNo String
      * @return DevBaseDataStore
      */
     public DevBaseDataStore getRequestDData(String requestNo){
         String qrequestNo = requestNo;
         DevBaseDataStore devBaseDataStore = new DevBaseDataStore();
         devBaseDataStore.setRequestNo(qrequestNo);
         devBaseDataStore.onQuery();
         //参考价格总价格
         double totAmt =getTotAmt(devBaseDataStore);
         this.setValue("TOT_AMT",totAmt);
         return devBaseDataStore;
     }
     /**
      * 拿到明细表数据
      * @param requestNo String
      * @return DevBaseDataStore
      */
     public DevNegpriceDataStore getDev_NegPriceData(String requestNo){
         String qrequestNo = requestNo;
         DevNegpriceDataStore devNegPriceDataStore = new DevNegpriceDataStore();
         devNegPriceDataStore.setRequestNo(qrequestNo);
         devNegPriceDataStore.onQuery();
         //参考价格总价格
         return devNegPriceDataStore;
     }

     /**
      * 计算总价格
      * @param devBaseDataStore TDataStore
      * @return double
      */
     public double getTotAmt(TDataStore devBaseDataStore){
         int rowCount = devBaseDataStore.rowCount();
         double totAmt = 0;
         for (int i = 0; i < rowCount; i++) {
             if(!devBaseDataStore.isActive(i))
                 continue;
             totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE")*devBaseDataStore.getItemDouble(i, "QTY");
         }
         return totAmt;
     }
     /**
      * 拿到TABLE1的查询条件
      * @return TParm
      */
     public TParm getTable1QueryParm(){
         TParm result = new TParm();
         String startDate = StringTool.getString((Timestamp)this.getValue("QSTART_DATE"),"yyyyMMdd");
         String endDate = StringTool.getString((Timestamp)this.getValue("QEND_DATE"),"yyyyMMdd");
         String deptCode = this.getValueString("QDEPT_CODE");
         String operator = this.getValueString("QOPERATOR");
         String reteoptro = this.getValueString("QRATEOFPRO_CODE");
         String requestNo = this.getValueString("QREQUEST_NO");
         String ydDateStart = StringTool.getString((Timestamp)this.getValue("QUSERSTART_DATE"),"yyyyMMdd");
         String ydDateEnd = StringTool.getString((Timestamp)this.getValue("QUSEEND_DATE"),"yyyyMMdd");
         String fundsouCode = this.getValueString("QFUNDSOU_CODE");
         String purtypeCode = this.getValueString("QPURTYPE_CODE");
         String devUseCode = this.getValueString("QDEVUSE_CODE");
         if(startDate.length()!=0)
             result.setData("QSTART_DATE",startDate);
         if(endDate.length()!=0)
             result.setData("QEND_DATE",endDate);
         if(ydDateStart.length()!=0)
             result.setData("YSTART_DATE",ydDateStart);
         if(ydDateEnd.length()!=0)
             result.setData("YEND_DATE",ydDateEnd);
         if(deptCode.length()!=0)
             result.setData("REQUEST_DEPT",deptCode);
         if(operator.length()!=0)
             result.setData("REQUEST_USER",operator);
         if(reteoptro.length()!=0)
             result.setData("RATEOFPRO_CODE",reteoptro);
         if(requestNo.length()!=0)
             result.setData("REQUEST_NO",requestNo);
         if(fundsouCode.length()!=0)
             result.setData("FUNDSOU_CODE",fundsouCode);
         if(purtypeCode.length()!=0)
             result.setData("PURTYPE_CODE",purtypeCode);
         if(devUseCode.length()!=0)
             result.setData("DEVUSE_CODE",devUseCode);
         return result;
     }
     /**
      * 清空
      */
     public void onClear(){
         //清空
         this.clearValue("QREQUEST_NO;QFUNDSOU_CODE;QPURTYPE_CODE;QDEVUSE_CODE;REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         /**
          * 初始化页面
          */
         onInitPage();
         /**
         * 初始化权限
         */
        onInitPopeDem();
        //切换画面
        this.getTTabbedPane(TABLEPANE).setSelectedIndex(0);
        type = "INSERT";
        this.checkFlg = false;
     }
     /**
      * 查询
      */
     public void onQuery(){
         this.clearValue("REQUEST_NO;TOT_AMT;CHECK_FLG;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE;DETAILED_USE;REQUEST_REASON;BENEFIT_PROVE;REMARK");
         this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
         //初始化TABLE1
         this.getTTable(TABLE1).setDataStore(getTableTDataStore("TABLE1"));
         this.getTTable(TABLE1).setDSValue();
         //初始化TABLE2
         this.getTTable(TABLE2).setDataStore(getRequestDData(""));
         this.getTTable(TABLE2).setDSValue();
         //初始化TABLE3
         this.getTTable(TABLE3).setDataStore(getDev_NegPriceData(""));
         this.getTTable(TABLE3).setDSValue();
     }
     /**
      * 关闭事件
      * @return boolean
      */
     public boolean onClosing(){
         return true;
     }
     /**
      * 1、一般权限（科室人员）:锁定左右科室人员,请购进度默认为申请锁定右锁定核准 GeneralPermissions
      * 2、合并请购单（设备科员）锁定右科室人员锁定核准 ClerksPermissions
      * 3、审核（设备科长） AuditPermissions
      */
     public void onInitPopeDem(){
         //审核
         if(this.getPopedem("AuditPermissions")){
             this.getTCheckBox("CHOOSE_FLG").setEnabled(true);
             this.getTCheckBox("CHECKNeg_FLG").setEnabled(true);
         }
         //一般权限
         if(this.getPopedem("GeneralPermissions")){
             this.getTCheckBox("CHOOSE_FLG").setEnabled(false);
             this.getTCheckBox("CHECKNeg_FLG").setEnabled(false);
         }
     }
     /**
      * 拿到TTextFormat
      * @return TTextFormat
      */
     public TTextFormat getTTextFormat(String tag){
         return (TTextFormat)this.getComponent(tag);
     }
     /**
      * 拿到TComboBox
      * @param tag String
      * @return TComboBox
      */
     public TComboBox getTComboBox(String tag){
         return (TComboBox)this.getComponent(tag);
     }
     /**
      * 拿到TCheckBox
      * @param tag String
      * @return TCheckBox
      */
     public TCheckBox getTCheckBox(String tag){
         return (TCheckBox)this.getComponent(tag);
     }
     /**
      * 拿到TTable
      * @param tag String
      * @return TTable
      */
     public TTable getTTable(String tag){
         return (TTable)this.getComponent(tag);
     }
     /**
      * 删除
      */
     public void onDelete(){
         int row = this.getTTable(TABLE3).getSelectedRow();
         DevNegpriceDataStore devNegpriceData = (DevNegpriceDataStore)this.getTTable(TABLE3).getDataStore();
         int rowCount = devNegpriceData.rowCount();
         if(row<0){
             this.messageBox("请选择要删除的信息！");
             return;
         }
         if(checkFlgN){
             this.messageBox("已经审核不可以删除！");
             return;
         }
         if(this.getTCheckBox("CHOOSE_FLG").isSelected()){
             this.messageBox("已经中标不可以删除！");
             return;
         }
         if (messageBox("提示信息", "是否删除？", this.YES_NO_OPTION) != 0) {
             return;
         }
         if(rowCount>1){
             String sql[] = devNegpriceData.deleteRows(row);
             TParm sqlParm = new TParm();
             sqlParm.setData("SQL",sql);
             TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
             if (actionParm.getErrCode() < 0) {
                 this.messageBox("保存失败！");
                 return;
             }
             this.clearValue("SUP_CODE;SUP_SALES1;SUP_SALES1_TEL;ADDRESS;SUP_SALES1_EMAIL;MAN_CODE;MAN_NATION;NEGTOT_AMT;CURRENCY;RMBTOT_AMT;CHOOSE_FLG;CHECKNeg_FLG;PAYMENT_TERMS;REMARK");
             this.getTTable(TABLE3).setDSValue();
             this.messageBox("保存成功！");
         }else{
             String sql[] = devNegpriceData.deleteRows(row);
             String arraySql[] = StringUtil.getInstance().copyArray(sql,getUpdateRequestMSQL("A"));
             for(String temp:arraySql){
                 System.out.println("SQL:"+temp);
             }
             TParm sqlParm = new TParm();
             sqlParm.setData("SQL",arraySql);
             TParm actionParm = TIOM_AppServer.executeAction(actionName,"saveDevRequest", sqlParm);
             if (actionParm.getErrCode() < 0) {
                 this.messageBox("保存失败！");
                 return;
             }
             this.messageBox("保存成功！");
             this.onClear();
         }
     }

     /**
      * 拿到请购主档数据
      * @return TParm
      */
     public TParm getRequestM(String requestNo) {
         TParm result = new TParm();
         //请购单号
         result.setData("REQUEST_NO", requestNo);
         //请购日期
         result.setData("REQUEST_DATE", this.getValue("REQUEST_DATE"));
         //请购科室
         result.setData("REQUEST_DEPT", this.getValue("REQUEST_DEPT"));
         //请购人员
         result.setData("REQUEST_USER", this.getValue("REQUEST_USER"));
         //请购进度
         result.setData("RATEOFPRO_CODE", this.getValue("RATEOFPRO_CODE"));
         //预定使用日
         result.setData("USE_DATE", this.getValue("USE_DATE"));
         //请购总价
         result.setData("TOT_AMT", this.getValue("TOT_AMT"));
         //资金来源
         result.setData("FUNDSOU_CODE", this.getValue("FUNDSOU_CODE"));
         //请购类别
         result.setData("PURTYPE_CODE", this.getValue("PURTYPE_CODE"));
         //设备用途
         result.setData("DEVUSE_CODE", this.getValue("DEVUSE_CODE"));
         return result;
     }
     /**
      * 打印请购单
      */
     public void onPrint(){
         int row = this.getTTable(TABLE1).getSelectedRow();
        if(row<0){
            this.messageBox("请选择要打印的数据！");
            return;
        }
        TParm parm = getRequestM(this.getValueString("REQUEST_NO"));
        parm.setData("TITLE_NAME","TEXT",Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        parm.setData("FORMS_NAME","TEXT","议价分析表");
        TParm printDataParm = new TParm();
        TParm tableParm = this.getTTable(TABLE2).getDataStore().getBuffer(TDataStore.PRIMARY);
        int rowCount = tableParm.getCount();
        for(int i=0;i<rowCount;i++){
            if(!tableParm.getBoolean("#ACTIVE#",i))
                continue;
            printDataParm.addRowData(tableParm,i,"DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;UNIT_PRICE;QTY;REMARK");
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
        TParm printDataThreeParm = new TParm();
        TParm tableThreeParm = this.getTTable(TABLE3).getDataStore().getBuffer(TDataStore.PRIMARY);
        int rowCountThree = tableThreeParm.getCount();
        for(int i=0;i<rowCountThree;i++){
            if(!tableThreeParm.getBoolean("#ACTIVE#",i))
                continue;
            printDataThreeParm.addRowData(tableThreeParm,i,"SEQ_NO;SUP_CODE;TOT_AMT;MAN_CODE;REMARK");
        }
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "SEQ_NO");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
        printDataThreeParm.addData("SYSTEM", "COLUMNS", "REMARK");
        printDataThreeParm.setCount(printDataThreeParm.getCount("SEQ_NO"));
        parm.setData("DEV_NEGPRICE", printDataThreeParm.getData());
        this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\DevNegPrice.jhw",parm, false);
     }
}
