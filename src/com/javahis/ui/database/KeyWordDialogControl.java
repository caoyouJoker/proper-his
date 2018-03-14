package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TWord;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 数字签字对话框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lzk 2010.2.2
 * @version 1.0
 */
public class KeyWordDialogControl extends TControl{
    TWord word;
    /**
     * 初始化
     */
    public void onInit()
    {
        word = (TWord)getParameter();
        setValue("KEY_INDEX",word.getPM().getModifyNodeManager().getIndex());
    }
    /**
     * 确定
     */
    public void onOK()
    {
        word.getPM().getModifyNodeManager().setIndex(TypeTool.getInt(getValue("KEY_INDEX")));
        closeWindow();
    }
}
