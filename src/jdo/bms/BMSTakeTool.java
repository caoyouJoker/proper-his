package jdo.bms;

//import jdo.bms.ws.BmsTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * @author shibl
 * 
 */
public class BMSTakeTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static BMSTakeTool instanceObject;
	/**
     * 
     */
	private String MQuerysql = "SELECT *  FROM  BMS_BLDTAKEM WHERE BLOOD_TANO='<BLOOD_TANO>'";
	/**
     * 
     */
	private String DQuerysql = "SELECT *  FROM  BMS_BLDTAKED WHERE BLOOD_TANO='<BLOOD_TANO>' ORDER BY SEQ ";
	/**
     * 
     */
	private String Mdelsql = "DELETE FROM BMS_BLDTAKEM WHERE BLOOD_TANO='<BLOOD_TANO>'";
	/**
     * 
     */
	private String Ddelsql = "DELETE FROM BMS_BLDTAKED WHERE BLOOD_TANO='<BLOOD_TANO>'";
	/**
     * 
     */
	private String Minsertsql = "INSERT INTO BMS_BLDTAKEM(BLOOD_TANO,ADM_TYPE,"
			+ " CASE_NO,MR_NO,IPD_NO,BED_NO,"
			+ " DEPT_CODE,STATION_CODE,OPT_USER,"
			+ " OPT_DATE,OPT_TERM,BLOOD_TYPE,BLOOD_RH_TYPE,ORDER_DRCODE,ORDER_DRDATE" 
			
			//add by yangjj 20150601
			+ " ,OPE_FLG,OPE_ROOM "
			
			+")"
			+ " VALUES ('<BLOOD_TANO>','<ADM_TYPE>',"
			+ " '<CASE_NO>','<MR_NO>',"
			+ "'<IPD_NO>','<BED_NO>','<DEPT_CODE>',"
			+ "'<STATION_CODE>','<OPT_USER>',SYSDATE,'<OPT_TERM>',"
			+ "'<BLOOD_TYPE>','<BLOOD_RH_TYPE>','<ORDER_DRCODE>',TO_DATE('<ORDER_DRDATE>','YYYYMMDDHH24MISS')" 
			
			//add by yangjj 20150601
			+ " ,'<OPE_FLG>','<OPE_ROOM>'"
			
			+")";
	/**
     * 
     */
	private String Dinsertsql = "INSERT INTO BMS_BLDTAKED(BLOOD_TANO, SEQ,"
			+ " BLD_CODE,APPLY_QTY,UNIT_CODE, OPT_USER,"
			+ " OPT_DATE, OPT_TERM)VALUES('<BLOOD_TANO>','<SEQ>','<BLD_CODE>',"
			+ " '<APPLY_QTY>','<UNIT_CODE>',"
			+ " '<OPT_USER>',SYSDATE,'<OPT_TERM>')";

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static BMSTakeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BMSTakeTool();
		return instanceObject;
	}
    /**
     * 
     * @return
     */
	public static String getNo() {
		return SystemTool.getInstance().getNo("ALL", "BMS", "BMS_TAKENO", "BMS_TAKENO");
	}
	
	/**
	 * 建造SQL语句
	 * 
	 * @param SQL
	 *            原始语句
	 * @param obj
	 *            替换语句中?的数值数组
	 * @return SQL语句
	 */
	public String buildSQL(String SQL, TParm parm) {
		SQL = SQL.trim();
		Object[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			String name = (String) names[i];
			if (SQL.indexOf("<" + name.trim() + ">") == -1)
				continue;
			SQL = replace(SQL, "<" + name.trim() + ">", parm
					.getValue((String) names[i]));
		}
		return SQL;
	}

	public String replace(String s, String name, String value) {
		int index = s.indexOf(name);
		while (index >= 0) {
			s = s.substring(0, index) + value
					+ s.substring(index + name.length(), s.length());
			index = s.indexOf(name);
		}
		return s;
	}

	/**
	 * 构造器
	 */
	public BMSTakeTool() {
		onInit();
	}

	/**
	 * 得到主表sql
	 * 
	 * @param parm
	 * @return
	 */
	public String getMInsertsql(TParm parm) {
		//String sql = BmsTool.getInstance().buildSQL(Minsertsql, parm);
		String sql = buildSQL(Minsertsql, parm);
		return sql;
	}

	/**
	 * 得到细表sql
	 * 
	 * @param parm
	 * @return
	 */
	public String getDInsertsql(TParm parm) {
		//String sql = BmsTool.getInstance().buildSQL(Dinsertsql, parm);
		String sql = buildSQL(Dinsertsql, parm);
		return sql;
	}

	/**
	 * 得到主表删除
	 * 
	 * @param parm
	 * @return
	 */
	public String getMdelsql(TParm parm) {
		//String sql = BmsTool.getInstance().buildSQL(Mdelsql, parm);
		String sql = buildSQL(Mdelsql, parm);
		return sql;
	}

	/**
	 * 得到细表删除
	 * 
	 * @param parm
	 * @return
	 */
	public String getDdelsql(TParm parm) {
		//String sql = BmsTool.getInstance().buildSQL(Ddelsql, parm);
		String sql = buildSQL(Ddelsql, parm);
		return sql;
	}

	/**
	 * 得到主表查询
	 * 
	 * @param parm
	 * @return
	 */
	public String getMQuerysql(TParm parm) {
		//String sql = BmsTool.getInstance().buildSQL(MQuerysql, parm);
		String sql = buildSQL(MQuerysql, parm);
		return sql;
	}

	/**
	 * 得到细表查询
	 * 
	 * @param parm
	 * @return
	 */
	public String getDQuerysql(TParm parm) {
		//String sql = BmsTool.getInstance().buildSQL(DQuerysql, parm);
		String sql = buildSQL(DQuerysql, parm);
		return sql;
	}
}
