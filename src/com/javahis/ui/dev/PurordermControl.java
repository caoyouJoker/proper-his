package com.javahis.ui.dev;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 订购单</p>
 *
 * <p>Description:订购单 </p> 
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PurordermControl extends TControl {
    /**
     * TABLE
     */
    private static String TABLE = "TABLE";
    /**
     * 权限代码
     */
    private String popedemType="1";
    public void onInit() {
        this.initPage();
        this.initPopedem();
        this.initEven();
    }
    public void initPopedem(){
        Object obj = this.getParameter();
        if(obj!=null){
            popedemType = ((TParm)obj).getValue("POPEDEM");
        }
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
        this.setValue("REQUEST_NO",parm.getData("REQUEST_NO"));
        this.setValue("REQUEST_DEPT", parm.getData("REQUEST_DEPT"));
        this.setValue("REQUEST_USER", parm.getData("REQUEST_USER"));
        this.setValue("RATEOFPRO_CODE", parm.getData("RATEOFPRO_CODE"));
        this.setValue("FUNDSOU_CODE", parm.getData("FUNDSOU_CODE"));
        this.setValue("PURTYPE_CODE", parm.getData("PURTYPE_CODE"));
        this.setValue("DEVUSE_CODE", parm.getData("DEVUSE_CODE"));
    }
    public void initPage() {
        //科室、人员
        this.setValue("REQUEST_DEPT", Operator.getDept());
        this.setValue("REQUEST_USER", Operator.getID());
        //请购进度
        this.setValue("RATEOFPRO_CODE", "B");
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
        String sql = "SELECT * FROM DEV_PURCHASEM";  
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
                sql += " REQUEST_DATE BETWEEN TO_DATE('" +
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
                sql += " USE_DATE BETWEEN TO_DATE('" +
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
        System.out.println("sql:"+sql);
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
        String deptCode = this.getValueString("REQUEST_DEPT");
        String operator = this.getValueString("REQUEST_USER");
        String reteoptro = this.getValueString("RATEOFPRO_CODE");
        String requestNo = this.getValueString("REQUEST_NO");
        String ydDateStart = StringTool.getString( (Timestamp)this.getValue(
            "QUSERSTART_DATE"), "yyyyMMdd");
        String ydDateEnd = StringTool.getString( (Timestamp)this.getValue(
            "QUSEEND_DATE"), "yyyyMMdd");
        String fundsouCode = this.getValueString("FUNDSOU_CODE");
        String purtypeCode = this.getValueString("PURTYPE_CODE");
        String devUseCode = this.getValueString("DEVUSE_CODE");
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
    public void onClear(){
        this.clearValue("REQUEST_NO;FUNDSOU_CODE;PURTYPE_CODE;DEVUSE_CODE");
        this.onInit();
    }
    /**
     * 生成订购单  
     */
    public void onGenerateReceipt(){
        int row = this.getTTable(TABLE).getSelectedRow();
        if(row<0){
            this.messageBox("请选择请购单！");
            return;
        }
        TParm parm = this.getTTable(TABLE).getDataStore().getRowParm(row);
        String requestNo = parm.getValue("REQUEST_NO");
        //其他信息
        TParm result = getNegPriceData(requestNo);
        if(result.getErrCode()==-1){
            this.messageBox(parm.getErrText());
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        if(result.getErrCode()==-2){
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        if(result==null){
            this.setReturnValue(null);
            this.closeWindow();
            return;
        }
        parm.setData("SUP_CODE",result.getData("SUP_CODE"));
        parm.setData("SUP_SALES1",result.getData("SUP_SALES1"));
        parm.setData("SUP_SALES1_TEL",result.getData("SUP_SALES1_TEL"));
        parm.setData("TOT_AMT",result.getData("TOT_AMT"));
        parm.setData("PAYMENT_TERMS",result.getData("PAYMENT_TERMS"));
        this.setReturnValue(parm);
        this.closeWindow();
    }
    /**
     * 拿到议价明细
     * @param requestNo String
     * @return TParm
     */
    public TParm getNegPriceData(String requestNo){
        TParm parm = new TParm(this.getDBTool().select("SELECT * FROM DEV_NEGPRICE WHERE REQUEST_NO='"+requestNo+"'"));
        if(parm.getCount()<=0){
           parm.setErrCode(-1);
           parm.setErrText("此请购单没有议价明细！");
           return parm;
        }
        parm.setData("POPEDEM",popedemType);
        Object obj = this.openDialog("%ROOT%\\config\\dev\\SUPUI.x",parm);
        TParm returnParm = new TParm();
        if(obj!=null){
            returnParm = (TParm)obj;
            String supCode = returnParm.getValue("SUP_CODE");
            TParm action = onSupCodeChick(supCode);
            String supSale = action.getValue("SUP_SALES1",0);
            String supSaleTel = action.getValue("SUP_SALES1_TEL",0);
            returnParm.setData("SUP_CODE",supCode);
            returnParm.setData("SUP_SALES1",supSale);
            returnParm.setData("SUP_SALES1_TEL",supSaleTel);
        }
        if(obj==null){
            returnParm.setErrCode(-2);
        }
        return returnParm;
    }
    /**
     * 供应厂商下拉事件
     */
    public TParm onSupCodeChick(String supCode) {
        TParm parm = new TParm(this.getDBTool().select("SELECT SUP_SALES1,SUP_SALES1_TEL,SUP_SALES1_EMAIL,ADDRESS FROM SYS_SUPPLIER WHERE SUP_CODE='" +
            supCode + "'"));
        return parm;
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
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
