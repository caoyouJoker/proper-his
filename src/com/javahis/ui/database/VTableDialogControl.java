package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.div.VTable;
import com.dongyang.util.TypeTool;
import com.dongyang.tui.text.div.MV;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.tui.text.div.DIV;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.tui.text.div.VLine;
import com.dongyang.tui.text.div.VText;
import java.awt.Color;
import com.dongyang.tui.text.div.VPic;
import com.dongyang.tui.text.div.VBarCode;

/**
 *
 * <p>Title: 表格属性对话框控制类</p>
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
public class VTableDialogControl extends TControl{
    private VTable table;
    private TTable divTable;
    /**
     * 初始化
     */
    public void onInit()
    {
        table = (VTable)getParameter();
        divTable = (TTable)getComponent("DIV_TABLE");
        setValue("NAME",table.getName());
        setValue("DX",table.getX());
        setValue("DY",table.getY());
        setValue("DWidth",table.getWidth());
        setValue("DHeight",table.getHeight());
        setValue("V_CB",table.isVisible());
        setValue("DX1B",table.isX1B());
        setValue("DY1B",table.isY1B());
        setValue("DX2B",table.isX2B());
        setValue("DY2B",table.isY2B());
        switch(table.getShowPage())
        {
            case 0:
               setValue("SHOW_ALL",true) ;
               break;
            case 1:
                setValue("SHOW_ONE",true) ;
                break;
            case 2:
                setValue("SHOW_LAST",true) ;
                break;
        }
        //初始化成员
        initMv();
        divTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onCheckDIV");
    }
    /**
     * 初始化成员
     */
    public void initMv()
    {
        MV mv = table.getMV();
        TParm parm = new TParm();
        if(mv.size() == 0)
        {
            parm.setData("ID",new Vector());
            parm.setData("SHOW",new Vector());
            parm.setData("NAME",new Vector());
            parm.setData("TYPE",new Vector());
            parm.setCount(0);
        }else
        {
            for (int i = 0; i < mv.size(); i++) {
                DIV div = mv.get(i);
                parm.addData("ID",i);
                parm.addData("SHOW",div.isVisible());
                parm.addData("NAME",div.getName());
                parm.addData("TYPE",div.getType());
            }
            parm.setCount(mv.size());
        }
        divTable.setParmValue(parm);
    }
    /**
     * 设置名称
     */
    public void onName()
    {
        table.setName(getText("NAME"));
        table.update();
        table.modify("NAME");
    }
    /**
     * 设置x
     */
    public void onDX()
    {
        table.setX(TypeTool.getInt(getValue("DX")));
        table.update();
    }
    /**
     * 设置y
     */
    public void onDY()
    {
        table.setY(TypeTool.getInt(getValue("DY")));
        table.update();
    }
    /**
     * 设置宽度
     */
    public void onDWidth()
    {
        table.setWidth(TypeTool.getInt(getValue("DWIDTH")));
        table.update();
    }
    /**
     * 设置高度
     */
    public void onDHeight()
    {
        table.setHeight(TypeTool.getInt(getValue("DHEIGHT")));
        table.update();
    }
    /**
     * 显示
     */
    public void onVCB()
    {
        table.setVisible(TypeTool.getBoolean(getValue("V_CB")));
        table.update();
        table.modify("VISIBLE");
    }
    /**
     * 修改参数
     * @param name String
     */
    public void modify(String name)
    {
        if("NAME".equals(name))
        {
            setValue("NAME",table.getName());
            return;
        }
        if("VISIBLE".equals(name))
        {
            setValue("V_CB",table.isVisible());
            return;
        }
    }
    /**
     * 修改参数
     * @param mv MV
     * @param div DIV
     * @param name String
     */
    public void modify(MV mv,DIV div,String name)
    {
        MV showmv = table.getMV();
        if(showmv != mv)
            return;
        int index = mv.indexOf(div);
        if(index == -1)
            return;
        if("NAME".equals(name))
        {
            divTable.setItem(index,"NAME",div.getName());
            return;
        }
        if("VISIBLE".equals(name))
        {
            divTable.setItem(index,"SHOW",div.isVisible());
            return;
        }
    }
    /**
     * 双击组件
     */
    public void onDivDoubleClick()
    {
        MV mv = table.getMV();
        int row = divTable.getSelectedRow();
        DIV div = mv.get(row);
        div.openProperty();
    }
    /**
     * 点击checkbox
     * @param obj Object
     */
    public void onCheckDIV(Object obj)
    {
        TTable t=(TTable)obj;
        t.acceptText();
        int row = t.getSelectedRow();
        int column = t.getSelectedColumn();
        String columnName = t.getParmMap(column);
        boolean value = t.getParmValue().getBoolean(columnName,row);
        if("SHOW".equals(columnName))
            table.getMV().get(row).setVisible(value);
        table.update();
        table.getMV().get(row).propertyModify("VISIBLE");
    }
    /**
     * 新建DIV
     */
    public void onNewDIV()
    {
        MV mv = table.getMV();
        Object[] result = (Object[])openDialog("%ROOT%\\config\\database\\CreateDIVDialog.x",mv);
        if(result == null)
            return;
        switch(TypeTool.getInt(result[0]))
        {
            case 1://直线
                VLine line = mv.addLine(100,100,200,100,new Color(0,0,0));
                line.setName(TypeTool.getString(result[1]));
                line.openProperty();
                break;
            case 2://表格
                VTable table = mv.addTable(100,100,300,300);
                table.setName(TypeTool.getString(result[1]));
                table.openProperty();
                break;
            case 3://文字
                VText text = mv.addText(100,100,new Color(0,0,0));
                text.setName(TypeTool.getString(result[1]));
                text.setText("文字");
                text.openProperty();
                break;
            case 4://图片
                VPic pic = mv.addPic(100,100,300,300);
                pic.setName(TypeTool.getString(result[1]));
                pic.openProperty();
                break;
            case 5://条码
                VBarCode barCode = mv.addBarCode(100,100);
                barCode.setName(TypeTool.getString(result[1]));
                barCode.openProperty();
                break;
        }
        initMv();
    }
    /**
     * 删除DIV
     */
    public void onDeleteDIV()
    {
        MV mv = table.getMV();
        int row = divTable.getSelectedRow();
        if(row == -1)
            return;
        DIV div = mv.get(row);
        if(messageBox("提示信息","是否删除组件" + div.getName() + "?",YES_NO_OPTION) == 1)
            return;
        mv.remove(div);
        divTable.removeRow(row);
        if(divTable.getRowCount() > 0)
            divTable.setSelectedRow(row >= divTable.getRowCount()?divTable.getRowCount() - 1:row);
        table.update();
        div.DC();
    }
    /**
     * 全部页显示
     */
    public void onShowAll()
    {
        table.setShowPage(0);
        table.update();
    }
    /**
     *  首页显示
     */
    public void onShowOne()
    {
        table.setShowPage(1);
        table.update();
    }
    /**
     * 尾页显示
     */
    public void onShowLast()
    {
        table.setShowPage(2);
        table.update();
    }
    public void onDX1B()
    {
        table.setX1B(TypeTool.getBoolean(getValue("DX1B")));
        table.update();
    }
    public void onDY1B()
    {
        table.setY1B(TypeTool.getBoolean(getValue("DY1B")));
        table.update();
    }
    public void onDX2B()
    {
        table.setX2B(TypeTool.getBoolean(getValue("DX2B")));
        table.update();
    }
    public void onDY2B()
    {
        table.setY2B(TypeTool.getBoolean(getValue("DY2B")));
        table.update();
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        table.setPropertyWindow(null);
        return true;
    }
}
