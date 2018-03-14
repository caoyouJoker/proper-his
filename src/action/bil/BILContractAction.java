package action.bil;


import jdo.bil.BILContractDTool;
import jdo.bil.BILContractMTool;
import jdo.bil.BILContractRecordTool;
import jdo.bil.BILContractTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BILTool;
import jdo.opb.OPBReceiptTool;
import jdo.opd.OrderTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;

/**
 * <p>Title:合同单位action </p>
 *
 * <p>Description:合同单位action </p>
 *
 * <p>Copyright: Copyright (c) bluecore</p>
 *
 * <p>Company:bluecore </p>
 *
 * @author caowl 20130114
 * @version 1.0
 */
public class BILContractAction extends TAction {
	/**
     * 合同单位信息保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveAcctionCompany(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILContractMTool.getInstance().insertData(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 更新合同单位信息
     * */
    public TParm onUpdAcctionCompany(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILContractMTool.getInstance().updateData(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 删除合同单位信息
     * */
    public TParm onDelAcctionCompany(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILContractMTool.getInstance().deleteData(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * 病患信息保存
     * */
    public TParm onSaveAcctionPat(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILContractDTool.getInstance().insertDataPat(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
        	connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 病患信息更新
     * */
    public TParm onUpdAcctionPat(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILContractDTool.getInstance().updateDataPat(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 病患信息删除
     * */
    public TParm onDelAcctionPat(TParm parm) {
        TParm result=new TParm();
        if(parm==null)
            return result.newErrParm(-1,"参数为空");
        TConnection connection = getConnection();
        result=BILContractDTool.getInstance().deleteDataPat(parm,connection);
        if (result==null||result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
             return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
   
    /**
     * 合同单位交预交金
     * @param parm TParm
     * @return TParm
     */
    public TParm onContractBillPay(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILContractTool.getInstance().onContractBillPay(parm, connection);
        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 合同单位退预交金
     * */
    public TParm onReturnBillContractPay(TParm parm){
    	  TConnection connection = getConnection();
          TParm result = new TParm();
          result = BILContractTool.getInstance().onReturnBillContractPay(parm, connection);
          if (result.getErrCode() < 0) {
          	connection.rollback();
              connection.close();
              return result;
          }
          connection.commit();
          connection.close();
          return result;
    }
    
    /**
     * 合同单位冲销预交金
     * */
    public TParm onReturnContractPay(TParm parm,TConnection connection ){
          TParm result = new TParm();
          result = BILContractTool.getInstance().onReturnContractPay(parm, connection);
          if (result.getErrCode() < 0) {
              return result;
          }
          return result;
    }
    /**
     * 合同单位结账操作
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveContract(TParm parm) {
    	TConnection connection = getConnection();
        TParm result = null;
        TParm recodeParm=parm.getParm("recodeParm");
        TParm payParm = parm.getParm("payParm");
        TParm regParm = new TParm();
        //System.out.println("recodeParm:::"+recodeParm);
        //1.获得REG的票号
        //获得当前票据号
        TParm parms = new TParm();
        parms.setData("RECP_TYPE", "REG");
        parms.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parms.setData("STATUS", "0");
        parms.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parms);
               
        //2.获得OPB的票号
        TParm parmsS = new TParm();
        parmsS.setData("RECP_TYPE", "OPB");
        parmsS.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parmsS.setData("STATUS", "0");
        parmsS.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParmS = BILInvoiceTool.getInstance().selectNowReceipt(parmsS);      
        boolean flg = false;
        boolean opbFlg = false;
        for (int i = 0; i < recodeParm.getCount(); i++) {
            // 勾选的执行操作
            if (recodeParm.getBoolean("FLG", i)) {
                TParm recode=recodeParm.getRow(i);                
                recode.setData("OPT_USER",parm.getValue("OPT_USER"));
                recode.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                //挂号操作
                if ("REG".equals(recode.getValue("RECEIPT_TYPE"))) {
                	flg = true;
                	 //修改挂号收据表添加票号、票号时间
                    TParm newDataRecpParm = new TParm();
                    newDataRecpParm.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
                    newDataRecpParm.setData("PRINT_NO", noParm.getValue("UPDATE_NO",0));
                    newDataRecpParm.setData("CASE_NO", parm.getValue("CASE_NO"));
                    newDataRecpParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                    newDataRecpParm.setData("OPT_USER", parm.getValue("OPT_USER"));
                    result = BILREGRecpTool.getInstance().upRecpForRePrint(
                            newDataRecpParm, connection);
                    if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText() +
                            result.getErrName());
                        connection.close();
                        return result;
                    }
                	//regParm.addData("recode", recode.getData());
                    //result = regSave(recode, connection);
                } else {
                	opbFlg = true;
                	 //OPD_ORDER 表修改
                    TParm p = new TParm();
                    p.setData("CASE_NO", parm.getValue("CASE_NO"));
                    p.setData("PRINT_FLG", "Y");
                    p.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
                    result = OrderTool.getInstance().updateForRecode(p, connection);
                    if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText() +
                            result.getErrName());
                        connection.close();
                        return result;
                    }
                    //BIL_OPB_RECP 表
                    //修改门诊收据表添加票号、票号时间
                    TParm opbReceipt = new TParm();
                    opbReceipt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
                    opbReceipt.setData("PRINT_NO", noParmS.getValue("UPDATE_NO",0));
                    opbReceipt.setData("CASE_NO", parm.getValue("CASE_NO"));
                    opbReceipt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                    opbReceipt.setData("OPT_USER", parm.getValue("OPT_USER"));
                    result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
                            connection);
                    //计费操作
                    //result = opbSave(recode, connection);
                }
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    connection.close();
                    return result;
                }

                //修改完成状态
                TParm parmOne=recodeParm.getRow(i);
                parmOne.setData("BIL_STATUS","2");
                parmOne.setData("RECEIPT_FLG","1");
                parmOne.setData("OPT_USER",parm.getValue("OPT_USER"));
                parmOne.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                result = BILContractRecordTool.getInstance().updateRecode(
                        parmOne, connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    connection.close();
                    return result;
                }

            }
           
        }
       
        if(flg){
        	//修改票号主档表
            TParm printReceipt = noParm.getRow(0);
            printReceipt.setData("UPDATE_NO",
                                 StringTool.addString(noParm.getValue("UPDATE_NO",0)));
            //System.out.println("22222222222222");
            //调用写票
            result = BILInvoiceTool.getInstance().updateDatePrint(
                    printReceipt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
            //插入打印的票据BIL_Invrcpt
            TParm bilInvrcpt = new TParm();
            bilInvrcpt.setData("RECP_TYPE", "REG");
            bilInvrcpt.setData("INV_NO", noParm.getValue("UPDATE_NO",0));
            bilInvrcpt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
            bilInvrcpt.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
            bilInvrcpt.setData("AR_AMT", parm.getValue("AR_AMT"));
            bilInvrcpt.setData("OPT_USER", parm.getValue("OPT_USER"));
            bilInvrcpt.setData("CANCEL_FLG", "0");
            bilInvrcpt.setData("STATUS", "0");
            bilInvrcpt.setData("ADM_TYPE", "O");
            bilInvrcpt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            //调用写票据明细
            result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
        }else if(opbFlg){
        	//修改票号主档表
            TParm printReceipt = noParmS.getRow(0);
            printReceipt.setData("UPDATE_NO",
                                 StringTool.addString(noParmS.getValue("UPDATE_NO",0)));
            //System.out.println("22222222222222");
            //调用写票
            result = BILInvoiceTool.getInstance().updateDatePrint(
                    printReceipt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
            //插入打印的票据BIL_Invrcpt
            TParm bilInvrcpt = new TParm();
            bilInvrcpt.setData("RECP_TYPE", "REG");
            bilInvrcpt.setData("INV_NO", noParmS.getValue("UPDATE_NO",0));
            bilInvrcpt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
            bilInvrcpt.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
            bilInvrcpt.setData("AR_AMT", parm.getValue("AR_AMT"));
            bilInvrcpt.setData("OPT_USER", parm.getValue("OPT_USER"));
            bilInvrcpt.setData("CANCEL_FLG", "0");
            bilInvrcpt.setData("STATUS", "0");
            bilInvrcpt.setData("ADM_TYPE", "O");
            bilInvrcpt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
            //调用写票据明细
            result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
        }
        
        //冲销预交金
      int count = payParm.getCount("RECEIPT_NO"); 
      //System.out.println("冲销预交金的笔数count::::"+count);
      for(int j = 0;j<count; j++){    
      	//System.out.println("预交金数据：：："+parm.getParm("payParm").getRow(j));
      	  result = this.onReturnContractPay(parm.getParm("payParm").getRow(j),connection);
          if (result.getErrCode() < 0) {
 	           err(result.getErrCode() + " " + result.getErrText());
 	           connection.rollback();
 	           connection.close();
 	           return result;
 	        }
       }
       connection.commit();
       connection.close();
       return result;
    }
  
}
