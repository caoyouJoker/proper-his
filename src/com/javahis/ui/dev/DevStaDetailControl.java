package com.javahis.ui.dev;

import java.util.Date;

import jdo.sys.Operator;
 
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
 

/**                                    
 * <p>Title: 财产明细统计</p>
 * 
 * <p>Description:财产明细统计</p>
 * 
 * <p>Copyright: Copyright (c) 20130721</p>
 * 
 * <p>Company: ProperSoft </p>
 *  
 * @author  fux
 * 
 * @version 4.0
 */   

public class DevStaDetailControl extends TControl{
    /**
     * 初始化  
     */      
	public void onInit(){   
		super.init(); 
        callFunction("UI|DEV_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");
        //textfield接受回传值
        callFunction("UI|DEV_CODE|addEventListener",
        TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
//		String sql = " SELECT B.DEPT_CODE, C.DEVKIND_CODE, C.DEVTYPE_CODE, C.DEVPRO_CODE,C.DEV_CLASS , B.BATCH_SEQ, "+  
//               " B.DEV_CODE, C.DEV_CHN_DESC, C.DESCRIPTION, B.QTY,  C.UNIT_CODE, "+ 
//               " C.MAN_CODE, B.UNIT_PRICE, B.SCRAP_VALUE, TO_CHAR(B.GUAREP_DATE, 'YYYYMMDD') GUAREP_DATE, TO_CHAR(B.DEP_DATE, 'YYYYMMDD') DEP_DATE,"+  
//               " TO_CHAR(B.MAN_DATE, 'YYYYMMDD') MAN_DATE, B.MANSEQ_NO, B.CARE_USER, B.USE_USER, B.LOC_CODE,B.SPECIFICATION, B.QTY*B.UNIT_PRICE AS TOT_VALUE "+  
//               " FROM DEV_STOCKD B, DEV_BASE C "+ 
//               " WHERE  B.DEV_CODE=C.DEV_CODE " +   
		  
//		 SELECT  B.DEV_CODE,
//		  B.DEV_CODE_DETAIL,
//		  B.DEVSEQ_NO,
//		  B.REGION_CODE,
//		  B.DEPT_CODE,
//		  B.STOCK_QTY,
//		  B.STOCK_UNIT,
//		  B.CHECKTOLOSE_FLG,
//		  B.WAST_FLG,
//		  B.INWAREHOUSE_DATE,
//		  B.WAIT_ORG_CODE,
//		  B.CARE_USER,
//		  B.USE_USER,
//		  B.LOC_CODE,
//		  B.RFID,
//		  B.BARCODE,
//		  B.ACTIVE_FLG,
//		  B.SETDEV_CODE,
//		  B.UNIT_PRICE,
//		  B.MDEP_PRICE,
//		  B.DEP_PRICE,
//		  B.CURR_PRICE,
//		  B.MODEL,
//		  B.BRAND,
//		  B.SPECIFICATION,
//		  B.SERIAL_NUM,
//		  B.MAN_CODE,
//		  B.IP,
//		  B.TERM,
//		  B.MAN_NATION,
//		  B.WIRELESS_IP
//		  --,TO_CHAR(B.MAN_DATE, 'YYYYMMDD') MAN_DATE  
//		  FROM DEV_STOCKDD B, DEV_BASE C   
//		  --,DEV_STOCKD A
//		   WHERE  B.DEV_CODE=C.DEV_CODE   
		 
		    //去掉B.BATCH_SEQ
       		String sql = " SELECT B.DEPT_CODE, C.DEVKIND_CODE, C.DEVTYPE_CODE, C.DEVPRO_CODE,C.DEV_CLASS , "+  
            " B.DEV_CODE, C.DEV_CHN_DESC, C.DESCRIPTION, B.QTY,  C.UNIT_CODE, "+ 
            " C.MAN_CODE, B.UNIT_PRICE, B.SCRAP_VALUE, TO_CHAR(B.GUAREP_DATE, 'YYYYMMDD') GUAREP_DATE, TO_CHAR(B.DEP_DATE, 'YYYYMMDD') DEP_DATE,"+  
            " TO_CHAR(B.MAN_DATE, 'YYYYMMDD') MAN_DATE, B.MANSEQ_NO, B.CARE_USER, B.USE_USER, B.LOC_CODE,B.SPECIFICATION, B.QTY*B.UNIT_PRICE AS TOT_VALUE "+  
            " FROM DEV_STOCKD B, DEV_BASE C "+   
            " WHERE  B.DEV_CODE=C.DEV_CODE " +     
               con +        
               MONEY_PUR +       
               MONEY_SCR +    
               " ORDER BY B.DEV_CODE";  
		System.out.println("sql"+sql); 
		TParm result = new TParm(TJDODBTool.getInstance().select(sql)); 
		if(result.getCount()<=0){
			this.messageBox("查无数据！");
			return;  
		}
		if(result.getErrCode()>0){
			this.messageBox("查询有误,请确认！");
			return;
		}
		System.out.println("sql"+sql); 
        this.callFunction("UI|TABLE|setParmValue", result); 
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
     * 打印
     */
    public void onPrint() {
        if (this.getTable("TABLE").getRowCount() <= 0) { 
            this.messageBox("没有要打印的数据");
            return;
        } 
        TParm prtParm = new TParm();
        //表头
        prtParm.setData("TITLE","TEXT","财产明细统计报表");
        //日期 
        prtParm.setData("PRINT_DATE","TEXT","打印日期：" +
                        StringTool.getString(StringTool.getTimestamp(new Date()),
                                             "yyyy年MM月dd日"));
        TParm parm = this.getTable("TABLE").getParmValue();
        TParm prtTableParm=new TParm();  
        for(int i=0;i<parm.getCount("DEV_CODE");i++){
            prtTableParm.setData("DEPT_CODE",i,parm.getRow(i).getValue("DEPT_CODE"));
            prtTableParm.setData("DEV_CLASS",i,parm.getRow(i).getValue("DEV_CLASS"));
            prtTableParm.setData("DEVKIND_CODE",i,parm.getRow(i).getValue("DEVKIND_CODE"));
            prtTableParm.setData("DEVPRO_CODE",i,parm.getRow(i).getValue("DEVPRO_CODE"));
            prtTableParm.setData("BATCH_SEQ",i,parm.getRow(i).getValue("BATCH_SEQ"));
            prtTableParm.setData("DEV_CODE",i,parm.getRow(i).getValue("DEV_CODE"));
            prtTableParm.setData("SEQ_NO",i,parm.getRow(i).getValue("SEQ_NO")); 
            prtTableParm.setData("DEV_CHN_DESC",i,parm.getRow(i).getValue("DEV_CHN_DESC"));
            prtTableParm.setData("SPECIFICATION",i,parm.getRow(i).getValue("SPECIFICATION"));
            prtTableParm.setData("QTY",i,parm.getRow(i).getValue("QTY"));
            prtTableParm.setData("UNIT_CODE",i,parm.getRow(i).getValue("UNIT_CODE"));
            prtTableParm.setData("MAN_CODE",i,parm.getRow(i).getValue("MAN_CODE")); 
            prtTableParm.setData("TOT_VALUE",i,parm.getRow(i).getValue("TOT_VALUE"));
            prtTableParm.setData("SCRAP_VALUE",i,parm.getRow(i).getValue("SCRAP_VALUE"));
            prtTableParm.setData("GUAREP_DATE",i,parm.getRow(i).getValue("GUAREP_DATE"));
            prtTableParm.setData("DEP_DATE",i,parm.getRow(i).getValue("DEP_DATE"));
            prtTableParm.setData("MAN_DATE",i,parm.getRow(i).getValue("MAN_DATE"));
            prtTableParm.setData("MANSEQ_NO",i,parm.getRow(i).getValue("MANSEQ_NO"));
            prtTableParm.setData("CARE_USER",i,parm.getRow(i).getValue("CARE_USER"));
            prtTableParm.setData("USE_USER",i,parm.getRow(i).getValue("USE_USER"));
            prtTableParm.setData("LOC_CODE",i,parm.getRow(i).getValue("LOC_CODE"));
        }           
        //DEPT_CODE;DEV_CLASS;DEVKIND_CODE;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;SEQ_NO;DEV_CHN_DESC;SPECIFICATION;QTY;
        //UNIT_CODE;MAN_CODE;TOT_VALUE;SCRAP_VALUE;GUAREP_DATE;DEP_DATE;MAN_DATE;MANSEQ_NO;CARE_USER;USE_USER;LOC_CODE
        prtTableParm.setCount(prtTableParm.getCount("DEV_CHN_DESC"));
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEV_CLASS");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEVKIND_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "BATCH_SEQ"); 
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SEQ_NO");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        prtTableParm.addData("SYSTEM", "COLUMNS", "QTY");
        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "GUAREP_DATE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEP_DATE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "MANSEQ_NO");
        prtTableParm.addData("SYSTEM", "COLUMNS", "CARE_USER");
        prtTableParm.addData("SYSTEM", "COLUMNS", "USE_USER"); 
        prtTableParm.addData("SYSTEM", "COLUMNS", "LOC_CODE");
        prtParm.setData("TABLE", prtTableParm.getData());
        //表尾 
        prtParm.setData("USER","TEXT", "制表人：" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEVStaDetail.jhw", 
                             prtParm); 
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
 