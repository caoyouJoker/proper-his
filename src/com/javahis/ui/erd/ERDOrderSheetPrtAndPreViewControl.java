package com.javahis.ui.erd;


import jdo.sys.Pat;
import com.dongyang.control.TControl;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TWord;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import jdo.erd.ERDForSUMTool;

/**
 * <p>Title: ����ҽ������ӡԤ��������</p>
 *
 * <p>Description: ҽ����������Ч��
 *                  1,order_cat1_type:PHA  ����+��λ+Ƶ��+�÷�
 *                    ��PHA ��AW1,.,STAT ����ʾƾ��Ƶ�Σ�������ʾ Ƶ��
 *                  2,����ҽ��
 *                  3,ZZZZҽ����ע
 *                  4,ת�ƻ�ҳ</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ERDOrderSheetPrtAndPreViewControl extends TControl {

    // ҽ��������
    TRadioButton ST;
    TRadioButton UD;
    TRadioButton DS;
    TWord word;
    String caseNo = "";
    String name = "";
    String ipdNo = "";
    String mrNo = "";
    String dept = "";
    String station = "";
    String bed = "";
   
    TParm outsideParm = new TParm(); // ���������ⲿ�Ĳ���

    public void onInit() {
        super.onInit();
        myInitControler();
        outsideParm = (TParm) this.getParameter();
        // this.messageBox("===�ⲿ����===="+outsideParm);
        if (outsideParm != null) {
            if (!initParmFromOutside()) {
                return;
            } else {
                initWord();
            }
        } else {
            initWord();
        }
    }

    /**
     * ��ʼ��ʱ�õ����пؼ�����
     */
    public void myInitControler() {
        ST = (TRadioButton) this.getComponent("ST");
        UD = (TRadioButton) this.getComponent("UD");
        DS = (TRadioButton) this.getComponent("DS");
        word = (TWord) this.getComponent("WORD");
    }

    /**
     * ��ʼ���������caseNo/stationCode
     */
    public boolean initParmFromOutside() {
        // ������Ų�ѯ��caseNo
        this.setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
        TParm parm1 = new TParm();
        parm1.setData("CASE_NO", this.getCaseNo());
        TParm erdInfo = ERDForSUMTool.getInstance().selERDPatInfo(parm1);
        if (erdInfo.getCount() <= 0) {
            this.messageBox("û����ص�����ҽ����");
            return false;
        } else {
            this.setIpdNo(erdInfo.getValue("IPD_NO", 0));
            this.setMrNo(erdInfo.getValue("MR_NO", 0));
            String inDeptCode = erdInfo.getValue("IN_DEPT_CODE", 0);
            TParm firstData =
                    new TParm(TJDODBTool.getInstance().select(
                                                              "SELECT DEPT_CHN_DESC from sys_dept where DEPT_CODE='"
                                                                      + inDeptCode + "'"));
            this.setDept(firstData.getValue("DEPT_CHN_DESC", 0));
            station = erdInfo.getValue("ERD_REGION_DESC", 0);
            bed = erdInfo.getValue("BED_DESC", 0);
            Pat pat = Pat.onQueryByMrNo((String) erdInfo.getValue("MR_NO", 0));
            this.setName(pat.getName());
        }
        return true;
    }

    /**
     * ��ʼ����������
     */
    public void initWord() {
        word.setWordParameter(null);
        word.getWordText().getPM().getFileManager().onNewFile();
        TParm prtParm = new TParm();
        // ����TWord
        word.setWordParameter(prtParm);
        word.setPreview(true);
        TParm orderParm = new TParm();
        // �õ���ӡ����
        orderParm = getOrderParm();
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
        orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
        orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
        orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
        TParm data = new TParm();
        data.setData("TABLE", orderParm.getData());
        data.setData("NAME", "TEXT", this.getName());
        data.setData("MR_NO", "TEXT", this.getMrNo());
        data.setData("IPD_NO", "TEXT", this.getIpdNo());
        data.setData("DEPT", "TEXT", this.getDept());
        data.setData("STATION", "TEXT", station);
        data.setData("BED", "TEXT", bed);
        word.setWordParameter(data);
        word.setFileName("%ROOT%\\config\\prt\\erd\\OrderSheet.jhw");
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        word.print();
    }

    
    /**
     * ��������
     * 
     * @param flg
     *            Object
     */
    public void onCheck(Object flg) {
        word.setWordParameter(null);
        word.getWordText().getPM().getFileManager().onNewFile();
        TParm prtParm = new TParm();
        // ����TWord
        word.setWordParameter(prtParm);
        word.setPreview(true);
        TParm orderParm = new TParm();
        if ("ST".equals(flg + "") && ST.isSelected()) {
            // �õ���ӡ����
            orderParm = getSTOrderParm();
            orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
            orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
            orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
            TParm data = new TParm();
            data.setData("TABLE", orderParm.getData());
            data.setData("NAME", "TEXT", this.getName());
            data.setData("MR_NO", "TEXT", this.getMrNo());
            data.setData("IPD_NO", "TEXT", this.getIpdNo());
            data.setData("DEPT", "TEXT", this.getDept());
            word.setWordParameter(data);
            word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_ST.jhw");
            return;
        }
        if ("UD".equals(flg + "") && UD.isSelected()) {
            // �õ���ӡ����
            orderParm = getUDOrderParm();
            orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
            orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
            orderParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_CODE");
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE_DAY");
            orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE_TIME");
            orderParm.addData("SYSTEM", "COLUMNS", "DC_DR_CODE");
            orderParm.addData("SYSTEM", "COLUMNS", "DC_NS_CHECK_CODE");
            TParm data = new TParm();
            data.setData("TABLE", orderParm.getData());
            data.setData("NAME", "TEXT", this.getName());
            data.setData("MR_NO", "TEXT", this.getMrNo());
            data.setData("IPD_NO", "TEXT", this.getIpdNo());
            data.setData("DEPT", "TEXT", this.getDept());
            word.setWordParameter(data);
            word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_UD.jhw");
            return;
        }
        if ("DS".equals(flg + "") && DS.isSelected()) {
            // �õ���ӡ����
            orderParm = getDSOrderParm();// Ŀǰ��ʱ�ͳ�Ժ��ҩһ��
            orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
            orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
            orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
            orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
            TParm data = new TParm();
            data.setData("TABLE", orderParm.getData());
            data.setData("NAME", "TEXT", this.getName());
            data.setData("MR_NO", "TEXT", this.getMrNo());
            data.setData("IPD_NO", "TEXT", this.getIpdNo());
            data.setData("DEPT", "TEXT", this.getDept());
            word.setWordParameter(data);
            word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_DS.jhw");
            return;
        }
    }

    /**
     * �õ�����ҽ����Ϣ
     * 
     * @return TParm
     */
    private TParm getOrderParm() {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(getSQL()));
        
        TParm printData = arrangeData(STparm);
        return printData;
    }

    /**
     * ȡ������ҽ����ϢSQL
     * 
     * @return String
     */
    private String getSQL() {
        String sql =
                "SELECT TO_CHAR( A.ORDER_DATE, 'MM/DD') AS EFF_DATE_DAY, "
                        + "       TO_CHAR( A.ORDER_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.DR_CODE ORDER_DR_CODE, "
                        + "       A.ORDER_DESC, A.MEDI_QTY, F.UNIT_CHN_DESC, A.FREQ_CODE, A.DOSE_TYPE, A.LINKMAIN_FLG,"
                        + "       A.LINK_NO, A.DR_NOTE, A.ORDER_CODE, A.CAT1_TYPE, TO_CHAR( A.NS_EXEC_DATE, 'MM/DD HH24:MI') AS NS_EXEC_DATE, "
                        + "       A.NS_EXEC_CODE, A.ROUTE_CODE,B.IS_REMARK       "
                        + "  FROM OPD_ORDER A, SYS_UNIT F, SYS_FEE B "
                        + " WHERE A.CASE_NO = '#' AND A.RX_TYPE != '0' "
                        + "   AND A.RX_TYPE NOT IN ('7','6') AND A.HIDE_FLG = 'N' "
                        + "   AND A.MEDI_UNIT = F.UNIT_CODE(+)        "
                        + "   AND A.ORDER_CODE = B.ORDER_CODE(+)         "
                        + " ORDER BY A.ORDER_DATE  ";
        sql = sql.replaceFirst("#", this.getCaseNo());
        return sql;
    }

    /**
     * ��������
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            // �ж�����ҽ��
            if (ifLinkOrder(order)) {
                // ���Ϊ����ҽ��ϸ�����账��
                if (ifLinkOrderSubItem(order)) continue;
                String finalOrder = getLinkOrder(order, parm);
                result.addData("ORDER_DESC", finalOrder);
            } else { // ��ͨҽ��
                String drNote = order.getValue("DR_NOTE");
                String desc = order.getValue("ORDER_DESC");
                String qty = order.getValue("MEDI_QTY") + "";
                String unit = order.getValue("UNIT_CHN_DESC");
                String freq = order.getValue("FREQ_CODE");
                String dose = order.getValue("ROUTE_CODE");
                String cat1 = order.getValue("CAT1_TYPE");
                // �ж��Ƿ���ҽ����ע
                if (order.getBoolean("IS_REMARK")) {
                    desc = drNote.length() != 0 ? drNote : desc;
                    drNote = "";
                    qty = "";
                    unit = "";
                    freq = "";
                    dose = "";
                }
                // �����ҽ���Ƿ�PHA
                if ((!checkOrderCat1(cat1)) && chackFreq(freq)) {
                    qty = "";
                    unit = "";
                    freq = "";
                    dose = "";
                }
                String finalDesc = "";
                if (desc.startsWith("*")) finalDesc = desc;
                else finalDesc =
                        ""
                                + desc
                                + "\r"
                                + qty
                                + " "
                                + unit
                                + " "
                                + freq
                                + " "
                                + getRouteDesc(dose)
                                + (drNote.length() != 0 ? "\r" + "(" + drNote
                                        + ")" : "");
                // ��Ҫ����--ҽ��
                result.addData("ORDER_DESC", finalDesc);
            }
            result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
            result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
            result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
            result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
            result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
        }
        result.setCount(result.getCount("EFF_DATE_DAY"));
        return result;
    }

    /**
     * �õ��÷��ֵ�
     * @param dose
     * @return
     */
    private String getRouteDesc(String dose) {
        TParm parm =
                new TParm(TJDODBTool.getInstance().select(
                                                          "SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
                                                                  + dose + "'"));
        if (parm.getCount() <= 0) return "";
        return parm.getValue("ROUTE_CHN_DESC", 0);
    }

    /**
     * ��øò��˳���ҽ��
     * 
     * @return TParm
     */
    private TParm getUDOrderParm() {
        TParm UDparm = new TParm(TJDODBTool.getInstance().select(this.getSelectSQL("UD")));
        TParm printData = arrangeData(UDparm, "UD");
        return printData;
    }

    /**
     * ��øò�����ʱҽ��
     * 
     * @return TParm
     */
    private TParm getSTOrderParm() {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(this.getSelectSQL("ST")));
        TParm printData = arrangeData(STparm, "ST");
        return printData;
    }

    /**
     * ��øò��˳�Ժ��ҩҽ��
     * 
     * @return TParm
     */
    private TParm getDSOrderParm() {
        TParm STparm = new TParm(TJDODBTool.getInstance().select(this.getSelectSQL("DS")));
        TParm printData = arrangeData(STparm, "DS");
        return printData;
    }

    /**
     * ��������
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    private TParm arrangeData(TParm parm, String flg) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm order = parm.getRow(i);
            // �ж�����ҽ��
            if (ifLinkOrder(order)) {
                // ���Ϊ����ҽ��ϸ�����账��
                if (ifLinkOrderSubItem(order)) continue;
                String finalOrder = getLinkOrder(order, parm);
                result.addData("ORDER_DESC", finalOrder);
            } else { // ��ͨҽ��
                String drNote = order.getValue("DR_NOTE");
                String desc = order.getValue("ORDER_DESC");
                String qty = order.getValue("MEDI_QTY") + "";
                String unit = order.getValue("UNIT_CHN_DESC");
                String freq = order.getValue("FREQ_CODE");
                String dose = order.getValue("ROUTE_CODE");
                String cat1 = order.getValue("CAT1_TYPE");
                // �ж��Ƿ���ҽ����ע
                if (order.getBoolean("IS_REMARK")) {
                    desc = drNote.length() != 0 ? drNote : desc;
                    drNote = "";
                    qty = "";
                    unit = "";
                    freq = "";
                    dose = "";
                }
                // �����ҽ���Ƿ�PHA
                if ((!checkOrderCat1(cat1)) && chackFreq(freq)) {
                    qty = "";
                    unit = "";
                    freq = "";
                    dose = "";
                }
                String finalDesc =
                        ""
                                + desc
                                + "\r"
                                + qty
                                + " "
                                + unit
                                + " "
                                + freq
                                + " "
                                + dose
                                + (drNote.length() != 0 ? "\r" + "(" + drNote
                                        + ")" : "");
                // ��Ҫ����--ҽ��
                result.addData("ORDER_DESC", finalDesc);
            }
            // ����ҽ���������ò�ͬ��������
            if ("UD".equals(flg)) {
                result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
                result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
                result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
                result.addData("NS_CHECK_CODE", order.getData("NS_CHECK_CODE"));
                result.addData("DC_DATE_DAY", order.getData("DC_DATE_DAY"));
                result.addData("DC_DATE_TIME", order.getData("DC_DATE_TIME"));
                result.addData("DC_DR_CODE", order.getData("DC_DR_CODE"));
                result.addData("DC_NS_CHECK_CODE", order.getData("DC_NS_CHECK_CODE"));
            } else if ("ST".equals(flg)) { // ��ʱ
                result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
                result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
                result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
                result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
                result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
            } else { // ��Ժ��ҩ
                result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
                result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
                result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
                result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
                result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
            }
        }
        result.setCount(result.getCount("EFF_DATE_DAY"));
        return result;
    }

    /**
     * �жϸ�ҽ���Ƿ���PHA����
     * 
     * @param code
     *            String
     * @return boolean
     */
    private boolean checkOrderCat1(String code) {
        return "PHA".equals(code);
    }

    /**
     * ��PHAҽ�� ��AW1,.,STAT ����ʾƾ��Ƶ�Σ�
     * 
     * @param freq
     *            String
     * @return boolean
     */
    private boolean chackFreq(String freq) {
        return "AW1".equals(freq) || ".".equals(freq) || "STAT".equals(freq);
    }

    /**
     * ��������ҽ��ORDER_DESC
     * 
     * @param order
     *            TParm
     * @param parm
     *            TParm
     * @return String
     */
    private String getLinkOrder(TParm order, TParm parm) {
        String resultDesc = "";
        String mainOrder = order.getValue("ORDER_DESC");
        String mainNote = order.getValue("DR_NOTE");
        String mainmediQty = order.getValue("MEDI_QTY") + "";
        String mainUnit = order.getValue("UNIT_CHN_DESC");
        String mainFreq = order.getValue("FREQ_CODE");
        String mainDose = order.getValue("ROUTE_CODE");
        String mainLinkNo = order.getValue("LINK_NO");
        /* String mainRxKind= order.getValue("RX_KIND"); */
        resultDesc =
                mainOrder
                        + " "
                        + mainmediQty
                        + ""
                        + mainUnit
                        + (mainNote.length() != 0 ? "\r" + "(" + mainNote + ")"
                                : "");
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            String linkNo = parm.getValue("LINK_NO", i);
            /* String rxKind=(String) parm.getData("RX_KIND", i); */
            if (/* rxKind.equals(mainRxKind) && */mainLinkNo.equals(linkNo)
                    && !parm.getBoolean("LINKMAIN_FLG", i)) {
                String subOrder = parm.getValue("ORDER_DESC", i);
                String submediQty = parm.getValue("MEDI_QTY", i) + "";
                String subUnit = parm.getValue("UNIT_CHN_DESC", i);
                String subNote = parm.getValue("DR_NOTE", i);
                resultDesc +=
                        "\r"
                                + subOrder
                                + " "
                                + submediQty
                                + ""
                                + subUnit
                                + (subNote.length() != 0 ? "\r" + "(" + subNote
                                        + ")" : "");
            } else continue;
        }
        resultDesc +=
                "\r     " + mainFreq + " " + getRouteDesc(mainDose) + " " + "�� " + mainLinkNo
                        + " ��";
        return resultDesc;
    }

    private String getSelectSQL(String orderFlg) {
        String sql = "";
        // ��ʱand��Ժ��ҩ
        if ("ST".equals(orderFlg) || "DS".equals(orderFlg)) {
            sql =
                    " SELECT   TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
                            + " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, "
                            + " A.ORDER_DR_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
                            + " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE, "
                            + " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,C.IS_REMARK "
                            + " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B,SYS_FEE C "
                            + " WHERE  A.CASE_NO='" + this.getCaseNo() + "'"
                            + " AND A.CASE_NO=B.CASE_NO (+)" + " AND A.ORDER_NO=B.ORDER_NO (+)"
                            + " AND A.ORDER_SEQ=B.ORDER_SEQ (+)" + " AND A.RX_KIND='" + orderFlg
                            + "' " + " AND A.HIDE_FLG = 'N' "
                            + " AND A.ORDER_CODE=C.ORDER_CODE"
                            + " AND A.MEDI_UNIT = F.UNIT_CODE (+)" + " ORDER BY   A.EFF_DATE ";
        } else {
            sql =
                    " SELECT TO_CHAR(A.EFF_DATE,'MM/DD') AS EFF_DATE_DAY,TO_CHAR(A.EFF_DATE,'HH24:MI') AS EFF_DATE_TIME, "
                            + " A.ORDER_DR_CODE,A.NS_CHECK_CODE,A.ORDER_DESC,A.MEDI_QTY, "
                            + " F.UNIT_CHN_DESC,A.FREQ_CODE,A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO, "
                            + " A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE, "
                            + " TO_CHAR(A.DC_DATE,'MM/DD') AS DC_DATE_DAY,TO_CHAR(A.DC_DATE,'HH24:MI') AS DC_DATE_TIME, "
                            + " A.DC_DR_CODE,A.DC_NS_CHECK_CODE,A.RX_KIND,A.ROUTE_CODE,C.IS_REMARK "
                            + " FROM   ODI_ORDER A,SYS_UNIT F,SYS_FEE C "
                            + " WHERE  A.CASE_NO='"
                            + this.getCaseNo()
                            + "' "
                            + " AND A.RX_KIND='"
                            + orderFlg
                            + "' "
                            + " AND A.HIDE_FLG='N' "
                            + " AND A.MEDI_UNIT=F.UNIT_CODE (+)"
                            + " AND A.ORDER_CODE=C.ORDER_CODE"
                            + " ORDER BY A.EFF_DATE ";
        }
        // System.out.println("ҽ��===sql====��"+sql);
        return sql;
    }

    /**
     * �ж��Ƿ�������ҽ��
     * 
     * @return boolean
     */
    private boolean ifLinkOrder(TParm oneOrder) {
        String LinkNo = oneOrder.getValue("LINK_NO");
        if (LinkNo.length() == 0) return false;
        return true;
    }

    /**
     * �ж��Ƿ�������ҽ������
     * 
     * @return boolean
     */
    private boolean ifLinkOrderSubItem(TParm oneOrder) {
        return !oneOrder.getBoolean("LINKMAIN_FLG");
    }

    /**
     * �ر��¼�
     * 
     * @return boolean
     */
    public boolean onClosing() {
        return true;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getName() {
        return name;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getDept() {
        return dept;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public static void main(String[] args) {
        JavaHisDebug.initClient();
        // JavaHisDebug.initServer();
        // JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("inw\\INWOrderSheetPrtAndPreView.x");
    }
}