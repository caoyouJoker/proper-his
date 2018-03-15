package com.javahis.ui.udd;

import java.math.BigDecimal;
import java.sql.Timestamp; //import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable; //import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat; //import com.dongyang.ui.event.TPopupMenuEvent;
//import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

//import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 门/急/住诊患者抗菌药物处方比例统计
 * </p>
 * 
 * <p>
 * Description: 门/急/住诊患者抗菌药物处方比例统计
 * </p>
 * 
 * <p>
 * Copyright: ProperSoft
 * </p>
 * 
 * <p>
 * Company:    
 * </p>
 * 
 * @author yanjing
 * @version 1.0
 */
public class UDDpatAntiPrestatisticsControl extends TControl {
	private TTable table_i;// 住院表
	private TTable table_oe;// 门急诊表
	private TTable table_oes;// 门急诊表---人数
	private int pageFlg = 0;// 页签标记（0：住院；1：门急诊）

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化时间控件
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		this.table_i = this.getTable("TABLE_I");// 得到住院表格
		this.table_oe = this.getTable("TABLE_OE");// 得到门急诊表格
		table_oes = this.getTable("TABLE_OES");// 得到门急诊表格---人数
		// 初始化院区
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		setValue("OPT_USER", Operator.getID());// 初始化操作者
		this.clearValue("ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
		this.callFunction("UI|ODI_TYPE|setEnabled", false);
	}

	/**
	 * 获取表格组件
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * 页签点击事件
	 */
	public void onChangeTTabbedPane() {
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) {
			pageFlg = 0;// 住院
		} else if (tabbedPane.getSelectedIndex() == 1) {
			pageFlg = 1;// 门急诊
		} else {
			pageFlg = 2;// 门急诊--人数
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String dr_code = "";// 查询条件中是否有医生
		String adm_type = "";// 查询条件中是否有门急诊条件
		String adm_types = "";
		String dr_codes ="";
		if (this.getValueString("ADM_TYPE") != null
				&& !this.getValueString("ADM_TYPE").equals("")) {
			adm_type = "  AND A.ADM_TYPE = '" + this.getValueString("ADM_TYPE")
					+ "'";
		}// sql中添加门急诊条件
		if (this.getValueString("ADM_TYPES") != null
				&& !this.getValueString("ADM_TYPES").equals("")) {
			adm_types = "  AND A.ADM_TYPE = '" + this.getValueString("ADM_TYPES")
					+ "'";
		}// sql中添加门急诊条件
		if (this.getValueString("DR_OE_CODE") != null
				&& !this.getValueString("DR_OE_CODE").equals("")) {
			dr_code = "  AND A.DR_CODE = '" + this.getValueString("DR_OE_CODE")
					+ "'";
		}// sql中添加医生查询条件
		
		if (this.getValueString("DR_OE_CODES") != null
				&& !this.getValueString("DR_OE_CODES").equals("")) {
			dr_codes = "  AND A.DR_CODE = '" + this.getValueString("DR_OE_CODES")
					+ "'";
		}// sql中添加医生查询条件
		String region_code = "";// 区域加入查询条件，门急诊
		if (this.getValueString("REGION_CODE") != null
				&& !this.getValueString("REGION_CODE").equals("")) {
			region_code = "  AND A.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "'";
		}
		String region_code1 = "";// 区域加入查询条件,住院
		if (this.getValueString("REGION_CODE") != null
				&& !this.getValueString("REGION_CODE").equals("")) {
			region_code1 = "  AND M.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "'";
		}
		String odi_dr_code = "";// 住院医生
		String odi_type = "";// 住院状态
		String odi_dept_code = "";// 住院科室
		if (this.getValueString("DR_CODE") != null
				&& !this.getValueString("DR_CODE").equals("")) {
			odi_dr_code = "  AND M.VS_DR_CODE = '"
					+ this.getValueString("DR_CODE") + "'";
		}// sql中加医生查询条件
		if (this.getValueString("ODI_TYPE") != null
				&& !this.getValueString("ODI_TYPE").equals("")) {
			if (this.getValueString("ODI_TYPE").equals("1")) {
				odi_type = " AND M.DS_DATE IS NULL ";
			} else {
				odi_type = " AND M.DS_DATE IS NOT NULL ";
			}
		}// sql中加住院状态查询条件
		if (this.getValueString("DEPT_CODE") != null
				&& !this.getValueString("DEPT_CODE").equals("")) {
			odi_dept_code = "  AND M.DEPT_CODE = '"
					+ this.getValueString("DEPT_CODE") + "'";
		}// sql中加科室查询条件
		TTextFormat startDateComp = (TTextFormat) this.getComponent("S_DATE");// 开始时间
		TTextFormat endDateComp = (TTextFormat) this.getComponent("E_DATE");// 结束时间
		// 设置时间的格式
		String startDate = StringTool.getString((Timestamp) startDateComp
				.getValue(), "yyyyMMddHHmmss");
		String endDate = StringTool.getString((Timestamp) endDateComp
				.getValue(), "yyyyMMddHHmmss");
		// 定义存放查询结果的TParm
		TParm result = new TParm();
		TParm result1 = new TParm();
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) { // 当是住院页签时
			// 查询所有的住院的人
			String SQL_I = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE, "
					+ "F.USER_NAME AS DR_CODE,S.ANTIBIOTIC_CODE,M.CASE_NO,"
					+ "CASE WHEN M.DS_DATE IS NOT NULL THEN '出院' ELSE '在院' END AS ODI_TYPE "
					+ "FROM ADM_INP M,SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F,(SELECT A.CASE_NO,A.DEPT_CODE, A.VS_DR_CODE,"
					+ "CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL  THEN '01' ELSE '' END ANTIBIOTIC_CODE "
					+ "FROM ADM_INP A,ODI_ORDER B, PHA_BASE C  "
					+ "WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE(+)"
					+ " AND A.DS_DATE BETWEEN TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ "AND TO_DATE ('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ "GROUP BY A.DEPT_CODE, A.VS_DR_CODE, C.ANTIBIOTIC_CODE,A.CASE_NO) S "
					+ "WHERE M.CASE_NO = S.CASE_NO AND M.CANCEL_FLG <> 'Y' "
					+ "AND M.REGION_CODE = D.REGION_CODE AND "
					+ " M.DEPT_CODE = E.DEPT_CODE AND M.VS_DR_CODE = F.USER_ID "
					+ region_code1
					+ odi_dr_code
					+ odi_type
					+ odi_dept_code
					+ "GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,M.DS_DATE,S.ANTIBIOTIC_CODE,M.CASE_NO "
					+ "ORDER BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,ODI_TYPE,M.CASE_NO";
			result1 = new TParm(TJDODBTool.getInstance().select(SQL_I));
			// 整理查询结果,当CASE_NO重复时只保留一条数据
			TParm result2 = new TParm();
			for (int i = 0; i < result1.getCount(); i++) {
				String case_no1 = result1.getValue("CASE_NO", i);
				String case_no2 = result1.getValue("CASE_NO", i + 1);
				if (case_no1.equals(case_no2)) {
					result2.addRowData(result1, i);
					i++;
				} else {
					result2.addRowData(result1, i);
				}
			}
			// 整理查询结果，汇总院区、在院状态、科室、医生相同的数据
			TParm result3 = new TParm();
			double tot_num = 1.0;
			for (int i = 0; i < result2.getCount(); i++) {
				String dept_code1 = result2.getValue("DEPT_CODE", i);
				String dr_code1 = result2.getValue("DR_CODE", i);
				String odi_type1 = result2.getValue("ODI_TYPE", i);
				String dept_code2 = result2.getValue("DEPT_CODE", i + 1);
				String dr_code2 = result2.getValue("DR_CODE", i + 1);
				String odi_type2 = result2.getValue("ODI_TYPE", i + 1);
				// double tot_num = 1.0;
				if (dept_code1.equals(dept_code2) && dr_code1.equals(dr_code2)
						&& odi_type1.equals(odi_type2)) {
					tot_num++;
				} else {
					result3.addRowData(result2, i);
					result3.setData("TOT_NUM", i, tot_num);
					tot_num = 1.0;
				}

			}
			// 查询所有使用抗菌药物人数
			TParm result11 = new TParm();
			String sql_i = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE, "
					+ "F.USER_NAME AS DR_CODE,S.ANTIBIOTIC_CODE,M.CASE_NO,"
					+ "CASE WHEN M.DS_DATE IS NOT NULL THEN '出院' ELSE '在院' END AS ODI_TYPE "
					+ "FROM ADM_INP M,SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F,(SELECT A.CASE_NO,A.DEPT_CODE, A.VS_DR_CODE,"
					+ "CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL  THEN '01' ELSE '' END ANTIBIOTIC_CODE "
					+ "FROM ADM_INP A,ODI_ORDER B, PHA_BASE C  "
					+ "WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE"
					+ " AND A.DS_DATE BETWEEN TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ "AND TO_DATE ('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ "GROUP BY A.DEPT_CODE, A.VS_DR_CODE, C.ANTIBIOTIC_CODE,A.CASE_NO) S "
					+ "WHERE M.CASE_NO = S.CASE_NO AND M.CANCEL_FLG <> 'Y' "
					+ "AND M.REGION_CODE = D.REGION_CODE AND S.ANTIBIOTIC_CODE IS NOT NULL "
					+ " AND M.DEPT_CODE = E.DEPT_CODE AND M.VS_DR_CODE = F.USER_ID "
					+ region_code1
					+ odi_dr_code
					+ odi_type
					+ odi_dept_code
					+ "GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,M.DS_DATE,S.ANTIBIOTIC_CODE,M.CASE_NO "
					+ "ORDER BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,ODI_TYPE,M.CASE_NO ";
			result11 = new TParm(TJDODBTool.getInstance().select(sql_i));
			// 整理查询结果,当CASE_NO重复时只保留一条数据
			TParm result12 = new TParm();
			for (int i = 0; i < result11.getCount(); i++) {
				String case_no1 = result11.getValue("CASE_NO", i);
				String case_no2 = result11.getValue("CASE_NO", i + 1);
				if (case_no1.equals(case_no2)) {
					result12.addRowData(result11, i);
					i++;
				} else {
					result12.addRowData(result11, i);
				}
			}
			// 整理查询结果，汇总院区、在院状态、科室、医生相同的数据
			TParm result13 = new TParm();
			double anti_num = 1.0;
			for (int i = 0; i < result12.getCount(); i++) {
				String dept_code1 = result12.getValue("DEPT_CODE", i);
				String dr_code1 = result12.getValue("DR_CODE", i);
				String odi_type1 = result12.getValue("ODI_TYPE", i);
				String dept_code2 = result12.getValue("DEPT_CODE", i + 1);
				String dr_code2 = result12.getValue("DR_CODE", i + 1);
				String odi_type2 = result12.getValue("ODI_TYPE", i + 1);
				// double tot_num = 1.0;
				if (dept_code1.equals(dept_code2) && dr_code1.equals(dr_code2)
						&& odi_type1.equals(odi_type2)) {
					anti_num++;
				} else {
					result13.addRowData(result12, i);
					result13.setData("ANTI_NUM", i, anti_num);
					anti_num = 1.0;
				}

			}
			// 将result3和result13中数据整合到result中
			for (int i = 0; i < result3.getCount(); i++) {
				String dept_code1 = result3.getValue("DEPT_CODE", i);
				String dr_code1 = result3.getValue("DR_CODE", i);
				String odi_type1 = result3.getValue("ODI_TYPE", i);
				double anti_num2 = 0;
				for (int j = 0; j < result13.getCount(); j++) {
					String dept_code2 = result13.getValue("DEPT_CODE", j);
					String dr_code2 = result13.getValue("DR_CODE", j);
					String odi_type2 = result13.getValue("ODI_TYPE", j);
					if (dept_code1.equals(dept_code2)
							&& dr_code1.equals(dr_code2)
							&& odi_type1.equals(odi_type2)) {
						anti_num2 = result13.getDouble("ANTI_NUM", j);
					}
				}
				result.addRowData(result3, i);
				result.setData("ANTI_NUM", i, anti_num2);
			}

			if (result.getCount("REGION_CODE") < 0) {
				messageBox("查无数据");
				TParm resultparm = new TParm();
				this.table_i.setParmValue(resultparm);
				return;
			} else {
				TParm resultParm = new TParm();
				int j = 0;
				double count_tot = 0;
				double count_anti = 0;
				double all_count = 0;// 计算“总计”中总人数
				double all_anti_count = 0;// 计算“总计”中使用抗菌药物人数
				for (int i = 0; i < result.getCount("REGION_CODE"); i++) {
					double rx_no = result.getDouble("TOT_NUM", i);
					double rx_anti_no = result.getDouble("ANTI_NUM", i);
					all_count += rx_no;
					all_anti_count += rx_anti_no;
					double rx_anti_rate = 0.0000;
					String real_rate = "";
					if (rx_anti_no <= 0) {
						real_rate = rx_anti_rate + "%" + "";
						result.setData("ANTI_RATE", i, real_rate);
					} else {
						// 计算每一行的百分比
						rx_anti_rate = rx_anti_no / rx_no;
						BigDecimal bdi = new BigDecimal(rx_anti_rate);
						bdi = bdi.setScale(4, 4);
						BigDecimal parmMuli = new BigDecimal(100);
						parmMuli = parmMuli.setScale(0);
						rx_anti_rate = bdi.multiply(parmMuli).doubleValue();
						real_rate = rx_anti_rate + "%" + "";
						result.setData("ANTI_RATE", i, real_rate);
					}
					String d_code = result.getValue("DEPT_CODE", i);
					String next_dr_code = result.getValue("DEPT_CODE", i + 1);
					if (i == 0 || (d_code.equals(next_dr_code))) {
						count_tot += rx_no;
						count_anti += rx_anti_no;
						resultParm.addRowData(result, i);
						j++;
					} else {
						// 计算“小计”中的百分比
						count_tot += rx_no;
						count_anti += rx_anti_no;
						resultParm.addRowData(result, i);
						double rate = count_anti / count_tot;
						BigDecimal bdi1 = new BigDecimal(rate);
						bdi1 = bdi1.setScale(4, 4);
						BigDecimal parmMuli1 = new BigDecimal(100);
						parmMuli1 = parmMuli1.setScale(0);
						rate = bdi1.multiply(parmMuli1).doubleValue();
						real_rate = rate + "%" + "";
						result.addData("RX_ANTI_RATE", real_rate);
						resultParm.setData("REGION_CODE", ++j, "小计:");
						resultParm.setData("ODI_TYPE", ++j, "");
						resultParm.setData("DEPT_CODE", ++j, d_code);
						resultParm.setData("DR_CODE", ++j, "");
						resultParm.setData("TOT_NUM", ++j, count_tot);
						resultParm.setData("ANTI_NUM", ++j, count_anti);
						resultParm.setData("ANTI_RATE", ++j, real_rate);
						j++;
						count_tot = 0;
						count_anti = 0;

					}

				}
				// 计算总计中的百分比
				double last_rate = all_anti_count / all_count;
				BigDecimal bdi = new BigDecimal(last_rate);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				last_rate = bdi.multiply(parmMuli).doubleValue();
				String real_rate1 = last_rate + "%" + "";
				resultParm.setData("REGION_CODE", j, "总计:");
				resultParm.setData("ODI_TYPE", j, "");  
				resultParm.setData("DEPT_CODE", j, "");
				resultParm.setData("DR_CODE", j, "");
				resultParm.setData("TOT_NUM", j, all_count);
				resultParm.setData("ANTI_NUM", j, all_anti_count);
				resultParm.setData("ANTI_RATE", j, real_rate1);
				this.table_i.setParmValue(resultParm);
			}
		} else if (tabbedPane.getSelectedIndex() == 1) {
			// 查询所有的处方签数
			String SQL = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE,"
					+ " CASE A.ADM_TYPE WHEN 'O' THEN '门诊' ELSE '急诊' END AS ADM_TYPE,F.USER_NAME AS DR_CODE,"
					+ " COUNT(DISTINCT (B.RX_NO)) AS RX_NUM  "
					+
					// " CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL THEN COUNT(B.RX_NO) END AS RX_ANT_NUM "
					// +
					" FROM REG_PATADM A,OPD_ORDER B,PHA_BASE C , SYS_REGION D,SYS_DEPT E,"
					+ " SYS_OPERATOR F "
					+ " WHERE A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss')"
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') AND A.CASE_NO = B.CASE_NO AND "
					+ " A.REGCAN_USER IS NULL AND B.ORDER_CODE = C.ORDER_CODE AND B.REGION_CODE = D.REGION_CODE AND "
					+ " B.DEPT_CODE = E.DEPT_CODE AND A.DR_CODE = F.USER_ID  AND B.BILL_FLG='Y' "
					+ region_code
					+ dr_code
					+ adm_type
					+ " GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC,A.ADM_TYPE,F.USER_NAME"
					+ " ORDER BY E.DEPT_CHN_DESC,F.USER_NAME";
			result = new TParm(TJDODBTool.getInstance().select(SQL));
			//fux 
			// 查询抗菌药物处方签数
			String sql = "SELECT E.DEPT_CHN_DESC AS DEPT_CODE , F.USER_NAME AS DR_CODE,"
					+ " CASE B.ADM_TYPE WHEN 'O' THEN '门诊' ELSE '急诊' END AS ADM_TYPE,"
					+ " CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL THEN COUNT(DISTINCT (B.RX_NO)) END AS RX_ANT_NUM"
					+ " FROM REG_PATADM A,OPD_ORDER B,PHA_BASE C, SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F "
					+ " WHERE A.REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND "
					+ " A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ " AND TO_DATE('"  
					+ endDate         
					+ "','yyyymmddhh24miss') AND A.REGION_CODE = B.REGION_CODE "
					+ " AND B.DEPT_CODE = E.DEPT_CODE AND B.DR_CODE = F.USER_ID  AND B.BILL_FLG='Y' "
					+ region_code  
					+ dr_code
					+ adm_type  
					+ " GROUP BY C.ANTIBIOTIC_CODE,B.ADM_TYPE,E.DEPT_CHN_DESC,F.USER_NAME"
					+ " ORDER BY E.DEPT_CHN_DESC,F.USER_NAME";
			//System.out.println("页签2 ： 查询抗菌药物处方签数sql:"+sql);
			result1 = new TParm(TJDODBTool.getInstance().select(sql));
			//fux modify 20150812
			for (int j = 0; j < result.getCount(); j++) {
				// 取出result中的门急别、科室、医生
				String adm_type0 = result.getValue("ADM_TYPE", j);
				String dept_code0 = result.getValue("DEPT_CODE", j);
				String dr_code0 = result.getValue("DR_CODE", j);
				// 定义变量，校验相继的门急别、科室和医生是否相同
				String adm_type1 = "";
				String adm_type2 = "";  
				String dept_code1 = "";
				String dept_code2 = "";
				String dr_code1 = "";
				String dr_code2 = "";
				double rx_ant_num1 = 0;
				for (int i = 0; i < result1.getCount(); i++) {
					// 取出result中的门急别、科室、医生
					adm_type1 = result1.getValue("ADM_TYPE", i);
					dept_code1 = result1.getValue("DEPT_CODE", i);
					dr_code1 = result1.getValue("DR_CODE", i);
					if (adm_type0.equals(adm_type1)
							&& dept_code0.equals(dept_code1)
							&& dr_code0.equals(dr_code1)) {
						adm_type2 = result1.getValue("ADM_TYPE", i + 1);
						dept_code2 = result1.getValue("DEPT_CODE", i + 1);
						dr_code2 = result1.getValue("DR_CODE", i + 1);
						if (adm_type1.equals(adm_type2)         
								&& dept_code1.equals(dept_code2)
								&& dr_code1.equals(dr_code2)) {
							// 取出result1 中的RX_ANT_NUM值
							double temp = result1.getDouble("RX_ANT_NUM", i);
							rx_ant_num1 += temp;
						}
					}
				}
				result.setData("RX_ANT_NUM", j, rx_ant_num1);
			}
			  
			// 定义变量，校验相继的门急别、科室和医生是否相同
//			for (int j = 0; j < result.getCount(); j++) {
//				// 取出result中的门急别、科室、医生
//				String	adm_type0 = result.getValue("ADM_TYPE", j);
//				String  dept_code0 = result.getValue("DEPT_CODE", j);
//				String	dr_code0 = result.getValue("DR_CODE", j);
//				for (int i = 0; i < result1.getCount(); i++) {
//					// 取出result中的门急别、科室、医生
//					if (adm_type0.equals(result1.getValue("ADM_TYPE", i))
//							&& dept_code0.equals(result1.getValue("DEPT_CODE", i))
//							&& dr_code0.equals(result1.getValue("DR_CODE", i))) {
////						System.out.println("页签2.3 ：adm_type0:"+adm_type0);  
////						System.out.println("页签2.3 ：dept_code0:"+dept_code0);
////						System.out.println("页签2.3 ：dr_code0:"+dr_code0);
//						// 取出result1 中的RX_ANT_NUM值
//						result.setData("RX_ANT_NUM", j, result1.getDouble("RX_ANT_NUM", i));
//						//result.setData("RX_ANTI_RATE", j, StringTool.round(result1.getDouble("RX_ANT_NUM", i)/result.getDouble("RX_NUM",j)*100, 2)+"%");
//						break;
//					}
//				}  
//			}
			
			  
			if (result.getCount() < 0) {
				messageBox("查无数据");
				TParm resultparm = new TParm();
				this.table_oe.setParmValue(resultparm);
				return;
			} else {
				TParm resultParm = new TParm();// 表格中传入数据
				int j = 0;// 记录resultParm的行数
				double count_tot = 0;// 小计中总处方数
				double count_anti = 0;// 小计中使用抗生素的处方数
				double all_count = 0;// 总计中总处方数
				double all_anti_count = 0;// 总计中使用抗生素的处方数
				for (int i = 0; i < result.getCount(); i++) {
					double rx_no = result.getDouble("RX_NUM", i);
					double rx_anti_no = result.getDouble("RX_ANT_NUM", i);
					all_count += rx_no;
					all_anti_count += rx_anti_no;
					double rx_anti_rate = 0.0000;
					String real_rate = "";
					if (rx_anti_no <= 0) {
						real_rate = rx_anti_rate + "%" + "";
						result.setData("RX_ANTI_RATE", i, real_rate);
					} else {
						// 计算每一行的抗菌药物处方比例 start
						rx_anti_rate = rx_anti_no / rx_no;
						BigDecimal bdi = new BigDecimal(rx_anti_rate);
						bdi = bdi.setScale(4, 4);
						BigDecimal parmMuli = new BigDecimal(100);
						parmMuli = parmMuli.setScale(0);
						rx_anti_rate = bdi.multiply(parmMuli).doubleValue();
						real_rate = rx_anti_rate + "%" + "";
						result.setData("RX_ANTI_RATE", i, real_rate);
						// 计算每一行的抗菌药物处方比例 end
					}
					String d_code = result.getValue("DEPT_CODE", i);// 去紧相连的科室
					String next_dr_code = result.getValue("DEPT_CODE", i + 1);
					if ((d_code.equals(next_dr_code))) {// 若相继的科室相同
						count_tot += rx_no;
						count_anti += rx_anti_no;
						resultParm.addRowData(result, i);
						j++;
					} else {// 若相继的科室不同
						count_tot += rx_no;// 计算每个“小计”中总人数
						count_anti += rx_anti_no;// 计算每个“小计”中使用抗菌素的人数
						resultParm.addRowData(result, i);// resultParm
															// 中设置出总人数、使用抗菌素人数及比例之外的值
						// 计算“小计”中的使用率
						double rate = count_anti / count_tot;
						BigDecimal bdi1 = new BigDecimal(rate);
						bdi1 = bdi1.setScale(4, 4);
						BigDecimal parmMuli1 = new BigDecimal(100);
						parmMuli1 = parmMuli1.setScale(0);
						rate = bdi1.multiply(parmMuli1).doubleValue();
						real_rate = rate + "%" + "";
						result.addData("RX_ANTI_RATE", real_rate);
						// 设置“小计”行的值
						resultParm.setData("REGION_CODE", ++j, "小计:");
						resultParm.setData("ADM_TYPE", ++j, "");
						resultParm.setData("DEPT_CODE", ++j, d_code);
						resultParm.setData("DR_CODE", ++j, "");
						resultParm.setData("RX_NUM", ++j, count_tot);
						resultParm.setData("RX_ANT_NUM", ++j, count_anti);
						resultParm.setData("RX_ANTI_RATE", ++j, real_rate);
						j++;
						count_tot = 0;// "小计"赋值结束，将总人数变量清空
						count_anti = 0;// "小计"赋值结束，将使用抗菌素的人数变量清空
					}
				}
				// 设置“总计”行的值
				// 计算“总计”中百分比，start
				double last_rate = all_anti_count / all_count;
				BigDecimal bdi = new BigDecimal(last_rate);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				last_rate = bdi.multiply(parmMuli).doubleValue();
				String real_rate1 = last_rate + "%" + "";
				// 计算“总计”中百分比，end
				resultParm.setData("REGION_CODE", j, "总计:");
				resultParm.setData("ADM_TYPE", j, "");
				resultParm.setData("DEPT_CODE", j, "");
				resultParm.setData("DR_CODE", j, "");
				resultParm.setData("RX_NUM", j, all_count);
				resultParm.setData("RX_ANT_NUM", j, all_anti_count);
				resultParm.setData("RX_ANTI_RATE", j, real_rate1);
				this.table_oe.setParmValue(resultParm);// 为表格赋值
			}
		}else{//=========pangben 2013-11-21
			// 查询所有的人数
			String SQL = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE,"
					+ " CASE A.ADM_TYPE WHEN 'O' THEN '门诊' ELSE '急诊' END AS ADM_TYPE,F.USER_NAME AS DR_CODE,"
					+ " COUNT(DISTINCT (A.CASE_NO)) AS RX_NUM ,0 RX_ANT_NUM,'0.0%' AS RX_ANTI_RATE,B.PHA_NUM "
					+
					// " CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL THEN COUNT(B.RX_NO) END AS RX_ANT_NUM "
					// +
					" FROM REG_PATADM A, SYS_REGION D,SYS_DEPT E,"
					+ " SYS_OPERATOR F,(SELECT A.DEPT_CODE,A.REGION_CODE,A.DR_CODE ,COUNT(DISTINCT (A.CASE_NO)) PHA_NUM FROM REG_PATADM A,OPD_ORDER B " 
					+ "WHERE A.CASE_NO=B.CASE_NO AND B.CAT1_TYPE='PHA' AND A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss')"
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ region_code
					+ dr_codes
					+ adm_types+" GROUP BY A.DEPT_CODE,A.REGION_CODE,A.DR_CODE) B"
					+ " WHERE A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss')"
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') AND "
					+ " A.REGCAN_USER IS NULL AND A.REGION_CODE = D.REGION_CODE AND "
					+ " A.DEPT_CODE = E.DEPT_CODE AND A.DR_CODE = F.USER_ID AND "
					+ " A.REGION_CODE=B.REGION_CODE(+) AND A.DR_CODE=B.DR_CODE(+) AND"
					+ " A.DEPT_CODE=B.DEPT_CODE(+) "
					+ region_code
					+ dr_codes
					+ adm_types
					+ " GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC,A.ADM_TYPE,F.USER_NAME,B.PHA_NUM "
					+ " ORDER BY D.REGION_CHN_ABN,A.ADM_TYPE,E.DEPT_CHN_DESC,F.USER_NAME";
			//System.out.println("SQL:sdfasdfs::::"+SQL);
			result = new TParm(TJDODBTool.getInstance().select(SQL));
			// 查询抗菌药物处方签数
			String sql =" SELECT   S.REGION_CODE,S.DEPT_CODE , S.DR_CODE,ADM_TYPE,COUNT(S.CASE_NO) AS RX_ANT_NUM FROM ("+
                        "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE , F.USER_NAME AS DR_CODE,"+
                        "CASE A.ADM_TYPE WHEN 'O' THEN '门诊' ELSE '急诊' END AS ADM_TYPE,A.CASE_NO "+
                        "FROM REG_PATADM A,OPD_ORDER B,PHA_BASE C, SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F "+
                    " WHERE A.REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND "+
                    " A.ADM_DATE BETWEEN TO_DATE('"+startDate+"','yyyymmddhh24miss') "+
                    " AND TO_DATE('"+endDate+"','yyyymmddhh24miss') AND A.REGION_CODE = D.REGION_CODE "+
                    " AND B.DEPT_CODE = E.DEPT_CODE AND B.DR_CODE = F.USER_ID "+  
                    " AND C.ANTIBIOTIC_CODE IS NOT NULL AND B.BILL_FLG='Y' "
                    + region_code
					+ dr_codes    
					+ adm_types
					+
                    " GROUP BY D.REGION_CHN_ABN,C.ANTIBIOTIC_CODE,A.ADM_TYPE,E.DEPT_CHN_DESC,F.USER_NAME,A.CASE_NO )S"+
                    " GROUP BY S.REGION_CODE,S.DEPT_CODE , S.DR_CODE,ADM_TYPE"+
                    " ORDER BY S.REGION_CODE,S.ADM_TYPE,S.DEPT_CODE,S.DR_CODE";
			//System.out.println("页签3 ： 查询抗菌药物处方签数sql:"+sql);
			result1 = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount()<=0) {
				TParm resultparm = new TParm();
				this.table_oe.setParmValue(resultparm);
				this.messageBox("没有查询的数据");
				return;
			}
			String adm_type0="";
			String dept_code0="";
			String dr_code0="";
			// 定义变量，校验相继的门急别、科室和医生是否相同
			for (int j = 0; j < result.getCount(); j++) {
				// 取出result中的门急别、科室、医生
				adm_type0 = result.getValue("ADM_TYPE", j);
				dept_code0 = result.getValue("DEPT_CODE", j);
				dr_code0 = result.getValue("DR_CODE", j);
				for (int i = 0; i < result1.getCount(); i++) {
					// 取出result中的门急别、科室、医生
					if (adm_type0.equals(result1.getValue("ADM_TYPE", i))
							&& dept_code0.equals(result1.getValue("DEPT_CODE", i))
							&& dr_code0.equals(result1.getValue("DR_CODE", i))) {
						// 取出result1 中的RX_ANT_NUM值
						result.setData("RX_ANT_NUM", j, result1.getDouble("RX_ANT_NUM", i));
						result.setData("RX_ANTI_RATE", j, StringTool.round(result1.getDouble("RX_ANT_NUM", i)/result.getDouble("RX_NUM",j)*100, 2)+"%");
						break;
					}         
				}
			}  
			if (result.getCount() < 0) {  
				messageBox("查无数据");  
				TParm resultparm = new TParm();
				this.table_oe.setParmValue(resultparm);
				return;
			} else {
				TParm resultParm = new TParm();// 表格中传入数据
				double count_tot = 0;// 小计中总人数
				double count_anti = 0;// 小计中使用抗生素的人数
				double all_count = 0;// 总计中总人数
				double all_anti_count = 0;// 总计中使用抗生素的人数
				double count_pha=0;//小计中总取药人数
				double all_pha_coount=0;//总计中使用药品人数
				adm_type0 =result.getValue("ADM_TYPE", 0);
				dept_code0 =result.getValue("DEPT_CODE", 0);
				//dr_code0 = result.getValue("DR_CODE", 0);
				String regionCode =result.getValue("REGION_CODE", 0);
				for (int i = 0; i < result.getCount(); i++) {
					if (adm_type0.equals(result.getValue("ADM_TYPE", i))
							&&dept_code0.equals(result.getValue("DEPT_CODE", i))) {
						count_tot+=result.getDouble("RX_NUM", i);
						count_pha+=result.getDouble("PHA_NUM", i);
						count_anti+=result.getDouble("RX_ANT_NUM", i);
						getParmValue(resultParm, regionCode, dept_code0, adm_type0, result.getValue("DR_CODE", i), result.getDouble("RX_NUM", i), 
								result.getDouble("RX_ANT_NUM", i), result.getValue("RX_ANTI_RATE",i),result.getDouble("PHA_NUM", i));
					}else{
						String antiRate="0.0%";
						if (count_tot!=0) {
							antiRate=StringTool.round(count_anti/count_tot*100,2)+"%";
						}
						getParmValue(resultParm, "小计:", dept_code0, adm_type0, "", count_tot, count_anti,
								antiRate,count_pha);
						adm_type0=result.getValue("ADM_TYPE", i);
						dept_code0=result.getValue("DEPT_CODE", i);
						regionCode=result.getValue("REGION_CODE", i);
						getParmValue(resultParm, regionCode, dept_code0, adm_type0, result.getValue("DR_CODE", i), result.getDouble("RX_NUM", i), 
								result.getDouble("RX_ANT_NUM", i), result.getValue("RX_ANTI_RATE",i),result.getDouble("PHA_NUM", i));
						count_tot=0;
						count_anti=0;
						count_pha=0;
					}
					all_count += result.getDouble("RX_NUM", i);
					all_pha_coount += result.getDouble("PHA_NUM", i);
					all_anti_count += result.getDouble("RX_ANT_NUM", i);
					if (i==result.getCount()-1) {
						getParmValue(resultParm, "小计:", dept_code0, adm_type0, "", count_tot, count_anti,
								StringTool.round(count_anti/count_tot*100,2)+"%",count_pha);
					}
				}
				getParmValue(resultParm, "总计:", "", "", "", all_count, all_anti_count,
						StringTool.round(all_anti_count/all_count*100,2)+"%",all_pha_coount);
				resultParm.setCount(resultParm.getCount("REGION_CODE"));
				this.table_oes.setParmValue(resultParm);// 为表格赋值
			}
		}

	}
	private void getParmValue(TParm resultParm,String regionCode,String deptCode,
			String admType,String userName,double count_tot,double count_anti,String antiRate,double count_pha){
		resultParm.addData("REGION_CODE",regionCode);
		resultParm.addData("ADM_TYPE", admType);
		resultParm.addData("DEPT_CODE", deptCode);
		resultParm.addData("DR_CODE",userName);
		resultParm.addData("RX_NUM", count_tot);
		resultParm.addData("RX_ANT_NUM",count_anti);
		resultParm.addData("RX_ANTI_RATE",antiRate);
		resultParm.addData("PHA_NUM",count_pha);
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		try {
			TTabbedPane tabbedPane = ((TTabbedPane) this
					.getComponent("tTabbedPane_2"));
			if (tabbedPane.getSelectedIndex() == 0) {
				if (table_i.getParmValue().getCount()<=0) {
					messageBox("没有可导入的数据！");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_i,
						"住院患者抗菌药物处方比例统计");
			} else if (tabbedPane.getSelectedIndex() == 1) {
				if (table_oe.getParmValue().getCount()<=0) {
					messageBox("没有可导入的数据！");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oe,
						"门急诊患者抗菌药物处方比例统计-处方");
			}else{
				if (table_oes.getParmValue().getCount()<=0) {
					messageBox("没有可导入的数据！");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oes,
				"门急诊患者抗菌药物处方比例统计-人数");
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("没有可导入的数据！");
			return;
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		// 设置时间控件的值
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		// 清除表格中的数据
		table_i.setParmValue(new TParm());
		table_oe.setParmValue(new TParm());
		table_oes.setParmValue(new TParm());
		// 清除其他控件的值
		this.clearValue("OPT_USER;ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
	}
}
