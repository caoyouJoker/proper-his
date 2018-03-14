package jdo.opb;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;


/**
*
* <p>Title: 门（急）诊明细、汇总报表</p>
*
* <p>Description: 门（急）诊明细、汇总报表</p>
*
* <p>Copyright: Copyright (c)2013</p>
*
* <p>Company: JavaHis</p>
*
* @author wangm 2013.3.14
* @version 1.0
*/
public class OPBStatisticsTool extends TJDOTool {

	/**
     * 实例
     */
    public static OPBStatisticsTool instanceObject;
    /**
     * 得到实例
     * @return InvoiceTool
     */
    public static OPBStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBStatisticsTool();
        return instanceObject;
    }

    public OPBStatisticsTool() {
        setModuleName("opb\\OPBStatisticsModule.x");
        onInit();
    }
	
	public TParm selectReportData(TParm parm){
		TParm result = this.query(parm.getValue("REPORTFLG"),parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
		return result; 
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
	
	
}
