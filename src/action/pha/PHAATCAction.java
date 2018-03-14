package action.pha;
import java.util.Iterator;
import java.util.Map;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

import jdo.pha.TXNewATCTool;
import jdo.sys.PHATXATCTool;
import jdo.udd.UddChnCheckTool;

public class PHAATCAction extends TAction {

	/**
	 * 门诊包药机
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onATCO(TParm parm) {
		TParm result = new TParm();
		/**
		 * 1旧包药机 2新包药机
		 */
		String type = parm.getValue("TYPE");
		if (type.equals("1")) {
			if (PHATXATCTool.getInstance().generateOldATCTxtO(parm))
				result.setErrCode(0);
			else
				result.setErrCode(-1);
		} else if (type.equals("2")) {
			if (TXNewATCTool.getInstance().OInsertData(parm))
				result.setErrCode(0);
			else
				result.setErrCode(-1);
		}
		return result;
	}

	/**
	 * 住院包药机
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onATCI(TParm parm) {
		TParm result = new TParm();
		int insertSuccessCount = 0;
		/**
		 * 1旧包药机 2新包药机
		 */
		String type = parm.getValue("TYPE");
		if (type.equals("1")) {
			if (PHATXATCTool.getInstance().generateOldATCTxtI(parm))
				result.setErrCode(0);
			else
				result.setErrCode(-1);
		} else if (type.equals("2")) {
			TParm inparm = parm.getParm("DRUG_LIST_PARM");
			Map pat = TXNewATCTool.getInstance().groupByPatParm(inparm);
			Iterator it = pat.values().iterator();
			while (it.hasNext()) {
				TParm patParm = (TParm) it.next();
				if (TXNewATCTool.getInstance().IInsertData(patParm)) {
					insertSuccessCount = insertSuccessCount + patParm.getCount("PRESCRIPTIONNO");
					result = updateUddBarCode(patParm);
					result.setData("INSERT_COUNT", insertSuccessCount);
					if (result.getErrCode() < 0) {
						result.setErrCode(-2);
						return result;
					}
				} else{
					result.setData("INSERT_COUNT", insertSuccessCount);
					result.setErrCode(-1);
				    return result;
				}
			}
		}
		
		return result;
	}

	/**
	 * 更新BAR_CODE字段
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateUddBarCode(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		int count = parm.getCount("BAR_CODE");
		for (int i = 0; i < count; i++) {
			TParm parmRow = parm.getRow(i);
			result = UddChnCheckTool.getInstance().updateUddMBarCode(parmRow,connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				result.setErrCode(-1);
				return result;
			}
//			result = UddChnCheckTool.getInstance().updateUddDBarCode(parmRow,connection);
//			if (result.getErrCode() < 0) {
//				connection.rollback();
//				result.setErrCode(-1);
//				return result;
//			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * 将包药机错误日志输送到Tomcat日志
	 * 
	 * @param parm
	 */
	public TParm printLog(TParm parm) {
		System.out.println("送包药机日志:"
				+ StringTool.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmmss") + "  " + parm.getValue("MSG"));
		return new TParm();
	}
}
