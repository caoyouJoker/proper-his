package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 出院医师分值统计表Tool</p>
 *
 * <p>Description: 出院医师分值统计表Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT004DsScoreTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MRORT004DsScoreTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MRORT004DsScoreTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT004DsScoreTool();
        return instanceObject;
    }

    public MRORT004DsScoreTool() {
        this.setModuleName("mro\\MRORT004_DsScoreModule.x");
        this.onInit();
    }
    /**
     * 查询数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectParm(TParm parm){
        TParm result = this.query("select",parm);
        // 判断错误值
        if(result.getErrCode() < 0)
        {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
