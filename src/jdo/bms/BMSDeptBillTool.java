package jdo.bms;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSDeptBillTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BMSDeptBillTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static BMSDeptBillTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSDeptBillTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSDeptBillTool() {
        setModuleName("bms\\BMSDeptBillModule.x");
        onInit();
    }


    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {   
       String where ="";
       String whereOne="";
        if (null!=parm.getData("EXE_DEPT_CODE") &&!"".equals(parm.getValue("EXE_DEPT_CODE"))) {
        	where+=" AND A.EXE_DEPT_CODE='"+parm.getValue("EXE_DEPT_CODE")+"'";
        	//whereOne+=" AND A.EXE_DEPT_CODE='"+parm.getValue("EXE_DEPT_CODE")+"'";
        }
        if (null!=parm.getData("EXE_STATION_CODE") &&!"".equals(parm.getValue("EXE_STATION_CODE"))) {
        	where+=" AND A.EXE_STATION_CODE='"+parm.getValue("EXE_STATION_CODE")+"'";
        	//whereOne+=" AND A.EXE_STATION_CODE='"+parm.getValue("EXE_STATION_CODE")+"'";
        }
        if (null!=parm.getData("CASE_NO") &&!"".equals(parm.getValue("CASE_NO"))) {
        	where+=" AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
        	whereOne+=" AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
        }
        if (null!=parm.getData("IPD_NO") &&!"".equals(parm.getValue("IPD_NO"))) {
        	where+=" AND B.IPD_NO='"+parm.getValue("IPD_NO")+"'";
        	whereOne+=" AND B.IPD_NO='"+parm.getValue("IPD_NO")+"'";
        }
        if (null!=parm.getData("OPT_USER") &&!"".equals(parm.getValue("OPT_USER"))) {
        	where+=" AND B.OPT_USER='"+parm.getValue("OPT_USER")+"'";
        	whereOne+=" AND B.OPT_USER='"+parm.getValue("OPT_USER")+"'";
        }
        
        //add by yangjj 20160708
        if (null!=parm.getData("ORDER_CODE") &&!"".equals(parm.getValue("ORDER_CODE"))) {
        	where+=" AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
        	//whereOne+=" AND A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
        }
        
        
        String sql="SELECT E.DEPT_CHN_DESC, F.STATION_DESC, B.IPD_NO, B.MR_NO, G.PAT_NAME," +
        		"C.ORDER_DESC, C.SPECIFICATION, D.UNIT_CHN_DESC,CASE WHEN A.ORDERSET_CODE IS NOT NULL AND A.INDV_FLG='N' THEN H.TOT_AMT/A.DOSAGE_QTY ELSE A.OWN_PRICE END OWN_PRICE, A.DOSAGE_QTY," +
        		"CASE WHEN A.ORDERSET_CODE IS NOT NULL AND A.INDV_FLG='N' THEN H.TOT_AMT  " +
        		" WHEN D.UNIT_CODE = '212' THEN A.OWN_PRICE * A.DOSAGE_QTY * 1.5 " +               //modify by wukai 添加对1.5单位或者2单位或者0.5单位产品条件判断
        		" WHEN D.UNIT_CODE = '213' THEN A.OWN_PRICE * A.DOSAGE_QTY * 2   " +
        		" WHEN D.UNIT_CODE = '170' THEN A.OWN_PRICE * A.DOSAGE_QTY * 0.5 " +
        		" ELSE A.OWN_PRICE * A.DOSAGE_QTY END AMT " +
        		" FROM IBS_ORDD A, IBS_ORDM B, SYS_FEE C, SYS_UNIT D, SYS_DEPT E, SYS_STATION F, SYS_PATINFO G," +
        		"(SELECT SUM(A.TOT_AMT) TOT_AMT ,A.ORDERSET_CODE ,A.ORDERSET_GROUP_NO,A.CASE_NO_SEQ,A.CASE_NO FROM IBS_ORDD A,IBS_ORDM B " +
        		"WHERE A.CASE_NO = B.CASE_NO  AND A.CASE_NO_SEQ = B.CASE_NO_SEQ AND A.ORDERSET_CODE IS NOT NULL AND A.INDV_FLG = 'Y' " +
        		"AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')" +
        		" "+whereOne+
                " GROUP BY A.ORDERSET_CODE ,A.ORDERSET_GROUP_NO,A.CASE_NO_SEQ,A.CASE_NO ) H   WHERE A.CASE_NO = B.CASE_NO "+
        	    "  AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "+
        	    "  AND A.ORDER_CODE = C.ORDER_CODE "+
        	    "  AND A.DOSAGE_UNIT = D.UNIT_CODE "+
        	    "  AND A.EXE_DEPT_CODE = E.DEPT_CODE "+
        	    "  AND A.CASE_NO=H.CASE_NO(+) "+
        	    "  AND A.ORDER_CODE=H.ORDERSET_CODE(+) "+
        	    "  AND A.CASE_NO_SEQ=H.CASE_NO_SEQ(+) "+
        	    "  AND A.ORDERSET_GROUP_NO=H.ORDERSET_GROUP_NO(+) "+
        	    "  AND A.EXE_STATION_CODE = F.STATION_CODE(+) "+
        	    "  AND B.MR_NO = G.MR_NO "+
          	    "  AND B.DATA_TYPE = '1' AND (A.ORDERSET_CODE IS NULL OR (A.ORDERSET_CODE IS NOT NULL AND A.INDV_FLG = 'N'))"+
          	    "  AND A.BILL_DATE >= TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')  "+
          	    "  AND A.BILL_DATE <=TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')"+where;
        System.out.println("sql：：：S::::"+sql);
        TParm result =new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
