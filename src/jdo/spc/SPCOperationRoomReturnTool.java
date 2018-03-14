package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:手术室麻药归还Tool
 * </p>
 * 
 * <p>
 * Description:手术室麻药归还Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-08-12
 * @version 1.0
 */
public class SPCOperationRoomReturnTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SPCOperationRoomReturnTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCOperationRoomReturnTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCOperationRoomReturnTool();
		return instanceObject;
	}

	/**
	 * 查询归还
	 */
	public TParm queryInfo(String dispense_no, String toxic_id, String showFlg) {
		String con1 = "";
		if (showFlg == "Y") {
			con1 = "A.RETURN_FLG='Y' ";
		} else if (showFlg == "N") {
			con1 = "A.RETURN_FLG='N' ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(dispense_no)) {
			con2 = "AND A.DISPENSE_NO='" + dispense_no + "' ";
		}
		String con3 = "";
		if (!StringUtil.isNullString(toxic_id)) {
			con3 = "AND A.TOXIC_ID='" + toxic_id + "' ";
		}
		String sql = "SELECT TOXIC_ID,ORDER_DESC,SPECIFICATION,BATCH_NO,VALID_DATE,UNIT_CHN_DESC,DISPENSE_NO "
				+ "FROM IND_TOXICD A,SYS_UNIT B,PHA_BASE C "
				+ "WHERE "
				+ con1
				+ "AND A.UNIT_CODE=B.UNIT_CODE "
				+ "AND A.ORDER_CODE=C.ORDER_CODE "
				+ con2
				+ con3
				+ "  AND A.TOXIC_ID NOT IN ( SELECT D.BAR_CODE "
				+ "                FROM SPC_INV_RECORD D "
				+ "               WHERE A.TOXIC_ID = D.BAR_CODE) ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询回收
	 */
	public TParm queryInfoo(String flg, String dispense_no, String toxic_id) {
		String con1 = "";
		if (flg == "Y") {
			con1 = "AND D.RECLAIM_USER IS NOT NULL ";
		} else {
			con1 = "AND D.RECLAIM_USER IS NULL  ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(dispense_no)) {
			con2 = "AND A.DISPENSE_NO='" + dispense_no + "' ";
		}
		String con3 = "";
		if (!StringUtil.isNullString(toxic_id)) {
			con3 = "AND A.TOXIC_ID='" + toxic_id + "' ";
		}
		String sql = "SELECT TOXIC_ID,C.ORDER_DESC,SPECIFICATION,BATCH_NO,A.VALID_DATE,UNIT_CHN_DESC,DISPENSE_NO "
				+ "FROM IND_TOXICD A,SYS_UNIT B,PHA_BASE C,SPC_INV_RECORD D "
				+ "WHERE A.UNIT_CODE=B.UNIT_CODE "
				+ "AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.TOXIC_ID=D.BAR_CODE " + con1 + con2 + con3;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 回收
	 * 
	 * @return
	 */
	public TParm saveInfo(String user, String code) {
		String sql = "UPDATE SPC_INV_RECORD SET RECLAIM_USER='" + user
				+ "',RECLAIM_DATE=SYSDATE " + "WHERE BAR_CODE='" + code + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 根据麻精条码号查容器编号
	 * 
	 * @return
	 */
	public TParm queryINDToxicDByToxicIdInfo(String toxic_id) {
		String sql = "SELECT CONTAINER_ID FROM IND_TOXICD "
				+ "WHERE TOXIC_ID='" + toxic_id + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 更新IND_TOXICD
	 * 
	 * @return
	 */
	public TParm updateINDToxicDInfo(String toxic_id, TConnection conn) {
		String sql = "UPDATE IND_TOXICD SET RETURN_FLG='Y' "
				+ "WHERE TOXIC_ID='" + toxic_id + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 更新IND_CONTAINERD
	 * 
	 * @return
	 */
	public TParm updateINDContainerDInfo(String container_id, String toxic_id,
			String cabinet_id, TConnection conn) {
		String sql = "UPDATE IND_CONTAINERD SET CONTAINER_ID='" + container_id
				+ "',CABINET_ID='" + cabinet_id + "' WHERE TOXIC_ID='"
				+ toxic_id + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

}
