package jdo.sta;

import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 0点在院病人Tool
 * </p>
 * 
 * <p>
 * Description:  0点在院病人Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wukai 20160901
 * @version 1.0
 */
public class STAZeroInHosTool extends TJDOTool {
	
	private static STAZeroInHosTool mInstance;
	
	public static STAZeroInHosTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new STAZeroInHosTool();
		}
		return mInstance;
	}

	public STAZeroInHosTool() {
		setModuleName("sta\\STAZeroInHosModule.x");
		onInit();
	}
	
	/**
	 * 插入数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertData(TParm parm,TConnection conn) {
		System.out.println("-------------------");
		//System.out.println("==============  cccc  ============");
		TParm inParm=selectData(parm.getValue("CASE_NO"),parm.getValue("IN_DEPT_CODE"));
		System.out.println("99999"+inParm);
		parm.setData("IN_ICU_DATE", inParm.getData("IN_ICU_DATE"));
		parm.setData("ICU_INTUBATION_DATE", inParm.getData("ICU_INTUBATION_DATE"));
		parm.setData("ANTI_DATE", inParm.getData("ANTI_DATE"));
		parm.setData("ANTI_FLG", inParm.getData("ANTI_FLG"));
		TParm result = this.update("insertData", parm, conn);
		return result;
	}
	
	/**
	 * 插入一条数据
	 * @param parm
	 * @return
	 */
	public TParm insertData(TParm parm) {
		TParm result = this.update("insertData", parm);
		return result;
	}
	
	/**
	 * 查找数据
	 * @param parm
	 * @return
	 */
	public TParm selectData(TParm parm) {
		TParm result = this.query("selectData", parm);
		return result;
	}
	
	public TParm selectData(String caseNo,String deptCode){
		TParm result=new TParm();
		result.setData("IN_ICU_DATE", new TNull(Timestamp.class));
		result.setData("ICU_INTUBATION_DATE", new TNull(Timestamp.class));
		result.setData("ANTI_DATE", new TNull(Timestamp.class));
		result.setData("ANTI_FLG", "0");
		String antsql=" SELECT MIN(EFF_DATE) AS ORDER_DATE  FROM  ODI_ORDER A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+caseNo+"' AND A.DEPT_CODE='"+deptCode+"' "
				+ " AND A.RX_KIND='UD' AND B.ANTIBIOTIC_CODE IS NOT NULL AND ( (SYSDATE BETWEEN A.EFF_DATE AND A.DC_DATE) OR (A.DC_DATE IS NULL AND SYSDATE > A.EFF_DATE)) ";
		//System.out.println("1111+++"+antsql);
		TParm parm=new TParm(TJDODBTool.getInstance().select(antsql));
		if(parm.getCount()>0&&parm.getTimestamp("ORDER_DATE", 0)!=null){
			result.setData("ANTI_DATE",parm.getTimestamp("ORDER_DATE", 0));
		}
		String antflgsql="SELECT COUNT(CASE_NO) FROM ODI_ORDER A, PHA_BASE B WHERE     A.ORDER_CODE = B.ORDER_CODE AND A.CASE_NO = '"+caseNo+"' AND A.DEPT_CODE='"+deptCode+"' "
				+ "AND(((SYSDATE BETWEEN A.EFF_DATE AND A.DC_DATE) OR(A.DC_DATE IS NULL AND SYSDATE > A.EFF_DATE)) OR(A.RX_KIND = 'ST' AND TO_CHAR( EFF_DATE, 'YYYYMMDD') = TO_CHAR( SYSDATE, 'YYYYMMDD'))) AND B.ANTIBIOTIC_CODE IS NOT NULL";
		//System.out.println("2222+++"+antflgsql);
		parm=new TParm(TJDODBTool.getInstance().select(antflgsql));
		//
		if(parm.getCount()>0&&parm.getTimestamp("ORDER_DATE", 0)!=null){
			result.setData("ANTI_FLG","1");
		}
		String icusql="SELECT  DEPT_CODE  FROM  SYS_DEPT WHERE  DEPT_CODE='"+deptCode+"' AND ICU_TYPE='6103'";
		//System.out.println("3333+++"+icusql);
		parm=new TParm(TJDODBTool.getInstance().select(icusql));
		if(parm.getCount()<=0){
			return result;
		}
		String sql="SELECT TO_DATE(IN_DATE,'YYYYMMDD HH24MISS') AS IN_DATE FROM  ADM_TRANS_LOG WHERE CASE_NO='"+caseNo+"' AND OUT_DATE IS NULL AND IN_DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE ICU_TYPE = '6103') ";
		//System.out.println("4444+++"+sql);
		parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			result.setData("IN_ICU_DATE",parm.getTimestamp("IN_DATE", 0));
		}
		String ordersql=" SELECT MIN(EFF_DATE) AS ORDER_DATE  FROM  ODI_ORDER A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO='"+caseNo+"' AND A.DEPT_CODE='"+deptCode+"' "
				+ " AND A.RX_KIND='UD' AND A.DC_DATE IS NULL AND B.ORD_SUPERVISION='05' AND DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE ICU_TYPE = '6103') ";
		//System.out.println("5555+++"+ordersql);
		parm=new TParm(TJDODBTool.getInstance().select(ordersql));
		if(parm.getCount()>0&&parm.getTimestamp("ORDER_DATE", 0)!=null){
			result.setData("ICU_INTUBATION_DATE",parm.getTimestamp("ORDER_DATE", 0));
		}
		return result;
	}
	
}
