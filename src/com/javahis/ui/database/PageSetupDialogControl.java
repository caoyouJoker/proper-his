package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.MPage;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TRadioButton;

/**
 *
 * <p>Title: 页面设置</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.15
 * @version 1.0
 */
public class PageSetupDialogControl extends TControl{
    /**
     * 页面管理器
     */
    private MPage pageManager;
    /**
     * 初始化
     */
    public void onInit()
    {
        pageManager = (MPage)getParameter();
        if(pageManager == null)
            return;
        //初始化数据
        initData();
    }
    /**
     * 初始化数据
     */
    public void initData()
    {
        setValue("PageWidth",pageManager.getPageWidth());
        setValue("PageHeight",pageManager.getPageHeight());
        setValue("Top",pageManager.getImageableY());
        setValue("Bottom",pageManager.getPageHeight() - pageManager.getImageableHeight() - pageManager.getImageableY());
        setValue("Left",pageManager.getImageableX());
        setValue("Right",pageManager.getPageWidth() - pageManager.getImageableWidth() - pageManager.getImageableX());
        setValue("E_Top",pageManager.getEditInsets().top);
        setValue("E_Bottom",pageManager.getEditInsets().bottom);
        setValue("E_Left",pageManager.getEditInsets().left);
        setValue("E_Right",pageManager.getEditInsets().right);
        /*if(pageManager.getOrientation() == 1)
        {
            ( (TRadioButton) getComponent("OH")).setSelected(true);
            ( (TRadioButton) getComponent("OW")).setSelected(false);
        }else
        {
            ( (TRadioButton) getComponent("OH")).setSelected(false);
            ( (TRadioButton) getComponent("OW")).setSelected(true);
        }*/
    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(pageManager == null)
            return;
        pageManager.setPageWidth(TypeTool.getDouble(getValue("PageWidth")));
        pageManager.setPageHeight(TypeTool.getDouble(getValue("PageHeight")));
        pageManager.setImageableX(TypeTool.getDouble(getValue("Left")));
        pageManager.setImageableY(TypeTool.getDouble(getValue("Top")));
        pageManager.setImageableWidth(pageManager.getPageWidth() - TypeTool.getDouble(getValue("Right")) -
            pageManager.getImageableX());
        pageManager.setImageableHeight(pageManager.getPageHeight() - TypeTool.getDouble(getValue("Bottom")) -
            pageManager.getImageableY());
        pageManager.getEditInsets().top = TypeTool.getInt(getValue("E_Top"));
        pageManager.getEditInsets().bottom = TypeTool.getInt(getValue("E_Bottom"));
        pageManager.getEditInsets().left = TypeTool.getInt(getValue("E_Left"));
        pageManager.getEditInsets().right = TypeTool.getInt(getValue("E_Right"));


        /*pageManager.getInsets().top = TypeTool.getInt(getValue("Top"));
        pageManager.getInsets().bottom = TypeTool.getInt(getValue("Bottom"));
        pageManager.getInsets().left = ;
        pageManager.getInsets().right = TypeTool.getInt(getValue("Right"));
        */
        pageManager.setPageSizeModify();
        pageManager.update();
        closeWindow();
    }
    /**
     * 打印机设置
     */
    public void onSetup()
    {
        pageManager.printSetup();
        //初始化数据
        initData();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
}
