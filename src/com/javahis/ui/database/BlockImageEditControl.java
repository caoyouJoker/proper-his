package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EImage;
import com.dongyang.util.TypeTool;

public class BlockImageEditControl extends TControl{
    private EImage image;
    /**
     * ≥ı ºªØ
     */
    public void onInit()
    {
        image = (EImage) getParameter();
        setValue("BORDER_VISIBLE",image.isBorderVisible());
        switch(image.getSexControl())
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

    }
    public void onBorderVisible()
    {
        image.setBorderVisible(TypeTool.getBoolean(getValue("BORDER_VISIBLE")));
        image.update();
    }
    public void onRALL()
    {
        image.setSexControl(0);
    }
    public void onRMan()
    {
        image.setSexControl(1);
    }
    public void onRWoman()
    {
        image.setSexControl(2);
    }
}
