package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.VLine;
import com.dongyang.util.TypeTool;
import java.awt.Color;
import com.dongyang.util.StringTool;
import com.dongyang.tui.DColor;

/**
 *
 * <p>Title: 直线属性</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.21
 * @version 1.0
 */
public class LineDialogControl extends TControl{
    private VLine line;
    /**
     * 初始化
     */
    public void onInit()
    {
        line = (VLine)getParameter();
        setValue("NAME",line.getName());
        setValue("DX1",line.getX1());
        setValue("DY1",line.getY1());
        setValue("DX2",line.getX2());
        setValue("DY2",line.getY2());
        setValue("V_CB",line.isVisible());
        Color c = line.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        setValue("LINE_WIDTH",line.getLineWidth());
        setValue("LINE_WIDTH1",line.getLineWidth1());
        setValue("LINE_TYPE",line.getLineType());
        setValue("DX0",line.getX0());
        setValue("DY0",line.getY0());
        setValue("DCOUNT",line.getCount());
        setValue("DX1B",line.isX1B());
        setValue("DY1B",line.isY1B());
        setValue("DX2B",line.isX2B());
        setValue("DY2B",line.isY2B());
        setValue("LINE_T",line.isLineT());
    }
    /**
     * 设置名称
     */
    public void onName()
    {
        line.setName(getText("NAME"));
        line.update();
        line.modify("NAME");
    }
    /**
     * 设置x1
     */
    public void onDX1()
    {
        line.setX1(TypeTool.getInt(getValue("DX1")));
        line.update();
    }
    /**
     * 设置y1
     */
    public void onDY1()
    {
        line.setY1(TypeTool.getInt(getValue("DY1")));
        line.update();
    }
    /**
     * 设置x2
     */
    public void onDX2()
    {
        line.setX2(TypeTool.getInt(getValue("DX2")));
        line.update();
    }
    /**
     * 设置y2
     */
    public void onDY2()
    {
        line.setY2(TypeTool.getInt(getValue("DY2")));
        line.update();
    }
    /**
     * 显示
     */
    public void onVCB()
    {
        line.setVisible(TypeTool.getBoolean(getValue("V_CB")));
        line.update();
        line.modify("VISIBLE");
    }
    /**
     * 颜色
     */
    public void onColor()
    {
        Color color = StringTool.getColor(getText("COLOR"),getConfigParm());
        if(color == null)
            color = new Color(0,0,0);
        line.setColor(color);
        callFunction("UI|COLOR_D|setForeground",color);
        line.update();
    }
    /**
     * 调色板
     */
    public void onColorDialog()
    {
        line.setColor(DColor.colorDialog(getComponent(),line.getColor()));
        Color c = line.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        line.update();
    }
    /**
     * 线高
     */
    public void onLineWidth()
    {
        line.setLineWidth(TypeTool.getFloat(getValue("LINE_WIDTH")));
        line.update();
    }
    /**
     * 线高
     */
    public void onLineWidth1()
    {
        line.setLineWidth1(TypeTool.getFloat(getValue("LINE_WIDTH1")));
        line.update();
    }
    /**
     * 线类型
     */
    public void onLineType()
    {
        line.setLineType(TypeTool.getInt(getValue("LINE_TYPE")));
        line.update();
    }
    /**
     * 设置x0
     */
    public void onDX0()
    {
        line.setX0(TypeTool.getInt(getValue("DX0")));
        line.update();
    }
    /**
     * 设置y0
     */
    public void onDY0()
    {
        line.setY0(TypeTool.getInt(getValue("DY0")));
        line.update();
    }
    /**
     * 设置线数
     */
    public void onDCount()
    {
        line.setCount(TypeTool.getInt(getValue("DCOUNT")));
        line.update();
    }
    public void onDX1B()
    {
        line.setX1B(TypeTool.getBoolean(getValue("DX1B")));
        line.update();
    }
    public void onDY1B()
    {
        line.setY1B(TypeTool.getBoolean(getValue("DY1B")));
        line.update();
    }
    public void onDX2B()
    {
        line.setX2B(TypeTool.getBoolean(getValue("DX2B")));
        line.update();
    }
    public void onDY2B()
    {
        line.setY2B(TypeTool.getBoolean(getValue("DY2B")));
        line.update();
    }
    public void onLineT()
    {
        line.setLineT(TypeTool.getBoolean(getValue("LINE_T")));
        line.update();
    }
    /**
     * 修改参数
     * @param name String
     */
    public void modify(String name)
    {
        if("NAME".equals(name))
        {
            setValue("NAME",line.getName());
            return;
        }
        if("VISIBLE".equals(name))
        {
            setValue("V_CB",line.isVisible());
            return;
        }
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        line.setPropertyWindow(null);
        return true;
    }
}
