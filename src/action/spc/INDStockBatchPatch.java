package action.spc;

import com.dongyang.patch.Patch;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import jdo.ind.INDTool;
import jdo.ind.INDSQL;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 药品库存自动日结批次作业
 * </p>
 *
 * <p>
 * Description: 药品库存自动日结批次作业
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.03
 * @version 1.0
 */

public class INDStockBatchPatch
    extends Patch {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public boolean run() {
        TConnection connection = TDBPoolManager.getInstance().getConnection();
        Timestamp date = StringTool.getTimestamp(new Date());
        TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getINDSysParm()));
        // 判断药库参数档设定
        if (parm.getValue("MANUAL_TYPE", 0) == null) {
            connection.close();
            return false;
        }
        String manual_type = parm.getValue("MANUAL_TYPE", 0);
        // 判断是否为自动过批次
        if (!"0".equals(manual_type)) {
            connection.close();
            return false;
        }
        // 查询所有BATCH_FLG='N'的部门
        TParm orgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getINDBatchLockORG("N")));
        for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
            TParm ddstock = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getStockBatchByOrgCode(orgParm.getValue("ORG_CODE", i),
                                       StringTool.rollDate(date, -1).toString().
                                       substring(0, 10).replaceAll("-", ""))));
            // 判断是否已经过批次
            if (ddstock.getInt("NUM", 0) > 0) {
                continue;
            }
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", orgParm.getValue("ORG_CODE", i));
            inparm.setData("BATCH_FLG", "Y");
            inparm.setData("OPT_USER", "IND");
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", "192.168.1.100");
            inparm.setData("TRANDATE", StringTool.rollDate(date, -1));
            TParm result = new TParm();
            result = INDTool.getInstance().onIndStockBatchByOrgCode(inparm,
                connection);
            if (result.getErrCode() < 0) {
                connection.close();
                return false;
            }
        }
        connection.commit();
        connection.close();
        return true;
    }
}
