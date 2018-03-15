package com.javahis.ui.dev;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestDTool;
import jdo.dev.DevOutRequestMTool;
import jdo.dev.DevOutStorageTool;
import jdo.dev.DevTypeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem; 
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

//DEV_EXWAREHOUSED��  ����UNIT_PRICE,MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE,VERIFY_NO,VERIFY_NO_SEQ,BRAND,SPECIFICATION,MODEL
//UNIT_PRICE,MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE,BRAND,SPECIFICATION,MODEL
/**
 * <p>Title: �豸����(���ⵥ)</p> 
 *   
 * <p>Description: �豸����</p>   
 *  
 * <p>Copyright: Copyright (c) 2013</p>
 *   
 * <p>Company: ProperSoft</p>
 *    
 * @author fux     
 * @version 1.0   
 */                      
@SuppressWarnings({"unchecked"})  
//SEQ_NO:��ӦD���˳���  
//DDSEQ_NO:DD˳���
public class DevOutStorageControl extends TControl {
    //������ϸ������  
    TParm parmD = new TParm();
    /** 
     * ��ʼ������   
     */
    public void onInit() {
        super.onInit();
        addEventListener("DEV_EXWAREHOUSED->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        getTTable("DEV_EXWAREHOUSED").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,"onCreateEditComoponent");
        initTableD();  
        initTableDD();
        onInitComponent();  
        addDRow();       
        onInitOperatorDept();  
//        onChangeFinaFlg(); 
        //ɨ�账��ӦBARCODE��table���BARCODE��ͬʱ��ѡ��
        callFunction("UI|SCAN_BARCODE|addEventListener",TTextFieldEvent.KEY_PRESSED, this, "onChangeBarcode");
        ((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(false);
    }
    /**
     * ����ɨ���¼� 
     */ 
    public void onChangeBarcode(){  
    	//���� BARCODE  �ӿ�  ɨ��  BARCODE
    	TParm ddParm=getTable("DEV_EXWAREHOUSEDD").getParmValue(); 
    	boolean flg = false;
    	for (int i = 0; i < ddParm.getCount("BARCODE"); i++) {
    		String cString=ddParm.getData("BARCODE", i).toString().trim();
    		if (cString.equals(getValueString("SCAN_BARCODE").trim())) {
    			flg = true; 
    			getTable("DEV_EXWAREHOUSEDD").setItem(i, "SELECT_FLG", flg);
				break; 
			} 
		} 
    	this.clearValue("SCAN_BARCODE");
    	getTextField("SCAN_BARCODE").setEditable(true);
    	getTextField("SCAN_BARCODE").grabFocus();
	}
    
//    //fux need modify 
//    /**
//     * ����ɨ��س��¼�(����)
//     */
//    public void onBarcode(){ 
//      if(getValueString("EXWAREHOUSE_DEPT").length() == 0){
//          messageBox("������Ҳ���Ϊ��");
//          return;
//      }
//      if(getValueString("EXWAREHOUSE_USER").length() == 0){
//          messageBox("������Ա����Ϊ��");               
//          return;
//      }  
//      String barcode = getValueString("SCAN_BARCODE"); 
//      TParm parm = ((TTable)getComponent("DEV_EXWAREHOUSEDD")).getParmValue();
//      TTable tabledd = (TTable)getComponent("DEV_EXWAREHOUSEDD");
//      for(int i=0;i<tabledd.getRowCount();i++){ 
//    	  String barcodeDD = parm.getValue("BARCODE", i); 
//    	  if(barcode == barcodeDD){
//    		  tabledd.setItem(i, "SELECT_FLG", "Y");
//    	  }
//      }
//     
////       int devCodeLength = getDevCodeLength();
////       if(barcode.length() < devCodeLength){  
////           messageBox("¼�����������");
////           return;
////       } 
////       //��Ź��������豸��ź���ˮ�Ų�ѯ�豸
////       TParm parm = new TParm();
////       if(barcode.length() > devCodeLength){
////           parm.setData("SEQMAN_FLG","Y");
////           parm.setData("DEV_CODE",barcode.substring(0,devCodeLength));
////           //ȡ�豸���볤�ȵ����һλ
////           parm.setData("DEVSEQ_NO",Integer.parseInt(barcode.substring(devCodeLength,barcode.length())));
////       }
////       //������Ź����豸�����豸��Ų�ѯ
////       else{
////           parm.setData("DEV_CODE",barcode);
////           parm.setData("SEQMAN_FLG", "N");
////       }
////       //��ѯ��Χ�ڱ�����
////       parm.setData("DEPT_CODE",getValue("EXWAREHOUSE_DEPT"));
////       parm = DevOutStorageTool.getInstance().getExStorgeInf(parm);
////       if(parm.getErrCode()<0)
////           return;
////       if(parm.getCount("DEV_CODE") <= 0){
////           messageBox("���޴��豸,������ȷ�������Ƿ���ȷ");
////           return;
////       } 
////       //ɾ������ 
////       ((TTable)getComponent("DEV_EXWAREHOUSED")).removeRow(((TTable)getComponent("DEV_EXWAREHOUSED")).getRowCount() - 1);
////       //����豸�Ƿ����¼��
////       for(int i = 0;i < parm.getCount("DEV_CODE");i++){
////           boolean have = false;
////           for(int j = 0;j < ((TTable)getComponent("DEV_EXWAREHOUSED")).getRowCount();j++){
////               if(parm.getData("DEV_CODE",i).equals(((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(j,3))&&
////                  parm.getData("BATCH_SEQ",i).equals(((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(j,4))&&
////                  parm.getData("DEVSEQ_NO",i).equals(((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(j,5)))
////                   have = true;
////           }
////           if(have) 
////               continue;
////           parm.setData("INWAREHOUSE_DEPT", i, getValue("INWAREHOUSE_DEPT")); 
////           ((TTable)getComponent("DEV_EXWAREHOUSED")).addRow(parm.getRow(i));
////       }
////       ((TTable)getComponent("DEV_EXWAREHOUSED")).setLockColumns("1,4,5,6,"+
////                                                                  "9,14,15,16,"+
////                                                                  "17,18,19,20,23");
// 
//       ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false);
//    }
    /**
     * �ı�flg�¼�
     */
    private void onChangeFinaFlg(){   
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            ((TTable)getComponent("EXWAREHOUSE_TABLE")).setVisible(false); 
          	((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setVisible(true); 
            }              
        //��;״̬���ɱ���,ɾ��      
//        else if (getRadioButton("UPDATE_FLG_B").isSelected()) { 
//          	((TTable)getComponent("EXWAREHOUSE_TABLE")).setVisible(true); 
//       	    ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setVisible(false); 
////            ((TMenuItem) getComponent("delete")).setEnabled(false);  
////            ((TMenuItem) getComponent("save")).setEnabled(false);
//            } 
        //���״̬�²��ɱ���,ɾ��     
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) { 
          	((TTable)getComponent("EXWAREHOUSE_TABLE")).setVisible(true); 
       	    ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setVisible(false); 
//            ((TMenuItem) getComponent("delete")).setEnabled(false);  
//            ((TMenuItem) getComponent("save")).setEnabled(false);
            } 
    }  
    /** 
     * ���ÿ���������Ȩ��
     */
    private void onInitOperatorDept(){
        // ��ʾȫԺҩ�ⲿ��
        if (getPopedem("deptAll")) 
            return; 
//        ((TextFormatDEVOrg)getComponent("EX_DEPT")).setOperatorId(Operator.getID());
//        ((TextFormatDEVOrg)getComponent("EXWAREHOUSE_DEPT")).setOperatorId(Operator.getID());
    }
    /**
     * ��ʼ������Ĭ��ֵ
     */
    public void onInitComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("EXWAREHOUSE_DATE_BEGIN",timestamp);
        setValue("EXWAREHOUSE_DATE_END",timestamp);
        setValue("EX_DEPT",Operator.getDept());
        setValue("EXWAREHOUSE_DATE",timestamp);
        setValue("EXWAREHOUSE_DEPT",Operator.getDept());
        setValue("EXWAREHOUSE_USER",Operator.getID());
    }
    /**
     * ��շ���
     */
    public void onClear(){
        setValue("EX_NO","");
        setValue("IN_DEPT","");
        setValue("EXWAREHOUSE_NO","");
        setValue("INWAREHOUSE_DEPT","");
        setValue("SCAN_BARCODE","");
        onInitComponent();
        initTableD();   
        ((TTable)getComponent("EXWAREHOUSE_TABLE")).removeRowAll();
        ((TTable)getComponent("DEV_EXWAREHOUSED")).removeRowAll();
        ((TTable)getComponent("DEV_EXWAREHOUSEDD")).removeRowAll();
        parmD = new TParm();
        ((TTextFormat)getComponent("EXWAREHOUSE_DATE")).setEnabled(true);
        ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(true);
        ((TTextFormat)getComponent("EXWAREHOUSE_USER")).setEnabled(true);
        ((TTextFormat)getComponent("INWAREHOUSE_DEPT")).setEnabled(true);
        this.setValue("CHECK_IN", "N");
        ((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(false);
        addDRow(); 
    }
    /**
     * ��ʼ���豸���ⵥ��� 
     */  
    public void initTableD(){  
//        String column = "DEL_FLG;SELECT_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;"+
//                        "DEV_CHN_DESC;DESCRIPTION;QTY;STORGE_QTY;"+
//                        "INWAREHOUSE_DEPT;CARE_USER;USE_USER;LOC_CODE;"+
//                        "SETDEV_CODE;UNIT_CODE;UNIT_PRICE;TOT_VALUE;SCRAP_VALUE;"+
//                        "GUAREP_DATE;DEP_DATE;REMARK1;REMARK2;MAN_DATE";
      String column = "DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;" +
      		"QTY;STORGE_QTY;INWAREHOUSE_DEPT;CARE_USER;SETDEV_CODE;" +
      		"UNIT_CODE;REMARK1;REMARK2"; 
        String stringMap[] = StringTool.parseLine(column,";"); 
        TParm tableDDParm = new TParm();  
        for(int i = 0;i<stringMap.length;i++){  
            tableDDParm.addData(stringMap[i],"");
        }
        ((TTable)getComponent("DEV_EXWAREHOUSED")).setParmValue(tableDDParm);
        ((TTable)getComponent("DEV_EXWAREHOUSED")).removeRow(0); 
//        ((TTable)getComponent("DEV_EXWAREHOUSED")).setLockColumns("1,4,5,6,"+
//                                                                  "9,14,15,16,"+
//                                                                  "17,18,19,20,23");
    }
    /**
     * ��ʼ���豸������Ź����� 
     */
    public void initTableDD(){
        String column = "SELECT_FLG;DEVSEQ_NO;DEV_CODE_DETAIL;DEV_CODE;DEV_CHN_DESC;" +
        		"BARCODE;MODEL;SPECIFICATION;MAN_CODE;BRAND;" +
        		"LOC_CODE;UNIT_CODE;UNIT_PRICE";   
        String stringMap[] = StringTool.parseLine(column,";");
        TParm tableDParm = new TParm(); 
        for(int i = 0;i<stringMap.length;i++){  
            tableDParm.addData(stringMap[i],"");
        }                                        
        ((TTable)getComponent("DEV_EXWAREHOUSEDD")).setParmValue(tableDParm);
        ((TTable)getComponent("DEV_EXWAREHOUSEDD")).removeRow(0);
//        ((TTable)getComponent("DEV_EXWAREHOUSEDD")).setLockColumns("2,3,4,5,6,"+
//                                                                   "7,8,9");
    }
    /** 
     * ��ѯ����  
     */  
    public void onQuery(){ 
    	  TParm parm = new TParm(); 
//    	  ���ⵥ��,100;��������,100;�������,100,EXWAREHOUSE_DEPT;������,100,EXWAREHOUSE_DEPT;���쵥��,100;��������,120;���벿��,120,EXWAREHOUSE_DEPT;���ܲ���,120,EXWAREHOUSE_DEPT
//    	 EXWAREHOUSE_NO;EXWAREHOUSE_DATE;EXWAREHOUSE_DEPT;INWAREHOUSE_DEPT;REQUEST_NO;REQUEST_DATE;TO_ORG_CODE;APP_ORG_CODE
    	//��ѯδ��ɵ�
//        if (getRadioButton("UPDATE_FLG_A").isSelected()) { 
//        	 ((TTable)getComponent("EXWAREHOUSE_TABLE")).setVisible(false); 
//        	 ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setVisible(true); 
//        	 String sql = "SELECT ";
//            if(getValueString("EXWAREHOUSE_DATE_BEGIN").length() != 0)
//                parm.setData("REQUEST_DATE_BEGIN",getValue("EXWAREHOUSE_DATE_BEGIN"));
//            if(getValueString("EXWAREHOUSE_DATE_END").length() != 0)
//                parm.setData("REQUEST_DATE_END",getValue("EXWAREHOUSE_DATE_END"));
//            if(getValueString("EX_DEPT").length() != 0) 
//                parm.setData("APP_ORG_CODE",getValue("EX_DEPT")); 
//            if(getValueString("IN_DEPT").length() != 0) 
//                parm.setData("TO_ORG_CODE",getValue("IN_DEPT"));
//            //fux modify 20130806 ȥ���ж�
//            //parm.setData("FINAL_FLG","N"); 
//            if(parm.getNames().length == 0) {
//            	return;      
//            }
//            //�������request����  
//            parm = DevOutRequestMTool.getInstance().queryRequestM(parm);  
//            if(parm.getCount() <= 0){ 
//            	messageBox("�޲�ѯ���ݣ�");
//                return;     
//            }
//            if(parm.getErrCode() < 0){ 
//            	messageBox("��ѯ����");
//                return;     
//            }
//            //System.out.println("requset parm"+parm);  
//            //EXWAREHOUSE_DEPT 
//            //���ⵥ��,100;���쵥��,100;��������,100;�������,100,EXWAREHOUSE_DEPT;������,100,EXWAREHOUSE_DEPT;��������,120;���벿��,120,EXWAREHOUSE_DEPT;���ܲ���,120,EXWAREHOUSE_DEPT
//            //EXWAREHOUSE_NO;REQUEST_NO;EXWAREHOUSE_DATE;EXWAREHOUSE_DEPT;INWAREHOUSE_DEPT;REQUEST_DATE;TO_ORG_CODE;APP_ORG_CODE
//            ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setParmValue(parm);
            //EXWAREHOUSE_NO;REQUEST_NO;EXWAREHOUSE_DATE;EXWAREHOUSE_DEPT;INWAREHOUSE_DEPT;REQUEST_DATE;TO_ORG_CODE;APP_ORG_CODE
//        } 
    	//��ѯ��;
//        else if (getRadioButton("UPDATE_FLG_B").isSelected()) { 
//       	 ((TTable)getComponent("EXWAREHOUSE_TABLE")).setVisible(true);
//    	 ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setVisible(false);  
//        if(getValueString("EX_NO").length() != 0)   
//            parm.setData("EXWAREHOUSE_NO",getValue("EX_NO")); 
//        if(getValueString("EXWAREHOUSE_DATE_BEGIN").length() != 0)
//            parm.setData("EXWAREHOUSE_DATE_BEGIN",getValue("EXWAREHOUSE_DATE_BEGIN"));
//        if(getValueString("EXWAREHOUSE_DATE_END").length() != 0)
//            parm.setData("EXWAREHOUSE_DATE_END",getValue("EXWAREHOUSE_DATE_END"));
//        if(getValueString("EX_DEPT").length() != 0) 
//            parm.setData("EXWAREHOUSE_DEPT",getValue("EX_DEPT")); 
//        if(getValueString("IN_DEPT").length() != 0)  
//            parm.setData("INWAREHOUSE_DEPT",getValue("IN_DEPT"));
//        //fux modify 20130806 ȥ���ж� 
//        //parm.setData("FINAL_FLG","Y");    
//        if(parm.getNames().length == 0)  {
//            return;    
//        }
//        parm = DevOutStorageTool.getInstance().selectDevOutStorageInf(parm);
//        //messageBox(""+parm); 
//        if(parm.getCount() <= 0){ 
//        	messageBox("�޲�ѯ���ݣ�");
//            return;     
//        }
//        if(parm.getErrCode() < 0){ 
//        	messageBox("��ѯ����");
//            return;      
//        }
//        ((TTable)getComponent("EXWAREHOUSE_TABLE")).setParmValue(parm); 
//          }  
    	//��ѯ����ɵ� 
//        else if (getRadioButton("UPDATE_FLG_C").isSelected()) { 
       	 ((TTable)getComponent("EXWAREHOUSE_TABLE")).setVisible(true);
//    	 ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT")).setVisible(false); 
        if(getValueString("EX_NO").length() != 0)    
            parm.setData("EXWAREHOUSE_NO",getValue("EX_NO")); 
        if(getValueString("EXWAREHOUSE_DATE_BEGIN").length() != 0)
            parm.setData("EXWAREHOUSE_DATE_BEGIN",getValue("EXWAREHOUSE_DATE_BEGIN"));
        if(getValueString("EXWAREHOUSE_DATE_END").length() != 0)
            parm.setData("EXWAREHOUSE_DATE_END",getValue("EXWAREHOUSE_DATE_END"));
        if(getValueString("EX_DEPT").length() != 0) 
            parm.setData("EXWAREHOUSE_DEPT",getValue("EX_DEPT")); 
        if(getValueString("IN_DEPT").length() != 0)  
            parm.setData("INWAREHOUSE_DEPT",getValue("IN_DEPT"));
        //fux modify 20130806 ȥ���ж�  
        //parm.setData("FINAL_FLG","Y");
      //��ѯ����ɵ� 
        if (getRadioButton("UPDATE_FLG_C").isSelected()) {
        	parm.setData("DISCHECK_FLG","Y");
        }else{
        	parm.setData("DISCHECK_FLG","N");
        }
        if(parm.getNames().length == 0) {
            return;    
        } 
        parm = DevOutStorageTool.getInstance().selectDevOutStorageInf(parm);
        //messageBox(""+parm); 
        if(parm.getCount() <= 0){ 
        	messageBox("�޲�ѯ���ݣ�");
        	((TTable)getComponent("EXWAREHOUSE_TABLE")).removeRowAll();
        	((TTable)getComponent("DEV_EXWAREHOUSED")).removeRowAll();
        	((TTable)getComponent("DEV_EXWAREHOUSEDD")).removeRowAll();
        	addDRow();
            return;     
        }
        if(parm.getErrCode() < 0){
        	messageBox("��ѯ����");
            return;      
        }
        ((TTable)getComponent("EXWAREHOUSE_TABLE")).setParmValue(parm); 
//          } 
    }

    /**
     * �õ��豸���볤��
     * @return int
     */
    private int getDevCodeLength(){
        TParm parm = DevTypeTool.getInstance().getDevRule();
        return parm.getInt("TOT_NUMBER",0);
    }
    /** 
     * ����Ƿ������Ҫ��������  
     * @return boolean
     */                
    private boolean onSaveCheck(){                                
        TTable tableD = (TTable)getComponent("DEV_EXWAREHOUSED");
        TTable tableDD = (TTable)getComponent("DEV_EXWAREHOUSEDD"); 
        double contdd = 0;    
        //fux modify 20130806 ����-1 ȥ������ 
        for(int i = 0;i<tableD.getRowCount()-1;i++){
        	//�������   
        	String dept = this.getValueString("EXWAREHOUSE_DEPT"); 
        	//������
        	String deptTable = tableD.getItemString(i, "INWAREHOUSE_DEPT");
        	if(dept == deptTable){  
        		this.messageBox("��ѡ����ȷ�ĳ�����ң�");
        		return true;
        	}  
     	   if(tableD.getItemData(i,"SEQMAN_FLG").equals("N"))
     		   continue;    
     	   double contd = tableD.getItemDouble(i, "QTY");
     	   for(int j = 0;j<tableDD.getRowCount();j++){  
       		if ("N".equals(tableDD.getItemData(j, "SELECT_FLG").toString())) {
                 continue;
 			}       
       		else     
       			contdd++;  
     	   }      
     	   if(contd != contdd){ 
     		   this.messageBox("�豸"+tableD.getItemData(i,"DEV_CODE").toString()+"��ϸ��ѡ�������������������һ��");
             return true;  
     	   } 
        }      
        int rowCount = 0;
        for(int i = 0;i<((TTable)getComponent("DEV_EXWAREHOUSED")).getRowCount();i++){
            if(("" + ((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(i,0)).equals("N")
               //����в�Ϊ0	
               //&&
               //("" + ((TTable)getComponent("DEV_EXWAREHOUSED")).getValueAt(i,4)).length()!=0
               )
                rowCount++;
        }    
        if(((TTable)getComponent("EXWAREHOUSE_TABLE")).getSelectedRow() < 0 &&       
           rowCount == 0){
            messageBox("�ޱ�����Ϣ");    
            return true;
        }
        return false;
   }
   /**
    * ���涯��
    */
   public void onSave(){   
	   if (getRadioButton("UPDATE_FLG_C").isSelected()) {
		   return;
	   }
        getTTable("EXWAREHOUSE_TABLE").acceptText();
        getTTable("DEV_EXWAREHOUSED").acceptText();
        getTTable("DEV_EXWAREHOUSEDD").acceptText(); 
        if(onSaveCheck()) 
            return; 
        if(getValueString("EXWAREHOUSE_DEPT").length() == 0){ 
            messageBox("������Ҳ���Ϊ��");
            return;
        }
        if(getValueString("EXWAREHOUSE_USER").length() == 0){
            messageBox("������Ա����Ϊ��");
            return;
        } 
        TTable tableD = (TTable)getComponent("DEV_EXWAREHOUSED");
        for(int i = 0;i < tableD.getRowCount();i++){
            if((tableD.getValueAt(i,3) + "").length() == 0)  
                continue;   
            //��7��Ϊ���ÿ���
            if((tableD.getValueAt(i,7) + "").length() == 0){ 
                messageBox("��"+(i+1)+"�����ÿ��Ҳ���Ϊ��");
                return;
            }    
            //��8��Ϊ������
            if((tableD.getValueAt(i,8) + "").length() == 0){
                messageBox("��"+(i+1)+"�б����˲���Ϊ��");
                return;  
            }
        } 
       //�ж����������޸�
       TTable table = (TTable)getComponent("EXWAREHOUSE_TABLE");
//       if(table.getSelectedRow() < 0)
           onNew();
//       else
//           onUpdate();
   }
   /**
    * �����޸Ķ��� 
    */
   public void onUpdate(){
        TTable tableM = ((TTable)getComponent("EXWAREHOUSE_TABLE"));
        int row = tableM.getSelectedRow();
        if(row < 0)
            return;
        TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
        if(tableD.getRowCount() <= 0)
            return;
        TParm parm = tableD.getParmValue();
        TParm parmDTransport = new TParm();
        Timestamp timestamp = SystemTool.getInstance().getDate();
        for(int i = 0;i<parm.getCount("DEV_CODE");i++){
            if(parm.getValue("INWAREHOUSE_DEPT",i).length() == 0){
                messageBox("��"+(i + 1)+"�������Ҳ���Ϊ��");
                return;
            } 
            if(compareTo(parmD,parm,i))
                continue;
            cloneTParm(parm,parmDTransport,i);
            parmDTransport.addData("EXWAREHOUSE_DEPT",parm.getData("INWAREHOUSE_DEPT",i));
            parmDTransport.addData("EXWAREHOUSE_NO",tableM.getValueAt(row,0));
            parmDTransport.addData("OPT_USER",Operator.getID());
            parmDTransport.addData("OPT_DATE",timestamp);
            parmDTransport.addData("OPT_TERM",Operator.getIP());
        }
        //������
        parmDTransport = TIOM_AppServer.executeAction(
            "action.dev.DevAction","updateExStorageReceipt", parmDTransport);
        if(parmDTransport.getErrCode() < 0){
            messageBox("����ʧ��");
            return;
        }
        messageBox("����ɹ�");
        onPrintAction(""+tableM.getValueAt(row,0));
        parmD = new TParm();
        onTableMClick();
    }
   
   
   
   /**
    * ���뵥ϸ������
    * @param parm TParm
    * @return TParm
    */
   public TParm getRequestDData(TParm parm) {   
//       TTable tableM = ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT"));
       TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
       TParm requestD = new TParm();
       for (int i = 0; i < tableD.getRowCount(); i++) {
//           if ("N".equals(tableD.getItemString(i, "SELECT_FLG"))) {
//               continue;
//           }
//           else 
           if ("".equals(tableD.getParmValue().getValue("DEV_CODE", i))) {
               continue; 
           }
           else {   
        	   String requsetNo = parm.getValue("REQUEST_NO",0);
               requestD.addData("REQUEST_NO", requsetNo); 
               requestD = DevOutRequestDTool.getInstance().queryRequestD(requsetNo);    
               //�������ŵ����������Ƿ�=��������
               //DEV_REQUESTD ��QTY�Ƿ�=�����ϵ������  
               if (tableD.getItemDouble(i, "QTY")== requestD.getDouble("QTY",i)) {
                   requestD.addData("FINA_TYPE", "3"); 
               }            
               else {
                   requestD.addData("FINA_TYPE", "1"); 
               }
               requestD.addData("OPT_USER", Operator.getID());
               requestD.addData("OPT_DATE",
                                SystemTool.getInstance().getDate());
               requestD.addData("OPT_TERM", Operator.getIP());
           }
       }
       parm.setData("REQUEST_D", requestD.getData());
       return parm;
   }
            
   /**
    * ���뵥��������
    * @param parm TParm
    * @return TParm
    */
   public TParm getRequestMData(TParm parm) {
       TParm requestM = new TParm();
       requestM.setData("REQUEST_NO", parm.getValue("REQUEST_NO",0));
       boolean flg = true;  
       TParm requestD = parm.getParm("REQUEST_D");
       for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
           if ("1".equals(requestD.getValue("FINA_TYPE", i))) {
               flg = false;
               break;
           } 
       } 
       if (flg) { 
           requestM.setData("FINAL_FLG", "Y");
       }  
       else {
           requestM.setData("FINAL_FLG", "N");
       }
       requestM.setData("OPT_USER", Operator.getID());
       requestM.setData("OPT_DATE",
                        SystemTool.getInstance().getDate());
       requestM.setData("OPT_TERM", Operator.getIP());

       parm.setData("REQUEST_M", requestM.getData());
       return parm;
   }    
    @SuppressWarnings("rawtypes") 
    /**
     * ������������ 
     */
    public void onNew(){  
    	//�������
    	//TTable tablem = ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT"));
        TTable tabled = ((TTable)getComponent("DEV_EXWAREHOUSED"));
        TTable tabledd = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
        if(tabled.getRowCount() <= 0){ 
            return;
        }
        TTable tableM = ((TTable)getComponent("EXWAREHOUSE_TABLE"));
        System.out.println("EXWAREHOUSE_TABLE:"+tableM.getParmValue());
        if(this.getValue("CHECK_IN").equals("N") && tableM.getParmValue()!= null && tableM.getParmValue().getCount()>0){
        	this.messageBox("�빴ѡ���");
			return;
        }
        Timestamp timestamp = SystemTool.getInstance().getDate();
        Timestamp date = StringTool.getTimestamp(timestamp.toString(),"yyyy-MM-dd");
        TParm parm = tabled.getParmValue();
        TParm parmXH = tabledd.getParmValue();  
        TParm parmTransPort = new TParm();
        TParm dParm = new TParm();  
        TParm mParm = new TParm();
        TParm ddparm = new TParm();
        TParm stockMParm = new TParm();
        TParm stockDParm = new TParm(); 
        TParm stockDDParm = new TParm();
        //20150602 wangjc add start
        //���ȷ��
        String inwarehouseNo = "";
        if(this.getValue("CHECK_IN").equals("Y")){
        	inwarehouseNo = DevInStorageTool.getInstance().getInwarehouseNo();
        }
        TParm inMParm = new TParm();
        TParm inDParm = new TParm();
        TParm inDDParm = new TParm();    
        TParm inStockMParm = new TParm(); 
        TParm inStockDParm = new TParm();  
        TParm inStockDDParm = new TParm();
        //20150602 wangjc add end
//        TParm RequestMParm = new TParm();
//        TParm RequestDParm = new TParm();
//        int row = tablem.getSelectedRow();  
//        RequestMParm.setData("REQUEST_NO", tablem.getItemData(row, "REQUEST_NO"));
//        RequestDParm.setData("REQUEST_NO", tablem.getItemData(row, "REQUEST_NO"));
        Map mapM = new HashMap();
//        Map mapStockM = new HashMap();
        String exWarehouseNo = "";
        if(this.getValue("CHECK_IN").equals("N")){
        	exWarehouseNo = DevOutStorageTool.getInstance().getExwarehouseNo();
        }else{
        	exWarehouseNo = this.getValueString("EXWAREHOUSE_NO");
        }
        //����seq
        int num = 0;
        for(int i = 0;i < parm.getCount("DEV_CODE");i++){
            if(parm.getValue("DEV_CODE",i).length() == 0)
                continue;
            if(parm.getValue("DEL_FLG",i).equals("Y"))
                continue;
            //���ó��ⵥ������Ϣ  
            if(mapM.get(parm.getValue("INWAREHOUSE_DEPT",i)) == null){ 
                mapM.put(parm.getValue("INWAREHOUSE_DEPT",i),exWarehouseNo);
                mParm.addData("EXWAREHOUSE_NO",exWarehouseNo);
                mParm.addData("EXWAREHOUSE_DATE",date); 
                mParm.addData("EXWAREHOUSE_USER",getValueString("EXWAREHOUSE_USER"));
                mParm.addData("EXWAREHOUSE_DEPT",getValueString("EXWAREHOUSE_DEPT"));
                mParm.addData("INWAREHOUSE_DEPT",parm.getValue("INWAREHOUSE_DEPT",i));
                mParm.addData("OPT_USER",Operator.getID());
                mParm.addData("OPT_DATE",timestamp);
                mParm.addData("OPT_TERM",Operator.getIP());
//                mParm.addData("DISCHECK_FLG","Y");
                mParm.addData("DISCHECK_FLG",this.getValue("CHECK_IN"));
                //20150602 wangjc add start
                //���ȷ��
                //������ⵥ������Ϣ 
                inMParm.addData("INWAREHOUSE_NO",inwarehouseNo); 
                inMParm.addData("VERIFY_NO",exWarehouseNo);     
                inMParm.addData("INWAREHOUSE_DATE",StringTool.getTimestampDate(timestamp));
                inMParm.addData("INWAREHOUSE_USER",getValue("EXWAREHOUSE_USER"));
                inMParm.addData("INWAREHOUSE_DEPT",getValue("INWAREHOUSE_DEPT")); 
                inMParm.addData("OPT_USER",Operator.getID()); 
                inMParm.addData("OPT_DATE",timestamp); 
                inMParm.addData("OPT_TERM",Operator.getIP());
                //INWAREHOUSE_NO,VERIFY_NO,INWAREHOUSE_DATE,INWAREHOUSE_USER,
                //INWAREHOUSE_DEPT,OPT_USER,OPT_DATE,OPT_TERM
                //20150602 wangjc add end
            }
            //System.out.println("mParm"+mParm); 
            stockMParm.addData("QTY", tabled.getItemData(i, "QTY"));  
            //stockM���豸��С����ΪΨһ����DEV_CODE
            //stockMParm.addData("DEPT_CODE", this.getValueString("EXWAREHOUSE_DEPT"));
            stockMParm.addData("DEV_CODE", tabled.getItemData(i, "DEV_CODE"));
            stockMParm.addData("OPT_USER", Operator.getID());
            stockMParm.addData("OPT_DATE", timestamp);
            stockMParm.addData("OPT_TERM", Operator.getIP());
            //20150602 wangjc add start
            inStockMParm.addData("DEV_CODE", tabled.getItemData(i, "DEV_CODE"));
            inStockMParm.addData("QTY", tabled.getItemData(i, "QTY"));
            inStockMParm.addData("OPT_USER", Operator.getID()); 
            inStockMParm.addData("OPT_DATE", timestamp);  
            inStockMParm.addData("OPT_TERM", Operator.getIP()); 
            inStockMParm.addData("REGION_CODE", Operator.getRegion());  
            inStockMParm.addData("STOCK_FLG", "");
            //20150602 wangjc add end
            //System.out.println("stockMParm" + stockMParm);
            //���ó��ⵥ��ϸ��Ϣ
            //ͬ��D���parm��dParm�� 
            //SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEVSEQ_NO;
            //DEV_CHN_DESC;DESCRIPTION;QTY;STORGE_QTY;INWAREHOUSE_DEPT;
            //CARE_USER;USE_USER;LOC_CODE;SETDEV_CODE;UNIT_CODE;
            //UNIT_PRICE;TOT_VALUE;SCRAP_VALUE;GUAREP_DATE;DEP_DATE;
            //REMARK1;REMARK2;MAN_DATE            
//            EXWAREHOUSE_NO,SEQ_NO,REMARK1,REMARK2,DEV_CODE        
//            QTY,CARE_USER,USE_USER,LOC_CODE,OPT_USER     
//            OPT_DATE,OPT_TERM
            //��Ҫ���������
            double qty = tabled.getItemDouble(i, "QTY"); 
            String devCode = parm.getValue("DEV_CODE", i);  
            String deptCode = this.getValueString("EXWAREHOUSE_DEPT"); 
//            messageBox("qty"+qty); 
//            messageBox("devCode"+devCode);  
//            messageBox("deptCode"+deptCode);        
            //int D_qty = Integer.parseInt(parm.getValue("QTY"));
       	    dParm.addData("EXWAREHOUSE_NO",mapM.get(parm.getValue("INWAREHOUSE_DEPT",i)));
            dParm.addData("EXWAREHOUSE_DEPT", getValueString("EXWAREHOUSE_DEPT"));
            if(parm.getBoolean("SEQMAN_FLG",i)){
            dParm.addData("SEQ_NO",i + 1); 
            num = i + 1; 
            }  
        	else{   
            dParm.addData("SEQ_NO", num + 1);
            num = num + 1;
            }  
            dParm.addData("INWAREHOUSE_DATE", date); 
            //��ע1
            if(tabled.getItemData(i, "REMARK1") != null){
            dParm.addData("REMARK1",tabled.getItemData(i, "REMARK1")); 
            }
            else{   
            dParm.addData("REMARK1","");	 
            }     
            //��ע2
            if(tabled.getItemData(i, "REMARK2") != null){ 
            dParm.addData("REMARK2",tabled.getItemData(i, "REMARK2"));
            }
            else{ 
            dParm.addData("REMARK2","");	 
            }   
            dParm.addData("UNIT_PRICE",tabled.getItemData(i, "UNIT_PRICE"));    
            dParm.addData("DEV_CODE",tabled.getItemData(i, "DEV_CODE"));
            dParm.addData("QTY",qty);       
            //dParm.addData("CARE_USER",tabled.getItemData(i, "CARE_USER"));
            if(tabled.getItemData(i, "CARE_USER") != null){
                dParm.addData("CARE_USER",tabled.getItemData(i, "CARE_USER"));
                }      
                else{   
                dParm.addData("CARE_USER","");	   
                }    
//            if(tabled.getItemData(i, "LOC_CODE") != null){
//                dParm.addData("LOC_CODE",tabled.getItemData(i, "LOC_CODE"));
//                } 
//                else{       
//                dParm.addData("LOC_CODE","");	 
//                }   
            dParm.addData("OPT_USER",Operator.getID()); 
            dParm.addData("OPT_DATE",timestamp);
            dParm.addData("OPT_TERM",Operator.getIP());   
            dParm.addData("DISCHECK_FLG",this.getValue("CHECK_IN"));      
            dParm.addData("UNIT_PRICE",tabled.getItemData(i, "UNIT_PRICE")); 
            //MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE,BRAND,SPECIFICATION,MODEL
            //��������
            if(tabled.getItemData(i, "MAN_DATE") != null){
                dParm.addData("MAN_DATE",tabled.getItemData(i, "MAN_DATE"));
                }      
                else{     
                dParm.addData("MAN_DATE","");	   
                } 
            //��ֵ
            if(tabled.getItemData(i, "SCRAP_VALUE") != null){
                dParm.addData("SCRAP_VALUE",tabled.getItemData(i, "SCRAP_VALUE"));
                }      
                else{   
                dParm.addData("SCRAP_VALUE","");	   
                } 
            //������ֹ����
            if(tabled.getItemData(i, "GUAREP_DATE") != null){
                dParm.addData("GUAREP_DATE",tabled.getItemData(i, "GUAREP_DATE"));
                }      
                else{   
                dParm.addData("GUAREP_DATE","");	   
                } 
            //�۾���ֹ����
            if(tabled.getItemData(i, "DEP_DATE") != null){
                dParm.addData("DEP_DATE",tabled.getItemData(i, "DEP_DATE"));
                }      
                else{   
                dParm.addData("DEP_DATE","");	   
                } 
            //����
            if(tabled.getItemData(i, "BRAND") != null){
                dParm.addData("BRAND",tabled.getItemData(i, "BRAND"));
                }      
                else{   
                dParm.addData("BRAND","");	   
                } 
            //���
            if(tabled.getItemData(i, "SPECIFICATION") != null){
                dParm.addData("SPECIFICATION",tabled.getItemData(i, "SPECIFICATION"));
                }      
                else{   
                dParm.addData("SPECIFICATION","");	   
                } 
            //�ͺ� 
            if(tabled.getItemData(i, "MODEL") != null){
                dParm.addData("MODEL",tabled.getItemData(i, "MODEL"));
                }      
                else{   
                dParm.addData("MODEL","");	   
                } 
            
//            System.out.println("����dParm"+dParm);
            //20160602 wangjc add start
            inDParm.addData("SEQ_NO", Integer.valueOf(i + 1));    
            inDParm.addData("DEV_CODE", tabled.getItemData(i, "DEV_CODE"));
            inDParm.addData("DEV_CHN_DESC", tabled.getItemData(i, "DEV_CHN_DESC"));   
            inDParm.addData("SEQMAN_FLG", tabled.getItemData(i, "SEQMAN_FLG"));
            inDParm.addData("QTY", qty);        
            inDParm.addData("UNIT_PRICE", tabled.getItemData(i, "UNIT_PRICE"));  
            inDParm.addData("MAN_DATE", tabled.getItemData(i, "MAN_DATE"));               
            // dParm.addData("SCRAP_VALUE", tableParm.getValue("LAST_PRICE",i));
            inDParm.addData("SCRAP_VALUE", 0);   
//            inDParm.addData("GUAREP_DATE", tabled.getItemData(i, "GUAREP_DATE"));
          //������ֹ����
            if(tabled.getItemData(i, "GUAREP_DATE") != null){
            	inDParm.addData("GUAREP_DATE",tabled.getItemData(i, "GUAREP_DATE"));
            }else{   
            	inDParm.addData("GUAREP_DATE","");	   
            }
            if(tabled.getItemData(i, "DEP_DATE") != null){
            	inDParm.addData("DEP_DATE",tabled.getItemData(i, "DEP_DATE"));
            } else{   
            	inDParm.addData("DEP_DATE","");	   
            }
            inDParm.addData("FILES_WAY", "");
            inDParm.addData("VERIFY_NO", mapM.get(parm.getValue("INWAREHOUSE_DEPT",i)));
//            inDParm.addData("VERIFY_NO_SEQ", tabled.getItemData(i,"VERIFY_NO_SEQ"));
            if(parm.getBoolean("SEQMAN_FLG",i)){
            	inDParm.addData("VERIFY_NO_SEQ",i + 1); 
            }else{   
            	inDParm.addData("VERIFY_NO_SEQ", num);
            }
          //����
            if(tabled.getItemData(i, "BRAND") != null){
            	inDParm.addData("BRAND",tabled.getItemData(i, "BRAND"));
            }else{   
            	inDParm.addData("BRAND","");	   
            } 
            //���
            if(tabled.getItemData(i, "SPECIFICATION") != null){
            	inDParm.addData("SPECIFICATION",tabled.getItemData(i, "SPECIFICATION"));
            }else{   
                	inDParm.addData("SPECIFICATION","");	   
            } 
            //�ͺ� 
            if(tabled.getItemData(i, "MODEL") != null){
            	inDParm.addData("MODEL",tabled.getItemData(i, "MODEL"));
            }else{   
                	inDParm.addData("MODEL","");	   
            }
            inDParm.addData("INWAREHOUSE_NO", inwarehouseNo);
            inDParm.addData("OPT_USER", Operator.getID());  
            inDParm.addData("OPT_DATE", timestamp); 
            inDParm.addData("OPT_TERM", Operator.getIP());   
            //��ӡ׼�� 
//            inDParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));            
//            inDParm.addData("BRAND", tableParm.getValue("BRAND",i));  
//            inDParm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION",i)); 
//            inDParm.addData("MODEL", tableParm.getValue("MODEL",i));     
//            inDParm.addData("TOT_VALUE",tableParm.getValue("TOT_VALUE",i));
            //20150602 wangjc add end
            //cloneTParm(parm, stockMParm, i);       
            stockDParm.addData("QTY", qty);   
            stockDParm.addData("DEPT_CODE", this.getValueString("EXWAREHOUSE_DEPT"));
            stockDParm.addData("DEV_CODE", tabled.getItemData(i, "DEV_CODE"));
            //for ѭ���Ƚ��ȳ�ԭ��  fux  BATCH_SEQ  
        //����ʹ�����޼����۾���ֹ����   
        //Ӧ�ò�ѯ��������С�����õ�����   
        //fux need modify 20130806     
        stockDParm.addData("OPT_USER", Operator.getID());
        stockDParm.addData("OPT_DATE", timestamp);
        stockDParm.addData("OPT_TERM", Operator.getIP()); 
        //20150602 wangjc add start
        TParm devBaseIn = getDevBase(""+tabledd.getItemData(i, "DEV_CODE"));
        inStockDParm.addData("DEPT_CODE", this.getValueString("INWAREHOUSE_DEPT"));
        inStockDParm.addData("DEV_CODE", tabled.getItemData(i, "DEV_CODE"));
        if(devBaseIn.getData("DEVKIND_CODE", 0) != null){
        	inStockDParm.addData("DEVKIND_CODE", devBaseIn.getData("DEVKIND_CODE", 0));
        }else{   
        	inStockDParm.addData("DEVKIND_CODE","");
        }
//        inStockDParm.addData("DEVKIND_CODE", devBaseIn.getData("DEVKIND_CODE", 0));
        inStockDParm.addData("DEVTYPE_CODE", getDevTypeCode((String) tabled.getValueAt(i, 3)));
        if(tabled.getItemData(i, "DEVPRO_CODE") != null){
        	inStockDParm.addData("DEVPRO_CODE", tabled.getItemData(i, "DEVPRO_CODE"));
        }else{   
        	inStockDParm.addData("DEVPRO_CODE","");
        }
//        inStockDParm.addData("DEVPRO_CODE", tabled.getItemData(i, "DEVPRO_CODE"));
        if(devBaseIn.getData("SETDEV_CODE", 0) != null){
        	inStockDParm.addData("SETDEV_CODE", devBaseIn.getData("SETDEV_CODE", 0));
        }else{   
        	inStockDParm.addData("SETDEV_CODE","");
        }
//        inStockDParm.addData("SETDEV_CODE", devBaseIn.getData("SETDEV_CODE", 0));
//        inStockDParm.addData("SPECIFICATION", tabled.getItemData(i, "SPECIFICATION"));
      //���
        if(tabled.getItemData(i, "SPECIFICATION") != null){
        	inStockDParm.addData("SPECIFICATION", tabled.getItemData(i, "SPECIFICATION"));
        }else{   
        	inStockDParm.addData("SPECIFICATION","");	   
        }
        if(tabled.getItemData(i, "QTY") != null){
        	inStockDParm.addData("QTY", tabled.getItemData(i, "QTY"));
        }else{   
        	inStockDParm.addData("QTY","");
        }
//        inStockDParm.addData("QTY", tabled.getItemData(i, "QTY"));
        if(tabled.getItemData(i, "UNIT_PRICE") != null){
        	inStockDParm.addData("UNIT_PRICE", tabled.getItemData(i, "UNIT_PRICE"));
        }else{   
        	inStockDParm.addData("UNIT_PRICE","");	   
        }
//        inStockDParm.addData("UNIT_PRICE", tabled.getItemData(i, "UNIT_PRICE")); 
        if(devBaseIn.getData("BUYWAY_CODE", 0) != null){
        	inStockDParm.addData("BUYWAY_CODE", devBaseIn.getData("BUYWAY_CODE", 0));
        }else{   
        	inStockDParm.addData("BUYWAY_CODE","");	   
        }
//        inStockDParm.addData("BUYWAY_CODE", devBaseIn.getData("BUYWAY_CODE", 0));
        if(devBaseIn.getData("MAN_NATION", 0) != null){
        	inStockDParm.addData("MAN_NATION", devBaseIn.getData("MAN_NATION", 0));
        }else{   
        	inStockDParm.addData("MAN_NATION","");	   
        }
//        inStockDParm.addData("MAN_NATION", devBaseIn.getData("MAN_NATION", 0));
        if(devBaseIn.getData("MAN_CODE", 0) != null){
        	inStockDParm.addData("MAN_CODE", devBaseIn.getData("MAN_CODE", 0));
        }else{   
        	inStockDParm.addData("MAN_CODE","");	   
        }
//        inStockDParm.addData("MAN_CODE", devBaseIn.getData("MAN_CODE", 0));
        inStockDParm.addData("SUPPLIER_CODE", ""); 
        if(tabled.getItemData(i, "MAN_DATE") != null){
        	inStockDParm.addData("MAN_DATE", tabled.getItemData(i, "MAN_DATE"));
        }else{   
        	inStockDParm.addData("MAN_DATE","");	   
        }
//        inStockDParm.addData("MAN_DATE", tabled.getItemData(i, "MAN_DATE")); 
        inStockDParm.addData("MANSEQ_NO", Integer.valueOf(0)); 
        inStockDParm.addData("FUNDSOURCE", "");
        inStockDParm.addData("APPROVE_AMT", ""); 
        inStockDParm.addData("SELF_AMT", "");
      //������ֹ����
        if(tabled.getItemData(i, "GUAREP_DATE") != null){
        	inStockDParm.addData("GUAREP_DATE", tabled.getItemData(i, "GUAREP_DATE"));
        }else{   
        	inStockDParm.addData("GUAREP_DATE","");	   
        }
        if(devBaseIn.getData("DEPR_METHOD", 0) != null){
        	inStockDParm.addData("DEPR_METHOD", devBaseIn.getData("DEPR_METHOD", 0));
        }else{   
        	inStockDParm.addData("DEPR_METHOD", "");   
        }
//        inStockDParm.addData("DEPR_METHOD", devBaseIn.getData("DEPR_METHOD", 0));
        //stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i, "LAST_PRICE"));
        inStockDParm.addData("SCRAP_VALUE", 0);   
        inStockDParm.addData("QUALITY_LEVEL", "");
        inStockDParm.addData("DEV_CLASS", devBaseIn.getData("DEV_CLASS", 0));
        inStockDParm.addData("STOCK_STATUS", "");
        inStockDParm.addData("SERVICE_STATUS", "");
        inStockDParm.addData("CARE_USER", getValue("EXWAREHOUSE_USER"));
        inStockDParm.addData("USE_USER", "");
        inStockDParm.addData("LOC_CODE", "");       
        inStockDParm.addData("MEASURE_FLG", devBaseIn.getData("MEASURE_FLG", 0));
        inStockDParm.addData("MEASURE_ITEMDESC", devBaseIn.getData("MEASURE_ITEMDESC", 0));
        inStockDParm.addData("MEASURE_DATE", "");
        inStockDParm.addData("OPT_USER", Operator.getID());
        inStockDParm.addData("OPT_DATE", timestamp);
        inStockDParm.addData("OPT_TERM", Operator.getIP()); 
        inStockDParm.addData("INWAREHOUSE_DATE", StringTool.getTimestampDate(timestamp));
        //�۾���ֹ����
        if(tabled.getItemData(i, "DEP_DATE") != null){
        	inStockDParm.addData("DEP_DATE", tabled.getItemData(i, "DEP_DATE"));
        } else{   
        	inStockDParm.addData("DEP_DATE","");	   
        }
         
        //DEP_DATE,BRAND,MODEL  ����  �ͺ�
        if(tabled.getItemData(i, "BRAND") != null){
        	inStockDParm.addData("BRAND", tabled.getItemData(i, "BRAND"));
        }else{   
        	inStockDParm.addData("BRAND","");	   
        } 
      //�ͺ� 
        if(tabled.getItemData(i, "MODEL") != null){
        	inStockDParm.addData("MODEL", tabled.getItemData(i, "MODEL"));
        }else{   
        	inStockDParm.addData("MODEL","");	   
        }
        
        //20150602 wangjc add end
        //�����ŵ�����µ�ȡ��С���ŵ��߼�(���ƺĲ�)�����豸����Ҫ
            //��ɶ���ۼ� 
//            TParm parmTest = new TParm();
//            parmTest.setData("TEST1",getStockD(devCode,deptCode));
//            TParm parmTest2 = new TParm();
//            parmTest.setData("TEST2",parmTest2);
//           TParm batchParm = saveDevStockD(parmTest,qty);
//            batchParm =(TParm) batchParm.getData("TEST2");         	
//            for(int m =0; m < batchParm.getCount("BATCH_SEQ");m++){ 
//            	 dParm.addData("EXWAREHOUSE_NO",mapM.get(parm.getValue("INWAREHOUSE_DEPT",i)));
//                 dParm.addData("EXWAREHOUSE_DEPT", getValueString("EXWAREHOUSE_DEPT"));
//                 if(parm.getBoolean("SEQMAN_FLG",i)){
//                 dParm.addData("SEQ_NO",i + m + 1); 
//                 num = i + m + 1; 
//                 } 
//             	 else{  
//                 dParm.addData("SEQ_NO", num + 1);
//                 num = num + 1;
//                 }  
//                 dParm.addData("INWAREHOUSE_DATE", date); 
//                 //��ע1
//                 if(tabled.getItemData(i, "REMARK1") != null){
//                 dParm.addData("REMARK1",tabled.getItemData(i, "REMARK1")); 
//                 }
//                 else{  
//                 dParm.addData("REMARK1","");	 
//                 }     
//                 //��ע2
//                 if(tabled.getItemData(i, "REMARK2") != null){ 
//                 dParm.addData("REMARK2",tabled.getItemData(i, "REMARK2"));
//                 }
//                 else{ 
//                 dParm.addData("REMARK2","");	 
//                 }    
//                 dParm.addData("DEV_CODE",tabled.getItemData(i, "DEV_CODE"));
//                 dParm.addData("QTY",batchParm.getData("QTY",m));       
//                 dParm.addData("CARE_USER",tabled.getItemData(i, "CARE_USER"));
//                if(tabled.getItemData(i, "USE_USER") != null){
//                     dParm.addData("USE_USER",tabled.getItemData(i, "USE_USER"));
//                    }      
//                    else{   
//                     dParm.addData("USE_USER","");	 
//                     }  
//                if(tabled.getItemData(i, "LOC_CODE") != null){
//                    dParm.addData("LOC_CODE",tabled.getItemData(i, "LOC_CODE"));
//                     } 
//                     else{       
//                     dParm.addData("LOC_CODE","");	 
//                     }   
//                 dParm.addData("OPT_USER",Operator.getID()); 
//                 dParm.addData("OPT_DATE",timestamp);
//                 dParm.addData("OPT_TERM",Operator.getIP()); 
//                 dParm.addData("DISCHECK_FLG","Y");      
//                 dParm.addData("UNIT_PRICE",tabled.getItemData(i, "UNIT_PRICE")); 
//                 //���ݴ������������м���...     
//                 //��С��batch_seq(�ݹ�ʹ��)dParm ��  stockDParm  ��SPCINVRecordAction
//                 //DD���batch_seq  
//                 //��������һ������,���ǡǡ�ֿ��(N�����ŵ�)��Ȼ�����4��(��С���Ų����Թ���
//                 //�����Ҵ�DD���������4��������զ��)  
//                 dParm.addData("BATCH_SEQ", batchParm.getData("BATCH_SEQ",m));      
//                 System.out.println("����dParm"+dParm);     
//                 //cloneTParm(parm, stockMParm, i);       
//                 stockDParm.addData("QTY", batchParm.getDouble("QTY",m));   
//                 stockDParm.addData("DEPT_CODE", this.getValueString("EXWAREHOUSE_DEPT"));
//                 stockDParm.addData("DEV_CODE", tabled.getItemData(i, "DEV_CODE"));
//                 //for ѭ���Ƚ��ȳ�ԭ��  fux  BATCH_SEQ  
//             //����ʹ�����޼����۾���ֹ����   
//             //Ӧ�ò�ѯ��������С�����õ����� 
//             //fux need modify 20130806    
//             stockDParm.addData("BATCH_SEQ", batchParm.getData("BATCH_SEQ",m));  
//             stockDParm.addData("OPT_USER", Operator.getID());
//             stockDParm.addData("OPT_DATE", timestamp);
//             stockDParm.addData("OPT_TERM", Operator.getIP()); 
//             } 
                
            //���ÿ��������Ϣ�����ϲ���һ�� 
//            if(mapStockM.get(parm.getValue("INWAREHOUSE_DEPT", i) +
//                             parm.getValue("DEV_CODE", i)) == null){
//                cloneTParm(parm, stockMParm, i);
//                stockMParm.addData("INWAREHOUSE_DATE", date);
//                stockMParm.addData("OPT_USER",Operator.getID());
//                stockMParm.addData("OPT_DATE",timestamp);
//                stockMParm.addData("OPT_TERM",Operator.getIP()); 
//                mapStockM.put(parm.getValue("INWAREHOUSE_DEPT", i) +
//                              parm.getValue("DEV_CODE", i),stockMParm.getCount("DEV_CODE") - 1);
//            }
//            else{
//                int j = Integer.parseInt("" +  mapStockM.get(parm.getValue("INWAREHOUSE_DEPT", i) +
//                                                             parm.getValue("DEV_CODE", i)));
//                stockMParm.setData("QTY",j,stockMParm.getInt("QTY",j) + parm.getInt("QTY",i));
//            }     
        	TParm DDParm = DevOutStorageTool.getInstance().queryStockDD(devCode,deptCode);

            if(parm.getValue("SEQMAN_FLG",i).equals("N")) 
                continue;  
            for(int k = 0;k < parmXH.getCount("DEV_CODE");k++){ 
          	  if ("N".equals(tabledd.getItemString(k,"SELECT_FLG"))) {
                  continue;     
              }          
            	ddparm.addData("EXWAREHOUSE_NO", mapM.get(parm.getValue("INWAREHOUSE_DEPT",i)) );
            	ddparm.addData("DEV_CODE_DETAIL", DDParm.getValue("DEV_CODE_DETAIL",k));
            	//D���      
            	ddparm.addData("SEQ_NO", i + 1);
            	ddparm.addData("DEVSEQ_NO", DDParm.getInt("DEVSEQ_NO",k));
            	//���DD���   (DDSEQ_NO )ȡ��������ֵ��һ(��̨ʽ��������2̨��Ϊ1,2�ţ��ټ���Ϊ3,4��)
            	//ddparm.addData("DDSEQ_NO",k+1); 
            	ddparm.addData("DEV_CODE", DDParm.getValue("DEV_CODE",k));
            	
//            	ddparm.addData("BATCH_SEQ", DDParm.getInt("BATCH_SEQ",k));
            	ddparm.addData("SETDEV_CODE", DDParm.getValue("SETDEV_CODE",k)); 
            	//STOCK_D����  
            	//MAN_DATE      
            	ddparm.addData("MAN_DATE", TypeTool.getTimestamp(DDParm.getTimestamp("MAN_DATE",k)));
            	ddparm.addData("MANSEQ_NO", DDParm.getValue("MANSEQ_NO",k));
            	ddparm.addData("SCRAP_VALUE", DDParm.getInt("SCRAP_VALUE",k));  
            	ddparm.addData("GUAREP_DATE", TypeTool.getTimestamp(DDParm.getTimestamp("GUAREP_DATE",k)));
            	ddparm.addData("DEP_DATE", TypeTool.getTimestamp(DDParm.getTimestamp("DEP_DATE",k)));   
            	ddparm.addData("UNIT_PRICE", DDParm.getInt("UNIT_PRICE",k));	
            	ddparm.addData("OPT_USER", Operator.getID());    
            	ddparm.addData("OPT_DATE", timestamp);     
            	ddparm.addData("OPT_TERM", Operator.getIP()); 
            	ddparm.addData("RFID", "");      
            	ddparm.addData("BARCODE", DDParm.getValue("BARCODE",k)); 
            	ddparm.addData("MODEL", DDParm.getValue("MODEL",k)); 
            	ddparm.addData("BRAND", DDParm.getValue("BRAND",k));
            	ddparm.addData("SPECIFICATION", DDParm.getValue("SPECIFICATION",k));
            	ddparm.addData("SERIAL_NUM", DDParm.getValue("SERIAL_NUM",k));
            	ddparm.addData("MAN_CODE", DDParm.getValue("MAN_CODE",k));
            	ddparm.addData("WIRELESS_IP", DDParm.getValue("WIRELESS_IP",k));
            	ddparm.addData("IP", DDParm.getValue("IP",k));
            	ddparm.addData("TERM", DDParm.getValue("TERM",k));
               	ddparm.addData("LOC_CODE", DDParm.getValue("LOC_CODE",k));
            	ddparm.addData("USE_USER", DDParm.getValue("USE_USER",k));
            	//System.out.println("ddparm"+ddparm);  
            	
            	//20150602 wangjc add start
                //DEVKIND_CODE  IN_DEPT OUT_DEPT 
                String barCode = SystemTool.getInstance().getNo("ALL", "DEV", "BARCODE_NO", "BARCODE_NO");  
                //DEL_FLG;PRINT_FLG;DEVPRO_CODE;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;RFID;MAIN_DEV;MAN_DATE;MAN_SEQ;LAST_PRICE;
                //GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO
                inDDParm.addData("INWAREHOUSE_NO", inwarehouseNo);
                inDDParm.addData("SEQ_NO", Integer.valueOf(i + 1));     
                inDDParm.addData("DEVSEQ_NO", DDParm.getInt("DEVSEQ_NO",k));
                inDDParm.addData("DEV_CODE", DDParm.getValue("DEV_CODE",k));
                inDDParm.addData("DEV_CODE_DETAIL", DDParm.getValue("DEV_CODE_DETAIL",k));
                //��Ź�����ϸ���                         
                inDDParm.addData("SEQMAN_FLG", "Y"); 
                inDDParm.addData("SETDEV_CODE", DDParm.getValue("SETDEV_CODE",k));
                inDDParm.addData("MAN_DATE", TypeTool.getTimestamp(DDParm.getTimestamp("MAN_DATE",k))); 
                //fux 20140523             
                //MANSEQ_NO    SERIAL_NUM      WIRELESS_IP   IP   TERM  LOC_CODE 
                inDDParm.addData("MANSEQ_NO", DDParm.getValue("MANSEQ_NO",k)); 
                inDDParm.addData("SERIAL_NUM", DDParm.getValue("SERIAL_NUM",k)); 
                inDDParm.addData("WIRELESS_IP", DDParm.getValue("WIRELESS_IP",k)); 
                inDDParm.addData("IP", DDParm.getValue("IP",k)); 
                inDDParm.addData("TERM", DDParm.getValue("TERM",k)); 
                inDDParm.addData("LOC_CODE", DDParm.getValue("LOC_CODE",k)); 
                //ddParm.addData("SCRAP_VALUE", ddTable.getItemData(j, "LAST_PRICE"));
                inDDParm.addData("SCRAP_VALUE", 0);   
                //DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_DETAIL_CODE;DEV_CODE;
                //DEVSEQ_NO;DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;MODEL;
                //MAN_CODE;BRAND;MAN_DATE;LAST_PRICE;GUAREP_DATE;DEP_DATE;
                //TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
                inDDParm.addData("GUAREP_DATE", TypeTool.getTimestamp(DDParm.getTimestamp("GUAREP_DATE",k)));
                inDDParm.addData("DEP_DATE", TypeTool.getTimestamp(DDParm.getTimestamp("DEP_DATE",k)));
                inDDParm.addData("UNIT_PRICE", DDParm.getInt("UNIT_PRICE",k));
                inDDParm.addData("OPT_USER", Operator.getID());
                inDDParm.addData("OPT_DATE", timestamp);  
                inDDParm.addData("OPT_TERM", Operator.getIP()); 
                //Ĭ��rfid��Ϊ"" ������ͨ����
                inDDParm.addData("BARCODE", barCode);   
                inDDParm.addData("RFID", ""); 
                //20150602 wangjc add end
                TParm devBase = getDevBase(""+tabledd.getItemData(k, "DEV_CODE"));
                stockDDParm.addData("DEV_CODE", tabledd.getItemData(k, "DEV_CODE"));
                //��Ź�����ϸ���     //�����Ǹ��¶���
                stockDDParm.addData("DEVSEQ_NO",parmXH.getInt("DEVSEQ_NO",k));  
                stockDDParm.addData("REGION_CODE", Operator.getRegion());
                stockDDParm.addData("DEPT_CODE", ""); 
                stockDDParm.addData("WAIT_ORG_CODE", tabled.getItemData(i, "INWAREHOUSE_DEPT"));
                stockDDParm.addData("OPT_USER", Operator.getID());
                stockDDParm.addData("OPT_DATE", timestamp);
                stockDDParm.addData("OPT_TERM", Operator.getIP());
                //20150602 wangjc add start
                inStockDDParm.addData("DEV_CODE_DETAIL", DDParm.getValue("DEV_CODE_DETAIL",k));
                inStockDDParm.addData("DEV_CODE", tabledd.getItemData(k, "DEV_CODE"));
                inStockDDParm.addData("DEVSEQ_NO", parmXH.getInt("DEVSEQ_NO",k)); 
                inStockDDParm.addData("DEPT_CODE", getValue("INWAREHOUSE_DEPT"));
                inStockDDParm.addData("WAST_FLG", "N");   
                inStockDDParm.addData("WAIT_ORG_CODE", "");
                inStockDDParm.addData("OPT_USER", Operator.getID()); 
                inStockDDParm.addData("OPT_DATE", timestamp);
                inStockDDParm.addData("OPT_TERM", Operator.getIP());
                //20150602 wangjc add end
                //����ʱ�������۾�ֵ�ɽ���ʱ����  
//                Double mdepPrice = 0.00;
//                Double depPrice = 0.00; 
//                Double currPrice = 0.00;  
//                //���±��¼��۾�  
//                stockDDParm.addData("MDEP_PRICE", 0);
//                //�ۼ��۾�
//                stockDDParm.addData("DEP_PRICE", 0); 
//                //��ֵ
//                stockDDParm.addData("CURR_PRICE", 0); 
                //System.out.println("stockDDParm"+stockDDParm); 
            }   
        }        
//      getRequestMData(RequestMParm);    
//      getRequestDData(RequestDParm);
        parmTransPort.setData("DEV_EXWAREHOUSEM",mParm.getData());
        parmTransPort.setData("DEV_EXWAREHOUSED",dParm.getData());
        parmTransPort.setData("DEV_EXWAREHOUSEDD",ddparm.getData());
        //M��D���� DD���� 
        parmTransPort.setData("DEV_STOCKM",stockMParm.getData()); 
        parmTransPort.setData("DEV_STOCKD",stockDParm.getData());  
        parmTransPort.setData("DEV_STOCKDD",stockDDParm.getData());
//      parmTransPort.setData("DEV_REQUESTM",RequestMParm.getData());
//      parmTransPort.setData("DEV_REQUESTD",RequestDParm.getData());
        //20150602 wangjc add start
        parmTransPort.setData("DEV_INWAREHOUSEM",inMParm.getData());
        parmTransPort.setData("DEV_INWAREHOUSED",inDParm.getData());
        parmTransPort.setData("DEV_INWAREHOUSEDD",inDDParm.getData());
        parmTransPort.setData("DEV_STOCKM_IN",inStockMParm.getData());
      //�����µ�D�����ݣ���030101��⣬���⵽0202����ʱӦ�ò���0202���ݣ�
        parmTransPort.setData("DEV_STOCKD_IN",inStockDParm.getData());
        //���¶���������DEPT_CODE��WAIT_ORG_CODE 
        parmTransPort.setData("DEV_STOCKDD_IN",inStockDDParm.getData());
        parmTransPort.setData("CHECK_IN",this.getValue("CHECK_IN"));
        //20150602 wangjc add end
        parmTransPort = TIOM_AppServer.executeAction(
            "action.dev.DevAction","generateExStorageReceipt", parmTransPort); 
        if(parmTransPort.getErrCode() < 0) {
            messageBox("����ʧ��");  
            return;   
        }    
        messageBox("����ɹ�");
        if(this.getValue("CHECK_IN").equals("Y")){
        	for(int i = 0;i < mParm.getCount("EXWAREHOUSE_NO");i++){ 
        		//messageBox("EXWAREHOUSE_NO"+mParm.getValue("EXWAREHOUSE_NO",i));
        		onPrintAction(mParm.getValue("EXWAREHOUSE_NO",i)); 
        	}    
        }
//        onClear(); 
        onQuery(); 
    } 
    
    /**
     * �õ��豸���������Ϣ
     * @param code String
     * @return String
     * 20150602 wangjc add
     */
    private String getDevTypeCode(String code){
       TParm parm = DevTypeTool.getInstance().getDevRule();
       int classify1 = parm.getInt("CLASSIFY1",0);         
       return code.substring(0,classify1);
    } 
    
    
    
    //ѭ���ۿ��ȡ���ŷ���  
    /**
     * ѭ����inv_stockd 
     * d ΪҪ�۵�����(�ӽ�����ȡ�ó�������)
     */
    public TParm saveDevStockD(TParm parmTest, double d) {
    	//����5�� batch_seq6��,��num=1
    	//����5��batch_seq��Ҫ2��������num=2 
    	//��С���ŵ�����
    	TParm devSockDParm = (TParm)parmTest.getData("TEST1");
    	TParm parmTest2 = (TParm)parmTest.getData("TEST2"); 
		double qty = devSockDParm.getDouble("QTY", 0); 
		if(qty >= d){ 
			//��һ����ɣ�����ʵ�� ----------�޸����  
//			System.out.println("qty----111111111----->"+qty);  
//			System.out.println("d------222222222------>"+d);
			//�����С���ŵĹ��ã��򷵻���С����
//			 TParm updateDevStockD = this.updateDevStockD(devSockDParm.getValue("DEPT_CODE", 0), devSockDParm.getValue("DEV_CODE", 0),
//					devSockDParm.getValue("BATCH_SEQ", 0), qty-d,d,devSockDParm);
			parmTest2.addData("STOCK_QTY",d);  
		  		//STOCKDһ������
			parmTest2.addData("DEPT_CODE",devSockDParm.getValue("DEPT_CODE", 0)); 
			parmTest2.addData("DEV_CODE",devSockDParm.getValue("DEV_CODE", 0));
//			parmTest2.addData("BATCH_SEQ",devSockDParm.getValue("BATCH_SEQ", 0));
		  	    //ִ�еݹ�����£����һ��ȡ���źͿ��  
			parmTest2.addData("QTY",  d);
		}   
		else{   
			//��������ݹ�   
			parmTest2.addData("STOCK_QTY",d);  
	  		//STOCKDһ������
		parmTest2.addData("DEPT_CODE",devSockDParm.getValue("DEPT_CODE", 0)); 
		parmTest2.addData("DEV_CODE",devSockDParm.getValue("DEV_CODE", 0)); 
//		parmTest2.addData("BATCH_SEQ",devSockDParm.getValue("BATCH_SEQ", 0));
	  	    //ִ�еݹ�����£����һ��ȡ���źͿ��  
		parmTest2.addData("QTY", qty); 
		d = d - qty; 
//			System.out.println("devSockDParm:::--->"+devSockDParm);
//			System.out.println("DEPT_CODE"+devSockDParm.getCount("DEPT_CODE"));
			if(devSockDParm.getCount("DEPT_CODE")>0){
				devSockDParm.removeRow(0);
				this.saveDevStockD(parmTest, d);
			}else{  				  
				return devSockDParm;
			}
		} 
	
		//System.out.println("devSockDParm"+devSockDParm);
		parmTest.setData("TEST1",devSockDParm);
		parmTest.setData("TEST2",parmTest2);
		return parmTest;
//		return devSockDParm;
	}
    
    /**
     * �ۿⷽ��(���ﷵ��numֵ)
     * org
     * inv
     * batch_seq
     * qty 
     * */   
    public TParm updateDevStockD(String org,String dev,double qty,Double d,TParm devSockDParm){
  	    //��Ϊ���ߵݹ�Ȼ���������������Ի�������MUN,Ӱ�����
  	       devSockDParm.addData("STOCK_QTY",d);  
  		//STOCKDһ������
  	       devSockDParm.addData("DEPT_CODE",org); 
  	       devSockDParm.addData("DEV_CODE",dev);
//  	       devSockDParm.addData("BATCH_SEQ",batch_seq);
  	    //ִ�еݹ�����£����һ��ȡ���źͿ��  
  	       devSockDParm.addData("QTY", qty);
  	   //     }  
  		   if(devSockDParm.getErrCode()<0){ 
  			  return devSockDParm;
  		  } 
    	      return devSockDParm;  
  	}
    /**
     * �õ��豸���ϸ����Ϣ
     * @param devCode String
     * @param deptCode String 
     * @return TParm
     */
    public TParm getStockD(String devCode,String deptCode){
        String SQL=" SELECT * FROM DEV_STOCKD " +
		   " WHERE DEV_CODE = '"+devCode+"'" +
		   " AND DEPT_CODE = '"+deptCode+"'" +
		   " AND QTY > 0 " ;
//		   +   
//		   " ORDER BY BATCH_SEQ ";  
        TParm parm = new TParm(getDBTool().select(SQL));
        return parm; 
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
      * ��ѯՈ�I���������ɳ��쵥��Ϣ   
      */
     public void onGenerateReceipt(){
         if(getValueString("EXWAREHOUSE_DATE").length() == 0){
             messageBox("��¼���������");
             return;
         } 
         onClear();   
         //����Δ���
         ((TTable)getComponent("EXWAREHOUSE_TABLE")).setParmValue(((TTable)getComponent("EXWAREHOUSE_TABLE")).getParmValue());
         TParm parmDiag = (TParm)openDialog("%ROOT%\\config\\dev\\RequestMUI.x");
        // System.out.println("parmDiag"+parmDiag); 
         if(parmDiag == null)    
             return;           
         String requsetNo = parmDiag.getValue("REQUEST_NO");
         //�{��������Ϣ      
         TParm parmM = DevOutRequestMTool.getInstance().queryRequestM(requsetNo);
         //System.out.println("parmM"+parmM);
         //�{��ϵ����Ϣ
         TParm parmD = DevOutRequestDTool.getInstance().queryRequestD(requsetNo);
         //System.out.println("parmD"+parmD); 
         if(parmM.getErrCode() < 0)   
             return;
         if(parmM.getCount() <= 0){ 
             messageBox("�˳��ⵥ�豸�Ѿ�ȫ������");
             return;
         } 
         Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance().getDate().toString(),"yyyy-MM-dd");
         TParm tableParm = new TParm();
         for(int i = 0;i<parmD.getCount();i++){
             //ȡ�ó������� 
             String exWarehouseDate = getValueString("EXWAREHOUSE_DATE");
             //����ʹ�����޼����۾���ֹ����
             int year = Integer.parseInt(exWarehouseDate.substring(0, 4)) +
                        parmM.getInt("USE_DEADLINE", i);
             String depDate = year + exWarehouseDate.substring(4,exWarehouseDate.length());
             //�����۾���ֹ���ڱ�����ֹ�����Լ��豸����ȡ������  
             //����ϸ�����DEV_CODE   
//             int batchSeq = DevInStorageTool.getInstance().getUseBatchSeq(parmD.getValue("DEV_CODE",i),
//                                            StringTool.getTimestamp(depDate,"yyyy-MM-dd"),
//                                            timestamp);   
             //��Ҫ�Ķ�
             tableParm.addData("DEL_FLG","N"); 
             tableParm.addData("SELECT_FLG","Y");  
             tableParm.addData("SEQMAN_FLG",parmD.getData("SEQMAN_FLG",i));
             tableParm.addData("DEVPRO_CODE",parmD.getData("DEVPRO_CODE",i));
             tableParm.addData("DEV_CODE",parmD.getData("DEV_CODE",i));  
             //parmD.getData("BATCH_SEQ",i)
             //fux need request
//             tableParm.addData("BATCH_SEQ", batchSeq);   
             tableParm.addData("DEVSEQ_NO",parmD.getData("DEVSEQ_NO",i));
             tableParm.addData("DEV_CHN_DESC",parmD.getData("DEV_CHN_DESC",i));
             tableParm.addData("DESCRIPTION",parmD.getData("DESCRIPTION",i));
             //fux modify
             tableParm.addData("QTY","");  
             tableParm.addData("STORGE_QTY",parmD.getData("STORGE_QTY",i));
             //INWAREHOUSE_DEPT;CARE_USER 
             //������� //������Ҫ�Լ��� 
             tableParm.addData("INWAREHOUSE_DEPT",parmM.getData("TO_ORG_CODE",0));
             //������  
             tableParm.addData("CARE_USER",Operator.getName());
             //ʹ����
             tableParm.addData("USE_USER","");  
             //��ŵص�
             tableParm.addData("LOC_CODE","");
                  
             tableParm.addData("SETDEV_CODE",parmD.getData("SETDEV_CODE",i));
             tableParm.addData("UNIT_CODE",parmD.getData("UNIT_CODE",i));
             tableParm.addData("UNIT_PRICE",parmD.getData("UNIT_PRICE",i)); 
             tableParm.addData("TOT_VALUE",parmD.getData("TOT_VALUE",i)); 
             //��ֵ
             tableParm.addData("SCRAP_VALUE","");
             //������ֹ����
             tableParm.addData("GUAREP_DATE","");
             //�۾���ֹ���� 
             tableParm.addData("DEP_DATE","");
            
             tableParm.addData("REMARK1","");
             tableParm.addData("REMARK2","");
             tableParm.addData("MAN_DATE","");  
//             tableParm.addData("RFID",requsetNo);  
         //����ϸTABLE   
         ((TTable)getComponent("DEV_EXWAREHOUSED")).setParmValue(tableParm);
         //��������
         ((TTextFormat)getComponent("EXWAREHOUSE_DATE")).setEnabled(false);
         setTableLock();
          }  
         //�ӿ���
         addDRow(); 
     }   
     /**
      * ��ס��񲻿ɱ༭��λ
      */
     public void setTableLock(){    
         ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("1,2,3,4,5,6,8,"+
                                                                   "12,16,17,18,"+
                                                                   "19,20"); 
         ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("1,2,3,4,5,6,7,8,9,10,11,"+
                                                                    "12,13,14,15"); 
     } 

     
        
     /**
      * �����ܼ۸�
      * @param devBaseDataStore TDataStore
      * @return double
      */
     public double getTotAmt(TDataStore devBaseDataStore){
         int rowCount = devBaseDataStore.rowCount();
         double totAmt = 0;
         for (int i = 0; i < rowCount; i++) {
             if(!devBaseDataStore.isActive(i)&&!(Boolean)devBaseDataStore.getItemData(i,"#NEW#"))
                 continue;
             totAmt += devBaseDataStore.getItemDouble(i, "UNIT_PRICE")*devBaseDataStore.getItemDouble(i, "RECEIPT_QTY");
         }
         return totAmt;
     }
     /**
      * �õ�TTextFormat
      * @return TTextFormat 
      */
     public TTextFormat getTTextFormat(String tag){
         return (TTextFormat)this.getComponent(tag);
     }
    /**
     * �豸���ⵥ�������¼�  
     */
    public void onTableMClick(){     
        //�����
//        if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            TTable tableM = ((TTable)getComponent("EXWAREHOUSE_TABLE"));
            TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
            int row = tableM.getSelectedRow();
            TParm tableMParm = tableM.getParmValue();
            setValue("EXWAREHOUSE_NO",tableMParm.getData("EXWAREHOUSE_NO",row));
            setValue("EXWAREHOUSE_DATE",tableMParm.getData("EXWAREHOUSE_DATE",row));
            setValue("EXWAREHOUSE_DEPT",tableMParm.getData("EXWAREHOUSE_DEPT",row));
            setValue("EXWAREHOUSE_USER",tableMParm.getData("EXWAREHOUSE_USER",row));
            setValue("INWAREHOUSE_DEPT",tableMParm.getData("INWAREHOUSE_DEPT",row));
            ((TTextFormat)getComponent("EXWAREHOUSE_DATE")).setEnabled(false);
            ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false); 
            ((TTextFormat)getComponent("EXWAREHOUSE_USER")).setEnabled(false);
            ((TTextFormat)getComponent("INWAREHOUSE_DEPT")).setEnabled(false);
	        TParm result = DevOutStorageTool.getInstance().queryWCExStorgeD(tableMParm.getValue("EXWAREHOUSE_NO",row));
	        //System.out.println("result"+result);
	        if(result.getErrCode() < 0) 
	            return;   
	        tableD.setParmValue(result); 
	        onTableDClick();   
//	        setTableLock(); 
	        for(int i = 0;i<result.getCount();i++){
	           cloneTParm(result,parmD,i);
	        }
	        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
	        	this.setValue("CHECK_IN", "N");
	        	((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(true);
	        }else{
	        	this.setValue("CHECK_IN", "Y");
	        	((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(false);
	        }
//        } 
        //��;
//        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
//            TTable tableM = ((TTable)getComponent("EXWAREHOUSE_TABLE"));
//            TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
//            int row = tableM.getSelectedRow();
//            TParm tableMParm = tableM.getParmValue();
//            setValue("EXWAREHOUSE_NO",tableMParm.getData("EXWAREHOUSE_NO",row));
//            setValue("EXWAREHOUSE_DATE",tableMParm.getData("EXWAREHOUSE_DATE",row));
//            setValue("EXWAREHOUSE_DEPT",tableMParm.getData("EXWAREHOUSE_DEPT",row));
//            setValue("EXWAREHOUSE_USER",tableMParm.getData("EXWAREHOUSE_USER",row));
//            setValue("INWAREHOUSE_DEPT",tableMParm.getData("INWAREHOUSE_DEPT",row));
//            ((TTextFormat)getComponent("EXWAREHOUSE_DATE")).setEnabled(false);
//            ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false); 
//            ((TTextFormat)getComponent("EXWAREHOUSE_USER")).setEnabled(false);
//            ((TTextFormat)getComponent("INWAREHOUSE_DEPT")).setEnabled(false);
//	        TParm result = DevOutStorageTool.getInstance().queryZTExStorgeD(tableMParm.getValue("EXWAREHOUSE_NO",row));
//	        //System.out.println("result"+result);
//	        if(result.getErrCode() < 0) 
//	            return;   
//	        tableD.setParmValue(result);  
//	        onTableDClick();  
//	        setTableLock();
//	        for(int i = 0;i<result.getCount();i++)
//	           cloneTParm(result,parmD,i);
//        } 
        //δ��� 
//        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
//            TTable tableM = ((TTable)getComponent("EXWAREHOUSE_TABLE_SELECT"));
//            TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
//            TTable tableDD = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
//            int row = tableM.getSelectedRow();
//            TParm tableMParm = tableM.getParmValue();  
//            //REQUEST_NO;REQUEST_DATE;TO_ORG_CODE;APP_ORG_CODE
//            ((TTextFormat)getComponent("EXWAREHOUSE_DATE")).setEnabled(false);
//            ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false);
//            ((TTextFormat)getComponent("EXWAREHOUSE_USER")).setEnabled(false);
//            ((TTextFormat)getComponent("INWAREHOUSE_DEPT")).setEnabled(false);
//        	 String requsetNo = tableMParm.getValue("REQUEST_NO");
////           //�{��������Ϣ      
////           TParm parmM = DevOutRequestMTool.getInstance().queryRequestM(requsetNo);
//             //�{��ϵ����Ϣ       
//             TParm result = DevOutRequestDTool.getInstance().queryRequestD(tableMParm.getValue("REQUEST_NO",row));
//             //��ѯstockD����Ӧ�豸�����µ�batch_seq
//             //1�������С���ŵĹ��ã�����һ��
//             //2����������ã�˳����һ������ 
//             //��������STOCK_D����ʱ����batch_seq��ѯ�����Ŀ�棬���и���  
//             result.setData("BATCH_SEQ", "1");
//            //System.out.println("�{��ϵ����Ϣ result"+result); 
//            if(result.getErrCode() < 0) 
//                return;     
//            tableD.setParmValue(result);  
//            onTableDClick(); 
//            tableD.setLockColumns("1,2,3,4,5,6,"+ 
//                                  "7,14,15,16,"+ 
//                                  "17,18,19,20,23");  
//            for(int i = 0;i<result.getCount();i++)
//               cloneTParm(result,parmD,i);
//        }  
//        addDRow();   
    } 
    //fux modify 20130814
    /**
     * �豸���ⵥϸ�����¼�
     */
    public void onTableDClick(){ 
    	//�����
//    	if (getRadioButton("UPDATE_FLG_C").isSelected()) {
//	        TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
	        TTable tableDD = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
//	        TParm tableDParm = tableD.getParmValue();
	        TParm tableDDParm = new TParm();
	        //����d��devCode�Ϳ��Ҳ�ѯ������STOCK_DD������Ȼ��ŵ�TABLE_DD��
//	        for(int j =0;j<tableD.getRowCount();j++){
//	            if(tableDParm.getValue("SEQMAN_FLG",j).equals("N")) 
//	                      continue;      
//	           for( int i=0; i <tableD.getItemDouble(i, "QTY");i++){  
		           	//��������DD
		           	TParm result = DevOutStorageTool.getInstance().queryExStorgeDD(this.getValueString("EXWAREHOUSE_NO"));
		           	if(result.getErrCode() < 0){
		           		return;
		           	}
		           	tableDD.setParmValue(result);
//		           	for(int i=0;i<result.getCount();i++){
////		           		String devCode = tableDParm.getValue("DEV_CODE", j);
////		           		String batchSeq =  tableDParm.getValue("BATCH_SEQ", j); 
//		           		//SELECT_FLG;DEVSEQ_NO;DEV_CODE;DEV_CHN_DESC;BARCODE;SPECIFICATION;
//		           		//BATCH_SEQ;UNIT_CODE;UNIT_PRICE
//		           		tableDDParm.addData("SELECT_FLG", "N");  
//		           		tableDDParm.addData("DEVSEQ_NO", result.getData("DEVSEQ_NO",i)); 
//		           		tableDDParm.addData("DEVPRO_CODE", result.getData("DEV_CODE_DETAIL",i));
//		           		tableDDParm.addData("DEV_CODE", result.getData("DEV_CODE",i));  
//		           		tableDDParm.addData("DEV_CHN_DESC", result.getData("DEV_CHN_DESC",i)); 
//		           		tableDDParm.addData("BARCODE", result.getData("BARCODE",i)); 
//		           		tableDDParm.addData("SPECIFICATION", result.getData("SPECIFICATION",i)); 
//		           		tableDDParm.addData("BATCH_SEQ", result.getData("BATCH_SEQ",i));
//		           		tableDDParm.addData("UNIT_CODE", result.getData("UNIT_CODE",i));
//		           		tableDDParm.addData("UNIT_PRICE",  result.getData("UNIT_PRICE",i)); 
//		           	}
////	           }
////	        } 
//	        if(tableDDParm.getErrCode() < 0) 
//	            return;  
////	        System.out.println("tableDDParm>>>"+tableDDParm);
//	        tableDD.setParmValue(tableDDParm);
	
	        for(int i = 0;i<tableDDParm.getCount();i++)
	           cloneTParm(tableDDParm,parmD,i); 
//    	} 
    	//��;------------��ѯdd
//    	if (getRadioButton("UPDATE_FLG_B").isSelected()) {
//        TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
//        TTable tableDD = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
//        //����d��devCode�Ϳ��Ҳ�ѯ������STOCK_DD������Ȼ��ŵ�TABLE_DD�� 
//        	//��������DD  
//        TParm result = DevOutStorageTool.getInstance().queryExStorgeDD(this.getValueString("EXWAREHOUSE_NO"));
//        System.out.println("result"+result); 
//        if(result.getErrCode() < 0)   
//            return;    
//        tableDD.setParmValue(result);
//    	}  
    	//δ���
//    	else 
//    	if (getRadioButton("UPDATE_FLG_A").isSelected()) { 	
//            TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
//            TTable tableDD = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
//            TParm tableDParm = tableD.getParmValue();
//            TParm tableDDParm = new TParm();
//            String flg = "";
//            if(this.getValue("EXWAREHOUSE_NO").equals("")){
//            	flg = "N";
//            }else{
//            	flg = "Y";
//            }
//            //stockDD��ص�����...   
//            for( int i=0; i <tableD.getRowCount();i++){    
//                String devCode = tableDParm.getValue("DEV_CODE", i); 
//                String deptCode = this.getValueString("EXWAREHOUSE_DEPT");  
//            	TParm stockDDParm = DevOutStorageTool.getInstance().queryStockDD(devCode,deptCode);
//            	//System.out.println("stockDDParm"+stockDDParm);  
//                if(stockDDParm.getErrCode() < 0){  
//                	this.messageBox("�������Ź�������,��ȷ��");
//                    return;    
//                } 
//            	//SELECT_FLG;DEVSEQ_NO;DEV_CODE;DEV_CHN_DESC;BARCODE;SPECIFICATION;BATCH_SEQ;UNIT_CODE;UNIT_PRICE
//            	for(int j=0; j <stockDDParm.getCount();j++){
//            		tableDDParm.addData("SELECT_FLG", flg);
//	            	tableDDParm.addData("DEV_CODE_DETAIL", stockDDParm.getValue("DEV_CODE_DETAIL",j)); 
//	            	tableDDParm.addData("DEVSEQ_NO", stockDDParm.getValue("DEVSEQ_NO",j));      
//	            	tableDDParm.addData("DEV_CODE", stockDDParm.getValue("DEV_CODE",j));  
//	            	tableDDParm.addData("DEV_CHN_DESC", stockDDParm.getValue("DEV_CHN_DESC",j));  
//	            	tableDDParm.addData("BARCODE",  stockDDParm.getValue("BARCODE",j)); 
//	            	tableDDParm.addData("SPECIFICATION", stockDDParm.getValue("SPECIFICATION",j));  
//	            	tableDDParm.addData("BATCH_SEQ",  stockDDParm.getValue("BATCH_SEQ",j));
//	            	tableDDParm.addData("UNIT_CODE",  stockDDParm.getValue("STOCK_UNIT",j));  
//	            	tableDDParm.addData("UNIT_PRICE",  stockDDParm.getValue("UNIT_PRICE",j)); 
//            	}
//            }     
//            tableDD.setParmValue(tableDDParm);
//              
//
//            for(int i = 0;i<tableDDParm.getCount();i++)
//               cloneTParm(tableDDParm,parmD,i);
//        	}
    } 
 
    /**
     * �豸������ϸ���༭�¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode)obj;
        //�������༭�¼�
        if(onTableQty(node)) 
            return true;
        //�豸����༭�¼�
        if(onDevCode(node))
            return true;
        //�豸���Ա༭�¼�
        if(onDevProCode(node))
            return true;
        //���ÿ��ұ༭�¼�
        if(onInExwarehouseDept(node))
            return true;
        return false; 
   }
   /**
    * ���ÿ��ұ༭�¼�
    * @param node TTableNode
    * @return boolean 
    */
   public boolean onInExwarehouseDept(TTableNode node){
       if(node.getColumn() != 7)
           return false; 
       if(node.getValue().equals(getValue("EXWAREHOUSE_DEPT")))
           return true;
       return false;
   }
   /**
    * �豸����༭�¼�
    * @param node TTableNode
    * @return boolean
    */
   public boolean onDevCode(TTableNode node){
       if(node.getColumn() != 3)
            return false;
        TTable table = (TTable)getComponent("DEV_EXWAREHOUSED");
        if(("" + table.getValueAt(node.getRow(),3)).length() != 0)
            return true; 
        return false;
   }
   /**
    * �豸���Ա༭�¼�
    * @param node TTableNode
    * @return boolean
    */
   public boolean onDevProCode(TTableNode node){
       if(node.getColumn() != 2) 
            return false;
        TTable table = (TTable)getComponent("DEV_EXWAREHOUSED");
        if(("" + table.getValueAt(node.getRow(),3)).length() != 0)
            return true; 
        return false;
   }
   //fux need modify 
   //��ѯ������¼������е�DD��ֵ
   /**
    * �豸�������༭�¼�  
    * @param node TTableNode
    * @return boolean
    */
   public boolean onTableQty(TTableNode node){
	   //fux need modify 
       if(node.getColumn() != 5)
            return false;   
       TParm parm = ((TTable)getComponent("DEV_EXWAREHOUSED")).getParmValue();
       //System.out.println("д��DD��parm"+parm);
       TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
       TTable tableDD = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
       //���һ��Ϊ�հ���,����ȥ�� 
//       for(int i = 0; i< parm.getCount("DEVPRO_CODE")-1;i++){ 
//           if("N".equals(parm.getData("SEQMAN_FLG",i)))  
//               continue; 
//           int rowCount = 0;
//           //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;BATCH_SEQ;DEVSEQ_NO;DEV_CHN_DESC;DESCRIPTION;QTY;
////           rowCount = parm.getInt("QTY",i); 
////           rowCount = parm.getInt("STORGE_QTY",i); 
//           String value = ("" + node.getValue()).length() == 0?"0":("" + node.getValue());
//           TParm tableDParm = tableD.getParmValue();
//           TParm tableDDParm = new TParm();
//           //stockDD��ص�����...    
//          
//               String devCode = tableDParm.getValue("DEV_CODE", i); 
//               String deptCode = this.getValueString("EXWAREHOUSE_DEPT");  
//           	TParm stockDDParm = DevOutStorageTool.getInstance().queryStockDD(devCode,deptCode);
//           //	System.out.println("stockDDParm"+stockDDParm);  
//               if(stockDDParm.getErrCode() < 0){  
//               	this.messageBox("�������Ź�������,��ȷ��");
//                   return true;    
//               }  
//           	//SELECT_FLG;DEVSEQ_NO;DEV_CODE;DEV_CHN_DESC;BARCODE;SPECIFICATION;BATCH_SEQ;UNIT_CODE;UNIT_PRICE
//           	for(int j=0; j <stockDDParm.getCount();j++){  
//           	tableDDParm.addData("SELECT_FLG", "N");   
//           	tableDDParm.addData("DEVSEQ_NO", stockDDParm.getValue("DEVSEQ_NO",j));      
//           	tableDDParm.addData("DEV_CODE", stockDDParm.getValue("DEV_CODE",j));  
//           	tableDDParm.addData("DEV_CHN_DESC", stockDDParm.getValue("DEV_CHN_DESC",j));  
//           	tableDDParm.addData("BARCODE",  stockDDParm.getValue("BARCODE",j)); 
//           	tableDDParm.addData("SPECIFICATION", stockDDParm.getValue("SPECIFICATION",j));  
//           	tableDDParm.addData("BATCH_SEQ",  stockDDParm.getValue("BATCH_SEQ",j));
//           	tableDDParm.addData("UNIT_CODE",  stockDDParm.getValue("STOCK_UNIT",j));  
//           	tableDDParm.addData("UNIT_PRICE",  stockDDParm.getValue("UNIT_PRICE",j)); 
//           	} 
//           	tableDD.setParmValue(tableDDParm);
//       }             
        if(Integer.parseInt(node.getValue() + "") == 0){
            messageBox("������������");
            return true;
        }
        if(Integer.parseInt(node.getValue() + "") > Integer.parseInt("" +tableD.getValueAt(node.getRow(),6))){
            messageBox("���������ɴ��ڿ����");  
            return true;  
        }           
        //ԭ������ֵ,��������
//        updateTableData("DEV_EXWAREHOUSED",node.getRow(),16,
//                        Integer.parseInt(node.getValue() + "") *
//                        Double.parseDouble("" +tableD.getValueAt(node.getRow(),15)));
        return false;
   }

   /**
    * ���±������
    * @param tableTag String
    * @param row int
    * @param column int
    * @param obj Object
    */
   public void updateTableData(String tableTag,int row,int column,Object obj){
       ((TTable)getComponent(tableTag)).setValueAt(obj,row,column);
       ((TTable)getComponent(tableTag)).getParmValue().setData(getFactColumnName(tableTag,column),row,obj);
   }
   /**
    * �����豸������ϸ������
    */ 
   public void addDRow(){
	   //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;
	   //DEV_CHN_DESC;QTY;STORGE_QTY;INWAREHOUSE_DEPT;CARE_USER;SETDEV_CODE;UNIT_CODE;REMARK1;REMARK2
       String column = "DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;"+
                       "DEV_CHN_DESC;QTY;STORGE_QTY;"+
                       "INWAREHOUSE_DEPT;CARE_USER;"+
                       "SETDEV_CODE;UNIT_CODE;"+
                       "REMARK1;REMARK2";  
       String stringMap[] = StringTool.parseLine(column,";"); 
       TParm tableDParm = new TParm();
       for(int i = 0;i<stringMap.length;i++){
           if("DEVPRO_CODE".equals(stringMap[i])) 
               tableDParm.setData(stringMap[i],"A"); 
           else
               tableDParm.setData(stringMap[i],"");
        }
       ((TTable)getComponent("DEV_EXWAREHOUSED")).addRow(tableDParm);
   }
   /**
    * �豸���ⵥ��ӡ 
    */ 
   public void onPrint(){
       TTable table = ((TTable)getComponent("EXWAREHOUSE_TABLE"));
       if(table.getSelectedRow() < 0){
    	   this.messageBox("�޴�ӡ����");
           return;    
       }
       onPrintAction("" + table.getValueAt(table.getSelectedRow(),0));
   }
   /**
    * ��ӡ���� (����,��δ�ӡ�е�����,�����) 
    * @param exWarehoseNo String  
    */
   public void onPrintAction(String exWarehoseNo){   
       TParm printdata = new TParm();    
       TParm parm = DevOutStorageTool.getInstance().queryExReceiptData(exWarehoseNo);
       if(exWarehoseNo == null ||
    	          exWarehoseNo.length() == 0)  
    	           return;   
       if(parm.getErrCode() < 0)     
           return;  
       //messageBox("COUNT:::"+parm.getCount("EXWAREHOUSE_NO"));
       if(parm.getCount("EXWAREHOUSE_NO") <= 0){
           messageBox("�޴�ӡ����"); 
           return; 
       }     
       for(int i=0;i<parm.getCount("EXWAREHOUSE_NO");i++){
    	   printdata.setData("EXWAREHOUSE_NO",i,parm.getValue("EXWAREHOUSE_NO",i));
    	   printdata.setData("EXWAREHOUSE_DATE",i,parm.getValue("EXWAREHOUSE_DATE",i).substring(0, 10));
    	   printdata.setData("DEV_CHN_DESC",i,parm.getValue("DEV_CHN_DESC",i));  
    	   printdata.setData("SPECIFICATION",i,parm.getValue("SPECIFICATION",i)); 
    	   printdata.setData("UNIT_PRICE",i,parm.getValue("UNIT_PRICE",i));
    	   printdata.setData("QTY",i,parm.getValue("QTY",i));  
    	   printdata.setData("TOT_VALUE",i,parm.getValue("TOT_VALUE",i));  
//    	   printdata.addData("DEVPRO_CODE",parm.getValue("DEVPRO_CODE",i));  
//    	   printdata.addData("BATCH_SEQ",parm.getValue("BATCH_SEQ",i));  
//    	   printdata.addData("SEQ_NO",parm.getValue("SEQ_NO",i));
//    	   printdata.addData("SETDEV_CODE",parm.getValue("SETDEV_CODE",i)); 
//    	   printdata.addData("CARE_USER",parm.getValue("CARE_USER",i)); 
//    	   printdata.addData("USE_USER",parm.getValue("USE_USER",i));
//    	   printdata.addData("LOC_CODE",parm.getValue("LOC_CODE",i));
//    	   printdata.addData("REMARK1",parm.getValue("REMARK1",i));
//    	   printdata.addData("REMARK2",parm.getValue("REMARK2",i)); 
       }   
       //ҽʦǩ��
       //�����ֵ
       //clearNullAndCode(parm);    
       //���ñ����Ϣ      
       TParm tableParm = ((TTable)getComponent("DEV_EXWAREHOUSED")).getParmValue();
       printdata.setCount(tableParm.getCount("DEV_CODE")); 
       printdata.addData("SYSTEM", "COLUMNS", "EXWAREHOUSE_NO"); 
       printdata.addData("SYSTEM", "COLUMNS", "EXWAREHOUSE_DATE");
       printdata.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
       printdata.addData("SYSTEM", "COLUMNS", "SPECIFICATION"); 
       printdata.addData("SYSTEM", "COLUMNS", "QTY"); 
       printdata.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");  
       printdata.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
       
//       printdata.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
//       printdata.addData("SYSTEM", "COLUMNS", "CARE_USER");
//       printdata.addData("SYSTEM", "COLUMNS", "USE_USER");
//       printdata.addData("SYSTEM", "COLUMNS", "LOC_CODE");  
//       printdata.addData("SYSTEM", "COLUMNS", "REMARK1");
//       printdata.addData("SYSTEM", "COLUMNS", "REMARK2"); 
//       printdata.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");  
       TParm printParm = new TParm();         
       
       printParm.setData("TABLE",printdata.getData());   
       printParm.setData("TITLE", "TEXT", Manager.getOrganization(). 
				getHospitalCHNFullName(Operator.getRegion()) +
				"���ⵥ");           
       openPrintWindow("%ROOT%\\config\\prt\\dev\\DevExStorageReceipt.jhw",printParm);
       //openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",printParm);
       //���ñ�ͷ         
//       printParm.setData("TITLE","TEXT","�豸���ⵥ") ;  
//       printParm.setData("EXWAREHOUSE_DEPT","TEXT",getDeptDesc(parm.getValue("EXWAREHOUSE_DEPT",0)));
//       printParm.setData("HOSP_NAME",Operator.getHospitalCHNShortName());
//       printParm.setData("EXWAREHOUSE_NO",parm.getValue("EXWAREHOUSE_NO",0));
//       printParm.setData("EXWAREHOUSE_DATE",parm.getValue("EXWAREHOUSE_DATE",0).substring(0,10).replace('-','/'));
//       printParm.setData("EXWAREHOUSE_DEPT",getDeptDesc(parm.getValue("EXWAREHOUSE_DEPT",0)));
//       printParm.setData("EXWAREHOUSE_USER",getOperatorName(parm.getValue("EXWAREHOUSE_USER",0)));
//       printParm.setData("INWAREHOUSE_DEPT",getDeptDesc(parm.getValue("INWAREHOUSE_DEPT",0)));

  }       
   
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
     * ȡ�����ݿ������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }
    /**
     * �õ�������Ա����
     * @param userID String
     * @return String
     */
    public String getOperatorName(String userID){
       TParm parm = new TParm(getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+userID+"'"));
       if(parm.getCount() <= 0)
           return "";
       return parm.getValue("USER_NAME",0);
    }
    /**
     * �õ��豸��������
     * @param devCode String
     * @return String
     */
    public String getDevDesc(String devCode){
        String SQL="SELECT DEV_CHN_DESC FROM DEV_BASE WHERE DEV_CODE = '"+devCode+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getCount() <= 0)
            return "";
        return parm.getValue("DEV_CHN_DESC",0);
    }
    /**
     * �õ�������������
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }
    /**
     * �����ֵ
     * @param parm TParm
     */
    private void clearNullAndCode(TParm parm){
      String names[] = parm.getNames();
      for(int i = 0;i < names.length;i++){
          for(int j = 0 ; j < parm.getCount(names[i]) ; j++){
              if(parm.getData(names[i],j) == null)
                  parm.setData(names[i],j,"");
              if("DEVPRO_CODE".equals(names[i]))
                  parm.setData(names[i],j,getDevProDesc(parm.getValue(names[i],j)));
              if("SETDEV_CODE".equals(names[i]))
                  parm.setData(names[i],j,getDevDesc(parm.getValue(names[i],j)));
              if("CARE_USER".equals(names[i]))
                  parm.setData(names[i],j,getOperatorName(parm.getValue(names[i],j)));
              if("USE_USER".equals(names[i]))
                  parm.setData(names[i],j,getOperatorName(parm.getValue(names[i],j)));
          }
      }
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
     * �����豸¼���¼�
     * @param com Component
     * @param row int   
     * @param column int   
     */
    public void onCreateEditComoponent(Component com,int row,int column){
        //״̬����ʾ 
        callFunction("UI|setSysStatus",""); 
        //�õ�����
        String columnName = getFactColumnName("DEV_EXWAREHOUSED",column);
        if(!"DEV_CODE".equals(columnName))
            return;
        if(!(com instanceof TTextField))
            return; 
        TTextField textFilter = (TTextField)com;
        textFilter.onInit();
        if(("" +getTTable("DEV_EXWAREHOUSED").getValueAt(row,column)).length() != 0)
            return; 
        TParm parm = new TParm();
        parm.setData("TABLEDATA",getTTable("DEV_EXWAREHOUSED").getParmValue());
        parm.setData("DEPT_CODE",getValue("EXWAREHOUSE_DEPT"));//DEVPRO_CODE
        int selRow = getTTable("DEV_EXWAREHOUSED").getSelectedRow();
        TParm rowParm = getTTable("DEV_EXWAREHOUSED").getParmValue().getRow(selRow);
        parm.setData("DEVPRO_CODE",rowParm.getValue("DEVPRO_CODE"));
//        System.out.println("!!!!!!!!!!parm"+parm); 
        //���õ����˵�   
        textFilter.setPopupMenuParameter("DEVBASE",getConfigParm().newConfig("%ROOT%\\config\\sys\\DevStockPopupUI.x"),parm);
        //������ܷ���ֵ����  
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE,this,"popReturn");
    }  
    /** 
     * �����豸¼�뷵�ز���
     * @param tag String
     * @param obj Object   
     */
    public void popReturn(String tag,Object obj){
        // fux need request  ������Ϣ������
        //�ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
        if (obj == null && !(obj instanceof TParm)) {
            return;
        }
        //����ת����TParm
        TParm parm = (TParm) obj;
        if(getValueString("EXWAREHOUSE_DEPT").length() ==0){
            messageBox("��¼��������");
            return; 
        }  
//        System.out.println("parm>>>>"+parm);
        String devCode = parm.getValue("DEV_CODE"); 
        TParm devBase = onDevBase(devCode); 
        if(devBase.getCount()<=0){ 
           //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
           //QTY;STORGE_QTY;INWAREHOUSE_DEPT;CARE_USER; 
           //SETDEV_CODE;UNIT_CODE;REMARK1;REMARK2
        TParm tableParm = new TParm();  
        tableParm.setData("DEL_FLG","N");
        tableParm.setData("SELECT_FLG","Y"); 
        tableParm.setData("SEQMAN_FLG",parm.getData("SEQMAN_FLG"));
        tableParm.setData("DEVPRO_CODE",parm.getData("DEVPRO_CODE"));
        tableParm.setData("DEV_CODE",parm.getData("DEV_CODE")); 
        tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
        //tableParm.setData("DESCRIPTION",parm.getData("SPECIFICATION"));
        tableParm.setData("QTY",parm.getData("QTY"));            
        tableParm.setData("STORGE_QTY",parm.getData("QTY"));
        tableParm.setData("INWAREHOUSE_DEPT",getValueString("INWAREHOUSE_DEPT"));
        tableParm.setData("CARE_USER",parm.getData("CARE_USER"));  
        //tableParm.setData("USE_USER",parm.getData("USE_USER"));   
        //tableParm.setData("LOC_CODE",parm.getData("LOC_CODE"));
        tableParm.setData("SETDEV_CODE",parm.getData("SETDEV_CODE"));
        tableParm.setData("UNIT_CODE",parm.getData("UNIT_CODE"));
           
        //�ܶ�                
        //��SQL��ʹ���������ӣ�����(+)������λ�õ���һ��Ϊ���ӵķ���������˵���Ⱥ��Ҳ�����м�¼���ᱻ��ʾ��������������Ƿ�õ�ƥ��  
        //tableParm.setData("TOT_VALUE",Integer.parseInt ("" + parm.getData("QTY")) *
        //                              Double.parseDouble("" + parm.getData("UNIT_PRICE")));
        //UNIT_PRICE,MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE,BRAND,SPECIFICATION,MODEL
        tableParm.setData("UNIT_PRICE",parm.getData("UNIT_PRICE"));
        tableParm.setData("MAN_DATE",parm.getData("MAN_DATE"));
        tableParm.setData("SCRAP_VALUE",parm.getData("SCRAP_VALUE"));
        tableParm.setData("GUAREP_DATE",parm.getData("GUAREP_DATE"));
        tableParm.setData("DEP_DATE",parm.getData("DEP_DATE"));
        tableParm.setData("REMARK1","");         
        tableParm.setData("REMARK2","");
        tableParm.setData("BRAND",parm.getData("BRAND"));     
        tableParm.setData("SPECIFICATION",parm.getData("SPECIFICATION"));   
        tableParm.setData("MODEL",parm.getData("MODEL"));  
//        tableParm.setData("RFID",parm.getData("RFID"));
        getTTable("DEV_EXWAREHOUSED").removeRow(getTTable("DEV_EXWAREHOUSED").getSelectedRow());
        getTTable("DEV_EXWAREHOUSED").addRow(tableParm);  
//        ((TTable)getComponent("DEV_EXWAREHOUSED")).setLockColumns("1,4,5,6,"+
//                                                                  "14,15,16,"+
//                                                                  "17,18,19,20,23");
        addDRow();
        ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false);
        }
        else {  
            //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
            //QTY;STORGE_QTY;INWAREHOUSE_DEPT;CARE_USER; 
            //SETDEV_CODE;UNIT_CODE;REMARK1;REMARK2
            TParm tableParm = new TParm();  
            tableParm.setData("DEL_FLG","N");
            tableParm.setData("SELECT_FLG","Y"); 
            tableParm.setData("SEQMAN_FLG",parm.getData("SEQMAN_FLG"));
            tableParm.setData("DEVPRO_CODE",parm.getData("DEVPRO_CODE"));
            tableParm.setData("DEV_CODE",parm.getData("DEV_CODE"));
            tableParm.setData("DEV_CHN_DESC",parm.getData("DEV_CHN_DESC"));
            //tableParm.setData("DESCRIPTION",parm.getData("SPECIFICATION"));
            tableParm.setData("QTY",parm.getData("QTY"));            
            tableParm.setData("STORGE_QTY",parm.getData("QTY"));
            tableParm.setData("INWAREHOUSE_DEPT",getValueString("INWAREHOUSE_DEPT"));
            tableParm.setData("CARE_USER",parm.getData("CARE_USER"));
            //tableParm.setData("USE_USER",parm.getData("USE_USER")); 
            //tableParm.setData("LOC_CODE",parm.getData("LOC_CODE"));
            tableParm.setData("SETDEV_CODE",parm.getData("SETDEV_CODE"));
            tableParm.setData("UNIT_CODE",parm.getData("UNIT_CODE"));
            tableParm.setData("UNIT_PRICE",parm.getData("UNIT_PRICE"));  
            //�ܶ�              
            //tableParm.setData("TOT_VALUE",Integer.parseInt ("" + parm.getData("QTY")) *
            //                              Double.parseDouble("" + parm.getData("UNIT_PRICE")));
            //tableParm.setData("SCRAP_VALUE",parm.getData("SCRAP_VALUE"));
            //tableParm.setData("GUAREP_DATE",parm.getData("GUAREP_DATE"));
            //tableParm.setData("DEP_DATE",parm.getData("DEP_DATE"));
            tableParm.setData("REMARK1","");
            tableParm.setData("REMARK2",""); 
            //tableParm.setData("MAN_DATE",parm.getData("MAN_DATE")); 
//            tableParm.setData("RFID",parm.getData("RFID"));
            getTTable("DEV_EXWAREHOUSED").removeRow(getTTable("DEV_EXWAREHOUSED").getSelectedRow());
            getTTable("DEV_EXWAREHOUSED").addRow(tableParm);  
//            ((TTable)getComponent("DEV_EXWAREHOUSED")).setLockColumns("1,4,5,6,"+
//                                                                      "14,15,16,"+
//                                                                       "17,18,19,20,23");
            //������������parm  
   	     TParm tableParmSet = new TParm();  
            for(int i=0;i<devBase.getCount();i++){
           	 //addData������  setData���ַ���
                //DEL_FLG;SEQMAN_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;
                //QTY;STORGE_QTY;INWAREHOUSE_DEPT;CARE_USER; 
                //SETDEV_CODE;UNIT_CODE;REMARK1;REMARK2
            	tableParmSet.setData("DEL_FLG","N");
            	tableParmSet.setData("SELECT_FLG","Y"); 
            	tableParmSet.setData("SEQMAN_FLG",devBase.getData("SEQMAN_FLG",i)); 
            	tableParmSet.setData("DEVPRO_CODE",devBase.getData("DEVPRO_CODE",i));
            	tableParmSet.setData("DEV_CODE",devBase.getData("DEV_CODE",i));
            	tableParmSet.setData("DEV_CHN_DESC",devBase.getData("DEV_CHN_DESC",i));
            	//tableParmSet.setData("DESCRIPTION",devBase.getData("SPECIFICATION",i));
            	tableParmSet.setData("QTY",devBase.getData("QTY",i));            
            	tableParmSet.setData("STORGE_QTY",devBase.getData("QTY",i));
            	tableParmSet.setData("INWAREHOUSE_DEPT",getValueString("INWAREHOUSE_DEPT"));
            	tableParmSet.setData("CARE_USER",devBase.getData("CARE_USER",i));
            	//tableParmSet.setData("USE_USER",devBase.getData("USE_USER",i)); 
            	//tableParmSet.setData("LOC_CODE",devBase.getData("LOC_CODE",i));
            	tableParmSet.setData("SETDEV_CODE",devBase.getData("SETDEV_CODE",i));
            	tableParmSet.setData("UNIT_CODE",devBase.getData("UNIT_CODE",i));
            	//tableParmSet.setData("UNIT_PRICE",devBase.getData("UNIT_PRICE",i)); 
                //�ܶ�                 
            	//tableParmSet.setData("TOT_VALUE",Integer.parseInt ("" + devBase.getData("QTY",i)) *
                //                                Double.parseDouble("" + devBase.getData("UNIT_PRICE",i)));
            	//tableParmSet.setData("SCRAP_VALUE",devBase.getData("SCRAP_VALUE",i));
            	//tableParmSet.setData("GUAREP_DATE",devBase.getData("GUAREP_DATE",i));
            	//tableParmSet.setData("DEP_DATE",devBase.getData("DEP_DATE",i));
            	tableParmSet.setData("REMARK1","");  
            	tableParmSet.setData("REMARK2","");  
            	//tableParmSet.setData("MAN_DATE",devBase.getData("MAN_DATE",i));     
          }        
            getTTable("DEV_EXWAREHOUSED").addRow(tableParmSet);    
            addDRow();  
            ((TTextFormat)getComponent("EXWAREHOUSE_DEPT")).setEnabled(false);
        }
        //����dd����ʾ����stockDD���ݷ���
        onTableDDCreat();      
   } 
    
    /**           
     * ��ѯ��������DEV_BASE         
     * @return boolean
     */ 
    public TParm onDevBase(String devCode){
       String deptCode = this.getValueString("EXWAREHOUSE_DEPT");
 	   String sql = " SELECT A.DEV_CODE," +
       " A.STOCK_FLG,A.QTY,B.ACTIVE_FLG," +
       " B.DEVKIND_CODE,B.DEVTYPE_CODE,B.DEVPRO_CODE,B.DEV_CHN_DESC,B.DESCRIPTION," +
       " B.SPECIFICATION,B.UNIT_CODE,B.SEQMAN_FLG,B.DEV_CLASS,B.BUYWAY_CODE" +
       " FROM DEV_STOCKM A ,DEV_BASE B" +
       " WHERE A.DEV_CODE = B.DEV_CODE " +
       " AND B.SETDEV_CODE = '"+devCode+"'" +  
       " AND B.ACTIVE_FLG = 'Y'" +
//       " AND A.DEPT_CODE = '"+deptCode+"'" +
       " AND B.DEVPRO_CODE = 'B'";   
// 	   System.out.println("sql"+sql); 
 	   TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
 	   return parm; 
    } 
   /**
    * ����tabled�����ݺ�stockdd�����п����ϸ
    * @param tableDD ----stockDD
    * @return void 
    */  
    public void onTableDDCreat(){  
        TParm parmD = ((TTable)getComponent("DEV_EXWAREHOUSED")).getParmValue();
        //System.out.println("д��DD��parm"+parm);
        TTable tableD = ((TTable)getComponent("DEV_EXWAREHOUSED"));
        TTable tableDD = ((TTable)getComponent("DEV_EXWAREHOUSEDD"));
        //���һ��Ϊ�հ���,����ȥ�� 
        for(int i = 0; i< parmD.getCount("DEVPRO_CODE")-1;i++){ 
            if("N".equals(parmD.getData("SEQMAN_FLG",i)))   
                continue; 
            TParm tableDParm = tableD.getParmValue();
            TParm tableDDParm = new TParm();  
            //stockDD��ص�����...       
                String devCode = tableDParm.getValue("DEV_CODE", i); 
                String deptCode = this.getValueString("EXWAREHOUSE_DEPT");  
            	TParm stockDDParm = DevOutStorageTool.getInstance().queryStockDD(devCode,deptCode);
            //	System.out.println("stockDDParm"+stockDDParm);  
                if(stockDDParm.getErrCode() < 0){   
                	this.messageBox("�������Ź�������,��ȷ��");
                }   
                //SELECT_FLG;DEVSEQ_NO;DEV_CODE_DETAIL;DEV_CODE;DEV_CHN_DESC;
                //BARCODE;MODEL;SPECIFICATION;MAN_CODE;BRAND;LOC_CODE;BATCH_SEQ;UNIT_CODE;UNIT_PRICE
                //System.out.println("stockDDParm::::"+stockDDParm);   
            	for(int j=0; j <stockDDParm.getCount();j++){    
            	tableDDParm.addData("SELECT_FLG", "N");     
            	tableDDParm.addData("DEVSEQ_NO", stockDDParm.getValue("DEVSEQ_NO",j));    
            	tableDDParm.addData("DEV_CODE_DETAIL", stockDDParm.getValue("DEV_CODE_DETAIL",j));
            	tableDDParm.addData("DEV_CODE", stockDDParm.getValue("DEV_CODE",j));  
            	tableDDParm.addData("DEV_CHN_DESC", stockDDParm.getValue("DEV_CHN_DESC",j)); 
            	tableDDParm.addData("BARCODE", stockDDParm.getValue("BARCODE",j));
            	tableDDParm.addData("MODEL",  stockDDParm.getValue("MODEL",j)); 
            	tableDDParm.addData("SPECIFICATION", stockDDParm.getValue("SPECIFICATION",j));  
            	tableDDParm.addData("MAN_CODE",  stockDDParm.getValue("MAN_CODE",j)); 
            	tableDDParm.addData("BRAND",  stockDDParm.getValue("BRAND",j));  
            	tableDDParm.addData("LOC_CODE",  stockDDParm.getValue("LOC_CODE",j));  	
//            	tableDDParm.addData("BATCH_SEQ",  stockDDParm.getValue("BATCH_SEQ",j));
            	tableDDParm.addData("UNIT_CODE",  stockDDParm.getValue("UNIT_CODE",j));  
            	tableDDParm.addData("UNIT_PRICE",  stockDDParm.getValue("UNIT_PRICE",j)); 
            	} 
            	tableDD.setParmValue(tableDDParm);
        }   
    }
   /**
    * �õ������λ��
    * @param tableTag String
    * @param column int
    * @return String
    */
   public String getFactColumnName(String tableTag,int column){
        int col = getThisColumnIndex(column);
        return getTTable(tableTag).getDataStoreColumnName(col);
    }
    /**
     * �õ������λ����
     * @param column int
     * @return int
     */
    public int getThisColumnIndex(int column){
        return getTTable("DEV_EXWAREHOUSED").getColumnModel().getColumnIndex(column);
    }

    /**
     * ���ÿ����¼�
     */
    public void onInwarehouseDept(){
        TTable tabled = ((TTable)getComponent("DEV_EXWAREHOUSED"));
        if(getValueString("INWAREHOUSE_DEPT").equals(getValueString("EXWAREHOUSE_DEPT")))
            setValue("INWAREHOUSE_DEPT",""); 
        TParm dParm = tabled.getParmValue();
        for(int i=0;i<tabled.getRowCount();i++){
        	dParm.addData("INWAREHOUSE_DEPT", getValueString("INWAREHOUSE_DEPT"));
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
