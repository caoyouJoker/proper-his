package jdo.iva;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class IVARefundMedicineTool extends TJDODBTool {

	/** 单例模式 */
	private static IVARefundMedicineTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static IVARefundMedicineTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVARefundMedicineTool();
		}
		return instanceObject;
	}

	// 查询有退药记录的病区
	public TParm queryInfo(TParm parm) {

		String sql = "SELECT C.STATION_DESC,A.STATION_CODE FROM ODI_DSPNM A ,ODI_DSPND B,SYS_STATION C "
				+ "WHERE "
				+ " A.DC_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') "
				+ " AND A.IVA_DEPLOY_USER IS  NULL "
				+ " AND A.IVA_FLG='Y' "
				+ " AND A.IVA_RETN_USER IS NULL"
				+ " AND A.CASE_NO=B.CASE_NO  "
				+ " AND A.ORDER_NO=B.ORDER_NO  "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ  "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM  "
				+ " AND A.STATION_CODE=C.STATION_CODE "
				+ " GROUP BY A.STATION_CODE, C.STATION_DESC ";
//		System.out.println("sql===="+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
		
	}

	// 查询已退药信息
	public TParm queryready(TParm parm) {
		String sql = "SELECT B.STATION_DESC,A.STATION_CODE FROM ODI_DSPNM A ,SYS_STATION B WHERE "
				+ " A.IVA_RETN_USER IS NOT NULL AND A.IVA_FLG='Y' AND A.DC_DATE < SYSDATE AND A.STATION_CODE=B.STATION_CODE"
				+ " GROUP BY A.STATION_CODE, B.STATION_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));

	}

	// 查询退药患者信息
	public TParm querypatient(TParm parm) {
		String sql = "SELECT 'N' AS SELECT_AVG,D.STATION_DESC,C.PAT_NAME,"
				+ " A.CASE_NO,A.MR_NO,F.BED_NO_DESC,A.IVA_RETN_USER,"
				+ " E.CHN_DESC FROM ODI_DSPNM A,ODI_DSPND B,"
				+ " SYS_PATINFO C,SYS_STATION D,SYS_DICTIONARY E,SYS_BED F WHERE "
				+ " A.DC_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') ";
		if (parm.getValue("STATION_CODE") != null
				&& !"".equals(parm.getValue("STATION_CODE"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "' ";
		}
		if (parm.getValue("MR_NO") != null
				&& !"".equals(parm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + parm.getValue("MR_NO") + "' ";
		}
		sql += " AND A.IVA_FLG ='Y'"
//				+ " AND B.IVA_DEPLOY_USER IS  NULL "
//				+ " AND B.IVA_DISPENSE_USER IS NULL "
				+ " AND B.DC_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') "
				+ " AND B.IVA_FLG = 'Y' "
				+ " AND B.IVA_DEPLOY_USER IS NULL "
				+ " AND B.IVA_DISPENSE_USER IS NULL "
				+ " AND A.CASE_NO= B.CASE_NO "
				+ " AND A.ORDER_NO =B.ORDER_NO "
				+ " AND A.ORDER_SEQ =B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
				+ " AND A.MR_NO =C.MR_NO "
				+ " AND A.STATION_CODE =D.STATION_CODE "
				+ " AND E.ID =C.SEX_CODE "
				+ " AND E.GROUP_ID ='SYS_SEX' "
				+ " AND A.BED_NO= F.BED_NO GROUP BY 'N',D.STATION_DESC,"
				+ " C.PAT_NAME ,A.CASE_NO,A.MR_NO,F.BED_NO_DESC,"
				+ " A.IVA_RETN_USER,E.CHN_DESC ";
//		System.out.println(">>>>>>>>>>>"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// 查询ODI_DSPNM退药患者
	public TParm querypdspnm(TParm parm) {
//		String sql = "SELECT A.CASE_NO,A.MR_NO,"
//				+ "A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.END_DTTM,A.REGION_CODE,A.STATION_CODE,A.DEPT_CODE,A.VS_DR_CODE,"
//				+ "A.BED_NO,A.IPD_NO,A.DSPN_KIND,A.DSPN_DATE,A.DSPN_USER,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.EXEC_DEPT_CODE,A.LINKMAIN_FLG,"
//				+ "A.ORDER_CODE,A.ORDER_DESC,A.ROUTE_CODE,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,A.OWN_AMT,"
//				+ "A.TOT_AMT,A.ATC_FLG,A.SENDATC_FLG,A.DC_TOT,A.RTN_NO,A.RTN_NO_SEQ,A.RTN_DOSAGE_QTY,A.RTN_DOSAGE_UNIT,A.CANCEL_DOSAGE_QTY,"
//				+ "A.PHA_RETN_CODE,A.PHA_RETN_DATE,A.PHA_TYPE,A.DOSE_TYPE,A.DCTAGENT_FLG,A.URGENT_FLG,A.BILL_FLG,"
//				+ "A.ANTIBIOTIC_CODE,A.FINAL_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.RETURN_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.RETURN_QTY2,"
//				+ "A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.RETURN_QTY3,A.TAKEMED_ORG "
//				+ "FROM ODI_DSPNM A,ODI_DSPND B,SYS_PATINFO C,SYS_STATION D,SYS_DICTIONARY E ,SYS_BED F "
//				+ "WHERE  ";
//		if (parm.getValue("CASE_NO") != null
//				&& !"".equals(parm.getValue("CASE_NO"))) {
//			sql += "  A.CASE_NO='" + parm.getValue("CASE_NO") + "' AND";
//		}
//		sql += "    A.IVA_FLG ='Y'"
//				+ " AND A.IVA_DEPLOY_USER IS  NULL"
//				+ " AND A.IVA_DISPENSE_USER IS NULL "
//				+ " AND A.CASE_NO= B.CASE_NO  "
//				+ " AND A.ORDER_NO =B.ORDER_NO  "
//				+ " AND A.ORDER_SEQ =B.ORDER_SEQ  "
//				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM "
//				+ " AND B.DC_DATE < SYSDATE "
//				+ " AND A.MR_NO =C.MR_NO  "
//				+ " AND A.STATION_CODE =D.STATION_CODE "
//				+ " AND E.ID =C.SEX_CODE  "
//				+ " AND E.GROUP_ID ='SYS_SEX'  "
//				+ " AND A.BED_NO= F.BED_NO "
//				+ " GROUP BY A.CASE_NO,A.MR_NO,"
//				+ "A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.END_DTTM,A.REGION_CODE,A.STATION_CODE,A.DEPT_CODE,A.VS_DR_CODE,"
//				+ "A.BED_NO,A.IPD_NO,A.DSPN_KIND,A.DSPN_DATE,A.DSPN_USER,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.EXEC_DEPT_CODE,A.LINKMAIN_FLG,"
//				+ "A.ORDER_CODE,A.ORDER_DESC,A.ROUTE_CODE,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,A.OWN_AMT,"
//				+ "A.TOT_AMT,A.ATC_FLG,A.SENDATC_FLG,A.DC_TOT,A.RTN_NO,A.RTN_NO_SEQ,A.RTN_DOSAGE_QTY,A.RTN_DOSAGE_UNIT,A.CANCEL_DOSAGE_QTY,"
//				+ "A.PHA_RETN_CODE,A.PHA_RETN_DATE,A.PHA_TYPE,A.DOSE_TYPE,A.DCTAGENT_FLG,A.URGENT_FLG,A.BILL_FLG,"
//				+ "A.ANTIBIOTIC_CODE,A.FINAL_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.RETURN_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.RETURN_QTY2,"
//				+ "A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.RETURN_QTY3,A.TAKEMED_ORG  ";
		
//		String sql = "SELECT B.CASE_NO,B.MR_NO,B.ORDER_NO,B.ORDER_SEQ,B.START_DTTM,B.END_DTTM,B.REGION_CODE,B.STATION_CODE,B.DEPT_CODE,B.VS_DR_CODE,"
//				+ "B.BED_NO,B.IPD_NO,B.DSPN_KIND,B.DSPN_DATE,B.DSPN_USER,B.ORDER_CAT1_CODE,B.CAT1_TYPE,B.EXEC_DEPT_CODE,B.LINKMAIN_FLG,"
//				+ "B.ORDER_CODE,B.ORDER_DESC,B.ROUTE_CODE,B.DISPENSE_QTY,B.DISPENSE_UNIT,B.GIVEBOX_FLG,B.OWN_PRICE,B.NHI_PRICE,B.OWN_AMT,"
//				+ "B.TOT_AMT,B.ATC_FLG,B.SENDATC_FLG,B.DC_TOT,B.RTN_NO,B.RTN_NO_SEQ,B.RTN_DOSAGE_QTY,B.RTN_DOSAGE_UNIT,B.CANCEL_DOSAGE_QTY,"
//				+ "B.PHA_RETN_CODE,B.PHA_RETN_DATE,B.PHA_TYPE,B.DOSE_TYPE,B.DCTAGENT_FLG,B.URGENT_FLG,B.BILL_FLG,"
//				+ "B.ANTIBIOTIC_CODE,B.FINAL_TYPE,B.VERIFYIN_PRICE1,B.DISPENSE_QTY1,B.RETURN_QTY1,B.VERIFYIN_PRICE2,B.DISPENSE_QTY2,B.RETURN_QTY2,"
//				+ "B.VERIFYIN_PRICE3,B.DISPENSE_QTY3,B.RETURN_QTY3,B.TAKEMED_ORG "
				
		String sql = "SELECT B.CASE_NO,B.ORDER_NO,B.ORDER_SEQ,B.START_DTTM,"
				+ " B.END_DTTM,B.ORDER_CODE,B.ORDER_DESC,B.DC_TOT,B.RTN_DOSAGE_QTY,B.RTN_DOSAGE_UNIT,"
				+ " B.TRANSMIT_RSN_CODE,B.DSPN_USER,B.DSPN_DATE,B.OPT_DATE,B.OPT_USER,B.OPT_TERM,"
				+ " B.DSPN_KIND,B.EXEC_DEPT_CODE,B.MR_NO,B.STATION_CODE,B.BED_NO,B.IPD_NO,B.DISPENSE_QTY,"
				+ " B.DISPENSE_UNIT,B.DOSE_TYPE,B.GIVEBOX_FLG,B.OWN_PRICE,B.PHA_TYPE,B.ROUTE_CODE,"
				+ " B.RTN_NO,B.RTN_NO_SEQ,B.ORDER_CAT1_CODE,B.REGION_CODE,B.DEPT_CODE,B.CAT1_TYPE,"
				+ " B.VS_DR_CODE,B.PARENT_CASE_NO,B.PARENT_ORDER_NO,B.PARENT_ORDER_SEQ,"
				+ " B.PARENT_START_DTTM,B.RT_KIND "
				
				+ " FROM ODI_ORDER A,ODI_DSPNM B,"
				+ " ODI_DSPND C,SYS_PATINFO D,SYS_STATION E,SYS_DICTIONARY F,"
				+ " SYS_BED G WHERE A.CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND A.DC_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') ";
		if (parm.getValue("STATION_CODE") != null
				&& !"".equals(parm.getValue("STATION_CODE"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "' ";
		}
		sql += " AND B.IVA_FLG ='Y'"
				+ " AND C.IVA_FLG = 'Y' "
				+ " AND C.IVA_DEPLOY_USER IS NULL "
				+ " AND C.IVA_DISPENSE_USER IS NULL "
				+ " AND A.CASE_NO= B.CASE_NO "
				+ " AND A.ORDER_NO =B.ORDER_NO "
				+ " AND A.ORDER_SEQ =B.ORDER_SEQ "
				+ " AND B.CASE_NO= C.CASE_NO "
				+ " AND B.ORDER_NO =C.ORDER_NO "
				+ " AND B.ORDER_SEQ =C.ORDER_SEQ "
				+ " AND C.ORDER_DATE || C.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM "
				+ " AND B.MR_NO =D.MR_NO "
				+ " AND B.STATION_CODE =E.STATION_CODE "
				+ " AND F.ID =D.SEX_CODE "
				+ " AND F.GROUP_ID ='SYS_SEX' "
				+ " AND B.BED_NO= G.BED_NO ";
//				+ "GROUP BY 'N',E.STATION_DESC,"
//				+ "D.PAT_NAME ,B.CASE_NO,B.MR_NO,G.BED_NO_DESC,"
//				+ "B.IVA_RETN_USER,F.CHN_DESC ";

		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// 查询ODI_DSPND退药患者
	public TParm querydspnd(TParm parm) {
		String sql = "select b.* "
				+ "from odi_dspnm a ,odi_dspnd b where a.case_no='"
				+ parm.getValue("CASE_NO")
				+ "' and A.ORDER_NO='"
				+ parm.getValue("ORDER_NO")
				+ "'"
				+ " and A.ORDER_SEQ='"
				+ parm.getValue("ORDER_SEQ")
				+ "'"
				+ "and A.IVA_FLG ='Y'"
				+ "AND A.IVA_DEPLOY_USER IS  NULL"
				+ "AND A.IVA_DISPENSE_USER IS NULL "
				+ "AND A.CASE_NO= B.CASE_NO  "
				+ "AND A.ORDER_NO =B.ORDER_NO  "
				+ "AND A.ORDER_SEQ =B.ORDER_SEQ  "
				+ "AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM "
				+ "AND B.DC_DATE < SYSDATE ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// 患者退药明细
	public TParm querydetail(TParm parm) {
		String sql = "SELECT 'Y' AS ACTION, A.LINKMAIN_FLG AS SELECT_FLG,A.CASE_NO,A.LINK_NO,"
				+ " A.ORDER_NO,A.START_DTTM,A.ORDER_DESC,A.CANCEL_DOSAGE_QTY,"
				+ " A.CANCELRSN_CODE,A.SPECIFICATION,A.RETURN_QTY1,B.ORDER_DATE,B.ORDER_DATETIME,"
				+ " E.UNIT_CHN_DESC "
//				+ " B.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,C.FREQ_CHN_DESC"
				+ " FROM ODI_DSPNM A,ODI_DSPND B,SYS_PHAFREQ C,SYS_UNIT E "
				+ " WHERE A.CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND A.IVA_FLG = 'Y' "
				+ " AND B.DC_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') "
				+ "SUBSTR( TO_CHAR(B.DC_DATE,'YYYYMMDDHH24MISS'),1,12) "
				+ "> B.ORDER_DATE || B.ORDER_DATETIME"
				+ " AND B.IVA_FLG = 'Y' "
				+ " AND B.IVA_RETN_USER IS NULL "
				+ " AND B.IVA_DEPLOY_USER IS NULL "
				+ " AND B.IVA_DISPENSE_USER IS NULL "
				+ " AND A.CASE_NO= B.CASE_NO "
				+ " AND A.ORDER_NO =B.ORDER_NO "
				+ " AND A.ORDER_SEQ =B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
				+ " A.START_DTTM AND A.END_DTTM "
				+ " AND A.FREQ_CODE=C.FREQ_CODE "
				+ " AND B.DOSAGE_UNIT=E.UNIT_CODE "
				+ "GROUP BY 'Y', A.LINKMAIN_FLG AS SELECT_FLG,A.CASE_NO,A.LINK_NO,"
				+ " A.ORDER_NO,A.START_DTTM,A.ORDER_DESC,A.CANCEL_DOSAGE_QTY,"
				+ " A.CANCELRSN_CODE,A.SPECIFICATION,A.RETURN_QTY1,B.ORDER_DATE,B.ORDER_DATETIME,"
				+ " E.UNIT_CHN_DESC "
				+ " ORDER BY A.LINK_NO,B.ORDER_DATE, B.ORDER_DATETIME, CASE WHEN A.LINKMAIN_FLG = 'Y' "
				+ " THEN '1' ELSE '2' END ";
		System.out.println("sql======"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 确认退药
	 */
	public TParm updateInfoM(TParm tparm, TConnection conn) {

		String sql = "UPDATE ODI_DSPNM  SET IVA_RETN_USER='"
				+ tparm.getValue("IVA_RETN_USER") + "',"
				+ "IVA_RETN_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	public TParm updateInfoD(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPND  SET IVA_RETN_USER='"
				+ tparm.getValue("IVA_RETN_USER") + "',"
				+ "IVA_RETN_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/*
	 * M表新增
	 */
	public TParm insertInfoM(TParm parm, TConnection conn) {
		SimpleDateFormat logFormater = new SimpleDateFormat("yyMMdd HHmmssSSS");

		SimpleDateFormat logFormaternew = new SimpleDateFormat(
				"yyyy/MM/dd/ HH:mm:ss");
//		String sql = "INSERT INTO odi_dspnm(CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,"
//				+ "END_DTTM,REGION_CODE,STATION_CODE,DEPT_CODE,VS_DR_CODE,"
//				+ "BED_NO,IPD_NO,MR_NO,DSPN_KIND,DSPN_DATE,DSPN_USER,ORDER_CAT1_CODE,"
//				+ "CAT1_TYPE,EXEC_DEPT_CODE,LINKMAIN_FLG,ORDER_CODE,ORDER_DESC,ROUTE_CODE,"
//				+ "DISPENSE_QTY,DISPENSE_UNIT,GIVEBOX_FLG,OWN_PRICE,NHI_PRICE,OWN_AMT,TOT_AMT,"
//				+ "ATC_FLG,SENDATC_FLG,DC_TOT,RTN_NO,RTN_NO_SEQ,RTN_DOSAGE_QTY,RTN_DOSAGE_UNIT,"
//				+ "CANCEL_DOSAGE_QTY,PHA_RETN_CODE,PHA_RETN_DATE,PHA_TYPE,DOSE_TYPE,DCTAGENT_FLG,URGENT_FLG,"
//				+ "BILL_FLG,ANTIBIOTIC_CODE,FINAL_TYPE,VERIFYIN_PRICE1,DISPENSE_QTY1,RETURN_QTY1,VERIFYIN_PRICE2,"
//				+ "DISPENSE_QTY2,RETURN_QTY2,VERIFYIN_PRICE3,DISPENSE_QTY3,RETURN_QTY3,TAKEMED_ORG,OPT_USER,"
//				+ "OPT_DATE,OPT_TERM" + ")VALUES(CASE_NO='"
//				+ parm.getValue("CASE_NO")
//				+ "',ORDER_NO='"
//				+ parm.getValue("ORDER_NO")
//				+ "',ORDER_SEQ='"
//				+ parm.getValue("ORDER_SEQ")
//				+ "',"
//				+ "START_DTTM='"
//				+ logFormater.format(new Date())
//				+ "',END_DTTM='"
//				+ logFormater.format(new Date())
//				+ "',REGION_CODE='"
//				+ parm.getValue("REGION_CODE")
//				+ "',"
//				+ "STATION_CODE='"
//				+ parm.getValue("STATION_CODE")
//				+ "',DEPT_CODE='"
//				+ parm.getValue("DEPT_CODE")
//				+ "',VS_DR_CODE='"
//				+ parm.getValue("VS_DR_CODE")
//				+ "',"
//				+ "BED_NO='"
//				+ parm.getValue("BED_NO")
//				+ "',IPD_NO='"
//				+ parm.getValue("IPD_NO")
//				+ "',MR_NO='"
//				+ parm.getValue("MR_NO")
//				+ "',"
//				+ "DSPN_KIND='RT'"
//				+ ",DSPN_DATE=sysdate"
//				+ ",DSPN_USER='"
//				+ Operator.getID()
//				+ "',"
//				+ "ORDER_CAT1_CODE='"
//				+ parm.getValue("ORDER_CAT1_CODE")
//				+ "',CAT1_TYPE='"
//				+ parm.getValue("CAT1_TYPE")
//				+ "',EXEC_DEPT_CODE='"
//				+ parm.getValue("EXEC_DEPT_CODE")
//				+ "',"
//				+ "LINKMAIN_FLG='"
//				+ parm.getValue("LINKMAIN_FLG")
//				+ "',ORDER_CODE='"
//				+ parm.getValue("ORDER_CODE")
//				+ "',"
//				+ "ORDER_DESC='"
//				+ parm.getValue("ORDER_DESC")
//				+ "',ROUTE_CODE='"
//				+ parm.getValue("ROUTE_CODE")
//				+ "',DISPENSE_QTY='"
//				+ parm.getValue("DISPENSE_QTY")
//				+ "',"
//				+ "DISPENSE_UNIT='"
//				+ parm.getValue("DISPENSE_UNIT")
//				+ "',GIVEBOX_FLG='"
//				+ parm.getValue("GIVEBOX_FLG")
//				+ "',OWN_PRICE='"
//				+ parm.getValue("OWN_PRICE")
//				+ "',"
//				+ "NHI_PRICE='"
//				+ parm.getValue("NHI_PRICE")
//				+ "',OWN_AMT='"
//				+ parm.getValue("OWN_AMT")
//				+ "',TOT_AMT='"
//				+ parm.getValue("TOT_AMT")
//				+ "',"
//				+ "ATC_FLG='"
//				+ parm.getValue("ATC_FLG")
//				+ "',SENDATC_FLG='"
//				+ parm.getValue("SENDATC_FLG")
//				+ "',DC_TOT='"
//				+ parm.getValue("DC_TOT")
//				+ "',"
//				+ "RTN_NO='"
//				+ parm.getValue("RTN_NO")
//				+ "',RTN_NO_SEQ='"
//				+ parm.getValue("RTN_NO_SEQ")
//				+ "',RTN_DOSAGE_QTY='"
//				+ parm.getValue("RTN_DOSAGE_QTY")
//				+ "',"
//				+ "RTN_DOSAGE_UNIT='"
//				+ parm.getValue("RTN_DOSAGE_UNIT")
//				+ "',CANCEL_DOSAGE_QTY='"
//				+ parm.getValue("CANCEL_DOSAGE_QTY")
//				+ "',PHA_RETN_CODE='"
//				+ parm.getValue("PHA_RETN_CODE")
//				+ "',"
//				+ "PHA_RETN_DATE='"
//				+ parm.getValue("PHA_RETN_DATE")
//				+ "',PHA_TYPE='"
//				+ parm.getValue("PHA_TYPE")
//				+ "',DOSE_TYPE='"
//				+ parm.getValue("DOSE_TYPE")
//				+ "',"
//				+ "DCTAGENT_FLG='"
//				+ parm.getValue("DCTAGENT_FLG")
//				+ "',URGENT_FLG='"
//				+ parm.getValue("URGENT_FLG")
//				+ "',BILL_FLG='"
//				+ parm.getValue("BILL_FLG")
//				+ "',"
//				+ "ANTIBIOTIC_CODE='"
//				+ parm.getValue("ANTIBIOTIC_CODE")
//				+ "',FINAL_TYPE='"
//				+ parm.getValue("FINAL_TYPE")
//				+ "',VERIFYIN_PRICE1='"
//				+ parm.getValue("VERIFYIN_PRICE1")
//				+ "'"
//				+ ",DISPENSE_QTY1='"
//				+ parm.getValue("DISPENSE_QTY1")
//				+ "',RETURN_QTY1='"
//				+ parm.getValue("RETURN_QTY1")
//				+ "',VERIFYIN_PRICE2='"
//				+ parm.getValue("VERIFYIN_PRICE2")
//				+ "',"
//				+ "DISPENSE_QTY2='"
//				+ parm.getValue("DISPENSE_QTY2")
//				+ "',RETURN_QTY2='"
//				+ parm.getValue("RETURN_QTY2")
//				+ "',VERIFYIN_PRICE3='"
//				+ parm.getValue("VERIFYIN_PRICE3")
//				+ "'"
//				+ ",DISPENSE_QTY3='"
//				+ parm.getValue("DISPENSE_QTY3")
//				+ "',RETURN_QTY3='"
//				+ parm.getValue("RETURN_QTY3")
//				+ "',TAKEMED_ORG='"
//				+ parm.getValue("TAKEMED_ORG")
//				+ "',"
//				+ "OPT_USER='"
//				+ parm.getValue("OPT_USER")
//				+ "',OPT_DATE='"
//				+ parm.getValue("OPT_DATE")
//				+ "',OPT_TERM='"
//				+ parm.getValue("OPT_TERM") + "')";
		String sql = "INSERT INTO ODI_DSPNM (CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,"
				+ " END_DTTM,ORDER_CODE,ORDER_DESC,DC_TOT,RTN_DOSAGE_QTY,RTN_DOSAGE_UNIT,"
				+ " TRANSMIT_RSN_CODE,DSPN_USER,DSPN_DATE,OPT_DATE,OPT_USER,OPT_TERM,"
				+ " DSPN_KIND,EXEC_DEPT_CODE,MR_NO,STATION_CODE,BED_NO,IPD_NO,DISPENSE_QTY,"
				+ " DISPENSE_UNIT,DOSE_TYPE,GIVEBOX_FLG,OWN_PRICE,PHA_TYPE,ROUTE_CODE,"
				+ " RTN_NO,RTN_NO_SEQ,ORDER_CAT1_CODE,REGION_CODE,DEPT_CODE, CAT1_TYPE,"
				+ " VS_DR_CODE,PARENT_CASE_NO,PARENT_ORDER_NO,PARENT_ORDER_SEQ,"
				+ " PARENT_START_DTTM,RT_KIND ) "
                +"VALUES('"
                + parm.getValue("CASE_NO")
                +"','"
                + parm.getValue("ORDER_NO")
                +"','"
                + parm.getValue("ORDER_SEQ")
                +"','"
                + parm.getValue("START_DTTM")
                +"','"
                + parm.getValue("END_DTTM")
                +"','"
                + parm.getValue("ORDER_CODE")
                +"','"
                + parm.getValue("ORDER_DESC")
                +"','"
                + parm.getValue("DC_TOT")
                +"','"
                + parm.getValue("RTN_DOSAGE_QTY")
                +"','"
                + parm.getValue("RTN_DOSAGE_UNIT")
                +"','"
                + parm.getValue("TRANSMIT_RSN_CODE")
                +"','"
                + parm.getValue("DSPN_USER")
                +"',SYSDATE,SYSDATE,'"
                + parm.getValue("OPT_USER")
                +"','"
                + parm.getValue("OPT_TERM")
                +"','"
                + parm.getValue("RX_KIND")
                +"','"
                + parm.getValue("EXEC_DEPT_CODE")
                +"','"
                + parm.getValue("MR_NO")
                +"','"
                + parm.getValue("STATION_CODE")
                +"','"
                + parm.getValue("BED_NO")
                +"','"
                + parm.getValue("IPD_NO")
                +"','"
                + parm.getValue("DISPENSE_QTY")
                +"','"
                + parm.getValue("DISPENSE_UNIT")
                +"','"
                + parm.getValue("DOSE_TYPE")
                +"','"
                + parm.getValue("GIVEBOX_FLG")
                +"','"
                + parm.getValue("OWN_PRICE")
                +"','"
                + parm.getValue("PHA_TYPE")
                +"','"
                + parm.getValue("ROUTE_CODE")
                +"','"
                + parm.getValue("RTN_NO")
                +"','"
                + parm.getValue("RTN_NO_SEQ")
                +"','"
                + parm.getValue("ORDER_CAT1_CODE")
                +"','"
                + parm.getValue("REGION_CODE")
                +"','"
                + parm.getValue("DEPT_CODE")
                +"','"
                + parm.getValue("CAT1_TYPE")
                +"','"
                + parm.getValue("VS_DR_CODE")
                +"','"
                + parm.getValue("PARENT_CASE_NO")
                +"','"
                + parm.getValue("PARENT_ORDER_NO")
                +"','"
                + parm.getValue("PARENT_ORDER_SEQ")
                +"','"
                + parm.getValue("PARENT_START_DTTM")
                +"','"
                + parm.getValue("RT_KIND")
                +"')";
		System.out.println("insterSQL===="+sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	// 插入明细ODI_DSPMD
	public TParm OninsertDspnd(TParm parm) {
		String sql = "INSERT INTO ODI_DSPND(CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
				+ "NURSE_DISPENSE_FLG,ORDER_CODE,DOSAGE_QTY,DOSAGE_UNIT,EXEC_DEPT_CODE,"
				+ "BILL_FLG,OPT_USER,OPT_DATE,OPT_TERM,TAKEMED_ORG)VALUE('"
				+ parm.getValue("CASE_NO")
				+ "','"
				+ parm.getValue("ORDER_NO")
				+ "','"
				+ parm.getValue("ORDER_SEQ")
				+ "','"
				+ parm.getValue("ORDER_DATE")
				+ "','"
				+ parm.getValue("ORDER_DATETIME")
				+ "','"
				+ parm.getValue("NURSE_DISPENSE_FLG")
				+ "','"
				+ parm.getValue("ORDER_CODE")
				+ "','"
				+ parm.getValue("DOSAGE_QTY")
				+ "','"
				+ parm.getValue("DOSAGE_UNIT")
				+ "','"
				+ parm.getValue("EXEC_DEPT_CODE")
				+ "','"
				+ parm.getValue("BILL_FLG")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',sysdate,'"
				+ parm.getValue("OPT_TERM")
				+ "','"
				+ parm.getValue("TAKEMED_ORG")
				+ "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

}
