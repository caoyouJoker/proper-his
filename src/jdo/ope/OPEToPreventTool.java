package jdo.ope;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 手术预防使用率统计</p>
 *
 * <p>Description:手术预防使用率统计</p>
 *
 * <p>Copyright: Copyright (c)cao yong 2013</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2013.10.28
 * @version 1.0
 */
public class OPEToPreventTool extends TJDOTool {
	public static OPEToPreventTool instanceObject;
	
	public static OPEToPreventTool getInstance(){
	if(instanceObject==null){
		instanceObject=new OPEToPreventTool();
	}
	   return instanceObject;
	}
	public OPEToPreventTool(){
		setModuleName("ope\\OPEToPreventModule.x");
        onInit();
	}
	
	/**
	 * 查询住院患者
	 * @param parm
	 * @return
	 */
	public TParm selectdata(TParm parm) {
		TParm result = new TParm();
		result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 联合用药的病人
	 * @param parm
	 * @return
	 */
	public TParm selectdataM(TParm parm) {
		TParm result = new TParm();
		result = query("selectdataM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
