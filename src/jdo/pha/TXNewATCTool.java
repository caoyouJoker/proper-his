package jdo.pha;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.opd.TotQtyTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>
 * Title:门诊、住院包药机数据插入类
 * </p>
 * 
 * <p>
 * Description:门诊、住院包药机数据插入类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class TXNewATCTool {
	/**
	 * 实例
	 */
	public static TXNewATCTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return TXNewATCTool
	 */
	public static TXNewATCTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new TXNewATCTool();
		}
		return instanceObject;
	}

	/**
	 * 构造方法
	 */
	public TXNewATCTool() {
		// onSetConfig();
	}

	// /**
	// * 初始化配置(废除)
	// */
	// public void onSetConfig() {
	// this.setAtcDBType(TConfig.getSystemValue("ACTDB.Type"));
	// this.setAtcDBIp(TConfig.getSystemValue("ACTDB.Address"));
	// this.setAtcDBPort(TConfig.getSystemValue("ACTDB.Port"));
	// this.setAtcDBName(TConfig.getSystemValue("ACTDB.DBName"));
	// this.setAtcUserName(TConfig.getSystemValue("ACTDB.UserName"));
	// this.setAtcPassWord(TConfig.getSystemValue("ACTDB.Password"));
	// }
	/**
	 * 取得门诊新包药机处方签取号
	 * 
	 * @return String
	 */
	static public String getOATCCode() {
		return SystemTool.getInstance().getNo("ALL", "OPD", "ATC_CODE",
				"ATC_CODE");
	}

	/**
	 * 取得住院新包药机处方签取号
	 * 
	 * @return String
	 */
	static public String getIATCCode() {
		return SystemTool.getInstance().getNo("ALL", "ODI", "ATC_CODE",
				"ATC_CODE");
	}

	/**
	 * <br>
	 * 方法说明：获得数据连接 <br>
	 * 输入参数： <br>
	 * 返回类型：Connection 连接对象
	 */
	public Connection conn(String admType) {
		Connection conn = null;
		// 包药机数据库类型
		String atcDBType = "";
		// 包药机数据库IP
		String atcDBIp = "";
		// 包药机数据库端口
		String atcDBPort = "";
		// 包药机数据库名称
		String atcDBName = "";
		// 包药机数据库用户名
		String atcUserName = "";
		// 包药机数据库密码
		String atcPassWord = "";
		if (admType.equals("O")) {
			atcDBType = TConfig.getSystemValue("ACTDBO.Type");
			atcDBIp = TConfig.getSystemValue("ACTDBO.Address");
			atcDBPort = TConfig.getSystemValue("ACTDBO.Port");
			atcDBName = TConfig.getSystemValue("ACTDBO.DBName");
			atcUserName = TConfig.getSystemValue("ACTDBO.UserName");
			atcPassWord = TConfig.getSystemValue("ACTDBO.Password");

		} else if (admType.equals("I")) {
			atcDBType = TConfig.getSystemValue("ACTDBI.Type");
			atcDBIp = TConfig.getSystemValue("ACTDBI.Address");
			atcDBPort = TConfig.getSystemValue("ACTDBI.Port");
			atcDBName = TConfig.getSystemValue("ACTDBI.DBName");
			atcUserName = TConfig.getSystemValue("ACTDBI.UserName");
			atcPassWord = TConfig.getSystemValue("ACTDBI.Password");
		}
		try {
			String url = "";
			// 加载JDBC驱动
			if (atcDBType.equalsIgnoreCase("oracle"))
				Class.forName("oracle.jdbc.driver.OracleDriver");
			else if (atcDBType.equalsIgnoreCase("SQLServer"))
				Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			// URL
			if (atcDBType.equalsIgnoreCase("oracle"))
				url = "jdbc:oracle:thin:@" + atcDBIp + ":" + atcDBPort + ":"
						+ atcDBName;
			else if (atcDBType.equalsIgnoreCase("SQLServer"))
				url = " jdbc:microsoft:sqlserver://" + atcDBIp + ":"
						+ atcDBPort + ";databasename=" + atcDBName;
			// 创建数据库连接
			conn = DriverManager.getConnection(url, atcUserName, atcPassWord);
			return conn;
		} catch (ClassNotFoundException cnf) {
			System.out.println("driver not find:" + cnf);
			return null;
		} catch (SQLException sqle) {
			System.out.println("can't connection db:" + sqle);
			return null;
		} catch (Exception e) {
			System.out.println("Failed to load JDBC/ODBC driver.");
			return null;
		}
	}

	/**
	 * <br>
	 * 输入参数：Connection con 数据库连接 <br>
	 * 输入参数：String sql 要执行的SQL语句 <br>
	 * 返回类型：void
	 */
	public boolean executeUpdate(Connection con, String sql) {
		boolean exeFlg = true;
		Statement stmt = null;
		try {
			if (con == null) {
				exeFlg = false;
				return exeFlg;
			}
			stmt = con.createStatement();
			// 执行数据库操作（更新操作）
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			exeFlg = false;
			System.out.println("包药机数据插入异常SQL：" + sql);
			System.out.println(e);
		}
		return exeFlg;
	}

	/**
	 * <br>
	 * 取数据最大序号 输入参数：Connection con 数据库连接 <br>
	 * 输入参数：String sql 要执行的SQL语句 <br>
	 * 返回类型：void
	 */
	public Map executeQuery(String rxNo, String admType) {
		int seq = 0;
		Connection con = null;
		Map map = new HashMap();
		String[] preNo = null;
		if (rxNo.indexOf(",") > 0)
			preNo = rxNo.split(",");
		else {
			preNo = new String[1];
			preNo[0] = rxNo;
		}
		for (int i = 0; i < preNo.length; i++) {
			map.put(preNo[i], 0);
		}
		try {
			con = this.conn(admType);
			Statement stmt = con.createStatement();
			String sql = "SELECT A.PRESCRIPTIONNO AS PRESCRIPTIONNO,MAX(A.SEQNO) AS NUM FROM PRESCRIPTION A WHERE A.PRESCRIPTIONNO IN("
					+ rxNo + ") GROUP BY  A.PRESCRIPTIONNO ";
			// System.out.println("-=-sql-----------------------"+sql);
			// 执行数据库查询
			ResultSet set = stmt.executeQuery(sql);
			while (set.next()) {
				map.put(set.getString("PRESCRIPTIONNO"), set.getInt("NUM"));
			}
			stmt.close();
			con.commit();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 门诊数据插入
	 */
	public boolean OInsertData(TParm parm) {
		boolean flg = true;
		if (parm == null) {
			flg = false;
			return flg;
		}
		Connection con = this.conn("O");
		try {
			con.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		TParm inparm = parm.getParm("DRUG_LIST_PARM");
		for (int i = 0; i < inparm.getCount("PRESCRIPTIONNO"); i++) {
			String sql = this.SpellSql(inparm.getRow(i));
			// System.out.println("--------------sql----------------" + sql);
			if (!this.executeUpdate(con, sql)) {
				flg = false;
				break;
			}
		}
		try {
			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	}

	/**
	 * 住院数据插入
	 */
	public boolean IInsertData(TParm parm) {
		boolean flg = true;
		if (parm == null) {
			flg = false;
			return flg;
		}
		Connection con = this.conn("I");
		try {
			con.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			for (int i = 0; i < parm.getCount("PRESCRIPTIONNO"); i++) {
				String sql = "";
				try {
					sql = this.SpellSql(parm.getRow(i));
					if (!this.executeUpdate(con, sql)) {
						flg = false;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flg;
	}

	/**
	 * 将按病患分组
	 * 
	 * @param parm
	 * @return
	 */
	public Map groupByPatParm(TParm parm) {
		Map result = new HashMap();
		if (parm == null) {
			return null;
		}
		int count = parm.getCount();
		if (count < 1) {
			return null;
		}
		TParm temp = new TParm();
		String[] names = parm.getNames();
		if (names == null) {
			return null;
		}
		if (names.length < 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(";");
		}
		try {
			sb.replace(sb.lastIndexOf(";"), sb.length(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TParm tranParm = new TParm();
		for (int i = 0; i < count; i++) {
			String orderNo = parm.getValue("PATIENTID", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			} else {
				tranParm = (TParm) result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
		return result;
	}

	/**
	 * 得到重送标记
	 * 
	 * @param parm
	 * @return
	 */
	public String getSendAtcFlg(TParm parm) {
		String sendAtcDttm = "";
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		String orderSeq = parm.getValue("ORDER_SEQ");
		String startDttm = parm.getValue("START_DTTM");
		String barCode = parm.getValue("BAR_CODE");
		String sql = "SELECT TO_CHAR(SENDATC_DTTM,'YYYY/MM/DD HH24:MI:SS') AS SENDATC_DTTM FROM ODI_DSPNM  WHERE CASE_NO='"
				+ caseNo
				+ "' AND ORDER_NO='"
				+ orderNo
				+ "' AND ORDER_SEQ="
				+ orderSeq + " AND START_DTTM='" + startDttm + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() > 0) {
			sendAtcDttm = result.getValue("SENDATC_DTTM", 0);
		}
		return sendAtcDttm;
	}

	/**
	 * 拼装SQL
	 * 
	 * @param inparm
	 * @return
	 */
	public String SpellSql(TParm inparm) {
		String sql = "";
		String takedate = "";
		if (inparm.getValue("TAKEDATE").equals(""))
			takedate = "NULL,'";
		else
			takedate = "TO_DATE('"
					+ ("" + inparm.getTimestamp("TAKEDATE")).substring(0, 19)
					+ "', 'YYYY-MM-DD HH24:MI:SS'),'";// 19
		sql = "Insert into PRESCRIPTION"
				+ "(PRESCRIPTIONNO, SEQNO, GROUP_NO, MACHINENO, PROCFLG,"
				+ " PATIENTID, PATIENTNAME, ENGLISHNAME, BIRTHDAY, SEX,"
				+ " IOFLG, WARDCD, WARDNAME, ROOMNO, BEDNO,"
				+ " DOCTORCD, DOCTORNAME, PRESCRIPTIONDATE, TAKEDATE, TAKETIME,"
				+ " LASTTIME, PRESC_CLASS, DRUGCD, DRUGNAME, DRUGSHAPE,"
				+ " PRESCRIPTIONDOSE, PRESCRIPTIONUNIT, DISPENSEDDOSE,"
				+ " DISPENSEDTOTALDOSE, DISPENSEDUNIT,"
				+ " AMOUNT_PER_PACKAGE, FIRM_ID, DISPENSE_DAYS, FREQ_DESC_CODE, FREQ_DESC,"
				+ " FREQ_COUNTER, FREQ_DESC_DETAIL_CODE, FREQ_DESC_DETAIL, EXPLANATION_CODE,"
				+ " EXPLANATION,"
				+ " ADMINISTRATION_NAME, DOCTORCOMMENT, BAGORDERBY, MAKERECTIME, UPDATERECTIME,"
				+ " FILLER, ORDER_NO, ORDER_SUB_NO, BAGPRINTFMT, BAGLEN,"
				+ " TICKETNO, BAGPRINTPATIENTNM, FREEPRINTITEM_PRESC1, FREEPRINTITEM_PRESC2,"
				+ " FREEPRINTITEM_PRESC3,"
				+ " FREEPRINTITEM_PRESC4, FREEPRINTITEM_PRESC5, FREEPRINTITEM_DRUG1,"
				+ " FREEPRINTITEM_DRUG2, FREEPRINTITEM_DRUG3,"
				+ " FREEPRINTITEM_DRUG4, FREEPRINTITEM_DRUG5, SYNTHETICFLG, CUTFLG, PHARMACYTIME,"
				+ " CARVEDSEAL, CARVEDSEALABB, PREBARCODE1, PREBARCODE2, PREDRUGBARCODE,"
				+ " PREBARCODEFMT)" + " Values" + "('"
				+ inparm.getValue("PRESCRIPTIONNO")// 1
				+ "',"
				+ inparm.getInt("SEQNO")// 2
				+ ","
				+ inparm.getInt("GROUP_NO")// 3
				+ ","
				+ inparm.getInt("MACHINENO")// 4
				+ ","
				+ inparm.getInt("PROCFLG")// 5
				+ ",'"
				+ inparm.getValue("PATIENTID")// 6
				+ "','"
				+ inparm.getValue("PATIENTNAME")// 7
				+ "','"
				+ inparm.getValue("ENGLISHNAME")// 8
				+ "',"
				+ "TO_DATE('"
				+ ("" + inparm.getTimestamp("BIRTHDAY")).substring(0, 19)
				+ "', 'YYYY-MM-DD HH24:MI:SS')"// 9
				+ ",'"
				+ inparm.getValue("SEX")// 10
				+ "','"
				+ inparm.getValue("IOFLG")// 11
				+ "','"
				+ inparm.getValue("WARDCD")// 12
				+ "','"
				+ inparm.getValue("WARDNAME")// 13
				+ "','"
				+ inparm.getValue("ROOMNO")// 14
				+ "','"
				+ inparm.getValue("BEDNO")// 15
				+ "','"
				+ inparm.getValue("DOCTORCD")// 16
				+ "','"
				+ inparm.getValue("DOCTORNAME")// 17
				+ "',"
				+ "TO_DATE('"
				+ ("" + inparm.getTimestamp("PRESCRIPTIONDATE")).substring(0,
						19)
				+ "', 'YYYY-MM-DD HH24:MI:SS'),"// 18
				+ takedate
				+ inparm.getValue("TAKETIME")// 20
				+ "','"
				+ inparm.getValue("LASTTIME")// 21
				+ "',"
				+ inparm.getInt("PRESC_CLASS")// 22
				+ ",'"
				+ inparm.getValue("DRUGCD")// 23
				+ "','"
				+ inparm.getValue("DRUGNAME")// 24
				+ "','"
				+ inparm.getValue("DRUGSHAPE")// 25
				+ "',"
				+ inparm.getDouble("PRESCRIPTIONDOSE")// 26
				+ ",'"
				+ inparm.getValue("PRESCRIPTIONUNIT")// 27
				+ "',"
				+ inparm.getDouble("DISPENSEDDOSE")// 28
				+ ","
				+ inparm.getDouble("DISPENSEDTOTALDOSE")// 29
				+ ",'"
				+ inparm.getValue("DISPENSEDUNIT")// 30
				+ "',"
				+ (inparm.getValue("AMOUNT_PER_PACKAGE").equals("") ? "NULL"
						: inparm.getDouble("AMOUNT_PER_PACKAGE"))// 31
				+ ",'"
				+ inparm.getValue("FIRM_ID")// 32
				+ "',"
				+ inparm.getInt("DISPENSE_DAYS")// 33
				+ ",'"
				+ inparm.getValue("FREQ_DESC_CODE")// 34
				+ "','"
				+ inparm.getValue("FREQ_DESC")// 35
				+ "','"
				+ inparm.getValue("FREQ_COUNTER")// 36
				+ "','"
				+ inparm.getValue("FREQ_DESC_DETAIL_CODE")// 37
				+ "','"
				+ inparm.getValue("FREQ_DESC_DETAIL")// 38
				+ "','"
				+ inparm.getValue("EXPLANATION_CODE")// 39
				+ "','"
				+ inparm.getValue("EXPLANATION")// 40
				+ "','"
				+ inparm.getValue("ADMINISTRATION_NAME")// 41
				+ "','"
				+ inparm.getValue("DOCTORCOMMENT")// 42
				+ "',"
				+ (inparm.getValue("BAGORDERBY").equals("") ? "NULL" : inparm
						.getInt("BAGORDERBY"))// 43
				+ ","
				+ "TO_DATE('"
				+ inparm.getValue("MAKERECTIME")
				+ "', 'YYYY-MM-DD HH24:MI:SS'),"// 44
				+ "NULL,'" // 45(默认为空)
				+ inparm.getValue("FILLER")// 46
				+ "',"
				+ (inparm.getValue("ORDER_NO").equals("") ? "NULL" : inparm
						.getLong("ORDER_NO"))// 47
				+ ","
				+ (inparm.getValue("ORDER_SUB_NO").equals("") ? "NULL" : inparm
						.getInt("ORDER_SUB_NO"))// 48
				+ ",'" + inparm.getValue("BAGPRINTFMT")// 49
				+ "','" + inparm.getValue("BAGLEN")// 50
				+ "','" + inparm.getValue("TICKETNO")// 51
				+ "','" + inparm.getValue("BAGPRINTPATIENTNM")// 52
				+ "','" + inparm.getValue("FREEPRINTITEM_PRESC1")// 53
				+ "','" + inparm.getValue("FREEPRINTITEM_PRESC2")// 54
				+ "','" + inparm.getValue("FREEPRINTITEM_PRESC3")// 55
				+ "','" + inparm.getValue("FREEPRINTITEM_PRESC4")// 56
				+ "','" + inparm.getValue("FREEPRINTITEM_PRESC5")// 57
				+ "','" + inparm.getValue("FREEPRINTITEM_DRUG1")// 58
				+ "','" + inparm.getValue("FREEPRINTITEM_DRUG2")// 59
				+ "','" + inparm.getValue("FREEPRINTITEM_DRUG3")// 60
				+ "','" + inparm.getValue("FREEPRINTITEM_DRUG4")// 61
				+ "','" + inparm.getValue("FREEPRINTITEM_DRUG5")// 62
				+ "','" + inparm.getValue("SYNTHETICFLG")// 63
				+ "','" + inparm.getValue("CUTFLG")// 64
				+ "','" + inparm.getValue("PHARMACYTIME")// 65
				+ "','" + inparm.getValue("CARVEDSEAL")// 66
				+ "','" + inparm.getValue("CARVEDSEALABB")// 67
				+ "','" + inparm.getValue("PREBARCODE1")// 68
				+ "','" + inparm.getValue("PREBARCODE2")// 69
				+ "','" + inparm.getValue("PREDRUGBARCODE")// 70
				+ "','" + inparm.getValue("PREBARCODEFMT") + "')";// 71
		return sql;
	}

	// /**
	// * @return the atcDBIp
	// */
	// public String getAtcDBIp() {
	// return atcDBIp;
	// }
	//
	// /**
	// * @param atcDBIp
	// * the atcDBIp to set
	// */
	// public void setAtcDBIp(String atcDBIp) {
	// this.atcDBIp = atcDBIp;
	// }
	//
	// /**
	// * @return the atcDBPort
	// */
	// public String getAtcDBPort() {
	// return atcDBPort;
	// }
	//
	// /**
	// * @param atcDBPort
	// * the atcDBPort to set
	// */
	// public void setAtcDBPort(String atcDBPort) {
	// this.atcDBPort = atcDBPort;
	// }
	//
	// /**
	// * @return the atcDBName
	// */
	// public String getAtcDBName() {
	// return atcDBName;
	// }
	//
	// /**
	// * @param atcDBName
	// * the atcDBName to set
	// */
	// public void setAtcDBName(String atcDBName) {
	// this.atcDBName = atcDBName;
	// }
	//
	// /**
	// * @return the atcUserName
	// */
	// public String getAtcUserName() {
	// return atcUserName;
	// }
	//
	// /**
	// * @param atcUserName
	// * the atcUserName to set
	// */
	// public void setAtcUserName(String atcUserName) {
	// this.atcUserName = atcUserName;
	// }
	//
	// /**
	// * @return the atcPassWord
	// */
	// public String getAtcPassWord() {
	// return atcPassWord;
	// }
	//
	// /**
	// * @param atcPassWord
	// * the atcPassWord to set
	// */
	// public void setAtcPassWord(String atcPassWord) {
	// this.atcPassWord = atcPassWord;
	// }
	//
	// /**
	// * @return the atcDBType
	// */
	// public String getAtcDBType() {
	// return atcDBType;
	// }
	//
	// /**
	// * @param atcDBType
	// * the atcDBType to set
	// */
	// public void setAtcDBType(String atcDBType) {
	// this.atcDBType = atcDBType;
	// }

	/**
	 * 获得时间编码
	 * 
	 * @param freqCode
	 * @return
	 */
	static public String getTimeLine(String freqCode) {
		String Str = "";
		List timeList = TotQtyTool.getInstance().getStandingTime(freqCode,
				false);
		if (timeList.size() > 0) {
			for (int i = 0; i < timeList.size(); i++) {
				if (Str.length() > 0)
					Str += "-";
				Str += timeList.get(i);
			}
		}
		return Str;
	}

	/**
	 * 获得时间详细
	 * 
	 * @param freqCode
	 * @return
	 */
	static public String getTimeDetail(String freqCode) {
		String Str = "";
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT FREQ_UNIT_48,SUN_FLG,MON_FLG,TUE_FLG,WED_FLG, "
										+ " THUR_FLG,FRI_FLG,STA_FLG FROM SYS_PHAFREQ WHERE FREQ_CODE='"
										+ freqCode + "'"));
		if (parm.getErrCode() != 0) {
			return Str;
		}
		// 时间字符串
		String timeStr = "";
		List result = new ArrayList();
		String sFreq_Unit48 = parm.getValue("FREQ_UNIT_48", 0);
		char[] freq48 = sFreq_Unit48.toCharArray();
		for (int i = 0; i < freq48.length; i++) {
			String cnt = "";
			if ("Y".equalsIgnoreCase(String.valueOf(freq48[i]))) {
				// 正点判断
				if ((i + 1) % 2 != 0) {
					cnt = String.valueOf((i + 1) % 2 == 0 ? (i + 1) / 2 - 1
							: (i + 1) / 2);
					if (cnt.length() == 0)
						cnt = "00";
					if (cnt.length() == 1)
						cnt = "0" + cnt;
					cnt += ":00";
				} else {
					cnt = String.valueOf((i + 1) % 2 == 0 ? (i + 1) / 2 - 1
							: (i + 1) / 2);
					if (cnt.length() == 0)
						cnt = "00";
					if (cnt.length() == 1)
						cnt = "0" + cnt;
					cnt += ":30";
				}
				if (timeStr.length() > 0)
					timeStr += "-";
				timeStr += cnt;
			}
		}
		String WeekStr = "";
		// 取得星期几，星期日为7，星期六为6
		if (TCM_Transform.getBoolean(parm.getValue("SUN_FLG", 0))) {
			WeekStr += "7";
		}
		if (TCM_Transform.getBoolean(parm.getValue("MON_FLG", 0))) {
			if (WeekStr.length() > 0)
				WeekStr += ";1";
			else
				WeekStr += "1";
		}
		if (TCM_Transform.getBoolean(parm.getValue("TUE_FLG", 0))) {
			if (WeekStr.length() > 0)
				WeekStr += ";2";
			else
				WeekStr += "2";
		}
		if (TCM_Transform.getBoolean(parm.getValue("WED_FLG", 0))) {
			if (WeekStr.length() > 0)
				WeekStr += ";3";
			else
				WeekStr += "3";
		}
		if (TCM_Transform.getBoolean(parm.getValue("THUR_FLG", 0))) {
			if (WeekStr.length() > 0)
				WeekStr += ";4";
			else
				WeekStr += "4";
		}
		if (TCM_Transform.getBoolean(parm.getValue("FRI_FLG", 0))) {
			if (WeekStr.length() > 0)
				WeekStr += ";5";
			else
				WeekStr += "5";
		}
		if (TCM_Transform.getBoolean(parm.getValue("STA_FLG", 0))) {
			if (WeekStr.length() > 0)
				WeekStr += ";6";
			else
				WeekStr += "6";
		}
		if (WeekStr.length() > 0)
			Str = WeekStr + " " + timeStr;
		else
			Str = timeStr;
		return Str;
	}
}
