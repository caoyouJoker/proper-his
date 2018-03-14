package action.ind;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ind.INDOpmedJDOTool;

public class INDOpmedAction extends TAction
{
  public TParm onInsert(TParm parm)
  {
    TParm result = new TParm();
    TConnection conn = getConnection();
    TParm parmM = parm.getParm("parmM");
    TParm parmD = parm.getParm("parmD");

    int countD = parmD.getCount("ORDER_CODE");

    result = INDOpmedJDOTool.getNewInstance().deleteDataM(parmM, conn);
    if (result.getErrCode() < 0) {
      conn.rollback();
      conn.close();
      return result;
    }
    for (int i = 0; i < countD - 1; i++) {
      result = INDOpmedJDOTool.getNewInstance().deleteDataD(parmD.getRow(i), conn);
      if (result.getErrCode() < 0) {
        conn.rollback();
        conn.close();
        return result;
      }
    }

    result = INDOpmedJDOTool.getNewInstance().insertDataM(parmM, conn);
    if (result.getErrCode() < 0) {
      conn.rollback();
      conn.close();
      return result;
    }

    for (int i = 0; i < countD; i++) {
      result = INDOpmedJDOTool.getNewInstance().insertDataD(parmD.getRow(i), conn);
      if (result.getErrCode() < 0) {
        conn.rollback();
        conn.close();
        return result;
      }
    }
    conn.commit();
    conn.close();
    return result;
  }

  public TParm onDeleteD(TParm parm)
  {
    double amt = 0.0D;
    TParm result = new TParm();
    TConnection conn = getConnection();
    int count = parm.getCount("ORDER_CODE");

    for (int i = 0; i < count; i++) {
      if (parm.getValue("N_DEL", i).equals("Y")) {
        amt += parm.getDouble("N_TOTFEE");
        result = INDOpmedJDOTool.getNewInstance().deleteDataD(parm.getRow(i), conn);
        if (result.getErrCode() < 0) {
          conn.rollback();
          conn.close();
          return result;
        }
      }
    }
    conn.commit();
    conn.close();
    return result;
  }

  public TParm onDeleteMD(TParm parm)
  {
    TParm result = new TParm();
    TConnection conn = getConnection();
    TParm parmM = parm.getParm("parmM");
    TParm parmDAll = parm.getParm("parmDAll");
    result = INDOpmedJDOTool.getNewInstance().deleteDataM(parmM, conn);
    if (result.getErrCode() < 0) {
      conn.rollback();
      conn.close();
      return result;
    }
    int count = parmDAll.getCount("ORDER_CODE");
    for (int i = 0; i < count - 1; i++) {
      result = INDOpmedJDOTool.getNewInstance().deleteDataD(parmDAll.getRow(i), conn);
      if (result.getErrCode() < 0) {
        conn.rollback();
        conn.close();
        return result;
      }
    }

    conn.commit();
    conn.close();
    return result;
  }
}