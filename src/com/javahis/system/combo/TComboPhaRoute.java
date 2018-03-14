package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
*
* <p>Title: 途径下拉列表</p>
*
* <p>Description: 途径下拉列表</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 20081012
* @version 1.0
*/
public class TComboPhaRoute extends TComboBox{


    /**
     * 西药注记
     */
    private String wesmedFlg;
    /**
     * 设置西药注记
     * @param wesmedFlg String
     */
    public void setWesmedFlg(String wesmedFlg){
    	this.wesmedFlg=wesmedFlg;
    }
    /**
     * 得到西药注记
     * @return wesmedFlg String
     */
    public String getWesmedFlg(){
    	return this.wesmedFlg;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("WESMED_FLG",getTagValue(getWesmedFlg()));
        setModuleParm(parm);
        super.onQuery();
    }
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
        object.setValue("Tip","频次");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSPhaRouteModule.x";
    }
    public String getModuleMethodName()
    {
        return "selectdataForCombo";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("WesmedFlg".equalsIgnoreCase(name))
        {
            setWesmedFlg(value);
            getTObject().setValue("WesmedFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("WesmedFlg","String","","Left"));
    }


}
