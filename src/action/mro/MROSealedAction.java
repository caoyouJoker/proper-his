package action.mro;

import jdo.mro.MROSealedTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: ²¡°¸·â´æ 
 *
 * <p>Company: ProperSoft</p>
 *
 * @author  huangtt 20161110 
 * @version 4.0
 */
public class MROSealedAction extends TAction{
	
	
	/**
	 * ²¡Àú·â´æ ±£´æ
	 * @param parm
	 * @return
	 */
	public TParm updateMroSealed(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		
		TParm emrParm = parm.getParm("emrParm");
		for (int i = 0; i < emrParm.getCount("CASE_NO"); i++) {
			result = MROSealedTool.getInstance().updateEmrFileIndex(emrParm.getRow(i), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			
		}
		
		result = MROSealedTool.getInstance().updateMroMreTech(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		
		connection.commit();
		connection.close();
		return result;
		
		
	}
	
	
	public TParm updateMroMreTechSealedPrint(TParm parm){
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = MROSealedTool.getInstance().updateMroMreTechSealedPrint(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		
		connection.commit();
		connection.close();
		return result;
	}
	
	
	public TParm updateMroMreTechSealedProblem(TParm parm){
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = MROSealedTool.getInstance().updateMroMreTechSealedProblem(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		
		connection.commit();
		connection.close();
		return result;
	}

}
