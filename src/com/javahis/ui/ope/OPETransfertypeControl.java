package com.javahis.ui.ope;


import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;




/**
 * Title: 科室选择
 * Description:科室选择
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class OPETransfertypeControl extends TControl {
	private TParm parm;
	private String mrNo;//病案号	
	private String caseNo;//就诊号
	private String patName;//姓名
	private String fromDept;//姓名
	private String deptTypeFlg; //用于科室选择界面显示科室标记	

	private String dayopeflg;//日间手术标记 zhanglei 20170329

	public void onInit() {
		super.onInit();
		parm = (TParm) getParameter();//查询信息
		//若无此信息返回
		if (null == parm) {
			return;
		}
		mrNo = parm.getValue("MR_NO");
		caseNo = parm.getValue("CASE_NO");
		patName = parm.getValue("PAT_NAME");
		fromDept = parm.getValue("FROM_DEPT");
		deptTypeFlg = parm.getValue("DEPT_TYPE_FLG");//用于科室选择界面显示科室标记
		//		System.out.println("---deptTypeFlg----------------"+deptTypeFlg);
		dayopeflg = parm.getValue("DAY_OPE_FLG");//日间手术标记 zhanglei 20170329	
	}
	/**
	 * 生成交接单
	 */
	public void onCreate() {
		/** modified by WangQing 20170411 -start*/
		TParm action = new TParm();
		if(this.getValueString("DEPT_TYPE").trim().equals("") || this.getValue("DEPT_TYPE")==null){
			this.messageBox("转入科室不能为空");
			return;
		}
		/*modified by Eric 20170518 */
		TParm actionParm = new TParm();// 病历模板
		if(fromDept.equals("030503")){// 手术室
			if(getValue("DEPT_TYPE").equals("0303")){// ICU
				actionParm = this.getEmrFilePath("EMR0603055");
				action.setData("TRANSFER_CLASS", "OI"); // 交接类型
			}else{// CCU/病区
				actionParm = this.getEmrFilePath("EMR0603088");
				action.setData("TRANSFER_CLASS", "OC/OW"); // 交接类型
			}		
		}else{// 介入室
			actionParm = this.getEmrFilePath("EMR0603033");
			action.setData("TRANSFER_CLASS", "TC/TW"); // 交接类型
		}
		
		
		action.setData("MR_NO", mrNo);// 病案号
		action.setData("CASE_NO", caseNo);// 就诊号
		action.setData("PAT_NAME", patName);// 姓名
		action.setData("FROM_DEPT", fromDept); // 转出科室
		action.setData("TO_DEPT", getValue("DEPT_TYPE")); // 转入科室
		// action.setData("OP_CODE",parm.getValue("OP_CODE")); //术式
		action.setData("DAY_OPE_FLG", dayopeflg);//日间手术标记 zhanglei 20170329
		
		/*modified by Eric 20170518*/
//		action.setData("TEMPLET_PATH", parm.getValue("TEMPLET_PATH"));// 交接单路径
//		action.setData("EMT_FILENAME", parm.getValue("EMT_FILENAME"));// 交接单名称
		action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",0));// 交接单路径
		action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",0));// 交接单名称
		
		
		action.setData("FLG", false);// 打开模版
		action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ"));// 手术申请单号
//		action.setData("TRANSFER_CLASS", parm.getValue("TRANSFER_CLASS")); // 交接类型
		// 调用模版
		this.openWindow("%ROOT%\\config\\emr\\EMRTransferWordUI.x", action);
		this.closeWindow();		
		/** modified by WangQing 20170411 -end*/
	}

	/**
	 * 得到EMR路径
	 */
	public TParm getEmrFilePath(String subclassCode){
		String sql=" SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE," +
				" A.TEMPLET_PATH FROM EMR_TEMPLET A"+
				" WHERE A.SUBCLASS_CODE = '"+subclassCode+ "'";
		TParm result = new TParm();
		result = new TParm(TJDODBTool.getInstance().select(sql)); 
		//    	System.out.println("---result----------------getEmrFilePath"+result);
		return result;
	}
	
}
