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
 * Title: CDR��ϸ��ѯ����
 * </p>
 * 
 * <p>
 * Description: CDR��ϸ��ѯ����
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
	private TParm parameterParm;// ҳ�洫��
	private TTable tableData;// ��ϸ����Table
	private String tempPath;
	
    public EMRCdrDetailDataControl() {
        super();
    }
    
    /**
     * ��ʼ������
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
					this.messageBox("ҳ�洫�δ���");
				}
			} else {
				this.messageBox("ҳ�洫�δ���");
			}
		}
		// ��ʼ��ҳ��
		this.onInitPage();
    }
    
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		
		tableData = getTable("TABLE_DATA");
		// ҳ�����
		String title = "";
		// ���ݱ���б���
		String tableHeader = "";
		// ���ݱ��������
		String tableParmMap = "";
		// ���ݱ���ж��뷽ʽ
		String tableAlignData = "";
		// ����������
		String lockColumns = "";
		// ���ݱ���ѯ���
		TParm result = new TParm();
		
		if (null != parameterParm) {
			if (StringUtils.equals("1", parameterParm.getValue("DATA_TYPE"))) {
				title = "�����¼";
				tableHeader = "��������,60,ADM_TYPE_COMBO;��������,80,timestamp,yyyy/MM/dd;��Ժ����,80,timestamp,yyyy/MM/dd;�������,80;����/����,100;����ҽ��,80;����ҽ��,80,OPERATOR_COMBO;����ȼ�,60;���˷ּ�,60;���Ļ����,80,boolean";
				tableParmMap = "ADM_TYPE_DESC;ADM_DATE_DESC;DISCHARGE_DATE;DEPT_DESC;AREA_DESC;VS_DR_NAME;ATTEND_DR_CODE;NURSING_CLASS;TRIAGE_LEVEL;NIS_REPORT";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left";
				lockColumns = "0,1,2,3,4,5,6,7,8";
				result = EMRCDRSummaryInfoTool.getInstance().queryVisitRecord(parameterParm);
				tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seeNisReport");
			} else if (StringUtils.equals("2", parameterParm.getValue("DATA_TYPE"))) {
				title = "�����ֲ�ʷ";
				tableHeader = "��������,500;�͹�����,500;��¼ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "SUBJECTIVE;OBJECTIVE;VISIT_DATE;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left";
				lockColumns = "all";
				result = EMRCDRSummaryInfoTool.getInstance().queryPresentIllness(parameterParm);
			} else if (StringUtils.equals("3", parameterParm.getValue("DATA_TYPE"))) {
				title = "����ʷ";
				tableHeader = "��ȥ��ʷ,400;����ʷ,400;��¼ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "PAST_HISTORY;FAMILY_HISTORY;VISIT_DATE;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left";
				lockColumns = "all";
				result = EMRCDRSummaryInfoTool.getInstance().queryMedicalHistory(parameterParm);
			} else if (StringUtils.equals("4", parameterParm.getValue("DATA_TYPE"))) {
				title = "����ʷ";
				tableHeader = "����Դ,250;����֢״,180;�������س̶�,100;��¼ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "ALLERGEN_NAME;SYMPTOM;SERIOUS_LEVEL;VISIT_DATE;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left";
				lockColumns = "all";
				result = EMRCDRSummaryInfoTool.getInstance().queryAllergyHistory(parameterParm);
			} else if (StringUtils.equals("5", parameterParm.getValue("DATA_TYPE"))) {
				title = "���";
				tableHeader = "�������,100;��ϱ���,80;�������,200;�����,50,boolean;���ʱ��,160,Timestamp,yyyy/MM/dd HH:mm:ss;��ע,120;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "DIAG_TYPE;ICD_CODE;DIAG_DESC;IS_MAIN_DIAG;OPT_DATE;DIAG_DEFINITION;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,right;2,left;4,left;5,left;6,left;7,left;8,left;9,left";
				lockColumns = "all";
				result = EMRCdrTool.getInstance().getDiagnosisData(parameterParm);
			} else if (StringUtils.equals("9", parameterParm.getValue("DATA_TYPE"))) {
				
				result = EMRCDRSummaryInfoTool.getInstance().queryOperation(parameterParm);
				
				// ����table��ѡ�����¼�
				tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "selectCheckBox");
			} else if (StringUtils.equals("10", parameterParm.getValue("DATA_TYPE"))) {
				title = "����";
				tableHeader = "������Ŀ,240;Ƶ��,80;����,50;����,50;ҽ����ע,230;����,30,boolean;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "TR_DESC;FREQUENCY;TR_DAYS;QTY;DR_NOTE;IS_URGENT;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,right;3,right;4,left;6,left;7,left;8,left;9,left";
				lockColumns = "0,1,2,3,4,5,6,7,8,9";
				result = EMRCDRSummaryInfoTool.getInstance().queryTreatment(parameterParm);
			} else if (StringUtils.equals("11", parameterParm.getValue("DATA_TYPE"))) {
				title = "��Ѫ";
				tableHeader = "ѪҺ����,150;����,60;��λ,60;ABO��Ѫ,90;RH(D),80;����,80;�β�,80;��Ѫ���,90;�������,90;����ҽ��,80;����ʱ��,90;��ǰ״̬,113;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "BLDPROD_DESC;VOLUME;UNIT_DESC;BLOOD_TYPING;RH_TYPING;MAIN_CROSS_TEST;SUB_CROSS_TEST;CROSS_MATCH;DEPT_DESC;VS_DR_NAME;ORDER_TIME;STATUS;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,right;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left";
				lockColumns = "all";
				result = EMRCdrTool.getInstance().getBmsData(parameterParm);
			} else if (StringUtils.equals("12", parameterParm.getValue("DATA_TYPE"))) {
				title = "����";
				tableHeader = "����ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;�������,80;��������,80;����ԭ��,100;����ժҪ,150;�������,80;����ʱ��,80;�ظ�ʱ��,80;�����ص�˵��,80;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "ORDER_TIME;ORDER_DEPT;CONSULT_KIND;CONSULT_REASON;ILLNESS_STATE;CONSULT_DEPT_NAME;ACCEPT_TIME;REPLY_DATE;CONSULT_REPORT;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;10,left;11,left;12,left;13,left";
				lockColumns = "0,1,2,3,4,5,6,7,8,10,11,12,13,14";
				result = EMRCdrTool.getInstance().getConsult(parameterParm);
				//tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seeConsultReport");
			} else if (StringUtils.equals("13", parameterParm.getValue("DATA_TYPE"))) {
				title = "��������";
				tableHeader = "��������,180;��Ա,100;ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;�鵵ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��ǰ״̬,100;���Ĳ���,80,boolean;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
				tableParmMap = "CHART_NAME;EDIT_USER;EDIT_DATE;CONFIRM_TIME;STATUS;WORD;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC";
				tableAlignData = "0,left;1,left;2,left;3,left;4,left;6,left;7,left;8,left;9,left";
				lockColumns = "0,1,2,3,4,6,7,8,9";
				result = EMRCdrTool.getInstance().getFileData(parameterParm);
				
				// ��������table��ѡ�����¼�_���Ĳ���
				tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "seePdfWord");
			}
			if(StringUtils.equals("9", parameterParm.getValue("DATA_TYPE"))){
				tableData.setParmValue(result);
			}else{
				// ����ҳ�����
				this.setTitle(title);
				// ���ñ����
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
	 * ���� ���--������ҩ �����м໤ �����Ĳ���  ��ѡ�¼�
	 * 
	 * @param obj
	 */
	public void selectCheckBox(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		
		TFrame tFrame = (TFrame) this.getComponent("UI");//���ҳ��
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		TParm parm = new TParm();
		int column = table.getSelectedColumn();
		boolean flg = false;
		if (column == 14 && "Y".equals(tableParm.getValue("WORD", row))) {//���Ĳ���
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//���������
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
				// ���ķֲ�ʽ�洢����
				fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
				byte data[] = (byte[]) fileParm.getData("FILE_DATA");
				if (data == null) {
					messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
					return;
				}
				try {
					FileTool.setByte(tempPath + "\\" + fileName, data);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				try {
					// ���ļ�
					runtime.exec("rundll32 url.dll FileProtocolHandler "
							+ tempPath + "\\" + fileName);
					flg = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if(flg){
				tFrame.setExtendedState(TFrame.ICONIFIED); //������С��
			}
			//table.setItem(row, "WORD", "N");
		}else{
				TTable table1 = (TTable) this.getComponent("TABLE1");//��ҩ��¼���;
				TTable table2 = (TTable) this.getComponent("TABLE2");//���������;
				TTable tableEvent = (TTable) this.getComponent("TABLE_EVENT");//�����¼�
				TTabbedPane tTabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_0");
				
				if (column == 13 &&	"Y".equals(tableParm.getValue("OPEING", row))) {
					
					tTabbedPane.setEnabledAt(0, false);//��ҩ��¼ҳǩ
					tTabbedPane.setEnabledAt(1, false);//�������ҳǩ
					tTabbedPane.setEnabledAt(2, false);//���м໤ҳǩ
					
					table1.removeRowAll();//�����ҩ��¼���
					table2.removeRowAll();//������������
					tableEvent.removeRowAll();//��������¼����
					for(int i = 0;i < tableParm.getCount();i++){
						if(tableParm.getValue("OPEING", i).equals("Y")&&i!=row){
							table.setItem(i, "OPEING", "N");
						}
						if(tableParm.getValue("WORD", i).equals("Y")){
							table.setItem(i,"WORD","N");//���������
						}
					}
					parm.setData("OPE_BOOK_NO", tableParm.getValue("APPLY_NO", row));
					parm.setData("ADM_TYPE", "I");
					TParm result=EMRCdrTool.getInstance().getOpeEventData(parm);
					int count1 = 0;
					int count2 = 0;
					if(result.getCount()>0){//�����¼�
						count1++;
						tTabbedPane.setSelectedIndex(0);
						tableEvent.setParmValue(result);
						tTabbedPane.setEnabledAt(0, true);
					}
					result=EMRCdrTool.getInstance().getOpeAnaData(parm);
					if(result.getCount()>0){//��ҩ��¼
						count2++;
						if(count1 == 0){
							tTabbedPane.setSelectedIndex(1);
						}
						table1.setParmValue(result);
						tTabbedPane.setSelectedIndex(1);
						tTabbedPane.setEnabledAt(1, true);
					}
					result=EMRCdrTool.getInstance().getOpeLisenerData(parm);
					if(result.getCount()>0){//�������
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
	 * ��������--���Ĳ����¼�
	 * 
	 * @param obj
	 */
	public void seePdfWord(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		TFrame tFrame = (TFrame) this.getComponent("UI");//���ҳ��
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		boolean flg = false;
		if ("Y".equals(tableParm.getValue("WORD", row))) {
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("WORD", i).equals("Y")){
					table.setItem(i,"WORD","N");//���������
				}
			}
			String fileName = tableParm.getValue("FILE_NAME", row) + ".pdf";
			String filePath = tableParm.getValue("FILE_PATH", row);
			Runtime runtime = Runtime.getRuntime();
			// ���ķֲ�ʽ�洢����
			TParm fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
			byte data[] = (byte[]) fileParm.getData("FILE_DATA");
			if (data == null) {
				messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
				return;
			}
			try {
				FileTool.setByte(tempPath + "\\" + fileName, data);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			try {
				// ���ļ�
				runtime.exec("rundll32 url.dll FileProtocolHandler " + tempPath
						+ "\\" + fileName);
				flg = true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}
		if(flg){
			tFrame.setExtendedState(TFrame.ICONIFIED); //������С��
		}
	}
	
	/**
	 * ���Ļ����¼
	 * @param obj
	 */
	public void seeNisReport(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		TFrame tFrame = (TFrame) this.getComponent("UI");//���ҳ��
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		boolean flg = false;
		if("Y".equals(tableParm.getValue("NIS_REPORT", row))){
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("NIS_REPORT", i).equals("Y")){
					table.setItem(i,"NIS_REPORT","N");//���������
				}
			}
			SystemTool.getInstance().OpeNisFormList(tableParm.getValue("CASE_NO",row), parameterParm.getValue("MR_NO"));
			flg = true;
		}
		if(flg){
			tFrame.setExtendedState(TFrame.ICONIFIED); //������С��
		}
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
    
}
