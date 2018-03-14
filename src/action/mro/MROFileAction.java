package action.mro;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MROFileTool;
/**
 * 病历归档ACTION
 * @author wangqing 20171226
 */
public class MROFileAction extends TAction {

	/**
	 * 批量归档
	 * @param parm
	 * @return
	 */
	public TParm file(TParm parm){
		TConnection conn = this.getConnection();
		TParm temp = new TParm();
		TParm result = new TParm();
		for(int i=0; i<parm.getCount(); i++){
			temp = parm.getRow(i);
			result = MROFileTool.getInstance().file(temp, conn);
			// 判断错误值
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// 判断是否更新成功
			if(result.getValue("RETURN")!=null && result.getValue("RETURN").equals("0")){
				conn.rollback();
				conn.close();
				result.setErrCode(-1);
				result.setErrText("并发操作错误");
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 批量归档退回
	 * @param parm
	 * @return
	 */
	public TParm unFile(TParm parm){
		TConnection conn = this.getConnection();
		TParm temp = new TParm();
		TParm result = new TParm();
		for(int i=0; i<parm.getCount(); i++){
			temp = parm.getRow(i);
			result = MROFileTool.getInstance().unFile(temp, conn);
			// 判断错误值
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// 判断是否更新成功
			if(result.getValue("RETURN")!=null && result.getValue("RETURN").equals("0")){
				conn.rollback();
				conn.close();
				result.setErrCode(-1);
				result.setErrText("并发操作错误");
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	
}
