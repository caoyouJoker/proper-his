package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INS_Item_RegTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static INS_Item_RegTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INS_Item_RegTool
	 */
	public static INS_Item_RegTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INS_Item_RegTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INS_Item_RegTool() {
		setModuleName("ins\\INS_Item_RegModule.x");
		onInit();
	}
	/**
	 * ������Ϣ�ϴ���������
	 * @param parm
	 * @return
	 */
	public TParm upInsItemReg(TParm parm,TConnection connection){
		TParm result = update("upInsItemReg",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ϣ������������
	 * @param parm
	 * @return
	 */
	public TParm onInsertInsItemReg(TParm parm,TConnection connection){
		TParm result=null;
//		if(parm.getValue("REG_TYPE").equals("1")){
			result = update("insertInsItemReg",parm,connection);
//		}else{
//			result = update("insertInsItemReg_1",parm,connection);
//		}
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ϣ�޸ĸ�������
	 * @param parm
	 * @return
	 */
	public TParm onUpdateInsItemReg(TParm parm,TConnection connection){
		TParm result = update("updateInsItemReg",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ������Ϣ���ظ�������
	 * @param parm
	 * @return
	 */
	public TParm UpdateInsItemRegDown(TParm parm,TConnection connection){
		TParm result = update("downUpInsItemReg",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
