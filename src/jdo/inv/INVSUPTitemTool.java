package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 灭菌记账字典维护Tool
 * </p>
 * 
 * <p>
 * Description: 灭菌记账字典维护Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangzl 20130502
 * @version 1.0
 */
public class INVSUPTitemTool extends TJDODBTool {
	/** 实例 */
	private static INVSUPTitemTool instance;

	/** 得到实例 */
	public static INVSUPTitemTool getInstance() {
		if (instance == null)
			return instance = new INVSUPTitemTool();
		return instance;
	}
	/**保存方法*/
	public TParm onSave(TParm parm){
		String sql="INSERT INTO INV_SUPTITEM (SUPITEM_CODE,SUPITEM_DESC,PY1,PY2,DESCRIPTION," 
											+"COST_PRICE,ADD_PRICE,OPT_USER,OPT_DATE,OPT_TERM) " 
					   +"VALUES (" 
					   			+"'"+parm.getData("SUPITEM_CODE")+"'," 
					   			+"'"+parm.getData("SUPITEM_DESC")+"'," 
					   			+"'"+parm.getData("PY1")+"'," 
					   			+"NULL," 
					   			+"'"+parm.getData("DESCRIPTION")+"'," 
					   			+""+parm.getDouble("COST_PRICE")+"," 
					   			+""+parm.getDouble("ADD_PRICE")+"," 
					   			+"'"+parm.getData("OPT_USER")+"'," 
					   			+"SYSDATE," 
					   			+"'"+parm.getData("OPT_TERM")+"')";
		TParm result=new TParm(this.update(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**更新方法*/
	public TParm onUpdate(TParm parm){
		String sql="UPDATE INV_SUPTITEM " 
					 +"SET SUPITEM_DESC='"+parm.getData("SUPITEM_DESC")+"'," +
					 		"PY1='"+parm.getData("PY1")+"'," +
					 		"DESCRIPTION='"+parm.getData("DESCRIPTION")+"'," +
					 		"COST_PRICE="+parm.getDouble("COST_PRICE")+"," +
					 		"ADD_PRICE="+parm.getDouble("ADD_PRICE")+"," +
					 		"OPT_USER='"+parm.getData("OPT_USER")+"'," +
					 		"OPT_DATE=SYSDATE," +
					 		"OPT_TERM='"+parm.getData("OPT_TERM")+"' " 
				   +"WHERE SUPITEM_CODE='"+parm.getData("SUPITEM_CODE")+"'";
		TParm result=new TParm(this.update(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**删除方法*/
	public TParm onDelete(TParm parm){
		String sql="DELETE INV_SUPTITEM WHERE SUPITEM_CODE='"+parm.getData("SUPITEM_CODE")+"'";
		TParm result=new TParm(this.update(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
}
