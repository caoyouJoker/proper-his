package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:智能柜出入库查询
 * </p>
 * 
 * <p>
 * Description:智能柜出入库查询
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
 * @author shendr 2013-10-11
 * @version 1.0
 */
public class SPCCabinetSearchTool extends TJDOTool {

	public SPCCabinetSearchTool() {
		super.onInit();
	}

	/**
	 * 实例
	 */
	public static SPCCabinetSearchTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndStockTool
	 */
	public static SPCCabinetSearchTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCCabinetSearchTool();
		return instanceObject;
	}

	/**
	 * 未出库-明细
	 * 
	 * @return
	 */
	public TParm queryInfoAA(TParm parm) {
		String drug_flg = parm.getValue("DRUG_FLG");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con1 = "";
		if ("Y".equals(drug_flg)) {
			con1 = "AND EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		} else {
			con1 = "AND NOT EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.TAKEMED_NO,A.MR_NO,C.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC, "
				+ "A.SPECIFICATION,A.DOSAGE_QTY AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_CABDSPN A,IND_CABINET B,SYS_PATINFO C,SYS_UNIT D "
				+ "WHERE A.ACUM_OUTBOUND_QTY < A.DOSAGE_QTY "
				+ "AND A.STATION_CODE = B.ORG_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.TAKEMED_ORG = '1' " + con1 + con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未出库-汇总
	 * 
	 * @return
	 */
	public TParm queryInfoA(TParm parm) {
		String drug_flg = parm.getValue("DRUG_FLG");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con1 = "";
		if ("Y".equals(drug_flg)) {
			con1 = "AND EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		} else {
			con1 = "AND NOT EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_CABDSPN A,IND_CABINET B,SYS_UNIT D "
				+ "WHERE A.ACUM_OUTBOUND_QTY < A.DOSAGE_QTY "
				+ "AND A.STATION_CODE = B.ORG_CODE "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.TAKEMED_ORG = '1' "
				+ con1
				+ con2
				+ con3
				+ con4
				+ "GROUP BY ORDER_CODE,ORDER_DESC,SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已出库-明细
	 * 
	 * @return
	 */
	public TParm queryInfoBB(TParm parm) {
		String drug_flg = parm.getValue("DRUG_FLG");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con1 = "";
		if ("Y".equals(drug_flg)) {
			con1 = "AND EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		} else {
			con1 = "AND NOT EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT TAKEMED_NO,A.MR_NO,C.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC, "
				+ "A.SPECIFICATION,A.DOSAGE_QTY AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_CABDSPN A,IND_CABINET B,SYS_PATINFO C,SYS_UNIT D "
				+ "WHERE A.ACUM_OUTBOUND_QTY = A.DOSAGE_QTY "
				+ "AND A.STATION_CODE = B.ORG_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.TAKEMED_ORG = '1' " + con1 + con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已出库-汇总
	 * 
	 * @return
	 */
	public TParm queryInfoB(TParm parm) {
		String drug_flg = parm.getValue("DRUG_FLG");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con1 = "";
		if ("Y".equals(drug_flg)) {
			con1 = "AND EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		} else {
			con1 = "AND NOT EXISTS (SELECT C.CTRLDRUGCLASS_CODE "
					+ "FROM SYS_CTRLDRUGCLASS C "
					+ "WHERE C.CTRLDRUGCLASS_CODE=A.CTRLDRUGCLASS_CODE "
					+ "AND C.CTRL_FLG='Y') ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_CABDSPN A,IND_CABINET B,SYS_UNIT D "
				+ "WHERE A.ACUM_OUTBOUND_QTY = A.DOSAGE_QTY "
				+ "AND A.STATION_CODE = B.ORG_CODE "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.TAKEMED_ORG = '1' "
				+ con1
				+ con2
				+ con3
				+ con4
				+ "GROUP BY ORDER_CODE,ORDER_DESC,SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已备药-明细
	 * 
	 * @return
	 */
	public TParm queryInfoCC(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.REQUEST_NO,A.MR_NO,C.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC, "
				+ "A.SPECIFICATION,A.DOSAGE_QTY AS QTY,D.UNIT_CHN_DESC "
				+ "FROM ODI_DSPNM A,IND_CABINET B,SYS_PATINFO C,SYS_UNIT D "
				+ "WHERE A.STATION_CODE = B.ORG_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.REQUEST_NO IS NOT NULL " + con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已备药-汇总
	 * 
	 * @return
	 */
	public TParm queryInfoC(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM ODI_DSPNM A,IND_CABINET B,SYS_PATINFO C,SYS_UNIT D "
				+ "WHERE A.STATION_CODE = B.ORG_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.REQUEST_NO IS NOT NULL "
				+ con2
				+ con3
				+ con4
				+ "GROUP BY ORDER_CODE,ORDER_DESC,SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未备药-明细
	 * 
	 * @return
	 */
	public TParm queryInfoDD(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.REQUEST_NO,A.MR_NO,C.PAT_NAME,A.ORDER_CODE,A.ORDER_DESC, "
				+ "A.SPECIFICATION,A.DOSAGE_QTY AS QTY,D.UNIT_CHN_DESC "
				+ "FROM ODI_DSPNM A,IND_CABINET B,SYS_PATINFO C,SYS_UNIT D "
				+ "WHERE A.STATION_CODE = B.ORG_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.REQUEST_NO IS NULL " + con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未备药-汇总
	 * 
	 * @return
	 */
	public TParm queryInfoD(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.START_DTTM BETWEEN '" + format(start_time)
					+ "' AND '" + format(end_time) + "' ";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND B.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM ODI_DSPNM A,IND_CABINET B,SYS_PATINFO C,SYS_UNIT D "
				+ "WHERE A.STATION_CODE = B.ORG_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.DOSAGE_UNIT = D.UNIT_CODE "
				+ "AND A.REQUEST_NO IS NULL "
				+ con2
				+ con3
				+ con4
				+ "GROUP BY ORDER_CODE,ORDER_DESC,SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已入库-汇总(麻药)
	 * 
	 * @return
	 */
	public TParm queryInfoEDrug(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.OPT_DATE BETWEEN TO_DATE('" + formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND A.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,SUM(1) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_TOXICM A,IND_TOXICD B,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO  "
				+ "AND A.DISPENSE_SEQ_NO = B.DISPENSE_SEQ_NO "
				+ "AND A.CONTAINER_ID = B.CONTAINER_ID "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.IS_STORE = 'Y' "
				+ con2
				+ con3
				+ con4
				+ "GROUP BY B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已入库-明细(麻药)
	 * 
	 * @return
	 */
	public TParm queryInfoEEDrug(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.OPT_DATE BETWEEN TO_DATE('" + formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND A.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.DISPENSE_NO,'' AS MR_NO,'' AS PAT_NAME,B.ORDER_CODE,E.ORDER_DESC, "
				+ "E.SPECIFICATION,1 AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_TOXICM A,IND_TOXICD B,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO  "
				+ "AND A.DISPENSE_SEQ_NO = B.DISPENSE_SEQ_NO "
				+ "AND A.CONTAINER_ID = B.CONTAINER_ID "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.IS_STORE = 'Y'"
				+ con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未入库-汇总(麻药)
	 * 
	 * @return
	 */
	public TParm queryInfoFDrug(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.OPT_DATE BETWEEN TO_DATE('" + formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND A.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,SUM(1) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_TOXICM A,IND_TOXICD B,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO  "
				+ "AND A.DISPENSE_SEQ_NO = B.DISPENSE_SEQ_NO "
				+ "AND A.CONTAINER_ID = B.CONTAINER_ID "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.IS_STORE = 'N' "
				+ con2
				+ con3
				+ con4
				+ "GROUP BY B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未入库-明细(麻药)
	 * 
	 * @return
	 */
	public TParm queryInfoFFDrug(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.OPT_DATE BETWEEN TO_DATE('" + formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND A.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.DISPENSE_NO,'' AS MR_NO,'' AS PAT_NAME,B.ORDER_CODE,E.ORDER_DESC, "
				+ "E.SPECIFICATION,1 AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_TOXICM A,IND_TOXICD B,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO  "
				+ "AND A.DISPENSE_SEQ_NO = B.DISPENSE_SEQ_NO "
				+ "AND A.CONTAINER_ID = B.CONTAINER_ID "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.IS_STORE = 'N'"
				+ con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已入库-汇总(普药)
	 * 
	 * @return
	 */
	public TParm queryInfoENormal(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND C.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_DISPENSEM A,IND_DISPENSED B,IND_CABINET C,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO  "
				+ "AND A.APP_ORG_CODE=C.ORG_CODE  "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND B.IS_PUTAWAY = 'Y' "
				+ con2
				+ con3
				+ con4
				+ "GROUP BY B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 已入库-明细(普药)
	 * 
	 * @return
	 */
	public TParm queryInfoEENormal(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND C.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.DISPENSE_NO,'' AS MR_NO,'' AS PAT_NAME,B.ORDER_CODE,E.ORDER_DESC, "
				+ "E.SPECIFICATION,B.ACTUAL_QTY AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_DISPENSEM A,IND_DISPENSED B,IND_CABINET C,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO "
				+ "AND A.APP_ORG_CODE=C.ORG_CODE "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND B.IS_PUTAWAY = 'Y' " + con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未入库-汇总(普药)
	 * 
	 * @return
	 */
	public TParm queryInfoFNormal(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND C.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,SUM(B.ACTUAL_QTY) AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_DISPENSEM A,IND_DISPENSED B,IND_CABINET C,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO  "
				+ "AND A.APP_ORG_CODE=C.ORG_CODE  "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND B.IS_PUTAWAY = 'N' "
				+ con2
				+ con3
				+ con4
				+ "GROUP BY B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,D.UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未入库-明细(普药)
	 * 
	 * @return
	 */
	public TParm queryInfoFFNormal(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String cabinet_id = parm.getValue("CABINET_ID");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND B.ORDER_CODE = '" + order_code + "'";
		}
		String con3 = "";
		if ((!StringUtil.isNullString(start_time))
				&& (!StringUtil.isNullString(end_time))) {
			con3 = "AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ formatE(start_time)
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ formatE(end_time) + "','YYYY-MM-DD HH24:MI:SS')";
		}
		String con4 = "";
		if (!StringUtil.isNullString(cabinet_id)) {
			con4 = "AND C.CABINET_ID = '" + cabinet_id + "'";
		}
		String sql = "SELECT B.DISPENSE_NO,'' AS MR_NO,'' AS PAT_NAME,B.ORDER_CODE,E.ORDER_DESC, "
				+ "E.SPECIFICATION,B.ACTUAL_QTY AS QTY,D.UNIT_CHN_DESC "
				+ "FROM IND_DISPENSEM A,IND_DISPENSED B,IND_CABINET C,SYS_UNIT D,SYS_FEE E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO "
				+ "AND A.APP_ORG_CODE=C.ORG_CODE "
				+ "AND B.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND B.IS_PUTAWAY = 'N' " + con2 + con3 + con4;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 格式化时间
	 * 
	 * @param str
	 * @return
	 */
	private String format(String str) {
		str = str.substring(0, str.indexOf("."));
		str = str.replaceAll("-", "");
		str = str.replaceAll(":", "");
		str = str.replaceAll(" ", "");
		return str;
	}

	/**
	 * 格式化时间
	 * 
	 * @param str
	 * @return
	 */
	private String formatE(String str) {
		str = str.substring(0, str.indexOf("."));
		return str;
	}

}
