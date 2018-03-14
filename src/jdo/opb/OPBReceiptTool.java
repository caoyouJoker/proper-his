package jdo.opb;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import jdo.reg.PatAdmTool;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

public class OPBReceiptTool extends TJDOTool {
    /**
     * 实例
     */
    public static OPBReceiptTool instanceObject;

    /**
     * 得到实例
     *
     * @return OPBReceiptTool
     */
    public static OPBReceiptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBReceiptTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public OPBReceiptTool() {
        setModuleName("bil\\BILOpbRecepModule.x");
        onInit();
    }

    /**
     * 票据保存入口
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm opbSave(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            result = initReceipt(parm.getRow(i), connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        return result;
    }

    /**
     * 保存新票据
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm initReceipt(TParm parm, TConnection connection) {
        // 取号原则得到票据号
        String receiptNo = "";
        if (null != parm.getValue("FLG") && parm.getValue("FLG").equals("Y")) {
        } else {
            // 取号原则得到票据号
            receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                    "RECEIPT_NO", "RECEIPT_NO");
            parm.setData("RECEIPT_NO", receiptNo);
        }

        TParm result = update("insertReceipt", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        result.setData("RECEIPT_NO", receiptNo); //
        return result;
    }

    /**
     * 保存退费新票据(flg true: 添加一条新的收据  false 添加一条作废数据)
     * @param parm TParm
     * @param flg boolean
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertBackReceipt(TParm parm, boolean flg,
                                   TConnection connection) {
        // 取号原则得到票据号
    	String receiptNo="";
    	if(parm.getValue("RECEIPT_NO").length()<=0){
    		receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO", "RECEIPT_NO");
    		// 票据流水号
    		parm.setData("RECEIPT_NO", receiptNo);
    	}else{
    		receiptNo=parm.getValue("RECEIPT_NO");
    	}
        // 宏冲票据号
        //===zhangp 20120319
//		if (flg) {
//			parm.setData("PRINT_USER","");
//		}else{
//		parm.setData("RESET_RECEIPT_NO", receiptNo);
        parm.setData("RESET_RECEIPT_NO", "");
//		parm.setData("PRINT_USER",null==parm.getValue("PRINT_USER") ||parm.getValue("PRINT_USER").length()<=0 ?"":parm.getValue("PRINT_USER"));
//		}
        TParm result = update("insertReceipt", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        // 返回渠道的票据流水号
        result.setData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * 更新退费票据信息
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updateBackReceipt(TParm parm, TConnection connection) {
        TParm result = update("updateBackReceipt", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 医疗卡更新退费票据信息
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */

    public TParm updateBackReceiptOne(TParm parm, TConnection connection) {
        TParm result = update("updateBackReceiptOne", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 查找病患的费用列表:：记账查找，没有执行结算操作的数据退费
     *
     * @param caseNo
     *            String
     * @return TParm =======================pangben 20110823
     */
    public TParm getContractReceipt(String caseNo) {
        TParm result = new TParm();
        if (caseNo == null || caseNo.length() == 0) {
            //System.out.println("传入参数为空");
            return result.newErrParm( -1, "参数不能为空");
        }
        result.setData("CASE_NO", caseNo);
        result = query("getContractReceipt", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 查找病患的费用列表
     *
     * @param caseNo
     *            String
     * @return TParm
     */
    public TParm getReceipt(String caseNo) {
        TParm result = new TParm();
        if (caseNo == null || caseNo.length() == 0) {
            //System.out.println("传入参数为空");
            return result.newErrParm( -1, "参数不能为空");
        }
        result.setData("CASE_NO", caseNo);
        result = query("getReceipt", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 得到一条门诊票据档信息
     *
     * @param receiptNo
     *            String
     * @return TParm
     */
    public TParm getOneReceipt(String receiptNo) {
        TParm result = new TParm();
        if (receiptNo == null || receiptNo.length() == 0) {
            //System.out.println("传入参数为空");
            return result.newErrParm( -1, "参数不能为空");
        }
        result.setData("RECEIPT_NO", receiptNo);
        result = query("getOneReceipt", result);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 得到一条门诊票据档信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getOneReceipt(TParm parm) {
        TParm result = query("getOneReceipt", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 更新补印票据时票据号:现金
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updatePrintNO(TParm parm, TConnection connection) {
        TParm result = update("updatePrintNO", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 更新补印票据时票据号：医疗卡
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm updateEKTPrintNO(TParm parm, TConnection connection) {
        TParm result = update("updateEKTPrintNO", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 得到日结金额
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getSumAramt(TParm parm) {
        TParm result = query("getSumAramt", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 得到日结金额OEH
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getSumAramtAll(TParm parm) {
        TParm result = query("getSumAramtAll", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 得到日结票据张数
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getReceiptCount(TParm parm) {
        TParm result = query("getReceiptCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 日结票据
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm saveAcconntReceipt(TParm parm, TConnection connection) {
        TParm result = update("account", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 日结票据o e h
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm saveAcconntReceiptAll(TParm parm, TConnection connection) {
        TParm result = update("accountAll", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 得到日结打印数据
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm getAccountPrint(TParm parm) {
        TParm result = query("getAccountPrint", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 医疗卡打票操作,获得票据金额
     *
     * @param parm
     *            TParm
     * @return TParm ================pangben 20111024
     */
    public TParm getSumEKTFee(TParm parm) {
        TParm result = query("getSumEKTFee", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 医疗卡打票操作,判断是否存在票据
     *
     * @param parm
     *            TParm
     * @return TParm ================pangben 20111024
     */
    public TParm getCountEKT(TParm parm) {
        TParm result = query("getCountEKT", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 医疗卡退费清空收据票号================pangben 20111028
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateUnPrintNo(TParm parm, TConnection connection) {
        TParm result = update("updateUnPrintNo", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }

    /**
     * 医保使用医疗卡绿色通道 flg true: 正流程 医保扣款医疗卡 退费操作 false ：逆流程 医保退费 医疗卡扣款
     * @param parm TParm
     * @param result TParm
     * @param flg boolean
     * @param connection TConnection
     * @return TParm
     */
    private TParm greenPay(TParm parm, TParm result, boolean flg,
                           TConnection connection) {
        double green = 0.00;
        boolean temp = true;
        if (flg) {

            green = parm.getDouble("GREEN_BALANCE")
                    + parm.getDouble("APPLY_AMT");
            temp = green <= parm.getDouble("GREEN_PATH_TOTAL") ? true : false;
        } else {
            green = parm.getDouble("GREEN_BALANCE")
                    - parm.getDouble("APPLY_AMT");
            temp = green >= 0 ? true : false; // 判断绿色通道是否扣款有效
        }
        if (temp) { // 使用绿色通道 退还全部金额
            if (flg) {
                parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0)
                             + parm.getDouble("APPLY_AMT"));
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0));
                parm.setData("GREEN_BALANCE", green);
            } else {
                parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0)
                             - parm.getDouble("APPLY_AMT"));
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0));
                parm.setData("GREEN_BALANCE", green);
            }

        } else {
            if (flg) { // 使用绿色通道退还部分金额 医疗卡中回冲费用
                parm.setData("PAY_OTHER1", parm.getDouble("GREEN_PATH_TOTAL"));
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0)
                             + parm.getDouble("GREEN_PATH_TOTAL") - green);
            } else {
                result.setErr( -1, "绿色通道修改有问题");
                return result;
            }
        }
        TParm updateGreen = PatAdmTool.getInstance().updateEKTGreen(parm,
                connection);
        if (updateGreen.getErrCode() < 0) {
            updateGreen.setErr( -1, "绿色通道修改有问题");
            return updateGreen;
        }
        return updateGreen;
    }

    /**
     * 撤销门诊收费医保扣款修改BIL_OPB_RECP 表中医保金额属性值
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm concelINSAmtPrint(TParm parm, TConnection connection) {
        TParm result = selectMedicalCardAmt(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        if (result.getCount() <= 0) {
            return null;
        }

        // 使用绿色通道
        if (null != parm.getValue("GREEN_BALANCE")
            && parm.getValue("GREEN_BALANCE").length() > 0) {
            if (parm.getDouble("GREEN_BALANCE") < parm
                .getDouble("GREEN_PATH_TOTAL")) { // 使用绿色通道
                if (greenPay(parm, result, false, connection).getErrCode() < 0) {
                    result.setErr( -1, "绿色通道修改有问题");
                    return result;
                }
            } else {
                // 没有使用绿色通道
                parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                        "PAY_MEDICAL_CARD", 0)
                             + parm.getDouble("APPLY_AMT"));
                parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0));
            }
        } else {
            parm.setData("PAY_MEDICAL_CARD", result.getDouble(
                    "PAY_MEDICAL_CARD", 0)
                         + parm.getDouble("APPLY_AMT"));
            parm.setData("PAY_OTHER1", result.getDouble("PAY_OTHER1", 0));
        }
        result = update("updateINSAmtPrint", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 获得收据医疗卡金额 门诊收费医保扣款修改BIL_OPB_RECP 表中医保金额属性值使用
     * @param parm TParm
     * @return TParm
     */
    private TParm selectMedicalCardAmt(TParm parm) {
        TParm result = query("selectMedicalCardAmt", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 通过PRINT_NO 查询此次就诊是否是医疗卡操作
     * @param parm TParm
     * @return TParm
     */
    public TParm seletEktState(TParm parm) {
        TParm result = query("seletEktState", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 查找日结票据张数
     * ===zhangp 20120319
     * @param parm TParm
     * @return TParm
     */
    public TParm getOpbRecpCount(TParm parm) {
        TParm result = query("getOpbRecpCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm getOpbResetCount(TParm parm) {
        TParm result = query("getOpbResetCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }


    /**
     * 更新BIL_OPB_RECEIPT的charge01-30,自定义打票
     * @param parm
     * @return
     */
    public TParm updateOpbReceiptCharge(TParm parm){
    	String sql = "UPDATE BIL_OPB_RECP "+
                     "SET  CHARGE01="+parm.getValue("CHARGE01")+",CHARGE02="+parm.getValue("CHARGE02")+",CHARGE03="+parm.getValue("CHARGE03")+",CHARGE04="+parm.getValue("CHARGE04")+"," +
                          "CHARGE05="+parm.getValue("CHARGE05")+",CHARGE06="+parm.getValue("CHARGE06")+",CHARGE07="+parm.getValue("CHARGE07")+",CHARGE08="+parm.getValue("CHARGE08")+"," +
                          "CHARGE09="+parm.getValue("CHARGE09")+",CHARGE10="+parm.getValue("CHARGE10")+",CHARGE11="+parm.getValue("CHARGE11")+",CHARGE12="+parm.getValue("CHARGE12")+"," +
                          "CHARGE13="+parm.getValue("CHARGE13")+",CHARGE14="+parm.getValue("CHARGE14")+",CHARGE15="+parm.getValue("CHARGE15")+",CHARGE16="+parm.getValue("CHARGE16")+"," +
                          "CHARGE17="+parm.getValue("CHARGE17")+",CHARGE18="+parm.getValue("CHARGE18")+",CHARGE19="+parm.getValue("CHARGE19")+",CHARGE20="+parm.getValue("CHARGE20")+"," +
                          "CHARGE21="+parm.getValue("CHARGE21")+",CHARGE22="+parm.getValue("CHARGE22")+",CHARGE23="+parm.getValue("CHARGE23")+",CHARGE24="+parm.getValue("CHARGE24")+"," +
                          "CHARGE25="+parm.getValue("CHARGE25")+",CHARGE26="+parm.getValue("CHARGE26")+",CHARGE27="+parm.getValue("CHARGE27")+",CHARGE28="+parm.getValue("CHARGE28")+"," +
                          "CHARGE29="+parm.getValue("CHARGE29")+",CHARGE30="+parm.getValue("CHARGE30")+",AR_AMT="+parm.getValue("AR_AMT")+" ,TOT_AMT="+parm.getValue("TOT_AMT")+ "   "+
                     "WHERE RECEIPT_NO='"+parm.getValue("RECEIPT_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' ";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result ;
    }
    
}
