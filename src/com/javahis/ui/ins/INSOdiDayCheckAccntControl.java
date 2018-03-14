//package com.javahis.ui.ins;
//
//public class INSOdiDayCheckAccntControl {
//
//}
package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 住院医保日对账
 * </p>
 * 
 * <p>
 * Description: 住院医保日对账
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangs 20160115
 * @version 1.0
 */
public class INSOdiDayCheckAccntControl extends TControl {
	private TTable table;
	private TTable table2;
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		table=(TTable)this.getComponent("TABLE1");	
		table2=(TTable)this.getComponent("TABLE2");
		setValue("CTZ_CODE", 1);
		setValue("START_DATE", SystemTool.getInstance().getDate());
		setValue("END_DATE", SystemTool.getInstance().getDate());
		addListener(table);
		addListenerOne(table2);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String regionCode = Operator.getRegion();// 区域
		String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = startDate;//getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		} else {
			messageBox("请输入查询条件");
			return;
		}
//		就诊序号,100;资格确认书号,100;期号,100;姓名,100;人员类别,100;发生金额,100,double,######0.00;申报金额,100,double,######0.00;全自费金额,100,double,######0.00;增付金额,100,double,######0.00;明细数量,100
//		ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;TOT_AMT;OWN_AMT;ADD_AMT;NHI_AMT;COUNT
//		ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMT
		int ctz = getValueInt("CTZ_CODE");
		String sql = 
			" SELECT A.ADM_SEQ,B.CONFIRM_NO,to_char(A.CHARGE_DATE,'YYYYMMDD') BILL_DATE, "+
			" B.PAT_NAME,C.CTZ_DESC,SUM(case when A.NHI_ORDER_CODE='***018' then 0 else A.TOTAL_AMT end ) TOT_AMT, "+
			" SUM(A.OWN_AMT) OWN_AMT,SUM(A.ADDPAY_AMT) ADD_AMT, "+
			" SUM(A.TOTAL_NHI_AMT) NHI_AMT,COUNT(A.ORDER_CODE) COUNT,to_char(A.UP_DATE,'YYYYMMDD') YEAR_MON "+
			" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B,SYS_CTZ C "+
			" WHERE A.UP_DATE between to_date('"+startDate+"','YYYYMMDDHH24MISS') and to_date('"+endDate+"','YYYYMMDDHH24MISS') "+
//			" AND A.NHI_ORDER_CODE!='***018' "+
			" AND A.UP_FLG='2' "+
			" AND A.REGION_CODE='"+regionCode+"' "+
			" AND B.ADM_SEQ=A.ADM_SEQ "+
			" AND C.CTZ_CODE=B.HIS_CTZ_CODE "+
			" AND C.NHI_CTZ_FLG='Y' ";
		if (ctz == 1) {
			sql = sql + " AND C.MRO_CTZ= '1'";// 城职
		}
		if (ctz == 2) {
			sql = sql + " AND C.MRO_CTZ= '2'";// 城居
		}
		sql = sql + 
			" GROUP BY A.ADM_SEQ,B.CONFIRM_NO,to_char(A.CHARGE_DATE,'YYYYMMDD'),to_char(A.UP_DATE,'YYYYMMDD'),B.PAT_NAME, "+
			" C.CTZ_DESC "+
			" ORDER BY A.ADM_SEQ ";
//System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("查无数据");
		}
		this.callFunction("UI|TABLE1|setParmValue", result);
	}

	/**
	 * 对总账
	 */
	public void onCheckAll() {
		boolean flg=true;
		String regionCode = Operator.getRegion();// 区域
		String startDate = "";
		String endDate = "";
		int ctz = getValueInt("CTZ_CODE");
		if (!"".equals(this.getValueString("START_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = startDate;//getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		} else {
			messageBox("请输入查询条件");
			return;
		}
		TParm result = queryCheckAll(regionCode, startDate, endDate, ctz);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("无数据");
			return;
		}
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		TParm parm = new TParm();
		parm.addData("HOSP_NHI_NO", hospital);// 医院编码
		parm.addData("OPT_USER", null);// 操作员编码
		parm.addData("CHECK_DATE", startDate.substring(0, 8));// 对账时间
//		parm.addData("CHECK_DATE", "20160309");// 对账时间
		// 传参
		parm.addData("TOT_AMT", result.getData("TOTAL_AMT", 0));// 发生金额
		parm.addData("OWN_AMT", result.getData("OWN_AMT", 0));// 全自费金额
		parm.addData("ADD_AMT", result.getData("ADDPAY_AMT", 0));// 增负金额
		parm.addData("NHI_AMT", result.getData("TOTAL_NHI_AMT", 0));// 申报金额
		parm.addData("SUM_COUNT_P", result.getData("SUM_COUNT_P", 0));// 总人次
		parm.addData("SUM_COUNT_FEE", result.getData("SUM_COUNT_FEE", 0));// 医院明细总数

		if (ctz == 1) {// 城职
			parm.setData("PIPELINE", "DataDown_ssks");
			parm.setData("PLOT_TYPE", "D");
		}
		if (ctz == 2) {// 城居
			parm.setData("PIPELINE", "DataDown_csks");
			parm.setData("PLOT_TYPE", "D");
		}
		parm.addData("PARM_COUNT", 9);
		result = InsManager.getInstance().safe(parm);// 医保卡接口方法
//		System.out.println("对总账:::PIPELINE=DataDown_yb:::PLOT_TYPE=K:::出参===="
//				+ result);
		if (!INSTJTool.getInstance().getErrParm(result)) {
		    messageBox(result.getErrText());
		    return;
		}
		
		TTable table = (TTable) this.getComponent("TABLE1");// TABLE1
	    TParm tempParm=table.getParmValue();
	    String admSeq="";
	    for(int i=0;i<tempParm.getCount();i++){
//	    tempParm.getValue("ADM_SEQ", i);
	    	if(admSeq.equals(tempParm.getValue("ADM_SEQ", i))){
	    		continue;
	    	}
	    	admSeq=tempParm.getValue("ADM_SEQ", i);
			String sql=" UPDATE INS_IBS_UPLOAD SET "+
	        " INS_FLG='3', "+
	        " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
	        " WHERE ADM_SEQ='"+admSeq+"' "+
	        " AND UP_DATE between to_date('"+startDate+"','YYYYMMDDHH24MISS') and to_date('"+endDate+"','YYYYMMDDHH24MISS') "+
	        //" AND INS_FLG='0' "+
	        " AND UP_FLG='2' "+
	        " AND REGION_CODE='"+regionCode+"' ";
//	     System.out.println("onInsItemRegDown_sql:"+sql);
	     TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));
//	     System.out.println("onInsItemRegDown_sql:"+result1.getErrCode());

	     if (result1.getErrCode() < 0) {
//	    	 messageBox(result1.getErrText());
	    	 flg=false;
	     }
	     
	    }
	    if(flg){
	    	messageBox(result.getValue("PROGRAM_MESSAGE"));
	    }else{
	    	messageBox(result.getValue("PROGRAM_MESSAGE")+",本地数据状态更新失败");
	    }

	}

	/**
	 * 对总账查询
	 * 
	 * @param regionCode
	 * @param startDate
	 * @param endDate
	 * @param ctz
	 * @return
	 */
	public TParm queryCheckAll(String regionCode, String startDate,
			String endDate, int ctz) {
		String sql = 
			" SELECT (SUM(M.TOTAL_AMT)) TOTAL_AMT,SUM(M.OWN_AMT) OWN_AMT,SUM(M.ADDPAY_AMT) ADDPAY_AMT, "+
			" SUM(M.TOTAL_NHI_AMT) TOTAL_NHI_AMT,COUNT(SUM_COUNT_P) SUM_COUNT_P,SUM(SUM_COUNT_P) SUM_COUNT_FEE "+
			" FROM ( "+
			" SELECT B.CONFIRM_NO,SUM(case when A.NHI_ORDER_CODE='***018' then 0 else A.TOTAL_AMT end) TOTAL_AMT,SUM(A.OWN_AMT) OWN_AMT, "+
			" SUM(A.ADDPAY_AMT) ADDPAY_AMT,SUM(A.TOTAL_NHI_AMT) TOTAL_NHI_AMT, "+
			" COUNT(A.ORDER_CODE) SUM_COUNT_P "+
			" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B,SYS_CTZ C "+
			" WHERE A.UP_DATE between to_date('"+startDate+"','YYYYMMDDHH24MISS') and to_date('"+endDate+"','YYYYMMDDHH24MISS') "+
//			" AND A.NHI_ORDER_CODE!='***018' "+
			" AND A.REGION_CODE='"+regionCode+"' "+
			" AND A.UP_FLG='2' "+
			" AND B.ADM_SEQ=A.ADM_SEQ "+
			" AND C.CTZ_CODE=B.HIS_CTZ_CODE "+
			" AND C.NHI_CTZ_FLG='Y' ";
		if (ctz == 1) {
			sql = sql + " AND C.MRO_CTZ= '1'";// 城职
		}
		if (ctz == 2) {
			sql = sql + " AND C.MRO_CTZ= '2'";// 城居
		}
		sql = sql+" GROUP BY B.CONFIRM_NO,TO_CHAR(A.CHARGE_DATE,'YYYYMMDD') ) M ";
//System.out.println("queryCheckAll:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 对明细账
	 */
	public void onCheckDetailAccnt() {
		String startDate = "";
		String endDate = "";
		int ctz = getValueInt("CTZ_CODE");
		if (!"".equals(this.getValueString("START_DATE"))
//				&& !"".equals(this.getValueString("END_DATE"))
				) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = startDate;//getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		}
		//onQuery();// 查询table1
		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		TTable table2 = (TTable) this.getComponent("TABLE2");// TABLE2
		if (ctz == 1) {
			parm = new TParm();
			// 传参
			parm.setData("PIPELINE", "DataDown_sskd");
			parm.setData("PLOT_TYPE", "A");
			table2
//					.setHeader("就诊序号,100;资格确认书号,100;期号,100;姓名,100;人员类别,100;支付类别,100;"
//							+ "发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;"
//							+ "全自费金额,100,double,#########0.00;增负金额,100,double,#########0.00;"
//							+ "个人账户支付金额,100,double,#########0.00");
			.setHeader("就医顺序号,100;资格确认书号,100;费用发生时间,100;个人编码,100;住院科室,100;医师编码,100;操作员编码,100;"
			+ "发生金额,100,double,#########0.00;自费金额,100,double,#########0.00;"
			+ "增负金额,100,double,#########0.00;申报金额,100,double,#########0.00;"
			+ "明细总条数,100,double,#########0.00");
			
			table2
					.setParmMap("ADM_SEQ;CONFIRM_NO;BILL_DATE;OWN_NO;DEPT_DESC;DR_CODE;"
							+ "OPT_USER;TOT_AMT;OWN_AMT;ADD_AMT;NHI_AMT;SUM_COUNT");
		}
		if (ctz == 2) {
			parm = new TParm();
			// 传参
			parm.setData("PIPELINE", "DataDown_cskd");
			parm.setData("PLOT_TYPE", "A");
			table2
			.setHeader("资格确认书号,100;费用发生时间,100;个人编码,100;住院科室,100;医师编码,100;操作员编码,100;"
					+ "发生金额,100,double,#########0.00;自费金额,100,double,#########0.00;"
					+ "增负金额,100,double,#########0.00;申报金额,100,double,#########0.00;"
					+ "明细总条数,100,double,#########0.00");
					
					table2
							.setParmMap("ADM_SEQ;CONFIRM_NO;BILL_DATE;OWN_NO;DEPT_DESC;DR_CODE;"
									+ "OPT_USER;TOT_AMT;OWN_AMT;ADD_AMT;NHI_AMT;SUM_COUNT");

		}
		parm.addData("HOSP_NHI_NO", hospital);// 医院编码
		parm.addData("START_DATE", startDate.substring(0,8));// 开始时间
		parm.addData("END_DATE", endDate.substring(0,8));// 结束时间
//		parm.addData("START_DATE", "20160307");// 开始时间
//		parm.addData("END_DATE", "20160307");// 结束时间
		parm.addData("PARM_COUNT", 3);
		TParm result = InsManager.getInstance().safe(parm, null);// 医保卡接口方法(复数)
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}else{
			this.messageBox("明细下载成功");
		}
//		table2.setParmValue(result);
		this.callFunction("UI|TABLE2|setParmValue", result);
		countDetail();
	}

	/**
	 * 计算明细账差别
	 */
	public void countDetail() {
		TTable table1 = (TTable) this.getComponent("TABLE1");// TABLE1
		TTable table2 = (TTable) this.getComponent("TABLE2");// TABLE2
		if (table1.getParmValue() == null || table2.getParmValue() == null) {
			messageBox("对账数据不能为空");
			return;
		}
		// ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMT
		TParm tableParm1 = table1.getParmValue();
		TParm tableParm2 = table2.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < tableParm1.getCount(); i++) {
			String confirmNoLocal = tableParm1.getData("CONFIRM_NO", i)
					.toString().trim();
			String billDateLocal = tableParm1.getData("BILL_DATE", i)
			.toString().trim();
			boolean canfind = false;
//			System.out.println("confirmNoLocal:"+confirmNoLocal);
//			System.out.println("confirmNoLocal:"+tableParm2);
			for (int j = 0; j < tableParm2.getCount("CONFIRM_NO"); j++) {
				String confirmNoCenter = tableParm2.getData("CONFIRM_NO", j)
						.toString().trim();
				String billDateCenter = tableParm2.getData("BILL_DATE", j)
				.toString().trim();
//				System.out.println("confirmNoCenter:"+confirmNoCenter);
				if (!confirmNoLocal.equals(confirmNoCenter)){
					continue;
				}
				if (!billDateLocal.equals(billDateCenter)){
					continue;
				}
				canfind = true;
				// 本地金额
				double totAmtLocal = tableParm1.getDouble("TOT_AMT", i);// 发生金额
				double nhiAmtLocal = tableParm1.getDouble("NHI_AMT", i);// 申报金额
				double ownAmtLocal = tableParm1.getDouble("OWN_AMT", i);// 全自费金额
				double addAmtLocal = tableParm1.getDouble("ADD_AMT", i);// 增付金额
				// 中心端金额
				double totAmtCenter = tableParm2.getDouble("TOT_AMT", j);// 发生金额
				double nhiAmtCenter = tableParm2.getDouble("NHI_AMT", j);// 申报金额
				double ownAmtCenter = tableParm2.getDouble("OWN_AMT", j);// 全自费金额
				double addAmtCenter = tableParm2.getDouble("ADD_AMT", j);// 增付金额
				if (totAmtLocal != totAmtCenter || nhiAmtLocal != nhiAmtCenter
						|| ownAmtLocal != ownAmtCenter
						|| addAmtLocal != addAmtCenter) {
					parm.addData("STATUS_ONE", "Y");
					parm.addData("STATUS_TWO", "N");
					parm.addData("STATUS_THREE", "N");
					parm.addData("ADM_SEQ", tableParm1.getData("ADM_SEQ", i));
					parm.addData("CONFIRN_NO", tableParm1.getData("CONFIRM_NO",
							i));
					parm.addData("YEAR_MON", tableParm1.getData("BILL_DATE", i));
					parm.addData("NAME", tableParm1.getData("PAT_NAME", i));
					parm.addData("TOT_AMT_LOCAL", tableParm1
							.getData("TOT_AMT", i));
					parm.addData("TOT_AMT_CENTER", tableParm2.getData(
							"TOT_AMT", j));
					parm.addData("NHI_AMT_LOCAL", tableParm1
							.getData("NHI_AMT", i));
					parm.addData("NHI_AMT_CENTER", tableParm2.getData(
							"NHI_AMT", j));
					parm.addData("OWN_AMT_LOCAL", tableParm1.getData("OWN_AMT",
							i));
					parm.addData("OWN_AMT_CENTER", tableParm2.getData(
							"OWN_AMT", j));
					parm.addData("ADD_AMT_LOCAL", tableParm1.getData("ADD_AMT",
							i));
					parm.addData("ADD_AMT_CENTER", tableParm2.getData(
							"ADD_AMT", j));
				}
			}
			if (!canfind) {
				parm.addData("STATUS_ONE", "N");
				parm.addData("STATUS_TWO", "Y");
				parm.addData("STATUS_THREE", "N");
				parm.addData("ADM_SEQ", tableParm1.getData("ADM_SEQ", i));
				parm.addData("CONFIRN_NO", tableParm1.getData("CONFIRM_NO", i));
				parm.addData("YEAR_MON", tableParm1.getData("BILL_DATE", i));
				parm.addData("NAME", tableParm1.getData("PAT_NAME", i));
				parm.addData("TOT_AMT_LOCAL", tableParm1.getData("TOT_AMT", i));
				parm.addData("TOT_AMT_CENTER", 0);
				parm.addData("NHI_AMT_LOCAL", tableParm1.getData("NHI_AMT", i));
				parm.addData("NHI_AMT_CENTER", 0);
				parm.addData("OWN_AMT_LOCAL", tableParm1.getData("OWN_AMT", i));
				parm.addData("OWN_AMT_CENTER", 0);
				parm.addData("ADD_AMT_LOCAL", tableParm1.getData("ADD_AMT", i));
				parm.addData("ADD_AMT_CENTER", 0);
			}
		}
		for (int i = 0; i < tableParm2.getCount(); i++) {
			String confirmNoCenter = tableParm2.getData("CONFIRM_NO", i)
					.toString();
			boolean canfind = false;
			for (int j = 0; j < tableParm1.getCount(); j++) {
				String confirmNoLocal = tableParm1.getData("CONFIRM_NO", i)
						.toString();
				if (!confirmNoLocal.equals(confirmNoCenter))
					continue;
				canfind = true;
			}
			if (!canfind) {
				parm.addData("STATUS_ONE", "N");
				parm.addData("STATUS_TWO", "N");
				parm.addData("STATUS_THREE", "Y");
				parm.addData("ADM_SEQ", tableParm2.getData("ADM_SEQ", i));
				parm.addData("CONFIRN_NO", tableParm2.getData("CONFIRN_NO", i));
				parm.addData("YEAR_MON", tableParm2.getData("BILL_DATE", i));
				parm.addData("NAME", "");
				parm.addData("TOT_AMT_LOCAL", 0);
				parm.addData("TOT_AMT_CENTER", tableParm2.getData("TOT_AMT",
						i));
				parm.addData("NHI_AMT_LOCAL", 0);
				parm.addData("NHI_AMT_CENTER", tableParm2.getData(
						"NHI_AMT", i));
				parm.addData("OWN_AMT_LOCAL", 0);
				parm
						.addData("OWN_AMT_CENTER", tableParm2.getData(
								"OWN_AMT", i));
				parm.addData("ADD_AMT_LOCAL", 0);
				parm.addData("ADD_AMT_CENTER", tableParm2.getData("ADD_AMT",
						i));
			}
		}
		parm.addData("CTZ",getValueInt("CTZ_CODE"));
		if (parm.getCount("CONFIRN_NO") <= 0) {
			messageBox("对明细帐成功");
			return;
		}
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSOdiDayCheckDetail.x", parm);
	}

	/**
	 * 清空
	 */
	public void onclear() {
		clearValue("CTZ_CODE;START_DATE");
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table2.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table2.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table2.getParmMap(sortColumnOne);
				// 转成parm中的列
				int col = tranParmColIndexOne(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// 将排序后的vector转成parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}
	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	public void  insDayRevoke(){
		
		TTable table1 = (TTable) this.getComponent("TABLE1");// TABLE1
		int row=table1.getSelectedRow();
		if (row == -1) {
			messageBox("未选中需撤销的记录");
			return;
		}
		// ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMT
		TParm tableParm1 = table1.getParmValue();
		int ctz=getValueInt("CTZ_CODE");
//		if(row!=-1){
//		String admSeq=tableParm1.getData("ADM_SEQ", row).toString();
//		String confirnNo=tableParm1.getData("CONFIRM_NO", row).toString();
//		String billDate=tableParm1.getData("BILL_DATE", row).toString();
		this.dataDown_ssks_C(tableParm1.getData("ADM_SEQ", row).toString(),
				tableParm1.getData("CONFIRM_NO", row).toString(),
				tableParm1.getData("BILL_DATE", row).toString(), 
				ctz);
//		}else{
//			for(int i=0;i<tableParm1.getCount();i++){
//				this.dataDown_ssks_C(tableParm1.getData("ADM_SEQ", i).toString(),
//						tableParm1.getData("CONFIRM_NO", i).toString(),
//						tableParm1.getData("BILL_DATE", i).toString(), 
//						ctz);
//			}
//		}
		
	}
private void dataDown_ssks_C(String admSeq,String confirnNo,
		String billDate,int ctz){
	TParm parm = new TParm();
	TParm regionParm = SYSRegionTool.getInstance().selectdata(
			Operator.getRegion());
	String hospital = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
	// 传参
	if(ctz==1){
	    parm.setData("PIPELINE", "DataDown_ssks");
	    parm.setData("PLOT_TYPE", "C");	
	}else if(ctz==2){
		parm.setData("PIPELINE", "DataDown_csks");
		parm.setData("PLOT_TYPE", "C");	
	}
	parm.addData("CONFIRM_NO", confirnNo);// 开始时间
	parm.addData("BILL_DATE", billDate.substring(0, 4)+"-"+
			                  billDate.substring(4, 6)+"-"+
			                  billDate.substring(6, 8));// 结束时间
//	parm.addData("BILL_DATE", "2016-03-08");// 结束时间
	parm.addData("HOSP_NHI_NO", hospital);// 医院编码
	parm.addData("PARM_COUNT", 3);
//	System.out.println("dataDown_ssks_C:"+parm);
	TParm result = InsManager.getInstance().safe(parm, null);// 医保卡接口方法(复数)
//	if (result.getErrCode() < 0) {
//		messageBox(result.getErrText());
//		return;
//	}
	String sql=" UPDATE INS_IBS_UPLOAD SET "+
    " INS_FLG='0',UP_FLG='3',UP_DATE=NULL, "+
    " OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' "+
    " WHERE ADM_SEQ='"+admSeq+"' " +
    " AND CHARGE_DATE between TO_DATE('"+billDate+"000000','YYYYMMDDHH24MISS') "+
    " AND TO_DATE('"+billDate+"235959','YYYYMMDDHH24MISS') "+
    //" AND INS_FLG='3' "+
    " AND UP_FLG='2' ";

// System.out.println("onInsItemRegDown_sql:"+sql);
 TParm result2 = new TParm(TJDODBTool.getInstance().update(sql));
// System.out.println("onInsItemRegDown_sql:"+);
// System.out.println("onInsItemRegDown_sql:"+result2.getErrCode());

 if (result2.getErrCode() < 0) {
	 this.messageBox("医保接口调用成功,本地状态更新失败");	 
 }else{
	 this.messageBox("撤销成功");
 }
		
	
}

}
