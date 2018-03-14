package jdo.spc.phaBaseSyncInfo;

import java.sql.Timestamp;

import javax.jws.WebService;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 物联网与HIS PHA_BASE与PHA_TRANSUNIT 表同步 werbservice接口
 * </p>
 * 
 * <p>
 * Description: 物联网与HIS PHA_BASE与PHA_TRANSUNIT表同步 werbservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author shendr 2013-08-12
 * @version 1.0
 */
@WebService
public class SPCPhaBaseSynchWsToolImpl implements SPCPhaBaseSynchWsTool {

	// 更新HIS的PHA_BASE
	@Override
	public boolean updatePhaBase(String orderCode, String purchUnit,
			String stockUnit, String dosageUnit, String mediUnit) {
		try {
			String sql = "UPDATE PHA_BASE SET PURCH_UNIT='" + purchUnit
					+ "',STOCK_UNIT='" + stockUnit + "',DOSAGE_UNIT='"
					+ dosageUnit + "',MEDE_UNIT='" + mediUnit + "' "
					+ "WHERE ORDER_CODE='" + orderCode + "'";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode() < 0
					|| (Integer) result.getData("RETURN") == 0)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// 更新HIS的PHA_TRANSUNIT
	@Override
	public boolean updatePhaTransUnit(String flg, String orderCode,
			String purchUnit, String stockUnit, String dosageUnit,
			String mediUnit, double purchQty, double stockQty,
			double dosageQty, double mediQty, String optUser,
			Timestamp optDate, String optTerm, String regionCode) {
		try {
			String sql = "";
			if (flg == "insert") {
				sql = "INSERT INTO PHA_TRANSUNIT"
						+ "(ORDER_CODE,PURCH_UNIT,PURCH_QTY,STOCK_UNIT,STOCK_QTY,"
						+ "DOSAGE_UNIT,DOSAGE_QTY,MEDI_UNIT,MEDI_QTY,OPT_USER,"
						+ "OPT_DATE,OPT_TERM,REGION_CODE) VALUES('"
						+ orderCode
						+ "','"
						+ purchUnit
						+ "',"
						+ purchQty
						+ ",'"
						+ stockUnit
						+ "',"
						+ stockQty
						+ ",'"
						+ dosageUnit
						+ "',"
						+ dosageQty
						+ ",'"
						+ mediUnit
						+ "',"
						+ mediQty
						+ ",'"
						+ optUser
						+ "',"
						+ optDate
						+ ",'" + optTerm + "','" + regionCode + "')";
			} else if (flg == "update") {
				sql = "UPDATE PHA_TRANSUNIT SET PURCH_UNIT = '', "
						+ "PURCH_QTY = '', " + "STOCK_UNIT = '', "
						+ " STOCK_QTY = '', " + "DOSAGE_UNIT = '', "
						+ "DOSAGE_QTY = '', " + " MEDI_UNIT = '', "
						+ "MEDI_QTY = '' " + "WHERE ORDER_CODE = ''";
			}
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode() < 0
					|| (Integer) result.getData("RETURN") == 0)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
