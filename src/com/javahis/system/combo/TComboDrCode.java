package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
/**
 *
 * <p>Title:人员下拉列表 </p>
 *
 * <p>Description:人员下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.04
 * @version 1.0
 */
public class TComboDrCode extends TComboBox{
    /**
     * 科室
     */
    private String dept;
    /**
     * 设置科室
     * @param dept String
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * 得到科室
     * @return String
     */
    public String getDept() {
        return dept;
    }
    /**
    * 执行Module动作
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       if(getDept() != null && getDept().length() > 0)
           parm.setData("DEPT_CODE",getDept());
       String optRegion = Operator.getRegion();
       if(!"".equals(optRegion))
           parm.setData("REGION_CODE_ALL",optRegion);
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
        object.setValue("Tip","人员");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSOperatorModule.x";
    }
    public String getModuleMethodName()
    {
        return "initoperatorcode";
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
        data.add(new TAttribute("Dept","String","","Left"));//增加科室筛选条件
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("Dept".equalsIgnoreCase(name))
        {
            setDept(value);
            getTObject().setValue("Dept",value);
            return;
        }

        super.setAttribute(name,value);
    }
}
