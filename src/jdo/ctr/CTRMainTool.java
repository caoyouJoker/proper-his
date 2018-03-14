package jdo.ctr;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;

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
 public class CTRMainTool
    extends TJDOTool {

//使这个对象是单例的,只能初始化一个对象
private static CTRMainTool instance = null;
private CTRMainTool() {

    //加载Module文件,文件格式在下面说明
    this.setModuleName("ctr\\CTRMainModule.x");
    onInit();
}

/**
 *
 * @return
 */
public static CTRMainTool getNewInstance() {
    if (instance == null) {
        instance = new CTRMainTool();
    }
    return instance;
}
//主项表查询
public TParm onMQuery(TParm parm) {
    TParm result = this.query("Mquery", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
//细项表主查询
public TParm onDQuery(TParm parm) {
    TParm result = this.query("Dquery", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
//细项表FU查询
public TParm onDDQuery(TParm parm) {
    TParm result = this.query("DDquery", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**主项表插入
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onMInsert(TParm parm,TConnection conn) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Minsert", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**细项主表插入
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onDInsert(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Dinsert", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**主项表更新
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onMUpdate(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Mupdate", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**细项主表更新
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onDUpdate(TParm parm) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Dupdate", parm);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**主项表删除
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onMDelete(TParm parm,TConnection conn) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Mdelete", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
/**细项主表删除
 * onInsert
 *
 * @param parm TParm
 * @return TParm
 */
public TParm onDDelete(TParm parm,TConnection conn) {
    if (parm == null) {
        err("ERR:" + parm);
    }
    TParm result = this.update("Ddelete", parm,conn);
    if (result.getErrCode() < 0) {
        err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
        return result;
    }
    return result;
}
}

