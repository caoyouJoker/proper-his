package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

public class INDDispenseAssignWithLiquidControl  extends TControl {


    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    // 主项表格选中行
    private int row_m;

    // 细项表格选中行
    private int row_d;

    // 细项序号
    private int seq;

    // 返回结果集
    private TParm resultParm;



    // 单据类型
    private String request_type;

    // 申请单号
    private String request_no;

    // 使用单位
    private String u_type;

    // 出库部门
    private String out_org_code;

    // 入库部门
    private String in_org_code;

    // 是否出库
    private boolean out_flg;

    // 是否入库
    private boolean in_flg;

    // 入库单号
    private String dispense_no;

    // 全院药库部门作业单据
    private boolean request_all_flg = true;

    public INDDispenseAssignWithLiquidControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
/*    	TFrame tFrame  = (TFrame)getComponent("UI");
    	final TTextField mrField = (TTextField)getComponent("ASSIGN_CODE");
    	tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
    	            public void windowOpened(java.awt.event.WindowEvent evt) {
    	             mrField.requestFocus();
    	            }
    	});*/
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table_m.setParmValue(iniParmM());
    }

    /**
     * 清空方法
     */
    public void onClear() {
       setValue("ASSIGN_CODE", "");
       setValue("BIG_BOX_CODE", "");
       setValue("REGION_CODE", "");
       setValue("BOTTLE_CODE", "");
       table_d.removeRowAll();
       table_m.removeRowAll();
    }

    /**
     * 保存方法
     */
    public void onSave() {

    }
    
    /**
     * 核对瓶签 
     */
    public void onBottleCode(){
    	TParm parm = table_m.getParmValue();
    	if(null == parm && parm.getCount()<1){
    		this.messageBox("主档数据不能为空");
    	}
    	String bottleCode = getValueString("BOTTLE_CODE");
    	TTextField BOTTLE_CODE = (TTextField) getComponent("BOTTLE_CODE");
    	BOTTLE_CODE.select(0,bottleCode.length());
    	
     	String a = getValueString("BIG_BOX_CODE");
    	setValue("BIG_BOX_CODE",a);
    	for(int i =  0 ; i < parm.getCount(); i++){
    		String remarkStr = (String) parm.getData("REMARK", i);
    		if(null == remarkStr || "".equals(remarkStr)){
    			table_m.setItem(i, "REMARK", "是");
    			break;
    		}
    	}
    }
    /**
     * 保存方法
     */   
    public void onAssignCode(){
    	String assignCode = getValueString("ASSIGN_CODE");
    	if(null == assignCode || "".equals(assignCode)){
    		this.messageBox("配药单号不能为空");
    		return ;
    	}
//    	 ( (TTextField) getComponent("BIG_BOX_CODE")).grabFocus();
    	this.setValue("REGION_CODE", "1病区");
    	 table_m.setParmValue(iniParmM());
    	 table_m.setSelectedRow(0);
    }
    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        row_d = table_m.getSelectedRow();
        TParm a = getDispenseDParm(new TParm());
        table_d.setParmValue(a);
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
  
    }
        
	/**
	 * 明细表格(TABLE_D)单击事件
	 */
	public void onClickM() {
		table_m.acceptText();
		int i = table_m.getSelectedRow();
//		table_m.setSelectedRow(i+1);
		if (i == 0) {
			//table_m.setItem(i, "REMARK", "是");
			table_m.setItem(i, "BOXCODE", "01号盒");
			table_m.setSelectedRow(1);
		}
		if (i == 1) {
//			table_m.setItem(i, "BOXCODE", "02号盒");
			table_m.setSelectedRow(2);
		}
		if (i == 2) {
//			table_m.setItem(i, "BOXCODE", "03号盒");
			table_m.setSelectedRow(3);
		}
		if (i == 3) {
//			table_m.setItem(i, "BOXCODE", "04号盒");
			table_m.setSelectedRow(4);
		}
		if (i == 4) {
//			table_m.setItem(i, "BOXCODE", "05号盒");

		}
	
	}

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
    }




    /**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm iniParmM() {
        // 药库参数信息
        TParm parmM = new TParm();
        //        MR_NO;ORG_CODE;BED_NO;PATION_NAME;SEX;AGE;ALL_ORDER_NAME;TIMERS;BOX_CODE;REMARK
        parmM.setData("MR_NO",0, "000000385144");
        parmM.setData("ORG_CODE",0, "内一科");
        parmM.setData("BED_NO", 0,"0876");
        parmM.setData("PATION_NAME",0, "王四");
        parmM.setData("SEX",0, "男");
        parmM.setData("AGE", 0,"78");
        parmM.setData("ALL_ORDER_NAME",0, "静脉营养液1");
        parmM.setData("TIMERS",0, "09:00");
        parmM.setData("BOX_CODE",0, "");
        parmM.setData("REMARK",0, "");
      
        parmM.setData("MR_NO",1, "000000385144");
        parmM.setData("ORG_CODE",1, "内一科");
        parmM.setData("BED_NO", 1,"0876");
        parmM.setData("PATION_NAME",1, "王四");
        parmM.setData("SEX",1,"男");
        parmM.setData("AGE", 1,"78");
        parmM.setData("ALL_ORDER_NAME",1, "静脉营养液2");
        parmM.setData("TIMERS",1, "13:00");
        parmM.setData("BOXCODE",1, "");
        parmM.setData("REMARK",1, "");
        
        parmM.setData("MR_NO",2, "000000385144");
        parmM.setData("ORG_CODE",2, "内一科");
        parmM.setData("BED_NO", 2,"2876");
        parmM.setData("PATION_NAME",2, "王四");
        parmM.setData("SEX",2, "男");
        parmM.setData("AGE", 2,"78");
        parmM.setData("ALL_ORDER_NAME",2, "静脉营养液3");
        parmM.setData("TIMERS",2, "18:00");
        parmM.setData("BOXCODE",2, "");
        parmM.setData("REMARK",2, "");
        
        parmM.setData("MR_NO",3, "000000385236");
        parmM.setData("ORG_CODE",3, "内一科");
        parmM.setData("BED_NO", 3,"3873");
        parmM.setData("PATION_NAME",3, "张三");
        parmM.setData("SEX",3, "男");
        parmM.setData("AGE", 3,"88");
        parmM.setData("ALL_ORDER_NAME",3, "静脉营养液1");
        parmM.setData("TIMERS",3, "13：00");
        parmM.setData("BOXCODE",3, "");
        parmM.setData("REMARK",3, "");
        
        parmM.setData("MR_NO",4, "000000385287");
        parmM.setData("ORG_CODE",4, "内一科");
        parmM.setData("BED_NO", 4,"4876");
        parmM.setData("PATION_NAME",4, "张英");
        parmM.setData("SEX",4, "女");
        parmM.setData("AGE", 4,"77");
        parmM.setData("ALL_ORDER_NAME",4, "静脉营养液1");
        parmM.setData("TIMERS",4, "09:00");
        parmM.setData("BOXCODE",4, "");
        parmM.setData("REMARK",4, "");
        parmM.setCount(5);
        return parmM;
    }

    /**
     * 获得明细信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        //ORDER_DESC;SPECIFICATION;QTY;UNIT;BATCH_NO;VALID_DATE;PRICE
        parmD.setData("ORDER_DESC",0, "生理氯化钠盐水");
        parmD.setData("SPECIFICATION", 0,"500ml");
        parmD.setData("QTY",0, "1");
        parmD.setData("UNIT",0, "瓶");
        parmD.setData("BATCH_NO", 0,"2012042");
        parmD.setData("VALID_DATE",0, "2013-10-10");
        parmD.setData("PRICE",0, "12.00");
 
        parmD.setData("ORDER_DESC",1, "葡萄糖注射液");
        parmD.setData("SPECIFICATION", 1,"10%250ML");
        parmD.setData("QTY",1, "1");
        parmD.setData("UNIT",1, "袋");
        parmD.setData("BATCH_NO", 1,"2112342");
        parmD.setData("VALID_DATE",1, "2013-11-11");
        parmD.setData("PRICE",1, "14.00");
        
        parmD.setData("ORDER_DESC",2, "葡萄糖注射液");
        parmD.setData("SPECIFICATION", 2,"5%150ML");
        parmD.setData("QTY",2, "2");
        parmD.setData("UNIT",2, "瓶");
        parmD.setData("BATCH_NO", 2,"20130101");
        parmD.setData("VALID_DATE",2, "2013-12-22");
        parmD.setData("PRICE",2, "24.00");
        return parmD;
    }


    public void onBigBoxCode(){
    	String a = getValueString("BIG_BOX_CODE");
    	setValue("BIG_BOX_CODE",a);
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
     * 取得SYS_FEE信息，放置在状态栏上
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "药品代码:" + order.getValue("ORDER_CODE")
            + " 药品名称:" + order.getValue("ORDER_DESC")
            + " 商品名:" + order.getValue("GOODS_DESC")
            + " 规格:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
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
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }


}
