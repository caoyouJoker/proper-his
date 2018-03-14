package jdo.bil;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class BILQEPatchTool extends TJDOTool{
	
	/**
     * 实例
     */
    public static BILQEPatchTool instanceObject;
    /**
     * 得到实例
     * @return BILCounteTool
     */
    public static BILQEPatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILQEPatchTool();
        return instanceObject;
    }
    
    /**
     * 查询Q医批量补票数据 
     * @param parm
     * @return
     */
    public TParm queryPatchReprint(TParm parm){
    	String cashierCode="";
    	if(parm.getValue("RECP_TYPE").equals("OPB")){
    		cashierCode="A.CASHIER_CODE,";
    	}
    	if(parm.getValue("RECP_TYPE").equals("REG")){
    		cashierCode="A.CASH_CODE CASHIER_CODE,";
    	}
    	
    	String whereSql = "";
    	String caseNo = parm.getValue("CASE_NO");
    	String mrNo = parm.getValue("MR_NO");
    	if(caseNo.length() > 0){
    		whereSql += " AND A.CASE_NO='"+caseNo+"'";
    	}
    	if(mrNo.length() > 0){
    		whereSql += " AND A.MR_NO='"+mrNo+"'";
    	}
    	String sql ="";
    	if(parm.getValue("RECP_TYPE").equals("OPB")){
    		 sql = "SELECT 'N' FLG," +
 			" CASE WHEN A.PRINT_NO IS NULL THEN 'N' ELSE 'Y' END PRINT_FLG " +
 			", A.PRINT_NO,A.RECEIPT_NO,A.BILL_DATE ," +  
 			cashierCode +
 			" A.AR_AMT, A.ADM_TYPE,A.CASE_NO,A.CONFIRM_NO,A.MR_NO,D.PAT_NAME,E.CTZ_DESC,B.INS_PAT_TYPE  " +
 			" FROM BIL_OPB_RECP A,SYS_PATINFO D,REG_PATADM B,SYS_CTZ E " +
 			" WHERE A.ORDER_NO IS NOT NULL" +
 			" AND A.RESET_RECEIPT_NO IS NULL " +
 			" AND A.MR_NO = D.MR_NO " +
 			" AND A.CASE_NO = B.CASE_NO" +
 			" AND B.CTZ1_CODE = E.CTZ_CODE" +
 			whereSql+
 			//" AND A.CASH_CODE='" + parm.getValue("CASHIER_CODE") + "'" +
 			" AND A.BILL_DATE BETWEEN TO_DATE('" + parm.getValue("START_DATE") + "','YYYYMMDDHH24MISS') " +
 			" AND TO_DATE('" + parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')" +
 			" ORDER BY A.BILL_DATE ";
    	}
    	if(parm.getValue("RECP_TYPE").equals("REG")){
    		 sql = "SELECT 'N' FLG," +
			" CASE WHEN A.PRINT_NO IS NULL THEN 'N' ELSE 'Y' END PRINT_FLG " +
			", A.PRINT_NO,A.RECEIPT_NO,A.BILL_DATE ," +
			//"A.CASH_CODE," +
			cashierCode +
			" A.AR_AMT, A.ADM_TYPE,A.CASE_NO,C.CONFIRM_NO,A.MR_NO,D.PAT_NAME,E.CTZ_DESC,B.INS_PAT_TYPE   " +
			" FROM BIL_REG_RECP A,INS_OPD C,SYS_PATINFO D" +
			" ,REG_PATADM B,SYS_CTZ E" +
			" WHERE A.ORDER_NO IS NOT NULL" +
			" AND A.CASE_NO = C.CASE_NO(+)" +
			" AND A.RESET_RECEIPT_NO IS NULL " +
			" AND C.RECP_TYPE(+)='" + parm.getValue("RECP_TYPE") + "'" +
			" AND A.MR_NO = D.MR_NO " +
			"  AND A.CASE_NO = B.CASE_NO" +
			" AND B.CTZ1_CODE = E.CTZ_CODE" +
			 whereSql+
			//" AND A.CASH_CODE='" + parm.getValue("CASHIER_CODE") + "'" +
			" AND A.BILL_DATE BETWEEN TO_DATE('" + parm.getValue("START_DATE") + "','YYYYMMDDHH24MISS') " +
			" AND TO_DATE('" + parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')" +
			" ORDER BY A.BILL_DATE ";
    	}
	
    	System.out.println("查询Q医批量补票数据 ----"+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
	

}
