package com.javahis.ui.inv;
 
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jdo.inv.INVSQL;
import jdo.inv.InvDispenseDTool;
import jdo.inv.InvStockDDTool;
import jdo.inv.InvStockDTool;
import jdo.inv.InvStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;


import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>
 * Title: 物资库入库管理Control  按单子出库（RFID扫描然后写入DD表），
 *                              按扫码，启动结束扫描（全部）入库    
 * </p>   在途显示出库单号，入库显示入库单号    INV_STOCKDD 取批号效期 
 *
 * <p> 
 * 出库（保存时只更新请领状态）-插入表，更新请领单 
 * 入库-（保存时只更新出库状态）插入表，更新出库单 （入库）
 * 
 * Description: 物资库入库管理Control     
 * </p>           
 * 未完成：出库单(0)  在途：入库状态0 完成：入库状态1   
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fux 2013.05.02
 * @version 1.0 
 */
public class INVDispenseInControl 
    extends TControl { 
    public INVDispenseInControl() {  
    }  

	TLabel tlabTip;
	 
	//private TParm parmStr;
	
	
	private TParm tparm=new TParm();
	
	boolean flg;
	 
    
    
    
    private TTable table_m;//申请单

    private TTable table_d;//具体细项
     
    
    
    private TTable table_m_select; //完成状态查询入库单号 
    

    // 全院药库部门作业单据
    private boolean request_all_flg = true;

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }
  
    /**
     * 清空方法 
     */
    public void onClear() {
        String clearString =    
            "START_DATE;END_DATE;REQUEST_TYPE_Q;TO_ORG_CODE_Q;DISPENSE_NO_OUTQ;"
            + "DISPENSE_NO_INQ;DISPENSE_NO;DISPENSE_NO_OUT;REQUEST_TYPE;DISPENSE_DATE;"
            + "TO_ORG_CODE;FROM_ORG_CODE;REN_CODE;DISPENSE_USER;REMARK;"
            + "FINA_FLG;CHECK_DATE;CHECK_DATE;SELECT_ALL;URGENT_FLG";
        this.clearValue(clearString);                                          
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE", 
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        table_m.setSelectionMode(0);  
        table_m.removeRowAll();
        table_d.setSelectionMode(0); 
        table_d.removeRowAll();
        table_m_select.setSelectionMode(0); 
        table_m_select.removeRowAll();
    } 

  
    /**  
     * 保存方法
     */  
    public void onSave() { 
        if (!checkData()) {
            return;
        }     
//    	setData:对象
//    	addData:数组
        //后续加入出入库标示来确认出入库
        TParm parm = new TParm();
        // 入库单主项信息
        getDispenseMData(parm);
        // 入库单细项信息
        getDispenseDData(parm);     
        // 入库单序号管理信息  
        // 库存主项数据
        getInvStockMData(parm); 
        // 库存明细数据(更新只能柜)
        getInsertInvStockDData(parm);  
        // 更新库存序号管理数据 
        //应该更新入库状态???(考虑加入出入库完成标记（和状态一起判断状态）)  
        // 出单细项状态                  
        getUpdateDispenseMData(parm); 
        // 出库单主项状态  细项没有状态字段  
        // getUpdateDispenseDData(parm);    
        // 查询出库方式(N.入库确认注记(在途)，Y.出库即入库)  
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvSysParm()));  
        if (sysParm.getCount() > 0) {    
            TParm discheck_flg = new TParm(); 
            discheck_flg.setData("DISCHECK_FLG",
                                 sysParm.getValue("DISCHECK_FLG", 0));
            parm.setData("DISCHECK_FLG", discheck_flg.getData());
            TParm result = TIOM_AppServer.executeAction(
                "action.inv.INVDispenseAction", "onInsert", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            onClear();
        }
        else {
            this.messageBox("没有设定物资参数档");
            return;
        }
    }

    /**
     * 取得入库单主项数据
     * @param parm TParm
     * @return TParm
     */  
    private TParm getDispenseMData(TParm parm) {
        TParm dispenseM = new TParm();
        // 出库单号     
        dispenseM.setData("DISPENSE_NO",
                          SystemTool.getInstance().getNo("ALL", "INV",
            "DISPENSE_NO", "No"));
        // 单据类别 
        dispenseM.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE"));
        // 申请单号(录入也应该插入申请单号)  
        dispenseM.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));         
        // 申请日期 
        dispenseM.setData("REQUEST_DATE",   
                          table_m.getParmValue().getTimestamp("REQUEST_DATE",
            table_m.getSelectedRow()));
        // 接受申请部门 
        dispenseM.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
        // 申请部门
        dispenseM.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        // 出库日期
        dispenseM.setData("DISPENSE_DATE", this.getValue("DISPENSE_DATE"));
        // 出库人员     
        dispenseM.setData("DISPENSE_USER", Operator.getID());
        // 紧急注记
        dispenseM.setData("URGENT_FLG", this.getValueString("URGENT_FLG"));
        // 备注
        dispenseM.setData("REMARK", this.getValueString("REMARK"));
        // 取消出库
        dispenseM.setData("DISPOSAL_FLG", "N");
        // 入库确认日期
        dispenseM.setData("CHECK_DATE", SystemTool.getInstance().getDate());
        // 入库确认人员
        dispenseM.setData("CHECK_USER", Operator.getID());
        // 申请原因
        dispenseM.setData("REN_CODE", this.getValueString("REN_CODE"));
        // 出入库注记            
        dispenseM.setData("FINA_FLG", 
                          "WAS".equals(this.getValueString("REQUEST_TYPE")) ?
                          "0" : "1");     
        // 申请原因
        dispenseM.setData("REN_CODE", this.getValueString("REN_CODE"));
        
        // 入出库标记
        dispenseM.setData("IO_FLG", "1");  
        // OPT 
        dispenseM.setData("OPT_USER", Operator.getID());
        dispenseM.setData("OPT_DATE", SystemTool.getInstance().getDate());
        dispenseM.setData("OPT_TERM", Operator.getIP());
        parm.setData("DISPENSE_M", dispenseM.getData());  
        return parm;
    } 

    /**
     * 取得入库单细项数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getDispenseDData(TParm parm) {
        TParm dispenseD = new TParm();
        int count = 0;
        TNull tnull = new TNull(Timestamp.class);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
            	//跳出本次循环
                continue;
            }   
            // 出库单号
            dispenseD.addData("DISPENSE_NO",
                              parm.getParm("DISPENSE_M").getValue("DISPENSE_NO"));
            // 出库单序号
            dispenseD.addData("SEQ_NO", count);
            count++;
            // 批次序号
            dispenseD.addData("BATCH_SEQ",
                              table_d.getParmValue().getInt("BATCH_SEQ", i));
            // 物资代码
            dispenseD.addData("INV_CODE",
                              table_d.getParmValue().getValue("INV_CODE", i));
            // 物资序号
            dispenseD.addData("INVSEQ_NO",
                              table_d.getParmValue().getInt("INVSEQ_NO", i));
            // 序号管理注记
            dispenseD.addData("SEQMAN_FLG",
                              table_d.getParmValue().getValue("SEQMAN_FLG", i));
            // 数量
            dispenseD.addData("QTY", table_d.getItemDouble(i, "QTY"));
            // 单位
            dispenseD.addData("DISPENSE_UNIT", 
                              table_d.getParmValue().getValue("DISPENSE_UNIT",
                i));
            // 成本价
            dispenseD.addData("COST_PRICE",
                              table_d.getItemDouble(i, "COST_PRICE"));
            // 申请序号
            dispenseD.addData("REQUEST_SEQ",
                              table_d.getParmValue().getInt("REQUEST_SEQ", i));
            // 批号
            dispenseD.addData("BATCH_NO",
                              table_d.getParmValue().getValue("BATCH_NO", i));
            // 效期 VALID_DATE 
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                dispenseD.addData("VALID_DATE", tnull);
            }
            else { 
                dispenseD.addData("VALID_DATE",
                                  TypeTool.getTimestamp(table_d.getParmValue().
                    getTimestamp("VALID_DATE", i)));
            }
            // 取消出库
            dispenseD.addData("DISPOSAL_FLG", "N");
            // OPT
            dispenseD.addData("OPT_USER", Operator.getID());
            dispenseD.addData("OPT_DATE", SystemTool.getInstance().getDate());
            dispenseD.addData("OPT_TERM", Operator.getIP());
            // 入出库标记 1：入库
            dispenseD.addData("IO_FLG", "1");   
        }
        parm.setData("DISPENSE_D", dispenseD.getData());
        return parm;
    }  

    /**
     * 取得库存主项数据
     * @param parm TParm
     * @return TParm
     */ 
    public TParm getInvStockMData(TParm parm) {
        //TParm stockOutM = new TParm();
        TParm stockInM = new TParm();
        // 出库部门
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = "";
//        REQUEST_NO
//        REQUEST_DATE
//        REQUEST_TYPE
        String request_type = this.getValueString("REQUEST_TYPE");
        if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
            // 请领,调拨
            out_org_code = this.getValueString("FROM_ORG_CODE");
            in_org_code = this.getValueString("TO_ORG_CODE");
        }
        else if ("RET".equals(request_type) ||
                 "WAS".equals(request_type)) {
            // 退库,耗损
            out_org_code = this.getValueString("TO_ORG_CODE");
            in_org_code = this.getValueString("FROM_ORG_CODE");
        }
        double qty = 0;
        Map map = new HashMap();
        for (int i = 0; i < table_d.getRowCount(); i++) {
//            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
//                continue;
//            }
//            else  
            	if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            else {
                inv_code = table_d.getParmValue().getValue("INV_CODE", i);
                qty = table_d.getItemDouble(i, "QTY");
                if (map.isEmpty()) {
                    map.put(inv_code, qty);
                }
                else {
                    if (map.containsKey(inv_code)) {
                        qty += TypeTool.getDouble(map.get(inv_code));
                        map.put(inv_code, qty);
                    }
                    else {
                        map.put(inv_code, qty);
                    }
                }
            }
        }

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
 //           stockOutM.addData("ORG_CODE", out_org_code);
            inv_code = TypeTool.getString(iterator.next());
 //           stockOutM.addData("INV_CODE", inv_code);
 //           stockOutM.addData("STOCK_QTY", TypeTool.getDouble(map.get(inv_code)));
 //           stockOutM.addData("OPT_USER", Operator.getID());
 //           stockOutM.addData("OPT_DATE", SystemTool.getInstance().getDate());
 //           stockOutM.addData("OPT_TERM", Operator.getIP());

            if (!"WAS".equals(request_type)) {
                TParm stockParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvStockM(in_org_code, inv_code)));
                TParm baseParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvBase(inv_code)));
                if (stockParm == null || stockParm.getCount() <= 0) {
                    stockInM.addData("TYPE", "INSERT"); //新增数据
                    stockInM.addData("ORG_CODE", in_org_code);
                    stockInM.addData("INV_CODE", inv_code);
                    stockInM.addData("REGION_CODE", Operator.getRegion());
                    stockInM.addData("DISPENSE_FLG", "N");
                    stockInM.addData("DISPENSE_ORG_CODE", "");
                    stockInM.addData("STOCK_FLG", "N");
                    stockInM.addData("MATERIAL_LOC_CODE", "");
                    stockInM.addData("SAFE_QTY", 0);
                    stockInM.addData("MIN_QTY", 0);
                    stockInM.addData("MAX_QTY", 0);
                    stockInM.addData("ECONOMICBUY_QTY", 0);
                    stockInM.addData("STOCK_QTY",
                                     TypeTool.getDouble(map.get(inv_code)));
                    stockInM.addData("MM_USE_QTY", 0);
                    stockInM.addData("AVERAGE_DAYUSE_QTY", 0);
                    stockInM.addData("STOCK_UNIT",
                                     baseParm.getValue("DISPENSE_UNIT",0));//========pangben modify 2011829
                    stockInM.addData("OPT_USER", Operator.getID());
                    stockInM.addData("OPT_DATE",
                                     SystemTool.getInstance().getDate());
                    stockInM.addData("OPT_TERM", Operator.getIP());
                    stockInM.addData("BASE_QTY", 0);
                }
                else {
                    stockInM.addData("TYPE", "UPDATE"); //更新数据
                    stockInM.addData("ORG_CODE", in_org_code);
                    stockInM.addData("INV_CODE", inv_code);
                    stockInM.addData("STOCK_QTY",
                                     TypeTool.getDouble(map.get(inv_code)));
                    stockInM.addData("OPT_USER", Operator.getID());
                    stockInM.addData("OPT_DATE",
                                     SystemTool.getInstance().getDate());
                    stockInM.addData("OPT_TERM", Operator.getIP());
                }
            }  
        }
        //parm.setData("STOCK_OUT_M", stockOutM.getData());
        parm.setData("STOCK_IN_M", stockInM.getData());
        return parm;
    }
    
    /**
     * 取得库存明细档数据(INV_STOCKD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertInvStockDData(TParm parm) {
        TParm stockD = new TParm();
        String org_code =this.getValueString("TO_ORG_CODE");;
       
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code =  "";
        int batchSeq = 0;
        for(int i = 0; i < table_d.getRowCount(); i++){
       	 inv_code = table_d.getParmValue().getValue("INV_CODE", i);
       	 batchSeq  = table_d.getItemInt(i, "QTY");            
         TParm stockDParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvStockD(org_code, inv_code,batchSeq)));
        	if(stockDParm.getData()==null){        
        		stockD.addData("TYPE", "INSERT"); //更新数据   
                stockD.addData("FLG", "INSERT");  
                    // 抓取最大BATCH_SEQ+1      
                stockD.addData("ORG_CODE", org_code);           
                stockD.addData("INV_CODE", inv_code);    
                stockD.addData("BATCH_SEQ", table_d.getItemData(i, "BATCH_SEQ"));
                stockD.addData("REGION_CODE", Operator.getRegion());
                stockD.addData("BATCH_NO",  table_d.getParmValue().
                        getValue("BATCH_NO", i));   
                stockD.addData("VALID_DATE",TypeTool.getTimestamp(table_d.getItemTimestamp(i,"VALID_DATE")));
                //  stockD.addData("STOCK_QTY", parmD.getDouble("IN_QTY", i));
                	  stockD.addData("STOCK_QTY", table_d.getItemDouble(i, "QTY"));
              
                stockD.addData("LASTDAY_TOLSTOCK_QTY", 0); 
                // stockD.addData("DAYIN_QTY", parmD.getDouble("IN_QTY", i));
                stockD.addData("DAYIN_QTY", 0);  
                stockD.addData("DAYOUT_QTY", 0);
                stockD.addData("DAY_CHECKMODI_QTY", 0);
                stockD.addData("DAY_VERIFYIN_QTY", 0);
                stockD.addData("DAY_VERIFYIN_AMT",
                              0);
                stockD.addData("GIFTIN_QTY",0);
                stockD.addData("DAY_REGRESSGOODS_QTY", 0);
                stockD.addData("DAY_REGRESSGOODS_AMT", 0);
                stockD.addData("DAY_REQUESTIN_QTY", 0);
                stockD.addData("DAY_REQUESTOUT_QTY", 0);
                stockD.addData("DAY_CHANGEIN_QTY", 0);
                stockD.addData("DAY_CHANGEOUT_QTY", 0);
                stockD.addData("DAY_TRANSMITIN_QTY", 0);
                stockD.addData("DAY_TRANSMITOUT_QTY", 0);
                stockD.addData("DAY_WASTE_QTY", 0);
                stockD.addData("DAY_DISPENSE_QTY", 0);
                stockD.addData("DAY_REGRESS_QTY", 0);
                stockD.addData("FREEZE_TOT", 0);
                stockD.addData("UNIT_PRICE", table_d.getItemDouble(i, "COST_PRICE"));
                stockD.addData("STOCK_UNIT", table_d.getItemString(i, "DISPENSE_UNIT"));
                stockD.addData("OPT_USER", Operator.getID());
                stockD.addData("OPT_DATE", date);
                stockD.addData("OPT_TERM", Operator.getIP());
        	}else{
        		stockD.addData("TYPE", "UPDATE"); //更新数据
                stockD.addData("FLG", "UPDATE");
                    // 抓取最大BATCH_SEQ+1    
                stockD.addData("ORG_CODE", org_code);  
                stockD.addData("INV_CODE", inv_code);  
                stockD.addData("BATCH_SEQ", table_d.getItemData(i, "BATCH_SEQ"));
                stockD.addData("REGION_CODE", Operator.getRegion());
                stockD.addData("BATCH_NO",  table_d.getParmValue().
                        getValue("BATCH_NO", i));
                stockD.addData("VALID_DATE",TypeTool.getTimestamp(table_d.getItemTimestamp(i,"VALID_DATE")));
              //  stockD.addData("STOCK_QTY", parmD.getDouble("IN_QTY", i));
                	  stockD.addData("STOCK_QTY", table_d.getItemDouble(i, "QTY"));
              
                stockD.addData("LASTDAY_TOLSTOCK_QTY", 0);
               // stockD.addData("DAYIN_QTY", parmD.getDouble("IN_QTY", i));
                stockD.addData("DAYIN_QTY", 0);  
                stockD.addData("DAYOUT_QTY", 0);
                stockD.addData("DAY_CHECKMODI_QTY", 0);
                stockD.addData("DAY_VERIFYIN_QTY", 0);
                stockD.addData("DAY_VERIFYIN_AMT",
                              0);
                stockD.addData("GIFTIN_QTY",0);
                stockD.addData("DAY_REGRESSGOODS_QTY", 0);
                stockD.addData("DAY_REGRESSGOODS_AMT", 0);
                stockD.addData("DAY_REQUESTIN_QTY", 0);
                stockD.addData("DAY_REQUESTOUT_QTY", 0);
                stockD.addData("DAY_CHANGEIN_QTY", 0);
                stockD.addData("DAY_CHANGEOUT_QTY", 0);
                stockD.addData("DAY_TRANSMITIN_QTY", 0);
                stockD.addData("DAY_TRANSMITOUT_QTY", 0);
                stockD.addData("DAY_WASTE_QTY", 0);
                stockD.addData("DAY_DISPENSE_QTY", 0);
                stockD.addData("DAY_REGRESS_QTY", 0);
                stockD.addData("FREEZE_TOT", 0);
                stockD.addData("UNIT_PRICE", table_d.getItemDouble(i, "COST_PRICE"));
                stockD.addData("STOCK_UNIT", table_d.getItemString(i, "DISPENSE_UNIT"));
                stockD.addData("OPT_USER", Operator.getID());
                stockD.addData("OPT_DATE", date);
                stockD.addData("OPT_TERM", Operator.getIP());
        	}
        }
        parm.setData("STOCK_IN_D", stockD.getData());                 
        return parm;
    }


    /**
     * 取得库存明细数据 
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvStockDData(TParm parm) {
        //TParm stockOutD = new TParm();
        TParm stockInD = new TParm();
        // 出库部门 
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = "";
        TNull tnull = new TNull(Timestamp.class);
        String request_type = this.getValueString("REQUEST_TYPE");
        if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
            // 请领,调拨
            out_org_code = this.getValueString("FROM_ORG_CODE");
            in_org_code = this.getValueString("TO_ORG_CODE");
        }
        else if ("RET".equals(request_type) ||
                 "WAS".equals(request_type)) {
            // 退库,耗损
            out_org_code = this.getValueString("TO_ORG_CODE");
            in_org_code = this.getValueString("FROM_ORG_CODE");
        }

        if (!"WAS".equals(request_type)) { 
            int batch_seq = 0; 
            for(int i = 0; i < table_d.getRowCount(); i++){
            	 inv_code = table_d.getParmValue().getValue("INV_CODE", i);
//                 batch_seq = stockOutD.getInt("BATCH_SEQ", i);
//                 if (stockDParm == null || stockDParm.getCount("INV_CODE") <= 0) {
//                	 messageBox("INSERT");
//                     TParm baseParm = new TParm(TJDODBTool.getInstance().select(
//                         INVSQL.getInvBase(inv_code)));
//                     stockInD.addData("TYPE", "INSERT"); //新增数据
//                     stockInD.addData("ORG_CODE", in_org_code); 
//                     stockInD.addData("INV_CODE", inv_code);    
//                     stockInD.addData("BATCH_SEQ", batch_seq);
//                     stockInD.addData("REGION_CODE", Operator.getRegion());
//                     stockInD.addData("BATCH_NO",
//                                      table_d.getParmValue().
//                                      getValue("BATCH_NO", i));
//                     
//                     if (table_d.getItemData(i, "VALID_DATE") == null ||
//                             "".equals(table_d.getItemString(i, "VALID_DATE"))) {
//                    	       stockInD.addData("VALID_DATE", tnull); 
//                         }
//                         else { 
//                        	  stockInD.addData("VALID_DATE",
//                                      table_d.getParmValue().
//                                      getTimestamp("VALID_DATE", i));
//                         }
//                     
//                     stockInD.addData("STOCK_QTY",  
//                    		 table_d.getItemDouble(i, "QTY"));
//                     stockInD.addData("LASTDAY_TOLSTOCK_QTY", 0);
//                     stockInD.addData("DAYIN_QTY", 
//                    		 table_d.getItemDouble(i, "QTY"));
//                     stockInD.addData("DAYOUT_QTY", 0);
//                     stockInD.addData("DAY_CHECKMODI_QTY", 0);
//                     stockInD.addData("DAY_VERIFYIN_QTY", 0);
//                     stockInD.addData("DAY_VERIFYIN_AMT", 0);
//                     stockInD.addData("GIFTIN_QTY", 0);
//                     stockInD.addData("DAY_REGRESSGOODS_QTY", 0);
//                     stockInD.addData("DAY_REGRESSGOODS_AMT", 0);
//                     if ("REQ".equals(request_type)) {
//                         stockInD.addData("DAY_REQUESTIN_QTY",
//                        		 table_d.getItemDouble(i, "QTY"));
//                     }
//                     else { 
//                         stockInD.addData("DAY_REQUESTIN_QTY", 0);
//                     }
//                     stockInD.addData("DAY_REQUESTOUT_QTY", 0);
//                     if ("GIF".equals(request_type)) {
//                         stockInD.addData("DAY_CHANGEIN_QTY",
//                        		 table_d.getItemDouble(i, "QTY"));
//                     }
//                     else {
//                         stockInD.addData("DAY_CHANGEIN_QTY", 0);
//                     }
//                     stockInD.addData("DAY_CHANGEOUT_QTY", 0);
//                     if ("RET".equals(request_type)) {
//                         stockInD.addData("DAY_TRANSMITIN_QTY",
//                        		 table_d.getItemDouble(i, "QTY"));
//                     }
//                     else {
//                         stockInD.addData("DAY_TRANSMITIN_QTY", 0);
//                     }
//                     stockInD.addData("DAY_TRANSMITOUT_QTY", 0);
//                     stockInD.addData("DAY_WASTE_QTY", 0);
//                     stockInD.addData("DAY_DISPENSE_QTY", 0);
//                     stockInD.addData("DAY_REGRESS_QTY", 0);
//                     stockInD.addData("FREEZE_TOT", 0);
//                     stockInD.addData("UNIT_PRICE",
//                                      baseParm.getDouble("COST_PRICE", 0));
//                     stockInD.addData("STOCK_UNIT",
//                                      baseParm.getValue("DISPENSE_UNIT", 0));
//
//                     stockInD.addData("OPT_USER", Operator.getID());
//                     stockInD.addData("OPT_DATE",
//                                      SystemTool.getInstance().getDate());
//                     stockInD.addData("OPT_TERM", Operator.getIP());
//                 }
                 //else {
                	 //messageBox("UPDATE"); 
            	 
               TParm baseParm = new TParm(TJDODBTool.getInstance().select(
               INVSQL.getInvBase(inv_code)));
            	 TParm batchSeqParm = new TParm(TJDODBTool.getInstance().select(
                         INVSQL.getInvStockMaxBatchSeq(in_org_code, inv_code)));
//                     System.out.println("===========bat====="+org_code+"00"+inv_code);
//                     System.out.println("===========bat====="+batchSeqParm);
                     if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
                         batch_seq = 1;
                     }
                     else {
                         batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
                     }
                    
//      			   VALID_DATE, STOCK_QTY, LASTDAY_TOLSTOCK_QTY, , DAYOUT_QTY, &
//      			   DAY_CHECKMODI_QTY, DAY_VERIFYIN_QTY, DAY_VERIFYIN_AMT, GIFTIN_QTY, DAY_REGRESSGOODS_QTY, &
//      			   DAY_REGRESSGOODS_AMT, , DAY_REQUESTOUT_QTY, , DAY_CHANGEOUT_QTY, &
//      			   , DAY_TRANSMITOUT_QTY, DAY_WASTE_QTY, DAY_DISPENSE_QTY, DAY_REGRESS_QTY, &
//      			   FREEZE_TOT, , OPT_USER, OPT_DATE, OPT_TERM
                     
                     stockInD.addData("TYPE", "UPDATE"); //更新数据
                     stockInD.addData("ORG_CODE", in_org_code);
                     stockInD.addData("INV_CODE", inv_code);
                     stockInD.addData("BATCH_SEQ", batch_seq);
                     stockInD.addData("BATCH_NO",
                          table_d.getParmValue().
                           getValue("BATCH_NO", i));
                     stockInD.addData("REGION_CODE", Operator.getRegion());
                     stockInD.addData("STOCK_QTY",
                    		 table_d.getItemDouble(i, "QTY"));
                     stockInD.addData("DAYIN_QTY",
                    		 table_d.getItemDouble(i, "QTY"));
                     if ("REQ".equals(request_type)) {
                         stockInD.addData("DAY_REQUESTIN_QTY",
                        		 table_d.getItemDouble(i, "QTY"));
                     }
                     else {
                         stockInD.addData("DAY_REQUESTIN_QTY", 0);
                     } 
                     if ("GIF".equals(request_type)) {
                         stockInD.addData("DAY_CHANGEIN_QTY",
                        		 table_d.getItemDouble(i, "QTY"));
                     }
                     else {
                         stockInD.addData("DAY_CHANGEIN_QTY", 0);
                     }
                     if ("RET".equals(request_type)) {
                         stockInD.addData("DAY_TRANSMITIN_QTY", 
                        		 table_d.getItemDouble(i, "QTY")); 
                     }
                     else {
                         stockInD.addData("DAY_TRANSMITIN_QTY", 0);
                     }

                     stockInD.addData("OPT_USER", Operator.getID());
                     stockInD.addData("OPT_DATE",
                                      SystemTool.getInstance().getDate());
                     stockInD.addData("OPT_TERM", Operator.getIP());
                     
                     stockInD.addData("UNIT_PRICE",
                           baseParm.getDouble("COST_PRICE", 0));
          stockInD.addData("STOCK_UNIT",
                           baseParm.getValue("DISPENSE_UNIT", 0));
                 }
            // }   
            }
 
        parm.setData("STOCK_IN_D", stockInD.getData()); 
        return parm;    
    } 
    

  
    /**
     * 出库单主项数据(更新标记位)
     * @param parm TParm
     * @return TParm
     */     
    public TParm getUpdateDispenseMData(TParm parm) {
    	//不用像请领一样，先看细项表的FINA_TYPE，再更新主表的FINAL_FLG 
        TParm DispenseM = new TParm(); 
        DispenseM.setData("DISPENSE_NO", this.getValueString("DISPENSE_NO_OUT"));
//      TParm DispenseD = parm.getParm("DISPENSE_D");  
        DispenseM.setData("FINA_FLG", "1");  
        DispenseM.setData("OPT_USER", Operator.getID());       
        DispenseM.setData("OPT_DATE",          
                         SystemTool.getInstance().getDate());
        DispenseM.setData("OPT_TERM", Operator.getIP());
        parm.setData("DISPENSE_M_UPDATE", DispenseM.getData());
        return parm;
    } 

    /**
     * 数据检核             
     * @return boolean
     */
    private boolean checkData() {
        table_d.acceptText();
        if ("".equals(this.getValueString("DISPENSE_NO_OUT"))) {
            this.messageBox("出库单号不能为空");
            return false;
        } 
        if (table_d.getRowCount() < 1) {
            this.messageBox("没有入库信息");
            return false;
        }
        boolean flg = false;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    flg = true;
                    break;
                }
            }
        } 
        //D表全为N则证明无数据
        if (!flg) {
            this.messageBox("没有入库信息");
            return false;
        }
        String request_type = this.getValueString("REQUEST_TYPE");
        // 出库部门
        String org_code = "";
        // 出库物资
        String inv_code = "";
 
        TParm result = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            inv_code = table_d.getParmValue().getValue("INV_CODE", i);
            if (!"".equals(inv_code)) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                } 
                if (table_d.getItemDouble(i, "QTY") >0) {
                     
                }else {
                	this.messageBox("入库数量不能小于或等于0");	
				}      	  
                if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
                    // 请领,调拨
                    org_code = this.getValueString("FROM_ORG_CODE");
                }
                else if ("RET".equals(request_type) ||
                         "WAS".equals(request_type)) {
                    // 退库,耗损 
                    org_code = this.getValueString("TO_ORG_CODE");
                } 
                TParm stockParm = new TParm();
//                if ("N".equals(table_d.getParmValue().getValue("SEQMAN_FLG", i))) {
//                    // 查询库存
//                    stockParm.setData("ORG_CODE", org_code);
//                    stockParm.setData("INV_CODE", inv_code);
//                    //非序号管理   批号？
////                    stockParm.setData("BATCH_SEQ",
////                                      table_d.getItemData(i, "BATCH_SEQ"));
//                    result =  InvStockMTool.getInstance().getStockQty(stockParm);
//                    if (result == null || result.getCount() <= 0 ||
//                        result.getDouble("SUM(STOCK_QTY)", 0) <
//                        table_d.getItemDouble(i, "QTY")) {
//                        this.messageBox("物资:" +
//                                        table_d.getItemString(i, "INV_CHN_DESC") +
//                                        "库存不足, 当前库存量为" +
//                                        result.getDouble("STOCK_QTY", 0));
//                        return false;
//                    }
//                }
               
            }
        }
        return true;
    }


    /**
     * 查询方法
     */  
    public void onQuery() { 
        TParm parm = new TParm(); 
        // 单据状态     
        // 未完成
         if (this.getRadioButton("RadioButton3").isSelected()) {
        	 table_m_select.setVisible(false);
        	 table_m.setVisible(true); 
        	parm.setData("TYPE","DISPENSE_OUT");
            parm.setData("IO_FLG", "2");
            parm.setData("FINA_FLG", "0");   
            // 查询时间     
            if (!"".equals(this.getValueString("START_DATE")) &&
                !"".equals(this.getValueString("END_DATE"))) {
                parm.setData("START_DATE", this.getValue("START_DATE"));
                parm.setData("END_DATE", this.getValue("END_DATE"));
            }
            // 单据类别
            if (!"".equals(this.getValueString("REQUEST_TYPE_Q"))) {
                parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE_Q"));
            }
            // 入库部门
                parm.setData("TO_ORG_CODE", Operator.getDept());    
            // 入库单号
            if (!"".equals(this.getValueString("DISPENSE_NO_INQ"))) {
                parm.setData("DISPENSE_NO_IN", this.getValueString("DISPENSE_NO_INQ"));
            } 
            // 出库单号 
            if (!"".equals(this.getValueString("DISPENSE_NO_OUTQ"))) {
                parm.setData("DISPENSE_NO_OUT", this.getValueString("DISPENSE_NO_OUTQ"));
            }
        
            TParm inparm = new TParm();    
            inparm.setData("DISPENSE_M", parm.getData());  
            // 查询 
            TParm result = TIOM_AppServer.executeAction(   
                "action.inv.INVDispenseAction", "onQueryMIn", inparm); 
            if (result == null || result.getCount() <= 0) {    
                this.messageBox("没有查询数据");
                table_m.removeRowAll();
                return;
            }
            // 全院药库部门作业单据
//            if (!request_all_flg) {
    //
//            }
            table_m.setParmValue(result);
        }  
        //1:完成   
        else if (this.getRadioButton("RadioButton1").isSelected()) {  
        	parm.setData("TYPE","DISPENSE_IN"); 
            parm.setData("IO_FLG", "1"); 
            parm.setData("FINA_FLG", "1");    
       	    table_m_select.setVisible(true);    
    	    table_m.setVisible(false);   
            if (!"".equals(this.getValueString("START_DATE")) &&
                    !"".equals(this.getValueString("END_DATE"))) {
                    parm.setData("START_DATE", this.getValue("START_DATE"));
                    parm.setData("END_DATE", this.getValue("END_DATE"));
                }
                // 单据类别
                if (!"".equals(this.getValueString("REQUEST_TYPE_Q"))) {
                    parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE_Q"));
                }
                // 入库部门
                    parm.setData("TO_ORG_CODE",Operator.getDept());
                // 入库单号
                if (!"".equals(this.getValueString("DISPENSE_NO_INQ"))) {
                    parm.setData("DISPENSE_NO_IN", this.getValueString("DISPENSE_NO_INQ"));
                } 
                // 出库单号 
                if (!"".equals(this.getValueString("DISPENSE_NO_OUTQ"))) {
                    parm.setData("DISPENSE_NO_OUT", this.getValueString("DISPENSE_NO_OUTQ"));
                }
          
                TParm inparm = new TParm();    
                inparm.setData("DISPENSE_M", parm.getData());  
                // 查询
                TParm result = TIOM_AppServer.executeAction(   
                    "action.inv.INVDispenseAction", "onQueryMIn", inparm);
                if (result == null || result.getCount() <= 0) {
                    this.messageBox("没有查询数据");
                    table_m_select.removeRowAll(); 
                    return;
                }             
                // 全院药库部门作业单据
//                if (!request_all_flg) {
        //
//                }  
                //System.out.println("---" + result);  
                table_m_select.setParmValue(result);
        }                                           
    }

    /**
     * 主项表格(TABLE_M)单击事件（根据出库信息插入入库的） 待修改 
     */
    public void onTableMClicked() { 
    	table_m.acceptText();
    	table_m_select.acceptText(); 
        int row = table_m.getSelectedRow();
        int Row = table_m_select.getSelectedRow();  
        //未完成状态
        if (getRadioButton("RadioButton3").isSelected()) 
        { 
        	table_m.setVisible(true); 
        	table_m_select.setVisible(false); 
        // 设置选中行模式  
        // table_d.setSelectionMode(0); 
        // 主项信息(TABLE中取得)    
        setValue("DISPENSE_NO_OUT", table_m.getItemString(row, "DISPENSE_NO_OUT"));
        setValue("REQUEST_TYPE", table_m.getItemString(row, "REQUEST_TYPE"));
        setValue("REQUEST_NO", table_m.getItemString(row, "REQUEST_NO"));
        Timestamp date = SystemTool.getInstance().getDate();
            setValue("DISPENSE_DATE",
                     table_m.getItemTimestamp(row, "DISPENSE_DATE"));
        setValue("TO_ORG_CODE", table_m.getItemString(row, "TO_ORG_CODE"));
        setValue("FROM_ORG_CODE",
                 table_m.getItemString(row, "FROM_ORG_CODE"));
        setValue("DISPENSE_USER",
                 table_m.getItemString(row, "DISPENSE_USER"));
        setValue("REMARK", table_m.getItemString(row, "REMARK"));
        setValue("FINA_FLG", table_m.getItemString(row, "FINA_FLG"));
        setValue("CHECK_DATE", table_m.getItemTimestamp(row, "CHECK_DATE"));
        setValue("URGENT_FLG", table_m.getItemString(row, "URGENT_FLG"));
        setValue("CHECK_USER", table_m.getItemString(row, "CHECK_USER"));

        // 明细信息
        TParm parm = new TParm();
        TParm result = new TParm();   
        //未完成
        //出库号       
            parm.setData("DISPENSE_NO",   
                         table_m.getItemString(row, "DISPENSE_NO_OUT")); 
            result = InvDispenseDTool.getInstance().onQueryDispenseDIn(parm);
        table_d.removeRowAll();  
        table_d.setParmValue(result);
        }    
        //完成状态
        if (getRadioButton("RadioButton1").isSelected())
        {       
        	table_m_select.setVisible(true); 
        	table_m.setVisible(false);
               // 主项信息(TABLE中取得)                
               setValue("DISPENSE_NO", table_m_select.getItemString(Row, "DISPENSE_NO_IN"));
               setValue("REQUEST_TYPE", table_m_select.getItemString(Row, "REQUEST_TYPE"));
               Timestamp date = SystemTool.getInstance().getDate();
                   setValue("DISPENSE_DATE",
                		   table_m_select.getItemTimestamp(Row, "DISPENSE_DATE"));
               setValue("TO_ORG_CODE", table_m_select.getItemString(Row, "TO_ORG_CODE"));
               setValue("FROM_ORG_CODE",
            		   table_m_select.getItemString(Row, "FROM_ORG_CODE"));
               setValue("DISPENSE_USER",
            		   table_m_select.getItemString(Row, "DISPENSE_USER"));
               setValue("REMARK", table_m_select.getItemString(Row, "REMARK"));
               setValue("FINA_FLG", table_m_select.getItemString(Row, "FINA_FLG"));
               setValue("CHECK_DATE", table_m_select.getItemTimestamp(Row, "CHECK_DATE"));
               setValue("URGENT_FLG", table_m_select.getItemString(Row, "URGENT_FLG"));
               setValue("CHECK_USER", table_m_select.getItemString(Row, "CHECK_USER"));
        	 // 明细信息  
        	  TParm parm = new TParm();
              TParm result = new TParm();
            	//入库号        
                parm.setData("DISPENSE_NO",    
                             this.getValueString("DISPENSE_NO"));  
                parm.setData("IO_FLG","1");    
                result = InvDispenseDTool.getInstance().onQueryDispenseD(
                    parm); 
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有出库明细");
                return;
            }       
            table_d.removeRowAll();
            table_d.setParmValue(result);   
        }  
    } 
    /**
     * 全选事件
     */
    public void onSelectAll() {
        String flg = "Y";
        if (getCheckBox("SELECT_ALL").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
            table_d.setItem(i, "SELECT_FLG", flg);
        }
    }


    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table的列名
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
          
                if (qty <= 0) { 
                    this.messageBox("出库数量不能小于或等于0");
                    return true;
                }
                else if (qty >
                         (table_d.getItemDouble(row, "REQUEST_QTY") -
                          table_d.getItemDouble(row, "ACTUAL_QTY"))) {
                    this.messageBox("出库数量不能大于申请数量");
                    return true;
                }
                else {
                    // 出库金额
                    table_d.setItem(row, "SUM_AMT", qty *
                                    table_d.getItemDouble(row, "COST_PRICE"));
                    return false;
                }
            
        }  
//        else if("SELECT_FLG".equals(columnName)){
//            String dispenseNo = this.getValueString("DISPENSE_NO_OUT");
//            String dispenseNoIN  = this.getValueString("DISPENSE_NO"); 
//            TParm  parm  = new TParm();     
//            parm.setData("DISPENSE_NO", dispenseNo);      
//            TParm  dispenseD = InvDispenseDTool.getInstance().onQueryDispenseD(parm);
//            TParm  dispenseDD = new TParm();                
//            for (int i = 0; i <dispenseD.getCount("INV_CODE"); i++) {
//                //在途    
//            	if ("Y".equals(table_d.getItemString(row, "SEQMAN_FLG"))) {
//            		if ("Y".equals(table_d.getItemString(row,"SELECT_FLG" ))){ 
//                	   if (getRadioButton("RadioButton2").isSelected()) { 
//                    	   dispenseDD.setData("DISPENSE_NO", this.getValueString("DISPENSE_NO_OUT"));   
//                    	   dispenseDD.setData("IO_FLG", "2") ;                  	   
//                    	   dispenseDD = INVDispenseDDTool.getInstance().onQuery(dispenseDD);     
//                           if (dispenseDD == null || dispenseDD.getCount() <= 0) { 
//                               this.messageBox("没有出库明细");
//                               return false;   
//                           }            
//                           table_dd.setParmValue(dispenseDD);       
//                	        }                            
//                	   //已完成
//                       else if(getRadioButton("RadioButton1").isSelected()){    
//                    	   dispenseDD.setData("DISPENSE_NO", this.getValueString("DISPENSE_NO"));   
//                    	   dispenseDD.setData("IO_FLG", "1");                  	   
//                    	   dispenseDD = INVDispenseDDTool.getInstance().onQuery(dispenseDD);    
//                           if (dispenseDD == null || dispenseDD.getCount() <= 0) { 
//                               this.messageBox("没有入库明细");
//                               return false;
//                           }
//                           table_dd.setParmValue(dispenseDD);       
//                       }
//            	}  
//            	}
//            }
//        }
        return false;
    }
 

    /**
     * 单据状态变更事件
     */     
    public void onChangeFinaFlg() { 
        TTextField dispense_no_inq = this.getTextField("DISPENSE_NO_INQ");
        //未完成(可以删除)
        if (getRadioButton("RadioButton3").isSelected()) {
        	dispense_no_inq.setEnabled(true);
        	table_m.setVisible(true);
        	table_m_select.setVisible(false);   
//          ( (TMenuItem) getComponent("delete")).setEnabled(false);
//          ( (TMenuItem) getComponent("save")).setEnabled(false);
        	this.onClear();
        	this.onQuery();
        }         
        //完成 (不可以删除)
        else if (getRadioButton("RadioButton1").isSelected()) {
        	dispense_no_inq.setEnabled(true);
        	table_m.setVisible(false);
        	table_m_select.setVisible(true);
//          ( (TMenuItem) getComponent("delete")).setEnabled(true);
//          ( (TMenuItem) getComponent("save")).setEnabled(true); 
        	this.onClear();
        	this.onQuery();
        }  
    }
    /**
	 * 打印
	 */
	public void onPrint(){
		TParm tableParm = table_d.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("无打印数据") ;
			return ;
		}
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("ORG_DESC", i+1); //赋值 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("DISPENSE_UNIT", i)); 
			result.addData("QTY", tableParm.getValue("QTY", i)); 
			result.addData("COST_PRICE", tableParm.getValue("COST_PRICE", i)); 
			result.addData("AMT", tableParm.getValue("AMT", i)); 
		}
		result.setCount(tableParm.getCount()) ;    //设置报表的行数
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");//排序
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");//排序
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "AMT");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//制表时间
		String orgDesc = this.getValueString("ORG_CODE").length()>0?tableParm.getValue("ORG_DESC", 0):"全部" ;
		String requestType = this.getValueString("REQUEST_TYPE").length()>0?this.getValueString("REQUEST_TYPE"):"全部" ;
		printParm.setData("TITLE", "TEXT","入库单") ;
		//printParm.setData("DATE", "TEXT","统计区间:"+date) ;
		printParm.setData("P_DATE", "TEXT", "制表时间: " + pDate);
		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
		printParm.setData("ORG_DESC", "TEXT", "部门: " + orgDesc);
		//printParm.setData("REQUEST_TYPE", "TEXT", "请领类别: " + requestType);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDispenseIn.jhw",
				printParm);
	}

    /**
     * 初始画面数据
     */
    private void initPage() {
        table_m = getTable("TABLE_M");   
        table_d = getTable("TABLE_D"); 
        table_m_select = getTable("TABLE_M_SELECT");   
        // 全院药库部门作业单据
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        } 
        else {  
            request_all_flg = true;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间  
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");      
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
            
        // TABLE_D值改变事件 
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue"); 
        //CABINET_ID 回车事件 取消        
//        callFunction("UI|CABINET_ID|addEventListener", 
//				TTextFieldEvent.KEY_PRESSED, this, "onCharge");
        //单据状态变更事件     
         onChangeFinaFlg();   
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

    /**
     * 得到TCheckBox对象
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 得到TTextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    



}
