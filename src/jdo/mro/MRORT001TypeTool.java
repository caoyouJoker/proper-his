package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;

/**
 * <p>Title: 问题类型统计表Tool</p>
 *
 * <p>Description: 问题类型统计表Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT001TypeTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MRORT001TypeTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MRORT001TypeTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MRORT001TypeTool();
        return instanceObject;
    }
    public MRORT001TypeTool() {
        this.setModuleName("mro\\MRORT001_TypeModule.x");
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
