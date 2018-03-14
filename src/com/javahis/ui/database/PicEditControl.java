package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EPic;
import com.dongyang.util.TypeTool;
/**
 *
 * <p>Title: 图区编辑对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.29
 * @version 1.0
 */
public class PicEditControl extends TControl{
    private EPic pic;
    /**
     * 初始化
     */
    public void onInit()
    {
        pic = (EPic)getParameter();
        if(pic == null)
            return;
        setValue("NAME",pic.getName());
        setValue("PicWidth",pic.getWidth());
        setValue("PicHeight",pic.getHeight());
        if(pic.getModel() == null)
            return;
        setValue("FF",pic.getModel().getSyntax("paintForeground").getSyntax());
        setValue("BF",pic.getModel().getSyntax("paintBackground").getSyntax());
    }
    /**
     * 确定
     */
    public void onOK()
    {
        pic.setName(getText("NAME"));
        pic.setWidth(TypeTool.getInt(getValue("PicWidth")));
        pic.setHeight(TypeTool.getInt(getValue("PicHeight")));
        pic.setModify(true);
        if(pic.getModel() != null)
        {
            pic.getModel().getSyntax("paintForeground").setSyntax(getText("FF"));
            pic.getModel().getSyntax("paintBackground").setSyntax(getText("BF"));
        }
        setReturnValue("OK");
        closeWindow();
    }
}
