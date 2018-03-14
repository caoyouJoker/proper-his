package com.javahis.ui.database;

import com.dongyang.ui.TTextField;
import java.awt.event.KeyEvent;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.control.TControl;
import com.dongyang.tui.text.image.GBlock;

public class GBlockSingleChoosePopMenuControl extends TControl{
    /**
     * 单选文本对象
     */
    private GBlock block;
    private TTable table;
    private TTextField value;
    /**
     * 初始化
     */
    public void onInit()
    {
        block = (GBlock) getParameter();
        if (block == null)
            return;
        table = (TTable)getComponent("TABLE");
        table.setValue(block.getData());
        String text = block.getText();
        value = (TTextField)getComponent("VALUE");
        value.setText(text);
        for(int i = 0;i < table.getRowCount();i++)
            if(text.equals(table.getValueAt(i,0)))
            {
                table.setSelectedRow(i);
                break;
            }
        table.setSelectedColumn(0);
        callFunction("UI|VALUE|addEventListener","VALUE->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
    }
    /**
     * 按键事件
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            closeWindow();
            return;
        }
        int count = table.getRowCount();
        if(count <= 0)
            return;
        switch(e.getKeyCode()){
        case KeyEvent.VK_UP:
            int row = table.getSelectedRow() - 1;
            if(row < 0)
                row = 0;
            table.setSelectedRow(row);
            value.setText((String)table.getValueAt(row,0));
            value.selectAll();
            break;
        case KeyEvent.VK_DOWN:
            row = table.getSelectedRow() + 1;
            if(row >= count)
                row = count - 1;
            table.setSelectedRow(row);
            value.setText((String)table.getValueAt(row,0));
            value.selectAll();
            break;
        case KeyEvent.VK_ENTER:
            onSelect();
            break;
        default:
            table.clearSelection();
            break;
        }
    }
    /**
     * 选中
     */
    public void onSelect()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            block.setText(value.getText());
        else
            block.setText((String)table.getValueAt(row,0));
        block.update();
        closeWindow();
    }
    /**
     * 确定
     */
    public void onOK()
    {
        onSelect();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
}
