package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title:住院票据汇总表
 * </p>
 * 
 * <p>
 * Description:住院票据汇总表
 * </p>
 * 
 * @author kangy
 * @version 1.0
 */
public class BILIbsUsedControl extends TControl{
	TTable table;
	TParm payParm;
	TParm ibsParm;
	TParm opbParm;
	private String start_date;
	private String end_date;

	/**
	 * 初始化界面
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
		initPage();
	}
	/**
	 * 初始化方法
	 */
	private void initPage(){
		Timestamp today = SystemTool.getInstance().getDate();
		String startDate = today.toString();
		String endDate;
		startDate = startDate.substring(0, 4) + "/" + startDate.substring(5, 7)
				+ "/" + startDate.substring(8, 10) + " 00:00:00";
		endDate = startDate.substring(0, 4) + "/" + startDate.substring(5, 7)
				+ "/" + startDate.substring(8, 10) + " 23:59:59";
		setValue("START_DATE", startDate);
		setValue("END_DATE", endDate);
		setValue("DEPT", "");
		setValue("USER", "");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		 start_date = getValueString("START_DATE");
		 end_date = getValueString("END_DATE");
		if (start_date.equals("") || end_date.equals("")) {
			messageBox("请选择时间段");
			return;
		} else {
			start_date = start_date.substring(0, 4)
					+ start_date.substring(5, 7) + start_date.substring(8, 10)
					+ start_date.substring(11, 13)
					+ start_date.substring(14, 16)
					+ start_date.substring(17, 19);
			end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
					+ end_date.substring(8, 10) + end_date.substring(11, 13)
					+ end_date.substring(14, 16) + end_date.substring(17, 19);
		}
		String dept = getValueString("DEPT");
		String user = getValueString("USER");
		TParm tableparm = new TParm();
		payParm = getPAYparm(start_date, end_date ,dept,user);
		ibsParm = getIBSparm(start_date, end_date ,dept,user);
		opbParm = getOPBparm(start_date, end_date ,dept,user);
		if (payParm == null && ibsParm == null&&opbParm==null) {
			messageBox("无数据");
			table.removeRowAll();
			return;
		}
		if (payParm != null) {
			for (int i = 0; i < payParm.getCount("PRINT_USER"); i++) {
				tableparm
						.addData("RECP_TYPE", payParm.getValue("RECP_TYPE", i));
				tableparm.addData("PRINT_USER", payParm.getValue("PRINT_USER",
						i));
				tableparm.addData("INV_NOS", payParm.getValue("INV_NOS", i));
				tableparm
						.addData("INV_COUNT", payParm.getValue("INV_COUNT", i));
			}
		}
		if (ibsParm != null) {
			for (int i = 0; i < ibsParm.getCount("PRINT_USER"); i++) {
				tableparm
						.addData("RECP_TYPE", ibsParm.getValue("RECP_TYPE", i));
				tableparm.addData("PRINT_USER", ibsParm.getValue("PRINT_USER",
						i));
				tableparm.addData("INV_NOS", ibsParm.getValue("INV_NOS", i));
				tableparm
						.addData("INV_COUNT", ibsParm.getValue("INV_COUNT", i));
			}
		}
		if (opbParm != null) {
			for (int i = 0; i < opbParm.getCount("PRINT_USER"); i++) {
				tableparm
				.addData("RECP_TYPE", opbParm.getValue("RECP_TYPE", i));
				tableparm.addData("PRINT_USER", opbParm.getValue("PRINT_USER",
						i));
				tableparm.addData("INV_NOS", opbParm.getValue("INV_NOS", i));
				tableparm
				.addData("INV_COUNT", opbParm.getValue("INV_COUNT", i));
			}
		}
		table.setParmValue(tableparm);
	}

	/**
	 * 比较票号
	 * 
	 * @param inv_no
	 * @param latestInv_no
	 * @return
	 */
	private boolean compareInvno(String inv_no, String latestInv_no) {
		String inv_no_num = inv_no.replaceAll("[^0-9]", "");// 去非数字
		String inv_no_word = inv_no.replaceAll("[0-9]", "");// 去数字
		String latestInv_no_num = latestInv_no.replaceAll("[^0-9]", "");
		String latestInv_no_word = latestInv_no.replaceAll("[0-9]", "");
		if (inv_no_word.equals(latestInv_no_word)
				&& Long.valueOf(inv_no_num)
						- Long.valueOf(latestInv_no_num) == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取得预交金票号
	 * 
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getPAYparm(String start_date, String end_date, String dept,String user) {
		String sql1 = "SELECT   '预交金' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER ,A.CASHIER_CODE " +
				" FROM BIL_INVRCP A , SYS_OPERATOR B";
		String sql = " WHERE A.PRINT_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
				" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS')" +
				" AND A.RECP_TYPE = 'PAY' " +
				" AND A.PRINT_USER = B.USER_ID "
				+ " AND A.ADM_TYPE='I' ";
		if(!dept.equals("")){
			sql1 += " , SYS_OPERATOR_DEPT C"; 
			sql += " AND B.USER_ID = C.USER_ID AND C.DEPT_CODE = '"+dept+"' ";
		}
		if(!user.equals("")){
			sql+="AND A.PRINT_USER='"+user+"' ";
		}
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		sql = sql1 + sql; 
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return null;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String cashier_code = result.getValue("CASHIER_CODE", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm payParm = new TParm();
		List<String> paylist = new ArrayList<String>();
		paylist.add(result.getValue("INV_NO", 0));
		int paycount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("CASHIER_CODE", i).equals(cashier_code)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += paylist.get(0) + "~"
							+ paylist.get(paylist.size() - 1) + ",";
					paycount += paylist.size();
					paylist = new ArrayList<String>();
				}
			} else {
				inv_nos += paylist.get(0) + "~"
						+ paylist.get(paylist.size() - 1) + ",";
				paycount += paylist.size();
				paylist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				payParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				payParm.addData("PRINT_USER", print_user);
				payParm.addData("CASHIER_CODE", cashier_code);
				payParm.addData("INV_NOS", inv_nos);
				payParm.addData("INV_COUNT", paycount);
				inv_nos = "";
				paycount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			cashier_code = result.getValue("CASHIER_CODE", i);
			paylist.add(result.getValue("INV_NO", i));
		}
		if (paylist.size() > 0) {
			inv_nos += paylist.get(0) + "~" + paylist.get(paylist.size() - 1)
					+ ",";
		}
		paycount += paylist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		payParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		payParm.addData("PRINT_USER", print_user);
		payParm.addData("CASHIER_CODE", cashier_code);
		payParm.addData("INV_NOS", inv_nos);
		payParm.addData("INV_COUNT", paycount);
		TParm payParmF = new TParm();
		TParm cancelParm = getCancelInvByUser("PAY", start_date, end_date);
		for (int i = 0; i < payParm.getCount("RECP_TYPE"); i++) {
			payParmF.addData("RECP_TYPE", payParm.getValue("RECP_TYPE", i));
			payParmF.addData("PRINT_USER", payParm.getValue("PRINT_USER", i));
			payParmF.addData("INV_NOS", payParm.getValue("INV_NOS", i));
			payParmF.addData("INV_COUNT", payParm.getValue("INV_COUNT", i));
			for (int j = 0; j < cancelParm.getCount("CANCEL_USER"); j++) {
				if(payParm.getValue("CASHIER_CODE", i).equals(cancelParm.getValue("CANCEL_USER", j))){
					payParmF.addData("RECP_TYPE", "预交金作废");
					payParmF.addData("PRINT_USER", payParm.getValue("PRINT_USER", i));
					payParmF.addData("INV_NOS", cancelParm.getValue("INV_NO", j).substring(0, cancelParm.getValue("INV_NO", j).length()-1));
					payParmF.addData("INV_COUNT",cancelParm.getValue("INV_NO", j).length() - cancelParm.getValue("INV_NO", j).replaceAll(",","").length());
				}
			}
		}
		return payParmF;
	}

	/**
	 * 取得结算票号
	 * 
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getIBSparm(String start_date, String end_date, String dept,String user) {
		String sql1="SELECT   '结算票据' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER ,A.CASHIER_CODE " +
						" FROM BIL_INVRCP A , SYS_OPERATOR B ";  //modify by huangtt 20150605
		
		String sql = " WHERE A.PRINT_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
						" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS')" +
						" AND A.RECP_TYPE = 'IBS' " +
						" AND A.PRINT_USER = B.USER_ID "+
						" AND A.ADM_TYPE='I' " ;
		
		if(!dept.equals("")){
			sql1 += ", SYS_OPERATOR_DEPT C";  
			sql += " AND B.USER_ID = C.USER_ID AND C.DEPT_CODE = '"+dept+"' ";
		}
		if(!user.equals("")){
			sql+="AND A.PRINT_USER='"+user+"' ";
		}
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		sql = sql1 + sql;  
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return null;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String cashier_code = result.getValue("CASHIER_CODE", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm ibsParm = new TParm();
		List<String> ibslist = new ArrayList<String>();
		ibslist.add(result.getValue("INV_NO", 0));
		int ibscount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("CASHIER_CODE", i).equals(cashier_code)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += ibslist.get(0) + "~"
							+ ibslist.get(ibslist.size() - 1) + ",";
					ibscount += ibslist.size();
					ibslist = new ArrayList<String>();
				}
			} else {
				inv_nos += ibslist.get(0) + "~"
						+ ibslist.get(ibslist.size() - 1) + ",";
				ibscount += ibslist.size();
				ibslist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				ibsParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				ibsParm.addData("PRINT_USER", print_user);
				ibsParm.addData("CASHIER_CODE", cashier_code);
				ibsParm.addData("INV_NOS", inv_nos);
				ibsParm.addData("INV_COUNT", ibscount);
				inv_nos = "";
				ibscount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			cashier_code = result.getValue("CASHIER_CODE", i);
			ibslist.add(result.getValue("INV_NO", i));
		}
		if (ibslist.size() > 0) {
			inv_nos += ibslist.get(0) + "~" + ibslist.get(ibslist.size() - 1)
					+ ",";
		}
		ibscount += ibslist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		ibsParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		ibsParm.addData("PRINT_USER", print_user);
		ibsParm.addData("CASHIER_CODE", cashier_code);
		ibsParm.addData("INV_NOS", inv_nos);
		ibsParm.addData("INV_COUNT", ibscount);
		TParm ibsParmF = new TParm();
		TParm cancelParm = getCancelInvByUser("IBS", start_date, end_date);
		for (int i = 0; i < ibsParm.getCount("RECP_TYPE"); i++) {
			ibsParmF.addData("RECP_TYPE", ibsParm.getValue("RECP_TYPE", i));
			ibsParmF.addData("PRINT_USER", ibsParm.getValue("PRINT_USER", i));
			ibsParmF.addData("INV_NOS", ibsParm.getValue("INV_NOS", i));
			ibsParmF.addData("INV_COUNT", ibsParm.getValue("INV_COUNT", i));
			for (int j = 0; j < cancelParm.getCount("CANCEL_USER"); j++) {
				if(ibsParm.getValue("CASHIER_CODE", i).equals(cancelParm.getValue("CANCEL_USER", j))){
					ibsParmF.addData("RECP_TYPE", "结算票据作废");
					ibsParmF.addData("PRINT_USER", ibsParm.getValue("PRINT_USER", i));
					ibsParmF.addData("INV_NOS", cancelParm.getValue("INV_NO", j).substring(0, cancelParm.getValue("INV_NO", j).length()-1));
					ibsParmF.addData("INV_COUNT", cancelParm.getValue("INV_NO", j).length() - cancelParm.getValue("INV_NO", j).replaceAll(",","").length());
				}
			}
		}
		return ibsParmF;
	}
	/**
	 * 取得体检票号
	 * 
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getOPBparm(String start_date, String end_date, String dept,String user) {
		String sql1="SELECT   '体检票据' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER ,A.CASHIER_CODE " +
				" FROM BIL_INVRCP A , SYS_OPERATOR B ";
		
		String sql = " WHERE A.PRINT_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
				" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS')" +
				" AND A.RECP_TYPE = 'OPB' " +
				" AND A.PRINT_USER = B.USER_ID "+
				" AND A.ADM_TYPE='H' " ;
		
		if(!dept.equals("")){
			sql1 += ", SYS_OPERATOR_DEPT C";  
			sql += " AND B.USER_ID = C.USER_ID AND C.DEPT_CODE = '"+dept+"' ";
		}
		if(!user.equals("")){
			sql+="AND A.PRINT_USER='"+user+"' ";
		}
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		sql = sql1 + sql;  
//		System.out.println("取得体检票号::" + sql );
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return null;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String cashier_code = result.getValue("CASHIER_CODE", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm opbParm = new TParm();
		List<String> opblist = new ArrayList<String>();
		opblist.add(result.getValue("INV_NO", 0));
		int opbcount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("CASHIER_CODE", i).equals(cashier_code)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += opblist.get(0) + "~"
							+ opblist.get(opblist.size() - 1) + ",";
					opbcount += opblist.size();
					opblist = new ArrayList<String>();
				}
			} else {
				inv_nos += opblist.get(0) + "~"
						+ opblist.get(opblist.size() - 1) + ",";
				opbcount += opblist.size();
				opblist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				opbParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				opbParm.addData("PRINT_USER", print_user);
				opbParm.addData("CASHIER_CODE", cashier_code);
				opbParm.addData("INV_NOS", inv_nos);
				opbParm.addData("INV_COUNT", opbcount);
				inv_nos = "";
				opbcount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			cashier_code = result.getValue("CASHIER_CODE", i);
			opblist.add(result.getValue("INV_NO", i));
		}
		if (opblist.size() > 0) {
			inv_nos += opblist.get(0) + "~" + opblist.get(opblist.size() - 1)
					+ ",";
		}
		opbcount += opblist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		opbParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		opbParm.addData("PRINT_USER", print_user);
		opbParm.addData("CASHIER_CODE", cashier_code);
		opbParm.addData("INV_NOS", inv_nos);
		opbParm.addData("INV_COUNT", opbcount);
		TParm opbParmF = new TParm();
		TParm cancelParm = getCancelInvByUser("OPB", start_date, end_date);
		for (int i = 0; i < opbParm.getCount("RECP_TYPE"); i++) {
			opbParmF.addData("RECP_TYPE", opbParm.getValue("RECP_TYPE", i));
			opbParmF.addData("PRINT_USER", opbParm.getValue("PRINT_USER", i));
			opbParmF.addData("INV_NOS", opbParm.getValue("INV_NOS", i));
			opbParmF.addData("INV_COUNT", opbParm.getValue("INV_COUNT", i));
			for (int j = 0; j < cancelParm.getCount("CANCEL_USER"); j++) {
				if(opbParm.getValue("CASHIER_CODE", i).equals(cancelParm.getValue("CANCEL_USER", j))){
					opbParmF.addData("RECP_TYPE", "体检票据作废");
					opbParmF.addData("PRINT_USER", opbParm.getValue("PRINT_USER", i));
					opbParmF.addData("INV_NOS", cancelParm.getValue("INV_NO", j).substring(0, cancelParm.getValue("INV_NO", j).length()-1));
					opbParmF.addData("INV_COUNT", cancelParm.getValue("INV_NO", j).length() - cancelParm.getValue("INV_NO", j).replaceAll(",","").length());
				}
			}
		}
		return opbParmF;
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		initPage();
		start_date="";
		end_date="";
		payParm = null;
		ibsParm = null;
		opbParm=null;
		table.removeRowAll();
	}
	
	/**
	 * 取得作废票据
	 * @param recp_type
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getCancelInv(String recp_type ,String start_date, String end_date,String adm_type ){
		String deptSql = "";
		String dept = "";
		if(!getValueString("DEPT").equals("")){
			deptSql = " AND A.PRINT_USER = B.USER_ID AND B.DEPT_CODE = '"+getValueString("DEPT")+"' ";
			dept = " ,SYS_OPERATOR_DEPT B ";
		}
		String sql = 
			"SELECT A.CANCEL_USER, C.USER_NAME, A.INV_NO" +
			" FROM BIL_INVRCP A, SYS_OPERATOR C " + dept +
			" WHERE A.CANCEL_FLG <> 0" +
			" AND A.RECP_TYPE = '" + recp_type + "'" +
			" AND A.CANCEL_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
			" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS') "+
		    " AND A.ADM_TYPE='"+adm_type+"' " +
			deptSql +
			" AND A.CANCEL_USER = C.USER_ID" +
			" ORDER BY A.CANCEL_USER, A.INV_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<0){
			return null;
		}
		int jump = 0;
		String inv = "";
		TParm invParm = new TParm();
		String cancel_user = result.getValue("CANCEL_USER", 0);
		for (int i = 0; i < result.getCount(); i++) {
			if(jump != 10){
				inv += result.getValue("INV_NO", i) + ",";
				jump++;
			}else{
				jump = 0;
				invParm.addData("INV_NO", inv);
				inv = result.getValue("INV_NO", i) + ",";
			}
		}
		return invParm;
	}
	
	/**
	 * 取得预交金作废票号
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getPAYCancelInv(String start_date , String end_date){
		TParm cancelParm = getCancelInv("PAY", start_date, end_date,"I");
		if(cancelParm == null){
			cancelParm = new TParm();
		}
		cancelParm.setCount(cancelParm.getCount("INV_NO"));
		cancelParm.addData("SYSTEM", "COLUMNS", "INV_NO");
		return cancelParm;
	}
	
	/**
	 * 取得结算作废票号
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getIBSCancelInv(String start_date , String end_date){
		TParm cancelParm = getCancelInv("IBS", start_date, end_date,"I");
		if(cancelParm == null){
			cancelParm = new TParm();
		}
		cancelParm.setCount(cancelParm.getCount("INV_NO"));
		cancelParm.addData("SYSTEM", "COLUMNS", "INV_NO");
		return cancelParm;
	}
	/**
	 * 打印
	 */
	public void onPrint(){
		if(table.getParmValue() == null){
			messageBox("无打印数据");
			return;
		}
		String start_date = getValueString("START_DATE");
		String end_date = getValueString("END_DATE");
		String printStartDate = "";
		String printEndDate = "";
		String printDate="";
		if (start_date.equals("") || end_date.equals("")) {
			messageBox("请选择时间段");
			return;
		} else {
			printDate=SystemTool.getInstance().getDate().toString().substring(0,19);
			printStartDate = start_date.substring(0, 19);
			printEndDate = end_date.substring(0, 19);
			start_date = start_date.substring(0, 4)
					+ start_date.substring(5, 7) + start_date.substring(8, 10)
					+ start_date.substring(11, 13)
					+ start_date.substring(14, 16)
					+ start_date.substring(17, 19);
			end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
					+ end_date.substring(8, 10) + end_date.substring(11, 13)
					+ end_date.substring(14, 16) + end_date.substring(17, 19);
		}
		TParm printParm = new TParm();
		if(payParm!=null){
		payParm.setCount(payParm.getCount("PRINT_USER"));
		payParm.addData("SYSTEM", "COLUMNS", "RECP_TYPE");
		payParm.addData("SYSTEM", "COLUMNS", "PRINT_USER");
		payParm.addData("SYSTEM", "COLUMNS", "INV_NOS");
		payParm.addData("SYSTEM", "COLUMNS", "INV_COUNT");
		printParm.setData("payTable", payParm.getData());}
		if(ibsParm!=null){
		ibsParm.setCount(ibsParm.getCount("PRINT_USER"));
		ibsParm.addData("SYSTEM", "COLUMNS", "RECP_TYPE");
		ibsParm.addData("SYSTEM", "COLUMNS", "PRINT_USER");
		ibsParm.addData("SYSTEM", "COLUMNS", "INV_NOS");
		ibsParm.addData("SYSTEM", "COLUMNS", "INV_COUNT");
		printParm.setData("ibsTable", ibsParm.getData());}
		if(opbParm!=null){
		opbParm.setCount(opbParm.getCount("PRINT_USER"));
		opbParm.addData("SYSTEM", "COLUMNS", "RECP_TYPE");
		opbParm.addData("SYSTEM", "COLUMNS", "PRINT_USER");
		opbParm.addData("SYSTEM", "COLUMNS", "INV_NOS");
		opbParm.addData("SYSTEM", "COLUMNS", "INV_COUNT");
		printParm.setData("opbTable", opbParm.getData());}
		
		
		
		printParm.setData("PRINT_USER","TEXT", Operator.getName());
		printParm.setData("PRINT_START","TEXT", "开始时间: "+printStartDate);
		printParm.setData("PRINT_END","TEXT", "结束时间: "+printEndDate);
		printParm.setData("PRINT_DATE","TEXT", "制表时间: "+printDate);
		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILIbsUsed.jhw",printParm);
	}
	
	/**
	 * 取得预交金作废票号byUser
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getPAYCancelInvByUser(String start_date , String end_date){
		TParm cancelParm = getCancelInv("PAY", start_date, end_date,"I");
		if(cancelParm == null){
			cancelParm = new TParm();
		}
		cancelParm.setCount(cancelParm.getCount("INV_NO"));
		cancelParm.addData("SYSTEM", "COLUMNS", "INV_NO");
		return cancelParm;
	}
	
	/**
	 * 取得结算作废票号byUser
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getIBSCancelInvByUser(String start_date , String end_date){
		TParm cancelParm = getCancelInv("IBS", start_date, end_date,"I");
		if(cancelParm == null){
			cancelParm = new TParm();
		}
		cancelParm.setCount(cancelParm.getCount("INV_NO"));
		cancelParm.addData("SYSTEM", "COLUMNS", "INV_NO");
		return cancelParm;
	}
	/**
	 * 取得体检作废票号byUser
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getOPBCancelInvByUser(String start_date , String end_date){
		TParm cancelParm = getCancelInv("OPB", start_date, end_date,"H");
		if(cancelParm == null){
			cancelParm = new TParm();
		}
		cancelParm.setCount(cancelParm.getCount("INV_NO"));
		cancelParm.addData("SYSTEM", "COLUMNS", "INV_NO");
		return cancelParm;
	}
	
	/**
	 * 取得作废票据byUser
	 * @param recp_type
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getCancelInvByUser(String recp_type ,String start_date, String end_date){
		String cancle=" (A.CANCEL_FLG <> 0 OR A.CANCEL_FLG IS NULL) ";
		String ibs="";
		String deptSql = "";
		String dept = "";
		if(!getValueString("DEPT").equals("")){
			deptSql = " AND A.PRINT_USER = B.USER_ID AND B.DEPT_CODE = '"+getValueString("DEPT")+"' ";
			dept = " ,SYS_OPERATOR_DEPT B ";
		}
	if("PAY".equals(recp_type)){
		 cancle=" (A.CANCEL_FLG  NOT IN('0','1') OR A.CANCEL_FLG IS NULL ) ";
		}
	if("IBS".equals(recp_type)){//add by kangy 20170927   住院票据隔日作废不显示
		ibs=" AND TRUNC((CASE  WHEN A.CANCEL_DATE IS NULL THEN A.OPT_DATE "+
             "  WHEN A.CANCEL_DATE IS NOT NULL THEN A.CANCEL_DATE "+
            "END)-A.PRINT_DATE)<1 ";
	}
	if("OPB".equals(recp_type)){// == 20180210 zhanglei #6243 住院票据汇总表修改 
		cancle = "A.CANCEL_FLG <> 0";
	}
		String sql = 
			"SELECT CASE WHEN A.CANCEL_USER IS NULL THEN A.OPT_USER WHEN A.CANCEL_USER IS NOT NULL THEN A.CANCEL_USER END  CANCEL_USER, C.USER_NAME, A.INV_NO" +
			" FROM BIL_INVRCP A, SYS_OPERATOR C " + dept +
			" WHERE " + cancle+
			" AND A.RECP_TYPE = '" + recp_type + "'" +
			" AND (CASE  WHEN A.CANCEL_DATE IS NULL THEN A.OPT_DATE "+
             "  WHEN A.CANCEL_DATE IS NOT NULL THEN A.CANCEL_DATE "+
            "END) BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
			" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS') " +
			deptSql +
			" AND CASE  WHEN A.CANCEL_USER IS NULL THEN A.OPT_USER "+
            "  WHEN A.CANCEL_USER IS NOT NULL THEN A.CANCEL_USER "+
            " END = C.USER_ID" + ibs;
			if(!"IBS".equals(recp_type)){
				sql+=" ORDER BY A.CANCEL_USER, A.INV_NO";
			}
			
		String sql1="SELECT A.CANCEL_USER, C.USER_NAME, A.INV_NO" +
				" FROM BIL_INVRCP A, SYS_OPERATOR C " + dept +
				" WHERE " + cancle+
				" AND A.RECP_TYPE = '" + recp_type + "'" +
				" AND A.CANCEL_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
				" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS') " +
				deptSql +
				" AND A.CANCEL_USER = C.USER_ID"
				+ " AND A.CANCEL_FLG='3' "
				+ " AND  TRUNC(A.CANCEL_DATE-A.PRINT_DATE)>=1";
		if("IBS".equals(recp_type)){
			
			sql="SELECT * FROM("+sql1+" UNION ALL "+sql+") ORDER BY CANCEL_USER, INV_NO ";
		}
//		System.out.println("取得体检作废票号：："+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<0){
			return new TParm();
		}
		String inv = "";
		TParm invParm = new TParm();
		String cancel_user = result.getValue("CANCEL_USER", 0);
		for (int i = 0; i < result.getCount(); i++) {
			if(result.getValue("CANCEL_USER", i).equals(cancel_user)){
				inv += result.getValue("INV_NO", i) + ",";
			}else{
				invParm.addData("CANCEL_USER", cancel_user);
				invParm.addData("INV_NO", inv);
				inv = result.getValue("INV_NO", i) + ",";
			}
			cancel_user = result.getValue("CANCEL_USER", i);
		}
		invParm.addData("CANCEL_USER", cancel_user);
		invParm.addData("INV_NO", inv);
		return invParm;
	}
	 /**
     * 汇出Excel 
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        
        ExportExcelUtil.getInstance().exportExcel(table, start_date+"到"+end_date+"住院票据汇总表");
    }
}
