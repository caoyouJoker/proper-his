package action.inv;

import jdo.inv.INVRegressGoodTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class INVQtyCheckAction extends TAction {

	/**
	 * ����
	 * */
	public TParm onSave222(TParm parms) {
		TParm result = new TParm();
		//TParm parm = parms.getParm("INVRegressGood");
		if (parms == null)
			return result.newErrParm(-1, "����Ϊ��");     
		TConnection connection = getConnection();
		// 1.��grid������д��INV_RETURNHIGH��
		int count = parms.getCount("CHECK_NO");
		for (int i = 0; i < count; i++) {
			result = INVRegressGoodTool.getInstance().insertDataForQtyCheck(
					parms.getRow(i), connection);
			if (result == null || result.getErrCode() < 0) {
				connection.rollback();
				connection.close(); 
				return result;
			}
		}
		// 2.����STOCKM STOCKD STOCKDD���ű������
		TParm invParm =  parms;
		result = this.onSaveINV(invParm, connection);
		if (result == null || result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	
	/**
	 * �ۿⷽ��
	 * */
	public TParm onSaveINV(TParm parm, TConnection connection) {

		TParm result = new TParm();
		if (parm.getCount("INV_CODE") < 0) {
			return result;
		}
		// ���� INV_STOCKM INV_STOCKD INV_STOCKDD������
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
				String inv_code = parm.getData("INV_CODE", i).toString();
				String orgCode = parm.getData("ORG_CODE", i).toString();
				String opt_user = parm.getData("OPT_USER", i).toString();
				String opt_term = parm.getData("OPT_TERM", i).toString();
				//�ۿ��̵���
				String checkFlg = parm.getData("CHECK_FLG", 0).toString();
				Double qty = parm.getDouble("STOCK_QTY", i)-parm.getDouble("MODI_QTY", i);// �ۿ�����

				// step1.����ORG_CODE,INV_CODE��ѯINV_STOCKM ORG_CODE ��ֵΪ������
				String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY "
						+ " FROM INV_STOCKM " + " WHERE ORG_CODE = '" + orgCode
						+ "' AND INV_CODE = '" + inv_code + "' ";// ORG_CODE��ʱд��
				// step2.���� INV_STOCKM����ֶ� stock_qty(-m)
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockM));  
//				System.out.println("++++++++++++++"+selectInvStockM);
//				System.out.println("++++++++++++++"+result);
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				String sql1 = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"
						+ inv_code + "'";
				TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
				String inv_chn_desc = parm1.getData("INV_CHN_DESC", 0)
						.toString();
				String sql2 = "SELECT ORG_DESC FROM INV_ORG WHERE ORG_CODE = '"
						+ orgCode + "'";
				TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
				String dept_chn_desc = parm2.getData("ORG_DESC", 0)
						.toString();
				if (result.getCount() < 0) {
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "��" + inv_chn_desc
							+ "���޴�����");
					connection.rollback();
					connection.close();
					return result;
					// ���޴�����
				}
				if (Double.parseDouble(result.getData("STOCK_QTY", 0)
						.toString()) < qty) {
					// ��治��
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "��" + inv_chn_desc
							+ "��治��");
					connection.rollback();
					connection.close();
					return result;
				}
				TParm updInvStockMParm = new TParm();
				updInvStockMParm.setData("ORG_CODE", orgCode);
				updInvStockMParm.setData("INV_CODE", inv_code);
				updInvStockMParm.setData("QTY", qty);
				updInvStockMParm.setData("OPT_USER", opt_user);
				updInvStockMParm.setData("OPT_TERM", opt_term);
				result = INVRegressGoodTool.getInstance().updInvStockM(
						updInvStockMParm, connection);
				if (result.getErrCode() < 0) {
					result.setErrText("����ʧ�ܣ�");
					connection.rollback();
					connection.close();   
					return result;
				}
				// step3.���� ORG_CODE,INV_CODE��ѯINV_STOCKD ����VALID_DATE��������
				String selectInvStockD = "SELECT INV_CODE,ORG_CODE,BATCH_SEQ,STOCK_QTY FROM INV_STOCKD WHERE INV_CODE = '"
						+ inv_code 
						+ "' AND ORG_CODE = '"     
						+ orgCode                
						+ "' ORDER BY VALID_DATE";
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockD));  
				if (result.getErrCode() < 0) {
					result.setErrText("����ʧ�ܣ�");
					connection.rollback();
					connection.close();
					return result;
				}
				if (result.getCount("ORG_CODE") < 0) {
					// ��������
//					result.setErrCode(-2);
//					result.setErrText(dept_chn_desc + "��" + inv_chn_desc
//							+ "���޴�����");
//					connection.rollback();
//					connection.close();
//					return result;
				}else {
					result = saveInvStockD(result, qty, connection, opt_user,
							opt_term,checkFlg);
				}
			
			
		}

		return result;

	}

	/** 
	 * ѭ����inv_stockd d ΪҪ�۵�����
	 * */
	public TParm saveInvStockD(TParm invSockDParm, double d,
			TConnection connection, String opt_user, String opt_term,String checkFlg) {

		double qty = invSockDParm.getDouble("STOCK_QTY", 0);
		if (qty >= d) {
			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0),
					invSockDParm.getValue("INV_CODE", 0), invSockDParm
							.getValue("BATCH_SEQ", 0), qty - d, connection,
					opt_user, opt_term, d,checkFlg);
		} else {                   
			//���� ����100  ʵ�ʿ��60
			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0),
					invSockDParm.getValue("INV_CODE", 0), invSockDParm
							.getValue("BATCH_SEQ", 0), qty, connection,
					opt_user, opt_term, qty,checkFlg);
			d = d - qty;
			if (invSockDParm.getCount("ORG_CODE") > 0) {
				invSockDParm.removeRow(0);
				this.saveInvStockD(invSockDParm, d, connection, opt_user,
						opt_term,checkFlg);
			} else {
				return invSockDParm;
			}
		}
		return invSockDParm;
	}

	/**
	 * �ۿⷽ�� org inv batch_seq qty
	 * */
	public TParm updateInvStockD(String org, String inv, String batch_seq,
			double qty, TConnection connection, String opt_user,
			String opt_term, Double d,String checkFlg) {
		TParm result = new TParm();                     
		// String updInvStockD =
		// "UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY - "+qty+" WHERE ORG_CODE = '"+org+"' AND INV_CODE = '"+inv+"' AND BATCH_SEQ = '"+batch_seq+"'";
		// System.out.println("updateInvStockDִ�С�������");
		TParm updInvStockDParm = new TParm();
//		updInvStockDParm.setData("STOCK_QTY", d); 
		updInvStockDParm.setData("QTY", qty);  
		updInvStockDParm.setData("ORG_CODE", org);  
		updInvStockDParm.setData("INV_CODE", inv);
		updInvStockDParm.setData("BATCH_SEQ", batch_seq); 
		updInvStockDParm.setData("OPT_USER", opt_user);
		updInvStockDParm.setData("OPT_TERM", opt_term);
		//�̵�ۿ�����һ��sql 
		if(checkFlg.equals("Y")){
	    result = INVRegressGoodTool.getInstance().CheckUpdInvStockD(updInvStockDParm,
					connection);	
		}   
		else{
		result = INVRegressGoodTool.getInstance().updInvStockD(updInvStockDParm,
				connection);
		}
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	
	
	
}
