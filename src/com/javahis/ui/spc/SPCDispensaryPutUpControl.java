package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCDispensaryPutUpTool;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.spc.SPCInStoreReginTool;
import jdo.spc.SPCMaterialLocTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:药房普药上架control
 * </p>
 * 
 * <p>
 * Description: 药房普药上架入库control
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author Yuanxm 20121015
 * @version 1.0
 */
public class SPCDispensaryPutUpControl extends TControl {

	TTable table_N;// 未入库
	TTable table_Y;// 已入库

	TPanel N_PANEL;
	TPanel Y_PANEL;
	
	TParm parmData = new TParm();
	int countResult = 0;// 记录移除药品的总数
	
	 // 单据类型
    private String request_type;
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
    
    private TParm labelParm = new TParm();
    

	/**
	 * 初始化方法
	 */
	public void onInit() {
		
		/**
		TFrame tFrame = (TFrame) getComponent("UI");
		final TTextField dispenseNo = this.getTextField("BOX_ESL_ID");
		tFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowOpened(java.awt.event.WindowEvent evt) {
				dispenseNo.requestFocus();
			}
		});*/
		Timestamp sysDate = SystemTool.getInstance().getDate();
		setValue("END_DATE",sysDate.toString().substring(0, 10).replace('-', '/'));
		setValue("START_DATE",StringTool.rollDate(sysDate, -2).toString().substring(0, 10).replace('-', '/') );
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
	}


	public void onQuery(){
		
		//周转箱
		String boxEslId = getValueString("BOX_ESL_ID");
		
		//出库单号
		String dispenseNo = getValueString("DISPENSE_NO");
		
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		N_PANEL = (TPanel)getComponent("N_PANEL");
		Y_PANEL = (TPanel)getComponent("Y_PANEL");
		
		
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO",dispenseNo);
		parm.setData("BOX_ESL_ID",boxEslId);
		
		String startDate = getValueString("START_DATE");
		if(startDate != null && !startDate.equals("")){
			startDate = startDate.substring(0,10);
			parm.setData("START_DATE",startDate);
		}
		String endDate = getValueString("END_DATE");
		if(endDate != null && !endDate.equals("")){
			endDate = endDate.substring(0,10);
			parm.setData("END_DATE",endDate);
		}
		
		if(N_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","N");
			parm.setData("UPDATE_FLG","1");
		}else if(Y_PANEL.isShowing()){
			parm.setData("IS_PUTAWAY","Y");
			parm.setData("UPDATE_FLG","3");
		}
		
		TParm result = SPCDispensaryPutUpTool.getInstance().onQuery(parm);
		labelParm = SPCDispensaryPutUpTool.getInstance().onQuery(parm);
		this.setValue("BOX_ESL_ID", boxEslId);
		this.setValue("DISPENSE_NO", dispenseNo);

		if(result.getCount() <  0 ){
			 
			if(N_PANEL.isShowing()){
				table_N.setParmValue(new TParm());
			}
			if(Y_PANEL.isShowing()){
				table_Y.setParmValue(new TParm());
			}
			return ;
		}
		
		String orgChnDesc = (String)result.getData( "ORG_CHN_DESC",0);
		setValue("ORG_CHN_DESC", orgChnDesc);
		//this.getTextField("DISPENSE_NO").setEditable(false);
		//this.getTextField("ORG_CHN_DESC").setEditable(false);
		if(N_PANEL.isShowing()){
			table_N.setParmValue(result);
			
			/**
			 * 去除重复合并
			 */
			
			int count = labelParm.getCount() ;
			for(int i = 0;i < count;i++) { 
				TParm rowParm = labelParm.getRow(i);
				String orderCode = rowParm.getValue("ORDER_CODE") ;
				 
				for(int j = i+1;j < count;j++){ 
					TParm rowParm1 = labelParm.getRow(j);
					String orderCode1 = rowParm1.getValue("ORDER_CODE") ;
					 
				   if(orderCode.equals(orderCode1)){ 
				   		 
					   labelParm.removeRow(j); 
					   j--;
					   count--;
				   }
				}
			} 
			labelParm.setCount(count);
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>() ;
			//调用电子标签接口
			for(int i = 0 ; i < labelParm.getCount() ; i++ ){
				Map<String, Object> map = new LinkedHashMap<String, Object> ();		
				TParm rowParm = (TParm)labelParm.getRow(i);
				String orderDesc = rowParm.getValue("ORDER_DESC");
				map.put("ProductName", orderDesc);
				map.put("SPECIFICATION",   rowParm.getValue("SPECIFICATION"));
				map.put("TagNo", rowParm.getValue("ELETAG_CODE"));
				map.put("Light", 50);
				
				String orgCode = rowParm.getValue("ORG_CODE");
				String apRegion = getApRegion(orgCode);
				if(apRegion == null || apRegion.equals("")){
					System.out.println("标签区域没有设置部门代码："+orgCode);
				}
				map.put("APRegion", apRegion);
				list.add(map);
			}
			try{
				String url = Constant.LABELDATA_URL ;
				LabelControl.getInstance().sendLabelDate(list, url);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
		    	System.out.println("调用电子标签服务失败");

			}
		}else if(Y_PANEL.isShowing()){
			table_Y.setParmValue(result);
		}
		
			
	}

	/**
	 * 周转箱回车事件
	 * */
	public void onMonZHOUZX() {
		
		onQuery();
		//this.getTextField("BOX_ESL_ID").setEditable(false);
		this.getTextField("ELETAG_CODE").grabFocus();
		
	}
	
	/**
	 * 出库单号回车事件
	 */
	public void onDispenseNo(){
		try {
			onQuery();
		}catch (Exception e) {
			// TODO: handle exception
		}
		//this.getTextField("BOX_ESL_ID").setEditable(false);
		this.getTextField("ELETAG_CODE").grabFocus();
	}

	/**
	 * 货位电子标签的回车事件
	 * 
	 * 
	 */
	public void onClickByElectTags() {
		
		//货位电子标签
		String eletagCode =  getValueString("ELETAG_CODE");
		if (StringUtil.isNullString(eletagCode)){
	        return;
		}
		String boxEslId = getValueString("BOX_ESL_ID");
		String dispenseNo = getValueString("DISPENSE_NO");
		
		
		if(!StringUtil.isNullString(dispenseNo)){
			this.setValue("DISPENSE_NO", dispenseNo);
		}
		
		table_N.acceptText();
		
		//搜索IND_MATERIALLOC,得到ORDER_CODE
		TParm tabParm = table_N.getParmValue();
			
		String labelNo = "";
		String productName = "";
		String orgCode = "";
		String orderCode = "";
		String spec = "";
		int count = table_N.getParmValue().getCount();
		
		//判断是否找到
		boolean b = false; 
    	TParm mRowParm = new TParm();
		for (int i = 0; i < count; i++) {
			TParm dParm = tabParm.getRow(i);
			
			if(eletagCode.equals(dParm.getValue("ELETAG_CODE"))){
				
				//根据出库单号查询IND_DISPENSEM表信息
				TParm inParmSec = new TParm() ;
				dispenseNo = dParm.getValue("DISPENSE_NO");
				inParmSec.setData("DISPENSE_NO",dispenseNo);
		    	TParm mParm = SPCInStoreReginTool.getInstance().onQueryDispenseM(inParmSec);
		    	mRowParm = mParm.getRow(0);
				//初始化值 
				setValueAll(mRowParm);
				labelNo = eletagCode;
				productName = dParm.getValue("ORDER_DESC");
				orgCode = dParm.getValue("ORG_CODE");
				orderCode= dParm.getValue("ORDER_CODE");
				//SPCDispensaryPutUpTool.getInstance().updateDispensed(rowParm);
				onSave(mRowParm, dParm);
				b = true;
				
				table_N.removeRow(i);
				count--;
				i--;
				 
			}
			 
		}
		
		if(table_N.getRowCount() <= 0 ){
			this.setValue("BOX_ESL_ID", "");
			this.setValue("DISPENSE_NO", "");
		}else{
			this.setValue("BOX_ESL_ID", boxEslId);
		}
		this.clearValue("ELETAG_CODE");
		
		//是的，调用电子标签接口显示库存与闪烁
		if(b){
			
			
			TParm inParm = new TParm();
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("ORG_CODE",orgCode);
			inParm.setData("ELETAG_CODE",eletagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			spec = outParm.getValue("SPECIFICATION",0);
		    /**
			 * 调用电子标签
			 */
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("ProductName", productName);
			map.put("SPECIFICATION",  spec+" "+outParm.getValue("QTY",0));
			map.put("TagNo", labelNo);
			map.put("Light", 0);
			 
			//对到部门对应的区域代码
			String apRegion = getApRegion(orgCode);
			if(apRegion == null || apRegion.equals("")){
				System.out.println("标签区域没有设置部门代码："+orgCode);
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
			
			/**
			TParm inParm = new TParm();
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("ORG_CODE",orgCode);
			inParm.setData("ELETAG_CODE",eletagCode);
			TParm outParm = SPCMaterialLocTool.getInstance().onQueryIndStockEleTag(inParm);
			spec = outParm.getValue("SPECIFICATION",0);
			 
			EleTagControl.getInstance().login();
			EleTagControl.getInstance().sendEleTag(labelNo, productName, spec, outParm.getValue("QTY",0), 0);*/
		}
		
		//onQuery() ;
		return;
	}
	
	
	/**
     * 保存方法
     */
    public void onSave(TParm mParm,TParm dParm) {
        
        /** 更新保存(入库单主项及细项) */
        // 2.出库批号判断
        String batchvalid = getBatchValid(mParm.getValue("REQTYPE_CODE"));
        // 3.入库部门库存是否可异动
        if (!getOrgBatchFlg(in_org_code)) {
            return;
        }
        if (!"THI".equals(request_type)) {
            // 入库作业
            getDispenseOutIn(out_org_code, in_org_code, batchvalid,
                             out_flg, in_flg,mParm,dParm);
        }else {
            // 其它入库作业
            getDispenseOtherIn(in_org_code, in_flg,mParm,dParm);
        }
        
    }
    
    /**
     * 入库作业
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg,TParm mParm,TParm dParm) {

        TParm parm = new TParm();
        
        // 主项信息(OUT_M)
        parm = getDispenseMParm(parm, "3",mParm);
        
        // 细项信息(OUT_D)
        if (!CheckDataD(dParm)) {
            return;
        }
        parm = getDispenseDParm(parm,dParm);
       
        if (!"".equals(in_org_code)) {
            // 入库部门(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // 是否入库(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // 执行数据新增
        TParm result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertIn", parm);

        // 保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        //this.messageBox("P0001");
        
        /**
        // 请领入库草药自动维护收费
        if ("DEP".equals(this.getValueString("REQTYPE_CODE"))) {
            // 判断药品零差价注记
            boolean pha_price_flg = false;
            TParm parmFlg = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDSysParm()));
            if (parmFlg.getCount() > 0) {
                pha_price_flg = parmFlg.getBoolean("PHA_PRICE_FLG", 0);
            }
            updateGrpricePrice(this.getValueString("DISPENSE_NO"),
                               pha_price_flg);
        }*/
       // onClear();
    }
    
    /**
     * 其他入库作业
     * @param in_org_code String
     * @param flg boolean
     */
    private void getDispenseOtherIn(String in_org_code, boolean in_flg,TParm mParm,TParm dParm) {
        
        TParm parm = new TParm();
        parm.setData("ORG_CODE", in_org_code);
        // 使用单位
        parm.setData("UNIT_TYPE", u_type);
        // 申请单类型
        parm.setData("REQTYPE_CODE", request_type);
         
        parm = getDispenseMParm(parm, "3",mParm);
        // 细项信息(OUT_D)
        if (!CheckDataD(dParm)) {
            return;
        }
        parm = getDispenseDParm(parm,dParm);
        

        if (!"".equals(in_org_code)) {
            // 入库部门(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // 是否入库(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // 执行数据新增
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertOtherIn", parm);
        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }
    
	
	/**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg,TParm mParm ) {
        // 药库参数信息
        TParm sysParm = getSysParm();
        // 是否回写购入价格
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
       
        parmM.setData("DISPENSE_NO", mParm.getValue("DISPENSE_NO"));
        
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
        parmM.setData("REQTYPE_CODE", mParm.getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", mParm.getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", mParm.getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", mParm.getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", mParm.getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", mParm.getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", mParm.getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", mParm.getValue("WAREHOUSING_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
       
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", SystemTool.getInstance().getDate());
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        //药品种类--普药:1,麻精：2
        parmM.setData("DRUG_CATEGORY","1");

        parmM.setData("REASON_CHN_DESC", mParm.getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", mParm.getValue("UNIT_TYPE"));
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        parmM.setData("REGION_CODE", Operator.getRegion());
        
        parmM.setData("CHECK_PUTAWAY","Y");
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
    private TParm getDispenseDParm(TParm parm,TParm rowParm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String batch_no = "";
        int count = 0;
        String order_code = "";
        String valid_date = "";
     
        parmD.setData("DISPENSE_NO", count,rowParm.getValue("DISPENSE_NO"));
        parmD.setData("SEQ_NO", count, rowParm.getValue("SEQ_NO"));
        parmD.setData("REQUEST_SEQ", count,rowParm.getInt("REQUEST_SEQ"));
        order_code = rowParm.getValue("ORDER_CODE");
        parmD.setData("ORDER_CODE", count, order_code);
        parmD.setData("QTY", count, rowParm.getDouble( "QTY"));
        parmD.setData("UNIT_CODE", count, rowParm.getValue(
            "UNIT_CODE"));
        parmD.setData("RETAIL_PRICE", count, rowParm.getDouble(
            "RETAIL_PRICE"));
        parmD.setData("STOCK_PRICE", count, rowParm.getDouble(
            "STOCK_PRICE"));
        parmD.setData("ACTUAL_QTY", count, rowParm.getDouble(
            "OUT_QTY"));
        parmD.setData("PHA_TYPE", count, rowParm.getValue(
            "PHA_TYPE"));
      
        parmD.setData("BATCH_SEQ",count,rowParm.getInt("BATCH_SEQ"));
        
        //供应商
        parmD.setData("SUP_CODE",count,rowParm.getValue("SUP_CODE"));
        
        batch_no = rowParm.getValue("BATCH_NO");
        parmD.setData("BATCH_NO", count, batch_no);
        valid_date = rowParm.getValue("VALID_DATE");
        if ("".equals(valid_date)) {
            parmD.setData("VALID_DATE", count, tnull);
        } else {
            parmD.setData("VALID_DATE", count,
            		rowParm.getTimestamp("VALID_DATE"));
        }
        parmD.setData("DOSAGE_QTY", count, rowParm.getDouble("DOSAGE_QTY"));
        parmD.setData("STOCK_QTY", count, rowParm.getDouble("STOCK_QTY"));
        parmD.setData("OPT_USER", count, Operator.getID());
        parmD.setData("OPT_DATE", count, date);
        parmD.setData("OPT_TERM", count, Operator.getIP());
        parmD.setData("REGION_CODE", count, Operator.getRegion());
        parmD.setData("PUTAWAY_USER",count,Operator.getID());
        parmD.setData("PUTAWAY_DATE",count,SystemTool.getInstance().getDate());
        parmD.setData("IS_PUTAWAY",count,"Y");
        //验收价格
        parmD.setData("VERIFYIN_PRICE",count,rowParm.getDouble("VERIFYIN_PRICE"));
        //整盒价格
        parmD.setData("INVENT_PRICE",count,rowParm.getDouble("INVENT_PRICE"));
        
        //Supplier order_code
        parmD.setData("SUP_ORDER_CODE",count,rowParm.getValue("SUP_ORDER_CODE"));
        count++;
        
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    
    public void setValueAll(TParm mParm){
    	request_type = mParm.getValue("REQTYPE_CODE");
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
                out_org_code = mParm.getValue("TO_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("APP_ORG_CODE");
                in_flg = true;
            }
            else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
                out_org_code = mParm.getValue("APP_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("TO_ORG_CODE");
                in_flg = true;
            }
            else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
                out_org_code = mParm.getValue("TO_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("APP_ORG_CODE");
                in_flg = false;
            }
            else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
                out_org_code = mParm.getValue("APP_ORG_CODE");
                out_flg = true;
                in_org_code = mParm.getValue("TO_ORG_CODE");
                in_flg = false;
            }
            else if ("THI".equals(request_type)) {
                out_org_code = mParm.getValue("TO_ORG_CODE");
                out_flg = false;
                in_org_code = mParm.getValue("APP_ORG_CODE");
                in_flg = true;
            }
    }
    
    
    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataD(TParm dParm) {
        // 判断数据正确性
        double qty = dParm.getDouble( "OUT_QTY");
        if (qty <= 0) {
            this.messageBox("入库数数量不能小于或等于0");
            return false;
        }
        double price = dParm.getDouble("STOCK_PRICE");
        if (price <= 0) {
            this.messageBox("成本价不能小于或等于0");
            return false;
        }
       
        return true;
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
            this.messageBox("药房批次过帐中则提示请先手动做日结");
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
	 * TPanel改变事件
	 * */
	public void onTPanlClick() {
		onQuery();
	}
	
	

	/**
	 * 清空操作
	 * */
	public void onClear() {
		String controlName = "DISPENSE_NO;BOX_ESL_ID;ORG_CHN_DESC;ELETAG_CODE";
		this.clearValue(controlName);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		this.getTextField("DISPENSE_NO").setEditable(true);
		this.getTextField("ORG_CHN_DESC").setEditable(true);
		table_N.removeRowAll();
		table_Y.removeRowAll();
		this.getTextField("BOX_ESL_ID").grabFocus();
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
    
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * 得到药房或者药库或者病区对应的区号
	 * @param orgCode
	 * @return
	 */
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
		
	
	
		
}
