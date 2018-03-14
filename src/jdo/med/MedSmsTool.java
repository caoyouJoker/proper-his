package jdo.med;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.DateUtil;

public class MedSmsTool extends TJDOTool {

	public MedSmsTool() {
		setModuleName("med\\MEDSmsModule.x");
		onInit();
	}

	String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 实例
	 */
	public static MedSmsTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static MedSmsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MedSmsTool();
		return instanceObject;
	}

	/**
	 * 新增短信1
	 * 
	 * @param parm
	 * @param con
	 * @return
	 */
	public TParm insertMedSms(TParm parm, TConnection con) {
		parm.setData("SMS_CODE", getSmsCode());
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 新增短信2
	 * 
	 * @param parm
	 * @param con
	 * @return
	 */
	public TParm insertMedSms(TParm parm) {
		parm.setData("SMS_CODE", getSmsCode());
		TParm result = this.update("insertdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public static String getSmsCode() {
		return SystemTool.getInstance().getNo("ALL", "PUB", "SMS_CODE",
				"SMS_CODE");
	}

	public TParm onQuery(TParm parm) {
		return new TParm(TJDODBTool.getInstance().select(selectSql(parm)));
	}

	/**
	 * 定时查询发送时间大于等于30分钟的短信
	 * 
	 * @param con
	 * @return
	 */
	public TParm getMinute() {
		/**
		 * TParm parm = new TParm(); parm.setData("CURRENT_DATE",
		 * DateUtil.getNowTime(TIME_FORMAT)); TParm result =
		 * this.query("selectminute", parm); if (result.getErrCode() < 0) {
		 * err("ERR:" + result.getErrCode() + result.getErrText() +
		 * result.getErrName()); return result; } return result;
		 */

		String date = DateUtil.getNowTime(TIME_FORMAT);
		String sql = " SELECT PATIENT_NAME, MR_NO,SMS_CONTENT,CASE_NO, (TO_CHAR(SEND_TIME,'yyyy-MM-dd HH24:MI:SS')) as SEND_TIME, "
				+ " ADM_TYPE,DEPT_CODE,SMS_CODE, "
				+ " NOTIFY_DIRECTOR_DR_TIME,NOTIFY_COMPETENT_TIME,NOTIFY_DEAN_TIME " // add
																						// by
																						// guoy
																						// 20151020
				+ " FROM MED_SMS  "
				+ " WHERE STATE !='9' AND (( TO_DATE('"
				+ date + "','yyyy-MM-dd HH24:MI:SS')-SEND_TIME)*24*60 >= 1 ) ";
		return new TParm(TJDODBTool.getInstance().select(sql));

	}

	/**
	 * 构造新增MED_SMS表SQL语句
	 * 
	 * @param parm
	 * @return
	 */
	public static String createSql(TParm parm) {
		String sql = "INSERT INTO MED_SMS"
				+ " ( SMS_CODE,PATIENT_NAME,CASE_NO,MR_NO,STATION_CODE,"
				+ "  BED_NO,IPD_NO,DEPT_CODE,BILLING_DOCTORS,APPLICATION_NO, "
				+ "  TESTITEM_CODE,TESTITEM_CHN_DESC,TEST_VALUE,CRTCLLWLMT,STATE,"
				+ "  SEND_TIME,SMS_CONTENT,OPT_USER,OPT_DATE,OPT_TERM,"
				+ "  ADM_TYPE ) " + " VALUES " + "('"
				+ getSmsCode()
				+ "', '"
				+ parm.getValue("PATIENT_NAME")
				+ "', '"
				+ parm.getValue("CASE_NO")
				+ "', '"
				+ parm.getValue("MR_NO")
				+ "', '"
				+ parm.getValue("STATION_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("BED_NO")
				+ "', '"
				+ parm.getValue("IPD_NO")
				+ "', '"
				+ parm.getValue("DEPT_CODE")
				+ "', '"
				+ parm.getValue("BILLING_DOCTORS")
				+ "', '"
				+ parm.getValue("APPLICATION_NO")
				+ "',"
				+ " '"
				+ parm.getValue("TESTITEM_CODE")
				+ "', '"
				+ parm.getValue("TESTITEM_CHN_DESC")
				+ "', '"
				+ parm.getValue("TEST_VALUE")
				+ "', '"
				+ parm.getValue("CRTCLLWLMT")
				+ "', '9', "
				+ " SYSDATE,'"
				+ parm.getValue("SMS_CONTENT")
				+ "', '"
				+ parm.getValue("OPT_USER")
				+ "', SYSDATE,'"
				+ parm.getValue("OPT_TERM")
				+ "',"
				+ " '"
				+ parm.getValue("ADM_TYPE") + "'  )";
		// System.out.println("SQL:"+sql);
		return sql;
	}

	public TParm getRealdrCode(String admType, String caseNo) {
		String sql = " SELECT s.TEL1,r.REALDR_CODE,r.DEPT_CODE,r.MR_NO "
				+ " FROM REG_PATADM r,SYS_OPERATOR s "
				+ " WHERE r.REALDR_CODE=s.USER_ID AND r.ADM_TYPE='" + admType
				+ "' AND r.CASE_NO='" + caseNo + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 根据申请号得到对应的case_no,adm_type
	 * 
	 * @param applicationNo
	 * @return
	 */
	public TParm getMedApply(String applicationNo) {
		// modify by wangbin 2017/08/24 增加病案号和开单科室，并优化查询速度
		// modify by wangbin 20140723 增加查询申请医师字段
		String sql = " SELECT m.CASE_NO,m.ADM_TYPE,m.IPD_NO,STATION_CODE,BED_NO,m.ORDER_DR_CODE,MR_NO,DEPT_CODE "
				+ " FROM MED_APPLY m "
				+ " WHERE m.APPLICATION_NO='"
				+ applicationNo + "' AND CAT1_TYPE IN ('LIS','RIS')";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm getVsDrCode(String caseNo) {
		String sql = " SELECT s.TEL1,a.VS_DR_CODE ,a.DEPT_CODE,a.MR_NO, a.STATION_CODE "
				+ " FROM ADM_INP a,SYS_OPERATOR s "
				+ " WHERE a.VS_DR_CODE =s.USER_ID AND  a.CASE_NO='"
				+ caseNo
				+ "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// 科主任
	public TParm getVsDirectorDrCode(String caseNo) {
		String sql = " SELECT s.TEL1,a.DIRECTOR_DR_CODE ,a.DEPT_CODE,a.MR_NO, a.STATION_CODE "
				+ " FROM ADM_INP a,SYS_OPERATOR s "
				+ " WHERE a.DIRECTOR_DR_CODE =s.USER_ID(+) AND  a.CASE_NO='"
				+ caseNo + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// 护士联系方式
	public TParm getVsNurseCode(String caseNo) {
		String sql = "SELECT s.TEL1,s.USER_ID ,d.DEPT_CODE,a.MR_NO, o.STATION_CLINIC_CODE"
				+ " FROM ADM_INP a,SYS_OPERATOR s ,SYS_OPERATOR_DEPT d ,SYS_OPERATOR_STATION o "
				+ "WHERE s.ROLE_ID = 'ZYHSZ' AND s.USER_ID =d.USER_ID"
				+ " AND s.USER_ID =o.USER_ID AND a.DEPT_CODE =d.DEPT_CODE "
				+ "AND a.STATION_CODE = o.STATION_CLINIC_CODE AND  a.CASE_NO='"
				+ caseNo + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 取住院主任电话号码
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getDirectorTel(String caseNo) {

		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = this.query("selectdirectortel", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 得到门诊主任电话号码或医务科主管或者主管院长的电话号码
	 * 
	 * @param deptCode
	 * @param competentType
	 * @return
	 */
	public TParm getDeanOrCompementTel(String deptCode, String competentType) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", deptCode);
		parm.setData("COMPETENT_TYPE", competentType);
		TParm result = this.query("selectdeanorcompementtel", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据科室得到值班电话
	 * 
	 * @param deptCode
	 * @param competentType
	 * @return
	 */
	public TParm getDutyTel(TParm parm) {

		String deptCode = parm.getValue("DEPT_CODE");
		if (deptCode == null || deptCode.equals("")) {
			return new TParm();
		}
		String sql = " SELECT A.DUTY_TEL,A.DEPT_CHN_DESC FROM SYS_DEPT A WHERE A.DEPT_CODE='"
				+ deptCode + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新短信表状态为9 代表处理
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateMedSms(TParm parm) {
		parm.setData("STATE", "9");
		parm.setData("HANDLE_USER", Operator.getID() + "");
		TParm result = this.update("updatedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm updateMedSmsTime(String smsCode, String filed) {

		/**
		 * TParm parm = new TParm(); parm.setData("SMS_CODE",smsCode);
		 * parm.setData("SMS_TIME",filed); TParm result =
		 * this.update("updatedatatime", parm); if (result.getErrCode() < 0) {
		 * err("ERR:" + result.getErrCode() + result.getErrText() +
		 * result.getErrName()); return result; } return result;
		 */

		String sql = " UPDATE MED_SMS SET " + filed + "=" + "SYSDATE "
				+ " WHERE SMS_CODE='" + smsCode + "' ";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	public String selectSql(TParm parm) {
		String sql = "SELECT ADM_TYPE,MR_NO,PATIENT_NAME,TESTITEM_CODE,TEST_VALUE,CRTCLLWLMT, "
				+ "STATE,BILLING_DOCTORS,NOTIFY_DOCTORS_TIME,DIRECTOR_DR_CODE,NOTIFY_COMPETENT_TIME, "
				+ "NOTIFY_DIRECTOR_DR_TIME,NOTIFY_DEAN_TIME,HANDLE_USER,HANDLE_TIME,SMS_CODE, "
				+ "HANDLE_OPINION,SEND_TIME,SMS_CONTENT,STATION_CODE, "
				+ "CASE_NO,OPT_USER,OPT_DATE,OPT_TERM,DEAN_CODE,  "
				+ "COMPETENT_CODE,TESTITEM_CHN_DESC,DEPT_CODE "
				// add by wukai on 20170413 === 查询增加上报科室和上报人员
				+ ", REPORT_USER, REPORT_DEPT_CODE "
				+ "FROM MED_SMS  "
				+ "WHERE 1=1 ";

		// add by wukai 增加上报科室查询条件
		String reportDept = parm.getValue("REPORT_DEPT_CODE");
		if (reportDept != null && !reportDept.equals("")) {
			sql += " AND REPORT_DEPT_CODE='" + reportDept + "' ";
		}
		// add by wangqing 20180104 增加上报人查询条件
		String reportUser = parm.getValue("REPORT_USER");
		if (reportUser != null && !reportUser.equals("")) {
			sql += " AND REPORT_USER='" + reportUser + "' ";
		} 

		String deptCode = parm.getValue("DEPT_CODE");
		if (deptCode != null && !deptCode.equals("")) {
			sql += " AND DEPT_CODE='" + parm.getValue("DEPT_CODE") + "' ";
		}

		String stationCODE = parm.getValue("STATION_CODE");
		if (stationCODE != null && !stationCODE.equals("")) {
			sql += " AND STATION_CODE='" + stationCODE + "' ";
		}

		String smsState = parm.getValue("SMS_STATE");
		if (smsState != null && !smsState.equals("")) {
			if (smsState.equals("0")) {
				sql += " AND STATE != '9' ";
			} else {
				sql += " AND STATE='" + smsState + "' ";
			}
		}

		String mrNo = parm.getValue("MR_NO");
		if (mrNo != null && !mrNo.equals("")) {
			sql += " AND MR_NO='" + mrNo + "' ";
		}

		String begionTime = parm.getValue("BEGIN_TIME");
		if (begionTime != null && !begionTime.equals("")) {
			begionTime = begionTime.substring(0, begionTime.length() - 2);
			sql += " AND SEND_TIME >=" + " TO_DATE('" + begionTime
					+ "','yyyy-MM-dd HH24:MI:SS')";
		}

		String endTime = parm.getValue("END_TIME");
		if (endTime != null && !endTime.equals("")) {
			endTime = endTime.substring(0, endTime.length() - 2);
			sql += " AND SEND_TIME <=" + " TO_DATE('" + endTime
					+ "','yyyy-MM-dd HH24:MI:SS')";
		}

		String admType = parm.getValue("ADM_TYPE");
		if (admType != null && !admType.equals("")) {
			sql += " AND ADM_TYPE = '" + admType + "'";
		}

		sql += "  ORDER BY SEND_TIME DESC  ";
		// System.out.println("sql==================:"+sql);
		return sql;
	}

	public TParm onQueryMedSms(TParm parm) {
		String caseNo = parm.getValue("CASE_NO");
		String applicationNo = parm.getValue("APPLICATION_NO");
		String testitemCode = parm.getValue("TESTITEM_CODE");
		String sql = " SELECT A.SMS_CODE FROM MED_SMS A "
				+ " WHERE A.CASE_NO='" + caseNo + "' "
				+ "      AND A.APPLICATION_NO='" + applicationNo + "' "
				+ "      AND A.TESTITEM_CODE='" + testitemCode + "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public static void main(String[] args) {
		TParm parm = new TParm();
		parm.setData("SMS_CODE", "1");
		parm.setData("CASE_NO", "120401000345");
		parm.setData("NAME", "宋学英");
		parm.setData("MR_NO", "000000251379");
		parm.setData("STATION_CODE", "5");
		parm.setData("BED_NO", "6");
		parm.setData("IP_NO", "7");
		parm.setData("DEPT_CODE", "8");
		parm.setData("BILLING_DOCTORS", "9");
		parm.setData("APPLICATION_NO", "10");
		parm.setData("TESTITEM_CODE", "11");
		parm.setData("TESTITEM_CHN_DESC", "12");
		parm.setData("TEST_VALUE", "13");
		parm.setData("CRTCLLWLMT", "14");
		parm.setData("STATE", "15");
		parm.setData("SEND_TIME", "20120921");
		parm.setData("SMS_CONTENT", "17");
		parm.setData("OPT_USER", "18");
		parm.setData("OPT_DATE", "19");
		parm.setData("OPT_TERM", "20");
		parm.setData("ADM_TYPE", "21");
		// String sql = createSql(parm);

		String begionTime = "2012-07-10 00:00:00.0";
		begionTime = begionTime.substring(0, begionTime.length() - 2);
	}

	/**
	 * 通过病案号查询病人信息
	 * 
	 * @param parm
	 *            装有病案号的TParm
	 * @return
	 */
	public TParm getPatInfoByMrNo(TParm parm) {
		String admType = parm.getValue("ADM_TYPE");
		String mrNo = parm.getValue("MR_NO");
		String sql = "";
		if (admType.equals("I")) {// 住院
			sql = " SELECT CASE_NO, MR_NO, IPD_NO, BED_NO, DEPT_CODE, STATION_CODE FROM ADM_INP"
					+ " WHERE MR_NO = '" + mrNo + "'"
					+ " ORDER BY IN_DATE DESC";// 按如院时间倒序
		} else if (admType.equals("O")) {// 门诊
			sql = " SELECT CASE_NO, MR_NO, REALDEPT_CODE AS DEPT_CODE, CLINICAREA_CODE FROM REG_PATADM"
					+ " WHERE MR_NO = '"
					+ mrNo
					+ "'"
					+ " AND ADM_TYPE = 'O'"
					+ " ORDER BY REG_DATE DESC";// 按挂号时间倒序
		}else if (admType.equals("E")) {// 急诊
			sql = " SELECT CASE_NO, MR_NO, REALDEPT_CODE AS DEPT_CODE, CLINICAREA_CODE FROM REG_PATADM"
					+ " WHERE MR_NO = '"
					+ mrNo
					+ "'"
					+ " AND ADM_TYPE = 'E'"
					+ " ORDER BY REG_DATE DESC";// 按挂号时间倒序
		} else if (admType.equals("H")) {
			sql = " SELECT CASE_NO, MR_NO, DEPT_CODE FROM HRM_PATADM" + " WHERE MR_NO = '"
					+ mrNo + "'" + " ORDER BY START_DATE DESC";
		} 
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询健检医师
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getHrmDrCode(String caseNo) {
		String sql = " SELECT s.TEL1,r.DR_CODE "
				+ " FROM HRM_ORDER r,SYS_OPERATOR s "
				+ " WHERE r.DR_CODE=s.USER_ID " + " AND r.CASE_NO='" + caseNo
				+ "'" + " GROUP BY s.TEL1,r.DR_CODE";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询病人姓名
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getPatName(String mrNo) {
		String sql = " SELECT PAT_NAME FROM SYS_PATINFO " + " WHERE MR_NO= '"
				+ mrNo + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public void writeXml(TParm parmRow, TParm telParm, String content) {
		// 写文件
		TParm xmlParm = new TParm();

		xmlParm.setData("Content", content);
		xmlParm.setData("MrNo", telParm.getValue("MR_NO", 0));

		// 得到科室,门急住类别
		String deptChnCode = parmRow.getValue("DEPT_CHN_DESC");
		String admType = parmRow.getValue("ADM_TYPE");
		String admTypeChn = "";
		admTypeChn = getAdmType(admType);

		xmlParm.setData("Name", parmRow.getValue("PAT_NAME") + ","
				+ deptChnCode + "," + admTypeChn);
		// System.out.println("xmlParm:"+xmlParm);
		// System.out.println("telParm:"+telParm);
		XmlUtil.createSmsFile(xmlParm, telParm);
	}

	private String getAdmType(String admType) {
		String admTypeChn = "";
		if (admType != null) {
			if (admType.equals("O")) {
				admTypeChn = "门诊";
			} else if (admType.equals("I")) {
				admTypeChn = "住院";
			} else if (admType.equals("E")) {
				admTypeChn = "急诊";
			} else if (admType.equals("H")) {
				admTypeChn = "健康检查";
			}
		}
		return admTypeChn;
	}

	/**
	 * 通过UserId得到医师电话号码
	 * 
	 * @param billDoc
	 *            医师的UserId
	 * @return
	 */
	public TParm getDrPhone(String userId) {
		String sql = "SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID = '" + userId
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 通过UserId得到医师电话号码
	 * 
	 * @param billDoc
	 *            医师的UserId
	 * @return
	 */
	public TParm getDutyDrPhone(String deptCode) {
		String sql = "SELECT DUTY_TEL AS TEL1 FROM SYS_DEPT WHERE DEPT_CODE = '"
				+ deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 获取打印数据方法
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getPrintData(TParm parm) {
		String sql = "SELECT A.DEPT_CHN_DESC, "
				+ " A.ZLS, "
				+ " B.ASLS, "
				+ " C.KZRLS, "
				+ " D.KZGLS, "
				+ " E.ZGYZLS, "
				+ " F.ZYSLS "
				+ " FROM (  SELECT B.DEPT_CHN_DESC, A.DEPT_CODE, COUNT(*) AS ZLS "
				+ "  FROM MED_SMS A "
				+ "  LEFT JOIN SYS_DEPT B ON A.DEPT_CODE = B.DEPT_CODE "
				+ "  WHERE SEND_TIME>=TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ "  AND SEND_TIME<=TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ "  GROUP BY B.DEPT_CHN_DESC,A.DEPT_CODE) A "
				+ "     LEFT JOIN (  SELECT DEPT_CODE, COUNT(*) AS ASLS "
				+ " 	   FROM MED_SMS "
				+ " 	   WHERE ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) > 0 "
				+ " 	   AND ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) < 31 "
				+ " 	   AND SEND_TIME>=TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   AND SEND_TIME<=TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   GROUP BY DEPT_CODE) B "
				+ "     ON A.DEPT_CODE = B.DEPT_CODE "
				+ "     LEFT JOIN (  SELECT DEPT_CODE, COUNT(*) AS KZRLS "
				+ " 	   FROM MED_SMS "
				+ " 	   WHERE ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) > 30 "
				+ " 	   AND ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) < 41 "
				+ " 	   AND SEND_TIME>=TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   AND SEND_TIME<=TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   GROUP BY DEPT_CODE) C "
				+ "     ON A.DEPT_CODE = C.DEPT_CODE "
				+ "     LEFT JOIN (  SELECT DEPT_CODE, COUNT(*) AS KZGLS "
				+ " 	   FROM MED_SMS "
				+ " 	   WHERE ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) > 40 "
				+ " 	   AND ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) < 51 "
				+ " 	   AND SEND_TIME>=TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   AND SEND_TIME<=TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   GROUP BY DEPT_CODE) D "
				+ "     ON A.DEPT_CODE = D.DEPT_CODE "
				+ "     LEFT JOIN (  SELECT DEPT_CODE, COUNT(*) AS ZGYZLS "
				+ " 	   FROM MED_SMS "
				+ " 	   WHERE ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) > 50 "
				+ " 	   AND SEND_TIME>=TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ "    AND SEND_TIME<=TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   GROUP BY DEPT_CODE) E "
				+ "     ON A.DEPT_CODE = E.DEPT_CODE "
				+ "     LEFT JOIN (  SELECT DEPT_CODE, COUNT(*) AS ZYSLS "
				+ " 	   FROM MED_SMS "
				+ " 	   WHERE ROUND (TO_NUMBER (HANDLE_TIME - SEND_TIME) * 24 * 60) > 30 "
				+ " 	   AND SEND_TIME>=TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   AND SEND_TIME<=TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','yyyy-MM-dd HH24:MI:SS') "
				+ " 	   GROUP BY DEPT_CODE) F "
				+ "     ON A.DEPT_CODE = F.DEPT_CODE " + "     WHERE 1=1 ";
		if (parm.getValue("DEPT_CODE").length() > 0)
			sql += " AND A.DEPT_CODE = '" + parm.getValue("DEPT_CODE") + "'";
		// System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm queryLisReport(TParm parm) {
		TParm result = query("queryLisReport", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
