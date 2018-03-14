package com.javahis.ui.cts;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
 * 
 * 
 * <p>
 * Description:洗衣更换标签查询
 * </p>
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author huangtt 20141128
 * @version 1.0
 */
public class CTSReplaceRfidTimesControl extends TControl {
	private static TTable table;

	/**
	 * 初始化方法 
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
	}

	public void onQuery() {
		String sql = "SELECT 'N' FLG, A.RFID CLOTH_NO, B.INV_CHN_DESC, A.OWNER_CODE, A.CTS_COST_CENTRE STATION_DESC,"
				+ " A.TURN_POINT,A.ACTIVE_FLG, 0 TIMES"
				+ " FROM INV_STOCKDD A, INV_BASE B"
				+ " WHERE A.INV_CODE = B.INV_CODE"
				+ " AND B.INV_KIND = '08'"
				+ " AND A.INV_CODE LIKE 'A2%'" + " AND A.WRITE_FLG = 'Y'";

		if (!getValueString("CLOTH_NO").equals("")) {
			sql += " AND A.RFID = '" + getValueString("CLOTH_NO") + "'";
		} else {
			sql += " AND A.RFID  IN (SELECT NEW_RFID FROM CTS_RFID_REPLACE WHERE VALID_FLG='Y')";
		}

		if (!getValueString("OWNER").equals("")) {
			sql += " AND A.OWNER = '" + getValueString("OWNER") + "'";
		}

		if (!getValueString("INV_CODE").equals("")) {
			sql += " AND A.INV_CODE = '" + getValueString("INV_CODE") + "'";
		}

		if (!getValueString("OWNER_CODE").equals("")) {
			sql += " AND A.OWNER_CODE = '" + getValueString("OWNER_CODE") + "'";
		}

		if (!getValueString("TURN_POINT").equals("")) {
			sql += " AND A.TURN_POINT = '" + getValueString("TURN_POINT") + "'";
		}

		sql += " ORDER BY A.RFID";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		String sql1 = "SELECT CLOTH_NO, COUNT (CLOTH_NO) AS TIMES FROM CTS_OUTD"
				+ " WHERE CLOTH_NO  IN (SELECT NEW_RFID FROM CTS_RFID_REPLACE WHERE VALID_FLG='Y')"
				+ " GROUP BY CLOTH_NO ORDER BY CLOTH_NO";
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		for (int i = 0; i < result1.getCount(); i++) {
			String clothNos = result1.getValue("CLOTH_NO", i);
			for (int j = 0; j < result.getCount(); j++) {
				if (result.getValue("CLOTH_NO", j).equals(clothNos)) {
					result.setData("TIMES", j, result1.getValue("TIMES", i));
					break;
				}
			}
		}
		
		String sql2="SELECT OLD_RFID,OLD_TIMES,NEW_RFID,VALID_FLG FROM CTS_RFID_REPLACE";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		for (int i = 0; i <result2.getCount(); i++) {
			if(result2.getBoolean("VALID_FLG", i)){
				for (int j = 0; j < result.getCount(); j++) {
					if(result.getValue("CLOTH_NO", j).equals(result2.getValue("NEW_RFID", i))){
						result.setData("TIMES", j, result.getInt("TIMES",j)+result2.getInt("OLD_TIMES", i));
						break;
					}
				}
			}
		}
		
		table.setParmValue(result);
		
	}
	
	public void onClear(){
		this.clearValue("CLOTH_NO;STATION_CODE;TURN_POINT;INV_CODE;OWNER;OWNER_CODE");
		table.removeAll();
	}
	
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "更替标签洗衣次数统计单");
	}
	
	public void showOwnerCode(){
		String OWNER = getValueString("OWNER");
		String sql="SELECT USER_ID, USER_NAME, COST_CENTER_CODE FROM SYS_OPERATOR WHERE USER_ID = '"+OWNER+"'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("OWNER_CODE", selParm.getValue("USER_NAME", 0));
	}

}
