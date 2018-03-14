package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;
import com.dongyang.data.TParm;

/**
 * <p>Title: 静点区域下拉列表</p>
 *
 * <p>Description: 静点区域下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.17
 * @version 1.0
 */
public class TComboPHLRegion
    extends TComboBox {
    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "TButton");
        object.setValue("showID", "Y");
        object.setValue("showName", "Y");
        object.setValue("showText", "N");
        object.setValue("showValue", "N");
        object.setValue("showPy1", "Y");
        object.setValue("showPy2", "Y");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "静点区域");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName()
    {
        return "phl\\PHLRegionModule.x";
    }
    public String getModuleMethodName()
    {
        return "initCombo";
    }
    /**
     * 执行Module动作
     */
    public void onQuery() {
//        TParm parm = new TParm();
//        String optRegion = Operator.getRegion();
//        if(!"".equals(optRegion))
//            parm.setData("REGION_CODE_ALL",optRegion);
//        setModuleParm(parm);
        super.onQuery();
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
    }
}
