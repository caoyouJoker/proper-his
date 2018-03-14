package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 门诊药房退药记录工具类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS (c) 2008</p>
 *
 * @author ZangJH 2008.09.26
 *
 * @version 1.0
 */


public class PhaOrderHistoryTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static PhaOrderHistoryTool instanceObject;

    /**
     * 得到实例
     * @return OrderTool
     */
    public static PhaOrderHistoryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaOrderHistoryTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhaOrderHistoryTool() {
        setModuleName("pha\\PhaOrderHistoryModule.x");

        onInit();
    }

    /**
     * 新增医嘱
     * 新增一条
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm,TConnection connection) {

        TParm result = new TParm();
        //执行module上的insert update delete用update
        result = update("insertdata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 插入操作
     * @param parm
     * @return result
     */
    public TParm onInsert(TParm parm,TConnection connection) {

        int count = parm.getCount();
        TParm result = new TParm();
        for (int i = 0; i < count; i++) {
            TParm inParm = new TParm();
            //从一个多行TParm中取得某一行的数据，放到一个单行Parm中
            inParm.setRowData( -1, parm, i); //依次取得多行parm的第i行，放到单行parm-inParm中
            //执行插入操作
            result = this.insertdata(inParm,connection);
            if (result.getErrCode() < 0)
                return result;
        }
        return result;
    }

}
