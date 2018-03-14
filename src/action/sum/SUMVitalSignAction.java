package action.sum;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sum.SUMVitalSignTool;

/**
 * <p>Title:成人(非新生体温单) </p>
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
public class SUMVitalSignAction
    extends TAction{
    public SUMVitalSignAction() {
    }

    /**
 * 保存入口
 * @param parm
 * @return TParm
 */
public TParm onSave(TParm parm) {
    TParm masterParm= parm.getParm("MASET");
    TParm detailParm= parm.getParm("DETAIL");
    TParm HWeightParm=parm.getParm("HW");//体重 身高
    boolean insertFlg=parm.getBoolean("I");

    TParm result = new TParm();
    //创建一个连接，在多事物的时候连接各个操作使用
    TConnection connection = getConnection();
    if(insertFlg){//插入
        result = SUMVitalSignTool.getInstance().insertVitalSign(
            masterParm, connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        result = SUMVitalSignTool.getInstance().insertVitalSignDtl(
            detailParm, connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        //更新ADM_INP的身高体重
        result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
            HWeightParm, connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }


    }else{//更新
        result = SUMVitalSignTool.getInstance().updateVitalSign(
            masterParm, connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        result = SUMVitalSignTool.getInstance().updateVitalSignDtl(
            detailParm, connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }
        //更新ADM_INP的身高体重
        result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
            HWeightParm, connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrText());
            connection.close();
            return result;
        }

    }
    connection.commit();
    connection.close();
    return result;
}

}
