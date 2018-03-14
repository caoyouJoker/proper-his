package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * �����鵵TOOL
 * @author wangqing 20171226
 */
public class MROFileTool extends TJDOTool {
	
	/**
     * ʵ��
     */
    public static MROFileTool instanceObject;
    
    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static MROFileTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROFileTool();
        return instanceObject;
    }
	
	/**
	 * �����鵵
	 * @param parm
	 * @return
	 */
	public TParm file(TParm parm, TConnection conn){
		String sql = "UPDATE MRO_MRV_TECH SET CHECK_FLG='3', ARCHIVE_CODE='"+parm.getValue("ARCHIVE_CODE")+"', ARCHIVE_DATE=SYSDATE WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND CHECK_FLG IN ('2', '-3')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * �����鵵�˻�
	 * @param parm
	 * @return
	 */
	public TParm unFile(TParm parm, TConnection conn){
		String sql = "UPDATE MRO_MRV_TECH SET CHECK_FLG='-3', ARCHIVE_CODE='"+parm.getValue("ARCHIVE_CODE")+"', ARCHIVE_DATE=SYSDATE WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND CHECK_FLG IN ('3')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}

}
