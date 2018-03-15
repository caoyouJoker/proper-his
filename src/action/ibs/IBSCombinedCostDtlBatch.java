package action.ibs;

import java.sql.Timestamp;
import java.util.Date;

//import jdo.adm.ADMAutoBillTool;
//import jdo.sys.Operator;
//import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
//import com.dongyang.db.TConnection;
//import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 费用明细合计同步Patch住院</p>
 *
 * <p>Description: 费用明细合计同步Patch住院</p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author zhangs 2014.09.09
 * @version 1.0
 */
public class IBSCombinedCostDtlBatch extends Patch {
    public IBSCombinedCostDtlBatch() {
    }
    /**
     * 批次线程
     * @return boolean
     */
    public boolean run() {
    	TParm result = patch_I(null);
    	if (result.getErrCode() < 0) {
            return false;
        }
        return true;
    } 
    public TParm patch_I(TParm parmDate){
        String stratDate;
        String endDate;
//        System.out.println("parmDate:"+parmDate);
        if(parmDate==null){
         Timestamp date = StringTool.getTimestamp(new Date());
         stratDate = StringTool.rollDate(date, -1).toString().substring(0, 10).replace("-", "") + "000000";
         endDate = StringTool.rollDate(date, -1).toString().substring(0, 10).replace("-", "") + "235959";
        }else{
        	 stratDate = parmDate.getValue("DATE",0) + "000000";
             endDate = parmDate.getValue("DATE",0) + "235959";
        }
        String sql =
        	" UPDATE IBS_ORDD A SET "+
        	" A.TOT_AMT2=(SELECT ROUND(A.DOSAGE_QTY*B.OWN_PRICE3,2) FROM SYS_FEE B WHERE B.ORDER_CODE=A.ORDER_CODE) "+
        	" WHERE TO_CHAR(A.BILL_DATE,'YYYYMMDDHH24MISS') BETWEEN '"+stratDate+"' AND '"+endDate+"' ";
//		System.out.println("patch_I:"+sql);
        
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
       
        return result;
    }
   
}
