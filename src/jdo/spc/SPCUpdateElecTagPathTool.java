package jdo.spc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: OPD_ORDER表 物联网与his同步
 * </p>
 * <p>
 * Description: OPD_ORDER表 物联网与his同步
 * </p>
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCUpdateElecTagPathTool extends TJDODBTool {
	
	 /** 实例*/
    public static SPCUpdateElecTagPathTool instanceObject;

    /**得到实例*/
    public static SPCUpdateElecTagPathTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCUpdateElecTagPathTool();
        return instanceObject;
    }
        
    /**查询*/
    public TParm query(TParm parm) {

    	String orgCode = parm.getValue("ORG_CODE");   	
    	    	
    	
    	
//    	String sql ="SELECT " +
//						"A.ORG_CODE," +
//						"A.ORDER_CODE," +
//						"B.ORDER_DESC," +
//						"B.SPECIFICATION," +
//						"A.ELETAG_CODE," +
//						"(SELECT SUM(C.STOCK_QTY/CASE WHEN D.DOSAGE_QTY<=0 THEN 1 ELSE D.DOSAGE_QTY END) FROM IND_STOCK C,PHA_TRANSUNIT D " +
//						"WHERE C.ORG_CODE=A.ORG_CODE AND C.ORDER_CODE=A.ORDER_CODE AND D.ORDER_CODE=C.ORDER_CODE) AS QTY " +
//					"FROM IND_STOCKM A ,PHA_BASE B " +
//					"WHERE A.ORG_CODE='"+orgCode+"' " +
//						"AND A.ELETAG_CODE IS NOT NULL " +
//						"AND A.ORDER_CODE=B.ORDER_CODE " ;
    	// <-- identity by shendr 20131127
//    	String sql ="SELECT B.ELETAG_CODE,D.ORDER_DESC,D.SPECIFICATION, "+
//					        "CASE WHEN A.STOCKQTY > 0 "+ 
//					             "THEN FLOOR(A.STOCKQTY/C.DOSAGE_QTY) || E.UNIT_CHN_DESC || CASE WHEN MOD(A.STOCKQTY,C.DOSAGE_QTY) > 0 "+ 
//					                                                                       "THEN MOD(A.STOCKQTY,C.DOSAGE_QTY) || F.UNIT_CHN_DESC "+ 
//					                                                                       "ELSE '' END "+
//					             "ELSE '' END  AS QTY "+
//					   "FROM (SELECT ORG_CODE,ORDER_CODE,SUM(STOCK_QTY) AS STOCKQTY "+
//					           "FROM IND_STOCK "+
//					          "WHERE ORG_CODE='"+orgCode+"' GROUP BY ORG_CODE,ORDER_CODE) A,IND_STOCKM B,PHA_TRANSUNIT C,PHA_BASE D,SYS_UNIT E,SYS_UNIT F "+
//					  "WHERE B.ORG_CODE=A.ORG_CODE "+
//					    "AND B.ORDER_CODE=A.ORDER_CODE "+
//					    "AND B.ELETAG_CODE IS NOT NULL "+
//					    "AND C.ORDER_CODE=A.ORDER_CODE "+ 
//					    "AND D.ORDER_CODE=A.ORDER_CODE "+ 
//					    "AND E.UNIT_CODE=D.STOCK_UNIT "+ 
//					    "AND F.UNIT_CODE=D.DOSAGE_UNIT";
    	String sql = "SELECT B.ELETAG_CODE,D.ORDER_DESC,D.SPECIFICATION, "
			       +"CASE WHEN SUM(A.STOCK_QTY) > 0 THEN FLOOR(SUM(A.STOCK_QTY)/C.DOSAGE_QTY) || E.UNIT_CHN_DESC ||  "
			       +"     CASE WHEN MOD(SUM(A.STOCK_QTY),C.DOSAGE_QTY) > 0  "
			       +"          THEN MOD(SUM(A.STOCK_QTY),C.DOSAGE_QTY) || F.UNIT_CHN_DESC ELSE '' END  "
			       +"ELSE '' END  AS QTY  "
				   +"FROM IND_STOCKM B,IND_STOCK A,PHA_TRANSUNIT C,PHA_BASE D,SYS_UNIT E,SYS_UNIT F "
				   +"WHERE B.ORG_CODE = '"+orgCode+"' "
				   +"AND B.ORG_CODE=A.ORG_CODE(+)  "
				   +"AND B.ORDER_CODE=A.ORDER_CODE(+)  "
				   +"AND B.ORDER_CODE=C.ORDER_CODE(+)  "
				   +"AND B.ORDER_CODE=D.ORDER_CODE(+)  "
				   +"AND D.STOCK_UNIT=E.UNIT_CODE(+)  "
				   +"AND D.DOSAGE_UNIT=F.UNIT_CODE(+)  "
				   +"GROUP BY B.ELETAG_CODE,B.ORG_CODE,B.ORDER_CODE,C.DOSAGE_QTY, "
				   +"E.UNIT_CHN_DESC,F.UNIT_CHN_DESC,D.ORDER_DESC,D.SPECIFICATION";
    	// --------->
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
        	
}
