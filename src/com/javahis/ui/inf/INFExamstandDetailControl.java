package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: 科室检测方案检测项目对照 </p>
 *
 * <p>Description: 科室检测方案检测项目对照 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFExamstandDetailControl extends TControl {

    /**
     * 初始化方法
     */
    public void onInit() {
        //设备明细表格编辑事件
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        super.onInit();
    }

    /**
     * 检测标准动作事件
     */
    public void onStand(){
        if(getValueString("EXAMSTAND_CODE").length() == 0){
            getThisTable().removeRowAll();
            getThisTable().getDataStore().resetModify();
            getThisTable().setSQL("");
            getThisTable().retrieve();
            return;
        }
        String SQL = " SELECT EXAMSTAND_CODE,INFITEM_CODE,ITEM_GAINPOINT,"+
                     "        OPT_USER,OPT_DATE,OPT_TERM "+
                     " FROM INF_EXAMSTANDDETAIL "+
                     " WHERE EXAMSTAND_CODE = '"+getValue("EXAMSTAND_CODE")+"'";
        getThisTable().setSQL(SQL);
        getThisTable().retrieve();
    }

    /**
     * 取得界面表格控件
     * @param tag String
     * @return TTable
     */
    public TTable getThisTable(){
        return (TTable)getComponent("TABLE");
    }

    /**
     * 新增方法
     */
    public void onNew(){
        if(getValueString("EXAMSTAND_CODE").length() == 0){
            messageBox("请选择方案");
            return;
        }
        getThisTable().addRow();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        getThisTable().setItem(getThisTable().getRowCount() - 1,
                                              "EXAMSTAND_CODE",
                                              getValueString("EXAMSTAND_CODE"));
        getThisTable().setItem(getThisTable().getRowCount() - 1,"OPT_USER",Operator.getID());
        getThisTable().setItem(getThisTable().getRowCount() - 1,"OPT_TERM",Operator.getIP());
        getThisTable().setItem(getThisTable().getRowCount() - 1,"OPT_DATE",timestamp);

    }

    /**
     * 删除方法
     */
    public void onDelete(){
        int row = getThisTable().getSelectedRow();
        if(row < 0)
            return;
        getThisTable().removeRow(row);
    }

    /**
     * 保存检核
     * @return boolean
     */
    private boolean onSaveCheck(){
        double tot = 0;
        //全部删除
        if(getThisTable().getRowCount() <= 0)
            return false;
        for(int i = 0;i<getThisTable().getRowCount();i++){
            if(getThisTable().getItemData(i,"INFITEM_CODE") == null ||
               getThisTable().getItemString(i,"INFITEM_CODE").length() == 0){
                messageBox("第"+(i+1)+"行项目编码不可为空");
                return true;
            }
            if(getThisTable().getItemData(i,"ITEM_GAINPOINT") == null ||
               getThisTable().getItemString(i,"ITEM_GAINPOINT").length() == 0||
               getThisTable().getItemDouble(i,"ITEM_GAINPOINT") == 0){
                messageBox("第"+(i+1)+"行监测单项评比不可为空");
                return true;
            }
            tot += getThisTable().getItemDouble(i,"ITEM_GAINPOINT");
        }
        if(tot != 100){
            messageBox("检测单项评比综合不可不等于100");
            return true;
        }
        return false;
    }

    /**
     * 设置操作人员操作时间操作端末
     */
    private void setOptData(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        for(int i = 0;i<getThisTable().getRowCount();i++){
            getThisTable().setItem(i,"OPT_USER",Operator.getID());
            getThisTable().setItem(i,"OPT_TERM",Operator.getIP());
            getThisTable().setItem(i,"OPT_DATE",timestamp);
        }
    }

    /**
     * 保存方法
     */
    public void onSave(){
        getThisTable().acceptText();
        if(onSaveCheck())
            return;
        setOptData();
        if(getThisTable().getDataStore().update()){
            messageBox("保存成功");
            return;
        }
        messageBox("保存失败");
    }

    /**
     * 检测项目触发事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode) obj;
        if(node.getColumn() != 1)
            return false;
        for(int i = 0;i < getThisTable().getRowCount();i++){
           if(node.getRow() == i)
               continue;
           if(!node.getValue().equals(getThisTable().getItemData(i,"INFITEM_CODE")))
               continue;
           return true;
        }
        return false;
    }


    /**
     * 清空方法
     */
    public void onCLear(){
        setValue("EXAMSTAND_CODE","");
        getThisTable().removeRowAll();
        getThisTable().getDataStore().resetModify();
        getThisTable().setSQL("");
        getThisTable().retrieve();
    }
}
