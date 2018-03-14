package action.adm;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title:临时解锁Action
 * </p>
 * 
 * <p>
 * Description:临时解锁Action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author yanmm
 * @version 1.0
 */
public class ADMUnlockAction extends TAction {

	public TParm insertUnlock(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		String sqlSave = " INSERT INTO ADM_UNLOCK_CAUSE "
				+ "(CASE_NO,MR_NO,SEQ_NO,UNLOCK_CASE,UNLOCK_CASE_TEXT,UNLOCK_DATE,ARREARAGE_AMT,OPT_TERM,OPT_USER,OPT_DATE) "
				+ "VALUES ('"
				+ parm.getValue("CASE_NO")
				+ "','"
				+ parm.getValue("MR_NO")
				+ "','"
				+ parm.getValue("SEQ_NO")
				+ "','"
				+ parm.getValue("UNLOCK_CASE")
				+ "',"
				+ "'"
				+ parm.getValue("UNLOCK_CASE_TEXT")
				+ "',"
				+ "TO_DATE('"
				+ parm.getValue("UNLOCK_DATE")
				+ "','YYYY/MM/DD HH24:MI:SS'),'"
				+ parm.getValue("ARREARAGE_AMT")
				+ "','"
				+ parm.getValue("OPT_TERM")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ "TO_DATE('"
				+ parm.getValue("OPT_DATE")
				+ "','YYYY/MM/DD HH24:MI:SS')) ";
		TParm p = new TParm(TJDODBTool.getInstance().update(sqlSave, conn));
		if (p.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return p;
	}

	public TParm upUnlock(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		String sqlSave1 = "UPDATE ADM_INP SET STOP_BILL_FLG ='N',UNLOCKED_FLG ='1' WHERE CASE_NO ='"
				+ parm.getValue("CASE_NO") + "' ";

		TParm p1 = new TParm(TJDODBTool.getInstance().update(sqlSave1, conn));
		if (p1.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return p1;
	}

	/*
	 * public TParm insertCordon(TParm parm){ TParm result = new TParm();
	 * TConnection conn = this.getConnection(); String sql =
	 * " INSERT INTO IBS_DICTIONARY_CORDON " +
	 * "(RED_SIGN,YELLOW_SIGN,OPT_TERM,OPT_USER,OPT_DATE,ID) " + "VALUES ('" +
	 * parm.getValue("RED_SIGN") + "','" + parm.getValue("YELLOW_SIGN") +"','" +
	 * parm.getValue("OPT_TERM") + "','" + parm.getValue("OPT_USER") + "'," +
	 * "TO_DATE('" + parm.getValue("OPT_DATE") +
	 * "','YYYY/MM/DD HH24:MI:SS'),'1') "; System.out.println("插入"+sql); TParm p
	 * = new TParm(TJDODBTool.getInstance().update(sql,conn));
	 * if(p.getErrCode()<0){ conn.close(); return result; } conn.commit();
	 * conn.close(); return p; }
	 */

	public TParm updateCordon(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		String sql = "UPDATE ODI_SYSPARM SET RED_SIGN='"
				+ parm.getValue("RED_SIGN") + "',YELLOW_SIGN='"
				+ parm.getValue("YELLOW_SIGN") + "',OPT_USER='"
				+ parm.getValue("OPT_USER") + "'," + "OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=TO_DATE('"
				+ parm.getValue("OPT_DATE") + "','YYYY/MM/DD HH24:MI:SS') ";
		// System.out.println("更新" + sql);
		TParm p1 = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (p1.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return p1;
	}

	public TParm insertCtz(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		String sql = " INSERT INTO SYS_CTZ_REBATE "
				+ "(CTZ_CODE,DISCOUNT_RATE,LOCK_CTZ_FLG,OPT_TERM,OPT_USER,OPT_DATE) "
				+ "VALUES ('" + parm.getValue("CTZ_CODE") + "','"
				+ parm.getValue("DISCOUNT_RATE") + "','"
				+ parm.getValue("LOCK_CTZ_FLG") + "','"
				+ parm.getValue("OPT_TERM") + "','" + parm.getValue("OPT_USER")
				+ "'," + "TO_DATE('" + parm.getValue("OPT_DATE")
				+ "','YYYY/MM/DD HH24:MI:SS')) ";
		// System.out.println("插入" + sql);
		TParm p = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (p.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return p;
	}

	public TParm updateCtz(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		String sql = "UPDATE SYS_CTZ_REBATE SET " + "DISCOUNT_RATE = '"
				+ parm.getValue("DISCOUNT_RATE") + "'" + ",LOCK_CTZ_FLG = '"
				+ parm.getValue("LOCK_CTZ_FLG") + "'" + ",OPT_USER = '"
				+ parm.getValue("OPT_USER") + "'" + ",OPT_TERM = '"
				+ parm.getValue("OPT_TERM") + "'" + ",OPT_DATE = TO_DATE ('"
				+ parm.getValue("OPT_DATE") + "'"
				+ ", 'YYYY/MM/DD HH24:MI:SS') " + "WHERE CTZ_CODE = '"
				+ parm.getValue("CTZ_CODE") + "' ";
		// System.out.println("更新" + sql);
		TParm p1 = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (p1.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return p1;
	}

}
