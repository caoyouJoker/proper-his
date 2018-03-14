package com.javahis.ui.ekt.testEkt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;

import com.dongyang.data.TParm;

public class EktRegOrOpbClient extends EktClient {

	
	public  EktParam openClient(EktParam ektParam){

		ektParam = onAccntClient(ektParam);
		if(ektParam.getOpType().length() > 0){
			return ektParam;
		}
		
		if (EKTDialogSwitch()) {
			p.setData("CONFIRM_NO", ektParam.getConfirmNo());
			if (!amt.equals(BigDecimal.ZERO)) {
				TParm r = null;
//				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// ҽ�ƿ��н��С�ڴ˴��շѵĽ��
				if(amt.compareTo(oldAmtGerrenBalance) > 0){
					
					if (null != unFlg && unFlg.equals("Y")) {// ҽ���޸�ҽ������
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// ��ɫͨ��ʣ����
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// ��ɫͨ���������
						p.setData("unParm", parm.getParm("unParm").getData());
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\ekt\\EKTOpdChageUI.x", p);
						result.setData("unParm", r.getData());
					} else {
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
					}
					
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
		
		
		double currentBalance = readCard.getDouble("CURRENT_BALANCE");
		List list = EKTIO.getInstance().onOPDAccnt(parm.getData(), cardNo,
				caseNo, Operator.getID(), Operator.getIP(), type,
				currentBalance, ektAMT.doubleValue(),
				greenBalance.doubleValue(), greenPathTotal.doubleValue(),
				businessType);
		if (list == null) {
			ektParam.setOpType("3");
			return ektParam;
		}
		result.setData("RX_LIST", list);
		setResultValue();
		
		ektParam.getOrderParm().setData("result", result.getData());
		System.out.println("ektclient====="+ektParam.getOrderParm().getParm("result"));
		return ektParam;
		
	}
	
	


}
                                                  