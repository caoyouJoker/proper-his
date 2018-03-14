package jdo.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.StringUtils;
/**
 * <p>
 * Title:三管ICU统计报表Tool
 * </p>
 * 
 * <p>
 * Description:三管ICU统计报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2017-3-27
 * @version JavaHis 1.0
 */
public class INFICUThreePipeTool extends TJDOTool {
	 /**
     * 构造器
     */
    public INFICUThreePipeTool() {
        onInit();
    }

    /**
     * 实例
     */
    private static INFICUThreePipeTool instanceObject;

    /**
     * 得到实例
     * @return INFCaseTool
     */
    public static INFICUThreePipeTool getInstance() {
        if (instanceObject == null) instanceObject = new INFICUThreePipeTool();
        return instanceObject;
    }
    
    public TParm getICUThreePipe(TParm parm) {
    	
    	String startDate = parm.getValue("START_DATE");
    	
    	String endDate = parm.getValue("END_DATE");
    	
    	String mrno = parm.getValue("MR_NO");
    	
    	String dept = parm.getValue("DEPT_CODE");
    	
    	if(!StringUtils.isEmpty(mrno)) {
    		mrno = " AND  B.MR_NO = '" + mrno  + "'";
    	}
    	
    	if(!StringUtils.isEmpty(dept)) {
    		dept = " AND  E.DEPT_CODE = '" + dept  + "'";
    	}
    	
    	String sql = 
    		"SELECT "  
    	         + "B1.MR_NO, "
    	         + "D.PAT_NAME, "
    	         + "D.SEX_CODE, "
    	         + "D.BIRTH_DATE, "
    	         + "E.IN_DATE, "
    	         + "B1.CASE_NO, "
    	         + "B1.IN_OP_TIME, "
    	         
    	         + "(SELECT MAX(B2.START_TIME) FROM OPE_EVENT B2 WHERE B1.OPE_BOOK_NO = B2.OPE_BOOK_NO AND B2.EVENT_ID = '动脉插管') AS IN_TUBAIR_TIME, "
    	         + "'' AS IN_TUBURETER_TIME, "
    	         + "(SELECT MAX(B3.START_TIME) FROM OPE_EVENT B3 WHERE B1.OPE_BOOK_NO = B3.OPE_BOOK_NO AND B3.EVENT_ID = '麻醉开始') AS IN_TUBVEIN_TIME, "
    	        
    	          //入ICU时间
    	         + " MAX (C1.TRANSFER_DATE) AS IN_ICU_TIME, "
    	        
    	         + "'' AS ACC_TUBAIR_TIME, "
    	         + "'' AS ACC_TUBURETER_TIME, "
    	         + "'' AS ACC_TUBVEIN_TIME, "
    	         + "'' AS SIN_TUBAIR_TIME, "
    	        
    	        
    	         + "(SELECT MAX(D1.DC_DATE) FROM ODI_ORDER D1 WHERE D1.ORDER_CODE = '108011' AND D1.CASE_NO = B1.CASE_NO) AS OUT_TUBAIR_TIME, "
    	         + "(SELECT MAX(D2.DC_DATE) FROM ODI_ORDER D2 WHERE D2.ORDER_CODE = '108006' AND D2.CASE_NO = B1.CASE_NO) AS OUT_TUBURETER_TIME, "
    	         + "(SELECT MAX(D3.DC_DATE) FROM ODI_ORDER D3 WHERE D3.ORDER_CODE = '108008' AND D3.CASE_NO = B1.CASE_NO) AS OUT_TUBVEIN_TIME, "

    	          //出ICU时间
    	         + " MAX (C2.TRANSFER_DATE) AS OUT_ICU_TIME, "
    	        
    	        
    	         + "'' AS CA_TUBAIR_TIME, '' AS CA_TUBURETER_TIME, '' AS CA_TUBVEIN_TIME, "
    	         + "'' AS PULL_TUBAIR_TIME, '' AS PULL_TUBURETER_TIME, '' AS PULL_TUBVEIN_TIME, "
    	        
    	         + "'' AS IS_MAC_INFECT, '' AS IS_YSNL_INFECT, '' AS IS_DBGNL_INFECT, '' AS IS_DBGWZZ_INFECT, "
    	         + "'' AS IS_XLGRLC_INFECT, '' AS IS_XLGRSY_INFECT, '' AS IS_XFY_INFECT "
    	         
    	   + " FROM ( SELECT * FROM "
    	   + "				( SELECT B.OPE_BOOK_NO, B.MR_NO, B.CASE_NO, B.START_TIME AS IN_OP_TIME, ROW_NUMBER () OVER (PARTITION BY B.CASE_NO ORDER BY B.START_TIME DESC)  ROWNUMBER"
    	   + "                 FROM OPE_EVENT B "
    	   + "                 WHERE     B.EVENT_ID = '进手术间' "
    	   + "                       AND B.START_TIME BETWEEN TO_DATE ('" + startDate + "', 'YYYYMMDDHH24MISS') "
    	   + "                                     AND TO_DATE ('" + endDate + "', 'YYYYMMDDHH24MISS') "
    	   +                         mrno
    	   + "               ) WHERE   ROWNUMBER = '1' "
    	   + "       ) B1 "
    	   + " LEFT JOIN INW_TRANSFERSHEET C1 ON B1.CASE_NO = C1.CASE_NO  AND C1.TRANSFER_CLASS = 'OI'AND C1.STATUS_FLG = '5' "
    	   + " LEFT JOIN INW_TRANSFERSHEET C2 ON B1.CASE_NO = C2.CASE_NO  AND C2.TRANSFER_CLASS = 'IW'AND C2.STATUS_FLG = '5' "
    	   + ", SYS_PATINFO D, ADM_INP E "
    	  
    	   
    	   
    	   + " WHERE D.MR_NO = B1.MR_NO  AND E.MR_NO = B1.MR_NO AND E.CASE_NO = B1.CASE_NO "
           +         dept
    	   + " GROUP BY B1.MR_NO, B1.CASE_NO, B1.IN_OP_TIME,B1.OPE_BOOK_NO, D.PAT_NAME, D.SEX_CODE, D.BIRTH_DATE, E.IN_DATE "
    	   + " ORDER BY B1.MR_NO ";
    	
    	
    	System.out.println("INFICUThreePipeTool.getICUThreePipe() sql>>>>>>>> " + sql);
    	TParm result = new TParm( TJDODBTool.getInstance().select(sql) );
    	return result;
    	
    }
    
    
    
    
    
}
