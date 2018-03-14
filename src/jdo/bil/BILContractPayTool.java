package jdo.bil;

import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * 合同单位预交金
 * */
public class BILContractPayTool extends TJDOTool{
	 /**
     * 实例
     */
    public static BILContractPayTool instanceObject;
    /**
     * 得到实例
     * @return BILPrintTool
     */
    public static BILContractPayTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILContractPayTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILContractPayTool() {
    	  setModuleName("bil\\BILContractPayModule.x");
	      onInit();
    }
    /**
     * 显示数据
     * @param parm TParm
     */
    public TParm selectData(String sqlName,TParm parm){
        TParm result = query(sqlName, parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 冲销预交金
     * */
    public TParm updOffRecpNo(TParm parm, TConnection connection){
    	 TParm result = this.update("updOffRecpNo", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
     * 合同单位预交金保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveBilPay(TParm parm, TConnection connection) {
        //得到一个票据
        TParm result = null;
        //后台取号原则取出的此票号
        String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                "RECEIPT_NO", "RECEIPT_NO");
        parm.setData("RECEIPT_NO", receiptNo);
      
        parm.setData("PRINT_NO", parm.getValue("INV_NO"));
        parm.setData("RESET_RECP_NO", new TNull(String.class));
        //交预交金  bil_contract_pay
        result = this.insertData(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        //更新当前票号BIL_Invoice
        TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
        //调用写票
        result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //插入打印的票据BIL_Invrcpt
        TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
        //收据流水号
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "O");//
        //调用写票据明细
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //更新bil_contractm表的预交金余额
        TParm contractmParm = (TParm)parm.getParm("BILCONTRACTM");
        result = BILContractMTool.getInstance().updPrePay(contractmParm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //存储过程得到的票据号返回
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }
    
    /**
     * 插入合同单位预交金
     * @param parm TParm
    * @param connection 
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = this.update("insertData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 更新合同单位预交金
     * 作废
     * */
    public TParm updataData(TParm parm, TConnection connection){
    	 TParm result = this.update("refundData", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
}
