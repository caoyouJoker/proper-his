package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>
 * Title: 静配中心成品审核工具类
 * </p>
 * 
 * <p>
 * Description: 静配中心成品审核工具类
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
 * @author zhangy 2013.07.21
 * @version 1.0
 */
public class IVAAllocatecheckTool extends TJDODBTool {

	/**
	 * 实例
	 */
	public static IVAAllocatecheckTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndAgentTool
	 */
	public static IVAAllocatecheckTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVAAllocatecheckTool();
		}
		return instanceObject;
	}

	/*
	 * 查询未调配信息
	 */
	public TParm queryInfo(TParm parm) {
		String sql = "SELECT C.STATION_DESC,"
				+ " B.PAT_NAME,"
				+ " A.CASE_NO,"
				+ " A.MR_NO, "
				+ " D.IVA_CHECK_USER "
				+ " FROM ODI_DSPND D,"
				+ " ODI_DSPNM A, "
				+ " SYS_PATINFO B,"
				+ " SYS_STATION C "
				+ " WHERE D.BAR_CODE = '"
				+ parm.getValue("BAR_CODE")
				+ "' AND D.IVA_DEPLOY_USER IS NOT NULL "
				// +" AND D.IVA_CHECK_USER IS NULL "
				+ " AND D.IVA_RETN_USER IS NULL " + " AND D.IVA_FLG = 'Y' "
				+ " AND A.CASE_NO = D.CASE_NO "
				+ " AND A.ORDER_NO = D.ORDER_NO "
				+ " AND A.ORDER_SEQ = D.ORDER_SEQ "
				+ " AND D.ORDER_DATE || D.ORDER_DATETIME BETWEEN A.START_DTTM "
				+ " AND A.END_DTTM " + " AND A.IVA_FLG = 'Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.MR_NO = B.MR_NO "
				+ " AND A.STATION_CODE = C.STATION_CODE "
				+ " GROUP BY C.STATION_DESC," + " B.PAT_NAME," + " A.CASE_NO,"
				+ " A.MR_NO," + " D.IVA_CHECK_USER";
		// System.out.println("case_no"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// liuyalin 20170328 add
	/*
	 * 肠外营养液筛选
	 */
	public TParm querycheck(TParm cparm) {
		String sql = "SELECT ORDER_CODE FROM IND_MONITOR_MED WHERE MONITOR_TYPE= 'NUTR'";// NUTR:肠外营养液配置
		// System.out.println("case_no"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 患者药品明细
	 */
	public TParm querydetail(TParm tparm) {
		String sql = "SELECT  A.LINKMAIN_FLG AS SELECT_FLG,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,"
				+ " A.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,B.DOSAGE_QTY || F.UNIT_CHN_DESC AS DOSAGE_QTY, "
				+ "B.ORDER_DATE || B.ORDER_DATETIME AS EXEC_DATE,"
				+ "C.FREQ_CHN_DESC,A.FREQ_CODE,D.ROUTE_CHN_DESC,A.LINK_NO,B.BAR_CODE "
				+ "FROM ODI_DSPNM A,ODI_DSPND B,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_UNIT E,SYS_UNIT F "
				+ "WHERE  A.CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND A.IVA_FLG = 'Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM "
				+ " AND B.BAR_CODE = '"
				+ tparm.getValue("BAR_CODE")
				+ "' AND B.IVA_DEPLOY_USER IS NOT NULL "
				+ " AND B.IVA_CHECK_USER IS NULL "
				+ " AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE AND A.MEDI_UNIT=E.UNIT_CODE "
				+ " AND B.DOSAGE_UNIT = F.UNIT_CODE "
				+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME, CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END";
		// System.out.println("sql========"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 成品核对
	 */
	public TParm updateInfoM(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPNM SET IVA_CHECK_USER = '"
				+ tparm.getValue("IVA_CHECK_USER") + "', "
				+ " IVA_CHECK_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO") + "' AND ORDER_NO = '"
				+ tparm.getValue("ORDER_NO") + "' AND ORDER_SEQ = '"
				+ tparm.getValue("ORDER_SEQ") + "' AND START_DTTM = '"
				+ tparm.getValue("START_DTTM") + "' AND END_DTTM = '"
				+ tparm.getValue("END_DTTM")
				+ "' AND IVA_FLG='Y' AND IVA_DEPLOY_USER IS NOT NULL";
		// System.out.println("sqlM=========="+sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	public TParm updateInfoD(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPND SET IVA_CHECK_USER='"
				+ tparm.getValue("IVA_CHECK_USER") + "',"
				+ "IVA_CHECK_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO") + "' AND ORDER_NO='"
				+ tparm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
				+ tparm.getValue("ORDER_SEQ") + "' AND ORDER_DATE = '"
				+ tparm.getValue("ORDER_DATE") + "' AND BATCH_CODE = '"
				+ tparm.getValue("BATCH_CODE") + "' AND BAR_CODE = '"
				+ tparm.getValue("BAR_CODE") + "' AND ORDER_DATETIME = '"
				+ tparm.getValue("ORDER_DATETIME")
				+ "' AND IVA_FLG='Y' AND IVA_DEPLOY_USER IS NOT NULL "
				+ " AND IVA_CHECK_USER IS NULL";
		// System.out.println("sqlD=========="+sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	// // liuyalin 20170331 add
	// /*
	// * 住院配液增加配液费存于表IBS_ORDD
	// */
	// public TParm insertIBSOrder(TParm tparm, TConnection connection) {
	// TParm result = new TParm();
	// Timestamp billDate = SystemTool.getInstance().getDate();
	// // TParm inMParm = new TParm();
	// // inMParm.setData("CASE_NO",
	// // tparm.getData("CASE_NO") == null ? new TNull(String.class)
	// // : tparm.getData("CASE_NO"));
	// // inMParm.setData(
	// // "CASE_NO_SEQ",
	// // tparm.getData("IBS_CASE_NO_SEQ") == null ? -1 : tparm
	// // .getData("IBS_CASE_NO_SEQ"));
	// // inMParm.setData("BILL_DATE", billDate);
	// // inMParm.setData("IPD_NO",
	// // tparm.getData("IPD_NO") == null ? -1 : tparm.getData("IPD_NO"));
	// // inMParm.setData("MR_NO",
	// // tparm.getData("MR_NO") == null ? -1 : tparm.getData("MR_NO"));
	// // inMParm.setData("DEPT_CODE", tparm.getData("DEPT_CODE") == null ? -1
	// // : tparm.getData("DEPT_CODE"));
	// // inMParm.setData(
	// // "STATION_CODE",
	// // tparm.getData("STATION_CODE") == null ? -1 : tparm
	// // .getData("STATION_CODE"));
	// // inMParm.setData("BED_NO",
	// // tparm.getData("BED_NO") == null ? -1 : tparm.getData("BED_NO"));
	// // inMParm.setData("DATA_TYPE", tparm.getData("DATA_TYPE") == null ? -1
	// // : tparm.getData("DATA_TYPE"));
	// // inMParm.setData("BILL_NO", "");
	// // inMParm.setData("OPT_USER", tparm.getData("OPT_USER"));
	// // inMParm.setData("OPT_DATE", tparm.getData("OPT_DATE"));
	// // inMParm.setData("OPT_TERM", tparm.getData("OPT_TERM"));
	// // TParm stationInfo = SYSStationTool.getInstance().selStationRegion(
	// // tparm.getValue("STATION_CODE", 0));
	// // inMParm.setData("REGION_CODE", stationInfo.getData("REGION_CODE", 0));
	// // System.out.println("插入OrderM数据" + inMParm);
	// // result = IBSOrdermTool.getInstance().insertdata(inMParm, connection);
	// // if (result.getErrCode() < 0) {
	// // err(result.getErrName() + " " + result.getErrText());
	// // return result;
	// // }
	// // double totalAmtForADM = 0.00;
	// TParm inDParm = new TParm();
	// inDParm.setData("CASE_NO",
	// tparm.getData("CASE_NO") == null ? new TNull(String.class)
	// : tparm.getData("CASE_NO"));
	// inDParm.setData(
	// "CASE_NO_SEQ",
	// tparm.getData("IBS_CASE_NO_SEQ") == null ? -1 : tparm
	// .getData("IBS_CASE_NO_SEQ"));
	// inDParm.setData("SEQ_NO", tparm.getData("IBS_SEQ_NO") == null ? -1
	// : tparm.getData("IBS_SEQ_NO"));
	//
	// inDParm.setData("BILL_DATE", billDate);
	// inDParm.setData("ORDER_NO",
	// tparm.getData("ORDER_NO") == null ? new TNull(String.class)
	// : tparm.getData("ORDER_NO"));
	// inDParm.setData("ORDER_SEQ", tparm.getData("ORDER_SEQ") == null ? "0"
	// : tparm.getData("ORDER_SEQ"));
	// TParm sysFeeInfo = SYSFeeTool.getInstance().getFeeAllData(
	// tparm.getValue("ORDER_CODE"));
	// inDParm.setData("ORDER_CODE",
	// sysFeeInfo.getData("IBS_ORDER_CODE") == null ? new TNull(
	// String.class) : sysFeeInfo.getData("IBS_ORDER_CODE"));
	// inDParm.setData("ORDER_CAT1_CODE", sysFeeInfo
	// .getData("ORDER_CAT1_CODE") == null ? new TNull(String.class)
	// : sysFeeInfo.getData("ORDER_CAT1_CODE"));
	// inDParm.setData("ORDERSET_GROUP_NO",
	// tparm.getData("ORDERSET_GROUP_NO") == null ? new TNull(
	// String.class) : tparm.getData("ORDERSET_GROUP_NO"));
	// inDParm.setData(
	// "ORDERSET_CODE",
	// tparm.getData("ORDERSET_CODE") == null ? new TNull(String.class)
	// : tparm.getData("ORDERSET_CODE"));
	// inDParm.setData("DEPT_CODE",
	// tparm.getData("ORDER_DEPT_CODE") == null ? new TNull(
	// String.class) : tparm.getData("ORDER_DEPT_CODE"));
	// inDParm.setData("STATION_CODE",
	// tparm.getData("STATION_CODE") == null ? new TNull(String.class)
	// : tparm.getData("STATION_CODE"));
	// inDParm.setData(
	// "DR_CODE",
	// tparm.getData("ORDER_DR_CODE") == null ? new TNull(String.class)
	// : tparm.getData("ORDER_DR_CODE"));
	// inDParm.setData("EXE_DEPT_CODE",
	// tparm.getData("EXEC_DEPT_CODE") == null ? new TNull(
	// String.class) : tparm.getData("EXEC_DEPT_CODE"));
	// inDParm.setData("EXE_STATION_CODE",
	// tparm.getData("STATION_CODE") == null ? new TNull(String.class)
	// : tparm.getData("STATION_CODE"));
	// inDParm.setData("EXE_DR_CODE",
	// tparm.getData("OPT_USER") == null ? new TNull(String.class)
	// : tparm.getData("OPT_USER"));
	// inDParm.setData("MEDI_QTY", tparm.getData("MEDI_QTY") == null ? 0.00
	// : tparm.getData("MEDI_QTY"));
	// inDParm.setData("MEDI_UNIT",
	// tparm.getData("MEDI_UNIT") == null ? new TNull(String.class)
	// : tparm.getData("MEDI_UNIT"));
	// inDParm.setData("DOSE_CODE",
	// tparm.getData("DOSE_CODE") == null ? new TNull(String.class)
	// : tparm.getData("DOSE_CODE")); // 剂型;剂型类型
	// inDParm.setData("FREQ_CODE",
	// tparm.getData("FREQ_CODE") == null ? new TNull(String.class)
	// : tparm.getData("FREQ_CODE"));
	// inDParm.setData("TAKE_DAYS", tparm.getData("TAKE_DAYS") == null ? 0
	// : tparm.getData("TAKE_DAYS"));
	// inDParm.setData(
	// "DOSAGE_QTY",
	// tparm.getData("DOSAGE_QTY") == null ? 0.00 : (-1)
	// * tparm.getDouble("DOSAGE_QTY"));
	// inDParm.setData("DOSAGE_UNIT",
	// sysFeeInfo.getData("DOSAGE_UNIT") == null ? new TNull(
	// String.class) : sysFeeInfo.getData("DOSAGE_UNIT"));
	// inDParm.setData("OWN_PRICE", tparm.getData("OWN_PRICE") == null ? 0.00
	// : tparm.getData("OWN_PRICE"));
	// inDParm.setData("NHI_PRICE", tparm.getData("NHI_PRICE") == null ? 0.00
	// : tparm.getDouble("NHI_PRICE"));
	// inDParm.setData("TOT_AMT",
	// (-1) * StringTool.round(tparm.getDouble("TOT_AMT"), 2));
	// inDParm.setData("OWN_FLG", "Y");
	// inDParm.setData("BILL_FLG", "Y");
	// inDParm.setData("REXP_CODE",
	// tparm.getData("REXP_CODE") == null ? new TNull(String.class)
	// : tparm.getData("REXP_CODE"));
	// inDParm.setData("HEXP_CODE",
	// sysFeeInfo.getData("CHARGE_HOSP_CODE") == null ? new TNull(
	// String.class) : sysFeeInfo.getData("CHARGE_HOSP_CODE"));
	// inDParm.setData("BILL_NO", new TNull(String.class));
	// if ("0".equals(tparm.getValue("DATA_TYPE"))) {
	// billDate = tparm.getTimestamp("BILL_DATE");
	// inDParm.setData("BEGIN_DATE", billDate);
	// inDParm.setData("END_DATE", billDate);
	// }
	// inDParm.setData("BEGIN_DATE",
	// tparm.getData("BEGIN_DATE") == null ? new TNull(String.class)
	// : tparm.getData("BEGIN_DATE"));
	// inDParm.setData("END_DATE",
	// tparm.getData("END_DATE") == null ? new TNull(String.class)
	// : tparm.getData("END_DATE"));
	// inDParm.setData("OWN_AMT",
	// (-1) * StringTool.round(tparm.getDouble("OWN_AMT"), 4));
	// inDParm.setData("OWN_RATE", tparm.getData("OWN_RATE") == null ? 0.00
	// : tparm.getData("OWN_RATE"));
	// inDParm.setData("REQUEST_FLG", "N");
	// inDParm.setData("REQUEST_NO", new TNull(String.class));
	// inDParm.setData("INV_CODE", new TNull(String.class));
	// inDParm.setData("OPT_USER", Operator.getID());
	//
	// inDParm.setData("OPT_TERM", Operator.getIP());
	// inDParm.setData("CAT1_TYPE",
	// sysFeeInfo.getData("CAT1_TYPE") == null ? new TNull(
	// String.class) : sysFeeInfo.getData("CAT1_TYPE"));
	// inDParm.setData("INDV_FLG", tparm.getData("HIDE_FLG") == null ? "N"
	// : tparm.getData("HIDE_FLG"));
	// inDParm.setData("COST_AMT", tparm.getData("COST_AMT") == null ? 0.00
	// : (-1) * tparm.getDouble("COST_AMT"));
	// inDParm.setData("ORDER_CHN_DESC",
	// sysFeeInfo.getData("ORDER_DESC", 0) == null ? new TNull(
	// String.class) : sysFeeInfo.getData("ORDER_DESC", 0));
	// inDParm.setData("DS_FLG",
	// tparm.getData("DS_FLG") == null ? "N" : tparm.getData("DS_FLG"));
	// inDParm.setData("EXEC_DATE", billDate);// 执行时间
	// inDParm.setData(
	// "CLNCPATH_CODE",
	// tparm.getData("CLNCPATH_CODE") == null ? "N" : tparm
	// .getData("CLNCPATH_CODE"));
	// inDParm.setData("SCHD_CODE", tparm.getData("SCHD_CODE") == null ? "N"
	// : tparm.getData("SCHD_CODE"));
	// // inDParm.setData(
	// // "COST_CENTER_CODE",
	// // DeptTool.getInstance().getCostCenter(
	// // inDParm.getValue("EXEC_DEPT_CODE"),
	// // inDParm.getValue("EXE_STATION_CODE")) == null ? new TNull(
	// // String.class) : DeptTool.getInstance()
	// // .getCostCenter(inDParm.getValue("EXEC_DEPT_CODE"),
	// // inDParm.getValue("EXE_STATION_CODE")));
	// System.out.println("插入OrderD数据" + inDParm);
	// result = IBSOrderdTool.getInstance().insertdata(inDParm, connection);
	// if (result.getErrCode() < 0) {
	// err(result.getErrName() + " " + result.getErrText());
	// return result;
	// }
	// return result;
	// }

	// liuyalin add 20170324
	/*
	 * 住院配液增加配液费存于表IBS_ORDD
	 */
	public TParm insertIBSOrder(TParm tparm, TConnection conn) {
		int seqNo = tparm.getInt("IBS_SEQ_NO");
		String begin_date = tparm.getValue("BEGIN_DATE");
		String end_date = tparm.getValue("END_DATE");
		//
		if (!StringUtils.isEmpty(begin_date) && !StringUtils.isEmpty(end_date)) {
			begin_date = begin_date.substring(0, 19).replace(".0", "").replace(
					"-", "/");
			end_date = end_date.substring(0, 19).replace(".0", "").replace("-",
					"/");
		}
		String sql = "INSERT INTO IBS_ORDD (CASE_NO,CASE_NO_SEQ,SEQ_NO,BILL_DATE,ORDER_NO,"
				+ "ORDER_SEQ,ORDER_CODE,ORDER_CAT1_CODE,CAT1_TYPE,ORDERSET_GROUP_NO,ORDERSET_CODE,"
				+ "INDV_FLG,DEPT_CODE,STATION_CODE,DR_CODE,EXE_DEPT_CODE,EXE_STATION_CODE,EXE_DR_CODE,"
				+ "MEDI_QTY,MEDI_UNIT,DOSE_CODE,FREQ_CODE,TAKE_DAYS,"
				+ "DOSAGE_QTY,DOSAGE_UNIT,OWN_PRICE,NHI_PRICE,TOT_AMT,"
				+ "OWN_FLG,BILL_FLG,BILL_NO,HEXP_CODE,REXP_CODE,"
				+ "BEGIN_DATE,END_DATE,OWN_AMT,OWN_RATE,REQUEST_FLG,"
				+ "REQUEST_NO,INV_CODE,OPT_USER,OPT_DATE,OPT_TERM,COST_AMT,"
				+ "ORDER_CHN_DESC,COST_CENTER_CODE,SCHD_CODE,CLNCPATH_CODE,DS_FLG,KN_FLG,TOT_AMT2,EXEC_DATE) "
				+ "VALUES ('"
				+ tparm.getValue("CASE_NO")
				+ "','"
				+ tparm.getValue("IBS_CASE_NO_SEQ")
				+ "',"
				+ seqNo
				+ ", SYSDATE,'"
				+ tparm.getValue("ORDER_NO")
				+ "','"
				+ tparm.getValue("ORDER_SEQ")
				+ "','"
				+ tparm.getValue("IBS_ORDER_CODE")
				+ "','"
				+ tparm.getValue("ORDER_CAT1_CODE")
				+ "','"
				+ tparm.getValue("CAT1_TYPE")
				+ "','"
				+ tparm.getValue("ORDERSET_GROUP_NO")
				+ "','"
				+ tparm.getValue("ORDERSET_CODE")
				+ "','"
				+ tparm.getValue("INDV_FLG")
				+ "','"
				+ tparm.getValue("DEPT_CODE")
				+ "','"
				+ tparm.getValue("STATION_CODE")
				+ "','"
				+ tparm.getValue("DR_CODE")
				+ "','"
				+ tparm.getValue("EXE_DEPT_CODE")
				+ "','"
				+ tparm.getValue("EXE_STATION_CODE")
				+ "','"
				+ tparm.getValue("EXE_DR_CODE")
				+ "','"
				+ tparm.getValue("MEDI_QTY")
				+ "','"
				+ tparm.getValue("MEDI_UNIT")
				+ "','"
				+ tparm.getValue("DOSE_CODE")
				+ "','"
				+ tparm.getValue("FREQ_CODE")
				+ "','"
				+ tparm.getValue("TAKE_DAYS")
				+ "','"
				+ 1
				+ "','"
				+ 24
				+ "','"
				+ tparm.getValue("OWN_PRICE")
				+ "','"
				// + tparm.getValue("NHI_PRICE")
				+ 0
				+ "','"
				+ tparm.getValue("OWN_PRICE")
				+ "','"
				+ tparm.getValue("OWN_FLG")
				+ "','"
				+ tparm.getValue("BILL_FLG")
				+ "','"
				+ tparm.getValue("BILL_NO")
				+ "','"
				+ 230.003
				+ "','"
				+ "026"
				+ "',TO_DATE ('"
				+ begin_date
				+ "','YYYY/MM/DD HH24:MI:SS'),TO_DATE ('"
				+ end_date
				+ "','YYYY/MM/DD HH24:MI:SS'),'"
				+ tparm.getValue("OWN_PRICE")
				+ "','"
				// + tparm.getValue("OWN_RATE")
				+ 1
				+ "','"
				// + tparm.getValue("REQUEST_FLG")
				+ 'N'
				+ "','"
				+ tparm.getValue("REQUEST_NO")
				+ "','"
				+ tparm.getValue("INV_CODE")
				+ "','"
				+ tparm.getValue("OPT_USER")
				+ "',SYSDATE,'"
				+ tparm.getValue("OPT_TERM")
				+ "','"
				+ 0
				+ "','"
				+ tparm.getValue("ORDER_CHN_DESC")
				+ "','"
				+ tparm.getValue("COST_CENTER_CODE")
				+ "','"
				+ tparm.getValue("SCHD_CODE")
				+ "','"
				+ tparm.getValue("CLNCPATH_CODE")
				+ "','"
				+ tparm.getValue("DS_FLG") + "','"
				// + tparm.getValue("KN_FLG")
				+ 'N' + "','" + tparm.getValue("TOT_AMT2")
				// + "','"+ tparm.getValue("EXEC_DATE") + "' )";
				+ "',SYSDATE) ";
		// System.out.println("sqlD==========" + sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/*
	 * 住院配液增加配液费存于表IBS_ORDD
	 */
	public TParm insertIBSOrderM(TParm tparm, TConnection conn) {
		// CASE_NO VARCHAR2(20 BYTE) NOT NULL,
		// CASE_NO_SEQ NUMBER(5) NOT NULL,
		// BILL_DATE DATE,
		// IPD_NO VARCHAR2(20 BYTE),
		// MR_NO VARCHAR2(20 BYTE),
		// DEPT_CODE VARCHAR2(20 BYTE),
		// STATION_CODE VARCHAR2(20 BYTE),
		// BED_NO VARCHAR2(20 BYTE),
		// DATA_TYPE VARCHAR2(1 BYTE),
		// BILL_NO VARCHAR2(20 BYTE),
		// OPT_USER VARCHAR2(20 BYTE) NOT NULL,
		// OPT_DATE DATE NOT NULL,
		// OPT_TERM VARCHAR2(20 BYTE) NOT NULL,
		// REGION_CODE VARCHAR2(20 BYTE),
		// COST_CENTER_CODE VARCHAR2(20 BYTE)
		String sql = "INSERT INTO IBS_ORDM (CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,"
				+ "MR_NO,DEPT_CODE,STATION_CODE,BED_NO,DATA_TYPE,BILL_NO,"
				+ "OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,COST_CENTER_CODE) "
				+ "VALUES ('"
				+ tparm.getValue("CASE_NO")
				+ "','"
				+ tparm.getValue("IBS_CASE_NO_SEQ")
				+ "',SYSDATE,'"
				+ tparm.getValue("IPD_NO")
				+ "','"
				+ tparm.getValue("MR_NO")
				+ "','"
				+ tparm.getValue("DEPT_CODE")
				+ "','"
				+ tparm.getValue("STATION_CODE")
				+ "','"
				+ tparm.getValue("BED_NO")
				+ "','"
				+ 3
				+ "','"
				+ tparm.getValue("BILL_NO")
				+ "','"
				+ tparm.getValue("OPT_USER")
				+ "',SYSDATE,'"
				+ tparm.getValue("OPT_TERM")
				+ "','"
				+ tparm.getValue("REGION_CODE")
				+ "','"
				+ tparm.getValue("COST_CENTER_CODE") + "') ";
		System.out.println("sqlM==========" + sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	// liuyalin add 20170324
	/*
	 * 住院配液增加配液费存于表ODI_SOLUTION
	 */
	public TParm updateSolution(TParm tparm, TConnection conn) {
		String sql = "INSERT INTO ODI_SOLUTION (CASE_NO,ORDER_NO,ORDER_SEQ,IBS_CASE_NO_SEQ,IBS_SEQ_NO,"
				+ "ORDER_DATE,ORDER_DATETIME,"
				+ "DOSAGE_QTY,"
				+ "BAR_CODE,IBS_ORDER_DESC,OWN_PRICE,"
				+ "OPT_DATE,OPT_USER,OPT_TERM,BATCH_CODE,ORDER_CODE )"
				+ "VALUES ('"
				+ tparm.getValue("CASE_NO")
				+ "','"
				+ tparm.getValue("ORDER_NO")
				+ "','"
				+ tparm.getValue("ORDER_SEQ")
				+ "','"
				+ tparm.getValue("IBS_CASE_NO_SEQ")
				+ "','"
				+ tparm.getValue("IBS_SEQ_NO")
				+ "','"
				+ tparm.getValue("ORDER_DATE")
				+ "','"
				+ tparm.getValue("ORDER_DATETIME")
				+ "','"
				+ tparm.getValue("DOSAGE_QTY")
				+ "','"
				+ tparm.getValue("BAR_CODE")
				+ "','"
				+ tparm.getValue("IBS_ORDER_DESC")
				+ "','"
				+ tparm.getValue("OWN_PRICE")
				+ "',SYSDATE,'"
				+ tparm.getValue("OPT_USER")
				+ "','"
				+ tparm.getValue("OPT_TERM")
				+ "','"
				+ tparm.getValue("BATCH_CODE")
				+ "','"
				+ tparm.getValue("ORDER_CODE") + "')";
		System.out.println("sqlS==========" + sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

}
