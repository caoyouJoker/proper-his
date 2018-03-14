package action.spc;

import com.dongyang.patch.Patch;
import java.util.Date;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;
import com.dongyang.db.TDBPoolManager;
import java.sql.Timestamp;
import jdo.ind.INDSQL;
import jdo.ind.INDTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import jdo.ind.IndReStockBatchTool;

/**
 * <p>Title: 药品库存重新自动日结批次作业</p>
 *
 * <p>Description: 药品库存重新自动日结批次作业</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2011.2.18
 * @version 1.0
 */
public class INDReStockBatchPatch extends Patch{
    public INDReStockBatchPatch() {
    }

    public boolean run() {
        TConnection connection = TDBPoolManager.getInstance().getConnection();
        Timestamp date = StringTool.getTimestamp(new Date());
        date = StringTool.rollDate(date, -1);
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
        String datetime = date.toString().substring(0, 4) +
            date.toString().substring(5, 7) +
            date.toString().substring(8, 10);
        for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
            boolean flg = IndReStockBatchTool.getInstance().onReStockBatch(orgParm.
                getValue("ORG_CODE", i), datetime, connection);
            if (!flg) {
                continue;
            }
        }
        
        orgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDBatchLockORG("N")));
        for(int i = 0; i < orgParm.getCount("ORG_CODE"); i++)
        {
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", orgParm.getValue("ORG_CODE", i));
            inparm.setData("BATCH_FLG", "Y");
            inparm.setData("OPT_USER", "IND");
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", "192.168.1.100");
            inparm.setData("TRANDATE", StringTool.rollDate(date, -1L));
            TParm result = new TParm();
            result = INDTool.getInstance().onIndStockBatchByOrgCode(inparm, connection);
            if(result.getErrCode() < 0)
            {
                connection.close();
                return false;
            }
        }
        
        connection.commit();
        connection.close();
        return true;
    }
}
