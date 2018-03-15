package jdo.sum;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:�����������ݹ����� </p>
 *
 * <p>Description: �����������ݹ�����</p>
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
     * ʵ��
     */
    public static SUMVitalSignTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static SUMVitalSignTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SUMVitalSignTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public SUMVitalSignTool() {
        setModuleName("sum\\SUMVitalSignModule.x");
        onInit();
    }

    /**
     * ����OEI��CASE_NO��ѯ�ò��˵ļ�¼--EXAMINE_DATE,USER_ID
     * @param regMethod String
     * @return TParm
     */
    public TParm selectExmDateUser(TParm date) {

        TParm result = query("selectExmUser", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����OEI��CASE_NO,���� ��ѯĳһ�������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateDtl(TParm date) {

        TParm result = query("selectOneDayDtl", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����OEI��CASE_NO,���� ��ѯĳһ�������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectOneDateMst(TParm date) {

        TParm result = query("selectOneDayMst", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����OEI��CASE_NO������ ��ѯ��������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataMst(TParm date) {

        TParm result = query("selectVitalSign", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����OEI��CASE_NO������ ��ѯ�������
     * @param regMethod String
     * @return TParm
     */
    public TParm selectdataDtl(TParm date) {

        TParm result = query("selectVitalSignDtl", date);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����һ��������Ϣ
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
     * ����һ��������Ϣ
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
     * ����һ��ϸ����Ϣ
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
     * ����һ��ϸ����Ϣ
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
    * ����ADM_INP�����غ�����
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
     * ����Ҫ����������Ƿ�Ӧ�����ڣ���������
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
     * ���²�����̬��Ϣ
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
			result.setErr(-1, "����Ϊ��");
			err("ERR:���������������ڿ�ֵ!");
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