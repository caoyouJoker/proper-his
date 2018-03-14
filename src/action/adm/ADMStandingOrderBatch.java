package action.adm;

import com.dongyang.patch.Patch;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.inw.InwStationMaintainTool;
import java.util.Vector;
import jdo.sys.SystemTool;

/**
 * <p>Title: 长期医嘱展开批次执行动作类</p>
 *
 * <p>Description: 长期医嘱展开批次执行动作类</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.03.26
 * @version 1.0
 */
public class ADMStandingOrderBatch extends Patch {
    public ADMStandingOrderBatch() {
    }

    /**
     * 批次线程
     * @return boolean
     */
    public boolean run() {
        TConnection connection = TDBPoolManager.getInstance().getConnection();
        Timestamp date = StringTool.getTimestamp(new Date());
        String sql =
                " SELECT C.PAT_NAME, C.BIRTH_DATE, B.IN_DATE, B.DS_DATE, B.CTZ1_CODE, B.MR_NO," +
                "        B.CASE_NO, B.IPD_NO, B.TOTAL_AMT, B.TOTAL_BILPAY, B.STATION_CODE," +
                "        B.DEPT_CODE, B.CUR_AMT, B.BILL_STATUS " +
                "   FROM SYS_BED A, ADM_INP B, SYS_PATINFO C " +
                "  WHERE A.BED_NO = B.BED_NO(+) " +
                "    AND A.CASE_NO = B.CASE_NO(+) " +
                "    AND A.MR_NO = B.MR_NO(+) " +
                "    AND A.MR_NO = C.MR_NO(+) " +
                "    AND A.ACTIVE_FLG = 'Y' " +
                "    AND B.DS_DATE IS NULL " +
                "    AND A.ALLO_FLG = 'Y' " +
                "    AND B.CANCEL_FLG <> 'Y' " +
                "    AND A.BED_STATUS = '1' " +
                "  ORDER BY A.BED_NO ";
        //查询在院病患
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        parm.setData("DATE", date);
        // 是否存在在院病人
        if (parm.getCount() <= 0) {
            connection.close();
            return false;
        }
        //try {
            for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            	//成功展开则提交
                if (insertSingleData(parm, i, connection)) {
                    connection.commit();
                    
                //失败则继续运行批次
                }else{
                	continue;
                }
            }
       /* } catch (Exception e) {
            e.printStackTrace();
        }*/
        connection.commit();
        connection.close();
        return true;
    }
    /**
     * 循环
     * @param parm TParm
     * @param row int
     * @param connection TConnection
     * @return boolean
     */
    public boolean insertSingleData(TParm parm, int row, TConnection connection) {
        TParm result = new TParm();
        TParm batchParm = new TParm();
        TParm selMaxSeqParm = new TParm();
        TParm selMaxSeq = new TParm();
        TParm batchLogParm = new TParm();
        //单行应诊号
        String caseNo = parm.getValue("CASE_NO", row);
        String postDate = StringTool.getString(parm.getTimestamp("DATE"),
                                               "yyyyMMdd") + "235959";
        selMaxSeq.setData("POST_DATE",postDate);
        selMaxSeq.setData("SYSTEM_CODE","STANDING_ORDER");
        //查询最大批次日志档序号
        selMaxSeqParm = SystemTool.getInstance().selMaxBatchSeq(selMaxSeq);
        if (selMaxSeqParm.getErrCode() < 0) {
            return false;
        }
        int maxSeq = selMaxSeqParm.getInt("SEQ", 0);
        maxSeq = maxSeq + 1;
        TParm actionParm = new TParm();
        String mrNo = parm.getValue("MR_NO", row);
        String ipdNo = parm.getValue("IPD_NO", row);
        String deptCode = parm.getValue("DEPT_CODE", row);
        String stationCode = parm.getValue("STATION_CODE", row);
        //行记录就诊号加入
        Vector caseNoVct = new Vector();
        caseNoVct.add(caseNo);
        actionParm.setData("CASE_NO", caseNoVct);
        actionParm.setData("OPT_DATE", parm.getTimestamp("DATE"));
        actionParm.setData("OPT_USER", "SO_BATCH");
        actionParm.setData("OPT_TERM", "127.0.0.1");
        batchLogParm.setData("POST_DATE", postDate);
        batchLogParm.setData("SYSTEM_CODE", "STANDING_ORDER");
        batchLogParm.setData("SEQ", maxSeq);
//        System.out.println("");
        batchLogParm.setData("CASE_NO", caseNo);
        batchLogParm.setData("MR_NO", mrNo);
        batchLogParm.setData("IPD_NO", ipdNo);
        batchLogParm.setData("DEPT_CODE", deptCode);
        batchLogParm.setData("STATION_CODE", stationCode);
        batchLogParm.setData("OPT_USER", "SO_BATCH");
        batchLogParm.setData("OPT_TERM", "127.0.0.1");
        //长期医嘱展开(处置)批次保存
        try{
        	//test
        	//if(row!=3){
        		result = InwStationMaintainTool.getInstance().unfold(actionParm,
	                connection);
        	/*}else{
        		System.out.println("----caseNoVct----"+caseNoVct);
        		
        		throw new Exception("出错了...");
        	}*/
	    //假如有未知异常出现
        }catch(Exception e){
        	System.out.println();
            //写入批次日志档(失败)   SYS_FALSE_BATCH_LOG
            actionParm.setData("STATUS", "0");
            batchParm = SystemTool.getInstance().insertBatchLog(batchLogParm,
                    connection);
            if (batchParm.getErrCode() < 0) {
                connection.rollback();
                return false;
            }
            connection.commit();
            return false;
        }
        
        //正常错误出现
        if (result.getErrCode() < 0) {
            //写入批次日志档(失败)   SYS_FALSE_BATCH_LOG
            actionParm.setData("STATUS", "0");
            batchParm = SystemTool.getInstance().insertBatchLog(batchLogParm,
                    connection);
            if (batchParm.getErrCode() < 0) {
                connection.rollback();
                return false;
            }
            connection.commit();
            return false;
        }
        return true;
    }
}
