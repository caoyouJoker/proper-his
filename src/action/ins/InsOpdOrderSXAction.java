package action.ins;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Vector;
import com.dongyang.manager.TCM_Transform;
import jdo.ins.INSOpdOrderSXTool;
import java.util.List;
import java.util.Iterator;
import com.dongyang.jdo.TJDODBTool;
import java.util.ArrayList;

/**
 *
 * <p>Title: 门诊医保省医保分割动作类</p>
 *
 * <p>Description: 门诊医保省医保分割动作类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version JavaHis 1.0
 */
public class InsOpdOrderSXAction extends TAction {
    /**
     * 删除数据
     * @param parm TParm
     * @return TParm
     */
    public TParm delInsOpdOrder(TParm parm){
        //        System.out.println("进入分割动作类");
                TConnection connection = getConnection();
                TParm result = new TParm();
                TParm orderParm = new TParm();
                TParm endData = new TParm();
                try {
                    orderParm.setRowData(parm);
                    Vector order = (Vector) orderParm.getData("ORDER");
                    String caseNo = (String) order.get(0); //从3.0批价界面接参
                    int count = order.size();
                    TParm allData = new TParm();
                    TParm addseqParm = new TParm(); //补充计价问题
                    addseqParm.setData("CASE_NO", caseNo); //补充计价问题
                    int maxSeq = TCM_Transform.getInt(INSOpdOrderSXTool.getInstance().
                                                      getAddOrderSeq(addseqParm)) == 0 ?
                                 1 :
                                 TCM_Transform.getInt(INSOpdOrderSXTool.getInstance().
                                                      getAddOrderSeq(addseqParm));
                    for (int i = 1; i < count; i++) {
                        String[] opdOrder = (String[]) order.get(i);
                        String orderCode = opdOrder[0]; //从3.0批价界面接参
                        String orderNo = opdOrder[1] == null ||
                                         opdOrder[1].equals("null") ||
                                         opdOrder[1].trim().length() == 0 ?
                                         "X00000000000" :
                                         opdOrder[1]; //从3.0批价界面接参
                        String orderSeq = opdOrder[2];
                        if (opdOrder[1] == null || opdOrder[1].equals("null") ||
                            opdOrder[1].trim().length() == 0)
                            orderSeq = String.valueOf(maxSeq);
                        String orderTime = opdOrder[3];
                        String ordersetCode = opdOrder[4];
                        String ordersetGroupNo = opdOrder[5];
                        String deptCode = opdOrder[6];
                        String drCode = opdOrder[7];
                        String rborderDeptCode = opdOrder[8];
                        String doseCode = opdOrder[9];
                        String ownPrice = opdOrder[10];
                        String totQty = opdOrder[11];
                        String totAmt = opdOrder[12];
                        //交易号(新加)
                        String bkc023 = opdOrder[13];
                        int orderSeq1 = TCM_Transform.getInt(orderSeq);
                        allData.setData("CASE_NO", caseNo);
                        allData.setData("ORDER_CODE", orderCode);
                        allData.setData("ORDER_NO", orderNo);
                        allData.setData("ORDER_SEQ", orderSeq1);
                        //2008.11.24 处理增付---------------start----------------
                        int ordersetGroupNo1 = TCM_Transform.getInt(ordersetGroupNo);
                        double ownPrice1 = TCM_Transform.getDouble(ownPrice);
                        double totQty1 = TCM_Transform.getDouble(totQty);
                        double totAmt1 = TCM_Transform.getDouble(totAmt);
                        String rborderDeptCode1 = rborderDeptCode == null ||
                                                  rborderDeptCode.equals("null") ? "" :
                                                  TCM_Transform.getString(
                                                          rborderDeptCode);
                        allData.setData("BILL_DATE", orderTime.substring(0, 8));
                        allData.setData("ORDERSET_CODE", ordersetCode);
                        allData.setData("ORDER_GROUP_NO", ordersetGroupNo1);
                        allData.setData("DEPT_CODE", deptCode);
                        allData.setData("DR_CODE", drCode);
                        allData.setData("EXE_DEPT_CODE", rborderDeptCode1);
                        allData.setData("DOSE_CODE", doseCode);
                        allData.setData("PRICE", ownPrice1);
                        allData.setData("QTY", totQty1);
                        allData.setData("TOT_AMT", totAmt1);
                        //交易号
                        allData.setData("BKC023", bkc023);
                        //2008.11.24 处理增付---------------end----------------
                        allData.setData("OPT_USER", parm.getData("OPT_USER"));
                        allData.setData("OPT_TERM", parm.getData("OPT_TERM"));
                        endData = INSOpdOrderSXTool.getInstance().deldata(allData,
                                connection);
                        if (endData.getErrCode() < 0) {
                            System.out.println("insert->" + endData.getErrCode());
                            break;
                        }
                        maxSeq += 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (endData.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return endData;
                }
                connection.commit();
                connection.close();
                TParm returnParm = new TParm();
                returnParm.setData("CASE_NO", parm.getData("CASE_NO"));
                result = INSOpdOrderSXTool.getInstance().selectdata(returnParm);
                return result;

    }
    /**
     * 分割主档数据
     * @param parm TParm
     * @return TParm
     */
    public TParm saveInsOpdOrder(TParm parm) {
//        System.out.println("进入分割动作类");
        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm orderParm = new TParm();
        TParm endData = new TParm();
        List jyNo = new ArrayList();
        orderParm.setRowData(parm);
        Vector order = (Vector) orderParm.getData("ORDER");
        String caseNo = (String) order.get(0); //从3.0批价界面接参
        try {
            int count = order.size();
            TParm allData = new TParm();
            TParm addseqParm = new TParm(); //补充计价问题
            addseqParm.setData("CASE_NO", caseNo); //补充计价问题
            int maxSeq = TCM_Transform.getInt(INSOpdOrderSXTool.getInstance().
                                              getAddOrderSeq(addseqParm)) == 0 ?
                         1 :
                         TCM_Transform.getInt(INSOpdOrderSXTool.getInstance().
                                              getAddOrderSeq(addseqParm));
            for (int i = 1; i < count; i++) {
                String[] opdOrder = (String[]) order.get(i);
                String orderCode = opdOrder[0]; //从3.0批价界面接参
                String orderNo = opdOrder[1] == null ||
                                 opdOrder[1].equals("null") ||
                                 opdOrder[1].trim().length() == 0 ?
                                 "X00000000000" :
                                 opdOrder[1]; //从3.0批价界面接参
                String orderSeq = opdOrder[2];
                if (opdOrder[1] == null || opdOrder[1].equals("null") ||
                    opdOrder[1].trim().length() == 0)
                    orderSeq = String.valueOf(maxSeq);
                String orderTime = opdOrder[3];
                String ordersetCode = opdOrder[4];
                String ordersetGroupNo = opdOrder[5];
                String deptCode = opdOrder[6];
                String drCode = opdOrder[7];
                String rborderDeptCode = opdOrder[8];
                String doseCode = opdOrder[9];
                String ownPrice = opdOrder[10];
                String totQty = opdOrder[11];
                String totAmt = opdOrder[12];
                //交易号(新加)
                String bkc023 = opdOrder[13];
//            System.out.println("action中从前台传入的order11111111%%" + i + orderCode);
//            System.out.println("action中从前台传入的order22222222%%" + i + orderNo);
//            System.out.println("action中从前台传入的order33333333%%" + i + orderSeq);
//            System.out.println("action中从前台传入的order44444444%%" + i + orderTime);
//            System.out.println("action中从前台传入的order55555555%%" + i + ordersetCode);
//            System.out.println("action中从前台传入的order66666666%%" + i + ordersetGroupNo);
//            System.out.println("action中从前台传入的order77777777%%" + i + deptCode);
//            System.out.println("action中从前台传入的order88888888%%" + i + drCode);
//            System.out.println("action中从前台传入的order99999999%%" + i + rborderDeptCode);
//            System.out.println("action中从前台传入的order00000000%%" + i + doseCode);
//            System.out.println("action中从前台传入的order01010101%%" + i + ownPrice);
//            System.out.println("action中从前台传入的order02020202%%" + i + totQty);
//            System.out.println("action中从前台传入的order03030303%%" + i + totAmt);
//            System.out.println("action中从前台传入的order04040404%%" + i + bkc023);
                int orderSeq1 = TCM_Transform.getInt(orderSeq);
                allData.setData("CASE_NO", caseNo);
                allData.setData("ORDER_CODE", orderCode);
                allData.setData("ORDER_NO", orderNo);
                allData.setData("ORDER_SEQ", orderSeq1);
                //2008.11.24 处理增付---------------start----------------
                int ordersetGroupNo1 = TCM_Transform.getInt(ordersetGroupNo);
                double ownPrice1 = TCM_Transform.getDouble(ownPrice);
                double totQty1 = TCM_Transform.getDouble(totQty);
                double totAmt1 = TCM_Transform.getDouble(totAmt);
                String rborderDeptCode1 = rborderDeptCode == null ||
                                          rborderDeptCode.equals("null") ? "" :
                                          TCM_Transform.getString(
                                                  rborderDeptCode);
                allData.setData("BILL_DATE", orderTime.substring(0, 8));
                allData.setData("ORDERSET_CODE", ordersetCode);
                allData.setData("ORDER_GROUP_NO", ordersetGroupNo1);
                allData.setData("DEPT_CODE", deptCode);
                allData.setData("DR_CODE", drCode);
                allData.setData("EXE_DEPT_CODE", rborderDeptCode1);
                allData.setData("DOSE_CODE", doseCode);
                allData.setData("PRICE", ownPrice1);
                allData.setData("QTY", totQty1);
                allData.setData("TOT_AMT", totAmt1);
                //交易号
                allData.setData("BKC023", bkc023);
                //2008.11.24 处理增付---------------end----------------
                allData.setData("OPT_USER", parm.getData("OPT_USER"));
                allData.setData("OPT_TERM", parm.getData("OPT_TERM"));
                endData = INSOpdOrderSXTool.getInstance().insertdata(allData,
                        connection);
                if (endData.getErrCode() < 0) {
                    System.out.println("insert->" + endData.getErrCode());
                    break;
                }
                jyNo.add(endData.getData("BKC023"));
                maxSeq += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (endData.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return endData;
        }

        connection.commit();
        connection.close();
        String sqlWhere = createSQLWhere(jyNo,caseNo);
        String sqlAll =
                " SELECT CASE_NO,BILL_DATE,ORDER_NO,ORDER_SEQ,ORDER_CODE,"+
                "        NHI_ORDER_CODE,ORDERSET_CODE,ORDER_GROUP_NO,ORDER_DESC,APPROVE_FLG,"+
                "        DEPT_CODE,DR_CODE,EXE_DEPT_CODE,OWN_RATE,DOSE_CODE,"+
                "        DOSE_DESC,STANDARD,PRICE,QTY,OWN_AMT,"+
                "        NHI_AMT,TOTAL_AMT,OPT_USER,OPT_DATE,OPT_TERM,"+
                "        BKC023 "+
                "   FROM INS_OPD_ORDER "+sqlWhere;
//        System.out.println("sqlAll"+sqlAll);
        result = new TParm(TJDODBTool.getInstance().select(sqlAll));
//        TParm returnParm = new TParm();
//        returnParm.setData("CASE_NO", parm.getData("CASE_NO"));
//        result = INSOpdOrderSXTool.getInstance().selectdata(returnParm);
        return result;
    }
    /**
       * 产生IN条件
       * @param jyNo List
       * @return String
       */
      public static String createSQLWhere(List jyNo,String caseNo){
          StringBuffer result = new StringBuffer();
          int size = jyNo.size();
          if(jyNo == null){
              return ""+result;
          }
          result.append(" WHERE CASE_NO = '"+caseNo+"' AND BKC023 IN (");
          Iterator iterator = jyNo.iterator();
          int whileCount = 0;
          while(iterator.hasNext()){
              String temp = ""+iterator.next();
              result.append("'"+temp+"'");
              if(whileCount==size-1)
                 break;
              result.append(",");
              whileCount++;
          }
          result.append(")");
          return ""+result;
      }

    /**
     * 申报结算主档数据
     * @param parm TParm
     * @return TParm
     */
    public TParm saveInsOpdPaym(TParm parm) {

        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm parm2 = new TParm();
        parm2.setRowData(parm);
        TParm insertPayMInfo = new TParm();
        TParm parm3 = new TParm();
        parm3.setData("INS_COMPANY", parm2.getData("INS_COMPANY"));
        parm3.setData("CASE_NO", parm2.getData("CASE_NO"));
        //判断是否已存在于结算主档中
        TParm existsPaym = INSOpdOrderSXTool.getInstance().existsPaym(parm3);
        String count = existsPaym.getValue("COUNT", 0);
        int icount;
        if (count == null || count.length() < 1)
            icount = 0;
        else
            icount = Integer.valueOf(count);
        if (icount > 0) {
            connection.rollback();
            connection.close();
            return existsPaym;
        }

        TParm parm1 = new TParm();
        parm1.setData("INS_COMPANY", parm2.getData("INS_COMPANY"));
        parm1.setData("CASE_NO", parm2.getData("CASE_NO"));
        parm1.setData("MR_NO", parm2.getData("MR_NO"));
        parm1.setData("PAT_NAME", parm2.getData("PAT_NAME"));
        parm1.setData("ADM_DATE", parm2.getData("ADM_DATE"));
        parm1.setData("NHI_NO", parm2.getData("NHI_NO")); //暂不用
        parm1.setData("CTZ1_CODE", parm2.getData("CTZ1_CODE"));
        parm1.setData("CTZ2_CODE", parm2.getData("CTZ2_CODE"));
        parm1.setData("CTZ3_CODE", parm2.getData("CTZ3_CODE"));
        parm1.setData("SEX", parm2.getData("SEX"));
        parm1.setData("AGE", parm2.getData("AGE"));
        parm1.setData("ID_NO", parm2.getData("ID_NO"));
        parm1.setData("OFFICE", parm2.getData("OFFICE"));
        parm1.setData("INS_ACCUL_AMT", parm2.getData("INS_ACCUL_AMT")); //暂不用
        parm1.setData("DEPT_CODE", parm2.getData("DEPT_CODE"));
        parm1.setData("OPD_DIAG", parm2.getData("OPD_DIAG")); //暂不用
        parm1.setData("OUT_STATUS", parm2.getData("OUT_STATUS")); //暂不用
        parm1.setData("TOTAL_AMT", parm2.getDouble("TOTAL_AMT"));
        parm1.setData("PAY_CASH", parm2.getDouble("PAY_CASH"));
        parm1.setData("PAY_BANK_CARD", parm2.getDouble("PAY_BANK_CARD"));
        parm1.setData("PAY_CHECK", parm2.getDouble("PAY_CHECK"));
        parm1.setData("PAY_INS_CARD", parm2.getDouble("PAY_INS_CARD"));
        parm1.setData("PAY_DEBIT", parm2.getDouble("PAY_DEBIT"));
        parm1.setData("PAY_OTHER1", parm2.getDouble("PAY_OTHER1"));
        parm1.setData("PAY_OTHER2", parm2.getDouble("PAY_OTHER2")); //暂不用
        parm1.setData("INS_REJECT_AMT", parm2.getData("INS_REJECT_AMT")); //暂不用
        parm1.setData("RESPON_DEPT", parm2.getData("RESPON_DEPT"));
        parm1.setData("RESPON_USER", parm2.getData("RESPON_USER"));
        parm1.setData("INS_STATUS", parm2.getData("INS_STATUS")); //暂不用
        parm1.setData("OPT_USER", parm2.getData("OPT_USER"));
        parm1.setData("OPT_TERM", parm2.getData("OPT_TERM"));
        insertPayMInfo = INSOpdOrderSXTool.getInstance().insertPayMInfo(
                parm1, connection);
        if (insertPayMInfo.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return insertPayMInfo;
        }
        connection.commit();
        connection.close();
        return result;
    }


}
