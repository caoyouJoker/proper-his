package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: 计量单位下拉列表</p>
 *
 * <p>Description: 计量单位下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.2.4
 * @version 1.0
 */
public class TComboSYSUnit extends TComboBox{
    /**
     * 药品用量单位注记
     */
    private String mediFlg;
    /**
     * 处置单位注记
     */
    private String disposeFlg;
    /**
     * 整包装注记
     */
    private String packageFlg;
    /**
     * 其他注记
     */
    private String otherFlg;
    /**
     * 设置药品用量单位注记
     * @param mediFlg String
     */
    public void setMediFlg(String mediFlg)
    {
        this.mediFlg = mediFlg;
    }
    /**
     * 得到药品用量单位注记
     * @return String
     */
    public String getMediFlg()
    {
        return mediFlg;
    }
    /**
     * 设置处置单位注记
     * @param disposeFlg String
     */
    public void setDisposeFlg(String disposeFlg){
        this.disposeFlg = disposeFlg;
    }
    /**
     * 得到处置单位注记
     * @return String
     */
    public String getDisposeFlg(){
        return disposeFlg;
    }
    /**
     * 设置整包装注记
     * @param packageFlg String
     */
    public void setPackageFlg(String packageFlg){
        this.packageFlg = packageFlg;
    }
    /**
     * 得到整包装注记
     * @return String
     */
    public String getPackageFlg(){
        return packageFlg;
    }
    /**
     * 设置其他注记
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg){
        this.otherFlg = otherFlg;
    }
    /**
     * 得到其他注记
     * @return String
     */
    public String getOtherFlg(){
        return otherFlg;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("MEDI_FLG",getTagValue(getMediFlg()));
        parm.setDataN("DISPOSE_FLG",getTagValue(getDisposeFlg()));
        parm.setDataN("PACKAGE_FLG",getTagValue(getPackageFlg()));
        parm.setDataN("OTHER_FLG",getTagValue(getOtherFlg()));
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
        object.setValue("Tip","计量单位");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSUnitModule.x";
    }
    public String getModuleMethodName()
    {
        return "initSYSUnit";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("MediFlg","String","","Left"));
        data.add(new TAttribute("DisposeFlg","String","","Left"));
        data.add(new TAttribute("PackageFlg","String","","Left"));
        data.add(new TAttribute("OtherFlg","String","","Left"));


    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("MediFlg".equalsIgnoreCase(name))
        {
            setMediFlg(value);
            getTObject().setValue("MediFlg",value);
            return;
        }
        if("DisposeFlg".equalsIgnoreCase(name))
        {
            setDisposeFlg(value);
            getTObject().setValue("DisposeFlg",value);
            return;
        }
        if("PackageFlg".equalsIgnoreCase(name))
        {
            setPackageFlg(value);
            getTObject().setValue("PackageFlg",value);
            return;
        }
        if("OtherFlg".equalsIgnoreCase(name))
        {
            setOtherFlg(value);
            getTObject().setValue("OtherFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
