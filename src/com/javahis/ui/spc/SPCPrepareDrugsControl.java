package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCDispenseOutTool;
import jdo.spc.SPCOutStoreTool;
import jdo.spc.SPCOutpatientOutStoreTool;
import jdo.spc.SPCPoisonTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 科室备药麻精出智能柜
 * </p>
 * 
 * <p>
 * Description: 科室备药麻精出智能柜
 * </p>
 * 
 * <p>d
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author
 * @version 1.0
 */
public class SPCPrepareDrugsControl extends TControl {

	/**
	 * 容器
	 */
	TTable table_D ;
	
	/**
	 * 麻精
	 */
	TTable table_M;
	
	//未入库，已入库列表
	TTable table_List; 
	
    String ip ;
	
	String containerId ;
	
	/**
	 * 智能柜本机物理IP
	 */
	String guardIp;
	
	/**
	 * 智能柜部门
	 */
	String orgCode ;
	
	String userId;
	
	String cabinetId;

	String appOrgCode = "I01;C02";
	
	String dispenseNo = "";
	
	String requestNo ;
	
	
	/**
	 * 初始化
	 */
	public void onInit() {
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
		table_List = getTable("TABLE_LIST");
		String parameter = (String)this.getParameter();
		if(parameter != null && !parameter.equals("")){
			appOrgCode = parameter ;
		}
		initData() ;
	 
	}
	/**
	 * 初始化数据
	 */
	public void initData(){
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress();//获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		TParm parm = new TParm();
		parm.setData("IP",ip);
		TParm result = SPCCabinetTool.getInstance().onQuery(parm);
		
		guardIp = result.getValue("GUARD_IP",0);
		orgCode = result.getValue("ORG_CODE",0);
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		
		
       
        parm.setData("ORG_CODE",appOrgCode);
        // 未出库
        parm.setData("STATUS", "N");
        result = SPCOutpatientOutStoreTool.getInstance().onQuery(parm);
		
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
		if(result.getCount() < 0 ){
			table_List.setParmValue(new TParm());
			this.messageBox("没有查询到数据");
			return ;
		}
		//如果不为空，则取病区
		//String stationCode = result.getValue("STATION_CODE",0);
		//setValue("STATION_CODE", stationCode);
		table_List.setParmValue(result);
		
	}
	
	public void onQuery(){
		
		table_List = getTable("TABLE_LIST");
		requestNo = getValueString("REQUEST_NO");
		TParm parm = new TParm();
		parm.setData("REQUEST_NO",requestNo);
		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
		parm.setData("START_DATE", startDate);
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        parm.setData("END_DATE", endDate);
        // 出库状态
        if (getRadioButton("UPDATE_FLG_N").isSelected()) {
            // 未出库
            parm.setData("STATUS", "N");
            getTextField("CONTAINER_ID").setEditable(true);
			getTextField("TOXIC_ID").setEditable(true);
        }
        else if (getRadioButton("UPDATE_FLG_Y").isSelected()) {
            //已出库
            parm.setData("STATUS", "Y");
        }
        parm.setData("ORG_CODE",appOrgCode);
		TParm result = SPCOutpatientOutStoreTool.getInstance().onQuery(parm);
		
		if(result.getCount() < 0 ){
			table_List.setParmValue(new TParm());
			table_M = getTable("TABLE_M");
			table_D = getTable("TABLE_D");
			table_List = getTable("TABLE_LIST");
			table_D.setParmValue(new TParm());
			table_M.setParmValue(new TParm());
			table_List.setParmValue(new TParm());
			this.messageBox("没有查询到数据");
			
			return ;
		}
		//如果不为空，则取病区
		//String stationCode = result.getValue("STATION_CODE",0);
		//setValue("STATION_CODE", stationCode);
		table_List.setParmValue(result);
		this.getTextField("CONTAINER_ID").grabFocus();
		
	}
	
	public void onRequestNoClicked(){
		onQuery();
	}
	
	 /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableListClicked() {
        int rowList  = table_List.getSelectedRow();
        if (rowList != -1) {
            // 主项表格选中行数据上翻
            
        	
            // 申请单号
            requestNo = (String)table_List.getItemData(rowList, "REQUEST_NO");
            // 出库单号
            dispenseNo = (String)table_List.getItemData(rowList, "DISPENSE_NO"); 
            String appOrgCode =  (String)table_List.getItemData(rowList, "APP_ORG_CODE");
            String toOrgCode = (String)table_List.getItemData(rowList, "TO_ORG_CODE");
            String reqtypeCode = (String)table_List.getItemData(rowList, "REQTYPE_CODE");
            setValue("REQUEST_NO", requestNo);
            setValue("DISPENSE_NO", dispenseNo);
            setValue("APP_ORG_CODE", appOrgCode);
            setValue("TO_ORG_CODE", toOrgCode);
            setValue("REQTYPE_CODE", reqtypeCode);
            if (getRadioButton("UPDATE_FLG_N").isSelected()) {
                // 明细信息  未出库
                getTableDInfo(requestNo);
                
            }else {
                // 明细信息 出库
                getTableDInfo2(dispenseNo);
                
            }
            rowList = -1;
        }
    }
    
    public void getTableDInfo(String requestNo){
    	
    	TParm parm = new TParm();
    	parm.setData("REQUEST_NO",requestNo);
    	
    	//传进去是不等于
    	parm.setData("UPDATE_FLG","3");
        // 取得未完成的细项信息
        TParm result = SPCDispenseOutTool.getInstance().onQuery(parm);
        if(result.getCount() <= 0){
        	this.messageBox("没有查到对应数据");
        	return ;
        }
        table_D = getTable("TABLE_D");
        table_D.setParmValue(result);
        table_D.setSelectedRow(0);
        onTableDClicked();
        
    }
    
    public void getTableDInfo2(String dispenseNo){
    	TParm parm = new TParm() ;
    	parm.setData("DISPENSE_NO",dispenseNo);
    	 
        TParm result = SPCDispenseOutTool.getInstance().onQueryDispense(parm);
         
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_D.setParmValue(result);
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
        if (getRadioButton("UPDATE_FLG_N").isSelected()) {
            flg = false;
        }
        else {
            flg = true;
        }
        for (int i = 0; i < result.getCount(); i++) {
          //  parm.setData("SELECT_FLG", i, flg);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            
            if (getRadioButton("UPDATE_FLG_N").isSelected()) {
                parm.setData("OUT_QTY", i, qty - actual_qty);
            }
            else {
                parm.setData("OUT_QTY", i, qty);
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
           
            
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
	 * 容器单单击事件
	 */
	public void onContainerIdClicked(){
		String containerId = getValueString("CONTAINER_ID");
		if(StringUtil.isNullString(containerId)){
			this.messageBox("请扫描或输入容器编码!");
			return ;
		}
		table_D.acceptText();
		TParm inParm = new TParm();
		inParm.setData("CONTAINER_ID",containerId);
		TParm result = SPCContainerTool.getInstance().onQueryBy(inParm);
		if(result.getCount() >  0 ){
			String orderCode = result.getValue("ORDER_CODE",0);
			int tableDselectRow =  table_D.getSelectedRow();
			TParm  parm = table_D.getParmValue();
			
			String orderCodeD = parm.getValue("ORDER_CODE",tableDselectRow);
			if(!orderCodeD.equals(orderCode)){
				this.messageBox("该容器的麻精品种与当前要出库的品种不同,请重新操作!");
				setValue("CONTAINER_ID", "");
				this.getTextField("CONTAINER_ID").grabFocus();
				return ;
			}else {
				// modify by wangbin 2015/1/29 内部需求 #788 门急诊药房管理 > 备药申请麻精出库 相关调整 START
				TParm tparm = new TParm();
				tparm.setData("CONTAINER_ID", containerId);
				TParm deleteResult = SPCPoisonTool.getInstance().deleteContainerWithToxic(tparm);
				if (deleteResult.getErrCode() < 0) {
					this.messageBox("解除麻精盒子与麻精药品的绑定关系时发生异常");
					err("ERR:" + deleteResult.getErrCode() + deleteResult.getErrText());
					return;
				}
				
				deleteResult = SPCPoisonTool.getInstance().deleteContainerWithCabinet(tparm);
				if (deleteResult.getErrCode() < 0) {
					this.messageBox("解除麻精盒子与智能柜的绑定关系时发生异常");
					err("ERR:" + deleteResult.getErrCode() + deleteResult.getErrText());
					return;
				}
				// modify by wangbin 2015/1/29 内部需求 #788 门急诊药房管理 > 备药申请麻精出库 相关调整 END
				double toxicQty = result.getDouble("TOXIC_QTY",0);
				setValue("TOXIC_QTY", toxicQty);
				setValue("CONTAINER_DESC", result.getValue("CONTAINER_DESC",0));
			}
		}else {
			this.messageBox("没有该容器!");
			setValue("CONTAINER_ID", "");
			setValue("CONTAINER_DESC","");
			setValue("TOXIC_QTY", "");
			this.getTextField("CONTAINER_ID").grabFocus();
			return ;
		}
		this.getTextField("TOXIC_ID").grabFocus();
	}
	
	 /**
	 * 条码事件
	 */
	public void onCode(){
		//容器与麻精流水号同一个控件
		String toxicId = getValueString("TOXIC_ID");
		TParm sysParm = SPCContainerTool.getInstance().onQuerySysParm();
		int length = sysParm.getInt("TOXIC_LENGTH");
		
		//如果containerId的长度与麻精长度一致,处理麻精，不是，容器
		if(toxicId.length() == length){
			onToxicIdClicked(toxicId);
		}else{
			onContainerIdClicked(toxicId);
		}
	}
	
	
	/**
	 * 容器单单击事件
	 */
	public void onContainerIdClicked(String containerId){
		 
		if(StringUtil.isNullString(containerId)){
			this.messageBox("容器为空!");
			return ;
		}
		table_D.acceptText();
		table_M.acceptText() ;
		TParm inParm = new TParm();
		inParm.setData("CONTAINER_ID",containerId);
		TParm result = SPCContainerTool.getInstance().onQueryBy(inParm);
		if(result.getCount() >  0 ){
			String orderCode = result.getValue("ORDER_CODE",0);
			int tableDselectRow =  table_D.getSelectedRow();
			TParm  parm = table_D.getParmValue();
			
			String orderCodeD = parm.getValue("ORDER_CODE",tableDselectRow);
			if(!orderCodeD.equals(orderCode)){
				this.messageBox("该容器的麻精品种与当前要出库的品种不同,请重新操作!");
				setValue("CONTAINER_ID", "");
				this.getTextField("CONTAINER_ID").grabFocus();
				return ;
			}else {
				
				
				TParm inParmAll = new TParm();
				 
				 
				//根据容器ID查询对应的麻精信息
				String cabinetId = getValueString("CABINET_ID");
				inParm.setData("CABINET_ID",cabinetId);
				
				//一个容器对应一种麻精药品
				TParm containerToxicParm = SPCOutStoreTool.getInstance().onQueryContainerDetail(inParm);
				
				int count = containerToxicParm.getCount() ;
				if(count < 0 ){
					this.messageBox("空容器");
					setValue("TOXIC_ID", "");
					return ;
				}
				
				String orderCode1 = result.getValue("ORDER_CODE",0);
				 
				int tableDRow = table_D.getSelectedRow();
				int dispenseQty  = table_D.getParmValue().getInt("QTY",tableDRow);
				int acumOutBoundQty  = table_D.getParmValue().getInt("ACTUAL_QTY",tableDRow);
 
				//要出的数量
				double outQty =  containerToxicParm.getCount() ;
				if(    acumOutBoundQty+outQty > dispenseQty ){
					this.messageBox("容器药品总数量大于实际要出的数量，不能出");
					return ;
				}
				
				//状态
				String  updateFlg = "1";
				if(dispenseQty - acumOutBoundQty == outQty){
					updateFlg = "3";
				}
				 
				
				String seqNo =  table_D.getParmValue().getValue("REQUEST_SEQ",tableDRow);
				
				inParmAll.setData("REQUEST_NO",requestNo);
				inParmAll.setData("SEQ_NO",seqNo);
				inParmAll.setData("DISPENSE_NO",requestNo);
				inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
				inParmAll.setData("CONTAINER_ID",containerId);
				
				inParmAll.setData("CONTAINER_DESC",containerToxicParm.getValue("CONTAINER_DESC",0));
				inParmAll.setData("OPT_USER",Operator.getID());
				inParmAll.setData("OPT_TERM",Operator.getIP());
				inParmAll.setData("CABINET_ID",getValueString("CABINET_ID"));
				inParmAll.setData("ORDER_CODE",orderCode1);
				inParmAll.setData("IS_BOXED","Y");
				inParmAll.setData("BOXED_USER",Operator.getID());
				
				Timestamp date = SystemTool.getInstance().getDate();
				inParmAll.setData("OPT_DATE",date);
 
				inParmAll.setData("ACTUAL_QTY",outQty);
				inParmAll.setData("UPDATE_FLG",updateFlg);
				
				TParm inParm2 = new TParm() ;
				int count2  = 0 ;
				Timestamp validate = null;
				String batchSeq = null ;
				String batchNo = null ;
				String unitCode = null;
				Double verifyinPrice = null; 
				Double inventPrice = null;
				String supOrderCode = "";
				for(int i = 0 ; i < containerToxicParm.getCount() ; i++ ){
					TParm rowParm = (TParm)containerToxicParm.getRow(i) ;
					inParm2.setData("ORDER_CODE",count2,orderCode1);
					inParm2.setData("BATCH_NO",count2 ,rowParm.getValue("BATCH_NO"));
					inParm2.setData("VERIFYIN_PRICE",count2,rowParm.getValue("VERIFYIN_PRICE"));
					inParm2.setData("VALID_DATE",count2,rowParm.getValue("VALID_DATE"));
					inParm2.setData("BATCH_SEQ",count2,rowParm.getValue("BATCH_SEQ"));
					inParm2.setData("DISPENSE_NO",count2,requestNo);
					inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
					inParm2.setData("CABINET_ID",count2,getValueString("CABINET_ID")); 
					inParm2.setData("TOXIC_ID",count2,rowParm.getValue("TOXIC_ID"));
					inParm2.setData("CONTAINER_ID",count2,getValueString("CONTAINER_ID"));
					inParm2.setData("CONTAINER_DESC",count2,rowParm.getValue("CONTAINER_DESC"));
					inParm2.setData("OPT_USER",count2,Operator.getID());
					inParm2.setData("OPT_TERM",count2,Operator.getIP());
					if(validate == null ){
						validate = rowParm.getTimestamp("VALID_DATE");
					}
					if(batchSeq == null ){
						batchSeq = rowParm.getValue("BATCH_SEQ");
					}
					if(batchNo == null ){
						batchNo = rowParm.getValue("BATCH_NO") ;
					}
					if(unitCode == null ){
						unitCode = rowParm.getValue("UNIT_CODE");
					}
					if(verifyinPrice == null ){
						verifyinPrice = rowParm.getDouble("VERIFYIN_PRICE");
					}
					if(inventPrice == null ){
						inventPrice = rowParm.getDouble("INVENT_PRICE");
					}
					if(supOrderCode == null || supOrderCode.equals("")){
						supOrderCode = rowParm.getValue("SUP_ORDER_CODE");
					}
					count2++ ;
				}
				inParmAll.setData("VALID_DATE",validate);
				inParmAll.setData("BATCH_SEQ",batchSeq);
				inParmAll.setData("BATCH_NO",batchNo);
				inParmAll.setData("UNIT_CODE",unitCode);
				inParmAll.setData("VERIFYIN_PRICE",verifyinPrice);
				inParmAll.setData("INVENT_PRICE",inventPrice);
				inParmAll.setData("SUP_ORDER_CODE",supOrderCode);
				
				if(inParm2 != null ){
					inParmAll.setData("OUT_TOXICID",inParm2.getData());  
				}
				
				inParmAll = getDispenseMParm(inParmAll);
				inParmAll = getDispenseDParm(inParmAll);
				//写数据到容器交易表主表与明细表,更新实际出库数量
				TParm result2  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
	                    "onInsertOutpatientOutBatch", inParmAll);
				if(result2.getErrCode() != -1  ){
					table_D.setItem(tableDRow, "ACTUAL_QTY",
							acumOutBoundQty + count);
				}

				inParm.setData("DISPENSE_NO",requestNo);
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("SEQ_NO",seqNo);
				//查询新增的显示出来
				TParm p = SPCToxicTool.getInstance().onQuery(inParm);
				table_M.setParmValue(p);
						 
				//setValue("TOXIC_QTY", result.getDouble("TOXIC_QTY",0));
				//setValue("CONTAINER_DESC", result.getValue("CONTAINER_DESC",0));
				setValue("CONTAINER_ID","");
			}
		}else {
			this.messageBox("没有该容器!");
			setValue("CONTAINER_ID", "");
			setValue("CONTAINER_DESC","");
			setValue("TOXIC_QTY", "");
			this.getTextField("CONTAINER_ID").grabFocus();
			return ;
		}
		this.getTextField("TOXIC_ID").grabFocus();
	}
	
	/**
	 * 麻精流水号单击事件
	 */
	public void onToxicIdClicked(String  toxicId){
		table_D.acceptText();
		table_M.acceptText() ;
		
		 
		containerId = getValueString("CONTAINER_ID");
		requestNo = getValueString("REQUEST_NO");
		
		//不显示出来的容器名称
		String containerDesc = getValueString("CONTAINER_DESC");
		String cabinetId = getValueString("CABINET_ID");
		
		 
		if(StringUtil.isNullString(containerId)){
			this.messageBox("容器ID不能为空,请先扫描");
			return ;
		}
		
		int tableDRow = table_D.getSelectedRow() ;
		int dispenseQty  = table_D.getParmValue().getInt("QTY",tableDRow);
		int acumOutBoundQty  = table_D.getParmValue().getInt("ACTUAL_QTY",tableDRow);
		
		if(dispenseQty == acumOutBoundQty ){
			this.messageBox("已出库完成");
			return  ;
		}
		String  updateFlg = "1";
		if(dispenseQty - acumOutBoundQty == 1){
			updateFlg = "3";
		}

		if(toxicId != null && !toxicId.equals("")){
			
			
			//根据麻精流水号与orcer_code查询对应的
			TParm inParm = new TParm();
			
			//先查询容器还能不能装
			 
			String orderCode  = table_D.getParmValue().getValue("ORDER_CODE",tableDRow);
			
			String seqNo =  table_D.getParmValue().getValue("REQUEST_SEQ",tableDRow);
			String requestNo = getValueString("REQUEST_NO");
			 
			inParm.setData("REQUEST_NO",requestNo);
			inParm.setData("SEQ_NO",seqNo);
			 
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("TOXIC_ID",toxicId);
			inParm.setData("CABINET_ID",cabinetId);
			
			TParm toxParm = new TParm();
			toxParm.setData("DISPENSE_NO",requestNo);
			toxParm.setData("DISPENSE_SEQ_NO",seqNo);
			toxParm.setData("CONTAINER_ID",containerId);
			TParm dParm = SPCToxicTool.getInstance().onQueryDByCount(toxParm);
			int ccout = dParm.getInt("NUM",0) ;
			 
			int toxicQty = getValueInt("TOXIC_QTY");
			if(ccout == toxicQty ){
				this.messageBox("容器已满!");
				setValue("CONTAINER_ID", "");
				this.getTextField("CONTAINER_ID").grabFocus();
				return  ;
			}
			
			TParm parm = SPCContainerTool.getInstance().onQuery(inParm);
			if(parm.getCount() <=  0){
				this.messageBox("查无该麻精药品,请重新拣选!");
				return  ;
			}
			Timestamp validDate = parm.getTimestamp("VALID_DATE",0);
		 
			//查询对应的效期是否是最近的
			TParm returnParm = SPCToxicTool.getInstance().onQueryByValidDate(inParm);
			
			Timestamp  minValidDate =  null;
			if(returnParm.getCount()> 0 ){
				minValidDate = returnParm.getTimestamp("VALID_DATE",0);
			}
			
			inParm.setData("BATCH_NO",parm.getValue("BATCH_NO",0));
			inParm.setData("VERIFYIN_PRICE",parm.getValue("VERIFYIN_PRICE",0));
			inParm.setData("VALID_DATE",parm.getTimestamp("VALID_DATE",0));
			inParm.setData("BATCH_SEQ",parm.getValue("BATCH_SEQ",0));
			inParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
			inParm.setData("ACTUAL_QTY",1);
			inParm.setData("UPDATE_FLG",updateFlg);
			inParm.setData("SUP_ORDER_CODE",parm.getValue("SUP_ORDER_CODE",0));
			inParm.setData("SUP_CODE",parm.getValue("SUP_CODE",0));
			inParm.setData("INVENT_PRICE",parm.getDouble("INVENT_PRICE",0));
			inParm.setData("RETAIL_PRICE",parm.getDouble("RETAIL_PRICE",0));
			//保存时重新设置空
			inParm.setData("CABINET_ID",""); 
			 
			Timestamp nowTimestamp = SystemTool.getInstance().getDate() ;
			if(nowTimestamp.getTime() >= validDate.getTime() ){
				this.messageBox("药品过期，不能出库，效期="+validDate);
				return ;
			}
			if(minValidDate != null &&  ( validDate.getTime() >  minValidDate.getTime())){
				//if (this.messageBox("确定", "取得不是最早效期!"+validDate+":"+minValidDate, 2) == 0) {
					TParm result = batchSave(containerDesc, inParm);
					if(result.getErrCode() < 0 ){
						this.messageBox(result.getErrText());
						return ;
					}else {						
						 
						table_D.setItem(tableDRow, "ACTUAL_QTY", acumOutBoundQty+1);
					}
				//}
			}else {
				TParm result = batchSave(containerDesc, inParm);
				if(result.getErrCode() < 0 ){
					this.messageBox(result.getErrText());
					return ;
				}else {
					 
					table_D.setItem(tableDRow, "ACTUAL_QTY", acumOutBoundQty+1);
				}
			}
			
			//查询新增的显示出来
			TParm p = SPCToxicTool.getInstance().onQuery(inParm);
			
			table_M.setParmValue(p);
			setValue("TOXIC_ID", "");
			 
		}
		
	}
	
	
	/**
	 * 出库Table单击事件，没有完全出库的，显示出库的加一行空白行
	 */
	public void onTableDClicked(){
		table_D.acceptText();
		table_M.acceptText() ;
		int row = table_D.getSelectedRow() ;
		TParm parm = table_D.getParmValue() ;
		int dispenseQty  = parm.getInt("QTY",row);
		int acumOutBoundQty  = parm.getInt("ACTUAL_QTY",row);

		TParm inParm = new TParm();
		inParm.setData("DISPENSE_NO",getValueString("REQUEST_NO"));
		inParm.setData("SEQ_NO",parm.getValue("REQUEST_SEQ",row));
		inParm.setData("ORDER_CODE",parm.getValue("ORDER_CODE",row));
		
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		table_M.setParmValue(p);
		
		// 出库状态
        if (getRadioButton("UPDATE_FLG_N").isSelected()) {
            // 未出库
        	if(acumOutBoundQty < dispenseQty ){
   			 
    			getTextField("CONTAINER_ID").setEditable(true);
    			getTextField("TOXIC_ID").setEditable(true);
    			this.getTextField("CONTAINER_ID").grabFocus();
    		}else {
    			//不新增一个空白行
    			this.messageBox("全部出库完成");
    			getTextField("CONTAINER_ID").setEditable(false);
    			getTextField("TOXIC_ID").setEditable(false);
    			
    		}	
            
        } else if (getRadioButton("UPDATE_FLG_Y").isSelected()) {
            //已出库
        	getTextField("CONTAINER_ID").setEditable(false);
			getTextField("TOXIC_ID").setEditable(false);
        }
		
		
		setValue("CONTAINER_ID", "");
		setValue("TOXIC_QTY", "");
		setValue("CONTAINER_DESC", "");
		
		table_D.setSelectedRow(row);
	}
	
	/**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm ) {
        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        
        getDispenseNo();
        int rowList = table_List.getSelectedRow() ;
        
        parmM.setData("DISPENSE_NO", dispenseNo);
        parmM.setData("REQTYPE_CODE", "TEC");
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        Timestamp requestDate = table_List.getItemTimestamp(rowList, "REQUEST_DATE");
        parmM.setData("REQUEST_DATE", requestDate);
        String appOrgCode = (String)table_List.getItemData(rowList, "APP_ORG_CODE");
        parmM.setData("APP_ORG_CODE", appOrgCode);
        
        String toOrgCode = (String)table_List.getItemData(rowList, "TO_ORG_CODE");
        parmM.setData("TO_ORG_CODE", toOrgCode);
        parmM.setData("URGENT_FLG", "N");
        parmM.setData("DESCRIPTION", "");
        parmM.setData("DISPENSE_DATE", date);
        parmM.setData("DISPENSE_USER", Operator.getID());
        
        parmM.setData("WAREHOUSING_DATE", tnull);
        parmM.setData("WAREHOUSING_USER", "");
         
        parmM.setData("REASON_CHN_DESC", "");
        parmM.setData("UNIT_TYPE", "1");
        
        parmM.setData("UPDATE_FLG", "1");
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parmM.setData("REGION_CODE", Operator.getRegion());

        //药品种类--普药:1,麻精：2
        parmM.setData("DRUG_CATEGORY","2");
 
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO      
        parmM.setData("APPLY_TYPE","1");
        
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
        String batch_no = "";
                
        parmD.setData("DISPENSE_NO",   dispenseNo);
        parmD.setData("SEQ_NO",   parm.getValue("SEQ_NO"));
        
        parmD.setData("REQUEST_SEQ",   parm.getValue("SEQ_NO"));
        
        parmD.setData("ORDER_CODE",   parm.getValue("ORDER_CODE"));
        
        int tabDselect = table_D.getSelectedRow();
        double qty = table_D.getItemDouble(tabDselect, "QTY");
        parmD.setData("QTY",   qty);
        parmD.setData("UNIT_CODE",   parm.getValue("UNIT_CODE"));
        parmD.setData("RETAIL_PRICE",   parm.getDouble("RETAIL_PRICE"));
        parmD.setData("VERIFYIN_PRICE", parm.getValue("VERIFYIN_PRICE"));
        parmD.setData("STOCK_PRICE",   parm.getValue("VERIFYIN_PRICE"));
        parmD.setData("ACTUAL_QTY",   parm.getDouble("ACTUAL_QTY"));
        parmD.setData("PHA_TYPE",   "W");
       
        parmD.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ"));
        
        parmD.setData("INVENT_PRICE", parm.getValue("INVENT_PRICE"));
        parmD.setData("SUP_CODE", parm.getValue("SUP_CODE"));
        batch_no = parm.getValue( "BATCH_NO");
        parmD.setData("BATCH_NO",   batch_no);
        Timestamp valid_date = parm.getTimestamp("VALID_DATE");
         
        parmD.setData("VALID_DATE",   valid_date);
         
        parmD.setData("DOSAGE_QTY",    parm.getDouble("ACTUAL_QTY"));
        parmD.setData("OPT_USER",   Operator.getID());
        parmD.setData("OPT_DATE",   date);
        parmD.setData("OPT_TERM",   Operator.getIP());
        
        //是否下架
        parmD.setData("IS_BOXED", "Y");
        parmD.setData("BOXED_USER", Operator.getID());
        parmD.setData("BOX_ESL_ID", "");  
        parmD.setData("SUP_ORDER_CODE",parm.getValue("SUP_ORDER_CODE"));
        
         
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }
    
   

	private void getDispenseNo() {
		TParm searchParm = new TParm();
        searchParm.setData("REQUEST_NO",getValue("REQUEST_NO"));
        
        String dNo = "";
        // 出库单号
        TParm result = SPCOutpatientOutStoreTool.getInstance().onQueryDispenseNo(searchParm);
        if(result.getCount() >  0 ){
        	dNo = result.getRow(0).getValue("DISPENSE_NO");
        }
        
        //根据requestNo去查，有没有出库单号，有用现成 ，没有新加一个
        if (dNo == null || "".equals(dNo) ) {
            dispenseNo = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }else {
        	dispenseNo = dNo ;
		}
	}
	
	public void onCLear(){
		String controlName = "REQUEST_NO;DISPENSE_NO;CONTAINER_ID;TOXIC_ID;TOXIC_QTY;CONTAINER_DESC;APP_ORG_CODE;TO_ORG_CODE";
		this.clearValue(controlName);
		this.getTextField("CONTAINER_ID").setEditable(true);
		this.getTextField("TOXIC_ID").setEditable(true);
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
		table_List = getTable("TABLE_LIST");
		table_M.acceptText() ;
		table_D.acceptText() ;
		table_D.setParmValue(new TParm());
		table_M.setParmValue(new TParm());
		table_List.setParmValue(new TParm());
	}
	

	/**
	 * 智能柜库存查询
	 */
	public void openSearch(){
	    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);

    }
	
	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow() ;
		TParm tableDParm = table_D.getParmValue() ;
		requestNo = getValueString("REQUEST_NO");
		String seqNo = tableDParm.getValue("REQUEST_SEQ",tableDRow);
		inParm.setData("DISPENSE_NO",requestNo);
		inParm.setData("DISPENSE_SEQ_NO",seqNo);
		inParm.setData("SEQ_NO",seqNo);
		inParm.setData("IS_BOXED","Y");
		inParm.setData("BOXED_USER",Operator.getID());
		inParm.setData("CONTAINER_ID",containerId);
		inParm.setData("CONTAINER_DESC",containerDesc);
		inParm.setData("OPT_USER",Operator.getID());
		inParm.setData("OPT_TERM",Operator.getIP());
		Timestamp date = SystemTool.getInstance().getDate();
		inParm.setData("OPT_DATE",date);
		inParm.setData("ORG_CODE",orgCode);
		
		inParm = getDispenseMParm(inParm);
		inParm = getDispenseDParm(inParm);
		
		//写数据到容器交易表主表与明细表,更新实际出库数量
		TParm result  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
		                                    "onInsertOutpatientOut", inParm);
		return result ;
	}
	
	
	/**
     * 打印出库单
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("不存在出库单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
           /* date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "出库单");*/
            date.setData("TITLE", "TEXT","药品出库单");
            //===============pangben modify 20110607 添加急件注记
            if (null !=
                getValue("URGENT_FLG") && getValue("URGENT_FLG").equals("Y"))
                date.setData("URGENT", "TEXT", "急");
            else
                date.setData("URGENT", "TEXT", "");
            //===============pangben modify 20110607 stop
            date.setData("DISP_NO", "TEXT",
                         "出库单号: " + this.getValueString("DISPENSE_NO"));
            date.setData("REQ_NO", "TEXT",
                         "申请单号: " + this.getValueString("REQUEST_NO"));
            
            int listSelectRow = table_List.getSelectedRow() ;
            String dispenseDate = table_List.getParmValue().getValue("DISPENSE_DATE", listSelectRow);
             
            date.setData("OUT_DATE", "TEXT",
                         "出库日期: " + dispenseDate.
                         substring(0, 10).replace('-', '/'));
            date.setData("REQ_TYPE", "TEXT", "申请类别: " +
            		 this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("ORG_CODE_APP", "TEXT", "出库部门: " +
            		 this.getComboBox("TO_ORG_CODE").getSelectedName());
            date.setData("ORG_CODE_FROM", "TEXT", "接受部门: " +
            		 this.getComboBox("APP_ORG_CODE").getSelectedName());
            date.setData("DATE", "TEXT",			   
                         "制表日期: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // 表格数据			
            TParm parm = new TParm();
 
            double qty = 0;
            double sum_retail_price = 0;
          
            double sum_verifyin_price = 0;
			String orgCode =  getValueString("APP_ORG_CODE");
			String sql = "";
		 
		    sql =
                "SELECT A.ORDER_CODE, " +
                "   CASE WHEN B.GOODS_DESC IS NULL "
                + "  THEN B.ORDER_DESC ELSE B.ORDER_DESC   END AS ORDER_DESC, "
                + "  B.SPECIFICATION, C.UNIT_CHN_DESC,  " 
                + " CASE  WHEN A.UNIT_CODE = D.STOCK_UNIT "
			    + "       THEN A.RETAIL_PRICE * D.DOSAGE_QTY * D.STOCK_QTY "
			    + "       WHEN A.UNIT_CODE = D.DOSAGE_UNIT "
			    + "       THEN A.RETAIL_PRICE "
			    + "       ELSE A.RETAIL_PRICE "
			    + "       END  RETAIL_PRICE, "
                + "  A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.ACTUAL_QTY , "
                + "  CASE WHEN A.UNIT_CODE=D.STOCK_UNIT " 
                + "       THEN A.INVENT_PRICE "
                + "       WHEN A.UNIT_CODE=D.DOSAGE_UNIT THEN A.VERIFYIN_PRICE ELSE A.VERIFYIN_PRICE  END  VERIFYIN_PRICE "   //ADD VERIFYIN_PRICE
                + " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C ,PHA_TRANSUNIT D "
                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                + " AND A.UNIT_CODE = C.UNIT_CODE "
                + " AND A.ORDER_CODE=D.ORDER_CODE "
                + " AND A.DISPENSE_NO = '" +
                this.getValueString("DISPENSE_NO") + "' "
                + " ORDER BY A.SEQ_NO";
			 
				
              //luhai modify 出库单删除商品名 begin 
                TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
                for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
                    parm.addData("ORDER_DESC",
                                 printData.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 printData.getValue("SPECIFICATION", i));
                    parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
                    parm.addData("UNIT_PRICE",
                                 df4.format(printData.getDouble("RETAIL_PRICE",
                        i)));
                    parm.addData("VERIFYIN_PRICE",
                    		df4.format(printData.getDouble("VERIFYIN_PRICE",
                    				i)));
                    qty = printData.getDouble("ACTUAL_QTY", i);
                    if("040108".equals(orgCode)||"040109".equals(orgCode)) {
                    	 parm.addData("QTY", printData.getDouble("MIN_QTY", i));
                    	 qty = printData.getDouble("MIN_QTY", i) ;
                    }else {
                    	 parm.addData("QTY", qty);				
                    }
                    parm.addData("AMT", StringTool.round(printData.getDouble(
                        "RETAIL_PRICE", i) * qty, 2));
                    parm.addData("AMT_VERIFYIN", StringTool.round(printData.getDouble(
                    		"VERIFYIN_PRICE", i) * qty, 2));
                    sum_retail_price += printData.getDouble("RETAIL_PRICE", i) *
                        qty;
                    sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i) *
                    qty;
                    parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
                    parm.addData("VALID_DATE", StringTool.getString(printData.
                        getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
                }

            
            if (parm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("没有打印数据");
                return;
            }
            //luhai 2012-1-22 modify 加入采购金额，采购单价 begin
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");			
         //   parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            															
            date.setData("TABLE", parm.getData());

            // 表尾数据
            //luhai 2012-1-22 加入合计atm
            String atm = StringTool.round(sum_retail_price, 2)+"";
            String verifyinAtm=StringTool.round(sum_verifyin_price, 2)+"";
//            date.setData("TOT_CHN", "TEXT",
//                         "合计(大写): " + StringUtil.getInstance().numberToWord(atm));
            date.setData("BAR_CODE", "TEXT",this.getValueString("DISPENSE_NO"));
            date.setData("ATM", "TEXT",atm);
            date.setData("VERIFYIN_ATM", "TEXT",verifyinAtm);
            date.setData("VERIFYIN_ATM_DESC", "TEXT",StringUtil.getInstance().numberToWord(Double.parseDouble(verifyinAtm)));
            date.setData("TOT", "TEXT", "合计: ");
            date.setData("USER", "TEXT", Operator.getName());
            date.setData("BAR_CODE", "TEXT", this.getValueString("REQUEST_NO"));
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DispenseOut.jhw", date,true);
        }
        else {
            this.messageBox("没有打印数据");
            return;
        }
    }
	
	 
	
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
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
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
    "##########0.0000");
	


}
