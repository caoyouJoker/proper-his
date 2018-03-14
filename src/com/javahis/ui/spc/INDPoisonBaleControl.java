package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Map;

import org.jaxen.function.FalseFunction;

import sun.reflect.generics.tree.Tree;

import jdo.ind.INDSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.javahis.manager.IndVerifyinObserver;
import com.javahis.util.StringUtil;
import com.sun.media.rtp.TrueRandom;

import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 毒麻药赋码及打包Control
 * </p>
 *
 * <p>
 * Description: 毒麻药赋码及打包Control
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
 * @author liyh 2012.10.10
 * @version 1.0
 */

public class INDPoisonBaleControl
    extends TControl {



    public INDPoisonBaleControl() {
        super();
    }
    
    private TTable table_m;

    private TTable table_d;


    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
    }

    /**
     * 清空方法
     */
    public void onClear() {
   /*     getRadioButton("UPDATE_FLG_B").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;IND_DISPENSE_NO;OPT_USER;FRID_CODE;FRID_DESC";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // 画面状态
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(true);
        getTable("TABLE_M").removeRowAll();
        getTable("TABLE_D").removeRowAll();*/
    }

    /**
     * 保存方法
     */
    public void onSave() {
       String fridCode = getValueString("FRID_CODE");
       if(null == fridCode || "".equals(fridCode)){
    	   this.messageBox("请选择容器");
       }else{
    	   this.messageBox("打包成功");
       }
    }




    /**
     * 初始画面数据
     */
    private void initPage() {
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    	 // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            //((TextFormatINDOrg)this.getComponent("ORG_CODE")).o
            //getTextFormat("ORG_CODE")
//            if (parm.getCount("NAME") > 0) {
//                getComboBox("ORG_CODE").setSelectedIndex(1);
//            }
        }

        TParm parm = new TParm();
        // 设置弹出菜单
        getTextField("FRID_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\ind\\INDPoisonBaleSelectFrid2.x"),
            parm);
        // 定义接受返回值方法
        getTextField("FRID_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        initParamenter();
    }
    
    /**
     * 插入模版片语
     */
    public void onInsertTemplatePY() {
	    TParm inParm = new TParm();
	    inParm.setData("FRID_CODE",    getTextField("FRID_CODE"));
	    inParm.addListener( TPopupMenuEvent.RETURN_VALUE, this,
	    		 "popReturn");
	    TWindow window = (TWindow) this.openWindow(
	    		"%ROOT%\\config\\ind\\INDPoisonBaleSelectFrid.x", inParm,
	    true);
        // 定义接受返回值方法
        getTextField("FRID_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
	    window.setX(ImageTool.getScreenWidth() - window.getWidth());
	    window.setY(30);
	    window.setVisible(true);

    }
    
    /**
     * 初始化参数
     */
    public void initParamenter() {
    	//SELECT_FLG;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;PRICE;VALID_DATE;BATCH_NO;BAR_CODE
        TParm tParm = new TParm();
        tParm.setData("SELECT_FLG", 0, false);
        tParm.setData("ORDER_DESC", 0, "盐酸吗啡注射液");
        tParm.setData("SPECIFICATION", 0, "10mg");
        tParm.setData("DOSAGE_QTY", 0, "1");
        tParm.setData("UNIT_CHN_DESC", 0, "支");
        tParm.setData("PRICE", 0, "100.1");
        tParm.setData("VALID_DATE", 0, "2013-10-10");
        tParm.setData("BATCH_NO", 0, "121010");
        tParm.setData("BAR_CODE", 0, "A00001");
        
        tParm.setData("SELECT_FLG", 1, false);
        tParm.setData("ORDER_DESC", 1, "枸橼酸芬太尼注射液");
        tParm.setData("SPECIFICATION", 1, "2ml:0.1mg");
        tParm.setData("DOSAGE_QTY", 1, "1");
        tParm.setData("UNIT_CHN_DESC", 1, "支");
        tParm.setData("PRICE", 1, "200");
        tParm.setData("VALID_DATE", 1, "2013-10-10");
        tParm.setData("BATCH_NO", 1, "121010");
        tParm.setData("BAR_CODE", 1, "A00001");

        tParm.setData("SELECT_FLG", 2, false);
        tParm.setData("ORDER_DESC", 2, "枸橼酸芬太尼注射液");
        tParm.setData("SPECIFICATION", 2, "2ml:0.5mg");
        tParm.setData("DOSAGE_QTY", 2, "1");
        tParm.setData("UNIT_CHN_DESC", 2, "支");
        tParm.setData("PRICE", 2, "200");
        tParm.setData("VALID_DATE", 2, "2013-10-10");
        tParm.setData("BATCH_NO", 2, "121010");
        tParm.setData("BAR_CODE", 2, "A00001");
        
        tParm.setData("SELECT_FLG", 3, false);
        tParm.setData("ORDER_DESC", 3, "盐酸哌替啶注射液");
        tParm.setData("SPECIFICATION", 3, "50mg");
        tParm.setData("DOSAGE_QTY", 3, "1");
        tParm.setData("UNIT_CHN_DESC", 3, "支");
        tParm.setData("PRICE", 3, "300");
        tParm.setData("VALID_DATE", 3, "2013-10-10");
        tParm.setData("BATCH_NO", 3, "121010");
        tParm.setData("BAR_CODE", 3, "A00001");
        
        table_m.setParmValue(tParm);
        
    }
    /**
     * 初始化参数
     */
    public void initParamenter2() {
    	
        //ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;PRICE;VALID_DATE;BATCH_NO;FRID_ID;FRID_CODE;FRID_DESC
        TParm tParm1 = new TParm();
        tParm1.setData("ORDER_DESC", 0, "盐酸吗啡注射液");
        tParm1.setData("SPECIFICATION", 0, "10mg");
        tParm1.setData("DOSAGE_QTY", 0, "1");
        tParm1.setData("UNIT_CHN_DESC", 0, "支");
        tParm1.setData("PRICE", 0, "100.1");
        tParm1.setData("VALID_DATE", 0, "2013-10-10");
        tParm1.setData("BATCH_NO", 0, "121010");
        tParm1.setData("FRID_ID", 0, "1");
        tParm1.setData("FRID_CODE", 0, "A00001");
        tParm1.setData("FRID_DESC", 0, "容器1");
      
        tParm1.setData("ORDER_DESC", 1, "枸橼酸芬太尼注射液");
        tParm1.setData("SPECIFICATION", 1, "2ml:0.1mg");
        tParm1.setData("DOSAGE_QTY", 1, "1");
        tParm1.setData("UNIT_CHN_DESC", 1, "支");
        tParm1.setData("PRICE", 1, "200");
        tParm1.setData("VALID_DATE", 1, "2013-10-10");
        tParm1.setData("BATCH_NO", 1, "121010");
        tParm1.setData("FRID_ID", 1, "2");
        tParm1.setData("FRID_CODE", 1, "A00002");
        tParm1.setData("FRID_DESC", 1, "容器2");

        tParm1.setData("ORDER_DESC", 2, "枸橼酸芬太尼注射液");
        tParm1.setData("SPECIFICATION", 2, "2ml:0.5mg");
        tParm1.setData("DOSAGE_QTY", 2, "1");
        tParm1.setData("UNIT_CHN_DESC", 2, "支");
        tParm1.setData("PRICE", 2, "200");
        tParm1.setData("VALID_DATE", 2, "2013-10-10");
        tParm1.setData("BATCH_NO", 2, "121010");
        tParm1.setData("FRID_ID", 2, "3");
        tParm1.setData("FRID_CODE", 2, "A00003");
        tParm1.setData("FRID_DESC", 2, "容器3");

        table_d.setParmValue(tParm1);
    }
    
    public void onChangeTTabbedPane(){
    	  if (getPanel("tPanel_3").isShowing()){
    		  ( (TMenuItem) getComponent("save")).setEnabled(true);
    		  initParamenter();
    	  }    else if (getPanel("tPanel_5").isShowing()) {
    		  ( (TMenuItem) getComponent("save")).setEnabled(false);
    		  initParamenter2();
    	  }
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        System.out.println("----22222----parm:"+parm);
        String order_code = parm.getValue("FRID_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("FRID_CODE").setValue(order_code);
        String order_desc = parm.getValue("FRID_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("FRID_DESC").setValue(order_desc);

    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableMCheckBoxClicked(Object obj) {
       this.messageBox("123");
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
     * 得到TPanel对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
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
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
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

   
   
}
