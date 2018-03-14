package jdo.emr;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 电子病历操作日志Tool</p>
 *
 * <p>Description: 日志查询和批量删除</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class OptLogTool
    extends TJDOTool {
    public OptLogTool() {
        this.setModuleName("emr\\EMROptLogModule.x");
        onInit();
    }

    /**
     * 实例
     */
    public static OptLogTool instance;
    /**
     * 日期格式化类
     */
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * 得到实例
     * @return IBSTool
     */
    public static OptLogTool getInstance() {
        if (instance == null) {
            instance = new OptLogTool();
        }
        return instance;
    }

    /**
     * CASE_NO 就诊号
     * FILE_SEQ 文件序号
     * OPT_SEQ 操作序号
     * OPT_TYPE 操作类型 L:登陆 C:新建 O:打开 M:修改 D:删除
     * MR_NO 病案号
     * IPD_NO 住院号
     * FILE_NAME 文件名称
     * DEPT_CODE 科室
     * STATION_CODE 病区
     * BED_NO 床号
     * OPT_USER 操作用户
     * OPT_DATE 操作日期
     * OPT_TERM 操作时间
     * @return TParm
     */
    public TParm writeOptLog(TParm parm) {
        TParm result = new TParm();
        result = this.update("writeOptLog", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 非三级检诊医师及值班医师：电子病历操作日志/EMR_OPTLOG/登陆 新建 打开 修改 删除
     * @param obj Object 病人信息
     * @param optType 操作类型 L:登陆 C:新建 O:打开 M:修改 D:删除
     * @param emrParm FILE_SEQ:文件序号 FILE_NAME:文件名称
     * @return TParm
     */
    public TParm writeOptLog(Object obj, String optType, TParm emrParm) {
        // 判断对象是否为空和是否为TParm类型
        if (obj == null && ! (obj instanceof TParm)) {
            return null;
        }
        // 类型转换成TParm
        TParm parm = (TParm) obj;

        // 操作序号
        StringBuffer sqlBuf = new StringBuffer();
        sqlBuf.append(
            " SELECT NVL(MAX(OPT_SEQ) + 1, 0) AS OPT_SEQ FROM EMR_OPTLOG WHERE ");
        sqlBuf.append(" CASE_NO = '" + parm.getValue("CASE_NO") +
                      "' AND FILE_SEQ = '" + emrParm.getValue("FILE_SEQ") +
                      "'");
        TParm optSeqParm = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));
        sqlBuf.delete(0, sqlBuf.length());
        // 床号
        sqlBuf.append(" SELECT BED_NO FROM ADM_INP AI WHERE CASE_NO = '");
        sqlBuf.append(parm.getValue("CASE_NO") + "'");
        TParm bedNoParm = new TParm(TJDODBTool.getInstance().select(sqlBuf.
            toString()));

        // 组装实际参数
        TParm realParm = new TParm();
        // CASE_NO 就诊号
        realParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        // FILE_SEQ 文件序号
        realParm.setData("FILE_SEQ", emrParm.getValue("FILE_SEQ"));
        // OPT_SEQ 操作序号
        realParm.setData("OPT_SEQ", optSeqParm.getInt("OPT_SEQ", 0));
        // OPT_TYPE 操作类型 L:登陆 C:新建 O:打开 M:修改 D:删除
        realParm.setData("OPT_TYPE", optType);
        // MR_NO 病案号
        realParm.setData("MR_NO", parm.getValue("MR_NO"));
        // IPD_NO 住院号
        realParm.setData("IPD_NO", parm.getValue("IPD_NO"));
        // FILE_NAME 文件名称
        realParm.setData("FILE_NAME", emrParm.getValue("FILE_NAME"));
        // DEPT_CODE 科室
        realParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
        // STATION_CODE 病区
        realParm.setData("STATION_CODE", parm.getValue("STATION_CODE"));
        // BED_NO 床号
        realParm.setData("BED_NO", bedNoParm.getValue("BED_NO", 0));
        // OPT_USER 操作用户
        realParm.setData("OPT_USER", Operator.getID());
        // OPT_DATE 操作日期
        realParm.setData("OPT_DATE",
                         dateFormat.format(SystemTool.getInstance().getDate()));
        // OPT_TERM 操作时间
        realParm.setData("OPT_TERM", Operator.getIP());
        // 病患名称
        realParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
        TParm result = this.writeOptLog(realParm);
        return result;
    }

    /**
     * 日志查询
     * @param parm TParm
     * @return TParm
     */
    public TParm queryOptLog(TParm parm) {
        TParm result = new TParm();
        result = this.query("queryOptLog", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    public TParm delete(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.update("deleteOptLog", parm, conn);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }
}
