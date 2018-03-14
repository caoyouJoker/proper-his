package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;  
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 *   
 * 
 * <p>
 * 
 * Title: 手术包溯源
 * </p>
 * 
 * <p>  
 * Description:手术包溯源
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
public class INVOptPacketTracingControl extends TControl{
	/**
	 * 初始化方法
	 */
	public void onInit(){  
        //初始化用户
        String deptCode = Operator.getDept();   
        this.setValue("DEPT_CODE", deptCode);
        //PACK_CODE 
        callFunction("UI|PACK_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\inv\\INVPackPopup.x");  
        //textfield接受回传值        
        callFunction("UI|PACK_CODE|addEventListener",    
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                 getDate(), -1); 
//		 setValue("S_TIME", yesterday.toString().substring(0,10)+"00:00:00");
//		 setValue("E_TIME", SystemTool.getInstance().getDate().toString().substring(0,10)+"23:59:59");
		 setValue("USER_TIMES", yesterday);
		 setValue("USER_TIMEE", SystemTool.getInstance().getDate());	
		 setValue("PACK_TIMES", "");
		 setValue("PACK_TIMEE", "");
		 setValue("STERILIZATION_TIMES", "");
		 setValue("STERILIZATION_TIMEE", "");
		 setValue("DISINFECTION_TIMES", ""); 
		 setValue("DISINFECTION_TIMEE", "");
		 
	}     
	/**
	 * 查询病患信息
	 */
	public void onQueryNO(){ 
        //MR_NO  
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
				TypeTool.getString(getValue("MR_NO"))));   
	}  
    /** 
     * 编码接受返回值方法 
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String pack_code = parm.getValue("PACK_CODE"); 
        if (!pack_code.equals("")) {
            getTextField("PACK_CODE").setValue(pack_code);
        } 
        String pack_desc = parm.getValue("PACK_DESC");
        if (!pack_desc.equals("")) {  
            getTextField("PACK_DESC").setValue(pack_desc); 
        }    
    }
	
	/**
	 * 查询 
	 */  
	public void onQuery(){   
//        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
//        DecimalFormat ff = new DecimalFormat("######0.00");
        //科室  
        String deptcode = this.getValueString("DEPT_CODE");
        //手术包名称
        String packcode = this.getValueString("PACK_CODE");
        //手术包条码
        String barcode = this.getValueString("BARCODE");
        //病案号
        String mrNo = this.getValueString("MR_NO"); 
        //使用日期起 
        String userTimeS = this.getValueString("USER_TIMES").replace("-","").substring(0, 8); 
        //使用日期讫  
        String userTimeE = this.getValueString("USER_TIMEE").replace("-","").substring(0, 8); 
        //条件增加：增加手术包是否已使用条件
        //打包日期、灭菌日期、消毒日期、消毒锅次、消毒锅号 
        //打包日期起 
        String packTimeS = this.getValueString("PACK_TIMES"); 
        //打包日期讫  
        String packTimeE = this.getValueString("PACK_TIMEE"); 
        //灭菌日期起 
        String sterTimeS = this.getValueString("STERILIZATION_TIMES"); 
        //灭菌日期讫  
        String sterTimeE = this.getValueString("STERILIZATION_TIMEE"); 
        //消毒日期起 
        String disTimeS = this.getValueString("DISINFECTION_TIMES"); 
        //消毒日期讫       
        String disTimeE = this.getValueString("DISINFECTION_TIMEE"); 
        //消毒锅次   
        String disPotseq = this.getValueString("DISINFECTION_POTSEQ");
        //病案号,100;病人姓名,100;手术包编码,100;手术包名称,100;计费日期,100;打包日期,100;
        //打包人员,100;计费人员,100;术间,100;
        //灭菌日期,100;灭菌锅次,100;灭菌程序,100;消毒日期,100;消毒锅次,100;消毒程序,100
        //MR_NO;PAT_NAME;PACK_CODE;PACK_DESC;BILL_DATE;OPT_DATE;OPT_USER;
        //CASEIER_CODE;OP_ROOM;OPT_DATE1;STERILIZATION_POTSEQ;STERILLZATION_PROGRAM; 
        //OPT_DATE2;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM
           
        //INV_DISINFECTION 消毒     INV_STERILIZATION 灭菌
        String sql =                
                " SELECT C.MR_NO,E.PAT_NAME,B.PACK_CODE,D.PACK_DESC,TO_CHAR(C.BILL_DATE,'YYYY/MM/DD HH:mm:ss') AS BILL_DATE,TO_CHAR(A.OPT_DATE,'YYYY/MM/DD HH:mm:ss') AS OPT_DATE," +
                " A.OPT_USER,C.CASEIER_CODE,C.OP_ROOM,TO_CHAR(B.OPT_DATE,'YYYY/MM/DD HH:mm:ss') AS OPT_DATE1,B.STERILIZATION_POTSEQ,B.STERILLZATION_PROGRAM,C.PACK_BARCODE," +
                " TO_CHAR(F.OPT_DATE,'YYYY/MM/DD HH:mm:ss') AS OPT_DATE2,F.DISINFECTION_POTSEQ,F.DISINFECTION_PROGRAM "+ 
                " FROM INV_PACKSTOCKM_HISTORY A,INV_STERILIZATION B,SPC_INV_RECORD C,INV_PACKM D,SYS_PATINFO E, INV_DISINFECTION F"  +
                " WHERE C.PACK_BARCODE = B.BARCODE" +    
                " AND C.PACK_BARCODE = A.BARCODE" +
                " AND C.PACK_BARCODE = F.BARCODE" +    
                " AND A.PACK_CODE = D.PACK_CODE " +         
                " AND C.MR_NO = E.MR_NO" +                
                " AND C.CASE_NO_SEQ IS NOT NULL" +  
                " AND C.SEQ_NO IS NOT NULL";     
        StringBuffer SQL = new StringBuffer();  
        SQL.append(sql);         
        //科室            
        if (!deptcode.equals("")) { 
            SQL.append(" AND A.ORG_CODE='" + deptcode + "'");
        }
        //手术包名称        
        if (!packcode.equals("")) {
            SQL.append(" AND A.PACK_CODE='" + packcode + "'"); 
        }
        //条码 
        if (!barcode.equals("")) {
            SQL.append(" AND A.BARCODE='" + barcode + "'");
        }
        //病案号   
        if(!mrNo.equals("")){   
             SQL.append(" AND　C.MR_NO='"+mrNo+"'");
           }            
        //消毒锅次 
        if(!disPotseq.equals("")){   
             SQL.append(" AND　F.DISINFECTION_POTSEQ='"+disPotseq+"'");
           }  
        //使用日期    
        SQL.append(" AND C.OPT_DATE BETWEEN TO_DATE ('" + userTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +  
                "    AND TO_DATE ('" + userTimeE + "235959" + "', 'yyyyMMddHH24miss')");   
        if(!packTimeS.equals("")&&!packTimeE.equals("")){
        //打包日期    
            packTimeS = this.getValueString("PACK_TIMES").replace("-","").substring(0, 8); 
            packTimeE = this.getValueString("PACK_TIMEE").replace("-","").substring(0, 8); 
        SQL.append(" AND A.OPT_DATE BETWEEN TO_DATE ('" + packTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +   
                "    AND TO_DATE ('" + packTimeE + "235959" + "', 'yyyyMMddHH24miss')");  
        
        } 
        //灭菌日期  
        if(!sterTimeS.equals("")&&!sterTimeE.equals("")){
            sterTimeS = this.getValueString("STERILIZATION_TIMES").replace("-","").substring(0, 8); 
            sterTimeE = this.getValueString("STERILIZATION_TIMEE").replace("-","").substring(0, 8); 
        SQL.append(" AND B.OPT_DATE BETWEEN TO_DATE ('" + sterTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +  
                "    AND TO_DATE ('" + sterTimeE + "235959" + "', 'yyyyMMddHH24miss')");   
        }  
        //消毒日期   
        if(!disTimeS.equals("")&&!disTimeE.equals("")){
        	  disTimeS = this.getValueString("DISINFECTION_TIMES").replace("-","").substring(0, 8);      
              disTimeE = this.getValueString("DISINFECTION_TIMEE").replace("-","").substring(0, 8); 
        SQL.append(" AND F.OPT_DATE BETWEEN TO_DATE ('" + disTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +   
                "    AND TO_DATE ('" + disTimeE + "235959" + "', 'yyyyMMddHH24miss')"); 
        }  
        SQL.append(" ORDER BY  B.PACK_CODE");       
        //System.out.println("SQL---INVOPTPacket"+SQL);                             
        TParm result = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
        // 判断错误值 
        if (result == null || result.getCount() <= 0) {  
            callFunction("UI|TABLE|removeRowAll");
            this.messageBox("没有查询数据");
            return;   
        }   
        TParm date = new TParm();   
        for (int i = 0; i < result.getCount(); i++) { 
        	//MR_NO;PAT_NAME;PACK_CODE;PACK_DESC;BILL_DATE;
        	//OPT_DATE;OPT_USER;CASEIER_CODE;OP_ROOM;STERILIZATION_POTSEQ;STERILLZATION_PROGRAM   
            date.addData("MR_NO", result.getValue("MR_NO", i));
            date.addData("PAT_NAME", result.getValue("PAT_NAME", i));
            date.addData("PACK_CODE", result.getValue("PACK_CODE", i));
            date.addData("PACK_DESC", result.getValue("PACK_DESC", i));
            date.addData("BILL_DATE", result.getValue("BILL_DATE", i));
            date.addData("OPT_DATE", result.getValue("OPT_DATE", i)); 
            date.addData("OPT_DATE", result.getValue("OPT_DATE", i));
            date.addData("CASEIER_CODE", result.getValue("CASEIER_CODE", i));   
            date.addData("OP_ROOM", result.getValue("OP_ROOM", i));
            date.addData("STERILIZATION_POTSEQ", result.getValue("STERILIZATION_POTSEQ", i));
            date.addData("STERILLZATION_PROGRAM", result.getValue("STERILLZATION_PROGRAM", i));
        }    
        this.callFunction("UI|TABLE|setParmValue", result);   
    
		
	}
	
	/**
	 * 清空
	 */   
	public void onClear(){ 
		String str = "DEPT_CODE;PACK_CODE;BARCODE;MR_NO;PAT_NAME;PACK_DESC" +
				";STERILIZATION_NO;STERILIZATION_POTSEQ";
		this.clearValue(str);  
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                 getDate(), -1);
		 setValue("USER_TIMES", yesterday);
		 setValue("USER_TIMEE", SystemTool.getInstance().getDate());
		 setValue("PACK_TIMES", "");  
		 setValue("PACK_TIMEE", "");
		 setValue("STERILIZATION_TIMES", "");
		 setValue("STERILIZATION_TIMEE", "");
		 setValue("DISINFECTION_TIMES", "");    
		 setValue("DISINFECTION_TIMEE", "");
		callFunction("UI|TABLE|removeRowAll");  
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
//        TParm parm = this.getTable("TABLE").getShowParmValue();;
//        TParm prtTableParm=new TParm(); 
//    	//MR_NO;PAT_NAME;PACK_CODE;PACK_DESC;BILL_DATE;
//    	//OPT_DATE;OPT_USER;CASEIER_CODE;OP_ROOM;STERILIZATION_POTSEQ;STERILLZATION_PROGRAM  
//        for(int i=0;i<parm.getCount("MR_NO");i++){
//            prtTableParm.addData("MR_NO",parm.getRow(i).getValue("MR_NO"));
//            prtTableParm.addData("PAT_NAME",parm.getRow(i).getValue("PAT_NAME"));
//            prtTableParm.addData("PACK_CODE",parm.getRow(i).getValue("PACK_CODE"));
//            prtTableParm.addData("PACK_DESC",parm.getRow(i).getValue("PACK_DESC"));
//            prtTableParm.addData("BILL_DATE",parm.getRow(i).getValue("BILL_DATE"));
//            prtTableParm.addData("OPT_DATE",parm.getRow(i).getValue("OPT_DATE"));
//            prtTableParm.addData("OPT_USER",parm.getRow(i).getValue("OPT_USER"));
//            prtTableParm.addData("CASEIER_CODE",parm.getRow(i).getValue("CASEIER_CODE"));
//            prtTableParm.addData("OP_ROOM",parm.getRow(i).getValue("OP_ROOM"));
//            prtTableParm.addData("STERILIZATION_POTSEQ",parm.getRow(i).getValue("STERILIZATION_POTSEQ"));
//            prtTableParm.addData("STERILLZATION_PROGRAM",parm.getRow(i).getValue("STERILLZATION_PROGRAM"));;
//        }  
//        //633.275590551181 
//        //841.8897637795276
//        prtTableParm.setCount(prtTableParm.getCount("MR_NO"));
//        prtTableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "PACK_CODE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "OPT_USER");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "CASEIER_CODE");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "OP_ROOM");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "STERILIZATION_POTSEQ");
//        prtTableParm.addData("SYSTEM", "COLUMNS", "STERILLZATION_PROGRAM");
//        prtParm.setData("TABLE", prtTableParm.getData());
//        //表尾  
//        prtParm.setData("USER","TEXT", "制表人：" + Operator.getName());
//        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVOptPacketVTracingReport.jhw",
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
                    "手术包溯源报表");
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
 