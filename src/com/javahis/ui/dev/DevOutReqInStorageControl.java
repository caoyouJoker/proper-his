package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;

import jdo.dev.DevDepTool;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevTypeTool;
import jdo.sys.Operator; 
import jdo.sys.SystemTool;    
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.TTableBase;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;  
import com.javahis.util.OrderUtil;
import com.silionmodule.ReaderType.AntTypeE;
           
/**
 * <p>Title: ���ȷ��</p>  
 *
 * <p>Description: ���ȷ��</p>me
 *  
 * <p>Copyright: Copyright (c) 2013</p>                
 *
 * <p>Company:bluecore </p> 
 *  
 * @author  fux 
 * @version 4.0 
 */ 
public class DevOutReqInStorageControl extends TControl {
    //�豸�����ϸ������
    TParm parmD = new TParm();
    //�豸��ϸ��Ź�������
    TParm parmDD = new TParm();
    //��������ϸ��parm          
    TParm parmExD = new TParm();
    //�����ϸTABLE:DEV_INWAREHOUSED   
    //��ϸ���TABLE:DEV_INWAREHOUSEDD
    String outStorageNo = null;
    //���ȷ������ϸ��parm 
    TParm tableParm = new TParm();
    /**
     * ��ʼ������
     */  
    public void onInit() {  
        super.onInit();
        //�豸��ϸ���༭�¼�
        addEventListener("DEV_INWAREHOUSED->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
//        //�豸��ϸ���༭�¼�
//        addEventListener("DEV_INWAREHOUSEDD->" + TTableEvent.CHANGE_VALUE,"onTableValueChangeDD"); 
 		// ����ҽ��CHECK_BOX�����¼�
		getTTable("DEV_INWAREHOUSEDD").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");
        //�豸¼���¼�
        getTTable("DEV_INWAREHOUSED").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
        //�豸¼�¼�
        getTTable("DEV_INWAREHOUSED").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableComponent");
        //�豸DD¼�¼�
        getTTable("DEV_INWAREHOUSEDD").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onTableDDComponent");
        //��ʼ����������ʱ�ؼ�Ĭ��ֵ
        initComponent();
        //��ʼ���豸�����ϸ���
        initTableD();
        //��ʼ���豸�����ϸ��Ź�����      
        initTableDD();
        //�������
        addDRow();
        //��ס��񲻿ɱ༭��λ  
        setTableLock();
        //����Ȩ��
        onInitOperaotrDept(); 
        callFunction("UI|SCAN_BARCODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChangeBarcode");
    }

    /**
     * ����ɨ���¼� 
     */  
    public void onChangeBarcode(){ 
    	//ɨ��BARCODE 
    	TParm ddParm=getTTable("DEV_INWAREHOUSEDD").getParmValue();
    	boolean flg = false; 
    	for (int i = 0; i < ddParm.getCount("BARCODE"); i++) {
    		String cString=ddParm.getData("BARCODE", i).toString().trim();
    		if (cString.equals(getValueString("SCAN_BARCODE").trim())) {
    			flg = true;
    			getTTable("DEV_INWAREHOUSEDD").setItem(i, "SELECT_FLG", flg);
				break; 
			} 
		}  
    	this.clearValue("SCAN_BARCODE");
    	getTextField("SCAN_BARCODE").setEditable(true);
    	getTextField("SCAN_BARCODE").grabFocus();
	}
    /**
     * ����Ȩ��
     */
    private void onInitOperaotrDept(){
        // ��ʾȫԺҩ�ⲿ�� 
        if (getPopedem("deptAll"))
            return; 
//        ((TextFormatDEVOrg)getComponent("INWAREHOUSE_DEPT")).setOperatorId(Operator.getID());
//        ((TextFormatDEVOrg)getComponent("DEPT")).setOperatorId(Operator.getID());
    }

    /**
     * ��ס��񲻿ɱ༭��λ
     */
    public void setTableLock(){
        ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("3,4,5,6,8,"+
                                                                  "9,10,12,16,17,18,"+
                                                                  "19,20,21,22,"+
                                                                  "24,25,26,27,28");
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,2,3,4,5,11,"+
                                                                   "12,13,14,15");
    }
    /**
     * ��ʼ������ؼ�Ĭ��ֵ
     */
    public void initComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("INWARE_START_DATE",timestamp);
        setValue("INWARE_END_DATE",timestamp);
        setValue("INWAREHOUSE_DATE",timestamp); 
        setValue("INWAREHOUSE_DEPT",Operator.getDept());
        setValue("DEPT",Operator.getDept()); 
        setValue("OPERATOR",Operator.getID());
    }
    /**   
     * ��ʼ���豸�����ϸ��� 
     */
    public void initTableD(){
        String column = "DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
        		"QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;DEPR_METHOD;" +
        		"USE_DEADLINE;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;" +
        		"FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;" +
        		"SEQ_NO;DEVKIND_CODE"; 
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            tableDParm.addData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(tableDParm);
        ((TTable)getComponent("DEV_INWAREHOUSED")).removeRow(0);
    }
    /**
     * ��ʼ���豸�����ϸ��Ź�����
     */
    public void initTableDD(){
    	//����ѡ���ȷ����ϸ���Ƿ���ȷ 
        String column = "DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;" +
        		"DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;" +
        		"DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;" +
        		"MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;" +
        		"GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;" +
        		"SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER";
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            tableDParm.addData(stringMap[i],""); 
        }
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableDParm);  
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).removeRow(0);
    }
    /**
     * �������
     */
    public void addDRow(){
        String column =  "DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
		                 "QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;DEPR_METHOD;" +
		                 "USE_DEADLINE;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;" +
		                 "FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;" +
		                 "SEQ_NO;DEVKIND_CODE"; 
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm();
        for(int i = 0;i<stringMap.length;i++){
            if(stringMap[i].equals("DEVPRO_CODE"))
                tableDParm.setData(stringMap[i],"A");
            else
                tableDParm.setData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_INWAREHOUSED")).addRow(tableDParm);
    }

    /**
     * ��ѯ����
     */
    public void onQuery(){
        ((TTable)getComponent("EXWAREHOUSE")).removeRowAll();
        ((TTable)getComponent("EXWAREHOUSE")).resetModify();
        TParm parm = new TParm();
        //��ⵥ��
        if(getValueString("INWAREHOUSE_NO").length()!=0)
            parm.setData("INWAREHOUSE_NO",getValueString("INWAREHOUSE_NO"));
        //��⿪ʼʱ��
        if(getValueString("INWARE_START_DATE").length()!=0)
            parm.setData("INWARE_START_DATE",StringTool.getTimestamp(getValueString("INWARE_START_DATE"),"yyyy-MM-dd"));
        //������ʱ��
        if(getValueString("INWARE_END_DATE").length()!=0)
            parm.setData("INWARE_END_DATE",StringTool.getTimestamp(getValueString("INWARE_END_DATE"),"yyyy-MM-dd"));
        //������
        if(getValueString("INWAREHOUSE_DEPT").length()!=0)
            parm.setData("INWAREHOUSE_DEPT",getValueString("INWAREHOUSE_DEPT"));
        //�����Ա
        if(getValueString("INWAREHOUSE_USER").length()!=0) 
            parm.setData("INWAREHOUSE_USER",getValueString("INWAREHOUSE_USER"));
        //���ȷ�ϲ���Ҫ������Ϣ
//        //���յ���
//        if(getValueString("RECEIPT_NO").length()!=0)
//            parm.setData("RECEIPT_NO",getValueString("RECEIPT_NO"));
//        //���տ�ʼʱ�� 
//        if(getValueString("RECEIPT_START_DATE").length()!=0)
//            parm.setData("RECEIPT_START_DATE",StringTool.getTimestamp(getValueString("RECEIPT_START_DATE"),"yyyy-MM-dd"));
//        //���ս���ʱ��
//        if(getValueString("RECEIPT_END_DATE").length()!=0)
//            parm.setData("RECEIPT_END_DATE",StringTool.getTimestamp(getValueString("RECEIPT_END_DATE"),"yyyy-MM-dd"));
        if(parm.getNames().length == 0)
            return;
        parm = DevInStorageTool.getInstance().selectDevInStorageInf(parm);
        if(parm.getErrCode() < 0 ){
        	messageBox("��ѯ����");
            return;
        }
        if(parm.getCount()<=0) {
        	messageBox("�޲�ѯ���ݣ�");
            return;
        }
        ((TTable)getComponent("EXWAREHOUSE")).setParmValue(parm);

    }


    /**
     * �õ���Ӧ�������Ϣ
     * @param receiptNo String
     * @return TParm
     */
    public TParm getSupInf(String receiptNo){
        TParm parm = new TParm(getDBTool().select(" SELECT B.SUP_CODE,B.SUP_SALES1,B.SUP_SALES1_TEL"+
                                                  " FROM DEV_RECEIPTM A, SYS_SUPPLIER B"+
                                                  " WHERE A.RECEIPT_NO = '"+receiptNo+"'"+
                                                  " AND   A.SUP_CODE = B.SUP_CODE"));
        return parm;
    }

    /**
     * ������ⵥ��Ϣ(���ⵥ)
     */ 
    public void onGenerateReceipt(){
    	//fux modify
        if(getValueString("INWAREHOUSE_DATE").length() == 0){
            messageBox("��¼���������");
            return;
        }   
        onClear();
        ((TTable)getComponent("EXWAREHOUSE")).setParmValue(((TTable)getComponent("EXWAREHOUSE")).getParmValue());
        //Ӧ���ǳ��ⵥ����������ReceiptUI.x,����������ﻻ�ɳ��ⵥ(������ҵ->�����ҵ��������ҵ->�������)  
        TParm parmDiag = (TParm)openDialog("%ROOT%\\config\\dev\\StorageMUI.x");
        if(parmDiag == null)
            return;    
        //���ⵥ��   
        outStorageNo = parmDiag.getValue("EXWAREHOUSE_NO");      
        //ԭ�õ����յ���Ϣ���ڸ�Ϊ�õ����ⵥ��Ϣ 
        //InStorageTool   selectDevexwarehouseD 
        TParm parmM = new TParm();
        //parmM = DevInStorageTool.getInstance().selectDevexwarehouseM(outStorageNo);
        parmExD = DevInStorageTool.getInstance().selectDevexwarehouseD(outStorageNo); 
        TParm parmDD = DevInStorageTool.getInstance().selectDevexwarehouseDD(outStorageNo);
        if(parmExD.getErrCode() < 0)  
            return;       
        if(parmExD.getCount() <= 0){
            messageBox("����ⵥ�豸�Ѿ�ȫ�����"); 
            return;
        }
//        setValue("SUP_CODE",parmDiag.getValue("SUP_CODE"));
//        setValue("SUP_BOSSNAME",parmDiag.getValue("SUP_SALES1"));
//        setValue("SUP_TEL",parmDiag.getValue("SUP_SALES1_TEL"));
        Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
        
        TParm tableParmXH = new TParm();
        TParm tableParmDD = new TParm(); 
        for(int i = 0;i<parmExD.getCount();i++){
        	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;DEV_CHN_DESC;
        	//BARCODE;MAIN_DEV;SPECIFICATION;MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;GUAREP_DATE;
        	//DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
            //ȡ���������
            String inWarehouseDate = getValueString("INWAREHOUSE_DATE"); 
            //����ʹ�����޼����۾���ֹ���� 
            int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + 
            parmExD.getInt("USE_DEADLINE", i);
            String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length());

            tableParm.addData("DEL_FLG","N"); 
            tableParm.addData("DEVPRO_CODE",parmExD.getData("DEVPRO_CODE",i));
            tableParm.addData("DEV_CODE",parmExD.getData("DEV_CODE",i));
            tableParm.addData("DEV_CHN_DESC",parmExD.getData("DEV_CHN_DESC",i));
            tableParm.addData("SPECIFICATION",parmExD.getData("SPECIFICATION",i));
            tableParm.addData("MAN_CODE",parmExD.getData("MAN_CODE",i));
            tableParm.addData("QTY",parmExD.getData("QTY",i));  
//            tableParm.addData("SUM_QTY",parm.getData("SUM_QTY",i));  
            //������   
            tableParm.addData("RECEIPT_QTY",parmExD.getData("QTY",i)); 
            tableParm.addData("UNIT_CODE",parmExD.getData("UNIT_CODE",i));
            tableParm.addData("UNIT_PRICE",parmExD.getData("UNIT_PRICE",i));
            tableParm.addData("TOT_VALUE",parmExD.getDouble("UNIT_PRICE",i) * parmExD.getDouble("QTY",i));
            tableParm.addData("MAN_DATE",parmExD.getData("MAN_DATE",i));  
            tableParm.addData("LAST_PRICE",0);      
            tableParm.addData("GUAREP_DATE",parmExD.getData("GUAREP_DATE",i));                       
            tableParm.addData("DEPR_METHOD",parmExD.getData("DEPR_METHOD",i));  
            tableParm.addData("USE_DEADLINE",parmExD.getData("USE_DEADLINE",i));
            //tableParm.addData("DEP_DATE",StringTool.getTimestamp(depDate,"yyyy-MM-dd"));
            tableParm.addData("DEP_DATE",parmExD.getData("GUAREP_DATE",i));  
            tableParm.addData("MAN_NATION",parmExD.getData("MAN_NATION",i));
            tableParm.addData("SEQMAN_FLG",parmExD.getData("SEQMAN_FLG",i));
            tableParm.addData("MEASURE_FLG",parmExD.getData("MEASURE_FLG",i));
            tableParm.addData("BENEFIT_FLG",parmExD.getData("BENEFIT_FLG",i));
            tableParm.addData("FILES_WAY","");   
            //����ú����  
            tableParm.addData("VERIFY_NO",outStorageNo);    
            tableParm.addData("VERIFY_NO_SEQ",parmExD.getData("SEQ_NO",i));
            //���Ż�û����
            tableParm.addData("INWAREHOUSE_NO","");  
            tableParm.addData("SEQ_NO",""); 
            tableParm.addData("DEVKIND_CODE",parmExD.getData("DEVKIND_CODE",i));  
            tableParm.addData("DEP_DATE",parmExD.getData("DEP_DATE",i)); 
            tableParm.addData("BRAND",parmExD.getData("BRAND",i)); 
            tableParm.addData("MODEL",parmExD.getData("MODEL",i));  
        }  
        for(int j = 0;j<parmDD.getCount() ;j++){
        	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;
        	//DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;
        	//GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
        	tableParmXH.addData("DEL_FLG","N");  
        	tableParmXH.addData("SELECT_FLG","N");    
        	tableParmXH.addData("PRINT_FLG","N");    
        	tableParmXH.addData("DEVPRO_CODE",parmDD.getData("DEVPRO_CODE",j));
        	//DEV_CODE_DETAIL
        	tableParmXH.addData("DEV_CODE_DETAIL",parmDD.getData("DEV_CODE_DETAIL",j));  
        	tableParmXH.addData("DEV_CODE",parmDD.getData("DEV_CODE",j));
        	tableParmXH.addData("DEV_CHN_DESC",parmDD.getData("DEV_CHN_DESC",j));
        	tableParmXH.addData("SPECIFICATION",parmDD.getData("SPECIFICATION",j)); 
        	tableParmXH.addData("MAN_CODE",parmDD.getData("MAN_CODE",j)); 
        	tableParmXH.addData("QTY",parmDD.getData("QTY",j));   
//            tableParm.addData("SUM_QTY",parm.getData("SUM_QTY",i));  
//            tableParm.addData("RECEIPT_QTY",parm.getData("RECEIPT_QTY",i));  
        	tableParmXH.addData("UNIT_CODE",parmDD.getData("UNIT_CODE",j));  
        	tableParmXH.addData("UNIT_PRICE",parmDD.getData("UNIT_PRICE",j));
        	tableParmXH.addData("TOT_VALUE",parmDD.getDouble("UNIT_PRICE",j) * parmDD.getDouble("QTY",j));
        	//   
        	tableParmXH.addData("MAN_DATE",parmDD.getData("MAN_DATE",j)); 
        	tableParmXH.addData("LAST_PRICE",parmDD.getData("UNIT_PRICE",j));
        	//  
        	tableParmXH.addData("GUAREP_DATE",parmDD.getData("GUAREP_DATE",j));
        	tableParmXH.addData("DEPR_METHOD",parmDD.getData("DEPR_METHOD",j));
        	tableParmXH.addData("USE_DEADLINE",parmDD.getData("USE_DEADLINE",j));
        	//�۾�  need modify 
        	 String inWarehouseDate = getValueString("INWAREHOUSE_DATE"); 
        	tableParmXH.addData("DEP_DATE",StringTool.getTimestamp(inWarehouseDate,"yyyy-MM-dd"));
        	tableParmXH.addData("MAN_NATION",parmDD.getData("MAN_NATION",j));
        	tableParmXH.addData("SEQMAN_FLG",parmDD.getData("SEQMAN_FLG",j));
        	tableParmXH.addData("MEASURE_FLG",parmDD.getData("MEASURE_FLG",j));
        	tableParmXH.addData("BENEFIT_FLG",parmDD.getData("BENEFIT_FLG",j));
        	tableParmXH.addData("FILES_WAY",""); 
        	//����ſ�ȡ 
        	tableParmXH.addData("VERIFY_NO",outStorageNo);  
        	tableParmXH.addData("VERIFY_NO_SEQ",parmDD.getData("SEQ_NO",j));
        	tableParmXH.addData("BARCODE",parmDD.getData("BARCODE",j));
            //��������
        	tableParmXH.addData("INWAREHOUSE_NO","");         
        	tableParmXH.addData("SEQ_NO",parmDD.getData("SEQ_NO",j));   
        	tableParmXH.addData("DEVSEQ_NO",parmDD.getData("DEVSEQ_NO",j));
        	tableParmXH.addData("DEVKIND_CODE",parmDD.getData("DEVKIND_CODE",j));            	
        	tableParmXH.addData("MAIN_DEV",parmDD.getData("SETDEV_CODE",j));
        	 
        	tableParmXH.addData("SERIAL_NUM",parmDD.getData("SERIAL_NUM",j));
        	tableParmXH.addData("IP",parmDD.getData("IP",j));  
        	tableParmXH.addData("TERM",parmDD.getData("TERM",j));
        	tableParmXH.addData("LOC_CODE",parmDD.getData("LOC_CODE",j));
        	tableParmXH.addData("USE_USER",parmDD.getData("USE_USER",j));  
        }   
        
        setValue("INWAREHOUSE_USER",Operator.getID());     
        setValue("INWAREHOUSE_DEPT",parmM.getData("INWAREHOUSE_DEPT",0));
        
        //addDevDDTable(tableParmDD, tableParmXH, 1);  
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(tableParm);
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmXH); 
        ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
        this.setValue("DEPT", parmM.getValue("INWAREHOUSE_DEPT", 0));
        setTableLock();      
    }

    /**
     * ������Ź�����ϸ����Ϣ
     * @param tableParmDD TParm 
     * @param tableParm TParm
     * @param row int 
     */   
    public void addDevDDTable(TParm tableParmDD,TParm tableParm,int row){
        for(int i = 0;i<tableParm.getCount();i++){  
        	//DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;DEVSEQ_NO;DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;
        	//MODEL;MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;
        	//DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
        	tableParmDD.addData("DEL_FLG","N");
        	tableParmDD.addData("SELECT_FLG","N");  
        	tableParmDD.addData("PRINT_FLG","N");        
            tableParmDD.addData("DEVPRO_CODE",tableParm.getData("DEVPRO_CODE",i));
            tableParmDD.addData("DEV_DETAIL_CODE",tableParm.getData("DEV_DETAIL_CODE",i));
            tableParmDD.addData("DEV_CODE",tableParm.getData("DEV_CODE",i)); 
            tableParmDD.addData("DEV_CHN_DESC",tableParm.getData("DEV_CHN_DESC",i));
            tableParmDD.addData("BARCODE",tableParm.getData("BARCODE",i));
            tableParmDD.addData("MAIN_DEV","");
            tableParmDD.addData("MAN_DATE",tableParm.getData("MAN_DATE",i));
            tableParmDD.addData("MAN_SEQ","");
            tableParmDD.addData("LAST_PRICE",tableParm.getData("LAST_PRICE",i));
            tableParmDD.addData("GUAREP_DATE",tableParm.getData("GUAREP_DATE",i));
            tableParmDD.addData("DEP_DATE",tableParm.getData("DEP_DATE",i));
            tableParmDD.addData("TOT_VALUE",tableParm.getData("UNIT_PRICE",i));
            tableParmDD.addData("INWAREHOUSE_NO","");  
            tableParmDD.addData("SEQ_NO",tableParm.getData("SEQ_NO",i)); 
            tableParmDD.addData("DEVSEQ_NO",tableParm.getData("DEVSEQ_NO",i)); 
            tableParmDD.addData("SERIAL_NUM",tableParm.getData("SERIAL_NUM",i));
            tableParmDD.addData("IP",tableParm.getData("IP",i)); 
            tableParmDD.addData("TERM",tableParm.getData("TERM",i));
            tableParmDD.addData("LOC_CODE",tableParm.getData("LOC_CODE",i));
            tableParmDD.addData("USE_USER",tableParm.getData("USE_USER",i)); 
        } 
        //system.out.println("tableParmDD"+tableParmDD);
    }

    /**
     * Dֵ�ı��¼�
     * @param obj Object
     * @return boolean
     */ 
    public boolean onTableValueChange(Object obj) { 
    	//��table��������д������� 
        TTableNode node = (TTableNode)obj;
        //ɾ������ 
        onTableValueChange0(node); 
        //�豸����
        onTableValueChange1(node);   
        //�豸��Ŷ���
        onTableValueChange2(node);
        //���������
        onTableValueChange4(node); 
        //��������
//        //���۶���(�����޷�ȷ����������³��ⶫ���ĵ���,ȥ��)
//        onTableValueChange11(node);
//        //�������ڶ��� 
//        onTableValueChange15(node);
//        //�۾����ڶ���
//        onTableValueChange18(node);
        return false;
   }
    //fux modify 20140612
    /**
     * DDֵ�ı��¼�  
     * @param obj Object
     * @return boolean
     */ 
    public boolean onTableValueChangeDD(Object obj) { 
    	//��table��������д������� 
        TTableNode node = (TTableNode)obj;
        //ѡ���� 
        onTableDDValueChange0(node); 
        return false;
   }
     
    /** 
     * �豸ѡ����
     * @param node TTableNode
     */  
    public void onTableDDValueChange0(TTableNode node){
    	
//    	if (columnName.equals("CHOOSE_FLG")) {
//			String flg = tableParm.getValue(columnName, row);
//			// System.out.println("flg::::::::"+flg);
//			if (flg.equals("N")) {}
    	
         if(node.getColumn() != 0 )
             return;   
         TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
         for(int i = 0;i < ddTable.getRowCount();i++){
                updateTableData("DEV_INWAREHOUSEDD", i, 0,node.getValue()); 
         }
    }
    
    
	/**
	 * checkBoxֵ�ı��¼�
	 * 
	 * @param obj
	 */
	public boolean onCheckBoxValue(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		 TParm tableParm = chargeTable.getParmValue();
		 int col = getTTable("DEV_INWAREHOUSEDD").getSelectedColumn();
		 String columnName =getTTable("DEV_INWAREHOUSEDD").getDataStoreColumnName(col);
		 int row = getTTable("DEV_INWAREHOUSEDD").getSelectedRow();
		 if (col == 0) {
		 String flg = tableParm.getValue(columnName, row);
		 if (flg.equals("Y")) {
		 tableParm.setData("FLG", row, "Y");
		 getTTable("DEV_INWAREHOUSEDD").setParmValue(tableParm);
		 return true;
		   } 
		 }
		 return true;        
		 }
		

   /** 
    * �豸ɾ����
    * @param node TTableNode
    */
   public void onTableValueChange0(TTableNode node){
        if(node.getColumn() != 0 )
            return; 
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,5).equals(dTable.getValueAt(dTable.getSelectedRow(),2))
               //&&ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),1))
               ) 
               updateTableData("DEV_INWAREHOUSEDD", i, 0,node.getValue()); 
        }
   }

   /**
    * �豸���Զ���
    * @param node TTableNode
    */
   public void onTableValueChange1(TTableNode node){
	    //����豸���Բ��ǵ�һ��
        if(node.getColumn() != 1 )
            return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < ddTable.getRowCount();i++){
        if(ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),1)))
        	//����豸���Ų�Ϊ�գ���DEV_INWAREHOUSED��ȡֵ����ֵ��node  
            node.setValue(dTable.getValueAt(node.getRow(),node.getColumn()));
        }
   }

   /**
    * �������ݱ�������ͬ���Parm
    * @param tableTag String
    * @param row int
    * @param column int
    * @param obj Object
    */  
   public void updateTableData(String tableTag,int row,int column,Object obj){
       ((TTable)getComponent(tableTag)).setValueAt(obj,row,column);
       //getFactColumnName:�õ��������
       ((TTable)getComponent(tableTag)).getParmValue().setData
       (getFactColumnName(tableTag,column),row,obj);
   }

   /**
    * �豸��Ŷ���
    * @param node TTableNode
    */
   public void onTableValueChange2(TTableNode node){
	    //TTableNode node��table������
        if(node.getColumn() != 2 )
            return;
        //����ǵڶ��У��豸��ţ�
        String devCodeOld = "" + getTTable("DEV_INWAREHOUSED")
        .getValueAt(node.getRow(),node.getColumn());
        //���ѡ�е�������ֵ
        if(("" +getTTable("DEV_INWAREHOUSED").getValueAt(node.getRow(),3)).
        		length() != 0){
            node.setValue(devCodeOld);
            return;
        }
   }

   /**
    * �豸��������������
    * @param node TTableNode
    * @return boolean
    */
   private boolean onTableValueChange4Check(TTableNode node){
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       if((""+node.getValue()).length()!= 0&&     
          (Double.parseDouble("" + dTable.getValueAt(node.getRow(),5)) -
           Double.parseDouble("" + dTable.getValueAt(node.getRow(),6)) <
           Double.parseDouble("" + node.getValue()))){  
           messageBox("��" + String.valueOf(node.getRow()+1) + "������������ɴ����ۼ�������������֮��");
           node.setValue(dTable.getValueAt(node.getRow(),4));
           return true; 
       }   
       if((""+node.getValue()).length() == 0){
           messageBox("��" + String.valueOf(node.getRow()+1) + "���������������Ϊ��");
           node.setValue(dTable.getValueAt(node.getRow(),4));
           return true; 
       }
       return false;
   }
   /**
    * �豸�����������
    * @param node TTableNode
    */
   public void onTableValueChange4(TTableNode node) {
       if(node.getColumn() != 4) 
           return; 
       if(node.getValue().toString().length() == 0)
           return;
       if(onTableValueChange4Check(node))
           return;
       TParm parm = ((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue();
       TParm tableParmDD = new TParm();
       TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
       //���ݵ����Լ�����������ܼ�ֵ 
//       double unitPrice = 0;
//       if(("" + dTable.getValueAt(node.getRow(), 11)).length() != 0)
//           unitPrice = Double.parseDouble("" + dTable.getValueAt(node.getRow(), 11));
//       double totValue = Double.parseDouble("" + node.getValue()) * unitPrice;
//       updateTableData("DEV_INWAREHOUSED", node.getRow(), 12, totValue);
       //���½���Ź�����ϸչ��
       for(int i = 0; i< parm.getCount("DEVPRO_CODE");i++){
           if("N".equals(parm.getData("SEQMAN_FLG",i)))
               continue;
           int rowCount = 0; 
           String value = ("" + node.getValue()).length() == 0?"0":("" + node.getValue());
           if(i == node.getRow())
               rowCount = Integer.parseInt(value);
           else
               rowCount = parm.getInt("QTY",i);
           for(int j = 0;j<rowCount;j++){ 
        	   tableParmDD.addData("SELECT_FLG","Y");
        	   //DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;QTY;SUM_QTY;RECEIPT_QTY;UNIT_CODE;
        	   //DEPR_METHOD;USE_DEADLINE;SEQMAN_FLG;MEASURE_FLG;
        	   //BENEFIT_FLG;FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE
//               tableParmDD.addData("DEL_FLG",parm.getData("DEL_FLG",i));
                
//               tableParmDD.addData("PRINT_FLG","N");
//               tableParmDD.addData("DEVPRO_CODE",parm.getData("DEVPRO_CODE",i));
//               tableParmDD.addData("DEV_DETAIL_CODE",""); 
//               tableParmDD.addData("DEV_CODE",parm.getData("DEV_CODE",i));
//               tableParmDD.setData("DEVSEQ_NO",""); 
//               tableParmDD.addData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC",i));
//               tableParmDD.addData("BARCODE","");
//               tableParmDD.addData("MAIN_DEV","");
//               
//               tableParmDD.addData("SPECIFICATION","");
//               tableParmDD.addData("MODEL","");
//               tableParmDD.addData("MAN_CODE","");
//               tableParmDD.addData("BRAND","");
//               
//               tableParmDD.addData("MAN_DATE",parm.getData("MAN_DATE",i));
//               tableParmDD.addData("LAST_PRICE",parm.getData("LAST_PRICE",i));
//               tableParmDD.addData("GUAREP_DATE",parm.getData("GUAREP_DATE",i));
//               tableParmDD.addData("DEP_DATE",parm.getData("DEP_DATE",i));
//               tableParmDD.addData("TOT_VALUE",parm.getData("UNIT_PRICE",i));
//               tableParmDD.setData("INWAREHOUSE_NO","");
//               tableParmDD.setData("SEQ_NO","");
//               tableParmDD.setData("DEVSEQ_NO","");
//               tableParmDD.setData("SERIAL_NUM","");
//               tableParmDD.setData("IP","");
//               tableParmDD.setData("TERM","");
//               tableParmDD.setData("LOC_CODE","");
//               tableParmDD.setData("USE_USER","");
           }
       }
       //TABLE:DEV_INWAREHOUSEDD����Parmֵ
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmDD);
   }
   /**
    * ���۶���
    * @param node TTableNode
    */
   public void onTableValueChange11(TTableNode node) {
       if(node.getColumn() != 11)
           return;
       if(node.getValue().toString().length() == 0)
           return;
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       //�����޸ĵĵ����Լ�������������ܼ�ֵ
       int qty = 0;
       if(("" + dTable.getValueAt(node.getRow(),7)).length() != 0)
           qty = Integer.parseInt("" + dTable.getValueAt(node.getRow(),7));
       double totValue = Double.parseDouble("" + node.getValue()) * qty;
       updateTableData("DEV_INWAREHOUSED", node.getRow(), 12, totValue);
       for(int i = 0;i < ddTable.getRowCount();i++){
           if(ddTable.getValueAt(i,2).equals(dTable.getValueAt(dTable.getSelectedRow(),3))&&
              ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),2)))
               updateTableData("DEV_INWAREHOUSEDD", i, 12, node.getValue());
        }
   }
   /**
    * �������ڶ���
    * @param node TTableNode  
    */
   public void onTableValueChange15(TTableNode node){
        if(node.getColumn() != 15 )
           return;  
        if(node.getValue().toString().length() == 0)             
           return; 
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        Timestamp guarepDate = (Timestamp)node.getValue();  
        Timestamp depDate = (Timestamp)dTable.getValueAt(dTable.getSelectedRow(),18);
        String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(),2);
        int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,depDate,guarepDate);
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,2).equals(dTable.getValueAt(dTable.getSelectedRow(),3))&&
               ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),2))){
                updateTableData("DEV_INWAREHOUSEDD", i, 10, node.getValue());
                if(batNo!=Integer.parseInt("" + ddTable.getValueAt(i,2)))
                    updateTableData("DEV_INWAREHOUSEDD", i, 2, batNo);
            }
        }
        if(batNo!=Integer.parseInt("" + dTable.getValueAt(dTable.getSelectedRow(),3)))
            updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3, batNo);
   }

   /**
    * �۾����ڶ���
    * @param node TTableNode
    */
   public void onTableValueChange18(TTableNode node){
        if(node.getColumn() != 18 )
            return;
        if(node.getValue().toString().length() == 0)
           return;
        TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
        TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        Timestamp depDate = (Timestamp)node.getValue();
        Timestamp guarepDate = (Timestamp)dTable.getValueAt(dTable.getSelectedRow(),15);
        String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(),2);
        int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,depDate,guarepDate);
        for(int i = 0;i < ddTable.getRowCount();i++){
            if(ddTable.getValueAt(i,2).equals(dTable.getValueAt(dTable.getSelectedRow(),3))&&
               ddTable.getValueAt(i,3).equals(dTable.getValueAt(dTable.getSelectedRow(),2))){
                updateTableData("DEV_INWAREHOUSEDD", i, 11, node.getValue());
                if(batNo!=Integer.parseInt("" + ddTable.getValueAt(i,2)))
                    updateTableData("DEV_INWAREHOUSEDD", i, 2, batNo);
            }
        }
        if(batNo!=Integer.parseInt("" + dTable.getValueAt(dTable.getSelectedRow(),3)))
            updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3, batNo);
   }

   /**
    * ���淽��
    */
   public void onSave(){  
       //����Ƿ��б�������
       if(onSaveCheck())
           return;
       if(getValueString("INWAREHOUSE_DATE").length() == 0){
           messageBox("������ڲ���Ϊ��");
           return;  
       }
       if(getValueString("OPERATOR").length() == 0){
           messageBox("�����Ա����Ϊ��");
           return;
       }
       if(getValueString("DEPT").length() == 0){
           messageBox("�����Ҳ���Ϊ��");
           return;
       }
       getTTable("DEV_INWAREHOUSED").acceptText();
       getTTable("DEV_INWAREHOUSEDD").acceptText();
       //�ж������������޸�
       if(((TTable)getComponent("EXWAREHOUSE")).getSelectedRow()<0)
           onNew();
       else
           onUpdate();
   }

   /**
    * �����޸Ķ���
    */
   private void onUpdate(){ 
       Timestamp timestamp = SystemTool.getInstance().getDate();
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       TParm tableParmD = dTable.getParmValue();
       TParm tableParmDD = ddTable.getParmValue();
       TParm parmInD = new TParm();
       TParm parmInDD = new TParm();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           cloneTParm(tableParmD,parmInD,i);
           parmInD.addData("OPT_USER",Operator.getID());
           parmInD.addData("OPT_DATE",timestamp);
           parmInD.addData("OPT_TERM",Operator.getIP());
       }
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           cloneTParm(tableParmDD,parmInDD,i);
           parmInDD.addData("OPT_USER",Operator.getID());
           parmInDD.addData("OPT_DATE",timestamp);
           parmInDD.addData("OPT_TERM",Operator.getIP());
       }
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSED",parmInD.getData());
       parm.setData("DEV_INWAREHOUSEDD",parmInDD.getData());
       parm = TIOM_AppServer.executeAction(
           "action.dev.DevAction","updateInStorageReceipt", parm);
       if(parm.getErrCode() < 0){
           messageBox("����ʧ��");
           return;
       }
       messageBox("����ɹ�");
       onDevInwarehouse();
       onPrintReceipt();
   }

   /**
    * ��������
    */
   private void onNew(){
	   //����m d dd
       String inwarehouseNo = DevInStorageTool.getInstance().getInwarehouseNo();
       Timestamp timestamp = SystemTool.getInstance().getDate();
       TParm mParm = new TParm();
       TParm dParm = new TParm();
       TParm ddParm = new TParm();    
       TParm stockMParm = new TParm(); 
       TParm stockDParm = new TParm();  
       TParm stockDDParm = new TParm();
       //insertDevInwarehouseD
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       //dTable:(����TABLEֵ�ı��¼���������Parm����TTable����)
       TTable ddTable = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
       
       //tableParm:{Data={BENEFIT_FLG=[N], MAN_NATION=[86], SEQ_NO=[], DEP_DATE=[2024-05-23 00:00:00.0], 
       //USE_DEADLINE=[10.0], QTY=[4], DEVPRO_CODE=[A], VERIFY_NO=[140523000001], DEL_FLG=[N], VERIFY_NO_SEQ=[1],
       //MEASURE_FLG=[Y], MAN_DATE=[2014-05-23 00:00:00.0], UNIT_PRICE=[null], LAST_PRICE=[null], DEVKIND_CODE=[B], 
       //DEPR_METHOD=[2], RECEIPT_QTY=[4], INWAREHOUSE_NO=[], DEV_CODE=[830110000001], TOT_VALUE=[0.0],
       //SEQMAN_FLG=[Y], FILES_WAY=[], DEV_CHN_DESC=[̨ʽ��], 
       //SPECIFICATION=[null], MAN_CODE=[100], UNIT_CODE=[158], GUAREP_DATE=[2014-05-23 00:00:00.0]}}
       for(int i = 0;i<dTable.getRowCount();i++){ 
           //DEV_CODE  
           if ((""+dTable.getValueAt(i, 2)).length() == 0) {
             continue;   
           }  
           if (dTable.getValueAt(i, 0).equals("Y"))
           {
             continue;
           } 
           Object obj = null;
           TParm parm = (TParm)obj;   
           parm = dTable.getParmValue(); 
           String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
           int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + parm.getInt("USE_DEADLINE",i);
           String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length()); 
           dParm.addData("SEQ_NO", Integer.valueOf(i + 1));    
           dParm.addData("DEV_CODE", tableParm.getValue("DEV_CODE",i));
           dParm.addData("DEV_CHN_DESC", tableParm.getValue("DEV_CHN_DESC",i));   
           dParm.addData("SEQMAN_FLG", tableParm.getValue("SEQMAN_FLG",i));
           dParm.addData("QTY", dTable.getItemData(i, "QTY"));        
           dParm.addData("UNIT_PRICE", tableParm.getDouble("UNIT_PRICE",i));  
           dParm.addData("MAN_DATE", tableParm.getTimestamp("MAN_DATE",i));               
           // dParm.addData("SCRAP_VALUE", tableParm.getValue("LAST_PRICE",i));
           dParm.addData("SCRAP_VALUE", 0); 
           if(tableParm.getTimestamp("GUAREP_DATE",i)!=null){
        	   dParm.addData("GUAREP_DATE", tableParm.getTimestamp("GUAREP_DATE",i));
               }else{
               dParm.addData("GUAREP_DATE", "");   
               }   
           if(tableParm.getTimestamp("DEP_DATE",i)!=null){
        	   dParm.addData("DEP_DATE", tableParm.getTimestamp("DEP_DATE",i));
               }else{
               dParm.addData("DEP_DATE", "");   
               }  
           //dParm.addData("GUAREP_DATE", tableParm.getTimestamp("GUAREP_DATE",i));
           //dParm.addData("DEP_DATE", tableParm.getTimestamp("DEP_DATE",i));   
           dParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
           dParm.addData("VERIFY_NO", tableParm.getValue("VERIFY_NO",i));
           dParm.addData("VERIFY_NO_SEQ", tableParm.getDouble("VERIFY_NO_SEQ",i));
           dParm.addData("INWAREHOUSE_NO", inwarehouseNo);
           dParm.addData("OPT_USER", Operator.getID());  
           dParm.addData("OPT_DATE", timestamp); 
           dParm.addData("OPT_TERM", Operator.getIP());   
           //��ӡ׼�� 
           dParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));            
           dParm.addData("BRAND", tableParm.getValue("BRAND",i));  
           dParm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION",i)); 
           dParm.addData("MODEL", tableParm.getValue("MODEL",i));     
           dParm.addData("TOT_VALUE",tableParm.getValue("TOT_VALUE",i));
           stockMParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
           stockMParm.addData("QTY", dTable.getItemData(i, "QTY"));
           stockMParm.addData("OPT_USER", Operator.getID()); 
           stockMParm.addData("OPT_DATE", timestamp);  
           stockMParm.addData("OPT_TERM", Operator.getIP()); 
           stockMParm.addData("REGION_CODE", Operator.getRegion());  
           stockMParm.addData("STOCK_FLG", ""); 
           //��ֵȷ���Ƿ�Ӧ�ö�Ϊ0     
           //stockMParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
//           stockMParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
//           stockMParm.addData("SCRAP_VALUE", 0); 
//           stockMParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
//           stockMParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
//           stockMParm.addData("CARE_USER", getValue("OPERATOR"));
//           stockMParm.addData("USE_USER", ""); 
//           stockMParm.addData("LOC_CODE", "");  
//           stockMParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
//           stockMParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
           TParm devBase = getDevBase(""+dTable.getItemData(i, "DEV_CODE"));          
//           DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;QTY;SUM_QTY;
//           RECEIPT_QTY;UNIT_CODE;DEPR_METHOD;USE_DEADLINE;
//           SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;FILES_WAY;VERIFY_NO;
//           VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE
           stockDParm.addData("DEPT_CODE", getValue("DEPT"));
           stockDParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
           stockDParm.addData("DEVKIND_CODE", dTable.getItemData(i, "DEVKIND_CODE"));
           stockDParm.addData("DEVTYPE_CODE", getDevTypeCode((String) dTable.getValueAt(i, 2)));
           stockDParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));
           if(devBase.getData("SETDEV_CODE", 0)!=null){  
        	       stockDParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
               } 
               else{ 
            	   stockDParm.addData("SETDEV_CODE", "");   
               } 
           if(dTable.getItemData(i, "SPECIFICATION")!=null){  
               stockDParm.addData("SPECIFICATION", dTable.getItemData(i, "SPECIFICATION"));
           }  
           else{  
               stockDParm.addData("SPECIFICATION", "");  
           } 
           stockDParm.addData("QTY", dTable.getItemData(i, "QTY"));
//           stockDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
//           stockDParm.addData("BUYWAY_CODE", devBase.getData("BUYWAY_CODE", 0));
//           stockDParm.addData("MAN_NATION", devBase.getData("MAN_NATION", 0));
//           stockDParm.addData("MAN_CODE", devBase.getData("MAN_CODE", 0));
//           stockDParm.addData("SUPPLIER_CODE", "");
//           stockDParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
//           stockDParm.addData("MANSEQ_NO", Integer.valueOf(0));
//           stockDParm.addData("FUNDSOURCE", "");
//           stockDParm.addData("APPROVE_AMT", ""); 
//           stockDParm.addData("SELF_AMT", "");
//           stockDParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
//           stockDParm.addData("DEPR_METHOD", devBase.getData("DEPR_METHOD", 0));
//           stockDParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
//           //stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
//           stockDParm.addData("SCRAP_VALUE", 0);   
//           stockDParm.addData("QUALITY_LEVEL", "");
//           stockDParm.addData("DEV_CLASS", devBase.getData("DEV_CLASS", 0));
//           stockDParm.addData("STOCK_STATUS", "");
//           stockDParm.addData("SERVICE_STATUS", "");
//           stockDParm.addData("CARE_USER", getValue("OPERATOR"));  
//           stockDParm.addData("USE_USER", "");
//           stockDParm.addData("LOC_CODE", ""); 
//
//           stockDParm.addData("MEASURE_FLG", devBase.getData("MEASURE_FLG", 0));
//           stockDParm.addData("MEASURE_ITEMDESC", devBase.getData("MEASURE_ITEMDESC", 0));
//           stockDParm.addData("MEASURE_DATE", "");
           //1  
           stockDParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));  
           stockDParm.addData("BUYWAY_CODE", devBase.getData("BUYWAY_CODE", 0));
           stockDParm.addData("MAN_NATION", devBase.getData("MAN_NATION", 0));
           stockDParm.addData("MAN_CODE", devBase.getData("MAN_CODE", 0));
           stockDParm.addData("SUPPLIER_CODE", ""); 
           if(dTable.getItemData(i, "MAN_DATE")!=null){
        	   stockDParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE")); 
           } else{
        	   stockDParm.addData("MAN_DATE", "");
           }  
           stockDParm.addData("MANSEQ_NO", Integer.valueOf(0)); 
           stockDParm.addData("FUNDSOURCE", "");
           stockDParm.addData("APPROVE_AMT", ""); 
           stockDParm.addData("SELF_AMT", "");
           stockDParm.addData("DEPR_METHOD", devBase.getData("DEPR_METHOD", 0));
           //stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
           stockDParm.addData("SCRAP_VALUE", 0);   
           stockDParm.addData("QUALITY_LEVEL", "");
           stockDParm.addData("DEV_CLASS", devBase.getData("DEV_CLASS", 0));
           stockDParm.addData("STOCK_STATUS", "");
           stockDParm.addData("SERVICE_STATUS", "");
           stockDParm.addData("CARE_USER", getValue("OPERATOR"));
           //
           stockDParm.addData("USE_USER", "");
           stockDParm.addData("LOC_CODE", "");       
 
           stockDParm.addData("MEASURE_FLG", devBase.getData("MEASURE_FLG", 0));
           stockDParm.addData("MEASURE_ITEMDESC", devBase.getData("MEASURE_ITEMDESC", 0));
           stockDParm.addData("MEASURE_DATE", "");
           stockDParm.addData("OPT_USER", Operator.getID());
           stockDParm.addData("OPT_DATE", timestamp);
           stockDParm.addData("OPT_TERM", Operator.getIP()); 
           stockDParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
           //�۾���ֹ����
           stockDParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE")); 
           //DEP_DATE,BRAND,MODEL  ����  �ͺ�
           if(dTable.getItemData(i, "BRAND")!=null){   
               stockDParm.addData("BRAND", dTable.getItemData(i, "BRAND")); 
           }    
           else{      
               stockDParm.addData("BRAND", "");  
           } 
           if(dTable.getItemData(i, "MODEL")!=null){  
               stockDParm.addData("MODEL", dTable.getItemData(i, "MODEL"));
           }  
           else{    
               stockDParm.addData("MODEL", "");  
           } 
           //int maxDevSeqNo = 0;
           for (int j = 0; j < ddTable.getRowCount(); j++)
           { 
               if (dTable.getValueAt(i, 2).equals(ddTable.getValueAt(j, 5))) { 
               //DEL_FLG   
               if (ddTable.getValueAt(j, 0).equals("Y"))
                 continue;       
                 //fux  select_flg���޸�  
//               //SELECT_FLG
//               if (ddTable.getValueAt(j, 1).equals("N"))
//                   continue; 
               //DEVKIND_CODE  IN_DEPT OUT_DEPT 
               String barCode = SystemTool.getInstance().getNo("ALL", "DEV", "BARCODE_NO", "BARCODE_NO");  
               ddParm.addData("INWAREHOUSE_NO", inwarehouseNo);
               ddParm.addData("SEQ_NO", Integer.valueOf(i + 1));      
               ddParm.addData("DEVSEQ_NO",ddTable.getItemData(j, "DEVSEQ_NO"));      
               ddParm.addData("DEV_CODE", ddTable.getItemData(j, "DEV_CODE")); 
               ddParm.addData("DEV_CODE_DETAIL", ddTable.getItemData(j, "DEV_CODE_DETAIL"));
               //��Ź�����ϸ���                         
               ddParm.addData("SEQMAN_FLG", "Y"); 
               if(devBase.getData("SETDEV_CODE", 0)!=null){ 
            	   ddParm.addData("SETDEV_CODE", devBase.getData("SETDEV_CODE", 0));
                   }  
                   else{       
                   ddParm.addData("SETDEV_CODE", "");   
                   }  
               if(ddTable.getItemData(j, "MAN_DATE")!=null){
            	   ddParm.addData("MAN_DATE", ddTable.getItemData(j, "MAN_DATE")); 
               }else{
            	   ddParm.addData("MAN_DATE", "");  
               }
               //fux 20140523             
               if(ddTable.getItemData(j, "MANSEQ_NO")!=null){  
            	   ddParm.addData("MANSEQ_NO", ddTable.getItemData(j, "MAN_SEQ")); 
               }else{
            	   ddParm.addData("MANSEQ_NO", "");  
               }
               if(ddTable.getItemData(j, "SERIAL_NUM")!=null){
            	   ddParm.addData("SERIAL_NUM", ddTable.getItemData(j, "SERIAL_NUM")); 
               }else{
            	   ddParm.addData("SERIAL_NUM", "");  
               }
               if(ddTable.getItemData(j, "WIRELESS_IP")!=null){
            	   ddParm.addData("WIRELESS_IP", ddTable.getItemData(j, "WIRELESS_IP")); 
               }else{
            	   ddParm.addData("WIRELESS_IP", "");  
               }
               if(ddTable.getItemData(j, "IP")!=null){
            	   ddParm.addData("IP", ddTable.getItemData(j, "IP")); 
               }else{
            	   ddParm.addData("IP", "");  
               }
               if(ddTable.getItemData(j, "TERM")!=null){
            	   ddParm.addData("TERM", ddTable.getItemData(j, "TERM")); 
               }else{
            	   ddParm.addData("TERM", "");  
               }
               if(ddTable.getItemData(j, "LOC_CODE")!=null){
            	   ddParm.addData("LOC_CODE", ddTable.getItemData(j, "LOC_CODE")); 
               }else{  
            	   ddParm.addData("LOC_CODE", "");  
               }
               ddParm.addData("SCRAP_VALUE", 0);   
               ddParm.addData("GUAREP_DATE", ddTable.getItemData(j, "GUAREP_DATE"));
               ddParm.addData("DEP_DATE", ddTable.getItemData(j, "DEP_DATE"));
               ddParm.addData("UNIT_PRICE", ddTable.getItemData(i, "TOT_VALUE"));
               ddParm.addData("OPT_USER", Operator.getID());
               ddParm.addData("OPT_DATE", timestamp);  
               ddParm.addData("OPT_TERM", Operator.getIP()); 
               //Ĭ��rfid��Ϊ"" ������ͨ����           
               ddParm.addData("BARCODE", barCode);     
               ddParm.addData("RFID", "");  
               devBase = getDevBase(""+ddTable.getItemData(j, "DEV_CODE"));
               stockDDParm.addData("DEV_DETAIL_CODE", ddTable.getItemData(j, "DEV_DETAIL_CODE"));
               stockDDParm.addData("DEV_CODE", ddTable.getItemData(j, "DEV_CODE"));  
               stockDDParm.addData("DEVSEQ_NO",ddTable.getItemData(j, "DEVSEQ_NO"));   
               stockDDParm.addData("DEPT_CODE", getValue("DEPT")); 
               stockDDParm.addData("WAST_FLG", "N");   
               stockDDParm.addData("WAIT_ORG_CODE", "");  
               stockDDParm.addData("OPT_USER", Operator.getID()); 
               stockDDParm.addData("OPT_DATE", timestamp);
               stockDDParm.addData("OPT_TERM", Operator.getIP());   
             }    
           }       
         }  
       //������ⵥ������Ϣ 
       mParm.addData("INWAREHOUSE_NO",inwarehouseNo); 
       mParm.addData("VERIFY_NO",dTable.getItemData(0, "VERIFY_NO"));     
       mParm.addData("INWAREHOUSE_DATE",StringTool.getTimestampDate(timestamp));
       mParm.addData("INWAREHOUSE_USER",getValue("OPERATOR"));
       mParm.addData("INWAREHOUSE_DEPT",getValue("DEPT")); 
       mParm.addData("OPT_USER",Operator.getID()); 
       mParm.addData("OPT_DATE",timestamp); 
       mParm.addData("OPT_TERM",Operator.getIP());
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSEM",mParm.getData());
       parm.setData("DEV_INWAREHOUSED",dParm.getData());
       parm.setData("DEV_INWAREHOUSEDD",ddParm.getData()); 
       //�������  �����ȫ��������⵽��������   ����M��Ŀ���   ���ֻ����һ���֣������M�������棬Ȼ�����һ���µĶ���������(D��Ҳ������)   
       //������� 
       //DEV_STOCKM �ǲ����仯��,��Ϊֻ��dev_code�ķ���Ϊ����,ֻ�б��ϲű�
       //������� �����ȷ��ֻ�ܼ���
       parm.setData("DEV_STOCKM",stockMParm.getData()); 
       //�����µ�D�����ݣ���030101��⣬���⵽0202����ʱӦ�ò���0202���ݣ�
       parm.setData("DEV_STOCKD",stockDParm.getData());
       //���¶���������DEPT_CODE��WAIT_ORG_CODE 
       parm.setData("DEV_STOCKDD",stockDDParm.getData());   
       //����ظ�����M���D���DISCHECK_FLG     
       TParm  parmEx = new TParm(); 
       parmEx.setData("EXWAREHOUSE_NO",outStorageNo);   
       parm.setData("DEV_EXWAREHOUSED",getEXWareHouseDData(parmEx).getData()); 
       parm.setData("DEV_EXWAREHOUSEM",getEXWareHouseMData(parmEx).getData()); 
           TParm result = TIOM_AppServer.executeAction(
                   "action.dev.DevAction","generateOutReqInStorageReceipt", parm); 
           if(result.getErrCode() < 0){
               messageBox("����ʧ��");    
               return;  
           }   
           else{
           messageBox("����ɹ�"); 
           }
       onPrintNew(parm);       
       onClear(); 
       onQuery(); 
   }
    
   /**
    * ����ϸ�����ݸ���״̬
    * @param parm TParm
    * @return TParm
    */
   public TParm getEXWareHouseDData(TParm parm) {         
       TTable dTable = ((TTable)getComponent("DEV_INWAREHOUSED"));
       TParm exWareHouseD = new TParm(); 
       exWareHouseD.addData("EXWAREHOUSE_NO",parm.getData("EXWAREHOUSE_NO"));  
       for (int i = 0; i < dTable.getRowCount(); i++) {
           if ("Y".equals(dTable.getItemString(i, "DEL_FLG"))) {
               continue;
           }   
           else if ("".equals(dTable.getParmValue().getValue("DEV_CODE", i))) {
               continue;     
           }      
           else  {    
        	   exWareHouseD.addData("SEQ_NO",parmExD.getData("SEQ_NO",i));
               if (dTable.getItemDouble(i, "QTY") == 
               	dTable.getParmValue().getDouble("RECEIPT_QTY", i)  
               	||  dTable.getItemDouble(i, "SUM_QTY") ==
                   	dTable.getParmValue().getDouble("RECEIPT_QTY", i)) {
            	   exWareHouseD.addData("DISCHECK_FLG", "N");
               }        
               else {     
            	   exWareHouseD.addData("DISCHECK_FLG", "Y"); 
               } 
           }
       }
       return exWareHouseD; 
   }

   /**
    * �����������״̬
    * @param parm TParm
    * @return TParm
    */
   public TParm getEXWareHouseMData(TParm parm) {
       TParm exWareHouseM = new TParm();  
       exWareHouseM.addData("EXWAREHOUSE_NO",parm.getData("EXWAREHOUSE_NO"));
       boolean flg = true; 
       TParm exWareHouseD = parm.getParm("DEV_EXWAREHOUSED");
       for (int i = 0; i < exWareHouseD.getCount("EXWAREHOUSE_NO"); i++) {
           if ("Y".equals(exWareHouseD.getValue("DISCHECK_FLG", i))) {
               flg = false;
               break; 
           }  
       }  
       if (flg) {
    	   exWareHouseM.addData("DISCHECK_FLG", "N");
       }       
       else {    
    	   exWareHouseM.addData("DISCHECK_FLG", "Y"); 
       }

       return exWareHouseM;   
   }

   
   /**
    * �õ��豸����������Ϣ
    * @param devCode String
    * @return TParm
    */
   public TParm getDevBase(String devCode){
       String SQL="SELECT * FROM DEV_BASE WHERE DEV_CODE = '"+devCode+"'";
       TParm parm = new TParm(getDBTool().select(SQL));
       return parm; 
    }
    /**
     * �õ��豸���������Ϣ
     * @param code String
     * @return String
     */
    private String getDevTypeCode(String code){
       TParm parm = DevTypeTool.getInstance().getDevRule();
       int classify1 = parm.getInt("CLASSIFY1",0);         
       return code.substring(0,classify1);
    } 
    /**
     * ������ӡ�豸��ⵥ(���ȷ�ϵ���ӡҲҪ����)
     * @param inParm TParm
     */
    public void onPrintNew(TParm inParm){
       //���ñ�ͷ��Ϣ  
       TParm printParm = new TParm();  
       printParm.setData("HOSP_NAME_T",Operator.getHospitalCHNFullName());
       printParm.setData("INWAREHOUSE_NO_T",inParm.getParm("DEV_INWAREHOUSEM").getData("INWAREHOUSE_NO",0));
       printParm.setData("MAN_CODE_T",getSupDesc(getValueString("SUP_CODE")));
       printParm.setData("INWAREHOUSE_DEPT_T",getDeptDesc(inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DEPT",0)));
       printParm.setData("INWAREHOUSE_DATE_T", inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DATE",0).substring(0,10).replace('-','/'));
       printParm.setData("CONTACT_T",getValue("SUP_BOSSNAME"));
       printParm.setData("INWAREHOUSE_USER_T",getOperatorName(inParm.getParm("DEV_INWAREHOUSEM").getValue("INWAREHOUSE_USER",0)));
       printParm.setData("VERIFY_NO_T",inParm.getParm("DEV_INWAREHOUSEM").getValue("VERIFY_NO",0));
       printParm.setData("TEL_NO_T",getValue("SUP_TEL"));
       TParm parm = new TParm();
       //�����ݿ�������ⵥ����ε���
       //Caused by: java.lang.StringIndexOutOfBoundsException: String index out of range: 10
	   //at java.lang.String.substring(Unknown Source)
	   //at com.javahis.ui.dev.DevOutReqInStorageControl.onPrintNew(DevOutReqInStorageControl.java:1273)
       for(int i = 0;i<inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE");i++){
           cloneTParm(inParm.getParm("DEV_INWAREHOUSED"),parm,i);
       }
       //ת��ʱ�����ڸ�ʽ
       for(int i = 0;i<parm.getCount("DEVPRO_CODE");i++){
           parm.setData("MAN_DATE",i,parm.getValue("MAN_DATE",i).substring(0,10).replace('-','/'));
           //���ȷ�ϵ����޷���ӡ���۾���ֹ������ά����ֹ����
//           parm.setData("GUAREP_DATE",i,parm.getValue("GUAREP_DATE",i).substring(0,10).replace('-','/'));
//           parm.setData("DEP_DATE",i,parm.getValue("DEP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEVPRO_CODE",i,getDevProDesc(parm.getValue("DEVPRO_CODE",i)));
       }  
       //������ⵥ�����Ϣ
       parm.setCount(inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE"));
       parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
       parm.addData("SYSTEM", "COLUMNS", "QTY");
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");  
       parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
       parm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
//       parm.addData("SYSTEM", "COLUMNS", "GUAREP_DATE");
//       parm.addData("SYSTEM", "COLUMNS", "DEP_DATE");
       printParm.setData("TABLE",parm.getData()); 
       printParm.setData("TITLE", "TEXT", Manager.getOrganization().
				getHospitalCHNFullName(Operator.getRegion()) +
				"���ȷ�ϵ�");      
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
   }
   /**
    * ��շ���  
    */
   public void onClear(){ 
       setValue("INWAREHOUSE_NO_QUARY","");  
       setValue("INWAREHOUSE_DATE","");
       ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(true);
       setValue("VERIFY_NO","");
       setValue("DEPT","");
       ((TTextFormat)getComponent("DEPT")).setEnabled(true);
       setValue("OPERATOR","");
       ((TTextFormat)getComponent("OPERATOR")).setEnabled(true);
       ((TTable)getComponent("EXWAREHOUSE")).removeRowAll();
       ((TTable)getComponent("EXWAREHOUSE")).resetModify();
       setValue("SUP_CODE","");
       setValue("SUP_BOSSNAME","");
       setValue("SUP_TEL","");
       parmD = new TParm();
       parmDD = new TParm();
       initComponent();
       initTableD();
       initTableDD();
       addDRow();
       setTableLock();
   }
   /**
    * �豸��ⵥ����Ϣ�����¼�
    */ 
   public void onDevInwarehouse(){ 
       TTable table = ((TTable)getComponent("EXWAREHOUSE"));  
       int row = table.getSelectedRow(); 
       TParm parm = DevInStorageTool.getInstance().selectDevInwarehouseD("" + table.getValueAt(row,1));
       if(parm.getErrCode()<0)
           return;
       parmD = new TParm(); 
       for(int i = 0;i<parm.getCount();i++)   
           cloneTParm(parm,parmD,i);
       ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(parm);
       parm = DevInStorageTool.getInstance().selectDevInwarehouseDD("" + table.getValueAt(row,1));
       if(parm.getErrCode()<0)
         return;
       parmDD = new TParm(); 
       for(int i = 0;i<parm.getCount();i++)
           cloneTParm(parm,parmDD,i); 
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(parm);
       ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("0,1,2,3,4,5,6,"+
                                                                 "7,8,9,10,12,15,"+
                                                                 "16,17,18,19,20,"+
                                                                 "21,22,24,25,26,27,28");
       ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,2,3,4,5,10,"+
                                                                  "11,12,13,14,15");
       setValue("INWAREHOUSE_NO_QUARY",table.getValueAt(row,1));
       setValue("INWAREHOUSE_DATE",table.getValueAt(row,0));
       ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
       setValue("VERIFY_NO",table.getValueAt(row,3));
       setValue("DEPT",table.getValueAt(row,2));
       ((TTextFormat)getComponent("DEPT")).setEnabled(false);
       setValue("OPERATOR",table.getValueAt(row,5)); 
       ((TTextFormat)getComponent("OPERATOR")).setEnabled(false);
       if(("" + table.getValueAt(row,3)).length() == 0)
           return;
       TParm supParm = getSupInf("" + table.getValueAt(row,3));
       if(supParm.getErrCode() < 0)
           return;
       if(supParm.getCount() < 0) 
           return;
       setValue("SUP_CODE",supParm.getValue("SUP_CODE",0));
       setValue("SUP_BOSSNAME",supParm.getValue("SUP_SALES1",0));
       setValue("SUP_TEL",supParm.getValue("SUP_SALES1_TEL",0));
   }
   //����С��,����Ҳ��,RFID��ǩֻɨ�費��ӡ
//   /**
//    * ��ӡRFID��ǩ
//    */
//   public void onPrintRFID() {
//	   TTable table = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
//       int row = table.getSelectedRow();
//       if(row < 0){
//           messageBox("��ѡ��һ����ⵥ�ٴ�ӡ����");
//           return;
//       }
//       String strRFID = (String)table.getParmValue().getData("RFID", row);
//       if (null == strRFID) {
//    	   messageBox("���ȱ�����ⵥ���ٴ�ӡRFID��ǩ");
//    	   return;
//       }
//       try {
//		ModuleDriver.write("COM5", AntTypeE.ONE_ANT, strRFID);
//	} catch (Exception e) {
//		messageBox("RFID��ǩ��ӡʱ����");
//		e.printStackTrace();
//		return;
//	}
//   }

   /**
    * ����TParm
    * @param from TParm
    * @param to TParm
    * @param row int
    */
   private void cloneTParm(TParm from,TParm to,int row){
       String names[] = from.getNames();
       for(int i = 0;i < names.length;i++){
           Object obj = from.getData(names[i],row);
           if(obj == null)
               obj = "";
           to.addData(names[i],obj);
       }
    }

    /**
     * �Ƚ���ͬ��TParm��ֵ�Ƿ�ı�
     * @param parmA TParm
     * @param parmB TParm
     * @param row int
     * @return boolean
     */
    private boolean compareTo(TParm parmA,TParm parmB,int row){
       String names[] = parmA.getNames();
       for(int i = 0;i < names.length;i++){
           if(parmA.getValue(names[i],row).equals(parmB.getValue(names[i],row)))
               continue;
           return false;
       }
       return true;
   }

   /**
    * ��˻����Ƿ�����Ҫ�������Ϣ
    * @return boolean
    */
   private boolean onSaveCheck(){
       int rowCount = 0; 
       TTable tableD = getTTable("DEV_INWAREHOUSED");
       TTable tableDD = getTTable("DEV_INWAREHOUSEDD"); 
  
       //����Ϊɾ�������豸��Ų���Ϊ��
       for(int i = 0;i<tableD.getRowCount();i++){
           double contd = 0;
           double contdd = 0;  
           if(("" + tableD.getValueAt(i,0)).equals("N")&&
              ("" + tableD.getValueAt(i,3)).length()!=0)
              rowCount++;    
    	   if(tableD.getItemData(i,"SEQMAN_FLG").equals("N"))
    		   continue;     
           contd = tableD.getItemDouble(i, "QTY");
    	   for(int j = 0;j<tableDD.getRowCount();j++){    
         		if ("Y".equals(tableDD.getItemData(j, "SELECT_FLG").toString())&&
         			tableD.getItemData(i,"DEV_CODE").equals(tableDD.getItemData(j, "DEV_CODE"))) {
         			contdd++;
   			}             
         		else       
         			continue;    
       	   }     
       	   if(contd!=contdd){ 
       		   this.messageBox("�豸"+tableD.getItemData(i,"DEV_CODE").toString()+"��ϸ��ѡ�������������������һ��");
               return true;  
       	   } 
       } 
//	   for(int j = 0;j<tableDD.getRowCount();j++){    
//  		if ("Y".equals(tableDD.getItemData(j, "SELECT_FLG").toString())) {
//  			contdd++;    
//		}     
//  		else 
//  			continue; 
//	   }     
//	   if(contd != contdd){
//		   //"+tableD.getItemData(i,"DEV_CODE").toString()+"
//		   this.messageBox("�豸��ϸ��ѡ�������������������һ��");
//        return true;    
//	   } 
       if(getTTable("EXWAREHOUSE").getSelectedRow() < 0 &&
          rowCount == 0){
           messageBox("�ޱ�����Ϣ");
           return true; 
       }  
       return false; 
   }
   /**
    * �豸��ⵥ��ӡ 
    */
   public void onPrintReceipt(){    
       TTable table = (TTable)getComponent("EXWAREHOUSE"); 
       int row = table.getSelectedRow();  
       if(row < 0){
    	   this.messageBox("�޴�ӡ����");
           return;
       }
       TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
       TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           messageBox("�������ݽ������޸��뱣����ٴ�ӡ");
           return;
       }   
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           messageBox("�������ݽ������޸��뱣����ٴ�ӡ");
           return;
       }
       TParm printParm = new TParm();
       printParm.setData("HOSP_NAME_T",Operator.getHospitalCHNFullName());
       printParm.setData("INWAREHOUSE_NO_T",table.getValueAt(row,1));
       printParm.setData("MAN_CODE_T",getSupDesc(getValueString("SUP_CODE")));
       printParm.setData("INWAREHOUSE_DEPT_T",getDeptDesc("" + table.getValueAt(row,2)));
       printParm.setData("INWAREHOUSE_DATE_T", ("" + table.getValueAt(row,0)).substring(0,10).replace('-','/'));
       printParm.setData("CONTACT_T",getValue("SUP_BOSSNAME"));
       printParm.setData("INWAREHOUSE_USER_T",getOperatorName("" + table.getValueAt(row,5)));
       printParm.setData("VERIFY_NO_T",table.getValueAt(row,3));
       printParm.setData("TEL_NO_T",getValue("SUP_TEL"));
       TParm tableParm = ((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue();
       TParm parm = new TParm();
       for(int i = 0;i<tableParm.getCount("DEV_CODE");i++){
           cloneTParm(tableParm,parm,i); 
       }   
       for(int i = 0;i<parm.getCount("DEVPRO_CODE");i++){
           parm.setData("MAN_DATE",i,parm.getValue("MAN_DATE",i).substring(0,10).replace('-','/'));
//           parm.setData("GUAREP_DATE",i,parm.getValue("GUAREP_DATE",i).substring(0,10).replace('-','/'));
//           parm.setData("DEP_DATE",i,parm.getValue("DEP_DATE",i).substring(0,10).replace('-','/'));
           parm.setData("DEVPRO_CODE",i,getDevProDesc(parm.getValue("DEVPRO_CODE",i)));
       }  
       parm.setCount(tableParm.getCount("DEV_CODE"));   
       //��ⵥ��  
       parm.addData("SYSTEM", "COLUMNS", "INWAREHOUSE_NO");
       //�������� 
       parm.addData("SYSTEM", "COLUMNS", "MAN_CODE_T");
       //�������
       parm.addData("SYSTEM", "COLUMNS", "INWAREHOUSE_DATE");
       //�豸����
       parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       //���
       parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");  
       //��λ 
       parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC"); 
       //����
       parm.addData("SYSTEM", "COLUMNS", "QTY"); 
       //����
       parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");  
       //�ܽ�� 
       parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");  
       //��Ʊ����
       parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");   
       //��Ʊ����
       parm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
       printParm.setData("TITLE", "TEXT", Manager.getOrganization().  
				getHospitalCHNFullName(Operator.getRegion()) +
				"���ȷ�ϵ�");  
       printParm.setData("TABLE",parm.getData());  
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
   }
   //�б���İ·��������ɽ��

   /**
    * ȡ���豸������������
    * @param devProCode String
    * @return String
    */
   public String getDevProDesc(String devProCode){
       TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                 " WHERE GROUP_ID = 'DEVPRO_CODE'"+
                                                 " AND   ID = '"+devProCode+"'"));
       return parm.getValue("CHN_DESC",0);
    }

    /**
     * �����豸����
     */ 
    public void onBarcodePrint(){
       if(getTTable("EXWAREHOUSE").getSelectedRow() < 0){
           messageBox("��ѡ��һ����ⵥ�ٴ�ӡ����");
           return; 
       } 
       TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
       TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
       for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmD,tableParmD,i))
               continue;
           messageBox("�������ݽ������޸��뱣����ڴ�ӡ");
           return;
       }
       for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
           if(compareTo(parmDD,tableParmDD,i))
               continue;
           messageBox("�������ݽ������޸��뱣����ڴ�ӡ");
           return;
       }
       TParm parm = new TParm();
       parm.setData("DEV_INWAREHOUSED",((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue().getData());
       parm.setData("DEV_INWAREHOUSEDD",((TTable)getComponent("DEV_INWAREHOUSEDD")).getParmValue().getData());
       parm.setData("INWAREHOUSE_NO",getValue("INWAREHOUSE_NO_QUARY"));
       parm.setData("INWAREHOUSE_DATE",getValue("INWAREHOUSE_DATE"));
       parm.setData("INWAREHOUSE_DEPT",getValue("DEPT"));
       parm.setData("INWAREHOUSE_USER",getValue("OPERATOR"));
       openDialog("%ROOT%\\config\\dev\\DEVBarcodeUI.x", parm);
   }
   /**
    * �������ݿ��������
    * @return TJDODBTool
    */
   public TJDODBTool getDBTool() {
       return TJDODBTool.getInstance();
   }
   /**
    * �õ�����
    * @param deptCode String
    * @return String 
    */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * �õ��û���  
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
        TParm parm = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
        return parm.getValue("USER_NAME",0);
    }
    /**
      * �õ���Ӧ����
      * @param deptCode String
      * @return String
      */
     public String getSupDesc(String supCode){
         TParm parm = new TParm(getDBTool().select("SELECT SUP_CHN_DESC FROM SYS_SUPPLIER WHERE SUP_CODE='"+supCode+"'"));
         return parm.getValue("SUP_CHN_DESC",0);
    }
   /**
    * �õ�TTable
    * @param tag String
    * @return TTable
    */
   public TTable getTTable(String tag){
       return (TTable)getComponent(tag);
    }
   
   /**
    * �õ�TTextField
    * @param tag String
    * @return getTextField
    */
   public TTextField getTextField(String tag){
       return (TTextField)getComponent(tag);
    }
    /**
     * �豸¼���¼�
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        //�豸����
        String devProCode = ""+getTTable("DEV_INWAREHOUSED").getValueAt(row,1);
        //״̬����ʾ
        callFunction("UI|setSysStatus","");
        //�õ�����
        String columnName = getFactColumnName("DEV_INWAREHOUSED",column);
        if(!"DEV_CODE".equals(columnName))
            return;
        if(!(com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        if(("" +getTTable("DEV_INWAREHOUSED").getValueAt(row,column)).length() != 0)
            return;
        TParm parm = new TParm();
        parm.setData("DEVPRO_CODE",devProCode);
        parm.setData("ACTIVE","Y");
        //���õ����˵�
        textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DEVBASEPopupUI.x"),parm);
        //������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    }

    /**
     * �豸¼�뷵��ֵ�¼�
     * @param tag String 
     * @param obj Object
     */
    public void popReturn(String tag,Object obj){
        //�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
        if (obj == null &&!(obj instanceof TParm))
            return ;
        if(getValueString("INWAREHOUSE_DATE").length() == 0){
            messageBox("��¼���������");
            return;
        }
        //����ת����TParm
        TParm parm = (TParm)obj;   
        Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
 
        String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
        int year = Integer.parseInt(inWarehouseDate.substring(0, 4)) + parm.getInt("USE_DEADLINE");
        String depDate = year + inWarehouseDate.substring(4,inWarehouseDate.length()); 
        int batchNo = DevInStorageTool.getInstance().getUseBatchSeq(parm.getValue("DEV_CODE"), 
                                                                   StringTool.getTimestamp(depDate,"yyyy-MM-dd"),
                                                                   timestamp);
        //���¼���豸�Ƿ��Ѿ�����
        for(int i = 0;i< getTTable("DEV_INWAREHOUSED").getRowCount();i++){
            if (!parm.getValue("DEV_CODE").equals(getTTable("DEV_INWAREHOUSED").getValueAt(i, 2)))
                continue;
            if(batchNo != Integer.parseInt(""+getTTable("DEV_INWAREHOUSED").getValueAt(i, 3)))
                continue;
            messageBox("���豸�Ѿ�����,���ѽ�������ӵ���Ӧ����");
            int qty = 1 + Integer.parseInt("" + getTTable("DEV_INWAREHOUSED").getValueAt(i,7));
            updateTableData("DEV_INWAREHOUSED", i, 7, qty);
            updateTableData("DEV_INWAREHOUSED", i, 12, qty * Double.parseDouble("" + getTTable("DEV_INWAREHOUSED").getValueAt(i,11)));
            updateTableData("DEV_INWAREHOUSED", getTTable("DEV_INWAREHOUSED").getSelectedRow(), 2, "");
            resetDDTableData();
            return;
        }  
        //system.out.println("parm"+parm);
        callFunction("UI|setSysStatus",parm.getValue("DEV_CODE")+":"+parm.getValue("DEV_CHN_DESC")+parm.getValue("SPECIFICATION"));
        getTTable("DEV_INWAREHOUSED").acceptText();
        TParm tableParm =  new TParm();
        TParm tableParmDD = new TParm();
        tableParm.setData("DEL_FLG","N");
        tableParm.setData("DEVPRO_CODE",parm.getData("DEVPRO_CODE"));
        tableParm.setData("DEV_CODE",parm.getData("DEV_CODE"));
        tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
        tableParm.setData("SPECIFICATION",parm.getData("SPECIFICATION"));
        tableParm.setData("MAN_CODE",parm.getData("MAN_CODE"));
        tableParm.setData("QTY",1);
        tableParm.setData("SUM_QTY",0);
        tableParm.setData("RECEIPT_QTY",0);
        tableParm.setData("UNIT_CODE",parm.getData("UNIT_CODE"));
        tableParm.setData("UNIT_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("TOT_VALUE",parm.getDouble("UNIT_PRICE"));
        tableParm.setData("MAN_DATE",timestamp);
        tableParm.setData("LAST_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("GUAREP_DATE",timestamp);
        tableParm.setData("DEPR_METHOD",parm.getData("DEPR_METHOD"));
        tableParm.setData("USE_DEADLINE",parm.getData("USE_DEADLINE"));
        tableParm.setData("DEP_DATE",StringTool.getTimestamp(depDate,"yyyy-MM-dd"));
        tableParm.setData("MAN_NATION",parm.getData("MAN_NATION"));
        tableParm.setData("SEQMAN_FLG",parm.getData("SEQMAN_FLG"));
        tableParm.setData("MEASURE_FLG",parm.getData("MEASURE_FLG"));
        tableParm.setData("BENEFIT_FLG",parm.getData("BENEFIT_FLG"));
        tableParm.setData("FILES_WAY","");
        tableParm.setData("VERIFY_NO","");
        tableParm.setData("VERIFY_NO_SEQ","");
        tableParm.setData("INWAREHOUSE_NO",""); 
        tableParm.setData("SEQ_NO",""); 
        tableParm.setData("DEVKIND_CODE",parm.getData("DEVKIND_CODE"));
        ((TTable)getComponent("DEV_INWAREHOUSED")).removeRow(((TTable)getComponent("DEV_INWAREHOUSED")).getRowCount()-1);
        ((TTable)getComponent("DEV_INWAREHOUSED")).addRow(tableParm);
        //����Ź�����豸չ����Ź�����ϸ
        if("Y".equals(parm.getData("SEQMAN_FLG"))){
            setDevDDTablePop(tableParmDD, tableParm);
            ((TTable)getComponent("DEV_INWAREHOUSEDD")).addRow(tableParmDD);
        }
        addDRow(); 
        setTableLock();     
        ((TTextFormat)getComponent("INWAREHOUSE_DATE")).setEnabled(false);
    }
    /**
     * ����������Ź�����ϸ��Ϣ
     */
    public void resetDDTableData(){
       TTable tableD = getTTable("DEV_INWAREHOUSED");
       TParm parmD = tableD.getParmValue();
       TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
       TParm parmDD = new TParm();
       for(int i = 0;i < parmD.getCount("DEV_CODE");i++){
           if(!"Y".equals(parmD.getValue("SEQMAN_FLG",i)))
               continue;
           addDevDDTable(parmDD, parmD, i);
       }
       tableDD.setParmValue(parmDD);
   }
   /**
    * �����豸¼��ʱ��Ź�����ϸ��Ϣ
    * @param tableParmDD TParm
    * @param tableParm TParm
    */
   public void setDevDDTablePop(TParm tableParmDD,TParm tableParm){
            tableParmDD.setData("DEL_FLG","N");
            tableParmDD.setData("DEVPRO_CODE",tableParm.getData("DEVPRO_CODE"));
            tableParmDD.setData("DEV_CODE",tableParm.getData("DEV_CODE"));
            tableParmDD.setData("DDSEQ_NO",1);
            tableParmDD.setData("DEV_CHN_DESC",tableParm.getData("DEV_CHN_DESC"));
            tableParmDD.setData("MAIN_DEV","");
            tableParmDD.setData("MAN_DATE",tableParm.getData("MAN_DATE"));
            tableParmDD.setData("MAN_SEQ","");
            tableParmDD.setData("LAST_PRICE",tableParm.getData("LAST_PRICE"));
            tableParmDD.setData("GUAREP_DATE",tableParm.getData("GUAREP_DATE"));
            tableParmDD.setData("DEP_DATE",tableParm.getData("DEP_DATE"));
            tableParmDD.setData("TOT_VALUE",tableParm.getData("UNIT_PRICE"));
            tableParmDD.setData("INWAREHOUSE_NO","");
            tableParmDD.setData("SEQ_NO","");
            tableParmDD.setData("DEVSEQ_NO","");
    }
    /**
     * �õ��������
     * @param tableTag String
     * @param column int
     * @return String
     */
    public String getFactColumnName(String tableTag,int column){
        int col = getThisColumnIndex(column);
        return getTTable(tableTag).getDataStoreColumnName(col);
    }

    /**
     * �õ����������
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column){
        return getTTable("DEV_INWAREHOUSED").getColumnModel().getColumnIndex(column);
    }

    /**
     * table�����¼�
     * @param obj Object  
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();
        return true;
    }
    
    /**  
     * tabledd�����¼�
     * @param obj Object  
     * @return boolean
     */
    public boolean onTableDDComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();  
        return true;
    }
    
    
    /**
     * ��ʼɨ��
     * @param obj Object  
     * @return 
     */
    public void onStartScan(Object obj) {
     messageBox("��ʼɨ��");
    } 
    
    
    /**
     * ����ɨ��
     * @param obj Object  
     * @return 
     */
    public void onStopScan(Object obj) {
     messageBox("δ����ɨ�����"); 
     messageBox("����ɨ�裡"); 
    }
}
