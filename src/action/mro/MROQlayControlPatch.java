package action.mro;

import com.dongyang.patch.Patch;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 自动质控批次</p>
 *
 * <p>Description: 自动质控批次</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.9
 * @version 1.0
 */
public class MROQlayControlPatch
    extends Patch {
    public MROQlayControlPatch() {
    }

    public boolean run() {
        MROQlayControlAction action = new MROQlayControlAction();
        TParm parm = new TParm();
        String sql = "SELECT METHOD_CODE FROM MRO_METHOD ORDER BY METHOD_CODE ";
        TParm methodParm = new TParm(TJDODBTool.getInstance().select(sql));
        for (int i = 0; i < methodParm.getCount("METHOD_CODE"); i++) {
            parm.setData("METHOD_CODE", methodParm.getValue("METHOD_CODE", i));
            parm.setData("OPT_USER", "admin");
            parm.setData("OPT_TERM", "127.0.0.1");
            action.onQlayControlMethod(parm);
        }
        return true;
    }
}
