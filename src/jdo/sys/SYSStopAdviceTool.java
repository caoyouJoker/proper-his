/**
 * 
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 长期医嘱停用查询
 * </p>
 * 
 * <p>
 * Description: 长期医嘱停用查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wufc 2012-8-7上午10:31:04
 * @version 1.0
 */
public class SYSStopAdviceTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SYSStopAdviceTool instance;
	/**
	 * 构造方法
	 */
	private SYSStopAdviceTool(){
		onInit();
	}
	/**
	 * 得到实例的静态方法
	 * @return
	 */
	public static SYSStopAdviceTool getInstance(){
		if(instance == null){
			instance = new SYSStopAdviceTool();
		}
		return instance;
	}
	public TParm onQuery(TParm parm){
		String condition = (String) parm.getData("condition");
		String sql = "SELECT A.ORDER_CODE AS ORDER_CODE ,A.ORDER_DESC || CASE "+
							"WHEN B.SPECIFICATION IS NOT NULL THEN '(' || B.SPECIFICATION || ')' ELSE '' END "+ 
							"AS ORDER_DESC ,A.MR_NO AS MR_NO,C.PAT_NAME AS PAT_NAME ," +
							"H.BED_NO_DESC AS BED_NO ,D.DEPT_CHN_DESC AS DEPT_CHN_DESC ,E.STATION_DESC AS STATION_DESC ,"+ 
							"F.USER_NAME AS TREAT_DOC_NAME ,G.USER_NAME AS BILL_CREATE_NAME ,A.ORDER_DATE AS BILL_CREATE_DATE" +
					" FROM ODI_ORDER A,SYS_FEE B,SYS_PATINFO C,SYS_DEPT D,SYS_STATION E,SYS_OPERATOR F,SYS_OPERATOR G, SYS_BED H" +
					" WHERE " + condition + " A.RX_KIND='UD' AND A.DC_DATE IS NULL AND B.ORDER_CODE=A.ORDER_CODE AND "+
							"B.ACTIVE_FLG='N' AND C.MR_NO=A.MR_NO AND D.DEPT_CODE=A.DEPT_CODE AND "+
							"E.STATION_CODE=A.STATION_CODE AND F.USER_ID=A.VS_DR_CODE AND G.USER_ID=A.ORDER_DR_CODE AND H.BED_NO = A.BED_NO " +
					" ORDER BY D.DEPT_CHN_DESC,E.STATION_DESC, TREAT_DOC_NAME";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
