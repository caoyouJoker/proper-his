package com.javahis.ui.nss;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMInpTool;
import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ����Ӫ��ȷ��</p>
 *
 * <p>Description: ����Ӫ��ȷ��</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.25
 * @version 1.0
 */
public class NSSEnteralNutritionConfirmControl extends TControl {
    public NSSEnteralNutritionConfirmControl() {
        super();
    }

    private TTable tableOrderM; // Ӫ��ʦҽ������
    private TTable tableDSPNM; // Ӫ��ʦҽ��չ������
    private String unfoldEndDate; // չ����ֹʱ��

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
		tableOrderM = getTable("TABLE_ORDERM");
		tableDSPNM = getTable("TABLE_DSPNM");
		
		clearValue("DR_DIET;ENC_DEPT_CODE;ENC_STATION_CODE;MR_NO;SELECT_ALL");
    	// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// �趨Ĭ��չ������
    	this.setValue("CONFIRM_DATE_S", todayDate);
    	this.setValue("CONFIRM_DATE_E", todayDate);
    	// �趨Ĭ�ϲ�ѯ����
    	this.setValue("QUERY_DATE_S", todayDate);
    	this.setValue("QUERY_DATE_E", todayDate);
    	// ���չ����ֹʱ��
    	unfoldEndDate = todayDate;
	}
	
    /**
     * ��ѯ����Ӫ��ʦҽ������
     */
    public void onQuery() {
    	TParm queryParm = new TParm();
    	queryParm.setData("ORDER_CODE", this.getValueString("DR_DIET"));
    	queryParm.setData("DEPT_CODE", this.getValueString("ENC_DEPT_CODE"));
    	queryParm.setData("STATION_CODE", this.getValueString("ENC_STATION_CODE"));
    	queryParm.setData("MR_NO", this.getValueString("MR_NO"));
    	// ֻ��ѯʹ���е�Ӫ��ʦҽ��
    	queryParm.setData("DIE_ORDER_STATUS", "Y");
    	// ֻ��ѯʹ���е�סԺҽʦҽ��
    	queryParm.setData("DR_ORDER_STATUS", "Y");
		// ҽ������
		if (getRadioButton("RX_KIND_UD").isSelected()) {
			queryParm.setData("RX_KIND", "UD");
			callFunction("UI|CONFIRM_DATE_E|setEnabled", true);
		} else {
			queryParm.setData("RX_KIND", "ST");
			// ȡ�õ�ǰ����
			String todayDate = SystemTool.getInstance().getDate().toString()
					.substring(0, 10).replace('-', '/');
	    	this.setValue("CONFIRM_DATE_E", todayDate);
	    	// ���չ����ֹʱ��
	    	unfoldEndDate = todayDate;
			callFunction("UI|CONFIRM_DATE_E|setEnabled", false);
		}
		queryParm.setData("NOW_DATE", SystemTool.getInstance().getDate()
				.toString().substring(0, 10).replaceAll("-", ""));
    	
    	// ��ѯӪ��ʦҽ����������
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderM(
				queryParm);
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯӪ��ʦҽ���������");
    		tableOrderM.setParmValue(new TParm());
    		tableDSPNM.setParmValue(new TParm());
    		clearValue("SELECT_ALL");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("����Ӫ��ʦҽ����������");
    		tableOrderM.setParmValue(new TParm());
    		tableDSPNM.setParmValue(new TParm());
    		clearValue("SELECT_ALL");
    		return;
    	} else {
    		tableOrderM.setParmValue(result);
    	}
    	
    	// ��չ�ѡע��
    	clearValue("SELECT_ALL");
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
     * ��ѯ��ȷ��չ����ҽ������
     */
    public void onQueryConfirmedData() {
    	TParm parm = new TParm();
    	
    	if (StringUtils.isEmpty(this.getValueString("QUERY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("QUERY_DATE_E"))) {
    		tableDSPNM.setParmValue(new TParm());
    		this.messageBox("�������ѯʱ��");
    		return;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace('-', '/'));
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace('-', '/'));
		if (StringUtils.isNotEmpty(this.getValueString("DR_DIET").trim())) {
			parm.setData("ORDER_CODE", this.getValueString("DR_DIET"));
		}
		if (StringUtils.isNotEmpty(this.getValueString("ENC_DEPT_CODE").trim())) {
			parm.setData("DEPT_CODE", this.getValueString("ENC_DEPT_CODE"));
		}
		if (StringUtils.isNotEmpty(this.getValueString("ENC_STATION_CODE").trim())) {
			parm.setData("STATION_CODE", this.getValueString("ENC_STATION_CODE"));
		}
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}

		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENDspnM(parm);
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯӪ��ʦҽ��չ���������");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	if (result.getCount() <= 0) {
    		this.messageBox("����Ӫ��ʦҽ��չ����������");
    		tableDSPNM.setParmValue(new TParm());
    		return;
    	} else {
    		tableDSPNM.setParmValue(result);
    	}
    	
    	for (int i = 0; i < tableDSPNM.getRowCount(); i++) {
			// ��ȡ���������б�����ɫ����Ϊ��ɫ
			if (tableDSPNM.getParmValue().getBoolean("CANCEL_FLG", i)) {
				tableDSPNM.setRowColor(i, new Color(255, 255, 0));
			} else {
				// ������ԭɫ
				tableDSPNM.removeRowColor(i);
			}
		}
    }
    
    /**
     * 	��ʳҽ���ı��¼�
     */
    public void onDietChange() {
    	// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
    	String drDietCode = this.getValueString("DR_DIET");
    	
    	// �л���ʳҽ����ʱ�����������
    	tableOrderM.setParmValue(new TParm());
    	clearValue("SELECT_ALL");
    	
    	// ���û��ѡ����ʳҽ������Ĭ��ֻ��չ������
    	if (StringUtils.isEmpty(drDietCode)) {
    		// �趨Ĭ��չ������
        	this.setValue("CONFIRM_DATE_E", todayDate);
        	// ���չ����ֹʱ��
        	unfoldEndDate = todayDate;
    		return;
    	} else {
    		// ҽ������
			if (getRadioButton("RX_KIND_UD").isSelected()) {
				// ��ѯ��ʳ�����ֵ�
				TParm result = NSSEnteralNutritionTool.getInstance()
						.queryENCategory(drDietCode);

				if (result.getErrCode() < 0) {
					this.messageBox("��ѯ��ʳ�����ֵ����");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return;
				}

				if (result.getCount() <= 0) {
					this.messageBox("������ʳ�����ֵ�����");
					return;
				} else {
					int dayCount = result.getInt("VALID_PERIOD", 0) - 1;
					String dateStr = this.addDay(todayDate, dayCount);
					// ������ʳ����ı��������趨Ĭ��չ������
					this.setValue("CONFIRM_DATE_E", dateStr);
					// ���չ����ֹʱ��
					unfoldEndDate = dateStr;
				}
			}
    	}
    }
    
	/**
	 * ȫѡ��ѡ��ѡ���¼�
	 */
	public void onCheckSelectAll() {
		if (tableOrderM.getRowCount() <= 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < tableOrderM.getRowCount(); i++) {
			tableOrderM.setItem(i, "FLG", flg);
		}
	}
	
	/**
	 * ����
	 */
	public void onSave() {
		// ǿ��ʧȥ�༭����
		if (tableOrderM.getTable().isEditing()) {
			tableOrderM.getTable().getCellEditor().stopCellEditing();
		}
		
		if (tableOrderM.getRowCount() <= 0) {
			this.messageBox("��ѡ��Ҫչ����ҽ������");
			return;
		} else {
			TParm parm = tableOrderM.getParmValue();
			TParm queryParm = new TParm();
			TParm result = new TParm();
			boolean checkFlg = false;
			String userId = Operator.getID();
			String userIp = Operator.getIP();
			// סԺҽ��ͣ��ҽ��List
			List<String> admDcMrNoList = new ArrayList<String>();
			// Ӫ��ʦͣ��ҽ��List
			List<String> dieDcMrNoList = new ArrayList<String>();
			String message = "";
			// �ɹ�չ����������
			int saveCount = 0;
			
			int count = parm.getCount();
			for (int i = count - 1; i > -1 ; i--) {
				if (parm.getBoolean("FLG", i)) {
					checkFlg = true;
				} else {
					parm.removeRow(i);
				}
			}
			
			if (!checkFlg) {
				this.messageBox("��ѡ��Ҫչ����ҽ������");
				this.onQuery();
				return;
			} else {
				// չ�����ڵļ������
				int unfoldDateCount = 0;
				
				// ҽ������
				if (getRadioButton("RX_KIND_UD").isSelected()) {
					// ����չ�����ڵļ������
					unfoldDateCount = StringTool.getInt(DateUtil.getTwoDay(this
							.getValueString("CONFIRM_DATE_E").substring(0, 10)
							.replace('-', '/'), this.getValueString(
							"CONFIRM_DATE_S").substring(0, 10).replace('-', '/')));
				} else {
					unfoldDateCount = 0;
				}
				
				if (unfoldDateCount < 0) {
					this.messageBox("չ����ֹ���ڲ���С�ڿ�ʼ����");
					return;
				}
				
				// �������������ֹ���ں��û���ǰѡ��Ľ�ֹ�����������
				int endDateCount = StringTool.getInt(DateUtil.getTwoDay(this
						.getValueString("CONFIRM_DATE_E").substring(0, 10)
						.replace('-', '/'), unfoldEndDate.substring(0, 10)
						.replace('-', '/')));
				
				if (endDateCount > 0) {
					this.messageBox("չ����ֹ���ڲ��ܴ������չ�����ڣ�"+unfoldEndDate);
					return;
				}
				
				TParm saveParm = parm;
				count = saveParm.getCount();
				// չ������
				String unfoldDate = "";
				// ��������
				String orderDate = "";
				
				for (int i = 0; i <= unfoldDateCount; i++) {
					// ȡ�õ�ǰӦչ������
					unfoldDate = this.addDay(this.getValueString(
							"CONFIRM_DATE_S").substring(0, 10)
							.replace('-', '/'), i);
					
					for (int k = 0; k < count; k++) {
						queryParm = new TParm();
						queryParm.setData("CASE_NO", saveParm.getValue("CASE_NO", k));
						queryParm.setData("EN_ORDER_NO", saveParm.getValue("EN_ORDER_NO", k));
						queryParm.setData("EN_PREPARE_DATE", unfoldDate);
						// δȡ��
						queryParm.setData("CANCEL_FLG", "N");
						
						// ��֤�ñ�����ͬ�������Ƿ����δȡ������ͬ���Ƶ�����
						result = NSSEnteralNutritionTool.getInstance().queryENDspnM(
								queryParm);
						
						if (result.getErrCode() < 0) {
							this.messageBox("��ѯչ�����ݴ���");
							err("ERR:" + result.getErrCode() + result.getErrText()
									+ result.getErrName());
							return;
						}
						
						// ����Ѵ���δȡ���������޳�,�����ظ�����
						if (result.getCount() > 0) {
							// Ӧ��������
							saveParm.setData("EXIST_FLG", k, true);
						} else {
							saveParm.setData("EXIST_FLG", k, false);
						}
						
						queryParm.setData("ORDER_NO", saveParm.getValue("ORDER_NO", k));
						queryParm.setData("ORDER_SEQ", saveParm.getValue("ORDER_SEQ", k));
						// չ��ʱʵʱ��ѯ���ݿ��и�ҽ���Ƿ�ͣ�ã��������û��ˢ��չ����������
						result = NSSEnteralNutritionTool.getInstance().queryOrderInfo(
								queryParm);
						
						if (result.getErrCode() < 0) {
							this.messageBox("��ѯҽ����Ϣ�쳣");
							err(result.getErrCode() + " " + result.getErrText());
							return;
						}
						
						// ͣ��ע��
						saveParm.setData("DC_FLG", k, false);
						// סԺҽ��ͣ��
						if (StringUtils.isNotEmpty(result
								.getValue("DC_DATE", 0))) {
							if (!admDcMrNoList.contains(saveParm.getValue(
									"MR_NO", k))) {
								admDcMrNoList.add(saveParm.getValue("MR_NO", k)
										+ ","
										+ saveParm.getValue("PAT_NAME", k));
							}
							// ͣ��ע�ǲ���չ��
							saveParm.setData("DC_FLG", k, true);
						} else if (StringUtils.isNotEmpty(result.getValue(
								"EN_DC_DATE", 0))) {
							// Ӫ��ʦͣ��
							if (!dieDcMrNoList.contains(saveParm.getValue(
									"MR_NO", k))) {
								dieDcMrNoList.add(saveParm.getValue("MR_NO", k)
										+ ","
										+ saveParm.getValue("PAT_NAME", k));
							}
							// ͣ��ע�ǲ���չ��
							saveParm.setData("DC_FLG", k, true);
						}
						
						// ȷ�����Ƶ���
						saveParm.setData("EN_PREPARE_NO", k, "");
						// Ӧ��������
						saveParm.setData("EN_PREPARE_DATE", k, unfoldDate);
						// �ۼ�ʹ����
						saveParm.setData("TOTAL_ACCU_QTY", k, 0);
						
						orderDate = saveParm.getValue("ORDER_DATE", k);
						if (null != orderDate && orderDate.length() > 19) {
							orderDate = orderDate.substring(0, 19);
						}
						// ��������
						saveParm.setData("ORDER_DATE", k, orderDate);
						// ȷ����Ա
						saveParm.setData("CONFIRM_DR_CODE", k, userId);
						// ����״̬(0_δ���,1_�����)
						saveParm.setData("PREPARE_STATUS", k, "0");
						// ������Ա
						saveParm.setData("PREPARE_DR_CODE", k, "");
						// ����ʱ��
						saveParm.setData("PREPARE_DATE", k, "");
						// �շ�ע��
						saveParm.setData("BILL_FLG", k, "N");
						// ִ��ע��
						saveParm.setData("EXEC_STATUS", k, "0");
						// ȡ��
						saveParm.setData("CANCEL_FLG", k, "N");
						// ������Ա
						saveParm.setData("OPT_USER", k, userId);
						// ����ʱ��
						saveParm.setData("OPT_TERM", k, userIp);
						// ���
						saveParm.setData("SEQ", k, 0);
						// ִ��ע��(Y_��ִ��,N_δִ��)
						saveParm.setData("EXEC_FLG", k, "N");
						// ִ����Ա
						saveParm.setData("EXEC_USER", k, "");
						// ִ��ʱ��
						saveParm.setData("EXEC_DATE", k, "");
						
						if (StringUtils.isEmpty(saveParm.getValue("DC_DATE", k))) {
							// ͣ������
							saveParm.setData("DC_DATE", k, "");
						}
						
						queryParm = new TParm();
						queryParm.setData("CASE_NO", saveParm.getValue("CASE_NO", k));
						// ÿ��չ������ʱ��ADM_INP��ȡ���µĴ��ű���ת���󴲺Ŵ���
						result = ADMInpTool.getInstance().selectall(queryParm);
						
						if (result.getErrCode() < 0) {
							this.messageBox("��ѯ���´�λ�Ŵ���");
							err("ERR:" + result.getErrCode() + result.getErrText()
									+ result.getErrName());
						}
						
						if (result.getCount() > 0) {
							saveParm.setData("BED_NO", k, result.getValue("BED_NO", 0));
						}
					}
					
					// ִ�б������
					result = TIOM_AppServer.executeAction(
							"action.nss.NSSEnteralNutritionAction",
							"onSaveNSSENDspnM", saveParm);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						this.messageBox("E0001");
						return;
					}
					
					// �����ۼ�չ������
					saveCount = saveCount + result.getInt("SAVE_COUNT");
				}

				int admDcCount = admDcMrNoList.size();
				if (admDcCount > 0) {
					message = "";
					for (int m = 0; m < admDcCount; m++) {
						message = message + "�����ţ�"
								+ admDcMrNoList.get(m).split(",")[0] + "��������"
								+ admDcMrNoList.get(m).split(",")[1] + "\n";
					}
					
					message = message + "��סԺҽ��ͣ��ҽ��������δ��չ��";
					this.messageBox(message);
				}
				
				int dieDcCount = dieDcMrNoList.size();
				if (dieDcCount > 0) {
					message = "";
					for (int m = 0; m < dieDcCount; m++) {
						message = message + "�����ţ�"
								+ dieDcMrNoList.get(m).split(",")[0] + "��������"
								+ dieDcMrNoList.get(m).split(",")[1] + "\n";
					}
					
					message = message + "��Ӫ��ʦͣ���䷽������δ��չ��";
					this.messageBox(message);
				}
				
				if (saveCount > 0) {
					this.messageBox("P0001");
				} else if (saveCount == 0 && message.length() == 0) {
					this.messageBox("P0001");
				}
				
				// ˢ�½�������
				this.onQuery();
			}
		}
	}
	
    /**
     * ȡ��չ������
     */
    public void onDelete() {
    	if (tableDSPNM.getSelectedRow() < 0) {
    		this.messageBox("��ѡ����Ҫȡ����չ������");
    		return;
    	}
    	
		TParm queryParm = tableDSPNM.getParmValue().getRow(
				tableDSPNM.getSelectedRow());
		queryParm.setData("EN_PREPARE_DATE", queryParm.getValue("EN_PREPARE_DATE").substring(
				0, 10).replace('-', '/'));
    	// ʹ��ѡ�е������в�ѯ������֤�������Ƿ��Ѿ��������
		TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
		
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯӪ��ʦҽ��չ���������");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
    	}
    	
    	// ����Ѿ�������ƣ�������ȡ��
    	if (StringUtils.equals("1", result.getValue("PREPARE_STATUS", 0))) {
    		this.messageBox("��չ�������Ѿ�������Ʋ���ȡ��");
    		return;
    	} else {
    		queryParm.setData("OPT_USER", Operator.getID());
    		queryParm.setData("OPT_TERM", Operator.getIP());
			result = NSSEnteralNutritionTool.getInstance()
					.updateENDspnMByCancel(queryParm);
			
			if (result.getErrCode() < 0) {
	    		this.messageBox("ȡ��չ��ҽ������");
				err("ERR:" + result.getErrCode() + result.getErrText());
				return;
	    	}
			
			this.messageBox("P0005");
			this.onQueryConfirmedData();
    	}
    }
	
    /**
     * ��շ���
     */
    public void onClear() {
    	// ��ʼ��ҳ��ؼ�����
    	this.onInitPage();
    	tableOrderM.setParmValue(new TParm());
    	tableDSPNM.setParmValue(new TParm());
    }
    
	/**
	 * ����ָ�����ڼ���ָ�������������
	 * 
     * @param date
     *            ����ǰ����
     * @param num
     *            ����������
     * @return ���������(��ʽ:yyyy/MM/dd)
	 */
	private String addDay(String date, int num) {
		return StringTool.getString(DateUtils.addDays(DateUtil.strToDate(date),
				num), "yyyy/MM/dd");
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
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
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
