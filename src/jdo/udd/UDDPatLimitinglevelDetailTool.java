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

public class UDDPatLimitinglevelDetailTool  extends TJDOTool{
	
public static UDDPatLimitinglevelDetailTool instanceObject;
	
	public static UDDPatLimitinglevelDetailTool getInstance(){
	if(instanceObject==null){
		instanceObject=new UDDPatLimitinglevelDetailTool();
	}
	   return instanceObject;
	}
	
	public TParm getselectInum(TParm parm){
		TParm result = new TParm();
		String deptCode ="";
		String drCode="";
		if(parm.getValue("DEPT_CODE")!=null&&!"".equals(parm.getValue("DEPT_CODE"))){
			deptCode=" AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
		}
		if(parm.getValue("VS_DR_CODE")!=null&&!"".equals(parm.getValue("VS_DR_CODE"))){
			drCode=" AND A.VS_DR_CODE='"+parm.getValue("VS_DR_CODE")+"'";
		}
		
		String sql=  "SELECT DISTINCT A.CASE_NO,A.REGION_CODE, " +
		             "'在院'AS ODI_TYPE, "+
				     " A.MR_NO,A.DEPT_CODE,A.VS_DR_CODE,C.PAT_NAME "+
				     "FROM ADM_INP A , ODI_ORDER B, SYS_PATINFO C "+
				     "WHERE A.CASE_NO=B.CASE_NO  " +
				     "AND A.MR_NO=C.MR_NO "+
				     "AND A.DS_DATE IS NOT NULL "+
				     "AND B.ANTIBIOTIC_CODE IN ('02','03') "+
				     "AND B.ORDER_CAT1_CODE='PHA_W' "+
				     deptCode +drCode+
	                 "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	         		 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') "+
	         		 "ORDER BY A.DEPT_CODE,A.VS_DR_CODE ";
		 //System.out.println("sql:::::::::::::sssssssss:::::::"+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

}
