package jdo.bil;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
*
* <p>Title: 合同单位预交金工具类</p>
*
* <p>Description: 合同单位预交金工具</p>
*
* <p>Copyright: Copyright (c) ProperSoft</p>
*
* <p>Company: ProperSoft</p>
*
* @author caowl
* @version 1.0
*/
public class BILContractTool extends TJDOTool{
	
	 /**
     * 实例
     */
    public static BILContractTool instanceObject;
    /**
     * 得到实例
     * @return BILTool
     */
    public static BILContractTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILContractTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILContractTool() {
        onInit();
    }
    /**
     * 合同单位冲销预交金
     * */
    public TParm onReturnContractPay(TParm parm, TConnection connection){
    	 TParm result = new TParm();
    	 /**
    	  *更新OFF_RECP_NO 
    	  * */
    	 TParm updParm = new TParm();
    	 updParm.setData("RECEIPT_NO",parm.getData("RECEIPT_NO"));
    	 updParm.setData("OFF_RECP_NO",parm.getData("RECEIPT_NO"));//??????应该是账单里的票据号，判定冲销的是哪张账单
    	 updParm.setData("OPT_DATE",parm.getData("OPT_DATE")==null? "" : parm.getData("OPT_DATE"));//12
    	 updParm.setData("OPT_USER",parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));//13
    	 updParm.setData("OPT_TERM",parm.getData("OPT_TERM")==null ? "" : parm.getData("OPT_TERM"));//14
    	 result = BILContractPayTool.getInstance().updOffRecpNo(updParm, connection);
    	  if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
    	  /**
          * 插入一笔负值
          */
         TParm inParm = new TParm();
         //System.out.println("parm:"+parm);
         // inParm = parm;
         String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                 "RECEIPT_NO", "RECEIPT_NO");
         inParm.setData("RECEIPT_NO", receiptNo);//1
         inParm.setData("CONTRACT_CODE",parm.getData("CONTRACT_CODE")== null ? new TNull(String.class):parm.getData("CONTRACT_CODE"));//2
         inParm.setData("BUSINESS_TYPE","3");//3
         inParm.setData("CASHIER_CODE",parm.getData("CASHIER_CODE")==null ? new TNull(String.class): parm.getData("CASHIER_CODE"));//4
         inParm.setData("CHARGE_DATE",parm.getData("CHARGE_DATE")==null ? new TNull(String.class) :parm.getData("CHARGE_DATE"));//5
         inParm.setData("REFUND_FLG","N");//7       
         inParm.setData("REFUND_CODE","");//8
         inParm.setData("REFUND_DATE","");//9
         inParm.setData("OFF_RECP_NO","");//10       
         inParm.setData("OPT_DATE",parm.getData("OPT_DATE")==null? "" : parm.getData("OPT_DATE"));//12
         inParm.setData("OPT_USER",parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));//13
         inParm.setData("OPT_TERM",parm.getData("OPT_TERM")==null ? "" : parm.getData("OPT_TERM"));//14
         inParm.setData("PAY_TYPE",parm.getData("PAY_TYPE")==null ? "" :parm.getData("PAY_TYPE"));//15    
         inParm.setData("REMARK",parm.getData("REMARK")==null ? "" : parm.getData("REMARK"));//11
         inParm.setData("TRANSACT_TYPE", "02");
         inParm.setData("PRINT_NO",parm.getData("PRINT_NO")==null ? "" : parm.getData("PRINT_NO"));
         if (inParm.getData("CHECK_NO") == null)
             inParm.setData("CHECK_NO", new TNull(String.class));
         inParm.setData("PRE_PAY", -TypeTool.getDouble(parm.getData("PRE_AMT")));//6      
         result = BILContractPayTool.getInstance().insertData(inParm, connection);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         /**
          * 更新bil_contractm表中的预交金余额值
          * */
         TParm contractParm = new TParm();
         contractParm.setData("CONTRACT_CODE", parm.getValue("CONTRACT_CODE"));
         TParm totBilPay = BILContractMTool.getInstance().selectData("queryByContractCode", parm);
         contractParm.setData("PREPAY_AMT",
                         totBilPay.getDouble("PREPAY_AMT", 0) -
                         parm.getDouble("PRE_AMT"));
         contractParm.setData("OPT_USER", parm.getData("OPT_USER"));
         contractParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
         contractParm.setData("OPT_DATE",parm.getData("OPT_DATE"));
         //更新bil_contractm表的预交金余额        
         result = BILContractMTool.getInstance().updPrePay(contractParm,connection);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }

         /**
          * 更新票据状态
          * */
         TParm invRcpParm = new TParm();
         invRcpParm.setData("CANCEL_FLG", "1");
         invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));
         invRcpParm.setData("OPT_USER", parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));
         invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM")==null ? "" : parm.getData("OPT_TERM"));
         invRcpParm.setData("RECP_TYPE", "HPAY");
         invRcpParm.setData("INV_NO", parm.getData("PRINT_NO")==null ? "" : parm.getData("PRINT_NO"));
         //退预交金更新票据明细档票据状态
         result = BILInvrcptTool.getInstance().updataData(invRcpParm, connection);
         if (result.getErrCode() < 0) {
             err(result.getErrName() + " " + result.getErrText());
             return result;
         }

         return result;
    }
  
   /**
    * 合同单位退预交金
    * ??????????????
    * */ 
    public TParm onReturnBillContractPay(TParm parm, TConnection connection){
    	  TParm result = new TParm();
          /**
           * 置REFOUND_FLG 为'Y' 表示退费
           */
          result = BILContractPayTool.getInstance().updataData(parm, connection);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          /**
           * 插入一笔负值
           */
          TParm inParm = new TParm();
          //System.out.println("parm:"+parm);
          // inParm = parm;
          String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                  "RECEIPT_NO", "RECEIPT_NO");
          inParm.setData("RECEIPT_NO", receiptNo);//1
          inParm.setData("CONTRACT_CODE",parm.getData("CONTRACT_CODE")== null ? new TNull(String.class):parm.getData("CONTRACT_CODE"));//2
          inParm.setData("BUSINESS_TYPE","2");//3
          inParm.setData("CASHIER_CODE",parm.getData("CASHIER_CODE")==null ? new TNull(String.class): parm.getData("CASHIER_CODE"));//4
          inParm.setData("CHARGE_DATE",parm.getData("CHARGE_DATE")==null ? new TNull(String.class) :parm.getData("CHARGE_DATE"));//5
          inParm.setData("REFUND_FLG","N");//7       
          inParm.setData("REFUND_CODE","");//8
          inParm.setData("REFUND_DATE","");//9
          inParm.setData("OFF_RECP_NO","");//10       
          inParm.setData("OPT_DATE",parm.getData("OPT_DATE")==null? "" : parm.getData("OPT_DATE"));//12
          inParm.setData("OPT_USER",parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));//13
          inParm.setData("OPT_TERM",parm.getData("OPT_TERM")==null ? "" : parm.getData("OPT_TERM"));//14
          inParm.setData("PAY_TYPE",parm.getData("PAY_TYPE")==null ? "" :parm.getData("PAY_TYPE"));//15    
          inParm.setData("REMARK",parm.getData("REMARK")==null ? "" : parm.getData("REMARK"));//11
          inParm.setData("TRANSACT_TYPE", "02");
          inParm.setData("PRINT_NO",parm.getData("PRINT_NO")==null ? "" : parm.getData("PRINT_NO"));
          if (inParm.getData("CHECK_NO") == null)
              inParm.setData("CHECK_NO", new TNull(String.class));
          inParm.setData("PRE_PAY", -TypeTool.getDouble(parm.getData("PRE_AMT")));//6      
          result = BILContractPayTool.getInstance().insertData(inParm, connection);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }
          /**
           * 更新bil_contractm表中的预交金余额值
           * */
          TParm contractParm = new TParm();
          contractParm.setData("CONTRACT_CODE", parm.getValue("CONTRACT_CODE"));
          TParm totBilPay = BILContractMTool.getInstance().selectData("queryByContractCode", parm);
          contractParm.setData("PREPAY_AMT",
                          totBilPay.getDouble("PREPAY_AMT", 0) -
                          parm.getDouble("PRE_AMT"));
          contractParm.setData("OPT_USER", parm.getData("OPT_USER"));
          contractParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
          contractParm.setData("OPT_DATE",parm.getData("OPT_DATE"));
          //更新bil_contractm表的预交金余额        
          result = BILContractMTool.getInstance().updPrePay(contractParm,connection);
          if (result.getErrCode() < 0) {
              err("ERR:" + result.getErrCode() + result.getErrText() +
                  result.getErrName());
              return result;
          }

          /**
           * 更新票据状态
           * */
          TParm invRcpParm = new TParm();
          invRcpParm.setData("CANCEL_FLG", "1");
          invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));
          invRcpParm.setData("OPT_USER", parm.getData("OPT_USER")==null ? "" : parm.getData("OPT_USER"));
          invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM")==null ? "" : parm.getData("OPT_TERM"));
          invRcpParm.setData("RECP_TYPE", "HPAY");
          invRcpParm.setData("INV_NO", parm.getData("PRINT_NO")==null ? "" : parm.getData("PRINT_NO"));
          //退预交金更新票据明细档票据状态
          result = BILInvrcptTool.getInstance().updataData(invRcpParm, connection);
          if (result.getErrCode() < 0) {
              err(result.getErrName() + " " + result.getErrText());
              return result;
          }

          return result;
    }

    /**
     * 交合同单位预交金
     * @param parm TParm  有问题？？？？？？？？？？
     * @param connection TConnection
     * @return TParm
     */
    public TParm onContractBillPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //1.更新bil_contractm表的预交金余额参数
        TParm contractmParm = new TParm();
        contractmParm.setData("CONTRACT_CODE", parm.getValue("CONTRACT_CODE"));
        //查询预交金余额
        TParm totBilPay = BILContractMTool.getInstance().selectData("queryByContractCode", parm);
        contractmParm.setData("PREPAY_AMT",
                        totBilPay.getDouble("PREPAY_AMT", 0) +
                        parm.getDouble("PRE_PAY"));
        contractmParm.setData("OPT_USER", parm.getData("OPT_USER"));
        contractmParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        contractmParm.setData("OPT_DATE",parm.getData("OPT_DATE"));
        //2.写入bil_Invricpt表
        TParm bilInvricpt = new TParm();
        bilInvricpt.setData("RECP_TYPE", "HPAY");
        bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
        bilInvricpt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvricpt.setData("OPT_USER", parm.getData("OPT_USER"));
        bilInvricpt.setData("INV_NO", parm.getData("INV_NO"));
        bilInvricpt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
        bilInvricpt.setData("TOT_AMT", parm.getData("PRE_PAY"));
        bilInvricpt.setData("AR_AMT", parm.getData("PRE_PAY"));

        //3.写入bilInvoice表
        TParm bilInvoice = new TParm();
        bilInvoice.setData("RECP_TYPE", "HPAY");
        bilInvoice.setData("STATUS", "0");
        bilInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
        bilInvoice.setData("UPDATE_NO",
                           StringTool.addString(parm.getData("INV_NO").
                                                toString()));
        TParm actionParm = new TParm();
        actionParm = parm;
        actionParm.setData("BILCONTRACTM",contractmParm.getData());
        actionParm.setData("BILINVRICPT", bilInvricpt.getData());
        actionParm.setData("BILINVOICE", bilInvoice.getData());
        //存储票据档
        result = BILContractPayTool.getInstance().saveBilPay(actionParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

}
