package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCExportXmlToCmsClinicTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCExportXmlToCmsClinicTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCExportXmlToCmsClinicTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCExportXmlToCmsClinicTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCExportXmlToCmsClinicTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onSave(TParm parm , TConnection conn){
    	TParm result = new TParm();
    	for(int i = 0 ; i < parm.getCount("ORDER_CODE"); i++ ){
    		TParm rowParm = parm.getRow(i) ;
    		String insertSql = " INSERT INTO  IND_ODD ( " +
    				           "  CLOSE_DATE, ORG_CODE, ORDER_CODE, " +
    				           "  LAST_ODD_QTY, OUT_QTY,ODD_QTY, " +
    				           "  OPT_USER, OPT_DATE, OPT_TERM, " +
    				           "  IS_UPDATE )  " +
    				           " VALUES  " +
    				           "( " +
    				           " '"+rowParm.getValue("CLOSE_DATE")+"','"+rowParm.getValue("ORG_CODE")+"','"+rowParm.getValue("ORDER_CODE")+"',"+
    				           rowParm.getValue("LAST_ODD_QTY")+","+rowParm.getValue("OUT_QTY")+","+rowParm.getValue("ODD_QTY")+","+
    				           " '"+rowParm.getValue("OPT_USER")+"',SYSDATE,'"+rowParm.getValue("OPT_TERM")+"', "+
    				           " '"+rowParm.getValue("IS_UPDATE")+"' " +
    				           "   ) " ;
    		//System.out.println("sql----------:"+insertSql);
    		result =  new TParm(TJDODBTool.getInstance().update(insertSql,conn));
    		if(result == null || result.getErrCode() <  0 ){
    			conn.rollback() ;
    			return result;
    		}
    	}
    	return result ;
    }
    
    public TParm onQueryIndOdd(TParm parm ){
    	String sql = " SELECT A.ODD_QTY,A.ORDER_CODE " +
    					"FROM IND_ODD A " +
    					"WHERE A.ORG_CODE='"+parm.getValue("ORG_CODE")+"' AND "+
    				"     ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'  AND " +
    				"   A.CLOSE_DATE=(SELECT MAX(B.CLOSE_DATE) FROM IND_ODD B WHERE B.ORG_CODE=A.ORG_CODE ) ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	return result ;
    }
}
