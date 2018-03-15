package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:麻精空瓶回收
 * </p>
 * 
 * <p>
 * Description:麻精空瓶回收
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
 * @author fuwj 2012.10.23
 * @version 1.0
 */

public class SPCRecoverTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SPCRecoverTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCRecoverTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCRecoverTool();
		return instanceObject;
	}

	/**
	 * 构造器 
	 */
	public SPCRecoverTool() {
		//setModuleName("spc\\SPCInStoreModule.x");
		onInit();
	}
	  
	
	/**
	 * 获取麻精药品回收信息
	 * @param parm
	 * @return
	 */
	public TParm getRecover(TParm parm) {
		String toxicId = parm.getValue("TOXIC_ID");
		String isRecover= parm.getValue("IS_RECOVER");  
		String orgCode = parm.getValue("ORG_CODE");
		String sql = "SELECT A.TOXIC_ID1,A.ORDER_DESC,A.SPECIFICATION," +
				" (SELECT D.BED_NO_DESC FROM SYS_BED D WHERE D.BED_NO=A.BED_NO) AS BED_NO,A.MR_NO,B.PAT_NAME,C.DEPT_CHN_DESC,A.RECLAIM_USER,A.RECLAIM_DATE FROM IND_CABDSPN A," +
				"SYS_PATINFO B,SYS_DEPT C WHERE A.MR_NO=B.MR_NO AND A.DEPT_CODE=C.DEPT_CODE " +
				"AND A.TOXIC_ID1 IS NOT NULL AND A.IS_RECLAIM='"+isRecover+"' AND A.STATION_CODE='"+orgCode+"' "; 
		if(toxicId!=null&&!"".equals(toxicId)) {				
			sql = sql +" AND (A.TOXIC_ID1='"+toxicId+"' or A.TOXIC_ID2='"+toxicId+"' or A.TOXIC_ID3='"+toxicId+"')";
		}	    
		return new TParm(TJDODBTool.getInstance().select(sql)); 
	}
	
	/**
	 * 更新回收状态
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateRecover(TParm parm,TConnection conn) {
		 String toxicId = parm.getValue("TOXIC_ID");
		 String userId = parm.getValue("RECLAIM_USER");
		 String sql = " UPDATE IND_CABDSPN A SET A.IS_RECLAIM='Y',A.RECLAIM_DATE=SYSDATE," +
		 		"A.RECLAIM_USER='"+userId+"' WHERE " +
		 		"(A.TOXIC_ID1='"+toxicId+"' OR A.TOXIC_ID2='"+toxicId+"' OR A.TOXIC_ID3='"+toxicId+"')";
		 return new TParm(TJDODBTool.getInstance().update(sql,conn)); 
	}
	
	/**
	 * 断开麻精与容器关系
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteToxic(TParm parm,TConnection conn) {
		 String toxicId = parm.getValue("TOXIC_ID");
		 String sql = "UPDATE IND_CONTAINERD SET CONTAINER_ID = NULL WHERE TOXIC_ID='"+toxicId+"'";
		 return new TParm(TJDODBTool.getInstance().update(sql,conn)); 
	}
					   
	
}
