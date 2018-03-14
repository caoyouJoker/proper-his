package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import com.dongyang.data.TParm;

/**
 * <p>Title: 统一编码厂商注册</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author Miracle
 * @version 1.0
 */
public class SysIoInfControl extends TControl {
    /**
     * TABLE
     */
    private static String TABLE="TABLE1";
    /**
     *  初始化方法
     */
    public void onInit(){
        super.onInit();
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
        String sql = "SELECT * FROM SYS_IO_INF ";
        //厂商代码
        String supCode = this.getValueString("IO_CODE");
        //厂商名称
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
        //System.out.println("SQL=="+sql);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        return dataStore;
    }
    /**
     * 新增
     */
    public void onNewData(){
        if(this.getValueString("IO_CODE").length()==0){
            this.messageBox("请输入厂商编码！");
            return;
        }
        int row = this.getTTable(TABLE).addRow();
        this.getTTable(TABLE).getDataStore().setItem(row,"IO_CODE",this.getValueString("IO_CODE"));
        this.getTTable(TABLE).getDataStore().setItem(row,"CHN_DESC",this.getValueString("CHN_DESC"));
        this.getTTable(TABLE).getDataStore().setItem(row,"ENG_DESC",this.getValueString("ENG_DESC"));
        this.getTTable(TABLE).getDataStore().setItem(row,"PY1",this.getValueString("PY1"));
        this.getTTable(TABLE).getDataStore().setItem(row,"PY2",this.getValueString("PY2"));
        this.getTTable(TABLE).getDataStore().setItem(row,"CONTACTS_NAME",this.getValueString("CONTACTS_NAME"));
        this.getTTable(TABLE).getDataStore().setItem(row,"TEL",this.getValueString("TEL"));
        this.getTTable(TABLE).getDataStore().setItem(row,"E_MAIL",this.getValueString("E_MAIL"));
        this.getTTable(TABLE).getDataStore().setItem(row,"STATUS",this.getValueString("STATUS"));
        this.getTTable(TABLE).getDataStore().setItem(row,"PASSWORD",this.getValueString("PASSWORD"));
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
     * 同步数据基本信息
     */
    public void openTranInf(){
        int selRow  = this.getTTable(TABLE).getSelectedRow();
        TParm selParm = this.getTTable(TABLE).getDataStore().getRowParm(selRow);
        this.openDialog("%ROOT%\\config\\sys\\SYSIOTABLEUI.x",selParm);
    }
    /**
     * 同步LOG信息
     */
    public void openTranLogInf(){
        int selRow  = this.getTTable(TABLE).getSelectedRow();
        TParm selParm = this.getTTable(TABLE).getDataStore().getRowParm(selRow);
        this.openDialog("%ROOT%\\config\\sys\\SYSIOLOGUI.x",selParm);
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
     * 通过审核
     */
    public void onCheckOk(){
        int selRow = this.getTTable(TABLE).getSelectedRow();
        this.getTTable(TABLE).getDataStore().setItem(selRow,"STATUS","ENABLED");
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * 取消审核
     */
    public void onCheckNo(){
        int selRow = this.getTTable(TABLE).getSelectedRow();
        this.getTTable(TABLE).getDataStore().setItem(selRow,"STATUS","DISABLED");
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
        this.clearText("IO_CODE;CHN_DESC;ENG_DESC;CONTACTS_NAME;E_MAIL;PASSWORD;STATUS;TEL");
        getTTable(TABLE).getDataStore().setFilter("IO_CODE=''");
        getTTable(TABLE).getDataStore().filter();
    }
}
