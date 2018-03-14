package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * 病历归档TOOL
 * @author wangqing 20171226
 */
public class MROFileTool extends TJDOTool {
	
	/**
     * 实例
     */
    public static MROFileTool instanceObject;
    
    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROFileTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROFileTool();
        return instanceObject;
    }
	
	/**
	 * 单个归档
	 * @param parm
	 * @return
	 */
	public TParm file(TParm parm, TConnection conn){
		String sql = "UPDATE MRO_MRV_TECH SET CHECK_FLG='3', ARCHIVE_CODE='"+parm.getValue("ARCHIVE_CODE")+"', ARCHIVE_DATE=SYSDATE WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND CHECK_FLG IN ('2', '-3')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 单个归档退回
	 * @param parm
	 * @return
	 */
	public TParm unFile(TParm parm, TConnection conn){
		String sql = "UPDATE MRO_MRV_TECH SET CHECK_FLG='-3', ARCHIVE_CODE='"+parm.getValue("ARCHIVE_CODE")+"', ARCHIVE_DATE=SYSDATE WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND CHECK_FLG IN ('3')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}

}
