package com.javahis.ui.spc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCOperationRoomOutTool;
import jdo.spc.SPCToxicTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class SPCOperationRoomOutQueryControl extends TControl {

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

	 
	
	String dispenseNo = "";
	
	String requestNo ;
	
	TParm resultParm = new TParm();
	
	
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
        parm.setData("ORG_CODE",orgCode);
         
        /**
        result = SPCOperationRoomOutTool.getInstance().onQueryList(parm);	
		if(result.getCount() < 0 ){
			table_List.setParmValue(new TParm());
			this.messageBox("没有查询到数据");
			return ;
		}
		//如果不为空，则取病区
		//String stationCode = result.getValue("STATION_CODE",0);
		//setValue("STATION_CODE", stationCode);
		table_List.setParmValue(result);
		*/
		
	}
	
	public void onQuery(){
		
		table_List = getTable("TABLE_LIST");
		dispenseNo = getValueString("DISPENSE_NO");
		TParm parm = new TParm();
		parm.setData("REQUEST_NO",requestNo);
		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
		parm.setData("START_DATE", startDate);
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        parm.setData("END_DATE", endDate);
         
        parm.setData("ORG_CODE",orgCode);
		TParm result = SPCOperationRoomOutTool.getInstance().onQueryList(parm);
		
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
		
		
	}
	
	
	public void onQueryListClicked(){
		table_List = getTable("TABLE_LIST");
		int row = table_List.getSelectedRow() ;
		dispenseNo = table_List.getItemString(row,  "DISPENSE_NO");
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO",dispenseNo);
		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
		parm.setData("START_DATE", startDate);
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0,19).replaceAll("-", "").replaceAll(":","").replaceAll(" ", "");
        parm.setData("END_DATE", endDate);
         
        parm.setData("ORG_CODE",orgCode);
		TParm result = SPCOperationRoomOutTool.getInstance().onQuery(parm);
		if(result.getCount() >  0){
			table_D.setParmValue(result);
			resultParm = result ;
			setValue("DISPENSE_NO", dispenseNo);
		}else{
			this.messageBox("没有数据");
			setValue("DISPENSE_NO", "");
		}
	}
	
	public void onTableDClicked(){
		int rowSelect = table_D.getSelectedRow();
		dispenseNo = getValueString( "DISPENSE_NO");
		int seqNo = resultParm.getInt( "SEQ_NO",rowSelect);
		String orderCode = resultParm.getValue( "ORDER_CODE",rowSelect);
		 
		TParm searchParm = new TParm();
		searchParm.setData("DISPENSE_NO",dispenseNo);
		searchParm.setData("SEQ_NO",seqNo);
		searchParm.setData("ORDER_CODE",orderCode);
		 
	    TParm result = SPCToxicTool.getInstance().onQuery(searchParm);
	    if(result.getCount() >  0) {
	    	table_M.setParmValue(result);
	    }else{
	    	this.messageBox("没有查询到数据");
	    }
		
		
	}
	
	 /**
     * 打印出库单
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒

        TTable table_d = getTable("TABLE_D");
        dispenseNo  = getValueString("DISPENSE_NO");
        if(dispenseNo == null && dispenseNo.equals("")){
        	this.messageBox("没有打印数据!");
        	return ;
        }
        if (table_d.getRowCount() > 0) {
        	table_List = getTable("TABLE_LIST");
        	int rowSelect = table_List.getSelectedRow() ;
        	
        	Timestamp optDate = table_List.getParmValue().getTimestamp("OPT_DATE",rowSelect);
        	if(optDate == null || optDate.equals("")){
        		optDate =datetime;
        	}
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
                           df.format(optDate));
             
            date.setData("DATE", "TEXT",			   
                         "制表日期: " +
                         df.format(optDate));

            // 表格数据			
            TParm parm = new TParm();
            TParm searchParm = new TParm();
            searchParm.setData("DISPENSE_NO",dispenseNo);
			TParm tableParm = SPCToxicTool.getInstance().onQueryBy(searchParm);
            for (int i = 0; i < tableParm.getCount("TOXIC_ID"); i++) {
            	TParm rowParm = (TParm)tableParm.getRow(i);
            	parm.addData("TOXIC_ID",rowParm.getValue("TOXIC_ID"));
            	parm.addData("ORDER_CODE",rowParm.getValue("ORDER_CODE"));
            	
            	parm.addData("ORDER_DESC",rowParm.getValue("ORDER_DESC"));
            	parm.addData("SPECIFICATION",rowParm.getValue("SPECIFICATION"));
    			
            	
            	parm.addData("BATCH_NO",rowParm.getValue("BATCH_NO"));
            	Timestamp validDate = rowParm.getTimestamp("VALID_DATE");
            	String valdate = "";
            	if(validDate != null ){
            		valdate = df.format(validDate).substring(0, 10).replace('-', '/');
            	}
            	parm.addData("VALID_DATE",valdate );
            
            	parm.addData("CONTAINER_DESC",rowParm.getValue("CONTAINER_DESC"));
    			 
                 
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
    
	
	public void onClear(){
		setValue("DISPENSE_NO", "");
		table_List.setParmValue(new TParm());
		table_D.setParmValue(new TParm());
		table_M.setParmValue(new TParm());
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
