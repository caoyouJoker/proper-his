package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.sta.STATool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

public class INFOpeRateControl extends TControl {
	public void onInit() {
		super.onInit();
		initPage();
	}

	private void initPage() {
		this.clearValue("START_DATE;END_DATE;DEPT_CODE");
		Timestamp startime = STATool.getInstance().getLastMonth();
		Timestamp endtime = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", startime);
		this.setValue("END_DATE", endtime);
		this.callFunction("UI|TABLE|setParmValue", new TParm());

		// 设置区域
		this.setValue("REGION_CODE", "H01");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String regionCode = this.getValueString("REGION_CODE");
		String deptCode = this.getValueString("DEPT_CODE");
		String startDate = this.getValueString("START_DATE").substring(0, 10);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp endtime = SystemTool.getInstance().getDate();
		String endDate = sdf.format(endtime).substring(0, 10);
		//  根据科室、切口分类分组 查询
		String sql = " SELECT DEPT_CHN_DESC, "
				+ " CHN_DESC,"
				+ " WM_CONCAT (INF_OPESITE) AS INF_OPESITE ,"
				+ " SUM(AMOUNT1) AS AMOUNT1 ,"
				+ " SUM(AMOUNT2) AS AMOUNT2 "
				+ " FROM (SELECT D.DEPT_CHN_DESC,"
				+ "  E.CHN_DESC,"
				+ "  '' AS INF_OPESITE,"
				+ "  COUNT (A.OPBOOK_SEQ) AS AMOUNT1,"
				+ "  0 AS AMOUNT2"
				+ "  FROM OPE_OPBOOK A,"
				+ "  ADM_INP B,"
				+ "  SYS_OPERATIONICD C,"
				+ "  SYS_DEPT D,"
				+ "  SYS_DICTIONARY E"
				+ "   WHERE     A.CASE_NO = B.CASE_NO"
				+ "   AND A.OP_CODE1 = C.OPERATION_ICD"
				+ "   AND A.OP_DEPT_CODE = D.DEPT_CODE"
				+ "   AND C.OPE_INCISION = E.ID"
				+ "   AND E.GROUP_ID = 'OPE_INCISION'"
				+ "   AND C.OPE_INCISION IS NOT NULL  ";
		if(!"".equals(regionCode)){
			sql += " AND B.REGION_CODE = '"+ regionCode +"' ";
				}
			if(!"".equals(deptCode)){
				sql += " AND A.OP_DEPT_CODE = '"+ deptCode +"' "; 
			}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sql += " AND B.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
			}		
				sql += " GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC"
				+ " UNION"
				+ " SELECT D.DEPT_CHN_DESC,"
				+ "       E.CHN_DESC,"
				+ "       WM_CONCAT (A.INF_OPESITE) AS INF_OPESITE,"
				+ "       0 AS AMOUNT1,"
				+ "       COUNT (B.REPORT_NO) AS AMOUNT2"
				+ "  FROM SYS_OPERATIONICD A,"
				+ "       INF_CASE B,"
				+ "       ADM_INP C,"
				+ "       SYS_DEPT D,"
				+ "       SYS_DICTIONARY E"
				+ " WHERE     A.OPERATION_ICD = B.OP_CODE"
				+ "       AND B.CASE_NO = C.CASE_NO"
				+ "       AND B.DEPT_CODE = D.DEPT_CODE"
				+ "       AND A.OPE_INCISION = E.ID"
				+ "       AND E.GROUP_ID = 'OPE_INCISION'"
				+ "       AND A.OPE_INCISION IS NOT NULL"
				+ "       AND B.OPBOOK_SEQ IS NOT NULL";
		if(!"".equals(regionCode)){
			sql += " AND C.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(deptCode)){
			sql += " AND B.DEPT_CODE = '"+ deptCode +"' "; 
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sql += " AND C.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		sql += "	GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC" + " )"
				+ " GROUP BY DEPT_CHN_DESC, CHN_DESC"
				+ " ORDER BY  DEPT_CHN_DESC, CHN_DESC";
		System.out.println("sql:" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//根据科室分组查询
		String sqlDept = " SELECT DEPT_CHN_DESC, "
				+ " WM_CONCAT (INF_OPESITE) AS INF_OPESITE, "
				+ " SUM(AMOUNT1) AS  AMOUNT1 ,"
				+ " SUM(AMOUNT2) AS  AMOUNT2"
				+ " FROM (SELECT D.DEPT_CHN_DESC,"
				+ "  E.CHN_DESC,"
				+ "  '' AS INF_OPESITE,"
				+ "  COUNT (A.OPBOOK_SEQ) AS AMOUNT1,"
				+ "  0 AS AMOUNT2"
				+ "  FROM OPE_OPBOOK A,"
				+ "  ADM_INP B,"
				+ "  SYS_OPERATIONICD C,"
				+ "  SYS_DEPT D,"
				+ "  SYS_DICTIONARY E"
				+ "   WHERE     A.CASE_NO = B.CASE_NO"
				+ "   AND A.OP_CODE1 = C.OPERATION_ICD"
				+ "   AND A.OP_DEPT_CODE = D.DEPT_CODE"
				+ "   AND C.OPE_INCISION = E.ID"
				+ "   AND E.GROUP_ID = 'OPE_INCISION'"
				+ "   AND C.OPE_INCISION IS NOT NULL  ";
		if(!"".equals(regionCode)){
			sql += " AND B.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(deptCode)){
			sqlDept += " AND A.OP_DEPT_CODE = '"+ deptCode +"' "; 
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sqlDept += " AND B.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
						+ endDate
						+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		sqlDept += " GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC"
				+ " UNION"
				+ " SELECT D.DEPT_CHN_DESC,"
				+ "       E.CHN_DESC,"
				+ "       WM_CONCAT (A.INF_OPESITE) AS INF_OPESITE,"
				+ "       0 AS AMOUNT1,"
				+ "       COUNT (B.REPORT_NO) AS AMOUNT2"
				+ "  FROM SYS_OPERATIONICD A,"
				+ "       INF_CASE B,"
				+ "       ADM_INP C,"
				+ "       SYS_DEPT D,"
				+ "       SYS_DICTIONARY E"
				+ " WHERE     A.OPERATION_ICD = B.OP_CODE"
				+ "       AND B.CASE_NO = C.CASE_NO"
				+ "       AND B.DEPT_CODE = D.DEPT_CODE"
				+ "       AND A.OPE_INCISION = E.ID"
				+ "       AND E.GROUP_ID = 'OPE_INCISION'"
				+ "       AND A.OPE_INCISION IS NOT NULL"
				+ "       AND B.OPBOOK_SEQ IS NOT NULL";
		if(!"".equals(regionCode)){
			sql += " AND C.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(deptCode)){
			sqlDept += " AND B.DEPT_CODE = '"+ deptCode +"' "; 
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sqlDept += " AND C.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		sqlDept += "	GROUP BY D.DEPT_CHN_DESC, E.CHN_DESC" + " )"
				+ " GROUP BY DEPT_CHN_DESC" + " ORDER BY DEPT_CHN_DESC";
//		System.out.println("sql:" + sql);
		TParm parmDept = new TParm(TJDODBTool.getInstance().select(sqlDept));
		
		TParm parmShow = new TParm();
		
		double Sum1 = 0;// 合计统计手术例数
		double Sum2 = 0;// 合计统计感染例数
		double a = 0;//手术例数细项
		double b = 0;//感染例数细项
		double rate = 0;//例次感染率细项
		
		int bCount = 0;//总数表浅切口
		int sCount = 0;//总数深部切口
		int qCount = 0;//总数器官/腔隙
		int b1Count = 0;//Ⅰ类表浅切口
		int s1Count = 0;//Ⅰ类深部切口
		int q1Count = 0;//Ⅰ类器官/腔隙
		int b2Count = 0;//Ⅱ类表浅切口
		int s2Count = 0;//Ⅱ类深部切口
		int q2Count = 0;//Ⅱ类器官/腔隙
		int b3Count = 0;//III类表浅切口
		int s3Count = 0;//III类深部切口
		int q3Count = 0;//III类器官/腔隙
		int b4Count = 0;//IV类表浅切口
		int s4Count = 0;//IV类深部切口
		int q4Count = 0;//IV类器官/腔隙
		
		int opesite11 =0;//Ⅰ类手术例数
		int opesite12 =0;//Ⅰ类感染例数
		int opesite21 =0;//Ⅱ类手术例数
		int opesite22 =0;//Ⅱ类感染例数
		int opesite31 =0;//III类手术例数
		int opesite32 =0;//III类感染例数
		int opesite41 =0;//IV类手术例数
		int opesite42 =0;//IV类感染例数
		// 手术数循环
		for (int i = 0; i < parm.getCount(); i++) {
			String dept = parm.getValue("DEPT_CHN_DESC", i);
			String opeInsion = parm.getValue("CHN_DESC", i);
			String infOpesite = parm.getValue("INF_OPESITE", i);
			parmShow.addData("DEPT_CHN_DESC", dept);
			parmShow.addData("CHN_DESC", opeInsion);
			parmShow.addData("AMOUNT1", parm.getValue("AMOUNT1", i));
			parmShow.addData("AMOUNT2", parm.getValue("AMOUNT2", i));
			a = Double.parseDouble(parm.getValue("AMOUNT1", i));
			b = Double.parseDouble(parm.getValue("AMOUNT2", i));
			rate = b / a * 100;
			parmShow.addData("RATE", rate);
			Sum1 = Sum1 + a;
			Sum2 = Sum2 + b;
			//计算感染手术部位（表浅切口、深部切口、器官/腔隙）的数量
			if ("".equals(infOpesite)) {
				parmShow.addData("B_AMOUNT", 0);
				parmShow.addData("S_AMOUNT", 0);
				parmShow.addData("Q_AMOUNT", 0);
			}
			if (!infOpesite.contains(",")) {

				if ("01".equals(infOpesite)) {
					parmShow.addData("B_AMOUNT", 1);
					parmShow.addData("S_AMOUNT", 0);
					parmShow.addData("Q_AMOUNT", 0);
				} else if ("02".equals(infOpesite)) {
					parmShow.addData("S_AMOUNT", 1);
					parmShow.addData("B_AMOUNT", 0);
					parmShow.addData("Q_AMOUNT", 0);
				} else if ("03".equals(infOpesite)) {
					parmShow.addData("Q_AMOUNT", 1);
					parmShow.addData("B_AMOUNT", 0);
					parmShow.addData("S_AMOUNT", 0);
				}
			} else {
				String[] str = infOpesite.split(",");
				TParm result = getString(str);
				parmShow.addData("B_AMOUNT", result.getValue("B_AMOUNT"));
				parmShow.addData("S_AMOUNT", result.getValue("S_AMOUNT"));
				parmShow.addData("Q_AMOUNT", result.getValue("Q_AMOUNT"));
			}
			
			
			//切口分类 统计合计
			if("Ⅰ类（清洁）切口".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite11 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite12 +=amount2;
			}
			if("Ⅱ类（清洁-污染）切口".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite21 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite22 +=amount2;
			}
			if("Ⅲ类（污染）切口".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite31 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite32 +=amount2;
			}
			if("Ⅳ类（污秽-感染）切口".equals(parm.getValue("CHN_DESC",i))){
				int amount1 = Integer.parseInt(parm.getValue("AMOUNT1",i));
				opesite41 +=amount1;
				int amount2 = Integer.parseInt(parm.getValue("AMOUNT2",i));
				opesite42 +=amount2;
			}
			
			// 科室小计循环
			for (int j = 0; j < parmDept.getCount(); j++) {
				String deptJ = parmDept.getValue("DEPT_CHN_DESC", j);
				if (i < parm.getCount() - 1) {
					String deptNew = parm.getValue("DEPT_CHN_DESC", i + 1);
					//循环当前科室和下一个科室比较，如果不一样则插入科室小计：
					if (!deptNew.equals(dept) && dept.equals(deptJ)) {
						parmShow.addData("DEPT_CHN_DESC", deptJ);
						parmShow.addData("CHN_DESC", "小计");
						parmShow.addData("AMOUNT1", parmDept.getValue(
								"AMOUNT1", j));
						parmShow.addData("AMOUNT2", parmDept.getValue(
								"AMOUNT2", j));
						a = Double.parseDouble(parmDept.getValue("AMOUNT1", j));
						b = Double.parseDouble(parmDept.getValue("AMOUNT2", j));
						rate = b / a * 100;
						parmShow.addData("RATE", rate);
						
						String infOpesite1 = parmDept.getValue("INF_OPESITE", j);
						
						if ("".equals(infOpesite1)) {
							parmShow.addData("B_AMOUNT", 0);
							parmShow.addData("S_AMOUNT", 0);
							parmShow.addData("Q_AMOUNT", 0);
						}
						if (!infOpesite1.contains(",")) {

							if ("01".equals(infOpesite1)) {
								parmShow.addData("B_AMOUNT", 1);
								parmShow.addData("S_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("02".equals(infOpesite1)) {
								parmShow.addData("S_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("03".equals(infOpesite1)) {
								parmShow.addData("Q_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("S_AMOUNT", 0);
							}
							
						} else {
							String[] str = infOpesite1.split(",");
							TParm result = getString(str);
							parmShow.addData("B_AMOUNT", result.getValue("B_AMOUNT"));
							parmShow.addData("S_AMOUNT", result.getValue("S_AMOUNT"));
							parmShow.addData("Q_AMOUNT", result.getValue("Q_AMOUNT"));
						}
					}
				}
				//科室小计最后一个
				if (i == parm.getCount() - 1) {
					if (dept.equals(deptJ)) {
						parmShow.addData("DEPT_CHN_DESC", deptJ);
						parmShow.addData("CHN_DESC", "小计");
						parmShow.addData("AMOUNT1", parmDept.getValue(
								"AMOUNT1", j));
						parmShow.addData("AMOUNT2", parmDept.getValue(
								"AMOUNT2", j));
						a = Double.parseDouble(parmDept.getValue("AMOUNT1", j));
						b = Double.parseDouble(parmDept.getValue("AMOUNT2", j));
						rate = b / a * 100;
						parmShow.addData("RATE", rate);
						
						String infOpesite1 = parmDept.getValue("INF_OPESITE", j);
						if ("".equals(infOpesite1)) {
							parmShow.addData("B_AMOUNT", 0);
							parmShow.addData("S_AMOUNT", 0);
							parmShow.addData("Q_AMOUNT", 0);
						}
						if (!infOpesite1.contains(",")) {

							if ("01".equals(infOpesite1)) {
								parmShow.addData("B_AMOUNT", 1);
								parmShow.addData("S_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("02".equals(infOpesite1)) {
								parmShow.addData("S_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("Q_AMOUNT", 0);
							} else if ("03".equals(infOpesite1)) {
								parmShow.addData("Q_AMOUNT", 1);
								parmShow.addData("B_AMOUNT", 0);
								parmShow.addData("S_AMOUNT", 0);
							}

						} else {
							String[] str = infOpesite1.split(",");
							TParm result = getString(str);
							parmShow.addData("B_AMOUNT", result.getValue("B_AMOUNT"));
							parmShow.addData("S_AMOUNT", result.getValue("S_AMOUNT"));
							parmShow.addData("Q_AMOUNT", result.getValue("Q_AMOUNT"));
						}
					}
				}

			}
			
		}
//		System.out.println("parm:"+parm);
		
		//合计
		int bAmount = 0;//表浅切口总数量
		int sAmount = 0;//深部切口总数量
		int qAmount = 0;//器官/腔隙总数量
		//感染手术部位 总数量
		for(int i=0;i<parmShow.getCount("B_AMOUNT");i++){
			bAmount = Integer.parseInt(parmShow.getValue("B_AMOUNT",i));
			sAmount = Integer.parseInt(parmShow.getValue("S_AMOUNT",i));
			qAmount = Integer.parseInt(parmShow.getValue("Q_AMOUNT",i));
			//四类切口分类 感染手术部位  统计
			if("Ⅰ类（清洁）切口".equals(parmShow.getValue("CHN_DESC",i))){
//				messageBox_(parmShow.getValue("CHN_DESC",i));
				b1Count += bAmount;
				s1Count += sAmount;
				q1Count += qAmount;
			}
			if("Ⅱ类（清洁-污染）切口".equals(parmShow.getValue("CHN_DESC",i))){
				b2Count += bAmount;
				s2Count += sAmount;
				q2Count += qAmount;
			}
			if("Ⅲ类（污染）切口".equals(parmShow.getValue("CHN_DESC",i))){
				b3Count += bAmount;
				s3Count += sAmount;
				q3Count += qAmount;
			}
			if("Ⅳ类（污秽-感染）切口".equals(parmShow.getValue("CHN_DESC",i))){
				b4Count += bAmount;
				s4Count += sAmount;
				q4Count += qAmount;
			}
			bCount += bAmount;
			sCount += sAmount;
			qCount += qAmount;
		}
//		messageBox_(parmDept.getCount("DEPT_CHN_DESC"));
		if(parmDept.getCount("DEPT_CHN_DESC") > 1){
			//"I类(清洁)切口 统计
			double opesite11l = (int) opesite11;
			double opesite12l = (int) opesite12;
			parmShow.addData("DEPT_CHN_DESC", "全院合计");
			parmShow.addData("CHN_DESC", "I类(清洁)切口");
			parmShow.addData("AMOUNT1", opesite11);
			parmShow.addData("AMOUNT2", opesite12);
			if(!Double.isNaN(opesite12l / opesite11l * 100)){
				parmShow.addData("RATE", opesite12l / opesite11l * 100);
			}else{
				parmShow.addData("RATE", 0);
			}
			parmShow.addData("B_AMOUNT", b1Count);
			parmShow.addData("S_AMOUNT", s1Count);
			parmShow.addData("Q_AMOUNT", q1Count);
			//II类(清洁-污染)切口 统计
			double opesite21l = (int) opesite21;
			double opesite22l = (int) opesite22;
			parmShow.addData("DEPT_CHN_DESC", "全院合计");
			parmShow.addData("CHN_DESC", "II类(清洁-污染)切口");
			parmShow.addData("AMOUNT1", opesite21);
			parmShow.addData("AMOUNT2", opesite22);
			if(!Double.isNaN(opesite22l / opesite21l * 100)){
				parmShow.addData("RATE", opesite22l / opesite21l * 100);
			}else{
				parmShow.addData("RATE", 0);
			}
			parmShow.addData("B_AMOUNT", b2Count);
			parmShow.addData("S_AMOUNT", s2Count);
			parmShow.addData("Q_AMOUNT", q2Count);
			//III类(污染)切口 统计
			double opesite31l = (int) opesite31;
			double opesite32l = (int) opesite32;
			parmShow.addData("DEPT_CHN_DESC", "全院合计");
			parmShow.addData("CHN_DESC", "III类(污染)切口");
			parmShow.addData("AMOUNT1", opesite31);
			parmShow.addData("AMOUNT2", opesite32);
			if(!Double.isNaN(opesite32l / opesite31l * 100)){
				parmShow.addData("RATE", opesite32l / opesite31l * 100);
			}else{
				parmShow.addData("RATE", 0);
			}
			parmShow.addData("B_AMOUNT", b3Count);
			parmShow.addData("S_AMOUNT", s3Count);
			parmShow.addData("Q_AMOUNT", q3Count);
			//IV类(污秽-感染)切口 统计
			double opesite41l = (int) opesite41;
			double opesite42l = (int) opesite42;
			parmShow.addData("DEPT_CHN_DESC", "全院合计");
			parmShow.addData("CHN_DESC", "IV类(污秽-感染)切口");
			parmShow.addData("AMOUNT1", opesite41);
			parmShow.addData("AMOUNT2", opesite42);
			if(!Double.isNaN(opesite42l / opesite41l * 100) ){
				parmShow.addData("RATE", opesite42l / opesite41l * 100);
			}else{
				parmShow.addData("RATE", 0.00);
			}
			parmShow.addData("B_AMOUNT", b4Count);
			parmShow.addData("S_AMOUNT", s4Count);
			parmShow.addData("Q_AMOUNT", q4Count);
			
			//全院合计 
			int sum1 = (int) Sum1;
			int sum2 = (int) Sum2;
			parmShow.addData("DEPT_CHN_DESC", "全院合计");
			parmShow.addData("CHN_DESC", "总数");
			parmShow.addData("AMOUNT1", sum1);
			parmShow.addData("AMOUNT2", sum2);
			parmShow.addData("RATE", Sum2 / Sum1 * 100);
			parmShow.addData("B_AMOUNT", bCount/2);
			parmShow.addData("S_AMOUNT", sCount/2);
			parmShow.addData("Q_AMOUNT", qCount/2);
		}
		
//		System.out.println("parmShow:"+parmShow);
		this.getTable("TABLE").setParmValue(parmShow);
		if (this.getTable("TABLE").getRowCount() < 1) {
			// 查无数据
			this.messageBox("查无数据");
		}
	}

	/**
	 * 计算感染手术部位 对应数量
	 * @param s
	 * @return
	 */
	public TParm getString(String[] s) {
		int num1 = 0;
		int num2 = 0;
		int num3 = 0;
		for (int i = 0; i < s.length; i++) {
			if ("01".equals(s[i])) {
				num1++;
			} else if ("02".equals(s[i])) {
				num2++;
			} else if ("03".equals(s[i])) {
				num3++;
			}
		}
		TParm p = new TParm();
		p.setData("B_AMOUNT", num1);
		p.setData("S_AMOUNT", num2);
		p.setData("Q_AMOUNT", num3);
		return p;

	}

	/**
	 * 导出excel
	 */
	public void onExport() {
		if (getTable("TABLE").getRowCount() <= 0) {
			this.messageBox("没有导出数据");
			return;
		}
		INFOpeRateUtil.getInstance().exportExcel(getTable("TABLE"),
				"手术部位感染率统计表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
	}

	/**
	 * 取得Table控件
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}
}
