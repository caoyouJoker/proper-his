package action.mro;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MROFileTool;
/**
 * �����鵵ACTION
 * @author wangqing 20171226
 */
public class MROFileAction extends TAction {

	/**
	 * �����鵵
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
			// �жϴ���ֵ
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// �ж��Ƿ���³ɹ�
			if(result.getValue("RETURN")!=null && result.getValue("RETURN").equals("0")){
				conn.rollback();
				conn.close();
				result.setErrCode(-1);
				result.setErrText("������������");
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * �����鵵�˻�
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
			// �жϴ���ֵ
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// �ж��Ƿ���³ɹ�
			if(result.getValue("RETURN")!=null && result.getValue("RETURN").equals("0")){
				conn.rollback();
				conn.close();
				result.setErrCode(-1);
				result.setErrText("������������");
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	
}
