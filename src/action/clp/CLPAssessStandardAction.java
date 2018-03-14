package action.clp;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.CLPAssessStandardTool;

/**
 * <p>Title: 临床路径评估标准Action</p>
 *
 * <p>Description: 临床路径评估标准</p>
 *
 * <p>Copyright: Copyright (c) Javahis 2011</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPAssessStandardAction extends TAction {

    public CLPAssessStandardAction() {
        super();
    }

    public TParm deleteCLP_EVL_CAT(TParm parm) {
//        System.out.println("parm：--------" + parm);
        TConnection conn = this.getConnection();
        TParm result = null;
        //删除第一个表中的数据
        if (parm.getValue("INDEX").equals("2")) {
//            System.out.println("删除1-----------大分类-------------------");
            //删除第1个表中的数据
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT1", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            //删除第2个表中的数据
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT2WithParentID1", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            //删除第3个表中的数据
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT3WithParentID1", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
        } else if (parm.getValue("INDEX").equals("3")) {
//            System.out.println("删除2------------中分类------------------");
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT2", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            //删除第3个表中的数据
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT3WithParentID2", parm, conn);
            if (result.getErrCode() < 0) {
                conn.rollback();
                conn.close();
                return result;
            }

        }else{
            result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
                    "deleteCLP_EVL_CAT3", parm, conn);
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
