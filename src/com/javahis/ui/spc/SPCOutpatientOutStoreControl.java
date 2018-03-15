package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCDispenseOutTool;
import jdo.spc.SPCOutpatientOutStoreTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * 
 * <p>
 * Title:临时医嘱/首日量-门急诊麻精出智能柜
 * </p>
 * 
 * <p>
 * Description:TODO
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class SPCOutpatientOutStoreControl extends TControl{

	/**
	 * 所有未出库已出库
	 */
	TTable table_List;
	/**
	 * 出库信息
	 */
	TTable table_D ;
	
	/**
	 * 药品详细
	 */
	TTable table_M;
	
	String ip ;
	
	String requestNo ;
	
	String containerId ;
	
	/**
	 * 智能柜本机物理IP
	 */
	String guardIp;
	
	/**
	 * 智能柜部门
	 */
	String orgCode ;
	
	String appOrgCode = "0202";
	
	String dispenseNo = "";
	
	/**
	 * 初始化
	 */
	public void onInit() {
		table_List = getTable("TABLE_LIST");
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
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
		
		Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        parm.setData("ORG_CODE",appOrgCode);
        // 未出库
        parm.setData("STATUS", "N");
        result = SPCOutpatientOutStoreTool.getInstance().onQuery(parm);
		
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
            setValue("REQUEST_NO", requestNo);
            setValue("DISPENSE_NO", dispenseNo);
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
			this.messageBox("容器为空!");
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
		containerId = getValueString("TOXIC_ID");
		TParm sysParm = SPCContainerTool.getInstance().onQuerySysParm();
		int length = sysParm.getInt("TOXIC_LENGTH");
		
		//如果containerId的长度与麻精长度一致,处理麻精，不是，容器
		if(containerId.length() == length){
			onToxicIdClicked(containerId);
		}else{
			onContainerIdClicked(containerId);
		}
	}

	
	/**
	 * 麻精流水号单击事件
	 */
	public void onToxicIdClicked(String toxicId ){
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
			 
			String caseNo = table_D.getParmValue().getValue("CASE_NO",tableDRow);
			inParm.setData("REQUEST_NO",requestNo);
		 
			inParm.setData("CASE_NO",caseNo);
			inParm.setData("SEQ_NO",seqNo);
			 
			inParm.setData("ORDER_CODE",orderCode);
			inParm.setData("TOXIC_ID",toxicId);
			inParm.setData("CABINET_ID",cabinetId);
			
			getDispenseNo() ;
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
			Timestamp  minValidDate = returnParm.getTimestamp("VALID_DATE",0);
			
			inParm.setData("BATCH_NO",parm.getValue("BATCH_NO",0));
			inParm.setData("VERIFYIN_PRICE",parm.getValue("VERIFYIN_PRICE",0));
			inParm.setData("VALID_DATE",parm.getTimestamp("VALID_DATE",0));
			inParm.setData("BATCH_SEQ",parm.getValue("BATCH_SEQ",0));
			inParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
			inParm.setData("ACTUAL_QTY",1);
			inParm.setData("UPDATE_FLG",updateFlg);
			
			//保存时重新设置空
			inParm.setData("CABINET_ID",""); 
			if(validDate.getTime() >  minValidDate.getTime()){
				if (this.messageBox("确定", "取得不是最早效期!", 2) == 0) {
					TParm result = batchSave(containerDesc, inParm);
					if(result.getErrCode() < 0 ){
						this.messageBox("出智能柜失败");
						return ;
					}else {						
						 
						table_D.setItem(tableDRow, "ACTUAL_QTY", acumOutBoundQty+1);
					}
				}
			}else {
				TParm result = batchSave(containerDesc, inParm);
				if(result.getErrCode() < 0 ){
					this.messageBox("出智能柜失败");
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
	 * 麻精页签 容器回车事件
	 * 
	 */
	public void onContainerIdClicked(String containerId) {

		    String userId = ""; 
			table_D = getTable("TABLE_D");
			table_D.acceptText();
			table_M = getTable("TABLE_M");
			 
			TParm inParm = new TParm();
			inParm.setData("CONTAINER_ID", containerId);
			TParm result = SPCContainerTool.getInstance().onQueryBy(inParm);
			if (result.getCount() > 0) {
				String orderCode = result.getValue("ORDER_CODE", 0);
				int tableDselectRow = table_D.getSelectedRow();
				TParm parm = table_D.getParmValue();

				String orderCodeD = parm
						.getValue("ORDER_CODE", tableDselectRow);
				if (!orderCodeD.equals(orderCode)) {
					this.messageBox("该容器的麻精品种与当前要出库的品种不同,请重新操作!");
					setValue("TOXIC_ID", "");
					this.getTextField("CONTAINER_ID").grabFocus();
					return;
				} else {
					
					//备:按照朱总所说刷容器整合出
					String cabinetId  = getValueString("CABINET_ID");
					inParm.setData("CABINET_ID",cabinetId);
					
					//查询出智能柜容器对应的所有麻精的数
					TParm containerToxicParm = SPCContainerTool.getInstance().onQueryByContainerId(inParm);
					int count = containerToxicParm.getCount() ;
					if(count < 0 ){
						this.messageBox("空容器");
						setValue("TOXIC_ID", "");
						return ;
					}
					
					
					int tableDRow = table_D.getSelectedRow();
					int acumOutBoundQty = table_D.getParmValue().getInt(
							"ACTUAL_QTY", tableDRow);
					int dispenseQty = table_D.getParmValue().getInt(
							"QTY", tableDRow);
					
					//count 出智能柜容器里面有多少支麻精
					if(    acumOutBoundQty+count > dispenseQty ){
						this.messageBox("容器药品总数量大于实际要出的数量，不能出");
						setValue("TOXIC_ID", "");
						return ;
					}
					
					requestNo = getValueString("REQUEST_NO"); 
					
					TParm inParmAll = new TParm();
					String seqNo = table_D.getParmValue().getValue("ORDER_SEQ",tableDRow);
					
					inParmAll.setData("DISPENSE_NO",requestNo);
					inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
					inParmAll.setData("CONTAINER_ID",containerId);
					
					inParmAll.setData("CONTAINER_DESC",result.getValue("CONTAINER_DESC",0));
					inParmAll.setData("IS_BOXED","Y");
					inParmAll.setData("BOXED_USER",Operator.getID());
					inParmAll.setData("BOX_ESL_ID","");
					
					if(userId == null || userId.equals("")){
						userId = Operator.getID() ;
					}
					inParmAll.setData("OPT_USER",userId);
					inParmAll.setData("OPT_TERM",Operator.getIP());
					inParmAll.setData("CABINET_ID",getValueString("CABINET_ID"));
					inParmAll.setData("ORDER_CODE",orderCode);
					
					
					/**
					 * ODI_DSPNM 四个主键
					 */
					String startDttm = table_D.getParmValue().getValue("START_DTTM",
							tableDRow);
					String orderNo = table_D.getParmValue().getValue("ORDER_NO",
							tableDRow);
					String caseNo = table_D.getParmValue().getValue("CASE_NO",
							tableDRow);
					inParmAll.setData("START_DTTM", startDttm);
					inParmAll.setData("ORDER_NO", orderNo);
					inParmAll.setData("CASE_NO", caseNo);
					inParmAll.setData("ORDER_SEQ", seqNo);
					
					//取药者
					inParmAll.setData("TAKEMED_USER",userId);
					
					int count2  = 0 ;
					TParm inParm2 = new TParm() ;
					for(int i = 0 ; i < count ; i++ ){
						TParm rowParm = (TParm)containerToxicParm.getRow(i) ;
						inParm2.setData("ORDER_CODE",count2,rowParm.getValue("ORDER_CODE"));
						inParm2.setData("BATCH_NO",count2 ,rowParm.getValue("BATCH_NO"));
						inParm2.setData("VERIFYIN_PRICE",count2,rowParm.getValue("VERIFYIN_PRICE"));
						inParm2.setData("VALID_DATE",count2,rowParm.getValue("VALID_DATE"));
						inParm2.setData("BATCH_SEQ",count2,rowParm.getValue("BATCH_SEQ"));
						inParm2.setData("UNIT_CODE",count2,rowParm.getValue("UNIT_CODE"));
						inParm2.setData("DISPENSE_NO",count2,dispenseNo);
						inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
						inParm2.setData("CABINET_ID",count2,getValueString("CABINET_ID")); 
						inParm2.setData("IS_BOXED",count2,"Y");
						inParm2.setData("TOXIC_ID",count2,rowParm.getValue("TOXIC_ID"));
						inParm2.setData("BOXED_USER",count2,Operator.getID());
						inParm2.setData("BOX_ESL_ID",count2,"");
						inParm2.setData("CONTAINER_ID",count2,rowParm.getValue("CONTAINER_ID"));
						inParm2.setData("CONTAINER_DESC",count2,rowParm.getValue("CONTAINER_DESC"));
						inParm2.setData("OPT_USER",count2,userId);
						inParm2.setData("OPT_TERM",count2,Operator.getIP());
						count2++ ;
					}
					inParm2.setCount(count2);
					if(inParm2 != null ){
						inParmAll.setData("OUT_D",inParm2.getData());  
					}
										
					//写数据到容器交易表主表与明细表,更新实际出库数量
					TParm result2  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
					                                    "onInsertMlStationBatch", inParmAll);
				
					if(result2.getErrCode() < 0 ){
						this.messageBox("麻精出智能柜失败!");
						return ;
					}else{
					
						int acturalQty = acumOutBoundQty + count;
						table_D.setItem(tableDRow, "ACUM_OUTBOUND_QTY",acturalQty);
					}
					
					// 查询新增的显示出来
					TParm searchParm = new TParm();
					searchParm.setData("DISPENSE_NO",dispenseNo);
					searchParm.setData("SEQ_NO",seqNo);
					searchParm.setData("ORDER_CODE",orderCode);
					 
					TParm p = SPCToxicTool.getInstance().onQuery(searchParm);
					table_M.setParmValue(p);
					double toxicQty = result.getDouble("TOXIC_QTY", 0);
					setValue("TOXIC_QTY", toxicQty);
					setValue("CONTAINER_DESC", result.getValue(
							"CONTAINER_DESC", 0)+"容器");
					 
				
				}
			} else {
				this.messageBox("没有该容器!");
				setValue("CONTAINER_ID", "");
				setValue("CONTAINER_DESC", "");
				setValue("TOXIC_QTY", "");
				this.getTextField("CONTAINER_ID").grabFocus();
				return;
			}
			setValue("CONTAINER_ID", "");
			this.getTextField("CONTAINER_ID").grabFocus();
		 
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
        
        //getDispenseNo();
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
        parmD.setData("RETAIL_PRICE",    0);
        parmD.setData("VERIFYIN_PRICE", parm.getValue("VERIFYIN_PRICE"));
        parmD.setData("STOCK_PRICE",   parm.getValue("VERIFYIN_PRICE"));
        parmD.setData("ACTUAL_QTY",   1);
        parmD.setData("PHA_TYPE",   "W");
       
        parmD.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ"));
        
        parmD.setData("INVENT_PRICE", 0);
        parmD.setData("SUP_CODE", "18");
        batch_no = parm.getValue( "BATCH_NO");
        parmD.setData("BATCH_NO",   batch_no);
        Timestamp valid_date = parm.getTimestamp("VALID_DATE");
         
        parmD.setData("VALID_DATE",   valid_date);
         
        parmD.setData("DOSAGE_QTY",   1);
        parmD.setData("OPT_USER",   Operator.getID());
        parmD.setData("OPT_DATE",   date);
        parmD.setData("OPT_TERM",   Operator.getIP());
        
        //是否下架
        parmD.setData("IS_BOXED", "Y");
        parmD.setData("BOXED_USER", Operator.getID());
        parmD.setData("BOX_ESL_ID", "");
        
        //电子标签应用
        
        //parmD.setData("ORDER_DESC", tableDParm.getValue("ORDER_DESC",i));
        
         
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
		String controlName = "REQUEST_NO;DISPENSE_NO;CONTAINER_ID;TOXIC_ID;TOXIC_QTY;CONTAINER_DESC";
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
	


}

