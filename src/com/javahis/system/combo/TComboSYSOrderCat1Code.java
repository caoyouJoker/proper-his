package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: 医嘱分类下拉列表</p>
 *
 * <p>Description: 医嘱分类下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSOrderCat1Code extends TComboBox{
    /**
     * 医嘱细分类群组
     */
    private String cat1Type;
    /**
     * 设置医嘱细分类群组
     * @param cat1Type String
     */
    public void setCat1Type(String cat1Type){
        this.cat1Type = cat1Type;
    }
    /**
     * 得到医嘱细分类群组
     * @return String
     */
    public String getCat1Type(){
        return cat1Type;
    }
    /**
    * 执行Module动作
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       parm.setDataN("CAT1_TYPE",getTagValue(getCat1Type()));
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
       object.setValue("Tip","医嘱分类");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSOrderCAT1Module.x";
   }
   public String getModuleMethodName()
   {
       return "initOrderCat1Code";
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
       data.add(new TAttribute("Cat1Type","String","","Left"));//增加医嘱细分类群组筛选条件
   }
   /**
    * 设置属性
    * @param name String 属性名
    * @param value String 属性值
    */
   public void setAttribute(String name,String value)
   {
       if("Cat1Type".equalsIgnoreCase(name))
       {
           setCat1Type(value);
           getTObject().setValue("Cat1Type",value);
           return;
       }
       super.setAttribute(name,value);
   }
}
