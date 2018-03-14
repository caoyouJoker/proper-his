package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TWord;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EPic;
import com.dongyang.util.TypeTool;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.EFixed;
import java.util.List;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 调试对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.11.27
 * @version 1.0
 */
public class DebugDialogControl extends TControl{
    private TWord word;
    /**
     * 初始化
     */
    public void onInit()
    {
        word = (TWord) getParameter();
        switch(word.getPM().getSexControl())
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
    /**
     * 查找
     */
    public void onFindPic()
    {
        String name = getText("NAME");
        if(name == null || name.length() == 0)
        {
            setValue("VALUE","请输入名称");
            return;
        }
        ETable table = (ETable)word.findObject(name,EComponent.TABLE_TYPE);
        setValue("VALUE","(ETable)word.findObject(\"" + name + "\",EComponent.TABLE_TYPE);\n" + table);
        ETR tr = table.appendTR();
        int count = tr.size();
        for(int i = 0;i < count;i++)
        {
            ETD td = tr.get(i);
            td.setString("A" + i);
        }
        table.setLockEdit(true);
        table.update();
    }
    /**
     * 显示
     */
    public void onVisiblePic()
    {
        word.setVisiblePic(getText("NAME"),TypeTool.getBoolean(getValue("PIC_VISIBLE")));
    }
    public void onO1()
    {
        word.onInsertFileBehindFixed("A1","%ROOT%\\config\\prt","日常病程记录.jhw",1,false);
    }
    public void onO2()
    {
        String name = getText("InsetPort");
        EFixed fixed = word.findFixed("A1");
        if(fixed == null)
            return;
        fixed.setFocusLast();
        //分割
        if(!word.separatePanel())
            return;
        fixed = word.insertFixed(name,name);
        fixed.setActionType("abc");
        word.update();
    }
    public void onO3()
    {
        List list = word.filterFixed("abc");
        for(int i = 0;i < list.size();i++)
        {
            EFixed fixed = (EFixed) list.get(i);
            //System.out.println(fixed.getName());
        }
    }
    public void onO4()
    {
        //word.onInsertFileBehindFixed("APPLEND","%ROOT%\\config\\prt","肠内营养(EN)医嘱单(后续).jhw",1,false);
        this.word.setMicroField("aaa","bbb");
    }
    public void onRALL()
    {
        word.getPM().setSexControl(0);
        word.update();
    }
    public void onRMan()
    {
        word.getPM().setSexControl(1);
        word.update();
    }
    public void onRWoman()
    {
        word.getPM().setSexControl(2);
        word.update();
    }
    public void onFixedTryReset()
    {
        word.fixedTryReset(getText("tTextField_0"),getText("tTextField_1"));
    }
    public void onDeleteTable()
    {
        ETable table = (ETable)word.findObject(getText("tTextField_2"),EComponent.TABLE_TYPE);
        if(table == null)
            return;
        ETable t = table.getNextTable();
        while(t != null)
        {
            t.removeThis();
            t = table.getNextTable();
        }
        while(table.size() > 1)
            table.remove(table.get(table.size() - 1));
        table.setModify(true);
        table.update();
    }
    public void onInsertTR()
    {
        ETable table = (ETable)word.findObject(getText("tTextField_2"),EComponent.TABLE_TYPE);
        if(table == null)
            return;
        int count = TypeTool.getInt(getValue("tTextFormat_0"));
        for(int i = 0;i< count;i ++)
        {
            ETR tr = table.appendTR();
            ETD td = tr.get(0);
            td.setString("" + i);
        }
        table.update();
    }
}
