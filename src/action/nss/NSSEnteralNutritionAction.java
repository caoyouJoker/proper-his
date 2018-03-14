package action.nss;

import org.apache.commons.lang.StringUtils;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ����Ӫ��Action</p>
 *
 * <p>Description: ����Ӫ��Action</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangbin 2015.3.20
 * @version 1.0
 */
public class NSSEnteralNutritionAction extends TAction {
	
	public NSSEnteralNutritionAction() {
	}

    /**
     * ����Ӫ��չ��ҽ������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveNSSENDspnM(TParm parm) {
		TConnection conn = getConnection();
		int count  = parm.getCount();
		TParm result = new TParm();
		String prepareNo = "";
		int seq = 0;
		int saveCount = 0;
		
		for (int i = 0; i < count; i++) {
			// ��������Ѿ�չ�������ٲ���
			if (parm.getBoolean("EXIST_FLG", i)) {
				continue;
			}
			
			// ������ڽ���û��ˢ�£�����ͣ�����ݽ������򲻽��в���
			if (parm.getBoolean("DC_FLG", i)) {
				continue;
			}
			
			prepareNo = SystemTool.getInstance().getNo("ALL", "NSS",
					"EN_PREPARE_NO", "EN_PREPARE_NO");
			// ȡ��ԭ��(Ϊ�˵���ִ��ɨ��ʱ�����Ƿ��ǳ���Ӫ���������ɵĺ���ǰ�ӱ�ʶ0)
			parm.setData("EN_PREPARE_NO", i, "0" + prepareNo);
			
			// ����Ӫ��ʦչ��ҽ������
			result = NSSEnteralNutritionTool.getInstance().insertNSSENDspnM(
					parm.getRow(i), conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}
			
			// ��ǩ������ϸ��չ������
			seq = parm.getInt("LABEL_QTY", i);
			for (int j = 1; j <= seq; j++) {
				// ���
				parm.setData("SEQ", i, j);
				// ����Ӫ��ʦչ��ҽ��ϸ��
				result = NSSEnteralNutritionTool.getInstance().insertNSSENDspnD(
						parm.getRow(i), conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.close();
					return result;
				}
			}
			
			saveCount++;
		}
		
		conn.commit();
		conn.close();
		result.setData("SAVE_COUNT", saveCount);
		return result;
	}
	
	/**
     * ɾ������Ӫ���䷽����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm deleteNSSENOrderD(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		String[] sqlArray = (String[]) parm.getData("DELETE_SQL");
		if (null == sqlArray) {
			result.setErr(-1, "���δ���");
			return result;
		}
		
		int count = sqlArray.length;
		for (int i = 0; i < count; i++) {
			if (sqlArray[i].contains("INSERT")) {
				continue;
			}
			
			// ����Ӫ��ʦչ��ҽ������
			result = NSSEnteralNutritionTool.getInstance().deleteNSSENOrderD(
					sqlArray[i], conn);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * ����Ӫ���������ʱ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSaveByPrepareComplete(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		// ������Ա�������ʱ���³���Ӫ��ִ������������
		result = NSSEnteralNutritionTool.getInstance().updateENDspnMByPrepare(
				parm.getParm("parmM"), conn);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "��������״̬�����쳣");
			conn.close();
			return result;
		}
		
		// ���мƷ���Ŀ����мƷ�
		if (parm.getBoolean("CHARGE_FLG")) {
			if (null == parm.getParm("parmD")) {
				result.setErr(-1, "�ƷѲ����������");
				conn.close();
				return result;
			} else {
				// ����Ӫ��������ɼƷ�
				result = NSSEnteralNutritionTool.getInstance().insertIBSOrder(parm.getParm("parmD"),
						conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText());
					result.setErr(-1, "�Ʒѷ����쳣");
					conn.close();
					return result;
				}
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
    /**
     * ����Ӫ����ʿ����ִ�б���
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveNSSENDspnBySingleExe(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();

		// ��ʿ����ִ�б����������ҽ��չ������
		result = NSSEnteralNutritionTool.getInstance()
				.updateENDspnMBySingleExe(parm, conn);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "����ҽ��չ���������");
			conn.close();
			return result;
		}
		
		// ��ʿ����ִ�б����������ҽ��չ��ϸ��
		result = NSSEnteralNutritionTool.getInstance()
				.updateENDspnDBySingleExe(parm, conn);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "����ҽ��չ��ϸ�����");
			conn.close();
			return result;
		}
		
		if ((StringUtils.equals("UD", parm.getValue("RX_KIND"))
				&& parm.getInt("TOTAL_ACCU_QTY") > 0 && parm
				.getInt("TOTAL_ACCU_QTY")
				% parm.getInt("MEDI_QTY") == 0)
				|| StringUtils.equals("ST", parm.getValue("RX_KIND"))) {
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			queryParm.setData("ORDER_NO", parm.getValue("ORDER_NO"));
			queryParm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ"));
			queryParm.setData("ORDER_DATE", SystemTool.getInstance().getDate()
					.toString().substring(0, 10).replaceAll("-", ""));
			queryParm.setData("EXEC_FLG", "N");
			
			// ��ѯODI_DSPND��չ����ϸ
			result = NSSEnteralNutritionTool.getInstance().queryODIDspnDInfo(queryParm);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText());
				result.setErr(-1, "��ѯODI_DSPND��չ����ϸ����");
				conn.close();
				return result;
			}
			
			if (result.getCount("CASE_NO") > 0) {
				// ��ʿ����ִ�б����������ҽ��չ��ϸ��ODI_DSPND
				TParm updateParm = new TParm();
				updateParm.setData("CASE_NO", result.getValue("CASE_NO", 0));
				updateParm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
				updateParm.setData("ORDER_SEQ", result.getValue("ORDER_SEQ", 0));
				updateParm.setData("ORDER_DATE", result.getValue("ORDER_DATE", 0));
				updateParm.setData("ORDER_DATETIME", result.getValue("ORDER_DATETIME", 0));
				updateParm.setData("NS_EXEC_CODE_REAL", parm.getValue("OPT_USER"));
				result = NSSEnteralNutritionTool.getInstance().updateODIDspnD(updateParm, conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText());
					result.setErr(-1, "����ҽ��չ��ϸ��OID_DSPND�����");
					conn.close();
					return result;
				}
			}
		}

		conn.commit();
		conn.close();
		return result;
	}
	
	/**
     * ����Ӫ������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveByENHandOver(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			
			// ���潻������
			result = NSSEnteralNutritionTool.getInstance().onSaveByENHandOver(
					parm.getRow(i), conn);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * ����Ӫ�����Ƹ��ӷ��üƷ�
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm chargeENExtraFee(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		// ����Ӫ��������ɼƷ�
		result = NSSEnteralNutritionTool.getInstance().insertIBSOrder(parm,
				conn);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "�Ʒѷ����쳣");
			conn.close();
			return result;
		}
		
		conn.commit();
		conn.close();
		return result;
	}
}
