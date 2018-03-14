package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * 泵入方式字典工具类
 * @author wukai on 20160526
 *
 */
public class SYSPumpTypeTool extends TJDOTool{
	
	private static SYSPumpTypeTool mInstance;
	
	public static SYSPumpTypeTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new SYSPumpTypeTool();
		}
		return mInstance;
	}
	
	public SYSPumpTypeTool(){
		this.setModuleName("sys\\SYSPumpTypeModule.x");
		super.onInit();
	}
	
	public TParm onQuery(TParm parm) {
		parm = this.query("query", parm);
		return parm;
	}
	
	public boolean onUpdate(TParm parm) {
		if(this.update("update", parm).getErrCode() < 0) {
			return false;
		} 
		return true;
	}
	
	public boolean onDelete(TParm parm) {
		if(this.update("delete", parm).getErrCode() <0) {
			return false;
		}
		return true;
	}
	
	public boolean onSave(TParm parm) {
		if(this.update("save",parm).getErrCode() < 0) {
			return false;
		}
		return true;
	}
}
