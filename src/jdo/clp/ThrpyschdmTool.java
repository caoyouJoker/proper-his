package jdo.clp;

import java.text.*;

import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 数据库操作工具类</p>
 *
 * <p>Description: 治疗时程</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ThrpyschdmTool
    extends TJDOTool {
    /**
     * 构造方法私有化，加载SQL语句配置文件
     */
    private ThrpyschdmTool() {
        this.setModuleName("clp\\CLPBscInfoModule.x");
        onInit();
    }

    /**
     * 声明静态数据库工具类实例
     */
    private static ThrpyschdmTool instance = null;
    /**
     * 声明日期格式化类对象
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * 声明静态方法，返回此类的实例
     * @return BscInfoTool
     */
    public static ThrpyschdmTool getInstance() {
        if (instance == null) {
            instance = new ThrpyschdmTool();
        }
        return instance;
    }

    /**
     * 治疗时程
     * @param parm TParm
     * @return TParm
     */
    public TParm getThrpyschdmList(TParm parm) {
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(
            " SELECT CD.DURATION_CHN_DESC AS SCHD_DESC, CT.CLNCPATH_CODE,CT.SCHD_CODE,CT.SCHD_DAY,CT.SUSTAINED_DAYS,CT.SEQ,CT.REGION_CODE,CT.SCHD_AMT FROM CLP_THRPYSCHDM CT, CLP_DURATION CD  WHERE 1=1 ");
        sqlBuf.append(" AND CT.SCHD_CODE = CD.DURATION_CODE");
        sqlBuf.append(" AND CT.REGION_CODE = CD.REGION_CODE");
        if (parm.getValue("REGION_CODE") != null &&
            !"".equals(parm.getValue("REGION_CODE").trim())) {
            sqlBuf.append(" AND CT.REGION_CODE = '" +
                          parm.getValue("REGION_CODE") +
                          "'");
        }
        if (parm.getValue("CLNCPATH_CODE") != null &&
            !"".equals(parm.getValue("CLNCPATH_CODE").trim())) {
            sqlBuf.append(" AND CT.CLNCPATH_CODE = '" +
                          parm.getValue("CLNCPATH_CODE") + "'");
        }
        sqlBuf.append(" ORDER BY CT.SEQ ");
        //System.out.println("治疗时程查询语句:"+sqlBuf.toString());
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 精确查询治疗时程
     * @param parm TParm
     * @return TParm
     */
    public TParm getThrpyschdmObject(TParm parm) {
        TParm result = new TParm();
        result = query("queryThrpyschdm", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * update
     *
     * @param parm TParm
     */
    public TParm update(TParm parm) {
        TParm result = new TParm();
        result = this.update("updateThrpyschdm", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * insert
     *
     * @param parm TParm
     */
    public TParm insert(TParm parm) {
        TParm result = new TParm();
        result = this.update("insertThrpyschdm", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * delete
     *
     * @param parm TParm
     * @return TParm
     */
    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("deleteThrpyschdm", parm, conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
