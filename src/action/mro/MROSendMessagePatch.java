package action.mro;

import com.dongyang.patch.Patch;
import com.dongyang.data.TParm;

/**
 * <p>Title: 自动发送邮件批次</p>
 *
 * <p>Description: 自动发送邮件批次</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.9
 * @version 1.0
 */
public class MROSendMessagePatch
    extends Patch {
    public MROSendMessagePatch() {
    }

    public boolean run() {
        MROQlayControlAction action = new MROQlayControlAction();
        TParm parm = new TParm();
        parm.setData("OPT_USER", "admin");
        parm.setData("OPT_TERM", "127.0.0.1");
        action.onSendMessage(parm);
        return true;
    }

}
