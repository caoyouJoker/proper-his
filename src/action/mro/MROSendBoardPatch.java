package action.mro;

import com.dongyang.patch.Patch;
import com.dongyang.data.TParm;

/**
 * <p>Title: 自动发送公布栏批次</p>
 *
 * <p>Description: 自动发送公布栏批次</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.17
 * @version 1.0
 */
public class MROSendBoardPatch
    extends Patch {
    public MROSendBoardPatch() {
    }

    public boolean run() {
        MROQlayControlAction action = new MROQlayControlAction();
        TParm parm = new TParm();
        parm.setData("OPT_USER", "admin");
        parm.setData("OPT_TERM", "127.0.0.1");
        action.onBoardMessage(parm);
        return true;
    }

}
