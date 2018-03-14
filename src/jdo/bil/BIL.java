package jdo.bil;

import java.util.HashMap;
import java.util.Map;

import jdo.opb.OPBReceiptTool;
import jdo.reg.PanelTypeFeeTool;
import jdo.reg.Reg;
import jdo.sys.BILRecpParmTool;
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.StatusDetailsTool;
import jdo.sys.SysFee;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.Log;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 账务处理</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class BIL {
    /**
     * 引入SYSFeeTool
     * @return SYSFeeTool
     */
    public static SYSFeeTool SYSFeeTool() {
        return SYSFeeTool.getInstance();
    }

    /**
     * 引入PanelTypeFeeTool
     * @return PanelTypeFeeTool
     */
    public static PanelTypeFeeTool PanelTypeFeeTool() {
        return PanelTypeFeeTool.getInstance();
    }

    /**
     * 引入StatusDetailsToolTool
     * @return StatusDetailsTool
     */
    public static StatusDetailsTool getStatusDetailsTool() {
        return StatusDetailsTool.getInstance();
    }

    /**
     * 引入BILInvoiceTool
     * @return BILInvoiceTool
     */
    public static BILInvoiceTool getBILInvoiceTool() {
        return BILInvoiceTool.getInstance();
    }

    /**
     * 单身份费用折扣率
     * @param ctz1 String
     * @param chargeHospCode String
     * @return double
     */
    public static double getOwnRate(String ctz1, String chargeHospCode, String orderCode) {
        return getOwnRate(ctz1, null, null, chargeHospCode, orderCode);
    }

    /**
     * 双身份折扣率
     * @param ctz1 String
     * @param ctz2 String
     * @param chargeHospCode String
     * @return double
     */
    public static double getOwnRate(String ctz1, String ctz2,
                                    String chargeHospCode, String orderCode) {
        return getOwnRate(ctz1, ctz2, null, chargeHospCode, orderCode);
    }

    /**
     * 三身份折扣率
     * @param ctz1 String
     * @param ctz2 String
     * @param ctz3 String
     * @param chargeHospCode String
     * @return double
     */
    public static double getOwnRate(String ctz1, String ctz2, String ctz3,
                                    String chargeHospCode, String orderCode) {
        return BILStrike.getInstance().getOwnRate(ctz1, ctz2, ctz3,
                                                  chargeHospCode, orderCode);
    }

    /**
     * 根据admType，clinicTypeCode,服务等级给出挂号费用
     * @param admType String
     * @param clinicTypecode String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String level) {
//        System.out.println("  " + admType + " " + clinicTypecode);
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
        TParm result = PanelTypeFeeTool().getOrderCode(admType, clinicTypecode);
        double out = 0.00;
        if (result.getCount() == 0) {
            return -1;
        }
        for (int i = 0; i < result.getCount(); i++) {
            out +=
                SysFee.getFee( (String) result.getData("ORDER_CODE", i), level);
        }
        return out;
    }

    /**
     * 根据admType，clinicTypecode，CTZ1，CTZ2，CTZ3，服务等级得到折扣后的费用
     * @param admType String
     * @param clinicTypecode String
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String CTZ1, String CTZ2, String CTZ3,
                                   String level) {
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
//        System.out.println("开始" + admType + "  " + clinicTypecode + "  " + CTZ1);
        TParm result = PanelTypeFeeTool().getOrderCode(admType, clinicTypecode);
//        System.out.println("result  :" + result);
        double out = 0.00;
        if (result.getCount() == 0) {
            return -1;
        }
        for (int i = 0; i < result.getCount(); i++) {
//            System.out.println( (String) result.getData("ORDER_CODE", i));
            double fee = SysFee.getFee( (String) result.getData("ORDER_CODE", i),
                                       level);
            double ownrate = getOwnRate(CTZ1, CTZ2, CTZ3,
                                        SysFee.
                                        getChargeHospCode( (String) result.
                getData("ORDER_CODE", i)), (String) result.
                getData("ORDER_CODE", i));
            out += fee * ownrate;
//            System.out.println("结束out   " + out);
        }
        return out;
    }

    /**
     * 根据admType，clinicTypecode，CTZ1，CTZ2，CTZ3,服务等级得到费用
     * @param admType String
     * @param clinicTypecode String
     * @param CTZ1 String
     * @param CTZ2 String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String CTZ1, String CTZ2, String level) {
        return getRegFee(admType, clinicTypecode, CTZ1, CTZ2, null, level);
    }

    /**
     * 根据admType，clinicTypecode，CTZ1，服务等级得到折扣后的费用
     * @param admType String
     * @param clinicTypecode String
     * @param CTZ1 String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String CTZ1, String level) {
        return getRegFee(admType, clinicTypecode, CTZ1, null, null, level);
    }

    /**
     * 根据REG对象得到费用
     * @param reg Reg
     * @return double
     */
    public static double getRegfee(Reg reg) {

        return 0.00;
//         getRegFee(reg.getAdmType(),reg.getClinicareaNo())
    }

    /**
     * 根据admType，clinicTypeCode,receiptType,服务等级给出REG详细费用
     * @param admType String
     * @param clinicTypecode String
     * @param receiptType String
     * @param level String
     * @return double
     */
    public static double getRegDetialFee(String admType, String clinicTypecode,
                                         String receiptType, String level) {
//        System.out.println("  " + admType + " " + clinicTypecode);
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
        if (receiptType == null || receiptType.trim().length() == 0)
            return -1;
        TParm result = PanelTypeFeeTool().getOrderCodeDetial(admType,
            clinicTypecode, receiptType);
        double out = 0.00;
        if (result.getCount() == 0) {
            return -1;
        }
        out += SysFee.getFee( (String) result.getData("ORDER_CODE", 0), level);
        return out;
    }

    /**
     * 查询含身份的reg费用明细
     * @param admType String
     * @param clinicTypecode String
     * @param receiptType String
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param level String
     * @return double
     */
    public static double getRegDetialFee(String admType, String clinicTypecode,
                                         String receiptType, String CTZ1,
                                         String CTZ2, String CTZ3, String level) {
//        System.out.println("服务等级" + level);
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
        if (receiptType == null || receiptType.trim().length() == 0)
            return -1;
        TParm result = PanelTypeFeeTool().getOrderCodeDetial(admType,
            clinicTypecode, receiptType);
        double out = 0.00;
        if (result.getCount() == 0) {
            return out;
        }
        //查询折扣率
        out = chargeCTZ(CTZ1, CTZ2, CTZ3,
                        (String) result.getData("ORDER_CODE", 0), level);
        return out;
    }

    /**
     * 根据REG和ordercode得到三个身份
     * @param reg Reg
     * @param orderCode String
     * @return double
     */
    public static double chargeCTZ(Reg reg, String orderCode) {
        if (reg == null || orderCode == null || orderCode.length() == 0)
            return -1;
        return chargeCTZ(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                         orderCode, "");
    }

    /**
     * 根据身份,ordercode算收费,三身份,服务等级方法
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double chargeCTZ(String CTZ1, String CTZ2, String CTZ3,
                                   String orderCode, String level) {
        if (orderCode == null || orderCode.trim().length() == 0)
            return -1;
        return BILStrike.getInstance().chargeCTZ(CTZ1, CTZ2, CTZ3, orderCode,
                                                 level);
    }

    /**
     * 根据三身份和医嘱代码，服务等级得到自付比例
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double getRate(String CTZ1, String CTZ2, String CTZ3,
                                 String orderCode, String level) {
        String chargeHospCode = SysFee.getChargeHospCode(orderCode);
        if (chargeHospCode == null || chargeHospCode.length() == 0)
            return -1;
        double fee = SysFee.getFee(orderCode, level);
        if (fee == -1)
            return fee;
        return getOwnRate(CTZ1, CTZ2, CTZ3, chargeHospCode, orderCode);
    }

    /**
     * 根据就诊序号,医嘱代码,数量,服务等级返回价钱信息
     * @param caseNo String
     * @param orderCode String
     * @param qty double
     * @param level String
     * @return TParm
     */
    public static TParm getPriceForODI(String caseNo, String orderCode,
                                       double qty, String level) {
        TParm result = new TParm();
        String ctzSql =
            "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO='" +
            caseNo + "'";
        TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
        if (ctzParm == null) {
            return result;
        }
        if (ctzParm.getErrCode() != 0) {
            return result;
        }
        TParm sysFee = new TParm(TJDODBTool.getInstance().select(
            "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"));
        if (sysFee == null) {
            return result;
        }
        if (sysFee.getErrCode() != 0) {
            return result;
        }
        String ctz1Code = ctzParm.getValue("CTZ1_CODE", 0);
        String ctz2Code = ctzParm.getValue("CTZ2_CODE", 0);
        String ctz3Code = ctzParm.getValue("CTZ3_CODE", 0);
        double ownPrice = getFee(orderCode, level);
        double nhiPrice = sysFee.getDouble("NHI_PRICE", 0);
        double rate = getRate(ctz1Code, ctz2Code, ctz3Code, orderCode, level);
        double ownAmtM = StringTool.round(BIL.getFee(orderCode, qty, level), 2);
        double totAmtM = StringTool.round(ownAmtM * rate, 2);
        result.setData("OWN_PRICE", ownPrice);
        result.setData("NHI_PRICE", nhiPrice);
        result.setData("RATE", rate);
        result.setData("OWN_AMT", ownAmtM);
        result.setData("TOT_AMT", totAmtM);
        return result;
    }

    /**
     * 根据医嘱代码,服务等级查找医嘱单价
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double getFee(String orderCode, String level) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return SysFee.getFee(orderCode, level);
    }

    /**
     * 根据医嘱代码查找医嘱单价
     * @param orderCode String
     * @return double
     */
    public static double getFee(String orderCode) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return SysFee.getFee(orderCode);
    }

    /**
     * 根据医嘱代码,总量,服务等级计算费用
     * @param orderCode String
     * @param qty double
     * @param level String
     * @return double
     */
    public static double getFee(String orderCode, double qty, String level) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return getFee(orderCode, level) * qty;
    }

    /**
     * 根据医嘱代码,总量计算费用
     * @param orderCode String
     * @param qty double
     * @return double
     */
    public static double getFee(String orderCode, double qty) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return getFee(orderCode) * qty;
    }

    /**
     * 两身份,ordercode,服务等级类型
     * @param CTZ1 String
     * @param CTZ2 String
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double chargeCTZ(String CTZ1, String CTZ2, String orderCode,
                                   String level) {
        return chargeCTZ(CTZ1, CTZ2, null, orderCode, level);
    }

    /**
     * 一个身份,ordercode类型
     * @param CTZ String
     * @param orderCode String
     * @return double
     */
    public static double chargeCTZ(String CTZ, String orderCode) {
        return chargeCTZ(CTZ, null, null, orderCode);
    }

    /**
     * 根据身份算收费,三身份方法，ordercode，计算含总量的总价格
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param QTY double
     * @return double
     */

    public static double chargeTotCTZ(String CTZ1, String CTZ2, String CTZ3,
                                      String orderCode, double QTY) {
        return chargeCTZ(CTZ1, CTZ2, CTZ3, orderCode) * QTY;
    }

    public static double chargeTotCTZ(String CTZ1, String CTZ2, String CTZ3,
                                      String orderCode, double QTY,
                                      String level) {
//        System.out.println("BIL入参" + CTZ1 + ";" + CTZ2 + ";" + CTZ3 + ";" +
//                           orderCode + ";" + level);

        return chargeCTZ(CTZ1, CTZ2, CTZ3, orderCode, level) * QTY;
    }

    /**
     *  两身份类型，ordercode,计算含总量的总价格
     * @param CTZ1 String
     * @param CTZ2 String
     * @param orderCode String
     * @param QTY double
     * @return double
     */

    public static double chargeTotCTZ(String CTZ1, String CTZ2,
                                      String orderCode, double QTY) {
        return chargeTotCTZ(CTZ1, CTZ2, null, orderCode, QTY);
    }

    /**
     * 一个身份类型,ordercode ，计算含总量的总价格
     * @param CTZ1 String
     * @param orderCode String
     * @param QTY double
     * @return double
     */
    public static double chargeTotCTZ(String CTZ1, String orderCode, double QTY) {
        return chargeTotCTZ(CTZ1, null, null, orderCode, QTY);
    }

    /**
     * 得到当前票号和下一票号
     * @param recpType String
     * @return String[]
     */
    public static String[] getUpdateNo(String recpType) {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", recpType);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "01");
        String[] updateNo = getBILInvoiceTool().getUpdateUpdateNo(parm);
        return updateNo;
    }

    /**
     * 检核REG是否开账
     * @return boolean
     */
    public static boolean checkREG() {
        return checkSwitchBil("REG");
    }

    /**
     * 检核OPB是否开账
     * @return boolean
     */
    public static boolean checkOPB() {
        return checkSwitchBil("OPB");
    }

    /**
     * 检核是否开账
     * @param type String
     * @return boolean
     */
    public static boolean checkSwitchBil(String type) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", type);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0"); //01表示票据使用中
        TParm cashiercode = BILCounteTool.getInstance().CheckCounter(parm);
        String result = cashiercode.getValue("CASHIER_CODE");
        if (result.length() > 0)
            return true;
        return false;
    }

    /**
     * 根据院内费用代码,门急别得到收据类别
     * @param chargeHospCode String
     * @param admType String
     * @return String
     */
    public static String getRexpCode(String chargeHospCode, String admType) {
        TParm result = getCode(chargeHospCode);
        String rexpCode = "";
        if ("I".equals(admType))
            rexpCode = result.getValue("IPD_CHARGE_CODE", 0);
        else
            rexpCode = result.getValue("OPD_CHARGE_CODE", 0);
        return rexpCode;
    }

    /**
     * 根据院内费用代码得到病案首页费用代码
     * @param chargeHospCode String
     * @return String
     */
    public static String getMroChargeCode(String chargeHospCode) {
        TParm result = getCode(chargeHospCode);
        String mroChargeCode = result.getValue("MRO_CHARGE_CODE", 0);
        return mroChargeCode;
    }

    /**
     * 根据院内费用代码得到统计费用
     * @param chargeHospCode String
     * @return String
     */
    public static String getStaChargeCode(String chargeHospCode) {
        TParm result = getCode(chargeHospCode);
        String staChargeCode = result.getValue("STA_CHARGE_CODE", 0);
        return staChargeCode;
    }

    /**
     * 根据院内费用代码查找各种费用
     * @param chargeHospCode String
     * @return TParm
     */
    public static TParm getCode(String chargeHospCode) {
        TParm parm = new TParm();
        parm.setData("CHARGE_HOSP_CODE", chargeHospCode);
        TParm result = SYSChargeHospCodeTool.getInstance().selectChargeCode(
            parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 整理收费类别
     * @param admType String
     * @return Map
     */
    public Map getChargeToRexpCodeMap(String admType) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        TParm result = BILRecpParmTool.getInstance().selectcharge(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        Map map = new HashMap();
        map.put(result.getValue("CHARGE01", 0), "CHARGE01");
        map.put(result.getValue("CHARGE02", 0), "CHARGE02");
        map.put(result.getValue("CHARGE03", 0), "CHARGE03");
        map.put(result.getValue("CHARGE04", 0), "CHARGE04");
        map.put(result.getValue("CHARGE05", 0), "CHARGE05");
        map.put(result.getValue("CHARGE06", 0), "CHARGE06");
        map.put(result.getValue("CHARGE07", 0), "CHARGE07");
        map.put(result.getValue("CHARGE08", 0), "CHARGE08");
        map.put(result.getValue("CHARGE09", 0), "CHARGE09");
        map.put(result.getValue("CHARGE10", 0), "CHARGE10");
        map.put(result.getValue("CHARGE11", 0), "CHARGE11");
        map.put(result.getValue("CHARGE12", 0), "CHARGE12");
        map.put(result.getValue("CHARGE13", 0), "CHARGE13");
        map.put(result.getValue("CHARGE14", 0), "CHARGE14");
        map.put(result.getValue("CHARGE15", 0), "CHARGE15");
        map.put(result.getValue("CHARGE16", 0), "CHARGE16");
        map.put(result.getValue("CHARGE17", 0), "CHARGE17");
        map.put(result.getValue("CHARGE18", 0), "CHARGE18");
        map.put(result.getValue("CHARGE19", 0), "CHARGE19");
        map.put(result.getValue("CHARGE20", 0), "CHARGE20");
        map.put(result.getValue("CHARGE21", 0), "CHARGE21");
        map.put(result.getValue("CHARGE22", 0), "CHARGE22");
        map.put(result.getValue("CHARGE23", 0), "CHARGE23");
        map.put(result.getValue("CHARGE24", 0), "CHARGE24");
        map.put(result.getValue("CHARGE25", 0), "CHARGE25");
        map.put(result.getValue("CHARGE26", 0), "CHARGE26");
        map.put(result.getValue("CHARGE27", 0), "CHARGE27");
        map.put(result.getValue("CHARGE28", 0), "CHARGE28");
        map.put(result.getValue("CHARGE29", 0), "CHARGE29");
        map.put(result.getValue("CHARGE30", 0), "CHARGE30");
        return map;

    }

    /**
     *  得到chargeId和收费名称对应map
     * @return Map
     */
    public Map getChargeToRexpdDescMap() {
        TParm result = DictionaryTool.getInstance().getSysCharge("SYS_CHARGE");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        //得到结果数量
        int count = result.getCount();
        //准备的存储map
        Map map = new HashMap();
        //收费代码
        String chargeCode;
        for (int i = 0; i < count; i++) {
            //取出chargercode
            chargeCode = result.getValue("ID", i);
            //放入code和desc的对照
            map.put(chargeCode, result.getValue("CHN_DESC", i));
        }
        return map;
    }

    /**
     * 得到chargeId和收费名称对应map
     * @return Map
     */
    public Map getChargeToChargeDescMap() {
        TParm result = DictionaryTool.getInstance().getSysCharge("SYS_CHARGE");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        //得到结果数量
        int count = result.getCount();
        //准备的存储map
        Map map = new HashMap();
        //收费代码
        String chargeCode;
        for (int i = 0; i < count; i++) {
            //取出chargercode
            chargeCode = result.getValue("ID", i);
            //整理chargecode
            chargeCode = dealStringCharge(chargeCode);
            //放入code和desc的对照
            map.put(chargeCode, result.getValue("CHN_DESC", i));
        }
        return map;
    }

    /**
     * 整理charge代码
     * @param chargeCode String
     * @return String
     */
    public String dealStringCharge(String chargeCode) {
        for (int i = chargeCode.length(); i < 2; i++) {
            chargeCode = "0" + chargeCode;
        }
        return "CHARGE" + chargeCode;
    }

    /**
     * 配置门诊收费金额
     * @return TParm
     */
    public TParm getChargeParm() {
        TParm chargeParm = new TParm();
        chargeParm.setData("CHARGE01", 0);
        chargeParm.setData("CHARGE02", 0);
        chargeParm.setData("CHARGE03", 0);
        chargeParm.setData("CHARGE04", 0);
        chargeParm.setData("CHARGE05", 0);
        chargeParm.setData("CHARGE06", 0);
        chargeParm.setData("CHARGE07", 0);
        chargeParm.setData("CHARGE08", 0);
        chargeParm.setData("CHARGE09", 0);
        chargeParm.setData("CHARGE10", 0);
        chargeParm.setData("CHARGE11", 0);
        chargeParm.setData("CHARGE12", 0);
        chargeParm.setData("CHARGE13", 0);
        chargeParm.setData("CHARGE14", 0);
        chargeParm.setData("CHARGE15", 0);
        chargeParm.setData("CHARGE16", 0);
        chargeParm.setData("CHARGE17", 0);
        chargeParm.setData("CHARGE18", 0);
        chargeParm.setData("CHARGE19", 0);
        chargeParm.setData("CHARGE20", 0);
        chargeParm.setData("CHARGE21", 0);
        chargeParm.setData("CHARGE22", 0);
        chargeParm.setData("CHARGE23", 0);
        chargeParm.setData("CHARGE24", 0);
        chargeParm.setData("CHARGE25", 0);
        chargeParm.setData("CHARGE26", 0);
        chargeParm.setData("CHARGE27", 0);
        chargeParm.setData("CHARGE28", 0);
        chargeParm.setData("CHARGE29", 0);
        chargeParm.setData("CHARGE30", 0);
        return chargeParm;
    }

    /**
     * 得到付款方式的parm
     * @return TParm
     */
//                      CHARGE30,TOT_AMT,REDUCE_AMT,
//                      AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,&
//                      PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,&
//                      PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK

    public TParm getPayParm() {
        TParm payParm = new TParm();
        //应付金额
        payParm.setData("TOT_AMT", 0);
        //折扣/减免金额
        payParm.setData("REDUCE_AMT", 0);
        //应收金额
        payParm.setData("AR_AMT", 0);
        //现金支付
        payParm.setData("PAY_CASH", 0);
        //医疗卡支付
        payParm.setData("PAY_MEDICAL_CARD", 0);
        //银行刷卡
        payParm.setData("PAY_BANK_CARD", 0);
        //医保卡
        payParm.setData("PAY_INS_CARD", 0);
        //支票支付
        payParm.setData("PAY_CHECK", 0);
        //记账
        payParm.setData("PAY_DEBIT", 0);
        //汇款
        payParm.setData("PAY_DRAFT", 0);
        //预交金冲销
        payParm.setData("PAY_BILPAY", 0);
        //医保统筹记账
        payParm.setData("PAY_INS", 0);
        //其它支付(慢性病卡)
        payParm.setData("PAY_OTHER1", 0);
        //其它支付2
        payParm.setData("PAY_OTHER2", 0);
        //支付宝 add by huangtt 20160607
        payParm.setData("ALIPAY", 0);
        return payParm;
    }

    /**
     *
     * @return Map
     */
    public Map getHrexpCodeToHrexpDesc() {
        Map map = new HashMap();
        return map;
    }

    /**
     * 得到票据的连续数据
     * @param parm TParm
     * @return TParm
     */
    public TParm dealVoteNo(TParm parm) {
        int count = parm.getCount();
        TParm returnParm = new TParm();
        int firstNo = 0;
        int endNo = 0;
        for (int i = 0; i < count; i++) {
            firstNo = parm.getInt("", i);
        }

        return returnParm;
    }

    /**
     * 日志输出
     * @param text String 日志内容
     */
    public static void out(String text) {
        Log.getInstance().out(text);
    }

    /**
     * 日志输出
     * @param text String 日志内容
     * @param debug boolean true 强行输出 false 不强行输出
     */
    public static void out(String text, boolean debug) {
        Log.getInstance().out(text, debug);
    }

    /**
     * 错误日志输出
     * @param text String 日志内容
     */
    public static void err(String text) {
        Log.getInstance().err(text);
    }

    /*
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.initClient();

//        System.out.println("wwwwww"+getRate("99", "", "","1CCA0001"));
//        TIOM_AppServer.resetAction();
//
    }

    /**
     * 计算收费项目的费用(原价总价)
     * @param orderParm TParm
     * @return TParm
     */
    public TParm getChargeFeeParm(TParm orderParm) {
//        System.out.println("orderParm健康检查==="+orderParm);
        Map chargeMap = getChargeToRexpCodeMap("O");
        //每次计算费用都要重新初始化收费parm
        TParm chargeParm = getChargeParm();
        int count = orderParm.getCount();
        for (int i = 0; i < count; i++) {
            //收费类别
            String rexpCode = orderParm.getValue("REXP_CODE", i);
            //通过收费类别得到CHARGE
            String charge = chargeMap.get(rexpCode).toString();
            //得到parm中的老金额
            double oldTotAmt = chargeParm.getDouble(charge);
//            System.out.println("计算新金额"+orderParm.getDouble("OWN_AMT", i)+"==="+oldTotAmt);
            //计算新金额
            double totAmt = orderParm.getDouble("OWN_AMT", i) + oldTotAmt;
            //存入收费金额
            chargeParm.setData(charge, totAmt);
        }
        return chargeParm;
    }
    
    /**
     * 计算收费项目的费用(折扣后的总价)
     * @param orderParm TParm
     * @return TParm
     */
    public TParm getChargeFeeArAmtParm(TParm orderParm) {
//        System.out.println("orderParm健康检查==="+orderParm);
        Map chargeMap = getChargeToRexpCodeMap("O");
        //每次计算费用都要重新初始化收费parm
        TParm chargeParm = getChargeParm();
        int count = orderParm.getCount();
        for (int i = 0; i < count; i++) {
            //收费类别
            String rexpCode = orderParm.getValue("REXP_CODE", i);
            //通过收费类别得到CHARGE
            String charge = chargeMap.get(rexpCode).toString();
            //得到parm中的老金额
            double oldTotAmt = chargeParm.getDouble(charge);
//            System.out.println("计算新金额"+orderParm.getDouble("OWN_AMT", i)+"==="+oldTotAmt);
            //计算新金额
            double totAmt = orderParm.getDouble("AR_AMT", i) + oldTotAmt;
            //存入收费金额
            chargeParm.setData(charge, totAmt);
        }
        return chargeParm;
    }

    /**
     * 计算收费项目的费用
     * @param rexp TParm
     * @return TParm
     */
    public TParm getopbChargeParm(TParm rexp) {
//        System.out.println("rexp===" + rexp);
        Map chargeMap = getChargeToRexpCodeMap("O");
        TParm AmtParm = new TParm();
        TParm chargeParm = OPBReceiptTool.getInstance().getOneReceipt(rexp.
            getValue("RECEIPT_NO", 0));
//        System.out.println("chargeParm====。======" + chargeParm);
        int count = rexp.getCount();
        for (int i = 0; i < count; i++) {
            //收费类别
            String rexpCode = rexp.getValue("REXP_CODE", i);
            //通过收费类别得到CHARGE
            String charge = chargeMap.get(rexpCode).toString();
//            System.out.println("charge==========" + charge);
            //得到parm中的费用
            double totAmt = chargeParm.getDouble(charge, 0);
//            System.out.println("totAmt==========" + totAmt);
            //存入收费金额
            AmtParm.addData(rexpCode, totAmt);
        }
//        System.out.println("AmtParm==========" + AmtParm);
        return AmtParm;
    }

}
