package jdo.sum;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:生命迹象数据工具类 </p>
 *
 * <p>Description: 生命迹象数据工具类</p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class SUMVitalSignTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SUMVitalSignTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static SUMVitalSignTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SUMVitalSignTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SUMVitalSignTool() {
        setModuleName("sum\\SUMVitalSignModule.x");
        onInit();
    }

    /**
     * 根据OEI，CASE_NO查询该病人的记录--EXAMINE_DATE,USER_ID
     * @param regMethod String
     * @return TParm
     */
    public TParm selectExmDateUser(TParm date) {

        TParm result = query("selectExmUser", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据OEI，CASE_NO,日期 查询某一天的数据
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateDtl(TParm date) {

        TParm result = query("selectOneDayDtl", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据OEI，CASE_NO,日期 查询某一天的数据
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateMst(TParm date) {

        TParm result = query("selectOneDayMst", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 根据OEI，CASE_NO，日期 查询体温主表
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataMst(TParm date) {

        TParm result = query("selectVitalSign", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据OEI，CASE_NO，日期 查询生命标记
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataDtl(TParm date) {

        TParm result = query("selectVitalSignDtl", date);
        // 判断错误值
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增一条主表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertVitalSign(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertVitalSign", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 跟新一条主表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateVitalSign(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateVitalSign", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }


    /**
     * 新增一条细表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertVitalSignDtl(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm oneParm = new TParm();
            oneParm = (TParm) parm.getParm(i + "PARM");
            result = update("insertVitalSignDtl", oneParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;

    }

    /**
     * 跟新一条细表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateVitalSignDtl(TParm parm, TConnection connection) {
        TParm result = new TParm();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            TParm oneParm = new TParm();
            oneParm = (TParm) parm.getParm(i + "PARM");
            result = update("updateVitalSignDtl", oneParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;

    }

    /**
    * 跟新ADM_INP的体重和身高
    * @param parm TParm
    * @param connection TConnection
    * @return TParm
    */
   public TParm updateHeightAndWeight(TParm parm, TConnection connection) {
       TParm result = new TParm();
        result = update("updateHWForAdminp", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

   }

    /**
     * 检查该要保存的数据是否应经存在，或者作废
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm checkIsExist(TParm parm) {
        TParm result = new TParm();
        result = query("checkIsExist", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 更新病患动态信息
     * 
     * @param parm TParm
     * @return result
     */
	public TParm updatePatientTrends(TParm parm) {
		TParm result = new TParm();
		String ptMovecateCode = parm.getValue("PTMOVECATECODE");
		String ptMovecateDesc = parm.getValue("PTMOVECATEDESC");
		String caseNo = parm.getValue("CASE_NO");
		String admType = parm.getValue("ADM_TYPE");
		String examineDate = parm.getValue("EXAMINE_DATE");
		String examineSession = parm.getValue("EXAMINESESSION");

		if (StringUtils.isEmpty(caseNo) || StringUtils.isEmpty(admType)
				|| StringUtils.isEmpty(examineDate)
				|| StringUtils.isEmpty(examineSession)) {
			result.setErr(-1, "参数为空");
			err("ERR:更新条件参数存在空值!");
			return result;
		}

		String sql = "UPDATE SUM_VTSNTPRDTL SET PTMOVECATECODE = '"
				+ ptMovecateCode + "',PTMOVECATEDESC = '" + ptMovecateDesc
				+ "' WHERE CASE_NO = '" + caseNo + "' AND ADM_TYPE = '"
				+ admType + "' AND EXAMINE_DATE = '" + examineDate
				+ "' AND EXAMINESESSION = '" + examineSession + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
