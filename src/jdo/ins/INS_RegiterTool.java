package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: ������Ŀ��������
 * </p>
 * 
 * <p>
 * Description:������Ŀ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangs 20140414
 * @version 1.0
 */

public class INS_RegiterTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INS_RegiterTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSADMConfirmTool
	 */
	public static INS_RegiterTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INS_RegiterTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INS_RegiterTool() {
		setModuleName("ins\\INS_RegiterModule.x");
		onInit();
	}
	/**
	 * ɾ���ɵ��豸��Ŀ¼��Ϣ
	 * @param parm
	 * @return
	 */
	public TParm deleteINSNeedRegisterItem(TParm parm,TConnection connection){
		TParm result = update("deleteINSNeedRegisterItem",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * �豸��Ŀ¼��Ϣ�����������
	 * @param parm
	 * @return
	 */
	public TParm insertINSNeedRegisterItemOne(TParm parm,TConnection connection){
		System.out.println("insertINSNeedRegisterItemOne:"+parm);
		TParm result = update("insertINSNeedRegisterItemOne",parm,connection);
		System.out.println("insertINSNeedRegisterItemOne:"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ŀ������Ϣ���ظ�������
	 * @param parm
	 * @return
	 */
	public TParm updateINSRegisterItemOne(TParm parm,TConnection connection){
		TParm result = update("updateINSRegisterItemOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ŀ������Ϣȡ����������
	 * @param parm
	 * @return
	 */
	public TParm onCancelInsRegisterItemOne(TParm parm,TConnection connection){
		TParm result = update("onCancelInsRegisterItemOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ŀ������Ϣɾ����������
	 * @param parm
	 * @return
	 */
	public TParm onDeleteInsRegisterItem(TParm parm,TConnection connection){
		TParm result = update("onDeleteInsRegisterItem",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ŀ������Ϣ��������
	 * @param parm
	 * @return
	 */
	public TParm onInsertInsRegisterItem(TParm parm,TConnection connection){
//		System.out.println("onInsertInsRegisterItem_jdo_start"+parm);
		TParm result = update("onInsertInsRegisterItem",parm,connection);
//		System.out.println("onInsertInsRegisterItem_jdo_end"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ŀ������Ϣ�޸�����
	 * @param parm
	 * @return
	 */
	public TParm onInsRegisterItemUpdate(TParm parm,TConnection connection){
		System.out.println("onInsRegisterItemUpdate_jdo_start"+parm);
		TParm result = update("onInsRegisterItemUpdate",parm,connection);
		System.out.println("onInsRegisterItemUpdate_jdo_end"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
