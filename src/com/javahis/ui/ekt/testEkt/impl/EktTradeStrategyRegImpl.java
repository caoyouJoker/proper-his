package com.javahis.ui.ekt.testEkt.impl;

import jdo.bil.BIL;
import jdo.ekt.EKTIO;
import jdo.ins.INSTJReg;
import jdo.reg.PatAdmTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktRegOrOpbClient;
import com.javahis.ui.ekt.testEkt.IEktTradeStrategy;
import com.javahis.ui.reg.REGPatAdmControl;

public class EktTradeStrategyRegImpl implements IEktTradeStrategy {
	
	private EktParam ektParam;
	private Reg reg;
	private Pat pat;
	public REGPatAdmControl regPatAdmControl;
	private EktRegOrOpbClient ektRegOrOpbClient;
	
	
	public EktTradeStrategyRegImpl(EktParam ektParam){
		this.ektParam = ektParam;
		regPatAdmControl = ektParam.getRegPatAdmControl();
		ektParam.settControl(regPatAdmControl);
		ektRegOrOpbClient = new EktRegOrOpbClient();
	}
	
	
	

	@Override
	public <T> EktParam creatParam(T t) {
		reg = (Reg) t;
		reg.setEktSql(null);
		if (reg == null) {
			return null;
		}
		
		String payWay = ektParam.getOrderParm().getValue("payWay");
		String caseNo = ektParam.getOrderParm().getValue("caseNo");
		pat = ektParam.getPat();

		// ҽ�ƿ�֧��
		if (payWay.equals("PAY_MEDICAL_CARD")) {
			// ����CASE_NO ��Ϊҽ�ƿ���ҪCASE_NO ��������ҽ�ƿ�֧����ʱ��������CASE_NO
			if ("N".endsWith(reg.getApptCode())) {
				// System.out.println("222222222222222222");
				if (null != caseNo && caseNo.length() > 0) {
					reg.setCaseNo(caseNo);
				} else {
					reg.setCaseNo(SystemTool.getInstance().getNo("ALL", "REG",
							"CASE_NO", "CASE_NO"));
				}
				// ����ҽ�ƿ�
				if (!onTXEktSave("Y",null)) {
//					System.out.println("!!!!!!!!!!!ҽ�ƿ��������");
					return null;
				}
				if (null != regPatAdmControl.greenParm
						&& null != regPatAdmControl.greenParm.getValue("GREEN_FLG")
						&& regPatAdmControl.greenParm.getValue("GREEN_FLG").equals("Y")) {
					// ʹ����ɫͨ�����
					reg.getRegReceipt().setPayMedicalCard(
							TypeTool.getDouble(regPatAdmControl.greenParm.getDouble("EKT_USE")));
					reg.getRegReceipt().setOtherFee1(
							regPatAdmControl.greenParm.getDouble("GREEN_USE"));
				}
			}
		}
		
		if (payWay.equals("PAY_INS_CARD")) {
			TParm result = null;
			// ҽ����֧��
			result = onSaveRegTwo(payWay, regPatAdmControl.ins_exe, caseNo);
			if (null == result) {
				return null;
			}
			regPatAdmControl.ins_exe = result.getBoolean("INS_EXE");
			regPatAdmControl.ins_amt = result.getDouble("INS_AMT");
			regPatAdmControl.accountamtforreg = result.getDouble("ACCOUNT_AMT_FORREG");
			ektParam.setConfirmNo(reg.getConfirmNo());
		}
		
		ektParam.setOpType("");
		return ektParam;
	}

	@Override
	public EktParam openClient(EktParam ektParam) {

		ektParam = ektRegOrOpbClient.openClient(ektParam);
		
		if(ektParam != null){
			
			String type = ektParam.getOpType();
			// System.out.println("type===" + type);
			if (type.equals("3")) {
				regPatAdmControl.messageBox("E0115");
				return null;
			}
			if (type.equals("2")) {
				return null;
			}
			if (type.equals("-1")) {
				regPatAdmControl.messageBox("��������!");
				return null;
			}
			
			System.out.println("ektParam.getSqls()-----"+ektParam.getSqls());
			reg.setEktSql(ektParam.getSqls());
			
			TParm parm = ektParam.getOrderParm().getParm("result");
			regPatAdmControl.tredeNo = parm.getValue("TREDE_NO");
			regPatAdmControl.businessNo = parm.getValue("BUSINESS_NO"); // //����ҽ�ƿ��ۿ��������ʹ��
			regPatAdmControl.ektOldSum = parm.getValue("OLD_AMT"); // ִ��ʧ�ܳ����Ľ��
			regPatAdmControl.ektNewSum = parm.getValue("EKTNEW_AMT"); // �ۿ��Ժ�Ľ��
			// �ж��Ƿ������ɫͨ��
			if (null != parm.getValue("GREEN_FLG")
					&& parm.getValue("GREEN_FLG").equals("Y")) {
				regPatAdmControl.greenParm = parm;
			}
			
		}
		
		return ektParam;
	}
	
	public EktParam openClientR(EktParam ektParam) {
		reg = ektParam.getReg();
		
		ektParam = ektRegOrOpbClient.openClient(ektParam);
		
		if(ektParam != null){
			
			String type = ektParam.getOpType();
			// System.out.println("type===" + type);
			if (type.equals("3") || type.equals("-1")) {
				regPatAdmControl.messageBox("E0115");
				return null;
			}
			if (type.equals("2")) {
				return null;
			}
			
			reg.setEktSql(ektParam.getSqls());
			
			TParm parm = ektParam.getOrderParm().getParm("result");
			regPatAdmControl.tradeNoT = parm.getValue("TREDE_NO");
			
			
		}
		
		return ektParam;
	}
	
	
	private boolean onTXEktSave(String FLG, TParm insParm) {
		
		if (EKTIO.getInstance().ektSwitch()) { // ҽ�ƿ����أ���¼�ں�̨config�ļ���
			
			TParm orderParm = orderEKTParm(FLG);  // ׼������ҽ�ƿ��ӿڵ�����
			
			if (null == insParm){
				
				orderParm.addData("AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY")));
				orderParm.setData("SHOW_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY")));
				orderParm.setData("INS_FLG", "N");
				// ҽ�����������ֽ���ȡ
				reg.setInsPatType(""); // ����ҽ������ ��Ҫ���浽REG_PATADM���ݿ����1.��ְ��ͨ 2.��ְ����
				// 3.�Ǿ�����
				// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
				orderParm.setData("ektParm", regPatAdmControl.p3.getData());
				orderParm.setData("EXE_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))); // ҽ�ƿ��Ѿ��շѵ�����
				orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
				
			}else{
				
				orderParm.addData("AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))
						- insParm.getDouble("INS_SUMAMT")); // ҽ�����ԷѲ��ֽ��
				orderParm.setData("INS_AMT", insParm.getDouble("INS_SUMAMT")); // ҽ�����ԷѲ��ֽ��
				orderParm.setData("INS_FLG", "Y"); // ҽ����ע��
				orderParm.setData("OPBEKTFEE_FLG", true);// ȡ����ť
				orderParm.setData("RECP_TYPE", "REG"); // ���EKT_ACCNTDETAIL ������ʹ��
				orderParm.setData("comminuteFeeParm", insParm.getParm(
						"comminuteFeeParm").getData()); // ���÷ָ�ز���
				orderParm.setData("ektParm", regPatAdmControl.p3.getData());
				orderParm.setData("EXE_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))
						- insParm.getDouble("INS_SUMAMT")); // �˲��������շ�ҽ�������Ѿ���Ʊ��
				orderParm.setData("SHOW_AMT", TypeTool.getDouble(regPatAdmControl.getValue("FeeY"))
						- insParm.getDouble("INS_SUMAMT")); // ��ʾ���
				orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
				
				
			}
				
			ektParam.setOrderParm(orderParm);
		
		} else {
			regPatAdmControl.messageBox_("ҽ�ƿ��ӿ�δ����");
			return false;
		}
		return true;
		
		
	}
	
	/**
	 * ҽ�ƿ����
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	private TParm orderEKTParm(String FLG) {
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // д�̶�ֵ
		orderParm.addData("ORDER_CODE", "REG"); // д�̶�ֵ
		orderParm.addData("SEQ_NO", "1"); // д�̶�ֵ
		orderParm.addData("EXEC_FLG", "N"); // д�̶�ֵ
		orderParm.addData("RECEIPT_FLG", "N"); // д�̶�ֵ
		orderParm.addData("BILL_FLG", FLG);
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		orderParm.setData("BUSINESS_TYPE", "REG");
		return orderParm;
	}
	
	/**
	 * ִ�б��� ҽ�������ݲ���
	 * 
	 * @param payWay
	 *            String
	 * @param ins_amt
	 *            double
	 * @param ins_exe
	 *            boolean
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm onSaveRegTwo(String payWay, boolean ins_exe, String caseNo) {
		double ins_amtTemp = 0.00;// ҽ�����
		TParm result = new TParm();
		if (payWay.equals("PAY_INS_CARD")) {
			// ��ѯ�Ƿ�������������
			if (null == caseNo || caseNo.length() <= 0) {
				caseNo = SystemTool.getInstance().getNo("ALL", "REG",
						"CASE_NO", "CASE_NO"); // ��þ����
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm = PatAdmTool.getInstance().selEKTByMrNo(parm);
			if (parm.getErrCode() < 0) {
				regPatAdmControl.messageBox("E0005");
				return null;
			}

			if (parm.getDouble("GREEN_BALANCE", 0) > 0) {
				regPatAdmControl.messageBox("�˾��ﲡ��ʹ��������,������ʹ��ҽ������");
				return null;
			}
			if (regPatAdmControl.getValue("REG_CTZ1").toString().length() <= 0) {
				regPatAdmControl.messageBox("��ѡ��ҽ������������");
				return null;
			}
			// ��Ҫ���浽REG_PATADM���ݿ����1.��ְ��ͨ
			// 2.��ְ���� 3.�Ǿ�����
			// ҽ�����Һ�
			// ��ùҺŷ��ô��룬���ý�����
			
			TParm mtParm=PatAdmTool.getInstance().getMTClinicFee(regPatAdmControl.insParm);

			
			//��õ�ǰʱ��
			String sysdate =StringTool.getString(SystemTool.
					getInstance().getDate(),"yyyyMMddHHmmss");
			String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
					+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
					+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE_HISTORY B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
					+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='"
					+ regPatAdmControl.admType
					+ "'"
					+ " AND A.CLINICTYPE_CODE='"
					+ regPatAdmControl.getValue("CLINICTYPE_CODE") + "'" 
					+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
			
			//add by huangtt 20170504 ���ؼ�10��Ǯ
			if(mtParm.getValue("mrCliniFeeCode").trim().length() > 0){
				regFeesql += " UNION ALL "
						+ " SELECT B.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, "
						+ " B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
						+ " B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, "
						+ " '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,'' AS RECEIPT_TYPE,"
						+ " C.DOSE_CODE FROM SYS_FEE_HISTORY B,PHA_BASE C"
						+ " WHERE B.ORDER_CODE = '" + mtParm.getValue("mrCliniFeeCode") + "'"
						+ " AND B.ORDER_CODE=C.ORDER_CODE(+)	"
						+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
			}

			// �Һŷ�
			double reg_fee = BIL.getRegDetialFee(regPatAdmControl.admType, TypeTool
					.getString(regPatAdmControl.getValue("CLINICTYPE_CODE")), "REG_FEE",
					TypeTool.getString(regPatAdmControl.getValue("REG_CTZ1")), TypeTool
							.getString(regPatAdmControl.getValue("REG_CTZ2")), TypeTool
							.getString(regPatAdmControl.getValue("CTZ3_CODE")), regPatAdmControl
							.getValueString("SERVICE_LEVEL") == null ? ""
							: regPatAdmControl.getValueString("SERVICE_LEVEL"));
			// ���� �����ۿ�
			double clinic_fee = BIL.getRegDetialFee(regPatAdmControl.admType, TypeTool
					.getString(regPatAdmControl.getValue("CLINICTYPE_CODE")), "CLINIC_FEE",
					TypeTool.getString(regPatAdmControl.getValue("REG_CTZ1")), TypeTool
							.getString(regPatAdmControl.getValue("REG_CTZ2")), TypeTool
							.getString(regPatAdmControl.getValue("CTZ3_CODE")), 
							regPatAdmControl.getValueString("SERVICE_LEVEL") == null ? ""
							: regPatAdmControl.getValueString("SERVICE_LEVEL"));
			
			
			// System.out.println("regFeesql:::::" + regFeesql); 
			TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
					regFeesql));
			if (regFeeParm.getErrCode() < 0) {
				regPatAdmControl.err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
				regPatAdmControl.messageBox("ҽ��ִ�в���ʧ��");
				return null;
			}
			for (int i = 0; i < regFeeParm.getCount(); i++) {
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
					regFeeParm.setData("AR_AMT", i, reg_fee);
				}
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
					regFeeParm.setData("AR_AMT", i, clinic_fee);
				}
				if(mtParm.getValue("mrCliniFeeCode").trim().length() > 0){ //modify by huangtt 20170504
					if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("")) {
						regFeeParm.setData("RECEIPT_TYPE", i, mtParm.getDouble("fee"));
						regFeeParm.setData("AR_AMT", i, mtParm.getDouble("fee"));
					}
				}
			}
			// System.out.println("regFeesql::" + regFeesql);
			result = TXsaveINSCard(regFeeParm, caseNo); // ִ�в���
			// System.out.println("RESULT::::" + result);
			if (null == result)
				return null;
			if (result.getErrCode() < 0) {
				regPatAdmControl.err(result.getErrCode() + " " + result.getErrText());
				regPatAdmControl.messageBox("ҽ��ִ�в���ʧ��");
				return null;
			}
			// 24ҽ����֧��(REG_RECEIPT)
			if (null != result.getValue("MESSAGE_FLG")
					&& result.getValue("MESSAGE_FLG").equals("Y")) {
				System.out.println("ҽ�������ִ����ֽ���ȡ");
			} else {
				// ҽ��֧��
				ins_amtTemp = regPatAdmControl.tjInsPay(result, regFeeParm);
				ins_exe = true; // ҽ��ִ�в��� ��Ҫ�ж���;״̬
				reg.setInsPatType(regPatAdmControl.insParm.getValue("INS_TYPE")); // ����ҽ������
				reg.setConfirmNo(regPatAdmControl.insParm.getValue("CONFIRM_NO")); // ҽ�������
				// CONFIRM_NO
			}

		}
		result.setData("INS_AMT", ins_amtTemp);
		result.setData("INS_EXE", ins_exe);

		return result;
	}
	
	/**
	 * ̩��ҽԺҽ�����������
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm TXsaveINSCard(TParm parm, String caseNo) {
		// û�л��ҽ�ƿ���Ϣ �ж��Ƿ�ִ���ֽ��շ�
		if (!regPatAdmControl.tjINS && !regPatAdmControl.insFlg) {
			if (regPatAdmControl.messageBox("��ʾ", "û�л��ҽ�ƿ���Ϣ,ִ���ֽ��շ��Ƿ����", 2) != 0) {
				return null;
			}
		}
		if (regPatAdmControl.tjINS) { // ҽ�ƿ�����
			if (regPatAdmControl.p3.getDouble("CURRENT_BALANCE") < regPatAdmControl.getValueDouble("FeeY")) {
				regPatAdmControl.messageBox("ҽ�ƿ�����,���ֵ");
				return null;
			}
		}
		TParm result = new TParm();
		regPatAdmControl.insParm.setData("REG_PARM", parm.getData()); // ҽ����Ϣ
		regPatAdmControl.insParm.setData("DEPT_CODE", regPatAdmControl.getValue("DEPT_CODE")); // ���Ҵ���
		regPatAdmControl.insParm.setData("MR_NO", pat.getMrNo()); // ������

		reg.setCaseNo(caseNo);
		regPatAdmControl.insParm.setData("RECP_TYPE", "REG"); // ���ͣ�REG / OPB
		regPatAdmControl.insParm.setData("CASE_NO", reg.caseNo());
		regPatAdmControl.insParm.setData("REG_TYPE", "1"); // �Һű�־:1 �Һ�0 �ǹҺ�
		regPatAdmControl.insParm.setData("OPT_USER", Operator.getID());
		regPatAdmControl.insParm.setData("OPT_TERM", Operator.getIP());
		regPatAdmControl.insParm.setData("DR_CODE", regPatAdmControl.getValue("DR_CODE"));// ҽ������
		// insParm.setData("PAY_KIND", "11");// 4 ֧�����:11���ҩ��21סԺ//֧�����12��
		if (regPatAdmControl.getValueString("ERD_LEVEL").length() > 0) {
			regPatAdmControl.insParm.setData("EREG_FLG", "1"); // ����
		} else {
			regPatAdmControl.insParm.setData("EREG_FLG", "0"); // ��ͨ
		}

		regPatAdmControl.insParm.setData("PRINT_NO", regPatAdmControl.getValue("NEXT_NO")); // Ʊ��
		regPatAdmControl.insParm.setData("QUE_NO", reg.getQueNo());

		TParm returnParm = regPatAdmControl.insExeFee(true);
		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
			return null;
		}
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.ʧ�� 1. �ɹ�
		if (returnType == 0 || returnType == -1) { // ȡ������
			return null;
		}

		regPatAdmControl.insParm.setData("comminuteFeeParm", returnParm.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�����
		regPatAdmControl.insParm.setData("settlementDetailsParm", returnParm.getParm(
				"settlementDetailsParm").getData()); // ���ý���

		// System.out.println("insParm:::::::"+insParm);
		result = INSTJReg.getInstance().insCommFunction(regPatAdmControl.insParm.getData());

		if (result.getErrCode() < 0) {
			regPatAdmControl.err(result.getErrCode() + " " + result.getErrText());
			// this.messageBox("ҽ��ִ�в���ʧ��");
			return result;
		}
		// System.out.println("ҽ����������:" + insParm);

		result.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
		result.setData("ACCOUNT_AMT_FORREG", returnParm
				.getDouble("ACCOUNT_AMT_FORREG")); // �˻����
		regPatAdmControl.insParm.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
		if (regPatAdmControl.tjINS) { // ҽ�ƿ�����
			// ִ��ҽ�ƿ��ۿ��������Ҫ�ж�ҽ�������ҽ�ƿ����
			if (!onTXEktSave("Y", result)) {
//				result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
//						"deleteOldData", regPatAdmControl.insParm);
//				if (result.getErrCode() < 0) {
//					regPatAdmControl.err(result.getErrCode() + " " + result.getErrText());
//					result.setErr(-1, "ҽ����ִ�в���ʧ��");
//					// return result;
//				}
//				result.setErr(-1, "ҽ�ƿ�ִ�в���ʧ��");
				return null;
			}

		}
		return result;
	}
	
	
	

}
