package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.dongyang.ui.base.TComboBoxModel;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import java.util.Vector;

public class CLPDifDetailControl extends TControl {
	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 表格
	private TTable table;

	public CLPDifDetailControl() {

	}

	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		this.callFunction("UI|CLPTABLE|removeRowAll");
		initControl();
	}

	/**
	 * 初始化控件
	 */
	public void initControl() {
		// //起日
		// Timestamp startDate = StringTool.getTimestamp(new Date());
		// //迄日
		// Timestamp endDate = StringTool.rollDate(startDate, 1);
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table = (TTable) this.getComponent("CLPTABLE");
		start_date = (TTextFormat) this.getComponent("START_DATE");
		end_date = (TTextFormat) this.getComponent("END_DATE");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (this.table.getRowCount() <= 0) {
			this.messageBox("没有要打印的数据");
			return;
		}
		TParm prtParm = new TParm();
		// 表头
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "所有院区")
				+ "临床路径差异细表");
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate)) {
			startDate = startDate.substring(0, (startDate.length() - 2));
		}
		if (this.checkNullAndEmpty(endDate)) {
			endDate = endDate.substring(0, (endDate.length() - 2));
		}
		prtParm.setData("START_DATE", "TEXT", startDate);
		prtParm.setData("END_DATE", "TEXT", endDate);
		TParm tableparm = this.getSelectTParm();
		// 设置总行数
		tableparm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
		tableparm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "MR_NO");
		tableparm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		tableparm.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		tableparm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "TOT");
		tableparm.addData("SYSTEM", "COLUMNS", "FEE_STAND");
		tableparm.addData("SYSTEM", "COLUMNS", "ORDER_DESC_1");
		tableparm.addData("SYSTEM", "COLUMNS", "MAINTOT");
		tableparm.addData("SYSTEM", "COLUMNS", "FEE_REAL");
		tableparm.addData("SYSTEM", "COLUMNS", "REAL_TOT");
		tableparm.addData("SYSTEM", "COLUMNS", "FEE_DIFF");
		tableparm.addData("SYSTEM", "COLUMNS", "MONCAT_CHN_DESC");
		tableparm.addData("SYSTEM", "COLUMNS", "VARIANCE_CHN_DESC");
		prtParm.setData("CLPTABLE", tableparm.getData());
		// 表尾
		prtParm.setData("CREATEUSER", "TEXT", "制表人：" + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPNewDifDetail.jhw",
				prtParm);
	}

	public void onQuery() {
		if (!checkData()) {
			return;
		}
		TParm parm = this.getSelectTParm();
		if (parm == null) {
			this.messageBox("查无数据");
		}
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		TParm tableparm = new TParm();
		// 得到时间查询条件 0,19
		String startdate = this.getValueString("START_DATE");
		startdate = startdate.substring(0, 19).replace(" ", "")
				.replace("-", "").replace(":", "");
		String enddate = this.getValueString("END_DATE");
		enddate = enddate.substring(0, 19).replace(" ", "").replace("-", "")
				.replace(":", "");
		// 得到差异总表信息
		String SqlY = " SELECT MR.REGION_CODE AS REGION_CODE, ST.STATION_DESC,A.IPD_NO,O.CLNCPATH_CHN_DESC, A.MR_NO, Z.CTZ_DESC, " // wangzhilei
		// 20120723
				// 修改
				+ " DE.DEPT_CHN_DESC, R.USER_NAME, F.ORDER_DESC, "
				+ " (CASE WHEN D.TOT IS NULL THEN 0 ELSE D.TOT END) TOT, "
				+ " (CASE WHEN D.TOT_AMT IS NULL THEN 0 ELSE D.TOT_AMT END) FEE_STAND, "
				+ " F2.ORDER_DESC AS ORDER_DESC_1 ,"
				+ " (CASE WHEN D.MAINTOT IS NULL THEN 0 ELSE D.MAINTOT END) MAINTOT, "
				+ " (CASE WHEN D.MAIN_AMT IS NULL THEN 0 ELSE D.MAIN_AMT END) FEE_REAL, "
				+ " (CASE WHEN D.MAINTOT IS NULL THEN 0 ELSE D.MAINTOT END) -"
				+ " (CASE WHEN D.TOT IS NULL THEN 0 ELSE D.TOT END) REAL_TOT, "
				+ " (CASE WHEN D.MAIN_AMT IS NULL THEN 0 ELSE D.MAIN_AMT END) -"
				+ " (CASE WHEN D.TOT_AMT IS NULL THEN 0 ELSE D.TOT_AMT END) FEE_DIFF, "
				+ " T.MONCAT_CHN_DESC, V.VARIANCE_CHN_DESC"
				+ " FROM CLP_MANAGED D, SYS_OPERATOR R, SYS_FEE F, SYS_FEE F2, "
				+ " CLP_BSCINFO O, SYS_DEPT DE, CLP_VARMONCAT T, CLP_VARIANCE V, "
				+ "  ADM_INP A, SYS_CTZ Z ,MRO_RECORD MR, "
				+ " SYS_STATION ST "
				+ "  WHERE  MR.IN_STATION=ST.STATION_CODE(+) AND  MR.CASE_NO=D.CASE_NO AND  D.R_USER = R.USER_ID(+) AND D.ORDER_CODE = F.ORDER_CODE(+) AND D.REGION_CODE='"
				+ Operator.getRegion()
				+ "' AND MR.OUT_DATE>TO_DATE('"
				+ startdate
				+ "','YYYYMMDDHH24MISS') AND MR.OUT_DATE<TO_DATE('"
				+ enddate
				+ "','YYYYMMDDHH24MISS')"
				+ " AND D.MAINORD_CODE = F2.ORDER_CODE(+) "
				+ " AND D.MEDICAL_MONCAT IS NOT NULL "
				+ " AND D.CLNCPATH_CODE = O.CLNCPATH_CODE(+) "
				+ " AND T.MONCAT_CODE(+) = D.MEDICAL_MONCAT "
				+ " AND V.VARIANCE_CODE(+) = D.MEDICAL_VARIANCE "
				+ " AND D.CASE_NO = A.CASE_NO "
				+ " AND A.CTZ1_CODE = Z.CTZ_CODE "
				+ " AND A.DEPT_CODE = DE.DEPT_CODE(+) "
				+ " AND D.ORDER_FLG='Y' ";
		String SqlN = " SELECT  MR.REGION_CODE AS REGION_CODE, ST.STATION_DESC,A.IPD_NO,O.CLNCPATH_CHN_DESC, A.MR_NO, Z.CTZ_DESC, "// wangzhilei
		// 20120723
				// 修改
				+ " DE.DEPT_CHN_DESC, R.USER_NAME, F.CHKITEM_CHN_DESC AS ORDER_DESC, "
				+ " (CASE WHEN D.TOT IS NULL THEN 0 ELSE D.TOT END) TOT, "
				+ " (CASE WHEN D.TOT_AMT IS NULL THEN 0 ELSE D.TOT_AMT END) FEE_STAND, "
				+ " F2.CHKITEM_CHN_DESC  AS ORDER_DESC_1 ,"
				+ " (CASE WHEN D.MAINTOT IS NULL THEN 0 ELSE D.MAINTOT END) MAINTOT, "
				+ " (CASE WHEN D.MAIN_AMT IS NULL THEN 0 ELSE D.MAIN_AMT END) FEE_REAL, "
				+ " (CASE WHEN D.MAINTOT IS NULL THEN 0 ELSE D.MAINTOT END) -"
				+ " (CASE WHEN D.TOT IS NULL THEN 0 ELSE D.TOT END) REAL_TOT, "
				+ " (CASE WHEN D.MAIN_AMT IS NULL THEN 0 ELSE D.MAIN_AMT END) -"
				+ " (CASE WHEN D.TOT_AMT IS NULL THEN 0 ELSE D.TOT_AMT END) FEE_DIFF, "
				+ " T.MONCAT_CHN_DESC, V.VARIANCE_CHN_DESC"
				+ " FROM CLP_MANAGED D, SYS_OPERATOR R, CLP_CHKITEM F, CLP_CHKITEM F2, "
				+ " CLP_BSCINFO O, SYS_DEPT DE, CLP_VARMONCAT T, CLP_VARIANCE V, "
				+ "  ADM_INP A, SYS_CTZ Z ,MRO_RECORD MR, "
				+ " SYS_STATION ST "
				+ "  WHERE  MR.IN_STATION=ST.STATION_CODE(+) AND  MR.CASE_NO=D.CASE_NO AND  D.R_USER = R.USER_ID(+) AND D.ORDER_CODE = F.CHKITEM_CODE(+) AND D.REGION_CODE='"
				+ Operator.getRegion()
				+ "' AND MR.OUT_DATE>TO_DATE('"
				+ startdate
				+ "','YYYYMMDDHH24MISS') AND MR.OUT_DATE<TO_DATE('"
				+ enddate
				+ "','YYYYMMDDHH24MISS')"
				+ " AND D.MAINORD_CODE = F2.CHKITEM_CODE(+) "
				+ " AND D.MEDICAL_MONCAT IS NOT NULL "
				+ " AND D.CLNCPATH_CODE = O.CLNCPATH_CODE(+) "
				+ " AND T.MONCAT_CODE(+) = D.MEDICAL_MONCAT "
				+ " AND V.VARIANCE_CODE(+) = D.MEDICAL_VARIANCE "
				+ " AND D.CASE_NO = A.CASE_NO "
				+ " AND A.CTZ1_CODE = Z.CTZ_CODE "
				+ " AND A.DEPT_CODE = DE.DEPT_CODE(+) "
				+ " AND D.ORDER_FLG='N' ";
		String SqlO = " SELECT MR.REGION_CODE AS REGION_CODE,ST.STATION_DESC,A.IPD_NO,O.CLNCPATH_CHN_DESC, A.MR_NO, Z.CTZ_DESC, "// wangzhilei
		// 20120723
				// 修改
				+ " DE.DEPT_CHN_DESC, R.USER_NAME, F.ORDER_CHN_DESC AS ORDER_DESC, "
				+ " (CASE WHEN D.TOT IS NULL THEN 0 ELSE D.TOT END) TOT, "
				+ " (CASE WHEN D.TOT_AMT IS NULL THEN 0 ELSE D.TOT_AMT END) FEE_STAND, "
				+ " F2.ORDER_CHN_DESC AS ORDER_DESC_1,"
				+ " (CASE WHEN D.MAINTOT IS NULL THEN 0 ELSE D.MAINTOT END) MAINTOT, "
				+ " (CASE WHEN D.MAIN_AMT IS NULL THEN 0 ELSE D.MAIN_AMT END) FEE_REAL, "
				+ " (CASE WHEN D.MAINTOT IS NULL THEN 0 ELSE D.MAINTOT END) -"
				+ " (CASE WHEN D.TOT IS NULL THEN 0 ELSE D.TOT END) REAL_TOT, "
				+ " (CASE WHEN D.MAIN_AMT IS NULL THEN 0 ELSE D.MAIN_AMT END) -"
				+ " (CASE WHEN D.TOT_AMT IS NULL THEN 0 ELSE D.TOT_AMT END) FEE_DIFF, "
				+ " T.MONCAT_CHN_DESC, V.VARIANCE_CHN_DESC"
				+ " FROM CLP_MANAGED D, SYS_OPERATOR R, CLP_NURSORDER F, CLP_NURSORDER F2, "
				+ " CLP_BSCINFO O, SYS_DEPT DE, CLP_VARMONCAT T, CLP_VARIANCE V, "
				+ "  ADM_INP A, SYS_CTZ Z ,MRO_RECORD MR, "
				+ " SYS_STATION ST "
				+ "  WHERE  MR.IN_STATION=ST.STATION_CODE(+) AND  MR.CASE_NO=D.CASE_NO AND  D.R_USER = R.USER_ID(+) AND D.ORDER_CODE = F.ORDER_CODE(+) AND D.REGION_CODE='"
				+ Operator.getRegion()
				+ "' AND MR.OUT_DATE>TO_DATE('"
				+ startdate
				+ "','YYYYMMDDHH24MISS') AND MR.OUT_DATE<TO_DATE('"
				+ enddate
				+ "','YYYYMMDDHH24MISS')"
				+ " AND D.MAINORD_CODE = F2.ORDER_CODE(+) "
				+ " AND D.MEDICAL_MONCAT IS NOT NULL "
				+ " AND D.CLNCPATH_CODE = O.CLNCPATH_CODE(+) "
				+ " AND T.MONCAT_CODE(+) = D.MEDICAL_MONCAT "
				+ " AND V.VARIANCE_CODE(+) = D.MEDICAL_VARIANCE "
				+ " AND D.CASE_NO = A.CASE_NO "
				+ " AND A.CTZ1_CODE = Z.CTZ_CODE "
				+ " AND A.DEPT_CODE = DE.DEPT_CODE(+) "
				+ " AND D.ORDER_FLG='O' ";
		String sql = SqlY + " UNION  " + SqlN + " UNION " + SqlO;
		tableparm = new TParm(TJDODBTool.getInstance().select(sql));
		if (tableparm.getCount() <= 0) {
			return null;
		}
		TParm prtTableParm = new TParm();
		// 处理查询出来的parm
		// 统计信息begin
		int total = 0;
		int maintot = 0;
		double standardFee = 0;
		double realFee = 0;
		TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
		// 统计信息end
		for (int i = 0; i < tableparm.getCount(); i++) {
			TParm rowParm = tableparm.getRow(i);
			// wangzhilei 12-07-24添加
			String rn = tableparm.getValue("REGION_CODE", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			// wangzhilei 12-07-24 添加
			// 区域
			prtTableParm.addData("REGION_CODE", rn); // wangzhilei
			// 20120723
			// 修改
			// 临床路径
			prtTableParm.addData("CLNCPATH_CHN_DESC", rowParm
					.getValue("CLNCPATH_CHN_DESC"));
			// 病案号
			prtTableParm.addData("MR_NO", rowParm.getValue("MR_NO"));
			// 身份
			prtTableParm.addData("CTZ_DESC", rowParm.getValue("CTZ_DESC"));
			// 科室
			prtTableParm.addData("DEPT_CHN_DESC", rowParm
					.getValue("DEPT_CHN_DESC"));
			// 查核人员
			prtTableParm.addData("USER_NAME", rowParm.getValue("USER_NAME"));
			// 标准项目
			prtTableParm.addData("ORDER_DESC", rowParm.getValue("ORDER_DESC"));
			// 标准数量
			prtTableParm.addData("TOT", rowParm.getValue("TOT"));
			total += rowParm.getInt("TOT");
			// 标准费用
			prtTableParm.addData("FEE_STAND", rowParm.getValue("FEE_STAND"));
			standardFee += rowParm.getDouble("FEE_STAND");
			// 实际项目
			prtTableParm
					.addData("ORDER_DESC_1", rowParm.getValue("ORDER_DESC"));
			// 实际数量
			prtTableParm.addData("MAINTOT", rowParm.getValue("MAINTOT"));

			maintot += rowParm.getInt("MAINTOT");
			// 实际费用
			prtTableParm.addData("FEE_REAL", rowParm.getValue("FEE_REAL"));
			realFee += rowParm.getDouble("FEE_REAL");
			// 数量差额
			prtTableParm.addData("REAL_TOT", rowParm.getValue("REAL_TOT"));
			// 费用差额
			prtTableParm.addData("FEE_DIFF", rowParm.getValue("FEE_DIFF"));
			// 变异类别
			prtTableParm.addData("MONCAT_CHN_DESC", rowParm
					.getValue("MONCAT_CHN_DESC"));
			// 变异原因
			prtTableParm.addData("VARIANCE_CHN_DESC", rowParm
					.getValue("VARIANCE_CHN_DESC"));
			// 病区
			prtTableParm.addData("STATION_DESC", rowParm
					.getValue("STATION_DESC"));
			// 住院号
			prtTableParm.addData("IPD_NO", rowParm.getValue("IPD_NO"));
		}
		// 处理总计
		// 临床路径
		prtTableParm.addData("REGION_CODE", "总计：");// wangzhilei 20120723 修改
		// 病案号
		prtTableParm.addData("MR_NO", "");
		// 身份
		prtTableParm.addData("CTZ_DESC", "");
		// 科室
		prtTableParm.addData("DEPT_CHN_DESC", "");
		// 查核人员
		prtTableParm.addData("USER_NAME", "");
		// 标准项目
		prtTableParm.addData("ORDER_DESC", "");
		// 标准数量
		prtTableParm.addData("TOT", total);
		// 标准费用
		prtTableParm.addData("FEE_STAND", standardFee);
		// 实际项目
		prtTableParm.addData("ORDER_DESC_1", "");
		// 实际数量
		prtTableParm.addData("MAINTOT", maintot);
		// 实际费用
		prtTableParm.addData("FEE_REAL", realFee);
		// 数量差额
		prtTableParm.addData("REAL_TOT", "");
		// 费用差额
		prtTableParm.addData("FEE_DIFF", "");
		// 变异类别
		prtTableParm.addData("MONCAT_CHN_DESC", "");
		// 变异原因
		prtTableParm.addData("VARIANCE_CHN_DESC", "");
		// 病区
		prtTableParm.addData("STATION_DESC", "");
		// 住院号
		prtTableParm.addData("IPD_NO", "");
		prtTableParm.setCount(prtTableParm.getCount("REGION_CODE"));// wangzhilei
		// 20120723
		// 修改
		return prtTableParm;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径差异细表");
		}
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		String start = this.getValueString("START_DATE");
		if (start == null || start.length() <= 0) {
			this.messageBox("开始时间不能为空");
			return false;
		}
		String end = this.getValueString("END_DATE");
		if (end == null || end.length() <= 0) {
			this.messageBox("结束时间不能为空");
			return false;
		}

		return true;
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

}
