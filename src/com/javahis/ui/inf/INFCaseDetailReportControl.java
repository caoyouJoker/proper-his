package com.javahis.ui.inf;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:感染病例明细报告 （感染登记处获取数据）
 * </p>
 * 
 * <p>
 * Description:感染病例明细报告
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2017-4-24
 * @version JavaHis 1.0
 */
public class INFCaseDetailReportControl extends TControl {

	// 表格
	private TTable table;

	@Override
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 获取时间查询区间
		String sDate = this.getValueString("IN_START_DATE");
		String eDate = this.getValueString("IN_END_DATE");

		String in_date = "";
		sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		in_date += " AND b.in_date > TO_DATE('" + sDate
				+ "','YYYYMMDDHH24MISS') ";
		eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		in_date += " AND b.in_date < TO_DATE('" + eDate
				+ "','YYYYMMDDHH24MISS') ";

		// 查询序号，病案号，患者姓名，生日，性别，科室简称，科室全称，入院日期，出院日期，住院天数，手术日期,手术名称,感染部位1-4
		// 感染日期1-4,感染系统1-4，是否送检1-4,转归
		String sql = "  SELECT ROW_NUMBER () OVER (ORDER BY INF_DATE DESC) AS NUM, A.MR_NO, A.PAT_NAME,A.BIRTH_DATE, C.CHN_DESC,D.DEPT_ABS_DESC,"
				+ "D.DEPT_CHN_DESC,B.IN_DATE,E.ICD_CHN_DESC AS INF_DESC1,F.ICD_CHN_DESC AS INF_DESC2,B.DS_DATE,Z.INF_DATE,WM_CONCAT (J.CHN_DESC) AS INF_SYSTEMDESC,"
				+ "WM_CONCAT (I.CHN_DESC) AS PART_DESC,WM_CONCAT (O.CHN_DESC) AS SPEC_DESC,WM_CONCAT (Z.ETIOLGEXM_FLG) AS ETIOLGEXM_FLG,Z.EXAM_DATE,G.OPT_CHN_DESC,Z.OP_DATE,Z.INICU_DATE,"
				+ " WM_CONCAT (O.CHN_DESC) AS SPEC_DESC,WM_CONCAT (N.SENS_LEVEL) AS SENS_DESC, WM_CONCAT (N.CULURE_CODE) AS CULTURE_CODE,K.CHN_DESC AS INFRETN_DESC "
				+ "FROM INF_CASE Z,SYS_PATINFO A,ADM_INP B,SYS_DICTIONARY C, SYS_DEPT D,SYS_DIAGNOSIS E,SYS_DIAGNOSIS F,SYS_OPERATIONICD G,"
				+ "INF_ICDPART H,SYS_DICTIONARY I,INF_ANTIBIOTEST N,SYS_DICTIONARY O,SYS_DICTIONARY J,SYS_DICTIONARY K "
				+ "WHERE     A.MR_NO = B.MR_NO "
				+ "AND Z.OP_CODE = G.OPERATION_ICD(+) "
				+ "AND Z.INFRETN_CODE = K.ID(+) "
				+ "AND Z.INF_NO = H.INF_NO(+) "
				+ " AND Z.INF_NO = N.INF_NO(+) "
				+ " AND Z.INFCASE_SEQ = N.INFCASE_SEQ(+) "
				+ "AND A.MR_NO = '"
				+ this.getValue("MR_NO")
				+ "' "
				+ "AND C.GROUP_ID(+) = 'SYS_SEX' "
				+ "AND I.GROUP_ID(+) = 'INF_INFPOSITION' "
				+ "AND J.GROUP_ID(+) = 'INF_SYSTEM' "
				+ "AND O.GROUP_ID(+) = 'INF_LABSPECIMEN' "
				+ "AND K.GROUP_ID(+) = 'ADM_RETURN' "
				+ in_date
				+ "AND B.DEPT_CODE = D.DEPT_CODE "
				+ "AND A.SEX_CODE = C.ID(+) "
				+ "AND H.PART_CODE = I.ID(+) "
				+ " AND Z.SPECIMEN_CODE = O.ID(+)"
				+ " AND H.INF_SYSTEMCODE = J.ID(+)"
				+ " AND Z.CASE_NO = B.CASE_NO"
				+ " AND Z.IN_DIAG1 = E.ICD_CODE(+)"
				+ " AND Z.IN_DIAG2 = F.ICD_CODE(+)"
				+ " AND Z.MR_NO = B.MR_NO "
				+ "AND Z.DEPT_CODE = B.DEPT_CODE"
				+ " AND Z.STATION_CODE = B.STATION_CODE"
				+ " AND Z.BED_NO = B.BED_NO "
				+ "GROUP BY A.MR_NO,A.PAT_NAME,A.BIRTH_DATE,C.CHN_DESC,D.DEPT_ABS_DESC,D.DEPT_CHN_DESC,B.IN_DATE,E.ICD_CHN_DESC,F.ICD_CHN_DESC,B.DS_DATE,"
				+ "Z.INF_DATE, Z.ETIOLGEXM_FLG, Z.EXAM_DATE, G.OPT_CHN_DESC,K.CHN_DESC,Z.OP_DATE,Z.INICU_DATE, Z.SPECIMEN_CODE, N.SENS_LEVEL,N.CULURE_CODE ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("感染的：：：：" + sql);
		if (parm.getCount() <= 0) {
			this.messageBox("查无数据");
			return;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			// 拼接入院诊断
			String inf_desc1 = parm.getValue("INF_DESC1", i);
			String inf_desc2 = parm.getValue("INF_DESC2", i);
			TParm iParm = new TParm();
			String inf_desc = "";
			if (!StringUtil.isNullString(inf_desc1)
					&& StringUtil.isNullString(inf_desc2)) {
				inf_desc = inf_desc1;
				iParm.setData("INF_DESC", i, inf_desc);
			} else if (StringUtil.isNullString(inf_desc1)
					&& !StringUtil.isNullString(inf_desc2)) {
				inf_desc = inf_desc2;
				iParm.setData("INF_DESC", i, inf_desc);
			} else if (StringUtil.isNullString(inf_desc1)
					&& StringUtil.isNullString(inf_desc2)) {
				iParm.setData("INF_DESC", i, " ");
			} else {
				inf_desc = inf_desc1 + ";" + inf_desc2;
				iParm.setData("INF_DESC", i, inf_desc);
			}
			parm.setData("INF_DESC", i, inf_desc);

			// 计算年龄
			String AGE = com.javahis.util.StringUtil.showAge(
					(Timestamp) parm.getData("BIRTH_DATE", i),
					(Timestamp) parm.getData("IN_DATE", i));
			parm.setData("AGE", i, AGE);
			// 计算住院天数
			String ds_date = parm.getValue("DS_DATE", i);
			if (StringUtil.isNullString(ds_date)) {
				parm.setData("IN_DAYS", i, "");
			} else {
				int in_days = StringTool.getDateDiffer(
						(Timestamp) parm.getData("DS_DATE", i),
						(Timestamp) parm.getData("IN_DATE", i));
				parm.setData("IN_DAYS", i, in_days);
			}
			// 判断该患者是否为术后患者
			String op_date = parm.getValue("OP_DATE", i);
			if (StringUtil.isNullString(op_date)) {
				parm.setData("OP_FLG", i, "否");
			} else {
				parm.setData("OP_FLG", i, "是");
			}
			// 判断该患者是否在 ICU治疗过
			String inicu_date = parm.getValue("INICU_DATE", i);
			if (StringUtil.isNullString(inicu_date)) {
				parm.setData("INICU_FLG", i, "否");
			} else {
				parm.setData("INICU_FLG", i, "是");
			}
			//转归
			parm.setData("INFRETN_DESC", i,
					parm.getValue("INFRETN_DESC", i) == null ? -1 : parm
							.getValue("INFRETN_DESC", i));
			// 感染部位 感染系统 判断是否送检 送检日期 送检标本种类
			String[] part_desc = parm.getValue("PART_DESC", i).split(",");
			String[] inf_systemdesc = parm.getValue("INF_SYSTEMDESC", i).split(
					",");
			for (int f = 0; f < part_desc.length; f++) {
				parm.setData("INF_DATE" + (f + 1), i,
						(Timestamp) parm.getTimestamp("INF_DATE", i));
				parm.setData("PART_DESC" + (f + 1), i, part_desc[f]);
				parm.setData("INF_SYSTEMDESC" + (f + 1), i, inf_systemdesc[f]);
				parm.setData("ETIOLGEXM_FLG"+(f + 1), i, parm.getValue("EXAM_DATE", i) == "" ? "否" : "是");
				parm.setData("EXAM_DATE"+(f + 1), i, (Timestamp) parm.getTimestamp("EXAM_DATE", i));
				parm.setData("SPEC_DESC"+(f + 1),i, parm.getValue("SPEC_DESC", i)== null ? -1 : parm.getValue("SPEC_DESC", i));
				parm.setData("SENS_DESC"+(f + 1),i, parm.getValue("SENS_DESC", i)== null ? -1 : parm.getValue("SENS_DESC", i));				
				parm.setData("CULTURE_CODE"+(f + 1),i, parm.getValue("CULTURE_CODE", i)== null ? -1 : parm.getValue("CULTURE_CODE", i));
				if (f == 0) {
					parm.setData("INF_DATE2", "");
					parm.setData("INF_DATE3", "");
					parm.setData("INF_DATE4", "");
					parm.setData("PART_DESC2", "");
					parm.setData("PART_DESC3", "");
					parm.setData("PART_DESC4", "");
					parm.setData("INF_SYSTEMDESC2", "");
					parm.setData("INF_SYSTEMDESC3", "");
					parm.setData("INF_SYSTEMDESC4", "");
					parm.setData("ETIOLGEXM_FLG2", "");
					parm.setData("ETIOLGEXM_FLG3", "");
					parm.setData("ETIOLGEXM_FLG4", "");
					parm.setData("EXAM_DATE2", "");
					parm.setData("EXAM_DATE3", "");
					parm.setData("EXAM_DATE4", "");
					parm.setData("SPEC_DESC2", "");
					parm.setData("SPEC_DESC3", "");
					parm.setData("SPEC_DESC4", "");
					parm.setData("SENS_DESC2", "");
					parm.setData("SENS_DESC3", "");
					parm.setData("SENS_DESC4", "");
					parm.setData("CULTURE_CODE2", "");
					parm.setData("CULTURE_CODE3", "");
					parm.setData("CULTURE_CODE4", "");
				}
				if (f == 1) {
					parm.setData("INF_DATE3", "");
					parm.setData("INF_DATE4", "");
					parm.setData("PART_DESC3", "");
					parm.setData("PART_DESC4", "");
					parm.setData("INF_SYSTEMDESC3", "");
					parm.setData("INF_SYSTEMDESC4", "");
					parm.setData("ETIOLGEXM_FLG3", "");
					parm.setData("ETIOLGEXM_FLG4", "");
					parm.setData("EXAM_DATE3", "");
					parm.setData("EXAM_DATE4", "");
					parm.setData("SPEC_DESC3", "");
					parm.setData("SPEC_DESC4", "");
					parm.setData("SENS_DESC3", "");
					parm.setData("SENS_DESC4", "");
					parm.setData("CULTURE_CODE3", "");
					parm.setData("CULTURE_CODE4", "");
				}
				if (f == 2) {
					parm.setData("INF_DATE4", "");
					parm.setData("PART_DESC4", "");
					parm.setData("INF_SYSTEMDESC4", "");
					parm.setData("ETIOLGEXM_FLG4", "");
					parm.setData("EXAM_DATE4", "");
					parm.setData("SPEC_DESC4", "");
					parm.setData("SENS_DESC4", "");
					parm.setData("CULTURE_CODE4", "");
				}
			}
			// 抗菌药物治疗
			String sqlG = "SELECT A.ANTIBIOTIC_WAY FROM ODI_ORDER A , INF_CASE B "
					+ "WHERE A.CASE_NO = B.CASE_NO "
					+ "AND A.BED_NO =B.BED_NO "
					+ "AND A.DEPT_CODE =B.DEPT_CODE "
					+ "AND A.STATION_CODE =B.STATION_CODE "
					+ "AND A.MR_NO = '"
					+ this.getValue("MR_NO") + "' ";
			TParm parmG = new TParm(TJDODBTool.getInstance().select(sqlG));
			System.out.println("是否感染药：：：：" + sqlG);
			if ("02".equals(parmG.getValue("ANTIBIOTIC_WAY", 0))) {
				parm.setData("ANTI_FLG", i, "是");
			} else {
				parm.setData("ANTI_FLG", i, "否");
			}
			// 侵入性操作
			String sqlB = "SELECT  A.MR_NO,WM_CONCAT (H.IO_CODE) AS IO_CODE,Z.INF_DATE "
					+ "FROM INF_CASE Z,SYS_PATINFO A,ADM_INP B,SYS_DICTIONARY C, SYS_DEPT D,INF_IO H "
					+ "WHERE     A.MR_NO = B.MR_NO "
					+ "AND Z.INF_NO = H.INF_NO(+) " + "AND Z.MR_NO = '"
					+ this.getValue("MR_NO") + "' "
					+ "AND C.GROUP_ID(+) = 'IO_TYPE' " + in_date
					+ "AND B.DEPT_CODE = D.DEPT_CODE "
					+ "AND H.IO_CODE = C.ID(+) " + "AND Z.CASE_NO = B.CASE_NO "
					+ "AND Z.MR_NO = B.MR_NO "
					+ "AND Z.DEPT_CODE = B.DEPT_CODE "
					+ "AND Z.STATION_CODE = B.STATION_CODE "
					+ "AND Z.BED_NO = B.BED_NO "
					+ "GROUP BY A.MR_NO,Z.INF_DATE ";
			TParm parmB = new TParm(TJDODBTool.getInstance().select(sqlB));
			System.out.println("侵入性操作：：：：" + sqlB);
			parm.setData("INV_FLG1", i, "否");
			parm.setData("INV_FLG2", i, "否");
			parm.setData("INV_FLG3", i, "否");
			parm.setData("INV_FLG4", i, "否");
			parm.setData("INV_FLG5", i, "否");
			parm.setData("INV_FLG6", i, "否");
			String[] io_code = parmB.getValue("IO_CODE", i).split(",");
			for (int k = 0; k < io_code.length; k++) {
				if ("01".equals(io_code[k])) {
					parm.setData("INV_FLG1", i, "是");
				}
				if ("02".equals(io_code[k])) {
					parm.setData("INV_FLG2", i, "是");
				}
				if ("03".equals(io_code[k])) {
					parm.setData("INV_FLG3", i, "是");
				}
				if ("04".equals(io_code[k])) {
					parm.setData("INV_FLG4", i, "是");
				}
				if ("05".equals(io_code[k])) {
					parm.setData("INV_FLG5", i, "是");
				}
				if ("06".equals(io_code[k])) {
					parm.setData("INV_FLG6", i, "是");
				}
			}
			// 易感因素
			String sqlA = "SELECT  A.MR_NO,WM_CONCAT (H.INFREASON_CODE)AS INFREASON_CODE,Z.INF_DATE "
					+ "FROM INF_CASE Z,SYS_PATINFO A,ADM_INP B,SYS_DICTIONARY C, SYS_DEPT D,INF_INFREASRCD H "
					+ "WHERE     A.MR_NO = B.MR_NO "
					+ "AND Z.INF_NO = H.INF_NO(+) " + "AND Z.MR_NO = '"
					+ this.getValue("MR_NO") + "' "
					+ "AND C.GROUP_ID(+) = 'INF_INFREASON' " + in_date
					+ "AND B.DEPT_CODE = D.DEPT_CODE "
					+ "AND H.INFREASON_CODE = C.ID(+) "
					+ "AND Z.CASE_NO = B.CASE_NO " + "AND Z.MR_NO = B.MR_NO "
					+ "AND Z.DEPT_CODE = B.DEPT_CODE "
					+ "AND Z.STATION_CODE = B.STATION_CODE "
					+ "AND Z.BED_NO = B.BED_NO "
			        + "GROUP BY A.MR_NO,Z.INF_DATE ";
			TParm parmA = new TParm(TJDODBTool.getInstance().select(sqlA));
			System.out.println("易感因素：：：：" + sqlA);
			parm.setData("FAC_FLG1", i, "否");
			parm.setData("FAC_FLG2", i, "否");
			parm.setData("FAC_FLG3", i, "否");
			parm.setData("FAC_FLG4", i, "否");
			parm.setData("FAC_FLG5", i, "否");
			parm.setData("FAC_FLG6", i, "否");
			parm.setData("FAC_FLG7", i, "否");
			parm.setData("FAC_FLG8", i, "否");
			parm.setData("FAC_FLG9", i, "否");
			parm.setData("FAC_FLG10", i, "否");
			parm.setData("FAC_FLG11", i, "否");
			parm.setData("FAC_FLG12", i, "否");
			parm.setData("FAC_FLG13", i, "否");
			parm.setData("FAC_FLG14", i, "否");
			parm.setData("FAC_FLG15", i, "否");
			parm.setData("FAC_FLG16", i, "否");
			parm.setData("FAC_FLG17", i, "否");
			parm.setData("FAC_FLG18", i, "否");
			parm.setData("FAC_FLG19", i, "否");
			parm.setData("FAC_FLG20", i, "否");
			parm.setData("FAC_FLG21", i, "否");
			parm.setData("FAC_FLG22", i, "否");
			parm.setData("FAC_FLG23", i, "否");
			String[] infreason_code = parmA.getValue("INFREASON_CODE", i).split(",");
			for (int j = 0; j < infreason_code.length; j++) {
				parmA.getValue("INFREASON_CODE", j);
				if ("01".equals(infreason_code[j])) {
					parm.setData("FAC_FLG1", i, "是");
				}
				if ("02".equals(infreason_code[j])) {
					parm.setData("FAC_FLG2", i, "是");
				}
				if ("03".equals(infreason_code[j])) {
					parm.setData("FAC_FLG3", i, "是");
				}
				if ("04".equals(infreason_code[j])) {
					parm.setData("FAC_FLG4", i, "是");
				}
				if ("05".equals(infreason_code[j])) {
					parm.setData("FAC_FLG5", i, "是");
				}
				if ("06".equals(infreason_code[j])) {
					parm.setData("FAC_FLG6", i, "是");
				}
				if ("07".equals(infreason_code[j])) {
					parm.setData("FAC_FLG7", i, "是");
				}
				if ("08".equals(infreason_code[j])) {
					parm.setData("FAC_FLG8", i, "是");
				}
				if ("09".equals(infreason_code[j])) {
					parm.setData("FAC_FLG9", i, "是");
				}
				if ("10".equals(infreason_code[j])) {
					parm.setData("FAC_FLG10", i, "是");
				}
				if ("11".equals(infreason_code[j])) {
					parm.setData("FAC_FLG11", i, "是");
				}
				if ("12".equals(infreason_code[j])) {
					parm.setData("FAC_FLG12", i, "是");
				}
				if ("13".equals(infreason_code[j])) {
					parm.setData("FAC_FLG13", i, "是");
				}
				if ("14".equals(infreason_code[j])) {
					parm.setData("FAC_FLG14", i, "是");
				}
				if ("15".equals(infreason_code[j])) {
					parm.setData("FAC_FLG15", i, "是");
				}
				if ("16".equals(infreason_code[j])) {
					parm.setData("FAC_FLG16", i, "是");
				}
				if ("17".equals(infreason_code[j])) {
					parm.setData("FAC_FLG17", i, "是");
				}
				if ("18".equals(infreason_code[j])) {
					parm.setData("FAC_FLG18", i, "是");
				}
				if ("19".equals(infreason_code[j])) {
					parm.setData("FAC_FLG19", i, "是");
				}
				if ("20".equals(infreason_code[j])) {
					parm.setData("FAC_FLG20", i, "是");
				}
				if ("21".equals(infreason_code[j])) {
					parm.setData("FAC_FLG21", i, "是");
				}
				if ("22".equals(infreason_code[j])) {
					parm.setData("FAC_FLG22", i, "是");
				}
				if ("23".equals(infreason_code[j])) {
					parm.setData("FAC_FLG23", i, "是");
				}
			}

		}
		table.setParmValue(parm);
	}

	/**
	 * 病案号回车事件
	 */
	public void onMrNo() {
		this.clearValue("PAT_NAME");
		String mrno = this.getValueString("MR_NO");

		if (StringUtils.isEmpty(mrno)) {
			return;
		}
		mrno = PatTool.getInstance().checkMrno(mrno);
		this.setValue("MR_NO", mrno);

		String patName = PatTool.getInstance().getNameForMrno(mrno);

		this.setValue("PAT_NAME", patName);

		onQuery();
	}

	/**
	 * 导出Excel
	 */
	public void onExcel() {
		TTable table = this.getTTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox_("暂无导出Excel数据");
			return;
		}
		INFCaseDetailReportUtil.getInstance().exportExcel(table, "医院感染病例报告");

	}

	/**
	 * 清空
	 */
	public void onClear() {

		initPage();

	}

	/**
	 * 初始化页面控件
	 */
	private void initPage() {
		table = (TTable) getComponent("TABLE");
		this.clearValue("DEPT;MR_NO;PAT_NAME;IN_START_DATE;IN_END_DATE");
		Timestamp time = SystemTool.getInstance().getDate();
		Timestamp endDate = Timestamp.valueOf(time.toString().substring(0, 10)
				+ " 23:59:59");
		Timestamp startDate = Timestamp.valueOf(StringTool.rollDate(time, -7)
				.toString().substring(0, 10)
				+ " 00:00:00");
		this.setValue("IN_START_DATE", startDate);
		this.setValue("IN_END_DATE", endDate);
		this.setValue("DEPT", Operator.getDept());
		// 设置区域
		this.setValue("REGION_CODE", "H01");
	}

	/**
	 * 获取TTable
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

}
