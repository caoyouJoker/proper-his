package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

public class EKTBilpayInfoControl extends TControl{
	/**
	 * <p>
	 * Title:预交金统计报表
	 * </p>
	 * <p>
	 * Description:预交金统计报表
	 * </p>
	 * <p>
	 * Company:Javahis
	 * </p>
	 * 
	 * @author kangy
	 * @version 1.0
	 */
	TTable table;
	TParm ektParm;
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
		String endDate = today.toString();
		startDate = startDate.substring(0, 4) + "/" + startDate.substring(5, 7)
				+ "/" + startDate.substring(8, 10) + " 00:00:00";
		endDate = endDate.substring(0, 4) + "/" + endDate.substring(5, 7)
		+ "/" + endDate.substring(8, 10) + " 23:59:59";
		setValue("START_DATE", startDate);
		setValue("END_DATE", endDate);
		setValue("DEPT", Operator.getDept());
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
		TParm tableparm = new TParm();
		ektParm=null;
		 ektParm = getEKTparm(start_date, end_date ,dept);
		if (ektParm == null) {
			messageBox("无数据");
			table.removeRowAll();
			return;
		}
		if (ektParm != null) {
			for (int i = 0; i < ektParm.getCount("PRINT_USER"); i++) {
				tableparm
						.addData("RECP_TYPE", ektParm.getValue("RECP_TYPE", i));
				tableparm.addData("PRINT_USER", ektParm.getValue("PRINT_USER",
						i));
				tableparm.addData("INV_NOS", ektParm.getValue("INV_NOS", i));
				tableparm
						.addData("INV_COUNT", ektParm.getValue("INV_COUNT", i));
			}
		}
		table.setParmValue(tableparm);
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		initPage();
		start_date="";
		end_date="";
		ektParm = null;
		table.removeRowAll();
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
	 * 取得挂号票号
	 * 
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getEKTparm(String start_date, String end_date, String dept) {
		String sql1 = "SELECT   '医疗卡' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER ,A.CASHIER_CODE " +
				" FROM BIL_INVRCP A , SYS_OPERATOR B";
		String sql = " WHERE A.PRINT_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
				" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS')" +
				" AND A.RECP_TYPE = 'EKT' " +
				" AND A.PRINT_USER = B.USER_ID ";
		if(!dept.equals("")){
			sql1 += " , SYS_OPERATOR_DEPT C"; 
			sql += " AND B.USER_ID = C.USER_ID AND C.DEPT_CODE = '"+dept+"' ";
		}
		//sql += " AND LENGTH (A.INV_NO) < 12";
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		sql = sql1 + sql; 
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return null;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String cashier_code = result.getValue("CASHIER_CODE", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm ektParm = new TParm();
		List<String> reglist = new ArrayList<String>();
		reglist.add(result.getValue("INV_NO", 0));
		int regcount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("CASHIER_CODE", i).equals(cashier_code)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += reglist.get(0) + "~"
							+ reglist.get(reglist.size() - 1) + ",";
					regcount += reglist.size();
					reglist = new ArrayList<String>();
				}
			} else {
				inv_nos += reglist.get(0) + "~"
						+ reglist.get(reglist.size() - 1) + ",";
				regcount += reglist.size();
				reglist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				ektParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				ektParm.addData("PRINT_USER", print_user);
				ektParm.addData("CASHIER_CODE", cashier_code);
				ektParm.addData("INV_NOS", inv_nos);
				ektParm.addData("INV_COUNT", regcount);
				inv_nos = "";
				regcount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			cashier_code = result.getValue("CASHIER_CODE", i);
			reglist.add(result.getValue("INV_NO", i));
		}
		if (reglist.size() > 0) {
			inv_nos += reglist.get(0) + "~" + reglist.get(reglist.size() - 1)
					+ ",";
		}
		regcount += reglist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		ektParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		ektParm.addData("PRINT_USER", print_user);
		ektParm.addData("CASHIER_CODE", cashier_code);
		ektParm.addData("INV_NOS", inv_nos);
		ektParm.addData("INV_COUNT", regcount);
		TParm regParmF = new TParm();
		TParm cancelParm = getCancelInvByUser("EKT", start_date, end_date);
		for (int i = 0; i < ektParm.getCount("RECP_TYPE"); i++) {
			regParmF.addData("RECP_TYPE", ektParm.getValue("RECP_TYPE", i));
			regParmF.addData("PRINT_USER", ektParm.getValue("PRINT_USER", i));
			regParmF.addData("INV_NOS", ektParm.getValue("INV_NOS", i));
			regParmF.addData("INV_COUNT", ektParm.getValue("INV_COUNT", i));
			for (int j = 0; j < cancelParm.getCount("CANCEL_USER"); j++) {
				if(ektParm.getValue("CASHIER_CODE", i).equals(cancelParm.getValue("CANCEL_USER", j))){
					regParmF.addData("RECP_TYPE", "医疗卡作废");
					regParmF.addData("PRINT_USER", ektParm.getValue("PRINT_USER", i));
					regParmF.addData("INV_NOS", cancelParm.getValue("INV_NO", j).substring(0, cancelParm.getValue("INV_NO", j).length()-1));
					regParmF.addData("INV_COUNT", "");
				}
			}
		}
		return regParmF;
	}
	
	/**
	 * 取得作废票据
	 * @param recp_type
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getCancelInv(String recp_type ,String start_date, String end_date){
		String deptSql = "";
		String dept = "";
		if(!getValueString("DEPT").equals("")){
			deptSql = " AND A.PRINT_USER = B.USER_ID AND B.DEPT_CODE = '"+getValueString("DEPT")+"' ";
			dept = " ,SYS_OPERATOR_DEPT B ";
		}
		String sql = 
			"SELECT A.CANCEL_USER, C.USER_NAME, A.INV_NO" +
			" FROM BIL_INVRCP A, SYS_OPERATOR C " + dept +
			" WHERE A.CANCEL_FLG ='3' " +
			" AND A.RECP_TYPE = '" + recp_type + "'" +
			" AND A.CANCEL_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
			" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS') " +
			deptSql +
			//" AND LENGTH (A.INV_NO) < 12" +
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
	 * 取得挂号作废票号
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getREGCancelInv(String start_date , String end_date){
		TParm cancelParm = getCancelInv("EKT", start_date, end_date);
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
		if (start_date.equals("") || end_date.equals("")) {
			messageBox("请选择时间段");
			return;
		} else {
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
		ektParm.setCount(ektParm.getCount("PRINT_USER"));
		ektParm.addData("SYSTEM", "COLUMNS", "RECP_TYPE");
		ektParm.addData("SYSTEM", "COLUMNS", "PRINT_USER");
		ektParm.addData("SYSTEM", "COLUMNS", "INV_NOS");
		ektParm.addData("SYSTEM", "COLUMNS", "INV_COUNT");
		
		printParm.setData("ektTable", ektParm.getData());
		printParm.setData("PRINT_USER","TEXT", Operator.getName());
		printParm.setData("PRINT_START","TEXT", "开始时间: "+printStartDate);
		printParm.setData("PRINT_END","TEXT", "结束时间: "+printEndDate);
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTBilpayInfo.jhw",printParm);
	}
	
	/**
	 * 取得作废票据byUser
	 * @param recp_type
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getCancelInvByUser(String recp_type ,String start_date, String end_date){
		String deptSql = "";
		String dept = "";
		if(!getValueString("DEPT").equals("")){
			deptSql = " AND A.PRINT_USER = B.USER_ID AND B.DEPT_CODE = '"+getValueString("DEPT")+"' ";
			dept = " ,SYS_OPERATOR_DEPT B ";
		}
		String sql = 
			"SELECT A.CANCEL_USER, C.USER_NAME, A.INV_NO" +
			" FROM BIL_INVRCP A, SYS_OPERATOR C " + dept +
			" WHERE A.CANCEL_FLG = '3'" +
			" AND A.RECP_TYPE = '" + recp_type + "'" +
			" AND A.CANCEL_DATE BETWEEN TO_DATE ('"+ start_date + "', 'YYYYMMDDHH24MISS')" + 
			" AND TO_DATE ('"+ end_date + "', 'YYYYMMDDHH24MISS') " +
			deptSql +
			//" AND LENGTH (A.INV_NO) < 12" +
			" AND A.CANCEL_USER = C.USER_ID" +
			" ORDER BY A.CANCEL_USER, A.INV_NO";
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
	 * 取得挂号作废票号byUser
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	public TParm getREGCancelInvByUser(String start_date , String end_date){
		TParm cancelParm = getCancelInv("EKT", start_date, end_date);
		if(cancelParm == null){
			cancelParm = new TParm();
		}
		cancelParm.setCount(cancelParm.getCount("INV_NO"));
		cancelParm.addData("SYSTEM", "COLUMNS", "INV_NO");
		return cancelParm;
	}
	 /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        
        ExportExcelUtil.getInstance().exportExcel(table, start_date+"到"+end_date+"预交金统计表");
    }
}
