package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 天津住院 医保票据医保收费项目明细对应
 * </p>
 * 
 * <p>
 * Description:天津住院 医保票据医保收费项目明细对应
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhangp 20130528
 * @version 1.0
 */

public class TJINSRecpTool extends TJDOTool {
	// 查询对应关系
	private final String SQL = " SELECT TJINS01, TJINS02, TJINS03, TJINS04, TJINS05, TJINS06, TJINS07, TJINS08,"
			+ " TJINS09, TJINS10" + " FROM BIL_TJINSPARM";
	// 字段名，用;分割
	private final String COLUMNS = "TJINS01;TJINS02;TJINS03;TJINS04;TJINS05;TJINS06;TJINS07;TJINS08;TJINS09;TJINS10";
	/**
	 * 实例
	 */
	public static TJINSRecpTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RuleTool
	 */
	public static TJINSRecpTool getInstance() {
		if (instanceObject == null)
			instanceObject = new TJINSRecpTool();
		return instanceObject;
	}

	/**
	 * 取得天津医保字段与收费项目明细对应
	 * 
	 * @return TParm
	 */
	public TParm getColumns() {
		TParm result = new TParm();
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		String[] columList = COLUMNS.split(";");
		for (int i = 0; i < columList.length; i++) {
			if (parm.getValue(columList[i], 0).length() > 0) {
				result.setData(parm.getValue(columList[i], 0), columList[i]);
			}
		}
		return result;
	}
	
	public TParm updateRecpm(TParm parm, TConnection conn){
		String sql =
			" UPDATE BIL_IBS_RECPM" +
			" SET TJINS01 = '" + getParmValue(parm, "TJINS01") +"'," +
			" TJINS02 = '" + getParmValue(parm, "TJINS02") +"'," +
			" TJINS03 = '" + getParmValue(parm, "TJINS03") +"'," +
			" TJINS04 = '" + getParmValue(parm, "TJINS04") +"'," +
			" TJINS05 = '" + getParmValue(parm, "TJINS05") +"'," +
			" TJINS06 = '" + getParmValue(parm, "TJINS06") +"'," +
			" TJINS07 = '" + getParmValue(parm, "TJINS07") +"'," +
			" TJINS08 = '" + getParmValue(parm, "TJINS08") +"'," +
			" TJINS09 = '" + getParmValue(parm, "TJINS09") +"'," +
			" TJINS10 = '" + getParmValue(parm, "TJINS10") +"'" +
			" WHERE CASE_NO = '" + parm.getValue("CASE_NO") +"'" +
			" AND RECEIPT_NO = '" + parm.getValue("RECEIPT_NO") +"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
		return result;
	}
	
	private String getParmValue(TParm parm, String name){
		String value = parm.getValue(name);
		if(value.length()==0){
			value = "0";
		}
		return value;
	}
	
	public TParm selectTjInsDataForReturn(TParm parm){
		String sql = 
			" SELECT TJINS01, TJINS02, TJINS03, TJINS04, TJINS05, TJINS06, TJINS07, TJINS08," +
			" TJINS09, TJINS10" +
			" FROM BIL_IBS_RECPM" +
			" WHERE CASE_NO = '" + parm.getValue("CASE_NO") +"'" +
			" AND RECEIPT_NO = '" + parm.getValue("RECEIPT_NO") +"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TParm returnParm = new TParm();
		String[] columList = COLUMNS.split(";");
		for (int i = 0; i < columList.length; i++) {
			returnParm.setData(columList[i], - result.getDouble(columList[i], 0));
		}
		returnParm.setCount(1);
		return returnParm;
	}
	
	public TParm tjins2Word(){
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		parm = parm.getRow(0);
		return parm;
	}

}
