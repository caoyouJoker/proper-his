package action.sys;

import java.sql.SQLException;
import java.sql.Statement;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class SYSPYCODEAction extends TAction {

	/**
	 * Ö´ÐÐ¸üÐÂ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdate(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		Statement statement=null;
		try {
			 statement = conn.createStatement();
			String sql = (String)parm.getData("UPDATE_PY");
			TParm badParm = new TParm(TJDODBTool.getInstance().update(sql,
					statement));
			if (badParm.getErrCode() < 0) {
				conn.rollback();
				statement.close();
				conn.close();
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, e.getMessage());
			err(e.getMessage());
		}
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn.commit();
		conn.close();
		return result;
	}
}
