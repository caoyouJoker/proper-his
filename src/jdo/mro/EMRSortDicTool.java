package jdo.mro;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class EMRSortDicTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static EMRSortDicTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static EMRSortDicTool getInstance() { 
		if (instanceObject == null)
			instanceObject = new EMRSortDicTool();
		return instanceObject;
	}
	public EMRSortDicTool()
    {
        setModuleName("mro\\EMRSortDicModule.x");
        onInit();
    }
	/**
	 * 查询方法
	 * @param parm
	 * @return
	 */
	public TParm onQuery(TParm parm){
		TParm result = this.query("onSelectEMRSortDic", parm); 
		return result;
	}
	/**
	 * 删除方法
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDelete(TParm parm, TConnection conn){
		TParm result = new TParm();
//		result = this.update("deleteEmrSortDic", parm, conn);
//        if (result.getErrCode() < 0) {
//            err(result.getErrName() + " " + result.getErrText());
//            return result;
//        }
//
//        result = this.update("deleteEmrSortDicDetail", parm, conn);
//        if (result.getErrCode() < 0) {
//            err(result.getErrName() + " " + result.getErrText());
//            return result;
//        } 
	    result = new TParm(TJDODBTool.getInstance().update(parm.getValue("sqlDetail")));
        if (result.getErrCode() < 0) {
    	  err(result.getErrName() + " " + result.getErrText());
    	  return result;
  	    }
        result = new TParm(TJDODBTool.getInstance().update(parm.getValue("sqlSortDic")));
        if (result.getErrCode() < 0) {
      	  err(result.getErrName() + " " + result.getErrText());
      	  return result;
    	}
		return result;
	}
	/**
	 * 保存emrsortdic
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onSaveEmrSortDic(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = this.update("onSaveEmrSortDic", parm, conn);
		if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
		return result;
	}
	/**
	 * 保存emrsortdicdetail
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onSaveEmrSortDicDetail(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = this.update("onSaveEmrSortDicDetail", parm, conn);
		if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
		return result;
	}
	
	
	/**
	 * 更新emrsortdic
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateEmrSortDic(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = this.update("onUpdateEmrSortDic", parm, conn);
		if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
		return result;
	}
	/**
	 * 更新emrsortdicdetail
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateEmrSortDicDetail(TParm parm, TConnection conn){
		TParm result = new TParm();
		result = this.update("onUpdateEmrSortDicDetail", parm, conn);
		if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
		return result;
	}
}
