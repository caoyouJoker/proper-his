package action.odi;

import jdo.odi.ODICISVitalSignTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ����CIS�����������Action
 * </p>
 * 
 * <p>
 * Description: ����CIS�����������Action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangbin 2015.4.27
 * @version 1.0
 */
public class ODICISVitalSignAction extends TAction {
	
	public ODICISVitalSignAction() {
	}
	
	/**
	 * ��CIS�ӿ�ȡ���������������뵽ODI_CISVITALSIGN��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsertODICISVitalSign(TParm parm) {
		// modified by WangQing at 20170213 -start
		// ��������Ƶ�connection����֮ǰ
		TParm result = new TParm();
		
		TParm insertParm = parm.getParm("INSERT");
		TParm updateParm = parm.getParm("UPDATE");
		
		if (insertParm == null || updateParm == null) {
			result.setErr(-1, "���δ���");
			result.setData("SUCCESS_FLG", false);
			return result;
		}
		
		TConnection conn = getConnection();
		// modified by WangQing at 20170213 -end
		int count = insertParm.getCount("CASE_NO");
		
		for (int i = 0; i < count; i++) {
			insertParm.setData("OPT_USER", i, "PROPERSOFT");
			insertParm.setData("OPT_TERM", i, "127.0.0.1");
			insertParm.setData("MONITOR_TIME", i, insertParm.getValue(
					"MONITOR_TIME", i).replaceAll("-", "").replaceAll(":", "")
					.replaceAll(" ", ""));
			
			if (StringUtils.equals("N", insertParm.getValue("INSERT_FLG", i))) {
				continue;
			}
			
			// ����ǰ���жϱ����Ƿ��Ѿ����ڸ����ݱ���������ͻ
			result = ODICISVitalSignTool.getInstance().queryODICISVitalSign(insertParm.getRow(i));
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				result.setData("SUCCESS_FLG", false);
				return result;
			}
			
			if (result.getCount("MR_NO") > 0) {
				continue;
			}
			
			// ������֢��������¼��ODI_CISVITALSIGN��������
			result = ODICISVitalSignTool.getInstance().insertODICISVitalSign(
					insertParm.getRow(i), conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				result.setData("SUCCESS_FLG", false);
				return result;
			}
		}
		
		// ����סԺ�������Ĳ�׽����ʱ������
		result = ODICISVitalSignTool.getInstance().updateODISysparm(updateParm, conn);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			result.setData("SUCCESS_FLG", false);
			return result;
		}
		
		conn.commit();
		conn.close();
		result.setData("SUCCESS_FLG", true);
		return result;
	}
	
	/**
	 * �ֶ���CIS�ӿ�ȡ���������������뵽ODI_CISVITALSIGN��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsertODICISVitalSignByManually(TParm parm) {
		// modified by WangQing at 20170213 -start
	    // ��������Ƶ�connection����֮ǰ
		TParm result = new TParm();
		
		TParm insertParm = parm.getParm("INSERT");
		
		if (insertParm == null) {
			result.setErr(-1, "���δ���");
			result.setData("SUCCESS_FLG", false);
			return result;
		}
		TConnection conn = getConnection();
		// modified by WangQing at 20170213 -end
		int count = insertParm.getCount("MR_NO");
		
		for (int i = 0; i < count; i++) {
			insertParm.setData("OPT_USER", i, "PROPERSOFT");
			insertParm.setData("OPT_TERM", i, "127.0.0.1");
			insertParm.setData("MONITOR_TIME", i, insertParm.getValue(
					"MONITOR_TIME", i).replaceAll("-", "").replaceAll(":", "")
					.replaceAll(" ", ""));
			
			if (StringUtils.equals("N", insertParm.getValue("INSERT_FLG", i))) {
				continue;
			}
			
			// ����ǰ���жϱ����Ƿ��Ѿ����ڸ����ݱ���������ͻ
			result = ODICISVitalSignTool.getInstance().queryODICISVitalSign(insertParm.getRow(i));
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				result.setData("SUCCESS_FLG", false);
				return result;
			}
			
			if (result.getCount("MR_NO") > 0) {
				continue;
			}
			
			// ������֢��������¼��ODI_CISVITALSIGN��������
			result = ODICISVitalSignTool.getInstance().insertODICISVitalSign(
					insertParm.getRow(i), conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				result.setData("SUCCESS_FLG", false);
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		result.setData("SUCCESS_FLG", true);
		return result;
	}
}
