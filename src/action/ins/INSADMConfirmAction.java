package action.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.sys.SYSFeeTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title:资格确认书下载和开立
 * </p>
 * 
 * <p>
 * Description:资格确认书下载和开立
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2011-11-29
 * @version 2.0
 */
public class INSADMConfirmAction extends TAction {

	
	/**
	 * 城职城居生成资格确认书
	 * 
	 * @param parm
	 * @return
	 */

	DateFormat df = new SimpleDateFormat("yyyyMMdd");

	public TParm onSaveConfirmApply(TParm parm) {
		TConnection connection = getConnection();
		// 获得数据添加INS_ADM_CONFIRM 表
		TParm result = INSADMConfirmTool.getInstance().insertConfirmApply(parm,
				connection);
		if (result.getErrCode() < 0) {
			// this.messageBox("此资格确认书已下载或开立过");
			connection.close();
			return result;
		}
		// Update Adm_Resv Set Confirm_No=xxx
		// 参数：预约单号,资格确认书编号
		result = ADMResvTool.getInstance()
				.updateResvConfirmNo(parm, connection);
		if (result.getErrCode() < 0) {
			// this.messageBox("此资格确认书已下载或开立过");
			connection.close();
			return result;
		}
		// 修改 Adm_Inp Confirm_NO=xxx CTZ1_CODE=xxx where case_no
		// 参数：身份,资格确认书编号,CASE_NO
		ADMInpTool.getInstance().updateINPConfirmNo(parm, connection);
		if (result.getErrCode() < 0) {
			// this.messageBox("此资格确认书已下载或开立过");
			connection.close();
			return result;
		}
		// 在开立资格确认书的时候update Ins_OrdM Confirm_NO=xxx Adm_seq= xxx where Case_No=
		// xx And year_mon=xx

		// insert into ibs_ctz(HOSP_AREA, CASE_NO, BEGIN_DATE,MR_NO, BED_NO,
		// CTZ_CODE,CTZ_CODE1, CTZ_CODE2,OPT_USER,OPT_TERM, OPT_DATE)
		connection.commit();
		connection.close();
		return result;
	}

	
}
