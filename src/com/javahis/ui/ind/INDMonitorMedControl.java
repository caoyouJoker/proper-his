package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.ind.INDMonitorMedTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:ҩƷ���Ʒ��ά��
 * </p>
 * 
 * <p>
 * Description:ҩƷ���Ʒ��ά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2017-01-22
 * @version JavaHis 1.0
 */
public class INDMonitorMedControl extends TControl {

	private TTable table;

	@Override
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		// ���õ����˵�
		TParm parm = new TParm();
		// parm.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		onClear();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryParm = new TParm();
		if (this.getTRadioButton("STARTUSE").isSelected()) {
			// ����
			queryParm.setData("ENABLE_FLG", "Y");
		} else {
			// ͣ��
			queryParm.setData("ENABLE_FLG", "N");
		}
		String temp = null;
		temp = this.getValueString("ORDER_CODE");
		if (!StringUtils.isEmpty(temp)) {
			queryParm.setData("ORDER_CODE", temp);
		}
		temp = this.getValueString("MONITOR_TYPE");
		if (!StringUtils.isEmpty(temp)) {
			queryParm.setData("MONITOR_TYPE", temp);
		}
		TParm result = INDMonitorMedTool.getNewInstance().selectMonitorMed(
				queryParm);
		if (result.getCount("ORDER_CODE") <= 0) {
			// this.messageBox("��������");
			table.setParmValue(new TParm());
		} else {
			table.setParmValue(result);
		}

	}

	/**
	 * ���ѡ���¼� �����渳ֵ
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = table.getParmValue();
		this.setValue("ORDER_CODE", tableParm.getData("ORDER_CODE", row));
		this.setValue("ORDER_DESC", tableParm.getData("ORDER_DESC", row));
		this.setValue("SPECIFICATION", tableParm.getData("SPECIFICATION", row));
		getTComboBox("MONITOR_TYPE").setValue(
				tableParm.getData("MONITOR_TYPE", row));
		if ("Y".equals(tableParm.getData("ENABLE_FLG", row))) {
			getTRadioButton("STARTUSE").setSelected(true);
			getTRadioButton("STOPUSE").setSelected(false);
		} else {
			getTRadioButton("STOPUSE").setSelected(true);
			getTRadioButton("STARTUSE").setSelected(false);
		}
		this.getTextField("ORDER_CODE").setEnabled(false);

	}

	/**
	 * ������޸�
	 */
	public void onSave() {

		String orderCode = this.getValueString("ORDER_CODE");
		if (StringUtils.isEmpty(orderCode)) {
			this.messageBox("������ҩƷ���룡");
			return;
		}
		TParm saveParm = new TParm();
		saveParm.setData("ORDER_CODE", orderCode);

		String monitorType = this.getValueString("MONITOR_TYPE");
		if (StringUtils.isEmpty(monitorType)) {
			this.messageBox("�����������ͣ�");
			return;
		}
		saveParm.setData("MONITOR_TYPE", monitorType);

		if (this.getTRadioButton("STARTUSE").isSelected()) {
			// ����
			saveParm.setData("ENABLE_FLG", "Y");
		} else {
			// ͣ��
			saveParm.setData("ENABLE_FLG", "N");
		}
		saveParm.setData("ORDER_DESC", this.getValueString("ORDER_DESC"));
		saveParm.setData("SPECIFICATION", this.getValueString("SPECIFICATION"));
		saveParm.setData("OPT_USER", Operator.getID());
		saveParm.setData("OPT_TERM", Operator.getIP());
		saveParm.setData("OPT_DATE", this.formatDate(new Date()));

		int row = table.getSelectedRow();
		if (row >= 0) {
			// ����
			TParm result = null;
			String oldMonitor = table.getParmValue().getData("MONITOR_TYPE",
					row)
					+ "";
			if (!monitorType.equals(oldMonitor)) {
				// moitor����
				result = INDMonitorMedTool.getNewInstance().selectMonitorMed(
						saveParm);
				if (result.getCount("ORDER_CODE") > 0) {
					this.messageBox(this.getValueString("ORDER_DESC")
							+ "��"
							+ this.getValueString("SPECIFICATION")
							+ "���Ѵ��� ["
							+ this.getTComboBox("MONITOR_TYPE")
									.getSelectedName() + "]������");
					return;
				}
			}
			saveParm.setData("OLD_MONITOR_TYPE",
					table.getParmValue().getData("MONITOR_TYPE", row));
			result = INDMonitorMedTool.getNewInstance().updateMonitorMed(
					saveParm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
			} else {
				this.messageBox("���³ɹ�");
			}
		} else {
			// ����
			// �жϴ�ҩƷ�Ƿ���ҽ��ҩƷ
			if ("NVAL".equals(monitorType)) {
				TParm feeParm = SYSFeeTool.getInstance().getFeeAllData(orderCode);
				String date = SystemTool.getInstance().getDate().toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
				TParm ruleParm = getMedInsRule(feeParm.getValue("NHI_CODE_I", 0), feeParm.getValue("NHI_CODE_O", 0), feeParm.getValue("NHI_CODE_E", 0), date );
				boolean flg = true;  //��ʼĬ��Ϊҽ��ҩƷ
				for(int i = 0; i < ruleParm.getCount("SFXMBM"); i++) {
					if("005306".equals(ruleParm.getData("SFXMBM", i))) {
						//��ҽ��ҩƷ
						flg = false;
						break;
					}
				}
				if(flg) {
					// ҽ��ҩƷ ��ʾһ��
					if (this.messageBox("����",
							this.getValueString("ORDER_DESC") + "\n"
									+ "Ϊҽ��ҩƷ��\n ȷ�Ͻ�����뵽��ҽ������б��У�",
							this.YES_NO_OPTION) == this.NO_OPTION) {
						return;
					}
				}
			}
			TParm result = INDMonitorMedTool.getNewInstance().selectMonitorMed(
					saveParm);
			if (result.getCount("ORDER_CODE") > 0) {
				this.messageBox(this.getValueString("ORDER_DESC") + "��"
						+ this.getValueString("SPECIFICATION") + "���Ѵ��� ["
						+ this.getTComboBox("MONITOR_TYPE").getSelectedName()
						+ "]������");
				return;
			}
			result = INDMonitorMedTool.getNewInstance()
					.saveMonitorMed(saveParm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
			} else {
				this.messageBox("�����ɹ�");
			}
		}
		this.getTextField("ORDER_CODE").setEnabled(true);
		this.clearValue("ORDER_CODE;ORDER_DESC;SPECIFICATION");
		onQuery();
	}

	/**
	 * ��ȡҩƷ����ľ����
	 * @param nhicodeI
	 * @param nhicodeO
	 * @param nhicodeE
	 * @param date
	 * @return
	 */
	public TParm getMedInsRule(String nhicodeI, String nhicodeO, String nhicodeE,
			String date) {
		String sql = " SELECT SFXMBM, XMMC, BZJG, "
				+ "CASE MZYYBZ WHEN '1' THEN 'Y' ELSE 'N' END AS MZYYBZ,"
				+ "CASE ETYYBZ WHEN '1' THEN 'Y'  ELSE 'N' END AS ETYYBZ,"
				+ "CASE YKD242 WHEN '1' THEN 'Y' ELSE 'N' END AS YKD242,"
				+ "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, "
				+ "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ"
				+ " FROM INS_RULE" + " WHERE SFXMBM IN ('" + nhicodeI + "','"
				+ nhicodeO + "'," + "'" + nhicodeE + "') " + " AND   TO_DATE('"
				+ date + "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ"
				+ " ORDER BY SFXMBM";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			//this.messageBox("E0116");// û������
			return result;
		}
		return result;
	}

	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		return sdf.format(date);
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������ɾ��");
			return;
		}
		if (this.messageBox("��ʾ", "ȷ��ɾ��?", YES_NO_OPTION) == NO_OPTION) {
			return;
		}
		TParm tableParm = table.getParmValue();
		TParm result = INDMonitorMedTool.getNewInstance().delectMonitorMed(
				tableParm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("ɾ��ʧ��");
		} else {
			this.messageBox("ɾ���ɹ�");
		}
		this.getTextField("ORDER_CODE").setEnabled(true);
		this.clearValue("ORDER_CODE;ORDER_DESC;SPECIFICATION");
		onQuery();
	}

	/**
	 * ���
	 */
	public void onClear() {
		table.setParmValue(new TParm());
		getTRadioButton("STARTUSE").setSelected(true);
		getTRadioButton("STOPUSE").setSelected(false);
		this.getTextField("ORDER_CODE").setEnabled(true);
		this.clearValue("ORDER_CODE;ORDER_DESC;SPECIFICATION;MONITOR_TYPE");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
		String spec = parm.getValue("SPECIFICATION");
		if (!StringUtil.isNullString(spec))
			getTextField("SPECIFICATION").setValue(spec);

	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tag
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * ��ȡTComboBox����
	 * 
	 * @param tag
	 * @return
	 */
	private TComboBox getTComboBox(String tag) {
		return (TComboBox) getComponent(tag);
	}
}
