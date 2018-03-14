package action.ctr;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.ctr.CTRMainTool;
import jdo.ctr.CTRTool;
/**
 *
 * <p>Title: 医令管控</p>
 *
 * <p>Description: 医令管控</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author shibl
 * @version 1.0
 */
public class CTRMainAction
    extends TAction {
    /**
     * 删除主项医令
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteCTRMaincode(TParm parm) {
        TParm mresult = new TParm();
        TConnection conn = getConnection();
        mresult = CTRTool.getInstance().onMDelete(parm, conn);
       if (mresult.getErrCode() < 0) {
            conn.close();
            return mresult;
        }
        conn.commit();
        conn.close();
        return mresult;
    }
    /**
   * 删除细项医令
   * @param parm TParm
   * @return TParm
   */
   public TParm deleteCTRDetailcode(TParm parm) {
      TParm result = new TParm();
      TConnection conn = getConnection();
      result = CTRTool.getInstance().onDDelete(parm, conn);
     if (result.getErrCode() < 0) {
          conn.close();
          return result;
      }
      conn.commit();
      conn.close();
      return result;
  }
  /**
  * 插入主项医令
  * @param parm TParm
  * @return TParm
  */
  public TParm InsertCTRMaincode(TParm parm) {
     TParm result = new TParm();
     TConnection conn = getConnection();
     result = CTRTool.getInstance().onMInsert(parm, conn);
    if (result.getErrCode() < 0) {
         conn.close();
         return result;
     }
     conn.commit();
     conn.close();
     return result;
 }

}
