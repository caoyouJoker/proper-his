package com.javahis.ui.database;

import com.dongyang.control.TControl;
import java.util.Vector;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;

/**
 *
 * <p>Title: 有无选择文本属性对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author lzk 2009.8.28
 * @version 1.0
 */
public class HasChooseControl extends TControl{
    /**
     * 有无选择文本对象
     */
    private EHasChoose hasChoose;
    private TTable table;
    /**
     * 初始化
     */
    public void onInit()
    {
        hasChoose = (EHasChoose) getParameter();
        if (hasChoose == null)
            return;
        setValue("GROUP_NAME", hasChoose.getGroupName());
        setValue("NAME", hasChoose.getName());
        setValue("TEXT", hasChoose.getText());
        setValue("ALLOW_NULL", hasChoose.isAllowNull() ? "Y" : "N");
        setValue("CHK_ISCDA",hasChoose.isIsDataElements()?"Y" : "N");
        setValue("CHK_LOCKED",hasChoose.isLocked()?"Y" : "N");

        table = (TTable)getComponent("TABLE");
        table.setValue(newVector(hasChoose.getData()));
        if(table.getRowCount() > 0)
            table.setSelectedRow(0);

        /**if (hasChoose.isIsDataElements()) {
            TTextField tf_name = (TTextField) getComponent("NAME");
            TTextField tf_text = (TTextField) getComponent("TEXT");
            TButton btn0 = (TButton) getComponent("tButton_0");
            TButton btn1 = (TButton) getComponent("tButton_1");
            TButton btn2 = (TButton) getComponent("tButton_2");
            TCheckBox ck_text = (TCheckBox) getComponent("ALLOW_NULL");

            tf_name.setEditable(false);
            tf_text.setEditable(false);
            btn0.setEnabled(false);
            btn1.setEnabled(false);
            btn2.setEnabled(false);
            ck_text.setEnabled(false);

        }**/




    }
    /**
     * Vector 克隆
     * @param v Vector
     * @return Vector
     */
    public Vector newVector(Vector v)
    {
        Vector data = new Vector();
        for(int i = 0;i < v.size();i++)
        {
            Vector t = (Vector)v.get(i);
            Vector t1 = new Vector();
            data.add(t1);
            for(int j = 0;j < t.size();j++)
                t1.add(t.get(j));
        }
        return data;
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
        if(getText("TEXT").length() == 0)
        {
            this.messageBox_("文本不能为空!");
            return;
        }

        hasChoose.setGroupName(getText("GROUP_NAME"));
        hasChoose.setName(getText("NAME"));
        hasChoose.setText(getText("TEXT"));
        hasChoose.setModify(true);
        hasChoose.setData(table.getValue());
        if(getValue("CHK_ISCDA").equals("Y")){
            hasChoose.setDataElements(true);
        }else{
             hasChoose.setDataElements(false);
        }
        hasChoose.setLocked(getValueBoolean("CHK_LOCKED"));

        setReturnValue("OK");
        closeWindow();
    }
}
