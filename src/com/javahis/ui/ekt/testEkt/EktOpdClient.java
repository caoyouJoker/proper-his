package com.javahis.ui.ekt.testEkt;

import java.math.BigDecimal;

import jdo.ekt.EKTGreenPathTool;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;



public class EktOpdClient extends EktClient{

	public  EktParam openClient(EktParam ektParam){
		
		isNull = ektParam.isNull();
		
		ektParam = onAccntClient(ektParam);
		if(ektParam.getOpType().length() > 0){
			return ektParam;
		}

		if (EKTDialogSwitch()) {
			if (!amt.equals(BigDecimal.ZERO) || !isNull) {
				TParm r = null;
				p.setData("RE_PARM",parm.getData());//ҽ��վҽ����������UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX д��һ������
				p.setData("OPB_AMT", opbAmt.doubleValue());// ����ҽ��վ�������=====pangben 2013-7-17
				p.setData("ODO_EKT_FLG", "Y");// ����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�=====pangben 2013-7-17
							
				
//				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// ҽ�ƿ��н��С�ڴ˴��շѵĽ��
				if(amt.compareTo(oldAmtGerrenBalance) > 0){	
					if (null != unFlg && unFlg.equals("Y")) {// ҽ���޸�ҽ������
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// ��ɫͨ��ʣ����
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// ��ɫͨ���������
						p.setData("unParm", parm.getParm("unParm").getData());
						p.setData("EKT_TYPE_FLG",3);//�޸�ҽ�������˻ش���ǩ�еĽ��
						p.setData("OPD_UN_FLG","Y");//�����޸�OPD_ORDER�� ===pangben 2013-7-17
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewReAmt.x", p);
						
						ektParam.getOrderParm().setData("unParm", r);

						
					} else {//�����޸�ҽ�������Ǵ˴ν���ҽ�ƿ���� 
						p.setData("EKT_TYPE_FLG",2);
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x", p);
					}
					
				} else if(null!=parm.getValue("OPBEKTFEE_FLG")&&
						parm.getValue("OPBEKTFEE_FLG").equals("Y")&& 
						null != unFlg && unFlg.equals("Y")) {// ҽ�ƿ�������:ҽ���޸�ҽ������//ҽ�ƿ�����㹻�˻�ҽ�ƿ�������
					//����ʾ�ۿ���棬ֱ�Ӳ���=====pangben 2013-4-27 
					r = exeRefund(p);
				}else {

					r = (TParm) control.openDialog(
							"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
					
				}
				if (r == null) {
					// System.out.println("asdadasd");
					ektParam.setOpType("3");
					return ektParam;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					ektParam.setOpType("3");
					return ektParam;
				}
			
				type = r.getInt("OP_TYPE");
				// ����
				if (type == 2) {
					ektParam.setOpType("3");
					return ektParam;
				}
				
				
//				if (null == unFlg
//						|| unFlg.equals("N")
//						|| amt <= (oldAmt + greenParm.getDouble(
//								"GREEN_BALANCE", 0))) {
				if( null == unFlg
						|| unFlg.equals("N")
						||  amt.compareTo(oldAmtGerrenBalance) <=0){
					
					// cardNo = r.getValue("CARD_NO");
					// =========pangben 20111024 start
					ektAMT =new BigDecimal(r.getDouble("EKTNEW_AMT")) ;// ����ҽ�ƿ��еĽ��
					ektOldAMT = new BigDecimal(r.getDouble("OLD_AMT"));
					if (null != r.getValue("AMT")
							&& r.getValue("AMT").length() > 0) {
						result.setData("AMT", r.getValue("AMT")); // �շѽ��
						result.setData("EKT_USE", r.getDouble("EKT_USE")); // ��ҽ�ƿ����
						// greenUseAmt=r.getDouble("GREEN_USE");//��ɫͨ��ʹ�ý��
						// ektUseAmt= r.getDouble("EKT_USE");//ҽ�ƿ�ʹ�ý��
						result.setData("GREEN_USE", r.getDouble("GREEN_USE")); // ����ɫͨ�����
					}
					greenBalance = new BigDecimal(r.getDouble("GREEN_BALANCE")); // ��ɫͨ��δ�ۿ���
					greenPathTotal =new BigDecimal( r.getDouble("GREEN_PATH_TOTAL")); // ��ɫͨ���ܽ��
					// =========pangben 20111024 stop
					
					tradeNo = r.getValue("TRADE_NO");
					historyNo = r.getValue("HISTORY_NO");
					cancelTrede = r.getValue("CANCLE_TREDE");
					greenFlg = r.getValue("GREEN_FLG");
				}
				
				ektParam.setSqls((String[]) r.getData("SQL"));

			} 

		}

		
		setResultValue();
		
		ektParam.getOrderParm().setData("result", result.getData());
		System.out.println(ektParam.getOrderParm().getParm("result"));
		return ektParam;
		
	}
	
	
	/**
	 * ִ���˿���� �ż���ҽ��վ �޸�ҽ���������˻�ҽ�ƿ����
	 */
	private TParm exeRefund(TParm sumParm){
		TParm parm = new TParm();
		TParm readCard = sumParm.getParm("READ_CARD");
		TParm result = new TParm();
		if (readCard.getErrCode() < 0) {
			// TParm parm = new TParm();
			parm.setErr(-1, "��ҽ�ƿ���Ч");
			return parm;
		}
		//String cardNo = readCard.getValue("CARD_NO");
		String caseNo = sumParm.getValue("CASE_NO");//�����
		String tradeSumNo=sumParm.getValue("TRADE_SUM_NO");//�˴β����Ѿ��շѵ��ڲ����׺���,��ʽ'xxx','xxx'
		parm.setData("CASE_NO", caseNo);
		double amt = sumParm.getDouble("AMT");//��ʾ�Ľ��
		String insFlg = sumParm.getValue("INS_FLG");// ҽ����ע��
		double exeAmt = sumParm.getDouble("EXE_AMT");//ҽ��վ�˴β������
		TParm reParm=sumParm.getParm("RE_PARM");//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬��ӵ�һ��������
		// ��ѯ�˴ξ��ﲡ���Ƿ����ҽ�ƿ���ɫͨ��
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");
		double sumAmt = oldAmt - amt;
		double tempAmt = oldAmt;
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// ��ѯ��ɫͨ���ۿ���ܳ�ֵ���
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			sumAmt += result.getDouble("GREEN_BALANCE", 0);// �ۿ�֮��Ľ��
			tempAmt += result.getDouble("GREEN_BALANCE", 0);// δ�ۿ���
		}
		parm.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
		if (sumAmt < 0) {
			parm.setData("OP_TYPE", 2);
		} else {
			TParm cp = new TParm();
			cp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
			cp.setData("CASE_NO", caseNo);
			cp.setData("MR_NO",  sumParm.getValue("MR_NO"));
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));
			cp.setData("IDNO", readCard.getValue("IDNO"));
			// cp.setData("BUSINESS_NO",businessNo);
			String ektTradeType = sumParm.getValue("EKT_TRADE_TYPE");// ��ѯ����
			String businessType = sumParm.getValue("BUSINESS_TYPE");// �������ݲ���
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("EKT_TRADE_TYPE", ektTradeType);//�Һ�ʹ�� REG_PATADM ��û��TRADE_NO �ֶ� 
			cp.setData("OLD_AMT", oldAmt);// ҽ�ƿ����
			cp.setData("NEW_AMT",sumAmt);
			cp.setData("INS_FLG", insFlg);// ҽ����ע��
			// zhangp 20120106 ����seq
			cp.setData("SEQ", readCard.getValue("SEQ"));// ���
			//cp.setData("SUMOPDORDER_AMT", sumOpdorderAmt);
			// �ۿ�
			if (amt >= 0) {
				onFee(cp, result, oldAmt, amt, sumAmt);
			} else {
				// �˷�
				onUnFee(cp, result, oldAmt, amt, sumAmt);
			}
			//parm.setData("TRADE_NO",tradeSumNo);
			//��Ҫ�����Ľ��׽��
			//TParm ektTradeSumParm=EKTNewTool.getInstance().selectEktTradeUnSum(parm);
			// ҽ�ƿ������ڴ˴οۿ���
			if (null != result && result.getCount() > 0) {
				onExeGreenFee(oldAmt, amt, businessType, cp, exeAmt, caseNo, result);
			}else{
				cp.setData("EKT_USE", exeAmt);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",0.00);//��ɫͨ���ۿ���
				cp.setData("EKT_OLD_AMT",oldAmt+exeAmt-amt);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("GREEN_BALANCE",0.00);//������ۿ���
			}
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			cp.setData("TRADE_SUM_NO",tradeSumNo);////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
			cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬��ӵ�һ��������
			cp.setData("OPB_AMT",sumParm.getDouble("OPB_AMT"));// ����ҽ��վ�������=====pangben 2013-7-17
			cp.setData("ODO_EKT_FLG",sumParm.getValue("ODO_EKT_FLG"));// ����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�=====pangben 2013-7-17
			parm.setData("ektParm", cp.getData());
			
			// ̩��ҽԺ�ۿ����
			TParm p = new TParm(EKTNewIO.getInstance().onNewSaveFee(
					cp.getData()));
			// TParm p = EKTIO.getInstance().consume(cp);
			if (p.getErrCode() < 0)
				parm.setErr(-1, p.getErrText());
			else {
				parm.setData("SQL", p.getData("SQL")); //add by huangtt 20160901
				parm.setData("OP_TYPE", 1);
				parm.setData("TRADE_NO", p.getValue("TRADE_NO"));
				parm.setData("OLD_AMT", oldAmt);
				parm.setData("NEW_AMT",sumAmt);
				parm.setData("EKTNEW_AMT", cp.getDouble("EKT_AMT"));// ҽ�ƿ��еĽ��
				parm.setData("CANCLE_TREDE", p.getValue("CANCLE_TREDE"));// �˷Ѳ���ִ�е�ҽ�ƿ��ۿ�����TREDE_NO��Ϣ
				if (null != result && result.getCount() > 0) {
					parm.setData("AMT", amt < 0 ? amt * (-1) : amt); // �շѽ��
					parm.setData("EKT_USE", cp.getDouble("EKT_USE")); // ��ҽ�ƿ����
					parm.setData("GREEN_USE", cp.getDouble("GREEN_USE")); // ����ɫͨ�����
					parm.setData("GREEN_FLG", "Y"); // �ж��Ƿ������ɫͨ�������BIL_OPB_RECP
													// ��PAY_MEDICAL_CARD����ʱ��Ҫ�ж�Ϊ0ʱ�Ĳ���
					parm.setData("GREEN_BALANCE", result.getDouble(
							"GREEN_BALANCE", 0)); // ��ɫͨ��δ�ۿ���
					parm.setData("GREEN_PATH_TOTAL", result.getDouble(
							"GREEN_PATH_TOTAL", 0)); // ��ɫͨ���ܽ��
				}	
			}
		}
		return parm;
	}
	/**
	 * �������շ�
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// ҽ�ƿ���ɫͨ������
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//ҽ�ƿ����+ҽ�ƿ���ɫǮ���Ľ��
			if (oldAmt - amt >= 0) {
				cp.setData("EKT_AMT", oldAmt - amt); // ҽ�ƿ�������
//				cp.setData("EKT_USE", amt);// ҽ�ƿ��ۿ���
				cp.setData("SHOW_GREEN_USE",0.00);//��ɫͨ���ۿ���
			} else {
				cp.setData("EKT_AMT", 0.00); // ҽ�ƿ�������
//				cp.setData("EKT_USE", oldAmt);// ҽ�ƿ��ۿ���
			    cp.setData("SHOW_GREEN_USE",StringTool.round(amt - oldAmt,2));//��ɫͨ���ۿ���
			}
			cp.setData("FLG", "Y"); // ��ɫͨ������ע��
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",0));
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT",sumAmt); // û��ҽ�ƿ���ɫǮ��
			cp.setData("FLG", "N"); // ��ɫͨ��������ע��
		}
	}

	/**
	 * �������˷�
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onUnFee(TParm cp,TParm result,double oldAmt,double amt,double sumAmt) {
		// ҽ�ƿ���ɫͨ������
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//ҽ�ƿ����+ҽ�ƿ���ɫǮ���Ľ��
			// �����˷�
			if (result.getDouble("GREEN_BALANCE", 0) >= result.getDouble(
					"GREEN_PATH_TOTAL", 0)) {
				cp.setData("EKT_AMT", oldAmt - amt); // ҽ�ƿ�����˷ѳ�ֵ
//				cp.setData("EKT_USE", -amt);// ҽ�ƿ��ۿ���
				cp.setData("SHOW_GREEN_USE",0.00);//��ɫͨ���ۿ���
			} else {
				// ���ȣ�ҽ�ƿ���ɫǮ���ۿ����ֵ Ȼ�� ��ɫǮ���ۿ�����ڳ�ֵ�Ľ���Ժ���ȥ��ֵҽ�ƿ�
				double tempFee = result.getDouble("GREEN_BALANCE", 0) - amt;// �鿴��ɫǮ���ۿ���+��Ҫ�˷ѽ���Ƿ���ڳ�ֵ���
				// �����ڳ�ֵ������ۿ���
				if (tempFee > result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt + tempFee
							- result.getDouble("GREEN_PATH_TOTAL", 0));// ҽ�ƿ��н�����ۿ����Ժ�Ľ��+ҽ�ƿ��н��
//					cp.setData("EKT_USE", tempFee
//							- result.getDouble("GREEN_PATH_TOTAL", 0));// ҽ�ƿ��ۿ���
					cp.setData("SHOW_GREEN_USE",StringTool.round(result.getDouble("GREEN_BALANCE", 0)-result.getDouble("GREEN_PATH_TOTAL", 0),2));//��ɫͨ���ۿ���
				} else if (tempFee <= result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt);// ҽ�ƿ��еĽ���
//					cp.setData("EKT_USE", 0.00);// ҽ�ƿ��ۿ���
					cp.setData("SHOW_GREEN_USE",amt);//��ɫͨ���ۿ���
				}
			}
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",
					0));
			cp.setData("FLG", "Y"); // ��ɫͨ������ע��
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT", sumAmt); // û��ҽ�ƿ���ɫǮ��
			cp.setData("FLG", "N"); // ��ɫͨ��������ע��
		}

	}
	
	/**
	 * ��������������ݣ����EKT_TRADE ��
	 * ==========panben 2013-7-26
	 */
	public void onExeGreenFee(double oldAmt,double amt,String businessType,
			TParm cp,double exeAmt,String caseNo,TParm result){
		if (oldAmt - amt >= 0) {
			if (amt > 0) {//����������������ۿ����̣�amt > 0 ҽ�ƿ��д��ڽ��˴οۿ�۳�ҽ�ƿ��еĽ��
				cp.setData("EKT_USE", exeAmt);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE", 0.00);// ��ɫͨ���ۿ���
				// EKT_OLD_AMT:EKT_TRADE ���� OLD_AMT ���=ԭ�����
				// +�˴οۿ���(EXE_AMT)-��ʾ���(AMT)
				cp.setData("EKT_OLD_AMT", oldAmt + exeAmt - amt);// ҽ�ƿ����ڲ���֮ǰ�Ľ����˴ζ����Ĵ���ǩ�����н��س��õ�ǰҽ�ƿ��Ľ�
			}else{//ҽ�ƿ����� 
				onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);	
			}
		} else {
			onGreenFeeTemp(businessType, oldAmt, amt, cp, exeAmt, caseNo, result);
		}
		cp.setData("GREEN_BALANCE",result.getDouble("GREEN_BALANCE", 0));//������ۿ���
	}
	
	/**
	 * ҽ�ƿ�ʹ�������������
	 * ==========pangben 2013-7-26
	 */
	private void onGreenFeeTemp(String businessType,double oldAmt,double amt,
			TParm cp,double exeAmt,String caseNo,TParm result){
		//EKT_OLD_AMT: OLD_AMT ���=ԭ�����+(�˴οۿ���+������ʣ����-�������)
		double ektOldAmt=0.00;
		if(businessType.equals("REG") || businessType.equals("REGT")){//�ҺŲ���
			ektOldAmt=oldAmt;
			cp.setData("EKT_OLD_AMT",ektOldAmt);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
			cp.setData("EKT_USE", ektOldAmt);// ҽ�ƿ��ۿ���
			cp.setData("GREEN_USE",exeAmt-ektOldAmt);//��ɫͨ���ۿ���
		}else{
			TParm ektTradeParm=new TParm();
			ektTradeParm.setData("CASE_NO",caseNo);
			ektTradeParm.setData("EKT_TRADE_TYPE","'REG'");
			TParm ektSumTradeParm = EKTNewTool.getInstance().selectEktTrade(
					ektTradeParm);
			//ʣ����+�˴�ִ�еĽ��-��ʾ���<�������-�Һ�������ʹ�ý��
			ektOldAmt=oldAmt+result.getDouble("GREEN_BALANCE", 0)+exeAmt-amt;
			double greenPath=result.getDouble("GREEN_PATH_TOTAL", 0)-ektSumTradeParm.getDouble("GREEN_BUSINESS_AMT",0);
			if(ektOldAmt>=greenPath){
				//����һ������ҽ�ƿ��еĽ��
				cp.setData("EKT_OLD_AMT",ektOldAmt-greenPath);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("EKT_USE", ektOldAmt-greenPath);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",exeAmt-ektOldAmt+greenPath);//��ɫͨ���ۿ���
			}else{
				cp.setData("EKT_OLD_AMT",0.00);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("EKT_USE", 0.00);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",exeAmt);//��ɫͨ���ۿ���
			}
		}
	}
	

}
