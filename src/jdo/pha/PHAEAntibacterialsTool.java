package jdo.pha;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;


/**
 * 
 * <p>
 * Title: 院内急诊抗菌药物明细统计
 * </p>
 * 
 * <p>
 * Description: 院内急诊抗菌药物明细统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangm 2013.3.18
 * @version 1.0
 */
public class PHAEAntibacterialsTool extends TJDOTool{

	/**
     * 实例
     */
    public static PHAEAntibacterialsTool instanceObject;
    /**
     * 得到实例
     * @return InvoiceTool
     */
    public static PHAEAntibacterialsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PHAEAntibacterialsTool();
        return instanceObject;
    }

    public PHAEAntibacterialsTool() {
        setModuleName("pha\\PHAEAntibacterialsModule.x");
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
