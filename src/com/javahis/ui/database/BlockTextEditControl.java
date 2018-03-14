package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.image.GBlock;
import com.dongyang.util.StringTool;
import java.awt.Color;
import com.dongyang.tui.DColor;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTable;
import java.util.Vector;

/**
 *
 * <p>Title: 文本属性对话框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2010.3.30
 * @version 1.0
 */
public class BlockTextEditControl extends TControl{
    private GBlock block;
    private TTable table;
    /**
     * 初始化
     */
    public void onInit()
    {
        //this.messageBox("come in");
        block = (GBlock)getParameter();
        setValue("DTEXT",block.getText());
        Color c = block.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        c = block.getBkColor();
        if(c != null)
        {
            setValue("BK_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BK_COLOR_D|setForeground", c);
        }else
        {
            setValue("BK_COLOR","");
            callFunction("UI|BK_COLOR_D|setForeground", new Color(0,0,0));
        }
        c = block.getBorderColor();
        if(c != null)
        {
            setValue("BORDER_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BORDER_COLOR_D|setForeground", c);
        }else
        {
            setValue("BORDER_COLOR","");
            callFunction("UI|BORDER_COLOR_D|setForeground", new Color(0,0,0));
        }
        setValue("DFONT",block.getFontName());
        setText("DFONT_SIZE",""+block.getFontSize());
        setValue("DB",block.isFontBold());
        setValue("DI",block.isFontItalic());
        setValue("DFONT_ROTATE",block.getFontRotate());
        setValue("AUTO_ENTER_HEIGHT",block.getAutoEnterHeight());
        switch(block.getAlignment())
        {
            case 0:
                setValue("ALEFT",true);
                break;
            case 1:
                setValue("ACENTER",true);
                break;
            case 2:
                setValue("ARIGHT",true);
                break;
        }
        switch(block.getAlignmentH())
        {
            case 0:
                setValue("AUH",true);
                break;
            case 1:
                setValue("ACH",true);
                break;
            case 2:
                setValue("ADH",true);
                break;
        }
        setValue("BORDER_VISIBLE",block.isBorderVisible());
        setValue("BORDER_LINE_WIDTH",block.getBorderWidth());
        setValue("BORDER_LINE_TYPE",block.getBorderLineType());
        setValue("PIC_AUTO_SIZE",block.isPictureAutoSize());
        setValue("ARROW1_TYPE",block.getArrow1());
        setValue("ARROW2_TYPE",block.getArrow2());
        setValue("ARROW1_LENGTH",block.getArrow1Length());
        setValue("ARROW2_LENGTH",block.getArrow2Length());
        setValue("ARROW1_DEGREE",block.getArrow1Degree());
        setValue("ARROW2_DEGREE",block.getArrow2Degree());
        setValue("G_ENABLED",block.isEnabled());

        table = (TTable)getComponent("TABLE");
        table.setValue(newVector(block.getData()));
        if(table.getRowCount() > 0)
            table.setSelectedRow(0);
        setValue("BK_PIC",block.getPictureName());

    }
    public void onOK()
    {

    }
    /**
     * 修改文本
     */
    public void onText()
    {
        //this.messageBox("text"+getText("DTEXT"));
        block.setText(getText("DTEXT"));
        block.update();
    }
    /**
     * 颜色
     */
    public void onColor()
    {


        Color color = StringTool.getColor(getText("COLOR"),getConfigParm());
        if(color == null)
            color = new Color(0,0,0);
        block.setColor(color);
        callFunction("UI|COLOR_D|setForeground",color);
        block.update();
    }
    /**
     * 调色板
     */
    public void onColorDialog()
    {
        block.setColor(DColor.colorDialog(getComponent(),block.getColor()));
        Color c = block.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        block.update();
    }
    /**
     * 背景颜色
     */
    public void onBkColor()
    {
         //this.messageBox("COLOR"+getText("COLOR"));

        Color color = StringTool.getColor(getText("BK_COLOR"),getConfigParm());
        block.setBkColor(color);
        if(color != null)
            color = new Color(0,0,0);
        callFunction("UI|BK_COLOR_D|setForeground",color);
        block.update();
    }
    /**
     * 背景颜色调色板
     */
    public void onBKColorDialog()
    {
        block.setBkColor(DColor.colorDialog(getComponent(),block.getColor()));
        Color c = block.getBkColor();
        if(c != null)
        {
            setValue("BK_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BK_COLOR_D|setForeground",c);
        }
        else
        {
            setValue("BK_COLOR", "");
            callFunction("UI|BK_COLOR_D|setForeground",new Color(0,0,0));
        }
        block.update();
    }
    /**
     * 边框颜色
     */
    public void onBorderColor()
    {
        Color color = StringTool.getColor(getText("BORDER_COLOR"),getConfigParm());
        block.setBorderColor(color);
        if(color != null)
            color = new Color(0,0,0);
        callFunction("UI|BORDER_COLOR_D|setForeground",color);
        block.update();
    }
    /**
     * 边框调色板
     */
    public void onBorderColorDialog()
    {
        block.setBorderColor(DColor.colorDialog(getComponent(),block.getColor()));
        Color c = block.getBorderColor();
        if(c != null)
        {
            setValue("BORDER_COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
            callFunction("UI|BORDER_COLOR_D|setForeground",c);
        }
        else
        {
            setValue("BORDER_COLOR", "");
            callFunction("UI|BK_COLOR_D|setForeground",new Color(0,0,0));
        }
        block.update();
    }
    /**
     * 字体
     */
    public void onFont()
    {
        block.setFontName(TypeTool.getString(getValue("DFONT")));
        block.update();
    }
    /**
     * 字号
     */
    public void onFontSize()
    {
        block.setFontSize(TypeTool.getInt(getText("DFONT_SIZE")));
        block.update();
    }
    /**
     * 粗体
     */
    public void onBold()
    {
        //this.messageBox("onBold"+TypeTool.getBoolean(getValue("DB")));
        block.setFontBold(TypeTool.getBoolean(getValue("DB")));
        block.update();
    }
    /**
     * 斜体
     */
    public void onItalic()
    {
        block.setFontItalic(TypeTool.getBoolean(getValue("DI")));
        block.update();
    }
    /**
     * 旋转
     */
    public void onFontRotate()
    {
        block.setFontRotate(TypeTool.getDouble(getValue("DFONT_ROTATE")));
        block.update();
    }
    /**
     * 居左
     */
    public void onALeft()
    {
        block.setAlignment(0);
        block.update();
    }
    /**
     * 居中
     */
    public void onACenter()
    {
        block.setAlignment(1);
        block.update();
    }
    /**
     * 居右
     */
    public void onARight()
    {
        block.setAlignment(2);
        block.update();
    }
    /**
     * 居左
     */
    public void onAUH()
    {
        block.setAlignmentH(0);
        block.update();
    }
    /**
     * 居中
     */
    public void onACH()
    {
        block.setAlignmentH(1);
        block.update();
    }
    /**
     * 居右
     */
    public void onADH()
    {
        block.setAlignmentH(2);
        block.update();
    }
    /**
     * 自动折行高度
     */
    public void onAutoEnterHeight()
    {
        block.setAutoEnterHeight(TypeTool.getInt(getValue("AUTO_ENTER_HEIGHT")));
        block.update();
    }
    /**
     * 是否显示边框
     */
    public void onBorderVisible()
    {
        block.setBorderVisible(TypeTool.getBoolean(getValue("BORDER_VISIBLE")));
        block.update();
    }
    /**
     * 设置边框线高
     */
    public void onBorderLineWidth()
    {
        block.setBorderWidth(TypeTool.getFloat(getValue("BORDER_LINE_WIDTH")));
        block.update();
    }
    /**
     * 设置边框线类型
     */
    public void onBorderLineType()
    {
        block.setBorderLineType(TypeTool.getInt(getValue("BORDER_LINE_TYPE")));
        block.update();
    }
    /**
     * 设置背景图片
     */
    public void onBkPic()
    {
        block.setPictureName(getText("BK_PIC"));
        block.update();
    }
    /**
     * 设置图片是否自动拉伸尺寸
     */
    public void onPicAutoSize()
    {
        block.setPictureAutoSize(TypeTool.getBoolean(getValue("PIC_AUTO_SIZE")));
        block.update();
    }
    public void onArrow1Type()
    {
        block.setArrow1(TypeTool.getInt(getValue("ARROW1_TYPE")));
        block.update();
    }
    public void onArrow2Type()
    {
        block.setArrow2(TypeTool.getInt(getValue("ARROW2_TYPE")));
        block.update();
    }
    public void onArrow1Length()
    {
        block.setArrow1Length(TypeTool.getInt(getValue("ARROW1_LENGTH")));
        block.update();
    }
    public void onArrow2Length()
    {
        block.setArrow2Length(TypeTool.getInt(getValue("ARROW2_LENGTH")));
        block.update();
    }
    public void onArrow1Degree()
    {
        block.setArrow1Degree(TypeTool.getDouble(getValue("ARROW1_DEGREE")));
        block.update();
    }
    public void onArrow2Degree()
    {
        block.setArrow2Degree(TypeTool.getDouble(getValue("ARROW2_DEGREE")));
        block.update();
    }
    public void onEnabled()
    {
        block.setEnabled(TypeTool.getBoolean(getValue("G_ENABLED")));
        block.update();
    }
    /**
     * Vector 克隆
     * @param v Vector
     * @return Vector
     */
    public Vector newVector(Vector v)
    {
        Vector data = new Vector();
        for(int i = 0;i < v.size();i++)
        {
            Vector t = (Vector)v.get(i);
            Vector t1 = new Vector();
            data.add(t1);
            for(int j = 0;j < t.size();j++)
                t1.add(t.get(j));
        }
        return data;
    }
    /**
     * 新增
     */
    public void onNew()
    {
        Vector v = new Vector();
        v.add("");
        v.add("");
        table.insertRowValue(table.getRowCount(),v);
        table.setSelectedRow(table.getRowCount() - 1);
        block.setData(table.getValue());
    }
    /**
     * 插入
     */
    public void onInsert()
    {
        int row = table.getSelectedRow();
        if(row == -1)
        {
            onNew();
            return;
        }
        Vector v = new Vector();
        v.add("");
        v.add("");
        table.insertRowValue(row,v);
        block.setData(table.getValue());
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        table.removeRow(row);
        if(table.getRowCount() == 0)
            return;
        if(row >= table.getRowCount())
            row = table.getRowCount() - 1;
        table.setSelectedRow(row);
        block.setData(table.getValue());
    }
    /**
     * 上移
     */
    public void onUp()
    {
        int row = table.getSelectedRow();
        if(row <= 0)
            return;
        Vector v = new Vector();
        v.add(table.getValueAt(row - 1,0));
        v.add(table.getValueAt(row - 1,1));
        table.insertRowValue(row + 1,v);
        table.removeRow(row - 1);
        table.setSelectedRow(row - 1);
        block.setData(table.getValue());
    }
    /**
     * 下移
     */
    public void onDown()
    {
        int row = table.getSelectedRow();
        if(row == -1 || row == table.getRowCount() - 1)
            return;
        Vector v = new Vector();
        v.add(table.getValueAt(row + 1,0));
        v.add(table.getValueAt(row + 1,1));
        table.removeRow(row + 1);
        table.insertRowValue(row,v);
        table.setSelectedRow(row + 1);
        block.setData(table.getValue());
    }
}
