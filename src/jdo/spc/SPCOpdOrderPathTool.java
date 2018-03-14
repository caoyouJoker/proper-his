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
public class SPCOpdOrderPathTool extends TJDODBTool {
	
	 /** 实例*/
    public static SPCOpdOrderPathTool instanceObject;

    /**得到实例*/
    public static SPCOpdOrderPathTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOpdOrderPathTool();
        return instanceObject;
    }
        
    /**查询*/
    public TParm query(TParm parm) {

    	String phaCheckDate = parm.getValue("ORDER_DATE");//2012-04-01   	
    	    	
    	
    	String sql = "SELECT " +
    						"CASE_NO, RX_NO, " +
    						"SEQ_NO, PHA_CHECK_CODE, " +
    						"PHA_CHECK_DATE, PHA_DOSAGE_CODE, " +
    						"PHA_DOSAGE_DATE, PHA_DISPENSE_CODE, " +
    						"PHA_DISPENSE_DATE,PHA_RETN_CODE, " +
    						"PHA_RETN_DATE, " +
    						"BATCH_SEQ1,VERIFYIN_PRICE1, " +
    						"BATCH_SEQ2,VERIFYIN_PRICE2, " +
    						"BATCH_SEQ3,VERIFYIN_PRICE3 " +
    					"FROM " +
    						"OPD_ORDER " +
    					"WHERE " +
    						"TO_CHAR (ORDER_DATE, 'YYYY-MM-DD') = '" + phaCheckDate + "' OR " +
    						"TO_CHAR (PHA_CHECK_DATE, 'YYYY-MM-DD') = '" + phaCheckDate + "' OR " +
    						"TO_CHAR (PHA_DOSAGE_DATE, 'YYYY-MM-DD') = '" + phaCheckDate + "' OR " +
    						"TO_CHAR (PHA_DISPENSE_DATE, 'YYYY-MM-DD') = '" + phaCheckDate + "' OR " +
    						"TO_CHAR (PHA_RETN_DATE, 'YYYY-MM-DD') = '" + phaCheckDate + "'";    	
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    
	/**保存方法*/
	public boolean save(TParm parm) {
		
		String sql = "INSERT INTO IND_BATCH_LOG " +
							"(BATCH_CODE,START_TIME,END_TIME,RESULT_DESC) " +
					"VALUES('"+parm.getData("BATCH_CODE")+"','"+
								parm.getData("START_TIME")+"','"+
								parm.getData("END_TIME")+"','"+
								parm.getData("RESULT_DESC")+"')";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
    	
}
