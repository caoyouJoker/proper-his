/**
 *
 */
package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
*
* <p>Title: 诊断码</p>
*
* <p>Description:诊断码 </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800901
* @version 1.0
*/
public class SYSDiagnosisTool extends TJDOTool{
	/**
     * 实例
     */
    public static SYSDiagnosisTool instanceObject;
    /**
     * 得到实例
     * @return SYSDiagnosisTool
     */
    public static SYSDiagnosisTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new SYSDiagnosisTool();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public SYSDiagnosisTool()
    {
        setModuleName("sys\\SYSDiagnosisModule.x");
        onInit();
    }
    /**
     * 根据疾病代码查询数据
     * @param ICD_CODE String 疾病代码,ICD_TYPE 代码类型（中医，西医）
     * @return TParm
     */
    public TParm selectdata(String ICD_CODE,String ICD_TYPE){
        TParm parm = new TParm();
        if(ICD_CODE==null||"".equals(ICD_CODE)){
        	parm.setData("ICD_TYPE",ICD_TYPE);
        	TParm result = query("selectdatawithtype",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
        }else{
        	parm.setData("ICD_CODE",ICD_CODE);
            parm.setData("ICD_TYPE",ICD_TYPE);
            TParm result = query("selectdatawithkeys",parm);
            if(result.getErrCode() < 0)
            {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;

        }

    }
    /**
     * 为初始化icd combo用
     * @param icdType
     * @return tparm
     */
    public TParm initCombo(String icdType){
    	TParm parm=new TParm();
    	if(icdType==null||icdType.length()==0){
    		return null;
    	}
    	parm.setData("ICD_TYPE",icdType);
    	TParm result=query("initcombo",parm);
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据ICD_CODE查询诊断信息
     * @param ICD_CODE String
     * @return TParm
     */
    public TParm selectDataWithCode(String ICD_CODE){
        TParm parm = new TParm();
        parm.setData("ICD_CODE",ICD_CODE);
        TParm result = this.query("selectdatawithcode",parm);
        if(result.getErrCode() < 0){
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
