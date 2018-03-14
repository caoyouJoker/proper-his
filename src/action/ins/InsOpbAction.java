package action.ins;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ins.INSOpdApproveTool;
import java.util.Vector;

/**
 *
 * <p>Title:门诊医保动审核作类 </p>
 *
 * <p>Description:门诊医保审核动作类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.11.05
 * @version JavaHis 1.0
 */
public class InsOpbAction extends TAction {
    /**
     * 审核主档数据
     * @param parm TParm
     * @return TParm
     */
    public TParm saveInsOpdApprove(TParm parm) {

        TConnection connection = getConnection();
        TParm result = new TParm();
        TParm approveParm = new TParm();
        TParm endData = new TParm();
        approveParm.setRowData(parm);
        endData = INSOpdApproveTool.getInstance().insertdata(approveParm,
                connection);
        if (endData.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return endData;
        }
        connection.commit();
        connection.close();
        result = INSOpdApproveTool.getInstance().selectdata(parm);
        return result;
    }

    /**
     * 更新审核标记位，同时更新挂号主档
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfo(TParm parm) {
        TConnection connection = getConnection();
//        System.out.println("parmparm"+parm);
//        System.out.println("前台取值"+parm.getData("Table"));
        Vector tableVct = (Vector) parm.getData("Table");
//        System.out.println("循环次数"+tableVct.size());
        TParm result = new TParm();
        TParm allParm = new TParm();
        allParm.setData("Data", parm.getData("Table"));
//        System.out.println("allParmallParm"+allParm);
        int count = (Integer) tableVct.size();
        for (int i = 0; i < count; i++) {
//            System.out.println("tableVct.get(i)"+tableVct.get(0).getClass());
            Vector approveFlg = (Vector) tableVct.get(i);
//            System.out.println("approveFlg"+approveFlg);
            String appFlg = approveFlg.get(6) == null ? "02" :
                            approveFlg.get(6).toString();
            TParm approveParm = new TParm();
            approveParm.setData("APPROVE_FLG", appFlg);
            approveParm.setData("CASE_NO", parm.getData("CASE_NO", i));
            approveParm.setData("BILL_DATE", parm.getData("BILL_DATE", i));
            approveParm.setData("ORDER_NO", parm.getData("ORDER_NO", i));
            approveParm.setData("ORDER_SEQ", parm.getDouble("ORDER_SEQ", i));
            approveParm.setData("OPT_USER", parm.getData("OPT_USER", i));
            approveParm.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            TParm result1 = INSOpdApproveTool.getInstance().updatedata(
                    approveParm,
                    connection);
            if (result1.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result1;
            }

        }

        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 更新身份
     * @param parm TParm
     * @return TParm
     */
    public TParm upDateCTZ(TParm parm) {
        TConnection connection = getConnection();
        TParm result = INSOpdApproveTool.getInstance().updateCtz(parm,
                connection);
        TParm result1 = INSOpdApproveTool.getInstance().delApprove(parm,
                connection);
        if (result.getErrCode() < 0 || result1.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
}
