package action.ekt;

//import java.sql.Timestamp;
//import java.util.Map;

import java.text.SimpleDateFormat;

import com.dongyang.action.TAction;
//import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import jdo.bil.BILInvoiceTool;
import jdo.ekt.EKTNewIO;
//import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.manager.TIOM_AppServer;

import jdo.ekt.EKTIO;
//import jdo.bil.BILGreenPathTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.opb.OPBTool;
//import jdo.opd.OrderTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import junit.textui.ResultPrinter;
//import jdo.reg.REGTool;
//import jdo.sys.Operator;
//import jdo.sys.PatTool;
//import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ҽ�ƿ�����
 * </p>
 * 
 * <p>
 * Description:ҽ�ƿ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 20111007
 * @version 2.0
 */
public class EKTAction extends TAction {
	public EKTAction() {
	}
	 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * ҽ�ƿ�����д��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm TXEKTRenewCard(TParm parm) {
		TConnection connection = getConnection();
		// �޸�ҽ�ƿ�������:����ǰ����������ʧ��״̬
		TParm result = EKTTool.getInstance()
				.updateEKTIssuelog(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ���ҽ�ƿ�������
		result = EKTTool.getInstance().insertEKTIssuelog(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ҽ�ƿ�������
		result = EKTIO.getInstance().createCard(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ҽ�ƿ���ϸ����������
		String businessNo = EKTTool.getInstance().getBusinessNo();
		TParm businessParm = parm.getParm("businessParm");
		businessParm.setData("BUSINESS_NO", businessNo);
		businessParm.setData("GREEN_BUSINESS_AMT", 0);
		businessParm.setData("GREEN_BALANCE", 0);
		businessParm.setData("BUSINESS_TYPE", "");
		result = EKTTool.getInstance()
				.insertEKTDetail(businessParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		//add by huangtt 20160921 ����ҽ�ƿ���ʷ ��������
		TParm historyParm = EKTNewIO.getInstance().getEktMasterHistoryParm(businessParm);
		String sql = EKTNewIO.getInstance().getEktMasterHistorySql(historyParm,"");
		result = new TParm(TJDODBTool.getInstance().update(sql,connection));
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		// zhangp 20111222 ҽ�ƿ���ֵ�˿��������
		TParm bilParm = parm.getParm("bilParm");
		// =======zhangp 20120227 modify start
		// String billBusinessNo = EKTTool.getInstance().getBillBusinessNo();
		bilParm.setData("BIL_BUSINESS_NO", businessNo);
		bilParm.setData("PRINT_NO", "");
		// bilParm.setData("BIL_BUSINESS_NO", billBusinessNo);
		// =======zhangp 20120227 modify end
		// System.out.println("д��bilParm==="+bilParm);
		result = EKTTool.getInstance().insertEKTBilPay(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ�ƿ���ֵ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============pangben 20111007
	 */
	public TParm TXEKTonFee(TParm parm) {
		//System.out.println("actiompamrm-----"+parm);
		TConnection connection = getConnection();
		//===================================add by huangjw 20150710
		double initAmt=0.00;//�ڳ����
		double lastAmt=0.00;//��ĩ���
		TParm initParm=new TParm();
		initParm.setData("MR_NO",parm.getValue("MR_NO"));
		initParm.setData("WRITE_FLG","Y");
		initAmt=EKTTool.getInstance().queryCurrentBalance(initParm).getDouble("CURRENT_BALANCE",0);
		//===================================add by huangjw 20150710
		// �������
		TParm result = null;
		//======20130509 yanjing modify��ҽ�ƿ�EKT-MASER��д��һ��������
//		 if (parm.getBoolean("FLG")) {
//			 // �½�ҽ�ƿ���Ϣ
		 result = EKTIO.getInstance().createCard(parm, connection);
//		 } else {
		 // ����ҽ�ƿ���Ϣ
//		 result = EKTTool.getInstance().updateEKTMaster(parm, connection);
//		 }
		 if (result.getErrCode() < 0) {
			 connection.close();
			 return result;
		 }
		// ҽ�ƿ���ϸ����������
		String businessNo = EKTTool.getInstance().getBusinessNo();
		TParm businessParm = parm.getParm("businessParm");
		businessParm.setData("BUSINESS_NO", businessNo);
		businessParm.setData("GREEN_BALANCE", 0.00);
		businessParm.setData("GREEN_BUSINESS_AMT", 0.00);
		businessParm.setData("BUSINESS_TYPE", "");
		result = EKTTool.getInstance()
				.insertEKTDetail(businessParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		//add by huangtt 20160921 ����ҽ�ƿ���ʷ ��������
		TParm historyParm = EKTNewIO.getInstance().getEktMasterHistoryParm(businessParm);
		String sql = EKTNewIO.getInstance().getEktMasterHistorySql(historyParm,"");
		result = new TParm(TJDODBTool.getInstance().update(sql,connection));
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		
		TParm billParm = parm.getParm("billParm");
		// ҽ�ƿ���ֵ��
		// ===zhangp 20120314 start
		// String billBusinessNo = EKTTool.getInstance().getBillBusinessNo();
		// billParm.setData("BIL_BUSINESS_NO", billBusinessNo);
		billParm.setData("BIL_BUSINESS_NO", businessNo);
		billParm.setData("PRINT_NO", parm.getValue("PRINT_NO"));
		// ҽ�ƿ���ֵ����KET_BIL_PAY
		result = EKTTool.getInstance().insertEKTBilPay(billParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		
		//connection.close();
		
		//============================================ҽ�ƿ���־����add by huangjw 20150710 start
		TParm lastParm=new TParm();
		lastParm.setData("MR_NO",parm.getValue("MR_NO"));
		lastParm.setData("WRITE_FLG","Y");
		lastAmt=EKTTool.getInstance().queryCurrentBalance(lastParm).getDouble("CURRENT_BALANCE",0);
		//===================================add by huangjw 20150710
		TParm inParm=new TParm();
		String date=TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
		inParm.setData("MR_NO",parm.getValue("MR_NO"));
		inParm.setData("EKT_DATE",date);
		TParm queryParm=EKTTool.getInstance().queryEktBilLog(inParm);
		TParm logParm=new TParm();
		if(queryParm.getCount()>0){//��������
			logParm.setData("MR_NO",parm.getValue("MR_NO"));
			logParm.setData("EKT_DATE",date);
			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//�ڳ����
			logParm.setData("LAST_AMT",lastAmt);//��ĩ���
			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result=EKTTool.getInstance().updateEktBilLog(logParm, connection); 
			
		}else{//��������
			logParm.setData("MR_NO",parm.getValue("MR_NO"));
			logParm.setData("EKT_DATE",date);
			logParm.setData("INIT_AMT",initAmt);//�ڳ����
			logParm.setData("LAST_AMT",lastAmt);//��ĩ���
			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result=EKTTool.getInstance().insertEktBilLog(logParm, connection);
		}
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		//============================================ҽ�ƿ���־����add by huangjw 20150710 end
		
		//==============================ҽ�ƿ���ֵ���˷�����add by kangy 20160804 start
		result=EKTNewIO.getInstance().EKTFee(parm, connection, businessNo);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		//=============================ҽ�ƿ���ֵ�˷�����add by kangy 20160804 end

		
		connection.commit();
		connection.close();

		// result.setData("BIL_BUSINESS_NO", billBusinessNo);// ��ֵ�վݺ�
		result.setData("BIL_BUSINESS_NO", businessNo);// ��ֵ�վݺ�
		// ===zhangp 20120314 end
		return result;
	}

	/**
	 * ����������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertData(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTGreenPathTool.getInstance().insertdata(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		// ���¹Һ�����������ɫͨ�����
		result = updateEKTGreen(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ���¹Һ�����������ɫͨ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateEKTGreen(TParm parm, TConnection conn) {
		// ���¹Һ�����������ɫͨ�����
		TParm result = PatAdmTool.getInstance().updateEKTGreen(
				ektParmTemp(parm), conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		// ��ô˾��ﲡ����ɫͨ�����
		TParm check = PatAdmTool.getInstance().selEKTByMrNo(ektParm);
		double GREEN_BALANCE = check.getDouble("GREEN_BALANCE", 0);
		double GREEN_PATH_TOTAL = check.getDouble("GREEN_PATH_TOTAL", 0);
		ektParm.setData("GREEN_BALANCE", StringTool.round(parm
				.getDouble("APPROVE_AMT"), 2)
				+ GREEN_BALANCE);
		ektParm.setData("GREEN_PATH_TOTAL", StringTool.round(parm
				.getDouble("APPROVE_AMT"), 2)
				+ GREEN_PATH_TOTAL);
		return ektParm;
	}

	/**
	 * ����һ����ɫͨ����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm cancelGreenPath(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTGreenPathTool.getInstance().cancleGreenPath(parm,
				conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		parm.setData("APPROVE_AMT", -parm.getDouble("APPROVE_AMT"));
		// ���¹Һ�����������ɫͨ�����
		result = updateEKTGreen(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ɾ���Һ�ʧ�ܳ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteRegOldData(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTTool.getInstance().deleteTrade(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		result = EKTTool.getInstance().deleteDetail(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ҽ�ƿ��ս���� ==============zhangp
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onEKTAccount(TParm parm) {
		TConnection connection = this.getConnection();
		TParm result = new TParm();
		result = EKTTool.getInstance().executeEKTAccount(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		//����BIL_INVRCP���ս��ֶ�
		result=EKTTool.getInstance().updateAccountSeq(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		//��BIL_ACCOUNT������ս���Ϣ
		result=EKTTool.getInstance().insertBilAccount(parm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ִ��ҽ��������� ҽ�ƿ����ױ� EKT_TREDE �� EKT_ACCNTDETAIL ���ҽ����������
	 * 
	 * @param parm
	 *            AMT:���β������ BUSINESS_TYPE :���β������� CASE_NO:�������
	 * @param type
	 *            ��9, ҽ���ۿ� 0,ҽ���س�
	 * @return
	 */
	public TParm exeInsSave(TParm parm) {
		// ��ֹ�ڷ������˵���
		TParm result = new TParm();
		TParm p = null;
		TConnection connection = getConnection();
		/**
		 * �����շ�/�˷�ʹ��
		 */
		if (null != parm.getValue("EXE_FLG")
				&& parm.getValue("EXE_FLG").equals("Y")) {
			parm.setData("ID_NO",parm.getValue("ID_NO"));
			// parm.setData("CASE_NO", caseNo);
			parm.setData("NAME", parm.getValue("PAT_NAME"));
			parm.setData("CREAT_USER", parm.getValue("OPT_USER"));
			parm.setData("CURRENT_BALANCE", parm
					.getDouble("CURRENT_BALANCE")
					- parm.getDouble("AMT"));
			result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			result = EKTTool.getInstance().insertEKTMaster(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			//ҽ��ҽ�ƿ���Ʊ����
			p =EKTNewIO.getInstance().unInsOpbReceiptNo(parm, connection);
			if (p.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + p.getErrCode()
						+ p.getErrText());
				connection.rollback();
				connection.close();
				return p;
			}
			//ҽ���˷Ѳ��� ���޸�OPD_ORDER �����ڲ����׺���
//			parm.setData("BUSINESS_NO",p.getValue("TRADE_NO"));
//			result=EKTNewTool.getInstance().updateOpdOrderBusinessNo(parm, connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:EKTIO.createCard " + result.getErrCode()
//						+ result.getErrText());
//				connection.rollback();
//				connection.close();
//				return result;
//			}
		}
		// �����շ�ҽ�ƿ�����,ҽ���ָ��� ����INSFEEPrintControl�����
		if (null != parm.getValue("INS_EXE_FLG")
				&& parm.getValue("INS_EXE_FLG").equals("Y")) {
			TParm orderParm = parm.getParm("orderParm");
			TParm readCard = parm.getParm("readCard");// ̩��ҽ�ƿ���������
			// TParm cp=new TParm();
			// ��ѯ�˾��ﲡ���������ݻ��ܽ��
			TParm cp = new TParm();
			cp.setData("EKT_USE", - parm.getDouble("INS_AMT"));// ҽ�ƿ��ۿ���=û�д�Ʊ���ܽ��-ҽ�����
			cp.setData("EKT_OLD_AMT", readCard.getDouble("CURRENT_BALANCE"));// ҽ�ƿ����ڲ���֮ǰ�Ľ��
			// �����˴ζ����Ĵ���ǩ�����н��
			// �س�
			// ��õ�ǰ
			// ҽ�ƿ��Ľ�
			cp.setData("GREEN_BALANCE", 0.00);// ������ۿ���
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));// ��������
			cp.setData("MR_NO", readCard.getValue("MR_NO"));// ������
			cp.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));// ��������
			// cp.setData("OLD_AMT", readCard.getDouble("CURRENT_BALANCE"));//
			// ҽ�ƿ�ԭ�н��
			cp.setData("BUSINESS_TYPE", "OPBT");// ����
			cp.setData("GREEN_PATH_TOTAL", 0);// �������������
			cp.setData("GREEN_USE", 0);// ������˴οۿ���
			cp.setData("OPT_USER", parm.getValue("OPT_USER"));// ������
			cp.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			cp.setData("IDNO", readCard.getValue("IDNO"));// ���֤��
			cp.setData("billAmt", parm.getDouble("billAmt"));//δ�շѽ��
			//cp.setData("TRADE_SUM_NO",orderParm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
			// // CURRENT_BALANCE:ҽ�ƿ����ڽ��
			double ektAmt = readCard.getDouble("CURRENT_BALANCE")
					+ parm.getDouble("INS_AMT") - parm.getDouble("billAmt");
			cp.setData("EKT_AMT", ektAmt);// ҽ�ƿ���ǰ���
			
			//TParm billParm = orderParm.getParm("parmBill");//δ�շ�ҽ������
			
			cp.setData("CONFIRM_NO", parm.getData("CONFIRM_NO")); //ҽ������� add by huangtt 20160920
			// ̩��ҽԺ�ۿ����
			p = new TParm(EKTNewIO.getInstance().onNewSaveInsFee(cp.getData()));
			if (p.getErrCode() < 0) {
				connection.close();
				return p;
			}
			//TParm newParm = orderParm.getParm("parmSum");// ����ҽ��
			orderParm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//ҽ��˳��� add by huangtt 20160920
			orderParm.setData("HISTORY_NO",p.getValue("HISTORY_NO"));  //add by huangtt 20160920ҽ�ƿ���ʷ���ϵ�ID��
			orderParm.setData("TRADE_NO",p.getValue("TRADE_NO"));
			orderParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ������
			orderParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			orderParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
			//orderParm.setData("billParm", orderParm.getParm("parmBill").getData());//δ�շ�ҽ������
			result = OPBTool.getInstance().onHl7ExeBillFlg(orderParm, connection);
			if (result.getErrCode() < 0) {
				// result = ektCancel(parm);
				if (result.getErrCode() < 0) {
					System.out.println("ҽ�ƿ��ع���Ϣ����ʧ��");
				}
				connection.close();
				return result;
			}
			parm.setData("EXE_USER", parm.getValue("OPT_USER"));
			parm.setData("EXE_TERM", parm.getValue("OPT_TERM"));
			parm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));//=====pangben 2013-3-13 �޸Ĵ�ǰ̨¼��
			result = INSRunTool.getInstance().deleteInsRun(parm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.close();
				return result;
			}
			result = INSTJFlow.getInstance().updateInsAmtFlgPrint(parm,
					parm.getValue("RECP_TYPE"), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return p;
	}
	/**
	 * ҽ�ƿ���ֵ�˷Ѳ�ӡ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============kangy 20160805
	 */
	public TParm TXEKTReprint(TParm parm) {
		//System.out.println("sssss"+parm);
		TParm result = new TParm(); 
		TParm insertParm=new TParm();
		TParm updataParm=new TParm();
		TParm updateinvoiceParm=new TParm();
		TParm updateektbilpayparm=new TParm();
		TConnection connection = getConnection();
	
		
		//String updateno = StringTool.addString(parm.getValue("INV_NO"));
		
		insertParm=parm.getParm("insertparm");
		result=BILInvoiceTool.getInstance().insertFeeDate(insertParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		
		updataParm=parm.getParm("updateparm");
		//System.out.println("update===="+updataParm);
		result=BILInvoiceTool.getInstance().cancelDate(updataParm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		
		updateinvoiceParm=parm.getParm("updateinvoiceparm");
		result=BILInvoiceTool.getInstance().updateDatePrint(updateinvoiceParm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
 	    
		updateektbilpayparm=parm.getParm("updateektbilpayparm");
		result=BILInvoiceTool.getInstance().updateprintNo(updateektbilpayparm,connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
