package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: 插入元素对话框</p>
 *
 * <p>Description:插入元素对话框 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS</p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class InsertElementDialogControl extends TControl
{
    private TTextFormat ftElemCategory;
    private TTextFormat ftElem;
    private TComboBox  cmbMacro;
    private TComboBox  cmbComponentType;

    private TTextField txtDocumentType;

    public InsertElementDialogControl() {
    }

    public void onInit()
    {
        //取文档类型参数，赋值;
        Object obj=this.getParameter();
        String documentType=((TParm) obj).getValue("DOCUMENT_TYPE");
        txtDocumentType=(TTextField)getComponent("txt_document_type");
        //this.messageBox("name+++"+txtDocumentType.getName());
        txtDocumentType.setValue(documentType);

        ftElemCategory=(TTextFormat)getComponent("tf_elemCategory");
        ftElem = (TTextFormat)getComponent("tf_elem");
        cmbMacro=(TComboBox)getComponent("cmb_macro");
        cmbComponentType=(TComboBox)getComponent("cmb_componenttype");
        //初始化控件；
        cmbComponentType.setStringData("[[id,name],[,],[3,固定文本],[4,单选],[5,多选],[6,有无选择],[7,输入提示],[8,输入宏],[9,抓取对象],[10,选择框],[14,数字],[15,下拉框]]");

    }
    /**
     *宏查询
     */
    public void macroQuery(){
        String elemData=(String)ftElem.getValue();
        //控件是宏或者抓取框
        //通过EMR_MICRO_CONVERT表取宏名称；
       cmbMacro.setSQL("SELECT MICRO_NAME FROM EMR_MICRO_CONVERT WHERE DATA_ELEMENT_CODE='" + elemData + "'");
       //System.out.println("sql============"+"SELECT MICRO_NAME,MICRO_NAME FROM EMR_MICRO_CONVERT WHERE DATA_ELEMENT_CODE='" + elemData + "'");
       cmbMacro.retrieve();
        //如果大于0
        //this.messageBox("==count=="+cmbMacro.getDataStore().rowCount());
        //不是宏
        if(cmbMacro.getDataStore().rowCount()==0){
            cmbMacro.setValue("");
            cmbMacro.setEnabled(false);

        }else if(cmbMacro.getDataStore().rowCount()>1){
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");

        }else{
            cmbMacro.setEnabled(true);
            cmbMacro.setValue("");
            cmbMacro.setSelectedIndex(1);
        }

        //设置默认控件；
        String sql="SELECT COMPONENT_TYPE FROM EMR_CLINICAL_DATAGROUP WHERE 1=1";
            sql+=" AND GROUP_CODE='"+ftElemCategory.getValue()+"'";
            sql+=" AND DATA_CODE='"+ftElem.getValue()+"'";
            TParm returnParm=new TParm(this.getDBTool().select(sql));
            cmbComponentType.setValue(returnParm.getValue("COMPONENT_TYPE",0));

    }

    /**
     * 确定功能
     */
    public void onOk()
    {
        if(cmbMacro.isEnabled()&&cmbMacro.getValue().equals("")){
            //this.messageBox("请选择宏!");
            //return;
        }

        if(ftElemCategory.getValue()==null  ||ftElem.getValue() == null)
        {
            this.messageBox("请选择元素分类或元素数据!");
            return;
        }else{
            //取对应的数据库值，并传回主窗体；
            String sql="SELECT * FROM EMR_CLINICAL_DATAGROUP WHERE 1=1";
            sql+=" AND GROUP_CODE='"+ftElemCategory.getValue()+"'";
            sql+=" AND DATA_CODE='"+ftElem.getValue()+"'";
            //System.out.println("======sql====="+sql);
            TParm returnParm=new TParm(this.getDBTool().select(sql));
            returnParm.addData("MACRO_NAME",cmbMacro.getValue());
            returnParm.addData("SELECTED_COMPONENT_TYPE",cmbComponentType.getValue());

            //System.out.println("returnParm====="+returnParm);
            //this.messageBox("aa"+returnParm.getCount());
            if(returnParm.getCount()<0){
                this.messageBox("无数据源信息！");
                return;
            }

            this.setReturnValue(returnParm);
            this.closeWindow();


        }


    }
    /**
     * 取消关闭功能
     */
    public void onCancel()
    {
        closeWindow();
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }




}
