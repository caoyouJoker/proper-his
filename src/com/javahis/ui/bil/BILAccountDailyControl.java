package com.javahis.ui.bil;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import jdo.bil.BILAccountTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import java.util.Vector;
import com.javahis.util.StringUtil;
import jdo.bil.BILSysParmTool;
import jdo.ins.TJINSRecpTool;
import jdo.sys.SYSOperatorTool;

/**
 * <p>
 * Title: סԺ�ս������
 * </p>
 *
 * <p>
 * Description: סԺ�ս������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author wangl 2009.09.19
 * @version 1.0
 */
public class BILAccountDailyControl extends TControl {
	String accountSeq = "";
	TParm accountSeqParm = new TParm();

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// table���������¼�
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		TTable table = (TTable) this.getComponent("Table");
		// table����checkBox�¼�
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableComponent");
		initPage();
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		// ��ʼ��Ժ��
		setValue("REGION_CODE", Operator.getRegion());
		// ��ʼ����ѯ��ʱ,��ʱ
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		Timestamp today = SystemTool.getInstance().getDate();
		setValue("S_DATE", yesterday);
		setValue("E_DATE", today);
		setValue("ACCOUNT_DATE", today);
		String todayTime = StringTool.getString(today, "HH:mm:ss");
		String accountTime = todayTime;
		if (getAccountDate().length() != 0) {
			accountTime = getAccountDate();
			accountTime = accountTime.substring(0, 2) + ":"
					+ accountTime.substring(2, 4) + ":"
					+ accountTime.substring(4, 6);
		}
		// ========pangben modify 20120320 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20120320 stop
		setValue("ACCOUNT_TIME", accountTime);
		setValue("S_TIME", accountTime);
		setValue("E_TIME", accountTime);
		setValue("CASHIER_CODE", Operator.getID());

		// ���սᰴťΪ��
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		//===zhangp 201200502 start
//		callFunction("UI|ACCOUNT_TIME|setEnabled", true);
//		callFunction("UI|ACCOUNT_DATE|setEnabled", false);
		//===zhangp 201200502 end

	}

	public String getTime(String name) {
		String time = getText(name);
		if (time.length() != 8)
			return "";
		try {
			if (!checkTime(time.substring(0, 2), 23))
				return "";
			if (!checkTime(time.substring(3, 5), 59))
				return "";
			if (!checkTime(time.substring(6), 59))
				return "";
		} catch (Exception e) {
			return "";
		}
		return time.substring(0, 2) + time.substring(3, 5) + time.substring(6);
	}

	public boolean checkTime(String s, int max) {
		if (s.substring(0, 1).equals("0"))
			s = s.substring(1);
		int x = Integer.parseInt(s);
		return x >= 0 && x <= max;
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String start = getTime("S_TIME");
		if (start.length() == 0) {
			messageBox("�����ѯ���յ�ʱ�䲻��ȷ!");
			return;
		}
		String end = getTime("E_TIME");
		if (end.length() == 0) {
			messageBox("�����ѯ���յ�ʱ�䲻��ȷ!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		TParm result = new TParm();
		TParm selAccountData = new TParm();
		selAccountData.setData("ACCOUNT_TYPE", "IBS");
		if (getValue("CASHIER_CODE").toString().length() > 0)
			selAccountData.setData("ACCOUNT_USER", getValue("CASHIER_CODE")
					.toString());
		selAccountData.setData("S_TIME", startTime + start);
		selAccountData.setData("E_TIME", endTime + end);
		selAccountData.setData("REGION_CODE", Operator.getRegion());
		result = BILAccountTool.getInstance().accountQuery(selAccountData);
		this.callFunction("UI|Table|setParmValue", result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();
		accountSeq = new String();
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (accountSeqParm.getCount("ACCOUNT_SEQ") <= 0) {
			messageBox("��ѡ���ӡ����!");
			return;
		}
		if ("N".equals(getValue("TOGEDER_FLG"))) {
			int count = accountSeqParm.getCount("ACCOUNT_SEQ");
			TParm optNameParm = new TParm();
			String optName = "";
			for (int i = 0; i < count; i++) {
				optNameParm = SYSOperatorTool.getInstance().selectdata(
						accountSeqParm.getValue("ACCOUNT_USER", i));
				optName = optNameParm.getValue("USER_NAME", 0);
				print(accountSeqParm.getValue("ACCOUNT_SEQ", i), optName);
			}
			return;
		}
		if ("Y".equals(getValue("TOGEDER_FLG"))) {
			togederPrint();
			return;
		}
	}

	/**
	 * �ս�
	 */
	public void onSave() {
		//===zhangp 20120502 start
		TTextFormat text = (TTextFormat) getComponent("ACCOUNT_DATE");
		Timestamp time = (Timestamp) text.getValue();
		//===zhangp 20120502 end
		TParm parm = new TParm();
		parm.setData("CASHIER_CODE", getValue("CASHIER_CODE"));
		parm.setData("REGION_CODE", getValue("REGION_CODE"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		//===zhangp 20120502 start
		String date = StringTool.getString((Timestamp)text.getValue(), "yyyyMMdd")+getValueString("ACCOUNT_TIME").substring(0, 2)+
			getValueString("ACCOUNT_TIME").substring(3, 5)+getValueString("ACCOUNT_TIME").substring(6, 8);
		parm.setData("ACCOUNT_DATE", date);
		//===zhangp 20120502 end
		TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
				"onSaveAcctionBIL", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			// �ս�ʧ��
//			this.messageBox("E0005");
			this.messageBox("���ս�����");
			return;
		}
		// �ս�ɹ�
		this.messageBox("P0005");
	}

	/**
	 * ���ñ����ӡԤ������
	 *
	 * @param accountSeq
	 *            String
	 * @param accountUser
	 *            String
	 */
	public void print(String accountSeq, String accountUser) {
		DecimalFormat formatObject = new DecimalFormat("###########0.00");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd");
		String sDate = StringTool.getString(
				(Timestamp) this.getValue("S_DATE"), "yyyy/MM/dd")
				+ " " + this.getValueString("S_TIME");

		String eDate = StringTool.getString(
				(Timestamp) this.getValue("E_DATE"), "yyyy/MM/dd")
				+ " " + this.getValueString("E_TIME");
		// ===zhangp 20120305 modify start
		String printNoSQL = "select INV_NO from bil_invrcp where account_seq = '"
				+ accountSeq
				+ "' "
				+ "and STATUS='0' AND RECP_TYPE = 'IBS' ORDER BY INV_NO";
		// ===zhangp 20120305 modify end
		TParm printNoParm = new TParm(TJDODBTool.getInstance().select(
				printNoSQL));
		if (printNoParm.getErrCode() < 0) {
			System.out.println("BILAccountDailyControl.print Err:"
					+ printNoParm.getErrText());
			return;
		}
		// =====zhangp 20120302 modify start
		String selReceiptNo = " SELECT RECEIPT_NO,CHARGE_DATE,CASE_NO "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") " +
				// "    AND (RESET_RECEIPT_NO = '' OR RESET_RECEIPT_NO IS NULL) "
				// +
				"  ORDER BY RECEIPT_NO";
		// =====zhangp 20120302 modify end
		// �վݺ���
		TParm selReceiptNoParm = new TParm(TJDODBTool.getInstance().select(
				selReceiptNo));
		int receiptNoCount = selReceiptNoParm.getCount("RECEIPT_NO");
		StringBuffer allReceiptNo = new StringBuffer();
		StringBuffer allCaseNo = new StringBuffer();// ��ѯCASE_NO pangben
													// 2012-3-19
		for (int j = 0; j < receiptNoCount; j++) {
			String seq = "";
			seq = selReceiptNoParm.getValue("RECEIPT_NO", j);
			if (allReceiptNo.length() > 0)
				allReceiptNo.append(",");
			allReceiptNo.append(seq);
			// ���case_no pangben 2012-3-19
			if (!allCaseNo.toString().contains(
					selReceiptNoParm.getValue("CASE_NO", j))) {
				if (allCaseNo.length() > 0)
					allCaseNo.append(",");
				allCaseNo.append(selReceiptNoParm.getValue("CASE_NO", j));
			}
		}
		String allReceiptNoStr = allReceiptNo.toString();
		String selRecpD = " SELECT REXP_CODE,WRT_OFF_AMT "
				+ "   FROM BIL_IBS_RECPD " + "  WHERE RECEIPT_NO IN ("
				+ allReceiptNoStr + ") ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
				.select(selRecpD));
		int recpDCount = selRecpDParm.getCount("REXP_CODE");
		TParm charge = new TParm();
		String rexpCode = "";
		double wrtOffAmt101 = 0.00;
		double wrtOffAmt102 = 0.00;
		double wrtOffAmt103 = 0.00;
		double wrtOffAmt104 = 0.00;
		double wrtOffAmt105 = 0.00;
		double wrtOffAmt106 = 0.00;
		double wrtOffAmt107 = 0.00;
		double wrtOffAmt108 = 0.00;
		double wrtOffAmt109 = 0.00;
		double wrtOffAmt110 = 0.00;
		double wrtOffAmt111 = 0.00;
		double wrtOffAmt112 = 0.00;
		double wrtOffAmt113 = 0.00;
		double wrtOffAmt114 = 0.00;
		double wrtOffAmt115 = 0.00;
		double wrtOffAmt116 = 0.00;
		double wrtOffAmt117 = 0.00;
		double wrtOffAmt118 = 0.00;
		double wrtOffAmt119 = 0.00;
		double wrtOffAmt120 = 0.00;
		double totAmt = 0.00;
		double wrtOffSingle = 0.00;
		TParm chargeParm = new TParm();
		for (int i = 0; i < recpDCount; i++) {
			rexpCode = selRecpDParm.getValue("REXP_CODE", i);
			wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
			totAmt = totAmt + wrtOffSingle;
			if ("020".equals(rexpCode)) { // ��λ��(ס)
				wrtOffAmt101 = wrtOffAmt101 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt101);
				chargeParm.setData("CHARGE01", arAmtS);
			} else if ("021".equals(rexpCode)) { // ����(ס)
				wrtOffAmt102 = wrtOffAmt102 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt102);
				chargeParm.setData("CHARGE02", arAmtS);
			} else if ("022.01".equals(rexpCode)) { // ������(ס)
				wrtOffAmt103 = wrtOffAmt103 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt103);
				chargeParm.setData("CHARGE03", arAmtS);
			} else if ("022.02".equals(rexpCode)) { // �ǿ�����(ס)
				wrtOffAmt104 = wrtOffAmt104 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt104);
				chargeParm.setData("CHARGE04", arAmtS);
			} else if ("023".equals(rexpCode)) { // �г�ҩ��(ס)
				wrtOffAmt105 = wrtOffAmt105 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt105);
				chargeParm.setData("CHARGE05", arAmtS);
			} else if ("024".equals(rexpCode)) { // �в�ҩ��(ס)
				wrtOffAmt106 = wrtOffAmt106 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt106);
				chargeParm.setData("CHARGE06", arAmtS);
			} else if ("025".equals(rexpCode)) { // ����(ס)
				wrtOffAmt107 = wrtOffAmt107 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt107);
				chargeParm.setData("CHARGE07", arAmtS);
			} else if ("026".equals(rexpCode)) { // ���Ʒ�(ס)
				wrtOffAmt108 = wrtOffAmt108 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt108);
				chargeParm.setData("CHARGE08", arAmtS);
			} else if ("027".equals(rexpCode)) { // �����(ס)
				wrtOffAmt109 = wrtOffAmt109 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt109);
				chargeParm.setData("CHARGE09", arAmtS);
			} else if ("028".equals(rexpCode)) { // ������(ס)
				wrtOffAmt110 = wrtOffAmt110 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt110);
				chargeParm.setData("CHARGE10", arAmtS);
			} else if ("029".equals(rexpCode)) { // �����(ס)
				wrtOffAmt111 = wrtOffAmt111 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt111);
				chargeParm.setData("CHARGE11", arAmtS);
			} else if ("02A".equals(rexpCode)) { // ��Ѫ��(ס)
				wrtOffAmt112 = wrtOffAmt112 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt112);
				chargeParm.setData("CHARGE12", arAmtS);
			} else if ("02B".equals(rexpCode)) { // ������(ס)
				wrtOffAmt113 = wrtOffAmt113 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt113);
				chargeParm.setData("CHARGE13", arAmtS);
			} else if ("02C".equals(rexpCode)) { // ������(ס)
				wrtOffAmt114 = wrtOffAmt114 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt114);
				chargeParm.setData("CHARGE14", arAmtS);
			} else if ("02D".equals(rexpCode)) { // ��λ��
				wrtOffAmt115 = wrtOffAmt115 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt115);
				chargeParm.setData("CHARGE15", arAmtS);
			} else if ("02E".equals(rexpCode)) { // �Ҵ���(ס)
				wrtOffAmt116 = wrtOffAmt116 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt116);
				chargeParm.setData("CHARGE16", arAmtS);
			} else if ("032".equals(rexpCode)) { // CT(ס)
				wrtOffAmt117 = wrtOffAmt117 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt117);
				chargeParm.setData("CHARGE17", arAmtS);
			} else if ("033".equals(rexpCode)) { // MR��ס��
				wrtOffAmt118 = wrtOffAmt118 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt118);
				chargeParm.setData("CHARGE18", arAmtS);
			} else if ("02F".equals(rexpCode)) { // �ԷѲ���(ס)
				wrtOffAmt119 = wrtOffAmt119 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt119);
				chargeParm.setData("CHARGE19", arAmtS);
			}
			// ===zhangp 20120307 modify start
			else if ("035".equals(rexpCode)) { // ���Ϸ�
				wrtOffAmt120 = wrtOffAmt120 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt120);
				chargeParm.setData("CHARGE20", arAmtS);
			}
			// ===zhangp 20120307 modify end
		}
		// parm = getPrintNo(accountSeq);
		// ====zhangp 20120308 modify start
		// charge.setData("CHARGE01",
		// chargeParm.getData("CHARGE01") == null ? 0.00 :
		// chargeParm.getData("CHARGE01"));
		// charge.setData("CHARGE02",
		// chargeParm.getData("CHARGE02") == null ? 0.00 :
		// chargeParm.getData("CHARGE02"));
		// charge.setData("CHARGE03",
		// chargeParm.getData("CHARGE03") == null ? 0.00 :
		// chargeParm.getData("CHARGE03"));
		// charge.setData("CHARGE04",
		// chargeParm.getData("CHARGE04") == null ? 0.00 :
		// chargeParm.getData("CHARGE04"));
		// charge.setData("CHARGE05",
		// chargeParm.getData("CHARGE05") == null ? 0.00 :
		// chargeParm.getData("CHARGE05"));
		// charge.setData("CHARGE06",
		// chargeParm.getData("CHARGE06") == null ? 0.00 :
		// chargeParm.getData("CHARGE06"));
		// charge.setData("CHARGE07",
		// chargeParm.getData("CHARGE07") == null ? 0.00 :
		// chargeParm.getData("CHARGE07"));
		// charge.setData("CHARGE08",
		// chargeParm.getData("CHARGE08") == null ? 0.00 :
		// chargeParm.getData("CHARGE08"));
		// charge.setData("CHARGE09",
		// chargeParm.getData("CHARGE09") == null ? 0.00 :
		// chargeParm.getData("CHARGE09"));
		// charge.setData("CHARGE10",
		// chargeParm.getData("CHARGE10") == null ? 0.00 :
		// chargeParm.getData("CHARGE10"));
		// charge.setData("CHARGE11",
		// chargeParm.getData("CHARGE11") == null ? 0.00 :
		// chargeParm.getData("CHARGE11"));
		// charge.setData("CHARGE12",
		// chargeParm.getData("CHARGE12") == null ? 0.00 :
		// chargeParm.getData("CHARGE12"));
		// charge.setData("CHARGE13",
		// chargeParm.getData("CHARGE13") == null ? 0.00 :
		// chargeParm.getData("CHARGE13"));
		// charge.setData("CHARGE14",
		// chargeParm.getData("CHARGE14") == null ? 0.00 :
		// chargeParm.getData("CHARGE14"));
		// charge.setData("CHARGE15",
		// chargeParm.getData("CHARGE15") == null ? 0.00 :
		// chargeParm.getData("CHARGE15"));
		// charge.setData("CHARGE16",
		// chargeParm.getData("CHARGE16") == null ? 0.00 :
		// chargeParm.getData("CHARGE16"));
		// charge.setData("CHARGE17",
		// chargeParm.getData("CHARGE17") == null ? 0.00 :
		// chargeParm.getData("CHARGE17"));
		// charge.setData("CHARGE18",
		// chargeParm.getData("CHARGE18") == null ? 0.00 :
		// chargeParm.getData("CHARGE18"));
		// charge.setData("CHARGE19",
		// chargeParm.getData("CHARGE19") == null ? 0.00 :
		// chargeParm.getData("CHARGE19"));
		// charge.setData("CHARGE20",
		// chargeParm.getData("CHARGE20") == null ? 0.00 :
		// chargeParm.getData("CHARGE20"));
		// charge.setData("CHARGE00",
		// chargeParm.getDouble("CHARGE03")+chargeParm.getDouble("CHARGE04"));
		// charge.setData("totAmt", formatObject.format(totAmt));
		// ====zhangp 20120308 modify end
		charge.setData("accountSeq", accountSeq);
		// =======zhangp 20120302 modify start
		String stardate = selReceiptNoParm.getData("CHARGE_DATE", 0).toString();
		String enddate = selReceiptNoParm.getData("CHARGE_DATE",
				selReceiptNoParm.getCount() - 1).toString();
		stardate = stardate.substring(0, 19);
		enddate = enddate.substring(0, 19);
		charge.setData("S_DATE", stardate + " �� " + enddate);
		// charge.setData("E_DATE", eDate);
		// ======zhangp 20120302 modify end
		charge.setData("PRINT_DATE", sysDate);
		charge.setData("CASHIER_CODE", accountUser);
		// System.out.println("��������"+accountSeq);
		charge.setData("patCount", getPatCount(accountSeq));
		// System.out.println("�������"+getPayCX(accountSeq));
		charge.setData("writeOffFee", formatObject
				.format(-getPayCX(accountSeq)));
		charge
				.setData("backWashFee", formatObject
						.format(getPayHC(accountSeq)));
		charge
				.setData("fillFee", formatObject
						.format(getPatFee(accountSeq)[0]));
		charge.setData("returnFee", formatObject
				.format(getPatFee(accountSeq)[1]));
		//====zhangp 20120514 start
		charge.setData("fillFeeCash", formatObject
				.format(getPatFee(accountSeq)[3]));
		charge.setData("fillFeeCheck", formatObject
				.format(getPatFee(accountSeq)[4]));
		charge.setData("returnFeeCash", formatObject
				.format(getPatFee(accountSeq)[5]));
		charge.setData("returnFeeCheck", formatObject
				.format(getPatFee(accountSeq)[6]));
		//====zhangp 20120514 end
		charge.setData("debitFee", "0.00");
		charge.setData("ownFee", formatObject.format(getPatFee(accountSeq)[2]));
		charge.setData("arAmt", formatObject.format(totAmt));
		String tot=formatObject.format(totAmt);
	   String tmp=StringUtil.getInstance().numberToWord(StringTool.getDouble(tot));
	   if(tmp.lastIndexOf("��")>0){//�з�ȥ������
		   tmp=tmp.substring(0,tmp.lastIndexOf("��")+1);
		  
	   }
		charge.setData("zhAmt", tmp);//modify by caoyong 2013722
		//charge.setData("zhAmt", StringUtil.getInstance().numberToWord(totAmt));
		charge.setData("OPT_USER", Operator.getName());
		// ===zhangp 20120305 modify start
		// ========pangben 2012-3-19 start �޸�ҽ����ʾ
		TParm insParm = getInsParm(accountSeq);
//		 System.out.println("insParm====="+insParm);
		if (insParm.getErrCode() < 0) {
			return;
		}
		//===zhangp 20120424 start
        double armyAi_amt = insParm.getDouble("ARMYAI_AMT");//����
        double account_pay_amt =  insParm.getDouble("ACCOUNT_PAY_AMT");//�����˻�
        double nhi_comment =  insParm.getDouble("NHI_COMMENT");//����
        double nhi_pay =  insParm.getDouble("NHI_PAY")+armyAi_amt;//ͳ��(��������)
        double ins = armyAi_amt + nhi_comment + nhi_pay;//���ո���
        double pay_cash = insParm.getDouble("OWN_PAY");//�ֽ�֧��/�Ը�
        double illnesssubsidyamt = insParm.getDouble("ILLNESS_SUBSIDY_AMT");//�����
		charge.setData("nhiComment", StringTool.round(nhi_comment, 2));// ���˾���
		charge.setData("illnesssubsidyamt", StringTool.round(illnesssubsidyamt, 2));//�����
		//=====================================================================================================
		charge.setData("accountPayAmt", StringTool.round(account_pay_amt, 2));// �����˻�֧��
		//=====================================================================================================
		charge.setData("debitFee", StringTool.round(nhi_pay, 2));//ͳ��
		charge.setData("ins_amt",  StringTool.round(nhi_comment+illnesssubsidyamt+
				                   account_pay_amt+nhi_pay, 2));//ҽ���ϼ�
		//===zhangp 20120424 end
		// ========pangben 2012-3-19 stop
		String selRecpNo = "SELECT START_INVNO,END_INVNO FROM BIL_INVOICE WHERE RECP_TYPE = 'IBS'";
		TParm invNoParm = new TParm(TJDODBTool.getInstance().select(selRecpNo));
		String recp_no = "";
		for (int i = 0; i < invNoParm.getCount(); i++) {
			TParm p = new TParm();
			String startNo = invNoParm.getData("START_INVNO", i).toString();
			String endNo = invNoParm.getData("END_INVNO", i).toString();
			// System.out.println(startNo+"                   "+endNo);
			for (int j = 0; j < printNoParm.getCount("INV_NO"); j++) {
				String invNo = printNoParm.getData("INV_NO", j).toString();
				if (invNo.compareTo(startNo) >= 0
						&& invNo.compareTo(endNo) <= 0) {
					p.addData("INV_NO", invNo);
				}
			}
			if (p.getCount("INV_NO") > 1) {
				recp_no += "," + p.getData("INV_NO", 0) + " ~ "
						+ p.getData("INV_NO", p.getCount("INV_NO") - 1);
			}
			if (p.getCount("INV_NO") > 0 && p.getCount("INV_NO") <= 1) {
				recp_no = "," + p.getData("INV_NO", 0);
			}
		}
		if (recp_no.length() > 0) {
			recp_no = recp_no.substring(1, recp_no.length());
		}
		charge.setData("PRINT_NO", recp_no);
		// charge.setData("PRINT_NO",
		// getPrintNO( (Vector) printNoParm.getData("INV_NO")));
		// ===zhangp 20120305 modify end
		// System.out.println("�ս�����"+charge);
		TParm print = new TParm();
		// ===zhangp 20120308 modify start
		String sql = " SELECT SUM(PAY_CASH) PAY_CASH "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") " + "    AND REFUND_DATE IS NULL "
				+ "    AND AR_AMT >= 0 ";
		TParm payWayParm = new TParm(TJDODBTool.getInstance().select(sql));
		print.setData("fillFeeCash", "TEXT", "�ֽ�: "
				+ formatObject.format(payWayParm.getDouble("PAY_CASH", 0)));
		for (int i = 1; i < 21; i++) {
			if (i < 10) {
				print.setData("CHARGE0" + i, "TEXT", chargeParm
						.getData("CHARGE0" + i) == null ? 0.00 : formatObject
						.format(chargeParm.getDouble("CHARGE0" + i)));
			} else {
				print.setData("CHARGE" + i, "TEXT", chargeParm.getData("CHARGE"
						+ i) == null ? 0.00 : formatObject.format(chargeParm
						.getDouble("CHARGE" + i)));
			}
		}
		print.setData("CHARGE00", "TEXT", formatObject.format(chargeParm
				.getDouble("CHARGE03")
				+ chargeParm.getDouble("CHARGE04")));
		print.setData("totAmt", "TEXT", formatObject.format(totAmt));
		// ===zhangp 20120308 modify end
		print.setData("CHARGE", charge.getData());
//		System.out.println("ssssssssssssssssssssssddddds"+charge);
		// System.out.println("print======="+print);
//		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILAccount.jhw", print);   
		this.openPrintWindow(IReportTool.getInstance().getReportPath("BILAccount.jhw"),
                             IReportTool.getInstance().getReportParm("BILAccount.class", print));//����ϲ�modify by wanglong 20130730
	}
        /**
         * ���ҽ������ ===============pangben 2012-3-19
         * @param accountSeq String
         * @return TParm
         */
        private TParm getInsParm(String accountSeq) {
//		String todayTime = StringTool.getString(SystemTool.getInstance()
//				.getDate(), "yyyyMM");
//		//====zhangp 20120424 start
////		String sql = "SELECT SUM(NHI_COMMENT) AS NHI_COMMENT,SUM(ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT "
////				+ "FROM  INS_IBS WHERE  YEAR_MON='"
////				+ todayTime
////				+ "' AND "
////				+ " CASE_NO IN (" + allCaseNo + ")";
//		//===zhangp 20120606 start
////		String sql = "SELECT CONFIRM_NO,CASE_NO FROM INS_ADM_CONFIRM WHERE CASE_NO IN (" + allCaseNo + ") " +
//		String sql = "SELECT CONFIRM_NO,CASE_NO FROM INS_ADM_CONFIRM WHERE CASE_NO IN " +
//				"(" +
//				" SELECT CASE_NO FROM BIL_IBS_RECPM WHERE CASE_NO IN (" + allCaseNo + ") AND REFUND_FLG IS NULL " +
//						" AND AR_AMT > 0" +
//						")" +
//						" AND CONFIRM_NO NOT LIKE 'KN%'" +
//		//===zhangp 20120606 end
//		//===ZHANGP 20120530 start
//				" AND IN_STATUS <> '5'";
//		//===ZHANGP 20120530 end
//        TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
//        if(confirmParm.getErrCode()<0){
//        	return confirmParm;
//        }
//        if(confirmParm.getCount()<0){
//        	return confirmParm;
//        }
//        String confirmnos = "";
////        (a.confirm_no ='08081204055209' and b.case_no = '120405000137') or
//        for (int i = 0; i < confirmParm.getCount(); i++) {
//        	confirmnos += " (A.CONFIRM_NO ='"+confirmParm.getData("CONFIRM_NO", i)+"' AND " +
//        			"B.CASE_NO = '"+confirmParm.getData("CASE_NO", i)+"') OR";
//		}
//        confirmnos = confirmnos.substring(0, confirmnos.length()-2);
//        System.out.println(confirmnos);
//        sql = 
//        	"SELECT SUM (INS.INSBASE_LIMIT_BALANCE) INSBASE_LIMIT_BALANCE," +
//        	" SUM (INS.INS_LIMIT_BALANCE) INS_LIMIT_BALANCE," +
//        	" SUM (INS.RESTART_STANDARD_AMT) RESTART_STANDARD_AMT," +
//        	" SUM (INS.REALOWN_RATE) REALOWN_RATE, SUM (INS.INSOWN_RATE) INSOWN_RATE," +
//        	" SUM (INS.ARMYAI_AMT) ARMYAI_AMT, SUM (INS.NHI_PAY) NHI_PAY," +
//        	" SUM (INS.NHI_COMMENT) NHI_COMMENT, SUM (INS.OWN_PAY) OWN_PAY," +
//        	" SUM (INS.AR_AMT) AR_AMT, SUM (INS.ACCOUNT_PAY_AMT) ACCOUNT_PAY_AMT" +
//        	" FROM (SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
//        	" B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE," +
//        	" B.ARMYAI_AMT," +
//        	" (  CASE" +
//        	" WHEN ARMYAI_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE ARMYAI_AMT" +
//        	" END" +
//        	" + CASE" +
//        	" WHEN TOT_PUBMANADD_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE TOT_PUBMANADD_AMT" +
//        	" END" +
//        	" + B.NHI_PAY" +
//        	" + CASE" +
//            " WHEN A.IN_STATUS = '4'" +
//            " THEN 0" +
//            " ELSE CASE" +
//            " WHEN B.REFUSE_TOTAL_AMT IS NULL" +
//            " THEN 0" +
//            " ELSE B.REFUSE_TOTAL_AMT" +
//            " END" +
//            " END" +
//        	" ) NHI_PAY," +
//        	" B.NHI_COMMENT," +
//        	" (  B.OWN_AMT" +
//        	" + B.ADD_AMT" +
//        	" + B.RESTART_STANDARD_AMT" +
//        	" + B.STARTPAY_OWN_AMT" +
//        	" + B.PERCOPAYMENT_RATE_AMT" +
//        	" + B.INS_HIGHLIMIT_AMT" +
//        	" - CASE" +
//        	" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE B.ACCOUNT_PAY_AMT" +
//        	" END" +
//        	" - CASE" +
//        	" WHEN ARMYAI_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE ARMYAI_AMT" +
//        	" END" +
//        	" - CASE" +
//        	" WHEN TOT_PUBMANADD_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE TOT_PUBMANADD_AMT" +
//        	" END" +
//        	" ) OWN_PAY," +
//        	" (  B.NHI_COMMENT" +
//        	" + B.NHI_PAY" +
//        	" + B.OWN_AMT" +
//        	" + B.ADD_AMT" +
//        	" + B.RESTART_STANDARD_AMT" +
//        	" + B.STARTPAY_OWN_AMT" +
//        	" + B.PERCOPAYMENT_RATE_AMT" +
//        	" + B.INS_HIGHLIMIT_AMT" +
//        	" + CASE" +
//            " WHEN A.IN_STATUS = '4'" +
//            " THEN 0" +
//            " ELSE CASE" +
//            " WHEN B.REFUSE_TOTAL_AMT IS NULL" +
//            " THEN 0" +
//            " ELSE B.REFUSE_TOTAL_AMT" +
//            " END" +
//            " END" +
//        	" ) AR_AMT," +
//        	" (CASE" +
//        	" WHEN B.ACCOUNT_PAY_AMT IS NULL" +
//        	" THEN 0" +
//        	" ELSE B.ACCOUNT_PAY_AMT" +
//        	" END" +
//        	" ) ACCOUNT_PAY_AMT" +
//        	" FROM INS_ADM_CONFIRM A, INS_IBS B" +
//        	" WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
//        	" AND " +confirmnos+
//        	//===zhangp 20120604 start
////        	" AND B.YEAR_MON = '"+todayTime+"'" +
//        	//===zhangp 20120604 end 
//        	" AND A.IN_STATUS IN ('1', '2', '3', '4')) INS";
//        System.out.println(sql);
        //===zhangp 20130717 start
        TParm tjIns2Word = TJINSRecpTool.getInstance().tjins2Word();
        //===zhangp 20130717 end
//        String sql = 
//        	" SELECT SUM (PAY_INS_CARD) ACCOUNT_PAY_AMT, SUM (PAY_INS) NHI_PAY, " +
//        	" 0 ARMYAI_AMT, 0 NHI_COMMENT, 0 OWN_PAY" +
//        	" FROM BIL_IBS_RECPM" +
//        	" WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
        String sql = 
    			" SELECT SUM(TJINS01) " + tjIns2Word.getValue("TJINS01") + ", " +
    			" SUM(TJINS02) " + tjIns2Word.getValue("TJINS02") + ", " +
    			" SUM(TJINS03) " + tjIns2Word.getValue("TJINS03") + ", " +
    			" SUM(TJINS04) " + tjIns2Word.getValue("TJINS04") + ", " +
    			" SUM(TJINS05) " + tjIns2Word.getValue("TJINS05") + ", " +
    			" SUM(TJINS06) " + tjIns2Word.getValue("TJINS06") + ", " +
    			" SUM(TJINS07) " + tjIns2Word.getValue("TJINS07") + ", " +
    			" SUM(TJINS08) " + tjIns2Word.getValue("TJINS08") + ", " +
    			" SUM(TJINS09) " + tjIns2Word.getValue("TJINS09") + ", " +
    			" SUM(TJINS10) " + tjIns2Word.getValue("TJINS10") + "" +
    			" FROM BIL_IBS_RECPM" +
    			" WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
//        System.out.println(sql);
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println(insParm);
        if(insParm.getErrCode()<0){
        	return insParm;
        }
        if(insParm.getCount()<0){
        	return insParm;
        }
        TParm result = new TParm();
        result = insParm.getRow(0);
        result.setCount(1);
    	return result;
	}

	public String getPrintNO(Vector printNo) {
		if (printNo == null)
			return "";
		String s1 = "";
		String s2 = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < printNo.size(); i++) {
			String t = (String) printNo.get(i);
			if (s2.length() == 0) {
				s1 = t;
				s2 = s1;
				continue;
			}
			if (StringTool.addString(s2).equals(t))
				s2 = t;
			else {
				if (sb.length() > 0)
					sb.append(",");
				if (s1.equals(s2)) {
					sb.append(s1);
				} else
					sb.append(s1 + "-" + s2);
				s1 = t;
				s2 = s1;
			}
		}
		if (sb.length() > 0)
			sb.append(",");
		if (s1.equals(s2)) {
			sb.append(s1);
		} else
			sb.append(s1 + "-" + s2);
		return sb.toString();
	}

	/**
	 * �ϲ���ӡ
	 */
	public void togederPrint() {
		DecimalFormat formatObject = new DecimalFormat("###########0.00");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd");
		String sDate = StringTool.getString(
				(Timestamp) this.getValue("S_DATE"), "yyyy/MM/dd")
				+ " " + this.getValueString("S_TIME");

		String eDate = StringTool.getString(
				(Timestamp) this.getValue("E_DATE"), "yyyy/MM/dd")
				+ " " + this.getValueString("E_TIME");
		// ======zhangp 20120302 modify start
		String selReceiptNo = " SELECT RECEIPT_NO,CHARGE_DATE,CASE_NO "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") "
				//===zhangp 20120824 start
//				+ "    AND (RESET_RECEIPT_NO ='' OR RESET_RECEIPT_NO IS NULL) " +
//				" AND AR_AMT > 0"
				//===zhangp 20120824 end
				+ "  ORDER BY RECEIPT_NO,CHARGE_DATE";
		// ======zhangp 20120302 modify end
		// �վݺ���
		TParm selReceiptNoParm = new TParm(TJDODBTool.getInstance().select(
				selReceiptNo));
		int receiptNoCount = selReceiptNoParm.getCount("RECEIPT_NO");
		StringBuffer allReceiptNo = new StringBuffer();
		StringBuffer allCaseNo = new StringBuffer();// pangben 2012-3-19
		for (int j = 0; j < receiptNoCount; j++) {
			String seq = "";
			seq = selReceiptNoParm.getValue("RECEIPT_NO", j);
			if (allReceiptNo.length() > 0)
				allReceiptNo.append(",");
			allReceiptNo.append(seq);
			// ���case_no pangben 2012-3-19
			if (!allCaseNo.toString().contains(
					selReceiptNoParm.getValue("CASE_NO", j))) {
				if (allCaseNo.length() > 0)
					allCaseNo.append(",");
				allCaseNo.append(selReceiptNoParm.getValue("CASE_NO", j));
			}
		}
		String allReceiptNoStr = allReceiptNo.toString();
		String selRecpD = " SELECT REXP_CODE,WRT_OFF_AMT "
				+ "   FROM BIL_IBS_RECPD " + "  WHERE RECEIPT_NO IN ("
				+ allReceiptNoStr + ") ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
				.select(selRecpD));
		int recpDCount = selRecpDParm.getCount("REXP_CODE");
		TParm charge = new TParm();
		String rexpCode = "";
		double wrtOffAmt101 = 0.00;
		double wrtOffAmt102 = 0.00;
		double wrtOffAmt103 = 0.00;
		double wrtOffAmt104 = 0.00;
		double wrtOffAmt105 = 0.00;
		double wrtOffAmt106 = 0.00;
		double wrtOffAmt107 = 0.00;
		double wrtOffAmt108 = 0.00;
		double wrtOffAmt109 = 0.00;
		double wrtOffAmt110 = 0.00;
		double wrtOffAmt111 = 0.00;
		double wrtOffAmt112 = 0.00;
		double wrtOffAmt113 = 0.00;
		double wrtOffAmt114 = 0.00;
		double wrtOffAmt115 = 0.00;
		double wrtOffAmt116 = 0.00;
		double wrtOffAmt117 = 0.00;
		double wrtOffAmt118 = 0.00;
		double wrtOffAmt119 = 0.00;
		double wrtOffAmt120 = 0.00;
		double wrtOffSingle = 0.00;
		double totAmt = 0.00;
		TParm chargeParm = new TParm();

		String accountUser = "";
		Vector au = new Vector();
		int count = accountSeqParm.getCount("ACCOUNT_USER");
		for (int i = 0; i < count; i++) {
			String s = accountSeqParm.getValue("ACCOUNT_USER", i);
			if (au.indexOf(s) != -1)
				continue;
			au.add(s);
		}
		for (int i = 0; i < au.size(); i++) {
			TParm optNameParm = SYSOperatorTool.getInstance().selectdata(
					(String) au.get(i));
			String s = optNameParm.getValue("USER_NAME", 0);
			if (accountUser.length() > 0)
				accountUser += ",";
			accountUser += s;
		}

		for (int i = 0; i < recpDCount; i++) {
			rexpCode = selRecpDParm.getValue("REXP_CODE", i);
			wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
			totAmt = totAmt + wrtOffSingle;
			if ("020".equals(rexpCode)) { // ��λ��(ס)
				wrtOffAmt101 = wrtOffAmt101 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt101);
				chargeParm.setData("CHARGE01", arAmtS);
			} else if ("021".equals(rexpCode)) { // ����(ס)
				wrtOffAmt102 = wrtOffAmt102 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt102);
				chargeParm.setData("CHARGE02", arAmtS);
			} else if ("022.01".equals(rexpCode)) { // ������(ס)
				wrtOffAmt103 = wrtOffAmt103 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt103);
				chargeParm.setData("CHARGE03", arAmtS);
			} else if ("022.02".equals(rexpCode)) { // �ǿ�����(ס)
				wrtOffAmt104 = wrtOffAmt104 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt104);
				chargeParm.setData("CHARGE04", arAmtS);
			} else if ("023".equals(rexpCode)) { // �г�ҩ��(ס)
				wrtOffAmt105 = wrtOffAmt105 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt105);
				chargeParm.setData("CHARGE05", arAmtS);
			} else if ("024".equals(rexpCode)) { // �в�ҩ��(ס)
				wrtOffAmt106 = wrtOffAmt106 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt106);
				chargeParm.setData("CHARGE06", arAmtS);
			} else if ("025".equals(rexpCode)) { // ����(ס)
				wrtOffAmt107 = wrtOffAmt107 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt107);
				chargeParm.setData("CHARGE07", arAmtS);
			} else if ("026".equals(rexpCode)) { // ���Ʒ�(ס)
				wrtOffAmt108 = wrtOffAmt108 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt108);
				chargeParm.setData("CHARGE08", arAmtS);
			} else if ("027".equals(rexpCode)) { // �����(ס)
				wrtOffAmt109 = wrtOffAmt109 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt109);
				chargeParm.setData("CHARGE09", arAmtS);
			} else if ("028".equals(rexpCode)) { // ������(ס)
				wrtOffAmt110 = wrtOffAmt110 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt110);
				chargeParm.setData("CHARGE10", arAmtS);
			} else if ("029".equals(rexpCode)) { // �����(ס)
				wrtOffAmt111 = wrtOffAmt111 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt111);
				chargeParm.setData("CHARGE11", arAmtS);
			} else if ("02A".equals(rexpCode)) { // ��Ѫ��(ס)
				wrtOffAmt112 = wrtOffAmt112 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt112);
				chargeParm.setData("CHARGE12", arAmtS);
			} else if ("02B".equals(rexpCode)) { // ������(ס)
				wrtOffAmt113 = wrtOffAmt113 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt113);
				chargeParm.setData("CHARGE13", arAmtS);
			} else if ("02C".equals(rexpCode)) { // ������(ס)
				wrtOffAmt114 = wrtOffAmt114 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt114);
				chargeParm.setData("CHARGE14", arAmtS);
			} else if ("02D".equals(rexpCode)) { // ��λ��
				wrtOffAmt115 = wrtOffAmt115 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt115);
				chargeParm.setData("CHARGE15", arAmtS);
			} else if ("02E".equals(rexpCode)) { // �Ҵ���(ס)
				wrtOffAmt116 = wrtOffAmt116 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt116);
				chargeParm.setData("CHARGE16", arAmtS);
			} else if ("032".equals(rexpCode)) { // CT(ס)
				wrtOffAmt117 = wrtOffAmt117 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt117);
				chargeParm.setData("CHARGE17", arAmtS);
			} else if ("033".equals(rexpCode)) { // MR��ס��
				wrtOffAmt118 = wrtOffAmt118 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt118);
				chargeParm.setData("CHARGE18", arAmtS);
			} else if ("02F".equals(rexpCode)) { // �ԷѲ���(ס)
				wrtOffAmt119 = wrtOffAmt119 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt119);
				chargeParm.setData("CHARGE19", arAmtS);
			}
			// ==zhangp 20120307 modify start
			else if ("035".equals(rexpCode)) { // �ԷѲ���(ס)
				wrtOffAmt120 = wrtOffAmt120 + wrtOffSingle;
				String arAmtS = formatObject.format(wrtOffAmt120);
				chargeParm.setData("CHARGE20", arAmtS);
			}
			// ===zhangp 20120307 modify end
		}
		// parm = getPrintNo(accountSeq);
		// ====zhangp 20120308 modify start
		// charge.setData("CHARGE01",
		// chargeParm.getData("CHARGE01") == null ? 0.00 :
		// chargeParm.getData("CHARGE01"));
		// charge.setData("CHARGE02",
		// chargeParm.getData("CHARGE02") == null ? 0.00 :
		// chargeParm.getData("CHARGE02"));
		// charge.setData("CHARGE03",
		// chargeParm.getData("CHARGE03") == null ? 0.00 :
		// chargeParm.getData("CHARGE03"));
		// charge.setData("CHARGE04",
		// chargeParm.getData("CHARGE04") == null ? 0.00 :
		// chargeParm.getData("CHARGE04"));
		// charge.setData("CHARGE05",
		// chargeParm.getData("CHARGE05") == null ? 0.00 :
		// chargeParm.getData("CHARGE05"));
		// charge.setData("CHARGE06",
		// chargeParm.getData("CHARGE06") == null ? 0.00 :
		// chargeParm.getData("CHARGE06"));
		// charge.setData("CHARGE07",
		// chargeParm.getData("CHARGE07") == null ? 0.00 :
		// chargeParm.getData("CHARGE07"));
		// charge.setData("CHARGE08",
		// chargeParm.getData("CHARGE08") == null ? 0.00 :
		// chargeParm.getData("CHARGE08"));
		// charge.setData("CHARGE09",
		// chargeParm.getData("CHARGE09") == null ? 0.00 :
		// chargeParm.getData("CHARGE09"));
		// charge.setData("CHARGE10",
		// chargeParm.getData("CHARGE10") == null ? 0.00 :
		// chargeParm.getData("CHARGE10"));
		// charge.setData("CHARGE11",
		// chargeParm.getData("CHARGE11") == null ? 0.00 :
		// chargeParm.getData("CHARGE11"));
		// charge.setData("CHARGE12",
		// chargeParm.getData("CHARGE12") == null ? 0.00 :
		// chargeParm.getData("CHARGE12"));
		// charge.setData("CHARGE13",
		// chargeParm.getData("CHARGE13") == null ? 0.00 :
		// chargeParm.getData("CHARGE13"));
		// charge.setData("CHARGE14",
		// chargeParm.getData("CHARGE14") == null ? 0.00 :
		// chargeParm.getData("CHARGE14"));
		// charge.setData("CHARGE15",
		// chargeParm.getData("CHARGE15") == null ? 0.00 :
		// chargeParm.getData("CHARGE15"));
		// charge.setData("CHARGE16",
		// chargeParm.getData("CHARGE16") == null ? 0.00 :
		// chargeParm.getData("CHARGE16"));
		// charge.setData("CHARGE17",
		// chargeParm.getData("CHARGE17") == null ? 0.00 :
		// chargeParm.getData("CHARGE17"));
		// charge.setData("CHARGE18",
		// chargeParm.getData("CHARGE18") == null ? 0.00 :
		// chargeParm.getData("CHARGE18"));
		// charge.setData("CHARGE19",
		// chargeParm.getData("CHARGE19") == null ? 0.00 :
		// chargeParm.getData("CHARGE19"));
		// charge.setData("CHARGE20",
		// chargeParm.getData("CHARGE20") == null ? 0.00 :
		// chargeParm.getData("CHARGE20"));
		// charge.setData("CHARGE00",
		// chargeParm.getDouble("CHARGE03")+chargeParm.getDouble("CHARGE04"));
		// charge.setData("totAmt", formatObject.format(totAmt));
		// ====zhangp 20120308 modify end
		charge.setData("accountSeq", accountSeq);
		// =======zhangp 20120302 modify start
		String stardate = selReceiptNoParm.getData("CHARGE_DATE", 0).toString();
		String enddate = selReceiptNoParm.getData("CHARGE_DATE",
				selReceiptNoParm.getCount() - 1).toString();
		stardate = stardate.substring(0, 19);
		enddate = enddate.substring(0, 19);
		charge.setData("S_DATE", stardate + " �� " + enddate);
		// charge.setData("E_DATE", eDate);
		// ======zhangp 20120302 modify end
		charge.setData("PRINT_DATE", sysDate);
		charge.setData("CASHIER_CODE", accountUser);
		// System.out.println("�ϲ�����"+accountSeq);
		charge.setData("patCount", getPatCount(accountSeq));
		charge.setData("writeOffFee", formatObject
				.format(-getPayCX(accountSeq)));
		charge
				.setData("backWashFee", formatObject
						.format(getPayHC(accountSeq)));
		charge
				.setData("fillFee", formatObject
						.format(getPatFee(accountSeq)[0]));
		charge.setData("returnFee", formatObject
				.format(getPatFee(accountSeq)[1]));
		//====zhangp 20120514 start
		charge.setData("fillFeeCash", formatObject
				.format(getPatFee(accountSeq)[3]));
		charge.setData("fillFeeCheck", formatObject
				.format(getPatFee(accountSeq)[4]));
		charge.setData("returnFeeCash", formatObject
				.format(getPatFee(accountSeq)[5]));
		charge.setData("returnFeeCheck", formatObject
				.format(getPatFee(accountSeq)[6]));
		//====zhangp 20120514 end
		charge.setData("debitFee", "0.00");
		charge.setData("ownFee", formatObject.format(getPatFee(accountSeq)[2]));
		charge.setData("arAmt", formatObject.format(totAmt));
		 String tmp=StringUtil.getInstance().numberToWord(StringTool.getDouble(formatObject.format(totAmt)));
		   if(tmp.lastIndexOf("��")>0){//�з�ȥ������
			   tmp=tmp.substring(0,tmp.lastIndexOf("��")+1);
		   }
		charge.setData("zhAmt", tmp);//modify caoyong 2013722
		//charge.setData("zhAmt", StringUtil.getInstance().numberToWord(totAmt));
		charge.setData("OPT_USER", Operator.getName());
		// ========pangben 2012-3-19 start �޸�ҽ����ʾ
		TParm insParm = getInsParm(accountSeq);
		if (insParm.getErrCode() < 0) {
			return;
		}
		//===zhangp 20120424 start
        double armyAi_amt = insParm.getDouble("ARMYAI_AMT");//����
        double account_pay_amt =  insParm.getDouble("ACCOUNT_PAY_AMT");//�����˻�
        double nhi_comment =  insParm.getDouble("NHI_COMMENT");//����
        double nhi_pay =  insParm.getDouble("NHI_PAY")+armyAi_amt;//ͳ��(��������)
        double ins = armyAi_amt + nhi_comment + nhi_pay;//���ո���
        double pay_cash = insParm.getDouble("OWN_PAY");//�ֽ�֧��/�Ը�
        double illnesssubsidyamt = insParm.getDouble("ILLNESS_SUBSIDY_AMT");//�����
		charge.setData("nhiComment", StringTool.round(nhi_comment, 2));// ���˾���
		charge.setData("illnesssubsidyamt", StringTool.round(illnesssubsidyamt, 2));//�����
		//==============================================================================================
		charge.setData("accountPayAmt", StringTool.round(account_pay_amt, 2));// �����˻�֧��
		//==============================================================================================
		charge.setData("debitFee", StringTool.round(nhi_pay, 2));//ͳ��
		charge.setData("ins_amt",  StringTool.round(nhi_comment+illnesssubsidyamt+
                account_pay_amt+nhi_pay, 2));//ҽ���ϼ�
		//===zhangp 20120424 end
		// ========pangben 2012-3-19 stop
		// charge.setData("PRINT_NO",
		// getPrintNO( (Vector) printNoParm.getData("INV_NO")));
		// System.out.println("�ս�����"+charge);
		TParm print = new TParm();
		// ===zhangp 20120308 modify start
		String sql = " SELECT SUM(PAY_CASH) PAY_CASH ,SUM(PAY_INS_CARD) PAY_INS_CARD"
				+ "   FROM BIL_IBS_RECPM "
				+ "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq
				+ ") "
				+ "    AND REFUND_DATE IS NULL "
				+ "    AND AR_AMT >= 0 ";
		TParm payWayParm = new TParm(TJDODBTool.getInstance().select(sql));
		print.setData("fillFeeCash", "TEXT", "�ֽ�: "
				+ formatObject.format(payWayParm.getDouble("PAY_CASH", 0)));
		print.setData("PAY_INS_CARD", "TEXT", "�ֽ�: "
				+ formatObject.format(payWayParm.getDouble("PAY_INS_CARD", 0)));
		for (int i = 1; i < 21; i++) {
			if (i < 10) {
				print.setData("CHARGE0" + i, "TEXT", chargeParm
						.getData("CHARGE0" + i) == null ? 0.00 : formatObject
						.format(chargeParm.getDouble("CHARGE0" + i)));
			} else {
				print.setData("CHARGE" + i, "TEXT", chargeParm.getData("CHARGE"
						+ i) == null ? 0.00 : formatObject.format(chargeParm
						.getDouble("CHARGE" + i)));
			}
		}
		print.setData("CHARGE00", "TEXT", formatObject.format(chargeParm
				.getDouble("CHARGE03")
				+ chargeParm.getDouble("CHARGE04")));
		print.setData("totAmt", "TEXT", formatObject.format(totAmt));
		// ===zhangp 20120308 modify end
		print.setData("CHARGE", charge.getData());
		// System.out.println("print======="+print);
//		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILAccountTot.jhw", print);
		this.openPrintWindow(IReportTool.getInstance().getReportPath("BILAccountTot.jhw"),
                             IReportTool.getInstance().getReportParm("BILAccountTot.class", print));//����ϲ�modify by wanglong 20130730
		// this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILAccountSum.jhw",
		// print);
	}

	/**
	 * �������ϱ��ӡ����
	 *
	 * @param accountSeq
	 *            String
	 * @return TParm
	 */
	private TParm getPrintCancelTableDate(String accountSeq) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm parmData = new TParm();
		// parmData =
		String selMrNo = " SELECT RECEIPT_NO,AR_AMT FROM BIL_INVRCP WHERE RECP_TYPE = 'IBS' AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") AND STATUS = '0' ";
		parmData = new TParm(TJDODBTool.getInstance().select(selMrNo));
		int count = parmData.getCount("RECEIPT_NO");
		TParm aparm = new TParm();
		// ��������ʾ�㷨
		int row = 0;
		int column = 0;
		for (int i = 0; i < count; i++) {

			aparm.addData("RECEIPT_NO_" + column, parmData.getData(
					"RECEIPT_NO", i));
			aparm.addData("AR_AMT_" + column, df.format(parmData.getDouble(
					"AR_AMT", i)));
			column++;
			if (column == 2) {
				column = 0;
				row++;
			}
		}
		aparm.setCount(row);
		// this.messageBox_("��������"+aparm);
		TParm printData = new TParm(); // ��ӡ����
		printData.setCount(row);
		printData = aparm;
		printData.addData("SYSTEM", "COLUMNS", "RECEIPT_NO_0");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
		printData.addData("SYSTEM", "COLUMNS", "RECEIPT_NO_1");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
		// System.out.println("��ӡ2����"+printData);
		return printData;
	}

	/**
	 * �����˷Ѵ�ӡ����
	 *
	 * @param accountSeq
	 *            String
	 * @return TParm
	 */
	private TParm getPrintReturnTableDate(String accountSeq) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm parmData = new TParm();
		// parmData =
		String selMrNo = " SELECT RECEIPT_NO,AR_AMT FROM BIL_INVRCP WHERE RECP_TYPE = 'IBS' AND ACCOUNT_SEQ IN ("
				+ accountSeq + ") AND CANCEL_FLG = '1' ";
		parmData = new TParm(TJDODBTool.getInstance().select(selMrNo));
		int count = parmData.getCount("RECEIPT_NO");
		TParm aparm = new TParm();
		// ��������ʾ�㷨
		int row = 0;
		int column = 0;
		for (int i = 0; i < count; i++) {

			aparm.addData("RECEIPT_NO_" + column, parmData.getData(
					"RECEIPT_NO", i));
			aparm.addData("AR_AMT_" + column, df.format(parmData.getDouble(
					"AR_AMT", i)));
			column++;
			if (column == 2) {
				column = 0;
				row++;
			}
		}
		aparm.setCount(row);
		// this.messageBox_("�˷�����"+aparm);
		TParm printData = new TParm(); // ��ӡ����
		printData.setCount(row);
		printData = aparm;
		printData.addData("SYSTEM", "COLUMNS", "RECEIPT_NO_0");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
		printData.addData("SYSTEM", "COLUMNS", "RECEIPT_NO_1");
		printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
		// System.out.println("��ӡ1����"+printData);
		return printData;
	}

	/**
	 * �õ�����ʱ���
	 *
	 * @return String
	 */
	public String getAccountDate() {
		String accountDate = "";
		TParm accountDateParm = new TParm();
		accountDateParm = BILSysParmTool.getInstance().getDayCycle("I");
		accountDate = accountDateParm.getValue("DAY_CYCLE", 0);
		return accountDate;
	}

	/**
	 * table����checkBox�¼�
	 *
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableComponent(Object obj) {
		accountSeq = new String();
		accountSeqParm = new TParm();
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int allRow = table.getRowCount();
		StringBuffer allSeq = new StringBuffer();
		String cashierCode = "";
		for (int i = 0; i < allRow; i++) {
			String seq = "";
			if ("Y".equals(tableParm.getValue("FLG", i))) {
				seq = tableParm.getValue("ACCOUNT_SEQ", i);
				cashierCode = tableParm.getValue("ACCOUNT_USER", i);
				if (allSeq.length() > 0)
					allSeq.append(",");
				allSeq.append(seq);
				accountSeqParm.addData("ACCOUNT_SEQ", seq);
				accountSeqParm.addData("ACCOUNT_USER", cashierCode);
			}
		}
		accountSeq = allSeq.toString();
		// this.messageBox_("�ս��" + accountSeq);
		return true;
	}

	/**
	 * ȫѡ�¼�
	 */
	public void setAllFlg() {
		accountSeq = new String();
		accountSeqParm = new TParm();
		TTable table = (TTable) this.getComponent("Table");
		String select = getValueString("ALL_FLG");
		TParm parm = table.getParmValue();
		if (parm != null) {
			int count = parm.getCount("FLG");
			StringBuffer allSeq = new StringBuffer();
			String cashierCode = "";
			for (int i = 0; i < count; i++) {
				String seq = "";
				parm.setData("FLG", i, select);
				if ("Y".equals(select)) {
					seq = parm.getValue("ACCOUNT_SEQ", i);
					cashierCode = parm.getValue("ACCOUNT_USER", i);
					if (allSeq.length() > 0)
						allSeq.append(",");
					allSeq.append(seq);
					accountSeqParm.addData("ACCOUNT_SEQ", seq);
					accountSeqParm.addData("ACCOUNT_USER", cashierCode);
				}
			}
			accountSeq = allSeq.toString();
			table.setParmValue(parm);

		}
	}

	/**
	 * �ж�Ʊ������
	 *
	 * @param vote
	 *            Vector
	 * @return Vector @ othour fudw
	 */
	public Vector seats(Vector vote) {
		long[] voteno = new long[vote.size()];
		long p;
		int delete = 0;
		for (int t = 0; t < vote.size(); t++) {
			String invNo = (String) vote.get(t);
			if (invNo == null || invNo.length() <= 0) {
				delete++;
				continue;
			}
			p = Long.parseLong((String) vote.get(t));
			voteno[t] = p;
		}
		// ����
		long temp;
		for (int i = 0; i < voteno.length; i++) {
			for (int j = 0; j < voteno.length - 1; j++) {
				if (voteno[j + 1] < voteno[j]) {
					temp = voteno[j + 1];
					voteno[j + 1] = voteno[j];
					voteno[j] = temp;
				}
			}
		}
		// ������
		Vector result = new Vector();
		// ѭ����������
		for (int i = 0; i < vote.size();) {
			// ������ʼƱ��voteNO[0]�ͽ���Ʊ��voteNO[1]
			String[] voteNO = new String[2];
			// �õ���ǰƱ��
			long no = voteno[i];
			// �洢��ʼƱ��
			voteNO[0] = "" + no;
			for (int s = i + 1; s < vote.size(); s++, i++) {
				no++;
				if (voteno[s] == no)
					continue;
				no--;
				break;
			}
			i++;
			// �洢����Ʊ��
			voteNO[1] = "" + no;
			result.add(voteNO);
		}
		return result;
	}

	/**
	 * �õ���������
	 *
	 * @param accountSeq
	 *            String
	 * @return int
	 */
	public int getPatCount(String accountSeq) {
		String selRecp = " SELECT DISTINCT(CASE_NO) COUNT "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		int count = selRecpParm.getCount("COUNT");
		return count;
	}

	/**
	 * �õ����������ܼ�
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPatFeeB(String accountSeq) {
		String selRecp = " SELECT SUM(PAY_CASH) PAY_CASH "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") " + "    AND REFUND_DATE IS NULL "
				+ "    AND PAY_CASH >0 ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double bFee = selRecpParm.getDouble("PAY_CASH", 0);
		return bFee;
	}

	/**
	 * �õ������˷��ܼ�
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPatFeeT(String accountSeq) {
		String selRecp = " SELECT SUM(PAY_CASH) PAY_CASH "
				+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
				+ accountSeq + ") " + "    AND REFUND_DATE IS NULL "
				+ "    AND PAY_CASH <0 ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PAY_CASH", 0);
		return tFee;
	}

	/**
	 * �õ�Ԥ����������
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPayCX(String accountSeq) {
		String selRecp =
                          " SELECT SUM(PRE_AMT) AS PRE_AMT FROM BIL_PAY "+
                          "  WHERE RESET_RECP_NO IN( SELECT RECEIPT_NO  "+
                          "                            FROM BIL_IBS_RECPM "+
                          "                           WHERE ACCOUNT_SEQ IN (" + accountSeq + ") " +
//                          "                                 AND REFUND_DATE IS NULL) "+
                          "                                 ) "+
                          "    AND TRANSACT_TYPE = '03' ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PRE_AMT", 0);
		return tFee;
	}

	/**
	 * �õ�Ԥ����س���
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double getPayHC(String accountSeq) {
		String selRecp =
                          " SELECT SUM(PRE_AMT) AS PRE_AMT FROM BIL_PAY " +
                          "  WHERE RESET_RECP_NO IN( SELECT RECEIPT_NO "+
                          "                            FROM BIL_IBS_RECPM "+
                          "                           WHERE ACCOUNT_SEQ IN ("+ accountSeq+ ") "+
//                          "                             AND REFUND_DATE IS NULL) "+
                          "                             ) "+
                          "    AND TRANSACT_TYPE = '04' ";
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpParm = new TParm(TJDODBTool.getInstance().select(selRecp));
		double tFee = selRecpParm.getDouble("PRE_AMT", 0);
		return tFee;
	}

	/**
	 * �õ����˿���Ϣ
	 *
	 * @param accountSeq
	 *            String
	 * @return double
	 */
	public double[] getPatFee(String accountSeq) {
		String selRecpAr =
		// ===zhangp 20120319 start
		// " SELECT SUM(AR_AMT) AR_AMT " +
		// "   FROM BIL_IBS_RECPM " +
		// "  WHERE ACCOUNT_SEQ IN (" + accountSeq + ") " +
		// "    AND REFUND_DATE IS NULL " +
		// "    AND AR_AMT >= 0 ";
			//===zhangp 20120424 start
//		"SELECT SUM (AR_AMT - PAY_BILPAY) AS SUM" + " FROM BIL_IBS_RECPM"
//				+ " WHERE REFUND_DATE IS NULL" + " AND AR_AMT >= 0"
//				+ " AND PAY_BILPAY >= 0" + " AND AR_AMT >= PAY_BILPAY"
//				+ " AND ACCOUNT_SEQ IN (" + accountSeq + ")";
		"SELECT SUM (  CASE" +
		" WHEN PAY_CASH IS NULL" +
		" THEN 0" +
		" ELSE PAY_CASH" +
		" END" +
		" + CASE" +
		" WHEN PAY_CHECK IS NULL" +
		" THEN 0" +
		" ELSE PAY_CHECK" +
		" END" +
		//===zhangp 20120604 start
		" + CASE" +
		" WHEN PAY_BANK_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_BANK_CARD" +
		" END" +
		//===zhangp 20120604 end
		" ) AS SUM " +
		//===zhangp 20120514 start
		", SUM( CASE" +
		" WHEN PAY_CASH IS NULL" +
		" THEN 0" +
		" ELSE PAY_CASH" +
		" END ) PAY_CASH ," +
		" SUM( CASE" +
		" WHEN PAY_CHECK IS NULL" +
		" THEN 0" +
		" ELSE PAY_CHECK" +
		//===zhangp 20120604 start
//		" END ) PAY_CHECK " +
		" END " +
		" + CASE" +
		" WHEN PAY_BANK_CARD IS NULL" +
		" THEN 0" +
		" ELSE PAY_BANK_CARD " +
		" END ) PAY_CHECK " +
		//===zhangp 20120604 end
		//===zhangp 20120514 end
		" FROM BIL_IBS_RECPM" +
		//===zhangp 20120614 start
//		" WHERE REFUND_DATE IS NULL" +
//		" AND AR_AMT >= 0" +
		" WHERE AR_AMT >= 0" +
		//===zhangp 20120614 end
		" AND   CASE" +
		" WHEN OWN_AMT IS NULL" +
		" THEN 0" +
		" ELSE OWN_AMT" +
		" END" +
		" - CASE" +
		" WHEN PAY_BILPAY IS NULL" +
		" THEN 0" +
		" ELSE PAY_BILPAY" +
		" END >= 0" +
		" AND CASE" +
		" WHEN PAY_BILPAY IS NULL" +
		" THEN 0" +
		" ELSE PAY_BILPAY" +
		" END >= 0" +
		" AND ACCOUNT_SEQ IN ("+accountSeq+")";
		//===zhangp 20120424 end 
		// ===zhangp 20120319 end
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpArParm = new TParm(TJDODBTool.getInstance().select(
				selRecpAr));
		// ===zhangp 20120319 start
		// double aFee = selRecpArParm.getDouble("AR_AMT", 0);
		double aFee = selRecpArParm.getDouble("SUM", 0);
		// ====zhangp 20120319 end
		//==zhangp 20120514 start
		double acsFee = selRecpArParm.getDouble("PAY_CASH", 0);
		double ackFee = selRecpArParm.getDouble("PAY_CHECK", 0);
		//==zhangp 20120514 end
		String selRecpPPay =
		// ====zhangp 20120319 start
		// " SELECT SUM(PAY_BILPAY) PAY_BILPAY " +
		// "   FROM BIL_IBS_RECPM " +
		// "  WHERE ACCOUNT_SEQ IN (" + accountSeq + ") " +
		// "    AND REFUND_DATE IS NULL " +
		// "    AND AR_AMT >= 0 ";
			"SELECT SUM (  CASE" +
			" WHEN PAY_CASH IS NULL" +
			" THEN 0" +
			" ELSE PAY_CASH" +
			" END" +
			" + CASE" +
			" WHEN PAY_CHECK IS NULL" +
			" THEN 0" +
			" ELSE PAY_CHECK" +
			" END" +
			//===zhangp 20120604 start
			" + CASE" +
			" WHEN PAY_BANK_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_BANK_CARD" +
			" END" +
			//===zhangp 20120604 end
			" ) AS SUM" +
			//===zhangp 20120514 start
			", SUM( CASE" +
			" WHEN PAY_CASH IS NULL" +
			" THEN 0" +
			" ELSE PAY_CASH" +
			" END ) PAY_CASH ," +
			" SUM( CASE" +
			" WHEN PAY_CHECK IS NULL" +
			" THEN 0" +
			" ELSE PAY_CHECK" +
			//===zhangp 20120604 start
//			" END ) PAY_CHECK " +
			" END " +
			" + CASE" +
			" WHEN PAY_BANK_CARD IS NULL" +
			" THEN 0" +
			" ELSE PAY_BANK_CARD " +
			" END ) PAY_CHECK " +
			//===zhangp 20120604 end
			//===zhangp 20120514 end
			" FROM BIL_IBS_RECPM" +
			//===zhangp 20120614 start
//			" WHERE REFUND_DATE IS NULL" +
//			" AND AR_AMT >= 0" +
			" WHERE AR_AMT >= 0" +
			//===zhangp 20120614 end
			" AND   CASE" +
			" WHEN OWN_AMT IS NULL" +
			" THEN 0" +
			" ELSE OWN_AMT" +
			" END" +
			" - CASE" +
			" WHEN PAY_BILPAY IS NULL" +
			" THEN 0" +
			" ELSE PAY_BILPAY" +
			" END < 0" +
			" AND CASE" +
			" WHEN PAY_BILPAY IS NULL" +
			" THEN 0" +
			" ELSE PAY_BILPAY" +
			" END >= 0" +
			" AND ACCOUNT_SEQ IN ("+accountSeq+")";
		// ===zhangp 20120319 end
		// ��ѯ��֧ͬ����ʽ������(�ս���)
		TParm selRecpPayPParm = new TParm(TJDODBTool.getInstance().select(
				selRecpPPay));
		// ===zhangp 20120319 start
		// double pFee = selRecpPayPParm.getDouble("PAY_BILPAY", 0);
		double pFee = selRecpPayPParm.getDouble("SUM", 0);
		//===zhangp 20120514 start
		double pcsFee = selRecpPayPParm.getDouble("PAY_CASH", 0);
		double pckFee = selRecpPayPParm.getDouble("PAY_CHECK", 0);
		//===zhangp 20120514 end
		double[] bFee = new double[7];
		// if (aFee - pFee > 0) {
		// bFee[0] = aFee - pFee;
		// bFee[1] = 0.00;
		// }
		// else {
		// bFee[0] = 0.00;
		// bFee[1] = -(aFee - pFee);
		// }
		bFee[0] = aFee;
		bFee[1] = pFee;
		// ===zhangp 20120319 end
		//==zhangp 20120424 start
		String ownSql =
			"SELECT SUM(OWN_AMT) OWN_AMT FROM BIL_IBS_RECPM WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
		TParm ownParm = new TParm(TJDODBTool.getInstance().select(ownSql));
		double oFee = ownParm.getDouble("OWN_AMT", 0);
		bFee[2] = oFee;
		//==zhangp 20120424 end
		//=====zhangp 20120514 start
		bFee[3] = acsFee;
		bFee[4] = ackFee;
		bFee[5] = pcsFee;
		bFee[6] = pckFee;
		//=====zhangp 20120514 end
		return bFee;
	}
	 /**
     * ҽ����ϸ��ӡ
     */
    public void onDetailPrint() {
		if (accountSeqParm.getCount("ACCOUNT_SEQ") <= 0) {
			messageBox("��ѡ���ӡ����!");
			return;
		}
		  TParm printData = new TParm();
			//���ҽ����ϸ        
        	printData = getInsDetailPrint(accountSeq);      	
            //��ͷ
            printData.setData("TITLE", "TEXT","סԺҽ����ϸ��");
           
            //��ӡ����
            String printDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                                 "yyyy-MM-dd HH:mm:ss");
            printData.setData("PRINTDATE","TEXT",printDate);
            
            //�շ�Ա
            printData.setData("USER","TEXT",Operator.getName());
            if (printData == null)
                return;
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\bil\\BILINSDetailPrint.jhw", printData);
			
		}
    /**
     *�õ�ҽ����ϸ
     * @param tableParm TParm
     * @return TParm
     */
    public TParm getInsDetailPrint(String accountSeq) {
   	 TParm returnParm = new TParm();
   	 //�ǵ�����
    String sql = " SELECT DISTINCT B.ARMYAI_AMT,( CASE WHEN TOT_PUBMANADD_AMT IS NULL" + 
    " THEN 0 ELSE TOT_PUBMANADD_AMT END + B.NHI_PAY ) NHI_PAY, B.NHI_COMMENT," + 
    " (CASE WHEN B.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE B.ACCOUNT_PAY_AMT END )" +
    " ACCOUNT_PAY_AMT,B.ILLNESS_SUBSIDY_AMT,A.SPECIAL_PAT," +
    " D.INS_CROWD_TYPE,B.CASE_NO,B.INS_CROWD_TYPE AS INS_TYPE " +
    " FROM INS_ADM_CONFIRM A, INS_IBS B ,BIL_IBS_RECPM C,SYS_CTZ D " +
    " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
    " AND A.CONFIRM_NO =  B.CONFIRM_NO" +  
    " AND B.CASE_NO =  C.CASE_NO " +
    " AND A.HIS_CTZ_CODE = D.CTZ_CODE" +
    " AND A.SDISEASE_CODE IS  NULL" +
    " AND B.RECEIPT_NO = C.RECEIPT_NO" + 
    " AND C.ACCOUNT_SEQ IN ("+accountSeq+")";
	//System.out.println("sql======"+sql);
	 TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
     double CZaccountpayamt =  0;//�����˻���ְ������أ�
     double CXaccountpayamt =  0;//�����˻�����
     double nhicomment = 0;//����
     double CZnhipay = 0;//ҽ�������ְ������أ�
     double CXnhipay = 0;//ҽ���������
     double illnesssubsidyamt = 0;//�����
     double servantarmyaiamt = 0;//����Ա����
     double soldierarmyaiamt = 0;//���в���
     double civilarmyaiamt = 0;//��������
     double specialarmyaiamt = 0;//�����Ÿ�
     double otherarmyaiamt = 0;//������������أ�
     double insallamt = 0;//ҽ���ϼ�
     double insallamtnosdisease = 0;//�ǵ�����ҽ���ϼ�
     double insallamtsdisease = 0;//������ҽ���ϼ�
     double Anotherplacepay = 0;//���ͳ��֧��
     double CZnhipayall = 0;//ҽ�������ְ�����+����أ�
     double CZaccountpayamtall = 0;//�����˻���ְ�����+����أ�
     double CZnhipayotherplace = 0;//ҽ�������ְ����أ�
     double CZaccountpayamtotherplace = 0;//�����˻���ְ����أ�
     if(insParm.getCount()>0){
    	 for (int i = 0; i < insParm.getCount(); i++) {
    		 	if(insParm.getValue("INS_CROWD_TYPE", i).equals("1")){
    		 	//ҽ�������ְ�����+����أ�
    		 	CZnhipayall += insParm.getDouble("NHI_PAY", i);
    		 	//�����˻���ְ�����+����أ�
    		 	CZaccountpayamtall += insParm.getDouble("ACCOUNT_PAY_AMT", i);
    		 	if(insParm.getData("INS_TYPE", i).equals("3")){
    		 	//ҽ�������ְ����أ�
    		 	CZnhipayotherplace += insParm.getDouble("NHI_PAY",i);
    		 	//�����˻���ְ����أ�
    		 	CZaccountpayamtotherplace += insParm.getDouble("ACCOUNT_PAY_AMT",i);
    		 	}
    		 	}
    		 	if(insParm.getValue("INS_CROWD_TYPE", i).equals("2")){	
     			//ҽ������(ͳ��)	
     			CXnhipay +=  insParm.getDouble("NHI_PAY",i);
     			//�����˻�
     			CXaccountpayamt +=  insParm.getDouble("ACCOUNT_PAY_AMT",i);
     	 		}
    		 	//����
				nhicomment +=  insParm.getDouble("NHI_COMMENT",i);
				//�����
				illnesssubsidyamt += insParm.getDouble("ILLNESS_SUBSIDY_AMT",i);
				
				//����Ա����
 				if(insParm.getData("SPECIAL_PAT", i).equals("06"))
 				servantarmyaiamt += insParm.getDouble("ARMYAI_AMT", i);
 				//���в���
 				else if(insParm.getData("SPECIAL_PAT", i).equals("04"))
 				soldierarmyaiamt += insParm.getDouble("ARMYAI_AMT", i);
 				//��������
 				else if(insParm.getData("SPECIAL_PAT", i).equals("07"))
 				civilarmyaiamt += insParm.getDouble("ARMYAI_AMT", i);
 				//�����Ÿ�
 				else if(insParm.getData("SPECIAL_PAT", i).equals("08"))
 				specialarmyaiamt += insParm.getDouble("ARMYAI_AMT", i);
 				//������������أ�
 				else if(insParm.getValue("SPECIAL_PAT", i).length()==0){
 				if(insParm.getData("INS_TYPE", i).equals("3"))
 				otherarmyaiamt += insParm.getDouble("ARMYAI_AMT", i);	
 				}							
 															
    	 }
    	 //���ͳ��֧��=������������أ�+ҽ�������ְ����أ�+�����˻���ְ����أ�
    	 Anotherplacepay = otherarmyaiamt+CZnhipayotherplace+CZaccountpayamtotherplace;
    	 
    	 //ҽ�������ְ������أ�
    	 CZnhipay = CZnhipayall-CZnhipayotherplace;
    	 
    	 //�����˻���ְ������أ�
    	 CZaccountpayamt = CZaccountpayamtall-CZaccountpayamtotherplace;
    	 
    	 //�ǵ�����ҽ���ϼ�
    	 insallamtnosdisease = servantarmyaiamt+soldierarmyaiamt+civilarmyaiamt+
    	                       specialarmyaiamt+Anotherplacepay+CZnhipay+CZaccountpayamt+
    	                       CXnhipay+CXaccountpayamt+nhicomment+illnesssubsidyamt;
    	                       
     }
	//������
	String sql1 = "SELECT DISTINCT B.ARMYAI_AMT,( CASE WHEN TOT_PUBMANADD_AMT IS NULL" +	
    " THEN 0 ELSE TOT_PUBMANADD_AMT END + B.NHI_PAY + B.SINGLE_STANDARD_OWN_AMT - " +
    " B.SINGLE_SUPPLYING_AMT) NHI_PAY,B.NHI_COMMENT," +
    " (CASE WHEN B.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE B.ACCOUNT_PAY_AMT END )" +
    " ACCOUNT_PAY_AMT,B.ILLNESS_SUBSIDY_AMT,A.SPECIAL_PAT,B.CASE_NO,D.INS_CROWD_TYPE" +
    " FROM INS_ADM_CONFIRM A, INS_IBS B,BIL_IBS_RECPM C,SYS_CTZ D" +
    " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
    " AND A.CONFIRM_NO =  B.CONFIRM_NO" +  
    " AND B.CASE_NO =  C.CASE_NO " +
    " AND A.HIS_CTZ_CODE = D.CTZ_CODE" +
    " AND A.SDISEASE_CODE IS NOT NULL" +
    " AND B.RECEIPT_NO = C.RECEIPT_NO" + 
    " AND C.ACCOUNT_SEQ IN ("+accountSeq+")";
	//System.out.println("sql1======"+sql1);
	TParm insParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
	 //double accountpayamtdisease =  0;
     //double nhipaydisease = 0;
     double illnesssubsidyamtdisease = 0;//�����
     //double armyaiamtdisease = 0;
     double CZnhipaydisease = 0;//������ ��ְ ͳ��
     double CZaccountpayamtdisease = 0;//������ ��ְ �����˻�
     double CZservantarmyaiamt = 0;//������ ��ְ ����Ա����
     double CZsoldierarmyaiamt = 0;//������ ��ְ ���в���
     double CZcivilarmyaiamt = 0;//������ ��ְ ��������
     double CZspecialarmyaiamt = 0;//������ ��ְ �����Ÿ�
     double CZnhicommentdisease = 0;//������ ��ְ ����
     
     double CXnhipaydisease = 0;//������ ���� ͳ��
     double CXaccountpayamtdisease = 0;//������ ���� �����˻�
     double CXservantarmyaiamt = 0;//������ ���� ����Ա����
     double CXsoldierarmyaiamt = 0;//������ ���� ���в���
     double CXcivilarmyaiamt = 0;//������ ���� ��������
     double CXspecialarmyaiamt = 0;//������ ���� �����Ÿ� 
     double CXnhicommentdisease = 0;//������ ���� ����
	if(insParm1.getCount()>0){
   	 for (int i = 0; i < insParm1.getCount(); i++) {
   		if(insParm1.getValue("INS_CROWD_TYPE", i).equals("1")){
   		//������ ��ְ ͳ��
   		CZnhipaydisease +=  insParm1.getDouble("NHI_PAY",i);
   		//������ ��ְ �����˻�
   		CZaccountpayamtdisease +=  insParm1.getDouble("ACCOUNT_PAY_AMT",i);
   		//������ ��ְ ����
   		CZnhicommentdisease +=  insParm1.getDouble("NHI_COMMENT",i);
   		//������ ��ְ ����Ա����
   		if(insParm1.getData("SPECIAL_PAT", i).equals("06"))
   		CZservantarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   		//������ ��ְ ���в���
   		else if(insParm1.getData("SPECIAL_PAT", i).equals("04"))
   		CZsoldierarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   		//������ ��ְ ��������
   		else if(insParm1.getData("SPECIAL_PAT", i).equals("07"))
   		CZcivilarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   		//������ ��ְ �����Ÿ�
   		else if(insParm1.getData("SPECIAL_PAT", i).equals("08"))
   		CZspecialarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   		}
   		
   		if(insParm1.getValue("INS_CROWD_TYPE", i).equals("2")){
   	   	//������ ���� ͳ��
   		CXnhipaydisease +=  insParm1.getDouble("NHI_PAY",i);
   	   	//������ ���� �����˻�
   		CXaccountpayamtdisease +=  insParm1.getDouble("ACCOUNT_PAY_AMT",i);
   		//������ ���� ����
   		CXnhicommentdisease +=  insParm1.getDouble("NHI_COMMENT",i);
   	   	//������ ���� ����Ա����
   	   	if(insParm1.getData("SPECIAL_PAT", i).equals("06"))
   	   	CXservantarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   	   	//������ ���� ���в���
   	   	else if(insParm1.getData("SPECIAL_PAT", i).equals("04"))
   	   	CXsoldierarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   	   	//������ ���� ��������
   	   	else if(insParm1.getData("SPECIAL_PAT", i).equals("07"))
   	   	CXcivilarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   	   	//������ ���� �����Ÿ�
   	   	else if(insParm1.getData("SPECIAL_PAT", i).equals("08"))
   	   	CXspecialarmyaiamt += insParm1.getDouble("ARMYAI_AMT", i);
   	   	}
		//�����
		illnesssubsidyamtdisease += insParm1.getDouble("ILLNESS_SUBSIDY_AMT",i); 
		//����
		//nhicommentdisease +=  insParm1.getDouble("NHI_COMMENT",i);	
   	 }
   //������ҽ���ϼ�
   	insallamtsdisease =	CZnhipaydisease+CZaccountpayamtdisease+CZservantarmyaiamt+CZsoldierarmyaiamt+
   						CZcivilarmyaiamt+CZspecialarmyaiamt+CXnhipaydisease+CXaccountpayamtdisease+
   						CXservantarmyaiamt+CXsoldierarmyaiamt+CXcivilarmyaiamt+CXspecialarmyaiamt+
   						illnesssubsidyamtdisease+CZnhicommentdisease+CXnhicommentdisease;
   	 }
	//��ũ��ҽ��֧��
	double xnh = 0.00;
	String sql2 = "SELECT A.REAL_INS_AMT FROM INS_XNH A,BIL_IBS_RECPM B"
				+ " WHERE B.ACCOUNT_SEQ IN ("+accountSeq+") AND B.CASE_NO = A.CASE_NO";
	TParm xnhparm = new TParm(TJDODBTool.getInstance().select(sql2));
	if(xnhparm.getCount()>0){
		for (int i = 0; i < xnhparm.getCount(); i++) {
			xnh += xnhparm.getDouble("REAL_INS_AMT", i);
		}
	}
	//ҽ���ϼ�
	insallamt =insallamtnosdisease+insallamtsdisease+xnh;
		
	returnParm.setData("CZ_NHI_PAY", "TEXT", StringTool.round(CZnhipay,2)); //ҽ�������ְ������أ�
	returnParm.setData("CX_NHI_PAY", "TEXT", StringTool.round(CXnhipay,2));//ҽ���������
	returnParm.setData("NHI_COMMENT", "TEXT", StringTool.round(nhicomment,2));//����
	returnParm.setData("CZ_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(CZaccountpayamt,2));//�����˻���ְ������أ�
	returnParm.setData("CX_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(CXaccountpayamt,2));//�����˻� ����
    returnParm.setData("ILLNESS_SUBSIDY_AMT", "TEXT", StringTool.round(illnesssubsidyamt,2));//�����
	returnParm.setData("SERVANT_ARMY_AI_AMT", "TEXT", StringTool.round(servantarmyaiamt,2));//����Ա����
	returnParm.setData("SOLDIER_ARMY_AI_AMT", "TEXT", StringTool.round(soldierarmyaiamt,2));//���в���
	returnParm.setData("CIVIL_ARMY_AI_AMT", "TEXT", StringTool.round(civilarmyaiamt,2));//��������
	returnParm.setData("SPECIAL_ARMY_AI_AMT", "TEXT", StringTool.round(specialarmyaiamt,2));//�����Ÿ�
	returnParm.setData("ANOTHER_PLACE_PAY", "TEXT", StringTool.round(Anotherplacepay,2));//���ͳ��֧��
	//returnParm.setData("SINGLE_NHI_PAY", "TEXT", StringTool.round(nhipaydisease,2));
	//returnParm.setData("SINGLE_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(accountpayamtdisease,2));
	returnParm.setData("SINGLE_ILLNESS_SUBSIDY_AMT", "TEXT", StringTool.round(illnesssubsidyamtdisease,2));//������ �����	
	//returnParm.setData("SINGLE_NHI_COMMENT", "TEXT", StringTool.round(nhicommentdisease,2));//����	
	//returnParm.setData("SINGLE_ARMY_AI_AMT", "TEXT", StringTool.round(armyaiamtdisease,2));	//����
	
	returnParm.setData("CZ_SINGLE_NHI_PAY", "TEXT", StringTool.round(CZnhipaydisease,2));//������ ��ְ ͳ��
	returnParm.setData("CZ_SINGLE_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(CZaccountpayamtdisease,2));//������ ��ְ �����˻�
	returnParm.setData("CZ_SERVANT_ARMY_AI_AMT", "TEXT", StringTool.round(CZservantarmyaiamt,2));//������ ��ְ ����Ա����
	returnParm.setData("CZ_SOLDIER_ARMY_AI_AMT", "TEXT", StringTool.round(CZsoldierarmyaiamt,2));//������ ��ְ ���в���
	returnParm.setData("CZ_CIVIL_ARMY_AI_AMT", "TEXT", StringTool.round(CZcivilarmyaiamt,2));//������ ���� ��������
	returnParm.setData("CZ_SPECIAL_ARMY_AI_AMT", "TEXT", StringTool.round(CZspecialarmyaiamt,2));//������ ��ְ �����Ÿ�
	
	returnParm.setData("CX_SINGLE_NHI_PAY", "TEXT", StringTool.round(CXnhipaydisease,2));//������ ���� ͳ��
	returnParm.setData("CX_SINGLE_ACCOUNT_PAY_AMT", "TEXT", StringTool.round(CXaccountpayamtdisease,2));//������ ���� �����˻�
	returnParm.setData("CX_SERVANT_ARMY_AI_AMT", "TEXT", StringTool.round(CXservantarmyaiamt,2));//������ ���� ����Ա����
	returnParm.setData("CX_SOLDIER_ARMY_AI_AMT", "TEXT", StringTool.round(CXsoldierarmyaiamt,2));//������ ���� ���в���
	returnParm.setData("CX_CIVIL_ARMY_AI_AMT", "TEXT", StringTool.round(CXcivilarmyaiamt,2));//������ ���� ��������
	returnParm.setData("CX_SPECIAL_ARMY_AI_AMT", "TEXT", StringTool.round(CXspecialarmyaiamt,2));//������ ���� �����Ÿ�
	
	returnParm.setData("SINGLE_DISEASE_AMT", "TEXT", StringTool.round(insallamtsdisease,2));//������ҽ���ϼ�
	returnParm.setData("INS_ALL_AMT", "TEXT", StringTool.round(insallamt,2));//ҽ���ϼ�
	returnParm.setData("XNH_PAY", "TEXT", StringTool.round(xnh,2));//��ũ��
	
	returnParm.setData("CZ_NHI_COMMENT", "TEXT", StringTool.round(CZnhicommentdisease,2));//��ְ����
	returnParm.setData("CX_NHI_COMMENT", "TEXT", StringTool.round(CXnhicommentdisease,2));//�������
	
	//��������
	String selReceiptNo = " SELECT RECEIPT_NO,CHARGE_DATE,CASE_NO "
		+ "   FROM BIL_IBS_RECPM " + "  WHERE ACCOUNT_SEQ IN ("
		+ accountSeq + ") "
		+ "  ORDER BY RECEIPT_NO,CHARGE_DATE";
    TParm selReceiptNoParm = new TParm(TJDODBTool.getInstance().select(
		selReceiptNo));
    String stardate = selReceiptNoParm.getData("CHARGE_DATE", 0).toString();
	String enddate = selReceiptNoParm.getData("CHARGE_DATE",
			selReceiptNoParm.getCount() - 1).toString();
	stardate = stardate.substring(0, 19);
	enddate = enddate.substring(0, 19);
	returnParm.setData("ACCOUNTDATE", "TEXT",stardate + " �� " + enddate);
   	 return returnParm; 
    }
    
}
