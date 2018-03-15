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
 * Title: CDR�ۺϲ�ѯ����
 * </p>
 * 
 * <p>
 * Description: CDR�ۺϲ�ѯ����
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
	private String mrNo;// ������
	private TTable visitRecordTable;// �����¼Table
	private TTable presentIllnessTable;// �ֲ�ʷTable
	private TTable medicalHistoryTable;// ����ʷTable
	private TTable allergyHistoryTable;// ����ʷTable
	private TTable diagnosisTable;// ���Table
	private TTable medicationTable;// ҩ��Table
	private TTable laboratoryTable;// ����Table
	private TTable physicalExamTable;// ���Table
	private TTable operationTable;// ����Table
	private TTable treatmentTable;// ����Table
	private TTable bloodTransfusionsTable;// ��ѪTable
	private TTable consultationTable;// ����Table
	private TTable medicalRecordTable;// ��������Table
	private TTextFormat startDate;// ��ʼʱ��
	private TTextFormat endDate;// ��ֹʱ��
	private TButton buttonVisitRecord;// �����¼Button
	private TButton buttonPresentIllness;// �ֲ�ʷButton
	private TButton buttonMedicalHistory;// ����ʷButton
	private TButton buttonAllergyHistory;// ����ʷButton
	private TButton buttonDiagnosis;// ���Button
	private TButton buttonMedication;// ҩ��Button
	private TButton buttonLaboratory;// ����Button
	private TButton buttonPhysicalExam;// ���Button
	private TButton buttonOperation;// ����Button
	private TButton buttonTreatment;// ����Button
	private TButton buttonBloodTransfusions;//��ѪButton
	private TButton buttonConsultation;//����Button
	private TButton buttonMedicalRecord;// ��������Button
	Window window=null;
    public EMRCdrSummaryInfoControl() {
        super();
    }
    
    /**
     * ��ʼ������
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
					this.messageBox("ҳ�洫�δ���");
				}
			}
		}
    	
		this.onInitPage();
		//ToolTip toolTip = new ToolTip("");
    }
    
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		// ��ʼ��ҳ�水ť�ؼ�
		this.initPanelComponents();
		// ��ȡ��Ļ�ֱ���
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        
        // ��ʼ��ҳ������
        this.initPanelData();
        
        // ���ð�ť�ؼ�
        this.setButtonEnabled();
        
        JScrollPane scrollPane = new JScrollPane(tPanel2);
        // ���ݿͻ��˵ķֱ����趨������Panle��λ���Լ�����
		scrollPane.setBounds(0, 0, screenWidth - 40, screenHeight);
		// �趨panel�ĳ���,����������panel�Ŀ��һ�£����������趨����������400�Ա㴥������������ʾ
        tPanel2.setPreferredSize(new Dimension(scrollPane.getWidth(),
                scrollPane.getHeight() + 800));
        
        tPanel1.add(scrollPane);
       
        tPanel2.revalidate();
	}
	
	/**
	 * ��ʼ��ҳ������
	 */
	private void initPanelData() {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		// ��ʼ��������Ϣ
		this.initPatInfo(parm);
		// ��ѯ�������
		parm.setData("MR_NO", PatTool.getInstance().getMrRegMrNos(mrNo));
		this.onQueryTableData(parm);
	}
	
	/**
	 * ��ʼ��ҳ�水ť�ؼ�
	 */
	private void initPanelComponents() {
		tPanel1 = ((TPanel) getComponent("TPANEL1"));
		tPanel2 = ((TPanel) getComponent("TPANEL2"));
		// �����¼
		visitRecordTable = getTable("TABLE_VISIT_RECORD");
		// �ֲ�ʷ
		presentIllnessTable = getTable("TABLE_PRESENT_ILLNESS");
		// ����ʷ
		medicalHistoryTable = getTable("TABLE_MEDICAL_HISTORY");
		// ����ʷ
		allergyHistoryTable = getTable("TABLE_ALLERGY_HISTORY");
		// ���
		diagnosisTable = getTable("TABLE_DIAGNOSIS");
		// ҩ��
		medicationTable = getTable("TABLE_MEDICATION");
		// ����
		laboratoryTable = getTable("TABLE_LABORATORY");
		// ���
		physicalExamTable = getTable("TABLE_PHYSICAL_EXAM");
		// ����
		operationTable = getTable("TABLE_OPERATION");
		// ����
		treatmentTable = getTable("TABLE_TREATMENT");
		// ��Ѫ
		bloodTransfusionsTable = getTable("TABLE_BLOOD_TRANSFUSION");
		// ����
		consultationTable = getTable("TABLE_CONSULT");
		// ��������
		medicalRecordTable = getTable("TABLE_MEDICAL_RECORD");
		// ��ʼʱ��
		startDate = getTTextFormat("S_DATE");
		// ��ֹʱ��
		endDate = getTTextFormat("E_DATE");
		// �����¼Button
		buttonVisitRecord = getTButton("BUTTON_VISIT_RECORD");
		// �ֲ�ʷButton
		buttonPresentIllness = getTButton("BUTTON_PRESENT_ILLNESS");
		// ����ʷButton
		buttonMedicalHistory = getTButton("BUTTON_MEDICAL_HISTORY");
		// ����ʷButton
		buttonAllergyHistory = getTButton("BUTTON_ALLERGY_HISTORY");
		// ���Button
		buttonDiagnosis = getTButton("BUTTON_DIAGNOSIS");
		// ҩ��Button
		buttonMedication = getTButton("BUTTON_MEDICATION");
		// ����Button
		buttonLaboratory = getTButton("BUTTON_LABORATORY");
		// ���Button
		buttonPhysicalExam = getTButton("BUTTON_PHYSICAL_EXAM");
		// ����Button
		buttonOperation = getTButton("BUTTON_OPERATION");
		// ����Button
		buttonTreatment = getTButton("BUTTON_TREATMENT");
		// ��ѪButton
		buttonBloodTransfusions = getTButton("BUTTON_BLOOD_TRANSFUSION");
		// ����Button
		buttonConsultation = getTButton("BUTTON_CONSULT");
		// ��������Button
		buttonMedicalRecord = getTButton("BUTTON_MEDICAL_RECORD");
	}
	
	/**
	 * ���ݶ�Ӧ����Ƿ�鵽���ݾ�����ť�Ƿ����
	 */
	private void setButtonEnabled() {
		// �����¼
		if (visitRecordTable.getRowCount() <= 0) {
			buttonVisitRecord.setEnabled(false);
		} else {
			buttonVisitRecord.setEnabled(true);
		}
		
		// �����ֲ�ʷ��ʷ
		if (presentIllnessTable.getRowCount() <= 0) {
			buttonPresentIllness.setEnabled(false);
		} else {
			buttonPresentIllness.setEnabled(true);
		}
		
		// ����ʷ
		if (medicalHistoryTable.getRowCount() <= 0) {
			buttonMedicalHistory.setEnabled(false);
		} else {
			buttonMedicalHistory.setEnabled(true);
		}
		
		// ����ʷ
		if (allergyHistoryTable.getRowCount() <= 0) {
			buttonAllergyHistory.setEnabled(false);
		} else {
			buttonAllergyHistory.setEnabled(true);
		}
		
		// ��ϼ�¼
		if (diagnosisTable.getRowCount() <= 0) {
			buttonDiagnosis.setEnabled(false);
		} else {
			buttonDiagnosis.setEnabled(true);
		}
		
		// ҩ����¼
		if (medicationTable.getRowCount() <= 0) {
			buttonMedication.setEnabled(false);
		} else {
			buttonMedication.setEnabled(true);
		}
		
		// �����¼
		if (laboratoryTable.getRowCount() <= 0) {
			buttonLaboratory.setEnabled(false);
		} else {
			buttonLaboratory.setEnabled(true);
		}
		
		// ����¼
		if (physicalExamTable.getRowCount() <= 0) {
			buttonPhysicalExam.setEnabled(false);
		} else {
			buttonPhysicalExam.setEnabled(true);
		}
		
		// ����
		if (operationTable.getRowCount() <= 0) {
			buttonOperation.setEnabled(false);
		} else {
			buttonOperation.setEnabled(true);
		}
		
		// ����
		if (treatmentTable.getRowCount() <= 0) {
			buttonTreatment.setEnabled(false);
		} else {
			buttonTreatment.setEnabled(true);
		}
		
		// ��Ѫ
		if (bloodTransfusionsTable.getRowCount() <= 0) {
			buttonBloodTransfusions.setEnabled(false);
		} else {
			buttonBloodTransfusions.setEnabled(true);
		}
		
		// ����
		if (consultationTable.getRowCount() <= 0) {
			buttonConsultation.setEnabled(false);
		} else {
			buttonConsultation.setEnabled(true);
		}
		
		// ��������
		if (medicalRecordTable.getRowCount() <= 0) {
			buttonMedicalRecord.setEnabled(false);
		} else {
			buttonMedicalRecord.setEnabled(true);
		}
	}
	
	/**
	 * �����¼���˫���¼�
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

        // ��ǰ�ȹرո�ҳ��
        tabbedPane.closePanel(panelUI);
		// �򿪽���
		tabbedPane.openPanel(panelUI, configName, parm);
		TComponent component = (TComponent) callFunction(
				"UI|SYSTEM_TAB|findObject", panelUI);
		if (component != null) {
			tabbedPane.setSelectedComponent((Component) component);
			return;
		}
	}
	
	/**
	 * ��ʼ��������Ϣ
	 */
	private void initPatInfo(TParm parm) {
		// ��ѯ����������Ϣ
		TParm result = EMRCDRSummaryInfoTool.getInstance().queryPatInfo(parm);
		
		// ����
		this.setValue("PAT_NAME", result.getValue("PAT_NAME", 0));
		// �Ա�
		this.setValue("SEX", result.getValue("SEX_TYPE", 0));
		String age = OdiUtil.showAge(result.getTimestamp("BIRTH_DATE", 0),
				SystemTool.getInstance().getDate());
		// ����
		this.setValue("AGE", age);
	}
	
	/**
	 * ��ѯ����
	 */
	private void onQueryTableData(TParm parm) {
		// ��ѯ�����¼
		this.queryVisitRecord(parm);
		// ��ѯ�ֲ�ʷ
		this.queryPresentIllness(parm);
		// ��ѯ����ʷ
		this.queryMedicalHistory(parm);
		// ��ѯ�ֲ�ʷ
		this.queryAllergyHistory(parm);
		// ��ѯ�������
		this.queryDiagnosis(parm);
		// ��ѯҩ������
		this.queryMedication(parm);
		// ��ѯ��������
		this.queryLaboratory(parm);
		// ��ѯ�������
		this.queryPhysicalExam(parm);
		// ��ѯ��������
		this.queryOperation(parm);
		// ��ѯ��������
		this.queryTreatment(parm);
		// ��ѯ��Ѫ����
		this.queryBloodTransfusions(parm);
		// ��ѯ��������
		this.queryConsultation(parm);
		// ��ѯ������������
		this.queryMedicalRecord(parm);
	}
	
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
//		parm.setData("MR_NO", mrNo);
		parm.setData("MR_NO", PatTool.getInstance().getMrRegMrNos(mrNo));
		
		// ��ʼ����
		String sDate = TypeTool.getString(startDate.getValue());
		// ��ֹ����
		String eDate = TypeTool.getString(endDate.getValue());

		if (StringUtils.isNotEmpty(sDate) && StringUtils.isNotEmpty(eDate)) {
			parm.setData("S_DATE", sDate.substring(0, 10).replace('-', '/'));
			parm.setData("E_DATE", eDate.substring(0, 10).replace('-', '/'));
		} else if ((StringUtils.isEmpty(sDate) && StringUtils.isNotEmpty(eDate))
				|| (StringUtils.isNotEmpty(sDate) && StringUtils.isEmpty(eDate))) {
			this.messageBox("����д��������������");
			return;
		}
		
		// ��ѯ�������
		this.onQueryTableData(parm);
        // ���ð�ť�ؼ�
        this.setButtonEnabled();
	}
	
	/**
	 * ��ѯ�����¼
	 */
	private void queryVisitRecord(TParm parm) {
		// ��ѯ�����¼
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
	 * ��ѯ�����ֲ�ʷ
	 */
	private void queryPresentIllness(TParm parm) {
		// ��ѯ�����¼
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
	 * ��ѯ����ʷ
	 */
	private void queryMedicalHistory(TParm parm) {
		// ��ѯ����ʷ
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
	 * ��ѯ����ʷ
	 */
	private void queryAllergyHistory(TParm parm) {
		// ��ѯ����ʷ
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
	 * ��ѯ�������
	 */
	private void queryDiagnosis(TParm parm) {
		// ��ѯ�������
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
	 * ��ѯҩ������
	 */
	private void queryMedication(TParm parm) {
		// ��ѯ�������
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
	 * ��ѯ��������
	 */
	private void queryLaboratory(TParm parm) {
		// ��ѯ��������
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
	 * ��ѯ�������
	 */
	private void queryPhysicalExam(TParm parm) {
		// ��ѯ�������
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
	 * ��ѯ��������
	 */
	private void queryOperation(TParm parm) {
		// ��ѯ��������
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
	 * ��ѯ��������
	 */
	private void queryTreatment(TParm parm) {
		// ��ѯ��������
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
	 * ��ѯ��Ѫ����
	 */
	private void queryBloodTransfusions(TParm parm) {
		// ��ѯ��Ѫ����
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
	 * ��ѯ��������
	 */
	private void queryConsultation(TParm parm) {
		// ��ѯ��������
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
	 * ��ѯ������������
	 */
	private void queryMedicalRecord(TParm parm) {
		// ��ѯ������������
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
     * �򿪲�ѯ��ϸ����
     */
	public void onOpenDetailDataWindow(Object obj) {
		TParm parm = new TParm();
//		parm.setData("MR_NO", mrNo);
		parm.setData("MR_NO", PatTool.getInstance().getMrRegMrNos(mrNo));
		// ��ѯ��������
		parm.setData("DATA_TYPE", obj);
		
		// ��ʼ����
		String sDate = TypeTool.getString(startDate.getValue());
		// ��ֹ����
		String eDate = TypeTool.getString(endDate.getValue());

		if (StringUtils.isNotEmpty(sDate) && StringUtils.isNotEmpty(eDate)) {
			parm.setData("S_DATE", sDate.substring(0, 10).replace('-', '/'));
			parm.setData("E_DATE", eDate.substring(0, 10).replace('-', '/'));
		}
		if(window!=null){
			window.dispose();//�ر�
		}
		
		// ҩ��;����;���
		if ("6,7,8,9".contains(TypeTool.getString(obj))) {
			//
			if("6".contains(TypeTool.getString(obj))){//ҩ��
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrDetailDataByTree.x", parm);
			}
			
			if("7".contains(TypeTool.getString(obj))){//����
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrOpenLisUI.x", parm);
			}
			
			if("8".contains(TypeTool.getString(obj))){//���
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRRisOpenReport.x", parm);
			}
			if("9".contains(TypeTool.getString(obj))){//����
				window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrOpenOperationUI.x", parm);
			}
		} else {
			window=(Window) this.openWindow("%ROOT%\\config\\emr\\EMRCdrDetailData.x", parm);
		}
		
		
		
        window.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width-window.getWidth(), 80);//���õ������ڵ�λ�� ���ڵ�λ���ǿ����Ͻ�
        //window.setY(130);*/
        //window.setVisible(true);
        //AWTUtilities.setWindowOpacity(window, 0.9f);
	}
	
    /**
     * ���ط���
     */
	public void onReturn() {
		this.closeWindow();
	}
	
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
     * �õ�TTextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
    /**
     * �õ�TButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TButton getTButton(String tagName) {
        return (TButton) getComponent(tagName);
    }
    
    @Override
    public boolean onClosing() {
    	if(window!=null){//�رո�������
    		window.dispose();
    	}
    	return super.onClosing();
    	
    }
}
