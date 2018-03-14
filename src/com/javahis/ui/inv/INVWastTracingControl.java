package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
  
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;  
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/** 
 * 
 * 
 * 
 * <p>
 *   
 * Title: 未使用高值溯源 
 * </p>
 * 
 * <p>
 * Description:未使用高值溯源
 * </p> 
 *  
 * <p>
 * Copyright: Copyright (c) BLUECORE 2013
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 *  
 * @author fux 2013.11.08 
 * @version 1.0
 */
public class INVWastTracingControl extends TControl{
	/**
	 * 初始化方法
	 */  
	public void onInit(){
        //初始化用户  
        String deptCode = Operator.getDept();   
        this.setValue("DEPT_CODE", deptCode);
        callFunction("UI|INV_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\inv\\INVBasePopup.x");
        //textfield接受回传值   
        callFunction("UI|INV_CODE|addEventListener", 
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
//		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
//                 getDate(), -1); 
//		 Timestamp tommorw = StringTool.rollDate(SystemTool.getInstance().
//                 getDate(), +1);  
//		 setValue("BIL_DATES", yesterday.toString().substring(0,10)+"00:00:00");
//		 setValue("BIL_DATEE", SystemTool.getInstance().getDate().toString().substring(0,10)+"23:59:59");
		 setValue("VAILD_TIMES", "");
		 setValue("VAILD_TIMEE", "");
		 setValue("DISPENSE_TIMES", ""); 
		 setValue("DISPENSE_TIMEE", "");
	}        
//	/** 
//	 * 查询病患信息
//	 */  
//	public void onQueryNO(){
//        //MR_NO   
//		setValue("MR_NO", PatTool.getInstance().checkMrno(
//				TypeTool.getString(getValue("MR_NO"))));
//		setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
//				TypeTool.getString(getValue("MR_NO"))));   
//	}  
    /**
     * 编码接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String inv_code = parm.getValue("INV_CODE");
        if (!inv_code.equals("")) {
            getTextField("INV_CODE").setValue(inv_code);
        }
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!inv_desc.equals("")) {  
            getTextField("INV_CHN_DESC").setValue(inv_desc);
        }   
    }
	
	/**
	 * 查询 
	 */
	public void onQuery(){    
		
		//入库单号,100;物资编码,120;物资名称,400;规格,200;RFID,100;条码号,100;采购价格（单价）,120,double,#########0.00;采购金额（总额）,120,double,#########0.00;供应商,100,SUP;上级供应商,100,EX_SUP;生产商,100,MAN;入库日期,150,Timestamp,yyyy/MM/dd HH:mm:ss;效期,150,Timestamp,yyyy/MM/dd HH:mm:ss;批号,100
		//VERIFYIN_NO;INV_CODE;INV_CHN_DESC;DESCRIPTION;RFID;ORGIN_CODE;CONTRACT_PRICE;TOT;SUP_CODE;UP_SUP_CODE;MAN_CODE;VERIFYIN_DATE;VALID_DATE;BATCH_NO
//        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
//        DecimalFormat ff = new DecimalFormat("######0.00");
        //科室
        String deptcode = this.getValueString("DEPT_CODE"); 
        //物资编码  
        String invcode = this.getValueString("INV_CODE");
        //设备大分类  
        String kind = this.getValueString("KIND");
        //供应商 
        String supCode = this.getValueString("SUP_CODE"); 
        //上级供应商
        String exSupCode = this.getValueString("EX_SUP_CODE"); 
        //入库号    
        String dispenseNo = this.getValueString("DISPENSE_NO"); 
        //批号   
        String batchNo = this.getValueString("BATCH_NO");
        //RFID 
        String rfid = this.getValueString("RFID"); 
//        //使用日期起   
//        String billTimeS = this.getValueString("BIL_DATES"); 
//        //使用日期讫
//        String billTimeE = this.getValueString("BIL_DATEE"); 
        //效期起   
        String vaildTimeS = this.getValueString("VAILD_TIMES"); 
        //效期讫
        String vaildTimeE = this.getValueString("VAILD_TIMEE"); 
        //入库起   
        String dispenseTimeS = this.getValueString("DISPENSE_TIMES"); 
        //入库讫
        String dispenseTimeE = this.getValueString("DISPENSE_TIMEE");
        //INV_STOCKDD     WAST_FLG  为N的是未使用的
        String sql =               
                " SELECT G.VERIFYIN_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION," +
                " A.RFID,A.ORGIN_CODE,C.CONTRACT_PRICE,C.CONTRACT_PRICE*A.STOCK_QTY AS TOT," +
                " B.SUP_CODE,B.UP_SUP_CODE,B.MAN_CODE,A.VERIFYIN_DATE,A.VALID_DATE,A.BATCH_NO" +               
                " FROM INV_STOCKDD A LEFT JOIN INV_VERIFYINDD G " +
                " ON A.RFID = G.RFID ," + 
                " INV_BASE B,INV_AGENT C" +     
                " WHERE " +                                    
                " A.INV_CODE = B.INV_CODE" +  
                " AND A.INV_CODE = C.INV_CODE" +     
                " AND A.WAST_FLG = 'N' " +                                      
                " AND A.REGION_CODE='"+Operator.getRegion()+"'" +
                " AND B.EXPENSIVE_FLG = 'Y'" +
                " AND B.SEQMAN_FLG = 'Y' "; 
        StringBuffer SQL = new StringBuffer(); 
        //System.out.println("sql"+sql); 
        SQL.append(sql);   
        //科室         
        if (!deptcode.equals("")) {  
            SQL.append(" AND A.ORG_CODE='" + deptcode + "'");
        }
        //物资名称    
        if (!invcode.equals("")) {
            SQL.append(" AND A.INV_CODE='" + invcode + "'"); 
        }
        //设备大分类 
        if (!kind.equals("")) {
            SQL.append(" AND B.INV_KIND='" + kind + "'");
        }
        //供应商
        if (!supCode.equals("")) {
            SQL.append(" AND B.SUP_CODE='" + supCode + "'");
        } 
        //上级供应商
        if(!exSupCode.equals("")){ 
        	SQL.append(" AND　B.UP_SUP_CODE='"+exSupCode+"'");
        }
        //入库单号
        if(!dispenseNo.equals("")){
               SQL.append(" AND　G.VERIFYIN_NO='"+dispenseNo+"'");  
           } 
        //RFID 
        if(!rfid.equals("")){
            SQL.append(" AND　A.RFID='"+rfid+"'");  
        }     
        //批号
        if(!batchNo.equals("")){
            SQL.append(" AND　A.BATCH_NO='"+batchNo+"'");  
        }   
        //效期  
        if(!vaildTimeS.equals("")&&!vaildTimeE.equals("")){
            vaildTimeS = this.getValueString("VAILD_TIMES").replace("-","").substring(0, 8); 
            vaildTimeE = this.getValueString("VAILD_TIMEE").replace("-","").substring(0, 8); 
         SQL.append(" AND A.VALID_DATE BETWEEN TO_DATE ('" + vaildTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +
                "    AND TO_DATE ('" + vaildTimeE + "235959" + "', 'yyyyMMddHH24miss')"); 
        }        
        //入库日期    
        if(!dispenseTimeS.equals("")&&!dispenseTimeE.equals("")){
        	 dispenseTimeS = this.getValueString("DISPENSE_TIMES").replace("-","").substring(0, 8); 
             dispenseTimeE = this.getValueString("DISPENSE_TIMEE").replace("-","").substring(0, 8);
        SQL.append(" AND A.VERIFYIN_DATE BETWEEN TO_DATE ('" + dispenseTimeS + "000000" +
              "', 'yyyyMMddHH24miss')" +
              "    AND TO_DATE ('" + dispenseTimeE + "235959" + "', 'yyyyMMddHH24miss')");
        } 
        //使用日期、上级供应商、供应商、物资编码、效期区间、批号、使用部门、病案号
        SQL.append(" ORDER BY  B.UP_SUP_CODE,B.SUP_CODE,A.INV_CODE,A.BATCH_SEQ,A.ORG_CODE");  
        //System.out.println("SQL---TRACING!!!"+SQL);     
        TParm result = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
        //System.out.println("result"+result);
         // 判断错误值                     
        if (result == null || result.getCount() <= 0) {  
            callFunction("UI|TABLE|removeRowAll");
            this.messageBox("没有查询数据");
            return;      
        }                  
        this.callFunction("UI|TABLE|setParmValue", result);   
	}
	 
	/** 
	 * 清空
	 */
	public void onClear(){
		String str = "DEPT_CODE;INV_CODE;INV_CHN_DESC;EXPENSIVE_FLG;KIND;" +
        "SUP_CODE;EX_SUP_CODE;QTY_FLG;SAVE_FLG;BATCH_NO;RFID";
		this.clearValue(str);
		callFunction("UI|TABLE|removeRowAll"); 
		callFunction("UI|TABLEDD|removeRowAll");
	}
	
	/**   
	 * 打印 
	 */
	public void onPrint(){
//        if (this.getTable("TABLE").getRowCount() <= 0) {
//            this.messageBox("没有要打印的数据");
//            return;
//        } 
//        TParm prtParm = new TParm();
//        //表头
//        prtParm.setData("TITLE","TEXT","物资库存统计报表"); 
//        //日期
//        prtParm.setData("PRINT_DATE","TEXT","打印日期：" +
//                        StringTool.getString(StringTool.getTimestamp(new Date()),
//                                             "yyyy年MM月dd日"));
////        //财产总计
////        prtParm.setData("TOT","TEXT", "财产总计：" +this.getValueDouble("TOT_VALUE"));
//        TParm parm = this.getTable("TABLE").getShowParmValue();
//        TParm prtTableParm=new TParm(); 
//    	//MR_NO;PAT_NAME;INV_CODE;INV_CHN_DESC;DESCRIPTION;CONTRACT_PRICE;TOT;SUP_CODE;
//    	//UP_SUP_CODE;MAN_CODE;OWN_PRICE;AR_AMT;BILL_DATE;VERIFYIN_DATE;VALID_DATE;BATCH_NO;
//    	//CASEIER_CODE;OP_ROOM  
//        for(int i=0;i<parm.getCount("MR_NO");i++){
//            prtTableParm.addData("MR_NO",parm.getRow(i).getValue("MR_NO"));
//            prtTableParm.addData("PAT_NAME",parm.getRow(i).getValue("PAT_NAME"));
//            prtTableParm.addData("INV_CODE",parm.getRow(i).getValue("INV_CODE"));
//            prtTableParm.addData("INV_CHN_DESC",parm.getRow(i).getValue("INV_CHN_DESC"));
//            prtTableParm.addData("DESCRIPTION",parm.getRow(i).getValue("DESCRIPTION"));
//            prtTableParm.addData("CONTRACT_PRICE",parm.getRow(i).getValue("CONTRACT_PRICE"));
//            prtTableParm.addData("TOT",parm.getRow(i).getValue("TOT"));
//            prtTableParm.addData("SUP_CODE",parm.getRow(i).getValue("SUP_CODE"));
//            prtTableParm.addData("UP_SUP_CODE",parm.getRow(i).getValue("UP_SUP_CODE"));
//            prtTableParm.addData("MAN_CODE",parm.getRow(i).getValue("MAN_CODE"));
//            prtTableParm.addData("OWN_PRICE",parm.getRow(i).getValue("OWN_PRICE"));
//            prtTableParm.addData("AR_AMT",parm.getRow(i).getValue("AR_AMT"));
//            prtTableParm.addData("BILL_DATE",parm.getRow(i).getValue("BILL_DATE"));
//            prtTableParm.addData("VERIFYIN_DATE",parm.getRow(i).getValue("VERIFYIN_DATE"));
//            prtTableParm.addData("VALID_DATE",parm.getRow(i).getValue("VALID_DATE"));
//            prtTableParm.addData("BATCH_NO",parm.getRow(i).getValue("BATCH_NO"));
//            prtTableParm.addData("CASEIER_CODE",parm.getRow(i).getValue("CASEIER_CODE"));
//            prtTableParm.addData("OP_ROOM",parm.getRow(i).getValue("OP_ROOM"));
//        }  
//        prtTableParm.setCount(prtTableParm.getCount("MR_NO"));
//        prtTableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "INV_CODE"); 
//        prtTableParm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "TOT");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "UP_SUP_CODE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "BILL_DATE"); 
//        prtTableParm.addData("SYSTEM", "COLUMNS", "VERIFYIN_DATE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "CASEIER_CODE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "OP_ROOM");
//        prtParm.setData("TABLE", prtTableParm.getData());
//        //表尾  
//        prtParm.setData("USER","TEXT", "制表人：" + Operator.getName());
//        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVTracingReport.jhw",
//                             prtParm);   
//    
//		
	} 
	
    /**  
     * 导出Excel
     */
    public void onExport() {
        if (this.getTable("TABLE").getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
                    "物资溯源报表");
        } 
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
     * 得到文本控件
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
      
}
 