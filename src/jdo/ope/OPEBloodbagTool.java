package jdo.ope;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 手术记录</p>
 *
 * <p>Description: 手术记录</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 1.0
 */
public class OPEBloodbagTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static OPEBloodbagTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static OPEBloodbagTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPEBloodbagTool();
        return instanceObject;
    }

    /**
     * 更新接收血袋人员和时间
     * @param tparm
     * @param conn
     * @return
     */
    public TParm updateReceive(TParm tparm, TConnection conn) {
    	String sql = "";
    	TParm result = new TParm();
    	for(int i=0; i<tparm.getCount(); i++){
    		sql = "UPDATE BMS_BLOOD SET RECEIVED_USER = '"
    				+ tparm.getValue("RECEIVED_USER", i)
    				+ "', BLDTRANS_USER = '"
    				+ tparm.getValue("RECEIVED_USER", i)
    				+ "', BLDTRANS_TIME = SYSDATE, RECEIVED_DATE = SYSDATE WHERE BLOOD_NO = '"
    				+ tparm.getValue("BLOOD_NO", i)+"'";
//    		System.out.println("=================="+sql);
    		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				return result;
			}
    	}
    	return result;
	}
    
    /**
     * 更新接收血袋人员和时间
     * @param tparm
     * @param conn
     * @return
     */
    public TParm updateRecheck(TParm tparm, TConnection conn) {
    	TParm result = new TParm();
    	String sql = "UPDATE BMS_BLOOD SET CHECK_USER = '"
    			+ tparm.getValue("CHECK_USER")
    			+ "', CHECK_DATE = SYSDATE, BLDTRANS_END_USER = '"
    			+ tparm.getValue("BLDTRANS_END_USER")
    			+ "', BLDTRANS_END_TIME = TO_DATE ('"
				+ tparm.getValue("BLDTRANS_END_TIME")
				+ "','YYYY/MM/DD HH24:MI:SS'), FACT_VOL = '"
    			+ tparm.getValue("FACT_VOL")
    			+ "' WHERE BLOOD_NO = '"
    			+ tparm.getValue("BLOOD_NO")+"'";
//    		System.out.println("=================="+sql);
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			return result;
		}
    	return result;
    }
}
