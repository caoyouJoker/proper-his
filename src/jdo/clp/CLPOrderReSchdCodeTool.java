package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title:路径时程替换
 * </p>
 * 
 * <p>
 * Description: 医嘱替换时程操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 2014-9-11
 * @version 1.0
 */
public class CLPOrderReSchdCodeTool extends TJDOTool {
	public CLPOrderReSchdCodeTool() {
		setModuleName("clp\\CLPOrderReSchdCodeModule.x");
		onInit();
	}
	 /**
     * 实例
     */
    public static CLPOrderReSchdCodeTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static CLPOrderReSchdCodeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPOrderReSchdCodeTool();
        return instanceObject;
    }
	/**
	 * 查询住院病患表数据
	 * @param parm
	 * @return
	 */
	public TParm selectAdmInp(TParm parm){
		String sql = "SELECT A.CASE_NO,CASE WHEN A.DS_DATE IS NULL THEN '在院' ELSE '出院' "
				+ " END ADM_STATUS ,A.MR_NO,B.PAT_NAME,A.IN_DATE,A.CLNCPATH_CODE "
				+ " FROM ADM_INP A ,SYS_PATINFO B "
				+ " WHERE A.MR_NO=B.MR_NO AND (A.CANCEL_FLG IS NULL OR A.CANCEL_FLG='N') "
				+ " AND A.CLNCPATH_CODE IS NOT NULL";
		if(parm.getData("FLG")==null || parm.getValue("FLG").equals("")){
				sql += " AND A.IN_DATE BETWEEN TO_DATE('"
						+ parm.getValue("START_IN_DATE") + "','YYYYMMDDHH24MISS') "
						+ "AND TO_DATE('" + parm.getValue("END_IN_DATE")
						+ "','YYYYMMDDHH24MISS')";
		}		
		if (parm.getValue("STATUS").length()>0) {
			sql+=parm.getValue("STATUS");
		}
		if (parm.getValue("MR_NO").length()>0) {
			sql+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
		}
		if (parm.getValue("DEPT_CODE").length()>0) {
			sql+=" AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
		}
		if (parm.getValue("STATION_CODE").length()>0) {
			sql+=" AND A.STATION_CODE='"+parm.getValue("STATION_CODE")+"'";
		}
//		if (parm.getValue("MR_NO").length()>0) {
//			sql+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
//		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//TParm result=query("selectAdmInp",parm);
		return result;
	}
	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryIbsOrder(TParm parm){
		String sql="SELECT 'N' FLG,A.CLNCPATH_CODE,A.SCHD_CODE,B.ORDER_DESC,B.DESCRIPTION,A.MEDI_QTY,A.MEDI_UNIT,"+
                   "A.FREQ_CODE,A.DOSAGE_QTY,A.TOT_AMT,A.BILL_DATE,A.OPT_USER,A.CASE_NO_SEQ,A.SEQ_NO,A.CASE_NO "+
                   "FROM IBS_ORDD A ,SYS_FEE B "+
                   "WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
		if (parm.getValue("CLNCPATH_CODE").toString().length()>0) {
			sql+=" AND A.CLNCPATH_CODE='"+parm.getValue("CLNCPATH_CODE")+"'";
		}
		if (parm.getValue("SCHD_CODE").toString().length()>0) {
			sql+=" AND A.SCHD_CODE='"+parm.getValue("SCHD_CODE")+"'";
		}
		if (parm.getValue("EXEC_DEPT_CODE").toString().length()>0) {
			sql+=" AND A.EXE_DEPT_CODE='"+parm.getValue("EXEC_DEPT_CODE")+"'";
		}
		if (parm.getValue("OPT_USER").toString().length()>0) {
			sql+=" AND A.OPT_USER='"+parm.getValue("OPT_USER")+"'";
		}
		if (parm.getValue("IN_DEPT_CODE").toString().length()>0) {
			sql+=" AND A.DEPT_CODE='"+parm.getValue("IN_DEPT_CODE")+"'";
		}
		if (parm.getValue("IN_STATION_CODE").toString().length()>0) {
			sql+=" AND A.STATION_CODE='"+parm.getValue("IN_STATION_CODE")+"'";
		}
		if (parm.getValue("DR_CODE").toString().length()>0) {
			sql+=" AND A.DR_CODE='"+parm.getValue("DR_CODE")+"'";
		}
		if (parm.getValue("START_BILL_DATE").toString().length()>0) {
			sql+=" AND A.BILL_DATE BETWEEN TO_DATE("+parm.getValue("START_BILL_DATE")+",'YYYYMMDDHH24MISS') AND TO_DATE("+parm.getValue("END_BILL_DATE")+",'YYYYMMDDHH24MISS')";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//TParm result=query("queryIbsOrder",parm);
		return result;
	}
	/**
	 * 更新收费医嘱路径代码和时程
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateIbsOrddBySchdCode(TParm parm,TConnection conn){
		TParm result=update("updateIbsOrddBySchdCode",parm,conn);
		return result;
	}
}
