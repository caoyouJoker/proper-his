package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.text.SimpleDateFormat;
 
public class DEVTool extends TJDOTool {
    /**
     * 实例
     */
    public static DEVTool instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static DEVTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DEVTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public DEVTool() {
        setModuleName("dev\\DEVModule.x");
        onInit();
    }

    public String MaxDSeq(TParm parm) {
        return
                " SELECT MAX(DEVSEQ_NO) AS SEQ FROM DEV_STOCKD WHERE HOSP_AREA='" +
                parm.getValue("HOSP_AREA", 0) +
                "' AND DEV_CODE='" + parm.getValue("DEV_CODE", 0) +
                "' AND BATCH_SEQ='" + parm.getInt("BATCH_SEQ", 0) + "'";
    }

    /**
     * 得到院区
     * @return String
     */
    public String getArea() {
        String regionCode;
        String sql = "SELECT * FROM SYS_REGION WHERE REGION_CODE='" +
                     Operator.getRegion() + "'";
        TParm parm = new TParm(this.getDBTool().select(sql));
        regionCode = parm.getValue("REGION_CODE", 0); 
        return regionCode;   
    }
 

    public TParm getDDate(TParm parm) {
        String sql = "SELECT A.DEV_CODE,A.BATCH_SEQ,A.HOSP_AREA,A.BATCH_CODE, A.DEPT_CODE,A.SCRAP_VALUE," +
                     " A.GUAREP_DATE,A.INWAREHOUSE_DATE,A.DEP_DATE,A.LOC_CODE,B.MANSEQ_NO,B.SETDEV_CODE," +
                     " A.SUP_CODE,A.USE_USER,A.CARE_USER,A.MAN_DATE,A.UNIT_PRICE FROM DEV_STOCKM " +
                     " A,DEV_INWAREHOUSEDD B  WHERE A.DEV_CODE='" +
                     parm.getValue("DEV_CODE", 0) + "' AND A.HOSP_AREA='" +
                     parm.getValue("HOSP_AREA", 0) +
                     "' AND A.BATCH_SEQ='" + parm.getValue("BATCH_SEQ", 0) +
                     "'";   
        TParm result = new TParm(this.getDBTool().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 得到盘点时间
     * @return String
     */
    public TParm getChecktime(String deptcode,int SEQ) {
        SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd");
        String start = String.valueOf(day.format(SystemTool.getInstance().
                                                 getDate()));
        String end = String.valueOf(day.format(StringTool.rollDate(SystemTool.
                getInstance().getDate(), 1)));
        String sql;
        if(SEQ==1){
            sql = "SELECT DISTINCT ACTUAL_CHECKQTY_DATE  ACTUAL_CHECKQTY_DATE FROM DEV_QTYCHECK WHERE DEPT_CODE='" +
                  deptcode + "' AND ACTUAL_CHECKQTY_DATE BETWEEN TO_DATE('" +
                  start + "','yyyyMMdd') AND TO_DATE('" + end +
                  "','yyyyMMdd') ORDER BY ACTUAL_CHECKQTY_DATE DESC ";
        }else{
            sql = "SELECT DISTINCT ACTUAL_CHECKQTY_DATE  ACTUAL_CHECKQTY_DATE FROM DEV_QTYCHECK WHERE DEPT_CODE='" +
                    deptcode + "'ORDER BY ACTUAL_CHECKQTY_DATE DESC ";
        }
        TParm parm = new TParm(this.getDBTool().select(sql));
        if (parm.getErrCode() < 0) {
            err("ERR:" + parm.getErrCode() + parm.getErrText()
                + parm.getErrName());
            return parm;
        }
        return parm;
    }

    /**
     * 得到盘点时间数据
     * @param parm TParm
     */
    public TParm getCheckData(TParm parm) {   
        TParm result = new TParm();
        String orgcode = parm.getValue("DEPT_CODE", 0);
        String checktime = parm.getValue("ACTUAL_CHECKQTY_DATE", 0);
        String region = parm.getValue("REGION_CODE", 0);
        String sql = "SELECT A.DEPT_CODE,A.DEV_CODE,C.DEV_CHN_DESC,A.BATCH_SEQ,A.REGION_CODE,A.STOCK_QTY QTY,A.CHECK_TYPE,A.ACTUAL_CHECK_QTY,A.ACTUAL_CHECKQTY_DATE," +
                     "A.ACTUAL_CHECKQTY_USER,A.CHECK_OPT_CODE,A.CHECK_PHASE_AMT,A.CHECK_PHASE_QTY,A.MODI_AMT,A.MODI_DATE,A.MODI_QTY,A.MODIQTY_OPT_CODE,B.UNIT_PRICE," +
                     "D.UNIT_CHN_DESC,B.INWAREHOUSE_DATE,B.GUAREP_DATE,B.DEP_DATE,B.LOC_CODE,C.SEQMAN_FLG,C.DESCRIPTION " +
                     " FROM DEV_QTYCHECK  A,DEV_STOCKM B,DEV_BASE C,SYS_UNIT D " +
                     "WHERE A.DEPT_CODE=B.DEPT_CODE(+) AND A.DEV_CODE=B.DEV_CODE " +
                     "AND A.BATCH_SEQ=B.BATCH_SEQ(+) AND A.DEV_CODE=C.DEV_CODE(+) " +
                     "AND C.UNIT_CODE=D.UNIT_CODE(+)" + 
                     "AND A.DEPT_CODE='" + orgcode +    
                     "' AND A.ACTUAL_CHECKQTY_DATE=TO_DATE('" + checktime +
                     "','YYYYMMDDHH24MISS') AND A.REGION_CODE='" + region + "'";
        //system.out.println("sql"+sql); 
        result = new TParm(this.getDBTool().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 更新库存数量
     * @param parm TParm
     * @param conn TConnection 
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询细表
     * @param parm
     * @return
     */
    public TParm onDQuery(TParm parm) {
        String sql = "SELECT COUNT(*) AS COUNT, A.DEV_CODE" +
                     " FROM DEV_STOCKD A,DEV_BASE B,SYS_UNIT C WHERE A.DEV_CODE=B.DEV_CODE(+)AND B.UNIT_CODE=C.UNIT_CODE(+) AND" +
                     " A.HOSP_AREA='" + parm.getValue("HOSP_AREA", 0) +
                     "'AND A.DEV_CODE='" + parm.getValue("DEV_CODE", 0) +
                     "' AND " +
                     " A.BATCH_SEQ='" + parm.getInt("BATCH_SEQ", 0) +
                     "' AND A.INWAREHOUSE_DATE=TO_DATE('" +
                     parm.getValue("INWAREHOUSE_DATE", 0).substring(0, 10) + "','yyyy-MM-dd')   GROUP BY A.DEV_CODE,A.HOSP_AREA,A.INWAREHOUSE_DATE,A.BATCH_SEQ";
        TParm result = new TParm(this.getDBTool().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询细表
     * @param parm
     * @return
     */
    public TParm onMQuery(TParm parm) {
        TParm result = this.query("queryM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     *新增细表库存数量
     * @param parm TParm
     * @return TParm
     */
    public TParm onDInsert(TParm parm) {
        TParm result = this.update("insertD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *删除细表库存数量
     * @param parm TParm
     * @return TParm
     */
    public TParm onDDelete(TParm parm) {
        TParm result = this.update("deleteD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * getDBTool
     * 数据库工具实例
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
