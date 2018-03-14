package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
*
* <p>Title: 健康检查团体交费</p>
*
* <p>Description: 健康检查团体交费</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMChargeTool extends TJDOTool {

    /**
     * 实例
     */
    public static HRMChargeTool instanceObject;

    /**
     * 得到实例
     * 
     * @return HRMChargeTool
     */
    public static HRMChargeTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new HRMChargeTool();
        }
        return instanceObject;
    }

    /**
     * 根据合同查询票据
     */
    public TParm onQueryReceiptByContract(String contractCode) {// add by wanglong 20130324
        TParm result = new TParm();
        if (contractCode == null || contractCode.equals("")) {
            result.setErrCode(-1);
            result.setErrText("参数为空");
            return result;
        }
        String sql =
                "SELECT A.* FROM BIL_OPB_RECP A, HRM_BILL B WHERE A.CASE_NO = B.CONTRACT_CODE "
                        + " AND A.MR_NO = B.COMPANY_CODE AND A.RECEIPT_NO = B.RECEIPT_NO "
                        + " AND B.CONTRACT_CODE = '" + contractCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据病案号查询票据
     */
    public TParm onQueryReceiptByMr(String mrNo) {// add by wanglong 20130324
        TParm result = new TParm();
        if (mrNo == null || mrNo.equals("")) {
            result.setErrCode(-1);
            result.setErrText("参数为空");
            return result;
        }
        String sql =
                "SELECT A.* FROM BIL_OPB_RECP A, HRM_BILL B, HRM_CONTRACTD C "
                        + " WHERE A.CASE_NO = B.CONTRACT_CODE AND A.MR_NO = B.COMPANY_CODE "
                        + " AND A.RECEIPT_NO = B.RECEIPT_NO AND B.BILL_NO = C.BILL_NO "
                        + " AND C.MR_NO ='" + mrNo + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 根据收据号查询医嘱明细
     */
    public TParm onQueryOrderdetail(String receiptNo) {// add by wanglong 20130324
        TParm result = new TParm();
        if (receiptNo == null || receiptNo.equals("")) {
            result.setErrCode(-1);
            result.setErrText("参数为空");
            return result;
        }
        String sql = // modify by wanglong 20130424
                "SELECT DISTINCT A.SEQ_NO,A.STAFF_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,A.MR_NO,A.PAT_NAME,"
                        + "A.SEX_CODE,A.IDNO,A.REPORT_DATE,A.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC,C.UNIT_CHN_DESC UNIT_CODE,"
                        + "A.DISPENSE_QTY,A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,A.OWN_PRICE OWN_AMT,A.DISCOUNT_RATE,A.OWN_AMT AR_AMT "
                        + "FROM (SELECT B.SEQ_NO,B.STAFF_NO,B.COMPANY_CODE,A.CONTRACT_CODE,B.PACKAGE_CODE,"
                        + "             A.MR_NO,A.CASE_NO,B.PAT_NAME,B.SEX_CODE,B.IDNO,B.REAL_CHK_DATE REPORT_DATE,A.ORDERSET_CODE,"
                        + "             A.DISCOUNT_RATE,SUM(A.DISPENSE_QTY) DISPENSE_QTY,SUM(A.OWN_PRICE) OWN_PRICE,SUM(A.OWN_AMT) OWN_AMT "
                        + "        FROM (SELECT A.MR_NO,A.CONTRACT_CODE,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                     SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.ORDERSET_GROUP_NO "
                        + "                FROM HRM_CONTRACTD A, HRM_ORDER B "
                        + "               WHERE A.MR_NO = B.MR_NO "
                        + "                 AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "                 AND B.RECEIPT_NO='"
                        + receiptNo
                        + "'           GROUP BY A.MR_NO,A.CONTRACT_CODE,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,B.ORDERSET_GROUP_NO) A, HRM_CONTRACTD B "
                        + "       WHERE A.MR_NO = B.MR_NO "
                        + "         AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "    GROUP BY B.SEQ_NO,B.STAFF_NO,B.COMPANY_CODE,A.CONTRACT_CODE,B.PACKAGE_CODE,A.MR_NO,A.CASE_NO,B.PAT_NAME,B.SEX_CODE,"
                        + "             B.IDNO,B.REAL_CHK_DATE,A.ORDERSET_CODE,A.DISCOUNT_RATE,A.OWN_PRICE) A, HRM_ORDER B, SYS_UNIT C "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   AND B.DISPENSE_UNIT = C.UNIT_CODE "
                        + "ORDER BY A.COMPANY_CODE, A.CONTRACT_CODE, A.SEQ_NO";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
}
