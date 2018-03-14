package action.bil;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.bil.BILGreenPathTool;

/**
 * <p>Title: 绿色通道ACTION</p>
 *
 * <p>Description: 绿色通道ACTION</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-6-23
 * @version 4.0
 */
public class BILGreenPathAction
    extends TAction {
    public BILGreenPathAction() {
    }
    /**
     * 插入新数据
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm){
        TConnection conn = this.getConnection();
        TParm result = BILGreenPathTool.getInstance().insertdata(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * 作废一条绿色通道信息
     * @param parm TParm
     * @return TParm
     */
    public TParm cancelGreenPath(TParm parm){
        TConnection conn = this.getConnection();
        TParm result = BILGreenPathTool.getInstance().cancleGreenPath(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
