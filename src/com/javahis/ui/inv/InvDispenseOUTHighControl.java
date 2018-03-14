package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import jdo.inv.InvRequestDTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.event.TTableEvent;

import com.dongyang.data.TNull;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

import jdo.inv.INVSQL;
import jdo.inv.INVTool;
import jdo.inv.InvDispenseDTool;
import jdo.inv.InvStockMTool;

/** 
 * <p>
 * Title: ���ʸ�ֵɨ����� �� �����ҿⷿ
 * Description: ���ʸ�ֵɨ����� �� �����ҿⷿ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013 
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author fux 2016.08.02
 * @version 4.0  
 */
public class InvDispenseOUTHighControl 
    extends TControl { 
    public InvDispenseOUTHighControl() {  
    }

    //����  onBarcode �¼�
    
    private TTable table_m;//���뵥

    private TTable table_d;//����ϸ��                 
    
    private TParm parmTable;//����table - parm
    

    // ȫԺҩ�ⲿ����ҵ����
    @SuppressWarnings("unused")
	private boolean request_all_flg = true;

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }
    
    /**
     * ����ɨ���¼�
     */
    public void onBarcode(){
    	if("".equals(this.getValueString("FROM_ORG_CODE"))){
    		this.messageBox("���ܿ���  ����Ϊ��");
    		
    	}
    	
    	if("".equals(this.getValueString("TO_ORG_CODE"))){
    		this.messageBox("�������  ����Ϊ��");
    		
    	}
    	
    	if("".equals(this.getValueString("BARCODE"))){
    		this.messageBox("ɨ������  ����Ϊ��");
            callFunction("UI|save|setEnabled", false);
    	}
        String rfid = "";
        if(table_d.getParmValue() != null){
        	 if(table_d.getParmValue().getCount("RFID")>0){
             	for (int j = 0; j < table_d.getParmValue().getCount("RFID"); j++) {
             		rfid = table_d.getParmValue().getValue("RFID", j);
             		if(rfid.equals(this.getValueString("BARCODE"))){
             			this.messageBox("�ظ����� ����ɨ�裡");
             			return;
             		}
         		} 
             }
        }
       
    
    	
    	
        callFunction("UI|save|setEnabled", true);
    	//����rifd �� inv_stockdd ȡֵ Ȼ�� �ŵ� inv_dispensedd��
    	String barCode  = this.getValueString("BARCODE");
    	String fromOrgCode = this.getValueString("FROM_ORG_CODE");
    	//SELECT_FLG;INV_CODE;INV_CHN_DESC;INVSEQ_NO;DESCRIPTION;QTY;REQUEST_QTY;ACTUAL_QTY;DISPENSE_UNIT;
    	//COST_PRICE;SUM_AMT;BATCH_SEQ;BATCH_NO;VALID_DATE;MAN_CODE;SEQMAN_FLG;VALIDATE_FLG
    	String sql = " SELECT 'Y' AS SELECT_FLG,B.INV_CODE,B.INV_CHN_DESC,A.INVSEQ_NO,B.DESCRIPTION,'1' AS QTY," +
    			" '1' AS REQUEST_QTY,B.DISPENSE_UNIT,B.COST_PRICE,'0' AS SUM_AMT,A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE," +
    			" B.MAN_CODE,B.SEQMAN_FLG,B.VALIDATE_FLG,A.RFID " +   
    			" FROM INV_STOCKDD A,INV_BASE B  " +
    			" WHERE A.INV_CODE = B.INV_CODE" + 
    			" AND A.ORG_CODE = '"+fromOrgCode+"' " +
    			" AND A.RFID = '"+barCode+"' " +
    			" AND A.WAST_FLG = 'N'  ";
    	//System.out.println("sql:"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //parmTable = new TParm();
        for(int i=0;i<parm.getCount("INV_CODE");i++){
//        	messageBox("parmTable"+parmTable);  
        	parmTable.addData("SELECT_FLG","Y");
        	parmTable.addData("DESCRIPTION",parm.getRow(i).getValue("DESCRIPTION"));
        	parmTable.addData("INV_CODE",parm.getRow(i).getValue("INV_CODE"));
        	parmTable.addData("INV_CHN_DESC",parm.getRow(i).getValue("INV_CHN_DESC"));  
        	parmTable.addData("INVSEQ_NO",parm.getRow(i).getValue("INVSEQ_NO"));
        	parmTable.addData("DESCRIPTION",parm.getRow(i).getValue("DESCRIPTION"));
        	parmTable.addData("QTY",1);
        	parmTable.addData("REQUEST_QTY",1);
        	parmTable.addData("DISPENSE_UNIT",parm.getRow(i).getValue("DISPENSE_UNIT"));
        	parmTable.addData("COST_PRICE",parm.getRow(i).getValue("COST_PRICE"));
        	parmTable.addData("SUM_AMT",parm.getRow(i).getValue("SUM_AMT"));  
        	parmTable.addData("BATCH_SEQ",parm.getRow(i).getValue("BATCH_SEQ"));
        	parmTable.addData("BATCH_NO",parm.getRow(i).getValue("BATCH_NO"));
        	parmTable.addData("VALID_DATE",parm.getRow(i).getTimestamp("VALID_DATE"));
        	parmTable.addData("MAN_CODE",parm.getRow(i).getValue("MAN_CODE"));
        	parmTable.addData("SEQMAN_FLG",parm.getRow(i).getValue("SEQMAN_FLG"));
        	parmTable.addData("VALIDATE_FLG",parm.getRow(i).getValue("VALIDATE_FLG"));
        	parmTable.addData("RFID",parm.getRow(i).getValue("RFID"));
        }  
    	
    	if(parm.getCount("INV_CODE")>0){
        	table_d.setParmValue(parmTable);
        	this.setValue("BARCODE", "");
    	}else{
    		this.messageBox("�����벻����");   
    	}
    }
   
    /**  
     * ��շ���   
     */
    public void onClear() { 
    	//URGENT_FLG������ע��
        String clearString =   
            "START_DATE;END_DATE;TO_ORG_CODE_Q;"
            + "DISPENSE_NO_Q;DISPENSE_NO;DISPENSE_DATE;"
            + "TO_ORG_CODE;FROM_ORG_CODE;DISPENSE_USER;REMARK;"
            + "BARCODE;SELECT_ALL";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate(); 
        this.setValue("END_DATE",  
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE", 
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        table_m.setSelectionMode(0);  
        table_m.removeRowAll(); 
        table_d.setSelectionMode(0);   
        table_d.removeRowAll();     
        //��ʼ��tparm
        parmTable = new TParm();
    }


    /**  
     * ���淽��
     */
    public void onSave() {   
        if (!checkData()) {
            return;
        } 
     
        TParm parm = new TParm();
        // ���ⵥ������Ϣ 
        getDispenseMData(parm);
       
//        // ���ⵥϸ����Ϣ
//        getDispenseDData(parm);
    
        // ���ⵥ��Ź�����Ϣ
        getDispenseDDData(parm); 
       
//        // �����������
        getInvStockMData(parm);
////        // �����ϸ���� 
//       getInvStockDData(parm);     
        // ���¿����Ź�������     
        getInvStockDDData(parm); 

        // ��ѯ���ⷽʽ��N.���ȷ��ע�ǣ�Y.���⼴��⣩
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getInvSysParm()));
        //δ���
        ////��;----������;�ı���(DISCHECK_FLG,'Y') 
        	//ֱ�� ���� inv�����ű� Ȼ�� ��inv_stockdd�� ��org_code��һ��
        if (sysParm.getCount() > 0) { 
            TParm discheck_flg = new TParm(); 
            discheck_flg.setData("DISCHECK_FLG","N");
            parm.setData("DISCHECK_FLG", discheck_flg.getData());
            //�������ⵥ->N.���ȷ��ע�ǣ���;���ƣ�����Y.���⼴���->
            TParm result = TIOM_AppServer.executeAction(
                "action.inv.INVDispenseAction", "onInsertOutHigh", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }  
            this.messageBox("P0001");
            onClear();
        }
        else {  
            this.messageBox("û���趨���ʲ�����");
            return;
        }
             
        //��;----������;�ı���(DISCHECK_FLG,'N') 
//        if (this.getRadioButton("RadioButton2").isSelected()) {
//        	   if (sysParm.getCount() > 0) { 
//                   TParm discheck_flg = new TParm(); 
//                   discheck_flg.setData("DISCHECK_FLG",
//                                        sysParm.getValue("DISCHECK_FLG", 0));
//                   parm.setData("DISCHECK_FLG", discheck_flg.getData());
//                   //�������ⵥ->N.���ȷ��ע�ǣ�Y.���⼴���->
//                   TParm result = TIOM_AppServer.executeAction(
//                       "action.inv.INVDispenseAction", "onUpdate", parm);
//                   if (result == null || result.getErrCode() < 0) {
//                       this.messageBox("E0001");
//                       return;
//                   }  
//                   this.messageBox("P0001");
//                   onClear();
//               }
//               else {
//                   this.messageBox("û���趨���ʲ�����");
//                   return;
//               }
//        }
       
    }
 
    /**
     * ȡ�ó��ⵥ��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getDispenseMData(TParm parm) {
        TParm dispenseM = new TParm();
        

        TNull tnull = new TNull(Timestamp.class);
//		DISPENSE_NO , REQUEST_TYPE , REQUEST_NO, REQUEST_DATE , FROM_ORG_CODE ,&
//		TO_ORG_CODE , DISPENSE_DATE , DISPENSE_USER, URGENT_FLG , REMARK ,&
//		DISPOSAL_FLG, REN_CODE, FINA_FLG, CHECK_DATE, CHECK_USER, &
//		OPT_USER , OPT_DATE , OPT_TERM, IO_FLG
        // ���ⵥ��
        dispenseM.setData("DISPENSE_NO",
                          SystemTool.getInstance().getNo("ALL", "INV",
            "DISPENSE_NO", "No"));
        // �������벿��
        dispenseM.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
        // ���벿��
        dispenseM.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        // ��������
        dispenseM.setData("DISPENSE_DATE", this.getValue("DISPENSE_DATE"));
        // ������Ա     
        dispenseM.setData("DISPENSE_USER", Operator.getID());
    


      
        // ��������
        dispenseM.setData("REQUEST_TYPE","REQ");
        dispenseM.setData("REQUEST_NO","");
        // ��������
        dispenseM.setData("REQUEST_DATE", tnull);  
        dispenseM.setData("URGENT_FLG", "");   
        // ��ע
        dispenseM.setData("REMARK", this.getValueString("REMARK"));
        dispenseM.setData("DISPOSAL_FLG", "");
        dispenseM.setData("REN_CODE", "");     
        dispenseM.setData("FINA_FLG", "Y");
          
        dispenseM.setData("CHECK_DATE", SystemTool.getInstance().getDate());
        dispenseM.setData("CHECK_USER", Operator.getID());
        // OPT
        dispenseM.setData("OPT_USER", Operator.getID());
        dispenseM.setData("OPT_DATE", SystemTool.getInstance().getDate());
        dispenseM.setData("OPT_TERM", Operator.getIP());
        // ������� 2������
        dispenseM.setData("IO_FLG", "2");   
        System.out.println("DISPENSE_M"+dispenseM);   
        parm.setData("DISPENSE_M", dispenseM.getData());
        return parm;
    }

    /**
     * ȡ�ó��ⵥϸ������
     * @param parm TParm   
     * @return TParm
     */
    private TParm getDispenseDData(TParm parm) {
        TParm dispenseD = new TParm();
        int count = 0;
        TNull tnull = new TNull(Timestamp.class);
        System.out.println("");   
        for (int i = 0; i < table_d.getRowCount(); i++) { 
        	  if ("N".equals(table_d.getItemString(i,"SELECT_FLG"))) {
                  continue; 
              }          

            // ���ⵥ��    
            dispenseD.addData("DISPENSE_NO",
                              parm.getParm("DISPENSE_M").getValue("DISPENSE_NO"));
            // ���ⵥ���
            dispenseD.addData("SEQ_NO", count);
            count++;
            // �������    
            dispenseD.addData("BATCH_SEQ",  
                              table_d.getParmValue().getInt("BATCH_SEQ", i));
            // ���ʴ���
            dispenseD.addData("INV_CODE",
                              table_d.getParmValue().getValue("INV_CODE", i));
            // �������
            dispenseD.addData("INVSEQ_NO",
                              table_d.getParmValue().getInt("INVSEQ_NO", i));
            // ��Ź���ע��
            dispenseD.addData("SEQMAN_FLG",
                              table_d.getParmValue().getValue("SEQMAN_FLG", i));
            // ����
            dispenseD.addData("QTY", table_d.getItemDouble(i, "QTY"));
            // ��λ
            dispenseD.addData("DISPENSE_UNIT", 
                              table_d.getParmValue().getValue("DISPENSE_UNIT",
                i));
            // �ɱ���
            dispenseD.addData("COST_PRICE",
                              table_d.getItemDouble(i, "CONTRACT_PRICE")); 
            // �������
            dispenseD.addData("REQUEST_SEQ",  
                              table_d.getParmValue().getInt("REQUEST_SEQ", i));
            // ����
            dispenseD.addData("BATCH_NO",
                              table_d.getParmValue().getValue("BATCH_NO", i));
            // Ч�� VALID_DATE
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                dispenseD.addData("VALID_DATE", tnull);
            }
            else { 
                dispenseD.addData("VALID_DATE",
                                  TypeTool.getTimestamp(table_d.getParmValue().
                    getTimestamp("VALID_DATE", i)));
            }
            // ȡ������ 
            dispenseD.addData("DISPOSAL_FLG", "N");
            // OPT
            dispenseD.addData("OPT_USER", Operator.getID());
            dispenseD.addData("OPT_DATE", SystemTool.getInstance().getDate());
            dispenseD.addData("OPT_TERM", Operator.getIP());
            // ������� 2������
            dispenseD.addData("IO_FLG", "2");  
        }
        System.out.println("dispenseD"+dispenseD); 
        parm.setData("DISPENSE_D", dispenseD.getData());
        return parm;
    } 

    
    
    
    /**
     * ȡ�ó��ⵥϸ������
     * @param parm TParm   
     * @return TParm
     */
    private TParm getDispenseDDData(TParm parm) {
        TParm dispenseDD = new TParm();
        int count = 0;
        TNull tnull = new TNull(Timestamp.class);
        for (int i = 0; i < table_d.getRowCount(); i++) { 
        	  if ("N".equals(table_d.getItemString(i,"SELECT_FLG"))) {
                  continue; 
              }            
        	  //SELECT_FLG;INV_CODE;INV_CHN_DESC;RFID;INVSEQ_NO;DESCRIPTION;QTY;REQUEST_QTY;
        	  //ACTUAL_QTY;DISPENSE_UNIT;COST_PRICE;SUM_AMT;BATCH_SEQ;BATCH_NO;VALID_DATE;
        	  //MAN_CODE;SEQMAN_FLG;VALIDATE_FLG
              // ���ⵥ��    
        	  dispenseDD.addData("DISPENSE_NO",
                              parm.getParm("DISPENSE_M").getValue("DISPENSE_NO"));
              // ���ⵥ���
        	  dispenseDD.addData("SEQ_NO", count);
              count++;
              // ���ⵥ��ϸ���
        	  dispenseDD.addData("DDSEQ_NO", count);
              count++;
              // �������    
              dispenseDD.addData("BATCH_SEQ",  
                              table_d.getParmValue().getInt("BATCH_SEQ", i));
              // ���ʴ���
              dispenseDD.addData("INV_CODE",
                              table_d.getParmValue().getValue("INV_CODE", i));
              // RFID
              dispenseDD.addData("RFID",
                              table_d.getParmValue().getValue("RFID", i));
//            // �������
//            dispenseDD.addData("INVSEQ_NO",
//                              table_d.getParmValue().getInt("INVSEQ_NO", i));
            // ��Ź���ע��  
            dispenseDD.addData("SEQMAN_FLG",
                              table_d.getParmValue().getValue("SEQMAN_FLG", i));
            // ����
            dispenseDD.addData("QTY", table_d.getItemDouble(i, "QTY"));
            // ��λ
            dispenseDD.addData("STOCK_UNIT", 
                              table_d.getParmValue().getValue("DISPENSE_UNIT",
                i));
            // �ɱ���
            dispenseDD.addData("UNIT_PRICE",  
                              table_d.getItemDouble(i, "CONTRACT_PRICE")); 
            // ����
            dispenseDD.addData("BATCH_NO",
                              table_d.getParmValue().getValue("BATCH_NO", i));  
            // Ч�� VALID_DATE
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
            	dispenseDD.addData("VALID_DATE", tnull);
            }
            else { 
            	dispenseDD.addData("VALID_DATE",
                                  TypeTool.getTimestamp(table_d.getParmValue().
                    getTimestamp("VALID_DATE", i)));
            }
            // ȡ������ 
            dispenseDD.addData("DISPOSAL_FLG", "N");
            // OPT
            dispenseDD.addData("OPT_USER", Operator.getID());
            dispenseDD.addData("OPT_DATE", SystemTool.getInstance().getDate());
            dispenseDD.addData("OPT_TERM", Operator.getIP());
            // ������� 2������
            dispenseDD.addData("IO_FLG", "2");  
        }
        //System.out.println("dispenseDD"+dispenseDD);   
        parm.setData("DISPENSE_DD", dispenseDD.getData());
        return parm;
    } 
    

    /**
     * ȡ�ÿ����������  
     * @param parm TParm
     * @return TParm
     */ 
    public TParm getInvStockMData(TParm parm) {
    	//����
        TParm stockOutM = new TParm();
        //���
        TParm stockInM = new TParm();
        // ���ⲿ�� 
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = "";
//        REQUEST_NO
//        REQUEST_DATE
//        REQUEST_TYPE
        out_org_code = this.getValueString("FROM_ORG_CODE");
        in_org_code = this.getValueString("TO_ORG_CODE");
        double qty = 0;
        Map map = new HashMap(); 
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            else    
            	if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            else {
                inv_code = table_d.getParmValue().getValue("INV_CODE", i);
                qty = table_d.getItemDouble(i, "QTY");
                if (map.isEmpty()) { 
                    map.put(inv_code, qty);  
                }   
                else {
                    if (map.containsKey(inv_code)) {   
                        qty += TypeTool.getDouble(map.get(inv_code));
                        map.put(inv_code, qty);
                    }
                    else {
                        map.put(inv_code, qty);
                    }
                }
            }
        }

        Set set = map.keySet();  
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            stockOutM.addData("ORG_CODE", out_org_code);
            inv_code = TypeTool.getString(iterator.next());
            stockOutM.addData("INV_CODE", inv_code);
            stockOutM.addData("STOCK_QTY", TypeTool.getDouble(map.get(inv_code)));
            stockOutM.addData("OPT_USER", Operator.getID());
            stockOutM.addData("OPT_DATE", SystemTool.getInstance().getDate());
            stockOutM.addData("OPT_TERM", Operator.getIP());
         
                TParm stockParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvStockM(in_org_code, inv_code)));
                TParm baseParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvBase(inv_code)));
                if (stockParm == null || stockParm.getCount() <= 0) {
                	//INSERT INTO INV_STOCKM( 
                	//ORG_CODE, INV_CODE, REGION_CODE, DISPENSE_FLG, DISPENSE_ORG_CODE,
                	//STOCK_FLG, MATERIAL_LOC_CODE, SAFE_QTY, MIN_QTY, MAX_QTY, ECONOMICBUY_QTY,
                	//STOCK_QTY, MM_USE_QTY, AVERAGE_DAYUSE_QTY, STOCK_UNIT, 
                	//OPT_USER, OPT_DATE, OPT_TERM,BASE_QTY) 
                    stockInM.addData("TYPE", "INSERT"); //��������
                    stockInM.addData("ORG_CODE", in_org_code);
                    stockInM.addData("INV_CODE", inv_code);
                    stockInM.addData("REGION_CODE", Operator.getRegion());
                    stockInM.addData("DISPENSE_FLG", "N");
                    stockInM.addData("DISPENSE_ORG_CODE", "");
                    
                    stockInM.addData("STOCK_FLG", "N");
                    stockInM.addData("MATERIAL_LOC_CODE", "");
                    stockInM.addData("SAFE_QTY", 0);
                    stockInM.addData("MIN_QTY", 0);
                    stockInM.addData("MAX_QTY", 0);
                    stockInM.addData("ECONOMICBUY_QTY", 0);
                    
                    stockInM.addData("STOCK_QTY",
                                     TypeTool.getDouble(map.get(inv_code)));
                    stockInM.addData("MM_USE_QTY", 0);
                    stockInM.addData("AVERAGE_DAYUSE_QTY", 0);
                    stockInM.addData("STOCK_UNIT",
                                     baseParm.getValue("DISPENSE_UNIT",0));//========pangben modify 2011829
                    stockInM.addData("OPT_USER", Operator.getID());
                    stockInM.addData("OPT_DATE",
                                     SystemTool.getInstance().getDate());
                    stockInM.addData("OPT_TERM", Operator.getIP());
                    stockInM.addData("BASE_QTY", 0);
                }
                else {
                    stockInM.addData("TYPE", "UPDATE"); //��������
                    stockInM.addData("ORG_CODE", in_org_code);
                    stockInM.addData("INV_CODE", inv_code);
                    stockInM.addData("STOCK_QTY",
                                     TypeTool.getDouble(map.get(inv_code)));
                    stockInM.addData("OPT_USER", Operator.getID());
                    stockInM.addData("OPT_DATE",
                                     SystemTool.getInstance().getDate());
                    stockInM.addData("OPT_TERM", Operator.getIP());
                }
            
        }
        parm.setData("STOCK_OUT_M", stockOutM.getData());
        parm.setData("STOCK_IN_M", stockInM.getData());
        return parm;
    }
    
    
    
    /**
     * ȡ�ÿ����ϸ������(INV_STOCKD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertInvStockDData(TParm parm) {
        TParm stockD = new TParm();
        TParm parmD = parm.getParm("VER_D");
        String org_code ="";
        if (getCheckBox("CON_FLG").isSelected()) {
        	 org_code = this.getValueString("CON_ORG");
		}else {
			 org_code = this.getValueString("ORG_CODE");
		}
    
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        String batch_no = "";  
        String valid_date = "";
        int batch_seq = 0;
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            batch_no = parmD.getValue("BATCH_NO", i);
            valid_date = parmD.getValue("VALID_DATE", i);
            TParm stockDParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBatchSeq(org_code, inv_code, batch_no, valid_date)));
            if (stockDParm.getCount("BATCH_SEQ") > 0) {
                stockD.addData("FLG", "UPDATE");
                batch_seq = stockDParm.getInt("BATCH_SEQ", i);
            }
            else {
                stockD.addData("FLG", "INSERT");
                // ץȡ���BATCH_SEQ+1
                TParm batchSeqParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvStockMaxBatchSeq(org_code, inv_code)));
//                System.out.println("===========bat====="+org_code+"00"+inv_code);
//                System.out.println("===========bat====="+batchSeqParm);
                if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
                    batch_seq = 1;
                }
                else {
                    batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
                }
            }
            stockD.addData("ORG_CODE", org_code);
            stockD.addData("INV_CODE", inv_code);
            stockD.addData("BATCH_SEQ", batch_seq);
            stockD.addData("REGION_CODE", Operator.getRegion());
            stockD.addData("BATCH_NO", parmD.getValue("BATCH_NO", i));
            stockD.addData("VALID_DATE", parmD.getData("VALID_DATE", i));
          //  stockD.addData("STOCK_QTY", parmD.getDouble("IN_QTY", i));
            if (getCheckBox("CON_FLG").isSelected()) {
            	  stockD.addData("STOCK_QTY", parmD.getDouble("IN_QTY", i));
			}else {
				  stockD.addData("STOCK_QTY", "0");
			}
          
            stockD.addData("LASTDAY_TOLSTOCK_QTY", 0);
           // stockD.addData("DAYIN_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("DAYIN_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("DAYOUT_QTY", 0);
            stockD.addData("DAY_CHECKMODI_QTY", 0);
            stockD.addData("DAY_VERIFYIN_QTY", parmD.getDouble("IN_QTY", i));
            stockD.addData("DAY_VERIFYIN_AMT",
                           parmD.getDouble("QTY", i) *
                           parmD.getDouble("UNIT_PRICE", i));
            stockD.addData("GIFTIN_QTY", parmD.getDouble("GIFT_QTY", i));
            stockD.addData("DAY_REGRESSGOODS_QTY", 0);
            stockD.addData("DAY_REGRESSGOODS_AMT", 0);
            stockD.addData("DAY_REQUESTIN_QTY", 0);
            stockD.addData("DAY_REQUESTOUT_QTY", 0);
            stockD.addData("DAY_CHANGEIN_QTY", 0);  
            stockD.addData("DAY_CHANGEOUT_QTY", 0);
            stockD.addData("DAY_TRANSMITIN_QTY", 0);  
            stockD.addData("DAY_TRANSMITOUT_QTY", 0);
            stockD.addData("DAY_WASTE_QTY", 0);
            stockD.addData("DAY_DISPENSE_QTY", 0);
            stockD.addData("DAY_REGRESS_QTY", 0);
            stockD.addData("FREEZE_TOT", 0);
            stockD.addData("UNIT_PRICE", parmD.getDouble("UNIT_PRICE", i));
            stockD.addData("STOCK_UNIT", parmD.getValue("STOCK_UNIT", i));
            stockD.addData("OPT_USER", Operator.getID()); 
            stockD.addData("OPT_DATE", date);
            stockD.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("STOCK_D", stockD.getData());
        return parm;
    }


    /**
     * ȡ�ÿ����ϸ����
     * @param parm TParm  
     * @return TParm
     */
    public TParm getInvStockDData(TParm parm) {
        TParm stockOutD = new TParm();
        //TParm stockInD = new TParm();
        // ���ⲿ��
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = ""; 
        out_org_code = this.getValueString("FROM_ORG_CODE");
        in_org_code = this.getValueString("TO_ORG_CODE");

        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
                inv_code = table_d.getParmValue().getValue("INV_CODE", i);
                    stockOutD.addData("ORG_CODE", in_org_code);
                    stockOutD.addData("INV_CODE", inv_code);
                    stockOutD.addData("BATCH_SEQ",
                                      table_d.getParmValue().
                                      getInt("BATCH_SEQ", i));
                    stockOutD.addData("STOCK_QTY",
                                      table_d.getItemDouble(i, "QTY"));
                    stockOutD.addData("OPT_USER", Operator.getID());
                    stockOutD.addData("OPT_DATE",
                                      SystemTool.getInstance().getDate());
                    stockOutD.addData("OPT_TERM", Operator.getIP());
            }

        parm.setData("STOCK_OUT_D", stockOutD.getData());
        return parm; 
    }
    
    
    /**
     * ȡ�ÿ����ϸ����
     * @param parm TParm  
     * @return TParm
     */
    public TParm getInvStockDDData(TParm parm) {
        TParm stockOutDD = new TParm();
        //TParm stockInD = new TParm();
        // ���ⲿ��
        String out_org_code = "";
        String in_org_code = "";
        String inv_code = ""; 
        out_org_code = this.getValueString("FROM_ORG_CODE");
        in_org_code = this.getValueString("TO_ORG_CODE");

        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;  
            }
                inv_code = table_d.getParmValue().getValue("INV_CODE", i);
                    stockOutDD.addData("ORG_CODE", in_org_code); 
                    //stockOutD.addData("WAIT_ORG_CODE", out_org_code); 
                    stockOutDD.addData("STOCK_QTY",
                                      table_d.getItemDouble(i, "QTY"));
                    stockOutDD.addData("RFID",
                                      table_d.getItemData(i, "RFID"));
                    stockOutDD.addData("OPT_USER", Operator.getID());  
                    stockOutDD.addData("OPT_DATE",
                                      SystemTool.getInstance().getDate());
                    stockOutDD.addData("OPT_TERM", Operator.getIP());
            }
        //System.out.println("stockOutDD:"+stockOutDD);
        parm.setData("STOCK_DD", stockOutDD.getData());
        return parm; 
    }


    

    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() { 
    	//ֱ�ӽ����ı����ӹ���¼�
        table_d.acceptText();
        if (table_d.getRowCount() < 1) { 
            this.messageBox("û�г�����Ϣ");  
            return false;
        }
        
        
        boolean flg = false;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    flg = true;
                    break;
                }
            }
        }
        if (!flg) {
            this.messageBox("û�г�����Ϣ");
            return false;
        }
        String request_type = this.getValueString("REQUEST_TYPE");
        // ���ⲿ��
        String org_code = "";
        // ��������
        String inv_code = "";
        
        //����У�� ͬһ������rfid�����ظ�ɨ��

        TParm result = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
        	
            inv_code = table_d.getParmValue().getValue("INV_CODE", i);
            if (!"".equals(inv_code)) {
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }  
                if (table_d.getItemDouble(i, "QTY") >0) {
                   
                     
                }else {
                	this.messageBox("������������С�ڻ����0");
                	return false;
				}
                
                if ("REQ".equals(request_type) || "GIF".equals(request_type)) {
                    // ����,����
                    org_code = this.getValueString("FROM_ORG_CODE");
                }
                else if ("RET".equals(request_type) ||
                         "WAS".equals(request_type)) {
                    // �˿�,����
                    org_code = this.getValueString("TO_ORG_CODE");
                }  
                TParm stockParm = new TParm(); 
                //������Ź�����D��У��
                if ("N".equals(table_d.getParmValue().getValue("SEQMAN_FLG", i))) {
                    // ��ѯ���
                    stockParm.setData("ORG_CODE", org_code);
                    stockParm.setData("INV_CODE", inv_code);  
//                    stockParm.setData("BATCH_SEQ",
//                                      table_d.getItemData(i, "BATCH_SEQ"));
//                    result = InvStockDTool.getInstance().onQueryStockQty(
//                        stockParm);
                    result =  InvStockMTool.getInstance().getStockQty(stockParm);
                    if (result == null || result.getCount() <= 0 ||
                        result.getDouble("SUM(STOCK_QTY)", 0) <
                        table_d.getItemDouble(i, "QTY")) {
                        this.messageBox("����:" +
                                        table_d.getItemString(i, "INV_CHN_DESC") +
                                        "��治��, ��ǰ�����Ϊ" +
                                        result.getDouble("STOCK_QTY", 0));
                        return false;
                    }  
                }
            }
        }
        return true;
    }
    
    
   


    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();  
        //����״̬ 
        //���뵥��������� ״̬
        parm.setData("TYPE","DISPENSE_OUT");  
        String con = "";
        // ��ѯʱ��    
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        	String s=getValueString("START_DATE").substring(0, 10).replaceAll("-", "");
    		String e=getValueString("END_DATE").substring(0, 10).replaceAll("-", "");
    		
        	String sTime=getValueString("START_DATE").substring(11, 19).replaceAll(":", "");
    		String eTime=getValueString("END_DATE").substring(11, 19).replaceAll(":", "");
            con = con + " AND DISPENSE_DATE BETWEEN TO_DATE('"+s+"+"+sTime+"','YYYYMMDDHH24MISS') " +
            		" AND TO_DATE('"+e+"+"+eTime+"','YYYYMMDDHH24MISS') ";
        }  
      
        // ���벿��
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
            parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE_Q"));
            con = con + " AND TO_ORG_CODE = '"+this.getValueString("TO_ORG_CODE_Q")+"' ";
        }
  
        // ���ⵥ��
        if (!"".equals(this.getValueString("DISPENSE_NO_Q"))) {
            parm.setData("DISPENSE_NO", this.getValueString("DISPENSE_NO_Q"));
            con = con + " AND DISPENSE_NO = '"+this.getValueString("DISPENSE_NO_Q")+"' ";
        }

        TParm inparm = new TParm();
        inparm.setData("DISPENSE_M", parm.getData());
        
        // ��ѯ
        String querySql = "SELECT DISPENSE_NO, REQUEST_TYPE, REQUEST_NO, REQUEST_DATE, "+
			              "FROM_ORG_CODE, TO_ORG_CODE, DISPENSE_DATE, DISPENSE_USER, "+
			              "URGENT_FLG, REMARK, DISPOSAL_FLG, CHECK_DATE, "+
			              "CHECK_USER, REN_CODE, FINA_FLG "+
      		              "FROM INV_DISPENSEM WHERE 1=1  "+
      		              con +  
                          "ORDER BY DISPENSE_NO,REQUEST_NO   ";
        //System.out.println("querySql:"+querySql);
        TParm queryParm = new TParm(TJDODBTool.getInstance().select(querySql));
//        TParm result = TIOM_AppServer.executeAction( 
//            "action.inv.INVDispenseAction", "onQueryMOut", inparm);
        if (queryParm == null || queryParm.getCount() <= 0) {   
            this.messageBox("û�в�ѯ����");
            table_m.removeRowAll();
            return;
        } 
        // ȫԺҩ�ⲿ����ҵ����
//        if (!request_all_flg) { 
//
//        } 
        table_m.setParmValue(queryParm);
        
        //���水ť����
        //������Ĭ��״̬
        callFunction("UI|save|setEnabled", false);
    }
    
    /** 
     * ɾ������
     */
    public void onDelete() {
    	//δ���״̬����ɾ��������ɾ������ɶ��
    	//������;��ʱ�����ɾ�������δ���״̬
    	if (this.getRadioButton("RadioButton3").isSelected()) {
             	
		//this.messageBox("ɾ���ɹ���"); 
    	  }
    }

    /**
     * ������(TABLE_M)�����¼�
     */
    public void onTableMClicked() { 
    	//���� ���� ��ť
        //��ȡ inv_dispensedd ����
        int row = table_m.getSelectedRow();
        if (row != -1) {
            table_d.setSelectionMode(0);  
            // ������Ϣ(TABLE��ȡ��)
            setValue("DISPENSE_NO", table_m.getItemString(row, "DISPENSE_NO"));
            Timestamp date = SystemTool.getInstance().getDate();
            table_m.getItemTimestamp(row, "DISPENSE_DATE");
            
            setValue("TO_ORG_CODE", table_m.getItemString(row, "TO_ORG_CODE"));
            setValue("FROM_ORG_CODE",
                     table_m.getItemString(row, "FROM_ORG_CODE"));
            setValue("DISPENSE_USER",
                     table_m.getItemString(row, "DISPENSE_USER"));
            setValue("REMARK", table_m.getItemString(row, "REMARK"));

            // ��ϸ��Ϣ
            TParm result = new TParm();   

            
//        	String sql = " SELECT 'Y' AS SELECT_FLG,B.INV_CODE,B.INV_CHN_DESC,A.INVSEQ_NO,B.DESCRIPTION,'1' AS QTY," +
//			" '1' AS REQUEST_QTY,B.DISPENSE_UNIT,B.COST_PRICE,'0' AS SUM_AMT,A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE," +
//			" B.MAN_CODE,B.SEQMAN_FLG,B.VALIDATE_FLG,A.RFID " +   
//			" FROM INV_STOCKDD A,INV_BASE B  " +
//			" WHERE A.INV_CODE = B.INV_CODE" + 
//			" AND A.ORG_CODE = '"+fromOrgCode+"' " +
//			" AND A.RFID = '"+barCode+"' " +
//			" AND A.WAST_FLG = 'N'  ";
            
            
            String clicksql = " SELECT 'Y' AS SELECT_FLG,B.INV_CODE,B.INV_CHN_DESC,A.INVSEQ_NO,B.DESCRIPTION,'1' AS QTY," +
			" '1' AS REQUEST_QTY,B.DISPENSE_UNIT,B.COST_PRICE,'0' AS SUM_AMT,A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE," +
			" B.MAN_CODE,B.SEQMAN_FLG,B.VALIDATE_FLG,A.RFID " +   
            		" FROM INV_DISPENSEM M,INV_DISPENSEDD D ,INV_STOCKDD A,INV_BASE B" +
            		" WHERE M.DISPENSE_NO = D.DISPENSE_NO" +
    		" AND M.DISPENSE_NO =  '"+table_m.getItemString(row, "DISPENSE_NO")+"' AND A.RFID =D.RFID AND  A.INV_CODE = B.INV_CODE ";
            //System.out.println("clicksql:"+clicksql);
            result = new TParm(TJDODBTool.getInstance().select(clicksql));
              
            table_d.removeRowAll();
            table_d.setParmValue(result);   
                  
            if (result == null || result.getCount("RFID") <= 0) {
                this.messageBox("û��������ϸ"); 
                return;   
            }  

        }
    }  
   

    /**
     * ȫѡ�¼� 
     */
    public void onSelectAll() {
        String flg = "Y";
        if (getCheckBox("SELECT_ALL").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        //D��
        for (int i = 0; i < table_d.getRowCount(); i++) {
            table_d.setItem(i, "SELECT_FLG", flg);
        }  
  
    }
    
   

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj  
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table������
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if ("Y".equals(table_d.getParmValue().getValue("SEQMAN_FLG", row))) {
                this.messageBox("�����޸���Ź������ʵ�����");
                return true;
            }
            else {
                if (qty <= 0) {
                    this.messageBox("������������С�ڻ����0");
                    return true;
                }
                else if (qty >
                         (table_d.getItemDouble(row, "REQUEST_QTY") -
                          table_d.getItemDouble(row, "ACTUAL_QTY"))) {
                    this.messageBox("�����������ܴ�����������");
                    return true;
                } 
                else {
                    // ������
                    table_d.setItem(row, "SUM_AMT", qty *
                                    table_d.getItemDouble(row, "CONTRACT_PRICE"));
                    return false;
                }
            }
        }         
        return false;
    }

//    /**
//     * ����״̬����¼�
//     */
//    public void onChangeFinaFlg() { 
//    	//δ���ʱ�ܵ�����ⵥ��
//        TTextField dispense_no = this.getTextField("DISPENSE_NO_Q");
//        if (getRadioButton("RadioButton3").isSelected()) { 
//            dispense_no.setEnabled(true);
//            ( (TMenuItem) getComponent("delete")).setEnabled(true);  
//            ( (TMenuItem) getComponent("save")).setEnabled(true);
//            this.onClear();   
//        }                    
//        //���ʱ���ܵ�����ⵥ��  
//        else if (getRadioButton("RadioButton1").isSelected()) {
//            dispense_no.setEnabled(false); 
//            ( (TMenuItem) getComponent("save")).setEnabled(false);
//            ( (TMenuItem) getComponent("delete")).setEnabled(false); 
//            this.onClear();
//        }
//         
//    }
    private boolean flg = false;// �жϴ˲����Ƿ�֮ǰ�����ݹ�ѡȥ��

    

    /**
     * ��ʼ��������
     */
    private void initPage() {
        // ȫԺҩ�ⲿ����ҵ����
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }
        else {  
            request_all_flg = true;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        // ��ʼ����ѯ����
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') + 
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
      
        table_m = getTable("TABLE_M");    
        table_d = getTable("TABLE_D"); 
 
        // TABLE_Dֵ�ı��¼�
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue"); 
        //����״̬����¼�
        // onChangeFinaFlg();   
         //��ʼ��tparm
         parmTable = new TParm();
    } 
    
    
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		TParm tableParm = table_d.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("ORG_DESC", i+1); //��ֵ 
			result.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC", i)); 
			result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			result.addData("UNIT_CHN_DESC", tableParm.getValue("DISPENSE_UNIT", i)); 
			result.addData("QTY", tableParm.getValue("QTY", i)); 
			result.addData("COST_PRICE", tableParm.getValue("CONTRACT_PRICE", i)); 
			result.addData("AMT", tableParm.getValue("AMT", i)); 
		}
		result.setCount(tableParm.getCount()) ;    //���ñ��������
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "AMT");         
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,19);//�Ʊ�ʱ��
		String orgDesc = this.getValueString("ORG_CODE").length()>0?tableParm.getValue("ORG_DESC", 0):"ȫ��" ;
		String requestType = this.getValueString("REQUEST_TYPE").length()>0?this.getValueString("REQUEST_TYPE"):"ȫ��" ;
		printParm.setData("TITLE", "TEXT","���ⵥ") ;
		//printParm.setData("DATE", "TEXT","ͳ������:"+date) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�Ʊ���: " + Operator.getName());
		printParm.setData("ORG_DESC", "TEXT", "����: " + orgDesc);
		//printParm.setData("REQUEST_TYPE", "TEXT", "�������: " + requestType);
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDispenseOut.jhw",
				printParm);
	}
    
       
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return  
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }


    /**
     * �õ�TCheckBox����
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TTextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }


}
