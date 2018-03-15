package com.javahis.ui.inv;

import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;  
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p> Title: ��ֵ��Դ  </p>
 *  
 * <p> Description:��ֵ��Դ </p> 
 *  
 * <p> Copyright: Copyright (c) ProperSoft 2013 </p>
 * 
 * <p> Company:JavaHis </p>
 *  
 * @author fux 2013.11.08 
 * @version 1.0
 */
public class INVTracingControl extends TControl{
	/**
	 * ��ʼ������
	 */
	public void onInit(){
        //��ʼ���û�
        String deptCode = Operator.getDept();   
        this.setValue("DEPT_CODE", deptCode);
        callFunction("UI|INV_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\inv\\INVBasePopup.x");
        //textfield���ܻش�ֵ  
        callFunction("UI|INV_CODE|addEventListener", 
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                 getDate(), -1);
//		 Timestamp tommorw = StringTool.rollDate(SystemTool.getInstance().
//                 getDate(), +1);
//		 setValue("S_TIME", yesterday.toString().substring(0,10)+"00:00:00");
//		 setValue("E_TIME", SystemTool.getInstance().getDate().toString().substring(0,10)+"23:59:59");
//		 setValue("VAILD_TIMES", SystemTool.getInstance().getDate());
//		 setValue("VAILD_TIMEE", tommorw);  	
		 setValue("VAILD_TIMES", ""); 
		 setValue("VAILD_TIMEE", "");
		 setValue("DISPENSE_TIMES", ""); 
		 setValue("DISPENSE_TIMEE", ""); 
		 setValue("BIL_DATES", yesterday); 
		 setValue("BIL_DATEE", SystemTool.getInstance().getDate());
		
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
	 * ��ѯ 
	 */
	public void onQuery(){  
        //����
        String deptcode = this.getValueString("DEPT_CODE"); 
        //���ʱ���  
        String invcode = this.getValueString("INV_CODE");
        //�豸�����  
        String kind = this.getValueString("KIND");
        //��Ӧ�� 
        String supCode = this.getValueString("SUP_CODE");
        //�ϼ���Ӧ��
        String exSupCode = this.getValueString("EX_SUP_CODE");
        //������
        String mrNo = this.getValueString("MR_NO");  
        //����     
        String dispenseNo = this.getValueString("DISPENSE_NO"); 
        //����  
        String batchNo = this.getValueString("BATCH_NO");
        //RFID   
        String rfid = this.getValueString("RFID");  
        //Ч����    
        String vaildTimeS = this.getValueString("VAILD_TIMES"); 
        //Ч����
        String vaildTimeE = this.getValueString("VAILD_TIMEE"); 
        //�����   
        String dispenseTimeS = this.getValueString("DISPENSE_TIMES"); 
        //�����
        String dispenseTimeE = this.getValueString("DISPENSE_TIMEE");
        //INV_STOCKDD     WAST_FLG  ΪN����δʹ�õ�
        //�Ʒ���    
        String bilDateS = this.getValueString("BIL_DATES").replace("-","").substring(0, 8);  
        //�Ʒ���      
        String bilDateE = this.getValueString("BIL_DATEE").replace("-","").substring(0, 8); 
		
		//����ѡ��
//		if(this.getCheckBox("GATHER").isSelected()){
//			//�����ֵ˷Դ ,������ɶ���壿
//		}
//		else{
//        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
//        DecimalFormat ff = new DecimalFormat("######0.00");
		//�����ⵥ�š��������š�����Ч�ڡ�����rfid������������ڡ�
 
        String sql =              
                "SELECT G.VERIFYIN_NO, B.INV_CODE, C.INV_CHN_DESC, C.DESCRIPTION, B.MR_NO, B.PAT_NAME,A.RFID, G.ORGIN_CODE, D.CONTRACT_PRICE, " + 
                "       D.CONTRACT_PRICE * B.QTY AS TOT, C.SUP_CODE, C.UP_SUP_CODE, C.MAN_CODE, B.OWN_PRICE, B.AR_AMT, " + 
                "       TO_CHAR( B.OPT_DATE, 'YYYY/MM/DD HH:MM:SS') AS OPT_DATE, " + 
                "       TO_CHAR( B.BILL_DATE, 'YYYY/MM/DD HH:MM:SS') AS BILL_DATE, " + 
                "       TO_CHAR( A.VERIFYIN_DATE, 'YYYY/MM/DD HH:MM:SS') AS VERIFYIN_DATE, " + 
                "       TO_CHAR( A.VALID_DATE, 'YYYY/MM/DD HH:MM:SS') AS VALID_DATE, " + 
                "       B.BATCH_SEQ AS BATCH_NO, B.CASHIER_CODE, B.OP_ROOM, B.DEPT_CODE, B.ORDER_DEPT_CODE,B.ORDER_DR_CODE " +               
                " FROM INV_STOCKDD A LEFT JOIN INV_VERIFYINDD G ON A.RFID = G.RFID ," +
                "      SPC_INV_RECORD B,INV_BASE C,INV_AGENT D " +    
                " WHERE B.INV_CODE = A.INV_CODE " +                          
                "   AND B.INV_CODE = C.INV_CODE " +              
                "   AND B.INV_CODE = D.INV_CODE " +      
                "   AND B.BAR_CODE = A.RFID " +                    
                "   AND A.REGION_CODE='"+Operator.getRegion()+"' " +
                "   AND C.EXPENSIVE_FLG = 'Y' " +
                "   AND C.SEQMAN_FLG = 'Y' " +       
                "   AND B.CASE_NO_SEQ IS NOT NULL " +    
                "   AND B.SEQ_NO IS NOT NULL ";  
        StringBuffer SQL = new StringBuffer(); 
        //System.out.println("sql"+sql); 
        SQL.append(sql);   
        //����          
        if (!deptcode.equals("")) {  
        //2014-02-10ע��            SQL.append(" AND A.ORG_CODE='" + deptcode + "'");
        	SQL.append(" AND B.EXE_DEPT_CODE='" + deptcode + "'");
        }
        //��������    
        if (!invcode.equals("")) {
            SQL.append(" AND B.INV_CODE='" + invcode + "'"); 
        }
        //�豸����� 
        if (!kind.equals("")) {
            SQL.append(" AND C.INV_KIND='" + kind + "'");
        }
        //��Ӧ��
        if (!supCode.equals("")) {
            SQL.append(" AND C.SUP_CODE='" + supCode + "'");
        } 
        //�ϼ���Ӧ��
        if(!exSupCode.equals("")){ 
        	SQL.append(" AND��C.UP_SUP_CODE='"+exSupCode+"'");
        }
        //������ 
        if(!mrNo.equals("")){
               SQL.append(" AND��B.MR_NO='"+mrNo+"'");
           } 
        //��ⵥ�� 
        if(!dispenseNo.equals("")){
               SQL.append(" AND��G.VERIFYIN_NO='"+dispenseNo+"'");  
           } 
        //RFID
        if(!rfid.equals("")){
            SQL.append(" AND��A.RFID='"+rfid+"'");  
        }     
        //����
        if(!batchNo.equals("")){
            SQL.append(" AND��A.BATCH_NO='"+batchNo+"'");  
        }  
  
        //Ч��  
        if(!vaildTimeS.equals("")&&!vaildTimeE.equals("")){
        	   vaildTimeS = this.getValueString("VAILD_TIMES").replace("-","").substring(0, 8); 
        	   vaildTimeE = this.getValueString("VAILD_TIMEE").replace("-","").substring(0, 8); 
               SQL.append(" AND B.VALID_DATE BETWEEN TO_DATE ('" + vaildTimeS + "000000" +
                       "', 'yyyyMMddHH24miss')" +
                       "    AND TO_DATE ('" + vaildTimeE + "235959" + "', 'yyyyMMddHH24miss')"); 
        }
        //������� 
        if(!dispenseTimeS.equals("")&&!dispenseTimeE.equals("")){
        	dispenseTimeS = this.getValueString("DISPENSE_TIMES").replace("-","").substring(0, 8); 
        	dispenseTimeE = this.getValueString("DISPENSE_TIMEE").replace("-","").substring(0, 8);
        SQL.append(" AND A.VERIFYIN_DATE BETWEEN TO_DATE ('" + dispenseTimeS + "000000" +"', 'yyyyMMddHH24miss')" +
              "    AND TO_DATE ('" + dispenseTimeE + "235959" + "', 'yyyyMMddHH24miss')"); 
        }    
        //�Ʒ����ʱ��     
               SQL.append(" AND B.BILL_DATE BETWEEN TO_DATE ('" + bilDateS + "000000" +
               "', 'yyyyMMddHH24miss')" +
               "    AND TO_DATE ('" + bilDateE + "235959" + "', 'yyyyMMddHH24miss')");
        //ʹ�����ڡ��ϼ���Ӧ�̡���Ӧ�̡����ʱ��롢Ч�����䡢���š�ʹ�ò��š�������
        SQL.append(" ORDER BY  B.BILL_DATE,C.UP_SUP_CODE,C.SUP_CODE,B.INV_CODE,A.VALID_DATE,B.BATCH_SEQ,A.ORG_CODE,B.MR_NO");  
        System.out.println("SQL---TRACING!!!"+SQL);      
        TParm result = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
        //System.out.println("result"+result);  
         // �жϴ���ֵ                      
        if (result == null || result.getCount() <= 0) {  
            callFunction("UI|TABLE|removeRowAll");
            this.messageBox("û�в�ѯ����");
            return;     
        }                 
        
        for(int i=0;i<result.getCount("MR_NO");i++){
        	
        	if(result.getRow(i).getData("DEPT_CODE").toString().equals("030101")){
        		result.setData("DEPT_ABS_DESC", i, "���Ļ�");
//        		result.getRow(i).setData("DEPT_ABS_DESC", i, "���Ļ�");
        	}else if(result.getRow(i).getData("DEPT_CODE").toString().equals("030102")){
        		result.setData("DEPT_ABS_DESC", i, "����ǰ");
//        		result.getRow(i).setData("DEPT_ABS_DESC", i, "����ǰ");
        	}else if(result.getRow(i).getData("DEPT_CODE").toString().equals("030103")){
        		result.setData("DEPT_ABS_DESC", i, "�Ž�");
 //       		result.getRow(i).setData("DEPT_ABS_DESC", i, "�Ž�");
        	}else if(result.getRow(i).getData("DEPT_CODE").toString().equals("030104")){
        		result.setData("DEPT_ABS_DESC", i, "������");
 //       		result.getRow(i).setData("DEPT_ABS_DESC", i, "������");
        	}else if(result.getRow(i).getData("DEPT_CODE").toString().equals("030106")){
        		result.setData("DEPT_ABS_DESC", i, "�ź���");
  //      		result.getRow(i).setData("DEPT_ABS_DESC", i, "�ź���");
        	}else if(result.getRow(i).getData("DEPT_CODE").toString().equals("0304")){
        		result.setData("DEPT_ABS_DESC", i, "����");
  //      		result.getRow(i).setData("DEPT_ABS_DESC", i, "����");
        	}else {
        		result.setData("DEPT_ABS_DESC", i, "����");
  //      		result.getRow(i).setData("DEPT_ABS_DESC", i, "����");
        	}
        	
        }
        
        this.callFunction("UI|TABLE|setParmValue", result);   
//		}
	}
	
	/**  
	 * ���
	 */
	public void onClear(){
		String str = "DEPT_CODE;INV_CODE;INV_CHN_DESC;EXPENSIVE_FLG;KIND;" +
        "SUP_CODE;EX_SUP_CODE;QTY_FLG;SAVE_FLG;PAT_NAME;BATCH_NO;RFID";
		this.clearValue(str);
		callFunction("UI|TABLE|removeRowAll"); 
		callFunction("UI|TABLEDD|removeRowAll");
		 setValue("VAILD_TIMES", ""); 
		 setValue("VAILD_TIMEE", ""); 
		 setValue("DISPENSE_TIMES", ""); 
		 setValue("DISPENSE_TIMEE", ""); 		 
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                 getDate(), -1);
		 setValue("BIL_DATES", yesterday); 
		 setValue("BIL_DATEE", SystemTool.getInstance().getDate());
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
//        //��β  
//        prtParm.setData("USER","TEXT", "�Ʊ��ˣ�" + Operator.getName());
//        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVTracingReport.jhw",
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
                    "������Դ����");
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
    
    /**
     * �õ�ѡ��
     * @param tagName String 
     * @return TTextField
     */
    private TCheckBox getCheckBox(String tagName) { 
        return (TCheckBox) getComponent(tagName);
    }
    
    
    
}
 