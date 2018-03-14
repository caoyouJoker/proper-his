package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;


/**
 * <p>Title: ����л���ҽ�Ʊ��տ���Ԥ����֧����</p>
 *
 * <p>Description:����л���ҽ�Ʊ��տ���Ԥ����֧����</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yufh
 */
public class INSCheckReservedFundControl extends TControl {

	//ҽ��ҽԺ����
    private String nhi_hosp_code;
    //ҽ��ҽԺ����
    private String nhi_hosp_desc;
    /**
     * ��ʼ��
     */
    public void onInit() {
    	 TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                 getRegion());
         this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
         this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
    	onClear();
    }

    /**
     * ����
     */
    public void onDownload() {   
    	//���ݼ��
    	if(checkdata())
		    return;
    	TParm parm = new TParm();
    	parm.setData("PIPELINE", "DataDown_zjkd");
    	parm.setData("PLOT_TYPE", "U");
    	parm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//ҽԺ����
    	String year = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "YEAR")), "yyyy");
    	parm.addData("YEAR", year);//Э�����
    	parm.addData("PARM_COUNT", 2);//�������
    	TParm result = InsManager.getInstance().safe(parm, "");
//    	System.out.println("result" + result);
//      	System.out.println("result2======" + result.getCount("PAY_RAMAINDER_AMT"));
    	 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return;
         }
    	onPrint(result); 
    }
    /**
    *
    * ��ӡ
    * @return
    */ 
    public void onPrint(TParm result) {  
    	 DecimalFormat df = new DecimalFormat("##########0.00");
         TParm Data = new TParm();
         TParm Datacx = new TParm();
         String Date = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyyMMdd");//��ǰʱ��        
         double totalAmt = 0.00;
         double shouldPayAmt = 0.00;
         double payRatio = 0.00;
         double realPayAmt = 0.00;
         double payRamainderAmt = 0.00;              
         if(result.getValue("INS_TYPE",0).equals("310")){
        	 Data.setData("TITLE", "TEXT","����л���ҽ�Ʊ��տ���Ԥ����֧����"); //��ͷ
             Data.setData("DQSJ", "TEXT",Date.substring(0,4)+"��"+Date.substring(4,6)+"��");
             String year = StringTool.getString(TCM_Transform.getTimestamp(getValue(
    	     "YEAR")), "yyyy"); 
             Data.setData("YEAR", "TEXT",year);//Э�����
             Data.setData("YLJGBM", "TEXT", "0124765-6"); //ҽ�ƻ�������   	 
             Data.setData("YLJGMCH", "TEXT", this.nhi_hosp_desc); //ҽ�ƻ�������        
             Data.setData("BH", "TEXT", "���籣ҽ֧��320��");//���
             Data.setData("DW", "TEXT", "Ԫ"); //��λ
             Data.setData("INS_TYPE", "TEXT", "��ְ"); //����
             Data.setData("BATCH_NO", "TEXT", result.getValue("BATCH_NO",0));//��������
             totalAmt= result.getDouble("TOTAL_AMT",0);
        	 shouldPayAmt= result.getDouble("SHOULD_PAY_AMT",0);
        	 payRatio= result.getDouble("PAY_RATIO",0);
        	 realPayAmt= result.getDouble("REAL_PAY_AMT",0);
        	 payRamainderAmt= result.getDouble("PAY_RAMAINDER_AMT",0); 
        	 Data.setData("TOTAL_AMT", "TEXT", df.format(totalAmt)); //�ܿ���Ԥ����
             Data.setData("SHOULD_PAY_AMT", "TEXT", df.format(shouldPayAmt)); //����Ӧ֧����Ԥ����
             Data.setData("PAY_RATIO", "TEXT", payRatio); //���ο���Ԥ����֧������
             Data.setData("REAL_PAY_AMT", "TEXT", df.format(realPayAmt)); //���ο���Ԥ����ʵ��֧�����
             Data.setData("PAY_RAMAINDER_AMT", "TEXT",df.format(payRamainderAmt)); //����Ԥ����ʣ����
             Data.setData("REAL_PAYMENT_AMT", "TEXT", df.format(realPayAmt)); //ʵ��֧�����(Сд)
             Data.setData("REAL_PAYMENT_AMT_CAPITAL", "TEXT",
            		 StringUtil.getInstance().numberToWord(realPayAmt)); //ʵ��֧������д��
               this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_CHECK_RESERVE.jhw",
                     Data);  
         }
          if(result.getValue("INS_TYPE",1).equals("390")){
        	  Datacx.setData("TITLE", "TEXT","����л���ҽ�Ʊ��տ���Ԥ����֧����"); //��ͷ
        	  Datacx.setData("DQSJ", "TEXT",Date.substring(0,4)+"��"+Date.substring(4,6)+"��");
              String year = StringTool.getString(TCM_Transform.getTimestamp(getValue(
     	     "YEAR")), "yyyy"); 
              Datacx.setData("YEAR", "TEXT",year);//Э�����
              Datacx.setData("YLJGBM", "TEXT", "0124765-6"); //ҽ�ƻ�������   	 
              Datacx.setData("YLJGMCH", "TEXT", this.nhi_hosp_desc); //ҽ�ƻ�������        
              Datacx.setData("BH", "TEXT", "���籣ҽ֧��320��");//���
              Datacx.setData("DW", "TEXT", "Ԫ"); //��λ 
              Datacx.setData("INS_TYPE", "TEXT", "����"); //����
              Datacx.setData("BATCH_NO", "TEXT", result.getValue("BATCH_NO",1));//��������
             totalAmt= result.getDouble("TOTAL_AMT",1);
    	     shouldPayAmt= result.getDouble("SHOULD_PAY_AMT",1);
    	     payRatio= result.getDouble("PAY_RATIO",1);
    	     realPayAmt= result.getDouble("REAL_PAY_AMT",1);
    	     payRamainderAmt= result.getDouble("PAY_RAMAINDER_AMT",1);
    	     Datacx.setData("TOTAL_AMT", "TEXT", df.format(totalAmt)); //�ܿ���Ԥ����
    	     Datacx.setData("SHOULD_PAY_AMT", "TEXT", df.format(shouldPayAmt)); //����Ӧ֧����Ԥ����
    	     Datacx.setData("PAY_RATIO", "TEXT", payRatio); //���ο���Ԥ����֧������
    	     Datacx.setData("REAL_PAY_AMT", "TEXT", df.format(realPayAmt)); //���ο���Ԥ����ʵ��֧�����
    	     Datacx.setData("PAY_RAMAINDER_AMT", "TEXT",df.format(payRamainderAmt)); //����Ԥ����ʣ����
    	     Datacx.setData("REAL_PAYMENT_AMT", "TEXT", df.format(realPayAmt)); //ʵ��֧�����(Сд)
    	     Datacx.setData("REAL_PAYMENT_AMT_CAPITAL", "TEXT",
            		 StringUtil.getInstance().numberToWord(realPayAmt)); //ʵ��֧������д��
               this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_CHECK_RESERVE.jhw",
            		   Datacx);  
         }
                     
       
    }
    /**
    *
    * �˲��ѯ����
    * @return
    */
   private boolean checkdata() {
       String year = this.getValueString("YEAR"); //Э�����
       if ("".equals(year)) {
           this.messageBox("Э����Ȳ���Ϊ��");   
           return true;
       }       
       return false;
   }


    /**
     * ���
     */
    public void onClear() {      
        this.setValue("YEAR",SystemTool.getInstance().getDate());  
    }
		
	
}
