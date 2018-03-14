package jdo.util;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import jdo.sys.SYSRoomTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TStrike;
import com.dongyang.util.RunClass;
import com.dongyang.util.TSystem;

/**
 *
 * <p>
 * Title: 系统共用
 * </p>
 *
 * <p>
 * Description:
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
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Systems extends TStrike {
	/**
	 * 得到系统时间
	 *
	 * @return Timestamp
	 */
	public Timestamp getDate() {
		if (isClientlink())
			return (Timestamp) callServerMethod();
		return SystemTool.getInstance().getDate();
	}

	/**
	 * 得到病案号
	 *
	 * @return String
	 */
	public synchronized String getMrNo() {
		if (isClientlink())
			return (String) callServerMethod();
		return SystemTool.getInstance().getMrNo();
	}

	/**
	 * 得到住院号
	 *
	 * @return String
	 */
	public synchronized String getIpdNo() {
		if (isClientlink())
			return (String) callServerMethod();
		return SystemTool.getInstance().getIpdNo();
	}

	/**
	 * 得到取号原则
	 *
	 * @param regionCode
	 *            String
	 * @param systemCode
	 *            String
	 * @param operation
	 *            String
	 * @param section
	 *            String
	 * @return String
	 */
	public synchronized String getNo(String regionCode, String systemCode,
			String operation, String section) {
		if (isClientlink())
			return (String) callServerMethod(regionCode, systemCode, operation,
					section);
		return SystemTool.getInstance().getNo(regionCode, systemCode,
				operation, section);
	}

	/**
	 * 调用V3后端
	 *
	 * @param actionName
	 *            String Action名称
	 * @param methodName
	 *            String 方法名称
	 * @param tparm
	 *            TParm 参数"COMMIT组,RETURN组"
	 * @return TParm 参数"COMMIT组,RETURN组"
	 */
	public TParm doAction(String actionName, String methodName, TParm tparm) {
		Object obj = TSystem.getObject("V3Manager");
		if (obj == null) {
			TParm result = new TParm();
			result.setErr(-1, "没有连接V3系统");
			System.out.println("没有连接V3系统");
			return result;
		}
		return (TParm) RunClass.runMethod(obj, "doAction", new Object[] {
				actionName, methodName, tparm });
	}

	/**
	 * 调用V3后端
	 *
	 * @param ServiceName
	 * @param Parameter
	 * @return
	 */
	public Vector doService(String ServiceName, Vector Parameter) {
		Object obj = TSystem.getObject("V3Manager");
		if (obj == null) {
			System.out.println("没有连接V2系统");
			return null;
		}
		return (Vector) RunClass.runMethod(obj, "doService", new Object[] {
				ServiceName, Parameter });
	}

	/**
	 * 根据病房号取得病房信息
	 *
	 * @param room_code
	 *            病房号
	 * @return Map TParm{ROOM_CODE, ROOM_DESC, PY1, PY2, SEQ, DESCRIPT,
	 *         STATION_CODE, REGION_CODE, SEX_LIMIT_FLG, RED_SIGN, YELLOW_SIGN,
	 *         OPT_USER, OPT_DATE, OPT_TERM }
	 */
	public Map getSYSRoom(String room_code) {
		if (isClientlink())
			return (Map) callServerMethod(room_code);
		return SYSRoomTool.getInstance().selectdata(room_code).getData();
	}
	/**
	 * 根据给入的值全文检索，并返回pdf文件的名字
	 * @param queryWord String
	 * @return TParm
	 */
	public static TParm queryByWord(String queryWord){
		TParm result=new TParm();
		TParm parm=new TParm();
		parm.setData("QUERY_WORD",queryWord);

		return result;
	}

}
