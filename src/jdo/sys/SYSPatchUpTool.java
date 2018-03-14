package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: 执行批次删除类
 * </p>
 * 
 * <p>
 * Description: 执行批次删除类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangzl
 * @version 1.0
 */
public class SYSPatchUpTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SYSPatchUpTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static SYSPatchUpTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSPatchUpTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SYSPatchUpTool() {
		onInit();
	}

	/**
	 * 删除
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onDelete(TParm parm) {
		TParm result = null;
		int count = parm.getCount("PATCH_CODE");
		for (int i = 0; i < count; i++) {
			String sql = "DELETE FROM SYS_PATCH_LOG WHERE PATCH_CODE='"
					+ parm.getData("PATCH_CODE", i)
					+ "'   AND PATCH_START_DATE='"
					+ parm.getData("START_DATE", i) + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm) {
		String sqlWhere="";
		if(parm.getInt("patchType")==2){
			sqlWhere = " AND L.PATCH_TYPE=2";
		}
		if(parm.getInt("patchType")==1){
			 sqlWhere = " AND L.PATCH_TYPE=1";
		}
		Object PATCH_DESCWhere = "";
		if (parm.getData("patch_desc").toString().length() != 0){
			PATCH_DESCWhere = " AND L.PATCH_CODE ='"+parm.getData("patch_desc")+"'";
		}
		String sql = "SELECT 'true' AS FLG,P.PATCH_DESC,"
			+         " TO_CHAR (TO_DATE (L.PATCH_START_DATE, 'YYYYMMDD HH24MISS'),'YYYY/MM/DD HH24:MI:SS')  "
			+         " AS PATCH_START_DATE,"
			+         " TO_CHAR (TO_DATE (L.PATCH_END_DATE, 'YYYYMMDD HH24MISS'),'YYYY/MM/DD HH24:MI:SS')  "
			+         " AS PATCH_END_DATE,"
			+         " L.PATCH_REOMIT_INDEX, L.PATCH_STATUS, L.PATCH_MESSAGE, L.SERVER_IP,L.PATCH_CODE,"
			+         " L.PATCH_START_DATE AS START_DATE"
			+ "  FROM SYS_PATCH_LOG L,SYS_PATCH P" 
			+ "  WHERE  L.PATCH_START_DATE>='"
			+ parm.getData("dateStr") + "' AND L.PATCH_START_DATE<='" + parm.getData("dateStre") + "'"
			+ sqlWhere + PATCH_DESCWhere
			+         "AND L.PATCH_CODE=P.PATCH_CODE"
	        + "  ORDER BY L.PATCH_CODE,L.PATCH_START_DATE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
