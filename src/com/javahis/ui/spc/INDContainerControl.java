package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 毒麻容器COntrol
 * </p>
 *
 * <p>
 * Description: 毒麻容器COntrol
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: bluecore
 * </p>
 *
 * @author:　liyh 20121015
 * @version 1.0
 */

public class INDContainerControl
    extends TControl {

    private String action = "save";

    // 主项表格
    private TTable table;

    
    public INDContainerControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
    }
    
    
    /**
     * 关闭方法
     */
    public void onClose() {
    	this.messageBox("1");
    }
    
    /**
     * 容器ID回车事件
     */
    public void onContainId(){
    	initParm();
    }
    
    public void onFridId(){
    	String  a = getValueString("FRID_ID");
    	setValue("FRID_ID", a);
    }
    
    /**
     * 主表点击事件
     */
    public void  onTableClicked(){
        int row = table.getTable().getSelectedRow();
        if (row != -1) {//CONTAINER_CODE;CONTAINER_NAME;ORDER_CODE;ORDER_DESC;FRID_ID;ORG_CODE;ORG_NAME;COUNT
        	setValue("CONTAINER_CODE", table.getItemString(row, "CONTAINER_CODE"));
        	setValue("CONTAINER_NAME", table.getItemString(row, "CONTAINER_NAME"));
        	setValue("FRID_ID", table.getItemString(row, "FRID_ID"));
        	setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
        	setValue("ORDER_CODE", table.getItemString(row, "ORDER_CODE"));
        	setValue("ORDER_DESC", table.getItemString(row, "ORDER_DESC"));
        	setValue("COUNT", table.getItemString(row, "COUNT"));
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * 查询方法
     */
    public void onQuery() {
    	initParm();
    }

    /**
     * 清空方法
     */
    public void onClear() {
       table.removeRowAll();
        String tags =
            "ORG_CODE;CONTAINER_CODE;CONTAINER_NAME;FRID_ID;ORDER_CODE;ORDER_DESC;COUNT";
        clearValue(tags);

    }

   

    /**
     * 料位名称回车事件
     */
    public void onMaterialAction() {
        String name = getValueString("MATERIAL_CHN_DESC");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
        ( (TTextField) getComponent("MATERIAL_ENG_DESC")).grabFocus();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
    	table = getTable("TABLE");
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

    }
    
    /**
     * 初始化参数
     */
    public void initParm(){
        TParm tParm = new TParm();
        tParm.setData("CONTAINER_CODE", 0, "A001");
        tParm.setData("CONTAINER_NAME", 0, "芬001");
        tParm.setData("ORDER_CODE", 0, "2N020009");
        tParm.setData("ORDER_DESC", 0, "枸橼酸舒芬太尼注射液");
        tParm.setData("FRID_ID", 0, "");
        tParm.setData("ORG_CODE", 0, "040103");
        tParm.setData("ORG_NAME", 0, "药库");
        tParm.setData("COUNT", 0, "10");
        tParm.setData("CONTAINER_CODE", 1, "A002");
        tParm.setData("CONTAINER_NAME", 1, "芬002");
        tParm.setData("ORDER_CODE", 1, "2N020004");
        tParm.setData("ORDER_DESC", 1, "枸橼酸芬太尼注射液");
        tParm.setData("FRID_ID", 1, "");
        tParm.setData("ORG_CODE", 1, "040103");
        tParm.setData("ORG_NAME", 1, "药库");
        tParm.setData("COUNT", 1, "10");
        tParm.setData("CONTAINER_CODE", 2, "A003");
        tParm.setData("CONTAINER_NAME", 2, "芬003");
        tParm.setData("ORDER_CODE", 2, "2N020006");
        tParm.setData("ORDER_DESC", 2, "盐酸哌替啶注射液");
        tParm.setData("FRID_ID", 2, "");
        tParm.setData("ORG_CODE", 2, "040103");
        tParm.setData("ORG_NAME", 2, "药库");
        tParm.setData("COUNT", 2, "10");
        table.setParmValue(tParm);
    }


    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

  
    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            getTextField("ORDER_CODE").setValue(order_code);
        }
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc)) {
            getTextField("ORDER_DESC").setValue(order_desc);
        }
    }

}
