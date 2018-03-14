package com.javahis.ui.ekt;



import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 医疗卡交易方式汇总</p>
 *
 * <p>Description:  医疗卡交易方式汇总 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p> 
 *
 * @author zhangp 20120110
 * @version 1.0
 */

public class EKTTradeWayControl  extends TControl{
	
	TTable table;
	private int radio = 1;
	private String startDate = "";
	private String endDate = "";
	String regionCode = "";
	/**
     * 初始化方法
     */
    public void onInit() {
    	table = (TTable)this.getComponent("TABLE");
    	String region = Operator.getRegion();
    	setValue("REGION_CODE", region);
    	Timestamp today = SystemTool.getInstance().getDate();
    	setValue("START_DATE", today);
    	setValue("END_DATE", today);
    	regionCode = getValueString("REGION_CODE");
    }
    /**
     * radio监听
     * @param obj
     */
    public void onCheck(Object obj) {
    	//===start===modify by kangy 20170531
        if ("1".equals(obj.toString())) {
        	 table.setHeader("交易方式,120,ACCNT_TYPE;现金,120,double,#########0.00;刷卡,120,double,#########0.00;支票,120,double,#########0.00;工商圈存机,120,double,#########0.00;汇票,120,double,#########0.00;应收款,120,double,#########0.00;手续费,80,double,#########0.00;总金额,120,double,#########0.00");
            table.setParmMap("PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
            table.setItem("ACCNT_TYPE");
            table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right");
        	 radio = 1;
        }
        if ("2".equals(obj.toString())) {
            table.setHeader("付款方式,120,GATHER_TYPE;交易金额,120,double,#########0.00;手续费,80,double,#########0.00;总金额,120,double,#########0.00");
            table.setParmMap("PARM1;PARM2;PARM3;PARM4");
            table.setItem("GATHER_TYPE");
            table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right");	
            radio = 2;
        }
        if ("3".equals(obj.toString())) {
       	 table.setHeader("人员编号,120;现金,120,double,#########0.00;刷卡,120,double,#########0.00;支票,120,double,#########0.00;工商圈存机,120,double,#########0.00;汇票,120,double,#########0.00;应收款,120,double,#########0.00;手续费,80,double,#########0.00;总金额,120,double,#########0.00");
         table.setParmMap("PARM1;PARM2;PARM3;PARM4;PARM5;PARM6;PARM7;PARM8;PARM9");
         table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right");	
       	 table.setItem("");
         radio = 3;
        }
        //==end== modify by kangy 20170531
    }
    /**
     * 获取sql
     * @return
     */
    public String getSql(){
    	regionCode = getValueString("REGION_CODE");
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + "000000";
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + "235959";
		}
    	StringBuilder sql = new StringBuilder();
    	String sql1 = "";
    	//交易方式
    	if(radio == 1){
    		if(regionCode!=null&&!regionCode.equals("")){
    			sql.append(" AND B.REGION_CODE='"+regionCode+"' ");
    		}
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql.append(" AND A.STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ");
    		}
    		sql.append(" GROUP BY ACCNT_TYPE ");
    		sql1+="SELECT PARM1,SUM(PARM2) PARM2,SUM(PARM3) PARM3,SUM(PARM4) PARM4,SUM(PARM5) PARM5,SUM(PARM6) PARM6,SUM(PARM7) PARM7,SUM(PARM8) PARM8,SUM(PARM9) PARM9 FROM ( "
    			+ " SELECT A.ACCNT_TYPE AS PARM1, SUM(A.AMT) AS PARM2,0 PARM3,0 PARM4,0 PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,SUM(A.AMT+A.PROCEDURE_AMT) PARM9 "
    			+ " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
    			+ " AND A.ACCNT_TYPE <> '5' AND A.GATHER_TYPE = 'C0' "+sql.toString()
                + " UNION ALL "
                + " SELECT  A.ACCNT_TYPE AS PARM1,0 PARM2,SUM(A.AMT) PARM3,0 PARM4,0 PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,SUM(A.AMT+A.PROCEDURE_AMT) PARM9  "
                + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID"
                + " AND A.ACCNT_TYPE <> '5' AND A.GATHER_TYPE = 'C1' "+sql.toString()
                + " UNION ALL "
                + " SELECT  A.ACCNT_TYPE AS PARM1, 0 AS PARM2,0 PARM3,SUM(A.AMT) PARM4,0 PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,SUM(A.AMT+A.PROCEDURE_AMT) PARM9   "
                + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                + " AND A.ACCNT_TYPE <> '5' AND A.GATHER_TYPE = 'T0' "+sql.toString()
                + " UNION ALL "
                + " SELECT  A.ACCNT_TYPE AS PARM1, 0 PARM2,0 PARM3,0 PARM4,SUM(A.AMT) PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,SUM(A.AMT+A.PROCEDURE_AMT) PARM9   "
                + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                + " AND A.ACCNT_TYPE <> '5' AND A.GATHER_TYPE = 'Z' "+sql.toString()
                + " UNION ALL "
                + " SELECT  A.ACCNT_TYPE AS PARM1, 0 PARM2,0 PARM3,0 PARM4,0 PARM5,SUM(A.AMT) PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,SUM(A.AMT+A.PROCEDURE_AMT) PARM9   "
                + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                + " AND A.ACCNT_TYPE <> '5' AND A.GATHER_TYPE = 'C2' "+sql.toString()
                + " UNION ALL "
                + " SELECT  A.ACCNT_TYPE AS PARM1,0 PARM2,0 PARM3,0 PARM4,0 PARM5,0 PARM6,SUM(A.AMT) PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,SUM(A.AMT+A.PROCEDURE_AMT) PARM9   "
                + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                + " AND A.ACCNT_TYPE <> '5' AND A.GATHER_TYPE = 'C4' "+sql.toString()+" ) GROUP BY PARM1 ";
    		return sql1;
    	}
    	//付款方式
    	if(radio == 2){
    		//=====zhangp 20120226 modify start
//    		sql1 = "SELECT A.AMT-B.SUM AS CASH,B.SUM AS GREENPATH FROM ";
//    		sql.append(sql1);
//    		String sqla = " (SELECT SUM(A.AMT) AS AMT FROM EKT_TREDE A,REG_PATADM B WHERE B.CASE_NO = A.CASE_NO ";
//    		String sqlb = " (SELECT SUM(GREEN_PATH_TOTAL)-SUM(GREEN_BALANCE) AS SUM FROM REG_PATADM WHERE 1=1 ";
//    		if(regionCode!=null&&!regionCode.equals("")){
//    			sqla = sqla + " AND B.REGION_CODE = '"+regionCode+"' ";
//    			sqlb = sqlb + " AND REGION_CODE = '"+regionCode+"' ";
//    		}
//    		if(!startDate.equals("")&&!endDate.equals("")){
//    			sqla = sqla + " AND A.OPT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
//    			sqlb = sqlb + " AND OPT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
//    		}
//    		sqla = sqla + " ) A, ";
//    		sqlb = sqlb + " ) B ";
//    		sql.append(sqla+sqlb);
//    		System.out.println(sql);
//    		return sql.toString();
    		//==start=== modify by kangy 20170531
    		String payTypeSql="SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' ORDER BY SEQ,ID ";
        	TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(payTypeSql));
        	String sql2="";
    	/*	sql1 =
    			"SELECT SUM(AMT) AS CASH FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '4'";*/
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql2 =" AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
    		}
    		sql1="SELECT GATHER_TYPE,AMT,PROCEDURE_AMT FROM ("
    			+ "SELECT GATHER_TYPE, SUM(AMT) AMT,SUM(PROCEDURE_AMT) PROCEDURE_AMT FROM (";
    		for(int i=0;i<payTypeParm.getCount();i++){
        		sql1 +=" SELECT GATHER_TYPE,CASE WHEN ACCNT_TYPE='4' THEN SUM(AMT) WHEN ACCNT_TYPE='6' THEN -SUM(AMT) END AMT,ACCNT_TYPE,SUM(PROCEDURE_AMT) PROCEDURE_AMT FROM EKT_BIL_PAY WHERE ACCNT_TYPE IN ('2','4','6') AND GATHER_TYPE='"+payTypeParm.getValue("ID",i)+"'"+sql2
        				+" GROUP BY GATHER_TYPE,ACCNT_TYPE UNION ALL ";
        	}
    		sql1=sql1.substring(0, sql1.length()-10);
    		sql1+=") GROUP BY GATHER_TYPE ORDER BY GATHER_TYPE ) WHERE AMT<>0 OR PROCEDURE_AMT<>0 ";
    		//===end== modify by kangy 20170531
    		return sql1;
    		//======zhangp 20120226 modify end
    	}
    	//人员编号
    	if(radio == 3){
    		//==start=== modify by kangy 20170531
    		/*sql1 = "SELECT CREAT_USER AS PARM1,SUM(A.AMT) AS PARM2, SUM(PROCEDURE_AMT) AS PARM3,SUM(A.AMT+ PROCEDURE_AMT) AS PARM4 " +
    				" FROM EKT_BIL_PAY A, SYS_OPERATOR B " +
    				" WHERE A.CREAT_USER=B.USER_ID " +
    				" AND A.ACCNT_TYPE IN ('4','2') ";
    		sql.append(sql1);*/
    		if(regionCode!=null&&!regionCode.equals("")){
    			sql.append(" AND B.REGION_CODE='"+regionCode+"' ");
    		}
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql.append(" AND A.STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ");
    		}
    		sql.append(" GROUP BY CREAT_USER,ACCNT_TYPE ");
      		sql1+="SELECT PARM1,SUM(PARM2) PARM2,SUM(PARM3) PARM3,SUM(PARM4) PARM4,SUM(PARM5) PARM5,SUM(PARM6) PARM6,SUM(PARM7) PARM7,SUM(PARM8) PARM8,SUM(PARM9) PARM9 FROM ( "
        			+ " SELECT A.CREAT_USER AS PARM1, CASE WHEN ACCNT_TYPE='6' THEN -SUM(A.AMT) ELSE SUM(A.AMT) END PARM2,0 PARM3,0 PARM4,0 PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,  CASE WHEN ACCNT_TYPE = '6' THEN -SUM (A.AMT)+ SUM(A.PROCEDURE_AMT) ELSE SUM (A.AMT)+ sum(A.PROCEDURE_AMT) END PARM9 "
        			+ " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
        			+ " AND A.ACCNT_TYPE IN ('2','4','6') AND A.GATHER_TYPE = 'C0' "+sql.toString()
                    + " UNION ALL "
                    + " SELECT  A.CREAT_USER AS PARM1,0 PARM2,CASE WHEN ACCNT_TYPE='6' THEN -SUM(A.AMT) ELSE SUM(A.AMT) END PARM3,0 PARM4,0 PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,  CASE WHEN ACCNT_TYPE = '6' THEN -SUM (A.AMT)+ SUM(A.PROCEDURE_AMT) ELSE SUM (A.AMT)+ sum(A.PROCEDURE_AMT) END PARM9  "
                    + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID"
                    + " AND A.ACCNT_TYPE IN ('2','4','6') AND A.GATHER_TYPE = 'C1' "+sql.toString()
                    + " UNION ALL "
                    + " SELECT  A.CREAT_USER AS PARM1, 0 AS PARM2,0 PARM3,CASE WHEN ACCNT_TYPE='6' THEN -SUM(A.AMT) ELSE SUM(A.AMT) END PARM4,0 PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,  CASE WHEN ACCNT_TYPE = '6' THEN -SUM (A.AMT)+ SUM(A.PROCEDURE_AMT) ELSE SUM (A.AMT)+ sum(A.PROCEDURE_AMT) END PARM9   "
                    + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                    + " AND A.ACCNT_TYPE IN ('2','4','6') AND A.GATHER_TYPE = 'T0' "+sql.toString()
                    + " UNION ALL "
                    + " SELECT  A.CREAT_USER AS PARM1, 0 PARM2,0 PARM3,0 PARM4,CASE WHEN ACCNT_TYPE='6' THEN -SUM(A.AMT) ELSE SUM(A.AMT) END PARM5,0 PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,  CASE WHEN ACCNT_TYPE = '6' THEN -SUM (A.AMT)+ SUM(A.PROCEDURE_AMT) ELSE SUM (A.AMT)+ sum(A.PROCEDURE_AMT) END PARM9   "
                    + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                    + " AND A.ACCNT_TYPE IN ('2','4','6') AND A.GATHER_TYPE = 'Z' "+sql.toString()
                    + " UNION ALL "
                    + " SELECT  A.CREAT_USER AS PARM1, 0 PARM2,0 PARM3,0 PARM4,0 PARM5,CASE WHEN ACCNT_TYPE='6' THEN -SUM(A.AMT) ELSE SUM(A.AMT) END PARM6,0 PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,  CASE WHEN ACCNT_TYPE = '6' THEN -SUM (A.AMT)+ SUM(A.PROCEDURE_AMT) ELSE SUM (A.AMT)+ sum(A.PROCEDURE_AMT) END PARM9   "
                    + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                    + " AND A.ACCNT_TYPE IN ('2','4','6') AND A.GATHER_TYPE = 'C2' "+sql.toString()
                    + " UNION ALL "
                    + " SELECT  A.CREAT_USER AS PARM1,0 PARM2,0 PARM3,0 PARM4,0 PARM5,0 PARM6,CASE WHEN ACCNT_TYPE='6' THEN -SUM(A.AMT) ELSE SUM(A.AMT) END PARM7,SUM(A.PROCEDURE_AMT) AS PARM8,  CASE WHEN ACCNT_TYPE = '6' THEN -SUM (A.AMT)+ SUM(A.PROCEDURE_AMT) ELSE SUM (A.AMT)+ sum(A.PROCEDURE_AMT) END PARM9   "
                    + " FROM EKT_BIL_PAY A, SYS_OPERATOR B WHERE A.CREAT_USER = B.USER_ID "
                    + " AND A.ACCNT_TYPE IN ('2','4','6') AND A.GATHER_TYPE = 'C4' "+sql.toString()+" ) GROUP BY PARM1 ";
    		return sql1.toString();
    		//==end== modify by kangy 20170531
    	}
    	
    	return sql.toString();
    }
    
    /**
     * 查询
     * 
     */
    public void onQuery(){
    	String sql = getSql();
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    	}
    	if(result.getCount()<=0){
    		messageBox("查无结果");
    	}
//    	sql = 
//    		"SELECT ISSUERSN_CODE,FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '8'";
//    	TParm factResult = new TParm(TJDODBTool.getInstance().select(sql));
    	sql = 
    		"SELECT SUM(PROCEDURE_AMT) AS PROCEDURE_AMT FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '2'";
    	if(!startDate.equals("")&&!endDate.equals("")){
			sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
		}
    	TParm countResult = new TParm(TJDODBTool.getInstance().select(sql));
    	if(radio ==1){
    		for (int i = 0; i < result.getCount(); i++) {
				if(result.getData("PARM1",i).equals("2")){
					result.setData("PARM8", i, countResult.getDouble("PROCEDURE_AMT", 0) );
				}
			}
    	}
    	String sql2="";
    	if(radio==2){
    		//===start==== modify by kangy 20170531
    	/*	sql =
        			"SELECT SUM(AMT) AS CASH FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '6'";
        		if(!startDate.equals("")&&!endDate.equals("")){
        			sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
        		}
        		TParm tempParm = new TParm(TJDODBTool.getInstance().select(sql));
        		double tempCash = 0.00;
        		if(tempParm.getCount()>0){
        			tempCash = tempParm.getDouble("CASH", 0);
        		}
        		result.setData("CASH", 0 , result.getDouble("CASH", 0)-tempCash);
        		//==zhangp 20120319 start*/
    	/*	sql = 
    	    		"SELECT GATHER_TYPE,SUM(PROCEDURE_AMT) AS PROCEDURE_AMT FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '2'";
    	    	if(!startDate.equals("")&&!endDate.equals("")){
    				sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
    			}
    	    	TParm countResult2 = new TParm(TJDODBTool.getInstance().select(sql));
        		result.setData("PROCEDURE_AMT", 0 , countResult2.getDouble("PROCEDURE_AMT", 0));*/
        		//===zhangp 20120319 end
        		result = getParm(result);
        		//===end==== modify by kangy 20170531
    	}
    /*	if(radio==3){
//    		sql = 
//	    		"SELECT SUM(PROCEDURE_AMT) AS PROCEDURE_AMT,CREAT_USER FROM EKT_BIL_PAY WHERE ACCNT_TYPE = '2' ";
//    		if(!startDate.equals("")&&!endDate.equals("")){
//    			sql = sql + " AND STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
//    		}
//    		sql = sql + " GROUP BY CREAT_USER ORDER BY CREAT_USER";
//    		countResult = new TParm(TJDODBTool.getInstance().select(sql));
    		sql =
    			"SELECT CREAT_USER AS PARM1,SUM(A.AMT) AS PARM2, SUM(PROCEDURE_AMT) AS PARM3,SUM(A.AMT)+ SUM(PROCEDURE_AMT) AS PARM4 " +
				" FROM EKT_BIL_PAY A, SYS_OPERATOR B " +
				" WHERE A.CREAT_USER=B.USER_ID " +
				" AND A.ACCNT_TYPE = '6' ";
    		if(regionCode!=null&&!regionCode.equals("")){
    			sql = sql + " AND B.REGION_CODE='"+regionCode+"' ";
    		}
    		if(!startDate.equals("")&&!endDate.equals("")){
    			sql = sql + " AND A.STORE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS') ";
    		}
    		sql = sql + " GROUP BY CREAT_USER ORDER BY A.CREAT_USER";
    		countResult = new TParm(TJDODBTool.getInstance().select(sql));
    		double tempCash = 0.00;
    		for (int i = 0; i < result.getCount(); i++) {
    			for (int j = 0; j < countResult.getCount(); j++) {
    				tempCash = countResult.getDouble("PARM2", j);
    				if(result.getData("PARM1", i).equals(countResult.getData("PARM1", j))){
    					result.setData("PARM3", i, result.getDouble("PARM3", i)+countResult.getDouble("PARM3", j) );
    					result.setData("PARM2", i, result.getDouble("PARM2", i)-tempCash);
        			}
				}
			}
//    		System.out.println("3=="+result);
    	}*/
    	//zhangp 20120129 修改总计
    	/*	for (int i = 0; i < result.getCount(); i++) {
    			//求总和
    			double parm4 = result.getDouble("PARM2", i) + result.getDouble("PARM3", i);
    			result.setData("PARM4", i, parm4);
			}*/
//    	System.out.println(result);
    	this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    /**
     * 取得付款方式的tparm
     * @param parm
     * @return
     */
    public TParm getParm(TParm parm){
    	TParm result = new TParm();
    	//==start====modify by kangy 29170531
    	for(int i=0;i<parm.getCount();i++){
    		result.addData("PARM1", parm.getData("GATHER_TYPE",i));
        	result.addData("PARM2",parm.getData("AMT", i));
        	result.addData("PARM3",parm.getData("PROCEDURE_AMT", i));
        	result.addData("PARM4",parm.getDouble("AMT", i)+parm.getDouble("PROCEDURE_AMT",i));
    	}
    	//==end===modify by kangy 20170531
    	return result;
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
		double PARM2total = 0.00;
		double PARM3total = 0.00;
		double PARM4total = 0.00;
		double PARM5total = 0.00;
		double PARM6total = 0.00;
		double PARM7total = 0.00;
		double PARM8total = 0.00;
		double PARM9total = 0.00;
		TParm data = new TParm();// 打印的数据
		TParm parm = new TParm();// 表格数据
		for (int i = 0; i < table.getRowCount(); i++) {
			if(radio == 1){
				if(tableparm.getData("PARM1", i).equals("1")){
					parm.addData("PARM1", "购卡");
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
					//==start=== add by kangy 20170531
					PARM5total = PARM5total + tableparm.getDouble("PARM5", i);
					PARM6total = PARM6total + tableparm.getDouble("PARM6", i);
					PARM7total = PARM7total + tableparm.getDouble("PARM7", i);
					PARM8total = PARM8total + tableparm.getDouble("PARM8", i);
					PARM9total = PARM9total + tableparm.getDouble("PARM9", i);
					//==end=== add by kangy 20170531
				}
				if(tableparm.getData("PARM1", i).equals("2")){
					parm.addData("PARM1", "挂失");
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
					//==start=== add by kangy 20170531
					PARM5total = PARM5total + tableparm.getDouble("PARM5", i);
					PARM6total = PARM6total + tableparm.getDouble("PARM6", i);
					PARM7total = PARM7total + tableparm.getDouble("PARM7", i);
					PARM8total = PARM8total + tableparm.getDouble("PARM8", i);
					PARM9total = PARM9total + tableparm.getDouble("PARM9", i);
					//==end=== add by kangy 20170531
				}
				if(tableparm.getData("PARM1", i).equals("4")){
					parm.addData("PARM1", "充值");
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
					//==start=== add by kangy 20170531
					PARM5total = PARM5total + tableparm.getDouble("PARM5", i);
					PARM6total = PARM6total + tableparm.getDouble("PARM6", i);
					PARM7total = PARM7total + tableparm.getDouble("PARM7", i);
					PARM8total = PARM8total + tableparm.getDouble("PARM8", i);
					PARM9total = PARM9total + tableparm.getDouble("PARM9", i);
					//==end=== add by kangy 20170531
				}
				if(tableparm.getData("PARM1", i).equals("6")){
					parm.addData("PARM1", "退费");
					PARM2total = PARM2total - tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total - tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total - tableparm.getDouble("PARM4", i);
					//==start=== add by kangy 20170531
					PARM5total = PARM5total - tableparm.getDouble("PARM5", i);
					PARM6total = PARM6total - tableparm.getDouble("PARM6", i);
					PARM7total = PARM7total - tableparm.getDouble("PARM7", i);
					PARM8total = PARM8total + tableparm.getDouble("PARM8", i);
					PARM9total = PARM9total - tableparm.getDouble("PARM9", i);
					//==end=== add by kangy 20170531
				}
			}else{
				if(radio == 2){
					data.setData("TITLE2", "TEXT", "售卡汇总汇总表-付款方式");
					if("C0".equals(tableparm.getValue("PARM1",i))){
						parm.addData("PARM1", "现金");
					}
					if("C1".equals(tableparm.getValue("PARM1",i))){
						parm.addData("PARM1", "刷卡");
					}
					if("T0".equals(tableparm.getValue("PARM1",i))){
						parm.addData("PARM1","支票");
					}
					if("Z".equals(tableparm.getValue("PARM1",i))){
						parm.addData("PARM1","工商圈存机");
					}
					if("C2".equals(tableparm.getValue("PARM1",i))){
						parm.addData("PARM1", "汇票");
					}
					if("C4".equals(tableparm.getValue("PARM1",i))){
						parm.addData("PARM1", "应收款");
					}
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
				}else{
					data.setData("TITLE2", "TEXT", "售卡汇总汇总表-人员编号");
					parm.addData("PARM1", tableparm.getData("PARM1", i));
					PARM2total = PARM2total + tableparm.getDouble("PARM2", i);
					PARM3total = PARM3total + tableparm.getDouble("PARM3", i);
					PARM4total = PARM4total + tableparm.getDouble("PARM4", i);
					PARM5total = PARM5total + tableparm.getDouble("PARM5", i);
					PARM6total = PARM6total + tableparm.getDouble("PARM6", i);
					PARM7total = PARM7total + tableparm.getDouble("PARM7", i);
					PARM8total = PARM8total + tableparm.getDouble("PARM8", i);
					PARM9total = PARM9total + tableparm.getDouble("PARM9", i);
				}
				
			}
			parm.addData("PARM2", tableparm.getDouble("PARM2", i));
			parm.addData("PARM3", tableparm.getDouble("PARM3", i));
			parm.addData("PARM4", tableparm.getDouble("PARM4", i));
			if(radio!=2){
			parm.addData("PARM5", tableparm.getDouble("PARM5", i));
			parm.addData("PARM6", tableparm.getDouble("PARM6", i));
			parm.addData("PARM7", tableparm.getDouble("PARM7", i));
			parm.addData("PARM8", tableparm.getDouble("PARM8", i));
			parm.addData("PARM9", tableparm.getDouble("PARM9", i));
			}
		}
		if(radio == 1){
			data.setData("TITLE2", "TEXT", "售卡汇总汇总表-交易方式");
			data.setData("PARM1TITLE", "TEXT", "交易方式");
		}
		if(radio == 2){
			data.setData("TITLE2", "TEXT", "售卡汇总汇总表-付款方式");
			data.setData("PARM1TITLE", "TEXT", "付款方式");
		}
		if(radio == 3){
			data.setData("TITLE2", "TEXT", "售卡汇总汇总表-人员编号");
			data.setData("PARM1TITLE", "TEXT", "人员编号");
		}
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("PRINTDATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		if(!startDate.equals("")){
			data.setData("BUSINESSDATE", "TEXT", "交易时间: "+startDate.substring(0, 4)+
				"/"+startDate.substring(4, 6)+"/"+startDate.substring(6, 8)+" - "+
				endDate.substring(0, 4)+
				"/"+endDate.substring(4, 6)+"/"+endDate.substring(6, 8));
		}
		data.setData("PARM2TOTAL", "TEXT", PARM2total);
		data.setData("PARM3TOTAL", "TEXT", PARM3total);
		data.setData("PARM4TOTAL", "TEXT", PARM4total);
		if(radio!=2){// add by kangy 20170531
			data.setData("PARM5TOTAL", "TEXT", PARM5total);
			data.setData("PARM6TOTAL", "TEXT", PARM6total);
			data.setData("PARM7TOTAL", "TEXT", PARM7total);
			data.setData("PARM8TOTAL", "TEXT", PARM8total);
			data.setData("PARM9TOTAL", "TEXT", PARM9total);
		}
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		parm.setCount(parm.getCount("PARM1"));
		parm.addData("SYSTEM", "COLUMNS", "PARM1");
		parm.addData("SYSTEM", "COLUMNS", "PARM2");
		parm.addData("SYSTEM", "COLUMNS", "PARM3");
		parm.addData("SYSTEM", "COLUMNS", "PARM4");
		if(radio!=2){// add by kangy 20170531
			parm.addData("SYSTEM", "COLUMNS", "PARM5");
			parm.addData("SYSTEM", "COLUMNS", "PARM6");
			parm.addData("SYSTEM", "COLUMNS", "PARM7");
			parm.addData("SYSTEM", "COLUMNS", "PARM8");
			parm.addData("SYSTEM", "COLUMNS", "PARM9");
		}
		data.setData("TABLE", parm.getData());
		//==========modify by lim 2012/02/24 begin
		if(radio==2){
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTradeWay.jhw",data);
		}else{
			this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTradeWay2.jhw",data);

		}
		//==========modify by lim 2012/02/24 begin
    }
    /**
     * 清空
     */
    public void onClear(){
    	clearValue("START_DATE;END_DATE");
    }
    
}
