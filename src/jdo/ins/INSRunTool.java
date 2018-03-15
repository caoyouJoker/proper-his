package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 医保执行操作时在途状态管控
 * </p>
 * 
 * <p>
 * Description: 医保执行操作时在途状态管控
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 2011-12-29
 * @version 1.0
 */
public class INSRunTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INSRunTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSOpdApproveTool
	 */
	public static INSRunTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSRunTool();
		return instanceObject;
	}
	/**
	 * 构造器
	 */
	public INSRunTool() {
		setModuleName("ins\\INSRunModule.x");
        onInit();
	}
	/**
	 * 查询在途状态 0.在途 1.成功
	 * @param parm
	 * @return
	 */
	public TParm queryInsRun(TParm parm){
		TParm result = query("queryInsRun", parm);
		return result;
	}
	/**
	 * 修改在途状态 0.在途 1.成功
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateInsRun(TParm parm){
		TParm result = update("updateInsRun", parm);
		return result;
	}
	/**
	 * 添加在途状态 0.在途 1.成功
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertInsRun(TParm parm){ 
		TParm result = update("insertInsRun", parm);
		return result;
	}
	/**
	 * 添加在途状态 0.在途 1.成功
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertInsRun(TParm parm, TConnection connection){
		TParm result = update("insertInsRun", parm,connection);
		return result;
	}
	/**
	 * 删除
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteInsRun(TParm parm, TConnection connection){
		TParm result = update("deleteInsRun", parm,connection);
		return result;
	}
	/**
	 * 删除
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteInsRun(TParm parm){
		TParm result = update("deleteInsRun", parm);
		return result;
	}
	/**
	 * 删除
	 * =========pangben 2013-7-30
	 */
	public TParm deleteInsRunConcel(TParm parm){
		TParm result = update("deleteInsRunConcel", parm);
		return result;
	}
	/**
	 * 医保在途参数操作
	 * @param runParm
	 */
	public boolean runTempParm(TParm runParm){
		TParm parm = new TParm();
		parm.setData("CASE_NO", runParm.getValue("CASE_NO"));
		parm.setData("EXE_USER", runParm.getValue("OPT_USER"));
		parm.setData("EXE_TERM", runParm.getValue("OPT_TERM"));
		parm.setData("EXE_TYPE", runParm.getValue("EXE_TYPE"));
		parm.setData("STUTS", "1");
		runParm = INSRunTool.getInstance().queryInsRun(parm);
		if (runParm.getErrCode() < 0) {
			return false;
		}
		for (int i = 0; i < runParm.getCount(); i++) {
			if (runParm.getInt("STUTS", i) == 1) {
				return false;
			}
		}
		return true;
	}
	
}
