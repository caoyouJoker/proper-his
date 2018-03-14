package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 抗菌药品手术预防药品使用报表</p>
 *
 * <p>Description:抗菌药品手术预防药品使用报表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2014</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author 2014.02.12
 * @version 1.0
 */

public class UDDPatLimitinglevelTool  extends TJDOTool{
	
public static UDDPatLimitinglevelTool instanceObject;
	
	public static UDDPatLimitinglevelTool getInstance(){
	if(instanceObject==null){
		instanceObject=new UDDPatLimitinglevelTool();
	}
	   return instanceObject;
	}
	/**
	 * 查询全部出院人员
	 * @param parm
	 * @return
	 */
	public TParm getSelectTotnum(TParm parm){
		TParm result=new TParm();
		String drCode="";
		String deptCode="";
		if(!"".equals(parm.getValue("DEPT_CODE"))&&parm.getValue("DEPT_CODE")!=null){
			deptCode=" AND DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
		}
		if(!"".equals(parm.getValue("DR_CODE"))&&parm.getValue("DR_CODE")!=null){
			deptCode=" AND VS_DR_CODE='"+parm.getValue("DR_CODE")+"' ";
		}
		String sql="SELECT COUNT(DISTINCT CASE_NO) AS TOT_NUM, REGION_CODE,DEPT_CODE," +
				   " VS_DR_CODE,'出院' AS ODI_TYPE  FROM ADM_INP " +
				   " WHERE  DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	         	   " AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	         	   " AND DS_DATE IS NOT NULL "+deptCode+drCode +
	         	   " GROUP BY REGION_CODE,DEPT_CODE,VS_DR_CODE ORDER BY DEPT_CODE,VS_DR_CODE"; 
		//System.out.println("sql1111:::::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		
	}
	public TParm getselectAntnum(TParm parm){
		TParm result = new TParm();
		String deptCode ="";
		String drCode="";
		if(parm.getValue("DEPT_CODE")!=null&&!"".equals(parm.getValue("DEPT_CODE"))){
			deptCode=" AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
		}
		if(parm.getValue("VS_DR_CODE")!=null&&!"".equals(parm.getValue("VS_DR_CODE"))){
			drCode=" AND A.VS_DR_CODE='"+parm.getValue("VS_DR_CODE")+"'";
		}
		
		String sql=  "SELECT COUNT (DISTINCT A.CASE_NO ) AS ANTI_NUM ,A.REGION_CODE, " +
		             "A.DEPT_CODE,A.VS_DR_CODE,'出院' AS ODI_TYPE "+
				     "FROM ADM_INP A ,ODI_ORDER B "+
				     "WHERE A.CASE_NO=B.CASE_NO  " +
				     "AND A.DS_DATE IS NOT NULL "+
				     "AND B.ANTIBIOTIC_CODE IN ('02','03') "+
				     "AND B.ORDER_CAT1_CODE='PHA_W' "+
				     deptCode +drCode+
	                 "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	         		 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	         		 "GROUP BY A.REGION_CODE,A.DEPT_CODE,A.VS_DR_CODE ORDER BY A.DEPT_CODE,A.VS_DR_CODE ";
		
		//System.out.println("sql1111:::::"+sql);
		 
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

}
