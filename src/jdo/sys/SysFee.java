package jdo.sys;

import com.dongyang.data.TParm;

public class SysFee {
    /**
     * 引入SYSFeeTool
     * @return SYSFeeTool
     */
    public static SYSFeeTool SYSFeeTool() {
        return SYSFeeTool.getInstance();
    }
    /**
     * 根据orderCode,服务等级得到门诊医嘱单价
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double getFee(String orderCode, String level) {
        TParm result = SYSFeeTool().getFee(orderCode);
        if (result.getErrCode() < 0) {
            return -1;
        }
        if ("2".equals(level)) {
            return result.getDouble("OWN_PRICE2", 0);
        }
        else if ("3".equals(level)) {
            return result.getDouble("OWN_PRICE3", 0);
        }
        else
            return result.getDouble("OWN_PRICE", 0);
    }

    /**
     * 根据orderCode得到价格
     * @param orderCode String
     * @return double
     */
    public static double getFee(String orderCode)
       {
           TParm result=SYSFeeTool().getFee(orderCode);
           if (result.getErrCode() < 0){
                 return -1;
             }
           return result.getDouble("OWN_PRICE",0);
     }
    /**
     * 根据orderCode得到ChargeHospCode
     * @param orderCode String
     * @return String
     */
    public static String getChargeHospCode(String orderCode) {
        TParm chargeHospCode = SYSFeeTool().getChargHospCode(orderCode);
        if (chargeHospCode.getCount() == 0)
            return null;
        return chargeHospCode.getValue("CHARGE_HOSP_CODE", 0);
    }
    /**
     * 给出所有sysfee中的数据
     * @param orderCode String
     * @return TParm
     */
}
