package jdo.erd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:急诊留观体温单工具类 </p>
 *
 * <p>Description: 急诊留观体温单工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */

public class ERDForSUMTool
    extends TJDOTool {

    /**
     * 实例
     */
    private static ERDForSUMTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static ERDForSUMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ERDForSUMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ERDForSUMTool() {
        //加载Module文件
        setModuleName("erd\\ERDMainModule.x");
        onInit();
    }

    /**
     * 取得急诊留观病患基本信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selERDPatInfo(TParm parm){
        return query("selERDPatInfo",parm);
    }

}
