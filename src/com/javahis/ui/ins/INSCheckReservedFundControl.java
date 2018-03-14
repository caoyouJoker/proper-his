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
 * <p>Title: 天津市基本医疗保险考核预留金支付表</p>
 *
 * <p>Description:天津市基本医疗保险考核预留金支付表</p>
 *
 * <p>Company: javahis</p>
 *
 * @author yufh
 */
public class INSCheckReservedFundControl extends TControl {

	//医保医院代码
    private String nhi_hosp_code;
    //医保医院名称
    private String nhi_hosp_desc;
    /**
     * 初始化
     */
    public void onInit() {
    	 TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                 getRegion());
         this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
         this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
    	onClear();
    }

    /**
     * 下载
     */
    public void onDownload() {   
    	//数据检核
    	if(checkdata())
		    return;
    	TParm parm = new TParm();
    	parm.setData("PIPELINE", "DataDown_zjkd");
    	parm.setData("PLOT_TYPE", "U");
    	parm.addData("NHI_HOSP_NO", this.nhi_hosp_code);//医院编码
    	String year = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "YEAR")), "yyyy");
    	parm.addData("YEAR", year);//协议年度
    	parm.addData("PARM_COUNT", 2);//入参数量
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
    * 打印
    * @return
    */ 
    public void onPrint(TParm result) {  
    	 DecimalFormat df = new DecimalFormat("##########0.00");
         TParm Data = new TParm();
         TParm Datacx = new TParm();
         String Date = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyyMMdd");//当前时间        
         double totalAmt = 0.00;
         double shouldPayAmt = 0.00;
         double payRatio = 0.00;
         double realPayAmt = 0.00;
         double payRamainderAmt = 0.00;              
         if(result.getValue("INS_TYPE",0).equals("310")){
        	 Data.setData("TITLE", "TEXT","天津市基本医疗保险考核预留金支付表"); //表头
             Data.setData("DQSJ", "TEXT",Date.substring(0,4)+"年"+Date.substring(4,6)+"月");
             String year = StringTool.getString(TCM_Transform.getTimestamp(getValue(
    	     "YEAR")), "yyyy"); 
             Data.setData("YEAR", "TEXT",year);//协议年度
             Data.setData("YLJGBM", "TEXT", "0124765-6"); //医疗机构编码   	 
             Data.setData("YLJGMCH", "TEXT", this.nhi_hosp_desc); //医疗机构名称        
             Data.setData("BH", "TEXT", "津社保医支字320号");//表号
             Data.setData("DW", "TEXT", "元"); //单位
             Data.setData("INS_TYPE", "TEXT", "城职"); //险种
             Data.setData("BATCH_NO", "TEXT", result.getValue("BATCH_NO",0));//汇总批号
             totalAmt= result.getDouble("TOTAL_AMT",0);
        	 shouldPayAmt= result.getDouble("SHOULD_PAY_AMT",0);
        	 payRatio= result.getDouble("PAY_RATIO",0);
        	 realPayAmt= result.getDouble("REAL_PAY_AMT",0);
        	 payRamainderAmt= result.getDouble("PAY_RAMAINDER_AMT",0); 
        	 Data.setData("TOTAL_AMT", "TEXT", df.format(totalAmt)); //总考核预留金
             Data.setData("SHOULD_PAY_AMT", "TEXT", df.format(shouldPayAmt)); //本次应支考核预留金
             Data.setData("PAY_RATIO", "TEXT", payRatio); //本次考核预留金支付比例
             Data.setData("REAL_PAY_AMT", "TEXT", df.format(realPayAmt)); //本次考核预留金实际支付金额
             Data.setData("PAY_RAMAINDER_AMT", "TEXT",df.format(payRamainderAmt)); //考核预留金剩余金额
             Data.setData("REAL_PAYMENT_AMT", "TEXT", df.format(realPayAmt)); //实际支付金额(小写)
             Data.setData("REAL_PAYMENT_AMT_CAPITAL", "TEXT",
            		 StringUtil.getInstance().numberToWord(realPayAmt)); //实际支付金额（大写）
               this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_CHECK_RESERVE.jhw",
                     Data);  
         }
          if(result.getValue("INS_TYPE",1).equals("390")){
        	  Datacx.setData("TITLE", "TEXT","天津市基本医疗保险考核预留金支付表"); //表头
        	  Datacx.setData("DQSJ", "TEXT",Date.substring(0,4)+"年"+Date.substring(4,6)+"月");
              String year = StringTool.getString(TCM_Transform.getTimestamp(getValue(
     	     "YEAR")), "yyyy"); 
              Datacx.setData("YEAR", "TEXT",year);//协议年度
              Datacx.setData("YLJGBM", "TEXT", "0124765-6"); //医疗机构编码   	 
              Datacx.setData("YLJGMCH", "TEXT", this.nhi_hosp_desc); //医疗机构名称        
              Datacx.setData("BH", "TEXT", "津社保医支字320号");//表号
              Datacx.setData("DW", "TEXT", "元"); //单位 
              Datacx.setData("INS_TYPE", "TEXT", "城乡"); //险种
              Datacx.setData("BATCH_NO", "TEXT", result.getValue("BATCH_NO",1));//汇总批号
             totalAmt= result.getDouble("TOTAL_AMT",1);
    	     shouldPayAmt= result.getDouble("SHOULD_PAY_AMT",1);
    	     payRatio= result.getDouble("PAY_RATIO",1);
    	     realPayAmt= result.getDouble("REAL_PAY_AMT",1);
    	     payRamainderAmt= result.getDouble("PAY_RAMAINDER_AMT",1);
    	     Datacx.setData("TOTAL_AMT", "TEXT", df.format(totalAmt)); //总考核预留金
    	     Datacx.setData("SHOULD_PAY_AMT", "TEXT", df.format(shouldPayAmt)); //本次应支考核预留金
    	     Datacx.setData("PAY_RATIO", "TEXT", payRatio); //本次考核预留金支付比例
    	     Datacx.setData("REAL_PAY_AMT", "TEXT", df.format(realPayAmt)); //本次考核预留金实际支付金额
    	     Datacx.setData("PAY_RAMAINDER_AMT", "TEXT",df.format(payRamainderAmt)); //考核预留金剩余金额
    	     Datacx.setData("REAL_PAYMENT_AMT", "TEXT", df.format(realPayAmt)); //实际支付金额(小写)
    	     Datacx.setData("REAL_PAYMENT_AMT_CAPITAL", "TEXT",
            		 StringUtil.getInstance().numberToWord(realPayAmt)); //实际支付金额（大写）
               this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INS_CHECK_RESERVE.jhw",
            		   Datacx);  
         }
                     
       
    }
    /**
    *
    * 核查查询条件
    * @return
    */
   private boolean checkdata() {
       String year = this.getValueString("YEAR"); //协议年度
       if ("".equals(year)) {
           this.messageBox("协议年度不能为空");   
           return true;
       }       
       return false;
   }


    /**
     * 清空
     */
    public void onClear() {      
        this.setValue("YEAR",SystemTool.getInstance().getDate());  
    }
		
	
}
