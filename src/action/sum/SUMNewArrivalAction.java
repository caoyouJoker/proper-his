package action.sum;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ibs.IBSTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sum.SUMVitalSignTool;

/**
 * <p>Title: ���������µ�</p>
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
public class SUMNewArrivalAction
    extends TAction {
    public SUMNewArrivalAction() {
    }


    /**
     * �������
     * @param parm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TParm masterParm= parm.getParm("MASET");
        TParm detailParm= parm.getParm("DETAIL");
        boolean insertFlg=parm.getBoolean("I");

        TParm result = new TParm();
        //����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
        TConnection connection = getConnection();
        if(insertFlg){//����
            result = SUMNewArrivalTool.getInstance().insertNewArrival(
                masterParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
            result = SUMNewArrivalTool.getInstance().insertNewArrivalDtl(
                detailParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
        }else{//����
            result = SUMNewArrivalTool.getInstance().updateNewArrival(
                masterParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }
            result = SUMNewArrivalTool.getInstance().updateNewArrivalDtl(
                detailParm, connection);
            if (result.getErrCode() < 0) {
                System.out.println(result.getErrText());
                connection.close();
                return result;
            }

        }
        if(detailParm.getCount() > 0){
            TParm parmWeight = new TParm();
            int row = detailParm.getCount() - 1;
            parmWeight.setData("CASE_NO",((TParm)detailParm.getParm(row + "PARM")).getData("CASE_NO"));
            parmWeight.setData("WEIGHT",((TParm)detailParm.getParm(row + "PARM")).getData("WEIGHT"));
            parmWeight.setData("HEIGHT","");
            //����ADM_INP���������
            result = SUMVitalSignTool.getInstance().updateHeightAndWeight(
                    parmWeight, connection);
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
