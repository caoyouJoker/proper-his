package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>Title: 病案编目Tool</p>
 *
 * <p>Description: 病案编目Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-11
 * @version 1.0
 */
public class MROCatalogingTool
    extends TJDOTool {
    //SQL语句
    private static final String QUERY_MRO_RECORD= //根据条件查询首页表信息
        "SELECT A.MR_NO,A.IPD_NO,A.PAT_NAME,A.IN_DATE,A.OUT_DATE, "+
        " TRUNC(SYSDATE-IN_DATE) AS REAL_STAY_DAYS,TRUNC(SYSDATE-OUT_DATE) AS OUT_DAYS,C.DEPT_CHN_DESC AS OUT_DEPT,D.STATION_DESC AS OUT_STATION,A.CASE_NO, "+
        " A.ADMCHK_FLG,A.DIAGCHK_FLG,A.BILCHK_FLG,A.QTYCHK_FLG,B.CHN_DESC AS SEX,A.BIRTH_DATE,A.TEACH_EMR, A.TEST_EMR, A.VS_DR_CODE,A.ADM_SOURCE  "+
        " FROM MRO_RECORD A,SYS_DICTIONARY B,SYS_DEPT C,SYS_STATION D,SYS_PATINFO E "+
        " WHERE B.GROUP_ID='SYS_SEX' "+
        " AND A.SEX=B.ID "+
        " AND A.OUT_DEPT=C.DEPT_CODE(+) "+
        " AND A.OUT_STATION=D.STATION_CODE(+) "+
        " AND A.MR_NO=E.MR_NO ";//wanglong add 20141031
    /**
     * 实例
     */
    public static MROCatalogingTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROCatalogingTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROCatalogingTool();
        return instanceObject;
    }

    public MROCatalogingTool() {
    }
    /**
     * 根据条件查询病案编目
     * @param parm TParm
     * @return TParm
     */
    public TParm queryMROInfo(TParm parm){
        TParm result;
        String sql = this.getMROInfoSQL(parm);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getErrCode()<0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 拼接查询SQL
     * @param parm TParm
     * @return String
     */
    private String getMROInfoSQL(TParm parm){
        String sql = "";
        //判断查询类别
        if(parm.getValue("checkType").equals("0")){//未出院
            sql = QUERY_MRO_RECORD + " AND A.OUT_DATE IS NULL ";
        }else if(parm.getValue("checkType").equals("1")){//出院/未完成
            sql = QUERY_MRO_RECORD + " AND A.OUT_DATE IS NOT NULL ";
//        + " AND (A.ADMCHK_FLG<>'Y' OR A.DIAGCHK_FLG<>'Y' OR A.BILCHK_FLG<>'Y' OR A.QTYCHK_FLG<>'Y')";//delete by wanglong 20130124
            sql = addExtraWhere(sql, parm);//add by wanglong 20130124
        }else if(parm.getValue("checkType").equals("2")){//出院/已完成
            sql = QUERY_MRO_RECORD + " AND A.OUT_DATE IS NOT NULL " +
                " AND A.ADMCHK_FLG='Y' "+
                " AND A.DIAGCHK_FLG='Y' "+
                " AND A.BILCHK_FLG='Y' "+
                " AND A.QTYCHK_FLG='Y' ";
        }
        
        
      //  //日间手术add by huangtt 20161226
      //   if(parm.getValue("DAY_OPE_CODE").trim().length() > 0){
      //  	 sql += " AND A.DAY_OPE_CODE='" + parm.getValue("DAY_OPE_CODE") + "'";		 
      //  }
        
        //================pangben modify 20110518 start
        //区域
        if (parm.getValue("REGION_CODE").trim().length() > 0) {
            sql += " AND A.REGION_CODE='" + parm.getValue("REGION_CODE") + "'";
        }
        //================pangben modify 20110518 stop
        //出院科别
        if (parm.getValue("OUT_DEPT").trim().length() > 0) {
            sql += " AND A.OUT_DEPT='"+ parm.getValue("OUT_DEPT") +"'";
        }
        //姓名
        if(parm.getValue("PAT_NAME").trim().length()>0){
            sql += " AND A.PAT_NAME LIKE '%"+ parm.getValue("PAT_NAME") +"%'";
        }
        //姓名全拼 wanglong add 20141031
        if (parm.getValue("NAME_PY").trim().length() > 0) {
            sql +=
                    " AND py$(A.PAT_NAME)='"
                            + parm.getValue("NAME_PY").trim().toUpperCase() + "'";
        }
        //性别
        if(parm.getValue("SEX").trim().length()>0){
            sql += " AND A.SEX='"+ parm.getValue("SEX") +"'";
        }
        //住院号
        if(parm.getValue("IPD_NO").trim().length()>0){
            sql += " AND A.IPD_NO='"+ parm.getValue("IPD_NO") +"'";
        }
        //病案号
        if(parm.getValue("MR_NO").trim().length()>0){
            sql += " AND A.MR_NO='"+ parm.getValue("MR_NO") +"'";
        }
        //身份证号
        if(parm.getValue("IDNO").trim().length()>0){
            sql += " AND A.IDNO='"+ parm.getValue("IDNO") +"'";
        }
        //出院日期
        if (parm.getValue("OUT_DATE_START").trim().length()>0 &&
            parm.getValue("OUT_DATE_END").trim().length()>0) {
            sql += " AND A.OUT_DATE BETWEEN TO_DATE('" +
                parm.getValue("OUT_DATE_START") + "','YYYYMMDD') AND TO_DATE('" +
                parm.getValue("OUT_DATE_END") +
                "235959','YYYYMMDDHH24MISS')";
        }
        //入院日期
        if (parm.getValue("IN_DATE_START").trim().length() > 0 &&
            parm.getValue("IN_DATE_END").trim().length() > 0) {
            sql += " AND A.IN_DATE BETWEEN TO_DATE('" +
                parm.getValue("IN_DATE_START") + "','YYYYMMDD') AND TO_DATE('" +
                parm.getValue("IN_DATE_END") + "235959','YYYYMMDDHH24MISS')";
        }
        //费用总计
        if (parm.getData("CHARGE_START") != null && parm.getData("CHARGE_END") != null &&
            parm.getValue("CHARGE_START").trim().length() > 0 &&
            parm.getValue("CHARGE_END").trim().length() > 0) {
            //前台控件默认会有0 如果起始值和结束值都为0  那么不执行
            if(!(parm.getInt("CHARGE_START")==0&&parm.getInt("CHARGE_END")==0)){
                sql +=
                    " AND (A.CHARGE_01+A.CHARGE_02+A.CHARGE_03+A.CHARGE_04+A.CHARGE_05+A.CHARGE_06" +
                    "+A.CHARGE_07+A.CHARGE_08+A.CHARGE_09+A.CHARGE_10+A.CHARGE_11+A.CHARGE_12+A.CHARGE_13+" +
                    "A.CHARGE_14+A.CHARGE_15+A.CHARGE_16+A.CHARGE_17+A.CHARGE_18+A.CHARGE_19+A.CHARGE_20) BETWEEN " +
                    parm.getValue("CHARGE_START") + " AND " +
                    parm.getValue("CHARGE_END");
            }
        }
//        //出院主诊断
//        if(parm.getValue("OUT_DIAG_CODE1").trim().length()>0){
//            sql += " AND (A.OUT_DIAG_CODE1='"+ parm.getValue("OUT_DIAG_CODE1") +"' OR A.OUT_DIAG_CODE2='"+ parm.getValue("OUT_DIAG_CODE1") +"' OR " +
//            	" A.OUT_DIAG_CODE3='"+ parm.getValue("OUT_DIAG_CODE1") +"' OR A.OUT_DIAG_CODE4='"+ parm.getValue("OUT_DIAG_CODE1") +"' OR " +
//            	"A.OUT_DIAG_CODE5='"+ parm.getValue("OUT_DIAG_CODE1") +"' OR A.OUT_DIAG_CODE6='"+ parm.getValue("OUT_DIAG_CODE1") +"')";
//        }
//        //手术编码
//        if(parm.getValue("OP_CODE").trim().length()>0){
//            sql += " AND E.OP_CODE='"+ parm.getValue("OP_CODE") +"'";
//        }
//        //手术日期
//        if(parm.getValue("OP_DATE").trim().length()>0){
//            sql += " AND A.OP_DATE=TO_DATE('"+parm.getValue("OP_DATE")+"','YYYYMMDD')";
//        }
        //转归情况  CODE1_STATUS
        if (parm.getValue("CODE1_STATUS").trim().length()>0) {
            sql += " AND A.CODE1_STATUS='"+parm.getValue("CODE1_STATUS")+"'";
        }
        //住院医师
        if (parm.getValue("VS_DR_CODE").trim().length()>0) {
            sql += " AND A.VS_DR_CODE='"+parm.getValue("VS_DR_CODE")+"'";
        }
        //科主任
        if (parm.getValue("DIRECTOR_DR_CODE").trim().length()>0) {
            sql += " AND A.DIRECTOR_DR_CODE='"+parm.getValue("DIRECTOR_DR_CODE")+"'";
        }
        //工作单位
        if (parm.getValue("OFFICE").trim().length()>0) {
            sql += " AND A.OFFICE LIKE '%"+parm.getValue("OFFICE")+"%'";
        }
        //生日
        if (parm.getValue("BIRTH_DATE").trim().length()>0) {
            sql += " AND A.BIRTH_DATE=TO_DATE('"+parm.getValue("BIRTH_DATE")+"','YYYYMMDD')";
        }
        //联系人
        if (parm.getValue("CONTACTER").trim().length()>0) {
            sql += " AND A.CONTACTER LIKE '%"+parm.getValue("CONTACTER")+"%'";
        }
        //联系人电话
        if (parm.getValue("CONT_TEL").trim().length()>0) {
            sql += " AND A.CONT_TEL='"+parm.getValue("CONT_TEL")+"'";
        }
        //家庭电话
        if (parm.getValue("H_TEL").trim().length()>0) {
            sql += " AND A.H_TEL='"+parm.getValue("H_TEL")+"'";
        }
//        //住院处提交
//        if(parm.getValue("ADMCHK_FLG").trim().length()>0){//delete by wanglong 20130124
//            sql += " AND A.ADMCHK_FLG='"+parm.getValue("ADMCHK_FLG")+"'";
//        }
//        //医师提交
//        if(parm.getValue("DIAGCHK_FLG").trim().length()>0){
//            sql += " AND A.DIAGCHK_FLG='"+parm.getValue("DIAGCHK_FLG")+"'";
//        }

        /*
         * 病案首页中，加字段临床试验病例字典、
         * 教学病例字典同时，加上查询功能；病历维护加两个字段 TEST_EMR,TEACH_EMR
         * Modify ZhenQin ------ 2010-05-09
         */
        //临床试验病例标记
        if(parm.getValue("TEST_EMR").length() > 0){
            sql += " AND A.TEST_EMR = '"+parm.getValue("TEST_EMR") + "'";
        }
        //教学病例标记
        if(parm.getValue("TEACH_EMR").length() > 0){
            sql += " AND A.TEACH_EMR = '"+parm.getValue("TEACH_EMR") + "'";
        }
//        //账务提交
//        if(parm.getValue("BILCHK_FLG").trim().length()>0){//delete by wanglong 20130124
//            sql += " AND A.BILCHK_FLG='"+parm.getValue("BILCHK_FLG") + "'";
//        }
//        //质控提交
//        if(parm.getValue("QTYCHK_FLG").trim().length()>0){
//            sql += " AND A.QTYCHK_FLG='"+parm.getValue("QTYCHK_FLG") + "'";
//        }
        //诊断日期
        if(parm.getValue("CONFIRM_DATE").trim().length()>0){
            sql += " AND TRUNC(A.CONFIRM_DATE) = TO_DATE('"+parm.getValue("CONFIRM_DATE")+"','YYYYMMDD')";
        }
//        System.out.println(sql);
        return sql;
    }
    
    /**
     * 在SQL中增加“未提交选项”的相关where条件
     * @param sql
     * @param parm
     * @return
     */
    private String addExtraWhere(String sql, TParm parm) {//add by wanglong 20130124
        boolean admckFlg = parm.getBoolean("ADMCHK_FLG");
        boolean diagckFlg = parm.getBoolean("DIAGCHK_FLG");
        boolean bilckFlg = parm.getBoolean("BILCHK_FLG");
        boolean qtyckFlg = parm.getBoolean("QTYCHK_FLG");
        if (admckFlg && !diagckFlg && !bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (!admckFlg && diagckFlg && !bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (!admckFlg && !diagckFlg && bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (!admckFlg && !diagckFlg && !bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG='Y')";
        } else if (admckFlg && diagckFlg && !bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (!admckFlg && diagckFlg && bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (!admckFlg && !diagckFlg && bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG='Y')";
        } else if (admckFlg && !diagckFlg && bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (!admckFlg && diagckFlg && !bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG='Y')";
        } else if (admckFlg && !diagckFlg && !bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG='Y')";
        } else if (!admckFlg && diagckFlg && bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG<>'Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG='Y')";
        } else if (admckFlg && !diagckFlg && bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG<>'Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG='Y')";
        } else if (admckFlg && diagckFlg && !bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG<>'Y' AND A.QTYCHK_FLG='Y')";
        } else if (admckFlg && diagckFlg && bilckFlg && !qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG<>'Y')";
        } else if (admckFlg && diagckFlg && bilckFlg && qtyckFlg) {
            sql +=
                    " AND (A.ADMCHK_FLG='Y' AND A.DIAGCHK_FLG='Y' AND A.BILCHK_FLG='Y' AND A.QTYCHK_FLG='Y')";
        } else sql +=
                " AND (A.ADMCHK_FLG<>'Y' OR A.DIAGCHK_FLG<>'Y' OR A.BILCHK_FLG<>'Y' OR A.QTYCHK_FLG<>'Y')";
        return sql;
    }

    /**
     * 查询MRO_RECORD_DIAG表结果
     * @param parm
     * @return
     */
	public TParm queryMRO_DIAGInfo(TParm parm) {
		String sqlMRO_DIAG = "SELECT DISTINCT CASE_NO FROM MRO_RECORD_DIAG WHERE 1=1 ";
		if(null != parm.getValue("ICD_CODE") && parm.getValue("ICD_CODE").trim().length() > 0){
			sqlMRO_DIAG += " AND ICD_CODE = '" + parm.getValue("ICD_CODE") + "'";
		}
		if(StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sqlMRO_DIAG += " AND CASE_NO = '" + parm.getValue("CASE_NO") + "'";
		}
		if(StringUtils.isNotEmpty(parm.getValue("IO_TYPE"))) {
			sqlMRO_DIAG += " AND IO_TYPE = '" + parm.getValue("IO_TYPE") + "'";
		}
		
//		System.out.println(sqlMRO_DIAG);
		TParm resultMRO_DIAG = new TParm(TJDODBTool.getInstance().select(sqlMRO_DIAG));
		return resultMRO_DIAG;
	}

	/**
     * 查询MRO_RECORD_OP表结果
     * @param parm
     * @return
     */
	public TParm queryMRO_OPInfo(TParm parm) {
		String sqlMRO_OP = "SELECT DISTINCT CASE_NO FROM MRO_RECORD_OP WHERE 1=1 ";
//						   "WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "'";
		if(null != parm.getValue("OP_LEVEL") && parm.getValue("OP_LEVEL").trim().length() > 0){
			sqlMRO_OP += " AND OP_LEVEL = '" + parm.getValue("OP_LEVEL") + "'";
		}
		if(null != parm.getValue("OP_CODE") && parm.getValue("OP_CODE").trim().length() > 0){
			sqlMRO_OP += " AND OP_CODE = '" + parm.getValue("OP_CODE") + "'";
		}
		if(null != parm.getValue("OP_DATE") && parm.getValue("OP_DATE").trim().length() > 0){
			sqlMRO_OP += " AND OP_DATE = TO_DATE('" + parm.getValue("OP_DATE") + "','YYYYMMDD')";
		}
		if(null != parm.getValue("OP_HEALTH") && parm.getValue("OP_HEALTH").trim().length() > 0) {
			sqlMRO_OP += " AND HEALTH_LEVEL = '" + parm.getValue("OP_HEALTH") + "'";
		}
//		System.out.println(sqlMRO_OP);
		TParm resultMRO_OP = new TParm(TJDODBTool.getInstance().select(sqlMRO_OP));
		return resultMRO_OP;
	}
}
