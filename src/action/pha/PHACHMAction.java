package action.pha;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jdo.pha.PHACHMTool;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:普华中草药数据交互接口
 * </p>
 * 
 * <p>
 * Description:普华中草药数据交互接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangb
 * @version 1.0
 */
public class PHACHMAction extends TAction {
	
    /**
     * 获得数据连接
     * 
     * @return Connection 连接对象
     */
	public Connection getMDConnection() {
		Connection conn = null;
		String dbDriver = "org.firebirdsql.jdbc.FBDriver";
		TConfig prop = this.getProp();
		String dbPort = prop.getString("CHM.DB.Port");
		String database = prop.getString("CHM.DB.DataBase");
		String userName = prop.getString("CHM.DB.UserName");
		String password = prop.getString("CHM.DB.Password");
		String address = prop.getString("CHM.DB.Address");
		String url = "jdbc:firebirdsql://" + address + ":" + dbPort + "/"
				+ database;
		
		try {
			Class.forName(dbDriver).newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			return conn;
		} catch(ClassNotFoundException cnf) {
			System.out.println("driver not find:" + cnf);
			return null;
		} catch (SQLException sqle) {
			System.out.println("can't connection db:" + sqle);
			return null;
		} catch (Exception e) {
			System.out.println("Failed to load JDBC/ODBC driver.");
			return null;
		}
	}
	
	/**
	 * 向中草药接口发送数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertIntoDataPrescription(TParm parm) {
		Connection conn = this.getMDConnection();
		// 中草药接口_数据插入
		TParm result = PHACHMTool.getInstance().executeInsert(parm, conn);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		return result;
	}
	
	/**
	 * 读取 TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
