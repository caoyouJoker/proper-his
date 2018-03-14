package jdo.inf;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

import jdo.sys.SystemTool;

import com.dongyang.db.TConnection;

/**
 * <p>
 * Title:医务人员体液血液登记表
 * </p>
 * 
 * <p>
 * Description: 医务人员血液体液登记表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 20170509
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author yanmm 2017/5/12
 * @version 5.0
 */
public class INFMedicalInTool extends TJDOTool {

	/**
	 * 构造器
	 */
	public INFMedicalInTool() {
		onInit();
	}

	private static INFMedicalInTool instanceObject;

	public static INFMedicalInTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INFMedicalInTool();
		return instanceObject;
	}

	/**
	 * 获取数据
	 */
	public TParm getUiDate(TParm parm) {
		TParm result = new TParm();
		TParm ParmKey = new TParm();
		TParm ParmValue = new TParm();
		TParm ParmSet = new TParm();
		String key = "";
		String value = "";
		String set = "";
		// "to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd hh24:mi:ss'),"
		// this.messageBox("TOOL获取界面数据???");
		// 发生日期
		if (parm.getValue("HAPE_DATE").length() > 0) {
			key = key + ",HAPE_DATE";
			value = value + ",to_date('" + parm.getValue("HAPE_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",HAPE_DATE=to_date('" + parm.getValue("HAPE_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";

		}
		// 报告日期
		if (parm.getValue("REPORT_DATE").length() > 0) {
			key = key + ",REPORT_DATE";
			value = value + ",to_date('" + parm.getValue("REPORT_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",REPORT_DATE=to_date('" + parm.getValue("REPORT_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";

		}
		// 姓名
		if (parm.getValue("USER_NAME").length() > 0) {
			// ParmValue.setData("PAT_NAME", parm.getValue("PAT_NAME"));
			key = key + ",USER_NAME";
			value = value + ",'" + parm.getValue("USER_NAME") + "'";
			set = set + ",USER_NAME='" + parm.getValue("USER_NAME") + "'";
			// this.messageBox("姓名"+ParmValue.getValue("PAT_NAME"));
		}
		// 性别
		if (parm.getValue("SEX_CODE").length() > 0) {
			// ParmValue.setData("SEX_CODE", parm.getValue("SEX_CODE"));
			key = key + ",SEX_CODE";
			value = value + ",'" + parm.getValue("SEX_CODE") + "'";
			set = set + ",SEX_CODE='" + parm.getValue("SEX_CODE") + "'";
			// this.messageBox("性别"+ParmValue.getValue("SEX_CODE"));
		}
		// 科室
		if (parm.getValue("DEPT_CODE").length() > 0) {
			// ParmValue.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
			key = key + ",DEPT_CODE";
			value = value + ",'" + parm.getValue("DEPT_CODE") + "'";
			set = set + ",DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
			// this.messageBox("科室"+ParmValue.getValue("DEPT_CODE"));
		}
		// 电话
		if (parm.getValue("TEL1").length() > 0) {
			// ParmValue.setData("CELL_PHONE", parm.getValue("CELL_PHONE"));
			key = key + ",TEL1";
			value = value + ",'" + parm.getValue("TEL1") + "'";
			set = set + ",TEL1='" + parm.getValue("TEL1") + "'";
			// this.messageBox("电话"+ParmValue.getValue("CELL_PHONE"));
		}
		// 工龄
		if (parm.getValue("WORKING_YEARS").length() > 0) {
			// ParmValue.setData("WORKING_YEARS",
			// parm.getValue("WORKING_YEARS"));
			key = key + ",WORKING_YEARS";
			value = value + ",'" + parm.getValue("WORKING_YEARS") + "'";
			set = set + ",WORKING_YEARS='" + parm.getValue("WORKING_YEARS")
					+ "'";
			// this.messageBox("工龄"+ParmValue.getValue("WORKING_YEARS"));
		}
		// 患者病案号
		if (parm.getValue("MR_NO").length() > 0) {
			// ParmValue.setData("PATIENT_MR_NO",
			// parm.getValue("PATIENT_MR_NO"));
			key = key + ",MR_NO";
			value = value + ",'" + parm.getValue("MR_NO") + "'";
			set = set + ",MR_NO='" + parm.getValue("MR_NO") + "'";
			// this.messageBox("患者病案号"+ParmValue.getValue("PATIENT_MR_NO"));
		}
		// 病患名称
		if (parm.getValue("PATIENT_NAME").length() > 0) {
			key = key + ",PATIENT_NAME";
			value = value + ",'" + parm.getValue("PATIENT_NAME") + "'";
			set = set + ",PATIENT_NAME=" + parm.getValue("PATIENT_NAME") + "'";
		}
		// 患者科室
		if (parm.getValue("PATIENT_DEPT_CODE").length() > 0) {
			// ParmValue.setData("PATIENT_DEPT_CODE",
			// parm.getValue("PATIENT_DEPT_CODE"));
			key = key + ",PATIENT_DEPT_CODE";
			value = value + ",'" + parm.getValue("PATIENT_DEPT_CODE") + "'";
			set = set + ",PATIENT_DEPT_CODE='"
					+ parm.getValue("PATIENT_DEPT_CODE") + "'";
			// this.messageBox("患者科室"+ParmValue.getValue("PATIENT_DEPT_CODE"));
		}
		// 首次检验日期
		if (parm.getValue("SPLASH_F_DATE").length() > 0) {
			// ParmValue.setData("FIRST_INSPECTION_DATE",
			// parm.getValue("FIRST_INSPECTION_DATE"));
			key = key + ",SPLASH_F_DATE";
			value = value + ",to_date('" + parm.getValue("SPLASH_F_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",SPLASH_F_DATE=to_date('"
					+ parm.getValue("SPLASH_F_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			// this.messageBox("首次检验日期"+ParmValue.getValue("FIRST_INSPECTION_DATE"));
		}
		// 患者传筛情况
		if (parm.getValue("INFECT_SCR_RESULT").length() > 0) {
			key = key + ",INFECT_SCR_RESULT";
			value = value + ",'" + parm.getValue("INFECT_SCR_RESULT") + "'";
			set = set + ",INFECT_SCR_RESULT='"
					+ parm.getValue("INFECT_SCR_RESULT") + "'";
		}
		// ANTIHIV
		if (parm.getValue("ANTIHIV").length() > 0) {
			// ParmValue.setData("ANTIHIV", parm.getValue("ANTIHIV"));
			key = key + ",ANTIHIV";
			value = value + ",'" + parm.getValue("ANTIHIV") + "'";
			set = set + ",ANTIHIV='" + parm.getValue("ANTIHIV") + "'";
			// this.messageBox("ANTIHIV"+ParmValue.getValue("ANTIHIV"));
		}
		// HBSAG
		if (parm.getValue("HBSAG").length() > 0) {
			// ParmValue.setData("HBSAG", parm.getValue("HBSAG"));
			key = key + ",HBSAG";
			value = value + ",'" + parm.getValue("HBSAG") + "'";
			set = set + ",HBSAG='" + parm.getValue("HBSAG") + "'";
			// this.messageBox("HBSAG"+ParmValue.getValue("HBSAG"));
		}
		// ANTIHBS
		if (parm.getValue("ANTIHBS").length() > 0) {
			// ParmValue.setData("ANTIHBS", parm.getValue("ANTIHBS"));
			key = key + ",ANTIHBS";
			value = value + ",'" + parm.getValue("ANTIHBS") + "'";
			set = set + ",ANTIHBS='" + parm.getValue("ANTIHBS") + "'";
			// this.messageBox("ANTIHBS"+ParmValue.getValue("ANTIHBS"));
		}
		// ANTIHCV
		if (parm.getValue("ANTIHCV").length() > 0) {
			// ParmValue.setData("ANTIHCV", parm.getValue("ANTIHCV"));
			key = key + ",ANTIHCV";
			value = value + ",'" + parm.getValue("ANTIHCV") + "'";
			set = set + ",ANTIHCV='" + parm.getValue("ANTIHCV") + "'";
			// this.messageBox("ANTIHCV"+ParmValue.getValue("ANTIHCV"));
		}
		// VDRL
		if (parm.getValue("VDRL").length() > 0) {
			// ParmValue.setData("VDRL", parm.getValue("VDRL"));
			key = key + ",VDRL";
			value = value + ",'" + parm.getValue("VDRL") + "'";
			set = set + ",VDRL='" + parm.getValue("VDRL") + "'";
			// this.messageBox("VDRL"+ParmValue.getValue("VDRL"));
		}
		// PATIENT_ANTIHIV
		if (parm.getValue("PATIENT_ANTIHIV").length() > 0) {
			// ParmValue.setData("PATIENT_ANTIHIV",
			// parm.getValue("PATIENT_ANTIHIV"));
			key = key + ",PATIENT_ANTIHIV";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHIV") + "'";
			set = set + ",PATIENT_ANTIHIV='" + parm.getValue("PATIENT_ANTIHIV")
					+ "'";
			// this.messageBox("PATIENT_ANTIHIV"+ParmValue.getValue("PATIENT_ANTIHIV"));
		}
		// PATIENT_HBSAG
		if (parm.getValue("PATIENT_HBSAG").length() > 0) {
			// ParmValue.setData("PATIENT_HBSAG",
			// parm.getValue("PATIENT_HBSAG"));
			key = key + ",PATIENT_HBSAG";
			value = value + ",'" + parm.getValue("PATIENT_HBSAG") + "'";
			set = set + ",PATIENT_HBSAG='" + parm.getValue("PATIENT_HBSAG")
					+ "'";
			// this.messageBox("PATIENT_HBSAG"+ParmValue.getValue("PATIENT_HBSAG"));
		}
		// PATIENT_ANTIHBS
		if (parm.getValue("PATIENT_ANTIHBS").length() > 0) {
			// ParmValue.setData("PATIENT_ANTIHBS",
			// parm.getValue("PATIENT_ANTIHBS"));
			key = key + ",PATIENT_ANTIHBS";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHBS") + "'";
			set = set + ",PATIENT_ANTIHBS='" + parm.getValue("PATIENT_ANTIHBS")
					+ "'";
			// this.messageBox("PATIENT_ANTIHBS"+ParmValue.getValue("PATIENT_ANTIHBS"));
		}
		// PATIENT_ANTIHCV
		if (parm.getValue("PATIENT_ANTIHCV").length() > 0) {
			// ParmValue.setData("PATIENT_ANTIHCV",
			// parm.getValue("PATIENT_ANTIHCV"));
			key = key + ",PATIENT_ANTIHCV";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHCV") + "'";
			set = set + ",PATIENT_ANTIHCV='" + parm.getValue("PATIENT_ANTIHCV")
					+ "'";
			// this.messageBox("PATIENT_ANTIHCV"+ParmValue.getValue("PATIENT_ANTIHCV"));
		}
		// PATIENT_VDRL
		if (parm.getValue("PATIENT_VDRL").length() > 0) {
			// ParmValue.setData("PATIENT_VDRL", parm.getValue("PATIENT_VDRL"));
			key = key + ",PATIENT_VDRL";
			value = value + ",'" + parm.getValue("PATIENT_VDRL") + "'";
			set = set + ",PATIENT_VDRL='" + parm.getValue("PATIENT_VDRL") + "'";
			// this.messageBox("PATIENT_VDRL"+ParmValue.getValue("PATIENT_VDRL"));
		}
		// 喷溅物其他
		if (parm.getValue("SPLASH_OTHER_TEXT").length() > 0) {
			// ParmValue.setData("OTHER_TYPE_DESCRIBE",
			// parm.getValue("OTHER_TYPE_DESCRIBE"));
			key = key + ",SPLASH_OTHER_TEXT";
			value = value + ",'" + parm.getValue("SPLASH_OTHER_TEXT") + "'";
			set = set + ",SPLASH_OTHER_TEXT='"
					+ parm.getValue("SPLASH_OTHER_TEXT") + "'";
			// this.messageBox("尖锐物品类 其他描述"+Parm.getValue("OTHER_TYPE_DESCRIBE"));
		}
		// 暴露其他备注
		if (parm.getValue("EXPOSED_OTHER_TEXT").length() > 0) {
			// ParmValue.setData("OTHER_OPERATION_DESCRIBE",
			// parm.getValue("OTHER_OPERATION_DESCRIBE"));
			key = key + ",EXPOSED_OTHER_TEXT";
			value = value + ",'" + parm.getValue("EXPOSED_OTHER_TEXT") + "'";
			set = set + ",EXPOSED_OTHER_TEXT='"
					+ parm.getValue("EXPOSED_OTHER_TEXT") + "'";
			// this.messageBox("锐器伤时的操作 其他描述"+Parm.getValue("OTHER_OPERATION_DESCRIBE"));
		}
		// 动作其他备注
		if (parm.getValue("EXPOSED_ACTION_TEXT").length() > 0) {
			// ParmValue.setData("OTHER_ACTION_DESCRIBE",
			// parm.getValue("OTHER_ACTION_DESCRIBE"));
			key = key + ",EXPOSED_ACTION_TEXT";
			value = value + ",'" + parm.getValue("EXPOSED_ACTION_TEXT") + "'";
			set = set + ",EXPOSED_ACTION_TEXT='"
					+ parm.getValue("EXPOSED_ACTION_TEXT") + "'";
			// this.messageBox("锐器伤时的动作 其他描述"+ParmValue.getValue("OTHER_ACTION_DESCRIBE"));
		}
		// 穿戴其他备注
		if (parm.getValue("PROTECT_OTHER_TEXT").length() > 0) {
			// Parm.setData("PLACE_INJURY", this.getValue("PLACE_INJURY"));
			key = key + ",PROTECT_OTHER_TEXT";
			value = value + ",'" + parm.getValue("PROTECT_OTHER_TEXT") + "'";
			set = set + ",PROTECT_OTHER_TEXT='"
					+ parm.getValue("PROTECT_OTHER_TEXT") + "'";
			// this.messageBox("锐器伤发生地点"+Parm.getValue("PLACE_INJURY"));
		}
		// 喷溅不为
		if (parm.getValue("SPLASH_PLACE").length() > 0) {
			// Parm.setData("PLACE_INJURY", this.getValue("PLACE_INJURY"));
			key = key + ",SPLASH_PLACE";
			value = value + ",'" + parm.getValue("SPLASH_PLACE") + "'";
			set = set + ",SPLASH_PLACE='" + parm.getValue("SPLASH_PLACE") + "'";
			// this.messageBox("锐器伤发生地点"+Parm.getValue("PLACE_INJURY"));
		}

		// 喷溅发生地点
		if (parm.getValue("HAPPEN_PLACE").length() > 0) {
			// Parm.setData("POSITION_INJURY",
			// this.getValue("POSITION_INJURY"));
			key = key + ",HAPPEN_PLACE";
			value = value + ",'" + parm.getValue("HAPPEN_PLACE") + "'";
			set = set + ",HAPPEN_PLACE='" + parm.getValue("HAPPEN_PLACE") + "'";
			// this.messageBox("锐器伤发生部位"+Parm.getValue("POSITION_INJURY"));
		}
		// 喷溅体液是否受血液及体液污染
		if (parm.getValue("BLOOD_INFECTED").length() > 0) {
			key = key + ",BLOOD_INFECTED";
			value = value + ",'" + parm.getValue("BLOOD_INFECTED") + "'";
			set = set + ",BLOOD_INFECTED='" + parm.getValue("BLOOD_INFECTED")
					+ "'";
		}
		// 大约多少血 体液接触皮肤
		if (parm.getValue("BLOOD_CONTACT").length() > 0) {
			key = key + ",BLOOD_CONTACT";
			value = value + ",'" + parm.getValue("BLOOD_CONTACT") + "'";
			set = set + ",BLOOD_CONTACT='" + parm.getValue("BLOOD_CONTACT")
					+ "'";
		}
		// 曾经喷溅次数
		if (parm.getValue("SPLASH_MANY").length() > 0) {
			key = key + ",SPLASH_MANY";
			value = value + ",'" + parm.getValue("SPLASH_MANY") + "'";
			set = set + ",SPLASH_MANY='" + parm.getValue("SPLASH_MANY") + "'";
		}
		// 喷溅次数
		if (parm.getValue("NUM_FIRST").length() > 0) {
			// ParmValue.setData("INJURY_FREQUENCY",
			// parm.getValue("INJURY_FREQUENCY"));
			key = key + ",NUM_FIRST";
			value = value + ",'" + parm.getValue("NUM_FIRST") + "'";
			set = set + ",NUM_FIRST='" + parm.getValue("NUM_FIRST") + "'";
			// this.messageBox("受伤次数总共"+ParmValue.getValue("INJURY_FREQUENCY"));
		}
		// 喷溅后处理
		if (parm.getValue("SPLASH_DO").length() > 0) {
			key = key + ",SPLASH_DO";
			value = value + ",'" + parm.getValue("SPLASH_DO") + "'";
			set = set + ",SPLASH_DO='" + parm.getValue("SPLASH_DO") + "'";
		}
		// 部门负责人
		if (parm.getValue("DEPARTMENT_HEADS").length() > 0) {
			// ParmValue.setData("DEPARTMENT_HEADS",
			// parm.getValue("DEPARTMENT_HEADS"));
			key = key + ",DEPARTMENT_HEADS";
			value = value + ",'" + parm.getValue("DEPARTMENT_HEADS") + "'";
			set = set + ",DEPARTMENT_HEADS='"
					+ parm.getValue("DEPARTMENT_HEADS") + "'";
			// this.messageBox("部门负责人"+ParmValue.getValue("DEPARTMENT_HEADS"));
		}
		// 部门负责人签字时间
		if (parm.getValue("DEPARTMENT_DATE").length() > 0) {
			// ParmValue.setData("DEPARTMENT_DATE",
			// parm.getValue("DEPARTMENT_DATE"));
			key = key + ",DEPARTMENT_DATE";
			value = value + ",to_date('" + parm.getValue("DEPARTMENT_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",DEPARTMENT_DATE=to_date('"
					+ parm.getValue("DEPARTMENT_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			// this.messageBox("部门负责人签字时间"+ParmValue.getValue("DEPARTMENT_DATE"));
		}
		// 预防保健科主任
		if (parm.getValue("PREVENTION_HEADS").length() > 0) {
			// ParmValue.setData("PREVENTION_HEADS",
			// parm.getValue("PREVENTION_HEADS"));
			key = key + ",PREVENTION_HEADS";
			value = value + ",'" + parm.getValue("PREVENTION_HEADS") + "'";
			set = set + ",PREVENTION_HEADS='"
					+ parm.getValue("PREVENTION_HEADS") + "'";
			// this.messageBox("预防保健科主任"+ParmValue.getValue("PREVENTION_HEADS"));
		}
		// 预防保健科主任签字时间
		if (parm.getValue("PREVENTION_DATE").length() > 0) {
			// ParmValue.setData("PREVENTION_DATE",
			// parm.getValue("PREVENTION_DATE"));
			key = key + ",PREVENTION_DATE";
			value = value + ",to_date('" + parm.getValue("PREVENTION_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",PREVENTION_DATE=to_date('"
					+ parm.getValue("PREVENTION_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			// this.messageBox("预防保健科主任签字时间"+ParmValue.getValue("PREVENTION_DATE"));
		}
		// 医院感染管理科主任
		if (parm.getValue("INFECTED_HEADS").length() > 0) {
			// ParmValue.setData("INFECTED_HEADS",
			// parm.getValue("INFECTED_HEADS"));
			key = key + ",INFECTED_HEADS";
			value = value + ",'" + parm.getValue("INFECTED_HEADS") + "'";
			set = set + ",INFECTED_HEADS='" + parm.getValue("INFECTED_HEADS")
					+ "'";
			// this.messageBox("医院感染管理科主任"+ParmValue.getValue("INFECTED_HEADS"));
		}
		// 医院感染管理科主任签字时间
		if (parm.getValue("INFECTED_DATE").length() > 0) {
			// ParmValue.setData("INFECTED_DATE",
			// parm.getValue("INFECTED_DATE"));
			key = key + ",INFECTED_DATE";
			value = value + ",to_date('" + parm.getValue("INFECTED_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",INFECTED_DATE=to_date('"
					+ parm.getValue("INFECTED_DATE")
					+ "','yyyy-mm-dd hh24:mi:ss')";
			// this.messageBox("医院感染管理科主任签字时间"+ParmValue.getValue("INFECTED_DATE"));
		}
		// 年龄
		if (parm.getValue("AGE").length() > 0) {
			// ParmValue.setData("AGE", parm.getValue("AGE"));
			key = key + ",AGE";
			value = value + ",'" + parm.getValue("AGE") + "'";
			set = set + ",AGE='" + parm.getValue("AGE") + "'";
			// this.messageBox("年龄"+ParmValue.getValue("AGE"));
		}

		// 给parm赋值
		ParmValue.setData("value", value);
		ParmKey.setData("key", key);
		ParmSet.setData("set", set);

		// 给parm赋值
		result.setData("ParmValue", ParmValue.getData());
		result.setData("ParmKey", ParmKey.getData());
		result.setData("ParmSet", ParmSet.getData());

		return result;
	}

	/** 保存方法 */
	public TParm onNew(TParm parm, TConnection connection) {
		TParm getUiDate = getUiDate(parm);
		// 读取相关parm
		TParm ParmKey = getUiDate.getParm("ParmKey");
		TParm ParmValue = getUiDate.getParm("ParmValue");
		TParm ParmSet = getUiDate.getParm("ParmSet");
		System.out.println("进入保存方法:::::" + getUiDate);
		System.out.println("ParmKey::::::::::"
				+ ParmKey.getValue("key").toString());
		System.out.println("ParmValue::::::::::"
				+ ParmValue.getValue("value").toString());
		System.out.println("ParmSet:::::::::::::"
				+ ParmSet.getValue("set").toString());

		String sql = "INSERT INTO INF_SPLASH (USER_ID,SPLASH_NO,BLOOD,SPUTUM,URINE,"
				+ "FAECES,PUKE,PLEURAL,PERITONEAL,SALIVA,"
				+ "SPLASH_OTHER,EYE,MOUTH,NOSE,SKIN,MUCOSA,"
				+ "EXPOSED_OTHER,DRAW_BLOOD,SPUTUM_SUCTION,OPERATION,CATHETERIZATION,POUR_URINE,"
				+ "DRESSING,PUNCTURE,EXPOSED_ACTION,MASK_SURGERY,LATEX_GLOVES1,LATEX_GLOVES2,FILM_GLOVES1,"
				+ "FILM_GLOVES2,MIRROR,GLASSES,MASK_F,SURGICAL,LSOLATION,PROTECT_OTHER,EXPOSE_PLACE,INFECTION,OPT_USER,OPT_DATE,OPT_TERM "
				+ ParmKey.getValue("key").toString()
				+ ") "
				+ "VALUES ("
				+ "'"
				+ parm.getData("USER_ID")
				+ "',"
				+ "'"
				+ parm.getData("SPLASH_NO")
				+ "',"
				+ "'"
				+ parm.getData("BLOOD")
				+ "',"
				+ "'"
				+ parm.getData("SPUTUM")
				+ "',"
				+ "'"
				+ parm.getData("URINE")
				+ "',"
				+ "'"
				+ parm.getData("FAECES")
				+ "',"
				+ "'"
				+ parm.getData("PUKE")
				+ "',"
				+ "'"
				+ parm.getData("PLEURAL")
				+ "',"
				+ "'"
				+ parm.getData("PERITONEAL")
				+ "',"
				+ "'"
				+ parm.getData("SALIVA")
				+ "',"
				+ "'"
				+ parm.getData("SPLASH_OTHER")
				+ "',"
				+ "'"
				+ parm.getData("EYE")
				+ "',"
				+ "'"
				+ parm.getData("MOUTH")
				+ "',"
				+ "'"
				+ parm.getData("NOSE")
				+ "',"
				+ "'"
				+ parm.getData("SKIN")
				+ "',"
				+ "'"
				+ parm.getData("MUCOSA")
				+ "',"
				+ "'"
				+ parm.getData("EXPOSED_OTHER")
				+ "',"
				+ "'"
				+ parm.getData("DRAW_BLOOD")
				+ "',"
				+ "'"
				+ parm.getData("SPUTUM_SUCTION")
				+ "',"
				+ "'"
				+ parm.getData("OPERATION")
				+ "',"
				+ "'"
				+ parm.getData("CATHETERIZATION")
				+ "',"
				+ "'"
				+ parm.getData("POUR_URINE")
				+ "',"
				+ "'"
				+ parm.getData("DRESSING")
				+ "',"
				+ "'"
				+ parm.getData("PUNCTURE")
				+ "',"
				+ "'"
				+ parm.getData("EXPOSED_ACTION")
				+ "',"
				+ "'"
				+ parm.getData("MASK_SURGERY")
				+ "',"
				+ "'"
				+ parm.getData("LATEX_GLOVES1")
				+ "',"
				+ "'"
				+ parm.getData("LATEX_GLOVES2")
				+ "',"
				+ "'"
				+ parm.getData("FILM_GLOVES1")
				+ "',"
				+ "'"
				+ parm.getData("FILM_GLOVES2")
				+ "',"
				+ "'"
				+ parm.getData("MIRROR")
				+ "',"
				+ "'"
				+ parm.getData("GLASSES")
				+ "',"
				+ "'"
				+ parm.getData("MASK_F")
				+ "',"
				+ "'"
				+ parm.getData("SURGICAL")
				+ "',"
				+ "'"
				+ parm.getData("LSOLATION")
				+ "',"
				+ "'"
				+ parm.getData("PROTECT_OTHER")
				+ "',"
				+ "'"
				+ parm.getData("EXPOSE_PLACE")
				+ "',"
				+ "'"
				+ parm.getData("INFECTION")
				+ "',"
				+ "'"
				+ parm.getData("OPT_USER")
				+ "',"
				+ "to_date('"
				+ parm.getData("OPT_DATE")
				+ "','yyyy-mm-dd hh24:mi:ss'),"
				+ "'"
				+ parm.getData("OPT_TERM")
				+ "'"
				+ ParmValue.getValue("value").toString() + ")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		System.out.println("sqlb:" + sql);
		if (result.getErrCode() < 0)
			return result;
		return result;
	}

	/** 更新方法 */
	public TParm onUpDate(TParm parm, TConnection connection) {
		String sql = "UPDATE INF_SPLASH " + "SET USER_NAME='"
				+ parm.getValue("USER_NAME") + "'," + " USER_ID='"
				+ parm.getData("USER_ID") + "'," + " SEX_CODE='"
				+ parm.getData("SEX_CODE") + "'," + " HAPE_DATE=TO_DATE ('"
				+ parm.getData("HAPE_DATE") + "',"+" 'YYYY/MM/DD HH24:MI:SS')" +","+ " REPORT_DATE= TO_DATE ('"
				+ parm.getData("REPORT_DATE") + "',"+" 'YYYY/MM/DD HH24:MI:SS')"+"," + " AGE='"
				+ parm.getData("AGE") + "'," + " DEPT_CODE='"
				+ parm.getData("DEPT_CODE") + "'," + " TEL1='"
				+ parm.getData("TEL1") + "'," + " WORKING_YEARS='"
				+ parm.getData("WORKING_YEARS") + "'," + " MR_NO='"
				+ parm.getData("MR_NO") + "'," + " PATIENT_NAME ='"
				+ parm.getData("PATIENT_NAME") + "'," + " PATIENT_DEPT_CODE='"
				+ parm.getData("PATIENT_DEPT_CODE") + "'," + " SPLASH_F_DATE=TO_DATE ('"
				+ parm.getData("SPLASH_F_DATE") + "'," +" 'YYYY/MM/DD HH24:MI:SS')"+"," + " INFECT_SCR_RESULT='"
				+ parm.getData("INFECT_SCR_RESULT") + "',"

				+ "ANTIHIV='" + parm.getData("ANTIHIV") + "'," + "HBSAG='"
				+ parm.getData("HBSAG") + "'," + "ANTIHBS='"
				+ parm.getData("ANTIHBS") + "'," + "ANTIHCV='"
				+ parm.getData("ANTIHCV") + "'," + "VDRL='"
				+ parm.getData("VDRL") + "'," + "PATIENT_ANTIHIV='"
				+ parm.getData("PATIENT_ANTIHIV") + "'," + "PATIENT_HBSAG='"
				+ parm.getData("PATIENT_HBSAG") + "'," + "PATIENT_ANTIHBS='"
				+ parm.getData("PATIENT_ANTIHBS") + "'," + "PATIENT_ANTIHCV='"
				+ parm.getData("PATIENT_ANTIHCV") + "'," + "PATIENT_VDRL='"
				+ parm.getData("PATIENT_VDRL") + "'," + "HAPPEN_PLACE='"
				+ parm.getData("HAPPEN_PLACE") + "'," + "SPLASH_PLACE='"
				+ parm.getData("SPLASH_PLACE") + "',"

				+ "SPLASH_OTHER_TEXT='" + parm.getData("SPLASH_OTHER_TEXT")
				+ "'," + "EXPOSED_OTHER_TEXT='"
				+ parm.getData("EXPOSED_OTHER_TEXT") + "',"
				+ "EXPOSED_ACTION_TEXT='" + parm.getData("EXPOSED_ACTION_TEXT")
				+ "'," + "PROTECT_OTHER_TEXT='"
				+ parm.getData("PROTECT_OTHER_TEXT") + "'," + "BLOOD_CONTACT='"
				+ parm.getData("BLOOD_CONTACT") + "'," + "NUM_FIRST='"
				+ parm.getData("NUM_FIRST") + "'," + "SPLASH_MANY='"
				+ parm.getData("SPLASH_MANY") + "'," + "SPLASH_DO='"
				+ parm.getData("SPLASH_DO") + "',"

				+ "DEPARTMENT_HEADS='" + parm.getData("DEPARTMENT_HEADS")
				+ "'," + "DEPARTMENT_DATE=TO_DATE ('" + parm.getData("DEPARTMENT_DATE")
				+ "'," +" 'YYYY-MM-DD HH24:MI:SS')"+"," + "PREVENTION_HEADS='"
				+ parm.getData("PREVENTION_HEADS") + "'," + "PREVENTION_DATE=TO_DATE ('"
				+ parm.getData("PREVENTION_DATE") + "'," +" 'YYYY/MM/DD HH24:MI:SS')"+","+ "INFECTED_HEADS='"
				+ parm.getData("INFECTED_HEADS") + "'," + "INFECTED_DATE=TO_DATE ('"
				+ parm.getData("INFECTED_DATE") + "'," +" 'YYYY/MM/DD HH24:MI:SS')" +","
				+ "BLOOD='" + parm.getData("BLOOD") + "'," + "SPUTUM='"
				+ parm.getData("SPUTUM") + "'," + "URINE='"
				+ parm.getData("URINE") + "'," + "FAECES='"
				+ parm.getData("FAECES") + "'," + "PUKE='"
				+ parm.getData("PUKE") + "'," + "PLEURAL='"
				+ parm.getData("PLEURAL") + "'," + "PERITONEAL='"
				+ parm.getData("PERITONEAL") + "'," + "SALIVA='"
				+ parm.getData("SALIVA") + "'," + "SPLASH_OTHER='"
				+ parm.getData("SPLASH_OTHER") + "'," + "EYE='"
				+ parm.getData("EYE") + "'," + "MOUTH='"
				+ parm.getData("MOUTH") + "'," + "NOSE='"
				+ parm.getData("NOSE") + "'," + "SKIN='" + parm.getData("SKIN")
				+ "'," + "MUCOSA='" + parm.getData("MUCOSA") + "',"
				+ "EXPOSED_OTHER='" + parm.getData("EXPOSED_OTHER") + "',"
				+ "DRAW_BLOOD='" + parm.getData("DRAW_BLOOD") + "',"
				+ "SPUTUM_SUCTION='" + parm.getData("SPUTUM_SUCTION") + "',"
				+ "OPERATION='" + parm.getData("OPERATION") + "',"
				+ "CATHETERIZATION='" + parm.getData("CATHETERIZATION") + "',"
				+ "POUR_URINE='" + parm.getData("POUR_URINE") + "',"
				+ "DRESSING='" + parm.getData("DRESSING") + "'," + "PUNCTURE='"
				+ parm.getData("PUNCTURE") + "'," + "EXPOSED_ACTION='"
				+ parm.getData("EXPOSED_ACTION") + "'," + "MASK_SURGERY='"
				+ parm.getData("MASK_SURGERY") + "'," + "LATEX_GLOVES1='"
				+ parm.getData("LATEX_GLOVES1") + "'," + "LATEX_GLOVES2='"
				+ parm.getData("LATEX_GLOVES2") + "'," + "FILM_GLOVES1='"
				+ parm.getData("FILM_GLOVES1") + "'," + "FILM_GLOVES2='"
				+ parm.getData("FILM_GLOVES2") + "'," + "MIRROR='"
				+ parm.getData("MIRROR") + "'," + "GLASSES='"
				+ parm.getData("GLASSES") + "'," + "MASK_F='"
				+ parm.getData("MASK_F") + "'," + "SURGICAL='"
				+ parm.getData("SURGICAL") + "'," + "LSOLATION='"
				+ parm.getData("LSOLATION") + "'," + "PROTECT_OTHER='"
				+ parm.getData("PROTECT_OTHER") + "'," + "EXPOSE_PLACE='"
				+ parm.getData("EXPOSE_PLACE") + "'," + "INFECTION='"
				+ parm.getData("INFECTION") + "'," + "OPT_USER='"
				+ parm.getData("OPT_USER") + "'," + "OPT_DATE=SYSDATE,"
				+ "OPT_TERM='" + parm.getData("OPT_TERM") + "' "
				+ "WHERE SPLASH_NO='" + parm.getData("SPLASH_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0)
			return result;
		return result;
	}

}
