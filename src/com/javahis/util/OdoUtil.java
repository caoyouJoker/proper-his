package com.javahis.util;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.odo.OpdOrder;
import jdo.opd.DiagRec;
import jdo.opd.DiagRecList;
import jdo.opd.DrugAllergy;
import jdo.opd.DrugAllergyList;
import jdo.opd.Medhistory;
import jdo.opd.MedhistoryList;
import jdo.opd.ODO;
import jdo.opd.OPDSysParmTool;
import jdo.opd.Order;
import jdo.opd.OrderHistory;
import jdo.opd.OrderHistoryList;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;
import jdo.opd.TotQtyTool;
import jdo.pha.PhaBaseTool;
import jdo.reg.Reg;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.Pat;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSOrderSetDetailTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SYSFeeTool;
import jdo.bil.BILStrike;
import jdo.sys.DeptTool;

/**
 *
 * <p>
 * Title: ODO对象共用类
 *
 * <p>
 * Description: ODO对象共用类
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: javahis
 *
 * @author ehui 20081009
 * @version 1.0
 */
public class OdoUtil {

    private static String chnMedGroupName = "4";
    public static TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
    public static TDataStore SYS_FEE = TIOM_Database.getLocalTable("SYS_FEE");
    private static TParm sysfeeParm = SYS_FEE.getBuffer(SYS_FEE.isFilter() ?
        SYS_FEE.FILTER : SYS_FEE.PRIMARY);
    private static Vector sysFeeCode = (Vector) sysfeeParm.getData("ORDER_CODE");

    private static TParm icdParm = ICD.getBuffer(ICD.isFilter() ? ICD.FILTER :
                                                 ICD.PRIMARY);
    private static Vector icdCode = (Vector) icdParm.getData("ICD_CODE");

    private static final String GET_PHABASE = "SELECT * FROM PHA_BASE";
    private static TDS phaBase;
    /**
     * 根据给定条件返回新增的ORDER的TParm
     * @param odo ODO对象
     * @param groupName String 如存在ORDERLIST，则该ORDERLIST的组名
     * @param indexOfOrderList String PRESCRIPTIONLIST中该ORDERLIST的下标
     * @return TParm 新增ORDER的TParm
     */
    public static TParm newOrderTParm(ODO odo,
                                      String groupName, String indexOfOrderList) {
        OrderList ol;
        int index = TCM_Transform.getInt(indexOfOrderList);
        //PRESCRIPTIONLIST中没有此ORDERLIST
        if (odo.getPrescriptionList().getOrderList(groupName, index) == null) {
            ol = odo.getPrescriptionList().newOrderList(groupName);
        }
        //有此ORDERLIST，在此ORDERLIST中取得最大SEQ_NO，并追加新的ORDER
        else {
            ol = odo.getPrescriptionList().getOrderList(groupName, index);
        }
        Order o = ol.newOrder();
        o.setSeqNo(ol.size());
        TParm parm = new TParm();
        parm = o.getParm();
        //增加折扣金额这一列
        calculatePayAmount(parm);
        return parm;

    }

    /**
     * 根据给定条件返回新增的一行数据的TParm，共4个Order
     * @return TParm 新增ORDER的TParm
     * @param ol OrderList
     * @return TParm
     */
    public static TParm newOrderTParmChn(OrderList ol) {
        if (ol == null)
            return null;
        Order o;
        //中药饮片一行数据就有4条医嘱
        for (int i = 0; i < 4; i++) {
            o = ol.newOrder();
        }
        TParm result = chnMedicReArrange(ol.getParm());
        return result;
    }

    /**
     *
     * @param odo ODO
     * @param indexOfOrderList String
     */
    public static void deleteOrderChn(ODO odo, String indexOfOrderList) {

    }

    /**
     * 计算折扣金额
     * @param parm TParm
     * @return TParm
     */
    public static TParm calculatePayAmount(TParm parm) {
        int count = parm.getCount();
        if (count == -1) {
            double discountAmount = parm.getDouble("OWN_AMT") -
                parm.getDouble("AR_AMT");
            if (discountAmount == -0.0)
                discountAmount = 0.0;
            parm.setData("PAYAMOUNT", discountAmount);
            return parm;
        }
        for (int i = 0; i < count; i++) {
            double discountAmount = parm.getDouble("OWN_AMT", i) -
                parm.getDouble("AR_AMT", i);
            if (discountAmount == -0.0)
                discountAmount = 0.0;
            parm.addData("PAYAMOUNT", discountAmount);
        }
        return parm;
    }

    /**
     * 根据如下参数查询并实例化ODO对象
     * @param caseNo String
     * @param mrNo String
     * @param admType String
     * @return ODO
     */
    public static ODO queryByCaseNO(String caseNo, String mrNo, String admType) {
        ODO odo = new ODO();
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            System.out.println("pat is null");
            return null;
        }

        Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
        if (reg == null) {
            System.out.println("reg is null");

            return null;
        }

        odo.setPat(pat);
        odo.setReg(reg);
        if (!odo.onQuery(caseNo, admType)) {

            return null;
        }
        odo.setAdmType(admType);
        return odo;
    }

    /**
     * 为RX_NO控件准备显示用数据
     * @param odo ODO
     * @param groupName String
     * @return TParm
     */
    public static TParm prepareRxNoTParm(ODO odo, String groupName) {
        Vector prescrption = odo.getPrescriptionList().getGroup(groupName);
        if (prescrption == null || prescrption.size() == 0)
            return null;
        OrderList ol = new OrderList();
        String drName = "";
        TParm rxparm = new TParm();
        int orignalrows = prescrption.size();
        for (int i = 0; i < orignalrows; i++) {
            ol = (OrderList) prescrption.get(i);
            drName = OperatorTool.getInstance().getOperatorName(ol.getDrCode());
            rxparm.addData("ID", ol.getPresrtNo());
            rxparm.addData("TEXT", "【" + ol.getPresrtNo() + "】" + drName);
        }
        rxparm.setData("ACTION", "COUNT", orignalrows);
        return rxparm;
    }

    /**
     * 为新增功能检查数据主键是否全，并删除无用数据，ODO保存使用
     * @param odo ODO
     * @return boolean
     */
    public static boolean checkDataForOdo(ODO odo) {
        if (!checkDataForOrder(odo)) {
            return false;
        }
        if (!checkDataForMedHistory(odo.getMedhistoryList())) {
            return false;
        }
        if (!checkDataForDrugAllergy(odo.getDrugAllergyList())) {
            return false;
        }
        return true;
    }

    /**
     * 为新增功能检查数据主键是否全，既往史保存使用
     * @param mhl MedhistoryList
     * @return boolean
     */
    public static boolean checkDataForMedHistory(MedhistoryList mhl) {
        if (mhl == null) {
            return false;
        }
        int mhlCount = 0;
        if (mhl != null) {
            mhlCount = mhl.size();
        }

        for (int i = mhlCount - 1; i >= 0; i--) {
            if (i == -1)
                return false;
            Medhistory mh = mhl.getMedhistory(i);
            if (mh == null)
                continue;
            if (StringUtil.isNullString(mh.getIcdCode())) {
                mhl.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(mh.getIcdType())) {
                mhl.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(mh.getAdmDate())) {
                mhl.removeData(i);
                continue;
            }
        }
        return true;
    }

    /**
     * 为新增功能检查数据主键是否全，过敏史保存使用
     * @param dal DrugAllergyList
     * @return boolean
     */
    public static boolean checkDataForDrugAllergy(DrugAllergyList dal) {
        if (dal == null) {
            return false;
        }
        int dalCount = 0;
        if (dal != null) {
            dalCount = dal.size();
        }
        for (int i = dalCount - 1; i >= 0; i--) {
            if (i == -1)
                return false;
            DrugAllergy da = dal.getDrugAllergy(i);
            if (da == null)
                continue;
            if (StringUtil.isNullString(da.getMrNo())) {
                dal.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(da.getAdmDate())) {
                dal.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(da.getDrugType())) {
                dal.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(da.getDrugOringrdCode())) {
                dal.removeData(i);
                continue;
            }
        }
        return true;
    }

    /**
     * 为新增功能检查数据主键是否全，诊断保存使用
     * @param drl DiagRecList
     * @return boolean
     */
    public static boolean checkDataForDiagRec(DiagRecList drl) {
        if (drl == null) {
            return false;
        }
        boolean mainDiag = false;
        int drlCount = 0;
        String maindiagFlg = "";
        if (drl != null) {
            drlCount = drl.size();
        }
        for (int i = drlCount - 1; i >= 0; i--) {

            if (i == -1)
                return false;
            DiagRec dr = drl.getDiagRec(i);
            if (dr == null) {
                continue;
            }
            if (StringUtil.isNullString(dr.getIcdType())) {
                drl.removeData(i);
                continue;
            }
            if (StringUtil.isNullString(dr.getIcdCode())) {
                drl.removeData(i);
                continue;
            }
            maindiagFlg = dr.getMainDiagFlg();
            if (!StringUtil.isNullString(maindiagFlg) &&
                "Y".equalsIgnoreCase(maindiagFlg)) {
                mainDiag = true;
            }
        }
        drlCount = drl.size();
        if (!mainDiag && drlCount >= 0) {
            return false;
        }
        return true;
    }

    /**
     * 为新增功能检查数据主键是否全，Order保存使用
     * @param odo ODO
     * @return boolean Y:真，数据完整，可以送数据库新增，N：假，数据主键不全
     */
    public static boolean checkDataForOrder(ODO odo) {
        if (odo == null)
            return false;
        PrescriptionList pl = odo.getPrescriptionList();
        String[] groupName = pl.getGroupNames();
        OrderList ol = new OrderList();

        Order o = new Order();
        int orderListCount = groupName.length;
        int orderCount = 0;

        for (int i = 0; i < orderListCount; i++) {
            orderCount = pl.getGroupPrsptSize(groupName[i]);
            for (int j = 0; j < orderCount; j++) {
                ol = pl.getOrderList(groupName[i], j);
                //同一张处方签的执行科室要一致
                String rbdept = ol.getExecDeptCode();
                int order = ol.size();
                for (int x = order - 1; x >= 0; x--) {
                    if (x == -1) {
                        return false;
                    }

                    o = ol.getOrder(x);

                    if (o == null) {
                        continue;
                    }

                    if (StringUtil.isNullString(o.getOrderCode())) {
                        ol.removeData(x);
                        continue;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 删除前判断是否现在的操作人员与开立此记录的人员相同，如同则返回Y，如不同则返回N，不允许删除
     * @param drl DiagRecList
     * @param drCode String
     * @return boolean Y:真，N：假
     */
    public static boolean checkDiagRecForDelete(DiagRecList drl, String drCode) {
        if (drl == null) {
            return false;
        }
        int drlCount = 0;
        if (drl != null) {
            drlCount = drl.size();
        }
        String thisdrCode = "";
        boolean allow = true;
        for (int i = 0; i < drlCount; i++) {
            DiagRec dr = drl.getDiagRec(i);
            thisdrCode = dr.getDrCode();
            if (StringUtil.isNullString(thisdrCode) &&
                (!thisdrCode.equalsIgnoreCase(drCode))) {
                allow = false;
            }
        }
        if (!allow) {
            return false;
        }
        return true;
    }

    /**
     * 判断某处方签是否已经计价
     * @param odo ODO
     * @return boolean
     */
    public static boolean isChargedOrder(ODO odo) {

        Order o = new Order();
        String bilflg = "";
        PrescriptionList pl = odo.getPrescriptionList();
        String[] groupName = pl.getGroupNames();
        OrderList ol = new OrderList();
        int orderListCount = groupName.length;
        int orderCount = 0;
        for (int i = 0; i < orderListCount; i++) {
            orderCount = pl.getGroupPrsptSize(groupName[i]);
            for (int j = 0; j < orderCount; j++) {
                ol = pl.getOrderList(groupName[i], j);
                int order = ol.size();
                for (int x = 0; x < order; x++) {
                    o = ol.getOrder(x);
                    bilflg = o.getBillFlg();
                    if (StringUtil.isNullString(bilflg) &&
                        "Y".equalsIgnoreCase(bilflg)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 判断某条医嘱是否以计价
     * @param order Order
     * @return boolean
     */
    public static boolean isChargedOrder(Order order) {
        if (order == null)
            return true;
        String billflg = TCM_Transform.getString(order.getBillFlg());
        if ("Y".equalsIgnoreCase(billflg))
            return true;
        return false;
    }

    /**
     * 根据给定的处方类型和下标删除整张OrderList
     * @param ol OrderList
     * @return boolean
     */
    public static boolean deleteOrderList(OrderList ol) {
        if (ol == null)
            return false;
        int count = ol.size();
        Order o = new Order();
        for (int i = 0; i < count; i++) {
            o = ol.getOrder(i);
            if (o == null)
                continue;
            if (OdoUtil.isChargedOrder(o)) {
                return false;
            }
        }
        ol.removeDataAll();
        return true;
    }

    /**
     * 给入TABLE的行列号返回该行列属于哪个Order
     * @param row TABLE 中的行号
     * @param column TABLE中的列号
     * @return OrderList中的下标
     */
    public static int calculateOrderIndexForChnMed(int row, int column) {
        column = column / 3;
        row = row * 4;
        return row + column;
    }

    /**
     * 为界面TABLE显示用调整中药的TPARM
     * @param parm TParm
     * @return TParm
     */
    public static TParm chnMedicReArrange(TParm parm) {
        if (parm == null)
            return parm;
        TParm result = new TParm();
        int index = parm.getCount();
        for (int j = 0; j < index; j++) {
            int mod = j % 4 + 1;
            String orderCode = TCM_Transform.getString(parm.getData(
                "ORDER_DESC", j));
            result.setData("ORDER_CODE" + mod, j, orderCode);
            result.setData("TAKE_QTY" + mod, j, parm.getData("MEDI_QTY", j));
            result.setData("TOT_QTY" + mod, j, parm.getData("DOSAGE_QTY", j));
            result.setData("DCTEXCEP_CODE" + mod, j,
                           parm.getData("DCTEXCEP_CODE", j));
        }
        return result;
    }

    /**
     * 删除Order数据时，将数据准备好，放入OrderHistory中
     * @param odo ODO 对象
     * @return 真：准备成功，假：准备失败
     */
    public static boolean prepareOrderHistory(ODO odo) {
        if (odo == null)
            return false;
        Order o = new Order();
        PrescriptionList pl = odo.getPrescriptionList();
        String[] groupName = pl.getGroupNames();
        OrderList ol = new OrderList();
        OrderHistoryList ohl = new OrderHistoryList();
        odo.setOrderhistoryList(ohl);
        OrderHistory oh = new OrderHistory();
        int orderListCount = groupName.length;
        int orderCount = 0;
        for (int i = 0; i < orderListCount; i++) {
            orderCount = pl.getGroupPrsptSize(groupName[i]);
            for (int j = 0; j < orderCount; j++) {
                ol = pl.getOrderList(groupName[i], j);
                int order = ol.size();
                for (int x = 0; x < order; x++) {
                    o = ol.getOrder(x);
                    oh = ohl.newOrderHistory();

                    transferOrderToOrderHistory(o, oh);
                }
            }
        }
//		odo.setOrderhistoryList(ohl);
        return false;
    }

    public static void transferOrderToOrderHistory(Order o, OrderHistory oh) {
        if (o == null || oh == null)
            return;
        oh.setCaseNo(o.getCaseNo());
        oh.setRxNo(o.getRxNo());
        oh.setSeqNo(o.getSeqNo());
        oh.setPresrtNo(o.getPresrtNo());
        oh.setRegionCode(o.getRegionCode());
        oh.setMrNo(o.getMrNo());
        oh.setAdmType(o.getAdmType());
        oh.setRxType(o.getRxType());
        oh.setReleaseFlg(o.getReleaseFlg());
        oh.setLinkmainFlg(o.getLinkmainFlg());
//		oh.setLinkNo(o.getLinkNo());
        oh.setOrderCode(o.getOrderCode());
        oh.setOrderDesc(o.getOrderDesc());
        oh.setGoodsDesc(o.getGoodsDesc());
//		oh.setOrderCat1(o.getOrderCat1());
//		oh.setTakeQty(o.getTakeQty());
        oh.setMediUnit(o.getMediUnit());
        oh.setFreqCode(o.getFreqCode());
        oh.setRouteCode(o.getRouteCode());
        oh.setTakeDays(o.getTakeDays());
//		oh.setTotQty(o.getTotQty());
//		oh.setDgtTot(o.getDgtTot());
        oh.setDispenseUnit(o.getDispenseUnit());
//		oh.setOpdgiveboxFlg(o.getGiveboxFlg());
        oh.setOwnPrice(o.getOwnPrice());
        oh.setNhiPrice(o.getNhiPrice());
//		oh.setDiscnrate(o.getDiscnrate());
        oh.setOwnAmt(o.getOwnAmt());
        oh.setTotAmt(o.getArAmt());
        oh.setDescription(o.getDrNote());
        oh.setNsNote(o.getNsNote());
        oh.setDrCode(o.getDrCode());
        oh.setOrderDate(o.getOrderDate());
        oh.setDeptCode(o.getDeptCode());
        oh.setRborderDeptCode(o.getExecDeptCode());
        oh.setSetmainFlg(o.getSetmainFlg());
        oh.setOrdsetGroupNo(o.getOrderSetGroupNo());
        oh.setOrdersetCode(o.getOrdersetCode());
        oh.setHideFlg(o.getHideFlg());
        oh.setRpttypeCode(o.getRpttypeCode());
        oh.setOptitemCode(o.getOptitemCode());
        oh.setDevCode(o.getDevCode());
        oh.setMrCode(o.getMrCode());
        oh.setFileNo(o.getFileNo());
        oh.setDegreeCode(o.getDegreeCode());
        oh.setUrgentFlg(o.getUrgentFlg());
        oh.setInspayType(o.getInspayType());
        oh.setPhaType(o.getPhaType());
        oh.setDoseType(o.getDoseType());
        oh.setExpensiveFlg(o.getExpensiveFlg());
        oh.setPrinttypeflgInfant(o.getPrinttypeflgInfant());
        oh.setCtrldrugclassCode(o.getCtrldrugclassCode());
        oh.setDctagentCode(o.getDctagentCode());
        oh.setDctexcepCode(o.getDctexcepCode());
        oh.setDctTakeQty(o.getDctTakeQty());
        oh.setPackageTot(o.getPackageTot());

    }

    /**
     * 根据病患生日和传入的截至日期，计算病人年龄并根据是否为儿童以不同的形式显示年龄，如是儿童则显示X岁X月X日，如成人则显示x岁
     * @param birthday Timestamp
     * @param t Timestamp
     * @return String
     */
    public static String showAge(Timestamp birthday, Timestamp t) {
        String age = "";
        String[] res;
        if (birthday == null)
            return age;
        if (t == null)
            return age;
        res = StringTool.CountAgeByTimestamp(birthday, t);
        if (OPDSysParmTool.getInstance().isChild(birthday, t)) {
            if ("en".equalsIgnoreCase(Operator.getLanguage())) {
                age = res[0] + "Y" + res[1] + "M" + res[2] + "D";
            }
            else {
                age = res[0] + "岁" + res[1] + "月" + res[2] + "日";
            }

        }
        else {
            if ("en".equalsIgnoreCase(Operator.getLanguage())) {
                age = res[0] + "Y";
            }
            else {
                age = res[0] + "岁";
            }

        }
        return age;
    }

    /**
     * 根据病患生日和传入的截至日期，计算病人年龄并根据是否为儿童以不同的形式显示年龄，如是儿童则显示X岁X月X日，如成人则显示x岁(英文版)
     * @param birthday Timestamp
     * @param t Timestamp
     * @return String
     */
    public static String showEngAge(Timestamp birthday, Timestamp t) {

        String age = "";
        String[] res;
        if (birthday == null)
            return age;
        if (t == null)
            return age;
        res = StringTool.CountAgeByTimestamp(birthday, t);
        if (OPDSysParmTool.getInstance().isChild(birthday, t)) {//wanglong modify 20150119
            age = res[0] + "Y" + res[1] + "M" + res[2] + "D";
        }
        else {
            age = res[0] + "Y";
        }
        return age;

    }

    /**
     * 根据病患资料的LMP计算得到欲产期及怀孕周数
     * @param lmp Timestamp
     * @return Timestamp
     */
    public Timestamp getDate(Timestamp lmp) {
        if (lmp == null) {
            return null;
        }
        String lmpdate = StringTool.getString(lmp, "yyyyMMdd");
        /*预产期=LMP+9个月+7天*/
        String L_year = String.valueOf(lmpdate.substring(0, 4));
        String L_month = String.valueOf(lmpdate.substring(4, 6));
        String L_day = String.valueOf(lmpdate.substring(6, 8));
        /*先判断月份+9后是否大于12个月*/
        L_month = String.valueOf(Integer.parseInt(L_month) + 9);
        if (Integer.parseInt(L_month) > 12) {
            L_year = String.valueOf(Integer.parseInt(L_year) + 1);
            L_month = String.valueOf(Integer.parseInt(L_month) - 12);
        }
        /**
         * 判断日+7后是否大于该月份的最大天数，否则月份变成下一月份
         */
        L_day = String.valueOf(Integer.parseInt(L_day) + 7);
        /**
         * 是否为闰年
         */
        if (StringTool.IsLeapYear(L_year)) {
            if (L_month.equals("2") || L_month.equals("02")) {
                if (Integer.parseInt(L_day) > 29) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 29);
                }
            }
        }
        else {
            if (L_month.equals("2") || L_month.equals("02")) {
                if (Integer.parseInt(L_day) > 28) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 28);
                }
            }
        }
        if (!L_month.equals("2") && !L_month.equals("02")) {
            if (L_month.equals("1") || L_month.equals("3") ||
                L_month.equals("5") ||
                L_month.equals("7") ||
                L_month.equals("8") || L_month.equals("10") ||
                L_month.equals("12")) {
                if (Integer.parseInt(L_day) > 31) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 31);
                }
                else {}
            }
            else {
                if (Integer.parseInt(L_day) > 30) {
                    L_month = String.valueOf(Integer.parseInt(L_month) + 1);
                    L_day = String.valueOf(Integer.parseInt(L_day) - 30);
                }
            }
        }
        if (Integer.parseInt(L_month) > 12) {
            L_year = String.valueOf(Integer.parseInt(L_year) + 1);
            L_month = String.valueOf(Integer.parseInt(L_month) - 12);
        }

        if (L_month.length() < 2)
            L_month = "0" + L_month;
        if (L_day.length() < 2)
            L_day = "0" + L_day;
        String MedicineDate = String.valueOf(L_year + L_month + L_day);
        return StringTool.getTimestamp(MedicineDate, "yyyyMMdd");
    }

    /**
     * 计算怀孕周数
     * @param t1 就诊日期
     * @param t2 LMP
     * @return week int 怀孕周数
     */
    public static int getPreWeek(Timestamp t1, Timestamp t2) {
        /*自动计算怀孕周数=强制进位((就诊日期-LMP+1)/7)*/
        if (t1 == null || t2 == null) {
            return 0;
        }
        String date = t1.toString().trim().replaceAll("-", "");
        date = date.substring(0, date.indexOf(" "));
        float Medicinecount = (Float.parseFloat(String.valueOf(StringTool.
            getDateDiffer(t1, t2) + 1))) / 7;
        BigDecimal bd = new BigDecimal(Medicinecount);
        if (bd.signum() == -1) {
            bd = bd.negate();
        }
        int IntgerValue = bd.intValue();
        if (bd.scale() > 0)
            IntgerValue++;
        return IntgerValue; /*怀孕周数*/
    }

    /**
     * 返回查询完频次，途径，单位后并将这些指写入order对象中的order
     * @param order Order
     * @param sysFee TParm
     * @param ctz String[]
     * @return Order
     */
    public static Order fillOrder(Order order, TParm sysFee, String[] ctz) {
//        System.out.println("医嘱细分类群组1111111111111"+sysFee.getValue("CAT1_TYPE"));
        order.modifyOrderCode(sysFee.getValue("ORDER_CODE"));
        order.modifyOrderDesc(sysFee.getValue("ORDER_DESC").replaceFirst("(" +
            sysFee.getValue("SPECIFICATION") + ")", ""));
        order.modifyGoodsDesc(sysFee.getValue("GOODS_DESC"));
        order.modifyExpensiveFlg(sysFee.getValue("EXPENSIVE_FLG"));
        order.modifyOrderCat1Code(sysFee.getValue("ORDER_CAT1_CODE"));
        order.modifyCat1Type(sysFee.getValue("CAT1_TYPE"));
        order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue("OWN_PRICE")));
        order.modifySpecification(sysFee.getValue("SPECIFICATION"));
        order.modifySetmainFlg("N");
        String cat1Code = sysFee.getValue("ORDER_CAT1_CODE");
        if (!StringUtil.isNullString(cat1Code) && cat1Code.contains("PHA")) {
            TParm parm = PhaBaseTool.getInstance().selectByOrder(order.
                getOrderCode());
            if (parm.getErrCode() < 0) {
                System.out.println("phaBase is wrong");
                return order;
            }
            order.modifyPhaType(parm.getValue("PHA_TYPE", 0));
            order.modifyFreqCode(parm.getValue("FREQ_CODE", 0));
            order.modifyRouteCode(parm.getValue("ROUTE_CODE", 0));
            order.modifyMediUnit(parm.getValue("MEDI_UNIT", 0));
            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            order.modifyGiveboxFlg(parm.getValue("GIVEBOX_FLG", 0));
            if ("Y".equalsIgnoreCase(order.getGiveboxFlg())) {
                order.modifyDosageUnit(parm.getValue("STOCK_UNIT", 0));
                order.modifyDispenseUnit(order.getDosageUnit());
                order.modifyMediUnit(order.getDosageUnit());
            }
            else {
                order.modifyDosageUnit(parm.getValue("DOSAGE_UNIT", 0));
                order.modifyDispenseUnit(order.getDosageUnit());
                order.modifyMediUnit(order.getDosageUnit());
            }

            //TAKE_QTY
            String TAKE_QTY = parm.getValue("MEDI_QTY", 0);
            double takeQty = (TAKE_QTY == null || TAKE_QTY.trim().length() < 1 ||
                              "0.0".equalsIgnoreCase(TAKE_QTY)) ? 1.0 :
                Double.valueOf(TAKE_QTY);
            order.modifyMediQty(takeQty);
            String TAKE_DAY = parm.getValue("TAKE_DAYS", 0);
            int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 ||
                            "0".equalsIgnoreCase(TAKE_DAY)) ? 1 :
                Integer.valueOf(TAKE_DAY);
            order.modifyTakeDays(takedays);
            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            order.modifyGiveboxFlg(parm.getValue("GIVEBOX_FLG", 0));
            order.modifyCtz1Code(ctz[0]);
            order.modifyCtz2Code(ctz[1]);
            order.modifyCtz3Code(ctz[2]);
            OdoUtil.calcuQty(order);
            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
                                                order.getDosageQty(), 2));
            order.modifyArAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty()) <= 0 ? 0.00 :
                              BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty())
                );
            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
                "CHARGE_HOSP_CODE"), "O"));
            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
        }
        else {
            order.modifyDosageQty(1.0);
            order.modifyDispenseQty(1.0);
            order.modifyMediQty(1.0);
            order.modifyFreqCode("STAT");
            order.modifyMediUnit(sysFee.getValue("UNIT_CODE"));
            order.modifyDosageUnit(sysFee.getValue("UNIT_CODE"));
            order.modifyDispenseUnit(sysFee.getValue("UNIT_CODE"));
            order.modifyTakeDays(1);
            order.modifyCtz1Code(ctz[0]);
            order.modifyCtz2Code(ctz[1]);
            order.modifyCtz3Code(ctz[2]);
            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
                                                order.getDosageQty(), 2));
            order.modifyArAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty()) < 0 ? 0.00 :
                              BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                                               order.getOrderCode(), order
                                               .getDosageQty())
                );

            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
                "CHARGE_HOSP_CODE"), "O"));

            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
        }

        return order;
    }
    /**
     * =====pangben 2013-11-26 添加flg参数 门诊收费界面手术套餐回传参数使用
     * @param order
     * @param sysFee
     * @param ctz
     * @param Level
     * @param flg :true 正常传回  false:手术套餐传回
     * @return
     */
    public static Order fillOrder(Order order, TParm sysFee, String[] ctz,
    		double dosage_qty,String dosage_unit, String Level,boolean flg) {
        order.modifyOrderCode(sysFee.getValue("ORDER_CODE"));
        order.modifyOrderDesc(sysFee.getValue("ORDER_DESC").replaceFirst("(" +
            sysFee.getValue("SPECIFICATION") + ")", ""));
        order.modifyGoodsDesc(sysFee.getValue("GOODS_DESC"));
        order.modifyExpensiveFlg(sysFee.getValue("EXPENSIVE_FLG"));
        order.modifyOrderCat1Code(sysFee.getValue("ORDER_CAT1_CODE"));
        order.modifyRxType("0");
        order.modifyCat1Type(sysFee.getValue("CAT1_TYPE"));
        if(!"PHA".equals(sysFee.getValue("CAT1_TYPE"))){
            String deptCode = Operator.getDept();
            TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
            //执行科室
            if (deptParm.getCount("DEPT_CODE") > 0)
                deptCode = Operator.getDept();
            else
                deptCode = "";
            order.modifyExecDeptCode(sysFee.getValue("EXEC_DEPT_CODE")==null||sysFee.getValue("EXEC_DEPT_CODE").length()==0?deptCode:sysFee.getValue("EXEC_DEPT_CODE"));
            order.modifyFreqCode("STAT");
        }
        if ("2".equals(Level)) {
            order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue(
                "OWN_PRICE2")));

        }
        else if ("3".equals(Level)) {
            order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue(
                "OWN_PRICE3")));
        }
        else
            order.modifyOwnPrice(StringTool.getDouble(sysFee.getValue(
                "OWN_PRICE")));
        order.modifySpecification(sysFee.getValue("SPECIFICATION"));
        order.modifySetmainFlg("N");
//        if (!StringUtil.isNullString(cat1Code) && cat1Code.contains("PHA")) {
            TParm parm = PhaBaseTool.getInstance().selectByOrder(order.
                getOrderCode());
            if (parm.getErrCode() < 0) {
                System.out.println("phaBase is wrong");
                return order;
            }
            order.modifyPhaType(parm.getValue("PHA_TYPE", 0));
            order.modifyRouteCode(parm.getValue("ROUTE_CODE", 0));
            if (parm.getValue("MEDI_UNIT", 0) == null ||
                parm.getValue("MEDI_UNIT", 0).length() == 0)
                order.modifyMediUnit(sysFee.getValue("UNIT_CODE"));
            else
                order.modifyMediUnit(parm.getValue("MEDI_UNIT", 0));


            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            //========pangben 2013-11-26 门诊收费界面没有盒发药添加回传默认
			order.modifyGiveboxFlg("N");
            if ("Y".equalsIgnoreCase(order.getGiveboxFlg())) {
                order.modifyDosageUnit(parm.getValue("DOSAGE_UNIT", 0));//===pangben 2013-8-30 修改盒装药单位
                order.modifyDispenseUnit(parm.getValue("STOCK_UNIT", 0));//===pangben 2013-8-30 修改盒装药单位
            }
            else {
            	if (flg) {
            		 if (parm.getValue("DOSAGE_UNIT", 0) == null ||
                             parm.getValue("DOSAGE_UNIT", 0).length() == 0)
                             order.modifyDosageUnit(sysFee.getValue("UNIT_CODE"));
                         else
                             order.modifyDosageUnit(parm.getValue("DOSAGE_UNIT", 0));
				}else{
					order.modifyDosageUnit(dosage_unit);
				}
               
                order.modifyDispenseUnit(order.getDosageUnit());
            }

            //TAKE_QTY
            String TAKE_QTY = parm.getValue("MEDI_QTY", 0);
            double takeQty = (TAKE_QTY == null || TAKE_QTY.trim().length() < 1 ||
                              "0.0".equalsIgnoreCase(TAKE_QTY)) ? 1.0 :
                Double.valueOf(TAKE_QTY);
            order.modifyMediQty(takeQty);
            String TAKE_DAY = parm.getValue("TAKE_DAYS", 0);
            int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 ||
                            "0".equalsIgnoreCase(TAKE_DAY)) ? 1 :
                Integer.valueOf(TAKE_DAY);
            order.modifyTakeDays(takedays);
            order.modifyCtrldrugclassCode(parm.getValue("CTRLDRUGCLASS_CODE", 0));
            //order.modifyGiveboxFlg(parm.getValue("GIVEBOX_FLG", 0));
            order.modifyCtz1Code(ctz[0]);
            order.modifyCtz2Code(ctz[1]);
            order.modifyCtz3Code(ctz[2]);
            OdoUtil.calcuQty(order);
            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
                                                order.getDosageQty(), 2));
            double ctzRate=BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
                    order.getOrderCode(), order.getDosageQty(), Level);
            order.modifyArAmt(ctzRate<= 0 ?0.00 : ctzRate);
            //=====pangben 2013-8-30 修改折扣率添加
            double d[] = BILStrike.getInstance().chargeC(ctz[0],ctz[1], ctz[2],order.getOrderCode(),
            		sysFee.getValue("CHARGE_HOSP_CODE"),Level);
            order.modifyDiscountRate(d[1]);
            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
                "CHARGE_HOSP_CODE"), "O"));
            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
//        }
//        else {
//            order.modifyDosageQty(1.0);
//            order.modifyDispenseQty(1.0);
//            order.modifyMediQty(1.0);
//            order.modifyFreqCode("STAT");
//            order.modifyMediUnit(sysFee.getValue("UNIT_CODE"));
//            order.modifyDosageUnit(sysFee.getValue("UNIT_CODE"));
//            order.modifyDispenseUnit(sysFee.getValue("UNIT_CODE"));
//            order.modifyTakeDays(1);
//            order.modifyCtz1Code(ctz[0]);
//            order.modifyCtz2Code(ctz[1]);
//            order.modifyCtz3Code(ctz[2]);
//            order.modifyOwnAmt(StringTool.round(order.getOwnPrice() *
//                                                order.getDosageQty(), 2));
//            order.modifyArAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
//                                               order.getOrderCode(), order
//                                               .getDosageQty(), Level) <= 0 ?
//                              0.00 : BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
//                order.getOrderCode(), order
//                .getDosageQty(), Level)
//                );
//
//            order.modifyRexpCode(BIL.getRexpCode(sysFee.getValue(
//                "CHARGE_HOSP_CODE"), "O"));
//
//            order.modifyHexpCode(sysFee.getValue("CHARGE_HOSP_CODE"));
//        }
        return order;
    }

    /**
     * 设置医嘱与连接医嘱主项的频次，执行科室，日份，频次相同
     * @param o Order
     * @param orderMain Order
     * @param linkno int
     * @return Order
     */
    public static Order linkOrder(Order o, Order orderMain, int linkno) {
        o.modifyRouteCode(orderMain.getRouteCode());
        o.modifyExecDeptCode(orderMain.getExecDeptCode());
        o.modifyTakeDays(orderMain.getTakeDays());
        o.modifyFreqCode(orderMain.getFreqCode());
        o.modifyLinkNo(TCM_Transform.getString(linkno));
        return o;
    }

    /**
     *
     * @param o 传入Order
     * @return 更新完总量数据的Order
     */
    public static Order calcuQty(Order o) {
        TotQtyTool t = new TotQtyTool();
        TParm orderParm = o.getParm();
        String orderCode = orderParm.getValue("ORDER_CODE");
        TParm sysParm = SYSFeeTool.getInstance().getCat1Code(orderCode);
        orderParm.setData("CAT1_TYPE", sysParm.getData("CAT1_TYPE", 0));
//        System.out.println("计算总量进参"+o.getParm());
        TParm qty = t.getTotQty(orderParm);
//        System.out.println("计算总量出参"+qty);
        //System.out.println("------>if " + "Y".equalsIgnoreCase(o.getGiveboxFlg()));
        if ("Y".equalsIgnoreCase(o.getGiveboxFlg())) {
            o.modifyDispenseQty(StringTool.getDouble(TCM_Transform.getString(
                qty.getData("QTY_FOR_STOCK_UNIT"))));
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("TOT_QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "STOCK_UNIT")));
        }
        else {
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "DOSAGE_UNIT")));
            o.modifyDispenseQty(o.getDosageQty());
        }
//        System.out.println("总量"+o.getDosageQty());
//        System.out.println("自费总价"+StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
//        System.out.println("总价"+BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
//                                       o.getCtz3Code(), o.getOrderCode(), o
//                                       .getDosageQty()));
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
                                       o.getCtz3Code(), o.getOrderCode(), o
                                       .getDosageQty()));
//        System.out.println("返回总价"+o.getArAmt());
        return o;
    }

    public static Order calcuQty(Order o, String level) {
        TotQtyTool t = new TotQtyTool();
        TParm parm = PhaBaseTool.getInstance().selectByOrder(o.
            getOrderCode());
        if (parm.getErrCode() < 0) {
            System.out.println("phaBase is wrong");
            return o;
        }
        TParm orderParm = o.getParm();
        String orderCode = orderParm.getValue("ORDER_CODE");
        TParm sysParm = SYSFeeTool.getInstance().getCat1Code(orderCode);
        orderParm.setData("CAT1_TYPE", sysParm.getData("CAT1_TYPE", 0));
        orderParm.setData("PHA_TYPE", parm.getData("PHA_TYPE", 0));
        TParm qty = t.getTotQty(orderParm);

        if ("Y".equalsIgnoreCase(o.getGiveboxFlg())) {
            o.modifyDispenseQty(StringTool.getDouble(TCM_Transform.getString(
                qty.getData("QTY_FOR_STOCK_UNIT"))));
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("TOT_QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "STOCK_UNIT")));
        }
        else {
            o.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
                getData("QTY"))));
            o.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
                "DOSAGE_UNIT")));
            o.modifyDispenseQty(o.getDosageQty());
        }
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
                                       o.getCtz3Code(), o.getOrderCode(), o
                                       .getDosageQty(), level));
        return o;
    }

    /**
     * 总量修改
     * @param o Order
     * @param level String
     * @return Order
     */
    public static Order calcuQtyAll(Order o, String level) {
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(o.getCtz1Code(), o.getCtz2Code(),
                                       o.getCtz3Code(), o.getOrderCode(), o
                                       .getDosageQty(), level) );
        return o;
    }

    /**
     *
     * @param o 传入Order
     * @return 更新完用量数据的Order
     */
    public static Order calcuMediQty(Order o) {
        TotQtyTool t = new TotQtyTool();
        TParm qty = t.getTakeQty(o.getParm());
        o.modifyMediQty(TCM_Transform.getDouble(qty.getData("QTY")));
        o.modifyDosageQty(TCM_Transform.getDouble(qty.getData("QTY")));
        o.modifyDispenseQty(TCM_Transform.getDouble(qty.getData("QTY")));
        return o;
    }

    /**
     * 根据传入的处方签集合和组名，返回此组中的最大集合医嘱序号，如无，则返回无，也证明此张处方签中没有集合医嘱
     * @param ol OrderList
     * @return int 此集合医嘱中的序号
     */
    public static int getOrderSetGroupNo(OrderList ol) {
        int orderSet = 0;
        if (ol == null || ol.size() < 1)
            return 0;
        Order o = new Order();
        for (int i = 0; i < ol.size(ol.PRIMARY); i++) {
            o = ol.getOrder(i);
            if (o == null)
                return 0;
            if (!"Y".equalsIgnoreCase(o.getSetmainFlg()))
                continue;
            if (o.getOrderSetGroupNo() > orderSet)
                orderSet = o.getOrderSetGroupNo();
        }
        return orderSet;
    }

    /**
     * 初始化集合医嘱主项数据
     * @param reg Reg
     * @param parm TParm
     * @param o Order
     * @param isChild boolean
     * @return Order
     */
    public static Order initExaOrder(Reg reg, TParm parm, Order o,
                                     boolean isChild) {
        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
        o.modifyOptitemCode(parm.getValue("OPTITEM_CODE"));
        o.modifyDispenseUnit(parm.getValue("UNIT_CODE"));
        o.modifyDosageUnit(parm.getValue("UNIT_CODE"));
        o.modifyMediUnit(parm.getValue("UNIT_CODE"));
        o.modifyTakeDays(1);
        o.modifyDosageQty(1.0);
        o.modifyDispenseQty(1.0);
        o.modifyMediQty(1.0);
        o.modifyOwnPrice(StringTool.getDouble(parm.getValue("OWN_PRICE")));
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        o.modifyArAmt(BIL.chargeTotCTZ(reg.getCtz1Code(), reg.getCtz2Code(),
                                       reg.getCtz3Code(), o.getOrderCode(),
                                       o.getDosageQty()) <= 0 ? 0.00 :
                      BIL.chargeTotCTZ(reg.getCtz1Code(), reg.getCtz2Code(),
                                       reg.getCtz3Code(), o.getOrderCode(),
                                       o.getDosageQty())
            );
        if(!"PHA".equals(parm.getValue("CAT1_TYPE"))){
            String deptCode = Operator.getDept();
            TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
            //执行科室
            if (deptParm.getCount("DEPT_CODE") > 0)
                deptCode = Operator.getDept();
            else
                deptCode = "";
            o.modifyExecDeptCode(parm.getValue("EXEC_DEPT_CODE")==null||parm.getValue("EXEC_DEPT_CODE").length()==0?deptCode:parm.getValue("EXEC_DEPT_CODE"));
            o.modifyFreqCode("STAT");
        }

        o.modifyInspayType(parm.getValue("INSPAY_TYPE"));
        o.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE"));
        o.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE"));
        o.modifyDegreeCode(parm.getValue("DEGREE_CODE"));
        if (isChild) {
            o.modifyPrinttypeflgInfant("Y");
        }
        else {
            o.modifyPrinttypeflgInfant("N");
        }
        o.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyCtz1Code(reg.getCtz1Code());
        o.modifyCtz2Code(reg.getCtz2Code());
        o.modifyCtz3Code(reg.getCtz3Code());
        double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                     parm.getValue("ORDER_CODE"),
                                                     parm.getValue("CHARGE_HOSP_CODE"),
                                                     reg.getServiceLevel());
        o.modifyDiscountRate(d[1]);
        o.modifyRexpCode(BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "O"));
        o.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyBillFlg("N");
        return o;
    }

    public static Order initExaOrder(Reg reg, TParm parm, Order o,
                                     boolean isChild, String level) {
        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
        o.modifyRxType("0");
        o.modifyFreqCode("STAT");
        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
        o.modifyOptitemCode(parm.getValue("OPTITEM_CODE"));
        o.modifyDispenseUnit(parm.getValue("UNIT_CODE"));
        o.modifyDosageUnit(parm.getValue("UNIT_CODE"));
        o.modifyMediUnit(parm.getValue("UNIT_CODE"));
        o.modifyCat1Type(parm.getValue("CAT1_TYPE"));
        if(!"PHA".equals(parm.getValue("CAT1_TYPE"))){
            String deptCode = Operator.getDept();
            TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
            //执行科室
            if (deptParm.getCount("DEPT_CODE") > 0)
                deptCode = Operator.getDept();
            else
                deptCode = "";
            o.modifyExecDeptCode(parm.getValue("EXEC_DEPT_CODE")==null||parm.getValue("EXEC_DEPT_CODE").length()==0?deptCode:parm.getValue("EXEC_DEPT_CODE"));
            o.modifyFreqCode("STAT");
        }
        o.modifyTakeDays(1);
        o.modifyDosageQty(1.0);
        o.modifyDispenseQty(1.0);
        o.modifyMediQty(1.0);
        TParm feeParm =new TParm();
        feeParm.setData("ORDERSET_CODE", parm.getValue("ORDER_CODE"));
        o.modifyOwnPrice(0.00);//====pangben 2013-8-30集合医嘱主项没有金额
        o.modifyOwnAmt(StringTool.round(o.getOwnPrice() * o.getDosageQty(), 2));
        double ctzRate=BIL.chargeTotCTZ(reg.getCtz1Code(), reg.getCtz2Code(),
                reg.getCtz3Code(), o.getOrderCode(),
                o.getDosageQty());
        o.modifyArAmt(ctzRate<= 0 ? 0.00 :ctzRate);
        o.modifyInspayType(parm.getValue("INSPAY_TYPE"));
        o.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE"));
        o.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE"));
        o.modifyDegreeCode(parm.getValue("DEGREE_CODE"));
        if (isChild) {
            o.modifyPrinttypeflgInfant("Y");
        }
        else {
            o.modifyPrinttypeflgInfant("N");
        }
        o.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyCtz1Code(reg.getCtz1Code());
        o.modifyCtz2Code(reg.getCtz2Code());
        o.modifyCtz3Code(reg.getCtz3Code());
        double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                     parm.getValue("ORDER_CODE"),
                                                     parm.getValue("CHARGE_HOSP_CODE"),
                                                     reg.getServiceLevel());
        o.modifyDiscountRate(d[1]);
        o.modifyRexpCode(BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "O"));
        o.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyBillFlg("N");
        return o;
    }

    public static Order initOpOrder(ODO odo, TParm parm, Order o) {
        o.modifyOrderCode(parm.getValue("ORDER_CODE"));
        o.modifyOrderDesc(parm.getValue("ORDER_DESC"));
        o.modifyGoodsDesc(parm.getValue("GOODS_DESC"));
        o.modifyDosageQty(1.0);
        o.modifyFreqCode("STAT");
        o.modifyTakeDays(1);

        o.modifyOptitemCode(parm.getValue("OPTITEM_CODE"));
        o.modifyDispenseUnit(parm.getValue("UNIT_CODE"));
        o.modifyDosageUnit(parm.getValue("UNIT_CODE"));
        String rbdeptCode = parm.getValue("EXEC_DEPT_CODE");
        if (!StringUtil.isNullString(rbdeptCode)) {
            o.modifyExecDeptCode(rbdeptCode);
        }

        o.modifyInspayType(parm.getValue("INSPAY_TYPE"));
        o.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE"));
        o.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE"));
        o.modifyDegreeCode(parm.getValue("DEGREE_CODE"));
        if (odo.isChild()) {
            o.modifyPrinttypeflgInfant("Y");
        }
        else {
            o.modifyPrinttypeflgInfant("N");
        }
        o.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyCtz1Code(odo.getReg().getCtz1Code());
        o.modifyCtz2Code(odo.getReg().getCtz2Code());
        o.modifyCtz3Code(odo.getReg().getCtz3Code());
        o.modifyDiscountRate(BIL.getOwnRate(odo.getReg().getCtz1Code(),
                                            odo.getReg().getCtz2Code(),
                                            odo.getReg().getCtz3Code(),
                                            parm.getValue("CHARGE_HOSP_CODE"), parm.getValue("ORDER_CODE")));
        o.modifyRexpCode(BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "O"));
        o.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE"));
        o.modifyBillFlg("N");
//	        OdoUtil.fillOrder(o);
        return o;
    }

    /**
     * 添加集合医嘱
     * @param reg Reg
     * @param ol OrderList
     * @param index int
     * @param isChild boolean
     * @return OrderList
     */
    public static OrderList addOrder(Reg reg, OrderList ol, int index,
                                     boolean isChild) {
        double own_price = 0.0;
        double own_amt = 0.0;
        double ar_amt = 0.0;
        if (reg == null || ol == null || ol.size() < 1)
            return null;
        int groupNo = OdoUtil.getOrderSetGroupNo(ol) + 1;
        Order o = ol.getOrder(index);
        if (o == null || StringUtil.isNullString(o.getOrderCode()))
            return null;
        o.modifyOrdersetCode(o.getOrderCode());
        o.modifyOrderSetGroupNo(groupNo);
        o.modifySetmainFlg("Y");
        TParm parm = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(
            ol.getOrder(index).getOrderCode());
        if (parm.getErrCode() != 0) {
            System.out.println("get order set wrong");
            return null;
        }
        Order newo;
        for (int i = 0; i < parm.getCount(); i++) {
            newo = ol.newOrder();
            newo.modifyOrderCode(parm.getValue("ORDER_CODE", i));
            newo.modifyOrderDesc(parm.getValue("ORDER_DESC", i));
            newo.modifyGoodsDesc(parm.getValue("GOODS_DESC", i));
            newo.modifyOptitemCode(parm.getValue("OPTITEM_CODE", i));
            newo.modifyDispenseUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyMediUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyMediQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyDispenseQty(StringTool.getDouble(parm.getValue("TOTQTY",
                i)));
            newo.modifyOwnPrice(StringTool.getDouble(parm.getValue("OWN_PRICE",
                i)));
            newo.modifyCaseNo(reg.caseNo());
            newo.modifyFreqCode(o.getFreqCode());
            String rbdeptCode = parm.getValue("EXEC_DEPT_CODE", i);
            if (!StringUtil.isNullString(rbdeptCode)) {
                o.modifyExecDeptCode(rbdeptCode);
            }

            newo.modifyInspayType(parm.getValue("INSPAY_TYPE", i));
            newo.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE", i));
            newo.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE", i));
            newo.modifyDegreeCode(parm.getValue("DEGREE_CODE", i));
            if (isChild) {
                newo.modifyPrinttypeflgInfant("Y");
            }
            else {
                newo.modifyPrinttypeflgInfant("N");
            }
            newo.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifySetmainFlg("N");
            newo.modifyCtz1Code(reg.getCtz1Code());
            newo.modifyCtz2Code(reg.getCtz2Code());
            newo.modifyCtz3Code(reg.getCtz3Code());
            double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                         parm.getValue("ORDER_CODE"),
                                                         parm.getValue("CHARGE_HOSP_CODE"),
                                                         reg.getServiceLevel());
            newo.modifyDiscountRate(d[1]);
            newo.modifyRexpCode(BIL.getRexpCode(parm.getValue(
                "CHARGE_HOSP_CODE", i), "O"));
            newo.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifyBillFlg("N");
            newo.modifyHideFlg(parm.getValue("HIDE_FLG", i));
            newo.modifyOrdersetCode(o.getOrdersetCode());
            newo.modifyOrderSetGroupNo(groupNo);
            double eachown = StringTool.getDouble(parm.getValue("TOTQTY", i)) *
                StringTool.getDouble(parm.getValue("OWN_PRICE", i));
            double eachtot = BIL.chargeTotCTZ(reg.
                                              getCtz1Code(), reg.getCtz2Code(),
                                              reg.getCtz3Code(),
                                              newo.getOrderCode(),
                                              StringTool.getDouble(parm.
                getValue("TOTQTY", i))) <= 0 ? 0.00 : BIL.chargeTotCTZ(reg.
                getCtz1Code(), reg.getCtz2Code(),
                reg.getCtz3Code(),
                newo.getOrderCode(),
                StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyOwnAmt(eachown);
            newo.modifyArAmt(eachtot);
            own_price += eachown;
            own_amt += eachown;
            ar_amt += eachtot;
        }
        return ol;
    }

    public static OrderList addOrder(Reg reg, OrderList ol, int index,
                                     boolean isChild, String level) {
        double own_price = 0.0;
        double own_amt = 0.0;
        double ar_amt = 0.0;
        if (reg == null || ol == null || ol.size() < 1)
            return null;
        int groupNo = OdoUtil.getOrderSetGroupNo(ol) + 1;
        Order o = ol.getOrder(index);
        if (o == null || StringUtil.isNullString(o.getOrderCode()))
            return null;
        o.modifyOrdersetCode(o.getOrderCode());
        o.modifyOrderSetGroupNo(groupNo);
        o.modifySetmainFlg("Y");
        TParm parm = SYSOrderSetDetailTool.getInstance().selectByOrderSetCode(
            ol.getOrder(index).getOrderCode());
//        System.out.println("医嘱信息"+parm);
        if (parm.getErrCode() != 0) {
            System.out.println("get order set wrong");
            return null;
        }
        Order newo;
        for (int i = 0; i < parm.getCount(); i++) {
            TParm sysParm = SYSFeeTool.getInstance().getFeeAllData(parm.getValue("ORDER_CODE", i));
            newo = ol.newOrder();
            newo.modifyOrderCode(parm.getValue("ORDER_CODE", i));
            newo.modifyOrderDesc(parm.getValue("ORDER_DESC", i));
            newo.modifyGoodsDesc(parm.getValue("GOODS_DESC", i));
            newo.modifyOptitemCode(parm.getValue("OPTITEM_CODE", i));
            newo.modifyRxType("0");
            newo.modifyDispenseUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyMediUnit(parm.getValue("UNIT_CODE", i));
            newo.modifyDosageQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyMediQty(StringTool.getDouble(parm.getValue("TOTQTY", i)));
            newo.modifyDispenseQty(StringTool.getDouble(parm.getValue("TOTQTY",
                i)));
            if ("2".equals(level)) {
                newo.modifyOwnPrice(StringTool.getDouble(parm.getValue(
                    "OWN_PRICE3", i)));
            }
            else if ("3".equals(level)) {
                newo.modifyOwnPrice(StringTool.getDouble(parm.getValue(
                    "OWN_PRICE3", i)));
            }
            else
                newo.modifyOwnPrice(StringTool.getDouble(parm.getValue(
                    "OWN_PRICE", i)));
            newo.modifyCaseNo(reg.caseNo());
            newo.modifyFreqCode(o.getFreqCode());
            if(!"PHA".equals(parm.getValue("CAT1_TYPE",i))){
                String deptCode = Operator.getDept();
                TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
                //执行科室
                if (deptParm.getCount("DEPT_CODE") > 0)
                    deptCode = Operator.getDept();
                else
                    deptCode = "";
                newo.modifyExecDeptCode(sysParm.getValue("EXEC_DEPT_CODE",0)==null||sysParm.getValue("EXEC_DEPT_CODE",0).length()==0?deptCode:sysParm.getValue("EXEC_DEPT_CODE",0));
                newo.modifyFreqCode("STAT");
        }
            newo.modifyInspayType(parm.getValue("INSPAY_TYPE", i));
            newo.modifyOrderCat1Code(parm.getValue("ORDER_CAT1_CODE", i));
//            System.out.println("医嘱细分类群组222222222"+parm.getValue("CAT1_TYPE"));
            newo.modifyCat1Type(parm.getValue("CAT1_TYPE", i));
            newo.modifyRpttypeCode(parm.getValue("RPTTYPE_CODE", i));
            newo.modifyDegreeCode(parm.getValue("DEGREE_CODE", i));
            if (isChild) {
                newo.modifyPrinttypeflgInfant("Y");
            }
            else {
                newo.modifyPrinttypeflgInfant("N");
            }
            newo.modifyDevCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifySetmainFlg("N");
            newo.modifyCtz1Code(reg.getCtz1Code());
            newo.modifyCtz2Code(reg.getCtz2Code());
            newo.modifyCtz3Code(reg.getCtz3Code());
            double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                                                         parm.getValue("ORDER_CODE"),
                                                         parm.getValue("CHARGE_HOSP_CODE"),
                                                         reg.getServiceLevel());
            newo.modifyDiscountRate(d[1]);
            newo.modifyRexpCode(BIL.getRexpCode(parm.getValue(
                "CHARGE_HOSP_CODE", i), "O"));
            newo.modifyHexpCode(parm.getValue("CHARGE_HOSP_CODE", i));
            newo.modifyBillFlg("N");
            newo.modifyHideFlg(parm.getValue("HIDE_FLG", i));
            newo.modifyOrdersetCode(o.getOrdersetCode());
            newo.modifyOrderSetGroupNo(groupNo);
            newo.modifyExecDeptCode(sysParm.getValue("EXEC_DEPT_CODE",0) == null||sysParm.getValue("EXEC_DEPT_CODE",0).length()==0 ?
                                    Operator.getDept() :
                                    sysParm.getValue("EXEC_DEPT_CODE",0));
            double ownPrice = 0.00;
            if ("2".equals(level)) {
                ownPrice = StringTool.getDouble(parm.getValue("OWN_PRICE2", i));
            }
            else if ("3".equals(level)) {
                ownPrice = StringTool.getDouble(parm.getValue("OWN_PRICE3", i));
            }
            else
                ownPrice = StringTool.getDouble(parm.getValue("OWN_PRICE", i));
            double eachown = StringTool.getDouble(parm.getValue("TOTQTY", i)) *
                ownPrice;
            double eachtot = BIL.chargeTotCTZ(reg.
                                              getCtz1Code(), reg.getCtz2Code(),
                                              reg.getCtz3Code(),
                                              newo.getOrderCode(),
                                              StringTool.getDouble(parm.
                getValue("TOTQTY", i)), level);
//            System.out.println("OdoUtil医嘱单价" + eachtot);
            newo.modifyOwnAmt(eachown);
            newo.modifyArAmt(eachtot);
            own_price += eachown;
            own_amt += eachown;
            ar_amt += eachtot;
        }
        return ol;
    }

    /**
     * 将细相金额压缩进主项
     * @param ol 传入OrderList
     * @return TParm 压缩细相金额的OrderList 的TParm
     */
    public static TParm showOrderSet(OrderList ol) {
        if (ol == null || ol.size() < 1)
            return null;
        TParm parm;
        int ordergroupno;
        String ordersetCode = "";
        double own_price = 0.0;
        double own_amt = 0.0;
        double ar_amt = 0.0;
        //ORDER_CODE;OPTITEM_CODE;MEDI_QTY;
        //DOSAGE_UNIT;OWN_PRICE;AR_AMT;PAYAMOUNT;OWN_AMT;
        //DR_NOTE;NS_NOTE;EXEC_DEPT_CODE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;REGISTER_DATE;PHA_CHECK_DATE
        for (int i = ol.size() - 1; i >= 0; i--) {
            try {
                Order o = ol.getOrder(i);
                if (o == null)
                    return null;
                own_amt += o.getOwnAmt();
                ar_amt += o.getArAmt();
                if ("Y".equalsIgnoreCase(o.getSetmainFlg()) &&
                    !ordersetCode.equalsIgnoreCase(o.getOrdersetCode())) {
                    ordersetCode = o.getOrdersetCode();
                    ordergroupno = o.getOrderSetGroupNo();
                    o.modifyOwnAmt(own_amt);
                    o.modifyArAmt(ar_amt);
                    o.modifyOwnPrice(own_amt);
                    continue;
                }
            }
            catch (Exception e) {
//				  System.out.println("i============="+i);
            }

        }
        parm = ol.getParm(ol.PRIMARY);
        for (int i = parm.getCount() - 1; i >= 0; i--) {
            if (parm.getValue("SETMAIN_FLG", i).equalsIgnoreCase("N") ||
                "".equalsIgnoreCase(parm.getValue("SETMAIN_FLG", i))) {
                parm.removeRow(i);
            }
        }
        return parm;
    }

    /**
     * 删除集合医嘱
     * @param ol OrderList
     * @param o Order
     * @return OrderList
     */
    public static OrderList deleteOrderSet(OrderList ol, Order o) {
        if (ol == null || ol.size() < 1)
            return ol;
        if (!"Y".equalsIgnoreCase(o.getSetmainFlg())) {
            return ol;
        }
        int ordersetGroupNo = -1;
        if (o != null) {
            ordersetGroupNo = o.getOrderSetGroupNo();
        }
        Order temp;
        for (int i = ol.size() - 1; i > -1; i--) {
            temp = ol.getOrder(i);
            if (temp.getOrderSetGroupNo() == ordersetGroupNo) {
                ol.removeData(i);
            }
        }
        return ol;
    }

    /**
     * 初始化一条医嘱
     * @param table TTable
     * @param order OpdOrder
     * @param row int
     * @param parm TParm
     * @param parmBase TParm
     * @param ctz String[]
     */
    public static void initOrder(TTable table, OpdOrder order, int row,
                                 TParm parm,
                                 TParm parmBase, String[] ctz) {
        order.itemNow = true;
        String rxNo = order.getItemString(row, "RX_NO");
        String rxType = order.getItemString(row, "RX_TYPE");
        order.setItem(row, "PRESRT_NO", row + 1);
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "RELEASE_FLG", "N");
        order.setItem(row, "LINKMAIN_FLG", "N");
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("ORDER_DESC")
                      .replaceFirst("(" + parm.getValue("SPECIFICATION") + ")",
                                    ""));
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        order.setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE"));
        order.setItem(row, "CHARGE_HOSP_CODE", parm
                      .getValue("CHARGE_HOSP_CODE"));
        order.setItem(row, "REXP_CODE", BIL.getRexpCode(parm
            .getValue("CHARGE_HOSP_CODE"), "O"));
        order.setItem(row, "SETMAIN_FLG", "N");
        order.setItem(row, "ORDERSET_GROUP_NO", 0);
        order.setItem(row, "CTZ1_CODE", ctz[0]);
        order.setItem(row, "CTZ2_CODE", ctz[1]);
        order.setItem(row, "CTZ3_CODE", ctz[2]);
        double TAKE_QTY = parm.getDouble("MEDI_QTY", 0);
        order.setItem(row, "MEDI_QTY", 1.0);
        order.setItem(row, "DISPENSE_QTY", 1.0);
        order.setItem(row, "DOSAGE_QTY", 1.0);
        order.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
        if (parmBase != null) {
            double takeQty = (TAKE_QTY < 1.0) ? 1.0 : TAKE_QTY;
            order.setItem(row, "MEDI_QTY", parmBase.getDouble("MEDI_QTY", 1));

            order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
            order
                .setItem(row, "ROUTE_CODE", parmBase.getValue("ROUTE_CODE",
                0));
            String TAKE_DAY = parmBase.getValue("TAKE_DAYS", 0);
            int takedays = (TAKE_DAY == null || TAKE_DAY.trim().length() < 1 ||
                            "0"
                            .equalsIgnoreCase(TAKE_DAY)) ? 1 : Integer
                .valueOf(TAKE_DAY);
            order.setItem(row, "TAKE_DAYS", takedays);
            order.setItem(row, "CTRLDRUGCLASS_CODE", parmBase.getValue(
                "CTRLDRUGCLASS_CODE", 0));
            order.setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG",
                0));
            if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
                order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
                    "STOCK_UNIT", 0));
                order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
                    "STOCK_UNIT", 0));
                order.setItem(row, "MEDI_UNIT", parmBase.getValue("STOCK_UNIT",
                    0));
            }
            else {
                order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
                    "DOSAGE_UNIT", 0));
                order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
                    "DOSAGE_UNIT", 0));
                order.setItem(row, "MEDI_UNIT", parmBase.getValue(
                    "MEDI_UNIT", 0));
            }
        }

        double ownAmt = StringTool.round(order.getItemDouble(row, "OWN_PRICE")
                                         *
                                         order.getItemDouble(row, "DOSAGE_QTY"),
                                         2);
        double arAmt = BIL
            .chargeTotCTZ(ctz[0], ctz[1], ctz[2], parm
                          .getValue("ORDER_CODE"), order.getItemDouble(row,
            "DOSAGE_QTY"));
        order.setItem(row, "OWN_AMT", ownAmt);
        order.setItem(row, "AR_AMT", arAmt);
        order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
        order.itemNow = false;
    }

    /**
     * 根据icd中的开始年龄，结束年龄，限制科室，限制性别、粗码等限制条件判断此条医嘱是否能开立
     * @param icd TParm
     * @param dept String
     * @param sex String
     * @param birthday Timestamp
     * @param admDate Timestamp
     * @return boolean
     */
    public static boolean isAllowDiag(TParm icd, String dept, String sex,
                                      Timestamp birthday, Timestamp admDate) {
        String limitSex = icd.getValue("LIMIT_SEX_CODE");
        //性别限制
        if (!StringUtil.isNullString(limitSex) &&
            !"N".equalsIgnoreCase(limitSex) && !sex.equalsIgnoreCase(limitSex)) {
            System.out.println("limitSex");
            return false;
        }
        String[] ages = StringTool.CountAgeByTimestamp(birthday, admDate);
        String[] reverseAges = StringTool.CountAgeByTimestamp(admDate, birthday);
        if (StringUtil.isNullArray(ages)) {
            return false;
        }
        long age = StringTool.getLong(ages[0]);
        long startAge = icd.getLong("START_AGE");
        long endAge = icd.getLong("END_AGE");
        if (icd.getBoolean("CAT_FLG")) {
            System.out.println("catFlg");
            return false;
        }
        //年两限制
        if (startAge > 0 && age < startAge) {
            return false;
        }
        if (endAge > 0 && age > endAge) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param icd TParm
     * @return boolean
     */
    public static boolean isAllowChnDiag(TParm icd) {
        if (icd.getBoolean("SYNDROME_FLG"))
            return false;
        return true;
    }

    /**
     * 判断一条诊断是否可以做主诊断
     * @param icdCode String
     * @return boolean
     */
    public static boolean isAllowMainDiag(String icdCode) {

        TParm icdParm = ICD.getBuffer(ICD.isFilter() ? ICD.FILTER : ICD.PRIMARY);
        Vector code = (Vector) icdParm.getData("ICD_CODE");
        int row = code.indexOf(icdCode);
        if (row < 0) {
            return false;
        }
        TParm parm = icdParm.getRow(row);
        return icdParm.getBoolean("MAIN_DIAG_FLG", row);
    }

    /**
     * 给如诊断代码返回中文
     * @param icdCode String 诊断代码
     * @return icdDesc String 诊断中文
     */
    public static String getIcdDesc(String icdCode) {
        TParm icdParm = ICD.getBuffer(ICD.isFilter() ? ICD.FILTER : ICD.PRIMARY);
        Vector code = (Vector) icdParm.getData("ICD_CODE");
        int row = code.indexOf(icdCode);
        if (row < 0) {
            return "";
        }
        TParm parm = icdParm.getRow(row);
        return icdParm.getValue("ICD_CHN_DESC", row);
    }

    /**
     * 如果是医保自费药则显示紫色
     * @param ctz String[]
     * @param order TDataStore
     * @param whetherCallInsItf boolean
     * @return Map
     */
    public static Map getInsColor(String[] ctz, TDataStore order,
                                  boolean whetherCallInsItf) {

        Map color = new HashMap();
        if (!CTZTool.getInstance().getFlgByCtz(ctz[0]).getBoolean("NHI_CTZ_FLG",
            0))
            return color;
        String insPay;
        Color purple = new Color(128, 0, 128);
        for (int i = 0; i < order.rowCount(); i++) {
            int row = sysFeeCode.indexOf(order.getItemString(i, "ORDER_CODE"));
            if (row < 0)
                return color;
            insPay = sysfeeParm.getValue("INSPAY_TYPE", row);
            if ("C".equalsIgnoreCase(insPay)) {
                color.put(i, purple);
                continue;
            }
        }
        return color;
    }

    /**
     * 是否有权限开出此药品
     * @param orderCode String
     * @param userId String
     * @return boolean
     */
    public static boolean isHavingLiciense(String orderCode, String userId) {

        String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode +
            "'";
        TParm sysFee = new TParm(TJDODBTool.getInstance().select(sql));
        if (sysFee.getErrCode() != 0 || sysFee.getCount() != 1) {
            return false;
        }
        sysFee = sysFee.getRow(0);
        String lcsClassCode = sysFee.getValue("LCS_CLASS_CODE");
        if (StringUtil.isNullString(lcsClassCode)) {
            return true;
        }
        String lcsCode;

        TParm lcs = SYSOperatorTool.getInstance().getLcsCode(userId);
        if (lcs.getErrCode() != 0) {
            return false;
        }
        int count = lcs.getCount("LCS_CLASS_CODE");
        for (int i = 0; i < count; i++) {
            lcsCode = lcs.getValue("LCS_CLASS_CODE", i);
            if (lcsCode.equalsIgnoreCase(lcsClassCode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 管制药品返回TABLE颜色集合，如和管制药品则在TABLE上显示红色
     * @param color Map
     * @param order TDataStore
     * @return Map
     */
    public static Map getCtrlColor(Map color, TDataStore order) {

    	 Color red = new Color(255, 0, 0);    
         Color blue = new Color(0, 0, 255); // chenxi 提示蓝色
         TParm phaBaseParm = getPhaBase().getBuffer(TDS.PRIMARY);
         Vector baseCode = (Vector) phaBaseParm.getData("ORDER_CODE");
         for (int i = 0; i < order.rowCount(); i++) {
             String orderCode = order.getItemString(i, "ORDER_CODE");
             //$$=========== add by chenx start ==========$$//
 			String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '" +orderCode+ "' " ;
 			TParm sysfeeParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
 			//System.out.println("======chenxui======="+sql);
 			// sysfeeParm.getValue("DRUG_NOTES_DR", 0) ;
 			//$$=========== add by chenx end ==========$$//
             int row = baseCode.indexOf(orderCode);
             if (row < 0)
                 return color;
             if (!StringUtil.isNullString(phaBaseParm.getValue(
                 "CTRLDRUGCLASS_CODE", row))) {
                 color.put(i, red);
             }else if(!StringUtil.isNullString(sysfeeParm.getValue("DRUG_NOTES_DR", 0))){
             	
             	color.put(i, blue);    //==============如果  DRUG_NOTES_DR有值，table药的颜色变为蓝色
         }
             }
         
        return color;
    }

    /**
     * 返回PHA_BASE
     * @return TDS
     */
    public static TDS getPhaBase() {
        if (phaBase == null) {
            phaBase = new TDS();
            phaBase.setSQL(GET_PHABASE);
            phaBase.retrieve();
        }
        return phaBase;
    }

    /**
     * 返回抗生素默认使用天数
     * @param takeDays int
     * @param orderCode String
     * @return boolean
     */
    public static boolean isAllowAntiBiotic(int takeDays, String orderCode) {
        TParm parm = new TParm();
        parm.setData("ORDER_CODE", orderCode);
        int limitDays = PhaBaseTool.getInstance().getAntiDrugDays(parm).getInt(
            "TAKE_DAYS", 0);

        return (limitDays > 0 && takeDays > limitDays);
    }

    /**
     * 设置单价总价，CHARGE_HOSP_CODE
     * @param orderCode String
     * @return TParm
     */
    public static TParm getPrice(String orderCode) {
        TParm result = new TParm();
        TParm sysFee = SYS_FEE.getBuffer(SYS_FEE.isFilter() ? SYS_FEE.FILTER :
                                         SYS_FEE.PRIMARY);

        Vector code = (Vector) sysFee.getData("ORDER_CODE");
        int row = code.indexOf(orderCode);
        if (row < 0)
            return result;
        result.setData("OWN_PRICE", sysFee.getDouble("OWN_PRICE", row));
        result.setData("CHARGE_HOSP_CODE",
                       sysFee.getValue("CHARGE_HOSP_CODE", row));

        return result;
    }

    public static void main(String[] args) {
//		JavaHisDebug.initClient();
        Timestamp t1 = StringTool.getTimestamp("20090821114800",
                                               "yyyyMMddHHmmss");
        Timestamp t2 = StringTool.getTimestamp("20090901080000",
                                               "yyyyMMddHHmmss");
        int days = StringTool.getDateDiffer(t1, t2);
//		System.out.println(2/3);
//		ODO odo = new ODO();
//		odo.setPat(new Pat());
//		odo.getPat().setMrNo("1");
//		odo.onQuery("ABC");
//		odo.getPrescriptionList().newOrderList("2");
//		System.out.println(TCM_Transform.getString(odo.getPrescriptionList().getGroupPrsptSize("2")-1)+"count");
//		TParm parm=OdoUtil.newOrderTParm(odo, "2",TCM_Transform.getString(odo.getPrescriptionList().getGroupPrsptSize("2")-1));
    }
}
