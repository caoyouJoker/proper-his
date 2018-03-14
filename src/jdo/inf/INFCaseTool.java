package jdo.inf;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 感染控制感染病例筛选数据库工具类</p>
 *
 * <p>Description: 感染控制感染病例筛选数据库工具类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFCaseTool extends TJDOTool{

    /**
     * 构造器
     */
    public INFCaseTool() {
        setModuleName("inf\\INFCaseModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static INFCaseTool instanceObject;

    /**
     * 得到实例
     * @return INFCaseTool
     */
    public static INFCaseTool getInstance() {
        if (instanceObject == null) instanceObject = new INFCaseTool();
        return instanceObject;
    }

    /**
     * 感染病历登记病人资料
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterPatInfo(TParm parm) {
        TParm result = query("caseRegisterPatInfo", parm);
        return result;
    }

    /**
     * 感染病历登记诊断信息
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterDiag(TParm parm) {
        TParm result = query("caseRegisterDiag", parm);
        return result;
    }

    /**
     * 感染病历登记感染信息
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterCase(TParm parm) {
        TParm result = query("caseRegisterCase", parm);
        return result;
    }

    /**
     * 取得病患感染原因
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfReasonByCaseInfNo(TParm parm) {
        TParm result = query("selectInfReasonByCaseInfNo", parm);
        return result;
    }

    /**
     * 取得病患介入信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIntvoprecByCaseInfNo(TParm parm) {
        TParm result = query("selectInfIntvoprecByCaseInfNo", parm);
        return result;
    }

    /**
     * 取得病患试验结果信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfResultByCaseInfNo(TParm parm) {
        TParm result = query("selectInfResultByCaseInfNo", parm);
        return result;
    }
    //liuyalin 20170421 add
    /**
     * 取得病患感染部位诊断信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfICDPartByCaseInfNo(TParm parm) {
        TParm result = query("selectInfICDPartByCaseInfNo", parm);
        return result;
    }
    /**
     * 取得病患侵入性操作信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIoByCaseInfNo(TParm parm) {
        TParm result = query("selectInfIoByCaseInfNo", parm);
        return result;
    }
    /**
     * 从ODI_ORDER表取得病患侵入性操作信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIoFromOdiByCase(TParm parm) {
        TParm result = query("selectInfIoFromOdiByCase", parm);
        return result;
    }
    //liuyalin 20170421 add end

    /**
     * 写入感控记录信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfCase(TParm parm, TConnection connection) {
        TParm result = update("insertInfCase", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 更新感控记录信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCase(TParm parm, TConnection connection) {
        TParm result = update("updateInfCase", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 删除易感染因素信息
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInfInfreasrcd(TParm parm, TConnection connection) {
        TParm result = update("deleteInfInfreasrcd", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 写入易感染因素信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfInfreasrcd(TParm parm, TConnection connection) {
        TParm result = update("insertInfInfreasrcd", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 删除感控实验结果信息
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInfantibiotest(TParm parm, TConnection connection) {
        TParm result = update("deleteInfantibiotest", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 写入感染实验结果信息
     * @param parm TParm
     * @return TParm
     */
    public TParm insertInfantibiotest(TParm parm, TConnection connection) {
        TParm result = update("insertInfantibiotest", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }
    //liuyalin 20170421 add
    /**
     * 删除感染部位诊断信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteInfICDPart(TParm parm, TConnection conn) {
        TParm result = update("deleteInfICDPart", parm, conn);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 写入感染部位诊断信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertInfICDPart(TParm parm, TConnection conn) {
        TParm result = update("insertInfICDPart", parm, conn);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 删除侵入性操作信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteInfIO(TParm parm, TConnection conn) {
        TParm result = update("deleteInfIO", parm, conn);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }

    /**
     * 写入侵入性操作信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertInfIO(TParm parm, TConnection conn) {
        TParm result = update("insertInfIO", parm, conn);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }
    /**
     * 查询介入操作信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInvOpt(TParm parm) {
        TParm result = query("selectInvOpt", parm);
        return result;
    }
    //liuyalin 20170421 add end
    /**
     * 取得感控等级编号
     * @return String
     */
    public String getInfNo() {
        return SystemTool.getInstance().getNo("ALL", "INF", "INF_NO", "INF_NO");
    }

    /**
     * 取得最大感控序号
     * @param parm TParm
     * @return TParm
     */
    public TParm selectMaxInfNo(TParm parm) {
        TParm result = query("selectMaxInfNo", parm);
        return result;
    }

    /**
     * 删除抗生素信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteAntibiotrcd(TParm parm, TConnection connection) {
        TParm result = update("deleteAntibiotrcd", parm, connection);
        return result;
    }

    /**
     * 写入抗生素信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertAntibiotrcd(TParm parm, TConnection connection) {
        TParm result = update("insertAntibiotrcd", parm, connection);
        return result;
    }

    /**
     * 查询抗生素信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAntibiotrcd(TParm parm) {
        TParm result = query("selectAntibiotrcd", parm);
        return result;
    }

    /**
     * 查询最后一次住院记录
     * @param parm TParm
     * @return TParm
     */
    public TParm selectLastAdmCase(TParm parm) {
        TParm result = query("selectLastAdmCase", parm);
        return result;
    }

    /**
     * 查询上报感控信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfCaseReport(TParm parm) {
        TParm result = query("selectInfCaseReport", parm);
        return result;
    }

    /**
     * 更新感控上报信息
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCaseReport(TParm parm, TConnection connection) {
        TParm result = update("updateInfCaseReport", parm, connection);
        return result;
    }

    /**
     * 更行感控记录表取消注记
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateInfCaseCancelFlg(TParm parm, TConnection connection) {
        TParm result = update("updateInfCaseCancelFlg", parm, connection);
        return result;
    }

    /**
     * 取得感染病历报告卡信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfCaseCardInf(TParm parm) {
        TParm result = query("selectInfCaseCardInf", parm);
        return result;
    }

    /**
     * 取得病患感染病例报告卡感染原因信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfReasonForCard(TParm parm) {
        TParm result = query("selectInfReasonForCard", parm);
        return result;
    }

    /**
     * 更新病案感控诊断
     * @param parm TParm
     * @return TParm
     */
    public TParm updateMROINFDiag(TParm parm, TConnection connection) {
        TParm result = update("updateMROINFDiag", parm, connection);
        if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
    }
}
