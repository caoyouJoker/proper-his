package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: 病案封存 
 *
 * <p>Company: bluecore</p>
 *
 * @author  huangtt 20161110 
 * @version 4.0
 */
public class MROSealedTool extends TJDOTool{
	
	   /**
     * 实例
     */
    public static MROSealedTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROSealedTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROSealedTool();
        return instanceObject;
    }

    public MROSealedTool() {
        this.setModuleName("mro\\MROSealedModule.x");
        this.onInit();
    }
    
    public TParm updateEmrFileIndex(TParm parm, TConnection conn ){
    	TParm result = this.update("updateEmrFileIndex", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm updateMroMreTech(TParm parm, TConnection conn ){
    	TParm result = this.update("updateMroMreTech", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm updateMroMreTechSealedPrint(TParm parm, TConnection conn ){
    	TParm result = this.update("updateMroMreTechSealedPrint", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm updateMroMreTechSealedProblem(TParm parm, TConnection conn ){
    	TParm result = this.update("updateMroMreTechSealedProblem", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
	

}
