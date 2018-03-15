package com.javahis.ui.inv;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut; 

import jdo.inv.INVSQL;
import jdo.inv.InvVerifyinDDTool;
import jdo.inv.InvVerifyinDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;  
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.javahis.ui.inv.FileParseExcel;

/**
 * <p>getInsertTableDDDatagetInsertTableDDData
 * Title: �������չ���Control
 * </p>
 *               
 * <p>
 * Description: �������չ���Control
 * </p> 
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fux 2014.4.11
 * @version 1.0
 */ 

public class INVVerifyinLowControl
    extends TControl {
    public INVVerifyinLowControl() {
    }

    private TTable table_m;

    private TTable table_d;  
    TLabel l;

    Map<String, String> map;
    
    
    Map<String, String> kindmap;

    // ����Ȩ��
    private boolean gift_flg = true;

    // ȫ������Ȩ��
    private boolean dept_flg = true;
    
   String disNo= "";
   
   String reqNo= "";
   Map<String, String> seqMap=new HashMap<String, String>();
    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʼ��������
        initPage();
    }

    /**
     * ��δ������ϸ
     */
    public void onExport() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("���ղ��Ų���Ϊ��");
            return;
        }   
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Object result = openDialog("%ROOT%\\config\\inv\\INVUnVerifyin.x", parm);
        if (result != null) {
        	
            TParm addParm = (TParm) result; 
            getTextFormat("CON_ORG").setValue(addParm.getData("CON_ORG"));
            System.out.println("addParm.getData('CON_ORG')==="+addParm.getData("CON_ORG"));
            if (addParm == null) {
                return;
            }
            // ��Ӧ����
            this.setValue("SUP_CODE", addParm.getValue("SUP_CODE", 0));
            // �ƻ�����
            this.setValue("STATIO_NO", addParm.getValue("STATIO_NO", 0));

            //System.out.println("-----" + addParm);  
            double purorder_qty = 0;
            double actual_qty = 0;
            double puroder_price = 0;
            String cString=  SystemTool.getInstance().getNo("ALL", "INV","RFID", "No");
            for (int i = 0; i < addParm.getCount("INV_CODE"); i++) {
            	if(!addParm.getValue("SEQMAN_FLG", i).equals("Y")){
                // ������
                purorder_qty = addParm.getDouble("PURORDER_QTY", i);
                actual_qty = addParm.getDouble("STOCKIN_SUM_QTY", i);

                int row = table_d.addRow();
                // ѡ��
                table_d.setItem(row, "SELECT_FLG", "N");
                // ��������
                table_d.setItem(row, "INV_CHN_DESC",
                                addParm.getValue("INV_CHN_DESC", i));
                // ���
                table_d.setItem(row, "DESCRIPTION",
                                addParm.getValue("DESCRIPTION", i));
                // ��������
                table_d.setItem(row, "VERIFIN_QTY", purorder_qty - actual_qty);
                // ������
                table_d.setItem(row, "GIFT_QTY", addParm.getData("GIFT_QTY", i));
                // ������λ
                table_d.setItem(row, "BILL_UNIT",
                                addParm.getValue("BILL_UNIT", i));
                // ���յ���  
                puroder_price = addParm.getDouble("PURORDER_PRICE", i);
                table_d.setItem(row, "UNIT_PRICE", puroder_price);
                // С��
                table_d.setItem(row, "VERIFYIN_AMT", StringTool.round(
                    puroder_price * (purorder_qty - actual_qty), 2));
                // �������
                table_d.setItem(row, "IN_QTY", StringTool.round(
                    addParm.getDouble("STOCK_QTY", i) *
                    addParm.getDouble("DISPENSE_QTY", i) /
                    addParm.getDouble("PURCH_QTY", i) *
                    (purorder_qty - actual_qty), 1));
                // ��ⵥλ
                table_d.setItem(row, "STOCK_UNIT",
                                addParm.getValue("DISPENSE_UNIT", i));
                // ����  ----fux modify  �ֹ���д 
//                table_d.setItem(row, "BATCH_NO",
//                		cString);
                table_d.setItem(row, "BATCH_NO",
                		"");
                // ��������
                table_d.setItem(row, "MAN_CODE",
                                addParm.getValue("MAN_CODE", i));
                // ��Ź���
                table_d.setItem(row, "SEQMAN_FLG",
                                addParm.getValue("SEQMAN_FLG", i));
                // Ч�ڹ��� 
                table_d.setItem(row, "VALIDATE_FLG",
                                addParm.getValue("VALIDATE_FLG", i));
                // ���ʴ���
                table_d.getParmValue().setData("INV_CODE", row,
                                               addParm.getValue("INV_CODE", i));
                // ���������
                table_d.getParmValue().setData("STESEQ_NO", row,
                                               addParm.getInt("SEQ_NO", i));
                // ��������
                table_d.getParmValue().setData("PURORDER_NO", row,
                                               addParm.
                                               getValue("PURORDER_NO", i));
                // ��������
                table_d.getParmValue().setData("PURORDER_QTY", row,
                                               purorder_qty);
                // ���ת����
                table_d.getParmValue().setData("STOCK_QTY", row,
                                               addParm.getDouble("STOCK_QTY", i));
                // ����ת����
                table_d.getParmValue().setData("DISPENSE_QTY", row,
                                               addParm.getDouble("DISPENSE_QTY",
                    i));
            }
            }
            this.getTextFormat("ORG_CODE").setEnabled(false);
            this.getTextFormat("SUP_CODE").setEnabled(false);
        }
    }
    
   
      //��ֵ�޸���
//    /**
//     * �򿪸���
//     */
//    public void onAddRfid() {
//        if ("".equals(getValueString("ORG_CODE"))) {
//            this.messageBox("���ղ��Ų���Ϊ��");
//            return;
//        }
//        table_dd.acceptText();
//        TParm parm = table_dd.getShowParmValue();
//        Object result = openDialog("%ROOT%\\config\\inv\\INVBarcodeAndRFID.x", parm);
//        if (result != null) {
//            TParm addParm = (TParm) result;
//            if (addParm == null) {
//                return;
//            }
//            Map<String, String> map=new HashMap<String, String>();
//            for (int i = 0; i < addParm.getCount("RFID"); i++) {
//                // ������
//            	String rfid=addParm.getValue("RFID", i);
//            	String code=addParm.getValue("ORGIN_CODE", i);
//            	map.put(rfid, code);
//
//            }
//            for (int i = 0; i < table_dd.getRowCount(); i++) {
//            	
//            	String rfid=table_dd.getItemData(i, "RFID").toString();
//            	table_dd.setItem(i, "ORGIN_CODE", map.get(rfid));
//            	
//				   
//			}
//        }
//    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        if (!dept_flg) {
            if ("".equals(this.getValueString("ORG_CODE_Q"))) {
                this.messageBox("��ѡ���ѯ����");
                return;
            }
        }
        TParm parm = new TParm();
        // ����״̬
        if (this.getRadioButton("UPDATE_FLG_B").isSelected()) {
            parm.setData("CHECK_FLG", "N");
        }
        else if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
            parm.setData("CHECK_FLG", "Y");
        }
        // ���յ���
        if (!"".equals(this.getValueString("VERIFYIN_NO").trim())) {
            parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO_Q"));
        }
        // ��ѯʱ��
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        // ���ղ���
        if (!"".equals(this.getValueString("ORG_CODE_Q"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE_Q"));
        }
        // ��������
        if (!"".equals(this.getValueString("SUP_CODE_Q"))) {
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE_Q"));
        }
        // �ƶ�����
        if (!"".equals(this.getValueString("CON_ORG_Q"))) {
            parm.setData("CON_ORG", this.getValueString("CON_ORG_Q"));
        }


        TParm inparm = new TParm();
        inparm.setData("VER_M", parm.getData());
        // ��ѯ
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVVerifyinAction", "onQueryM", inparm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
            table_m.removeRowAll();
            return;
        }
        //System.out.println("---" + result);
        table_m.setParmValue(result);
    }
    

    /**
     * ��շ���
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        onChangeCheckFlg();
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextFormat("ORG_CODE").setEnabled(true);
       
        l.setText("");
        String clearString =
            "START_DATE;END_DATE;ORG_CODE_Q;SUP_CODE_Q;VERIFYIN_NO_Q;"
            + "VERIFYIN_NO;VERIFYIN_DATE;ORG_CODE;SUP_CODE;INVOICE_NO;"
            + "INVOICE_AMT;STATIO_NO;SELECT_ALL;CON_ORG";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("VERIFYIN_DATE", date);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        
    }

    /**
     * ���淽��
     */
    public void onSave() {
        //���ݼ��
        if (!checkData()) {
            return;
        }
        
        
        disNo= SystemTool.getInstance().getNo("ALL", "INV",
                "DISPENSE_NO", "No");
       
       reqNo= SystemTool.getInstance().getNo("ALL", "INV",
               "INV_REQUEST", "No");

        TParm parm = new TParm();

        TParm result = new TParm();
        boolean flg=  getCheckBox("CON_FLG").isSelected();
        if (flg) {
        	parm.setData("MFLG", "A");
		}else {
			parm.setData("MFLG", "B");
		}
        
        if (table_m.getSelectedRow() < 0) {
        	messageBox("����");
            //1.ȡ��������������(TABLE_M)
            getInsertTableMData(parm);
            //2.ȡ��������ϸ����(TABLE_D)
            getInsertTableDData(parm);
                //4.�����ֵ�����ƶ���Ȩƽ��(INV_BASE)
                getInvBaseData(parm);
                //5.ȡ�ÿ����������(INV_STOCKM)
                result = getUpdateInvStockMData(parm);
                if (result == null) {
                    return;
                }
                //6.ȡ�ÿ����ϸ������(INV_STOCKD)
                getInsertInvStockDData(parm);
                //8.ȡ�ö�����ϸ������
                getInvPuroderDData(parm); 
                //9.ȡ�ö�������������
                getInvPurorderMData(parm);
                if (!getCheckBox("CON_FLG").isSelected()) {
                	 //�������쵥
                	 getRequestDData(parm); 
                     getRequestMData(parm);
                     //���ɳ��ⵥ
                     getDispenseDData(parm); 
                     getDispenseMData(parm);
                }
           
              
            // ��������  
             
                
            result = TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onInsert", parm);
            System.out.println("result"+result);
        }
        else {
            //1.ȡ��������������(TABLE_M)
            getUpdateTableMData(parm);
            //2.ȡ��������ϸ����(TABLE_D)
            getUpdateTableDData(parm);
                //4.�����ֵ�����ƶ���Ȩƽ��(INV_BASE)
                getInvBaseData(parm);
                //5.ȡ�ÿ����������(INV_STOCKM)
                result = getUpdateInvStockMData(parm);
                if (result == null) {
                    return;
                }
                //6.ȡ�ÿ����ϸ������(INV_STOCKD) 
                getInsertInvStockDData(parm);
                //8.ȡ�ö�����ϸ������
                getInvPuroderDData(parm);
                //9.ȡ�ö�������������
                getInvPurorderMData(parm);
            //System.out.println("parm---" + parm);
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVVerifyinAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");    
        onClear();
    }
    /**
     * ȡ�ó��ⵥ��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getDispenseMData(TParm parm) {
        TParm dispenseM = new TParm();
        // ���ⵥ��
        dispenseM.setData("DISPENSE_NO",disNo);
        // �������
        dispenseM.setData("REQUEST_TYPE", "REQ");
        // ���뵥��
        dispenseM.setData("REQUEST_NO", reqNo);
        // ��������
        dispenseM.setData("REQUEST_DATE", this.getValue("VERIFYIN_DATE"));
        // �������벿��
        dispenseM.setData("FROM_ORG_CODE", "011201");
        // ���벿��
        dispenseM.setData("TO_ORG_CODE", this.getValueString("CON_ORG"));
        // ��������
        dispenseM.setData("DISPENSE_DATE", this.getValue("VERIFYIN_DATE"));
        // ������Ա     
        dispenseM.setData("DISPENSE_USER", Operator.getID());
        // ����ע��
        dispenseM.setData("URGENT_FLG", "N");
        // ��ע
        dispenseM.setData("REMARK", "");
        // ȡ������
        dispenseM.setData("DISPOSAL_FLG", "N");
        // ����ȷ������
        dispenseM.setData("CHECK_DATE", SystemTool.getInstance().getDate());
        // ����ȷ����Ա
        dispenseM.setData("CHECK_USER", Operator.getID());
        // ����ԭ��
        dispenseM.setData("REN_CODE", "R01");
        // �����ע��
        dispenseM.setData("FINA_FLG","0");  
        // OPT
        dispenseM.setData("OPT_USER", Operator.getID());
        dispenseM.setData("OPT_DATE", SystemTool.getInstance().getDate());
        dispenseM.setData("OPT_TERM", Operator.getIP());
        // ������� 2������
        dispenseM.setData("IO_FLG", "2");   
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
        for (int i = 0; i < table_d.getRowCount(); i++) { 
            // ���ⵥ��    
            dispenseD.addData("DISPENSE_NO",
                             disNo);
            // ���ⵥ���
            dispenseD.addData("SEQ_NO", count+1);
            count++;
            // �������    
            dispenseD.addData("BATCH_SEQ",  
                              seqMap.get(table_d.getParmValue().getValue("INV_CODE", i)));
            // ���ʴ���
            dispenseD.addData("INV_CODE",
                              table_d.getParmValue().getValue("INV_CODE", i));
            // �������
            dispenseD.addData("INVSEQ_NO",
                             "1");
            // ��Ź���ע��
            dispenseD.addData("SEQMAN_FLG",
                              "N");
            // ����
            dispenseD.addData("QTY", table_d.getItemDouble(i, "IN_QTY"));
            // ��λ
            dispenseD.addData("DISPENSE_UNIT", 
                              table_d.getParmValue().getValue("STOCK_UNIT",
                i));
            // �ɱ���
            dispenseD.addData("COST_PRICE",
                              table_d.getItemDouble(i, "UNIT_PRICE"));
            // �������
            dispenseD.addData("REQUEST_SEQ",
                              i+1);
            // ����
            dispenseD.addData("BATCH_NO",
            		table_d.getItemString(i, "BATCH_NO"));
            // Ч�� VALID_DATE
            
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                    "".equals(table_d.getItemString(i, "VALID_DATE"))) {
            	dispenseD.addData("VALID_DATE", tnull);
                }
                else {
                	dispenseD.addData("VALID_DATE", TypeTool.getTimestamp(table_d.
                        getItemTimestamp(i, "VALID_DATE")));
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
     * ȡ�����뵥��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestMData(TParm parm) {
        TParm inparm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        // ���뵥��
            inparm.setData("REQUEST_NO",reqNo
                           );
        // �������
        inparm.setData("REQUEST_TYPE", "REQ");
        // ��������
        inparm.setData("REQUEST_DATE", this.getValue("VERIFYIN_DATE"));
        // �������벿��
        inparm.setData("FROM_ORG_CODE", "011201");
        // ���벿��
        inparm.setData("TO_ORG_CODE",  this.getValueString("CON_ORG"));
        // ����ԭ��
        inparm.setData("REN_CODE", "R01");
        // ����ע��
        inparm.setData("URGENT_FLG", "N");
        // ��ע
        inparm.setData("REMARK", "");
        // ����״̬
        inparm.setData("FINAL_FLG", "Y");
        // ʵ�ʽ��
        inparm.setData("ACTUAL_AMT", this.getValue("INVOICE_AMT"));
        // OPT
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", date);
        inparm.setData("OPT_TERM", Operator.getIP());

        parm.setData("REQ_M", inparm.getData());
        return parm;
    }

    /**
     * ȡ�����뵥ϸ������
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestDData(TParm parm) {
        TParm inparm = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            // 1.���뵥��
            inparm.addData("REQUEST_NO",
                          reqNo);
            // 2.���
            inparm.addData("SEQ_NO", i+1);
            // 3.���ʴ���
            inparm.addData("INV_CODE",
                           table_d.getParmValue().getValue("INV_CODE", i));
            // 4.�������
            inparm.addData("INVSEQ_NO",
            		i+1);
            // 5.��������
            inparm.addData("QTY", table_d.getItemDouble(i, "IN_QTY"));
            // 6.�ۼƳ�����
            inparm.addData("ACTUAL_QTY", "0");
            // 10.����ע��
            inparm.addData("FINA_TYPE", "2");
            // 11,12,13 OPT
            inparm.addData("OPT_USER", Operator.getID());
            inparm.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
            inparm.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("REQ_D", inparm.getData());
        return parm;
    }


    /**
     * ɾ������
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO"));
        if (row_d >= 0) {
            // ɾ�����յ�ϸ��
            if ("".equals(this.getValueString("VERIFYIN_NO"))) {
                table_d.removeRow(row_d);
                return;
            }
            else if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
                parm.setData("SEQ_NO",
                             table_d.getParmValue().getInt("SEQ_NO", row_d));
                result = InvVerifyinDTool.getInstance().onDelete(parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                table_d.removeRow(row_d);
                this.messageBox("ɾ���ɹ�");
            }
        }
        else if (row_m >= 0) {
            // ɾ�����յ�����
            if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����յ�", 2) == 0) {
                result = TIOM_AppServer.executeAction(
                    "action.inv.INVVerifyinAction", "onDelete", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("ɾ��ʧ��");
                    return;
                }
                table_m.removeRow(row_m);
                table_d.removeRowAll();
                this.messageBox("ɾ���ɹ�");
            }
        }
        else {
            this.messageBox("û��ѡ����");
            return;
        }
    }
    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * ��ӡ��ⵥ
     */
    // ��ӡ�����ʽ
    java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
    java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
    java.text.DecimalFormat df3 = new java.text.DecimalFormat(
        "##########0.000");
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");
    //luhai 2012-2-28 ����һλ�ĸ�ʽ��
    java.text.DecimalFormat df5 = new java.text.DecimalFormat(
    "##########0");

    /**
     * ��ӡ����
     */
    public void onPrint() {
    	printIn();
    	this.printOut();
    }
    

    
    private void printIn(){

    	TParm tableParm = table_d.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0.0000");
		DecimalFormat df3 = new DecimalFormat("0");
		double count = 0;
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("ORG_DESC", i+1); //��ֵ 
			String description = tableParm.getValue("DESCRIPTION", i);//���
			String invChnDesc = "";
			if("".equals(description)||description==null){
				//�ش������Ƴ���
				invChnDesc = tableParm.getValue("INV_CHN_DESC", i);
				invChnDesc = invChnDesc.length() > 17 ?invChnDesc.substring(0, 18):invChnDesc;
				result.addData("INV_CHN_DESC", invChnDesc); 
			}else{
				//�ش������Ƴ���
				invChnDesc = tableParm.getValue("INV_CHN_DESC", i)+"("+tableParm.getValue("DESCRIPTION", i)+")";
				invChnDesc = invChnDesc.length() > 17 ?invChnDesc.substring(0, 18):invChnDesc;
				result.addData("INV_CHN_DESC", invChnDesc); 
			} 
			//result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			TParm orgParm = new TParm(TJDODBTool.getInstance().select(this.getSQL(tableParm.getValue("BILL_UNIT", i))));
			result.addData("UNIT_CHN_DESC", orgParm.getValue("UNIT_CHN_DESC", 0)); 
//			result.addData("UNIT_CHN_DESC", tableParm.getValue("BILL_UNIT", i)); 
			result.addData("QTY", df3.format(tableParm.getDouble("VERIFIN_QTY", i))); 
			result.addData("COST_PRICE", df2.format(tableParm.getDouble("UNIT_PRICE", i))); 
			result.addData("AMT", df.format(tableParm.getDouble("VERIFYIN_AMT", i))); 
			count = count + Double.parseDouble(tableParm.getValue("VERIFYIN_AMT", i));
		}

		//�鿴�������Ƿ���6������������-�������������ǣ�(count+2)%6���� ����6-����������-duzhw
		int allCount = tableParm.getCount();
		int remainder = (allCount + 2)%6;//����
		int addCount = 0;
		if(remainder!=0){
			addCount = 6 - remainder;
			for (int i = 0; i < addCount; i++){
				result.addData("ORG_DESC", ""); //��ֵ 
				result.addData("INV_CHN_DESC", ""); 
				//result.addData("DESCRIPTION", ""); 
				result.addData("UNIT_CHN_DESC", ""); 
				result.addData("QTY", ""); 
				result.addData("COST_PRICE", ""); 
				result.addData("AMT", "");
			}
		}
		
		
		result.setCount(tableParm.getCount() + addCount) ;    //���ñ���������
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		//result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "AMT");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,10).replaceAll("-", "/");//�Ʊ�ʱ��
		String orgDesc = this.getTextFormat("ORG_CODE").getText();
		String requestType = this.getValueString("REQUEST_TYPE").length()>0?this.getValueString("REQUEST_TYPE"):"ȫ��" ;
		printParm.setData("TITLE", "TEXT","������ⵥ") ;
		printParm.setData("DATE", "TEXT","�������: "+this.getValueString("VERIFYIN_DATE").substring(0, 10).replaceAll("-", "/")) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�������: " + getTextFormat("CON_ORG").getText());
		printParm.setData("ORG_DESC", "TEXT", "����: " + orgDesc);
		printParm.setData("SUP_CODE", "TEXT", "��Ӧ�̣�" + this.getTextFormat("SUP_CODE").getText());
		printParm.setData("REQUEST_TYPE", "TEXT", "��ⵥ��: " + this.getValueString("VERIFYIN_NO"));
		printParm.setData("TOTAL", "TEXT", "�ܼ�: "+df.format(count));
		printParm.setData("M_USER", "TEXT", "�Ƶ���: "+Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDispenseOut.jhw",printParm);

    }

    
    
    
    private void printOut(){
    	TParm tableParm = table_d.getParmValue() ;
		TParm  result = new TParm() ;
		if(tableParm==null || tableParm.getCount()<=0){
			this.messageBox("�޴�ӡ����") ;
			return ;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0.0000");
		DecimalFormat df3 = new DecimalFormat("0");
		double count = 0;
		for(int i=0;i<tableParm.getCount();i++){
			result.addData("ORG_DESC", i+1); //��ֵ 
			String description = tableParm.getValue("DESCRIPTION", i);//���
			String invChnDesc = "";
			if("".equals(description)||description==null){
				//�ش������Ƴ���
				invChnDesc = tableParm.getValue("INV_CHN_DESC", i);
				invChnDesc = invChnDesc.length() > 17 ?invChnDesc.substring(0, 18):invChnDesc;
				result.addData("INV_CHN_DESC", invChnDesc); 
			}else{
				//�ش������Ƴ���
				invChnDesc = tableParm.getValue("INV_CHN_DESC", i)+"("+tableParm.getValue("DESCRIPTION", i)+")";
				invChnDesc = invChnDesc.length() > 17 ?invChnDesc.substring(0, 18):invChnDesc;
				result.addData("INV_CHN_DESC", invChnDesc); 
			} 
			//result.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i)); 
			TParm orgParm = new TParm(TJDODBTool.getInstance().select(this.getSQL(tableParm.getValue("BILL_UNIT", i))));
			result.addData("UNIT_CHN_DESC", orgParm.getValue("UNIT_CHN_DESC", 0)); 
//			result.addData("UNIT_CHN_DESC", tableParm.getValue("BILL_UNIT", i)); 
			result.addData("QTY", df3.format(tableParm.getDouble("VERIFIN_QTY", i))); 
			result.addData("COST_PRICE", df2.format(tableParm.getDouble("UNIT_PRICE", i))); 
			result.addData("AMT", df.format(tableParm.getDouble("VERIFYIN_AMT", i))); 
			count = count + Double.parseDouble(tableParm.getValue("VERIFYIN_AMT", i));
		}

		//�鿴�������Ƿ���6������������-�������������ǣ�(count+2)%6���� ����6-����������-duzhw
		int allCount = tableParm.getCount();
		int remainder = (allCount + 2)%6;//����
		int addCount = 0;
		if(remainder!=0){
			addCount = 6 - remainder;
			for (int i = 0; i < addCount; i++){
				result.addData("ORG_DESC", ""); //��ֵ 
				result.addData("INV_CHN_DESC", ""); 
				//result.addData("DESCRIPTION", ""); 
				result.addData("UNIT_CHN_DESC", ""); 
				result.addData("QTY", ""); 
				result.addData("COST_PRICE", ""); 
				result.addData("AMT", "");
			}
		}
		
		result.setCount(tableParm.getCount() + addCount) ;    //���ñ���������
		result.addData("SYSTEM", "COLUMNS", "ORG_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		//result.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");//����
		result.addData("SYSTEM", "COLUMNS", "COST_PRICE");
		result.addData("SYSTEM", "COLUMNS", "AMT");
		TParm printParm = new TParm() ;
		printParm.setData("TABLE", result.getData()) ; 
		String pDate = SystemTool.getInstance().getDate().toString().substring(0,10).replaceAll("-", "/");//�Ʊ�ʱ��
		String orgDesc = this.getTextFormat("ORG_CODE").getText();
		String requestType = this.getValueString("REQUEST_TYPE").length()>0?this.getValueString("REQUEST_TYPE"):"ȫ��" ;
		printParm.setData("TITLE", "TEXT","���ճ��ⵥ") ;
		printParm.setData("DATE", "TEXT","��������: "+this.getValueString("VERIFYIN_DATE").substring(0, 10).replaceAll("-", "/")) ;
		printParm.setData("P_DATE", "TEXT", "�Ʊ�ʱ��: " + pDate);
		printParm.setData("P_USER", "TEXT", "�������: " + getTextFormat("CON_ORG").getText());
		printParm.setData("ORG_DESC", "TEXT", "����: " + orgDesc);
		printParm.setData("SUP_CODE", "TEXT", "��Ӧ�̣�" + this.getTextFormat("SUP_CODE").getText());
		printParm.setData("REQUEST_TYPE", "TEXT", "���ⵥ��: " + disNo);
		printParm.setData("TOTAL", "TEXT", "�ܼ�: "+df.format(count));
		printParm.setData("M_USER", "TEXT", "�Ƶ���: "+Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDispenseOut.jhw",printParm);
    }
    
    

    private String getSQL(String unitCode){
    	return "SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '"+unitCode+"'";
    }
    
    /**
     * ��ӡ����
     */
    public void onPrintBarcode() {
    	if (getTable("TABLE_D").getRowCount()<=0) {
			messageBox("��ѡ����ϸ��");
		}
    	getTable("TABLE_D").acceptText();
    	TParm parm=getTable("TABLE_D").getParmValue();
//    	if (Operator.getDept().equals("011202")) {
//    		for (int i = 0; i < getTable("TABLE_DD").getRowCount(); i++) {
//        		TParm newParm=new TParm();
//        		if (!"Y".equals(table_dd.getItemData(i, "FLG"))) {
//    				continue;
//    			}
//        		newParm.setData(RFIDPrintUtils.PARM_CODE, parm.getData("RFID", i).toString().trim());
//        		newParm.setData(RFIDPrintUtils.PARM_NAME,  parm.getData("INV_CHN_DESC", i));
//        		newParm.setData(RFIDPrintUtils.PARM_PRFID, parm.getData("RFID", i).toString().trim());
//        		String cString="";
//        		if ( parm.getData("VALID_DATE", i)!=null&&!parm.getData("VALID_DATE", i).toString().equals("")) {
//        			cString=parm.getData("VALID_DATE", i).toString();
//        			cString=cString.substring(0,10);
//    			}
//        		newParm.setData(RFIDPrintUtils.PARM_VALID_DATE, cString);
//        		newParm.setData(RFIDPrintUtils.PARM_SPEC, parm.getData("DESCRIPTION", i));
//        		RFIDPrintUtils.send2LPT(newParm);
//        		try {
//    				Thread.sleep(2000);
//    			} catch (InterruptedException e) {
//    				// TODO Auto-generated catch block
//    				e.printStackTrace();
//    			}
//    		}
//        	
//		}else {
    	for (int j = 0; j < parm.getCount(); j++) {
    		 if ("Y".equals(table_d.getItemString(j, "SELECT_FLG"))) {
    				for (int i = 0; i < parm.getRow(j).getInt("QTY"); i++) {
    			        TParm reportParm = new TParm();
    			        reportParm.setData("PACK_DESC","TEXT","���ƣ�"+parm.getData("INV_CHN_DESC", i));
    			        reportParm.setData("PDATE_WORD","TEXT","���"+parm.getData("DESCRIPTION", i));
    			        reportParm.setData("PACK_DATE","TEXT","Ч�ڣ�"+parm.getData("VALID_DATE", i).toString().substring(0,10));
    			        reportParm.setData("PACK_CODE_SEQ_SEC","TEXT",  parm.getData("RFID", i).toString().trim());
    			        //���ô�ӡ����
    			        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSSBarCode.jhw", reportParm, true);
    				//	}
    				}	 
    		 }
		}

    }
    

    /**
     * ȫѡ����
     */
    public void onSelectAll() {
        String flg = "Y";
        if (getCheckBox("SELECT_ALL").isSelected()) {
            flg = "Y";
        }
        else {
            flg = "N";
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
            table_d.setItem(i, "SELECT_FLG", flg);
        }
        //�����ܽ��
        this.setValue("INVOICE_AMT", getSumAMT());
    }

    /**
     * ���ѡ�����¼�
     */
    public void onChangeCheckFlg() {
    	 getTextFormat("SUP_CODE").setEnabled(true);
         getTextFormat("ORG_CODE").setEnabled(true);
        
         l.setText("");
         String clearString =
             "START_DATE;END_DATE;ORG_CODE_Q;SUP_CODE_Q;VERIFYIN_NO_Q;"
             + "VERIFYIN_NO;VERIFYIN_DATE;ORG_CODE;SUP_CODE;INVOICE_NO;"
             + "INVOICE_AMT;STATIO_NO;SELECT_ALL;CON_ORG";
         this.clearValue(clearString);
         Timestamp date = StringTool.getTimestamp(new Date());
         this.setValue("END_DATE",
                       date.toString().substring(0, 10).replace('-', '/') +
                       " 23:59:59");
         this.setValue("START_DATE",
                       StringTool.rollDate(date, -7).toString().substring(0, 10).
                       replace('-', '/') + " 00:00:00");
         setValue("VERIFYIN_DATE", date);
         table_m.setSelectionMode(0);
         table_m.removeRowAll();
         table_d.setSelectionMode(0);
         table_d.removeRowAll();
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
        }
    }

    /**
     * �������(TABLE_M)�����¼�
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            getTextFormat("SUP_CODE").setEnabled(false);
            getTextFormat("ORG_CODE").setEnabled(false);
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                ( (TMenuItem) getComponent("delete")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("delete")).setEnabled(false);
            }
            table_d.setSelectionMode(0);
            // ������Ϣ(TABLE��ȡ��)
            setValue("VERIFYIN_NO", table_m.getItemString(row, "VERIFYIN_NO"));
            setValue("VERIFYIN_DATE", table_m.getItemTimestamp(row,
                "VERIFYIN_DATE"));
            setValue("ORG_CODE", table_m.getItemString(row, "VERIFYIN_DEPT"));
            setValue("SUP_CODE", table_m.getItemString(row, "SUP_CODE"));
            setValue("INVOICE_NO", table_m.getItemString(row, "INVOICE_NO"));
            setValue("INVOICE_AMT", table_m.getItemDouble(row, "INVOICE_AMT"));
            setValue("STATIO_NO", table_m.getItemString(row, "STATIO_NO"));
            setValue("CHECK_FLG", table_m.getItemString(row, "CHECK_FLG"));
            setValue("CON_FLG", table_m.getItemString(row, "CON_FLG"));
            setValue("CON_ORG", table_m.getItemString(row, "CON_ORG"));

            // ��ϸ��Ϣ
            TParm parm = new TParm();
            parm.setData("VERIFYIN_NO",
                         table_m.getItemString(row, "VERIFYIN_NO"));
            TParm result = InvVerifyinDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("û��������ϸ");
                return;
            }
            table_d.removeRowAll();
            table_d.setParmValue(result);

            //�����ܽ��
            this.setValue("INVOICE_AMT", getSumAMT());
        }
    }

    /**
     * ϸ�����(TABLE_D)�����¼�
     */
    public void onTableDClicked() {
        int row = table_d.getSelectedRow();
       String sqlString="select inv_code from INV_VERIFYIND where VERIFYIN_NO='"+this.getValueString("VERIFYIN_NO")+"' and SEQ_NO='"+table_d.getParmValue().getValue("SEQ_NO", row)+"'";
       TParm ss = new TParm(TJDODBTool.getInstance().select(sqlString)); 
       // l.setText(ss.getRow(0).getData("INV_CODE").toString());
         if ( ss.getRow(0).getData("INV_CODE")!=null&& ss.getRow(0).getData("INV_CODE").toString().length()>1) {
        	  callFunction(
            	      "UI|setSysStatus", new Object[] { 
            	    		  ss.getRow(0).getData("INV_CODE").toString() });
		}
      
       if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
            if ("Y".equals(table_d.getItemString(row, "SEQMAN_FLG"))) {
                TParm parm = new TParm();
                parm.setData("VERIFYIN_NO", this.getValueString("VERIFYIN_NO"));
                parm.setData("SEQ_NO",
                             table_d.getParmValue().getValue("SEQ_NO", row));
                TParm result = InvVerifyinDDTool.getInstance().onQuery(parm);
//                for (int i = 0; i < result.getCount("RIFD"); i++) {
//                	 result.setData("FLG", "Y");
//				}
               
                
                if (result == null || result.getCount("VERIFYIN_NO") <= 0) {
                    this.messageBox("û�й���ϸ������");
                    return;
                }
            }
        }
       
    }


    /**
     * ���ݼ��
     * @return boolean
     */
    private boolean checkData() {
        // ������Ϣ���
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("���ղ��Ų���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("SUP_CODE"))) {
            this.messageBox("�����̲���Ϊ��");
            return false;
        }

        // ϸ����Ϣ���
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
                break;
            }
        }
        if (flg) {
            this.messageBox("û��ѡ�е�ϸ��");
            return false;
        }

        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (table_d.getItemDouble(i, "VERIFIN_QTY") <= 0) {
                this.messageBox("������������С�ڻ����0");
                return false;
            }
            if (table_d.getItemDouble(i, "GIFT_QTY") < 0) {
                this.messageBox("������������С��0");
                return false;
            }
            if (table_d.getItemDouble(i, "UNIT_PRICE") <= 0) {
                this.messageBox("���յ��۲���С�ڻ����0");
                return false;
            }else {
            	
            	String sqlString="select CONTRACT_PRICE from inv_agent where inv_code='"+table_d.getParmValue().getValue("INV_CODE", i)+"'";
            	 TParm parmM=new TParm(TJDODBTool.getInstance().select(sqlString));
            	 if (new BigDecimal(parmM.getData("CONTRACT_PRICE", 0).toString()).doubleValue()!=table_d.getItemDouble(i, "UNIT_PRICE")) {
            		 this.messageBox("���յ��������ʽ����۲�ͬ����������⣡");
                     return false;
				}
            	
            	
			}
            if ("".equals(table_d.getItemString(i, "BATCH_NO"))) {
                this.messageBox("���Ų���Ϊ��");
                return false;
            }
            if ("Y".equals(table_d.getItemString(i, "VALIDATE_FLG")) &&
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                this.messageBox("��Ч�ڹ���������,Ч�ڲ���Ϊ��");
                return false;
            }
        }
        
        
        if (Operator.getDept().equals("011202")||Operator.getDept().equals("011203")) {
        	if (getCheckBox("CON_FLG").isSelected()==false) {
        		 this.messageBox("��ѡ��ֱ�����");
                 return false;
			}
			
		}
        return true;
    }

    /**
     * ȡ��������������(TABLE_M)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertTableMData(TParm parm) {
        TParm parm_M = new TParm();
        // ��������
        String verifyin_no = SystemTool.getInstance().getNo("ALL", "INV",
            "INV_VERIFYIN", "No");
        parm_M.setData("VERIFYIN_NO", verifyin_no);
        parm.setData("VERIFYIN_NO", verifyin_no);
        Timestamp date = SystemTool.getInstance().getDate();
        parm_M.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        parm_M.setData("VERIFYIN_DATE", this.getValue("VERIFYIN_DATE"));
        parm_M.setData("VERIFYIN_USER", Operator.getID());
        parm_M.setData("VERIFYIN_DEPT", this.getValueString("ORG_CODE"));
        parm_M.setData("INVOICE_NO", this.getValueString("INVOICE_NO"));
        parm_M.setData("INVOICE_DATE", date);
        parm_M.setData("INVOICE_AMT", this.getValueDouble("INVOICE_AMT"));
        parm_M.setData("INVOICE_AMT", this.getValueDouble("INVOICE_AMT"));
        parm_M.setData("STATIO_NO", this.getValueString("STATIO_NO"));
        parm_M.setData("CHECK_FLG",
                       "Y".equals(this.getValueString("CHECK_FLG")) ? "Y" : "N");
        parm_M.setData("OPT_USER", Operator.getID());
        parm_M.setData("OPT_DATE", date);
        parm_M.setData("OPT_TERM", Operator.getIP());
        if (getCheckBox("CON_FLG").isSelected()) {
        	parm_M.setData("CON_FLG", "Y");
        	parm_M.setData("CON_ORG", this.getValueString("CON_ORG"));
		}else {
			parm_M.setData("CON_FLG", "N");
        	parm_M.setData("CON_ORG", "");
		}
        parm.setData("VER_M", parm_M.getData());
        return parm;
    }

    /**
     * ȡ��������������(TABLE_M)
     * @param parm TParm
     * @return TParm
     */
    private TParm getUpdateTableMData(TParm parm) {
        TParm parm_M = new TParm();
        // ��������
        String verifyin_no = this.getValueString("VERIFYIN_NO");
        parm_M.setData("VERIFYIN_NO", verifyin_no);
        parm.setData("VERIFYIN_NO", verifyin_no);
        Timestamp date = SystemTool.getInstance().getDate();
        parm_M.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        parm_M.setData("VERIFYIN_DATE", this.getValue("VERIFYIN_DATE"));
        parm_M.setData("VERIFYIN_USER", Operator.getID());
        parm_M.setData("VERIFYIN_DEPT", this.getValueString("ORG_CODE"));
        parm_M.setData("INVOICE_NO", this.getValueString("INVOICE_NO"));
        parm_M.setData("INVOICE_DATE", date);
        parm_M.setData("INVOICE_AMT", this.getValueDouble("INVOICE_AMT"));
        parm_M.setData("STATIO_NO", this.getValueString("STATIO_NO"));
        parm_M.setData("CHECK_FLG",
                       "Y".equals(this.getValueString("CHECK_FLG")) ? "Y" : "N");
        parm_M.setData("OPT_USER", Operator.getID());
        parm_M.setData("OPT_DATE", date);
        parm_M.setData("OPT_TERM", Operator.getIP());
        if (getCheckBox("CON_FLG").isSelected()) {
        	parm_M.setData("CON_FLG", "Y");
        	parm_M.setData("CON_ORG", this.getValueString("CON_ORG"));
		}else {
			parm_M.setData("CON_FLG", "N");
        	parm_M.setData("CON_ORG", "");
		}
        parm.setData("VER_M", parm_M.getData());
        return parm;
    }


    /**
     * ȡ��������ϸ����(TABLE_D)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertTableDData(TParm parm) {
        table_d.acceptText();
        //System.out.println(table_d.getParmValue());
        TParm parm_D = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parm_D.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO")); 
            parm_D.addData("SEQ_NO", count);
            count++;
            parm_D.addData("INV_CODE", 
                           table_d.getParmValue().getValue("INV_CODE", i));
            String sql="select INV_KIND from INV_BASE where INV_CODE='"+table_d.getParmValue().getValue("INV_CODE", i)+"'";
            TParm parm2=new TParm(TJDODBTool.getInstance().select(sql));
            String c=parm2.getData("INV_KIND",0).toString();
            parm_D.addData("INV_KIND", c);
            parm_D.addData("QTY", table_d.getItemDouble(i, "VERIFIN_QTY"));
            parm_D.addData("GIFT_QTY", table_d.getItemDouble(i, "GIFT_QTY"));
            parm_D.addData("BILL_UNIT", table_d.getItemString(i, "BILL_UNIT"));
            parm_D.addData("IN_QTY", table_d.getItemDouble(i, "IN_QTY"));
            parm_D.addData("STOCK_UNIT", table_d.getItemString(i, "STOCK_UNIT"));
            parm_D.addData("UNIT_PRICE",
                           table_d.getItemDouble(i, "UNIT_PRICE"));
            parm_D.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                parm_D.addData("VALID_DATE", tnull);
            }
            else {
                parm_D.addData("VALID_DATE", TypeTool.getTimestamp(table_d.
                    getItemTimestamp(i, "VALID_DATE")));
            }
            parm_D.addData("PURORDER_NO",
                           table_d.getParmValue().getValue("PURORDER_NO", i));
            parm_D.addData("STESEQ_NO",
                           table_d.getParmValue().getInt("STESEQ_NO", i));
            parm_D.addData("REN_CODE", table_d.getItemString(i, "REN_CODE"));
            parm_D.addData("QUALITY_DEDUCT_AMT",
                           table_d.getItemDouble(i, "QUALITY_DEDUCT_AMT"));
            parm_D.addData("OPT_USER", Operator.getID());
            parm_D.addData("OPT_DATE", date);
            parm_D.addData("OPT_TERM", Operator.getIP());
            parm_D.addData("SEQMAN_FLG",
                           table_d.getItemString(i, "SEQMAN_FLG"));
        }
        parm.setData("VER_D", parm_D.getData());
        return parm;
    }

    /**
     * ȡ��������ϸ����(TABLE_D)
     * @param parm TParm
     * @return TParm
     */
    private TParm getUpdateTableDData(TParm parm) {
        table_d.acceptText();
        TParm parm_D = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parm_D.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
            parm_D.addData("SEQ_NO", table_d.getParmValue().getInt("SEQ_NO", i));
            parm_D.addData("INV_CODE",
                           table_d.getParmValue().getValue("INV_CODE", i));
            parm_D.addData("QTY", table_d.getItemDouble(i, "VERIFIN_QTY"));
            parm_D.addData("GIFT_QTY", table_d.getItemDouble(i, "GIFT_QTY"));
            parm_D.addData("BILL_UNIT", table_d.getItemString(i, "BILL_UNIT"));
            parm_D.addData("IN_QTY", table_d.getItemDouble(i, "IN_QTY"));
            parm_D.addData("STOCK_UNIT", table_d.getItemString(i, "STOCK_UNIT"));
            parm_D.addData("UNIT_PRICE",
                           table_d.getItemDouble(i, "UNIT_PRICE"));
            parm_D.addData("BATCH_NO", table_d.getItemString(i, "BATCH_NO"));
            if (table_d.getItemData(i, "VALID_DATE") == null ||
                "".equals(table_d.getItemString(i, "VALID_DATE"))) {
                parm_D.addData("VALID_DATE", tnull);
            }
            else {
                parm_D.addData("VALID_DATE", TypeTool.getTimestamp(table_d.
                    getItemTimestamp(i, "VALID_DATE")));
            }
            parm_D.addData("PURORDER_NO",
                           table_d.getParmValue().getValue("PURORDER_NO", i));
            parm_D.addData("STESEQ_NO",
                           table_d.getParmValue().getInt("STESEQ_NO", i));
            parm_D.addData("REN_CODE", table_d.getItemString(i, "REN_CODE"));
            parm_D.addData("QUALITY_DEDUCT_AMT",
                           table_d.getItemDouble(i, "QUALITY_DEDUCT_AMT"));
            parm_D.addData("OPT_USER", Operator.getID());
            parm_D.addData("OPT_DATE", date);
            parm_D.addData("OPT_TERM", Operator.getIP());
            parm_D.addData("SEQMAN_FLG",
                           table_d.getItemString(i, "SEQMAN_FLG"));
        }
        parm.setData("VER_D", parm_D.getData());
        return parm;
    }


    /**
     * ȡ��������Ź���ϸ������(TABLE_DD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertTableDDData(TParm parm) {
        TParm parm_DD = new TParm();
        TParm parm_D = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String sql = "";
        String valid_date = "";
        for (int i = 0; i < parm_D.getCount("INV_CODE"); i++) {
            if ("Y".equals(parm_D.getValue("SEQMAN_FLG", i))) {
                //INVSEQ_NO ץȡ����+1
                TParm invSeqNoParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getInvMaxInvSeqNo(parm_D.getValue("INV_CODE", i))));
                int invseq_no = 1;
                if (invSeqNoParm.getCount() > 0) {
                    invseq_no = invSeqNoParm.getInt("INVSEQ_NO", 0) + 1;
                }
                //�������ź�Ч��ȡ��BATCH_SEQ
                valid_date = TypeTool.getString(parm_D.getValue("VALID_DATE", i));
                if (!"".equals(valid_date) && valid_date.length() > 18) {
                    valid_date = parm_D.getValue("VALID_DATE", i).substring(0,
                        4) + parm_D.getValue("VALID_DATE", i).substring(5, 6)
                        + parm_D.getValue("VALID_DATE", i).substring(7, 8)
                        + parm_D.getValue("VALID_DATE", i).substring(9, 10)
                        + parm_D.getValue("VALID_DATE", i).substring(11, 13)
                        + parm_D.getValue("VALID_DATE", i).substring(14, 16);
                } 
                sql = INVSQL.getInvBatchSeq(getValueString("ORG_CODE"),
                                            parm_D.getValue("INV_CODE", i),
                                            parm_D.getValue("BATCH_NO", i),
                                            valid_date);
                TParm stockDParm = new TParm(TJDODBTool.getInstance().
                                               select(sql));
                
                int batch_seq = 1; 
                if (stockDParm.getCount("BATCH_SEQ") > 0) {
                    batch_seq = stockDParm.getInt("BATCH_SEQ", 0);
                }
                else {
                    // ץȡ���BATCH_SEQ+1
                    TParm batchSeqParm = new TParm(TJDODBTool.getInstance().
                        select(INVSQL.getInvStockMaxBatchSeq(getValueString(
                        "ORG_CODE"), parm_D.getValue("INV_CODE", i))));
                    if (batchSeqParm == null || batchSeqParm.getCount() <= 0) {
                        batch_seq = 1;
                    }
                    else {
                        batch_seq = batchSeqParm.getInt("BATCH_SEQ", 0) + 1;
                    }
                }   
                String kind= parm_D.getValue("INV_KIND", i);
                //fux modify 20140320  �й�RFID����
                if(kind.equals("08")){
                     kindmap.put(kind, "C");
                }else if(kind.equals("21")){
                	 kindmap.put(kind, "S");
                }else {  
                	 kindmap.put(kind, "A");
                }
                              
                String xString=kindmap.get(kind);
                for (int j = 0; j < parm_D.getDouble("QTY", i); j++) {
                    parm_DD.addData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
                    parm_DD.addData("SEQ_NO", parm_D.getInt("SEQ_NO", i));
                    parm_DD.addData("DDSEQ_NO", j);
                    parm_DD.addData("INV_CODE", parm_D.getValue("INV_CODE", i));
                    parm_DD.addData("INVSEQ_NO", invseq_no);
                    invseq_no++;
                    parm_DD.addData("BATCH_SEQ", batch_seq);
                    parm_DD.addData("BATCH_NO", parm_D.getValue("BATCH_NO", i));
                    if (parm_D.getData("VALID_DATE", i) == null ||
                        "".equals(parm_D.getData("VALID_DATE", i))) {
                        parm_DD.addData("VALID_DATE", tnull);
                    }
                    else {
                        parm_DD.addData("VALID_DATE", TypeTool.getTimestamp(
                            parm_D.getData("VALID_DATE", i)));
                    } 
                    parm_DD.addData("STOCK_UNIT",
                                    parm_D.getValue("STOCK_UNIT", i));
                    parm_DD.addData("UNIT_PRICE",
                                    parm_D.getDouble("UNIT_PRICE", i));
                    parm_DD.addData("OPT_USER", Operator.getID());
                    parm_DD.addData("OPT_DATE", date);
                    parm_DD.addData("OPT_TERM", Operator.getIP());
                  String cString=  SystemTool.getInstance().getNo("ALL", "INV","RFID", "No");
                  
                    parm_DD.addData("RFID",xString+cString);

                    
                } 
            }
        }
        parm.setData("VER_DD", parm_DD.getData());
        System.out.println("========"+ parm_DD.getData());
        return parm;
    }

    /**
     * ȡ�ÿ����������(INV_STOCKM)
     * @param parm TParm
     * @return TParm
     */
    private TParm getUpdateInvStockMData(TParm parm) {
        TParm stockM = new TParm();
        TParm parmD = parm.getParm("VER_D");
        String org_code ="";
        if (getCheckBox("CON_FLG").isSelected()) {
        	 org_code = this.getValueString("CON_ORG");
		}else {
			 org_code = this.getValueString("ORG_CODE");
		}
    
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            TParm stockMParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockM(org_code, inv_code)));
            if (stockMParm == null || stockMParm.getCount("INV_CODE") <= 0) {
                this.messageBox("û���趨�������");
                return null;
            }
            stockM.addData("ORG_CODE", org_code);
            stockM.addData("INV_CODE", inv_code);
            if (getCheckBox("CON_FLG").isSelected()) {
            	stockM.addData("STOCK_QTY", parmD.getDouble("IN_QTY", i));
			}else {
				stockM.addData("STOCK_QTY", "0");
			}
            
            stockM.addData("OPT_USER", Operator.getID());
            stockM.addData("OPT_DATE", date);
            stockM.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("STOCK_M", stockM.getData());
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
            
            
            //Ϊ��������ⵥbatch seq
            
            seqMap.put(inv_code, ""+batch_seq);
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
        System.out.println("stockD.getData()=========="+stockD.getData());
        parm.setData("STOCK_D", stockD.getData());
        return parm;
    }

    /**
     * ȡ�ÿ����Ź���ϸ������(INV_STOCKDD)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInsertInvStockDDData(TParm parm) {
        TParm stockDD = new TParm();
        TParm parmDD = parm.getParm("VER_DD");
        System.out.println("parmDD"+parm.getParm("VER_DD"));
        String org_code =""; 
        if (getCheckBox("CON_FLG").isSelected()) {
        	 org_code = this.getValueString("CON_ORG");
		}else {
			 org_code = this.getValueString("ORG_CODE");
		}
    
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmDD.getCount("INV_CODE"); i++) {
            stockDD.addData("INV_CODE", parmDD.getValue("INV_CODE", i));
            stockDD.addData("INVSEQ_NO", parmDD.getValue("INVSEQ_NO", i));
            stockDD.addData("REGION_CODE", Operator.getRegion());
            stockDD.addData("BATCH_SEQ", parmDD.getInt("BATCH_SEQ", i));
            stockDD.addData("ORG_CODE", org_code);
            stockDD.addData("BATCH_NO", parmDD.getValue("BATCH_NO", i));
            stockDD.addData("VALID_DATE", parmDD.getData("VALID_DATE", i));
            stockDD.addData("STOCK_QTY", 1);
            stockDD.addData("UNIT_PRICE", parmDD.getDouble("UNIT_PRICE", i));
            stockDD.addData("STOCK_UNIT", parmDD.getValue("STOCK_UNIT", i));
            stockDD.addData("CHECKTOLOSE_FLG", "N");
            stockDD.addData("WAST_FLG", "N");
            stockDD.addData("VERIFYIN_DATE", date);
            stockDD.addData("PACK_FLG", "N");
            stockDD.addData("ACTIVE_FLG", "");
            stockDD.addData("CABINET_ID", "");
            stockDD.addData("OPT_USER", Operator.getID());
            stockDD.addData("OPT_DATE", date);
            stockDD.addData("RFID", parmDD.getValue("RFID", i));
            stockDD.addData("OPT_TERM", Operator.getIP());
        } 
        System.out.println("stockDD.getData()=========="+stockDD.getData());
        parm.setData("STOCK_DD", stockDD.getData());
        return parm;
    }

    /**
     * ȡ�ö�����ϸ������
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPuroderDData(TParm parm) {
        TParm purorderD = new TParm();
        TParm parmD = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            purorderD.addData("PURORDER_NO", parmD.getValue("PURORDER_NO", i));
            purorderD.addData("SEQ_NO", parmD.getInt("STESEQ_NO", i));
            purorderD.addData("STOCKIN_SUM_QTY", parmD.getDouble("QTY", i));
            purorderD.addData("UNDELIVERY_QTY", parmD.getDouble("QTY", i));
            purorderD.addData("OPT_USER", Operator.getID());
            purorderD.addData("OPT_DATE", date);
            purorderD.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("PUR_D", purorderD.getData());
        return parm;
    }

    /**
     * ȡ�ö�������������
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvPurorderMData(TParm parm) {
        TParm purorderM = new TParm();
        TParm parmD = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        purorderM.setData("PURORDER_NO", parmD.getValue("PURORDER_NO", 0));
        purorderM.setData("OPT_USER", Operator.getID());
        purorderM.setData("OPT_DATE", date);
        purorderM.setData("OPT_TERM", Operator.getIP());

        parm.setData("PUR_M", purorderM.getData());
        return parm;
    }
    
    
    /**
     * �����ֵ�����ƶ���Ȩƽ��(INV_BASE)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvAgentData(TParm parm) {
    	
    	 TParm invagent = new TParm();
         TParm parmD = parm.getParm("VER_D");
         Timestamp date = SystemTool.getInstance().getDate();
         
         for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
           String  inv_code = parmD.getValue("INV_CODE", i);
           invagent.addData("INV_CODE", inv_code);
             // �������
             // ����
           double  unit_price = parmD.getDouble("UNIT_PRICE", i);
             invagent.addData("CONTRACT_PRICE", unit_price);
             invagent.addData("OPT_USER", Operator.getID());
             invagent.addData("OPT_DATE", date);
             invagent.addData("OPT_TERM", Operator.getIP());
         }
         parm.setData("BASE", invagent.getData());
    	
    	return parm;
    }
    
    
    /**
     * �����ֵ�����ƶ���Ȩƽ��(INV_BASE)
     * @param parm TParm
     * @return TParm
     */
    private TParm getInvBaseData(TParm parm) {
        TParm invbase = new TParm();
        TParm parmD = parm.getParm("VER_D");
        Timestamp date = SystemTool.getInstance().getDate();
        String inv_code = "";
        double sum_qty = 0;
        double cost_price = 0;
        double in_qty = 0;
        double verifyin_qty = 0;
        double unit_price = 0;
        double gift_qty = 0;
        for (int i = 0; i < parmD.getCount("INV_CODE"); i++) {
            inv_code = parmD.getValue("INV_CODE", i);
            invbase.addData("INV_CODE", inv_code);
            // Sum(�����INV_STOCKM.STOCK_QTY
            TParm stockQty = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvStockSumQty(inv_code)));
            if (stockQty.getCount() > 0) {
                sum_qty = stockQty.getDouble("SUM_QTY", 0);
            }
            // ��Ȩƽ���ɱ���
            TParm costPrice = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvBase(inv_code)));
            // ת����
            TParm rateParm = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvTransUnit(inv_code)));
            cost_price = costPrice.getDouble("COST_PRICE", 0);
            // �������
            in_qty = parmD.getDouble("QTY", i);
            // ��������
            verifyin_qty = parmD.getDouble("QTY", i) *
                rateParm.getDouble("STOCK_QTY", 0) *
                rateParm.getDouble("DISPENSE_QTY", 0);
            // ����
            unit_price = parmD.getDouble("UNIT_PRICE", i);
            // ������
            gift_qty = parmD.getDouble("GIFT_QTY", i) *
                rateParm.getDouble("STOCK_QTY", 0) *
                rateParm.getDouble("DISPENSE_QTY", 0);

            cost_price = (sum_qty * cost_price +
                          in_qty *
                          ( (verifyin_qty * unit_price /
                             (verifyin_qty + gift_qty)))) /
                (sum_qty + in_qty);
            //System.out.println(i + "---" + cost_price);
            invbase.addData("COST_PRICE", cost_price);
            invbase.addData("OPT_USER", Operator.getID());
            invbase.addData("OPT_DATE", date);
            invbase.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("BASE", invbase.getData());
        return parm;
    }

    /**
     * ����(TABLE)��ѡ��ı��¼�
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        // ��õ����table����
        TTable tableDown = (TTable) obj;
        // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
        tableDown.acceptText();
        // ���ѡ�е���
        int column = tableDown.getSelectedColumn();
        if (column == 0) {
            //�����ܽ��
            this.setValue("INVOICE_AMT", getSumAMT());
        }
    }

    /**
     * ��ʼ��������
     */   
    private void initPage() {
   	 l= (TLabel)getComponent("INV_CODE1");
     /**
      * Ȩ�޿���      
      * Ȩ��1:һ�����������Ȩ��,ֻ��ʾ������������;������¼�빦��
      * Ȩ��2:һ���������Ȩ��,ֻ��ʾ������������;��������¼�빦��
      * Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ�Ű�������¼�빦��
      */
 	if (Operator.getDept().equals("011202")||Operator.getDept().equals("011203")) {
 		getCheckBox("CON_FLG").setEnabled(true);
 		setValue("CON_FLG", "Y");
			
		}else {
			getCheckBox("CON_FLG").setEnabled(false);
			setValue("CON_FLG", "N");
		}
 	
 	
 	
     // ����Ȩ��
     if (!this.getPopedem("giftEnabled")) {
         TTable table_d = getTable("TABLE_D");
         table_d.setLockColumns("1,2,4,5,7,12,13,14,15,16");
         gift_flg = false;
     }
     // ��ʾȫԺҩ�ⲿ��
     TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
         "ORG_CODE");
     TextFormatINVOrg inv_org_q = (TextFormatINVOrg)this.getTextFormat(
         "ORG_CODE_Q");
     if (!this.getPopedem("deptAll")) {  
         inv_org.setOperatorId(Operator.getID());  
         inv_org_q.setOperatorId(Operator.getID());
         dept_flg = false;
     }
     else {  
         inv_org.setOperatorId("");
         inv_org_q.setOperatorId("");  
         dept_flg = true;
     }
     //kindmap��ʼ������ rfid�������
     
     kindmap=new HashMap<String, String>();
     //fux modify 20140320 ȥ����ʼ�� ,���뵽��Ź�����
//     kindmap.put("01", "A");
//     kindmap.put("02", "A");
//     kindmap.put("03", "A");
//     kindmap.put("04", "A"); 
//     kindmap.put("05", "A");
//     kindmap.put("06", "A");
//     kindmap.put("07", "A");
//     kindmap.put("08", "C");
//     kindmap.put("21", "S");
  

     Timestamp date = StringTool.getTimestamp(new Date());
     // ��ʼ����ѯ����
     this.setValue("END_DATE",
                   date.toString().substring(0, 10).replace('-', '/') +
                   " 23:59:59");
     this.setValue("START_DATE",
                   StringTool.rollDate(date, -7).toString().substring(0, 10).
                   replace('-', '/') + " 00:00:00");
     ( (TMenuItem) getComponent("delete")).setEnabled(false);
     setValue("VERIFYIN_DATE", date);
     setValue("SUP_CODE", "19");   
     setValue("ORG_CODE", "011201");
     setValue("ORG_CODE_Q", "011201");
     
 
     table_m = getTable("TABLE_M");
     table_d = getTable("TABLE_D");

     // ��TABLEDEPT�е�CHECKBOX���������¼�
     callFunction("UI|TABLE_D|addEventListener",
                  TTableEvent.CHECK_BOX_CLICKED, this,
                  "onTableCheckBoxClicked");


     // ��ʼ��TABLE_D��Parm
     TParm parmD = new TParm();
     String[] verD = {
         "SELECT_FLG", "PURORDER_NO", "PURORDER_DATE", "INV_CHN_DESC",
         "PURORDER_QTY", "GIFT_QTY", "BILL_UNIT", "PURORDER_PRICE",
         "STOCKIN_SUM_QTY", "INV_CODE", "DESCRIPTION", "MAN_CODE",
         "VALIDATE_FLG", "SEQMAN_FLG", "PURCH_QTY", "STOCK_QTY",
         "SUP_CODE", "STATIO_NO", "STOCK_UNIT", "SEQ_NO", "VALID_DATE",
         "BATCH_NO", "DISPENSE_UNIT", "DISPENSE_QTY"};
     for (int i = 0; i < verD.length; i++) {
         parmD.setData(verD[i], new Vector());
     }
     table_d.setParmValue(parmD);
     getCheckBox("CHECK_FLG").setSelected(true);
     getTextFormat("CON_ORG").setEnabled(false);
 } 
	    
//		public void onConChange() {
//			// TODO Auto-generated method stub
//			if (getCheckBox("CON_FLG").isSelected()) {
//				   getTextFormat("CON_ORG").setValue(Operator.getDept());
//				   
//			}else {
//				 getTextFormat("CON_ORG").setValue("");
//			}
//			
//			
//		
//		}
//		
    /**
     * �����ܽ��
     * @return double
     */
    private double getSumAMT() {
        TParm parm = table_d.getParmValue();
        double sum_amt = 0;
        for (int i = 0; i < 10; i++) {
            if (!"Y".equals(parm.getValue("SELECT_FLG", i))) {
                continue;
            }
            sum_amt +=
                (parm.getDouble("VERIFYIN_AMT", i) -
                 parm.getDouble("QUALITY_DEDUCT_AMT", i));
        }
        return sum_amt;
    }
   //fux add ��Ҫȷ�� 20140319 
    
    // �������
    private String action = "insert";
    
    /**
	 * �����ҩ������excel
	 * 
	 * @date 20140319
	 * @author fux
	 */
	public void onImpExcel() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
			return;
		}
		if ("".equals(getValueString("SUP_CODE"))) {
			this.messageBox("��Ӧ���̲���Ϊ��");
			return;
		}		 
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		String supCode = getValueString("SUP_CODE");
        //�򿪹�ҩ����xml����
		Object result = openDialog("%ROOT%\\config\\inv\\INVVerifyinImpXML.x", parm);

		if (result != null) {
//			FileParseExcel fileParseExcel=new  FileParseExcel();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			//ȡ�������ʾ���ڱ���.java�ļ���ʱ�򣬲��ڳ���һЩ���� �������û���õ���������ʾ���棬��
			//@SuppressWarnings("unused")֮�� ��������ʧ
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			try {
//				addParm = (TParm) FileUtils.readXMLFileP(filePath);
				//��ҩҩƷ�ӿ� 
				addParm = (TParm) FileParseExcel.getInstance().readXls(filePath);
//				System.out.println("---------------parm: "+addParm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//????
			TTable table = getTable("TABLE_D");
			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			// ��Ӧ����
//			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// �ƻ�����
			this.setValue("PLAN_NO", "");

			int rowCount = 0 ;
			for (int i = 0; i < addParm.getCount("INV_CODE"); i++) {
				String erpId = addParm.getInt("ERP_PACKING_ID", i)+"";
				//���ж�ERP_ID�Ƿ��Ѿ����� true����
				//>????
				boolean flg = isImpERPInfo("","","",erpId);
				if(flg){//������� ������һ��ѭ��
					continue;
				} 
				//ѡ;��������;���;������;������;������λ;���յ���;С��;Ч��;���ս��;Ʒ�ʿۿ�;�������;��ⵥλ;��������;��Ź���;Ч�ڹ���
				int row = table.addRow();
				// INV_CODE
				String invCode = addParm.getValue("INV_CODE", i);
				TParm phaParm = new TParm(TJDODBTool.getInstance().select(INVSQL.getInvBase(invCode)));
//				System.out.println(i+"--"+orderCode+"--1127-------phaParm:"+phaParm);
				// ���DATESTORE       ���ʵ��� 
				//fux modify ��λ���
				//resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
				table.getDataStore().setItem(row, "INV_CODE",invCode);
				table.getDataStore().setItem(row, "INV_CHN_DESC", phaParm.getValue("INV_CHN_DESC", 0));
				//�Ƿ���Ҫת���ʣ�
				// ���������ת����  
//				TParm getTRA = INDTool.getInstance().getTransunitByCode(orderCode);
//				if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
//					this.messageBox("ҩƷ" + orderCode + "ת���ʴ���");
//					return;
//				}
				// ���TABLE_D����
				// ������
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				int stockQty = addParm.getInt("STOCK_QTY", 0);  
//				System.out.println("--------stockQty: "+stockQty);
				purorder_qty = purorder_qty * stockQty;
//				System.out.println("--------purorder_qty: "+purorder_qty);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty);
				// ������
				table.setItem(row, "GIFT_QTY", 0);
				
				// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
				// 0));
				// ������λ
				table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));

				// ���յ���
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
//				System.out.println(i+"--------------SPCSQL.getPriceOfSupCode: "+SPCSQL.getPriceOfSupCode("18", orderCode));
/*				//��ѯ ��Ӧ�̵ļ۸�
				TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
				if(null != agentParm && agentParm.getCount()>0 ){
					double verifyPrice =  agentParm.getDouble("LAST_VERIFY_PRICE", 0);
					verifyPrice =  agentParm.getDouble("CONTRACT_PRICE", 0);
					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(verifyPrice,4));
					// �������
					table.setItem(row, "INVOICE_AMT", StringTool.round(verifyPrice*purorder_qty,2));
		
				}else{
					//����ι�Ӧ��û�д������ҩƷ�򱣴�
					onSaveAgentInfo(orderCode,StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4),supCode);
					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
					// �������
					table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));
				}*/                       
				// by liyh 20130213 �۸񲻴�ind_agentȡ ֱ��ȡpha_base
				table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0),4));
				// �������
				table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*purorder_qty,2));				
				// ���ۼ�
				retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
				table.setItem(row, "RETAIL_PRICE", StringTool.round(phaParm.getDouble("RETAIL_PRICE", 0),4));  
					
 
				// ��������
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// �����������
				table.setItem(row, "STESEQ_NO", addParm.getData("STESEQ_NO", i)); 
				// �ۼ�������
				table.setItem(row, "ACTUAL_QTY", 0);  

				String time1 = addParm.getData("INVOICE_DATE", i) + "";
				time1 = time1.replaceAll("-", "/");
				// ��Ʊ����
				table.setItem(row, "INVOICE_DATE", time1); 
				String validDate = addParm.getData("VALID_DATE", i) + "";
				validDate = validDate.replaceAll("-", "/");
				table.setItem(row, "REASON_CHN_DESC", "VER01");
				// Ч��
				table.setItem(row, "VALID_DATE", validDate);
				// ��������
				table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
				// ��Ʊ��
				table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
				// ����
				table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
				// ����
				table.setItem(row, "ERP_PACKING_ID", addParm.getData("ERP_PACKING_ID", i));				
//				// װ�䵥��
//				String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
//				table.setItem(row, "SPC_BOX_BARCODE",boxCode);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				table.getDataStore().setActive(row, false);
			} 
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			//���ﲻ��Ҫ������Ȩ�޿���  
			//this.setCheckFlgStatus(action);
			getCheckBox("SELECT_ALL").setSelected(true);
			//onCheckSelectAll();
		}
	}  
	
  
	
	
	 /**
     * ��ѯ��ҩ�������Ƿ��Ѿ����뵽������-������
     * @param orderCode ҩƷ����
     * @param boxCode   ��������
     * @param billNo    ���۵���-�����ձ����ֶ���PURORDER_NO
     * @param erpId     ��ҩ������-ID
     * @return String
     */
	public static boolean isImpERPInfo(String orderCode,String boxCode,String billNo,String erpId){
		//false ��ʾ δ���룬true��ʾ�Ѿ�����
		boolean flag = false;
		//����У��sql
//		System.out.println("------------SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId):"+
		//SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId));
		TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId)));
		if(null != parm && parm.getCount() > 0 && parm.getInt("COUNT_NUM", 0)> 0){
			flag = true;  
		}
//		System.out.println("-------------flag: "+flag);
		return flag;  
	}
    
                               
//    
//    /**
//	 * ͨ��excel����Ա����Ϣ,Ĭ��EXCEL�ĸ�ʽΪ��һ��Ϊ��ͷ��
//	 * ����˳�����磺��ţ�����������֤�ţ��Ա��ײʹ��룬���ע�ǣ��������ڣ����ţ��������壬�绰���ʱ࣬��ַ��Ԥ��ʱ��
//	 * ���е�˳���ܸı� ����Ĭ��Ϊ��Ϣ����excel�ĵ�һ��sheetҳ��
//	 */
//	public void onInsertPatByExl() {
//		if ("".equals(getValueString("ORG_CODE"))) {
//			this.messageBox("���ղ��Ų���Ϊ��");
//			return;
//		}
//		if ("".equals(getValueString("SUP_CODE"))) {
//			this.messageBox("��Ӧ���̲���Ϊ��");
//			return;
//		}		
//		TParm parm = new TParm();
//		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
//		String supCode = getValueString("SUP_CODE");
//		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
//			parm.setData("DROG_TYPE", "N");
//		} else {// �龫
//			parm.setData("DROG_TYPE", "Y");
//		}
//		
// 		JFileChooser fileChooser = new JFileChooser();
//		int option = fileChooser.showOpenDialog(null);
//	
//		if (option == JFileChooser.APPROVE_OPTION) {
//			File file = fileChooser.getSelectedFile();
//			String filePath = file.getPath();
//			System.out.println("----------filePaht:"+filePath);
//			if (filePath != null) {
//				TParm addParm = new TParm();
//				try {
////					addParm = (TParm) FileUtils.readXMLFileP(filePath);
//					addParm = (TParm) FileParseExcel.getInstance().readXls(filePath);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				resultParm = (TParm) addParm;
//				TTable table = getTable("TABLE_D");
//				table.removeRowAll();
//				double purorder_qty = 0;
//				double actual_qty = 0;
//				double puroder_price = 0;
//				double retail_price = 0;
//				// ��Ӧ����
////				getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
//				// �ƻ�����
//				this.setValue("PLAN_NO", "");
//
//				getRadioButton("GEN_DRUG").setEnabled(false);
//				getRadioButton("TOXIC_DRUG").setEnabled(false);
//				int rowCount = 0 ;
//				//���ҩƷ�Ƿ��й�Ӧ����Ϣ
///*				String message = checkOrderCodeInAgent(supCode,addParm);
//				if (null != message && message.length()>0) {//���û�����ֶ�ά��
//					this.messageBox("û������ҩƷ�Ĺ�Ӧ�̺ͼ۸���Ϣ��"+message);
//					return;
//				}*/
//				for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
//					String erpId = addParm.getInt("ERP_PACKING_ID", i)+"";
//					//���ж�ERP_ID�Ƿ��Ѿ����� true����
//					boolean flg = isImpERPInfo("","","",erpId);
//					if(flg){//������� ������һ��ѭ��
//						continue;
//					}
//					int row = table.addRow();
//					// ORDER_CODE
//					String orderCode = addParm.getValue("ORDER_CODE", i);
//					TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
////					System.out.println("phaParm:"+phaParm);
//					// ���DATESTORE
//					resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
//					table.getDataStore().setItem(row, "ORDER_CODE",orderCode);
//					table.getDataStore().setItem(row, "ORDER_DESC", phaParm.getValue("ORDER_DESC", 0));
//					// ���������ת����
//					TParm getTRA = INDTool.getInstance().getTransunitByCode(orderCode);
//					if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
//						this.messageBox("ҩƷ" + orderCode + "ת���ʴ���");
//						return;
//					}
//					// ���TABLE_D����
//					// ������
//					purorder_qty = addParm.getDouble("PURORDER_QTY", i);
//					int stockQty = getTRA.getInt("STOCK_QTY", 0);
//					//�а�װ
//					int conversionTraio = phaParm.getInt("CONVERSION_RATIO", 0);
//					conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
////					System.out.println("--------stockQty: "+stockQty+",conversionTraio:"+conversionTraio+",--purorder_qty:"+purorder_qty);
//					purorder_qty = purorder_qty * stockQty * conversionTraio;
////					System.out.println("--------purorder_qty: "+purorder_qty);
//					table.setItem(row, "VERIFYIN_QTY", purorder_qty);
//					// ������
//					table.setItem(row, "GIFT_QTY", 0);
//					// ������λ
//					// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
//					// 0));
//					table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));
//
//					// ���յ���
//					puroder_price = addParm.getDouble("PURORDER_PRICE", i);
////					System.out.println(i+"--------------SPCSQL.getPriceOfSupCode: "+SPCSQL.getPriceOfSupCode("18", orderCode));
//					/******************���ռ۸� ȡ ind_agent ��Ϊpha_base  by liyh 20130313 start*****************************/
//					//��ѯ ��Ӧ�̵ļ۸�
//					TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
//					if(null != agentParm && agentParm.getCount()>0 ){
//						double verifyPrice =  agentParm.getDouble("LAST_VERIFY_PRICE", 0);
//						verifyPrice =  agentParm.getDouble("CONTRACT_PRICE", 0);
//						table.setItem(row, "VERIFYIN_PRICE", StringTool.round(verifyPrice,4));
//						// �������
//						table.setItem(row, "INVOICE_AMT", StringTool.round(verifyPrice*purorder_qty,2));
//			
//					}
//					/*				else{//�����̵�ҩƷ��Ϣ�����Զ�ά��
//						//����ι�Ӧ��û�д������ҩƷ�򱣴�
//						onSaveAgentInfo(orderCode,StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4),supCode);
//						table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
//						// �������
//						table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));
//					}
//					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
//					// �������
//					table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));*/
//					/******************���ռ۸� ȡ ind_agent ��Ϊpha_base  by liyh 20130313  end *****************************/
//					// ���ۼ�
//					retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
//					table.setItem(row, "RETAIL_PRICE", StringTool.round(phaParm.getDouble("RETAIL_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
//						
//					// ��������
//					table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
//					// �����������
//					table.setItem(row, "PURSEQ_NO", addParm.getData("PURSEQ_NO", i));
//					// �ۼ�������
//					table.setItem(row, "ACTUAL", 0);
//
//					String invoiceDate = addParm.getData("INVOICE_DATE", i) + "";
//					invoiceDate = invoiceDate.replaceAll("-", "/");
//					// ��Ʊ����
//					table.setItem(row, "INVOICE_DATE", invoiceDate);
//					String validDate = addParm.getData("VALID_DATE", i) + "";
//					validDate = validDate.replaceAll("-", "/");
//					table.setItem(row, "REASON_CHN_DESC", "VER01");
//					// Ч��
//					table.setItem(row, "VALID_DATE", validDate);
//					// ��������
//					table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
//					// ��Ʊ��
//					table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
//					// ����
//					table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
//					// ����
//					table.setItem(row, "ERP_PACKING_ID", addParm.getData("ERP_PACKING_ID", i));				
//					// װ�䵥��
//					String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
//					table.setItem(row, "SPC_BOX_BARCODE",boxCode);
//					table.getDataStore().setItem(i, "UPDATE_FLG", "0");
//					table.getDataStore().setActive(row, false);
//				}
//				table.setDSValue();
//				getComboBox("ORG_CODE").setEnabled(false);
//				getTextFormat("SUP_CODE").setEnabled(false);
//				action = "insert";
//				this.setCheckFlgStatus(action);
//				getCheckBox("SELECT_ALL").setSelected(true);
//				onCheckSelectAll();
//			}
//		}
//		//onPackage();
//
//	}	

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
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
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
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
    }

}