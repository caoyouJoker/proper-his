package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title:医保住院医疗费申请支付信息导出EXCLE
 * </p>
 * 
 * <p>
 * Description:医保住院医疗费申请支付信息导出EXCLE
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangb 2012-8-8
 * @version 4.0
 */
public class INSIbsMaterialSearchControl extends TControl{
	private TTable table; // 病患基本信息列表
	//======排序
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE"); // 病患基本信息列表
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		addListener(table);
	}
	private String sql ="SELECT A.MR_NO,C.IPD_NO, A.PAT_NAME, E.CHN_DESC,"+
	       "A.PAT_AGE, F.CTZ_DESC,"+
	       "TO_CHAR (A.IN_DATE, 'YYYYMMDD') AS IN_DATE,"+
	       "TO_CHAR (A.DS_DATE, 'YYYYMMDD') AS DS_DATE,"+
	      "CASE WHEN TRUNC (A.DS_DATE, 'DD') - TRUNC (A.IN_DATE, 'DD') = 0 THEN 1 "+
	      "ELSE TRUNC (A.DS_DATE, 'DD') - TRUNC (A.IN_DATE, 'DD') END AS IN_DAYS,"+
	      "A.DEPT_DESC, A.PHA_AMT, A.EXM_AMT,"+
	      "A.TREAT_AMT, A.OP_AMT, A.BED_AMT,"+
	      "A.MATERIAL_AMT, A.BLOODALL_AMT,"+
	      "A.BLOOD_AMT, A.OTHER_AMT,"+
	      "A.PHA_AMT+ A.EXM_AMT+ A.TREAT_AMT+ A.OP_AMT+ A.BED_AMT+ A.MATERIAL_AMT"+
	      "+ A.BLOODALL_AMT+ A.BLOOD_AMT+ A.OTHER_AMT AS SUM_AMT,"+
	      "A.OWN_AMT,"+
	      "A.ADD_AMT,"+
	      
	      " CASE WHEN B.SDISEASE_CODE IS NULL THEN A.RESTART_STANDARD_AMT+ A.STARTPAY_OWN_AMT+ A.OWN_AMT+ A.PERCOPAYMENT_RATE_AMT "+
	      " + A.ADD_AMT+ A.INS_HIGHLIMIT_AMT ELSE A.RESTART_STANDARD_AMT+A.STARTPAY_OWN_AMT+CASE WHEN A.BED_SINGLE_AMT IS NULL THEN 0 ELSE A.BED_SINGLE_AMT END + CASE WHEN A.MATERIAL_SINGLE_AMT IS NULL THEN 0 ELSE A.MATERIAL_SINGLE_AMT END END AS OWN_SUM_AMT, "+//个人合计
	      
	      " CASE WHEN B.SDISEASE_CODE IS NULL THEN A.RESTART_STANDARD_AMT+ A.STARTPAY_OWN_AMT+ A.OWN_AMT + A.PERCOPAYMENT_RATE_AMT "+
	      " + A.ADD_AMT + A.INS_HIGHLIMIT_AMT - A.ILLNESS_SUBSIDY_AMT - "+
	      " (CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END) - A.ARMYAI_AMT ELSE A.RESTART_STANDARD_AMT+A.STARTPAY_OWN_AMT+CASE WHEN A.BED_SINGLE_AMT IS NULL THEN 0 ELSE A.BED_SINGLE_AMT END + CASE WHEN A.MATERIAL_SINGLE_AMT IS NULL THEN 0 ELSE A.MATERIAL_SINGLE_AMT-CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END - A.ILLNESS_SUBSIDY_AMT- A.ARMYAI_AMT END END AS SJZFJE, "+//实际自负金额
	      
	      "A.NHI_PAY,A.NHI_PAY_REAL,A.NHI_COMMENT AS HOSP_APPLY_AMT,A.NHI_COMMENT," +
	      "A.ILLNESS_SUBSIDY_AMT AS HOSP_ILLNESS_SUBSIDY_AMT,A.ILLNESS_SUBSIDY_AMT,A.ACCOUNT_PAY_AMT ,"+
	      "A.ACCOUNT_PAY_AMT AS GRZHZF,"+
	      "CASE WHEN A.NHI_PAY  IS NULL THEN 0 ELSE A.NHI_PAY END+" +
	      "CASE WHEN A.NHI_COMMENT IS NULL THEN 0 ELSE A.NHI_COMMENT END+" +
          "CASE WHEN A.ARMYAI_AMT IS NULL THEN 0 ELSE A.ARMYAI_AMT END+" +
          "CASE WHEN A.ACCOUNT_PAY_AMT IS NULL THEN 0 ELSE A.ACCOUNT_PAY_AMT END+" +
          "CASE WHEN A.ILLNESS_SUBSIDY_AMT  IS NULL THEN 0 ELSE  A.ILLNESS_SUBSIDY_AMT END AS HJZFJE,"+
	      "K.OP_DESC, (SELECT CASE WHEN SUM(L.TOTAL_AMT) IS NULL THEN 0 ELSE SUM(L.TOTAL_AMT) END "+
	  	  "FROM INS_IBS_ORDER  L, SYS_FEE M "+
		  "WHERE  L.YEAR_MON=A.YEAR_MON AND  L.CASE_NO=A.CASE_NO "+
		  "AND L.ORDER_CODE=M.ORDER_CODE  AND  M.OWN_PRICE>=100  "+
		  "AND  M.ORDER_CAT1_CODE ='PHA_W' "+ 
		  "AND  M.ACTIVE_FLG ='Y' ) AS SPHA, (SELECT CASE WHEN SUM(N.TOTAL_AMT) IS NULL THEN 0 ELSE SUM(N.TOTAL_AMT) END "+
	  	  "FROM INS_IBS_ORDER  N, SYS_FEE O "+
		  "WHERE  N.YEAR_MON=A.YEAR_MON AND  N.CASE_NO=A.CASE_NO "+
		  "AND N.ORDER_CODE=O.ORDER_CODE  AND  O.OWN_PRICE>=500  "+
		  "AND  O.ORDER_CAT1_CODE ='MAT' "+ 
		  "AND  O.ACTIVE_FLG ='Y' ) AS SMATERIAL_AMT "+
		  //////////////////////////////////////////////////zhangs 20170426 add
		  " ,D.USER_NAME ZYYS,G.CHN_DESC CBQX,A.DIAG_CODE||A.DIAG_DESC MAIN_DIAG,B.SDISEASE_CODE,L.CHN_DESC SDISEASE_DESC,M.USER_NAME MAIN_SUGEON_DESC, "+
		  " CASE WHEN B.SDISEASE_CODE IS NOT NULL THEN A.SINGLE_SUPPLYING_AMT ELSE 0 END SINGLE_SUPPLYING_AMT, "+ 
		  " A.RESTART_STANDARD_AMT, "+
		  " CASE WHEN B.SDISEASE_CODE IS NULL THEN A.STARTPAY_OWN_AMT ELSE 0 END STARTPAY_OWN_AMT, "+
		  " CASE WHEN B.SDISEASE_CODE IS NOT NULL THEN A.STARTPAY_OWN_AMT ELSE 0 END NHI_OWN_PAY, "+
		  " CASE WHEN A.BED_SINGLE_AMT IS NULL THEN 0 ELSE A.BED_SINGLE_AMT END + CASE WHEN A.MATERIAL_SINGLE_AMT IS NULL THEN 0 ELSE A.MATERIAL_SINGLE_AMT END AS TX_OWN_AMT "+
		  ////////////////////////////////////////////////////zhangs 20170426 add
	   "FROM INS_IBS A LEFT JOIN INS_ADM_CONFIRM B ON A.CONFIRM_NO = B.CONFIRM_NO "+
	       "LEFT JOIN ADM_INP C "+
	       "ON  A.CASE_NO = C.CASE_NO "+
	       "LEFT JOIN SYS_OPERATOR D "+
	       "ON C.VS_DR_CODE = D.USER_ID "+
	       "LEFT JOIN SYS_DICTIONARY E ON A.SEX_CODE = E.ID "+
	       "AND E.GROUP_ID = 'SYS_SEX' "+
	       "INNER JOIN SYS_CTZ F "+
	       "ON B.HIS_CTZ_CODE = F.CTZ_CODE " +   
	       "LEFT JOIN SYS_DICTIONARY G "+
	       "ON A.INSBRANCH_CODE = G.ID AND G.GROUP_ID = 'INS_FZX' "+
	       "LEFT JOIN SYS_DICTIONARY H "+
	       "ON A.SPEDRS_CODE = H.ID AND H.GROUP_ID = 'INS_MTLBA' "+
	       "LEFT JOIN SYS_DICTIONARY I "+
	       "ON I.GROUP_ID = 'INS_JYLB' AND I.ID = A.ADM_CATEGORY "+
	       "LEFT JOIN SYS_DICTIONARY J "+
	       "ON J.GROUP_ID = 'SP_PRESON_TYPE' AND J.ID = B.SPECIAL_PAT "+
	       "LEFT JOIN MRO_RECORD_OP K ON K.MAIN_FLG = 'Y' "+
	       "AND K.CASE_NO = A.CASE_NO "+
	       ////////////////////////////////////
	       " LEFT JOIN SYS_DICTIONARY L ON L.GROUP_ID = 'SIN_DISEASE' AND L.ID = B.SDISEASE_CODE LEFT JOIN SYS_OPERATOR M ON K.MAIN_SUGEON = M.USER_ID ";
	       //////////////////////////////////////
	//病案号,100;病患名称,100;性别,100;年龄,100;身份,100;入院日期,100;
	//出院日期,100;住院天数,100;科室,100;药品费,100;检查费,100;治疗费,100;
	//手术费,100;床位费,100;医用材料,100;输全血,100;成分输血,100;其他,100;合计,100;
	//自费金额,100;增负金额,100;个人合计,100;实际自负金额,100;医保基金_医疗机构申请金额,100;
	//医保基金_社保支付金额,100;大额救助_医疗机构申请金额,100;大额救助_社保建议支付金额,100;
	//个人账户支付_医疗机构申请金额,100;个人账户支付_社保支付金额,100;合计支付_社保支付合计金额,100;术式,100
	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (null == this.getValue("OUT_DATE_START")
				|| this.getValue("OUT_DATE_START").toString().length() <= 0
				|| null == this.getValue("OUT_DATE_END")
				|| this.getValue("OUT_DATE_END").toString().length() <= 0) {

			if (null == this.getValue("OUT_DATE_START")
					|| this.getValue("OUT_DATE_START").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_START");
			}
			if (null == this.getValue("OUT_DATE_END")
					|| this.getValue("OUT_DATE_END").toString().length() <= 0) {
				this.grabFocus("OUT_DATE_END");
			}
			this.messageBox("请输入出院日期");
			return;
		}
		StringBuffer bf = new StringBuffer();
		
		bf.append(" WHERE A.DS_DATE BETWEEN TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_START"), true)).append(
				"','YYYYMMDDHH24MISS') AND TO_DATE('").append(
				SystemTool.getInstance().getDateReplace(
						this.getValueString("OUT_DATE_END").substring(0,10), false)).append(
				"','YYYYMMDDHH24MISS')");
		if (this.getValue("REGION_CODE").toString().length() > 0) {
			bf.append(" AND C.REGION_CODE ='").append(
					this.getValue("REGION_CODE").toString()).append("'");
		}
//		AND B.SDISEASE_CODE IS NOT NULL
//		AND F.INS_CROWD_TYPE='2'
		if(this.getValue("HZ_TYPE").toString().equals("1")){
			bf.append(" AND B.SDISEASE_CODE IS NULL ");
		}if(this.getValue("HZ_TYPE").toString().equals("2")){
			bf.append(" AND B.SDISEASE_CODE IS NOT NULL ");
		}
		if (this.getValue("YB_TYPE").toString().length() > 0) {
			bf.append(" AND F.INS_CROWD_TYPE ='").append(
					this.getValue("YB_TYPE").toString()).append("'");
		}
		if (this.getValue("OPE_ICD").toString().length() > 0) {
			bf.append(" AND K.OP_CODE ='").append(
					this.getValue("OPE_ICD").toString()).append("'");
		}
//		System.out.println(sql + bf);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql + bf));
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			return;
		}
		this.addSum(result);
		table.setParmValue(result);
	}
	public void addSum(TParm result){
		//MR_NO;IPD_NO;PAT_NAME;CHN_DESC;PAT_AGE;CTZ_DESC;IN_DATE;DS_DATE;IN_DAYS;DEPT_DESC;PHA_AMT;EXM_AMT;TREAT_AMT;OP_AMT;BED_AMT;MATERIAL_AMT;BLOODALL_AMT;BLOOD_AMT;OTHER_AMT;SUM_AMT;OWN_AMT;ADD_AMT;OWN_SUM_AMT;SJZFJE;NHI_PAY;NHI_PAY_REAL;HOSP_APPLY_AMT;NHI_COMMENT;HOSP_ILLNESS_SUBSIDY_AMT;ILLNESS_SUBSIDY_AMT;ACCOUNT_PAY_AMT;GRZHZF;HJZFJE;OP_DESC;SPHA;SMATERIAL_AMT;ZYYS;CBQX;MAIN_DIAG;SDISEASE_CODE;SDISEASE_DESC;MAIN_SUGEON_DESC;SINGLE_SUPPLYING_AMT;RESTART_STANDARD_AMT;STARTPAY_OWN_AMT;NHI_OWN_PAY;TX_OWN_AMT
		
		int row=result.insertRow();
		result.setData("MR_NO", row, "总计");
		for(int i=0;i<row;i++){
			//PHA_AMT;EXM_AMT;TREAT_AMT;OP_AMT;BED_AMT;
			result.setData("PHA_AMT", row, result.getDouble("PHA_AMT", row)+result.getDouble("PHA_AMT", i));
			result.setData("EXM_AMT", row, result.getDouble("EXM_AMT", row)+result.getDouble("EXM_AMT", i));
			result.setData("TREAT_AMT", row, result.getDouble("TREAT_AMT", row)+result.getDouble("TREAT_AMT", i));
			result.setData("OP_AMT", row, result.getDouble("OP_AMT", row)+result.getDouble("OP_AMT", i));
			result.setData("BED_AMT", row, result.getDouble("BED_AMT", row)+result.getDouble("BED_AMT", i));
			//MATERIAL_AMT;BLOODALL_AMT;BLOOD_AMT;OTHER_AMT;SUM_AMT;	
			result.setData("MATERIAL_AMT", row, result.getDouble("MATERIAL_AMT", row)+result.getDouble("MATERIAL_AMT", i));
			result.setData("BLOODALL_AMT", row, result.getDouble("BLOODALL_AMT", row)+result.getDouble("BLOODALL_AMT", i));
			result.setData("BLOOD_AMT", row, result.getDouble("BLOOD_AMT", row)+result.getDouble("BLOOD_AMT", i));
			result.setData("OTHER_AMT", row, result.getDouble("OTHER_AMT", row)+result.getDouble("OTHER_AMT", i));
			result.setData("SUM_AMT", row, result.getDouble("SUM_AMT", row)+result.getDouble("SUM_AMT", i));
			//OWN_AMT;ADD_AMT;OWN_SUM_AMT;SJZFJE;NHI_PAY;	
			result.setData("OWN_AMT", row, result.getDouble("OWN_AMT", row)+result.getDouble("OWN_AMT", i));
			result.setData("ADD_AMT", row, result.getDouble("ADD_AMT", row)+result.getDouble("ADD_AMT", i));
			result.setData("OWN_SUM_AMT", row, result.getDouble("OWN_SUM_AMT", row)+result.getDouble("OWN_SUM_AMT", i));
			result.setData("SJZFJE", row, result.getDouble("SJZFJE", row)+result.getDouble("SJZFJE", i));
			result.setData("NHI_PAY", row, result.getDouble("NHI_PAY", row)+result.getDouble("NHI_PAY", i));
			//NHI_PAY_REAL;HOSP_APPLY_AMT;NHI_COMMENT;HOSP_ILLNESS_SUBSIDY_AMT;ILLNESS_SUBSIDY_AMT;	
			result.setData("NHI_PAY_REAL", row, result.getDouble("NHI_PAY_REAL", row)+result.getDouble("NHI_PAY_REAL", i));
			result.setData("HOSP_APPLY_AMT", row, result.getDouble("HOSP_APPLY_AMT", row)+result.getDouble("HOSP_APPLY_AMT", i));
			result.setData("NHI_COMMENT", row, result.getDouble("NHI_COMMENT", row)+result.getDouble("NHI_COMMENT", i));
			result.setData("HOSP_ILLNESS_SUBSIDY_AMT", row, result.getDouble("HOSP_ILLNESS_SUBSIDY_AMT", row)+result.getDouble("HOSP_ILLNESS_SUBSIDY_AMT", i));
			result.setData("ILLNESS_SUBSIDY_AMT", row, result.getDouble("ILLNESS_SUBSIDY_AMT", row)+result.getDouble("ILLNESS_SUBSIDY_AMT", i));
			//ACCOUNT_PAY_AMT;GRZHZF;HJZFJE;SPHA;SMATERIAL_AMT;	
			result.setData("ACCOUNT_PAY_AMT", row, result.getDouble("ACCOUNT_PAY_AMT", row)+result.getDouble("ACCOUNT_PAY_AMT", i));
			result.setData("GRZHZF", row, result.getDouble("GRZHZF", row)+result.getDouble("GRZHZF", i));
			result.setData("HJZFJE", row, result.getDouble("HJZFJE", row)+result.getDouble("HJZFJE", i));
			result.setData("SPHA", row, result.getDouble("SPHA", row)+result.getDouble("SPHA", i));
			result.setData("SMATERIAL_AMT", row, result.getDouble("SMATERIAL_AMT", row)+result.getDouble("SMATERIAL_AMT", i));
			//SINGLE_SUPPLYING_AMT;RESTART_STANDARD_AMT;STARTPAY_OWN_AMT;NHI_OWN_PAY;TX_OWN_AMT	
			result.setData("SINGLE_SUPPLYING_AMT", row, result.getDouble("SINGLE_SUPPLYING_AMT", row)+result.getDouble("SINGLE_SUPPLYING_AMT", i));
			result.setData("RESTART_STANDARD_AMT", row, result.getDouble("RESTART_STANDARD_AMT", row)+result.getDouble("RESTART_STANDARD_AMT", i));
			result.setData("STARTPAY_OWN_AMT", row, result.getDouble("STARTPAY_OWN_AMT", row)+result.getDouble("STARTPAY_OWN_AMT", i));
			result.setData("NHI_OWN_PAY", row, result.getDouble("NHI_OWN_PAY", row)+result.getDouble("NHI_OWN_PAY", i));
			result.setData("TX_OWN_AMT", row, result.getDouble("TX_OWN_AMT", row)+result.getDouble("TX_OWN_AMT", i));
			//20171205 zhangs add
			result.setData("IN_DAYS", row, result.getDouble("IN_DAYS", row)+result.getDouble("IN_DAYS", i));
			
		}
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		// TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(
					table,
					StringTool.getString((Timestamp) this
							.getValue("OUT_DATE_START"), "yyyy.MM")
							+ "住院医疗费申请支付审核表");
	}
	public void onClear(){
		this.setValue("OUT_DATE_START", SystemTool.getInstance().getDate());
		this.setValue("OUT_DATE_END", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		///////////////////////////////////////////////////////
		this.setValue("HZ_TYPE", "");
		this.setValue("YB_TYPE", "");
		this.setValue("OPE_ICD", "");
		table.removeRowAll();
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
}
