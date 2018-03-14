package jdo.pda;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.sys.Operator;

/**
 * <p>
 * Title: PDA工具类
 * </p>
 * 
 * <p>
 * Description: PDA工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangjc 20170711
 * @version 1.0
 */
public class PDATool extends TJDODBTool {

	/**
	 * 实例
	 */
	public static PDATool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndAgentTool
	 */
	public static PDATool getInstance() {
		if (instanceObject == null) {
			instanceObject = new PDATool();
		}
		return instanceObject;
	}

	
	public TParm onQueryPDABindingIP(TParm parm) {
		String sql = "SELECT "
				+ " B.CHN_DESC,A.IP,A.ROOM_NO,A.OPBOOK_SEQ,A.TYPE_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM "
				+ " FROM "
				+ " OPE_IPROOM A, SYS_DICTIONARY B "
				+ " WHERE "
				+ " A.ROOM_NO = B.ID "
				+ " AND B.GROUP_ID = 'OPE_OPROOM' ";
		if(StringUtils.isNotEmpty(parm.getValue("ROOM_NO"))){
			sql += " AND A.ROOM_NO = '"+ parm.getValue("ROOM_NO") + "' ";
		}
		if(StringUtils.isNotEmpty(parm.getValue("IP"))){
			sql += " AND A.IP = '"+ parm.getValue("IP") + "' ";
		}
		if(StringUtils.isNotEmpty(parm.getValue("OPBOOK_SEQ"))){
			sql += " AND A.OPBOOK_SEQ LIKE '%"+ parm.getValue("OPBOOK_SEQ") + "%' ";
		}
		if(StringUtils.isNotEmpty(parm.getValue("TYPE_CODE"))){
			sql += " AND A.TYPE_CODE = '"+ parm.getValue("TYPE_CODE") + "' ";
		}
		sql += "ORDER BY A.ROOM_NO";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	public TParm onUpdatePDABindingIP(TParm parm, TConnection conn) {
		String sql = "UPDATE OPE_IPROOM SET "
				+ " IP ='"+parm.getValue("IP")+"', "
				+ " ROOM_NO = '"+parm.getValue("ROOM_NO")+"', "
				+ " OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"', "
				+ " TYPE_CODE = '"+parm.getValue("TYPE_CODE")+"', "
				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"',"
				+ " OPT_DATE = SYSDATE, "
				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"' "
				+ " WHERE IP ='"+parm.getValue("OLD_IP")+"'";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}
	
	public TParm onInsertPDABindingIP(TParm parm, TConnection conn) {
		String sql = "INSERT INTO OPE_IPROOM (IP,ROOM_NO,OPBOOK_SEQ,TYPE_CODE,OPT_USER,OPT_DATE,OPT_TERM) "
				+ " VALUES ('"
				+ parm.getValue("IP") 
				+"','"
				+ parm.getValue("ROOM_NO") 
				+"','"
				+ parm.getValue("OPBOOK_SEQ") 
				+"','"
				+ parm.getValue("TYPE_CODE") 
				+"','"
				+ parm.getValue("OPT_USER") 
				+ "',SYSDATE,'"
				+ parm.getValue("OPT_TERM") + "' )";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}
	
	public TParm onRemovePDABindingIP(TParm parm, TConnection conn) {
		String sql = "UPDATE OPE_IPROOM SET "
				+ " OPBOOK_SEQ = NULL,"
				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"', "
				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"', "
				+ " OPT_DATE = SYSDATE "
				+ " WHERE "
				+ " IP = '"+parm.getValue("IP")+"' "
				+ " AND ROOM_NO = '"+parm.getValue("ROOM_NO")+"' ";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}
	
	public TParm onDeletePDABindingIP(TParm parm, TConnection conn) {
		String sql = "DELETE FROM OPE_IPROOM WHERE "
				+ " IP = '"+parm.getValue("IP")+"' "
				+ " AND ROOM_NO = '"+parm.getValue("ROOM_NO")+"' ";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}



}
