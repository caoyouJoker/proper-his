package com.javahis.ui.pes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.pes.PESTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:点评病历选择
 * </p>
 * 
 * <p>
 * Description:点评病历选择
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhangp 2012.8.2
 * @version 1.0
 */
public class PESRxSelectControl extends TControl {
	private static TTable tableL;
	private static TTable tableR;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/')
				+ " 00:00:00");
		setValue("EVA_DATE", date.toString().substring(0, 4)
				+ date.toString().substring(5, 7));
		setValue("EVAL_CODE", Operator.getID());
		this.setValue("ADM_TYPE","OE");
		setValue("REGION_CODE", Operator.getRegion());
		tableL = (TTable) getComponent("TABLE1");
		tableR = (TTable) getComponent("TABLE2");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		onClearI();
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		TParm parm = new TParm();
		parm.setData("START_DATE", date_s);
		parm.setData("END_DATE", date_e);
		if (!getValueString("DEPT").equals("")) {
			parm.setData("DEPT_CODE", getValueString("DEPT"));
		}
		if (!getValueString("DOCTOR").equals("")) {
			parm.setData("DR_CODE", getValueString("DOCTOR"));
		}
		if (!getValueString("ANTIBIOTIC_CODE").equals("")) {
			parm.setData("ANTIBIOTIC_CODE", getValueString("ANTIBIOTIC_CODE"));
		}
		//$--- modify caoyong 20140213 start
		 if("".equals(getValueString("ADM_TYPE"))){
			 this.messageBox("门急住别不能为空");
			 return;
		  } else if("OE".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
          }else if("O".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE_O",getValueString("ADM_TYPE"));
          }else if("E".equals(getValueString("ADM_TYPE"))){
        	 parm.setData("ADM_TYPE_E",getValueString("ADM_TYPE"));
          }
			//$--- modify caoyong 20140213 end
		if (getValueString("PES_TYPE").equals("")) {
			messageBox("请选择点评类别");
			return;
		} else if (getValueString("PES_TYPE").equals("02")) {
			parm.setData("PES_TYPE", getValueString("PES_TYPE"));
		}
		TParm result =new TParm();
		if ("I".equals(getValueString("ADM_TYPE"))) {
			result =PESTool.getInstance().selectinpNo(parm);
			
		  }else{
			  
			  result = PESTool.getInstance().selectRxNo(parm);
	           
		  }
		 gridBind(result,getValueString("ADM_TYPE"));
		
	}

	/**
	 * 向右按钮事件
	 */
	public void onRight() {
		tableL.acceptText();
		tableR.acceptText();
		int countR = tableR.getRowCount();
		
		TParm tmpParm = tableR.getParmValue();
		
		
		if (tmpParm == null) {
			tmpParm = new TParm();
		}
		TParm leftParm = tableL.getParmValue();
		String inpNo=leftParm.getValue("IPD_NO",0);
		//this.messageBox(""+inpNo);
		List list = new ArrayList();
		for (int i = 0; i < leftParm.getCount("FLG"); i++) {
			if (leftParm.getValue("FLG", i).equals("Y")) {
				tmpParm.addData("FLG", "N");
				tmpParm.addData("SEQ_NO", countR + 1);
				//$-----------MODIFY caoyong 20130213 start
				if(!"".equals(inpNo)&&inpNo!=null){
					tmpParm.addData("IN_DATE", leftParm.getValue("IN_DATE", i)
							.substring(0, 4)
							+ "/"
							+ leftParm.getValue("IN_DATE", i).substring(5, 7)
							+ "/"
							+ leftParm.getValue("IN_DATE", i).substring(8, 10));
					tmpParm.addData("VS_DR_CODE", leftParm.getValue("VS_DR_CODE", i));
					tmpParm.addData("IPD_NO", leftParm.getValue("IPD_NO", i));
				}else{
					
				tmpParm.addData("ADM_DATE", leftParm.getValue("ADM_DATE", i)
						.substring(0, 4)
						+ "/"
						+ leftParm.getValue("ADM_DATE", i).substring(5, 7)
						+ "/"
						+ leftParm.getValue("ADM_DATE", i).substring(8, 10));
				tmpParm.addData("DR_CODE", leftParm.getValue("DR_CODE", i));
				tmpParm.addData("RX_NO", leftParm.getValue("RX_NO", i));
				
				}
				//$-----------MODIFY caoyong 20130213 end
				tmpParm.addData("DEPT_CODE", leftParm.getValue("DEPT_CODE", i));
				tmpParm.addData("MR_NO", leftParm.getValue("MR_NO", i));
				tmpParm.addData("PAT_NAME", leftParm.getValue("PAT_NAME", i));
				tmpParm.addData("CASE_NO", leftParm.getValue("CASE_NO", i));
				
				countR++;
				list.add(i);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			leftParm.removeRow(Integer.valueOf("" + list.get(i)) - i);
		}
		for (int i = 0; i < tmpParm.getCount("FLG"); i++) {

		}
		tableR.setParmValue(tmpParm);
		tableL.setParmValue(leftParm);
		setValue("COUNT", tmpParm.getCount("FLG"));
	}

	/**
	 * 向左按钮事件
	 */
	public void onLeft() {
		tableR.acceptText();
		tableL.acceptText();
		TParm tmpParm = tableL.getParmValue();
		if (tmpParm == null) {
			tmpParm = new TParm();
		}
		TParm rightParm = tableR.getParmValue();
		String inpNo=rightParm.getValue("IPD_NO",0);
		//this.messageBox(""+inpNo);
		List list = new ArrayList();
		for (int i = 0; i < rightParm.getCount("FLG"); i++) {
			if (rightParm.getValue("FLG", i).equals("Y")) {
				tmpParm.addData("FLG", "N");
				//$-----------MODIFY caoyong 20130213 start
				if(!"".equals(inpNo)&&inpNo!=null){
				tmpParm.addData("IN_DATE", rightParm.getValue("IN_DATE", i)
						.substring(0, 4)
						+ "/"
						+ rightParm.getValue("IN_DATE", i).substring(5, 7)
						+ "/"
						+ rightParm.getValue("IN_DATE", i).substring(8, 10));
				tmpParm.addData("VS_DR_CODE", rightParm.getValue("VS_DR_CODE", i));
				tmpParm.addData("IPD_NO", rightParm.getValue("IPD_NO", i));
				}else{
					tmpParm.addData("ADM_DATE", rightParm.getValue("ADM_DATE", i)
							.substring(0, 4)
							+ "/"
							+ rightParm.getValue("ADM_DATE", i).substring(5, 7)
							+ "/"
							+ rightParm.getValue("ADM_DATE", i).substring(8, 10));
					tmpParm.addData("RX_NO", rightParm.getValue("RX_NO", i));
					tmpParm.addData("DR_CODE", rightParm.getValue("DR_CODE", i));
				}
				//$-----------MODIFY caoyong 20130213 end
				tmpParm.addData("CASE_NO", rightParm.getValue("CASE_NO", i));
				tmpParm.addData("DEPT_CODE", rightParm.getValue("DEPT_CODE", i));
				tmpParm.addData("MR_NO", rightParm.getValue("MR_NO", i));
				tmpParm.addData("PAT_NAME", rightParm.getValue("PAT_NAME", i));
				
				list.add(i);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			rightParm.removeRow(Integer.valueOf("" + list.get(i)) - i);
		}
		int seq = 1;
		for (int i = 0; i < rightParm.getCount("FLG"); i++) {
			rightParm.setData("SEQ_NO", i, seq);
			seq++;
		}
		tableL.setParmValue(tmpParm);
		tableR.setParmValue(rightParm);
		setValue("COUNT", rightParm.getCount("FLG"));
	}

	/**
	 * 保存
	 */
	public void onSave() {
		tableR.acceptText();
		if (tableR.getRowCount() < 1) {
			messageBox("无记录，请选择");
			return;
		}
		if (getValueString("PES_TYPE").equals("")) {
			messageBox("请选择点评类别");
			return;
		}
		if (getValueString("EVA_DATE").equals("")) {
			messageBox("请输入点评期间");
			return;
		}
		if (getValueString("EVAL_CODE").equals("")) {
			messageBox("请选择点评人员");
			return;
		}
		TParm parm = new TParm();
		int seq_no = 1;
		boolean success = true;
		TParm result =new TParm();
		String inpNo=tableR.getParmValue().getValue("IPD_NO",0);
		
		for (int i = 0; i < tableR.getRowCount(); i++) {
			parm = tableR.getParmValue().getRow(i);
			parm.setData("TYPE_CODE", getValue("PES_TYPE"));
			parm.setData("PES_NO", getValue("EVA_DATE"));
			parm.setData("EVAL_CODE", getValue("EVAL_CODE"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("SEQ_NO", seq_no);
			Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
			parm.setData("PAT_NAME", pat.getName());
			parm.setData("AGE", StringUtil.showAge(pat.getBirthday(),
					StringTool.getTimestamp(new Date())));
			
			if("".equals(inpNo)||inpNo==null){
			     result = TIOM_AppServer.executeAction("action.pes.PESAction",
					"saveSelectRx", parm);//门诊
			}else{//住院
				result = TIOM_AppServer.executeAction("action.pes.PESAction",
						"saveSelectIpdRx", parm);//门诊
			}
			if (result.getErrCode() < 0) {
				messageBox("插入失败");
				success = false;
				return;
			}
			seq_no = result.getInt("SEQ_NO");
		}
		if (success) {
			if("".equals(inpNo)||inpNo==null){
			    result = TIOM_AppServer.executeAction("action.pes.PESAction","insertPESResult", parm);
			}else{
				 result = TIOM_AppServer.executeAction("action.pes.PESAction","insertIDPPESResult", parm);
			}
					
			if (result.getErrCode() < 0) {
				messageBox("插入失败");
				return;
			}
			messageBox("插入成功");
			
		}
		if("".equals(inpNo)||inpNo==null){
		 result = TIOM_AppServer.executeAction("action.pes.PESAction",
				"updatePESOPDMD", parm);
		}
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		tableR.acceptText();
		// 得到UI对应控件对象的方法
		TParm parm = tableR.getParmValue();
		if (null == parm || parm.getCount("FLG") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(tableR, "处方点评处方列表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		TParm parm = new TParm();
		tableL.setParmValue(parm);
		tableR.setParmValue(parm);
		this.setValue("ADM_TYPE","OE");
		this.clearValue("COUNT;NUM");
	}
	
	
	public void onClearI() {
		TParm parm = new TParm();
		tableL.setParmValue(parm);
		tableR.setParmValue(parm);
		this.clearValue("COUNT;NUM");
	}

	public void onRam() {
		int num = getValueInt("NUM");
		num++;
		int count = 0;
		tableL.acceptText();
		TParm parmL = tableL.getParmValue();
		for (int i = 0; i < parmL.getCount("FLG"); i++) {
			parmL.setData("FLG", i, "N");
			if (i % num == 0) {
				parmL.setData("FLG", i, "Y");
				count++;
			}
		}
		tableL.setParmValue(parmL);
		messageBox("选择了" + count + "个处方");
	}
	/**
	 * 改变GRID
	 * add caoyong 20140213
	 *            
	 */
	public void SelectItem(){
		TParm result =new TParm();
			 gridBind(result,this.getValueString("ADM_TYPE"));
			
	}
	/**
	 * 绑定GRID
	 * add caoyong 20140213
	 * @param data
	 *            TParm 数据
	 * @param type
	 *            
	 */
	private void gridBind( TParm data,String type) {
		TTable table = (TTable) this.getComponent("Table");
		if (!"I".equals(type)) {
			// 设置表头
			tableL.setHeader("选,30,boolean;就诊日期,100;科室,100,DEPT;医生,100,DOCTOR;病案号,100;姓名,100");
			tableL.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// 设置所有列不可编辑
			// 设置焦点顺序
			tableL.setFocusIndexList("0,1,2,3,4,5,64");
			// 设置对其方式
			tableL.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;");
			// 设定数据对应项 parmMap
			tableL.setParmMap("FLG;ADM_DATE;DEPT_CODE;DR_CODE;MR_NO;PAT_NAME;RX_NO;CASE_NO");
			
			tableR.setHeader("选,30,boolean;序号,60;就诊日期,100;科室,100,DEPT;医生,100,DOCTOR;病案号,100;姓名,100;处方号,100");
			tableR.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// 设置所有列不可编辑
			// 设置焦点顺序
			tableR.setFocusIndexList("0,1,2,3,4,5,6,7,8");
			// 设置对其方式
			tableR.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left");
			// 设定数据对应项 parmMap
			tableR.setParmMap("FLG;SEQ_NO;ADM_DATE;DEPT_CODE;DR_CODE;MR_NO;PAT_NAME;RX_NO");
			tableL.setParmValue(data);// 数据绑定
			tableL.retrieve();
		} else{
			tableL.setHeader("选,30,boolean;入院日期,100;科室,100,DEPT;医生,100,DOCTOR;住院号,100;病案号,100;姓名,100");
			tableL.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// 设置所有列不可编辑
			// 设置焦点顺序
			tableL.setFocusIndexList("0,1,2,3,4,5,6");
			// 设置对其方式
			tableL.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;");
			// 设定数据对应项 parmMap
			tableL.setParmMap("FLG;IN_DATE;DEPT_CODE;VS_DR_CODE;IPD_NO;MR_NO;PAT_NAME;CASE_NO");
			
			tableR.setHeader("选,30,boolean;序号,60;入院日期,100;科室,100,DEPT;医生,100,DOCTOR;住院号,100;病案号,100;姓名,100");
			tableR.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12");// 设置所有列不可编辑
			// 设置焦点顺序
			tableR.setFocusIndexList("0,1,2,3,4,5,6,7,8");
			// 设置对其方式
			tableR.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,left;7,left");
			// 设定数据对应项 parmMap
			tableR.setParmMap("FLG;SEQ_NO;IN_DATE;DEPT_CODE;VS_DR_CODE;IPD_NO;MR_NO;PAT_NAME;CASE_NO");
			tableL.setParmValue(data);// 数据绑定
			
			tableL.retrieve();
		}
	}
	
	
}
