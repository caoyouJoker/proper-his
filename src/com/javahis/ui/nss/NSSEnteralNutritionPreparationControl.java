package com.javahis.ui.nss;

import java.util.ArrayList;
import java.util.List;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.DeptTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ����Ӫ������</p>
 *
 * <p>Description: ����Ӫ������</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.20
 * @version 1.0
 */
public class NSSEnteralNutritionPreparationControl extends TControl {
    public NSSEnteralNutritionPreparationControl() {
        super();
    }

    private TTable tableM;
    private TTable tableD;

    /**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
		this.onInitPage();
    }
    
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
		
		// �����������ݵ����¼�
//		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
//				+ TTableEvent.CLICKED, this, "onTableMClicked");
		
		// ������������˫���¼�
		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableMDoubleClicked");
		
		// ��������ѡ��ѡ�¼�
		tableM.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableMCheckBoxClicked");
		
		// �ؼ���ʼ��
		this.onInitControl();
	}
	
	/**
	 * �ؼ���ʼ��
	 */
	public void onInitControl() {
		// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// �趨Ĭ��չ������
    	this.setValue("QUERY_DATE_S", todayDate);
    	this.setValue("QUERY_DATE_E", todayDate);
    	
    	clearValue("ENP_DEPT_CODE;ENP_STATION_CODE;MR_NO;EN_PREPARE_NO;SELECT_ALL");
    	
    	getRadioButton("STATUS_N").setSelected(true);
    	
    	tableM.setParmValue(new TParm());
    	tableD.setParmValue(new TParm());
	}

    /**
     * ��ѯ����
     */
    public void onQuery() {
    	clearValue("SELECT_ALL");
		tableM.setParmValue(new TParm());
		tableD.setParmValue(new TParm());
		
    	// ��ȡ��ѯ��������
    	TParm queryParm = this.getQueryParm();
    	
    	if (queryParm.getErrCode() < 0) {
    		this.messageBox(queryParm.getErrText());
    		return;
    	}
    	
    	TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯӪ��ʦչ���䷽����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("��������");
			tableM.setParmValue(new TParm());
			tableD.setParmValue(new TParm());
			return;
		}
		
        tableM.setParmValue(result);
    }
    
	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isEmpty(this.getValueString("QUERY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("QUERY_DATE_E"))) {
			tableM.setParmValue(new TParm());
			parm.setErr(-1, "�������ѯʱ��");
    		return parm;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace('-', '/'));
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace('-', '/'));
		
		// ����
		if (StringUtils.isNotEmpty(this.getValueString("ENP_DEPT_CODE").trim())) {
			parm.setData("DEPT_CODE", this.getValueString("ENP_DEPT_CODE"));
		}
		// ����
		if (StringUtils.isNotEmpty(this.getValueString("ENP_STATION_CODE").trim())) {
			parm.setData("STATION_CODE", this.getValueString("ENP_STATION_CODE"));
		}
		// ���Ƶ���
		if (StringUtils.isNotEmpty(this.getValueString("EN_PREPARE_NO").trim())) {
			parm.setData("EN_PREPARE_NO", this.getValueString("EN_PREPARE_NO"));
		}
		// ������
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// ����״̬
		if (getRadioButton("STATUS_N").isSelected()) {
			parm.setData("PREPARE_STATUS", "0");
		} else {
			parm.setData("PREPARE_STATUS", "1");
		}
		// δȡ��
		parm.setData("CANCEL_FLG", "N");
		
		return parm;
	}
	
	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("���޴˲�����");
				return;
			}
			//modify by huangtt 20160930 EMPI���߲�����ʾ  start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
		            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
		    }
			//modify by huangtt 20160930 EMPI���߲�����ʾ  end
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
	
	/**
	 * �������Ƶ��Ų�ѯ
	 */
	public void onQueryByPrepareNo() {
		this.onQuery();
		TParm parm = tableM.getParmValue();
		if (parm.getCount() > 0) {
			tableM.setSelectedRow(0);
			this.onTableMDoubleClicked(0);
		} else {
			this.setValue("EN_PREPARE_NO", "");
		}
	}
	
	/**
	 * ��Ӷ�tableM��ѡ�м����¼�
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableD.setParmValue(new TParm());
		
		TParm data = tableM.getParmValue();
		int selectedRow = tableM.getSelectedRow();
		
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderD(
				data.getRow(selectedRow));
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�䷽��ϸ����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		tableD.setParmValue(result);
	}
	
	/**
	 * ��Ӷ�tableM˫���ļ����¼�
	 * 
	 * @param row
	 */
	public void onTableMDoubleClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TParm data = tableM.getParmValue();
		int selectedRow = tableM.getSelectedRow();
		TParm parm = data.getRow(selectedRow);
		
		TParm parameterParm = new TParm();
		// ����״̬
		if (getRadioButton("STATUS_N").isSelected()) {
			parameterParm.setData("COMPLETE_STATUS", "N");
		} else {
			parameterParm.setData("COMPLETE_STATUS", "Y");
		}
		parameterParm.setData("EN_PREPARE_NO", parm.getValue("EN_PREPARE_NO"));
		parameterParm.setData("EN_ORDER_NO", parm.getValue("EN_ORDER_NO"));
		parameterParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		parameterParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
		parameterParm.setData("BED_NO_DESC", parm.getValue("BED_NO_DESC"));
		
		TParm queryParm = new TParm();
		queryParm.setData("EN_PREPARE_NO", parm.getValue("EN_PREPARE_NO"));
		queryParm.setData("EN_ORDER_NO", parm.getValue("EN_ORDER_NO"));
		queryParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		queryParm.setData("CANCEL_FLG", "Y");

		// ��ѯӪ��ʦҽ��չ����������
		queryParm = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
		
		if (queryParm.getErrCode() < 0) {
			this.messageBox("��ѯչ���䷽����");
			err("ERR:" + queryParm.getErrCode() + queryParm.getErrText());
			return;
		}
		
		if (queryParm.getCount() > 0) {
			this.messageBox("��չ���䷽�ѱ�ȡ��");
			this.onQuery();
			return;
		}
		
		parameterParm.addListener("addListener", this, "onRefresh");
		// ������ϸ��������
		this.openDialog("%ROOT%\\config\\nss\\NSSEnteralNutritionFormula.x", parameterParm);
	}
	
	/**
	 * ������ɺ�ˢ�½�������
	 * 
	 * @param obj
	 */
	public void onRefresh(Object obj) {
		if (obj != null) {
			if (obj instanceof TParm) {
				this.onQuery();
			}
		}
	}
	
	/**
	 * ��Ӷ�tableM�й�ѡ��ѡ�ļ����¼�
	 * 
	 * @param obj
	 */
	public void onTableMCheckBoxClicked(Object obj) {
		TParm parm = tableM.getParmValue();
		// ǿ��ʧȥ�༭����
		if (this.tableM.getTable().isEditing()) {
			this.tableM.getTable().getCellEditor().stopCellEditing();
		}
		
		int count = parm.getCount();
		// ���Ƶ���
		String enOrderNoList = "";
		// ���Ƶ���
		String enPrepareNoList = "";
		
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				if (enOrderNoList.length() == 0) {
					enOrderNoList = parm.getValue("EN_ORDER_NO", i);
					enPrepareNoList = parm.getValue("EN_PREPARE_NO", i);
				} else {
					// ƴ��ѡ�������еĶ��Ƶ���
					enOrderNoList = enOrderNoList + "','"
							+ parm.getValue("EN_ORDER_NO", i);
					// ƴ��ѡ�������е����Ƶ���
					enPrepareNoList = enPrepareNoList + "','"
							+ parm.getValue("EN_PREPARE_NO", i);
				}
			}
		}
		
		// �޹�ѡ����
		if (enOrderNoList.length() > 0) {
			TParm queryParm = new TParm();
			queryParm.setData("EN_ORDER_NO", enOrderNoList);
			queryParm.setData("EN_PREPARE_NO", enPrepareNoList);
			// ͳ���䷽��ϸ����
			TParm result = NSSEnteralNutritionTool.getInstance()
					.queryENOrderDDataAccount(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ�䷽��ϸ��������");
				err("ERR:" + result.getErrCode() + result.getErrText());
				return;
			}
			
			if (result.getCount() <= 0) {
				this.messageBox("�����䷽��ϸ��������");
				tableD.setParmValue(new TParm());
				return;
			} else {
				tableD.setParmValue(result);
			}
		} else {
			tableD.setParmValue(new TParm());
		}
	}
	
    /**
     * ��շ���
     */
    public void onClear() {
    	// ��ʼ��ҳ��ؼ�����
    	this.onInitControl();
    }
    
	/**
	 * ȫѡ��ѡ��ѡ���¼�
	 */
	public void onCheckSelectAll() {
		if (tableM.getRowCount() <= 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < tableM.getRowCount(); i++) {
			tableM.setItem(i, "FLG", flg);
		}
		
		this.onTableMCheckBoxClicked(tableM);
	}
	
	/**
	 * ��ӡ�����嵥
	 */
	public void onPrintReady() {
		TParm parm = tableD.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// ��ӡ��Parm
		TParm printParm = new TParm();
		// ��ӡ����
		TParm printData = new TParm();
		int count = parm.getCount();
		
		printParm.setData("TITLE", "TEXT", Manager.getOrganization().
                getHospitalCHNFullName(Operator.getRegion()) + "����Ӫ�����ϵ�");
		printParm.setData("PRINT_DATE", "TEXT", "�Ʊ�����:"+todayDate);
		
		for (int i = 0; i < count; i++) {
			printData.addData("FORMULA_CHN_DESC", parm.getValue("FORMULA_CHN_DESC", i));
			printData.addData("TOTAL_QTY", parm.getDouble("MEDI_QTY", i));
			printData.addData("TOTAL_UNIT", parm.getValue("MEDI_UNIT", i));
		}
		
		printData.setCount(count);
		
		printData.addData("SYSTEM", "COLUMNS", "FORMULA_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "TOTAL_QTY");
		printData.addData("SYSTEM", "COLUMNS", "TOTAL_UNIT");
		
		printParm.setData("TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSENMaterialsReadyPrint", printParm);
	}
	
	/**
	 * ��ӡ���ӵ�
	 */
	public void onPrintHandOver() {
		TParm parm = tableM.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// ��ӡ��Parm
		TParm printParm = new TParm();
		// ��ӡ������
		TParm printData = new TParm();
		
		// ����ѡ�����ݰ��ղ�������
		printData = this.sortByStationCode(parm);
		
		if (printData.getErrCode() < 0) {
			this.messageBox(printData.getErrText());
			return;
		}
		
		printParm.setData("TITLE", "TEXT", Manager.getOrganization().
                getHospitalCHNFullName(Operator.getRegion()) + "����Ӫ�����ӵ�");
		printParm.setData("PRINT_DATE", "TEXT", "��ӡ����:"+todayDate);
		
		printData.setCount(printData.getCount("MR_NO"));
		
		printData.addData("SYSTEM", "COLUMNS", "STATION_CODE");
		printData.addData("SYSTEM", "COLUMNS", "BED_NO");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		printData.addData("SYSTEM", "COLUMNS", "FREQ");
		printData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
		printData.addData("SYSTEM", "COLUMNS", "TOTAL_QTY");
		
		printParm.setData("TABLE", printData.getData());
		
		this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSENHandOverPrint", printParm);
	}
	
	/**
	 * ��ӡ�����ǩ
	 */
	public void onPrintENBarCode() {
		TParm parm = tableM.getShowParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		// ��ӡ��Parm
		TParm printParm = new TParm();
		TParm queryParm = new TParm();
		TParm result = new TParm();
		int count = parm.getCount();
		int selectedCount = 0;
		// ��ǩ��ʾ����
		String completeDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// �����ʶ
		boolean reprintFlg = false;
		// ��ӡѭ������
		int printCount = 0;
		// ҽ����ע
		String drNote = "";
		
		// ����״̬
		if (getRadioButton("STATUS_N").isSelected()) {
			reprintFlg = false;
		} else {
			reprintFlg = true;
		}
		
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				queryParm = tableM.getParmValue().getRow(i);
				// ��ѯҽ����Ϣ�еı�ע
				result = NSSEnteralNutritionTool.getInstance().queryOrderInfo(
						queryParm);
				
				if (result.getErrCode() < 0) {
					this.messageBox("��ѯҽ����Ϣ�쳣");
					err(result.getErrCode() + " " + result.getErrText());
					return;
				}
				
				if (result.getCount() > 0) {
					drNote = result.getValue("DR_NOTE", 0);
				}
				
				if (StringUtils.isNotEmpty(drNote.trim())) {
					drNote = "(" + drNote + ")";
				}
				
				printParm = new TParm();
				// ����
				printParm.setData("BAR_CODE", "TEXT", parm.getValue("EN_PREPARE_NO", i));
				printParm.setData("DIET_DESC", "TEXT", parm.getValue("ORDER_DESC", i));
				printParm.setData("DR_NOTE", "TEXT", drNote);
				printParm.setData("BED_DESC", "TEXT", parm.getValue("BED_NO_DESC", i));
				printParm.setData("PAT_NAME", "TEXT", parm.getValue("PAT_NAME", i));
				printParm.setData("MR_NO", "TEXT", parm.getValue("MR_NO", i));
				printParm.setData("MEDI_QTY", "TEXT", parm.getInt(
						"LABEL_CONTENT", i)
						+ parm.getValue("LABEL_UNIT", i)
						+ "*"
						+ parm.getValue("LABEL_QTY", i));
				if (StringUtils.isNotEmpty(parm.getValue("PREPARE_DATE", i))) {
					completeDate = parm.getValue("PREPARE_DATE", i);
				}
				printParm.setData("COM_DATE", "TEXT", completeDate);
				printParm.setData("DEPT_DESC", "TEXT", DeptTool.getInstance()
						.getDescByCode(Operator.getDept()));
				
				// ����ǲ�����ֻ����һ�Σ������ռ�����ı�ǩ�����Զ�ѭ����ӡ
				if (reprintFlg) {
					printCount = 1;
				} else {
					printCount = parm.getInt("LABEL_QTY", i);
				}
				
				for (int k = 0; k < printCount; k++) {
					this.printENBarCode(printParm);
				}
				selectedCount++;
			}
		}
		
		if (selectedCount < 1) {
			this.messageBox("�빴ѡҪ��ӡ������");
			return;
		}
	}
	
	/**
	 * ��ӡ�����ǩ
	 */
	private void printENBarCode(TParm printParm) {
		this.openPrintWindow("%ROOT%\\config\\prt\\NSS\\NSSENBarCodePrint.jhw", printParm, true);
	}
	
	/**
	 * ����ѡ�����ݰ��ղ�������
	 */
	private TParm sortByStationCode(TParm parm) {
		TParm sortParm = new TParm();
		int count = parm.getCount();
		// ����List
		List<String> stationCodeList = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("FLG", i)) {
				if (!stationCodeList.contains(parm.getValue("STATION_CODE", i))) {
					stationCodeList.add(parm.getValue("STATION_CODE", i));
				}
			}
		}
		
		// ���û�й�ѡ������
		if (stationCodeList.size() == 0) {
			sortParm.setErr(-1, "�빴ѡҪ��ӡ������");
			return sortParm;
		}
		
		int stationCodeListSize = stationCodeList.size();
		for (int j = 0; j < stationCodeListSize; j++) {
			for (int k = 0; k < count; k++) {
				if (parm.getBoolean("FLG", k)
						&& StringUtils.equals(stationCodeList.get(j), parm
								.getValue("STATION_CODE", k))) {
					sortParm.addData("STATION_CODE", parm.getValue(
							"STATION_CODE", k));
					sortParm.addData("BED_NO", parm.getValue("BED_NO_DESC", k));
					sortParm.addData("MR_NO", parm.getValue("MR_NO", k));
					sortParm.addData("PAT_NAME", parm.getValue("PAT_NAME", k));
					sortParm.addData("MEDI_QTY", parm.getDouble(
							"LABEL_CONTENT", k)
							+ parm.getValue("LABEL_UNIT", k)
							+ "*"
							+ parm.getValue("LABEL_QTY", k));
					sortParm.addData("FREQ", parm.getValue("FREQ_CODE", k));
					sortParm
							.addData("TAKE_DAYS", parm.getValue("TAKE_DAYS", k));
					sortParm.addData("TOTAL_QTY", parm.getValue("TOTAL_QTY", k)
							+ parm.getValue("TOTAL_UNIT", k));
				}
			}
			
			// ��װ��ӡ����
			this.addPrintParmData(sortParm);
		}
		
		return sortParm;
	}
	
    /**
     * ��װ��ӡ����
     *
     * @param parm
     *            TParm
     * @return
     */
	private void addPrintParmData(TParm parm) {
		parm.addData("STATION_CODE", "");
		parm.addData("BED_NO", "");
		parm.addData("MR_NO", "");
		parm.addData("PAT_NAME", "");
		parm.addData("MEDI_QTY", "");
		parm.addData("FREQ", "");
		parm.addData("TAKE_DAYS", "");
		parm.addData("TOTAL_QTY", "������Ա:");
		
		parm.addData("STATION_CODE", "");
		parm.addData("BED_NO", "");
		parm.addData("MR_NO", "");
		parm.addData("PAT_NAME", "");
		parm.addData("MEDI_QTY", "");
		parm.addData("FREQ", "");
		parm.addData("TAKE_DAYS", "");
		parm.addData("TOTAL_QTY", "");
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
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
	
	/**
	 * �õ�getCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
