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
 * Title: ��������Դ
 * </p>
 * 
 * <p>  
 * Description:��������Դ
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
	 * ��ʼ������
	 */
	public void onInit(){  
        //��ʼ���û�
        String deptCode = Operator.getDept();   
        this.setValue("DEPT_CODE", deptCode);
        //PACK_CODE 
        callFunction("UI|PACK_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\inv\\INVPackPopup.x");  
        //textfield���ܻش�ֵ        
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
	 * ��ѯ������Ϣ
	 */
	public void onQueryNO(){ 
        //MR_NO  
		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
				TypeTool.getString(getValue("MR_NO"))));   
	}  
    /** 
     * ������ܷ���ֵ���� 
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
	 * ��ѯ 
	 */  
	public void onQuery(){   
//        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
//        DecimalFormat ff = new DecimalFormat("######0.00");
        //����  
        String deptcode = this.getValueString("DEPT_CODE");
        //����������
        String packcode = this.getValueString("PACK_CODE");
        //����������
        String barcode = this.getValueString("BARCODE");
        //������
        String mrNo = this.getValueString("MR_NO"); 
        //ʹ�������� 
        String userTimeS = this.getValueString("USER_TIMES").replace("-","").substring(0, 8); 
        //ʹ��������  
        String userTimeE = this.getValueString("USER_TIMEE").replace("-","").substring(0, 8); 
        //�������ӣ������������Ƿ���ʹ������
        //������ڡ�������ڡ��������ڡ��������Ρ��������� 
        //��������� 
        String packTimeS = this.getValueString("PACK_TIMES"); 
        //���������  
        String packTimeE = this.getValueString("PACK_TIMEE"); 
        //��������� 
        String sterTimeS = this.getValueString("STERILIZATION_TIMES"); 
        //���������  
        String sterTimeE = this.getValueString("STERILIZATION_TIMEE"); 
        //���������� 
        String disTimeS = this.getValueString("DISINFECTION_TIMES"); 
        //����������       
        String disTimeE = this.getValueString("DISINFECTION_TIMEE"); 
        //��������   
        String disPotseq = this.getValueString("DISINFECTION_POTSEQ");
        //������,100;��������,100;����������,100;����������,100;�Ʒ�����,100;�������,100;
        //�����Ա,100;�Ʒ���Ա,100;����,100;
        //�������,100;�������,100;�������,100;��������,100;��������,100;��������,100
        //MR_NO;PAT_NAME;PACK_CODE;PACK_DESC;BILL_DATE;OPT_DATE;OPT_USER;
        //CASEIER_CODE;OP_ROOM;OPT_DATE1;STERILIZATION_POTSEQ;STERILLZATION_PROGRAM; 
        //OPT_DATE2;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM
           
        //INV_DISINFECTION ����     INV_STERILIZATION ���
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
        //����            
        if (!deptcode.equals("")) { 
            SQL.append(" AND A.ORG_CODE='" + deptcode + "'");
        }
        //����������        
        if (!packcode.equals("")) {
            SQL.append(" AND A.PACK_CODE='" + packcode + "'"); 
        }
        //���� 
        if (!barcode.equals("")) {
            SQL.append(" AND A.BARCODE='" + barcode + "'");
        }
        //������   
        if(!mrNo.equals("")){   
             SQL.append(" AND��C.MR_NO='"+mrNo+"'");
           }            
        //�������� 
        if(!disPotseq.equals("")){   
             SQL.append(" AND��F.DISINFECTION_POTSEQ='"+disPotseq+"'");
           }  
        //ʹ������    
        SQL.append(" AND C.OPT_DATE BETWEEN TO_DATE ('" + userTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +  
                "    AND TO_DATE ('" + userTimeE + "235959" + "', 'yyyyMMddHH24miss')");   
        if(!packTimeS.equals("")&&!packTimeE.equals("")){
        //�������    
            packTimeS = this.getValueString("PACK_TIMES").replace("-","").substring(0, 8); 
            packTimeE = this.getValueString("PACK_TIMEE").replace("-","").substring(0, 8); 
        SQL.append(" AND A.OPT_DATE BETWEEN TO_DATE ('" + packTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +   
                "    AND TO_DATE ('" + packTimeE + "235959" + "', 'yyyyMMddHH24miss')");  
        
        } 
        //�������  
        if(!sterTimeS.equals("")&&!sterTimeE.equals("")){
            sterTimeS = this.getValueString("STERILIZATION_TIMES").replace("-","").substring(0, 8); 
            sterTimeE = this.getValueString("STERILIZATION_TIMEE").replace("-","").substring(0, 8); 
        SQL.append(" AND B.OPT_DATE BETWEEN TO_DATE ('" + sterTimeS + "000000" +
                "', 'yyyyMMddHH24miss')" +  
                "    AND TO_DATE ('" + sterTimeE + "235959" + "', 'yyyyMMddHH24miss')");   
        }  
        //��������   
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
        // �жϴ���ֵ 
        if (result == null || result.getCount() <= 0) {  
            callFunction("UI|TABLE|removeRowAll");
            this.messageBox("û�в�ѯ����");
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
	 * ���
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
	 * ��ӡ 
	 */
	public void onPrint(){
//        if (this.getTable("TABLE").getRowCount() <= 0) {
//            this.messageBox("û��Ҫ��ӡ������");
//            return;
//        } 
//        TParm prtParm = new TParm();
//        //��ͷ
//        prtParm.setData("TITLE","TEXT","���ʿ��ͳ�Ʊ���"); 
//        //����
//        prtParm.setData("PRINT_DATE","TEXT","��ӡ���ڣ�" +
//                        StringTool.getString(StringTool.getTimestamp(new Date()),
//                                             "yyyy��MM��dd��"));
////        //�Ʋ��ܼ�
////        prtParm.setData("TOT","TEXT", "�Ʋ��ܼƣ�" +this.getValueDouble("TOT_VALUE"));
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
//        //��β  
//        prtParm.setData("USER","TEXT", "�Ʊ��ˣ�" + Operator.getName());
//        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVOptPacketVTracingReport.jhw",
//                             prtParm);    
//    
//		
	}
	 
    /**  
     * ����Excel
     */
    public void onExport() {
        if (this.getTable("TABLE").getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
                    "��������Դ����");
        } 
    }
    /** 
     * �õ���ؼ�
     * @param tagName String
     * @return TTable
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
     * �õ��ı��ؼ�
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    

}
 