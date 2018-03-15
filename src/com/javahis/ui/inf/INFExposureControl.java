package com.javahis.ui.inf;


import java.sql.Timestamp;

import javax.swing.JComboBox;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>Title:职业暴露登记控制类 </p>
 *
 * <p>Dription:职业暴露等级控制类 </p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author zhanglei 
 * @version 5.0
 */
public class INFExposureControl extends TControl {
	/**
	 *  存放参数
	 */
	private TParm Parameter;
	
	
	 /**
	 *初始化 
	 */
	public void onInit(){
		
		
		// 获取预约住院界面传入的参数
		Object a = this.getParameter();
		
		if(a instanceof TParm){
			Object obj = this.getParameter();
			TParm parm = null;
			if (obj instanceof TParm){
				parm = (TParm) obj;
			}
			this.setValue("EXPOSURE_NO", parm.getValue("EXPOSURE_NO"));
			onQuery();	
		}else{
			onInitialization();
		}
	}
	/**
	 *初始化界面数据 
	 */
	public void onInitialization(){
		//得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		//初始化发生日期
		setValue("OCCURRENCE_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//初始化报告日期
		setValue("REPORT_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//初始化部门负责人日期
		setValue("DEPARTMENT_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//初始化保健科主任
		setValue("PREVENTION_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//初始化感染科主任
		setValue("INFECTED_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
	}
	/**
	 * 工号回车事件
	 */
	public void onUSERID(){
		
		String sql = " SELECT A.USER_NAME,A.SEX_CODE,B.DEPT_CODE "
				   + " FROM SYS_OPERATOR A,SYS_OPERATOR_DEPT B WHERE A.USER_ID='" + this.getValue("USER_ID") + "'"
				   + " AND A.USER_ID=B.USER_ID AND B.MAIN_FLG='Y'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //this.messageBox("" + parm);
        if (parm.getCount() < 0){
        	this.messageBox("没有查到该工号员工如是实习生或临时请选择-实习");
        }
        setValue("PAT_NAME",parm.getValue("USER_NAME",0));
        setValue("SEX_CODE",parm.getValue("SEX_CODE",0));
        setValue("DEPT_CODE",parm.getValue("DEPT_CODE",0));
	}
	
	
	
	
	
	
	
	/**
	 * 实习事件
	 */
	public void onInternship(){
		if(this.getValue("PAT_NAME").toString().length() > 0){
			this.messageBox("不允许查询到相关人员后再改变临时身份");
			setValue("INTERNSHIP", "N");
		}
		if (getValueString("INTERNSHIP").equals("N")){
			((TTextField) getComponent("PAT_NAME")).setEnabled(false);
			((TComboBox) getComponent("SEX_CODE")).setEnabled(false);
			((TTextFormat) getComponent("DEPT_CODE")).setEnabled(false);
		}
		else{
			((TTextField) getComponent("PAT_NAME")).setEnabled(true);
			((TComboBox) getComponent("SEX_CODE")).setEnabled(true);
			((TTextFormat) getComponent("DEPT_CODE")).setEnabled(true);
		}		
	}
	/**
	 * 查询
	 * 根据暴露编号查询  
	 */
	public void onQuery(){
		
		//判断页面的暴露号有没有12位的值
		
		if(this.getValue("EXPOSURE_NO").toString().length() == 12){
			
			String sql = " SELECT EXPOSURE_NO,PAT_NAME,BIRTH_DATE,SEX_CODE,DEPT_CODE,CELL_PHONE,WORKING_YEARS,PATIENT_MR_NO,PATIENT_DEPT_CODE,FIRST_INSPECTION_DATE,PATIENT_PASS_SCREENING,ANTIHIV,HBSAG,ANTIHBS,ANTIHCV,VDRL,PATIENT_ANTIHIV,PATIENT_HBSAG,PATIENT_ANTIHBS,PATIENT_ANTIHCV,PATIENT_VDRL,LNJECTION_NEEDLE,INDWELLING_NEEDLE,SCALP_ACUPUNCTURE,NEEDLE,VACUUM_BLOOD_COLLECTOR,SURGICAL_INSTRUMENTS,GLASS_ITEMS,OTHER_TYPE,OTHER_TYPE_DESCRIBE,BLOOD_COLLECTION,CATHETER_PLACEMENT,OPERATION,FORMULATED_REHYDRATION,INJECTION,EQUIPMENT,OTHER_OPERATION,OTHER_OPERATION_DESCRIBE,OPEN_NEEDLE,MISALIGNMENT_PUNCTURE,DOSING_TIME,BACK_SLEEVE,BENDING_BREAKING_NEEDLE,OTHERS_STABBED,PARTING_INSTRUMENT,CLEANING_ITEMS,PIERCING_BOX,HIDE_ITEMS,BREAK_USE,OTHER_ACTION,OTHER_ACTION_DESCRIBE,CONTACT_POLLUTION,WEAR_GLOVES,INJURED,INJURY_FREQUENCY,INJURY_TREATMENT,DEPARTMENT_HEADS,DEPARTMENT_DATE,PREVENTION_HEADS,PREVENTION_DATE,INFECTED_HEADS,INFECTED_DATE,OPT_USER,OPT_DATE,OPT_TERM,AGE,OCCURRENCE_DATE,PLACE_INJURY,POSITION_INJURY,USER_ID,INTERNSHIP,REPORT_DATE"
	                + " FROM INF_EXPOSURE where EXPOSURE_NO='" + getValue("EXPOSURE_NO") + "'";
	        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	        //      
//	        this.messageBox("进入查询" + "AGE:" + parm.getValue("AGE") +
//	        		";OCCURRENCE_DATE:" + parm.getValue("OCCURRENCE_DATE") + ";PLACE_INJURY:" + parm.getValue("PLACE_INJURY") + 
//	        		";POSITION_INJURY:" + parm.getValue("POSITION_INJURY") + ";OTHER_ACTION:" + parm.getValue("OTHER_ACTION"));
	       // this.messageBox("查询暴露编号是否存在:" + parm);
	        if (parm.getCount("EXPOSURE_NO") < 0){
	        	this.messageBox("没有查到相应是数据");
	        }
	        parm = parm.getRow(0);
	        
	        setValue("INTERNSHIP",parm.getValue("INTERNSHIP"));
	        setValue("USER_ID",parm.getValue("USER_ID"));
	        //System.out.println("parm::::::" + parm);
	        //this.messageBox("1111111111");
	        setValue("AGE",parm.getValue("AGE"));
	        //this.messageBox("222222");
	        setValue("PAT_NAME",parm.getValue("PAT_NAME"));
	        //this.messageBox("33333");
	        setValue("SEX_CODE",parm.getValue("SEX_CODE"));
	        //this.messageBox("44444");
	        setValue("DEPT_CODE",parm.getValue("DEPT_CODE"));
	        //this.messageBox("55555");
	        setValue("CELL_PHONE",parm.getValue("CELL_PHONE"));
	        //this.messageBox("66666");
	        setValue("WORKING_YEARS",parm.getValue("WORKING_YEARS"));
	        //this.messageBox("77777");
	        setValue("PATIENT_MR_NO",parm.getValue("PATIENT_MR_NO"));
	        //this.messageBox("8");
	        setValue("PATIENT_DEPT_CODE",parm.getValue("PATIENT_DEPT_CODE"));
	        //this.messageBox("9");
	        //this.messageBox("" + parm.getValue("FIRST_INSPECTION_DATE").toString().length());
	        //this.messageBox("10");
	        
	        if(parm.getValue("PATIENT_PASS_SCREENING").equals("Y")){
	        	setValue("PATIENT_PASS_SCREENING_Y","Y");
	        }else if(parm.getValue("PATIENT_PASS_SCREENING").equals("Y")){
	        	setValue("PATIENT_PASS_SCREENING_N","Y");
	        }else{
	        	
	        }
	        //this.messageBox("第一个if");
	        setValue("ANTIHIV",parm.getValue("ANTIHIV"));
	        setValue("HBSAG",parm.getValue("HBSAG"));
	        setValue("ANTIHBS",parm.getValue("ANTIHBS"));
	        setValue("ANTIHCV",parm.getValue("ANTIHCV"));
	        setValue("VDRL",parm.getValue("VDRL"));
	        setValue("PATIENT_ANTIHIV",parm.getValue("PATIENT_ANTIHIV"));
	        setValue("PATIENT_HBSAG",parm.getValue("PATIENT_HBSAG"));
	        setValue("PATIENT_ANTIHBS",parm.getValue("PATIENT_ANTIHBS"));
	        setValue("PATIENT_ANTIHCV",parm.getValue("PATIENT_ANTIHCV"));
	        setValue("PATIENT_VDRL",parm.getValue("PATIENT_VDRL"));
	        setValue("LNJECTION_NEEDLE",parm.getValue("LNJECTION_NEEDLE"));
	        setValue("INDWELLING_NEEDLE",parm.getValue("INDWELLING_NEEDLE"));
	        setValue("SCALP_ACUPUNCTURE",parm.getValue("SCALP_ACUPUNCTURE"));
	        setValue("NEEDLE",parm.getValue("NEEDLE"));
	        setValue("VACUUM_BLOOD_COLLECTOR",parm.getValue("VACUUM_BLOOD_COLLECTOR"));
	        setValue("SURGICAL_INSTRUMENTS",parm.getValue("SURGICAL_INSTRUMENTS"));
	        setValue("GLASS_ITEMS",parm.getValue("GLASS_ITEMS"));
	        setValue("OTHER_TYPE",parm.getValue("OTHER_TYPE"));
	        if(parm.getValue("OTHER_TYPE").equals("Y")){
	        	((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(true);
	        }
	        //this.messageBox("第二个if");
	        setValue("OTHER_TYPE_DESCRIBE",parm.getValue("OTHER_TYPE_DESCRIBE"));
	        setValue("BLOOD_COLLECTION",parm.getValue("BLOOD_COLLECTION"));
	        setValue("CATHETER_PLACEMENT",parm.getValue("CATHETER_PLACEMENT"));
	        setValue("OPERATION",parm.getValue("OPERATION"));
	        setValue("FORMULATED_REHYDRATION",parm.getValue("FORMULATED_REHYDRATION"));
	        setValue("INJECTION",parm.getValue("INJECTION"));
	        setValue("EQUIPMENT",parm.getValue("EQUIPMENT"));
	        setValue("OTHER_OPERATION",parm.getValue("OTHER_OPERATION"));
	        if(parm.getValue("OTHER_OPERATION").equals("Y")){
	        	((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(true);
	        }
	        
	        //this.messageBox("第三个if");
	        setValue("OTHER_OPERATION_DESCRIBE",parm.getValue("OTHER_OPERATION_DESCRIBE"));
	        setValue("OPEN_NEEDLE",parm.getValue("OPEN_NEEDLE"));
	        setValue("MISALIGNMENT_PUNCTURE",parm.getValue("MISALIGNMENT_PUNCTURE"));
	        setValue("DOSING_TIME",parm.getValue("DOSING_TIME"));
	        setValue("BACK_SLEEVE",parm.getValue("BACK_SLEEVE"));
	        setValue("BENDING_BREAKING_NEEDLE",parm.getValue("BENDING_BREAKING_NEEDLE"));
	        setValue("OTHERS_STABBED",parm.getValue("OTHERS_STABBED"));
	        setValue("PARTING_INSTRUMENT",parm.getValue("PARTING_INSTRUMENT"));
	        setValue("CLEANING_ITEMS",parm.getValue("CLEANING_ITEMS"));
	        setValue("PIERCING_BOX",parm.getValue("PIERCING_BOX"));
	        setValue("HIDE_ITEMS",parm.getValue("HIDE_ITEMS"));
	        setValue("BREAK_USE",parm.getValue("BREAK_USE"));
	        setValue("OTHER_ACTION",parm.getValue("OTHER_ACTION"));
	        //this.messageBox(parm.getValue("OTHER_ACTION"));
	        if(parm.getValue("OTHER_ACTION").equals("Y")){
	        	((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(true);
	        }
	        setValue("OTHER_ACTION_DESCRIBE",parm.getValue("OTHER_ACTION_DESCRIBE"));
	        
	        //this.messageBox("第四个if");
	        if(parm.getValue("CONTACT_POLLUTION").equals("1")){
	        	setValue("CONTACT_POLLUTION_1", "Y");
			}else if(parm.getValue("CONTACT_POLLUTION").equals("2")){
				setValue("CONTACT_POLLUTION_2", "Y");
			}else if(parm.getValue("CONTACT_POLLUTION").equals("3")){
				setValue("CONTACT_POLLUTION_3", "Y");
			}
	        
	        
	        
	        
	        //this.messageBox("第五个if");
	        if (parm.getValue("INJURED").equals("1")){
	        	setValue("INJURED_1", "Y");
			}else if(parm.getValue("INJURED").equals("2")){
				setValue("INJURED_2", "Y");
				((TTextField) getComponent("INJURY_FREQUENCY")).setEnabled(true);
			}
	        
	        //this.messageBox("第六个if");
	        if (parm.getValue("WEAR_GLOVES").equals("1")){
	        	setValue("WEAR_GLOVES_1", "Y");
			}else if(parm.getValue("WEAR_GLOVES").equals("2")){
				setValue("WEAR_GLOVES_2", "Y");
			}else if(parm.getValue("WEAR_GLOVES").equals("3")){
				setValue("WEAR_GLOVES_3", "Y");
			}
	        //this.messageBox("第七个if");
	        setValue("INJURY_FREQUENCY",parm.getValue("INJURY_FREQUENCY"));
	        
	        
	        if (parm.getValue("INJURY_TREATMENT").equals("1")){
	        	setValue("INJURY_TREATMENT_1", "Y");
			}else if(parm.getValue("INJURY_TREATMENT").equals("2")){
				setValue("INJURY_TREATMENT_2", "Y");
			}else if(parm.getValue("INJURY_TREATMENT").equals("3")){
				setValue("INJURY_TREATMENT_3", "Y");
			}else if(parm.getValue("INJURY_TREATMENT").equals("4")){
				setValue("INJURY_TREATMENT_4", "Y");
			}
	        //this.messageBox("第八个if");
	        setValue("DEPARTMENT_HEADS",parm.getValue("DEPARTMENT_HEADS"));
	        if(parm.getValue("DEPARTMENT_DATE").toString().length() > 0){
	        	setValue("DEPARTMENT_DATE",parm.getValue("DEPARTMENT_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("第一个日期");
	        setValue("PREVENTION_HEADS",parm.getValue("PREVENTION_HEADS"));
	        if(parm.getValue("PREVENTION_DATE").toString().length() > 0){
	        	setValue("PREVENTION_DATE",parm.getValue("PREVENTION_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("第二个日期");
	        setValue("INFECTED_HEADS",parm.getValue("INFECTED_HEADS"));
	        if(parm.getValue("INFECTED_DATE").toString().length() > 0){
	        	setValue("INFECTED_DATE",parm.getValue("INFECTED_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("第三个日期");
	        if(parm.getValue("OCCURRENCE_DATE").toString().length() > 0){
	        	setValue("OCCURRENCE_DATE",parm.getValue("OCCURRENCE_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("第四个日期");
	        if(parm.getValue("FIRST_INSPECTION_DATE").toString().length() > 0){
	        	setValue("FIRST_INSPECTION_DATE",parm.getValue("FIRST_INSPECTION_DATE").toString().substring(0,10).replaceAll("-", "/"));
	        }
	        //this.messageBox("第五个日期");
	        if(parm.getValue("REPORT_DATE").toString().length() > 0){
	        	setValue("REPORT_DATE",parm.getValue("REPORT_DATE").toString().substring(0,19).replaceAll("-", "/"));
	        }
	        //this.messageBox("第六个日期");
	        setValue("PLACE_INJURY",parm.getValue("PLACE_INJURY"));
	        setValue("POSITION_INJURY",parm.getValue("POSITION_INJURY"));
		}else{
			this.messageBox("请输入12位暴露编号");
			return;
		}
//		TParm parmM = new TParm();
//		TParm parmD = new TParm();
//		TParm parm = new TParm();
//		
//		String a = "EXPOSURE_NO";
//		String b = ",b";
//		String c = a + b + ",c";
//		parmM.setData("string", c);
//		parmD.setData("PAT_NAME", this.getValue("PAT_NAME"));
//		parmD.setData("SEX_CODE", this.getValue("SEX_CODE"));
//		
//		parm.setData("DSPNM", parmM.getData());
//		parm.setData("DSPND", parmD.getData());
//		
//		TParm parm1 = parm.getParm("DSPNM");
//		String d = parm1.getValue("string");
//		TParm parm2 = parm.getParm("DSPND");
//		
//		this.messageBox("parm1:" + parm1 + "-parm2:" + parm2 + "-d:" + d + "-PAT_NAME:" + parmD.getValue("PAT_NAME"));
//		
//		
		//getUiDate();		
	}

	/**
     * 查询暴露编号是否存在
     */
	public Boolean onQueryExposureno(String EXPOSURE_NO){
		String sql = " SELECT EXPOSURE_NO"
                + " FROM INF_EXPOSURE where EXPOSURE_NO='" + EXPOSURE_NO + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //this.messageBox("查询暴露编号是否存在:" + parm);
        if (parm.getCount() <= 0){
        	return false;
        }
		return true;
	}
    /**
     * 取得数据库访问类
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
	
	/**
	 * 保存(修改)
	 */
	public void onSave(){
		
//		if(this.getValue("EXPOSURE_NO").toString().length() != 12){
//			this.messageBox("暴露编号不合法");
//			return;
//		}
		/**
		 * 必填
		 */
		if(getValue("PAT_NAME").toString().length() <= 0 || 
				 getValue("USER_ID").toString().length() <= 0){
			this.messageBox("请填写必要的医疗人员姓名、工号");
			return;
		}
		
		if(onQueryExposureno(this.getValue("EXPOSURE_NO").toString())){
			//this.messageBox("进入更新");
			//更新
			onUPDATE();
			
		}else{
			//this.messageBox("进入新增");
			//新增
			onNew();
			
		}
	}

	/**
     * 界面上不可编辑的控件
     */
	public void setTextEnabled(boolean boo) {
		callFunction("UI|EXPOSURE_NO|setEnabled", boo);
	}
	
	/**
     * 尖锐物品种类注记
     */
	public void onOtherTypeFlg() {    	    
	    if (getValueString("OTHER_TYPE").equals("N"))
	            ((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(false);
	    else
	       ((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(true);
	}
	 
	 /**
	 * 尖锐物品操作注记
	 */
	 public void onOTHEROPERATIONFlg() {    	    
		if (getValueString("OTHER_OPERATION").equals("N"))
		     ((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(false);
		else
		     ((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(true);
	}
		 
	/**
    * 尖锐物品动作注记
	*/
	public void onOTHER_ACTIONFlg() {    	
		 if (getValueString("OTHER_ACTION").equals("N"))
		     ((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(false);
		 else
			  ((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(true);
	}
	/**
	 * 曾经受伤注记
	 */
	public void onINJURED2Flg() { 
		//this.messageBox(getValueString("INJURED_2"));
		if (getValueString("INJURED_2").equals("N"))
			((TTextField) getComponent("INJURY_FREQUENCY")).setEnabled(false);
		else
			((TTextField) getComponent("INJURY_FREQUENCY")).setEnabled(true);
	}

	/**
	 * 删除
	 */
	public void onDelete(){
		
		
		//打印pram
		//System.out.println(parm.getCount());
		
		
		
		
		//System.out.println("reParmdsDF:::"+reParm);
//		if(parm.getCount()>0){
//			//确认是否删除
//			switch(this.messageBox("提示信息",
//	                            "确认删除!", TControl.YES_NO_OPTION)) {
//	        //保存
//	        case 0:
//	            break;
//	            //不保存
//	        case 1:
//	            return;
//			}
//		
//			
//			if(.getErrCode()<0){
//				this.messageBox("删除失败");
//				return;
//			}
//			this.messageBox("删除成功");
			//onClear();
			
		
	//	}  
	}
	/**
	 * 获得界面数据
	 */
	public TParm getUiDate(){
		TParm Parm=new TParm();
		
		//实习
		if (getValueString("INTERNSHIP").length() > 0){
			Parm.setData("INTERNSHIP", this.getValue("INTERNSHIP"));
			//this.messageBox("发生日期"+Parm.getValue("OCCURRENCE_DATE"));
		}
		//工号
		if (getValueString("USER_ID").length() > 0){
			Parm.setData("USER_ID", this.getValue("USER_ID"));
			//this.messageBox("发生日期"+Parm.getValue("OCCURRENCE_DATE"));
		}
		//发生日期
		if (getValueString("OCCURRENCE_DATE").length() > 0){
			Parm.setData("OCCURRENCE_DATE", this.getValue("OCCURRENCE_DATE").toString().substring(0,19).replaceAll("-", "/"));
			//this.messageBox("发生日期"+Parm.getValue("OCCURRENCE_DATE"));
			}
		//报告日期
		if (getValueString("REPORT_DATE").length() > 0){
			Parm.setData("REPORT_DATE", this.getValue("REPORT_DATE").toString().substring(0,19).replaceAll("-", "/"));
			//this.messageBox("报告日期"+Parm.getValue("REPORT_DATE"));
			}
		//姓名
		if (getValueString("PAT_NAME").length() > 0){
			Parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
			//this.messageBox("姓名"+Parm.getValue("PAT_NAME"));
		}
		//性别
		if (getValueString("SEX_CODE").length() > 0){
			Parm.setData("SEX_CODE", this.getValue("SEX_CODE"));
			//this.messageBox("性别"+Parm.getValue("SEX_CODE"));
		}
		//科室
		if (getValueString("DEPT_CODE").length() > 0){
			Parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
			//this.messageBox("科室"+Parm.getValue("DEPT_CODE"));
		}
		//电话
		if (getValueString("CELL_PHONE").length() > 0){
			Parm.setData("CELL_PHONE", this.getValue("CELL_PHONE"));
			//this.messageBox("电话"+Parm.getValue("CELL_PHONE"));
		}
		//工龄
		if (getValueString("WORKING_YEARS").length() > 0){
			Parm.setData("WORKING_YEARS", this.getValue("WORKING_YEARS"));
			//this.messageBox("工龄"+Parm.getValue("WORKING_YEARS"));
		}
		//患者病案号
		if (getValueString("PATIENT_MR_NO").length() > 0){
			Parm.setData("PATIENT_MR_NO", this.getValue("PATIENT_MR_NO"));
			//this.messageBox("患者病案号"+Parm.getValue("PATIENT_MR_NO"));
		}
		//患者科室
		if (getValueString("PATIENT_DEPT_CODE").length() > 0){
			Parm.setData("PATIENT_DEPT_CODE", this.getValue("PATIENT_DEPT_CODE"));
			//this.messageBox("患者科室"+Parm.getValue("PATIENT_DEPT_CODE"));
		}
		//首次检验日期
		if (getValueString("FIRST_INSPECTION_DATE").length() > 0){
			Parm.setData("FIRST_INSPECTION_DATE", this.getValue("FIRST_INSPECTION_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("首次检验日期"+Parm.getValue("FIRST_INSPECTION_DATE"));
		}
		//患者传筛情况
		if (getValueString("PATIENT_PASS_SCREENING_N").equals("Y")){
			//this.messageBox("PATIENT_PASS_SCREENING_N:"+getValueString("PATIENT_PASS_SCREENING_N"));
			Parm.setData("PATIENT_PASS_SCREENING", 'N');
			//this.messageBox("患者传筛情况"+Parm.getValue("PATIENT_PASS_SCREENING"));
		}else{
			//this.messageBox("PATIENT_PASS_SCREENING_N:"+getValueString("PATIENT_PASS_SCREENING_N"));
			Parm.setData("PATIENT_PASS_SCREENING", 'Y');
			//this.messageBox("患者传筛情况"+Parm.getValue("PATIENT_PASS_SCREENING"));
		}
		//ANTIHIV
		if (getValueString("ANTIHIV").length() > 0){
			Parm.setData("ANTIHIV", this.getValue("ANTIHIV"));
			//this.messageBox("ANTIHIV"+Parm.getValue("ANTIHIV"));
		}
		//HBSAG
		if (getValueString("HBSAG").length() > 0){
			Parm.setData("HBSAG", this.getValue("HBSAG"));
			//this.messageBox("HBSAG"+Parm.getValue("HBSAG"));
		}
		//ANTIHBS
		if (getValueString("ANTIHBS").length() > 0){
			Parm.setData("ANTIHBS", this.getValue("ANTIHBS"));
			//this.messageBox("ANTIHBS"+Parm.getValue("ANTIHBS"));
		}
		//ANTIHCV
		if (getValueString("ANTIHCV").length() > 0){
			Parm.setData("ANTIHCV", this.getValue("ANTIHCV"));
			//this.messageBox("ANTIHCV"+Parm.getValue("ANTIHCV"));
		}
		//VDRL
		if (getValueString("VDRL").length() > 0){
			Parm.setData("VDRL", this.getValue("VDRL"));
			//this.messageBox("VDRL"+Parm.getValue("VDRL"));
		}
		//PATIENT_ANTIHIV
		if (getValueString("PATIENT_ANTIHIV").length() > 0){
			Parm.setData("PATIENT_ANTIHIV", this.getValue("PATIENT_ANTIHIV"));
			//this.messageBox("PATIENT_ANTIHIV"+Parm.getValue("PATIENT_ANTIHIV"));
		}
		//PATIENT_HBSAG
		if (getValueString("PATIENT_HBSAG").length() > 0){
			Parm.setData("PATIENT_HBSAG", this.getValue("PATIENT_HBSAG"));
			//this.messageBox("PATIENT_HBSAG"+Parm.getValue("PATIENT_HBSAG"));
		}
		//PATIENT_ANTIHBS
		if (getValueString("PATIENT_ANTIHBS").length() > 0){
			Parm.setData("PATIENT_ANTIHBS", this.getValue("PATIENT_ANTIHBS"));
			//this.messageBox("PATIENT_ANTIHBS"+Parm.getValue("PATIENT_ANTIHBS"));
		}
		//PATIENT_ANTIHCV
		if (getValueString("PATIENT_ANTIHCV").length() > 0){
			Parm.setData("PATIENT_ANTIHCV", this.getValue("PATIENT_ANTIHCV"));
			//this.messageBox("PATIENT_ANTIHCV"+Parm.getValue("PATIENT_ANTIHCV"));
		}
		//PATIENT_VDRL
		if (getValueString("PATIENT_VDRL").length() > 0){
			Parm.setData("PATIENT_VDRL", this.getValue("PATIENT_VDRL"));
			//this.messageBox("PATIENT_VDRL"+Parm.getValue("PATIENT_VDRL"));
		}
		//一般丢弃注射针
		if (getValueString("LNJECTION_NEEDLE").length() > 0){
			Parm.setData("LNJECTION_NEEDLE", this.getValue("LNJECTION_NEEDLE"));
			//this.messageBox("一般丢弃注射针"+Parm.getValue("LNJECTION_NEEDLE"));
		}
		//留置针
		if (getValueString("INDWELLING_NEEDLE").length() > 0){
			Parm.setData("INDWELLING_NEEDLE", this.getValue("INDWELLING_NEEDLE"));
			//this.messageBox("留置针"+Parm.getValue("INDWELLING_NEEDLE"));
		}
		//头皮针
		if (getValueString("SCALP_ACUPUNCTURE").length() > 0){
			Parm.setData("SCALP_ACUPUNCTURE", this.getValue("SCALP_ACUPUNCTURE"));
			//this.messageBox("头皮针"+Parm.getValue("SCALP_ACUPUNCTURE"));
		}
		//缝针
		if (getValueString("NEEDLE").length() > 0){
			Parm.setData("NEEDLE", this.getValue("NEEDLE"));
			//this.messageBox("缝针"+Parm.getValue("NEEDLE"));
		}
		//真空采血器
		if (getValueString("VACUUM_BLOOD_COLLECTOR").length() > 0){
			Parm.setData("VACUUM_BLOOD_COLLECTOR", this.getValue("VACUUM_BLOOD_COLLECTOR"));
			//this.messageBox("真空采血器"+Parm.getValue("VACUUM_BLOOD_COLLECTOR"));
		}
		//外科器械
		if (getValueString("SURGICAL_INSTRUMENTS").length() > 0){
			Parm.setData("SURGICAL_INSTRUMENTS", this.getValue("SURGICAL_INSTRUMENTS"));
			//this.messageBox("外科器械"+Parm.getValue("SURGICAL_INSTRUMENTS"));
		}
		//玻璃物品
		if (getValueString("GLASS_ITEMS").length() > 0){
			Parm.setData("GLASS_ITEMS", this.getValue("GLASS_ITEMS"));
			//this.messageBox("玻璃物品"+Parm.getValue("GLASS_ITEMS"));
		}
		//尖锐物品类 其他
		if (getValueString("OTHER_TYPE").length() > 0){
			Parm.setData("OTHER_TYPE", this.getValue("OTHER_TYPE"));
			//this.messageBox("尖锐物品类 其他"+Parm.getValue("OTHER_TYPE"));
		}
		//尖锐物品类 其他描述
		if (getValueString("OTHER_TYPE_DESCRIBE").length() > 0){
			Parm.setData("OTHER_TYPE_DESCRIBE", this.getValue("OTHER_TYPE_DESCRIBE"));
			//this.messageBox("尖锐物品类 其他描述"+Parm.getValue("OTHER_TYPE_DESCRIBE"));
		}
		//锐器伤发生地点
		if (getValueString("PLACE_INJURY").length() > 0){
			Parm.setData("PLACE_INJURY", this.getValue("PLACE_INJURY"));
			//this.messageBox("锐器伤发生地点"+Parm.getValue("PLACE_INJURY"));
		}
		//锐器伤发生部位
		if (getValueString("POSITION_INJURY").length() > 0){
			Parm.setData("POSITION_INJURY", this.getValue("POSITION_INJURY"));
			//this.messageBox("锐器伤发生部位"+Parm.getValue("POSITION_INJURY"));
		}
		//采血
		if (getValueString("BLOOD_COLLECTION").length() > 0){
			Parm.setData("BLOOD_COLLECTION", this.getValue("BLOOD_COLLECTION"));
			//this.messageBox("采血"+Parm.getValue("BLOOD_COLLECTION"));
		}
		//放置导管
		if (getValueString("CATHETER_PLACEMENT").length() > 0){
			Parm.setData("CATHETER_PLACEMENT", this.getValue("CATHETER_PLACEMENT"));
			//this.messageBox("放置导管"+Parm.getValue("CATHETER_PLACEMENT"));
		}
		//手术
		if (getValueString("OPERATION").length() > 0){
			Parm.setData("OPERATION", this.getValue("OPERATION"));
			//this.messageBox("手术"+Parm.getValue("OPERATION"));
		}
		//配置补液
		if (getValueString("FORMULATED_REHYDRATION").length() > 0){
			Parm.setData("FORMULATED_REHYDRATION", this.getValue("FORMULATED_REHYDRATION"));
			//this.messageBox("配置补液"+Parm.getValue("FORMULATED_REHYDRATION"));
		}
		//皮内，皮下或肌肉注射
		if (getValueString("INJECTION").length() > 0){
			Parm.setData("INJECTION", this.getValue("INJECTION"));
			//this.messageBox("皮内，皮下或肌肉注射"+Parm.getValue("INJECTION"));
		}
		//整理或清理器械
		if (getValueString("EQUIPMENT").length() > 0){
			Parm.setData("EQUIPMENT", this.getValue("EQUIPMENT"));
			//this.messageBox("整理或清理器械"+Parm.getValue("EQUIPMENT"));
		}
		//锐器伤时的操作 其他
		if (getValueString("OTHER_OPERATION").length() > 0){
			Parm.setData("OTHER_OPERATION", this.getValue("OTHER_OPERATION"));
			//this.messageBox("锐器伤时的操作 其他"+Parm.getValue("OTHER_OPERATION"));
		}
		//锐器伤时的操作 其他描述
		if (getValueString("OTHER_OPERATION_DESCRIBE").length() > 0){
			Parm.setData("OTHER_OPERATION_DESCRIBE", this.getValue("OTHER_OPERATION_DESCRIBE"));
			//this.messageBox("锐器伤时的操作 其他描述"+Parm.getValue("OTHER_OPERATION_DESCRIBE"));
		}
		//打开针头套
		if (getValueString("OPEN_NEEDLE").length() > 0){
			Parm.setData("OPEN_NEEDLE", this.getValue("OPEN_NEEDLE"));
			//this.messageBox("打开针头套"+Parm.getValue("OPEN_NEEDLE"));
		}
		//未对准或被戳破
		if (getValueString("MISALIGNMENT_PUNCTURE").length() > 0){
			Parm.setData("MISALIGNMENT_PUNCTURE", this.getValue("MISALIGNMENT_PUNCTURE"));
			//this.messageBox("未对准或被戳破"+Parm.getValue("MISALIGNMENT_PUNCTURE"));
		}
		//加药时
		if (getValueString("DOSING_TIME").length() > 0){
			Parm.setData("DOSING_TIME", this.getValue("DOSING_TIME"));
			//this.messageBox("加药时"+Parm.getValue("DOSING_TIME"));
		}
		//套回针头套
		if (getValueString("BACK_SLEEVE").length() > 0){
			Parm.setData("BACK_SLEEVE", this.getValue("BACK_SLEEVE"));
			//this.messageBox("套回针头套"+Parm.getValue("BACK_SLEEVE"));
		}
		//分开针头及针筒弯曲或折断针头
		if (getValueString("BENDING_BREAKING_NEEDLE").length() > 0){
			Parm.setData("BENDING_BREAKING_NEEDLE", this.getValue("BENDING_BREAKING_NEEDLE"));
			//this.messageBox("分开针头及针筒弯曲或折断针头"+Parm.getValue("BENDING_BREAKING_NEEDLE"));
		}
		//他人之意外扎伤
		if (getValueString("OTHERS_STABBED").length() > 0){
			Parm.setData("OTHERS_STABBED", this.getValue("OTHERS_STABBED"));
			//this.messageBox("他人之意外扎伤"+Parm.getValue("OTHERS_STABBED"));
		}
		//分合器械如装上或取下刀片
		if (getValueString("PARTING_INSTRUMENT").length() > 0){
			Parm.setData("PARTING_INSTRUMENT", this.getValue("PARTING_INSTRUMENT"));
			//this.messageBox("分合器械如装上或取下刀片"+Parm.getValue("PARTING_INSTRUMENT"));
		}
		//整理或清理物品
		if (getValueString("CLEANING_ITEMS").length() > 0){
			Parm.setData("CLEANING_ITEMS", this.getValue("CLEANING_ITEMS"));
			//this.messageBox("整理或清理物品"+Parm.getValue("CLEANING_ITEMS"));
		}
		//尖锐物品穿出收集盒
		if (getValueString("PIERCING_BOX").length() > 0){
			Parm.setData("PIERCING_BOX", this.getValue("PIERCING_BOX"));
			//this.messageBox("尖锐物品穿出收集盒"+Parm.getValue("PIERCING_BOX"));
		}
		//尖锐物品隐藏于其它物品中
		if (getValueString("HIDE_ITEMS").length() > 0){
			Parm.setData("HIDE_ITEMS", this.getValue("HIDE_ITEMS"));
			//this.messageBox("尖锐物品隐藏于其它物品中"+Parm.getValue("HIDE_ITEMS"));
		}
		//使用时破碎物
		if (getValueString("BREAK_USE").length() > 0){
			Parm.setData("BREAK_USE", this.getValue("BREAK_USE"));
			//this.messageBox("使用时破碎物"+Parm.getValue("BREAK_USE"));
		}
		//锐器伤时的动作 其他
		if (getValueString("OTHER_ACTION").length() > 0){
			Parm.setData("OTHER_ACTION", this.getValue("OTHER_ACTION"));
			//this.messageBox("锐器伤时的动作 其他"+Parm.getValue("OTHER_ACTION"));
		}
		//锐器伤时的动作 其他描述
		if (getValueString("OTHER_ACTION_DESCRIBE").length() > 0){
			Parm.setData("OTHER_ACTION_DESCRIBE", this.getValue("OTHER_ACTION_DESCRIBE"));
			//this.messageBox("锐器伤时的动作 其他描述"+Parm.getValue("OTHER_ACTION_DESCRIBE"));
		}
		//锐器伤物品曾接触过病人的血液及体液污染
		if(getValueString("CONTACT_POLLUTION_1").equals("Y")){
			Parm.setData("CONTACT_POLLUTION", "1");
			//this.messageBox("锐器伤物品曾接触过病人的血液及体液污染"+Parm.getValue("CONTACT_POLLUTION"));
		}else if(getValueString("CONTACT_POLLUTION_2").equals("Y")){
			Parm.setData("CONTACT_POLLUTION", "2");
			//this.messageBox("锐器伤物品曾接触过病人的血液及体液污染"+Parm.getValue("CONTACT_POLLUTION"));
		}else if(getValueString("CONTACT_POLLUTION_3").equals("Y")){
			Parm.setData("CONTACT_POLLUTION", "3");
			//this.messageBox("锐器伤物品曾接触过病人的血液及体液污染"+Parm.getValue("CONTACT_POLLUTION"));
		}
		//锐器伤时是否戴手套
		if (getValueString("WEAR_GLOVES_1").equals("Y")){
			Parm.setData("WEAR_GLOVES", "1");
			//this.messageBox("锐器伤时是否戴手套"+Parm.getValue("WEAR_GLOVES"));
		}else if(getValueString("WEAR_GLOVES_2").equals("Y")){
			Parm.setData("WEAR_GLOVES", "2");
			//this.messageBox("锐器伤时是否戴手套"+Parm.getValue("WEAR_GLOVES"));
		}else if(getValueString("WEAR_GLOVES_3").equals("Y")){
			Parm.setData("WEAR_GLOVES", "3");
			//this.messageBox("锐器伤时是否戴手套"+Parm.getValue("WEAR_GLOVES"));
		}
		//受伤次数
		if (getValueString("INJURED_1").equals("Y")){
			Parm.setData("INJURED", "1");
			//this.messageBox("受伤次数"+Parm.getValue("INJURED"));
		}else if(getValueString("INJURED_2").equals("Y")){
			Parm.setData("INJURED", "2");
			//this.messageBox("受伤次数"+Parm.getValue("INJURED"));
		}
		//受伤次数总共
		if (getValueString("INJURY_FREQUENCY").length() > 0){
			Parm.setData("INJURY_FREQUENCY", this.getValue("INJURY_FREQUENCY"));
			//this.messageBox("受伤次数总共"+Parm.getValue("INJURY_FREQUENCY"));
		}
		//锐器伤后处理
		if (getValueString("INJURY_TREATMENT_1").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "1");
			//this.messageBox("锐器伤后处理"+Parm.getValue("INJURY_TREATMENT"));
		}else if(getValueString("INJURY_TREATMENT_2").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "2");
			//this.messageBox("锐器伤后处理"+Parm.getValue("INJURY_TREATMENT"));
		}else if(getValueString("INJURY_TREATMENT_3").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "3");
			//this.messageBox("锐器伤后处理"+Parm.getValue("INJURY_TREATMENT"));
		}else if(getValueString("INJURY_TREATMENT_4").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "4");
			//this.messageBox("锐器伤后处理"+Parm.getValue("INJURY_TREATMENT"));
		}
		//部门负责人
		if (getValueString("DEPARTMENT_HEADS").length() > 0){
			Parm.setData("DEPARTMENT_HEADS", this.getValue("DEPARTMENT_HEADS"));
			//this.messageBox("部门负责人"+Parm.getValue("DEPARTMENT_HEADS"));
		}
		//部门负责人签字时间
		if (getValueString("DEPARTMENT_DATE").length() > 0){
			Parm.setData("DEPARTMENT_DATE", this.getValue("DEPARTMENT_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("部门负责人签字时间"+Parm.getValue("DEPARTMENT_DATE"));
		}
		//预防保健科主任
		if (getValueString("PREVENTION_HEADS").length() > 0){
			Parm.setData("PREVENTION_HEADS", this.getValue("PREVENTION_HEADS"));
			//this.messageBox("预防保健科主任"+Parm.getValue("PREVENTION_HEADS"));
		}
		//预防保健科主任签字时间
		if (getValueString("PREVENTION_DATE").length() > 0){
			Parm.setData("PREVENTION_DATE", this.getValue("PREVENTION_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("预防保健科主任签字时间"+Parm.getValue("PREVENTION_DATE"));
		}
		//医院感染管理科主任
		if (getValueString("INFECTED_HEADS").length() > 0){
			Parm.setData("INFECTED_HEADS", this.getValue("INFECTED_HEADS"));
			//this.messageBox("医院感染管理科主任"+Parm.getValue("INFECTED_HEADS"));
		}
		//医院感染管理科主任签字时间
		if (getValueString("INFECTED_DATE").length() > 0){
			Parm.setData("INFECTED_DATE", this.getValue("INFECTED_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("医院感染管理科主任签字时间"+Parm.getValue("INFECTED_DATE"));
		}
		//年龄
		if (getValueString("AGE").length() > 0){
			Parm.setData("AGE", this.getValue("AGE"));
			//this.messageBox("年龄"+Parm.getValue("AGE"));
		}
		
		//操作人
		Parm.setData("OPT_USER", Operator.getID());
		//this.messageBox("操作人"+Parm.getValue("OPT_USER"));
		//操作时间
		Parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0,10).replaceAll("-", "/"));
		//this.messageBox("操作时间"+Parm.getValue("OPT_DATE"));
		//操作IP
		Parm.setData("OPT_TERM", Operator.getIP());
		//this.messageBox("操作IP"+Parm.getValue("OPT_TERM"));
		return Parm;
	}
	
	/**
	 * 修改
	 */
	public void onUPDATE(){
			TParm Parm=new TParm();
			Parm = this.getUiDate();
			//得到暴露编号
			Parm.setData("EXPOSURE_NO", getValue("EXPOSURE_NO"));
			//this.messageBox("EXPOSURE_NO修改:" + Parm.getValue("EXPOSURE_NO"));
			TParm UpdateParm = TIOM_AppServer.executeAction("action.inf.InfExposureAction",
	                "onUpDate", Parm);
	        if (UpdateParm.getErrCode() < 0) {
	            messageBox("保存失败");
	            return;
	        }
	        messageBox("保存成功");
	}
	/**
	 * 新增
	 */
	public void onNew(){
		TParm Parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		//生成暴露号
		String caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
						"CASE_NO"); // 调用取号原则
		//增加暴露编号
		Parm = this.getUiDate();
		Parm.setData("EXPOSURE_NO", caseNo);
		
		//首次报告为报告日期
//		Parm.setData("REPORT_DATE", date.toString().substring(0, 19).replace('-', '/'));
		//this.messageBox("暴露编号:" + Parm.getValue("EXPOSURE_NO"));
//		this.messageBox("一般丢弃注射针:" + Parm.getValue("LNJECTION_NEEDLE"));
		//System.out.println("control1111111" + Parm);
		
		
		TParm NewParm = TIOM_AppServer.executeAction("action.inf.InfExposureAction",
                "onNew", Parm);
        if (NewParm.getErrCode() < 0) {
            messageBox("保存失败");
            return;
        }
        messageBox("保存成功");
		onClear();
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
        clearText();
        onInitialization();
    }
	 /**
     * 清空属性
     */
    public void clearText() {
        clearText("EXPOSURE_NO;PLACE_INJURY;POSITION_INJURY;PAT_NAME;BIRTH_DATE;SEX_CODE;DEPT_CODE;CELL_PHONE;WORKING_YEARS;PATIENT_MR_NO;PATIENT_DEPT_CODE;FIRST_INSPECTION_DATE;PATIENT_PASS_SCREENING;ANTIHIV;HBSAG;ANTIHBS;ANTIHCV;VDRL;PATIENT_ANTIHIV;PATIENT_HBSAG;PATIENT_ANTIHBS;PATIENT_ANTIHCV;PATIENT_VDRL;OTHER_TYPE_DESCRIBE;OTHER_OPERATION_DESCRIBE;OTHER_ACTION_DESCRIBE;DEPARTMENT_HEADS;DEPARTMENT_DATE;PREVENTION_HEADS;PREVENTION_DATE;INFECTED_HEADS;INFECTED_DATE;AGE;OCCURRENCE_DATE;INJURY_FREQUENCY;USER_ID;");
        this.setValue("PATIENT_PASS_SCREENING_N", "Y");
        this.setValue("INTERNSHIP", "N");
        this.setValue("LNJECTION_NEEDLE", "N");
        this.setValue("INDWELLING_NEEDLE", "N");
        this.setValue("SCALP_ACUPUNCTURE", "N");
        this.setValue("NEEDLE", "N");
        this.setValue("VACUUM_BLOOD_COLLECTOR", "N");
        this.setValue("SURGICAL_INSTRUMENTS", "N");
        this.setValue("GLASS_ITEMS", "N");
        this.setValue("OTHER_TYPE", "N");
        this.setValue("BLOOD_COLLECTION", "N");
        this.setValue("CATHETER_PLACEMENT", "N");
        this.setValue("OPERATION", "N");
        this.setValue("FORMULATED_REHYDRATION", "N");
        this.setValue("INJECTION", "N");
        this.setValue("EQUIPMENT", "N");
        this.setValue("OTHER_OPERATION", "N");
        this.setValue("OPEN_NEEDLE", "N");
        this.setValue("MISALIGNMENT_PUNCTURE", "N");
        this.setValue("DOSING_TIME", "N");
        this.setValue("BACK_SLEEVE", "N");
        this.setValue("BENDING_BREAKING_NEEDLE", "N");
        this.setValue("OTHERS_STABBED", "N");
        this.setValue("PARTING_INSTRUMENT", "N");
        this.setValue("CLEANING_ITEMS", "N");
        this.setValue("PIERCING_BOX", "N");
        this.setValue("HIDE_ITEMS", "N");
        this.setValue("BREAK_USE", "N");
        this.setValue("OTHER_ACTION", "N");
        ((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(false);
        ((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(false);
        ((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(false);
        ((TTextField) getComponent("PAT_NAME")).setEnabled(false);
		((TComboBox) getComponent("SEX_CODE")).setEnabled(false);
		((TTextFormat) getComponent("DEPT_CODE")).setEnabled(false);
        
//        this.setValue("CONTACT_POLLUTION_1", "N");
//        this.setValue("CONTACT_POLLUTION_2", "N");
//        this.setValue("CONTACT_POLLUTION_3", "N");
//        this.setValue("WEAR_GLOVES_1", "N");
//        this.setValue("WEAR_GLOVES_2", "N");
//        this.setValue("WEAR_GLOVES_3", "N");
//        this.setValue("INJURED_1", "N");
//        this.setValue("INJURED_2", "N");
//        this.setValue("INJURY_TREATMENT_1", "N");
//        this.setValue("INJURY_TREATMENT_2", "N");
//        this.setValue("INJURY_TREATMENT_3", "N");
//        this.setValue("INJURY_TREATMENT_4", "N");
        
    }




    
//    /**
//     * 打印报表
//     */
//    public void onPrint(){
//    	if(table.getRowCount()<=0){
//    		this.messageBox("没有要打印的数据");
//    		return;
//    	}
//    	TParm parm=new TParm();
//    	//第一个参数是标题参数第二个参数代表传进来的参数第三个参数为名称
//    	//表头部分
//    	parm.setData("TT","TEXT","病患列表");
//    	//Timestamp today = SystemTool.getInstance().getDate();
//    	//获取前端START_DATE和END_DATE中的日期放到报表中的相应位置
//    	String START = this.getValueString("START_DATE");
//    	String END = this.getValueString("END_DATE");
//    	String S=START.substring(0,19);
//    	String E=END.substring(0,19);
//    	parm.setData("ZBSJ","TEXT",S);
//    	parm.setData("ZBSJ2","TEXT",E);
//    	//创建新容器,便利每行的科室和日期把日期中时间的.0去掉
//    	TParm resultParm=table.getParmValue();
//    	for(int i=0;i<resultParm.getCount();i++){
//    		String optdateA=resultParm.getValue("OPT_DATE",i);
//    		String optdateB=optdateA.substring(0,19);
//    		resultParm.setData("OPT_DATE",i,optdateB);
//    		String databirthA=resultParm.getValue("DATE_BIRTH",i);
//    		String databirthB=databirthA.substring(0,19);
//    		resultParm.setData("DATE_BIRTH",i,databirthB);
//    		if(resultParm.getValue("PRG_ID",i).equals("ODI")){
//    			resultParm.setData("PRG_ID",i,"儿科");
//    		}else{
//    			resultParm.setData("PRG_ID",i,"妇科");
//    		}
//    	}
//    	
//    	
//    	//获得数据
//    	resultParm.addData("SYSTEM", "COLUMNS","MR_NO");
//    	resultParm.addData("SYSTEM", "COLUMNS","OPT_USER");
//    	resultParm.addData("SYSTEM", "COLUMNS","OPT_DATE");
//    	resultParm.addData("SYSTEM", "COLUMNS","OPT_TERM");
//    	resultParm.addData("SYSTEM", "COLUMNS","PRG_ID");
//    	resultParm.addData("SYSTEM", "COLUMNS","DATE_BIRTH");
//    	//将表格放到容器中
//    	parm.setData("T1",resultParm.getData());
//    	//表尾部分
//    	parm.setData("BW","TEXT","制作人:张磊");
//    	this.openPrintWindow(
//                "%ROOT%\\config\\prt\\REG\\d2.jhw",
//                parm);
//    }
    
	/**
	 * 病案号事件
	 */
	public void onMrNo() {

		String mrno = this.getValueString("PATIENT_MR_NO");

		if (StringUtils.isEmpty(mrno)) {
			return;
		}
		mrno = PatTool.getInstance().checkMrno(mrno);
		this.setValue("PATIENT_MR_NO", mrno);
			
		//获得病患信息
//		TParm patParm = PatTool.getInstance().getInfoForMrno(mrno);
//		String patName = PatTool.getInstance().getNameForMrno(mrno);
//		this.setValue("PAT_NAME", patName);

	}
    
}
