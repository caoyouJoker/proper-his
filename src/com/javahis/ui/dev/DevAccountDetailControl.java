package com.javahis.ui.dev;

import java.util.Date;

import jdo.sys.Operator;
 
import action.sys.SYSPopedomAction;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
 

/**                                    
 * <p>Title: 设备台账</p>
 * 
 * <p>Description:设备台账</p>
 * 
 * <p>Copyright: Copyright (c) 20140621</p>
 * 
 * <p>Company: ProperSoft </p>
 *  
 * @author  fux
 * 
 * @version 4.0
 */     

public class DevAccountDetailControl extends TControl{

	private TTable mainTable;
    /**
     * 初始化  
     */      
	public void onInit(){   
		super.init(); 
		mainTable =getTable("TABLE"); 
        callFunction("UI|DEV_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");  
        //textfield接受回传值
        callFunction("UI|DEV_CODE|addEventListener",  
        TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
            
//        // 套餐细相值改变事件
//        mainTable.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this,
//                                     "onTableChangeValue");
	}
	 
	/**
     * 编码接受返回值方法
     * @param tag String
     * @param obj Object 
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String dev_code = parm.getValue("DEV_CODE");
        if (!dev_code.equals("")) {
            getTextField("DEV_CODE").setValue(dev_code);
        }
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!dev_desc.equals("")) {
            getTextField("DEV_CHN_DESC").setValue(dev_desc);
        } 
    } 
    /**
     * 得到父节点编码
     * @param code String
     * @param parm TParm 
     * @return String
     */
    private String getParentCode(String code,TParm parm){
        int classify1 = parm.getInt("CLASSIFY1",0);
        int classify2 = parm.getInt("CLASSIFY2",0);
        int classify3 = parm.getInt("CLASSIFY3",0);
        int classify4 = parm.getInt("CLASSIFY4",0);
        int classify5 = parm.getInt("CLASSIFY5",0);
        int serialNumber = parm.getInt("SERIAL_NUMBER",0);
        if(code.length() == classify1)
            return "";
        if(code.length() == classify1 + classify2)
            return code.substring(0,classify1);
        if(code.length() == classify1 + classify2 + classify3)
            return code.substring(0,classify1 + classify2);
        if(code.length() == classify1 + classify2 + classify3 + classify4)
            return code.substring(0,classify1 + classify2 + classify3);
        if(code.length() == classify1 + classify2 + classify3 + classify4 + classify5)
            return code.substring(0,classify1 + classify2 + classify3 + classify4);
        if(code.length() == classify1 + classify2 + classify3 + classify4 + classify5 + serialNumber)
            return code.substring(0,classify1 + classify2 + classify3 + classify4 + classify5);
        return "";
    }
    
    
    /**
     * 查询 
     */   
	public void onQuery(){
		String con = ""; 
	   
        String devkindCode = this.getValueString("DEVKIND_CODE");
      
        String devproCode = this.getValueString("DEVPRO_CODE");
      
        String devClass = this.getValueString("DEV_CLASS");
      
        String deptCode = this.getValueString("DEPT_CODE");
     
        String locCode = this.getValueString("LOC_CODE"); 
       
        String devCode = this.getValueString("DEV_CODE");
                             
        
  	    //校验购入价和现值
  	    String MONEY_PUR = "";  
  	    String MONEY_SCR = "";
        int purl = this.getValueInt("PUR_LOW");
        int purh = this.getValueInt("PUR_HIGH");
        int scpl = this.getValueInt("SCR_LOW");
        int scph = this.getValueInt("SCR_HIGH");
  	    //购入价
        if(purl>purh){
           this.messageBox("查询条件:购入价不允许下限大于上限");	
           return;
        } 
    	//现值  
        if(scpl>scph){
           this.messageBox("查询条件:现值不允许下限大于上限");	
           return;
        } 
        if(purh!=0){
        MONEY_PUR = MONEY_PUR +" AND  B.UNIT_PRICE  between '" + purl + "' and '" + purh + "' ";
        }
        if(scph!=0){
        MONEY_SCR = MONEY_SCR +" AND  B.SCRAP_VALUE between '" + scpl + "' and '" + scph + "' ";
        }
		 if (!devkindCode.equals("")) {
	            con  = con + " AND C.DEVKIND_CODE ='" + devkindCode + "'";
	        } 
		 if (!devproCode.equals("")) {
	            con  = con + " AND C.DEVPRO_CODE ='" + devproCode + "'";
	        }
		 if (!devClass.equals("")) {
	            con  = con + " AND C.DEV_CLASS='" + devClass + "'";
	        } 
		 if (!deptCode.equals("")) {
	            con  = con + " AND B.DEPT_CODE ='" + deptCode + "'";
	        }
		 if (!locCode.equals("")) { 
	            con  = con + " AND B.LOC_CODE ='" + locCode + "'";
	        }  
		 if (!devCode.equals("")) { 
	            con  = con + " AND B.DEV_CODE ='" + devCode + "'";
 	        }    
		 String sql =  
			 " SELECT 'Y' AS EXEC,B.DEV_CODE,C.DEV_CHN_DESC,B.DEV_CODE_DETAIL,B.DEVSEQ_NO,B.DEPT_CODE,B.STOCK_UNIT,"+
		     "  B.INWAREHOUSE_DATE,B.WAIT_ORG_CODE,B.CARE_USER,B.USE_USER,B.LOC_CODE,"+
		     "  B.RFID,B.BARCODE,B.ACTIVE_FLG,B.SETDEV_CODE,B.UNIT_PRICE,B.MDEP_PRICE,"+
		     "  B.DEP_PRICE,B.CURR_PRICE,B.MAN_CODE,B.SPECIFICATION,B.MAN_NATION,"+
		     "  B.MODEL,B.BRAND,B.SERIAL_NUM,B.IP,B.TERM,"+
		     "  B.WIRELESS_IP,B.CHECKTOLOSE_FLG,B.WAST_FLG"+
		     "  FROM DEV_STOCKDD B, DEV_BASE C"+  
		     "  WHERE B.DEV_CODE = C.DEV_CODE"+ 
               con +             
               MONEY_PUR +             
               MONEY_SCR +            
               " ORDER BY B.DEV_CODE";  
//		 String sql =    
//			 " SELECT * FROM DEV_STOCKDD ";   
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));     
		//System.out.println("result:"+result);
		if(result.getCount()<=0){ 
			this.messageBox("查无数据！");    
			return;      
		}
		if(result.getErrCode()>0){
			this.messageBox("查询有误,请确认！");
			return;
		}
        this.callFunction("UI|TABLE|setParmValue", result); 
	}
	
	
	/**
     * 保存
     */   
	public void onSave(){
    	TParm mainUpdateParm = new TParm();//分类主表   
    	TParm mainUpdateData = new TParm();
		  
		TParm mainParm = getTable("TABLE").getParmValue();
		for (int m = 0; m < mainParm.getCount(); m++){       
			String exec = mainParm.getValue("EXEC", m);  
			if("U".equals(exec)){//1：修改数据-分类主表
				mainUpdateParm.addData("DEV_CODE_DETAIL", mainParm.getValue("DEV_CODE_DETAIL",m));
				mainUpdateParm.addData("SPECIFICATION",  mainParm.getValue("SPECIFICATION",m));
				mainUpdateParm.addData("MODEL", mainParm.getValue("MODEL",m));
				mainUpdateParm.addData("BRAND", mainParm.getValue("BRAND",m)); 
                //修改项  
				mainUpdateParm.addData("OPT_USER", Operator.getID());
				mainUpdateParm.addData("OPT_TERM", Operator.getIP());
			} 
		}                 
		//循环遍历分类主表修改数据
		for (int k = 0; k < mainUpdateParm.getCount("DEV_CODE_DETAIL"); k++) {
			mainUpdateData.addRowData(mainUpdateParm, k);    
		}
    	TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onUpdate", mainUpdateData);
    	
		if(result.getErrCode()<0){        
			this.messageBox("保存失败！");
			return;  
		}
	}
	
    /**
     * 值改变事件
     */
    public void onTableChangeValue(){     
    	int selectedIndx=mainTable.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//修改老数据  
		TParm mainParm=mainTable.getParmValue();   
		String exec = mainParm.getValue("EXEC",mainTable.getSelectedRow());
		
		if("Y".equals(exec)){          
			mainParm.setData("EXEC", mainTable.getSelectedRow(), "U");
			mainTable.setParmValue(mainParm);  
		}   
		
    }
	
    /**
     * 导出Excel 
     */
    public void onExport() {
        if (this.getTable("TABLE").getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
                    "设备财产明细统计报表");
        } 
    }
 
    
    /** 
     * 清空数据<br> 
     *        
     */ 
	public void onClear(){
        if (this.getTable("TABLE").getRowCount() > 0) {
            callFunction("UI|TABLE|removeRowAll");
        }

        this.clearValue( 
                "DEVKIND_CODE;DEVPRO_CODE;DEV_CLASS;DEPT_CODE;LOC_CODE;" +
                "DEV_CODE;DEV_CHN_DESC");
        callFunction("UI|TABLE|removeRowAll");
		  
	}
	
    /**
     * 得到表控件
     * @param tagName String
     * @return TTable 
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
  
    /** 
     * 得到选框
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
    
    
    /**
     * 得到文本控件
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
 