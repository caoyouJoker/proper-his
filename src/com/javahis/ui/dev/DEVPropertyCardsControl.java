package com.javahis.ui.dev;

import jdo.dev.DevGetNoTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
/** 
 * <p>Title: 资产卡片维护</p> 
 *  
 * <p>Description:资产卡片维护)</p>
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
    //入库界面传入资产卡片的数据集
    TParm parmDD;
   /**
	* 初始化 
	*/
   public void onInit(){ 
	  //接受入库界面TABLEDD上传回参数  
      super.onInit(); 
	  //得到前台传来的数据并显示在界面上 
	  parmDD = (TParm)this.getParameter();   
	  row = parmDD.getInt("ROW");             
	  //加入资产编码   
	  this.setValue("DEV_DETAIL_CODE", parmDD.getValue("DEV_CODE_DETAIL") );     
      this.setValue("DEV_CODE", parmDD.getValue("DEV_CODE")); 
      this.setValue("DEV_CHN_DESC", parmDD.getValue("DEV_CHN_DESC"));
      this.setValue("BRAND", parmDD.getValue("BRAND"));
      this.setValue("SPECIFICATION", parmDD.getValue("SPECIFICATION"));
      this.setValue("MODEL", parmDD.getValue("MODEL"));
      this.setValue("MAN_CODE", parmDD.getValue("MAN_CODE")); 
      this.setValue("CURR_PRICE", parmDD.getValue("TOT_VALUE")); 
      //入库数量默认为1  
      this.setValue("QTY",1);  
	}     
   /**
    * 保存
    */
   public void onSave(){ 
	  //保存与更新功能并存 
	  //设备分类编码
	  String devCode = this.getValueString("DEV_CODE"); 
	  //设备分类名称
	  String devDesc = this.getValueString("DEV_CHN_DESC"); 
	  //品牌
	  String Brand = this.getValueString("BRAND"); 
	  //规格
	  String Specification = this.getValueString("SPECIFICATION");
	  //型号
	  String model = this.getValueString("MODEL");
	  //序列号 
	  String serialNum = this.getValueString("SERIAL_NUM");
	  //计量单位
	  String UnitCode = this.getValueString("UNIT_CODE"); 
	  //生产厂商
	  String ManCode = this.getValueString("MAN_CODE");
	  //生产国
	  String ManNation = this.getValueString("MAN_NATION");
	  //入库数量  
	  int qty = this.getValueInt("QTY");
	  //设备单价
	  String unitPrice = this.getValueString("UNIT_PRICE");
	  //入库日期
	  String InwarehouseDate = this.getValueString("INWAREHOUSE_DATE");
	  //IP
	  String Ip = this.getValueString("IP");
	  //入库日期 
	  String term = this.getValueString("TERM");
	  //存放地点 
	  String locCode  = this.getValueString("LOC_CODE");
	  //存放地点  
	  String useUser  = this.getValueString("USE_USER");  
	  //上一个资产编码  
	  String detailCodeold  = parmDD.getValue("DEV_CODE_DETAIL_OLD").replace('[', ' ').replace(']',' ').trim();
	  //最大填写资产卡片入库数量    
	  //int tot =  parmDD.getInt("TOT"); 
	  TParm parmIo = new TParm();   
	  if(qty == 0){ 
		 this.messageBox("请填写入库数量！");   
		 return; 
	  }     
//	  if(qty > tot){  
//			 this.messageBox("本类设备录入数量超过本次最大入库数量！"); 
//			 return;  
//		  } 
	  TParm parmReturn = new TParm();
	    for(int i = 0;i<qty;i++){      
	  	  //没有旧资产编码(每一次新建一种devCode的资产卡片)  
	  	  if(detailCodeold == null||"".equals(detailCodeold)){ 
	  		  parmReturn = DevGetNoTool.getInstance().finshNumber(devCode,qty) ;   
	  	  }
	  	  //(已有同种类devCode的资产卡片,则在本次点击资产卡片行上一行的资产编码流水号+1)
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
			 this.messageBox("请填写入库数量！"); 
			 return; 
		  } 
	   //传回parm  
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
    * 清空
    */
     public void onClear(){ 
        //清空界面  DEV_CODE   DEV_DESC    UNIT_CODE  UNIT_PRICE
		clearValue("DDSEQ_NO;BRAND;SPECIFICATION; "
				+  "MODEL;SERIAL_NUM;MAN_CODE;MAN_NATION; "
				+  "QTY;INWAREHOUSE_DATE;IP;TERM;" +
				   "LOC_CODE;USE_USER");     
   }
}
