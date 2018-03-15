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
 * <p>Company: ProperSoft</p>
 *
 * @author 2014.02.12
 * @version 1.0
 */

public class UDDOpePreventionTool  extends TJDOTool{
	
public static UDDOpePreventionTool instanceObject;
	
	public static UDDOpePreventionTool getInstance(){
	if(instanceObject==null){
		instanceObject=new UDDOpePreventionTool();
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
		
		String sql=  "SELECT DISTINCT C.ORDER_CODE,A.CASE_NO, A.MR_NO,TO_CHAR(B.OP_DATE,'YYYY/MM/DD HH:MM:SS') AS OP_DATE , " +
				     "TO_CHAR(C.ORDER_DATE,'YYYY/MM/DD HH:MM:SS') AS ORDER_DATE, C.ORDER_DESC,E.OPT_CHN_DESC AS OP_CODE1, " +//B.OP_CODE1, "+
				     //"CEIL (B.OP_DATE - C.ORDER_DATE) AS DATE_DEFFEREBCE,
				     "D.PAT_NAME "+
				     "FROM ADM_INP A ,OPE_OPBOOK B, ODI_ORDER C, SYS_PATINFO D ,SYS_OPERATIONICD E "+
				     "WHERE A.CASE_NO=B.CASE_NO  " +
				     "AND A.CASE_NO=C.CASE_NO "+
				     "AND A.MR_NO=D.MR_NO "+
				    "AND B.OP_CODE1 = E.OPERATION_ICD "+
				     "AND A.DS_DATE IS NOT NULL "+
				     "AND C.ANTIBIOTIC_CODE IS NOT NULL "+
				     "AND C.ANTIBIOTIC_WAY='01' "+
				     "AND E.PHA_PREVENCODE IS NOT NULL "+
				     "AND C.RX_KIND='ST' "+
				     deptCode +drCode+
	                 "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMMDDHH24MISS') "+
	         		 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMMDDHH24MISS') ORDER BY A.CASE_NO ";
		
		
	         		  
		 
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	

}
