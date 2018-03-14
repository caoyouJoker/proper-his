package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMovePane;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.RunClass;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * <p>Title: 表示图控制类</p>
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
public class TableViewControl extends TControl{
    /**
     * Table控件
     */
    TTable table;
    /**
     * 移动块
     */
    TMovePane movePane;
    /**
     * 父类
     */
    Object parent;
    /**
     * 表名
     */
    String tableName;
    /**
     * 表中文
     */
    String tableCn;
    /**
     * 数据库工具
     */
    TJDODBTool dbTool = TJDODBTool.getInstance();
    /**
     * 行选中颜色
     */
    Color selectRowColor = new Color(49,106,197);
    /**
     * 行选中字体颜色
     */
    Color selectRowTextColor = new Color(255,255,255);
    /**
     * 列行颜色
     */
    Map columnRowColorMap = new HashMap();
    /**
     * 列字颜色
     */
    Map columnRowTextColorMap = new HashMap();
    /**
     * 连接行颜色
     */
    Map linkRowColorMap = new HashMap();
    /**
     * 连接字颜色
     */
    Map linkRowTextColorMap = new HashMap();
    /**
     * 连接显示类型
     */
    int viewType;
    /**
     * 初始化
     */
    public void onInit()
    {
        //得到移动块
        movePane = (TMovePane)getComponent("MovePane");
        //得到Table
        table = (TTable)getComponent("Table");
        Object[] parm = (Object[])getParameter();
        if(parm != null)
        {
            tableName = (String)parm[0];
            tableCn = (String)parm[1];
            parent = parm[2];
            movePane.setText(tableName);
        }
        //初始化Table
        initTable();
    }
    /**
     * 初始化Table
     */
    public void initTable()
    {
        if(tableName == null || tableName.length() == 0)
            return;
        TParm parm = new TParm(dbTool.getColumnsAndComments(tableName));
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
     * 关闭试图
     */
    public void onTableClose()
    {
        //泛行调用控制类
        RunClass.runMethod(parent,"onRemoveTable",new Object[]{tableName});
    }
    /**
     * 点击行
     */
    public void onClickedRow()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        switch(viewType)
        {
            case 0:
                selectColumn(row);
                break;
            case 2:
                selectLink(row);
                break;
        }
    }
    /**
     * 选择连接
     * @param row int
     */
    public void selectLink(int row)
    {
        table.setRowColor(row, selectRowColor);
        table.setRowTextColor(row, selectRowTextColor);
        table.getTable().repaint();
        RunClass.runMethod(parent,"onSelectLink",
                           new Object[]{tableName,
                           table.getValueAt(row,0),this});
    }
    /**
     * 清除连接
     */
    public void clearLink()
    {
        table.setRowColorMap(new HashMap());
        table.setRowTextColorMap(new HashMap());
        table.getTable().repaint();
    }
    /**
     * 清除选择行
     * @param columnName String
     */
    public void clearSelected(String columnName)
    {
        int row = getFindRow(columnName);
        if(row == -1)
            return;
        table.setRowColor(row,null);
        table.setRowTextColor(row,null);
        table.getTable().repaint();
    }
    /**
     * 查找行号
     * @param columnName String
     * @return int
     */
    public int getFindRow(String columnName)
    {
        TParm parm = table.getParmValue();
        if(parm == null)
            return -1;
        Vector v = (Vector)parm.getData("COLUMN_NAME");
        if(v == null)
            return -1;
        return v.indexOf(columnName);
    }
    /**
     * 选择列
     * @param row int
     */
    public void selectColumn(int row)
    {
        Color color = table.getRowColor(row);
        if(color == null)
        {
            table.setRowColor(row, selectRowColor);
            table.setRowTextColor(row, selectRowTextColor);
            //泛行调用控制类
            RunClass.runMethod(parent,"onAddColumn",
                               new Object[]{tableName,
                               table.getValueAt(row,0),
                               table.getValueAt(row,1)});
        }
        else
        {
            table.removeRowColor(row);
            table.removeRowTextColor(row);
            RunClass.runMethod(parent,"onRemoveColumn",
                               new Object[]{tableName,
                               table.getValueAt(row,0)});
        }
        table.getTable().repaint();
    }
    /**
     * 设置显示类型
     * @param viewType int
     * 0 列
     * 1 表
     * 2 条件
     */
    public void viewType(int viewType)
    {
        if(this.viewType == viewType)
            return;
        switch(this.viewType)
        {
            case 0:
                columnRowColorMap = table.getRowColorMap();
                columnRowTextColorMap = table.getRowTextColorMap();
                break;
            case 2:
                linkRowColorMap = table.getRowColorMap();
                linkRowTextColorMap = table.getRowTextColorMap();
                break;
        }
        switch(viewType)
        {
            case 0:
                table.setRowColorMap(columnRowColorMap);
                table.setRowTextColorMap(columnRowTextColorMap);
                break;
            case 1:
                table.setRowColorMap(new HashMap());
                table.setRowTextColorMap(new HashMap());
            case 2:
                table.setRowColorMap(linkRowColorMap);
                table.setRowTextColorMap(linkRowTextColorMap);
                break;
        }
        this.viewType = viewType;
        table.getTable().repaint();
    }
}
