package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INDOpmedJDOTool extends TJDOTool
{
  private static INDOpmedJDOTool instance = null;

  private INDOpmedJDOTool()
  {
    setModuleName("ind\\INDOpmedModule.x");
    onInit();  
  }

  public static INDOpmedJDOTool getNewInstance()
  {
    if (instance == null) {
      instance = new INDOpmedJDOTool();
    }
    return instance;
  }

  public TParm insertDataD(TParm parm, TConnection conn)
  {
    TParm result = update("insertDataD", parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      return result;
    }
    return result;
  }

  public TParm insertDataM(TParm parm, TConnection conn)
  {
    TParm result = update("insertDataM", parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      return result;
    }
    return result;
  }

  public TParm deleteDataD(TParm parm, TConnection conn)
  {  
    TParm result = update("deleteDataD", parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      return result;
    }
    return result;
  }

  public TParm deleteDataM(TParm parm, TConnection conn)
  {
    TParm result = update("deleteDataM", parm, conn);
    if (result.getErrCode() < 0) {
      err("ERR:" + result.getErrCode() + result.getErrText() + 
        result.getErrName());
      return result;
    }
    return result;
  }
}