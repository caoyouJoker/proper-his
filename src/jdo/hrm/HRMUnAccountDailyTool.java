package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>Title: 健检对账查询</p>
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
public class HRMUnAccountDailyTool   extends TJDOTool {

    /**
     * 实例
     */
    private static HRMUnAccountDailyTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static HRMUnAccountDailyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new HRMUnAccountDailyTool();
        return instanceObject;
    }
    
    public TParm onQuery(TParm parm){
    	
    	String startDate = parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
    	String regionCode = parm.getValue("REGION_CODE");
    	String cashierCode =  parm.getValue("CASHIER_CODE");
    	String admType = parm.getValue("ADM_TYPE") ;
    	String sql =" SELECT B.USER_NAME, "+
				    "   A.MR_NO, "+
    				"   A.PRINT_NO, "+
			    	"   A.AR_AMT, "+
			    	"   A.PAY_CASH, "+
			    	"   A.PAY_BANK_CARD, "+
			    	"   A.PAY_CHECK, "+
			    	"   A.PAY_MEDICAL_CARD, "+
			    	"   A.PAY_INS_CARD, "+
			    	"   A.PAY_OTHER1 AS OTHER_FEE1, "+
			    	"   A.PAY_OTHER2, "+
			    	"   A.PAY_DEBIT, "+
			    	"   A.PAY_DRAFT, "+
			    	"   A.PRINT_DATE, "+
			    	"   A.ACCOUNT_SEQ, "+
			    	"   A.ACCOUNT_DATE, "+
			    	"   A.ACCOUNT_USER "+
			    	" FROM BIL_OPB_RECP A ,SYS_OPERATOR B  "+
			    	" WHERE   A.CASHIER_CODE = B.USER_ID   "+
			    	"   AND   A.REGION_CODE = '"+regionCode+"' ";
    	if(cashierCode != null && !cashierCode.equals("")) {
    		sql += "   AND A.CASHIER_CODE = '"+cashierCode+"' ";
    	}
			    	
			  sql +="   AND A.ADM_TYPE = '"+admType+"' "+
			    	"   AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS') "+
			    	"           AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS') "+
			    	" 	ORDER BY A.BILL_DATE DESC";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    

}
