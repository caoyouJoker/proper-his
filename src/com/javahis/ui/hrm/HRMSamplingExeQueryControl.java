package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 体检采样执行查询统计
 * </p>
 * 
 * <p>
 * Description: 体检采样执行查询统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.7.4
 * @version 1.0
 */
public class HRMSamplingExeQueryControl extends TControl {

	private TTable table;
	private TTextFormat company;// 团体名称TTextFormat
	private TTextFormat contract;// 团体合同TTextFormat
	private HRMContractD contractD;// 合同对象
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		this.initComponent();
		this.onInitPage();
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		company = (TTextFormat) this.getComponent("COMPANY_CODE");
		table = (TTable) this.getComponent("TABLE");
		addSortListener(table);
	}

	/**
	 * 初始化控件
	 */
	public void onInitPage() {
		// 取得当前日期
		Timestamp nowDate = SystemTool.getInstance().getDate();
		// 设定默认展开日期
		this.setValue("START_DATE", nowDate);
		this.setValue("END_DATE", nowDate);
		table.setParmValue(new TParm());

		contractD = new HRMContractD();
		contractD.onQuery("", "", "");

		// 查询团体信息(暂时只查询一期临床)
		TParm companyData = HRMCompanyTool.getInstance()
				.selectCompanyComboByRoleType("PIC");
		company.setPopupMenuData(companyData);
		company.setComboSelectRow();
		company.popupMenuShowData();
	}

	/**
	 * 团体代码点选事件
	 */
	public void onCompanyChoose() {
		String companyCode = this.getValueString("COMPANY_CODE");
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm == null || contractParm.getCount() <= 0
				|| contractParm.getErrCode() != 0) {
			this.messageBox_("该团体下无合同数据");
			return;
		}

		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		String contractCode = contractParm.getValue("ID", 0);
		if (StringUtils.isEmpty(contractCode)) {
			this.messageBox_("查询失败");
			return;
		}
		contract.setValue(contractCode);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			this.messageBox("采样起止时间不能为空");
			return;
		}
		
		// 起讫时间
		startDate = startDate.substring(0, 10).replace("-", "");
		endDate = endDate.substring(0, 10).replace("-", "");
		// 团体信息
		String companyCode = this.getValueString("COMPANY_CODE");
		// 合同
		String contractCode = this.getValueString("CONTRACT_CODE");
		// 病案号
		String mrNo = this.getValueString("MR_NO");
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.MR_NO,C.PAT_NAME,B.STAFF_NO,A.ORDER_DESC,A.BLOOD_DATE,A.BLOOD_USER ");
		sbSql.append(" FROM MED_APPLY A, HRM_CONTRACTD B, HRM_PATADM C ");
		sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.CASE_NO = C.CASE_NO AND B.MR_NO = C.MR_NO ");
		sbSql.append(" AND B.CONTRACT_CODE = C.CONTRACT_CODE AND A.BLOOD_USER IS NOT NULL AND A.BLOOD_DATE >= TO_DATE('");
		sbSql.append(startDate);
		sbSql.append("000000','YYYYMMDDHH24MISS') AND A.BLOOD_DATE <= TO_DATE('");
		sbSql.append(endDate);
		sbSql.append("235959','YYYYMMDDHH24MISS') ");
		
		if (StringUtils.isNotEmpty(companyCode)) {
			sbSql.append(" AND B.COMPANY_CODE = '");
			sbSql.append(companyCode);
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(contractCode)) {
			sbSql.append(" AND B.CONTRACT_CODE = '");
			sbSql.append(contractCode);
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(mrNo)) {
			sbSql.append(" AND A.MR_NO = '");
			sbSql.append(mrNo);
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY BLOOD_DATE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			this.messageBox("查询采样数据失败");
			err("查询采样数据失败:" + result.getErrText());
		} else if (result.getCount() < 1) {
			table.setParmValue(new TParm());
			this.messageBox("查无数据");
			return;
		} else {
			table.setParmValue(result);
		}
	}
	
	/**
	 * 根据病案号查询
	 */
	public void onQueryByMrNo() {
		// 取得病案号
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("查无此病案号");
				return;
			}
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("COMPANY_CODE;CONTRACT_CODE;MR_NO");
		this.onInitPage();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "采样执行查询统计");
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// 点击相同列，翻转排序
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// 取得表单中的数据
				String columnName[] = tableData.getNames("Data");// 获得列名
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
				int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * 根据列名数据，将TParm转为Vector
	 * 
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * 返回指定列在列名数组中的index
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * 根据列名数据，将Vector转成Parm
	 * 
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
}
