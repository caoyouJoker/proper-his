package jdo.ibs;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.bil.BIL;
import jdo.bil.BILIBSRecpdTool;
import jdo.bil.BILIBSRecpmTool;
import jdo.bil.BILTool;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.bil.BILPrintTool;
import jdo.ins.TJINSRecpTool;

import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

import jdo.sys.SYSStationTool;
import jdo.sys.DeptTool;

/**
 *
 * <p>Title: 住院计价工具类</p>
 *
 * <p>Description: 住院计价工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 * 
 */
public class IBSTool extends TJDOTool {
    /**
     * 实例
     */
    public static IBSTool instanceObject;
    /**
     * 得到实例
     * @return IBSTool
     */
    public static IBSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IBSTool() {
        onInit();
    }

    public int getselMaxSeq(String caseNo, String billNo) {
        TParm selMaxSeqParm = new TParm();
        selMaxSeqParm.setData("CASE_NO", caseNo);
        selMaxSeqParm.setData("BILL_NO", billNo);
        TParm selMaxSeq = IBSBillmTool.getInstance().selMaxSeq(
                selMaxSeqParm);
        if (selMaxSeq.getCount("BILL_SEQ") <= 0)
            return 1;
        return selMaxSeq.getInt("BILL_SEQ", 0) + 1;
    }

    /**
     * 逐个写入账单档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIBSBillDataOne(TParm parm, TConnection connection) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        String caseNo = parm.getValue("CASE_NO");
        TParm result = new TParm();
        TParm p = new TParm();
        String flg = parm.getValue("FLG");
        if ("Y".equals(flg))
            parm.setData("BILL_DATE", SystemTool.getInstance().getDate());
        TParm orderm = IBSOrdermTool.getInstance().selectdate(parm);
        //System.out.println("未生成账单数据集M======"+orderm);
        if (orderm.getErrCode() < 0) {
            err(orderm.getErrText());
            return orderm;
        }
        if ("ADM".equals(parm.getValue("TYPE")) && orderm.getCount() == 0)
            return parm;
        if (orderm.getCount() == 0)
            return parm;
      
        if (orderm.getCount() < 0) {
        	
            //置为出院无账单状态
            result = ADMTool.getInstance().updateBillStatus("1", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrText());
                return result;
            }
            orderm.setErr( -1, "无费用数据");
            return orderm;
        }
        String billNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "BILL_NO",
                "BILL_NO");
        int maxBillSeq = getselMaxSeq(caseNo, billNo);
        p.setData("BILL_SEQ", maxBillSeq);
        //插入IBS_BILLM表中数据
        p.setData("BILL_NO", billNo);
        p.setData("CASE_NO", caseNo);
        p.setData("IPD_NO", orderm.getData("IPD_NO", 0));
        p.setData("MR_NO", orderm.getData("MR_NO", 0));
        p.setData("BILL_DATE", parm.getData("BILL_DATE"));
        p.setData("REFUND_FLG", "N");
        p.setData("REFUND_BILL_NO", "");
        p.setData("RECEIPT_NO", "");
        p.setData("CHARGE_DATE", new TNull(Timestamp.class));
        p.setData("CTZ1_CODE",
                  orderm.getData("CTZ1_CODE", 0) == null ? "" :
                  orderm.getData("CTZ1_CODE", 0));
        p.setData("CTZ2_CODE",
                  orderm.getData("CTZ2_CODE", 0) == null ? "" :
                  orderm.getData("CTZ2_CODE", 0));
        p.setData("CTZ3_CODE",
                  orderm.getData("CTZ3_CODE", 0) == null ? "" :
                  orderm.getData("CTZ3_CODE", 0));
        TParm orderTimeData = new TParm();
        orderTimeData.setData("CASE_NO", orderm.getData("CASE_NO", 0));
        //modify by caowl 20130110 start
        String sql ="SELECT COUNT(*) AS COUNT FROM IBS_BILLM WHERE CASE_NO = '"+orderm.getData("CASE_NO", 0)+"'";
        TParm parmDate = new TParm(TJDODBTool.getInstance().select(sql));    
        if(parmDate.getInt("COUNT",0)==0){
        	//第一次生成账单 begin_date取adm_inp表中in_date时间,   end_date则与bill_date一致.
        	String sql_in = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO = '"+orderm.getData("CASE_NO", 0)+"'";
        	TParm dateParm = new TParm(TJDODBTool.getInstance().select(sql_in));       	
        	 p.setData("BEGIN_DATE",dateParm.getTimestamp("IN_DATE",0) );
        }else{
        	//不是第一次生成账单  取最后一次生成的账单中的end_date赋值给此账单的begin_date; end_date仍与此账单的bill_date一致.
        	String sqls = "SELECT MAX (END_DATE) AS BEGIN_DATE FROM IBS_BILLM WHERE CASE_NO = '"+orderm.getData("CASE_NO", 0)+"' ";
        	TParm beginParm = new TParm(TJDODBTool.getInstance().select(sqls));         	
        	java.sql.Timestamp now = beginParm.getTimestamp("BEGIN_DATE",0);
        	long time = now.getTime();			
			java.sql.Timestamp date = new Timestamp(time+1000);
			p.setData("BEGIN_DATE", date);
        	//p.setData("BEGIN_DATE", beginParm.getTimestamp("BEGIN_DATE",0));
        }        
        p.setData("END_DATE", parm.getData("BILL_DATE"));
        //modify by caowl 20130110 end 
        if ("Y".equals(flg)) {
            p.setData("DISCHARGE_FLG", "Y");
            //置为出院未缴费状态
            result = ADMTool.getInstance().updateBillStatus("2", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrText());
                return result;
            }
        } else {
            p.setData("DISCHARGE_FLG", "N");

        }
        p.setData("DEPT_CODE", orderm.getData("DEPT_CODE", 0));
        p.setData("STATION_CODE", orderm.getData("STATION_CODE", 0));
        TParm stationInfo = SYSStationTool.getInstance().selStationRegion(
                orderm.getValue("STATION_CODE", 0));
        p.setData("REGION_CODE", stationInfo.getData("REGION_CODE", 0));
        //===zhangp 20120425 start
//      p.setData("BED_NO", orderm.getData("BED_NO", 0));
      p.setData("BED_NO", orderm.getData("BED_NO", 0)==null?"":orderm.getData("BED_NO", 0));
      //===zhangp 20120425 end
        TParm orderDData = new TParm();
        orderDData.setData("CASE_NO", orderm.getData("CASE_NO", 0));
        orderDData.setData("BILL_DATE", parm.getData("BILL_DATE"));
        if ("Y".equals(flg))
            orderDData.setData("BILL_DATE", SystemTool.getInstance().getDate());
//       System.out.println("时间！！！！！"+orderDData.getData("BILL_DATE"));
        TParm orderDParm = IBSOrderdTool.getInstance().selOrderDAll(
                orderDData);
//        System.out.println("查询未产生账单的费用明细档所有数据DDDDD"+orderDParm);
        int orderDCount = orderDParm.getCount("CASE_NO");
        double ownAmt = 0.00;
        double nhiAmt = 0.00;
        double arAmt = 0.00;
//        double reduceAmt = 0.00;
        for (int d = 0; d < orderDCount; d++) {
            ownAmt = ownAmt + StringTool.round(
                    orderDParm.getDouble("DOSAGE_QTY", d) *
                    orderDParm.getDouble("OWN_PRICE", d), 2);
            nhiAmt = nhiAmt + StringTool.round(
                    orderDParm.getDouble("DOSAGE_QTY", d) *
                    orderDParm.getDouble("NHI_PRICE", d), 2);
            arAmt = arAmt + StringTool.round(orderDParm.getDouble("TOT_AMT", d), 2);//add by wanglong 20140314
        }
//      arAmt = StringTool.round(ownAmt - reduceAmt, 2);
        p.setData("OWN_AMT", StringTool.round(ownAmt, 2));//modify by wanglong 20140314
        p.setData("NHI_AMT", StringTool.round(nhiAmt, 2));//modify by wanglong 20140314
        p.setData("APPROVE_FLG", "N");
        p.setData("REDUCE_REASON", "N");
        p.setData("REDUCE_AMT", 0.00);
        p.setData("REDUCE_DATE", "");
        p.setData("REDUCE_DEPT_CODE", "");
        p.setData("REDUCE_RESPOND", "");
        p.setData("AR_AMT", StringTool.round(arAmt, 2));//modify by wanglong 20140314
        p.setData("PAY_AR_AMT", 0.00);
        p.setData("CANDEBT_CODE", "");
        p.setData("CANDEBT_PERSON", "");
        p.setData("REFUND_CODE", "");
        p.setData("REFUND_DATE", new TNull(Timestamp.class));
        p.setData("OPT_USER", parm.getData("OPT_USER"));
        p.setData("OPT_TERM", parm.getData("OPT_TERM"));

        //插入ibs_billm
        result = IBSBillmTool.getInstance().insertdata(p, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        p.setData("BILL_DATE", parm.getData("BILL_DATE"));
        //更新ibs_ordm
        result = IBSOrdermTool.getInstance().updateBillNO(p, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //更新ibs_ordd
        result = IBSOrderdTool.getInstance().updateBillNO(p, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //准备插入IBS_BILLD的数据条数
//        System.out.println("准备插入IBS_BILLD的数据条数入参"+parm);
//        TParm inDParm = IBSOrderdTool.getInstance().selectdata(parm);
        
        /*modified by Eric 20170606 */
        TParm inDParm = IBSOrderdTool.getInstance().selectdata2(parm);
        

//        System.out.println("准备插入IBS_BILLD的数据"+inDParm);
        int dCount = inDParm.getCount("CASE_NO");
        TParm dParmSingle = new TParm();
        //插入IBS_BILLD表中数据
        for (int i = 0; i < dCount; i++) {
            String rexpCode = inDParm.getValue("REXP_CODE", i);
            double ownAmtD = inDParm.getDouble("OWN_AMT", i);
            double arAmtD = inDParm.getDouble("AR_AMT", i);
//            int row = -1;
//            Vector endVct = (Vector) dParmSingle.getData("REXP_CODE");
//            if (endVct != null) {
//                row = endVct.indexOf(rexpCode);
//            }
//            if (row == -1) {
//                dParmSingle.addData("REXP_CODE", rexpCode);
//                dParmSingle.addData("BILL_NO", billNo);
//                dParmSingle.addData("BILL_SEQ", maxBillSeq);
//                dParmSingle.addData("PAY_AR_AMT", 0);
//                dParmSingle.addData("OPT_USER", parm.getData("OPT_USER"));
//                dParmSingle.addData("OPT_TERM", parm.getData("OPT_TERM"));
//                dParmSingle.addData("REFUND_BILL_NO", "");
//                dParmSingle.addData("REFUND_FLG", "N");
//                dParmSingle.addData("REFUND_CODE", "");
//                dParmSingle.addData("REFUND_DATE", new TNull(Timestamp.class));
//                dParmSingle.addData("OWN_AMT", StringTool.round(ownAmtD, 2));
//                dParmSingle.addData("AR_AMT", StringTool.round(arAmtD, 2));
//
//            } else {
//                dParmSingle.setData("OWN_AMT", row,
//                                    StringTool.round(ownAmtD +
//                        dParmSingle.getDouble("OWN_AMT", row), 2));
//                dParmSingle.setData("AR_AMT", row,
//                                    StringTool.round(arAmtD +
//                        dParmSingle.getDouble("AR_AMT", row), 2));
//            }
            
            /*modified by Eric 20170606 START */
            dParmSingle.addData("REXP_CODE", rexpCode);
            dParmSingle.addData("BILL_NO", billNo);
            dParmSingle.addData("BILL_SEQ", maxBillSeq);
            dParmSingle.addData("PAY_AR_AMT", 0);
            dParmSingle.addData("OPT_USER", parm.getData("OPT_USER"));
            dParmSingle.addData("OPT_TERM", parm.getData("OPT_TERM"));
            dParmSingle.addData("REFUND_BILL_NO", "");
            dParmSingle.addData("REFUND_FLG", "N");
            dParmSingle.addData("REFUND_CODE", "");
            dParmSingle.addData("REFUND_DATE", new TNull(Timestamp.class));
            dParmSingle.addData("OWN_AMT", StringTool.round(ownAmtD, 2));
            dParmSingle.addData("AR_AMT", StringTool.round(arAmtD, 2));
            /*modified by Eric 20170606 END */
            
            
            
            
            
            
        }
        int inBilldCount = dParmSingle.getCount("BILL_NO");
        for (int j = 0; j < inBilldCount; j++) {
            TParm inBilldParm = dParmSingle.getRow(j);
            result = IBSBilldTool.getInstance().insertdata(inBilldParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

        }

        return result;
    }

//    public TParm getOrdermParm(TParm parm)
//    {
//        TParm result = new TParm();
//        return result;
//    }
//    public TParm getOrderdParm(TParm parm)
//    {
//        TParm result = new TParm();
//        return result;
//    }
    /**
     * 新增账单(insert IBS_BILLM,IBS_BILLD,update IBS_ORDM,IBS_ORDD)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIBSBillData(TParm parm, TConnection connection) {
        //System.out.println("新增账单"+parm);
        TParm result = new TParm();
        int count = parm.getCount();
        Vector v = (Vector) parm.getData("CASE_NO");
       // System.out.println("新增账单  参数："+parm);
        for (int i = 0; i < count; i++) {
            parm.setData("CASE_NO", v.get(i));
//            System.out.println("新增账单入参"+parm);
            //插入ibs_billm
            result = insertIBSBillDataOne(parm, connection);
            if (result.getErrCode() < 0) {
                return result;
            }
            result = ADMTool.getInstance().updateBillDate(parm.getValue(
                    "CASE_NO"), connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

        }
        return result;
    }

    /**
     * 缴费作业保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertIBSChargeData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        TParm recpmParm = parm.getParm("RECPM");
        String recpRecpNo = recpmParm.getValue("RECEIPT_NO");
        TParm bilPayParm = parm.getParm("BILPAY");
        //预交金注记 有true 没有 flase
        boolean payFlg = true;
        if (bilPayParm == null || bilPayParm.getCount("CASE_NO") <= 0)
            payFlg = false;
        //caowl 20130205 start
        String sql = "SELECT BILL_STATUS FROM ADM_INP WHERE CASE_NO ='"+recpmParm.getValue("CASE_NO")+"'";
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String bill_status = selParm.getValue("BILL_STATUS",0);
    	if(!bill_status.equals("0")){
    		 result = ADMTool.getInstance().updateBillStatus("4",
    	                recpmParm.getValue("CASE_NO"), connection);
    	}
    	//caowl 20130205 end
//        result = ADMTool.getInstance().updateBillStatus("4",
//                recpmParm.getValue("CASE_NO"), connection);
        if (result.getErrCode() < 0) {
            err(result.getErrText());
            return result;
        }
        if (payFlg) {
            //预交金操作
            bilPayParm.setData("IBS_RECP_NO", recpRecpNo);
            result = BILTool.getInstance().onOffBilPay(bilPayParm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        //插入收据主档
//        System.out.println("收据主档数据" + recpmParm);
        result = BILIBSRecpmTool.getInstance().insertData(recpmParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20130717 start
        //TODO
        TParm tjinsParm = parm.getParm("TJINS");
        tjinsParm.setData("CASE_NO", recpmParm.getValue("CASE_NO"));
        tjinsParm.setData("RECEIPT_NO", recpRecpNo);
        result = TJINSRecpTool.getInstance().updateRecpm(tjinsParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20130717 end
        //插入收据明细档
        TParm recpdParm = parm.getParm("RECPD");
//        System.out.println("细表数据"+recpdParm);
        TParm inRecpdParm = new TParm();
        int recpdCount = recpdParm.getCount("RECEIPT_NO");
        for (int i = 0; i < recpdCount; i++) {
            inRecpdParm.setData("RECEIPT_NO", recpdParm.getData("RECEIPT_NO", i));
            inRecpdParm.setData("BILL_NO", recpdParm.getData("BILL_NO", i));
            inRecpdParm.setData("REXP_CODE", recpdParm.getData("REXP_CODE", i));
            inRecpdParm.setData("WRT_OFF_AMT",
                                recpdParm.getData("WRT_OFF_AMT", i));
            inRecpdParm.setData("OPT_USER", recpdParm.getData("OPT_USER", i));
            inRecpdParm.setData("OPT_TERM", recpdParm.getData("OPT_TERM", i));
            result = BILIBSRecpdTool.getInstance().insertData(inRecpdParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        TParm billdParm = parm.getParm("BILLD");
        //更新收据号码
        TParm updataForRecpParm = new TParm();
        StringBuffer allBillNo = new StringBuffer();
        int blldCount = billdParm.getCount("BILL_NO");
        for (int j = 0; j < blldCount; j++) {
            String seq = "";
            seq = "'" + billdParm.getValue("BILL_NO", j) + "'";
            if (allBillNo.length() > 0)
                allBillNo.append(",");
            allBillNo.append(seq);
        }
        String allBillNoStr = allBillNo.toString();
        updataForRecpParm.setData("RECEIPT_NO",
                                  recpdParm.getData("RECEIPT_NO", 0));
        updataForRecpParm.setData("BILL_NO", allBillNoStr);
//        System.out.println("缴费作业更新billm入/参｝｝｝｝｝"+updataForRecpParm);
        result = IBSBillmTool.getInstance().updataForRecp(updataForRecpParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
       
        result = BILPrintTool.getInstance().saveIBSRecp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        result = ADMTool.getInstance().updateBillDate(recpmParm.getValue(
                "CASE_NO"), connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 新增费用(For INW , UDD,PATCH)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm dataType=3 护士站,dataType=2 药房,dataType=0 批次,dataType=4 膳食
     */
    public TParm insertIBSOrder(TParm parm, TConnection connection) {
//        System.out.println("新增费用(For INW , UDD,PATCH)"+parm);
        TParm result = new TParm();
        TParm mParm = parm.getParm("M");
//        System.out.println("pp+++====mParm mParm is ::"+mParm);
        String dspnKind = "";//传过来的DSPN_KIND的值
        Timestamp billDate;
//        System.out.println("计价类别》》》》》》》》》》》"+parm.getValue("DATA_TYPE"));
        if ("0".equals(parm.getValue("DATA_TYPE")))
            billDate = parm.getTimestamp("BILL_DATE");
        else
            billDate = SystemTool.getInstance().getDate();
//        System.out.println("计费日期"+billDate);
        TParm inMParm = new TParm();
        inMParm.setData("CASE_NO", mParm.getData("CASE_NO", 0));
        inMParm.setData("CASE_NO_SEQ", mParm.getData("CASE_NO_SEQ", 0));
        inMParm.setData("BILL_DATE", billDate);
        inMParm.setData("IPD_NO", mParm.getData("IPD_NO", 0));
        inMParm.setData("MR_NO", mParm.getData("MR_NO", 0));
        inMParm.setData("DEPT_CODE", mParm.getData("DEPT_CODE", 0));
        inMParm.setData("STATION_CODE", mParm.getData("STATION_CODE", 0));
        inMParm.setData("BED_NO", mParm.getData("BED_NO", 0));
        inMParm.setData("DATA_TYPE", parm.getData("DATA_TYPE"));
        inMParm.setData("BILL_NO", "");
        inMParm.setData("OPT_USER", mParm.getData("OPT_USER", 0));
        inMParm.setData("OPT_TERM", mParm.getData("OPT_TERM", 0));
        TParm stationInfo = SYSStationTool.getInstance().selStationRegion(mParm.
                getValue("STATION_CODE", 0));
        inMParm.setData("REGION_CODE", stationInfo.getData("REGION_CODE", 0));
//        System.out.println("插入OrderM数据" + inMParm);
        result = IBSOrdermTool.getInstance().insertdata(inMParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
//        System.out.println("插入orderM数据成功！！HHHHHHHHHH");
        int dCount = mParm.getCount("CASE_NO");
        double totalAmtForADM = 0.00;
        for (int j = 0; j < dCount; j++) {
            TParm inDParm = new TParm();
            inDParm.setData("CASE_NO",
                            mParm.getData("CASE_NO", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("CASE_NO", j));
            inDParm.setData("CASE_NO_SEQ",
                            mParm.getData("CASE_NO_SEQ", j) == null ? -1 :
                            mParm.getData("CASE_NO_SEQ", j));
            inDParm.setData("SEQ_NO",
                            mParm.getData("SEQ_NO", j) == null ? -1 :
                            mParm.getData("SEQ_NO", j));
            inDParm.setData("BILL_DATE", billDate);
            inDParm.setData("EXEC_DATE", billDate);//执行时间
            
            inDParm.setData("ORDER_NO",
                            mParm.getData("ORDER_NO", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_NO", j));
            inDParm.setData("ORDER_SEQ",
                            mParm.getData("ORDER_SEQ", j) == null ?
                            "0" :
                            mParm.getData("ORDER_SEQ", j));
            inDParm.setData("ORDER_CODE",
                            mParm.getData("ORDER_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_CODE", j));
            TParm sysFeeInfo = SYSFeeTool.getInstance().getFeeAllData(mParm.
                    getValue("ORDER_CODE", j));
            inDParm.setData("ORDER_CHN_DESC",
                            sysFeeInfo.getData("ORDER_DESC", 0) == null ?
                            new TNull(String.class) :
                            sysFeeInfo.getData("ORDER_DESC", 0));
            inDParm.setData("ORDER_CAT1_CODE",
                            mParm.getData("ORDER_CAT1_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_CAT1_CODE", j));
            inDParm.setData("CAT1_TYPE",
                            mParm.getData("CAT1_TYPE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("CAT1_TYPE", j));
            inDParm.setData("ORDERSET_GROUP_NO",
                            mParm.getData("ORDERSET_GROUP_NO", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDERSET_GROUP_NO", j));
            inDParm.setData("ORDERSET_CODE",
                            mParm.getData("ORDERSET_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDERSET_CODE", j));
            inDParm.setData("INDV_FLG",
                            mParm.getData("HIDE_FLG", j) == null ?
                            "N" :
                            mParm.getData("HIDE_FLG", j));
            inDParm.setData("DS_FLG",
                            mParm.getData("DS_FLG", j) == null ?
                            "N" :
                            mParm.getData("DS_FLG", j));
            inDParm.setData("DEPT_CODE",
                            mParm.getData("ORDER_DEPT_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_DEPT_CODE", j));
            inDParm.setData("STATION_CODE",
                            mParm.getData("STATION_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("STATION_CODE", j));
            inDParm.setData("DR_CODE",
                            mParm.getData("ORDER_DR_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("ORDER_DR_CODE", j));
            inDParm.setData("EXE_DEPT_CODE",
                            mParm.getData("EXEC_DEPT_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("EXEC_DEPT_CODE", j));
            inDParm.setData("EXE_STATION_CODE",
                            mParm.getData("STATION_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("STATION_CODE", j));
            inDParm.setData("COST_CENTER_CODE",
                            DeptTool.getInstance().getCostCenter(inDParm.
                    getValue("EXEC_DEPT_CODE"),
                    inDParm.getValue("EXE_STATION_CODE")) == null ?
                            new TNull(String.class) :
                            DeptTool.getInstance().getCostCenter(inDParm.
                    getValue("EXEC_DEPT_CODE"),
                    inDParm.getValue("EXE_STATION_CODE")));

            inDParm.setData("EXE_DR_CODE",
                            mParm.getData("OPT_USER", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("OPT_USER", j));
            inDParm.setData("MEDI_QTY",
                            mParm.getData("MEDI_QTY", j) == null ? 0.00 :
                            mParm.getData("MEDI_QTY", j));
            inDParm.setData("MEDI_UNIT",
                            mParm.getData("MEDI_UNIT", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("MEDI_UNIT", j));
            inDParm.setData("DOSE_CODE", mParm.getData("DOSE_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("DOSE_CODE", j)); //剂型;剂型类型
            inDParm.setData("FREQ_CODE",
                            mParm.getData("FREQ_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("FREQ_CODE", j));
            inDParm.setData("TAKE_DAYS",
                            mParm.getData("TAKE_DAYS", j) == null ? 0 :
                            mParm.getData("TAKE_DAYS", j));
            inDParm.setData("DOSAGE_UNIT",
                            mParm.getData("DOSAGE_UNIT", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("DOSAGE_UNIT", j));
            inDParm.setData("OWN_PRICE",
                            mParm.getData("OWN_PRICE", j) == null ? 0.00 :
                            mParm.getData("OWN_PRICE", j));
            inDParm.setData("NHI_PRICE",
                            mParm.getData("NHI_PRICE", j) == null ? 0.00 :
                            mParm.getDouble("NHI_PRICE", j));
            inDParm.setData("OWN_FLG", "Y");
            inDParm.setData("BILL_FLG", "Y");
            inDParm.setData("REXP_CODE",
                            mParm.getData("REXP_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("REXP_CODE", j));
            inDParm.setData("BILL_NO", new TNull(String.class));
            inDParm.setData("HEXP_CODE",
                            mParm.getData("HEXP_CODE", j) == null ?
                            new TNull(String.class) :
                            mParm.getData("HEXP_CODE", j));
            Timestamp startDate = mParm.getTimestamp("DISPENSE_EFF_DATE", j);
            Timestamp endDate = mParm.getTimestamp("DISPENSE_END_DATE", j);
//            System.out.println("开始时间" + startDate);
//            System.out.println("结束时间" + endDate);
            inDParm.setData("BEGIN_DATE",
                            startDate == null ? new TNull(Timestamp.class) :
                            startDate);
            inDParm.setData("END_DATE",
                            endDate == null ? new TNull(Timestamp.class) :
                            endDate);
            if ("0".equals(parm.getValue("DATA_TYPE"))) {
                billDate = parm.getTimestamp("BILL_DATE");
                inDParm.setData("BEGIN_DATE", billDate);
                inDParm.setData("END_DATE", billDate);

            }
            String flg = parm.getValue("FLG");
            if (flg.equals("ADD")) {
                inDParm.setData("DOSAGE_QTY",

                                mParm.getData("DOSAGE_QTY", j) == null ? 0.00 :
                                mParm.getDouble("DOSAGE_QTY", j));
                inDParm.setData("COST_AMT",
                                mParm.getData("COST_AMT", j) == null ? 0.00 :
                                mParm.getDouble("COST_AMT", j));
                inDParm.setData("OWN_AMT",
                                StringTool.round(mParm.getDouble("OWN_AMT", j),
                                                 4));
                inDParm.setData("TOT_AMT",
                                StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                 2));
                totalAmtForADM = totalAmtForADM +
                                 StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                  2);
//                backWriteOdiParm.addData("BILL_FLG", "Y");

            } else {
                inDParm.setData("DOSAGE_QTY",
                                mParm.getData("DOSAGE_QTY", j) == null ? 0.00 :
                                ( -1) * mParm.getDouble("DOSAGE_QTY", j));
                inDParm.setData("COST_AMT",
                                mParm.getData("COST_AMT", j) == null ? 0.00 :
                                ( -1) * mParm.getDouble("COST_AMT", j));
                inDParm.setData("OWN_AMT",
                                ( -1) *
                                StringTool.round(mParm.getDouble("OWN_AMT", j),
                                                 4));
                inDParm.setData("TOT_AMT",
                                ( -1) *
                                StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                 2));
//                backWriteOdiParm.addData("BILL_FLG", "N");
                totalAmtForADM = totalAmtForADM +
                                 StringTool.round(mParm.getDouble("TOT_AMT", j),
                                                  2);
            }
            inDParm.setData("OWN_RATE",
                            mParm.getData("OWN_RATE", j) == null ? 0.00 :
                            mParm.getData("OWN_RATE", j));
            inDParm.setData("REQUEST_FLG", "N");
            inDParm.setData("REQUEST_NO", new TNull(String.class));
            inDParm.setData("INV_CODE", new TNull(String.class));
            inDParm.setData("OPT_USER", mParm.getData("OPT_USER", j));
            inDParm.setData("OPT_TERM", mParm.getData("OPT_TERM", j));
            String schdCode = getSchdCode(mParm.getValue("CASE_NO", j),mParm.getValue("DSPN_KIND", j),
            		mParm.getValue("ORDER_NO", j),mParm.getValue("ORDER_SEQ", j));
            inDParm.setData("SCHD_CODE",schdCode);//====临床路径时程   yanjing 20140903
            inDParm.setData("CLNCPATH_CODE",this.getClncPathCode(mParm.getValue("CASE_NO", j)));//====临床路径  yanjing 20140903
//            System.out.println("插入第" + j + "条细表数据" + inDParm);
            result = IBSOrderdTool.getInstance().insertdata(inDParm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        String caseNoForADM = mParm.getValue("CASE_NO", 0);
        TParm selADMAll = new TParm();
        selADMAll.setData("CASE_NO", caseNoForADM);
        TParm selADMAllData = ADMInpTool.getInstance().selectall(selADMAll);
        double totalAmt = selADMAllData.getDouble("TOTAL_AMT", 0);
        double curAmt = selADMAllData.getDouble("CUR_AMT", 0);
        String flg = parm.getValue("FLG");
        double patchAmt = 0.00;
        if (parm.getData("PATCH_AMT") != null) {
            patchAmt = parm.getDouble("PATCH_AMT");
//            System.out.println("批次金额"+patchAmt);
        }
        if (flg.equals("ADD")) {
            if ("0".equals(parm.getValue("DATA_TYPE"))) {
                //  System.out.println("批次执行医疗总金额"+(totalAmt+totalAmtForADM-patchAmt));
                result = ADMTool.getInstance().updateTOTAL_AMT("" +
                        (totalAmt + totalAmtForADM - patchAmt), caseNoForADM,
                        connection); //更新ADM中医疗总金额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
                // System.out.println("批次执行目前余额"+(curAmt - totalAmtForADM+patchAmt));
                result = ADMTool.getInstance().updateCUR_AMT("" +
                        (curAmt - totalAmtForADM + patchAmt), caseNoForADM,
                        connection); //更新ADM中目前余额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }

            } else {
                //System.out.println("医疗总金额"+(totalAmt+totalAmtForADM));
                result = ADMTool.getInstance().updateTOTAL_AMT("" +
                        (totalAmt + totalAmtForADM), caseNoForADM, connection); //更新ADM中医疗总金额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
//        System.out.println("行目前余额"+(curAmt - totalAmtForADM));
                result = ADMTool.getInstance().updateCUR_AMT("" +
                        (curAmt - totalAmtForADM), caseNoForADM, connection); //更新ADM中目前余额
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }

            }
        } else {
            //System.out.println("退费医疗总金额"+(totalAmt+totalAmtForADM));
            result = ADMTool.getInstance().updateTOTAL_AMT("" +
                    (totalAmt - totalAmtForADM), caseNoForADM, connection); //更新ADM中医疗总金额
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
//            System.out.println("退费目前余额"+(curAmt + totalAmtForADM));
            result = ADMTool.getInstance().updateCUR_AMT("" +
                    (curAmt + totalAmtForADM), caseNoForADM, connection); //更新ADM中目前余额
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

        }
//        System.out.println("计价结束》》》》》》》》》》》");
        return result;
    }
    /**
     * 查询病患的时程（区分长期和临时）
     */
    private String getSchdCode(String caseNo,String dspnKind,String orderNo,String seqNo){
    	String schdCode = "";
    	String sql = "";
//    	System.out.println("dspnKind dspnKind is :"+dspnKind);
    	if(dspnKind.equals("ST")){//临时医生下医嘱的时程
    		sql = "SELECT SCHD_CODE FROM ODI_ORDER WHERE CASE_NO = '"+caseNo+"' AND RX_KIND IN ('F','ST') " +
			"AND ORDER_NO = '"+orderNo+"' AND ORDER_SEQ = '"+seqNo+"' ";
    	}else{//长期时病人当前的时程
    		sql = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
    	}
//    	if(dspnKind.equals("UD")||dspnKind.equals("F")){//长期时病人当前的时程
//    		sql = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
//    	}else{//临时/首日量医生下医嘱的时程
//    		sql = "SELECT SCHD_CODE FROM ODI_ORDER WHERE CASE_NO = '"+caseNo+"' AND RX_KIND IN ('F','ST') " +
//    				"AND ORDER_NO = '"+orderNo+"' AND ORDER_SEQ = '"+seqNo+"' ";
//    	}
//    	System.out.println("----sqlsql sql is ::"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		schdCode = parm.getValue("SCHD_CODE", 0);
    	}
    	return schdCode;
    }
    /**
     * 查询病患的临床路径
     */
    private String getClncPathCode(String caseNo){
    	String clncpathCode = "";
    	String sql = "";
    		sql = "SELECT SCHD_CODE,CLNCPATH_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"' ";
//    	System.out.println("----sqlsql sql is ::"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()>0){
    		clncpathCode = parm.getValue("CLNCPATH_CODE", 0);
    	}
    	return clncpathCode;
    }

    /**
     * 得到账务序号,账务子序号,价格信息
     * @param parm TParm
     * @return TParm 加入
     */
    public TParm getIBSOrderData(TParm parm) {
//        System.out.println("getIBSOrderData方法入参" + parm);
        String flg = parm.getValue("FLG");
        TParm result = new TParm();
        TParm mParm = parm.getParm("M");
        result = mParm;
        TParm selLevelParm = new TParm();
        selLevelParm.setData("CASE_NO", mParm.getValue("CASE_NO", 0));
        TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
        String level = selLevel.getValue("SERVICE_LEVEL", 0);
        int dCount = mParm.getCount("CASE_NO");
        String caseNo = mParm.getValue("CASE_NO", 0);
        for (int i = 0; i < dCount; i++) {
            if(!caseNo.equals(mParm.getValue("CASE_NO", i))){
                result.setErr(-1,"不能传多个人员");
             //   System.out.println(mParm.getValue("CASE_NO", i)+"不能传多个人员");
                return result;
            }
            String ctz1Code = parm.getValue("CTZ1_CODE") == null ? "" :
                              parm.getValue("CTZ1_CODE");
            String ctz2Code = parm.getValue("CTZ2_CODE") == null ? "" :
                              parm.getValue("CTZ2_CODE");
            String ctz3Code = parm.getValue("CTZ3_CODE") == null ? "" :
                              parm.getValue("CTZ3_CODE");
            String orderCode = mParm.getValue("ORDER_CODE", i);
            String dspnKind = mParm.getValue("RX_KIND", i);//===PANGBEN 2015-8-20添加临时长期类别，临床路径使用
            TParm feeParm = new TParm();
            double ownPrice = 0.0;
            double nhiPrice = 0.0;
            String chargeHospCode = "";
            String chargeCode;
            //如果是扣库扣费的话，从SYS_FEE查询单价、医保价、院内费用代码
            if ("ADD".equalsIgnoreCase(flg)) {
                feeParm = SYSFeeTool.getInstance().getFeeData(orderCode);
                if ("2".equals(level)) {
                    ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
                } else if ("3".equals(level)) {
                    ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
                } else
                    ownPrice = feeParm.getDouble("OWN_PRICE", 0);
                nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
                chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
                TParm inChargeParm = new TParm();
                inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
                TParm chargeParm = SYSChargeHospCodeTool.getInstance().
                                   selectChargeCode(inChargeParm);
                chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);
            }
            //如果是增库增费的话，从IBS_ORDD查询当时的单价、医保价、院内费用代码
            else {
                feeParm = SYSFeeTool.getInstance().getFeeData(orderCode);
//                feeParm = IBSTool.getInstance().getIbsHistoryParam(mParm.getRow(
//                    i));
                if (feeParm.getErrCode() != 0) {
//                    System.out.println("查询历史数据错误=" + feeParm.getErrText());
                    return result.newErrParm( -1, "查询历史数据错误");
                }
                if ("2".equals(level)) {
                    ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
                } else if ("3".equals(level)) {
                    ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
                } else
                    ownPrice = feeParm.getDouble("OWN_PRICE", 0);
                nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
                chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);
                TParm inChargeParm = new TParm();
                inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
                TParm chargeParm = SYSChargeHospCodeTool.getInstance().
                                   selectChargeCode(inChargeParm);
                chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);
            }
            double dosageQty = TypeTool.getDouble(mParm.getData("DOSAGE_QTY", i));
            double ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
                                         orderCode, level);
            if (ownRate < 0) {
                return result.newErrParm( -1, "自付比例错误");
            }
//            System.out.println("护士站测试用ownRate自付比例"+ownRate);
            double ownAmt = ownPrice * dosageQty;
            double totAmt = ownAmt * ownRate;

            result.addData("HEXP_CODE", chargeHospCode);

            if (flg.equals("ADD")) {
                result.setData("BILL_FLG", i, "Y");
            } else {
                result.setData("BILL_FLG", i, "N");
            }
            //===PANGBEN 2015-8-20添加临时长期类别，临床路径使用
            result.addData("DSPN_KIND", dspnKind);
            result.addData("REXP_CODE", chargeCode);
            result.setData("OWN_PRICE", i, ownPrice);
            result.setData("NHI_PRICE", i, nhiPrice);
            result.setData("OWN_AMT", i, ownAmt);
            result.setData("TOT_AMT", i, totAmt);
            result.addData("OWN_RATE", ownRate);
            TParm maxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
                    mParm.getValue("CASE_NO", 0));
//            System.out.println("取得最大账务序号|||||||||||||"+maxCaseNoSeq);
            if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
                result.addData("CASE_NO_SEQ", 1);
            } else {
                result.addData("CASE_NO_SEQ",
                               (maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1));
            }
            result.addData("SEQ_NO", 1 + i);
        }
//        System.out.println("getIBSOrderData方法出参" + result);
        return result;
    }

    /**
     * 根据外部传入需要增费的数据，取得CASE_NO，ORDER_NO，ORDER_SEQ，取得ODI_DSPNM中的IBS_CASE_NO_SEQ,IBS_SEQ_NO
     * 再根据CASE_NO，CASE_NO_SEQ，ORDER_CODE查得IBS_ORDD中的OWN_PRICE，NHI_PRICE，REXP_CODE，HEXP_CODE
     * @param parm TParm
     * @return TParm
     */
    public TParm getIbsHistoryParam(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            return null;
        }
        String caseNo = parm.getValue("CASE_NO");
        String orderNo = parm.getValue("ORDER_NO");
        String orderCode = parm.getValue("ORDER_CODE");
        int orderSeq = TypeTool.getInt(parm.getData("ORDER_SEQ"));
        if (caseNo == null || "".equalsIgnoreCase(caseNo)) {
            return null;
        }
        if (orderNo == null || "".equalsIgnoreCase(orderNo)) {
            return null;
        }
        if (orderSeq < 0) {
            return null;
        }
        int ibsCaseNoSeq = TypeTool.getInt(parm.getData("IBS_CASE_NO_SEQ"));
        int ibsSeqNo = TypeTool.getInt(parm.getData("IBS_SEQ_NO"));
        if (ibsCaseNoSeq <= 0) {
            String sql =
                    "SELECT IBS_CASE_NO_SEQ,IBS_SEQ_NO " +
                    "	FROM ODI_DSPNM " +
                    "	WHERE CASE_NO='" + caseNo + "' " +
                    "		  AND ORDER_NO='" + orderNo + "' " +
                    "		  AND ORDER_SEQ=" + orderSeq +
                    "		  AND START_DTTM='" + parm.getValue("START_DTTM") + "'";
//            System.out.println("sql-=-=-----" + sql);
            TParm dspnM = new TParm(TJDODBTool.getInstance().select(sql));
            if (dspnM.getErrCode() != 0) {
//                System.out.println("getIbsHistoryParam.dspnM.sql=" +
//                                   dspnM.getErrText());
                return null;
            }
            ibsCaseNoSeq = TypeTool.getInt(dspnM.getData("IBS_CASE_NO_SEQ", 0));
            ibsSeqNo = TypeTool.getInt(dspnM.getData("IBS_SEQ_NO", 0));
            if (ibsCaseNoSeq < 0) {
                return null;
            }
        }
        String ibsSql =
                "SELECT OWN_PRICE,NHI_PRICE,REXP_CODE,HEXP_CODE " +
                "		FROM IBS_ORDD " +
                "		WHERE CASE_NO='" + caseNo + "' " +
                "			  AND CASE_NO_SEQ= " + ibsCaseNoSeq +
                "			  AND SEQ_NO= " + ibsSeqNo +
                "			  AND ORDER_CODE='" + orderCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(ibsSql));

        return result;
    }

    /**
     * 删除
     * @param parm TParm
     * @return TParm CASE_NO,CASE_NO_SEQ
     */
    public TParm deletedata(TParm parm) {
        TParm result = new TParm();
        result = update("deletedata", parm);
        return result;
    }

    /**
     * 删除费用信息(For 批次)
     * @param caseNo String
     * @param billDate String
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteOrderForPatch(String caseNo, String billDate,
                                     TConnection connection) {
        TParm result = new TParm();
        TParm caseNoSeqParm = IBSOrdermTool.getInstance().selCaseNoSeqForPatchSum(
                caseNo, billDate);
        //修改IBS_ORDM表删除数据IBS_ORDD表没有删除数据问题=====pangben 2015-11-9
        result = IBSOrdermTool.getInstance().deleteOrderMPatch(caseNo, billDate,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //=====pangben 2015-11-9添加查询多条CASE_NO_SEQ数据
        String caseNoSeq="";
        for (int i = 0; i < caseNoSeqParm.getCount("CASE_NO_SEQ"); i++) {
        	caseNoSeq+=""+caseNoSeqParm.getValue("CASE_NO_SEQ",i)+",";
		}
		if (caseNoSeq.length() > 0) {
			caseNoSeq = caseNoSeq.substring(0, caseNoSeq.lastIndexOf(","));
			result = IBSOrderdTool.getInstance().deleteOrderDPatchSum(caseNo,
					billDate, caseNoSeq, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			// 查询批次待删除数据
			TParm selDelOrder = IBSOrderdTool.getInstance()
					.seldelOrderDPatchSum(caseNo, billDate, caseNoSeq);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			result.setData("PATCH_AMT", selDelOrder.getDouble("TOT_AMT", 0));
		}else{
			result.setData("PATCH_AMT",0.00);
		}
       
        return result;
    }
    /**
     * 作废账单
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertBillReturn(TParm parm, TConnection connection) {
        //System.out.println("作废账单"+parm);
        TParm result = new TParm();
        TParm actionParm = parm.getParm("DATA");
        String billNo = actionParm.getValue("BILL_NO");
        String mrNo = actionParm.getValue("MR_NO");
        String ipdNo = actionParm.getValue("IPD_NO");
        String caseNo = actionParm.getValue("CASE_NO");
        String optUser = actionParm.getValue("OPT_USER");
        String optTerm = actionParm.getValue("OPT_TERM");
        TParm selBillmParm = new TParm();
        selBillmParm.setData("CASE_NO", caseNo);
        selBillmParm.setData("BILL_NO", billNo);
        //账单主档数据
        TParm selBillm = IBSBillmTool.getInstance().selBillData(
                selBillmParm);
        //账单流水号
        String newBillNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "BILL_NO", "BILL_NO");
        TParm insertBillmParm = new TParm();
        TParm selMaxSeqParm = new TParm();
        selMaxSeqParm.setData("CASE_NO", caseNo);
        selMaxSeqParm.setData("BILL_NO", newBillNo);
        TParm selMaxSeq = IBSBillmTool.getInstance().selMaxSeq(selMaxSeqParm);
        int maxBillSeq = selMaxSeq.getInt("BILL_SEQ", 0);
        //插入IBS_BILLM表中数据
        insertBillmParm.setData("BILL_SEQ", maxBillSeq + 1);
        insertBillmParm.setData("BILL_NO", newBillNo);
        insertBillmParm.setData("CASE_NO", selBillm.getData("CASE_NO", 0));
        insertBillmParm.setData("IPD_NO", selBillm.getData("IPD_NO", 0));
        insertBillmParm.setData("MR_NO", selBillm.getData("MR_NO", 0));
        //caowl 20130411 start
        // 得到系统时间       
		Timestamp sysDate = SystemTool.getInstance().getDate();
        insertBillmParm.setData("BILL_DATE", sysDate);
        //caowl 20130411 end
        //============pangben 2011-11-17 修改作废账单
        if (null != parm.getValue("RETURN_FLG") &&
            parm.getValue("RETURN_FLG").equals("Y"))
            insertBillmParm.setData("REFUND_FLG", "Y");
        else
            insertBillmParm.setData("REFUND_FLG", "N");
        insertBillmParm.setData("REFUND_BILL_NO",
                                selBillm.getData("REFUND_BILL_NO", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REFUND_BILL_NO", 0));
        insertBillmParm.setData("RECEIPT_NO", selBillm.getData("RECEIPT_NO", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("RECEIPT_NO", 0)
                );
        insertBillmParm.setData("CHARGE_DATE",  new TNull(Timestamp.class));
        
        insertBillmParm.setData("CTZ1_CODE",
                                selBillm.getData("CTZ1_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CTZ1_CODE", 0));
        insertBillmParm.setData("CTZ2_CODE",
                                selBillm.getData("CTZ2_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CTZ2_CODE", 0));
        insertBillmParm.setData("CTZ3_CODE",
                                selBillm.getData("CTZ3_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CTZ3_CODE", 0));
        insertBillmParm.setData("BEGIN_DATE", selBillm.getData("BEGIN_DATE", 0));
        insertBillmParm.setData("END_DATE", selBillm.getData("END_DATE", 0));
        insertBillmParm.setData("DISCHARGE_FLG",
                                selBillm.getData("DISCHARGE_FLG", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("DISCHARGE_FLG", 0));
        insertBillmParm.setData("DEPT_CODE", selBillm.getData("DEPT_CODE", 0));
        insertBillmParm.setData("STATION_CODE",
                                selBillm.getData("STATION_CODE", 0));
        TParm stationInfo = SYSStationTool.getInstance().selStationRegion(
                selBillm.getValue("STATION_CODE", 0));
        insertBillmParm.setData("REGION_CODE",
                                stationInfo.getData("REGION_CODE", 0));
        insertBillmParm.setData("BED_NO", selBillm.getData("BED_NO", 0));
        insertBillmParm.setData("OWN_AMT", -selBillm.getDouble("OWN_AMT", 0));
        insertBillmParm.setData("NHI_AMT", -selBillm.getDouble("NHI_AMT", 0));
        insertBillmParm.setData("APPROVE_FLG", "N");
        insertBillmParm.setData("REDUCE_REASON",
                                selBillm.getData("REDUCE_REASON", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REDUCE_REASON", 0));
        insertBillmParm.setData("REDUCE_AMT", selBillm.getData("REDUCE_AMT", 0));
        insertBillmParm.setData("REDUCE_DATE",
                                selBillm.getData("REDUCE_DATE", 0) == null ?
                                new TNull(Timestamp.class) :
                                selBillm.getData("REDUCE_DATE", 0));
        insertBillmParm.setData("REDUCE_DEPT_CODE",
                                selBillm.getData("REDUCE_DEPT_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REDUCE_DEPT_CODE", 0));
        insertBillmParm.setData("REDUCE_RESPOND",
                                selBillm.getData("REDUCE_RESPOND", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REDUCE_RESPOND", 0));
        insertBillmParm.setData("AR_AMT", -selBillm.getDouble("AR_AMT", 0));
        insertBillmParm.setData("PAY_AR_AMT",
                                selBillm.getDouble("PAY_AR_AMT", 0));
        insertBillmParm.setData("CANDEBT_CODE",
                                selBillm.getData("CANDEBT_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CANDEBT_CODE", 0));
        insertBillmParm.setData("CANDEBT_PERSON",
                                selBillm.getData("CANDEBT_PERSON", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("CANDEBT_PERSON", 0));
        insertBillmParm.setData("REFUND_CODE",
                                selBillm.getData("REFUND_CODE", 0) == null ?
                                new TNull(String.class) :
                                selBillm.getData("REFUND_CODE", 0));
        insertBillmParm.setData("REFUND_DATE",
                                selBillm.getData("REFUND_DATE", 0) == null ?
                                new TNull(Timestamp.class) :
                                selBillm.getData("REFUND_DATE", 0));
        insertBillmParm.setData("OPT_USER", optUser);
        insertBillmParm.setData("OPT_TERM", optTerm);
        result = IBSBillmTool.getInstance().insertdata(insertBillmParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //更新账单主档
        TParm updateBillmParm = new TParm();
        updateBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", 0));
        updateBillmParm.setData("REFUND_BILL_NO", newBillNo);
        updateBillmParm.setData("REFUND_FLG", "Y");
        updateBillmParm.setData("REFUND_CODE", optUser);
        result = IBSBillmTool.getInstance().updataDate(updateBillmParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //账单明细档数据
        TParm selBilldParm = new TParm();
        selBilldParm.setData("BILL_NO", selBillm.getData("BILL_NO", 0));
        selBilldParm.setData("BILL_SEQ", selBillm.getData("BILL_SEQ", 0));
        TParm selBliid = IBSBilldTool.getInstance().selectAllData(selBilldParm);
        //更新账单明细档
        TParm updateBliidParm = new TParm();
        updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", 0));
        updateBliidParm.setData("REFUND_BILL_NO", newBillNo);
        updateBliidParm.setData("REFUND_CODE", optUser);
        result = IBSBilldTool.getInstance().updataDate(updateBliidParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }

        int billDCount = selBliid.getCount("BILL_NO");
        TParm insertBilldParm = new TParm();
        for (int i = 0; i < billDCount; i++) {
            insertBilldParm.setData("BILL_NO", newBillNo);
            insertBilldParm.setData("BILL_SEQ",
                                    maxBillSeq + 1);
            insertBilldParm.setData("REXP_CODE",
                                    selBliid.getData("REXP_CODE", i));
            insertBilldParm.setData("OWN_AMT", -selBliid.getDouble("OWN_AMT", i));
            insertBilldParm.setData("AR_AMT", -selBliid.getDouble("AR_AMT", i));
            insertBilldParm.setData("PAY_AR_AMT",
                                    selBliid.getDouble("PAY_AR_AMT", i));
            insertBilldParm.setData("REFUND_BILL_NO",
                                    selBillm.getData("REFUND_BILL_NO", 0) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REFUND_BILL_NO", 0));
            insertBilldParm.setData("REFUND_FLG", "Y");
            insertBilldParm.setData("REFUND_CODE", optUser);
            insertBilldParm.setData("REFUND_DATE",
                                    selBillm.getData("REFUND_DATE", 0) == null ?
                                    new TNull(Timestamp.class) :
                                    selBillm.getData("REFUND_DATE", 0));
            insertBilldParm.setData("OPT_USER", optUser);
            insertBilldParm.setData("OPT_TERM", optTerm);
            result = IBSBilldTool.getInstance().insertdata(insertBilldParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        //费用主档数据
        TParm selOrderm = new TParm();
        selOrderm.setData("BILL_NO", billNo);
        TParm ordermParm = IBSOrdermTool.getInstance().selBillReturnM(selOrderm);
//        System.out.println("费用主档数据"+ordermParm);
        if (ordermParm.getErrCode() < 0) {
            err(ordermParm.getErrName() + " " + ordermParm.getErrText());
            return ordermParm;
        } 
        TParm selMaxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
                caseNo);
        int caseNoSeq = selMaxCaseNoSeq.getInt("CASE_NO_SEQ", 0);
        String caseNoM = "";
        int caseNoSeqM = 0;
        TParm orderMOne = new TParm();
        int mCount = ordermParm.getCount("BILL_NO");
        for (int i = 0; i < mCount; i++) {
            orderMOne = ordermParm.getRow(i);
            caseNoM = orderMOne.getValue("CASE_NO");
            caseNoSeqM = orderMOne.getInt("CASE_NO_SEQ");
            orderMOne.setData("CASE_NO_SEQ", caseNoSeq + 1);
            //插入费用主档
            orderMOne.setData("BILL_DATE", orderMOne.getTimestamp("BILL_DATE") == null ?
                    new TNull(Timestamp.class) :
                    	orderMOne.getTimestamp("BILL_DATE"));//===zhangp 20121109
            orderMOne.setData("REGION_CODE",
                              stationInfo.getData("REGION_CODE", 0));
            result = IBSOrdermTool.getInstance().insertdata(orderMOne,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            orderMOne.setData("CASE_NO_SEQ", caseNoSeq + 2);
            orderMOne.setData("BILL_NO", new TNull(String.class));
            //插入费用主档
            result = IBSOrdermTool.getInstance().insertdata(orderMOne,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            //费用明细档数据
            TParm selOrderd = new TParm();
            selOrderd.setData("CASE_NO", caseNoM);
            selOrderd.setData("CASE_NO_SEQ", caseNoSeqM);
            TParm orderdParm = IBSOrderdTool.getInstance().selBillReturnD(
                    selOrderd);
//            System.out.println("费用明细档数据"+orderdParm);
            if (orderdParm.getErrCode() < 0) {
                err(orderdParm.getErrName() + " " + orderdParm.getErrText());
                return orderdParm;
            }
            int countD = orderdParm.getCount("BILL_NO");
            for (int j = 0; j < countD; j++) {
                TParm orderDOne = orderdParm.getRow(j);
                //===========pangben modify 20110630 start
                orderDOne.setData("BEGIN_DATE",
                                  orderDOne.getTimestamp("BEGIN_DATE") == null ?
                                  new TNull(Timestamp.class) :
                                  orderDOne.getTimestamp("BEGIN_DATE"));
                orderDOne.setData("END_DATE",
                                  orderDOne.getTimestamp("END_DATE") == null ?
                                  new TNull(Timestamp.class) :
                                  orderDOne.getTimestamp("END_DATE"));
                //===========pangben modify 20110630 stop
                String orderCode = orderDOne.getData("ORDER_CODE") == null ? "" :
                                   orderDOne.getValue("ORDER_CODE");
                TParm sysFeeInfo = SYSFeeTool.getInstance().getFeeAllData(
                        orderCode);
                orderDOne.setData("ORDER_CHN_DESC",
                                  sysFeeInfo.getData("ORDER_DESC", 0) == null ?
                                  new TNull(String.class) :
                                  sysFeeInfo.getData("ORDER_DESC", 0));

                orderDOne.setData("CASE_NO_SEQ", caseNoSeq + 2);
                orderDOne.setData("BILL_DATE", orderDOne.getTimestamp("BILL_DATE") == null ?
                        new TNull(Timestamp.class) :
                            orderDOne.getTimestamp("BILL_DATE"));//===zhangp 20121109
                orderDOne.setData("DOSE_CODE",
                                  orderDOne.getData("DOSE_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("DOSE_CODE"));
                //=====pangben 2015-9-17 添加校验为空字段
                orderDOne.setData("ORDER_NO",
                        orderDOne.getData("ORDER_NO") == null ?
                        new TNull(String.class) :
                        orderDOne.getData("ORDER_NO"));
                orderDOne.setData("ORDER_SEQ",
                        orderDOne.getData("ORDER_SEQ") == null ?
                        new TNull(String.class) :
                        orderDOne.getData("ORDER_SEQ"));
                //===pangben end
                orderDOne.setData("CAT1_TYPE", orderDOne.getData("CAT1_TYPE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("CAT1_TYPE")
                        );
                orderDOne.setData("EXE_STATION_CODE",
                                  orderDOne.getData("EXE_STATION_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("EXE_STATION_CODE")
                        );
                orderDOne.setData("COST_CENTER_CODE",
                                  DeptTool.getInstance().getCostCenter(
                                          orderDOne.getValue("EXE_DEPT_CODE"),
                                          orderDOne.getValue("EXE_STATION_CODE")) == null ?
                                  new TNull(String.class) :
                                  DeptTool.getInstance().getCostCenter(
                                          orderDOne.getValue("EXE_DEPT_CODE"),
                                          orderDOne.getValue("EXE_STATION_CODE"))
                        );
                orderDOne.setData("REQUEST_FLG",
                                  orderDOne.getData("REQUEST_FLG") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("REQUEST_FLG")
                        );
                orderDOne.setData("INV_CODE", orderDOne.getData("INV_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("INV_CODE")
                        );
                orderDOne.setData("INDV_FLG", orderDOne.getData("INDV_FLG") == null ?
                                  "N" :
                                  orderDOne.getData("INDV_FLG")
                        );
                orderDOne.setData("ORDERSET_CODE",
                                  orderDOne.getData("ORDERSET_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDERSET_CODE")
                        );
                orderDOne.setData("DOSAGE_UNIT",
                                  orderDOne.getData("DOSAGE_UNIT") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("DOSAGE_UNIT")
                        );
                orderDOne.setData("MEDI_UNIT", orderDOne.getData("MEDI_UNIT") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("MEDI_UNIT")
                        );
                orderDOne.setData("ORDER_NO", orderDOne.getData("ORDER_NO") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDER_NO")
                        );
                orderDOne.setData("ORDER_CAT1_CODE",
                                  orderDOne.getData("ORDER_CAT1_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDER_CAT1_CODE")
                        );
                orderDOne.setData("ORDERSET_GROUP_NO",
                                  orderDOne.getData("ORDERSET_GROUP_NO") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("ORDERSET_GROUP_NO")
                        );
                orderDOne.setData("BILL_FLG", orderDOne.getData("BILL_FLG") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("BILL_FLG")
                        );
                orderDOne.setData("REQUEST_NO", orderDOne.getData("REQUEST_NO") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("REQUEST_NO")
                        );
                orderDOne.setData("FREQ_CODE", orderDOne.getData("FREQ_CODE") == null ?
                                  new TNull(String.class) :
                                  orderDOne.getData("FREQ_CODE")
                        );
                orderDOne.setData("COST_AMT", orderDOne.getData("COST_AMT") == null ?
                                  0.00 : orderDOne.getData("COST_AMT"));
                String oldBillno = orderDOne.getValue("BILL_NO");
                orderDOne.setData("BILL_NO", new TNull(String.class));
                //===ZHANGP 20120806 START
				orderDOne
						.setData(
								"DS_FLG",
								orderDOne.getData("DS_FLG") == null ? "N"
										: orderDOne.getData("DS_FLG"));
                //===ZHANGP 20120806 END
                // 插入费用明细主档
                result = IBSOrderdTool.getInstance().insertdata(orderDOne,
                        connection);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
                orderDOne.setData("BILL_NO", oldBillno);
                orderDOne.setData("CASE_NO_SEQ", caseNoSeq + 1);
                orderDOne.setData("TOT_AMT", -orderDOne.getDouble("TOT_AMT"));
                orderDOne.setData("OWN_AMT", -orderDOne.getDouble("OWN_AMT"));
                orderDOne.setData("MEDI_QTY", -orderDOne.getInt("MEDI_QTY"));
                orderDOne.setData("DOSAGE_QTY", -orderDOne.getInt("DOSAGE_QTY"));
                result = IBSOrderdTool.getInstance().insertdata(orderDOne,
                        connection);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }

            }
            caseNoSeq = caseNoSeq + 2;
        }
        return result;
    }
    /**
     * 校验医嘱金额合计和账单金额合计是否相等
     * @param caseNo String
     * @return boolean
     */
    public boolean checkData(String caseNo) {
        TParm result = new TParm();
        String billSql =
            " SELECT SUM (AR_AMT) AS AR_AMT "+
            "   FROM IBS_BILLM "+
            "  WHERE CASE_NO = '"+caseNo+"' ";
        result = new TParm(TJDODBTool.getInstance().select(billSql));
        if (result.getErrCode() < 0) {
            err( -1, result.getErrName());
            return false;
        }
        double arAmt =  result.getDouble("AR_AMT",0);
        String orderSql =
            " SELECT SUM (TOT_AMT) AS TOT_AMT "+
            "   FROM IBS_ORDD "+
            "  WHERE CASE_NO = '"+caseNo+"' ";
        result = new TParm(TJDODBTool.getInstance().select(orderSql));
        if (result.getErrCode() < 0) {
            err( -1, result.getErrName());
            return false;
        }
        double totAmt =  result.getDouble("TOT_AMT",0);
        if(arAmt!=totAmt)
            return false;
        return true;
    }
    /**
     * 批次补执行
     * @return TParm
     */
    public TParm batchFillFee() {
        TParm result = new TParm();
        String mrCaseSql =
                " SELECT MR_NO, CASE_NO, MSEQ, DSEQ " +
                "   FROM (SELECT M.MR_NO, M.CASE_NO, MAX (M.CASE_NO_SEQ) AS MSEQ," +
                "                MAX (D.CASE_NO_SEQ) AS DSEQ " +
                "           FROM IBS_ORDM M, IBS_ORDD D " +
                "          WHERE M.CASE_NO = D.CASE_NO " +
                "       GROUP BY M.MR_NO, M.CASE_NO) AA " +
                "  WHERE AA.MSEQ <> AA.DSEQ "+
                "  ORDER BY CASE_NO ";
        TParm mrCaseParm = new TParm(TJDODBTool.getInstance().select(mrCaseSql));
        int rowMrCase = mrCaseParm.getCount();
        for (int i = 0; i < rowMrCase; i++) {
            String mrNo = "";
            String caseNo = "";
            mrNo = mrCaseParm.getValue("MR_NO",i);
            caseNo = mrCaseParm.getValue("CASE_NO",i);
            String caseNoSeqSql =
                    " SELECT DISTINCT (CASE_NO_SEQ) " +
                    "   FROM IBS_ORDD " +
                    "  WHERE CASE_NO = '" + caseNo + "' " +
                    "    AND CASE_NO_SEQ NOT IN (SELECT CASE_NO_SEQ " +
                    "                              FROM IBS_ORDM " +
                    "                             WHERE MR_NO = '" + mrNo +
                    "') " +
                    "  ORDER BY CASE_NO_SEQ ";
            TParm caseNoSeqParm = new TParm(TJDODBTool.getInstance().select(caseNoSeqSql));
            int caseNoSeqCount = caseNoSeqParm.getCount();
            for(int j=0;j<caseNoSeqCount;j++){
                int caseNoSeq =caseNoSeqParm.getInt("CASE_NO_SEQ",j);
                String insertData =
                        " SELECT A.CASE_NO, A.CASE_NO_SEQ, A.BILL_DATE, B.IPD_NO, B.MR_NO,"+
                        "        A.DEPT_CODE, A.STATION_CODE, B.BED_NO, '0' AS DATA_TYPE, A.BILL_NO,"+
                        "        A.OPT_USER, A.OPT_DATE, A.OPT_TERM, 'H01' AS REGION_CODE,"+
                        "   FROM IBS_ORDD A, ADM_INP B "+
                        "  WHERE A.CASE_NO = B.CASE_NO "+
                        "    AND A.CASE_NO = '"+caseNo+"' "+
                        "    AND A.CASE_NO_SEQ = "+caseNoSeq+" "+
                        "  ORDER BY OPT_DATE ";
                TParm insertDataParm = new TParm(TJDODBTool.getInstance().select(insertData));
                String insertMSql =
                        ""+
                        ""+
                        "";

            }
        }

        return result;

    }
    /**
	 * 修改身份
	 * 
	 * @author caowl
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updBill(TParm parm, TConnection connection) {

		
		TParm result = new TParm();

		// 得到系统时间
		Timestamp sysDate = SystemTool.getInstance().getDate();

		// 得到界面传过来的参数
		String caseNo = parm.getValue("CASE_NO");
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		//新的身份
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");

		// 根据caseNo查询病人信息
		TParm selPatientParm = new TParm();
		selPatientParm = selBycaseNo(caseNo);
		
		//更新ADM_INP修改身份
		String updSql = "UPDATE ADM_INP SET CTZ1_CODE = '"+CTZ1+"' ,CTZ2_CODE =  '"+CTZ2+"',CTZ3_CODE = '"+CTZ3+"' ,OPT_USER = '"+optUser+"', OPT_DATE = SYSDATE ,OPT_TERM = '"+optTerm+"',BILL_DATE = SYSDATE  WHERE CASE_NO = '"+caseNo+"'";
		
		result = new TParm(TJDODBTool.getInstance().update(updSql));
		// 设置查询条件--根据case_no查询主张单信息
		TParm selBillmParm = new TParm();
		selBillmParm.setData("CASE_NO", caseNo);		
		
		// 账单主档数据--根据case_no查询作废账单的全部数据
		//System.out.println("查询账单的条件：：："+selBillmParm);
		TParm selBillm = IBSBillmTool.getInstance().selByCase_no(selBillmParm);
		//System.out.println("查询出来的账单数据："+selBillm);
		int billmcount = selBillm.getCount();// 账单主档的数据数目
	
//		//更新账单信息，如果没有的话，保持原样
//		//System.out.println("更新账单信息"+result.getErrCode());
//		if(result.getErrCode() == 0){
//			//System.out.println("账单数："+billmcount);
//			if(billmcount>0){
//			// 更新账单主档
//			TParm updateBillmParm = new TParm();
//			updateBillmParm.setData("CASE_NO", caseNo);
//			updateBillmParm.setData("REFUND_FLG", "Y");
//			
//			updateBillmParm.setData("REFUND_CODE",optUser);
//
//			result = IBSBillmTool.getInstance().updataDateforCTZ(updateBillmParm,
//					connection);
//
//			//System.out.println("更新主张单的结果为："+result.getErrText());
//			if (result.getErrCode() < 0) {
//				err(result.getErrName() + " " + result.getErrText());
//				return result;
//			}
//			if(result.getErrCode() == 0){
//				// 更新账单明细档
//				for (int i = 0; i < billmcount; i++) {
//
//					TParm updateBliidParm = new TParm();
//					updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));					
//					updateBliidParm.setData("REFUND_CODE",optUser);
//					result = IBSBilldTool.getInstance().updataDateforCTZ(
//							updateBliidParm, connection);
//                    System.out.println("更新明细账结果："+result.getErrCode());
//					if (result.getErrCode() < 0) {
//						err(result.getErrName() + " " + result.getErrText());
//						return result;
//					}
//			}
//			}
//			}
//			
//		}else{
//			return result;
//		}	
		
		if(result.getErrCode() ==0){
			//System.out.println("插入正负数据");
		for (int i = 0; i < billmcount; i++) {
			
 
			// 账单流水号--根据取号原则得到账单号和账单序号
			String newBillNo = SystemTool.getInstance().getNo("ALL", "IBS",
					"BILL_NO", "BILL_NO");
			String newBillNo1 = SystemTool.getInstance().getNo("ALL", "IBS",
					"BILL_NO", "BILL_NO");
			
			TParm selMaxSeqParm = new TParm();


			// 更新账单主档
			TParm updateBillmParm = new TParm();
			updateBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
			updateBillmParm.setData("REFUND_FLG", "Y");
			updateBillmParm.setData("REFUND_BILL_NO",newBillNo);
			updateBillmParm.setData("REFUND_CODE",optUser);

			result = IBSBillmTool.getInstance().updataDateforCTZ(updateBillmParm,
					connection);

			//System.out.println("更新主张单的结果为："+result.getErrText());
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
			
			// 插入IBS_BILLM表中负数据
			TParm insertBillmNegativeParm = new TParm();
			insertBillmNegativeParm.setData("BILL_SEQ", 1);
			insertBillmNegativeParm.setData("BILL_NO", newBillNo);
			insertBillmNegativeParm.setData("CASE_NO",
					selBillm.getData("CASE_NO", i));
			insertBillmNegativeParm.setData("IPD_NO",
					selBillm.getData("IPD_NO", i));
			insertBillmNegativeParm.setData("MR_NO",
					selBillm.getData("MR_NO", i));
			insertBillmNegativeParm.setData("BILL_DATE", sysDate);
			insertBillmNegativeParm.setData("REFUND_FLG", "Y");//caowl 20130419

			insertBillmNegativeParm.setData("REFUND_BILL_NO","");//caowl 20130419

			insertBillmNegativeParm.setData("RECEIPT_NO", selBillm.getData(
					"RECEIPT_NO", i) == null ? new TNull(String.class)
					: selBillm.getData("RECEIPT_NO", i));

			insertBillmNegativeParm.setData("CHARGE_DATE", selBillm.getData(
					"CHARGE_DATE", i) == null ? new TNull(String.class)
					: selBillm.getData("CHARGE_DATE", i));

			insertBillmNegativeParm.setData("CTZ1_CODE", selBillm.getData(
					"CTZ1_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CTZ1_CODE", i));
			insertBillmNegativeParm.setData("CTZ2_CODE", selBillm.getData(
					"CTZ2_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CTZ2_CODE", i));
			insertBillmNegativeParm.setData("CTZ3_CODE", selBillm.getData(
					"CTZ3_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CTZ3_CODE", i));
			insertBillmNegativeParm.setData("BEGIN_DATE",
					selBillm.getData("BEGIN_DATE", i));
			insertBillmNegativeParm.setData("END_DATE",
					selBillm.getData("END_DATE", i));
			insertBillmNegativeParm.setData("DISCHARGE_FLG", selBillm.getData(
					"DISCHARGE_FLG", i) == null ? new TNull(String.class)
					: selBillm.getData("DISCHARGE_FLG", i));

			insertBillmNegativeParm.setData("DEPT_CODE",
					selBillm.getData("DEPT_CODE", i));
			insertBillmNegativeParm.setData("STATION_CODE",
					selBillm.getData("STATION_CODE", i));
			insertBillmNegativeParm.setData("REGION_CODE",
					selBillm.getData("REGION_CODE", i));
			insertBillmNegativeParm.setData("BED_NO",
					selBillm.getData("BED_NO", i));
			insertBillmNegativeParm.setData("OWN_AMT",
					-selBillm.getDouble("OWN_AMT", i));
			insertBillmNegativeParm.setData("NHI_AMT",
					-selBillm.getDouble("NHI_AMT", i));
			insertBillmNegativeParm.setData("APPROVE_FLG", "N");
			insertBillmNegativeParm.setData("REDUCE_REASON", selBillm.getData(
					"REDUCE_REASON", i) == null ? new TNull(String.class)
					: selBillm.getData("REDUCE_REASON", i));
			insertBillmNegativeParm.setData("REDUCE_AMT",
					selBillm.getData("REDUCE_AMT", i));
			insertBillmNegativeParm.setData("REDUCE_DATE", selBillm.getData(
					"REDUCE_DATE", i) == null ? new TNull(Timestamp.class)
					: selBillm.getData("REDUCE_DATE", i));
			insertBillmNegativeParm.setData("REDUCE_DEPT_CODE", selBillm
					.getData("REDUCE_DEPT_CODE", i) == null ? new TNull(
					String.class) : selBillm.getData("REDUCE_DEPT_CODE", i));
			insertBillmNegativeParm.setData("REDUCE_RESPOND", selBillm.getData(
					"REDUCE_RESPOND", i) == null ? new TNull(String.class)
					: selBillm.getData("REDUCE_RESPOND", i));
			insertBillmNegativeParm.setData("AR_AMT",
					-selBillm.getDouble("AR_AMT", i));
			insertBillmNegativeParm.setData("PAY_AR_AMT",
					selBillm.getDouble("PAY_AR_AMT", i));
			insertBillmNegativeParm.setData("CANDEBT_CODE", selBillm.getData(
					"CANDEBT_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CANDEBT_CODE", i));
			insertBillmNegativeParm.setData("CANDEBT_PERSON", selBillm.getData(
					"CANDEBT_PERSON", i) == null ? new TNull(String.class)
					: selBillm.getData("CANDEBT_PERSON", i));
			insertBillmNegativeParm.setData("REFUND_CODE", selBillm.getData(
					"REFUND_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("REFUND_CODE", i));
			insertBillmNegativeParm.setData("REFUND_DATE", selBillm.getData(
					"REFUND_DATE", i) == null ? new TNull(Timestamp.class)
					: selBillm.getData("REFUND_DATE", i));
			insertBillmNegativeParm.setData("OPT_USER", optUser);
			insertBillmNegativeParm.setData("OPT_TERM", optTerm);

			result = IBSBillmTool.getInstance().insertdata(
					insertBillmNegativeParm, connection);

			
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}

			// 插入IBS_BILLM表中正数据
			
			TParm insertBillmPositiveParm = new TParm();

			insertBillmPositiveParm.setData("BILL_SEQ", 1);
			insertBillmPositiveParm.setData("BILL_NO", newBillNo1);
			insertBillmPositiveParm.setData("CASE_NO",
					selBillm.getData("CASE_NO", i));
			insertBillmPositiveParm.setData("IPD_NO",
					selBillm.getData("IPD_NO", i));
			insertBillmPositiveParm.setData("MR_NO",
					selBillm.getData("MR_NO", i));
			insertBillmPositiveParm.setData("BILL_DATE", sysDate);
			insertBillmPositiveParm.setData("REFUND_FLG", "N");
			insertBillmPositiveParm.setData("REFUND_BILL_NO", selBillm.getData(
					"REFUND_BILL_NO", i) == null ? new TNull(String.class)
					: selBillm.getData("REFUND_BILL_NO", i));
			insertBillmPositiveParm.setData("RECEIPT_NO", selBillm.getData(
					"RECEIPT_NO", i) == null ? new TNull(String.class)
					: selBillm.getData("RECEIPT_NO", i));
			insertBillmPositiveParm.setData("CHARGE_DATE", selBillm.getData(
					"CHARGE_DATE", i) == null ? new TNull(String.class)
					: selBillm.getData("CHARGE_DATE", i));
			insertBillmPositiveParm.setData("CTZ1_CODE", CTZ1);
			insertBillmPositiveParm.setData("CTZ2_CODE", CTZ2);
			insertBillmPositiveParm.setData("CTZ3_CODE", CTZ3);
			insertBillmPositiveParm.setData("BEGIN_DATE",
					selBillm.getData("BEGIN_DATE", i));
			insertBillmPositiveParm.setData("END_DATE",
					selBillm.getData("END_DATE", i));
			insertBillmPositiveParm.setData("DISCHARGE_FLG", selBillm.getData(
					"DISCHARGE_FLG", i) == null ? new TNull(String.class)
					: selBillm.getData("DISCHARGE_FLG", i));
			insertBillmPositiveParm.setData("DEPT_CODE",
					selBillm.getData("DEPT_CODE", i));
			insertBillmPositiveParm.setData("STATION_CODE",
					selBillm.getData("STATION_CODE", i));
			insertBillmPositiveParm.setData("REGION_CODE",
					selBillm.getData("REGION_CODE", i));
			insertBillmPositiveParm.setData("BED_NO",
					selBillm.getData("BED_NO", i));
			insertBillmPositiveParm.setData("OWN_AMT",
					selBillm.getDouble("OWN_AMT", i));
			insertBillmPositiveParm.setData("NHI_AMT",
					selBillm.getDouble("NHI_AMT", i));
			insertBillmPositiveParm.setData("APPROVE_FLG", "N");
			insertBillmPositiveParm.setData("REDUCE_REASON", selBillm.getData(
					"REDUCE_REASON", i) == null ? new TNull(String.class)
					: selBillm.getData("REDUCE_REASON", i));
			insertBillmPositiveParm.setData("REDUCE_AMT",
					selBillm.getData("REDUCE_AMT", i));
			insertBillmPositiveParm.setData("REDUCE_DATE", selBillm.getData(
					"REDUCE_DATE", i) == null ? new TNull(Timestamp.class)
					: selBillm.getData("REDUCE_DATE", i));
			insertBillmPositiveParm.setData("REDUCE_DEPT_CODE", selBillm
					.getData("REDUCE_DEPT_CODE", i) == null ? new TNull(
					String.class) : selBillm.getData("REDUCE_DEPT_CODE", i));
			insertBillmPositiveParm.setData("REDUCE_RESPOND", selBillm.getData(
					"REDUCE_RESPOND", i) == null ? new TNull(String.class)
					: selBillm.getData("REDUCE_RESPOND", i));
			insertBillmPositiveParm.setData("AR_AMT",
					selBillm.getDouble("AR_AMT", i));
			insertBillmPositiveParm.setData("PAY_AR_AMT",
					selBillm.getDouble("PAY_AR_AMT", i));
			insertBillmPositiveParm.setData("CANDEBT_CODE", selBillm.getData(
					"CANDEBT_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("CANDEBT_CODE", i));
			insertBillmPositiveParm.setData("CANDEBT_PERSON", selBillm.getData(
					"CANDEBT_PERSON", i) == null ? new TNull(String.class)
					: selBillm.getData("CANDEBT_PERSON", i));
			insertBillmPositiveParm.setData("REFUND_CODE", selBillm.getData(
					"REFUND_CODE", i) == null ? new TNull(String.class)
					: selBillm.getData("REFUND_CODE", i));
			insertBillmPositiveParm.setData("REFUND_DATE", selBillm.getData(
					"REFUND_DATE", i) == null ? new TNull(Timestamp.class)
					: selBillm.getData("REFUND_DATE", i));
			insertBillmPositiveParm.setData("OPT_USER", optUser);
			insertBillmPositiveParm.setData("OPT_TERM", optTerm);
			result = IBSBillmTool.getInstance().insertdata(
					insertBillmPositiveParm, connection);
			//System.out.println("IBSTool中IBS_BILLM正数据结果为：" + result);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}

			// 查询账单明细档数据
			TParm selBilldParm = new TParm();
			selBilldParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));
			selBilldParm.setData("BILL_SEQ", selBillm.getData("BILL_SEQ", i));
			TParm selBliid = IBSBilldTool.getInstance().selectAllData(
					selBilldParm);
			int billDCount = selBliid.getCount("BILL_NO");
			TParm insertBilldNegativeParm = new TParm();
			TParm insertBilldPositiveParm = new TParm();
			for (int j = 0; j < billDCount; j++) {
				
				TParm updateBliidParm = new TParm();
				updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", i));					
				updateBliidParm.setData("REFUND_CODE",optUser);
				updateBliidParm.setData("REFUND_BILL_NO",newBillNo);
				result = IBSBilldTool.getInstance().updataDateforCTZ(
						updateBliidParm, connection);
                //System.out.println("更新明细账结果："+result.getErrCode());
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				// 插入账单明细档负数据
				insertBilldNegativeParm.setData("BILL_NO", newBillNo);
				insertBilldNegativeParm.setData("BILL_SEQ", 1);
				insertBilldNegativeParm.setData("REXP_CODE",
						selBliid.getData("REXP_CODE", j));
				insertBilldNegativeParm.setData("OWN_AMT",
						-selBliid.getDouble("OWN_AMT", j));
				insertBilldNegativeParm.setData("AR_AMT",
						-selBliid.getDouble("AR_AMT", j));
				insertBilldNegativeParm.setData("PAY_AR_AMT",
						selBliid.getDouble("PAY_AR_AMT", j));
				insertBilldNegativeParm.setData("REFUND_BILL_NO", "");
				insertBilldNegativeParm.setData("REFUND_FLG", "Y");
				insertBilldNegativeParm.setData("REFUND_CODE", "");
				insertBilldNegativeParm.setData("REFUND_DATE", "");
				insertBilldNegativeParm.setData("OPT_USER", optUser);
				insertBilldNegativeParm.setData("OPT_TERM", optTerm);
				result = IBSBilldTool.getInstance().insertdata(
						insertBilldNegativeParm, connection);

				
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				// 插入账单明细档正数据
				insertBilldPositiveParm.setData("BILL_NO", newBillNo1);
				insertBilldPositiveParm.setData("BILL_SEQ",1);
				insertBilldPositiveParm.setData("REXP_CODE",
						selBliid.getData("REXP_CODE", j));
				insertBilldPositiveParm.setData("OWN_AMT",
						selBliid.getDouble("OWN_AMT", j));
				insertBilldPositiveParm.setData("AR_AMT",
						selBliid.getDouble("AR_AMT", j));
				insertBilldPositiveParm.setData("PAY_AR_AMT",
						selBliid.getDouble("PAY_AR_AMT", j));
				insertBilldPositiveParm.setData("REFUND_BILL_NO", selBliid
						.getData("REFUND_BILL_NO", j) == null ? new TNull(
						String.class) : selBliid.getData("REFUND_BILL_NO", j));
				insertBilldPositiveParm.setData("REFUND_FLG", "N");
				insertBilldPositiveParm.setData("REFUND_CODE", "");
				insertBilldPositiveParm.setData("REFUND_DATE", "");
				insertBilldPositiveParm.setData("OPT_USER", optUser);
				insertBilldPositiveParm.setData("OPT_TERM", optTerm);
				result = IBSBilldTool.getInstance().insertdata(
						insertBilldPositiveParm, connection);
				
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
				//更新IBS_ORDD表
				String updIBSORDDsql = "UPDATE IBS_ORDD  " +
						" SET BILL_NO = '"+newBillNo1+"' " +
						" WHERE CASE_NO = '"+caseNo+"' AND BILL_NO = '"+selBillm.getData("BILL_NO", i)+"'";
				result = new TParm(TJDODBTool.getInstance().update(updIBSORDDsql));
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " + result.getErrText());
					return result;
				}
			}

		}
		}
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		
		// 插入日志信息
		String mr_no = selPatientParm.getValue("MR_NO").toString();
		if (mr_no != null && mr_no.length() != 0) {
			mr_no = mr_no.substring(1, mr_no.length() - 1);
		} else {
			mr_no = "";
		}

		String ipd_no = selPatientParm.getData("IPD_NO").toString();
		if (ipd_no != null && ipd_no.length() != 0) {
			ipd_no = ipd_no.substring(1, ipd_no.length() - 1);
		} else {
			ipd_no = "";
		}

		String bed_no = selPatientParm.getData("BED_NO").toString();
		if (bed_no != null && bed_no.length() != 0) {
			bed_no = bed_no.substring(1, bed_no.length() - 1);
		} else {
			bed_no = "";
		}

		String region_code = selPatientParm.getData("REGION_CODE").toString();
		if (region_code != null && region_code.length() != 0) {
			region_code = region_code.substring(1, region_code.length() - 1);
		} else {
			region_code = "";
		}

		String ctz1_code = selPatientParm.getData("CTZ1_CODE").toString();
		if (ctz1_code != null && ctz1_code.length() != 0) {
			ctz1_code = ctz1_code.substring(1, ctz1_code.length() - 1);
		} else {
			ctz1_code = "";
		}

		String ctz2_code = selPatientParm.getData("CTZ2_CODE").toString();
		if (ctz2_code != null && ctz2_code.length() != 0) {
			ctz2_code = ctz2_code.substring(1, ctz2_code.length() - 1);
		} else {
			ctz2_code = "";
		}

		String ctz3_code = selPatientParm.getData("CTZ3_CODE").toString();
		if (ctz3_code != null && ctz3_code.length() != 0) {
			ctz3_code = ctz3_code.substring(1, ctz3_code.length() - 1);
		} else {
			ctz3_code = "";
		}

		int seq_no = 1;
		int count = selCountOfCaseno(caseNo);
		seq_no += count;
		String logSql = "INSERT INTO "
				+ " ADM_CTZ_LOG(CASE_NO,SEQ_NO,MR_NO,IPD_NO,BED_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM) "
				+ " VALUES ('"
				+ caseNo
				+ "','"
				+ seq_no
				+ "','"
				+ mr_no
				+ "','"
				+ ipd_no
				+ "','"
				+ bed_no
				+ "','"
				+ region_code
				+ "','"
				+ ctz1_code
				+ "','"
				+ ctz2_code
				+ "','"
				+ ctz3_code
				+ "','"
				+ CTZ1
				+ "','"
				+ CTZ2
				+ "','"
				+ CTZ3
				+ "','"
				+ optUser
				+ "',SYSDATE,'"
				+ optTerm + "')";
		result = new TParm(TJDODBTool.getInstance().update(logSql));
		
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 修改身份 --查询病人信息
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return TParm
	 */
	public TParm selBycaseNo(String caseNo) {
		TParm selParm = new TParm();
		String sql = "SELECT CASE_NO,IPD_NO,MR_NO,REGION_CODE,BED_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "'";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		return selParm;
	}

	/**
	 * 修改身份 --查询日志
	 * 
	 * @author caowl
	 * @param parm
	 *            String
	 * @return int
	 */
	public int selCountOfCaseno(String caseNo) {
		String sql = "SELECT CASE_NO,SEQ_NO,MR_NO,IPD_NO,BED_NO,REGION_CODE,CTZ_CODE1_O,CTZ_CODE2_O,CTZ_CODE3_O,CTZ_CODE1_N,CTZ_CODE2_N,CTZ_CODE3_N,OPT_USER,OPT_DATE,OPT_TERM " +
				" FROM ADM_CTZ_LOG " +
				" WHERE CASE_NO = '" + caseNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() == -1){
			return 0;
		}
		return result.getCount();
	}
	/**
	 * 校验是否存在多个临床路径
	 * @return
	 */
	public TParm onCheckClpDiff(String caseNo){
		TParm clpCParm=new TParm();
		clpCParm.setData("CASE_NO",caseNo);
		TParm clpCheckParm=IBSOrderdTool.getInstance().selClpClncpathCode(clpCParm);
		TParm result=new TParm();
		//存在临床路径
		if (null!=clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)&&clpCheckParm.getValue("CLNCPATH_CODE_SUM",0).length()>0) {
			TParm clpSchdCodeParm=IBSOrderdTool.getInstance().selClpSchdCode(clpCParm);
			String sql="";
			if (clpSchdCodeParm.getCount()>0) {
				if(null!=clpCheckParm.getValue("SCHD_CODE",0)&&
						clpCheckParm.getValue("SCHD_CODE",0).length()>0){
					sql="UPDATE IBS_ORDD SET SCHD_CODE='"+clpCheckParm.getValue("SCHD_CODE",0)+
					"' WHERE CASE_NO='"+caseNo+"' AND SCHD_CODE IS NULL ";
				}else{
					String sqlClp="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)+"' ORDER BY SEQ";
					TParm clpParm = new TParm(TJDODBTool.getInstance().select(sqlClp));
					sql="UPDATE IBS_ORDD SET SCHD_CODE='"+clpParm.getValue("SCHD_CODE",0)+
					"' WHERE CASE_NO='"+caseNo+"' AND SCHD_CODE IS NULL ";
				}
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
			if (clpCheckParm.getCount()>1) {//存在多个路径情况
				sql="UPDATE IBS_ORDD SET CLNCPATH_CODE='"+clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)+
				"' WHERE CASE_NO='"+caseNo+"' AND (CLNCPATH_CODE IS NULL OR CLNCPATH_CODE<>'"+
				clpCheckParm.getValue("CLNCPATH_CODE_SUM",0)+"')";
				result = new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
		return result;
	}
	
	/**
	 * 以账单号分组查询IBS_BILLM总金额
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm selectIbsBillMArAmtGroupByBillNo(TParm parm) {
		TParm result = new TParm();
		String billNo = parm.getValue("BILL_NO");
		if (StringUtils.isEmpty(billNo)) {
			result.setErr(-1, "账单号为空");
			return result;
		}
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT BILL_NO,SUM (AR_AMT) AS AR_AMT ");
		sbSql.append(" FROM IBS_BILLM ");
		sbSql.append(" WHERE BILL_NO IN ('");
		sbSql.append(billNo);
		sbSql.append("') ");
		
		sbSql.append(" GROUP BY BILL_NO ");
		sbSql.append(" ORDER BY BILL_NO ");
		
		result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 以账单号分组查询IBS_BILLD总金额
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm selectIbsBillDArAmtGroupByBillNo(TParm parm) {
		TParm result = new TParm();
		String billNo = parm.getValue("BILL_NO");
		if (StringUtils.isEmpty(billNo)) {
			result.setErr(-1, "账单号为空");
			return result;
		}
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT BILL_NO,SUM (AR_AMT) AS AR_AMT ");
		sbSql.append(" FROM IBS_BILLD ");
		sbSql.append(" WHERE BILL_NO IN ('");
		sbSql.append(billNo);
		sbSql.append("') ");
		
		sbSql.append(" GROUP BY BILL_NO ");
		sbSql.append(" ORDER BY BILL_NO ");
		
		result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			return result;
		}
		return result;
	}
	
	/**
	 * 校验账单主细项金额是否一致
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm checkIbsBillAmount(TParm parm) {
		TParm result = new TParm();
		// 以账单号分组查询IBS_BILLM总金额
		TParm billM = selectIbsBillMArAmtGroupByBillNo(parm);
		// 以账单号分组查询IBS_BILLD总金额
		TParm billD = selectIbsBillDArAmtGroupByBillNo(parm);

		if (billM.getErrCode() < 0 || billD.getErrCode() < 0) {
			result.setErr(-1, "查询账单数据异常");
			return result;
		}

		int countM = billM.getCount();
		int countD = billD.getCount();
		if (countM != countD) {
			result.setErr(-1, "账单主细项个数不一致，请联系信息部");
			return result;
		}

		StringBuffer errMsg = new StringBuffer();
		String billNoM = "";
		double arAmtM = 0;
		boolean existFlg = false;
		for (int i = 0; i < countM; i++) {
			existFlg = false;
			billNoM = billM.getValue("BILL_NO", i);
			arAmtM = billM.getDouble("AR_AMT", i);
			for (int j = 0; j < countD; j++) {
				if (StringUtils.equals(billNoM, billD.getValue("BILL_NO", j))) {
					existFlg = true;
					if (arAmtM != billD.getDouble("AR_AMT", j)) {
						errMsg.append("账单号：" + billNoM + "\r\n");
					}
					break;
				}
			}

			// 遍历账单明细后未找到对应的账单号也给出提示
			if (!existFlg) {
				errMsg.append("账单号：" + billNoM + "\r\n");
			}
		}

		if (errMsg.length() > 0) {
			result.setErr(-1, errMsg.toString() + "账单信息主细不一致，请联系信息部");
			return result;
		}

		return result;
	}
}
