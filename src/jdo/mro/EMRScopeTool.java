package jdo.mro;



import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class EMRScopeTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static EMRScopeTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static EMRScopeTool getInstance() { 
		if (instanceObject == null)
			instanceObject = new EMRScopeTool();
		return instanceObject;
	}
	public EMRScopeTool()
    {
        setModuleName("mro\\EMRScopeModule.x");
        onInit();
    }
	public TParm onQuery(TParm parm){
		TParm result = this.query("onSelectEMRScope", parm); 
		return result;
	}
	/**
	    * 插入新数据数据
	    * @return 
	    */
    public boolean onSave(TParm parm) {
       TParm result = this.update("insertData",parm);
       if(result.getErrCode() < 0){
           err(result.getErrCode() + " " + result.getErrText());
           return false;
       }
       return true;
    }
    /**
     * 删除数据
     */
    public boolean onDelete(TParm parm){
 	   TParm result = this.update("deleteData",parm);
 	   if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText());
            return false;
        } 
 	   return true;
    }
    /**
     * 更改数据
     */
    public boolean onUpdata(TParm parm){
 	   TParm result = this.update("updateData", parm);
 	   if (result.getErrCode() < 0) {
 		   err("ERR:" + result.getErrCode() + result.getErrText());
            return false;
        }
 	   return true; 
    }
    
}
