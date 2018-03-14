package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 医院挂“审配发退/药品全名显示”</p>
 *
 * <p>Description: 标记工具类</p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */

public class PhaSysParmTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static PhaSysParmTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static PhaSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaSysParmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaSysParmTool() {
        setModuleName("pha\\PHASysParmModule.x");
        onInit();
    }

    /**
     * 查询
     * 返回EXAMINE_FLG DISPENSE_FLG SEND_FLG的标记Y/N
     */

    public boolean needExamine() {
        //获得“西药是否需要审核后配药标记”--N(不用审核，直接配药)Y(先审核，再配药)
        return getResultBoolean(query("needExamine"),"EXAMINEOE_FLG");
    }

    public boolean dispEqualSend() {
        //获得“是否开启配药即发药模式”--N(不是)/Y(是)
        return getResultBoolean(query("dispEqualSend"),"DGTSENDOE_FLG");
    }

    /**
     * 查询
     * 返回EXAMINEOED_FLG的标记Y/N
     */
    public boolean needExamineD() {
        //获得“中药是否需要审核后配药标记”--N(不用审核，直接配药)Y(先审核，再配药)
        return getResultBoolean(query("needExamineD"),"EXAMINEOED_FLG");
    }
}
