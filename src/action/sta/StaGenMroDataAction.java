package action.sta;

import jdo.sta.StaGenMroDataTool;
import jdo.sta.StaGenMroDataTran;
import jdo.sta.StaMroGenTranDataTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class StaGenMroDataAction extends TAction {
	/**
	 * 导入中间表信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不可为空！");
			return result;
		}
		TConnection conn = this.getConnection();
		result = StaGenMroDataTool.getInstance().onSavedata(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		conn.close();
		return result;
	}

	/**
	 * 生成文件
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCreateDBF(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不可为空！");
			return result;
		}
		String strDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String fileNa = parm.getValue("FILE_DATA");
		String filePath = parm.getValue("FILE_PATH");
		TParm inparm = parm.getParm("INPARM");
		TConnection conn = this.getConnection();
		TParm dateParm = StaMroGenTranDataTool.getInstance().createDbf(strDate,
				endDate, fileNa, filePath, inparm, conn);
		if (dateParm.getErrCode() < 0) {
			err("ERR:" + dateParm.getErrCode() + dateParm.getErrText()
					+ dateParm.getErrName());
			conn.close();
			return dateParm;
		}
		for (int i = 0; i < inparm.getCount(); i++) {
			result = StaGenMroDataTool.getInstance().updateSTAFlg(
					inparm.getRow(i), fileNa, "2", conn);
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
		result.setData("BYTES", dateParm.getData("BYTES"));
		return result;
	}

	/**
	 * 发送文件
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSendDBF(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不可为空！");
			return result;
		}
		String zipTime = parm.getValue("ZIP_TIME");
		String sendTime = parm.getValue("SEND_TIME");
		String response = parm.getValue("RESPONSE");
		TConnection conn = this.getConnection();
		result = StaGenMroDataTool.getInstance().updateSTASendFlg(zipTime,
				sendTime, "4", conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		if (!response.equals("")) {
			if (!StaGenMroDataTran.getInstance().onLog(response, conn, "")) {
				result.setErrCode(-1);
				result.setErrText("插入日志失败");
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
}
