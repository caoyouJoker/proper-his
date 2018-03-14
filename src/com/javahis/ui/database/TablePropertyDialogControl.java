package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.ETable;
import com.dongyang.ui.TTextArea;
import com.dongyang.tui.text.ui.CTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.tui.text.EPanel;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 表格属性对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.5
 * @version 1.0
 */
public class TablePropertyDialogControl extends TControl{
    private ETable table;
    private CTable cTable;
    private EPanel panel;
    private TCheckBox hasSum;
    private TTextArea sqlArea;
    private TCheckBox showBorder2;
    private TCheckBox showBorder;
    private TCheckBox showWLine;
    private TCheckBox showHLine;
    private TCheckBox inputData;
    /**
     * 初始化
     */
    public void onInit()
    {
        sqlArea = (TTextArea)getComponent("SQL");
        hasSum = (TCheckBox)getComponent("SUM_CHECKBOX");
        showBorder2 = (TCheckBox)getComponent("SHOW_BORDER2");
        showBorder = (TCheckBox)getComponent("SHOW_BORDER");
        showWLine = (TCheckBox)getComponent("SHOW_W_LINE");
        showHLine = (TCheckBox)getComponent("SHOW_H_LINE");
        inputData = (TCheckBox)getComponent("INPUT_DATA");

        table = (ETable)getParameter();
        if(table == null)
            return;
        panel = table.getPanel();
        if(panel != null)
        {
            setValue("PF",panel.getParagraphForward());
            setValue("PA",panel.getParagraphAfter());
        }
        table = table.getHeadTable();
        cTable = table.getCTable();
        showBorder2.setSelected(table.isShowBorder2());
        showBorder.setSelected(table.isShowBorder());
        showWLine.setSelected(table.isShowWLine());
        showHLine.setSelected(table.isShowHLine());
        setValue("I_TOP",table.getInsets().top);
        setValue("I_BOTTOM",table.getInsets().bottom);
        setValue("I_LEFT",table.getInsets().left);
        setValue("I_RIGHT",table.getInsets().right);
        setValue("R_TOP",table.getTRInsets().top);
        setValue("R_BOTTOM",table.getTRInsets().bottom);
        setValue("R_LEFT",table.getTRInsets().left);
        setValue("R_RIGHT",table.getTRInsets().right);
        setValue("D_TOP",table.getTDInsets().top);
        setValue("D_BOTTOM",table.getTDInsets().bottom);
        setValue("D_LEFT",table.getTDInsets().left);
        setValue("D_RIGHT",table.getTDInsets().right);
        setValue("W_SPACE",table.getWSpace());
        setValue("H_SPACE",table.getHSpace());

        if(cTable != null)
        {
            sqlArea.setText(cTable.getSQL());
            hasSum.setSelected(cTable.hasSum());
            setText("TABLE_ID",cTable.getTableID());
            inputData.setSelected(cTable.isInputData());
        }else
            setText("TABLE_ID",table.getName());

    }
    /**
     * 分组选择
     */
    public void onGroup()
    {

    }
    /**
     * 确定
     */
    public void onOK()
    {
        ETable t = table;
        if(t == null)
            return;
        while(t != null)
        {
            t.setShowBorder2(showBorder2.isSelected());
            t.setShowBorder(showBorder.isSelected());
            t.setShowWLine(showWLine.isSelected());
            t.setShowHLine(showHLine.isSelected());
            t.getInsets().top = TypeTool.getInt(getValue("I_TOP"));
            t.getInsets().bottom = TypeTool.getInt(getValue("I_BOTTOM"));
            t.getInsets().left = TypeTool.getInt(getValue("I_LEFT"));
            t.getInsets().right = TypeTool.getInt(getValue("I_RIGHT"));
            t.getTRInsets().top = TypeTool.getInt(getValue("R_TOP"));
            t.getTRInsets().bottom = TypeTool.getInt(getValue("R_BOTTOM"));
            t.getTRInsets().left = TypeTool.getInt(getValue("R_LEFT"));
            t.getTRInsets().right = TypeTool.getInt(getValue("R_RIGHT"));
            t.getTDInsets().top = TypeTool.getInt(getValue("D_TOP"));
            t.getTDInsets().bottom = TypeTool.getInt(getValue("D_BOTTOM"));
            t.getTDInsets().left = TypeTool.getInt(getValue("D_LEFT"));
            t.getTDInsets().right = TypeTool.getInt(getValue("D_RIGHT"));

            t.setWSpace(TypeTool.getInt(getValue("W_SPACE")));
            t.setHSpace(TypeTool.getInt(getValue("H_SPACE")));
            t.setModify(true);
            EPanel p = t.getPanel();
            if(p != null)
            {
                p.setParagraphForward(TypeTool.getInt(getValue("PF")));
                p.setParagraphAfter(TypeTool.getInt(getValue("PA")));
            }
            t = t.getNextTable();
        }
        if(cTable != null)
        {
            cTable.setSQL(sqlArea.getText());
            cTable.setHasSum(hasSum.isSelected());
            cTable.setTableID(getText("TABLE_ID"));
            cTable.setInputData(inputData.isSelected());
        }else
            table.setName(getText("TABLE_ID"));
        table.getFocusManager().update();
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
