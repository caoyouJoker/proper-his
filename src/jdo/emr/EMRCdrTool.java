package jdo.emr;



import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;

public class EMRCdrTool {
	public static EMRCdrTool instanceObject;// 实例
	/**
     * 得到实例
     * @return ACIRecordTool
     */
    public static EMRCdrTool getInstance() {
        if (instanceObject == null) instanceObject = new EMRCdrTool();
        return instanceObject;
    }
	/**
	 * 获取病患信息
	 */
	public TParm getPatInfo(TParm param){
		String sql="SELECT PAT_NAME,CASE SEX_TYPE WHEN 'F' THEN '女' WHEN 'M' THEN '男' ELSE '不详' END SEX_CODE,BIRTH_DATE" +
				" FROM CRP_PATIENT ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" WHERE  CASE_NO='"+param.getValue("CASE_NO")+"'";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" WHERE MR_NO='"+param.getValue("MR_NO")+"' ";
		}
		//System.out.println("获取病患信息sql："+sql);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 获取就诊信息
	 * @param caseNo
	 */
	public TParm getAdmInfo(TParm param){
		String sql="SELECT A.ADM_DATE,CASE A.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' END ADM_TYPE," +
				" 	A.DEPT_DESC,A.CLINICAREA_DESC,A.VS_DR_NAME,A.TRIAGE_LEVEL,B.DIAG_CODE,B.DIAG_DESC," +
				" A.STATION_DESC,A.BED_NO,A.NURSING_CLASS SERVICE_LEVEL,C.CLNCPATH_DESC,A.DISCHARGE_DATE " +
				" FROM CRP_VISIT_RECORD A,CRP_DIAGNOSIS B,CRP_CPCASEINFO C WHERE " +
				" A.CASE_NO=B.CASE_NO(+) AND " +
				" A.CASE_NO=C.CASE_NO(+) " ;
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" AND  A.CASE_NO='"+param.getValue("CASE_NO")+"' ORDER BY A.CASE_NO";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" AND  A.MR_NO='"+param.getValue("MR_NO")+"' ORDER BY A.CASE_NO";
		}
		//System.out.println("获取就诊信息sql："+sql);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取次诊断
	 * @param param
	 * @return
	 */
	public TParm getNextDiag(TParm param){
		String sql="SELECT DIAG_DESC FROM CRP_DIAGNOSIS WHERE (IS_MAIN_DIAG IS NULL OR IS_MAIN_DIAG='N') AND CASE_NO='"+param.getValue("CASE_NO")+"'  ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 主诉现病史
	 * @param param
	 * @return
	 */
	public TParm getSubjective(TParm param){
		String sql="SELECT A.SUBJECTIVE,A.OBJECTIVE,A.VISIT_DATE OPT_DATE ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_PRESENT_ILLNESS  A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" FROM CRP_PRESENT_ILLNESS  A,CRP_VISIT_RECORD B WHERE   A.MR_NO='"+param.getValue("MR_NO")+"' ";
			sql=sql+" AND  A.CASE_NO = B.CASE_NO  ";
		}
		
		
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+"ORDER BY A.VISIT_DATE DESC";
		//System.out.println("主诉现病史主诉现病史:::::::::::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 过敏史
	 * @param param
	 * @returnALLERGY
	 */
	public TParm getAllergy(TParm param){
		String sql="SELECT A.*  ";
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" FROM CRP_ALLERGY_HISTORY A where A.MR_NO='"+param.getValue("MR_NO")+"' ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" FROM CRP_ALLERGY_HISTORY A,CRP_VISIT_RECORD B WHERE A.MR_NO='"+param.getValue("MR_NO")+"' ";
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		
		sql=sql+"ORDER BY A.VISIT_DATE DESC";
		//System.out.println("过敏史：：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 既往史
	 * @param param
	 * @returnALLERGY
	 */
	public TParm getMedicalHistory(TParm param){
		String sql="SELECT A.*  ";
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+"  FROM CRP_MEDICAL_HISTORY A where A.MR_NO='"+param.getValue("MR_NO")+"' ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" FROM CRP_MEDICAL_HISTORY A,CRP_VISIT_RECORD B WHERE A.MR_NO='"+param.getValue("MR_NO")+"' AND A.CASE_NO = B.CASE_NO  ";
			sql=sql+"  AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+"  ORDER BY A.VISIT_DATE DESC";
		//System.out.println("既往史:::::::::::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（病史）数据
	 */
	public TParm getHistoryData(TParm param) {
		String sql = "SELECT A.SUBJECTIVE,B.PAST_HISTORY,C.ALLERGEN_NAME FROM CRP_PRESENT_ILLNESS A,CRP_MEDICAL_HISTORY B,CRP_ALLERGY_HISTORY C "
				+ " WHERE A.CASE_NO=B.CASE_NO(+)" + " AND A.CASE_NO=C.CASE_NO(+) ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" AND A.CASE_NO='"+param.getValue("CASE_NO")+"'";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" AND A.MR_NO='"+param.getValue("MR_NO")+"' ";
		}
		//System.out.println("获取（病史）数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（诊断）的数据
	 * @param caseNo
	 */
	public TParm getDiagnosisData(TParm param){
		String sql="SELECT CASE A.DIAG_TYPE WHEN 'I' THEN '门急诊诊断' WHEN 'M' THEN '入院诊断' WHEN 'Q' THEN '感染诊断' WHEN 'W' THEN '并发诊断' " +
				" WHEN 'Z' THEN '拟诊诊断' WHEN 'O' THEN '出院诊断' END DIAG_TYPE," +
				" A.DIAG_CODE ICD_CODE,A.DIAG_DESC,A.IS_MAIN_DIAG,A.DIAG_TIME OPT_DATE,A.DIAG_DEFINITION" +
				" ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_DIAGNOSIS A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			
			sql = sql+" ,CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'";
			sql = sql+" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,";
			sql = sql+" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC";
			
			sql=sql+" FROM CRP_DIAGNOSIS A,CRP_VISIT_RECORD B WHERE   A.MR_NO IN ("+param.getValue("MR_NO")+") AND A.CASE_NO = B.CASE_NO  ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+" ORDER BY A.DIAG_TIME DESC";
		//System.out.println("获取（诊断）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（药嘱）的数据
	 * @param caseNo
	 */
	public TParm getMedData(TParm param){
		String sql="SELECT CASE A.RX_KIND WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'UD' THEN '住院长期' " +
				" WHEN 'ST' THEN '住院临时' WHEN 'DS' THEN '出院带药' ELSE '' END RX_KIND," +
				" A.IVA_LINK_NO,A.DRUG_DESC||' '||A.SPECIFICATION DRUG_DESC,A.GOODS_DESC," +
				" TO_CHAR(A.MEDI_QTY,'fm999999990.099999999')||' '||A.MEDI_UNIT MEDI_QTY,A.FREQUENCY,A.ROUTE," +
				" TO_CHAR(A.DOSAGE_QTY,'fm999999990.099999999')||' '||A.DOSAGE_UNIT DOSAGE_QTY,A.DR_NOTE," +
				" A.START_DATE,A.END_DATE,A.DOSE_DESC,A.CATE1_DESC,A.CATE2_DESC,A.CTRLCLASS_DESC," +
				" CASE A.ANTIBIOTIC_WAY WHEN '01' THEN '预防性' WHEN '02' THEN '治疗性' END ANTIBIOTIC_WAY," +
				" CASE A.ANTIBIOTIC_LEVEL WHEN '01' THEN '非限制类' WHEN '02' THEN '限制类' WHEN '03' THEN '特殊类' END ANTIBIOTIC_LEVEL," +
				" CASE A.STATUS WHEN '1' THEN '医嘱开立' WHEN '2' THEN '护士确认' " +
				" WHEN '3' THEN '药房审核' WHEN '4' THEN '药房调配' WHEN '5' THEN '药房发药' END STATUS" +
				"  ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_MEDICATION A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			
			sql = sql+" ,CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'";
			sql = sql+" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,";
			sql = sql+" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC";
			
			sql=sql+"  FROM CRP_MEDICATION A,CRP_VISIT_RECORD B WHERE  A.MR_NO IN ("+param.getValue("MR_NO")+") AND A.CASE_NO = B.CASE_NO   ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		if (StringUtils.isNotEmpty(param.getValue("FILTER_DATA"))) {
			sql=sql+" AND A.DRUG_CODE like '"+param.getValue("FILTER_DATA")+"%' ";
		}
		sql=sql+"  ORDER BY A.START_DATE DESC,A.ORDER_SEQ,A.IVA_LINK_NO";
		//System.out.println("获取（药嘱）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（检验）的数据
	 * @param caseNo
	 */
	public TParm getLisData(TParm param){
		String sql="SELECT  A.TEST_NAME, A.CATE_DESC,A.DR_NOTE," +
				" A.ORDER_TIME,A.REPORT_TIME," +
				" CASE A.STATUS WHEN 0 THEN '医嘱开立' WHEN 1 THEN '医嘱接收' WHEN 2 THEN '预约' " +
				" WHEN 3 THEN '取消预约' WHEN 4 THEN '到检' WHEN 5 THEN '取消到检'" +
				" WHEN 6 THEN '检查完成' WHEN 7 THEN '报告完成'" +
				" WHEN 8 THEN '报告审核完成' WHEN 9 THEN '医嘱接收' WHEN 10 THEN '取消报告' END STATUS,'N' LIS_WORD, " +
				" A.CAT1_TYPE,A.APPLY_NO,A.LAB_TYPE " +
				" " +
				" " ;
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_LABORDER A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			
			sql = sql+" ,CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'";
			sql = sql+" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,";
			sql = sql+" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC";
			sql=sql+" FROM CRP_LABORDER A,CRP_VISIT_RECORD B WHERE  A.MR_NO IN ("+param.getValue("MR_NO")+") AND A.CASE_NO = B.CASE_NO  ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		if (StringUtils.isNotEmpty(param.getValue("FILTER_DATA"))) {
			sql=sql+"AND A.TEST_CODE like '"+param.getValue("FILTER_DATA")+"%'";
		}
		sql=sql+"  ORDER BY A.ORDER_TIME DESC,A.CATE_CODE";
		//System.out.println("获取（检验）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	 /**
	  * 获取一般检验报告内容
	  * @return
	  */
	public TParm getLisData1(TParm param){
		String sql="SELECT  TEST_NAME,TEST_RESULT,TEST_UNIT,REMARKS," +
				" NORMAL_LOW || CASE WHEN NORMAL_LOW IS NULL OR NORMAL_HIGH IS NULL THEN '' ELSE ' - '|| NORMAL_HIGH END AS HIGHLOW," +
				" CASE LAB_INDICATOR WHEN 'SH' THEN '超高' WHEN 'SL' THEN '超低' " +
				" WHEN 'H' THEN '高值' WHEN 'L' THEN '低值' " +
				" WHEN 'NM' THEN '正常值' ELSE '' END LAB_INDICATOR" +
				" FROM CRP_LIS_RPT WHERE " +
				" CAT1_TYPE='"+param.getValue("CAT1_TYPE")+"' AND APPLY_NO='"+param.getValue("APPLY_NO")+"'";
		//System.out.println("一般检验报告：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取药敏实验报告
	 */
	public TParm getLisAntitest(TParm param){
		
		String sql="SELECT  CULTURE_DESC,ANTI_DESC," +
				" CASE SENS_LEVEL WHEN 'S' THEN '敏感' WHEN 'I' THEN '中介' WHEN 'R' THEN '耐药' END SENS_LEVEL," +
				" TEST_VALUE,REMARKS" +
				" FROM CRP_LIS_ANTITEST WHERE CAT1_TYPE='"+param.getValue("CAT1_TYPE")+"' AND APPLY_NO='"+param.getValue("APPLY_NO")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("药敏报告：："+sql);
		return parm;
	}
	/**
	 * 获取细菌培养实验报告
	 */
	public TParm getLisCulrpt(TParm param){
		String sql="SELECT  CULTURE_DESC,CULTURE_RESULT," +
				" COLONY_COUNT,GRAM_STAIN,INFECT_LEVEL,REMARKS" +
				" FROM CRP_LIS_CULRPT WHERE CAT1_TYPE='"+param.getValue("CAT1_TYPE")+"' AND APPLY_NO='"+param.getValue("APPLY_NO")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//System.out.println("细菌培养：："+sql);
		return parm;
	}
	
	/**
	 * 获取（检查）的数据
	 * @param caseNo
	 */
	public TParm getExmData(TParm param){
		String sql="SELECT A.EXAM_ITEM_DESC,A.EXAM_SITE,A.CATE1_DESC,A.IS_URGENT,A.DR_NOTE,A.ORDER_TIME,A.CHECK_IN_TIME," +
				" A.OUTCOME_DESCRIBE,A.OUTCOME_CONCLUSION," +
				" CASE A.OUTCOME_TYPE WHEN 'H' THEN '阴性' WHEN 'T' THEN '阳性' ELSE NULL END OUTCOME_TYPE," +
				" CASE A.STATUS WHEN 0 THEN '医嘱开立' WHEN 1 THEN '医嘱接收' WHEN 2 THEN '预约' " +
				" WHEN 3 THEN '取消预约' WHEN 4 THEN '到检' WHEN 5 THEN '取消到检'" +
				" WHEN 6 THEN '检查完成' WHEN 7 THEN '报告完成'" +
				" WHEN 8 THEN '报告审核完成' WHEN 9 THEN '医嘱接收' WHEN 10 THEN '取消报告' END STATUS," +
				" 'N' SEEIMAGE,'N' RIS_REPORT,IS_PACS,A.CASE_NO,A.APPLY_NO " +
				" ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_PHYSICAL_EXAM A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql = sql+" ,CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'";
			sql = sql+" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,";
			sql = sql+" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC";
			
			sql=sql+" FROM CRP_PHYSICAL_EXAM A,CRP_VISIT_RECORD B WHERE   A.MR_NO IN ("+param.getValue("MR_NO")+") AND A.CASE_NO = B.CASE_NO   ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		if (StringUtils.isNotEmpty(param.getValue("FILTER_DATA"))) {
			sql=sql+" AND A.EXAM_CODE like '"+param.getValue("FILTER_DATA")+"%'";
		}
		sql=sql+" ORDER BY A.ORDER_TIME DESC,A.EXAM_CODE";
		//System.out.println("获取（检查）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（生命体征）的数据
	 * @param caseNo
	 */
	public TParm getLifeData(TParm param){
		String sql="SELECT TO_CHAR(A.MEASURE_DATE,'yyyy/MM/dd')||' '||A.MEASURE_TIME RECORD_TIME,A.MEASURE_NAME,A.MEASURE_RESULT,B.MEASURE_UNIT," +
		" B.NORMAL_LOW || CASE WHEN B.NORMAL_LOW IS NULL OR B.NORMAL_HIGH IS NULL THEN '' ELSE ' - '|| B.NORMAL_HIGH END AS HIGHLOW," +
		" C.STATION_DESC CLINICAREA_DESC,C.BED_NO,A.REMARKS," +
		" CASE A.SCR_RSLT WHEN 'NM' THEN '正常' WHEN 'H' THEN '高值' WHEN 'L' THEN '低值' WHEN 'SH' THEN '超高' WHEN 'SL' THEN '超低' ELSE NULL END SCR_RSLT"+
		" FROM CRP_VISIT_RECORD C,CRP_VITAL_SIGN A ,CRP_VITAL_CONFIG B " +
		" WHERE 1 = 1 " ;
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+"  AND A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" AND A.MR_NO='"+param.getValue("MR_NO")+"' ";
		}
		sql= sql+" AND A.CASE_NO=C.CASE_NO AND A.MEASURE_CODE=B.MEASURE_CODE(+) " +
				"  ORDER BY B.SEQ ASC,TO_CHAR(A.MEASURE_DATE,'YYYYMMDD')||A.MEASURE_TIME DESC ";
		System.out.println("获取（住院生命体征）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 急诊生命体体征检测记录
	 * @param param
	 * @return
	 */
	public TParm getELifeData(TParm param){
		String sql="SELECT A.MEASURE_NAME,"+
        " TO_DATE(A.MEASURE_DATE,'yyyy/MM/dd')||' '||A.MEASURE_TIME AS RECORD_TIME,"+
        " A.MEASURE_RESULT,"+
        " B.MEASURE_UNIT ,"+
        " B.NORMAL_LOW || CASE WHEN B.NORMAL_LOW IS NULL OR B.NORMAL_HIGH IS NULL THEN '' ELSE ' - '|| B.NORMAL_HIGH END AS HIGHLOW,"+
        " C.STATION_DESC AS CLINICAREA_DESC,"+
        " C.BED_NO "+
		" FROM CRP_VISIT_RECORD C,CRP_VITAL_SIGN A,CRP_VITAL_CONFIG B  "+
		" WHERE 1 = 1";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+"  AND C.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+"  AND C.MR_NO='"+param.getValue("MR_NO")+"' ";
		}
		sql=sql+" AND A.CASE_NO=C.CASE_NO AND A.MEASURE_CODE=B.MEASURE_CODE(+) " +
				" ORDER BY B.SEQ ASC,TO_CHAR(A.MEASURE_DATE,'YYYYMMDD')||A.MEASURE_TIME DESC ";
		System.out.println("获取（急诊生命体征）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 获取（手术）的数据
	 * @param caseNo
	 */
	public TParm getOpeData(TParm param){
		String sql="SELECT A.OP_DESC1,A.OP_DESC2,A.OP_TYPE,A.IS_URGENT,A.IS_ISOLATE,A.OP_LEVEL," +
				" A.OP_RISK,A.OP_WAY,A.OP_SITE,A.INCISION_TYPE,A.ANA_WAY,A.ASA_LEVEL," +
				" CASE A.STATUS WHEN '0' THEN '已申请' WHEN '1' THEN '已排程' WHEN '2' THEN '接患者' WHEN '3' THEN '手术室交接' ELSE A.STATUS END STATUS," +
				" A.APPLY_NO," +
				" 'N' ANA_MED,'N' OPEING,'N' WORD   ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_OPERATION  A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" FROM CRP_OPERATION  A,CRP_VISIT_RECORD B WHERE  A.MR_NO='"+param.getValue("MR_NO")+"' AND A.CASE_NO = B.CASE_NO  ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+"  ORDER BY A.BOOK_DATE DESC";
		//System.out.println("获取（手术）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 *	获取麻醉用药记录
	 * @return
	 */
	public TParm getOpeAnaData(TParm param){
		String sql="SELECT A.START_TIME,A.END_TIME, A.ORDER_DESC," +
				" TO_CHAR(A.EXEC_DOSE,'fm999999990.099999999')||' '||A.MEDI_UNIT_DESC EXEC_DOSE,TO_CHAR(A.DOSAGE_QTY,'fm999999990.099999999') ||' '||A.DOSAGE_UNIT_DESC DOSAGE_QTY," +
				" C.ROUTE_CHN_DESC,A.REMARKS," +
				" CASE A.DRUG_ATTB WHEN '1' THEN '麻醉药品' WHEN '2' THEN '普通药品' WHEN '3' THEN '输液' WHEN '4' THEN '气体' WHEN '5' THEN '诱导用药' ELSE NULL END DRUG_ATTB" +
				" FROM OPE_MAR A,SYS_PHAROUTE C" +
				" WHERE " +
				" A.ROUTE_CODE=C.ROUTE_CODE(+)" +
				" AND A.ADM_TYPE='"+param.getValue("ADM_TYPE")+"'" +
				//" AND A.DRUG_ATTB='1'" +
				" AND A.OPE_BOOK_NO='"+param.getValue("OPE_BOOK_NO")+"' " +
				" AND A.MESSAGE_ID=(SELECT MAX(B.MESSAGE_ID) FROM OPE_MAR B "+
                " WHERE B.OPE_BOOK_NO=A.OPE_BOOK_NO AND B.ORDER_DESC = A.ORDER_DESC "+
                " AND B.START_TIME = A.START_TIME)"+
				" ORDER BY A.START_TIME DESC";
		//System.out.println("麻醉用药数据：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
//	 *	术中监护记录
	 * @return
	 */
	public TParm getOpeLisenerData(TParm param){
		/*String sql="SELECT MONITOR_ITEM_EN,MONITOR_TIME,MONITOR_VALUE,UNIT_DESC FROM OPE_VITALSIGN " +
				" WHERE ADM_TYPE='"+param.getValue("ADM_TYPE")+"'" +
				" AND OPE_BOOK_NO='"+param.getValue("OPE_BOOK_NO")+"'";*/
		String sql="SELECT MONITOR_ITEM  AS MONITOR_ITEM_EN,MONITOR_TIME AS MONITOR_TIME,MONITOR_VALUE AS MONITOR_VALUE," +
				" UNIT_DESC AS UNIT_DESC,NORMAL_RANGE,REMARKS FROM " +
				" (SELECT MONITOR_ITEM_CH AS MONITOR_ITEM,SUBSTR(MONITOR_TIME,1,4)||'/'||SUBSTR(MONITOR_TIME,5,2)||'/'||" +
				" SUBSTR(MONITOR_TIME,7,2)||' '||SUBSTR(MONITOR_TIME,9,2)||':'||SUBSTR(MONITOR_TIME,11,2)||':'||SUBSTR(MONITOR_TIME,13,2) AS MONITOR_TIME," +
				" MONITOR_VALUE,UNIT_DESC,REMARKS,NORMAL_RANGE_L || CASE WHEN NORMAL_RANGE_H IS NOT NULL AND NORMAL_RANGE_L IS NOT NULL THEN '-' ELSE '' END || NORMAL_RANGE_H AS NORMAL_RANGE" +
				" FROM OPE_VITALSIGN WHERE OPE_BOOK_NO = '"+param.getValue("OPE_BOOK_NO")+"'" +
				" UNION ALL" +
				" SELECT IO_DESC AS MONITOR_ITEM,SUBSTR(MONITOR_TIME,1,4)||'/'||SUBSTR(MONITOR_TIME,5,2)||'/'||" +
				" SUBSTR(MONITOR_TIME,7,2)||'/'||' '||SUBSTR(MONITOR_TIME,9,2)||':'||" +
				" SUBSTR(MONITOR_TIME,11,2)||':'||SUBSTR(MONITOR_TIME,13,2) AS MONITOR_TIME,MONITOR_VALUE," +
				" NULL AS NORMAL_RANGE,UNIT_DESC,REMARKS" +
				" FROM OPE_IO_RECORD A " +
				" WHERE A.OPE_BOOK_NO='"+param.getValue("OPE_BOOK_NO")+"'" +
				" AND A.MESSAGE_ID=(SELECT MAX(B.MESSAGE_ID)" +
				" FROM OPE_IO_RECORD B" +
				" WHERE B.OPE_BOOK_NO=A.OPE_BOOK_NO AND B.IO_CODE=A.IO_CODE AND B.MONITOR_TIME = A.MONITOR_TIME))" +
				" C ORDER BY C.MONITOR_ITEM ASC,C.MONITOR_TIME DESC";
		//System.out.println("术 中监护数据：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
		
	}
	
	/**
	 * 获取术中事件数据
	 * @return
	 */
	public TParm getOpeEventData(TParm param){
		String sql = "SELECT A.EVENT_DESC,A.START_TIME,A.END_TIME,A.REMARKS FROM OPE_EVENT A "+
		" WHERE A.OPE_BOOK_NO='"+param.getValue("OPE_BOOK_NO")+"' " +
		" AND A.MESSAGE_ID=(SELECT MAX(B.MESSAGE_ID) " +
		" FROM OPE_EVENT B WHERE B.OPE_BOOK_NO=A.OPE_BOOK_NO " +
		" AND B.EVENT_ID=A.EVENT_ID) ORDER BY START_TIME DESC";
		//System.out.println("::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 调阅病历
	 * @return
	 */
	public TParm getWordPath(TParm param){
		String  sql="SELECT FILE_NAME,FILE_PATH FROM CRP_FILE_INDEX WHERE 1 = 1 " ;
		if(param.getValue("CASE_NO").toString().length()>0 && param.getValue("CASE_NO") != null){
			sql=sql+" AND CASE_NO='"+param.getValue("CASE_NO")+"'";
		}
		if(param.getValue("OPE_BOOK_NO").toString().length()>0 && param.getValue("OPE_BOOK_NO") != null){
			sql=sql+" AND OPE_BOOK_NO='"+param.getValue("OPE_BOOK_NO")+"'";
		}
		if(param.getValue("SUBCLASS_CODE").toString().length()>0 && param.getValue("SUBCLASS_CODE") != null){
			sql=sql+" AND SUBCLASS_CODE='"+param.getValue("SUBCLASS_CODE")+"'";
		}
		if(StringUtils.isNotEmpty(param.getValue("FILE_SEQ"))){
			sql=sql+" AND FILE_SEQ='"+param.getValue("FILE_SEQ")+"'";
		}
		//System.out.println("获取调阅病历sql:::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（治疗）的数据
	 * @param caseNo
	 */
	public TParm getOthData(TParm param){
		String sql="SELECT A.TR_DESC,A.FREQUENCY,Case A.TR_DAYS when null then  '' else  a.tr_days||''  end tr_days ,A.QTY,A.DR_NOTE,A.IS_URGENT," +
				" A.START_DATE,A.END_DATE" +
				"  ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_TREATMENT A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+" FROM CRP_TREATMENT A,CRP_VISIT_RECORD B WHERE  A.MR_NO='"+param.getValue("MR_NO")+"' AND A.CASE_NO = B.CASE_NO ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+"   ORDER BY A.START_DATE DESC,A.TR_CODE";
		//System.out.println("获取（治疗）的数据sql："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（输血）的数据
	 * @param param
	 * @return
	 */
	public TParm getBmsData(TParm param){
		String  sql="SELECT A.ORDER_TIME,B.DEPT_DESC,B.VS_DR_NAME,A.BLDPROD_DESC,A.VOLUME,A.UNIT_DESC," +
				//规格
				" A.BLOOD_TYPING,A.RH_TYPING,A.MAIN_CROSS_TEST,A.SUB_CROSS_TEST,A.CROSS_MATCH," +
				" CASE A.STATUS WHEN '1' THEN '开立备血单 ' WHEN '2' THEN '开立配血医嘱' " +
				" WHEN '3' THEN '护士确认' WHEN '4' THEN '血标本接收 ' " +
				" WHEN '5' THEN '交叉配血 ' WHEN '6' THEN '配血结果核对'" +
				" WHEN '7' THEN '开立取血单' WHEN '8' THEN '血品出库'" +
				" WHEN '9' THEN '科室核收' WHEN '10' THEN '执行输血' END STATUS," +
				" CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'"+
				" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,"+
				" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC"+
				" FROM CRP_TRANSFUSION A,CRP_VISIT_RECORD B WHERE ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+"    A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+"  A.MR_NO IN ("+param.getValue("MR_NO")+")  ";
		}
		sql=sql+" AND A.CASE_NO=B.CASE_NO ";
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+"  ORDER BY A.ORDER_TIME DESC";
		//System.out.println("用血sql：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 获取（会诊）的数据
	 * @param param
	 * @return
	 */
	public TParm getConsult(TParm param){
		String sql="SELECT A.ORDER_TIME,A.ORDER_DEPT,A.CONSULT_KIND,A.CONSULT_REASON,A.ILLNESS_STATE,A.CONSULT_DEPT_NAME," +
				" A.ACCEPT_TIME,A.REPLY_DATE,A.CONSULT_REPORT,A.CONSULT_NO,'N' AS WORD " +
				"  ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_CONSULT  A WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"' ";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql = sql+" ,CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'";
			sql = sql+" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,";
			sql = sql+" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC";
			sql=sql+" FROM CRP_CONSULT  A,CRP_VISIT_RECORD B WHERE  A.MR_NO IN ("+param.getValue("MR_NO")+") AND A.CASE_NO = B.CASE_NO  ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		sql=sql+"   ORDER BY A.ORDER_TIME DESC";
		//System.out.println("会诊sql：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	/**
	 * 病历文书
	 */
	public TParm getFileData(TParm param){
		String sql="SELECT A.CASE_NO,A.CHART_NAME, A.CHART_TYPE,A.EDIT_USER,A.EDIT_DATE," +
				" A.CONFIRM_TIME," +
				" CASE A.STATUS WHEN '-1' THEN '未提交' WHEN '1' THEN '已提交'" +
				" WHEN '-2' THEN '审核回退' WHEN '2' THEN '已审核' WHEN '-3' THEN '归档退回' WHEN '3' THEN '已归档'  END STATUS ," +
				" 'N' WORD,A.OPE_BOOK_NO,A.FILE_SEQ,A.FILE_PATH,A.FILE_NAME ";
		if(!"".equals(param.getValue("CASE_NO")) && param.getValue("CASE_NO")!=null){
			sql=sql+" FROM CRP_FILE_INDEX A  WHERE A.CASE_NO='"+param.getValue("CASE_NO")+"'";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql = sql+" ,CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检' ";
			sql = sql+" WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC, ";
			sql = sql+" B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC ";
			sql=sql+" FROM CRP_FILE_INDEX A,CRP_VISIT_RECORD B WHERE  A.MR_NO IN ("+param.getValue("MR_NO")+") AND A.CASE_NO = B.CASE_NO  ";
		}
		if (StringUtils.isNotEmpty(param.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(param.getValue("E_DATE"))) {
			sql=sql+" AND B.ADM_DATE BETWEEN TO_DATE('"+param.getValue("S_DATE")+" 00:00:00','YYYY/MM/DD HH24:MI:SS')" +
					" AND TO_DATE('"+param.getValue("E_DATE")+" 23:59:59','YYYY/MM/DD HH24:MI:SS')";
		}
		if(!"".equals(param.getValue("MR_NO")) && param.getValue("MR_NO")!=null){
			sql=sql+"   ORDER BY B.ADM_DATE DESC,A.CLASS_CODE,A.SUBCLASS_CODE";
		}else{
			sql=sql+"   ORDER BY A.EDIT_DATE DESC,A.CLASS_CODE,A.SUBCLASS_CODE";
		}
		
		//System.out.println("病历文书sql：："+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	/**
	 * 获取生理参数的数据
	 * @return 
	 */
	public TParm getPhiscalParam(TParm parm){//PHYSI_CH_DESC;PHYSI_VALUE;TEST_UNIT;REMARKS;HIGH_LOW
		TParm result  = new TParm();
		String sql = "SELECT B.PHYSI_CH_DESC,A.PHYSI_VALUE,A.TEST_UNIT,A.REMARKS,B.NORMAL_LOW || CASE WHEN B.NORMAL_HIGN IS NOT NULL" +
				" AND B.NORMAL_LOW IS NOT NULL THEN '-' ELSE '' END || B.NORMAL_HIGN AS HIGH_LOW FROM " +
				" MED_PHYSI_DTL A,MED_PHYSI_PARAM B WHERE A.CAT1_TYPE='RIS' AND A.APPLICATION_NO = '"+parm.getValue("APPLICATION_NO")+"'" +
				" AND B.PHYSI_CODE=A.PHYSI_CODE";
		//System.out.println("生理参数sql：："+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 调阅影像（检查）
	 * @param mrNS
	 */
	public void getRisReport(String mrNo){
		SystemTool.getInstance().OpenRisWeb(mrNo);
	}
	
	/**
	 * 调阅分布式存储病历
	 * 
	 * @param filePath 文件路径
	 * @param fileName 文件名称
	 * @return parm
	 */
	public TParm readFile(String filePath, String fileName) {
		TParm parm = new TParm();
		String serverRoot = TConfig.getSystemValue("FileServer.Main.Root");
		TSocket socket = TIOM_FileServer.getSocket();

		if (StringUtils.isNotEmpty(fileName) && fileName.indexOf("_") != -1) {
			String sYear = fileName.substring(0, 2);
			String root = TConfig.getSystemValue("FileServer." + sYear
					+ ".Root");
			String ip = TConfig.getSystemValue("FileServer." + sYear + ".IP");
			if (StringUtils.isNotEmpty(root)) {
				serverRoot = root;
			}
			if (StringUtils.isNotEmpty(ip)) {
				socket = new TSocket(ip, 8103);
			}
		}
		String serverPath = serverRoot + "\\"
				+ TConfig.getSystemValue("EmrData") + "\\"
				+ filePath.replaceFirst("JHW", "PDF");
		parm.setData("FILE_DATA", TIOM_FileServer.readFile(socket, serverPath
				+ "\\" + fileName));
		parm.setData("SERVER_PATH", serverPath);
		return parm;
	}
}
