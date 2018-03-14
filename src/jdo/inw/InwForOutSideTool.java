package jdo.inw;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: 住院护士站对外总接口    
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH
 * 
 * @version 1.0
 */
public class InwForOutSideTool extends TJDODBTool {

	/**
	 * 实例
	 */
	private static InwForOutSideTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static InwForOutSideTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwForOutSideTool();
		return instanceObject;
	}

	public InwForOutSideTool() {
	}
   //=============================================   chenxi modify 20130321
	/**
	 * 检查该病人医嘱是否还有未执行的医嘱
	 * 
	 * @return boolean true-还有为执行的 false-没了
	 */
	public boolean checkOrderisEXETool(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo
				+ "' AND (NS_EXEC_CODE IS NULL OR (DC_DR_CODE IS NOT NULL AND NS_EXEC_DC_CODE IS NULL)) "//添加长期医嘱停用执行校验
				+ "AND DSPN_KIND<>'RT' AND DC_NS_CHECK_DATE IS NULL";//shibl  20121101 add 护士取消审核的时间
		TParm result = new TParm(select(checkSql));
		// 如果没有为执行的数据返回数据集数量为0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	   //=============================================   chenxi modify 20130321
	/**
	 * 检查该病人是否有药房未审核的医嘱
	 * 
	 * @return boolean true-还有为执行的 false-没了
	 */
	public boolean checkDrug(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo
				+ "' AND NS_CHECK_CODE IS NOT NULL AND RX_KIND IN ('UD','ST','DS') " 
				+"   AND DC_NS_CHECK_DATE IS NULL " 
				+"   AND (ORDER_CAT1_CODE='PHA_W' OR ORDER_CAT1_CODE='PHA_C')" 
				//过滤术中医嘱 wanglong add 20150410
			    + "  AND  (OPBOOK_SEQ IS NULL OR (OPBOOK_SEQ IS NOT NULL AND EXEC_DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE CLASSIFY='2'))) "
	            +"   AND  DISPENSE_FLG='N' " 
				+"   AND  PHA_CHECK_DATE IS NULL 	";
//		System.out.println("sql=========11111==="+checkSql);
		TParm result = new TParm(select(checkSql));
		// 如果没有为执行的数据返回数据集数量为0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 检查该病人是否有药房未配药的医嘱
	 * 
	 * @return boolean true-还有未执行的 false-没了
	 */
	public boolean exeDrug(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
        String checkSql =
                "SELECT COUNT(A.CASE_NO) AS COUNT             "
                        + " FROM ODI_DSPNM A, SYS_FEE B                "
                        + "WHERE A.ORDER_CODE = B.ORDER_CODE "// wanglong add 20140725 过滤掉为备注的医嘱（该字段无默认值，空则表示非备注）
                        + "   AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) AND A.CASE_NO='"
                        + caseNo
                        + "'  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
                        + "   AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL "
                        + "   AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL"
                        + "   AND A.DISPENSE_FLG='N'";
//		System.out.println("sql=========222222==="+checkSql);
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	   //=============================================   chenxi modify 20130321
	/**
	 * 检查该病人医嘱是否还有未审核的医嘱
	 * 
	 * @return boolean true-还有为审核的 false-没了
	 */
	public boolean checkOrderisCHECKTool(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND (NS_CHECK_CODE IS NULL OR (DC_DR_CODE IS NOT NULL AND DC_NS_CHECK_CODE IS NULL))";
		//wanglong add 20150106
        checkSql+=" AND OPBOOK_SEQ IS NULL ";//过滤术中医嘱
        // add by wangb 2015/11/11 过滤院前用药和术中医嘱，只校验临时、长期和出院带药
        checkSql+=" AND RX_KIND IN ('ST','UD','DS') ";
		TParm result = new TParm(select(checkSql));
		// 如果没有为执行的数据返回数据集数量为0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 检查该病人是否开立医嘱
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisEXISTTool(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' ";
		TParm result = new TParm(select(checkSql));
		// 如果没有为执行的数据返回数据集数量为0
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 检查该病人是否还存在已执行医嘱
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisExistExec(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_DSPNM WHERE CASE_NO='"
				+ caseNo + "' AND NS_EXEC_CODE IS NOT NULL AND DC_NS_CHECK_DATE IS NULL  ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  ORDER_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 检查该病人是否还存在已审核医嘱
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisExistCheck(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' AND NS_CHECK_CODE IS NOT NULL  ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  ORDER_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 检查该病人是否还存在医嘱
	 * 
	 * @return boolean true
	 */
	public boolean checkOrderisExist(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT COUNT(CASE_NO) AS COUNT FROM ODI_ORDER WHERE CASE_NO='"
				+ caseNo + "' ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  ORDER_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return false;
		return true;
	}
	/**
	 * 检查该病人是否开立医嘱
	 * 
	 * @return boolean true
	 */
	public TParm checkOrderFee(TParm Parm) {
		String caseNo = (String) Parm.getData("CASE_NO");
		String checkSql = "SELECT SUM(TOT_AMT) AS TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"
				+ caseNo + "' ";
		if(!Parm.getValue("IN_DEPT_CODE").equals("")){
			checkSql+=" AND  DEPT_CODE='"+Parm.getValue("IN_DEPT_CODE")+"'";
		}
		if(!Parm.getValue("IN_STATION_CODE").equals("")){
			checkSql+=" AND  STATION_CODE='"+Parm.getValue("IN_STATION_CODE")+"'";
		}
		if(!Parm.getValue("IN_DATE").equals("")){
			checkSql+=" AND  BILL_DATE>=TO_DATE('"+Parm.getValue("IN_DATE")+"','YYYYMMDD HH24MISS')";
		}
		TParm result = new TParm(select(checkSql));
		if (result.getCount() <= 0 || result.getInt("COUNT", 0) == 0)
			return result;
		return result;
	}
}
