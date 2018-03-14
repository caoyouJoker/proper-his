package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 插入表格对话框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.10.23
 * @version 1.0
 */
public class InsertTableBaseControl extends TControl{
    /**
     * 确定
     */
    public void onOK()
    {
        int column = TypeTool.getInt(getValue("ROW_COUNT"));
        int row = TypeTool.getInt(getValue("COLUMN_COUNT"));
        String name = TypeTool.getString(getValue("NAME"));
        setReturnValue(new Object[]{name,column,row});
        closeWindow();
    }

}
