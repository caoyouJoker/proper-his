package com.javahis.ui.ind;

import java.sql.Timestamp;

import jdo.ind.INDAutoMedLackTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;



import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>
 * Title: ���ұ�ҩ������ȱ�ⲹ��
 * </p>
 * 
 * <p>
 * Description: ���ұ�ҩ������ȱ�ⲹ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wukai 2016.12.06
 * @version 1.0
 */
public class INDAutoMedLackControl extends TControl {

	private String action = "insert";

	private TTable table;

	private String u_type = "1";

	public INDAutoMedLackControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table = (TTable) this.getComponent("TABLE");
		onClear();
	}

	public void onQuery() {
		// TODO Auto-generated method stub
		TParm parm = new TParm();
		String batch_date = this.getValueString("BATCH_DATE");
		if (StringUtils.isEmpty(batch_date)) {
			this.messageBox("�����������");
			return;
		}
		parm.setData(
				"BATCH_DATE",
				batch_date.replaceAll("/", "").replaceAll("-", "")
						.substring(0, 6));
		
		String org_code = this.getValueString("ORG_CODE");
		if(!StringUtils.isEmpty(org_code)) {
			parm.setData("ORG_CODE", org_code);
		}
		
		//��ӵ�ǰ�û���������
		String dept = Operator.getDept();
		if(!"040101".equals(dept)) {
			//ҩ�ⲻ�����������
			parm.setData("USER_DEPT", dept);
		}
		
		if (getTRadioButton("STATUS_0").isSelected()) {
			//δ���
			parm.setData("STATUS", "0");
			parm = INDAutoMedLackTool.getInstance().selectForDispense(parm);
		} else if (getTRadioButton("STATUS_1").isSelected()) {
			parm.setData("STATUS", "1");
			//�����
			parm = INDAutoMedLackTool.getInstance().selectForDispenseB(parm);
		}
		
		if (parm == null || parm.getErrCode() < 0 || parm.getCount() <= 0) {
			this.messageBox("�޲�ѯ����");
			table.setParmValue(new TParm());
			return;
		}
		setTableParm(parm);
	}
	
	public void onStatus() {
		if (getTRadioButton("STATUS_0").isSelected()) { 
			//this.getComponent("")
			( (TMenuItem) getComponent("save")).setEnabled(true);
			
		} else {
			( (TMenuItem) getComponent("save")).setEnabled(false);
		}
		
	}
	
	/**
	 * ����Tableֵ ��Ҫ�Ǹ���order_code��org_code��ѯ��IND_STOCK��ѯ�����
	 * 
	 * @param result
	 */
	private void setTableParm(TParm result) {
		String flg = this.getTCheckBox("CHECK_ALL").isSelected() ? "Y" : "N";
		String out_org_code = "";
		String order_code = "";
		double stock_price = 0;
		double retail_price = 0;
		double qty = 0;
		double atm = 0;
		for (int i = 0; i < result.getCount(); i++) {
			out_org_code = result.getValue("ORG_CODE", i);
			order_code = result.getValue("ORDER_CODE", i);
			result.setData("SELECT_FLG", i, flg);
			if ("0".equals(u_type)) {
				result.setData(
						"STOCK_QTY",
						i,
						INDAutoMedLackTool.getInstance().selectStockQty(out_org_code,
								order_code)
								/ result.getDouble("DOSAGE_QTY", i));
			} else {
				result.setData("STOCK_QTY", i, INDAutoMedLackTool.getInstance().selectStockQty(out_org_code, order_code));
				
			}
			stock_price = result.getDouble("STOCK_PRICE", i);
			retail_price = result.getDouble("RETAIL_PRICE", i);
			qty = result.getDouble("ACTUAL_QTY", i) - result.getDouble("QTY");
			atm = StringTool.round(stock_price * qty, 2);
			result.setData("STOCK_ATM", i, atm);
			atm = StringTool.round(retail_price * qty, 2);
			result.setData("RETAIL_ATM", i, atm);
			atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
			result.setData("DIFF_ATM", i, atm);
		}
//		System.out.println("setTableParm :::: " + result);
		table.setParmValue(result);
	}

	public void onCheckAll() {
		table.acceptText();
		if (this.getTCheckBox("CHECK_ALL").isSelected()) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "SELECT_FLG", true);
			}

		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "SELECT_FLG", false);
			}
		}
	}

	/**
	 * ������г��ⲹ������
	 */
	public void onSave() {
		// ��Ҫ���������
		table.acceptText();
		if(table.getRowCount() <= 0) {
			this.messageBox("��ɸѡ�����ݲ�ѡ����ڽ��б���");
			return;
		}
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		TParm seletedParm = new TParm();
		TParm tableParm = table.getParmValue();
		int count = 0;
		double actual_qty = 0;  //��������
		double qty = 0;   //�ۼƳ�����
		double out_qty = 0; //���γ�����
		double stock_qty = 0; //�����
		double validStockQty = 0;  //У��Ч�ں�Ŀ����
		boolean flg = true;
		for (int i = 0; i < table.getRowCount(); i++) {
			if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			stock_qty = table.getItemDouble(i, "STOCK_QTY");
			out_qty = table.getItemDouble(i, "OUT_QTY");
			if(stock_qty < out_qty) {
				this.messageBox(table.getItemString(i, "ORDER_DESC") + "��������㣬�޷����ⲹ��");
				table.acceptText();
				table.setItem(i, "SELECT_FLG", false);
				flg = false;
				break;
			}
			validStockQty = INDAutoMedLackTool.getInstance().selectStockQtyVaildDate(tableParm.getValue("ORG_CODE", i) + "", tableParm.getValue("ORDER_CODE", i)); 
			if(validStockQty < out_qty) {
				this.messageBox(table.getItemString(i, "ORDER_DESC") + "��沿��Ч�ڹ��ڣ��޷����ⲹ��");
				table.acceptText();
				table.setItem(i, "SELECT_FLG", false);
				flg = false;
				break;
			}
			
			actual_qty =  table.getItemDouble(i, "ACTUAL_QTY");
			qty = tableParm.getDouble("QTY", i);
			seletedParm.setData("OLD_QTY", count, qty);
			qty += out_qty;
			seletedParm.setData("ACTUAL_QTY", count, actual_qty);
			seletedParm.setData("OUT_QTY", count, out_qty);
			seletedParm.setData("QTY", count, qty);
			if(qty >= actual_qty) {
				//��ɳ���
				seletedParm.setData("STATUS",count, "1");
			} else {
				//δ��ɳ���
				seletedParm.setData("STATUS",count, "0");
			}
			seletedParm.setData("BATCH_DATE",count, tableParm.getData("BATCH_DATE", i));
			seletedParm.setData("DISPENSE_NO", count, table.getItemString(i, "DISPENSE_NO"));
			seletedParm.setData("ORG_CODE", count, tableParm.getData("ORG_CODE", i));
			seletedParm.setData("ORDER_CODE", count, tableParm.getData("ORDER_CODE", i));
			seletedParm.setData("SEQ_NO", count, tableParm.getData("SEQ_NO", i));
			seletedParm.setData("PHA_TYPE", count, tableParm.getData("PHA_TYPE", i));
			seletedParm.setData("UNIT_TYPE", count, u_type);
			seletedParm.setData("OPT_USER", count, Operator.getID());
			seletedParm.setData("OPT_DATE", count, date);
			seletedParm.setData("OPT_TERM", count, Operator.getIP());
			seletedParm.setData("UNIT_CODE", count, tableParm.getData("UNIT_CODE", i));
			// �Ƿ��¼�
			seletedParm.setData("IS_BOXED", count, "Y");
			seletedParm.setData("BOXED_USER", count, "");
			seletedParm.setData("BOX_ESL_ID", count, "");
			
			//���ӱ�ǩ
			seletedParm.setData("ELETAG_CODE", count, "");
			
			//Ч��
			seletedParm.setData("VALID_DATE", count, tnull);
			count ++;
		}
		if(!flg) {
			return;
		}
		if(seletedParm.getCount("DISPENSE_NO") <= 0) {
			this.messageBox("��ѡ��һ���������Ҫ���ⲹ��������");
			return;
		}
		
		//���п�油��
//		System.out.println("seletedParm :::::: " + seletedParm);
		seletedParm = TIOM_AppServer.executeAction("action.ind.INDAutoMedLackAction",
                "onSaveIndDispenseAutoMed", seletedParm);
		if(seletedParm == null || seletedParm.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			table.setParmValue(new TParm());
			return;
		}
		this.messageBox("����ɹ�");
		onQuery();
	}

	public void onClear() {
		this.getTRadioButton("STATUS_0").setSelected(true);
		table.setParmValue(new TParm());
		Timestamp date = SystemTool.getInstance().getDate();
		this.clearValue("ORG_CODE");
		this.setValue("BATCH_DATE",
				date.toString().substring(0, 7).replace('-', '/'));
		if (getTRadioButton("STATUS_0").isSelected()) { 
			//this.getComponent("")
			( (TMenuItem) getComponent("save")).setEnabled(true);
			
		} else {
			( (TMenuItem) getComponent("save")).setEnabled(false);
		}
	}

	/**
	 * ��ȡRadioButton
	 * 
	 * @param tag
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	private TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

}
