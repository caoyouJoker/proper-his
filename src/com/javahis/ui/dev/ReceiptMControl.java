package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDataStore;
import java.util.Date;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;


/**
 * <p>Title: 验收单窗口</p> 
 *
 * <p>Description: 验收单窗口</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ReceiptMControl extends TControl {
    /**
      * TABLE
      */
     private static String TABLE = "TABLE";
     public void onInit() {
         this.initPage();
         this.initEven();  
     }
     /**
      * 初始化事件
      */
     public void initEven(){
         callFunction("UI|" + TABLE + "|addEventListener",
                      TABLE + "->" + TTableEvent.CLICKED, this, "onTableClicked");
     }
     /**
      * 点击事件
      * @param row int
      */
     public void onTableClicked(int row){
         TParm parm = this.getTTable(TABLE).getDataStore().getRowParm(row);
         this.setValue("PURORDER_NO",parm.getData("PURORDER_NO"));
         this.setValue("PURORDER_DEPT", parm.getData("PURORDER_DEPT"));
         this.setValue("PURORDER_USER", parm.getData("PURORDER_USER"));
         this.setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
         this.setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
     }
     public void initPage() {
         //科室、人员
         this.setValue("PURORDER_DEPT", Operator.getDept());
         this.setValue("PURORDER_USER", Operator.getID());
         //请购进度
         this.setValue("RATEOFPRO_CODE", "C");
         //起日
         Timestamp startDate = StringTool.getTimestamp(StringTool.getString(
             StringTool.getTimestamp(new Date()), "yyyy/MM/dd"), "yyyy/MM/dd");
         //迄日
         Timestamp endDate = StringTool.getTimestamp("9999/12/31", "yyyy/MM/dd");
         //查询请购日期起日
         this.setValue("QSTART_DATE", startDate);
         //查询请购日期迄日
         this.setValue("QEND_DATE", endDate);
         //预定使用日起日
         this.setValue("QUSERSTART_DATE", startDate);
         //预定使用日迄日
         this.setValue("QUSEEND_DATE", endDate);
         //初始化TABLE1
        this.getTTable(TABLE).setDataStore(getTableTDataStore());
        this.getTTable(TABLE).setDSValue();
     }

     /**
      * 返回TABLE的数据
      * @param tag String  
      * @param queryParm TParm
      * @return TDataStore
      */
     public TDataStore getTableTDataStore() {
         TDataStore dateStore = new TDataStore();         
         String sql = "SELECT * FROM DEV_PURORDERM";
         TParm queryParm = this.getTable1QueryParm();
         String columnName[] = queryParm.getNames();
         if (columnName.length > 0)
             sql += " WHERE ";
         int count = 0;
         for (String temp : columnName) {
             if (temp.equals("QEND_DATE"))
                 continue;
             if (temp.equals("YEND_DATE"))
                 continue;
             //请购日期
             if (temp.equals("QSTART_DATE")) {
                 if (count > 0)
                     sql += " AND ";
                 sql += " PURORDER_DATE BETWEEN TO_DATE('" +
                     queryParm.getValue("QSTART_DATE") +
                     "','YYYYMMDD') AND TO_DATE('" +
                     queryParm.getValue("QEND_DATE") + "','YYYYMMDD')";
                 count++;
                 continue;
             }
             //预使用定日期
             if (temp.equals("YSTART_DATE")) {
                 if (count > 0)
                     sql += " AND ";
                 sql += " RES_DELIVERY_DATE BETWEEN TO_DATE('" +
                     queryParm.getValue("YSTART_DATE") +
                     "','YYYYMMDD') AND TO_DATE('" +
                     queryParm.getValue("YEND_DATE") + "','YYYYMMDD')";
                 count++;
                 continue;
             }
             if (count > 0)
                 sql += " AND ";
             sql += temp + "='" + queryParm.getValue(temp) + "' ";
             count++;
         }  
         dateStore.setSQL(sql);
         dateStore.retrieve();
         return dateStore;
     }
 
     /**
      * 拿到TABLE1的查询条件
      * @return TParm
      */
     public TParm getTable1QueryParm() {
         TParm result = new TParm();
         String startDate = StringTool.getString( (Timestamp)this.getValue(
             "QSTART_DATE"), "yyyyMMdd");
         String endDate = StringTool.getString( (Timestamp)this.getValue(
             "QEND_DATE"), "yyyyMMdd");
         String deptCode = this.getValueString("PURORDER_DEPT");
         String operator = this.getValueString("PURORDER_USER");
         String reteoptro = this.getValueString("RATEOFPRO_CODE");
         String purOrderNo = this.getValueString("PURORDER_NO");
         String ydDateStart = StringTool.getString( (Timestamp)this.getValue(
             "QUSERSTART_DATE"), "yyyyMMdd");
         String ydDateEnd = StringTool.getString( (Timestamp)this.getValue(
             "QUSEEND_DATE"), "yyyyMMdd");
         String fundsouCode = this.getValueString("FUNDSOU_CODE");
         if (startDate.length() != 0)
             result.setData("QSTART_DATE", startDate);
         if (endDate.length() != 0)
             result.setData("QEND_DATE", endDate);
         if (ydDateStart.length() != 0)
             result.setData("YSTART_DATE", ydDateStart);
         if (ydDateEnd.length() != 0)
             result.setData("YEND_DATE", ydDateEnd);
         if (deptCode.length() != 0)
             result.setData("PURORDER_DEPT", deptCode);
         if (operator.length() != 0)
             result.setData("PURORDER_USER", operator);
         if (reteoptro.length() != 0)
             result.setData("RATEOFPRO_CODE", reteoptro);
         if (purOrderNo.length() != 0)
             result.setData("PURORDER_NO", purOrderNo);
         if (fundsouCode.length() != 0)
             result.setData("FUNDSOU_CODE", fundsouCode);
         return result;
     }
     public void onClear(){
         this.clearValue("PURORDER_NO;FUNDSOU_CODE");
         this.onInit();
     }
     /**
      * 生成验收单  
      */  
     public void onGenerateReceipt(){
         int row = this.getTTable(TABLE).getSelectedRow();
         if(row<0){
             this.messageBox("请选择验收单！");
             return;
         }
         TParm parm = this.getTTable(TABLE).getDataStore().getRowParm(row);
         this.setReturnValue(parm);
         this.closeWindow();
     }

     /**
      * 查询  
      */   
     public void onQuery(){
         //初始化TABLE1
         this.getTTable(TABLE).setDataStore(getTableTDataStore());
         this.getTTable(TABLE).setDSValue();
     }

     public TTable getTTable(String tag) {
         return (TTable)this.getComponent(tag);
     }
}
