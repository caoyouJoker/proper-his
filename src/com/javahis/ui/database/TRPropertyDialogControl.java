package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.ETR;
import com.dongyang.ui.TCheckBox;

/**
 *
 * <p>Title: 行数属性对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.15
 * @version 1.0
 */
public class TRPropertyDialogControl extends TControl{
    private ETR tr;
    private TCheckBox showHLine;
    private TCheckBox showUp;
    private TCheckBox showDown;
    private TCheckBox showLeft;
    private TCheckBox showRight;
    private TCheckBox visible;
    /**
     * 初始化
     */
    public void onInit()
    {
        showHLine = (TCheckBox)getComponent("SHOW_H_LINE");
        showUp = (TCheckBox)getComponent("SHOW_UP");
        showDown = (TCheckBox)getComponent("SHOW_DOWN");
        showLeft = (TCheckBox)getComponent("SHOW_LEFT");
        showRight = (TCheckBox)getComponent("SHOW_RIGHT");
        visible = (TCheckBox)getComponent("T_VISIBLE");
        tr = (ETR)getParameter();
        if(tr == null)
            return;
        showHLine.setSelected(tr.isShowHLine());
        showUp.setSelected(tr.isShowBorder(1));
        showDown.setSelected(tr.isShowBorder(2));
        showLeft.setSelected(tr.isShowBorder(4));
        showRight.setSelected(tr.isShowBorder(8));
        visible.setSelected(tr.isVisible());
    }
    /**
     * 确定
     */
    public void onOK()
    {
        tr.setShowBorder(0);
        tr.setShowBorder(1,showUp.isSelected());
        tr.setShowBorder(2,showDown.isSelected());
        tr.setShowBorder(4,showLeft.isSelected());
        tr.setShowBorder(8,showRight.isSelected());
        tr.setShowHLine(showHLine.isSelected());
        tr.setVisible(visible.isSelected());
        tr.getFocusManager().update();
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
