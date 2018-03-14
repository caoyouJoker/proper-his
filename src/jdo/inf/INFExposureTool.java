package jdo.inf;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

import jdo.sys.SystemTool;

import com.dongyang.db.TConnection;

/**
 * <p>Title: 感染控制感染控制职业暴露登记tool类</p>
 *
 * <p>Description: 感染控制感染控制职业暴露登记tool</p>
 *
 * <p>Copyright: Copyright (c) 20170509</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhanglei
 * @version 5.0
 */
public class INFExposureTool extends TJDOTool{

    /**
     * 构造器
     */
    public INFExposureTool() {
        onInit();
    }

    /**
     * 实例
     */
    private static INFExposureTool instanceObject;

    /**
     * 得到实例
     * @return INFCaseTool
     */
    public static INFExposureTool getInstance() {
        if (instanceObject == null) instanceObject = new INFExposureTool();
        return instanceObject;
    }
    
    /**
     * 得到SQL必要参数
     */
	/**
	 * 获得界面数据
	 */
	public TParm getUiDate(TParm parm){
		TParm result = new TParm();
		TParm ParmKey = new TParm();
		TParm ParmValue = new TParm();
		TParm ParmSet = new TParm();
		String key = "";
		String value = "";
		String set = "";
		//"to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd hh24:mi:ss'),"
		//医院感染管理科主任签字时间
		if (parm.getValue("INFECTED_DATE").length() > 0){
			//ParmValue.setData("INFECTED_DATE", parm.getValue("INFECTED_DATE"));
			key = key + ",INFECTED_DATE";
			value = value + ",to_date('" + parm.getValue("INFECTED_DATE") + "','yyyy-mm-dd')";
			set = set + ",INFECTED_DATE=to_date('" + parm.getValue("INFECTED_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("医院感染管理科主任签字时间"+ParmValue.getValue("INFECTED_DATE"));
		}
		//报告日期时间
		if (parm.getValue("REPORT_DATE").length() > 0){
			//ParmValue.setData("INFECTED_DATE", parm.getValue("INFECTED_DATE"));
			key = key + ",REPORT_DATE";
			value = value + ",to_date('" + parm.getValue("REPORT_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",REPORT_DATE=to_date('" + parm.getValue("REPORT_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			//this.messageBox("医院感染管理科主任签字时间"+ParmValue.getValue("INFECTED_DATE"));
				}
		//年龄
		if (parm.getValue("AGE").length() > 0){
			//ParmValue.setData("AGE", parm.getValue("AGE"));
			key = key + ",AGE";
			value = value + ",'" + parm.getValue("AGE") +"'";
			set = set + ",AGE='" + parm.getValue("AGE") +"'";
			//this.messageBox("年龄"+ParmValue.getValue("AGE"));
		}
		//实习
		if (parm.getValue("INTERNSHIP").length() > 0){
			//ParmValue.setData("INTERNSHIP", parm.getValue("INTERNSHIP"));
			key = key + ",INTERNSHIP";
			value = value + ",'" + parm.getValue("INTERNSHIP") +"'";
			set = set + ",INTERNSHIP='" + parm.getValue("INTERNSHIP") +"'";
			//this.messageBox("实习"+ParmValue.getValue("INTERNSHIP"));
		}
		//工号
		if (parm.getValue("USER_ID").length() > 0){
			//ParmValue.setData("USER_ID", parm.getValue("USER_ID"));
			key = key + ",USER_ID";
			value = value + ",'" + parm.getValue("USER_ID") +"'";
			set = set + ",USER_ID='" + parm.getValue("USER_ID") +"'";
			//this.messageBox("工号"+ParmValue.getValue("USER_ID"));
		}
		//发生日期
		if (parm.getValue("OCCURRENCE_DATE").length() > 0){
			//ParmValue.setData("OCCURRENCE_DATE", parm.getValue("OCCURRENCE_DATE"));
			//System.out.println("时间::" + ParmValue.getValue("OCCURRENCE_DATE"));
			key = key + ",OCCURRENCE_DATE";
			value = value + ",to_date('" + parm.getValue("OCCURRENCE_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			set = set + ",OCCURRENCE_DATE=to_date('" + parm.getValue("OCCURRENCE_DATE") + "','yyyy-mm-dd hh24:mi:ss')";
			//this.messageBox("发生日期"+ParmValue.getValue("OCCURRENCE_DATE"));
		}
		//姓名
		if (parm.getValue("PAT_NAME").length() > 0){
			//ParmValue.setData("PAT_NAME", parm.getValue("PAT_NAME"));
			key = key + ",PAT_NAME";
			value = value + ",'" + parm.getValue("PAT_NAME") +"'";
			set = set + ",PAT_NAME='" + parm.getValue("PAT_NAME") +"'";
			//this.messageBox("姓名"+ParmValue.getValue("PAT_NAME"));
		}
		//性别
		if (parm.getValue("SEX_CODE").length() > 0){
			//ParmValue.setData("SEX_CODE", parm.getValue("SEX_CODE"));
			key = key + ",SEX_CODE";
			value = value + ",'" + parm.getValue("SEX_CODE") +"'";
			set = set + ",SEX_CODE='" + parm.getValue("SEX_CODE") +"'";
			//this.messageBox("性别"+ParmValue.getValue("SEX_CODE"));
		}
		//科室
		if (parm.getValue("DEPT_CODE").length() > 0){
			//ParmValue.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
			key = key + ",DEPT_CODE";
			value = value + ",'" + parm.getValue("DEPT_CODE") +"'";
			set = set + ",DEPT_CODE='" + parm.getValue("DEPT_CODE") +"'";
			//this.messageBox("科室"+ParmValue.getValue("DEPT_CODE"));
		}
		//电话
		if (parm.getValue("CELL_PHONE").length() > 0){
			//ParmValue.setData("CELL_PHONE", parm.getValue("CELL_PHONE"));
			key = key + ",CELL_PHONE";
			value = value + ",'" + parm.getValue("CELL_PHONE") +"'";
			set = set + ",CELL_PHONE='" + parm.getValue("CELL_PHONE") +"'";
			//this.messageBox("电话"+ParmValue.getValue("CELL_PHONE"));
		}
		//工龄
		if (parm.getValue("WORKING_YEARS").length() > 0){
			//ParmValue.setData("WORKING_YEARS", parm.getValue("WORKING_YEARS"));
			key = key + ",WORKING_YEARS";
			value = value + ",'" + parm.getValue("WORKING_YEARS") +"'";
			set = set + ",WORKING_YEARS='" + parm.getValue("WORKING_YEARS") +"'";
			//this.messageBox("工龄"+ParmValue.getValue("WORKING_YEARS"));
		}
		//患者病案号
		if (parm.getValue("PATIENT_MR_NO").length() > 0){
			//ParmValue.setData("PATIENT_MR_NO", parm.getValue("PATIENT_MR_NO"));
			key = key + ",PATIENT_MR_NO";
			value = value + ",'" + parm.getValue("PATIENT_MR_NO") +"'";
			set = set + ",PATIENT_MR_NO='" + parm.getValue("PATIENT_MR_NO") +"'";
			//this.messageBox("患者病案号"+ParmValue.getValue("PATIENT_MR_NO"));
		}
		//患者科室
		if (parm.getValue("PATIENT_DEPT_CODE").length() > 0){
			//ParmValue.setData("PATIENT_DEPT_CODE", parm.getValue("PATIENT_DEPT_CODE"));
			key = key + ",PATIENT_DEPT_CODE";
			value = value + ",'" + parm.getValue("PATIENT_DEPT_CODE") +"'";
			set = set + ",PATIENT_DEPT_CODE='" + parm.getValue("PATIENT_DEPT_CODE") +"'";
			//this.messageBox("患者科室"+ParmValue.getValue("PATIENT_DEPT_CODE"));
		}
		//首次检验日期
		if (parm.getValue("FIRST_INSPECTION_DATE").length() > 0){
			//ParmValue.setData("FIRST_INSPECTION_DATE", parm.getValue("FIRST_INSPECTION_DATE"));
			key = key + ",FIRST_INSPECTION_DATE";
			value = value + ",to_date('" + parm.getValue("FIRST_INSPECTION_DATE") + "','yyyy-mm-dd')";
			set = set + ",FIRST_INSPECTION_DATE=to_date('" + parm.getValue("FIRST_INSPECTION_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("首次检验日期"+ParmValue.getValue("FIRST_INSPECTION_DATE"));
		}
		//患者传筛情况
		if (parm.getValue("PATIENT_PASS_SCREENING").length() > 0){
			key = key + ",PATIENT_PASS_SCREENING";
			value = value + ",'" + parm.getValue("PATIENT_PASS_SCREENING") +"'";
			set = set + ",PATIENT_PASS_SCREENING='" + parm.getValue("PATIENT_PASS_SCREENING") +"'";
		}
		//ANTIHIV
		if (parm.getValue("ANTIHIV").length() > 0){
			//ParmValue.setData("ANTIHIV", parm.getValue("ANTIHIV"));
			key = key + ",ANTIHIV";
			value = value + ",'" + parm.getValue("ANTIHIV") +"'";
			set = set + ",ANTIHIV='" + parm.getValue("ANTIHIV") +"'";
			//this.messageBox("ANTIHIV"+ParmValue.getValue("ANTIHIV"));
		}
		//HBSAG
		if (parm.getValue("HBSAG").length() > 0){
			//ParmValue.setData("HBSAG", parm.getValue("HBSAG"));
			key = key + ",HBSAG";
			value = value + ",'" + parm.getValue("HBSAG") +"'";
			set = set + ",HBSAG='" + parm.getValue("HBSAG") +"'";
			//this.messageBox("HBSAG"+ParmValue.getValue("HBSAG"));
		}
		//ANTIHBS
		if (parm.getValue("ANTIHBS").length() > 0){
			//ParmValue.setData("ANTIHBS", parm.getValue("ANTIHBS"));
			key = key + ",ANTIHBS";
			value = value + ",'" + parm.getValue("ANTIHBS") +"'";
			set = set + ",ANTIHBS='" + parm.getValue("ANTIHBS") +"'";
			//this.messageBox("ANTIHBS"+ParmValue.getValue("ANTIHBS"));
		}
		//ANTIHCV
		if (parm.getValue("ANTIHCV").length() > 0){
			//ParmValue.setData("ANTIHCV", parm.getValue("ANTIHCV"));
			key = key + ",ANTIHCV";
			value = value + ",'" + parm.getValue("ANTIHCV") +"'";
			set = set + ",ANTIHCV='" + parm.getValue("ANTIHCV") +"'";
			//this.messageBox("ANTIHCV"+ParmValue.getValue("ANTIHCV"));
		}
		//VDRL
		if (parm.getValue("VDRL").length() > 0){
			//ParmValue.setData("VDRL", parm.getValue("VDRL"));
			key = key + ",VDRL";
			value = value + ",'" + parm.getValue("VDRL") +"'";
			set = set + ",VDRL='" + parm.getValue("VDRL") +"'";
			//this.messageBox("VDRL"+ParmValue.getValue("VDRL"));
		}
		//PATIENT_ANTIHIV
		if (parm.getValue("PATIENT_ANTIHIV").length() > 0){
			//ParmValue.setData("PATIENT_ANTIHIV", parm.getValue("PATIENT_ANTIHIV"));
			key = key + ",PATIENT_ANTIHIV";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHIV") +"'";
			set = set + ",PATIENT_ANTIHIV='" + parm.getValue("PATIENT_ANTIHIV") +"'";
			//this.messageBox("PATIENT_ANTIHIV"+ParmValue.getValue("PATIENT_ANTIHIV"));
		}
		//PATIENT_HBSAG
		if (parm.getValue("PATIENT_HBSAG").length() > 0){
			//ParmValue.setData("PATIENT_HBSAG", parm.getValue("PATIENT_HBSAG"));
			key = key + ",PATIENT_HBSAG";
			value = value + ",'" + parm.getValue("PATIENT_HBSAG") +"'";
			set = set + ",PATIENT_HBSAG='" + parm.getValue("PATIENT_HBSAG") +"'";
			//this.messageBox("PATIENT_HBSAG"+ParmValue.getValue("PATIENT_HBSAG"));
		}
		//PATIENT_ANTIHBS
		if (parm.getValue("PATIENT_ANTIHBS").length() > 0){
			//ParmValue.setData("PATIENT_ANTIHBS", parm.getValue("PATIENT_ANTIHBS"));
			key = key + ",PATIENT_ANTIHBS";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHBS") +"'";
			set = set + ",PATIENT_ANTIHBS='" + parm.getValue("PATIENT_ANTIHBS") +"'";
			//this.messageBox("PATIENT_ANTIHBS"+ParmValue.getValue("PATIENT_ANTIHBS"));
		}
		//PATIENT_ANTIHCV
		if (parm.getValue("PATIENT_ANTIHCV").length() > 0){
			//ParmValue.setData("PATIENT_ANTIHCV", parm.getValue("PATIENT_ANTIHCV"));
			key = key + ",PATIENT_ANTIHCV";
			value = value + ",'" + parm.getValue("PATIENT_ANTIHCV") +"'";
			set = set + ",PATIENT_ANTIHCV='" + parm.getValue("PATIENT_ANTIHCV") +"'";
			//this.messageBox("PATIENT_ANTIHCV"+ParmValue.getValue("PATIENT_ANTIHCV"));
		}
		//PATIENT_VDRL
		if (parm.getValue("PATIENT_VDRL").length() > 0){
			//ParmValue.setData("PATIENT_VDRL", parm.getValue("PATIENT_VDRL"));
			key = key + ",PATIENT_VDRL";
			value = value + ",'" + parm.getValue("PATIENT_VDRL") +"'";
			set = set + ",PATIENT_VDRL='" + parm.getValue("PATIENT_VDRL") +"'";
			//this.messageBox("PATIENT_VDRL"+ParmValue.getValue("PATIENT_VDRL"));
		}
		//尖锐物品类 其他描述
		if (parm.getValue("OTHER_TYPE_DESCRIBE").length() >= 0){
			//ParmValue.setData("OTHER_TYPE_DESCRIBE", parm.getValue("OTHER_TYPE_DESCRIBE"));
			key = key + ",OTHER_TYPE_DESCRIBE";
			value = value + ",'" + parm.getValue("OTHER_TYPE_DESCRIBE") +"'";
			set = set + ",OTHER_TYPE_DESCRIBE='" + parm.getValue("OTHER_TYPE_DESCRIBE") +"'";
			//this.messageBox("尖锐物品类 其他描述"+Parm.getValue("OTHER_TYPE_DESCRIBE"));
		}
		//锐器伤时的操作 其他描述
		if (parm.getValue("OTHER_OPERATION_DESCRIBE").length() >= 0){
			//ParmValue.setData("OTHER_OPERATION_DESCRIBE", parm.getValue("OTHER_OPERATION_DESCRIBE"));
			key = key + ",OTHER_OPERATION_DESCRIBE";
			value = value + ",'" + parm.getValue("OTHER_OPERATION_DESCRIBE") +"'";
			set = set + ",OTHER_OPERATION_DESCRIBE='" + parm.getValue("OTHER_OPERATION_DESCRIBE") +"'";
			//this.messageBox("锐器伤时的操作 其他描述"+Parm.getValue("OTHER_OPERATION_DESCRIBE"));
		}
		//锐器伤时的动作 其他描述
		if (parm.getValue("OTHER_ACTION_DESCRIBE").length() >= 0){
			//ParmValue.setData("OTHER_ACTION_DESCRIBE", parm.getValue("OTHER_ACTION_DESCRIBE"));
			key = key + ",OTHER_ACTION_DESCRIBE";
			value = value + ",'" + parm.getValue("OTHER_ACTION_DESCRIBE") +"'";
			set = set + ",OTHER_ACTION_DESCRIBE='" + parm.getValue("OTHER_ACTION_DESCRIBE") +"'";
			//this.messageBox("锐器伤时的动作 其他描述"+ParmValue.getValue("OTHER_ACTION_DESCRIBE"));
		}
		//锐器伤发生地点
		if (parm.getValue("PLACE_INJURY").length() > 0){
			//Parm.setData("PLACE_INJURY", this.getValue("PLACE_INJURY"));
			key = key + ",PLACE_INJURY";
			value = value + ",'" + parm.getValue("PLACE_INJURY") +"'";
			set = set + ",PLACE_INJURY='" + parm.getValue("PLACE_INJURY") +"'";
			//this.messageBox("锐器伤发生地点"+Parm.getValue("PLACE_INJURY"));
		}
		//锐器伤发生部位
		if (parm.getValue("POSITION_INJURY").length() > 0){
			//Parm.setData("POSITION_INJURY", this.getValue("POSITION_INJURY"));
			key = key + ",POSITION_INJURY";
			value = value + ",'" + parm.getValue("POSITION_INJURY") +"'";
			set = set + ",POSITION_INJURY='" + parm.getValue("POSITION_INJURY") +"'";
			//this.messageBox("锐器伤发生部位"+Parm.getValue("POSITION_INJURY"));
		}
		//锐器伤物品曾接触过病人的血液及体液污染
		if(parm.getValue("CONTACT_POLLUTION").length() > 0){
			key = key + ",CONTACT_POLLUTION";
			value = value + ",'" + parm.getValue("CONTACT_POLLUTION") +"'";
			set = set + ",CONTACT_POLLUTION='" + parm.getValue("CONTACT_POLLUTION") +"'";
		}
		//锐器伤时是否戴手套
		if (parm.getValue("WEAR_GLOVES").length() > 0){
			key = key + ",WEAR_GLOVES";
			value = value + ",'" + parm.getValue("WEAR_GLOVES") +"'";
			set = set + ",WEAR_GLOVES='" + parm.getValue("WEAR_GLOVES") +"'";
		}
		//受伤次数
		if (parm.getValue("INJURED").length() > 0){
			key = key + ",INJURED";
			value = value + ",'" + parm.getValue("INJURED") +"'";
			set = set + ",INJURED='" + parm.getValue("INJURED") +"'";
		}
		//受伤次数总共
		if (parm.getValue("INJURY_FREQUENCY").length() >= 0){
			//ParmValue.setData("INJURY_FREQUENCY", parm.getValue("INJURY_FREQUENCY"));
			key = key + ",INJURY_FREQUENCY";
			value = value + ",'" + parm.getValue("INJURY_FREQUENCY") +"'";
			set = set + ",INJURY_FREQUENCY='" + parm.getValue("INJURY_FREQUENCY") +"'";
			//this.messageBox("受伤次数总共"+ParmValue.getValue("INJURY_FREQUENCY"));
		}
		//锐器伤后处理
		if (parm.getValue("INJURY_TREATMENT").length() > 0){
			key = key + ",INJURY_TREATMENT";
			value = value + ",'" + parm.getValue("INJURY_TREATMENT") +"'";
			set = set + ",INJURY_TREATMENT='" + parm.getValue("INJURY_TREATMENT") +"'";
		}
		//部门负责人
		if (parm.getValue("DEPARTMENT_HEADS").length() > 0){
			//ParmValue.setData("DEPARTMENT_HEADS", parm.getValue("DEPARTMENT_HEADS"));
			key = key + ",DEPARTMENT_HEADS";
			value = value + ",'" + parm.getValue("DEPARTMENT_HEADS") +"'";
			set = set + ",DEPARTMENT_HEADS='" + parm.getValue("DEPARTMENT_HEADS") +"'";
			//this.messageBox("部门负责人"+ParmValue.getValue("DEPARTMENT_HEADS"));
		}
		//部门负责人签字时间
		if (parm.getValue("DEPARTMENT_DATE").length() > 0){
			//ParmValue.setData("DEPARTMENT_DATE", parm.getValue("DEPARTMENT_DATE"));
			key = key + ",DEPARTMENT_DATE";
			value = value + ",to_date('" + parm.getValue("DEPARTMENT_DATE") + "','yyyy-mm-dd')";
			set = set + ",DEPARTMENT_DATE=to_date('" + parm.getValue("DEPARTMENT_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("部门负责人签字时间"+ParmValue.getValue("DEPARTMENT_DATE"));
		}
		//预防保健科主任
		if (parm.getValue("PREVENTION_HEADS").length() > 0){
			//ParmValue.setData("PREVENTION_HEADS", parm.getValue("PREVENTION_HEADS"));
			key = key + ",PREVENTION_HEADS";
			value = value + ",'" + parm.getValue("PREVENTION_HEADS") +"'";
			set = set + ",PREVENTION_HEADS='" + parm.getValue("PREVENTION_HEADS") +"'";
			//this.messageBox("预防保健科主任"+ParmValue.getValue("PREVENTION_HEADS"));
		}
		//预防保健科主任签字时间
		if (parm.getValue("PREVENTION_DATE").length() > 0){
			//ParmValue.setData("PREVENTION_DATE", parm.getValue("PREVENTION_DATE"));
			key = key + ",PREVENTION_DATE";
			value = value + ",to_date('" + parm.getValue("PREVENTION_DATE") + "','yyyy-mm-dd')";
			set = set + ",PREVENTION_DATE=to_date('" + parm.getValue("PREVENTION_DATE") + "','yyyy-mm-dd')";
			//this.messageBox("预防保健科主任签字时间"+ParmValue.getValue("PREVENTION_DATE"));
		}
		//医院感染管理科主任
		if (parm.getValue("INFECTED_HEADS").length() > 0){
			//ParmValue.setData("INFECTED_HEADS", parm.getValue("INFECTED_HEADS"));
			key = key + ",INFECTED_HEADS";
			value = value + ",'" + parm.getValue("INFECTED_HEADS") +"'";
			set = set + ",INFECTED_HEADS='" + parm.getValue("INFECTED_HEADS") +"'";
			//this.messageBox("医院感染管理科主任"+ParmValue.getValue("INFECTED_HEADS"));
		}
		if(parm.getValue("ANTIHIV").equals("1") || parm.getValue("ANTIHBS").equals("1") || 
				parm.getValue("VDRL").equals("1") || parm.getValue("HBSAG").equals("1") || 
				parm.getValue("ANTIHCV").equals("1")){
			//ParmValue.setData("INFECTED_HEADS", parm.getValue("INFECTED_HEADS"));
			key = key + ",INFECTION";
			value = value + ",'Y'";
			set = set + ",INFECTION='Y'";
			//this.messageBox("医院感染管理科主任"+ParmValue.getValue("INFECTED_HEADS"));
		}else{
			key = key + ",INFECTION";
			value = value + ",'N'";
			set = set + ",INFECTION='N'";
		}
		
		
		//给parm赋值
		ParmValue.setData("value", value);
		ParmKey.setData("key", key);
		ParmSet.setData("set", set);
		
		//给parm赋值
		result.setData("ParmValue", ParmValue.getData());
		result.setData("ParmKey", ParmKey.getData());
		result.setData("ParmSet", ParmSet.getData());
		
		return result;
	}

    
    
	/**保存方法*/
	public TParm onNew(TParm parm, TConnection connection){
		TParm getUiDate = getUiDate(parm);
		//读取相关parm
		TParm ParmKey = getUiDate.getParm("ParmKey");
		TParm ParmValue = getUiDate.getParm("ParmValue");
		TParm ParmSet = getUiDate.getParm("ParmSet");
//		System.out.println("进入Tool保存方法：" + getUiDate);
//		System.out.println("ParmKey：" + ParmKey.getValue("key").toString());
//		System.out.println("ParmValue：" + ParmValue.getValue("value").toString());
//		System.out.println("ParmSet：" + ParmSet.getValue("set").toString());
		
		String sql="INSERT INTO INF_EXPOSURE (EXPOSURE_NO,LNJECTION_NEEDLE,INDWELLING_NEEDLE,SCALP_ACUPUNCTURE," + 
					"NEEDLE,VACUUM_BLOOD_COLLECTOR,SURGICAL_INSTRUMENTS,GLASS_ITEMS,OTHER_TYPE," + 
					"BLOOD_COLLECTION,CATHETER_PLACEMENT,OPERATION," + 
					"FORMULATED_REHYDRATION,INJECTION,EQUIPMENT,OTHER_OPERATION,OPEN_NEEDLE,MISALIGNMENT_PUNCTURE," + 
					"DOSING_TIME,BACK_SLEEVE,BENDING_BREAKING_NEEDLE,OTHERS_STABBED,PARTING_INSTRUMENT,CLEANING_ITEMS," + 
					"PIERCING_BOX,HIDE_ITEMS,BREAK_USE,OPT_USER,OPT_DATE,OPT_TERM " + 
					//测试 
					//",OTHER_ACTION) "                    
					ParmKey.getValue("key").toString() + ",OTHER_ACTION) " 
					   +"VALUES (" 
					   			+"'"+parm.getData("EXPOSURE_NO")+"',"
					   			+"'"+parm.getData("LNJECTION_NEEDLE")+"'," 
					   			+"'"+parm.getData("INDWELLING_NEEDLE")+"'," 
					   			+"'"+parm.getData("SCALP_ACUPUNCTURE")+"'," 
					   			+"'"+parm.getData("NEEDLE")+"'," 
					   			+"'"+parm.getData("VACUUM_BLOOD_COLLECTOR")+"'," 
					   			+"'"+parm.getData("SURGICAL_INSTRUMENTS")+"'," 
					   			+"'"+parm.getData("GLASS_ITEMS")+"'," 
					   			+"'"+parm.getData("OTHER_TYPE")+"'," 
					   			+"'"+parm.getData("BLOOD_COLLECTION")+"'," 
					   			+"'"+parm.getData("CATHETER_PLACEMENT")+"'," 
					   			+"'"+parm.getData("OPERATION")+"'," 
					   			+"'"+parm.getData("FORMULATED_REHYDRATION")+"'," 
					   			+"'"+parm.getData("INJECTION")+"'," 
					   			+"'"+parm.getData("EQUIPMENT")+"'," 
					   			+"'"+parm.getData("OTHER_OPERATION")+"'," 
					   			+"'"+parm.getData("OPEN_NEEDLE")+"'," 
					   			+"'"+parm.getData("MISALIGNMENT_PUNCTURE")+"'," 
					   			+"'"+parm.getData("DOSING_TIME")+"'," 
					   			+"'"+parm.getData("BACK_SLEEVE")+"'," 		
					   			+"'"+parm.getData("BENDING_BREAKING_NEEDLE")+"'," 
					   			+"'"+parm.getData("OTHERS_STABBED")+"'," 
					   			+"'"+parm.getData("PARTING_INSTRUMENT")+"'," 
					   			+"'"+parm.getData("CLEANING_ITEMS")+"'," 
					   			+"'"+parm.getData("PIERCING_BOX")+"'," 
					   			+"'"+parm.getData("HIDE_ITEMS")+"'," 
					   			+"'"+parm.getData("BREAK_USE")+"'," 
					   			+"'"+parm.getData("OPT_USER")+"'," 
					   			+"to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd')," 
					   			+"'"+parm.getData("OPT_TERM")+"'" 
					   			+ParmValue.getValue("value").toString()+"," 
					   			+"'"+parm.getData("OTHER_ACTION")+"')";
		
		//System.out.println("sql:" + sql);
		
		//to_date('2000-11-26 00:04:47','yyyy-mm-dd hh24:mi:ss')
		
		
//		return parm;
		TParm result= new TParm(TJDODBTool.getInstance().update(sql,
                connection));
		
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**更新方法*/
	public TParm onUpDate(TParm parm, TConnection connection){
		//System.out.println("Tool进入onOuDate");
		TParm getUiDate = getUiDate(parm);
		//读取相关parm
		TParm ParmSet = getUiDate.getParm("ParmSet");
		//
		//System.out.println("Set::" + ParmSet.getValue("set").toString());
		String sql="UPDATE INF_EXPOSURE " +
					"SET LNJECTION_NEEDLE='"+parm.getData("LNJECTION_NEEDLE")+"'," +
				    "INDWELLING_NEEDLE='"+parm.getData("INDWELLING_NEEDLE")+"'," +
					"SCALP_ACUPUNCTURE='"+parm.getData("SCALP_ACUPUNCTURE")+"'," +
					"NEEDLE='"+parm.getData("NEEDLE")+"'," +
					"VACUUM_BLOOD_COLLECTOR='"+parm.getData("VACUUM_BLOOD_COLLECTOR")+"'," +
					"SURGICAL_INSTRUMENTS='"+parm.getData("SURGICAL_INSTRUMENTS")+"'," +
					"GLASS_ITEMS='"+parm.getData("GLASS_ITEMS")+"'," +
					"OTHER_TYPE='"+parm.getData("OTHER_TYPE")+"'," +
					"BLOOD_COLLECTION='"+parm.getData("BLOOD_COLLECTION")+"'," +
					"CATHETER_PLACEMENT='"+parm.getData("CATHETER_PLACEMENT")+"'," +
					"OPERATION='"+parm.getData("OPERATION")+"'," +
					"FORMULATED_REHYDRATION='"+parm.getData("FORMULATED_REHYDRATION")+"'," +
					"INJECTION='"+parm.getData("INJECTION")+"'," +
					"EQUIPMENT='"+parm.getData("EQUIPMENT")+"'," +
					"OTHER_OPERATION='"+parm.getData("OTHER_OPERATION")+"'," +
					"OPEN_NEEDLE='"+parm.getData("OPEN_NEEDLE")+"'," +
					"MISALIGNMENT_PUNCTURE='"+parm.getData("MISALIGNMENT_PUNCTURE")+"'," +
					"DOSING_TIME='"+parm.getData("DOSING_TIME")+"'," +
					"BACK_SLEEVE='"+parm.getData("BACK_SLEEVE")+"'," +
					"BENDING_BREAKING_NEEDLE='"+parm.getData("BENDING_BREAKING_NEEDLE")+"'," +
					"OTHERS_STABBED='"+parm.getData("OTHERS_STABBED")+"'," +
					"PARTING_INSTRUMENT='"+parm.getData("PARTING_INSTRUMENT")+"'," +
					"CLEANING_ITEMS='"+parm.getData("CLEANING_ITEMS")+"'," +
					"PIERCING_BOX='"+parm.getData("PIERCING_BOX")+"'," +
					"HIDE_ITEMS='"+parm.getData("HIDE_ITEMS")+"'," +
					"BREAK_USE='"+parm.getData("BREAK_USE")+"'," +
					"OPT_USER='"+parm.getData("OPT_USER")+"'," +
				//	to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd hh24:mi:ss')
					"OPT_DATE=to_date('"+parm.getData("OPT_DATE")+ "','yyyy-mm-dd') ," +
					"OPT_TERM='"+parm.getData("OPT_TERM")+"'" +
					ParmSet.getValue("set").toString() +","+
					"OTHER_ACTION='"+parm.getData("OTHER_ACTION")+"'" +
					" WHERE EXPOSURE_NO='"+parm.getData("EXPOSURE_NO")+"'";
		//System.out.println("Tool进入onOuDate的SQL:::::" + sql);
		TParm result= new TParm(TJDODBTool.getInstance().update(sql,
                connection));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**删除方法*/
	public TParm onDelete(TParm parm, TConnection connection){
		String sql="DELETE INV_SUPTITEM WHERE SUPITEM_CODE='"+parm.getData("SUPITEM_CODE")+"'";
		TParm result= new TParm(TJDODBTool.getInstance().update(sql,
                connection));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
    
    

}
