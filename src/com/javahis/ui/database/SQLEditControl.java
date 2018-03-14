package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.ui.TTabbedPane;
import java.awt.Component;
import com.dongyang.util.RunClass;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTextArea;

/**
 *
 * <p>Title: SQL编辑窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.3.25
 * @version 1.0
 */
public class SQLEditControl extends TControl{
    int x = 10;
    int y = 10;
    /**
     * 面板
     */
    TPanel panel;
    TTable tableInf;
    TTable columnInf;
    TTable whereInf;
    TTabbedPane tab;
    TTextArea sqlText;

    /**
     * 连接的第一个点
     */
    LinkData firstLink;
    /**
     * 初始化
     */
    public void onInit()
    {
        panel = (TPanel)getComponent("Panel");
        tableInf = (TTable)getComponent("Table_TableInf");
        columnInf = (TTable)getComponent("Table_ColumnInf");
        whereInf = (TTable)getComponent("Table_WhereInf");
        tab = (TTabbedPane)getComponent("TAB");
        sqlText = (TTextArea)getComponent("TextArea_SQL");
    }
    /**
     * 选择数据表
     */
    public void onSelectTable()
    {
        openDialog("%ROOT%\\config\\database\\DBTableDialog.x",this);
    }
    /**
     * 打开Table
     * @param tableName String
     * @param tableCn String
     */
    public void openTable(String tableName,String tableCn)
    {
        panel.addItem(tableName,"%ROOT%\\config\\database\\TableView.x",new Object[]{tableName,tableCn,this},false);
        TPanel table = (TPanel)getComponent(tableName);
        table.setLocation(x,y);
        x += 200;
        if(x > 1000)
        {
            x = 10;
            y += 10;
        }
    }
    /**
     * 选中Table
     * @param tablename String
     * @param tableCn String
     */
    public void onAddTable(String tablename,String tableCn)
    {
        openTable(tablename,tableCn);
        TParm parm = tableInf.getParmValue();
        if(parm == null)
        {
            parm = new TParm();
            tableInf.setParmValue(parm);
            parm.setCount(0);
        }
        parm.addData("TABLE_NAME",tablename);
        parm.addData("COMMENTS",tableCn);
        parm.setCount(parm.getCount() + 1);
        tableInf.setParmValue(parm);
    }
    /**
     * 关闭试图
     * @param tableName String
     */
    public void onRemoveTable(String tableName)
    {
        panel.removeItem(tableName);
        TParm parm = tableInf.getParmValue();
        if(parm == null)
            return;
        Vector vector =(Vector)parm.getData("TABLE_NAME");
        if(vector == null)
            return;
        int index = vector.indexOf(tableName);
        if(index == -1)
            return;
        parm.removeRow(index);
        tableInf.setParmValue(parm);
        onRemoveColumn(tableName);
    }
    /**
     * 增加列
     * @param tableName String
     * @param columnName String
     * @param columnCn String
     */
    public void onAddColumn(String tableName,String columnName,String columnCn)
    {
        TParm parm = columnInf.getParmValue();
        if(parm == null)
        {
            parm = new TParm();
            columnInf.setParmValue(parm);
            parm.setCount(0);
        }
        parm.addData("TABLE_NAME",tableName);
        parm.addData("COLUMN_NAME",columnName);
        parm.addData("COMMENTS",columnCn);
        parm.addData("SYNTAX",tableName + "." + columnName);
        parm.setCount(parm.getCount() + 1);
        columnInf.setParmValue(parm);
    }
    /**
     * 删除列
     * @param tableName String
     */
    public void onRemoveColumn(String tableName)
    {
        onRemoveColumn(tableName,"");
    }
    /**
     * 删除列
     * @param tableName String
     * @param columnName String
     */
    public void onRemoveColumn(String tableName,String columnName)
    {
        TParm parm = columnInf.getParmValue();
        if(parm == null)
            return;
        for(int i = parm.getCount() - 1;i >= 0;i--)
        {
            String t = parm.getValue("TABLE_NAME",i);
            if(!t.equals(tableName))
                continue;
            if(columnName != null && columnName.length() > 0)
            {
                String c = parm.getValue("COLUMN_NAME", i);
                if (!c.equals(columnName))
                    continue;
            }
            parm.removeRow(i);
            columnInf.setParmValue(parm);
        }
    }
    /**
     * 设置显示类型
     * @param type int
     */
    public void setTableViewType(int type)
    {
        for(int i = 0;i < panel.getComponentCount();i++)
        {
            Component component = panel.getComponent(i);
            if(component == null || !(component instanceof TPanel))
                continue;
            TPanel p = (TPanel)component;
            TControl control = p.getControl();
            if(control == null)
                continue;
            RunClass.runMethod(control,"viewType",
                               new Object[]{type});

        }
    }
    /**
     * 标签翻页
     */
    public void onTabChanged()
    {
        int index = tab.getSelectedIndex();
        //设置显示类型
        setTableViewType(index);
        if(index == 3)
        {
            sqlText.setText(getSQL());
        }
    }
    /**
     * 得到SQL语句
     * @return String
     */
    public String getSQL()
    {
        String columnSql = getColumnSQL();
        if(columnSql.length() == 0)
            return "";
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ");
        sb.append(columnSql);
        sb.append("\n  FROM ");
        sb.append(getTableSQL());
        String where = getWhereSQL();
        if(where.length() > 0)
        {
            sb.append("\n WHERE ");
            sb.append(where);
        }
        return sb.toString();
    }
    /**
     * 得到列SQL
     * @return String
     */
    public String getColumnSQL()
    {
        StringBuffer sb = new StringBuffer();
        int count = columnInf.getRowCount();
        for(int row = 0;row < count;row++)
        {
            if(sb.length() > 0)
                sb.append(",  ");
            if(row % 5 == 0)
                sb.append("\n      ");
            sb.append(columnInf.getItemString(row,"SYNTAX"));
        }
        return sb.toString();
    }
    /**
     * 得到表SQL
     * @return String
     */
    public String getTableSQL()
    {
        StringBuffer sb = new StringBuffer();
        int count = tableInf.getRowCount();
        for(int row = 0;row < count;row++)
        {
            if(sb.length() > 0)
                sb.append(",  ");
            if(row % 5 == 0)
                sb.append("\n      ");
            sb.append(tableInf.getItemString(row,"TABLE_NAME"));
        }
        return sb.toString();
    }
    /**
     * 得到条件SQL
     * @return String
     */
    public String getWhereSQL()
    {
        StringBuffer sb = new StringBuffer();
        int count = whereInf.getRowCount();
        for(int row = 0;row < count;row++)
        {
            sb.append("\n      ");
            sb.append(whereInf.getItemString(row,"COLUMN1"));
            sb.append(" ");
            sb.append(whereInf.getItemString(row,"S"));
            sb.append(" ");
            sb.append(whereInf.getItemString(row,"COLUMN2"));
            sb.append(" ");
            sb.append(whereInf.getItemString(row,"LINK"));
        }
        return sb.toString();
    }
    /**
     * 连接
     * @param tableName String
     * @param columnName String
     * @param control Object
     */
    public void onSelectLink(String tableName,String columnName,Object control)
    {
        LinkData linkData = new LinkData(tableName,columnName,control);
        //第一次连接
        if(firstLink == null)
        {
            firstLink = linkData;
            return;
        }
        TParm parm = whereInf.getParmValue();
        if(parm == null)
        {
            parm = new TParm();
            whereInf.setParmValue(parm);
            parm.setCount(0);
        }
        if(parm.getCount() > 0)
        {
            String link = parm.getValue("LINK", parm.getCount() - 1);
            if (link == null || link.length() == 0)
                parm.setData("LINK", parm.getCount() - 1, "AND");
        }
        //连接同一个位置
        if(firstLink.equals(linkData))
        {
            parm.addData("COLUMN1",firstLink.tableName + "." + firstLink.columnName);
            parm.addData("S","=");
            parm.addData("COLUMN2","''");
            parm.addData("LINK","");
            parm.setCount(parm.getCount() + 1);
            whereInf.setParmValue(parm);
            //清除连接
            RunClass.runMethod(firstLink.control,"clearLink",new Object[]{});
            firstLink = null;
            return;
        }
        parm.addData("COLUMN1",firstLink.tableName + "." + firstLink.columnName);
        parm.addData("S","=");
        parm.addData("COLUMN2",linkData.tableName + "." + linkData.columnName);
        parm.addData("LINK","");
        parm.setCount(parm.getCount() + 1);
        whereInf.setParmValue(parm);
        //清除连接
        RunClass.runMethod(linkData.control,"clearLink",new Object[]{});
        RunClass.runMethod(firstLink.control,"clearLink",new Object[]{});
        firstLink = null;
    }
    class LinkData
    {
        /**
         * 表名
         */
        String tableName;
        /**
         * 列名
         */
        String columnName;
        /**
         * 控制类
         */
        Object control;
        /**
         * 构造器
         * @param tableName String
         * @param columnName String
         * @param control Object
         */
        public LinkData(String tableName,String columnName,Object control)
        {
            this.tableName = tableName;
            this.columnName = columnName;
            this.control = control;
        }
        /**
         * 相同比较
         * @param obj Object
         * @return boolean
         */
        public boolean equals(Object obj)
        {
            if(obj == null || !(obj instanceof LinkData))
                return false;
            LinkData d = (LinkData)obj;
            if(!tableName.equals(d.tableName) ||
               !columnName.equals(d.columnName) ||
               control != d.control)
                return false;
            return true;
        }
    }
    /**
     * 表弹出菜单
     */
    public void onColumnPopupMenu()
    {
        columnInf.setPopupMenuSyntax("上移,U,onUpColumn;" +
                                     "下移,P,onDownColumn;|;" +
                                     "增加自定义列,C,onAddUserColumn;|;" +
                                     "删除,D,onDeleteColumn");
    }
    /**
     * 删除列
     */
    public void onDeleteColumn()
    {
        int row = columnInf.getSelectedRow();
        if(row < 0)
            return;
        String tableName = (String)columnInf.getValueAt(row,0);
        String columnName = (String)columnInf.getValueAt(row,1);
        columnInf.removeRow(row);
        if(tableName == null || tableName.length() == 0)
            return;

        TComponent component = panel.findItem(tableName);
        if(component == null)
            return;
        component.callFunction("clearSelected",columnName);
    }
    /**
     * 增加自定义列
     */
    public void onAddUserColumn()
    {
        TParm parm = new TParm();
        parm.setData("TABLE_NAME","");
        parm.setData("COLUMN_NAME","自定义");
        parm.setData("COMMENTS","");
        parm.setData("SYNTAX","");
        columnInf.addRow(parm);
    }
    /**
     * 上移
     */
    public void onUpColumn()
    {
        int row = columnInf.getSelectedRow();
        if(row <= 0)
            return;
        columnInf.sRowItem(row - 1,row);
        columnInf.setSelectedRow(row - 1);
    }
    /**
     * 下移
     */
    public void onDownColumn()
    {
        int row = columnInf.getSelectedRow();
        if(row > columnInf.getRowCount() - 2)
            return;
        columnInf.sRowItem(row,row + 1);
        columnInf.setSelectedRow(row + 1);
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        String sql = getSQL();
        if(sql.length() > 0)
        {
            int count = columnInf.getRowCount();
            setReturnValue(new Object[]{sql,count});
        }
        return true;
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.runDialog("database\\SQLEdit.x");
    }
}
