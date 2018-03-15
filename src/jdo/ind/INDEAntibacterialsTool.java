package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;


/**
 * 
 * <p>
 * Title: Ժ�ڼ��￹��ҩ����ϸͳ��
 * </p>
 * 
 * <p>
 * Description: Ժ�ڼ��￹��ҩ����ϸͳ��
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
public class INDEAntibacterialsTool extends TJDOTool{

	/**
     * ʵ��
     */
    public static INDEAntibacterialsTool instanceObject;
    /**
     * �õ�ʵ��
     * @return InvoiceTool
     */
    public static INDEAntibacterialsTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INDEAntibacterialsTool();
        return instanceObject;
    }

    public INDEAntibacterialsTool() {
        setModuleName("ind\\INDEAntibacterialsModule.x");
        onInit();
    }
    
    //��ñ�������
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