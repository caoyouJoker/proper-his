package com.javahis.ui.nss;

import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMInpTool;
import jdo.bil.BIL;
import jdo.ibs.IBSOrdermTool;
import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: ����Ӫ��������ϸ</p>
 *
 * <p>Description: ����Ӫ��������ϸ</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.3.20
 * @version 1.0
 */
public class NSSEnteralNutritionFormulaControl extends TControl {
    public NSSEnteralNutritionFormulaControl() {
        super();
    }

    private TTable table;
	private TParm parameterParm; // ҳ�洫�����
	private TParm golbalParm;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		
		Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			}
		}
		
		this.onInitPage();
	}
    
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		table = getTable("TABLE");
		
		if (null != parameterParm) {
			if (StringUtils.equals("N", parameterParm.getValue("COMPLETE_STATUS"))) {
				// ������ɰ�ť
				callFunction("UI|SAVE_BTN|setEnabled", true);
			} else {
				// ������ɰ�ť
				callFunction("UI|SAVE_BTN|setEnabled", false);
			}
			
			parameterParm.setData("PREPARE_STATUS", "0");
			parameterParm.setData("CANCEL_FLG", "N");
			
			golbalParm = NSSEnteralNutritionTool.getInstance().queryENDspnMData(
					parameterParm).getRow(0);
			
			this.setValue("ENF_STATION_CODE", golbalParm.getValue("STATION_CODE"));
			this.setValue("ENF_DEPT_CODE", golbalParm.getValue("DEPT_CODE"));
			this.setValue("MR_NO", golbalParm.getValue("MR_NO"));
			this.setValue("PAT_NAME", parameterParm.getValue("PAT_NAME"));
			this.setValue("BED_NO_DESC", parameterParm.getValue("BED_NO_DESC"));
			this.setValue("TOTAL_QTY", golbalParm.getValue("TOTAL_QTY"));
			this.setValue("TOTAL_UNIT", golbalParm.getValue("TOTAL_UNIT"));
			this.setValue("CONTAINER_CODE", golbalParm.getValue("CONTAINER_CODE"));
			this.setValue("LABEL_QTY", golbalParm.getValue("LABEL_QTY"));
			this.setValue("LABEL_CONTENT", golbalParm.getValue("LABEL_CONTENT"));
			this.setValue("MEDI_UNIT", golbalParm.getValue("MEDI_UNIT"));
			
			TParm result = NSSEnteralNutritionTool.getInstance()
					.queryENOrderD(golbalParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ�䷽��ϸ����");
				err("ERR:" + result.getErrCode() + result.getErrText());
				return;
			}
			
			table.setParmValue(result);
		} else {
			// ������ɰ�ť
			callFunction("UI|SAVE_BTN|setEnabled", false);
		}
	}
	
    /**
     * ���淽��
     */
    public void onSave() {
    	String userId = Operator.getID();
    	String userIp = Operator.getIP();
    	String userDept = Operator.getDept();
    	TParm parm = new TParm();
    	boolean dcFlg = false;
    	String message = "";
    	int selectedNo = 0;
    	
    	// ����ǰ��֤סԺҽʦҽ���Ƿ�ͣ���Լ�Ӫ��ʦҽ���Ƿ�ͣ��
		TParm result = NSSEnteralNutritionTool.getInstance().queryOrderInfo(
				golbalParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯҽ����Ϣ�쳣");
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		if (StringUtils.isNotEmpty(result.getValue("DC_DATE", 0))) {
			message = "��ҽ���ѱ�סԺҽ��ͣ��";
			dcFlg = true;
		} else if (StringUtils.isNotEmpty(result.getValue("EN_DC_DATE", 0))) {
			message = "���䷽�ѱ�Ӫ��ʦͣ��";
			dcFlg = true;
		}
		
		// ������ƹ����з���ҽ��ͣ��
		if (dcFlg) {
			selectedNo = this.messageBox("�Ʒ�ȷ��", message + ",�Ƿ�Ʒѣ�", 1);
			
			// ȡ��
			if (selectedNo == 2) {
				parameterParm.runListener("addListener", result);
				this.closeWindow();
				return;
			}
		}
    	
    	golbalParm.setData("PREPARE_DR_CODE", userId);
    	golbalParm.setData("OPT_USER", userId);
    	golbalParm.setData("OPT_TERM", userIp);
    	golbalParm.setData("EN_PREPARE_DATE", golbalParm.getValue(
				"EN_PREPARE_DATE").substring(0, 10).replace('-', '/'));
    	
    	// �Ʒ�
		if (selectedNo == 0) {

			// ȡ������������
			TParm maxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
					golbalParm.getValue("CASE_NO"));

			// ȡ������������
			int caseNoSeq = 0;
			if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
				caseNoSeq = 1;
			} else {
				caseNoSeq = maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1;
			}
			golbalParm.setData("CASE_NO_SEQ", caseNoSeq);

			// ��ѯ�����䷽�շ���ϸ
			result = NSSEnteralNutritionTool.getInstance().queryNSSENOrderDPay(
					golbalParm);

			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ�����䷽�շ���ϸ�쳣");
				err(result.getErrCode() + " " + result.getErrText());
				return;
			}

			// ����������䷽�к����շ���Ŀ����мƷ�
			if (result.getCount() <= 0) {
				parm.setData("CHARGE_FLG", false);
			} else {
				golbalParm.setData("CHARGE_FLG", true);
				parm.setData("CHARGE_FLG", true);
				int count = result.getCount();
				String orderCode = "";
				TParm feeParm = new TParm();
				TParm inChargeParm = new TParm();
				TParm chargeParm = new TParm();
				double ownPrice = 0.0;
				double nhiPrice = 0.0;
				String chargeHospCode = "";
				String chargeCode = "";
				double dosageQty = 0;
				double ownRate = 0;
				double ownAmt = 0;
				double totAmt = 0;

				String ctz1Code = result.getValue("CTZ1_CODE", 0) == null ? ""
						: result.getValue("CTZ1_CODE", 0);
				String ctz2Code = result.getValue("CTZ2_CODE", 0) == null ? ""
						: result.getValue("CTZ2_CODE", 0);
				String ctz3Code = result.getValue("CTZ3_CODE", 0) == null ? ""
						: result.getValue("CTZ3_CODE", 0);
				TParm selLevelParm = new TParm();
				selLevelParm.setData("CASE_NO", golbalParm.getValue("CASE_NO"));
				TParm selLevel = ADMInpTool.getInstance().selectall(
						selLevelParm);
				String level = selLevel.getValue("SERVICE_LEVEL", 0);

				for (int i = 0; i < count; i++) {
					orderCode = result.getValue("ORDER_CODE", i);
					// ����ҽ�������ѯ������Ϣ
					feeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
					if ("2".equals(level)) {
						ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
					} else if ("3".equals(level)) {
						ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
					} else {
						ownPrice = feeParm.getDouble("OWN_PRICE", 0);
					}

					nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
					chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);

					inChargeParm = new TParm();
					inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
					chargeParm = SYSChargeHospCodeTool.getInstance()
							.selectChargeCode(inChargeParm);
					chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);

					dosageQty = TypeTool.getDouble(result
							.getData("MEDI_QTY", i));
					ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
							orderCode, level);
					if (ownRate < 0) {
						this.messageBox("�Ը���������");
						return;
					}

					ownAmt = ownPrice * dosageQty;
					totAmt = ownAmt * ownRate;

					result.addData("HEXP_CODE", chargeHospCode);
					result.addData("BILL_FLG", "Y");
					result.addData("REXP_CODE", chargeCode);
					result.addData("OWN_PRICE", ownPrice);
					result.addData("NHI_PRICE", nhiPrice);
					result.addData("OWN_AMT", ownAmt);
					result.addData("TOT_AMT", totAmt);
					result.addData("OWN_RATE", ownRate);
					result.addData("CASE_NO_SEQ", caseNoSeq);
					result.addData("SEQ_NO", 1 + i);
					result.addData("EXEC_DEPT_CODE", userDept);
					result.addData("OPT_USER", userId);
					result.addData("OPT_TERM", userIp);
				}

				parm.setData("parmD", result.getData());
			}
		} else if (selectedNo == 1) {
			// ���"��",��ֻ��������״̬�����мƷ�
			parm.setData("CHARGE_FLG", false);
		}
        
        parm.setData("parmM", golbalParm.getData());
        
		// ִ�б������
		result = TIOM_AppServer.executeAction(
				"action.nss.NSSEnteralNutritionAction",
				"onSaveByPrepareComplete", parm);
		
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			err(result.getErrCode() + " " + result.getErrText());
			return;
		}
		
		this.messageBox("P0001");
		
		// add by wangb 2016/3/2 START
		TParm extraFeeParm = new TParm();
		extraFeeParm.setData("CATEGORY_CODE", golbalParm.getValue("ORDER_CODE"));
		extraFeeParm.setData("ACTIVE_FLG", "Y");
		
		// ��ѯ����Ӫ�����ӷ���
		extraFeeParm = NSSEnteralNutritionTool.getInstance().queryENExtraFee(extraFeeParm);
		
		if (extraFeeParm.getErrCode() < 0) {
			this.messageBox("��ѯ����Ӫ�����ӷ��ô���");
			err(extraFeeParm.getErrCode() + " " + extraFeeParm.getErrText());
			return;
		}
		
		TParm ibsOrddParm = new TParm();
		String date = SystemTool.getInstance().getDate().toString().substring(
				0, 10).replace("-", "");
		
		List<String> orderCodeList = new ArrayList<String>();
		for (int i = 0; i < extraFeeParm.getCount(); i++) {
			if (StringUtils
					.equals("0", extraFeeParm.getValue("CHARGE_TYPE", i))) {
				ibsOrddParm = new TParm();
				ibsOrddParm.setData("CASE_NO", golbalParm.getValue("CASE_NO"));
				ibsOrddParm.setData("ORDER_CODE", extraFeeParm.getValue("ORDER_CODE", i));
				ibsOrddParm.setData("BILL_DATE", date);
				// ��ѯ�Ʒ���ϸ
				ibsOrddParm = NSSEnteralNutritionTool.getInstance().queryIbsOrdd(ibsOrddParm);
				
				if (ibsOrddParm.getErrCode() < 0 || ibsOrddParm.getCount() > 0) {
					continue;
				} else {
					orderCodeList.add(extraFeeParm.getValue("ORDER_CODE", i));
				}
			} else {
				orderCodeList.add(extraFeeParm.getValue("ORDER_CODE", i));
			}
		}
		
		String orderCode = "";
		for (int i = 0; i < orderCodeList.size(); i++) {
			orderCode = orderCode + orderCodeList.get(i);
			if (i < orderCodeList.size() - 1) {
				orderCode = orderCode + "','";
			}
		}
		
		if (orderCode.length() > 0) {
			TParm extraFeeForIbsParm = new TParm();
			extraFeeForIbsParm.setData("CASE_NO", golbalParm.getValue("CASE_NO"));
			extraFeeForIbsParm.setData("EN_PREPARE_NO", golbalParm.getValue("EN_PREPARE_NO"));
			extraFeeForIbsParm.setData("ORDER_CODE", orderCode);
			// ��ѯ����Ӫ��Ӧ�ո��ӷѹ����Ʒ�����
			extraFeeForIbsParm = NSSEnteralNutritionTool.getInstance()
					.queryENExtraFeeForIbsOrdd(extraFeeForIbsParm);
			
			if (extraFeeForIbsParm.getErrCode() < 0) {
				this.messageBox("��ѯ����Ӫ��Ӧ�ո��ӷѹ����Ʒ����ݴ���");
				err(extraFeeParm.getErrCode() + " " + extraFeeParm.getErrText());
				return;
			} else if (extraFeeForIbsParm.getCount() > 0) {
				// ����Ӫ�����ӷ��üƷ�
				TParm extraFeeResult = this.billENExtraFee(extraFeeForIbsParm);
				
				if (extraFeeResult.getErrCode() < 0) {
					this.messageBox("����Ӫ�����ӷ��üƷ�ʧ��");
					err(extraFeeResult.getErrCode() + " " + extraFeeResult.getErrText());
				}
			}
		}
		// add by wangb 2016/3/2 END
		
		parameterParm.runListener("addListener", result);
		this.closeWindow();
    }
    
    /**
     * ����Ӫ�����ӷ��üƷ�
     * 
     * @param parm
     * @return result
     */
    private TParm billENExtraFee(TParm parm) {
    	TParm result = new TParm();
    	String userId = Operator.getID();
    	String userIp = Operator.getIP();
    	String userDept = Operator.getDept();
    	int count = parm.getCount();
		String orderCode = "";
		TParm feeParm = new TParm();
		TParm inChargeParm = new TParm();
		TParm chargeParm = new TParm();
		double ownPrice = 0.0;
		double nhiPrice = 0.0;
		String chargeHospCode = "";
		String chargeCode = "";
		double ownRate = 0;
		double ownAmt = 0;
		double totAmt = 0;
		
		// ȡ������������
		TParm maxCaseNoSeq = IBSOrdermTool.getInstance().selMaxCaseNoSeq(
				golbalParm.getValue("CASE_NO"));
		// ȡ������������
		int caseNoSeq = 0;
		if (maxCaseNoSeq.getCount("CASE_NO_SEQ") == 0) {
			caseNoSeq = 1;
		} else {
			caseNoSeq = maxCaseNoSeq.getInt("CASE_NO_SEQ", 0) + 1;
		}
		
		String ctz1Code = parm.getValue("CTZ1_CODE", 0) == null ? ""
				: parm.getValue("CTZ1_CODE", 0);
		String ctz2Code = parm.getValue("CTZ2_CODE", 0) == null ? ""
				: parm.getValue("CTZ2_CODE", 0);
		String ctz3Code = parm.getValue("CTZ3_CODE", 0) == null ? ""
				: parm.getValue("CTZ3_CODE", 0);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", golbalParm.getValue("CASE_NO"));
		TParm selLevel = ADMInpTool.getInstance().selectall(
				selLevelParm);
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		
		for (int i = 0; i < count; i++) {
			orderCode = parm.getValue("ORDER_CODE", i);
			// ����ҽ�������ѯ������Ϣ
			feeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
			if ("2".equals(level)) {
				ownPrice = feeParm.getDouble("OWN_PRICE2", 0);
			} else if ("3".equals(level)) {
				ownPrice = feeParm.getDouble("OWN_PRICE3", 0);
			} else {
				ownPrice = feeParm.getDouble("OWN_PRICE", 0);
			}

			nhiPrice = feeParm.getDouble("NHI_PRICE", 0);
			chargeHospCode = feeParm.getValue("CHARGE_HOSP_CODE", 0);

			inChargeParm = new TParm();
			inChargeParm.setData("CHARGE_HOSP_CODE", chargeHospCode);
			chargeParm = SYSChargeHospCodeTool.getInstance()
					.selectChargeCode(inChargeParm);
			chargeCode = chargeParm.getValue("IPD_CHARGE_CODE", 0);
			
			ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,
					orderCode, level);
			if (ownRate < 0) {
				this.messageBox("�Ը���������");
				result.setErr(-1, "�Ը���������");
				return result;
			}

			ownAmt = ownPrice;
			totAmt = ownAmt * ownRate;

			parm.addData("HEXP_CODE", chargeHospCode);
			parm.addData("BILL_FLG", "Y");
			parm.addData("REXP_CODE", chargeCode);
			parm.addData("OWN_PRICE", ownPrice);
			parm.addData("NHI_PRICE", nhiPrice);
			parm.addData("OWN_AMT", ownAmt);
			parm.addData("TOT_AMT", totAmt);
			parm.addData("OWN_RATE", ownRate);
			parm.addData("CASE_NO_SEQ", caseNoSeq);
			parm.addData("SEQ_NO", 1 + i);
			parm.addData("EXEC_DEPT_CODE", userDept);
			parm.addData("OPT_USER", userId);
			parm.addData("OPT_TERM", userIp);
			parm.addData("MEDI_QTY", 1);
			parm.addData("MEDI_UNIT", parm.getValue("UNIT_CODE", i));
			parm.addData("DOSAGE_QTY", 1);
			parm.addData("DOSAGE_UNIT", parm.getValue("UNIT_CODE", i));
		}
		
		// ִ�б������
		result = TIOM_AppServer.executeAction(
				"action.nss.NSSEnteralNutritionAction", "chargeENExtraFee",
				parm);
		
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
    	
    	return result;
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
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
