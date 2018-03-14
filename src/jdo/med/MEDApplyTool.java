package jdo.med;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MEDApplyTool
    extends TJDODBTool {
    /**
     * 实例
     */
    public static MEDApplyTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MEDApplyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MEDApplyTool();
        return instanceObject;
    }

    /**
     * 保存住院医嘱
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveMedApply(TParm parm, TConnection con) {
        String sqlStr[] = (String[]) parm.getData("SQL");
        TParm result = new TParm(this.update(sqlStr, con));
        return result;
    }
    /**
     * 查询医技状态
     * @param parm TParm
     * @return TParm
     */
    public TParm queryMedApply(String caseNo){
        TParm result = new TParm(this.select("SELECT * FROM MED_APPLY WHERE CASE_NO='"+caseNo+"'"));
        return result;
    }
    /**
     * 更新状态
     * @param caseNo String
     * @param applyNo String
     * @return TParm
     */
    public TParm updateStauts(String caseNo,String applyNo,String orderNo,String seqNo,String billFlg,TConnection con){
//        System.out.println("UPDATE MED_APPLY SET BILL_FLG='"+billFlg+"' WHERE CASE_NO='"+caseNo+"' AND APPLICATION_NO='"+applyNo+"' AND ORDER_NO='"+orderNo+"' AND SEQ_NO='"+seqNo+"'");
        TParm result = new TParm(this.update("UPDATE MED_APPLY SET BILL_FLG='"+billFlg+"' WHERE CASE_NO='"+caseNo+"' AND APPLICATION_NO='"+applyNo+"' AND ORDER_NO='"+orderNo+"' AND SEQ_NO='"+seqNo+"'",con));
        return result;
    }
    /**
     * 插入MED_APPLY表数据补充计价
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm insertMedApply(TParm parm,TConnection con){
        TParm result = new TParm();
        if(parm==null){
            parm.setErrCode(-1);
            parm.setErrText("无数据！");
            return parm;
        }
        int count = parm.getCount("CASE_NO");
//        System.out.println("数量=="+count);
        for(int i=0;i<count;i++){
            if(("Y".equals(parm.getValue("SETMAIN_FLG",i))&&"LIS".equals(parm.getValue("CAT1_TYPE",i)))||("Y".equals(parm.getValue("SETMAIN_FLG",i))&&"RIS".equals(parm.getValue("CAT1_TYPE",i)))){
                String sql = createSql(parm.getRow(i));
//                System.out.println("插入MED"+sql);
                result = new TParm(this.update(sql,con));
                if(result.getErrCode()!=0)
                    return result;
            }
        }
        return result;
    }
    /**
     * 查询检体名称
     * @param iptCode String
     * @return String
     */
    public String queryOptItem(String iptCode){
        TParm parm = new TParm(this.select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_OPTITEM' AND ID = '"+iptCode+"'"));
        return parm.getValue("CHN_DESC",0);
    }
    /**
    * 得到集合医嘱细项价格
    * @param parm TParm
    * @param type String
    * @param exDeptCode String
    * @return TParm
    */
   public double getOrderSetList(String orderCode){
       double ownPrice=0.0;
       TParm result = new TParm();
       result = new TParm(this.select("SELECT OWN_PRICE "+
           " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='"+orderCode+"' AND A.ORDER_CODE=B.ORDER_CODE"));
       int rowCount = result.getCount("OWN_PRICE");
//       System.out.println("个数:"+rowCount);
//       System.out.println("返回值:"+result);
       for(int i=0;i<rowCount;i++){
           ownPrice+=result.getDouble("OWN_PRICE",i);
       }
       return ownPrice;
   }
   /**
     * 根據給入orderCat1Code得到deal_system
     * @param orderCat1Code
     * @return
     */
    public  String getDealSystem(String orderCat1Code){
//        System.out.println("SQL"+"SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"+orderCat1Code+"'");
        TParm result=new TParm(this.select("SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"+orderCat1Code+"'"));
        return result.getValue("DEAL_SYSTEM",0);
    }
    /**
    * 得到折扣比率
    * @param ctzCode String
    * @return double
    */
   public double getOwnRate(String ctzCode,String orderCode){
       double ownRate = 1;
       TParm action = new TParm(this.select("SELECT CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"'"));
       TParm actionParm = new TParm(this.select("SELECT DISCOUNT_RATE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE='"+ctzCode+"' AND CHARGE_HOSP_CODE='"+action.getValue("CHARGE_HOSP_CODE",0)+"'"));
       if(actionParm.getDouble("DISCOUNT_RATE",0)==0)
           return ownRate;
       else
           ownRate = actionParm.getDouble("DISCOUNT_RATE",0);
       return ownRate;
   }
   /**
    * 查询挂号科室
    * @param caseNo String
    * @return TParm
    */
   public TParm getRegPatAdmDate(String caseNo){
       TParm result = new TParm(this.select("SELECT * FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'"));
       return result.getRow(0);
   }
    /**
     * 生成SQL
     * @param parm TParm
     * @return String[]
     */
    public String createSql(TParm parm){
        //查询检体名称
        String optItemDesc = queryOptItem(parm.getValue("OPTITEM_CODE"));
        //得到诊断类别
        TParm icdParm = new TParm();
        if("O".equals(parm.getValue("ADM_TYPE"))||"E".equals(parm.getValue("ADM_TYPE"))){
            icdParm = new TParm(this.select("SELECT ICD_TYPE,ICD_CODE FROM OPD_DIAGREC WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND MAIN_DIAG_FLG = 'Y'"));
        }
        String mainDiag = "";
        String icdType = "";
        if(icdParm.getCount()>0){
            mainDiag = icdParm.getValue("ICD_CODE",0);
            icdType = icdParm.getValue("ICD_TYPE",0);
        }
        //得到自费总价格
        double ownAmt = getOrderSetList(parm.getValue("ORDER_CODE"));
        //开立时间
        String startDttm = StringTool.getString(parm.getTimestamp("ORDER_DATE"),"yyyyMMddHHmmss");
        //接口厂商名称
        String dealSystem=getDealSystem(parm.getValue("ORDER_CAT1_CODE"));
//        System.out.println("接口厂商名称"+dealSystem);
        TParm pat = getRegPatAdmDate(parm.getValue("CASE_NO"));
        String regCr = pat.getValue("CLINICAREA_CODE");
        String regCno = pat.getValue("CLINICROOM_NO");
        //应付金额
        double arAmt = ownAmt*getOwnRate(parm.getValue("CTZ1_CODE"),parm.getValue("ORDER_CODE"));
        TParm patParm = new TParm(this.select("SELECT * FROM SYS_PATINFO WHERE MR_NO='"+parm.getValue("MR_NO")+"'"));
        String patName = "";
        String patName1 = "";
        String birthDate = "";
        String sexCode = "";
        String postCode = "";
        String address = "";
        String companyCode = "";
        String tel = "";
        String idNo = "";
//        System.out.println("patPamr==基本信息"+patParm);
        patName = patParm.getValue("PAT_NAME", 0);
        patName1 = patParm.getValue("PAT_NAME1", 0);
        birthDate = StringTool.getString(patParm.getTimestamp("BIRTH_DATE", 0),"yyyyMMddHHmmss");
        sexCode = patParm.getValue("SEX_CODE", 0);
        postCode = patParm.getValue("POST_CODE", 0);
        address = patParm.getValue("ADDRESS", 0);
        companyCode = patParm.getValue("COMPANY_DESC", 0);
        tel = patParm.getValue("TEL_HOME", 0);
        idNo = patParm.getValue("IDNO", 0);
        String sql = "INSERT INTO MED_APPLY"+
                        " (CAT1_TYPE, APPLICATION_NO, CASE_NO, IPD_NO, MR_NO,"+
                        "  ADM_TYPE, PAT_NAME, PAT_NAME1, BIRTH_DATE, SEX_CODE,"+
                        "  POST_CODE, ADDRESS,COMPANY,TEL,IDNO,"+
                        "  DEPT_CODE, REGION_CODE, CLINICROOM_NO, STATION_CODE, BED_NO,"+
                        " ORDER_NO,SEQ_NO,ORDER_CODE,ORDER_DESC, ORDER_DR_CODE,"+
                        " ORDER_DATE, ORDER_DEPT_CODE, START_DTTM, EXEC_DEPT_CODE,EXEC_DR_CODE,"+
                        " OPTITEM_CODE, OPTITEM_CHN_DESC, ORDER_CAT1_CODE, DEAL_SYSTEM, RPTTYPE_CODE,"+
                        " DEV_CODE, REMARK, ICD_TYPE, ICD_CODE, STATUS,"+
                        " XML_DATE, NEW_READ_FLG, DC_DR_CODE, DC_ORDER_DATE, DC_DEPT_CODE,"+
                        " DC_READ_FLG,REJECTRSN_CODE, RESERVED_DATE, REGISTER_DATE, INSPECT_DATE,"+
                        " INSPECT_TOTTIME,WAIT_TOTTIME, REPORT_DR, REPORT_DATE, EXAMINE_DR,"+
                        " EXAMINE_DATE,DIAGNOSIS_QUALITY,TECHNOLOGY_QUALITY, SERVICE_QUALITY, SEND_FLG,"+
                        " BILL_FLG,OWN_AMT, AR_AMT, OPT_USER, OPT_DATE,"+
                        " OPT_TERM,ORDER_ENG_DESC,URGENT_FLG,CLINICAREA_CODE)"+
                        " VALUES"+
                        "('"+parm.getValue("CAT1_TYPE")+"', '"+parm.getValue("MED_APPLY_NO")+"', '"+parm.getValue("CASE_NO")+"', '"+parm.getValue("IPD_NO")+"', '"+parm.getValue("MR_NO")+"',"+
                        " '"+parm.getValue("ADM_TYPE")+"', '"+patName+"', '"+patName1+"', TO_DATE('"+birthDate+"','YYYYMMDDHH24MISS'), '"+sexCode+"',"+
                        " '"+postCode+"', '"+address+"', '"+companyCode+"', '"+tel+"', '"+idNo+"',"+
                        " '"+parm.getValue("DEPT_CODE")+"', '"+parm.getValue("REGION_CODE")+"', '"+regCno+"', '"+parm.getValue("STATION_CODE")+"', '"+parm.getValue("BED_NO")+"',"+
                        " '"+parm.getValue("RX_NO")+"', '"+parm.getValue("SEQ_NO")+"', '"+parm.getValue("ORDER_CODE")+"', '"+parm.getValue("ORDER_DESC")+"', '"+parm.getValue("DR_CODE")+"',"+
                        " SYSDATE, '"+parm.getValue("DEPT_CODE")+"',TO_DATE('"+startDttm+"','YYYYMMDDHH24MISS'), '"+parm.getValue("EXEC_DEPT_CODE")+"', '"+parm.getValue("EXEC_DR_CODE")+"',"+
                        " '"+parm.getValue("OPTITEM_CODE")+"', '"+optItemDesc+"', '"+parm.getValue("ORDER_CAT1_CODE")+"', '"+dealSystem+"', '"+parm.getValue("RPTTYPE_CODE")+"',"+
                        " '"+parm.getValue("DEV_CODE")+"', '"+parm.getValue("REMARK")+"', '"+icdType+"', '"+mainDiag+"', '0',"+
                        " '', 'N', '', '', '',"+
                        " 'N', '', '', '', '',"+
                        " '', '', '', '', '',"+
                        " '', '', '', '', '0',"+
                        " '"+parm.getValue("BILL_FLG")+"', '"+StringTool.round(ownAmt,2)+"', '"+StringTool.round(arAmt,2)+"', '"+parm.getValue("OPT_USER")+"', SYSDATE,"+
                        " '"+parm.getValue("OPT_TERM")+"','"+parm.getValue("TRADE_ENG_DESC")+"','"+parm.getValue("URGENT_FLG")+"','"+regCr+"')";
                    return sql;

    }
//    /**
//     * 健康检查使用：删除检验数据
//     * @param parm
//     * @param conn
//     * @return
//     * ========pangben 2013-3-10 根据就诊号码删除
//     */
//    public TParm deleteMedApply(TParm parm ,TConnection conn){
//    	String sql="DELETE FROM MED_APPLY WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
//    	TParm result = new TParm(this.update(sql,conn));
//    	return result;
//    }
//    
//    /**
//     * 健康检查使用：使检验数据失效
//     * @param parm
//     * @param conn
//     * @return
//     * ========wanglong 2013-3-14 根据就诊号码使失效
//     */
//    public TParm disableMedApply(TParm parm ,TConnection conn){
//        String sql="UPDATE MED_APPLY SET STATUS = 9,SEND_FLG = '1' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
//        TParm result = new TParm(this.update(sql,conn));
//        return result;
//    }
    
    /**
     * 查询检验检查医嘱信息
     * @param parm TParm
     * @return TParm
     * @author wangb
     */
    public TParm queryMedApplyInfo(TParm parm){
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("SELECT * FROM MED_APPLY WHERE 1 = 1 ");
    	
    	if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
    		sbSql.append(" AND CASE_NO = '");
    		sbSql.append(parm.getValue("CASE_NO"));
        	sbSql.append("' ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("APPLICATION_NO"))) {
    		sbSql.append(" AND APPLICATION_NO = '");
    		sbSql.append(parm.getValue("APPLICATION_NO"));
        	sbSql.append("' ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
    		sbSql.append(" AND MR_NO = '");
    		sbSql.append(parm.getValue("MR_NO"));
        	sbSql.append("' ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
    		sbSql.append(" AND CAT1_TYPE = '");
    		sbSql.append(parm.getValue("CAT1_TYPE"));
        	sbSql.append("' ");
    	}
    	
        TParm result = new TParm(this.select(sbSql.toString()));
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }
    
    /**
     * 查询一期临床受试者编号
     * 
     * @param caseNo 就诊号
     */
    public String queryRecruitNo(String caseNo) {
    	String sql = "SELECT RECRUIT_NO FROM ADM_RESV WHERE IN_CASE_NO = '" + caseNo + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getErrCode() < 0) {
    		return "";
    	} else {
    		return result.getValue("RECRUIT_NO", 0);
    	}
    }
    
    /**
     * 查询一期临床方案编号
     * 
     * @param caseNo 就诊号
     */
    public String queryPlanNo(String caseNo) {
    	String sql = "SELECT OPD_CASE_NO FROM ADM_RESV WHERE IN_CASE_NO = '" + caseNo + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getErrCode() < 0) {
    		return "";
    	}
    	
    	String opdCaseNo = result.getValue("OPD_CASE_NO", 0);
    	if (StringUtils.isNotEmpty(opdCaseNo)) {
    		opdCaseNo = opdCaseNo.split(",")[0];
			sql = "SELECT PLAN_NO FROM HRM_CONTRACTD A,HRM_PATADM B WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE AND A.ROLE_TYPE = 'PIC' AND B.CASE_NO = '"
					+ opdCaseNo + "'";
    		result = new TParm(TJDODBTool.getInstance().select(sql));
        	if (result.getErrCode() < 0) {
        		return "";
        	} else {
        		return result.getValue("PLAN_NO", 0);
        	}
    	} else {
    		return "";
    	}
    }
    
    /**
     * 更新MED_APPLY表的接收信息
     * 
     * @param parm
     * @return result 更新结果集
     */
    public TParm updateMedApplyLisReceiveData(TParm parm) {
    	TParm result = new TParm();
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("UPDATE MED_APPLY SET LIS_RE_DATE = SYSDATE,OPT_DATE = SYSDATE,");
    	sbSql.append("LIS_RE_USER = '");
    	sbSql.append(parm.getValue("LIS_RE_USER"));
    	sbSql.append("',OPT_USER = '");
    	sbSql.append(parm.getValue("OPT_USER"));
    	sbSql.append("',OPT_TERM = '");
    	sbSql.append(parm.getValue("OPT_TERM"));
    	sbSql.append("' WHERE 1 = 1 ");
    	
    	if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
    		sbSql.append(" AND CASE_NO = '");
    		sbSql.append(parm.getValue("CASE_NO"));
    		sbSql.append("' ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("APPLICATION_NO"))) {
    		sbSql.append(" AND APPLICATION_NO = '");
    		sbSql.append(parm.getValue("APPLICATION_NO"));
    		sbSql.append("' ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
    		sbSql.append(" AND CAT1_TYPE = '");
    		sbSql.append(parm.getValue("CAT1_TYPE"));
    		sbSql.append("' ");
    	}
    	
    	result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
    	}
    	return result;
    }
    
    /**
     * 查询外送检验基本数据
     * 
     * @param parm
     * @return 就诊信息集合
     */
	public TParm queryExternalLisBasicData(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.CASE_NO,A.APPLICATION_NO,A.APPLICATION_NO AS BAR_CODE,");
		sbSql.append("A.PAT_NAME,C.CHN_DESC AS SEX,A.BIRTH_DATE,A.OPTITEM_CHN_DESC AS SAMPLE_TYPE,");
		sbSql.append("F.USER_NAME AS ORDER_DR_DESC,D.DEPT_CHN_DESC AS DEPT_DESC,");
		sbSql.append("A.TEL AS PAT_TEL,E.BED_NO_DESC,A.BLOOD_DATE,");
		sbSql.append("A.LIS_RE_DATE,MAX(ORDER_DATE) AS ORDER_DATE ");
		sbSql.append(" FROM MED_APPLY A,SYS_FEE B,SYS_DICTIONARY C,SYS_DEPT D,SYS_BED E,SYS_OPERATOR F # ");
		sbSql.append(" WHERE A.ORDER_CODE = B.ORDER_CODE AND A.CAT1_TYPE = 'LIS'  ");
		sbSql.append(" AND B.CAT1_TYPE = 'LIS' AND B.TRANS_OUT_FLG = 'Y' AND A.ADM_TYPE = '");
		sbSql.append(parm.getValue("ADM_TYPE"));
		sbSql.append("' ");
		
		// 健检使用报到时间进行查询
		if ("H".equals(parm.getValue("ADM_TYPE"))) {
			sbSql.append(" AND G.REPORT_DATE > TO_DATE('");
			sbSql.append(parm.getValue("START_DATE"));
			sbSql.append("','YYYY/MM/DD HH24:MI:SS') AND G.REPORT_DATE <= TO_DATE('");
			sbSql.append(parm.getValue("END_DATE"));
			sbSql.append("','YYYY/MM/DD HH24:MI:SS') ");
		} else {
			sbSql.append(" AND A.ORDER_DATE >= TO_DATE('");
			sbSql.append(parm.getValue("START_DATE"));
			sbSql.append("','YYYY/MM/DD HH24:MI:SS') AND A.ORDER_DATE <= TO_DATE('");
			sbSql.append(parm.getValue("END_DATE"));
			sbSql.append("','YYYY/MM/DD HH24:MI:SS') ");
		}
		
		sbSql.append(" AND C.GROUP_ID = 'SYS_SEX' AND A.SEX_CODE = C.ID AND A.STATUS <> '9' ");
		sbSql.append(" AND A.DEPT_CODE = D.DEPT_CODE AND A.BED_NO = E.BED_NO(+) AND A.ORDER_DR_CODE = F.USER_ID ");

		// 外送院所
		if (StringUtils.isNotEmpty(parm.getValue("TRANS_HOSP_CODE"))) {
			sbSql.append(" AND B.TRANS_HOSP_CODE IN ('");
			sbSql.append(parm.getValue("TRANS_HOSP_CODE"));
			sbSql.append("') ");
		}

		// 完成状态
		if ("N".equals(parm.getValue("FINISH_STATUS"))) {
			sbSql.append(" AND A.LIS_RE_DATE IS NULL ");
		} else if ("Y".equals(parm.getValue("FINISH_STATUS"))) {
			sbSql.append(" AND A.LIS_RE_DATE IS NOT NULL ");
		}
		
		if ("H".equals(parm.getValue("ADM_TYPE"))) {
			sbSql.append(" AND A.CASE_NO = G.CASE_NO AND G.COVER_FLG = 'Y' ");
		}

		sbSql.append(" GROUP BY A.CASE_NO,A.APPLICATION_NO,A.PAT_NAME,C.CHN_DESC,");
		sbSql.append("A.BIRTH_DATE,A.OPTITEM_CHN_DESC,F.USER_NAME,");
		sbSql.append("D.DEPT_CHN_DESC,A.TEL,E.BED_NO_DESC,A.BLOOD_DATE,A.LIS_RE_DATE ");
		sbSql.append(" ORDER BY A.APPLICATION_NO ");
		
		String sql = sbSql.toString();
		if ("H".equals(parm.getValue("ADM_TYPE"))) {
			sql = sql.replace("#", ", HRM_PATADM G ");
		} else {
			sql = sql.replace("#", "");
		}
		
		result = new TParm(TJDODBTool.getInstance().select(sql));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}

		return result;
	}
    
	/**
	 * 查询住院患者检验医嘱信息
	 * 
	 * @param parm
	 * @return 检验医嘱信息
	 */
	public TParm queryInHospLisData(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.*,B.NS_EXEC_CODE_REAL,B.NS_EXEC_DATE_REAL ");
		sbSql.append(" FROM MED_APPLY A, ODI_DSPND B ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO AND A.ORDER_NO = B.ORDER_NO ");
		sbSql.append(" AND A.ORDER_CODE = B.ORDER_CODE AND ADM_TYPE = 'I' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
			sbSql.append(" AND A.CAT1_TYPE IN '");
			sbSql.append(parm.getValue("CAT1_TYPE"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("APPLICATION_NO"))) {
			sbSql.append(" AND A.APPLICATION_NO IN ('");
			sbSql.append(parm.getValue("APPLICATION_NO"));
			sbSql.append("') ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND A.CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 查询外送检验合码医嘱信息
	 * 
	 * @param parm
	 * @return 检验医嘱信息
	 */
	public TParm queryLisMergeOrderData(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT APPLICATION_NO, MAX (ORDER_DESC) AS ORDER_DESC,");
		sbSql.append("MAX (ORDER_CODE) AS ORDER_CODE, COUNT(ORDER_CODE) AS SAMPLE_COUNT ");
		sbSql.append(" FROM (SELECT APPLICATION_NO,ORDER_DATE,WM_CONCAT (A.ORDER_DESC) OVER (PARTITION BY APPLICATION_NO ORDER BY A.ORDER_CODE) AS ORDER_DESC,");
		sbSql.append(" WM_CONCAT (A.ORDER_CODE) OVER (PARTITION BY APPLICATION_NO ORDER BY A.ORDER_CODE) AS ORDER_CODE ");
		sbSql.append(" FROM MED_APPLY A, SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE AND A.CAT1_TYPE = 'LIS' AND B.CAT1_TYPE = 'LIS' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("TRANS_HOSP_CODE"))) {
			sbSql.append(" AND B.TRANS_OUT_FLG = 'Y' AND B.TRANS_HOSP_CODE IN ('");
			sbSql.append(parm.getValue("TRANS_HOSP_CODE"));
			sbSql.append("') ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("APPLICATION_NO"))) {
			sbSql.append(" AND APPLICATION_NO IN ('");
			sbSql.append(parm.getValue("APPLICATION_NO"));
			sbSql.append("') ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		sbSql.append(") GROUP BY APPLICATION_NO ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 查询外送检验标本整合后CSV数据
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryExtrnalLisSampleData(TParm parm) {
		TParm result = new TParm();
		// 查询外检样本数据
		result = this.queryExternalLisBasicData(parm);

		if (result.getErrCode() < 0) {
			result.setErrText("查询外检样本数据异常:" + result.getErrText());
			return result;
		}

		int count = result.getCount();
		if (count > 0) {
			String applicationNoList = result.getValue("APPLICATION_NO")
					.replace("[", "").replace("]", "").replace(" ", "")
					.replace(",", "','");
			parm.setData("APPLICATION_NO", applicationNoList);
			// 查询检验检查医嘱信息
			TParm medApplyResult = new TParm();

			// 查询检验医嘱信息
			if ("I".equals(parm.getValue("ADM_TYPE"))) {
				medApplyResult = this.queryInHospLisData(parm);
			}

			if (medApplyResult.getErrCode() < 0) {
				result.setErrText("查询检验医嘱数据异常:" + medApplyResult.getErrText());
				return result;
			}

			// 查询检验合码医嘱信息
			TParm lisMergeResult = this.queryLisMergeOrderData(parm);

			if (lisMergeResult.getErrCode() < 0) {
				result
						.setErrText("查询检验合码医嘱数据异常:"
								+ lisMergeResult.getErrText());
				return result;
			}

			int medCount = medApplyResult.getCount();
			int mergeCount = lisMergeResult.getCount();
			Timestamp now = SystemTool.getInstance().getDate();

			for (int i = 0; i < count; i++) {
				result.addData("DIAGNOSIS", "");// 诊断
				result.addData("SAMPLE_NO", "");// 标本号
				result.addData("REMARKS", "");// 备注
				result.addData("DA_BAR_CODE", "");// 迪安条码
				result.addData("DR_TEl", "");// 医生联系方式

				if (StringUtils.isEmpty(result.getValue("LIS_RE_DATE", i))) {
					result.setData("LIS_RE_DATE", i, now);
				}
				// 计算当时的年龄
				result
						.addData("AGE", OdiUtil.showAge(result.getTimestamp(
								"BIRTH_DATE", i), result.getTimestamp(
								"ORDER_DATE", i)));

				if ("I".equals(parm.getValue("ADM_TYPE"))) {
					for (int j = 0; j < medCount; j++) {
						if (StringUtils.equals(result.getValue(
								"APPLICATION_NO", i), medApplyResult.getValue(
								"APPLICATION_NO", j))) {
							// 采样时间
							result.setData("BLOOD_DATE", i, medApplyResult
									.getTimestamp("NS_EXEC_DATE_REAL", j));
							break;
						}
					}
				}

				for (int k = 0; k < mergeCount; k++) {
					if (StringUtils.equals(
							result.getValue("APPLICATION_NO", i),
							lisMergeResult.getValue("APPLICATION_NO", k))) {
						result.setData("ORDER_CODE", i, lisMergeResult
								.getValue("ORDER_CODE", k));
						result.setData("ORDER_DESC", i, lisMergeResult
								.getValue("ORDER_DESC", k));
						result.setData("SAMPLE_COUNT", i, lisMergeResult
								.getValue("SAMPLE_COUNT", k));
						break;
					}
				}
			}
		}

		return result;
	}
	
	/**
	 * 查询临检结果数据
	 * 
	 * @param parm 查询Parm
	 * @return reslut 查询结果集
	 */
	public TParm queryMedLisRpt(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT * FROM MED_LIS_RPT WHERE 1 = 1 ");
		if (StringUtils.isNotEmpty(parm.getValue("APPLICATION_NO"))) {
			sbSql.append("  AND APPLICATION_NO = '");
			sbSql.append(parm.getValue("APPLICATION_NO"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
			sbSql.append("  AND CAT1_TYPE = '");
			sbSql.append(parm.getValue("CAT1_TYPE"));
			sbSql.append("' ");
		}

		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 删除临检结果数据
	 * 
	 * @param parm 查询Parm
	 * @return reslut 执行结果
	 */
	public TParm deleteMedLisRpt(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("DELETE MED_LIS_RPT WHERE 1 = 1 ");
		if (StringUtils.isNotEmpty(parm.getValue("APPLICATION_NO"))) {
			sbSql.append("  AND APPLICATION_NO = '");
			sbSql.append(parm.getValue("APPLICATION_NO"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
			sbSql.append("  AND CAT1_TYPE = '");
			sbSql.append(parm.getValue("CAT1_TYPE"));
			sbSql.append("' ");
		}

		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 插入临检结果数据
	 * 
	 * @param parm 查询Parm
	 * @return reslut 执行结果
	 */
	public TParm insertMedLisRpt(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("INSERT INTO MED_LIS_RPT");
		sbSql.append("  (CAT1_TYPE,APPLICATION_NO,RPDTL_SEQ,TESTITEM_CODE,TESTITEM_CHN_DESC,");
		sbSql.append("   TEST_VALUE,TEST_UNIT,REMARK,UPPE_LIMIT,LOWER_LIMIT,CRTCLUPLMT,");
		sbSql.append("   CRTCLLWLMT,EXEC_DEV_CODE,OPT_USER,OPT_DATE,OPT_TERM,");
		sbSql.append("   TESTITEM_ENG_DESC,ORDER_NO,SEQ_NO,EXEC_DEV_DESC,PY1) ");
		sbSql.append(" VALUES (");
		sbSql.append("'" + parm.getValue("CAT1_TYPE") + "',");
		sbSql.append("'" + parm.getValue("APPLICATION_NO") + "',");
		sbSql.append(parm.getValue("RPDTL_SEQ") + ",");
		sbSql.append("'" + parm.getValue("TESTITEM_CODE") + "',");
		sbSql.append("'" + parm.getValue("TESTITEM_CHN_DESC") + "',");
		sbSql.append("'" + parm.getValue("TEST_VALUE") + "',");
		sbSql.append("'" + parm.getValue("TEST_UNIT") + "',");
		sbSql.append("'" + parm.getValue("REMARK") + "',");
		sbSql.append("'" + parm.getValue("UPPE_LIMIT") + "',");
		sbSql.append("'" + parm.getValue("LOWER_LIMIT") + "',");
		sbSql.append("'" + parm.getValue("CRTCLUPLMT") + "',");
		sbSql.append("'" + parm.getValue("CRTCLLWLMT") + "',");
		sbSql.append("'" + parm.getValue("EXEC_DEV_CODE") + "',");
		sbSql.append("'" + parm.getValue("OPT_USER") + "',");
		sbSql.append("SYSDATE,");
		sbSql.append("'" + parm.getValue("OPT_TERM") + "',");
		sbSql.append("'" + parm.getValue("TESTITEM_ENG_DESC") + "',");
		sbSql.append("'" + parm.getValue("ORDER_NO") + "',");
		sbSql.append("'" + parm.getValue("SEQ_NO") + "',");
		sbSql.append("'" + parm.getValue("EXEC_DEV_DESC") + "',");
		sbSql.append("'" + parm.getValue("PY1") + "')");

		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 查询外送检验字典数据
	 * 
	 * @param parm 查询Parm
	 * @return reslut 查询结果集
	 */
	public TParm queryMedLisExternalParam(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT * FROM MED_LIS_EXTERNAL_PARAM WHERE 1 = 1 ");
		if (StringUtils.isNotEmpty(parm.getValue("TEST_CODE"))) {
			sbSql.append("  AND TEST_CODE = '");
			sbSql.append(parm.getValue("TEST_CODE"));
			sbSql.append("' ");
		}

		sbSql.append(" ORDER BY TEST_CODE ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
	
	/**
	 * 更新检验检查数据状态
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateMedApplyStatus(TParm parm) {
		String sql = "UPDATE MED_APPLY SET STATUS = '"
				+ parm.getValue("STATUS") + "',PDFRE_FLG='"
				+ parm.getValue("PDFRE_FLG") + "' WHERE CAT1_TYPE = '"
				+ parm.getValue("CAT1_TYPE") + "' AND APPLICATION_NO = '"
				+ parm.getValue("APPLICATION_NO") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		return result;
	}
}
