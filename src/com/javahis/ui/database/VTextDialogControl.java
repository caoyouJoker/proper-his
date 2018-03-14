package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.VText;
import java.awt.Color;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import com.dongyang.tui.DColor;
/**
 *
 * <p>Title: 文字属性控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.22
 * @version 1.0
 */
public class VTextDialogControl extends TControl{
    private VText text;
    /**
     * 初始化
     */
    public void onInit()
    {
        text = (VText)getParameter();
        setValue("NAME",text.getName());
        setValue("DX",text.getX());
        setValue("DY",text.getY());
        setValue("DWIDTH",text.getWidth());
        setValue("DX1B",text.isX1B());
        setValue("DY1B",text.isY1B());
        setValue("DX2B",text.isX2B());
        setValue("V_CB",text.isVisible());
        Color c = text.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        setValue("DX0",text.getX0());
        setValue("DY0",text.getY0());
        setValue("DCOUNT",text.getCount());
        setValue("DTEXT",text.getText());
        setValue("DFONT",text.getFontName());
        setText("DFONT_SIZE",""+text.getFontSize());
        setValue("DB",text.isFontBold());
        setValue("DI",text.isFontItalic());
        setValue("DFONT_ROTATE",text.getFontRotate());
        setValue("OFF_SET_FLG",text.isTextOffsetFlg());
        setValue("OFF_SET",text.getTextOffset());
        setValue("OFF_SET_FORMAT",text.getTextOffsetFormat());
        setValue("AUTO_ENTER",text.isAutoEnter());
        setValue("AUTO_ENTER_HEIGHT",text.getAutoEnterHeight());
        setValue("TEXT_T",text.isTextT());
        switch(text.getAlignment())
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
        setValue("DL",text.isLine());
        setValue("BOTTOMLINE",text.isBottomLine());
        setValue("DEL_ORDER",text.isDelOrder());
        setValue("txtGroupName",text.getGroupName());
        setValue("MACRO_NAME",text.getMicroName());
        //
        setValue("TAKECDANAME",text.getTakeCdaName());
        setValue("CDAVALUE",text.getCdaValue());
    }
    /**
     * 设置名称
     */
    public void onName()
    {
        text.setName(getText("NAME"));
        text.update();
        text.modify("NAME");
    }
    /**
     * 设置x
     */
    public void onDX()
    {
        text.setX(TypeTool.getInt(getValue("DX")));
        text.update();
    }
    /**
     * 设置y
     */
    public void onDY()
    {
        text.setY(TypeTool.getInt(getValue("DY")));
        text.update();
    }
    /**
     * 显示
     */
    public void onVCB()
    {
        text.setVisible(TypeTool.getBoolean(getValue("V_CB")));
        text.update();
        text.modify("VISIBLE");
    }
    /**
     * 颜色
     */
    public void onColor()
    {
        Color color = StringTool.getColor(getText("COLOR"),getConfigParm());
        if(color == null)
            color = new Color(0,0,0);
        text.setColor(color);
        callFunction("UI|COLOR_D|setForeground",color);
        text.update();
    }
    /**
     * 调色板
     */
    public void onColorDialog()
    {
        text.setColor(DColor.colorDialog(getComponent(),text.getColor()));
        Color c = text.getColor();
        setValue("COLOR",c.getRed() + "," + c.getGreen() + "," + c.getBlue());
        callFunction("UI|COLOR_D|setForeground",c);
        text.update();
    }
    /**
     * 设置x0
     */
    public void onDX0()
    {
        text.setX0(TypeTool.getInt(getValue("DX0")));
        text.update();
    }
    /**
     * 设置y0
     */
    public void onDY0()
    {
        text.setY0(TypeTool.getInt(getValue("DY0")));
        text.update();
    }
    /**
     * 设置线数
     */
    public void onDCount()
    {
        text.setCount(TypeTool.getInt(getValue("DCOUNT")));
        text.update();
    }
    /**
     * 文本
     */
    public void onText()
    {
        text.setText(getText("DTEXT"));
        text.update();
    }
    /**
     * 字体
     */
    public void onFont()
    {
        text.setFontName(TypeTool.getString(getValue("DFONT")));
        text.update();
    }
    /**
     * 字号
     */
    public void onFontSize()
    {
        text.setFontSize(TypeTool.getInt(getText("DFONT_SIZE")));
        text.update();
    }
    /**
     * 粗体
     */
    public void onBold()
    {
        text.setFontBold(TypeTool.getBoolean(getValue("DB")));
        text.update();
    }
    /**
     * 斜体
     */
    public void onItalic()
    {
        text.setFontItalic(TypeTool.getBoolean(getValue("DI")));
        text.update();
    }
    /**
     * 旋转
     */
    public void onFontRotate()
    {
        text.setFontRotate(TypeTool.getDouble(getValue("DFONT_ROTATE")));
        text.update();
    }
    /**
     * 偏移开关
     */
    public void onOffSetFlg()
    {
        text.setTextOffsetFlg(TypeTool.getBoolean(getValue("OFF_SET_FLG")));
        text.update();
    }
    /**
     * 偏移量
     */
    public void onOffSet()
    {
        text.setTextOffset(TypeTool.getDouble(getValue("OFF_SET")));
        text.update();
    }
    /**
     * 偏移显示格式
     */
    public void onOffSetFormat()
    {
        text.setTextOffsetFormat(TypeTool.getString(getValue("OFF_SET_FORMAT")));
        text.update();
    }
    /**
     * 自动折行
     */
    public void onAutoEnter()
    {
        text.setAutoEnter(TypeTool.getBoolean(getValue("AUTO_ENTER")));
        text.update();
    }
    /**
     * 自动折行高度
     */
    public void onAutoEnterHeight()
    {
        text.setAutoEnterHeight(TypeTool.getInt(getValue("AUTO_ENTER_HEIGHT")));
        text.update();
    }
    /**
     * 文字TParm 代入
     */
    public void onTextT()
    {
        text.setTextT(TypeTool.getBoolean(getValue("TEXT_T")));
        text.update();
    }
    /**
     * 修改参数
     * @param name String
     */
    public void modify(String name)
    {
        if("NAME".equals(name))
        {
            setValue("NAME",text.getName());
            return;
        }
        if("VISIBLE".equals(name))
        {
            setValue("V_CB",text.isVisible());
            return;
        }
    }
    /**
     * 设置宽度
     */
    public void onDWidth()
    {
        text.setWidth(TypeTool.getInt(getValue("DWIDTH")));
        text.update();
    }
    public void onDX1B()
    {
        text.setX1B(TypeTool.getBoolean(getValue("DX1B")));
        text.update();
    }
    public void onDY1B()
    {
        text.setY1B(TypeTool.getBoolean(getValue("DY1B")));
        text.update();
    }
    public void onDX2B()
    {
        text.setX2B(TypeTool.getBoolean(getValue("DX2B")));
        text.update();
    }
    /**
     * 居左
     */
    public void onALeft()
    {
        text.setAlignment(0);
        text.update();
    }
    /**
     * 居中
     */
    public void onACenter()
    {
        text.setAlignment(1);
        text.update();
    }
    /**
     * 居右
     */
    public void onARight()
    {
        text.setAlignment(2);
        text.update();
    }
    /**
     * 下划线
     */
    public void onIsLine()
    {
        text.setIsLine(TypeTool.getBoolean(getValue("DL")));
        text.update();
    }
    /**
     * 底线
     */
    public void onBottomLine()
    {
        text.setBottomLine(TypeTool.getBoolean(getValue("BOTTOMLINE")));
        text.update();
    }
    /**
     * 多余的不显示
     */
    public void onDelOrder()
    {
        text.setDelOrder(TypeTool.getBoolean(getValue("DEL_ORDER")));
        text.update();
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        text.setPropertyWindow(null);
        return true;
    }
    /**
     * 设置数据组名
     */
    public void onSetGroupName(){
        text.setGroupName(TypeTool.getString(getValue("txtGroupName")));
        text.update();

    }
    /**
     * 设置宏名
     */
    public void onSetMacroName(){
        text.setMicroName(TypeTool.getString(getValue("MACRO_NAME")));
        text.update();

    }
    /**
     * 是否取CDA名称；
     */
    public void onSetTakeCdaName(){
    	System.out.println("==take cdaName=="+TypeTool.getBoolean(getValue("TAKECDANAME")));
        text.setTakeCdaName(TypeTool.getBoolean(getValue("TAKECDANAME")));
        text.update();
    }

    /**
     * 设置CDA对应值；
     */
    public void onSetCdaValue(){
        text.setCdaValue(TypeTool.getString(getValue("CDAVALUE")));
        text.update();
    }

}
