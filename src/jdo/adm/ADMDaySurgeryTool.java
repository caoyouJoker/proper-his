package jdo.adm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:日间手术变更</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhanglei
 * @version 5.0
 */
public class ADMDaySurgeryTool extends TJDOTool {

    /**
     * 实例
     */
    public static ADMDaySurgeryTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMDaySurgeryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMDaySurgeryTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMDaySurgeryTool() {
        setModuleName("adm\\ADMDaySurgeryModule.x");
        onInit();
    }
    
    /**
     * insert ADM_INP
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm update(TParm parm, TConnection connection) {
        TParm result = new TParm();
          //更新ADM_INP中DAY_OPE_FLG字段
        String sqlADM_INP = "update ADM_INP set DAY_OPE_FLG = '" + parm.getValue("DayOpeFlg") + "' where MR_NO ='" + 
        parm.getValue("MR_NO") + "' AND CASE_NO = '" + parm.getValue("CASE_NO") + "'";
        System.out.println("sqlADM_INP:" + sqlADM_INP);
        
        result = new TParm(TJDODBTool.getInstance().update(sqlADM_INP, connection));
        System.out.println("执行完更新语句");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        
        //更新ADM_RESV中DAY_OPE_FLG字段
        String sqlADM_RESV = "update ADM_RESV set DAY_OPE_FLG = '" + parm.getValue("DayOpeFlg") + "' where MR_NO ='" + 
        parm.getValue("MR_NO") + "' AND IN_CASE_NO = '" + parm.getValue("CASE_NO") + "'";
        System.out.println("sqlADM_RESV:" + sqlADM_RESV);     
        result = new TParm(TJDODBTool.getInstance().update(sqlADM_RESV, connection));    
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        
        	//更新MRO_RECORD中DAY_OPE_FLG字段
        	String sqlMRO_RECORD = "update MRO_RECORD set DAY_OPE_FLG = '" + parm.getValue("DayOpeFlg") + "' where MR_NO ='" + 
        	parm.getValue("MR_NO") + "' AND CASE_NO = '" + parm.getValue("CASE_NO") + "'";
        	System.out.println("sqlMRO_RECORD:" + sqlMRO_RECORD);
      
            result = new TParm(TJDODBTool.getInstance().update(sqlMRO_RECORD, connection));
        	System.out.println("执行完更新语句1");
        	if (result.getErrCode() < 0) {
        		err("ERR:" + result.getErrCode() + result.getErrText() +
        				result.getErrName());
        		return result;
        	}
        
        
        return result;
    }

	


	
}
