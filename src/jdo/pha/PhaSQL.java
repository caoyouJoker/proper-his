package jdo.pha;

import org.apache.commons.lang.StringUtils;

import jdo.clp.intoPathStatisticsTool;

import com.dongyang.data.TParm;
import com.javahis.ui.customquery.ParmInputControl;

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
public class PhaSQL {
    public PhaSQL() {
    }

    public static String getPhaDecoct() {
        return "SELECT DEPT_CODE AS ID, DEPT_CHN_DESC AS NAME "
            + "FROM SYS_DEPT A WHERE DEPT_GRADE = '3' "
            + "AND CLASSIFY = '4' AND ACTIVE_FLG = 'Y'";
    }

    /**
     *
     * @return String
     */
    public static String getPhaDecoctByIndOrg(String ind_org) {
        return "SELECT * FROM SYS_DEPT WHERE DEPT_GRADE = '3' AND ACTIVE_FLG = 'Y' ";
    }
    
    /**
    * 查询病人信息根据病案号
    * @return String
    * @author liyh
    */
   public static String getPationInfo(TParm parm) {
       return " SELECT  A.MR_NO,A.PAT_NAME ,A.BIRTH_DATE ,A.SEX_CODE,B.CHN_DESC AS SEX_NAME , " +
			  " CASE WHEN FLOOR(MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD'),A.BIRTH_DATE)/12)= 0 " +
	   		  "      	  THEN TO_CHAR(FLOOR(MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD'),A.BIRTH_DATE))) || '月' " +
	   		  "  	  	  ELSE TO_CHAR(FLOOR(MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD'),A.BIRTH_DATE)/12)) " +
       		  " END AS AGE "		+
              " FROM SYS_PATINFO A,SYS_DICTIONARY B " +
              " WHERE  A.MR_NO='" + parm.getValue("MR_NO") + "' AND B.GROUP_ID='SYS_SEX' AND A.SEX_CODE=B.ID " ;        
   }
   
   /**
    * 保存病人病案号和电子标签关系
    * @return String
    * @author liyh
    */
   public static String savBasketInfo(TParm parm) {
       return " INSERT INTO IND_MEDBASKET(  MR_NO,BASKET_ID,RX_NO,CASE_NO,PAT_NAME,SEX_TYPE,AGE,OPT_USER, OPT_DATE, OPT_TERM) " +
              " VALUES('" + parm.getValue("MR_NO") + "','" + parm.getValue("BASKET_ID") + "','" + parm.getValue("RX_NO")+"'," +
              " '" + parm.getValue("CASE_NO") + "','" + parm.getValue("PAT_NAME") + "','" + parm.getValue("SEX_TYPE") + "','" + parm.getValue("AGE") + "'," + 
              " '" + parm.getValue("OPT_USER") + "',SYSDATE,'" + parm.getValue("OPT_TERM") + "' )"; 
       
   }
   
   public static String getBasketInfo(String rxNo){
		   	String sql = " SELECT MR_NO,BASKET_ID,RX_NO,CASE_NO,PAT_NAME,SEX_TYPE,AGE "+
			 " FROM IND_MEDBASKET WHERE RX_NO='" + rxNo + "' ";
   	return sql;
   	
   }
}
