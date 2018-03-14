package com.javahis.ui.nss;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
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
 * @author wangb 2015.3.16
 * @version 1.0
 */
public class NSSEnteralNutritionOrderControl extends TControl {
    public NSSEnteralNutritionOrderControl() {
        super();
    }

    private TTable tablePat; // ������Ϣtable
    private TTable tableDrOrder; // סԺҽ��ҽ��table
    private TTable tableDietitionOrder; // Ӫ��ʦҽ��table
    private TTable tableFormula; // �䷽table
    private TTable tableNutrition; // Ӫ���ɷ�table
    private Map<String, String> dietitionOrderMap; // Ӫ��ʦҽ������Map

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
		tablePat = getTable("TABLE_PAT");
    	tableDrOrder = getTable("TABLE_DR_ORDER");
    	tableDietitionOrder = getTable("TABLE_DIETITION_ORDER");
    	tableFormula = getTable("TABLE_FORMULA");
    	tableNutrition = getTable("TABLE_NUTRITION");
    	
		// ������Ϣ������ݵ���¼�
		this.callFunction("UI|TABLE_PAT|addEventListener", "TABLE_PAT->"
				+ TTableEvent.CLICKED, this, "onTablePatClicked");
		// סԺҽ����ʳҽ��������ݵ���¼�
		this.callFunction("UI|TABLE_DR_ORDER|addEventListener", "TABLE_DR_ORDER->"
				+ TTableEvent.CLICKED, this, "onTableDrOrderClicked");
		// Ӫ��ʦҽ��������ݵ���¼�
		this.callFunction("UI|TABLE_DIETITION_ORDER|addEventListener", "TABLE_DIETITION_ORDER->"
				+ TTableEvent.CLICKED, this, "onTableDietitionOrderClicked");
        // ע�ἤ��TableFormula�������¼�
		getTable("TABLE_FORMULA").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
				this, "onTableFormulaInput");
		// �޸��䷽����ʱ�ĵ����¼�
		this.addEventListener("TABLE_FORMULA->" + TTableEvent.CHANGE_VALUE,
				"onTableFormulaChangeValue");
		
		// ��ʼ������������Ϣ����
		this.onInitPatInfo();
		
		// ��ʼ��Ӫ��ʦҽ������
		this.onInitENDietitionOrderInfo();
		
		// ��ʼ��Ӫ���ɷֱ������
		this.onInitENNutritionTableInfo();
	}
	
	/**
	 * ��ʼ��������Ϣ����
	 */
	private void onInitPatInfo() {
		clearValue("ENO_STATION_CODE;ENO_DEPT_CODE;BED_NO_DESC;MR_NO;PAT_NAME;SEX_CODE;AGE;WEIGHT;HEIGHT;DR_DIET;EN_ORDER_NO");
		getRadioButton("ORDER_STATUS_N").setSelected(true);
	}
	
	/**
	 * ��ʼ��Ӫ��ʦҽ������
	 */
	private void onInitENDietitionOrderInfo() {
		clearValue("MEDI_QTY;MEDI_UNIT;FREQ_CODE;TOTAL_QTY;TOTAL_UNIT;CONTAINER_CODE;LABEL_QTY;EN_ORDER_NO");
	}
	
	/**
	 * ��ʼ��Ӫ���ɷֱ������
	 */
	private void onInitENNutritionTableInfo() {
		TParm parm = new TParm();
		String[] nutritionArray = { "Ene,0kcal,'',KCl,0g", "Pro,0g,0%,NaCl,0g",
				"Fat,0g,0%,DF,0g", "Carb,0g,0%,Ca,0g", "N,0g,0%,VC,0g" };
		int count = nutritionArray.length;
		String[] dataArray = new String[3];
		for (int i = 0; i < count; i++) {
			dataArray = nutritionArray[i].split(",");
			parm.addData("NUTRITION_DESC_L", dataArray[0].replaceAll("'", ""));
			parm.addData("DATA_L", dataArray[1].replaceAll("'", ""));
			parm.addData("PROPORTION", dataArray[2].replaceAll("'", ""));
			parm.addData("NUTRITION_DESC_R", dataArray[3].replaceAll("'", ""));
			parm.addData("DATA_R", dataArray[4].replaceAll("'", ""));
		}
		
		tableNutrition.setParmValue(parm);
	}
	
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	// ���ݲ�ѯ������ѯ��������Ӫ����ʳҽ���Ĳ�����Ϣ
		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENOrderPatInfo(this.getQueryParm());
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯ����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("��������");
        	// ��ʼ��Ӫ��ʦҽ������
        	this.onInitENDietitionOrderInfo();
        	// ��ʼ��Ӫ���ɷֱ������
        	this.onInitENNutritionTableInfo();
        	
        	tablePat.setParmValue(new TParm());
    		tableDrOrder.setParmValue(new TParm());
    		tableDietitionOrder.setParmValue(new TParm());
    		tableFormula.setParmValue(new TParm());
    		return;
    	}
    	
    	// ����δ���������Ҫ�����������ͣ��ҽ��������
		if (getRadioButton("ORDER_STATUS_N").isSelected()) {
			TParm queryParm = new TParm();
	    	int count = result.getCount();
	    	for (int i = count - 1; i > -1; i--) {
	    		queryParm = new TParm();
	    		queryParm.setData("CASE_NO", result.getValue("CASE_NO", i));
	    		queryParm.setData("ORDER_NO", result.getValue("ORDER_NO", i));
	    		queryParm.setData("ORDER_SEQ", result.getInt("ORDER_SEQ", i));
	    		queryParm.setData("DIE_ORDER_STATUS", "Y");
	    		queryParm.setData("DR_ORDER_STATUS", "Y");
	    		
	    		// ��ѯ��ǰ�����Ƿ����ʹ���е�Ӫ��ʦҽ����������
	    		queryParm = NSSEnteralNutritionTool.getInstance().queryENOrderM(queryParm);
	    		
	    		if (queryParm.getErrCode() < 0) {
	    			this.messageBox("��ѯӪ��ʦҽ���������ݴ���");
	    			continue;
	    		}
	    		
	    		// �������ʹ���е�ҽ����У���ҽ�����Ƿ�����䷽
	    		if (queryParm.getCount() > 0) {
	    			queryParm = NSSEnteralNutritionTool.getInstance().queryENOrderD(queryParm.getRow(0));
	    			if (queryParm.getErrCode() < 0) {
	    				continue;
	    			}
	    			
	    			if (queryParm.getCount() > 0) {
	    				result.removeRow(i);
	    			}
	    		}
	    	}
		}
    	
    	if (result.getCount("CASE_NO") < 1) {
    		this.messageBox("��������");
    	}
    	
    	tablePat.setParmValue(result);
    	// ��ʼ��Ӫ��ʦҽ������
    	this.onInitENDietitionOrderInfo();
    	// ��ʼ��Ӫ���ɷֱ������
    	this.onInitENNutritionTableInfo();
    	
		tableDrOrder.setParmValue(new TParm());
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
    }
    
    /**
     * ���淽��
     */
    public void onSave() {
    	// ����У��
    	if (!this.validate()) {
    		return;
    	}
    	
    	// ����Ӫ��ʦҽ�����ſؼ��жϲ�������
    	String eNOrderNo = this.getValueString("EN_ORDER_NO");
    	
    	// ����Ӫ��ʦҽ������
    	if (StringUtils.isEmpty(eNOrderNo)) {
    		// ��ȡ��������
    		TParm saveParm = this.getSaveParmData();
    		
    		if (saveParm.getErrCode() < 0) {
    			this.messageBox(saveParm.getErrText());
    			return;
    		}
    		
    		// ����Ӫ��ʦҽ������
    		TParm result = NSSEnteralNutritionTool.getInstance()
    				.insertNSSENOrderM(saveParm);
    		
    		if (result.getErrCode() < 0) {
    			this.messageBox("E0001");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
    		} else {
    			this.messageBox("P0001");
    			
    			this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
    		}
    	} else {
    		// ����ע��
    		boolean updateFlg = false;
    		
        	// ��ǰѡ�е�Ӫ��ʦҽ������
    		int selectedRow = tableDietitionOrder.getSelectedRow();
    		TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
    		
    		// ���Ӫ��ʦҽ�������иĶ�
			if (!StringUtils.equals(this.getValueString("MEDI_QTY"),
					dietitionOrderMap.get("MEDI_QTY"))
					|| !StringUtils.equals(this.getValueString("MEDI_UNIT"),
							dietitionOrderMap.get("MEDI_UNIT"))
					|| !StringUtils.equals(this.getValueString("FREQ_CODE"),
							dietitionOrderMap.get("FREQ_CODE"))
					|| !StringUtils.equals(this.getValueString("TOTAL_QTY"),
							dietitionOrderMap.get("TOTAL_QTY"))
					|| !StringUtils.equals(this.getValueString("TOTAL_UNIT"),
							dietitionOrderMap.get("TOTAL_UNIT"))
					|| !StringUtils.equals(this
							.getValueString("CONTAINER_CODE"),
							dietitionOrderMap.get("CONTAINER_CODE"))) {
				
				data.setData("CANCEL_FLG", "N");
				data.setData("PREPARE_STATUS", "0");
        		// ����֮ǰ����֤�Ƿ�����չ��δȡ����δ���Ƶ�����
        		TParm validateResult = NSSEnteralNutritionTool.getInstance().queryENDspnM(data);
        		
            	if (validateResult.getErrCode() < 0) {
            		this.messageBox("��ѯ����չ�����ݴ���");
        			err("ERR:" + validateResult.getErrCode() + validateResult.getErrText());
        			return;
            	}
            	
            	if (validateResult.getCount() > 0) {
            		this.messageBox("��ҽ������δ�������Ƶ�չ�����ݣ�����ȡ����ȷ�����ݺ��ٽ����޸�");
            		return;
            	}
				
				TParm updateParm = new TParm();
				updateParm.setData("CASE_NO", data.getValue("CASE_NO"));
				updateParm.setData("ORDER_NO", data.getValue("ORDER_NO"));
				updateParm.setData("ORDER_SEQ", data.getValue("ORDER_SEQ"));
				updateParm.setData("EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
				updateParm.setData("MEDI_QTY", this.getValueString("MEDI_QTY"));
				updateParm.setData("MEDI_UNIT", this.getValueInt("MEDI_UNIT"));
				updateParm.setData("FREQ_CODE", this.getValueString("FREQ_CODE"));
				
				TParm parm = new TParm();
				parm.setData("TOTAL_QTY", this.getValueDouble("TOTAL_QTY"));
				parm.setData("MEDI_QTY", this.getValueDouble("MEDI_QTY"));
				// ʹ������
				updateParm.setData("TAKE_DAYS", this.getTakeDays(parm));
				
				// ����Ӧ��ӡ��ǩ����
				TParm result = this.getLabelQty(parm);
				
				if (result.getErrCode() < 0) {
					this.messageBox(result.getErrText());
					return;
				}
				
				int labelQty = result.getInt("LABEL_QTY");
				// ��ǩ��������
				updateParm.setData("LABEL_QTY", labelQty);
				// ÿ�ű�ǩ����
				double labelContent = this.getValueDouble("TOTAL_QTY") / labelQty;
				updateParm.setData("LABEL_CONTENT", labelContent);
				updateParm.setData("TOTAL_QTY", this.getValueInt("TOTAL_QTY"));
				updateParm.setData("TOTAL_UNIT", this.getValueString("TOTAL_UNIT"));
				updateParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
				updateParm.setData("OPT_USER", Operator.getID());
				updateParm.setData("OPT_TERM", Operator.getIP());
				
				// ����Ӫ��ʦҽ����������
				result = NSSEnteralNutritionTool.getInstance().updateENOrderM(updateParm);
				
	    		if (result.getErrCode() < 0) {
	    			this.messageBox("E0001");
	    			err("ERR:" + result.getErrCode() + result.getErrText());
	    			return;
	    		}
	    		
	    		updateFlg = true;
			}
    		
    		// ����Ӫ��ʦҽ��ϸ��
    		tableFormula.acceptText();
            TDataStore dataStore = tableFormula.getDataStore();
            
            // ����䷽��ϸ�и���
            if (dataStore.getUpdateSQL().length > 0) {
            	// ѡ�е�Ӫ��ʦҽ������
        		TParm selectedDietParm = tableDietitionOrder.getParmValue().getRow(tableDietitionOrder
        				.getSelectedRow());
        		selectedDietParm.setData("CANCEL_FLG", "N");
        		
        		// ����֮ǰ����֤�Ƿ�����չ��δȡ������
        		TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(selectedDietParm);
        		
            	if (result.getErrCode() < 0) {
            		this.messageBox("��ѯ����չ�����ݴ���");
        			err("ERR:" + result.getErrCode() + result.getErrText());
        			return;
            	}
            	
            	if (result.getCount() > 0) {
            		this.messageBox("��ҽ��������չ��δȡ�������ݣ������޸��䷽��ϸ");
            		return;
            	}
            	
        		// ��ѯ����SEQ
        		TParm parm = NSSEnteralNutritionTool.getInstance().queryENOrderD(data);
        		
        		int maxSeq = 0;
        		if (parm.getCount() > 0) {
        			maxSeq = parm.getInt("SEQ", parm.getCount() - 1);
        		}

                // ���ȫ����������
                int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
                
    			for (int i = 0; i < newrows.length; i++) {
    				dataStore.setItem(newrows[i], "EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
    				dataStore.setItem(newrows[i], "MR_NO", data.getValue("MR_NO"));
    				dataStore.setItem(newrows[i], "SEQ", maxSeq + 1);
    				dataStore.setItem(newrows[i], "CASE_NO", data.getValue("CASE_NO"));
    				maxSeq++;
    			}
    			
    			if (!dataStore.update()) {
    				this.messageBox("E0001");
    				return;
    			} else {
    				updateFlg = true;
    			}
            }
            
			// ִ�гɹ�
			if (updateFlg) {
				TParm orderInfoParm = new TParm();
				orderInfoParm.setData("CASE_NO", data.getValue("CASE_NO"));
				orderInfoParm.setData("ORDER_NO", data.getValue("ORDER_NO"));
				orderInfoParm.setData("ORDER_SEQ", data.getValue("ORDER_SEQ"));
				orderInfoParm.setData("EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
				orderInfoParm.setData("ORDER_DEPT_CODE", Operator.getDept());
				orderInfoParm.setData("ORDER_DR_CODE", Operator.getID());
				
				// �����������䷽�����޸ĺ���¿�����Ա��Ϣ
				TParm result = NSSEnteralNutritionTool.getInstance()
						.updateENOrderMOrderInfo(orderInfoParm);
				
				if (result.getErrCode() < 0) {
            		this.messageBox("���²�����Ա��Ϣ���ݴ���");
        			err("ERR:" + result.getErrCode() + result.getErrText());
        			return;
            	}
            	
				this.messageBox("P0001");
			} else {
				this.messageBox("���޸�����");
				return;
			}
			
			this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
			tableDietitionOrder.setSelectedRow(0);
			this.onTableDietitionOrderClicked(0);
    		
            return;
    	}
    }
    
    /**
     * ɾ������
     */
    public void onDelete() {
    	TParm parmDiet = tableDietitionOrder.getParmValue();
    	if (parmDiet == null || parmDiet.getCount() <= 0) {
    		this.messageBox("��ɾ������");
    		return;
    	}
    	
    	if (tableDietitionOrder.getSelectedRow() < 0) {
    		this.messageBox("��ѡ��Ҫɾ����Ӫ��ʦҽ������");
    		return;
    	}
    	
		// ǿ��ʧȥ�༭����
		if (tableDietitionOrder.getTable().isEditing()) {
			tableDietitionOrder.getTable().getCellEditor().stopCellEditing();
		}
		
		// ǿ��ʧȥ�༭����
		if (tableFormula.getTable().isEditing()) {
			tableFormula.getTable().getCellEditor().stopCellEditing();
		}
		
    	// ɾ��������������ע��
    	boolean deleteTableDietFlg = false;
    	// ɾ������ϸ������ע��
    	boolean deleteTableFormulaFlg = false;
    	// ѡ�е�Ӫ��ʦҽ������
		TParm selectedDietParm = parmDiet.getRow(tableDietitionOrder
				.getSelectedRow());
		selectedDietParm.setData("CANCEL_FLG", "N");
		
		// ����֮ǰ����֤ѡ�е�Ӫ��ʦ�����Ƿ�����չ��δȡ������
		TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(selectedDietParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯ��չ��ҽ�����ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() > 0) {
    		this.messageBox("��ҽ��������չ������δȡ�������ݣ�����ɾ��");
    		return;
    	}
		
    	for (int i = 0; i < parmDiet.getCount(); i++) {
    		if (parmDiet.getBoolean("FLG", i)) {
    			deleteTableDietFlg = true;
    			break;
    		}
    	}
    	
    	// ѡ������ɾ��
    	if (deleteTableDietFlg) {
    		if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ���ö�������", 2) == 0) {
    			// ��ɾ�������µ�����ϸ��
    			tableFormula.getDataStore().deleteRowAll();
    			
    			if (!tableFormula.getDataStore().update()) {
    				this.messageBox("E0001");
    				return;
    			}
    			
    			// ɾ������
				result = NSSEnteralNutritionTool.getInstance()
						.deleteNSSENOrderM(selectedDietParm);
				
		    	if (result.getErrCode() < 0) {
		    		this.messageBox("ɾ���������");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
		    	}
		    	
		    	this.messageBox("P0003");
				this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
    		}
    	} else {
    		int formulaCount = tableFormula.getDataStore().rowCount();
    		TParm formulaParm = tableFormula.getParmValue();
    		
    		// ���û��ѡ���������ݣ���鿴�Ƿ�ѡ����ϸ��
    		for (int k = formulaCount - 1; k > -1; k--) {
				if (formulaParm.getBoolean("FLG", k)) {
					if (StringUtils.isNotEmpty(tableFormula.getDataStore().getItemString(k, "EN_ORDER_NO"))) {
						tableFormula.getDataStore().deleteRow(k);
						tableFormula.setDSValue();
					} else {
						tableFormula.removeRow(k);
						tableFormula.setDSValue();
					}
					deleteTableFormulaFlg = true;
				}
    		}
    		
			if (deleteTableFormulaFlg) {
				TParm sqlParm = new TParm();
				sqlParm.setData("DELETE_SQL", tableFormula.getDataStore().getUpdateSQL());
				// ִ��ɾ������
				result = TIOM_AppServer.executeAction(
						"action.nss.NSSEnteralNutritionAction",
						"deleteNSSENOrderD", sqlParm);
				
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					this.messageBox("E0005");
					return;
				}
				
				tableFormula.setDSValue();
				this.messageBox("P0003");
				// �����䷽��ϸ����е�ֵ����Ӫ���ɷ�����
				this.calculateTotalAmountOfNutrients();
			}
    		
    		// ��ϸ�û�й�ѡ
    		if (!deleteTableDietFlg && !deleteTableFormulaFlg) {
    			this.messageBox("�빴ѡ��Ҫɾ��������");
    			return;
    		}
    	}
    	
    }
    
	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	private TParm getSaveParmData() {
		// ѡ�е�סԺҽʦҽ��
		TParm saveParm = tableDrOrder.getParmValue().getRow(
				tableDrOrder.getSelectedRow());
		
		// ȡ��ԭ��
		saveParm.setData("EN_ORDER_NO", SystemTool.getInstance().getNo("ALL",
				"NSS", "EN_ORDER_NO", "EN_ORDER_NO"));
		saveParm.setData("MEDI_QTY", this.getValueDouble("MEDI_QTY"));
		saveParm.setData("MEDI_UNIT", this.getValueString("MEDI_UNIT"));
		saveParm.setData("FREQ_CODE", this.getValueString("FREQ_CODE"));
		
		TParm parm = new TParm();
		parm.setData("TOTAL_QTY", this.getValueDouble("TOTAL_QTY"));
		parm.setData("MEDI_QTY", this.getValueDouble("MEDI_QTY"));
		
		// ����ʹ������
		saveParm.setData("TAKE_DAYS", this.getTakeDays(parm));
		saveParm.setData("TOTAL_QTY", this.getValueDouble("TOTAL_QTY"));
		saveParm.setData("TOTAL_UNIT", this.getValueString("TOTAL_UNIT"));
		saveParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
		saveParm.setData("ORDER_DEPT_CODE", Operator.getDept());
		
		// ����Ӧ��ӡ��ǩ����
		TParm result = this.getLabelQty(parm);
		
		if (result.getErrCode() < 0) {
			return result;
		}
		
		// ��ǩ����
		int labelQty = result.getInt("LABEL_QTY");
		// ÿ�ű�ǩ����
		saveParm.setData("LABEL_QTY", labelQty);
		double labelContent = this.getValueDouble("TOTAL_QTY") / labelQty;
		saveParm.setData("LABEL_CONTENT", labelContent);
		saveParm.setData("ORDER_DEPT_CODE", Operator.getDept());
		saveParm.setData("ORDER_DR_CODE", Operator.getID());
		saveParm.setData("DC_DR_CODE", "");
		saveParm.setData("DC_DATE", "");
		saveParm.setData("OPT_USER", Operator.getID());
		saveParm.setData("OPT_TERM", Operator.getIP());
		return saveParm;
	}
    
	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		// ��ʳҽ��
		parm.setData("DR_DIET", getValueString("DR_DIET"));
		// ����
		parm.setData("DEPT_CODE", getValueString("ENO_DEPT_CODE"));
		// ����
		parm.setData("STATION_CODE", getValueString("ENO_STATION_CODE"));
		// ������
		parm.setData("MR_NO", getValueString("MR_NO"));
		// ���״̬
		if (getRadioButton("ORDER_STATUS_N").isSelected()) {
			parm.setData("ORDER_STATUS", "N");
		} else {
			parm.setData("ORDER_STATUS", "Y");
		}
		
		// �����ʱҽ�����ӵ�ǰ�����������ˣ�ֻ�ڵ�����Կ���
		parm.setData("SYSDATE", SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace("-", ""));
		
		return parm;
	}
	
	/**
	 * ҳ��ؼ�������֤
	 */
	private boolean validate() {
		// �������ʱ����֤������
		// ����
		if (this.getValueDouble("MEDI_QTY") == 0) {
			this.messageBox("����������");
			return false;
		}
		// ������λ
		if (StringUtils.isEmpty(this.getValueString("MEDI_UNIT"))) {
			this.messageBox("������������λ");
			return false;
		}
		// Ƶ��
		if (StringUtils.isEmpty(this.getValueString("FREQ_CODE"))) {
			this.messageBox("������Ƶ��");
			return false;
		}
		// ����
		if (this.getValueDouble("TOTAL_QTY") == 0) {
			this.messageBox("����������");
			return false;
		}
		// ������λ
		if (StringUtils.isEmpty(this.getValueString("TOTAL_UNIT"))) {
			this.messageBox("������������λ");
			return false;
		}
		// ����
		if (StringUtils.isEmpty(this.getValueString("CONTAINER_CODE"))) {
			this.messageBox("������ʢ������");
			return false;
		}
		// ������λ��������λ����һ��
		if (!StringUtils.equals(this.getValueString("MEDI_UNIT"), this
				.getValueString("TOTAL_UNIT"))) {
			this.messageBox("������λ��������λ��һ��");
			return false;
		}

		// ����ѡ���������ѯ��Ӧ�ĵ�λ
		TParm result = NSSEnteralNutritionTool.getInstance().queryENContainer(
				this.getValueString("CONTAINER_CODE"));

		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�����ֵ����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return false;
		}

		if (result.getCount() <= 0) {
			this.messageBox("���������ֵ�����");
			return false;
		}

		// ������λ��������λ����һ��
		if (!StringUtils.equals(this.getValueString("TOTAL_UNIT"), result
				.getValue("CAPACITY_UNIT", 0))) {
			this.messageBox("������λ���������λ��һ��");
			return false;
		}
		
		tableFormula.acceptText();
        for (int i = 0; i < tableFormula.getRowCount(); i++) {
            if (!tableFormula.getDataStore().isActive(i)) {
                continue;
            }
            if (tableFormula.getItemDouble(i, "MEDI_QTY") <= 0) {
                this.messageBox("��������С�ڻ����0");
                return false;
            }
        }
		
		return true;
	}
	
    /**
     * ��շ���
     */
    public void onClear() {
    	// ��ʼ��������Ϣ����
    	this.onInitPatInfo();
    	// ��ʼ��Ӫ��ʦҽ������
    	this.onInitENDietitionOrderInfo();
    	// ��ʼ��Ӫ���ɷֱ������
    	this.onInitENNutritionTableInfo();
    	
    	tablePat.setParmValue(new TParm());
		tableDrOrder.setParmValue(new TParm());
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
    }
    
	/**
	 * ��Ӷ�tablePat�ļ����¼�
	 * 
	 * @param row
	 */
	public void onTablePatClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableDrOrder.setParmValue(new TParm());
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
    	// ��ʼ��Ӫ��ʦҽ������
    	this.onInitENDietitionOrderInfo();
    	// ��ʼ��Ӫ���ɷֱ������
    	this.onInitENNutritionTableInfo();
		
		int selectedRow = tablePat.getSelectedRow();
		TParm data = tablePat.getParmValue().getRow(selectedRow);
		// �������ѯ�������Ϸ�����
		setValueForParm("ENO_DEPT_CODE;ENO_STATION_CODE;BED_NO_DESC;MR_NO;PAT_NAME;SEX_CODE;AGE;WEIGHT;HEIGHT", data);
		
		// ���ݲ�����Ϣ��ʾסԺҽ����������ʳҽ��
		TParm result = NSSEnteralNutritionTool.getInstance().queryENDrOrderInfo(data);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯסԺҽ��ҽ������");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("����סԺҽ��ҽ��");
			return;
		}
		
		tableDrOrder.setParmValue(result);
	}
	
	/**
	 * ��Ӷ�tableDrOrder�ļ����¼�
	 * 
	 * @param row
	 */
	public void onTableDrOrderClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableDietitionOrder.setParmValue(new TParm());
		tableFormula.setParmValue(new TParm());
		
		int selectedRow = tableDrOrder.getSelectedRow();
		TParm data = tableDrOrder.getParmValue().getRow(selectedRow);
		
		if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
			data.setData("DIE_ORDER_STATUS", "Y");
		} else {
			data.setData("DIE_ORDER_STATUS", "N");
		}
		
		// ����ѡ�е�סԺҽ����������ʳҽ�������в�ѯ��Ӧ��Ӫ��ʦҽ����������
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderM(data);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯӪ��ʦҽ���������");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		// ����鵽δͣ��Ӫ��ʦҽ���������ݴ���
		if (result.getCount() > 0) {
	    	// ��ʼ��Ӫ��ʦҽ������
	    	this.onInitENDietitionOrderInfo();
        	// ��ʼ��Ӫ���ɷֱ������
        	this.onInitENNutritionTableInfo();
	    	
			tableDietitionOrder.setParmValue(result);
			
			if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
				tableDietitionOrder.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
			} else {
				tableDietitionOrder.setLockColumns("all");
			}
		} else {
			if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
				// ��ʼ��Ӫ��ʦҽ������
				this.onInitENDietitionOrderInfo();
	        	// ��ʼ��Ӫ���ɷֱ������
	        	this.onInitENNutritionTableInfo();

				tableDietitionOrder.setParmValue(new TParm());
				// ��Ӫ��ʦҽ���ؼ���������
				setValueForParm("MEDI_QTY;MEDI_UNIT;FREQ_CODE", data);
				this.setValue("TOTAL_QTY", data.getDouble("DOSAGE_QTY"));
				this.setValue("TOTAL_UNIT", data.getValue("DOSAGE_UNIT"));
				
				// �����ǳ��ڻ�����ʱ��������������Ƶ���Զ���������
				// ����Ƶ�δ����ѯһ���ִ�д���
				result = NSSEnteralNutritionTool.getInstance().queryPhaFreq(
						this.getValueString("FREQ_CODE"));

				if (result.getErrCode() < 0) {
					this.messageBox("��ѯƵ���ֵ����ݴ���");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
				}

				if (result.getCount() <= 0) {
					this.messageBox("����Ƶ���ֵ�����");
					return;
				}

				// ����=����*һ��Ĵ���
				double totalQty = data.getDouble("MEDI_QTY")
						* result.getDouble("FREQ_TIMES", 0);
				this.setValue("TOTAL_QTY", totalQty);
			}
		}
		
	}
	
	/**
	 * ��Ӷ�tableDietitionOrder�ļ����¼�
	 * 
	 * @param row
	 */
	public void onTableDietitionOrderClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableFormula.setParmValue(new TParm());
		
		int selectedRow = tableDietitionOrder.getSelectedRow();
		TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
		
		dietitionOrderMap = new HashMap<String, String>();
		dietitionOrderMap.put("MEDI_QTY", data.getValue("MEDI_QTY"));
		dietitionOrderMap.put("MEDI_UNIT", data.getValue("MEDI_UNIT"));
		dietitionOrderMap.put("FREQ_CODE", data.getValue("FREQ_CODE"));
		dietitionOrderMap.put("TOTAL_QTY", data.getValue("TOTAL_QTY"));
		dietitionOrderMap.put("TOTAL_UNIT", data.getValue("TOTAL_UNIT"));
		dietitionOrderMap.put("CONTAINER_CODE", data.getValue("CONTAINER_CODE"));
		
		// ��Ӫ��ʦҽ���ؼ���������
		setValueForParm("MEDI_QTY;MEDI_UNIT;FREQ_CODE;TOTAL_QTY;TOTAL_UNIT;CONTAINER_CODE;LABEL_QTY;EN_ORDER_NO", data);
		
		// ��ϸ��Ϣ
		this.getTableFormulaInfo(data);
		// ��ϸ�������һ��
		this.addRow();
		
		if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
			tableFormula.setLockColumns("3,4,5,6");
		} else {
			tableFormula.setLockColumns("all");
		}
		
		// �����䷽��ϸ����е�ֵ����Ӫ���ɷ�����
		this.calculateTotalAmountOfNutrients();
	}
	
    /**
     * ���������ѯ�䷽��ϸ����
     */
    private void getTableFormulaInfo(TParm parm) {
		// ��ϸ��Ϣ
    	tableFormula.removeRowAll();
    	tableFormula.setSelectionMode(0);
		TDS tds = new TDS();
		String sql = NSSEnteralNutritionTool.getInstance().queryENOrderDSql(parm);
		tds.setSQL(sql);
		tds.retrieve();

		tableFormula.setDataStore(tds);
		tableFormula.setDSValue();
    }
	
    /**
     * �䷽��ϸTABLE�������
     */
    private void addRow() {
		// ��δ�༭��ʱ����
		if (!this.isNewRow()) {
			return;
		}
		int row = tableFormula.addRow();
		tableFormula.getDataStore().setActive(row, false);
    }
    
	/**
	 * �Ƿ���δ�༭��
	 * 
	 * @return boolean
	 */
	private boolean isNewRow() {
		Boolean flag = false;
		TParm parmBuff = tableFormula.getDataStore().getBuffer(
				tableFormula.getDataStore().PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			flag = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
		} else {
			flag = true;
		}
		return flag;
	}
	
    /**
     * ��TABLE�����༭�ؼ�ʱ
     *
     * @param com
     * @param row
     * @param column
     */
    public void onTableFormulaInput(Component com, int row, int column) {
        if (column != 1) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // ���õ����˵�
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\nss\\NSSENFormulaPop.x"), new TParm());
        // ������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        // ��ǰѡ�е�����
        int selectedRow = tableFormula.getSelectedRow();
        tableFormula.acceptText();
        
        // �䷽����
        String fomulaCode = parm.getValue("FORMULA_CODE");
        // �䷽��������
        String fomulaChnDesc = parm.getValue("FORMULA_CHN_DESC");
        // �䷽����
        double mediQty = parm.getDouble("MEDI_QTY");
        // �䷽��λ
        String mediUnit = parm.getValue("MEDI_UNIT");
        
        // �䷽����
        if (StringUtils.isNotEmpty(fomulaCode)) {
			tableFormula.getDataStore().setItem(selectedRow, "FORMULA_CODE", fomulaCode);
		}
		// �䷽����
		if (StringUtils.isNotEmpty(fomulaChnDesc)) {
			tableFormula.setItem(selectedRow, "FORMULA_CHN_DESC", fomulaChnDesc);
		}
		// �䷽����
		if (mediQty > 0) {
			tableFormula.setItem(selectedRow, "MEDI_QTY", mediQty);
		}
		// �䷽��λ
		if (StringUtils.isNotEmpty(mediUnit)) {
			tableFormula.setItem(selectedRow, "MEDI_UNIT", mediUnit);
		}
		
        tableFormula.setItem(selectedRow, "OPT_USER", Operator.getID());
        tableFormula.setItem(selectedRow, "OPT_DATE", SystemTool.getInstance()
				.getDate());
        tableFormula.setItem(selectedRow, "OPT_TERM", Operator.getIP());
        tableFormula.getDataStore().setActive(selectedRow, true);
		
        int count = tableFormula.getDataStore().rowCount();
        // �ж��Ƿ����ظ�����
        for (int i = 0; i < count; i++) {
            if (i == selectedRow) {
                continue;
            }
            if (fomulaCode.equals(tableFormula.getDataStore().getItemData(i, "FORMULA_CODE"))) {
                this.messageBox("�䷽:��" + fomulaChnDesc + "���Ѵ���");
                addRow();
                tableFormula.removeRow(selectedRow);
                return;
            }
        }
        
		// ��������
		this.addRow();
    }
    
	/**
	 * �޸��䷽����ʱ�ĵ����¼�
	 */
	public void onTableFormulaChangeValue(Object obj) {
		// ֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return;
		}
		// �ж����ݸı�
		if (node.getValue().equals(node.getOldValue())) {
			return;
		}
		// Table������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		if ("MEDI_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("��������С�ڻ����0");
				return;
			} else {
				node.getTable().getDataStore().setItem(node.getRow(),
						"MEDI_QTY", qty);
				// �����䷽��ϸ����е�ֵ����Ӫ���ɷ�����
				this.calculateTotalAmountOfNutrients();
			}
		}
	}
	
	/**
	 * �����䷽��ϸ����е�ֵ����Ӫ���ɷ�����
	 */
	private void calculateTotalAmountOfNutrients() {
	    TDataStore dataStore = tableFormula.getDataStore();
	    tableFormula.acceptText();
		TParm parm = new TParm();
		TParm result = new TParm();
		Map<String, Double> dataMap = this.getMapData();
		double qty = 0;
		
    	// ��ʼ��Ӫ���ɷֱ������
    	this.onInitENNutritionTableInfo();

		for (int i = 0; i < dataStore.rowCount(); i++) {
			if (dataStore.getItemDouble(i, "MEDI_QTY") > 0) {
				parm = new TParm();
				parm.setData("MEDI_QTY", dataStore.getItemDouble(i, "MEDI_QTY"));
				parm.setData("FORMULA_CODE", dataStore.getItemString(i,
						"FORMULA_CODE"));

				// �����䷽������ѯ������䷽�µ�Ӫ���ɷֺ���
				result = NSSEnteralNutritionTool.getInstance()
						.queryNutritionContentQty(parm);

				if (result.getErrCode() < 0) {
					this.messageBox("����Ӫ���ɷֺ�������");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
				}

				for (int k = 0; k < result.getCount(); k++) {
					qty = dataMap.get(result.getValue("NUTRITION_CHN_DESC", k));
					qty = qty + result.getDouble("CONTENT_QTY", k);
					dataMap.put(result.getValue("NUTRITION_CHN_DESC", k), qty);
				}
			}
		}

		this.setNutritionData(dataMap);
	}
	
	/**
	 * �趨Ӫ���ɷֺ�����ռ��
	 */
	private void setNutritionData(Map<String, Double> dataMap) {
		tableNutrition.setItem(0, 1, this.countData(dataMap, 0, 1, "����", "kcal"));
		tableNutrition.setItem(1, 1, this.countData(dataMap, 1, 1, "������", "g"));
		tableNutrition.setItem(2, 1, this.countData(dataMap, 2, 1, "֬��", "g"));
		tableNutrition.setItem(3, 1, this.countData(dataMap, 3, 1, "̼ˮ������", "g"));
		
		tableNutrition.setItem(0, 4, this.countData(dataMap, 0, 4, "��", "g"));
		tableNutrition.setItem(1, 4, this.countData(dataMap, 1, 4, "��", "g"));
		tableNutrition.setItem(2, 4, this.countData(dataMap, 2, 4, "��ʳ��ά", "g"));
		tableNutrition.setItem(3, 4, this.countData(dataMap, 3, 4, "��", "g"));
		tableNutrition.setItem(4, 4, this.countData(dataMap, 4, 4, "VC", "g"));
		
		DecimalFormat df = new DecimalFormat("0.00");
		// �����ʺ���
		double proteinQty = TypeTool.getDouble(tableNutrition.getItemString(1, 1)
				.replaceAll("g", ""));
		// ֬������
		double fatQty = TypeTool.getDouble(tableNutrition.getItemString(2, 1)
				.replaceAll("g", ""));
		double NQty = proteinQty / 6.25;
		// ����=������/6.25
		tableNutrition.setItem(4, 1, df.format(NQty));
		
		// ������
		double energyQty = TypeTool.getDouble(tableNutrition.getItemString(0, 1)
				.replaceAll("kcal", ""));
		
		if (energyQty > 0) {
			// ���ף�=������*4/����*100
			tableNutrition.setItem(1, 2, df.format(proteinQty * 4 / energyQty * 100) + "%");
			// ֬����=֬��*9/����*100
			tableNutrition.setItem(2, 2, df.format(fatQty * 9 / energyQty * 100) + "%");
			// ̼ˮ�����=100%-���ף�-֬����
			tableNutrition.setItem(3, 2, df.format((1 - (proteinQty*4 + fatQty*9)/energyQty)*100) + "%");
		}
		
		if (proteinQty > 0) {
			// E/N=������/����
			tableNutrition.setItem(4, 2, df.format(energyQty / NQty));
		}
	}
	
	/**
	 * ���ݹ�ʽ���㺬��
	 */
	private String countData(Map<String, Double> map,int row, int col, String key, String unit) {
		double data = map.get(key);
		if (StringUtils.equals("��", key)) {
			data = data/1000*74.5/39;
		} else if (StringUtils.equals("��", key)) {
			data = data/1000*58.5/23;
		} else if (StringUtils.equals("��", key)) {
			data = data/1000;
		} else if (StringUtils.equals("VC", key)) {
			data = data/1000;
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(data) + unit;
	}
	
	/**
	 * ����Ӫ���ɷ��ֵ乹��Map����
	 */
	private Map<String, Double> getMapData() {
		Map<String, Double> dataMap = new HashMap<String, Double>();
		// ��ѯȫ��Ӫ���ɷ��ֵ�����
		TParm result = NSSEnteralNutritionTool.getInstance().queryNSSNutrition();

		if (result.getErrCode() < 0) {
			this.messageBox("��ѯӪ���ɷ��ֵ����ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return dataMap;
		}

		if (result.getCount() <= 0) {
			this.messageBox("����Ӫ���ɷ��ֵ�����");
			return dataMap;
		}

		// ��������Ӫ���ɷ�����������ΪKeyֵ��Map
		for (int i = 0; i < result.getCount(); i++) {
			if (!dataMap.containsKey(result.getValue("NUTRITION_CHN_DESC", i))) {
				dataMap.put(result.getValue("NUTRITION_CHN_DESC", i),
						new Double(0));
			}
		}
		
		return dataMap;
	}
    
	/**
	 * ��Ӷ�Ӫ��ʦҽ��ͣ���л��ļ����¼�
	 */
	public void onChangeDieOrderStatus() {
		// �ؼ����ý����л�
		if (getRadioButton("DIE_ORDER_STATUS_Y").isSelected()) {
			this.switchControlEnable(true);
		} else {
			this.switchControlEnable(false);
		}
		
    	// ��ʼ��Ӫ��ʦҽ������
    	this.onInitENDietitionOrderInfo();
    	// ��ʼ��Ӫ���ɷֱ������
    	this.onInitENNutritionTableInfo();
		int selectedRow = tableDrOrder.getSelectedRow();
		this.onTableDrOrderClicked(selectedRow);
	}
	
	/**
	 * ͣ�ð�ť����¼�
	 */
	public void onDCButtonClick() {
		if (tableDietitionOrder == null
				|| tableDietitionOrder.getParmValue() == null) {
			this.messageBox("����ѡ����Ҫͣ�õ�Ӫ��ʦҽ��");
			return;
		}
		
		int selectedRow = tableDietitionOrder.getSelectedRow();
		if (selectedRow > -1) {
			TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
			data.setData("DC_DR_CODE", Operator.getID());
			data.setData("OPT_USER", Operator.getID());
			data.setData("OPT_TERM", Operator.getIP());
			
			// ͣ��ѡ�е�Ӫ��ʦҽ��
			TParm result = NSSEnteralNutritionTool.getInstance()
					.updateDCENOrderM(data);
			
			if (result.getErrCode() < 0) {
    			this.messageBox("E0001");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
    		} else {
    			this.messageBox("P0005");
    			
    			this.onTableDrOrderClicked(tableDrOrder.getSelectedRow());
    		}
		} else {
			this.messageBox("����ѡ����Ҫͣ�õ�Ӫ��ʦҽ��");
			return;
		}
	}
	
	/**
	 * �ؼ����������л�����
	 */
	public void switchControlEnable(boolean flg) {
		this.callFunction("UI|DC_BUTTON|setEnabled", flg);
		this.callFunction("UI|MEDI_QTY|setEnabled", flg);
		this.callFunction("UI|MEDI_UNIT|setEnabled", flg);
		this.callFunction("UI|FREQ_CODE|setEnabled", flg);
		this.callFunction("UI|TOTAL_QTY|setEnabled", flg);
		this.callFunction("UI|TOTAL_UNIT|setEnabled", flg);
		this.callFunction("UI|CONTAINER_CODE|setEnabled", flg);
	}
	
	/**
	 * ��������
	 * 
	 * @return ����
	 */
	private int getTakeDays(TParm parm) {
		int takeDays = 0;
		// ����Ƶ�δ����ѯһ���ִ�д���
		TParm result = NSSEnteralNutritionTool.getInstance().queryPhaFreq(
				this.getValueString("FREQ_CODE"));
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯƵ���ֵ����ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return takeDays;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("����Ƶ���ֵ�����");
			return takeDays;
		}
		
		// ʹ������=����/(����*һ��Ĵ���)
		takeDays = (int) Math.ceil(parm.getDouble("TOTAL_QTY")
				/ (parm.getDouble("MEDI_QTY") * result.getDouble("FREQ_TIMES", 0)));
		
		return takeDays;
	}
	
	/**
	 * ����Ӧ��ӡ��ǩ����
	 * 
	 * @return ��ǩ����
	 */
	private TParm getLabelQty(TParm parm) {
		int labelQty = 0;
		TParm labelParm = new TParm();
		
		// ѡ�е�סԺҽʦҽ��
		TParm saveParm = tableDrOrder.getParmValue().getRow(
				tableDrOrder.getSelectedRow());
		// ��ѯ��ʳ�����ֵ�
		TParm categoryResult = NSSEnteralNutritionTool.getInstance()
				.queryENCategory(saveParm.getValue("ORDER_CODE"));
		
    	if (categoryResult.getErrCode() < 0) {
    		categoryResult.setErr(-1, "��ѯ��ʳ�����ֵ����");
			return categoryResult;
    	}
    	
    	if (categoryResult.getCount() <= 0) {
    		categoryResult.setErr(-1, "������ʳ�����ֵ�����");
    		return categoryResult;
    	}
		
		// ����ѡ���������ѯ��Ӧ�ĵ�λ
		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENContainer(this.getValueString("CONTAINER_CODE"));
		
    	if (result.getErrCode() < 0) {
    		result.setErr(-1, "��ѯ�����ֵ����");
			return result;
    	}
    	
    	if (result.getCount() <= 0) {
    		result.setErr(-1, "���������ֵ�����");
			return result;
    	}
    	
		// ��������ӡ
		if (categoryResult.getBoolean("TOTAL_PRINT_FLG", 0)) {
			labelQty = 1;
		} else if (categoryResult.getBoolean("FREQ_PRINT_FLG", 0)) {
			// ��ǩ���� = ����/���� * ȡ����(����/��������)
			labelQty = ((int) Math.ceil(parm.getDouble("TOTAL_QTY")
					/ parm.getDouble("MEDI_QTY")))
					* ((int) Math.ceil(parm.getDouble("MEDI_QTY")
							/ result.getDouble("CAPACITY", 0)));
		} else {
			labelParm.setErr(-1, "����ʳ����δ�趨��ӡ����");
    		return labelParm;
		}
    	
		labelParm.setData("LABEL_QTY", labelQty);
		
		return labelParm;
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
	 * ��ѯָ������������Ժ������ж����䷽��¼
	 */
	public void onQueryOrderHistory() {
		if (tableDietitionOrder.getSelectedRow() < 0) {
			this.messageBox("��ѡ��һ��Ӫ��ʦҽ��");
			return;
		}
		
		int selectedRow = tableDietitionOrder.getSelectedRow();
		TParm data = tableDietitionOrder.getParmValue().getRow(selectedRow);
		
		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", data.getValue("CASE_NO"));
		queryParm.setData("EN_ORDER_NO", data.getValue("EN_ORDER_NO"));
		Object result = openDialog(
				"%ROOT%\\config\\nss\\NSSEnteralNutritionOrderHistory.x",
				queryParm);
		if (result != null) {
			if (result instanceof TParm) {
				TParm parm = (TParm) result;
				int count = parm.getCount();
				// ��ǰ�䷽��ϸ���е�������������
				int seq = tableFormula.getDataStore().rowCount();
				if (seq > 1) {
					// ����������䷽���ݣ��������ٵ�����ʷ��¼
					this.messageBox("��������䷽����������");
					return;
				}
				
				tableFormula.acceptText();
				for (int i = 0; i < count; i++) {
					if (parm.getBoolean("FLG", i)) {
						tableFormula.setSelectedRow(seq-1);
						this.popReturn("", parm.getRow(i));
						seq = seq + 1;
					}
				}
				
				// �����䷽��ϸ����е�ֵ����Ӫ���ɷ�����
				this.calculateTotalAmountOfNutrients();
			} else {
				this.messageBox("�������ش���");
				return;
			}
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
}
