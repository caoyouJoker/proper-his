package action.erd;

import org.apache.commons.lang.StringUtils;

import jdo.erd.ERDCISVitalSignTool;

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
public class ERDCISVitalSignAction extends TAction {
	
	public ERDCISVitalSignAction() {
	}
	
	/**
	 * ��CIS�ӿ�ȡ���������������뵽ERD_CISVITALSIGN��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsertERDCISVitalSign(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		TParm insertParm = parm.getParm("INSERT");
		TParm updateParm = parm.getParm("UPDATE");
		
		if (insertParm == null || updateParm == null) {
			result.setErr(-1, "���δ���");
			result.setData("SUCCESS_FLG", false);
			return result;
		}
		
		int count = insertParm.getCount("BED_NO");
		
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
			result = ERDCISVitalSignTool.getInstance().queryERDCISVitalSign(insertParm.getRow(i));
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				result.setData("SUCCESS_FLG", false);
				return result;
			}
			
			if (result.getCount("BED_NO") > 0) {
				continue;
			}
			
			// ������������¼��ERD_CISVITALSIGN��������
			result = ERDCISVitalSignTool.getInstance().insertERDCISVitalSign(
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
		
		// ��������������Ĳ�׽����ʱ������
		result = ERDCISVitalSignTool.getInstance().updateOPDSysparm(updateParm, conn);
		
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
	 * �ֶ���CIS�ӿ�ȡ���������������뵽ERD_CISVITALSIGN��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsertERDCISVitalSignByManually(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		TParm insertParm = parm.getParm("INSERT");
		
		if (insertParm == null) {
			result.setErr(-1, "���δ���");
			result.setData("SUCCESS_FLG", false);
			return result;
		}
		
		int count = insertParm.getCount("BED_NO");
		
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
			result = ERDCISVitalSignTool.getInstance().queryERDCISVitalSign(insertParm.getRow(i));
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				result.setData("SUCCESS_FLG", false);
				return result;
			}
			
			if (result.getCount("BED_NO") > 0) {
				continue;
			}
			
			// ������������¼��ERD_CISVITALSIGN��������
			result = ERDCISVitalSignTool.getInstance().insertERDCISVitalSign(
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
