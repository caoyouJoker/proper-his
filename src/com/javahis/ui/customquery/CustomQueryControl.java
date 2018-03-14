package com.javahis.ui.customquery;
//SELECT_TABLE表的数据未显示（column_NAME,ID）

import com.dongyang.control.TControl;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import jdo.sys.CustomQueryTool;
import java.util.Vector;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 *
 * <p>Title: 自定义查询窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.18
 * @version 1.0
 */
public class CustomQueryControl extends TControl{
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表信息
     */
    private TParm tableParm;
    private TTable columnList;
    private TTable whereList;
    private TTable publicTable;
    private TTable privateTable;
    private TRadioButton pubRadio;
    private TRadioButton prvRadio;
    private TCheckBox defaultCheckBox;
    private TTabbedPane tab;
    private int editWhereCode = -1;
    private TComboBox templateCombo;
    /**
     * 初始化参数
     */
    public void onInitParameter()
    {
        Object parm = getParameter();
        if(parm == null || !(parm instanceof Object[]))
            return;
        Object pData[] = (Object[])parm;
        tableName = TypeTool.getString(pData[0]);
    }
    /**
     * 初始化
     */
    public void onInit()
    {
    	templateCombo=(TComboBox)getComponent("TEMP_COMBO");
    	
        columnList = (TTable)getComponent("COLUMN_LIST");
        whereList = (TTable)getComponent("WHERE_LIST");
        publicTable = (TTable)getComponent("PUBLIC_TABLE");
        privateTable = (TTable)getComponent("PRIVATE_TABLE");
        pubRadio = (TRadioButton)getComponent("PUB_RADIO");
        prvRadio = (TRadioButton)getComponent("PRV_RADIO");
        defaultCheckBox = (TCheckBox)getComponent("DEFAULT_CHECKBOX");
        tab = (TTabbedPane)getComponent("TAB");
        publicTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheck");
        privateTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheck");
        if(tableName == null || tableName.length() == 0)
            return;
        //设置标题
        setTitle(getDBTool().getTableComment(tableName) + "查询设置窗口");
        //初始化Where Table
        initWhereTable();
        //得到表参数
        initTableParm();
        //初始化列信息
        initColumnList(CustomQueryTool.getInstance().getViewTableColumn(tableName));
        //初始化公有Table
        initPublicTable();
        //初始化私有Table
        initPrivateTable();
    }
    /**
     * 初始化公有Table
     */
    public void initPublicTable()
    {
        TParm parm = CustomQueryTool.getInstance().getPubWhereList(tableName);
        System.out.println("pub data="+parm);
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return;
        }
        publicTable.setParmValue(parm);
    }
    /**
     * 初始化私有Table
     */
    public void initPrivateTable()
    {
        TParm parm = CustomQueryTool.getInstance().getPriWhereList(tableName);
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return;
        }
        privateTable.setParmValue(parm);
    }
    /**
     * 得到表参数
     */
    public void initTableParm()
    {
        if(tableName == null || tableName.length() == 0)
            return;
        tableParm = new TParm(getDBTool().getColumnsInf(tableName));
        if(tableParm.getErrCode() < 0)
        {
            messageBox_(tableParm.getErrText());
            return;
        }

        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("ID:COLUMN_NAME;TEXT:COMMENTS");
        comboBox.setParmValue(tableParm);
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        whereList.addItem("ColumnCombo",comboBox);

        comboBox = new TComboBox();
        comboBox.setParmMap("ID:ID;TEXT:TEXT");
        comboBox.setParmValue(getCParm());
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        whereList.addItem("CCombo",comboBox);

        comboBox = new TComboBox();
        comboBox.setParmMap("ID:ID;TEXT:TEXT");
        comboBox.setParmValue(getLParm());
        comboBox.setShowID(false);
        comboBox.setTableShowList("TEXT");
        whereList.addItem("LCombo",comboBox);
    }
    /**
     * 得到判断数据
     * @return TParm
     */
    public TParm getCParm()
    {
        TParm parm = new TParm();
        parm.addData("ID","=");
        parm.addData("TEXT","等于");
        parm.addData("ID","<>");
        parm.addData("TEXT","不等于");
        parm.addData("ID",">");
        parm.addData("TEXT","大于");
        parm.addData("ID","<");
        parm.addData("TEXT","小于");
        parm.addData("ID",">=");
        parm.addData("TEXT","大于等于");
        parm.addData("ID","<=");
        parm.addData("TEXT","小于等于");
        parm.addData("ID","like");
        parm.addData("TEXT","模糊");
        parm.setCount(parm.getCount("ID"));
        return parm;
    }
    /**
     * 得到连接数据
     * @return TParm
     */
    public TParm getLParm()
    {
        TParm parm = new TParm();
        parm.addData("ID","AND");
        parm.addData("TEXT","并且");
        parm.addData("ID","OR");
        parm.addData("TEXT","或者");
        parm.setCount(parm.getCount("ID"));
        return parm;
    }
    /**
     * 初始化列信息
     * @param columns String[]
     */
    public void initColumnList(String columns[])
    {
        if(tableParm == null || tableParm.getErrCode() < 0)
            return;
        TParm parm = tableParm;
        if(columns != null && columns.length > 0)
        {
            parm = new TParm();
            Vector columnNames = (Vector)tableParm.getData("COLUMN_NAME");
            for(int i = 0;i < columns.length;i++)
            {
                int index = columnNames.indexOf(columns[i]);
                if(index < 0)
                    continue;
                TParm row = tableParm.getRow(index);
                parm.setRowData(i,row);
            }
            parm.setCount(parm.getCount("COLUMN_NAME"));
        }
        columnList.setParmValue(parm);
        if(columnList.getRowCount() > 0)
            columnList.setSelectedRow(0);
    }
    /**
     * 得到数据库对象
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool()
    {
        return TJDODBTool.getInstance();
    }
    /**
     * 插入
     * N;C;V;L;E
     */
    public void onInsert()
    {
        int row = whereList.getSelectedRow();
        addWhere(row);
    }
    /**
     * 追加
     */
    public void onNew()
    {
        int row = whereList.getRowCount();
        addWhere(row);
    }
    /**
     * 增加查询条件
     * BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
     * @param row int
     */
    public void addWhere(int row)
    {
        String name = "";
        int selectRow = columnList.getSelectedRow();
        if(selectRow >= 0)
            name = columnList.getParmValue().getValue("COLUMN_NAME",selectRow);
        TParm parm = new TParm();
        parm.setData("BRACKET_START","");
        parm.setData("COLUMN_ID",name);
        parm.setData("MARK","=");
        parm.setData("VALUE","");
        parm.setData("BRACKET_END","");
        parm.setData("LINK","AND");
        parm.setData("EXTERNAL_FLG","Y");
        row = whereList.addRow(row,parm);
        whereList.setSelectedRow(row);
        callFunction("UI|DELETE_WHERE_BUTTOM|setEnabled",true);
    }
    /**
     * 初始化Where Table
     * BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
     */
    public void initWhereTable()
    {
        TParm parm = new TParm();
        parm.setData("BRACKET_START",new Vector());
        parm.setData("COLUMN_ID",new Vector());
        parm.setData("MARK",new Vector());
        parm.setData("VALUE",new Vector());
        parm.setData("BRACKET_END",new Vector());
        parm.setData("LINK",new Vector());
        parm.setData("EXTERNAL_FLG",new Vector());
        whereList.setParmValue(parm);
    }
    /**
     * 条件选择
     */
    public void onSelectColumn()
    {
        String[] result = (String[])openDialog("%ROOT%\\config\\customquery\\SelectColumnDialog.x",new Object[]{tableName});
        if(result == null)
            return;
        //初始化列信息
        initColumnList(result);
    }
    /**
     * 删除Where
     */
    public void onDeleteWhere()
    {
        if(whereList.getRowCount() == 0)
            return;
        int row = whereList.getSelectedRow();
        if(row < 0)
            return;
        whereList.removeRow(row);
        if(whereList.getRowCount() <= 0)
        {
            callFunction("UI|DELETE_WHERE_BUTTOM|setEnabled",false);
            return;
        }
        whereList.setSelectedRow(row < whereList.getRowCount()?row:whereList.getRowCount() - 1);
    }
    /**
     * 另存为
     * BRACKET_START;COLUMN_ID;MARK;VALUE;BRACKET_END;LINK;EXTERNAL_FLG
     */
    public void onSaveAs()
    {
        whereList.acceptText();
        //保存测试
        if(!saveCheck())
            return;
        String name = getText("WHERE_NAME");
        boolean pubFlg = pubRadio.isSelected();
        boolean defaultFlg = defaultCheckBox.isSelected();
        TParm parm = new TParm();
        TParm whereParm = whereList.getParmValue();
        if(whereParm != null)
        {
            for (int i = 0; i < whereParm.getCount(); i++) {
                parm.addData("BRACKET_START", whereParm.getValue("BRACKET_START", i));
                parm.addData("COLUMN_ID", whereParm.getValue("COLUMN_ID", i));
                parm.addData("MARK", whereParm.getValue("MARK", i));
                parm.addData("VALUE", whereParm.getValue("VALUE", i));
                parm.addData("BRACKET_END", whereParm.getValue("BRACKET_END", i));
                parm.addData("LINK", whereParm.getValue("LINK", i));
                parm.addData("EXTERNAL_FLG", whereParm.getBoolean("EXTERNAL_FLG", i)?"Y":"N");
            }
            parm.setCount(whereParm.getCount());
        }
        TParm result = CustomQueryTool.getInstance().insertViewWhere(name,tableName,pubFlg,defaultFlg,parm);
        if(result.getErrCode() < 0)
        {
            messageBox_(result.getErrText());
            return;
        }
        //初始化公有Table
        initPublicTable();
        //初始化私有Table
        initPrivateTable();
        editWhereCode = result.getInt("WHERE_CODE");
        setText("SELECT_ID","正在编辑ID:" + editWhereCode);
        messageBox_("保存成功!");
        callFunction("UI|SAVE_BUTTOM|setEnabled",true);
    }
    /**
     * 保存
     */
    public void onSave()
    {
        whereList.acceptText();
        if(editWhereCode <= 0)
            return;
        //保存测试
        if(!saveCheck())
            return;
        String name = getText("WHERE_NAME");
        boolean pubFlg = pubRadio.isSelected();
        boolean defaultFlg = defaultCheckBox.isSelected();
        TParm parm = new TParm();
        TParm whereParm = whereList.getParmValue();
        if(whereParm != null)
        {
            for (int i = 0; i < whereParm.getCount(); i++) {
                parm.addData("BRACKET_START", whereParm.getValue("BRACKET_START", i));
                parm.addData("COLUMN_ID", whereParm.getValue("COLUMN_ID", i));
                parm.addData("MARK", whereParm.getValue("MARK", i));
                parm.addData("VALUE", whereParm.getValue("VALUE", i));
                parm.addData("BRACKET_END", whereParm.getValue("BRACKET_END", i));
                parm.addData("LINK", whereParm.getValue("LINK", i));
                parm.addData("EXTERNAL_FLG", whereParm.getBoolean("EXTERNAL_FLG", i)?"Y":"N");
            }
            parm.setCount(whereParm.getCount());
        }
        TParm result = CustomQueryTool.getInstance().updateViewWhere(editWhereCode,name,tableName,pubFlg,defaultFlg,parm);
        if(result.getErrCode() < 0)
        {
            messageBox_(result.getErrText());
            return;
        }
        //初始化公有Table
        initPublicTable();
        //初始化私有Table
        initPrivateTable();
        messageBox_("保存成功!");
    }
    /**
     * 保存测试
     * @return boolean
     */
    public boolean saveCheck()
    {
        String name = getText("WHERE_NAME");
        if(name.length() == 0)
        {
            messageBox_("请输入名称!");
            grabFocus("WHERE_NAME");
            return false;
        }
        return true;
    }
    /**
     * 公有table点击
     */
    public void onPublicClick()
    {
        privateTable.clearSelection();
    }
    /**
     * 私有table点击
     */
    public void onPrivateClick()
    {
        publicTable.clearSelection();
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        boolean isPublic = false;
        int whereCode = 0;
        int row = publicTable.getSelectedRow();
        if(row >= 0)
        {
            TParm parm = publicTable.getParmValue();
            String name = parm.getValue("WHERE_DESC",row);
            if(this.messageBox("提示信息","确认删除公有查询'" + name + "'",YES_NO_OPTION) != 0)
                return;
            whereCode = parm.getInt("WHERE_CODE",row);
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row < 0)
                return;
            TParm parm = privateTable.getParmValue();
            String name = parm.getValue("WHERE_DESC",row);
            if(this.messageBox("提示信息","确认删除私有查询'" + name + "'",YES_NO_OPTION) != 0)
                return;
            whereCode = parm.getInt("WHERE_CODE",row);
        }
        TParm parm = CustomQueryTool.getInstance().deleteWhere(whereCode);
        if(parm.getErrCode() < 0)
        {
            messageBox(parm.getErrText());
            return;
        }
        if(isPublic)
            publicTable.removeRow(row);
        else
            privateTable.removeRow(row);
    }
    /**
     * 编辑
     */
    public void onEdit()
    {
        boolean isPublic = false;
        int whereCode = 0;
        String name = "";
        int row = publicTable.getSelectedRow();
        if(row >= 0)
        {
            TParm parm = publicTable.getParmValue();
            name = parm.getValue("WHERE_DESC",row);
            whereCode = parm.getInt("WHERE_CODE",row);
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row < 0)
                return;
            TParm parm = privateTable.getParmValue();
            name = parm.getValue("WHERE_DESC",row);
            whereCode = parm.getInt("WHERE_CODE",row);
        }
        tab.setSelectedIndex(1);
        if(isPublic)
            pubRadio.setSelected(true);
        else
            prvRadio.setSelected(true);
        setText("WHERE_NAME",name);
        TParm parm = CustomQueryTool.getInstance().queryWhere(whereCode);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        whereList.setParmValue(parm);
        editWhereCode = whereCode;
        setText("SELECT_ID","正在编辑ID:" + whereCode);
        callFunction("UI|SAVE_BUTTOM|setEnabled",true);
    }
    /**
     * 上移
     */
    public void onUp()
    {
        boolean isPublic = false;
        TParm parm;
        int row = publicTable.getSelectedRow();
        if(row > 0)
        {
            parm = publicTable.getParmValue();
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row <= 0)
                return;
            parm = privateTable.getParmValue();
        }
        int whereCode = parm.getInt("WHERE_CODE",row);
        int whereCode1 = parm.getInt("WHERE_CODE",row - 1);
        parm = CustomQueryTool.getInstance().updateSeq(whereCode,whereCode1);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        //初始化公有Table
        initPublicTable();
        //初始化私有Table
        initPrivateTable();
        if(isPublic)
            publicTable.setSelectedRow(row - 1);
        else
            privateTable.setSelectedRow(row - 1);
    }
    /**
     * 下移
     */
    public void onDown()
    {
        boolean isPublic = false;
        TParm parm;
        int row = publicTable.getSelectedRow();
        if(row >= 0)
        {
            if(row >= publicTable.getRowCount() - 1)
                return;
            parm = publicTable.getParmValue();
            isPublic = true;
        }else
        {
            row = privateTable.getSelectedRow();
            if(row < 0)
                return;
            if(row >= privateTable.getRowCount() - 1)
                return;
            parm = privateTable.getParmValue();
        }
        int whereCode = parm.getInt("WHERE_CODE",row);
        int whereCode1 = parm.getInt("WHERE_CODE",row + 1);
        parm = CustomQueryTool.getInstance().updateSeq(whereCode,whereCode1);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        //初始化公有Table
        initPublicTable();
        //初始化私有Table
        initPrivateTable();
        if(isPublic)
            publicTable.setSelectedRow(row + 1);
        else
            privateTable.setSelectedRow(row + 1);
    }
    /**
     * 公有Table点击默认
     * @param obj Object
     */
    public void onCheck(Object obj)
    {
        TTable table=(TTable)obj;
        table.acceptText();
        int row = table.getSelectedRow();
        int whereCode = table.getParmValue().getInt("WHERE_CODE",row);
        TParm parm = CustomQueryTool.getInstance().getUpateDefaultSQL(tableName,whereCode);
        if(parm.getErrCode() < 0)
        {
            messageBox_(parm.getErrText());
            return;
        }
        cancelDefCheck();
        table.setItem(row,"DEF","Y");
    }
    public void cancelDefCheck()
    {
        Vector v = (Vector)publicTable.getParmValue().getData("DEF");
        for(int i = 0;i < v.size();i++)
        {
            if("Y".equals(v.get(i)))
                publicTable.setValueAt("N",i,0);
            v.set(i, "N");
        }
        v = (Vector)privateTable.getParmValue().getData("DEF");
        for(int i = 0;i < v.size();i++)
        {
            if("Y".equals(v.get(i)))
                privateTable.setValueAt("N",i,0);
            v.set(i, "N");
        }
    }
    public static void main(String args[])
    {
        Object parm = com.javahis.util.JavaHisDebug.runDialog("customquery\\customQuery.x",new Object[]{"UDD_SHEET"});
        System.out.println("parm=" + parm);
    }
}
