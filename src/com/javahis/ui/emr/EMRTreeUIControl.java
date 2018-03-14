package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import java.util.Date;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;

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
public class EMRTreeUIControl extends TControl {
    private static final String TABLE = "TABLE";
    private static final String tabSql = "SELECT * FROM EMR_TREE";
    public void onInit(){
        super.onInit();
        TDataStore tabDataStore = new TDataStore();
        tabDataStore.setSQL(tabSql);
        tabDataStore.retrieve();
        this.getTTable(TABLE).setDataStore(tabDataStore);
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * 查询
     */
    public void onQuery(){
        this.getTTable(TABLE).setFilter(getFilterStr());
        this.getTTable(TABLE).filter();
        this.getTTable(TABLE).setDSValue();
    }
    /**
     * 拿到过滤条件
     * @return String
     */
    public String getFilterStr(){
        String filterStr = "";
        int andCount = 0;
        if(this.getValueString("SYSTEM_CODE").length()!=0){
            filterStr+=" SYSTEM_CODE='"+this.getValueString("SYSTEM_CODE")+"' ";
            andCount++;
        }
        if(this.getValueString("CLASS_CODE").length()!=0){
            if(andCount==1){
                filterStr+=" AND ";
            }
            filterStr+=" CLASS_CODE='"+this.getValueString("CLASS_CODE")+"' ";
            andCount++;
        }
        if(andCount>0)
             filterStr+=" AND ";
        String emrFlg = this.getTCheckBox("EMR_FLG").isSelected()?"Y":"N";
        filterStr+=" EMR_FLG='"+emrFlg+"' ";

        if(this.getValueString("SYS_PROGRAM").length()!=0){
            if(andCount==1||andCount==2||andCount==3){
                filterStr+=" AND ";
            }
            filterStr+=" SYS_PROGRAM='"+this.getValueString("SYS_PROGRAM")+"' ";
        }
        return filterStr;
    }
    /**
     * 得到TCHECKBOX对象
     * @param tag String
     * @return TCheckBox
     */
    public TCheckBox getTCheckBox(String tag){
        return (TCheckBox)this.getComponent(tag);
    }
    /**
     * 添加
     */
    public void onAdd(){
        int rowID = this.getTTable(TABLE).addRow();
        TParm parm = new TParm(this.getDBTool().select("SELECT MAX(SEQ)+1 AS SEQ FROM EMR_TREE"));
        int seqNo = 0;
        if(parm.getCount("SEQ")<=0){
            seqNo = 1;
        }else{
            seqNo = parm.getInt("SEQ",0);
        }
        this.getTTable(TABLE).getDataStore().setItem(rowID,"OPT_USER",Operator.getID());
        this.getTTable(TABLE).getDataStore().setItem(rowID,"OPT_DATE",StringTool.getTimestamp(new Date()));
        this.getTTable(TABLE).getDataStore().setItem(rowID,"OPT_TERM",Operator.getIP());
        this.getTTable(TABLE).getDataStore().setItem(rowID,"SEQ",seqNo);
    }
    /**
     * 清空
     */
    public void onClear(){
        this.getTCheckBox("EMR_FLG").setSelected(true);
        this.clearValue("SYSTEM_CODE;CLASS_CODE;SYS_PROGRAM");
        this.onQuery();
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }

    /**
     * 保存
     */
    public void onSave(){
        this.getTTable(TABLE).acceptText();
        if(this.getTTable(TABLE).update()){
           this.messageBox_("保存成功");
       }else{
           this.messageBox_("保存失败");
       }

    }
    /**
     * 得到TABLE对象
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 拿到TTextField对象
     * @param tag String
     * @return TTextField
     */
    public TTextField getTTextField(String tag){
        return (TTextField)this.getComponent(tag);
    }
    /**
     * EMR注记勾选事件
     */
    public void onSelectCheckBox(){
        if(this.getTCheckBox("EMR_FLG").isSelected()){
            getTTextField("SYS_PROGRAM").setEnabled(false);
        }else{
            getTTextField("SYS_PROGRAM").setEnabled(true);
        }
    }
}
