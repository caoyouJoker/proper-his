package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 医疗卡售卡明细表</p>
 *
 * <p>Description: 医疗卡售卡明细表 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20111226 
 * @version 1.0
 */
public class EKTSellCardDetailControl extends TControl {
	
	private TParm result;//查询结果
SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public EKTSellCardDetailControl(){
		
	}
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	setValue("REGION_CODE", Operator.getRegion());
    	setValue("OPT_USER", Operator.getID());
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	setValue("DEPT", Operator.getDept());
    }
    
    /**
     * 查询方法
     */
	public void onQuery(){
		String regionCode = getValue("REGION_CODE").toString();
		String accntType = getValue("ACCNT_TYPE").toString(); 
		String optUser = getValueString("OPT_USER");
		String dept = getValueString("DEPT");
		String startDate = "";
		String endDate = "";
		String sql1="";
		String sql2="";
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
		}
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlNew = new StringBuilder();
		if("Y".equals(this.getValue("FLG"))){
			sql1=" SELECT   D.EKT_CARD_NO AS CARD_NO, A.STORE_DATE, B.USER_NAME," +
			" A.ACCNT_TYPE REAL_ACCNT_TYPE, A.GATHER_TYPE REAL_GATHER_TYPE," +
			" A.MR_NO, C.PAT_NAME, A.AMT, A.PROCEDURE_AMT,'' CANCEL_FLG, " +
			" CASE" +
			" WHEN A.ACCNT_TYPE = 1" +
			" THEN '购卡'" +
			" WHEN A.ACCNT_TYPE = 2" +
			" THEN '换卡'" +
			" WHEN A.ACCNT_TYPE = 3" +
			" THEN '补卡'" +
			" WHEN A.ACCNT_TYPE = 4" +
			" THEN '充值'" +
			" WHEN A.ACCNT_TYPE = 5" +
			" THEN '扣款'" +
			" WHEN A.ACCNT_TYPE = 6" +
			" THEN '退费'" +
			" END ACCNT_TYPE," +
			" F.CHN_DESC GATHER_TYPE, A.CREAT_USER" +
			" FROM EKT_BIL_PAY A," +
			" SYS_OPERATOR B," +
			" SYS_PATINFO C," +
			" EKT_ISSUELOG D," +
			" SYS_OPERATOR_DEPT E," +
			" SYS_DICTIONARY F" +
			" WHERE A.CREAT_USER = B.USER_ID(+)" +
			" AND A.MR_NO = C.MR_NO(+)" +
			" AND A.CARD_NO = D.CARD_NO(+)" +
			" AND A.ACCNT_TYPE <> '5'" +
			" AND B.USER_ID = E.USER_ID(+)" +
			" AND E.MAIN_FLG = 'Y'" +
			" AND F.GROUP_ID = 'GATHER_TYPE'" +
			" AND A.GATHER_TYPE = F.ID" ;
		}else{
		 sql1 = "SELECT * FROM ("+
			" SELECT   D.EKT_CARD_NO AS CARD_NO, A.STORE_DATE, B.USER_NAME," +
			" A.ACCNT_TYPE REAL_ACCNT_TYPE, A.GATHER_TYPE REAL_GATHER_TYPE," +
			" A.MR_NO, C.PAT_NAME, A.AMT, A.PROCEDURE_AMT, G.CANCEL_FLG, " +
			" CASE" +
			" WHEN A.ACCNT_TYPE = 1" +
			" THEN '购卡'" +
			" WHEN A.ACCNT_TYPE = 2" +
			" THEN '换卡'" +
			" WHEN A.ACCNT_TYPE = 3" +
			" THEN '补卡'" +
			" WHEN (A.ACCNT_TYPE = 4 AND G.CANCEL_FLG=0)" +
			" THEN '充值'" +
			" WHEN A.ACCNT_TYPE = 5" +
			" THEN '扣款'" +
			" WHEN (A.ACCNT_TYPE = 6 AND G.CANCEL_FLG=1)" +
			" THEN '退费'" +
			" WHEN (A.ACCNT_TYPE = 4 AND G.CANCEL_FLG=3)" +
			" THEN '充值作废'" +
			" WHEN (A.ACCNT_TYPE = 6 AND G.CANCEL_FLG=3)" +
			" THEN '退费作废'" +
			" END ACCNT_TYPE," +
			" F.CHN_DESC GATHER_TYPE, A.CREAT_USER," +
			" G.INV_NO PRINT_NO"+
			//" A.PRINT_NO " +
			" FROM EKT_BIL_PAY A," +
			" SYS_OPERATOR B," +
			" SYS_PATINFO C," +
			" EKT_ISSUELOG D," +
			" SYS_OPERATOR_DEPT E," +
			" SYS_DICTIONARY F," +
			" BIL_INVRCP G " +
			" WHERE A.CREAT_USER = B.USER_ID(+)" +
			" AND A.MR_NO = C.MR_NO(+)" +
			" AND A.CARD_NO = D.CARD_NO(+)" +
			" AND A.ACCNT_TYPE <> '5'" +
			" AND B.USER_ID = E.USER_ID(+)" +
			" AND E.MAIN_FLG = 'Y'" +
			" AND F.GROUP_ID = 'GATHER_TYPE'" +
			" AND A.GATHER_TYPE = F.ID " +
			" AND G.RECEIPT_NO=A.BIL_BUSINESS_NO " + 
			" AND G.RECP_TYPE='EKT' ";
		 sql2=" UNION ALL"+
		" SELECT D.EKT_CARD_NO AS CARD_NO, A.STORE_DATE,B.USER_NAME," +
		"A.ACCNT_TYPE REAL_ACCNT_TYPE,A.GATHER_TYPE REAL_GATHER_TYPE," +
		"A.MR_NO,C.PAT_NAME,A.AMT,A.PROCEDURE_AMT,'' CANCEL_FLG, CASE WHEN A.ACCNT_TYPE = 2 " +
		"THEN '换卡' WHEN A.ACCNT_TYPE=1 THEN '购卡' END   ACCNT_TYPE," +
		"F.CHN_DESC GATHER_TYPE,A.CREAT_USER,  '' PRINT_NO" +
		" FROM EKT_BIL_PAY A, SYS_OPERATOR B, " +
		"SYS_PATINFO C, EKT_ISSUELOG D, SYS_OPERATOR_DEPT E," +
		" SYS_DICTIONARY F  WHERE  A.CREAT_USER = B.USER_ID(+) " +
		"AND A.MR_NO = C.MR_NO(+) AND A.CARD_NO = D.CARD_NO(+)" +
		" AND A.ACCNT_TYPE <> '5' AND B.USER_ID = E.USER_ID(+)" +
		" AND E.MAIN_FLG = 'Y' AND F.GROUP_ID = 'GATHER_TYPE'" +
		" AND A.GATHER_TYPE = F.ID " +
		" AND (A.ACCNT_TYPE= '2' OR A.ACCNT_TYPE='1')";
		 }
		sql.append(sql1);
		sqlNew.append(sql2);
		if(!regionCode.equals("")&&regionCode!=null){
			sql.append(" AND B.REGION_CODE ='"+regionCode+"' ");
			sqlNew.append(" AND B.REGION_CODE ='"+regionCode+"' ");
			
		}
		if(!startDate.equals("")&&startDate!=null&&!endDate.equals("")&&endDate!=null){
			sql.append(" AND A.STORE_DATE BETWEEN TO_DATE('" + startDate +
					"','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate +
                "','YYYYMMDDHH24MISS') ");
			sqlNew.append(" AND A.STORE_DATE BETWEEN TO_DATE('" + startDate +
					"','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate +
			"','YYYYMMDDHH24MISS') ");
		}
		if(!accntType.equals("")&&accntType!=null){
			sql.append(" AND A.ACCNT_TYPE = '"+accntType+"' ");
			sqlNew.append(" AND A.ACCNT_TYPE = '"+accntType+"' ");
		}
		if(!optUser.equals("")&&optUser!=null){
			sql.append(" AND A.OPT_USER = '"+optUser+"' ");
			sqlNew.append(" AND A.OPT_USER = '"+optUser+"' ");
		}
		if(!dept.equals("")&&dept!=null){
			sql.append(" AND E.DEPT_CODE = '"+dept+"' ");
			sqlNew.append(" AND E.DEPT_CODE = '"+dept+"' ");
		}
		if("N".equals(this.getValue("FLG"))){
			sql.append(sqlNew);
			sql.append(") ORDER BY STORE_DATE ");
		}else{
		sql.append(" ORDER BY A.CREAT_USER, A.STORE_DATE");
		}
		//System.out.println(";;;;;;;"+sql.toString());
		result =new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if(result.getCount()<=0)
            this.messageBox("没有要查询的数据");
        this.callFunction("UI|TABLE|setParmValue", result);
	}
	
//	EKTSellCardDetail.jhw
	/**
	 * 打印
	 */
	public void onPrint(){
		onQuery();
		TParm data = new TParm();
		TParm tableParm = print(result);
		tableParm.setCount(tableParm.getCount("USER_NAME"));
		tableParm.addData("SYSTEM", "COLUMNS", "STORE_DATE");
		tableParm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		tableParm.addData("SYSTEM", "COLUMNS", "ACCNT_TYPE");
		tableParm.addData("SYSTEM", "COLUMNS", "GATHER_TYPE");
		tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "CARD_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		tableParm.addData("SYSTEM", "COLUMNS", "AMT");
		tableParm.addData("SYSTEM", "COLUMNS", "PROCEDURE_AMT");
		tableParm.addData("SYSTEM", "COLUMNS", "PRINT_NO");
		String date = SystemTool.getInstance().getDate().toString();
//		System.out.println(date);
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("PRINTDATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		
		//把表格数据添加进要打印的parm
		data.setData("TABLE", tableParm.getData());
		//========modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTSellCardDetail.jhw",data);
		//========modify by lim 2012/02/24 end
	}
	/**
	 * 获取打印数据
	 * @param parm
	 * @return
	 */
	public TParm print(TParm parm){
		String creatUser = parm.getValue("CREAT_USER", 0);
		int buyCardCount = 0;//购卡次数
		int totbuyCardCount = 0;//购卡次数
		double sumAmt = 0;//个人总钱数
		double sumProcedureAmt = 0;//个人总手续费
		double totAmt = 0;//总钱数
		double totProcedureAmt = 0;//总手续费
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if(parm.getValue("CREAT_USER", i).equals(creatUser)){
				if(parm.getValue("REAL_ACCNT_TYPE", i).equals("1")){
					buyCardCount++;
					totbuyCardCount++;
				}
				if(!"3".equals(parm.getValue("CANCEL_FLG",i))){
				if(parm.getValue("REAL_ACCNT_TYPE", i).equals("6")){
					sumAmt -= parm.getDouble("AMT", i);
					sumProcedureAmt -= parm.getDouble("PROCEDURE_AMT", i);
					totAmt -= parm.getDouble("AMT", i);
					totProcedureAmt -= parm.getDouble("PROCEDURE_AMT", i);
				}else{
					sumAmt += parm.getDouble("AMT", i);
					sumProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
					totAmt += parm.getDouble("AMT", i);
					totProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
					}
				}
				
				result.addData("STORE_DATE", sdf.format(parm.getData("STORE_DATE", i)));
				result.addData("USER_NAME", parm.getData("USER_NAME", i));
				result.addData("ACCNT_TYPE", parm.getData("ACCNT_TYPE", i));
				result.addData("GATHER_TYPE", parm.getData("GATHER_TYPE", i));
				result.addData("MR_NO", parm.getData("MR_NO", i));
				result.addData("CARD_NO", parm.getData("CARD_NO", i));
				result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
				result.addData("AMT", parm.getData("AMT", i));
				result.addData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT", i));
				result.addData("PRINT_NO", parm.getData("PRINT_NO", i));
			}else{
				result.addData("STORE_DATE", "");
				result.addData("USER_NAME", "<柜员发卡");
				result.addData("ACCNT_TYPE", "合计>");
				result.addData("GATHER_TYPE", buyCardCount+" 张卡");
				result.addData("MR_NO", "");
				result.addData("CARD_NO", "");
				result.addData("PAT_NAME", "");
				result.addData("AMT", StringTool.round(sumAmt,2));
				result.addData("PROCEDURE_AMT", StringTool.round(sumProcedureAmt,2));
				result.addData("PRINT_NO", "");
				
				result.addData("STORE_DATE", "");
				result.addData("USER_NAME", "");
				result.addData("ACCNT_TYPE", "");
				result.addData("GATHER_TYPE", "");
				result.addData("MR_NO", "");
				result.addData("CARD_NO", "");
				result.addData("PAT_NAME", "");
				result.addData("AMT", "");
				result.addData("PROCEDURE_AMT", "");
				result.addData("PRINT_NO", "");
				sumAmt = 0;
				sumProcedureAmt = 0;
				buyCardCount = 0;
				
				if(!"3".equals(parm.getValue("CANCEL_FLG",i))){
					if(parm.getValue("REAL_ACCNT_TYPE", i).equals("6")){
						sumAmt -= parm.getDouble("AMT", i);
						sumProcedureAmt -= parm.getDouble("PROCEDURE_AMT", i);
						totAmt -= parm.getDouble("AMT", i);
						totProcedureAmt -= parm.getDouble("PROCEDURE_AMT", i);
					}else{
						sumAmt += parm.getDouble("AMT", i);
						sumProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
						totAmt += parm.getDouble("AMT", i);
						totProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
						}
					}
				
				result.addData("STORE_DATE", sdf.format(parm.getTimestamp("STORE_DATE", i)));
				result.addData("USER_NAME", parm.getData("USER_NAME", i));
				result.addData("ACCNT_TYPE", parm.getData("ACCNT_TYPE", i));
				result.addData("GATHER_TYPE", parm.getData("GATHER_TYPE", i));
				result.addData("MR_NO", parm.getData("MR_NO", i));
				result.addData("CARD_NO", parm.getData("CARD_NO", i));
				result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
				result.addData("AMT", parm.getData("AMT", i));
				result.addData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT", i));
				result.addData("PRINT_NO", parm.getData("PRINT_NO", i));
			}
			creatUser = parm.getValue("CREAT_USER", i);
		}
		result.addData("STORE_DATE", "");
		result.addData("USER_NAME", "<柜员发卡");
		result.addData("ACCNT_TYPE", "合计>");
		result.addData("GATHER_TYPE", buyCardCount+" 张卡");
		result.addData("MR_NO", "");
		result.addData("CARD_NO", "");
		result.addData("PAT_NAME", "");
		result.addData("AMT", StringTool.round(sumAmt,2));
		result.addData("PROCEDURE_AMT", StringTool.round(sumProcedureAmt,2));
		result.addData("PRINT_NO","");
		
		result.addData("STORE_DATE", "");
		result.addData("USER_NAME", "<发卡总计");
		result.addData("ACCNT_TYPE", ">");
		result.addData("GATHER_TYPE", totbuyCardCount+" 张卡");
		result.addData("MR_NO", "");
		result.addData("CARD_NO", "");
		result.addData("PAT_NAME", "");
		result.addData("AMT", StringTool.round(totAmt,2));
		result.addData("PROCEDURE_AMT", StringTool.round(totProcedureAmt,2));
		result.addData("PRINT_NO","");
		
//		DecimalFormat df = new DecimalFormat("########0.00");
//		String sql =
//			"SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'GATHER_TYPE'";
//		TParm gather = new TParm(TJDODBTool.getInstance().select(sql));
//		Map<Object, Object> map = parm.getData();
//		map = (Map<Object, Object>) map.get("Data");
//		
//		List user_names = (List) map.get("USER_NAME");
//		List amts = (List) map.get("AMT");
//		List procedure_amts = (List) map.get("PROCEDURE_AMT");
//		List mr_nos = (List) map.get("MR_NO");
//		List store_dates = (List) map.get("STORE_DATE");
//		List gather_types = (List) map.get("GATHER_TYPE");
//		List pay_names = (List) map.get("PAT_NAME");
//		List accnt_types = (List) map.get("ACCNT_TYPE");
//		List cardNos = (List) map.get("CARD_NO");
//		String gather_type_str = "";
//		String store_date_str = "";
//		String accnt_type_str = "";
//		int buyCardCount = 0;//购卡次数
//		String user_name = "";
//		double amtFee = 0.00;//收入
//		double prAmtFee = 0.00;//手续费
//		double fee = 0.00;//金额
//		int buyCardCountTotal = 0;//购卡总次数
//		double feeTotal = 0.00;//总金额
//		double amtFeeTotal = 0.00;//总收入
//		double prAmtFeeTotal = 0.00;//总手续费
//		int k = 0;
//		for (int i = 0; i < user_names.size(); i++) {
//			for (int j = 0; j < gather.getCount(); j++) {
//				if(gather_types.get(i).equals(gather.getData("ID", j))){
//					gather_type_str= gather.getData("CHN_DESC", j).toString();
//					gather_types.set(i, gather_type_str);
//				}
//			}
//			if(store_dates.get(i)!=null&&!store_dates.get(i).equals("")){
//				store_date_str = store_dates.get(i).toString().substring(0, 4)+
//				"/"+store_dates.get(i).toString().substring(5, 7)+"/"+store_dates.get(i).toString().substring(8, 10);
//				store_dates.set(i, store_date_str);
//			}
////			明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
//			if(accnt_types.get(i).equals("1")){
//				accnt_type_str = "购卡";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(accnt_types.get(i).equals("2")){
//				accnt_type_str = "换卡";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(accnt_types.get(i).equals("3")){
//				accnt_type_str = "补卡";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(accnt_types.get(i).equals("4")){
//				accnt_type_str = "充值";
//				accnt_types.set(i, accnt_type_str);
//			}
////			if(accnt_types.get(i).equals("5")){
////				accnt_type_str = "扣款";
////				accnt_types.set(i, accnt_type_str);
////			}
//			if(accnt_types.get(i).equals("6")){
//				accnt_type_str = "退费";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(!user_names.get(i).toString().equals(user_name)&&i!=0){
//				for (int j = k; j < i; j++) {
//					//发卡
//					if(accnt_types.get(j).equals("购卡")){
////						System.out.println(i);
//						buyCardCount++;//发卡笔数
//						amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//收入
//						prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//手续费
//					}
//					//换卡补卡充值扣款
//					if(accnt_types.get(j).equals("换卡")||accnt_types.get(j).equals("补卡")
//							||accnt_types.get(j).equals("充值")){
//						amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//收入
//						prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//手续费
//					}
//					//退费
//					if(accnt_types.get(j).equals("退费")){
//						amtFee = amtFee - Double.parseDouble(amts.get(j).toString());//收入
//						prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//手续费
//					}
//					k=i;
//				}
//				fee = amtFee;
//				feeTotal = feeTotal+fee;
//				amtFeeTotal = amtFeeTotal + amtFee;
//				prAmtFeeTotal = prAmtFeeTotal + prAmtFee;
//				buyCardCountTotal = buyCardCountTotal +buyCardCount;
//				user_names.add(i, "");
//				user_names.add(i, "<柜员发卡");
//				amts.add(i, "");
//				amts.add(i, StringTool.round(fee,2));
//				procedure_amts.add(i, "");
//				procedure_amts.add(i, StringTool.round(prAmtFee,2));
//				mr_nos.add(i, "");
//				mr_nos.add(i, "");
//				store_dates.add(i, "");
//				store_dates.add(i, "");
//				gather_types.add(i, "");
//				gather_types.add(i, buyCardCount+" 张卡");
//				pay_names.add(i, "");
//				pay_names.add(i, "");
//				accnt_types.add(i, "");
//				accnt_types.add(i, "合计>");
//				cardNos.add(i, "");
//				cardNos.add(i, "");
//				i=i+2;
//				amtFee=0;
//				prAmtFee=0;
//				feeTotal=0;
//				amtFeeTotal=0;
//				prAmtFeeTotal=0;
//				fee=0;
//				prAmtFee=0;
//				buyCardCount=0;
//			}
//			user_name = user_names.get(i).toString();
//		}
//		for (int j = k; j < user_names.size(); j++) {
//			//发卡
//			if(accnt_types.get(j).equals("购卡")){
//				buyCardCount++;//发卡笔数
//				amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//收入
//				prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//手续费
//			}
//			//换卡补卡充值扣款
//			if(accnt_types.get(j).equals("换卡")||accnt_types.get(j).equals("补卡")
//					||accnt_types.get(j).equals("充值")){
//				amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//收入
//				prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//手续费
//			}
//			//退费
//			if(accnt_types.get(j).equals("退费")){
//				amtFee = amtFee - Double.parseDouble(amts.get(j).toString());//收入
//				prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//手续费
//			}
//		}
//		fee = amtFee;
//		feeTotal = feeTotal+fee;
//		amtFeeTotal = amtFeeTotal + amtFee;
//		prAmtFeeTotal = prAmtFeeTotal + prAmtFee;
//		buyCardCountTotal = buyCardCountTotal +buyCardCount;
//		user_names.add("<柜员发卡");
//		user_names.add("<发卡总计");
//		amts.add(StringTool.round(fee,2));
//		amts.add(StringTool.round(feeTotal,2));
//		procedure_amts.add(StringTool.round(prAmtFee,2));
//		procedure_amts.add(StringTool.round(prAmtFeeTotal,2));
//		mr_nos.add("");
//		mr_nos.add("");
//		store_dates.add("");
//		store_dates.add("");
//		gather_types.add(buyCardCount+" 张卡");
//		gather_types.add(buyCardCountTotal+" 张卡");
//		pay_names.add("");
//		pay_names.add("");
//		accnt_types.add("合计>");
//		accnt_types.add(">");
//		cardNos.add("");
//		cardNos.add("");
//		
//		map.put("AMT", amts);
//		map.put("PROCEDURE_AMT", procedure_amts);
//		map.put("MR_NO", mr_nos);
//		map.put("STORE_DATE", store_dates);
//		map.put("GATHER_TYPE", gather_types);
//		map.put("PAT_NAME", pay_names);
//		map.put("USER_NAME", user_names);
//		map.put("ACCNT_TYPE", accnt_types);
//		Map<Object, Object> resultMap = new HashMap<Object, Object>();;
//		resultMap.put("Data", map);
//		TParm result = new TParm(resultMap);
		return result;
	}
	/**
	 * 清空
	 */
	public void onClear(){
		onInit();
		this.setValue("FLG", "N");
		 this.callFunction("UI|TABLE|setParmValue", new TParm());
		//clearValue("ACCNT_TYPE;OPT_USER;START_DATE;END_DATE");
	}
}
