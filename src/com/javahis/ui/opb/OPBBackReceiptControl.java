package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

import jdo.odo.OpdOrderHistory;
import jdo.opb.OPB;
import jdo.opb.OPBTool;

import com.dongyang.data.TParm;

import jdo.sys.Pat;
import jdo.reg.REGCcbReTool;
import jdo.reg.Reg;
import jdo.opb.OPBReceiptList;

import com.dongyang.jdo.TJDODBTool;

import jdo.opd.OrderTool;
import jdo.opb.OPBReceiptTool;

import com.dongyang.manager.TIOM_AppServer;

import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

import jdo.reg.PatAdmTool;
import jdo.util.Manager;



import com.javahis.ui.testOpb.tools.AssembleTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJReg;

import com.dongyang.util.TypeTool;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * Title: ���������ϸ��ѯ
 * </p>
 * 
 * <p>
 * Description: ���������ϸ��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw 2009-07-08
 * @version 1.0
 */
public class OPBBackReceiptControl extends TControl {
	/**
	 * Ʊ�ݵ�
	 */
	TTable tableM;
	/**
	 * ҽ����
	 */
	TTable tableD;
	/**
	 * �Ƽ۶���
	 */
	OPB opb;
	/**
	 * table����
	 */
	TParm tableMParm;
	/**
	 * ӡˢ��
	 */
	String printNoOnly;
	/**
	 * �˷�Ȩ��
	 */
	boolean backBill = false;
	private String printNo;// Ʊ��
	private Timestamp bill_date;// �շ�����
	// ����
	private TParm batchNoParm;

	private TParm ektParm;// ҽ�ƿ�����
	private TParm parmEKT;// ҽ�ƿ���������
	private String nhiRegionCode;// ҽ���������
	// =====zhangp 20120229
	private int insType = 0;
	TParm sendHL7Parm = new TParm();
	// ====zhangp 20120229
	private boolean cashFLg = false;// �ֽ��˷�
	private TParm regionParm;
	private String type = "";// �������� 1���ֽ� 2��ҽ�ƿ� 3:ҽ����
	private boolean insFlg = false;// �Ƿ����ҽ��
	// private double totAmt = 0.00;// �ܽ��
	private String caseNo;
	private boolean printFlg = false;//��ʧ��ӡ
	private String CONFIRMNO = "";// ҽ����
	private String oldMrNo = "";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		tableM = (TTable) this.getComponent("TABLEM");
		tableD = (TTable) this.getComponent("TABLED");
		// ��������
		dealData();
		batchNoParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT ORDER_CODE, BATCH_NO FROM IND_STOCK"));
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
		
		
		// ��ʼ��Ʊ��  add by huangtt 20150630 
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		
	}
	
	/**
	 * ��ʼ��Ʊ��     add by huangtt 20150630
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// ��˿�����
		if (bilInvoice == null) {
			this.messageBox_("����δ����!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			// this.onClear();
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
		}
		callFunction("UI|UPDATE_NO|setValue", bilInvoice.getUpdateNo());
		return true;
	}

	/**
	 * ��������
	 */
	public void dealData() {
		// ��ʼ��Ȩ��
		if (!initPopedem())
			return;
		// //�����ݷ������
		// if (!getReceiptList())
		// return;
		// �õ��շ��б�
		getReceiptParm();
	}

	/**
	 * ����ǰ̨�������ݺͳ�ʼ��Ȩ��
	 * 
	 * @return boolean
	 */
	public boolean initPopedem() {
		TParm parm;
		// ǰ̨�����ļƼ۶���
		if (this.getParameter() != null) {
			parm = (TParm) this.getParameter();
			// ����opb
			if (!initOpb(parm))
				return false;
		}
		// �˷�Ȩ��
		if (!getPopedem("BACKBILL")) {
			backBill = false;
		}
		return true;
	}

	/**
	 * ����opb
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean initOpb(TParm parm) {
		caseNo = parm.getValue("CASE_NO");
		String mrNo = parm.getValue("MR_NO");
		oldMrNo = parm.getValue("MR_NO_OLD");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("���޴˲�����");
			return false;
		}
		// ���渳ֵ
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		// �жϹҺ���Ϣ
		if (reg == null)
			return false;
		// �����
		callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
		callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
		callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
		// ͨ��reg��caseNo�õ�pat
		opb = OPB.onQueryByCaseNo(reg);
		// �������ϲ��ֵط���ֵ
		if (opb == null) {
			this.messageBox("�˲�����δ����!");
			return false;
		}
		return true;
	}

	/**
	 * ��ʼ���շ��б�
	 * 
	 * @return boolean
	 */
	public boolean getReceiptList() {
		OPBReceiptList opbReceiptList = new OPBReceiptList()
				.initReceiptList(opb.getReg().caseNo());
		if (opbReceiptList == null)
			return false;
		opb.setReceiptList(opbReceiptList);
		return true;
	}

	/**
	 * �õ��շѽ������շ��б�
	 */
	public void getReceiptParm() {
		TParm parm = OPBReceiptTool.getInstance().getReceipt(
				opb.getReg().caseNo());
		// opb.getReceiptList().getParm(opb.getReceiptList().PRIMARY);
		tableM.setParmValue(parm);
		// opb.getReceiptList().initOrder(opb.getPrescriptionList());
	}

	/**
	 * �������¼�
	 */
	public void onClickTableM() {
		tableMParm = new TParm();
		cashFLg = false;
		type = "";
		insFlg = false;
		CONFIRMNO = "";
		int row = tableM.getSelectedRow();
		TParm tableParm = tableM.getParmValue();
		printNo = tableParm.getValue("PRINT_NO", row);// Ʊ��
		bill_date = tableParm.getTimestamp("BILL_DATE", row);// �շ�����
		// System.out.println("��������"+tableParm);
		// //�õ�һ��Ʊ��
		// OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
		// //�õ����е�parm
		// TParm parm = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
		TParm orderParm = new TParm();
		TParm parm = null;
		// String recpType=tableParm.getValue("RECP_TYPE",row);
		// ҽ�ƿ�ҽ����ʾ
		TParm tempParm = new TParm();
		tempParm.setData("PRINT_NO", printNo);
		ektParm = OPBReceiptTool.getInstance().seletEktState(tempParm);// ��ѯ�˴ξ����Ƿ���ҽ�ƿ�����
		// System.out.println("ektParm::"+ektParm);
		orderParm.setData("RECEIPT_NO", tableParm.getData("RECEIPT_NO", row));
		orderParm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
		//=====zhangp 20120925 start
		//=====zhangp 20121217 start
//		if(tableParm.getDouble("PAY_INS_CARD", row) == 0){
//			parm = OrderTool.getInstance().queryFill(orderParm);
//		}else{
			parm = getInsRuleParm(orderParm);
//		}
		//=====zhangp 20121217 end
		//=====zhangp 20120925 end
		// System.out.println("��ϸ������"+parm);
		tableMParm = parm;
		tableD.setParmValue(parm);
		// // У���Ƿ��ѵ�����ѷ�ҩ
		// int count = parm.getCount("EXEC_FLG");
		// String exeFlg = "";
		// for (int i = 0; i < count; i++) {
		// exeFlg = parm.getValue("EXEC_FLG", i);
		// if ("Y".equals(exeFlg)) {
		// returnFeeFlg = true;
		// return;
		// }
		// }
	}

	/**
	 * �˷����
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		int row = tableM.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫ�˷ѵ�Ʊ��!");
		}
		switch (this.messageBox("��ʾ��Ϣ", "�Ƿ��˷�", this.YES_NO_OPTION)) {
		case 0:
			if (!backReceipt(row))
				return false;
			break;
		case 1:
			return true;
		}
		return true;
	}

	/**
	 * �˷ѷ���
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean backReceipt(int row) {
		// if (returnFeeFlg) {
		// this.messageBox("�ѷ�ҩ���ѵ��죬�����˷�!");
		// return false;
		// }
		// if (!opb.onSaveBackReceipt(row)) {
		if (!onSaveBackReceipt(row)) {
			// ����ʧ��
			// messageBox("E0001");
			return false;
		}
		// ����ɹ�
		messageBox("P0001");
		if (cashFLg) {
			sendHL7Mes(sendHL7Parm);
		}
		// ��������³�ʼ��Ʊ��
		afterSave();
		return true;
	}

	/**
	 * ��������³�ʼ��Ʊ��
	 */
	public void afterSave() {
		// //�����ݷ������
		// if (!getReceiptList())
		// return;
		// �õ��շ��б�
		dealData();
		getReceiptParm();
		tableD.removeRowAll();
		// ====zhangp 20120229
		insType = 0;
		// =====zhangp 20120229
		// ��ʼ��Ʊ��
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("OPB"));
	}

	// /**
	// * ��ӡ�����嵥
	// */
	// public void onFill() {
	// if (opb == null)
	// return;
	// System.out.println("tableMParm" + tableMParm);
	// int count = tableMParm.getCount("SETMAIN_FLG");
	// String setmainFlg = "";
	// for (int i = count - 1; i >= 0; i--) {
	// setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);
	// if ("Y".equals(setmainFlg)) {
	// tableMParm.removeRow(i);
	// }
	// }
	// TParm parm = opb.getReceiptList().dealTParm(tableMParm);
	// if (parm == null)
	// return;
	// TParm pringListParm = new TParm();
	// pringListParm.setData("TABLEORDER", parm.getData());
	// //������
	// pringListParm.setData("MR_NO", opb.getPat().getMrNo());
	// //��������
	// pringListParm.setData("PAT_NAME", opb.getPat().getName());
	// //�������
	// pringListParm.setData("CASE_NO", opb.getReg().caseNo());
	// String sql =
	// " SELECT CHN_DESC FROM SYS_DICTIONARY " +
	// "  WHERE GROUP_ID ='SYS_SEX' " +
	// "    AND ID = '" + opb.getPat().getSexCode() + "'";
	// TParm sexParm = new TParm(TJDODBTool.getInstance().select(sql));
	// //�Ա�
	// pringListParm.setData("SEX_CODE", sexParm.getValue("CHN_DESC", 0));
	// //��������
	// pringListParm.setData("ADM_DATE", opb.getReg().getAdmDate());
	// //ҽԺ����
	// pringListParm.setData("HOSP",Operator.getHospitalCHNFullName()+"��������嵥");
	// this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderList.jhw",
	// pringListParm);
	// }
	/**
	 * ��ӡ�����嵥 ============̩��ҽԺ�����嵥��ӡ
	 */
	public void onFill() {
		if(this.messageBox("��ʾ��Ϣ", "�Ƿ��ӡ�����嵥", 2) != 0)//===pangben 2013-9-2 �����ʾ
			return;
		DecimalFormat df = new DecimalFormat("##########0.00");
		if (opb == null)
			return;
		// System.out.println("tableMParm" + tableMParm);
		// ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;
		// DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT
		int count = tableMParm.getCount("SETMAIN_FLG");
		String setmainFlg = "";
		for (int i = count - 1; i >= 0; i--) {
			setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);// ����ҽ��ע��
			if ("Y".equals(setmainFlg)) {
				tableMParm.removeRow(i);
			}
		}
		// System.out.println("batchNoParm:::"+batchNoParm);
		TParm parm = opb.getReceiptList().dealTParm(tableMParm, batchNoParm);
		if (parm == null)
			return;
		double sum = parm.getDouble("SUM");// �ϼƽ��
		TParm pringListParm = new TParm();
		pringListParm.setData("TABLEORDER", parm.getData());
		// ��������
		pringListParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// ��������
		pringListParm
				.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());// ҽԺ����
		pringListParm.setData("TITLE", "TEXT", Operator
				.getHospitalCHNFullName());// ҽԺ����
		pringListParm.setData("BILL_DATE", "TEXT", StringTool.getString(
				TypeTool.getTimestamp(bill_date), "yyyyMMddHHmmss"));// �շ�����
		pringListParm.setData("PRINT_NO", "TEXT", printNo);// Ʊ��
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		pringListParm.setData("DATE", "TEXT", yMd);// ����
		pringListParm.setData("TOTAL", "TEXT", df.format(StringTool.round(sum,
				2)));// ����
		// System.out.println("parm==============" + parm);
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderPrint.jhw",
//				pringListParm);
	      this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBOrderPrint.jhw"),
	                           IReportTool.getInstance().getReportParm("OPBOrderPrint.class", pringListParm));//����ϲ�modify by wanglong 20130730
	}

	/**
	 * ����Ʊ��
	 */
	public void onPrint() {
		int row = tableM.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫ��ӡ������");
			return;
		}
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("Ʊ��������!");
			return;
		}
		if(this.messageBox("��ʾ", "�Ƿ�ִ�в�ӡ����", 2) != 0){//===pangben 2013-9-2 �����ʾ
			return ;
		}
		if (!onSaveRePrint(row)) {
			messageBox("��ӡʧ��!");
			
			return;
		}
//		messageBox("����ɹ�");
		// //�õ�һ��Ʊ��
		// OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
		// if (opbReceipt == null)
		// return;
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		String receiptNo = actionParm.getValue("RECEIPT_NO");
		TParm recpParm = null;
		// �����վݵ�����:ҽ�ƿ��շѴ�Ʊ\�ֽ��շѴ�Ʊ
		recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
		if (ektParm.getCount("CASE_NO") > 0) {
			onPrint(recpParm, true);
		} else {
			onPrint(recpParm, false);
		}
		// ��������³�ʼ��Ʊ��
		afterSave();
		
		
	}
	
	/**
	 * ��ʧ��ӡ
	 */
	public void onLostPrint(){
		printFlg = true;
		int row = tableM.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫ��ӡ������");
			return;
		}
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("Ʊ��������!");
			return;
		}
		if(this.messageBox("��ʾ", "�Ƿ�ִ����ʧ��ӡ����", 2) != 0){//===pangben 2013-9-2 �����ʾ
			return ;
		}
		
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		String receiptNo = actionParm.getValue("RECEIPT_NO");
		TParm recpParm = null;
		// �����վݵ�����:ҽ�ƿ��շѴ�Ʊ\�ֽ��շѴ�Ʊ
		recpParm = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
		if (ektParm.getCount("CASE_NO") > 0) {
			onPrint(recpParm, true);
			
		} else {
			onPrint(recpParm, false);
		
		}
		
		// ��ʼ��Ʊ��
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		
	}
	
	/**
	 * ��ӡƱ�ݷ�װ===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm, boolean flg) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		TParm insOpdInParm = new TParm();
		String confirmNo = "";
		String cardNo = "";
		String insCrowdType = "";
		String insPatType = "";
		// ������Ա������
		String spPatType = "";
		// ������Ա���
		String spcPerson = "";
		double startStandard = 0.00; // �𸶱�׼
		double accountPay = 0.00; // ����ʵ���ʻ�֧��
		double gbNhiPay = 0.00; // ҽ��֧��
		String reimType = ""; // �������
		double gbCashPay = 0.00; // �ֽ�֧��
		double agentAmt = 0.00; // �������
		double unreimAmt = 0.00;// ����δ�������
		double illnesssubsidyamt = 0.00; //����󲡽��
		TParm parm = new TParm();
		double servantAmt = 0.00;
		parm.setData("CASE_NO", opb.getReg().caseNo());
		parm.setData("INV_NO", recpParm.getValue("PRINT_NO", 0));
		parm.setData("RECP_TYPE", "OPB");// �շ�����
		// ��ѯ�Ƿ�ҽ�� �˷�
//		System.out.println("parm="+parm);
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
//		System.out.println("result:::"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// ҽ����Ʊ����
		if (null != result && null != result.getValue("CONFIRM_NO", 0)
				&& result.getValue("CONFIRM_NO", 0).length() > 0) {
			parm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm);
			confirmNo = result.getValue("CONFIRM_NO", 0);
			cardNo = mzConfirmParm.getValue("INSCARD_NO", 0);// ҽ������
			insOpdInParm.setData("CASE_NO", opb.getReg().caseNo());
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// ����δ����
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.��ְ
			// 2.�Ǿ�
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.��ͨ
			// ������Ա������
			String sql = " SELECT SPECIAL_PAT" +
			" FROM INS_MZ_CONFIRM " +
			" WHERE CASE_NO = '"+insOpdInParm.getData("CASE_NO")+"'" +
			" AND CONFIRM_NO ='"+confirmNo+"'";
	        TParm PAT = new TParm(TJDODBTool.getInstance().select(sql));			
	        spPatType = PAT.getValue("SPECIAL_PAT", 0);
			// ������Ա���
			spcPerson = getSpPatDesc(spPatType);
			//�������
			reimType = insOpdParm.getValue("REIM_TYPE", 0);
			
			// ��ְ��ͨ
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
				{	gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				}
				else{
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
					}
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
				
			}
			// ��ְ����
			if (insCrowdType.equals("1") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0)
							;
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// �Ǿ�����
			if (insCrowdType.equals("2") && insPatType.equals("2")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//����󲡽��
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else
					//ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						- insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//����󲡽��
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
				
				//����󲡽��
				illnesssubsidyamt = insOpdParm.getDouble("ILLNESS_SUBSIDY_AMT", 0);
			}
		}
		//��ѯ��Ա���
		String CtzDescSql =" SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
							" WHERE A.CASE_NO = '"+ opb.getReg().caseNo() +"'"+
							" AND A.CTZ1_CODE = B.CTZ_CODE"+
							" AND A.REALDR_CODE =C.USER_ID";		 
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
	
		//��Ա���
		String personClass = CtzDescParm.getValue("CTZ_DESC", 0);
		// Ʊ����Ϣ	
		oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());// ����
		// ������Ա���
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				(personClass+spcPerson).length() == 0 ? "�Է�" : personClass+" "+spcPerson);
		// ��Ա���
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "ְ��ҽ��");
		oneReceiptParm.setData("COPY", "TEXT", "(COPY)");			
		// �������
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			if (opb.getReg().getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			if (opb.getReg().getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		}else {
			oneReceiptParm.setData("ADVANCE_TITLE", "TEXT", "���ҽ���渶�������ҽԺ������");
		}	
		// ҽԺ����
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(opb.getReg().getRegion()));
//		// �𸶽��
//		oneReceiptParm.setData("START_AMT", "TEXT", df.format(startStandard));
		//����δ������ʾ����======pangben 2012-7-12
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "����δ�������:");
		// ����޶����
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "" : df
				.format(unreimAmt));
		//�ѳ��涨ˢ���޶���˵渶���ձ���
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "/�ѳ��涨ˢ���޶���˵渶���ձ���");
		// �����˻�֧��
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));

		// ���úϼ�
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// ������ʾ��д���
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));
		// �ֽ�֧��= ҽ�ƿ����+�ֽ�+��ɫͨ��+֧����
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2)
				+ StringTool.round(recpParm.getDouble("ALIPAY", 0), 2);
		// �ֽ�֧��
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));
		
		// �𸶽��
		if(startStandard !=0 ){
			oneReceiptParm.setData("START_AMT_NAME", "TEXT","�𸶱�׼");
			oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
					startStandard, 2));
		}
		if (agentAmt != 0) {
			if(insCrowdType.equals("2")||(insCrowdType.equals("1")&&reimType.equals("0"))){
				oneReceiptParm.setData("AGENT_NAME", "TEXT", "������");// ҽ�ƾ������
				oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
				}
		}
		if (illnesssubsidyamt != 0) {
			oneReceiptParm.setData("ILLNESS_SUBSIDY_AMT_NAME", "TEXT", "�����֧��");// ����󲡽��
			oneReceiptParm.setData("ILLNESS_SUBSIDY_AMT", "TEXT", df.format(illnesssubsidyamt));
		}
		
		if(insCrowdType.equals("2")||(insCrowdType.equals("1")&&reimType.equals("0"))){
			//����ҽ��֧���ܶ�
			oneReceiptParm.setData("QTYL", "TEXT", (illnesssubsidyamt+agentAmt) == 0? "0.00" : 
				df.format(illnesssubsidyamt+agentAmt));	
		}else{
			oneReceiptParm.setData("QTYL", "TEXT",  "0.00" );
		}
//		oneReceiptParm.setData("MR_NO", "TEXT", opb.getPat().getMrNo());
		oneReceiptParm.setData("MR_NO", "TEXT", oldMrNo);
		// ��ӡ����
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy  MM  dd"));	
		//ҽ��ͳ��֧��
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df
				.format(gbNhiPay));
		if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "��ɫͨ��֧��");
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1", 0), 2));

		}
		// ҽ������
		oneReceiptParm.setData("DR_NAME", "TEXT", CtzDescParm.getValue("USER_NAME", 0));

		// ��ӡ��
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		oneReceiptParm.setData("SEX", "TEXT", opb.getPat().getSexString());//�Ա�
		oneReceiptParm.setData("ID_NO", "TEXT", opb.getPat().getIdNo());//
		
		oneReceiptParm.setData("DETAIL", "TEXT", "(��������嵥)");
		// ��ᱣ�Ϻţ���ʾ���֤�ţ�
		if (cardNo.equals("")) {
		oneReceiptParm.setData("CARD_CODE", "TEXT", "");// �������ҽ��			
		} else {			
			String idNOBefore = cardNo.substring(0, 3);
            String idNOEnd = cardNo.substring( cardNo.length()-3, cardNo.length());
            oneReceiptParm.setData("CARD_CODE", "TEXT", idNOBefore+"************"+idNOEnd);

		}
		//ҽ�ƻ�������
            String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
    	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
    	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
    	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
    	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
    	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE0" + i, 0));
			} else {
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "0.00" : recpParm
						.getData("CHARGE" + i, 0));
			}
		}
		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
				.getDouble("CHARGE01", 0)
				+ recpParm.getDouble("CHARGE02", 0)));
		String caseNo = opb.getReg().caseNo();//
		
		oneReceiptParm.setData("SEX", "TEXT", opb.getPat().getSexString());
		oneReceiptParm.setData("ID_NO", "TEXT", opb.getPat().getIdNo());
		if(!"".equals(result.getValue("CONFIRM_NO", 0))){
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  result.getValue("CONFIRM_NO", 0));
		}else{
			oneReceiptParm.setData("RECEIPT_NO", "TEXT",  recpParm.getValue("RECEIPT_NO", 0));
		}	 
		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", opb.getReg().getAdmType());
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
		System.out.println("oneReceiptParm = = = = = = " + oneReceiptParm);
		//��ʧ��ӡ add by lich ------------start
		if(printFlg){
			String sql = "";
			sql =  "SELECT INV_NO FROM BIL_INVRCP WHERE RECEIPT_NO = '"+ 
				recpParm.getValue("RECEIPT_NO", 0)+"' AND RECP_TYPE = 'OPB' AND CANCEL_FLG = '0'";
			TParm invNoParm = new TParm(TJDODBTool.getInstance().select(sql));
			oneReceiptParm.setData("NO", "TEXT",  invNoParm.getValue("INV_NO", 0));
			
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrintBD.jhw"),
					IReportTool.getInstance().getReportParm("OPBRECTPrintBD.class", oneReceiptParm), false);//����ϲ�modify by wanglong 20130730
			printFlg = false;
		}else{
			this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
					IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//����ϲ�modify by wanglong 20130730
		}
		//��ʧ��ӡ add by lich ------------end
//        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
//                IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//����ϲ�modify by wanglong 20130730
		return;

	}

	/**
	 * �ֽ��Ʊ��ϸ���
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (tableresultparm.getCount() > 6) {
			tableresultparm.setData("DETAIL", "TEXT", "(���������ϸ��)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * ������Ա���
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "�м�����";
		if ("06".equals(type))
			return "����Ա";
		if ("07".equals(type))
			return "��������";
		if ("08".equals(type))
			return "�����Ÿ�";
		if ("09".equals(type))
			return "�ǵ䲹��";
		return "";
	}

	/**
	 * ҽ�ƿ���Ʊ��ϸ���
	 * 
	 * @param oneReceiptParm
	 * @param recpParm
	 * @param dparm
	 */
	private void onPrintEktParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		// if(orderParm.getCount()>10){
		// oneReceiptParm.setData("DETAIL", "TEXT", "(���������ϸ��)");
		// }
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * ҽ�ƿ����ֽ��˷�Ʊ��
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveBackReceipt(int row) {
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER_T", Operator.getID());
		actionParm.setData("OPT_TERM_T", Operator.getIP());
		actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
		actionParm.setData("PRINT_DATE", actionParm.getData("BILL_DATE"));
		actionParm.setData("ADM_TYPE", opb.getReg().getAdmType());	
		

		if (saveParm == null)				
			return false;
		// ����opbaction
		TParm result = null;
		TParm parm = null;
		
		//�����ʷ������ add by huangtt 20150604
//		TParm makeOrderInvalidParm = OPBMakeRecpAndOrderInvalid.getInstance().getMakeOrderInvalid(actionParm.getValue("RECEIPT_NO"));
		  TParm makeOrderInvalidParm = getMakeOrderInvalid(actionParm.getValue("RECEIPT_NO"),actionParm.getValue("CONFIRM_NO"));
		
//		if(!OPBMakeRecpAndOrderInvalid.getInstance().makeOrderInvalid(actionParm.getValue("RECEIPT_NO"))){
//			this.messageBox("д��ʷ�����");
//			return false;
//		}
		
		// ҽ�ƿ�����
		if (actionParm.getDouble("PAY_MEDICAL_CARD")
				+ actionParm.getDouble("PAY_OTHER1")
				+ actionParm.getDouble("ALIPAY") > 0) { //add by huangtt 20160612 ֧����֧����ʽ
			type = "ҽ�ƿ�";
			// ҽ������
			if (!reSetInsSave(actionParm))
				return false;
			if (!insFlg) {
				this.messageBox("Ʊ�ݺ���:"+actionParm.getValue("PRINT_NO")+" "+type + "��Ʊ����,��ִ���˷�");
			}
			result = ektResetFee(actionParm);
			if (null == result) {
				return false;
			}
			// �ֽ����
		} else if (actionParm.getDouble("PAY_CASH") > 0) {
			type = "�ֽ�";
			cashFLg = true;
			isCheckBack(1);
			// ҽ������
			if (!reSetInsSave(actionParm))
				return false;
			if (!insFlg) {
				this.messageBox("Ʊ�ݺ���:"+actionParm.getValue("PRINT_NO")+" "+type + "�˷ѽ��:"
						+ actionParm.getDouble("PAY_CASH"));
			}
			actionParm.setData("INS_FLG", insFlg);
			actionParm.setData("CONFIRM_NO", CONFIRMNO);
			// �ֽ��˷�
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"backOPBRecp", actionParm);
			// ҽ��ȫ����������,ִ���ֽ����
		} else if (actionParm.getDouble("PAY_INS_CARD") > 0
				&& (actionParm.getDouble("PAY_MEDICAL_CARD")
						+ actionParm.getDouble("PAY_OTHER1")+ actionParm.getDouble("ALIPAY")) == 0
				&& actionParm.getDouble("PAY_CASH") == 0 && actionParm.getDouble("PAY_OTHER2") == 0) {
			if (!onExeInsSum(actionParm, result)) {
				return false;							
			}
			result = new TParm();  //add by huangtt 20160622

			//���в���
		}else if(actionParm.getDouble("PAY_OTHER2") >0){
			if(!isCheckBack(2)){
				return false;
			}
			TParm opbParm=getCcbParm(actionParm);
			this.messageBox("opbParm==="+opbParm);
			//���нӿڲ���						
			//TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
			TParm resultData = TIOM_AppServer.executeAction("action.ccb.CCBServerAction",
					"getCcbRe", opbParm);																							
			if (resultData.getErrCode()<0) {
				this.messageBox("���нӿڵ��ó�������,����ϵ��Ϣ����");
				return false;				
			}
			//ҽ������								
			if(!REGCcbReTool.getInstance().reSetInsSave(opbParm,this)){
				return false;
			}
			resultData.setData("OPT_USER",Operator.getID()); 
			resultData.setData("OPT_TERM",Operator.getIP());
			resultData.setData("BUSINESS_TYPE","OPBT");
			result=REGCcbReTool.getInstance().saveEktCcbTrede(resultData);
			if (result.getErrCode() < 0) {
				this.messageBox("��ӽ��׵�ʧ��");
				return false;
			}
			//����Ʊ��
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"backOPBRecp", actionParm);
			
		} else if (actionParm.getDouble("TOT_AMT") == 0) {
			result = ektResetFee(actionParm);
			if (null == result) {
				return false;
			}
		}
		
//		System.out.println("result---"+result);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
//		System.out.println("ɾ��ҽ������-----------------"+opb.getReg().caseNo());
		if (!deleteInsRun(opb.getReg().caseNo())) {
			System.out.println("ɾ����;״̬ʧ�ܣ�" + opb.getReg().caseNo());
		}
//		System.out.println("д��ʷ�����----------------");
		//modiby by huagtt 20150604 start
		if(!OPBMakeRecpAndOrderInvalid.getInstance().makeOrderInvalid(makeOrderInvalidParm)){
			this.messageBox("д��ʷ�����");
			return false;
		}
		//modiby by huagtt 20150604 end
		
		return true;
	}
	/**
	 * ҽ��ȫ�������߼�
	 * @return
	 */
	private boolean onExeInsSum(TParm actionParm,TParm result){
		//��ӽ���
		TParm ektCcbParm=new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='"+opb.getReg().caseNo()+
				"' AND RECEIPT_NO='"+actionParm.getValue("RECEIPT_NO")+"'"));
		if (ektCcbParm.getErrCode()<0) {
			return false;
		}
		if (ektCcbParm.getCount("CASE_NO")>0) {
			if(!isCheckBack(2)){
				return false;
			}
			//���в���
			TParm opbParm=getCcbParm(actionParm);
			if(!REGCcbReTool.getInstance().reSetInsSave(opbParm,this)){
				return false;
			}
			actionParm.setData("CCB_FLG","Y");
		}else{
			type = "ҽ�ƿ�";
			if (!reSetInsSave(actionParm))
				return false;
			// ҽ�ƿ��˷�
			actionParm.setData("INS_UN_FLG", "Y");
			actionParm.setData("INS_FLG", insFlg);
			actionParm.setData("CONFIRM_NO", CONFIRMNO);
		}
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backOPBRecp", actionParm);
		return true;
	}
	/**
	 * �ֽ𡢽��в��� �Ƿ�����˷�
	 * @return
	 * type��1 ���ֽ���� 2.���в���
	 */
	private boolean isCheckBack(int type){
		boolean returnFeeFlg = false;
		// У���Ƿ��ѵ�����ѷ�ҩ
		StringBuffer messagePha = new StringBuffer();
		StringBuffer message = new StringBuffer();
		TParm orderParm = tableD.getParmValue();
		int count = orderParm.getCount("EXEC_FLG");
		String exeFlg = "";
		int HL7Count = 0;
		for (int i = 0; i < count; i++) {
			exeFlg = orderParm.getValue("EXEC_FLG", i);
			if ("Y".equals(exeFlg)) {
				returnFeeFlg = true;
				if (orderParm.getValue("CAT1_TYPE", i).equals("PHA")) {// ҩƷ
					messagePha.append(orderParm.getValue("ORDER_DESC", i)
							+ ",");
				} else {
					if (orderParm.getValue("CAT1_TYPE", i).equals("LIS")
							|| orderParm.getValue("CAT1_TYPE", 0).equals(
									"RIS")) {
						sendHL7Parm.setRowData(HL7Count, orderParm, i);
						HL7Count++;
						if (orderParm.getValue("SETMAIN_FLG", i)
								.equals("Y")) {
							message.append(orderParm.getValue("ORDER_DESC",
									i)
									+ ",");
						}
					}
				}
			}
		}

		if (returnFeeFlg) {
			String sumMessage = "";
			if (messagePha.length() > 0) {
				sumMessage = messagePha.toString().substring(0,
						messagePha.lastIndexOf(","))
						+ " �Ѿ���ҩ\n";
			}
			if (message.length() > 0) {
				sumMessage += message.toString().substring(0,
						message.lastIndexOf(","))
						+ " �Ѿ�����\n";
			}
			switch (type){
			case 1:
				sumMessage += "�ֽ��˷Ѳ�����ע��";
				break;
			case 2:
				sumMessage += "�����Խ��н����˷Ѳ���";
				break;
			}
			
			this.messageBox(sumMessage);
			return false;
		}
		return true;
	}
	/**
	 * ����ҽ���������
	 * @param actionParm
	 * @return
	 */
	private TParm getCcbParm(TParm actionParm){
		TParm opbParm=new TParm();
		opbParm.setData("CASE_NO" ,opb.getReg().caseNo());// �˹�ʹ��
		opbParm.setData("PAT_NAME", opb.getPat().getName());
		opbParm.setData("MR_NO", opb.getPat().getMrNo());// ������
		opbParm.setData("NHI_NO",regionParm.getValue("NHI_NO", 0));
		opbParm.setData("PRINT_NO",actionParm.getValue("PRINT_NO"));
		opbParm.setData("OPT_USER", Operator.getID());// id
		opbParm.setData("OPT_TERM", Operator.getIP());// ip
		opbParm.setData("OPT_NAME", Operator.getName());// ip
		opbParm.setData("CTZ1_CODE", this.getValue("CTZ1_CODE"));
		opbParm.setData("RECEIPT_NO",actionParm.getValue("RECEIPT_NO"));					
		opbParm.setData("AMT", actionParm.getDouble("PAY_OTHER2"));
		return opbParm;
	}
	/**
	 * ����HL7
	 */
	private void sendHL7Mes(TParm sendHL7Parm) {
		/**
		 * ����HL7��Ϣ
		 * 
		 * @param admType
		 *            String �ż�ס��
		 * @param catType
		 *            ҽ�����
		 * @param patName
		 *            ��������
		 * @param caseNo
		 *            String �����
		 * @param applictionNo
		 *            String �����
		 * @param flg
		 *            String ״̬(0,����1,ȡ��)
		 */
		int count = 0;
		if (null != sendHL7Parm && null != sendHL7Parm.getData("ADM_TYPE"))
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		List list = new ArrayList();
		String patName = opb.getPat().getName();
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			String admType = temp.getValue("ADM_TYPE");
			String sql = " SELECT CASE_NO,MED_APPLY_NO,CAT1_TYPE FROM OPD_ORDER "
					+ "  WHERE CASE_NO ='"
					+ opb.getReg().caseNo()
					+ "' "
					+ "    AND RX_NO='"
					+ temp.getValue("RX_NO")
					+ "' "
					+ "    AND ORDERSET_CODE IS NOT NULL "
					+ "    AND ORDERSET_CODE = ORDER_CODE "
					+ "    AND SEQ_NO='"
					+ temp.getValue("SEQ_NO")
					+ "' AND ADM_TYPE='" + admType + "'";
			// System.out.println("SQL:"+sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			// System.out.println("��ѯ���:"+result);
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			parm.setData("FLG", 1);// �˷�
			parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
			parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
			parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));

			list.add(parm);
		}
		// System.out.println("���ͽӿ���Ŀ:"+list);
		// ���ýӿ�
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0) {
			this.messageBox(resultParm.getErrText());
		}
	}

	/**
	 * ҽ�ƿ��˷Ѳ���
	 * 
	 * @param parm
	 * @param actionParm
	 * @return
	 */
	private TParm ektResetFee(TParm actionParm) {
		TParm result = new TParm();
		//flg = true;// �ж��˷ѷ�ʽ
		actionParm.setData("REGION_CODE", Operator.getRegion());
		actionParm.setData("CASE_NO", opb.getReg().caseNo());
		actionParm.setData("MR_NO", opb.getPat().getMrNo());
		actionParm.setData("INS_FLG", insFlg);
		actionParm.setData("CONFIRM_NO", CONFIRMNO);
		// ҽ�ƿ��˷�
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"backEKTOPBRecp", actionParm);
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 */
	private void concelResetFee(TParm parm) {
		// ҽ�ƿ��˷Ѳ���ʧ�ܻس�����
		TParm writeParm = new TParm();
		writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
		writeParm.setData("MR_NO", parmEKT.getValue("MR_NO"));
		writeParm.setData("SEQ", parmEKT.getValue("SEQ"));
		writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
				parmEKT.getValue("MR_NO")); // ��дҽ�ƿ����
		if (writeParm.getErrCode() < 0)
			System.out.println("err:" + writeParm.getErrText());
		TParm concelParm = new TParm();
		concelParm.setData("TREDE_NO", parm.getValue("TREDE_NO"));// ҽ�ƿ��վݺ�
		concelParm.setData("BUSINESS_NO", parm.getValue("BUSINESS_NO"));// ҽ�ƿ����׺�
		writeParm = concelEKT(concelParm);
		if (writeParm.getErrCode() < 0) {
			System.out.println("ҽ�ƿ��˷ѳ���ҽ�ƿ�����ʧ��");
		} else {
			System.out.println("ҽ�ƿ��˷ѳ���ҽ�ƿ������ɹ�");
		}
	}

	/**
	 * ����ҽ�ƿ�����
	 * 
	 * @return
	 */
	private TParm concelEKT(TParm parm) {
		TParm result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"deleteRegOldData", parm);
		return result;
		// EKTTool.getInstance().deleteTrede(parm, connection);
	}

	/**
	 * Ʊ�ݲ���
	 * 
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean onSaveRePrint(int row) {
		TParm saveRePrintParm = getRePrintData(row);
		if (saveRePrintParm == null)
			return false;
		// ����opbaction
		TParm result = null;
		if (ektParm.getCount("CASE_NO") > 0) {
//			System.out.println("����ҽ�ƿ�����");
			// ҽ�ƿ�����
			// COUNT
			saveRePrintParm.setData("COUNT", ektParm.getCount("CASE_NO"));
//			System.out.println("����ҽ�ƿ�����11111");
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBEKTRePrint", saveRePrintParm);
//			System.out.println("����ҽ�ƿ�����22222");

		} else {
//			System.out.println("�����ֽ𲹴�");
			// �ֽ𲹴�
			saveRePrintParm.setData("COUNT", -1);
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"saveOPBRePrint", saveRePrintParm);
		}
		printNoOnly = result.getValue("PRINT_NO");
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}

		return true;
	}

	/**
	 * �õ���ӡ����
	 * 
	 * @param row
	 *            int
	 * @return TParm
	 */
	public TParm getRePrintData(int row) {
		TParm saveParm = tableM.getParmValue();
		TParm actionParm = saveParm.getRow(row);
		actionParm.setData("OPT_USER", Operator.getID());
		actionParm.setData("OPT_TERM", Operator.getIP());
		actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		// System.out.println("actionParm"+actionParm);
		actionParm.setData("ADM_TYPE", opb.getReg().getAdmType());
		return actionParm;

	}

	/**
	 * ҽ����ȡ����
	 * 
	 * @return
	 */
	private void getInsValue(TParm resultParm, TParm sumParm,String printNo) {
		TParm insFeeParm = new TParm();
		insFeeParm.setData("CASE_NO", opb.getReg().caseNo());// �˹�ʹ��
		insFeeParm.setData("RECP_TYPE", "OPB");// �շ�ʹ��
		// insFeeParm.setData("CONFIRM_NO", resultParm.getValue("CONFIRM_NO",
		// 0));// ҽ�������
		// ---д������Ҫ�޸�
		insFeeParm.setData("NAME", opb.getPat().getName());
		insFeeParm.setData("MR_NO", opb.getPat().getMrNo());// ������
		if (ektParm.getCount("CASE_NO") > 0) {
			insFeeParm.setData("PAY_TYPE", true);// ֧����ʽ
		} else {
			insFeeParm.setData("PAY_TYPE", false);// ֧����ʽ
		}
		TParm result = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				insFeeParm);// ��ѯҽ����Ϣ
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return;
		}
		if (cashFLg) {
			this.messageBox("Ʊ�ݺ���:"+printNo+" ҽ���˷ѽ��:"
					+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
					+ ","
					+ type
					+ "�ۿ�:"
					+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
							- sumParm.getDouble("PAY_INS_CARD", 0), 2))
					+ ",��ע��");
		} else {
			this.messageBox("Ʊ�ݺ���:"+printNo+" ҽ���˷ѽ��:"
					+ StringTool.round(sumParm.getDouble("PAY_INS_CARD", 0), 2)
					+ ","
					+ type
					+ "�ۿ�:"
					+ (StringTool.round(sumParm.getDouble("TOT_AMT", 0)
							- sumParm.getDouble("PAY_INS_CARD", 0), 2)));

		}
		nhiRegionCode = result.getValue("REGION_CODE", 0);
		if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 1) {
			resultParm.setData("INS_TYPE", "1");// ҽ����ҽ���
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 1
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "2");// ҽ����ҽ���
		} else if (result.getInt("INS_CROWD_TYPE", 0) == 2
				&& result.getInt("INS_PAT_TYPE", 0) == 2) {
			resultParm.setData("INS_TYPE", "3");// ҽ����ҽ���
		}
	}

	/**
	 * ҽ���˷Ѳ���
	 */
	private boolean reSetInsSave(TParm opbParm) {

		// ��ѯҽ�����
		String sql = "SELECT PAY_INS_CARD,TOT_AMT,RECEIPT_NO FROM BIL_OPB_RECP WHERE PRINT_NO='"
				+ opbParm.getValue("PRINT_NO") + "'";
		TParm bilParm = new TParm(TJDODBTool.getInstance().select(sql));

		// ҽ�ƿ�����
		if (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY") //add by huangtt 20160612 ֧����
				+ opbParm.getDouble("PAY_OTHER1") > 0 || opbParm.getDouble("PAY_INS_CARD") > 0
				&& (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY")
				+ opbParm.getDouble("PAY_OTHER1")) == 0&& opbParm.getDouble("PAY_CASH") == 0) {
//			if (null == parmEKT || parmEKT.getErrCode() < 0) {  //delete by huangtt 20171107ҽ����ҽ�ƿ����ʱ����Ҫ�ȶ�����ѯ���
				parmEKT = EKTIO.getInstance().TXreadEKT();
				if (null == parmEKT || parmEKT.getErrCode() < 0
						|| parmEKT.getValue("MR_NO").length() <= 0) {
					this.messageBox("���ҽ�ƿ�");
					return false;
				}
//			}
			if (!parmEKT.getValue("MR_NO").equals(opb.getPat().getMrNo())) {
				this.messageBox("ҽ�ƿ���Ϣ��˲�������");
				return false;
			}
			if (parmEKT.getDouble("CURRENT_BALANCE") < bilParm.getDouble(
					"PAY_INS_CARD", 0)) {
				this.messageBox("ҽ�ƿ��н��С��ҽ���˷ѽ��,����ִ��ҽ�ƿ��˷Ѳ���");
				return false;
			}
		}
		TParm reSetInsParm = new TParm();
		TParm parm = new TParm();
		// System.out.println("---------------ҽ���˷�-------------:" + opbParm);
		parm.setData("CASE_NO", opb.getReg().caseNo());
		parm.setData("INV_NO", opbParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", "OPB");// �շ�����

		// ��ѯ�Ƿ�ҽ�� �˷�
		TParm result = INSOpdTJTool.getInstance().selectInsInvNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return false;
		}
		if (result.getCount("CASE_NO") <= 0) {// ����ҽ���˷�
			return true;
		}

		// // ��ѯҽ���˷ѽ��
		// TParm sumParm = INSOpdTJTool.getInstance().selectInsSumAmt(parm);
		// if (sumParm.getErrCode() < 0) {
		// this.messageBox("E0005");
		// return false;
		// }
		// ҽ�����˷� ��Ҫ�޸�ҽ�ƿ�����
		if (null == opb.getReg().caseNo()
				&& opb.getReg().caseNo().length() <= 0) {
			this.messageBox("E0005");
			return false;
		}

		// int returnType = insExeFee(opbParm, result, false);
		// if (returnType == 0) {// ȡ��
		// System.out.println("ҽ������ʧ��");
		// return false;
		// }
		getInsValue(result, bilParm,opbParm.getValue("PRINT_NO"));
		// ====zhangp 20120229
		insType = result.getInt("INS_TYPE");
		CONFIRMNO = result.getValue("CONFIRM_NO", 0);//ҽ����
		// =====zhangp 20120229
		reSetInsParm.setData("CASE_NO", opb.getReg().caseNo());// �����
		reSetInsParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));// ҽ�������
		reSetInsParm.setData("INS_TYPE", result.getValue("INS_TYPE"));// ҽ����ҽ����
		reSetInsParm.setData("RECP_TYPE", "OPB");// �շ�����
		reSetInsParm.setData("UNRECP_TYPE", "OPBT");// �˷�����
		reSetInsParm.setData("OPT_USER", Operator.getID());// id
		reSetInsParm.setData("OPT_TERM", Operator.getIP());// ip
		reSetInsParm.setData("REGION_CODE", nhiRegionCode);// ҽ���������
		reSetInsParm.setData("INV_NO", opbParm.getValue("PRINT_NO"));// Ʊ�ݺ�
		reSetInsParm.setData("PAT_TYPE", this.getValue("CTZ1_CODE"));// ���
		reSetInsParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// ���

		// System.out.println("reSetInsParm:::::::" + reSetInsParm);
		result = INSTJReg.getInstance().insResetCommFunction(
				reSetInsParm.getData());

		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return false;
		}
		insFlg = true;// ҽ������
		if (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY")
				+ opbParm.getDouble("PAY_OTHER1") > 0 || opbParm.getDouble("PAY_INS_CARD") > 0
				&& (opbParm.getDouble("PAY_MEDICAL_CARD") + opbParm.getDouble("ALIPAY")
				+ opbParm.getDouble("PAY_OTHER1")) == 0&& opbParm.getDouble("PAY_CASH") == 0) {

			TParm opdParm = new TParm();
			opdParm.setData("NAME", opb.getPat().getName());
			opdParm.setData("SEX", opb.getPat().getSexCode().equals("1") ? "��"
					: "Ů");
			opdParm.setData("AMT", bilParm.getDouble("PAY_INS_CARD", 0));
			opdParm.setData("INS_FLG", "Y"); // ҽ��ʹ��
			// ��Ҫ�޸ĵĵط�
			opdParm.setData("MR_NO", opb.getPat().getMrNo());
			result = insExeUpdate(-bilParm.getDouble("PAY_INS_CARD", 0),
					bilParm.getDouble("TOT_AMT", 0), opb.getReg().caseNo(), "OPBT",bilParm.getValue("RECEIPT_NO", 0),CONFIRMNO);

			// result = EKTIO.getInstance().insUnFee(opdParm, this);
			// System.out.println("��result::::::::"+result);
			if (result.getErrCode() < 0) {
				this.messageBox(result.getErrText());
				return false;
			}
		}
		return true;
	}

	/**
	 * ɾ��ҽ����;״̬
	 * 
	 * @return
	 */
	public boolean deleteInsRun(String caseNo) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", "OPBT");
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		// System.out.println("��;״̬���ݷ��أ�" + result);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "ҽ����ִ�в���ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * ҽ�ƿ���������˴�ҽ�����ۿ���
	 * 
	 * @param returnParm
	 * @return
	 */
	private TParm insExeUpdate(double accountAmt, double totAmt,
			String caseNo, String business_type,String receiptNo,String CONFIRMNO) {
		// ���:AMT:���β������ BUSINESS_TYPE :���β������� CASE_NO:�������
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		orderParm.setData("RECEIPT_NO", receiptNo);
		orderParm.setData("CURRENT_BALANCE",parmEKT.getDouble("CURRENT_BALANCE"));
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("OPT_TERM", Operator.getIP());
		orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("ID_NO", parmEKT.getValue("IDNO"));
		orderParm.setData("CARD_NO", parmEKT.getValue("PK_CARD_NO"));// ����
		orderParm.setData("MR_NO", parmEKT.getValue("MR_NO"));// ������
		orderParm.setData("PAT_NAME", parmEKT.getValue("PAT_NAME"));// ��������
		orderParm.setData("EKT_USE", totAmt);// �ܽ��
		orderParm.setData("EKT_OLD_AMT",parmEKT.getDouble("CURRENT_BALANCE"));// ԭ�����
		orderParm.setData("CONFIRMNO",CONFIRMNO);// ҽ����
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}
	/**
	 * ȡ�ô���Ŀ�ֵ���嵥����
	 * ======zhangp 20120925
	 * @param orderParm
	 * @return
	 */
	private TParm getInsRuleParm(TParm orderParm){
        String sql = // wanglong modify 20150123 ��sql��Ϊ������ҽ����Ҳ��Ӱ�������еķ��أ������ջ�ҽԺ��
            "SELECT B.*, CASE WHEN B.ZFBL1 IS NULL "
                    + "       THEN (B.ORDER_DESC1 || CASE WHEN TRIM(B.MAN_CHN_DESC) IS NOT NULL OR TRIM(B.MAN_CHN_DESC) <> '' "
                    + "                                    THEN '*' || B.MAN_CHN_DESC ELSE '' END) "
                    + "       ELSE (CASE WHEN B.ZFBL1 = 1 THEN '��' WHEN B.ZFBL1 > 0 AND B.ZFBL1 < 1  THEN '#' END "
                    + "             || B.NHI_CODE_O || ' ' || B.ORDER_DESC1 || "
                    + "             CASE WHEN TRIM(B.MAN_CHN_DESC) IS NOT NULL OR TRIM(B.MAN_CHN_DESC) <> '' THEN '*' "
                    + "             || B.MAN_CHN_DESC ELSE '' END || ' ' || B.PZWH || ' ' || B.ZFBL1 * 100 || '%') "
                    + "       END AS ORDER_DESC "
                    + " FROM (SELECT A.CASE_NO, A.RX_NO, A.SEQ_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, "
                    + "            A.PRESRT_NO, A.REGION_CODE, A.MR_NO, A.ADM_TYPE, A.RX_TYPE, "
                    + "            A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, A.LINK_NO, A.ORDER_CODE, "
                    + "            D.NHI_CODE_O,A.ORDER_DESC ORDER_DESC1 ,C.MAN_CHN_DESC, "
                    + "            A.SPECIFICATION, A.GOODS_DESC, A.ORDER_CAT1_CODE, A.MEDI_QTY, A.MEDI_UNIT, "
                    + "            A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, A.DOSAGE_QTY, A.DOSAGE_UNIT, A.DISPENSE_QTY, "
                    + "            A.DISPENSE_UNIT, A.GIVEBOX_FLG, A.OWN_PRICE, A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT,  "
                    + "            A.AR_AMT, A.DR_NOTE, A.NS_NOTE, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE, A.DC_DR_CODE,  "
                    + "            A.DC_ORDER_DATE, A.DC_DEPT_CODE, A.EXEC_DEPT_CODE, A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, "
                    + "            A.ORDERSET_CODE, A.HIDE_FLG, A.RPTTYPE_CODE, A.OPTITEM_CODE, A.DEV_CODE, A.MR_CODE, "
                    + "            A.FILE_NO, A.DEGREE_CODE, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE, "
                    + "            A.PRINTTYPEFLG_INFANT, A.EXPENSIVE_FLG, A.CTRLDRUGCLASS_CODE, A.PRESCRIPT_NO, "
                    + "            A.ATC_FLG, A.SENDATC_DATE, A.RECEIPT_NO, A.BILL_FLG, A.BILL_DATE, A.BILL_USER,  "
                    + "            A.PRINT_FLG, A.REXP_CODE, A.HEXP_CODE, A.CONTRACT_CODE, A.CTZ1_CODE, A.CTZ2_CODE,  "
                    + "            A.CTZ3_CODE, A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE,  "
                    + "            A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE, A.NS_EXEC_CODE, A.NS_EXEC_DATE, A.NS_EXEC_DEPT,  "
                    + "            A.DCTAGENT_CODE, A.DCTEXCEP_CODE, A.DCT_TAKE_QTY, A.PACKAGE_TOT, A.AGENCY_ORG_CODE,  "
                    + "            A.DCTAGENT_FLG, A.DECOCT_CODE, A.EXEC_FLG, A.RECEIPT_FLG, A.CAT1_TYPE, A.MED_APPLY_NO,  "
                    + "            A.BILL_TYPE, A.PHA_RETN_CODE, F.DOSE_CHN_DESC, '0.0%' AS AL, A.BUSINESS_NO, "
                    + "            G.PZWH, G.ZFBL1, G.KSSJ, G.JSSJ "
                    + "       FROM OPD_ORDER A, SYS_MANUFACTURER C, SYS_FEE_HISTORY D, PHA_BASE E, PHA_DOSE F, "
                    + "            INS_RULE G "
                    + "      WHERE A.ORDER_CODE = D.ORDER_CODE(+) "
                    + "        AND D.MAN_CODE = C.MAN_CODE(+) "
                    + "        AND A.ORDER_CODE = E.ORDER_CODE(+) "
                    + "        AND E.DOSE_CODE = F.DOSE_CODE(+) "
                    + "        AND CASE_NO = '" + orderParm.getValue("CASE_NO") + "' "
                    + "        AND RECEIPT_NO = '" + orderParm.getValue("RECEIPT_NO") + "' "
                    + "        AND D.NHI_CODE_O = G.SFXMBM(+) "
//                    + "        AND A.BILL_DATE BETWEEN G.KSSJ AND G.JSSJ "
//                    + "        AND A.BILL_DATE BETWEEN TO_DATE( D.START_DATE, 'YYYYMMDDHH24MISS') AND TO_DATE( D.END_DATE, 'YYYYMMDDHH24MISS') "
                    + "        AND TO_CHAR( A.BILL_DATE, 'YYYYMMDDHH24MISS') >= D.START_DATE(+) "
                    + "        AND TO_CHAR( A.BILL_DATE, 'YYYYMMDDHH24MISS') <= D.END_DATE(+)) B "
                    + " WHERE B.KSSJ IS NULL                    "
                    + "    OR B.JSSJ IS NULL                    "
                    + "    OR (B.KSSJ IS NULL AND B.JSSJ IS NULL) "
                    + "    OR B.BILL_DATE BETWEEN B.KSSJ AND B.JSSJ "
                    + "ORDER BY B.RX_TYPE, B.RX_NO, B.SEQ_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			System.out.println(result.getErrText());
		}
		return result;
	}
	
	 public TParm getMakeOrderInvalid(String receiptNo,String confirmNo) {
		 String sql = OPBMakeRecpAndOrderInvalid.getInstance().SQL.replace("#", receiptNo);
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 TParm parm = new TParm();
		 TParm parmR = new TParm();
		 for (int i = 0; i < result.getCount(); i++) {
			 result.setData("RECEIPT_NO", i, "");
			 result.setData("PRINT_FLG", i, "N");
			 parm.addRowData(result, i);
			
		}
		 
		 if(parm.getCount("ORDER_CODE") > 0){
			 TParm inParm = new TParm();
				inParm.setData("orderParm", parm.getData());			
				inParm.setData("EKT_HISTORY_NO", "");
				inParm.setData("OPT_TYPE", "UPDATE");
				inParm.setData("OPT_USER", Operator.getID());
				inParm.setData("OPT_TERM", Operator.getIP());
				 if(confirmNo.length() > 0){
					 inParm.setData("MZCONFIRM_NO", "*"+confirmNo);
				 }else{
					 inParm.setData("MZCONFIRM_NO", "");
				 }

				TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);

				TParm sqlParm = historyParm.getParm("sqlParm");
				String ids = historyParm.getValue("LastHistoryIds");
				
				for (int j = 0; j < sqlParm.getCount("SQL"); j++) {
					 parmR.addData("SQL", sqlParm.getValue("SQL",j));
					
				}
			 
				if(ids.length() > 0){
					ids = ids.substring(0, ids.length()-1);
					String sqlU="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
					parmR.addData("SQL", sqlU);
				}
			 
			
		 }
		 
		 return parmR;
		 
	 }
	
}
