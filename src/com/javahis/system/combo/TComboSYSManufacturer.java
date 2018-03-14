package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: 生产厂商下拉列表</p>
 *
 * <p>Description: 生产厂商下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSManufacturer extends TComboBox{
    /**
     * 药品供货商注记
     */
    private String phaFlg;
    /**
     * 低值耗材供货商注记
     */
    private String matFlg;
    /**
     * 设备供货商注记
     */
    private String devFlg;
    /**
     * 其它供应厂商注记
     */
    private String otherFlg;
    /**
     * 初始化
     */
    public void onInit()
    {
        super.onInit();
    }
    /**
    * 执行Module动作
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       parm.setDataN("PHA_FLG",getTagValue(getPhaFlg()));
       parm.setDataN("MAT_FLG",getTagValue(getMatFlg()));
       parm.setDataN("DEV_FLG",getTagValue(getDevFlg()));
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
       object.setValue("Tip","生产厂商");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSManufacturer.x";
   }
   public String getModuleMethodName()
   {
       return "initSYSManufacturerCode";
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
       data.add(new TAttribute("PhaFlg","String","","Left"));
       data.add(new TAttribute("MatFlg","String","","Left"));
       data.add(new TAttribute("DevFlg","String","","Left"));
       data.add(new TAttribute("OtherFlg","String","","Left"));
}
   /**
    * 设置属性
    * @param name String 属性名
    * @param value String 属性值
    */
   public void setAttribute(String name,String value)
   {
       if("PhaFlg".equalsIgnoreCase(name))
       {
           setPhaFlg(value);
           getTObject().setValue("PhaFlg",value);
           return;
       }
       if("MatFlg".equalsIgnoreCase(name))
       {
           setMatFlg(value);
           getTObject().setValue("MatFlg",value);
           return;
       }
       if("DevFlg".equalsIgnoreCase(name))
       {
           setDevFlg(value);
           getTObject().setValue("DevFlg",value);
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
   /**
    * 得到设备供货商注记
    * @return String
    */
   public String getDevFlg() {
        return devFlg;
    }
    /**
     * 得到低值耗材供货商注记
     * @return String
     */
    public String getMatFlg() {
        return matFlg;
    }
    /**
     * 得到其它供应厂商注记
     * @return String
     */
    public String getOtherFlg() {
        return otherFlg;
    }
    /**
     * 得到药品供货商注记
     * @return String
     */
    public String getPhaFlg() {
        return phaFlg;
    }
    /**
     * 设置设备供货商注记
     * @param devFlg String
     */
    public void setDevFlg(String devFlg) {
        this.devFlg = devFlg;
    }
    /**
     * 设置低值耗材供货商注记
     * @param matFlg String
     */
    public void setMatFlg(String matFlg) {
        this.matFlg = matFlg;
    }
    /**
     * 设置其它供应厂商注记
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
    }
    /**
     * 设置药品供货商注记
     * @param phaFlg String
     */
    public void setPhaFlg(String phaFlg) {
        this.phaFlg = phaFlg;
    }
}
