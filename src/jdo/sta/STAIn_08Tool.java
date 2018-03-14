package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: STA_IN_08手术医师工作量
 * </p>
 * 
 * <p>
 * Description: STA_IN_08手术医师工作量
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-6-26
 * @version 1.0
 */
public class STAIn_08Tool extends TJDOTool {
	/**
	 * 实例
	 */
	public static STAIn_08Tool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return STAIn_08Tool
	 */
	public static STAIn_08Tool getInstance() {
		if (instanceObject == null)
			instanceObject = new STAIn_08Tool();
		return instanceObject;
	}

	public STAIn_08Tool() {
		setModuleName("sta\\STAIn_08Module.x");
		onInit();
	}

	/**
	 * 查询指定SQL的数据
	 * 
	 * @param type
	 *            String 指定sql语句名称
	 * @param DATE_S
	 *            String 起始日期 格式yyyyMMdd
	 * @param DATE_E
	 *            String 截止日期 格式yyyyMMdd
	 * @return TParm ================pangben modify 20110525 添加区域参数
	 */
	public TParm selectDataT(String type, String DATE_S, String DATE_E,
			String regionCode) {
		TParm parm = new TParm();
		parm.setData("DATE_S", DATE_S);
		parm.setData("DATE_E", DATE_E);
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		TParm result = this.query(type, parm);
		if (result.getErrCode() < 0) {
			err(type + " ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 整合数据
	 * 
	 * @param DATE_S
	 *            String 起始日期
	 * @param DATE_E
	 *            String 截止日期
	 * @param dr_list
	 *            TParm 医师列表
	 * @return TParm
	 */
	public TParm selectData(String DATE_S, String DATE_E, TParm dr_list,
			String regionCode) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm result = new TParm();
		TParm selectDATA_01 = this.selectDataT("selectDATA_01", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_01.getErrCode() < 0) {
			err("ERR:" + selectDATA_01.getErrCode()
					+ selectDATA_01.getErrText() + selectDATA_01.getErrName());
			return selectDATA_01;
		}
		TParm selectDATA_02 = this.selectDataT("selectDATA_02", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_02.getErrCode() < 0) {
			err("ERR:" + selectDATA_02.getErrCode()
					+ selectDATA_02.getErrText() + selectDATA_02.getErrName());
			return selectDATA_02;
		}
		TParm selectDATA_03 = this.selectDataT("selectDATA_03", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_03.getErrCode() < 0) {
			err("ERR:" + selectDATA_03.getErrCode()
					+ selectDATA_03.getErrText() + selectDATA_03.getErrName());
			return selectDATA_03;
		}
		TParm selectDATA_05 = this.selectDataT("selectDATA_05", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_05.getErrCode() < 0) {
			err("ERR:" + selectDATA_05.getErrCode()
					+ selectDATA_05.getErrText() + selectDATA_05.getErrName());
			return selectDATA_05;
		}
		TParm selectWJNum = this.selectDataT("selectWJNum", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectWJNum.getErrCode() < 0) {
			err("ERR:" + selectWJNum.getErrCode() + selectWJNum.getErrText()
					+ selectWJNum.getErrName());
			return selectWJNum;
		}
		TParm selectDATA_07 = this.selectDataT("selectDATA_07", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_07.getErrCode() < 0) {
			err("ERR:" + selectDATA_07.getErrCode()
					+ selectDATA_07.getErrText() + selectDATA_07.getErrName());
			return selectDATA_07;
		}
		TParm selectDATA_08 = this.selectDataT("selectDATA_08", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_08.getErrCode() < 0) {
			err("ERR:" + selectDATA_08.getErrCode()
					+ selectDATA_08.getErrText() + selectDATA_08.getErrName());
			return selectDATA_08;
		}
		TParm selectDATA_09 = this.selectDataT("selectDATA_09", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_09.getErrCode() < 0) {
			err("ERR:" + selectDATA_09.getErrCode()
					+ selectDATA_09.getErrText() + selectDATA_09.getErrName());
			return selectDATA_09;
		}
		TParm selectDATA_10 = this.selectDataT("selectDATA_10", DATE_S, DATE_E,
				regionCode);// =====pangben modify 20110525
		if (selectDATA_10.getErrCode() < 0) {
			err("ERR:" + selectDATA_10.getErrCode()
					+ selectDATA_10.getErrText() + selectDATA_10.getErrName());
			return selectDATA_10;
		}
		int TDATA_01 = 0;
		int TDATA_02 = 0;
		int TDATA_03 = 0;
		double TDATA_04 = 0;
		int TDATA_05 = 0;
		double TDATA_06 = 0;
		double TDATA_07 = 0;
		double TDATA_08 = 0;
		double TDATA_09 = 0;
		int TDATA_10 = 0;
		double TDATA_11 = 0;
		double TDATA_12 = 0;
		double TDATA_13 = 0;
		double TDATA_14 = 0;
		double TDATA_15 = 0;
		double inday=0;//平均住院天数合计
		double beday=0;//术前
		double sumTot=0;//总费用
		double sumdurg=0;//总药品
		double sumdata1=0;//住院费
		double sumdata2=0;//手术费
		double sumdata3=0;//检查治疗
		int data_04Rows = 0, data_06Rows = 0, data_07Rows = 0, data_08Rows = 0;
		int data_09Rows = 0, data_11Rows = 0, data_12Rows = 0, data_13Rows = 0;
		int data_14Rows = 0, data_15Rows = 0;
		for (int i = 0; i < dr_list.getCount("USER_ID"); i++) {
			String dr_code = dr_list.getValue("USER_ID", i);// 获取医师CODE
			String dr_name = dr_list.getValue("USER_NAME", i);// 获取医师姓名
			// 定义各个字段的变量
			int DATA_01 = 0;
			int DATA_02 = 0;
			int DATA_03 = 0;
			double DATA_04 = 0;
			int DATA_05 = 0;
			double DATA_06 = 0;
			double DATA_07 = 0;
			double DATA_08 = 0;
			double DATA_09 = 0;
			int DATA_10 = 0;
			double DATA_11 = 0;
			double DATA_12 = 0;
			double DATA_13 = 0;
			double DATA_14 = 0;
			double DATA_15 = 0;
			int WJNum = 0;// 无菌切口手术例数
			// 循环提取数据
			for (int j = 0; j < selectDATA_01.getCount(); j++) {
				if (selectDATA_01.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_01 = selectDATA_01.getInt("NUM", j);// 术者例数
				}
			}
			for (int j = 0; j < selectDATA_02.getCount(); j++) {
				if (selectDATA_02.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_02 = selectDATA_02.getInt("NUM", j);// 一助例数
				}
			}
			for (int j = 0; j < selectDATA_03.getCount(); j++) {
				if (selectDATA_03.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_03 = selectDATA_03.getInt("NUM", j);// I 类切口，甲级愈合例数
				}
			}
			for (int j = 0; j < selectDATA_05.getCount(); j++) {
				if (selectDATA_05.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_05 = selectDATA_05.getInt("NUM", j);// I 类切口感染例数
				}
			}
			for (int j = 0; j < selectWJNum.getCount(); j++) {
				if (selectWJNum.getValue("DR_CODE", j).equals(dr_code)) {
					WJNum = selectWJNum.getInt("NUM", j);// 无菌切口手术例数
				}
			}
			for (int j = 0; j < selectDATA_07.getCount(); j++) {
				if (selectDATA_07.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_07 = selectDATA_07.getInt("NUM", j);// 平均住院天数
					inday+=DATA_07*DATA_01;
				}
			}
			for (int j = 0; j < selectDATA_08.getCount(); j++) {
				if (selectDATA_08.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_08 = selectDATA_08.getInt("NUM", j);// 术前平均住院天数
					beday+=DATA_08*DATA_01;
				}
			}
			for (int j = 0; j < selectDATA_09.getCount(); j++) {
				if (selectDATA_09.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_09 = DATA_07-DATA_08;// 术后平均住院天数
				}
			}
			for (int j = 0; j < selectDATA_10.getCount(); j++) {
				if (selectDATA_10.getValue("DR_CODE", j).equals(dr_code)) {
					DATA_10 = selectDATA_10.getInt("NUM", j);// 术后10日死亡例数
				}
			}
			TParm selectDATA_11 = selectIBS(dr_code, DATE_S, DATE_E);
			//住院小计 不包含药费
	        double admSum =selectDATA_11.getDouble("CHARGE01") + selectDATA_11.getDouble("CHARGE02") +
	        selectDATA_11.getDouble("CHARGE07") + selectDATA_11.getDouble("CHARGE08") +
	        selectDATA_11.getDouble("CHARGE09") + selectDATA_11.getDouble("CHARGE10") +
	        selectDATA_11.getDouble("CHARGE11") + selectDATA_11.getDouble("CHARGE12") +
	        selectDATA_11.getDouble("CHARGE13") + selectDATA_11.getDouble("CHARGE14") +
	        selectDATA_11.getDouble("CHARGE15") + selectDATA_11.getDouble("CHARGE17") +
	        selectDATA_11.getDouble("CHARGE18") + selectDATA_11.getDouble("CHARGE19")+ selectDATA_11.getDouble("CHARGE20");
	        //药品住院收入
	        double admDrug = (selectDATA_11.getDouble("CHARGE06") + selectDATA_11.getDouble("CHARGE03") +
	        		selectDATA_11.getDouble("CHARGE04") + selectDATA_11.getDouble("CHARGE05"));
	        sumTot+=(admSum+admDrug);
	        sumdurg+=admDrug;
	        sumdata1+=(selectDATA_11.getDouble("CHARGE01")
					+ selectDATA_11.getDouble("CHARGE02"));
	        sumdata2+=selectDATA_11.getDouble("CHARGE10");
	        sumdata3+=admSum-selectDATA_11.getDouble("CHARGE01")
					-selectDATA_11.getDouble("CHARGE02")-selectDATA_11.getDouble("CHARGE10");
			DATA_11 = (admSum+admDrug)/DATA_01; // 平均总费用
			DATA_12 = (selectDATA_11.getDouble("CHARGE01")
					+ selectDATA_11.getDouble("CHARGE02"))/DATA_01;// 平均住院费
			DATA_13 = admDrug/DATA_01;// 平均药费
			DATA_14 = selectDATA_11.getDouble("CHARGE10")/DATA_01;// 平均手术费
			DATA_15 = (DATA_11 - DATA_12 - DATA_13 - DATA_14);// 平均检查治疗费
			if (WJNum != 0) {// 无菌切口手术例数
				DATA_04 = (double) DATA_03 / (double) WJNum * 100;// I 类切口甲级愈合率
				DATA_06 = (double) DATA_05 / (double) WJNum * 100;// I 类切口感染率
			}
			// 填充数据
				result.addData("DR_NAME", dr_name);
				result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
				result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
				result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
				result.addData("DATA_04", DATA_04 == 0 ? "" : df
						.format(DATA_04));
				result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
				result.addData("DATA_06", DATA_06 == 0 ? "" : df
						.format(DATA_06));
				result.addData("DATA_07", DATA_07 == 0 ? "" : df
						.format(DATA_07));
				result.addData("DATA_08", DATA_08 == 0 ? "" : df
						.format(DATA_08));
				result.addData("DATA_09", DATA_09 == 0 ? "" : df
						.format(DATA_09));
				result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
				result.addData("DATA_11", DATA_11 == 0 ? "" : df
						.format(DATA_11));
				result.addData("DATA_12", DATA_12 == 0 ? "" : df
						.format(DATA_12));
				result.addData("DATA_13", DATA_13 == 0 ? "" : df
						.format(DATA_13));
				result.addData("DATA_14", DATA_14 == 0 ? "" : df
						.format(DATA_14));
				result.addData("DATA_15", DATA_15 == 0 ? "" : df
						.format(DATA_15));
				TDATA_01 += DATA_01;
				TDATA_02 += DATA_02;
				TDATA_03 += DATA_03;
				TDATA_04 += DATA_04;
				TDATA_05 += DATA_05;
				TDATA_06 += DATA_06;
				TDATA_07 += DATA_07;
				TDATA_08 += DATA_08;
				TDATA_09 += DATA_09;
				TDATA_10 += DATA_10;
				TDATA_11 += DATA_11;
				TDATA_12 += DATA_12;
				TDATA_13 += DATA_13;
				TDATA_14 += DATA_14;
				TDATA_15 += DATA_15;
				
		}
		result.addData("DR_NAME", "合计:");
		result.addData("DATA_01", TDATA_01 == 0 ? "" : TDATA_01);
		result.addData("DATA_02", TDATA_02 == 0 ? "" : TDATA_02);
		result.addData("DATA_03", TDATA_03 == 0 ? "" : TDATA_03);
		result.addData("DATA_04", TDATA_04 == 0 ? "" : df.format((TDATA_03
				/ TDATA_01)*100));
		result.addData("DATA_05", TDATA_05 == 0 ? "" : TDATA_05);
		result.addData("DATA_06", TDATA_06 == 0 ? "" : df.format((TDATA_05
				/ TDATA_01)*100));
		result.addData("DATA_07", TDATA_07 == 0 ? "" : df.format(inday
				/ TDATA_01));
		result.addData("DATA_08", TDATA_08 == 0 ? "" : df.format(beday
				/ TDATA_01));
		result.addData("DATA_09", TDATA_09 == 0 ? "" : df.format((inday-beday)
				/ TDATA_01));
		result.addData("DATA_10", TDATA_10 == 0 ? "" : TDATA_10);
		result.addData("DATA_11", TDATA_11 == 0 ? "" : df.format(sumTot
				/ TDATA_01));// 未确定
		result.addData("DATA_12", TDATA_12 == 0 ? "" : df.format(sumdata1
				/ TDATA_01));// 未确定
		result.addData("DATA_13", TDATA_13 == 0 ? "" : df.format(sumdurg
				/ TDATA_01));// 未确定
		result.addData("DATA_14", TDATA_14 == 0 ? "" : df.format(sumdata2
				/ TDATA_01));// 未确定
		result.addData("DATA_15", TDATA_15 == 0 ? "" : df.format(sumdata3
				/ TDATA_01));// 未确定
		return result;
	}

	/**
	 * 查询科室列表
	 * 
	 * @return TParm
	 */
	public TParm selectDept() {
		TParm result = this.query("selectDept");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询医师列表
	 * 
	 * @return TParm ================pangben modify 20110525 添加区域参数
	 */
	public TParm selectDr(String regionCode) {
		TParm parm = new TParm();
		// ================pangben modify 20110525 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ================pangben modify 20110525 stop
		TParm result = this.query("selectDr", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据本门查询医师
	 * 
	 * @return TParm ============pangben modify 20110525 添加区域参数
	 */
	public TParm selectDyByDept(String dept_code, String regionCode) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", dept_code);
		// ============pangben modify 20110525 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110525 stop
		TParm result = this.query("selectDyByDept", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据IBS系统返回的住院费用
	 * 
	 */
	public TParm selectIBS(String drcode, String start, String end) {
		TParm inparm = new TParm();
		inparm.setData("DR_CODE", drcode);
		inparm.setData("DATE_S", start);
		inparm.setData("DATE_E", end);
		TParm ibs = this.query("selectDATA_11", inparm);
		if (ibs.getErrCode() < 0) {
			err("ERR:" + ibs.getErrCode() + ibs.getErrText() + ibs.getErrName());
			return ibs;
		}
		// 以键值对的形式存储财务数据
		Map charge = new HashMap();
		double sumTot = 0;
		double ownTot = 0;
		Map MrofeeCode = STATool.getInstance().getIBSChargeName();
		for (int i = 0; i < ibs.getCount(); i++) {
			if (ibs.getValue("IPD_CHARGE_CODE", i).length() > 0) {
				charge.put(ibs.getValue("IPD_CHARGE_CODE", i), ibs.getValue(
						"TOT_AMT", i));
			}
		}
		String seq = "";
		String c_name = "";
		TParm parm = new TParm();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE";
			if (i < 10)// I小于10 补零
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			parm.setData(c_name, charge.get(MrofeeCode.get(c_name)) == null ? 0
					: charge.get(MrofeeCode.get(c_name)));
		}
		return parm;
	}

}
