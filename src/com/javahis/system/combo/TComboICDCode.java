package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title:疾病诊断代码下拉列表 </p>
 *
 * <p>Description:疾病诊断代码下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.26
 * @version 1.0
 */
public class TComboICDCode extends TComboBox{
    /**
     * 中西医
     */
    private String icdType;
    /**
      * 设置中西医
      * @param icdType String
      */
     public void setIcdType(String icdType) {
         this.icdType = icdType;
     }

     /**
      * 得到中西医
      * @return String
      */
     public String getIcdType() {
         return icdType;
     }
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
        parm.setDataN("ICD_TYPE",getTagValue(getIcdType()));
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
         object.setValue("Tip","疾病诊断代码");
         object.setValue("TableShowList","id,text");
         object.setValue("ModuleParmString","");
         object.setValue("ModuleParmTag","");
     }
     public String getModuleName()
     {
         return "sys\\SYSDiagnosisModule.x";
     }
     public String getModuleMethodName()
     {
         return "initcombo";
     }
     public String getParmMap()
     {
         return "id:ID;text:TEXT;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
     }
     /**
      * 增加扩展属性
      * @param data TAttributeList
      */
     public void getEnlargeAttributes(TAttributeList data){
         data.add(new TAttribute("IcdType","String","","Left"));//增加中西医筛选条件
     }
     /**
      * 设置属性
      * @param name String 属性名
      * @param value String 属性值
      */
     public void setAttribute(String name,String value)
     {
         if("IcdType".equalsIgnoreCase(name))
         {
             setIcdType(value);
             getTObject().setValue("IcdType",value);
             return;
         }

         super.setAttribute(name,value);
    }}
