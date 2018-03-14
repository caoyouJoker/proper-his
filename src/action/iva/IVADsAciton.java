package action.iva;

import jdo.iva.IVAAllocatecheckTool;
import jdo.iva.IVADeploymentTool;
import jdo.iva.IVADispensingTool;
import jdo.iva.IVAPutMedicineWorkCheckTool;
import jdo.iva.IVAPutMedicineWorkTool;
import jdo.iva.IVARefundMedicineTool;
import jdo.sys.Operator;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

public class IVADsAciton extends TAction {

	// ≈≈“©±£¥Ê
	public TParm onUpdatePut(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVAPutMedicineWorkTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			conn.close();
			return resultM;
		}
		resultD = IVAPutMedicineWorkTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			conn.close();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// ≈≈“©∫À∂‘±£¥Ê
	public TParm onUpdatePutCheck(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVAPutMedicineWorkCheckTool.getInstance().updateInfoM(parm,
				conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			conn.close();
			return resultM;
		}
		resultD = IVAPutMedicineWorkCheckTool.getInstance().updateInfoD(parm,
				conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			conn.close();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// µ˜≈‰±£¥Ê
	public TParm onUpdateDep(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVADeploymentTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			conn.close();
			return resultM;
		}
		resultD = IVADeploymentTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			conn.close();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// µ˜≈‰…Û∫À±£¥Ê
	public TParm onUpdateDepCheck(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		for(int i = 0; i < parm.getCount("CASE_NO"); i++){
			result = IVAAllocatecheckTool.getInstance().updateInfoM(parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			result = IVAAllocatecheckTool.getInstance().updateInfoD(parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
			if(i == 0){
				// liuyalin 20170324 add
				result = IVAAllocatecheckTool.getInstance().updateSolution(parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM IBS_ORDD WHERE CASE_NO = '"
						+ parm.getValue("CASE_NO",i)
						+ "' AND CASE_NO_SEQ = '"
						+ parm.getValue("IBS_CASE_NO_SEQ",i) + "'";
				TParm tparm = new TParm(TJDODBTool.getInstance().select(sql));
				// System.out.println("aaaaaa:::::" + tparm.getInt("SEQ_NO"));
				parm.setData("IBS_SEQ_NO", tparm.getInt("SEQ_NO", 0) + 1);
				// System.out.println("aaaaaa:::::" + parm.getValue("IBS_SEQ_NO"));
				result = IVAAllocatecheckTool.getInstance().insertIBSOrder(parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				// ≤Â»ÎIBS_ORDM±Ì
				result = IVAAllocatecheckTool.getInstance().insertIBSOrderM(parm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

	// ∑¢“©±£¥Ê
	public TParm onUpdateDispensing(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		resultM = IVADispensingTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			conn.close();
			return resultM;
		}
		resultD = IVADispensingTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			conn.close();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	// ÕÀ“©±£¥Ê
	public TParm onUpdateReturn(TParm parm) {
		TConnection conn = getConnection();
		TParm resultM = new TParm();
		TParm resultD = new TParm();

		resultM = IVARefundMedicineTool.getInstance().updateInfoM(parm, conn);

		if (resultM.getErrCode() < 0) {
			err("ERR:" + resultM.getErrCode() + resultM.getErrText()
					+ resultM.getErrName());
			conn.rollback();
			conn.close();
			return resultM;
		}

		resultD = IVARefundMedicineTool.getInstance().updateInfoD(parm, conn);

		if (resultD.getErrCode() < 0) {
			err("ERR:" + resultD.getErrCode() + resultD.getErrText()
					+ resultD.getErrName());
			conn.rollback();
			conn.close();
			return resultD;
		}
		conn.commit();
		conn.close();
		return resultD;
	}

	public TParm onInsertMReturn(TParm parm) {
		TConnection conn = getConnection();
		TParm insertM = new TParm();
		insertM = IVARefundMedicineTool.getInstance().insertInfoM(parm, conn);
		if (insertM.getErrCode() < 0) {
			err("ERR:" + insertM.getErrCode() + insertM.getErrText()
					+ insertM.getErrName());
			conn.rollback();
			conn.close();
			return insertM;
		}
		conn.commit();
		conn.close();
		return insertM;
	}

}
