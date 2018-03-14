package com.javahis.ui.opb;

import java.util.List;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.bean.OpdOrderHistory;
import com.javahis.ui.testOpb.tools.QueryTool;
import com.javahis.ui.testOpb.tools.SqlTool;
import com.javahis.ui.testOpb.tools.Type;

/**
 * 门诊作废收据医嘱写历史表
 * 
 * @author zhangp
 * 
 */
public class OPBMakeRecpAndOrderInvalid {

	private static OPBMakeRecpAndOrderInvalid opbMakeRecpAndOrderInvalid;

	public static OPBMakeRecpAndOrderInvalid getInstance() {
		if (opbMakeRecpAndOrderInvalid == null) {
			opbMakeRecpAndOrderInvalid = new OPBMakeRecpAndOrderInvalid();
		}
		return opbMakeRecpAndOrderInvalid;
	}

	public final String SQL = "SELECT * FROM OPD_ORDER WHERE RECEIPT_NO ='#'";

	/**
	 * 门诊作废收据医嘱写历史表 
	 * 
	 * @param receiptNo
	 * @return
	 */
	public boolean makeOrderInvalid(TParm parm) {

			TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
					"onSaveTax", parm);

			if (result.getErrCode() < 0) {
				return false;
			}

		return true;
	}

	/**
	 * 得到门诊作废收据医嘱
	 * 
	 * @param receiptNo
	 * @return
	 */
	public TParm getMakeOrderInvalid(String receiptNo) {
		String sql = SQL.replace("#", receiptNo);
		TParm parm = new TParm();
		List<OpdOrder> list;
		try {
			list = QueryTool.getInstance().queryBySql(sql, new OpdOrder());

			OpdOrderHistory opdOrderHistory;

			for (OpdOrder opdOrder : list) {

				opdOrderHistory = new OpdOrderHistory();

				opdOrderHistory = QueryTool.getInstance().synClasses(opdOrder,
						opdOrderHistory);

				opdOrderHistory.dcOrderDate = QueryTool.getInstance()
						.getSystemTime();

				opdOrderHistory.modifyState = Type.INSERT;

				String sql2 = SqlTool.getInstance().getSql(opdOrderHistory);

				parm.addData("SQL", sql2);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return parm;
		}
	}
	
	
	
	
}
