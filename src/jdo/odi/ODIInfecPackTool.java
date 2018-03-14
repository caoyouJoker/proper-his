package jdo.odi;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:感染医嘱套餐 工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
public class ODIInfecPackTool extends TJDOTool {
	public ODIInfecPackTool() {
		onInit();
	}

	private static ODIInfecPackTool instanceObject;

	public static ODIInfecPackTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new ODIInfecPackTool();
		}
		return instanceObject;
	}

	/**
	 * 得到icd
	 * 
	 * @param packcode
	 * @return
	 */
	public TParm getInfecIcdParm(String packcode) {
		String sql = "SELECT ICD_CODE_BEGIN,ICD_CODE_END FROM ODI_INFEC_PACKICD "
				+ "WHERE PACK_CODE='" + packcode + "'";
		TParm parmicd = new TParm(TJDODBTool.getInstance().select(sql));
		if (parmicd.getErrCode() < 0)
			return parmicd;
		return parmicd;
	}

	/**
	 * 得到医嘱
	 * 
	 * @param packcode
	 * @return
	 */
	public TParm getInfecOrderParm(String packcode) {
		String sql = "SELECT * FROM ODI_INFEC_PACKORDER " + "WHERE PACK_CODE='"
				+ packcode + "' ORDER BY SEQ_NO";
		TParm parmorder = new TParm(TJDODBTool.getInstance().select(sql));
		if (parmorder.getErrCode() < 0)
			return parmorder;
		return parmorder;
	}

	/**
	 * 删除套餐
	 * 
	 * @param packcode
	 * @param conn
	 * @return
	 */
	public TParm onDeletePackM(String packcode, TConnection conn) {
		String sql = "DELETE FROM ODI_INFEC_PACKM WHERE PACK_CODE='" + packcode
				+ "'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (parm.getErrCode() < 0) {
			return parm;
		}
		return parm;
	}

	/**
	 * 删除icd
	 * 
	 * @param packcode
	 * @param conn
	 * @return
	 */
	public TParm onDeletePackICD(String packcode, TConnection conn) {
		String sql = "DELETE FROM ODI_INFEC_PACKICD WHERE PACK_CODE='"
				+ packcode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (parm.getErrCode() < 0) {
			return parm;
		}
		return parm;
	}

	/**
	 * 删除医嘱
	 * 
	 * @param packcode
	 * @param conn
	 * @return
	 */
	public TParm onDeletePackORDER(String packcode, TConnection conn) {
		String sql = "DELETE FROM ODI_INFEC_PACKORDER WHERE PACK_CODE='"
				+ packcode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (parm.getErrCode() < 0) {
			return parm;
		}
		return parm;
	}

	/**
	 * 插入主表
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertPackM(TParm parm, TConnection conn) {
		if (parm == null)
			return parm;
		String sql = "INSERT INTO ODI_INFEC_PACKM "
				+ " (PACK_CODE, PACK_DESC, PY1, MESSAGE, OPT_DATE,"
				+ " OPT_TERM, OPT_USER,SMEALTYPE,PHA_PREVENCODE)" + " VALUES" + " ('" //add PHA_PREVENCODE(“抗菌素限制类型”),
				+ parm.getValue("PACK_CODE") + "', '"                                 //SMEALTYPE(套餐类型) caoyong 2013813
				+ parm.getValue("PACK_DESC") + "','" + parm.getValue("PY1")
				+ "','" + parm.getValue("MESSAGE") + "', SYSDATE, " + " '"
				+ parm.getValue("OPT_TERM") + "', '"
				+ parm.getValue("OPT_USER") + "','"
				+ parm.getValue("SMEALTYPE")+"','"
				+ parm.getValue("PHA_PREVENCODE")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		
		
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 插入icd表
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertPackicd(TParm parm, TConnection conn) {
		if (parm.getCount() <= 0)
			return parm;
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmRow = parm.getRow(i);
			String sql = "INSERT INTO ODI_INFEC_PACKICD"
					+ "(PACK_CODE, ICD_TYPE_BEGIN, ICD_CODE_BEGIN, ICD_TYPE_END, ICD_CODE_END, "
					+ " SEQ, OPT_DATE, OPT_TERM, OPT_USER)" + " VALUES "
					+ " ('" + parmRow.getValue("PACK_CODE") + "', '"
					+ parmRow.getValue("ICD_TYPE_BEGIN") + "','"
					+ parmRow.getValue("ICD_CODE_BEGIN") + "'," + "'"
					+ parmRow.getValue("ICD_TYPE_END") + "','"
					+ parmRow.getValue("ICD_CODE_END") + "'," + "'"
					+ parmRow.getValue("SEQ") + "', SYSDATE, " + " '"
					+ parmRow.getValue("OPT_TERM") + "', '"
					+ parmRow.getValue("OPT_USER") + "')";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * 插入医嘱表
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertPackorder(TParm parm, TConnection conn) {
		if (parm == null)
			return parm;
		if (parm.getCount() <= 0)
			return parm;
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			
			TParm parmRow = parm.getRow(i);
			String sql = "INSERT INTO ODI_INFEC_PACKORDER"
					+ "(PACK_CODE, SEQ_NO, DESCRIPTION, SPECIFICATION, ORDER_DESC, "
					+ "TRADE_ENG_DESC, LINKMAIN_FLG, LINK_NO, ORDER_CODE, MEDI_QTY, "
					+ "MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, DCTEXCEP_CODE, "
					+ "CAT1_TYPE, ORDER_CAT1_CODE, OPT_TERM, OPT_DATE, OPT_USER)"
					+ "VALUES" + "('"
					+ parmRow.getValue("PACK_CODE")
					+ "','"
					+ parmRow.getValue("SEQ_NO")
					+ "', '"
					+ parmRow.getValue("DESCRIPTION")
					+ "', '"
					+ parmRow.getValue("SPECIFICATION")
					+ "', '"
					+ parmRow.getValue("ORDER_DESC")
					+ "', "
					+ "'"
					+ parmRow.getValue("TRADE_ENG_DESC")
					+ "', '"
					+ parmRow.getValue("LINKMAIN_FLG")
					+ "', '"
					+ parmRow.getValue("LINK_NO")
					+ "', '"
					+ parmRow.getValue("ORDER_CODE")
					+ "', '"
					+ parmRow.getValue("MEDI_QTY")
					+ "', "
					+ "'"
					+ parmRow.getValue("MEDI_UNIT")
					+ "', '"
					+ parmRow.getValue("FREQ_CODE")
					+ "', '"
					+ parmRow.getValue("ROUTE_CODE")
					+ "', '"
					+ parmRow.getValue("TAKE_DAYS")
					+ "', '"
					+ parmRow.getValue("DCTEXCEP_CODE")
					+ "', "
					+ "'"
					+ parmRow.getValue("CAT1_TYPE")
					+ "', '"
					+ parmRow.getValue("ORDER_CAT1_CODE")
					+ "', '"
					+ parmRow.getValue("OPT_TERM")
					+ "', SYSDATE, '"
					+ parmRow.getValue("OPT_USER") + "')";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}
	/**
	 * add caoyong 20140226 
	 * 删除 ODI_INFEC_PACKORDER 医嘱
	 * @param parm
	 * @return
	 */
	public TParm onDeleteIcd(TParm parm){
		TParm result=new TParm();
		String sql="DELETE FROM ODI_INFEC_PACKORDER " +
				    "WHERE PACK_CODE='"+parm.getValue("PACK_CODE",0)+"' " +
					"AND ORDER_CODE='"+parm.getValue("ORDER_CODE",0)+"'" +
					"AND SEQ_NO='"+parm.getValue("SEQ_NO",0)+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
	      return result;
	}
}
