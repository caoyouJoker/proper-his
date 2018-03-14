package action.clp;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
/**
 * <p>
 * Title: 费用时程修改
 * </p>
 * 
 * <p>
 * Description: 医嘱时程替换操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 2014.08.29
 * @version 4.0
 */
public class CLPOrderReSchdCodeAction extends TAction{
	/**
	 * 替换界面保存操作
	 * @return
	 */
	public TParm onSave(TParm parm){
		TConnection conn = getConnection();
		TParm result=new TParm();
		String sql="";
		String setClncpathSql="";
		String setSchdSql="";
		String set="";
		if (null!=parm.getValue("RE_CLNCPATH_CODE")&&
				parm.getValue("RE_CLNCPATH_CODE").length()>0) {
			setClncpathSql="CLNCPATH_CODE='"+parm.getValue("RE_CLNCPATH_CODE")+"'";
		}
		if (null!=parm.getValue("RE_SCHD_CODE")&&
				parm.getValue("RE_SCHD_CODE").length()>0) {
			setSchdSql="SCHD_CODE='"+parm.getValue("RE_SCHD_CODE")+"'";
		}
		if (setClncpathSql.length()>0&&setSchdSql.length()>0) {
			set=setClncpathSql+","+setSchdSql;
		}else if(setClncpathSql.length()>0&&setSchdSql.length()<=0){
			set=setClncpathSql;
		}else if(setClncpathSql.length()<=0&&setSchdSql.length()>0){
			set=setSchdSql;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			sql="UPDATE IBS_ORDD SET "+set+" WHERE CASE_NO='"+
			parm.getValue("CASE_NO",i)+"' AND CASE_NO_SEQ='"+
			parm.getValue("CASE_NO_SEQ",i)+"' AND SEQ_NO='"+
			parm.getValue("SEQ_NO",i)+"'";
			result = new TParm(TJDODBTool.getInstance().update(sql,conn));
			if (result.getErrCode()<0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
}
