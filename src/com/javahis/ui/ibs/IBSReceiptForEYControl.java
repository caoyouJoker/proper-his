package com.javahis.ui.ibs;

import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;
import com.javahis.util.JavaHisDebug;

/**
 * <p>Title: 患者医疗费用明细分户账</p>
 *
 * <p>Description: 患者医疗费用明细分户账</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.13
 * @version 1.0
 */
public class IBSReceiptForEYControl
    extends TControl {
    TParm patinfoParm;
    TParm tableSelParm;
    public IBSReceiptForEYControl(){
    }
    public void onInit() {
        super.onInit();
        //table中checkBox专用的监听
        getTTable("Table").addEventListener(TTableEvent.
                                            CHECK_BOX_CLICKED, this,
                                            "onTableComponent");

        initPage();
    }

    /**
     * 得到TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * table中checkBox监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return true;
    }

    /**
     * grid数据
     */
    public void setPageValue() {
        DecimalFormat df = new DecimalFormat("##########0.00");
        if (tableSelParm == null) {
            this.messageBox("请勾选数据!");
            return;
        }
        int allRow = tableSelParm.getCount("DEPT_DESC");
        if (allRow < 0) {
            this.messageBox("请勾选数据!");
            return;
        }
        patinfoParm = new TParm();
        for (int i = 0; i < allRow; i++) {
            String patName = tableSelParm.getValue("PAT_NAME", i);
            String sexDesc = tableSelParm.getValue("SEX_DESC", i);
            String deptDesc = tableSelParm.getValue("DEPT_DESC", i);
            String mrNo = tableSelParm.getValue("MR_NO", i);
            String ipdNo = tableSelParm.getValue("IPD_NO", i);
            String ctzDesc = tableSelParm.getValue("CTZ_DESC", i);
            String inDate = tableSelParm.getValue("IN_DATE", i);
            String outDate = tableSelParm.getValue("DS_DATE", i);
            if (inDate.length() > 0 && inDate != null) {
                patinfoParm.addData("IN_YEAR", inDate.substring(0, 4));
                patinfoParm.addData("IN_MONTH", inDate.substring(4, 6));
                patinfoParm.addData("IN_DAY", inDate.substring(6, 8));
            }
            else {
                patinfoParm.addData("IN_YEAR", null);
                patinfoParm.addData("IN_MONTH", null);
                patinfoParm.addData("IN_DAY", null);
            }
            if (outDate.length() > 0 && outDate != null) {
                patinfoParm.addData("OUT_YEAR", outDate.substring(0, 4));
                patinfoParm.addData("OUT_MONTH", outDate.substring(4, 6));
                patinfoParm.addData("OUT_DAY", outDate.substring(6, 8));
            }
            else {
                patinfoParm.addData("OUT_YEAR", null);
                patinfoParm.addData("OUT_MONTH", null);
                patinfoParm.addData("OUT_DAY", null);
            }
            String caseNo = tableSelParm.getValue("CASE_NO", i);
            String printDateSql =
                " SELECT MAX (TO_CHAR (CHARGE_DATE, 'yyyyMMdd')) AS CHARGE_DATE " +
                "   FROM BIL_RECPM " +
                "  WHERE CASE_NO = '" + caseNo + "' AND REFUND_CODE IS NULL ";
            TParm printParm = new TParm(TJDODBTool.getInstance().select(
                printDateSql));
            String prtDate = printParm.getValue("CHARGE_DATE", 0);
            if (prtDate.length() > 0) {
                patinfoParm.addData("YEAR", prtDate.substring(0, 4));
                patinfoParm.addData("MONTH", prtDate.substring(4, 6));
                patinfoParm.addData("DAY", prtDate.substring(6, 8));
            }
            else {
                patinfoParm.addData("YEAR", null);
                patinfoParm.addData("MONTH", null);
                patinfoParm.addData("DAY", null);
            }
            String mroSql =
                " SELECT CONTACTER, H_ADDRESS AS CONT_ADDRESS, H_TEL AS CONT_TEL " +
                "   FROM MRO_FRONTPG_M " +
                "  WHERE MR_NO = '" + mrNo + "' AND CASE_NO = '" + caseNo +
                "' ";
            TParm mroParm = new TParm(TJDODBTool.getInstance().select(mroSql));
            patinfoParm.addData("CONTACTER", mroParm.getData("CONTACTER", 0));
            String contAddress = mroParm.getValue("CONT_ADDRESS", 0);
            if (contAddress.length() > 14) {
                patinfoParm.addData("CONT_ADDRESS", contAddress.substring(0, 14));
                patinfoParm.addData("CONT_ADDRESS_ADD",
                                    contAddress.substring(14,
                    contAddress.length()));
            }
            else {
                patinfoParm.addData("CONT_ADDRESS", contAddress);
                patinfoParm.addData("CONT_ADDRESS_ADD", "");
            }
            patinfoParm.addData("CONT_TEL", mroParm.getData("CONT_TEL", 0));
            String mroDiagSql =
                " SELECT DIAG_CHN_DESC " +
                "   FROM MRO_FRONTPG_DIAG " +
                "  WHERE MR_NO = '" + mrNo + "' AND CASE_NO = '" + caseNo +
                "' AND MAIN_FLG = 'Y' AND DIAG_TYPE = 'O' ";
            TParm mroDiagParm = new TParm(TJDODBTool.getInstance().select(
                mroDiagSql));
            patinfoParm.addData("DIAG_CHN_DESC",
                                mroDiagParm.getData("DIAG_CHN_DESC", 0));
            String bilPaySql =
                " SELECT SUM (PRE_AMT) AS PRE_AMT " +
                "   FROM BIL_PAY " +
                "  WHERE CASE_NO = '" + caseNo +
                "' AND BUSINESS_TYPE = '1' AND REFUND_FLG = 'N' ";
            TParm bilPayParm = new TParm(TJDODBTool.getInstance().select(
                bilPaySql));
            patinfoParm.addData("SUM_PAY_FEE",
                                df.format(bilPayParm.getDouble("PRE_AMT", 0)));
            String oeDrSql =
                " SELECT B.USER_NAME,A.TOTAL_PAY_AMT  " +
                "   FROM ADM_INP A, SYS_OPERATOR B " +
                "  WHERE A.BEDSN_DR_CODE = B.USER_ID AND A.CASE_NO = '" +
                caseNo + "' ";
            TParm oeDrParm = new TParm(TJDODBTool.getInstance().select(oeDrSql));
            String selNhiFee =
                " SELECT SUM(PAY_DEBIT) AS PAY_DEBIT "+
                "   FROM BIL_RECPM "+
                "  WHERE CASE_NO = '"+caseNo+"' ";
            TParm nhiParm = new TParm(TJDODBTool.getInstance().select(selNhiFee));
            patinfoParm.addData("OPD_DR", oeDrParm.getData("USER_NAME", 0));
            patinfoParm.addData("PAT_NAME", patName);
            patinfoParm.addData("SEX_DESC", sexDesc);
            patinfoParm.addData("DEPT_DESC", deptDesc);
            patinfoParm.addData("MR_NO", mrNo);
            patinfoParm.addData("IPD_NO", ipdNo);
            patinfoParm.addData("CTZ_DESC", ctzDesc);
            String feeSql = "";
            if ("Y".equals(getValue("S_IN"))) {
                feeSql =
                    " SELECT REXP_CODE, SUM (TOT_AMT) AS TOT_AMT " +
                    "   FROM IBS_ORDD " +
                    "  WHERE CASE_NO  = '" + caseNo +
                    "' GROUP BY REXP_CODE ORDER BY REXP_CODE ";

            }
            else {
                feeSql =
                    " SELECT B.REXP_CODE, SUM (B.WRT_OFF_AMT) AS TOT_AMT " +
                    "   FROM BIL_RECPM A, BIL_WRT_OFF B " +
                    "  WHERE A.HOSP_AREA = B.HOSP_AREA " +
                    "    AND A.RECEIPT_NO = B.RECEIPT_NO " +
                    "    AND A.CASE_NO = '" + caseNo +
                    "' GROUP BY B.REXP_CODE ORDER BY B.REXP_CODE ";
            }
//            System.out.println("sql");
            TParm feeParm = new TParm(TJDODBTool.getInstance().select(feeSql));
            int feeCount = feeParm.getCount("REXP_CODE");
            double sumFee = 0.00;
            patinfoParm.addData("PHA_W_FEE", 0.00);
            patinfoParm.addData("PHA_G_FEE", 0.00);
            patinfoParm.addData("PHA_C_FEE", 0.00);
            patinfoParm.addData("CLINIC_FEE", 0.00);
            patinfoParm.addData("CURE_FEE", 0.00);
            patinfoParm.addData("LAB_FEE", 0.00);
            patinfoParm.addData("OPT_FEE", 0.00);
            patinfoParm.addData("BLOOD_FEE", 0.00);
            patinfoParm.addData("BED_FEE", 0.00);
            patinfoParm.addData("NURSING_FEE", 0.00);
            patinfoParm.addData("EXA_FEE", 0.00);
            patinfoParm.addData("AC_FEE", 0.00);
            patinfoParm.addData("SPE_MAT_FEE", 0.00);
            patinfoParm.addData("SPE_SERV_FEE", 0.00);
            patinfoParm.addData("OTHER_FEE", 0.00);
            for (int j = 0; j < feeCount; j++) {
                String rexpCode = feeParm.getValue("REXP_CODE", j);
                double totAmt = feeParm.getDouble("TOT_AMT", j);
                sumFee = sumFee + totAmt;
                if ("01".equals(rexpCode)) //西药费(住)
                    patinfoParm.setData("PHA_W_FEE",i, totAmt);
                if ("02".equals(rexpCode)) //中成药(住)
                    patinfoParm.setData("PHA_G_FEE",i, totAmt);
                if ("03".equals(rexpCode)) //中草药(住)
                    patinfoParm.setData("PHA_C_FEE",i, totAmt);
                if ("04".equals(rexpCode)) //诊查费(住)
                    patinfoParm.setData("CLINIC_FEE",i, totAmt);
                if ("08".equals(rexpCode)) //治疗费(住)
                    patinfoParm.setData("CURE_FEE",i, totAmt);
                if ("09".equals(rexpCode)) //化验费(住)
                    patinfoParm.setData("LAB_FEE",i, totAmt);
                if ("10".equals(rexpCode)) //手术费(住)
                    patinfoParm.setData("OPT_FEE",i, totAmt);
                if ("13".equals(rexpCode)) //血费(住)
                    patinfoParm.setData("BLOOD_FEE",i, totAmt);
                if ("17".equals(rexpCode)) //床位费(住)
                    patinfoParm.setData("BED_FEE",i, totAmt);
                if ("19".equals(rexpCode)) //护理费(住)
                    patinfoParm.setData("NURSING_FEE",i, totAmt);
                if ("20".equals(rexpCode)) //检查费(住)
                    patinfoParm.setData("EXA_FEE",i, totAmt);
                if ("21".equals(rexpCode)) //调温费(住)
                    patinfoParm.setData("AC_FEE",i, totAmt);
                if ("22".equals(rexpCode)) //特殊材料费(住)
                    patinfoParm.setData("SPE_MAT_FEE",i, totAmt);
                if ("23".equals(rexpCode)) //特需服务(住)
                    patinfoParm.setData("SPE_SERV_FEE",i, totAmt);
                if ("24".equals(rexpCode)) //其他(住)
                    patinfoParm.setData("OTHER_FEE",i, totAmt);
            }
            patinfoParm.addData("SUM_FEE", df.format(sumFee));
            patinfoParm.addData("SUM_OWN_FEE",
                                df.format(sumFee -nhiParm.getDouble("PAY_DEBIT", 0)));
            patinfoParm.addData("SUM_NHI_FEE",
                                df.format(nhiParm.getDouble("PAY_DEBIT", 0)));
            patinfoParm.addData("SUM_FEE_CHN",
                                StringUtil.getInstance().numberToWord(sumFee));
//            System.out.println("费用明细"+patinfoParm);

        }

    }

    /**
     * 初始化界面
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("STATION_CODE", "");
        this.callFunction("UI|Table|removeRowAll");
        setValue("S_OUT", "Y");
    }

    /**
     * 打印
     */
    public void onPrint() {
        print();
    }

    /**
     * 调用报表打印预览界面
     */
    private void print() {
        TTable table = (TTable)this.getComponent("Table");
        int row = table.getRowCount();
        if (row < 1) {
            this.messageBox("先查询数据!");
            return;
        }
        setPrintData();
        setPageValue();
//        System.out.println("打印数据" + patinfoParm);
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyyMMddhhmmss");
        int allRow = patinfoParm.getCount("DEPT_DESC");
        for (int i = 0; i < allRow; i++) {
            TParm parm = new TParm();
            parm.setData("YEAR", "TEXT",
                         patinfoParm.getData("YEAR", i) == null ?
                         sysDate.substring(0, 4) :
                         patinfoParm.getData("YEAR", i));
            parm.setData("MONTH", "TEXT",
                         patinfoParm.getData("MONTH", i) == null ?
                         sysDate.substring(4, 6) :
                         patinfoParm.getData("MONTH", i));
            parm.setData("DAY", "TEXT",
                         patinfoParm.getData("DAY", i) == null ?
                         sysDate.substring(6, 8) : patinfoParm.getData("DAY", i));
            parm.setData("PAT_NAME", "TEXT", patinfoParm.getData("PAT_NAME", i));
            parm.setData("SEX_DESC", "TEXT", patinfoParm.getData("SEX_DESC", i));
            parm.setData("DEPT_DESC", "TEXT",
                         patinfoParm.getData("DEPT_DESC", i));
            parm.setData("MR_NO", "TEXT", patinfoParm.getData("MR_NO", i));
            parm.setData("IPD_NO", "TEXT", patinfoParm.getData("IPD_NO", i));
            parm.setData("CTZ_DESC", "TEXT", patinfoParm.getData("CTZ_DESC", i));
            parm.setData("CONTACTER", "TEXT",
                         patinfoParm.getData("CONTACTER", i));
            parm.setData("CONT_ADDRESS", "TEXT",
                         patinfoParm.getData("CONT_ADDRESS", i));
            parm.setData("CONT_ADDRESS_ADD", "TEXT",
                         patinfoParm.getData("CONT_ADDRESS_ADD", i));
            parm.setData("CONT_TEL", "TEXT", patinfoParm.getData("CONT_TEL", i));
            parm.setData("DIAG_CHN_DESC", "TEXT",
                         patinfoParm.getData("DIAG_CHN_DESC", i));
            parm.setData("OPD_DR", "TEXT", patinfoParm.getData("OPD_DR", i));
            parm.setData("IN_YEAR", "TEXT",
                         patinfoParm.getData("IN_YEAR", i) == null ? "-" :
                         patinfoParm.getData("IN_YEAR", i));
            parm.setData("IN_MONTH", "TEXT",
                         patinfoParm.getData("IN_MONTH", i) == null ? "-" :
                         patinfoParm.getData("IN_MONTH", i));
            parm.setData("IN_DAY", "TEXT",
                         patinfoParm.getData("IN_DAY", i) == null ? "-" :
                         patinfoParm.getData("IN_DAY", i));
            parm.setData("OUT_YEAR", "TEXT",
                         patinfoParm.getData("OUT_YEAR", i) == null ? "-" :
                         patinfoParm.getData("OUT_YEAR", i));
            parm.setData("OUT_MONTH", "TEXT",
                         patinfoParm.getData("OUT_MONTH", i) == null ? "-" :
                         patinfoParm.getData("OUT_MONTH", i));
            parm.setData("OUT_DAY", "TEXT",
                         patinfoParm.getData("OUT_DAY", i) == null ? "-" :
                         patinfoParm.getData("OUT_DAY", i));
            //表里
            parm.setData("PHA_W_FEE", "TEXT",
                         patinfoParm.getData("PHA_W_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("PHA_W_FEE", i));
            parm.setData("PHA_G_FEE", "TEXT",
                         patinfoParm.getData("PHA_G_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("PHA_G_FEE", i));
            parm.setData("PHA_C_FEE", "TEXT",
                         patinfoParm.getData("PHA_C_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("PHA_C_FEE", i));
            parm.setData("CLINIC_FEE", "TEXT",
                         patinfoParm.getData("CLINIC_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("CLINIC_FEE", i));
            parm.setData("CURE_FEE", "TEXT",
                         patinfoParm.getData("CURE_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("CURE_FEE",i));
            parm.setData("LAB_FEE", "TEXT",
                         patinfoParm.getData("LAB_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("LAB_FEE", i));
            parm.setData("OPT_FEE", "TEXT",
                         patinfoParm.getData("OPT_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("OPT_FEE", i));
            parm.setData("BLOOD_FEE", "TEXT",
                         patinfoParm.getData("BLOOD_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("BLOOD_FEE", i));
            parm.setData("BED_FEE", "TEXT",
                         patinfoParm.getData("BED_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("BED_FEE", i));
            parm.setData("NURSING_FEE", "TEXT",
                         patinfoParm.getData("NURSING_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("NURSING_FEE", i));
            parm.setData("EXA_FEE", "TEXT",
                         patinfoParm.getData("EXA_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("EXA_FEE", i));
            parm.setData("AC_FEE", "TEXT",
                         patinfoParm.getData("AC_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("AC_FEE", i));
            parm.setData("SPE_MAT_FEE", "TEXT",
                         patinfoParm.getData("SPE_MAT_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("SPE_MAT_FEE", i));
            parm.setData("SPE_SERV_FEE", "TEXT",
                         patinfoParm.getData("SPE_SERV_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("SPE_SERV_FEE", i));
            parm.setData("OTHER_FEE", "TEXT",
                         patinfoParm.getData("OTHER_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("OTHER_FEE", i));
            parm.setData("SUM_FEE", "TEXT",
                         patinfoParm.getData("SUM_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("SUM_FEE", i));
            parm.setData("SUM_FEE_CHN", "TEXT",
                         patinfoParm.getValue("SUM_FEE_CHN", i) == null ? "" :
                         patinfoParm.getValue("SUM_FEE_CHN", i));
            parm.setData("SUM_PAY_FEE", "TEXT",
                         patinfoParm.getData("SUM_PAY_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("SUM_PAY_FEE", i));
            parm.setData("SUM_OWN_FEE", "TEXT",
                         patinfoParm.getData("SUM_OWN_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("SUM_OWN_FEE", i));
            parm.setData("SUM_NHI_FEE", "TEXT",
                         patinfoParm.getData("SUM_NHI_FEE", i) == null ? 0.00 :
                         patinfoParm.getDouble("SUM_NHI_FEE", i));
//            System.out.println("最终显示数据"+parm);
            this.openPrintWindow("%ROOT%\\config\\prt\\091111.jhw", parm, true);
        }

    }

    /**
     * 整理打印数据
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        TParm selParm = new TParm();
        DecimalFormat df = new DecimalFormat("##########0");
        String stationCodeWhere = "";
        if (getValue("STATION_CODE").toString().length() != 0)
            stationCodeWhere = " AND C.STATION_CODE = '" +
                getValue("STATION_CODE") +
                "'  ";
        String nhiCtzFlgWhere = "";
        if ("Y".equals(getValue("OWN_CHECK")) &&
            "N".equals(getValue("NHI_CHECK"))) {
            nhiCtzFlgWhere = " AND B.NHI_CTZ_FLG = 'N' ";
        }
        if ("N".equals(getValue("OWN_CHECK")) &&
            "Y".equals(getValue("NHI_CHECK"))) {
            nhiCtzFlgWhere = " AND B.NHI_CTZ_FLG = 'Y' ";
        }
        if ("Y".equals(getValue("OWN_CHECK")) &&
            "Y".equals(getValue("NHI_CHECK"))) {
            nhiCtzFlgWhere = "";
        }
        String dsWhere = "";
        if ("Y".equals(getValue("S_IN"))) {
            dsWhere =
                " AND H.CHARGE_DATE IS NULL ";
        }
        else {
            dsWhere =
                "    AND H.CHARGE_DATE IS NOT NULL " +
                "    AND H.CHARGE_DATE BETWEEN TO_DATE ('" + startTime + "163000" +
                "', 'yyyyMMddHH24miss') " +
                "                      AND TO_DATE ('" + endTime + "163000" +
                "', 'yyyyMMddHH24miss') ";
        }

        String sql =
            " SELECT 'N' AS FLG,E.DEPT_DESC, F.STATION_DESC, A.MR_NO, A.IPD_NO, A.CASE_NO, D.PAT_NAME," +
            "        B.CTZ_DESC, C.BED_NO," +
            "        CASE WHEN D.SEX_CODE = '1' THEN '男' WHEN D.SEX_CODE = '2' THEN '女'" +
            "        WHEN D.SEX_CODE = '9' THEN '不定' END AS SEX_DESC," +
            "        TO_CHAR (A.IN_DATE, 'YYYYMMDD') AS IN_DATE, TO_CHAR (A.BILL_DATE, 'YYYYMMDD') AS DS_DATE," +
            "        TO_CHAR (A.CHARGE_DATE, 'YYYYMMDD') AS CHARGE_DATE " +
            "   FROM ADM_INP A, SYS_CTZ B, SYS_BED C, SYS_PATINFO D,SYS_DEPT E,SYS_STATION F,BIL_RECPM H " +
            "  WHERE A.HOSP_AREA = B.HOSP_AREA " +
            "    AND A.CTZ_CODE = B.CTZ_CODE " +
            "    AND A.HOSP_AREA = C.HOSP_AREA " +
            "    AND A.BED_NO = C.BED_NO " +
            "    AND A.MR_NO = D.MR_NO " +
            "    AND D.DELETE_FLG <> 'Y' " +
            "    AND A.CANCEL_FLG != 'Y' " +
            "    AND A.DS_DEPT_CODE = E.DEPT_CODE " +
            "    AND C.STATION_CODE = F.STATION_CODE " +
            "    AND A.CASE_NO=H.CASE_NO "+
            "    AND H.OFFRECEIPT_NO IS NULL "+
            "    AND H.REFUND_FLG='N' "+
            stationCodeWhere +
            dsWhere +
            nhiCtzFlgWhere +
            "    AND A.HOSP_AREA = 'HIS' " +
            "  ORDER BY E.DEPT_DESC, F.STATION_DESC, A.MR_NO, A.IPD_NO, A.CASE_NO ";
//        System.out.println("sql" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("CASE_NO") < 1) {
            this.messageBox("查无数据");
            this.initPage();
            return selParm;
        }
//        System.out.println("界面显示数据"+selParm);
//        System.out.println("界面显示数据");
        this.callFunction("UI|Table|setParmValue", selParm);
        return selParm;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        if ("N".equals(getValue("OWN_CHECK")) &&
            "N".equals(getValue("NHI_CHECK"))) {
            this.messageBox("请勾选自费或医保种类");
            return;
        }
        TParm printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "患者医疗费用明细分户账");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        setValue("ALL_FLG","N");
        setValue("OWN_CHECK","N");
        setValue("NHI_CHECK","N");
        table.removeRowAll();

    }

    /**
     * ridioButton改变事件
     */
    public void selDate() {
        if ("Y".equals(getValue("S_IN"))) {
            setValue("S_DATE", "");
            setValue("E_DATE", "");
            this.callFunction("UI|S_DATE|setEnabled", false);
            this.callFunction("UI|E_DATE|setEnabled", false);
        }
        if ("Y".equals(getValue("S_OUT"))) {
            this.callFunction("UI|S_DATE|setEnabled", true);
            this.callFunction("UI|E_DATE|setEnabled", true);
            Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                getDate(), -1);
            setValue("S_DATE", yesterday);
            setValue("E_DATE", SystemTool.getInstance().getDate());
        }

    }

    /**
     * 全选事件
     */
    public void selAllData() {
        TTable table = (TTable)this.getComponent("Table");
        if ("Y".equals(getValue("ALL_FLG"))) {
            int allRow = table.getRowCount();
            for (int i = 0; i < allRow; i++) {
                table.setItem(i, "FLG", "Y");
                table.getParmValue().setData("FLG", i, "Y");
            }
        }
        else {
            int allRow = table.getRowCount();
            for (int i = 0; i < allRow; i++) {
                table.setItem(i, "FLG", "N");
                table.getParmValue().setData("FLG", i, "N");
            }
        }
    }

    /**
     * 为打印攒数据
     */
    public void setPrintData() {
        TTable table = (TTable)this.getComponent("Table");
        TParm tableParm = table.getParmValue();
        tableSelParm = new TParm();
        int count = tableParm.getCount("DEPT_DESC");
        //FLG;DEPT_DESC;STATION_DESC;MR_NO;IPD_NO;CASE_NO;PAT_NAME;CTZ_DESC;BED_NO;SEX_DESC
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("FLG", i)) {
                tableSelParm.addData("DEPT_DESC",
                                     tableParm.getValue("DEPT_DESC", i));
                tableSelParm.addData("STATION_DESC",
                                     tableParm.getValue("STATION_DESC", i));
                tableSelParm.addData("MR_NO",
                                     tableParm.getValue("MR_NO", i));
                tableSelParm.addData("IPD_NO",
                                     tableParm.getValue("IPD_NO", i));
                tableSelParm.addData("CASE_NO",
                                     tableParm.getValue("CASE_NO", i));
                tableSelParm.addData("PAT_NAME",
                                     tableParm.getValue("PAT_NAME", i));
                tableSelParm.addData("CTZ_DESC",
                                     tableParm.getValue("CTZ_DESC", i));
                tableSelParm.addData("BED_NO",
                                     tableParm.getValue("BED_NO", i));
                tableSelParm.addData("SEX_DESC",
                                     tableParm.getValue("SEX_DESC", i));
                tableSelParm.addData("IN_DATE",
                                     tableParm.getValue("IN_DATE", i));
                tableSelParm.addData("DS_DATE",
                                     tableParm.getValue("DS_DATE", i));
            }
        }
    }
    public static void main(String[] args) {
        IBSReceiptForEYControl ibsEy = new IBSReceiptForEYControl();
        JavaHisDebug.runFrame("ibs\\IBSReceiptForEY.x");
    }
}
