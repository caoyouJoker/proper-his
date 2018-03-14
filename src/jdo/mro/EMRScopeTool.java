package jdo.mro;



import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class EMRScopeTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static EMRScopeTool instanceObject;

	/**
	 * �õ�ʵ��
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
	    * ��������������
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
     * ɾ������
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
     * ��������
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
