package jdo.bil;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class BILCCBPatchTool extends TJDOTool{
	/**
     * 实例
     */
    public static BILCCBPatchTool instanceObject;
    /**
     * 得到实例
     * @return BILCounteTool
     */
    public static BILCCBPatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILCCBPatchTool();
        return instanceObject;
    }
    /**
     * 查询建行批量补票数据
     * @param parm
     * @return
     */
    public TParm queryPatchReprint(TParm parm){
    	String sql = 
    		" SELECT 'N' FLG, A.INV_NO PRINT_NO, A.RECEIPT_NO, A.CASHIER_CODE, A.AR_AMT, A.PRINT_USER," +
    		" A.PRINT_DATE,A.ADM_TYPE,B.CASE_NO,C.CONFIRM_NO,D.PAT_NAME,B.REGION_CODE,B.MR_NO" +
    		" FROM BIL_INVRCP A, BIL_" + parm.getValue("RECP_TYPE") +"_RECP B,INS_OPD C,SYS_PATINFO D" +
    		" WHERE A.PRINT_USER = '" + parm.getValue("CASHIER_CODE") + "' AND A.RECP_TYPE = '" + parm.getValue("RECP_TYPE") + "' AND A.CANCEL_FLG = 0" +
    		" AND A.PRINT_DATE BETWEEN TO_DATE('" + parm.getValue("START_DATE") + "','YYYYMMDDHH24MISS') AND TO_DATE('" + parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')" +
    		" AND A.ADM_TYPE = B.ADM_TYPE AND A.INV_NO = B.PRINT_NO" +
    		" AND B.CASE_NO = C.CASE_NO (+) AND B.PRINT_NO = C.INV_NO(+)" +
    		" AND B.MR_NO = D.MR_NO " +
    		" AND LENGTH(A.INV_NO) = 12" +
    		" ORDER BY A.INV_NO";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }

}
