package jdo.mro;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jdo.adm.ADMChildImmunityTool;
import jdo.adm.ADMTransLogTool;
import jdo.emr.EMRCreateXMLTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 首页打印Tool
 * </p>
 * 
 * <p>
 * Description: 首页打印Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangk 2009-9-20
 * @version 4.0
 */
public class MROPrintTool extends TJDOTool {
	TDataStore ICD_DATA = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	TDataStore DICTIONARY;
	Map drList;
	/**
	 * 实例
	 */
	public static MROPrintTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static MROPrintTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROPrintTool();
		return instanceObject;
	}

	public MROPrintTool() {
		drList = getDrList();
		getDICTIONARY();
		// DICTIONARY.showDebug();
	}

	/**
	 * 获取首页打印信息（废除）
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm getMroRecordPrintData(String CASE_NO) {
		// 获取某一病患的首页信息
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		// 判断该病患是否是产妇
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// 获取新生儿的信息 显示在母亲的病案首页上
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// 查询新生儿的首页信息
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		// 查询手术信息
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		DecimalFormat df = new DecimalFormat("0.00");
		// 整理打印数据
		TParm data = new TParm();
		// 页眉信息
		data.setData("head_mr_no", "TEXT", print.getValue("MR_NO", 0));// MR_NO
		data.setData("head_ipd_no", "TEXT", print.getValue("IPD_NO", 0));// IPD_NO
		// data.setData("CTZ1","TEXT",getDesc("SYS_CTZ","","CTZ_DESC","CTZ_CODE",print.getValue("CTZ1_CODE",0)));//身份1//CTZ1
		// S.13
		/**
		 * add 20120113
		 */
		data.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// 病案首页身份
		data.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		data.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// 组织机构代码
		data.setData("NHI_NO", "TEXT", print.getValue("NHI_NO", 0));// 健康卡号 医保卡
		TParm sumParm = new TParm();
		sumParm.setData("CASE_NO", CASE_NO);
		sumParm.setData("ADM_TYPE", "I");
		data.setData("NB_ADM_WEIGHT", "TEXT", SUMNewArrivalTool.getInstance()
				.getNewAdmWeight(sumParm));// 新生儿入院体重
		data.setData("NB_WEIGHT", "TEXT", SUMNewArrivalTool.getInstance()
				.getNewBornWeight(sumParm));// 新生儿出生体重
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		data.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 7 ? birthplace.substring(0, 7)
						: birthplace);// //籍贯

		data.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// 通信地址
		// HR03.00.005
		// H.05
		data.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// 通信邮编

		data.setData("TEL", "TEXT", print.getValue("TEL", 0));// 通信邮编

		// 设置身份HR56.00.002.05 CDA值
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		data.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		data.setData("IN_COUNT", "TEXT", "第" + print.getValue("IN_COUNT", 0)
				+ "次住院");// 住院次数
		// 病患基本信息
		data.setData("HR02.01.001.01", "TEXT", print.getValue("PAT_NAME", 0));// 姓名
		data.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// 性别
																	// HR02.02.001
																	// H.03
		// 设置性别CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		data.setData("HR02.02.001", "TEXT", sexCDAValue);
		data.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy年MM月dd日"));// 出生日期
																		// HR30.00.001
																		// H.03
		data.setData("AGE", "TEXT", print.getValue("AGE", 0));// 年龄 HR02.03.001
																// H.03
		data.setData("MARRIGE", "TEXT", print.getValue("MARRIGE", 0));// 婚姻
																		// HR02.06.003
																		// H.03
		// 设置婚姻CDA代码
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		data.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		data.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// 职业 HR02.07.011.02 H.03
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		data.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 7 ? HOEMPLACE.substring(0, 7) : HOEMPLACE);// 出生地
																				// 代码需要转换
																				// HR30.00.005
																				// H.03
		data.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// 民族
																				// HR02.05.001
																				// H.03
		data.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// 国籍
																				// HR02.04.001
																				// H.03
		data.setData("IDNO", "TEXT", print.getValue("IDNO", 0));// 身份证
																// HR01.01.002.02
																// H.02
		data.setData("OFFICE", "TEXT", print.getValue("OFFICE", 0));// 工作单位
																	// HR02.07.006
																	// H.05
		data.setData("O_ADDRESS", "TEXT", print.getValue("O_ADDRESS", 0));// 单位地址
																			// HR03.00.003
																			// H.05
		data.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// 单位电话
																	// HR04.00.001.03
																	// H.06
		data.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// 单位邮编
																		// HR03.00.005
																		// H.05
		data.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// 户口地址
																			// HR03.00.004.02
																			// H.05
		data.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// 户口邮编
																		// HR03.00.005
																		// H.05
		// HR03.00.005
		// H.05
		data.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// 联系人姓名
																			// HR02.01.002
																			// H.02
		data.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// 联系人关系 HR02.18.004
		data.setData("CONT_ADDRESS", "TEXT", print.getValue("CONT_ADDRESS", 0));// 联系人地址
																				// HR03.00.004.02
																				// H.05
		data.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// 联系人电话
																		// HR04.00.001.03
																		// H.06
		data.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日 HH时"));// 入院日期
																		// HR00.00.001.06
																		// H.01
		data.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// 入院科别 HR21.01.100.05
														// H.08
		data.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
						print.getValue("IN_ROOM_NO", 0)));// 入院病房 --------无编码

		data.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(getTranDept(CASE_NO)));// 转科科别 wanglong
															// modify 20150115
															// HR42.03.100
															// H.10
		data.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日 HH时"));// 出院日期
																		// HR42.02.201
																		// H.10
		if (print.getData("OUT_DATE", 0) != null) {// 判断是否出院
			data.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // 出院科室 HR42.03.100
																// H.10
			data.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
							print.getValue("OUT_ROOM_NO", 0))); // 出院病室
		} else {
			data.setData("OUT_DEPT", "TEXT", "");
			data.setData("OUT_ROOM", "TEXT", "");
		}
		data.setData("	", "TEXT", print.getValue("REAL_STAY_DAYS", 0));// 实际住院天数
																		// HR52.02.103
																		// S.12
		data.setData("IN_CONDITION", "TEXT", print.getValue("IN_CONDITION", 0)
				.replace("0", ""));// 入院情况
		// 设置入院情况CDA代码
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		data.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		data.setData("CONFIRM_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CONFIRM_DATE", 0), "yyyy年MM月dd日"));// 确诊日期
																		// HR42.02.202
																		// S.11.002
																		// 实际要求以天数为单位
		/**
		 * add 20120113
		 */
		data.setData("ADM_SOURCE", "TEXT",
				getNewadmSource(print.getValue("ADM_SOURCE", 0)));// 病人来源
		data.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// 离院方式
		data.setData("TRAN_HOSP", "TEXT", print.getValue("TRAN_HOSP", 0));// 外转院区
		data.setData("BE_COMA_TIME", "TEXT", print.getValue("BE_COMA_TIME", 0)
				.substring(0, 2)
				+ "天"
				+ print.getValue("BE_COMA_TIME", 0).substring(2, 4)
				+ "小时"
				+ print.getValue("BE_COMA_TIME", 0).substring(4, 6) + "分钟");// 入院前昏迷时间
		data.setData("AF_COMA_TIME", "TEXT", print.getValue("AF_COMA_TIME", 0)
				.substring(0, 2)
				+ "天"
				+ print.getValue("AF_COMA_TIME", 0).substring(2, 4)
				+ "小时"
				+ print.getValue("AF_COMA_TIME", 0).substring(4, 6) + "分钟");// 入院后昏迷时间
		data.setData("VS_NURSE_CODE", "TEXT",
				print.getValue("VS_NURSE_CODE", 0));// 责任护士
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		data.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31天计划标记
		data.setData("AGN_PLAN_INTENTION", "TEXT",
				print.getValue("AGN_PLAN_INTENTION", 0));// 31天计划原因

		String OE_DIAG_CODE = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// 诊断代码：（疾病代码）HR55.02.057.05
																// S.07
																// （疾病名称）HR55.02.057.04
																// S.07
			OE_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		}
		if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
			OE_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		}
		data.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断
		String IN_DIAG_CODE = getICD_DESC(print.getValue("IN_DIAG_CODE", 0));
		if (print.getValue("IN_DIAG_CODE2", 0).length() > 0) {
			IN_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("IN_DIAG_CODE2", 0));
		}
		if (print.getValue("IN_DIAG_CODE3", 0).length() > 0) {
			IN_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("IN_DIAG_CODE3", 0));
		}
		data.setData("IN_DIAG_CODE", "TEXT", IN_DIAG_CODE);// 入院诊断
															// HR55.02.057.04
															// S.07
		/***** 出院诊断部分信息 *******************/
		// 如果是新生儿 那么填写新生儿诊断
		if (child.getCount("CASE_NO") > 0) {
			data.setData("C_OUT_DIAG_CODE1", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
			data.setData("C_OUT_DIAG_CODE2", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
			data.setData("C_OUT_DIAG_CODE3", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3

			data.setData("C_OUT1_" + childDiag.getValue("CODE1_STATUS", 0),
					"TEXT", "√"); // 出院主诊断 转归
			data.setData("C_OUT2_" + childDiag.getValue("CODE2_STATUS", 0),
					"TEXT", "√"); // 出院诊断2 转归
			data.setData("C_OUT3_" + childDiag.getValue("CODE3_STATUS", 0),
					"TEXT", "√"); // 出院诊断3 转归

			data.setData("C_OUT1_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断
			data.setData("C_OUT2_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2
			data.setData("C_OUT3_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3
		}
		data.setData("OUT_DIAG_CODE1", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
		data.setData("OUT_DIAG_CODE2", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
		data.setData("OUT_DIAG_CODE3", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3
		data.setData("OUT_DIAG_CODE4", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // 出院诊断4
		data.setData("OUT_DIAG_CODE5", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // 出院诊断5
		data.setData("OUT_DIAG_CODE6", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // 出院诊断6
		data.setData("OUT1_" + print.getValue("CODE1_STATUS", 0), "TEXT", "√"); // 出院主诊断
																				// 转归
		data.setData("OUT2_" + print.getValue("CODE2_STATUS", 0), "TEXT", "√"); // 出院诊断2
																				// 转归
		data.setData("OUT3_" + print.getValue("CODE3_STATUS", 0), "TEXT", "√"); // 出院诊断3
																				// 转归
		data.setData("OUT4_" + print.getValue("CODE4_STATUS", 0), "TEXT", "√"); // 出院诊断4
																				// 转归
		data.setData("OUT5_" + print.getValue("CODE5_STATUS", 0), "TEXT", "√"); // 出院诊断5
																				// 转归
		data.setData("OUT6_" + print.getValue("CODE6_STATUS", 0), "TEXT", "√"); // 出院诊断6
																				// 转归
		data.setData("OUT1_ICD", "TEXT", print.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断
		data.setData("OUT2_ICD", "TEXT", print.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2
		data.setData("OUT3_ICD", "TEXT", print.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3
		data.setData("OUT4_ICD", "TEXT", print.getValue("OUT_DIAG_CODE4", 0)); // 出院诊断4
		data.setData("OUT5_ICD", "TEXT", print.getValue("OUT_DIAG_CODE5", 0)); // 出院诊断5
		data.setData("OUT6_ICD", "TEXT", print.getValue("OUT_DIAG_CODE6", 0)); // 出院诊断6
		/****** 医师部分 *********/
		String INTE_DIAG_CODE = getICD_DESC(print.getValue("INTE_DIAG_CODE", 0));
		data.setData("INTE_DIAG_CODE", "TEXT",
				INTE_DIAG_CODE.length() == 0 ? "—" : INTE_DIAG_CODE);// 院内感染诊断
		data.setData("INTE_ICD", "TEXT", print.getValue("INTE_DIAG_CODE", 0));// 院内感染诊断CODE
		data.setData("INTE_STATUS" + print.getValue("INTE_DIAG_STATUS", 0),
				"TEXT", "√");// 院内感染转归
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		data.setData("PATHOLOGY_DIAG", "TEXT",
				PATHOLOGY_DIAG.length() == 0 ? "—" : PATHOLOGY_DIAG);// 病理诊断

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		data.setData("EX_RSN", "TEXT", EX_RSN.length() == 0 ? "—" : EX_RSN);// 损伤、中毒的外部因素
		data.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// 药物过敏
		data.setData("HBsAg", "TEXT", print.getValue("HBSAG", 0));// HBSAG
		// 设置HBsAgHRCDA代码
		String HBsAgHRCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HBSAG", 0));
		data.setData("HBsAgHR51.99.004.08", "TEXT", HBsAgHRCDAValue);
		data.setData("HCV-Ab", "TEXT", print.getValue("HCV_AB", 0));// HCV_AB
		// 设置HCV_ABCDA代码
		String HCV_ABCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HCV-Ab", 0));
		data.setData("HCV-AbHR51.99.004.08", "TEXT", HCV_ABCDAValue);
		data.setData("HIV-Ab", "TEXT", print.getValue("HIV_AB", 0));// HIV_AB
		// 设置HIV-AbCDA代码
		String HIV_AbCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HCV-Ab", 0));
		data.setData("HIV-AbHR51.99.004.08", "TEXT", HIV_AbCDAValue);
		data.setData("QUYCHK_OI", "TEXT", print.getValue("QUYCHK_OI", 0));// 门诊与住院
		// 设置门诊与住院代码
		String QUYCHK_OICDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_OI", 0));
		data.setData("QUYCHK_OIHR55.01.045.02", "TEXT", QUYCHK_OICDAValue);
		data.setData("QUYCHK_INOUT", "TEXT", print.getValue("QUYCHK_INOUT", 0));// 入院与出院
		// 设置入院与出院代码
		String QUYCHK_INOUTCDAValue = EMRCreateXMLTool.getInstance()
				.getCDACode("S.11.002", "HR55.01.045.02",
						print.getValue("QUYCHK_INOUT", 0));
		data.setData("QUYCHK_INOUTHR55.01.045.02", "TEXT", QUYCHK_INOUTCDAValue);
		data.setData("QUYCHK_OPBFAF", "TEXT",
				print.getValue("QUYCHK_OPBFAF", 0));// 术前术后
		// 设置术前与术后代码
		String QUYCHK_OPBFAFCDAValue = EMRCreateXMLTool.getInstance()
				.getCDACode("S.11.002", "HR55.01.045.02",
						print.getValue("QUYCHK_OPBFAF", 0));
		data.setData("QUYCHK_OPBFAFHR55.01.045.02", "TEXT",
				QUYCHK_OPBFAFCDAValue);
		data.setData("QUYCHK_CLPA", "TEXT", print.getValue("QUYCHK_CLPA", 0));// 临床与病理
		// 设置临床与病理
		String QUYCHK_CLPACDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_CLPA", 0));
		data.setData("QUYCHK_CLPAHR55.01.045.02", "TEXT", QUYCHK_CLPACDAValue);
		data.setData("QUYCHK_RAPA", "TEXT", print.getValue("QUYCHK_RAPA", 0));// 放射与病理
		// 设置放射与病理
		String QUYCHK_RAPACDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_RAPA", 0));
		data.setData("QUYCHK_RAPAHR55.01.045.02", "TEXT", QUYCHK_RAPACDAValue);
		data.setData("GET_TIMES", "TEXT", print.getValue("GET_TIMES", 0));// 抢救次数
		data.setData("SUCCESS_TIMES", "TEXT",
				print.getValue("SUCCESS_TIMES", 0));// 成功次数
		data.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// 科主任
		data.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// 主任医师
		data.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// 主治医师
		data.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// 住院医师
		data.setData("INDUCATION_DR_CODE", "TEXT",
				drList.get(print.getValue("INDUCATION_DR_CODE", 0)));// 进修医师
		data.setData("GRADUATE_INTERN_CODE", "TEXT",
				drList.get(print.getValue("GRADUATE_INTERN_CODE", 0)));// 研究生实习医师
		data.setData("INTERN_DR_CODE", "TEXT",
				drList.get(print.getValue("INTERN_DR_CODE", 0)));// 实习医师
		data.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// 编码员
		data.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// 病案质量
		// 设置病案质量
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		data.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		data.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// 质控医师
		data.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// 质控护士
		data.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy年MM月dd日"));// 质控日期
		/***** 费用部分 **********/
		data.setData("INFECT_REPORT", "TEXT",
				print.getValue("INFECT_REPORT", 0));// 传染病报告
		data.setData("DIS_REPORT", "TEXT", print.getValue("DIS_REPORT", 0));// 四病报告
		data.setData("BODY_CHECK", "TEXT", print.getValue("BODY_CHECK", 0));// 尸检
		data.setData("FIRST_CASE", "TEXT", print.getValue("FIRST_CASE", 0));// 首例
		// 根据随诊 年 月 周 判断是否随诊
		if (print.getValue("ACCOMPANY_WEEK", 0).length() > 0
				|| print.getValue("ACCOMPANY_MONTH", 0).length() > 0
				|| print.getValue("ACCOMPANY_YEAR", 0).length() > 0)
			data.setData("ACCOMPANY", "TEXT", "1");// 随诊
		else
			data.setData("ACCOMPANY", "TEXT", "2");
		data.setData(
				"ACCOMPANY_DATE",
				"TEXT",
				print.getValue("ACCOMPANY_WEEK", 0) + "周"
						+ print.getValue("ACCOMPANY_MONTH", 0) + "月"
						+ print.getValue("ACCOMPANY_YEAR", 0) + "年");// 随诊日期
		data.setData("SAMPLE_FLG", "TEXT", print.getValue("SAMPLE_FLG", 0));// 示教病例
		data.setData(
				"BLOOD_TYPE",
				"TEXT",
				print.getValue("BLOOD_TYPE", 0).length() == 0
						|| "6".equalsIgnoreCase(print.getValue("BLOOD_TYPE", 0)) ? "-"
						: print.getValue("BLOOD_TYPE", 0));// 血型
		// 设置血型
		String blockType = print.getValue("BLOOD_TYPE", 0).length() == 0
				|| "6".equalsIgnoreCase(print.getValue("BLOOD_TYPE", 0)) ? "-"
				: print.getValue("BLOOD_TYPE", 0);
		String BlockTypeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.02.001", "HR51.03.003", blockType);
		data.setData("HR51.03.003", "TEXT", BlockTypeCDAValue);
		// 设置RH
		String rhType = print.getValue("RH_TYPE", 0).length() == 0 ? "-"
				: print.getValue("RH_TYPE", 0);
		String rhTypeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.02.001", "HR51.03.004", rhType);
		data.setData("HR51.03.004", "TEXT", rhTypeCDAValue);
		data.setData(
				"RH_TYPE",
				"TEXT",
				print.getValue("RH_TYPE", 0).length() == 0 ? "-" : print
						.getValue("RH_TYPE", 0));// RH
		data.setData(
				"TRANS_REACTION",
				"TEXT",
				print.getValue("TRANS_REACTION", 0).length() == 0 ? "-" : print
						.getValue("TRANS_REACTION", 0));// 输血反应
		// 设置输血反应
		String transAction = print.getValue("TRANS_REACTION", 0).length() == 0 ? "-"
				: print.getValue("TRANS_REACTION", 0);
		String transActionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.09.002", "HR55.02.050", transAction);
		data.setData("HR55.02.050", "TEXT", transActionCDAValue);
		data.setData("RBC", "TEXT", print.getValue("RBC", 0));// 红血球
		data.setData("PLATE", "TEXT", print.getValue("PLATE", 0));// 血小板
		data.setData("PLASMA", "TEXT", print.getValue("PLASMA", 0));// 血浆
		data.setData("WHOLE_BLOOD", "TEXT", print.getValue("WHOLE_BLOOD", 0));// 全血
		data.setData("OTH_BLOOD", "TEXT", print.getValue("OTH_BLOOD", 0));// 其他
		data.setData(
				"SUMTOT",
				"TEXT",
				df.format(print.getDouble("CHARGE_01", 0)
						+ print.getDouble("CHARGE_02", 0)
						+ print.getDouble("CHARGE_03", 0)
						+ print.getDouble("CHARGE_04", 0)
						+ print.getDouble("CHARGE_05", 0)
						+ print.getDouble("CHARGE_06", 0)
						+ print.getDouble("CHARGE_07", 0)
						+ print.getDouble("CHARGE_08", 0)
						+ print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)
						+ print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)
						+ print.getDouble("CHARGE_14", 0)
						+ print.getDouble("CHARGE_15", 0)
						+ print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		data.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		data.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		data.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		data.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		data.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		data.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		data.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		data.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		data.setData("CHARGE_09", "TEXT",
				df.format(print.getDouble("CHARGE_09", 0)));
		data.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_10", 0)));
		data.setData("CHARGE_11", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		data.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		data.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_13", 0)));
		data.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		data.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		data.setData("CHARGE_16", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		data.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_17", 0)));
		data.setData("OP", getOP_DATA(op_date).getData());// 手术信息
		// 新生儿信息
		if (child.getCount("CASE_NO") > 0) {
			data.setData("Child_T", true);
			data.setData("C_SEX", "TEXT", childDiag.getValue("SEX", 0));
			data.setData("C_WEIGHT", "TEXT",
					this.getChildWeight(childDiag.getValue("CASE_NO", 0)));// 出生体重
			data.setData("APGAR", "TEXT", child_I.getValue("APGAR_NUMBER", 0));// APGAR评分
			// 婴儿卡介苗
			if (child_I.getBoolean("BABY_VACCINE_FLG", 0))
				data.setData("C_KJ", "TEXT", "1");
			else
				data.setData("C_KJ", "TEXT", "2");
			// 乙肝疫苗
			if (child_I.getBoolean("LIVER_VACCINE_FLG", 0))
				data.setData("C_YG", "TEXT", "1");
			else
				data.setData("C_YG", "TEXT", "2");
			// TSH
			if (child_I.getBoolean("TSH_FLG", 0))
				data.setData("C_TSH", "TEXT", "1");
			else
				data.setData("C_TSH", "TEXT", "2");
			// PKU_FLG
			if (child_I.getBoolean("PKU_FLG", 0))
				data.setData("C_PKU", "TEXT", "1");
			else
				data.setData("C_PKU", "TEXT", "2");

		} else {
			data.setData("Child_T", false);
		}
		return data;
	}

	/**
	 * 打印数据 新方法1
	 * 
	 * @param CASE_NO
	 * @return
	 */
	public TParm getNewMroRecordprintData(String CASE_NO) {
		TParm result = new TParm();
		// 获取某一病患的首页信息
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		boolean childrenFlg = false;// 新生儿注记
		childrenFlg = MRORecordTool.getInstance().getNewBornFlg(parm);

		// 判断该病患是否是产妇
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// 获取新生儿的信息 显示在母亲的病案首页上
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// 查询新生儿的首页信息
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		result.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// 组织机构代码
		// 设置身份HR56.00.002.05 CDA值
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		result.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		result.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// 病案首页身份
		result.setData("NHI_NO", "TEXT",
				this.getcheckStr(print.getValue("NHI_NO", 0)));// 健康卡号
		// 医保卡
		result.setData("MR_NO", "TEXT", print.getValue("MR_NO", 0));// 病案号
		result.setData("IPD_NO", "TEXT", print.getValue("IPD_NO", 0));// 住院号
		// 获取病患基本信息
		Pat pat = Pat.onQueryByMrNo(print.getValue("MR_NO", 0));
		result.setData("IN_COUNT", "TEXT", "第" + print.getInt("IN_COUNT", 0)
				+ "次住院");// 住院次数
		result.setData("PAT_NAME", "TEXT", print.getValue("PAT_NAME", 0));// 患者姓名
		// 病患基本信息
		result.setData("HR02.01.002", "TEXT", print.getValue("PAT_NAME", 0));// 姓名
		result.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// 性别
		// 设置性别CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		result.setData("HR02.02.001", "TEXT", sexCDAValue);
		result.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy年MM月dd日"));// 出生日期
		String[] res;
		res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
				print.getTimestamp("IN_DATE", 0));// 年龄
		// 年龄小于1周岁
		if (TypeTool.getInt(res[0]) < 1) {
			if (TypeTool.getInt(res[1]) >= 10)
				result.setData("MO", "TEXT", res[1]);// 整月数
			else
				result.setData("MO", "TEXT", " " + res[1]);// 整月数
			if (TypeTool.getInt(res[2]) >= 10)
				result.setData("CHDAY", "TEXT", res[2]);// 日数
			else
				result.setData("CHDAY", "TEXT", " " + res[2]);// 日数
			result.setData("CHCOUNT", "TEXT", "30");// 月的基数
			result.setData("AGE", "TEXT", "-");
		} else if (TypeTool.getInt(res[0]) >= 1) {
			result.setData("AGE", "TEXT", res[0] + "岁");// 整岁
			result.setData("MO", "TEXT", "-");
		}
		result.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// 国籍
		// 新生儿
		if (true) {
			parm.setData("ADM_TYPE", "I");
			String bornweight = "";// 出生体重
			String inweight = "";// 入院体重
			TParm bornWParm = SUMNewArrivalTool.getInstance().getNewBornWeight(
					parm);
			// System.out.println("------bornWParm---------------"+bornWParm);
			if (!bornWParm.getValue("NB_WEIGHT").equals(""))
				bornweight = bornWParm.getValue("NB_WEIGHT");
			else
				bornweight = "-";
			TParm inParm = SUMNewArrivalTool.getInstance()
					.getNewAdmWeight(parm);
			// System.out.println("------inParm---------------"+inParm);
			if (!inParm.getValue("NB_ADM_WEIGHT").equals(""))
				inweight = inParm.getValue("NB_ADM_WEIGHT");
			else
				inweight = "-";
			result.setData("NB_ADM_WEIGHT", "TEXT", inweight);// 新生儿入院体重
			result.setData("NB_WEIGHT", "TEXT", bornweight);// 新生儿出生体重
		}
		// modify by wangb 2017/3/28 调整出生地和籍贯的最大显示长度
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		result.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 16 ? HOEMPLACE.substring(0, 16)
						: HOEMPLACE);// 出生地
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		result.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 16 ? birthplace.substring(0, 16)
						: birthplace);// //籍贯
		result.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// 民族
		result.setData("IDNO", "TEXT",
				this.getcheckStr(print.getValue("IDNO", 0)));// 身份证
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		result.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// 职业
		result.setData("MARRIGE", "TEXT",
				this.getNewMarrige(print.getValue("MARRIGE", 0)));// 婚姻
		// 设置婚姻CDA代码
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		result.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		result.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// 通信地址
		result.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// 通信邮编
		result.setData("TEL", "TEXT", print.getValue("TEL", 0));// 通信电话
		String office = print.getValue("OFFICE", 0);// wanglong add 20150226
		if (!print.getValue("O_ADDRESS", 0).equals("")) {
			office += "(" + print.getValue("O_ADDRESS", 0) + ")";
		}
		result.setData("OFFICE", "TEXT", office);// 工作单位及地址
		result.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// 单位电话
		result.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// 单位邮编
		result.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// 户口地址
		result.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// 户口邮编
		result.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// 联系人姓名
		result.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// 联系人关系
		result.setData("CONT_ADDRESS", "TEXT",
				print.getValue("CONT_ADDRESS", 0));// 联系人地址
		result.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// 联系人电话
		/*---------------------------------基础信息部分-------------------------------------------------*/
		result.setData("ADM_SOURCE", "TEXT",
				this.getNewadmSource(print.getValue("ADM_SOURCE", 0)));// 入院途径
		result.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日 HH时"));// 入院日期
		result.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// 入院科别
		result.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
						print.getValue("IN_STATION", 0)));// 入院病房
		result.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(getTranDept(CASE_NO)));// 转科科别 wanglong
															// modify 20150115

		result.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日 HH时"));// 出院日期

		if (print.getData("OUT_DATE", 0) != null) {// 判断是否出院
			result.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // 出院科室
			result.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
							print.getValue("OUT_STATION", 0))); // 出院病室
		} else {
			result.setData("OUT_DEPT", "TEXT", "");
			result.setData("OUT_ROOM", "TEXT", "");
		}
		result.setData("REAL_STAY_DAYS", "TEXT",
				print.getValue("REAL_STAY_DAYS", 0));// 实际住院天数
		// 设置入院情况CDA代码
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		result.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		TParm diagnosis = new TParm(
				this.getDBTool()
						.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
								+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
								+ CASE_NO
								+ "' ORDER BY IO_TYPE ASC,MAIN_FLG DESC,SEQ_NO")); // 诊断记录
		
		int outindex = 2;
		String OE_DIAG_CODE = "";
		String OE_DIAG_DESC = "";
		boolean q = true;
		boolean r = true;
		boolean typeO= false;
		boolean flg =false;
		for (int j = 0; j < diagnosis.getCount(); j++) {		// 如果医生站只填写了院感诊断，没填写出院诊断的，把院感诊断的内容显示成出院诊断
			if (diagnosis.getValue("TYPE", j).equals("O")) {
				typeO=true;
					
			}
			if (diagnosis.getValue("TYPE", j).equals("Q")) {
				q =false;
			}
		}
		if(!q && !typeO){//有 Q 没有O
			flg =true;
		}
		
		for (int j = 0; j < diagnosis.getCount(); j++) {	//如果出院诊断和院感诊断都存在并且有相同的诊断编码, 去掉院感诊断

		if (diagnosis.getValue("TYPE", j).equals("Q")) {
			for (int i = 0; i < diagnosis.getCount(); i++) {
				if (diagnosis.getValue("TYPE", i).equals("O")) {
					if (diagnosis.getValue("CODE", j).equals(
							diagnosis.getValue("CODE", i))) {
						r = false;
						break;
					}
				}
				if(!r){
					break;
				}

			}
		}
		}
		if (diagnosis.getCount() > 0) {
			for (int j = 0; j < diagnosis.getCount(); j++) {
				if (diagnosis.getValue("TYPE", j).equals("I")) {

					if (OE_DIAG_DESC.length() > 0) {// 诊断代码：（疾病代码）
						OE_DIAG_DESC += "," + diagnosis.getValue("NAME", j);
					} else {
						OE_DIAG_DESC = diagnosis.getValue("NAME", j);
					}
					if (OE_DIAG_CODE.length() > 0) {
						OE_DIAG_CODE += "," + diagnosis.getValue("CODE", j);
					} else {
						OE_DIAG_CODE = diagnosis.getValue("CODE", j);
					}
					result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
					result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
				}
				if (diagnosis.getValue("TYPE", j).equals("O")) {
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // 出院主诊断疾病编码
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院主诊断疾病名称
						result.setData("DIAG_TYPE1", "TEXT", ""); // 出院主诊断疾病编码
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // 出院诊断疾病编码
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院诊断疾病名称
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"其他诊断:"); // 出院诊断疾病编码
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // 出院诊断疾病编码
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
						outindex++;
					}
				}

				

				if (flg) {
				if (diagnosis.getValue("TYPE", j).equals("Q")) {
					
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // 出院主诊断疾病编码
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院主诊断疾病名称
						result.setData("DIAG_TYPE1", "TEXT", ""); // 出院主诊断疾病编码
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // 出院诊断疾病编码
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院诊断疾病名称
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"其他诊断:"); // 出院诊断疾病编码
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // 出院诊断疾病编码
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
						outindex++;
					}
				}
				}else{
					if (r) {
						if (diagnosis.getValue("TYPE", j).equals("Q")) {
							result.setData("DIAG_CODE" + outindex, "TEXT",
									diagnosis.getValue("CODE", j)); // 感染诊断疾病编码
							result.setData(
									"DIAG" + outindex,
									"TEXT",
									diagnosis.getValue("NAME", j)
											+ addBrace(diagnosis.getValue("REMARK",
													j))); // 感染诊断疾病名称
							result.setData("DIAG_TYPE" + outindex, "TEXT", "");
							result.setData("DIAG_CONDITION" + outindex, "TEXT",
									diagnosis.getValue("IN_PAT_CONDITION", j)); // 感染诊断入院病情
							outindex++;
						}
					}
				}
				if (diagnosis.getValue("TYPE", j).equals("W")) {// 并发症诊断 add by
																// wanglong
																// 20140411
					result.setData("DIAG_CODE" + outindex, "TEXT",
							diagnosis.getValue("CODE", j)); // 疾病编码
					result.setData("DIAG" + outindex, "TEXT",
							"  " + diagnosis.getValue("NAME", j)
									+ addBrace(diagnosis.getValue("REMARK", j))); // 疾病名称
					// result.setData("DIAG_TYPE"+outindex, "TEXT","并发症诊断:"); //
					// 疾病编码
					result.setData("DIAG_TYPE" + outindex, "TEXT", "");
					result.setData("DIAG_CONDITION" + outindex, "TEXT",
							diagnosis.getValue("IN_PAT_CONDITION", j)); // 入院病情
					outindex++;
				}
			}
		}
		if (diagnosis.getErrCode() < 0) {
			System.out.println("查询MRO_RECORD_DIAG病案诊断数据错误！");
		}
		// shibl 20120618 modify
		// String OE_DIAG_DESC = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		// String OE_DIAG_CODE = print.getValue("OE_DIAG_CODE", 0);
		// if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// 诊断代码：（疾病代码）
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE2", 0);
		// }
		// if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE3", 0);
		// }
		// result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
		// result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
		// /*---------------------------------住院部分-----------------------------------------------------*/
		// result.setData("DIAG1", "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
		// // result.setData("DIAG2", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
		// // result.setData("DIAG3", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3
		// // result.setData("DIAG4", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // 出院诊断4
		// // result.setData("DIAG5", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // 出院诊断5
		// // result.setData("DIAG6", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // 出院诊断6
		// result.setData("DIAG_CODE1", "TEXT",
		// print.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断疾病编码
		// // result.setData("DIAG_CODE2", "TEXT",
		// // print.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2疾病编码
		// // result.setData("DIAG_CODE3", "TEXT",
		// // print.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3疾病编码
		// // result.setData("DIAG_CODE4", "TEXT",
		// // print.getValue("OUT_DIAG_CODE4", 0)); // 出院诊断4疾病编码
		// // result.setData("DIAG_CODE5", "TEXT",
		// // print.getValue("OUT_DIAG_CODE5", 0)); // 出院诊断5疾病编码
		// // result.setData("DIAG_CODE6", "TEXT",
		// // print.getValue("OUT_DIAG_CODE6", 0)); // 出院诊断6疾病编码
		// result.setData("DIAG_CONDITION1", "TEXT",
		// print.getValue("OUT_DIAG_CONDITION1", 0)); // 出院主诊断入院病情
		// // result.setData("DIAG_CONDITION2", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION2", 0)); // 出院诊断2入院病情
		// // result.setData("DIAG_CONDITION3", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION3", 0)); // 出院诊断3入院病情
		// // result.setData("DIAG_CONDITION4", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION4", 0)); // 出院诊断4入院病情
		// // result.setData("DIAG_CONDITION5", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION5", 0)); // 出院诊断5入院病情
		// // result.setData("DIAG_CONDITION6", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION6", 0)); // 出院诊断6入院病情
		// int seq = 2;
		// for (int i = 2; i < 7; i++) {
		// if (!print.getValue("OUT_DIAG_CODE" + i, 0).equals("")) {
		// result.setData("DIAG" + seq, "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE" + i, 0)));
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CODE" + i, 0));
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CONDITION" + i, 0));
		// if (seq == 2)
		// result.setData("DIAG_TYPE" + seq, "TEXT", "其他诊断:");
		// seq++;
		// }
		// }
		// String INTE_DIAG_CODE = getICD_DESC(print.getValue(
		// "INTE_DIAG_CODE", 0));
		// result.setData("DIAG" + seq, "TEXT", INTE_DIAG_CODE);// 院内感染诊断
		// // 待确定
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CODE", 0));// 院内感染诊断CODE
		// // 待确定
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CONDITION", 0));// 院内感染诊断入院病情
		// result.setData("DIAG_TYPE" + seq, "TEXT", "感染诊断:");
		/*----------------------------------------诊断表---------------------------------------------------*/
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		if (null != PATHOLOGY_DIAG && !"".equals(PATHOLOGY_DIAG))
			result.setData("PATHOLOGY_DIAG", "TEXT", PATHOLOGY_DIAG);// 病理诊断
		else
			result.setData("PATHOLOGY_DIAG", "TEXT", "-");// 病理诊断

		if (null != print.getValue("PATHOLOGY_DIAG", 0)
				&& !"".equals(print.getValue("PATHOLOGY_DIAG", 0)))
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT",
					print.getValue("PATHOLOGY_DIAG", 0));// 病理诊断疾病编码
		else
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT", "-");// 病理诊断疾病编码

		if (null != print.getValue("PATHOLOGY_NO", 0)
				&& !"".equals(print.getValue("PATHOLOGY_NO", 0)))
			result.setData("PATHOLOGY_NO", "TEXT",
					print.getValue("PATHOLOGY_NO", 0));// 病理号
		else
			result.setData("PATHOLOGY_NO", "TEXT", "-");// 病理号

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		if (null != EX_RSN && !"".equals(EX_RSN))
			result.setData("EX_RSN", "TEXT", EX_RSN);// 损伤、中毒的外部因素
		else
			result.setData("EX_RSN", "TEXT", "-");// 损伤、中毒的外部因素

		if (null != print.getValue("EX_RSN", 0)
				&& !"".equals(print.getValue("EX_RSN", 0)))
			result.setData("EX_RSN_CODE", "TEXT", print.getValue("EX_RSN", 0));// 损伤、中毒的外部因素疾病编码
		else
			result.setData("EX_RSN_CODE", "TEXT", "-");// 损伤、中毒的外部因素疾病编码

		result.setData("ALLEGIC_CODE", "TEXT",
				this.getcheckStr(print.getValue("ALLEGIC_FLG", 0)));// 是否有药物过敏
		result.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// 药物过敏
		result.setData("BODY_CHECK", "TEXT",
				this.getcheckStr(print.getValue("BODY_CHECK", 0)));// 尸检
		result.setData("BLOOD_TYPE", "TEXT", print.getValue("BLOOD_TYPE", 0));// 血型
		// 设置RH
		String rhType = print.getValue("RH_TYPE", 0);
		result.setData("RH_TYPE", "TEXT", rhType);// RH
		result.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// 科主任
		result.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// 主任医师
		result.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// 主治医师
		result.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// 住院医师
		result.setData("INDUCATION_DR_CODE", "TEXT",
				print.getValue("INDUCATION_DR_CODE", 0));// 进修医师 duzhw modify by
															// 20131204
		result.setData("VS_NURSE_CODE", "TEXT",
				drList.get(print.getValue("VS_NURSE_CODE", 0)));// 责任护士
		result.setData("INTERN_DR_CODE", "TEXT",
				print.getValue("INTERN_DR_CODE", 0));// 实习医师 duzhw modify by
														// 20131204
		result.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// 编码员
		result.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// 病案质量
		// 设置病案质量
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		result.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		result.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// 质控医师
		result.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// 质控护士
		result.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy年MM月dd日"));// 质控日期
		/*-------------------------------------------------------------------------------------------*/
		// 查询手术信息
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		// System.out.println("------op_date---------"+op_date);
		TParm anaParm = getOP_DATA(op_date);
		// System.out.println("-=-=------------------"+anaParm);
		int index = 2;
		for (int i = 0; i < op_date.getCount(); i++) {
			if (op_date.getValue("MAIN_FLG", i).equals("Y")) {
				result.setData("OPE_CODE1", "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE1", "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL1", "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC1", "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				} else {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR11", "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR21", "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL1", "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY1", "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR1", "TEXT", op_date.getValue("ANA_DR", i));// 麻醉师
			} else {
				result.setData("OPE_CODE" + index, "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE" + index, "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL" + index, "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC" + index, "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				} else {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR1" + index, "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR2" + index, "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL" + index, "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY" + index, "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR" + index, "TEXT",
						op_date.getValue("ANA_DR", i));// 麻醉师
				index++;
			}
		}
		result.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// 离院方式
		if (print.getValue("OUT_TYPE", 0).equals("2"))
			result.setData(
					"TRAN_HOSP1",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// 外转院区
																// 其他999999可以自定义
																// 20120918
																// shibl
		else
			result.setData("TRAN_HOSP1", "TEXT", "-");// wanglong add 20150226
		if (print.getValue("OUT_TYPE", 0).equals("3"))
			result.setData(
					"TRAN_HOSP2",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// 外转社区
																// 其他999999可以自定义
																// 20120918
																// shibl
		else
			result.setData("TRAN_HOSP2", "TEXT", "-");// wanglong add 20150226
		if (print.getValue("BE_COMA_TIME", 0).equals("")) {
			result.setData("BE_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院前昏迷时间
		} else {
			result.setData(
					"BE_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("BE_COMA_TIME", 0)
							.substring(0, 2))
							+ "天"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(2, 4))
							+ "小时"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(4, 6)) + "分钟");// 入院前昏迷时间
		}
		if (print.getValue("AF_COMA_TIME", 0).equals("")) {
			result.setData("AF_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院后昏迷时间
		} else {
			result.setData(
					"AF_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("AF_COMA_TIME", 0)
							.substring(0, 2))
							+ "天"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(2, 4))
							+ "小时"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(4, 6)) + "分钟");// 入院后昏迷时间
		}
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		result.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31天计划标记
		result.setData("AGN_PLAN_INTENTION", "TEXT",
				this.getcheckStr(print.getValue("AGN_PLAN_INTENTION", 0)));// 31天计划原因
		/*------------------------------------费用待确定------------------------------------------------*/
		DecimalFormat df = new DecimalFormat("0.00");
		result.setData("SUMTOT", "TEXT",
				df.format(print.getDouble("SUM_TOT", 0)));
		result.setData("OWN_TOT", "TEXT",
				df.format(print.getDouble("OWN_TOT", 0)));
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		// 一般医疗服务费
		result.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		// 一般治疗操作费
		result.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		// 护理费
		result.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		// 其他费用
		result.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		// 病理诊断费
		result.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		// 实验室诊断费
		result.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		// 影像学诊断费
		result.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		// 临床诊断项目费
		result.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		// 非手术治疗费用
		result.setData(
				"CHARGE_09",
				"TEXT",
				df.format(print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)));
		// 临床物理治疗费
		result.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_9", 0)));
		// 手术治疗费
		result.setData(
				"CHARGE_11",
				"TEXT",
				df.format(print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)));
		// 手术治疗费-麻醉费
		result.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		// 手术治疗费-手术费
		result.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		// 康复费
		result.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		// 中医治疗费
		result.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		// 西药费用
		result.setData(
				"CHARGE_16",
				"TEXT",
				df.format(print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		// 抗菌药物费用
		result.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		// 中成药费
		result.setData("CHARGE_18", "TEXT",
				df.format(print.getDouble("CHARGE_18", 0)));
		// 中草药费
		result.setData("CHARGE_19", "TEXT",
				df.format(print.getDouble("CHARGE_19", 0)));
		// 血费
		result.setData("CHARGE_20", "TEXT",
				df.format(print.getDouble("CHARGE_20", 0)));
		// 白蛋白类制品费
		result.setData("CHARGE_21", "TEXT",
				df.format(print.getDouble("CHARGE_21", 0)));
		// 球蛋白类制品费
		result.setData("CHARGE_22", "TEXT",
				df.format(print.getDouble("CHARGE_22", 0)));
		// 凝血因子类制品费
		result.setData("CHARGE_23", "TEXT",
				df.format(print.getDouble("CHARGE_23", 0)));
		// 细胞因子类制品费
		result.setData("CHARGE_24", "TEXT",
				df.format(print.getDouble("CHARGE_24", 0)));
		// 检查用一次性医用材料费
		result.setData("CHARGE_25", "TEXT",
				df.format(print.getDouble("CHARGE_25", 0)));
		// 治疗用一次性医用材料费
		result.setData("CHARGE_26", "TEXT",
				df.format(print.getDouble("CHARGE_26", 0)));
		// 手术用一次性医用材料费
		result.setData("CHARGE_27", "TEXT",
				df.format(print.getDouble("CHARGE_27", 0)));
		// 其他费
		result.setData("CHARGE_28", "TEXT",
				df.format(print.getDouble("CHARGE_28", 0)));

		// 2013-5-23 zhangh modify 增加重症监护表格和呼吸机使用时间
		result.setData("VENTI_TIME", "TEXT", print.getData("VENTI_TIME", 0));// 呼吸机使用时间
		// 重症监护表格数据
		for (int i = 1; i < 6; i++) {
			String inDate = "", icuInDate = "", outDate = "", icuOutDate = "";
			if (print.getData("ICU_IN_DATE" + i, 0) != null) {
				inDate = print
						.getData("ICU_IN_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_IN_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// 得到数据库中的进入时间
			}
			if (print.getData("ICU_OUT_DATE" + i, 0) != null) {
				outDate = print
						.getData("ICU_OUT_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_OUT_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// 得到数据库中的退出时间
			}
			icuInDate = getInOutDate(inDate);
			icuOutDate = getInOutDate(outDate);
			// 获取房间具体名称
			String deptCode = print.getValue("ICU_ROOM" + i, 0);
			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			result.setData("IN_DATE_" + i, "TEXT", icuInDate);
			result.setData("OUT_DATE_" + i, "TEXT", icuOutDate);
			result.setData("ICU_ROOM_" + i, "TEXT", deptDesc);
		}
		return result;
	}

	/**
	 * 打印数据 新方法2
	 * 
	 * @param CASE_NO
	 * @param realStayDays
	 * @return
	 */
	public TParm getNewMroRecordprintData(String CASE_NO, Object realStayDays) {
		TParm result = new TParm();
		// 获取某一病患的首页信息
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		boolean childrenFlg = false;// 新生儿注记
		childrenFlg = MRORecordTool.getInstance().getNewBornFlg(parm);

		// 判断该病患是否是产妇
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// 获取新生儿的信息 显示在母亲的病案首页上
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// 查询新生儿的首页信息
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// 医院名称
		result.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// 组织机构代码
		// 设置身份HR56.00.002.05 CDA值
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		result.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		result.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// 病案首页身份
		result.setData("NHI_NO", "TEXT",
				this.getcheckStr(print.getValue("NHI_NO", 0)));// 健康卡号
		// 医保卡
		result.setData("MR_NO", "TEXT", print.getValue("MR_NO", 0));// 病案号
		result.setData("IPD_NO", "TEXT", print.getValue("IPD_NO", 0));// 住院号
		// 获取病患基本信息
		Pat pat = Pat.onQueryByMrNo(print.getValue("MR_NO", 0));
		result.setData("IN_COUNT", "TEXT", "第" + print.getInt("IN_COUNT", 0)
				+ "次住院");// 住院次数
		result.setData("PAT_NAME", "TEXT", print.getValue("PAT_NAME", 0));// 患者姓名
		// 病患基本信息
		result.setData("HR02.01.002", "TEXT", print.getValue("PAT_NAME", 0));// 姓名
		result.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// 性别
		// 设置性别CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		result.setData("HR02.02.001", "TEXT", sexCDAValue);
		result.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy年MM月dd日"));// 出生日期
		String[] res;
		res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
				print.getTimestamp("IN_DATE", 0));// 年龄
		// 年龄小于1周岁
		if (TypeTool.getInt(res[0]) < 1) {
			if (TypeTool.getInt(res[1]) >= 10)
				result.setData("MO", "TEXT", res[1]);// 整月数
			else
				result.setData("MO", "TEXT", " " + res[1]);// 整月数
			if (TypeTool.getInt(res[2]) >= 10)
				result.setData("CHDAY", "TEXT", res[2]);// 日数
			else
				result.setData("CHDAY", "TEXT", " " + res[2]);// 日数
			result.setData("CHCOUNT", "TEXT", "30");// 月的基数
			result.setData("AGE", "TEXT", "-");
		} else if (TypeTool.getInt(res[0]) >= 1) {
			result.setData("AGE", "TEXT", res[0] + "岁");// 整岁
			result.setData("MO", "TEXT", "-");
		}
		result.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// 国籍
		// 新生儿
		if (true) {
			parm.setData("ADM_TYPE", "I");
			String bornweight = "";// 出生体重
			String inweight = "";// 入院体重
			TParm bornWParm = SUMNewArrivalTool.getInstance().getNewBornWeight(
					parm);
			// System.out.println("------bornWParm---------------"+bornWParm);
			if (!bornWParm.getValue("NB_WEIGHT").equals(""))
				bornweight = bornWParm.getValue("NB_WEIGHT");
			else
				bornweight = "-";
			TParm inParm = SUMNewArrivalTool.getInstance()
					.getNewAdmWeight(parm);
			// System.out.println("------inParm---------------"+inParm);
			if (!inParm.getValue("NB_ADM_WEIGHT").equals(""))
				inweight = inParm.getValue("NB_ADM_WEIGHT");
			else
				inweight = "-";
			result.setData("NB_ADM_WEIGHT", "TEXT", inweight);// 新生儿入院体重
			result.setData("NB_WEIGHT", "TEXT", bornweight);// 新生儿出生体重
		}
		// modify by wangb 2017/3/28 调整出生地和籍贯的最大显示长度
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		result.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 16 ? HOEMPLACE.substring(0, 16)
						: HOEMPLACE);// 出生地
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		result.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 16 ? birthplace.substring(0, 16)
						: birthplace);// //籍贯
		result.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// 民族
		result.setData("IDNO", "TEXT",
				this.getcheckStr(print.getValue("IDNO", 0)));// 身份证
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		result.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// 职业
		result.setData("MARRIGE", "TEXT",
				this.getNewMarrige(print.getValue("MARRIGE", 0)));// 婚姻
		// 设置婚姻CDA代码
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		result.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		result.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// 通信地址
		result.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// 通信邮编
		result.setData("TEL", "TEXT", print.getValue("TEL", 0));// 通信电话
		String office = print.getValue("OFFICE", 0);// wanglong add 20150226
		if (!print.getValue("O_ADDRESS", 0).equals("")) {
			office += "(" + print.getValue("O_ADDRESS", 0) + ")";
		}
		result.setData("OFFICE", "TEXT", office);// 工作单位及地址
		result.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// 单位电话
		result.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// 单位邮编
		result.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// 户口地址
		result.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// 户口邮编
		result.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// 联系人姓名
		result.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// 联系人关系
		result.setData("CONT_ADDRESS", "TEXT",
				print.getValue("CONT_ADDRESS", 0));// 联系人地址
		result.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// 联系人电话
		/*---------------------------------基础信息部分-------------------------------------------------*/
		result.setData("ADM_SOURCE", "TEXT",
				this.getNewadmSource(print.getValue("ADM_SOURCE", 0)));// 入院途径
		result.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日 HH时"));// 入院日期
		result.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// 入院科别
		result.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
						print.getValue("IN_STATION", 0)));// 入院病房
		result.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(getTranDept(CASE_NO)));// 转科科别 wanglong
															// modify 20150115
		result.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy年MM月dd日 HH时"));// 出院日期

		if (print.getData("OUT_DATE", 0) != null) {// 判断是否出院
			result.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // 出院科室
			result.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
							print.getValue("OUT_STATION", 0))); // 出院病室
		} else {
			result.setData("OUT_DEPT", "TEXT", "");
			result.setData("OUT_ROOM", "TEXT", "");
		}
		result.setData("REAL_STAY_DAYS", "TEXT", realStayDays);// 实际住院天数
		// 设置入院情况CDA代码
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		result.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		TParm diagnosis = new TParm(
				this.getDBTool()
						.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
								+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
								+ CASE_NO
								+ "' ORDER BY DECODE(IO_TYPE,'I','1','O','2','Q','3','W','4',IO_TYPE),MAIN_FLG DESC,SEQ_NO")); // 诊断记录
																																// modify
																																// by
		//	IO_TYPE: 		O出院诊断 		Q感染诊断																							// wanglong
																																// 20140411
		int outindex = 2;
		String OE_DIAG_CODE = "";
		String OE_DIAG_DESC = "";
		boolean q = true;
		boolean r = true;
		boolean typeO= false;
		boolean flg =false;
		for (int j = 0; j < diagnosis.getCount(); j++) {		// 如果医生站只填写了院感诊断，没填写出院诊断的，把院感诊断的内容显示成出院诊断
			if (diagnosis.getValue("TYPE", j).equals("O")) {
				typeO=true;
					
			}
			if (diagnosis.getValue("TYPE", j).equals("Q")) {
				q =false;
			}
		}
		if(!q && !typeO){//有 Q 没有O
			flg =true;
		}
		
		for (int j = 0; j < diagnosis.getCount(); j++) {	//如果出院诊断和院感诊断都存在并且有相同的诊断编码, 去掉院感诊断

		if (diagnosis.getValue("TYPE", j).equals("Q")) {
			for (int i = 0; i < diagnosis.getCount(); i++) {
				if (diagnosis.getValue("TYPE", i).equals("O")) {
					if (diagnosis.getValue("CODE", j).equals(
							diagnosis.getValue("CODE", i))) {
						r = false;
						break;
					}
				}
				if(!r){
					break;
				}

			}
		}
		}
		if (diagnosis.getCount() > 0) {
			for (int j = 0; j < diagnosis.getCount(); j++) {
				if (diagnosis.getValue("TYPE", j).equals("I")) {

					if (OE_DIAG_DESC.length() > 0) {// 诊断代码：（疾病代码）
						OE_DIAG_DESC += "," + diagnosis.getValue("NAME", j);
					} else {
						OE_DIAG_DESC = diagnosis.getValue("NAME", j);
					}
					if (OE_DIAG_CODE.length() > 0) {
						OE_DIAG_CODE += "," + diagnosis.getValue("CODE", j);
					} else {
						OE_DIAG_CODE = diagnosis.getValue("CODE", j);
					}
					result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
					result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
				}
				if (diagnosis.getValue("TYPE", j).equals("O")) {
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // 出院主诊断疾病编码
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院主诊断疾病名称
						result.setData("DIAG_TYPE1", "TEXT", ""); // 出院主诊断疾病编码
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // 出院诊断疾病编码
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院诊断疾病名称
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"其他诊断:"); // 出院诊断疾病编码
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // 出院诊断疾病编码
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
						outindex++;
					}
				}

				

				if (flg) {
				if (diagnosis.getValue("TYPE", j).equals("Q")) {
				
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // 出院主诊断疾病编码
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院主诊断疾病名称
						result.setData("DIAG_TYPE1", "TEXT", ""); // 出院主诊断疾病编码
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院主诊断入院病情
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // 出院诊断疾病编码
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // 出院诊断疾病名称
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"其他诊断:"); // 出院诊断疾病编码
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // 出院诊断疾病编码
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // 出院诊断入院病情
						outindex++;
					}
				}
				}else{
					if (r) {
						if (diagnosis.getValue("TYPE", j).equals("Q")) {
							result.setData("DIAG_CODE" + outindex, "TEXT",
									diagnosis.getValue("CODE", j)); // 感染诊断疾病编码
							result.setData(
									"DIAG" + outindex,
									"TEXT",
									diagnosis.getValue("NAME", j)
											+ addBrace(diagnosis.getValue("REMARK",
													j))); // 感染诊断疾病名称
							result.setData("DIAG_TYPE" + outindex, "TEXT", "");
							result.setData("DIAG_CONDITION" + outindex, "TEXT",
									diagnosis.getValue("IN_PAT_CONDITION", j)); // 感染诊断入院病情
							outindex++;
						}
					}
				}
				if (diagnosis.getValue("TYPE", j).equals("W")) {// 并发症诊断 add by
																// wanglong
																// 20140411
					result.setData("DIAG_CODE" + outindex, "TEXT",
							diagnosis.getValue("CODE", j)); // 疾病编码
					result.setData("DIAG" + outindex, "TEXT",
							"  " + diagnosis.getValue("NAME", j)
									+ addBrace(diagnosis.getValue("REMARK", j))); // 疾病名称
					// result.setData("DIAG_TYPE"+outindex, "TEXT","并发症诊断:"); //
					// 疾病编码
					result.setData("DIAG_TYPE" + outindex, "TEXT", "");
					result.setData("DIAG_CONDITION" + outindex, "TEXT",
							diagnosis.getValue("IN_PAT_CONDITION", j)); // 入院病情
					outindex++;
				}
			}
		}
		if (diagnosis.getErrCode() < 0) {
			System.out.println("查询MRO_RECORD_DIAG病案诊断数据错误！");
		}
		// ======================== wanglong add 20141223
		// 增加出院诊断附页（第14个之后的出院诊断放在附页中显示）
		for (int i = diagnosis.getCount() - 1; i >= 0; i--) {
			if (!diagnosis.getValue("TYPE", i).equals("O")
					&& !diagnosis.getValue("TYPE", i).equals("Q")
					&& !diagnosis.getValue("TYPE", i).equals("W")) {
				diagnosis.removeRow(i);
			}
		}
		if (diagnosis.getCount("CODE") > 14) {
			TParm addDiagTable = new TParm();
			for (int j = 14; j < diagnosis.getCount("CODE"); j++) {
				if (diagnosis.getValue("TYPE", j).equals("O")) {// 出院诊断
					addDiagTable.addData("DIAG_TYPE_" + (j % 2), "其他诊断:"); // 诊断类型
				} else if (diagnosis.getValue("TYPE", j).equals("Q")) { // 感染诊断
					addDiagTable.addData("DIAG_TYPE_" + (j % 2), "感染诊断:"); // 诊断类型
				} else if (diagnosis.getValue("TYPE", j).equals("W")) {// 并发症诊断
					addDiagTable.addData("DIAG_TYPE_" + (j % 2), "并发症诊断:"); // 诊断类型
				} else {
					continue;
				}
				addDiagTable.addData(
						"DIAG_DESC_" + (j % 2),
						diagnosis.getValue("NAME", j)
								+ addBrace(diagnosis.getValue("REMARK", j))); // 诊断名称
				addDiagTable.addData("DIAG_CODE_" + (j % 2),
						diagnosis.getValue("CODE", j)); // 疾病编码
				addDiagTable.addData("DIAG_CONDITION_" + (j % 2),
						diagnosis.getValue("IN_PAT_CONDITION", j)); // 入院病情
				addDiagTable.setData("TABLE_VALUE",
						addDiagTable.getCount("DIAG_TYPE_0") - 1,
						"#0.Bold=Y;#4.Bold=Y");
			}
			if (diagnosis.getCount() % 2 == 1) {
				addDiagTable.addData("DIAG_TYPE_1", ""); // 诊断类型
				addDiagTable.addData("DIAG_DESC_1", ""); // 诊断名称
				addDiagTable.addData("DIAG_CODE_1", ""); // 疾病编码
				addDiagTable.addData("DIAG_CONDITION_1", ""); // 入院病情
			}
			addDiagTable.setCount(addDiagTable.getCount("DIAG_TYPE_0"));
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_TYPE_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_DESC_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CODE_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CONDITION_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_TYPE_1");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_DESC_1");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CODE_1");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CONDITION_1");
			result.setData("ADD_DIAG_TAB", addDiagTable.getData());
			TParm addDiagTitle = new TParm();
			addDiagTitle.addData("COL1", " ");
			addDiagTitle.setCount(addDiagTitle.getCount("COL1"));
			addDiagTitle.addData("SYSTEM", "COLUMNS", "COL1");
			result.setData("ADD_DIAG_TITLE", addDiagTitle.getData());
			TParm addDiagTableTitle = new TParm();
			addDiagTableTitle.addData("COL1", " ");
			addDiagTableTitle.setCount(addDiagTableTitle.getCount("COL1"));
			addDiagTableTitle.addData("SYSTEM", "COLUMNS", "COL1");
			result.setData("ADD_DIAG_TABLE_TITLE", addDiagTableTitle.getData());
		}
		// ======================== add end
		// shibl 20120618 modify
		// String OE_DIAG_DESC = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		// String OE_DIAG_CODE = print.getValue("OE_DIAG_CODE", 0);
		// if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// 诊断代码：（疾病代码）
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE2", 0);
		// }
		// if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE3", 0);
		// }
		// result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// 门急诊诊断
		// result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// 门急诊诊断疾病编码
		// /*---------------------------------住院部分-----------------------------------------------------*/
		// result.setData("DIAG1", "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // 出院主诊断
		// // result.setData("DIAG2", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // 出院诊断2
		// // result.setData("DIAG3", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // 出院诊断3
		// // result.setData("DIAG4", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // 出院诊断4
		// // result.setData("DIAG5", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // 出院诊断5
		// // result.setData("DIAG6", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // 出院诊断6
		// result.setData("DIAG_CODE1", "TEXT",
		// print.getValue("OUT_DIAG_CODE1", 0)); // 出院主诊断疾病编码
		// // result.setData("DIAG_CODE2", "TEXT",
		// // print.getValue("OUT_DIAG_CODE2", 0)); // 出院诊断2疾病编码
		// // result.setData("DIAG_CODE3", "TEXT",
		// // print.getValue("OUT_DIAG_CODE3", 0)); // 出院诊断3疾病编码
		// // result.setData("DIAG_CODE4", "TEXT",
		// // print.getValue("OUT_DIAG_CODE4", 0)); // 出院诊断4疾病编码
		// // result.setData("DIAG_CODE5", "TEXT",
		// // print.getValue("OUT_DIAG_CODE5", 0)); // 出院诊断5疾病编码
		// // result.setData("DIAG_CODE6", "TEXT",
		// // print.getValue("OUT_DIAG_CODE6", 0)); // 出院诊断6疾病编码
		// result.setData("DIAG_CONDITION1", "TEXT",
		// print.getValue("OUT_DIAG_CONDITION1", 0)); // 出院主诊断入院病情
		// // result.setData("DIAG_CONDITION2", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION2", 0)); // 出院诊断2入院病情
		// // result.setData("DIAG_CONDITION3", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION3", 0)); // 出院诊断3入院病情
		// // result.setData("DIAG_CONDITION4", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION4", 0)); // 出院诊断4入院病情
		// // result.setData("DIAG_CONDITION5", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION5", 0)); // 出院诊断5入院病情
		// // result.setData("DIAG_CONDITION6", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION6", 0)); // 出院诊断6入院病情
		// int seq = 2;
		// for (int i = 2; i < 7; i++) {
		// if (!print.getValue("OUT_DIAG_CODE" + i, 0).equals("")) {
		// result.setData("DIAG" + seq, "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE" + i, 0)));
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CODE" + i, 0));
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CONDITION" + i, 0));
		// if (seq == 2)
		// result.setData("DIAG_TYPE" + seq, "TEXT", "其他诊断:");
		// seq++;
		// }
		// }
		// String INTE_DIAG_CODE = getICD_DESC(print.getValue(
		// "INTE_DIAG_CODE", 0));
		// result.setData("DIAG" + seq, "TEXT", INTE_DIAG_CODE);// 院内感染诊断
		// // 待确定
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CODE", 0));// 院内感染诊断CODE
		// // 待确定
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CONDITION", 0));// 院内感染诊断入院病情
		// result.setData("DIAG_TYPE" + seq, "TEXT", "感染诊断:");
		/*----------------------------------------诊断表---------------------------------------------------*/
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		if (null != PATHOLOGY_DIAG && !"".equals(PATHOLOGY_DIAG))
			result.setData("PATHOLOGY_DIAG", "TEXT", PATHOLOGY_DIAG);// 病理诊断
		else
			result.setData("PATHOLOGY_DIAG", "TEXT", "-");// 病理诊断

		if (null != print.getValue("PATHOLOGY_DIAG", 0)
				&& !"".equals(print.getValue("PATHOLOGY_DIAG", 0)))
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT",
					print.getValue("PATHOLOGY_DIAG", 0));// 病理诊断疾病编码
		else
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT", "-");// 病理诊断疾病编码

		if (null != print.getValue("PATHOLOGY_NO", 0)
				&& !"".equals(print.getValue("PATHOLOGY_NO", 0)))
			result.setData("PATHOLOGY_NO", "TEXT",
					print.getValue("PATHOLOGY_NO", 0));// 病理号
		else
			result.setData("PATHOLOGY_NO", "TEXT", "-");// 病理号

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		if (null != EX_RSN && !"".equals(EX_RSN))
			result.setData("EX_RSN", "TEXT", EX_RSN);// 损伤、中毒的外部因素
		else
			result.setData("EX_RSN", "TEXT", "-");// 损伤、中毒的外部因素

		if (null != print.getValue("EX_RSN", 0)
				&& !"".equals(print.getValue("EX_RSN", 0)))
			result.setData("EX_RSN_CODE", "TEXT", print.getValue("EX_RSN", 0));// 损伤、中毒的外部因素疾病编码
		else
			result.setData("EX_RSN_CODE", "TEXT", "-");// 损伤、中毒的外部因素疾病编码

		result.setData("ALLEGIC_CODE", "TEXT",
				this.getcheckStr(print.getValue("ALLEGIC_FLG", 0)));// 是否有药物过敏
		result.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// 药物过敏
		result.setData("BODY_CHECK", "TEXT",
				this.getcheckStr(print.getValue("BODY_CHECK", 0)));// 尸检
		result.setData("BLOOD_TYPE", "TEXT", print.getValue("BLOOD_TYPE", 0));// 血型
		// 设置RH
		String rhType = print.getValue("RH_TYPE", 0);
		result.setData("RH_TYPE", "TEXT", rhType);// RH
		result.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// 科主任
		result.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// 主任医师
		result.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// 主治医师
		result.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// 住院医师
		result.setData("INDUCATION_DR_CODE", "TEXT",
				print.getValue("INDUCATION_DR_CODE", 0));// 进修医师 duzhw modify by
															// 20131204
		result.setData("VS_NURSE_CODE", "TEXT",
				drList.get(print.getValue("VS_NURSE_CODE", 0)));// 责任护士
		result.setData("INTERN_DR_CODE", "TEXT",
				print.getValue("INTERN_DR_CODE", 0));// 实习医师 duzhw modify by
														// 20131204
		result.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// 编码员
		result.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// 病案质量
		// 设置病案质量
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		result.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		result.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// 质控医师
		result.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// 质控护士
		result.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy年MM月dd日"));// 质控日期
		/*-------------------------------------------------------------------------------------------*/
		// 查询手术信息
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		// System.out.println("------op_date---------"+op_date);
		TParm anaParm = getOP_DATA(op_date);
		// System.out.println("-=-=------------------"+anaParm);
		int index = 2;
		for (int i = 0; i < op_date.getCount(); i++) {
			if (op_date.getValue("MAIN_FLG", i).equals("Y")) {
				result.setData("OPE_CODE1", "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE1", "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL1", "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC1", "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				} else {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR11", "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR21", "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL1", "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY1", "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR1", "TEXT", op_date.getValue("ANA_DR", i));// 麻醉师
			} else {
				result.setData("OPE_CODE" + index, "TEXT",
						op_date.getValue("OP_CODE", i));// 手术编码
				result.setData("OPE_DATE" + index, "TEXT",
						op_date.getValue("OP_DATE", i));// 手术日期
				result.setData("OPE_LEVEL" + index, "TEXT",
						op_date.getValue("OP_LEVEL", i));// 手术级别
				result.setData("OPE_DESC" + index, "TEXT",
						op_date.getValue("OP_DESC", i));// 手术名称
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// 术者
				} else {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// 术者
				}
				result.setData("AST_DR1" + index, "TEXT",
						op_date.getValue("AST_DR1", i));// 助手1
				result.setData("AST_DR2" + index, "TEXT",
						op_date.getValue("AST_DR2", i));// 助手2
				result.setData("HEL" + index, "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// 愈合等级
				result.setData("ANA_WAY" + index, "TEXT",
						anaParm.getValue("ANA_WAY", i));// 麻醉方式
				result.setData("ANA_DR" + index, "TEXT",
						op_date.getValue("ANA_DR", i));// 麻醉师
				index++;
			}
		}
		result.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// 离院方式
		if (print.getValue("OUT_TYPE", 0).equals("2"))
			result.setData(
					"TRAN_HOSP1",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// 外转院区
																// 其他999999可以自定义
																// 20120918
																// shibl
		if (print.getValue("OUT_TYPE", 0).equals("3"))
			result.setData(
					"TRAN_HOSP2",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// 外转社区
																// 其他999999可以自定义
																// 20120918
																// shibl
		if (print.getValue("BE_COMA_TIME", 0).equals("")) {
			result.setData("BE_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院前昏迷时间
		} else {
			result.setData(
					"BE_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("BE_COMA_TIME", 0)
							.substring(0, 2))
							+ "天"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(2, 4))
							+ "小时"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(4, 6)) + "分钟");// 入院前昏迷时间
		}
		if (print.getValue("AF_COMA_TIME", 0).equals("")) {
			result.setData("AF_COMA_TIME", "TEXT", "-" + "天" + "-" + "小时" + "-"
					+ "分钟");// 入院后昏迷时间
		} else {
			result.setData(
					"AF_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("AF_COMA_TIME", 0)
							.substring(0, 2))
							+ "天"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(2, 4))
							+ "小时"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(4, 6)) + "分钟");// 入院后昏迷时间
		}
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		result.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31天计划标记
		result.setData("AGN_PLAN_INTENTION", "TEXT",
				this.getcheckStr(print.getValue("AGN_PLAN_INTENTION", 0)));// 31天计划原因
		/*------------------------------------费用待确定------------------------------------------------*/
		DecimalFormat df = new DecimalFormat("0.00");
		result.setData("SUMTOT", "TEXT",
				df.format(print.getDouble("SUM_TOT", 0)));
		result.setData("OWN_TOT", "TEXT",
				df.format(print.getDouble("OWN_TOT", 0)));
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		// 一般医疗服务费
		result.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		// 一般治疗操作费
		result.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		// 护理费
		result.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		// 其他费用
		result.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		// 病理诊断费
		result.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		// 实验室诊断费
		result.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		// 影像学诊断费
		result.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		// 临床诊断项目费
		result.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		// 非手术治疗费用
		result.setData(
				"CHARGE_09",
				"TEXT",
				df.format(print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)));
		// 临床物理治疗费
		result.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_9", 0)));
		// 手术治疗费
		result.setData(
				"CHARGE_11",
				"TEXT",
				df.format(print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)));
		// 手术治疗费-麻醉费
		result.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		// 手术治疗费-手术费
		result.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		// 康复费
		result.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		// 中医治疗费
		result.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		// 西药费用
		result.setData(
				"CHARGE_16",
				"TEXT",
				df.format(print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		// 抗菌药物费用
		result.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		// 中成药费
		result.setData("CHARGE_18", "TEXT",
				df.format(print.getDouble("CHARGE_18", 0)));
		// 中草药费
		result.setData("CHARGE_19", "TEXT",
				df.format(print.getDouble("CHARGE_19", 0)));
		// 血费
		result.setData("CHARGE_20", "TEXT",
				df.format(print.getDouble("CHARGE_20", 0)));
		// 白蛋白类制品费
		result.setData("CHARGE_21", "TEXT",
				df.format(print.getDouble("CHARGE_21", 0)));
		// 球蛋白类制品费
		result.setData("CHARGE_22", "TEXT",
				df.format(print.getDouble("CHARGE_22", 0)));
		// 凝血因子类制品费
		result.setData("CHARGE_23", "TEXT",
				df.format(print.getDouble("CHARGE_23", 0)));
		// 细胞因子类制品费
		result.setData("CHARGE_24", "TEXT",
				df.format(print.getDouble("CHARGE_24", 0)));
		// 检查用一次性医用材料费
		result.setData("CHARGE_25", "TEXT",
				df.format(print.getDouble("CHARGE_25", 0)));
		// 治疗用一次性医用材料费
		result.setData("CHARGE_26", "TEXT",
				df.format(print.getDouble("CHARGE_26", 0)));
		// 手术用一次性医用材料费
		result.setData("CHARGE_27", "TEXT",
				df.format(print.getDouble("CHARGE_27", 0)));
		// 其他费
		result.setData("CHARGE_28", "TEXT",
				df.format(print.getDouble("CHARGE_28", 0)));

		// 2013-5-23 zhangh modify 增加重症监护表格和呼吸机使用时间
		result.setData("VENTI_TIME", "TEXT", print.getData("VENTI_TIME", 0));// 呼吸机使用时间

		// 重症监护表格数据
		for (int i = 1; i < 6; i++) {
			String inDate = "", icuInDate = "", outDate = "", icuOutDate = "";
			if (print.getData("ICU_IN_DATE" + i, 0) != null) {
				inDate = print
						.getData("ICU_IN_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_IN_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// 得到数据库中的进入时间
			}
			if (print.getData("ICU_OUT_DATE" + i, 0) != null) {
				outDate = print
						.getData("ICU_OUT_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_OUT_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// 得到数据库中的退出时间
			}
			icuInDate = getInOutDate(inDate);
			icuOutDate = getInOutDate(outDate);
			// 获取房间具体名称
			String deptCode = print.getValue("ICU_ROOM" + i, 0);
			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			result.setData("IN_DATE_" + i, "TEXT", icuInDate);
			result.setData("OUT_DATE_" + i, "TEXT", icuOutDate);
			result.setData("ICU_ROOM_" + i, "TEXT", deptDesc);
		}

		// wanglong add 20141103 将空字符串变为-
		Map b = (Map) result.getData();
		Set c = b.keySet();
		for (Object object : c) {
			if (!object.equals(TParm.DEFAULT_GROUP)) {
				if (object.toString().indexOf("DIAG_TYPE_") != -1
						|| object.toString().indexOf("DIAG_DESC_") != -1
						|| object.toString().indexOf("DIAG_CODE_") != -1
						|| object.toString().indexOf("DIAG_CONDITION_") != -1
						|| object.toString().indexOf("IN_DATE_") != -1
						|| object.toString().indexOf("OUT_DATE_") != -1
						|| object.toString().indexOf("ICU_ROOM_") != -1
						|| object.toString().matches("DIAG_CODE[0-9]+")
						|| object.toString().matches("DIAG[0-9]+")
						|| object.toString().matches("DIAG_TYPE[0-9]+")
						|| object.toString().matches("DIAG_CONDITION[0-9]+")) {// wanglong
																				// add
																				// 20150227
					continue;
				}
				if (result.getValue(object.toString(), "TEXT").trim()
						.equals("")) {
					result.setData(object.toString(), "TEXT", "-");
				}
			} else {
				String[] name = result.getNames();
				for (String str : name) {
					if (str.indexOf("DIAG_TYPE_") != -1
							|| str.indexOf("DIAG_DESC_") != -1
							|| str.indexOf("DIAG_CODE_") != -1
							|| str.indexOf("DIAG_CONDITION_") != -1
							|| str.indexOf("IN_DATE_") != -1
							|| str.indexOf("OUT_DATE_") != -1
							|| str.indexOf("ICU_ROOM_") != -1
							|| object.toString().matches("DIAG_CODE[0-9]+")
							|| object.toString().matches("DIAG[0-9]+")
							|| object.toString().matches("DIAG_TYPE[0-9]+")
							|| object.toString()
									.matches("DIAG_CONDITION[0-9]+")) {// wanglong
																		// add
																		// 20150227
						continue;
					}
					if (result.getValue(str).trim().equals("")) {
						result.setData(str, "-");
					}
				}
			}
		}
		return result;
	}

	private String getInOutDate(String date) {
		String icuDate = "", hour = "";
		if (date != null && date.length() >= 10) {
			String[] inDateParts = date.split(" ");
			String[] inYmd = inDateParts[0].split("/");
			inYmd[0] += "年";
			inYmd[1] += "月";
			inYmd[2] += "日";
			if (date.length() > 10) {
				String[] inHms = inDateParts[1].split(":");
				hour = inHms[0] + "时";
			}
			for (String string : inYmd) {
				icuDate += string;
			}
			icuDate += hour;
		}
		return icuDate;
	}

	/**
	 * 替换中文
	 * 
	 * @param TableName
	 *            String 表名
	 * @param groupID
	 *            String 组名
	 * @param descColunm
	 *            String 中文列名
	 * @param codeColunm
	 *            String code列名
	 * @param code
	 *            String 代码
	 * @return String
	 */
	public String getAnaMayDesc(String TableName, String groupID,
			String descColunm, String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + descColunm + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (descColunm.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code.trim() // +
																		// " "//delete
																		// by
																		// wanglong
																		// 20120921
																		// 最新库中，字符串最后不带空格
						+ "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code.trim() // +
																		// " "//delete
																		// by
																		// wanglong
																		// 20120921
																		// 最新库中，字符串最后不带空格
						+ "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(descColunm, 0);
	}

	/**
	 * 替换中文
	 * 
	 * @param TableName
	 *            String 表名
	 * @param groupID
	 *            String 组名
	 * @param descColunm
	 *            String 中文列名
	 * @param codeColunm
	 *            String code列名
	 * @param code
	 *            String 代码
	 * @return String
	 */
	public String getDesc(String TableName, String groupID, String descColunm,
			String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + descColunm + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (descColunm.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code + "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code + "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(descColunm, 0);
	}

	String filterICD;

	/**
	 * 获取诊断中文
	 * 
	 * @param ICD
	 *            String 诊断码
	 * @return String
	 */
	private String getICD_DESC(String ICD) {
		filterICD = ICD;
		ICD_DATA.filterObject(this, "filterICD");
		return ICD_DATA.getItemString(0, "ICD_CHN_DESC");
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filterICD(TParm parm, int row) {
		return filterICD.equals(parm.getValue("ICD_CODE", row));
	}

	/**
	 * 获取新生儿 出生体重 查询新生儿体温单信息表
	 * 
	 * @param CASE_NO
	 *            String
	 * @return String
	 */
	public String getChildWeight(String CASE_NO) {
		String sql = MROSqlTool.getInstance().getChildWeightSQL(CASE_NO);
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return "";
		}
		return result.getValue("BORNWEIGHT", 0);
	}

	/**
	 * 整理手术信息
	 * 
	 * @param opParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getOP_DATA(TParm opParm) {
		// 循环替换麻醉方式为中文
		for (int i = 0; i < opParm.getCount(); i++) {
			// System.out.println("1-------------"+opParm.getValue("ANA_WAY",
			// i));
			String OP_DESC = this.getAnaMayDesc("SYS_DICTIONARY",
					"OPE_ANAMETHOD", "CHN_DESC", "ID",
					opParm.getValue("ANA_WAY", i));
			// System.out.println("2-------------"+OP_DESC);
			opParm.setData("ANA_WAY", i, OP_DESC);
		}
		return opParm;
	}

	/**
	 * 获取医师姓名列表
	 * 
	 * @return Map
	 */
	private Map getDrList() {
		String sql = "SELECT USER_ID,USER_NAME FROM SYS_OPERATOR";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Map list = new HashMap();
		for (int i = 0; i < result.getCount(); i++) {
			list.put(result.getValue("USER_ID", i),
					result.getValue("USER_NAME", i));
		}
		return list;
	}

	/**
	 * 获取大字典数据
	 */
	private void getDICTIONARY() {
		if (DICTIONARY == null) {
			String sql = "SELECT GROUP_ID,ID,CHN_DESC FROM SYS_DICTIONARY ";
			DICTIONARY = new TDataStore();
			DICTIONARY.setSQL(sql);
			DICTIONARY.retrieve();
		}
	}

	String DictionaryID;
	String DictionaryGroup;

	/**
	 * 获取大字典中的中文
	 * 
	 * @param groupId
	 *            String
	 * @param Id
	 *            String
	 * @return String
	 */
	private String getDictionaryDesc(String groupId, String Id) {
		if (DICTIONARY == null) {
			return "";
		}
		DictionaryGroup = groupId;
		DictionaryID = Id;
		DICTIONARY.filterObject(this, "filterDictionary");
		return DICTIONARY.getItemString(0, "CHN_DESC");
	}

	/**
	 * 过滤方法
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filterDictionary(TParm parm, int row) {
		return DictionaryGroup.equals(parm.getValue("GROUP_ID", row))
				&& DictionaryID.equals(parm.getValue("ID", row));
	}

	/**
	 * 转换连接符
	 * 
	 * @param str
	 * @return
	 */
	private String getLineTrandept(String str) {
		String line = "";
		String regex = "|";
		if (str.indexOf(regex) != -1) {
			String[] dept = str.split("[|]");
			for (int i = 0; i < dept.length; i++) {
				if (line.length() > 0) {
					line += "->";
				}
				line += getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						dept[i]);
			}
		} else {
			line = getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE", str);
		}
		return line;
	}

	/**
	 * 入院情况 转换成国家规定的编码 1.急诊 2.门诊 3.其他医疗机构转入 9.其他
	 * 
	 * @param id
	 * @return
	 */
	private String getNewadmSource(String id) {
		String code = "";
		if (id.equals("01"))
			code = "2";
		else if (id.equals("02"))
			code = "1";
		else if (id.equals("09"))
			code = "3";
		else if (id.equals("99"))
			code = "9";
		else
			code = id;
		return code;
	}

	/**
	 * 婚姻状态 转换成国家规定的编码 1.未婚；2.已婚；3.丧偶；4.离婚；9.其他。
	 * 
	 * @param id
	 * @return
	 */
	private String getNewMarrige(String id) {
		String code = "";
		if (id.equals("3"))
			code = "4";
		else if (id.equals("4"))
			code = "3";
		else
			code = id;
		return code;
	}

	/**
	 * 检验字符串是否为空 空时符"-"值
	 * 
	 * @param str
	 * @return
	 */
	private String getcheckStr(String str) {
		String line = "";
		if (str.trim().length() == 0)
			line = "-";
		else
			line = str;
		return line;
	}

	// /**
	// * 年龄满1周岁的，以实足年龄的相应整数填写；年龄不足1周岁的，按照实足年龄的月龄填写，以分数形式表示：
	// * 分数的整数部分代表实足月龄，分数部分分母为30，分子为不足1个月的天数，如“2 月”代表患儿实足年龄为2个月又15天。
	// * @param odo
	// * @return String 界面显示的年龄
	// */
	// public String showAge(Timestamp birthday, Timestamp t) {
	// String age = "";
	// String[] res;
	// res = StringTool.CountAgeByTimestamp(birthday, t);
	// if (TypeTool.getInt(res[0]) < 1) {
	// age =res[1]+" "+ res[2] + "─—";
	// } else {
	// age = res[0] + "岁";
	// }
	// return age;
	// }
	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 给文字加括号
	 */
	public String addBrace(String str) {// wanglong add 20141120
		if (str == null || str.trim().length() <= 0) {
			return "";
		}
		return "(" + str + ")";
	}

	/**
	 * 多转科科室中间加"|"
	 * 
	 * @param caseNo
	 * @return
	 */
	private String getTranDept(String caseNo) {// wanglong add 20150115
		if (TCM_Transform.isNull(caseNo)) {
			return "";
		}
		String str = "";
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm tranParm = ADMTransLogTool.getInstance().getTranHospFormro(parm); // 转科数据
		for (int i = tranParm.getCount() - 2; i >= 0; i--) {// 去掉第一个和最后一个
			if (str.trim().length() > 0) {
				str += "|";
			}
			str += tranParm.getValue("IN_DEPT_CODE", i);
		}
		return str;
	}
}
