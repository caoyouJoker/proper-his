package com.javahis.ui.odi;


import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;

import psyg.graphic.SysObj;




/**
 * Title: 科室选择
 * Description:科室选择
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class ODITransfertypeControl extends TControl {
	private TParm parm;
	private String mrNo;//病案号	
	private String caseNo;//就诊号
	private String patName;//姓名
	private String fromDept;//姓名
	private String deptTypeFlg; //用于科室选择界面显示科室标记
	private TComboBox combobox;
	private String dayOpeCode;//日间手术标记
	
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
		dayOpeCode = parm.getValue("DAY_OPE_CODE");
//		System.out.println("---deptTypeFlg----------------"+deptTypeFlg);
		  //带出数据
		  combobox = (TComboBox) this.getComponent("DEPT_TYPE");
		if (deptTypeFlg.equals("ODI")) {
			combobox.setStringData("[[id,text],[,],[0306,介入治疗中心],[030503,手术室]]");
		} else if (deptTypeFlg.equals("OPE")) {
			String toDept = parm.getValue("TO_DEPT");
			String sql = " SELECT DEPT_CHN_DESC FROM SYS_DEPT"
					+ " WHERE DEPT_CODE = '" + toDept + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			String toDeptname = result.getValue("DEPT_CHN_DESC", 0);
			combobox.setStringData("[[id,text],[,],[0304,CCU],[" + toDept + ","
					+ toDeptname + "]]");
		} else if (deptTypeFlg.equals("ERD")) {
			String toDept = parm.getValue("TO_DEPT");
			String sql = " SELECT DEPT_CHN_DESC FROM SYS_DEPT"
					+ " WHERE DEPT_CODE = '" + toDept + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			String toDeptname = result.getValue("DEPT_CHN_DESC", 0);
			combobox.setStringData("[[id,text],[,],[" + toDept + ","
					+ toDeptname + "],[0306,介入治疗中心],[030503,手术室]]");
		}
    }
    /**
     * 生成交接单
     */
    public void onCreate() {
    	TParm action = new TParm();
    	if(this.getValue("DEPT_TYPE").equals("")){
    		this.messageBox("转入科室不能为空");
    		return;
    	}
    	if(this.getValue("DEPT_TYPE")==null){
    		this.messageBox("转入科室不能为空");
    		return;
    	}
		if (deptTypeFlg.equals("ODI")) {
			// 病区—介入
			if (this.getValue("DEPT_TYPE").equals("0306")) {
				TParm recptype = new TParm();
				recptype.setData("CASE_NO", caseNo);
				recptype.setData("TYPE_CODE", "2");
				TParm result = (TParm) this.openDialog("%ROOT%\\config\\ope\\OPEOpDetailList.x", recptype);
				
				// 查询模版信息
				TParm actionParm = new TParm();
				action.setData("MR_NO", mrNo);// 病案号
				action.setData("CASE_NO", caseNo);// 就诊号
				action.setData("PAT_NAME", patName);// 姓名
				action.setData("FROM_DEPT", fromDept); // 转出科室
				action.setData("TO_DEPT", getValue("DEPT_TYPE")); // 转入科室(介入)
				action.setData("TRANSFER_CLASS", "WT"); // 交接类型(病区-介入 WT)
				// 查询模版信息
				actionParm = this.getEmrFilePath("EMR0603022");
				action.setData("TEMPLET_PATH", actionParm.getValue(
						"TEMPLET_PATH", 0));// 交接单路径
				action.setData("EMT_FILENAME", actionParm.getValue(
						"EMT_FILENAME", 0));// 交接单名称
				action.setData("FLG", false);// 打开模版
				action.setData("DAY_OPE_CODE",this.dayOpeCode);
				action.setData("OPBOOK_SEQ", result.getValue("OPBOOK_SEQ"));// 手术申请单号
				String optChnDescSql = "SELECT OPT_CHN_DESC FROM SYS_OPERATIONICD A WHERE A.OPERATION_ICD = '"+result.getValue("OP_CODE1")+"'";
				TParm optChnDescParm = new TParm(TJDODBTool.getInstance().select(optChnDescSql)); 
				action.setData("OPT_CHN_DESC", optChnDescParm.getValue("OPT_CHN_DESC",0));// 术式
				//20170328 zhanglei 为结构化病例病区与介入室术前交接单 现实日间手术标记
		        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "日间手术":"");//日间手术标记
				// System.out.println("---action----------------"+action);
				// 调用模版
				TFrame frame = new TFrame();
				frame.init(getConfigParm().newConfig(
						"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
				if (action != null)
					frame.setParameter(action);
				frame.onInit();
				frame.setSize(1012, 739);
				frame.setLocation(200, 5);
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
			}
			// 病区—手术室(非病历交接单)
			else {
				// 拿到调用数据
				TParm Data = (TParm) parm.getData("PARM");
				// System.out.println("---Data----------------"+Data);
				// 调用程序
				this.openDialog("%ROOT%\\config\\inw\\INWTransferSheetWo.x",
						Data);
			}
		} else if (deptTypeFlg.equals("OPE")) {
			// 查询模版信息
			TParm actionParm = new TParm();
			action.setData("MR_NO", mrNo);// 病案号
			action.setData("CASE_NO", caseNo);// 就诊号
			action.setData("PAT_NAME", patName);// 姓名
			action.setData("FROM_DEPT", fromDept); // 转出科室
			action.setData("TO_DEPT", getValue("DEPT_TYPE")); // 转入科室(CCU或病区)
			action.setData("TRANSFER_CLASS", "TC/TW"); // 交接类型(介入-CCU/病区 TC/TW)
			// action.setData("OP_CODE",parm.getValue("OP_CODE")); //术式
			// 查询模版信息
			actionParm = this.getEmrFilePath("EMR0603033");
			action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",
					0));// 交接单路径
			action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",
					0));// 交接单名称
			action.setData("FLG", false);// 打开模版
			action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ"));// 手术申请单号
			action.setData("DAY_OPE_CODE",this.dayOpeCode);
			// System.out.println("---action----------------"+action);
			
			//20170328 zhanglei 为结构化病例病区与介入室术前交接单 现实日间手术标记
	        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "日间手术":"");//日间手术标记

	        
			// 调用模版
			TFrame frame = new TFrame();
			frame.init(getConfigParm().newConfig(
					"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
			if (action != null)
				frame.setParameter(action);
			frame.onInit();
			frame.setSize(1012, 739);
			frame.setLocation(200, 5);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
		}
		// 急诊-病区/手术室/介入室
		else if (deptTypeFlg.equals("ERD")) {
			// 查询模版信息
			TParm actionParm = new TParm();
			action.setData("MR_NO", mrNo);// 病案号
			action.setData("CASE_NO", caseNo);// 就诊号(住院)
			action.setData("REG_CASE_NO", parm.getValue("REG_CASE_NO"));// 就诊号(门急诊)
			action.setData("PAT_NAME", patName);// 姓名
			action.setData("FROM_DEPT", fromDept); // 转出科室
			action.setData("TO_DEPT", getValue("DEPT_TYPE")); // 转入科室
			String transferClass = "";
			if (StringUtils.equals("0306", getValueString("DEPT_TYPE"))) {
				// 急诊-介入
				transferClass = "ET";
			} else if (StringUtils.equals("030503", getValueString("DEPT_TYPE"))) {
				// 急诊-手术室
				transferClass = "EO";
			} else {
				// 急诊-病区
				transferClass = "EW";
			}
			action.setData("TRANSFER_CLASS", transferClass); // 交接类型
			// 查询模版信息
			actionParm = this.getEmrFilePath("EMR06030601");
			action.setData("TEMPLET_PATH", actionParm.getValue("TEMPLET_PATH",
					0));// 交接单路径
			action.setData("EMT_FILENAME", actionParm.getValue("EMT_FILENAME",
					0));// 交接单名称
			action.setData("FLG", false);// 打开模版
			action.setData("DAY_OPE_CODE",this.dayOpeCode);
			action.setData("OPBOOK_SEQ", parm.getValue("OPBOOK_SEQ"));// 手术申请单号
			//20170328 zhanglei 为结构化病例病区与介入室术前交接单 现实日间手术标记
	        action.setData("DAY_OPE_FLG","Y".equals(parm.getValue("DAY_OPE_FLG")) ? "日间手术":"");//日间手术标记
			// 调用模版
			TFrame frame = new TFrame();
			frame.init(getConfigParm().newConfig(
					"%ROOT%\\config\\emr\\EMRTransferWordUI.x"));
			if (action != null)
				frame.setParameter(action);
			frame.onInit();
			frame.setSize(1012, 739);
			frame.setLocation(200, 5);
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
		}
    	
    	this.closeWindow();		
	    
    	
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
