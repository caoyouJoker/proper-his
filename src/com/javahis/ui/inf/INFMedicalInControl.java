package com.javahis.ui.inf;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.seraph.antlr.expression.ExpressionParser.object_return;

import jdo.inf.INFCaseTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:医务人员体液血液登记表
 * </p>
 * 
 * <p>
 * Dription:医务人员体液血液登记表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author yanmm 2017/5/12
 * @version 5.0
 */
public class INFMedicalInControl extends TControl {

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm parm = (TParm) obj;
			this.setValue("SPLASH_NO", parm.getValue("SPLASH_NO"));
			callFunction("UI|USER_ID|setEnabled", false);
			callFunction("UI|PATIENT_NAME|setEnabled", false);
			callFunction("UI|MR_NO|setEnabled", false);
			onQuery();
		}
		initPage();

	}

	/**
	 * 初始画面数据 REPORT_DATE
	 */
	private void initPage() {
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("HAPE_DATE", date);
		setValue("SPLASH_F_DATE", date);
		setValue("DEPARTMENT_DATE", date);
		setValue("PREVENTION_DATE", date);
		setValue("INFECTED_DATE", date);
		setValue("REPORT_DATE", date);
		onNUM_FIRSTFlg();
	}

	/**
	 * 查询 根据编号查询
	 */
	public void onQuery() {
		if (this.getValue("SPLASH_NO").toString().length() > 0) {
			String sql = " SELECT USER_ID,SPLASH_NO,USER_NAME,SEX_CODE,DEPT_CODE,"
					+ "TEL1,WORKING_YEARS,MR_NO,PATIENT_NAME,PATIENT_DEPT_CODE,SPLASH_F_DATE,"
					+ "INFECT_SCR_RESULT,ANTIHIV,HBSAG,ANTIHBS,ANTIHCV,VDRL,"
					+ "PATIENT_ANTIHIV,PATIENT_HBSAG,PATIENT_ANTIHBS,PATIENT_ANTIHCV,"
					+ "PATIENT_VDRL,BLOOD,SPUTUM,URINE,FAECES,PUKE,PLEURAL,PERITONEAL,"
					+ "SALIVA,SPLASH_OTHER,SPLASH_OTHER_TEXT,EYE,MOUTH,NOSE,SKIN,MUCOSA,"
					+ "EXPOSED_OTHER,EXPOSED_OTHER_TEXT,DRAW_BLOOD,EXPOSED_ACTION,MASK_SURGERY,"
					+ "LATEX_GLOVES1,LATEX_GLOVES2,FILM_GLOVES1,FILM_GLOVES2,MIRROR,GLASSES,"
					+ "MASK_F,SURGICAL,LSOLATION,PROTECT_OTHER,PROTECT_OTHER_TEXT,"
					+ "SPUTUM_SUCTION,OPERATION,CATHETERIZATION,POUR_URINE,DRESSING,PUNCTURE,"
					+ "EXPOSED_ACTION,EXPOSED_ACTION_TEXT,BLOOD_INFECTED,BLOOD_CONTACT,"
					+ "NUM_FIRST,SPLASH_MANY,SPLASH_DO,DEPARTMENT_HEADS,DEPARTMENT_DATE,PREVENTION_HEADS,"
					+ "PREVENTION_DATE,INFECTED_HEADS,INFECTED_DATE,REPORT_DATE,OPT_USER,OPT_DATE,OPT_TERM,AGE,HAPE_DATE,"
					+ "HAPPEN_PLACE,SPLASH_PLACE"
					+ " FROM INF_SPLASH where SPLASH_NO='"
					+ getValue("SPLASH_NO") + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			/*
			 * if (parm.getCount("SPLASH_NO") < 0) { this.messageBox("该编号不存在");
			 * }
			 */
			parm = parm.getRow(0);
			// System.out.println("parm::::::" + parm);
			setValue("USER_ID", parm.getValue("USER_ID"));
			setValue("USER_NAME", parm.getValue("USER_NAME"));
			setValue("SEX_CODE", parm.getValue("SEX_CODE"));
			setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
			setValue("TEL1", parm.getValue("TEL1"));
			setValue("WORKING_YEARS", parm.getValue("WORKING_YEARS"));
			setValue("MR_NO", parm.getValue("MR_NO"));
			setValue("PATIENT_NAME", parm.getValue("PATIENT_NAME"));
			setValue("PATIENT_DEPT_CODE", parm.getValue("PATIENT_DEPT_CODE"));
			setValue("HAPE_DATE", parm.getValue("HAPE_DATE").toString()
					.substring(0, 19).replaceAll("-", "/"));
			setValue("SPLASH_F_DATE", parm.getValue("SPLASH_F_DATE").toString()
					.substring(0, 10).replaceAll("-", "/"));
			if (parm.getValue("INFECT_SCR_RESULT").equals("Y")) {
				setValue("INFECT_SCR_RESULT_Y", "Y");
			} else if (parm.getValue("INFECT_SCR_RESULT").equals("Y")) {
				setValue("INFECT_SCR_RESULT_N", "Y");
			}
			setValue("ANTIHIV", parm.getValue("ANTIHIV"));
			setValue("HBSAG", parm.getValue("HBSAG"));
			setValue("ANTIHBS", parm.getValue("ANTIHBS"));
			setValue("ANTIHCV", parm.getValue("ANTIHCV"));
			setValue("VDRL", parm.getValue("VDRL"));
			setValue("PATIENT_ANTIHIV", parm.getValue("PATIENT_ANTIHIV"));
			setValue("PATIENT_HBSAG", parm.getValue("PATIENT_HBSAG"));
			setValue("PATIENT_ANTIHBS", parm.getValue("PATIENT_ANTIHBS"));
			setValue("PATIENT_ANTIHCV", parm.getValue("PATIENT_ANTIHCV"));
			setValue("PATIENT_VDRL", parm.getValue("PATIENT_VDRL"));
			setValue("BLOOD", parm.getValue("BLOOD"));
			setValue("SPUTUM", parm.getValue("SPUTUM"));
			setValue("URINE", parm.getValue("URINE"));
			setValue("FAECES", parm.getValue("FAECES"));
			setValue("PUKE", parm.getValue("PUKE"));
			setValue("PLEURAL", parm.getValue("PLEURAL"));
			setValue("PERITONEAL", parm.getValue("PERITONEAL"));
			setValue("SALIVA", parm.getValue("SALIVA"));
			setValue("SPLASH_OTHER", parm.getValue("SPLASH_OTHER"));
			onSPLASH_OTHERFlg();
			setValue("SPLASH_OTHER_TEXT", parm.getValue("SPLASH_OTHER_TEXT"));
			setValue("EYE", parm.getValue("EYE"));
			setValue("MOUTH", parm.getValue("MOUTH"));
			setValue("NOSE", parm.getValue("NOSE"));
			setValue("SKIN", parm.getValue("SKIN"));
			setValue("MUCOSA", parm.getValue("MUCOSA"));
			setValue("EXPOSED_OTHER", parm.getValue("EXPOSED_OTHER"));
			onEXPOSED_OTHERFlg();
			setValue("EXPOSED_OTHER_TEXT", parm.getValue("EXPOSED_OTHER_TEXT"));
			setValue("DRAW_BLOOD", parm.getValue("DRAW_BLOOD"));
			setValue("SPUTUM_SUCTION", parm.getValue("SPUTUM_SUCTION"));
			setValue("OPERATION", parm.getValue("OPERATION"));
			setValue("CATHETERIZATION", parm.getValue("CATHETERIZATION"));
			setValue("POUR_URINE", parm.getValue("POUR_URINE"));
			setValue("DRESSING", parm.getValue("DRESSING"));
			setValue("PUNCTURE", parm.getValue("PUNCTURE"));
			setValue("EXPOSED_ACTION", parm.getValue("EXPOSED_ACTION"));
			onEXPOSED_ACTIONFlg();
			setValue("EXPOSED_ACTION_TEXT",
					parm.getValue("EXPOSED_ACTION_TEXT"));
			setValue("MASK_SURGERY", parm.getValue("MASK_SURGERY"));
			setValue("LATEX_GLOVES1", parm.getValue("LATEX_GLOVES1"));
			setValue("LATEX_GLOVES2", parm.getValue("LATEX_GLOVES2"));
			setValue("FILM_GLOVES1", parm.getValue("FILM_GLOVES1"));
			setValue("FILM_GLOVES2", parm.getValue("FILM_GLOVES2"));
			setValue("MIRROR", parm.getValue("MIRROR"));
			setValue("GLASSES", parm.getValue("GLASSES"));
			setValue("MASK_F", parm.getValue("MASK_F"));
			setValue("SURGICAL", parm.getValue("SURGICAL"));
			setValue("LSOLATION", parm.getValue("LSOLATION"));
			setValue("PROTECT_OTHER", parm.getValue("PROTECT_OTHER"));
			onPROTECT_OTHERFlg();
			setValue("PROTECT_OTHER_TEXT", parm.getValue("PROTECT_OTHER_TEXT"));
			if (parm.getValue("BLOOD_INFECTED").equals("1")) {
				setValue("BLOOD_INFECTED_Y", "Y");
			} else if (parm.getValue("BLOOD_INFECTED").equals("2")) {
				setValue("BLOOD_INFECTED_N", "Y");
			} else if (parm.getValue("BLOOD_INFECTED").equals("3")) {
				setValue("BLOOD_INFECTED_YN", "Y");
			}

			if (parm.getValue("NUM_FIRST").equals("1")) {
				setValue("NUM_FIRST1", "Y");
			} else if (parm.getValue("NUM_FIRST").equals("2")) {
				setValue("NUM_FIRST2", "Y");
			}

			if (parm.getValue("BLOOD_CONTACT").equals("1")) {
				setValue("BLOOD_CONTACT_5", "Y");
			} else if (parm.getValue("BLOOD_CONTACT").equals("2")) {
				setValue("BLOOD_CONTACT_50", "Y");
			} else if (parm.getValue("BLOOD_CONTACT").equals("3")) {
				setValue("BLOOD_CONTACT_55", "Y");
			}
			
			setValue("SPLASH_MANY", parm.getValue("SPLASH_MANY"));
			//System.out.println(":::"+parm.getValue("SPLASH_MANY"));
			if (parm.getValue("SPLASH_DO").equals("1")) {
				setValue("SPLASH_DO1", "Y");
			} else if (parm.getValue("SPLASH_DO").equals("2")) {
				setValue("SPLASH_DO2", "Y");
			} else if (parm.getValue("SPLASH_DO").equals("3")) {
				setValue("SPLASH_DO3", "Y");
			}
			setValue("DEPARTMENT_HEADS", parm.getValue("DEPARTMENT_HEADS"));
			setValue("PREVENTION_HEADS", parm.getValue("PREVENTION_HEADS"));
			setValue("INFECTED_HEADS", parm.getValue("INFECTED_HEADS"));
			setValue("AGE", parm.getValue("AGE"));
			if (!parm.getValue("DEPARTMENT_DATE").equals("")) {
				setValue("DEPARTMENT_DATE", parm.getValue("DEPARTMENT_DATE")
						.toString().substring(0, 19).replaceAll("-", "/"));
			}
			if (!parm.getValue("PREVENTION_DATE").equals("")) {
				setValue("PREVENTION_DATE", parm.getValue("PREVENTION_DATE")
						.toString().substring(0, 19).replaceAll("-", "/"));
			}
			if (!parm.getValue("INFECTED_DATE").equals("")) {
				setValue("INFECTED_DATE", parm.getValue("INFECTED_DATE")
						.toString().substring(0, 19).replaceAll("-", "/"));
			}
			if (!parm.getValue("REPORT_DATE").equals("")) {
				setValue("REPORT_DATE", parm.getValue("REPORT_DATE").toString()
						.substring(0, 19).replaceAll("-", "/"));
			}
			setValue("HAPPEN_PLACE", parm.getValue("HAPPEN_PLACE"));
			setValue("SPLASH_PLACE", parm.getValue("SPLASH_PLACE"));
			onLATEX_GLOVESFlg();
			onFILM_GLOVESFlg();
			callFunction("UI|SPLASH_NO|setEnabled", false);
		} else {
			this.messageBox("请输入记录编号");
			return;
		}
	}

	/**
	 * 查询记录编号是否为空
	 */
	public Boolean onQueryExposureno(String SPLASH_NO) {
		if (SPLASH_NO.length() == 0) {
			return false;
		}
		return true;
		// String sql = " SELECT SPLASH_NO  FROM INF_SPLASH WHERE SPLASH_NO= '"
		// + SPLASH_NO + "'";
		// TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//
		//
		// if (parm.getCount() <= 0) {
		// return false;
		// }
		// return true;
	}

	/**
	 * 取得数据库访问类
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	// public void querySplashM(){
	// onQuery();
	// }

	/**
	 * 保存(修改)
	 */
	public void onSave() {

		if (onQueryExposureno(this.getValue("SPLASH_NO").toString())) {
			// 更新
			onUPDATE();
		} else {
			// 新增
			onNew();
		}
	}

	/**
	 * 尖锐物品种类注记
	 */
	public void onSPLASH_OTHERFlg() {
		if (getValueString("SPLASH_OTHER").equals("N")) {
			((TTextField) getComponent("SPLASH_OTHER_TEXT")).setEnabled(false);
			setValue("SPLASH_OTHER_TEXT", "");
		} else {
			((TTextField) getComponent("SPLASH_OTHER_TEXT")).setEnabled(true);
		}
	}

	/**
	 * 暴露部位
	 */
	public void onEXPOSED_OTHERFlg() {
		if (getValueString("EXPOSED_OTHER").equals("N")) {
			((TTextField) getComponent("EXPOSED_OTHER_TEXT")).setEnabled(false);
			setValue("EXPOSED_OTHER_TEXT", "");
		} else {
			((TTextField) getComponent("EXPOSED_OTHER_TEXT")).setEnabled(true);
		}
	}

	/**
	 * 喷溅时动作
	 */
	public void onEXPOSED_ACTIONFlg() {
		if (getValueString("EXPOSED_ACTION").equals("N")) {
			((TTextField) getComponent("EXPOSED_ACTION_TEXT"))
					.setEnabled(false);
			setValue("EXPOSED_ACTION_TEXT", "");
		} else {
			((TTextField) getComponent("EXPOSED_ACTION_TEXT")).setEnabled(true);
		}
	}

	/**
	 * 喷溅时传代
	 */
	public void onPROTECT_OTHERFlg() {
		if (getValueString("PROTECT_OTHER").equals("N")) {
			((TTextField) getComponent("PROTECT_OTHER_TEXT")).setEnabled(false);
			setValue("PROTECT_OTHER_TEXT", "");
		} else {
			((TTextField) getComponent("PROTECT_OTHER_TEXT")).setEnabled(true);

		}
	}

	/**
	 * 喷溅次数
	 */
	public void onNUM_FIRSTFlg() {
		if (getValueString("NUM_FIRST1").equals("Y")) {
			((TTextField) getComponent("SPLASH_MANY")).setEnabled(false);
		} else if (getValueString("NUM_FIRST2").equals("Y")) {
			((TTextField) getComponent("SPLASH_MANY")).setEnabled(true);
		} else {
			((TTextField) getComponent("SPLASH_MANY")).setEnabled(true);
		}
	}

	/**
	 * 香蕉手套
	 * 
	 */
	public void onLATEX_GLOVESFlg() {
		if (getValueString("LATEX_GLOVES1").equals("Y")) {
			((TCheckBox) getComponent("LATEX_GLOVES2")).setEnabled(false);
		} else if (getValueString("LATEX_GLOVES1").equals("N")) {
			((TCheckBox) getComponent("LATEX_GLOVES2")).setEnabled(true);
		}
		if (getValueString("LATEX_GLOVES2").equals("Y")) {
			((TCheckBox) getComponent("LATEX_GLOVES1")).setEnabled(false);
		} else if (getValueString("LATEX_GLOVES2").equals("N")) {
			((TCheckBox) getComponent("LATEX_GLOVES1")).setEnabled(true);
		}
	}

	/**
	 * 薄膜手套
	 */
	public void onFILM_GLOVESFlg() {
		if (getValueString("FILM_GLOVES1").equals("Y")) {
			((TCheckBox) getComponent("FILM_GLOVES2")).setEnabled(false);
		} else if (getValueString("FILM_GLOVES1").equals("N")) {
			((TCheckBox) getComponent("FILM_GLOVES2")).setEnabled(true);
		}

		if (getValueString("FILM_GLOVES2").equals("Y")) {
			((TCheckBox) getComponent("FILM_GLOVES1")).setEnabled(false);
		} else if (getValueString("FILM_GLOVES2").equals("N")) {
			((TCheckBox) getComponent("FILM_GLOVES1")).setEnabled(true);
		}
	}

	/**
	 * 获得界面数据
	 */
	public TParm getUiDate() {
		TParm Parm = new TParm();

		// 记录编号
		if (getValueString("SPLASH_NO").length() > 0) {
			Parm.setData("SPLASH_NO", this.getValue("SPLASH_NO"));
		}
		// 记录工号
		if (getValueString("USER_ID").length() > 0) {
			Parm.setData("USER_ID", this.getValue("USER_ID"));
		}
		// 患者名称
		if (getValueString("PATIENT_NAME").length() > 0) {
			Parm.setData("PATIENT_NAME", this.getValue("PATIENT_NAME"));
		}else {
			Parm.setData("PATIENT_NAME","");
		}
		// 发生日期
		if (getValueString("HAPE_DATE").length() > 0) {
			Parm.setData("HAPE_DATE", this.getValue("HAPE_DATE").toString()
					.substring(0, 19).replaceAll("-", "/"));
		} else {
			Parm.setData("HAPE_DATE","");
		}

		// 姓名
		if (getValueString("USER_NAME").length() > 0) {
			Parm.setData("USER_NAME", this.getValue("USER_NAME"));
		} else {
			Parm.setData("USER_NAME","");
		}
		// 性别
		if (getValueString("SEX_CODE").length() > 0) {
			Parm.setData("SEX_CODE", this.getValue("SEX_CODE"));
		} else {
			Parm.setData("SEX_CODE","");
		}
		// 科室
		if (getValueString("DEPT_CODE").length() > 0) {
			Parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		} else {
			Parm.setData("DEPT_CODE","");
		}
		// 电话
		if (getValueString("TEL1").length() > 0) {
			Parm.setData("TEL1", this.getValue("TEL1"));
		} else {
			Parm.setData("TEL1","");
		}
		// 工龄
		if (getValueString("WORKING_YEARS").length() > 0) {
			Parm.setData("WORKING_YEARS", this.getValue("WORKING_YEARS"));
		} else {
			Parm.setData("WORKING_YEARS","");
		}
		// 患者病案号
		if (getValueString("MR_NO").length() > 0) {
			Parm.setData("MR_NO", this.getValue("MR_NO"));
		} else {
			Parm.setData("MR_NO","");
		}
		// 患者科室
		if (getValueString("PATIENT_DEPT_CODE").length() > 0) {
			Parm.setData("PATIENT_DEPT_CODE",
					this.getValue("PATIENT_DEPT_CODE"));
		} else {
			Parm.setData("PATIENT_DEPT_CODE","");
		}
		// 首次检验日期
		if (getValueString("SPLASH_F_DATE").length() > 0) {
			Parm.setData("SPLASH_F_DATE", this.getValue("SPLASH_F_DATE")
					.toString().substring(0, 19).replaceAll("-", "/"));
		} else {
			Parm.setData("SPLASH_F_DATE","");
		}
		// 患者传筛情况
		if (getValueString("INFECT_SCR_RESULT_N").equals("Y")) {
			Parm.setData("INFECT_SCR_RESULT",'N');
		} else {
			Parm.setData("INFECT_SCR_RESULT",'Y');
		}
		// ANTIHIV
		if (getValueString("ANTIHIV").length() > 0) {
			Parm.setData("ANTIHIV", this.getValue("ANTIHIV"));
			if (Parm.getValue("ANTIHIV").equals("1")) {
				Parm.setData("INFECTION",'Y');
			}
		} else {
			Parm.setData("ANTIHIV","");
		}
		// HBSAG
		if (getValueString("HBSAG").length() > 0) {
			Parm.setData("HBSAG", this.getValue("HBSAG"));
			if (Parm.getValue("HBSAG").equals("1")) {
				Parm.setData("INFECTION",'Y');
			}
		} else {
			Parm.setData("HBSAG","");
		}
		// ANTIHBS
		if (getValueString("ANTIHBS").length() > 0) {
			Parm.setData("ANTIHBS", this.getValue("ANTIHBS"));
			if (Parm.getValue("ANTIHBS").equals("1")) {
				Parm.setData("INFECTION",'Y');
			}
		} else {
			Parm.setData("ANTIHBS","");
		}
		// ANTIHCV
		if (getValueString("ANTIHCV").length() > 0) {
			Parm.setData("ANTIHCV", this.getValue("ANTIHCV"));
			if (Parm.getValue("ANTIHCV").equals("1")) {
				Parm.setData("INFECTION",'Y');
			}
		} else {
			Parm.setData("ANTIHCV","");
		}
		// VDRL
		if (getValueString("VDRL").length() > 0) {
			Parm.setData("VDRL", this.getValue("VDRL"));
			if (Parm.getValue("VDRL").equals("1")) {
				Parm.setData("INFECTION",'Y');
			}
		} else {
			Parm.setData("VDRL","");
		}

		if (Parm.getValue("INFECTION").equals("")) {
			Parm.setData("INFECTION",'N');
		}
		// if(getValueString("INFECTION").length() > 0){
		// Parm.setData("INFECTION", 'Y');
		// }else{
		// Parm.setData("INFECTION", 'N');
		// }
		// PATIENT_ANTIHIV
		if (getValueString("PATIENT_ANTIHIV").length() > 0) {
			Parm.setData("PATIENT_ANTIHIV", this.getValue("PATIENT_ANTIHIV"));
		} else {
			Parm.setData("PATIENT_ANTIHIV","");
		}
		// PATIENT_HBSAG
		if (getValueString("PATIENT_HBSAG").length() > 0) {
			Parm.setData("PATIENT_HBSAG", this.getValue("PATIENT_HBSAG"));
		} else {
			Parm.setData("PATIENT_HBSAG","");
		}
		// PATIENT_ANTIHBS
		if (getValueString("PATIENT_ANTIHBS").length() > 0) {
			Parm.setData("PATIENT_ANTIHBS", this.getValue("PATIENT_ANTIHBS"));
		} else {
			Parm.setData("PATIENT_ANTIHBS","");
		}
		// PATIENT_ANTIHCV
		if (getValueString("PATIENT_ANTIHCV").length() > 0) {
			Parm.setData("PATIENT_ANTIHCV", this.getValue("PATIENT_ANTIHCV"));
		} else {
			Parm.setData("PATIENT_ANTIHCV","");
		}
		// PATIENT_VDRL
		if (getValueString("PATIENT_VDRL").length() > 0) {
			Parm.setData("PATIENT_VDRL", this.getValue("PATIENT_VDRL"));
		} else {
			Parm.setData("PATIENT_VDRL","");
		}
		// 血液
		if (getValueString("BLOOD").length() > 0) {
			Parm.setData("BLOOD", this.getValue("BLOOD"));
		}
		// 痰液
		if (getValueString("SPUTUM").length() > 0) {
			Parm.setData("SPUTUM", this.getValue("SPUTUM"));
		}
		// 尿液
		if (getValueString("URINE").length() > 0) {
			Parm.setData("URINE", this.getValue("URINE"));
		}
		// 便液
		if (getValueString("FAECES").length() > 0) {
			Parm.setData("FAECES", this.getValue("FAECES"));
		}
		// 呕吐物
		if (getValueString("PUKE").length() > 0) {
			Parm.setData("PUKE", this.getValue("PUKE"));
		}
		// 胸膜液
		if (getValueString("PLEURAL").length() > 0) {
			Parm.setData("PLEURAL", this.getValue("PLEURAL"));
		}
		// 腹膜液
		if (getValueString("PERITONEAL").length() > 0) {
			Parm.setData("PERITONEAL", this.getValue("PERITONEAL"));
		}
		// 唾液
		if (getValueString("SALIVA").length() > 0) {
			Parm.setData("SALIVA", this.getValue("SALIVA"));
		}
		// 喷溅 其他
		if (getValueString("SPLASH_OTHER").length() > 0) {
			Parm.setData("SPLASH_OTHER", this.getValue("SPLASH_OTHER"));
		}
		// 喷溅备注
		if (getValueString("SPLASH_OTHER_TEXT").length() > 0) {
			Parm.setData("SPLASH_OTHER_TEXT",
					this.getValue("SPLASH_OTHER_TEXT"));
		} else {
			Parm.setData("SPLASH_OTHER_TEXT","");
		}
		// 喷溅发生地点
		if (getValueString("HAPPEN_PLACE").length() > 0) {
			Parm.setData("HAPPEN_PLACE", this.getValue("HAPPEN_PLACE"));
		} else {
			Parm.setData("HAPPEN_PLACE","");
		}
		// 喷溅部位
		if (getValueString("SPLASH_PLACE").length() > 0) {
			Parm.setData("SPLASH_PLACE", this.getValue("SPLASH_PLACE"));
		} else {
			Parm.setData("SPLASH_PLACE","");
		}
		// 暴露部位EXPOSE_PLACE
		String expose = "";

		// 眼
		if (getValueString("EYE").equals("Y")) {

			expose += "眼;";
		}
		Parm.setData("EYE", this.getValue("EYE"));
		// 口
		if (getValueString("MOUTH").equals("Y")) {
			expose += "口;";
		}
		Parm.setData("MOUTH", this.getValue("MOUTH"));
		// 鼻
		if (getValueString("NOSE").equals("Y")) {
			expose += "鼻;";
		}
		Parm.setData("NOSE", this.getValue("NOSE"));
		// 破损皮肤
		if (getValueString("SKIN").equals("Y")) {
			expose += "破损皮肤;";
		}
		Parm.setData("SKIN", this.getValue("SKIN"));
		// 破损黏膜
		if (getValueString("MUCOSA").equals("Y")) {
			Parm.setData("MUCOSA", this.getValue("MUCOSA"));
			expose += "破损黏膜;";
		}
		Parm.setData("MUCOSA", this.getValue("MUCOSA"));

		// 暴露其他
		if (getValueString("EXPOSED_OTHER").length() > 0) {
			Parm.setData("EXPOSED_OTHER", this.getValue("EXPOSED_OTHER"));
		}
		// 暴露其他备注
		if (getValueString("EXPOSED_OTHER_TEXT").length() > 0) {
			Parm.setData("EXPOSED_OTHER_TEXT",
					this.getValue("EXPOSED_OTHER_TEXT"));
			expose += this.getValueString("EXPOSED_OTHER_TEXT");

		} else {
			Parm.setData("EXPOSED_OTHER_TEXT","");
		}
		Parm.setData("EXPOSE_PLACE", expose);
		if (Parm.getValue("EXPOSE_PLACE").length() < 0) {
			Parm.setData("EXPOSE_PLACE","");
		}

		// 采血
		if (getValueString("DRAW_BLOOD").length() > 0) {
			Parm.setData("DRAW_BLOOD", this.getValue("DRAW_BLOOD"));
		}
		// 吸痰
		if (getValueString("SPUTUM_SUCTION").length() > 0) {
			Parm.setData("SPUTUM_SUCTION", this.getValue("SPUTUM_SUCTION"));
		}
		// 手术
		if (getValueString("OPERATION").length() > 0) {
			Parm.setData("OPERATION", this.getValue("OPERATION"));
		}
		// 导尿
		if (getValueString("CATHETERIZATION").length() > 0) {
			Parm.setData("CATHETERIZATION", this.getValue("CATHETERIZATION"));
		}
		// 倒尿
		if (getValueString("POUR_URINE").length() > 0) {
			Parm.setData("POUR_URINE", this.getValue("POUR_URINE"));
		}
		// 换药
		if (getValueString("DRESSING").length() > 0) {
			Parm.setData("DRESSING", this.getValue("DRESSING"));
		}
		// 穿刺
		if (getValueString("PUNCTURE").length() > 0) {
			Parm.setData("PUNCTURE", this.getValue("PUNCTURE"));
		}
		// 喷溅动作 其他
		if (getValueString("EXPOSED_ACTION").length() > 0) {
			Parm.setData("EXPOSED_ACTION", this.getValue("EXPOSED_ACTION"));
		}
		// 动作其他备注
		if (getValueString("EXPOSED_ACTION_TEXT").length() > 0) {
			Parm.setData("EXPOSED_ACTION_TEXT",
					this.getValue("EXPOSED_ACTION_TEXT"));
		} else {
			Parm.setData("EXPOSED_ACTION_TEXT","");
		}
		// 外科口罩
		if (getValueString("MASK_SURGERY").length() > 0) {
			Parm.setData("MASK_SURGERY", this.getValue("MASK_SURGERY"));
		}
		// 单层乳胶手套
		if (getValueString("LATEX_GLOVES1").length() > 0) {
			Parm.setData("LATEX_GLOVES1", this.getValue("LATEX_GLOVES1"));
		}
		// 双层乳胶手套
		if (getValueString("LATEX_GLOVES2").length() > 0) {
			Parm.setData("LATEX_GLOVES2", this.getValue("LATEX_GLOVES2"));
		}
		// 单层薄膜手套
		if (getValueString("FILM_GLOVES1").length() > 0) {
			Parm.setData("FILM_GLOVES1", this.getValue("FILM_GLOVES1"));
		}
		// 双层薄膜手套
		if (getValueString("FILM_GLOVES2").length() > 0) {
			Parm.setData("FILM_GLOVES2", this.getValue("FILM_GLOVES2"));
		}
		// 防护镜
		if (getValueString("MIRROR").length() > 0) {
			Parm.setData("MIRROR", this.getValue("MIRROR"));
		}
		// 眼镜
		if (getValueString("GLASSES").length() > 0) {
			Parm.setData("GLASSES", this.getValue("GLASSES"));
		}
		// 面罩
		if (getValueString("MASK_F").length() > 0) {
			Parm.setData("MASK_F", this.getValue("MASK_F"));
		}
		// 外科手术衣
		if (getValueString("SURGICAL").length() > 0) {
			Parm.setData("SURGICAL", this.getValue("SURGICAL"));
		}
		// 隔离衣
		if (getValueString("LSOLATION").length() > 0) {
			Parm.setData("LSOLATION", this.getValue("LSOLATION"));
		}
		// 传代其他
		if (getValueString("PROTECT_OTHER").length() > 0) {
			Parm.setData("PROTECT_OTHER", this.getValue("PROTECT_OTHER"));
		}
		// 传代其他备注
		if (getValueString("PROTECT_OTHER_TEXT").length() > 0) {
			Parm.setData("PROTECT_OTHER_TEXT",
					this.getValue("PROTECT_OTHER_TEXT"));
		} else {
			Parm.setData("PROTECT_OTHER_TEXT","");
		}
		// 喷溅体液是否受血液感染
		if (getValueString("BLOOD_INFECTED_Y").equals("Y")) {
			Parm.setData("BLOOD_INFECTED","1");
		} else if (getValueString("BLOOD_INFECTED_N").equals("Y")) {
			Parm.setData("BLOOD_INFECTED","2");
		} else if (getValueString("BLOOD_INFECTED_YN").equals("Y")) {
			Parm.setData("BLOOD_INFECTED","3");
		}
		// 多少血 体液接触皮肤
		if (getValueString("BLOOD_CONTACT_5").equals("Y")) {
			Parm.setData("BLOOD_CONTACT","1");
		} else if (getValueString("BLOOD_CONTACT_50").equals("Y")) {
			Parm.setData("BLOOD_CONTACT","2");
		} else if (getValueString("BLOOD_CONTACT_55").equals("Y")) {
			Parm.setData("BLOOD_CONTACT","3");
		}
		// 喷溅次数
		if (getValueString("NUM_FIRST1").equals("Y")) {
			Parm.setData("NUM_FIRST","1");
		} else if (getValueString("NUM_FIRST2").equals("Y")) {
			Parm.setData("NUM_FIRST","2");
		}
		// 曾经喷溅次数
		if (getValueString("SPLASH_MANY").length() > 0) {
			Parm.setData("SPLASH_MANY", this.getValue("SPLASH_MANY"));
		} else {
			Parm.setData("SPLASH_MANY","");
		}

		// 喷溅后处理
		if (getValueString("SPLASH_DO1").equals("Y")) {
			Parm.setData("SPLASH_DO","1");
		} else if (getValueString("SPLASH_DO2").equals("Y")) {
			Parm.setData("SPLASH_DO","2");
		} else if (getValueString("SPLASH_DO3").equals("Y")) {
			Parm.setData("SPLASH_DO","3");
		} else {
			Parm.setData("SPLASH_DO","");
		}
		// 部门负责人
		if (getValueString("DEPARTMENT_HEADS").length() > 0) {
			Parm.setData("DEPARTMENT_HEADS", this.getValue("DEPARTMENT_HEADS"));
		} else {
			Parm.setData("DEPARTMENT_HEADS","");
		}
		// 部门负责人签字时间
		if (getValueString("DEPARTMENT_DATE").length() > 0) {
			Parm.setData("DEPARTMENT_DATE", this.getValue("DEPARTMENT_DATE")
					.toString().substring(0, 19).replaceAll("-", "/"));
		} else {
			Parm.setData("DEPARTMENT_DATE","");
		}
		// 预防保健科主任
		if (getValueString("PREVENTION_HEADS").length() > 0) {
			Parm.setData("PREVENTION_HEADS", this.getValue("PREVENTION_HEADS"));
		} else {
			Parm.setData("PREVENTION_HEADS","");
		}
		// 预防保健科主任签字时间
		if (getValueString("PREVENTION_DATE").length() > 0) {
			Parm.setData("PREVENTION_DATE", this.getValue("PREVENTION_DATE")
					.toString().substring(0, 19).replaceAll("-", "/"));
		} else {
			Parm.setData("PREVENTION_DATE","");
		}
		// 医院感染管理科主任
		if (getValueString("INFECTED_HEADS").length() > 0) {
			Parm.setData("INFECTED_HEADS", this.getValue("INFECTED_HEADS"));
		} else {
			Parm.setData("INFECTED_HEADS","");
		}
		// 医院感染管理科主任签字时间
		if (getValueString("INFECTED_DATE").length() > 0) {
			Parm.setData("INFECTED_DATE", this.getValue("INFECTED_DATE")
					.toString().substring(0, 19).replaceAll("-", "/"));
		} else {
			Parm.setData("INFECTED_DATE","");
		}
		// 报告日期
		if (getValueString("REPORT_DATE").length() > 0) {
			Parm.setData("REPORT_DATE", this.getValue("REPORT_DATE").toString()
					.substring(0, 19).replaceAll("-", "/"));
		} else {
			Parm.setData("REPORT_DATE","");
		}
		// 年龄
		if (getValueString("AGE").length() > 0) {
			Parm.setData("AGE", this.getValue("AGE"));
		} else {
			Parm.setData("AGE","");
		}

		// 操作人
		Parm.setData("OPT_USER", Operator.getID());
		// 操作时间
		Parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString()
				.substring(0, 19).replaceAll("-", "/"));
		// 操作IP
		Parm.setData("OPT_TERM", Operator.getIP());
		return Parm;
	}

	/**
	 * 修改
	 */
	public void onUPDATE() {

		TParm Parm = new TParm();
		Parm = this.getUiDate();
		System.out.println("--------------" + Parm);
		TParm UpdateParm = TIOM_AppServer.executeAction(
				"action.inf.INFMedicalInAction", "onUpate", Parm);
		if (UpdateParm.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		}
		messageBox("保存成功");
	}

	/**
	 * 新增
	 */
	public void onNew() {
		String splashUserId = this.getValueString("USER_ID");
		if (StringUtils.isEmpty(splashUserId)) {
			this.messageBox("工号不能为空");
			return;
		}
		TParm Parm = new TParm();
		Parm = this.getUiDate();// selMaxUserIdSeq
		System.out.println("++++++++++++++++++++" + Parm);
		String infSplashNo = INFCaseTool.getInstance().getInfNo();

		Parm.setData("SPLASH_NO", infSplashNo);

		// String plashNoQ = Parm.getValue("SPLASH_NO");
		// TParm maxSplashNo = onQuerySplashNo(plashNoQ);
		// for (int i = 0; i < maxSplashNo.getCount(); i++) {
		// if (maxSplashNo.getValue("SPLASH_NO", i).equals(
		// this.getValue("SPLASH_NO"))) {
		// this.messageBox("编号已存在");
		// return;
		// }
		// }

		TParm NewParm = TIOM_AppServer.executeAction(
				"action.inf.INFMedicalInAction", "onNew", Parm);
		if (NewParm.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		}
		messageBox("保存成功");
		setValue("SPLASH_NO", infSplashNo);
		onQuery();
	}

	/**
	 * 查询工号
	 * 
	 * @param caseNoprivate
	 * @return
	 */
	public synchronized TParm selMaxUserIdSeq(String user_id) {
		String sql = " SELECT USER_ID FROM INF_SPLASH WHERE USER_ID='"
				+ user_id + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 查询编号
	 * 
	 * @param caseNoprivate
	 * @return
	 */
	public synchronized TParm onQuerySplashNo(String plashNoQ) {
		String sql = " SELECT SPLASH_NO FROM INF_SPLASH WHERE SPLASH_NO='"
				+ plashNoQ + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 工号回车
	 */
	public void onQueryUser() {
		String userId = getValueString("USER_ID").trim();
		String sql = " SELECT A.USER_NAME,A.SEX_CODE,A.TEL1,B.DEPT_CODE"
				+ " FROM SYS_OPERATOR A, SYS_OPERATOR_DEPT B WHERE A.USER_ID='"
				+ userId + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		setValue("USER_NAME", result.getValue("USER_NAME", 0));
		setValue("SEX_CODE", result.getValue("SEX_CODE", 0));
		setValue("TEL1", result.getValue("TEL1", 0));
		setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
		callFunction("UI|USER_ID|setEnabled", false);
		if (result.getCount() <= 0) {
			this.messageBox("工号不存在!");
		}
	}

	/**
	 * 病案号回车
	 */
	public void onQueryMrno() {
		String mrNo = PatTool.getInstance().checkMrno(
				this.getValueString("MR_NO"));
		setValue("MR_NO", mrNo);
		String sql = " SELECT PAT_NAME" + " FROM SYS_PATINFO WHERE MR_NO='"
				+ mrNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		setValue("PATIENT_NAME", result.getValue("PAT_NAME", 0));
		callFunction("UI|PATIENT_NAME|setEnabled", false);
		callFunction("UI|MR_NO|setEnabled", false);
		if (result.getCount() <= 0) {
			this.messageBox("病案号不存在!");
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		clearText("USER_ID;SPLASH_NO;HAPPEN_PLACE;PATIENT_NAME;SPLASH_PLACE;USER_NAME;SEX_CODE;"
				+ "DEPT_CODE;TEL1;WORKING_YEARS;MR_NO;PATIENT_DEPT_CODE;"
				+ "SPLASH_F_DATE;INFECT_SCR_RESULT;ANTIHIV;HBSAG;ANTIHBS;"
				+ "ANTIHCV;VDRL;PATIENT_ANTIHIV;PATIENT_HBSAG;PATIENT_ANTIHBS;"
				+ "PATIENT_ANTIHCV;PATIENT_VDRL;SPLASH_OTHER_TEXT;EXPOSED_OTHER_TEXT;"
				+ "EXPOSED_ACTION_TEXT;PROTECT_OTHER_TEXT;DEPARTMENT_HEADS;"
				+ "DEPARTMENT_DATE;PREVENTION_HEADS;PREVENTION_DATE;INFECTED_HEADS;"
				+ "INFECTED_DATE;REPORT_DATE;AGE;HAPE_DATE;SPLASH_MANY;");
		this.setValue(" CATHETERIZATION", "N");
		this.setValue("POUR_URINE ", "N");
		this.setValue("DRESSING ", "N");
		this.setValue(" PUNCTURE", "N");
		this.setValue("EXPOSED_ACTION ", "N");
		this.setValue("BLOOD_CONTACT ", "N");
		this.setValue("MASK_SURGERY ", "N");
		this.setValue(" LATEX_GLOVES1", "N");
		this.setValue(" LATEX_GLOVES2", "N");
		this.setValue("FILM_GLOVES1 ", "N");
		this.setValue("FILM_GLOVES2 ", "N");
		this.setValue(" MIRROR", "N");
		this.setValue("GLASSES ", "N");
		this.setValue("MASK_F ", "N");
		this.setValue("SURGICAL ", "N");
		this.setValue(" LSOLATION", "N");
		this.setValue("PROTECT_OTHER ", "N");
		this.setValue(" BLOOD_INFECTED", "N");
		this.setValue("SPLASH_DO ", "N");
		this.setValue("BLOOD", "N");
		this.setValue("SPUTUM ", "N");
		this.setValue("URINE ", "N");
		this.setValue("FAECES ", "N");
		this.setValue(" PUKE", "N");
		this.setValue(" PLEURAL", "N");
		this.setValue(" PERITONEAL", "N");
		this.setValue("SALIVA ", "N");
		this.setValue("NUM_FIRST ", "N");
		this.setValue("SPLASH_OTHER ", "N");
		this.setValue("EYE ", "N");
		this.setValue(" MOUTH", "N");
		this.setValue("NOSE ", "N");
		this.setValue(" SKIN", "N");
		this.setValue("MUCOSA ", "N");
		this.setValue(" EXPOSED_OTHER", "N");
		this.setValue(" DRAW_BLOOD", "N");
		this.setValue("SPUTUM_SUCTION ", "N");
		this.setValue("OPERATION ", "N");
		this.setValue("BLOOD_INFECTED_Y", "Y");
		this.setValue("BLOOD_INFECTED_N", "N");
		this.setValue("BLOOD_INFECTED_YN", "N");
		this.setValue("BLOOD_CONTACT_5", "Y");
		this.setValue("BLOOD_INFECTED_50", "N");
		this.setValue("BLOOD_INFECTED_55", "N");
		this.setValue("NUM_FIRST1", "Y");
		this.setValue("NUM_FIRST2", "N");
		this.setValue("SPLASH_DO_1", "Y");
		this.setValue("SPLASH_DO_2", "N");
		this.setValue("SPLASH_DO_3", "N");
		onNUM_FIRSTFlg();
		onLATEX_GLOVESFlg();
		onFILM_GLOVESFlg();
		onEXPOSED_OTHERFlg();
		onEXPOSED_ACTIONFlg();
		onSPLASH_OTHERFlg();
		onPROTECT_OTHERFlg();
		initPage();
		callFunction("UI|USER_ID|setEnabled", true);
		callFunction("UI|PATIENT_NAME|setEnabled", true);
		callFunction("UI|MR_NO|setEnabled", true);
	}

}
