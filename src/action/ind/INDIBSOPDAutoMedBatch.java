package action.ind;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jdo.ibs.IBSOrderdTool;
import jdo.ind.INDIBSOPDAutoMedTool;
import jdo.ind.IndOrgTool;
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndRequestDTool;
import jdo.spc.IndRequestMTool;
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import psyg.graphic.SysObj;
import action.ind.client.SpcIndRequestd;
import action.ind.client.SpcIndRequestm;
import action.ind.client.SpcService_SpcServiceImplPort_Client;

import com.dongyang.config.TConfig;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: סԺ���ż��ﱸҩ����
 * </p>
 * 
 * <p>
 * Description: סԺ���ż��ﱸҩ����
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
public class INDIBSOPDAutoMedBatch extends Patch {

	public INDIBSOPDAutoMedBatch() {

	}

	private String app_org_code; // ���벿��

	private String to_org_code; // ���ܲ���

	private String request_no; // ���뵥��

	private String u_type = "1";

	private String request_type = "EXM"; // ����Ʒ�

	private boolean out_flg = true; // ����flg

	private boolean in_flg = false; // ���flg

	private String dispense_no; // ���ⵥ��

	// ϸ�����
	private int seq;

	private String opt_user = ""; // ������Ա

	public static final int DISPENSE_ERR_CODE = 99; // ������

	private String batch_date; // ����ִ�еĲ�����

	// private TConnection conn;
	
	private Timestamp requestAndDispenseDate = null;
	
	
	@Override
	public boolean run() {
		// ��������
		requestAndDispenseDate = SystemTool.getInstance().getDate();
		// ��ʼ�ͽ���ʱ�� ��ȡ start
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(SystemTool.getInstance().getDate().getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String endD = sdf.format(cal.getTime());
		cal.add(Calendar.MONTH, -1);// �ع�һ����
		String startD = sdf.format(cal.getTime());
		batch_date = endD.substring(0, 6);
		// ��ʼ�ͽ���ʱ�� ��ȡ end
		TParm queryParm = new TParm();

		// ��ȡ���е����벿��
		TConnection conn = TDBPoolManager.getInstance().getConnection();
		queryParm.setData("ORG_TYPE", "C");
		queryParm.setData("EXINV_FLG", "Y");
		TParm appOrgParm = IndOrgTool.getInstance().getOrgCode(queryParm, conn);
		conn.close();

		queryParm.removeData("EXINV_FLG");
		queryParm.removeData("ORG_TYPE");
		queryParm.setData("START_DATE", startD);
		queryParm.setData("END_DATE", endD);
		queryParm.setData("REQUEST_FLG_B", "N");

		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			queryParm.setData("REGION_CODE", Operator.getRegion());
		}
		
		StringBuilder message = new StringBuilder("");
		
		TParm result = null;
		String type = "IBS"; // OPD���ż�֢ IBS:סԺ
		this.to_org_code = "040103"; // to_org_codeסԺҩ��
		this.opt_user = "INDIBSAutoMed";
		TParm requestM = null;
		TParm requestD = null;
		//if (getOrgBatchFlg(to_org_code)) { //��ʱȥ�� BATCH_FLG�ж�
			for (int i = 0; i < appOrgParm.getCount("ID"); i++) {
				app_org_code = String.valueOf(appOrgParm.getData("ID", i));
				queryParm.setData("TYPE", type);
				queryParm.setData("APP_ORG_CODE", app_org_code);
				queryParm.setData("TO_ORG_CODE", to_org_code);
				result = INDTool.getInstance().onQueryDeptExm(queryParm);
				if (result.getErrCode() < 0) {
					continue;
				}
				requestM = result.getParm("RESULT_M", 0);
				requestD = result.getParm("RESULT_D", 0);
				if (requestM.getCount("REQUEST_NO") <= 0
						&& requestD.getCount("PAT_NAME") <= 0) {
					continue;
				}
				// �������뵥 start ͬʱ���ӳ���
				result = onSaveRequestAndDispense(result, type);
				if(result.getErrCode() < 0) {
					message.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "��סԺҩ��������� ʧ�ܣ�\n");
				} else {
					message.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "��סԺҩ��������� �ɹ���\n");
				}
				// ������ⵥ start ͬʱ���ӳ���
			}
		
//		} else {
//			// ��������Ϣ��ӡ����̨log��
//			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//			String fileNmae = (new StringBuilder())
//					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
//					.append("\\�������Զ���ҩ���δ���(סԺҩ��)").append(batch_date).append(".txt")
//					.toString();
//			try {
//				FileTool.setString(fileNmae, "[" + sdf.format(new Date())
//						+ "] סԺҩ�������̵�״̬��סԺҩ���Զ���ҩʧ�ܣ�\n");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
			String fileNmae1 = (new StringBuilder())
					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
					.append("\\�������Զ���ҩ���ν��(סԺҩ��)").append(batch_date).append(".txt")
					.toString();
			try {
				FileTool.setString(fileNmae1, message.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}	
		
			
		StringBuilder message1 = new StringBuilder("");	
		result = null;
		requestM = null;
		requestD = null;
		type = "OPD";
		this.to_org_code = "040102"; // to_org_code�ż���ҩ�� ����
		this.opt_user = "INDOPDAutoMed";
//		if (getOrgBatchFlg(to_org_code)) {
			for (int i = 0; i < appOrgParm.getCount("ID"); i++) {
				app_org_code = String.valueOf(appOrgParm.getData("ID", i));
				queryParm.setData("TYPE", type);
				queryParm.setData("APP_ORG_CODE", app_org_code);
				queryParm.setData("TO_ORG_CODE", to_org_code);
				result = INDTool.getInstance().onQueryDeptExm(queryParm);
				if (result.getErrCode() < 0) {
					continue;
				}
				requestM = result.getParm("RESULT_M", 0);
				requestD = result.getParm("RESULT_D", 0);
				if (requestM.getCount("REQUEST_NO") <= 0
						&& requestD.getCount("PAT_NAME") <= 0) {
					continue;
				}
				// �������뵥 start ͬʱ���ӳ���
				result = onSaveRequestAndDispense(result, type);
				if(result.getErrCode() < 0) {
					message1.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "���ż���ҩ��������� ʧ�ܣ�\n");
				} else {
					message1.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "���ż���ҩ��������� �ɹ���\n");
				}
				// ������ⵥ start ͬʱ���ӳ���
			}
			String fileNmae2 = (new StringBuilder())
					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
					.append("\\�������Զ���ҩ���ν��(�ż���ҩ��)").append(batch_date).append(".txt")
					.toString();
			try {
				FileTool.setString(fileNmae2, message1.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}	
//		} else {
//			// ��������Ϣ��ӡ����̨log��
//			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//			String fileNmae = (new StringBuilder())
//					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
//					.append("\\�������Զ���ҩ���δ���(�ż���)").append(batch_date).append(".txt")
//					.toString();
//
//			try {
//				FileTool.setString(fileNmae, "[" + sdf.format(new Date())
//						+ "] �ż���ҩ�������̵�״̬���ż���ҩ���Զ���ҩʧ�ܣ�\n");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		return true;
	}

	/**
	 * ���⼴�����ҵ
	 * 
	 * @param out_org_code
	 * @param in_org_code
	 * @param batchvalid
	 */
	private TParm getDispenseOutIn(TParm dispenseM, TParm dispenseD,
			String out_org_code, String in_org_code, String reuprice_flg,
			boolean out_flg, boolean in_flg, TConnection conn) {
		TParm parm = new TParm();
		in_flg = false;
		// ������Ϣ(OUT_M)
		parm = getDispenseMParm(parm, dispenseM, "3");
		// ϸ����Ϣ(OUT_D)
		parm = getDispenseDParm(parm, dispenseD);
		// ʹ�õ�λ
		parm.setData("UNIT_TYPE", u_type);
		// ���뵥����
		parm.setData("REQTYPE_CODE", request_type);
		// ���ⲿ��
		parm.setData("OUT_ORG_CODE", out_org_code);
		// ��ⲿ��
		parm.setData("IN_ORG_CODE", in_org_code);
		// �Ƿ����(IN_FLG)
		parm.setData("IN_FLG", in_flg);
		// �ж��Ƿ��Զ����ɱ��۴��������
		parm.setData("REUPRICE_FLG", reuprice_flg);
		// ���β�����
		parm.setData("BATCH_DATE", batch_date);
		// ִ����������
		// parm = INDTool.getInstance().onInsertDispenseOutIn(parm, conn);
		parm = INDIBSOPDAutoMedTool.getInstance().onInsertDispenseOutIn(parm,
				conn);
		// �����ж�
		return parm;
	}

	/**
	 * ������;��ҵ/��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
	 */
	private TParm getDispenseOutOn(String org_code, TParm dispenseM,
			TParm dispenseD, TConnection conn) {
		TParm parm = new TParm();
		parm = getDispenseMParm(parm, dispenseM, "1");
		parm = getDispenseDParm(parm, dispenseD);
		// ʹ�õ�λ
		parm.setData("UNIT_TYPE", u_type);
		// ���뵥����
		parm.setData("REQTYPE_CODE", request_type);
		// ���ⲿ��
		parm.setData("ORG_CODE", org_code);
		// ���β�����
		parm.setData("BATCH_DATE", batch_date);
		// ִ����������
		// parm = INDTool.getInstance().onInsertDispenseOutOn(parm, conn);
		parm = INDIBSOPDAutoMedTool.getInstance().onInsertDispenseOutOn(parm,
				conn);
		// �����ж�
		return parm;
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseMParm(TParm parm, TParm dispenseM,
			String update_flg) {
		TParm parmM = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		// ���ⵥ��
		dispense_no = "";
		if ("".equals(dispenseM.getValue("DISPENSE_NO"))) {
			dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
					"IND_DISPENSE", "No");
		} else {
			dispense_no = dispenseM.getValue("DISPENSE_NO");
		}
		parmM.setData("DISPENSE_NO", dispense_no);
		parmM.setData("REQTYPE_CODE", dispenseM.getData("REQTYPE_CODE"));
		parmM.setData("REQUEST_NO", dispenseM.getData("REQUEST_NO"));
		parmM.setData("REQUEST_DATE", dispenseM.getData("REQUEST_DATE"));
		parmM.setData("APP_ORG_CODE", dispenseM.getData("APP_ORG_CODE"));
		parmM.setData("TO_ORG_CODE", dispenseM.getData("TO_ORG_CODE"));
		parmM.setData("URGENT_FLG", dispenseM.getData("URGENT_FLG"));
		parmM.setData("DESCRIPTION", "�Զ���ҩ");
		parmM.setData("DISPENSE_DATE", requestAndDispenseDate);
		parmM.setData("DISPENSE_USER", opt_user);
		if (!"1".equals(update_flg)) {
			parmM.setData("WAREHOUSING_DATE", date);
			parmM.setData("WAREHOUSING_USER", "127.0.0.1");
		} else {
			parmM.setData("WAREHOUSING_DATE", tnull);
			parmM.setData("WAREHOUSING_USER", "");
		}
		if (dispenseM.getData("REASON_CHN_DESC") == null) {
			parmM.setData("REASON_CHN_DESC", "");
		} else {
			parmM.setData("REASON_CHN_DESC",
					dispenseM.getData("REASON_CHN_DESC"));
		}

		parmM.setData("UNIT_TYPE", u_type);
		if ("WAS".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "THO".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "COS".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "SRD".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "EXM".equals(dispenseM.getData("REQTYPE_CODE"))) {
			update_flg = "3";
		}
		parmM.setData("UPDATE_FLG", update_flg);
		parmM.setData("OPT_USER", opt_user);
		parmM.setData("OPT_DATE", date);
		parmM.setData("OPT_TERM", "127.0.0.1");
		// zhangyong20110517
		parmM.setData("REGION_CODE", "H01");

		parmM.setData("DRUG_CATEGORY", dispenseM.getData("DRUG_CATEGORY"));
		// ���뷽ʽ--ȫ��:APP_ALL,�˹�:APP_ARTIFICIAL,���콨��:APP_PLE,�Զ��β�:APP_AUTO

		if (parmM != null) {
			parm.setData("OUT_M", parmM.getData());
		}
		return parm;
	}

	/**
	 * �����ϸ��Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseDParm(TParm parm, TParm dispenseD) {
		TParm parmD = new TParm();
		TParm stockParm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		int count = 0;
		String order_code = "";
		Object valid_date = "";
		TParm orderParm = null;
		for (int i = 0; i < dispenseD.getCount("ORDER_CODE"); i++) {
			if (dispenseD.getData("ORDER_CODE", i) != null) {
				order_code = dispenseD.getData("ORDER_CODE", i) + "";
			} else {
				order_code = "";
			}
			//��ȡҩƷ��Ϣ
			stockParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getStockQtyInfo(order_code)));
			stockParm = stockParm.getRow(0);
			parmD.setData("RETAIL_PRICE", count,
					stockParm.getDouble("RETAIL_PRICE"));
			parmD.setData("STOCK_PRICE", count,
					stockParm.getDouble("STOCK_PRICE"));
			parmD.setData("PHA_TYPE", count, stockParm.getData("PHA_TYPE"));
			parmD.setData("DOSAGE_QTY", count,
					stockParm.getDouble("DOSAGE_QTY"));
			parmD.setData("ORDER_DESC", count, stockParm.getData("ORDER_DESC"));
			// �������
			parmD.setData("DISPENSE_NO", count, dispense_no);
			parmD.setData("SEQ_NO", count, seq + count);
			parmD.setData("REQUEST_SEQ", count, dispenseD.getInt("SEQ_NO", i));

			parmD.setData("ORDER_CODE", count, order_code);
			orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			parmD.setData("QTY", count, dispenseD.getDouble("QTY", i));
			parmD.setData("UNIT_CODE", count,
					orderParm.getData("DOSAGE_UNIT", 0));
			parmD.setData("ACTUAL_QTY", count, dispenseD.getDouble("QTY", i));
			parmD.setData("BATCH_SEQ", count, dispenseD.getInt("BATCH_SEQ", i));
			parmD.setData("BATCH_NO", count, dispenseD.getData("BATCH_NO", i));
			valid_date = dispenseD.getData("VALID_DATE", i);
			if (valid_date != null) {
				parmD.setData("VALID_DATE", count, tnull);
			} else {
				parmD.setData("VALID_DATE", count,
						dispenseD.getTimestamp("VALID_DATE", i));
			}

			parmD.setData("OPT_USER", count, opt_user);
			parmD.setData("OPT_DATE", count, date);
			parmD.setData("OPT_TERM", count, "127.0.0.1");

			// �Ƿ��¼�
			parmD.setData("IS_BOXED", count, "Y");
			parmD.setData("BOXED_USER", count, "");
			parmD.setData("BOX_ESL_ID", count, "");

			// ���ӱ�ǩӦ��
			if (dispenseD.getData("ELETAG_CODE", i) == null) {
				parmD.setData("ELETAG_CODE", count, "");
			} else {
				parmD.setData("ELETAG_CODE", count,
						dispenseD.getData("ELETAG_CODE", i));
			}
			count++;
		}
		if (parmD != null) {
			parm.setData("OUT_D", parmD.getData());
		}
		return parm;
	}

	/**
	 * ����Ƿ���춯״̬�ж�
	 * 
	 * @param org_code
	 * @return
	 */
	private boolean getOrgBatchFlg(String org_code) {
		// ����Ƿ���춯״̬�ж�
		if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
			return false;
		}
		return true;
	}

	/**
	 * �������ҵ״̬�ж�
	 * 
	 * @return
	 */
	private String getDisCheckFlg(TParm parm) {
		// �������ҵ״̬�ж�
		if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
				&& !"".equals(to_org_code) && !"".equals(app_org_code)) {
			// ��������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->��;״̬
			return "1";
		} else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
				&& !"".equals(to_org_code) && !"".equals(app_org_code)) {
			// ����������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->���⼴���
			return "2";
		}
		return "1";
	}

	// �������뵥
	private TParm onSaveRequestAndDispense(TParm res, String type) {
		TConnection connection1 = TDBPoolManager.getInstance().getConnection();
		TParm result = new TParm();
		TParm requestM = res.getParm("RESULT_M", 0);
		TParm requestD = res.getParm("RESULT_D", 0);
		TParm parm = new TParm();
		getRequestExmParmM(parm, requestM);
		getRequestExmParmD(parm, requestD);
		if ("OPD".equals(type)) {
			parm.setData("TYPE", "E");
		} else if ("IBS".equals(type)) {
			parm.setData("TYPE", "I");
		}
		getDeptRequestUpdate(parm, requestM, requestD, type);
		result = onCreateDeptExmRequest(parm, connection1);
		// �����ж�
		if (result == null || result.getErrCode() < 0) {
			// connection1.rollback();
			connection1.close();
			return result;
		} else {
			connection1.commit();
			connection1.close();
		}

		/**** �������� ������ⵥ start */
		TConnection connection2 = TDBPoolManager.getInstance().getConnection();
		result = new TParm();
		// ҩ�������Ϣ
		TParm sysParm = IndSysParmTool.getInstance().onQuery();
		// �������ҵ״̬�ж�(1-���ȷ�ϣ�2-���⼴���)
		String dis_check = getDisCheckFlg(sysParm);
		// �Ƿ��д����۸�
		String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
		if ("1".equals(dis_check)) {
			// ���ⲿ�ſ���Ƿ���춯
//			if (!getOrgBatchFlg(to_org_code)) {
//				result.setErrCode(DISPENSE_ERR_CODE);
//				connection2.close();
//				return result;
//			}
			// ������;��ҵ/��������������ҵ(���⼴���)
			result = getDispenseOutOn(to_org_code, parm.getParm("REQUEST_M"),
					parm.getParm("REQUEST_D"), connection2);
			if (result == null || result.getErrCode() < 0) {
				connection2.close();
				return result;
			} else {
				connection2.commit();
				connection2.close();
			}
			return result;

		} else if ("2".equals(dis_check)) {
			// ���ⲿ�ſ���Ƿ���춯
//			if (!getOrgBatchFlg(to_org_code)) {
//				result.setErrCode(DISPENSE_ERR_CODE);
//				connection2.close();
//				return result;
//			}
//			// ��ⲿ�ſ���Ƿ���춯
//			if (!"".equals(app_org_code) && !getOrgBatchFlg(to_org_code)) {
//				result.setErrCode(DISPENSE_ERR_CODE);
//				connection2.close();
//				return result;
//			}
			// ���⼴�����ҵ(����ⲿ�ž���Ϊ��)
			result = getDispenseOutIn(parm.getParm("REQUEST_M"),
					parm.getParm("REQUEST_D"), to_org_code, app_org_code,
					reuprice_flg, out_flg, in_flg, connection2);
			if (result == null || result.getErrCode() < 0) {
				connection2.close();
				return result;
			} else {
				connection2.commit();
				connection2.close();
			}
			return result;
		}
		return result;
		/**** �������� ������ⵥ end */
	}

	/**
	 * �������ݣ����뵥����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmM(TParm parm, TParm requestM) {
		TParm inparm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		request_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "EXM");
		inparm.setData("APP_ORG_CODE", app_org_code);
		inparm.setData("TO_ORG_CODE", to_org_code);
		inparm.setData("REQUEST_DATE", requestAndDispenseDate);
		inparm.setData("REQUEST_USER", opt_user);
		inparm.setData("REASON_CHN_DESC", "�Զ���ҩ");
		inparm.setData("DESCRIPTION", "�Զ���ҩ");
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("OPT_USER", opt_user);
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", "127.0.0.1");
		// zhangyong20110517
		inparm.setData("REGION_CODE", "H01");
		// ҩƷ����
		inparm.setData("DRUG_CATEGORY", "1");
		inparm.setData("APPLY_TYPE", "1");
		parm.setData("REQUEST_M", inparm.getData());
		return parm;
	}

	/**
	 * �������ݣ����뵥ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmD(TParm parm, TParm resquestD) {
		TParm inparm = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();

		int count = 0;
//		for (int i = 0; i < resquestD.getCount("PAT_NAME"); i++) {
//			inparm.addData("REQUEST_NO", request_no);
//			inparm.addData("SEQ_NO", count + 1);
//			inparm.addData("ORDER_CODE", resquestD.getData("ORDER_CODE", i));
//			inparm.addData("BATCH_NO", "");
//			inparm.addData("VALID_DATE", tnull);
//			inparm.addData("QTY", resquestD.getDouble("DOSAGE_QTY", i));
//			inparm.addData("ACTUAL_QTY", 0);
//			inparm.addData("UPDATE_FLG", "0");
//			inparm.addData("OPT_USER", opt_user);
//			inparm.addData("OPT_DATE", date);
//			inparm.addData("OPT_TERM", "127.0.0.1");
//			count++;
//		}
		//modify by wangjc 20171128   ������ͬҩƷ   ����ע�͵�����֮ǰ��д��
		for (int i = 0; i < resquestD.getCount("PAT_NAME"); i++) {
			boolean flg = true;
			for(int j=0;j<inparm.getCount();j++){
				if(inparm.getValue("ORDER_CODE", j).equals(resquestD.getValue("ORDER_CODE", i))){
					inparm.setData("QTY", j, resquestD.getDouble("DOSAGE_QTY", i)+inparm.getDouble("QTY", j));
					flg = false;
					break;
				}
			}
			if(flg){
				inparm.addData("REQUEST_NO", request_no);
				inparm.addData("SEQ_NO", count + 1);
				inparm.addData("ORDER_CODE", resquestD.getData("ORDER_CODE", i));
				inparm.addData("BATCH_NO", "");
				inparm.addData("VALID_DATE", tnull);
				inparm.addData("QTY", resquestD.getDouble("DOSAGE_QTY", i));
				inparm.addData("ACTUAL_QTY", 0);
				inparm.addData("UPDATE_FLG", "0");
				inparm.addData("OPT_USER", opt_user);
				inparm.addData("OPT_DATE", date);
				inparm.addData("OPT_TERM", "127.0.0.1");
				count++;
				inparm.setCount(count);
			}
		}
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
	}

	/**
	 * �������ݣ���������״̬
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getDeptRequestUpdate(TParm parm, TParm requestM,
			TParm requestD, String type) {
		TParm inparm = new TParm();
		int count = 0;
		if ("IBS".equals(type)) {
			for (int i = 0; i < requestD.getCount("PAT_NAME"); i++) {
				inparm.setData("CASE_NO", count,
						requestD.getValue("CASE_NO", i));
				inparm.setData("CASE_NO_SEQ", count,
						requestD.getInt("CASE_NO_SEQ", i));
				inparm.setData("SEQ_NO", count, requestD.getInt("SEQ_NO", i));
				inparm.setData("REQUEST_FLG", count, "Y");
				inparm.setData("REQUEST_NO", count, request_no);
				count++;
			}
		} else if ("OPD".equals(type)) {
			for (int i = 0; i < requestD.getCount("PAT_NAME"); i++) {
				inparm.setData("CASE_NO", count,
						requestD.getValue("CASE_NO", i));
				inparm.setData("RX_NO", count, requestD.getValue("RX_NO", i));
				inparm.setData("SEQ_NO", count, requestD.getInt("SEQ_NO", i));
				inparm.setData("REQUEST_FLG", count, "Y");
				inparm.setData("REQUEST_NO", count, request_no);
				count++;
			}
		}
		parm.setData("UPDATE", inparm.getData());
		return parm;
	}

	/**
	 * �������쵥
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onCreateDeptExmRequest(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// �������뵥����
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �������쵥ϸ��
		TParm requestD = parm.getParm("REQUEST_D");
//		System.out.println("requestD>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+requestD);
		String order_code = "";
		for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", requestD.getData("REQUEST_NO", i));
			inparm.setData("SEQ_NO", requestD.getData("SEQ_NO", i));
			order_code = requestD.getValue("ORDER_CODE", i);
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_NO", requestD.getData("BATCH_NO", i));
			inparm.setData("VALID_DATE", requestD.getData("VALID_DATE", i));
			inparm.setData("QTY", requestD.getData("QTY", i));
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			inparm.setData("UNIT_CODE", orderParm.getData("DOSAGE_UNIT", 0));
			inparm.setData("RETAIL_PRICE", orderParm.getData("RETAIL_PRICE", 0));
			inparm.setData("STOCK_PRICE", orderParm.getData("STOCK_PRICE", 0));
			inparm.setData("ACTUAL_QTY", requestD.getData("ACTUAL_QTY", i));
			inparm.setData("UPDATE_FLG", requestD.getData("UPDATE_FLG", i));
			inparm.setData("OPT_USER", requestD.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", requestD.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", requestD.getData("OPT_TERM", i));
			result = IndRequestDTool.getInstance().onInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// �����ż�ס״̬
		String type = parm.getValue("TYPE");
		TParm update = parm.getParm("UPDATE");
		if ("I".equals(type)) {
			// ����סԺ״̬
			String caseNo = "";
			int caseNoSeq = 0;
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				caseNoSeq = update.getInt("CASE_NO_SEQ", i);
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				result = IBSOrderdTool.getInstance().upForDeptMedic(caseNo,
						caseNoSeq, seqNo, requestFlg, requestNo, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		} else if ("O".equals(type) || "E".equals(type)) {
			// �����ż���״̬
			String caseNo = "";
			String rxNo = "";
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			String sql = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				rxNo = update.getValue("RX_NO", i);
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				sql = "UPDATE OPD_ORDER SET REQUEST_NO='" + requestNo
						+ "' ,REQUEST_FLG='" + requestFlg + "' WHERE CASE_NO='"
						+ caseNo + "' AND RX_NO='" + rxNo + "' AND SEQ_NO="
						+ seqNo;
				result = new TParm(TJDODBTool.getInstance().update(sql, conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * �������쵥������������[���ұ�ҩ����]
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCreateDeptExmRequestSpc(TParm parm, TConnection conn) {
		TParm result = new TParm();
		// TConnection connection =
		// TDBPoolManager.getInstance().getConnection();
		result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		TParm reqParm = parm.getParm("REQUEST_M");
		SpcIndRequestm indRequestm = getInsertM(reqParm);
		// System.out.println("reqParm---------:"+reqParm);
		TParm reqDParm = parm.getParm("REQUEST_D");
		// System.out.println("reqDParm---------:"+reqDParm);
		indRequestm = getInsertD(indRequestm, reqDParm);
		try {
			String out = SpcService_SpcServiceImplPort_Client
					.onSaveSpcRequest(indRequestm);

			if (out != null && !out.equals("success")) {
				result.setErrCode(-1);
				result.setErrText(out);

				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			result.setErrCode(-1);
			result.setErrText("���÷���˱���ʧ��");
			// conn.close();
			return result;
		}
		return result;
	}

	/**
	 * ��װ������������IND_REQUESTM������
	 * 
	 * @param obj
	 * @return
	 */
	private SpcIndRequestm getInsertM(TParm parm) {
		SpcIndRequestm obj = new SpcIndRequestm();
		obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
		obj.setAppOrgCode(parm.getValue("APP_ORG_CODE"));
		obj.setToOrgCode(parm.getValue("TO_ORG_CODE"));
		obj.setRequestDate(parm.getValue("REQUEST_DATE"));
		obj.setRequestUser(parm.getValue("REQUEST_USER"));
		obj.setReasonChnDesc(parm.getValue("REASON_CHN_DESC"));
		obj.setDescription(parm.getValue("DESCRIPTION"));

		obj.setUnitType(parm.getValue("UNIT_TYPE"));
		obj.setUrgentFlg(parm.getValue("URGENT_FLG"));
		obj.setOptUser(parm.getValue("OPT_USER"));

		obj.setOptDate(parm.getValue("OPT_DATE"));
		obj.setOptTerm(parm.getValue("OPT_TERM"));
		obj.setDrugCategory(parm.getValue("DRUG_CATEGORY"));
		obj.setRequestNo(parm.getValue("REQUEST_NO"));
		obj.setRegionCode(parm.getValue("REGION_CODE"));
		obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
		obj.setApplyType("1");
		return obj;
	}

	/**
	 * ��װ������������IND_REQUESTD������
	 * 
	 * @param obj
	 * @return
	 */
	private SpcIndRequestm getInsertD(SpcIndRequestm obj, TParm parm) {

		int count = parm.getCount("ORDER_CODE");
		List<SpcIndRequestd> list = new ArrayList<SpcIndRequestd>();
		for (int i = 0; i < count; i++) {
			TParm rowParm = parm.getRow(i);
			SpcIndRequestd objD = new SpcIndRequestd();

			String order_code = rowParm.getValue("ORDER_CODE");
			objD.setOrderCode(order_code);

			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));

			String unitCode = (String) orderParm.getData("DOSAGE_UNIT", 0);
			double retailPrice = orderParm.getDouble("RETAIL_PRICE", 0);
			double stockPrice = orderParm.getDouble("STOCK_PRICE", 0);

			objD.setUnitCode(unitCode);
			objD.setStockPrice(stockPrice);
			objD.setRetailPrice(retailPrice);

			objD.setQty(rowParm.getDouble("QTY"));
			objD.setActualQty(rowParm.getDouble("ACTUAL_QTY"));
			objD.setRequestNo(rowParm.getValue("REQUEST_NO"));
			objD.setSeqNo(rowParm.getInt("SEQ_NO"));
			objD.setBatchNo("");
			objD.setValidDate("");

			objD.setUpdateFlg(rowParm.getValue("UPDATE_FLG"));
			objD.setOptDate(rowParm.getValue("OPT_DATE"));
			objD.setOptTerm(rowParm.getValue("OPT_TERM"));
			objD.setOptUser(rowParm.getValue("OPT_USER"));
			list.add(objD);
		}
		obj.setIndRequestds(list);
		return obj;
	}
}
