package action.clp;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.Operator;
import jdo.clp.ClpVarMoncatTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
/**
 * <p>Title:变异字典 </p>
 *
 * <p>Description: 变异字典</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110507
 * @version 1.0
 */
public class ClpVarMoncatAction extends TAction{
    public ClpVarMoncatAction() {
    }
    /**
     * 修改添加子表方法
     * @param varianceParm TParm
     */
    public TParm saveVariance(TParm varianceParm) {
        TConnection conn = getConnection();
        TParm resultVariance = new TParm();
        for (int i = 0; i < varianceParm.getCount("MONCAT_CODE"); i++) {
            TParm parmValue = new TParm();
            //获得表格中的一行数据执行修改添加方法
            parmValue.setRowData( -1, varianceParm, i);
            //FLG=N 执行修改方法 FLG=Y 执行添加方法
            if (varianceParm.getValue("FLG", i).equals("N")) {
                resultVariance = ClpVarMoncatTool.getInstance().
                                 updateClpVariance(parmValue, conn);
            } else {
                //没有子表主键不能执行添加方法
                if (null == varianceParm.getValue("VARIANCE_CODE", i) ||
                    varianceParm.getValue("VARIANCE_CODE", i).equals(""))
                    break;
                //添加方法
                resultVariance = ClpVarMoncatTool.getInstance().saveClpVariance(
                        parmValue, conn);
            }

        }
        if (resultVariance == null) {
            //回滚
            conn.rollback();
            conn.close();
            return null;
        }
        //提交
        conn.commit();
        conn.close();
        return resultVariance;
    }

}
