package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCContainerTool;
import jdo.spc.SPCOperationRoomOutTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

public class SPCOperationRoomOutControl   extends TControl{

	String toxicId ;
	String dispenseNo ;
	String ip;
	String orgCode;
	String guardIp ;
	String cabinetId;
	TTable table ;
	
	TParm resultParm ;
	
	String userId ;
	/**
	 * 初始化
	 */
	public void onInit() {
		table = getTable("TABLE");
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
		cabinetId =  result.getValue("CABINET_ID", 0) ;
		setValue("CABINET_ID",cabinetId);
		setValue("CABINET_DESC", result.getValue("CABINET_DESC", 0));
		setValue("ORG_CHN_DESC", result.getValue("ORG_CHN_DESC", 0));
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		if(getRadioButton("UPDATE_Y").isSelected()){
			toxicId = getValueString("TOXIC_ID");
			dispenseNo = getValueString("DISPENSE_NO");
			TParm parm = new TParm() ;
			if( (toxicId != null && !toxicId.equals("")) || 
				( dispenseNo !=null && !dispenseNo.equals(""))){
				if(toxicId != null && !toxicId.equals("") ) {
					parm.setData("TOXIC_ID",toxicId);
				}
				if(dispenseNo !=null && !dispenseNo.equals("")) {
					parm.setData("DISPENSE_NO",dispenseNo);
				}
				TParm result = SPCOperationRoomOutTool.getInstance().onQuery(parm);
				if(result.getCount() > 0 ){
					resultParm = result ;
					table.setParmValue(result);
				}else{
					this.messageBox("没有查询到数据");
				}
			}
		}
		if(getRadioButton("UPDATE_N").isSelected()){
			table.setParmValue(new TParm());
			setValue("DISPENSE_NO", "");
		}
	}
	
	/**
	 * 条码事件
	 */
	public void onCode(){
		//容器与麻精流水号同一个控件
		toxicId = getValueString("TOXIC_ID");
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
	 * 条码单击事件
	 */
	public void onToxicIdClicked(String toxicId){
		
		TParm searchParm = new TParm();
		searchParm.setData("TOXIC_ID",toxicId);
		searchParm.setData("CABINET_ID",cabinetId);
		table =  getTable("TABLE") ;
		
		
		int rowCount  = table.getRowCount() ;
		TParm exitsParm = null ;
		if(rowCount > 0 ){
			 exitsParm = table.getParmValue();
			 TParm result = SPCOperationRoomOutTool.getInstance().onQueryBy(searchParm);
			  
			 for(int j = 0 ; j < exitsParm.getCount("TOXIC_ID") ; j++){
				 TParm secParm = (TParm)exitsParm.getRow(j);
				 String secToxicId = secParm.getValue("TOXIC_ID");
				 if(toxicId.equals(secToxicId)){
					 this.messageBox("列表已存在该麻精,麻精流水号："+secToxicId);
					 setValue("TOXIC_ID", "");
					 this.getTextField("TOXIC_ID").grabFocus();
					 return ;
				 }
			  
		     }
			 exitsParm.addParm(result);
		}else {
			 exitsParm = SPCOperationRoomOutTool.getInstance().onQueryBy(searchParm);
		}
		resultParm = exitsParm ;
		table.setParmValue(exitsParm);
		setValue("TOXIC_ID", "");
		this.getTextField("TOXIC_ID").grabFocus();
	}
	
	public void onContainerIdClicked(String containerId){
		TParm searchParm = new TParm();
		searchParm.setData("CONTAINER_ID",containerId);
		table =  getTable("TABLE") ;
		searchParm.setData("CABINET_ID",cabinetId);
		
		int rowCount  = table.getRowCount() ;
		TParm exitsParm = null ;
		if(rowCount > 0 ){
			 exitsParm = table.getParmValue();
			 TParm result = SPCOperationRoomOutTool.getInstance().onQueryBy(searchParm);
			 for(int i = 0 ; i < result.getCount() ; i++){
				 TParm rowParm = (TParm)result.getRow(i);
				 String toxicId = rowParm.getValue("TOXIC_ID");
				 for(int j = 0 ; j < exitsParm.getCount("TOXIC_ID") ; j++){
					 TParm secParm = (TParm)exitsParm.getRow(j);
					 String secToxicId = secParm.getValue("TOXIC_ID");
					 if(toxicId.equals(secToxicId)){
						 this.messageBox("列表已存在该麻精,麻精流水号："+secToxicId);
						 setValue("TOXIC_ID", "");
						 this.getTextField("TOXIC_ID").grabFocus();
						 return ;
					 }
				 }
			 }
			 exitsParm.addParm(result);
		}else {
			 exitsParm = SPCOperationRoomOutTool.getInstance().onQueryBy(searchParm);
		}
		resultParm = exitsParm ;
		table.setParmValue(exitsParm);
		setValue("TOXIC_ID", "");
		this.getTextField("TOXIC_ID").grabFocus();
	}
	
	/**
	 * 保存事件
	 */
	public void onSave(){
		if(getRadioButton("UPDATE_Y").isSelected()){
			this.messageBox("已出库不能保存");
			return ;
		}
		
		if(getValueString("DISPENSE_NO")!= null &&  !getValueString("DISPENSE_NO").equals("")  ){
			this.messageBox("已出库不能保存");
			return ;
		}
		
		int count = table.getRowCount() ;
		if(count <= 0 ){
			this.messageBox("没有保存数据");
			return ;
		}
		// 密码判断
		if (!checkPW()) {
			return ;
		}
		dispenseNo = SystemTool.getInstance().getNo("ALL", "IND",
	                "IND_DISPENSE", "No");
		TParm parm = new TParm() ;
		
		
		TParm parmD = new TParm();
		for(int i = 0 ; i < count ; i++){
			
			parmD.setData("DISPENSE_NO",i,dispenseNo);
			parmD.setData("DISPENSE_SEQ_NO",i,1);
			parmD.setData("CONTAINER_ID",i,resultParm.getValue("CONTAINER_ID",i));
			parmD.setData("TOXIC_ID",i,table.getItemData(i,"TOXIC_ID"));
			
			parmD.setData("ORDER_CODE",i,table.getItemData(i,"ORDER_CODE"));
			parmD.setData("BATCH_NO",i,table.getItemData(i,"BATCH_NO"));
			
			String validDate = resultParm.getValue("VALID_DATE",i);
			
			parmD.setData("VALID_DATE",i,validDate);
			parmD.setData("VERIFYIN_PRICE",i,resultParm.getDouble("VERIFYIN_PRICE",i));
			parmD.setData("BATCH_SEQ",i,resultParm.getInt("BATCH_SEQ",i));
			parmD.setData("CABINET_ID",i,cabinetId);
			parmD.setData("UNIT_CODE",i,resultParm.getValue("UNIT_CODE",i));
			parmD.setData("OPT_USER",i,userId);
			parmD.setData("OPT_TERM",i,Operator.getIP());	
		}
		if(parmD.getCount("DISPENSE_NO") > 0){
			parm.setData("OUT_D", parmD.getData());
		}
		

		HashSet<String> set = new HashSet<String>();
		for(int i = 0 ; i < count ; i++){
			TParm rowParm = (TParm)resultParm.getRow(i);
			String containerId = rowParm.getValue("CONTAINER_ID");
			String containerDesc = rowParm.getValue("CONTAINER_DESC");
			String orderCode = rowParm.getValue("ORDER_CODE");
			set.add(containerId+";"+containerDesc+";"+orderCode);
		}
		TParm parmM = new TParm() ;
		int row = 0 ;
		Iterator<String> it = set.iterator() ;
		while (it.hasNext()) {
			String containerIds = it.next() ;
			String [] id = containerIds.split(";");
			parmM.setData("DISPENSE_NO",row,dispenseNo);
			parmM.setData("DISPENSE_SEQ_NO",row,1);
			parmM.setData("CONTAINER_ID",row,id[0]);
			parmM.setData("CONTAINER_DESC",row,id[1]);
			parmM.setData("ORDER_CODE",row,id[2]);
			parmM.setData("IS_BOXED",row,"Y");
			parmM.setData("BOXED_USER",row,Operator.getID());
			parmM.setData("BOXED_DATE",row,"");
			parmM.setData("CABINET_ID",row,cabinetId);
			parmM.setData("OPT_USER",row,userId);
			parmM.setData("OPT_TERM",row,Operator.getIP());	
			parmM.setData("APP_ORG_CODE",row,orgCode);
			row++;
		}
		if(parmM.getCount("DISPENSE_NO") > 0){
			parm.setData("OUT_M", parmM.getData());
		}
		
		parm = TIOM_AppServer.executeAction("action.spc.SPCOutStoreAction",
                  "onSaveOperationRoom", parm);

        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return ;
        }
        this.messageBox("P0001");
        onPrintSave(dispenseNo) ;
        
        onClear() ;
	}
	
	 /**
     * 打印出库单
     */
    public void onPrintSave(String dispenseNo) {
        Timestamp datetime = SystemTool.getInstance().getDate();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒

        TTable table_d = getTable("TABLE");
        if(dispenseNo == null && dispenseNo.equals("")){
        	this.messageBox("没有打印数据!");
        	return ;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
           /* date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "出库单");*/
            date.setData("TITLE", "TEXT","手术室麻精出库单");
            
            date.setData("DISPENSE_NO", "TEXT",
                           dispenseNo);
             
            date.setData("DISPENSE_DATE", "TEXT",
                           df.format(datetime));
             
            date.setData("DATE", "TEXT",			   
                         "制表日期: " +
                         df.format(datetime));

            // 表格数据			
            TParm parm = new TParm();
           
			TParm tableParm = table_d.getParmValue();
            for (int i = 0; i < tableParm.getCount("TOXIC_ID"); i++) {
            	TParm rowParm = (TParm)tableParm.getRow(i);
            	parm.addData("TOXIC_ID",rowParm.getValue("TOXIC_ID"));
            	parm.addData("ORDER_CODE",table.getItemData(i,"ORDER_CODE"));
            	
            	parm.addData("ORDER_DESC",resultParm.getValue("ORDER_DESC",i));
            	parm.addData("SPECIFICATION",table.getItemData(i,"SPECIFICATION"));
    			
            	
            	parm.addData("BATCH_NO",table.getItemData(i,"BATCH_NO"));
            	Timestamp validDate = resultParm.getTimestamp("VALID_DATE",i);
            	String valdate = "";
            	if(validDate != null ){
            		valdate = df.format(validDate).substring(0, 10).replace('-', '/');
            	}
            	parm.addData("VALID_DATE",valdate );
            
            	parm.addData("CONTAINER_DESC",resultParm.getValue("CONTAINER_DESC",i));
    			 
                 
            }
 
            if (parm.getCount("TOXIC_ID") <= 0) {
                this.messageBox("没有打印数据");
                return;
            }
          
            parm.setCount(parm.getCount("TOXIC_ID"));
            parm.addData("SYSTEM", "COLUMNS", "TOXIC_ID");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
            parm.addData("SYSTEM", "COLUMNS", "CONTAINER_DESC");
           
            															
            date.setData("TABLE", parm.getData());

             
            date.setData("BAR_CODE", "TEXT",dispenseNo);
            
            date.setData("DISPENSE_USER", "TEXT", Operator.getName());
            date.setData("BAR_CODE", "TEXT", dispenseNo);
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\OperatorRoomOut.jhw", date);
        }
        else {
            this.messageBox("没有打印数据");
            return;
        }
    }
    
	
	 /**
     * 打印出库单
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒

        TTable table_d = getTable("TABLE");
        dispenseNo  = getValueString("DISPENSE_NO");
        if(dispenseNo == null && dispenseNo.equals("")){
        	this.messageBox("没有打印数据!");
        	return ;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
           /* date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "出库单");*/
            date.setData("TITLE", "TEXT","手术室麻精出库单");
            
            date.setData("DISPENSE_NO", "TEXT",
                           dispenseNo);
             
            date.setData("DISPENSE_DATE", "TEXT",
                           df.format(datetime));
             
            date.setData("DATE", "TEXT",			   
                         "制表日期: " +
                         df.format(datetime));

            // 表格数据			
            TParm parm = new TParm();
           
			TParm tableParm = table_d.getParmValue();
            for (int i = 0; i < tableParm.getCount("TOXIC_ID"); i++) {
            	TParm rowParm = (TParm)tableParm.getRow(i);
            	parm.addData("TOXIC_ID",rowParm.getValue("TOXIC_ID"));
            	parm.addData("ORDER_CODE",table.getItemData(i,"ORDER_CODE"));
            	
            	parm.addData("ORDER_DESC",resultParm.getValue("ORDER_DESC",i));
            	parm.addData("SPECIFICATION",table.getItemData(i,"SPECIFICATION"));
    			
            	
            	parm.addData("BATCH_NO",table.getItemData(i,"BATCH_NO"));
            	Timestamp validDate = resultParm.getTimestamp("VALID_DATE",i);
            	String valdate = "";
            	if(validDate != null ){
            		valdate = df.format(validDate).substring(0, 10).replace('-', '/');
            	}
            	parm.addData("VALID_DATE",valdate );
            
            	parm.addData("CONTAINER_DESC",resultParm.getValue("CONTAINER_DESC",i));
    			 
                 
            }
 
            if (parm.getCount("TOXIC_ID") <= 0) {
                this.messageBox("没有打印数据");
                return;
            }
          
            parm.setCount(parm.getCount("TOXIC_ID"));
            parm.addData("SYSTEM", "COLUMNS", "TOXIC_ID");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
            parm.addData("SYSTEM", "COLUMNS", "CONTAINER_DESC");
           
            															
            date.setData("TABLE", parm.getData());

             
            date.setData("BAR_CODE", "TEXT",dispenseNo);
            
            date.setData("DISPENSE_USER", "TEXT", Operator.getName());
            date.setData("BAR_CODE", "TEXT", dispenseNo);
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\OperatorRoomOut.jhw", date);
        }
        else {
            this.messageBox("没有打印数据");
            return;
        }
    }
    
    public void openSearch(){
    	
    	TParm parm = new TParm();
    	parm.setData("CABINET_ID",getValueString("CABINET_ID"));
    	Object result = openDialog("%ROOT%\\config\\spc\\SPCContainerStatic.x", parm);
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
	
	
	public void onClear() {
		String controlName = "TOXIC_ID;DISPENSE_NO;";
		this.clearValue(controlName);
		getRadioButton("UPDATE_N").setSelected(true);
		table.setParmValue(new TParm());
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
