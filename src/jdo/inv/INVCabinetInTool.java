package jdo.inv;

import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title:智能柜入柜
 * </p>
 * 
 * <p>
 * Description: 智能柜入柜
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liuzhen 20121101
 * @version 1.0
 */
public class INVCabinetInTool extends TJDODBTool {
	
	/**实例*/
	private static INVCabinetInTool instanceObject;

	/**得到实例*/
	public static INVCabinetInTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INVCabinetInTool();
		return instanceObject;
	}
	
	/**根据IP查询智能柜*/
	public TParm queryCabinet(TParm parm) {
		
		String cabnetIP = "";
		if (null != parm && null != parm.getData("CABINET_IP")) {
			cabnetIP = " AND CABINET_IP="+ parm.getData("CABINET_IP") + " ";
		}
	
		String sql = "SELECT " +
						"CABINET_ID,CABINET_DESC," +
						"CABINET_IP,GUARD_IP," +
						"RFID_IP,ORG_CODE," +
						"DESCRIPTION,REGION_CODE " +
					"FROM INV_CABINET " +
					"WHERE 1=1 "+ cabnetIP +" " +
					"ORDER BY CABINET_ID";
		
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	
	
	/**根据组织机构查询-验收单据*/
	public TParm queryOrderM(TParm parm) {
		
		String orgCode = "";
		if (null != parm && null != parm.getData("ORG_CODE")) {
			orgCode = " AND VERIFYIN_DEPT="+ parm.getData("ORG_CODE") + " ";
		}
		
		String sql = "SELECT  " +
							"VERIFYIN_NO," +
							"VERIFYIN_DATE," +
							"VERIFYIN_USER," +
							"VERIFYIN_DEPT," +
							"CHECK_FLG " +
						"FROM INV_VERIFYINM " + 
						"WHERE CHECK_FLG='N'" +
						orgCode+
						" ORDER BY VERIFYIN_NO";
		
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	
	/**根据单号查询-单据下具体药品*/
	public TParm queryOrderD(TParm parm) {
		
		String orderNo = "";
		if (null != parm && null != parm.getData("ORDER_NO")) {
			orderNo = " AND A.VERIFYIN_NO="+ parm.getData("ORDER_NO") + " ";
		}
		
		String seqFlg = "";
		if (null != parm && null != parm.getData("IS_SEQ")) {
			orderNo = " AND B.SEQMAN_FLG ="+ parm.getData("IS_SEQ") + " ";
		}
		
		String sql = "SELECT " +
						"A.VERIFYIN_NO," +
						"A.SEQ_NO," +
						"A.INV_CODE," +
						"A.IN_QTY+A.GIFT_QTY AS TOTAL_QTY," +
						"A.ACTUAL_QTY," +
						"A.STOCK_UNIT," +
						"A.BATCH_NO," +
						"A.VALID_DATE," +
						"A.REN_CODE," +
						"B.SEQMAN_FLG" +
					"FROM INV_VERIFYIND A," +
						"INV_BASE B " + 
					"WHERE A.INV_CODE=B.INV_CODE " +
						orderNo	+
						seqFlg	+
				" ORDER BY SEQ_NO";
		
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	
	
	/**保存智能柜日志*/
	public boolean insertLog(TParm parm) {

		String sql = "INSERT INTO INV_CABINET_LOG"
						  + "(CABINET_ID," +
						  		"LOG_TIME," +
						  		"TASK_TYPE," +
						  		"TASK_NO," +
						  		"EVENT_TYPE," +
						  		"GUARD_ID," +
						  		"OPT_USER,OPT_DATE,OPT_TERM)"
				+ "VALUES ("+parm.getData("CABINET_ID")+","
				+			parm.getData("LOG_TIME")
				+			parm.getData("TASK_TYPE")
				+			parm.getData("TASK_NO")
				+			parm.getData("EVENT_TYPE")
				+			parm.getData("GUARD_ID")
				+			parm.getData("OPT_USER")
				+			parm.getData("OPT_DATE")
				+			parm.getData("OPT_TERM")
				+")";
		 
		TParm result= new TParm(update(sql));
		if(result.getErrCode()<0)
			return false;
		return true;
		
	}
	
	
	/**根据单号查询-单据下具体药品*/
	public TParm queryOrderDD(TParm parm) {
		
		String orderNo = "";
		if (null != parm && null != parm.getData("ORDER_NO")) {
			orderNo = " AND A.VERIFYIN_NO="+ parm.getData("ORDER_NO") + " ";
		}
		
		String sql = "SELECT " +
						"VERIFYIN_NO," +
						"SEQ_NO," +
						"DDSEQ_NO," +
						"RFID,INV_CODE," +
						"INVSEQ_NO," +
						"BATCH_SEQ," +
						"BATCH_NO," +
						"VALID_DATE," +
						"STOCK_UNIT," +
						"UNIT_PRICE," +
						"FLG " +
					"FROM INV_VERIFYINDD " + 
					"WHERE 1=1 " +
					orderNo+
					" ORDER BY VERIFYIN_NO,SEQ_NO,DDSEQ_NO";
		
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	
	
	/***修改DD表，标记rfid状态，1已经入库，0没有入库*/
	public boolean updateDD(List<String> list){
		
		String inCondition ="";
		
		for(int i=0; i<list.size(); i++){			
			inCondition = inCondition + list.get(i);
			if(i != list.size()-1){
				inCondition = inCondition + ",";		
				}
		}
		
		String sql = "UPDATE INV_VERIFYINDD SET FLG=1 WHERE RFID IN (" + inCondition + ")";
		
		TParm result = new TParm(this.update(sql));
		
		if (result.getErrCode() < 0) return false;
		
		return true;
		
	}
	
	/***修改D表，标记该单子是否完成1为完成*/
	public boolean updateD(TParm parm){
		
		String sql ="UPDATE INV_VERIFYIND " +
						"SET ACTUAL_QTY=SELECT COUNT(*) " +
											"FROM INV_VERIFYINDD A,INV_VERIFYIND B " +
											"WHERE " +
												"VERIFYIN_NO='"+parm.getValue("ORDER_NO")+"' " +
												"AND A.SEQ_NO=B.SEQ_NO" +
					"WHERE VERIFYIN_NO='" + parm.getValue("ORDER_NO") +"'";
		
		TParm result = new TParm(this.update(sql));
		
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/***修改M表，标记该单子是否完成1为完成*/
	public boolean updateM(TParm parm){
		
		String sql ="UPDATE INV_VERIFYINM " +
						"SET ACTUAL_QTY='"+parm.getValue("CHECK_FLG")+"' " +
					"WHERE VERIFYIN_NO='" + parm.getValue("ORDER_NO") +"'";
		
		TParm result = new TParm(this.update(sql));
		
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	

	/**初始化方法*/
	public TParm onInit(TParm parm) {
		return null;
	}


	/**查询方法*/
	public TParm onQuery(TParm parm) {
		
		String condition1 = "";
		if (parm.getData("EMPLOYEE_CODE").toString().length() != 0) {
			condition1 = " AND EMPLOYEE_CODE="+ parm.getData("EMPLOYEE_CODE") + " ";
		}
		
		String condition2 = "";
		if (null != parm.getData("PAYMONTH") && parm.getData("PAYMONTH").toString().length() != 0) {
			
			String str = parm.getData("PAYMONTH").toString();
			String conStr = str.substring(0, 4) + str.substring(5, 7);
			
			condition2 = " AND PAYMONTH='"+ conStr + "' ";
		}
				
		String sql = "SELECT " +
						"EMPLOYEE_CODE,PAYMONTH,WAGEITEM_CODE,WAGE_AMOUNT,"+
						"OPT_USER,OPT_DATE,OPT_TERM " +
						"FROM HRS_WAGE " + 
						"WHERE 1=1 "+
						condition1+
						condition2+
						" ORDER BY EMPLOYEE_CODE";
		
		TParm result = new TParm(this.select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	
	/**
	 * 更新前查询方法
	 * */
	public TParm onQueryByUpdate(TParm parm){
		String str = parm.getData("PAYMONTH").toString();
		String conStr = str.substring(0, 4) + str.substring(5, 7);
		
		String sql = "SELECT EMPLOYEE_CODE"
			 + " FROM HRS_WAGE" 
			 + " WHERE EMPLOYEE_CODE='"+parm.getData("EMPLOYEE_CODE")+"'"
			 + " AND PAYMONTH='"+conStr+"' AND WAGEITEM_CODE='"+parm.getData("WAGEITEM_CODE")+"'";
		
		TParm result=new TParm(this.select(sql));
		
		return result;
	}
	
	/**保存方法*/
	public boolean onSave(TParm parm) {
		String str = parm.getData("PAYMONTH").toString();
		String conStr = str.substring(0, 4) + str.substring(5, 7);
				
		String sql = "INSERT INTO HRS_WAGE"
				+ 		"(EMPLOYEE_CODE,PAYMONTH,WAGEITEM_CODE,WAGE_AMOUNT,"+
						"OPT_USER,OPT_DATE,OPT_TERM)"
				+    " VALUES('" 
				+ parm.getData("EMPLOYEE_CODE") + "'," + "'"
				+ conStr + "'," + "'"
				+ parm.getData("WAGEITEM_CODE") + "'," 
				+ parm.getData("WAGE_AMOUNT") + ","  + "'"
				
				+ parm.getData("OPT_USER")
				+ "',SYSDATE,'" 
				+ parm.getData("OPT_TERM") + "')";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}

	
	/**修改方法*/
	public boolean onUpdate(TParm parm) {
		
		String str = parm.getData("PAYMONTH").toString();
		String conStr = str.substring(0, 4) + str.substring(5, 7);
					
		String sql = "UPDATE HRS_WAGE " +
						 "SET " 
				+ 			"WAGE_AMOUNT="+ parm.getData("WAGE_AMOUNT") + "," 
	
				+ 			"OPT_USER='"+ parm.getData("OPT_USER") + "'," 
				+ 			"OPT_DATE=SYSDATE,"
				+ 			"OPT_TERM='" + parm.getData("OPT_TERM") + "' "
				
				+ " WHERE EMPLOYEE_CODE='"+ parm.getData("EMPLOYEE_CODE") +"' " +
						"AND PAYMONTH='"+conStr+"' " +
						"AND WAGEITEM_CODE='"+ parm.getData("WAGEITEM_CODE") +"' ";
		
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) 
			return false;
		
		return true;
	}


	/**删除操作*/
	public boolean onDelete(TParm parm) {
		
		String str = parm.getData("PAYMONTH").toString();
		String conStr = str.substring(0, 4) + str.substring(4, 6);
		
		String sql = "DELETE HRS_WAGE " + 
					"WHERE EMPLOYEE_CODE='"+parm.getData("EMPLOYEE_CODE")+"' " +
						"AND PAYMONTH='"+ conStr +"' AND WAGEITEM_CODE='"+parm.getData("WAGEITEM_CODE")+"'";
		
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
}
