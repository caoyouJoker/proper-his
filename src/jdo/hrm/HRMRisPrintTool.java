package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 检查单打印JDO
 * </p>
 *
 * <p>Description: 检查单打印JDO</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangm
 * @version 1.0
 */
public class HRMRisPrintTool extends TJDODBTool {
	/**
     * 实例
     */
    public static HRMRisPrintTool instanceObject;

    /**
     * 得到实例
     * @return HRMRisPrintTool
     */
    public static HRMRisPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new HRMRisPrintTool();
        return instanceObject;
    }
    
    
    /**
     * 保存打印状态
     * @param parm TParm
     * @param con TConnection
     * @return TParm
     */
    public TParm saveHRMRisPrintStat(TParm parm, TConnection con) {
        String sqlStr[] = (String[]) parm.getData("SQL");
        TParm result = new TParm(this.update(sqlStr, con));
        return result;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
