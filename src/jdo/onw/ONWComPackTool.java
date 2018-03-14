package jdo.onw;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>��������֮��ͷҽ���ײ�</p>
 * 
 * @author wangqing 20170901
 *
 */
public class ONWComPackTool extends TJDOTool {
    /**
     * ʵ��
     */
    private static ONWComPackTool instanceObject;
    /**
     * �õ�ʵ��
     * @return ONWComPackTool
     */
    public static ONWComPackTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWComPackTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ONWComPackTool() {
        setModuleName("onw\\ONWComPackModule.x");
        onInit();
    }
    
    /**
     * �����ײ�
     * @param parm
     * @param conn
     * @return
     */
	public TParm insertOnwPackMain(TParm parm, TConnection conn){
		TParm result = update("insertOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * ɾ���ײ�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteOnwPackMain(TParm parm, TConnection conn){
		TParm result = update("deleteOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * �޸��ײ�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwPackMain(TParm parm, TConnection conn){
		TParm result = update("updateOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * ��ѯ�ײ�
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectOnwPackMain(TParm parm, TConnection conn){
		TParm result = query("selectOnwPackMain", parm, conn);
        return result;
	}
	
	/**
	 * ����ҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertOnwPackOrder(TParm parm, TConnection conn){
		TParm result = update("insertOnwPackOrder", parm, conn);
        return result;
	}
	
	/**
	 * ɾ��ҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteOnwPackOrder(TParm parm, TConnection conn){
		TParm result = update("deleteOnwPackOrder", parm, conn);
        return result;
	}
	
	/**
	 * �޸�ҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwPackOrder(TParm parm, TConnection conn){
		TParm result = update("updateOnwPackOrder", parm, conn);
        return result;
	}
	
	/**
	 * ��ѯҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectOnwPackOrder(TParm parm, TConnection conn){
		TParm result = query("selectOnwPackOrder", parm, conn);
        return result;
	}

	/**
	 * ONW_ORDER����ҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm insertOnwOrder(TParm parm, TConnection conn){
		TParm result = update("insertOnwOrder", parm, conn);
        return result;
	}
	
	/**
	 * ONW_ORDERɾ��ҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteOnwOrder(TParm parm, TConnection conn){
		TParm result = update("deleteOnwOrder", parm, conn);
        return result;
	}
	
	/**
	 * ONW_ORDER��ѯҽ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm selectOnwOrder(TParm parm, TConnection conn){
		TParm result = query("selectOnwOrder", parm, conn);
        return result;
	}
	
	/**
	 * ONW_ORDER����ҽ�����߻�ʿǩ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder1(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder1", parm, conn);
        return result;
	}
	
	/**
	 * ȡ����ʿǩ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder2(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder2", parm, conn);
        return result;
	}
	
	/**
	 * ҽ��ǩ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder3(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder3", parm, conn);
        return result;
	}
	
	/**
	 * ȡ��ҽ��ǩ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateOnwOrder4(TParm parm, TConnection conn){
		TParm result = update("updateOnwOrder4", parm, conn);
        return result;
	}
	
	/**
	 * ������������
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAmiErdVtsRecord(TParm parm, TConnection conn){
		TParm result = update("updateAmiErdVtsRecord", parm, conn);
        return result;
	}
	
	/**
	 * ��ʿǩ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAmiErdVtsRecord1(TParm parm, TConnection conn){
		TParm result = update("updateAmiErdVtsRecord1", parm, conn);
        return result;
	}
	
	/**
	 * ȡ����ʿǩ��
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAmiErdVtsRecord2(TParm parm, TConnection conn){
		TParm result = update("updateAmiErdVtsRecord2", parm, conn);
        return result;
	}
	
	


}
