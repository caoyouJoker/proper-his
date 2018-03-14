package com.javahis.ui.sys;

import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * 终端设置打印机
 * @author lixiang
 *
 */
public class SYSPrintConfigControl extends TControl{
	/**
	 * 终端打打印机列表
	 */
	private TComboBox printlist;
	/**
	 * 报告列表;
	 */
	private TComboBox reportlist;
	
	
	private TTable printerTable;
	/**
	 * 终端IP
	 */
	private TTextField termIP;
	/**
	 * 
	 */
	int selectRow = -1;
	
	//行记录;
	private TParm data;

    public SYSPrintConfigControl() {
    }
    /**
     * 初始化
     */
    public void onInit() {
        //
        printlist = (TComboBox) getComponent("PRINT_LIST");
        //REPORT_LIST
        reportlist= (TComboBox) getComponent("REPORT_LIST");
        //
        termIP=(TTextField) getComponent("TERM_IP");
        //
        printerTable=(TTable) getComponent("PRINTER_TABLE");
        
        //终端IP (有装虚拟机可能)
        termIP.setValue(Operator.getIP());
        
        initPrintList();
      
        initTable();
        
        callFunction("UI|PRINTER_TABLE|addEventListener", "PRINTER_TABLE->"
                + TTableEvent.CLICKED, this, "onTABLEClicked");
        
    }
    /**
     * 
     * @param row
     */
    public void onTABLEClicked(int row) {
    	//this.messageBox("rowrow==="+row);
    	
    	if (row < 0) {
            return;
        }
    	TParm data = (TParm) callFunction("UI|PRINTER_TABLE|getParmValue");
    	//this.messageBox("==data=="+data);
    	//赋值;
    	termIP.setValue(data.getValue("PRINTER_IP", row));
    	reportlist.setValue(data.getValue("REPORT_ID", row));
    	printlist.setValue(data.getValue("PRINTER_CHN_DESC", row));
    	selectRow = row;
    	/**setValueForParm(
                "SEQ;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG",
                data, row);**/

        
    	
    }
    
    
    /**
     * 初始化报告列表
     */

    private void initTable(){
    	String sql="SELECT a.PRINTER_IP,a.PRINTER_CHN_DESC,b.REPORT_CHN_DESC,a.OPT_USER,a.OPT_DATE,a.OPT_TERM,a.REPORT_ID";
    	sql+=" FROM SYS_PRINTER_LIST a,SYS_PRINTER_REPORT b";
    	sql+=" WHERE a.REPORT_ID(+)=b.REPORT_ID";
    	sql+=" AND a.PRINTER_IP='"+termIP.getValue().trim()+"'";
    	//System.out.println("--------------------SQL===="+sql);
    	TParm result = new TParm( this.getDBTool().select(sql));
    	//this.messageBox("result"+result.getCount());
    	printerTable.setParmValue(result);
    	selectRow=-1;
    	
    }

    /**
     * 初始化打印机列表
     */
    private void initPrintList() {
        List list = new ArrayList();
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (int i = 0; i < services.length; i++) {
            list.add(services[i].getName().trim());
        }
        String s[] = (String[]) list.toArray(new String[] {});
        printlist.setData(s);
        PrintService ps = PrinterJob.getPrinterJob().getPrintService();

        String defaultPrint = ps.getName();
        printlist.setSelectedID(defaultPrint);
    }
    
    /**
     * 保存
     */
    public void onSave(){
    	//校验检查;
    	if(termIP.getValue().trim().equals("")){
    		this.messageBox("请填写终端机IP！");
    		return;
    	}
    	
    	if(reportlist.getSelectedID().equals("")){
    		this.messageBox("请选择报告！");
    		return;	
    	}
    	
    	if(printlist.getSelectedID().equals("")){
    		this.messageBox("请选择打印机！");
    		return;	
    	}
   	
    	if(selectRow==-1){
    		//this.messageBox("新增");
    		onInsert();
    	}else{
    		//this.messageBox("更新");
    		onUpdate();
    	}
    }
    
    /**
     * 新增
     */
    private void onInsert(){
    	//判断是否有相同的设置了
    	//有，报错
    	 String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd hh:mm:ss");
      	//是否存在
    	 
    	String sql="INSERT INTO SYS_PRINTER_LIST (PRINTER_IP,PRINTER_CHN_DESC,REPORT_ID,OPT_USER,OPT_DATE,OPT_TERM)";
    	sql+=" VALUES(";
    	sql+="'"+termIP.getValue().trim()+"',";
    	sql+="'"+printlist.getSelectedID()+"',";
    	sql+="'"+reportlist.getSelectedID()+"',";
    	sql+="'"+Operator.getID()+"',";
    	sql += "TO_DATE('" + sysDate +
        "','YYYY/MM/DD HH24:MI:SS') ,";
    	sql+="'"+Operator.getIP()+"'";
    	sql+=")";
    	    	
    	TParm parm = new TParm(this.getDBTool().update(sql));
    	//parm.getErrText()
    	if (parm.getErrCode() < 0) {
            this.messageBox("保存失败！");
            return;
            
    	}else{
    		this.messageBox("保存成功！");
    		initTable();
    		onClear();
    		
    	}  	
    	
    }
    
    /**
     * 更新
     */
    private void onUpdate(){
    	int row=selectRow;
    	//this.messageBox("==row=="+row);
    	TParm data = (TParm) callFunction("UI|PRINTER_TABLE|getParmValue");
    	//表格选中值为当前值;
    	String cTermIP=data.getValue("PRINTER_IP", row);
    	String creportID=data.getValue("REPORT_ID", row);
    	String printerChnDesc=data.getValue("PRINTER_CHN_DESC", row);   	
    	String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyy/MM/dd hh:mm:ss");
    	
    	String sql="UPDATE SYS_PRINTER_LIST SET PRINTER_IP='"+termIP.getValue().trim()+"'," ;
    		   sql+="PRINTER_CHN_DESC='"+printlist.getSelectedID()+"',";
    		   sql+="REPORT_ID='"+reportlist.getSelectedID()+"',";
    		   sql+="OPT_DATE=TO_DATE('" + sysDate +
    			        "','YYYY/MM/DD HH24:MI:SS')";
    		   sql+=" WHERE PRINTER_IP='"+cTermIP+"'";
    		   sql+=" AND REPORT_ID='"+creportID+"'";
    		   sql+=" AND PRINTER_CHN_DESC='"+printerChnDesc+"'";
    		   //System.out.println("===update sql==="+sql);
    		   
    	TParm parm = new TParm(this.getDBTool().update(sql));
    	if (parm.getErrCode() < 0) {
            this.messageBox("修改失败！");
            return;
            
    	}else{
    		this.messageBox("修改成功！");
    		initTable();
    		onClear();
    	}
    	
    	
    	
    }
    
    /**
     * 删除记录
     */
    public void onDelete(){
    	
    	 if(selectRow<0){
    		 this.messageBox("请先选择记录?");
    		 return;
    	 }
    	 
    	 TParm data = (TParm) callFunction("UI|PRINTER_TABLE|getParmValue");
    	 
    	//this.messageBox("selectRow==="+selectRow);
     	//表格选中值为当前值;
     	String cTermIP=data.getValue("PRINTER_IP", selectRow);
     	String creportID=data.getValue("REPORT_ID", selectRow);
     	String printerChnDesc=data.getValue("PRINTER_CHN_DESC", selectRow);
    	
    	 if (this.messageBox("提示信息", "确定删除?", 2) == 0) {
    		 String sql="DELETE FROM SYS_PRINTER_LIST";
    		 sql+=" WHERE PRINTER_IP='"+cTermIP+"'";
  		     sql+=" AND REPORT_ID='"+creportID+"'";
  		     sql+=" AND PRINTER_CHN_DESC='"+printerChnDesc+"'";
  		   
    		 TParm parm = new TParm(this.getDBTool().update(sql));
    		 if (parm.getErrCode() < 0) {
    	            this.messageBox("删除失败！");
    	            return;
    	            
    	     }else{
    	    		this.messageBox("删除成功！");
    	    		initTable();
    	    		onClear();
    	     }
    	 }   	
    }
    /**
     * 清空
     */
    public void onClear(){
    	//initPrintList();
    	reportlist.setSelectedID("");
    	initTable();
    	
    }
    
    /**
     * 查询操作
     */
    public void onQuery(){
    	initTable();
    }
    
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
}
