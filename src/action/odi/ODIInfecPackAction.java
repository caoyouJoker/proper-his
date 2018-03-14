package action.odi;

import jdo.odi.ODIInfecPackTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class ODIInfecPackAction extends TAction {
	public ODIInfecPackAction() {
	}

	/**
	 * ±£´æ·½·¨
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		TParm mparm = parm.getParm("mPARM");
		TParm icdparm = parm.getParm("icdPARM");
		TParm orderparm = parm.getParm("orderPARM");
		String packcode = mparm.getValue("PACK_CODE");
		result = ODIInfecPackTool.getInstance().onDeletePackM(packcode, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onInsertPackM(mparm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onDeletePackICD(packcode, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onInsertPackicd(icdparm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onDeletePackORDER(packcode,
				conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onInsertPackorder(orderparm,
				conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onDelete(TParm parm){
		TParm result = new TParm();
		TConnection conn = this.getConnection();
		TParm mparm = parm.getParm("mPARM");
		String packcode = mparm.getValue("PACK_CODE");
		result = ODIInfecPackTool.getInstance().onDeletePackM(packcode, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onDeletePackICD(packcode, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		result = ODIInfecPackTool.getInstance().onDeletePackORDER(packcode,
				conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
}
