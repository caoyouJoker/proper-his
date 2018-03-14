package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TComboNode;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title:区域下拉列表 </p>
 *
 * <p>Description: 区域下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.08.25
 * @version 1.0
 */
public class TComboRegion extends TComboBox{
    /**
     * 所有院区
     */
    private boolean allRegionFlg;
    /**
     * 设置所有院区
     * @param allRegionFlg boolean true  增加所有院区 flase 不增加所有院区
     */
    public void setAllRegionFlg(boolean allRegionFlg)
    {
        this.allRegionFlg = allRegionFlg;
    }
    /**
     * 是否是所有院区
     * @return boolean
     */
    public boolean isAllRegionFlg()
    {
        return allRegionFlg;
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
        object.setValue("Tip","区域");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSRegionModule.x";
    }
    public String getModuleMethodName()
    {
        return "initregioncode";
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
        data.add(new TAttribute("AllRegionFlg","String","","Left"));
    }
   /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("AllRegionFlg".equalsIgnoreCase(name))
        {
            setAllRegionFlg(StringTool.getBoolean(value));
            getTObject().setValue("AllRegionFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
    /**
     * 根据需求，插入一条数据，在右边属性栏AllRegionFlg选中为真时显示
     * wangl 2008.09.03
     */
    public void onQuery() {
        super.onQuery();
        TParm parm = new TParm();
        String optRegion = Operator.getRegion();
        if (!"".equals(optRegion))
            parm.setData("REGION_CODE_ALL", optRegion);
        setModuleParm(parm);
        super.onQuery();
        if (isAllRegionFlg()) {
            TComboNode node = new TComboNode();
            node.setID("ALL");
            node.setName("全部院区");
            getModel().getItems().add(1, node);
        }

    }
}
