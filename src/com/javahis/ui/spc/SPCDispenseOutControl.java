package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCDispenseOutTool;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 出库装箱管理
 * </p>
 *
 * <p>
 * Description: 出库装箱管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author Yuanxm 2012.12.4
 * @version 1.0
 */
public class SPCDispenseOutControl extends TControl  {
	
	 // 主项表格
    private TTable table_m;
    
    //未装箱
    TTable table_N;
    
    //已装箱
	TTable table_Y;
	
	//周转箱
	String boxEslId;
	
	//货位电子标签
	String eletagCode;
	
	//申请单号
    private String request_no;

    //申请部门
    private String app_org;

    //单据类型
    private String request_type;

    //返回结果集
    private TParm resultParm;
    
    //全院药库部门作业单据
    private boolean request_all_flg = true;

    // 出库部门
    private String out_org_code;

    // 入库部门
    private String in_org_code;

    // 是否出库
    private boolean out_flg;

    // 是否入库
    private boolean in_flg;

    // 出库单号
    private String dispense_no;
    
    // 使用单位
    private String u_type;
    
    private int row_m  ;
    private int row_n ;
    private String status="resend" ;//重送标记
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
    "##########0.0000");
    
    // 页面上翻数据列表
    private String[] pageItem = {
        "REQTYPE_CODE", "REQUEST_NO", "APP_ORG_CODE",
        "TO_ORG_CODE", "REASON_CHN_DESC", "REQUEST_DATE", "DISPENSE_NO",
        "DESCRIPTION", "URGENT_FLG"};

    
	/**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
        initData();
        
     // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_N|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        
    }
    
    /**
     * 初始画面数据
     */
    private void initPage() {
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }

        // 初始化验收时间
        // 出库日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_N = getTable("TABLE_N");
        
        row_m = -1;
        row_n = -1;
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
      //  ( (TMenuItem) getComponent("cancle")).setEnabled(false);
        resultParm = new TParm();
        
        //设置药品种
        setTypesOfDrug();
    }

    /**
     * 设置药品种类
     */
    public void setTypesOfDrug(){
    	TParm parm = getSysParm();
    	if(parm.getCount() > 0 ){
    		parm = parm.getRow(0);
    	}
    	String isSeparateReq = parm.getValue("IS_SEPARATE_REQ");
    	if(isSeparateReq != null && !isSeparateReq.equals("")){
    		if(isSeparateReq.equals("Y")){
    			getRadioButton("G_DRUGS").setSelected(true);
    		}else{
    			getRadioButton("ALL").setSelected(true);
    		}
    	}
    }
    
 
    /**
     * 初始化数据
     */
    public void initData(){
    	
    	// 传入参数集
        TParm parmIn = new TParm();
         
        // 查询区间
        parmIn.setData("REGION_CODE", Operator.getRegion());

        // 出库状态  未出库
        parmIn.setData("STATUS", "B");

        //药品种类--普药:1,麻精：2
       
        
        TParm sysParm = getSysParm();
    	if(sysParm.getCount() > 0 ){
    		sysParm = sysParm.getRow(0);
    	}
    	String isSeparateReq = sysParm.getValue("IS_SEPARATE_REQ");
    	if(isSeparateReq != null && !isSeparateReq.equals("")){
    		if(isSeparateReq.equals("Y")){
    			 parmIn.setData("DRUG_CATEGORY","1");
    		}else{
    			 parmIn.setData("DRUG_CATEGORY","3");
    		}
    	}
         
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
       
    	 TParm result = new TParm();
         result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                               "onQueryOutM", parmIn);
         
         // 全院药库部门作业单据
         if (!request_all_flg) {
             TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                 getOperatorDept(Operator.getID())));
             String dept_code = "";
             for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                 boolean flg = true;
                 for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                     dept_code = parm.getValue("DEPT_CODE", j);
                     if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                         "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                         "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                         if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                             flg = false;
                             break;
                         }
                         else {
                             flg = true;
                         }
                     }
                     else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                              "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                         if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                             flg = false;
                             break;
                         }
                         else {
                             flg = true;
                         }
                     }
                 }
                 if (flg) {
                     result.removeRow(i);
                 }
             }
         }
         if (result.getCount() <= 0 || result.getCount("REQUEST_NO") == 0) {
             this.messageBox("无查询结果");
             return;
         }
         table_m.setParmValue(result);
         
         //有数据，默认选中第一行
         table_m.setSelectedRow(0);
         onTableMClicked();
         
    }
    
    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
    	table_N = getTable("TABLE_N");
        table_N.acceptText();
        // 获得选中的列
        int column = table_N.getSelectedColumn();
        int row_d = table_N.getSelectedRow();
        if (column == 0) {
        	
            if ("Y".equals(table_N.getItemString(row_d, column))) {
            	
            	double stock_qty = 0 ;
            	double qty = 0 ;
            	TParm parm = table_N.getParmValue();
            	TParm rowParm = parm.getRow(row_d);
            	 // 库存不足信息
			    stock_qty = rowParm.getDouble("STOCK_QTY");
			    qty = rowParm.getDouble("OUT_QTY");
			    
			    if(qty > stock_qty ){
			    	table_N.setItem(row_d,"OUT_QTY",stock_qty);
			    	qty = stock_qty ;
			    }
			    
			    if(stock_qty <= 0) {
			    	this.messageBox("选中的记录没有库存");
			    	table_N.setItem(row_d, "SELECT_FLG", "N");
			    	return ;
			    }
                table_N.setItem(row_d, "SELECT_FLG", "Y");
            } else {
                table_N.setItem(row_d, "SELECT_FLG", "N");
                
            }
        }
    }
    

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                              "onQueryOutM", onQueryParm());
        // 全院药库部门作业单据
        if (!request_all_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getOperatorDept(Operator.getID())));
            String dept_code = "";
            for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                boolean flg = true;
                for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                    dept_code = parm.getValue("DEPT_CODE", j);
                    if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                    else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                             "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                }
                if (flg) {
                    result.removeRow(i);
                }
            }
        }
        if (result.getCount() <= 0 || result.getCount("REQUEST_NO") == 0) {
            this.messageBox("无查询结果");
            return;
        }
        table_m.setParmValue(result);
        
        //有数据，默认选中第一行
        table_m.setSelectedRow(1);
               
    }
    
    /**
     * 
     */
    public void onSave(){
    	
    	TParm parm = new TParm();
    	outStore(parm);
    	status = "save" ;
    	this.onCreate010Xml();
    }
    
    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // 主项表格选中行数据上翻
            getTableSelectValue(table_m, row_m, pageItem);
            // 出库时间
            if (table_m.getItemData(row_m, "DISPENSE_DATE") != null) {
                this.setValue("DISPENSE_DATE", table_m.getItemData(row_m,
                    "DISPENSE_DATE"));
            }
            // 设定页面状态
            getComboBox("REQTYPE_CODE").setEnabled(false);
            getComboBox("APP_ORG_CODE").setEnabled(false);
            getTextField("REQUEST_NO").setEnabled(false);
            // 单据类别
            request_type = getValueString("REQTYPE_CODE");
            // 申请单号
            request_no = getValueString("REQUEST_NO");
            // 出库单号
            dispense_no = getValueString("DISPENSE_NO");
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                // 明细信息  未出库
                getTableDInfo(request_no);
                getTextField("BOX_ESL_ID").setEnabled(true);
                getTextField("ELETAG_CODE").setEnabled(true);
                setValue("BOX_ESL_ID", "");
                setValue("ELETAG_CODE", "");
            }else {
                // 明细信息 出库
                getTableDInfo2(dispense_no);
                getTextField("BOX_ESL_ID").setEnabled(false);
                getTextField("ELETAG_CODE").setEnabled(false);
            }
            row_n = -1;
        }
    }
    
    /**
     * 表格选中行数据上翻
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }
    
    
    /**
     * 根据申请单号取得细项信息并显示在细项表格上
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
    	
    	TParm parm = new TParm();
    	parm.setData("REQUEST_NO",req_no);
    	
    	//传进去是不等于
    	parm.setData("UPDATE_FLG","3");
    	
    	parm.setData("REQTYPE_CODE",getValueString("REQTYPE_CODE"));
        // 取得未完成的细项信息
        TParm result = SPCDispenseOutTool.getInstance().onQuery(parm);
//        System.out.println("sql==="+INDSQL.getOutRequestDInfo(req_no, "3"));
//        System.out.println("result==" + result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_N.removeRowAll();
        table_N.setSelectionMode(0);
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }
        else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = false;
        }

        // 填充结果集
        resultParm = result;
        // 填充TABLE_D
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_N.setParmValue(result);
    }

    /**
     * 根据出库单号取得细项信息并显示在细项表格上
     *
     * @param dispense_no
     */
    private void getTableDInfo2(String dispense_no) {
    	
    	TParm parm = new TParm() ;
    	parm.setData("DISPENSE_NO",dispense_no);
    	 
        TParm result = SPCDispenseOutTool.getInstance().onQueryDispense(parm);
        //===zhangp 20120710 end
        //System.out.println("result----"+result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_N.removeRowAll();
        table_N.setSelectionMode(0);
        // 设定单位类型
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        // 设定出入库部门
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;

        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }

        resultParm = result;
        // 填充TABLE_D
        //System.out.println("result---"+result);
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_N.setParmValue(result);
    }
    
    
    /**
     * 填充TABLE_D
     *
     * @param table
     * @param parm
     * @param args
     */
    private TParm setTableDValue(TParm result) {
        TParm parm = new TParm();
        double qty = 0;
        double actual_qty = 0;
        double stock_price = 0;
        double retail_price = 0;
        double atm = 0;
        String order_code = "";
        boolean flg = false;
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            flg = false;
        }
        else {
            flg = true;
        }
        for (int i = 0; i < result.getCount(); i++) {
            parm.setData("SELECT_FLG", i, flg);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            // 库存量(出库部门)
            if (!"".equals(result.getValue("BATCH_NO", i))) {
                if ("0".equals(u_type)) {//0代表库存单位是盒 所以stock_qty（片） 要除以规格=盒，1代表发药单位by liyh 20120910
                	if("RET".equals(request_type)){//如果是退药，查询库存量 不用根据batch_no,valid_date来查询，只需根据order_code就Ok by liyh 20120910
                        parm.setData("STOCK_QTY", i,
                                INDTool.getInstance().getStockQTY(
                                    out_org_code, order_code) /
                                result.getDouble("DOSAGE_QTY", i));
                	}else if("WAS".equals(request_type)){
                		//包含过期
                		parm.setData("STOCK_QTY", i,
                                INDTool.getInstance().getStockQTYAll(
                                    out_org_code, order_code,
                                    result.getValue("BATCH_NO", i),
                                    result.getValue("VALID_DATE",
                       i).substring(0, 10), Operator.getRegion()) /
                                result.getDouble("DOSAGE_QTY", i)
                       );
                	}else{
	                    parm.setData("STOCK_QTY", i,
	                                 INDTool.getInstance().getStockQTY(
	                                     out_org_code, order_code,
	                                     result.getValue("BATCH_NO", i),
	                                     result.getValue("VALID_DATE",
	                        i).substring(0, 10), Operator.getRegion()) /
	                                 result.getDouble("DOSAGE_QTY", i)
	                        );
                	}
                }else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code,
                                     result.getValue("BATCH_NO", i),
                                     result.getValue("VALID_DATE",
                        i).substring(0, 10), Operator.getRegion()));
                }
            }else {
                if ("0".equals(u_type)) {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code) /
                                 result.getDouble("DOSAGE_QTY", i));
                }else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTY(
                                     out_org_code, order_code));
                }
            }
            
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                parm.setData("OUT_QTY", i, qty - actual_qty);
            }else {
                parm.setData("OUT_QTY", i, qty);
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
           
            /**
            // 使用单位
            if ("0".equals(u_type)) {
                // 库存单位
                stock_price = result.getDouble("STOCK_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
                retail_price = result.getDouble("RETAIL_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
            }
            else {
                // 配药单位
                stock_price = result.getDouble("STOCK_PRICE", i);
                retail_price = result.getDouble("RETAIL_PRICE", i);
            }*/
            stock_price = result.getDouble("STOCK_PRICE", i);
            parm.setData("STOCK_PRICE", i, stock_price);
            atm = StringTool.round(stock_price * qty, 2);
            parm.setData("STOCK_ATM", i, atm);
            parm.setData("RETAIL_PRICE", i, retail_price);
            atm = StringTool.round(retail_price * qty, 2);
            parm.setData("RETAIL_ATM", i, atm);
            atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
            parm.setData("DIFF_ATM", i, atm);
            parm.setData("BATCH_NO", i, result.getValue("BATCH_NO", i));
            parm.setData("VALID_DATE", i, result.getTimestamp("VALID_DATE", i));
            parm.setData("PHA_TYPE", i, result.getValue("PHA_TYPE", i));
            parm.setData("ORDER_CODE", i, order_code);
            parm.setData("REQUEST_SEQ",i,result.getInt("REQUEST_SEQ",i));
            parm.setData("BATCH_SEQ",i,result.getValue("BATCH_SEQ",i));
            parm.setData("UNIT_CHN_DESC",i,result.getValue("UNIT_CHN_DESC",i));
            parm.setData("MATERIAL_LOC_CODE",i,result.getValue("MATERIAL_LOC_CODE",i));
            parm.setData("ELETAG_CODE",i,result.getValue("ELETAG_CODE",i));
            parm.setData("SUP_CODE",i,result.getValue("SUP_CODE",i));
            
        }
        //System.out.println("------"+parm);
        return parm;
    }

    
    /**
     * 查询条件参数
     *
     * @return
     */
    private TParm onQueryParm() {
        // 传入参数集
        TParm parm = new TParm();
        // 申请单号
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        // 申请部门
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        // 单号类别
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        // 出库单号
        if (!"".equals(getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        // 查询区间
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return parm;
        }
        // 出库状态
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            // 未出库
            parm.setData("STATUS", "B");
        }
        else if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // 完成
            parm.setData("STATUS", "A");
        }
        else {
            // 途中
            parm.setData("STATUS", "C");
        }
        
        //药品种类--普药:1,麻精：2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","2");
        }else if(getRadioButton("ALL").isSelected()){
        	parm.setData("DRUG_CATEGORY","3");
        }
        
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
        if(getRadioButton("APP_ALL").isSelected()){
        	
        }else if(getRadioButton("APP_ARTIFICIAL").isSelected()){
        	parm.setData("APPLY_TYPE","1");
        }else if(getRadioButton("APP_PLE").isSelected()){
        	parm.setData("APPLY_TYPE","2");
        }else if(getRadioButton("APP_AUTO").isSelected()){
        	parm.setData("APPLY_TYPE","3");
        }
        
        return parm;
    }
    
    /**
	 * 周转箱的回车事件
	 * */
	public void onBoxEslIdClicked() {
		boxEslId = this.getValueString("BOX_ESL_ID");
	
		table_N.acceptText();
		table_N = getTable("TABLE_N");
		
		//String dispenseNo = getValueString("DISPENSE_NO");
		if(StringUtil.isNullString(boxEslId)){
			this.messageBox("周转箱为空!");
			return ;
		}
			
		int count = table_N.getRowCount() ;
		if(count <= 0 ){
			this.messageBox("没有出库下架装箱数据");
			return ;
		}
		
		this.getTextField("ELETAG_CODE").grabFocus();
		
		//周转箱显示出库单号与出库部门
		//
		//String toOrgCode = (String)getValue("TO_ORG_CODE");
		TComboBox tcb = getComboBox("APP_ORG_CODE");
		String orgChnDesc = tcb.getSelectedName();
		
		/**
		 * 调用电子标签
		 */
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("ProductName", orgChnDesc);
		
		String spec = "";

        //药品种类--普药:1,麻精：2
        if(getRadioButton("G_DRUGS").isSelected()){
        	spec = "普药";
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	spec = "麻精";
        }
		map.put("SPECIFICATION",   spec);
		map.put("TagNo", boxEslId);
		map.put("Light", 1);
		String apRegion = getApRegion(out_org_code);
		if(apRegion == null || apRegion.equals("")){
			System.out.println("标签区域没有设置部门代码："+out_org_code);
		}
		map.put("APRegion", apRegion);
		//System.out.println("apRegion-----:"+apRegion);
		list.add(map);
		try{
			String url = Constant.LABELDATA_URL ;
			LabelControl.getInstance().sendLabelDate(list, url);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
	/**
	 * 电子标签的回车事件
	 * */
	public void onEleTagClicked() {
		 
			String eletagCode=this.getValueString("ELETAG_CODE");
			boxEslId = this.getValueString("BOX_ESL_ID");
					
			if(StringUtil.isNullString(eletagCode) || StringUtil.isNullString(boxEslId)){
				return;
			}
			
			table_N = getTable("TABLE_N");
			table_N.acceptText();
			
			TParm parm = table_N.getParmValue();
			int count = table_N.getRowCount();
			
			//判断列表里是否有这个电子标签
			double stock_qty = 0;
			double qty = 0 ;
			String msgValidate = "";
			for (int i = 0; i < count; i++) {
				TParm rowParm = parm.getRow(i);
				String eleCode = rowParm.getValue("ELETAG_CODE");
				if(eletagCode.equals(eleCode)){
					
				    // 库存不足信息
				    stock_qty = rowParm.getDouble("STOCK_QTY");
				    qty = rowParm.getDouble("OUT_QTY");
				    
				    if(qty > stock_qty ){
				    	table_N.setItem(i,"OUT_QTY",stock_qty);
				    	qty = stock_qty ;
				    }
				    
				    if(stock_qty <= 0) {
				    	this.messageBox("该药品没有库存");
				    	return ;
				    }

				    table_N.setItem(i, "SELECT_FLG", "Y");
					/**
					rowParm.setData("BOX_ESL_ID",boxEslId);
					productName = rowParm.getValue("ORDER_DESC");
					rowParm.setData("BOXED_USER",Operator.getID());
					rowParm.setData("IS_BOXED","Y");
					//出库
					outStore(rowParm);
					*/
				   
				    
				    //出库的数量，重新复值给一个变量
				    double validateQty = qty ;
				    String batchNo = "";
				    if ("0".equals(u_type)) {
				    	validateQty = validateQty * INDTool.getInstance().getPhaTransUnitQty(rowParm.getValue("ORDER_CODE"), "2");
					} 
				    // 根据药库编号及药品代码查询药品的批次序号、库存和零售价
					TParm stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(out_org_code, rowParm.getValue("ORDER_CODE"), "");
					for (int j = 0; j < stock_parm.getCount(); j++) {
						double stockQty = stock_parm.getDouble("QTY", j);
						batchNo = stock_parm.getValue("BATCH_NO",j) ;
						 
						if(batchNo != null && !batchNo.equals("") && batchNo.length() > 10){
							batchNo = batchNo.substring(0,10);
						}
						//如果出库的数量小于库存量一次出库
						if (stockQty >= validateQty) {
							msgValidate += "["+batchNo+"]";
							break;
						}else{
							// 更新出库量
							validateQty = validateQty - stockQty;
							msgValidate += "["+batchNo+"]/";
						}
					}
					
					//SPCGenDrugPutDownTool.getInstance().updateINDDispensed(rowParm);
				    String productName = rowParm.getValue("ORDER_DESC");
				    String spec = rowParm.getValue("SPECIFICATION");
				    double lastQty = stock_qty-qty ;
				    
				    
				    /**
					 * 调用电子标签
					 */
					List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					map.put("ProductName", productName);
					map.put("SPECIFICATION",  spec+" "+lastQty);
					map.put("TagNo", eletagCode);
					map.put("Light", 1);
					
					String apRegion = getApRegion(out_org_code);
					if(apRegion == null || apRegion.equals("")){
						System.out.println("标签区域没有设置部门代码："+out_org_code);
					}
					map.put("APRegion", apRegion);
					
					list.add(map);
					try{
						String url = Constant.LABELDATA_URL ;
						LabelControl.getInstance().sendLabelDate(list, url);
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
				    	System.out.println("调用电子标签服务失败");

					}
				}
			}

			this.setValue("ELETAG_CODE", "");
			this.setValue("VALID_DATE_STR", msgValidate);
			return;
		
	}
	
	/**
	 * 出库动作
	 */
	public void outStore(TParm parm){
		if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            /** 新增保存(出库单主项及细项) */
            // 药库参数信息
            TParm sysParm = getSysParm();
            // 出入库作业状态判断(1-入库确认；2-出库即入库)
            String dis_check = getDisCheckFlg(sysParm);
            // 是否回写购入价格
            String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
            if ("1".equals(dis_check)) {
                // 出库部门库存是否可异动
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("出库部门处于盘点状态，库存不可异动");
                    return;
                }
                
                /**
                // 判断是否有库存，并将没有库存的药品取消勾选
                String message = checkStockQty(parm);
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                    return ;
                }*/
                
                
                // 出库在途作业/耗损、其它出库作业(出库即入库)
                if (getDispenseOutOn(out_org_code)) {
                	
                    // 打印出库单
                    this.onPrint();
                    this.onClear();
                }
            } else if ("2".equals(dis_check)) {
                // 出库部门库存是否可异动
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("出库部门处于盘点状态，库存不可异动");
                    return;
                }
                // 入库部门库存是否可异动
                if (!"".equals(in_org_code) && !getOrgBatchFlg(in_org_code)) {
                    this.messageBox("入库部门处于盘点状态，库存不可异动");
                    return;
                }
                
                /**
                // 判断是否有库存，并将没有库存的药品取消勾选
                String message = checkStockQty(parm);
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                    return ;
                }*/
                
                // 出库即入库作业(出入库部门均不为空)
                getDispenseOutIn(out_org_code, in_org_code, reuprice_flg,
                                 out_flg, in_flg,parm);
                
                //打印出库单
                this.onPrint();
                this.onClear();
            }
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            /** 在途更新 */
            if (row_m != -1) {
                // 更新主项
                getUpdateDispenseMOutOn();
            }
            else {
                // 更新细项
                this.messageBox("申请单已出库，不可修改");
            }
        }
        else {
            /** 完成更新 */
            this.messageBox("单据已完成，不可修改");
        }
	}
	
	/**
     * 更新主项(在途)
     */
    private void getUpdateDispenseMOutOn() {
        TParm parm = new TParm();
        // 主项信息
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "1").getParm("OUT_M");
        // 执行数据更新
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onUpdateMOutOn", parm);
        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

	
	 /**
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }

    /**
     * 出入库作业状态判断
     *
     * @return
     */
    private String getDisCheckFlg(TParm parm) {
        // 出入库作业状态判断
        if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
            && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // 需进行入库确认者且申请单状态入出库部门皆不为空-->在途状态
            return "1";
        }
        else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
                 && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // 不需进行入库确认者且申请单状态入出库部门皆不为空-->出库即入库
            return "2";
        }
        return "1";
    }

    /**
     * 库存是否可异动状态判断
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // 库存是否可异动状态判断
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            return false;
        }
        return true;
    }

    /**
     * 出库批号判断: 1,不指定批号和效期;2,指定批号和效期
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }
    
    /**
     * 判断是否有库存，并将没有库存的药品取消勾选
     * @return String
     */
    private String checkStockQty(TParm parm) {
        double stock_qty = 0;
        // 库存不足信息
        String message = "";
        stock_qty = parm.getDouble("STOCK_QTY");
        double qty = parm.getDouble("OUT_QTY");

        if (qty > stock_qty) {
        	message += "库存不足！";
        }
        return message;
    }
    
    /**
     * 出库在途作业/耗损、其它出库作业、卫耗材、科室备药(出库即入库)
     */
    private boolean getDispenseOutOn(String org_code) {
        
    	/**判断细项选中*/
    	if (!checkSelectRow()) {
            return false;
        }
    	
    	 //判断请领出库
        if ("DEP".equals(request_type)) {
            String order_code = "";
            double out_qty = 0;
            String order_desc = "";
            for (int i = 0; i < table_N.getRowCount(); i++) {
                if ("Y".equals(table_N.getItemString(i, "SELECT_FLG"))) {
                    //判断中药出多批次数据
                    order_code = table_N.getParmValue().getValue("ORDER_CODE",
                        i);
                    out_qty = table_N.getItemDouble(i, "OUT_QTY");
                    String sql =
                        " SELECT A.STOCK_QTY / B.STOCK_QTY / B.DOSAGE_QTY AS "
                        + "STOCK_QTY,C.UNIT_CHN_DESC,A.BATCH_NO,A.VALID_DATE, "
                        + " A.ORDER_CODE, D.ORDER_DESC "
                        + " FROM IND_STOCK A, PHA_TRANSUNIT B, "
                        + " SYS_UNIT C, PHA_BASE D "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + " AND A.ORDER_CODE = D.ORDER_CODE "
                        + " AND B.ORDER_CODE = D.ORDER_CODE "
                        + " AND A.ORG_CODE = '" + org_code
                        + "' AND A.ORDER_CODE = '" + order_code
                        + "' AND A.STOCK_QTY > 0 AND A.VALID_DATE > SYSDATE "
                        + " AND A.ACTIVE_FLG = 'Y' AND D.PHA_TYPE = 'G'"
                        + " AND B.STOCK_UNIT = C.UNIT_CODE "
                        + " ORDER BY A.VALID_DATE DESC, A.BATCH_SEQ";
                    TParm checkParm = new TParm(TJDODBTool.getInstance().select(
                        sql));
                    if (checkParm.getCount("ORDER_CODE") > 0) {
                        if (out_qty > checkParm.getDouble("STOCK_QTY", 0)) {
                            order_desc = checkParm.getValue("ORDER_DESC", 0);
                            this.messageBox(order_desc + " " + order_code
                                            + " 请领出库 " + out_qty + " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + ", 批号:" +
                                            checkParm.getValue("BATCH_NO", 0) +
                                            ",效期:" +
                                            checkParm.getValue("VALID_DATE", 0).
                                            substring(0, 10) +
                                            ",当前库存为" +
                                            checkParm.getDouble("STOCK_QTY", 0) +
                                            " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + " ,建议先将其全部出库");
                            return false;
                        }
                    }
                }
            }
        }
            
        

        TParm parm = new TParm();
        // 主项信息
        if (!CheckDataM()) {
            return false;
        }
        parm = getDispenseMParm(parm, "1");
        
        /**
        // 细项信息
        if (!CheckDataD()) {
            return false;
        }*/
        parm = getDispenseDParm(parm);
        // 使用单位
        parm.setData("UNIT_TYPE", u_type);
        // 申请单类型
        parm.setData("REQTYPE_CODE", request_type);
        // 出库部门
        parm.setData("ORG_CODE", org_code);
        
        //先取出数据暂放，电子标签显示应用
        //TParm outD = parm.getParm("OUT_D");
        // 执行数据新增
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertOutOn", parm);

        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
        this.messageBox("P0001");
        this.setValue("DISPENSE_NO", dispense_no);
        
        /**
        //电子标签亮灯
        for(int i = 0 ; i < outD.getCount("ORDER_CODE");  i++ ){
        	TParm inParm = new TParm();
        	String eleTagCode = outD.getValue("ELETAG_CODE",i) ;
			inParm.setData("ORDER_CODE",outD.getValue("ORDER_CODE",i));
			inParm.setData("ORG_CODE",getValue("TO_ORG_CODE"));
			inParm.setData("ELETAG_CODE",eleTagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			String spec = outParm.getValue("SPECIFICATION",0);
			EleTagControl.getInstance().login();
			EleTagControl.getInstance().sendEleTag(eleTagCode, outD.getValue("ORDER_DESC",i), spec, outParm.getValue("QTY",0), 0);
        }*/
        
        return true;
    }
    
    
    /**
     * 出库即入库作业
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String reuprice_flg, boolean out_flg,
                                  boolean in_flg,TParm rowParm) {
        /**
    	if (!checkSelectRow()) {
            return;
        }*/
    	
        TParm parm = new TParm();
        // 主项信息(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        
        /**刷电子标签时处理
        parm = getDispenseMParm(parm, "3");
        // 细项信息(OUT_D)
        if (!CheckDataD(rowParm)) {
            return;
        }*/
        
        parm = getDispenseDParm(parm);
        //System.out.println("PARM-> " + parm);

        // 使用单位
        parm.setData("UNIT_TYPE", u_type);
        // 申请单类型
        parm.setData("REQTYPE_CODE", request_type);
        // 出库部门
        parm.setData("OUT_ORG_CODE", out_org_code);
        // 入库部门
        parm.setData("IN_ORG_CODE", in_org_code);
        // 是否入库(IN_FLG)
        parm.setData("IN_FLG", in_flg);
        // 判断是否自动将成本价存回批发价
        parm.setData("REUPRICE_FLG", reuprice_flg);

        //TParm outD = parm.getParm("OUT_D");
        
        // 执行数据新增
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertOutIn", parm);
        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
       
        /**
        for(int i = 0 ; i < outD.getCount("ORDER_CODE");  i++ ){
        	TParm inParm = new TParm();
        	String eleTagCode = outD.getValue("ELETAG_CODE",i) ;
			inParm.setData("ORDER_CODE",outD.getValue("ORDER_CODE",i));
			inParm.setData("ORG_CODE",getValue("TO_ORG_CODE"));
			inParm.setData("ELETAG_CODE",eleTagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			String spec = outParm.getValue("SPECIFICATION");
			EleTagControl.getInstance().login();
			EleTagControl.getInstance().sendEleTag(eleTagCode, outD.getValue("ORDER_DESC",i), spec, outParm.getValue("QTY",0), 0);
        }*/
        
        onClear();
    }
    
    /**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg) {
        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        // 出库单号
        dispense_no = "";
        if ("".equals(getValueString("DISPENSE_NO"))) {
            dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }
        else {
            dispense_no = getValueString("DISPENSE_NO");
        }
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", getValue("DISPENSE_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", date);
            parmM.setData("WAREHOUSING_USER", Operator.getIP());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        parmM.setData("REASON_CHN_DESC", getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", u_type);
        if ("WAS".equals(getValue("REQTYPE_CODE")) ||
            "THO".equals(getValue("REQTYPE_CODE")) ||
            "COS".equals(getValue("REQTYPE_CODE")) ||
            "EXM".equals(getValue("REQTYPE_CODE"))) {
            update_flg = "3";
        }
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parmM.setData("REGION_CODE", Operator.getRegion());

        //药品种类--普药:1,麻精：2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parmM.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parmM.setData("DRUG_CATEGORY","2");
        }else{
        	parmM.setData("DRUG_CATEGORY","3");
        }
        
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
        if(getRadioButton("APP_ALL").isSelected()){
        	
        }else if(getRadioButton("APP_ARTIFICIAL").isSelected()){
        	parmM.setData("APPLY_TYPE","1");
        }else if(getRadioButton("APP_PLE").isSelected()){
        	parmM.setData("APPLY_TYPE","2");
        }else if(getRadioButton("APP_AUTO").isSelected()){
        	parmM.setData("APPLY_TYPE","3");
        }
        
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        
       
        return parm;
    }
    
    /**
     * 获得明细信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String batch_no = "";
        int count = 0;
        String order_code = "";
        String valid_date = "";
        TTable table_d = getTable("TABLE_N");
        TParm tableDParm = table_d.getParmValue() ;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmD.setData("DISPENSE_NO", count, dispense_no);
            parmD.setData("SEQ_NO", count, count);
            parmD.setData("REQUEST_SEQ", count, tableDParm.getValue( "REQUEST_SEQ",i));
            order_code = tableDParm.getValue("ORDER_CODE",i);
            parmD.setData("ORDER_CODE", count, order_code);
            parmD.setData("QTY", count, tableDParm.getDouble( "QTY",i));
            parmD.setData("UNIT_CODE", count, tableDParm.getValue(
                "UNIT_CODE",i));
            parmD.setData("RETAIL_PRICE", count,  tableDParm.getValue("RETAIL_PRICE",i));
            parmD.setData("STOCK_PRICE", count, tableDParm.getDouble(
                "STOCK_PRICE",i));
            parmD.setData("ACTUAL_QTY", count, tableDParm.getDouble(
                "OUT_QTY",i));
            parmD.setData("PHA_TYPE", count, tableDParm.getValue(
                "PHA_TYPE",i));
            //luahi modify 2012-1-16 batch_seq 的值需要从Tparm中取得，因为页面中并不体现batch_seq begin
//            parmD.setData("BATCH_SEQ", count, table_d.getItemData(i,
//                "BATCH_SEQ"));
              parmD.setData("BATCH_SEQ",count,tableDParm.getInt("BATCH_SEQ",i));
            //luahi modify 2012-1-16 batch_seq 的值需要从Tparm中取得，因为页面中并不体现batch_seq end
            
            batch_no = tableDParm.getValue( "BATCH_NO",i);
            parmD.setData("BATCH_NO", count, batch_no);
            valid_date = tableDParm.getValue("VALID_DATE",i);
            if ("".equals(valid_date)) {
                parmD.setData("VALID_DATE", count, tnull);
            }
            else {
                parmD.setData("VALID_DATE", count,
                              table_d.getItemTimestamp(i, "VALID_DATE"));
            }
            parmD.setData("DOSAGE_QTY", count, tableDParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            
            //是否下架
            parmD.setData("IS_BOXED",count,"Y");
            parmD.setData("BOXED_USER",count,Operator.getID());
            parmD.setData("BOX_ESL_ID",count,boxEslId);
            
            //电子标签应用
            parmD.setData("ELETAG_CODE",count,tableDParm.getValue(
                "ELETAG_CODE", i));
            parmD.setData("ORDER_DESC",count,tableDParm.getValue("ORDER_DESC",i));
            
            parmD.setData("SUP_ORDER_CODE",count,tableDParm.getValue("SUP_ORDER_CODE",i));
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    
    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("REQUEST_NO"))) {
            this.messageBox("申请单号不能为空");
            return false;
        }
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("申请部门不能为空");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("单据类别不能为空");
            return false;
        }
        return true;
    }
    
    /**
     * 判断细项是否选中
     *
     * @return
     */
    private boolean checkSelectRow() {
        // 判断细项是否选中
        boolean flg = true;
        for (int i = 0; i < table_N.getRowCount(); i++) {
            if ("Y".equals(table_N.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("没有选中出库药品明细");
            return false;
        }
        return true;
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataD(TParm parm) {
       
        // 判断数据正确性
        double qty = parm.getDouble("OUT_QTY");
        if (qty <= 0) {
            this.messageBox("出库数数量不能小于或等于0");
            return false;
        }
        double price = parm.getDouble("STOCK_PRICE");
        if (price <= 0) {
            this.messageBox("成本价不能小于或等于0");
            return false;
        }
        
        return true;
    }

    /**
     * 打印出库单
     */
    public void onPrint() {
        TTable table_d = getTable("TABLE_N");
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("不存在出库单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // modify by wangbin 2015/2/2 内部需求 #786 泰心物联网系统调整方案 : 药库出库单打印相关修改(出库单打印按照供应商分单打印) START
			// 查询该出库单号下有多少供应商
			String supCountSql = "SELECT  A.SUP_CODE, B.SUP_ABS_DESC FROM IND_DISPENSED A, SYS_SUPPLIER B WHERE A.SUP_CODE = B.SUP_CODE (+) "
					+ " AND A.DISPENSE_NO = '"
					+ this.getValueString("DISPENSE_NO")
					+ "' GROUP BY A.SUP_CODE, B.SUP_ABS_DESC ORDER BY A.SUP_CODE";
			TParm supCountData = new TParm(TJDODBTool.getInstance().select(
					supCountSql));
			
			if (supCountData.getErrCode() < 0 || supCountData.getCount() <= 0) {
				this.messageBox("查询药品供应商信息错误");
				err("ERR:"+supCountData.getErrText());
				return;
			}
			
			for (int m = 0; m < supCountData.getCount(); m++) {
				if (StringUtils.isEmpty(supCountData.getValue("SUP_CODE", m))
						|| StringUtils.isEmpty(supCountData.getValue(
								"SUP_ABS_DESC", m))) {
					this.messageBox("该出库单中存在无供应商的数据");
				}
			}

			String orgCode =  getValueString("APP_ORG_CODE");
			
			String sql = "";
			if(getSpclTrtDept().equals(orgCode)){
				sql = " SELECT A.ORDER_CODE, "
					 +"        CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE B.ORDER_DESC END "
					 +"             AS ORDER_DESC, "
					 +"          B.SPECIFICATION, "
					 +"          C.UNIT_CHN_DESC, "
					 +"          CASE             "
					 +"             WHEN A.UNIT_CODE = D.STOCK_UNIT "
					 +"             THEN     "
					 +"                A.RETAIL_PRICE * D.DOSAGE_QTY "
					 +"             WHEN A.UNIT_CODE = D.DOSAGE_UNIT "
					 +"             THEN                             "
					 +"                A.RETAIL_PRICE                "
					 +"            ELSE                              "
					 +"               A.RETAIL_PRICE                 "
					 +"         END                                  "
					 +"             RETAIL_PRICE,                    "
					 +"          A.QTY AS OUT_QTY,                   "
					 +"          A.BATCH_NO,                         "
					 +"          A.VALID_DATE,                       "
					 +"          CASE                                "
					 +"             WHEN A.UNIT_CODE = D.STOCK_UNIT THEN A.INVENT_PRICE  "
					 +"            WHEN A.UNIT_CODE = D.DOSAGE_UNIT THEN A.VERIFYIN_PRICE "
					 +"            ELSE A.VERIFYIN_PRICE                                  "
					 +"          END                                                      "
					 +"             VERIFYIN_PRICE,A.SUP_CODE, F.TYPE_CODE                "
					 +"     FROM IND_DISPENSED A,                                         "
					 +"         SYS_FEE B,                                                "
					 +"         SYS_UNIT C,                                               "
					 +"         PHA_TRANSUNIT D,                                          "
					 +"          (SELECT A.ORDER_CODE, A.OPMED_CODE                       "
					 +"             FROM SYS_ORDER_OPMEDD A                               "
					 +"           WHERE A.OPMED_CODE = (SELECT A.OPMED_CODE               "
					 +"                                    FROM (  SELECT A.OPMED_CODE,   "
					 +"                                                   COUNT (A.OPMED_CODE) NUM  "
					 +"                                              FROM SYS_ORDER_OPMEDD A,     "
					 +"                                                  SYS_ORDER_OPMEDM B       "
					 +"                                             WHERE     A.OPMED_CODE = B.OPMED_CODE  "
					 +"                                                  AND B.OP_FLG = 'Y'                 "
					 +"                                         GROUP BY A.OPMED_CODE                       "
					 +"                                          ORDER BY COUNT (A.OPMED_CODE) DESC) A      "
					 +"                                   WHERE ROWNUM = 1)) E,                             "
					 +"         PHA_BASE F                                                                  "
					 +"   WHERE     A.ORDER_CODE = B.ORDER_CODE                                             "
					 +"          AND A.UNIT_CODE = C.UNIT_CODE                                              "
					 +"          AND A.ORDER_CODE = D.ORDER_CODE                                            "
					 +"          AND A.ORDER_CODE = E.ORDER_CODE(+)                                         "
					 +"          AND A.ORDER_CODE = F.ORDER_CODE                                            "
					 +"          AND A.DISPENSE_NO = '"+this.getValueString("DISPENSE_NO")+"' AND 1=1 "
					 +"  ORDER BY A.SUP_CODE, F.TYPE_CODE, A.SEQ_NO                                         ";   
			}else if("040108".equals(orgCode)||"040109".equals(orgCode)) {
				sql =
                    "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL "
                    + " THEN B.ORDER_DESC ELSE B.ORDER_DESC  "
                    + "  END AS ORDER_DESC, "
                    + " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
                    + " (A.QTY*D.DOSAGE_QTY) AS MIN_QTY,A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.VERIFYIN_PRICE, "//ADD VERIFYIN_PRICE
                    + " A.SUP_CODE,E.TYPE_CODE "
                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C ,PHA_TRANSUNIT D,PHA_BASE E "
                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + " AND A.Order_Code=D.Order_Code AND D.DOSAGE_UNIT=C.UNIT_CODE AND A.ORDER_CODE = E.ORDER_CODE"
                    + " AND A.DISPENSE_NO = '" +
                    this.getValueString("DISPENSE_NO") + "' AND 1=1 "						
                    + " ORDER BY A.SUP_CODE, E.TYPE_CODE, A.SEQ_NO";  
			}else {
				   sql =
	                    "SELECT A.ORDER_CODE, " +
	                    "   CASE WHEN B.GOODS_DESC IS NULL "
	                    + "  THEN B.ORDER_DESC ELSE B.ORDER_DESC   END AS ORDER_DESC, "
	                    + "  B.SPECIFICATION, C.UNIT_CHN_DESC,  " 
	                    + " CASE  WHEN A.UNIT_CODE = D.STOCK_UNIT "
					    + "       THEN A.RETAIL_PRICE * D.DOSAGE_QTY  "
					    + "       WHEN A.UNIT_CODE = D.DOSAGE_UNIT "
					    + "       THEN A.RETAIL_PRICE "
					    + "       ELSE A.RETAIL_PRICE "
					    + "       END  RETAIL_PRICE, "
	                    + "  A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,"
	                    + "  CASE WHEN A.UNIT_CODE=D.STOCK_UNIT " 
	                    + "       THEN A.INVENT_PRICE "
	                    + "       WHEN A.UNIT_CODE=D.DOSAGE_UNIT THEN A.VERIFYIN_PRICE ELSE A.VERIFYIN_PRICE  END  VERIFYIN_PRICE, "   //ADD VERIFYIN_PRICE
	                    + " A.SUP_CODE,E.TYPE_CODE "
	                    + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C ,PHA_TRANSUNIT D,PHA_BASE E "
	                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
	                    + " AND A.UNIT_CODE = C.UNIT_CODE "
	                    + " AND A.ORDER_CODE=D.ORDER_CODE "
	                    + " AND A.ORDER_CODE=E.ORDER_CODE "
	                    + " AND A.DISPENSE_NO = '" +
	                    this.getValueString("DISPENSE_NO") + "' AND 1=1 "
	                    + " ORDER BY A.SUP_CODE, E.TYPE_CODE, A.SEQ_NO";
			}
			
			// 卫耗材
			sql = sql.replace("AND 1=1", "AND TYPE_CODE='4'");
			TParm healthSuppliesData = new TParm(TJDODBTool.getInstance().select(sql));
			
			if (healthSuppliesData.getErrCode() < 0) {
				this.messageBox("查询出库明细错误");
				err("ERR:" + healthSuppliesData.getErrText());
				return;
			}

			// 非卫耗材
			sql = sql.replace("AND TYPE_CODE='4'", "AND TYPE_CODE<>'4'");
			TParm notHealthSuppliesData = new TParm(TJDODBTool.getInstance().select(sql));
			
			if (notHealthSuppliesData.getErrCode() < 0) {
				this.messageBox("查询出库明细错误");
				err("ERR:" + notHealthSuppliesData.getErrText());
				return;
			}

			// 卫耗材与非卫耗材都查不到数据时
			if (healthSuppliesData.getCount() <= 0
					&& notHealthSuppliesData.getCount() <= 0) {
				this.messageBox("查无出库明细数据");
				return;
			}
			
			if (healthSuppliesData.getCount() > 0) {
				// 按供应商分单打印卫耗材出库单
				for (int m = 0; m < supCountData.getCount(); m++) {
					this.setPrintData(supCountData.getRow(m), healthSuppliesData,
							orgCode);
				}
			}

			if (notHealthSuppliesData.getCount() > 0) {
				// 按供应商分单打印非卫耗材出库单
				for (int m = 0; m < supCountData.getCount(); m++) {
					this.setPrintData(supCountData.getRow(m), notHealthSuppliesData,
							orgCode);
				}
			}
			// modify by wangbin 2015/2/2 内部需求 #786 泰心物联网系统调整方案 : 药库出库单打印相关修改(出库单打印按照供应商分单打印) END
        }
        else {
            this.messageBox("没有打印数据");
            return;
        }
    }
    
    
    /**
     * 取消出库方法
     */
    public void onCancle() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("没有取消单据");
            return;
        }
        if (this.messageBox("提示", "是否取消出库", 2) == 0) {
            String dispense_no = this.getValueString("DISPENSE_NO");
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("DISPENSE_NO", dispense_no);
            parm.setData("ORG_CODE", out_org_code);
            parm.setData("REQTYPE_CODE", this.getValueString("REQTYPE_CODE"));
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("PARM_D", table_N.getParmValue().getData());
            TParm result = TIOM_AppServer.executeAction(
                "action.spc.INDDispenseAction", "onUpdateDipenseCancel", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("取消失败");
                return;
            }
            this.messageBox("取消成功");
            this.onClear();
        }
    }
    
    /**
     * 终止单据方法
     */
    public void onStop() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("没有终止单据");
            return;
        }
        if (this.messageBox("提示", "是否终止单据", 2) == 0) {
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                "action.spc.INDRequestAction", "onUpdateFlg", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("终止失败");
                return;
            }
            this.messageBox("终止成功");
            this.onClear();
        }
    }
	
    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        // 清空画面内容
        String clearString =
            "REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;DISPENSE_NO;"
            + "REQUEST_DATE;TO_ORG_CODE;REASON_CHN_DESC;START_DATE;END_DATE;"
            + "DESCRIPTION;URGENT_FLG;SELECT_ALL;"
            + "SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE;"
            + "BOX_ESL_ID;ELETAG_CODE;VALID_DATE_STR";
        clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        
        //默认选中普药
        getRadioButton("G_DRUGS").setSelected(true);
        
        // 初始化页面状态及数据
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_N.setSelectionMode(0);
        table_N.removeRowAll();
         
        getTextField("REQUEST_NO").setEnabled(true);
        getComboBox("APP_ORG_CODE").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
       // ( (TMenuItem) getComponent("cancle")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        resultParm = new TParm();
    }
    
    
    /**
     * 出库状态改变事件
     */
    public void onChangeSelectAction() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_A").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
           // ( (TMenuItem) getComponent("cancle")).setEnabled(false);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
           // ( (TMenuItem) getComponent("cancle")).setEnabled(true);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(false);
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
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
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
    private TPanel getTPanel(String tagName){
    	return(TPanel)getComponent(tagName);
    }
    
    private String getApRegion( String orgCode) {
		TParm searchParm = new TParm () ;
		searchParm.setData("ORG_CODE",orgCode);
		TParm resulTParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode(searchParm);
		String apRegion = "";
		if(resulTParm != null ){
			apRegion = resulTParm.getValue("AP_REGION");
		}
		return apRegion;
	}
    
    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
    	table_N.acceptText();
        if (table_N.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        double stock_qty =  0 ;
        double qty =  0 ; 
        
    	TParm parm = table_N.getParmValue();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_N.getRowCount(); i++) {
                
                TParm rowParm = parm.getRow(i) ;
                // 库存不足信息
			    stock_qty = rowParm.getDouble("STOCK_QTY");
			    qty = rowParm.getDouble("OUT_QTY");
			    
			    if(qty > stock_qty ){
			    	table_N.setItem(i,"OUT_QTY",stock_qty);
			    	qty = stock_qty ;
			    }
			    
			    if(stock_qty <= 0) {
			    	this.messageBox("选中的记录没有库存");
			    	continue ;
			    }
                table_N.setItem(i, "SELECT_FLG", true);
            }
            
        }else {
            for (int i = 0; i < table_N.getRowCount(); i++) {
            	table_N.setItem(i, "SELECT_FLG", false);
            }
        }
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
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataD() {
        // 判断细项是否有被选中项
    	table_N.acceptText();
        for (int i = 0; i < table_N.getRowCount(); i++) {
            // 判断数据正确性
            double qty = table_N.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("出库数数量不能小于或等于0");
                return false;
            }
            
            /**
            double price = table_N.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return false;
            }*/
        }
        return true;
    }
    
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	 public  static String getSpclTrtDept(){
		 TConfig config = getProp() ;
		 String deptCode = config.getString("", "SPCL_TRT_DEPT");
		 return deptCode;
	 }
	 //==============================chenxi
  	 /**
  	  * 同步his出库信息
  	  */
  	 public void onCreate010Xml(){
  		 TConfig config = getProp() ;
		 String hisPath = config.getString("", "bsmHis.path");
		 if(hisPath.length()>0){
			 TParm parm = new TParm() ;
	  		 parm.setData("DISPENSE_NO", dispense_no) ;
	  		 System.out.println("DISPENSE_NO======"+dispense_no);
	  		TParm	 result = TIOM_AppServer.executeAction(
	                "action.spc.bsm.SPCBsmAction", "onDispenseOut", parm);  
	  		if(result.getErrCode()<0){
	  			this.messageBox("同步HIS出库信息失败") ;
	  			System.out.println("==err==="+result.getErrName());
	  			return  ;
	  			
	  		}
	  		if(status.equals("resend"))
	  		this.messageBox("重送成功") ;
	  		status = "resend" ;
	  		this.onClear() ;
	  		return  ;
		 }
  		return ;
	 }
  	 
  	 
	/**
	 * 组装出库单打印数据
	 * 
	 * @param supCountData
	 *            供应商数据
	 * @param printData
	 *            出库明细
	 * @author wangbin 2015/2/2
	 */
	private void setPrintData(TParm supCountData, TParm printData,
			String orgCode) {
		// 表格数据
		TParm parm = new TParm();
		double qty = 0;
		double sum_retail_price = 0;
		// luhai 2012-1-22 加入采购价总和
		double sum_verifyin_price = 0;

		for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
			if (StringUtils.equals(supCountData.getValue("SUP_CODE"),
					printData.getValue("SUP_CODE", i))) {
				parm.addData("ORDER_DESC", printData.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", printData.getValue(
						"SPECIFICATION", i));
				parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
				parm.addData("UNIT_PRICE", df4.format(printData.getDouble(
						"RETAIL_PRICE", i)));
				parm.addData("VERIFYIN_PRICE", df4.format(printData.getDouble(
						"VERIFYIN_PRICE", i)));
				qty = printData.getDouble("OUT_QTY", i);
				if ("040108".equals(orgCode) || "040109".equals(orgCode)) {

					qty = printData.getDouble("MIN_QTY", i);
					parm.addData("QTY", StringTool.round(qty, 3));
				} else {
					parm.addData("QTY", StringTool.round(qty, 3));
				}
				parm.addData("AMT", StringTool.round(printData.getDouble(
						"RETAIL_PRICE", i)
						* qty, 2));
				parm.addData("AMT_VERIFYIN", StringTool.round(printData
						.getDouble("VERIFYIN_PRICE", i)
						* qty, 2));
				sum_retail_price += printData.getDouble("RETAIL_PRICE", i)
						* qty;
				sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i)
						* qty;
				parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
				parm.addData("VALID_DATE", StringTool.getString(printData
						.getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
			}
		}

		if (parm.getCount("ORDER_DESC") <= 0) {
			return;
		}

		Timestamp datetime = SystemTool.getInstance().getDate();
		TParm data = new TParm();
		// luhai 2012-1-22 modify 加入采购金额，采购单价 begin
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "UNIT");
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
		parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");

		data.setData("TABLE", parm.getData());

		// 表头数据
		data.setData("TITLE", "TEXT", "药品出库单");
		// ===============pangben modify 20110607 添加急件注记
		if (null != getValue("URGENT_FLG")
				&& getValue("URGENT_FLG").equals("Y"))
			data.setData("URGENT", "TEXT", "急");
		else
			data.setData("URGENT", "TEXT", "");
		// ===============pangben modify 20110607 stop
		data.setData("DISP_NO", "TEXT", "出库单号: "
				+ this.getValueString("DISPENSE_NO"));
		data.setData("REQ_NO", "TEXT", "申请单号: "
				+ this.getValueString("REQUEST_NO"));
		data.setData("OUT_DATE", "TEXT", "出库日期: "
				+ this.getValueString("DISPENSE_DATE").substring(0, 10)
						.replace('-', '/'));
		data.setData("REQ_TYPE", "TEXT", "申请类别: "
				+ this.getComboBox("REQTYPE_CODE").getSelectedName());
		data.setData("ORG_CODE_APP", "TEXT", "出库部门: "
				+ this.getComboBox("TO_ORG_CODE").getSelectedName());
		data.setData("ORG_CODE_FROM", "TEXT", "接受部门: "
				+ this.getComboBox("APP_ORG_CODE").getSelectedName());
		data.setData("DATE", "TEXT", "制表日期: "
				+ datetime.toString().substring(0, 10).replace('-', '/'));
		// 供应商
		data.setData("SUP_ABS_DESC", "TEXT", "供应商: "
				+ supCountData.getValue("SUP_ABS_DESC"));

		// 表尾数据
		// 零售总金额
		String atm = String.valueOf(StringTool.round(sum_retail_price, 2));
		// 验收总金额
		String verifyinAtm = String.valueOf(StringTool.round(sum_verifyin_price, 2));
		
		data.setData("BAR_CODE", "TEXT", this.getValueString("DISPENSE_NO"));
		data.setData("ATM", "TEXT", atm);
		data.setData("VERIFYIN_ATM", "TEXT", verifyinAtm);
		data.setData("VERIFYIN_ATM_DESC", "TEXT", StringUtil.getInstance()
				.numberToWord(Double.parseDouble(verifyinAtm)));
		data.setData("TOT", "TEXT", "合计: ");
		data.setData("USER", "TEXT", "制表人:"+Operator.getName());
		data.setData("BAR_CODE", "TEXT", this.getValueString("DISPENSE_NO"));
		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DispenseOut.jhw", data);
	}

}
