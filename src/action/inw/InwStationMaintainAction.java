package action.inw;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.inw.InwStationMaintainTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 住院护士站病区维护Action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class InwStationMaintainAction
    extends TAction{
    public InwStationMaintainAction() {
    }

    /**
     * 展开入口
     * @param parm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
       TParm result = new TParm();
       //创建一个连接，在多事物的时候连接各个操作使用
       TConnection connection = getConnection();

       //调用长期处置的展开方法
       result = InwStationMaintainTool.getInstance().unfold(parm, connection);
       if (result.getErrCode() < 0) {
           System.out.println(result.getErrText());
           connection.rollback();
           connection.close();
           return result;
       }

       //可以在这里调用外部接口也可以在InwStationMaintainTool中调用
       //---------------

       connection.commit();
       connection.close();
       return result;
   }

}
