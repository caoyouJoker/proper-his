package jdo.sta;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 校验规则类
 * </p>
 * 
 * <p>
 * Description: 校验规则
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */

public class STAValidator extends TJDOTool {

	// 性别（男性）/疾病诊断ICD10编码
	private String[] ICD10ForMail = { "B26.0", "C60", "C61", "C62", "C63",
			"D07.4", "D07.5", "D07.6", "D17.6", "D29", "D40", "E29", "E89.5",
			"F52.4", "I86.1", "L29.1", "N40", "N41", "N42", "N43", "N44",
			"N45", "N46", "N47", "N48", "N49", "N50", "N51", "Q53", "Q54",
			"Q55", "R86", "S31.2", "S31.3", "Z12.5" };
	// 性别（女性）/疾病诊断ICD10编码
	private String[] ICD10ForFemail = { "A34", "B37.3", "C51", "C52", "C53",
			"C54", "C55", "C56", "C57", "C58", "C79.6", "D06", "D07.0",
			"D07.1", "D07.2", "D07.3", "D25", "D26", "D27", "D28", "D39",
			"E28", "E89.4", "F52.5", "F53", "I86.3", "L29.2", "M80.0", "M80.1",
			"M81.0", "M81.1", "M83.0", "N70", "N71", "N72", "N73", "N74",
			"N75", "N76", "N77", "N78", "N79", "N80", "N81", "N82", "N83",
			"N84", "N85", "N86", "N87", "N88", "N89", "N90", "N91", "N92",
			"N93", "N94", "N95", "N96", "N97",
			"N98",
			"N99.2",
			"N99.3",
			"P54.6", // O00-O99
			"Q50", "Q51", "Q52", "R87", "S31.4", "S37.4", "S37.5", "S37.6",
			"T19.2", "T19.3", "T83.3", "Z01.4", "Z12.4", "Z30.1", "Z30.3",
			"Z30.5", "Z31.1", "Z31.2", "Z32", "Z33", "Z34", "Z35", "Z36",
			"Z37", "Z39", "Z87.5", "Z97.5" };
	// 性别（男性）/手术操作编码
	// private String[] operatorCodeForMail = { "60","61","62","63","64",
	// "87.9", "98.24", "99.94" };
	// 性别（女性）/手术操作编码
	// private String[] operatorCodeForFemail = { "65","66","67","68","69",
	// "70","71","72","73","74",
	// "75", "87.8", "88.46","88.78",
	// "89.26", "92.17", "96.14","96.15","96.16",
	// "96.17","96.18", "97.7", "98.16","98.17" };

	// 错误信息
	private String error1Msg = "只适用于男性：B26.0；C60-C63；D07.4-D07.6；D17.6；D29；D40；E29；E89.5；F52.4；I86.1；L29.1；N40-N51；Q53-Q55；R86；S31.2-S31.3；Z12.5";
	private String error2Msg = "只适用于女性：A34；B37.3；C51-C58；C79.6；D06；D07.0-D07.3；D25-D28；D39；E28；E89.4；F52.5；F53；I86.3；L29.2；L70.5；M80.0-M80.1；M81.0-M81.1；"
			+ "M83.0；N70-N98；N99.2-N99.3；O00-O99；P54.6；Q50-Q52；R87；S31.4；S37.4-S37.6；T19.2-T19.3；T83.3；Z01.4；Z12.4；Z30.1；Z30.3；Z30.5；Z31.1-Z31.2；Z32-Z36；Z39；"
			+ "Z43.7；Z87.5；Z97.5";
	// private String error3Msg="只适用于男性：60-64；87.9；98.24；99.94";
	// private String
	// error4Msg="只适用于女性：65-75；87.8；88.46；88.78；89.26；92.17；96.14-96.18；97.7；98.16-98.17";
	// private String error5Msg="诊断治疗结果代码为‘4’，则离院方式为‘5’";
	private String error6Msg = "年龄=入院日期-出生日期";
	// private String error7Msg="入院日期应大于等于上次出院日期";
	private String error8Msg = "入院日期<=出院日期；入院日期<=入院后确诊日期<=出院日期；入院日期<=手术操作日<=出院日期";
	private String error9Msg = "门(急)诊诊断编码(ICD-10)、入院诊断编码(ICD-10)、主要诊断编码(ICD-10)、其它诊断编码(ICD-10)各项诊断编码范围应为：A～U开头和Z开头的编码；不包括字母V、W、X、Y开头的编码"
			+ "医院感染名称编码(ICD-10)各项编码范围应为：A00-U99；Z00-Z99；不包括M80000/0-M99999/6，及以字母V、W、X、Y开头的编码";
	private String error10Msg = "当门(急)诊诊断编码(ICD-10)、入院诊断编码(ICD-10)、主要诊断编码(ICD-10)、其它诊断编码(ICD-10)出现P10-P15，入院日期-出生日期≤28天，“年龄不足1周岁(月)”必须<=28天";
	private String error11Msg = "所有分娩产妇病历，本次出院其它诊断必须有分娩结局，即Z37编码";
	private String error12Msg = "ICD-10形态学编码范围：M8、M9开头的";
	// private String
	// error13Msg="当主要诊断编码(ICD-10)为：S00-T99，则损伤和中毒外部原因编码(ICD-10)、损伤和中毒外部原因名称必填";
	// private String error14Msg="ICD-10编码范围：V00-Y99";
	private String error15Msg = "抢救次数应等于抢救成功次数，除主要诊断出院情况、其他诊断出院情况、离院方式为死亡时，抢救次数可以等于抢救成功次数加1，表示抢救未成功死亡";
	private String error16Msg = "实际住院(天)>=特护(天)+一级护理(天)+二级护理(天)+三级护理(天)";
	private String error17Msg = "产科分娩病人ICD编码为Z37必填；年龄<=28天的新生儿必填";
	private String error18Msg = "年龄<=28天的新生儿必填";
	private String error19Msg = "年龄<1周岁的婴儿必填";
	private String error20Msg = "入院日期时间<=监护室进入日期时间<监护室退出日期时间<=出院日期时间";
	private String error21Msg = "总费用=各分项费用之和";
	private String error22Msg = "分项费用>=0";
	SimpleDateFormat Df = new SimpleDateFormat();

	/**
	 * 实例
	 */
	public static STAValidator instanceObject;
	private int compareTo;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static STAValidator getInstance() {
		if (instanceObject == null)
			instanceObject = new STAValidator();
		return instanceObject;
	}

	public STAValidator() {
		onInit();
	}

	/**
	 * 校验方法
	 * */
	public TParm checkUI(TParm initParm) {
		TParm result = new TParm();
		// System.out.println(initParm);
		StringBuffer errorBuffer = new StringBuffer();
		// 第一步：非空校验
		// 1.P900 医疗机构代码
		String P900 = initParm.getValue("P900");
		if (P900 == null || P900.length() == 0) {
			errorBuffer.append("<ERR:P900医疗机构代码不能为空>\r\n");
		}
		// 2.P6891 机构名称
		String P6891 = initParm.getValue("P6891");
		if (P6891 == null || P6891.length() == 0) {
			errorBuffer.append("<ERR:P6891机构名称不能为空>\r\n");
		}
		// 5.P1 医疗卡付款方式
		String P1 = initParm.getValue("P1");
		if (P1 == null || P1.length() == 0) {
			errorBuffer.append("<ERR:P1医疗卡付款方式不能为空>\r\n");
		}
		// 6.P2住院次数
		String P2 = initParm.getValue("P2");
		if (P2 == null || P2.length() == 0) {
			errorBuffer.append("<ERR:P2住院次数不能为空>\r\n");
		}
		// 7.P3病案号
		String P3 = initParm.getValue("P3");
		if (P3 == null || P3.length() == 0) {
			errorBuffer.append("<ERR:P3病案号不能为空>\r\n");
		}
		// 8.P4姓名
		String P4 = initParm.getValue("P4");
		if (P4 == null || P4.length() == 0) {
			errorBuffer.append("<ERR:P4姓名不能为空>\r\n");
		}
		// 9.P5性别
		Integer P5 = initParm.getInt("P5");
		if (P5 == null || P5.equals("")) {
			errorBuffer.append("<ERR:P5性别不能为空>\r\n");
		}
		Integer sex = P5;

		// 12.P8婚姻状况
		String P8 = initParm.getValue("P8");
		if (P8 == null || P8.length() == 0) {
			errorBuffer.append("<ERR:P8婚姻状况不能为空>\r\n");
		}
		// 31.P804 入院途径
		String P804 = initParm.getValue("P804");
		if (P804 == null || P804.length() == 0) {
			errorBuffer.append("<ERR:P804入院途径不能为空>\r\n");
		}
		// 33.P22 入院日期
		String P22 = initParm.getValue("P22");
		if (P22 == null || P22.length() == 0) {
			errorBuffer.append("<ERR:P22入院日期不能为空>\r\n");
		}
		// 34.P23 入院科别
		String P23 = initParm.getValue("P23");
		if (P23 == null || P23.length() == 0) {
			errorBuffer.append("<ERR:P23入院科别不能为空>\r\n");
		}

		// 37.P25 出院日期
		String P25 = initParm.getValue("P25");
		if (P25 == null || P25.length() == 0) {
			errorBuffer.append("<ERR:P25出院日期不能为空>\r\n");
		}
		// 38.P26 出院科别
		String P26 = initParm.getValue("P26");
		if (P26 == null || P26.length() == 0) {
			errorBuffer.append("<ERR:P26出院科别不能为空>\r\n");
		}
		// 40.P27 实际住院天数
		Integer P27 = initParm.getInt("P27");
		if (P27 == null || P27.equals("")) {
			errorBuffer.append("<ERR:P27实际住院天数不能为空>\r\n");
		}
		// 41.P28 门（急）诊诊断编码
		String P28 = initParm.getValue("P28");
		if (P28 == null || P28.length() == 0) {
			errorBuffer.append("<ERR:P28门（急）诊诊断编码不能为空>\r\n");
		}
		// 42.P281 门（急）诊诊断描述
		String P281 = initParm.getValue("P281");
		if (P281 == null || P281.length() == 0) {
			errorBuffer.append("<ERR:P281门（急）诊诊断描述不能为空>\r\n");
		}
//		// 43.P29 入院时情况
//		String P29 = initParm.getValue("P29");
//		if (P29 == null || P29.length() == 0) {
//			errorBuffer.append("<ERR:P29入院时情况不能为空>\r\n");
//		}
		// 44.P30 入院诊断编码
		String P30 = initParm.getValue("P30");
		if (P30 == null || P30.length() == 0) {
			errorBuffer.append("<ERR:P30入院诊断编码不能为空>\r\n");
		}
		// 45.P301 入院诊断描述
		String P301 = initParm.getValue("P301");
		if (P301 == null || P301.length() == 0) {
			errorBuffer.append("<ERR:P301入院诊断描述不能为空>\r\n");
		}
		// 46.P31 入院后确诊日期
		String P31 = initParm.getValue("P31");
		if (P31.length() == 0 || P31 == null) {
			errorBuffer.append("<ERR:P31入院后确诊日期不能为空>\r\n");
		}
		// 47.P321 主要诊断编码
		// System.out.println( "----->\r\n"+initParm.getValue("P321"));
		String P321 = initParm.getValue("P321");
		if (P321 == null || P321.length() == 0) {
			errorBuffer.append("<ERR:P321主要诊断编码不能为空>\r\n");
		}
		// 48.P322 主要诊断疾病描述
		String P322 = initParm.getValue("P322");
		if (P322 == null || P322.length() == 0) {
			errorBuffer.append("<ERR:P322主要诊断疾病描述不能为空>\r\n");
		}
		// 121.P431 科主任
		String P431 = initParm.getValue("P431");
		if (P431 == null || P431.length() == 0) {
			errorBuffer.append("<ERR:P431科主任不能为空>\r\n");
		}
		// 122.P432 主(副主)任医师
		String P432 = initParm.getValue("P432");
		if (P432 == null || P431.length() == 0) {
			errorBuffer.append("<ERR:P432主(副主)任医师不能为空>\r\n");
		}
		// 123.P433 主治医师
		String P433 = initParm.getValue("P433");
		if (P433 == null || P433.length() == 0) {
			errorBuffer.append("<ERR:P433主治医师不能为空>\r\n");
		}
		// 124.P434 住院医师
		String P434 = initParm.getValue("P434");
		if (P434 == null || P434.length() == 0) {
			errorBuffer.append("<ERR:P434住院医师不能为空>\r\n");
		}
		// 125.P819 责任护士
		String P819 = initParm.getValue("P819");
		if (P819 == null || P819.length() == 0) {
			errorBuffer.append("<ERR:P819责任护士不能为空>\r\n");
		}

		// 130.P44 病案质量
		String P44 = initParm.getValue("P44");
		if (P44 == null || P44.length() == 0) {
			errorBuffer.append("<ERR:P44病案质量不能为空>\r\n");
		}
		// 131.P45 质控医师
		String P45 = initParm.getValue("P45");
		if (P45 == null || P45.length() == 0) {
			errorBuffer.append("<ERR:P45质控医师不能为空>\r\n");
		}
		// 132.P46 质控护师
		String P46 = initParm.getValue("P46");
		if (P46 == null || P46.length() == 0) {
			errorBuffer.append("<ERR:P46质控护师不能为空>\r\n");
		}
		// 291.P62 ABO血型
		String P62 = initParm.getValue("P62");
		if (P62 == null || P62.length() == 0) {
			errorBuffer.append("<ERR:P62ABO血型不能为空>\r\n");
		}
		// 292.P63 Rh血型
		String P63 = initParm.getValue("P63");
		if (P63 == null || P63.length() == 0) {
			errorBuffer.append("<ERR:P63Rh血型不能为空>\r\n");
		}
		// 314.P741 离院方式
		String P741 = initParm.getValue("P741");
		if (P741 == null || P741.length() == 0) {
			errorBuffer.append("<ERR:P741离院方式不能为空>\r\n");
		}
		// 317.P782 住院总费用
		Double P782 = initParm.getDouble("P782");
		if (P782 == null || P782.equals("")) {
			errorBuffer.append("<ERR:P782住院总费用不能为空>\r\n");
		}
		// 第二步：诊断编码ICD10校验

		// 门（急）诊诊断编码
		if (P28 != null && !P28.equals("")) {
			if (!(this.checkICD10(sex, P28))) {

				if (sex == 1) {

					errorBuffer
							.append("<ERR:P28 门（急）诊诊断编码 error1" + this.error1Msg + ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P28  门（急）诊诊断编码  error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P28))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P28 error9  门（急）诊诊断编码  " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P28 error9 门（急）诊诊断编码 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}

		// 入院诊断编码
		if (P30 != null && !P30.equals("")) {
			if (!(this.checkICD10(sex, P30))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P30 入院诊断编码 error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P30 入院诊断编码 error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P30))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P30 入院诊断编码 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P30  入院诊断编码 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 主要诊断编码
		if (P321 != null && !P321.equals("")) {
			if (!(this.checkICD10(sex, P321))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P321 主要诊断编码 error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P321 主要诊断编码  error2 " + this.error2Msg
							+ ">\r\n");
				}

			}

			if (!(this.checkICD10Other(sex, P321))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P321 主要诊断编码  error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P321 主要诊断编码 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 当主要诊断编码(ICD-10)为：S00-T99，则损伤和中毒外部原因编码(ICD-10)、损伤和中毒外部原因名称必填

		// if (P321 != null && (P321.substring(0).equals("S") ||
		// P321.substring(0).equals("T"))) {
		// if (initParm.getValue("P361") == null
		// || initParm.getValue("P361").equals("")) {
		//			
		// errorBuffer
		// .append("<ERR:P361  error13 "+this.error13Msg+">\r\n");
		//				
		// }
		// // ICD-10编码范围：V00-Y99
		// if (!(initParm.getValue("P361").substring(0).equals("V")
		// || initParm.getValue("P361").substring(0).equals("W")
		// || initParm.getValue("P361").substring(0).equals("X") || initParm
		// .getValue("P361").substring(0).equals("Y"))) {
		//			
		// errorBuffer.append("<ERR:P361 error14 "+this.error14Msg+">\r\n");
		//			
		// }
		// if (initParm.getValue("P362") == null
		// || initParm.getValue("P362").equals("")) {
		//			
		// errorBuffer
		// .append("<ERR:P362 error13 "+this.error13Msg+">\r\n");
		//				
		// }
		// }
		// 其他诊断编码
		String P324 = initParm.getValue("P324");
		if (P324 != null && !P324.equals("")) {
			if (!(this.checkICD10(sex, P324))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P324 其他诊断编码 1 error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P324 其他诊断编码 1 error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P324))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P324 其他诊断编码 1 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P324 其他诊断编码 1 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}

		// 其他诊断编码2
		String P327 = initParm.getValue("P327");
		if (P327 != null && !P327.equals("")) {
			if (!(this.checkICD10(sex, P327))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P327 其他诊断编码2 error1 " + this.error1Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P327  其他诊断编码2 error2 " + this.error2Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P327))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P327 其他诊断编码2 error9 " + this.error9Msg+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P327 其他诊断编码2 error9 " + this.error9Msg+ ">\r\n");
				}

			}
		}
		// 其他诊断编码3
		String P3291 = initParm.getValue("P3291");
		if (P3291 != null && !P3291.equals("")) {
			if (!(this.checkICD10(sex, P3291))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3291   其他诊断编码3 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3291  其他诊断编码3  error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3291))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3291  其他诊断编码3 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3291  其他诊断编码3 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码4
		String P3294 = initParm.getValue("P3294");
		if (P3294 != null && !P3294.equals("")) {
			if (!(this.checkICD10(sex, P3294))) {

				if (sex == 1) {
					errorBuffer.append("<ERR:P3294 其他诊断编码4 error1 " + this.error1Msg+ " >\r\n");
				}
				if (sex == 2) {
					errorBuffer.append("<ERR:P3294 其他诊断编码4 error2 " + this.error2Msg+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3294))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3294 其他诊断编码4 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3294 其他诊断编码4 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码5
		String P3297 = initParm.getValue("P3297");
		if (P3297 != null && !P3297.equals("")) {
			if (!(this.checkICD10(sex, P3297))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3297 其他诊断编码5 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3297 其他诊断编码5 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3297))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3297 其他诊断编码5 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3297 其他诊断编码5 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码6
		String P3281 = initParm.getValue("P3281");
		if (P3281 != null && !P3281.equals("")) {
			if (!(this.checkICD10(sex, P3281))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3281 其他诊断编码6 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3281 其他诊断编码6 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3281))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3281 其他诊断编码6 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3281 其他诊断编码6 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码7
		String P3284 = initParm.getValue("P3284");
		if (P3284 != null && !P3284.equals("")) {
			if (!(this.checkICD10(sex, P3284))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3284 其他诊断编码7 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3284 其他诊断编码7 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3284))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3284 其他诊断编码7 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3284 其他诊断编码7 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码8
		String P3287 = initParm.getValue("P3287");
		if (P3287 != null && !P3287.equals("")) {
			if (!(this.checkICD10(sex, P3287))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3287 其他诊断编码8 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3287 其他诊断编码8 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3287))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3287 其他诊断编码8 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3287 其他诊断编码8 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码9
		String P3271 = initParm.getValue("P3271");
		if (P3271 != null && !P3271.equals("")) {
			if (!(this.checkICD10(sex, P3271))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3271 其他诊断编码9 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3271 其他诊断编码9 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3271))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3271 其他诊断编码9 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3271 其他诊断编码9 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 其他诊断编码10
		String P3274 = initParm.getValue("P3274");
		if (P3274 != null && !P3274.equals("")) {
			if (!(this.checkICD10(sex, P3274))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3274 其他诊断编码10 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3274 其他诊断编码10  error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
			if (!(this.checkICD10Other(sex, P3274))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P3274  其他诊断编码10 error9 " + this.error9Msg
							+ ">\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P3274  其他诊断编码10 error9 " + this.error9Msg
							+ ">\r\n");
				}

			}
		}
		// 病理诊断编码1
		String P351 = initParm.getValue("P351");
		// ICD-10编码范围：M80000/0-M99999/6
		if (P351 != null && !P351.equals("")) {
			if (!(StringTool.compareTo(P351, "M80000/0") > 0 && StringTool
					.compareTo("M99999/6", P351) > 0)) {
				{
					errorBuffer.append("<ERR:P351 病理诊断编码1 error12 " + this.error12Msg
							+ " >\r\n");
				}

			}

			if (!(this.checkICD10(sex, P351))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P351 病理诊断编码1  error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P351病理诊断编码1 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// 病理诊断编码2
		String P353 = initParm.getValue("P353");
		// ICD-10编码范围：M80000/0-M99999/6
		if (P353 != null && !P353.equals("")) {
			if (!(StringTool.compareTo(P353, "M80000/0") > 0 && StringTool
					.compareTo("M99999/6", P353) > 0)) {
				{
					errorBuffer.append("<ERR:P353 病理诊断编码2 error12 " + this.error12Msg
							+ " >\r\n");
				}

			}

			if (!(this.checkICD10(sex, P353))) {
				if (sex == 1) {
					errorBuffer.append("<ERR:P353 病理诊断编码2 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {
					errorBuffer.append("<ERR:P353 病理诊断编码2 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// 病理诊断编码3
		String P355 = initParm.getValue("P355");
		// ICD-10编码范围：M80000/0-M99999/6
		if (P355 != null && !P355.equals("")) {
			if (!(StringTool.compareTo(P355, "M80000/0") > 0 && StringTool
					.compareTo("M99999/6", P355) > 0)) {
				{
					errorBuffer.append("<ERR:P355 病理诊断编码3 error12 " + this.error12Msg
							+ " >\r\n");
				}

			}

			if (!(this.checkICD10(sex, P355))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P355  病理诊断编码3 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P355 病理诊断编码3 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// 损伤、中毒的外部因素编码1
		String P361 = initParm.getValue("P361");
		if (P361 != null && !P361.equals("")) {
			if (!(this.checkICD10(sex, P361))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P361 损伤、中毒的外部因素编码1 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P361  损伤、中毒的外部因素编码1 error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// 损伤、中毒的外部因素编码2
		String P363 = initParm.getValue("P363");
		if (P363 != null && !P363.equals("")) {
			if (!(this.checkICD10(sex, P363))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P363  损伤、中毒的外部因素编码2 error1 " + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P363 损伤、中毒的外部因素编码2  error2 " + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// 损伤、中毒的外部因素编码3
		String P365 = initParm.getValue("P365");
		if (P365 != null && !P365.equals("")) {
			if (!(this.checkICD10(sex, P365))) {

				if (sex == 1) {

					errorBuffer.append("<ERR:P365 error1  损伤、中毒的外部因素编码3" + this.error1Msg
							+ " >\r\n");
				}
				if (sex == 2) {

					errorBuffer.append("<ERR:P365 error2  损伤、中毒的外部因素编码3" + this.error2Msg
							+ " >\r\n");
				}

			}
		}
		// 第三步：手术编码校验
		// 手术操作编码1
		String P490 = initParm.getValue("P490");
		// if (P490 !=null && !P490.equals("")) {
		// if (!(this.checkOperatorCode(sex, P490))) {
		//
		// if (sex == 1) {
		// errorBuffer.append("<ERR:P490 error3 "+this.error3Msg+" >\r\n");
		// }
		// if (sex == 2) {
		// errorBuffer.append("<ERR:P490 error4 "+this.error4Msg+" >\r\n");
		// }
		//		
		// }
		// }
		// 手术操作编码2
		String P4911 = initParm.getValue("P4911");
		// if (P4911 !=null && !P4911.equals("")) {
		// if (!(this.checkOperatorCode(sex, P4911))) {
		//
		// if (sex == 1) {
		//				
		// errorBuffer.append("<ERR:P4911 error3 "+this.error3Msg+" >\r\n");
		// }
		// if (sex == 2) {
		//				
		// errorBuffer.append("<ERR:P4911 error4 "+this.error4Msg+" >\r\n");
		//				
		// }
		//		
		// }
		// }
		// 第四步：年龄的校验
		// 11.P7年龄
		Integer P7 = initParm.getInt("P7");// 得到要校验的年龄
		String P6 = initParm.getValue("P6");// 出生日期
		long age = 0;
		long mod = 0;
		if (P6 != null && !P6.equals("")) {
			Timestamp dt1 = getFormateDate(initParm.getTimestamp("P22"),"yyyyMMdd");
			Timestamp dt2 = initParm.getTimestamp("P6");
			long i = (dt1.getTime() - dt2.getTime()) / (1000 * 60 * 60 * 24);
			age = i / 365;
			if (age < 1) {
				if (initParm.getValue("P66") == null
						|| initParm.getValue("P66").toString().equals("")) {
					errorBuffer.append("<ERR:P66 （年龄不足1周岁的）年龄 error19 " + this.error19Msg
							+ " >\r\n");
				}
				age = i;
				mod = i;
				if (age <= 28) {
					if (initParm.getValue("P67") == null
							|| initParm.getValue("P67").toString().equals("")) {
						errorBuffer.append("<ERR:P67 新生儿出生体重(克) error18 "
								+ this.error18Msg + " >\r\n");
					}
					if (initParm.getValue("P681") == null
							|| initParm.getValue("P681").toString().equals("")) {
						errorBuffer.append("<ERR:新生儿出生体重1 P681 error17 "
								+ this.error17Msg + " >\r\n");
					}
				}
			}else{
				if (P7 > age + 1 || P7 < age - 1) {
					errorBuffer.append("<ERR:P7 年龄 error6 " + this.error6Msg + " >\r\n");
				}
			}
		}
		// P28，P30，P321，P324，P3291
		// 当门(急)诊诊断编码(ICD-10)、入院诊断编码(ICD-10)、主要诊断编码(ICD-10)、其它诊断编码(ICD-10)出现P10-P15，
		// 入院日期-出生日期≤28天，“年龄不足1周岁(月)”必须<=28天
		if (P28 != null && !P28.equals("")) {
			if (StringTool.compareTo(P28.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P28.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 （年龄不足1周岁的）年龄  error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
		}
		if (P30 != null && !P30.equals("")) {
			if (StringTool.compareTo(P30.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P30.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 （年龄不足1周岁的）年龄 error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
		}
		if (P321 != null && !P321.equals("")) {
			if (StringTool.compareTo(P321.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P321.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 （年龄不足1周岁的）年龄 error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
			if (P324 != null && !P324.equals(""))
				if (StringTool.compareTo(P324.substring(0, 3), "P10") > 0
						&& StringTool.compareTo("P15", P324.substring(0, 3)) > 0) {
					if (!(initParm.getValue("P66") != null
							&& initParm.getInt("P66") <= 28 && mod <= 28)) {
						errorBuffer.append("<ERR:P66 （年龄不足1周岁的）年龄 error10 "
								+ this.error10Msg + " >\r\n");
					}
				}
		}
		if (P3291 != null && !P3291.equals("")) {
			if (StringTool.compareTo(P3291.substring(0, 3), "P10") > 0
					&& StringTool.compareTo("P15", P3291.substring(0, 3)) > 0) {
				if (!(initParm.getValue("P66") != null
						&& initParm.getInt("P66") <= 28 && mod <= 28)) {
					errorBuffer.append("<ERR:P66 （年龄不足1周岁的）年龄 error10 " + this.error10Msg
							+ " >\r\n");
				}
			}
		}
		if (P324 != null && !P324.equals("")) {
			if (StringTool.compareTo(P324.substring(0, 3), "Z37") == 0) {
				if (initParm.getValue("P681") == null
						|| initParm.getValue("P681").toString().equals("")) {
					errorBuffer.append("<ERR:P681  当主要诊断或者其它诊断编码为Z37时必填 >\r\n");
				}
			}
		}
		// 第五步：入院日期应该大于等于上次出院日期 上次出院日期
		// String MR_NO = initParm.getValue("MR_NO").toString();
		// String sql = "SELECT DS_DATE FROM ADM_INP WHERE MR_NO = '" + MR_NO
		// + "' ORDER BY DS_DATE DESC";
		// TParm DS_DateParm = new TParm(TJDODBTool.getInstance().select(sql));
		// int count = DS_DateParm.getCount();
		// String DS_Date = "";
		// if(count >= 2){
		// DS_Date =(String) DS_DateParm.getData("DS_DATE", 0);// 上次出院日期
		// }

		// if(DS_Date != null && !DS_Date.equals("") && P22 != null &&
		// !P22.endsWith("")){
		// if (!(this.dateCompare(DS_Date, P22))) {
		// errorBuffer.append("<ERR:P22 error7 "+this.error7Msg+" >\r\n");
		// }
		// }
		// 第六步：入院日期<=出院日期 入院日期<=入院后确诊日期<=出院日期 入院日期<=手术操作日<=出院日期 error8
		String P491 = initParm.getValue("P491");// 手术操作日期
		// 入院日期<=出院日期
		if (P22 != null && !P22.equals("") && P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P25))) {
				errorBuffer.append("<ERR:P22 入院日期, P25 出院日期 error8 " + this.error8Msg
						+ " >\r\n");
			}
		}
		// 入院日期<=入院后确诊日期<=出院日期
		if (P22 != null && !P22.equals("") && P31 != null && !P31.equals("")) {
			if (!(this.dateCompare(P22, P31) && this.dateCompare(P31, P25))) {
				errorBuffer.append("<ERR:P22入院日期 , P31 入院后确诊日期 error8 " + this.error8Msg
						+ " >\r\n");
			}
		}
		String compareDate=(P22 != null && !P22.equals(""))?String.valueOf(StringTool.rollDate(initParm.getTimestamp("P22"), -1)):"";
		// 入院日期<=手术操作日期1<=出院日期
		if (P22 != null && !P22.equals("") && P491 != null && !P491.equals("")) {
			if (!(this.dateCompare(compareDate, P491) && this.dateCompare(P491, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P491手术操作日期1 error8 " + this.error8Msg
						+ " >\r\n");
			}
			// 手术代码为空
			if (P490.equals("") && P490 == null) {
				errorBuffer.append("<ERR:自定义错误  P490手术代码为空" + " >\r\n");
			}
			String P492 = initParm.getValue("P492");
			// 手术描述为空
			if (P492.equals("") && P492 == null) {
				errorBuffer.append("<ERR:自定义错误 P492手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期2<=出院日期
		String P4912 = initParm.getValue("P4912");
		if (P22 != null && !P22.equals("") && P4912 != null
				&& !P4912.equals("")) {
			if (!(this.dateCompare(compareDate, P4912) && this.dateCompare(P4912, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P4912手术操作日期2 error8 " + this.error8Msg
						+ " >\r\n");
			}
			// 手术代码为空
			if (P4911.equals("") && P4911 == null) {
				errorBuffer.append("<ERR:自定义错误  P4911手术代码为空" + " >\r\n");
			}
			String P4913 = initParm.getValue("P4913");
			// 手术描述为空
			if (P4913.equals("") && P4913 == null) {
				errorBuffer.append("<ERR:自定义错误 P4913手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期3<=出院日期
		String P4923 = initParm.getValue("P4923");
		if (P22 != null && !P22.equals("") && P4923 != null
				&& !P4923.equals("")) {
			if (!(this.dateCompare(compareDate, P4923) && this.dateCompare(P4923, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P4923手术操作日期3 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P4922 = initParm.getValue("P4922");
			// 手术代码为空
			if (P4922.equals("") && P4922 == null) {
				errorBuffer.append("<ERR:自定义错误  P4922手术代码为空" + " >\r\n");
			}
			String P4924 = initParm.getValue("P4924");
			// 手术描述为空
			if (P4924.equals("") && P4924 == null) {
				errorBuffer.append("<ERR:自定义错误 P4924手术描述为空  " + " >\r\n");
			}
		}

		// 入院日期<=手术操作日期4<=出院日期
		String P4534 = initParm.getValue("P4534");
		if (P22 != null && !P22.equals("") && P4534 != null
				&& !P4534.equals("")) {
			if (!(this.dateCompare(compareDate, P4534) && this.dateCompare(P4534, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P4534手术操作日期4 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P4533 = initParm.getValue("P4533");
			// 手术代码为空
			if (P4533.equals("") && P4533 == null) {
				errorBuffer.append("<ERR:自定义错误  P4533手术代码为空" + " >\r\n");
			}
			String P4535 = initParm.getValue("P4535");
			// 手术描述为空
			if (P4535.equals("") && P4535 == null) {
				errorBuffer.append("<ERR:自定义错误 P4535手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期5<=出院日期
		String P4545 = initParm.getValue("P4545");
		if (P22 != null && !P22.equals("") && P4545 != null
				&& !P4545.equals("")) {
			if (!(this.dateCompare(compareDate, P4545) && this.dateCompare(P4545, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P4545手术操作日期5 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P4544 = initParm.getValue("P4544");
			// 手术代码为空
			if (P4544.equals("") && P4544 == null) {
				errorBuffer.append("<ERR:自定义错误  P4544手术代码为空" + " >\r\n");
			}
			String P4546 = initParm.getValue("P4546");
			// 手术描述为空
			if (P4546.equals("") && P4546 == null) {
				errorBuffer.append("<ERR:自定义错误 P4546手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期6<=出院日期
		String P45003 = initParm.getValue("P45003");
		if (P22 != null && !P22.equals("") && P45003 != null
				&& !P45003.equals("")) {
			if (!(this.dateCompare(compareDate, P45003) && this
					.dateCompare(P45003, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P45003手术操作日期6 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45002 = initParm.getValue("P45002");
			// 手术代码为空
			if (P45002.equals("") && P45002 == null) {
				errorBuffer.append("<ERR:自定义错误  P45002手术代码为空" + " >\r\n");
			}
			String P45004 = initParm.getValue("P45004");
			// 手术描述为空
			if (P45004.equals("") && P45004 == null) {
				errorBuffer.append("<ERR:自定义错误 P45004手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期7<=出院日期
		String p45015 = initParm.getValue("P45015");
		if (P22 != null && !P22.equals("") && p45015 != null
				&& !p45015.equals("")) {
			if (!(this.dateCompare(compareDate, p45015) && this
					.dateCompare(p45015, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P45015手术操作日期7 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45014 = initParm.getValue("P45014");
			// 手术代码为空
			if (P45014.equals("") && P45014 == null) {
				errorBuffer.append("<ERR:自定义错误  P45014手术代码为空" + " >\r\n");
			}
			String P45016 = initParm.getValue("P45016");
			// 手术描述为空
			if (P45016.equals("") && P45016 == null) {
				errorBuffer.append("<ERR:自定义错误 P45016手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期8<=出院日期
		String p45027 = initParm.getValue("P45027");
		if (P22 != null && !P22.equals("") && p45027 != null
				&& !p45027.equals("")) {
			if (!(this.dateCompare(compareDate, p45027) && this
					.dateCompare(p45027, P25))) {
				errorBuffer.append("<ERR:P22入院日期, p45027手术操作日期8  error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45026 = initParm.getValue("P45026");
			// 手术代码为空
			if (P45026.equals("") && P45026 == null) {
				errorBuffer.append("<ERR:自定义错误  P45026手术代码为空" + " >\r\n");
			}
			String P45028 = initParm.getValue("P45028");
			// 手术描述为空
			if (P45028.equals("") && P45028 == null) {
				errorBuffer.append("<ERR:自定义错误 P45028手术描述为空  " + " >\r\n");
			}
		}

		// 入院日期<=手术操作日期9<=出院日期
		String p45039 = initParm.getValue("P45039");
		if (P22 != null && !P22.equals("") && p45039 != null
				&& !p45039.equals("")) {
			if (!(this.dateCompare(compareDate, p45039) && this
					.dateCompare(p45039, P25))) {
				errorBuffer.append("<ERR:P22入院日期, P45039手术操作日期9 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45038 = initParm.getValue("P45038");
			// 手术代码为空
			if (P45038.equals("") && P45038 == null) {
				errorBuffer.append("<ERR:自定义错误  P45038手术代码为空" + " >\r\n");
			}
			String P45040 = initParm.getValue("P45040");
			// 手术描述为空
			if (P45040.equals("") && P45040 == null) {
				errorBuffer.append("<ERR:自定义错误 P45040手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期<=手术操作日期10<=出院日期
		String p45051 = initParm.getValue("P45051");
		if (P22 != null && !P22.equals("") && p45051 != null
				&& !p45051.equals("")) {
			if (!(this.dateCompare(compareDate, p45051) && this
					.dateCompare(p45051, P25))) {
				errorBuffer.append("<ERR:P22入院日期, p45051手术操作日期10 error8 " + this.error8Msg
						+ " >\r\n");
			}
			String P45050 = initParm.getValue("P45050");
			// 手术代码为空
			if (P45050.equals("") && P45050 == null) {
				errorBuffer.append("<ERR:自定义错误  P45050手术代码为空" + " >\r\n");
			}
			String P45052 = initParm.getValue("P45052");
			// 手术描述为空
			if (P45052.equals("") && P45052 == null) {
				errorBuffer.append("<ERR:自定义错误 P45040手术描述为空  " + " >\r\n");
			}
		}
		// 入院日期时间<=监护室进入日期时间<监护室退出日期时间<=出院日期时间 error 20
		String P6912 = initParm.getValue("P6912");// 重症监护室1进入时间
		String P6913 = initParm.getValue("P6913");// 重症监护室1退出时间
		if (P22 != null && !P22.equals("") && P6912 != null
				&& !P6912.equals("") && P6913 != null && !P6913.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6912)
					&& this.dateCompare(P6912, P6913) && this.dateCompare(
					P6913, P25))) {
				errorBuffer.append("<ERR:P22入院日期时间, P6912监护室进入日期时间,P6913监护室退出日期时间, P25出院日期时间  error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6915 = initParm.getValue("P6915");// 重症监护室2进入时间
		String P6916 = initParm.getValue("P6916");// 重症监护室2退出时间
		if (P22 != null && !P22.equals("") && P6915 != null
				&& !P6915.equals("") && P6916 != null && !P6916.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6915)
					&& this.dateCompare(P6915, P6916) && this.dateCompare(
					P6916, P25))) {
				errorBuffer.append("<ERR:P22入院日期时间, P6915监护室进入日期时间2,P6916监护室退出日期时间2, P25出院日期时间 error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6918 = initParm.getValue("P6918");// 重症监护室3进入时间
		String P6919 = initParm.getValue("P6919");// 重症监护室3退出时间
		if (P22 != null && !P22.equals("") && P6918 != null
				&& !P6918.equals("") && P6919 != null && !P6919.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6918)
					&& this.dateCompare(P6918, P6919) && this.dateCompare(
					P6919, P25))) {
				errorBuffer.append("<ERR:P22入院日期时间, P6918监护室进入日期时间3,P6919监护室退出日期时间3, P25出院日期时间 error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6921 = initParm.getValue("P6921");// 重症监护室4进入时间
		String P6922 = initParm.getValue("P6922");// 重症监护室4退出时间
		if (P22 != null && !P22.equals("") && P6921 != null
				&& !P6921.equals("") && P6922 != null && !P6922.equals("")
				&& P25 != null && !P25.equals("")) {
			if (!(this.dateCompare(P22, P6921)
					&& this.dateCompare(P6921, P6922) && this.dateCompare(
					P6922, P25))) {
				errorBuffer.append("<ERR:P22入院日期时间, P6921监护室进入日期时间4,P6922监护室退出日期时间4, P25出院日期时间 error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		String P6924 = initParm.getValue("P6924");// 重症监护室5进入时间
		String P6925 = initParm.getValue("P6925");// 重症监护室5退出时间
		if (P22 != null && !P22.equals("") && P6924 != null
				&& !P6924.equals("") && P6925 != null && !P6925.equals("")
				&& P25 != null && !P6925.equals("")) {
			if (!(this.dateCompare(P22, P6924)
					&& this.dateCompare(P6924, P6925) && this.dateCompare(
					P6925, P25))) {
				errorBuffer.append("<ERR:P22入院日期时间, P6924监护室进入日期时间5,P6925监护室退出日期时间5, P25出院日期时间 error20 "
						+ this.error20Msg + " >\r\n");
			}
		}

		// 第七步：分项费用>=0
		double sums = 0.00;
		Double P752 = initParm.getDouble("P752");// 一般医疗服务费
		if (!(P752 == null || P752.equals(""))) {
			if (!(P752 >= 0)) {
				errorBuffer.append("<ERR:P752 一般医疗服务费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P752;
		}

		Double P754 = initParm.getDouble("P754");// 一般治疗操作费
		if (!(P754 == null || P754.equals(""))) {
			if (!(P754 >= 0)) {
				errorBuffer.append("<ERR:P754 一般治疗操作费error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P754;
		}
		Double P755 = initParm.getDouble("P755");// 护理费
		if (!(P755 == null || P755.equals(""))) {
			if (!(P755 >= 0)) {
				errorBuffer.append("<ERR:P755护理费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P755;
		}
		Double P756 = initParm.getDouble("P756");// 综合医疗服务类其它费用
		if (!(P756 == null || P756.equals(""))) {
			if (!(P756 >= 0)) {
				errorBuffer.append("<ERR:P756  综合医疗服务类其它费用 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P756;
		}
		Double P757 = initParm.getDouble("P757");// 病理诊断费
		if (!(P757 == null || P757.equals(""))) {
			if (!(P757 >= 0)) {
				errorBuffer.append("<ERR:P757  病理诊断费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P757;
		}
		Double P758 = initParm.getDouble("P758");// 实验室诊断费
		if (!(P758 == null || P758.equals(""))) {
			if (!(P758 >= 0)) {
				errorBuffer.append("<ERR:P758  实验室诊断费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P758;
		}
		Double P759 = initParm.getDouble("P759");// 影像学诊断费
		if (!(P759 == null || P759.equals(""))) {
			if (!(P759 >= 0)) {
				errorBuffer.append("<ERR:P759 影像学诊断费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P759;
		}

		Double P760 = initParm.getDouble("P760");// 临床诊断项目费
		if (!(P760 == null || P760.equals(""))) {
			if (!(P760 >= 0)) {
				errorBuffer.append("<ERR:P760 临床诊断项目费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P760;
		}
		Double P761 = initParm.getDouble("P761");// 非手术治疗项目费
		if (!(P761 == null || P761.equals(""))) {
			if (!(P761 >= 0)) {
				errorBuffer.append("<ERR:P761 非手术治疗项目费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P761;
		}
		Double P762 = initParm.getDouble("P762");// 临床物理治疗费
		if (!(P762 == null || P762.equals(""))) {
			if (!(P762 >= 0)) {
				errorBuffer.append("<ERR:P762 临床物理治疗费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P762);
		}
		Double P763 = initParm.getDouble("P763");// 手术治疗费
		if (!(P763 == null || P763.equals(""))) {
			if (!(P763 >= 0)) {
				errorBuffer.append("<ERR:P763 手术治疗费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P763;
		}
		Double P764 = initParm.getDouble("P764");// 麻醉费
		if (!(P764 == null || P764.equals(""))) {
			if (!(P764 >= 0)) {
				errorBuffer.append("<ERR:P764  麻醉费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P764);
		}
		Double P765 = initParm.getDouble("P765");// 手术费
		if (!(P765 == null || P765.equals(""))) {
			if (!(P765 >= 0)) {
				errorBuffer.append("<ERR:P765  手术费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P765);
		}

		Double P767 = initParm.getDouble("P767");// 康复费
		if (!(P767 == null || P767.equals(""))) {
			if (!(P767 >= 0)) {
				errorBuffer.append("<ERR:P767  康复费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P767;
		}
		Double P768 = initParm.getDouble("P768");// 中医治疗费
		if (!(P768 == null || P768.equals(""))) {
			if (!(P768 >= 0)) {
				errorBuffer.append("<ERR:P768 中医治疗费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P768;
		}
		Double P769 = initParm.getDouble("P769");// 西药费
		if (!(P769 == null || P769.equals(""))) {
			if (!(P769 >= 0)) {
				errorBuffer.append("<ERR:P769  西药费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P769;
		}
		Double P770 = initParm.getDouble("P770");// 抗菌药物费用
		if (!(P770 == null || P770.equals(""))) {
			if (!(P770 >= 0)) {
				errorBuffer.append("<ERR:P770  抗菌药物费用 error22 " + this.error22Msg
						+ " >\r\n");
			}
			// sums += Double.parseDouble(P770);
		}
		Double P771 = initParm.getDouble("P771");// 中成药费
		if (!(P771 == null || P771.equals(""))) {
			if (!(P771 >= 0)) {
				errorBuffer.append("<ERR:P771  中成药费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P771;
		}
		Double P772 = initParm.getDouble("P772");// 中草药费
		if (!(P772 == null || P772.equals(""))) {
			if (!(P772 >= 0)) {
				errorBuffer.append("<ERR:P772  中草药费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P772;
		}
		Double P773 = initParm.getDouble("P773");// 血费
		if (!(P773 == null || P773.equals(""))) {
			if (!(P773 >= 0)) {
				errorBuffer.append("<ERR:P773  血费  error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P773;
		}
		Double P774 = initParm.getDouble("P774");// 白蛋白类制品费
		if (!(P774 == null || P774.equals(""))) {
			if (!(P774 >= 0)) {
				errorBuffer.append("<ERR:P774 白蛋白类制品费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P774;
		}
		Double P775 = initParm.getDouble("P775");// 球蛋白类制品费
		if (!(P775 == null || P775.equals(""))) {
			if (!(P775 >= 0)) {
				errorBuffer.append("<ERR:P775  球蛋白类制品费  error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P775;
		}
		Double P776 = initParm.getDouble("P776");// 凝血因子类制品费
		if (!(P776 == null || P776.equals(""))) {
			if (!(P776 >= 0)) {
				errorBuffer.append("<ERR:P776 凝血因子类制品费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P776;
		}
		Double P777 = initParm.getDouble("P777");// 细胞因子类制品费
		if (!(P777 == null || P777.equals(""))) {
			if (!(P777 >= 0)) {
				errorBuffer.append("<ERR:P777  细胞因子类制品费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P777;
		}
		Double P778 = initParm.getDouble("P778");// 检查一次性医用材料费
		if (!(P778 == null || P778.equals(""))) {
			if (!(P778 >= 0)) {
				errorBuffer.append("<ERR:P778  检查一次性医用材料费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P778;
		}
		Double P779 = initParm.getDouble("P779");// 治疗一次性医用才来费
		if (!(P779 == null || P779.equals(""))) {
			if (!(P779 >= 0)) {
				errorBuffer.append("<ERR:P779 治疗一次性医用才来费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P779;
		}
		Double P780 = initParm.getDouble("P780");// 手术用一次性医用材料费
		if (!(P780 == null || P780.equals(""))) {
			if (!(P780 >= 0)) {
				errorBuffer.append("<ERR:P780 手术用一次性医用材料费 error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P780;
		}
		Double P781 = initParm.getDouble("P781");// 其他费
		if (!(P781 == null || P781.equals(""))) {
			if (!(P781 >= 0)) {
				errorBuffer.append("<ERR:P781 其他费  error22 " + this.error22Msg
						+ " >\r\n");
			}
			sums += P781;
		}
		// 第八步：总费用=各项费用之和
		// 住院总费用 P782
		double sum = P782;
		if (sum != StringTool.round(sums, 2)) {
			errorBuffer.append("<ERR:P782 住院总费用 error21 " + this.error21Msg + " >\r\n");
		}
		// 第九步：实际住院>=特护+一级护理+二级护理+三级护理
		Integer admDays = P27;
		Integer specialDays = initParm.getInt("P561");
		Integer firstDays = initParm.getInt("P562");
		Integer secondDays = initParm.getInt("P563");
		Integer thirdDays = initParm.getInt("P564");
		if (admDays != null && !admDays.equals("") && specialDays != null
				&& !specialDays.equals("") && firstDays != null
				&& !firstDays.equals("") && secondDays != null
				&& !secondDays.equals("") && thirdDays != null
				&& !thirdDays.equals("")) {
			if (!(admDays >= (firstDays + secondDays + thirdDays))) {
				errorBuffer.append("<ERR:P561,P562,P563,P564 error16 "
						+ this.error16Msg + " >\r\n");
			}
		}
		// 诊断治疗结果代码为‘4’，则离院方式为‘5’
		String P323 = initParm.getValue("P323");// 主要诊断出院情况
		String P326 = initParm.getValue("P326");//其他诊断1出院情况
		String P329 = initParm.getValue("P329");//2
		String P3293 = initParm.getValue("P3293");//3
		String P3296 = initParm.getValue("P3296");//4
		String P3299 = initParm.getValue("P3299");//5
		String P3283 = initParm.getValue("P3283");//6
		String P3286 = initParm.getValue("P3286");//7
		String P3289 = initParm.getValue("P3289");//8
		String P3273 = initParm.getValue("P3273");//9
		String P3276 = initParm.getValue("P3276");//10
		Integer P421 = initParm.getInt("P421");
		Integer P422 = initParm.getInt("P422");
		if (P323.equals("4")||P326.equals("4")||P329.equals("4")||P3293.equals("4")||P3296.equals("4")||P3299.equals("4")
				||P3283.equals("4")||P3286.equals("4")||P3289.equals("4")||P3273.equals("4")||P3276.equals("4")||P741.equals("5")) {
			// if (initParm.getValue("P741") == null
			// || !(initParm.getValue("P741").equals("5"))) {
			// errorBuffer.append("<ERR:P741 error5 "+this.error5Msg+" >\r\n");
			// }
			// 抢救次数=抢救成功次数或抢救次数=抢救成功次数+1
			if (P421 != null && !P421.equals("") && P422 != null
					&& !P422.equals("")) {
				if (!(P421 == P422 + 1)&&P421!= P422) {
					errorBuffer.append("<ERR:死亡时：抢救次数 = 抢救成功次数+1 P421, P422 error15 "
							+ this.error15Msg + " >\r\n");
				}
			}

		} else {
			// 抢救次数 = 抢救成功次数
			if (P421 != null && !P421.equals("") && P422 != null
					&& !P422.equals("")) {
				if (P421!= P422) {
					errorBuffer.append("<ERR:P421抢救次数 , P422抢救成功次数  error15 "
							+ this.error15Msg + " >\r\n");
				}
			}
		}
		// 身份证号码验证
		String P13 = initParm.getValue("P13");
		boolean flg = true;
		if (P13 != null && !(P13.toString().equals(""))) {
			STAIdcardValidator idcard = new STAIdcardValidator();
			flg = idcard.isValidatedAllIdcard(P13.toString());
			if (!flg) {
				errorBuffer.append("<ERR:P13 身份证号码不合法 >\r\n");
			}
		}
		// p782≥p751
		Double P751 = initParm.getDouble("P751");
		if (P782 < P751) {
			errorBuffer.append("<ERR:error 22 住院总费用/住院总费用其中自付金额     >\r\n");
		}

		// p761≥p762
		if (P761 < P762) {
			errorBuffer.append("<ERR: error23  非手术治疗项目费/临床物理治疗费     >\r\n");
		}

		// p763≥p764+p765
		if (P763 < P764 + P765) {
			errorBuffer.append("<ERR: error24  手术治疗费/麻醉费/手术费     >\r\n");
		}

		// p769≥p770
		if (P769 < P770) {
			errorBuffer.append("<ERR: error25  西药类：西药费/抗菌药物费用     >\r\n");
		}

		// 损伤和中毒外部原因编码(ICD-10)
		// ICD-10诊断编码范围：V、W、X、Y开头的
		P361 = initParm.getValue("P361");
		if (P361 != null && !P361.equals("")) {
			if (!(StringTool.compareTo(P361.substring(0, 1), "V") >= 0 && StringTool
					.compareTo(P361.substring(0, 1), "Y") <= 0)) {
				errorBuffer
						.append("<ERR: P361损伤诊断1 error 13  损伤和中毒外部原因编码(ICD-10) 诊断编码范围：V、W、X、Y开头的    >\r\n");
			}
		}
		P363 = initParm.getValue("P363");
		if (P363 != null && !P363.equals("")) {
			if (!(StringTool.compareTo(P363.substring(0, 1), "V") >= 0 && StringTool
					.compareTo(P363.substring(0, 1), "Y") <= 0)) {
				errorBuffer
						.append("<ERR:P363损伤诊断2 error 13  损伤和中毒外部原因编码(ICD-10) 诊断编码范围：V、W、X、Y开头的    >\r\n");
			}
		}
		P365 = initParm.getValue("P365");
		if (P365 != null && !P365.equals("")) {
			if (!(StringTool.compareTo(P365.substring(0, 1), "V") >= 0 && StringTool
					.compareTo(P365.substring(0, 1), "Y") <= 0)) {
				errorBuffer
						.append("<ERR:P365损伤诊断3 error 13  损伤和中毒外部原因编码(ICD-10) 诊断编码范围：V、W、X、Y开头的    >\r\n");
			}
		}
		// 入院日期时间≤质控日期
		String P47 = initParm.getValue("P47");
		if (P22 != null && !P22.equals("") && P47 != null && !P47.equals("")) {
			if (!this.dateCompare(P22, P47)) {
				errorBuffer.append("<ERR:P47 质控日期 error27  入院日期时间≤质控日期 >\r\n");
			}
			Timestamp Tp47=initParm.getTimestamp("P47");
			Timestamp now=SystemTool.getInstance().getDate();
			if(Tp47.getTime()>now.getTime()){
				errorBuffer.append("<ERR:P47 质控日期      质控日期 >当前日期\r\n");
			}
		}
		// 当主要诊断或者其它诊断编码出现分娩方式编码O80-O84，且流产结局的编码O00-O08不与分娩方式编码并存时，则必须有分娩结局的编码Z37
		// P324，P3291
		if (P324 != null && !P324.equals("")) {
			if (StringTool.compareTo(P324.substring(0, 3), "O80") > 0
					&& StringTool.compareTo("O84", P324.substring(0, 3)) > 0) {

			}
		}
		if (errorBuffer.toString().length() > 0) {
			result.setErrCode(-100);
			result.setErrText(errorBuffer.toString());
		}
		return result;
	}
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private Timestamp getFormateDate(Timestamp a, String b) {
		if (a == null) {
			return a;
		}
		Df = new SimpleDateFormat(b);
		String dfStr = Df.format(a);
		Timestamp redate = StringTool.getTimestamp(dfStr, b);
		return redate;
	}
	/**
	 * 两个日期的比较方法 date1<=date2 返回 true
	 * */
	public boolean dateCompare(String date1, String date2) {
		boolean flg = false;
		if (date1.length() > 19) {
			date1 = date1.substring(0, 18);
		}
		if (date2.length() > 19) {
			date2 = date2.substring(0, 18);
		}
		date1 = date1.replace("-", "/");
		date2 = date2.replace("-", "/");
		int count = StringTool.compareTo(date1, date2);
		if (count <= 0) {
			flg = true;
			return flg;
		}
		return flg;
	}

	/**
	 * 验证是否是数字
	 * */
	public boolean isNumber(String str) {
		boolean flg = true;
		str = str.trim();
		if (str == null || str.trim() == "") {
			flg = false;
			return flg;
		}

		flg = str.matches(("^[0-9_]+$"));
		return flg;
	}

	/**
	 * 校验是否是不超过n位的数字
	 * */
	public boolean isNumberN(String str, int n) {
		boolean flg = true;
		str = str.trim();
		if (str == null || str.trim() == "") {
			flg = false;
			return flg;
		}

		flg = str.matches(("^[0-9_]+$"));
		if (flg) {
			if (str.length() > n) {
				flg = false;
			}
		}
		return flg;
	}

	/**
	 * 根据性别验证疾病诊断ICD10编码 男性：1 女性：2
	 * */
	public boolean checkICD10(int i, String str) {

		boolean flg = true;
		// 男性
		if (i == 1) {
			for (String icd10 : ICD10ForFemail) {
				int j = icd10.length();
				if (str.length() >= j) {
					String str1 = str.substring(0, j);
					if (str1.equals(icd10)) {
						flg = false;
					}
				}
			}
			if (StringTool.compareTo(str, "O00") >=0
					&& StringTool.compareTo("O99", str) >=0) {
				flg = false;
			}
		}
		// 女性
		if (i == 2) {
			for (String icd10 : ICD10ForMail) {
				int j = icd10.length();
				if (str.length() >= j) {
					String str1 = str.substring(0, j);
					if (str1.equals(icd10)) {
						flg = false;
					}
				}
			}
		}
		return flg;
	}

	// /**
	// * 根据性别验证手术编码 男性：1 女性：2
	// * */
	// public boolean checkOperatorCode(int i, String str) {
	// boolean flg = true;
	// if (i == 1) {
	// for (String operator : this.operatorCodeForFemail) {
	// int length = operator.length();
	// String str1 = str.substring(0,length);
	// if (str1.equals(operator)) {
	// flg = false;
	// }
	// }
	// }
	// if (i == 2) {
	// for (String operator : this.operatorCodeForMail) {
	// int length = operator.length();
	// String str1 = str.substring(0,length);
	// if (str1.equals(operator)) {
	// flg = false;
	// }
	// }
	// }
	// return flg;
	// }

	/***
	 * 
	 * 门(急)诊诊断编码(ICD-10)、入院诊断编码(ICD-10)、出院时主要诊断编码(ICD-10)、
	 * 出院时其它诊断编码(ICD-10)、医院感染名称编码(ICD-10)各项编码范围应为：
	 * A00-U99；Z00-Z99；不包括字母V、W、X、Y开头的编码  M80000/0-M99999/6
	 * */
	public boolean checkICD10Other(int i, String str) {

		boolean flg = true;
		String PDStart="M80000/0";
		String PDEnd="M99999/6";
		if(str.length()==PDStart.length()&&str.contains("/")){
			if(StringTool.compareTo(str, PDStart) > 0&&StringTool.compareTo(PDEnd, str)>0){
				flg = false;
			}
		}
		str = str.substring(0, 1);
		if (!((StringTool.compareTo(str, "A")>=0 && StringTool.compareTo(str,
				"U")<=0) || str.equals("Z"))) {
			flg = false;
		}
		return flg;

	}

}