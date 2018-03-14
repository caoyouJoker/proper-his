package jdo.inw;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title:护士站工具类
 *
 * <p>Description: 用于执行SQL语句得到一些标记常量（也可以从前台直接执行SQL语句）</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-10
 * @version 1.0
 */
public class InwUtil
    extends TJDOTool {
    /**
     * 构造函数私有化
     */
    private InwUtil() {
        //设置关联的SQL文件
        setModuleName("inw\\INWUtilModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static InwUtil instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static InwUtil getInstance() {
        if (instanceObject == null)
            instanceObject = new InwUtil();
        return instanceObject;
    }

    /**
     * 得到护士审核执行注记
     * @return boolean
     */
    public boolean getNsCheckEXEFlg() {
        TParm result = query("getNsCheckEXEFlg");
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        //为N返回假
        if (result.getData("NS_CHECK_FLG", 0).equals("N")) {
            return false;
        }
        //反之为真
        return true;
    }


}
