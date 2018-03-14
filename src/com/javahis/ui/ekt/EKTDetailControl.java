package com.javahis.ui.ekt;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;


/**
 * <p>Title: 医疗卡明细账表</p>
 *
 * <p>Description: 医疗卡明细账表 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20120129
 * @version 1.0
 */

public class EKTDetailControl extends TControl{
	
	public EKTDetailControl(){
		
	}
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	setValue("START_DATE", today);
    	setValue("END_DATE", today);
    }
    
    /**
     * 查询方法
     */
	public void onQuery(){
		if("".equals(this.getValueString("START_DATE"))){
			this.messageBox("请选择日期");
			return;
		}
		String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE"))&&!"".equals(this.getValueString("END_DATE"))){
			startDate = getValueString("START_DATE").substring(0, 10);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + "000000";
		endDate = getValueString("END_DATE").substring(0, 10); 
		endDate =endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10)+"235959";
		}
		TParm result = query(startDate,endDate);
		this.callFunction("UI|TABLE|setParmValue", result);
	}
	
	/**
	 * 查询
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public TParm query(String startDate,String endDate){
		StringBuilder sql = new StringBuilder();
		//查交押金额，医院扣款，退押金额
		sql.append("SELECT MIN(A.BUSINESS_NO) AS BUSINESS_NO, A.MR_NO, B.PAT_NAME, "+
         "SUM (CASE WHEN A.CHARGE_FLG = '3' THEN A.BUSINESS_AMT ELSE 0 END) AS PAY, "+
         "SUM (CASE WHEN A.CHARGE_FLG IN ('1','2','6') THEN A.BUSINESS_AMT ELSE 0 END) AS DEDUCT, "+
         "SUM (CASE WHEN A.CHARGE_FLG = '7' THEN A.BUSINESS_AMT ELSE 0 END) AS REFUND "+
         "FROM (SELECT BUSINESS_NO || BUSINESS_SEQ AS BUSINESS_NO, BUSINESS_AMT,CHARGE_FLG, MR_NO " +
         "FROM EKT_ACCNTDETAIL WHERE 1=1 " );
		if(startDate!=null&&!startDate.equals("")){
			sql.append(" AND BUSINESS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate +
                "','YYYYMMDDHH24MISS') ");
		}
		sql.append(" ) A,SYS_PATINFO B WHERE A.MR_NO = B.MR_NO GROUP BY A.MR_NO,B.PAT_NAME ORDER BY A.MR_NO");
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
		}
		if(result.getCount()<=0){
			this.messageBox("没有数据");
			result = new TParm();
			return result;
		}
		sql = new StringBuilder();
		//查余额
		sql.append("SELECT MAX(TO_NUMBER(BUSINESS_NO||BUSINESS_SEQ)) AS SEQ,MR_NO,CURRENT_BALANCE "+
						"FROM EKT_ACCNTDETAIL WHERE CHARGE_FLG IN ('1','2','3','6','7')  ");
		if(startDate!=null&&!startDate.equals("")){
			sql.append(" AND BUSINESS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate +
            "','YYYYMMDDHH24MISS') ");
		}
		sql.append("GROUP BY MR_NO,BUSINESS_SEQ,CURRENT_BALANCE,BUSINESS_NO ORDER BY MR_NO,SEQ");
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if(result2.getErrCode()<0){
			this.messageBox(result.getErrText());
		}
		if(result2.getCount()<=0)
            this.messageBox("没有要查询的数据");
		result = getParm(result, result2);
		String date = getValueString("START_DATE").substring(0,10);
		for (int i = 0; i < result.getCount(); i++) {
			result.addData("DATE", date);
		}
		return result;
	}
	
	/**
	 * 得到查询的数据
	 * @param parm1
	 * @param parm2
	 * @return
	 */
	public TParm getParm(TParm parm1,TParm parm2){
		String mrno = (String) parm2.getData("MR_NO", 0);
		TParm result = new TParm();
		int k = 0 ;
		for (int i = 0; i < parm1.getCount(); i++) {
			if(parm1.getDouble("PAY", i)!=0.0||parm1.getDouble("DEDUCT", i)!=0.0
					||parm1.getDouble("REFUND", i)!=0.0){
				//将有消费记录的记录添加到新的tparm中
				result.addData("MR_NO", parm1.getData("MR_NO", i));
				result.addData("PAY", parm1.getData("PAY", i));
				result.addData("DEDUCT", parm1.getData("DEDUCT", i));
				result.addData("REFUND", parm1.getData("REFUND", i));
				result.addData("PAT_NAME", parm1.getData("PAT_NAME", i));
				k++;
			}
		}
		for (int i = 0; i < parm2.getCount(); i++) {
			if(!parm2.getData("MR_NO", i).equals(mrno)){
				mrno = (String) parm2.getData("MR_NO", i);
				result.addData("MR_NO", parm2.getData("MR_NO", i-1));
				result.addData("CURRENT_BALANCE", parm2.getData("CURRENT_BALANCE", i-1));
			}
			}
		result.addData("MR_NO", parm2.getData("MR_NO",parm2.getCount()-1));
		result.addData("CURRENT_BALANCE", parm2.getData("CURRENT_BALANCE",parm2.getCount()-1));
		result.setCount(k);
		return result;
	}
	/**
	 * 清空
	 */
	public void onClear(){
		clearValue("START_DATE");
	}
	/**
	 * 打印
	 */
	public void onPrint(){
	   	TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		TParm tableparm = table.getParmValue();
		TParm data = new TParm();// 打印的数据
		TParm parm = new TParm();// 表格数据
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("PRINT_DATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		date = getValueString("START_DATE").substring(0,10);
		data.setData("DATE", "TEXT", "查询日期: "+date);
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("DATE", tableparm.getData("DATE", i));
			parm.addData("MR_NO", tableparm.getData("MR_NO", i));
			parm.addData("PAT_NAME", tableparm.getData("PAT_NAME", i));
			parm.addData("PAY", tableparm.getDouble("PAY", i));
			parm.addData("DEDUCT", tableparm.getDouble("DEDUCT", i));
			parm.addData("REFUND", tableparm.getDouble("REFUND", i));
			parm.addData("CURRENT_BALANCE", tableparm.getDouble("CURRENT_BALANCE", i));
		}
		parm.setCount(parm.getCount("DATE"));
		parm.addData("SYSTEM", "COLUMNS", "DATE");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "PAY");
		parm.addData("SYSTEM", "COLUMNS", "DEDUCT");
		parm.addData("SYSTEM", "COLUMNS", "REFUND");
		parm.addData("SYSTEM", "COLUMNS", "CURRENT_BALANCE");
		data.setData("TABLE", parm.getData());
		//========modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTDetail.jhw",data);
		//========modify by lim 2012/02/24 begin
	}

}
