package jdo.util;

import java.util.Map;

import jdo.bil.BIL;
import jdo.ibs.IBSTool;
import jdo.sys.SYSBedFeeTool;
import jdo.sys.SYSChargeDetailTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TStrike;

/**
 * 
 * <p>
 * Title: 费用共用
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Charge extends TStrike {
	/**
	 * 根据,ordercode算收费,三身份方法
	 * 
	 * @param ctz1Code
	 *            String身份
	 * @param ctz2Code
	 *            String
	 * @param ctz3Code
	 *            String
	 * @param orderCode
	 *            String
	 * @return double
	 */
	public double chargeCTZ(String ctz1Code, String ctz2Code, String ctz3Code,
			String orderCode) {
		return BIL.chargeCTZ(ctz1Code, ctz2Code, ctz3Code, orderCode);
	}

	/**
	 * 得到账务序号,账务子序号,价格信息(INW,UDD调用)
	 * 
	 * @param map
	 *            Map
	 * @return Map
	 */
	public Map getIBSOrderData(Map map) {
		TParm parm = new TParm(map);
		return IBSTool.getInstance().getIBSOrderData(parm).getData();
	}

	/**
	 * 新增费用(For INW , UDD)
	 * 
	 * @param map
	 *            Map
	 * @param connection
	 *            TConnection
	 * @return Map (dataType=3 护士站,dataType=2 药房)
	 */
	public Map insertIBSOrder(Map map, TConnection connection) {
		TParm parm = new TParm(map);
		return IBSTool.getInstance().insertIBSOrder(parm, connection).getData();
	}

	/**
	 * 取得身份折扣
	 * 
	 * @param ctz_code
	 *            身份代码
	 * @param charge_hosp_code
	 *            院内费用代码
	 * @return
	 */
	public double getSYSChargeDetail(String ctz_code, String charge_hosp_code) {
		if (isClientlink())
			return (Double) callServerMethod(ctz_code, charge_hosp_code);
		return SYSChargeDetailTool.getInstance().selectdata(ctz_code,
				charge_hosp_code).getDouble("DISCOUNT_RATE", 0);
	}

	/**
	 * 根据床位等级取得当天的床位费用
	 * 
	 * @param bed_class_code
	 *            病床等级
	 * @return
	 */
	public double getSYSBedFeeToday(String bed_class_code) {
		if (isClientlink())
			return (Double) callServerMethod(bed_class_code);
		return SYSBedFeeTool.getInstance().getSysBdFeeToday(bed_class_code);
	}

	/**
	 * 根据床位等级取得床位费用信息
	 * 
	 * @param bed_class_code
	 *            病床等级
	 * @return Map TParm{BED_CLASS_CODE, ORDER_CODE, OWN_PRICE, CHK_OUT_TIME,
	 *         CODE_CALC_KIND}
	 */
	public Map getSYSBedFee(String bed_class_code) {
		if (isClientlink())
			return (Map) callServerMethod(bed_class_code);
		return SYSBedFeeTool.getInstance().getSysBedFee(bed_class_code)
				.getData();
	}
}
