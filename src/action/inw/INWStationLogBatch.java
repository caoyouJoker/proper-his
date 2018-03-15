package action.inw;

import java.sql.Timestamp;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;
import jdo.sta.STADeptListTool;
import jdo.sta.STAStationDailyTool;


/**
 * <p>Title: 病区日志批次展开动作类</p>
 *
 * <p>Description: 病区日志批次展开动作类</p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wanglong 2013.06.06
 * @version 1.0
 */
public class INWStationLogBatch
        extends Patch {

    public INWStationLogBatch() {}

    /**
     * 批次线程
     * 
     * @return boolean
     */
    public boolean run() {
        TConnection conn = TDBPoolManager.getInstance().getConnection();
        TParm sql = new TParm();
        Timestamp date = StringTool.getTimestamp(new Date());
        Timestamp yesterday = StringTool.rollDate(date, -1);
        sql.setData("STADATE", StringTool.getString(yesterday, "yyyyMMdd"));
        TParm inParm = this.getParm();
        sql.setData("OPT_USER", inParm.getValue("OPT_USER"));// SL_BATCH
        sql.setData("OPT_TERM", inParm.getValue("OPT_TERM"));// 127.0.0.1
        sql.setData("REGION_CODE", inParm.getValue("REGION_CODE"));
        TParm dept = new TParm();
        dept = STADeptListTool.getInstance().selectIPD_DEPT(sql);
        if (dept.getErrCode() < 0) {
            System.out.println("病区日志批次：查询部门中间表的住院部门失败 " + dept.getErrCode() + dept.getErrText()
                    + dept.getErrName());
            return false;
        }
        TParm parm = new TParm();
        parm.setData("SQL", sql.getData());
        parm.setData("DEPT", dept.getData());
        TParm result = new TParm();
        result = STAStationDailyTool.getInstance().insertData(parm, conn);
        if (result.getErrCode() < 0) {
            System.out.println("病区日志批次：导入病区中间表信息失败 " + result.getErrCode() + result.getErrText()
                    + result.getErrName());
            conn.close();
            return false;
        }
        conn.commit();
        conn.close();
        return true;
    }
}
