package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.util.RunClass;

/**
 *
 * <p>Title: 数据库表选取窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.3.26
 * @version 1.0
 */
public class DBTableDialogControl extends TControl{
    /**
     * Table控件
     */
    TTable table;
    /**
     * 父类
     */
    Object parent;
    /**
     * 数据库工具
     */
    TJDODBTool dbTool = TJDODBTool.getInstance();
    /**
     * 初始化
     */
    public void onInit()
    {
        //得到传参
        parent = getParameter();
        //得到Table
        table = (TTable)getComponent("Table");
        //初始化Table
        initTable();
    }
    /**
     * 初始化Table
     */
    public void initTable()
    {
        TParm parm = new TParm(dbTool.getTablesAndComments());
        if(parm == null || parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        table.setParmValue(parm);
        if(parm.getCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * Table双击
     */
    public void onTableDoubleClicked()
    {
        if(parent == null)
            return;
        int row = table.getSelectedRow();
        if(row < 0)
            return;
        String tableName = (String)table.getValueAt(row,0);
        String tableCn = (String)table.getValueAt(row,1);
        //泛行调用控制类
        RunClass.runMethod(parent,"onAddTable",new Object[]{tableName,tableCn});
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.runDialog("database\\DBTableDialog.x");
    }
}
