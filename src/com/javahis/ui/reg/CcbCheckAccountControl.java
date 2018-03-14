package com.javahis.ui.reg;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: 医建通项目：对账
 * </p>
 * 
 * <p>
 * Description:医建通项目：对账
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author fuwj 20120910
 * @version 1.0
 */

public class CcbCheckAccountControl extends TControl {
	
	/**
	 * 医院与建行对账
	 */
	public void checkAccount() {
		String accountDate = this.getValueString("ACCOUNT_DATE");
		String date = accountDate.substring(0, 10);
		String endDate = date + " 20:00:00";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar now = Calendar.getInstance();
		try {
			now.setTime(sf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		Date today = now.getTime();
		SimpleDateFormat dateSf = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = dateSf.format(today) + " 20:00:00";
		if (!"".equals(accountDate)) {
			String sql = "SELECT CARD_NO,CASE_NO,RECEIPT_NO,BUSINESS_NO,SUM(AMT) AS AMT,"
					+ "OPT_DATE,OPT_TERM,OPT_USER,GUID,BUSINESS_TYPE"
					+ " FROM EKT_CCB_TRADE"
					+ " WHERE (TO_CHAR(OPT_DATE,'yyyy-MM-dd hh24:mi:ss') between '"
					+ startDate
					+ "' AND '"
					+ endDate
					+ "')  "
					+ " AND STATE!='3'"
					+ " GROUP BY BUSINESS_NO,CARD_NO,CASE_NO,OPT_TERM,OPT_DATE,OPT_USER,"
					+ "GUID,RECEIPT_NO,BUSINESS_TYPE";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				messageBox(result.getErrText());
				return;
			}
			String filName = "Tedainch" + date.replace("-", "");
			TParm parm = new TParm();
			parm.setData("filName", filName);
			TParm resultData = TIOM_AppServer.executeAction(
					"action.ccb.CCBServerAction", "readFile", parm);
			if (result.getErrCode() < 0) {
				messageBox("读取服务器对账文件失败");
				return;
			}
			TParm ccbParm = new TParm();
			Map map = resultData.getData();
			List cardList = (List) map.get("CARD_NO");
			List caseList = (List) map.get("CASE_NO");
			List businessList = (List) map.get("BUSINESS_NO");
			List receiptList = (List) map.get("RECEIPT_NO");
			List amtList = (List) map.get("AMT");
			List dateList = (List) map.get("OPT_DATE");
			List ipList = (List) map.get("OPT_TERM");
			List userList = (List) map.get("OPT_USER");
			List guidList = (List) map.get("GUID");
			List typeList = (List) map.get("BUSINESS_TYPE");
			for (int i = 0; i < cardList.size(); i++) {
				ccbParm.addData("BUSINESS_NO", businessList.get(i));
				ccbParm.addData("CARD_NO", cardList.get(i));
				ccbParm.addData("CASE_NO", caseList.get(i));
				ccbParm.addData("AMT", amtList.get(i));
				ccbParm.addData("RECEIPT_NO", receiptList.get(i));
				ccbParm.addData("OPT_DATE", dateList.get(i));
				ccbParm.addData("OPT_TERM", ipList.get(i));
				ccbParm.addData("OPT_USER", userList.get(i));
				ccbParm.addData("GUID", guidList.get(i));
				ccbParm.addData("BUSINESS_TYPE", typeList.get(i));
			}
			boolean accountFlg = true;
			// 遍历医院有建行没有记录
			int count = result.getCount();
			for (int i = 0; i < count; i++) {
				String businessNo = (String) result.getData("BUSINESS_NO", i);
				DecimalFormat df = new DecimalFormat("0.00");
				String amt = String.valueOf(df.format(Math.abs(result
						.getDouble("AMT", i))));
				boolean flg = false;
				for (int j = 0; j < businessList.size(); j++) {
					if (businessNo.equals(businessList.get(j))
							&& amt.equals(amtList.get(j))) {
						flg = true;
					}
				}
				if (!flg) {
					// 变色
					accountFlg = false;
					this.getTable("Table_HIS").setRowTextColor(i,
							new Color(255, 0, 0));
				}
			}
			// 遍历建行有医院没有的记录
			for (int i = 0; i < businessList.size(); i++) {
				String businessNo = (String) businessList.get(i);
				String amt = (String) amtList.get(i);
				boolean flg = false;
				for (int j = 0; j < count; j++) {
					String hisBusiness = (String) result.getData("BUSINESS_NO",
							j);
					DecimalFormat df = new DecimalFormat("0.00");
					String hisAmt = String.valueOf(df.format(Math.abs(result
							.getDouble("AMT", j))));
					if (businessNo.equals(hisBusiness) && amt.equals(hisAmt)) {
						flg = true;
					}
				}
				if (!flg) {
					// 变色
					accountFlg = false;
					this.getTable("Table_CCB").setRowTextColor(i,
							new Color(255, 0, 0));
				}
			}
			this.callFunction("UI|Table_HIS|setParmValue", result);
			this.callFunction("UI|Table_CCB|setParmValue", ccbParm);
			if (accountFlg = true) {
				messageBox("全部对账成功");
			}
		} else {
			messageBox("请选择对账日期");
			return;
		}
	}
	/**
	 * 医院与建行对账
	 */
	public void onClear() {
		setValue("ACCOUNT_DATE", "");
		getTable("Table_HIS").removeRowAll();
		getTable("Table_CCB").removeRowAll();	
	}
	/**
	 * 获取table对象
	 * @param tableName
	 * @return
	 */
	public TTable getTable(String tableName) {
		return (TTable) this.getComponent(tableName);
	}

}
