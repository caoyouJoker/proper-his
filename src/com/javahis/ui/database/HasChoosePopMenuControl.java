package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTableNode;
import java.awt.event.KeyEvent;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import java.util.Vector;
import com.dongyang.ui.TWindow;

/**
 *
 * <p>Title: 有无选择组件填出选择菜单控制类</p>
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
public class HasChoosePopMenuControl extends TControl{
    /**
     * 有无选择文本对象
     */
    private EHasChoose hasChoose;
    private TTable table;
    private TTextField value;
    /**
     * 初始化
     */
    public void onInit()
    {
        hasChoose = (EHasChoose) getParameter();
        if (hasChoose == null)
            return;
        table = (TTable)getComponent("TABLE");
        String text = hasChoose.getText();
        table.setValue(newVector(text,hasChoose.getData()));
        value = (TTextField)getComponent("VALUE");
        value.setText(text);
        table.setSelectedColumn(0);
        value.addEventListener("VALUE->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onChoose");
        addEventListener("TABLE->"+TTableEvent.CHANGE_VALUE,"onTableChangeValue");
    }
    public void onChoose(Object obj)
    {
        table.acceptText();
    }
    public void onTableChangeValue(Object obj)
    {
        TTableNode node=(TTableNode)obj;
        String st = "";
        String sf = "";
        for(int i = 0;i < table.getRowCount();i++)
        {
            boolean b = table.getValueAt(i,0).equals("Y");
            if(i == node.getRow())
                b = "Y".equals(node.getValue());
            if(b)
            {
                if(st.length() > 0)
                    st += "、";
                st += table.getValueAt(i,1);
            }else
            {
                if(sf.length() > 0)
                    sf += "、";
                sf += table.getValueAt(i,1);
            }
        }
        String s = "";
        if(st.length() > 0)
            s += "有" + st;
        if(sf.length() > 0)
        {
            if(s.length() > 0)
                s += "，";
            s += "无" + sf;
        }
        value.setText(s);
    }
    /**
     * Vector 克隆
     * @param s String
     * @param v Vector
     * @return Vector
     */
    public Vector newVector(String s,Vector v)
    {
        Vector data = new Vector();
        String s1[] = StringTool.parseLine(s,"，");
        if(s1[0].startsWith("有"))
            s1 = StringTool.parseLine(s1[0].substring(1),"、");
        else
            s1 = null;
        for(int i = 0;i < v.size();i++)
        {
            Vector t = (Vector)v.get(i);
            Vector t1 = new Vector();
            data.add(t1);
            String name = (String)t.get(0);
            if(inString(name,s1))
                t1.add("Y");
            else
                t1.add("N");
            t1.add(name);
        }
        return data;
    }
    public boolean inString(String s,String s1[])
    {
        if(s1 == null)
            return false;
        for(int i = 0;i < s1.length;i++)
            if(s.equals(s1[i]))
                return true;
        return false;
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
        hasChoose.setText(value.getText());
        hasChoose.getPM().getFocusManager().update();
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
