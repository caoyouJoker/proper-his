package com.javahis.ui.bms;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:个人血品费用查询
 *
 * <p>Description: 个人血品费用查询
 *
 * <p>Copyright: 个人血品费用查询
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx   time 2013-04-27
 * @version 4.0
 */
public class BMSQueryFeeControl extends TControl{
	private TTable table ;
	private String caseNo ;
	private String patName ;
	
	/**
	 * 初始化
	 */

	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
		this.onQuery();

	
	}
	/**
	 * 初始化界面
	 */
	public void onInitPage(){  
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj != null) {
			initParm = (TParm) obj;
		}
		caseNo = initParm.getValue("CASE_NO").toString() ;
		patName = initParm.getValue("PAT_NAME").toString() ;
		table = (TTable)this.getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		double money = 0.00 ;
		String sql = "SELECT A.BILL_DATE,A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC||CASE WHEN B.SPECIFICATION IS NOT NULL  " +
				" THEN '('||B.SPECIFICATION||')' ELSE '' END AS ORDER_DESC,A.OWN_FLG,A.DOSAGE_QTY,A.OWN_PRICE, " +
				" A.TOT_AMT,A.OPT_DATE,C.USER_NAME,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO,A.ORDERSET_CODE,ORDERSET_GROUP_NO, A.INDV_FLG," +
				" D.COST_CENTER_ABS_DESC " +
				" FROM IBS_ORDD A,SYS_FEE B,SYS_OPERATOR C,SYS_COST_CENTER D " +
				" WHERE A.ORDER_CODE =B.ORDER_CODE " +
				" AND A.OPT_USER = C.USER_ID " +
				" AND A.EXE_DEPT_CODE = D.COST_CENTER_CODE " +
				" AND A.CASE_NO = '"+caseNo+"' " +
				" AND A.REXP_CODE = '02A' " +
				" ORDER BY A.BILL_DATE,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO" ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		if(parm.getCount()<=0){
			this.messageBox("查无数据") ;
			return ;
		}
		for(int i=0;i<parm.getCount();i++){
			money +=parm.getDouble("TOT_AMT", i) ;
		}
		this.setValue("NAME", patName) ;
		this.setValue("ALL_MONEY", money) ;
		table.setParmValue(parm) ;

	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("") ;
		table.removeAll() ;
	}
	
	
}
