package com.javahis.ui.opb;

import java.text.DecimalFormat;

import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.IReportTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILSQL;
import jdo.opd.OrderTool;
import jdo.opb.OPBReceiptTool;
import jdo.opb.OPBTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.bil.BILInvoiceTool;
import com.dongyang.ui.event.TTableEvent;
import jdo.util.Manager;
import com.javahis.util.StringUtil;
import jdo.reg.Reg;
import java.awt.Color;

/**
 * <p>Title: 门诊收费批量打票</p>
 *
 * <p>Description: 门诊收费批量打票</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.09.14
 * @version 1.0
 */
public class OPBPatchPrintControl extends TControl {
    TTable table;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        setValue("ADM_TYPE", "");
        table = (TTable)this.getComponent("TABLE");
        //账单table专用的监听
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                               "onTableComponent");
        initPage();
    }

    /**
     * 账单table监听事件
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        return true;
    }


    /**
     * 初始化界面数据
     */
    public void initPage() {
        //================pangben modify 20110407 start 区域锁定
        //this.setValue("REGION_CODE", "HIS");
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110407 stop
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "OPB");
        selInvoice.setData("CASHIER_CODE", Operator.getID());
        selInvoice.setData("TERM_IP", Operator.getIP());
//        System.out.println("票据主档数据" + selInvoice);
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        if (invNo == null || invNo.length() == 0) {
            this.messageBox("请先开帐");
            return;
        }
//        System.out.println("时间" + getApTime());
        this.setValue("UPDATE_NO", invNo);
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (getValue("ADM_TYPE") != null ||
            getValueString("ADM_TYPE").length() != 0) {
            parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
        }
        if (getValue("CLINIC_AREA") != null ||
            getValueString("CLINIC_AREA").length() != 0) {
            parm.setData("CLINIC_AREA", getValueString("CLINIC_AREA"));
        }
        if (getValue("SESSION_CODE") != null ||
            getValueString("SESSION_CODE").length() != 0) {
            parm.setData("SESSION_CODE", getValueString("SESSION_CODE"));
        }
        if (getValue("REALDEPT_CODE") != null ||
            getValueString("REALDEPT_CODE").length() != 0) {
            parm.setData("REALDEPT_CODE", getValueString("REALDEPT_CODE"));
        }
        if (getValue("REALDR_CODE") != null ||
            getValueString("REALDR_CODE").length() != 0) {
            parm.setData("REALDR_CODE", getValueString("REALDR_CODE"));
        }
        //=======================pangben modify 20110407 start  添加查询条件
        //区域
        if (getValueString("REGION_CODE").length() > 0)
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        //=======================pangben modify 20110407 stop
        String sql = this.getOPBPatchPrintSql(parm);
        System.out.println("批量打票查询" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            System.out.println("日结数据查询错误  " + result.getErrText());
            return;
        }
        table.setParmValue(result);
        TParm tableParm = table.getParmValue();
        String confirm_no = "";
        /**
         * 医保病人背景颜色
         */
        Color nhiColor = new Color(128, 0, 128);
        /**
         * 普通背景颜色
         */
        Color normalColorBJ = new Color(255, 255, 255);
        for (int i = 0; i < tableParm.getCount(); i++) {
//            ctz1Code = tableParm.getValue("CTZ1_CODE", i);
//            String ctzSql =
//                    " SELECT NHI_CTZ_FLG " + "   FROM SYS_CTZ " +
//                    "  WHERE CTZ_CODE = '" + ctz1Code + "' ";
//            TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
        	confirm_no = tableParm.getValue("CONFIRM_NO",i);
            if (confirm_no.length()>0) {
                table.setRowColor(i, nhiColor);
            } else {
                table.setRowColor(i, normalColorBJ);
            }
        }

    }

    /**
     * 全选事件
     */
    public void onSelectAll() {
        String select = getValueString("SELECT");
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        table.setParmValue(parm);
    }

    /**
     * 打印
     */
    public void onPrint() {
        //得到table上的数据
        table.acceptText();
        TParm tableParm = table.getParmValue();
//        System.out.println("table数据====" + tableParm);
        //检核打印数据
        if (!checkPrintData(tableParm))
            return;
        int rowCount = tableParm.getCount("CASE_NO");
//        System.out.println("table行数" + rowCount);
        Pat pat = null;
        Reg reg = null;
        for (int i = 0; i < rowCount; i++) {
//            System.out.println("总行数"+rowCount);
//            System.out.println("table数据清空后数据"+tableParm);
            String onlyCaseNo = tableParm.getValue("CASE_NO", i);
//            System.out.println("第" + i + "个case_no》》》》》" + onlyCaseNo);
            String flg = tableParm.getValue("FLG", i);
            if ("Y".equals(flg)) {
                TParm selInvoice = new TParm();
                selInvoice.setData("STATUS", "0");
                selInvoice.setData("RECP_TYPE", "OPB");
                selInvoice.setData("CASHIER_CODE", Operator.getID());
                selInvoice.setData("TERM_IP", Operator.getIP());
//                System.out.println("票据主档数据入参" + selInvoice);
                TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                        selInvoice);
//                System.out.println("票据主档数据信息======" + invoice);
                String invNo = invoice.getValue("UPDATE_NO", 0);
                String endInvNo = invoice.getValue("END_INVNO", 0);
                if (invNo.compareTo(endInvNo) > 0) {
                    this.messageBox("票据已用完!");
                    return;
                }
                pat = Pat.onQueryByMrNo(tableParm.getValue("MR_NO", i));
                reg = Reg.onQueryByCaseNo(pat, onlyCaseNo);
                TParm parm = new TParm();
                parm.setData("CASE_NO", onlyCaseNo);
                parm.setData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.setData("INV_NO", invNo);
                parm.setData("OPT_USER", Operator.getID());
                parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
                parm.setData("OPT_TERM", Operator.getIP());
                parm.setData("REGION_CODE", Operator.getRegion());
                parm.setData("START_INVNO", invoice.getData("START_INVNO", 0));
                // =============pangben modify 201110817 start
                parm.setData("feeShow", ""); // 金额的保存使用管控
                parm.setData("TOT_AMT", this.getValueDouble("TOT_AMT"));
                parm.setData("billFlg", "Y"); // 记账: N 不记账:Y
                parm.setData("CONTRACT_CODE", ""); // 记账单位

                TParm selOpdParm = new TParm();
                selOpdParm.setData("CASE_NO", onlyCaseNo);
                selOpdParm.setData("REGION_CODE", Operator.getRegion());
                OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
                selOpdParm.setData("REGION_CODE", Operator.getRegion());
                TParm opdParm = OrderTool.getInstance().selDataForOPBEKTC(
                        selOpdParm);
                TParm result = ektSavePrint(opdParm, parm);
                if (null == result || result.getErrCode() < 0) {
                    this.messageBox("E0005");
                    return;
                }
                String receiptNo = result.getValue("RECEIPT_NO", 0);
                int opdCount = opdParm.getCount("CASE_NO");
                if (opdCount <= 0) {
//                    this.messageBox("无可收费医嘱");
//                    return;
                	continue;
                }
                TParm recpParm = null;
                // 门诊收据档数据:医疗卡收费打票|现金收费打票||医保打票
                recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
                if(recpParm.getErrCode()<0){
                    System.out.println("票据查询错误  " + result.getErrText());
                        return;
                }
//                System.out.println("票据信息" + recpParm);
                onPrintRPT(recpParm, onlyCaseNo,
                           tableParm.getValue("PAT_NAME", i),
                           tableParm.getValue("IDNO", i), reg.getAdmType());
            }

        }
        this.onClear();
    }

    /**
     * 现金打票明细入参
     *
     * @param oneReceiptParm
     *            TParm
     * @param recpParm
     *            TParm
     * @param dparm
     *            TParm
     */
    private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
                                 TParm dparm) {
        String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
        dparm.setData("NO", receptNo);
        TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
        // if(orderParm.getCount()>10){
        // oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用明细表)");
        // }
        oneReceiptParm.setData("TABLE", tableresultparm.getData());
    }

    /**
     * 打印票据封装===================pangben 20111014
     * @param recpParm TParm
     * @param onlyCaseNo String
     * @param patName String
     * @param IDNO String
     * @param admType String
     */
    private void onPrintRPT(TParm recpParm, String onlyCaseNo, String patName,
                            String IDNO, String admType) {
//        System.out.println("recpParm=====" + recpParm);
        DecimalFormat df = new DecimalFormat("0.00");
        TParm oneReceiptParm = new TParm();
        TParm insOpdInParm = new TParm();
        String confirmNo = "";      
        String accountPay = ""; // 个人实际帐户支付
        String gbNhiPay = ""; // 医保统筹支付
        String gbCashPay = ""; // 现金支付
        String agentAmt = ""; // 补助金额
        String mrNo = "";
        
        String sql = "SELECT MR_NO FROM REG_PATADM WHERE CASE_NO = '"+onlyCaseNo+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        mrNo = result.getValue("MR_NO", 0);
        
        Pat pat = Pat.onQueryByMrNo(mrNo);
        
        insOpdInParm.setData("REGION_CODE", Operator.getRegion());
      //  insOpdInParm.setData("CONFIRM_NO", confirmNo);  
        insOpdInParm.setData("CASE_NO", onlyCaseNo);
        // INS_CROWD_TYPE, INS_PAT_TYPE
        // 票据信息
        // 姓名
        oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
        oneReceiptParm.setData("SEX", "TEXT", pat.getSexString());
       
   
        // 人员类别
        oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
        // 费用类别
        // ======zhangp 20120228 modify start
       
        // =====zhangp 20120228 modify end
        // 医疗机构名称
        oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
                               .getHospitalCHNFullName(Operator.getRegion()));
      
        // 账户支付
        oneReceiptParm.setData("DA_AMT", "TEXT",
                               accountPay.length() == 0 ? "0.00" :
                               df.format(accountPay));

        // 费用合计
        oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
                "TOT_AMT", 0)));
        // 费用显示大写金额
        oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
                               .numberToWord(recpParm.getDouble("TOT_AMT", 0)));

      
        // 现金支付= 医疗卡金额+现金+绿色通道+支付宝
        double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
                         + StringTool
                         .round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
                         +
                         StringTool.round(recpParm.getDouble("PAY_OTHER1", 0),2)
                         +StringTool.round(recpParm.getDouble("ALIPAY", 0),2);
        
        // 现金支付
        oneReceiptParm.setData("Cash", "TEXT",
                               gbCashPay.length() == 0 ? payCash :
                               df.format(gbCashPay));

        // 账户支付---医疗卡支付
       // oneReceiptParm.setData("Recharge", "TEXT", 0.00);
        // 医疗救助金额
//        oneReceiptParm.setData("AGENT_AMT", "TEXT",
//                               agentAmt.length() == 0 ? "0.00" :
//                               df.format(agentAmt));
 
        oneReceiptParm.setData("MR_NO", "TEXT", mrNo);
        // =====zhangp 20120229 modify end
        // 打印日期
        oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
                SystemTool.getInstance().getDate(), "yyyy  MM  dd"));
        // 医保统筹支付
        
        oneReceiptParm.setData("PAY_DEBIT", "TEXT","0.00");
//        oneReceiptParm.setData("PAY_DEBIT", "TEXT",
//                               gbNhiPay.length() == 0 ?
//                               StringTool.round(recpParm.getDouble(
//                                       "PAY_INS_CARD", 0), 2) :
//                               df.format(gbNhiPay));
        
        if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
            // 绿色通道金额
            oneReceiptParm.setData("GREEN_PATH", "TEXT", "绿色通道支付");
            // 绿色通道金额
            oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
                    recpParm.getDouble("PAY_OTHER1", 0), 2));

        }
        
      //查询医生名称
		String CtzDescSql =" SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
        					" WHERE A.CASE_NO = '"+onlyCaseNo+"'"+
        					" AND A.CTZ1_CODE = B.CTZ_CODE"+
        					" AND A.REALDR_CODE =C.USER_ID";		 
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
        // 医生名称
        oneReceiptParm.setData("DR_NAME", "TEXT", CtzDescParm.getValue(
                "USER_NAME", 0));
        //医疗机构类型
        String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));

        

        // 打印人
        oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
        oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
        oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(详见费用清单)");

        oneReceiptParm.setData("CARD_CODE", "TEXT", ""); // 如果不是医保	
         
        oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用清单)");
       
        //其他医保支付
        oneReceiptParm.setData("QTYL", "TEXT", "0.00");
      
        // =====20120229 zhangp modify end
        for (int i = 1; i <= 30; i++) {
            if (i < 10) {
                oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
                                       .getDouble("CHARGE0" + i, 0) == 0 ? "0.00" :
                                       recpParm
                                       .getData("CHARGE0" + i, 0));
            } else {
                oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
                                       .getDouble("CHARGE" + i, 0) == 0 ? "0.00" :
                                       recpParm
                                       .getData("CHARGE" + i, 0));
            }
        }
        // =================20120219 zhangp modify start
        oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
                .getDouble("CHARGE01", 0)
                + recpParm.getDouble("CHARGE02", 0)));

        TParm dparm = new TParm();
        dparm.setData("CASE_NO", onlyCaseNo);
        dparm.setData("ADM_TYPE", admType);
        onPrintCashParm(oneReceiptParm, recpParm, dparm);
        oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("RECEIPT_NO", 0));//add by wanglong 20121217
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//                             oneReceiptParm,true);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//报表合并modify by wanglong 20130730
        return;

    }

    /**
     *
     * @param opdParm TParm
     * @param parm TParm
     * @return TParm
     */
    private TParm ektSavePrint(TParm opdParm, TParm parm) {
        TParm result = new TParm();
        int opdCount = opdParm.getCount("CASE_NO");
        if (opdCount <= 0) {
            this.messageBox("无可收费医嘱");
            return null;
        }
        parm.setData("opdParm", opdParm.getData()); // 获得一条汇总金额
        parm.setData("REGION_CODE", Operator.getRegion()); // 区域
        //===zhangp 20120328 start
        parm.setData("ADM_TYPE", opdParm.getData("ADM_TYPE", 0));
        //===zhangp 20120328 end
        result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                                              "onOPBEktprint", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 检核打印数据
     * @param tableParm TParm
     * @return boolean
     */
    public boolean checkPrintData(TParm tableParm) {
        //未打票数据
        int count = table.getRowCount();
        if (count <= 0) {
            messageBox("无打印数据!");
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (tableParm.getValue("FLG", i).equals("Y")) {
                return true;
            }
        }
        messageBox("无打印数据!");
        return false;
    }

    /**
     * 清空
     */
    public void onClear() {
        //初始化页面信息
        initPage();
        this.callFunction("UI|TABLE|removeRowAll");

    }
    
    /**
     * 门诊收费批量打印
     * @param parm TParm
     * @return String
     */
    public static String getOPBPatchPrintSql(TParm parm) {
        String sql = "";
        if (parm == null)
            return sql;
        sql =
                "  SELECT '' AS FLG, TO_CHAR (F.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
                "         F.SESSION_CODE, F.REALDEPT_CODE, F.REALDR_CODE, F.QUE_NO, F.MR_NO," +
                "         SUM (A.AR_AMT) AS AR_AMT, F.CASE_NO, G.PAT_NAME,G.IDNO,F.CTZ1_CODE,F.CONFIRM_NO " +
                "    FROM OPD_ORDER A, EKT_TRADE D, REG_PATADM F, SYS_PATINFO G" +
                "   WHERE A.BUSINESS_NO = D.TRADE_NO(+)" +
                "     AND A.BILL_FLG = 'Y'" +
                "     AND A.BILL_TYPE IN ('C','E')" +
                "     AND A.RELEASE_FLG <> 'Y'" +
                "     AND A.RECEIPT_NO IS NULL" +
                "     AND (A.BUSINESS_NO IS NOT NULL OR A.BUSINESS_NO <> '')" +
                "     AND (A.PRINT_FLG IS NULL OR A.PRINT_FLG = 'N' OR A.PRINT_FLG = '')" +
                "     AND (D.STATE = '1' OR D.STATE IS NULL)" +
                "     AND F.MR_NO = G.MR_NO" +
                "     AND A.MR_NO = G.MR_NO" +
                "     AND F.CASE_NO = A.CASE_NO" ;
    	//===zhangp 此处写死 市内门诊只能查到市内门诊的人 start
    	String opDept = Operator.getDept();
    	if(opDept.equals("020103")){
    		sql += " AND F.REALDEPT_CODE='020103' ";
    	}else{
    		sql += " AND F.REALDEPT_CODE<>'020103' ";
    	}
    	//===zhangp 此处写死 市内门诊只能查到市内门诊的人 end
        String admType = parm.getValue("ADM_TYPE");
        if (admType != null && !admType.equals(""))
            sql += " AND F.ADM_TYPE='" + admType + "' ";
        String clinicArea = parm.getValue("CLINIC_AREA");
        if (clinicArea != null && !clinicArea.equals(""))
            sql += " AND F.CLINICAREA_CODE='" + clinicArea + "' ";
        String sessionCode = parm.getValue("SESSION_CODE");
        if (sessionCode != null && !sessionCode.equals(""))
            sql += " AND F.SESSION_CODE='" + sessionCode + "' ";
        String realDeptCode = parm.getValue("REALDEPT_CODE");
        if (realDeptCode != null && !realDeptCode.equals(""))
            sql += " AND F.REALDEPT_CODE='" + realDeptCode + "' ";
        String realDrCode = parm.getValue("REALDR_CODE");
        if (realDrCode != null && !realDrCode.equals(""))
            sql += " AND F.REALDR_CODE='" + realDrCode + "' ";
        //=================pangben modify 20110407 start 添加区域查询条件
        String region = parm.getValue("REGION_CODE");
        if (region != null && !region.trim().equals(""))
            sql += " AND F.REGION_CODE='" + region + "' ";
        //=================pangben modify 20110407 stop

        sql +=
                "GROUP BY F.ADM_DATE," +
                "         F.SESSION_CODE," +
                "         F.REALDEPT_CODE," +
                "         F.REALDR_CODE," +
                "         F.QUE_NO," +
                "         F.MR_NO," +
                "         G.PAT_NAME," +
                "         F.CASE_NO," +
                "         G.IDNO," +
                "         F.CTZ1_CODE," +
                "		  F.CONFIRM_NO"+
                " ORDER BY F.CASE_NO";
        return sql;
    }
}
