package com.javahis.ui.udd;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 抗菌药物使用病例微生物标本送检率统计
 * </p>
 * 
 * <p>
 * Description: 抗菌药物使用病例微生物标本送检率统计
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author duzhw 2013.08.06
 * @version
 */

public class UDDRateStatisticsControl extends TControl {

	private TTable table_z;
	private TTable table_m;

	public UDDRateStatisticsControl() {
		super();
	}

	/**
	 * 初始化方法
	 */

	public void onInit() {
		initPage();
		this.onClear();
	}

	private void initPage() {
		table_z = (TTable) this.getComponent("TABLE_Z");
		table_m = (TTable) this.getComponent("TABLE_M");

		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// 开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// 结束时间
		// 测试用数据
		// this.setValue("USER_ID", "000623a");
		this.setValue("USER_NAME", Operator.getID());
		this.setValue("REGION_CODE", Operator.getRegion());

	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (this.getValueString("START_DATE").length() == 0) {
			messageBox("开始时间不正确!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("结束时间不正确!");
			return;
		}
		String regionCode = getValueString("REGION_CODE"); // 区域
		if (regionCode.length() == 0) {
			messageBox("区域不能为空!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		String patientState = getValueString("PATIENT_STATE"); // 住院状态
		String deptCode = getValueString("DEPT_CODE"); // 科室
		String drCode1 = getValueString("DR_CODE1"); // 医生-住院页签选项
		String drCode2 = getValueString("DR_CODE2"); // 医生-门急诊页签选项
		String caseCode = getValueString("CASE_CODE"); // 门急诊类别

		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() == 0) { // 页签一：住院GRID
			getQueryParm1(startTime, endTime, patientState, deptCode, drCode1);
		}
		if (tab.getSelectedIndex() == 1) { // 页签二：门急诊
			getQueryParm2(startTime, endTime, drCode2, caseCode, deptCode);
		}
	}
	private void getShowParm(TParm sumParm ,String regionCode,String dsType,String vsDrCode,
			double allNum,String dept_code,double sjNum,String statis){
		sumParm.addData("IN_STATION_CODE", regionCode);
		sumParm.addData("DS_TYPE", dsType);
		sumParm.addData("DEPT_CHN_DESC", dept_code);
		sumParm.addData("USER_NAME", vsDrCode);
		sumParm.addData("ALLNUM", allNum);
		sumParm.addData("SJ_NUM", sjNum);
		sumParm.addData("STATIS", statis);
	}
	/**
	 * 查询第一个页签数据
	 */
	public void getQueryParm1(String startTime, String endTime,
			String patientState, String deptCode, String drCode1) {
		String anti_type = getValueString("ANTI_TYPE"); // yanjing 20131206 抗菌药物类型
		// *********住院 -查询为01的数据--将查询数据封装到returnParm中*********
		String sumSql="SELECT SS.REGION_CODE ,SS.DEPT_CODE,SS.VS_DR_CODE,SS.DS_TYPE, COUNT(SS.CASE_NO) ALLNUM FROM ( SELECT A.REGION_CODE,CASE WHEN A.DS_DATE IS NULL THEN  '在院' ELSE  '出院' END DS_TYPE," +
				"A.DEPT_CODE ,A.VS_DR_CODE,A.CASE_NO FROM ADM_INP A, ODI_ORDER B,SYS_FEE C, (SELECT A.CASE_NO FROM  ADM_INP A, ODI_ORDER B, SYS_FEE C ,SYS_DEPT D, SYS_OPERATOR E, SYS_REGION Z  " +
				"WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE AND A.REGION_CODE=Z.REGION_CODE"
						+ " AND A.VS_DR_CODE = E.USER_ID AND B.ORDER_CODE = C.ORDER_CODE AND B.ANTIBIOTIC_WAY='02'  "
						+ " AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND A.DS_DATE BETWEEN TO_DATE('"
						+ startTime
						+ "', 'YYYYMMDDHH24MISS') AND"
						+ " TO_DATE('"
						+ endTime
						+ "', 'YYYYMMDDHH24MISS')";
		//String sql=" GROUP BY A.CASE_NO ) ";
		// *********住院-这个是住院所有的病患数据不相同的数据*********
		StringBuffer sql1_2 = new StringBuffer();
		sql1_2.append("SELECT S.IN_STATION_CODE,S.DS_TYPE,S.DEPT_CODE,S.DEPT_CHN_DESC, S.VS_DR_CODE,S.REGION_CODE, S.USER_NAME,COUNT(S.CASE_NO) ALLNUM,0 SJ_NUM,'0%' STATIS FROM (")
				.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE,CASE WHEN A.DS_DATE IS NULL THEN  '在院' ELSE  '出院' END DS_TYPE,"
						+ " A.DEPT_CODE,D.DEPT_CHN_DESC, A.VS_DR_CODE, E.USER_NAME,A.CASE_NO,Z.REGION_CODE "
						+ " FROM ADM_INP A, ODI_ORDER B, SYS_FEE C, SYS_DEPT D, SYS_OPERATOR E, SYS_REGION Z"
						+ " WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE AND A.REGION_CODE=Z.REGION_CODE"
						+ " AND A.VS_DR_CODE = E.USER_ID AND B.ORDER_CODE = C.ORDER_CODE AND B.ANTIBIOTIC_WAY='02'  "
						+ " AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND A.DS_DATE BETWEEN TO_DATE('"
						+ startTime
						+ "', 'YYYYMMDDHH24MISS') AND"
						+ " TO_DATE('"
						+ endTime
						+ "', 'YYYYMMDDHH24MISS')");
		
		//====================yanjing 20131206 添加抗菌药物类型 start
		if ("1".equals(anti_type)) {// 限制类抗菌素
			sql1_2.append(" AND B.ANTIBIOTIC_CODE='02'");
			sumSql+=" AND B.ANTIBIOTIC_CODE='02'";
		} else if ("2".equals(anti_type)) {//特殊类抗菌素
			sql1_2.append(" AND B.ANTIBIOTIC_CODE='03'");
			sumSql+=" AND B.ANTIBIOTIC_CODE='03'";
		}else if("3".equals(anti_type)||"".equals(anti_type)||anti_type.equals(null)){//全部抗菌素
			sql1_2.append(" AND B.ANTIBIOTIC_CODE IS NOT NULL");
			sumSql+=" AND B.ANTIBIOTIC_CODE IS NOT NULL";
		}
		//====================yanjing 20131206 添加抗菌药物类型 end
		
		if ("1".equals(patientState)) {// 在院
			sql1_2.append(" AND A.DS_DATE IS NULL");
			//sql1_1.append(" AND A.DS_DATE IS NULL");
			sumSql+=" AND A.DS_DATE IS NULL";
		} else if ("2".equals(patientState)) {// 出院
			sql1_2.append(" AND A.DS_DATE IS NOT NULL");
			//sql1_1.append(" AND A.DS_DATE IS NOT NULL");
			sumSql+=" AND A.DS_DATE IS NOT NULL";
		}
		
		if (deptCode.toString().length() > 0) {// 科室
			sql1_2.append(" AND D.DEPT_CODE = '" + deptCode + "'");
			sumSql+=" AND D.DEPT_CODE = '" + deptCode + "'";
		}
		if (drCode1.toString().length() > 0) {// 医生
			sql1_2.append(" AND A.VS_DR_CODE = '" + drCode1 + "'");
			sumSql+=" AND A.VS_DR_CODE = '" + drCode1 + "'";
		}
		sql1_2.append(" GROUP BY Z.REGION_CHN_ABN, Z.REGION_CODE,A.DS_DATE,A.DEPT_CODE, D.DEPT_CHN_DESC, A.VS_DR_CODE,E.USER_NAME,A.CASE_NO ) S ")
						.append("GROUP BY S.REGION_CODE,S.IN_STATION_CODE,S.DS_TYPE,S.DEPT_CODE,S.DEPT_CHN_DESC, S.VS_DR_CODE, S.USER_NAME ")
				.append(" ORDER BY S.REGION_CODE,S.DS_TYPE,S.DEPT_CODE, S.VS_DR_CODE ");
//		System.out.println("输出看看sql1_2：：： "+sql1_2.toString());
		TParm returnParm2 = new TParm(TJDODBTool.getInstance().select(
				sql1_2.toString()));
		// 去掉 returnParm2中相同多余的数据
		sumSql+=" GROUP BY A.CASE_NO ) S WHERE A.CASE_NO=S.CASE_NO AND A.CASE_NO=B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE AND C.ORD_SUPERVISION = '01' " +
				" GROUP BY A.REGION_CODE,A.DS_DATE,A.DEPT_CODE ,A.VS_DR_CODE,A.CASE_NO ) SS " +
		"GROUP BY SS.REGION_CODE, SS.DS_TYPE, SS.DEPT_CODE, SS.VS_DR_CODE"+		
		" ORDER BY SS.REGION_CODE, SS.DS_TYPE, SS.DEPT_CODE, SS.VS_DR_CODE";
//		System.out.println("输出看看sumSql：：： "+sumSql);
		TParm returnParm1 = new TParm(TJDODBTool.getInstance().select(sumSql));
		/**
		 * 统计 returnParm2中数据放到新parm中 相同院区、住院状态、科室、医生、CASE_NO 不同 ord_supervision
		 * = '01'的个数为 统计：【总人数】为ord_supervision 的总数
		 * 统计：【抗菌药物病例送检数量】ord_supervision = '01'的总数 计算：【抗菌药物送检率 =
		 * 抗菌药物病例送检数量/总人数】 显示：【院区(IN_STATION_CODE) 住院状态(DS_TYPE)
		 * 科室DEPT_CHN_DESC(DEPT_CODE) 医生USER_NAME(DR_CODE) 总人数(ALLNUM)
		 * 抗菌药物病例送检数量(SJ_NUM) 抗菌药物送检率(STATIS)】
		 */
		String regionCode="";//区域
		String dsType="";//状态
		String dept_code="";//科室
		String drCode="";//医生
		if (returnParm2.getCount()<=0) {
			this.messageBox("没有需要查询的数据");
			table_z.setParmValue(new TParm());
			return;
		} 
	//	String sql1Temp=null;
		//抗菌药品累计合并
		for (int i = 0; i < returnParm2.getCount(); i++) {
			
			regionCode=returnParm2.getValue("REGION_CODE",i);
			dsType=returnParm2.getValue("DS_TYPE",i);
			dept_code=returnParm2.getValue("DEPT_CODE",i);
			drCode=returnParm2.getValue("VS_DR_CODE",i);
			for (int j = 0; j < returnParm1.getCount(); j++) {
				if (regionCode.equals(returnParm1.getValue("REGION_CODE",j))
						&&dsType.equals(returnParm1.getValue("DS_TYPE",j))
								&&dept_code.equals(returnParm1.getValue("DEPT_CODE",j))&&
										drCode.equals(returnParm1.getValue("VS_DR_CODE",j))) {
					returnParm2.setData("SJ_NUM", i, returnParm1.getInt("ALLNUM",j));
					returnParm2.setData("STATIS", i,StringTool.round(returnParm1.getDouble("ALLNUM",j)/returnParm2.getDouble("ALLNUM",i)*100, 2) +"%");
					break;
				}
			}
		}
		regionCode=returnParm2.getValue("REGION_CODE",0);
		dsType=returnParm2.getValue("DS_TYPE",0);
		dept_code=returnParm2.getValue("DEPT_CODE",0);
		drCode=returnParm2.getValue("VS_DR_CODE",0);
		//String userName="";
		String deptDesc="";
		double sum=0;//小计
		double codeSum=0;//小计抗菌
		double numSum=0;//总计
		double codeNumSum=0;//总计抗菌
		TParm sumParm=new TParm();
		//科室累计合计
		for (int i = 0; i < returnParm2.getCount(); i++) {
			if (regionCode.equals(returnParm2.getValue("REGION_CODE",i))
					&&dsType.equals(returnParm2.getValue("DS_TYPE",i))
							&&dept_code.equals(returnParm2.getValue("DEPT_CODE",i))) {
				getShowParm(sumParm, returnParm2.getValue("IN_STATION_CODE",i), dsType, returnParm2.getValue("USER_NAME",i), 
						returnParm2.getInt("ALLNUM",i),returnParm2.getValue("DEPT_CHN_DESC",i), 
						returnParm2.getInt("SJ_NUM",i), returnParm2.getValue("STATIS",i));
				sum+=returnParm2.getInt("ALLNUM",i);//小计
				codeSum+=returnParm2.getInt("SJ_NUM",i);//抗菌药品小计
				numSum+=returnParm2.getInt("ALLNUM",i);//总计
				codeNumSum+=returnParm2.getInt("SJ_NUM",i);//抗菌药品总计
				//userName=returnParm2.getValue("USER_NAME",i);
				deptDesc=returnParm2.getValue("DEPT_CHN_DESC",i);
			}else{
				getShowParm(sumParm, "小计", dsType, "", 
						sum, deptDesc, 
						codeSum,StringTool.round(codeSum/sum *100,2)+"%");
				regionCode=returnParm2.getValue("REGION_CODE",i);
				dsType=returnParm2.getValue("DS_TYPE",i);
				dept_code=returnParm2.getValue("DEPT_CODE",i);
				drCode=returnParm2.getValue("VS_DR_CODE",i);
				//userName=returnParm2.getValue("USER_NAME",i);
				deptDesc=returnParm2.getValue("DEPT_CHN_DESC",i);
				sum=returnParm2.getInt("ALLNUM",i);//小计
				codeSum=returnParm2.getInt("SJ_NUM",i);//抗菌药品小计
				numSum+=returnParm2.getInt("ALLNUM",i);//总计
				codeNumSum+=returnParm2.getInt("SJ_NUM",i);//抗菌药品总计
				getShowParm(sumParm, returnParm2.getValue("IN_STATION_CODE",i), dsType, returnParm2.getValue("USER_NAME",i), 
						returnParm2.getInt("ALLNUM",i), returnParm2.getValue("DEPT_CHN_DESC",i), 
						returnParm2.getInt("SJ_NUM",i), returnParm2.getValue("STATIS",i));
			}
			if (i==returnParm2.getCount()-1) {
				getShowParm(sumParm, "小计", dsType, "", 
						sum, deptDesc, 
						codeSum,StringTool.round(codeSum/sum *100,2)+"%");
			}
		}
		getShowParm(sumParm, "总计", "", "", 
				numSum, "", 
				codeNumSum,StringTool.round(codeNumSum/numSum *100,2)+"%");
		sumParm.setCount(sumParm.getCount("IN_STATION_CODE"));
		this.table_z.setParmValue(sumParm);
	}

	/**
	 * 查询第二个页签数据
	 */
	public void getQueryParm2(String startTime, String endTime, String drCode2,
			String caseCode, String deptCode) {

		// *********sql2_1-门诊-统计总人数 科室 医生*********
		StringBuffer sql2_1 = new StringBuffer();

		sql2_1
				.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE, COUNT(DISTINCT(A.CASE_NO)) ALLNUM,"
						+ " DECODE(A.ADM_TYPE, 'O', '门诊', 'E', '急诊') ADM_TYPE,"
						+ " C.USER_NAME,C.USER_ID,D.DEPT_CHN_DESC,D.DEPT_CODE"
						+ " FROM REG_PATADM A, OPD_ORDER B, SYS_OPERATOR C, SYS_DEPT D, SYS_REGION Z"
						+ " WHERE REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO AND A.REGION_CODE=Z.REGIN_CODE "
						+ " AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND A.DEPT_CODE = D.DEPT_CODE AND A.DR_CODE = C.USER_ID  ");

		if ("O".equals(caseCode)) {// 门诊-判断日期为 ADM_DATE
			sql2_1.append(" AND (A.ADM_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_1.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		} else if ("E".equals(caseCode)) {// 急诊-判断日期为 REG_DATE
			sql2_1.append(" AND (A.REG_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_1.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		}

		if (drCode2.toString().length() > 0) {// 医生
			sql2_1.append(" AND A.DR_CODE = '" + drCode2 + "'");
		}
		if (deptCode.toString().length() > 0) {// 科室
			sql2_1.append(" AND A.DEPT_CODE = '" + deptCode + "'");
		}
		sql2_1
				.append(" GROUP BY Z.REGION_CHN_ABN , A.ADM_TYPE,C.USER_NAME, C.USER_ID, D.DEPT_CHN_DESC, D.DEPT_CODE ");
		sql2_1
				.append(" ORDER BY Z.REGION_CHN_ABN , A.ADM_TYPE,D.DEPT_CODE, C.USER_ID ");
		TParm returnParm1 = new TParm(TJDODBTool.getInstance().select(
				sql2_1.toString()));
		// *********sql2_2-门诊-抗菌药物送检数量*********
		StringBuffer sql2_2 = new StringBuffer();

		sql2_2
				.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE, COUNT(DISTINCT(A.CASE_NO)) SJ_NUM,"
						+ " DECODE(A.ADM_TYPE, 'O', '门诊', 'E', '急诊') ADM_TYPE,"
						+ " D.USER_NAME,D.USER_ID,B.DEPT_CODE,E.DEPT_CHN_DESC"
						+ " FROM REG_PATADM A, OPD_ORDER B, SYS_FEE C,SYS_OPERATOR D,SYS_DEPT E, SYS_REGION Z"
						+ " WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND A.REGION_CODE=Z.REGION_CODE "
						+ " AND A.DR_CODE = D.USER_ID AND E.DEPT_CODE = B.DEPT_CODE"
						+ " AND A.REGCAN_USER IS NULL AND C.ORD_SUPERVISION = '01'"
						+ " AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND B.CAT1_TYPE = 'LIS' AND B.ORDER_CODE = B.ORDERSET_CODE"
						+ " AND B.SETMAIN_FLG = 'Y' AND B.BILL_FLG = 'Y'");

		if ("O".equals(caseCode)) {// 门诊-判断日期为 ADM_DATE
			sql2_2.append(" AND (A.ADM_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_2.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		} else if ("E".equals(caseCode)) {// 急诊-判断日期为 REG_DATE
			sql2_2.append(" AND (A.REG_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_2.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		}
		if (drCode2.toString().length() > 0) {// 医生
			sql2_2.append(" and A.DR_CODE = '" + drCode2 + "'");
		}
		if (deptCode.toString().length() > 0) {// 科室
			sql2_2.append(" AND A.DEPT_CODE = '" + deptCode + "'");
		}
		sql2_2
				.append(" GROUP BY Z.REGION_CHN_ABN , A.ADM_TYPE,D.USER_NAME,D.USER_ID,B.DEPT_CODE,E.DEPT_CHN_DESC ");
		sql2_2
				.append(" ORDER BY Z.REGION_CHN_ABN , A.ADM_TYPE,B.DEPT_CODE,D.USER_ID ");
		TParm returnParm2 = new TParm(TJDODBTool.getInstance().select(
				sql2_2.toString()));
		/**
		 * 合并returnParm1、returnParm2 为新TParm 合并后TParm存储格式： 院区(IN_STATION_CODE)
		 * 门急别(ADM_TYPE) 科室DEPT_CHN_DESC(DEPT_CODE) 医生USER_NAME(DR_CODE)
		 * 总人数(ALLNUM) 抗菌药物病例送检数量(SJ_NUM) 抗菌药物送检率(STATIS) 1、循环遍历returnParm1
		 */
		// TParm returnParm3 = new TParm();
		for (int j = 0; j < returnParm1.getCount(); j++) {
			// 取出returnParm1中的门急别、科室、医生
			String adm_type1 = returnParm1.getValue("ADM_TYPE", j);
			String dept_code1 = returnParm1.getValue("DEPT_CODE", j);
			String dr_code1 = returnParm1.getValue("USER_ID", j);
			String adm_type2 = "";
			String dept_code2 = "";
			String dr_code2 = "";
			double sj_num = 0.00;
			for (int i = 0; i < returnParm2.getCount(); i++) {
				// 取出returnParm2中的门急别、科室、医生
				adm_type2 = returnParm2.getValue("ADM_TYPE", i);
				dept_code2 = returnParm2.getValue("DEPT_CODE", i);
				dr_code2 = returnParm2.getValue("USER_ID", i);
				if (adm_type1.equals(adm_type2)
						&& dept_code1.equals(dept_code2)
						&& dr_code1.equals(dr_code2)) {
					sj_num = returnParm2.getDouble("SJ_NUM", i);
				}
			}
			returnParm1.setData("IN_STATION_CODE", j, returnParm2.getValue(
					"IN_STATION_CODE", j));
			returnParm1.setData("SJ_NUM", j, sj_num);// 送检总数
		}

		// 小计
		int all_count = 0;
		int all_sj_count = 0;
		int j = 0;// 记录加入“小计”和“总计”后，parm的行数
		// 计算“总计”
		double tot_sj_count = 0.0;
		double tot_count = 0.0;
		TParm newParm = new TParm();
		for (int i = 0; i < returnParm1.getCount(); i++) {
			double rx_no = returnParm1.getDouble("ALLNUM", i);
			double rx_sj_no = returnParm1.getDouble("SJ_NUM", i);
			// 计算总计
			double rx_sj_rate = 0;
			tot_count += rx_no;
			tot_sj_count += rx_sj_no;
			String real_rate = "";
			if (rx_sj_no <= 0) {
				real_rate = rx_sj_rate + "%" + "";
				returnParm1.setData("STATIS", i, real_rate);
			} else {
				rx_sj_rate = rx_sj_no / rx_no;
				BigDecimal bdi = new BigDecimal(rx_sj_rate);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				rx_sj_rate = bdi.multiply(parmMuli).doubleValue();
				real_rate = rx_sj_rate + "%" + "";
				returnParm1.setData("STATIS", i, real_rate);
			}
			String dept_chn_code = returnParm1.getValue("DEPT_CHN_DESC", i);
			String d_code = returnParm1.getValue("DEPT_CODE", i);
			String next_dr_code = returnParm1.getValue("DEPT_CODE", i + 1);
			if (i == 0 || (d_code.equals(next_dr_code))) {
				all_count += rx_no;
				all_sj_count += rx_sj_no;
				newParm.addRowData(returnParm1, i);
				j++;
			} else {
				all_count += rx_no;
				all_sj_count += rx_sj_no;
				newParm.addRowData(returnParm1, i);
				double rate = (double) all_sj_count / (double) all_count;
				BigDecimal bdi1 = new BigDecimal(rate);
				bdi1 = bdi1.setScale(4, 4);
				BigDecimal parmMuli1 = new BigDecimal(100);
				parmMuli1 = parmMuli1.setScale(0);
				rate = bdi1.multiply(parmMuli1).doubleValue();
				real_rate = rate + "%" + "";
				returnParm1.addData("STATIS", real_rate);
				newParm.setData("IN_STATION_CODE", ++j, "小计:");
				newParm.setData("ADM_TYPE", ++j, "");
				newParm.setData("DEPT_CHN_DESC", ++j, dept_chn_code);
				newParm.setData("USER_NAME", ++j, "");
				newParm
						.setData("ALLNUM", ++j, all_count == 0 ? "0"
								: all_count);
				newParm.setData("SJ_NUM", ++j, all_sj_count == 0 ? "0"
						: all_sj_count);
				newParm.setData("STATIS", ++j, real_rate);
				j++;
				all_count = 0;
				all_sj_count = 0;
			}

		}
		// 计算总计中送检率
		String real_rate1 = "0%";
		if (tot_count != 0) {
			double rate3 = tot_sj_count / tot_count;
			BigDecimal bdi1 = new BigDecimal(rate3);
			bdi1 = bdi1.setScale(4, 4);
			BigDecimal parmMuli1 = new BigDecimal(100);
			parmMuli1 = parmMuli1.setScale(0);
			rate3 = bdi1.multiply(parmMuli1).doubleValue();
			real_rate1 = rate3 + "%" + "";
		}

		// 添加“总计”
		newParm.setData("IN_STATION_CODE", j, "总计:");
		newParm.setData("ADM_TYPE", j, "");
		newParm.setData("DEPT_CHN_DESC", j, "");
		newParm.setData("USER_NAME", j, "");
		newParm.setData("ALLNUM", j, tot_count);
		newParm.setData("SJ_NUM", j, tot_sj_count);
		newParm.setData("STATIS", j, real_rate1);
		if (newParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (newParm.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			table_m.removeRowAll();
			return;
		}
		table_m.setParmValue(newParm);

	}

	/**
	 * 导出EXCEL
	 */
	public void onExport() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() == 0) { // 页签一：住院GRID
			onExport1();
		}
		if (tab.getSelectedIndex() == 1) { // 页签二：门急诊
			onExport2();
		}

	}

	public void onExport1() {// 住院
		TTable table_z = getTable("TABLE_Z");
		if (table_z.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table_z,
					"抗菌药物使用病例微生物标本送检率统计-住院");
		} else {
			this.messageBox("没有汇出数据");
			return;
		}
	}

	public void onExport2() {// 门诊
		TTable table_m = getTable("TABLE_M");
		if (table_m.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table_m,
					"抗菌药物使用病例微生物标本送检率统计-门诊");
		} else {
			this.messageBox("没有汇出数据");
			return;
		}

	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TParm clearParm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		// 设置时间控件的值
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		// 清除表格中的数据
		if (tab.getSelectedIndex() == 0) {
			table_z.setParmValue(clearParm);
		} else if (tab.getSelectedIndex() == 1) {
			table_m.setParmValue(clearParm);
		}
		// 清除其他控件的值
		this.clearValue("CASE_CODE;DR_CODE2;DR_CODE1;PATIENT_STATE;DEPT_CODE");
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("PATIENT_STATE", "2");
		this.callFunction("UI|PATIENT_STATE|setEnabled", false);
	}
}
