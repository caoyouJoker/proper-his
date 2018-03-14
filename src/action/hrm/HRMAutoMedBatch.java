package action.hrm;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jdo.hrm.HRMDeptRequestTool;
import jdo.ind.IndOrgTool;
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import action.ind.client.SpcIndRequestd;
import action.ind.client.SpcIndRequestm;
import action.ind.client.SpcService_SpcServiceImplPort_Client;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: ������鱸ҩ��������
 * </p>
 * 
 * <p>
 * Description: ������鱸ҩ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wukai 20161222
 * @version 1.0
 */
public class HRMAutoMedBatch extends Patch {

	public HRMAutoMedBatch() {
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

	private TConnection conn;
	// ���
	private String type = "H";

	@Override
	public boolean run() {
		// ��������
		conn = TDBPoolManager.getInstance().getConnection();
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHssmm");
		String endD = sdf.format(cal.getTime());
		cal.add(Calendar.MONTH, -1);// �ع�һ����
		String startD = sdf.format(cal.getTime());
		// ��ʼ�ͽ���ʱ�� ��ȡ end
		TParm queryParm = new TParm();
		// ��ȡ���е����벿��
		// ��ȡ���еĽ��ܲ���
		queryParm.setData("ORG_TYPE", "C");
		queryParm.setData("EXINV_FLG", "Y");
		TParm appOrgParm = IndOrgTool.getInstance().getOrgCode(queryParm, conn);
		queryParm.setData("ORG_TYPE", "B");
		queryParm.removeData("EXINV_FLG");
		TParm toOrgParm = IndOrgTool.getInstance().getOrgCode(queryParm, conn);
		String appOrg = null;
		String toOrg = null;

		queryParm.removeData("ORG_TYPE");
		queryParm.setData("START_DATE", startD);
		queryParm.setData("END_DATE", endD);
		queryParm.setData("REQUEST_FLG_B", "N");

		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			queryParm.setData("REGION_CODE", Operator.getRegion());
		}
		TParm result = null;
		for (int i = 0; i < appOrgParm.getCount("ID"); i++) {
			appOrg = String.valueOf(appOrgParm.getData("ID", i));
			for (int j = 0; j < toOrgParm.getCount("ID"); j++) {
				toOrg = String.valueOf(toOrgParm.getData("ID", j));
				queryParm.setData("APP_ORG_CODE", appOrg);
				queryParm.setData("TO_ORG_CODE", toOrg);
				result = HRMDeptRequestTool.getInstance().onQueryDeptExm(
						queryParm);
				if (result.getErrCode() < 0) {
					continue;
				}
				// �������뵥 start ͬʱ���ӳ���
				onSaveRequestAndDispense(result, type);
				// ������ⵥ start ͬʱ���ӳ���
			}
		}
		// ���뵥��ȡ������
		return true;

	}

	/**
	 * ���г������
	 */
	private void onSaveDispenseOut(TParm dispenseD, TParm dispenseM) {
		/** ��������(���ⵥ���ϸ��) */
		// ҩ�������Ϣ
		TParm sysParm = IndSysParmTool.getInstance().onQuery();
		// �������ҵ״̬�ж�(1-���ȷ�ϣ�2-���⼴���)
		String dis_check = getDisCheckFlg(sysParm);
		// �Ƿ��д����۸�
		String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
		if ("1".equals(dis_check)) {
			// ���ⲿ�ſ���Ƿ���춯
			if (!getOrgBatchFlg(to_org_code)) {
				return;
			}
			// ������;��ҵ/��������������ҵ(���⼴���)
			if (getDispenseOutOn(to_org_code, dispenseM, dispenseD)) {
				// �ɹ�
				conn.commit();
			}
			conn.close();
		} else if ("2".equals(dis_check)) {
			// ���ⲿ�ſ���Ƿ���춯
			if (!getOrgBatchFlg(to_org_code)) {
				return;
			}
			// ��ⲿ�ſ���Ƿ���춯
			if (!"".equals(app_org_code) && !getOrgBatchFlg(to_org_code)) {
				return;
			}
			// ���⼴�����ҵ(����ⲿ�ž���Ϊ��)
			getDispenseOutIn(dispenseM, dispenseD, to_org_code, app_org_code,
					reuprice_flg, out_flg, in_flg);
		}
	}

	/**
	 * ���⼴�����ҵ
	 * 
	 * @param out_org_code
	 * @param in_org_code
	 * @param batchvalid
	 */
	private void getDispenseOutIn(TParm dispenseM, TParm dispenseD,
			String out_org_code, String in_org_code, String reuprice_flg,
			boolean out_flg, boolean in_flg) {
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
		// ִ����������
		parm = INDTool.getInstance().onInsertDispenseOutIn(parm, conn);
		// �����ж�
		if (parm == null || parm.getErrCode() < 0) {
			return;
		} else {
			conn.commit();
		}
		conn.close();

	}

	/**
	 * ������;��ҵ/��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
	 */
	private boolean getDispenseOutOn(String org_code, TParm dispenseM,
			TParm dispenseD) {
		TParm parm = new TParm();
		parm = getDispenseMParm(parm, dispenseM, "1");
		parm = getDispenseDParm(parm, dispenseD);
		// ʹ�õ�λ
		parm.setData("UNIT_TYPE", u_type);
		// ���뵥����
		parm.setData("REQTYPE_CODE", request_type);
		// ���ⲿ��
		parm.setData("ORG_CODE", org_code);
		// ִ����������
		parm = INDTool.getInstance().onInsertDispenseOutOn(parm, conn);
		// �����ж�
		if (parm == null || parm.getErrCode() < 0) {
			return false;
		}
		return true;
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
		parmM.setData("DISPENSE_DATE", date);
		parmM.setData("DISPENSE_USER", "HRMAutoMed");
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
		parmM.setData("OPT_USER", "HRMAutoMed");
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
		for (int i = 0; i < dispenseD.getCount("ORDER_CODE"); i++) {
			if (dispenseD.getData("ORDER_CODE", i) != null) {
				order_code = dispenseD.getData("ORDER_CODE", i) + "";
			} else {
				order_code = "";
			}
			stockParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getStockQtyInfo(order_code)));
			stockParm = stockParm.getRow(0);
			if (stockParm.getDouble("STOCK_QTY") < dispenseD
					.getDouble("QTY", i)) {
				// ���������������
				continue;
			}
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
			parmD.setData("QTY", count, dispenseD.getDouble("QTY", i));
			parmD.setData("UNIT_CODE", count, dispenseD.getData("UNIT_CODE", i));
			parmD.setData("ACTUAL_QTY", count,
					dispenseD.getDouble("ACTUAL_QTY", i));
			parmD.setData("BATCH_SEQ", count, dispenseD.getInt("BATCH_SEQ", i));
			parmD.setData("BATCH_NO", count, dispenseD.getData("BATCH_NO", i));
			valid_date = dispenseD.getData("VALID_DATE", i);
			if (valid_date != null) {
				parmD.setData("VALID_DATE", count, tnull);
			} else {
				parmD.setData("VALID_DATE", count,
						dispenseD.getTimestamp("VALID_DATE", i));
			}

			parmD.setData("OPT_USER", count, "HRMAutoMed");
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
	private void onSaveRequestAndDispense(TParm result, String type) {
		TParm requestM = result.getParm("RESULT_M", 0);
		TParm requestD = result.getParm("RESULT_D", 0);
		if (requestM.getCount("REQUEST_NO") <= 0
				&& requestD.getCount("PAT_NAME") <= 0) {
			return;
		}
		TParm parm = new TParm();
		// �������ݣ����뵥����
		getRequestExmParmM(parm, requestM);
		// �������ݣ����뵥ϸ��
		getRequestExmParmD(parm, requestD);
		// �жϸ������(�ż�ס)
		parm.setData("TYPE", type);
		// �������ݣ���������״̬
		getDeptRequestUpdate(parm, requestM, requestD, type);
		String spcFlg = Operator.getSpcFlg();
		// ���÷���˱���ʧ��
		if (spcFlg != null && spcFlg.equals("Y")) {
			// �����������ӿڷ���������ǰ��onCreateDeptExmRequest��ΪonCreateDeptExmRequestSpc
			onCreateDeptExmRequestSpc(parm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				return;
			}
		} else {
			onCreateDeptExmRequest(parm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				return;
			}
		}
		// �������� ������ⵥ start
		onSaveDispenseOut(parm.getParm("REQUEST_D"), parm.getParm("REQUEST_M"));
		// �������� ������ⵥ end
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
		inparm.setData("REQUEST_DATE", date);
		inparm.setData("REQUEST_USER", "HRMAutoMed");
		inparm.setData("REASON_CHN_DESC", "�Զ���ҩ");
		inparm.setData("DESCRIPTION", "�Զ���ҩ");
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("OPT_USER", "HRMAutoMed");
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
		for (int i = 0; i < resquestD.getCount("PAT_NAME"); i++) {
			inparm.addData("REQUEST_NO", request_no);
			inparm.addData("SEQ_NO", count + 1);
			inparm.addData("ORDER_CODE", resquestD.getData("ORDER_CODE", i));
			inparm.addData("BATCH_NO", "");
			inparm.addData("VALID_DATE", tnull);
			inparm.addData("QTY", resquestD.getDouble("DOSAGE_QTY", i));
			inparm.addData("ACTUAL_QTY", 0);
			inparm.addData("UPDATE_FLG", "0");
			inparm.addData("OPT_USER", "HRMAutoMed");
			inparm.addData("OPT_DATE", date);
			inparm.addData("OPT_TERM", "127.0.0.1");
			count++;
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
		for (int i = 0; i < requestD.getCount("PAT_NAME"); i++) {
			inparm.setData("CASE_NO", count, requestD.getValue("CASE_NO", i));
			inparm.setData("RX_NO", count, requestD.getValue("RX_NO", i));
			inparm.setData("ORDER_CODE", count,
					requestD.getValue("ORDER_CODE", i));// add by wanglong
														// 2013024
			inparm.setData("SEQ_NO", count, requestD.getInt("SEQ_NO", i));
			inparm.setData("REQUEST_FLG", count, "Y");
			inparm.setData("REQUEST_NO", count, request_no);
			count++;
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
	public TParm onCreateDeptExmRequest(TParm parm) {
		TParm result = new TParm();
		result = jdo.spc.INDTool.getInstance().onCreateDeptExmRequest(parm,
				conn);
		if (result.getErrCode() < 0) {

			return result;
		}
		conn.commit();
		// conn.close();
		return result;
	}

	/**
	 * �������쵥������������[���ұ�ҩ����]
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCreateDeptExmRequestSpc(TParm parm) {
		TParm result = new TParm();
		// TConnection connection =
		// TDBPoolManager.getInstance().getConnection();
		result = jdo.spc.INDTool.getInstance().onCreateDeptExmRequest(parm,
				conn);
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
		conn.commit();
		// conn.close();
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
