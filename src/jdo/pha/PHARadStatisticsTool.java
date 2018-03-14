package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;


/**
*
* <p>Title: 放射科销售统计</p>
*
* <p>Description: 放射科销售统计</p>
*
* <p>Copyright: Copyright (c)2013</p>
*
* <p>Company: JavaHis</p>
*
* @author wangm 2013.3.18
* @version 1.0
*/
public class PHARadStatisticsTool extends TJDOTool {
	/**
     * 实例
     */
    public static PHARadStatisticsTool instanceObject;
    /**
     * 得到实例
     * @return InvoiceTool
     */
    public static PHARadStatisticsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PHARadStatisticsTool();
        return instanceObject;
    }

    public PHARadStatisticsTool() {
        setModuleName("pha\\PHARadStatisticsModule.x");
        onInit();
    }
    
    //获得报表数据
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
