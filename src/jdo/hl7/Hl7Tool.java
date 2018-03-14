package jdo.hl7;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jdo.sys.SystemTool;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Miracle
 * @version 1.0
 */
public class Hl7Tool extends TJDODBTool {
	/**
	 * 实例
	 */
	public static Hl7Tool instanceObject;

	/**
	 * 构造器
	 */
	public Hl7Tool() {
	}

	/**
	 * 得到实例
	 * 
	 * @return IBSTool
	 */
	public static Hl7Tool getInstance() {
		if (instanceObject == null)
			instanceObject = new Hl7Tool();
		return instanceObject;
	}
	/**
	 * 得到当前医院的名称
	 * SYS_REGION表
	 * REGION_CODE字段
	 * 条件是MAIN_FLG='Y'
	 * 
	 * @return TParm
	 */
	public TParm getRegionCode() {
		TParm result = new TParm(
				this.select("SELECT REGION_CODE FROM SYS_REGION WHERE MAIN_FLG='Y'"));
		return result;
	}
	/**
	 * 医嘱医嘱
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getodiOrderData(TParm parm) {
		String sql = "SELECT A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC,B.RX_KIND,B.LINK_NO,A.DOSAGE_UNIT,A.DOSAGE_QTY,A.DOSE_TYPE, CASE A.DOSE_TYPE"
				+ " WHEN NULL "
				+ "    THEN '' "
				+ " ELSE (SELECT c.chn_desc FROM SYS_DICTIONARY C WHERE   A.DOSE_TYPE = c.ID "
				+ "   AND C.GROUP_ID = 'SYS_DOSETYPE') "
				+ " END AS dose_typedesc,"
				+ " A.ORDER_DATE,A.NS_EXEC_DATE,A.DC_DATE,B.MED_APPLY_NO ,"
				+ " A.CAT1_TYPE,A.FREQ_CODE,A.ROUTE_CODE,B.ACUMDSPN_QTY, A.MEDI_QTY, A.MEDI_UNIT,A.PUMP_CODE,A.INFLUTION_RATE "
				+ " FROM ODI_DSPNM A,ODI_ORDER B"
				+ " WHERE A.CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "'"
				+ " AND A.ORDER_NO='"
				+ parm.getValue("ORDER_NO")
				+ "'"
				+ " AND A.ORDER_SEQ='"
				+ parm.getValue("ORDER_SEQ")
				+ "'"
				+ " AND A.CASE_NO=B.CASE_NO"
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ ";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * NIS医嘱
	 * zhanglei 20170419
	 * @param parm
	 * @return
	 */
	public TParm getodiOrderDataNIS(TParm parm) {
		String sql = "SELECT A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC,B.RX_KIND,B.LINK_NO,A.DOSAGE_UNIT,A.DOSAGE_QTY,A.DOSE_TYPE, "
				+ " A.ORDER_DATE,A.NS_EXEC_DATE,A.DC_DATE,B.MED_APPLY_NO ,"
				+ " A.CAT1_TYPE,A.FREQ_CODE,A.ROUTE_CODE,B.ACUMDSPN_QTY, A.MEDI_QTY, A.MEDI_UNIT,A.PUMP_CODE,A.INFLUTION_RATE "
				+ " FROM ODI_DSPNM A,ODI_ORDER B"
				+ " WHERE A.CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "'"
				+ " AND A.ORDER_NO='"
				+ parm.getValue("ORDER_NO")
				+ "'"
				+ " AND A.ORDER_SEQ='"
				+ parm.getValue("ORDER_SEQ")
				+ "'"
				+ " AND A.CASE_NO=B.CASE_NO"
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ ";
		
		//System.out.println("SQLNIS:::::" + sql);
		
		TParm result = new TParm(this.select(sql));
		return result;
	}

	/**
	 * 门急诊
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getopdorderParm(TParm parm) {
		TParm result = new TParm(
				this.select("SELECT * FROM OPD_ORDER WHERE ADM_TYPE='"
						+ parm.getValue("ADM_TYPE") + "' AND CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " AND RX_NO='"
						+ parm.getValue("RX_NO") + "' AND SEQ_NO='"
						+ parm.getValue("SEQ_NO") + "'"));
		return result;
	}

	/**
	 * 住院
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getodiorderParm(TParm parm) {
		TParm result = new TParm(
				this.select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " AND ORDER_NO='"
						+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
						+ parm.getValue("ORDER_SEQ") + "'"));
		return result;
	}

	/**
	 * 健检
	 * 
	 * @param parm
	 * @return
	 */
	public TParm gethrmorderParm(TParm parm) {
		TParm result = new TParm(
				this.select("SELECT * FROM HRM_ORDER WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " AND SEQ_NO="
						+ parm.getValue("SEQ_NO")));
		return result;
	}

	/**
	 * 得到MED数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMedData(TParm parm,Map map) {
		TParm result = new TParm();
		Iterator it=map.values().iterator();
		StringBuffer orderNoSeq=new StringBuffer();
		while(it.hasNext()){
			String orderline=(String)it.next();
			if(orderNoSeq.toString().length()>0)
				orderNoSeq.append(",");
			orderNoSeq.append("'"+orderline+"'");
		}
		String line="";
		if(map.size()>0){
			line=" AND A.ORDER_NO||A.SEQ_NO IN("+orderNoSeq.toString()+")";
		}
		String sql="";
		if (parm.getValue("FLG").equals("0")) {
			if(parm.getValue("ADM_TYPE").equals("O")||parm.getValue("ADM_TYPE").equals("E")){
			sql="SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND A.CAT1_TYPE='"
				+ parm.getValue("CAT1_TYPE")
				+ "' AND A.APPLICATION_NO='"
				+ parm.getValue("LAB_NO")
				+ "' AND A.ADM_TYPE='"
				+ parm.getValue("ADM_TYPE") + "' AND A.BILL_FLG='Y'"+line;
			result = new TParm(
					this.select(sql));
			}else{
				sql="SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND A.CAT1_TYPE='"
					+ parm.getValue("CAT1_TYPE")
					+ "' AND A.APPLICATION_NO='"
					+ parm.getValue("LAB_NO")
					+ "' AND A.ADM_TYPE='"
					+ parm.getValue("ADM_TYPE")+ "' " +line;
				result = new TParm(
						this.select(sql));
			}
		} else {
			sql="SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND A.CAT1_TYPE='"
				+ parm.getValue("CAT1_TYPE")
				+ "' AND A.APPLICATION_NO='"
				+ parm.getValue("LAB_NO")
				+ "' AND ADM_TYPE='"
				+ parm.getValue("ADM_TYPE") + "'"+ line;
			result = new TParm(
					this.select(sql));
		}
//		System.out.println("=========================="+sql);
		return result;
	}

	/**
	 * 得到标本送检数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getLisSendData(TParm parm) {
		TParm result = new TParm();
		String sql = "";
		if ("H".equals(parm.getValue("ADM_TYPE"))) {
			sql = "SELECT A.CASE_NO,A.BLOOD_USER AS NS_EXEC_CODE_REAL,A.BLOOD_DATE AS NS_EXEC_DATE_REAL,A.LIS_RE_USER,A.LIS_RE_DATE,A.APPLICATION_NO,A.ORDER_CAT1_CODE "
				+ " FROM MED_APPLY A, HRM_ORDER B "
				+ " WHERE A.CASE_NO = B.CASE_NO AND A.SEQ_NO = B.SEQ_NO "
				+ " AND A.APPLICATION_NO = B.MED_APPLY_NO AND A.CAT1_TYPE = B.CAT1_TYPE ";
			
			if (!parm.getValue("CASE_NO").equals("")) {
				sql += " AND A.CASE_NO='" + parm.getValue("CASE_NO") + "'";
			}
			if (!parm.getValue("LAB_NO").equals("")) {
				sql += " AND A.APPLICATION_NO='" + parm.getValue("LAB_NO")
						+ "'";
			}
		} else {
			sql = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,B.NS_EXEC_CODE_REAL,B.NS_EXEC_DATE_REAL,A.LIS_RE_USER,A.LIS_RE_DATE,C.APPLICATION_NO,A.ORDER_CAT1_CODE FROM ODI_DSPNM A,ODI_DSPND B,MED_APPLY C"
					+ " WHERE A.CASE_NO=B.CASE_NO "
					+ " AND A.ORDER_NO=B.ORDER_NO "
					+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
					+ " AND B.ORDER_DATE||B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
					+ " AND A.CASE_NO=C.CASE_NO "
					+ " AND A.CAT1_TYPE=C.CAT1_TYPE "
					+ " AND A.ORDER_NO=C.ORDER_NO "
					+ " AND A.ORDER_SEQ=C.SEQ_NO";

			if (!parm.getValue("CASE_NO").equals("")) {
				sql += " AND A.CASE_NO='" + parm.getValue("CASE_NO") + "'";
			}
			if (!parm.getValue("ORDER_NO").equals("")) {
				sql += " AND A.ORDER_NO='" + parm.getValue("ORDER_NO") + "'";
			}
			if (!parm.getValue("LAB_NO").equals("")) {
				sql += " AND C.APPLICATION_NO='" + parm.getValue("LAB_NO")
						+ "'";
			}
		}
		
		result = new TParm(this.select(sql));
		return result;
	}

	/**
	 * 得到病患基本信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getPatInfo(TParm parm) {
		// System.out.println("SQL=="+"SELECT * FROM SYS_PATINFO WHERE MR_NO='"+parm.getValue("MR_NO")+"'");
		TParm result = new TParm(
				this.select("SELECT * FROM SYS_PATINFO WHERE MR_NO='"
						+ parm.getValue("MR_NO") + "'"));
		return result;
	}

	/**
	 * 得到HL7信息门诊参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getODOParm(TParm parm) {
		TParm result = new TParm(
				this.select("SELECT * FROM REG_PATADM A,SYS_CTZ B WHERE CASE_NO = '"
						+ parm.getValue("CASE_NO")
						+ "' AND A.CTZ1_CODE=B.CTZ_CODE"));
		return result;
	}

	/**
	 * 得到HL7信息住院参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getODIParm(TParm parm) {
		/*TParm result = new TParm(
				this.select("SELECT * FROM ADM_INP A,SYS_CTZ B WHERE CASE_NO='"
						+ parm.getValue("CASE_NO")
						+ "' AND A.CTZ1_CODE=B.CTZ_CODE"));*/
		//liuf
		TParm result = new TParm(
				this.select(
						" SELECT * FROM ADM_INP A,SYS_CTZ B,  SYS_BED C " +
						" WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' "+
						" AND A.CTZ1_CODE=B.CTZ_CODE" +
					    " AND A.BED_NO   = C.BED_NO"));
		return result;
	}

	/**
	 * 得到HL7健康检查参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getHRMParm(TParm parm) {
		TParm result = new TParm(
				this.select("SELECT * FROM HRM_PATADM WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'"));
		return result;
	}

	/**
	 * 返回医令细分类
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getSysOrderCat1(String orderCat1Code) {
		TParm result = new TParm(
				this.select("SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"
						+ orderCat1Code + "'"));
		if (result.getCount() <= 0) {
			return result;
		}
		return result.getRow(0);
	}

	/**
	 * 返回报告类别
	 * 
	 * @param code
	 *            String
	 * @return TParm
	 */
	public TParm getEXMRule(String type, String code) {
		TParm result = new TParm(
				this.select("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE = '"
						+ type + "' AND CATEGORY_CODE = '" + code + "'"));
		if (result.getCount() <= 0) {
			return result;
		}
		return result.getRow(0);
	}

	/**
	 * 拿到诊断名称
	 * 
	 * @param code
	 *            String
	 * @return TParm
	 */
	public TParm getICDData(String code) {
		TParm result = new TParm(
				this.select("SELECT * FROM SYS_DIAGNOSIS WHERE ICD_CODE='"
						+ code + "'"));
		if (result.getCount() <= 0) {
			return result;
		}
		return result.getRow(0);
	}
	/**
	 * 拿到诊断名称
	 * 
	 * @param code
	 *            String
	 * @return TParm
	 */
	public String getDrName(String code) {
		TParm result = new TParm(
				this.select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"
						+ code + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("USER_NAME",0);
	}
	/**
	 * 门急诊诊断
	 * 
	 * @param caseNo
	 *            String
	 * @param icdType
	 *            String
	 * @param mainFlg
	 *            String
	 * @return TParm
	 */
	public TParm opdDiagrec(String caseNo, String admType, String mainFlg) {
		TParm result = new TParm(
				this.select("SELECT * FROM OPD_DIAGREC WHERE CASE_NO='"
						+ caseNo + "' AND ADM_TYPE='" + admType
						+ "' AND MAIN_DIAG_FLG='" + mainFlg + "'"));
		return result;
	}

	/**
	 * 住院诊断
	 * 
	 * @param caseNo
	 *            String
	 * @param icdType
	 *            String
	 * @param mainFlg
	 *            String
	 * @return TParm
	 */
	public TParm odiDiagrec(String caseNo, String ioType, String mainFlg) {
		String str = "";
		//20120918 取adm_inp 中最新的诊断码 shibl
		TParm result = new TParm(
				this.select("SELECT MAINDIAG AS ICD_CODE FROM ADM_INP WHERE CASE_NO='"
						+ caseNo + "'"));
		return result;
	}

	/**
	 * 拿到用户名
	 * 
	 * @param userID
	 *            String
	 * @return String
	 */
	public String getOperatorName(String userID) {
		TParm parm = new TParm(
				this.select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"
						+ userID + "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * 更新状态
	 * 
	 * @param caseNo
	 *            String
	 * @param labNo
	 *            String
	 * @return boolean
	 */
	public boolean updateMedApply(String caseNo, String labNo, String flg,
			String admType,Map map) {
		String status = "1";
		if ("1".equals(flg))
			status = "9";
		Iterator it=map.values().iterator();
		StringBuffer orderNoSeq=new StringBuffer();
		while(it.hasNext()){
			String orderline=(String)it.next();
			if(orderNoSeq.toString().length()>0)
				orderNoSeq.append(",");
			orderNoSeq.append("'"+orderline+"'");
		}
		TParm result = new TParm();
		if (admType.equals("O") || admType.equals("E")) {
			if ("1".equals(flg))
				result = new TParm(//==pangben 2013-7-23 修改sql,门诊检验检查退费出现报错
						this.update("UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo
								+ "' AND BILL_FLG='N'"+" AND ORDER_NO||SEQ_NO IN("+orderNoSeq.toString()+")"));
			else
				result = new TParm(
						this.update("UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo + "' AND ORDER_NO||SEQ_NO IN("+orderNoSeq.toString()+")"));

		} else {
			if ("1".equals(flg))
				result = new TParm(
						this.update("UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo
								+ "' AND ORDER_NO||SEQ_NO IN("+orderNoSeq.toString()+")"));
			else
				result = new TParm(
						this.update("UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo + "' AND ORDER_NO||SEQ_NO IN("+orderNoSeq.toString()+")"));//shibl
		}
		if (result.getErrCode() < 0) {
			System.out.println("updateMedApply(" + caseNo + "," + labNo
					+ ") ERR:" + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 拿到集合医嘱细项
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSet(String orderCode) {
		TParm result = new TParm(
				this.select("SELECT B.OWN_PRICE,A.DOSAGE_QTY,A.ORDER_CODE,B.ORDER_DESC FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDERSET_CODE='"
						+ orderCode + "'"));
		return result;
	}

	/**
	 * 手术数据
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm getOpeCisData(String opbookSeq) {
		String sql = " SELECT E.OP_RECORD_NO,A.OPBOOK_SEQ AS OPBOOK_NO,A.OP_CODE1,B.OPT_CHN_DESC,A.ANA_CODE,D.CHN_DESC AS ANA_DESC,A.ANA_USER1,C1.USER_NAME AS ANA_USER1DESC,A.ANA_USER2,"
				+ "  C2.USER_NAME AS ANA_USER2DESC,A.CIRCULE_USER1,C3.USER_NAME AS CIRCULE_USER1DESC,A.CIRCULE_USER2,C4.USER_NAME AS CIRCULE_USER2DESC,A.CIRCULE_USER3,C5.USER_NAME "
				+ " AS CIRCULE_USER3DESC,A.CIRCULE_USER4,C6.USER_NAME AS CIRCULE_USER4DESC,A.SCRUB_USER1,C7.USER_NAME AS SCRUB_USER1DESC,A.SCRUB_USER2,C8.USER_NAME "
				+ " AS SCRUB_USER2DESC,A.SCRUB_USER3,C9.USER_NAME AS SCRUB_USER3DESC,A.SCRUB_USER4,C10.USER_NAME AS SCRUB_USER4DESC,A.TIME_NEED AS OPE_TIME"
				+ " FROM OPE_OPBOOK A,SYS_OPERATIONICD B,SYS_OPERATOR C1,SYS_OPERATOR C2,SYS_OPERATOR C3,SYS_OPERATOR C4,SYS_OPERATOR C5,SYS_OPERATOR C6,"
				+ " SYS_OPERATOR C7,SYS_OPERATOR C8,SYS_OPERATOR C9,SYS_OPERATOR C10,SYS_DICTIONARY D,OPE_OPDETAIL E "
				+ " WHERE A.OPBOOK_SEQ='"
				+ opbookSeq
				+ "'"
				+ " AND A.OP_CODE1 = B.OPERATION_ICD"
				+ " AND A.ANA_USER1=C1.USER_ID(+) "
				+ " AND A.ANA_USER2=C2.USER_ID(+)"
				+ " AND A.CIRCULE_USER1=C3.USER_ID(+)"
				+ " AND A.CIRCULE_USER2=C4.USER_ID(+)"
				+ " AND A.CIRCULE_USER3=C5.USER_ID(+)"
				+ " AND A.CIRCULE_USER4=C6.USER_ID(+) "
				+ " AND A.SCRUB_USER1=C7.USER_ID(+) "
				+ " AND A.SCRUB_USER2=C8.USER_ID(+) "
				+ " AND A.SCRUB_USER3=C9.USER_ID(+) "
				+ " AND A.SCRUB_USER4=C10.USER_ID(+) "
				+ " AND A.ANA_CODE=D.ID(+) "
				+ " AND D.GROUP_ID='OPE_ANAMETHOD' "
				+ " AND A.OPBOOK_SEQ=E.OPBOOK_NO(+) ";
//		System.out.println("sql------------------------" + sql);
		TParm result = new TParm(this.select(sql));
		return result;
	}

	/**
	 * 手术名称
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm getOpeData(String caseNo) {
		TParm result = new TParm(
				this.select("SELECT B.OPERATION_ICD,B.OPT_CHN_DESC FROM OPE_OPDETAIL A,SYS_OPERATIONICD B WHERE A.OP_RECORD_NO IN (SELECT MAX(OP_RECORD_NO) FROM OPE_OPDETAIL WHERE CASE_NO = '"
						+ caseNo + "') AND A.OP_CODE1 = B.OPERATION_ICD"));
		return result;
	}
	/**
	 * 手术名称
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getOpeDESC(String opeIcd) {
		TParm result = new TParm(
				this.select("SELECT B.OPT_CHN_DESC FROM SYS_OPERATIONICD B WHERE B.OPERATION_ICD='"+opeIcd+"'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("OPT_CHN_DESC",0);
	}
	/**
	 * 拿到科室名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm result = new TParm(
				this.select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * 拿到床号名称	//yanmm
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getBedDesc(String bedNo) {
		TParm result = new TParm(
				this.select("SELECT BED_NO_DESC FROM SYS_BED WHERE BED_NO='"
						+ bedNo + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("BED_NO_DESC", 0);
	}
	
	/**
	 * 拿到诊间名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getClinicRoomDesc(String clinicCode) {
		TParm result = new TParm(
				this.select("SELECT CLINICROOM_DESC FROM REG_CLINICROOM WHERE CLINICROOM_NO='"
						+ clinicCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("CLINICROOM_DESC", 0);
	}

	/**
	 * 拿到病房名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getRoomDesc(String roomCode) {
		TParm result = new TParm(
				this.select("SELECT ROOM_DESC FROM SYS_ROOM WHERE ROOM_CODE='"
						+ roomCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("ROOM_DESC", 0);
	}

	/**
	 * 拿到病房名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm result = new TParm(
				this.select("SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("STATION_DESC", 0);
	}

	/**
	 * 得到过敏史数据
	 * 
	 * @param mrNo
	 *            String
	 * @return TParm
	 */
	public TParm getDrugAllErgy(String mrNo) {
		TParm result = new TParm(
				this.getDBTool()
						.select(" SELECT ROWNUM AS ID,ADM_DATE,DRUG_TYPE,DRUGORINGRD_CODE,ALLERGY_NOTE,DEPT_CODE,DR_CODE,ADM_TYPE,CASE_NO,MR_NO,OPT_USER,OPT_DATE,OPT_TERM FROM OPD_DRUGALLERGY WHERE MR_NO='"
								+ mrNo + "' ORDER BY ADM_DATE "));
		return result;
	}

	/**
	 * 拿到名称
	 * 
	 * @param type
	 *            String
	 * @param code
	 *            String
	 * @return String
	 */
	public String getDrugTypeName(String type, String code) {
		String name = "";
		if ("A".equals(type)) {
			TParm p = new TParm(
					this.select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT' AND ID='"
							+ code + "' ORDER BY ID"));
			name = p.getValue("CHN_DESC", 0);
		}
		if ("B".equals(type)) {
			TParm p = new TParm(
					this.select("SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE='"
							+ code + "'"));
			name = p.getValue("ORDER_DESC", 0);
		}
		if ("C".equals(type)) {
			TParm p = new TParm(
					this.select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE' AND ID='"
							+ code + "' ORDER BY ID"));
			name = p.getValue("CHN_DESC", 0);
		}
		return name;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * 查询体温单信息
	 * 
	 * @param caseNo 就诊号
	 * @return result 体温单信息
	 */
	public TParm getSumVitalsignInfo(String caseNo) {
		String sql = "SELECT * FROM SUM_VITALSIGN WHERE CASE_NO = '" + caseNo + "' ORDER BY EXAMINE_DATE DESC";
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 查询收费字典数据
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm getSysFeeData(TParm parm) {
		String sql = "SELECT A.*,B.CHN_DESC AS OPTITEM_CHN_DESC FROM SYS_FEE A,SYS_DICTIONARY B "
				+ " WHERE A.OPTITEM_CODE = B.ID AND GROUP_ID='SYS_OPTITEM' ";
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sql = sql + " AND ORDER_CODE = '" + parm.getValue("ORDER_CODE") + "' ";
		}
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
}
