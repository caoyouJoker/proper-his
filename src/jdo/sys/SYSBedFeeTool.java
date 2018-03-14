package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 床位费用设定Tool
 * </p>
 *
 * <p>
 * Description: 床位费用设定Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.06.16
 * @version 1.0
 */
public class SYSBedFeeTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SYSBedFeeTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return SYSDiagnosisTool
	 */
	public static SYSBedFeeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSBedFeeTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSBedFeeTool() {
		setModuleName("sys\\SYSBedFeeModule.x");
		onInit();
	}

	/**
	 * 取得床位费用信息
	 *
	 * @param bed_class_code
	 *            病床等级
	 * @return
	 */
	public TParm getSysBedFee(String bed_class_code) {
		String sql = SYSSQL.getSYSBedFee(bed_class_code);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
        /**
         * 取得床位费用信息
         * @param bedClassCode String 床位等级
         * @param bedOccuFlg String 占床注记
         * @return TParm
         */
        public TParm getSYSBedFeeOccu(String bedClassCode,
                                      String bedOccuFlg) {
            String sql = SYSSQL.getSYSBedFeeOccu(bedClassCode, bedOccuFlg);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            return result;
        }
	/**
	 * 取得当天的床位费用
	 *
	 * @param bed_class_code
	 *            病床等级
	 * @return
	 */
	public double getSysBdFeeToday(String bed_class_code) {
		String sql = SYSSQL.getSYSBedFee(bed_class_code);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return -1;
		}
		double bed_fee = 0.00;
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			bed_fee += result.getDouble("OWN_PRICE", i);
		}
		return bed_fee;
	}
}
