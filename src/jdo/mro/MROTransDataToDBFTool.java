package jdo.mro;

import java.sql.*;

import javax.swing.JOptionPane;

import java.io.FileWriter;
import java.io.IOException;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 生成DBF：病案首页、门诊、住院
 * </p>
 * 
 * <p>
 * Description: 生成DBF：病案首页、门诊、住院
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20111101
 * @version 1.0
 */
public class MROTransDataToDBFTool extends TJDOTool {
	DateFormat df;
	DateFormat df1;

	public MROTransDataToDBFTool() {
		df = new SimpleDateFormat("yyyy-MM-dd");
		df1 = new SimpleDateFormat("HH");
	}

	/**
	 * 实例
	 */
	public static MROTransDataToDBFTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static MROTransDataToDBFTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROTransDataToDBFTool();
		return instanceObject;
	}

	// 病案基本信息表
	private String[] MROInsertData = { "baza00", "baza01", "baza02", "baza03",
			"baza04", "baza05", "baza06", "bazaa1", "baza07", "baza08",
			"baza09", "baza10", "bazaa2", "baza11", "bazaa3", "bazaa4",
			"baza12", "baza13", "bazaa5", "baza14", "baza15", "baza16",
			"baza17", "baza18", "baza19", "baza20", "baza21", "bazaba",
			"baza22", "bazabb", "baza23", "bazabc", "baza24", "baza25",
			"baza26", "baza27", "baza28", "baza29", "baza30", "baza31",
			"bazab1", "baza32", "baza33", "baza34", "baza35", "bazab2",
			"bazab3", "bazab4", "baza41", "baza43", "baza44", "bazac1",
			"bazac2", "bazac3", "baza45", "bazac5", "bazac6", "bazac7",
			"bazac8", "bazac9", "bazaca", "bazacb", "bazacc", "baza46",
			"baza47", "baza48", "baza49", "baza50", "baza51", "baza52",
			"baza53", "baza55", "baza56", "baza57", "baza59", "baza58",
			"baza61", "baza62", "baza63", "baza64", "baza65", "baza69",
			"baza70", "baza73", "baza74", "baza75", "baza76", "baza81",
			"baza82", "baza83", "baza84", "baza85", "baza86", "baza87",
			"baza88", "baza89", "baza90", "baza91", "baza92", "baza93",
			"baza94", "baza95", "baza96", "baza97", "baza98", "baza99",
			"bazae2", "bazae3", "bazae4", "bazae5", "bazae6" };
	// 门诊科室工作日志登记表
	private String[] OPDInsertData = { "TJM2RQ", "TJM2KB", "TJM2001",
			"TJM2002", "TJM2003", "TJM2004", "TJM2005", "TJM2006", "TJM2007",
			"TJM2008", "TJM2009", "TJM2010", "TJM2011", "TJM2012", "TJM2013",
			"TJM2014", "TJM2015", "TJM2016" };
	// 住院病房工作日志登记表
	private String[] ODIInsertData = { "TJZ1RQ", "TJZ1KB", "TJZ1001",
			"TJZ1002", "TJZ1003", "TJZ1004", "TJZ1005", "TJZ1007", "TJZ1008",
			"TJZ1009", "TJZ1010", "TJZ1014", "TJZ1012", "TJZ1013", "TJZ1015",
			"TJZ1016", "TJZ1017", "TJZ1018", "TJZ1019", "TJZ1020" };
	// 出院次诊断信息表
	private String[] outDiagInfo = { "BAZA01", "BAF102", "BAF103", "BAF104",
			"BAF105", "BAF106", "BAF107", "BAF111" };
	// 查询数据SQL
	private String SQL = "SELECT CASE_NO, MR_NO, IPD_NO, PAT_NAME, SEX, "
			+ "BIRTH_DATE, AGE, MARRIGE, OCCUPATION,IN_COUNT, "
			+ "FOLK, NATION, IDNO, CTZ1_CODE,HOMEPLACE_CODE, "
			+ "OFFICE, O_ADDRESS, O_TEL, O_POSTNO, H_ADDRESS, "
			+ "H_TEL, H_POSTNO, CONTACTER, RELATIONSHIP, CONT_ADDRESS, "
			+ "CONT_TEL, IN_DATE, IN_DEPT, IN_STATION, IN_ROOM_NO, "
			+ "TRANS_DEPT,OUT_DATE, OUT_DEPT, OUT_STATION, OUT_ROOM_NO, "
			+ "REAL_STAY_DAYS, OE_DIAG_CODE, IN_CONDITION, IN_DIAG_CODE, CONFIRM_DATE,  "
			+ "OUT_DIAG_CODE1, CODE1_REMARK, CODE1_STATUS, OUT_DIAG_CODE2, CODE2_REMARK, "
			+ "CODE2_STATUS, OUT_DIAG_CODE3, CODE3_REMARK,CODE3_STATUS, OUT_DIAG_CODE4, "
			+ "CODE4_REMARK, CODE4_STATUS, OUT_DIAG_CODE5, CODE5_REMARK, CODE5_STATUS, "
			+ "OUT_DIAG_CODE6,CODE6_REMARK, CODE6_STATUS, INTE_DIAG_CODE, PATHOLOGY_DIAG, "
			+ "PATHOLOGY_REMARK, EX_RSN, ALLEGIC, HBSAG, HCV_AB, "
			+ "HIV_AB,QUYCHK_OI, QUYCHK_INOUT, QUYCHK_OPBFAF, QUYCHK_CLPA, "
			+ "QUYCHK_RAPA, GET_TIMES, SUCCESS_TIMES, DIRECTOR_DR_CODE, PROF_DR_CODE,  "
			+ "ATTEND_DR_CODE, VS_DR_CODE, INDUCATION_DR_CODE, GRADUATE_INTERN_CODE, INTERN_DR_CODE, "
			+ "ENCODER, QUALITY, CTRL_DR, CTRL_NURSE,CTRL_DATE, "
			+ "INFECT_REPORT, OP_CODE, OP_DATE, MAIN_SUGEON, OP_LEVEL, "
			+ "HEAL_LV, DIS_REPORT, BODY_CHECK, FIRST_CASE, ACCOMPANY_WEEK,  "
			+ "ACCOMPANY_MONTH, ACCOMPANY_YEAR, ACCOMP_DATE, SAMPLE_FLG, BLOOD_TYPE, "
			+ "RH_TYPE, TRANS_REACTION, RBC, PLATE, PLASMA,  "
			+ "WHOLE_BLOOD, OTH_BLOOD, STATUS, PG_OWNER, DRPG_OWNER, "
			+ "FNALPG_OWNER, ADMCHK_FLG, DIAGCHK_FLG, BILCHK_FLG, QTYCHK_FLG,  "
			+ "CHARGE_01, CHARGE_02, CHARGE_03, CHARGE_04, CHARGE_05, "
			+ "CHARGE_06, CHARGE_07, CHARGE_08, CHARGE_09, CHARGE_10, "
			+ "CHARGE_11,CHARGE_12, CHARGE_13, CHARGE_14, CHARGE_15, "
			+ "CHARGE_16, CHARGE_17, CHARGE_18, CHARGE_19, CHARGE_20, "
			+ "OPT_USER, OPT_DATE,OPT_TERM,MRO_CHAT_FLG,ADDITIONAL_CODE1, "
			+ "ADDITIONAL_CODE2,ADDITIONAL_CODE3,ADDITIONAL_CODE4,ADDITIONAL_CODE5,ADDITIONAL_CODE6, "
			+ "OE_DIAG_CODE2,OE_DIAG_CODE3,IN_DIAG_CODE2,IN_DIAG_CODE3,INTE_DIAG_STATUS,"
			+ "TEST_EMR,TEACH_EMR,CLNCPATH_CODE,DISEASES_CODE "
			+ "FROM MRO_RECORD ";

	/**
	 * 生成病案首页DBF文件
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMRODBF(TParm parm, TControl contorl) {
		// String TabName=parm.getValue("TabName");

		TParm result = new TParm();
		// if (null == TabName || TabName.equals("")) {
		// contorl.messageBox("请输入DBF数据表名称!!", "信息", JOptionPane.ERROR_MESSAGE);
		// result.setData("DBF_TYPE","1");//操作失败
		// return result;
		// }
		// Connection con1=makeConnection(TabName);
		// if (con1 == null) {
		// contorl.messageBox("dbf连接失败，请重新配置!! \n 请确定设定odbc数据库名称为'" + TabName +
		// "'!!", "信息", JOptionPane.ERROR_MESSAGE);
		// result.setData("DBF_TYPE", "1");
		// return result;
		// }
		StringBuffer caseNoSum = new StringBuffer();
		caseNoSum.append(" WHERE OUT_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')");
		// for (int i = 0; i < parm.getCount(); i++) {
		// caseNoSum.append(parm.getValue("CASE_NO", i) + ",");
		// }
		// String caseTemp = caseNoSum.toString().substring(0,
		// caseNoSum.toString().
		// lastIndexOf(",")) + ")";
		parm = new TParm(TJDODBTool.getInstance().select(SQL + caseNoSum));
		if (parm.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		if (null == parm || parm.getCount() <= 0) {
			contorl.messageBox("没有需要插入数据，请检查出院查询日期是否正确!!");
			result.setData("DBF_TYPE", "1");
			return result;
		}
		// String[] InsertData=new String[100];sys
		String TabName = "c:\\Sqlserver"
				+ StringTool.getString(SystemTool.getInstance().getDate(),
						"yyyyMMdd") + ".txt";
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < MROInsertData.length; i++) {
			content.append(MROInsertData[i] + " ");
		}
		content.append(";");
		for (int i = 0; i < parm.getCount(); i++) {
			result = mroUpdateName(parm.getRow(i));
			for (int j = 0; j < MROInsertData.length; j++) {
				content.append(result.getValue(MROInsertData[j]) + " ");
			}
			content.append(";");
		}
		appendLog(TabName, content.toString());
		contorl.messageBox("DBF转档成功");
		return result;
	}

	/**
	 * 门诊与住院工作日志登记参数
	 * 
	 * @param parm
	 *            TParm 界面获得的参数
	 * @param stringName
	 *            String 需要通过传入的值显示的数据：门诊和住院
	 * @param contorl
	 *            TControl
	 * @return TParm
	 */
	public TParm getOPDAndODIDBF(TParm parm, String stringName, TControl contorl) {
		TParm result = new TParm();
		if (null == parm || parm.getCount() <= 0) {
			contorl.messageBox("没有需要插入数据，请确认界面中有数据!!", "信息",
					JOptionPane.ERROR_MESSAGE);
			result.setData("DBF_TYPE", "1");
			return result;
		}

		String TabName = "c:\\Sqlserver"
				+ stringName
				+ StringTool.getString(SystemTool.getInstance().getDate(),
						"yyyyMMdd") + ".txt";
		StringBuffer content = new StringBuffer();
		if ("OPD".equals(stringName)) {
			for (int i = 0; i < OPDInsertData.length; i++) {
				content.append(OPDInsertData[i] + "\t\t");
			}
			content.append("\n");
			for (int i = 0; i < parm.getCount(); i++) {
				result = opdUpdateName(parm.getRow(i));
				for (int j = 0; j < OPDInsertData.length; j++) {
					content.append(result.getValue(OPDInsertData[j]) + "\t\t");
				}
				content.append("\n");
			}

		} else if ("ODI".equals(stringName)) {
			for (int i = 0; i < ODIInsertData.length; i++) {
				content.append(ODIInsertData[i] + "\t\t");
			}
			content.append("\n");
			for (int i = 0; i < parm.getCount(); i++) {
				result = odiUpdateName(parm.getRow(i));
				for (int j = 0; j < ODIInsertData.length; j++) {
					content.append(result.getValue(ODIInsertData[j]) + "\t\t");
				}
				content.append("\n");
			}

		}

		appendLog(TabName, content.toString());
		contorl.messageBox("DBF转档成功");
		return result;
	}

	// /**
	// * 数据库连接
	// * @param tabname String
	// * @return Connection
	// */
	// //connect to database
	// private Connection makeConnection(String tabname) {
	// Connection con = null;
	// try {
	// //con =(Connection)DriverManager.getConnection (url,username,password);
	// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	// //con = DriverManager.getConnection("jdbc:odbc:tjza");
	// con = DriverManager.getConnection("jdbc:odbc:" + tabname);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return con;
	// }
	/**
	 * 病案首页DBF参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm mroUpdateName(TParm parm) {
		TParm result = new TParm();
		String mrNo = parm.getValue("MR_NO");
		mrNo = mrNo.substring(4, mrNo.length());
		result.setData("baza00", StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy")
				+ mrNo
				+ (parm.getInt("IN_COUNT") < 10 ? "0" + parm.getInt("IN_COUNT")
						: parm.getInt("IN_COUNT"))); // 病案唯一性标志（键）4位出院年度+10病案号（病案号见下面说明）
		result.setData("baza01", resultName(parm.getValue("MR_NO"))); // 病案号8位病案号(不足8位用‘0’于前面补位)+2位住院次数(如‘01’)
		result.setData("baza02", resultName(parm.getValue("PAT_NAME"))); // 病人姓名须填写，不超过8个汉字
		// 查找性别
		String returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_SEX' AND ID='" + parm.getValue("SEX") + "'",
				"STA1_CODE");
		result.setData("baza03", returnString); // 性别
												// 须填写国家标准代码(0未知，1男，2女，3女变男，4男变女，9未说明)
		result.setData("baza04", null == parm.getTimestamp("BIRTH_DATE") ? ""
				: df.format(parm.getTimestamp("BIRTH_DATE"))); // 出生日期 须填写，形如
																// 2002-01-01
		result.setData("baza05", resultName(parm.getValue("IDNO"))); // 身份证号
																		// 填写不超过18位
		result.setData("baza06",
				resultName(parm.getValue("AGE")).equals("") ? "" : parm
						.getValue("AGE").substring(0,
								parm.getValue("AGE").length() - 1)); // 年龄
																		// 填写整数，如25
		result.setData("bazaa1", "Y"); // 年龄单位 填写代码(Y岁，M月，D天)
		// 查找婚姻状况
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_MARRIAGE' AND ID='" + parm.getValue("MARRIGE")
						+ "'", "STA1_CODE");
		result.setData("baza07", returnString.equals("") ? "9" : returnString); // 婚姻状况
																				// 填写国家标准代码(1未婚，2已婚，3丧偶，4离婚，9其他)
		// 查找职业
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_OCCUPATION' AND ID='"
						+ parm.getValue("OCCUPATION") + "'", "STA1_CODE");
		result.setData("baza08", returnString); // 职业 填写国家标准代码(GB6565-1999)
		// 查找出生地
		returnString = sqlReturnParm("SYS_HOMEPLACE", "HOMEPLACE_CODE='"
				+ parm.getValue("HOMEPLACE_CODE") + "'", "STA1_CODE");
		result.setData("baza09", returnString); // 出生地 填写国家标准代码(GB/T2260-84)
		// 查找民族
		returnString = sqlReturnParm(
				"SYS_DICTIONARY",
				"GROUP_ID='SYS_SPECIES' AND ID='" + parm.getValue("FOLK") + "'",
				"STA1_CODE");
		result.setData("baza10", returnString); // 民族 填写国家标准代码(GB/3304-91)
		// 查找国籍
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_NATION' AND ID='" + parm.getValue("NATION")
						+ "'", "STA1_CODE");
		result.setData("bazaa2", returnString); // 国籍 填写国家标准代码
		result.setData("baza11", resultName(parm.getValue("O_TEL"))); // 单位电话
																		// 填写患者单位电话,不超过16个字符
		result.setData("bazaa3", resultName(parm.getValue("O_ADDRESS"))); // 单位名称
																			// 填写患者单位名称,不超过20个字符
		result.setData("bazaa4", resultName(parm.getValue("O_POSTNO"))); // 单位邮编
		// 查找户口地址（省市县）
		returnString = sqlReturnParm("SYS_POSTCODE", "POST_CODE='"
				+ parm.getValue("H_POSTNO") + "'", "STA1_CODE");
		result.setData("baza12", returnString); // 户口地址（省市县）填写国家标准代码
		result.setData("baza13", resultName(parm.getValue("H_ADDRESS"))); // 户口地址（街村）
																			// 填写患者乡镇街村,不超过15个字符
		result.setData("bazaa5", resultName(parm.getValue("H_TEL"))); // 电话
		result.setData("baza14", resultName(parm.getValue("H_POSTNO"))); // 邮政编码
		result.setData("baza15", resultName(parm.getValue("CONTACTER"))); // 关系人姓名
		// 查找关系填写代码
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_RELATIONSHIP' AND ID='"
						+ parm.getValue("RELATIONSHIP") + "'", "STA1_CODE");
		result.setData("baza16", returnString); // 关系填写代码(1-9),参见国家标准代码
		result.setData("baza17", ""); // 关系地址（省市县）//有问题-----------设置NULL
		result.setData("baza18", resultName(parm.getValue("CONT_ADDRESS"))); // 关系地址（街村）
		result.setData("baza19", resultName(parm.getValue("CONT_TEL"))); // 关系电话
		// 查找入院情况
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='ADM_CONDITION' AND ID='"
						+ parm.getValue("IN_CONDITION") + "'", "STA1_CODE");
		result.setData("baza20", returnString); // 入院情况 填写代码(1危，2急，3一般)
		// 查找入院科别代码
		returnString = sqlReturnParm("SYS_DEPT", "DEPT_CODE='"
				+ parm.getValue("IN_DEPT") + "'", "STA1_CODE");
		result.setData("baza21", returnString); // 入院科别代码
		// 查找入院病区代码
		// returnString=sqlReturnParm("SYS_STATION","STATION_CODE='"+parm.getValue("IN_STATION")+"'","STA1_CODE");
		result.setData("bazaba", parm.getValue("IN_STATION")); // 入院病区代码
		// 查找转入科别代码
		returnString = sqlReturnParm("SYS_DEPT", "DEPT_CODE='"
				+ parm.getValue("TRANS_DEPT") + "'", "STA1_CODE");
		result.setData("baza22", returnString); // 转入科别代码
		// 查找转入病区代码
		// returnString=sqlReturnParm("SYS_STATION","STATION_CODE='"+parm.getValue("IN_STATION")+"'","STA1_CODE");
		// returnString=sqlReturnParm("ADM_INP","CASE_NO ='"+parm.getValue("CASE_NO")+"'","OPD_DR_CODE");
		result.setData("bazabb", ""); // 转入病区代码-------???不存在转入病区:
		// 查找出院科别代码
		returnString = sqlReturnParm("SYS_DEPT", "DEPT_CODE='"
				+ parm.getValue("OUT_DEPT") + "'", "STA1_CODE");
		result.setData("baza23", returnString); // 出院科别代码
		// 查找入院病区代码
		returnString = sqlReturnParm("SYS_STATION", "STATION_CODE='"
				+ parm.getValue("OUT_STATION") + "'", "STA1_CODE");
		result.setData("bazabc", returnString); // 出院病区代码
		result.setData("baza24", null == parm.getTimestamp("IN_DATE") ? "" : df
				.format(parm.getTimestamp("IN_DATE"))); // 入院日期
		result.setData("baza25", null == parm.getTimestamp("IN_DATE") ? ""
				: df1.format(parm.getTimestamp("IN_DATE"))); // 入院时间:填写钟点，如10
		// 查找出院方式
		// returnString=sqlReturnParm("SYS_DICTIONARY","GROUP_ID='ADM_RETURN' AND ID='"+parm.getValue("CODE1_STATUS")+"'","STA1_CODE");
		result.setData("baza26", ""); // 出院方式 填写代码(1常规，2自动，3转院)-----不确定 现在设置NULL
		result.setData("baza27", null == parm.getTimestamp("OUT_DATE") ? ""
				: df.format(parm.getTimestamp("OUT_DATE"))); // 出院日期
		result.setData("baza28", null == parm.getTimestamp("OUT_DATE") ? ""
				: df1.format(parm.getTimestamp("OUT_DATE"))); // 出院时间
		result.setData("baza29", resultName(parm.getValue("REAL_STAY_DAYS"))); // 住院天数
																				// :填写大于0的整数
		result.setData("baza30", null == parm.getTimestamp("CONFIRM_DATE") ? ""
				: df.format(parm.getTimestamp("CONFIRM_DATE"))); // 确诊日期
		int day = 0;
		if (null != parm.getTimestamp("CONFIRM_DATE"))
			day = StringTool.getDateDiffer(parm.getTimestamp("CONFIRM_DATE"),
					parm.getTimestamp("IN_DATE"));
		result.setData("baza31", null == parm.getTimestamp("CONFIRM_DATE") ? ""
				: day); // 第几天确诊
		// 查找科主任
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("DIRECTOR_DR_CODE") + "'", "USER_NAME");
		result.setData("bazab1", returnString); // 科主任 填写科主任姓名
		// 查找主（副主）任医师 填写姓名
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("PROF_DR_CODE") + "'", "USER_NAME");
		result.setData("baza32", returnString); // 主（副主）任医师 填写姓名
		// 查找主治医师填写姓名
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("ATTEND_DR_CODE") + "'", "USER_NAME");
		result.setData("baza33", returnString); // 主治医师填写姓名
		// 查找住院医师填写姓名
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("VS_DR_CODE") + "'", "USER_NAME");
		result.setData("baza34", returnString); // 住院医师填写姓名
		// 查找门诊医师填写姓名
		returnString = sqlReturnParm("ADM_INP", "CASE_NO ='"
				+ parm.getValue("CASE_NO") + "'", "OPD_DR_CODE");
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='" + returnString
				+ "'", "USER_NAME");
		result.setData("baza35", returnString); // 门诊医师填写姓名不确定没找到门诊医生代码
		// 查找进修医师填写姓名
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("INDUCATION_DR_CODE") + "'", "USER_NAME");
		result.setData("bazab2", returnString); // 进修医师填写姓名
		// 查找研究生实习医师填写姓名
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("GRADUATE_INTERN_CODE") + "'", "USER_NAME");
		result.setData("bazab3", returnString); // 研究生实习医师填写姓名
		// 查找实习医师填写姓名
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("INTERN_DR_CODE") + "'", "USER_NAME");
		result.setData("bazab4", returnString); // 实习医师填写姓名
		// 查找转归
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='ADM_RETURN' AND ID='"
						+ parm.getValue("CODE1_STATUS") + "'", "STA1_CODE");
		result.setData("baza41", returnString); // 转归（西医主诊断）须填写出院情况代码(1治愈，2好转，3未愈，4死亡，9其他)--与出院方式相同不能确定
		result.setData("baza43", null == parm.getValue("QUYCHK_OI")
				|| parm.getValue("QUYCHK_OI").length() <= 0 ? "0" : "1"); // 门出诊断符合标志:须填写代码(0未做，1符合，2不符合，3不肯定)
																			// ----(1
																			// 有
																			// 0、2、3
																			// 无)
		result.setData("baza44", null == parm.getValue("QUYCHK_INOUT")
				|| parm.getValue("QUYCHK_INOUT").length() <= 0 ? "0" : "1"); // 入出诊断符合标志:须填写代码(0未做，1符合，2不符合，3不肯定)----(1
																				// 有
																				// 0、2、3
																				// 无)
		result.setData("bazac1", null == parm.getValue("QUYCHK_CLPA")
				|| parm.getValue("QUYCHK_CLPA").length() <= 0 ? "0" : "1"); // 临床与病理符合标志
																			// 须填写代码(0未做，1符合，2不符合，3不肯定)----(1
																			// 有
																			// 0、2、3
																			// 无)
		result.setData("bazac2", null == parm.getValue("QUYCHK_RAPA")
				|| parm.getValue("QUYCHK_RAPA").length() <= 0 ? "0" : "1"); // 放射与病理符合标志
																			// 须填写代码(0未做，1符合，2不符合，3不肯定)----(1
																			// 有
																			// 0、2、3
																			// 无)
		result.setData("bazac3", ""); // 手术为本院第一例 填写代码(1是，2否)----不确定
		// 查找血型填写代码
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_BLOOD' AND ID='" + parm.getValue("BLOOD_TYPE")
						+ "'", "STA1_CODE");
		result.setData("baza45", returnString); // 血型填写代码(0未知，1 A，2 B，3 AB，4 O，9
												// 其他)
		result.setData("bazac5", null == parm.getValue("RH_TYPE")
				|| parm.getValue("RH_TYPE").equals("3") ? "" : parm
				.getValue("RH_TYPE")); // RH 填写代码(1阴，2阳)
		result.setData("bazac6", null == parm.getValue("HBSAG") ? "0" : parm
				.getValue("HBSAG")); // HBsAg 填写代码(0未做，1阴性，2阳性)
		result.setData("bazac7", null == parm.getValue("HCV_AB") ? "0" : parm
				.getValue("HCV_AB")); // HCV-Ab 填写代码(0未做，1阴性，2阳性)
		result.setData("bazac8", null == parm.getValue("HIV_AB") ? "0" : parm
				.getValue("HIV_AB")); // HIV-Ab 填写代码(0未做，1阴性，2阳性)
		result.setData("bazac9", ""); // 出院诊断疑诊标志----不确定
		result.setData("bazaca", ""); // 治疗为本院第一例 填写代码(1是，2否)----不确定
		result.setData("bazacb", ""); // 检查为本院第一例 填写代码(1是，2否)----不确定
		result.setData("bazacc", resultName(parm.getValue("FIRST_CASE"))); // 诊断为本院第一例
																			// 填写代码(1是，2否)
		result.setData("baza46", resultName(parm.getValue("GET_TIMES"))); // 抢救次数
																			// 有抢救情况，填写不小于0的整数
		result.setData("baza47", resultName(parm.getValue("SUCCESS_TIMES"))); // 成功次数
																				// 同抢救次数
		result.setData("baza48", null == parm.getValue("ACCOMP_DATE")
				|| parm.getValue("ACCOMP_DATE").length() <= 0 ? "2" : "1"); // 随诊
																			// 填写代码(1是，2否)
		StringBuffer accompDate = new StringBuffer();
		accompDate.append(null == parm.getValue("ACCOMPANY_WEEK")
				|| parm.getValue("ACCOMPANY_WEEK").length() <= 0 ? "" : parm
				.getValue("ACCOMPANY_WEEK")
				+ "W");
		accompDate.append(null == parm.getValue("ACCOMPANY_MONTH")
				|| parm.getValue("ACCOMPANY_MONTH").length() <= 0 ? "" : ","
				+ parm.getValue("ACCOMPANY_MONTH") + "M");
		accompDate.append(null == parm.getValue("ACCOMPANY_YEAR")
				|| parm.getValue("ACCOMPANY_YEAR").length() <= 0 ? "" : ","
				+ parm.getValue("ACCOMPANY_YEAR") + "Y");
		result.setData("baza49", accompDate); // 随诊期限 填写如1W（1周），1M（1月），1Y（1年）
		result.setData("baza50", resultName(parm.getValue("SAMPLE_FLG"))); // 示教病例
																			// 填写代码(1是，2否)
		result.setData("baza51", resultName(parm.getValue("QUALITY"))); // 病案质量
																		// 填写代码(1甲，2乙，3丙)
		result.setData("baza52", null == parm.getValue("ALLEGIC")
				|| parm.getValue("ALLEGIC").length() <= 0 ? "2" : "1"); // 药物过敏标志
																		// 填写代码(1有，2无)
		result.setData("baza53", null == parm.getValue("OP_CODE")
				|| parm.getValue("OP_CODE").length() <= 0 ? "2" : "1"); // 手术标志
																		// 填写代码(1有，2无)
		result.setData("baza55", "2"); // 治疗类别 填写代码(1中，2西，3中西医)----不确定:写死'2'
		// 入院途经
		returnString = sqlReturnParm("ADM_INP", "CASE_NO ='"
				+ parm.getValue("CASE_NO") + "'", "ADM_SOURCE");
		result.setData("baza56", null != returnString ? returnString.substring(
				1, returnString.length()) : returnString); // 入院途经
															// 填写代码(1门诊，2急诊，3转院)
		result.setData("baza57", null == parm.getValue("TRANS_REACTION") ? "0"
				: parm.getValue("TRANS_REACTION")); // 输血反应 填写代码(0未输，1有，2无)
		// 查找编码员
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("ENCODER") + "'", "USER_NAME");
		result.setData("baza59", returnString); // 编码员 输入姓名
		// 查找输液反应
		// returnString=sqlReturnParm("PHL_ORDER","CASE_NO ='"+parm.getValue("CASE_NO")+"'","CASE_NO");
		result.setData("baza58", ""); // 输液反应
										// 填写代码(0未输，1有，2无)---有输液反应不能确定:现在设置NULL
		result.setData("baza61", null == parm.getValue("PATHOLOGY_DIAG")
				|| parm.getValue("PATHOLOGY_DIAG").length() <= 0 ? "2" : "1"); // 病理标志
																				// 填写代码(1有，2无)
		result.setData("baza62", ""); // 科研病例 填写代码(1是，2否)--不确定
		result.setData("baza63", ""); // 抢救方法 填写代码(1中，2西，3中西医)---不能确定:现在设置NULL
		result.setData("baza64", parm.getValue("IN_DIAG_CODE")); // 入院主诊断（ICD_10）
		result.setData("baza65", parm.getValue("OUT_DIAG_CODE1")); // 出院主诊断（ICD_10)

		// 查找质控护士
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("CTRL_NURSE") + "'", "USER_NAME");
		result.setData("baza69", returnString); // 质控护士 填写姓名
		result.setData("baza70", ""); // 自制中药制剂 填写代码（0未知，1有，2无）---不确定:现在设置NULL
		result.setData("baza71", ""); // 中医特色治疗
		result.setData("baza72", ""); // 非必要检查
		result.setData("baza73", ""); // 入院前经外院诊治 填写代码(1有，2无)---不确定
		result.setData("baza74", ""); // 住院期间病情：危重 填写代码(1是，2否)--不确定
		// 查找医疗保险号
		returnString = sqlReturnParm("SYS_PATINFO", "MR_NO='"
				+ parm.getValue("MR_NO") + "'", "NHI_NO");
		result.setData("baza81", returnString); // 医疗保险号 填写实际医疗保险号 关联医保表
		// 查找医疗付款方式
		returnString = sqlReturnParm("SYS_CTZ", "CTZ_CODE='"
				+ parm.getValue("CTZ1_CODE") + "'", "STA1_CODE");
		result.setData("baza82", returnString); // 医疗付款方式 须填写代码，参见病案首页说明

		String bilSql = "SELECT SUM(WRT_OFF_AMT) AS WRT_OFF_AMT,REXP_CODE FROM BIL_IBS_RECPM A,BIL_IBS_RECPD B WHERE A.RECEIPT_NO=B.RECEIPT_NO AND A.CASE_NO='"
				+ parm.getValue("CASE_NO") + "' GROUP BY REXP_CODE";

		TParm bilParm = new TParm(TJDODBTool.getInstance().select(bilSql));
		if (bilParm.getErrCode() < 0) {
			return bilParm;
		}
		if (bilParm.getCount("WRT_OFF_AMT") <= 0) {
			bilSql = "SELECT SUM(TOT_AMT) AS WRT_OFF_AMT,REXP_CODE FROM IBS_ORDD WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' GROUP BY REXP_CODE";
			bilParm = new TParm(TJDODBTool.getInstance().select(bilSql));
		}
		double sum = parm.getDouble("CHARGE_15") + parm.getDouble("CHARGE_02")
				+ parm.getDouble("CHARGE_01") + parm.getDouble("CHARGE_04")
				+ parm.getDouble("CHARGE_12") + parm.getDouble("CHARGE_13")
				+ parm.getDouble("CHARGE_06") + parm.getDouble("CHARGE_11")
				+ parm.getDouble("CHARGE_18") + parm.getDouble("CHARGE_14")
				+ parm.getDouble("CHARGE_10") + parm.getDouble("CHARGE_05")
				+ parm.getDouble("CHARGE_03") + parm.getDouble("CHARGE_07")
				+ parm.getDouble("CHARGE_08") + parm.getDouble("CHARGE_19")
				+ parm.getDouble("CHARGE_17") + parm.getDouble("CHARGE_16");
		result.setData("baza83", sum); // 住院总费用 填写实际费用（2位小数，单位‘元’），以下分项和=总费用
		result.setData("baza84", parm.getDouble("CHARGE_01")); // 床位费
		result.setData("baza85", parm.getDouble("CHARGE_18")); // 中成药费
		result.setData("baza86", parm.getDouble("CHARGE_17")+parm.getDouble("CHARGE_16")); // 西药费
		result.setData("baza87", parm.getDouble("CHARGE_04")); // 检查费
		result.setData("baza88", parm.getDouble("CHARGE_12")); // 血费
		result.setData("baza89", parm.getDouble("CHARGE_13")); // 氧费
		result.setData("baza90", parm.getDouble("CHARGE_06")); // 治疗费（西医院无）
		result.setData("baza91", parm.getDouble("CHARGE_11")); // 手术费
		result.setData("baza92", parm.getDouble("CHARGE_18")); // 接生费
		result.setData("baza93", parm.getDouble("CHARGE_14")); // 其他费
		result.setData("baza94", parm.getDouble("CHARGE_10")); // 放射费 CT费用
		result.setData("baza95", parm.getDouble("CHARGE_05")); // 化验费
		result.setData("baza96", parm.getDouble("CHARGE_03")); // 中草药费
		result.setData("baza97", parm.getDouble("CHARGE_07")); // 护理费
		result.setData("baza98", parm.getDouble("CHARGE_08")); // 诊疗费
		result.setData("baza99", 0.00); // 医保拒付金额 不确定
		result.setData("bazae2", 0.00); // 麻醉费 不确定
		result.setData("bazae3", parm.getDouble("CHARGE_19")); // 婴儿费
		result.setData("bazae4", parm.getDouble("CHARGE_17")); // 陪床费 ：监护费
		result.setData("bazae5", parm.getDouble("CHARGE_16")); // 其他费1:冷暖费
		result.setData("bazae6", 0.00); // 其他费2
		return result;
	}

	private String resultName(String name) {
		if (null == name)
			return "";
		else
			return name;
	}

	public static void appendLog(String fileName, String content) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, true);
			fw.write(content);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 根据得到的数据查找相关表中STA1_CODE 值
	 * 
	 * @param tableName
	 *            String 表名称
	 * @param whereName
	 *            String 条件
	 * @param resultName
	 *            String 返回列名称
	 * @return String
	 */
	private String sqlReturnParm(String tableName, String whereName,
			String resultName) {
		StringBuffer sql = new StringBuffer();
		if (null == tableName || tableName.length() <= 0) {
			return null;
		}
		if (null == resultName || resultName.length() <= 0) {
			return null;
		}

		sql.append("SELECT " + resultName + " FROM " + tableName);
		if (null != whereName && whereName.length() > 0) {
			sql.append(" WHERE " + whereName);
		}
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		return null != result.getValue(resultName, 0)
				&& result.getValue(resultName, 0).length() > 0 ? result
				.getValue(resultName, 0) : "";
	}

	/**
	 * 门诊科室工作日志登记参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm opdUpdateName(TParm parm) {
		TParm result = new TParm();
		result.setData("TJM2RQ", "");// 日期 须填写形如 2002-01-01
		result.setData("TJM2KB", "");// 科别代码
		result.setData("TJM2001", "");// 正副主任医师工时
		result.setData("TJM2002", "");// 主治医师工时
		result.setData("TJM2003", "");// 医师医士工时
		result.setData("TJM2004", "");// 进修实习人员工时
		result.setData("TJM2005", "");// 正副主任医师人数
		result.setData("TJM2006", "");// 主治医师人数
		result.setData("TJM2007", "");// 医师医士人数
		result.setData("TJM2008", "");// 进修实习人员数
		result.setData("TJM2009", "");// 门诊人次:按实际情况填写整数值
		result.setData("TJM2010", "");// 专科门诊人次
		result.setData("TJM2011", "");// 专家门诊人次
		result.setData("TJM2012", "");// 急诊人次
		result.setData("TJM2013", "");// 会诊人次
		result.setData("TJM2014", "");// 出诊人次
		result.setData("TJM2015", "");// 单项健康检查人数
		result.setData("TJM2016", "");// 门诊手术例数
		return result;
	}

	/**
	 * 住院病房工作日志登记表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm odiUpdateName(TParm parm) {
		TParm result = new TParm();
		result.setData("TJZ1RQ", "");// 日期 须填写形如 2002-01-01
		result.setData("TJZ1KB", "");// 科别代码
		result.setData("TJZ1001", "");// 实际开放床位数:按实际情况填写整数值
		result.setData("TJZ1002", "");// 原有人数
		result.setData("TJZ1003", "");// 入院人数
		result.setData("TJZ1004", "");// 他科转入
		result.setData("TJZ1005", "");// 出院人数
		result.setData("TJZ1007", "");// 治愈
		result.setData("TJZ1008", "");// 好转
		result.setData("TJZ1009", "");// 未愈
		result.setData("TJZ1010", "");// 死亡
		result.setData("TJZ1014", "");// 其他
		result.setData("TJZ1012", "");// 正常分娩
		result.setData("TJZ1013", "");// 计划生育
		result.setData("TJZ1015", "");// 转往他科
		result.setData("TJZ1016", "");// 留院人数
		result.setData("TJZ1017", "");// 出院者占用总床日数
		result.setData("TJZ1018", "");// 危重病人数
		result.setData("TJZ1019", "");// 陪伴人数
		result.setData("TJZ1020", "");// 24小时内死亡人数
		return result;
	}

	/**
	 * 出院次诊断信息表
	 */
	private void outDiagInfo(TParm parm) {
		TParm result = new TParm();
		result.setData("BAZA01", parm.getValue("MR_NO")); // 病案号 BAZA01 C 10
		result.setData("BAF102", parm.getValue("MR_NO"));
		// 出院次诊断顺序号 BAF102 N 1
		// 出院次诊断（病名码） BAF103 C 10 对应“gb17”中的DM字段
		// 出院次诊断（ICD_9） BAF104 C 5
		// 转归（次诊断） BAF105 C 1
		// 并症标志 BAF106 C 1
		// 感染部位 BAF107 C 1
		// 出院次诊断（ICD_10） BAF111 C 8 对应“gb17”中的DMMC字段

	}// OUT_DIAG_CODE6
}
