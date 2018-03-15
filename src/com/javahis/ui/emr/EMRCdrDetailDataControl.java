package com.javahis.ui.emr;

import java.awt.Window;
import java.io.File;

import jdo.emr.EMRCDRSummaryInfoTool;
import jdo.emr.EMRCdrTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;

/**
 * <p>
 * Title: CDR明细查询界面
 * </p>
 * 
 * <p>
 * Description: CDR明细查询界面
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangb 2015.5.16
 * @version 1.0
 */
public class EMRCdrDetailDataControl extends TControl {
	private TParm parameterParm;// 页面传参
	private TTable tableData;// 明细数据Table
	private String tempPath;
	
    public EMRCdrDetailDataControl() {
        super();
    }
    
    /**
     * 初始化方法
     */
    public void onInit() {
    	Window window = (Window) this.getComponent("UI");
	    window.setAlwaysOnTop(true);
    	super.onInit();
		tempPath = "C:\\JavaHisFile\\temp\\pdf";
		//this.addEventListener("TABLE_PRESENT_ILLNESS->", TTableEvent.)
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		
    	Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				parameterParm = (TParm) obj;
				
				if (parameterParm == null) {
					this.messageBox("页面传参错误");
				}
			} else {
				this.messageBox("页面传参错误");
			}
		}
		// 初始化页面
		this.onInitPage();
    }
    
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		
		tableData = getTable("TABLE_DATA");
		// 页面标题
		String title = "";
		// 数据表格列标题
		String tableHeader = "";
		// 数据表格列数据
		String tableParmMap = "";
		// 数据表格列对齐方式
		String tableAlignData = "";
		// 数据锁定列
		String lockColumns = "";
		// 数据表格查询结果
		TParm result = new TParm();
		
		if (null != parameterParm) {
			if (StringUtils.equals("1", parameterParm.getValue("DATA_TYPE"))) {
				title = "就诊记录";
				tableHeader = "就诊类型,60,ADM_TYPE_COMBO;就诊日期,80,timestamp,yyyy/MM/dd;离院日期,80,timestamp,yyyy/MM/dd;就诊科室,80;病区/诊区,100;经治医生,80;主治医生,80,OPERATOR_COMBO;护理等级,60;检伤分级,60;调阅护理表单,80,boolean";
				tableParmMap = "ADM_TYPE_DESC;ADM_DATE_DESC;DISCHARGE_DATE;DEPT_DESC;AREA_DESC;VS_DR_NAME;ATTEND_DR_CODE;NURSING_CLASS;TRIAGE_LEVEL;NIS_REPORT";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left";
				lockColumns = "0,1,2,3,4,5,6,7,8";
				result = EMRCDRSummaryInfoTool.getInstance().queryVisitRecord(parameterParm);
				tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seeNisReport");
			} else if (StringUtils.equals("2", parameterParm.getValue("DATA_TYPE"))) {
				title = "主诉现病史";
				tableHeader = "病人主诉,500;客观描述,500;记录时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "SUBJECTIVE;OBJECTIVE;VISIT_DATE;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left";
				lockColumns = "all";
				result = EMRCDRSummaryInfoTool.getInstance().queryPresentIllness(parameterParm);
			} else if (StringUtils.equals("3", parameterParm.getValue("DATA_TYPE"))) {
				title = "既往史";
				tableHeader = "过去病史,400;家族史,400;记录时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "PAST_HISTORY;FAMILY_HISTORY;VISIT_DATE;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left";
				lockColumns = "all";
				result = EMRCDRSummaryInfoTool.getInstance().queryMedicalHistory(parameterParm);
			} else if (StringUtils.equals("4", parameterParm.getValue("DATA_TYPE"))) {
				title = "过敏史";
				tableHeader = "过敏源,250;过敏症状,180;过敏严重程度,100;记录时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "ALLERGEN_NAME;SYMPTOM;SERIOUS_LEVEL;VISIT_DATE;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left";
				lockColumns = "all";
				result = EMRCDRSummaryInfoTool.getInstance().queryAllergyHistory(parameterParm);
			} else if (StringUtils.equals("5", parameterParm.getValue("DATA_TYPE"))) {
				title = "诊断";
				tableHeader = "诊断类型,100;诊断编码,80;诊断名称,200;主诊断,50,boolean;诊断时间,160,Timestamp,yyyy/MM/dd HH:mm:ss;备注,120;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "DIAG_TYPE;ICD_CODE;DIAG_DESC;IS_MAIN_DIAG;OPT_DATE;DIAG_DEFINITION;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,right;2,left;4,left;5,left;6,left;7,left;8,left;9,left";
				lockColumns = "all";
				result = EMRCdrTool.getInstance().getDiagnosisData(parameterParm);
			} else if (StringUtils.equals("9", parameterParm.getValue("DATA_TYPE"))) {
				
				result = EMRCDRSummaryInfoTool.getInstance().queryOperation(parameterParm);
				
				// 手术table勾选监听事件
				tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "selectCheckBox");
			} else if (StringUtils.equals("10", parameterParm.getValue("DATA_TYPE"))) {
				title = "治疗";
				tableHeader = "治疗项目,240;频率,80;天数,50;次数,50;医嘱备注,230;急作,30,boolean;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "TR_DESC;FREQUENCY;TR_DAYS;QTY;DR_NOTE;IS_URGENT;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,right;3,right;4,left;6,left;7,left;8,left;9,left";
				lockColumns = "0,1,2,3,4,5,6,7,8,9";
				result = EMRCDRSummaryInfoTool.getInstance().queryTreatment(parameterParm);
			} else if (StringUtils.equals("11", parameterParm.getValue("DATA_TYPE"))) {
				title = "用血";
				tableHeader = "血液种类,150;数量,60;单位,60;ABO型血,90;RH(D),80;主测,80;次测,80;配血结果,90;申请科室,90;申请医生,80;申请时间,90;当前状态,113;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "BLDPROD_DESC;VOLUME;UNIT_DESC;BLOOD_TYPING;RH_TYPING;MAIN_CROSS_TEST;SUB_CROSS_TEST;CROSS_MATCH;DEPT_DESC;VS_DR_NAME;ORDER_TIME;STATUS;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,right;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left";
				lockColumns = "all";
				result = EMRCdrTool.getInstance().getBmsData(parameterParm);
			} else if (StringUtils.equals("12", parameterParm.getValue("DATA_TYPE"))) {
				title = "会诊";
				tableHeader = "申请时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;申请科室,80;会诊类型,80;会诊原因,100;病情摘要,150;会诊科室,80;接受时间,80;回复时间,80;会诊重点说明,80;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "ORDER_TIME;ORDER_DEPT;CONSULT_KIND;CONSULT_REASON;ILLNESS_STATE;CONSULT_DEPT_NAME;ACCEPT_TIME;REPLY_DATE;CONSULT_REPORT;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;10,left;11,left;12,left;13,left";
				lockColumns = "0,1,2,3,4,5,6,7,8,10,11,12,13,14";
				result = EMRCdrTool.getInstance().getConsult(parameterParm);
				//tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seeConsultReport");
			} else if (StringUtils.equals("13", parameterParm.getValue("DATA_TYPE"))) {
				title = "病历文书";
				tableHeader = "病历种类,180;人员,100;时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;归档时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;当前状态,100;调阅病历,80,boolean;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
				tableParmMap = "CHART_NAME;EDIT_USER;EDIT_DATE;CONFIRM_TIME;STATUS;WORD;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;6,left;7,left;8,left;9,left";
				lockColumns = "0,1,2,3,4,6,7,8,9";
				result = EMRCdrTool.getInstance().getFileData(parameterParm);
				
				// 病历文书table勾选监听事件_调阅病历
				tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seePdfWord");
			}
			if(StringUtils.equals("9", parameterParm.getValue("DATA_TYPE"))){
				tableData.setParmValue(result);
			}else{
				// 设置页面标题
				this.setTitle(title);
				// 设置表格列
				tableData.setHeader(tableHeader);
				tableData.setParmMap(tableParmMap);
				tableData.setColumnHorizontalAlignmentData(tableAlignData);
				tableData.setLockColumns(lockColumns);
				tableData.setParmValue(result);
				if (StringUtils.equals("1", parameterParm.getValue("DATA_TYPE"))){
					for(int i = 0; i < result.getCount(); i++){
						if(!result.getValue("ADM_TYPE",i).equals("I"))
							tableData.setLockCell(i, "NIS_REPORT", true);
					}
				}
			}
		}
	}
	
	
	
	/**
	 * 手术 表格--麻醉用药 、术中监护 、调阅病历  勾选事件
	 * 
	 * @param obj
	 */
	public void selectCheckBox(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		
		TFrame tFrame = (TFrame) this.getComponent("UI");//获得页面
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		TParm parm = new TParm();
		int column = table.getSelectedColumn();
		boolean flg = false;
		if (column == 14 && "Y".equals(tableParm.getValue("WORD", row))) {//调阅病历
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//清空其他列
				}
				if(tableParm.getValue("OPEING", i).equals("Y")){
					table.setItem(i, "OPEING", "N");
				}
			}
			parm.setData("CASE_NO",tableParm.getValue("CASE_NO", row));
			parm.setData("OPE_BOOK_NO", tableParm.getValue("APPLY_NO", row));
			TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
			TParm fileParm = new TParm();
			for (int i = 0; i < pathData.getCount(); i++) {
				String fileName = pathData.getValue("FILE_NAME", i) + ".pdf";
				String filePath = pathData.getValue("FILE_PATH", i);
				parm.setData("FILE_NAME", fileName);
				Runtime runtime = Runtime.getRuntime();
				// 调阅分布式存储病历
				fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
				byte data[] = (byte[]) fileParm.getData("FILE_DATA");
				if (data == null) {
					messageBox_("服务器上没有找到文件 " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
					return;
				}
				try {
					FileTool.setByte(tempPath + "\\" + fileName, data);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				try {
					// 打开文件
					runtime.exec("rundll32 url.dll FileProtocolHandler "
							+ tempPath + "\\" + fileName);
					flg = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if(flg){
				tFrame.setExtendedState(TFrame.ICONIFIED); //窗口最小化
			}
			//table.setItem(row, "WORD", "N");
		}else{
				TTable table1 = (TTable) this.getComponent("TABLE1");//用药记录表格;
				TTable table2 = (TTable) this.getComponent("TABLE2");//体征监测表格;
				TTable tableEvent = (TTable) this.getComponent("TABLE_EVENT");//术中事件
				TTabbedPane tTabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_0");
				
				if (column == 13 &&	"Y".equals(tableParm.getValue("OPEING", row))) {
					
					tTabbedPane.setEnabledAt(0, false);//用药记录页签
					tTabbedPane.setEnabledAt(1, false);//体征监测页签
					tTabbedPane.setEnabledAt(2, false);//术中监护页签
					
					table1.removeRowAll();//清空用药记录表格
					table2.removeRowAll();//清空体征监测表格
					tableEvent.removeRowAll();//清空术中事件表格
					for(int i = 0;i < tableParm.getCount();i++){
						if(tableParm.getValue("OPEING", i).equals("Y")&&i!=row){
							table.setItem(i, "OPEING", "N");
						}
						if(tableParm.getValue("WORD", i).equals("Y")){
							table.setItem(i,"WORD","N");//清空其他列
						}
					}
					parm.setData("OPE_BOOK_NO", tableParm.getValue("APPLY_NO", row));
					parm.setData("ADM_TYPE", "I");
					TParm result=EMRCdrTool.getInstance().getOpeEventData(parm);
					int count1 = 0;
					int count2 = 0;
					if(result.getCount()>0){//术中事件
						count1++;
						tTabbedPane.setSelectedIndex(0);
						tableEvent.setParmValue(result);
						tTabbedPane.setEnabledAt(0, true);
					}
					result=EMRCdrTool.getInstance().getOpeAnaData(parm);
					if(result.getCount()>0){//用药记录
						count2++;
						if(count1 == 0){
							tTabbedPane.setSelectedIndex(1);
						}
						table1.setParmValue(result);
						tTabbedPane.setSelectedIndex(1);
						tTabbedPane.setEnabledAt(1, true);
					}
					result=EMRCdrTool.getInstance().getOpeLisenerData(parm);
					if(result.getCount()>0){//体征监测
						if(count1 == 0 && count2 == 0){
							tTabbedPane.setSelectedIndex(2);
						}
						table2.setParmValue(result);
						tTabbedPane.setEnabledAt(2, true);
					}
					
			}
		}  
	}
	
	/**
	 * 病历文书--调阅病历事件
	 * 
	 * @param obj
	 */
	public void seePdfWord(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		TFrame tFrame = (TFrame) this.getComponent("UI");//获得页面
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		boolean flg = false;
		if ("Y".equals(tableParm.getValue("WORD", row))) {
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//清空其他列
				}
			}
			String fileName = tableParm.getValue("FILE_NAME", row) + ".pdf";
			String filePath = tableParm.getValue("FILE_PATH", row);
			Runtime runtime = Runtime.getRuntime();
			// 调阅分布式存储病历
			TParm fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
			byte data[] = (byte[]) fileParm.getData("FILE_DATA");
			if (data == null) {
				messageBox_("服务器上没有找到文件 " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
				return;
			}
			try {
				FileTool.setByte(tempPath + "\\" + fileName, data);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			try {
				// 打开文件
				runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
						+ "\\" + fileName);
				flg = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		if(flg){
			tFrame.setExtendedState(TFrame.ICONIFIED); //窗口最小化
		}
	}
	
	/**
	 * 调阅护理记录
	 * @param obj
	 */
	public void seeNisReport(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		TFrame tFrame = (TFrame) this.getComponent("UI");//获得页面
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		boolean flg = false;
		if("Y".equals(tableParm.getValue("NIS_REPORT", row))){
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("NIS_REPORT", i).equals("Y")){
					table.setItem(i,"NIS_REPORT","N");//清空其他列
				}
			}
			SystemTool.getInstance().OpeNisFormList(tableParm.getValue("CASE_NO",row), parameterParm.getValue("MR_NO"));
			flg = true;
		}
		if(flg){
			tFrame.setExtendedState(TFrame.ICONIFIED); //窗口最小化
		}
	}
	
    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
}
