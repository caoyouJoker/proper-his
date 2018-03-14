package com.javahis.util;

import com.dongyang.util.StringTool;
import jdo.opd.OPDSysParmTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.jdo.TDataStore;
import java.text.DecimalFormat;

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
public class ADMUtil {
    private static ADMUtil instanceObject;
    public ADMUtil(){
   }
   public static synchronized ADMUtil getInstance(){
       if (instanceObject == null) {
           instanceObject = new ADMUtil();
       }
       return instanceObject;
   }
   /**
    * 转换 case_no(in_case_no ----> opd_case_no)
    * @param caseNo 住院就诊号
    * @return String caseNo 门急诊就诊号
    */
   public static String getCaseNo(String caseNo) {
	   
	   String sql = "SELECT IN_CASE_NO,OPD_CASE_NO"
   			+ " FROM ADM_RESV"
   		    +" WHERE IN_CASE_NO='"+caseNo+"'";
	   
	   System.out.println("AMDUtil sql--->"+sql);
       TParm result = new TParm(TJDODBTool.getInstance().select(sql));
       
       System.out.println("AMDUtil result--->"+result);
       if (result.getErrCode() < 0) {
//           err("ERR:" + result.getErrCode() + result.getErrText() +
//           		result.getErrName());
           return caseNo;
       }
       return result.getValue("OPD_CASE_NO", 0);
   }
   /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

   

}
