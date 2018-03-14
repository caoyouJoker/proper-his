package com.javahis.ui.ekt.testEkt;

import java.math.BigDecimal;

import jdo.ekt.EKTNewIO;
import jdo.reg.PatAdmTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.javahis.ui.opd.OdoMainControl;

public class EktClient {
	protected TControl control;
	protected TParm parm;
	protected TParm readCard;
	
	protected String caseNo;
	protected String cardNo;
	protected String mrNo;
	protected String businessType;
	protected String ektTradeType;
	protected String type_flg;// �˷Ѳ���
	protected String ins_flg;// ҽ����ע��
	protected String unFlg;// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
	protected String tradeNo;
	protected String historyNo;
	protected String opbektFeeFlg;// ҽ�����س������
	protected BigDecimal oldAmt;//ҽ�ƿ����
	protected BigDecimal insAmt;// ҽ�����
	protected int type = 1;
	protected BigDecimal ektAMT = new BigDecimal(0.00);// ҽ�ƿ�ִ���Ժ���
	protected BigDecimal ektOldAMT =  new BigDecimal(0.00);// ҽ�ƿ�ԭ��������ʧ��ʱʹ��
	protected String cancelTrede = null;// �ۿ����ʧ�ܻع�ҽ�ƿ���������		
	protected BigDecimal opbAmt =  new BigDecimal(0.00);// ����ҽ��վ�����Ľ��
	protected BigDecimal greenBalance =  new BigDecimal(0.00);// ��ɫͨ���ܿۿ���
	protected BigDecimal greenPathTotal =  new BigDecimal(0.00);// ��ɫͨ���������
	protected String greenFlg = null;// �ж��Ƿ������ɫͨ�������BIL_OPB_RECP
	protected boolean isNull = true;
	protected TParm result = new TParm();
	protected TParm p = new TParm();
	protected TParm greenParm = new TParm();
	
	protected BigDecimal amt = new BigDecimal(0.00); // �ۿ���
	protected BigDecimal oldAmtGerrenBalance = new BigDecimal(0);
	
	public EktParam onAccntClient(EktParam ektParam){
		control = ektParam.gettControl();
//		TParm result = new TParm();
		// ��ֹ�ڷ������˵���
		if (EKTNewIO.getInstance().ektIsClientlink() ) {			
			ektParam.setOpType("-1");
			return ektParam;
		}
		// ҽ�ƿ������Ƿ�����
		if (!ektSwitch()) {
			ektParam.setOpType("0");
			return ektParam;
		}
		 parm = ektParam.getOrderParm();
		if (parm == null) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient TParm ����Ϊ��");
			ektParam.setOpType("-1");
			return ektParam;
		}
		 caseNo = ektParam.getReg().caseNo();
		if (caseNo == null || caseNo.length() == 0) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient caseNo ����Ϊ��");
			ektParam.setOpType("-1");
			return ektParam;
		}

		 readCard = parm.getParm("ektParm");// ̩��ҽ�ƿ���������
		if (parm.getBoolean("IS_NEW") && readCard.getErrCode() == -1) {
			if ("�޿�".equals(readCard.getValue("ERRCode")))
				control.messageBox("�޿�,����ҽ������δ�շѣ�");
			else
				control
						.messageBox(readCard.getValue("ERRCode")
								+ ",����ҽ������δ�շѣ�");
			ektParam.setOpType("5");
			return ektParam;

		}

		if (readCard.getErrCode() != 0) {;
			control.messageBox("ҽ�ƿ�����,��;ʧ��");
			ektParam.setOpType("-1");
			return ektParam;
		}
		
		// ̩��ҽԺ��ÿ���
		 cardNo = readCard.getValue("MR_NO") + readCard.getValue("SEQ");
		 oldAmt = new BigDecimal(readCard.getDouble("CURRENT_BALANCE"))  ;//ҽ�ƿ����
		 mrNo = parm.getValue("MR_NO");
		 businessType = parm.getValue("BUSINESS_TYPE");
		//��ѯ���ͻ������Һ�REG,REGT���շ�OPB,OPBT,ODO,ODOT
		 ektTradeType = parm.getValue("EKT_TRADE_TYPE");
		 type_flg = parm.getValue("TYPE_FLG");// �˷Ѳ���
		 ins_flg = parm.getValue("INS_FLG");// ҽ����ע��
		 insAmt =new BigDecimal( parm.getDouble("INS_AMT"));// ҽ�����
		 unFlg = parm.getValue("UN_FLG");// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
		 tradeNo = "";
		if (!mrNo.equals(readCard.getValue("MR_NO"))) {
			if (parm.getBoolean("IS_NEW")) {
				control.messageBox("�˿�Ƭ�����ڸû���,����ҽ������δ�շѣ�");
				ektParam.setOpType("5");
				return ektParam;
			}
			control.messageBox("�˿�Ƭ�����ڸû���!");
			ektParam.setOpType("3");
			return ektParam;
		}
		 opbektFeeFlg = parm.getValue("OPBEKTFEE_FLG");// ҽ�����س������
		 
		 
			if (EKTDialogSwitch()) {
				if (control == null) {
					//System.out.println("ERR:EKTIO.onOPDAccntClient control ����Ϊ��");
					ektParam.setOpType("-1");
					return ektParam;
				}
				p.setData("CASE_NO", caseNo);
				p.setData("CARD_NO", cardNo);
				p.setData("MR_NO", mrNo);
				
				
				// ��ѯ�˾��ﲡ���������ݻ��ܽ��
				//TParm orderSumParm=parm.getParm("orderSumParm");
				//��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
				//TParm ektSumParm =parm.getParm("ektSumParm");
				amt=new BigDecimal(parm.getDouble("SHOW_AMT"));//δ�շѵ��ܽ�� ��ʾ���
				
				if (amt.equals(BigDecimal.ZERO) && isNull) {
//					control.messageBox("û����Ҫ����������");
					ektParam.setOpType("6");// û����Ҫ����������
					return ektParam;
				}
				if (!amt.equals(BigDecimal.ZERO) || !isNull) {
					opbAmt =new BigDecimal(amt.doubleValue()) ;
					p.setData("AMT", amt.doubleValue());
					p.setData("EXE_AMT", parm.getDouble("EXE_AMT"));//ִ�н��(EKT_TRADE �д˴� �����Ľ��)
					//δ��Ʊ�����ܽ��
					readCard.setData("NAME", parm.getValue("NAME"));
					readCard.setData("SEX", parm.getValue("SEX"));
					p.setData("READ_CARD", readCard.getData());
					p.setData("BUSINESS_TYPE", businessType);// �ۿ��ֶ�
					p.setData("EKT_TRADE_TYPE", ektTradeType);
					p.setData("TYPE_FLG", type_flg);// �˷�ע��
					p.setData("INS_FLG", ins_flg);// ҽ��ע��
					p.setData("INS_AMT", insAmt);// ҽ�����
					p.setData("OPBEKTFEE_FLG", opbektFeeFlg);
					p.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
					//p.setData("newParm", parm.getParm("newParm").getData());//����ҽ��վҽ������Ҫ������ҽ��(��ɾ�����ݼ���)
					
					// ��ѯ��ɫͨ��ʹ�ý��
					TParm tempParm=new TParm();
					tempParm.setData("CASE_NO",caseNo);
				    greenParm = PatAdmTool.getInstance().selEKTByMrNo(tempParm);
					if (greenParm.getErrCode() < 0) {
						ektParam.setOpType("-1");
						return ektParam;
					}
					
					oldAmtGerrenBalance = oldAmtGerrenBalance.add(oldAmt);
					oldAmtGerrenBalance = oldAmtGerrenBalance.add(new BigDecimal(greenParm.getDouble("GREEN_BALANCE", 0)));
					
					
				} 
			}
		 
		 
		 
		
		ektParam.setOpType("");
		return ektParam;
	}
	
	
	public void setResultValue(){
		
		result.setData("EKTNEW_AMT", ektAMT.doubleValue());
		result.setData("OLD_AMT", ektOldAMT.doubleValue()); // ҽ�ƿ��Ѿ������Ժ�Ľ��
		result.setData("OP_TYPE", type);
		result.setData("TRADE_NO", tradeNo);
		result.setData("HISTORY_NO", historyNo);
		result.setData("CARD_NO", cardNo);
		result.setData("OPD_UN_FLG",parm.getValue("OPD_UN_FLG"));// ҽ���޸�ҽ������
		result.setData("CANCLE_TREDE", cancelTrede);// ����ʹ��
		result.setData("OPB_AMT", opbAmt.doubleValue());// ����ҽ��վ�������
		result.setData("GREEN_FLG", greenFlg);// ���BIL_OPB_RECP
		result.setData("GREEN_BALANCE", greenBalance.doubleValue()); // ��ɫͨ��δ�ۿ���
		result.setData("GREEN_PATH_TOTAL", greenPathTotal.doubleValue()); // ��ɫͨ���ܽ��
		
	}
	
	
	
	/**
	 * ekt����
	 * 
	 * @return boolean
	 */
	public boolean ektSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.switch"));
	}
	
	/**
	 * ҽ�ƿ�����
	 * 
	 * @return boolean
	 */
	public boolean EKTDialogSwitch() {
		return StringTool.getBoolean(TConfig
				.getSystemValue("ekt.opd.EKTDialogSwitch"));
	}

}
                                                  