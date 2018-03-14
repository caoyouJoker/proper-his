package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.div.MV;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: 创建DIV对话框控制类</p>
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
public class CreateDIVDialogControl extends TControl{
    private MV mv;
    private TTable table;
    /**
     * 初始化
     */
    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
        mv = (MV)getParameter();
        //初始化Table
        initTable();
    }
    /**
     * 初始化Table
     */
    public void initTable()
    {
        TParm parm = mv.getCreateTParm();
        table.setParmValue(parm);
        if(table.getRowCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * 确定
     */
    public void onOK()
    {
        String name = getText("NAME");
        if(name.length() == 0)
        {
            messageBox_("请输入名称!");
            grabFocus("NAME");
            return;
        }
        setReturnValue(new Object[]{table.getSelectedRow() + 1,name});
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
}
