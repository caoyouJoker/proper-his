package action.clp;

import com.dongyang.action.TAction;
import jdo.clp.CLPVariationTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.clp.CLPORDERTypeTool;

/**
 * <p>Title: 临床路径项目医嘱对照</p>
 *
 * <p>Description:临床路径项目医嘱对照 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPORDERTypeAction extends TAction{
    public CLPORDERTypeAction() {
    }
    public TParm saveORDERType(TParm basicParm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        for (int i = 0; i < basicParm.getCount("ORDER_CODE"); i++) {
            result=CLPORDERTypeTool.getInstance().saveORDERType(basicParm.getRow(i),conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }
}
