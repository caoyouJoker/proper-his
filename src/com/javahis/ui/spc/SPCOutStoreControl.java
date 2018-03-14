package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCOutStoreTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
 
/**
 * 
 * <p>Title: 补货任务-药房麻精出智能柜</p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCOutStoreControl extends TControl {
	
	/**
	 * 麻精详细药品Table
	 */
	TTable table_N ,table_D;
	
	
	/**
	 * 出库单号
	 */
	String dispenseNo ;
	
	/**
	 * 周转箱
	 */
	String boxEslId ;
	
	/**
	 * 容器ID
	 */
	String containerId ;
	
	String ip ;
	
	
	/**
	 * 智能柜本机物理IP
	 */
	String guardIp;
	
	/**
	 * 初始化　
	 */
	public void onInit() {
		super.onInit();
		
		table_D = (TTable)getComponent("TABLE_D");
		table_N = (TTable)getComponent("TABLE_N");
		
		
		this.onInitData();
		
		//麻精流水值事件
		/**
		table_N.addEventListener("TABLE_N->"+TTableEvent.CHANGE_VALUE, this,
		"onTableNChange");*/
 
	}
	
	 /***-----------------------------------门禁---------------------------------------------------------------*/
 

 

	
	public void onTestRfid(TParm parm){
		
		
		table_D.acceptText() ;
		
		//数据库数据与智能柜数据对比得到拿走的容器
		TParm tableDParm = table_D.getParmValue() ;
		String seqNo = "";
		int count2 = 0 ;
		for(int k = 0 ; k < parm.getCount("CONTAINER_ID") ; k++ ){
			TParm inParmAll = new TParm();
			String containerId = parm.getValue("CONTAINER_ID",k);
			TParm inParm = new TParm();
			//根据容器ID查询对应的麻精信息
			String cabinetId = getValueString("CABINET_ID");
			inParm.setData("CONTAINER_ID",containerId);
			inParm.setData("CABINET_ID",cabinetId);
			
			//一个容器对应一个麻精药品
			TParm result = SPCOutStoreTool.getInstance().onQueryContainerDetail(inParm);
			String orderCode1 = result.getValue("ORDER_CODE",0);
			for(int i = 0 ; i < tableDParm.getCount("ORDER_CODE"); i++ ){
				String orderCode2 = tableDParm.getValue("ORDER_CODE",i) ; 
				if(orderCode1.equals(orderCode2)){
					seqNo = tableDParm.getValue("SEQ_NO",i);
					break;
				}
			}
			
			//拿错药
			if(seqNo == null || seqNo.equals("")) {
				seqNo = "999" ;
			}
			
			inParmAll.setData("DISPENSE_NO",dispenseNo);
			inParmAll.setData("DISPENSE_SEQ_NO",seqNo);
			inParmAll.setData("CONTAINER_ID",containerId);
			
			inParmAll.setData("CONTAINER_DESC",parm.getValue("CONTAINER_DESC",k));
			inParmAll.setData("IS_BOXED","Y");
			inParmAll.setData("BOXED_USER",Operator.getID());
			inParmAll.setData("BOX_ESL_ID",boxEslId);
			inParmAll.setData("OPT_USER",Operator.getID());
			inParmAll.setData("OPT_TERM",Operator.getIP());
			inParmAll.setData("CABINET_ID",getValueString("CABINET_ID"));
			inParmAll.setData("ORDER_CODE",orderCode1);
			TParm inParm2 = new TParm() ;
			for(int i = 0 ; i < result.getCount() ; i++ ){
				TParm rowParm = (TParm)result.getRow(i) ;
				inParm2.setData("ORDER_CODE",count2,orderCode1);
				inParm2.setData("BATCH_NO",count2 ,rowParm.getValue("BATCH_NO"));
				inParm2.setData("VERIFYIN_PRICE",count2,rowParm.getValue("VERIFYIN_PRICE"));
				inParm2.setData("VALID_DATE",count2,rowParm.getValue("VALID_DATE"));
				inParm2.setData("BATCH_SEQ",count2,rowParm.getValue("BATCH_SEQ"));
				inParm2.setData("DISPENSE_NO",count2,dispenseNo);
				inParm2.setData("DISPENSE_SEQ_NO",count2,seqNo);
				inParm2.setData("CABINET_ID",count2,getValueString("CABINET_ID")); 
				inParm2.setData("IS_BOXED",count2,"Y");
				inParm2.setData("TOXIC_ID",count2,rowParm.getValue("TOXIC_ID"));
				inParm2.setData("BOXED_USER",count2,Operator.getID());
				inParm2.setData("BOX_ESL_ID",count2,boxEslId);
				inParm2.setData("CONTAINER_ID",count2,rowParm.getValue("CONTAINER_ID"));
				inParm2.setData("CONTAINER_DESC",count2,rowParm.getValue("CONTAINER_DESC"));
				inParm2.setData("OPT_USER",count2,Operator.getID());
				inParm2.setData("OPT_TERM",count2,Operator.getIP());
				count2++ ;
			}
			
			if(inParm2 != null ){
				inParmAll.setData("OUT_D",inParm2.getData());  
			}
			
			//写数据到容器交易表主表与明细表,更新实际出库数量
			TParm result2  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
			                                    "onInsertBatch", inParmAll);
			if(result2.getErrCode() != -1  ){
				if(seqNo != null && !seqNo.equals("") && !seqNo.equals("999")){
					for(int i = 0 ; i < tableDParm.getCount("ORDER_CODE"); i++ ){
						String orderCode2 = tableDParm.getValue("ORDER_CODE",i) ;
						if(orderCode1.equals(orderCode2)){
							table_D.setItem(i, "ACTUAL_QTY", tableDParm.getInt("ACTUAL_QTY",i) +result.getCount());
							break;
						}
					}
				}
			}
		}

		/**
    	//写日志
		TParm logParm = new TParm();
		logParm.setData("TASK_TYPE","5");
		logParm.setData("EVENT_TYPE","1");
		logParm.setData("GUARD_ID","");
		builderLog(logParm);
		*/
			
	}

	 
    
	// 将 byte转成符号
	private static String byte2Str(byte[] Data) {
		String Total_Data = "";
		for (int i = 0; i < Data.length; i++) {
			Character CWord = new Character((char) Data[i]);
			Total_Data = Total_Data + CWord.toString();
		}
		return Total_Data;
	}
    /**-----------------------------------------------------*/
	
	/**
	 * 初始化界面
	 */
	public void onInitData(){
		 
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
		setValue("CABINET_ID", result.getValue("CABINET_ID", 0));
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		setValue("OPT_USER", Operator.getName());
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		
		table_D = (TTable)getComponent("TABLE_D");
		table_N = (TTable)getComponent("TABLE_N");
		
		//查智能柜表()
		dispenseNo = getValueString("DISPENSE_NO");
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO",dispenseNo);
		TParm result = SPCOutStoreTool.getInstance().onQuery(parm);
		
		 
		if(result.getCount() > 0 ){
			table_D.setParmValue(result);			
			setValue("OUT_ORG_CHN_DESC", result.getValue("ORG_CHN_DESC",0));
			setValue("REQTYPE_CODE", getReqtype(result.getValue("REQTYPE_CODE",0)));
			table_D.setSelectedRow(0);
			
			/**
			double qty = StringTool.round(result.getDouble("QTY",0),2);
			double actualQty = StringTool.round(result.getDouble("ACTUAL_QTY",0), 2);
			String orderCode = result.getValue("ORDER_CODE",0);*/
			
			//查询是否已有出库的麻精
			parm.setData("SEQ_NO",result.getValue("SEQ_NO",0));
			
			TParm p = SPCToxicTool.getInstance().onQuery(parm);
			table_N.setParmValue(p);
			
		}else{
			this.messageBox("没有查询到数据");
		}
		
	}
	
	/**
	 * 出库单号回车事件
	 */
	public void onDispenseNoClick(){
		
		onQuery();
		this.getTextField("BOX_ESL_ID").grabFocus();
				
		//写日志
		/**
		TParm parm = new TParm();
		parm.setData("TASK_TYPE","5");
		parm.setData("EVENT_TYPE","1");
		parm.setData("GUARD_ID","");
		builderLog(parm);
		//initTimer();
		*/
		 
	}

	/**
	 * 组装写日志数据
	 */
	/**
	private void builderLog(TParm p) {
		//写开小门的日志
		TParm parm = new TParm();
		parm.setData("CABINET_ID", getValueString("CABINET_ID"));
		parm.setData("LOG_TIME", SystemTool.getInstance().getDate());
		parm.setData("TASK_TYPE", p.getValue("TASK_TYPE"));
		parm.setData("EVENT_TYPE", p.getValue("EVENT_TYPE")); //1开门，２关门
		parm.setData("GUARD_ID", p.getValue("GUARD_ID"));   //
		
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM",Operator.getIP());
		
		onSaveLog(parm);
	}*/
	
	/**
	 * 待出库Table单击事件
	 */
	public void onTableDClicked(){
		table_D.acceptText();
		table_N.acceptText() ;
		int row = table_D.getSelectedRow() ;
		TParm parm = table_D.getParmValue() ;
		double qty = StringTool.round(parm.getDouble("QTY",row),2);
		double actualQty = StringTool.round(parm.getDouble("ACTUAL_QTY",row), 2);
		String orderCode = parm.getValue("ORDER_CODE",row);
		
		//先查询是否有出库的，有显示出库的，并在最后新增一个空白行
		TParm inParm = new TParm();
		inParm.setData("DISPENSE_NO",getValueString("DISPENSE_NO"));
		inParm.setData("SEQ_NO",parm.getValue("SEQ_NO",row));
		inParm.setData("ORDER_CODE",orderCode);
		
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		table_N.setParmValue(p);
		if(actualQty < qty ){
			 
			getTextField("BOX_ESL_ID").setEditable(true);
			getTextField("CONTAINER_ID").setEditable(true);
		}else {
			//不新增一个空白行
			this.messageBox("全部出库完成");
			getTextField("BOX_ESL_ID").setEditable(false);
			getTextField("CONTAINER_ID").setEditable(false);
			
		}	
		
		setValue("BOX_ESL_ID", "");
		setValue("CONTAINER_ID", "");
		setValue("TOXIC_QTY", "");
		setValue("CONTAINER_DESC", "");
		setValue("TOXIC_ID", "");
		table_D.setSelectedRow(row);
	}
	
	/**
	 * 输入麻精流水号事件
	 * @param tNode
	 * @return
	 */
	public void onToxicIdClicked(){
		
		table_D.acceptText();
		table_N.acceptText() ;
		
		boxEslId = getValueString("BOX_ESL_ID");
		containerId = getValueString("CONTAINER_ID");
		String toxicId = getValueString("TOXIC_ID");
		
		//先判断是否出库完成
		int tableDrow = table_D.getSelectedRow() ;
		TParm tableD_parm = table_D.getParmValue() ;
		double tableD_qty = StringTool.round(tableD_parm.getDouble("QTY",tableDrow),2);
		double tableD_actualQty = StringTool.round(tableD_parm.getDouble("ACTUAL_QTY",tableDrow), 2);
		if(tableD_actualQty >= tableD_qty){
			this.messageBox("这项药品已出库完成");
			return ;
		}
		
		//不显示出来的容器名称
		String containerDesc = getValueString("CONTAINER_DESC");
		String cabinetId = getValueString("CABINET_ID");
		
		if(StringUtil.isNullString(boxEslId)){
			this.messageBox("周转箱不能为空，请先扫描!");
			return ;
		}
		if(StringUtil.isNullString(containerId)){
			this.messageBox("容器不能为空,请先扫描");
			return ;
		}
		
		if(StringUtil.isNullString(toxicId)){
			this.messageBox("麻精流水号为空！");
			return ;
		}
		
		 
		TParm tableDParm = table_D.getParmValue() ;
		String orderCode = tableDParm.getValue("ORDER_CODE",tableDrow);
		 
		//根据麻精流水号与orcer_code查询对应的
		TParm inParm = new TParm();
		inParm.setData("ORDER_CODE",orderCode);
		inParm.setData("TOXIC_ID",toxicId);
		inParm.setData("CABINET_ID",cabinetId);
	
		//先查询容器还能不能装
		 
		
		dispenseNo = tableDParm.getValue("DISPENSE_NO",tableDrow);
		String seqNo = tableDParm.getValue("SEQ_NO",tableDrow);
		TParm toxParm = new TParm();
		toxParm.setData("DISPENSE_NO",dispenseNo);
		toxParm.setData("DISPENSE_SEQ_NO",seqNo);
		toxParm.setData("CONTAINER_ID",containerId);
		TParm dParm = SPCToxicTool.getInstance().onQueryDByCount(toxParm);
		int ccout = dParm.getInt("NUM",0) ;
		 
		int toxicQty = getValueInt("TOXIC_QTY");
		if(ccout == toxicQty ){
			this.messageBox("容器已满!");
			setValue("CONTAINER_ID", "");
			return ;
		}
		
		TParm parm = SPCContainerTool.getInstance().onQuery(inParm);
		if(parm.getCount() <=  0){
			this.messageBox("查无该麻精药品,请重新拣选!");
			return ;
		}
		Timestamp validDate = parm.getTimestamp("VALID_DATE",0);
	 
		//查询对应的效期是否是最近的
		TParm returnParm = SPCToxicTool.getInstance().onQueryByValidDate(inParm);
		Timestamp  minValidDate = returnParm.getTimestamp("VALID_DATE",0);
		
		inParm.setData("BATCH_NO",parm.getValue("BATCH_NO",0));
		inParm.setData("VERIFYIN_PRICE",parm.getValue("VERIFYIN_PRICE",0));
		inParm.setData("VALID_DATE",parm.getValue("VALID_DATE",0));
		inParm.setData("BATCH_SEQ",parm.getValue("BATCH_SEQ",0));
		inParm.setData("UNIT_CODE",parm.getValue("UNIT_CODE",0));
		
		//保存时重新设置空
		inParm.setData("CABINET_ID",""); 
		if(validDate.getTime() >  minValidDate.getTime()){
			if (this.messageBox("确定", "取得不是最早效期!", 2) == 0) {
				TParm result = batchSave(containerDesc, inParm);
				if(result.getErrCode() < 0 ){
					this.messageBox("保存出错");
					return;
				}else {
					table_D.setItem(tableDrow, "ACTUAL_QTY", tableD_actualQty+1);
				}
			}
		}else {
			TParm result = batchSave(containerDesc, inParm);
			if(result.getErrCode() < 0 ){
				this.messageBox("保存出错");
				return ;
			}else {
				table_D.setItem(tableDrow, "ACTUAL_QTY", tableD_actualQty+1);
			}
		}
		
		//查询新增的显示出来
		TParm p = SPCToxicTool.getInstance().onQuery(inParm);
		
		table_N.setParmValue(p);
		setValue("TOXIC_ID", "");
		//setValue("CONTAINER_ID", "");
		this.getTextField("CONTAINER_ID").grabFocus();

	}

	/**
	 * 
	 * @param containerDesc
	 * @param inParm  组装好的参数
	 * @return
	 */
	private TParm batchSave(String containerDesc, TParm inParm) {
		int tableDRow = table_D.getSelectedRow() ;
		TParm tableDParm = table_D.getParmValue() ;
		dispenseNo = tableDParm.getValue("DISPENSE_NO",tableDRow);
		String seqNo = tableDParm.getValue("SEQ_NO",tableDRow);
		inParm.setData("DISPENSE_NO",dispenseNo);
		inParm.setData("DISPENSE_SEQ_NO",seqNo);
		inParm.setData("SEQ_NO",seqNo);
		inParm.setData("IS_BOXED","Y");
		inParm.setData("BOXED_USER",Operator.getID());
		inParm.setData("BOX_ESL_ID",boxEslId);
		inParm.setData("CONTAINER_ID",containerId);
		inParm.setData("CONTAINER_DESC",containerDesc);
		inParm.setData("OPT_USER",Operator.getID());
		inParm.setData("OPT_TERM",Operator.getIP());
		
		//写数据到容器交易表主表与明细表,更新实际出库数量
		TParm result  = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
		                                    "onInsert", inParm);
		return result ;
	}
	
	/**
	 * 周转箱回车事件
	 */
	public void onBoxEslIdClick(){
		boxEslId = getValueString("BOX_ESL_ID");
		if(boxEslId.length() <= 0 ){
			
			this.messageBox("周转箱不能为空");
			this.getTextField("BOX_ESL_ID").grabFocus();
			return ;
		}
		this.getTextField("CONTAINER_ID").grabFocus();
		this.getTextField("BOX_ESL_ID").setEditable(false);
		EleTagControl.getInstance().login();
		EleTagControl.getInstance().sendEleTag(boxEslId, getValueString("ORG_CHN_DESC"), "", "", 0);
		
		return ;
	}
	
	public void bindBoxEslId(){
		this.getTextField("BOX_ESL_ID").grabFocus();
		this.getTextField("BOX_ESL_ID").setEditable(true);
	}
	
	/**
	 * 容器回车事件
	 */
	public void onContainerIdClick(){
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
			}else {
				double toxicQty = result.getDouble("TOXIC_QTY",0);
				setValue("TOXIC_QTY", toxicQty);
				setValue("CONTAINER_DESC", result.getValue("CONTAINER_DESC",0));
			}
		}else {
			this.messageBox("没有该容器!");
			setValue("CONTAINER_ID", "");
			setValue("CONTAINER_DESC","");
			this.getTextField("CONTAINER_ID").grabFocus();
		}
			
		this.getTextField("TOXIC_ID").grabFocus();
		
		/**查询容器对应麻精数量(containerId)
		String cabinetId = getValueString("CABINET_ID");
		inParm.setData("CABINET_ID",cabinetId);
		TParm resultSec = SPCContainerTool.getInstance().onQueryByContainerId(inParm);
		if(resultSec.getCount() > 0 ){
			
			if (this.messageBox("确定", "确定整个容器出库吗!", 2) == 0) {
				TParm returnParm = table_N.getParmValue();
				returnParm.addParm(resultSec);
				table_N.setParmValue(returnParm);
				
				int tableDrow = table_D.getSelectedRow() ;
				TParm tableDParm = table_D.getParmValue() ;
				String orderCode = tableDParm.getValue("ORDER_CODE",tableDrow);
				 
				//根据麻精流水号与orcer_code查询对应的
				inParm.setData("ORDER_CODE",orderCode);
				inParm.setData("CABINET_ID",cabinetId);
				//循环保存 相当于下面扫麻精药每支出库
				for(int i = 0 ; i <  resultSec.getCount() ; i++ ){
					
				}
			}
		}*/
		
		
	}
	
	/**
	 * 新增空白的一行
	 */
	public TParm addTableDRow(TParm parm,String orderCode){
		if(parm.getCount() < 0 ){
			int count = table_N.getRowCount() ;
			
		 
			/**
			 * 序号
			 */
			parm.addData("ROW_NUM",count+1);
		}else{
			parm.addData("ROW_NUM", parm.getCount()+1);
		}
			
		/**
		 * 药品代码
		 */
		parm.addData("ORDER_CODE",orderCode);
		
		/**
		 * 麻精流水号
		 */
		parm.addData("TOXIC_ID","");
		
		/**
		 * 批号
		 */
		parm.addData("BATCH_NO","");
		
		/**
		 * 效期　
		 */
		parm.addData("VALID_DATE","");
		
		/**
		 * 所属容器
		 */
		parm.addData("CONTAINER_ID","");
		
		return parm;
		
	}
	/**
	 * 得到单号类型　
	 * @param reqtype
	 * @return
	 */
	private String getReqtype(String reqtype){
		if(StringUtil.isNullString(reqtype)){
			return "";
		}
		if(reqtype.equals("DEP")){
			return "部门请领";
		}else if(reqtype.equals("TEC")){
			return "备药生成";
		}else if(reqtype.equals("EXM")){
			return "补充计费";
		}else if(reqtype.equals("GIF")){
			return "药房调拨";
		}else if(reqtype.equals("RET")){
			return "退库";
		}else if(reqtype.equals("WAS")){
			return "损耗";
		}else if(reqtype.equals("THO")){
			return "其它出库";
		}else if(reqtype.equals("COS")){
			return "卫耗材领用";
		}else if(reqtype.equals("THI")){
			return "其它入库";
		}else if(reqtype.equals("EXM")){
			return "科室备药";
			
		}
		return "";
	}
	
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}

	
	 /**
     * 清空方法
     */
    public void onClear() {
    	table_D = (TTable)getComponent("TABLE_D");
		table_N = (TTable)getComponent("TABLE_N");
		
		table_N.acceptText();
		table_N.setParmValue(new TParm());
		   	
    	String controlName = "DISPENSE_NO;OUT_ORG_CHN_DESC;REQTYPE_CODE;BOX_ESL_ID;CONTAINER_ID;CONTAINER_DESC";
		this.clearValue(controlName);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		table_D.removeRowAll();
		
		
    }
    
    
    
    /**
     * 关闭窗口
     */
    /**
    public void onCloseF(){
    	
    	//写日志
		TParm parm = new TParm();
		parm.setData("TASK_TYPE","5");
		parm.setData("EVENT_TYPE","1");
		parm.setData("GUARD_ID","");
		builderLog(parm);
		
		//5.关才连接
		PullDriver.Disconnect(rtn1);
		
		//注销dll
		PullDriver.free();
		task.cancel();
	
    	this.onClosing();
    }*/
    
    /**
    public void onOpen(){
    	openDor(0,Operator.getIP());
    	
    	//写日志
		TParm parm = new TParm();
		parm.setData("TASK_TYPE","5");
		parm.setData("EVENT_TYPE","1");
		parm.setData("GUARD_ID","");
		builderLog(parm);
    }*/
    
    
    public void onSaveLog(TParm parm){
    	SPCInStoreTool.getInstance().insertLog(parm);
    }
   
   
    
    public void openSearch(){
    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);

    }
    
   

}
