package jdo.inf;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ��Ⱦ���Ƹ�Ⱦ����ɸѡ���ݿ⹤����</p>
 *
 * <p>Description: ��Ⱦ���Ƹ�Ⱦ����ɸѡ���ݿ⹤����</p>
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
     * ������
     */
    public INFCaseTool() {
        setModuleName("inf\\INFCaseModule.x");
        onInit();
    }

    /**
     * ʵ��
     */
    private static INFCaseTool instanceObject;

    /**
     * �õ�ʵ��
     * @return INFCaseTool
     */
    public static INFCaseTool getInstance() {
        if (instanceObject == null) instanceObject = new INFCaseTool();
        return instanceObject;
    }

    /**
     * ��Ⱦ�����Ǽǲ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterPatInfo(TParm parm) {
        TParm result = query("caseRegisterPatInfo", parm);
        return result;
    }

    /**
     * ��Ⱦ�����Ǽ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterDiag(TParm parm) {
        TParm result = query("caseRegisterDiag", parm);
        return result;
    }

    /**
     * ��Ⱦ�����ǼǸ�Ⱦ��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm caseRegisterCase(TParm parm) {
        TParm result = query("caseRegisterCase", parm);
        return result;
    }

    /**
     * ȡ�ò�����Ⱦԭ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfReasonByCaseInfNo(TParm parm) {
        TParm result = query("selectInfReasonByCaseInfNo", parm);
        return result;
    }

    /**
     * ȡ�ò���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIntvoprecByCaseInfNo(TParm parm) {
        TParm result = query("selectInfIntvoprecByCaseInfNo", parm);
        return result;
    }

    /**
     * ȡ�ò�����������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfResultByCaseInfNo(TParm parm) {
        TParm result = query("selectInfResultByCaseInfNo", parm);
        return result;
    }
    //liuyalin 20170421 add
    /**
     * ȡ�ò�����Ⱦ��λ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfICDPartByCaseInfNo(TParm parm) {
        TParm result = query("selectInfICDPartByCaseInfNo", parm);
        return result;
    }
    /**
     * ȡ�ò��������Բ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIoByCaseInfNo(TParm parm) {
        TParm result = query("selectInfIoByCaseInfNo", parm);
        return result;
    }
    /**
     * ��ODI_ORDER��ȡ�ò��������Բ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfIoFromOdiByCase(TParm parm) {
        TParm result = query("selectInfIoFromOdiByCase", parm);
        return result;
    }
    //liuyalin 20170421 add end

    /**
     * д��пؼ�¼��Ϣ
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
     * ���¸пؼ�¼��Ϣ
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
     * ɾ���׸�Ⱦ������Ϣ
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
     * д���׸�Ⱦ������Ϣ
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
     * ɾ���п�ʵ������Ϣ
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
     * д���Ⱦʵ������Ϣ
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
     * ɾ����Ⱦ��λ�����Ϣ
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
     * д���Ⱦ��λ�����Ϣ
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
     * ɾ�������Բ�����Ϣ
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
     * д�������Բ�����Ϣ
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
     * ��ѯ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInvOpt(TParm parm) {
        TParm result = query("selectInvOpt", parm);
        return result;
    }
    //liuyalin 20170421 add end
    /**
     * ȡ�øпصȼ����
     * @return String
     */
    public String getInfNo() {
        return SystemTool.getInstance().getNo("ALL", "INF", "INF_NO", "INF_NO");
    }

    /**
     * ȡ�����п����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectMaxInfNo(TParm parm) {
        TParm result = query("selectMaxInfNo", parm);
        return result;
    }

    /**
     * ɾ����������Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteAntibiotrcd(TParm parm, TConnection connection) {
        TParm result = update("deleteAntibiotrcd", parm, connection);
        return result;
    }

    /**
     * д�뿹������Ϣ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertAntibiotrcd(TParm parm, TConnection connection) {
        TParm result = update("insertAntibiotrcd", parm, connection);
        return result;
    }

    /**
     * ��ѯ��������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAntibiotrcd(TParm parm) {
        TParm result = query("selectAntibiotrcd", parm);
        return result;
    }

    /**
     * ��ѯ���һ��סԺ��¼
     * @param parm TParm
     * @return TParm
     */
    public TParm selectLastAdmCase(TParm parm) {
        TParm result = query("selectLastAdmCase", parm);
        return result;
    }

    /**
     * ��ѯ�ϱ��п���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfCaseReport(TParm parm) {
        TParm result = query("selectInfCaseReport", parm);
        return result;
    }

    /**
     * ���¸п��ϱ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCaseReport(TParm parm, TConnection connection) {
        TParm result = update("updateInfCaseReport", parm, connection);
        return result;
    }

    /**
     * ���ипؼ�¼��ȡ��ע��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateInfCaseCancelFlg(TParm parm, TConnection connection) {
        TParm result = update("updateInfCaseCancelFlg", parm, connection);
        return result;
    }

    /**
     * ȡ�ø�Ⱦ�������濨��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfCaseCardInf(TParm parm) {
        TParm result = query("selectInfCaseCardInf", parm);
        return result;
    }

    /**
     * ȡ�ò�����Ⱦ�������濨��Ⱦԭ����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectInfReasonForCard(TParm parm) {
        TParm result = query("selectInfReasonForCard", parm);
        return result;
    }

    /**
     * ���²����п����
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
