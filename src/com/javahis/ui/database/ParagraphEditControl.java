package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.MFocus;
import com.dongyang.tui.text.EText;
import com.dongyang.tui.text.EPanel;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TRadioButton;

/**
 *
 * <p>Title: 段落对话框控制类</p>
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
public class ParagraphEditControl extends TControl{
    MFocus focusManager;
    EPanel panel;
    /**
     * 初始化
     */
    public void onInit()
    {
        focusManager = (MFocus)getParameter();
        if(focusManager == null)
            return;
        EText text = (EText)focusManager.getFocus();
        if(text == null)
            return;
        panel = text.getPanel();
        setValue("PF",panel.getParagraphForward());
        setValue("PA",panel.getParagraphAfter());
        setValue("SB",panel.getSpaceBetween());
        setValue("RETRACT_LEFT",panel.getRetractLeft());
        setValue("RETRACT_RIGHT",panel.getRetractRight());
        setValue("RETRACT_WIDTH",panel.getRetractWidth());
        switch(panel.getSexControl())
        {
            case 0:
                setValue("tRadio_ALL",true);
                break;
            case 1:
                setValue("tRadio_MAN",true);
                break;
            case 2:
                setValue("tRadio_WOMAN",true);
                break;
        }
        switch(panel.getRetractType())
        {
            case 0:
                ((TRadioButton)getComponent("RETRACT_1")).setSelected(true);
                break;
            case 1:
                ((TRadioButton)getComponent("RETRACT_2")).setSelected(true);
                break;
            case 2:
                ((TRadioButton)getComponent("RETRACT_3")).setSelected(true);
                break;
        }
        setValue("ELEMENT_EDIT",panel.isElementEdit());
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(panel == null)
            return;
        panel.setParagraphForward(TypeTool.getInt(getValue("PF")));
        panel.setParagraphAfter(TypeTool.getInt(getValue("PA")));
        panel.setSpaceBetween(TypeTool.getInt(getValue("SB")));
        panel.setRetractLeft(TypeTool.getInt(getValue("RETRACT_LEFT")));
        panel.setRetractRight(TypeTool.getInt(getValue("RETRACT_RIGHT")));
        panel.setRetractWidth(TypeTool.getInt(getValue("RETRACT_WIDTH")));
        if(((TRadioButton)getComponent("RETRACT_1")).isSelected())
            panel.setRetractType(0);
        if(((TRadioButton)getComponent("RETRACT_2")).isSelected())
            panel.setRetractType(1);
        if(((TRadioButton)getComponent("RETRACT_3")).isSelected())
            panel.setRetractType(2);
        panel.setElementEdit(TypeTool.getBoolean(getValue("ELEMENT_EDIT")));
        panel.setModify(true);
        panel.setLinkPanelParameterAll();

        focusManager.update();
        closeWindow();
    }
    public void onRALL()
    {
        panel.setSexControl(0);
        panel.update();
    }
    public void onRMan()
    {
        panel.setSexControl(1);
        panel.update();
    }
    public void onRWoman()
    {
        panel.setSexControl(2);
        panel.update();
    }
}
