package jdo.sys;

import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
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
 * @author not attributable
 * @version 1.0
 */
public class SYSFee_PhaTool {

    /**
     * 实例
     */
    private static SYSFee_PhaTool instanceObject;
    /**
     * 得到实例
     * @return PatTool
     */
    public static SYSFee_PhaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFee_PhaTool();
        return instanceObject;
    }

    public SYSFee_PhaTool() {
    }

    public TParm exeSave(TParm parm,TConnection conn){
        //得到要执行的SQL
     String[] InwSql = parm.getStringArray("sysphasql");
   //  System.out.println("大对象执行SQL的数量---------》" + InwSql);
     //执行大对象的SQL语句
     Map rtn=TJDODBTool.getInstance().update(InwSql, conn);
     //包装成TParm验证
     TParm result=new TParm(rtn);
     if (result.getErrCode() < 0) {
         return result;
     }
     //调用IND提供的Tool继续执行


     if (result.getErrCode() < 0) {
         return result;
     }
        return result;

    }
}
