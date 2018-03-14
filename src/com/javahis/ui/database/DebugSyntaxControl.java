package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.MSyntax;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextArea;
import com.dongyang.tui.text.ESyntaxItem;

/**
 *
 * <p>Title: 调试语法窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.20
 * @version 1.0
 */
public class DebugSyntaxControl extends TControl{
    private MSyntax msyntax;
    private TTable table;
    private TTextArea syntaxText;
    /**
     * 初始化
     */
    public void onInit()
    {
        msyntax = (MSyntax)getParameter();
        table = (TTable)getComponent("TABLE");
        syntaxText = (TTextArea)getComponent("SYNTAX");
        //初始化Table
        initTable();
    }
    /**
     * 初始化Table
     */
    public void initTable()
    {
        if(msyntax == null)
            return;
        TParm parm = new TParm();
        for(int i = 0;i < msyntax.size();i++)
        {
            parm.addData("ID",i);
            parm.addData("NAME",msyntax.get(i).getName());
        }
        parm.setCount(parm.getCount("ID"));
        table.setParmValue(parm);
    }
    /**
     * 点击Table
     */
    public void onClickedTable()
    {
        int row = table.getSelectedRow();
        if(row < 0)
            return;
        ESyntaxItem syntax = msyntax.get(row);
        syntaxText.setText("" + syntax.getSyntax());
        setText("ID","" +row);
        setText("NAME",syntax.getName());
        setValue("TYPE",syntax.getType());
        setValue("CLASSIFY",syntax.getClassify());
        setValue("INPUT_PARM",syntax.getInValue());
        setValue("OUTPUT_PARM",syntax.getOutValue());
        setValue("FUNCTION_NAME",syntax.getFunctionNameValue());
    }
}
