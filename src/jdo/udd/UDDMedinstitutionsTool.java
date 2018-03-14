package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 天津市医疗机构抗菌药物临床应用信息表</p>
 *
 * <p>Description:天津市医疗机构抗菌药物临床应用信息表</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.12.11
 * @version 1.0
 */
public class UDDMedinstitutionsTool extends TJDOTool {
	/**
	 * 构造器
	 */
	private UDDMedinstitutionsTool() {
		// 加载Module文件
		this.setModuleName("UDD\\UDDMedinstitutionsModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static UDDMedinstitutionsTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static UDDMedinstitutionsTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new UDDMedinstitutionsTool();
		}
		return instanceObject;
	}
	
	/**
	 * 住院
	 * @param parm
	 * @return
	 */
	public TParm getSelectindate(TParm parm) {
				TParm result = new TParm();
		 String sql="SELECT TO_CHAR(A.IN_DATE,'YYYYMM') AS SDATE, TO_CHAR(A.DS_DATE,'YYYYMM') AS DS_DATE, " +
		 		   "A.CASE_NO,A.DEPT_CODE,B.ANTIBIOTIC_CODE,B.CAT1_TYPE, "+
	               "B.ANTIBIOTIC_WAY,B.DOSAGE_QTY,C.MEDI_QTY,D.DDD,F.PHA_PREVENCODE "+
			       "FROM ADM_INP A,ODI_ORDER B,PHA_TRANSUNIT C, "+
			       "SYS_FEE D,OPE_OPBOOK E,SYS_OPERATIONICD F,ADM_INP G "+
			       "WHERE A.CASE_NO=B.CASE_NO "+
			       "AND B.ORDER_CODE = C.ORDER_CODE "+
			       "AND B.ORDER_CODE = D.ORDER_CODE "+
			       "AND A.CASE_NO=E.CASE_NO "+
			       "AND A.CASE_NO=G.CASE_NO "+
			       "AND E.OP_CODE1 = F.OPERATION_ICD  " +
			       "AND A.DS_DATE IS NULL "+
			       "AND A.IN_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
				   "AND TO_DATE ('"+parm.getValue("E_DATE")+"','YYYYMM') "; 
		 
		 System.out.println("==========住院=="+sql);
		 result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 出院人数
	 * @param parm
	 * @return
	 */
  public TParm getSelectoutdate(TParm parm) {
		TParm result = new TParm();
		String sql="SELECT TO_CHAR (DS_DATE, 'YYYYMM') AS SDATE,COUNT (DISTINCT  CASE_NO) AS OUT_NUM " +
                   "FROM ADM_INP "+
                   "WHERE  DS_DATE IS NOT NULL "+
                   "AND DS_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
				   "AND TO_DATE ('"+parm.getValue("E_DATE")+"','YYYYMM') "+
                   "GROUP BY TO_CHAR ( DS_DATE, 'YYYYMM') "+
                   "ORDER BY TO_CHAR ( DS_DATE, 'YYYYMM') ";
		
 
		 System.out.println("==========出院人数=="+sql);
		 result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
		}
  /**
   * 出院人数使用抗菌药物人数
   * @param parm
   * @return
   */
  public TParm getSelectoutAdate(TParm parm) {
	  TParm result = new TParm();
	  String sql="SELECT TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE,COUNT (DISTINCT  A.CASE_NO) AS OUT_ANUM " +
		        "FROM ADM_INP A,ODI_ORDER B "+
		        "WHERE A.CASE_NO=B.CASE_NO "+
		        "AND B.ANTIBIOTIC_CODE IS NOT NULL "+
		        "AND A.DS_DATE IS NOT NULL "+ 
		        "AND A.DS_DATE  BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
			    "AND TO_DATE ('"+parm.getValue("E_DATE")+"','YYYYMM') "+
			    "GROUP BY TO_CHAR ( A.DS_DATE, 'YYYYMM') "+
                "ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') ";;
	  
	  
	  System.out.println("==========出院使用抗菌人数=="+sql);
	  result = new TParm(TJDODBTool.getInstance().select(sql));
	  if (result.getErrCode() < 0) {
		  err("ERR:M " + result.getErrCode() + result.getErrText()
				  + result.getErrName());
		  return result;
	  }
	  return result;
  }
  
  
	  /**
		 * 门诊就诊人数
		 * @param parm
		 * @return
		 */
	public TParm getSelectOdate(TParm parm) {
			TParm result = new TParm();
	String sql="SELECT TO_CHAR (A.ADM_DATE, 'YYYYMM') AS SDATE, COUNT (DISTINCT  A.CASE_NO) AS O_NUM "+
               "FROM REG_PATADM A "+
               "WHERE "+
               "A.ADM_TYPE = 'O' "+
               "AND A.ADM_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
               "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
               "GROUP BY TO_CHAR (A.ADM_DATE, 'YYYYMM') "+
               "ORDER BY TO_CHAR (A.ADM_DATE, 'YYYYMM') "; 
			 System.out.println("==========门诊就诊人数=="+sql);
			 result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			return result;
			}
	/**
	 * 门诊使用抗菌药人数
	 * @param parm
	 * @return
	 */
	public TParm getSelectOAdate(TParm parm) {
		TParm result = new TParm();
		String sql="SELECT TO_CHAR (A.ADM_DATE, 'YYYYMM') AS SDATE, COUNT (DISTINCT  B.CASE_NO) AS O_ANUM "+
				   "FROM REG_PATADM A, OPD_ORDER B, PHA_BASE C "+
				   "WHERE A.CASE_NO = B.CASE_NO "+
				   "AND B.ORDER_CODE = C.ORDER_CODE " +
				   "AND A.ADM_TYPE = 'O' " +
				   "AND B.CAT1_TYPE = 'PHA' " +
				   "AND  C.ANTIBIOTIC_CODE IS NOT NULL " +
				   "AND A.ADM_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') " +
		           "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') " +
		           "GROUP BY TO_CHAR (A.ADM_DATE, 'YYYYMM') " +
		           "ORDER BY TO_CHAR (A.ADM_DATE, 'YYYYMM')"; 
		System.out.println("==========门诊使用抗菌药人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 急诊就诊人数
	 * @param parm
	 * @return
	 */
	public TParm getSelectEdate(TParm parm) {
		TParm result = new TParm();
		String sql="SELECT TO_CHAR(A.REG_DATE,'YYYYMM') AS SDATE, COUNT (DISTINCT  A.CASE_NO) AS E_NUM "+
					"FROM REG_PATADM A "+
					"WHERE "+
					"A.ADM_TYPE = 'E' "+
					"AND A.REG_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
					"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
					"GROUP BY TO_CHAR (A.REG_DATE, 'YYYYMM') "+
					"ORDER BY TO_CHAR (A.REG_DATE, 'YYYYMM') "; 
		System.out.println("==========急诊就诊人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 急诊使用抗菌药人数
	 * @param parm
	 * @return
	 */
	public TParm getSelectEAdate(TParm parm) {
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.REG_DATE,'YYYYMM') AS SDATE , count (DISTINCT  B.case_no) as E_ANUM "+
					"FROM reg_patadm a, opd_order b, pha_base c "+
					"WHERE a.case_no = b.case_no "+
					"AND b.order_code = c.order_code " +
					"AND a.adm_type = 'E' " +
					"AND b.cat1_type = 'PHA' " +
					"AND  c.antibiotic_code IS NOT NULL " +
					"AND a.adm_date BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') " +
					"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') " +
					"group by TO_CHAR (A.REG_DATE, 'YYYYMM') " +
					"order by TO_CHAR (A.REG_DATE, 'YYYYMM')"; 
		System.out.println("==========急诊使用抗菌药人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 出院患者住院人天数
	 * @param parm
	 * @return
	 */
	public TParm getselectoutDays(TParm parm){
		TParm result = new TParm();
		String sql="SELECT SUM( CEIL (DS_DATE - IN_DATE)) AS OUT_DAYS, TO_CHAR (DS_DATE, 'YYYYMM') AS SDATE "+
				   "FROM ADM_INP "+
				   "WHERE DS_DATE IS NOT NULL "+
				   "AND DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
				   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
				   "GROUP BY TO_CHAR (DS_DATE, 'YYYYMM') "+
				   "ORDER BY TO_CHAR (DS_DATE, 'YYYYMM')";
		System.out.println("==========出院者住院天数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 出院患者特殊使用级抗菌药物使用人数
	 * @param parm
	 * @return
	 */
	public TParm getselectoutSpeanum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE, "+
		           "COUNT (DISTINCT A.CASE_NO) AS OUT_SPECIALANUM "+
                   "FROM ADM_INP A,ODI_ORDER B  "+
                   "WHERE A.CASE_NO=B.CASE_NO  "+
                   "AND B.CAT1_TYPE = 'PHA'  "+
                   "AND A.DS_DATE IS NOT NULL  "+
                   "AND B.ANTIBIOTIC_CODE='03'  "+
                   "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
				   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
                   "GROUP BY TO_CHAR(A.DS_DATE,'YYYYMM') "+
                   "ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') " ; 
                   
                   
       
		System.out.println("==========出院患者特殊使用级抗菌药物使用人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 *I类切口手术例数

	 * @param parm
	 * @return
	 */
	public TParm getselectInum(TParm parm){
		TParm result = new TParm();
		
		String sql=  "SELECT TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE," +
					 "COUNT(DISTINCT A.CASE_NO) AS I_NUM "+
	                 "FROM ADM_INP A, " +
	                 "OPE_OPBOOK B, " +
	                 "SYS_OPERATIONICD C "+
	                 "WHERE "+ 
	                 "A.CASE_NO=B.CASE_NO AND "+
	                 "B.OP_CODE1 = C.OPERATION_ICD "+ 
	                 "AND C.PHA_PREVENCODE='001' "+
	                 "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
	         		 "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
	         		 "GROUP BY TO_CHAR(A.DS_DATE,'YYYYMM') "+
	         		 "ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') " ;
		 
		
		
		
		System.out.println("==========I类切口手术例数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	 
	/**
	 *I类切口手术例数

	 * @param parm
	 * @return
	 */
	public TParm getselectIInum(TParm parm){
		TParm result = new TParm();
		
		String sql=  "SELECT TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE," +
		"COUNT(DISTINCT A.CASE_NO) AS II_OPENUM "+
		"FROM ADM_INP A, " +
		"OPE_OPBOOK B, " +
		"SYS_OPERATIONICD C "+
		"WHERE "+ 
		"A.CASE_NO=B.CASE_NO AND "+
		"B.OP_CODE1 = C.OPERATION_ICD "+ 
		"AND C.PHA_PREVENCODE='002' "+
		"AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
		"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
		"GROUP BY TO_CHAR(A.DS_DATE,'YYYYMM') "+
		"ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') " ;
		
		
		
		
		System.out.println("==========II类切口手术例数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 出院患者治疗性使用限制级抗菌药物人数
	 * @param parm
	 * @returngetselectoutLimitnum
	 */
	public TParm getselectoutLimitnum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE, "+
		           "COUNT (DISTINCT A.CASE_NO) AS OUT_LIMITANUM "+
                   "FROM ADM_INP A,ODI_ORDER B  "+
                   "WHERE A.CASE_NO=B.CASE_NO  "+
                   "AND B.CAT1_TYPE = 'PHA'  "+
                   "AND A.DS_DATE IS NOT NULL  "+
                   "AND B.ANTIBIOTIC_CODE='02'  "+
                   "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
				   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
                   "GROUP BY TO_CHAR(A.DS_DATE,'YYYYMM') "+
                   "ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') " ; 
                   
                   
       
		System.out.println("==========出院患者治疗性使用限制级抗菌药物人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 *出院患者治疗性使用特殊级抗菌药物人数

	 * @param parm
	 * @return
	 */
	public TParm getselectoutSpeacialnum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE, "+
		"COUNT (DISTINCT A.CASE_NO) AS OUT_SPEANUM "+
		"FROM ADM_INP A,ODI_ORDER B  "+
		"WHERE A.CASE_NO=B.CASE_NO  "+
		"AND B.CAT1_TYPE = 'PHA'  "+
		"AND A.DS_DATE IS NOT NULL  "+
		"AND B.ANTIBIOTIC_CODE='03'  "+
		"AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
		"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
		"GROUP BY TO_CHAR(A.DS_DATE,'YYYYMM') "+
		"ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') " ; 
		
		
		
		System.out.println("==========出院患者治疗性使用特殊级抗菌药物人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 *出院患者抗菌药物治疗使用前微生物送检验人数
	 * @param parm
	 * @return
	 */
	public TParm getselectoutAmicnum(TParm parm){
		TParm result = new TParm();
		
		
		String sql="SELECT  TO_CHAR(A.DS_DATE,'YYYYMM') AS SDATE, "+
					"COUNT (DISTINCT A.CASE_NO) AS OUT_AMICNUM "+
					" FROM ADM_INP A,ODI_ORDER B "+
					"WHERE A.CASE_NO=B.CASE_NO  "+
					"AND B.CAT1_TYPE = 'PHA'  "+
					"AND A.DS_DATE IS NOT NULL  "+
					"AND B.ANTIBIOTIC_WAY='2'  "+
					"AND B.ANTIBIOTIC_CODE IS NOT NULL "+
					"AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
					"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
					"GROUP BY TO_CHAR(A.DS_DATE,'YYYYMM') "+
					"ORDER BY TO_CHAR(A.DS_DATE,'YYYYMM') " ; 
		
		
		
		System.out.println("==========出院患者抗菌药物治疗使用前微生物送检验人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 *住院使用抗菌药物数
	 * @param parm
	 * @return
	 */
	public TParm getselectinAnum(TParm parm){
		TParm result = new TParm();
		
		String sql="SELECT   TO_CHAR (A.IN_DATE, 'YYYYMM') AS SDATE, "+
                   "COUNT (DISTINCT A.CASE_NO) AS IN_ANUM "+
                   "FROM ADM_INP A, ODI_ORDER B "+
                   "WHERE A.CASE_NO = B.CASE_NO "+
                   "AND B.CAT1_TYPE = 'PHA' "+
                   "AND A.CANCEL_FLG <> 'Y' "+
                   "AND B.ANTIBIOTIC_CODE IS NOT NULL "+
                   "AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
                   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
                   "GROUP BY TO_CHAR (A.IN_DATE, 'YYYYMM') "+
                   "ORDER BY TO_CHAR (A.IN_DATE, 'YYYYMM') ";

		
		
		
		
		System.out.println("==========住院使用抗菌药物数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 *住院使用抗菌药物数使用前微生物送检验人数
	 * @param parm
	 * @return
	 */
	public TParm getselectinAmicnum(TParm parm){
		TParm result = new TParm();
		
		String sql="SELECT   TO_CHAR (A.IN_DATE, 'YYYYMM') AS SDATE, "+
					"COUNT (DISTINCT A.CASE_NO) AS IN_AMICNUM "+
					"FROM ADM_INP A, ODI_ORDER B "+
					"WHERE A.CASE_NO = B.CASE_NO "+
					"AND B.CAT1_TYPE = 'PHA' "+
					"AND B.ANTIBIOTIC_WAY='2'  "+
					"AND A.CANCEL_FLG <> 'Y' "+
					"AND B.ANTIBIOTIC_CODE IS NOT NULL "+
					"AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
					"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
					"GROUP BY TO_CHAR (A.IN_DATE, 'YYYYMM') "+
					"ORDER BY TO_CHAR (A.IN_DATE, 'YYYYMM') ";
		
		
		
		
		
		System.out.println("==========住院使用抗菌药物数使用前微生物送检验人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 *住院人数
	 * @param parm
	 * @return
	 */
	public TParm getselectinNum(TParm parm){
		TParm result = new TParm();
		
		String sql="SELECT TO_CHAR (A.IN_DATE, 'YYYYMM') AS SDATE, "+
                   "COUNT (DISTINCT A.CASE_NO) AS IN_NUM "+
                   "FROM ADM_INP A, ODI_ORDER B "+
                   "WHERE A.CASE_NO = B.CASE_NO "+
                   "AND A.CANCEL_FLG <> 'Y' "+
                   "AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"', 'YYYYMM') "+
                   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
                   "GROUP BY TO_CHAR (A.IN_DATE, 'YYYYMM') "+
                   "ORDER BY TO_CHAR (A.IN_DATE, 'YYYYMM') ";

		
		
		
		
		System.out.println("==========住院人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 住院患者治疗性使用限制级抗菌药物人数
	 * @param parm
	 * @returngetselectoutLimitnum
	 */
	public TParm getselectInLimitnum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.IN_DATE,'YYYYMM') AS SDATE, "+
		           "COUNT (DISTINCT A.CASE_NO) AS IN_LIMITANUM "+
                   "FROM ADM_INP A,ODI_ORDER B  "+
                   "WHERE A.CASE_NO=B.CASE_NO  "+
                   "AND B.CAT1_TYPE = 'PHA'  "+
                   "AND B.ANTIBIOTIC_CODE='02'  "+
                   "AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
				   "AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
                   "GROUP BY TO_CHAR(A.IN_DATE,'YYYYMM') "+
                   "ORDER BY TO_CHAR(A.IN_DATE,'YYYYMM') " ; 
                   
                   
       
		System.out.println("==========住院患者治疗性使用限制级抗菌药物人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 *住院患者治疗性使用特殊级抗菌药物人数

	 * @param parm
	 * @return
	 */
	public TParm getselectInSpeacialnum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.IN_DATE,'YYYYMM') AS SDATE, "+
		"COUNT (DISTINCT A.CASE_NO) AS IN_SPEANUM "+
		"FROM ADM_INP A,ODI_ORDER B  "+
		"WHERE A.CASE_NO=B.CASE_NO  "+
		"AND B.CAT1_TYPE = 'PHA'  "+
		"AND B.ANTIBIOTIC_CODE='03'  "+
		"AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
		"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
		"GROUP BY TO_CHAR(A.IN_DATE,'YYYYMM') "+
		"ORDER BY TO_CHAR(A.IN_DATE,'YYYYMM') " ; 
		
		
		
		System.out.println("==========住院患者治疗性使用特殊级抗菌药物人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 住院患者治疗性使用限制级抗菌药物使用前微生物送检验人数
	 * @param parm

	 * @returngetselectoutLimitnum
	 */
	public TParm getselectInAmiLimitnum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.IN_DATE,'YYYYMM') AS SDATE, "+
		"COUNT (DISTINCT A.CASE_NO) AS IN_AMILIMITANUM "+
		"FROM ADM_INP A,ODI_ORDER B  "+
		"WHERE A.CASE_NO=B.CASE_NO  "+
		"AND B.CAT1_TYPE = 'PHA'  "+
		"AND B.ANTIBIOTIC_CODE='02'  "+
		"AND B.ANTIBIOTIC_WAY='2'  "+
		"AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
		"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
		"GROUP BY TO_CHAR(A.IN_DATE,'YYYYMM') "+
		"ORDER BY TO_CHAR(A.IN_DATE,'YYYYMM') " ; 
		
		
		
		System.out.println("==========住院患者治疗性使用限制级抗菌药物使用前微生物送检验人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 *住院患者治疗性使用特殊级抗菌药物使用前微生物送检验人数

	 * @param parm
	 * @return
	 */
	public TParm getselectInSpeacialAminum(TParm parm){
		TParm result = new TParm();
		String sql="SELECT  TO_CHAR(A.IN_DATE,'YYYYMM') AS SDATE, "+
		"COUNT (DISTINCT A.CASE_NO) AS IN_AMISPEANUM "+
		"FROM ADM_INP A,ODI_ORDER B  "+
		"WHERE A.CASE_NO=B.CASE_NO  "+
		"AND B.CAT1_TYPE = 'PHA'  "+
		"AND B.ANTIBIOTIC_CODE='03'  "+
		"AND B.ANTIBIOTIC_WAY='2'  "+
		"AND A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"','YYYYMM') "+
		"AND TO_DATE ('"+parm.getValue("E_DATE")+"', 'YYYYMM') "+
		"GROUP BY TO_CHAR(A.IN_DATE,'YYYYMM') "+
		"ORDER BY TO_CHAR(A.IN_DATE,'YYYYMM') " ; 
		
		
		
		System.out.println("==========住院患者治疗性使用特殊级抗菌药物使用前微生物送检验人数=="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
}
