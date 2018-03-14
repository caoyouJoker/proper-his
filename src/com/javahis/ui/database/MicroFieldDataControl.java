package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTableNode;
import java.awt.event.KeyEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import java.util.Vector;
import com.dongyang.util.StringTool;
import com.dongyang.tui.text.MMicroField;
import com.dongyang.tui.text.EHasChoose;

public class MicroFieldDataControl extends TControl{
    /**
     * 宏管理器
     */
    private MMicroField microField;
    private TTable table;
    /**
     * 初始化
     */
    public void onInit()
    {
        microField = (MMicroField) getParameter();
        if (microField == null)
            return;
        table = (TTable)getComponent("TABLE");
        table.setValue(microField.getVector());
        if(table.getRowCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * 新增
     */
    public void onNew()
    {
        Vector v = new Vector();
        v.add("");
        v.add("");
        table.insertRowValue(table.getRowCount(),v);
        table.setSelectedRow(table.getRowCount() - 1);
    }
    /**
     * 插入
     */
    public void onInsert()
    {
        int row = table.getSelectedRow();
        if(row == -1)
        {
            onNew();
            return;
        }
        Vector v = new Vector();
        v.add("");
        v.add("");
        table.insertRowValue(row,v);
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        table.removeRow(row);
        if(table.getRowCount() == 0)
            return;
        if(row >= table.getRowCount())
            row = table.getRowCount() - 1;
        table.setSelectedRow(row);
    }
    /**
     * 上移
     */
    public void onUp()
    {
        int row = table.getSelectedRow();
        if(row <= 0)
            return;
        Vector v = new Vector();
        v.add(table.getValueAt(row - 1,0));
        v.add(table.getValueAt(row - 1,1));
        table.insertRowValue(row + 1,v);
        table.removeRow(row - 1);
        table.setSelectedRow(row - 1);
    }
    /**
     * 下移
     */
    public void onDown()
    {
        int row = table.getSelectedRow();
        if(row == -1 || row == table.getRowCount() - 1)
            return;
        Vector v = new Vector();
        v.add(table.getValueAt(row + 1,0));
        v.add(table.getValueAt(row + 1,1));
        table.removeRow(row + 1);
        table.insertRowValue(row,v);
        table.setSelectedRow(row + 1);
    }
    /**
     * 确定
     */
    public void onOK()
    {
        table.acceptText();
        microField.setVector(table.getValue());
        setReturnValue("OK");
        closeWindow();
    }
}
