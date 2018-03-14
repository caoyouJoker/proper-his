package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class REGAutoBatchPatchTool extends TJDOTool{
	/**
     * 实例
     */
    public static REGAutoBatchPatchTool instanceObject;
    /**
     * 得到实例
     * @return REGArvTimeTool
     */
    public static REGAutoBatchPatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGAutoBatchPatchTool();
        return instanceObject;
    }
    

    /**
     * 构造器
     */
    public REGAutoBatchPatchTool() {
        setModuleName("reg\\REGAutoBatchPatchModule.x");
        onInit();
    }
    
    /**
     * 更改爽约次数 
     * */
    public TParm updMissCount(TParm parm,TConnection connection){
	  TParm result = new TParm();
	  result = update("updMissCount", parm,connection);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
   }
    /**
     * 置为黑名单
     * */
    public TParm updBlackFlg(TParm parm,TConnection connection){
      TParm result = new TParm();
   	  result = update("updBlackFlg", parm,connection);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
    /**
     * 清除黑名单
     * */
    public TParm removeBlackFlg(TParm parm,TConnection connection){
      TParm result = new TParm();
   	  result = update("removeBlackFlg", parm,connection);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText() +
                 result.getErrName());
             return result;
         }
         return result;
    }
}
