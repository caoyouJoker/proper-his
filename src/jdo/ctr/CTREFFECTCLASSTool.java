package jdo.ctr;
import com.dongyang.data.*;
import com.dongyang.jdo.*;

/**
* <p>Title: </p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: </p>
*
* @author
* @version 1.0
*/
 public class CTREFFECTCLASSTool
    extends TJDOTool {

//使这个对象是单例的,只能初始化一个对象
private static CTREFFECTCLASSTool instance = null;
private CTREFFECTCLASSTool() {

    //加载Module文件,文件格式在下面说明
    this.setModuleName("ctr\\CTREFFECTCLASSModule.x");
    onInit();
}

/**
 *
 * @return
 */
public static CTREFFECTCLASSTool getNewInstance() {
    if (instance == null) {
        instance = new CTREFFECTCLASSTool();
    }
    return instance;
}

public TParm onQuery(TParm parm) {
    TParm result = this.query("query", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}

/**
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onInsert(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("insert", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}

public TParm onUpdate(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("update", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}

public TParm onDelete(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("delete", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}

}

