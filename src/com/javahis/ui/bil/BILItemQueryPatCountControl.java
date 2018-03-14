package com.javahis.ui.bil;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 2013.05.10
 * @version 4.0
 */
public class BILItemQueryPatCountControl extends TControl{
	private TParm parm ;
	private TTable table ;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit() ;
		//======接受回参值
		parm = (TParm)this.getParameter() ;
		table = (TTable)this.getComponent("TABLE") ;
		this.onQuery() ;
	}
	/**
	 * 查询
	 */
	public void onQuery(){
		String type = parm.getValue("TYPE") ; //=======门键住别
		String orderCode =parm.getValue("ORDER_CODE");
		String deptCode = parm.getValue("DEPT_CODE") ;
		String sDate =parm.getValue("SDATE") ;
		String eDate = parm.getValue("EDATE") ;
		String sql = "" ;
		if(type.equals("O")){
		 sql = "SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.BILL_DATE FROM  OPD_ORDER A,SYS_PATINFO B" +
			"   WHERE  A.MR_NO = B.MR_NO" +
			"   AND A.ORDER_CODE = '"+orderCode+"'  " +
			"   AND A.EXEC_DEPT_CODE = '"+deptCode+"'" +
			"   AND A.BILL_DATE > TO_DATE('"+sDate+"', 'YYYYMMDDHH24MISS')" +
			"   AND A.BILL_DATE < TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		}
		else if(type.equals("H")){
			sql = "SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.ORDER_DATE  AS BILL_DATE FROM HRM_ORDER A,SYS_PATINFO B" +
			"   WHERE  A.MR_NO = B.MR_NO" +
			"   AND A.ORDER_CODE = '"+orderCode+"'  " +
			"   AND A.EXEC_DEPT_CODE = '"+deptCode+"'" +
			"   AND A.ORDER_DATE > TO_DATE('"+sDate+"', 'YYYYMMDDHH24MISS')" +
			"   AND A.ORDER_DATE < TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		}
		else {
			sql="SELECT C.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.BILL_DATE FROM  IBS_ORDD A,SYS_PATINFO B,IBS_ORDM C " +
					"  WHERE A.CASE_NO =C.CASE_NO" +
					"  AND A.CASE_NO_SEQ=C.CASE_NO_SEQ" +
					"  AND  C.MR_NO = B.MR_NO" +
					"  AND A.order_code = '"+orderCode+"'  " +
					"  AND A.EXE_DEPT_CODE = '"+deptCode+"'" +
					"  AND A.BILL_DATE > TO_DATE('"+sDate+"', 'YYYYMMDDHH24MISS')" +
					"  AND A.BILL_DATE < TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS')" ;
		}
		System.out.println("sql============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(result.getCount()<0){
			this.messageBox("查无数据") ;
			return ;
		}
		for(int i=0;i<result.getCount();i++){
			String[] AGE = StringTool.CountAgeByTimestamp(
					result.getTimestamp("BIRTH_DATE", i),
					SystemTool.getInstance().getDate());
			result.addData("AGE", AGE[0]+"岁") ;
		}
		this.setValue("COUNT", result.getCount()) ;
		table.setParmValue(result) ;
	}
}
