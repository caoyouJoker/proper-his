package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCErOutCabinetTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class SPCErOutCabinetControl extends TControl{


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
	
	String rxNo ;
	
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
	
	/**
	 * 初始化
	 */
	public void onInit() {
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
		table_List = getTable("TABLE_LIST");
		
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
        parm.setData("STATUS","N");
        TParm opdParm = SPCErOutCabinetTool.getInstance().onQuery(parm);
		if(opdParm.getCount() > 0 ){
			table_List.setParmValue(opdParm);
		}else{
			
			this.messageBox("没有查找到数据");
			table_List.setParmValue(new TParm());
		}
		
	}
	
	/**
	 * 查询所有未出库或者已出库单据列表
	 */
	public void onQueryList(){
		rxNo = getValueString("RX_NO");
		
		String status = "";
		//UPDATE_Y 已出库，UPDATE_N 未出库
        if(getRadioButton("UPDATE_Y").isSelected()){
        	status = "Y";
        }else if(getRadioButton("UPDATE_N").isSelected()){
        	status = "N";
        }
        
        //起始时间
        String startDate = getValueString("START_DATE");
        startDate = startDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        
        //结束时间
        String endDate = getValueString("END_DATE");
        endDate = endDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        
        TParm parm = new TParm() ;
         
        parm.setData("STATUS",status);
        parm.setData("START_DATE",startDate);
        parm.setData("END_DATE",endDate);
        parm.setData("RX_NO",rxNo);
        
        TParm odiParm = SPCErOutCabinetTool.getInstance().onQuery(parm);
		if(odiParm.getCount() > 0 ){
			table_List.setParmValue(odiParm);
		}else{
			this.messageBox("没有查找到数据");
			table_List.setParmValue(new TParm());
			table_D.setParmValue(new TParm());
			table_M.setParmValue(new TParm());
		}
	}
	
	/**
	 * 未出库列表表格单击事件
	 */
	public void onTableListClick(){
		
		int row = table_List.getSelectedRow() ;
		if (row < 0) {
			this.messageBox_("没有选中出库信息");
			return;
		}
		 
		String rxNo = table_List.getItemString(row, "RX_NO");
		if (rxNo == null || rxNo.equals("")) {
			this.messageBox_("取得出库信息失败");
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("RX_NO", rxNo);
		TParm result = new TParm();
		result = SPCErOutCabinetTool.getInstance().onQueryOpd(parm);
		if (result.getCount() < 0) {
			this.messageBox("查无数据");
			table_D.setParmValue(new TParm());
			return ;
		}
		 
		//是否出库完成
		boolean isOut = false;
		for(int i = 0 ; i < result.getCount() ; i++ ){
			TParm rowParm = (TParm)result.getRow(i);
			double dispenseQty = rowParm.getDouble("DISPENSE_QTY");
			double acumOutBoundQty = rowParm.getDouble("ACUM_OUTBOUND_QTY");
			
			//需要出库数是否大于已出库的数量 
			if(dispenseQty > acumOutBoundQty ){
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
		}
		
		this.setValue("RX_NO", rxNo);
		table_D.setParmValue(result);
		table_D.setSelectedRow(0);
		this.getTextField("CONTAINER_ID").grabFocus();

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
			// modify by wangbin 2015/1/29 内部需求 #791 泰心物联网系统调整方案 急诊区管理 > 医嘱出智能柜 相关调整 START
			this.messageBox("查无该麻精条码号，出库失败");
//			onContainerIdClicked(containerId);
			// modify by wangbin 2015/1/29 内部需求 #791 泰心物联网系统调整方案 急诊区管理 > 医嘱出智能柜 相关调整 START
		}
	}
	
	/**
	 * 麻精流水号单击事件
	 */
	public void onToxicIdClicked(String toxicId) {
		table_D.acceptText();
		table_M.acceptText();

		rxNo = getValueString("RX_NO");

		// 不显示出来的容器名称
		
		String cabinetId = getValueString("CABINET_ID");

		int tableDRow = table_D.getSelectedRow();
		int dispenseQty = table_D.getParmValue().getInt("DISPENSE_QTY",
				tableDRow);
		int acumOutBoundQty = table_D.getParmValue().getInt(
				"ACUM_OUTBOUND_QTY", tableDRow);
		
		// modify by wangbin 2015/1/29 内部需求 #791 泰心物联网系统调整方案 急诊区管理 > 医嘱出智能柜 相关调整 START
		if (tableDRow < 0) {
			this.messageBox("请选中要执行的药品数据行");
			return;
		}
		// modify by wangbin 2015/1/29 内部需求 #791 泰心物联网系统调整方案 急诊区管理 > 医嘱出智能柜 相关调整 END


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
			 * OPD_ORDER 三个主键
			 */
			String seqNo = table_D.getParmValue().getValue("SEQ_NO",
					tableDRow);
			String rxNo = table_D.getParmValue().getValue("RX_NO",
					tableDRow);
			String caseNo = table_D.getParmValue().getValue("CASE_NO",
					tableDRow);
			inParm.setData("RX_NO", rxNo);
			inParm.setData("CASE_NO", caseNo);
			inParm.setData("SEQ_NO", seqNo);

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
	
	/**
	 * 麻精页签 容器回车事件
	 * 
	 */
	public void onContainerIdClicked(String containerId) {

		
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
					
					rxNo = getValueString("RX_NO"); 
					
					TParm inParmAll = new TParm();
					String seqNo = table_D.getParmValue().getValue("SEQ_NO",tableDRow);
					
					inParmAll.setData("DISPENSE_NO",rxNo);
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
					 * OPD_ORDER 三个主键
					 */
					 
					 
					String caseNo = table_D.getParmValue().getValue("CASE_NO",
							tableDRow);
					 
					inParmAll.setData("RX_NO", rxNo);
					inParmAll.setData("CASE_NO", caseNo);
					inParmAll.setData("SEQ_NO", seqNo);
					
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
						inParm2.setData("DISPENSE_NO",count2,rxNo);
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
					                                    "onInsertErBatch", inParmAll);
				
					if(result2.getErrCode() < 0 ){
						this.messageBox("麻精出智能柜失败!");
						return ;
					}else{
					
						int acturalQty = acumOutBoundQty + count;
						table_D.setItem(tableDRow, "ACUM_OUTBOUND_QTY",acturalQty);
					}
					
					// 查询新增的显示出来
					TParm searchParm = new TParm();
					searchParm.setData("DISPENSE_NO",rxNo);
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
		int dispenseQty  = parm.getInt("DISPENSE_QTY",row);
		int acumOutBoundQty  = parm.getInt("ACUM_OUTBOUND_QTY",row);

		TParm inParm = new TParm();
		inParm.setData("DISPENSE_NO",getValueString("RX_NO"));
		inParm.setData("SEQ_NO",parm.getValue("SEQ_NO",row));
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
		
		table_M.setSelectedRow(row);
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
	
	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow();
		TParm tableDParm = table_D.getParmValue();
		rxNo = getValueString("RX_NO");
		String seqNo = tableDParm.getValue("SEQ_NO", tableDRow);
		inParm.setData("DISPENSE_NO", rxNo);
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
				"action.spc.SPCOutStoreAction", "onInsertEr", inParm);
		return result;
	}
	
	public void onCLear(){
		String controlName = "RX_NO;CONTAINER_ID;TOXIC_ID;TOXIC_QTY;CONTAINER_DESC";
		this.clearValue(controlName);
		this.getTextField("CONTAINER_ID").setEditable(true);
		
		table_M = getTable("TABLE_M");
		table_D = getTable("TABLE_D");
		table_List = getTable("TABLE_LIST");
		table_M.acceptText() ;
		table_D.acceptText() ;
		table_List.acceptText() ;
		table_D.setParmValue(new TParm());
		table_M.setParmValue(new TParm());
		table_List.setParmValue(new TParm());
	}
	
	
	
	
	
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
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
    

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
    

	public void onWindowClose(){
		 
		this.closeWindow();	
	}


}
