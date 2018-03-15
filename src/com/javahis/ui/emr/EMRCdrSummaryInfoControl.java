package com.javahis.ui.emr;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JScrollPane;

import jdo.emr.EMRCDRSummaryInfoTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.tui.DMessageIO;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdiUtil;
import com.sun.awt.AWTUtilities;

/**
 * <p>
 * Title: CDR综合查询界面
 * </p>
 * 
 * <p>
 * Description: CDR综合查询界面
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
 * @author wangb 2015.5.12
 * @version 1.0
 */
public class EMRCdrSummaryInfoControl extends TControl {
	
	private TPanel tPanel1;
	private TPanel tPanel2;
	private String mrNo;// 病案号
	private TTable visitRecordTable;// 就诊记录Table
	private TTable presentIllnessTable;// 现病史Table
	private TTable medicalHistoryTable;// 既往史Table
	private TTable allergyHistoryTable;// 过敏史Table
	private TTable diagnosisTable;// 诊断Table
	private TTable medicationTable;// 药嘱Table
	private TTable laboratoryTable;// 检验Table
	private TTable physicalExamTable;// 检查Table
	private TTable operationTable;// 手术Table
	private TTable treatmentTable;// 治疗Table
	private TTable bloodTransfusionsTable;// 输血Table
	private TTable consultationTable;// 会诊Table
	private TTable medicalRecordTable;// 病历文书Table
	private TTextFormat startDate;// 开始时间
	private TTextFormat endDate;// 截止时间
	private TButton buttonVisitRecord;// 就诊记录Button
	private TButton buttonPresentIllness;// 现病史Button
	private TButton buttonMedicalHistory;// 既往史Button
	private TButton buttonAllergyHistory;// 过敏史Button
	private TButton buttonDiagnosis;// 诊断Button
	private TButton buttonMedication;// 药嘱Button
	private TButton buttonLaboratory;// 检验Button
	private TButton buttonPhysicalExam;// 检查Button
	private TButton buttonOperation;// 手术Button
	private TButton buttonTreatment;// 治疗Button
	private TButton buttonBloodTransfusions;//输血Button
	private TButton buttonConsultation;//会诊Button
	private TButton buttonMedicalRecord;// 病历文书Button
	Window window=null;
    public EMRCdrSummaryInfoControl() {
        super();
    }
    
    /**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	
    	Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				TParm parm = (TParm) obj;
				
				if (parm != null) {
					mrNo = parm.getValue("MR_NO");
				} else {
					this.messageBox("页面传参错误");
				}
			}
		}
    	
		this.onInitPage();
		//ToolTip toolTip = new ToolTip("");
    }
    
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		// 初始化页面按钮控件
		this.initPanelComponents();
		// 获取屏幕分辨率
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        
        // 初始化页面数据
        this.initPanelData();
        
        // 设置按钮控件
        this.setButtonEnabled();
        
        JScrollPane scrollPane = new JScrollPane(tPanel2);
        // 根据客户端的分辨率设定滚动条Panle的位置以及长宽
		scrollPane.setBounds(0, 0, screenWidth - 40, screenHeight);
		// 设定panel的长宽,宽度与滚动条panel的宽度一致，长度在其设定基础上增加400以便触发滚动条的显示
        tPanel2.setPreferredSize(new Dimension(scrollPane.getWidth(),
                scrollPane.getHeight() + 800));
        
        tPanel1.add(scrollPane);
       
        tPanel2.revalidate();
	}
	
	/**
	 * 初始化页面数据
	 */
	private void initPanelData() {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		// 初始化病患信息
		this.initPatInfo(parm);
		// 查询表格数据
		parm.setData("MR_NO", PatTool.getInstance().getMrRegMrNos(mrNo));
		this.onQueryTableData(parm);
	}
	
	/**
	 * 初始化页面按钮控件
	 */
	private void initPanelComponents() {
		tPanel1 = ((TPanel) getComponent("TPANEL1"));
		tPanel2 = ((TPanel) getComponent("TPANEL2"));
		// 就诊记录
		visitRecordTable = getTable("TABLE_VISIT_RECORD");
		// 现病史
		presentIllnessTable = getTable("TABLE_PRESENT_ILLNESS");
		// 既往史
		medicalHistoryTable = getTable("TABLE_MEDICAL_HISTORY");
		// 过敏史
		allergyHistoryTable = getTable("TABLE_ALLERGY_HISTORY");
		// 诊断
		diagnosisTable = getTable("TABLE_DIAGNOSIS");
		// 药嘱
		medicationTable = getTable("TABLE_MEDICATION");
		// 检验
		laboratoryTable = getTable("TABLE_LABORATORY");
		// 检查
		physicalExamTable = getTable("TABLE_PHYSICAL_EXAM");
		// 手术
		operationTable = getTable("TABLE_OPERATION");
		// 治疗
		treatmentTable = getTable("TABLE_TREATMENT");
		// 输血
		bloodTransfusionsTable = getTable("TABLE_BLOOD_TRANSFUSION");
		// 会诊
		consultationTable = getTable("TABLE_CONSULT");
		// 病历文书
		medicalRecordTable = getTable("TABLE_MEDICAL_RECORD");
		// 开始时间
		startDate = getTTextFormat("S_DATE");
		// 截止时间
		endDate = getTTextFormat("E_DATE");
		// 就诊记录Button
		buttonVisitRecord = getTButton("BUTTON_VISIT_RECORD");
		// 现病史Button
		buttonPresentIllness = getTButton("BUTTON_PRESENT_ILLNESS");
		// 既往史Button
		buttonMedicalHistory = getTButton("BUTTON_MEDICAL_HISTORY");
		// 过敏史Button
		buttonAllergyHistory = getTButton("BUTTON_ALLERGY_HISTORY");
		// 诊断Button
		buttonDiagnosis = getTButton("BUTTON_DIAGNOSIS");
		// 药嘱Button
		buttonMedication = getTButton("BUTTON_MEDICATION");
		// 检验Button
		buttonLaboratory = getTButton("BUTTON_LABORATORY");
		// 检查Button
		buttonPhysicalExam = getTButton("BUTTON_PHYSICAL_EXAM");
		// 手术Button
		buttonOperation = getTButton("BUTTON_OPERATION");
		// 治疗Button
		buttonTreatment = getTButton("BUTTON_TREATMENT");
		// 输血Button
		buttonBloodTransfusions = getTButton("BUTTON_BLOOD_TRANSFUSION");
		// 会诊Button
		buttonConsultation = getTButton("BUTTON_CONSULT");
		// 病历文书Button
		buttonMedicalRecord = getTButton("BUTTON_MEDICAL_RECORD");
	}
	
	/**
	 * 根据对应表格是否查到数据决定按钮是否可用
	 */
	private void setButtonEnabled() {
		// 就诊记录
		if (visitRecordTable.getRowCount() <= 0) {
			buttonVisitRecord.setEnabled(false);
		} else {
			buttonVisitRecord.setEnabled(true);
		}
		
		// 主诉现病史病史
		if (presentIllnessTable.getRowCount() <= 0) {
			buttonPresentIllness.setEnabled(false);
		} else {
			buttonPresentIllness.setEnabled(true);
		}
		
		// 既往史
		if (medicalHistoryTable.getRowCount() <= 0) {
			buttonMedicalHistory.setEnabled(false);
		} else {
			buttonMedicalHistory.setEnabled(true);
		}
		
		// 过敏史
		if (allergyHistoryTable.getRowCount() <= 0) {
			buttonAllergyHistory.setEnabled(false);
		} else {
			buttonAllergyHistory.setEnabled(true);
		}
		
		// 诊断记录
		if (diagnosisTable.getRowCount() <= 0) {
			buttonDiagnosis.setEnabled(false);
		} else {
			buttonDiagnosis.setEnabled(true);
		}
		
		// 药嘱记录
		if (medicationTable.getRowCount() <= 0) {
			buttonMedication.setEnabled(false);
		} else {
			buttonMedication.setEnabled(true);
		}
		
		// 检验记录
		if (laboratoryTable.getRowCount() <= 0) {
			buttonLaboratory.setEnabled(false);
		} else {
			buttonLaboratory.setEnabled(true);
		}
		
		// 检查记录
		if (physicalExamTable.getRowCount() <= 0) {
			buttonPhysicalExam.setEnabled(false);
		} else {
			buttonPhysicalExam.setEnabled(true);
		}
		
		// 手术
		if (operationTable.getRowCount() <= 0) {
			buttonOperation.setEnabled(false);
		} else {
			buttonOperation.setEnabled(true);
		}
		
		// 治疗
		if (treatmentTable.getRowCount() <= 0) {
			buttonTreatment.setEnabled(false);
		} else {
			buttonTreatment.setEnabled(true);
		}
		
		// 输血
		if (bloodTransfusionsTable.getRowCount() <= 0) {
			buttonBloodTransfusions.setEnabled(false);
		} else {
			buttonBloodTransfusions.setEnabled(true);
		}
		
		// 会诊
		if (consultationTable.getRowCount() <= 0) {
			buttonConsultation.setEnabled(false);
		} else {
			buttonConsultation.setEnabled(true);
		}
		
		// 病历文书
		if (medicalRecordTable.getRowCount() <= 0) {
			buttonMedicalRecord.setEnabled(false);
		} else {
			buttonMedicalRecord.setEnabled(true);
		}
	}
	
	/**
	 * 就诊记录表格双击事件
	 */
	public void onTableDoubleClicked() {
		if(window!=null){
			window.dispose();
		}
		TParm parm = visitRecordTable.getParmValue().getRow(
				visitRecordTable.getSelectedRow());
		String panelUI = "";
    	String configName = "";
    	if (StringUtils.equals("I", parm.getValue("ADM_TYPE"))) {
			panelUI = "CDR_INP_MED_UI";
			configName = "%ROOT%\\config\\emr\\EMRCdrInpMedRecord.x";
		} else {
			panelUI = "CDR_OPD_MED_UI";
			configName = "%ROOT%\\config\\emr\\EMRCdrOpdMedRecord.x";
		}
		
		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

        // 打开前先关闭该页面
        tabbedPane.closePanel(panelUI);
		// 打开界面
		tabbedPane.openPanel(panelUI, configName, parm);
		TComponent component = (TComponent) callFunction(
				"UI|SYSTEM_TAB|findObject", panelUI);
		if (component != null) {
			tabbedPane.setSelectedComponent((Component) component);
			return;
		}
	}
	
	/**
	 * 初始化病患信息
	 */
	private void initPatInfo(TParm parm) {
		// 查询病患基本信息
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryPatInfo(parm);
		
		// 姓名
		this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		// 性别
		this.setValue("SEX", result.getValue("SEX_TYPE", 0));
		String age = OdiUtil.showAge(result.getTimestamp("BIRTH_DATE", 0),
				SystemTool.getInstance().getDate());
		// 年龄
		this.setValue("AGE", age);
	}
	
	/**
	 * 查询数据
	 */
	private void onQueryTableData(TParm parm) {
		// 查询就诊记录
		this.queryVisitRecord(parm);
		// 查询现病史
		this.queryPresentIllness(parm);
		// 查询既往史
		this.queryMedicalHistory(parm);
		// 查询现病史
		this.queryAllergyHistory(parm);
		// 查询诊断数据
		this.queryDiagnosis(parm);
		// 查询药嘱数据
		this.queryMedication(parm);
		// 查询检验数据
		this.queryLaboratory(parm);
		// 查询检查数据
		this.queryPhysicalExam(parm);
		// 查询手术数据
		this.queryOperation(parm);
		// 查询治疗数据
		this.queryTreatment(parm);
		// 查询输血数据
		this.queryBloodTransfusions(parm);
		// 查询会诊数据
		this.queryConsultation(parm);
		// 查询病历文书数据
		this.queryMedicalRecord(parm);
	}
	
	/**
	 * 查询数据
	 */
	public void onQuery() {
		TParm parm = new TParm();
//		parm.setData("MR_NO", mrNo);
		parm.setData("MR_NO", PatTool.getInstance().getMrRegMrNos(mrNo));
		
		// 起始日期
		String sDate = TypeTool.getString(startDate.getValue());
		// 截止日期
		String eDate = TypeTool.getString(endDate.getValue());

		if (StringUtils.isNotEmpty(sDate) && StringUtils.isNotEmpty(eDate)) {
			parm.setData("S_DATE", sDate.substring(0, 10).replace('-', '/'));
			parm.setData("E_DATE", eDate.substring(0, 10).replace('-', '/'));
		} else if ((StringUtils.isEmpty(sDate) && StringUtils.isNotEmpty(eDate))
				|| (StringUtils.isNotEmpty(sDate) && StringUtils.isEmpty(eDate))) {
			this.messageBox("请填写完整的起讫日期");
			return;
		}
		
		// 查询表格数据
		this.onQueryTableData(parm);
        // 设置按钮控件
        this.setButtonEnabled();
	}
	
	/**
	 * 查询就诊记录
	 */
	private void queryVisitRecord(TParm parm) {
		// 查询就诊记录
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryVisitRecord(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		visitRecordTable.setParmValue(result);
	}
	
	/**
	 * 查询主诉现病史
	 */
	private void queryPresentIllness(TParm parm) {
		// 查询就诊记录
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryPresentIllness(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		presentIllnessTable.setParmValue(result);
	}
	
	/**
	 * 查询既往史
	 */
	private void queryMedicalHistory(TParm parm) {
		// 查询既往史
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryMedicalHistory(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		medicalHistoryTable.setParmValue(result);
	}
	
	/**
	 * 查询过敏史
	 */
	private void queryAllergyHistory(TParm parm) {
		// 查询过敏史
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryAllergyHistory(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		allergyHistoryTable.setParmValue(result);
	}
	
	/**
	 * 查询诊断数据
	 */
	private void queryDiagnosis(TParm parm) {
		// 查询诊断数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryDiagnosis(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		diagnosisTable.setParmValue(result);
	}
	
	/**
	 * 查询药嘱数据
	 */
	private void queryMedication(TParm parm) {
		// 查询诊断数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryMedication(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		medicationTable.setParmValue(result);
	}
	
	/**
	 * 查询检验数据
	 */
	private void queryLaboratory(TParm parm) {
		// 查询检验数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryLaboratory(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		laboratoryTable.setParmValue(result);
	}
	
	/**
	 * 查询检查数据
	 */
	private void queryPhysicalExam(TParm parm) {
		// 查询检查数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryPhysicalExam(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		physicalExamTable.setParmValue(result);
	}
	
	/**
	 * 查询手术数据
	 */
	private void queryOperation(TParm parm) {
		// 查询手术数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryOperation(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		operationTable.setParmValue(result);
	}
	
	/**
	 * 查询治疗数据
	 */
	private void queryTreatment(TParm parm) {
		// 查询治疗数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryTreatment(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		treatmentTable.setParmValue(result);
	}
	
	/**
	 * 查询输血数据
	 */
	private void queryBloodTransfusions(TParm parm) {
		// 查询输血数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryBloodTransfusions(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		bloodTransfusionsTable.setParmValue(result);
	}
	
	/**
	 * 查询会诊数据
	 */
	private void queryConsultation(TParm parm) {
		// 查询会诊数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryConsultation(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		consultationTable.setParmValue(result);
	}
	
	/**
	 * 查询病历文书数据
	 */
	private void queryMedicalRecord(TParm parm) {
		// 查询病历文书数据
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryMedicalRecordDocuments(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox(result.getErrText());
			return;
		}
		
		medicalRecordTable.setParmValue(result);
	}
	
    /**
     * 打开查询明细窗口
     */
	public void onOpenDetailDataWindow(Object obj) {
		TParm parm = new TParm();
//		parm.setData("MR_NO", mrNo);
		parm.setData("MR_NO", PatTool.getInstance().getMrRegMrNos(mrNo));
		// 查询数据类型
		parm.setData("DATA_TYPE", obj);
		
		// 起始日期
		String sDate = TypeTool.getString(startDate.getValue());
		// 截止日期
		String eDate = TypeTool.getString(endDate.getValue());

		if (StringUtils.isNotEmpty(sDate) && StringUtils.isNotEmpty(eDate)) {
			parm.setData("S_DATE", sDate.substring(0, 10).replace('-', '/'));
			parm.setData("E_DATE", eDate.substring(0, 10).replace('-', '/'));
		}
		if(window!=null){
			window.dispose();//关闭
		}
		
		// 药嘱;检验;检查
		if ("6,7,8,9".contains(TypeTool.getString(obj))) {
			//
			if("6".contains(TypeTool.getString(obj))){//药嘱
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrDetailDataByTree.x", parm);
			}
			
			if("7".contains(TypeTool.getString(obj))){//检验
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrOpenLisUI.x", parm);
			}
			
			if("8".contains(TypeTool.getString(obj))){//检查
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRRisOpenReport.x", parm);
			}
			if("9".contains(TypeTool.getString(obj))){//手术
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrOpenOperationUI.x", parm);
			}
		} else {
			window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrDetailData.x", parm);
		}
		
		
		
        window.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width-window.getWidth(), 80);//设置弹出窗口的位置 现在的位置是靠右上角
        //window.setY(130);*/
        //window.setVisible(true);
        //AWTUtilities.setWindowOpacity(window, 0.9f);
	}
	
    /**
     * 返回方法
     */
	public void onReturn() {
		this.closeWindow();
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
    
    /**
     * 得到TTextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
    /**
     * 得到TButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TButton getTButton(String tagName) {
        return (TButton) getComponent(tagName);
    }
    
    @Override
    public boolean onClosing() {
    	if(window!=null){//关闭浮动窗口
    		window.dispose();
    	}
    	return super.onClosing();
    	
    }
}
