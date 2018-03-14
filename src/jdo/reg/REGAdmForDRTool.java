package jdo.reg;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:预约挂号tool </p>
 *
 * <p>Description:预约挂号tool </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author zhouGC attributable
 * @version 1.0
 */
public class REGAdmForDRTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static REGAdmForDRTool instanceObject;
    /**
     * 得到实例
     * @return ClinicAreaTool
     */
    public static REGAdmForDRTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGAdmForDRTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public REGAdmForDRTool() {
        setModuleName("reg\\REGPatAdmForDrModule.x");
        onInit();
    }

    /**
     * 退挂更新
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {
        TParm result = new TParm();
        result = this.update("updateForUnReg", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询VIP班表信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selVIPDate(TParm parm) {
        TParm result = query("selVIPDate", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增普通挂号
     * @param parm
     * @return  UPDATEZH
     */
    public TParm onSaveP(TParm parm) {
        TParm result = new TParm();
        result = this.update("insertInfoP", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增VIP挂号
     * @param parm
     * @return
     */
    public TParm onSaveV(TParm parm) {
        TParm result = new TParm();
//        System.out.println("<><>>>+" + parm);
        result = this.update("insertInfoV", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        //add by huangtt 20131128 start  ADM_DATE=2013-11-28 00:00:00.0
        String date=parm.getValue("ADM_DATE").substring(0, 10).replace("-", "");
        parm.setData("ADM_DATE", date);
        result = this.update("UPDATEvip", parm);
      //add by huangtt 20131128 end
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询患者挂号信息SQL
     */
    public static String QueryREGPatMessSql(String MR_NO, String DR_CODE,
                                 String state_date, String end_date,
                                 String dept_code, String session_code
)
    {
        String sql = "  SELECT CASE_NO,ADM_DATE,SESSION_CODE,DEPT_CODE,DR_CODE,QUE_NO,ADM_STATUS,ARRIVE_FLG,APPT_CODE,CLINICROOM_NO,ADM_TYPE "+
      "  FROM REG_PATADM " +
      "  WHERE MR_NO='" + MR_NO + 
//      "' AND DEPT_CODE = '" + dept_code +
      "' AND OPT_DATE BETWEEN TO_DATE ('" + state_date +
      "', 'YYYYMMDDHH24MISS')" +
      "  AND  TO_DATE ('" + end_date +
      "', 'YYYYMMDDHH24MISS') " +
      " AND REGCAN_USER IS NULL" +
      " ORDER BY ADM_DATE,SESSION_CODE,DEPT_CODE,DR_CODE,QUE_NO";
//      " AND SESSION_CODE='" + session_code + "'  " +
//      "  AND DR_CODE='" + DR_CODE + "' ";

        return sql;
    }

    /**
     *  VIP挂号信息SQL
     */
    public static String QueryVip(String ADM_DATE, String DR_CODE, String SESSION_CODE, String dept_code,String admType)
    {
    	//huangtt start 2013/10/30
    	String drCode="";
    	if(DR_CODE.length()>0){
    		drCode=" AND REG_SCHDAY.DR_CODE='"+DR_CODE+"'";
    	}
    	//huangtt end 2013/10/30
        String sql = "SELECT REG_SCHDAY.CLINICTYPE_CODE,  REG_CLINICQUE.SESSION_CODE AS SESSION_CODE,REG_CLINICQUE.CLINICROOM_NO AS CLINICROOM_NO," +
         "  REG_SCHDAY.DEPT_CODE  AS DEPT_CODE," +
         "  REG_SCHDAY.DR_CODE AS DR_CODE," +
         "  REG_CLINICQUE.QUE_NO AS QUE_NO," +
         "  REG_CLINICQUE.START_TIME AS START_TIME," +
         "  REG_CLINICQUE.QUE_STATUS AS QUE_STATUS" +
         "  FROM   REG_CLINICQUE , REG_SCHDAY " +
         "  WHERE       REG_CLINICQUE.ADM_DATE = REG_SCHDAY.ADM_DATE" +
         "  AND REG_CLINICQUE.CLINICROOM_NO = REG_SCHDAY.CLINICROOM_NO" +  //add by huangtt 2013/10/30
         "  AND REG_CLINICQUE.SESSION_CODE = REG_SCHDAY.SESSION_CODE" +  //add by huangtt 2013/10/30
         "  AND REG_CLINICQUE.SESSION_CODE = '" + SESSION_CODE + "'" +   //add by huangtt 2013/10/30
         "  AND REG_CLINICQUE.ADM_TYPE = '" + admType + "'" +   //add by huangtt 2013/12/05
         "  AND REG_SCHDAY.DEPT_CODE = '" + dept_code + "'" +   //add by huangtt 2013/11/06
         "  AND REG_SCHDAY.ADM_DATE = '" + ADM_DATE + "'" +
         drCode+   //add by huangtt 2013/10/30
         "   AND REG_SCHDAY.VIP_FLG='Y'" +
         "  ORDER BY REG_SCHDAY.DR_CODE,REG_CLINICQUE.SESSION_CODE,REG_CLINICQUE.CLINICROOM_NO,REG_CLINICQUE.QUE_NO";  //add by huangtt REG_SCHDAY.DR_CODE  2013/10/30
        return sql;
        
    }

    /**
     * 患者基本信息SQL
     */
    public static String QuseryPatMessSql(String MR_NO)
    {
        String sql = " SELECT PAT_NAME,PY1,BIRTH_DATE,SEX_CODE,FOREIGNER_FLG,IDNO,TEL_HOME,POST_CODE,ADDRESS,CTZ1_CODE " +
         " FROM SYS_PATINFO" +
         " WHERE MR_NO='" + MR_NO + "'";
        return sql;
    }

    /**
     * 查询当班医生信息，返回给下拉框SQL
     */
    public static String QueryDRNameSql(String sessionCode, String dept_code, String w)
    {
        String sql =
            "SELECT REG_SCHDAY.DR_CODE AS DR_CODE,SYS_OPERATOR.USER_NAME AS USER_NAME"+
            "  FROM   REG_SCHDAY,SYS_OPERATOR"+
            "  WHERE   ADM_DATE = '"+w+"' AND SESSION_CODE = '"+sessionCode+"' AND DEPT_CODE = '"+dept_code+"' AND SYS_OPERATOR.USER_ID=REG_SCHDAY.DR_CODE";

       return sql;

    }
    /**
     * 医生挂号信息(全部)SQL
     */
    public static String QueryDRCountSql(String stateDate, String dept,String sessionCode,String admType)
    {
        String sql =
          " SELECT   'N' AS XUAN,CLINICTYPE_CODE, DEPT_CODE,CLINICROOM_NO, DR_CODE,MAX_QUE,QUE_NO,VIP_FLG,(QUE_NO-1) QUE,SESSION_CODE," +
          " (SELECT COUNT (SEE_DR_FLG)" +
          " FROM REG_PATADM" +
          " WHERE DR_CODE = REG_SCHDAY.DR_CODE" +
          " AND SESSION_CODE = REG_SCHDAY.SESSION_CODE" +
          " AND CLINICROOM_NO = REG_SCHDAY.CLINICROOM_NO" +
          " AND SEE_DR_FLG = 'N'" +
          " AND ADM_DATE = TO_DATE (REG_SCHDAY.ADM_DATE, 'YYYY-MM-DD')" +
          " AND REGCAN_USER IS NULL) AS COUNT" +
          " FROM   REG_SCHDAY" +
          " WHERE   ADM_DATE = '" + stateDate + "' AND DEPT_CODE = '" + dept +
          "' AND ADM_TYPE='"+admType+
          "' AND SESSION_CODE = '" + sessionCode +"'"; //add by huangtt 20131105
      return sql;

    }

    /**
    * 医生挂号信息（个人）SQL
    */
   public static String QueryDRSql(String stateDate, String dept,String DR_CODE,String sessionCode,String admType)
   {
       String sql =
         " SELECT   'N' AS XUAN,CLINICTYPE_CODE, DEPT_CODE,CLINICROOM_NO, DR_CODE,MAX_QUE,QUE_NO,VIP_FLG,(QUE_NO-1) QUE,SESSION_CODE," +
         " (SELECT COUNT (SEE_DR_FLG)" +
         " FROM REG_PATADM" +
         " WHERE DR_CODE = REG_SCHDAY.DR_CODE" +
         " AND SESSION_CODE = REG_SCHDAY.SESSION_CODE" +
         " AND CLINICROOM_NO = REG_SCHDAY.CLINICROOM_NO" +
         " AND SEE_DR_FLG = 'N'" +
         " AND ADM_DATE = TO_DATE (REG_SCHDAY.ADM_DATE, 'YYYY-MM-DD')" +
         " AND REGCAN_USER IS NULL) AS COUNT" +
         " FROM   REG_SCHDAY" +
         " WHERE   ADM_DATE = '" + stateDate + "' AND DEPT_CODE = '" + dept +
         "' AND DR_CODE='"+DR_CODE+"' " +
         " AND ADM_TYPE='"+admType+"' " +
         " AND SESSION_CODE='"+sessionCode+"'";
     return sql;
   }
   /**
    * 查询普通医生就诊号
    */
   public static String QueryDRPSql(String stateDate, String clinicroomNo, String drCode, String sessionCode,String admType){
	   String sql = "SELECT QUE_NO" +
	   		" FROM REG_SCHDAY" +
	   		" WHERE ADM_DATE = '"+stateDate+"'" +
	   		" AND CLINICROOM_NO = '"+clinicroomNo+"'" +
	   		" AND DR_CODE = '"+drCode+"'" +
	   		" AND ADM_TYPE = '"+admType+"'" +
	   		" AND SESSION_CODE = '"+sessionCode+"'";
	   return sql;
   }


    /**
     * 修改诊号
     */

    public static String updateDept(String qu,String start_Date,String KS1,String YS1,String SD1,String ZJ1,String admType,String drCode )
    {
        String sql = "UPDATE  REG_SCHDAY SET QUE_NO=QUE_NO+1 " +
                " WHERE ADM_DATE='" +  start_Date + "' AND DEPT_CODE='" + KS1 +
                "' AND DR_CODE='" + YS1 + "'" +
                " AND SESSION_CODE='"+SD1+"'" +
                " AND ADM_TYPE='"+admType+"'" +
                " AND DR_CODE='"+drCode+"'" +
                " AND CLINICROOM_NO='"+ZJ1+"'" ;

        return sql;
    }
    
    /**
     * 在不选号的情况下，更新VIP没有占号的最小号
     */
    public static String updateVIP(String date,String sessionCode,String clinicroomNo,String que_no ){
    	String sql = "UPDATE  REG_CLINICQUE SET QUE_STATUS='Y' WHERE QUE_STATUS = 'N' AND ADM_DATE = '"+date+"' AND SESSION_CODE = '"+sessionCode+"' AND CLINICROOM_NO = '"+clinicroomNo+"' AND QUE_NO='"+que_no+"'";
    	return sql;
    }
    
    /**
     * 在不选号的情况下，查找VIP没有占号的最小号
     */
    public static String selectVIP(String date,String sessionCode,String clinicroomNo ){
    	String sql = "SELECT MIN(QUE_NO) QUE_NO FROM REG_CLINICQUE WHERE QUE_STATUS = 'N'  AND ADM_DATE = '"+date+"' AND SESSION_CODE = '"+sessionCode+"' AND CLINICROOM_NO = '"+clinicroomNo+"' ";
    	return sql;
    }
    
    }
