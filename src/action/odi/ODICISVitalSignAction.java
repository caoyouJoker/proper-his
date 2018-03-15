package action.odi;

import jdo.odi.ODICISVitalSignTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 病区CIS体征监测数据Action
 * </p>
 * 
 * <p>
 * Description: 病区CIS体征监测数据Action
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
	 * 将CIS接口取到的数据批量插入到ODI_CISVITALSIGN表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsertODICISVitalSign(TParm parm) {
		// modified by WangQing at 20170213 -start
		// 参数检测移到connection创建之前
		TParm result = new TParm();
		
		TParm insertParm = parm.getParm("INSERT");
		TParm updateParm = parm.getParm("UPDATE");
		
		if (insertParm == null || updateParm == null) {
			result.setErr(-1, "传参错误");
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
			
			// 插入前先判断表中是否已经存在该数据避免主键冲突
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
			
			// 向病区重症体征监测记录表ODI_CISVITALSIGN插入数据
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
		
		// 更新住院参数档的捕捉数据时间区间
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
	 * 手动将CIS接口取到的数据批量插入到ODI_CISVITALSIGN表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onInsertODICISVitalSignByManually(TParm parm) {
		// modified by WangQing at 20170213 -start
	    // 参数检测移到connection创建之前
		TParm result = new TParm();
		
		TParm insertParm = parm.getParm("INSERT");
		
		if (insertParm == null) {
			result.setErr(-1, "传参错误");
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
			
			// 插入前先判断表中是否已经存在该数据避免主键冲突
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
			
			// 向病区重症体征监测记录表ODI_CISVITALSIGN插入数据
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
