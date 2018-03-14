package jdo.spc.phaBaseSyncInfo;

import java.sql.Timestamp;

import javax.jws.WebService;

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
public interface SPCPhaBaseSynchWsTool {

	// 更新HIS的PHA_BASE
	public boolean updatePhaBase(String orderCode, String purchUnit,
			String stockUnit, String dosageUnit, String mediUnit);

	// 更新HIS的PHA_TRANSUNIT
	public boolean updatePhaTransUnit(String flg, String orderCode,
			String purchUnit, String stockUnit, String dosageUnit,
			String mediUnit, double purchQty, double stockQty,
			double dosageQty, double mediQty, String optUser,
			Timestamp optDate, String optTerm, String regionCode);

}
