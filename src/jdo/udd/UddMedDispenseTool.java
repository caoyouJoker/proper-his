package jdo.udd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 住院药房西药配药</p>
 *
 * <p>Description: 住院药房西药配药</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2011.1.20
 * @version 1.0
 */
public class UddMedDispenseTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static UddMedDispenseTool instanceObject;
    /**
     * 得到实例
     * @return UddMedDispenseTool
     */
    public static UddMedDispenseTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new UddMedDispenseTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public UddMedDispenseTool() {
        setModuleName("udd\\UDDMedDispenseModule.x");
        onInit();
    }

    /**
     * 住院药房病患清单查询
     * @param parm
     * @return
     */
    public TParm onQueryPat(TParm parm) {
        String sql = " SELECT 'N' AS EXEC, A.CASE_NO, A.BED_NO, B.PAT_NAME, "
            + " A.STATION_CODE, A.MR_NO, A.IPD_NO, A.PHA_DISPENSE_NO, "
            + " A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, "
            + " A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE "
            + " FROM ODI_DSPNM A, SYS_PATINFO B, SYS_BED C, SYS_PHAROUTE F "
            + " WHERE B.MR_NO = A.MR_NO "
            + " AND C.BED_NO = A.BED_NO "
            + " AND A.ROUTE_CODE = F.ROUTE_CODE "
            + " AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG = 'Y') "
            + " AND (C.BED_OCCU_FLG IS NULL OR C.BED_OCCU_FLG = 'N') "
            + " AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') "
            + " AND A.DISPENSE_FLG = 'N' ";
        String group_by = " GROUP BY A.CASE_NO, A.BED_NO, B.PAT_NAME, "
            + " A.STATION_CODE,A.MR_NO, A.IPD_NO, A.PHA_DISPENSE_NO, "
            + " A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, "
            + " A.PHA_DISPENSE_DATE ";
        String order_by = " ORDER BY A.CASE_NO, A.PHA_DISPENSE_NO ";

        if (parm.existData("EXEC_DEPT_CODE") &&
            !"".equals(parm.getValue("EXEC_DEPT_CODE"))) {
            sql += " AND A.EXEC_DEPT_CODE= '" + parm.getValue("EXEC_DEPT_CODE") +
                "'";
        }
        if (parm.existData("AGENCY_ORG_CODE") &&
            !"".equals(parm.getValue("AGENCY_ORG_CODE"))) {
            sql += " AND A.AGENCY_ORG_CODE= '" + parm.getValue("AGENCY_ORG_CODE") +
                "'";
        }
        if (parm.existData("ST") && !"".equals(parm.getValue("ST"))) {
            sql += " AND A.DSPN_KIND IN ('ST', 'F') ";
        }
        else if (parm.existData("UD") && !"".equals(parm.getValue("UD"))) {
            sql += " AND A.DSPN_KIND='UD' ";
        }
        else {
            sql += " AND A.DSPN_KIND='DS' ";
        }
        if (parm.existData("STATION_CODE") &&
            !"".equals(parm.getValue("STATION_CODE"))) {
            sql += " AND A.STATION_CODE= '" + parm.getValue("STATION_CODE") + "'";
        }
        if (parm.existData("MR_NO") && !"".equals(parm.getValue("MR_NO"))) {
            sql += " AND A.MR_NO= '" + parm.getValue("MR_NO") + "'";
        }
        if (parm.existData("BED_NO") && !"".equals(parm.getValue("BED_NO"))) {
            sql += " AND A.BED_NO= '" + parm.getValue("BED_NO") + "'";
        }
        if (parm.existData("PHA_DISPENSE_NO") &&
            !"".equals(parm.getValue("PHA_DISPENSE_NO"))) {
            sql += " AND A.PHA_DISPENSE_NO= '" + parm.getValue("PHA_DISPENSE_NO") +
                "'";
        }
        if (parm.existData("CHECK") && !"".equals(parm.getValue("CHECK"))) {
            sql += " AND A.PHA_CHECK_CODE IS NOT NULL ";
        }
        if (parm.existData("UNCHECK_DOSAGE") &&
            !"".equals(parm.getValue("UNCHECK_DOSAGE"))) {
            sql += " AND A.PHA_DOSAGE_CODE IS NULL AND A.START_DTTM >='" +
                parm.getValue("START_DATE") + "' AND A.START_DTTM <='" +
                parm.getValue("END_DATE") + "' ";
        }
        else if (parm.existData("UNCHECK_DISPENSE") &&
                 !"".equals(parm.getValue("UNCHECK_DISPENSE"))) {
            sql +=
                " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") +
                "', 'YYYYMMDDHH24MI') AND A.PHA_DISPENSE_CODE IS NULL ";
        }
        else if (parm.existData("CHECK_DOSAGE") &&
                 !"".equals(parm.getValue("CHECK_DOSAGE"))) {
            sql +=
                " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") + "', 'YYYYMMDDHH24MI') ";
        }
        else {
            sql +=
                " AND A.PHA_DISPENSE_CODE IS NOT NULL AND A.PHA_DISPENSE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") + "', 'YYYYMMDDHH24MI') ";
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(sql + group_by +
            order_by));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 统药单查询
     * @param parm
     * @return
     */
    public TParm onQueryTableSumMed(TParm parm) {
        String sql = "";
        String group_by = "";
        String order_by = "";
        if ("ORDER_BY_ORDER".equals(parm.getValue("ORDER_BY"))) {
            //按药品显示统药单
            sql = "SELECT   A.ORDER_DESC || '  ' || A.GOODS_DESC "
                + " || '  '||C.SPECIFICATION AS ORDER_DESC, "
                + " SUM (DOSAGE_QTY) AS DISPENSE_QTY, E.UNIT_CHN_DESC "
                + " AS DISPENSE_UNIT, CASE WHEN B.SERVICE_LEVEL = 2 "
                + " THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 "
                + " THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, "
                + " A.ORDER_CODE, CASE WHEN B.SERVICE_LEVEL = 2 "
                + " THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) "
                + " WHEN B.SERVICE_LEVEL = 3 "
                + " THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) "
                + " ELSE SUM (DOSAGE_QTY * C.OWN_PRICE)  END AS OWN_AMT "
                + " FROM ODI_DSPNM A, ADM_INP B, SYS_FEE C, SYS_PATINFO D, "
                + " SYS_UNIT E, SYS_PHAROUTE F, PHA_BASE G "
                + " WHERE A.CASE_NO = B.CASE_NO "
                + " AND A.ORDER_CODE = C.ORDER_CODE "
                + " AND A.MR_NO = D.MR_NO "
                + " AND A.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND A.ROUTE_CODE = F.ROUTE_CODE "
                + " AND A.ORDER_CODE = G.ORDER_CODE "
                + " AND (A.ORDER_CAT1_CODE = 'PHA_W' OR "
                + " A.ORDER_CAT1_CODE = 'PHA_C') "
                + " AND A.DISPENSE_FLG = 'N' ";
            group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, E.UNIT_CHN_DESC, "
                + " C.OWN_PRICE, A.GOODS_DESC, B.SERVICE_LEVEL, C.OWN_PRICE2, "
                + " C.OWN_PRICE3, C.SPECIFICATION ";
            order_by = " ORDER BY A.ORDER_CODE ";
        }
        else {
            //按病患显示统药单
            sql = " SELECT   A.ORDER_DESC || '  ' || A.GOODS_DESC "
                + " || '  ' || C.SPECIFICATION AS ORDER_DESC, "
                + " SUM (DOSAGE_QTY) AS DISPENSE_QTY, E.UNIT_CHN_DESC "
                + " AS DISPENSE_UNIT, CASE WHEN B.SERVICE_LEVEL = 2 "
                + " THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 "
                + " THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, "
                + " A.CASE_NO, A.ORDER_CODE, CASE WHEN B.SERVICE_LEVEL = 2 "
                + " THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) "
                + " WHEN B.SERVICE_LEVEL = 3 "
                + " THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) "
                + " ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) "
                + " END AS OWN_AMT, A.BED_NO, A.MR_NO, D.PAT_NAME "
                + " FROM ODI_DSPNM A, ADM_INP B, SYS_FEE C, SYS_PATINFO D, "
                + " SYS_UNIT E, SYS_PHAROUTE F, PHA_BASE G "
                + " WHERE A.CASE_NO = B.CASE_NO "
                + " AND A.ORDER_CODE = G.ORDER_CODE "
                + " AND A.MR_NO = D.MR_NO "
                + " AND A.DOSAGE_UNIT = E.UNIT_CODE "
                + " AND A.ROUTE_CODE = F.ROUTE_CODE "
                + " AND A.ORDER_CODE = C.ORDER_CODE "
                + " AND (A.ORDER_CAT1_CODE='PHA_W' OR "
                + " A.ORDER_CAT1_CODE='PHA_C') "
                + " AND A.DISPENSE_FLG = 'N' ";
            group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, E.UNIT_CHN_DESC,"
                + " C.OWN_PRICE, A.CASE_NO, A.GOODS_DESC, A.BED_NO, A.MR_NO,"
                + " D.PAT_NAME, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3,"
                + " C.SPECIFICATION, A.ORDER_DATE ";
            order_by = " ORDER BY A.ORDER_DATE ";
        }

        if (parm.existData("ST") && !"".equals(parm.getValue("ST"))) {
            sql += " AND A.DSPN_KIND IN ('ST', 'F') ";
        }
        else if (parm.existData("UD") && !"".equals(parm.getValue("UD"))) {
            sql += " AND A.DSPN_KIND='UD' ";
        }
        else {
            sql += " AND A.DSPN_KIND='DS' ";
        }
        if (parm.existData("DOSE_TYPE") && !"".equals(parm.getValue("DOSE_TYPE"))) {
            sql += " AND F.CLASSIFY_TYPE IN (" + parm.getValue("DOSE_TYPE") + ") ";
        }
        if (parm.existData("CHECK") && !"".equals(parm.getValue("CHECK"))) {
            sql += " AND A.PHA_CHECK_CODE IS NOT NULL ";
        }
        if (parm.existData("CTRL_FLG") && !"".equals(parm.getValue("CTRL_FLG"))) {
            sql +=
                " AND (G.CTRLDRUGCLASS_CODE <> '' OR G.CTRLDRUGCLASS_CODE IS NOT NULL ";
        }
        if (parm.existData("CASE_NO") && !"".equals(parm.getValue("CASE_NO"))) {
            sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ") ";
        }
        if (parm.existData("PHA_DISPENSE_NO") &&
            !"".equals(parm.getValue("PHA_DISPENSE_NO"))) {
            sql += " AND A.PHA_DISPENSE_NO IN (" + parm.getValue("PHA_DISPENSE_NO") +
                ") ";
        }

        if (parm.existData("UNCHECK_DOSAGE") &&
            !"".equals(parm.getValue("UNCHECK_DOSAGE"))) {
            sql += " AND A.PHA_DOSAGE_CODE IS NULL AND A.START_DTTM >='" +
                parm.getValue("START_DATE") + "' AND A.START_DTTM <='" +
                parm.getValue("END_DATE") + "' ";
        }
        else if (parm.existData("UNCHECK_DISPENSE") &&
                 !"".equals(parm.getValue("UNCHECK_DISPENSE"))) {
            sql +=
                " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") +
                "', 'YYYYMMDDHH24MI') AND A.PHA_DISPENSE_CODE IS NULL ";
        }
        else if (parm.existData("CHECK_DOSAGE") &&
                 !"".equals(parm.getValue("CHECK_DOSAGE"))) {
            sql +=
                " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") + "', 'YYYYMMDDHH24MI') ";
        }
        else {
            sql +=
                " AND A.PHA_DISPENSE_CODE IS NOT NULL AND A.PHA_DISPENSE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") + "', 'YYYYMMDDHH24MI') ";
        }

        //System.out.println("sql---" + sql + group_by + order_by);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql + group_by +
            order_by));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药品细项查询
     * @param parm
     * @return
     */
    public TParm onQueryTableMed(TParm parm) {
        String sql = " SELECT 'Y' AS EXEC, B.PAT_NAME, A.CASE_NO, A.ORDER_NO, "
            + " A.ORDER_SEQ, A.START_DTTM, A.END_DTTM, A.REGION_CODE, "
            + " A.STATION_CODE, A.DEPT_CODE, A.VS_DR_CODE, A.BED_NO, "
            + " A.IPD_NO, A.MR_NO, DSPN_KIND, A.DSPN_DATE, A.DSPN_USER, "
            + " A.DISPENSE_EFF_DATE, A.DISPENSE_END_DATE, A.EXEC_DEPT_CODE, "
            + " A.AGENCY_ORG_CODE, A.RX_NO, A.LINKMAIN_FLG, A.LINK_NO, "
            + " A.ORDER_CODE, A.ORDER_DESC || ' ' || A.GOODS_DESC || ' ' ||"
            + " A.SPECIFICATION ORDER_DESC, A.GOODS_DESC, A.SPECIFICATION, "
            + " A.MEDI_QTY, A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, "
            + " A.TAKE_DAYS, A.DOSAGE_QTY, A.DOSAGE_UNIT, A.DISPENSE_QTY, "
            + " A.DISPENSE_UNIT, A.GIVEBOX_FLG, A.DISCOUNT_RATE, TOT_AMT, "
            + " A.ORDER_DATE, A.ORDER_DEPT_CODE, A.ORDER_DR_CODE, A.DR_NOTE, "
            + " A.ATC_FLG, A.SENDATC_FLG, A.SENDATC_DTTM, A.INJPRAC_GROUP, "
            + " A.DC_DATE, A.DC_TOT, A.RTN_NO, A.RTN_NO_SEQ, A.RTN_DOSAGE_QTY, "
            + " A.RTN_DOSAGE_UNIT, A.CANCEL_DOSAGE_QTY, A.CANCELRSN_CODE, "
            + " A.TRANSMIT_RSN_CODE, A.PHA_RETN_CODE, A.PHA_RETN_DATE, "
            + " A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DISPENSE_NO, "
            + " A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, "
            + " A.PHA_DISPENSE_DATE, A.NS_EXEC_CODE, A.NS_EXEC_DATE, "
            + " A.NS_EXEC_DC_CODE, A.NS_EXEC_DC_DATE, A.NS_USER, "
            + " A.CTRLDRUGCLASS_CODE, A.PHA_TYPE, A.DOSE_TYPE, "
            + " A.DCTAGENT_CODE, A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, "
            + " A.PACKAGE_AMT, A.DCTAGENT_FLG, A.DECOCT_CODE, A.URGENT_FLG, "
            + " A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE, "
            + " A.RPTTYPE_CODE, A.OPTITEM_CODE, A.HIDE_FLG, A.DEGREE_CODE, "
            + " BILL_FLG, A.CASHIER_USER, A.CASHIER_DATE, A.IBS_CASE_NO_SEQ, "
            + " A.IBS_SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, "
            + " A.ORDER_CAT1_CODE, CASE WHEN E.SERVICE_LEVEL = 2 THEN "
            + " C.OWN_PRICE2 WHEN E.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 "
            + " ELSE C.OWN_PRICE END AS OWN_PRICE, CASE WHEN E.SERVICE_LEVEL "
            + " = 2 THEN A.DOSAGE_QTY * C.OWN_PRICE2 WHEN E.SERVICE_LEVEL = 3 "
            + " THEN A.DOSAGE_QTY * C.OWN_PRICE3 ELSE A.DOSAGE_QTY * "
            + " C.OWN_PRICE END AS OWN_AMT, D.BED_NO_DESC, E.SERVICE_LEVEL, "
            + " G.STOCK_PRICE * A.DOSAGE_QTY AS COST_AMT, "
            + " H.UNIT_CHN_DESC AS DOSAGE_DESC, I.UNIT_CHN_DESC AS MEDI_DESC, "
            + " F.ROUTE_CHN_DESC, J.FREQ_CHN_DESC "
            + " FROM ODI_DSPNM A, SYS_PATINFO B, SYS_FEE C, SYS_BED D, "
            + " ADM_INP E, SYS_PHAROUTE F, PHA_BASE G, SYS_UNIT H, "
            + " SYS_UNIT I, SYS_PHAFREQ J "
            + " WHERE A.MR_NO = B.MR_NO "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND A.BED_NO = D.BED_NO "
            + " AND A.CASE_NO = E.CASE_NO "
            + " AND A.ROUTE_CODE = F.ROUTE_CODE "
            + " AND A.ORDER_CODE = G.ORDER_CODE "
            + " AND A.DOSAGE_UNIT = H.UNIT_CODE "
            + " AND A.MEDI_UNIT = I.UNIT_CODE "
            + " AND A.FREQ_CODE = J.FREQ_CODE "
            + " AND A.DISPENSE_FLG = 'N'"
            + " AND (A.ORDER_CAT1_CODE = 'PHA_W' "
            +" OR A.ORDER_CAT1_CODE = 'PHA_C') ";
        String order_by =
            " ORDER BY A.CASE_NO, A.ORDER_NO, A.ORDER_SEQ, A.LINKMAIN_FLG DESC ";

        if (parm.existData("ST") && !"".equals(parm.getValue("ST"))) {
            sql += " AND A.DSPN_KIND IN ('ST', 'F') ";
        }
        else if (parm.existData("UD") && !"".equals(parm.getValue("UD"))) {
            sql += " AND A.DSPN_KIND='UD' ";
        }
        else {
            sql += " AND A.DSPN_KIND='DS' ";
        }
        if (parm.existData("DOSE_TYPE") && !"".equals(parm.getValue("DOSE_TYPE"))) {
            sql += " AND F.CLASSIFY_TYPE IN (" + parm.getValue("DOSE_TYPE") + ") ";
        }
        if (parm.existData("CHECK") && !"".equals(parm.getValue("CHECK"))) {
            sql += " AND A.PHA_CHECK_CODE IS NOT NULL ";
        }
        if (parm.existData("CTRL_FLG") && !"".equals(parm.getValue("CTRL_FLG"))) {
            sql +=
                " AND (G.CTRLDRUGCLASS_CODE <> '' OR G.CTRLDRUGCLASS_CODE IS NOT NULL ";
        }
        if (parm.existData("CASE_NO") && !"".equals(parm.getValue("CASE_NO"))) {
            sql += " AND A.CASE_NO IN (" + parm.getValue("CASE_NO") + ") ";
        }
        if (parm.existData("PHA_DISPENSE_NO") &&
            !"".equals(parm.getValue("PHA_DISPENSE_NO"))) {
            sql += " AND A.PHA_DISPENSE_NO IN (" + parm.getValue("PHA_DISPENSE_NO") +
                ") ";
        }

        if (parm.existData("UNCHECK_DOSAGE") &&
            !"".equals(parm.getValue("UNCHECK_DOSAGE"))) {
            sql += " AND A.PHA_DOSAGE_CODE IS NULL AND A.START_DTTM >='" +
                parm.getValue("START_DATE") + "' AND A.START_DTTM <='" +
                parm.getValue("END_DATE") + "' ";
        }
        else if (parm.existData("UNCHECK_DISPENSE") &&
                 !"".equals(parm.getValue("UNCHECK_DISPENSE"))) {
            sql +=
                " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") +
                "', 'YYYYMMDDHH24MI') AND A.PHA_DISPENSE_CODE IS NULL ";
        }
        else if (parm.existData("CHECK_DOSAGE") &&
                 !"".equals(parm.getValue("CHECK_DOSAGE"))) {
            sql +=
                " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") + "', 'YYYYMMDDHH24MI') ";
        }
        else {
            sql +=
                " AND A.PHA_DISPENSE_CODE IS NOT NULL AND A.PHA_DISPENSE_DATE BETWEEN TO_DATE('" +
                parm.getValue("START_DATE") + "', 'YYYYMMDDHH24MI') AND TO_DATE('" +
                parm.getValue("END_DATE") + "', 'YYYYMMDDHH24MI') ";
        }


        //System.out.println("sql + order_by----" + sql + order_by);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql + order_by));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
