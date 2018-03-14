package action.opd;

import jdo.odo.ODOSaveTool;
import jdo.opd.DiagRecTool;
import jdo.opd.DrugAllergyTool;
import jdo.opd.MedHistoryTool;
import jdo.opd.OPDSubjrecTool;
import jdo.opd.OrderTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.ekt.EKTTool;
import jdo.opb.OPBReceiptTool;
import jdo.bil.BILPrintTool;

import com.dongyang.util.StringTool;

import jdo.bil.BILInvoiceTool;

/**
 * 
 * <p>
 * Title: ҽ��վ������
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ehui 2008.9.8
 * @version 1.0
 */
public class ODOAction extends TAction {
	/**
	 * ����
	 */
	TConnection connection;

	/**
	 * ��ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		// �õ������
		String caseNo = parm.getValue("CASE_NO");
		if (caseNo.length() == 0)
			return err(-1, "CASE_NO is null");
		// �õ�������
		String mrNo = parm.getValue("MR_NO");
		if (mrNo.length() == 0)
			return err(-1, "MR_NO is null");
		TParm result = new TParm();
		String admType = parm.getValue("ADM_TYPE");
		// ��ѯ���߿�����Ϣ
		TParm subjectresult = OPDSubjrecTool.getInstance().query(caseNo,
				admType);

		if (subjectresult.getErrCode() != 0) {

			return err(subjectresult);
		}
		result.setData("SUBJREC", subjectresult.getData());

		// ��ѯҽ��
		TParm orderresult = OrderTool.getInstance().query(caseNo);
		if (orderresult.getErrCode() != 0) {
			return err(orderresult);
		}
		result.setData("ORDER", orderresult.getData());
		// ��ѯ����ʷ
		TParm medhistoryparm = new TParm();
		medhistoryparm.setData("MR_NO", mrNo);
		TParm medhistoryresult = MedHistoryTool.getInstance().selectdata(
				medhistoryparm);
		if (medhistoryresult.getErrCode() != 0) {
			return err(medhistoryresult);
		}
		result.setData("MEDHISTORY", medhistoryresult.getData());
		// ��ѯ���
		TParm diagrecparm = new TParm();
		diagrecparm.setData("CASE_NO", caseNo);
		TParm diagrecresult = new TParm();
		diagrecresult = DiagRecTool.getInstance().selectdata(diagrecparm);

		if (diagrecresult.getErrCode() != 0) {
			return err(diagrecresult);
		}
		result.setData("DIAGREC", diagrecresult.getData());
		// ��ѯ����ʷ
		TParm drugallergyparm = new TParm();
		drugallergyparm.setData("MR_NO", mrNo);
		TParm drugallergyresult = DrugAllergyTool.getInstance().selectdata(
				drugallergyparm);
		if (drugallergyresult.getErrCode() != 0) {
			return err(drugallergyresult);
		}
		result.setData("DRUGALLERGY", drugallergyresult.getData());
		// ��ѯҽ����ʷ
		TParm orderhistoryparm = new TParm();
		TParm orderhistoryresult = new TParm();
		// orderhistoryparm.setData("CASE_NO", caseNo);
		// orderhistoryresult = OrderHistoryTool.getInstance().selectdata(
		// orderhistoryparm);
		// if (orderhistoryresult.getErrCode() != 0) {
		// return err(orderhistoryresult);
		// }
		result.setData("ORDERHISTORY", orderhistoryresult.getData());

		return result;
	}

	/**
	 * �ż�ҽ��վ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("��������");
			return result;
		}
		// ȡ������
		TConnection conn = getConnection();
		result = ODOSaveTool.getInstance().onSave(parm, conn);
		if (result.getErrCode() != 0) {
			conn.rollback();
			conn.close();
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * �ֽ��ո�������ҽ��վʹ�� ����վݵ����ݣ�������˲�����Ӽ��˱����ݣ�����ʱ�޸�OPD_ORDER �� PRINT_FLG ��PRINT_NO
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveOpbRect(TParm parm) {
		TConnection conn = getConnection();
		String billFlg = parm.getValue("billFlg"); // �Ƿ���˱��
		String contractCode = parm.getValue("CONTRACT_CODE"); // ���˵�λ
		String cashierCode = parm.getValue("cashierCode"); // Ʊ�ݴ�����
		// ���ô洢Ʊ��
		TParm result = OPBReceiptTool.getInstance().initReceipt(parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			conn.close();
			return result;
		}
		String recpNo = result.getValue("RECEIPT_NO");
		// ���˲���
		if ("N".equals(billFlg)) {
			parm.setData("RECEIPT_NO", recpNo);
			result = BILPrintTool.getInstance().insertRecode(parm,
					contractCode, conn);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				conn.close();
				return result;

			}

		} else {
			// ������ִ���޸�Ʊ������
			TParm bilInvoice = new TParm();
			bilInvoice.setData("RECP_TYPE", "OPB");
			bilInvoice.setData("STATUS", "0");
			bilInvoice.setData("CASHIER_CODE", cashierCode);
			bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
			String updateNo = StringTool.addString(parm.getValue("PRINT_NO"));

			bilInvoice.setData("UPDATE_NO", updateNo);
			// ����дƱ
			result = BILInvoiceTool.getInstance().updateDatePrint(bilInvoice,
					conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}

		}
		// =========�˾��ﲡ����ִ�м��� OPD_ORDER �� PRINT_FLG �и���ΪN ��ִ�д�Ʊ������PRINT_NO=""
		TParm upOpdParm = new TParm();
		if ("N".equals(parm.getValue("billFlg"))) {
			upOpdParm.setData("PRINT_FLG", "N");
		} else
			upOpdParm.setData("PRINT_FLG", "Y");

		upOpdParm.setData("RECEIPT_NO", recpNo);
		upOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));

		result = OrderTool.getInstance().updateForOPBCash(upOpdParm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}

		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * ����ҽ��վ ҽ�ƿ�ɾ��ҽ������ ����ҽ�ƿۿ���� ѡ��ȡ����ť����ҽ������
	 * 
	 * @param parm
	 * =========pangben 2012-01-06
	 * @return
	 */
	public TParm concleDeleteOrder(TParm parm) {
		TConnection conn = getConnection();
		TParm result = OrderTool.getInstance().onInsertForOpbEkt(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * ҽ��վд��ʱû��rexp_code�ĺ�̨�����־
	 * @param parm
	 * @return
	 */
	public TParm noRexpCodeLog(TParm parm){
		System.out.println("REXP_CODEΪ��:::::::"+parm.getErrText());
		return parm;
	}
	/**
	 * �޸�ҩ��ִ��״̬
	 * @param parm
	 * yanjing
	 * 20130415
	 * @return
	 */
	public TParm updateEXEC_FLG(TParm parm){
		TConnection connection = getConnection();
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        for (int i = 0; i < parm.getCount(); i++) { 
        	  result = OrderTool.getInstance().getFlgUpdateDate(parm.getRow(i), connection);
        	  if (result.getErrCode() < 0) {
                  err("ERR:" + result.getErrCode() + result.getErrText() +
                      result.getErrName());
                  connection.rollback();
                  connection.close();
                  return result;
              }
		  }       
        connection.commit();
        connection.close();
        return result;
        }
	
	
	/**
	 * �����Ѫִ�п��Ҹ���
	 * @param parm
	 */
	 public TParm onSaveBlood(TParm inParm){
		 TConnection connection = getConnection();
	        TParm result = new TParm();
	        
	        TParm parm = inParm.getParm("updateParm");
	        for (int i = 0; i < parm.getCount(); i++) {
	        	result = OrderTool.getInstance().getUpdateExecDeptBlood(parm.getRow(i), connection);
	        	  if (result.getErrCode() < 0) {
	                  err("ERR:" + result.getErrCode() + result.getErrText() +
	                      result.getErrName());
	                  connection.rollback();
	                  connection.close();
	                  return result;
	              }
			  } 
	        
	        String[] sql=(String[])inParm.getData("SQL");
			if(sql==null){
				result.setErrCode(-1);
				connection.rollback();
                connection.close();
				return result;
			}
			if(sql.length<1){
				result.setErrCode(-1);
				connection.rollback();
                connection.close();
				return result;
			}
			for(String tempSql:sql){
				result=new TParm(TJDODBTool.getInstance().update(tempSql, connection));
				if(result.getErrCode()!=0){
					System.out.println("ODOSaveTool wrong sql:"+tempSql);
					connection.rollback();
	                connection.close();
	                return result;
				}
			}
	        
	        connection.commit();
	        connection.close();
	        return result;
	 }
	
	 public TParm insertOpddrugallergy(TParm parm){
		TConnection connection = getConnection(); 
		TParm result = new TParm();
		
		for(int i = 0;i<parm.getCount("MR_NO");i++){
			String insertSql = "INSERT INTO opd_drugallergy(MR_NO,ADM_DATE, DRUG_TYPE,DRUGORINGRD_CODE, "
					+ "ADM_TYPE, CASE_NO,DEPT_CODE, DR_CODE, ALLERGY_NOTE,"
					+ "OPT_USER, OPT_DATE, OPT_TERM) "
					+ "VALUES"
					+ "('#','#','#','#','#','#','#','#','#','#',TO_DATE('#','YYYY-MM-DD HH24:MI:SS'),'#')";
			insertSql = insertSql.replaceFirst("#", parm.getValue("MR_NO",i))
								 .replaceFirst("#", parm.getValue("ADM_DATE",i))
								 .replaceFirst("#", parm.getValue("DRUG_TYPE",i))
								 .replaceFirst("#", parm.getValue("DRUGORINGRD_CODE",i))
								 .replaceFirst("#", parm.getValue("ADM_TYPE",i))
								 .replaceFirst("#", parm.getValue("CASE_NO",i))
								 .replaceFirst("#", parm.getValue("DEPT_CODE",i))
								 .replaceFirst("#", parm.getValue("DR_CODE",i))
								 .replaceFirst("#", parm.getValue("ALLERGY_NOTE",i))
								 .replaceFirst("#", parm.getValue("OPT_USER",i))
								 .replaceFirst("#", parm.getValue("OPT_DATE",i))
								 .replaceFirst("#", parm.getValue("OPT_TERM",i));
			System.out.println("88888888888"+insertSql);
			result = new TParm (TJDODBTool.getInstance().update(insertSql,connection));
			if(result.getErrCode()<0){
				connection.rollback();
                connection.close();
                return result;
			}
		}
        connection.commit();
        connection.close();
		return result;
		 
	 }
	
    }
