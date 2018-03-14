package com.javahis.ui.dev;

import jdo.dev.DevGetNoTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
/** 
 * <p>Title: �ʲ���Ƭά��</p> 
 *  
 * <p>Description:�ʲ���Ƭά��)</p>
 *   
 * <p>Copyright: Copyright (c) 20131028</p>
 * 
 * <p>Company: BLUECORE </p>  
 *      
 * @author  fux  
 *  
 * @version 4.0     
 */
public class DEVPropertyCardsControl extends TControl{
    int row = 0; 
    //�����洫���ʲ���Ƭ�����ݼ�
    TParm parmDD;
   /**
	* ��ʼ�� 
	*/
   public void onInit(){ 
	  //����������TABLEDD�ϴ��ز���  
      super.onInit(); 
	  //�õ�ǰ̨���������ݲ���ʾ�ڽ����� 
	  parmDD = (TParm)this.getParameter();   
	  row = parmDD.getInt("ROW");             
	  //�����ʲ�����   
	  this.setValue("DEV_DETAIL_CODE", parmDD.getValue("DEV_CODE_DETAIL") );     
      this.setValue("DEV_CODE", parmDD.getValue("DEV_CODE")); 
      this.setValue("DEV_CHN_DESC", parmDD.getValue("DEV_CHN_DESC"));
      this.setValue("BRAND", parmDD.getValue("BRAND"));
      this.setValue("SPECIFICATION", parmDD.getValue("SPECIFICATION"));
      this.setValue("MODEL", parmDD.getValue("MODEL"));
      this.setValue("MAN_CODE", parmDD.getValue("MAN_CODE")); 
      this.setValue("CURR_PRICE", parmDD.getValue("TOT_VALUE")); 
      //�������Ĭ��Ϊ1  
      this.setValue("QTY",1);  
	}     
   /**
    * ����
    */
   public void onSave(){ 
	  //��������¹��ܲ��� 
	  //�豸�������
	  String devCode = this.getValueString("DEV_CODE"); 
	  //�豸��������
	  String devDesc = this.getValueString("DEV_CHN_DESC"); 
	  //Ʒ��
	  String Brand = this.getValueString("BRAND"); 
	  //���
	  String Specification = this.getValueString("SPECIFICATION");
	  //�ͺ�
	  String model = this.getValueString("MODEL");
	  //���к� 
	  String serialNum = this.getValueString("SERIAL_NUM");
	  //������λ
	  String UnitCode = this.getValueString("UNIT_CODE"); 
	  //��������
	  String ManCode = this.getValueString("MAN_CODE");
	  //������
	  String ManNation = this.getValueString("MAN_NATION");
	  //�������  
	  int qty = this.getValueInt("QTY");
	  //�豸����
	  String unitPrice = this.getValueString("UNIT_PRICE");
	  //�������
	  String InwarehouseDate = this.getValueString("INWAREHOUSE_DATE");
	  //IP
	  String Ip = this.getValueString("IP");
	  //������� 
	  String term = this.getValueString("TERM");
	  //��ŵص� 
	  String locCode  = this.getValueString("LOC_CODE");
	  //��ŵص�  
	  String useUser  = this.getValueString("USE_USER");  
	  //��һ���ʲ�����  
	  String detailCodeold  = parmDD.getValue("DEV_CODE_DETAIL_OLD").replace('[', ' ').replace(']',' ').trim();
	  //�����д�ʲ���Ƭ�������    
	  //int tot =  parmDD.getInt("TOT"); 
	  TParm parmIo = new TParm();   
	  if(qty == 0){ 
		 this.messageBox("����д���������");   
		 return; 
	  }     
//	  if(qty > tot){  
//			 this.messageBox("�����豸¼��������������������������"); 
//			 return;  
//		  } 
	  TParm parmReturn = new TParm();
	    for(int i = 0;i<qty;i++){      
	  	  //û�о��ʲ�����(ÿһ���½�һ��devCode���ʲ���Ƭ)  
	  	  if(detailCodeold == null||"".equals(detailCodeold)){ 
	  		  parmReturn = DevGetNoTool.getInstance().finshNumber(devCode,qty) ;   
	  	  }
	  	  //(����ͬ����devCode���ʲ���Ƭ,���ڱ��ε���ʲ���Ƭ����һ�е��ʲ�������ˮ��+1)
	  	  else{    
	  		  parmReturn = DevGetNoTool.getInstance().finshNumberSecord(devCode,qty,detailCodeold) ;
	  	  }	    	
		  String detailCode  = parmReturn.getValue("DEV_CODE_DETAIL",i).toString() ; 
		  parmIo.addData("DEV_CODE_DETAIL",detailCode);  
		  parmIo.addData("DEV_CODE",devCode); 
		  parmIo.addData("DEV_CHN_DESC",devDesc);  
		  parmIo.addData("BRAND",Brand); 
		  parmIo.addData("SPECIFICATION",Specification);
		  parmIo.addData("MODEL",model); 
		  parmIo.addData("SERIAL_NUM",serialNum);  
		  parmIo.addData("UNIT_CODE",UnitCode);
		  parmIo.addData("MAN_CODE",ManCode);        
		  parmIo.addData("MAN_NATION",ManNation==null?86:ManNation);
		  parmIo.addData("QTY",qty);  
		  parmIo.addData("UNIT_PRICE",unitPrice);
		  parmIo.addData("INWAREHOUSE_DATE",InwarehouseDate);
		  parmIo.addData("IP",Ip);
		  parmIo.addData("TERM",term);
		  parmIo.addData("LOC_CODE",locCode); 
		  parmIo.addData("USE_USER",useUser); 
		  parmIo.addData("ROW",qty + row);   
	  } 
	      if(qty == 0){ 
			 this.messageBox("����д���������"); 
			 return; 
		  } 
	   //����parm  
       if (parmIo.getErrCode()<0) {   
           this.messageBox("E0005!");  
           return; 
       }   
       else    
       {         	      
          this.messageBox("P0005");
		  this.setReturnValue(parmIo);  
		  this.closeWindow(); 
       }
  
   }       
   /**
    * ���
    */
     public void onClear(){ 
        //��ս���  DEV_CODE   DEV_DESC    UNIT_CODE  UNIT_PRICE
		clearValue("DDSEQ_NO;BRAND;SPECIFICATION; "
				+  "MODEL;SERIAL_NUM;MAN_CODE;MAN_NATION; "
				+  "QTY;INWAREHOUSE_DATE;IP;TERM;" +
				   "LOC_CODE;USE_USER");     
   }
}
