package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class EMRRusecopeTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static EMRRusecopeTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static EMRRusecopeTool getInstance() { 
		if (instanceObject == null)
			instanceObject = new EMRRusecopeTool();
		return instanceObject;
	}
	public EMRRusecopeTool()
    {
        setModuleName("mro\\EMRRusecopeModule.x");
        onInit();
    }
	public TParm onQuery(TParm parm){
		TParm result = this.query("onSelectEMRRusecope", parm); 
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
     * ɾ������
     */
    public boolean onDelete1(TParm parm){
 	   TParm result = this.update("deleteDataEMRSCOPE",parm);
 	   if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText());
            return false;
        } 
 	   return true;
    }
    
    /**
     * ��������������
     * @return 
     */
 	 public boolean onSaveEmrRuleAuth(TParm parm) {
 	    TParm result = this.update("insertDataEmr",parm);
 	    if(result.getErrCode() < 0){
 	        err(result.getErrCode() + " " + result.getErrText());
 	        return false;
 	    }
 	    return true;
 	 }
 	public TParm onQueryEmr(TParm parm){
		TParm result = this.query("onSelectEMRRule", parm); 
		return result;
	}
 	/**
     * ɾ������
     */
    public boolean onDeleteEMR(TParm parm){
 	   TParm result = this.update("deleteDataEMR",parm);
 	   if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText());
            return false;
        } 
 	   return true;
    }
}
