package com.javahis.ui.sys;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TCheckBox;

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
public class SYSIOSHARETABLEControl extends TControl {
    /**
     * TABLE
     */
    private static String TABLE="TABLE1";
    /**
     *  初始化方法
     */
    public void onInit(){
        onQuery();
    }
    /**
     * 查询
     */
    public void onQuery(){
        this.getTTable(TABLE).setDataStore(getSQL());
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * 拿到SQL
     * @return String
     */
    public TDataStore getSQL(){
        String sql = "SELECT * FROM SYS_IO_SHARETABLE ";
        //表名代码
        String supCode = this.getValueString("IO_CODE");
        //表名说明
        String tableDesc = this.getValueString("TABLE_DESC");
        //中文名称
        String supName = this.getValueString("CHN_DESC");
        //英文名称
        String engName = this.getValueString("ENG_DESC");
        int whereCount=0;
        if(supCode.length()>0){
            sql+="WHERE IO_CODE LIKE '%"+supCode+"%'";
            whereCount++;
        }
        if(supName.length()>0){
            if(whereCount>0)
                sql+=" AND ";
            if(whereCount==0)
                sql+="WHERE ";
            sql+="CHN_DESC LIKE '%"+supName+"%'";
        }
        if(engName.length()>0){
            if(whereCount>0)
                sql+=" AND ";
            if(whereCount==0)
                sql+="WHERE ";
            sql+="ENG_DESC LIKE '%"+engName+"%'";
        }
        if(tableDesc.length()>0){
            if(whereCount>0)
                sql+=" AND ";
            if(whereCount==0)
                sql+="WHERE ";
            sql+="TABLE_DESC LIKE '%"+tableDesc+"%'";
        }
//        System.out.println("SQL=="+sql);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        return dataStore;
    }
    /**
     * 新增
     */
    public void onNewData(){
        if(!this.emptyTextCheck("TABLE_NAME,TABLE_DESC,CHN_DESC,ENG_DESC,PY1,PY2")){
            return;
        }
        int row = this.getTTable(TABLE).addRow();
        this.getTTable(TABLE).getDataStore().setItem(row,"TABLE_NAME",this.getValueString("TABLE_NAME"));
        this.getTTable(TABLE).getDataStore().setItem(row,"TABLE_DESC",this.getValueString("TABLE_DESC"));
        this.getTTable(TABLE).getDataStore().setItem(row,"CHN_DESC",this.getValueString("CHN_DESC"));
        this.getTTable(TABLE).getDataStore().setItem(row,"ENG_DESC",this.getValueString("ENG_DESC"));
        this.getTTable(TABLE).getDataStore().setItem(row,"PY1",this.getValueString("PY1"));
        this.getTTable(TABLE).getDataStore().setItem(row,"PY2",this.getValueString("PY2"));
        this.getTTable(TABLE).getDataStore().setItem(row,"READ",this.getValueString("READ"));
        this.getTTable(TABLE).getDataStore().setItem(row,"WRITE",this.getValueString("WRITE"));
        this.getTTable(TABLE).getDataStore().setItem(row,"LISTEN",this.getValueString("LISTEN"));
        this.getTTable(TABLE).getDataStore().setItem(row,"OPT_USER",Operator.getID());
        this.getTTable(TABLE).getDataStore().setItem(row,"OPT_DATE",SystemTool.getInstance().getDate());
        this.getTTable(TABLE).getDataStore().setItem(row,"OPT_TERM",Operator.getIP());
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * 右击MENU弹出事件
     * @param tableName
     */
    public void showPopMenu(String tableName){
        this.getTTable(tableName).setPopupMenuSyntax("同步数据基本信息,openTranInf;同步数据LOG信息,openTranLogInf");
    }
    /**
     * 拼音
     */
    public void onPy(Object obj){
        if("CHN_DESC".equals(obj)){
            String py1 = StringUtil.onCode(this.getValueString("CHN_DESC"));
            this.setValue("PY1",py1);
        }
        if("ENG_DESC".equals(obj)){
            String py2 = StringUtil.onCode(this.getValueString("ENG_DESC"));
            this.setValue("PY2", py2);
        }
    }
    /**
     * 保存
     */
    public void onSave(){
        if(!this.getTTable(TABLE).update())
            this.messageBox("保存失败！");
        else
            this.messageBox("保存成功！");
    }
    /**
     * 删除
     */
    public void onDelete(){
        int selRow = this.getTTable(TABLE).getSelectedRow();
        this.getTTable(TABLE).getDataStore().deleteRow(selRow);
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearText("TABLE_NAME;TABLE_DESC;CHN_DESC;ENG_DESC;PY1;PY2");
        ((TCheckBox)this.getComponent("READ")).setSelected(false);
        ((TCheckBox)this.getComponent("WRITE")).setSelected(false);
        ((TCheckBox)this.getComponent("LISTEN")).setSelected(false);
        getTTable(TABLE).getDataStore().setFilter("TABLE_NAME=''");
        getTTable(TABLE).getDataStore().filter();
    }

}
