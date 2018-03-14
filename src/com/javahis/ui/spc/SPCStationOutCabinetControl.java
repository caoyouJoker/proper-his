package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCStationOutCabinetTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.device.PullDriver;

/**
 * 
 * <p>
 * Title:临时医嘱/首日量-病区普药及麻精出智能柜
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
 * Company: BlueCore
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class SPCStationOutCabinetControl extends TControl {

	/**
	 *D 普药 M 麻精
	 */
	TPanel panel_D;
	TPanel panel_M;

	TTable table_D;
	TTable table_M;
	
	//未入库，已入库列表
	TTable table_List; 

	// 普药Table
	TTable table;

	/**
	 * 取药单号
	 */
	String takemedNo;

	TParm result;

	String containerId;
	String toxicId;

	String org_code;
	String cabinetId;
	
	/**
	 * 智能柜本机物理IP
	 */
	String guardIp;
	
	/**
	 * 麻精柜门禁ID
	 */
	String guardId;
	
	//操作用户ID
	String userId ;
	
	private String ip = ""; // 智能柜IP
	
	private int rtn1;
	
	Set<String> guardSet = new HashSet<String>();

	
	/**
	 * 初始化
	 */
	public void onInit() {
		panel_D = (TPanel) getComponent("PANEL_D");
		panel_M = (TPanel) getComponent("PANEL_M");
		table_D = getTable("TABLE_D");
		table_M = getTable("TABLE_M");
		table = getTable("TABLE");
		
		table_List = getTable("TABLE_LIST");
		
		onInitData();
		
		addListener(getTable("TABLE_LIST"));
		
		// 初始化智能柜门禁
		/**
		int i = PullDriver.init();
		String params = "protocol=TCP,ipaddress=" + guardIp
				+ ",port=4370,timeout=2000,passwd=";
		rtn1 = PullDriver.Connect(params);
		if (rtn1 <= 0) {
			this.messageBox("智能柜初始化失败");
			// getTextField("DISPENSE_NO").setEnabled(false);
			// getTextField("BOX_ESL_ID").setEnabled(false);
		}*/
	
	}
	
	/**---------------------------------------------------------------*/

	/**
	 * 初始化数据
	 */
	public void onInitData() {

		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		TParm parm = new TParm();
		parm.setData("IP", ip);
		TParm result = SPCCabinetTool.getInstance().onQuery(parm);
		org_code = result.getValue("ORG_CODE", 0);
		cabinetId = result.getValue("CABINET_ID", 0);

		/**
		parm.setData("CABINET_ID",cabinetId);
		TParm cabinetParm = SPCInStoreTool.getInstance().queryCabinetGuard(parm);
		guardId = (String) cabinetParm.getData("GUARD_ID",0);
		 */
		
		guardIp = result.getValue("GUARD_IP",0);
		
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		
		  // 初始化验收时间
        // 出库日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
		//setValue("OPT_USER", Operator.getName());
		
		//Timestamp startDate = SystemTool.getInstance().getDate();
		//String startDateStr = sdf.format(startDate) ;
		//查询这个智能柜对应病区所有未出库的普药，麻精
		parm.setData("ORG_CODE",org_code);
		parm.setData("STATUS","N");
		//parm.setData("START_DATE",startDateStr+"0000");
		//parm.setData("END_DATE",startDateStr+"2359");
		TParm odiParm = SPCStationOutCabinetTool.getInstance().onQueryIndCabDspn(parm);
		if(odiParm.getCount() > 0 ){
			table_List.setParmValue(odiParm);
		}else{
			
			this.messageBox("没有查找到数据");
			table_List.setParmValue(new TParm());
		}
	}
	
	/**
	 * 查询所有未出库或者已出库单据列表
	 */
	public void onQueryList(){
		takemedNo = getValueString("TAKEMED_NO");
		
		String status = "";
		//UPDATE_Y 已出库，UPDATE_N 未出库
        if(getRadioButton("UPDATE_Y").isSelected()){
        	status = "Y";
        }else if(getRadioButton("UPDATE_N").isSelected()){
        	status = "N";
        }
        
        //起始时间
        String startDate = getValueString("START_DATE");
        startDate = startDate.substring(0,16).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        
        //结束时间
        String endDate = getValueString("END_DATE");
        endDate = endDate.substring(0,16).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        
        TParm parm = new TParm() ;
        parm.setData("ORG_CODE",org_code);
        parm.setData("STATUS",status);
        parm.setData("START_DATE",startDate);
        parm.setData("END_DATE",endDate);
        parm.setData("TAKEMED_NO",takemedNo);
        
        TParm odiParm = SPCStationOutCabinetTool.getInstance().onQueryIndCabDspn(parm);
		if(odiParm.getCount() > 0 ){
			table_List.setParmValue(odiParm);
		}else{
			this.messageBox("没有查找到数据");
			table_List.setParmValue(new TParm());
			table.setParmValue(new TParm());
			table_D.setParmValue(new TParm());
			table_M.setParmValue(new TParm());
		}
		
		
        
		
	}
	
	//未出库列表表格单击事件
	public void onTableListClick(){
		
		int row = table_List.getSelectedRow() ;
		if (row < 0) {
			this.messageBox_("没有选中出库信息");
			return;
		}
		 
		String takemedNo = table_List.getItemString(row, "TAKEMED_NO");
		if (takemedNo == null || takemedNo.equals("")) {
			this.messageBox_("取得出库信息失败");
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("TAKEMED_NO", takemedNo);
		TParm result = new TParm();
		if (panel_D.isShowing()) {
			result = SPCStationOutCabinetTool.getInstance().onQueryD(parm);
			if (result.getCount() < 0) {
				this.messageBox("查无数据");
				table.setParmValue(new TParm());
				return ;
			}
			
			//是否出库完成
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//需要出库数是否大于已出库的数量 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			this.setValue("TAKEMED_NO", takemedNo);
			table.setParmValue(result);
			//table.setSelectedRow(0);
			 
			/**
			if(isOpen){
				procesClose() ;
			}*/
			/**
			if(isOut){
				//未出库刷工卡
				
				if(getRadioButton("UPDATE_N").isSelected()){
					// 密码判断
					if (!checkPW()) {
						return ;
					}
				}
				//doJob() ;
			}*/
			
		} else if (panel_M.isShowing()) {
			
			result = SPCStationOutCabinetTool.getInstance().onQueryM(parm);
			if (result.getCount() < 0) {
				this.messageBox("查无数据");
				table_D.setParmValue(new TParm());
				return ;
			}
			 
			//是否出库完成
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//需要出库数是否大于已出库的数量 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			
			if(isOut){
				//未出库刷工卡
				
				if(getRadioButton("UPDATE_N").isSelected()){
					// 密码判断
					if (!checkPW()) {
						return ;
					}
				}
				//doJob() ;
			}
			
			this.setValue("TAKEMED_NO", takemedNo);
			table_D.setParmValue(result);
			//table_D.setSelectedRow(0);
			 
			
			/**
			if(isOpen){
				procesClose() ;
			}
			if(isOut){
				doJob() ;
			}*/
			
		}

		
		setValue("BED_NO", result.getValue("BED_NO_DESC", 0));
		setValue("MR_NO", result.getValue("MR_NO", 0));
		setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		 
		
	}

	public boolean onQuery() {
		takemedNo = getValueString("TAKEMED_NO");
		if(takemedNo == null || takemedNo.equals("")){
			return  false;
		}
		TParm parm = new TParm();
		parm.setData("TAKEMED_NO", takemedNo);
		
		TParm result = new TParm();
		if (panel_D.isShowing()) {
			result = SPCStationOutCabinetTool.getInstance().onQueryD(parm);
			if (result.getCount() < 0) {
				this.messageBox("查无数据");
				table.setParmValue(new TParm());
				setValue("TAKEMED_NO", "");
				setValue("BED_NO", "");
				setValue("MR_NO", "");
				setValue("PAT_NAME", "");
				return false;
			}
			
			//是否出库完成
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//需要出库数是否大于已出库的数量 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			/**
			if(isOut){
				onOpen(1);
			}*/
			
			table.setParmValue(result);
			//table.setSelectedRow(0);
		} else if (panel_M.isShowing()) {
			
			result = SPCStationOutCabinetTool.getInstance().onQueryM(parm);
			if (result.getCount() < 0) {
				this.messageBox("查无数据");
				setValue("TAKEMED_NO", "");
				setValue("BED_NO", "");
				setValue("MR_NO", "");
				setValue("PAT_NAME", "");
				table_D.setParmValue(new TParm());
				return false;
			}
			

			// 密码判断
			if (!checkPW()) {
				return false;
			}
			
			//是否出库完成
			boolean isOut = false;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i);
				double dosageQty = rowParm.getDouble("DOSAGE_QTY");
				double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
				
				//需要出库数是否大于已出库的数量 
				if(dosageQty > acumOutBoundQty ){
					isOut = true;
					break;
				}
			}
			/**
			if(isOut){
				onOpen(1);
			}*/
			
			table_D.setParmValue(result);
			//table_D.setSelectedRow(0);
		}

		
		setValue("BED_NO", result.getValue("BED_NO_DESC", 0));
		setValue("MR_NO", result.getValue("MR_NO", 0));
		setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		return true;

	}

	public boolean onTaskmedNoClicked() {
		
		/**
		if(panel_M.isShowing()){
			// 密码判断
			if (!checkPW()) {
				return false;
			}
		}*/
		onQuery();
		return true;
	}
	
	/**
	 * 调用密码验证
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "station";
		TParm parm = (TParm) this.openDialog(
				"%ROOT%\\config\\spc\\passWordCheck.x", inwCheck);
		if (parm == null) {
			return false;
		}
		String value = parm.getValue("OK");
		userId = parm.getValue("USER_ID");
		return value.equals("OK");
	}
	
	
	/**
	 * 条码事件
	 */
	public void onCode(){
		//容器与麻精流水号同一个控件
		containerId = getValueString("CONTAINER_ID");
		TParm sysParm = SPCContainerTool.getInstance().onQuerySysParm();
		int length = sysParm.getInt("TOXIC_LENGTH");
		
		//如果containerId的长度与麻精长度一致,处理麻精，不是，容器
		if(containerId.length() == length){
			onToxicIdClicked(containerId);
		}else{
			// modify by wangbin 2015/1/26 内部需求 #792 泰心物联网系统调整方案 : 病区管理 > 临时医嘱出智能柜 相关调整 START
			this.messageBox("查无该麻精条码号，出库失败");
//			onContainerIdClicked(containerId);
			// modify by wangbin 2015/1/26 内部需求 #792 泰心物联网系统调整方案 : 病区管理 > 临时医嘱出智能柜 相关调整 END
		}
	}

	/**
	 * 麻精页签 容器回车事件
	 * 
	 */
	public void onContainerIdClicked(String containerId) {

		if (panel_M.isShowing()) {
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
					setValue("CONTAINER_ID", "");
					this.getTextField("CONTAINER_ID").grabFocus();
					return;
				} else {
					
					//备:按照朱总所说刷容器整合出
					cabinetId  = getValueString("CABINET_ID");
					inParm.setData("CABINET_ID",cabinetId);
					
					//查询出智能柜容器对应的所有麻精的数
					TParm containerToxicParm = SPCContainerTool.getInstance().onQueryByContainerId(inParm);
					int count = containerToxicParm.getCount() ;
					if(count < 0 ){
						this.messageBox("空容器");
						setValue("CONTAINER_ID", "");
						return ;
					}
					
					
					int tableDRow = table_D.getSelectedRow();
					int acumOutBoundQty = table_D.getParmValue().getInt(
							"ACUM_OUTBOUND_QTY", tableDRow);
					int dispenseQty = table_D.getParmValue().getInt(
							"DISPENSE_QTY", tableDRow);
					
					//count 出智能柜容器里面有多少支麻精
					if(    acumOutBoundQty+count > dispenseQty ){
						this.messageBox("容器药品总数量大于实际要出的数量，不能出");
						return ;
					}
					
					takemedNo = getValueString("TAKEMED_NO"); 
					
					TParm inParmAll = new TParm();
					String seqNo = table_D.getParmValue().getValue("ORDER_SEQ",tableDRow);
					
					inParmAll.setData("DISPENSE_NO",takemedNo);
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
						inParm2.setData("DISPENSE_NO",count2,takemedNo);
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
					searchParm.setData("DISPENSE_NO",takemedNo);
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
	}

	/**
	 * 麻精流水号单击事件
	 */
	public void onToxicIdClicked(String toxicId) {
		table_D.acceptText();
		table_M.acceptText();

		//containerId = getValueString("CONTAINER_ID");
		takemedNo = getValueString("TAKEMED_NO");

		// 不显示出来的容器名称
		
		String cabinetId = getValueString("CABINET_ID");

		int tableDRow = table_D.getSelectedRow();
		int dispenseQty = table_D.getParmValue().getInt("DISPENSE_QTY",
				tableDRow);
		int acumOutBoundQty = table_D.getParmValue().getInt(
				"ACUM_OUTBOUND_QTY", tableDRow);
		
		// modify by wangbin 2015/1/26 内部需求 #792 泰心物联网系统调整方案 : 病区管理 > 临时医嘱出智能柜 相关调整 START
		if (tableDRow < 0) {
			this.messageBox("请选中要执行的药品数据行");
			return;
		}
		// modify by wangbin 2015/1/26 内部需求 #792 泰心物联网系统调整方案 : 病区管理 > 临时医嘱出智能柜 相关调整 END
		
		if (dispenseQty == acumOutBoundQty) {
			this.messageBox("已出库完成");
			return;
		}

		 
		if (toxicId != null && !toxicId.equals("")) {

			// 根据麻精流水号与orcer_code查询对应的
			TParm inParm = new TParm();

			// 先查询容器还能不能装

			String orderCode = table_D.getParmValue().getValue("ORDER_CODE",
					tableDRow);

			/**
			 * ODI_DSPNM 四个主键
			 */
			String seqNo = table_D.getParmValue().getValue("ORDER_SEQ",
					tableDRow);
			String startDttm = table_D.getParmValue().getValue("START_DTTM",
					tableDRow);
			String orderNo = table_D.getParmValue().getValue("ORDER_NO",
					tableDRow);
			String caseNo = table_D.getParmValue().getValue("CASE_NO",
					tableDRow);
			inParm.setData("START_DTTM", startDttm);
			inParm.setData("ORDER_NO", orderNo);
			inParm.setData("CASE_NO", caseNo);
			inParm.setData("ORDER_SEQ", seqNo);

			inParm.setData("ORDER_CODE", orderCode);
			inParm.setData("TOXIC_ID", toxicId);
			inParm.setData("CABINET_ID", cabinetId);

			
			/**
			TParm toxParm = new TParm();
			toxParm.setData("DISPENSE_NO", orderNo);
			toxParm.setData("DISPENSE_SEQ_NO", seqNo);
			toxParm.setData("CONTAINER_ID", containerId);
			TParm dParm = SPCToxicTool.getInstance().onQueryDByCount(toxParm);
			int ccout = dParm.getInt("NUM", 0);

			int toxicQty = getValueInt("TOXIC_QTY");
			if (ccout == toxicQty) {
				this.messageBox("容器已满!");
				setValue("CONTAINER_ID", "");

				return;
			}*/

			
			TParm parm = SPCContainerTool.getInstance().onQuery(inParm);
			if (parm.getCount() <= 0) {
				this.messageBox("查无该麻精药品,请重新拣选!");
				return;
			}
			
			containerId = parm.getValue("CONTAINER_ID",0);
			String containerDesc = parm.getValue("CONTAINER_DESC",0);
			Timestamp validDate = parm.getTimestamp("VALID_DATE", 0);
			
			inParm.setData("CONTAINER_ID",containerId);
			inParm.setData("CONTAINER_DESC",containerDesc);

			// 查询对应的效期是否是最近的
			TParm returnParm = SPCToxicTool.getInstance().onQueryByValidDate(
					inParm);
			Timestamp minValidDate = returnParm.getTimestamp("VALID_DATE", 0);

			inParm.setData("BATCH_NO", parm.getValue("BATCH_NO", 0));
			inParm
					.setData("VERIFYIN_PRICE", parm.getValue("VERIFYIN_PRICE",
							0));
			inParm.setData("VALID_DATE", parm.getValue("VALID_DATE", 0));
			inParm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", 0));
			inParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
			// 保存时重新设置空
			inParm.setData("CABINET_ID", "");
			Timestamp nowDate = SystemTool.getInstance().getDate();
			if(validDate.getTime() < nowDate.getTime() ){
				this.messageBox("已过效期或到效期,药品不能出库");
				return ;
			}
			if (validDate.getTime() > minValidDate.getTime()) {
				if (this.messageBox("确定", "取得不是最早效期!", 2) == 0) {
					TParm result = batchSave(containerDesc, inParm);
					if (result.getErrCode() < 0) {
						this.messageBox("保存出错");
					} else {

						int dselectRow = table_D.getSelectedRow();
						 
						table_D.setItem(dselectRow, "ACTUAL_QTY",
								acumOutBoundQty + 1);
					}
				}
			} else {
				TParm result = batchSave(containerDesc, inParm);
				if (result.getErrCode() < 0) {
					this.messageBox("保存出错");
					return ;
				} else {
					int dselectRow = table_D.getSelectedRow();
					table_D.setItem(dselectRow, "ACUM_OUTBOUND_QTY",
							acumOutBoundQty + 1);
				}
			}

			// 查询新增的显示出来
			TParm p = SPCToxicTool.getInstance().onQuery(inParm);
			table_M.setParmValue(p);
			
			setValue("TOXIC_QTY", 1);
			setValue("CONTAINER_DESC", "单支麻精");
			setValue("CONTAINER_ID", "");
			this.getTextField("CONTAINER_ID").grabFocus();

		}

	}

	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow();
		TParm tableDParm = table_D.getParmValue();
		takemedNo = getValueString("TAKEMED_NO");
		String seqNo = tableDParm.getValue("ORDER_SEQ", tableDRow);
		inParm.setData("DISPENSE_NO", takemedNo);
		inParm.setData("DISPENSE_SEQ_NO", seqNo);
		inParm.setData("SEQ_NO", seqNo);
		inParm.setData("IS_BOXED", "Y");
		inParm.setData("BOXED_USER", Operator.getID());
		inParm.setData("CONTAINER_ID", containerId);
		inParm.setData("CONTAINER_DESC", containerDesc);
		if(userId == null || userId.equals("")){
			userId = Operator.getID() ;
		}
		inParm.setData("OPT_USER", userId);
		inParm.setData("TAKEMED_USER",userId);
		inParm.setData("OPT_TERM", Operator.getIP());

		// 写数据到容器交易表主表与明细表,更新实际出库数量
		TParm result = TIOM_AppServer.executeAction(
				"action.spc.SPCOutStoreAction", "onInsertMlStation", inParm);
		return result;
	}

	public boolean onSave() {

		/**
		 * 普药需要保存一起出库
		 */

		TParm parm = new TParm();
		if (panel_D.isShowing()) {
			
			int  tableCount = table.getRowCount() ;
			if(tableCount <= 0 ){
				this.messageBox("没有要出智能柜的普药数据");
				return false;
			}
		
			TParm parmOrder = new TParm();
			TParm checkOrder = new TParm();
			TParm tableParm = table.getParmValue();

			int count = 0;
			
			/**
			if(userId == null || userId.equals("")){
				this.messageBox("请先打开智能柜取药，再保存");
				return false;
				//userId = Operator.getID() ;
			}*/
			for (int i = 0; i < tableParm.getCount(); i++) {
				
				double dispenseQty = tableParm.getDouble("DISPENSE_QTY",i);
				double acumOutBoundQty = tableParm.getDouble("ACUM_OUTBOUND_QTY",i);
				if(acumOutBoundQty >= dispenseQty ){
					continue ;
				}
				count++;
			}
			if(count == 0 ){
				this.messageBox("已全部出库完成");
				return false;
			}
			count = 0 ;
			
			// 密码判断
			if (!checkPW()) {
				return false;
			}
			
			for (int i = 0; i < tableParm.getCount(); i++) {
				
				double dispenseQty = tableParm.getDouble("DISPENSE_QTY",i);
				double acumOutBoundQty = tableParm.getDouble("ACUM_OUTBOUND_QTY",i);
				if(acumOutBoundQty >= dispenseQty ){
					continue ;
				}
				
				parmOrder.setData("ORDER_CODE", count, tableParm.getValue(
						"ORDER_CODE", i));
				parmOrder.setData("OUT_ORG_CODE", count, org_code);
				parmOrder.setData("ACTUAL_QTY", count, tableParm.getValue(
						"DISPENSE_QTY", i));
				
				
				//ODI_DSPNM表参数 更新ACUM_OUTBOUND_QTY
				parmOrder.setData("CASE_NO", count, tableParm.getValue("CASE_NO",i));
				parmOrder.setData("ORDER_NO", count,tableParm.getValue("ORDER_NO",i));
				parmOrder.setData("ORDER_SEQ", count, tableParm.getValue("ORDER_SEQ",i));
				parmOrder.setData("START_DTTM", count, tableParm.getValue("START_DTTM",i));
				
				parmOrder.setData("OPT_USER",count,userId);
				parmOrder.setData("OPT_DATE",count,SystemTool.getInstance().getDate());
				parmOrder.setData("OPT_TERM",count,Operator.getIP());
				
				//药品名称  检测库存用
				parmOrder.setData("ORDER_DESC",count,tableParm.getValue(
						"ORDER_DESC", i));
				parmOrder.setData("TAKEMED_USER",count,userId);
				
				count++;
			}
			if (parmOrder != null) {
				parm.setData("OUT_ORDER", parmOrder.getData());
			}
			
			int count1 = 0;
			for (int i = 0; i < tableParm.getCount(); i++) {
				
				double dispenseQty = tableParm.getDouble("DISPENSE_QTY",i);
				double acumOutBoundQty = tableParm.getDouble("ACUM_OUTBOUND_QTY",i);
				if(acumOutBoundQty >= dispenseQty ){
					continue ;
				}
				
				checkOrder.setData("ORDER_CODE", count1, tableParm.getValue(
						"ORDER_CODE", i));
				checkOrder.setData("OUT_ORG_CODE", count1, org_code);
				checkOrder.setData("ACTUAL_QTY", count1, tableParm.getValue(
						"DISPENSE_QTY", i));
				
				
				//ODI_DSPNM表参数 更新ACUM_OUTBOUND_QTY
				checkOrder.setData("CASE_NO", count1, tableParm.getValue("CASE_NO",i));
				checkOrder.setData("ORDER_NO", count1,tableParm.getValue("ORDER_NO",i));
				checkOrder.setData("ORDER_SEQ", count1, tableParm.getValue("ORDER_SEQ",i));
				checkOrder.setData("START_DTTM", count1, tableParm.getValue("START_DTTM",i));
				
				checkOrder.setData("OPT_USER",count1,userId);
				checkOrder.setData("OPT_DATE",count1,SystemTool.getInstance().getDate());
				checkOrder.setData("OPT_TERM",count1,Operator.getIP());
				
				//药品名称  检测库存用
				checkOrder.setData("ORDER_DESC",count1,tableParm.getValue(
						"ORDER_DESC", i));
				checkOrder.setData("TAKEMED_USER",count1,userId);
				
				count1++;
			}
			if (checkOrder != null) {
				parm.setData("CHECK_ORDER", checkOrder.getData());
			}

			

			parm.setData("CABINET_ID", cabinetId);
			parm.setData("ORG_CODE",org_code);
			
			// 执行数据新增
			TParm result = TIOM_AppServer.executeAction(
					"action.spc.SPCOutStoreAction", "onInsertIndStock", parm);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
			this.messageBox("出库成功");
			setValue("TAKEMED_NO", "");
			this.onQueryList();
		}
		
		return true;
		
		
	}

	public void onClear() {
	
		table_D = (TTable)getComponent("TABLE_D");
		table_M = (TTable)getComponent("TABLE_M");
		
		table_D.acceptText();
		table_M.acceptText() ;
		
		table_D.setParmValue( new TParm());
		table_M.setParmValue(new TParm());
		   	
    	String controlName = "CONTAINER_ID;TAKEMED_NO;BED_NO;MR_NO;PAT_NAME";
		this.clearValue(controlName);
		this.getTextField("CONTAINER_ID").setEditable(true);
		this.getTextField("TAKEMED_NO").grabFocus(); 
	 
		table = (TTable)getComponent("TABLE");
		table.acceptText();
		this.clearValue("TAKEMED_NO");
		table.setParmValue( new TParm());
		
		table_List = getTable("TABLE_LIST");
		table_List.acceptText();
		table_List.setParmValue(new TParm());
		
		userId = "" ;
		 
	}
	
	/**
	 * 出库Table单击事件，没有完全出库的，显示出库的加一行空白行
	 */
	public void onTableDClicked(){
		table_D.acceptText();
		table_M.acceptText() ;
		int row = table_D.getSelectedRow() ;
		TParm parm = table_D.getParmValue() ;
		int dispenseQty  = parm.getInt("DISPENSE_QTY",row);
		int acumOutBoundQty  = parm.getInt("ACUM_OUTBOUND_QTY",row);

		TParm inParm = new TParm();
		inParm.setData("DISPENSE_NO",getValueString("TAKEMED_NO"));
		inParm.setData("SEQ_NO",parm.getValue("ORDER_SEQ",row));
		inParm.setData("ORDER_CODE",parm.getValue("ORDER_CODE",row));
		
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		table_M.setParmValue(p);
		if(acumOutBoundQty < dispenseQty ){
			 
			getTextField("CONTAINER_ID").setEditable(true);
			//getTextField("TOXIC_ID").setEditable(true);
		}else {
			//不新增一个空白行
			this.messageBox("全部出库完成");
			getTextField("CONTAINER_ID").setEditable(false);
			//getTextField("TOXIC_ID").setEditable(false);
			
		}	
		
		setValue("CONTAINER_ID", "");
		setValue("TOXIC_QTY", "");
		setValue("CONTAINER_DESC", "");
		
		table_D.setSelectedRow(row);
	}
	
	
	 public void openSearch(){
	    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);
	}
	 

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	
	/**
	 * 智能柜开门
	 * @param type  0 
	 */
	public void onOpen(int type) {
		
		String cabinetSwitch = getCabinetSwitch();
		
		if(cabinetSwitch != null && cabinetSwitch.equals("Y")) {
			// 5.开门操作 日志正常，但设备正常开 (成功)
			int ret = 0;
			int operid = 1;
			int doorid = type;
			int outputadr = 1;
			int doorstate = 6;
			ret = PullDriver.ControlDevice(rtn1, operid, doorid, outputadr,
					doorstate, 0, "");
		}
	}
	
	
	
	private static  TConfig getProp() {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        return config;
	}
 
	public  static String getCabinetSwitch(){
		 TConfig config = getProp() ;
		 String cabinetSwitch = config.getString("", "CABINET_SWITCH");
		 return cabinetSwitch;
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
	 * 读日志
	 * @return
	 */
	private String getLog() {

		byte[] data = new byte[256];
		int rtn2 = PullDriver.GetRTLog(rtn1, data, 256);
		if (rtn1 < 0) {
		}
		String strData = byte2Str(data);
		return strData;
	}

	/**
	 * 将 byte转成符号
	 */
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		return Total_Data;
	}
	
	
	 
	//麻精
	static  String  DOOR_DRUG = "1"; 
	static  String  DOOR_NOT_DRUG = "0";    
	/**
	 * 日志监听
	 */				
	public void onLog() {																	
		String logStr = this.getLog();	
		String[] log = logStr.split(",");
		
		userId = log[2];
		String dispenseNo = getValueString("TAKEMED_NO");
		if ("8".equals(log[4])) {     
			TParm logParm = new TParm();
			logParm.setData("CABINET_ID", cabinetId);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "6");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "1");		
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", Operator.getID());
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);
			
			//门已开清掉
			if(log[3].equals(guardId)) {
				guardSet.clear();
			}

		}		
		
		 	
		
		//门已关闭
		if ("201".equals(log[4])) {
			TParm logParm = new TParm();
			logParm.setData("CABINET_ID", cabinetId);
			logParm.setData("LOG_TIME", SystemTool.getInstance().getDate());
			logParm.setData("TASK_TYPE", "6");
			logParm.setData("TASK_NO", dispenseNo);
			logParm.setData("EVENT_TYPE", "2");
			logParm.setData("GUARD_ID", log[3]);
			logParm.setData("OPT_USER", userId);
			logParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			logParm.setData("OPT_TERM", Operator.getIP());
			SPCInStoreTool.getInstance().insertLog(logParm);		
			 					
		}
		
		System.out.println(logStr);
		//21:刷卡     14：指纹
		if("21".equals(log[4]) ) {
			int ret = 0;
			int operid = 1;
			int outputadr = 1;			
			int doorstate = 6;
			if(log[3].equals(guardId)) {
				System.out.println(logStr);
				guardSet.add(log[2]);
				if(guardSet.size()==2) {
					ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
					if(ret<0){
						this.messageBox("开门失败");			
					}
				}
			}
			else {
				ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
				if(ret<0){
					this.messageBox("开门失败");			
				}
			}


		}
		
		//指纹
		/**
		if(("21".equals(log[4])||"14".equals(log[4]))&&log[3].equals(DOOR_DRUG)) {				
			int ret = 0;
			int operid = 1;
			int doorid = 1;
			int outputadr = 1;
			int doorstate = 6;
			ret=PullDriver.ControlDevice(rtn1, operid, doorid, outputadr, doorstate, 1, "");
			if(ret<0){
				this.messageBox("开门失败");
			}
		}*/
		
		if("35".equals(log[4])) {							
			int ret = 0;
			int operid = 1;
			int outputadr = 1;
			int doorstate = 6;
			if(log[3].equals(guardId)) {
				guardSet.add(log[2]);
				if(guardSet.size()==2) {
					ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
					if(ret<0){
						this.messageBox("开门失败");			
					}
				}
			}
			else {
				ret=PullDriver.ControlDevice(rtn1, operid, Integer.valueOf(log[3]), outputadr, doorstate,0, "");
				if(ret<0){
					this.messageBox("开门失败");			
				}
			}
		}	
													
		/*onStart();              //监听到关门事件初始化RFID连接
		logTimer.cancel();
		logTask.cancel();*/		 		
	}
	

	
	private TimerTask logTask; // log任务

	private Timer logTimer;

	private long period = 1 * 1000; // 间隔时间

	private long delay = 1*1000; // 延迟时间		
	
	 boolean  isOpen = false ;
	 
	public void doJob() {
		this.logTimer = new Timer();
		this.logTask = new TimerTask() {
			public void run() {
				onLog();
				isOpen = true;
			}
		};
		this.logTimer.schedule(this.logTask, this.delay, this.period);
	}
	
	public void procesClose(){
		logTimer.cancel();
		logTask.cancel();	
		isOpen = false;
	}
	
	
	public void onWindowClose(){
				
		//PullDriver.Disconnect(rtn1);
		// 注销dll
		//PullDriver.free();
		if(logTask != null ){
			logTask.cancel();	
		}
		
		if(logTimer != null ){
			logTimer.cancel();
		}
		
		isOpen = false;
		this.closeWindow();	
	}
	
	private BILComparator compare=new BILComparator();
	
	private boolean ascending = false;
	private int sortColumn = -1;
	/**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addListener(final TTable table) {

		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);

				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTable("TABLE_LIST").getParmValue();
				//TParm tableData = getTTable("TABLE_M").getShowParmValue();
				//System.out.println("tableData:"+tableData);
				tableData.removeGroupData("SYSTEM");

				// ==========modify-end========================================
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTable("TABLE_LIST").getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				TParm lastResultParm=new TParm();//记录最终结果
				lastResultParm=cloneVectoryParam(vct, new TParm(), strNames);//加入中间数据
			
				table.setParmValue(lastResultParm);
				// ==========modify-end========================================

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}	
	
	
	/**
	 * 加入排序功能
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	
	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 * @author liyh
	 * @date 20120710
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}	

	/**
	 * vectory转成param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		return parmTable;

	}
	
}
