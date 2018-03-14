package com.javahis.ui.cts;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

public class CTSUnfoldControl extends TControl{
	
	private static String CLOTH_NO1 = "";
	private static String CLOTH_NO2 = "";
	private static String STATION_CODE = "";
	private static String TURN_POINT = "";
	private static String PAT_FLG = "N";
	
	private static TTable table;
	private static String optIp = "";
	private static String optId = "";
	

    /**
	 * 初始化方法
	 */
	public void onInit() {
		optIp = Operator.getIP();
		optId = Operator.getID();
		table = (TTable) getComponent("TABLE");
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		getValue();
		
		StringBuilder sql = new StringBuilder();
		sql.append(
				" SELECT   A.RFID CLOTH_NO, A.CTS_COST_CENTRE STATION_CODE, A.INV_CODE, B.INV_CHN_DESC," +
				" A.ACTIVE_FLG, A.PAT_FLG, A.OWNER, A.TURN_POINT" +
				" FROM INV_STOCKDD A, INV_BASE B" +
				" WHERE A.INV_CODE = B.INV_CODE " +
				" AND A.RFID IS NOT NULL " +
				" AND B.INV_KIND = '08'");
		if(!"".equals(CLOTH_NO1) && !"".equals(CLOTH_NO2)){
			sql.append(" AND RFID BETWEEN '"+CLOTH_NO1+"' AND '"+CLOTH_NO2+"'");
		}
		sql.append(" ORDER BY A.CTS_COST_CENTRE, A.INV_CODE, A.RFID");
		
//		System.out.println(sql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		table.setParmValue(result);
	}
	
	/**
	 * 保存
	 */
	public void onSave(){
		getValue();
		
		TURN_POINT = getValueString("TURN_POINT");

		String sql =
			" UPDATE INV_STOCKDD" +
			" SET " +
			" PAT_FLG = '"+PAT_FLG+"'," ;
	
		if(!"".equals(STATION_CODE)){
			sql += " CTS_COST_CENTRE = '"+STATION_CODE+"'," ;
		}
		if(!"void".equals(TURN_POINT)){
			sql += " TURN_POINT = '"+TURN_POINT+"'," ;
		}
		sql += " OPT_USER = '"+optId+"'," +
		" OPT_DATE = SYSDATE," +
		" OPT_TERM = '"+optIp+"'," +
		" WRITE_FLG = 'Y'" +
		" WHERE RFID BETWEEN '"+CLOTH_NO1+"' AND '"+CLOTH_NO2+"'";
		System.out.println("update==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("操作成功");
		onQuery();
		
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		clearValue("CLOTH_NO1;CLOTH_NO2;STATION_CODE;TURN_POINT");
		table.removeRowAll();
	}
	
	
	/**
	 * 取得值
	 */
	public void getValue(){
		CLOTH_NO1=getValueString("CLOTH_NO1");
		CLOTH_NO2 = getValueString("CLOTH_NO2");
		
//		TURN_POINT = getValueString("TURN_POINT");
		STATION_CODE = getValueString("STATION_CODE");
		
	}
	
	public void onPat(){

		PAT_FLG = "Y";

	}
	
	public void onNoPat(){

		PAT_FLG = "N";

	}

	
	
}
