package jdo.inf;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ��Ⱦ��Ϣ �����ʼ�������
 * </p>
 * 
 * <p>
 * Description: ��Ⱦ��Ϣ �����ʼ�������
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
 * @author wukai
 * @version 1.0
 */
public class INFSmsTool extends TJDOTool {
	/**
	 * ������
	 */
	public INFSmsTool() {
		setModuleName("inf\\INFSmsModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	private static INFSmsTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INFReportTool
	 */
	public static INFSmsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INFSmsTool();
		return instanceObject;
	}

	/**
	 * �����ֶΣ���Ⱦ��Ԥ����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertInfWarnData(TParm parm, TConnection conn) {
		TParm result = this.update("insertInfWarnData", parm, conn);
		return result;
	}

	/**
	 * ��ѯȫ�ֶ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectAllData(TParm parm) {
		TParm result = this.update("selectAllData", parm);
		return result;
	}

	/**
	 * ��ȡ���ͼ�¼����Ⱦ��Ԥ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectInfWarnData(TParm parm) {
		TParm result = this.query("selectInfWarnData", parm);
		return result;
	}

	/**
	 * ��ȡ��Ԥ��ʩѡ��
	 * 
	 * @param inventId
	 *            : ��Ԥ��ʩID
	 * @return
	 */
	public TParm getInfInventOptions(String inventId) {

		String sql = "SELECT 'N' AS FLG, ID, CHN_DESC AS NAME FROM INF_INTERVENTION_OPTIONS WHERE INTERVENT_ID = '"
				+ inventId + "' ORDER BY ID ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		return result;

	}

	/**
	 * �����ֶ� ����Ⱦ��Ԥ����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateInfWarnData(TParm parm, TConnection conn) {
		TParm result = this.update("updateInfWarnData", parm, conn);
		return result;
	}

	/**
	 * �����ֶ� ����Ⱦ��Ԥ����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateInfWarnData(TParm parm) {
		TParm result = this.update("updateInfWarnData", parm);
		return result;
	}

	/**
	 * ��ȡ����ҽ����Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getBillDr(String drNameOrCode) {
		String sql = "SELECT USER_ID, TEL1 FROM SYS_OPERATOR WHERE USER_ID = '"
				+ drNameOrCode + "' OR USER_NAME = '" + drNameOrCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * �����ֶ� ����Ⱦ��Ϣ��ͨ��add lij 20170426
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertInfSms(TParm parm, TConnection conn) {
		TParm result = this.update("insertInfSmsData", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ȡ��Ⱦ����Ա�绰���� ����Ⱦ��Ϣ��ͨ��add lij 20170426
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm getSendUserCode(String caseNo) {
		String sql = "SELECT A.SEND_USER,A.MR_NO,B.TEL1 "
				+ " FROM INF_SMS A,SYS_OPERATOR B "
				+ " WHERE A.SEND_USER=B.USER_ID AND A.CASE_NO='" + caseNo
				+ "' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * �����ֶ� ����Ⱦ��Ϣ��ͨ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateInfSms(TParm parm) {
		TParm result = this.update("updateInfSmsData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �����ֶ� ����Ⱦ��Ϣ��ͨ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertInfSms(TParm parm) {
		TParm result = this.update("insertInfSmsData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
