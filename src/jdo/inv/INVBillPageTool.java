package jdo.inv;
/**
*
* <strong>Title : INVBillPageTool<br></strong>
* <strong>Description : </strong>���ʿ���<br>
* <strong>Create on : 2013-11-11<br></strong>
* <p>
* <strong>Copyright (C) <br></strong>
* <p>
* @author duzhw<br>
* @version <strong>ProperSoft</strong><br>
* <br>
* <strong>�޸���ʷ:</strong><br>
* �޸���		�޸�����		�޸�����<br>
* -------------------------------------------<br>
* <br>
* <br>
*/

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

public class INVBillPageTool extends TJDOTool {
    private static INVBillPageTool instanceObject;
    public INVBillPageTool() {
        setModuleName("inv\\INVBillPageToolModule.x");
        onInit();
    }
    public static INVBillPageTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new INVBillPageTool();
        }
        return instanceObject;
    }
    /**
     * ��ѯ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSysFeeMed(TParm parm) {
        TParm result = new TParm();
        result = query("selectSysFeeMed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ̨����ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseALL(TParm parm) {
    	TParm result = new TParm();
        result = query("selectDispenseALL", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��ҳ�������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseIN(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectDispenseIN", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ��ҳ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDispenseOUT(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectDispenseOUT", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ��ҳ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectVerifyin(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectVerifyin", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ��ҳ�˻�����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectRegress(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectRegress", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯָ�����ڵĿ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectStockQty(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectStockQty", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
}
