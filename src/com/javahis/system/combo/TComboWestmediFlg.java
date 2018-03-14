package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title:中西医标记下拉列表 </p>
 *
 * <p>Description:中西医标记下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.26
 * @version 1.0
 */
public class TComboWestmediFlg extends TComboBox{
    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object)
    {
        if(object == null)
            return;
        object.setValue("Width","81");
        object.setValue("Height","23");
        object.setValue("Text","TButton");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","中西医");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","GROUP_ID:SYS_WEST_MEDI_FLG");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSDictionaryModule.x";
    }
    public String getModuleMethodName()
    {
        return "getGroupList";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
     /**
     * 得到属性列表
     * @return TAttributeList
     */
    public TAttributeList getAttributes()
    {
        TAttributeList data = new TAttributeList();
        data.add(new TAttribute("Tag","String","","Left"));
        data.add(new TAttribute("Visible","boolean","Y","Center"));
        data.add(new TAttribute("Enabled","boolean","Y","Center"));
        data.add(new TAttribute("Editable","boolean","Y","Center"));
        data.add(new TAttribute("Name","String","","Left"));
        data.add(new TAttribute("Text","String","","Left"));
        data.add(new TAttribute("Tip","String","","Left"));
        data.add(new TAttribute("controlClassName","String","","Left"));
        data.add(new TAttribute("ShowID","boolean","Y","Center"));
        data.add(new TAttribute("ShowName","boolean","N","Center"));
        data.add(new TAttribute("ShowText","boolean","Y","Center"));
        data.add(new TAttribute("ShowValue","boolean","N","Center"));
        data.add(new TAttribute("ShowPy1","boolean","N","Center"));
        data.add(new TAttribute("ShowPy2","boolean","N","Center"));
        data.add(new TAttribute("TableShowList","String","","Left"));
        data.add(new TAttribute("Action","String","","Left"));
        data.add(new TAttribute("SelectedAction","String","","Left"));
        //data.add(new TAttribute("ModuleName","String","","Left"));
        //data.add(new TAttribute("ModuleMethodName","String","","Left"));
        //data.add(new TAttribute("ModuleParmString","String","","Left"));
        //data.add(new TAttribute("ModuleParmTag","String","","Left"));
        //data.add(new TAttribute("ParmMap","String","","Left"));
        data.add(new TAttribute("X","int","0","Right"));
        data.add(new TAttribute("Y","int","0","Right"));
        data.add(new TAttribute("Width","int","0","Right"));
        data.add(new TAttribute("Height","int","0","Right"));
        return data;
    }}
