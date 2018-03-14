package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCMaterialLocTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCMaterialLocTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCMaterialLocTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCMaterialLocTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCMaterialLocTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQueryIndStockM(TParm parm){
    	String locCode = parm.getValue("ELETAG_CODE");
    	if(StringUtil.isNullString(locCode)){
    		return new TParm();
    	}
    	String sql = " SELECT A.ORDER_CODE,B.ORDER_DESC,A.MATERIAL_LOC_CODE  " +
    			     " FROM IND_STOCKM A,PHA_BASE B " +
    			     " WHERE A.ELETAG_CODE='"+locCode+"' AND A.ORDER_CODE=B.ORDER_CODE " ;
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    	
    }
    
    public TParm onQueryIndStockEleTag(TParm parm){
    	
    	String sql = " SELECT A.ORDER_CODE,"+
					 "      A.STOCK_QTY,"+
					 "         FLOOR (A.STOCK_QTY / D.DOSAGE_QTY / D.STOCK_QTY) "+
					 "      || E.UNIT_CHN_DESC "+
					 "      || MOD (A.STOCK_QTY, D.DOSAGE_QTY / D.STOCK_QTY) "+
					 "      || F.UNIT_CHN_DESC QTY ,"+
					 "      C.SPECIFICATION "+
					 " FROM (  SELECT A.ORDER_CODE, SUM (A.STOCK_QTY) AS STOCK_QTY "+
					 "           FROM IND_STOCK A "+
					 "          WHERE A.ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"' AND A.ORG_CODE = '"+parm.getValue("ORG_CODE")+"' "+ 
					 "       GROUP BY ORDER_CODE) A,"+
					 "      PHA_BASE C,"+
					 "      PHA_TRANSUNIT D,"+
					 "      SYS_UNIT E,"+
					 "      SYS_UNIT F "+
					 " WHERE     A.ORDER_CODE = C.ORDER_CODE "+
					 "      AND A.ORDER_CODE = D.ORDER_CODE "+
					 "      AND C.STOCK_UNIT = E.UNIT_CODE(+) "+
					 "      AND C.DOSAGE_UNIT = F.UNIT_CODE(+)";
    	 
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    public TParm onQuerySPCStockEleTag(TParm parm){//wanglong add 20150202
        
        String sql = " SELECT A.ORDER_CODE,"+
                     "      A.STOCK_QTY,"+
                     "         FLOOR (A.STOCK_QTY / D.DOSAGE_QTY / D.STOCK_QTY) "+
                     "      || E.UNIT_CHN_DESC "+
                     "      || MOD (A.STOCK_QTY, D.DOSAGE_QTY / D.STOCK_QTY) "+
                     "      || F.UNIT_CHN_DESC QTY ,"+
                     "      C.SPECIFICATION "+
                     " FROM (  SELECT A.ORDER_CODE, SUM (A.STOCK_QTY) AS STOCK_QTY "+
                     "           FROM SPC_STOCK A "+
                     "          WHERE A.ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"' AND A.ORG_CODE = '"+parm.getValue("ORG_CODE")+"' "+ 
                     "       GROUP BY ORDER_CODE) A,"+
                     "      PHA_BASE C,"+
                     "      PHA_TRANSUNIT D,"+
                     "      SYS_UNIT E,"+
                     "      SYS_UNIT F "+
                     " WHERE     A.ORDER_CODE = C.ORDER_CODE "+
                     "      AND A.ORDER_CODE = D.ORDER_CODE "+
                     "      AND C.STOCK_UNIT = E.UNIT_CODE(+) "+
                     "      AND C.DOSAGE_UNIT = F.UNIT_CODE(+)";
         
        
        return new TParm(TJDODBTool.getInstance().select(sql));
    }  
}
