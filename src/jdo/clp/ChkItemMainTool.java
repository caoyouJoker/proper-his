package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.db.TConnection;

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
public class ChkItemMainTool
    extends TJDOTool {
    /**
     * 构造方法私有化，加载SQL语句配置文件
     */
    private ChkItemMainTool() {
        this.setModuleName("clp\\CLPChkItemMainModule.x");
        onInit();
    }

    /**
     * 声明静态数据库工具类实例
     */
    private static ChkItemMainTool instance = null;
    /**
     * 声明日期格式化类对象
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * 声明静态方法，返回此类的实例
     * @return BscInfoTool
     */
    public static ChkItemMainTool getInstance() {
        if (instance == null) {
            instance = new ChkItemMainTool();
        }
        return instance;
    }

    /**
     * 根据CLNCPATH_CODE查询时程
     * @param parm TParm
     * @return TParm
     */
    public TParm queryByClncPathCode(TParm parm) {
        TParm result = this.query("queryByClncPathCode", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据DURATION_CODE和PARENT_CODE查询时程
     * @param parm TParm
     * @return TParm
     */
    public TParm queryByDurationCode(TParm parm) {
        TParm result = this.query("queryByDurationCode", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询病人信息
     * @param parm TParm
     * @return TParm
     */
    public TParm queryPatientInfo(TParm parm) {
        TParm result = this.query("queryPatientInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 字符串非空验证
     * @param str String
     * @return boolean
     */
    public boolean checkInputString(String str) {
        if (str == null) {
            return false;
        }
        else if ("".equals(str.trim())) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 临床路径查核主档（CASE_NO/CLNCPATH_CODE/SCHD_CODE/ORDER_FLG）
     * @param parm TParm
     * @return TParm
     */
    public TParm queryManagerDBySchdCode(TParm parm) {
        String sql = " SELECT 'N' AS SEL_FLG," // 标准注记
            + " CM.EXEC_FLG," // 必执
            + " CM.CHKUSER_CODE," // 执行人
            + " CM.CHKTYPE_CODE," // 查核类别
            // 实际项目
            + " CASE STANDARD_FLG WHEN 'Y' THEN ("
            + " SELECT CHKITEM_CHN_DESC FROM CLP_CHKITEM CC WHERE CM.CHKTYPE_CODE = CC.CHKTYPE_CODE AND CM.ORDER_CODE = CC.CHKITEM_CODE"
            + " ) ELSE ("
            + " SELECT CHKITEM_CHN_DESC FROM CLP_CHKITEM CC WHERE CM.CHKTYPE_CODE = CC.CHKTYPE_CODE AND CM.MAINORD_CODE = CC.CHKITEM_CODE"
            + " ) END AS ORDER_CHN_DESC,"
            + " DECODE(CM.STANDARD_FLG, 'Y', CM.TOT, CM.MAINTOT) AS MAINTOT," // 标总量
            + " DECODE(CM.STANDARD_FLG, 'Y', CM.DISPENSE_UNIT, CM.MAINDISPENSE_UNIT) AS MAINDISPENSE_UNIT," // 单位
            + " 'N' AS EXECUTE," // 执行
            + " CM.PROGRESS_CODE," // 进度状态
            + " CM.MANAGE_NOTE," // 备注
            + " CM.CASE_NO," //
            + " CM.CLNCPATH_CODE," //
            + " CM.SCHD_CODE," //
            + " CM.ORDER_NO," //
            + " CM.ORDER_SEQ," //
            +
            " DECODE(CM.STANDARD_FLG, 'Y', CM.ORDER_CODE, CM.MAINORD_CODE) AS ORDER_CODE"
            + " FROM CLP_MANAGED CM"
            + " WHERE CM.CASE_NO = '" + parm.getValue("CASE_NO") + "'"
            + " AND CM.CLNCPATH_CODE = '" + parm.getValue("CLNCPATH_CODE") +
            "'"
            + " AND CM.SCHD_CODE = '" + parm.getValue("SCHD_CODE") + "'"
            + " AND CM.ORDER_FLG = '" + parm.getValue("ORDER_FLG") + "'"
            + " AND  CM.ORDER_CODE  IS NOT NULL ";
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(sql);
        if (checkInputString(parm.getValue("EXEC_FLG"))) {
            sqlBuf.append(" AND CM.EXEC_FLG = '" + parm.getValue("EXEC_FLG") + "'");
        }
        if (checkInputString(parm.getValue("CHKUSER_CODE"))) {
            sqlBuf.append(" AND CM.CHKUSER_CODE = '" + parm.getValue("CHKUSER_CODE") + "'");
        }
        if (checkInputString(parm.getValue("PROGRESS_CODE"))) {
            sqlBuf.append(" AND CM.PROGRESS_CODE LIKE '" + parm.getValue("PROGRESS_CODE") + "%'");
        }
        sqlBuf.append(" ORDER BY ORDER_SEQ");
        //System.out.println("CHKITEMMAIN______sqlBuf:::::"+sqlBuf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据CASE_NO取出CLNCPATH_CODE
     * @param parm TParm
     * @return TParm
     */
    public TParm queryManagerM(TParm parm) {
        TParm result = this.update("queryManagerM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateManagerD(TParm parm, TConnection conn) {
        TParm result = this.update("updateManagerD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertManagerD(TParm parm, TConnection conn) {
        TParm result = this.update("insertManagerD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm deleteManagerD(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("deleteManagerD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
}
