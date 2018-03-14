package jdo.reg;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class REGAutoBatchPatchTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static REGAutoBatchPatchTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGArvTimeTool
     */
    public static REGAutoBatchPatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGAutoBatchPatchTool();
        return instanceObject;
    }
    

    /**
     * ������
     */
    public REGAutoBatchPatchTool() {
        setModuleName("reg\\REGAutoBatchPatchModule.x");
        onInit();
    }
    
    /**
     * ����ˬԼ���� 
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
     * ��Ϊ������
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
     * ���������
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
